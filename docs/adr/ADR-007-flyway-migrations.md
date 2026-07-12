# ADR-007: Flyway for Database Migrations

## Status
Accepted

## Context
Database schema changes must be tracked, versioned, and reproducible across environments (development, staging, production). Manual DDL execution is error-prone and impossible to audit.

## Decision
We use **Flyway** for all database schema migrations. Hibernate's `ddl-auto` is set to `validate` (it only checks that entities match the schema, it never modifies the database).

**Migration rules:**
1. Migrations are **immutable** — never edit a migration after it's been applied
2. Naming convention: `V{version}__{description}.sql` (e.g., `V2__create_users_table.sql`)
3. Every schema change goes through a migration — no manual DDL
4. Migrations are tested locally before committing

## Alternatives Considered
- **Hibernate `ddl-auto=update`**: Convenient for prototyping but dangerous in production. It can drop columns, break data, and doesn't support rollbacks. It also doesn't version-control schema changes.
- **Liquibase**: More feature-rich than Flyway (XML/YAML/JSON formats, rollback support), but more complex. Flyway's simplicity (plain SQL files) is preferred.
- **Manual SQL scripts**: No versioning, no reproducibility, no conflict detection.

## Consequences
- **Easier**: Version-controlled schema, reproducible across environments, team-safe (conflicts detected automatically), integrates with Spring Boot auto-configuration
- **Harder**: Requires writing SQL migrations manually (but this is a feature — you understand exactly what's happening to your database)
