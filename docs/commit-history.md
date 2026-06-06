# Commit History

## Statistics

| Metric | Value |
| --- | --- |
| Total commits | 14 |
| First commit date | 2026-05-12 |
| Latest commit date | 2026-06-05 |
| Active contributors | 1 |
| Most active contributor | Lokeswaran DK |

## Monthly Velocity

| Month | Commits |
| --- | --- |
| 2026-06 | 6 |
| 2026-05 | 8 |

## Hotspots

Top changed files by commit frequency in HEAD history:

| File | Touch count |
| --- | --- |
| `src/main/resources/application.yml` | 3 |
| `src/main/java/com/lokeswarandk/db_backend/DbBackendApplication.java` | 3 |
| `pom.xml` | 3 |
| `src/main/resources/application.properties` | 2 |
| `api-testing/opencollection.yml` | 2 |
| `api-testing/User/folder.yml` | 2 |
| `api-testing/User/ListUsers.yml` | 2 |
| `api-testing/User/GetUser.yml` | 2 |
| `api-testing/User/DeleteUser.yml` | 2 |
| `api-testing/User/AddUser.yml` | 2 |
| `.gitignore` | 2 |
| `src/test/java/com/lokeswarandk/db_backend/DbBackendApplicationTests.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/repository/UserRepository.java` | 2 |
| `src/main/java/com/lokeswarandk/db_backend/service/UserService.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/controller/UserController.java` | 2 |
| `src/main/java/com/lokeswarandk/db_backend/model/User.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/TransactionItem.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/Transaction.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/Receipt.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/PricingType.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/PaymentMode.java` | 1 |
| `src/main/java/com/lokeswarandk/db_backend/model/OfferingType.java` | 1 |

## Feature Timeline

### 2026-05-12: project bootstrap and domain model foundation

- Initial Maven/Spring Boot project setup.
- Added JDBC dependency and core model classes for event, offering, receipt, transaction, and user concepts.

### 2026-05-12: config format change

- Converted application configuration from `.properties` to `.yml`.

### 2026-05-21: PostgreSQL connection and user CRUD endpoints

- Added PostgreSQL dependency and connected the app to a database.
- Introduced `UserController`, `UserRepository`, `ApiResponseBuilder`, and `GlobalExceptionHandler`.

### 2026-05-21: API testing support evolution

- Added Bruno-based endpoint testing files.

### 2026-05-31: local secret handling and run script

- Moved database secrets to environment variables.
- Added `scripts/run-local.sh` for local execution.
- Replaced the Bruno test files with a single `.http` REST Client file.

### 2026-06-01: docs baseline added

- Added project documentation set under `docs/`.

### 2026-06-01: user service layer extraction

- Introduced `UserService` and refactored `UserController` to delegate CRUD orchestration to the service layer.

### 2026-06-01: mobile search and filter APIs

- Added `GET /api/users/search/mobile` for distinct mobile-number prefix typeahead.
- Added `GET /api/users?mobile={mobileNo}` for exact-mobile user lookup.
- Introduced `StringUtils.requireNonBlank` and extended `UserRepository` with prefix and exact-mobile queries.

### 2026-06-05: Cursor IDE indexing exclusions

- Added `.cursorignore` to exclude build artifacts and local secrets from Cursor indexing.

### 2026-06-05: formatting enforcement baseline

- Added `.editorconfig` and Spotless Maven plugin (Google Java Format, AOSP style).
- Normalized Java and YAML formatting across the codebase; bound `spotless:check` to the `verify` phase.

### 2026-06-05: centralized exception handling improvements

- Added `ResourceNotFoundException` and registered it in `GlobalExceptionHandler`.
- Added SLF4J logging for handled exceptions and a generic client message for 500 responses.

## Breaking Changes

No commit message explicitly marks a breaking change. The switch from Bruno files to `api-testing/user.http` is a tooling change rather than an API contract change.

## Full Commit Log

| Hash | Author | Date | Message |
| --- | --- | --- | --- |
| `184af90` | Lokeswaran DK | 2026-06-05 | added logging for exceptions, added genric error message for 500 error code added seperate class for 404 errors |
| `fe80a08` | Lokeswaran DK | 2026-06-05 | aadded maven spotless plugin,.editorconfig, ran spotless apply and added plan for codebase standardisation |
| `146cc63` | Lokeswaran DK | 2026-06-05 | added .cursorignore file |
| `00f8842` | Lokeswaran DK | 2026-06-01 | added mobile no prefix api ans list users by mobile param |
| `0ac3c4c` | Lokeswaran DK | 2026-06-01 | seperated controller and service logic for users |
| `01c1674` | Lokeswaran DK | 2026-06-01 | docs init |
| `2419634` | Lokeswaran DK | 2026-05-31 | moved secrets to env file and added a script for local execution |
| `d7cd238` | Lokeswaran DK | 2026-05-31 | added http files for api testing and removed bruno files |
| `91d2a3c` | Lokeswaran DK | 2026-05-21 | added bruno endpoint testing |
| `37f01c0` | Lokeswaran DK | 2026-05-21 | added user endpoints |
| `c9fd405` | Lokeswaran DK | 2026-05-21 | added and connected postgresql db |
| `e261c4e` | Lokeswaran DK | 2026-05-12 | changed from properties file to yaml file |
| `0739ce7` | Lokeswaran DK | 2026-05-12 | Added JDBC dependency and Model classes |
| `e091728` | Lokeswaran DK | 2026-05-12 | Project init |

## Release and Tag Notes

No tags or releases were found in committed history.

Last Synced Commit: `184af905cc37b1d66c6e3ca8ba106f7ff245f3d6`