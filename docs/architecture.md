# CommerceCore — Architecture Overview

## System Context

CommerceCore is a headless commerce platform. The backend (this repository) serves as the single source of truth for two frontend applications:

- **CommerceCore-FE** — Next.js customer-facing storefront (port 3002)
- **CommerceCore-Admin** — React admin dashboard (port 5173)

Both frontends are pre-existing and define the API contract that this backend must honor.

## Architecture Style

**Modular Monolith** — the application is organized into feature modules (user, product, category, blog, etc.), each with its own controller/service/repository layers. All modules deploy as a single JAR but maintain clean boundaries for future extraction into microservices if needed.

## Layered Architecture

```
Controller Layer  →  Receives HTTP, validates input, returns ApiResponse
     ↓
Service Layer     →  Business logic, transactions, entity ↔ DTO mapping
     ↓
Repository Layer  →  Data access (Spring Data JPA)
     ↓
Entity Layer      →  JPA entities (never exposed outside Service layer)
```

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Auth | Spring Security + JWT |
| Migrations | Flyway |
| Mapping | MapStruct |
| Docs | Swagger / OpenAPI |
| Build | Maven |
| Containers | Docker Compose |

## Module Map

| Module | Domain | Key Entities |
|---|---|---|
| `user` | Identity & Access | User, RefreshToken |
| `category` | Catalog | Category, SubCategory |
| `product` | Catalog | Product, ProductVariant, ProductBadge, ProductMedia |
| `filter` | Catalog | FilterGroup, FilterOption |
| `media` | Content | Media, Folder |
| `cms` | Content | CMSPage |
| `blog` | Content | Blog, BlogCategory, BlogAuthor, BlogSection |
| `enquiry` | Engagement | Enquiry |

## Key Design Decisions

See the [Architecture Decision Records (ADRs)](adr/) for detailed rationale behind every major decision.
