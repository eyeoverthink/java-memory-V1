package brain_architecture.kernel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Builds validation suites from manifests or local workspace heuristics.
 */
public final class ValidationSuiteFactory {
    private final KernelConfiguration configuration;

    public ValidationSuiteFactory(KernelConfiguration configuration) {
        this.configuration = configuration;
    }

    public WorkspaceProfile profileWorkspace() throws IOException {
        Path workspaceRoot = configuration.getWorkspaceRoot();
        Set<String> languages = detectLanguages(workspaceRoot);
        boolean hasGradle = Files.exists(workspaceRoot.resolve("build.gradle")) || Files.exists(workspaceRoot.resolve("build.gradle.kts"));
        boolean hasCargo = Files.exists(workspaceRoot.resolve("Cargo.toml"));
        boolean hasJavaSources = languages.contains("java");

        Map<String, Boolean> toolsAvailable = new LinkedHashMap<>();
        toolsAvailable.put("powershell", isCommandAvailable("powershell"));
        toolsAvailable.put("java", isCommandAvailable("java"));
        toolsAvailable.put("javac", isCommandAvailable("javac"));
        toolsAvailable.put("gradle", isCommandAvailable("gradle"));
        toolsAvailable.put("cargo", isCommandAvailable("cargo"));

        OfflineToolchainManifest manifest = OfflineToolchainManifest.load(workspaceRoot);
        ValidationSuite recommended = manifest != null
            ? fromManifest(manifest, toolsAvailable)
            : fromHeuristics(hasGradle, hasCargo, hasJavaSources, toolsAvailable);

        return new WorkspaceProfile(
            workspaceRoot,
            languages,
            manifest != null,
            hasGradle,
            hasCargo,
            hasJavaSources,
            recommended,
            toolsAvailable
        );
    }

    public ValidationSuite resolveDefaultSuite() throws IOException {
        return profileWorkspace().getRecommendedValidationSuite();
    }

    private ValidationSuite fromManifest(OfflineToolchainManifest manifest, Map<String, Boolean> toolsAvailable) {
        List<ValidationCommand> tests = new ArrayList<>();
        List<String> invariants = new ArrayList<>();
        invariants.add("offline");

        String kernelVerify = manifest.getValue("kernel", "verify");
        if (kernelVerify != null && toolsAvailable.getOrDefault("powershell", false)) {
            tests.add(new ValidationCommand(
                "kernel.verify",
                configuration.getWorkspaceRoot(),
                List.of("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File",
                    configuration.getWorkspaceRoot().resolve(kernelVerify).toString())
            ));
        }

        String rustVerify = manifest.getValue("rust", "verify");
        if (rustVerify != null && toolsAvailable.getOrDefault("powershell", false) && toolsAvailable.getOrDefault("cargo", false)) {
            tests.add(new ValidationCommand(
                "rust.verify",
                configuration.getWorkspaceRoot(),
                List.of("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File",
                    configuration.getWorkspaceRoot().resolve(rustVerify).toString())
            ));
        } else if (rustVerify != null) {
            invariants.add("rust.verify.skipped_without_cargo");
        }

        return new ValidationSuite(List.of(), tests, List.of(), invariants);
    }

    private ValidationSuite fromHeuristics(
            boolean hasGradle,
            boolean hasCargo,
            boolean hasJavaSources,
            Map<String, Boolean> toolsAvailable) {
        List<ValidationCommand> buildChecks = new ArrayList<>();
        List<ValidationCommand> tests = new ArrayList<>();
        List<String> invariants = new ArrayList<>();
        invariants.add("offline");

        if (hasJavaSources && toolsAvailable.getOrDefault("javac", false)) {
            buildChecks.add(new ValidationCommand("javac.version", configuration.getWorkspaceRoot(), List.of("javac", "-version")));
        }
        if (hasGradle && toolsAvailable.getOrDefault("gradle", false)) {
            tests.add(new ValidationCommand("gradle.test", configuration.getWorkspaceRoot(), List.of("gradle", "test")));
        }
        if (hasCargo && toolsAvailable.getOrDefault("cargo", false)) {
            tests.add(new ValidationCommand("cargo.test", configuration.getWorkspaceRoot(), List.of("cargo", "test")));
        }

        return new ValidationSuite(buildChecks, tests, List.of(), invariants);
    }

    private Set<String> detectLanguages(Path workspaceRoot) throws IOException {
        Set<String> languages = new LinkedHashSet<>();
        // Breadth first reaches source roots before deep archives. Prune state/build
        // directories rather than filtering after Files.walk has already entered them.
        java.util.ArrayDeque<Path> pending = new java.util.ArrayDeque<>();
        pending.add(workspaceRoot);
        Set<String> generated = Set.of("node_modules", ".venv", "venv", ".cache", ".codex-temp");
        while (!pending.isEmpty() && languages.size() < 4) {
            Path directory = pending.removeFirst();
            try (var entries = Files.newDirectoryStream(directory)) {
                for (Path path : entries) {
                    if (Files.isSymbolicLink(path)) continue;
                    if (Files.isDirectory(path)) {
                        String name = path.getFileName().toString();
                        if (workspaceRoot.relativize(path).getNameCount() < 12
                                && !configuration.getIgnoredNames().contains(name) && !generated.contains(name)) {
                            pending.addLast(path);
                        }
                        continue;
                    }
                    if (!Files.isRegularFile(path)) continue;
                    String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                    if (name.endsWith(".java")) {
                        languages.add("java");
                    } else if (name.endsWith(".rs")) {
                        languages.add("rust");
                    } else if (name.endsWith(".py")) {
                        languages.add("python");
                    } else if (name.endsWith(".ts") || name.endsWith(".js")) {
                        languages.add("javascript");
                    }
                    if (languages.size() == 4) return languages;
                }
            }
        }
        return languages;
    }

    private boolean isCommandAvailable(String command) {
        try {
            List<String> probeCommand;
            if ("powershell".equalsIgnoreCase(command)) {
                probeCommand = List.of("powershell", "-NoProfile", "-Command", "$PSVersionTable.PSVersion.ToString()");
            } else {
                probeCommand = List.of(command, "--version");
            }
            Process process = new ProcessBuilder(probeCommand)
                .redirectErrorStream(true)
                .start();
            process.getInputStream().readAllBytes();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
