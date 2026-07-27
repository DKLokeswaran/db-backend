# Build and Deploy

## Available Commands

### Maven Wrapper

- `./mvnw spring-boot:run`: start the backend locally
- `./mvnw test`: run the Spring Boot test suite
- `./mvnw spotless:check`: verify Java and YAML formatting (also runs automatically on `verify`)
- `./mvnw spotless:apply`: apply Spotless formatting fixes locally

### Local Run Script

- `./scripts/run-local.sh`: source `.env`, export database variables, and start the app through Maven

## Build Pipeline

The committed build is Maven-based:

1. Maven Wrapper resolves the local Maven distribution.
2. Spring Boot parent manages dependency versions.
3. The application compiles as Java 17.
4. Spotless Maven Plugin checks Java (Google Java Format, AOSP) and YAML formatting on `verify`.
5. Spring Boot Maven Plugin packages or runs the app.
6. `spring-boot:run` starts the application without a separate container layer.

## Runtime Configuration

`src/main/resources/application.yml` sets:

- application name: `db-backend`
- server port: `8084`
- datasource URL, username, and password from environment variables
- JDBC logging levels for development visibility
- `org.springframework.security` logging at `TRACE`, which surfaces filter-chain and authentication decisions during local development

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
- The API test files use a fixed `baseUrl` pointing at a local network address, so they are environment-specific and not production-safe as committed.
- `api-testing/user.http` and `api-testing/auth.http` read `API_USERNAME` and `API_PASSWORD` through the REST Client `$dotenv` variable syntax to log in before calling protected endpoints. These are client-side variables consumed by the REST Client extension only — they are not Spring configuration properties and have no effect on the running application.