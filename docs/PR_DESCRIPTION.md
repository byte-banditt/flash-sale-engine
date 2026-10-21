# PR: Full-stack cloud evidence

## Summary

Branch: `feat/fullstack-cloud`

This change adds a same-origin React/Vite client, minimal read-only APIs, explicit order transition ownership, and deployment artifacts for a single EC2 host. The order acceptance, outbox, reconciliation, and terminal-state write path remains semantically unchanged.

## Phase 0

- Audited the existing REST API, tests, compose topology, nginx, and order lifecycle.
- Baseline full suite was run before code changes.
- Baseline result: `OutboxUnitTests` passed; fault tests were skipped by their system-property gate; `OrderFlowIntegrationTests` errored because this environment has no Docker socket.

## Phase 1

- Added `GET /api/v1/orders/products/{productId}/stock`.
- Added `GET /api/v1/orders/{orderId}`.
- Added `frontend/` with React, Vite, plain JavaScript, same-origin `/api` calls, live stock, buy flow, idempotency retry reuse, status polling, terminal polling stop, and documented error states.
- Added backend read-query tests and React Testing Library tests.

## Phase 2

- Added `OrderStatus` with an explicit allowed-transition table.
- Updated consumer confirmation and reconciliation cancellation to consult that table.
- Added allowed and forbidden transition tests.
- Added `docs/ARCHITECTURE.md` with code references for patterns actually present.

## Phase 3

- Added `docker-compose.prod.yml` with two app replicas, healthchecks, restart policies, private infrastructure services, and persistent PostgreSQL/RabbitMQ volumes.
- Added `deploy/nginx.prod.conf` and a multi-stage frontend nginx image.
- Added `.env.example`, `.gitignore` protection for `.env`, `scripts/deploy-ec2.sh`, and `docs/DEPLOY_AWS_EC2.md`.
- Deployment status remains **not yet deployed**.

## Verification run

Passed:

```text
npm test                                  7 tests passed
npm run build                             passed
./mvnw -Dtest=OrderQueryServiceTests,OutboxUnitTests test
                                           9 tests passed
./mvnw -Dtest=OrderStatusTests,OrderQueryServiceTests,OutboxUnitTests test
                                           12 tests passed
./mvnw -DskipTests package                 passed
bash -n scripts/deploy-ec2.sh               passed
git diff --check                            passed
```

Not verified:

- Full Testcontainers suite: Docker is unavailable in this environment (`/var/run/docker.sock` absent).
- `docker compose -f docker-compose.prod.yml config`: Docker CLI is unavailable here.
- Docker image builds, EC2 networking, TLS, real two-replica routing, persistence recovery, backup restore, and rollback on an actual host.
- No AWS resources or AWS CLI commands were used.

## Manual review checklist

- Confirm the read endpoint response shapes and `404` behavior match the frontend contract.
- Inspect `OrderStatus` against all existing terminal-state and reconciliation tests.
- Review nginx API proxying, frontend fallback routing, and POST retry behavior.
- Review `.env` handling and replace all example credentials before deployment.
- Run the full Maven/Testcontainers suite with Docker available.
- Run `docker compose --env-file .env -f docker-compose.prod.yml config`, build the images, and verify the two app replicas before opening traffic.
- Configure TLS and validate backup/restore procedures before production use.
