package fraymus.ultimate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lightweight index over repo text assets so the ultimate shell can reason over
 * docs, readmes, build files, and source files without depending on the full AI stack.
 */
public final class UltimateKnowledgeIndex {

    private static final int MAX_PREVIEW_BYTES = 16_384;
    private static final int MAX_PREVIEW_LINES = 12;
    private static final Set<String> INDEXED_EXTENSIONS = Set.of(
        "bat", "css", "gradle", "html", "java", "js", "json", "kts", "md",
        "properties", "py", "sh", "toml", "txt", "xml", "yaml", "yml"
    );
    private static final Set<String> SKIP_DIRECTORIES = Set.of(
        ".git", ".gradle", ".idea", ".vscode", "bin", "build", "data", "dist",
        "memory", "node_modules", "out", "target", "vault"
    );
    private static final List<String> SIGNALS = List.of(
        "phi", "memory", "quantum", "physics", "logic", "swarm",
        "dream", "agent", "ollama", "rag", "filesystem", "blockchain"
    );

    private final Path root;
    private final List<IndexedDocument> documents;
    private final List<IndexedDocument> readmes;
    private final Map<String, Integer> extensionCounts;
    private final Map<String, Integer> moduleCounts;
    private final Map<String, Integer> signalCounts;

    private UltimateKnowledgeIndex(
        Path root,
        List<IndexedDocument> documents,
        List<IndexedDocument> readmes,
        Map<String, Integer> extensionCounts,
        Map<String, Integer> moduleCounts,
        Map<String, Integer> signalCounts
    ) {
        this.root = root;
        this.documents = List.copyOf(documents);
        this.readmes = List.copyOf(readmes);
        this.extensionCounts = Map.copyOf(extensionCounts);
        this.moduleCounts = Map.copyOf(moduleCounts);
        this.signalCounts = Map.copyOf(signalCounts);
    }

    public static UltimateKnowledgeIndex build(Path root) {
        IndexBuilder builder = new IndexBuilder(root.toAbsolutePath().normalize());
        try {
            Files.walkFileTree(builder.root, builder);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to build knowledge index for " + root, e);
        }
        return builder.build();
    }

    public int documentCount() {
        return documents.size();
    }

    public int readmeCount() {
        return readmes.size();
    }

    public int moduleCount() {
        return moduleCounts.size();
    }

    public List<IndexedDocument> documents() {
        return documents;
    }

    public List<IndexedDocument> readmes() {
        return readmes;
    }

    public Map<String, Integer> extensionCounts() {
        return extensionCounts;
    }

    public Map<String, Integer> moduleCounts() {
        return moduleCounts;
    }

    public Map<String, Integer> signalCounts() {
        return signalCounts;
    }

    public String overview() {
        StringBuilder sb = new StringBuilder();
        sb.append("Knowledge: ").append(documentCount()).append(" indexed files, ")
            .append(readmeCount()).append(" readmes, ")
            .append(moduleCount()).append(" modules\n");
        sb.append("Top modules: ").append(formatCounts(moduleCounts, 5)).append("\n");
        sb.append("Top extensions: ").append(formatCounts(extensionCounts, 5)).append("\n");
        sb.append("Signals: ").append(formatCounts(signalCounts, 6));
        return sb.toString();
    }

