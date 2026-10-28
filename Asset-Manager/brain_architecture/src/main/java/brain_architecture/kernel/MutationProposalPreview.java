package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Preview summary for a bounded mutation proposal after shadow validation.
 */
public final class MutationProposalPreview {
    private final MutationProposal proposal;
    private final boolean riskAccepted;
    private final boolean validationPassed;
    private final boolean validationImproved;
    private final List<Path> candidatePaths;
    private final VerifierResult verifierResult;

    public MutationProposalPreview(
            MutationProposal proposal,
            boolean riskAccepted,
            boolean validationPassed,
            boolean validationImproved,
            List<Path> candidatePaths,
            VerifierResult verifierResult) {
        this.proposal = proposal;
        this.riskAccepted = riskAccepted;
        this.validationPassed = validationPassed;
        this.validationImproved = validationImproved;
        this.candidatePaths = Collections.unmodifiableList(new ArrayList<>(candidatePaths == null ? List.of() : candidatePaths));
        this.verifierResult = verifierResult;
    }

    public MutationProposal getProposal() {
        return proposal;
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

    public List<Path> getCandidatePaths() {
        return candidatePaths;
    }

    public VerifierResult getVerifierResult() {
        return verifierResult;
    }
}
