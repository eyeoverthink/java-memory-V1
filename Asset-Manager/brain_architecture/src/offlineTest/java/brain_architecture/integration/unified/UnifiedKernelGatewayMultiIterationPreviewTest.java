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
 * Verifies unified bounded evolution preview can inspect chained shadow rounds.
 */
public final class UnifiedKernelGatewayMultiIterationPreviewTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("unified-kernel-multi-iteration");
        try {
            Path sourceRoot = workspace.resolve(Path.of("src", "main", "java"));
            Path helper = sourceRoot.resolve(Path.of("training", "support", "ChainHelper.java"));
            Path consumer = sourceRoot.resolve(Path.of("training", "preview", "UseMultiIterationChain.java"));
            Path scriptsDir = workspace.resolve("scripts");
            Files.createDirectories(helper.getParent());
            Files.createDirectories(consumer.getParent());
            Files.createDirectories(scriptsDir);

            Files.writeString(helper, """
                package training.support;

                public final class ChainHelper {
                    public String value() {
                        return "ok";
                    }
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(consumer, """
                package training.preview;

                public final class UseMultiIterationChain {
                    public String render() {
                        ChainHelper helper = new ChainHelper();
                        return helper.value();
                    }
                """, StandardCharsets.UTF_8);

            writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

            try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace))) {
                UnifiedKernelGateway gateway = new UnifiedKernelGateway(kernel);
                UnifiedTask task = UnifiedTask.builder("repair chained parser and import failures while keeping the build offline")
                    .addArtifact(Path.of("src/main/java/training/preview/UseMultiIterationChain.java"))
                    .mode(TaskMode.MUTATION)
                    .includeEvolutionEvaluations(true)
                    .evolutionIterationLimit(2)
                    .build();

                UnifiedResult result = gateway.run(task, ValidationSuite.empty());

                assertTrue(!result.isValidationPassed(), "Expected failing validation for chained preview scenario");
                assertTrue(result.getEvolutionIterations().size() >= 2, "Expected at least two preview iterations");

                UnifiedEvolutionIteration firstIteration = result.getEvolutionIterations().get(0);
                UnifiedEvolutionIteration secondIteration = result.getEvolutionIterations().get(1);
                assertTrue(!firstIteration.isPromoted(), "Preview iterations must not promote changes");
                assertTrue(!secondIteration.isPromoted(), "Preview iterations must not promote changes");
                assertTrue(firstIteration.getSelectedProposalTitle() != null
                    && firstIteration.getSelectedProposalTitle().contains("Close unmatched braces for UseMultiIterationChain"),
                    "Expected parser repair to win the first preview round");
                assertTrue(secondIteration.getSelectedProposalTitle() != null
                    && secondIteration.getSelectedProposalTitle().contains("ChainHelper")
                    && (
                        secondIteration.getSelectedProposalTitle().contains("Add missing import")
                        || secondIteration.getSelectedProposalTitle().contains("Synthesize missing type")
                    ),
                    "Expected a ChainHelper semantic repair to win the second preview round but saw: " + secondIteration.getSelectedProposalTitle());
                assertTrue(secondIteration.getEvaluations().stream().anyMatch(UnifiedEvolutionEvaluation::isSelected),
                    "Expected a selected evaluation in the second round");
                long previewJournalCount = result.getJournalEntries().stream()
                    .filter(entry -> "evolution_preview_generated".equals(entry.getType()))
                    .count();
                assertTrue(previewJournalCount >= 2, "Expected two preview journal entries");
            }
        } finally {
            deleteRecursively(workspace);
        }

        System.out.println("UnifiedKernelGatewayMultiIterationPreviewTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\unified-multi-iteration-' + $PID)
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
