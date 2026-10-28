package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies failing offline tests can drive bounded missing-class construction in src/main/java.
 */
public final class FailingTestConstructionSynthesisTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("failing-test-construction");
        Path testRoot = workspace.resolve(Path.of("src", "offlineTest", "java", "demo", "tests"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(testRoot);
        Files.createDirectories(scriptsDir);

        Files.writeString(testRoot.resolve("MissingBuilderTest.java"), """
            package demo.tests;

            import demo.construct.MissingBuilder;

            public final class MissingBuilderTest {
                public static void main(String[] args) {
                    MissingBuilder builder = new MissingBuilder("seed");
                    String value = builder.value();
                    if (!"".equals(value)) {
                        throw new IllegalStateException("Expected deterministic empty string");
                    }
                    System.out.println("MissingBuilderTest passed");
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct the missing MissingBuilder implementation from the failing test and keep the build offline")
                .addArtifact(Path.of("src/offlineTest/java/demo/tests/MissingBuilderTest.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected failing test baseline");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6, baseline.getVerifierResult());
            MutationProposal synthesis = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Synthesize missing type MissingBuilder"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected failing-test construction proposal"));
            assertTrue(synthesis.getEvidenceTags().contains("verifier.compiler.type_synthesis"), "Expected type synthesis evidence");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Synthesize missing type MissingBuilder"), "Expected construction synthesis to win");
            assertTrue(iteration.promoted(), "Expected failing-test construction synthesis to be promoted");

            Path constructedType = workspace.resolve(Path.of("src", "main", "java", "demo", "construct", "MissingBuilder.java"));
            String content = Files.readString(constructedType, StandardCharsets.UTF_8);
            assertTrue(content.contains("package demo.construct;"), "Expected package inferred from failing test import");
            assertTrue(content.contains("public MissingBuilder(Object arg0)"), "Expected bounded one-argument constructor");
            assertTrue(content.contains("public String value()"), "Expected String-returning method inferred from test");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected failing test to pass after bounded construction");
        }

        System.out.println("FailingTestConstructionSynthesisTest passed");
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
            $testSource = Join-Path $projectRoot 'src\\offlineTest\\java'
            $buildDir = Join-Path $projectRoot ('build\\failing-test-construction-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'
            $javac = Get-Command javac -ErrorAction SilentlyContinue
            $java = Get-Command java -ErrorAction SilentlyContinue
            if (-not $javac) { throw 'javac was not found in PATH.' }
            if (-not $java) { throw 'java was not found in PATH.' }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir,$classesDir | Out-Null
                $sources = @()
                if (Test-Path $mainSource) {
                    $sources += Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                }
                if (Test-Path $testSource) {
                    $sources += Get-ChildItem $testSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                }
                [System.IO.File]::WriteAllLines($sourcesFile, $sources)
                & $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
                if ($LASTEXITCODE -ne 0) { throw "javac failed with exit code $LASTEXITCODE" }
                & $java.Source -cp $classesDir 'demo.tests.MissingBuilderTest'
                if ($LASTEXITCODE -ne 0) { throw "java failed with exit code $LASTEXITCODE" }
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
