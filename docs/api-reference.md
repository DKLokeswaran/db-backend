# API Reference

## Overview

The committed REST surface contains two resource groups: `/api/auth` for session authentication and `/api/users` for donor/user CRUD. Every `/api/users` endpoint requires an authenticated session established through `/api/auth/login`; only `POST /api/auth/login` is reachable without one.

## Endpoint Summary

### `/api/auth`

| Method | Path | Purpose | Success | Errors |
| --- | --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Authenticate and start a session | `200 OK` | `400`, `401` |
| `POST` | `/api/auth/logout` | End the current session | `200 OK` | `401` |
| `GET` | `/api/auth/me` | Fetch the authenticated caller | `200 OK` | `401` |

### `/api/users`

All endpoints below require an authenticated session (`JSESSIONID` cookie from a prior `POST /api/auth/login`); an unauthenticated request receives `401` before reaching the controller.

| Method | Path | Purpose | Success | Errors |
| --- | --- | --- | --- | --- |
| `POST` | `/api/users` | Create a user | `201 Created` | `400`, `401`, `500` |
| `GET` | `/api/users/{id}` | Fetch one user | `200 OK` | `401`, `404`, `500` |
| `GET` | `/api/users` | List all users, or filter by mobile | `200 OK` | `400`, `401`, `500` |
| `GET` | `/api/users/search/mobile` | Distinct mobile prefix search (typeahead) | `200 OK` | `400`, `401`, `500` |
| `PUT` | `/api/users/{id}` | Replace a user | `200 OK` | `400`, `401`, `404`, `500` |
| `DELETE` | `/api/users/{id}` | Delete a user | `200 OK` | `401`, `404`, `500` |

## Shared Schemas

### Login request body (`LoginRequest`)

Used for `POST /api/auth/login`.

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `username` | string | yes | `@NotBlank` ("Username is required") |
| `password` | string | yes | `@NotBlank` ("Password is required") |

### Current user response body (`CurrentUserResponse`)

Returned by `POST /api/auth/login` and `GET /api/auth/me`.

| Field | Type | Notes |
| --- | --- | --- |
| `username` | string | authenticated principal name |
| `role` | string | authority with the `ROLE_` prefix stripped, for example `ADMIN` |

### User request body (`UpsertUserRequest`)

Used for `POST /api/users` and `PUT /api/users/{id}`.

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `name` | string | yes | `@NotBlank` |
| `mobileNo` | string | yes | `@NotBlank` |
| `addressLine` | string | yes | `@NotBlank` |
| `locality` | string | yes | `@NotBlank` |
| `state` | string | yes | `@NotBlank` |
| `country` | string | yes | `@NotBlank` |
| `pincode` | string | yes | `@NotBlank` |

Request bodies do not accept `id` or `createdAt`; those are server-managed.

### User response body (`UserResponse`)

Returned on create, read, list, and update.

| Field | Type | Notes |
| --- | --- | --- |
| `id` | number | persisted primary key |
| `name` | string | |
| `mobileNo` | string | |
| `addressLine` | string | |
| `locality` | string | |
| `state` | string | |
| `country` | string | |
| `pincode` | string | |

`createdAt` is server-managed on the `User` entity but is not included in API responses.

### Mobile prefix search response (`MobilePrefixSearchResponse`)

Returned by `GET /api/users/search/mobile`.

| Field | Type | Notes |
| --- | --- | --- |
| `mobileNos` | string array | distinct mobile numbers matching the prefix |

## Login

`POST /api/auth/login`

Request body example:

```json
{
  "username": "admin",
  "password": "secret"
}
```

Behavior:

- Request body is validated with `@Valid` on `LoginRequest`.
- `AuthController` authenticates the credentials through `AuthenticationManager`, which delegates to `DbUserDetailsService` and the BCrypt-backed `PasswordEncoder`.
- On success, the resulting `Authentication` is stored in a new `SecurityContext` and persisted via `SecurityContextRepository`, which sets the `JSESSIONID` session cookie on the response.

Responses:

- `200 OK` with a `CurrentUserResponse` object, and a `Set-Cookie: JSESSIONID=...` header
- `400 Bad Request` when `username` or `password` is blank
- `401 Unauthorized` when credentials are invalid

401 response shape:

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password"
}
```

## Logout

`POST /api/auth/logout`

Behavior:

- Requires an authenticated session.
- Invalidates the session and clears the security context via `SecurityContextLogoutHandler`.

Success response shape:

```json
{
  "message": "Logout successful"
}
```

Responses:

- `200 OK` with the message payload
- `401 Unauthorized` when the caller has no valid session

## Get Current User

`GET /api/auth/me`

Behavior:

- Requires an authenticated session.
- Returns the same `CurrentUserResponse` shape as login, derived from the current `Authentication`.

Responses:

- `200 OK` with a `CurrentUserResponse`
- `401 Unauthorized` when the caller has no valid session

## Create User

`POST /api/users`

Request body example:

```json
{
  "name": "Jane Doe",
  "mobileNo": "9876543210",
  "addressLine": "123 Main Street",
  "locality": "Springfield",
  "state": "KA",
  "country": "India",
  "pincode": "560001"
}
```

Behavior:

- Request body is validated with `@Valid` on `UpsertUserRequest`.
- `UserController` delegates to `UserService.create`.
- `UserMapper.toEntity` maps the request to a `User` entity; the service clears `id` and sets `createdAt` when absent.
- Response is a `UserResponse` DTO.

Responses:

- `201 Created` with a `UserResponse` object
- `400 Bad Request` when validation fails
- `500 Internal Server Error` for unexpected exceptions

## Get User

`GET /api/users/{id}`

Path parameter:

- `id`: `Long`

Responses:

- `200 OK` with a `UserResponse`
- `404 Not Found` when the id does not exist (`ResourceNotFoundException` from `UserService`)
- `500 Internal Server Error` for unexpected exceptions

404 response shape:

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 404,
  "error": "User not found",
  "message": "No user with id 6 exists"
}
```

