# Model-discovery startup repair - October 25, 2026

Fraynix - By Vaughn Scott

The installed classes reproduced `NoClassDefFoundError: org/json/JSONObject`
at `LocalModelCatalog.endpoints:79`. The documented classes-only launch does
not include the Gradle dependency JAR. Catalog discovery and Ollama protocol
handling had introduced an org.json requirement into the core boot path.

## Changes

- LocalModelCatalog and OllamaIntegration now use the existing FraymusJSON
  strict parser and ordinary Java maps/lists for catalog and HTTP messages.
- Exact installed model tags, local store routing, remote-proxy filtering,
  inference options, structured response format and streamed replies remain
  covered by regression checks.
- LocalModelCatalog.metadataForModel now returns Map<String,Object>. The only
  production caller found in this source tree is its own parameterBillions
  method. External compiled callers of the former JSONObject signature would
  need recompilation.
- The factory-models source set now includes the shared JDK-only JSON parser,
  matching its existing reuse of the catalog and Ollama classes.
- The shared serializer now escapes every JSON control character. A protocol
  regression found that raw control characters previously produced invalid
  requests.

## Verified

- Exact installed-class failure reproduced before the edit, with no JARs.
- `gradlew.bat compileJava --offline --no-daemon --console=plain`: passed.
- `gradlew.bat compileJava :verifyLocalModels :factory-models:verifyModels
  --offline --no-daemon --console=plain`: passed, including 19 existing JUnit
  model tests and FactoryModelsTest.
- Fresh, JDK-only protocol checks: 83 passed with org.json absent.
- Strict JSON checks: 72 passed, including all 32 control characters.
- Installed classes-only FraynixBoot smoke run: phase 6 passed, reached shell,
  ran status, saved and exited with code 0. Working directory and user.home
  were isolated for that run.

Reproduce the standalone source check:

```powershell
python .\scripts\verify-ollama-json.py --output "$env:TEMP\fraynix-ollama-json-new"
```

Evidence: C:\Users\eyeka\.codex\validation\ollama-json-fix-20261025-futigems

The separate Carbon genomics addition has its own model installation and
inference evidence; it is loaded only when its commands are invoked.
