package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified view of discovered local model assets, tokenizers, and runtime bindings.
 */
public record LocalModelSubstrateCatalog(
        List<Path> roots,
        List<LocalModelDescriptor> modelDescriptors,
        List<TokenizerDigest> tokenizerDigests,
        List<LocalModelRuntimeDescriptor> runtimeDescriptors) {

    public LocalModelSubstrateCatalog {
        roots = List.copyOf(new ArrayList<>(roots == null ? List.of() : roots));
        modelDescriptors = List.copyOf(new ArrayList<>(modelDescriptors == null ? List.of() : modelDescriptors));
        tokenizerDigests = List.copyOf(new ArrayList<>(tokenizerDigests == null ? List.of() : tokenizerDigests));
        runtimeDescriptors = List.copyOf(new ArrayList<>(runtimeDescriptors == null ? List.of() : runtimeDescriptors));
    }

    public LocalModelDescriptor descriptorFor(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return null;
        }
        return modelDescriptors.stream()
            .filter(descriptor -> descriptor.modelId().equalsIgnoreCase(modelId))
            .findFirst()
            .orElse(null);
    }

    public TokenizerDigest tokenizerFor(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return null;
        }
        return tokenizerDigests.stream()
            .filter(digest -> digest.modelId().equalsIgnoreCase(modelId))
            .findFirst()
            .orElse(null);
    }

    public LocalModelRuntimeDescriptor runtimeFor(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return null;
        }
        return runtimeDescriptors.stream()
            .filter(runtime -> runtime.modelId().equalsIgnoreCase(modelId))
            .findFirst()
            .orElse(null);
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("roots", roots.stream().map(Path::toString).toList());
        summary.put("models", modelDescriptors.stream().map(LocalModelDescriptor::toSummaryMap).toList());
        summary.put("tokenizers", tokenizerDigests.stream().map(TokenizerDigest::toSummaryMap).toList());
        summary.put("runtimes", runtimeDescriptors.stream().map(LocalModelRuntimeDescriptor::toSummaryMap).toList());
        return summary;
    }
}
