# Deploy on AWS EC2

Status: deployment artifacts only; no AWS resources are created here.

## Host sizing

The historical deployment artifact records `JAVA_TOOL_OPTIONS=-Xmx256m` and
Compose caps PostgreSQL, Redis, and Kafka at `384m`; it records no EC2 instance
type or RAM. Therefore Kafka defaults to `KAFKA_HEAP_OPTS=-Xms64m -Xmx128m`
with log cleaning disabled for the capped single-node setup,
which fits the existing `384m` service cap. Set `.env` explicitly after choosing
an EC2 instance and verify total host RAM before production use; this repository
does not claim an instance-specific sizing result.

## Install and configure

Install Java 21, Docker Engine, and the Docker Compose plugin using official
Ubuntu instructions. Allow inbound TCP `80` (and `443` only when TLS is added)
and restrict SSH to the operator IP. Do not expose PostgreSQL `5432`, Redis
`6379`, or Kafka `9092`.

```bash
git clone <repository-url>
cd flash-sale-engine
git checkout feat/kafka-migration
cp .env.example .env
chmod 600 .env
```

Set unique private PostgreSQL values and, after checking the host, Kafka heap
values in `.env`. Never commit `.env`.

## Deploy and verify

```bash
./scripts/deploy-ec2.sh
docker compose --env-file .env -f docker-compose.prod.yml ps
curl -I http://<instance-ip>/
docker compose --env-file .env -f docker-compose.prod.yml logs --tail=100 nginx app kafka
```

Actuator health and metrics remain internal; nginx returns `404` for
`/actuator` and `/actuator/*`.

## Kafka recovery

This is one KRaft broker. Restart it without deleting its volume:

```bash
docker compose --env-file .env -f docker-compose.prod.yml restart kafka
docker compose --env-file .env -f docker-compose.prod.yml logs --tail=100 kafka app
```

Topics are normally created by the application. If storage was intentionally
replaced, recreate them from the Kafka container:

```bash
docker compose --env-file .env -f docker-compose.prod.yml exec kafka \
  /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --if-not-exists --topic orders --partitions 3 --replication-factor 1
docker compose --env-file .env -f docker-compose.prod.yml exec kafka \
  /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --if-not-exists --topic orders.dlq --partitions 1 --replication-factor 1
```

Inspect the DLQ without exposing Kafka publicly:

```bash
docker compose --env-file .env -f docker-compose.prod.yml exec kafka \
  /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic orders.dlq --from-beginning --max-messages 20
```

Keep `postgres_data` and `kafka_data` for rollback. Roll back application code
by checking out a known-good commit and rerunning the deploy script. A single
broker provides no replication durability beyond its disk; delivery remains
at-least-once.
