# Data Model

## Overview

The committed code uses Spring Data JDBC entities instead of migration files or an explicit DDL schema. The table definitions below are inferred from `@Table` annotations and field types in HEAD. No committed SQL migration or schema file exists in this repository history.

## Logical Tables

### `user`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key, `@Id` |
| `name` | `String` | no | validated on `UpsertUserRequest` |
| `mobileNo` | `String` | no | validated on `UpsertUserRequest` |
| `addressLine` | `String` | no | validated on `UpsertUserRequest` |
| `locality` | `String` | no | validated on `UpsertUserRequest` |
| `state` | `String` | no | validated on `UpsertUserRequest` |
| `country` | `String` | no | validated on `UpsertUserRequest` |
| `pincode` | `String` | no | validated on `UpsertUserRequest` |
| `createdAt` | `LocalDateTime` | yes | set by service on create when absent |

### `event`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `name` | `String` | no | `@NotBlank` |
| `description` | `String` | yes | optional description |
| `startDate` | `LocalDate` | yes | optional |
| `endDate` | `LocalDate` | yes | optional |
| `createdAt` | `LocalDateTime` | yes | optional |

### `offering_type`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `name` | `String` | no | `@NotBlank` |
| `category` | `OfferingCategory` | no | enum: `DONATION`, `SERVICE`, `TAX` |
| `pricingType` | `PricingType` | no | enum: `FIXED`, `VARIABLE` |
| `price` | `BigDecimal` | yes | `@DecimalMin("0.0", inclusive = false)` |
| `allowMultiple` | `Boolean` | no | `@NotNull` |
| `isActive` | `Boolean` | no | `@NotNull` |
| `createdAt` | `LocalDateTime` | yes | optional |

### `payment_mode`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `name` | `String` | no | `@NotBlank` |

### `receipt`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `eventId` | `AggregateReference<Event, Long>` | no | logical FK to `event.id` |
| `receiptNo` | `Integer` | no | `@NotNull`, `@Min(1)` |
| `userId` | `AggregateReference<User, Long>` | yes | logical FK to `user.id` |
| `displayName` | `String` | no | `@NotBlank` |
| `displayLocality` | `String` | yes | optional |
| `createdAt` | `LocalDateTime` | yes | optional |

### `transaction`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `receiptId` | `AggregateReference<Receipt, Long>` | no | logical FK to `receipt.id` |
| `offeringTypeId` | `AggregateReference<OfferingType, Long>` | no | logical FK to `offering_type.id` |
| `paymentModeId` | `AggregateReference<PaymentMode, Long>` | yes | logical FK to `payment_mode.id` |
| `quantity` | `Integer` | no | default initialized to `1`, `@Min(1)` |
| `amount` | `BigDecimal` | yes | `@DecimalMin("0.0", inclusive = false)` |
| `notes` | `String` | yes | optional |
| `createdAt` | `LocalDateTime` | yes | optional |

### `transaction_item`

| Column | Java type | Nullable | Constraints / notes |
| --- | --- | --- | --- |
| `id` | `Long` | yes | primary key |
| `transactionId` | `AggregateReference<Transaction, Long>` | no | logical FK to `transaction.id` |
| `itemName` | `String` | no | `@NotBlank` |
| `quantity` | `Integer` | no | `@NotNull`, `@Min(1)` |
| `unit` | `String` | yes | optional |

## Relationships

The committed code expresses the following relationships through `AggregateReference` fields:

- `receipt.eventId` references `event.id`
- `receipt.userId` references `user.id`
- `transaction.receiptId` references `receipt.id`
- `transaction.offeringTypeId` references `offering_type.id`
- `transaction.paymentModeId` references `payment_mode.id`
- `transaction_item.transactionId` references `transaction.id`

These are logical application relationships. No foreign-key DDL is committed in HEAD.

## Migration History

Not found in committed history.

## ORM and Query Patterns

- Spring Data JDBC is the persistence model.
- `UserRepository` extends `CrudRepository` and adds `findDistinctMobileNosByPrefix` (custom SQL via `@Query`) and `findByMobileNo` (derived query).
- Mobile numbers are not unique in the domain; multiple `User` rows may share the same `mobileNo`.
- No explicit transaction boundaries are annotated in committed history.

## DTO Shapes

Not found in committed history. The controller binds directly to entity classes.