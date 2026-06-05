# Modules

## Source Inventory

### Bootstrap

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/DbBackendApplication.java` | Boot application | `main(String[] args)` | Standard `@SpringBootApplication` entry point. |

### Web Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/controller/UserController.java` | REST controller | `addUser`, `getUser`, `listUsers`, `searchMobileByPrefix`, `updateUser`, `deleteUser` | CRUD and mobile search endpoints under `/api/users`. |

### Service Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/service/UserService.java` | Spring service | `create`, `findById`, `findAll`, `findByMobileNo`, `searchMobileNosByPrefix`, `update`, `deleteById` | Encapsulates user CRUD, mobile filter, and prefix search over the repository. |

### Shared Utilities

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/common/ApiResponseBuilder.java` | Utility class | `error`, `validationError`, `messagePayload` | Builds consistent response maps. |
| `src/main/java/com/lokeswarandk/db_backend/common/StringUtils.java` | Utility class | `requireNonBlank` | Trims and validates non-blank request parameters; throws `IllegalArgumentException` when invalid. |

### Error Handling

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` | `handleResourceNotFoundException`, `handleIllegalArgumentException`, `handleValidationException`, `handleGenericException` | Centralizes not-found, bad-request, validation, and unexpected error responses with SLF4J logging. |
| `src/main/java/com/lokeswarandk/db_backend/exception/ResourceNotFoundException.java` | Runtime exception | `ResourceNotFoundException`, `getError`, `forResourceWithId` | Domain not-found signal for services; maps to 404 via the global handler. |

### Persistence

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/repository/UserRepository.java` | Spring Data repository | `findDistinctMobileNosByPrefix`, `findByMobileNo`, inherited `CrudRepository` methods | CRUD plus mobile prefix and exact-mobile queries. |

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
  - `listUsers(String mobile)`: GET all users, or users matching `mobile` when the query param is present
  - `searchMobileByPrefix(String prefix, Integer limit)`: GET distinct mobile numbers for typeahead
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
  - `findByMobileNo(String mobileNo)`: exact mobile match; uses `StringUtils.requireNonBlank`
  - `searchMobileNosByPrefix(String prefix, Integer limit)`: distinct prefix search with default limit 5 and max 10
  - `update(Long id, User updatedUser)`: replace flow; returns empty when user does not exist
  - `deleteById(Long id)`: delete flow; returns false when user does not exist
- Constants: `MOBILE_PREFIX_MIN_LENGTH`, `MOBILE_SEARCH_DEFAULT_LIMIT`, `MOBILE_SEARCH_MAX_LIMIT`

### `StringUtils`

- Package: `com.lokeswarandk.db_backend.common`
- Class type: utility
- Public static methods:
  - `requireNonBlank(String value, String paramName)`: trims input; throws `IllegalArgumentException` with `{paramName} is required` when null or blank

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
- Logging: SLF4J `Logger` at WARN for client errors, DEBUG for validation, ERROR with stack trace for uncaught exceptions
- Public methods:
  - `handleResourceNotFoundException(ResourceNotFoundException ex)`
  - `handleIllegalArgumentException(IllegalArgumentException ex)`
  - `handleValidationException(MethodArgumentNotValidException ex)`
  - `handleGenericException(Exception ex)`
- Purpose: convert not-found, bad-request, validation, and unexpected exceptions into API responses; return a generic message for 500 responses

### `ResourceNotFoundException`

- Package: `com.lokeswarandk.db_backend.exception`
- Class type: unchecked domain exception
- Fields: `error` (response label), `message` (detail text, also the `RuntimeException` message)
- Public methods:
  - `ResourceNotFoundException(String error, String message)`
  - `getError()`
  - `forResourceWithId(String resourceName, Object id)`: factory for `{Resource} not found` / `No {resource} with id {id} exists`
- Purpose: let services signal missing entities without building HTTP responses directly

### `UserRepository`

- Package: `com.lokeswarandk.db_backend.repository`
- Class type: repository interface
- Annotation: `@Repository`
- Extends: `CrudRepository<User, Long>`
- Public methods:
  - `findDistinctMobileNosByPrefix(String prefix, int limit)`: custom `@Query` for `DISTINCT mobile_no` with `LIKE prefix%`
  - `findByMobileNo(String mobileNo)`: derived query for exact mobile match
  - inherited CRUD methods from `CrudRepository`

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

The repository includes a service class for user flows and mobile search/filter helpers. `ResourceNotFoundException` is available for not-found flows, but `UserController` still builds 404 responses inline. There are still no DTO classes or mapper classes. The controller still operates on entity objects at the API boundary for CRUD and mobile-filter responses; prefix search returns a `List<String>`.