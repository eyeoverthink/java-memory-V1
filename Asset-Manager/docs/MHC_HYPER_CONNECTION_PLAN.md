# MHC Hyper-Connection Plan

## Current build

The live router path now uses an MHC-oriented preset inside
`fraymus.neural.MoERouter`:

- multi-plane replicated streams
- Sinkhorn / Birkhoff style doubly-stochastic routing
- widened residual fusion
- bounded signal magnitude instead of blanket dampening
- capacity tracking for heavy-task paths
- `MHC-27B` preset metadata

This is the control-plane version of the architecture. It upgrades routing,
stream fusion, and stability without pretending the Java runtime is already a
full GPU training stack.

## What this enables

- higher-capacity multi-stream routing without collapsing to one expert path
- row/column mass conservation close to `1`
- sideways residual mixing across planes
- bounded outputs for stable long chains
- a clean place to attach heavier MOE/MOA orchestration later

## Next stage

The next stage is the real training/runtime substrate:

1. custom GPU kernels
   - Tailang kernel dialect
   - fused gating, residual, and normalization kernels
   - memory-bandwidth-aware layouts

2. VRAM reduction
   - selective recomputation
   - dual-pipe execution
   - wider internal width with lower activation residency

3. heavy-task operating mode
   - preserve stream capacity under reasoning load
   - widen residual bandwidth for logic and memory tasks
   - avoid damping away expert specialization

4. model-scale target
   - `27B`-class internal routing budget
   - MHC-first stream planning
   - stable sum-to-one transport at the routing layer

## Constraint

The Java repo can implement the routing math and orchestration contracts now.
It does not yet implement the actual custom GPU kernel compiler or training
runtime. That should be built as a dedicated lower-level subsystem and wired
back into the MHC preset already added here.
