package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A scored mutation proposal generated from a task goal.
 */
public final class MutationProposal {
    private final String title;
    private final String rationale;
    private final double confidence;
    private final double novelty;
    private final List<Path> targetPaths;
    private final MutationCandidate candidate;
    private final List<String> evidenceTags;

    public MutationProposal(
            String title,
            String rationale,
            double confidence,
            double novelty,
            List<Path> targetPaths,
            MutationCandidate candidate) {
        this(title, rationale, confidence, novelty, targetPaths, candidate, List.of());
    }

    public MutationProposal(
            String title,
            String rationale,
            double confidence,
            double novelty,
            List<Path> targetPaths,
            MutationCandidate candidate,
            List<String> evidenceTags) {
        this.title = title;
        this.rationale = rationale;
        this.confidence = confidence;
        this.novelty = novelty;
        this.targetPaths = Collections.unmodifiableList(new ArrayList<>(targetPaths == null ? List.of() : targetPaths));
        this.candidate = candidate;
        this.evidenceTags = Collections.unmodifiableList(new ArrayList<>(evidenceTags == null ? List.of() : evidenceTags));
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

    public List<Path> getTargetPaths() {
        return targetPaths;
    }

    public MutationCandidate getCandidate() {
        return candidate;
    }

    public List<String> getEvidenceTags() {
        return evidenceTags;
    }

    public double getPriorityScore() {
        return (0.7 * confidence) + (0.3 * novelty);
    }
}
