package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies stored repair templates directly shape future mutation proposals.
 */
public final class RepairTemplateIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("repair-template-integration");
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
        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope learnTask = TaskEnvelope.builder("repair unresolved method buildValue and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/UseTool.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(learnTask, 1);
            assertTrue(report.hasPromotedMutation(), "Expected a promoted repair to seed template memory");
        }

        Files.writeString(demoDir.resolve("UseAgain.java"), """
            package demo;

            public final class UseAgain {
                public void run() {
                    String text;
                    text = loadValue();
                    System.out.println(text);
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel reopened = new OfflineKernel(configuration)) {
            TaskEnvelope templateTask = TaskEnvelope.builder("repair unresolved method loadValue and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/UseAgain.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = reopened.runTask(templateTask, ValidationSuite.empty());
            assertTrue(!baseline.getVerifierResult().isSuccess(), "Expected second unresolved method baseline failure");

            List<MutationProposal> proposals = reopened.proposeMutations(templateTask, 6, baseline.getVerifierResult());
            MutationProposal methodRepair = proposals.stream()
                .filter(proposal -> proposal.getTitle().contains("Add missing method stub loadValue"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected template-guided method repair proposal"));
            assertTrue(methodRepair.getEvidenceTags().contains("repair.template"), "Expected repair template evidence");
            assertTrue(methodRepair.getRationale().toLowerCase().contains("template"), "Expected rationale to mention template reuse");

            String generatedContent = methodRepair.getCandidate().getOperations().get(0).getContent();
            assertTrue(generatedContent.contains("private String loadValue()"), "Expected template to upgrade method stub return type to String");

            EvolutionReport report = reopened.evolveTask(templateTask, 1);
            assertTrue(report.hasPromotedMutation(), "Expected template-guided repair to promote successfully");
        }

        System.out.println("RepairTemplateIntegrationTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\repair-template-' + $PID)
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
