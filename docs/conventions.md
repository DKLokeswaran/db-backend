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

- Imports are ordered and cleaned by Spotless (`removeUnusedImports`) with Google Java Format (AOSP style).
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

- Editor baseline: `.editorconfig` sets UTF-8, LF line endings, final newline, and trailing-whitespace trimming for all files.
- Java, XML, YAML, properties, and shell: 4-space indentation (`indent_style = space`, `indent_size = 4`).
- Java formatting: Spotless Maven plugin applies Google Java Format (AOSP style) to `src/main/java` and `src/test/java`.
- YAML resources: Spotless trims trailing whitespace and enforces a final newline.
- Quotes: double quotes in Java and shell scripts where string literals are needed
- Semicolons: used in Java, not applicable in shell
- Trailing commas: not used in Java
- Enforcement: `./mvnw spotless:check` runs on the `verify` phase; use `./mvnw spotless:apply` to fix formatting locally before commit.

## Validation and Error Style

- Input is validated with annotation-based Bean Validation at the controller boundary.
- Missing resources should eventually be signaled with `ResourceNotFoundException` from the service layer; `GlobalExceptionHandler` maps it to a 404 envelope.
- Until controllers are thinned, `UserController` still returns 404 inline via `ApiResponseBuilder.error`.
- Service parameter validation throws `IllegalArgumentException`; the global handler maps it to `400 Bad Request`.
- Unexpected exceptions are logged at ERROR with stack traces; clients receive a generic `An unexpected error occurred` message.
- Errors are returned as structured maps (`ApiResponseBuilder` envelopes), not raw exception types.

## Practical Rule of Thumb

To add new backend code in the same style, place HTTP handlers in `controller`, business orchestration in `service`, persistence interfaces in `repository`, and shared helpers such as response shaping or string validation in `common` rather than duplicating that logic in controllers or services.