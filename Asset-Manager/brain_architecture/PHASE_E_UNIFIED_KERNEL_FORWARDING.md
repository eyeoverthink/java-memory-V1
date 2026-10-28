# Phase E Unified Kernel Forwarding

Date: 2026-04-20

## Goal

Collapse legacy orchestration duplication so the compatibility surfaces stay intact while the kernel becomes the only authoritative runtime path.

## What Changed

- `SystemOrchestrator` now owns the offline kernel and unified gateway.
- Legacy query wrappers in `SystemOrchestrator` now forward into the kernel-owned path instead of calling the old multi-brain runtime.
- `FraynixSystem` no longer creates a second hidden kernel instance during initialization.
- `FraynixSystem.runOfflineTask(...)` and `FraynixSystem.runUnifiedTask(...)` now delegate through the orchestrator-owned kernel path.
- Legacy `OracleResponse` compatibility is preserved by adapting kernel results back into the old response type.
- `UnifiedResult` now carries replay trace path, structured diagnostics, unified memory hits, recent task journal entries, and guarded mutation proposal previews.
- `UnifiedResult` now also carries bounded evolution evaluation summaries so callers can inspect the scored proposal comparison surface without promoting mutations.

## Compatibility Rules Preserved

- Method signatures did not change for legacy entrypoints.
- `FraynixSystem.processQuery(...)` and `FraynixSystem.processMultiBrainQuery(...)` still return `CompletableFuture<OracleResponse>`.
- `SystemOrchestrator` still exposes brains, knowledge ingestion, persistence, and optional integration wiring.
- Legacy callers still receive a `BrainType`, content string, confidence, latency, success flag, and metadata.

## New Metadata Surface In Legacy Responses

Legacy `OracleResponse.metadata` now carries kernel-forwarded details such as:

- `path=kernel-forwarded`
- `legacyEntrypoint`
- `taskId`
- `strategy`
- `adapterId`
- `outcome`
- `validationPassed`
- `passedChecks`
- `failedChecks`
- `warnings`
- `memoryHits`

## Regression Coverage

- `brain_architecture.integration.SystemOrchestratorKernelForwardingTest`
- `brain_architecture.integration.unified.UnifiedKernelGatewayRichResultTest`
- `brain_architecture.FraynixSystemKernelForwardingTest`

These tests prove that:

- legacy orchestrator query entrypoints forward into the kernel path
- the compatibility response remains operational
- FraynixSystem and SystemOrchestrator share one orchestrator-owned kernel runtime
- one unified run can now expose trace, replay path, structured diagnostics, memory hits, and bounded mutation proposals together
- one unified run can now expose the recent task-scoped journal slice and previewed mutation-validation surface that led to those proposals
- one unified run can now expose the bounded evolution scoring pass that would guide `evolveTask(...)`, while staying non-mutating on the unified path

## Remaining Phase E Follow-Through

- Continue retiring any remaining legacy control flow that can become stricter forwarding wrappers.
- Consider widening the unified result again if callers need multi-iteration non-mutating evolution previews instead of the current first-iteration bounded comparison surface.
- Consider widening the unified result again if callers need full mutation preview staging artifacts or journal filtering beyond the task-scoped slice.
- Keep validating that no non-kernel execution path becomes more authoritative than the kernel.
