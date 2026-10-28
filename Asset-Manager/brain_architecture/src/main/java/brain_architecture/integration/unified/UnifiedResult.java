package brain_architecture.integration.unified;

import java.util.Objects;
import java.util.UUID;
import java.util.List;

/**
 * Enriched unified result contract for kernel-owned task execution.
 */
public final class UnifiedResult {
    private final UUID taskId;
    private final String strategy;
    private final String adapterId;
    private final String outcome;
    private final UnifiedDiagnostics diagnostics;
    private final UnifiedExecutionTrace trace;
    private final List<UnifiedMemoryHit> memoryHits;
    private final List<UnifiedMutationProposal> mutationProposals;
    private final List<UnifiedEvolutionIteration> evolutionIterations;
    private final List<UnifiedJournalEntry> journalEntries;
    private final String replayTracePath;

    public UnifiedResult(
            UUID taskId,
            String strategy,
            String adapterId,
            String outcome,
            UnifiedDiagnostics diagnostics) {
        this(taskId, strategy, adapterId, outcome, diagnostics, UnifiedExecutionTrace.empty(taskId), List.of(), List.of(), List.of(), List.of(), null);
    }

    public UnifiedResult(
            UUID taskId,
            String strategy,
            String adapterId,
            String outcome,
            UnifiedDiagnostics diagnostics,
            UnifiedExecutionTrace trace,
            List<UnifiedMemoryHit> memoryHits,
            List<UnifiedMutationProposal> mutationProposals,
            List<UnifiedEvolutionIteration> evolutionIterations,
            List<UnifiedJournalEntry> journalEntries,
            String replayTracePath) {
        this.taskId = Objects.requireNonNull(taskId, "taskId");
        this.strategy = strategy == null ? "unknown" : strategy;
        this.adapterId = adapterId == null ? "unknown" : adapterId;
        this.outcome = outcome == null ? "unknown" : outcome;
        this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
        this.trace = trace == null ? UnifiedExecutionTrace.empty(taskId) : trace;
        this.memoryHits = List.copyOf(memoryHits == null ? List.of() : memoryHits);
        this.mutationProposals = List.copyOf(mutationProposals == null ? List.of() : mutationProposals);
        this.evolutionIterations = List.copyOf(evolutionIterations == null ? List.of() : evolutionIterations);
        this.journalEntries = List.copyOf(journalEntries == null ? List.of() : journalEntries);
        this.replayTracePath = replayTracePath;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public String getOutcome() {
        return outcome;
    }

    public UnifiedDiagnostics getDiagnostics() {
        return diagnostics;
    }

    public UnifiedExecutionTrace getTrace() {
        return trace;
    }

    public List<UnifiedMemoryHit> getMemoryHits() {
        return memoryHits;
    }

    public List<UnifiedMutationProposal> getMutationProposals() {
        return mutationProposals;
    }

    public List<UnifiedEvolutionIteration> getEvolutionIterations() {
        return evolutionIterations;
    }

    public List<UnifiedJournalEntry> getJournalEntries() {
        return journalEntries;
    }

    public String getReplayTracePath() {
        return replayTracePath;
    }

    public boolean isValidationPassed() {
        return diagnostics.isValidationPassed();
    }
}
