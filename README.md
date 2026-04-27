# Atomik

`Atomik` hoje = backend Java em `atomik-api`.

Estado real atual:

- Implementado: API REST Spring Boot para auth, usuarios, contas, categorias, orcamentos, transacoes, auditoria e sync
- Implementado: JWT, JPA, Liquibase, PostgreSQL, Redis em compose, Swagger, testes unitarios/web/integracao, CI Maven
- Parcial: transacoes recorrentes existem no dominio/persistencia, mas controller ainda nao expoe fluxo completo
- Planejado: frontend React/PWA, sync offline real no cliente, relatorios pesados, filas, dashboard analitico

## Stack real

- Backend: Java 21+, Spring Boot 3.5.x
- Seguranca: Spring Security + JWT
- Persistencia: PostgreSQL + Spring Data JPA
- Migrations: Liquibase
- Cache/token store: Redis
- Testes: JUnit 5, Mockito, Spring Test, H2
- CI: GitHub Actions

## Estrutura

- `atomik-api/`: modulo backend Maven
- `docs/`: ADRs, arquitetura, especificacao funcional alvo
- `REPO_ANALYSIS.md`: diagnostico tecnico do repo
- `ATOMIK_API_PLAN.md`: plano de refatoracao vivo

## Funcionalidade hoje

### Existe

- registro/login com JWT
- CRUD de usuarios
- CRUD de contas
- CRUD de categorias
- CRUD de orcamentos
- criacao/edicao/exclusao/listagem de transacoes
- transferencias entre contas
- auditoria de mudancas em transacoes
- endpoint de sincronizacao
- validacao de ownership via principal autenticado
- profiles `dev`, `test`, `prod`
- `X-Request-Id` em resposta e logs

### Parcial

- refresh token existe no modelo/contratos, mas fluxo operacional completo ainda nao esta fechado como feature de ponta a ponta
- recorrencia existe em partes do core, sem exposicao completa e sem cobertura equivalente aos fluxos principais

### Planejado

- frontend React
- PWA/offline client-side
- jobs/background processing
- relatorios PDF/CSV
- dashboard analitico

## Contrato operacional

### Profiles

- `dev`: SQL visivel, logs da app em `DEBUG`
- `test`: menos ruido, usado pela suite de testes
- `prod`: SQL desligado, logs mais conservadores

Ativar profile:

```bash
cd atomik-api
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

### Variaveis de ambiente

Arquivo exemplo: [`atomik-api/.env.example`](/Users/guilhermefaglioni/Documents/Development/Atomik/atomik-api/.env.example)

Principais variaveis:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`
- `SPRING_PROFILES_ACTIVE`

### Stack local

Subir dependencias:

```bash
docker compose up -d
```

Rodar API:

```bash
cd atomik-api
./mvnw spring-boot:run
```

Swagger:

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI](http://localhost:8080/api-docs)

### Banco e migracoes

- Liquibase roda no bootstrap da aplicacao
- `spring.jpa.hibernate.ddl-auto=validate`
- schema fonte = `src/main/resources/db/changelog`

## Testes e build

Rodar testes:

```bash
cd atomik-api
./mvnw test
```

Build local:

```bash
cd atomik-api
./mvnw -q -DskipTests compile
```

## Documentacao

- [Plano tecnico](/Users/guilhermefaglioni/Documents/Development/Atomik/ATOMIK_API_PLAN.md)
- [Analise do repositorio](/Users/guilhermefaglioni/Documents/Development/Atomik/REPO_ANALYSIS.md)
- [Especificacao funcional alvo](/Users/guilhermefaglioni/Documents/Development/Atomik/docs/project-spec.md)
- [Arquitetura de dados](/Users/guilhermefaglioni/Documents/Development/Atomik/docs/architecture.md)
- [ADR 001](/Users/guilhermefaglioni/Documents/Development/Atomik/docs/adr/001-escolha-da-arquitetura.md)
- [ADR 002](/Users/guilhermefaglioni/Documents/Development/Atomik/docs/adr/002-uso-de-liquidbase.md)
- [ADR 003](/Users/guilhermefaglioni/Documents/Development/Atomik/docs/adr/003-perfis-runtime-observabilidade.md)

## Observacoes

- Suite segue verde, mas H2 ainda loga warning de DDL em `budgets.month/year`
- Docs em `docs/project-spec.md` descrevem produto alvo; nao tratam cada item como entregue
