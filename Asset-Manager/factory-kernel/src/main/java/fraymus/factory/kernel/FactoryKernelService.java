package fraymus.factory.kernel;

import brain_architecture.integration.unified.UnifiedKernelGateway;
import brain_architecture.integration.unified.UnifiedTask;
import brain_architecture.kernel.*;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/** The studio and headless launcher use the same actual kernel/gateway and durable workspace. */
public final class FactoryKernelService implements AutoCloseable {
    public record Receipt(UUID taskId, boolean passed, String outcome, String strategy, String adapter, String tracePath) {}
    public record MemoryResult(String path, String preview, double score) {}
    public record Monitor(java.time.Instant at, Map<String, String> status, List<MemoryResult> hits, List<String> receipts) {}
    private final Path workspace;
    private final OfflineKernel kernel;

    public FactoryKernelService(Path workspace) throws IOException {
        this.workspace = workspace.toAbsolutePath().normalize();
        this.kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(this.workspace));
    }

    public synchronized Receipt inspect(Path artifact) throws IOException {
        Path target = artifact.toAbsolutePath().normalize();
        if (!target.startsWith(workspace)) throw new IllegalArgumentException("Artifact must belong to this factory workspace");
        String hash;
        try { hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(target))); }
        catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
        String java = Path.of(System.getProperty("java.home"), "bin", System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win") ? "java.exe" : "java").toString();
        var check = new ValidationCommand("factory_artifact", workspace, List.of(java, "-cp", validatorClassPath(), ArtifactValidator.class.getName(), target.toString(), hash));
        var suite = new ValidationSuite(List.of(), List.of(check), List.of(), List.of("offline"));
        var task = UnifiedTask.builder("Inspect the saved factory scene and validate its local artifact.")
            .addArtifact(target).addConstraint("offline only")
            .strategy("factory.runId", UUID.randomUUID().toString())
            .strategy("unified.includeProposals", "false").strategy("unified.includeEvolution", "false").build();
        var result = new UnifiedKernelGateway(kernel).run(task, suite);
        Receipt receipt = new Receipt(result.getTaskId(), result.isValidationPassed(), result.getOutcome(), result.getStrategy(), result.getAdapterId(), result.getReplayTracePath());
        Path results = workspace.resolve("task-results"); Files.createDirectories(results);
        String text = "taskId=" + receipt.taskId() + "\npassed=" + receipt.passed() + "\noutcome=" + receipt.outcome()
            + "\nstrategy=" + receipt.strategy() + "\nadapter=" + receipt.adapter() + "\ntrace=" + receipt.tracePath() + "\nartifactSha256=" + hash + "\n";
        Files.writeString(results.resolve(receipt.taskId() + ".txt"), text, StandardOpenOption.CREATE_NEW);
        kernel.refreshProjectMemoryArtifacts(false);
        return receipt;
    }

    public synchronized Monitor monitor(String query) throws IOException {
        var status = new LinkedHashMap<String, String>();
        kernel.getStatus().forEach((key, value) -> status.put(key, String.valueOf(value)));
        var hits = query == null || query.isBlank() ? List.<MemoryResult>of() : kernel.getMemoryStore().search(query, List.of(), 8).stream()
            .map(hit -> new MemoryResult(String.valueOf(hit.getDocument().getPath()), hit.getDocument().getPreview(), hit.getTotalScore())).toList();
        var receipts = new ArrayList<String>();
        Path results = workspace.resolve("task-results");
        if (Files.isDirectory(results)) try (var files = Files.list(results)) {
            for (Path path : files.filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .sorted(Comparator.comparingLong(FactoryKernelService::modified).reversed()).limit(12).toList()) {
                receipts.add(Files.readString(path));
            }
        }
        return new Monitor(java.time.Instant.now(), Collections.unmodifiableMap(status), hits, List.copyOf(receipts));
    }

    private static long modified(Path path) {
        try { return Files.getLastModifiedTime(path).toMillis(); } catch (IOException error) { return 0; }
    }

    private static String validatorClassPath() throws IOException {
        try {
            return Path.of(ArtifactValidator.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (java.net.URISyntaxException error) { throw new IOException("Cannot locate artifact validator", error); }
    }

    @Override public synchronized void close() throws IOException { kernel.close(); }
}
