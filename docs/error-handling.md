# Error Handling

## Error Model

The backend does not define custom exception classes in committed history. Error handling is concentrated in `GlobalExceptionHandler` and `ApiResponseBuilder`.

## Handling Boundaries

### Validation failures

`GlobalExceptionHandler.handleValidationException` catches `MethodArgumentNotValidException` and converts field errors into a response with these keys:

- `timestamp`
- `status`
- `error`
- `details`

The `details` value is a field-name-to-message map.

### Unexpected failures

`GlobalExceptionHandler.handleGenericException` catches any `Exception` and returns:

- `timestamp`
- `status`
- `error`
- `message`

The error label used by the committed code is `Unexpected error`.

### Controller-level not found handling

`UserController` handles missing user ids directly by returning a 404 response from `ApiResponseBuilder.error`.

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
  "message": "..."
}
```

## Notable Gaps

- No domain-specific exception hierarchy
- No per-controller `try/catch` blocks beyond existence checks
- No explicit logging policy around exceptions
- No error code enum or catalog in committed history