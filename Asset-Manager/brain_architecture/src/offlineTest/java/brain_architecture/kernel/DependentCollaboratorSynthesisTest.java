package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies collaborator-set synthesis can preserve a bounded dependency chain between synthesized types.
 */
public final class DependentCollaboratorSynthesisTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("dependent-collaborator-synthesis");
        Path testRoot = workspace.resolve(Path.of("src", "offlineTest", "java", "demo", "tests"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(testRoot);
        Files.createDirectories(scriptsDir);

        Files.writeString(testRoot.resolve("DependentCollaboratorChainTest.java"), """
            package demo.tests;

            import demo.construct.AlphaLink;
            import demo.construct.BetaLink;

            public final class DependentCollaboratorChainTest {
                public static void main(String[] args) {
                    AlphaLink alpha = new AlphaLink();
                    BetaLink beta = alpha.beta();
                    String value = beta.value();
                    if (!"".equals(value)) {
                        throw new IllegalStateException("Expected deterministic empty string");
                    }
                    System.out.println("DependentCollaboratorChainTest passed");
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct dependent collaborator chain AlphaLink and BetaLink from the failing test and keep the build offline")
                .addArtifact(Path.of("src/offlineTest/java/demo/tests/DependentCollaboratorChainTest.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected failing dependent collaborator baseline");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 8, baseline.getVerifierResult());
            MutationProposal collaboratorSet = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Synthesize dependent collaborator set AlphaLink, BetaLink"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected dependent collaborator-set synthesis proposal"));
            assertTrue(collaboratorSet.getEvidenceTags().contains("verifier.compiler.multi_type_synthesis"), "Expected multi-type synthesis evidence");
            assertTrue(collaboratorSet.getEvidenceTags().contains("verifier.compiler.dependent_type_synthesis"), "Expected dependent synthesis evidence");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Synthesize dependent collaborator set AlphaLink, BetaLink"), "Expected dependent collaborator-set proposal to win");
            assertTrue(iteration.promoted(), "Expected dependent collaborator-set synthesis to promote");

            Path alphaPath = workspace.resolve(Path.of("src", "main", "java", "demo", "construct", "AlphaLink.java"));
            Path betaPath = workspace.resolve(Path.of("src", "main", "java", "demo", "construct", "BetaLink.java"));
            String alpha = Files.readString(alphaPath, StandardCharsets.UTF_8);
            String beta = Files.readString(betaPath, StandardCharsets.UTF_8);
            assertTrue(alpha.contains("public BetaLink beta()"), "Expected bounded AlphaLink dependency method");
            assertTrue(alpha.contains("return new BetaLink();"), "Expected dependent collaborator method to construct BetaLink");
            assertTrue(beta.contains("public String value()"), "Expected bounded BetaLink value method");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected dependent collaborator chain to validate after synthesis");
        }

        System.out.println("DependentCollaboratorSynthesisTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\dependent-collaborator-' + $PID)
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
                & $java.Source -cp $classesDir 'demo.tests.DependentCollaboratorChainTest'
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