## List Users

`GET /api/users`

Without query parameters, returns every user.

### List Users by Mobile

`GET /api/users?mobile={mobileNo}`

Query parameter:

- `mobile`: exact mobile number (trimmed in `UserService`; required when filtering)

Behavior:

- Delegates to `UserService.findByMobileNo`.
- Returns all matching users as `UserResponse` objects (mobile is not unique in the domain).
- Returns an empty array when no users match.
- Blank `mobile` values throw `IllegalArgumentException` in the service.

Responses:

- `200 OK` with an array of `UserResponse` (possibly empty)
- `400 Bad Request` when `mobile` is missing or blank
- `500 Internal Server Error` for unexpected exceptions

## Search Mobile Numbers by Prefix

`GET /api/users/search/mobile`

Query parameters:

| Parameter | Required | Default | Rules |
| --- | --- | --- | --- |
| `prefix` | yes | — | Trimmed; minimum length 2 |
| `limit` | no | `5` | Minimum 1; capped at 10 |

Behavior:

- Delegates to `UserService.searchMobileNosByPrefix`.
- Runs `SELECT DISTINCT mobile_no ... WHERE mobile_no LIKE prefix%` with the resolved limit.
- Intended for typeahead UIs that suggest mobile numbers while typing.

Success response: `MobilePrefixSearchResponse` object, for example:

```json
{
  "mobileNos": ["9876543210", "9876543211"]
}
```

Responses:

- `200 OK` with a `MobilePrefixSearchResponse` (possibly empty `mobileNos` array)
- `400 Bad Request` when `prefix` is missing, blank, or shorter than 2 characters; when `limit` is less than 1; or when Spring rejects a missing required `prefix` parameter
- `500 Internal Server Error` for unexpected exceptions

400 response shape (service-thrown `IllegalArgumentException`):

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 400,
  "error": "Bad request",
  "message": "prefix must be at least 2 characters"
}
```

## Update User

`PUT /api/users/{id}`

Request body shape: same `UpsertUserRequest` fields as create user.

Behavior:

- Body is validated with `@Valid` on `UpsertUserRequest`.
- `UserService.update` loads the user or throws `ResourceNotFoundException`.
- `UserMapper.applyFields` copies request fields onto the existing entity; path `id` is preserved.
- Response is a `UserResponse` DTO.

Responses:

- `200 OK` with the saved `UserResponse`
- `400 Bad Request` on validation failure
- `404 Not Found` when the user does not exist
- `500 Internal Server Error` for unexpected exceptions

## Delete User

`DELETE /api/users/{id}`

Behavior:

- `UserService.deleteById` throws `ResourceNotFoundException` when the id does not exist.
- Successful deletes return a simple message payload.

Success response shape:

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "message": "User deleted successfully",
  "id": 6
}
```

Responses:

- `200 OK` with the message payload
- `404 Not Found` when the user does not exist
- `500 Internal Server Error` for unexpected exceptions

## Bad Request Error Shape

Invalid search or filter parameters thrown as `IllegalArgumentException` from `UserService` are handled by `GlobalExceptionHandler.handleIllegalArgumentException` and use the same envelope as other API errors, with `error` set to `Bad request` and `message` describing the validation failure.

## Validation Error Shape

Validation failures are converted to a standard envelope by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 400,
  "error": "Validation failed",
  "details": {
    "name": "User name is required"
  }
}
```

## Unexpected Error Shape

Uncaught exceptions are handled by `GlobalExceptionHandler.handleGenericException`. The client receives a fixed message; internal details are logged server-side only.

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 500,
  "error": "Unexpected error",
  "message": "An unexpected error occurred"
}
```

## Auth and Permissions

- Authentication is a server-side session cookie (`JSESSIONID`), established by `POST /api/auth/login` and enforced by `SecurityConfig`'s `SecurityFilterChain`.
- Only `POST /api/auth/login` is `permitAll()`; every other endpoint, including all of `/api/users`, requires an authenticated session.
- There is no per-role authorization yet — any authenticated `ControllerAccount` (role `ROLE_{role}`) can call any protected endpoint.
- CSRF protection is disabled (`csrf().disable()`), so no CSRF token is required on state-changing requests.
- Requests without a valid session that hit a protected endpoint are rejected by the filter chain's `HttpStatusEntryPoint` before reaching a controller, which returns a bare `401` status that may have no JSON body. Only `AuthenticationException`s raised from inside a controller method (for example, a failed login) go through `GlobalExceptionHandler` and get the full JSON error envelope shown above.