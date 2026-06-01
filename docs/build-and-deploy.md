# Build and Deploy

## Available Commands

### Maven Wrapper

- `./mvnw spring-boot:run`: start the backend locally
- `./mvnw test`: run the Spring Boot test suite

### Local Run Script

- `./scripts/run-local.sh`: source `.env`, export database variables, and start the app through Maven

## Build Pipeline

The committed build is Maven-based:

1. Maven Wrapper resolves the local Maven distribution.
2. Spring Boot parent manages dependency versions.
3. The application compiles as Java 17.
4. Spring Boot Maven Plugin packages or runs the app.
5. `spring-boot:run` starts the application without a separate container layer.

## Runtime Configuration

`src/main/resources/application.yml` sets:

- application name: `db-backend`
- server port: `8081`
- datasource URL, username, and password from environment variables
- JDBC logging levels for development visibility

## Environment Variable Catalog

| Key | Where used | Purpose | Default | Required |
| --- | --- | --- | --- | --- |
| `DB_DATASOURCE_URL` | `application.yml` | JDBC connection URL | none | yes |
| `DB_DATASOURCE_USERNAME` | `application.yml` | database username | none | yes |
| `DB_DATASOURCE_PASSWORD` | `application.yml` | database password | none | yes |
| `HOME` | `mvnw` | Maven wrapper default home directory | system home | implicit |
| `MAVEN_USER_HOME` | `mvnw` | override wrapper download location | `${HOME}/.m2` | optional |

## Containerization

Not found in committed history.

## CI/CD

Not found in committed history.

## Deployment

No deploy script, release workflow, Docker image, or cloud manifest is committed in HEAD. The only operational entry point is the local Maven run path.

## Operational Notes

- The local script fails fast if `.env` is missing.
- The `.env` file is gitignored in committed history.
- The API test file uses a fixed `baseUrl` pointing at a local network address, so it is environment-specific and not production-safe as committed.