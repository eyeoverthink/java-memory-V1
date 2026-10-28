package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies mutations in a shadow workspace and promotes only after validation succeeds.
 */
public final class ShadowMutationEngine {
    private final KernelConfiguration configuration;
    private final KernelVerifier verifier;

    public ShadowMutationEngine(KernelConfiguration configuration, KernelVerifier verifier) {
        this.configuration = configuration;
        this.verifier = verifier;
    }

    public MutationPreview preview(MutationCandidate candidate, ValidationSuite suite) throws IOException {
        StagedMutation staged = stage(candidate, suite);
        return new MutationPreview(
            staged.riskAccepted(),
            staged.shadowRoot(),
            staged.backupRoot(),
            staged.verifierResult(),
            candidate.getOperations().stream().map(MutationOperation::getRelativePath).toList()
        );
    }

    public MutationReport apply(MutationCandidate candidate, ValidationSuite suite) throws IOException {
        return apply(candidate, suite, VerifierResult::isSuccess);
    }

    public MutationReport apply(
            MutationCandidate candidate,
            ValidationSuite suite,
            java.util.function.Predicate<VerifierResult> promotionPolicy) throws IOException {
        StagedMutation staged = stage(candidate, suite);
        List<Path> promotedFiles = new ArrayList<>();

        if (!staged.riskAccepted()) {
            return new MutationReport(false, false, null, null, staged.verifierResult(), List.of());
        }
        if (promotionPolicy == null || !promotionPolicy.test(staged.verifierResult())) {
            return new MutationReport(true, false, staged.shadowRoot(), staged.backupRoot(), staged.verifierResult(), List.of());
        }

        try {
            promote(candidate, staged.shadowRoot(), staged.backupRoot(), promotedFiles);
            return new MutationReport(true, true, staged.shadowRoot(), staged.backupRoot(), staged.verifierResult(), promotedFiles);
        } catch (IOException e) {
            restoreBackups(candidate, staged.backupRoot());
            throw e;
        }
    }

    private StagedMutation stage(MutationCandidate candidate, ValidationSuite suite) throws IOException {
        VerifierResult risk = verifier.assessMutationRisk(candidate);
        if (!risk.isSuccess()) {
            return new StagedMutation(false, null, null, risk);
        }

        Path shadowRoot = Files.createTempDirectory(configuration.getStateRoot(), "shadow-");
        Path backupRoot = Files.createTempDirectory(configuration.getStateRoot(), "backup-");

        try {
            copyRelevantRoots(candidate, suite, shadowRoot);
            applyOperations(candidate, shadowRoot);

            TaskEnvelope syntheticTask = TaskEnvelope.builder("Validate mutation " + candidate.getId())
                .mode(TaskMode.MUTATION)
                .build();
            ExecutionTrace trace = new ExecutionTrace(syntheticTask.getId(), configuration.getRandomSeed());
            VerifierResult validation = verifier.verify(syntheticTask, trace, suite, shadowRoot);
            return new StagedMutation(true, shadowRoot, backupRoot, validation);
        } catch (IOException e) {
            deleteTree(shadowRoot);
            deleteTree(backupRoot);
            throw e;
        }
    }

    private void copyRelevantRoots(MutationCandidate candidate, ValidationSuite suite, Path shadowRoot) throws IOException {
        Set<Path> roots = new LinkedHashSet<>();
        for (MutationOperation operation : candidate.getOperations()) {
            Path target = resolveWithinWorkspace(operation.getRelativePath());
            if (Files.exists(target)) {
                roots.add(relevantMutationRoot(target));
            } else if (target.getParent() != null) {
                roots.add(relevantMutationRoot(target));
            }
        }
        Path manifestPath = configuration.getWorkspaceRoot().resolve("offline-toolchain.toml");
        if (Files.exists(manifestPath)) {
            roots.add(manifestPath);
            OfflineToolchainManifest manifest = OfflineToolchainManifest.load(configuration.getWorkspaceRoot());
            for (String input : manifest.getShadowInputs().values()) {
                Path path = resolveWithinWorkspace(Path.of(input));
                if (!Files.exists(path) || !path.toRealPath().startsWith(configuration.getWorkspaceRoot().toRealPath())) {
                    throw new IOException("Missing or external shadow validation input: " + input);
                }
                roots.add(path);
            }
        }
        for (ValidationCommand command : suite.allCommands()) {
            Set<Path> commandPaths = resolveCommandWorkspacePaths(command);
            roots.addAll(commandPaths);
            if (command.getWorkingDirectory() != null) {
                Path workingDirectory = resolveWithinWorkspace(command.getWorkingDirectory());
                if (!workingDirectory.equals(configuration.getWorkspaceRoot()) || commandPaths.isEmpty()) {
                    roots.add(workingDirectory);
                }
            }
        }
        if (roots.isEmpty()) {
            roots.add(configuration.getWorkspaceRoot());
        }

        for (Path root : roots) {
            copyPath(root, shadowRoot.resolve(configuration.getWorkspaceRoot().relativize(root)));
        }
    }

