#!/usr/bin/env bash
set -Eeuo pipefail

BASE_URL="${1:-}"
if [[ -z "$BASE_URL" ]]; then
  printf 'usage: %s BASE_URL\n' "$0" >&2
  exit 2
fi
BASE_URL="${BASE_URL%/}"

tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT

check_status() {
  local name="$1" expected="$2" url="$3" method="${4:-GET}" data="${5:-}"
  local actual
  if [[ "$method" == POST ]]; then
    actual="$(curl -sS -o "$tmp_dir/response" -w '%{http_code}' -X POST "$url" \
      -H 'Content-Type: application/json' -d "$data")"
  else
    actual="$(curl -sS -o "$tmp_dir/response" -w '%{http_code}' "$url")"
  fi
  if [[ "$actual" != "$expected" ]]; then
    printf 'FAIL %s: expected HTTP %s, got %s\n' "$name" "$expected" "$actual" >&2
    cat "$tmp_dir/response" >&2
    exit 1
  fi
  printf 'PASS %s: HTTP %s\n' "$name" "$actual"
}

check_status 'frontend index' 200 "$BASE_URL/"
curl -sS "$BASE_URL/" >"$tmp_dir/index"
check_status 'SPA fallback' 200 "$BASE_URL/smoke-unknown-route"
curl -sS "$BASE_URL/smoke-unknown-route" >"$tmp_dir/route"
cmp -s "$tmp_dir/index" "$tmp_dir/route" || { echo 'FAIL SPA fallback body differs' >&2; exit 1; }
printf 'PASS SPA fallback body matches index\n'

product_id="$(date +%s%N)"
key="smoke-$product_id"
check_status 'sale initialization' 200 \
  "$BASE_URL/api/v1/orders/init?productId=$product_id&stock=1" POST '{}'
check_status 'GET stock' 200 "$BASE_URL/api/v1/orders/products/$product_id/stock"

order_body="{\"productId\":$product_id,\"quantity\":1,\"idempotencyKey\":\"$key\"}"
check_status 'place order' 202 "$BASE_URL/api/v1/orders" POST "$order_body"
order_id="$(sed -n 's/.*"orderId":"\([^"]*\)".*/\1/p' "$tmp_dir/response")"
[[ -n "$order_id" ]] || { echo 'FAIL place order: missing orderId' >&2; exit 1; }

terminal=''
for _ in $(seq 1 30); do
  curl -sS "$BASE_URL/api/v1/orders/$order_id" >"$tmp_dir/status"
  terminal="$(sed -n 's/.*"status":"\([^"]*\)".*/\1/p' "$tmp_dir/status")"
  if [[ "$terminal" == CONFIRMED || "$terminal" == CANCELLED ]]; then
    break
  fi
  sleep 1
done
[[ "$terminal" == CONFIRMED || "$terminal" == CANCELLED ]] || {
  echo "FAIL order terminal status: $terminal" >&2
  cat "$tmp_dir/status" >&2
  exit 1
}
printf 'PASS order terminal status: %s\n' "$terminal"

check_status 'duplicate idempotency key' 200 "$BASE_URL/api/v1/orders" POST "$order_body"
check_status 'out of stock' 409 "$BASE_URL/api/v1/orders" POST \
  "{\"productId\":$product_id,\"quantity\":1,\"idempotencyKey\":\"$key-out\"}"

check_status 'public actuator health blocked' 404 "$BASE_URL/actuator/health"
check_status 'public actuator metrics blocked' 404 "$BASE_URL/actuator/metrics"

printf 'Smoke test passed for productId=%s\n' "$product_id"
