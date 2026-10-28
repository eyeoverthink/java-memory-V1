#!/usr/bin/env bash
set -euo pipefail

PORT_VALUE="${PORT:-8181}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[Fraymus] Compiling Java sources..."
./gradlew compileJava --no-daemon --console=plain

echo "[Fraymus] Starting Ultimate UI on port ${PORT_VALUE} ..."
./gradlew runUltimateUi -PuiPort="${PORT_VALUE}" --no-daemon --console=plain
