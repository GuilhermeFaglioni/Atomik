# Plano Faltante Backend Atomik API

## Status Atual

- [x] Etapa 1. Refresh token completo
- [x] Etapa 2. Recorrência CRUD + status
- [x] Etapa 3. Executor automático de recorrências
- [ ] Etapa 4. Sync offline robusto
- [ ] Etapa 5. Cartão/fatura
- [ ] Etapa 6. Saldo por competência
- [ ] Etapa 7. Analytics backend
- [ ] Etapa 8. Observabilidade avançada

- Última atualização: 2026-04-27
- Validação mais recente: `./mvnw test` verde com `42` testes, `0` falhas, `0` erros
- Observação: warning antigo de H2 para `budgets.month/year` ainda existe, mas não quebra build

## Escopo

Implementar faltante de backend, sem relatórios:

- refresh token completo
- recorrência completa
- execução automática de recorrências
- sync offline robusto
- analytics backend
- fatura/cartão de crédito
- saldo por competência/data futura
- observabilidade avançada

---

## Ordem Recomendada

1. Refresh token
2. Recorrência CRUD + status
3. Executor automático de recorrências
4. Sync robusto/idempotente
5. Cartão/fatura
6. Saldo por competência
7. Analytics backend
8. Observabilidade avançada

Razão:

- auth e agendamento viram base
- sync e cartão mexem em invariantes
- analytics depende de modelo estável
- observabilidade fecha operação

---

## Etapa 1. Refresh Token Completo

### Objetivo

Fechar autenticação backend além de login inicial.

### Entregar

- endpoint `POST /auth/refresh`
- endpoint `POST /auth/logout`
- rotação de refresh token
- revogação por token
- expiração validada
- testes web + use case + integração

### Passos

1. Criar `RefreshTokenUseCase`
   - validar token
   - buscar `userId`
   - negar token expirado/inexistente
   - gerar novo access token
   - opcional: rotacionar refresh token

2. Criar `LogoutUseCase`
   - apagar token do repositório
   - retornar `204`

3. Revisar `RefreshTokenRepository`
   - garantir persistência de `expiresAt`
   - garantir delete por token

4. Expor endpoints no `AuthController`

5. Cobrir testes
   - refresh válido
   - refresh expirado
   - refresh inexistente
   - logout invalida token

### Regras

- refresh token nunca vira principal de request comum
- preferir rotação a reuso eterno
- logout deve invalidar token persistido

---

## Etapa 2. Recorrência CRUD Completo

### Objetivo

Fechar feature de recorrência como API utilizável.

### Entregar

- criar recorrência
- listar do usuário
- buscar por id
- atualizar status `ACTIVE/PAUSED/COMPLETED`
- deletar recorrência
- testes ponta a ponta backend

### Passos

1. Completar `RecurringTransactionsController`
   - `POST /recurring-transactions`
   - `GET /recurring-transactions/{userId}`
   - `GET /recurring-transactions/{userId}/{id}`
   - `PATCH /recurring-transactions/{userId}/{id}/status`
   - `DELETE /recurring-transactions/{userId}/{id}`

2. Reusar use cases já existentes
   - `CreateRecurringTransactionUseCase`
   - `GetRecurringTransactionById`
   - `GetRecurringTransactionsByUserUseCase`
   - `UpdateRecurringTransactionStatus`
   - criar `DeleteRecurringTransactionUseCase` se faltar

3. Aplicar auth/ownership igual restante
   - principal manda
   - não confiar em `userId` do request

4. Cobrir testes
   - create/list/get/status/delete
   - ownership cross-user
   - validação payload

### Regras

- recorrência não executa saldo no cadastro
- cadastro só agenda
- status controla elegibilidade de execução

---

## Etapa 3. Executor Automático de Recorrências

### Objetivo

Transformar recorrência em transação real no vencimento.

### Entregar

- job periódico
- seleção por `ACTIVE` + `next_due_date <= now`
- criação de transação financeira
- atualização de `next_due_date`
- auditoria
- proteção contra execução duplicada

### Passos

1. Criar scheduler
   - `@Scheduled` ou job explícito
   - classe dedicada tipo `RecurringTransactionExecutorJob`

2. Buscar recorrências vencidas
   - usar `findActiveByUserIdAndNextDueDateBefore` ou equivalente global

3. Para cada recorrência
   - criar `Transaction`
   - aplicar reconciliação/saldo
   - auditar
   - avançar `next_due_date`
   - marcar `COMPLETED` se passou `endDate`

4. Extrair cálculo de próxima data
   - diário/semanal/mensal/anual

5. Garantir idempotência
   - lock lógico
   - marca de última execução
   - ou transação única por recorrência+competência

6. Testar
   - gera 1 transação
   - não duplica em reexecução
   - pausa impede execução
   - endDate encerra recorrência

### Regras

- job deve ser transacional por item
- falha em item não deve corromper saldo
- duplicidade deve ser impossível ou detectada

---

## Etapa 4. Sync Offline Robusto

### Objetivo

Fazer `/sync` backend suportar retry e conflito sem corrupção.

### Entregar

- idempotência
- tratamento de duplicidade
- política de conflito
- respostas por item confiáveis
- testes de retry

### Passos

1. Definir chave idempotente
   - `transaction.id` vindo do cliente
   - se já existe, responder `SYNCED_ALREADY` ou similar

2. Revisar `SyncTransactionsUseCase`
   - create não duplica
   - update valida existência e ownership
   - delete ignora item já removido de forma segura ou responde status conhecido

3. Definir enum/status de sync mais rico
   - `CREATED`
   - `UPDATED`
   - `DELETED`
   - `ALREADY_SYNCED`
   - `CONFLICT`
   - `FAILED`

