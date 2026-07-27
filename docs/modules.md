# Modules

## Source Inventory

### Bootstrap

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/DbBackendApplication.java` | Boot application | `main(String[] args)` | Standard `@SpringBootApplication` entry point. |

### Security Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/config/SecurityConfig.java` | `@Configuration`, `@EnableWebSecurity` | `passwordEncoder`, `authenticationManager`, `securityContextRepository`, `securityContextLogoutHandler`, `securityFilterChain` | Defines the `SecurityFilterChain`: CSRF and form login disabled, `POST /api/auth/login` is `permitAll()`, all other requests authenticated, unauthenticated requests get an `HttpStatusEntryPoint(401)`. |
| `src/main/java/com/lokeswarandk/db_backend/security/DbUserDetailsService.java` | `@Service`, `UserDetailsService` | `loadUserByUsername` | Loads a `ControllerAccount` by username via `ControllerAccountRepository`; throws `UsernameNotFoundException` when missing. |

### Web Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/controller/UserController.java` | REST controller | `addUser`, `getUser`, `listUsers`, `searchMobileByPrefix`, `updateUser`, `deleteUser` | Thin HTTP layer; accepts `UpsertUserRequest`, returns DTOs or delete message payload. All endpoints require an authenticated session. |
| `src/main/java/com/lokeswarandk/db_backend/controller/AuthController.java` | REST controller | `login`, `logout`, `me` | `/api/auth`; authenticates via `AuthenticationManager`, persists the session via `SecurityContextRepository`, and logs out via `SecurityContextLogoutHandler`. |

### DTO Layer

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/dto/request/UpsertUserRequest.java` | Request DTO | getters/setters for user fields | Jakarta validation for create and update requests. |
| `src/main/java/com/lokeswarandk/db_backend/dto/response/UserResponse.java` | Response DTO | getters/setters for user fields plus `id` | Client-facing user shape for CRUD and mobile filter. |
| `src/main/java/com/lokeswarandk/db_backend/dto/response/MobilePrefixSearchResponse.java` | Response DTO | `mobileNos` | Wraps distinct mobile numbers from prefix search. |
| `src/main/java/com/lokeswarandk/db_backend/dto/request/LoginRequest.java` | Request DTO | `username`, `password` | Jakarta validation (`@NotBlank`) for `POST /api/auth/login`. |
| `src/main/java/com/lokeswarandk/db_backend/dto/response/CurrentUserResponse.java` | Response DTO | `username`, `role` | Returned by login and `GET /api/auth/me`; `role` has the `ROLE_` prefix stripped. |

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
| `src/main/java/com/lokeswarandk/db_backend/exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` | `handleResourceNotFoundException`, `handleIllegalArgumentException`, `handleAuthenticationException`, `handleValidationException`, `handleGenericException` | Centralizes not-found, bad-request, authentication, validation, and unexpected error responses with SLF4J logging. `handleAuthenticationException` returns a fixed `Invalid username or password` message regardless of cause. |
| `src/main/java/com/lokeswarandk/db_backend/exception/ResourceNotFoundException.java` | Runtime exception | `ResourceNotFoundException`, `getError`, `forResourceWithId` | Domain not-found signal for services; maps to 404 via the global handler. |

### Persistence

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/repository/UserRepository.java` | Spring Data repository | `findDistinctMobileNosByPrefix`, `findByMobileNo`, inherited `CrudRepository` methods | CRUD plus mobile prefix and exact-mobile queries. |
| `src/main/java/com/lokeswarandk/db_backend/repository/ControllerAccountRepository.java` | Spring Data repository | `findByUsername`, inherited `CrudRepository` methods | Used by `DbUserDetailsService` for authentication lookups. |

### Domain Model

| Path | Type | Public surface | Notes |
| --- | --- | --- | --- |
| `src/main/java/com/lokeswarandk/db_backend/model/User.java` | Entity | getters/setters for all fields | `@Table("user")`. |
| `src/main/java/com/lokeswarandk/db_backend/model/ControllerAccount.java` | Entity, `UserDetails` | getters/setters for all fields, `getAuthorities` | `@Table("controllers")`; implements `UserDetails` directly for Spring Security. |
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
| `src/test/java/com/lokeswarandk/db_backend/DbBackendApplicationTests.java` | Spring Boot test | `contextLoads()` | Startup smoke test. |
| `src/test/java/com/lokeswarandk/db_backend/controller/UserControllerTests.java` | Web slice test | CRUD and search endpoint tests | `@WebMvcTest` with mocked `UserService` and `DbUserDetailsService`; imports `SecurityConfig` and runs under `@WithMockUser`. |
| `src/test/java/com/lokeswarandk/db_backend/controller/AuthControllerTests.java` | Web slice test | login/logout/me endpoint tests | `@WebMvcTest(AuthController.class)` with mocked `AuthenticationManager`; imports `SecurityConfig`. |
| `src/test/java/com/lokeswarandk/db_backend/service/UserServiceTests.java` | Unit test | service method tests | Mockito-based; no Spring context. |
| `src/test/resources/application.yml` | Test config | H2 datasource settings | In-memory PostgreSQL-compatible test DB. |
| `api-testing/user.http` | REST Client file | N/A | Ad-hoc manual smoke collection with happy and error paths (not part of the automated test suite); logs in via `POST /api/auth/login` first and logs out last. Uses anonymized placeholder data. |
| `api-testing/auth.http` | REST Client file | N/A | Ad-hoc manual smoke collection for login, logout, and `me`, including invalid-credentials, blank-field, and no-session cases. |

