# Conventions

## Naming

- Packages use lowercase reverse-domain style: `com.lokeswarandk.db_backend`.
- Classes use PascalCase: `UserController`, `GlobalExceptionHandler`, `ApiResponseBuilder`.
- Interfaces use noun-plus-role naming: `UserRepository`.
- Fields and methods use lowerCamelCase: `mobileNo`, `createdAt`, `handleGenericException`.
- Constants use upper snake case: `USER_NOT_FOUND`, `KEY_TIMESTAMP`.

## File Organization

- Controllers live under `controller`.
- Services live under `service`.
- Repositories live under `repository`.
- Shared helpers live under `common`.
- Error handlers live under `exception`.
- Domain entities and enums live under `model`.
- Tests live under `src/test/java` using the same package structure as main code.

There are no barrel files or re-export aggregators in committed history.

## Code Structure

- Imports are grouped loosely by standard library, project, and Spring/Jakarta imports, but there is no formatter or import-order tool committed in HEAD.
- Public methods appear before private helpers in the controller and utility classes.
- Utility classes use a private constructor and static methods.
- Controllers return `ResponseEntity<Object>` rather than custom wrapper DTOs.

## Async Patterns

Not found in committed history. The backend is entirely synchronous in its committed state.

## Comments and Documentation Style

- Comments are sparse and explanatory only.
- There are no Javadoc blocks in committed history.
- A few inline comments clarify framework requirements or local behavior, such as Spring Data JDBC object materialization and the local run script.
- No TODO or FIXME markers were found in HEAD.

## Formatting

- Indentation: 4 spaces in Java and shell scripts
- Quotes: double quotes in Java and shell scripts where string literals are needed
- Semicolons: used in Java, not applicable in shell
- Trailing commas: not used in Java
- Line length: no committed formatter config was found, so no explicit hard limit is documented in HEAD

## Validation and Error Style

- Input is validated with annotation-based Bean Validation at the controller boundary.
- Existence checks are performed explicitly before update and delete operations.
- Errors are returned as structured maps rather than as exceptions propagated to the client.

## Practical Rule of Thumb

To add new backend code in the same style, place HTTP handlers in `controller`, business orchestration in `service`, persistence interfaces in `repository`, and shared helpers such as response shaping or string validation in `common` rather than duplicating that logic in controllers or services.