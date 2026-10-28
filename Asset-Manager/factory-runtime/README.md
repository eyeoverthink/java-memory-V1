# Shared factory runtime

JDK-only entity/component records, schema registry, revision-guarded world replacement, group cloning with entity-reference remapping, and versioned checksummed checkpoints.

The visual editor at `C:\Users\eyeka\Downloads\physics_mario\Vaughn-Scotts-Fraymus-Engine` builds against this canonical module. `factory-kernel` connects it to the existing Fraynix kernel and unified gateway. No graphics or model dependency is required by this module.

From Asset-Manager, run:

```powershell
.\gradlew.bat --offline :factory-runtime:verifyRuntime :factory-kernel:verifyIntegration runFactoryDemo
```

`runFactoryDemo` creates or restores a world under `.factory-demo`, verifies identity persistence, submits a real kernel artifact inspection, and records its receipt/trace. Checkpoint recovery is implemented; durable command replay, typed processing graphs, automatic job continuation and full component synthesis are later slices.
