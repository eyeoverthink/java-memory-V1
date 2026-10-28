package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of a bounded autonomous evolution pass.
 */
public final class EvolutionReport {
    private final TaskEnvelope task;
    private final KernelRunResult baselineRun;
    private final WorkspaceProfile baselineProfile;
    private final List<EvolutionIteration> iterations;

    public EvolutionReport(
            TaskEnvelope task,
            KernelRunResult baselineRun,
            WorkspaceProfile baselineProfile,
            List<EvolutionIteration> iterations) {
        this.task = task;
        this.baselineRun = baselineRun;
        this.baselineProfile = baselineProfile;
        this.iterations = Collections.unmodifiableList(new ArrayList<>(iterations == null ? List.of() : iterations));
    }

    public TaskEnvelope getTask() {
        return task;
    }

    public KernelRunResult getBaselineRun() {
        return baselineRun;
    }

    public WorkspaceProfile getBaselineProfile() {
        return baselineProfile;
    }

    public List<EvolutionIteration> getIterations() {
        return iterations;
    }

    public boolean hasPromotedMutation() {
        return iterations.stream().anyMatch(EvolutionIteration::promoted);
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("taskId", task.getId());
        summary.put("baselineOutcome", baselineRun == null ? "not-run" : baselineRun.getTrace().getOutcome());
        summary.put("baselineValidationCommands", baselineProfile == null
            ? 0
            : baselineProfile.getRecommendedValidationSuite().allCommands().size());
        summary.put("iterations", iterations.size());
        summary.put("promoted", hasPromotedMutation());
        return summary;
    }

    public record EvolutionIteration(
            int iteration,
            MutationProposal proposal,
            ShadowMutationEngine.MutationReport mutationReport,
            List<ProposalEvaluation> evaluations,
            String selectionReason) {
        public boolean promoted() {
            return mutationReport != null && mutationReport.promoted();
        }
    }

    public record ProposalEvaluation(
            MutationProposal proposal,
            double totalScore,
            boolean riskAccepted,
            boolean validationPassed,
            boolean validationImproved,
            int validationCommandDelta,
            boolean manifestAdded,
            List<String> notes) {
        public ProposalEvaluation {
            notes = notes == null ? List.of() : List.copyOf(notes);
        }

        public boolean viable() {
            return riskAccepted && (validationPassed || validationImproved);
        }
    }
}
