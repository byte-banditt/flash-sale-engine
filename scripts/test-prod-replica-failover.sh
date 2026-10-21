#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE=(docker compose --env-file "${ENV_FILE:-$ROOT_DIR/.env.example}" -f "$ROOT_DIR/docker-compose.prod.yml")
VICTIM="$("${COMPOSE[@]}" ps -q app | head -n 1)"
[[ -n "$VICTIM" ]] || { echo "No app replica is running" >&2; exit 1; }
trap 'docker start "$VICTIM" >/dev/null 2>&1 || true' EXIT

PRODUCT_ID="$(date +%s)"
KEY="prod-replica-failover-$PRODUCT_ID"
docker kill "$VICTIM" >/dev/null
curl -fsS -X POST "http://localhost/api/v1/orders/init?productId=$PRODUCT_ID&stock=1" >/dev/null
STATUS="$(curl -sS -o /tmp/flash-sale-replica-order.json -w '%{http_code}' \
  -X POST http://localhost/api/v1/orders \
  -H 'Content-Type: application/json' \
  -d "{\"productId\":$PRODUCT_ID,\"quantity\":1,\"idempotencyKey\":\"$KEY\"}")"
[[ "$STATUS" = 202 || "$STATUS" = 200 ]] || {
  cat /tmp/flash-sale-replica-order.json >&2
  exit 1
}
echo "replica failover order status=$STATUS"
