# Plano Atomik API

## Objetivo Geral

Fechar riscos criticos primeiro. Depois corrigir invariantes financeiras, persistencia, contratos HTTP, testes, design, performance e documentacao. Regra central: identidade vem de `SecurityContext`, nao de `path`, `query` ou `body`.

---

## Ordem Recomendada

1. Seguranca e identidade
2. Invariantes financeiras
3. Persistencia e mapeamento
4. Validacao HTTP e exception handling
5. Testes e build
6. Clean Code/SOLID
7. Performance e observabilidade
8. Documentacao

---

## Principios-Guia

- Verdade unica de identidade: token manda, request nao manda.
- Persistencia sem perda: mapper/adapter preservam estado inteiro.
- Invariante primeiro: saldo e ownership nunca opcionais.
- Falha previsivel deve ser tipada e tratada.
- Teste deve cobrir risco de negocio, nao so caminho feliz.
- Documentacao deve refletir codigo real.
- Refatorar por fatia vertical. Nao mexer tudo ao mesmo tempo.

---

## Etapa 1. Estancar Riscos Criticos

### Objetivo

Fechar falhas de seguranca e incoerencias de autenticacao/autorizacao. Garantir que sistema pare de aceitar operacao cruzada entre usuarios.

### Principais Passos

#### 1. Unificar identidade autenticada

- JWT deve carregar `userId` como principal efetivo.
- `JwtAuthenticationFilter` deve montar `Authentication` com UUID usuario, nao email.
- Endpoints devem usar principal autenticado, nao confiar em `userId` vindo de `path/body` quando evitavel.

#### 2. Eliminar IDOR horizontal

- Remover `userId` de payloads internos onde token ja resolve contexto.
- Em controllers restantes, comparar principal autenticado com recurso acessado.
- Mover validacao de ownership para camada de aplicacao ou policy dedicada.

#### 3. Externalizar segredos

- Tirar credenciais e JWT secret de `application.yml`.
- Usar env vars e profiles.
- Separar config `dev`, `test`, `prod`.

### Instrucoes

- Regra: identidade vem sempre de `SecurityContext`.
- Regra: controller nunca autoriza por parametro recebido.
- Regra: segredo nunca em repo.

### Saida Esperada

Nenhum endpoint autenticado aceita operar recurso de outro usuario.

### Checklist de Entrega

- [ ] JWT usa `userId` como subject
- [ ] Filtro de auth monta principal correto
- [ ] Controllers param de confiar em `userId` do request
- [ ] Ownership centralizado
- [ ] Secrets fora do repo
- [ ] Profiles separados
- [ ] Testes auth/ownership verdes

---

## Etapa 2. Corrigir Invariantes Financeiras

### Objetivo

Fazer saldo, transacao, transferencia e reconciliacao refletirem mesmo estado de negocio e mesmo estado persistido.

### Principais Passos

#### 1. Corrigir criacao de transacao unica

- Validar `accountId`, nao `userId`.
- Aplicar reconciliacao de saldo para `REVENUE` e `EXPENSE`.
- Validar ownership de conta e categoria.

#### 2. Corrigir transferencia

- Validar que contas pertencem ao usuario.
- Impedir uso de contas externas.
- Garantir persistencia correta do saldo alterado.

#### 3. Corrigir update/delete/sync

- `SyncTransactionsUseCase` deve chamar delete com ordem correta.
- `UpdateTransactionUseCase` deve suportar `destinationAccountId = null` para nao-transfer.
- Reconciliacao e persistencia devem usar mesmos FKs.

### Instrucoes

- Regra: toda mutacao financeira deve ser transacional.
- Regra: agregado financeiro muda saldo e persiste saldo no mesmo fluxo.
- Regra: `create/update/delete` devem ter testes de saldo antes/depois.

### Saida Esperada

`double-entry` consistente, sem divergencia entre dominio e banco.

### Checklist de Entrega

- [ ] `CreateUniqueTransactionUseCase` valida conta correta
- [ ] Criacao `REVENUE/EXPENSE` altera saldo
- [ ] Transfer valida ownership de contas
- [ ] Update suporta nao-transfer sem `destinationAccountId`
- [ ] Sync usa ordem correta
- [ ] Testes de integridade financeira verdes

---

## Etapa 3. Consertar Camada de Persistencia

### Objetivo

Fazer adapters e mappers preservarem estado completo dos agregados. Eliminar perda silenciosa de dados.

### Principais Passos

#### 1. Corrigir mappers

