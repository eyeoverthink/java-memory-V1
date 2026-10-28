# Northstar Kernel Memory And Forecast

## Purpose

This document is the canonical memory record for the current Fraynix offline kernel effort.

It exists for three reasons:

- to preserve what has actually been built, rather than what was only planned
- to give the system a stable narrative of its own construction
- to keep future self-improvement grounded in validated reality instead of drift, hype, or architectural wishfulness

## Core Goal

Build an air-gapped, Java-first, self-evolving kernel that can:

- plan and execute local work through typed interfaces
- store and retrieve local memory without network dependence
- verify its own work before promotion
- mutate itself through shadow execution and atomic promotion
- learn from successful repairs so future generations of the system are better than the last
- preserve a clean substrate boundary so the same intelligence core can target user-space, `f0_engine`, and later sovereign runtime backends

This is not a cloud wrapper and not a prompt-only illusion. The goal is an offline engineering organism with reproducible behavior and bounded self-modification.

## Governing Rules

- Offline operation is the default truth.
- Validation is the gate, not documentation.
- Self-editing is allowed only through shadow mutation plus verification.
- Typed interfaces matter more than personality metaphors.
- Biological naming can remain as strategy flavor, but the real system topology is functional, not theatrical.
- Bare-metal sovereignty remains in scope, but it does not justify skipping a working user-space core.
- Memory is not just storage. Memory must influence future planning, retrieval, repair, and evolution.

## What Has Been Built

### 1. Typed Offline Kernel

The runtime now centers on a typed flow:

- `TaskEnvelope -> Planner -> Executor -> Verifier -> Memory -> Mutation Engine`

This lives primarily in the kernel package and is anchored by:

- `OfflineKernel.java`
- `TaskEnvelope.java`
- `TaskPlanner.java`
- `KernelVerifier.java`
- `HybridMemoryStore.java`
- `ShadowMutationEngine.java`
- `MutationProposalEngine.java`

### 2. Reproducible Offline Verification

The system now treats reproducibility as a first-class invariant.

What exists:

- local offline verification manifests
- local PowerShell verification scripts
- no-dependency Java compilation and test verification path
- CLI entrypoints for status, profile, task, benchmark, replay, distill, propose, evolve, and memory refresh

Primary surfaces:

- `offline-toolchain.toml`
- `scripts/verify-offline-kernel.ps1`
- `scripts/run-offline-kernel.ps1`
- `OfflineKernelCli.java`

### 3. Hybrid Offline Memory

The system now has a hybrid retrieval layer that blends:

- lexical relevance
- graph locality
- HDC/VSA-style hypervector similarity
- recency

Important traits:

- local files can be ingested directly
- indexed state persists across restarts
- replay traces and generated memory artifacts can be re-indexed
- PDF content can be distilled into labeled local memory

Primary surfaces:

- `HybridMemoryStore.java`
- `KernelHypervectorSpace.java`
- `TraceReplayStore.java`
- `KernelStateStore.java`

### 4. Merkle Journal And Replay

The hot path no longer depends on proof-of-work or decorative chain logic. Instead, the system uses:

- an append-only Merkle-hashed journal
- replayable task traces
- persistent router state

This gives auditability, replay, rollback support, and post-run introspection without external infrastructure.

Primary surfaces:

- `MerkleAppendOnlyJournal.java`
- `TraceReplayStore.java`
- `.northstar-kernel/traces`

### 5. PDF Distillation And Corpus Memory

The system can now distill large local PDFs into labeled chunks and use those chunks in planning and mutation.

Implemented behavior:

- deduplicate overlapping PDF chunks
- label chunks as `spec`, `code-idea`, `test-idea`, `failure`, `research`, or `vision`
- store the distilled output locally
- use `failure` and `test-idea` chunks as mutation evidence

Primary surfaces:

- `PdfDistillationEngine.java`
- `PdfDistillationChunk.java`
- `OptionalPdfTextExtractor.java`
- `.northstar-kernel/distilled`

### 6. Shadow Mutation And Controlled Promotion

The mutation engine now supports:

- bounded file-level mutation operations
- shadow workspace previews
- verifier checks against the shadow workspace
- atomic promotion only after validation
- re-indexing of promoted files back into memory

Primary surfaces:

- `ShadowMutationEngine.java`
- `MutationCandidate.java`
- `MutationOperation.java`

### 7. Verifier Diagnostics

The verifier now emits structured diagnostics instead of only warning text.

Diagnostics include:

- tool label
- file
- line
- column
- severity
- message
- raw diagnostic body

This is the foundation for deterministic repair targeting.

Primary surfaces:

- `VerifierDiagnostic.java`
- `VerifierResult.java`
- `KernelVerifier.java`

### 8. Deterministic Semantic Repair

The kernel can now generate and validate bounded repairs for:

- missing imports
- method signature mismatch
- parser/brace failures
- illegal source artifacts and BOM noise
- missing methods
- missing fields
- public type and file-name mismatch
- package declaration and source-path mismatch

These repairs are verifier-guided, shadow-tested, and only promoted when validation succeeds.

### 9. Repair Memory

The system no longer forgets every successful repair.

It now persists:

