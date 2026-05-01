# Trip Forge

Minimal Spring Boot backend for the Trip Forge expense ledger.

## Stack

- Java 25
- Gradle
- Spring Boot 4
- PostgreSQL
- Flyway
- Spring Data JPA
- Testcontainers
- Docker Compose

## Domain

The first slice models a small shared-expense ledger:

- `Account` — a person or wallet that can pay for trip expenses.
- `LedgerTransaction` — an expense paid by one account.
- `TransactionParticipant` — participants and their calculated shares.

This is intentionally small. It is meant to become the future TripForge expenses module.

## Run locally with Docker Compose

```bash
docker compose up --build
```

The API will be available on `http://localhost:8080`.

PostgreSQL will be available on `localhost:5432` with:

```text
Database: tripforge
Username: tripforge
Password: tripforge
```

## Run tests

```bash
gradle clean test
```

The integration test starts PostgreSQL through Testcontainers.

## API examples

Create account:

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ola"}'
```

List accounts:

```bash
curl http://localhost:8080/api/accounts
```

Create transaction:

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{
    "title":"Hotel deposit",
    "amount":120.00,
    "currency":"EUR",
    "paidByAccountId":"<account-id>",
    "transactionDate":"2026-05-01",
    "participantAccountIds":["<account-id>"]
  }'
```

List transactions:

```bash
curl http://localhost:8080/api/transactions
```

## Project structure

```text
src/main/java/com/lesscau/tripforge
├── account        # Account entity, repository and REST API
├── common         # API exception handling
└── transaction    # Ledger transaction domain, service and REST API

src/main/resources
└── db/migration   # Flyway SQL migrations
```

## Design notes

- Flyway owns the schema.
- Hibernate runs with `ddl-auto=validate` only.
- Transaction creation is wrapped in a service-level `@Transactional` boundary.
- `ledger_transactions` is used instead of `transactions` to avoid SQL naming ambiguity and to keep the domain explicit.
- No security, observability, Kafka, Redis or UI yet. Those belong in later slices.
