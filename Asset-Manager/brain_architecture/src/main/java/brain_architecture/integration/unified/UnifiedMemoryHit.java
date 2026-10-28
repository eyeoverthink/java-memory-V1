package brain_architecture.integration.unified;

import brain_architecture.kernel.MemoryDocument;
import brain_architecture.kernel.MemoryHit;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified memory retrieval hit including causal grounding fields.
 */
public final class UnifiedMemoryHit {
    private final String sourceId;
    private final String path;
    private final String preview;
    private final boolean codeLike;
    private final double lexicalScore;
    private final double graphScore;
    private final double hdcScore;
    private final double recencyScore;
    private final double causalScore;
    private final double spuriousPenalty;
    private final boolean causallyGrounded;
    private final List<String> causalNotes;
    private final double totalScore;

    public UnifiedMemoryHit(
            String sourceId,
            String path,
            String preview,
            boolean codeLike,
            double lexicalScore,
            double graphScore,
            double hdcScore,
            double recencyScore,
            double causalScore,
            double spuriousPenalty,
            boolean causallyGrounded,
            List<String> causalNotes,
            double totalScore) {
        this.sourceId = sourceId == null ? "" : sourceId;
        this.path = path;
        this.preview = preview == null ? "" : preview;
        this.codeLike = codeLike;
        this.lexicalScore = lexicalScore;
        this.graphScore = graphScore;
        this.hdcScore = hdcScore;
        this.recencyScore = recencyScore;
        this.causalScore = causalScore;
        this.spuriousPenalty = spuriousPenalty;
        this.causallyGrounded = causallyGrounded;
        this.causalNotes = Collections.unmodifiableList(new ArrayList<>(causalNotes == null ? List.of() : causalNotes));
        this.totalScore = totalScore;
    }

    public static UnifiedMemoryHit fromMemoryHit(MemoryHit hit) {
        if (hit == null) {
            return new UnifiedMemoryHit("", null, "", false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false, List.of(), 0.0);
        }
        MemoryDocument document = hit.getDocument();
        Path path = document == null ? null : document.getPath();
        return new UnifiedMemoryHit(
            document == null ? "" : document.getSourceId(),
            path == null ? null : path.toString().replace('\\', '/'),
            document == null ? "" : document.getPreview(),
            document != null && document.isCodeLike(),
            hit.getLexicalScore(),
            hit.getGraphScore(),
            hit.getHdcScore(),
            hit.getRecencyScore(),
            hit.getCausalScore(),
            hit.getSpuriousPenalty(),
            hit.isCausallyGrounded(),
            hit.getCausalNotes(),
            hit.getTotalScore()
        );
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getPath() {
        return path;
    }

    public String getPreview() {
        return preview;
    }

    public boolean isCodeLike() {
        return codeLike;
    }

    public double getLexicalScore() {
        return lexicalScore;
    }

    public double getGraphScore() {
        return graphScore;
    }

    public double getHdcScore() {
        return hdcScore;
    }

    public double getRecencyScore() {
        return recencyScore;
    }

    public double getCausalScore() {
        return causalScore;
    }

    public double getSpuriousPenalty() {
        return spuriousPenalty;
    }

    public boolean isCausallyGrounded() {
        return causallyGrounded;
    }

    public List<String> getCausalNotes() {
        return causalNotes;
    }

    public double getTotalScore() {
        return totalScore;
    }
}
