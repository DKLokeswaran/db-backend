# API Reference

## Overview

The committed REST surface contains one resource group: `/api/users`.

## Endpoint Summary

| Method | Path | Purpose | Success | Errors |
| --- | --- | --- | --- | --- |
| `POST` | `/api/users` | Create a user | `201 Created` | `400`, `500` |
| `GET` | `/api/users/{id}` | Fetch one user | `200 OK` | `404`, `500` |
| `GET` | `/api/users` | List users | `200 OK` | `500` |
| `PUT` | `/api/users/{id}` | Replace a user | `200 OK` | `400`, `404`, `500` |
| `DELETE` | `/api/users/{id}` | Delete a user | `200 OK` | `404`, `500` |

## Shared Schemas

### User request body

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `name` | string | yes | `@NotBlank` |
| `mobileNo` | string | yes | `@NotBlank` |
| `addressLine` | string | yes | `@NotBlank` |
| `locality` | string | yes | `@NotBlank` |
| `state` | string | yes | `@NotBlank` |
| `country` | string | yes | `@NotBlank` |
| `pincode` | string | yes | `@NotBlank` |
| `createdAt` | datetime | no | if omitted, server fills it on create |
| `id` | number | no | ignored on create; overwritten on update |

### User response body

The controller returns the full `User` entity on create, read, list, and update. The entity shape matches the request body plus `id` and `createdAt`.

## Create User

`POST /api/users`

Request body example:

```json
{
  "name": "kandasamy",
  "mobileNo": "9994722907",
  "addressLine": "tsr layout",
  "locality": "tiruppur",
  "state": "tn",
  "country": "india",
  "pincode": "641607"
}
```

Behavior:

- Request body is validated with `@Valid`.
- `UserController` delegates create logic to `UserService`.
- In the service, incoming `id` is nulled before save.
- In the service, `createdAt` is set to `LocalDateTime.now()` when missing.
- Response is the saved entity.

Responses:

- `201 Created` with the saved `User` object
- `400 Bad Request` when validation fails
- `500 Internal Server Error` for unexpected exceptions

## Get User

`GET /api/users/{id}`

Path parameter:

- `id`: `Long`

Responses:

- `200 OK` with the matching `User`
- `404 Not Found` with response envelope when the id does not exist
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

Responses:

- `200 OK` with an array of `User`
- `500 Internal Server Error` for unexpected exceptions

## Update User

`PUT /api/users/{id}`

Request body shape: same as create user.

Behavior:

- Body is validated with `@Valid`.
- Existing user must exist or the endpoint returns 404.
- Path id wins over any body id because the service applies `updatedUser.setId(id)` before save.
- Response is the saved entity.

Responses:

- `200 OK` with the saved `User`
- `400 Bad Request` on validation failure
- `404 Not Found` when the user does not exist
- `500 Internal Server Error` for unexpected exceptions

## Delete User

`DELETE /api/users/{id}`

Behavior:

- Existence is checked in `UserService.deleteById` before deletion.
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

## Auth and Permissions

Not found in committed history. The exposed endpoints do not have auth annotations, guards, or middleware in HEAD.