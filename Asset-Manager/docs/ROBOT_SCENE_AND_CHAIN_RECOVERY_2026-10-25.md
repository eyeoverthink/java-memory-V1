# Fraynix scene, robot feedback and knowledge-record recovery

Fraynix - By Vaughn Scott. Evidence date: 2026-10-25.

## The idea and its actual implementation

The owner's idea is sound: an AI can reason about a physics scene through a
structured observation. Gson serializes objects into JSON; deserialization
reconstructs typed objects from JSON. This is useful perception through a data
interface. It does not imply that a text model receives rendered camera pixels.

The saved project label resolved to the older source under
`C:/Users/eyeka/Downloads/physics_mario/physics_mario`. The working Ollama scene
integration was found in the sibling `Vaughn-Scotts-Fraymus-Engine` project.
That editor already had substantial real implementation: SceneKnowledge,
SceneAgent, SceneProposal, FactorySession, SceneSimulation, scene checkpointing,
registered component deserialization and JBox2D. This change reuses that code.

The working source is preserved under `integrations/physics-scene` at the
workspace root: 132 files, 858,497 bytes, individually SHA256 checked against
the working editor. Its `source-intake.json` records included files and omitted
builds, checkout metadata, generated module bundles and machine UI state. The
shared factory-runtime, factory-kernel and factory-models remain canonical under
Asset-Manager. Existing third-party notices and the JBox2D library are retained.

## End-to-end wiring

```text
Fraynix shell or explicit [SPECIALIST: robot request.json]
  -> ModelSpecialists in the actual UnifiedBrainOrchestrator
  -> SpecialistModelService -> RobotPhysicsBridge
  -> RobotSceneWorker -> existing FactorySession / Scene / JBox2D
  -> complete before/after SceneKnowledge JSON and immutable command checkpoint
  -> existing WorldModel and EmbodiedAI transition feedback
  -> HDC memory / Kronos / AkashicRecord evidence
  -> verified robot-brains and specialist-profile digital-genome capsules
```

Scene observations include stable UUIDs, registered components and properties,
transforms, spatial bounds, runtime body positions and velocities, body count,
physics settings, camera and coordinate conventions. Local model planning uses
the existing SceneAgent and FactoryModelService, not a second inference stack.

The world/body brains receive an explicitly lossy numeric feature mapping:
selected x/y, entity/body counts and deterministically ordered object positions.
Coordinates are normalized with tanh(x/64); counts use tanh(count/100). This is
declared as fraynix.scene-features.v1. The complete JSON remains available to
the scene model; a fixed-size feature vector is not a lossless scene embedding.

The scene worker is headless and operates on its own checkpoint copy. It shares
the editor's real scene and physics classes. It does not inject actions into a
running OpenGL/render thread. Import reads original scene JSON or checkpoints;
tests verify that the original user checkpoint stays byte-for-byte unchanged.

## Commands and creation contracts

