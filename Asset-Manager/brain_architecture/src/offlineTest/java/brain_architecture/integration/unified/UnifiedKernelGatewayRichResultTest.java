package brain_architecture.integration.unified;

import brain_architecture.kernel.KernelConfiguration;
import brain_architecture.kernel.OfflineKernel;
import brain_architecture.kernel.TaskMode;
import brain_architecture.kernel.ValidationSuite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Verifies one unified gateway run can return trace, diagnostics, memory hits, replay path,
 * and mutation proposals together.
 */
public final class UnifiedKernelGatewayRichResultTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("unified-kernel-rich-result");
        try {
            Path sourceRoot = workspace.resolve(Path.of("src", "main", "java"));
            Path helper = sourceRoot.resolve(Path.of("training", "util", "ImportHelper.java"));
            Path consumer = sourceRoot.resolve(Path.of("training", "imports", "ImportConsumer.java"));
            Path scriptsDir = workspace.resolve("scripts");
            Files.createDirectories(helper.getParent());
            Files.createDirectories(consumer.getParent());
            Files.createDirectories(scriptsDir);

            Files.writeString(helper, """
                package training.util;

                public final class ImportHelper {
                    public String value() {
                        return "ok";
                    }
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(consumer, """
                package training.imports;

                public final class ImportConsumer {
                    public String render() {
                        ImportHelper helper = new ImportHelper();
                        return helper.value();
                    }
                }
                """, StandardCharsets.UTF_8);

            writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

            try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace))) {
                UnifiedKernelGateway gateway = new UnifiedKernelGateway(kernel);
                UnifiedTask task = UnifiedTask.builder("repair unresolved ImportHelper type and keep the build offline")
                    .addArtifact(Path.of("src/main/java/training/imports/ImportConsumer.java"))
                    .mode(TaskMode.MUTATION)
                    .build();

                UnifiedResult result = gateway.run(task, ValidationSuite.empty());

                assertTrue(!result.isValidationPassed(), "Expected failing validation for missing import scenario");
                assertTrue(result.getTrace() != null, "Expected unified trace");
                assertTrue(!result.getTrace().getPlanSteps().isEmpty(), "Expected trace plan steps");
                assertTrue(result.getReplayTracePath() != null && !result.getReplayTracePath().isBlank(), "Expected replay trace path");
                assertTrue(Files.exists(Path.of(result.getReplayTracePath())), "Expected replay trace artifact to exist");
                assertTrue(!result.getMemoryHits().isEmpty(), "Expected unified memory hits");
                assertTrue(result.getMemoryHits().stream().anyMatch(hit -> hit.getSourceId().contains("ImportConsumer") || hit.getSourceId().contains("ImportHelper")), "Expected relevant memory hit for the missing import scenario");
                assertTrue(!result.getDiagnostics().getDiagnostics().isEmpty(), "Expected structured diagnostics");
                assertTrue(result.getDiagnostics().getDiagnostics().stream().anyMatch(diagnostic -> diagnostic.getMessage().contains("cannot find symbol")), "Expected cannot find symbol diagnostic");
                assertTrue(!result.getMutationProposals().isEmpty(), "Expected mutation proposals on mutation-mode failure");
                assertTrue(result.getMutationProposals().stream().anyMatch(proposal -> proposal.getTitle().contains("Add missing import for ImportHelper")), "Expected missing import proposal");
                assertTrue(result.getMutationProposals().stream().allMatch(proposal -> proposal.getPreview() != null), "Expected guarded preview summaries on unified mutation proposals");
                assertTrue(result.getMutationProposals().stream().anyMatch(proposal -> proposal.getPreview().isRiskAccepted()), "Expected at least one preview with accepted risk");
                assertTrue(!result.getEvolutionIterations().isEmpty(), "Expected bounded evolution iteration previews");
                assertTrue(result.getEvolutionIterations().get(0).getEvaluations().stream().anyMatch(UnifiedEvolutionEvaluation::isSelected), "Expected a selected evolution evaluation");
                assertTrue(result.getEvolutionIterations().get(0).getSelectionReason() != null && !result.getEvolutionIterations().get(0).getSelectionReason().isBlank(), "Expected evolution selection reason");
                assertTrue(result.getEvolutionIterations().get(0).getEvaluations().stream().anyMatch(evaluation -> evaluation.getProposal().getTitle().contains("Add missing import for ImportHelper")), "Expected import repair in evolution comparison set");
                assertTrue(!result.getJournalEntries().isEmpty(), "Expected recent journal entries for the unified task");
                assertTrue(result.getJournalEntries().stream().anyMatch(entry -> "task_started".equals(entry.getType())), "Expected task_started journal entry");
                assertTrue(result.getJournalEntries().stream().anyMatch(entry -> "task_completed".equals(entry.getType())), "Expected task_completed journal entry");
                assertTrue(result.getJournalEntries().stream().anyMatch(entry -> "evolution_preview_generated".equals(entry.getType())), "Expected evolution preview journal entry");
            }
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("UnifiedKernelGatewayRichResultTest passed");
    }

    private static void writeOfflineManifest(Path workspace, Path verifyScript) throws Exception {
        Files.writeString(workspace.resolve("offline-toolchain.toml"), """
            [kernel]
            runtime = "air-gapped"
            java = "17"
            verify = "scripts/verify-workspace.ps1"
            """, StandardCharsets.UTF_8);
        Files.writeString(verifyScript, """
            Set-StrictMode -Version Latest
            $ErrorActionPreference = 'Stop'

            $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
            $projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
            $mainSource = Join-Path $projectRoot 'src\\main\\java'
            $buildDir = Join-Path $projectRoot ('build\\unified-rich-result-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'
            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) { throw 'javac was not found in PATH.' }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir,$classesDir | Out-Null
                $sources = @(Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName)
                [System.IO.File]::WriteAllLines($sourcesFile, $sources)
                & $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
                if ($LASTEXITCODE -ne 0) { throw "javac failed with exit code $LASTEXITCODE" }
            }
            finally {
                if (Test-Path $buildDir) { Remove-Item -Recurse -Force $buildDir -ErrorAction SilentlyContinue }
            }
            """, StandardCharsets.UTF_8);
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort temp cleanup only.
                }
            });
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
