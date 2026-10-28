package brain_architecture.integration.unified;

import brain_architecture.kernel.MutationCandidate;
import brain_architecture.kernel.MutationOperation;
import brain_architecture.kernel.MutationProposal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified proposal view with bounded mutation summaries.
 */
public final class UnifiedMutationProposal {
    private final String title;
    private final String rationale;
    private final double confidence;
    private final double novelty;
    private final double priorityScore;
    private final List<String> targetPaths;
    private final String scope;
    private final String expectedGain;
    private final List<String> risks;
    private final List<String> evidenceTags;
    private final List<UnifiedMutationOperation> operations;
    private final UnifiedProposalPreview preview;

    public UnifiedMutationProposal(
            String title,
            String rationale,
            double confidence,
            double novelty,
            double priorityScore,
            List<String> targetPaths,
            String scope,
            String expectedGain,
            List<String> risks,
            List<String> evidenceTags,
            List<UnifiedMutationOperation> operations,
            UnifiedProposalPreview preview) {
        this.title = title == null ? "" : title;
        this.rationale = rationale == null ? "" : rationale;
        this.confidence = confidence;
        this.novelty = novelty;
        this.priorityScore = priorityScore;
        this.targetPaths = Collections.unmodifiableList(new ArrayList<>(targetPaths == null ? List.of() : targetPaths));
        this.scope = scope == null ? MutationCandidate.Scope.WHOLE_CODEBASE.name() : scope;
        this.expectedGain = expectedGain == null ? "" : expectedGain;
        this.risks = Collections.unmodifiableList(new ArrayList<>(risks == null ? List.of() : risks));
        this.evidenceTags = Collections.unmodifiableList(new ArrayList<>(evidenceTags == null ? List.of() : evidenceTags));
        this.operations = Collections.unmodifiableList(new ArrayList<>(operations == null ? List.of() : operations));
        this.preview = preview;
    }

    public static UnifiedMutationProposal fromProposal(MutationProposal proposal) {
        return fromProposal(proposal, null);
    }

    public static UnifiedMutationProposal fromPreview(brain_architecture.kernel.MutationProposalPreview preview) {
        if (preview == null) {
            return fromProposal(null, null);
        }
        return fromProposal(preview.getProposal(), UnifiedProposalPreview.fromPreview(preview));
    }

    private static UnifiedMutationProposal fromProposal(MutationProposal proposal, UnifiedProposalPreview preview) {
        if (proposal == null) {
            return new UnifiedMutationProposal("", "", 0.0, 0.0, 0.0, List.of(), MutationCandidate.Scope.WHOLE_CODEBASE.name(), "", List.of(), List.of(), List.of(), preview);
        }
        MutationCandidate candidate = proposal.getCandidate();
        List<String> targetPaths = proposal.getTargetPaths().stream()
            .map(UnifiedMutationProposal::normalizePath)
            .toList();
        List<UnifiedMutationOperation> operations = candidate == null
            ? List.of()
            : candidate.getOperations().stream().map(UnifiedMutationOperation::fromOperation).toList();
        return new UnifiedMutationProposal(
            proposal.getTitle(),
            proposal.getRationale(),
            proposal.getConfidence(),
            proposal.getNovelty(),
            proposal.getPriorityScore(),
            targetPaths,
            candidate == null ? MutationCandidate.Scope.WHOLE_CODEBASE.name() : candidate.getScope().name(),
            candidate == null ? "" : candidate.getExpectedGain(),
            candidate == null ? List.of() : candidate.getRisks(),
            proposal.getEvidenceTags(),
            operations,
            preview
        );
    }

    private static String normalizePath(Path path) {
        return path == null ? null : path.toString().replace('\\', '/');
    }

    public String getTitle() {
        return title;
    }

    public String getRationale() {
        return rationale;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getNovelty() {
        return novelty;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public List<String> getTargetPaths() {
        return targetPaths;
    }

    public String getScope() {
        return scope;
    }

    public String getExpectedGain() {
        return expectedGain;
    }

    public List<String> getRisks() {
        return risks;
    }

    public List<String> getEvidenceTags() {
        return evidenceTags;
    }

    public List<UnifiedMutationOperation> getOperations() {
        return operations;
    }

    public UnifiedProposalPreview getPreview() {
        return preview;
    }

    public static final class UnifiedMutationOperation {
        private final String type;
        private final String relativePath;
        private final int payloadLength;

        public UnifiedMutationOperation(String type, String relativePath, int payloadLength) {
            this.type = type == null ? "" : type;
            this.relativePath = relativePath;
            this.payloadLength = payloadLength;
        }

        public static UnifiedMutationOperation fromOperation(MutationOperation operation) {
            int payloadLength = 0;
            if (operation.getContent() != null) {
                payloadLength = operation.getContent().length();
            } else if (operation.getReplacementText() != null) {
                payloadLength = operation.getReplacementText().length();
            } else if (operation.getSearchText() != null) {
                payloadLength = operation.getSearchText().length();
            }
            return new UnifiedMutationOperation(
                operation.getType().name(),
                normalizePath(operation.getRelativePath()),
                payloadLength
            );
        }

        public String getType() {
            return type;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public int getPayloadLength() {
            return payloadLength;
        }
    }
}
