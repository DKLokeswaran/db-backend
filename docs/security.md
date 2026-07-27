# Security

## Authentication

Authentication is session-cookie based, backed by Spring Security (`spring-boot-starter-security`).

- `SecurityConfig` (`@EnableWebSecurity`) defines the `SecurityFilterChain`. Only `POST /api/auth/login` is `permitAll()`; every other request must be authenticated.
- `AuthController` (`/api/auth`) exposes `POST /login`, `POST /logout`, and `GET /me`.
- `POST /api/auth/login` authenticates a `UsernamePasswordAuthenticationToken` through the `AuthenticationManager`, stores the resulting `Authentication` in a new `SecurityContext`, and persists it via `SecurityContextRepository` (`HttpSessionSecurityContextRepository`). The container sets the `JSESSIONID` cookie on the response.
- `POST /api/auth/logout` invalidates the session through `SecurityContextLogoutHandler`.
- `GET /api/auth/me` returns the current `CurrentUserResponse` (`username`, `role`) for an authenticated session.
- `DbUserDetailsService` (`UserDetailsService`) loads credentials by username via `ControllerAccountRepository.findByUsername`, throwing `UsernameNotFoundException` when no account matches.
- `ControllerAccount` (`@Table("controllers")`) implements `UserDetails` directly — its `enabled`, `accountNonExpired`, `accountNonLocked`, and `credentialsNonExpired` flags come straight from the `controllers` table row.
- Unauthenticated requests to protected endpoints are rejected by an `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)`, which returns a bare `401` (often without a JSON body, since it runs before `GlobalExceptionHandler` in the filter chain). `AuthenticationException` thrown inside a controller method (for example, bad login credentials) is instead caught by `GlobalExceptionHandler.handleAuthenticationException` and returns the standard JSON error envelope.
- `formLogin` is explicitly disabled; there is no browser login page or redirect-based flow.

## Authorization

- `ControllerAccount.getAuthorities()` grants a single `SimpleGrantedAuthority` of `ROLE_{role}`, where `role` is the raw column value on the `controllers` table (for example `ROLE_ADMIN`).
- `AuthController` strips the `ROLE_` prefix before returning the role in `CurrentUserResponse`.
- Authorization is currently all-or-nothing: `SecurityConfig` requires authentication for any request other than login, but there is no per-role or per-endpoint method-security (`@PreAuthorize`, `@Secured`) yet — any authenticated controller account can call any protected endpoint, including `/api/users/**`.

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

CSRF protection is explicitly disabled in `SecurityConfig` (`csrf(csrf -> csrf.disable())`), so no CSRF tokens are issued or required. That is a known gap: the SPA already uses the session cookie (`JSESSIONID`) for state-changing calls such as create, update, and delete on `/api/users`. Until CSRF (or another cross-site request defense) is added, treat browser-session mutations as unprotected against CSRF. No rate limiting is present in HEAD.

## Security-Sensitive Dependencies

- `org.postgresql:postgresql` for database connectivity
- `org.springframework.boot:spring-boot-starter-web` for HTTP handling
- `org.springframework.boot:spring-boot-starter-validation` for request validation
- `org.springframework.boot:spring-boot-starter-security` for authentication, authorization, and the security filter chain
- `org.springframework.security:spring-security-test` (test scope) for `@WithMockUser` and `SecurityMockMvcConfigurers.springSecurity()` in web slice tests

## Operational Note

JDBC logging is configured at debug/trace levels in `application.yml`, which is useful for development but can be noisy in production if copied unchanged.