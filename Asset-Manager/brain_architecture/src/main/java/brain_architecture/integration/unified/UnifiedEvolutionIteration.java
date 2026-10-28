package brain_architecture.integration.unified;

import brain_architecture.kernel.EvolutionReport;
import brain_architecture.kernel.MutationProposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified bounded evolution iteration summary.
 */
public final class UnifiedEvolutionIteration {
    private final int iteration;
    private final String selectedProposalTitle;
    private final String selectionReason;
    private final boolean promoted;
    private final List<UnifiedEvolutionEvaluation> evaluations;

    public UnifiedEvolutionIteration(
            int iteration,
            String selectedProposalTitle,
            String selectionReason,
            boolean promoted,
            List<UnifiedEvolutionEvaluation> evaluations) {
        this.iteration = iteration;
        this.selectedProposalTitle = selectedProposalTitle;
        this.selectionReason = selectionReason == null ? "" : selectionReason;
        this.promoted = promoted;
        this.evaluations = Collections.unmodifiableList(new ArrayList<>(evaluations == null ? List.of() : evaluations));
    }

    public static UnifiedEvolutionIteration fromIteration(EvolutionReport.EvolutionIteration iteration) {
        if (iteration == null) {
            return new UnifiedEvolutionIteration(0, null, "", false, List.of());
        }
        MutationProposal selected = iteration.proposal();
        return new UnifiedEvolutionIteration(
            iteration.iteration(),
            selected == null ? null : selected.getTitle(),
            iteration.selectionReason(),
            iteration.promoted(),
            iteration.evaluations().stream()
                .map(evaluation -> UnifiedEvolutionEvaluation.fromEvaluation(evaluation, selected))
                .toList()
        );
    }

    public int getIteration() {
        return iteration;
    }

    public String getSelectedProposalTitle() {
        return selectedProposalTitle;
    }

    public String getSelectionReason() {
        return selectionReason;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public List<UnifiedEvolutionEvaluation> getEvaluations() {
        return evaluations;
    }
}
