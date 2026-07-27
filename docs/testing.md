# Testing

## Test Stack

- Test runner: Maven Surefire through Spring Boot Starter Test
- Framework: JUnit 5
- Spring integration support: `@SpringBootTest`, `@WebMvcTest`
- Web layer testing: `spring-boot-starter-webmvc-test` (MockMvc)
- Security testing: `spring-security-test` (`SecurityMockMvcConfigurers.springSecurity()`, `@WithMockUser`)
- Mocking library: Mockito (`@Mock`, `@InjectMocks`, `@MockitoBean`)
- Assertion library: AssertJ (service tests), MockMvc `jsonPath` (controller tests)

## Security Test Setup

Controller slice tests that exercise secured endpoints import `SecurityConfig` alongside `GlobalExceptionHandler` and build `MockMvc` with the Spring Security test support instead of the plain `@Autowired MockMvc`:

```java
@Autowired private WebApplicationContext context;

private MockMvc mockMvc;

@BeforeEach
void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
}
```

- `UserControllerTests` additionally mocks `DbUserDetailsService` (so `SecurityConfig`'s `AuthenticationManager` bean can be created in the slice context) and annotates the class with `@WithMockUser` so every test runs as an authenticated principal.
- `AuthControllerTests` mocks `AuthenticationManager` directly and uses per-test `@WithMockUser` only on `logout` and `me` tests that require an existing session; `login` tests exercise the real authentication flow through the mocked manager.

## Test Configuration

### `src/test/resources/application.yml`

In-memory H2 datasource for tests:

- URL: `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
- Username: `sa`
- Password: empty

Production uses PostgreSQL via environment variables; tests do not require a live database for the current suite.

## Test Inventory

### `src/test/java/com/lokeswarandk/db_backend/DbBackendApplicationTests.java`

- Type: startup smoke test
- Annotation: `@SpringBootTest`
- Method: `contextLoads()`
- Purpose: verifies the Spring application context starts successfully

### `src/test/java/com/lokeswarandk/db_backend/controller/UserControllerTests.java`

- Type: web slice test
- Annotations: `@WebMvcTest(UserController.class)`, `@Import({GlobalExceptionHandler.class, SecurityConfig.class})`, class-level `@WithMockUser`
- Mocking: `@MockitoBean UserService`, `@MockitoBean DbUserDetailsService`
- Setup: `MockMvc` is built with `MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())` in a `@BeforeEach` method
- Coverage:
  - `POST /api/users` — `201` with `UserResponse`; `400` on validation failure
  - `GET /api/users/{id}` — `200` with `UserResponse`; `404` via `ResourceNotFoundException`
  - `GET /api/users` — list all; filter by `mobile` query param
  - `PUT /api/users/{id}` — `200` with `UserResponse`
  - `DELETE /api/users/{id}` — `200` message payload
  - `GET /api/users/search/mobile` — `MobilePrefixSearchResponse`; `400` on invalid prefix

### `src/test/java/com/lokeswarandk/db_backend/controller/AuthControllerTests.java`

- Type: web slice test
- Annotations: `@WebMvcTest(AuthController.class)`, `@Import({GlobalExceptionHandler.class, SecurityConfig.class})`
- Mocking: `@MockitoBean AuthenticationManager`
- Setup: same `springSecurity()`-backed `MockMvc` construction as `UserControllerTests`
- Coverage:
  - `POST /api/auth/login` — `200` with `CurrentUserResponse` (`username`, `role`) on valid credentials; `401` with the fixed `Invalid username or password` envelope on `BadCredentialsException`; `400` with `Validation failed` on blank fields
  - `POST /api/auth/logout` — `200` with `Logout successful` message (as `@WithMockUser`)
  - `GET /api/auth/me` — `200` with `CurrentUserResponse` (as `@WithMockUser(username = "admin", roles = {"ADMIN"})`); `401` when unauthenticated

### `src/test/java/com/lokeswarandk/db_backend/service/UserServiceTests.java`

- Type: unit test
- Annotation: `@ExtendWith(MockitoExtension.class)`
- Mocking: `@Mock UserRepository`, `@InjectMocks UserService`
- Coverage:
  - `create` — clears id, sets `createdAt`, maps to `UserResponse`
  - `findById` — returns DTO or throws `ResourceNotFoundException`
  - `findByMobileNo` — throws on blank mobile
  - `searchMobileNosByPrefix` — wraps results in `MobilePrefixSearchResponse`; validates prefix length and limit
  - `update` — applies fields via mapper; throws when missing
  - `deleteById` — deletes when exists; throws when missing

## API Smoke Artifacts

### `api-testing/user.http`

Ad-hoc REST Client smoke file for the user API — **not part of the automated test suite**. Use during development to manually verify endpoints; run `./mvnw test` for CI and local automated coverage.

Defines `baseUrl` (`http://localhost:8084`), `username`/`password` (via `{{$dotenv API_USERNAME}}` / `{{$dotenv API_PASSWORD}}`), `userId`, `mobileNo`, and `mobilePrefix`. Request bodies use anonymized placeholder data only.

