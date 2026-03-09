# 📝 Especificação Técnica: Atomik (Project Spec)

**Status:** Draft (Versão Inicial)
**Versão:** 1.0.0  
**Autor:** Guilherme Faglioni

---

## 1. Objetivo do Negócio
O **Atomik** é um motor de inteligência financeira focado em integridade bancária e resiliência. O sistema deve garantir rastreabilidade total (Partidas Dobradas) e funcionamento contínuo, independente da conectividade (PWA Offline), servindo como uma prova de conceito de engenharia de software robusta.

---

## 2. Regras de Negócio (RN)
*As leis que governam a Camada de Domínio e os Use Cases.*

* **RN-01 (Atomicidade de Transferência):** Conforme **RF-03**, uma transferência não é um campo editado, mas um par de registros (Crédito/Débito) que devem ser salvos simultaneamente ou sofrer rollback total.
* **RN-02 (Orçamentos):** Um orçamento (**RF-06**) é vinculado a uma categoria. O sistema deve validar e alertar (ou impedir, conforme configuração) gastos que excedam o limite mensal definido.
* **RN-03 (Consistência Offline):** Transações registradas offline (**RF-07**) devem possuir um ID único (UUID) gerado no cliente para evitar duplicidade durante a sincronização com o PostgreSQL (**RNF-01**).
* **RN-04 (Auditoria):** Toda alteração em transações deve gerar um rastro de log (**RF-10**), registrando o valor anterior, o novo valor e o timestamp da mudança.
* **RN-05 (Saldo Derivado):** O saldo de uma conta não é um campo editável. Ele deve ser sempre o resultado da soma de todas as transações (Entradas - Saídas) vinculadas a ela.
* **RN-06 (Cartão de Crédito):** Gastos em cartões de crédito não abatem o saldo da Conta Corrente imediatamente. Eles acumulam em uma "Fatura Aberta" e só impactam o saldo real no registro do pagamento da fatura.
* **RN-07 (Categorização Obrigatória):** Nenhuma transação pode ser salva sem estar vinculada a uma categoria (Ex: Alimentação, Lazer).
* **RN-08 (Data de Competência vs. Pagamento):** O sistema deve permitir registrar despesas com data retroativa ou futura, mas o saldo atualizado só deve considerar transações com data menor ou igual a `NOW()`.
* **RN-09 (Agendamento Recorrente):** Transações recorrentes devem ser salvas com uma data de vencimento e um intervalo (mensal ou semanal).

---

## 3. Requisitos Funcionais (RF)

| ID | Requisito | Descrição |
| :--- | :--- | :--- |
| **RF-01** | Gestão de Contas | Criar, editar e excluir contas (Corrente, Dinheiro, Cartão, Investimento). |
| **RF-02** | Registro de Transações | Registrar receitas, despesas e transferências entre contas. |
| **RF-03** | Lógica de Partidas Dobradas | Toda transferência deve debitar de uma conta e creditar em outra simultaneamente. |
| **RF-04** | Categorização de Gastos | Criar categorias e associar transações a elas. |
| **RF-05** | Agendamento Recorrente | Agendar transações que se repetem mensal ou semanalmente. |
| **RF-06** | Gestão de Orçamentos | Definir limites de gastos por categoria para um período (Mês). |
| **RF-07** | Sincronização Offline | Registro de gastos sem internet e sincronização automática ao detectar conexão. |
| **RF-08** | Geração de Relatórios | Gerar relatórios consolidados em formato PDF ou CSV. |
| **RF-09** | Dashboard Analítico | Exibir gráficos de evolução de patrimônio e distribuição de despesas. |
| **RF-10** | Histórico de Auditoria | Visualizar o log de alterações feitas em uma transação específica. |

---

## 4. Requisitos Não Funcionais (RNF)

| ID | Requisito | Descrição |
| :--- | :--- | :--- |
| **RNF-01** | Persistência Relacional | Uso de PostgreSQL para garantir propriedades ACID. |
| **RNF-02** | Autenticação Segura | Controle via JWT com uso de Refresh Tokens. |
| **RNF-03** | Arquitetura Limpa | Backend seguindo Clean Architecture isolado do Framework Spring. |
| **RNF-04** | Disponibilidade PWA | Interface instalável no mobile, responsiva e com Service Workers. |
| **RNF-05** | Processamento Assíncrono | Relatórios processados em background (Redis) para não travar a API. |
| **RNF-06** | Escalabilidade (Docker) | Aplicação inteiramente conteinerizada para paridade de ambientes. |
| **RNF-07** | Integridade de Dados | Validação de campos via Bean Validation (Back) e Schema Validation (Front). |
| **RNF-08** | Observabilidade | Logs estruturados e Global Exception Handling. |
| **RNF-09** | Performance de Consulta | Resposta de extratos em < 200ms para até 10k registros. |
| **RNF-10** | Internacionalização (I18n) | Suporte a múltiplas moedas e idiomas. |

---

## 5. Casos de Uso Críticos (UC)

### UC-01: Registrar Gasto Offline
1. Usuário abre o Atomik (mesmo sem sinal).
2. Usuário preenche valor (R$ 50), categoria (Almoço) e conta (Carteira).
3. O sistema salva o registro no **IndexedDB** local e marca como `status: PENDING_SYNC`.
4. A interface reflete o novo saldo (Optimistic UI).
5. Quando o sinal retorna, o Service Worker envia o registro para o Java.
6. O Java processa, salva no Postgres e o status muda para `status: SYNCED`.

---

## 6. Definição de "Pronto" (DoD)
1. Código segue os princípios SOLID e Clean Code.
2. Testes unitários cobrem a lógica de Partidas Dobradas (**RF-03**).
3. Swagger atualizado com os novos endpoints de Auditoria (**RF-10**).
4. Build do Docker passando sem erros.