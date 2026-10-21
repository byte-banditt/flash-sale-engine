# Post-fullstack benchmark verification

Date: `2026-10-21`

The existing `bench/experiment.py`, `bench/load.js`, and `bench/outbox_reconcile.py` were used unchanged. The documented fault workload is constant `50` VUs for `2m`; RabbitMQ restart and replica-kill injection occurs at the harness-defined point.

This verification ran `3` RabbitMQ-restart runs and `3` replica-kill runs. The documented target is `10` and `3`; RabbitMQ was capped at `3` here because each run took several minutes. Raw output was written outside the repository under `/tmp/flash-sale-post-fullstack-rabbit-ok-PQE4Fb` and `/tmp/flash-sale-post-fullstack-kill-ok-pOu9rJ`.

## Machine

```text
Linux DESKTOP-P6C785V 6.18.40.1-microsoft-standard-WSL2 x86_64
Host CPUs: 16
Host memory: 4.8GiB total at observation
Docker Server: 29.8.0
k6: v2.2.0
```

## Commands

```bash
docker compose --profile distributed up -d --build --scale app=2
OUTBOX_MODE=true BASE_URL=http://localhost:8080 python3 bench/experiment.py <run-dir> rabbit
OUTBOX_MODE=true BASE_URL=http://localhost:8080 python3 bench/experiment.py <run-dir> kill
python3 bench/outbox_reconcile.py <run-dir>
```

The distributed profile was used because the existing harness expects its PostgreSQL `postgres` role and fixed local container aliases. The production profile uses the configured `flashsale_app` role and is not silently substituted.

## RabbitMQ restart

| Run | req/s | p95 ms | p99 ms | lost | duplicate | stuck | oversold | audit pass |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| rabbit-1 | 391.362 | 93.106 | 185.597 | 0 | 0 | 0 | 0 | true |
| rabbit-2 | 433.641 | 53.864 | 122.865 | 0 | 0 | 0 | 0 | true |
| rabbit-3 | 392.036 | 97.127 | 171.918 | 0 | 0 | 0 | 0 | true |

Range: `391.362–433.641 req/s`.

## Replica kill

| Run | req/s | p95 ms | p99 ms | lost | duplicate | stuck | oversold | unacknowledged confirmed | audit pass |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| kill-1 | 365.598 | 106.983 | 173.674 | 0 | 0 | 0 | 0 | 0 | true |
| kill-2 | 356.292 | 110.332 | 176.860 | 0 | 0 | 0 | 0 | 1 | true |
| kill-3 | 315.140 | 173.938 | 328.742 | 0 | 0 | 0 | 0 | 1 | true |

Range: `315.140–365.598 req/s`.

## Comparison

| Scenario | Previous documented range | New range | Result |
|---|---:|---:|---|
| RabbitMQ restart | 427–480 req/s | 391.362–433.641 req/s | Lower overall |
| Replica kill | 442–459 req/s | 315.140–365.598 req/s | Lower overall |

All six audits reported zero lost keys, duplicate keys, stuck pending keys, and oversold units. Replica-kill runs 2 and 3 each recorded one `unacknowledged_confirmed` outcome, which the existing harness defines as a confirmed order without an observed HTTP `202`, not a lost or duplicate order.
