package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A distilled corpus seed that can bias future mutation proposals.
 */
public final class CorpusMutationSeed {
    private final PdfDistillationChunk.Label label;
    private final String sourceId;
    private final String excerpt;
    private final List<String> keywords;
    private final double causalScore;
    private final boolean causallyGrounded;
    private final List<String> causalNotes;

    public CorpusMutationSeed(
            PdfDistillationChunk.Label label,
            String sourceId,
            String excerpt,
            List<String> keywords,
            double causalScore,
            boolean causallyGrounded,
            List<String> causalNotes) {
        this.label = label;
        this.sourceId = sourceId;
        this.excerpt = excerpt == null ? "" : excerpt;
        this.keywords = Collections.unmodifiableList(new ArrayList<>(keywords == null ? List.of() : keywords));
        this.causalScore = causalScore;
        this.causallyGrounded = causallyGrounded;
        this.causalNotes = Collections.unmodifiableList(new ArrayList<>(causalNotes == null ? List.of() : causalNotes));
    }

    public PdfDistillationChunk.Label getLabel() {
        return label;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public double getCausalScore() {
        return causalScore;
    }

    public boolean isCausallyGrounded() {
        return causallyGrounded;
    }

    public List<String> getCausalNotes() {
        return causalNotes;
    }
}
