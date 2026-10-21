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
"${COMPOSE[@]}" up -d --remove-orphans --scale app=2
"${COMPOSE[@]}" ps
printf '%s\n' 'Deployment started. Verify http://<instance-ip>/ and API behavior before traffic cutover.'
