# TICKET-ADV006 — ER Model (8 Entities)

```mermaid
erDiagram
    COUNTERPARTIES ||--o{ TRADES : executes
    INSTRUMENTS ||--o{ TRADES : covers
    TRADES ||--o{ SETTLEMENTS : settles
    TRADES ||--o{ RECON_BREAKS : produces
    RECON_JOBS ||--o{ RECON_BREAKS : detects
    USERS ||--o{ AUDIT_LOG : performs
    TRADES ||--o{ AUDIT_LOG : audited

    COUNTERPARTIES {
        bigint id PK
        string name
        string lei_code UK
        string region
    }

    INSTRUMENTS {
        bigint id PK
        string symbol UK
        string name
        string asset_class
        string currency
        string isin UK
        string metadata
    }

    TRADES {
        bigint id PK
        string trade_ref UK
        bigint instrument_id FK
        bigint counterparty_id FK
        string asset_class
        string side
        float quantity
        float price
        date trade_date
        string status
        datetime deleted_at
        datetime created_at
        datetime modified_at
    }

    SETTLEMENTS {
        bigint id PK
        bigint trade_id FK
        date settlement_date
        float amount
        string status
    }

    RECON_BREAKS {
        bigint id PK
        bigint trade_id FK
        bigint recon_job_id FK
        string discrepancy_type
        string status
        datetime detected_at
        datetime resolved_at
        string resolution_note
    }

    RECON_JOBS {
        bigint id PK
        string job_id UK
        date from_date
        date to_date
        string status
        datetime started_at
        datetime finished_at
        int trades_processed
        int breaks_detected
    }

    AUDIT_LOG {
        bigint id PK
        string event_id UK
        bigint trade_id FK
        bigint user_id FK
        string event_type
        datetime event_timestamp
        string before_state
        string after_state
    }

    USERS {
        bigint id PK
        string email UK
        string password_hash
        string role
        boolean enabled
        datetime created_at
    }
```