Build and configure from the workspace root with cached Gradle dependencies:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\Java-Memory\Asset-Manager\scripts\specialists\build-physics-bridge.ps1 -Configure
```

The script runs the editor and shared-module checks, writes the runtime
classpath, and preserves existing configuration fields while setting
physics_root in the user's specialist-runtime.json. A fresh machine needs its
declared Java/Gradle dependencies provisioned before an offline build.

In the original Fraynix shell:

```text
specialist describe fraymus-scene
specialist robot data/specialists/examples/scene-observe.json
specialist robot data/specialists/examples/scene-plan.json
specialist chain data/specialists/examples/chain-audit.json
```

Robot requests select backend `fraymus-scene`. Operations are:

| Operation | Behavior |
| --- | --- |
| observe / query | Read the actual scene or run existing spatial queries |
| plan | Send complete scene JSON to a selected installed local model; return an answer or validated proposal |
| preview | Stage a proposal and run detached JBox2D validation; do not commit |
| apply | Validate fixed SceneProposal operations, then commit the candidate scene |
| import | Read source_scene_json or source_checkpoint into the separate environment |
| control | Set velocity on a registered non-static body, then simulate actual steps |
| step | Advance the environment without a new motor assignment |
| stop / resume | Persist a stop latch or explicitly clear it |

Mutation requests require a command_id. Apply, import, control and step require
the expected_scene_hash from the latest observation. Brain-inferred control
captures and freezes its own observation/hash before dispatch. Reuse the EXACT
same request and command_id after an uncertain result; a different request with
the same identity is rejected. Receipts and pending checkpoints reconcile
interruptions before or after scene commit without stepping twice.

Control requests include entity_id, positive speed_scale,
motor_units=`world-units-per-second`, and seconds in (0,60]. Two
normalized_motor channels in [-1,1] are optional. Omitting them invokes the
already wired brains; target_position can add an explicit proportional position
controller. World units are the editor's units, not an assumed SI calibration.

Example after substituting an observed UUID and hash:

```json
{
  "backend": "fraymus-scene", "operation": "control", "command_id": "move-001",
  "entity_id": "<observed UUID>", "expected_scene_hash": "<observed sceneHash>",
  "normalized_motor": [0.5, 0], "speed_scale": 2,
  "motor_units": "world-units-per-second", "seconds": 0.25, "reward": 0
}
```

Creation proposals require kind=proposal, the exact supplied observationId
string, a nonempty summary, and operations. A create operation uses a unique
internal key matching `[A-Za-z][A-Za-z0-9_]*`; display names may contain spaces.
Create has no entityId; the engine assigns it. op is a string, never an envelope
object. Later operations can refer to a newly created key as `$key`.

The scene instructions now contain a concrete create example. Rejected model
output is retained in the audit log, while the next correction receives the
complete frozen scene, original goal and validation error without replaying
malformed assistant examples. The validator remains strict.

An optional `structured_output: true` plan field passes the full output schema
through the real Ollama adapter. The schema fixes the observation ID and the
supported operation shapes during decoding. It does not establish that the
chosen operation achieves the goal. Plain JSON remains the default: the measured
schema runs did not justify imposing that decoding mode on every local model.

## What learns and what survives restart

The bridge uses the same WorldModel and EmbodiedAI objects wired at boot.
EmbodiedAI.learnTransition records actual simulator sensory/motor transitions,
updates its integrator and contingencies, body schema and reward expectations.
WorldModel processes observed numeric state with its existing implementation.
The snapshot contains actual serialized object state/parameters, not just stats.

Verified, versioned digital-genome capsules preserve both brains and committed
feedback identities. Boot restores them before the first command. A rejected
operation restores uncommitted neural state; feedback committed in a capsule is
not learned again when a command receipt is retried. Deserialization is restricted
to known state classes and bounded object graphs. A schema/runtime change may
require a migration; there is no promise of cross-version Java serialization.

Results identify simulator_observed feedback and explicitly set
physical_hardware_executed=false. Local transition learning is distinguished
from foundation-model weight updates. No Carbon, Oracle, Needle or hosted model
weights were fine-tuned by this bridge.

## Local model observations

The local `eyeoverthink/FRAYNIX-PROTOCOL:3b` model produced a valid creation
proposal after the key/operation instructions were clarified. Its proposed
Robot Sensor Target was actually committed through FraynixBoot and the shared
specialist adapter in an isolated application fixture, with feedback and clean
shutdown. The initial rejected attempt remains in the evidence.

`fraymus-oracle:latest` received the actual imported user scene. In an ordinary
description it correctly stated 15 entities and 8 physics bodies and named
Sprite_Object_Gen, but incorrectly suggested that other objects lacked names;
all 15 actually shared that name. Two structured creation runs failed protocol
validation (wrong/absent observation IDs and malformed operations). Neither
changed the scene. This establishes live data delivery and specific limitations,
not a blanket model-quality verdict. Do not treat readable prose as an executed
scene edit. Any later results are recorded separately in reports/robot-chain.

Follow-up tests on the full 15-object scene also rejected FRAYNIX-PROTOCOL
proposals. Schema decoding corrected its observation ID, but it selected set
instead of create and referenced nonexistent entities; Oracle's schema run
timed out. These failures are retained alongside the earlier successful
creation. No automatic repair of model intent or substitution of a different
model is reported as model success.

The original application then imported a COPY of the user's actual saved scene
into its own persistent physics environment, observed 15 entities/8 bodies via
the specialist route, learned simulator feedback, audited its active chain and
shut down cleanly. Source files, original FASTAs and the original editor
checkpoint were hash-checked unchanged. The application saved 80 available
knowledge blocks at that shutdown; the older 792-hash historical gap remains.

## Knowledge chain and record durability

The active Akashic aggregate had 64 available records, while its chain referred
to 792 additional hashes whose payloads were absent. Recovery searched older
archives and workspace snapshots. Three truncated aggregate snapshots exposed
99,238, 46,911 and 50,464 fully deserialized records before EOF, respectively;
none matched those 792 hashes. Originals were retained. These records were not
silently mixed into the active knowledge state.

The repair preserved the original chain and a reconciliation receipt under
`.akashic/history`, archived all 64 available records independently, and committed
an active chain containing those available records. Fresh-JVM readback verifies
active integrity and the independent payloads. historical_complete remains false
and unresolved hashes remain explicitly reported. This does not reconstruct the
missing historical content. Subsequent legitimate learning can increase the
active count without changing the historical gap report.

New KnowledgeRecordArchive storage uses immutable per-record metadata and
SHA256-addressed gzip UTF-8 payloads under `.akashic/record-archive`. It preserves
original category, time and legacy identity, validates content hash and length,
and rejects corrupt existing payloads. addBlock archives a record before
acknowledging it. A fresh JVM can rebuild available records after loss of the
aggregate blocks.dat, as demonstrated only on isolated test fixtures.

`specialist chain` accepts audit, recover and repair request operations. Repair
requires readable source state and checked disk commits; it keeps original chain
bytes and missing-history receipts. Checksums and Merkle-style references detect
changes; they are not authenticated signatures or homomorphic encryption.

## Verification and remaining boundaries

- Current-source Java compilation and the focused specialist verifier cover
  real Needle/native/Carbon workers, digital genomes, lifecycle, escaped Kronos
  persistence, deferred aggregate preservation, independent record recovery,
  actual robot feedback and separate-JVM state restoration.
- The editor has 60 passing JUnit checks, including four RobotSceneWorker checks.
  Shared factory modules have separate executable verification tasks.
- Full offline-kernel verification is run independently; logs record its exact
  test selection and result: 49/49 standalone checks passed. A leaked mock
  Docker/llama fixture server was identified by its exact test class and stopped
  after the tests; unrelated editor Java processes were left running.
- Real local model attempts, failures, validated commits and source checkpoint
  hashes are retained as separate evidence, not folded into mock-model tests.

Physical robot calibration, sensor/actuator transport, live render-thread
control and an original Odyssey3 model/decoder remain separate work. A numeric
brain prediction is not observed physics. Restart restores authored scene state
and velocities; it does not claim bit-exact restoration of JBox2D internal contact
manifolds. A single scene writer is enforced per worker workspace; the aggregate
knowledge store still assumes one application writer. Generated model weights,
credentials and caches are separate from source publication.

This completes the testable simulator integration and available-record repair.
The next measured improvement is a stronger Oracle scene-output protocol and
task corpus, followed by a real device adapter once a device is selected.
