package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of bootstrapping foundational symbolic and model substrates.
 */
public record FoundationalSubstrateReport(
        List<Path> artifactPaths,
        List<LocalModelDescriptor> modelDescriptors,
        List<TokenizerDigest> tokenizerDigests,
        List<LocalModelRuntimeDescriptor> runtimeDescriptors,
        Path languageArtifact,
        Path mathArtifact,
        Path modelInventoryArtifact,
        Path modelManifestArtifact,
        Path tokenizerInventoryArtifact,
        Path tokenizerManifestArtifact,
        Path runtimeInventoryArtifact) {

    public FoundationalSubstrateReport {
        artifactPaths = List.copyOf(new ArrayList<>(artifactPaths == null ? List.of() : artifactPaths));
        modelDescriptors = List.copyOf(new ArrayList<>(modelDescriptors == null ? List.of() : modelDescriptors));
        tokenizerDigests = List.copyOf(new ArrayList<>(tokenizerDigests == null ? List.of() : tokenizerDigests));
        runtimeDescriptors = List.copyOf(new ArrayList<>(runtimeDescriptors == null ? List.of() : runtimeDescriptors));
    }

    public Map<String, Long> familyCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : modelDescriptors) {
            counts.merge(descriptor.family(), 1L, Long::sum);
        }
        return counts;
    }

    public Map<String, Long> runtimeBackendCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelRuntimeDescriptor runtime : runtimeDescriptors) {
            counts.merge(runtime.backend(), 1L, Long::sum);
        }
        return counts;
    }

    public Map<String, Long> licenseCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : modelDescriptors) {
            counts.merge(descriptor.licenseId(), 1L, Long::sum);
        }
        return counts;
    }

    public Map<String, Long> provenanceCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : modelDescriptors) {
            counts.merge(descriptor.provenance(), 1L, Long::sum);
        }
        return counts;
    }

    public Map<String, Long> allowedOperationCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LocalModelDescriptor descriptor : modelDescriptors) {
            for (String operation : descriptor.allowedOperations()) {
                counts.merge(operation, 1L, Long::sum);
            }
        }
        return counts;
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("artifacts", artifactPaths);
        summary.put("models", modelDescriptors.size());
        summary.put("tokenizers", tokenizerDigests.size());
        summary.put("runtimes", runtimeDescriptors.size());
        summary.put("families", familyCounts());
        summary.put("licenses", licenseCounts());
        summary.put("provenance", provenanceCounts());
        summary.put("allowedOperations", allowedOperationCounts());
        summary.put("runtimeBackends", runtimeBackendCounts());
        summary.put("languageArtifact", languageArtifact == null ? null : languageArtifact.toString());
        summary.put("mathArtifact", mathArtifact == null ? null : mathArtifact.toString());
        summary.put("modelInventoryArtifact", modelInventoryArtifact == null ? null : modelInventoryArtifact.toString());
        summary.put("modelManifestArtifact", modelManifestArtifact == null ? null : modelManifestArtifact.toString());
        summary.put("tokenizerInventoryArtifact", tokenizerInventoryArtifact == null ? null : tokenizerInventoryArtifact.toString());
        summary.put("tokenizerManifestArtifact", tokenizerManifestArtifact == null ? null : tokenizerManifestArtifact.toString());
        summary.put("runtimeInventoryArtifact", runtimeInventoryArtifact == null ? null : runtimeInventoryArtifact.toString());
        summary.put("descriptors", modelDescriptors.stream().map(LocalModelDescriptor::toSummaryMap).toList());
        summary.put("tokenizerDigests", tokenizerDigests.stream().map(TokenizerDigest::toSummaryMap).toList());
        summary.put("runtimeDescriptors", runtimeDescriptors.stream().map(LocalModelRuntimeDescriptor::toSummaryMap).toList());
        return summary;
    }
}
