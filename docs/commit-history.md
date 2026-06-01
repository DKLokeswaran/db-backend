# Commit History

## Statistics

| Metric | Value |
| --- | --- |
| Total commits | 8 |
| First commit date | 2026-05-12 |
| Latest commit date | 2026-05-31 |
| Active contributors | 1 |
| Most active contributor | Lokeswaran DK |

## Monthly Velocity

| Month | Commits |
| --- | --- |
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
| `src/main/java/com/lokeswarandk/db_backend/repository/UserRepository.java` | 1 |
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

## Breaking Changes

No commit message explicitly marks a breaking change. The switch from Bruno files to `api-testing/user.http` is a tooling change rather than an API contract change.

## Full Commit Log

| Hash | Author | Date | Message |
| --- | --- | --- | --- |
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