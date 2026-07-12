# ADR-005: JWT Authentication Strategy

## Status
Accepted (implementation pending — Auth module)

## Context
The backend serves two frontend applications: a customer storefront (Next.js SSR) and an admin dashboard (React SPA). Both need stateless authentication that works across domains.

The Admin dashboard already implements a JWT-based auth flow with access tokens, refresh tokens, and a token refresh interceptor.

## Decision
We use **JWT (JSON Web Tokens)** with a dual-token strategy:
- **Access token**: Short-lived (15 minutes), sent in the `Authorization: Bearer` header
- **Refresh token**: Long-lived (7 days), used to obtain new access tokens via `POST /users/refresh-token`
- **Token blacklisting**: Redis-backed blacklist for logout and token revocation

## Alternatives Considered
- **Session-based auth (cookies)**: Requires sticky sessions or shared session store. Doesn't work well across domains (storefront + admin on different origins). Not truly stateless.
- **OAuth2 / OpenID Connect with external provider (Auth0, Keycloak)**: Powerful but adds external dependency and cost. Over-engineered for our current scale.
- **API Keys**: Too simple for user authentication. No user-specific permissions.

## Consequences
- **Easier**: Stateless — no server-side session storage needed. Works across domains. Standard `Authorization: Bearer` header pattern.
- **Harder**: Token revocation requires Redis blacklist. Must handle token refresh flow carefully. JWT payload must not contain sensitive data (tokens are base64-decoded, not encrypted).
