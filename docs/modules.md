# Modules

## Source Inventory

### Bootstrap

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/DbBackendApplication.java` | Boot application | `main(String[] args)` | Standard `@SpringBootApplication` entry point. |

### Web Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/controller/UserController.java` | REST controller | `addUser`, `getUser`, `listUsers`, `searchMobileByPrefix`, `updateUser`, `deleteUser` | Thin HTTP layer; accepts `UpsertUserRequest`, returns DTOs or delete message payload. |

### DTO Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/dto/request/UpsertUserRequest.java` | Request DTO | getters/setters for user fields | Jakarta validation for create and update requests. |
| `src/main/java/com/lokeswarandk/db_backend/dto/response/UserResponse.java` | Response DTO | getters/setters for user fields plus `id`, `createdAt` | Client-facing user shape for CRUD and mobile filter. |
| `src/main/java/com/lokeswarandk/db_backend/dto/response/MobilePrefixSearchResponse.java` | Response DTO | `mobileNos` | Wraps distinct mobile numbers from prefix search. |

### Mapping Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/mapper/UserMapper.java` | Utility mapper | `toEntity`, `applyFields`, `toResponse`, `toResponseList` | Manual DTO/entity conversion for the User module. |

### Service Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/service/UserService.java` | Spring service | `create`, `findById`, `findAll`, `findByMobileNo`, `searchMobileNosByPrefix`, `update`, `deleteById` | Returns DTOs; throws `ResourceNotFoundException` for missing users. |

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
  - `addUser(UpsertUserRequest request)`: POST create; returns `UserResponse`
  - `getUser(Long id)`: GET by id; service throws `ResourceNotFoundException` when missing
  - `listUsers(String mobile)`: GET all users as `UserResponse` list, or filter by `mobile` when present
  - `searchMobileByPrefix(String prefix, Integer limit)`: GET `MobilePrefixSearchResponse`
  - `updateUser(Long id, UpsertUserRequest request)`: PUT update; returns `UserResponse`
  - `deleteUser(Long id)`: DELETE; service throws `ResourceNotFoundException` when missing

### `UserService`

- Package: `com.lokeswarandk.db_backend.service`
- Class type: Spring `@Service`
- Injected dependency: `UserRepository`
- Public methods:
  - `create(UpsertUserRequest request)`: maps to entity, clears id, fills `createdAt` when absent; returns `UserResponse`
  - `findById(Long id)`: returns `UserResponse` or throws `ResourceNotFoundException`
  - `findAll()`: returns `List<UserResponse>`
  - `findByMobileNo(String mobileNo)`: exact mobile match; uses `StringUtils.requireNonBlank`
  - `searchMobileNosByPrefix(String prefix, Integer limit)`: returns `MobilePrefixSearchResponse`
  - `update(Long id, UpsertUserRequest request)`: applies fields via `UserMapper`; throws when user missing
  - `deleteById(Long id)`: throws `ResourceNotFoundException` when user missing
- Constants: `MOBILE_PREFIX_MIN_LENGTH`, `MOBILE_SEARCH_DEFAULT_LIMIT`, `MOBILE_SEARCH_MAX_LIMIT`

### `UserMapper`

- Package: `com.lokeswarandk.db_backend.mapper`
- Class type: utility mapper (`final`, private constructor)
- Public static methods:
  - `toEntity(UpsertUserRequest request)`
  - `applyFields(User user, UpsertUserRequest request)`
  - `toResponse(User user)`
  - `toResponseList(List<User> users)`

### `UpsertUserRequest`

- Package: `com.lokeswarandk.db_backend.dto.request`
- Fields: `name`, `mobileNo`, `addressLine`, `locality`, `state`, `country`, `pincode`
- Constraints: `@NotBlank` on all fields

### `UserResponse`

- Package: `com.lokeswarandk.db_backend.dto.response`
- Fields: `id`, `name`, `mobileNo`, `addressLine`, `locality`, `state`, `country`, `pincode`, `createdAt`

### `MobilePrefixSearchResponse`

- Package: `com.lokeswarandk.db_backend.dto.response`
- Fields: `mobileNos` (`List<String>`)

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
- Constraints: none in the workspace; validation lives on `UpsertUserRequest`
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

The User module uses request/response DTOs and `UserMapper` at the API boundary. `UserService` throws `ResourceNotFoundException` for missing users and returns `UserResponse` or `MobilePrefixSearchResponse` objects. Other domain modules still expose entities only through persistence layers.