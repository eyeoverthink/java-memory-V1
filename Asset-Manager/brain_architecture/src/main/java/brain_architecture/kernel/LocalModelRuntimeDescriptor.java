package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Planned local runtime binding for one model descriptor.
 */
public record LocalModelRuntimeDescriptor(
        String modelId,
        String backend,
        boolean available,
        Path executablePath,
        String targetReference,
        String selectionReason) {

    public LocalModelRuntimeDescriptor {
        modelId = modelId == null || modelId.isBlank() ? "unknown-model" : modelId;
        backend = backend == null || backend.isBlank() ? "none" : backend;
        executablePath = executablePath == null ? null : executablePath.toAbsolutePath().normalize();
        targetReference = targetReference == null ? "" : targetReference;
        selectionReason = selectionReason == null ? "" : selectionReason;
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("modelId", modelId);
        summary.put("backend", backend);
        summary.put("available", available);
        summary.put("executablePath", executablePath == null ? null : executablePath.toString());
        summary.put("targetReference", targetReference);
        summary.put("selectionReason", selectionReason);
        return summary;
    }
}
