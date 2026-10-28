package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies import, signature, and package repairs also become reusable templates.
 */
public final class RepairTemplateExpansionTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("repair-template-expansion");
        Path sourceRoot = workspace.resolve(Path.of("src", "main", "java"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(scriptsDir);
        writeOfflineManifest(workspace, scriptsDir.resolve("verify-workspace.ps1"));

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        seedImportTemplate(configuration);
        seedSignatureTemplate(configuration);
        seedPackageTemplate(configuration);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            Number templateCount = (Number) kernel.getStatus().get("repairTemplates");
            assertTrue(templateCount.intValue() >= 3, "Expected import, signature, and package templates to be recorded");

            Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "imports", "UseTemplateImportAgain.java")), """
                package demo.templates.imports;

                public final class UseTemplateImportAgain {
                    public String render() {
                        TemplateImportHelper helper = new TemplateImportHelper();
                        return helper.value();
                    }
                }
                """, StandardCharsets.UTF_8);
            TaskEnvelope importTask = TaskEnvelope.builder("repair unresolved TemplateImportHelper type and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/imports/UseTemplateImportAgain.java"))
                .mode(TaskMode.MUTATION)
                .build();
            KernelRunResult importBaseline = kernel.runTask(importTask, ValidationSuite.empty());
            MutationProposal importProposal = findProposal(
                kernel.proposeMutations(importTask, 6, importBaseline.getVerifierResult()),
                "Add missing import for TemplateImportHelper"
            );
            assertTrue(importProposal.getEvidenceTags().contains("repair.template.import"), "Expected import template evidence");
            assertTrue(importProposal.getRationale().toLowerCase().contains("template"), "Expected import rationale to mention template reuse");

            Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "signature", "UseTemplateSignatureAgain.java")), """
                package demo.templates.signature;

                public final class UseTemplateSignatureAgain {
                    public String run() {
                        TemplateSignatureService service = new TemplateSignatureService();
                        return service.greet();
                    }
                }
                """, StandardCharsets.UTF_8);
            TaskEnvelope signatureTask = TaskEnvelope.builder("repair signature mismatch in template signature workspace and stay offline")
                .addArtifact(Path.of("src/main/java/demo/templates/signature/UseTemplateSignatureAgain.java"))
                .mode(TaskMode.MUTATION)
                .build();
            KernelRunResult signatureBaseline = kernel.runTask(signatureTask, ValidationSuite.empty());
            MutationProposal signatureProposal = findProposal(
                kernel.proposeMutations(signatureTask, 6, signatureBaseline.getVerifierResult()),
                "Patch signature mismatch"
            );
            assertTrue(signatureProposal.getEvidenceTags().contains("repair.template.signature"), "Expected signature template evidence");
            assertTrue(signatureProposal.getRationale().toLowerCase().contains("template"), "Expected signature rationale to mention template reuse");
            assertTrue(signatureProposal.getCandidate().getOperations().get(0).getContent().contains("greet(\"\")"),
                "Expected signature template proposal to keep deterministic String defaults");

            Path packageHelperAgain = sourceRoot.resolve(Path.of("demo", "templates", "packagefix", "TemplatePackageHelperAgain.java"));
            Files.writeString(packageHelperAgain, """
                package drifted.again;

                public final class TemplatePackageHelperAgain {
                    public String value() {
                        return "ok";
                    }
                }
                """, StandardCharsets.UTF_8);
            Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "packagefix", "UseTemplatePackageAgain.java")), """
                package demo.templates.packagefix;

                public final class UseTemplatePackageAgain {
                    public String run() {
                        TemplatePackageHelperAgain helper = new TemplatePackageHelperAgain();
                        return helper.value();
                    }
                }
                """, StandardCharsets.UTF_8);
            TaskEnvelope packageTask = TaskEnvelope.builder("repair package declaration drift in template package workspace and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/packagefix/UseTemplatePackageAgain.java"))
                .mode(TaskMode.MUTATION)
                .build();
            KernelRunResult packageBaseline = kernel.runTask(packageTask, ValidationSuite.empty());
            MutationProposal packageProposal = findProposal(
                kernel.proposeMutations(packageTask, 8, packageBaseline.getVerifierResult()),
                "Align package declaration for TemplatePackageHelperAgain.java"
            );
            assertTrue(packageProposal.getEvidenceTags().contains("repair.template.package"), "Expected package template evidence");
            assertTrue(packageProposal.getRationale().toLowerCase().contains("template"), "Expected package rationale to mention template reuse");
        }

        System.out.println("RepairTemplateExpansionTest passed");
    }

    private static void seedImportTemplate(KernelConfiguration configuration) throws Exception {
        Path sourceRoot = configuration.getWorkspaceRoot().resolve(Path.of("src", "main", "java"));
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "templates", "imports")));
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "templates", "util")));
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "imports", "UseTemplateImport.java")), """
            package demo.templates.imports;

            public final class UseTemplateImport {
                public String render() {
                    TemplateImportHelper helper = new TemplateImportHelper();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "util", "TemplateImportHelper.java")), """
            package demo.templates.util;

            public final class TemplateImportHelper {
                public String value() {
                    return "ok";
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair unresolved TemplateImportHelper type and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/imports/UseTemplateImport.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected import repair to seed template memory");
        }
    }

    private static void seedSignatureTemplate(KernelConfiguration configuration) throws Exception {
        Path sourceRoot = configuration.getWorkspaceRoot().resolve(Path.of("src", "main", "java"));
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "templates", "signature")));
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "signature", "TemplateSignatureService.java")), """
            package demo.templates.signature;

            public final class TemplateSignatureService {
                public String greet(String name) {
                    return "hi " + name;
                }
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "signature", "UseTemplateSignature.java")), """
            package demo.templates.signature;

            public final class UseTemplateSignature {
                public String run() {
                    TemplateSignatureService service = new TemplateSignatureService();
                    return service.greet();
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair signature mismatch in template signature workspace and stay offline")
                .addArtifact(Path.of("src/main/java/demo/templates/signature/UseTemplateSignature.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected signature repair to seed template memory");
        }
    }

    private static void seedPackageTemplate(KernelConfiguration configuration) throws Exception {
        Path sourceRoot = configuration.getWorkspaceRoot().resolve(Path.of("src", "main", "java"));
        Files.createDirectories(sourceRoot.resolve(Path.of("demo", "templates", "packagefix")));
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "packagefix", "TemplatePackageHelper.java")), """
            package drifted.template;

            public final class TemplatePackageHelper {
                public String value() {
                    return "ok";
                }
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(sourceRoot.resolve(Path.of("demo", "templates", "packagefix", "UseTemplatePackage.java")), """
            package demo.templates.packagefix;

            public final class UseTemplatePackage {
                public String run() {
                    TemplatePackageHelper helper = new TemplatePackageHelper();
                    return helper.value();
                }
            }
            """, StandardCharsets.UTF_8);

        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("repair package declaration drift in template package workspace and keep the build offline")
                .addArtifact(Path.of("src/main/java/demo/templates/packagefix/UseTemplatePackage.java"))
                .mode(TaskMode.MUTATION)
                .build();
            EvolutionReport report = kernel.evolveTask(task, 1);
            assertTrue(report.hasPromotedMutation(), "Expected package repair to seed template memory");
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
            $buildDir = Join-Path $projectRoot ('build\\repair-template-expansion-' + $PID)
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
