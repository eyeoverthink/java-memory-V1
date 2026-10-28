package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies structured diagnostics capture file, line, column, and message.
 */
public final class VerifierDiagnosticsTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("verifier-diagnostics-workspace");
        Path sourceDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Path scriptsDir = workspace.resolve("scripts");
        Files.createDirectories(sourceDir);
        Files.createDirectories(scriptsDir);

        Path bomSource = sourceDir.resolve("BomFeature.java");
        byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = """
            package demo;
            public final class BomFeature {
                public String run() { return "ok"; }
            }
            """.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, combined, 0, bom.length);
        System.arraycopy(content, 0, combined, bom.length, content.length);
        Files.write(bomSource, combined);

        Files.writeString(workspace.resolve("offline-toolchain.toml"), """
            [kernel]
            runtime = "air-gapped"
            java = "17"
            verify = "scripts/verify-workspace.ps1"
            """, StandardCharsets.UTF_8);

        Files.writeString(scriptsDir.resolve("verify-workspace.ps1"), """
            Set-StrictMode -Version Latest
            $ErrorActionPreference = 'Stop'

            $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
            $projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
            $mainSource = Join-Path $projectRoot 'src\\main\\java'
            $buildDir = Join-Path $projectRoot ('build\\verifier-diagnostics-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'

            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) {
                throw 'javac was not found in PATH.'
            }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
                New-Item -ItemType Directory -Force -Path $classesDir | Out-Null
                $sources = Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                [System.IO.File]::WriteAllLines($sourcesFile, $sources)
                & $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
                if ($LASTEXITCODE -ne 0) {
                    throw "javac failed with exit code $LASTEXITCODE"
                }
            }
            finally {
                if (Test-Path $buildDir) {
                    Remove-Item -Recurse -Force $buildDir -ErrorAction SilentlyContinue
                }
            }
            """, StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        try (OfflineKernel kernel = new OfflineKernel(configuration)) {
            TaskEnvelope task = TaskEnvelope.builder("capture structured compiler diagnostics for bom source")
                .addArtifact(Path.of("src/main/java/demo/BomFeature.java"))
                .mode(TaskMode.MUTATION)
                .build();

            KernelRunResult result = kernel.runTask(task, ValidationSuite.empty());
            assertTrue(!result.getVerifierResult().isSuccess(), "Expected baseline failure for BOM source");
            VerifierDiagnostic diagnostic = result.getVerifierResult().getDiagnostics().stream()
                .filter(item -> item.getFile() != null && item.getFile().endsWith(Path.of("BomFeature.java")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected BomFeature diagnostic"));
            assertTrue(diagnostic.getSeverity() == VerifierDiagnostic.Severity.ERROR, "Expected error severity");
            assertTrue(diagnostic.getLine() != null && diagnostic.getLine() == 1, "Expected diagnostic on line 1");
            assertTrue(diagnostic.getColumn() != null && diagnostic.getColumn() == 1, "Expected caret column 1");
            assertTrue(diagnostic.getMessage().toLowerCase().contains("illegal character"), "Expected illegal character diagnostic");
        }

        System.out.println("VerifierDiagnosticsTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
