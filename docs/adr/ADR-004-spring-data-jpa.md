# ADR-004: Spring Data JPA with Hibernate

## Status
Accepted

## Context
The application needs an ORM layer to map Java objects to PostgreSQL tables. We need standard CRUD operations, pagination, dynamic queries, and audit support.

## Decision
We use **Spring Data JPA** with **Hibernate** as the JPA provider.

## Alternatives Considered
- **Raw JDBC / JdbcTemplate**: Maximum control and performance but requires writing all SQL manually, including mapping, pagination, and audit logic
- **MyBatis**: SQL-first mapper, good for complex queries but lacks the productivity of JPA for standard CRUD operations
- **JOOQ**: Type-safe SQL, excellent for complex queries, but less ecosystem integration with Spring Boot (no `@CreatedDate`, `@Version`, etc.)
- **Spring Data JDBC**: Simpler than JPA (no lazy loading, no session), but lacks features we need (inheritance mapping, `@MappedSuperclass`, optimistic locking via `@Version`)

## Consequences
- **Easier**: Repository interfaces with zero boilerplate, built-in pagination, JPA auditing (`@CreatedDate`, `@CreatedBy`), optimistic locking (`@Version`), Flyway integration
- **Harder**: N+1 query risks (mitigated with `@EntityGraph` and `JOIN FETCH`), session management complexity (mitigated with `open-in-view=false`), hibernate proxies can be confusing
