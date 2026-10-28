# Kronos Relationship Quality Upgrade (2026-05-20)

## Scope
This update hardens `AeonKronos` relationship learning without deleting learned relationships.

## Baseline (before patch)
- Relationship growth was capped but purely volume-based.
- Alias vectors could duplicate heavily.
- `status` reported counts but not relationship quality or utility.

## What Changed
1. Non-destructive alias dedupe
- Added vector-signature dedupe in `addAlias(...)`.
- Duplicate vectors no longer inflate alias storage.
- Duplicate re-observations increase alias strength instead of being dropped silently.

2. Quality-tier model (computed, not destructive)
- Added quality tiers: `BOOTSTRAP`, `PROMOTED`, `CORE`.
- Tier is derived from alias `strength` and `usage`.
- Existing relationships are preserved; quality is metadata over them.

3. Utility telemetry
- Added live counters:
  - `aliasHits`
  - `conceptHits`
  - `noise`
  - `promotions`
  - `duplicate vectors suppressed`
- Added `Alias Quality` and `Alias Utility` rows to `kronos status`.

4. Index rebuild on restore
- Added alias index/signature/strength rebuild after state restore so quality math is available after reboot.

## Verification
- `gradlew.bat compileJava --console=plain` passed.
- `AeonKronos` REPL smoke run with `STATUS` showed new telemetry lines:
  - `Alias Quality: core=..., promoted=..., bootstrap=...`
  - `Alias Utility: aliasHits=..., conceptHits=..., noise=..., promotions=...`
  - `Alias Dedupe: ... duplicate vectors suppressed`

## Design Intent
- Keep all valid learned relationship structure.
- Improve signal quality and observability.
- Avoid runaway memory growth from alias duplication.

## Next Suggested Steps
1. Add per-concept quality summaries (`top concepts by promoted/core aliases`).
2. Persist utility counters (`aliasHits`, `promotions`) across restarts.
3. Add a Kronos micro-test for dedupe and tier transitions.

## Persistence Hardening (follow-up)
- Fixed restore corruption path where large persisted Kronos payloads were not reconstructed (symptom: `Persisted 264 concepts...` then `Restored 0 concepts...`).
- Replaced brittle string-split restore logic with structured `FraymusJSON` parsing for concepts, bindings, and aliases.
- Restore now selects the richest valid Akashic payload per key, not just the last record.
- Added local snapshot fallback file:
  - `genesis_vault/kronos_state_snapshot.json`
  - Guarded write: a smaller session will not overwrite a richer snapshot.
  - Guarded read: if snapshot is richer than Akashic restore, snapshot is promoted automatically.
