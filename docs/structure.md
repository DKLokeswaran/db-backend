# Structure

## Annotated Tree

```text
db-backend/
  .editorconfig            Editor indentation and charset baseline
  .cursorignore            Cursor IDE indexing exclusions
  pom.xml                  Maven build and dependency manifest
  mvnw, mvnw.cmd           Maven Wrapper launchers
  scripts/run-local.sh     Local boot script that loads .env and starts Spring Boot
  api-testing/user.http    REST Client smoke requests for the user API
  src/main/java/com/lokeswarandk/db_backend/
    DbBackendApplication.java      Boot entry point
    common/ApiResponseBuilder.java  Shared response-map helper
    common/StringUtils.java         Shared string validation helper
    controller/UserController.java  User CRUD endpoints
    service/UserService.java        User CRUD service orchestration
    exception/GlobalExceptionHandler.java  Global validation and error advice
    exception/ResourceNotFoundException.java  Domain not-found runtime exception
    model/                          Domain entities and enums
    repository/UserRepository.java  Spring Data repository
  src/main/resources/application.yml  Datasource and server configuration
  src/test/java/com/lokeswarandk/db_backend/
    DbBackendApplicationTests.java   Spring context smoke test
```

## Folder Roles

- `src/main/java`: production code
- `src/main/resources`: application configuration
- `src/test/java`: automated tests
- `api-testing`: manual HTTP request artifacts
- `scripts`: local run helpers

## Where to Add New Code

- New controllers: `src/main/java/com/lokeswarandk/db_backend/controller`
- New services: `src/main/java/com/lokeswarandk/db_backend/service`
- New repositories: `src/main/java/com/lokeswarandk/db_backend/repository`
- Shared helpers: `src/main/java/com/lokeswarandk/db_backend/common`
- Error advice: `src/main/java/com/lokeswarandk/db_backend/exception`
- New domain models: `src/main/java/com/lokeswarandk/db_backend/model`
- New tests: mirror the main package structure under `src/test/java`

## Observed Scope

This repository is intentionally small in HEAD. There is no `docs` folder in committed history before this generation, no Docker configuration, and no CI/CD workflow files.