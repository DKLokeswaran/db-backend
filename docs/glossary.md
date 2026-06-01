# Glossary

## Domain Terms

| Term | Meaning |
| --- | --- |
| `User` | A donor or user record with address and contact fields. |
| `Event` | A dated activity or collection campaign. |
| `OfferingType` | A catalog entry describing what can be offered, its category, and pricing mode. |
| `OfferingCategory` | High-level classification for an offering: donation, service, or tax. |
| `PricingType` | Pricing mode for an offering: fixed or variable. |
| `PaymentMode` | A way of paying for a transaction. |
| `Receipt` | A receipt header tied to an event and optionally a user. |
| `Transaction` | A line item linking a receipt to an offering type and payment mode. |
| `TransactionItem` | A more granular item record tied to a transaction. |

## Abbreviations

| Abbreviation | Expansion | Notes |
| --- | --- | --- |
| `DB` | database | Appears in environment variable names and package name. |
| `ID` | identifier | Used in aggregate-reference field names. |
| `API` | application programming interface | Used in documentation and endpoint paths. |

## Status and Enum Values

### `OfferingCategory`

- `DONATION`: a contribution-style offering
- `SERVICE`: a service-based offering
- `TAX`: a tax-related offering

### `PricingType`

- `FIXED`: a fixed amount
- `VARIABLE`: an amount that can vary

## Business Process Terms

- `receiptNo`: receipt sequence number
- `displayName`: printable name on a receipt
- `displayLocality`: printable locality on a receipt
- `allowMultiple`: whether multiple quantities are allowed for an offering
- `isActive`: whether an offering type is active

## API Terms

- `baseUrl`: local REST Client variable in `api-testing/user.http`
- `userId`: local REST Client variable in `api-testing/user.http`

## Notes

The repository history is still early-stage, so the glossary is small and tightly coupled to the current domain classes rather than to a mature business vocabulary.