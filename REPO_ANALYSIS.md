# ESTADO ATUAL

## Tech stack detectada

- Monorepo parcial. Implementacao real presente apenas em `atomik-api/`.
- Backend: Java 21 alvo de build em `atomik-api/pom.xml:14`, Spring Boot 3.5.11 em `atomik-api/pom.xml:7`, Spring Web/Validation/Data JPA/Data Redis/Security em `atomik-api/pom.xml:20-36`.
- Persistencia: PostgreSQL + Spring Data JPA + Liquibase em `atomik-api/pom.xml:38-46` e `atomik-api/src/main/resources/application.yml:5-27`.
- Auth: JWT Auth0 + refresh token em Redis em `atomik-api/pom.xml:48-56`, `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/security/JwtTokenService.java:19-65`, `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/adapter/RedisRefreshTokenAdapter.java`.
- Infra local: `docker-compose.yml` sobe apenas PostgreSQL e Redis.
- Docs prometem frontend React/PWA, filas e CI/CD em `README.md:5`, `README.md:28-39`. Nada disso existe no repo atual.

## Maturidade do codigo

- Arquitetura separada em `domain/application/infrastructure/presentation`, mas contratos e mapeamentos quebram invariantes basicas.
- Cobertura de testes baixa: apenas 3 classes de teste localizadas em `atomik-api/src/test/...`.
- `./mvnw test` falha integralmente no ambiente atual. Causa direta: Mockito inline mock maker nao inicializa em Java 25 (`target/surefire-reports/com.atomik.atomik_api.application.usecases.AuthenticateUserUseCaseTest.txt`).
- Maturidade geral: prototipo funcional incompleto, com documentacao acima do estado real de implementacao.

# BUGS/CRITICAL

- Segredo JWT e credenciais de banco hardcoded em texto puro:
  - `atomik-api/src/main/resources/application.yml:6-9`
  - `atomik-api/src/main/resources/application.yml:36-39`
  Risco: vazamento de segredo, ambiente nao portavel, configuracao insegura.

- Fluxo de autenticacao inconsistente. JWT grava `id` como claim, mas filtro ignora claim e usa email como principal:
  - claim `id`: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/security/JwtTokenService.java:28-32`
  - principal = email: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/security/filter/JwtAuthenticationFilter.java:30-33`
  - `/sync` compara `authentication.getName()` com `request.userId()`: `atomik-api/src/main/java/com/atomik/atomik_api/presentation/controllers/SyncStatusController.java:25-29`
  Efeito: token autentica com email, endpoint exige UUID. Sincronizacao autenticada tende a retornar `403` sempre.

- IDOR horizontal em quase toda API autenticada. Seguranca exige apenas "qualquer usuario autenticado" (`SecurityConfig.java:31-37`), enquanto controllers aceitam `userId` via path/body sem vincular ao principal autenticado:
  - `AccountController.java:44-72`
  - `CategoryController.java:43-73`
  - `BudgetController.java:44-74`
  - `TransactionController.java:49-91`
  - `UserController.java:25-41`
  Efeito: quem possuir token valido pode operar recursos de outro usuario se souber UUID.

- `SyncTransactionsUseCase` chama delete com parametros invertidos:
  - assinatura correta em `DeleteTransactionUseCase.execute(String userId, String transactionId)`
  - chamada atual: `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/SyncTransactionsUseCase.java:80-82`
  Efeito: busca usuario pelo UUID da transacao e transacao pelo UUID do usuario. Delete via sync quebra logicamente.

- `CreateUniqueTransactionUseCase` valida conta usando `userId` no lugar de `accountId`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/CreateUniqueTransactionUseCase.java:47-49`
  Efeito: conta valida pode ser rejeitada; UUID de usuario pode mascarar erro.

- `CreateUniqueTransactionUseCase` nao reconcilia saldo de conta apos criar receita/despesa:
  - `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/CreateUniqueTransactionUseCase.java:53-60`
  Efeito: lancamento persiste, saldo nao acompanha.

- Persistencia de saldo quebrada. `AccountRepositoryAdapter.update` nunca salva `balance`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/adapter/AccountRepositoryAdapter.java:49-55`
  - `CreateTransferUseCase` depende disso para transferencias: `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/CreateTransferUseCase.java:56-60`
  - `TransactionReconciliationImpService` depende disso para rollback/apply.
  Efeito: double-entry e reconciliacao nao persistem saldo real.

