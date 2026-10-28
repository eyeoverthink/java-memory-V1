package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the kernel can chain two bounded repairs across multiple evolution iterations.
 */
public final class MultiStepEvolutionTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("multi-step-evolution");
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

            EvolutionReport report = kernel.evolveTask(task, 2);
            long promotedIterations = report.getIterations().stream().filter(EvolutionReport.EvolutionIteration::promoted).count();
            assertTrue(promotedIterations >= 2, "Expected two promoted repair iterations for the multi-step scenario");

            String consumer = Files.readString(sourceRoot.resolve(Path.of("demo", "multistep", "UseMultiStep.java")), StandardCharsets.UTF_8);
            assertTrue(consumer.contains("import demo.support.MultiStepSupport;"), "Expected import repair in consumer");
            assertTrue(consumer.contains("return helper.value();"), "Expected consumer body to remain intact after parser repair");

            KernelRunResult repaired = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(repaired.getVerifierResult().isSuccess(), "Expected workspace to validate after chained repairs");
        }

        System.out.println("MultiStepEvolutionTest passed");
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
            $buildDir = Join-Path $projectRoot ('build\\multi-step-' + $PID)
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
