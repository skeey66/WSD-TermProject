#!/usr/bin/env bash
set -euo pipefail

echo "[1/4] Pull latest code"
git pull --rebase

echo "[2/4] Ensure .env exists (DO NOT commit it)"
test -f .env || { echo "ERROR: .env not found. Copy from .env.example and fill secrets."; exit 1; }

echo "[3/4] Build & run containers"
docker compose up -d --build

echo "[4/4] Health check"
sleep 3
curl -sS "http://127.0.0.1:${SERVER_PORT:-8080}/health" | cat
echo
