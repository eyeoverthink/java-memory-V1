package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scored retrieval hit from the hybrid memory store.
 */
public final class MemoryHit {
    private final MemoryDocument document;
    private final double lexicalScore;
    private final double graphScore;
    private final double hdcScore;
    private final double recencyScore;
    private final double causalScore;
    private final double spuriousPenalty;
    private final boolean causallyGrounded;
    private final List<String> causalNotes;
    private final double totalScore;

    public MemoryHit(
            MemoryDocument document,
            double lexicalScore,
            double graphScore,
            double hdcScore,
            double recencyScore,
            double causalScore,
            double spuriousPenalty,
            boolean causallyGrounded,
            List<String> causalNotes,
            double totalScore) {
        this.document = document;
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

    public MemoryDocument getDocument() {
        return document;
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
