# Deploy on AWS EC2

Status: **not yet deployed**.

This repository contains deployment artifacts only. It does not create AWS resources or run AWS CLI commands.

## 1. Launch the instance

1. Launch an Ubuntu LTS EC2 instance in a public subnet.
2. Attach an IAM role only if host operations need AWS APIs; this application does not require one.
3. Allocate storage for PostgreSQL/RabbitMQ data and logs.

## 2. Security group

Allow inbound:

- TCP `80` from `0.0.0.0/0` for HTTP.
- TCP `443` from `0.0.0.0/0` when TLS is configured in front of nginx.
- TCP `22` only from the operator's fixed IP.

Do not expose PostgreSQL `5432`, Redis `6379`, RabbitMQ `5672`, or the RabbitMQ management port.

## 3. Install Docker

Install Java 21, Docker Engine, and the Docker Compose plugin using their official Ubuntu instructions. Add the deploy user to the `docker` group, then reconnect and verify:

```bash
docker version
docker compose version
java -version
```

## 4. Clone and configure

```bash
git clone <repository-url>
cd flash-sale-engine
git checkout feat/fullstack-cloud
cp .env.example .env
chmod 600 .env
```

Set unique private values in `.env`. Never commit `.env`.

## 5. Deploy or re-deploy

```bash
./scripts/deploy-ec2.sh
docker compose --env-file .env -f docker-compose.prod.yml ps
curl -I http://<instance-ip>/
```

The script validates configuration, builds the frontend and backend images, starts two app replicas, and is safe to re-run. PostgreSQL and RabbitMQ data persist in named volumes.

## 6. Verify

Check that nginx returns the frontend, that `/api/v1/orders/products/101/stock` returns the expected uninitialized-sale response before initialization, and that the existing order flow works after initializing a sale. Inspect logs:

```bash
docker compose --env-file .env -f docker-compose.prod.yml logs --tail=100 nginx app
```

Configure TLS certificates and an HTTPS nginx listener before treating the host as production-ready. TLS is not provisioned by this repository.

## 7. Roll back

Keep the previous commit and image volumes available. To roll back application code:

```bash
git checkout <previous-known-good-commit>
./scripts/deploy-ec2.sh
```

Do not delete `postgres_data` or `rabbitmq_data` during rollback. The deployment has no destructive database migration; restore data separately from a tested backup if data corruption is suspected.

Local validation can cover Compose syntax and image build inputs. EC2 networking, disk behavior, TLS, backup recovery, and real replica failover require an actual host and are unverified here.
