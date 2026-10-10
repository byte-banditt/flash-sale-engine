# Flash Sale Engine

A small Spring Boot service demonstrating flash-sale order flow. Reserves
inventory in Redis, queues accepted orders in RabbitMQ, persists processed
orders in PostgreSQL.

Learning/demo project; not production-ready commerce system.

Broker-restart reproduction exposed a refund/persistence race after publisher
confirms: a negative confirm caused inventory restoration, then the delivered
message became CONFIRMED. Redis decision arbitration now makes refund and
persistence mutually exclusive while decision state remains intact. Executed
reproduction, follow-up trials, latency costs and remaining limitations are in
[bench/REPORT.md](bench/REPORT.md) and [RESULTS.md](RESULTS.md).

## What it does

1. Operator initializes Redis stock key for a product ID.
2. `POST /api/v1/orders` runs Redis Lua script to atomically check/decrement stock.
3. Successful reservation publishes request to RabbitMQ and waits for a routed publisher confirm, then returns `202 Accepted` with generated order ID.
4. RabbitMQ consumer claims Redis persistence ownership, then stores `CONFIRMED` in PostgreSQL. A refund-winning delivery is discarded.
5. Consumer failures retry twice, delays two then four seconds. Exhausted retries republish to dead-letter queue; unclaimed orders refund once, while consumer-owned orders resume persistence.

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

## Distributed local profile

The default Compose invocation still starts only infrastructure for the host JVM.
Stop that JVM before starting the distributed profile, which uses the same port.
Build the jar before building the image:

```bash
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package
docker compose --profile distributed up -d --build --scale app=2
```

Change `--scale app=N` to choose replica count. Every app replica runs both HTTP
handlers and order/DLQ consumers. nginx resolves the scaled service through Docker
DNS and distributes requests round-robin; `X-Replica` identifies the upstream.
POSTs already sent are not retried by the proxy.

```text
k6 / HTTP clients (same laptop)
             |
       nginx :8080
       /     |     \\
    app     app    app ...     HTTP API + consumers in each
       \\     |     /
        Redis / RabbitMQ / PostgreSQL
             shared single instances
```

App heaps and container memory are capped in Compose. This is app replication,
not infrastructure HA: the shared services remain failure domains.
See `bench/` and [RESULTS.md](RESULTS.md) for executed experiments and limitations.

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
| `202 Accepted` | Redis reservation succeeded; broker confirmed a routed publish. Order `PENDING` for async processing. |
| `409 Conflict` / `OUT_OF_STOCK` | Redis key exists, insufficient stock. |
| `409 Conflict` / `NOT_FOUND` | Redis stock key uninitialized. |
| `503 Service Unavailable` | Publish failure, nack, return or confirm timeout. Redis arbitration refunds an unclaimed order; a consumer-owned order retains its reservation and can still become CONFIRMED. Retry with the same idempotency key. |
| `200 OK` | Persisted order already uses supplied idempotency key. |
| `400 Bad Request` | Missing or blank idempotency key, missing product ID, or nonpositive quantity. |

Concurrent requests with the same key receive the existing reservation ID with `200 OK` while it is pending.

## Design notes

- Redis Lua checks the idempotency key and decrements stock atomically. Concurrent requests with the same key reserve once.
- RabbitMQ separates fast reservation from DB persistence.
- `idempotencyKey` unique in PostgreSQL. Consumer checks it before save; duplicate delivery after persistence creates no second row. Redis keeps the reservation key until a failed order is rolled back.
- Redis arbitrates persistence versus refund using `order:<orderId>:decision`.
  Atomic Lua selects `CONSUMER` or `REFUND`. Refund first prevents a later
  consumer from writing PostgreSQL; consumer first prevents stock restoration.
  Both scripts keep the decision so redelivery can resume a consumer claim or
  discard a refunded order. PostgreSQL uniqueness still protects duplicate inserts.
- A claimed dead letter resumes persistence. An unclaimed dead letter refunds once;
  duplicate dead letters cannot increment stock twice. Permanent database failure
  can leave a claimed order pending rather than falsely releasing its inventory.
- Order queue durable; configured with dead-letter exchange/queue.
- Publisher confirms are correlated and publishes mandatory. A returned message
  cannot qualify as accepted even if the exchange acknowledges it. Configure the
  wait with `flashsale.publish-confirm-timeout-ms`; DLQ republish also waits for
  confirms before recovering the failed delivery.

## Current limitations

- Stock init unauthenticated, may overwrite Redis stock.
- `products` entity/table unused for inventory initialization/validation; orders use Redis only.
- App replication shares single Redis, RabbitMQ and PostgreSQL instances; these
  are not replicated infrastructure. Publisher confirms are broker acceptance,
  not proof that the consumer has committed a database row.
- Redis has image-default RDB snapshots and an anonymous data volume, with no
  explicit AOF or coordinated recovery policy. The observed normal restart saved
  and reloaded RDB; hard state loss and stale snapshots remain limitations.
- Reservation, publish, decision and persistence remain separate operations.
  Redis arbitration fixes the reproduced refund/late-persistence race while Redis
  state is intact. It is not a distributed transaction or proof of general fault
  tolerance. See [bench/REPORT.md](bench/REPORT.md) for executed trials and bounds.
- Consumer-first confirmation failure still produces an ambiguous HTTP outcome:
  an order may persist despite HTTP failure. Retry with the same idempotency key.
  Broker outage HTTP failures remain; this task does not remove them.
- Losing Redis reservations/decision keys, or restoring stale RDB data, invalidates
  the arbitration foundation. No coordinated Redis/PostgreSQL recovery or durable
  decision journal was added. Never treat a normal Redis restart as an HA proof.
- A crash after reserving but before publishing can leave an orphan reservation.
  Permanent DB failure can leave `CONSUMER` ownership pending; no background
  reconciliation or bounded final compensation was added. An unclaimed permanently
  failed delivery retains the existing DLQ refund behavior and can lack a DB order
  despite its earlier HTTP acceptance.
- Decision and rollback markers do not expire. Safe cleanup requires an inventory
  generation/recovery policy, which is not implemented. No outbox was added.
- No independent recovery path if Redis claim/refund keeps failing.
  `scripts/seed_data.sql` remains empty.

## Tests

```bash
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test
```

Tests use capped Testcontainers for PostgreSQL, Redis, and RabbitMQ; Docker must be running. They cover concurrent oversell, same-key reservations, DLQ redelivery, publish failure, nack, a real unroutable return, and confirm timeout. Added real-delivery/lost-confirm tests cover both arbitration
outcomes, same-key retry after nack, claimed redelivery and claimed-DLQ continuation.
Original test bodies remain unchanged. Run `k6 run scripts/load_test.js` against a running stack for load results. See [`RESULTS.md`](RESULTS.md) for measured output.

For distributed experiments, see [bench/README.md](bench/README.md). Scripts save
raw accepted-key ledgers, database/Redis reconciliation, Docker resource snapshots,
and failed load thresholds. Experiments run sequentially on the same laptop.
