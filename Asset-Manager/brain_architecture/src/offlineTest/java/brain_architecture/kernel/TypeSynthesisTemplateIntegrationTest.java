package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies successful bounded type synthesis becomes reusable template memory.
 */
public final class TypeSynthesisTemplateIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("type-synthesis-template");
        Path sourceRoot = workspace.resolve(Path.of("src", "main", "java", "demo", "templates", "construct"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(scriptsDir);
        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        seedTypeSynthesisTemplate(configuration, sourceRoot);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            Number templateCount = (Number) kernel.getStatus().get("repairTemplates");
            assertTrue(templateCount.intValue() >= 1, "Expected synthesized type template memory");

            Files.writeString(sourceRoot.resolve("UseTemplateConstructAgain.java"), """
                package demo.templates.construct;

                public final class UseTemplateConstructAgain {
                    public String render() {
                        TemplateConstructSupportAgain helper = new TemplateConstructSupportAgain();
                        return helper.value();
                    }
                }
                """, StandardCharsets.UTF_8);

            TaskEnvelope task = TaskEnvelope.builder("construct missing type TemplateConstructSupportAgain and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/construct/UseTemplateConstructAgain.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            MutationProposal proposal = findProposal(
                kernel.proposeMutations(task, 6, baseline.getVerifierResult()),
                "Synthesize missing type TemplateConstructSupportAgain"
            );
            assertTrue(proposal.getEvidenceTags().contains("repair.template.type_synthesis"), "Expected type synthesis template evidence");
            assertTrue(proposal.getRationale().toLowerCase().contains("template"), "Expected rationale to mention template reuse");
        }

        System.out.println("TypeSynthesisTemplateIntegrationTest passed");
    }

    private static void seedTypeSynthesisTemplate(KernelConfiguration configuration, Path sourceRoot) throws Exception {
        Files.writeString(sourceRoot.resolve("UseTemplateConstruct.java"), """
            package demo.templates.construct;

            public final class UseTemplateConstruct {
                public String render() {
                    TemplateConstructSupport helper = new TemplateConstructSupport();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct missing type TemplateConstructSupport and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/construct/UseTemplateConstruct.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected initial type synthesis to seed template memory");
        }
    }

    private static MutationProposal findProposal(List<MutationProposal> proposals, String titleFragment) {
        return proposals.stream()
            .filter(proposal -> proposal.getTitle().contains(titleFragment))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Expected proposal containing: " + titleFragment));
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
            $buildDir = Join-Path $projectRoot ('build\\type-synthesis-template-' + $PID)
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
