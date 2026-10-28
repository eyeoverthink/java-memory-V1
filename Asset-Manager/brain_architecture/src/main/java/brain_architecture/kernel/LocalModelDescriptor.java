package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Structured description of one local offline model asset or bundle.
 */
public record LocalModelDescriptor(
        String modelId,
        String family,
        Path sourcePath,
        long sizeBytes,
        String licenseId,
        String provenance,
        String sourceUri,
        String baseModel,
        String derivedFrom,
        List<String> allowedOperations,
        Map<String, String> attributes) {

    public LocalModelDescriptor {
        modelId = modelId == null || modelId.isBlank() ? "unknown-model" : modelId;
        family = family == null || family.isBlank() ? "unknown" : family;
        sourcePath = sourcePath == null ? Path.of(".").toAbsolutePath().normalize() : sourcePath.toAbsolutePath().normalize();
        licenseId = normalizeScalar(licenseId, "unknown");
        provenance = normalizeScalar(provenance, "unknown");
        sourceUri = normalizeScalar(sourceUri, "");
        baseModel = normalizeScalar(baseModel, "");
        derivedFrom = normalizeScalar(derivedFrom, "");
        allowedOperations = List.copyOf(new ArrayList<>(allowedOperations == null ? List.of() : allowedOperations));
        attributes = Map.copyOf(new LinkedHashMap<>(attributes == null ? Map.of() : attributes));
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("modelId", modelId);
        summary.put("family", family);
        summary.put("sourcePath", sourcePath.toString());
        summary.put("sizeBytes", sizeBytes);
        summary.put("licenseId", licenseId);
        summary.put("provenance", provenance);
        summary.put("sourceUri", sourceUri);
        summary.put("baseModel", baseModel);
        summary.put("derivedFrom", derivedFrom);
        summary.put("allowedOperations", allowedOperations);
        summary.put("attributes", attributes);
        return summary;
    }

    private static String normalizeScalar(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }
}