- `AccountMapper.toEntity` nao copia `balance`, apesar entidade exigir coluna nao nula:
  - mapper: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/persistence/AccountMapper.java:23-33`
  - entidade: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/persistence/AccountEntity.java:48-49`
  Efeito: criacao/merge de conta pode gravar `null` ou depender de comportamento incidental do ORM/DB.

- `TransactionMapper.toEntity` nao popula `user`, `category`, `sourceAccount`, `destinationAccount`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/persistence/TransactionMapper.java:26-37`
  - colunas/relacoes exigidas em `TransactionEntity.java:29-47`
  Efeito: criacao de transacao pode persistir FK nula; update jamais altera contas/categoria.

- `UpdateTransactionUseCase` sempre faz `UUID.fromString(destinationAccountId)`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/UpdateTransactionUseCase.java:52-56`
  Efeito: update de `REVENUE` ou `EXPENSE` com destino nulo quebra antes da validacao.

- `UpdateTransactionUseCase` gera `updatedTransaction` com novos FKs, mas `TransactionRepositoryAdapter.update` salva apenas campos escalares:
  - use case: `UpdateTransactionUseCase.java:52-64`
  - adapter: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/adapter/DatabaseTransactionRepositoryAdapter.java:25-37`
  Efeito: reconciliacao usa contas novas, banco mantem contas antigas. Estado financeiro diverge do estado persistido.

- `CreateTransferUseCase` valida existencia de usuario, mas nao valida ownership das contas fonte/destino:
  - `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/CreateTransferUseCase.java:40-47`
  Efeito: usuario pode movimentar conta de outro usuario se souber UUIDs.

- `CategoryMapper` descarta `isDefault` e nao seta relacao `user`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/persistence/CategoryMapper.java:11-28`
  Efeito: dado de negocio perdido; create/update podem falhar por FK nula ou retornar sempre `false` para categoria default.

- `DatabaseRecurringTransactionRepositoryAdapter` nao possui `@Component`:
  - `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/adapter/DatabaseRecurringTransactionRepositoryAdapter.java:14`
  Efeito: `RecurringTransactionRepository` pode ficar sem bean e impedir bootstrap quando contexto completo subir.

- Handler global cobre apenas 5 excecoes:
  - `atomik-api/src/main/java/com/atomik/atomik_api/presentation/advice/GlobalExceptionHandler.java:16-54`
  Excecoes restantes (`BudgetNotFoundException`, `CategoryNotFoundException`, `TransactionNotFoundException`, `AuditLogNotFoundException`, `RecurringTransactionNotFoundException`) tendem a vazar como 500.

- Nome de conta tem restricao unica global na entidade, mas regra de dominio e repository usam unicidade por usuario:
  - entidade: `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/persistence/AccountEntity.java:34-35`
  - regra de uso: `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/CreateAccountUseCase.java:23-31`
  Efeito: usuarios diferentes nao podem ter contas com mesmo nome.

- Testes nao sao executaveis no ambiente atual por incompatibilidade de mock:
  - `AuthenticateUserUseCaseTest.txt` mostra Java 25 + Mockito inline falhando.
  Efeito: pipeline local perde confianca basica.

# CLEAN CODE & SOLID

- SRP violado em use cases transacionais. `CreateTransferUseCase`, `UpdateTransactionUseCase` e `DeleteTransactionUseCase` validam ownership, reconciliam saldo, persistem transacao e ainda escrevem audit log. Muito acoplamento a persistencia e efeitos colaterais.

- DIP parcialmente quebrado por mapeadores/adapters que nao respeitam contrato do dominio:
  - `TransactionMapper`, `AccountMapper`, `CategoryMapper`.
  Interface abstrai repositorio, mas implementacao nao preserva estado completo do agregado.

- Inconsistencia de nomenclatura e typo em contratos publicos:
  - `DeleteAccounteResponse`
  - parametro `prefferedCurrency` em `UpdateUserUseCase.execute`
  - `TransactionReconciliationImpService` em vez de `Impl`
  Impacto: ruído, baixa legibilidade, chance maior de erro.

- DTOs misturam parsing/validacao/transporte:
  - `CreateAccountRequestDTO.getType()`
  - `CreateUniqueTransactionRequestDTO.getType()`
  - `UpdateTransactionRequestDTO.getType()/getDate()`
  DTO vira mini camada de dominio/aplicacao.

