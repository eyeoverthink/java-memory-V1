# Fraynix shell self-improvement

Fraynix - By Vaughn Scott

The shell now calls the existing typed offline kernel directly. It uses the
kernel already packaged by `factory-kernel`; no new model, service, or Python
dependency is required. A chat model's answer is not used as proof of execution.

## Commands

After rebuilding/restarting Fraynix:

```text
system improve status
system improve help
system improve recursive self-improvement
system goal recursive self-improvment
```

The last two commands preview one iteration. The original spelling is recognized
in the `system goal` compatibility route. Other `system goal` requests retain
their conversational route. Existing `system execute` behavior is unchanged.

For a concrete source repair:

```text
system improve --artifact "Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java" repair compiler failures in FraynixBoot
system improve --apply --artifact "Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java" repair compiler failures in FraynixBoot
```

Options:

- `--preview` / `--plan`: baseline and shadow evaluation; the default.
- `--apply`: re-evaluate candidates and promote only when every configured check passes.
- `--workspace "path"`: select a workspace with `offline-toolchain.toml`.
- `--artifact "relative file"`: provide source evidence; repeat for multiple files.
- `--iterations N`: request a positive, finite number of evolution iterations (default 1).
- `--`: treat all remaining words as the goal, including words beginning with `--`.

Quotes group paths with spaces. Backslashes are preserved. Arguments are not
passed through a command shell. A goal is not evaluated as a shell script.

## What happens

1. Resolve the workspace and acquire a lock against another shell improvement.
2. Read the configured verifier and display its commands.
3. Run a baseline and collect actual compiler/test diagnostics.
4. Generate candidates using the existing proposal engine, retrieval, and repair memory.
5. Apply candidates to disposable shadow copies and run validation there.
6. Print candidate paths, test outcomes and promotion decisions.
7. On `--apply`, revalidate the selected candidate before promotion. Re-run the
   task afterward; record a repair lesson only when that validation succeeds.
8. Persist replay traces, a report containing exact edit operations, and kernel state.

Preview writes kernel traces/reports and can produce validation build outputs.
It does not promote candidate source changes. `--apply` is a fresh evaluation,
not approval of an immutable previously displayed patch. Use it with the same
goal and options after reviewing the preview.

The existing lower-level evolution API retains its partial-repair policy for
legacy callers. The new shell route requires complete validation success.

## Workspace and validation boundary

`-Dfraynix.kernelWorkspace=...` sets a default; otherwise the shell finds the
nearest ancestor of its working directory containing `offline-toolchain.toml`.
From the normal Asset-Manager launch directory, that is `F:/Full-AI-System` on
the development PC. Resolution uses paths, not a hardcoded drive letter.

The root manifest now runs `scripts/verify-fraynix-app.ps1`: offline Gradle
`compileJava` plus `kernelCommandTest`. It requires source/test files to exist
and checks JUnit results contain executed, passing tests. The older fast
training-only verifier remains available separately.

The manifest's `[shadow]` entries explicitly list the build files, source roots,
resources, and test inputs needed in a shadow workspace. Missing inputs and
paths outside the workspace fail validation. Model weights, learned-memory
snapshots and generated build directories are not copied as validation inputs.
Language profiling prunes generated/state directories and stops after finding
all supported language families, avoiding a full archive traversal.

This validation covers compilation and the shell/kernel integration tests.
It is not the complete subsystem test suite or a proof that an arbitrary
generated algorithm meets its intended specification. Add behavior checks for
the specific change to the workspace verifier. An installed offline Gradle
distribution and cached dependencies are needed on a receiving machine.

## Evidence and limits

Reports are `<workspace>/.northstar-kernel/reports/improvement-<task UUID>.json`.
They include the goal/mode, baseline diagnostics, validation commands, proposed
edit operations, per-candidate eligibility, promotion results, replay location,
events, and before/after memory counts. Failed runs also save a report once a
task has been created. Console output prints the report's absolute path.

Repair lessons/templates use the existing kernel stores. Kernel persistence is
separate from the app's Akashic/Kronos stores; no additional Akashic snapshot is
required by this command. Shell locks coordinate these shell commands, not
independent legacy kernel processes; avoid concurrent writers to the same state.

Broad goals may produce a digest or test scaffold, and a healthy repair target
may yield no proposal. That is reported explicitly rather than described as
automatic intelligence growth. Supported repairs remain the kernel's existing
deterministic repair families. Source updates take effect after rebuild/restart;
this command does not hot-swap the running JVM.

## Reproduce validation

```powershell
Set-Location "F:\Full-AI-System\Java-Memory\Asset-Manager"
.\gradlew.bat --offline --no-daemon --console=plain kernelCommandTest
powershell -NoProfile -ExecutionPolicy Bypass -File .\brain_architecture\scripts\verify-offline-kernel.ps1 -FullSuite
```

The integration tests use isolated workspaces and real javac/java processes.
They exercise unchanged-source preview, repair promotion, behavior validation,
lesson restoration, refusal when another check fails, missing validation inputs,
quoted paths, option rejection, and the original FraynixBoot command route
without requiring Ollama or booting the organism.

To load changes into an already running app, enter `exit and save`, wait for
shutdown, then run the normal Gradle `runFraynixBoot` launcher again.