- `AccountMapper` deve mapear `balance`.
- `TransactionMapper` deve mapear `user`, `category`, `sourceAccount`, `destinationAccount`.
- `CategoryMapper` deve mapear `isDefault` e relacao `user`.

#### 2. Corrigir adapters

- `AccountRepositoryAdapter.update` deve persistir saldo.
- `DatabaseTransactionRepositoryAdapter.update` deve persistir FKs alteradas.
- Revisar `save/update/delete` para nao depender de estado parcial.

#### 3. Revisar entidades e constraints

- Unicidade de conta deve ser por usuario, nao global.
- Confirmar `nullable/unique` alinhado com regra de dominio.
- Revisar migrations para refletir verdade de negocio.

### Instrucoes

- Regra: mapper nunca pode descartar campo de dominio relevante.
- Regra: adapter deve ser perda-zero entre dominio e JPA.
- Regra: contrato do repositorio deve ser coberto por teste de integracao.

### Saida Esperada

Salvar, atualizar, ler e deletar preserva estado correto.

### Checklist de Entrega

- [ ] `AccountMapper` preserva `balance`
- [ ] `TransactionMapper` preserva relacoes
- [ ] `CategoryMapper` preserva `user/isDefault`
- [ ] `update()` persiste saldo e FKs
- [ ] Constraints revisadas
- [ ] Testes de integracao de repositorio verdes

---

## Etapa 4. Normalizar Contratos HTTP e Validacao

### Objetivo

Fazer API falhar cedo, com erro correto, sem empurrar payload invalido para dentro da regra de negocio.

### Principais Passos

#### 1. Corrigir DTOs

- Trocar `@NotBlank` em `BigDecimal` e `LocalDateTime` por `@NotNull`.
- Remover `@NotNull` inutil em `boolean` primitivo ou trocar para `Boolean`.
- Padronizar nomes, mensagens e tipos.

#### 2. Aplicar `@Valid`

- Todos controllers com `@RequestBody` devem validar entrada.
- `Path/query validation` tambem quando aplicavel.

#### 3. Completar handler global

- Mapear todas excecoes de dominio para status corretos.
- Padronizar payload de erro.
- Evitar `500` para falha prevista de negocio.

### Instrucoes

- Regra: erro de entrada = `400`.
- Regra: erro de ownership = `403` ou `401`, conforme fluxo.
- Regra: erro de entidade ausente = `404`.

### Saida Esperada

Contratos previsiveis, sem excecao de negocio vazando como erro interno.

### Checklist de Entrega

- [ ] DTOs usam anotacoes corretas
- [ ] Controllers usam `@Valid`
- [ ] Handler cobre excecoes de dominio
- [ ] Payload de erro padronizado
- [ ] Nenhum erro previsto vira `500`

---

## Etapa 5. Reforcar Design e Clean Code/SOLID

### Objetivo

Reduzir acoplamento, clarificar responsabilidade, facilitar manutencao e teste.

### Principais Passos

#### 1. Separar responsabilidades

- Use cases financeiros nao devem acumular validacao, ownership, reconciliacao, auditoria e persistencia em bloco unico.
- Extrair servicos/policies para ownership, auditoria e aplicacao de saldo.

#### 2. Padronizar nomenclatura

- Corrigir typos como `DeleteAccounteResponse`, `prefferedCurrency`, `ImpService`.
- Remover classe fantasma `infrastructure.adapter.AuditLog`.

#### 3. Revisar DTOs com comportamento

- Parsing de enum/data sair de DTO quando gerar acoplamento indevido.
- Preferir mapeador de entrada ou service de conversao.

### Instrucoes

- Regra: classe deve ter motivo unico de mudanca.
- Regra: nome deve refletir responsabilidade.
- Regra: camada web transporta; camada app orquestra; dominio valida regra.

### Saida Esperada

Codigo menos fragil, menos repeticao, menos surpresa.

### Checklist de Entrega

- [ ] Ownership extraido
- [ ] Auditoria extraida
- [ ] Aplicacao de saldo isolada
- [ ] Typos corrigidos
- [ ] DTOs viram transporte simples

---

## Etapa 6. Tornar Build e Testes Confiaveis

### Objetivo

Recuperar confianca do projeto. Build verde. Testes cobrindo risco real.

### Principais Passos

#### 1. Fixar ambiente

- Padronizar Java suportado. Ideal: Java 21 toolchain.
- Ajustar Mockito para ambiente real do time ou remover dependencia de inline mock maker.