Requests present in the workspace:

- `POST /api/auth/login` — logs in first so the session cookie authenticates every request below (full auth coverage lives in `auth.http`)
- `GET /api/users/{userId}` — fetch one `UserResponse`
- `GET /api/users/999999` — expect `404`
- `GET /api/users` — list all users
- `GET /api/users?mobile={mobileNo}` — filter by mobile
- `GET /api/users?mobile=` — expect `400` for blank mobile
- `GET /api/users/search/mobile?prefix={mobilePrefix}` — `MobilePrefixSearchResponse` (optional `limit`)
- `GET /api/users/search/mobile?prefix=9` — expect `400` for short prefix
- `GET /api/users/search/mobile` — expect `400` for missing prefix
- `GET /api/users?mobile=0000000000` — expect `200` with empty array
- `POST /api/users` — create with `UpsertUserRequest` body
- `POST /api/users` with empty name — expect `400`
- `PUT /api/users/{userId}` — update user
- `PUT /api/users/999999` — expect `404`
- `DELETE /api/users/{userId}` — delete success
- `DELETE /api/users/999999` — expect `404`
- `POST /api/auth/logout` — logs out last to end the authenticated session

### `api-testing/auth.http`

Ad-hoc REST Client smoke file dedicated to the auth API — **not part of the automated test suite**. Defines `baseUrl` (`http://localhost:8084`) and `username`/`password` via dotenv variables.

Requests present in the workspace:

- `POST /api/auth/login` — establishes the session cookie for the REST Client cookie jar
- `GET /api/users` (before login, or with `@no-cookie-jar`) — expect `401` for a protected endpoint without a session
- `POST /api/auth/login` with `@no-cookie-jar` — verify credentials without saving the session
- `POST /api/auth/login` with wrong password — expect `401`
- `POST /api/auth/login` with blank fields — expect `400` validation error
- `GET /api/auth/me` — expect `200` with `username` and `role` while logged in
- `GET /api/auth/me` with `@no-cookie-jar` — expect `401` without a session
- `POST /api/auth/logout` — expect `200` while logged in
- `POST /api/auth/logout` with `@no-cookie-jar` — expect `401` without a session

## Test Types Present

- Unit tests: `UserServiceTests` (mocked repository)
- Web slice tests: `UserControllerTests`, `AuthControllerTests` (`@WebMvcTest` + secured MockMvc)
- Integration tests: `DbBackendApplicationTests` context-load smoke test only
- End-to-end tests: not found
- Snapshot tests: not found
- Contract tests: not found

## Mocking and Fixtures

- Controller tests mock `UserService` or `AuthenticationManager` (plus `DbUserDetailsService` where needed) and import `GlobalExceptionHandler` and `SecurityConfig` for error envelope and security-filter assertions.
- Service tests mock `UserRepository` with local helper methods (`validRequest`, `sampleUser`).
- No shared fixture files or test data builders beyond inline helpers in test classes.

## Test Conventions

- File naming: `{Entity}ControllerTests.java`, `{Entity}ServiceTests.java`; `api-testing/{resource}.http` for optional manual REST Client smoke requests
- Controller tests: `@WebMvcTest` with `@MockitoBean` for service dependencies; tests on secured endpoints also import `SecurityConfig` and build `MockMvc` with `springSecurity()`
- Service tests: plain JUnit 5 + Mockito extension, no Spring context
- Per-resource minimum: one controller slice test class and one service unit test class (User module establishes the pattern)

## Coverage Notes

The User module has automated controller and service coverage plus expanded manual HTTP examples. Other domain modules still have no automated tests. Run the full suite with `./mvnw test`; use `./mvnw verify` to include Spotless checks.
