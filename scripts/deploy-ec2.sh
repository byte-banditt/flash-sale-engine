#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE=(docker compose --env-file "$ROOT_DIR/.env" -f "$ROOT_DIR/docker-compose.prod.yml")

if [[ ! -f "$ROOT_DIR/.env" ]]; then
  printf '%s\n' "Missing $ROOT_DIR/.env; copy .env.example and set private values." >&2
  exit 1
fi

cd "$ROOT_DIR"
"${COMPOSE[@]}" config >/dev/null
JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw -DskipTests package
"${COMPOSE[@]}" build
"${COMPOSE[@]}" up -d --remove-orphans --scale app=1

app_id="$("${COMPOSE[@]}" ps -q app)"
deadline=$((SECONDS + 180))
until [[ "$(docker inspect --format '{{.State.Health.Status}}' "$app_id" 2>/dev/null || true)" == "healthy" ]]; do
  if (( SECONDS >= deadline )); then
    printf '%s\n' 'The first app replica did not become healthy before scaling.' >&2
    exit 1
  fi
  sleep 2
done

"${COMPOSE[@]}" up -d --remove-orphans --scale app=2
"${COMPOSE[@]}" ps
printf '%s\n' 'Deployment started. Verify http://<instance-ip>/ and API behavior before traffic cutover.'
