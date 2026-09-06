# Flash Sale Engine

A high-throughput flash-sale order system built to survive concurrent demand
spikes on a single product without overselling.

## Architecture

- **Redis + Lua script** — the authoritative, atomic gatekeeper for live stock
  during a sale. A single-threaded check-and-decrement script avoids the
  row-lock contention a relational database would hit under concurrent writes
  to one product row.
- **RabbitMQ** — decouples the fast synchronous reservation from durable
  persistence. A successful Redis reservation publishes a message; a consumer
  processes it asynchronously, with retry and dead-lettering on failure.
- **PostgreSQL** — the durable ledger of what actually sold (`orders`) and the
  record of what a sale started with (`products`). Not touched during the hot
  path of a reservation.

## Running locally

1. `docker compose up -d` — starts Postgres, Redis, and RabbitMQ.
2. `mvn spring-boot:run` — starts the app on port 8080.
3. Yet to come