- Validacao de Bean Validation esta inconsistente:
  - anotacoes erradas em tipos nao-String: `CreateTransferRequestDTO.java:13-15`, `CreateUniqueTransactionRequestDTO.java:15-16`
  - `@NotNull` em `boolean` primitivo nao faz efeito: `CreateCategoryRequestDTO.java:11`
  - maioria dos controllers nao usa `@Valid` no `@RequestBody`: `AccountController`, `CategoryController`, `BudgetController`, `TransactionController`, `SyncStatusController`, `UserController`
  Impacto: contrato HTTP aceita payload invalido e desloca erro para runtime interno.

- Documento de arquitetura e README divergem do codigo real:
  - README promete frontend React/PWA, filas e CI/CD em `README.md:5`, `README.md:28-39`
  - repo contem apenas backend Spring + docs.
  Impacto: baixa confiabilidade documental.

- Classe fantasma inutil:
  - `atomik-api/src/main/java/com/atomik/atomik_api/infrastructure/adapter/AuditLog.java`
  Impacto: sombra de nome com `domain.model.AuditLog`, risco de import errado.

# PERFORMANCE

- `spring.jpa.show-sql=true` e `hibernate.format_sql/highlight_sql=true` em `application.yml:14-18`.
  Impacto: overhead de log e vazamento de dados em runtime.

- `ListUserTransactionUseCase` carrega lista inteira para depois checar autorizacao usando apenas primeiro item:
  - `atomik-api/src/main/java/com/atomik/atomik_api/application/usecases/ListUserTransactionUseCase.java:24-31`
  Impacto: custo de IO sem valor; autorizacao deveria vir da query ou do principal.

- `List*` use cases tratam lista vazia como excecao (`Budgets`, `Categories`, `RecurringTransactions`, `AuditLogs`).
  Impacto: gera fluxo de excecao para caso comum e aumenta custo de erro/serializacao.

- `JwtTokenService.validateToken` faz verificacao completa e depois `extractSubject` repete parse/verificacao:
  - `JwtAuthenticationFilter.java:29-33`
  - `JwtTokenService.java:45-60`
  Impacto: dupla verificacao por request.

- Processo de sync executa item por item sem batch, sem idempotencia forte e sem transacao agregada:
  - `SyncTransactionsUseCase.java:46-60`
  Impacto: alto round-trip de repositorio e consistencia parcial em lotes grandes.

# ROADMAP DE MELHORIA

1. Corrigir modelo de autenticacao/autorizacao.
   - Extrair `id` do JWT para principal.
   - Eliminar `userId` confiado do body/path quando derivavel do token.
   - Fechar IDOR em todos controllers.

2. Consertar camada de persistencia.
   - `AccountMapper`, `TransactionMapper`, `CategoryMapper`, `AccountRepositoryAdapter.update`, `DatabaseTransactionRepositoryAdapter.update`.
   - Adicionar testes de integracao JPA cobrindo save/update real.

3. Corrigir invariantes financeiras.
   - `CreateUniqueTransactionUseCase` deve validar `accountId` correto e reconciliar saldo.
   - `CreateTransferUseCase` deve validar ownership das contas.
   - `UpdateTransactionUseCase` deve suportar nao-transfer e persistir FKs alteradas.
   - `SyncTransactionsUseCase` deve corrigir ordem do delete.

4. Corrigir bootstrap e contratos HTTP.
   - Anotar `DatabaseRecurringTransactionRepositoryAdapter` como bean.
   - Completar `GlobalExceptionHandler`.
   - Aplicar `@Valid` em todos `RequestBody`.
   - Trocar `@NotBlank` por `@NotNull`/constraints corretas.

5. Sanear configuracao.
   - Externalizar secrets e credenciais via env vars.
   - Desligar SQL verbose fora de dev.
   - Alinhar expiracao de access token e refresh token.

6. Reerguer qualidade de teste.
   - Tornar build compativel com JDK real do time ou fixar toolchain.
   - Ajustar Mockito para Java 25 ou fixar Java 21 no CI/dev.
   - Cobrir fluxos criticos: auth, ownership, saldo, sync, mappings JPA.

7. Alinhar documentacao com realidade.
   - README deve refletir backend-only atual ou incluir frontend/CI prometidos.
   - ADR/spec devem marcar itens implementados vs planejados.
