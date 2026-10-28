package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies unresolved type diagnostics can synthesize a bounded implementation class.
 */
public final class MissingTypeSynthesisRepairTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("missing-type-synthesis");
        Path sourceRoot = workspace.resolve(Path.of("src", "main", "java", "demo", "synthesis"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(scriptsDir);

        Files.writeString(sourceRoot.resolve("UseSynthesizedSupport.java"), """
            package demo.synthesis;

            public final class UseSynthesizedSupport {
                public String render() {
                    SynthesizedSupport helper = new SynthesizedSupport();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);

        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"), "missing-type-synthesis");
        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("synthesize missing type SynthesizedSupport and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/synthesis/UseSynthesizedSupport.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected unresolved type baseline failure");

            List<MutationProposal> proposals = kernel.proposeMutations(task, 6, baseline.getVerifierResult());
            MutationProposal synthesis = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Synthesize missing type SynthesizedSupport"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected missing type synthesis proposal"));
            assertTrue(synthesis.getEvidenceTags().contains("verifier.compiler.type_synthesis"), "Expected type synthesis evidence tag");

            EvolutionReport report = kernel.evolveTask(task, 1);
            EvolutionReport.EvolutionIteration iteration = report.getIterations().get(0);
            assertTrue(iteration.proposal() != null, "Expected selected proposal");
            assertTrue(iteration.proposal().getTitle().contains("Synthesize missing type SynthesizedSupport"), "Expected type synthesis to win");
            assertTrue(iteration.promoted(), "Expected type synthesis to be promoted");

            Path generatedType = sourceRoot.resolve("SynthesizedSupport.java");
            String content = Files.readString(generatedType, StandardCharsets.UTF_8);
            assertTrue(content.contains("public final class SynthesizedSupport"), "Expected synthesized support class");
            assertTrue(content.contains("public SynthesizedSupport()"), "Expected explicit no-arg constructor");
            assertTrue(content.contains("public String value()"), "Expected bounded String value method");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected workspace to verify after type synthesis");
        }

        System.out.println("MissingTypeSynthesisRepairTest passed");
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
                $sources = @(Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName)
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
