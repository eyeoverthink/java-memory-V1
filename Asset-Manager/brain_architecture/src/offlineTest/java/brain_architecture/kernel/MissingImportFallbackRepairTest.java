package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies missing-import repair still triggers when compiler diagnostics only expose
 * the generic "cannot find symbol" message plus line/column coordinates.
 */
public final class MissingImportFallbackRepairTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("missing-import-fallback");
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

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair unresolved ImportHelper type in live training and keep the build offline")
                .addArtifact(Path.of("src/main/java/training/imports/ImportConsumer.java"))
                .mode(TaskMode.MUTATION)
                .build();

            VerifierResult verifierResult = new VerifierResult(
                false,
                List.of("task.goal.present"),
                List.of("command.kernel.verify.exit_1"),
                List.of(),
                List.of(new VerifierDiagnostic(
                    "kernel.verify",
                    consumer,
                    5,
                    9,
                    VerifierDiagnostic.Severity.ERROR,
                    "cannot find symbol",
                    consumer + ":5: error: cannot find symbol"
                )),
                java.util.Map.of()
            );

            List<MutationProposal> proposals = kernel.proposeMutations(task, 5, verifierResult);
            List<String> titles = proposals.stream().map(MutationProposal::getTitle).toList();
            boolean foundImportRepair = proposals.stream()
                .anyMatch(proposal -> proposal.getTitle().contains("Add missing import for ImportHelper"));
            assertTrue(foundImportRepair, "Expected fallback import repair proposal when raw symbol details are missing; titles=" + titles);
        }

        System.out.println("MissingImportFallbackRepairTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\missing-import-fallback-' + $PID)
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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
