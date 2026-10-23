# Kafka migration benchmark

## Historical result: NOT COMPARABLE

The historical RabbitMQ result is **NOT COMPARABLE** with the Kafka result.
That run used a host JVM, no app containers, and no nginx. Its values are
pre-migration evidence only:

| Metric | RabbitMQ historical, not comparable |
|---|---:|
| req/s | 433.86762161230314 |
| p50 (ms) | 2.273946 |
| p95 (ms) | 5.323319649999997 |
| p99 (ms) | 7.7905503099999995 |

Those numbers must not be used as a like-for-like Kafka comparison.

## K4 controlled comparison

K4 used the same Compose shape, local resource caps, two app containers,
nginx, PostgreSQL, broker, and unchanged `scripts/load_test.js` workload for
both setups. The randomized order was `K,R,K,R,R,K`. Each setup ran three
times on the same machine.

| Setup | req/s median (range) | p50 median ms (range) | p95 median ms (range) | p99 median ms (range) |
|---|---:|---:|---:|---:|
| Kafka | 333.8655163446326 (324.81161371817257–335.9224286935541) | 14.292865 (11.012732–19.149087) | 111.5403885 (111.14355849999998–122.70176739999998) | 167.74384249999986 (159.55265214999972–185.61052460000008) |
| RabbitMQ | 293.55494557394695 (287.0342017600844–296.58403603169245) | 36.065882 (33.878149–40.555748) | 158.65197375 (148.31514149999995–164.60832770000007) | 231.15485910000075 (217.20414480000002–246.67537124) |

The exact per-run summaries and compressed Docker stats are in
[`bench/k4/`](k4/). The maximum stats table is in
[`bench/k4/max-docker-stats.md`](k4/max-docker-stats.md).

These are repeated runs on one machine, not independent trials. Tail latency
varied between sessions: an earlier single Kafka run measured p95 160 ms,
while K4 measured 111–123 ms. Differences under roughly 40% should not be
read as real performance differences from this evidence.

Production Compose has no resource caps. During local K4, Kafka RSS peaked at
323.5 MiB under its 384 MiB cap. Kafka was a single broker; `acks=all` gives no
replication durability beyond that broker's disk. Relay database-pool
contention was not isolated.

No claim that Kafka is faster is made.
