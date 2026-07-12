# ADR-006: Redis for Caching and Sessions

## Status
Accepted

## Context
The application needs in-memory caching for frequently accessed data (product catalogs, categories) and a store for JWT token blacklisting on logout.

## Decision
We use **Redis 7** as the caching and session management layer.

Primary use cases:
1. **Cache hot data**: Product listings, category trees, CMS pages
2. **JWT token blacklist**: Store revoked access tokens until they naturally expire
3. **Rate limiting**: (future) API rate limiting per user/IP
4. **Session storage**: (future) Shopping cart sessions

## Alternatives Considered
- **In-process cache (Caffeine/Guava)**: Fast but not shared across multiple application instances. Token blacklist wouldn't work in a multi-instance deployment.
- **Memcached**: Simpler than Redis but lacks data structures (sets, sorted sets, hashes) needed for token blacklisting and rate limiting.
- **Database-backed cache**: PostgreSQL can cache, but adds load to the primary database and is slower than in-memory stores.

## Consequences
- **Easier**: Sub-millisecond reads, rich data structures, native TTL support for token expiry, excellent Spring Boot integration (`@Cacheable`)
- **Harder**: Additional infrastructure to maintain (mitigated by Docker Compose)
