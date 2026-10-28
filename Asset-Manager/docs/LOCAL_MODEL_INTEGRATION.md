# Local model integration and Ollama diagnosis

Verified on October 22, 2026. The supplied `F:\error-fraynix-intelle-j.txt` is an assistant transcript used as evidence; its embedded plans and commands were not treated as operator instructions.

## What failed

The graphical application's ExperimentManager started with `llama3.2:1b`, which the active daemon did not advertise. Its `ollama local` command selected `gpt-oss:120b`, which the active daemon also did not advertise. Before editing, the real Java client returned HTTP 404 for both models, while the same client returned `LOCAL_OK` with installed `llama3.2:latest`.

The active Ollama service itself was healthy. A direct cold inference took approximately 28 seconds, close to the old client's 30-second read timeout. The client's successful warm inference took approximately 2.7 seconds. These measurements do not prove all earlier symptoms had one cause.

The IDE assistant's URL-constructor cleanup and checked JSON casts were compared with preserved source. They did not change the local endpoint. There is no evidence assigning the observed model-selection failure to that cleanup. The separate scrape/UI-freeze hypotheses in the transcript were not reproduced or repaired here.

## Model stores found

| Store | Model directory | Local endpoint | Advertised entries |
| --- | --- | --- | ---: |
| Primary | `C:\Users\eyeka\.ollama\models` | `http://127.0.0.1:11434` | 35: 34 local, 1 cloud proxy |
| Archive F | `F:\LocalDiskC\Users\eyeka\.ollama\models` | `http://127.0.0.1:11435` | 19 local |

Together these contain 53 local store records representing **46 distinct local model names**, plus one cloud proxy record. Duplicate tags prefer the primary store; their weights and prompt configurations are not merged. Distinct model names can share the same base weight blob.

The second store adds 12 names absent from the primary catalog, including:

- `eyeoverthink/fraymus-bicameral:latest`, `eyeoverthink/fraymus-recursive:latest`
- `fraymus-genesis:latest`, `fraymus-nexus:latest`, `fraymus-oracle:latest`, `fraymus-trained:latest`
- `dolphin-llama3:latest`, `dolphin-mistral:latest`
- `glm-4.7-flash:latest`, `gpt-oss:120b`, `qwen3-coder:30b`, `qwen3.5:35b`

The active store already contains your Fraynix 3b/8b/ultimate/dna/agent/protocol, Fraymus Prime, Niles, and vision variants. Metadata captures exact names, configured context, advertised maximum context, capabilities, quantization, parameter size, model digest, source blob path, and a hash of the exported Modelfile. Private system prompts are not copied into the catalog.

An additional daemon was started for the existing F: store. The large weights were not copied to C:. The original primary daemon was not restarted. No model pulls or cloud inference were performed.

## Changes wired into the application

1. `OllamaIntegration` reads structured model metadata, excludes cloud proxies from local lists, resolves installed local defaults through the existing abstraction layer, routes generate/chat calls by model endpoint, handles escaped response text correctly, closes connections, and allows 120 seconds for reads.
2. `ExperimentManager` defaults to your installed Fraymus model, rejects unavailable manual local selections, lists model endpoints, exposes `ollama catalog`, and requires an explicit `ollama cloud` command for cloud mode. `FRAYNIX_LOCAL_MODEL` or JVM property `fraynix.localModel` can select another default.
3. `ModelAbstractionLayer` checks all exact names before family normalization. An exact `:8b` request therefore cannot be captured by an earlier `:3b` candidate.
4. `LocalModelCatalog` and `OpenModelRegistry` load persistent observations and preserve exact custom names. The registry refreshes its cached observations after the catalog changes. Existing family routing priors are retained; unknown custom models receive neutral quality priors. No benchmark scores are fabricated from names.
5. The core voice bridge used by FraynixBoot, Nexus bridge, default `llm.api.OllamaClient`, and default `OllamaSpine` route model requests to the catalog endpoint. Explicit custom URLs on the latter clients continue to take precedence.
6. `ModelRegistryIntegration` registers observed models in the separate organ/capability registry and installs real `LocalOllamaModelAdapter` instances. These execute text inference and conversation history with runtime options. Structured tool calls are explicitly unsupported by this adapter. Registration does not itself prove model intelligence, vision input handling, or automatic use by every legacy subsystem.
7. The cloud credential formerly embedded in `OllamaIntegration` now comes from `OLLAMA_API_KEY`. Local operation needs no cloud key. Previously exposed credentials should be replaced before using cloud mode again.

