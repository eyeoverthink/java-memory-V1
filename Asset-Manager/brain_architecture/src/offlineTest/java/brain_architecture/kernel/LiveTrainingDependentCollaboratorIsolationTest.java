package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies live-training collaborator synthesis remains local even when duplicate types exist elsewhere in the workspace.
 */
public final class LiveTrainingDependentCollaboratorIsolationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("live-training-dependent-isolation");
        Path liveRoot = workspace.resolve(Path.of("live-training", "src", "main", "java", "training", "dependent"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(liveRoot);
        Files.createDirectories(scriptsDir);

        Files.writeString(liveRoot.resolve("UseDependentChain.java"), """
            package training.dependent;

            public final class UseDependentChain {
                public String render() {
                    AlphaLink alpha = new AlphaLink();
                    BetaLink beta = alpha.beta();
                    return beta.value();
                }
            }
            """, StandardCharsets.UTF_8);

        // Decoy classes outside live-training simulate duplicate legacy symbols in the wider workspace.
        Path decoyRoot = workspace.resolve(Path.of("Java-Memory", "legacy", "decoy"));
        Files.createDirectories(decoyRoot);
        Files.writeString(decoyRoot.resolve("AlphaLink.java"), """
            package legacy.decoy;
            public final class AlphaLink { }
            """, StandardCharsets.UTF_8);
        Files.writeString(decoyRoot.resolve("BetaLink.java"), """
            package legacy.decoy;
            public final class BetaLink { }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct dependent collaborator chain in live training and keep the build offline")
                .addArtifact(Path.of("live-training/src/main/java/training/dependent/UseDependentChain.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected failing live-training dependent baseline");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 8, baseline.getVerifierResult());
            MutationProposal collaboratorSet = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Synthesize dependent collaborator set AlphaLink, BetaLink"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected local dependent collaborator-set synthesis proposal"));
            assertTrue(collaboratorSet.getEvidenceTags().contains("verifier.compiler.multi_type_synthesis"),
                "Expected multi-type synthesis evidence");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.promoted(), "Expected live-training dependent collaborator synthesis to promote");

            Path alphaPath = workspace.resolve(Path.of("live-training", "src", "main", "java", "training", "dependent", "AlphaLink.java"));
            Path betaPath = workspace.resolve(Path.of("live-training", "src", "main", "java", "training", "dependent", "BetaLink.java"));
            assertTrue(Files.exists(alphaPath), "Expected synthesized AlphaLink in live-training root");
            assertTrue(Files.exists(betaPath), "Expected synthesized BetaLink in live-training root");
        }

        System.out.println("LiveTrainingDependentCollaboratorIsolationTest passed");
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
            $mainSource = Join-Path $projectRoot 'live-training\\src\\main\\java'
            $buildDir = Join-Path $projectRoot ('build\\live-training-dependent-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'
            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) { throw 'javac was not found in PATH.' }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir,$classesDir | Out-Null
                $sources = @()
                if (Test-Path $mainSource) {
                    $sources += Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                }
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
