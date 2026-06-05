# Error Handling

## Error Model

Error handling is concentrated in `GlobalExceptionHandler` and `ApiResponseBuilder`. The workspace adds `ResourceNotFoundException` as the first domain-specific runtime exception for missing resources.

## Handling Boundaries

### Validation failures

`GlobalExceptionHandler.handleValidationException` catches `MethodArgumentNotValidException` and converts field errors into a response with these keys:

- `timestamp`
- `status`
- `error`
- `details`

The `details` value is a field-name-to-message map.

### Bad request failures from service validation

`GlobalExceptionHandler.handleIllegalArgumentException` catches `IllegalArgumentException` (for example from `StringUtils.requireNonBlank` or `UserService` search rules) and returns:

- `timestamp`
- `status` (`400`)
- `error` (`Bad request`)
- `message` (exception message, such as `prefix is required` or `prefix must be at least 2 characters`)

### Not found failures

`GlobalExceptionHandler.handleResourceNotFoundException` catches `ResourceNotFoundException` and returns:

- `timestamp`
- `status` (`404`)
- `error` (from `ex.getError()`, for example `User not found`)
- `message` (from `ex.getMessage()`, for example `No user with id 6 exists`)

`ResourceNotFoundException` carries separate `error` and `message` fields. Use `ResourceNotFoundException.forResourceWithId(String resourceName, Object id)` to build the standard `{Resource} not found` / `No {resource} with id {id} exists` pair.

The handler is registered in the workspace, but `UserController` still returns 404 inline via `ApiResponseBuilder.error` for missing users. Services are expected to throw `ResourceNotFoundException` as controllers are thinned in later refactors.

### Unexpected failures

`GlobalExceptionHandler.handleGenericException` catches any remaining `Exception` and returns:

- `timestamp`
- `status` (`500`)
- `error` (`Unexpected error`)
- `message` (`An unexpected error occurred`)

The handler logs the full stack trace at ERROR but does not expose the underlying exception message to clients.

### Controller-level not found handling (current)

`UserController` still handles missing user ids directly by returning a 404 response from `ApiResponseBuilder.error`. This matches the `ResourceNotFoundException` envelope shape but bypasses the new handler until the User module is refactored.

### Service-level parameter validation

`UserService` validates mobile search inputs before repository calls. Blank `mobile` or `prefix` values and invalid `limit` or prefix length throw `IllegalArgumentException`, which is converted to `400 Bad Request` by the global handler.

## Validation Rules

The validation library in use is Jakarta Bean Validation. Current committed constraints are:

| Model | Field | Constraint message |
| --- | --- | --- |
| `User` | `name` | `User name is required` |
| `User` | `mobileNo` | `Mobile number is required` |
| `User` | `addressLine` | `Address line is required` |
| `User` | `locality` | `Locality is required` |
| `User` | `state` | `State is required` |
| `User` | `country` | `Country is required` |
| `User` | `pincode` | `Pincode is required` |
| `Event` | `name` | `Event name is required` |
| `OfferingType` | `name` | `OfferingType name is required` |
| `OfferingType` | `category` | `Offering category is required` |
| `OfferingType` | `pricingType` | `Pricing type is required` |
| `OfferingType` | `price` | `Price must be greater than 0` |
| `OfferingType` | `allowMultiple` | `Allow multiple flag is required` |
| `OfferingType` | `isActive` | `Is active flag is required` |
| `PaymentMode` | `name` | `PaymentMode name is required` |
| `Receipt` | `eventId` | `Event ID is required` |
| `Receipt` | `receiptNo` | `Receipt number is required` / `Receipt number must be positive` |
| `Receipt` | `displayName` | `Display name is required` |
| `Transaction` | `receiptId` | `Receipt ID is required` |
| `Transaction` | `offeringTypeId` | `Offering type ID is required` |
| `Transaction` | `quantity` | `Quantity is required` / `Quantity must be at least 1` |
| `Transaction` | `amount` | `Amount must be greater than 0` |
| `TransactionItem` | `transactionId` | `Transaction ID is required` |
| `TransactionItem` | `itemName` | `Item name is required` |
| `TransactionItem` | `quantity` | `Quantity is required` / `Quantity must be at least 1` |

## Error Response Examples

### Validation error

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

### Generic error

```json
{
  "timestamp": "2026-05-31T12:34:56.789",
  "status": 500,
  "error": "Unexpected error",
  "message": "An unexpected error occurred"
}
```

## Logging Policy

`GlobalExceptionHandler` uses SLF4J:

| Exception type | Log level | Client exposure |
| --- | --- | --- |
| `ResourceNotFoundException` | WARN (message only) | Full `error` + `message` envelope |
| `IllegalArgumentException` | WARN (message only) | Exception message |
| `MethodArgumentNotValidException` | DEBUG (field map) | Validation `details` map |
| Uncaught `Exception` | ERROR (stack trace) | Generic `An unexpected error occurred` |

## Notable Gaps

- `ResourceNotFoundException` exists but is not yet thrown from `UserService` or `UserController`
- No broader domain exception hierarchy beyond not-found
- No per-controller `try/catch` blocks beyond existence checks
- No error code enum or catalog