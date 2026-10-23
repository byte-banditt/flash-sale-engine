# Kafka migration benchmark

The RabbitMQ values in `RESULTS.md` and `bench/results/**` are pre-migration
measurements. They were produced by the same k6 workload on one resource-capped
machine and are not independent trials.

| Metric | RabbitMQ pre-migration run-1 | Kafka run-1 | Kafka status |
|---|---:|---:|---|
| req/s | 433.86762161230314 | 330.8932151659467 | measured |
| p50 (ms) | 2.273946 | 10.756386 | measured |
| p95 (ms) | 5.323319649999997 | 160.258561 | measured |
| p99 (ms) | 7.7905503099999995 | 237.5963298 | measured |

Measured Kafka run-1: 330.8932151659467 req/s, p50 10.756386 ms,
p95 160.258561 ms, p99 237.5963298 ms. The raw k6 summary, drain log,
Postgres result, and resource snapshot are under `results/kafka-run-1/`.
The run used the same `scripts/load_test.js` stages and request parameters.
The first 120-second drain window expired with 8,521 pending rows; continued
drain reached zero at poll 139 and the final database count was
`39760|39760|39760`. This is one resource-capped machine and one completed
Kafka run, not independent trials; do not treat the values as a statistical
comparison.

An initial attempt OOM-killed the single Kafka container (exit 137) at the
384 MiB cap with a 256 MiB heap. The Compose heap is now 64/128 MiB and Kafka
log cleaning is disabled for this capped benchmark; this configuration change
is part of the measured setup.
