# Dependencies

## Build Coordinates

- Parent: `org.springframework.boot:spring-boot-starter-parent:4.0.6`
- Java version: `17`
- Artifact: `com.lokeswarandk:db-backend:0.0.1-SNAPSHOT`

## Direct Dependencies

| Dependency | Version | Scope | Category | Purpose | Security-sensitive |
| --- | --- | --- | --- | --- | --- |
| `org.springframework.boot:spring-boot-starter` | managed by parent | compile | application bootstrap | core Spring Boot runtime | no |
| `org.springframework.boot:spring-boot-starter-data-jdbc` | managed by parent | compile | database/ORM | Spring Data JDBC persistence | yes, data access |
| `org.springframework.boot:spring-boot-starter-validation` | managed by parent | compile | validation | Jakarta Bean Validation support | yes, input validation |
| `org.springframework.boot:spring-boot-starter-web` | managed by parent | compile | HTTP/networking | REST controller support and embedded web server | yes, HTTP exposure |
| `org.springframework.boot:spring-boot-starter-security` | managed by parent | compile | authentication/authorization | Spring Security filter chain, `AuthenticationManager`, `BCryptPasswordEncoder` | yes, auth |
| `org.postgresql:postgresql` | managed by parent or transitive version resolution in build | compile | database driver | PostgreSQL JDBC driver | yes |
| `org.springframework.boot:spring-boot-starter-test` | managed by parent | test | testing | Spring Boot test support (JUnit, Mockito, AssertJ) | no |
| `org.springframework.security:spring-security-test` | managed by parent | test | testing | `@WithMockUser`, `SecurityMockMvcConfigurers.springSecurity()` for secured MockMvc tests | no |
| `org.springframework.boot:spring-boot-starter-webmvc-test` | managed by parent | test | testing | MockMvc web slice tests | no |
| `com.h2database:h2` | managed by parent | test | database driver | in-memory JDBC for test profile | no |

## Notable Transitive Bundles

The build manifest does not pin transitive versions directly, but the starter dependencies imply these notable package groups:

- `spring-boot-starter-data-jdbc`: Spring Data JDBC, Spring Data Relational, JDBC support, transaction support
- `spring-boot-starter-validation`: Jakarta Validation API, Hibernate Validator
- `spring-boot-starter-web`: Spring MVC, embedded Tomcat, Jackson JSON support
- `spring-boot-starter-security`: Spring Security core, config, and web filter modules
- `spring-boot-starter-test`: JUnit Jupiter, Spring Test, Mockito, AssertJ, Hamcrest
- `spring-boot-starter-webmvc-test`: MockMvc and `@WebMvcTest` support

## Build Plugins

| Plugin | Version | Purpose |
| --- | --- | --- |
| `com.diffplug.spotless:spotless-maven-plugin` | `3.5.0` | Java and YAML formatting; `check` goal bound to `verify` |
| `spring-boot-maven-plugin` | managed by parent | package/run Spring Boot application |

## Dependency Categories

- UI framework: not found in backend history
- State management: not found in backend history
- Routing: Spring MVC request mapping
- HTTP/networking: Spring Web, embedded server, PostgreSQL JDBC
- Authentication/authorization: Spring Security (`spring-boot-starter-security`)
- Database/ORM: Spring Data JDBC, PostgreSQL driver
- Testing: Spring Boot Starter Test, Spring Security Test, Spring WebMvc Test, H2 (test scope), JUnit 5, Mockito, AssertJ
- Build tooling: Maven Wrapper, Spring Boot Maven Plugin, Spotless Maven Plugin
- Linting/formatting: Spotless (Google Java Format AOSP), `.editorconfig`
- Utilities: validation, response shaping helper

## Security-Sensitive Packages

- `org.postgresql:postgresql`
- `org.springframework.boot:spring-boot-starter-web`
- `org.springframework.boot:spring-boot-starter-validation`
- `org.springframework.boot:spring-boot-starter-data-jdbc`
- `org.springframework.boot:spring-boot-starter-security`

## Notes

The committed manifest is small enough that the dependency inventory is dominated by Spring Boot starter bundles rather than many direct third-party libraries.