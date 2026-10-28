package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies unresolved type diagnostics can be repaired with a unique missing import.
 */
public final class MissingImportRepairTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("missing-import-repair");
        Path demoDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Path utilDir = workspace.resolve(Path.of("src", "main", "java", "util"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(demoDir);
        Files.createDirectories(utilDir);
        Files.createDirectories(scriptsDir);

        Files.writeString(demoDir.resolve("UseHelper.java"), """
            package demo;

            public final class UseHelper {
                public String run() {
                    Helper helper = new Helper();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(utilDir.resolve("Helper.java"), """
            package util;

            public final class Helper {
                public String value() {
                    return "ok";
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair unresolved helper type and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/UseHelper.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected missing import baseline failure");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6, baseline.getVerifierResult());
            MutationProposal repair = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Add missing import for Helper"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected missing import repair proposal"));
            assertTrue(repair.getEvidenceTags().contains("verifier.compiler.import"), "Expected import evidence tag");
            assertTrue(repair.getEvidenceTags().contains("verifier.compiler.symbol"), "Expected symbol evidence tag");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Add missing import for Helper"), "Expected import repair to win");
            assertTrue(iteration.promoted(), "Expected import repair to be promoted");

            String updated = Files.readString(demoDir.resolve("UseHelper.java"), StandardCharsets.UTF_8);
            assertTrue(updated.contains("import util.Helper;"), "Expected helper import to be inserted");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected workspace to verify after import repair");
        }

        System.out.println("MissingImportRepairTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\missing-import-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'
            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) { throw 'javac was not found in PATH.' }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir,$classesDir | Out-Null
                $sources = Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
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
