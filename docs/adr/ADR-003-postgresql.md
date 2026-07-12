# ADR-003: PostgreSQL as Primary Database

## Status
Accepted

## Context
CommerceCore requires a relational database for transactional data integrity (orders, payments, inventory). The commerce domain has complex relationships (products → variants → attributes → categories) that benefit from strong referential integrity.

## Decision
We use **PostgreSQL 16** as the primary database.

## Alternatives Considered
- **MySQL**: Viable but PostgreSQL has superior JSON support (JSONB), better UUID handling, full-text search, and richer indexing options (GIN, GiST)
- **MongoDB**: Document model doesn't suit relational commerce data well. Lack of transactions across collections makes order processing risky
- **DynamoDB / Cloud-native**: Vendor lock-in, not suitable for local development without emulators

## Consequences
- **Easier**: ACID compliance, native UUID type, JSONB for flexible schema fields (product specifications, content blocks), excellent Spring Data JPA integration
- **Harder**: Requires running a PostgreSQL instance (mitigated by Docker Compose)