    public String readmeReport(int limit) {
        if (readmes.isEmpty()) {
            return "No README or markdown anchors were indexed.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("README ANCHORS\n");
        for (IndexedDocument document : readmes.stream().limit(limit).toList()) {
            sb.append("  - ")
                .append(displayPath(document.path()))
                .append(" :: ")
                .append(document.title())
                .append("\n");
        }
        return sb.toString().trim();
    }

    public String searchReport(String query, int limit) {
        if (query == null || query.isBlank()) {
            return "Usage: search <query>";
        }

        List<SearchHit> hits = search(query, limit);
        if (hits.isEmpty()) {
            return "No indexed docs matched: " + query;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SEARCH: ").append(query.trim()).append("\n");
        for (SearchHit hit : hits) {
            sb.append("  [")
                .append(hit.score())
                .append("] ")
                .append(displayPath(hit.document().path()))
                .append(" :: ")
                .append(hit.document().title())
                .append("\n");
        }
        return sb.toString().trim();
    }

    public String fileReport(String pathFragment) {
        if (pathFragment == null || pathFragment.isBlank()) {
            return "Usage: show <path-fragment>";
        }

        Optional<IndexedDocument> match = findByPathFragment(pathFragment);

        if (match.isEmpty()) {
            return "No indexed file matched: " + pathFragment;
        }

        IndexedDocument document = match.get();
        StringBuilder sb = new StringBuilder();
        sb.append("FILE: ").append(displayPath(document.path())).append("\n");
        sb.append("Module: ").append(document.module()).append("\n");
        sb.append("Type: ").append(document.extension()).append("\n");
        sb.append("Title: ").append(document.title()).append("\n");
        sb.append("Preview:\n").append(document.preview());
        return sb.toString();
    }

    public Optional<IndexedDocument> findByPathFragment(String pathFragment) {
        if (pathFragment == null || pathFragment.isBlank()) {
            return Optional.empty();
        }
        String needle = pathFragment.trim().toLowerCase(Locale.ROOT);
        return documents.stream()
            .filter(document -> matchesPathFragment(document, needle))
            .sorted(Comparator
                .comparingInt((IndexedDocument document) -> pathPreferenceScore(displayPath(document.path()), needle))
                .thenComparingInt(document -> displayPath(document.path()).length())
                .thenComparing(document -> displayPath(document.path())))
            .findFirst();
    }

    public List<IndexedDocument> findTopByPathFragment(String pathFragment, int limit) {
        if (pathFragment == null || pathFragment.isBlank() || limit <= 0) {
            return List.of();
        }
        String needle = pathFragment.trim().toLowerCase(Locale.ROOT);
        return documents.stream()
            .filter(document -> matchesPathFragment(document, needle))
            .sorted(Comparator
                .comparingInt((IndexedDocument document) -> pathPreferenceScore(displayPath(document.path()), needle))
                .thenComparingInt(document -> displayPath(document.path()).length())
                .thenComparing(document -> displayPath(document.path())))
            .limit(limit)
            .toList();
    }

    private boolean matchesPathFragment(IndexedDocument document, String needle) {
        String normalized = displayPath(document.path()).toLowerCase(Locale.ROOT);
        if (!normalized.contains(needle)) {
            return false;
        }
        boolean asksForGenerated = needle.contains("generated") || needle.contains("ultimate-selfbuild");
        if (!asksForGenerated && normalized.contains("/generated/ultimate-selfbuild/")) {
            return false;
        }
        return true;
    }

    private int pathPreferenceScore(String displayPath, String needle) {
        String normalized = displayPath.toLowerCase(Locale.ROOT);
        int score = 0;
        if (normalized.endsWith(needle)) {
            score -= 20;
        }
        if (normalized.contains("/src/main/java/")) {
            score -= 10;
        }
        if (normalized.contains("/generated/ultimate-selfbuild/")) {
            score += 100;
        } else if (normalized.contains("/generated/")) {
            score += 20;
        }
        return score;
    }

    public String abstractionReport(UltimateSubsystemRegistry registry) {
        StringBuilder sb = new StringBuilder();
        sb.append("ULTIMATE ABSTRACTION\n");
        sb.append("One system = orchestration + cognition + memory + physics + operating substrate + interfaces.\n");
        sb.append("Repo root: ").append(root.toString().replace('\\', '/')).append("\n");
        sb.append("Subsystem families: ").append(formatFamilyCounts(registry.familyCounts())).append("\n");
        sb.append("Knowledge footprint: ").append(documentCount()).append(" files across ")
            .append(moduleCount()).append(" modules\n");
        sb.append("Dominant signals: ").append(formatCounts(signalCounts, 6)).append("\n");
        sb.append("Primary modules: ").append(formatCounts(moduleCounts, 6)).append("\n");
        sb.append("Backbone:\n");
        sb.append("  - Route through Ultimate System for abstraction, search, and launch.\n");
        sb.append("  - Use Fraymus Convergence for the main current command surface.\n");
        sb.append("  - Use Fraynix Boot for maximum historical breadth.\n");
        sb.append("  - Use Fraynix OS for the cleanest platform architecture.\n");
        sb.append("  - Use Fraymus AI Core when the problem is reusable local AI, RAG, or tools.\n");
        if (!readmes.isEmpty()) {
            sb.append("Anchor docs:\n");
            for (IndexedDocument document : readmes.stream().limit(4).toList()) {
                sb.append("  - ")
                    .append(displayPath(document.path()))
                    .append(" :: ")
                    .append(document.title())
                    .append("\n");
            }
        }
        return sb.toString().trim();
    }

    public List<SearchHit> search(String query, int limit) {
        String needle = query.trim().toLowerCase(Locale.ROOT);
        return documents.stream()
            .map(document -> new SearchHit(document, score(document, needle)))
            .filter(hit -> hit.score() > 0)
            .sorted(Comparator.comparingInt(SearchHit::score).reversed()
                .thenComparing(hit -> displayPath(hit.document().path())))
            .limit(limit)
            .toList();
    }

    private int score(IndexedDocument document, String needle) {
        String path = displayPath(document.path()).toLowerCase(Locale.ROOT);
        String title = document.title().toLowerCase(Locale.ROOT);
        String preview = document.preview().toLowerCase(Locale.ROOT);
        String module = document.module().toLowerCase(Locale.ROOT);

        int score = 0;
        if (path.contains(needle)) {
            score += 8;
        }
        if (title.contains(needle)) {
            score += 6;
        }
        if (module.contains(needle)) {
            score += 3;
        }
        if (preview.contains(needle)) {
            score += 4;
        }
        return score;
    }

    private String displayPath(Path path) {
        try {
            if (path.startsWith(root)) {
                return root.relativize(path).toString().replace('\\', '/');
            }
        } catch (IllegalArgumentException ignored) {
            // Fall through to absolute path rendering.
        }
        return path.toString().replace('\\', '/');
    }

    public String relativePath(Path path) {
        return displayPath(path);
    }

    private static String formatCounts(Map<String, Integer> counts, int limit) {
        if (counts.isEmpty()) {
            return "(none)";
        }
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
            .limit(limit)
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", "));
    }

