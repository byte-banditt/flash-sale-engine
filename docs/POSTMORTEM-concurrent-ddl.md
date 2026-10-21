# Postmortem: Concurrent Production Schema Update

## Status

Local production-compose issue reproduced and mitigated. No EC2 or cloud deployment was involved in this verification.

## Symptom

During end-to-end local verification of the production Compose stack, the two app replicas emitted PostgreSQL/Hibernate schema-update errors while starting. Observed messages included duplicate `orders_id_seq`, duplicate `outbox_events` and `products` types, existing indexes/constraints, and a duplicate foreign-key constraint. The stack later became healthy and its API smoke flow worked, but the startup logs contained these errors.

## Detection

The issue was detected by inspecting the app and PostgreSQL logs after building and starting `docker-compose.prod.yml` with two app replicas, then exercising the stack through nginx. The verification covered frontend loading, stock, order creation, order status, duplicate idempotency, out-of-stock behavior, and replica failover.

## Root cause

The production Compose setup started two Spring Boot replicas concurrently while `src/main/resources/application.yaml` configured Hibernate with `ddl-auto: update`. Both instances could attempt the same schema changes at startup.

## Fix

Commit `1345aa0` (`fix: serialize production app startup`) changed `scripts/deploy-ec2.sh` to:

1. start one app replica;
2. wait until that replica reports Docker health `healthy`;
3. scale the app service to two replicas.

## Verification after the fix

With clean local Compose volumes, the first replica became healthy, the service was scaled to two healthy replicas, and the nginx smoke flow passed. Killing one replica and placing an order through nginx also returned `202`.

## Remaining limitation

Production still uses Hibernate `ddl-auto: update`. After the fix, Hibernate warnings of the form `constraint ... does not exist, skipping` remained during schema update. A versioned production migration strategy has not been added or verified.

No timestamp, customer impact, or impact count is recorded here because it was not established by the available evidence.