The independent legacy `brain_architecture` cloud/backend implementations are not all migrated to this catalog. The tested factory inspection task remains an offline artifact-verification workflow, rather than an LLM inference benchmark.

## Disk artifacts

The model-file scan covered mounted C:, D:, F:, and H: within the recorded search scope: all non-system data roots on D:/F:/H:, user Downloads/Documents, and the local Hugging Face cache. Access-denied system directories were recorded, not bypassed. Archives were not unpacked.

The inventory contains 422 candidate artifacts after excluding compiled/source-code false positives: 397 recipe/document candidates, 16 Safetensors files, two other weight candidates, and seven tiny GGUF test fixtures. Recipe counts include backup copies and do not mean 397 distinct models. All seven extension-matched GGUFs were 24-byte test data, not runnable model weights.

Real Hugging Face artifacts include GPT-2, TinyLlama, Faster Whisper, Stable Diffusion, and LTX Video, with backup copies on F:. These are inventoried as discovered artifacts. They were not falsely registered as Ollama models or loaded by an incompatible Java adapter. Some are pipeline components rather than standalone models.

## Reproduce the runtime

Run from `F:\Full-AI-System\Java-Memory\Asset-Manager`:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Start-LocalModelStores.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Sync-LocalModelCatalog.ps1
.\gradlew.bat --offline --no-daemon --console=plain :verifyLocalModels
```

`Start-LocalModelStores.ps1` verifies the existing store and starts an additional loopback-only daemon when needed. It saves configuration under `%USERPROFILE%\.fraymus\models\ollama-stores.json`. It does not install an OS startup task. Run it again after a machine restart before selecting F: models. The catalog refresh retains previous disk artifacts unless a new scan is requested.

To refresh disk discovery too:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\Sync-LocalModelCatalog.ps1 -ScanDisks
```

Inside the graphical application:

```text
ollama local
ollama models
ollama catalog
ollama model fraymus-oracle:latest
ollama ask Reply with a short local connection confirmation.
```

Rebuild/restart an already running application to load the changed Java classes. Existing IntelliJ sessions do not automatically replace a running JVM's class definitions.

## Evidence and limits

- `:verifyLocalModels` passed 19 tests covering parsing, model selection, both registries, organ routing regression, and routing to an isolated additional local endpoint through existing clients and the runnable adapter.
- Live application-client generation passed with `eyeoverthink/Fraymus:latest`, `fraynix-3b:latest`, and F:-backed `fraymus-oracle:latest`. Local chat also passed.
- The F: store's 19 manifests were inspected for referenced blob presence; all referenced files existed. Large-weight cryptographic hashing and inference benchmarks across every model were not run.
- `gpt-oss:120b`, GLM, Qwen, speech, image, and video quality/latency are not certified by this inventory. Metadata availability and real inference are separate evidence classes.
- The test runtime uses locally available Maven-cache JUnit artifacts. No internet download was needed.

Forensic copies, original diffs, inventory, before/after Java probe logs, and disk scan errors are under `C:\Users\eyeka\Downloads\physics_mario\system-analysis-2026-10-22\ollama-investigation`. Source changes are preserved alongside unrelated existing work; no repository reset was performed.
# Fraynix studio connection

The Physics Mario integration now consumes a canonical Java 17 `factory-models` module from this project. It compiles the existing `OllamaIntegration`, local catalog/registry/selection classes and `LocalOllamaModelAdapter`, plus a live health/residency facade and an append-only conversation store. The editor's Fraynix window exposes Chat, Create, Models, Kernel and Jobs. Its model picker is based on live local endpoints and excludes cloud proxy entries. The larger legacy full-system shell is not automatically started by the editor.

The shared adapter now supports an explicit `response_format=json` request parameter, placing `format` at the Ollama request root while keeping sampling/context settings in `options`. Existing plain chat/generation calls retain their original defaults. The Create tab uses this path for an actual observe/query/propose/validate loop; validation failures return to the model for correction within the selected turn budget. Complete component data, spatial queries, detached JBox2D previews, saved application and durable undo are implemented in the editor. Generic staged transactions and deterministic spatial queries also live in the shared zero-dependency `factory-runtime` module. The existing 19-test local-model suite and the structured-protocol harness pass; a native test verified a real `fraynix-3b:latest` proposal through application and undo after restart.

Editor source: `C:\Users\eyeka\Downloads\physics_mario\Vaughn-Scotts-Fraymus-Engine`. The shared module also has a portable source export there. Local model processes remain separate from the graphics process. See the editor's `docs/FACTORY_INTEGRATION.md` for the workspace, persistence, validation and runtime boundaries.

