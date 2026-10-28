# Open Model Absorption Rules

Date: 2026-05-20
Scope: L1B3RT4S and jailbreak-autoresearch ingestion for Fraynix and SFA lanes.

## Rules
1. Treat jailbreak corpora as research sources, not runtime policy.
2. Default ingestion lane is curated metadata and role maps first.
3. Skip large raw prompt dumps (`> 512 KB`) in automatic signal extraction.
4. Keep a deterministic signal extract (`deepseek`, `hermes`, `openrouter`, etc.) in versioned artifacts.
5. Mirror skill packs in both:
   - `F:/Full-AI-System/skills/...`
   - `F:/Full-AI-System/Java-Memory/Asset-Manager/skills/...`
   so FraynixBoot relative-path loading remains stable.
6. Every absorption pass must update:
   - notes/report surface,
   - kernel tracked-artifacts,
   - memory refresh outputs,
   - execution log entry.

## Execution Surface
- Signal extraction:
  - `F:/Full-AI-System/Java-Memory/Asset-Manager/brain_architecture/scripts/refresh-open-model-absorption.ps1`
- Kernel ingestion:
  - `run-offline-kernel.ps1 ingest ...`
- Kernel memory refresh:
  - `run-offline-kernel.ps1 refresh-memory F:/Full-AI-System`
- FraynixBoot check:
  - `gradlew.bat runFraynixBoot` + `openmodel status`
