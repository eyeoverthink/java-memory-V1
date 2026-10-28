package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies parser failures can be turned into bounded source repair mutations.
 */
public final class VerifierGuidedSourceRepairTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("verifier-guided-source-repair");
        Path sourceDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceDir);
        Files.createDirectories(scriptsDir);

        Path brokenSource = sourceDir.resolve("BrokenFeature.java");
        Files.writeString(brokenSource, """
            package demo;
            public final class BrokenFeature {
                public String run() { return "ok"; }
            """, StandardCharsets.UTF_8);

        Files.writeString(workspace.resolve("offline-toolchain.toml"), """
            [kernel]
            runtime = "air-gapped"
            java = "17"
            verify = "scripts/verify-workspace.ps1"
            """, StandardCharsets.UTF_8);

        Files.writeString(scriptsDir.resolve("verify-workspace.ps1"), """
            Set-StrictMode -Version Latest
            $ErrorActionPreference = 'Stop'

            $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
            $projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
            $mainSource = Join-Path $projectRoot 'src\\main\\java'
            $buildDir = Join-Path $projectRoot ('build\\verifier-guided-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'

            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) {
                throw 'javac was not found in PATH.'
            }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
                New-Item -ItemType Directory -Force -Path $classesDir | Out-Null
                $sources = Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                [System.IO.File]::WriteAllLines($sourcesFile, $sources)
                & $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
                if ($LASTEXITCODE -ne 0) {
                    throw "javac failed with exit code $LASTEXITCODE"
                }
                Write-Host 'Verifier guided workspace passed.'
            }
            finally {
                if (Test-Path $buildDir) {
                    Remove-Item -Recurse -Force $buildDir -ErrorAction SilentlyContinue
                }
            }
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair parser failure in BrokenFeature and keep it offline")
                .addArtifact(Path.of("src/main/java/demo/BrokenFeature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected baseline validation to fail");
            assertTrue(!baseline.getVerifierResult().getDiagnostics().isEmpty(), "Expected structured diagnostics on baseline failure");
            VerifierDiagnostic parserDiagnostic = baseline.getVerifierResult().getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getFile() != null && diagnostic.getFile().endsWith(Path.of("BrokenFeature.java")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected BrokenFeature diagnostic"));
            assertTrue(parserDiagnostic.getLine() != null && parserDiagnostic.getLine() == 3, "Expected line number on parser diagnostic");
            assertTrue(parserDiagnostic.getSeverity() == VerifierDiagnostic.Severity.ERROR, "Expected error severity");
            assertTrue(parserDiagnostic.getMessage().toLowerCase().contains("reached end of file while parsing"), "Expected parser diagnostic message");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6, baseline.getVerifierResult());
            MutationProposal repair = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Close unmatched braces"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected unmatched brace repair proposal"));
            assertTrue(repair.getEvidenceTags().contains("verifier.failure"), "Expected verifier failure evidence");
            assertTrue(repair.getEvidenceTags().contains("verifier.compiler.parse"), "Expected parser evidence");

            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(!report.getIterations().isEmpty(), "Expected an evolution iteration");
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected a selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Close unmatched braces"), "Expected source repair to be selected");
            assertTrue(iteration.promoted(), "Expected source repair to be promoted");

            String repairedSource = Files.readString(brokenSource, StandardCharsets.UTF_8);
            assertTrue(repairedSource.stripTrailing().endsWith("}"), "Expected repaired source to close the class");

            KernelRunResult repairedRun = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repairedRun.getVerifierResult().isSuccess(), "Expected verifier to pass after source repair");
        }

        System.out.println("VerifierGuidedSourceRepairTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
