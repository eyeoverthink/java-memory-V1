package com.eyeoverthink.fraymus.genesis;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class GenesisSandbox {

    private final Path root;

    public GenesisSandbox() {
        this(Path.of(".fraynix", "sandbox"));
    }

    public GenesisSandbox(Path root) {
        this.root = root;
    }

    public SandboxResult verifyJavaSource(String className, String javaSource) {
        try {
            Files.createDirectories(root);
            Path runDir = root.resolve("run_" + System.currentTimeMillis());
            Files.createDirectories(runDir);

            Path src = runDir.resolve(className + ".java");
            Files.writeString(src, javaSource, StandardCharsets.UTF_8);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                return SandboxResult.fail("No system Java compiler available (need JDK, not JRE)");
            }

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
            Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(List.of(src.toFile()));

            List<String> options = List.of("-d", runDir.toString());

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, units);
            boolean ok = Boolean.TRUE.equals(task.call());

            String diagText = diagnostics.getDiagnostics().stream()
                .map(d -> d.getKind() + ": " + d.getMessage(Locale.ROOT))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");

            fileManager.close();

            if (!ok) {
                return SandboxResult.fail(diagText.isBlank() ? "Compilation failed" : diagText);
            }
            return SandboxResult.ok(runDir, diagText);
        } catch (IOException e) {
            return SandboxResult.fail(e.getMessage());
        }
    }

    public record SandboxResult(boolean success, String error, Path outputDir, String diagnostics) {
        public static SandboxResult ok(Path outputDir, String diagnostics) {
            return new SandboxResult(true, null, outputDir, diagnostics);
        }

        public static SandboxResult fail(String error) {
            return new SandboxResult(false, error, null, null);
        }
    }
}
