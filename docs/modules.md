# Modules

## Source Inventory

### Bootstrap

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/DbBackendApplication.java` | Boot application | `main(String[] args)` | Standard `@SpringBootApplication` entry point. |

### Web Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/controller/UserController.java` | REST controller | `addUser`, `getUser`, `listUsers`, `updateUser`, `deleteUser` | CRUD endpoints under `/api/users`. |

### Service Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/service/UserService.java` | Spring service | `create`, `findById`, `findAll`, `update`, `deleteById` | Encapsulates user CRUD business flow and repository access. |

### Shared Utilities

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/common/ApiResponseBuilder.java` | Utility class | `error`, `validationError`, `messagePayload` | Builds consistent response maps. |

### Error Handling

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` | `handleValidationException`, `handleGenericException` | Centralizes validation and unexpected error responses. |

### Persistence

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/repository/UserRepository.java` | Spring Data repository | inherited `CrudRepository` methods | Only repository committed in HEAD. |

### Domain Model

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/model/User.java` | Entity | getters/setters for all fields | `@Table("user")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/Event.java` | Entity | getters/setters for all fields | `@Table("event")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/OfferingType.java` | Entity | getters/setters for all fields | `@Table("offering_type")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/PaymentMode.java` | Entity | getters/setters for all fields | `@Table("payment_mode")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/Receipt.java` | Entity | getters/setters for all fields | `@Table("receipt")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/Transaction.java` | Entity | getters/setters for all fields | `@Table("transaction")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/TransactionItem.java` | Entity | getters/setters for all fields | `@Table("transaction_item")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/OfferingCategory.java` | Enum | `DONATION`, `SERVICE`, `TAX` | Category classifier. |
| `src/main/java/com/lokeswarandk/db_backend/model/PricingType.java` | Enum | `FIXED`, `VARIABLE` | Pricing classifier. |

### Tests and API Artifacts

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/test/java/com/lokeswarandk/db_backend/DbBackendApplicationTests.java` | Spring Boot test | `contextLoads()` | Startup smoke test only. |
| `api-testing/user.http` | REST Client file | N/A | Manual endpoint smoke collection. |

## File-by-File Details

### `DbBackendApplication`

- Package: `com.lokeswarandk.db_backend`
- Class type: bootstrapping class
- Annotations: `@SpringBootApplication`
- Public method: `main(String[] args)` returns `void`
- Purpose: starts the Spring Boot application

### `UserController`

- Package: `com.lokeswarandk.db_backend.controller`
- Class type: REST controller
- Annotation: `@RestController`, `@RequestMapping("/api/users")`
- Injected dependency: `UserService`
- Public methods:
  - `addUser(User user)`: POST create flow delegated to service
  - `getUser(Long id)`: GET by id; returns 404 when missing
  - `listUsers()`: GET all users
  - `updateUser(Long id, User updatedUser)`: PUT replace flow delegated to service; returns 404 when missing
  - `deleteUser(Long id)`: DELETE by id; returns 404 when missing

### `UserService`

- Package: `com.lokeswarandk.db_backend.service`
- Class type: Spring `@Service`
- Injected dependency: `UserRepository`
- Public methods:
  - `create(User user)`: create flow; clears incoming id and fills `createdAt` when absent
  - `findById(Long id)`: fetch one user
  - `findAll()`: list all users
  - `update(Long id, User updatedUser)`: replace flow; returns empty when user does not exist
  - `deleteById(Long id)`: delete flow; returns false when user does not exist

### `ApiResponseBuilder`

- Package: `com.lokeswarandk.db_backend.common`
- Class type: utility/factory
- Public static methods:
  - `error(HttpStatus status, String error, String message)`
  - `validationError(Map<String, String> details)`
  - `messagePayload(String message)`
  - `messagePayload(String message, String key, Object value)`
- Purpose: standardize response envelopes for errors and simple success messages

### `GlobalExceptionHandler`

- Package: `com.lokeswarandk.db_backend.exception`
- Class type: controller advice
- Annotations: `@RestControllerAdvice`
- Public methods:
  - `handleValidationException(MethodArgumentNotValidException ex)`
  - `handleGenericException(Exception ex)`
- Purpose: convert validation failures and unexpected exceptions into API responses

### `UserRepository`

- Package: `com.lokeswarandk.db_backend.repository`
- Class type: repository interface
- Annotation: `@Repository`
- Extends: `CrudRepository<User, Long>`
- Public surface: inherited CRUD methods only

### `User`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("user")`
- Fields: `id`, `name`, `mobileNo`, `addressLine`, `locality`, `state`, `country`, `pincode`, `createdAt`
- Constraints: `@NotBlank` on all string fields except `createdAt`
- Public methods: standard getters and setters for each field

### `Event`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("event")`
- Fields: `id`, `name`, `description`, `startDate`, `endDate`, `createdAt`
- Constraint: `@NotBlank` on `name`

### `OfferingType`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("offering_type")`
- Fields: `id`, `name`, `category`, `pricingType`, `price`, `allowMultiple`, `isActive`, `createdAt`
- Constraints: `@NotBlank`, `@NotNull`, `@DecimalMin`

### `PaymentMode`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("payment_mode")`
- Fields: `id`, `name`
- Constraint: `@NotBlank` on `name`

### `Receipt`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("receipt")`
- Fields: `id`, `eventId`, `receiptNo`, `userId`, `displayName`, `displayLocality`, `createdAt`
- Relationship fields use `AggregateReference`
- Constraints: `@NotNull` on `eventId` and `receiptNo`, `@Min(1)` on `receiptNo`, `@NotBlank` on `displayName`

### `Transaction`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("transaction")`
- Fields: `id`, `receiptId`, `offeringTypeId`, `paymentModeId`, `quantity`, `amount`, `notes`, `createdAt`
- Constraints: `@NotNull` on `receiptId`, `offeringTypeId`, `quantity`, `@Min(1)` on `quantity`, `@DecimalMin` on `amount`

### `TransactionItem`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("transaction_item")`
- Fields: `id`, `transactionId`, `itemName`, `quantity`, `unit`
- Constraints: `@NotNull` on `transactionId` and `quantity`, `@Min(1)` on `quantity`, `@NotBlank` on `itemName`

### Enums

- `OfferingCategory`: `DONATION`, `SERVICE`, `TAX`
- `PricingType`: `FIXED`, `VARIABLE`

## Public API Notes

The repository now includes a service class for user flows. There are still no DTO classes, mapper classes, or custom exceptions in committed history. The controller still operates on entity objects at the API boundary.