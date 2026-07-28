# ADR-0003 — Use a GIN index with `jsonb_path_ops` on `instruments.metadata`

- Status: Accepted
- Date: 2026-07-27
- Deciders: ReconX team

## Context

Following ADR-0002, instrument metadata is stored in a PostgreSQL 16 `JSONB` column (`instruments.metadata`). Reconciliation analysts and automated jobs frequently filter instruments using JSON path and containment operators (`@>`). As data accumulates over the 5-year retention window (~91M trade-related records at steady state), unindexed queries will cause full-table scans and degrade analyst dashboard performance.

We evaluated three indexing strategies:
- **No index:** Unacceptable query performance and high CPU load for containment lookups.
- **B-tree index:** Ineffective for dynamic, deeply nested JSON containment queries (`@>`); only supports whole-column or scalar extraction.
- **Default GIN index (`jsonb_ops`):** Indexes both keys and values, resulting in significantly larger index footprints and higher write overhead during ingest (~50k trades/day).

## Decision

Create a GIN (Generalized Inverted Index) on `instruments.metadata` using the `jsonb_path_ops` operator class (`CREATE INDEX idx_instruments_metadata_path_ops ON instruments USING gin (metadata jsonb_path_ops);`).

## Consequences

**Positive**
- **Smaller Index Footprint:** `jsonb_path_ops` hashes entire key-value paths rather than individual keys and values separately, significantly reducing disk footprint compared to default GIN.
- **Faster Query Execution & Write Throughput:** Smaller index size yields better cache hit rates and lower write overhead during daily trade processing.
- **Optimized for Containment:** Provides top-tier performance for `@>` containment queries used across analyst workflows.

**Negative**
- **Loss of Key-Existence Queries:** Does not support the `?`, `?|`, or `?&` operators (key-existence checks). *Mitigation:* Analyst queries filter on key-value pairs (`@>`), making key-only lookups unnecessary.