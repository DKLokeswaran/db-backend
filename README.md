# Donation Management Backend

REST API service for the [Donation Management](https://github.com/DKLokeswaran/db-frontend) platform — a system built primarily to help temples track devotees (users) and donations, with a domain model that extends naturally to events, offerings, receipts, and transactions.

**Companion frontend:** [DKLokeswaran/db-frontend](https://github.com/DKLokeswaran/db-frontend)

---

## Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Overview](#api-overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Development](#development)
- [Testing](#testing)
- [Build](#build)
- [Deployment](#deployment)
- [Documentation](#documentation)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Code of Conduct](#code-of-conduct)
- [Security](#security)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Authors](#authors)
- [Support](#support)

---

## About

Donation Management Backend is a Spring Boot service that exposes HTTP APIs for managing user records and lays the groundwork for donation workflows. It is designed for temple administrators who need a reliable way to register devotees, look them up by mobile number, and (as the platform grows) record offerings, receipts, and payment transactions.

The codebase follows a layered monolith pattern and is intentionally small and readable so it can be adapted to adjacent use cases — community centers, cultural organizations, or any group that needs structured donor and contribution tracking.

---

## Features

- **User management** — Create, read, update, and delete user records with validated request bodies.
- **Mobile lookup** — List users by exact mobile number or search distinct mobile prefixes for typeahead UIs.
- **Consistent API responses** — Standard success payloads and centralized error handling via `GlobalExceptionHandler`.
- **Domain model for donations** — Entities for events, offering types, payment modes, receipts, transactions, and transaction items (persistence layer in place; REST endpoints expand over time).
- **PostgreSQL persistence** — Production datasource via environment variables; H2 in-memory database for tests.
- **Developer tooling** — Maven Wrapper, Spotless formatting, HTTP request samples under `api-testing/`.

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Runtime | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Web | Spring Web (REST) |
| Persistence | Spring Data JDBC |
| Database | PostgreSQL (runtime), H2 (tests) |
| Validation | Jakarta Bean Validation |
| Build | Maven (wrapper included) |
| Formatting | Spotless (Google Java Format, AOSP) |

---

## Prerequisites

- **JDK 17** or newer
- **PostgreSQL** database reachable from your machine
- **Git**
- (Optional) [Donation Management Frontend](https://github.com/DKLokeswaran/db-frontend) for the full UI experience

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/DKLokeswaran/db-backend.git
cd db-backend
```

### 2. Create a local environment file

Create a `.env` file in the project root (this file is gitignored and must not be committed):

```bash
DB_DATASOURCE_URL=jdbc:postgresql://localhost:5432/donation_management
DB_DATASOURCE_USERNAME=your_username
DB_DATASOURCE_PASSWORD=your_password
```

Ensure the PostgreSQL database exists and that Spring Data JDBC can create or access the required tables for your schema.

### 3. Start the server

```bash
./scripts/run-local.sh
```

The API listens on **port 8081** by default.

Alternatively, export the variables manually and run Maven:

```bash
export DB_DATASOURCE_URL=...
export DB_DATASOURCE_USERNAME=...
export DB_DATASOURCE_PASSWORD=...
./mvnw spring-boot:run
```

### 4. Verify

Run the test suite:

```bash
./mvnw test
```

Try a sample request (with the server running):

```bash
curl http://localhost:8081/api/users
```

See `api-testing/user.http` for more examples compatible with REST Client extensions.

---

## Configuration

| Variable | Required | Description |
| --- | --- | --- |
| `DB_DATASOURCE_URL` | Yes | JDBC URL for PostgreSQL |
| `DB_DATASOURCE_USERNAME` | Yes | Database username |
| `DB_DATASOURCE_PASSWORD` | Yes | Database password |

Application settings live in `src/main/resources/application.yml`:

- Application name: `db-backend`
- Server port: `8081`
- JDBC debug logging enabled for local development

Never commit secrets. Use `.env` locally and your platform's secret manager in production.

---

## Usage

With the backend running on `localhost:8081`, the frontend dev server proxies `/api` requests to this service. You can also call the API directly.

**Create a user:**

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Example Devotee",
    "mobileNo": "9876543210",
    "addressLine": "Temple Street",
    "locality": "City",
    "state": "State",
    "country": "Country",
    "pincode": "600001"
  }'
```

**Search mobile prefixes (typeahead):**

```bash
curl "http://localhost:8081/api/users/search/mobile?prefix=98&limit=10"
```

Full request/response documentation: [docs/api-reference.md](docs/api-reference.md).

---

## API Overview

Base path: `/api/users`

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users/{id}` | Get user by ID |
| `GET` | `/api/users` | List all users, or filter with `?mobile=` |
| `GET` | `/api/users/search/mobile` | Distinct mobile prefix search (`prefix`, optional `limit`) |
| `PUT` | `/api/users/{id}` | Update a user |
| `DELETE` | `/api/users/{id}` | Delete a user |

---

## Architecture

Request flow:

```
HTTP Client → UserController → UserService → UserMapper → UserRepository → PostgreSQL
                    ↓
         GlobalExceptionHandler / ApiResponseBuilder
```

```mermaid
flowchart LR
  Client[HTTP client] --> Controller[UserController]
  Controller --> Service[UserService]
  Service --> Repo[UserRepository]
  Service --> Mapper[UserMapper]
  Controller --> EH[GlobalExceptionHandler]
  Repo --> DB[(PostgreSQL)]
```

Layer responsibilities:

- **Controller** — REST mapping, `@Valid` on request DTOs, no entity exposure at the boundary.
- **Service** — Business orchestration and `ResourceNotFoundException` for missing records.
- **Mapper** — Manual entity ↔ DTO conversion.
- **Repository** — Spring Data JDBC `CrudRepository` implementations.
- **Model** — Persistence entities and enums only (no API validation).

Detailed design: [docs/architecture.md](docs/architecture.md).

---

## Project Structure

```
db-backend/
├── api-testing/          # HTTP samples (e.g. user.http)
├── docs/                 # In-depth project documentation
├── scripts/
│   └── run-local.sh      # Load .env and start Spring Boot
├── src/main/java/com/lokeswarandk/db_backend/
│   ├── common/           # ApiResponseBuilder, StringUtils
│   ├── controller/       # REST controllers
│   ├── dto/              # Request and response DTOs
│   ├── exception/        # GlobalExceptionHandler, domain exceptions
│   ├── mapper/           # Entity ↔ DTO mappers
│   ├── model/            # JDBC entities and enums
│   ├── repository/       # Spring Data JDBC repositories
│   └── service/          # Business logic
├── src/main/resources/
│   └── application.yml
└── src/test/             # Unit, slice, and context tests
```

---

## Development

### Code style

Format before committing:

```bash
./mvnw spotless:apply
```

Verify formatting (also runs on `verify`):

```bash
./mvnw spotless:check
```

### Adding a new resource

Follow the existing **User** module as the reference:

1. Entity in `model/`
2. `{Entity}Repository` extending `CrudRepository`
3. DTOs in `dto/request` and `dto/response`
4. `{Entity}Mapper` in `mapper/`
5. `{Entity}Service` and `{Entity}Controller`
6. Samples in `api-testing/{resource}.http`
7. `{Entity}ControllerTests` and `{Entity}ServiceTests`

Conventions: [docs/conventions.md](docs/conventions.md).

### Manual API testing

Use `api-testing/user.http` with IntelliJ HTTP Client, VS Code REST Client, or similar.

---

## Testing

```bash
./mvnw test
```

| Test class | Scope |
| --- | --- |
| `DbBackendApplicationTests` | Spring context smoke test |
| `UserControllerTests` | `@WebMvcTest` slice for `/api/users` |
| `UserServiceTests` | Unit tests with mocked repository |

Tests use an in-memory H2 database configured in `src/test/resources/application.yml` — no live PostgreSQL required for CI or local test runs.

Details: [docs/testing.md](docs/testing.md).

---

## Build

```bash
./mvnw clean package
```

Produces a runnable Spring Boot JAR under `target/`. Spotless checks run as part of the Maven `verify` lifecycle.

---

## Deployment

The repository currently ships with a local development entry point (`./scripts/run-local.sh`). There is no committed Dockerfile, docker-compose file, or CI/CD workflow yet.

For production:

1. Set `DB_DATASOURCE_*` environment variables on your host or orchestrator.
2. Build the JAR with `./mvnw clean package`.
3. Run `java -jar target/db-backend-*.jar` (or your container image equivalent).
4. Place the service behind a reverse proxy if exposing publicly.
5. Pair with the [frontend](https://github.com/DKLokeswaran/db-frontend) static build or dev proxy as needed.

Operational notes: [docs/build-and-deploy.md](docs/build-and-deploy.md).

---

## Documentation

| Document | Description |
| --- | --- |
| [docs/README.md](docs/README.md) | Documentation index |
| [docs/architecture.md](docs/architecture.md) | Layers, flow, and design decisions |
| [docs/api-reference.md](docs/api-reference.md) | REST endpoints and schemas |
| [docs/data-model.md](docs/data-model.md) | Tables and entity relationships |
| [docs/modules.md](docs/modules.md) | Module-by-module reference |
| [docs/error-handling.md](docs/error-handling.md) | Exception and response patterns |
| [docs/testing.md](docs/testing.md) | Test strategy and inventory |
| [docs/conventions.md](docs/conventions.md) | Coding standards |
| [docs/build-and-deploy.md](docs/build-and-deploy.md) | Build and operations |
| [docs/commit-history.md](docs/commit-history.md) | Change log from git history |

---

## Roadmap

- REST APIs for events, offerings, receipts, and transactions
- Database migration tooling (Flyway or Liquibase)
- Authentication and role-based access for temple staff
- Docker image and compose stack for local and production deployment
- CI pipeline (build, test, Spotless check)

Contributions toward any of these areas are welcome — see [Contributing](#contributing).

---

## Contributing

We welcome bug reports, documentation improvements, and pull requests.

1. Fork the repository and create a feature branch from `master`.
2. Follow existing conventions in [docs/conventions.md](docs/conventions.md).
3. Run `./mvnw spotless:apply` and `./mvnw test` before opening a PR.
4. Describe what changed and why in the pull request body.
5. Link related issues when applicable.

For larger changes (new resources, schema changes, auth), open an issue first to align on approach.

---

## Code of Conduct

This project expects respectful, inclusive collaboration. Be constructive in reviews, welcome newcomers, and focus feedback on the work rather than the person. Maintainers may remove contributions or participation that violates these expectations.

---

## Security

- **Do not commit** `.env` files, database passwords, or API keys.
- The committed API has **no authentication layer** yet; do not expose an unprotected instance to the public internet.
- Report vulnerabilities privately to the repository maintainer rather than opening a public issue with exploit details.

---

## License

No `LICENSE` file is committed in this repository yet. Until one is added, all rights are reserved by the copyright holder. If you intend to use or distribute this software, contact the maintainer to clarify licensing terms.

---

## Acknowledgments

- Spring Boot and the Spring ecosystem for the application framework
- PostgreSQL and the open-source database community
- Temple communities whose operational needs shaped the domain model

---

## Authors

**DK Lokeswaran** — [GitHub @DKLokeswaran](https://github.com/DKLokeswaran)

---

## Support

- **Bug reports & feature requests:** [GitHub Issues](https://github.com/DKLokeswaran/db-backend/issues)
- **Frontend companion:** [Donation Management Frontend](https://github.com/DKLokeswaran/db-frontend)
- **Deep dives:** [docs/](docs/)