4. Definir política de conflito
   - cliente manda versão/timestamp
   - backend decide `last-write-wins` ou `reject`
   - documentar

5. Cobrir testes
   - retry do mesmo payload
   - update de item inexistente
   - delete repetido
   - item de outro usuário

### Regras

- sync nunca pode duplicar saldo
- resposta deve ser determinística por item
- conflito não pode virar `500`

---

## Etapa 5. Cartão de Crédito / Fatura

### Objetivo

Implementar regra real de cartão além de enum.

### Entregar

- despesas em cartão vão para fatura, não saldo imediato de conta corrente
- ciclo de fatura
- visualização de fatura aberta
- pagamento de fatura
- fechamento de fatura

### Passos

1. Modelar entidade de fatura
   - `CreditCardStatement`
   - campos: `accountId`, `periodStart`, `periodEnd`, `dueDate`, `status`, `totalAmount`

2. Relacionar transações de cartão à fatura
   - ou por FK
   - ou por período calculado

3. Ajustar reconciliação
   - compra em `CREDIT_CARD` não altera saldo `cash/checking`
   - altera total da fatura

4. Criar pagamento de fatura
   - débito de conta pagadora
   - quitação parcial/total conforme regra escolhida

5. Expor endpoints
   - listar faturas
   - detalhar fatura
   - pagar fatura

6. Cobrir testes
   - compra em cartão
   - soma de fatura
   - pagamento
   - prevenção de dupla quitação

### Regras

- cartão ≠ conta corrente comum
- fatura precisa agregado próprio
- reconciliação deve distinguir saldo disponível vs passivo da fatura

---

## Etapa 6. Saldo por Competência / Data Futura

### Objetivo

Parar de tratar toda transação futura como impacto imediato no saldo atual.

### Entregar

- separação entre saldo atual e saldo projetado
- regra `date <= now` para saldo disponível
- consultas por data de competência

### Passos

1. Decidir fonte de verdade
   - `balance` persistido continua cache operacional
   - ou saldo passa a ser derivado por query
   - ideal médio prazo: saldo derivado ou recalculável

2. Ajustar reconciliação
   - transação futura não altera `currentBalance`
   - altera somente saldo projetado
   - ou grava mas com campo auxiliar claro

3. Criar endpoint/serviço de saldo
   - `currentBalance`
   - `projectedBalance`
   - opcional `balanceAt(date)`

4. Revisar update/delete
   - mover transação entre passado/futuro recalcula impacto

5. Cobrir testes
   - receita futura
   - despesa futura
   - edição de data futura para passada
   - remoção de transação futura

### Regras

- saldo atual deve refletir só competência vencida
- projeção deve ser explícita, não implícita

---

## Etapa 7. Analytics Backend

### Objetivo

Fornecer endpoints agregados para frontend/dashboard futuro.

### Entregar

- despesas por categoria
- receitas/despesas por período
- evolução de saldo/patrimônio
- totais mensais

### Passos

1. Criar camada query dedicada
   - não usar controllers CRUD para analytics
   - preferir `query service/read model`

2. Criar endpoints
   - `/analytics/cashflow`
   - `/analytics/categories`
   - `/analytics/net-worth`
   - `/analytics/monthly-summary`

3. Definir filtros
   - `from`
   - `to`
   - `accountId`
   - `categoryId`

4. Garantir semântica financeira correta
   - considerar data de competência
   - considerar exclusão/edição
   - considerar cartão/fatura conforme regra final

5. Cobrir testes
   - agregação correta
   - ownership
   - período vazio retorna zero/[] sem erro

### Regras

- endpoint analítico não deve recalcular regra de negócio errada
- query deve responder pergunta exata
- sem exceção para coleção vazia

---

## Etapa 8. Observabilidade Avançada

### Objetivo

Tornar backend operável em ambiente real.

### Entregar

- health checks
- métricas
- logs estruturados mínimos
- readiness/liveness
- base para tracing

### Passos

1. Adicionar Actuator
   - `health`
   - `info`
   - `metrics`

2. Expor endpoints seguros
   - público mínimo
   - restante protegido

3. Melhorar logs
   - manter `requestId`
   - incluir `userId` quando seguro
   - incluir evento de auth/sync/job failure

4. Instrumentar pontos críticos
   - login
   - sync
   - execução de recorrência
   - falha de reconciliação

5. Opcional
   - Micrometer + Prometheus

6. Cobrir testes básicos
   - actuator sobe
   - health disponível
   - config não abre endpoint indevido

### Regras

- observabilidade não vaza segredo
- logs devem ajudar incidente, não inundar
- endpoint operacional deve ser explícito por ambiente

---

## Dependências Entre Etapas

- **Refresh token** antes de endurecer auth geral
- **Recorrência CRUD** antes de **executor automático**
- **Cartão/fatura** antes de **analytics final**
- **Saldo por competência** antes de analytics confiável
- **Observabilidade** pode andar em paralelo, mas melhor após fluxos críticos

---

## Critério de Aceite por Feature

Feature só conta pronta se tiver:

- endpoint/controller
- use case
- persistência
- ownership/auth
- tratamento de erro
- teste unitário
- teste web ou integração
- documentação curta no README/spec/session context

---

## Roadmap Curto

### Sprint A

- refresh token completo
- recorrência CRUD

### Sprint B

- executor de recorrência
- sync robusto

### Sprint C

- cartão/fatura
- saldo por competência

### Sprint D

- analytics backend
- observabilidade avançada

---

## Modo de Uso

Usar este arquivo como contexto persistente para trabalho pós-plano base.

Fluxo sugerido:

1. Ler ordem recomendada
2. Executar uma etapa por vez
3. Validar critério de aceite
4. Atualizar session context e status local