    private static String formatFamilyCounts(Map<String, Long> counts) {
        if (counts.isEmpty()) {
            return "(none)";
        }
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", "));
    }

    public record IndexedDocument(Path path, String module, String extension, String title, String preview) {
    }

    public record SearchHit(IndexedDocument document, int score) {
    }

    private static final class IndexBuilder extends SimpleFileVisitor<Path> {
        private final Path root;
        private final List<IndexedDocument> documents = new ArrayList<>();
        private final List<IndexedDocument> readmes = new ArrayList<>();
        private final Map<String, Integer> extensionCounts = new LinkedHashMap<>();
        private final Map<String, Integer> moduleCounts = new LinkedHashMap<>();
        private final Map<String, Integer> signalCounts = new LinkedHashMap<>();

        private IndexBuilder(Path root) {
            this.root = root;
        }

        private UltimateKnowledgeIndex build() {
            return new UltimateKnowledgeIndex(root, documents, readmes, extensionCounts, moduleCounts, signalCounts);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            Path name = dir.getFileName();
            if (name != null && SKIP_DIRECTORIES.contains(name.toString())) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            String extension = extensionOf(file);
            if (!INDEXED_EXTENSIONS.contains(extension)) {
                return FileVisitResult.CONTINUE;
            }

            String preview = readPreview(file);
            if (preview.isBlank()) {
                return FileVisitResult.CONTINUE;
            }

            String title = titleOf(file, preview);
            String module = moduleOf(file);
            IndexedDocument document = new IndexedDocument(file, module, extension, title, preview);
            documents.add(document);

            extensionCounts.merge(extension, 1, Integer::sum);
            moduleCounts.merge(module, 1, Integer::sum);
            detectSignals(file, preview).forEach(signal -> signalCounts.merge(signal, 1, Integer::sum));

            if (isAnchorDocument(file, extension)) {
                readmes.add(document);
            }
            return FileVisitResult.CONTINUE;
        }

        private String readPreview(Path file) {
            try (InputStream inputStream = Files.newInputStream(file)) {
                byte[] buffer = inputStream.readNBytes(MAX_PREVIEW_BYTES);
                return normalizePreview(decode(buffer));
            } catch (IOException e) {
                return "";
            }
        }

        private String decode(byte[] bytes) throws CharacterCodingException {
            return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .decode(ByteBuffer.wrap(bytes))
                .toString();
        }

        private String normalizePreview(String raw) {
            List<String> lines = raw.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .limit(MAX_PREVIEW_LINES)
                .toList();
            return String.join("\n", lines);
        }

        private String titleOf(Path file, String preview) {
            for (String line : preview.split("\n")) {
                if (line.startsWith("#")) {
                    return line.replaceFirst("^#+\\s*", "").trim();
                }
                if (!line.isBlank()) {
                    return truncate(line, 90);
                }
            }
            return file.getFileName().toString();
        }

        private String moduleOf(Path file) {
            Path relative = root.relativize(file);
            return relative.getNameCount() > 0 ? relative.getName(0).toString() : "(root)";
        }

        private Set<String> detectSignals(Path file, String preview) {
            String combined = (root.relativize(file).toString() + "\n" + preview).toLowerCase(Locale.ROOT);
            Set<String> matches = new LinkedHashSet<>();
            for (String signal : SIGNALS) {
                if (combined.contains(signal)) {
                    matches.add(signal);
                }
            }
            return matches;
        }

        private String extensionOf(Path file) {
            String name = file.getFileName().toString();
            int index = name.lastIndexOf('.');
            if (index < 0 || index == name.length() - 1) {
                return "";
            }
            return name.substring(index + 1).toLowerCase(Locale.ROOT);
        }

        private boolean isAnchorDocument(Path file, String extension) {
            String filename = file.getFileName().toString().toLowerCase(Locale.ROOT);
            if (filename.startsWith("readme")) {
                return true;
            }
            if (!extension.equals("md")) {
                return false;
            }

            Path relative = root.relativize(file);
            if (relative.getNameCount() <= 2) {
                return true;
            }

            if (relative.getNameCount() <= 4) {
                String parent = relative.getName(relative.getNameCount() - 2).toString().toLowerCase(Locale.ROOT);
                return parent.equals("docs");
            }
            return false;
        }

        private String truncate(String value, int max) {
            return value.length() <= max ? value : value.substring(0, max - 3) + "...";
        }
    }
}
