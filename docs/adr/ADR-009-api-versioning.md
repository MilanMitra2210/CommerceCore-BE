# ADR-009: API Versioning via URL Path

## Status
Accepted

## Context
APIs evolve over time. Breaking changes (removing fields, changing response structure, renaming endpoints) can break frontend clients. We need a versioning strategy to allow API evolution without breaking existing consumers.

## Decision
We version the API via **URL path prefix**: `/api/v1/`.

This is configured globally via Spring Boot's `server.servlet.context-path=/api/v1`, so all endpoints automatically live under `/api/v1/`.

When a breaking change is needed, we introduce `/api/v2/` while keeping `/api/v1/` operational until all clients migrate.

**Non-breaking changes** (adding new fields, new endpoints) do not require a version bump.

## Alternatives Considered
- **Header versioning (`Accept: application/vnd.commercecore.v1+json`)**: Clean URLs but harder to test (can't paste URLs into a browser), harder to route at the proxy level
- **Query parameter versioning (`?version=1`)**: Simple but semantically incorrect — the version is not a query parameter, it's a fundamental aspect of the resource representation
- **No versioning**: Only works if you never make breaking changes. Unrealistic for a long-lived API.

## Consequences
- **Easier**: Simple, explicit, visible in URLs, easy to route at reverse proxy (nginx, API gateway), easy to test (just change the URL)
- **Harder**: URL changes when version bumps (frontend needs to update base URL), potentially running two versions simultaneously during migration
