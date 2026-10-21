# Architecture

This project documents only patterns implemented in the source.

## Transactional outbox

`OrderAcceptanceService` writes the order and `OutboxEvent` in one PostgreSQL transaction; `OutboxRelay` publishes pending events and marks them published after broker confirmation. This preserves durable delivery intent across an application or broker interruption.

## Idempotency key

`OrderAcceptanceService` serializes an idempotency key with a PostgreSQL advisory lock, and `Order.idempotencyKey` is unique. Retries therefore return the existing order instead of reserving stock again.

## Explicit state machine

`OrderStatus` owns the allowed-transition table: `PENDING` may become `CONFIRMED` or `CANCELLED`, and both terminal states reject all transitions. `OrderProcessingService` and `OrderReconciliation` use it for consumer confirmation and timeout cancellation.

## Pessimistic locking

`OrderAcceptanceService`, `OrderProcessingService`, `OrderReconciliation`, and `OutboxRelay` use PostgreSQL `FOR UPDATE`; the relay adds `SKIP LOCKED` to let replicas claim separate outbox work safely.

## Retry and dead-letter recovery

`RabbitMQConfig` configures listener retry, a dead-letter exchange, and `DLQConsumer`; `ConfirmingRabbitTemplate` uses publisher confirms and returns. This separates transient broker/consumer failure from the durable order outcome.

## Repository pattern

`OrderRepository` and `ProductRepository` are Spring Data repositories for entity access. The order write path intentionally uses explicit `JdbcTemplate` SQL where row-lock and transaction boundaries are part of the invariant.
