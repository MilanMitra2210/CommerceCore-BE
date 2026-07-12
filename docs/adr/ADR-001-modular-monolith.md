# ADR-001: Modular Monolith Architecture

## Status
Accepted

## Context
CommerceCore needs a backend architecture that supports clean module boundaries while keeping operational complexity low. The team is small and the product is greenfield.

Microservices offer independent deployment and scaling but introduce distributed transactions, service discovery, network latency, and massive operational overhead (Kubernetes, service mesh, distributed tracing). This is overkill for a team of 1-3 engineers.

A traditional monolith is simple to start but tends toward spaghetti code as it grows, making future extraction impossible.

## Decision
We adopt a **Modular Monolith** architecture. The application is organized into feature modules (user, product, category, blog, etc.), each with clear ownership boundaries. All modules deploy as a single JAR.

**Module rules:**
- Each module owns its entities, repositories, services, and controllers
- Cross-module communication happens only through service interfaces
- Entities never cross module boundaries
- No circular dependencies between modules

## Alternatives Considered
- **Microservices**: Rejected due to operational overhead disproportionate to team size
- **Traditional Monolith**: Rejected due to lack of module boundaries and future extraction difficulty

## Consequences
- **Easier**: Single deployment, shared database, simple debugging, low ops overhead
- **Harder**: Requires discipline to maintain module boundaries — code reviews must enforce the rules
- **Future**: Modules can be extracted into independent services when scale demands it
