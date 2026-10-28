# Open Model Absorption Log

## 2026-05-20T00:00:00Z (workspace local session)
- Independent analysis completed for:
  - `F:/Full-AI-System/L1B3RT4S-main/L1B3RT4S-main`
  - `F:/Full-AI-System/jailbreak-autoresearch-main/jailbreak-autoresearch-main`
- Decisions:
  - L1B3RT4S: curated metadata lane only, raw dump defer.
  - jailbreak-autoresearch: operational docs/model roster ingested now.
- FraynixBoot defaults verified and patched to include curated packs.
- Offline kernel ingestion state moved:
  - `trackedArtifacts: 85 -> 96`
  - `documents: 128 -> 139`
- FraynixBoot runtime check:
  - `openmodel status` reported `loaded=4` with curated packs present.

## Pending on next pass
- Re-run signal extraction script and ingest generated summaries.
- Refresh kernel memory surfaces and append updated counts here.
## 2026-05-20T03:19:09Z (signal extraction + memory refresh)
- Ran scripts/refresh-open-model-absorption.ps1 against F:/Full-AI-System.
- Generated artifacts:
  - Java-Memory/Asset-Manager/skills/open_model_sources/generated/open_model_signal_summary.md
  - Java-Memory/Asset-Manager/skills/open_model_sources/generated/L1B3RT4S-main-model-signals.json
  - Java-Memory/Asset-Manager/skills/open_model_sources/generated/jailbreak-autoresearch-main-model-signals.json
- Kernel ingest delta from this pass:
  - documents: 139 -> 144
  - 	rackedArtifacts: 96 -> 101
- Ran un-offline-kernel.ps1 refresh-memory F:/Full-AI-System to update generated self-build memory artifacts.
## 2026-05-20T03:45:08Z (persistent open-model status patch)
- Updated FraynixBoot open-model ingestion to self-reference persisted Akashic entries.
- Added identity-key dedupe (path + sha256) to avoid re-absorbing duplicates across restarts.
- Added directory expansion for curated lanes (skills/open_model_sources, generated, autoresearch root) with extension and max-size guardrails.
- Added session vs persisted counters in openmodel status:
  - sessionSeen, sessionNewSources, sessionNewSkills, persistedSources, persistedSkills.
- Verification run:
  - First status after boot: sessionSeen=9, sessionNewSources=3, persistedSources=8.
  - After openmodel refresh: sessionNewSources=0, persisted counts unchanged (dedupe confirmed).