#### 2. Criar piramide minima

- Testes unitarios: auth, ownership, validacoes, reconciliacao.
- Testes de integracao: repositories/mappers/JPA/Liquibase.
- Testes web: controllers com contratos HTTP.

#### 3. Cobrir fluxos criticos primeiro

- `login/register`
- `create/update/delete transaction`
- `transfer`
- `sync`
- `saldo de conta`
- `ownership cross-user`

### Instrucoes

- Regra: bug critico corrigido sem teste = bug candidato a voltar.
- Regra: adapter e mapper precisam teste de integracao, nao so mock.
- Regra: CI deve executar mesma versao Java do time.

### Saida Esperada

`mvn test` verde e confiavel.

### Checklist de Entrega

- [ ] Java suportado documentado e padronizado
- [ ] Mockito estabilizado
- [ ] Piramide minima criada
- [ ] Fluxos criticos cobertos
- [ ] CI alinhado com runtime real

---

## Etapa 7. Ajustar Performance e Observabilidade

### Objetivo

Reduzir custo inutil de IO/log/excecao. Melhorar diagnostico sem poluir runtime.

### Principais Passos

#### 1. Reduzir overhead de log

- Desligar `show-sql` fora de `dev`.
- Ajustar logging por profile.

#### 2. Revisar queries e fluxos de lista

- Nao usar excecao para lista vazia.
- Evitar carregar colecao inteira para validar ownership.
- Empurrar filtro/autorizacao para query ou service dedicado.

#### 3. Melhorar observabilidade

- Log estruturado em erros criticos.
- `correlation/request id`.
- Metricas para auth, sync, falha de reconciliacao.

### Instrucoes

- Regra: lista vazia nao e excecao de negocio por padrao.
- Regra: query deve responder pergunta exata.
- Regra: observabilidade ajuda debug, nao substitui regra correta.

### Saida Esperada

API mais leve, mais legivel em producao.

### Checklist de Entrega

- [ ] SQL log so em dev
- [ ] Listas vazias nao explodem
- [ ] Queries mais especificas
- [ ] Correlation id presente
- [ ] Metricas minimas implantadas

---

## Etapa 8. Alinhar Documentacao com Realidade

### Objetivo

Fazer docs refletirem estado real. Evitar promessa falsa de frontend, filas, CI e PWA inexistentes.

### Principais Passos

#### 1. Atualizar README

- Marcar backend real implementado.
- Separar `existente`, `parcial`, `planejado`.

#### 2. Revisar ADR/spec/changelog

- Registrar decisoes de seguranca, persistencia e refatoracao.
- Atualizar requisitos entregues vs pendentes.

#### 3. Documentar contrato operacional

- Profiles
- env vars obrigatorias
- stack local
- como rodar teste e migracao

### Instrucoes

- Regra: doc mentirosa custa mais que doc curta.
- Regra: backlog planejado deve ficar marcado como planejado.

### Saida Esperada

Onboarding rapido, expectativa correta.

### Checklist de Entrega

- [ ] README sincero
- [ ] Planejado separado de implementado
- [ ] ADR/spec atualizados
- [ ] Setup operacional documentado

---

## Entrega Ideal por Sprint

### Sprint 1

- auth principal por `userId`
- fechamento de IDOR
- secrets via env
- testes auth/ownership

### Sprint 2

- correcoes de saldo, transfer, sync, update/delete transaction
- testes financeiros de integridade

### Sprint 3

- mappers/adapters/JPA
- constraints e migrations alinhadas
- testes integracao repositorio

### Sprint 4

- validacao DTO/controller
- exception handler completo
- padronizacao de respostas

### Sprint 5

- clean code, renomeacoes, remocao de classes fantasmas
- performance basica
- docs finais

---

## Modo de Uso Deste Arquivo

Usar este arquivo como fonte unica de consulta rapida apos limpar contexto.

Fluxo sugerido:

1. Ler `Ordem Recomendada`
2. Ler etapa atual
3. Executar checklist da etapa
4. Atualizar caixas `[ ]` para `[x]`
5. Registrar decisoes grandes em documento complementar se necessario

---

## Status Atual

- [x] Etapa 1 concluida
- [x] Etapa 2 concluida
- [x] Etapa 3 concluida
- [x] Etapa 4 concluida
- [ ] Etapa 5 concluida
- [ ] Etapa 6 concluida
- [ ] Etapa 7 concluida
- [ ] Etapa 8 concluida
