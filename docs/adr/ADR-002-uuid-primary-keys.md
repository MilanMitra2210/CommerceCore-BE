# ADR-002: UUID Primary Keys

## Status
Accepted

## Context
Every entity needs a primary key. The two common choices are auto-incrementing integers and UUIDs. The existing frontend applications use `id: string` for all entities, which aligns with UUIDs.

## Decision
We use **`java.util.UUID`** in Java and PostgreSQL's native **`UUID`** column type for all primary keys. Generation strategy is `GenerationType.UUID` (Hibernate generates UUIDs before INSERT).

## Alternatives Considered
- **Auto-increment (SERIAL/BIGSERIAL)**: Simple and sequential, but exposes record count (enumeration attacks), causes issues in distributed systems (ID conflicts during database merges), and doesn't match the frontend's `string` ID type
- **ULID / KSUID**: Sortable by time, but not natively supported by JPA/PostgreSQL without custom types
- **Snowflake IDs**: Excellent for distributed systems but over-engineered for a modular monolith

## Consequences
- **Easier**: No enumeration attacks, globally unique, merge-safe across environments, matches frontend contract
- **Harder**: UUIDs are 128 bits vs 64 bits for BIGINT (slightly larger indexes), not human-readable, not naturally sortable (use `createdAt` for ordering instead)
