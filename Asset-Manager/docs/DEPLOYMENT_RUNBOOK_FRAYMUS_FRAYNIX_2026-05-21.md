# Deployment Runbook: Fraymus / Fraynix (2026-05-21)

## Objective
This runbook provides two production deployment lanes for the current project:
- Lane A: UI-only online demo
- Lane B: Full runtime service (backend + state + model calls)

It is based on the current workspace reality:
- HTML surface: `Fraymus_New_One.html`
- Java runtime project: `Java-Memory/Asset-Manager`
- Runtime UI server task: `runUltimateUi`
- Boot shell task: `runFraynixBoot`

---

## Lane A: UI-Only Demo Deploy (Fastest)

### What this is
Host only the front-end demo page online, no Fraynix backend execution.

### Source
- `F:\Full-AI-System\Java-Memory\Asset-Manager\Fraymus_New_One.html`

### Prepare publish artifact
1. Create static publish folder and index file.
```powershell
New-Item -ItemType Directory -Force -Path F:\Full-AI-System\publish\fraymus-ui | Out-Null
Copy-Item -LiteralPath F:\Full-AI-System\Java-Memory\Asset-Manager\Fraymus_New_One.html -Destination F:\Full-AI-System\publish\fraymus-ui\index.html -Force
```

2. Verify locally.
```powershell
cd F:\Full-AI-System\publish\fraymus-ui
python -m http.server 8080
```
Open `http://localhost:8080`.

### Replit deployment (Static)
- Deploy folder contents as static site (or use a simple web command in Reserved VM if static deployment is unavailable in your flow).
- If command-based hosting is needed:
```bash
python3 -m http.server $PORT --bind 0.0.0.0 --directory /home/runner/workspace/publish/fraymus-ui
```

### Required hardening before public link
- Label non-validated metrics as `SIMULATED`.
- Remove print footer noise from published screenshots/PDFs.
- Add visible build stamp (version + timestamp + commit id if available).

---

## Lane B: Full Runtime Deploy (Online service)

### What this is
Host Java runtime service with API/UI and persisted state.

### Recommended runtime entrypoint
Use `runUltimateUi` for online hosting (HTTP server).

- Task exists in `build.gradle`:
  - `runUltimateUi`
  - main class: `fraymus.ultimate.UltimateUiServer`
  - optional port arg via `-PuiPort`

### Local verification
```powershell
cd F:\Full-AI-System\Java-Memory\Asset-Manager
.\gradlew.bat compileJava --console=plain
.\gradlew.bat runUltimateUi -PuiPort=8181 --console=plain
```
Open `http://localhost:8181`.

### Replit Reserved VM deployment command
Use always-on VM deployment for runtime lane:
```bash
cd /home/runner/workspace/Java-Memory/Asset-Manager
./gradlew runUltimateUi -PuiPort=$PORT --no-daemon --console=plain
```

### VPS/Linux deployment command
```bash
cd /opt/full-ai-system/Java-Memory/Asset-Manager
./gradlew compileJava --no-daemon --console=plain
./gradlew runUltimateUi -PuiPort=${PORT:-8181} --no-daemon --console=plain
```

### Optional Fraynix shell lane (interactive)
`runFraynixBoot` is interactive and not ideal as public web runtime. Keep it internal/admin:
```powershell
.\gradlew.bat runFraynixBoot --console=plain
```

If needed with explicit open-model source override:
```powershell
.\gradlew.bat runFraynixBoot -PopenModelSource="F:\Full-AI-System\FRAYNIX_KNOWLEDGE_CORPUS\open_model_AI.pdf" --console=plain
```

---

## Production Layout

### Minimum structure
- `/publish/fraymus-ui` (Lane A static)
- `/Java-Memory/Asset-Manager` (Lane B runtime)
- `/Java-Memory/Asset-Manager/genesis_vault` (runtime snapshots/state)
- `/Java-Memory/Asset-Manager/.akashic` (knowledge persistence)

### Persisted state you must keep across deploys
- `.akashic/`
- `genesis_vault/`
- `.northstar-kernel/` (if used by runtime workflows)

---

## Security & Safety Gates

1. Do not expose raw shell/execute endpoints publicly without auth.
2. Restrict runtime mutation commands to authenticated operator role.
3. Separate public demo surface from privileged operator surface.
4. Add rate limits on chat/execute endpoints.
5. Add audit logs for command execution, model calls, and state writes.

---

## Observability Checklist

- Startup log includes active port and route map.
- Health endpoint responds in < 1s under normal load.
- Track:
  - boot success/fail count
  - request error rate
  - JVM memory pressure
  - persistence save/restore status

---

## Go-Live Test Sequence

1. Cold boot service.
2. Open UI route.
3. Execute one operator API action.
4. Restart service.
5. Verify persistence restored (`.akashic` and `genesis_vault` state).
6. Verify logs contain no uncaught exceptions.

---

## Decision Guidance

Use Lane A if you need immediate public visibility.
Use Lane B if you need real online Fraynix behavior and persistence.
Run both in parallel for best outcome: public demo + private operator runtime.

---

## Helper Scripts Added

- Local Windows startup:
  - `scripts/run-ultimate-ui-local.ps1`
  - Usage:
    ```powershell
    cd F:\Full-AI-System\Java-Memory\Asset-Manager
    .\scripts\run-ultimate-ui-local.ps1 -Port 8181
    ```

- Runtime Linux/Replit startup:
  - `scripts/run-ultimate-ui-runtime.sh`
  - Usage:
    ```bash
    cd /home/runner/workspace/Java-Memory/Asset-Manager
    bash scripts/run-ultimate-ui-runtime.sh
    ```
    (`PORT` env var is respected; defaults to `8181`.)
