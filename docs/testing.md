# Testing

## Test Stack

- Test runner: Maven Surefire through Spring Boot Starter Test
- Framework: JUnit 5
- Spring integration support: `@SpringBootTest`, `@WebMvcTest`
- Web layer testing: `spring-boot-starter-webmvc-test` (MockMvc)
- Mocking library: Mockito (`@Mock`, `@InjectMocks`, `@MockitoBean`)
- Assertion library: AssertJ (service tests), MockMvc `jsonPath` (controller tests)

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
- Annotations: `@WebMvcTest(UserController.class)`, `@Import(GlobalExceptionHandler.class)`
- Mocking: `@MockitoBean UserService`
- Coverage:
  - `POST /api/users` — `201` with `UserResponse`; `400` on validation failure
  - `GET /api/users/{id}` — `200` with `UserResponse`; `404` via `ResourceNotFoundException`
  - `GET /api/users` — list all; filter by `mobile` query param
  - `PUT /api/users/{id}` — `200` with `UserResponse`
  - `DELETE /api/users/{id}` — `200` message payload
  - `GET /api/users/search/mobile` — `MobilePrefixSearchResponse`; `400` on invalid prefix

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

Defines `baseUrl`, `userId`, `mobileNo`, and `mobilePrefix`. Request bodies use anonymized placeholder data only.

Requests present in the workspace:

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

## Test Types Present

- Unit tests: `UserServiceTests` (mocked repository)
- Web slice tests: `UserControllerTests` (`@WebMvcTest` + MockMvc)
- Integration tests: `DbBackendApplicationTests` context-load smoke test only
- End-to-end tests: not found
- Snapshot tests: not found
- Contract tests: not found

## Mocking and Fixtures

- Controller tests mock `UserService` and import `GlobalExceptionHandler` for error envelope assertions.
- Service tests mock `UserRepository` with local helper methods (`validRequest`, `sampleUser`).
- No shared fixture files or test data builders beyond inline helpers in test classes.

## Test Conventions

- File naming: `{Entity}ControllerTests.java`, `{Entity}ServiceTests.java`; `api-testing/{resource}.http` for optional manual REST Client smoke requests
- Controller tests: `@WebMvcTest` with `@MockitoBean` for service dependencies
- Service tests: plain JUnit 5 + Mockito extension, no Spring context
- Per-resource minimum: one controller slice test class and one service unit test class (User module establishes the pattern)

## Coverage Notes

The User module has automated controller and service coverage plus expanded manual HTTP examples. Other domain modules still have no automated tests. Run the full suite with `./mvnw test`; use `./mvnw verify` to include Spotless checks.
