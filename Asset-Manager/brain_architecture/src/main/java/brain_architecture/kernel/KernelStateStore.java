package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Persists kernel memory, tracked artifacts, and bandit state without external dependencies.
 */
public final class KernelStateStore {
    private final Path stateRoot;
    private final Path memoryFile;
    private final Path artifactFile;
    private final Path routerFile;
    private final Path causalFile;

    public KernelStateStore(Path stateRoot) throws IOException {
        this.stateRoot = stateRoot;
        Files.createDirectories(stateRoot);
        this.memoryFile = stateRoot.resolve("memory-index.tsv");
        this.artifactFile = stateRoot.resolve("tracked-artifacts.txt");
        this.routerFile = stateRoot.resolve("router-state.tsv");
        this.causalFile = stateRoot.resolve("causal-graph.tsv");
    }

    public void persist(HybridMemoryStore memoryStore, StrategyBanditRouter router) throws IOException {
        writeAtomically(memoryFile, encodeDocuments(memoryStore.snapshotDocuments().values()));
        writeAtomically(artifactFile, encodeArtifacts(memoryStore.snapshotTrackedArtifacts()));
        writeAtomically(routerFile, encodeRouter(router.snapshotState()));
        writeAtomically(causalFile, encodeCausal(
            memoryStore.snapshotCausalNodes(),
            memoryStore.snapshotCausalEdges(),
            memoryStore.snapshotCausalAnchors()
        ));
    }

    public KernelStateSnapshot load() throws IOException {
        List<MemoryDocument> documents = Files.exists(memoryFile)
            ? decodeDocuments(Files.readAllLines(memoryFile, StandardCharsets.UTF_8))
            : List.of();
        List<Path> artifacts = Files.exists(artifactFile)
            ? decodeArtifacts(Files.readAllLines(artifactFile, StandardCharsets.UTF_8))
            : List.of();
        Map<String, StrategyBanditRouter.ArmSnapshot> routerState = Files.exists(routerFile)
            ? decodeRouter(Files.readAllLines(routerFile, StandardCharsets.UTF_8))
            : Map.of();
        CausalStateSnapshot causalState = Files.exists(causalFile)
            ? decodeCausal(Files.readAllLines(causalFile, StandardCharsets.UTF_8))
            : CausalStateSnapshot.empty();
        return new KernelStateSnapshot(documents, artifacts, routerState, causalState.nodes(), causalState.edges(), causalState.anchors());
    }

