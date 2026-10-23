# K4 maximum Docker stats

Maximum sampled values across the three runs for each setup. Docker CPU can
exceed 100% because it is reported across cores. The source per-second logs are
compressed beside each run's `summary.json`.

| Setup | Component | Max CPU | Max memory |
|---|---|---:|---:|
| Kafka | app-1 | 155.92% | 343.6 MiB |
| Kafka | app-2 | 155.06% | 397.9 MiB |
| Kafka | PostgreSQL | 115.88% | 145.4 MiB |
| Kafka | Kafka | 324.66% | 323.5 MiB |
| Kafka | nginx | 20.08% | 19.72 MiB |
| RabbitMQ | app-1 | 107.59% | 340.3 MiB |
| RabbitMQ | app-2 | 121.18% | 335.5 MiB |
| RabbitMQ | PostgreSQL | 118.92% | 132.5 MiB |
| RabbitMQ | RabbitMQ | 239.04% | 279.1 MiB |
| RabbitMQ | nginx | 17.18% | 18.0 MiB |

The local Compose caps were 640 MiB per app, 384 MiB for PostgreSQL and the
broker, and 64 MiB for nginx. CPU limits were unlimited. Production Compose
does not define these caps.
