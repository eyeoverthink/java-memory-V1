package brain_architecture.integration.unified;

import brain_architecture.kernel.KernelRunResult;
import brain_architecture.kernel.MutationProposalPreview;
import brain_architecture.kernel.OfflineKernel;
import brain_architecture.kernel.EvolutionReport;
import brain_architecture.kernel.TaskEnvelope;
import brain_architecture.kernel.TaskMode;
import brain_architecture.kernel.ValidationSuite;
import brain_architecture.kernel.VerifierResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Phase A adapter that forwards unified contracts to the typed offline kernel runtime.
 */
public final class UnifiedKernelGateway {
    private final OfflineKernel kernel;

    public UnifiedKernelGateway(OfflineKernel kernel) {
        this.kernel = kernel;
    }

    public UnifiedResult run(UnifiedTask task, ValidationSuite suite) throws IOException {
        TaskEnvelope envelope = toEnvelope(task);
        ValidationSuite effectiveSuite = suite == null ? ValidationSuite.empty() : suite;
        KernelRunResult runResult = kernel.runTask(envelope, effectiveSuite);
        String outcome = runResult.getTrace() == null ? "unknown" : runResult.getTrace().getOutcome();
        List<MutationProposalPreview> proposalPreviews = shouldIncludeProposals(task, runResult)
            ? kernel.previewMutationProposals(envelope, effectiveSuite, resolveProposalLimit(task), runResult.getVerifierResult())
            : List.of();
        EvolutionReport evolutionPreview = shouldIncludeEvolution(task, runResult)
            ? kernel.previewEvolution(
                envelope,
                effectiveSuite,
                runResult,
                resolveEvolutionIterationLimit(task),
                resolveProposalLimit(task)
            )
            : null;
        return new UnifiedResult(
            envelope.getId(),
            runResult.getStrategy(),
            runResult.getAdapterId(),
            outcome,
            UnifiedDiagnostics.fromVerifierResult(runResult.getVerifierResult()),
            UnifiedExecutionTrace.fromTrace(runResult.getTrace()),
            runResult.getMemoryHits().stream().map(UnifiedMemoryHit::fromMemoryHit).toList(),
            proposalPreviews.stream().map(UnifiedMutationProposal::fromPreview).toList(),
            evolutionPreview == null
                ? List.of()
                : evolutionPreview.getIterations().stream().map(UnifiedEvolutionIteration::fromIteration).toList(),
            kernel.getRecentJournalEntriesForTask(envelope.getId(), resolveJournalLimit(task)).stream()
                .map(UnifiedJournalEntry::fromJournalEntry)
                .toList(),
            runResult.getReplayTracePath() == null ? null : runResult.getReplayTracePath().toString().replace('\\', '/')
        );
    }

    public static TaskEnvelope toEnvelope(UnifiedTask task) {
        TaskEnvelope.Builder builder = TaskEnvelope.builder(task.getGoal()).mode(task.getMode());
        for (String constraint : task.getConstraints()) {
            builder.addConstraint(constraint);
        }
        for (java.nio.file.Path artifact : task.getArtifacts()) {
            builder.addArtifact(artifact);
        }
        task.getStrategyMetadata().forEach(builder::strategy);
        return builder.build();
    }

    private boolean shouldIncludeProposals(UnifiedTask task, KernelRunResult runResult) {
        Boolean metadataOverride = booleanMetadata(task, "unified.includeProposals", "kernel.includeProposals", "includeProposals");
        if (metadataOverride != null) {
            return metadataOverride;
        }
        if (task.isIncludeProposals()) {
            return true;
        }
        VerifierResult verifierResult = runResult == null ? null : runResult.getVerifierResult();
        return task.getMode() == TaskMode.MUTATION || (verifierResult != null && !verifierResult.isSuccess());
    }

    private int resolveProposalLimit(UnifiedTask task) {
        Integer metadataLimit = integerMetadata(task, "unified.proposalLimit", "kernel.proposalLimit", "proposalLimit");
        if (metadataLimit != null && metadataLimit > 0) {
            return metadataLimit;
        }
        return Math.max(1, task.getProposalLimit());
    }

    private int resolveJournalLimit(UnifiedTask task) {
        Integer metadataLimit = integerMetadata(task, "unified.journalLimit", "kernel.journalLimit", "journalLimit");
        if (metadataLimit != null && metadataLimit > 0) {
            return metadataLimit;
        }
        return 12;
    }

    private boolean shouldIncludeEvolution(UnifiedTask task, KernelRunResult runResult) {
        Boolean metadataOverride = booleanMetadata(task, "unified.includeEvolution", "kernel.includeEvolution", "includeEvolution");
        if (metadataOverride != null) {
            return metadataOverride;
        }
        if (task.isIncludeEvolutionEvaluations()) {
            return true;
        }
        VerifierResult verifierResult = runResult == null ? null : runResult.getVerifierResult();
        return task.getMode() == TaskMode.MUTATION || (verifierResult != null && !verifierResult.isSuccess());
    }

    private int resolveEvolutionIterationLimit(UnifiedTask task) {
        Integer metadataLimit = integerMetadata(task, "unified.evolutionIterationLimit", "kernel.evolutionIterationLimit", "evolutionIterationLimit");
        if (metadataLimit != null && metadataLimit > 0) {
            return metadataLimit;
        }
        return Math.max(1, task.getEvolutionIterationLimit());
    }

    private Boolean booleanMetadata(UnifiedTask task, String... keys) {
        for (String key : keys) {
            String value = task.getStrategyMetadata().get(key);
            if (value == null || value.isBlank()) {
                continue;
            }
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if ("true".equals(normalized) || "yes".equals(normalized) || "1".equals(normalized)) {
                return Boolean.TRUE;
            }
            if ("false".equals(normalized) || "no".equals(normalized) || "0".equals(normalized)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    private Integer integerMetadata(UnifiedTask task, String... keys) {
        for (String key : keys) {
            String value = task.getStrategyMetadata().get(key);
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException ignored) {
                // Continue to the next metadata key.
            }
        }
        return null;
    }
}
