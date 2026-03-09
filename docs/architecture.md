# 🏛️ Arquitetura de Dados: Atomik

Este documento detalha a modelagem de dados do **Atomik**, focando em garantir a integridade financeira, auditabilidade e resiliência offline.

---

## 📊 Diagrama de Entidade-Relacionamento (ERD)

O diagrama abaixo representa a estrutura do banco de dados relacional (PostgreSQL) utilizada no projeto.

```mermaid
erDiagram
    USER ||--o{ ACCOUNT : "possui"
    USER ||--o{ CATEGORY : "define"
    USER ||--o{ BUDGET : "gerencia"
    ACCOUNT ||--o{ TRANSACTION : "origem/destino"
    CATEGORY ||--o{ TRANSACTION : "categoriza"
    CATEGORY ||--o{ BUDGET : "limita"
    TRANSACTION ||--o{ AUDIT_LOG : "rastreada por"

    USER {
        uuid id PK
        string name
        string email UK
        string password_hash
        string preferred_currency
        timestamp created_at
    }

    ACCOUNT {
        uuid id PK
        uuid user_id FK
        string name
        string type "CHECKING, SAVINGS, CASH, CREDIT_CARD"
        string currency
        timestamp created_at
    }

    TRANSACTION {
        uuid id PK
        uuid user_id FK
        uuid category_id FK
        uuid source_account_id FK "Débito"
        uuid destination_account_id FK "Crédito"
        decimal amount
        string description
        timestamp date
        string type "REVENUE, EXPENSE, TRANSFER"
        string sync_status "PENDING, SYNCED"
        timestamp created_at
    }

    CATEGORY {
        uuid id PK
        uuid user_id FK
        string name
        string icon
        string color
        boolean is_default
    }

    BUDGET {
        uuid id PK
        uuid user_id FK
        uuid category_id FK
        decimal limit_amount
        integer month
        integer year
    }

    AUDIT_LOG {
        uuid id PK
        uuid transaction_id FK
        string field_changed
        string old_value
        string new_value
        timestamp changed_at
    }