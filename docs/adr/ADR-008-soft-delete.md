# ADR-008: Soft Delete Strategy

## Status
Accepted

## Context
Business data (products, users, orders, blog posts) should never be permanently deleted from the database. Reasons include:
- **Audit compliance**: Regulatory requirements may require data retention
- **Recovery**: Accidental deletions can be reversed
- **Referential integrity**: Hard-deleting a category breaks all products that reference it
- **Analytics**: Historical data is valuable for business intelligence

## Decision
All business entities use **soft delete** via two fields in `BaseEntity`:
- `deleted` (boolean, default `false`) — marks the record as deleted
- `deletedAt` (Instant, nullable) — records when the deletion occurred

**Hard delete** is reserved for:
- Join/association tables (many-to-many relationships)
- System metadata tables (e.g., Flyway history)
- Temporary/ephemeral data (e.g., expired tokens after cleanup)

**Query convention**: All repository queries must filter by `deleted = false` by default. This is enforced via Hibernate's `@Where` annotation or `@FilterDef` on entities.

## Alternatives Considered
- **Hard delete everywhere**: Simpler queries but irreversible, breaks referential integrity, loses audit trail
- **Event sourcing**: Complete audit trail via append-only event log, but massive complexity increase. Overkill for our scale.
- **Archive tables**: Move deleted records to separate tables. Doubles table count and complicates queries.

## Consequences
- **Easier**: Data recovery, audit compliance, referential integrity preserved
- **Harder**: Every query must filter `deleted = false` (mitigated by `@Where`), storage grows over time (mitigated by periodic archival jobs), unique constraints need special handling (e.g., soft-deleted email should allow re-registration)
