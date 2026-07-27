# ADR-0002 — Store instrument metadata in a PostgreSQL JSONB column (`instruments.metadata`)

- Status: Accepted
- Date: 2026-06-03
- Deciders: ReconX team

## Context

ReconX processes ~50,000 trades/day across diverse asset classes requiring 5-year retention and support for 10 concurrent reconciliation analysts. Instrument metadata fields vary significantly by asset class and change frequently as market interfaces evolve.

We evaluated three alternative approaches:
- **Normalized relational tables:** Rigid schema requiring frequent DDL migrations and extensive `LEFT JOIN`s across sparse attributes.
- **Plain JSON/Text column:** Schema flexibility, but lacks native querying and indexing capabilities in PostgreSQL.
- **Entity-Attribute-Value (EAV) schema:** High query overhead, complex join logic, and poor type safety across large datasets.

We must balance schema flexibility for evolving instrument attributes with efficient querying, indexing, and transactional integrity on PostgreSQL 16.

## Decision

Store asset-class-specific instrument metadata in a single `JSONB` column (`instruments.metadata`) within PostgreSQL 16.

Key attributes required for frequent search and reconciliation filtering will be indexed using expression-based indexes or GIN (Generalized Inverted Index) operators (e.g., `jsonb_path_ops`).

## Consequences

**Positive**
- New instrument attributes can be ingested immediately without schema migrations or deployment dependencies.
- PostgreSQL 16 `JSONB` supports indexed containment (`@>`) and path extraction operations, meeting query latency requirements for analyst workflows.
- Eliminates complex EAV joins or sparse wide tables in Spring Boot 3 JPA entities.

**Negative**
- Absence of database-enforced relational schema constraints on sub-fields. *Mitigation:* Enforce schema validation at the Spring Boot application tier.
- Slight storage footprint overhead compared to normalized primitive types. *Mitigation:* Acceptable given the dataset volume (~50k trades/day) over the 5-year retention window.