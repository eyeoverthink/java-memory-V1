package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies that repair-oriented tasks stay focused on verifier-guided repairs
 * instead of generic digest or scaffold proposals.
 */
public final class RepairGoalProposalFocusTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("repair-goal-proposals");
        Path sourceRoot = workspace.resolve(Path.of("src", "main", "java"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "multistep")));
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "support")));
        Files.createDirectories(scriptsDir);

        Files.writeString(sourceRoot.resolve(Path.of("demo", "multistep", "UseMultiStep.java")), """
            package demo.multistep;

            public final class UseMultiStep {
                public String render() {
                    MultiStepSupport helper = new MultiStepSupport();
                    return helper.value();
                }
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceRoot.resolve(Path.of("demo", "support", "MultiStepSupport.java")), """
            package demo.support;

            public final class MultiStepSupport {
                public String value() {
                    return "ok";
                }
            }
            """, StandardCharsets.UTF_8);
        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair multi-step parser and import failures while keeping the build offline")
                .addArtifact(Path.of("src/main/java/demo/multistep/UseMultiStep.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            List<MutationProposal> proposals = kernel.proposeMutations(task, 10, baseline.getVerifierResult());
            assertTrue(!proposals.isEmpty(), "Expected repair proposals for the broken workspace");
            for (MutationProposal proposal : proposals) {
                assertTrue(!proposal.getTitle().startsWith("Workspace digest"),
                    "Repair goals should suppress generic workspace digests");
                assertTrue(!proposal.getTitle().startsWith("Generated test scaffold"),
                    "Repair goals should suppress generic generated tests");
            }
        }

        System.out.println("RepairGoalProposalFocusTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\repair-goal-' + $PID)
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
