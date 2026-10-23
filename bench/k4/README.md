# K4 controlled benchmark evidence

This directory contains the raw per-run k6 summaries and compressed Docker
stats from K4. `K` means the Kafka Compose setup and `R` means the equivalent
RabbitMQ Compose setup. Runs were executed in this order:

```text
K-1, R-1, K-2, R-2, R-3, K-3
```

The workload was the unchanged `scripts/load_test.js` scenario: the same
stages, request shape, 100,000 stock, two app containers, nginx, PostgreSQL,
and broker containers under the local Compose caps. Each run drained pending
orders before its database result was recorded.

## Per-run results

Values below are read from each run's `summary.json`.

| Run | req/s | p50 (ms) | p95 (ms) | p99 (ms) | final PostgreSQL result |
|---|---:|---:|---:|---:|---|
| K-1 | 324.81161371817257 | 19.149087 | 122.70176739999998 | 185.61052460000008 | `39046|39046|39046` |
| R-1 | 287.0342017600844 | 40.555748 | 158.65197375 | 231.15485910000075 | `34485|34485|34485` |
| K-2 | 335.9224286935541 | 11.012732 | 111.14355849999998 | 159.55265214999972 | `40359|40359|40359` |
| R-2 | 296.58403603169245 | 36.065882 | 148.31514149999995 | 217.20414480000002 | `35646|35646|35646` |
| R-3 | 293.55494557394695 | 33.878149 | 164.60832770000007 | 246.67537124 | `35282|35282|35282` |
| K-3 | 333.8655163446326 | 14.292865 | 111.5403885 | 167.74384249999986 | `40130|40130|40130` |

## Median and range

| Setup | req/s median (range) | p50 median ms (range) | p95 median ms (range) | p99 median ms (range) |
|---|---:|---:|---:|---:|
| Kafka | 333.8655163446326 (324.81161371817257–335.9224286935541) | 14.292865 (11.012732–19.149087) | 111.5403885 (111.14355849999998–122.70176739999998) | 167.74384249999986 (159.55265214999972–185.61052460000008) |
| RabbitMQ | 293.55494557394695 (287.0342017600844–296.58403603169245) | 36.065882 (33.878149–40.555748) | 158.65197375 (148.31514149999995–164.60832770000007) | 231.15485910000075 (217.20414480000002–246.67537124) |

These are repeated runs on one machine, not independent trials. Tail latency
varied between sessions: the earlier single Kafka run measured p95 160 ms,
while K4 measured 111–123 ms. Differences under roughly 40% should not be
read as real performance differences from this evidence.

The historical RabbitMQ run is **NOT COMPARABLE** to K4. It used a host JVM,
no app containers, and no nginx. Its `433.86762161230314 req/s`, p50
`2.273946 ms`, p95 `5.323319649999997 ms`, and p99 `7.7905503099999995 ms`
remain historical pre-migration measurements only.

Production Compose has no resource caps. The local Kafka setup used a single
broker with a 384 MiB container cap and `KAFKA_HEAP_OPTS=-Xms64m -Xmx128m`;
Kafka RSS peaked at 323.5 MiB. `acks=all` on this single broker gives no
replication durability beyond that broker's disk. Relay database-pool
contention was not isolated.

No conclusion that Kafka is faster is drawn from these measurements.
