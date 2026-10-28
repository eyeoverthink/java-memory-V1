package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies unresolved method diagnostics can be repaired with a bounded method stub.
 */
public final class MissingMethodRepairTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("missing-method-repair");
        Path demoDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(demoDir);
        Files.createDirectories(scriptsDir);

        Files.writeString(demoDir.resolve("UseTool.java"), """
            package demo;

            public final class UseTool {
                public String render() {
                    return buildValue();
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"), "missing-method");
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair unresolved method buildValue and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/UseTool.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected missing method baseline failure");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6, baseline.getVerifierResult());
            MutationProposal repair = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Add missing method stub buildValue"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected missing method repair proposal"));
            assertTrue(repair.getEvidenceTags().contains("verifier.compiler.method_missing"), "Expected method-missing evidence tag");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Add missing method stub buildValue"), "Expected missing method repair to win");
            assertTrue(iteration.promoted(), "Expected missing method repair to be promoted");

            String updated = Files.readString(demoDir.resolve("UseTool.java"), StandardCharsets.UTF_8);
            assertTrue(updated.contains("private String buildValue()"), "Expected bounded buildValue stub");
            assertTrue(updated.contains("return \"\";"), "Expected deterministic string default");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected workspace to verify after method repair");
        }

        System.out.println("MissingMethodRepairTest passed");
    }

    private static void writeOfflineManifest(Path workspace, Path verifyScript, String buildLabel) throws Exception {
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
            $buildDir = Join-Path $projectRoot ('build\\%s-' + $PID)
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
            """.formatted(buildLabel), StandardCharsets.UTF_8);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
