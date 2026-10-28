package brain_architecture.integration.unified;

import brain_architecture.kernel.EvolutionReport;
import brain_architecture.kernel.MutationCandidate;
import brain_architecture.kernel.MutationProposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified scored evaluation of one candidate inside a bounded evolution iteration.
 */
public final class UnifiedEvolutionEvaluation {
    private final UnifiedMutationProposal proposal;
    private final double totalScore;
    private final boolean viable;
    private final boolean selected;
    private final boolean riskAccepted;
    private final boolean validationPassed;
    private final boolean validationImproved;
    private final int validationCommandDelta;
    private final boolean manifestAdded;
    private final List<String> notes;

    public UnifiedEvolutionEvaluation(
            UnifiedMutationProposal proposal,
            double totalScore,
            boolean viable,
            boolean selected,
            boolean riskAccepted,
            boolean validationPassed,
            boolean validationImproved,
            int validationCommandDelta,
            boolean manifestAdded,
            List<String> notes) {
        this.proposal = proposal;
        this.totalScore = totalScore;
        this.viable = viable;
        this.selected = selected;
        this.riskAccepted = riskAccepted;
        this.validationPassed = validationPassed;
        this.validationImproved = validationImproved;
        this.validationCommandDelta = validationCommandDelta;
        this.manifestAdded = manifestAdded;
        this.notes = Collections.unmodifiableList(new ArrayList<>(notes == null ? List.of() : notes));
    }

    public static UnifiedEvolutionEvaluation fromEvaluation(
            EvolutionReport.ProposalEvaluation evaluation,
            MutationProposal selectedProposal) {
        if (evaluation == null) {
            return new UnifiedEvolutionEvaluation(
                UnifiedMutationProposal.fromProposal(null),
                0.0,
                false,
                false,
                false,
                false,
                false,
                0,
                false,
                List.of()
            );
        }
        MutationProposal proposal = evaluation.proposal();
        boolean selected = proposal != null
            && selectedProposal != null
            && proposal.getCandidate() != null
            && selectedProposal.getCandidate() != null
            && proposal.getCandidate().getId().equals(selectedProposal.getCandidate().getId());
        return new UnifiedEvolutionEvaluation(
            UnifiedMutationProposal.fromProposal(proposal),
            evaluation.totalScore(),
            evaluation.viable(),
            selected,
            evaluation.riskAccepted(),
            evaluation.validationPassed(),
            evaluation.validationImproved(),
            evaluation.validationCommandDelta(),
            evaluation.manifestAdded(),
            evaluation.notes()
        );
    }

    public UnifiedMutationProposal getProposal() {
        return proposal;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public boolean isViable() {
        return viable;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isRiskAccepted() {
        return riskAccepted;
    }

    public boolean isValidationPassed() {
        return validationPassed;
    }

    public boolean isValidationImproved() {
        return validationImproved;
    }

    public int getValidationCommandDelta() {
        return validationCommandDelta;
    }

    public boolean isManifestAdded() {
        return manifestAdded;
    }

    public List<String> getNotes() {
        return notes;
    }
}
