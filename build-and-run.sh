#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

build_service() {
  local service_dir="$1"
  echo "Building $(basename "$service_dir")..."
  (cd "$service_dir" && ./mvnw -B -DskipTests package)
}

build_service "$SCRIPT_DIR"
build_service "$SCRIPT_DIR/../order-service"
build_service "$SCRIPT_DIR/../notification-service"

cd "$SCRIPT_DIR"
docker compose up --build -d
docker compose ps
