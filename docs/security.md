# Security

## Authentication

Authentication is session-cookie based, backed by Spring Security (`spring-boot-starter-security`).

- `SecurityConfig` (`@EnableWebSecurity`) defines the `SecurityFilterChain`. `POST /api/auth/login` and `GET /api/auth/csrf` are `permitAll()`; every other request must be authenticated.
- `AuthController` (`/api/auth`) exposes `GET /csrf`, `POST /login`, `POST /logout`, and `GET /me`.
- `GET /api/auth/csrf` materializes a CSRF token and writes the readable `XSRF-TOKEN` cookie via `CookieCsrfTokenRepository.saveToken` (`204 No Content`).
- `POST /api/auth/login` authenticates a `UsernamePasswordAuthenticationToken` through the `AuthenticationManager`, stores the resulting `Authentication` in a new `SecurityContext`, and persists it via `SecurityContextRepository` (`HttpSessionSecurityContextRepository`). The container sets the `JSESSIONID` cookie on the response. Login is a state-changing request and requires a valid CSRF header.
- `POST /api/auth/logout` invalidates the session through `SecurityContextLogoutHandler`, then clears the CSRF cookie with `cookieCsrfTokenRepository.saveToken(null, ...)`.
- `GET /api/auth/me` returns the current `CurrentUserResponse` (`username`, `role`) for an authenticated session.
- `DbUserDetailsService` (`UserDetailsService`) loads credentials by username via `ControllerAccountRepository.findByUsername`, throwing `UsernameNotFoundException` when no account matches.
- `ControllerAccount` (`@Table("controllers")`) implements `UserDetails` directly — its `enabled`, `accountNonExpired`, `accountNonLocked`, and `credentialsNonExpired` flags come straight from the `controllers` table row.
- Unauthenticated requests to protected endpoints are rejected by an `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)`, which returns a bare `401` (often without a JSON body, since it runs before `GlobalExceptionHandler` in the filter chain). `AuthenticationException` thrown inside a controller method (for example, bad login credentials) is instead caught by `GlobalExceptionHandler.handleAuthenticationException` and returns the standard JSON error envelope.
- `formLogin` is explicitly disabled; there is no browser login page or redirect-based flow.

## Authorization

- `ControllerAccount.getAuthorities()` grants a single `SimpleGrantedAuthority` of `ROLE_{role}`, where `role` is the raw column value on the `controllers` table (for example `ROLE_ADMIN`).
- `AuthController` strips the `ROLE_` prefix before returning the role in `CurrentUserResponse`.
- Authorization is currently all-or-nothing: `SecurityConfig` requires authentication for any request other than login and CSRF bootstrap, but there is no per-role or per-endpoint method-security (`@PreAuthorize`, `@Secured`) yet — any authenticated controller account can call any protected endpoint, including `/api/users/**`.

## Session and CSRF cookies

| Cookie | HttpOnly | SameSite | Role |
| --- | --- | --- | --- |
| `JSESSIONID` | yes (`server.servlet.session.cookie.http-only: true`) | `lax` (`application.yml`) | Authenticated HTTP session |
| `XSRF-TOKEN` | no (`CookieCsrfTokenRepository.withHttpOnlyFalse()`) | `Lax` (cookie customizer) | CSRF secret for SPA cookie-to-header |

`Secure` is not forced in config so local HTTP via the Vite proxy continues to work.

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

Database credentials remain environment-variable-based; they are unrelated to the session-cookie authentication mechanism described above, which governs API client access rather than the datasource connection.

## Passwords

`SecurityConfig` registers a `BCryptPasswordEncoder` bean. `ControllerAccount.password` stores the BCrypt hash; the `AuthenticationManager` (Spring Security's default `DaoAuthenticationProvider`, wired through `DbUserDetailsService`) verifies submitted passwords against it during `POST /api/auth/login`. Plaintext passwords are never persisted or logged.

## Injection and Query Safety

The committed repository uses Spring Data JDBC repositories rather than handwritten SQL. No raw SQL string concatenation is present in HEAD, which reduces SQL injection risk for the implemented data access path.

## CSRF, Rate Limiting, and Similar Controls

CSRF is enabled with Spring’s cookie-to-header SPA pattern:

- `CookieCsrfTokenRepository` (bean) stores the expected token in the `XSRF-TOKEN` cookie (not in the HTTP session).
- `CsrfTokenRequestAttributeHandler` (bean) resolves the submitted value from the `X-XSRF-TOKEN` request header as the raw cookie value.
- Spring’s `CsrfFilter` validates unsafe methods (`POST`, `PUT`, `PATCH`, `DELETE`); safe methods such as `GET` are not CSRF-checked.
- Clients bootstrap with `GET /api/auth/csrf`, then send `X-XSRF-TOKEN` matching `XSRF-TOKEN` on mutations (including login and logout). Missing or mismatched CSRF yields `403` before the controller runs.
- Logout clears the CSRF cookie so the secret does not outlive the session.

No rate limiting is present.

## Security-Sensitive Dependencies

- `org.postgresql:postgresql` for database connectivity
- `org.springframework.boot:spring-boot-starter-web` for HTTP handling
- `org.springframework.boot:spring-boot-starter-validation` for request validation
- `org.springframework.boot:spring-boot-starter-security` for authentication, authorization, CSRF, and the security filter chain
- `org.springframework.security:spring-security-test` (test scope) for `@WithMockUser`, `csrf()`, and `SecurityMockMvcConfigurers.springSecurity()` in web slice tests

## Operational Note

JDBC logging is configured at debug/trace levels in `application.yml`, which is useful for development but can be noisy in production if copied unchanged.