## File-by-File Details

### `DbBackendApplication`

- Package: `com.lokeswarandk.db_backend`
- Class type: bootstrapping class
- Annotations: `@SpringBootApplication`
- Public method: `main(String[] args)` returns `void`
- Purpose: starts the Spring Boot application

### `SecurityConfig`

- Package: `com.lokeswarandk.db_backend.config`
- Class type: `@Configuration`, `@EnableWebSecurity`
- Public bean methods:
  - `passwordEncoder()`: `BCryptPasswordEncoder`
  - `authenticationManager(AuthenticationConfiguration configuration)`: exposes the default `AuthenticationManager` as a bean
  - `securityContextRepository()`: `HttpSessionSecurityContextRepository`
  - `securityContextLogoutHandler()`: `SecurityContextLogoutHandler`
  - `securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository)`: disables CSRF and form login, wires the session-based `SecurityContextRepository`, sets `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)` for unauthenticated requests, permits `POST /api/auth/login`, and requires authentication for every other request

### `DbUserDetailsService`

- Package: `com.lokeswarandk.db_backend.security`
- Class type: `@Service`, implements `UserDetailsService`
- Injected dependency: `ControllerAccountRepository`
- Public method: `loadUserByUsername(String username)` — returns the matching `ControllerAccount` or throws `UsernameNotFoundException`

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

### `AuthController`

- Package: `com.lokeswarandk.db_backend.controller`
- Class type: REST controller
- Annotation: `@RestController`, `@RequestMapping("/api/auth")`
- Injected dependencies: `AuthenticationManager`, `SecurityContextRepository`, `SecurityContextLogoutHandler`
- Public methods:
  - `login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)`: authenticates via `AuthenticationManager`, persists the `SecurityContext`, returns `CurrentUserResponse`
  - `logout(Authentication authentication, HttpServletRequest httpRequest, HttpServletResponse httpResponse)`: invalidates the session via `SecurityContextLogoutHandler`; returns a message payload
  - `me(Authentication authentication)`: returns `CurrentUserResponse` for the current session
- Private helper: `toCurrentUserResponse(Authentication)` builds the response and strips the `ROLE_` prefix from the first granted authority

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
- Fields: `id`, `name`, `mobileNo`, `addressLine`, `locality`, `state`, `country`, `pincode`

### `MobilePrefixSearchResponse`

- Package: `com.lokeswarandk.db_backend.dto.response`
- Fields: `mobileNos` (`List<String>`)

### `LoginRequest`

- Package: `com.lokeswarandk.db_backend.dto.request`
- Fields: `username`, `password`
- Constraints: `@NotBlank` on both fields (`"Username is required"`, `"Password is required"`)

### `CurrentUserResponse`

- Package: `com.lokeswarandk.db_backend.dto.response`
- Fields: `username`, `role`
- Constructors: no-arg (for JSON deserialization) and `(String username, String role)`

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

### `ControllerAccountRepository`

- Package: `com.lokeswarandk.db_backend.repository`
- Class type: repository interface
- Annotation: `@Repository`
- Extends: `CrudRepository<ControllerAccount, Long>`
- Public methods:
  - `findByUsername(String username)`: derived query returning `Optional<ControllerAccount>`, used by `DbUserDetailsService`
  - inherited CRUD methods from `CrudRepository`

### `User`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity
- Annotation: `@Table("user")`
- Fields: `id`, `name`, `mobileNo`, `addressLine`, `locality`, `state`, `country`, `pincode`, `createdAt`
- Constraints: none in the workspace; validation lives on `UpsertUserRequest`
- Public methods: standard getters and setters for each field

### `ControllerAccount`

- Package: `com.lokeswarandk.db_backend.model`
- Class type: entity, implements `UserDetails`
- Annotation: `@Table("controllers")`
- Fields: `id`, `username`, `password`, `role`, `enabled`, `accountNonExpired`, `accountNonLocked`, `credentialsNonExpired`, `createdAt`
- Constraints: none in the workspace; validation for login input lives on `LoginRequest`
- Public methods: standard getters and setters, plus `UserDetails` overrides (`getUsername`, `getPassword`, `isEnabled`, `isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`, `getAuthorities`)
- `getAuthorities()` returns a single `SimpleGrantedAuthority("ROLE_" + role)`

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