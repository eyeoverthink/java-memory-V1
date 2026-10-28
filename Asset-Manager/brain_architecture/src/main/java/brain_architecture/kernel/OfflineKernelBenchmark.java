package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple runner for lexical vs HDC vs hybrid retrieval comparison.
 */
public final class OfflineKernelBenchmark {
    public static void main(String[] args) throws Exception {
        Path workspace = Path.of(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
        String query = args.length > 1 ? args[1] : "offline kernel";
        List<Path> artifacts = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            artifacts.add(Path.of(args[i]));
        }

        try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace))) {
            MemoryBenchmarkReport report = kernel.benchmarkMemory(query, artifacts, 5);
            System.out.println("Winner: " + report.getWinner());
            System.out.println(report.toSummaryMap());
            for (var entry : report.getHitsByProfile().entrySet()) {
                System.out.println("[" + entry.getKey() + "]");
                for (MemoryHit hit : entry.getValue()) {
                    System.out.println(hit.getDocument().getSourceId() + " -> " + hit.getTotalScore());
                }
            }
        }
    }
}
