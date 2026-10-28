package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Verifies shadow mutation previews do not copy the whole workspace for root-level script validators.
 */
public final class ShadowWorkspaceMinimizationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("shadow-minimization");
        Path sourceDir = workspace.resolve(Path.of("src", "main", "java", "demo"));
        Path scriptsDir = workspace.resolve("scripts");
        Path archiveDir = workspace.resolve(Path.of("archive", "huge"));
        Files.createDirectories(sourceDir);
        Files.createDirectories(scriptsDir);
        Files.createDirectories(archiveDir);

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
            $buildDir = Join-Path $projectRoot ('build\\shadow-min-' + $PID)
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
        Files.writeString(sourceDir.resolve("UseTool.java"), """
            package demo;

            public final class UseTool {
                public String render() {
                    return buildValue();
                }
            }
            """, StandardCharsets.UTF_8);
        Files.writeString(archiveDir.resolve("massive.bin"), "do-not-copy", StandardCharsets.UTF_8);

        KernelConfiguration configuration = KernelConfiguration.defaultForWorkspace(workspace);
        Files.createDirectories(configuration.getStateRoot());
        ValidationSuite suite = new ValidationSuiteFactory(configuration).resolveDefaultSuite();
        ShadowMutationEngine mutationEngine = new ShadowMutationEngine(configuration, new KernelVerifier(configuration));
        MutationCandidate candidate = new MutationCandidate(
            MutationCandidate.Scope.WHOLE_CODEBASE,
            List.of(MutationOperation.writeFile(Path.of("src/main/java/demo/UseTool.java"), """
                package demo;

                public final class UseTool {
                    public String render() {
                        return buildValue();
                    }

                    private String buildValue() {
                        return "ok";
                    }
                }
                """)),
            "Patch missing method in target file",
            List.of("Shadow minimization test")
        );

        try (ShadowMutationEngine.MutationPreview preview = mutationEngine.preview(candidate, suite)) {
            assertTrue(preview.riskAccepted(), "Expected shadow preview risk assessment to pass");
            assertTrue(preview.verifierResult().isSuccess(), "Expected shadow preview validation to succeed");
            assertTrue(Files.exists(preview.shadowRoot().resolve("offline-toolchain.toml")),
                "Expected manifest to be copied into the shadow workspace");
            assertTrue(Files.exists(preview.shadowRoot().resolve(Path.of("scripts", "verify-workspace.ps1"))),
                "Expected verifier script to be copied into the shadow workspace");
            assertTrue(Files.exists(preview.shadowRoot().resolve(Path.of("src", "main", "java", "demo", "UseTool.java"))),
                "Expected mutated source file to be copied into the shadow workspace");
            assertTrue(!Files.exists(preview.shadowRoot().resolve(Path.of("archive", "huge", "massive.bin"))),
                "Expected unrelated workspace payloads to stay out of the shadow workspace");
        }

        System.out.println("ShadowWorkspaceMinimizationTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