- repair lessons
- repair templates

Lessons track what repair kind worked and in what context.

Templates capture successful construction patterns such as:

- method return type
- method parameter count
- field type
- static vs non-static repair shape

This means the system is not just remembering that a repair worked. It is learning how to build a better repair next time.

Primary surfaces:

- `RepairMemoryStore.java`
- `.northstar-kernel/repair-memory.tsv`
- `.northstar-kernel/repair-templates.tsv`

### 10. Template-Backed Repair Reuse

Repair memory now feeds directly into mutation generation for at least two important repair classes:

- missing method repairs
- missing field repairs

If the system has already validated a successful construction pattern, future proposals can reuse that pattern instead of falling back to weaker defaults.

This is a major shift:

- from passive scoring memory
- to active construction memory

That matters because the long-term goal is not only that the system repairs itself, but that it improves how it creates later versions of itself.

### 11. Project Memory Artifacts

The system now has a memory-refresh path that produces generated markdown artifacts under `.northstar-kernel/generated` and re-ingests them into the kernel memory store.

That creates a layered memory stack:

- canonical checked-in narrative
- generated runtime memory
- generated forecast memory
- replay traces
- Merkle journal
- repair lessons
- repair templates

Primary surfaces:

- `ProjectMemoryArtifactWriter.java`
- `OfflineKernel.refreshProjectMemoryArtifacts()`
- `OfflineKernelCli refresh-memory`

## What Has Been Verified

The no-dependency verifier has passed with tests covering:

- smoke behavior
- persistence
- substrate conformance
- validation suite generation
- corpus-guided evolution
- structured diagnostics
- verifier-guided parser repair
- missing import repair
- signature mismatch repair
- missing method repair
- missing field repair
- type-name alignment repair
- package alignment repair
- repair memory persistence
- repair template reuse
- project memory artifact generation and ingestion

This matters more than status prose. The system is not only described; it is exercised.

## Current Memory Stack

There are now several distinct memory layers:

### Canonical Narrative

- this file

### Generated Runtime Memory

- `.northstar-kernel/generated/SELF_BUILD_MEMORY.md`
- `.northstar-kernel/generated/SELF_BUILD_FORECAST.md`

### Persistent Index State

- `.northstar-kernel/memory-index.tsv`
- `.northstar-kernel/tracked-artifacts.txt`
- `.northstar-kernel/router-state.tsv`

### Distilled Corpus Memory

- `.northstar-kernel/distilled/*`

### Experience Memory

- `.northstar-kernel/traces/*`
- `.northstar-kernel/kernel-journal.log`

### Repair Memory

- `.northstar-kernel/repair-memory.tsv`
- `.northstar-kernel/repair-templates.tsv`

## What Is Not True Yet

The system is stronger than before, but there are still important boundaries:

- It is not a finished autonomous general intelligence.
- It is not yet a full self-rewriting engineering organism across the entire codebase.
- It is not yet broadly template-backed across every repair family.
- It does not yet prove sovereign runtime control beyond the current substrate boundary and `f0_engine` path.
- Rust-side verification still depends on the local machine having `cargo` available.
- PDF ingestion is useful but still pragmatic rather than perfect.
- The older multi-brain architecture files still exist in the repo, but they are not the primary runtime truth.

## Immediate Goal

Keep shifting the system from heuristic self-repair toward validated self-construction.

That means:

- more repair families should become template-backed
- generated tests should become reusable construction memory too
- successful architecture upgrades should leave behind better memory than the system had before
- the system should learn the difference between heuristic fallback and proven repair patterns

## Near-Term Forecast

- Extend template-backed reuse into signature repairs and regression test generation.
- Record mutation benchmark outcomes so the system can measure whether template reuse improves solve rate.
- Create clearer separation between architectural memory, repair memory, and corpus memory while still letting them cooperate.
- Improve CLI and artifact surfaces for memory inspection and refresh.

## Mid-Term Forecast

- Add repair-template families for unresolved methods on foreign types and more precise symbol disambiguation.
- Teach the kernel to turn more verifier failures into bounded, test-backed source edits.
- Use repair-memory evidence to bias evolution selection earlier and more strongly.
- Expand replay analysis so the system can mine its own failed and successful trajectories.

## Long-Term Forecast

- Move from repair-template reuse to broader self-construction templates.
- Let validated local memory guide architecture growth without bypassing shadow validation.
- Preserve one intelligence core across multiple execution substrates.
- Make the system capable of building later versions of itself to at least the current level, and then beyond it, while remaining offline and reproducible.

## Guidance For Future Work

- Do not replace validation with confidence.
- Do not let the system forget its own successful construction patterns.
- Do not treat generated docs as decoration; ingest them so they influence retrieval.
- Prefer building small deterministic loops that compound over time.
- When the system improves itself, wire the improvement into the generation path, not only into reporting.

## Bottom Line

The system is no longer only a concept. It now has:

- a typed offline kernel
- reproducible validation
- hybrid local memory
- replay and journal history
- PDF distillation
- verifier-guided repair
- repair lesson persistence
- repair template reuse
- generated project memory artifacts

The next milestone is not just more features.

It is this:

make the system better at producing the next version of the system.