    private Path relevantMutationRoot(Path target) {
        Path relative = configuration.getWorkspaceRoot().relativize(target);
        Path sourceRoot = detectStructuredSourceRoot(relative);
        if (sourceRoot != null) {
            return configuration.getWorkspaceRoot().resolve(sourceRoot).normalize();
        }
        return Files.isDirectory(target) ? target : target.getParent();
    }

    private Path detectStructuredSourceRoot(Path relativePath) {
        if (relativePath == null) {
            return null;
        }
        int nameCount = relativePath.getNameCount();
        for (int index = 0; index <= nameCount - 3; index++) {
            if (!"src".equals(relativePath.getName(index).toString())) {
                continue;
            }
            String scope = relativePath.getName(index + 1).toString();
            if (!("main".equals(scope) || "test".equals(scope) || "offlineTest".equals(scope))) {
                continue;
            }
            String language = relativePath.getName(index + 2).toString();
            if (!isStructuredSourceLanguage(language)) {
                continue;
            }
            return relativePath.subpath(0, index + 3);
        }
        return null;
    }

    private boolean isStructuredSourceLanguage(String name) {
        return switch (name) {
            case "java", "kotlin", "groovy", "resources", "rust", "python", "javascript", "js", "ts" -> true;
            default -> false;
        };
    }

    private Set<Path> resolveCommandWorkspacePaths(ValidationCommand command) throws IOException {
        Set<Path> paths = new LinkedHashSet<>();
        if (command == null || command.getCommand().isEmpty()) {
            return paths;
        }

        Path workingDirectory = command.getWorkingDirectory() == null
            ? configuration.getWorkspaceRoot()
            : resolveWithinWorkspace(command.getWorkingDirectory());
        for (String argument : command.getCommand()) {
            Path candidate = resolveCommandArgumentPath(argument, workingDirectory);
            if (candidate != null && Files.exists(candidate)) {
                paths.add(candidate);
            }
        }
        return paths;
    }

    private Path resolveCommandArgumentPath(String argument, Path workingDirectory) {
        if (argument == null || argument.isBlank()) {
            return null;
        }
        try {
            Path raw = Path.of(argument);
            Path candidate = raw.isAbsolute()
                ? raw.toAbsolutePath().normalize()
                : workingDirectory.resolve(raw).normalize();
            return candidate.startsWith(configuration.getWorkspaceRoot()) ? candidate : null;
        } catch (InvalidPathException ignored) {
            return null;
        }
    }

