# Security

## Authentication

Not found in committed history. There is no JWT, session, OAuth2, API key, or basic-auth implementation in HEAD.

## Authorization

Not found in committed history. There are no roles, permissions, guards, or method-security annotations.

## CORS and Security Headers

Not found in committed history. There is no explicit CORS configuration or header-hardening configuration.

## Input Sanitization

The code relies on Bean Validation constraints for request-shape validation. There is no dedicated sanitization layer or HTML escaping utility in committed history.

## Secret Handling

Secrets are externalized through environment variables in `application.yml` and loaded from a local `.env` file by `scripts/run-local.sh`.

Referenced secret keys:

- `DB_DATASOURCE_URL`
- `DB_DATASOURCE_USERNAME`
- `DB_DATASOURCE_PASSWORD`

The `.env` file is ignored by git in committed history.

## Passwords

Not found in committed history. No password hashing algorithm is used because the backend does not manage credentials.

## Injection and Query Safety

The committed repository uses Spring Data JDBC repositories rather than handwritten SQL. No raw SQL string concatenation is present in HEAD, which reduces SQL injection risk for the implemented data access path.

## CSRF, Rate Limiting, and Similar Controls

Not found in committed history.

## Security-Sensitive Dependencies

- `org.postgresql:postgresql` for database connectivity
- `org.springframework.boot:spring-boot-starter-web` for HTTP handling
- `org.springframework.boot:spring-boot-starter-validation` for request validation

## Operational Note

JDBC logging is configured at debug/trace levels in `application.yml`, which is useful for development but can be noisy in production if copied unchanged.