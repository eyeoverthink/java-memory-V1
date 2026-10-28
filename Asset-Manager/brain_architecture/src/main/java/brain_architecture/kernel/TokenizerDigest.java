package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Typed digest of a local tokenizer substrate.
 */
public record TokenizerDigest(
        String modelId,
        Path sourcePath,
        String format,
        int vocabSize,
        int mergeCount,
        int specialTokenCount,
        int maxTokenLength,
        List<String> sampleTokens,
        Map<String, String> attributes) {

    public TokenizerDigest {
        modelId = modelId == null || modelId.isBlank() ? "unknown-model" : modelId;
        sourcePath = sourcePath == null ? Path.of(".").toAbsolutePath().normalize() : sourcePath.toAbsolutePath().normalize();
        format = format == null || format.isBlank() ? "unknown" : format;
        sampleTokens = List.copyOf(new ArrayList<>(sampleTokens == null ? List.of() : sampleTokens));
        attributes = Map.copyOf(new LinkedHashMap<>(attributes == null ? Map.of() : attributes));
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("modelId", modelId);
        summary.put("sourcePath", sourcePath.toString());
        summary.put("format", format);
        summary.put("vocabSize", vocabSize);
        summary.put("mergeCount", mergeCount);
        summary.put("specialTokenCount", specialTokenCount);
        summary.put("maxTokenLength", maxTokenLength);
        summary.put("sampleTokens", sampleTokens);
        summary.put("attributes", attributes);
        return summary;
    }
}
