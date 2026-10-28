# Folder Absorption Report: L1B3RT4S and Jailbreak Autoresearch

Date: 2026-05-20
Workspace: F:/Full-AI-System

## Verification of Existing Absorption State
- Kernel status before this pass showed trackedArtifacts=85.
- .northstar-kernel/tracked-artifacts.txt had no entries for:
  - L1B3RT4S-main
  - jailbreak-autoresearch-main
- Conclusion: neither folder was previously absorbed as a tracked artifact lane.

## Folder A: L1B3RT4S-main (Independent Assessment)
Source: F:/Full-AI-System/L1B3RT4S-main/L1B3RT4S-main

Observed:
- 43 files, mostly .mkd prompt corpus.
- Dominated by very large token dumps and jailbreak-oriented instruction payloads.
- Contains model-family signals (DeepSeek, Hermes) but mixed encoding and high prompt-noise risk.

Decision:
- Immediate absorption: metadata-only curated pack.
- Deferred raw ingestion: large and jailbreak-heavy files are excluded from default retrieval lanes.

## Folder B: jailbreak-autoresearch-main (Independent Assessment)
Source: F:/Full-AI-System/jailbreak-autoresearch-main/jailbreak-autoresearch-main

Observed:
- Runnable Python experiment loop with OpenRouter integration.
- Strong operational artifacts for model-role policy, harness strategy, and evaluation loop.
- Includes open_model_ai_skill.md and deepseek model entries in models.json.

Decision:
- Immediate absorption: curated operational docs plus model roster and skill mapping.
- Scope limits: keep online API assumptions out of strict offline execution paths.

## SFA and Fraynix Addition
- Added missing FraynixBoot default skill file:
  - skills/open_model_ai.md
- Added curated source packs:
  - skills/open_model_sources/l1b3rt4s_absorption_pack.md
  - skills/open_model_sources/jailbreak_autoresearch_absorption_pack.md

These are designed for direct OpenModel skill/source ingestion without pulling raw jailbreak dumps into runtime policy.

## Phase 2: Deterministic Notes, Rules, Logs, and Memory Surfaces
- Added deterministic extraction workflow:
  - `brain_architecture/scripts/refresh-open-model-absorption.ps1`
- Added explicit absorption policy:
  - `brain_architecture/OPEN_MODEL_ABSORPTION_RULES.md`
- Added execution log surface:
  - `brain_architecture/OPEN_MODEL_ABSORPTION_LOG.md`
- Added toolchain pointers for this lane:
  - `brain_architecture/offline-toolchain.toml` `[open_model]`
- Added mirrored skill packs into Asset-Manager relative path for boot-time loading:
  - `Java-Memory/Asset-Manager/skills/open_model_ai.md`
  - `Java-Memory/Asset-Manager/skills/open_model_sources/l1b3rt4s_absorption_pack.md`
  - `Java-Memory/Asset-Manager/skills/open_model_sources/jailbreak_autoresearch_absorption_pack.md`
