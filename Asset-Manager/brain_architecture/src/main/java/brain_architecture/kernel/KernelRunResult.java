package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.List;

/**
 * Result of a kernel task run.
 */
public final class KernelRunResult {
    private final String strategy;
    private final String adapterId;
    private final List<MemoryHit> memoryHits;
    private final ExecutionTrace trace;
    private final VerifierResult verifierResult;
    private final Path replayTracePath;

    public KernelRunResult(
            String strategy,
            String adapterId,
            List<MemoryHit> memoryHits,
            ExecutionTrace trace,
            VerifierResult verifierResult) {
        this(strategy, adapterId, memoryHits, trace, verifierResult, null);
    }

    public KernelRunResult(
            String strategy,
            String adapterId,
            List<MemoryHit> memoryHits,
            ExecutionTrace trace,
            VerifierResult verifierResult,
            Path replayTracePath) {
        this.strategy = strategy;
        this.adapterId = adapterId;
        this.memoryHits = memoryHits == null ? List.of() : List.copyOf(memoryHits);
        this.trace = trace;
        this.verifierResult = verifierResult;
        this.replayTracePath = replayTracePath == null ? null : replayTracePath.toAbsolutePath().normalize();
    }

    public String getStrategy() {
        return strategy;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public List<MemoryHit> getMemoryHits() {
        return memoryHits;
    }

    public ExecutionTrace getTrace() {
        return trace;
    }

    public VerifierResult getVerifierResult() {
        return verifierResult;
    }

    public Path getReplayTracePath() {
        return replayTracePath;
    }
}
