# Flash Sale Engine

A small Spring Boot service demonstrating flash-sale order flow. Reserves
inventory in Redis, queues accepted orders in RabbitMQ, persists processed
orders in PostgreSQL.

Learning/demo project; not production-ready commerce system.

## What it does

1. Operator initializes Redis stock key for a product ID.
2. `POST /api/v1/orders` runs Redis Lua script to atomically check/decrement stock.
3. Successful reservation publishes request to RabbitMQ, returns `202 Accepted` with generated order ID.
4. RabbitMQ consumer stores `CONFIRMED` order in PostgreSQL.
5. Consumer failures retry twice, delays two then four seconds. Exhausted retries republish to dead-letter queue; its consumer attempts Redis stock rollback and logs failure.

Order endpoint response means reservation accepted for async processing; not that order already exists in PostgreSQL.

## Components

| Component | Current role |
| --- | --- |
| Spring Boot / Java 21 | HTTP API, app wiring, consumers |
| Redis 7 | Holds `product:{productId}:stock`; Lua atomic reservation |
| RabbitMQ 3.13 | Order messages; order/dead-letter queues |
| PostgreSQL 16 | Processed orders; JPA also maps `products` table |

## Local setup

Prereqs: Docker Compose, Java 21. Maven wrapper included.

```bash
docker compose up -d
./mvnw spring-boot:run
```

App runs `8080`. Defaults in [`application.yaml`](src/main/resources/application.yaml). Docker exposes PostgreSQL `5432`, Redis `6379`, RabbitMQ AMQP `5672`, management UI `15672` (`guest` / `guest`). Credentials local-demo only; do not reuse outside local environment.

Hibernate uses `ddl-auto: update`, creating/updating mapped tables at startup.

```bash
docker compose down
```

## Try flow

Initialize stock. Current API treats `productId` as Redis-key component; does not look up PostgreSQL product record.

```bash
curl -X POST 'http://localhost:8080/api/v1/orders/init?productId=101&stock=10'
```

Place order:

```bash
curl -i -X POST http://localhost:8080/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "productId": 101,
    "quantity": 1,
    "idempotencyKey": "demo-order-001"
  }'
```

| Status | Meaning |
| --- | --- |
| `202 Accepted` | Redis reservation succeeded; AMQP publish did not throw. Order `PENDING` for async processing. |
| `409 Conflict` / `OUT_OF_STOCK` | Redis key exists, insufficient stock. |
| `409 Conflict` / `NOT_FOUND` | Redis stock key uninitialized. |
| `503 Service Unavailable` | AMQP publish threw; code attempts stock rollback. |
| `200 OK` | Persisted order already uses supplied idempotency key. |

## Design notes

- Redis Lua check/decrement runs atomically, so concurrent requests cannot both reserve same units in that key.
- RabbitMQ separates fast reservation from DB persistence.
- `idempotencyKey` unique in PostgreSQL. Consumer checks it before save; duplicate delivery after persistence creates no second row.
- Order queue durable; configured with dead-letter exchange/queue.

## Current limitations

- No HTTP request validation: null, zero, negative quantities, missing idempotency keys not rejected at boundary.
- Stock init unauthenticated, may overwrite Redis stock.
- `products` entity/table unused for inventory initialization/validation; orders use Redis only.
- Idempotency check sees persisted orders only. Duplicates before async persistence can reserve stock/publish more than once.
- No RabbitMQ publisher confirms/outbox. Non-throwing publish not end-to-end durability guarantee.
- Dead-letter rollback failure logged for manual intervention; no further recovery path.
- No end-to-end, concurrency, load test impl. `scripts/seed_data.sql`, `scripts/load_test.js` empty.

## Tests

```bash
./mvnw test
```

Repo contains one Spring context-load test. It uses app's local PostgreSQL config, so PostgreSQL must run for test pass. No automated coverage for reservation/messaging flow.
