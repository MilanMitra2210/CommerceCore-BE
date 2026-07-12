# CommerceCore — Backend API

**Headless Commerce Platform** built with Java 21, Spring Boot 3, and PostgreSQL.

---

## Architecture

- **Style**: Modular Monolith with Layered Architecture
- **Pattern**: Headless Commerce (REST APIs consumed by separate frontends)
- **Database**: PostgreSQL 16 with Flyway migrations
- **Caching**: Redis 7
- **Auth**: JWT (access + refresh tokens)
- **Docs**: Swagger / OpenAPI

See [Architecture Documentation](docs/architecture.md) and [ADRs](docs/adr/) for design decisions.

---

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### 1. Start Infrastructure

```bash
docker compose up -d
```

This starts PostgreSQL (port 5432) and Redis (port 6379).

### 2. Configure Environment

```bash
cp .env.example .env
# Edit .env if you need to change defaults
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080/api/v1`.

### 4. Verify

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Swagger UI
open http://localhost:8080/api/v1/swagger-ui.html
```

---

## Project Structure

```
src/main/java/com/commercecore/api/
├── CommerceCoreApplication.java       # Entry point
├── common/                            # Cross-cutting concerns
│   ├── config/                        # Security, CORS, JPA, Swagger
│   ├── controller/                    # Health endpoint
│   ├── dto/                           # ApiResponse, PaginatedResponse
│   ├── entity/                        # BaseEntity
│   └── exception/                     # Global exception handling
└── {module}/                          # Feature modules (user, product, etc.)
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    ├── dto/
    └── mapper/
```

---

## Frontend Clients

| Application | Repository | Port | Technology |
|---|---|---|---|
| Storefront | CommerceCore-FE | 3002 | Next.js 16 |
| Admin Dashboard | CommerceCore-Admin | 5173 | React + Vite |

---

## Engineering Standards

- **Never** expose JPA entities in API responses — use DTOs
- **Controllers** contain zero business logic
- **Services** own business logic and transactions
- **Constructor injection** only (no field injection)
- **Bean Validation** on all request DTOs
- **UUID** primary keys (type-safe, no enumeration attacks)
- **Soft delete** for all business entities
- **Flyway** for all schema changes (no manual DDL)

See [ADRs](docs/adr/) for the rationale behind each decision.

---

## Development Workflow

```
feature/XX-description  →  develop  →  main
```

Commits follow [Conventional Commits](https://www.conventionalcommits.org/):
```
feat(product): add product variant CRUD
fix(auth): handle expired refresh token
docs(adr): add ADR-003 for PostgreSQL
```

---

## License

Private — All rights reserved.
