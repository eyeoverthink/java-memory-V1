package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Indexed local document.
 */
public final class MemoryDocument {
    private final String sourceId;
    private final Path path;
    private final String preview;
    private final Map<String, Integer> termFrequency;
    private final BitSet vector;
    private final Set<String> graphNeighbors;
    private final long indexedAt;
    private final boolean codeLike;
    private final boolean authoritative;

    public MemoryDocument(
            String sourceId,
            Path path,
            String preview,
            Map<String, Integer> termFrequency,
            BitSet vector,
            Set<String> graphNeighbors,
            long indexedAt,
            boolean codeLike,
            boolean authoritative) {
        this.sourceId = sourceId;
        this.path = path;
        this.preview = preview;
        this.termFrequency = Collections.unmodifiableMap(termFrequency);
        this.vector = vector;
        this.graphNeighbors = Collections.unmodifiableSet(graphNeighbors);
        this.indexedAt = indexedAt;
        this.codeLike = codeLike;
        this.authoritative = authoritative;
    }

    public String getSourceId() {
        return sourceId;
    }

    public Path getPath() {
        return path;
    }

    public String getPreview() {
        return preview;
    }

    public Map<String, Integer> getTermFrequency() {
        return termFrequency;
    }

    public BitSet getVector() {
        return vector;
    }

    public Set<String> getGraphNeighbors() {
        return graphNeighbors;
    }

    public long getIndexedAt() {
        return indexedAt;
    }

    public boolean isCodeLike() {
        return codeLike;
    }

    public boolean isAuthoritative() {
        return authoritative;
    }
}
