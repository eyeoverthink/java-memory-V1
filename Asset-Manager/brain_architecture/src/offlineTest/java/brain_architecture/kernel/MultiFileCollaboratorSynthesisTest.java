package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies the kernel can synthesize a bounded two-type collaborator set from one failing test.
 */
public final class MultiFileCollaboratorSynthesisTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("multi-file-collaborator-synthesis");
        Path testRoot = workspace.resolve(Path.of("src", "offlineTest", "java", "demo", "tests"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(testRoot);
        Files.createDirectories(scriptsDir);

        Files.writeString(testRoot.resolve("MissingCollaboratorSetTest.java"), """
            package demo.tests;

            import demo.construct.Alpha;
            import demo.construct.Beta;

            public final class MissingCollaboratorSetTest {
                public static void main(String[] args) {
                    Alpha alpha = new Alpha();
                    Beta beta = new Beta();
                    String value = alpha.value() + beta.value();
                    if (!"".equals(value)) {
                        throw new IllegalStateException("Expected deterministic empty string");
                    }
                    System.out.println("MissingCollaboratorSetTest passed");
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct collaborator set Alpha and Beta from the failing test and keep the build offline")
                .addArtifact(Path.of("src/offlineTest/java/demo/tests/MissingCollaboratorSetTest.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected failing collaborator baseline");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 8, baseline.getVerifierResult());
            MutationProposal collaboratorSet = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Synthesize collaborator set Alpha, Beta"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected collaborator-set synthesis proposal"));
            assertTrue(collaboratorSet.getEvidenceTags().contains("verifier.compiler.multi_type_synthesis"), "Expected multi-type synthesis evidence");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Synthesize collaborator set Alpha, Beta"), "Expected collaborator-set proposal to win");
            assertTrue(iteration.promoted(), "Expected collaborator-set synthesis to promote");

            Path alphaPath = workspace.resolve(Path.of("src", "main", "java", "demo", "construct", "Alpha.java"));
            Path betaPath = workspace.resolve(Path.of("src", "main", "java", "demo", "construct", "Beta.java"));
            String alpha = Files.readString(alphaPath, StandardCharsets.UTF_8);
            String beta = Files.readString(betaPath, StandardCharsets.UTF_8);
            assertTrue(alpha.contains("public final class Alpha"), "Expected synthesized Alpha class");
            assertTrue(beta.contains("public final class Beta"), "Expected synthesized Beta class");
            assertTrue(alpha.contains("public String value()"), "Expected bounded Alpha value method");
            assertTrue(beta.contains("public String value()"), "Expected bounded Beta value method");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected collaborator set to validate after synthesis");
        }

        System.out.println("MultiFileCollaboratorSynthesisTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\multi-file-collaborator-' + $PID)
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
                & $java.Source -cp $classesDir 'demo.tests.MissingCollaboratorSetTest'
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