    private void copyPath(Path source, Path destination) throws IOException {
        if (source == null || !Files.exists(source)) {
            return;
        }
        if (Files.isRegularFile(source)) {
            Files.createDirectories(destination.getParent());
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.equals(source) && shouldIgnore(dir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path relative = source.relativize(dir);
                Path target = destination.resolve(relative);
                Files.createDirectories(target);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (shouldIgnore(file)) {
                    return FileVisitResult.CONTINUE;
                }
                Path relative = source.relativize(file);
                Path target = destination.resolve(relative);
                Files.createDirectories(target.getParent());
                Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void applyOperations(MutationCandidate candidate, Path shadowRoot) throws IOException {
        for (MutationOperation operation : candidate.getOperations()) {
            Path target = shadowRoot.resolve(operation.getRelativePath()).normalize();
            Files.createDirectories(target.getParent());
            switch (operation.getType()) {
                case WRITE_FILE -> Files.writeString(target, operation.getContent(), StandardCharsets.UTF_8);
                case APPEND_FILE -> Files.writeString(
                    target,
                    operation.getContent(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                );
                case SEARCH_REPLACE -> {
                    String existing = Files.exists(target)
                        ? Files.readString(target, StandardCharsets.UTF_8)
                        : "";
                    String updated = existing.replace(operation.getSearchText(), operation.getReplacementText());
                    Files.writeString(target, updated, StandardCharsets.UTF_8);
                }
            }
        }
    }

    private void promote(MutationCandidate candidate, Path shadowRoot, Path backupRoot, List<Path> promotedFiles) throws IOException {
        for (MutationOperation operation : candidate.getOperations()) {
            Path relativePath = operation.getRelativePath();
            Path original = resolveWithinWorkspace(relativePath);
            Path shadow = shadowRoot.resolve(relativePath).normalize();
            Path backup = backupRoot.resolve(relativePath).normalize();

            if (Files.exists(original)) {
                Files.createDirectories(backup.getParent());
                Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.createDirectories(original.getParent());
            Files.copy(shadow, original, StandardCopyOption.REPLACE_EXISTING);
            promotedFiles.add(relativePath);
        }
    }

    private void restoreBackups(MutationCandidate candidate, Path backupRoot) throws IOException {
        for (MutationOperation operation : candidate.getOperations()) {
            Path original = resolveWithinWorkspace(operation.getRelativePath());
            Path backup = backupRoot.resolve(operation.getRelativePath()).normalize();
            if (Files.exists(backup)) {
                Files.createDirectories(original.getParent());
                Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private boolean shouldIgnore(Path path) {
        if (path == null || path.getFileName() == null) {
            return false;
        }
        if (path.startsWith(configuration.getStateRoot())) {
            return true;
        }
        return configuration.getIgnoredNames().contains(path.getFileName().toString());
    }

    private Path resolveWithinWorkspace(Path relativePath) throws IOException {
        Path resolved = configuration.getWorkspaceRoot().resolve(relativePath).toAbsolutePath().normalize();
        if (!resolved.startsWith(configuration.getWorkspaceRoot())) {
            throw new IOException("Path escapes workspace: " + relativePath);
        }
        return resolved;
    }

    private static void deleteTree(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public record MutationReport(
            boolean riskAccepted,
            boolean promoted,
            Path shadowRoot,
            Path backupRoot,
            VerifierResult verifierResult,
            List<Path> promotedFiles) {
        public void cleanupStaging() throws IOException {
            deleteTree(shadowRoot);
            deleteTree(backupRoot);
        }
    }

    private record StagedMutation(
            boolean riskAccepted,
            Path shadowRoot,
            Path backupRoot,
            VerifierResult verifierResult) {
    }

    public static final class MutationPreview implements AutoCloseable {
        private final boolean riskAccepted;
        private final Path shadowRoot;
        private final Path backupRoot;
        private final VerifierResult verifierResult;
        private final List<Path> candidatePaths;

        public MutationPreview(
                boolean riskAccepted,
                Path shadowRoot,
                Path backupRoot,
                VerifierResult verifierResult,
                List<Path> candidatePaths) {
            this.riskAccepted = riskAccepted;
            this.shadowRoot = shadowRoot;
            this.backupRoot = backupRoot;
            this.verifierResult = verifierResult;
            this.candidatePaths = candidatePaths == null ? List.of() : List.copyOf(candidatePaths);
        }

        public boolean riskAccepted() {
            return riskAccepted;
        }

        public Path shadowRoot() {
            return shadowRoot;
        }

        public Path backupRoot() {
            return backupRoot;
        }

        public VerifierResult verifierResult() {
            return verifierResult;
        }

        public List<Path> candidatePaths() {
            return candidatePaths;
        }

        @Override
        public void close() throws IOException {
            deleteTree(shadowRoot);
            deleteTree(backupRoot);
        }
    }
}
