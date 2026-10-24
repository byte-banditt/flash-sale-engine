# Flash Sale Engine

[![CI](https://github.com/byte-banditt/flash-sale-engine/actions/workflows/ci.yml/badge.svg?branch=feat/ci)](https://github.com/byte-banditt/flash-sale-engine/actions/workflows/ci.yml)

Spring Boot flash-sale demo with PostgreSQL inventory, durable order intent,
a transactional outbox, and RabbitMQ consumers. Learning project; shared
infrastructure remains a failure domain.

## Write and recovery flow

- Sale initialization records total stock, available stock, and sale start time
  in `products`. It rejects reinitialization while any orders exist for that sale.
- The HTTP transaction locks the idempotency key and product row, checks/decrements
  stock, inserts a `PENDING` order and its JSONB outbox event, then commits.
  It calls neither Redis nor RabbitMQ. Failure rolls back all these writes.
- Each replica runs a relay. `FOR UPDATE SKIP LOCKED` selects pending events in
  creation order. A routed publisher ack permits `PUBLISHED`; failure leaves
  `PENDING` for retry. The relay never refunds stock.
- Consumers lock the durable order and transition `PENDING` to `CONFIRMED`.
  Duplicate messages cannot repeat consumption; immutable payload fields are checked.
- The scheduled sweep locks expired `PENDING` orders, cancels them, restores stock,
  and marks their events `FAILED` in a single transaction. This is the only refund
  path. The same order lock prevents a late consumer from confirming a cancelled order.
  The database row provides the actual consumer-side outcome; RabbitMQ cannot prove
  that an individual in-flight message does not exist.

Stock and durable delivery intent cannot commit separately. Broker publication
remains asynchronous and at least once: a crash after broker ack but before the
relay commits can republish. This design guarantees a single database terminal
transition, not exactly-once physical broker delivery or immediate publication.

## Components

| Component | Role |
| --- | --- |
| Apps | HTTP API, outbox relay, sweep, order/DLQ consumers |
| PostgreSQL | Authoritative stock, sale start, orders and outbox |
| RabbitMQ | Durable queues with publisher confirms and mandatory returns |
| Redis | Retained in local Compose for compatibility; unused by the order path |

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

Build the jar, then use the existing distributed profile:

```bash
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package
docker compose --profile distributed up -d --build --scale app=2
```

Every replica runs HTTP, relay, sweep and consumers. nginx uses Docker DNS and
round-robin routing; `X-Replica` identifies the upstream. Heaps and container
memory remain capped in Compose. The load generator, nginx, apps and infrastructure
share the same laptop in these experiments.

```text
HTTP / k6 -> nginx -> app replicas
                         | accept / sweep / consumer terminal transaction
                         v
                     PostgreSQL
                     products + orders + outbox_events
                         |
                 replica relays (SKIP LOCKED)
                         | confirms, retry without refund
                         v
                     RabbitMQ -> replica consumers

Redis: local infrastructure retained; no authoritative stock or reservation.
```

## API

```bash
curl -X POST 'http://localhost:8080/api/v1/orders/init?productId=101&stock=10'
curl -i -X POST http://localhost:8080/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d '{"productId":101,"quantity":1,"idempotencyKey":"demo-order"}'
```

| Response | Meaning |
| --- | --- |
| `202 PENDING` | Stock, order and outbox intent committed; broker ack is asynchronous |
| `200` | Existing idempotency key, including terminal `CANCELLED` |
| `409 OUT_OF_STOCK / NOT_FOUND` | Stock unavailable or sale uninitialized |
| `400` | Invalid order input |

A cancelled key remains a tombstone. Retry that key returns its terminal outcome;
use a new key for a new purchase. Broker failure no longer triggers controller
refund or synchronous publish failure. Database failures still fail the HTTP request.

## Configuration and limitations

Configure `flashsale.outbox.batch-size`, `poll-delay-ms`, `pending-timeout-ms`,
`sweep-delay-ms`, and `scheduling-enabled`; publisher waits use
`flashsale.publish-confirm-timeout-ms`. Defaults live in the worker constructors
and scheduling annotations. Tests disable scheduling when invoking workers directly.

- Stock, order and event are atomically durable only to the extent PostgreSQL is
  durable. This demo has no replicated database, broker or infrastructure HA.
- A pending deadline can cancel an accepted order during a prolonged outage.
  `202` means durable intent, not an unconditional eventual `CONFIRMED` promise.
  Reconciliation fences late messages; it cannot infer broker non-delivery.
- PostgreSQL product row locks serialize sales for a hot product. Throughput and
  latency changes must be measured; this outbox is not a scaling claim.
- The relay holds DB locks while awaiting confirms. A failed batch retries;
  scheduling/connection failures may delay recovery. A surviving replica or
  restarted worker is required for progress.
- Redis has no coordinated persistence/recovery design. Redis loss no longer changes
  authoritative order stock; it remains separate local infrastructure.
- Initialization is unauthenticated. New sale generations and historical order
  retention need an explicit production design; do not reset a live sale.
- Hibernate `ddl-auto=update` is a demo schema mechanism. Review
  [scripts/outbox_schema.sql](scripts/outbox_schema.sql), use versioned migrations
  in production, and backfill legacy sale start from audited records. Do not invent
  start times or migrate Redis-only reservations silently: drain the old generation
  before switching writers. Mixed old/new app generations are unsupported.

## Tests and evidence

```bash
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test
# Build the app image before enabling capped, multi-replica Testcontainers faults:
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw -DskipTests package
docker build -t flash-sale-engine:local .
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw -Dflashsale.run-container-faults=true test
```

The previous test snapshot and this session's baseline are retained under
`bench/results/outbox-session/baseline/`. Publisher/refund tests now assert durable
acceptance, retry and sweep cancellation, as explicitly authorized. Oversell and
same-key stock/count assertions retain their protection. Container fault tests
reuse `bench/experiment.py`, `bench/load.js` and `bench/http_concurrency.py`.
The deterministic poller crash test blocks a DB update after a real broker ack,
kills the app, terminates its abandoned DB session, and restarts both workers;
it verifies actual republish with a single confirmed order.

Historical measurements remain in [RESULTS.md](RESULTS.md) and
[bench/REPORT.md](bench/REPORT.md). New results are appended separately; historical
Redis arbitration measurements are not evidence for the outbox implementation.
Benchmark setup and invariant checks are documented in
[bench/README.md](bench/README.md).

This session's commands, per-run recovery results, retained failed attempts and
before/after HTTP measurements are in [bench/OUTBOX_REPORT.md](bench/OUTBOX_REPORT.md).
