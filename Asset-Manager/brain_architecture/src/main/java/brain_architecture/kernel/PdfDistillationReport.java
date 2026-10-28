package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Summary of a PDF distillation pass.
 */
public final class PdfDistillationReport {
    private final List<Path> sourceFiles;
    private final Path distilledRoot;
    private final int rawChunkCount;
    private final int uniqueChunkCount;
    private final int duplicateChunkCount;
    private final Map<PdfDistillationChunk.Label, Integer> labelCounts;
    private final List<PdfDistillationChunk> chunks;

    public PdfDistillationReport(
            List<Path> sourceFiles,
            Path distilledRoot,
            int rawChunkCount,
            int uniqueChunkCount,
            int duplicateChunkCount,
            Map<PdfDistillationChunk.Label, Integer> labelCounts,
            List<PdfDistillationChunk> chunks) {
        this.sourceFiles = Collections.unmodifiableList(new ArrayList<>(sourceFiles == null ? List.of() : sourceFiles));
        this.distilledRoot = distilledRoot;
        this.rawChunkCount = rawChunkCount;
        this.uniqueChunkCount = uniqueChunkCount;
        this.duplicateChunkCount = duplicateChunkCount;
        this.labelCounts = labelCounts == null ? Map.of() : Map.copyOf(labelCounts);
        this.chunks = Collections.unmodifiableList(new ArrayList<>(chunks == null ? List.of() : chunks));
    }

    public List<Path> getSourceFiles() {
        return sourceFiles;
    }

    public Path getDistilledRoot() {
        return distilledRoot;
    }

    public int getRawChunkCount() {
        return rawChunkCount;
    }

    public int getUniqueChunkCount() {
        return uniqueChunkCount;
    }

    public int getDuplicateChunkCount() {
        return duplicateChunkCount;
    }

    public Map<PdfDistillationChunk.Label, Integer> getLabelCounts() {
        return labelCounts;
    }

    public List<PdfDistillationChunk> getChunks() {
        return chunks;
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("sourceFiles", sourceFiles.stream().map(Path::toString).toList());
        summary.put("distilledRoot", distilledRoot == null ? "" : distilledRoot.toString());
        summary.put("rawChunks", rawChunkCount);
        summary.put("uniqueChunks", uniqueChunkCount);
        summary.put("duplicatesRemoved", duplicateChunkCount);
        Map<String, Integer> labels = new LinkedHashMap<>();
        for (PdfDistillationChunk.Label label : PdfDistillationChunk.Label.values()) {
            labels.put(label.getSlug(), labelCounts.getOrDefault(label, 0));
        }
        summary.put("labels", labels);
        return summary;
    }
}
