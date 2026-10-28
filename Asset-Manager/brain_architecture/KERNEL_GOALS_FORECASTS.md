# Fraynix Kernel — Goals & Forecasts

**Label**: spec  
**Type**: goals-and-forecasts  
**Date**: April 2026

**Evidence tags**: `[VERIFIED]` = reproducible in current workspace, `[SCAFFOLD]` = partial integration or boundary-only support, `[SPECULATIVE]` = goal or forecast.

---

## [SPECULATIVE] Core Goal

Build the first air-gapped, self-improving, sovereign AI kernel that:
- Runs entirely offline with zero cloud or internet dependency
- Uses only free, public-domain mathematics
- Learns from its own repair history
- Evolves its own construction patterns
- Rewrites itself safely through validated shadow mutation
- Requires no proprietary libraries or external APIs

---

## [VERIFIED] What Is Already Working (April 2026)

| Capability | Status |
|-----------|--------|
| Typed offline task pipeline | ✅ Live |
| Hybrid memory (lexical + HDC + graph + recency) | ✅ Live |
| Thompson sampling bandit routing | ✅ Live |
| Shadow mutation with rollback | ✅ Live |
| Structured verifier diagnostics (javac parser) | ✅ Live |
| Missing import repair | ✅ Live |
| Method stub generation | ✅ Live |
| Field declaration generation | ✅ Live |
| Signature mismatch repair | ✅ Live |
| Type/file name alignment | ✅ Live |
| Package declaration repair | ✅ Live |
| Repair lesson memory (persistent) | ✅ Live |
| Repair template library (persistent) | ✅ Live |
| PDF distillation (labeled corpus chunks) | ✅ Live |
| Corpus-guided mutation proposals | ✅ Live |
| Merkle append-only journal | ✅ Live |
| Cross-restart state persistence | ✅ Live |
| Task replay store | ✅ Live |
| Operator CLI | ✅ Live |
| UserSpace substrate adapter | ✅ Live |
| f0_engine (Rust) substrate boundary | ✅ Defined |

---

## [SPECULATIVE] Near-Term Goals (Next 3–5 Sessions)

### [SPECULATIVE] 1. Full Source Synthesis from Verifier Failures
**What**: Generate whole class implementations from failing tests, not just surgical patches  
**Why**: Turns the kernel from "can fix broken syntax" into "can build new components"  
**How**: Template expansion + token substitution from corpus spec chunks  
**Metric**: Kernel generates a passing class from a failing test with no human input

### [SCAFFOLD] 2. Rust f0_engine Live Execution
**What**: Wire `F0RuntimeSubstrateAdapter` to the real compiled Rust binary  
**Why**: Opens the path to bare-metal sovereign substrate  
**How**: Build f0_engine with `cargo build`, wire output to adapter  
**Blocker**: `cargo` must be on PATH  
**Metric**: Substrate conformance tests pass on both Java and Rust adapters

### [SPECULATIVE] 3. Repair Memory Generalization
**What**: Extend templates beyond method/field stubs to: import patterns, class skeletons, interface implementations  
**Why**: More failures become template-backed rather than heuristic  
**Metric**: 80%+ of repair proposals reference a stored template

### [SPECULATIVE] 4. Causal Memory Integration
**What**: Wire `fraymus/causal/` (CausalModel, DAGVerifier, DoCalculusEngine) into hybrid retrieval scoring  
**Why**: Memory retrieval becomes causally grounded — penalize hits with no causal path to the goal  
**How**: During search, check causal model; reduce score for spurious correlations  
**Metric**: Retrieval precision improves on tasks with established causal relationships

### [SPECULATIVE] 5. Speech Recognition Fix
**What**: Fix the `WinError 1314` privilege error in the Fraynix CLI audio pipeline  
**Why**: The system cannot use audio input as a modality  
**How**: Replace symlink creation with direct file copy in the Python model loader OR run as Administrator  
**Root cause**: `shutil.copy2` with symlinks requires `SeCreateSymbolicLinkPrivilege`  
**Fix path**: Change `os.symlink` → `shutil.copytree` in the huggingface model cache path logic

---

## [SPECULATIVE] Medium-Term Goals (1–3 Months)

### [SPECULATIVE] 6. Self-Modifying Loop
**What**: Kernel proposes and applies changes to its own source code  
**Gate**: Every self-edit must pass full test suite before promotion (already enforced by shadow engine)  
**Risk mitigation**: Shadow workspace + atomic promotion + rollback already in place  
**Metric**: At least one verified self-improvement to a kernel component per week of operation

### [SPECULATIVE] 7. Multi-Modal Ingestion
**What**: Add audio and image ingestion alongside existing PDF and text  
**Path**: Audio → after speech fix; Images → OpenCV adapter as optional substrate  
**Metric**: System can build causal memory from code + docs + audio + images

### [SPECULATIVE] 8. Continuous Learning from Traces
**What**: Mine stored task replay traces to discover new repair templates and routing improvements  
**Why**: Every past run is a lesson — currently unused for self-improvement  
**How**: Trace miner extracts successful repair sequences, feeds to RepairMemoryStore  
**Metric**: Routing success rate improves over 30-day operational window

---

## [SPECULATIVE] Long-Term Goals (3–12 Months)

### [SPECULATIVE] 9. Bare-Metal Sovereignty
**What**: Run the kernel on a minimal boot target with no OS  
**Path**: f0_engine → minimal Rust runtime → bare-metal bootloader  
**Gate**: `SubstrateAdapter` interface is already defined; just need a new implementation  
**Metric**: Kernel boots and executes a task trace without a host OS

### [SPECULATIVE] 10. True Self-Building
**What**: Kernel generates its own next version from its accumulated lessons and templates  
**What it means**: The system that was built with AI assistance can build its next version by itself  
**Metric**: Version N+1 of the kernel is generated and validated by version N with no human code

---

## [SPECULATIVE] What Success Looks Like

**6 months**: The kernel repairs its own source from verifier failures, improves its own routing from traces, and operates fully offline with zero human code intervention for maintenance tasks.

**12 months**: The kernel generates a new version of itself from its own lessons. That version is measurably better on benchmark tasks.

**Long term**: Bare-metal sovereign runtime. No OS. No internet. No external dependencies. The system your creator built.

---

## [VERIFIED] Key Constraints (Non-Negotiable)

1. **Offline first** — every core capability works with zero network calls
2. **Shadow before live** — no direct edits to live workspace ever
3. **Validate before promote** — the verifier is always the gate
4. **Free math only** — VSA/HDC, Thompson sampling, TF-IDF, Merkle — all public domain
5. **Minimal dependencies** — every library must justify its presence; decorative dependencies are removed
