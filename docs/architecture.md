# Architecture

## Architectural Style

The committed backend is a small layered monolith:

- Web layer: `UserController`
- Service layer: `UserService`
- Shared response helpers: `ApiResponseBuilder`
- Error boundary: `GlobalExceptionHandler`
- Persistence abstraction: `UserRepository`
- Domain model: Spring Data JDBC entities under `model`
- Bootstrapping and configuration: `DbBackendApplication` and `application.yml`

Request flow in HEAD is:

1. HTTP request reaches `UserController`.
2. Bean Validation runs on request bodies annotated with `@Valid`.
3. The controller delegates CRUD logic to `UserService`.
4. `UserService` calls `UserRepository` for persistence operations.
5. Spring Data JDBC persists or fetches entities from PostgreSQL.
6. Errors are shaped by `ApiResponseBuilder` and `GlobalExceptionHandler`.

## Layer Diagram

```mermaid
flowchart TB
  subgraph Web
    UC[UserController]
  end
  subgraph Service
    US[UserService]
  end
  subgraph Common
    ARB[ApiResponseBuilder]
    GEH[GlobalExceptionHandler]
  end
  subgraph Persistence
    UR[UserRepository]
  end
  subgraph Domain
    U[User]
    E[Event]
    OT[OfferingType]
    R[Receipt]
    T[Transaction]
    TI[TransactionItem]
    PM[PaymentMode]
  end
  UC --> US
  US --> UR
  UC --> ARB
  GEH --> ARB
  UR --> U
  UR --> DB[(PostgreSQL)]
  U -.validated by.-> UC
```

## Design Patterns

- Repository pattern: `UserRepository` extends `CrudRepository<User, Long>`.
- Service layer pattern: `UserService` encapsulates user CRUD business flow over repository calls.
- Utility class pattern: `ApiResponseBuilder` has a private constructor and only static methods.
- Controller advice: `GlobalExceptionHandler` centralizes validation and generic exception handling.
- Constructor injection: `UserController` receives `UserService` through its constructor.
- Aggregate reference modeling: `Receipt`, `Transaction`, and `TransactionItem` use `AggregateReference` to point at other tables.
- Enum-based domain classification: `OfferingCategory` and `PricingType` encode allowed values.

## Inversion of Control

The code relies on Spring Boot auto-configuration and dependency injection. There is no custom DI container and no manual service locator.

## Cross-Cutting Concerns

- Validation is handled with Jakarta Bean Validation annotations on request models.
- Error shaping is centralized in `GlobalExceptionHandler`.
- Logging is configured at JDBC trace/debug levels in `application.yml`.
- No security, CORS, caching, rate limiting, or tracing middleware is present in HEAD.

## Backend Deep Dive

The backend now uses a dedicated service layer for user CRUD. `UserController` handles HTTP concerns and delegates create/read/update/delete behavior to `UserService`, while persistence remains in `UserRepository`. This keeps transport logic and business/data-access orchestration separated.

There are no scheduled jobs, async message consumers, or event publishers in committed history.

## Frontend Architecture

Not found in committed history for this repository.