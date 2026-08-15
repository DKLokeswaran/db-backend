# Structure

## Annotated Tree

```text
db-backend/
  .editorconfig            Editor indentation and charset baseline
  .cursorignore            Cursor IDE indexing exclusions
  pom.xml                  Maven build and dependency manifest
  mvnw, mvnw.cmd           Maven Wrapper launchers
  scripts/run-local.sh     Local boot script that loads .env and starts Spring Boot
  scripts/check.sh         Quiet Maven wrapper (compile, test, spotless, verify); one-line output
  .cursor/rules/           Cursor IDE rules (.mdc convention and workflow files)
  .cursor/plans/           Cursor implementation plans (.plan.md)
  api-testing/user.http    Ad-hoc REST Client smoke requests (not part of the automated test suite)
  api-testing/auth.http    Ad-hoc REST Client smoke requests for CSRF, login/logout/me
  src/main/java/com/lokeswarandk/db_backend/
    DbBackendApplication.java      Boot entry point
    common/ApiResponseBuilder.java  Shared response-map helper
    common/StringUtils.java         Shared string validation helper
    config/SecurityConfig.java      Spring Security filter chain, CSRF, and auth beans
    controller/UserController.java  User CRUD endpoints
    controller/AuthController.java  CSRF bootstrap, login, logout, and current-user endpoints
    service/UserService.java        User CRUD service orchestration
    dto/request/UpsertUserRequest.java  User create/update request DTO
    dto/request/LoginRequest.java       Login request DTO
    dto/response/UserResponse.java      User API response DTO
    dto/response/MobilePrefixSearchResponse.java  Mobile typeahead response DTO
    dto/response/CurrentUserResponse.java         Authenticated user response DTO
    mapper/UserMapper.java          User entity/DTO mapping
    exception/GlobalExceptionHandler.java  Global validation and error advice
    exception/ResourceNotFoundException.java  Domain not-found runtime exception
    model/                          Domain entities and enums, including ControllerAccount
    repository/UserRepository.java  Spring Data repository
    repository/ControllerAccountRepository.java  Controller account lookup repository
    security/DbUserDetailsService.java  Loads ControllerAccount for authentication
  src/main/resources/application.yml  Datasource, server session cookie, and security logging configuration
  src/test/java/com/lokeswarandk/db_backend/
    DbBackendApplicationTests.java   Spring context smoke test
    controller/UserControllerTests.java  User API web slice tests (secured, CSRF on mutations)
    controller/AuthControllerTests.java  Auth API web slice tests (CSRF bootstrap and validation)
    service/UserServiceTests.java    User service unit tests
  src/test/resources/application.yml  H2 in-memory test datasource config
```

## Folder Roles

- `src/main/java`: production code
- `src/main/resources`: application configuration
- `src/test/java`: automated tests
- `src/test/resources`: test configuration (H2 datasource)
- `api-testing`: ad-hoc REST Client smoke files (not part of the automated test suite)
- `scripts`: local run helpers (`run-local.sh`) and quiet Maven verification (`check.sh` — wraps `mvnw` with bounded output for compile, targeted test, spotless, and verify)
- `.cursor/rules`: Cursor IDE rules (`.mdc` files for project conventions and verification workflow)
- `.cursor/plans`: Cursor implementation plans (`.plan.md` files; not part of the application runtime)

## Where to Add New Code

- New controllers: `src/main/java/com/lokeswarandk/db_backend/controller`
- New services: `src/main/java/com/lokeswarandk/db_backend/service`
- New repositories: `src/main/java/com/lokeswarandk/db_backend/repository`
- Request DTOs: `src/main/java/com/lokeswarandk/db_backend/dto/request`
- Response DTOs: `src/main/java/com/lokeswarandk/db_backend/dto/response`
- Entity mappers: `src/main/java/com/lokeswarandk/db_backend/mapper`
- Shared helpers: `src/main/java/com/lokeswarandk/db_backend/common`
- Error advice: `src/main/java/com/lokeswarandk/db_backend/exception`
- New domain models: `src/main/java/com/lokeswarandk/db_backend/model`
- New tests: mirror the main package structure under `src/test/java`

## Observed Scope

This repository is intentionally small in HEAD. There is no `docs` folder in committed history before this generation, no Docker configuration, and no CI/CD workflow files.