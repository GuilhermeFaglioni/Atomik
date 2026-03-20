# ⚛️ Atomik | Financial Intelligence Engine

**Atomik** é uma plataforma de gestão financeira de alta fidelidade, projetada para oferecer o rigor técnico de sistemas bancários com a agilidade de uma experiência nativa moderna. 

O projeto utiliza **Java 21** com **Clean Architecture** no backend para garantir integridade absoluta dos dados (ACID), e **React** no frontend com capacidades de **PWA (Progressive Web App)** para resiliência total em cenários offline.

---

## 🚀 Diferenciais Técnicos 

* **Atomicidade & Integridade (RF-03):** Implementação do método de **Partidas Dobradas (Double-Entry Bookkeeping)**, garantindo que o dinheiro nunca "suma" entre contas através de transações atômicas.
* **Resiliência Offline (RF-07):** Sincronização inteligente via **Service Workers** e **IndexedDB**, permitindo o uso completo do sistema sem conexão com a internet.
* **Arquitetura Evolutiva (RNF-03):** Separação rigorosa entre regras de negócio e infraestrutura (Clean Architecture), facilitando a testabilidade e manutenção a longo prazo.
* **Processamento Assíncrono (RNF-05):** Geração de relatórios pesados via filas de mensageria (**Redis**), mantendo a API sempre responsiva e performática.

---

## 🛠️ Stack Tecnológica

### Backend (The Core)
* **Linguagem:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Segurança:** Spring Security + JWT (com Refresh Tokens)
* **Persistência:** PostgreSQL + Spring Data JPA
* **Migrations:** Liquibase (Versionamento de banco de dados)
* **Documentação:** Swagger / OpenAPI 3

### Frontend (The Shell)
* **Framework:** React + Vite
* **Linguagem:** TypeScript
* **Estado & Cache:** TanStack Query (React Query)
* **PWA:** Workbox / Service Workers
* **UI/UX:** Tailwind CSS + Radix UI + Shadcn/UI
* **Charts:** Recharts

### Infra & DevOps
* **Containerização:** Docker & Docker Compose
* **Mensageria/Cache:** Redis
* **CI/CD:** GitHub Actions (Automated Testing & Linting)

---

## 🏛️ Arquitetura do Sistema

O **Atomik** segue os princípios da **Clean Architecture**, organizado em quatro camadas principais para garantir o desacoplamento:

1.  **Domain:** Entidades puras e regras de negócio essenciais (Independente de frameworks).
2.  **Application:** Casos de Uso (Use Cases) que orquestram o fluxo de dados da aplicação.
3.  **Infrastructure:** Implementações técnicas (Persistência JPA, Clientes de API, Configurações do Spring).
4.  **Presentation/Web:** Controllers REST e DTOs para comunicação segura com o Frontend.

---

## 📋 Requisitos do Projeto

### Funcionais (RF)
- [ ] **RF-01:** Gestão de múltiplas contas (Corrente, Dinheiro, Cartão).
- [ ] **RF-02:** Registro de receitas, despesas e transferências.
- [ ] **RF-03:** Lógica de partidas dobradas para transferências atômicas.
- [ ] **RF-05:** Agendamento de transações recorrentes.
- [ ] **RF-07:** Registro e sincronização offline via PWA.
- [ ] **RF-08:** Geração assíncrona de relatórios financeiros (PDF/CSV).

### Não Funcionais (RNF)
- [ ] **RNF-01:** Persistência relacional com PostgreSQL (ACID).
- [ ] **RNF-02:** Autenticação segura via JWT com Refresh Tokens.
- [ ] **RNF-03:** Arquitetura Limpa com independência de frameworks.
- [ ] **RNF-05:** Processamento em background jobs para tarefas pesadas.

---

## 📖 Documentação Adicional

Para entender as decisões de design e a evolução do projeto, consulte:
* [**Architecture Decision Records (ADRs)**](./docs/adr/) - Por que escolhemos cada tecnologia.
* [**Especificação de Requisitos**](./docs/project-spec.md) - Detalhamento técnico do sistema.
* [**Changelog**](./CHANGELOG.md) - Histórico de evolução e versões do Atomik.

---

## ⚙️ Como Executar o Projeto

**Pré-requisitos:** Docker e Docker Compose instalados.
