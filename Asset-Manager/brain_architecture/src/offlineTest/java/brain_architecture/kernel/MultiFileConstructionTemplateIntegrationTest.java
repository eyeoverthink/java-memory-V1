package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies collaborator-set synthesis becomes reusable construction template memory.
 */
public final class MultiFileConstructionTemplateIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("multi-file-construction-template");
        Path testRoot = workspace.resolve(Path.of("src", "offlineTest", "java", "demo", "tests"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(testRoot);
        Files.createDirectories(scriptsDir);
        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        seedCollaboratorTemplate(configuration, testRoot);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            Files.writeString(testRoot.resolve("MissingTemplateCollaboratorSetAgainTest.java"), """
                package demo.tests;

                import demo.construct.Gamma;
                import demo.construct.Delta;

                public final class MissingTemplateCollaboratorSetAgainTest {
                    public static void main(String[] args) {
                        Gamma gamma = new Gamma();
                        Delta delta = new Delta();
                        String value = gamma.value() + delta.value();
                        if (!"".equals(value)) {
                            throw new IllegalStateException("Expected deterministic empty string");
                        }
                        System.out.println("MissingTemplateCollaboratorSetAgainTest passed");
                    }
                }
                """, StandardCharsets.UTF_8);

            TaskEnvelope task = TaskEnvelope.builder("construct collaborator set Gamma and Delta from the failing test and keep the build offline")
                .addArtifact(Path.of("src/offlineTest/java/demo/tests/MissingTemplateCollaboratorSetAgainTest.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult baseline = kernel.runTask(task, ValidationSuite.empty());
            MutationProposal collaboratorSet = findProposal(
                kernel.proposeMutations(task, 8, baseline.getVerifierResult()),
                "Synthesize collaborator set Delta, Gamma"
            );
            assertTrue(collaboratorSet.getEvidenceTags().contains("repair.template.multi_type_synthesis"), "Expected multi-type construction template evidence");
            assertTrue(collaboratorSet.getRationale().toLowerCase().contains("template"), "Expected rationale to mention template reuse");
        }

        System.out.println("MultiFileConstructionTemplateIntegrationTest passed");
    }

    private static void seedCollaboratorTemplate(KernelConfiguration configuration, Path testRoot) throws Exception {
        Files.writeString(testRoot.resolve("MissingTemplateCollaboratorSetTest.java"), """
            package demo.tests;

            import demo.construct.TemplateAlpha;
            import demo.construct.TemplateBeta;

            public final class MissingTemplateCollaboratorSetTest {
                public static void main(String[] args) {
                    TemplateAlpha alpha = new TemplateAlpha();
                    TemplateBeta beta = new TemplateBeta();
                    String value = alpha.value() + beta.value();
                    if (!"".equals(value)) {
                        throw new IllegalStateException("Expected deterministic empty string");
                    }
                    System.out.println("MissingTemplateCollaboratorSetTest passed");
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("construct collaborator set TemplateAlpha and TemplateBeta from the failing test and keep the build offline")
                .addArtifact(Path.of("src/offlineTest/java/demo/tests/MissingTemplateCollaboratorSetTest.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected collaborator-set synthesis to seed template memory");
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
            $testSource = Join-Path $projectRoot 'src\\offlineTest\\java'
            $buildDir = Join-Path $projectRoot ('build\\multi-file-template-' + $PID)
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

                $candidateTests = @(
                    'demo.tests.MissingTemplateCollaboratorSetTest',
                    'demo.tests.MissingTemplateCollaboratorSetAgainTest'
                )
                foreach ($testClass in $candidateTests) {
                    try {
                        & $java.Source -cp $classesDir $testClass
                        if ($LASTEXITCODE -ne 0) { throw "java failed with exit code $LASTEXITCODE" }
                    }
                    catch {
                        if ($testClass -eq 'demo.tests.MissingTemplateCollaboratorSetAgainTest') {
                            continue
                        }
                        throw
                    }
                }
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