    private void writeAtomically(Path target, List<String> lines) throws IOException {
        Files.createDirectories(stateRoot);
        Path temp = Files.createTempFile(stateRoot, target.getFileName().toString() + ".", ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temp);
            }
        }
    }

    private List<String> encodeDocuments(Iterable<MemoryDocument> documents) {
        List<String> lines = new ArrayList<>();
        for (MemoryDocument document : documents) {
            lines.add(String.join("\t",
                encode(document.getSourceId()),
                encode(document.getPath() == null ? "" : document.getPath().toString()),
                encode(document.getPreview()),
                encode(serializeTermFrequency(document.getTermFrequency())),
                encode(Base64.getEncoder().encodeToString(document.getVector().toByteArray())),
                encode(serializeSet(document.getGraphNeighbors())),
                Long.toString(document.getIndexedAt()),
                Boolean.toString(document.isCodeLike()),
                Boolean.toString(document.isAuthoritative())
            ));
        }
        return lines;
    }

    private List<MemoryDocument> decodeDocuments(List<String> lines) {
        List<MemoryDocument> documents = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t", -1);
            String sourceId = decode(parts[0]);
            Path path = decode(parts[1]).isBlank() ? null : Path.of(decode(parts[1]));
            String preview = decode(parts[2]);
            Map<String, Integer> termFrequency = deserializeTermFrequency(decode(parts[3]));
            byte[] bytes = Base64.getDecoder().decode(decode(parts[4]));
            BitSet vector = BitSet.valueOf(bytes);
            Set<String> neighbors = deserializeSet(decode(parts[5]));
            long indexedAt = Long.parseLong(parts[6]);
            boolean codeLike = Boolean.parseBoolean(parts[7]);
            boolean authoritative = parts.length >= 9 ? Boolean.parseBoolean(parts[8]) : true;
            documents.add(new MemoryDocument(
                sourceId,
                path,
                preview,
                termFrequency,
                vector,
                neighbors,
                indexedAt,
                codeLike,
                authoritative
            ));
        }
        return documents;
    }

    private List<String> encodeArtifacts(List<Path> artifacts) {
        List<String> lines = new ArrayList<>();
        for (Path artifact : artifacts) {
            lines.add(artifact.toString().replace('\\', '/'));
        }
        return lines;
    }

    private List<Path> decodeArtifacts(List<String> lines) {
        List<Path> artifacts = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                artifacts.add(Path.of(line));
            }
        }
        return artifacts;
    }

    private List<String> encodeRouter(Map<String, StrategyBanditRouter.ArmSnapshot> snapshot) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, StrategyBanditRouter.ArmSnapshot> entry : snapshot.entrySet()) {
            lines.add(entry.getKey() + "\t" + entry.getValue().alpha() + "\t" + entry.getValue().beta());
        }
        return lines;
    }

    private Map<String, StrategyBanditRouter.ArmSnapshot> decodeRouter(List<String> lines) {
        Map<String, StrategyBanditRouter.ArmSnapshot> snapshot = new LinkedHashMap<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t");
            snapshot.put(parts[0], new StrategyBanditRouter.ArmSnapshot(
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
            ));
        }
        return snapshot;
    }

    private List<String> encodeCausal(
            Map<String, String> nodes,
            Map<String, Set<String>> edges,
            Map<String, Set<String>> anchors) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : new TreeMap<>(nodes).entrySet()) {
            lines.add(String.join("\t", "N", encode(entry.getKey()), encode(entry.getValue())));
        }
        for (Map.Entry<String, Set<String>> entry : new TreeMap<>(edges).entrySet()) {
            Set<String> orderedChildren = new TreeSet<>(entry.getValue() == null ? Set.of() : entry.getValue());
            for (String child : orderedChildren) {
                lines.add(String.join("\t", "E", encode(entry.getKey()), encode(child)));
            }
        }
        for (Map.Entry<String, Set<String>> entry : new TreeMap<>(anchors).entrySet()) {
            lines.add(String.join("\t", "A", encode(entry.getKey()), encode(serializeSet(entry.getValue()))));
        }
        return lines;
    }

    private CausalStateSnapshot decodeCausal(List<String> lines) {
        Map<String, String> nodes = new LinkedHashMap<>();
        Map<String, Set<String>> edges = new LinkedHashMap<>();
        Map<String, Set<String>> anchors = new LinkedHashMap<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t", -1);
            if (parts.length < 3) {
                continue;
            }
            String kind = parts[0];
            if ("N".equals(kind)) {
                nodes.put(decode(parts[1]), decode(parts[2]));
            } else if ("E".equals(kind)) {
                edges.computeIfAbsent(decode(parts[1]), ignored -> new LinkedHashSet<>()).add(decode(parts[2]));
            } else if ("A".equals(kind)) {
                anchors.put(decode(parts[1]), deserializeSet(decode(parts[2])));
            }
        }
        return new CausalStateSnapshot(nodes, edges, anchors);
    }

    private String serializeTermFrequency(Map<String, Integer> termFrequency) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : new TreeMap<>(termFrequency).entrySet()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(encode(entry.getKey())).append(':').append(entry.getValue());
        }
        return builder.toString();
    }

    private Map<String, Integer> deserializeTermFrequency(String serialized) {
        Map<String, Integer> frequencies = new LinkedHashMap<>();
        if (serialized == null || serialized.isBlank()) {
            return frequencies;
        }
        String[] entries = serialized.split(";");
        for (String entry : entries) {
            int separator = entry.lastIndexOf(':');
            if (separator < 0) {
                separator = entry.lastIndexOf('=');
            }
            if (separator > 0) {
                frequencies.put(decode(entry.substring(0, separator)), Integer.parseInt(entry.substring(separator + 1)));
            }
        }
        return frequencies;
    }

    private String serializeSet(Set<String> values) {
        Set<String> ordered = new LinkedHashSet<>(values);
        StringBuilder builder = new StringBuilder();
        for (String value : ordered) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(encode(value));
        }
        return builder.toString();
    }

    private Set<String> deserializeSet(String serialized) {
        Set<String> values = new LinkedHashSet<>();
        if (serialized == null || serialized.isBlank()) {
            return values;
        }
        String[] entries = serialized.split(";");
        for (String entry : entries) {
            if (!entry.isBlank()) {
                values.add(decode(entry));
            }
        }
        return values;
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    public record KernelStateSnapshot(
            List<MemoryDocument> documents,
            List<Path> trackedArtifacts,
            Map<String, StrategyBanditRouter.ArmSnapshot> routerState,
            Map<String, String> causalNodes,
            Map<String, Set<String>> causalEdges,
            Map<String, Set<String>> causalAnchors) {
    }

    private record CausalStateSnapshot(
            Map<String, String> nodes,
            Map<String, Set<String>> edges,
            Map<String, Set<String>> anchors) {
        private static CausalStateSnapshot empty() {
            return new CausalStateSnapshot(Map.of(), Map.of(), Map.of());
        }
    }
}
