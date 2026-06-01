# Testing

## Test Stack

- Test runner: Maven Surefire through Spring Boot Starter Test
- Framework: JUnit 5
- Spring integration support: `@SpringBootTest`
- Mocking library: not used in committed test code
- Assertion library: not used directly in committed test code

## Test Inventory

### `src/test/java/com/lokeswarandk/db_backend/DbBackendApplicationTests.java`

- Type: startup smoke test
- Method: `contextLoads()`
- Purpose: verifies the Spring application context starts successfully

## API Smoke Artifacts

### `api-testing/user.http`

This committed REST Client file exercises manual HTTP calls against the user API.

Requests present in HEAD:

- `GET /api/users/{userId}`
- `GET /api/users`
- `POST /api/users`
- `DELETE /api/users/{userId}`

The file defines two client variables:

- `baseUrl`
- `userId`

It does not include a committed `PUT` example in HEAD, even though the controller supports update.

## Test Types Present

- Unit tests: not found
- Integration tests: context-load smoke test only
- End-to-end tests: not found
- Snapshot tests: not found
- Contract tests: not found

## Mocking and Fixtures

Not found in committed history. There are no mock factories, fixture files, or test data builders in HEAD.

## Test Conventions

- File naming: `*Tests.java` for Spring tests; `.http` for manual REST requests
- Structure: a single `contextLoads` method in the Spring test class
- Setup/teardown: not found in committed history
- Assertion style: not found in committed history

## Coverage Notes

The committed test surface is light. Most behavioral coverage currently lives in the controller implementation and in the manual HTTP request file rather than in automated assertions.