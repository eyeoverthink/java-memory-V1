package brain_architecture.kernel;

import fraymus.causal.CausalModel;
import fraymus.causal.DAGVerifier;
import fraymus.causal.DoCalculusEngine;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Local hybrid retrieval that blends lexical, graph, HDC, recency, and causal scoring.
 */
public final class HybridMemoryStore {
    private static final Pattern IMPORT_PATTERN =
        Pattern.compile("(?m)^\\s*import\\s+([A-Za-z_$][\\w$.]*)\\s*;");
    private static final Pattern TYPE_DECLARATION_PATTERN =
        Pattern.compile("\\b(?:class|interface|enum|record)\\s+([A-Za-z_$][\\w$]*)");
    private static final Pattern TYPE_TOKEN_PATTERN =
        Pattern.compile("\\b[A-Z][A-Za-z0-9_$]{2,}\\b");
    private static final Pattern CAUSAL_PHRASE_PATTERN =
        Pattern.compile(
            "(?i)\\b([A-Za-z_$][\\w$./-]*)\\b\\s+"
                + "(causes|leads to|requires|depends on|enables|prevents|blocks|fixes|repairs|drives)\\s+"
                + "\\b([A-Za-z_$][\\w$./-]*)\\b"
        );
    private static final Set<String> CAUSAL_STOPWORDS = Set.of(
        "the", "and", "for", "that", "this", "with", "from", "into", "while", "through",
        "using", "used", "local", "offline", "kernel", "brain", "fraynix", "document",
        "documents", "generated", "system", "value", "class", "public", "private", "final",
        "void", "static", "return", "string", "object", "localdocument", "workspace"
    );
    private static final String ROOT_CORPUS_PREFIX = "docs/root-md-corpus/";
    private static final String GENERATED_ROOT_SESSION_PREFIX =
        "java-memory/asset-manager/generated/ultimate-system/_session/f_/full-ai-system/";
    private static final List<String> INACTIVE_TREE_MARKERS = List.of(
        ".northstar-kernel/shadow-",
        "release-capsules/offline-production/dist/",
        "/code_generator_ui_backup/",
        "/fraymus-full-quantum-system/",
        "/fraymus_nexus_edition/",
        "/asset-manager-llm-dev/"
    );
    private static final List<String> CLAIM_KEYWORDS = List.of("proof", "live", "complete");
    private static final List<String> VERIFIER_EVIDENCE_MARKERS = List.of(
        "verify-offline-kernel.ps1",
        "release-gate-latest.txt",
        "offline-kernel-fullsuite-latest.txt",
        "verify-root-workspace.ps1",
        "release-capsules/offline-production/reports/latest.json",
        "offline kernel verification passed"
    );

    private final Path workspaceRoot;
    private final KernelHypervectorSpace hypervectorSpace;
    private final OptionalPdfTextExtractor pdfTextExtractor;
    private final Map<String, MemoryDocument> documentsBySource;
    private final Map<String, Integer> documentFrequency;
    private final Map<String, Set<String>> graph;
    private final Set<String> trackedArtifacts;
    private final boolean causalPolicyEnabled;
    private final CausalModel causalModel;
    private final DAGVerifier causalVerifier;
    private final DoCalculusEngine doCalculusEngine;
    private final Map<String, Set<String>> causalAnchorsByDocument;
    private int remappedCanonicalMirrors;
    private int skippedPolicyPaths;
    private int nonAuthoritativeDocuments;

    public HybridMemoryStore(Path workspaceRoot, KernelHypervectorSpace hypervectorSpace) {
        this(workspaceRoot, hypervectorSpace, true);
    }

    public HybridMemoryStore(Path workspaceRoot, KernelHypervectorSpace hypervectorSpace, boolean causalPolicyEnabled) {
        this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
        this.hypervectorSpace = hypervectorSpace;
        this.pdfTextExtractor = new OptionalPdfTextExtractor();
        this.documentsBySource = new LinkedHashMap<>();
        this.documentFrequency = new HashMap<>();
        this.graph = new HashMap<>();
        this.trackedArtifacts = new HashSet<>();
        this.causalPolicyEnabled = causalPolicyEnabled;
        this.causalModel = new CausalModel();
        this.causalVerifier = new DAGVerifier(causalModel);
        this.doCalculusEngine = new DoCalculusEngine(causalModel);
        this.causalAnchorsByDocument = new LinkedHashMap<>();
    }

    public int ingestPath(Path target) throws IOException {
        Path resolved = resolveToWorkspace(target);
        if (!Files.exists(resolved)) {
            return 0;
        }
        trackedArtifacts.add(relativize(resolved));
        if (Files.isDirectory(resolved)) {
            int count = 0;
            try (var walk = Files.walk(resolved)) {
                for (Path file : (Iterable<Path>) walk.filter(Files::isRegularFile)::iterator) {
                    if (isLikelyText(file)) {
                        try {
                            if (ingestFile(file)) {
                                count++;
                            }
                        } catch (IOException ignored) {
                            // Skip files that cannot be decoded as local text.
                        }
                    }
                }
            }
            return count;
        }
        return ingestFile(resolved) ? 1 : 0;
    }

    public void ingestText(String sourceId, String content) {
        Path synthetic = workspaceRoot.resolve(sourceId).normalize();
        MemoryDocument document = buildDocument(sourceId, synthetic, content);
        upsertDocument(document);
    }

    public List<MemoryHit> search(String query, List<Path> artifactHints, int topK) {
        return search(query, artifactHints, topK, RetrievalProfile.HYBRID);
    }

    public List<MemoryHit> searchLexicalOnly(String query, List<Path> artifactHints, int topK) {
        return search(query, artifactHints, topK, RetrievalProfile.LEXICAL_ONLY);
    }

    public List<MemoryHit> searchHdcOnly(String query, List<Path> artifactHints, int topK) {
        return search(query, artifactHints, topK, RetrievalProfile.HDC_ONLY);
    }

    public List<MemoryHit> search(String query, List<Path> artifactHints, int topK, RetrievalProfile profile) {
        if (documentsBySource.isEmpty()) {
            return List.of();
        }

        List<String> queryTerms = hypervectorSpace.tokenize(query);
        BitSet queryVector = hypervectorSpace.encodeText(query);
        List<String> artifactSources = normalizeArtifactSources(artifactHints);
        List<String> queryConcepts = extractQueryConcepts(queryTerms, artifactSources);
        long now = System.currentTimeMillis();
        int documentCount = Math.max(1, documentsBySource.size());

        List<MemoryHit> hits = new ArrayList<>();
        for (MemoryDocument document : documentsBySource.values()) {
            double lexical = lexicalScore(queryTerms, document, documentCount);
            double graphScore = graphScore(document, artifactSources);
            double hdcScore = hypervectorSpace.cosineSimilarity(queryVector, document.getVector());
            double recencyScore = Math.exp(-Math.max(0, now - document.getIndexedAt()) / 86_400_000.0);
            CausalAssessment causalAssessment = causalAssessment(document, queryConcepts, artifactSources, lexical, graphScore, hdcScore);
            double total = switch (profile) {
                case LEXICAL_ONLY -> lexical;
                case HDC_ONLY -> hdcScore;
                case HYBRID -> Math.max(
                    0.0,
                    (0.31 * lexical)
                        + (0.18 * graphScore)
                        + (0.25 * hdcScore)
                        + (0.11 * recencyScore)
                        + (0.15 * causalAssessment.causalScore())
                        - causalAssessment.spuriousPenalty()
                );
            };
            if (!document.isAuthoritative()) {
                total *= 0.70;
            }
            hits.add(new MemoryHit(
                document,
                lexical,
                graphScore,
                hdcScore,
                recencyScore,
                causalAssessment.causalScore(),
                causalAssessment.spuriousPenalty(),
                causalAssessment.grounded(),
                causalAssessment.notes(),
                total
            ));
        }

        hits.sort(Comparator.comparingDouble(MemoryHit::getTotalScore).reversed());
        return hits.subList(0, Math.min(topK, hits.size()));
    }

    public int getDocumentCount() {
        return documentsBySource.size();
    }

    public MemoryBenchmarkReport benchmark(String query, List<Path> artifactHints, int topK) {
        Map<RetrievalProfile, List<MemoryHit>> hitsByProfile = new LinkedHashMap<>();
        Map<RetrievalProfile, Double> scores = new LinkedHashMap<>();

        for (RetrievalProfile profile : RetrievalProfile.values()) {
            List<MemoryHit> hits = search(query, artifactHints, topK, profile);
            hitsByProfile.put(profile, hits);
            double aggregate = hits.stream()
                .mapToDouble(MemoryHit::getTotalScore)
                .sum();
            scores.put(profile, aggregate);
        }

        RetrievalProfile winner = scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(RetrievalProfile.HYBRID);

        return new MemoryBenchmarkReport(query, hitsByProfile, scores, winner);
    }

    public MemoryDocument findBySource(String sourceId) {
        return documentsBySource.get(sourceId);
    }

    public Map<String, MemoryDocument> snapshotDocuments() {
        return Collections.unmodifiableMap(documentsBySource);
    }

    public List<Path> snapshotTrackedArtifacts() {
        return trackedArtifacts.stream()
            .sorted()
            .map(Path::of)
            .toList();
    }

    public void restoreState(List<MemoryDocument> documents, List<Path> artifactPaths) {
        restoreState(documents, artifactPaths, Map.of(), Map.of(), Map.of());
    }

    public void restoreState(
            List<MemoryDocument> documents,
            List<Path> artifactPaths,
            Map<String, String> causalNodes,
            Map<String, Set<String>> causalEdges,
            Map<String, Set<String>> causalAnchors) {
        documentsBySource.clear();
        documentFrequency.clear();
        graph.clear();
        trackedArtifacts.clear();
        causalAnchorsByDocument.clear();
        causalModel.clear();
        nonAuthoritativeDocuments = 0;

        for (MemoryDocument document : documents == null ? List.<MemoryDocument>of() : documents) {
            documentsBySource.put(document.getSourceId(), document);
            updateDocumentFrequency(document);
            graph.put(document.getSourceId(), new HashSet<>(document.getGraphNeighbors()));
            if (!document.isAuthoritative()) {
                nonAuthoritativeDocuments++;
            }
        }

        for (Path artifactPath : artifactPaths == null ? List.<Path>of() : artifactPaths) {
            trackedArtifacts.add(artifactPath.toString().replace('\\', '/'));
        }

        if (causalNodes == null || causalNodes.isEmpty()) {
            rebuildCausalGraph();
        } else {
            restoreCausalGraph(causalNodes, causalEdges, causalAnchors);
        }
    }

    public Map<String, String> snapshotCausalNodes() {
        Map<String, String> snapshot = new LinkedHashMap<>();
        causalModel.getNodes().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> snapshot.put(entry.getKey(), entry.getValue().getVariable()));
        return snapshot;
    }

    public Map<String, Set<String>> snapshotCausalEdges() {
        Map<String, Set<String>> snapshot = new LinkedHashMap<>();
        causalModel.getNodes().keySet().stream()
            .sorted()
            .forEach(nodeId -> snapshot.put(nodeId, new LinkedHashSet<>(causalModel.getChildren(nodeId))));
        return snapshot;
    }

    public Map<String, Set<String>> snapshotCausalAnchors() {
        Map<String, Set<String>> snapshot = new LinkedHashMap<>();
        causalAnchorsByDocument.forEach((sourceId, anchors) -> snapshot.put(sourceId, new LinkedHashSet<>(anchors)));
        return snapshot;
    }

    public Map<String, Object> causalStatistics() {
        if (!causalPolicyEnabled) {
            return Map.of(
                "enabled", false,
                "nodes", 0,
                "edges", 0,
                "groundedDocuments", 0,
                "violations", 0
            );
        }
        int edgeCount = causalModel.getNodes().keySet().stream()
            .mapToInt(nodeId -> causalModel.getChildren(nodeId).size())
            .sum();
        long groundedDocuments = causalAnchorsByDocument.values().stream()
            .filter(anchors -> anchors != null && !anchors.isEmpty())
            .count();
        return Map.of(
            "enabled", true,
            "nodes", causalModel.getNodes().size(),
            "edges", edgeCount,
            "groundedDocuments", groundedDocuments,
            "violations", causalVerifier.getAllViolations().size()
        );
    }

    public Map<String, Object> ingestionPolicyStatistics() {
        return Map.of(
            "canonicalMirrorRemaps", remappedCanonicalMirrors,
            "skippedPolicyPaths", skippedPolicyPaths,
            "nonAuthoritativeDocuments", nonAuthoritativeDocuments
        );
    }

    private boolean ingestFile(Path file) throws IOException {
        Path canonicalFile = canonicalizeMirrorTarget(file);
        if (canonicalFile == null) {
            skippedPolicyPaths++;
            return false;
        }
        if (!canonicalFile.equals(file)) {
            remappedCanonicalMirrors++;
        }
        String content;
        if (isPdf(canonicalFile)) {
            String extracted = pdfTextExtractor.extract(canonicalFile);
            content = extracted.isBlank()
                ? "pdf " + canonicalFile.getFileName() + " local document"
                : extracted;
        } else {
            content = Files.readString(canonicalFile, StandardCharsets.UTF_8);
        }
        String sourceId = relativize(canonicalFile);
        MemoryDocument document = buildDocument(sourceId, canonicalFile, content);
        upsertDocument(document);
        return true;
    }

    private MemoryDocument buildDocument(String sourceId, Path path, String content) {
        List<String> tokens = hypervectorSpace.tokenize(content);
        Map<String, Integer> termFrequency = new HashMap<>();
        for (String token : tokens) {
            termFrequency.merge(token, 1, Integer::sum);
        }
        BitSet vector = hypervectorSpace.encodeText(content);
        Set<String> neighbors = new HashSet<>();
        boolean codeLike = looksLikeCode(path, content);
        boolean authoritative = isAuthoritativeDocument(sourceId, path, content);
        String preview = content.length() > 240 ? content.substring(0, 240) : content;
        MemoryDocument document = new MemoryDocument(
            sourceId,
            path,
            preview,
            termFrequency,
            vector,
            neighbors,
            System.currentTimeMillis(),
            codeLike,
            authoritative
        );
        return document;
    }

    private void updateDocumentFrequency(MemoryDocument document) {
        for (String term : document.getTermFrequency().keySet()) {
            documentFrequency.merge(term, 1, Integer::sum);
        }
    }

    private void removeDocumentFrequency(MemoryDocument document) {
        for (String term : document.getTermFrequency().keySet()) {
            Integer current = documentFrequency.get(term);
            if (current == null) {
                continue;
            }
            if (current <= 1) {
                documentFrequency.remove(term);
            } else {
                documentFrequency.put(term, current - 1);
            }
        }
    }

    private void upsertDocument(MemoryDocument document) {
        MemoryDocument existing = documentsBySource.get(document.getSourceId());
        if (existing != null) {
            removeDocumentFrequency(existing);
            graph.remove(existing.getSourceId());
            for (Set<String> neighbors : graph.values()) {
                neighbors.remove(existing.getSourceId());
            }
            if (!existing.isAuthoritative()) {
                nonAuthoritativeDocuments = Math.max(0, nonAuthoritativeDocuments - 1);
            }
        }
        documentsBySource.put(document.getSourceId(), document);
        updateDocumentFrequency(document);
        if (!document.isAuthoritative()) {
            nonAuthoritativeDocuments++;
        }
        rebuildGraphLinks(document);
        rebuildCausalGraph();
    }

    private void rebuildGraphLinks(MemoryDocument document) {
        graph.computeIfAbsent(document.getSourceId(), ignored -> new HashSet<>());
        for (MemoryDocument other : documentsBySource.values()) {
            if (document == other) {
                continue;
            }
            if (sharesDirectory(document.getPath(), other.getPath())
                    || mentionsFileName(document, other)
                    || mentionsFileName(other, document)) {
                graph.get(document.getSourceId()).add(other.getSourceId());
                graph.computeIfAbsent(other.getSourceId(), ignored -> new HashSet<>()).add(document.getSourceId());
            }
        }
    }

    private boolean mentionsFileName(MemoryDocument source, MemoryDocument target) {
        if (target.getPath() == null || target.getPath().getFileName() == null) {
            return false;
        }
        String fileName = target.getPath().getFileName().toString().toLowerCase(Locale.ROOT);
        return source.getPreview().toLowerCase(Locale.ROOT).contains(fileName);
    }

    private boolean sharesDirectory(Path left, Path right) {
        if (left == null || right == null || left.getParent() == null || right.getParent() == null) {
            return false;
        }
        return left.getParent().equals(right.getParent());
    }

    private double lexicalScore(List<String> queryTerms, MemoryDocument document, int documentCount) {
        if (queryTerms.isEmpty()) {
            return 0.0;
        }
        double score = 0.0;
        for (String term : queryTerms) {
            int tf = document.getTermFrequency().getOrDefault(term, 0);
            int df = documentFrequency.getOrDefault(term, 0);
            double idf = Math.log((documentCount + 1.0) / (df + 1.0)) + 1.0;
            score += Math.min(tf, 1) * idf;
        }
        return score / queryTerms.size();
    }

    private double graphScore(MemoryDocument document, List<String> artifactSources) {
        if (artifactSources.isEmpty()) {
            return 0.0;
        }
        double score = 0.0;
        if (artifactSources.contains(document.getSourceId())) {
            return 1.0;
        }
        Set<String> neighbors = graph.getOrDefault(document.getSourceId(), Set.of());
        for (String artifact : artifactSources) {
            if (neighbors.contains(artifact)) {
                score += 1.0;
            } else if (document.getPath() != null && artifact.endsWith(document.getPath().getFileName().toString())) {
                score += 0.5;
            }
        }
        return Math.min(1.0, score / artifactSources.size());
    }

    private List<String> normalizeArtifactSources(List<Path> artifactHints) {
        List<String> sources = new ArrayList<>();
        for (Path artifact : artifactHints == null ? List.<Path>of() : artifactHints) {
            try {
                sources.add(relativize(resolveToWorkspace(artifact)));
            } catch (IOException ignored) {
                // Skip unresolved artifact hints.
            }
        }
        return sources;
    }

    private Path resolveToWorkspace(Path path) throws IOException {
        Path candidate = path.isAbsolute() ? path : workspaceRoot.resolve(path);
        Path resolved = candidate.toAbsolutePath().normalize();
        if (!resolved.startsWith(workspaceRoot)) {
            throw new IOException("Path escapes workspace: " + path);
        }
        return resolved;
    }

    private String relativize(Path path) {
        if (path.startsWith(workspaceRoot)) {
            return workspaceRoot.relativize(path).toString().replace('\\', '/');
        }
        return path.toString().replace('\\', '/');
    }

    private boolean isLikelyText(Path path) {
        String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (lower.endsWith(".class") || lower.endsWith(".zip") || lower.endsWith(".jpg")
                || lower.endsWith(".jfif") || lower.endsWith(".mp3") || lower.endsWith(".png")) {
            return false;
        }
        return true;
    }

    private Path canonicalizeMirrorTarget(Path file) {
        Path normalized = file.toAbsolutePath().normalize();
        String sourceId = relativize(normalized);
        String normalizedSource = sourceId.toLowerCase(Locale.ROOT).replace('\\', '/');
        if (isPolicySkippedPath(normalizedSource)) {
            return null;
        }

        if (normalizedSource.startsWith(ROOT_CORPUS_PREFIX)) {
            return normalized;
        }

        boolean rootLevel = !normalizedSource.contains("/");
        boolean generatedRootMirror = normalizedSource.contains(GENERATED_ROOT_SESSION_PREFIX);
        if (!rootLevel && !generatedRootMirror) {
            return normalized;
        }

        Path fileName = normalized.getFileName();
        if (fileName == null) {
            return normalized;
        }
        Path canonical = workspaceRoot.resolve(Path.of("docs", "root-md-corpus", fileName.toString())).normalize();
        if (Files.exists(canonical)) {
            return canonical;
        }
        return normalized;
    }

    private boolean isPolicySkippedPath(String normalizedSource) {
        for (String marker : INACTIVE_TREE_MARKERS) {
            if (normalizedSource.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPdf(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".pdf");
    }

    private boolean isAuthoritativeDocument(String sourceId, Path path, String content) {
        String normalizedSource = sourceId == null ? "" : sourceId.toLowerCase(Locale.ROOT);
        String fileName = path == null || path.getFileName() == null
            ? normalizedSource
            : path.getFileName().toString().toLowerCase(Locale.ROOT);
        boolean requiresProof = false;
        for (String keyword : CLAIM_KEYWORDS) {
            if (fileName.contains(keyword)) {
                requiresProof = true;
                break;
            }
        }
        if (!requiresProof) {
            return true;
        }
        String lowerContent = content == null ? "" : content.toLowerCase(Locale.ROOT);
        for (String marker : VERIFIER_EVIDENCE_MARKERS) {
            if (lowerContent.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private boolean looksLikeCode(Path path, String content) {
        String name = path == null ? "" : path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".java")
            || name.endsWith(".rs")
            || name.endsWith(".py")
            || content.contains("class ")
            || content.contains("fn ")
            || content.contains("public ");
    }

    private void rebuildCausalGraph() {
        causalModel.clear();
        causalAnchorsByDocument.clear();

        Map<String, Set<String>> sourcesBySimpleName = new HashMap<>();
        for (MemoryDocument document : documentsBySource.values()) {
            String docNodeId = documentNodeId(document.getSourceId());
            addCausalNode(docNodeId, document.getSourceId());

            for (String simpleName : extractDocumentSimpleNames(document)) {
                sourcesBySimpleName.computeIfAbsent(simpleName, ignored -> new LinkedHashSet<>()).add(document.getSourceId());
            }
        }

        for (MemoryDocument document : documentsBySource.values()) {
            String docNodeId = documentNodeId(document.getSourceId());
            Set<String> anchors = extractCausalAnchors(document);
            causalAnchorsByDocument.put(document.getSourceId(), anchors);
            for (String anchor : anchors) {
                String conceptNode = conceptNodeId(anchor);
                addCausalNode(conceptNode, anchor);
                addCausalEdge(conceptNode, docNodeId);
            }

            for (String importedType : extractImportedTypes(document.getPreview())) {
                String normalizedImport = normalizeConcept(importedType);
                if (normalizedImport.isBlank()) {
                    continue;
                }
                String importNode = conceptNodeId(normalizedImport);
                addCausalNode(importNode, importedType);
                addCausalEdge(importNode, docNodeId);

                Set<String> sources = sourcesBySimpleName.getOrDefault(normalizedImport, Set.of());
                if (sources.size() == 1) {
                    String dependencySource = sources.iterator().next();
                    if (!dependencySource.equals(document.getSourceId())) {
                        addCausalEdge(documentNodeId(dependencySource), docNodeId);
                    }
                }
            }

            for (String mentionedType : extractMentionedTypes(document.getPreview())) {
                String normalizedMention = normalizeConcept(mentionedType);
                Set<String> sources = sourcesBySimpleName.getOrDefault(normalizedMention, Set.of());
                if (sources.size() == 1) {
                    String dependencySource = sources.iterator().next();
                    if (!dependencySource.equals(document.getSourceId())) {
                        addCausalEdge(documentNodeId(dependencySource), docNodeId);
                    }
                }
            }

            for (CausalRelation relation : extractCausalRelations(document.getPreview())) {
                if (relation.cause().equals(relation.effect())) {
                    continue;
                }
                String causeNode = conceptNodeId(relation.cause());
                String effectNode = conceptNodeId(relation.effect());
                addCausalNode(causeNode, relation.cause());
                addCausalNode(effectNode, relation.effect());
                addCausalEdge(causeNode, effectNode);
            }
        }
    }

    private void restoreCausalGraph(
            Map<String, String> causalNodes,
            Map<String, Set<String>> causalEdges,
            Map<String, Set<String>> causalAnchors) {
        causalModel.clear();
        causalAnchorsByDocument.clear();
        for (Map.Entry<String, String> entry : causalNodes.entrySet()) {
            addCausalNode(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Set<String>> entry : causalEdges.entrySet()) {
            for (String child : entry.getValue() == null ? Set.<String>of() : entry.getValue()) {
                addCausalEdge(entry.getKey(), child);
            }
        }
        if (causalAnchors != null) {
            causalAnchors.forEach((sourceId, anchors) ->
                causalAnchorsByDocument.put(sourceId, new LinkedHashSet<>(anchors == null ? Set.of() : anchors))
            );
        }
    }

    private void addCausalNode(String nodeId, String variable) {
        if (nodeId == null || nodeId.isBlank() || causalModel.getNode(nodeId) != null) {
            return;
        }
        causalModel.addNode(nodeId, variable == null ? nodeId : variable);
    }

    private void addCausalEdge(String parentId, String childId) {
        if (parentId == null || childId == null || parentId.equals(childId)) {
            return;
        }
        if (causalModel.getNode(parentId) == null || causalModel.getNode(childId) == null) {
            return;
        }
        if (causalModel.getChildren(parentId).contains(childId)) {
            return;
        }
        try {
            causalModel.addEdge(parentId, childId);
        } catch (IllegalArgumentException ignored) {
            // Skip cyclic or invalid heuristic edges rather than poisoning the live DAG.
        }
    }

    private Set<String> extractDocumentSimpleNames(MemoryDocument document) {
        Set<String> names = new LinkedHashSet<>();
        if (document.getPath() != null && document.getPath().getFileName() != null) {
            names.add(normalizeConcept(document.getPath().getFileName().toString().replaceFirst("\\.java$", "")));
        }
        names.addAll(extractDeclaredTypes(document.getPreview()));
        return names;
    }

    private Set<String> extractCausalAnchors(MemoryDocument document) {
        Set<String> anchors = new LinkedHashSet<>();
        anchors.addAll(extractDocumentSimpleNames(document));
        anchors.addAll(extractImportedTypes(document.getPreview()));
        if (document.isCodeLike()) {
            anchors.addAll(extractMentionedTypes(document.getPreview()));
        }
        for (CausalRelation relation : extractCausalRelations(document.getPreview())) {
            anchors.add(relation.cause());
            anchors.add(relation.effect());
        }
        anchors.removeIf(String::isBlank);
        return anchors;
    }

    private Set<String> extractDeclaredTypes(String content) {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = TYPE_DECLARATION_PATTERN.matcher(content == null ? "" : content);
        while (matcher.find()) {
            String normalized = normalizeConcept(matcher.group(1));
            if (!normalized.isBlank()) {
                names.add(normalized);
            }
        }
        return names;
    }

    private Set<String> extractImportedTypes(String content) {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = IMPORT_PATTERN.matcher(content == null ? "" : content);
        while (matcher.find()) {
            String fqcn = matcher.group(1).trim();
            int separator = fqcn.lastIndexOf('.');
            String simpleName = separator >= 0 ? fqcn.substring(separator + 1) : fqcn;
            String normalized = normalizeConcept(simpleName);
            if (!normalized.isBlank()) {
                names.add(normalized);
            }
        }
        return names;
    }

    private Set<String> extractMentionedTypes(String content) {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = TYPE_TOKEN_PATTERN.matcher(content == null ? "" : content);
        while (matcher.find()) {
            String normalized = normalizeConcept(matcher.group());
            if (!normalized.isBlank()) {
                names.add(normalized);
            }
        }
        return names;
    }

    private List<CausalRelation> extractCausalRelations(String content) {
        List<CausalRelation> relations = new ArrayList<>();
        Matcher matcher = CAUSAL_PHRASE_PATTERN.matcher(content == null ? "" : content);
        while (matcher.find()) {
            String cause = normalizeConcept(matcher.group(1));
            String effect = normalizeConcept(matcher.group(3));
            if (!cause.isBlank() && !effect.isBlank()) {
                relations.add(new CausalRelation(cause, effect));
            }
        }
        return relations;
    }

    private List<String> extractQueryConcepts(List<String> queryTerms, List<String> artifactSources) {
        Set<String> concepts = new LinkedHashSet<>();
        for (String term : queryTerms) {
            String normalized = normalizeConcept(term);
            if (!normalized.isBlank() && !CAUSAL_STOPWORDS.contains(normalized)) {
                concepts.add(normalized);
            }
            if (concepts.size() >= 6) {
                break;
            }
        }
        for (String artifactSource : artifactSources == null ? List.<String>of() : artifactSources) {
            Path artifactPath = Path.of(artifactSource);
            String fileName = artifactPath.getFileName() == null ? artifactSource : artifactPath.getFileName().toString();
            String normalized = normalizeConcept(fileName.replaceFirst("\\.java$", ""));
            if (!normalized.isBlank()) {
                concepts.add(normalized);
            }
        }
        return new ArrayList<>(concepts);
    }

    private CausalAssessment causalAssessment(
            MemoryDocument document,
            List<String> queryConcepts,
            List<String> artifactSources,
            double lexical,
            double graphScore,
            double hdcScore) {
        if (!causalPolicyEnabled) {
            return new CausalAssessment(0.0, 0.0, false, List.of("causal.disabled"));
        }
        String docNodeId = documentNodeId(document.getSourceId());
        Set<String> anchors = causalAnchorsByDocument.getOrDefault(document.getSourceId(), Set.of());
        List<String> notes = new ArrayList<>();
        double support = 0.0;
        double identifiableBoost = 0.0;
        boolean hasKnownContext = false;

        for (String queryConcept : queryConcepts == null ? List.<String>of() : queryConcepts) {
            String conceptNode = conceptNodeId(queryConcept);
            if (causalModel.getNode(conceptNode) == null) {
                continue;
            }
            hasKnownContext = true;
            if (anchors.contains(queryConcept)) {
                support = Math.max(support, 1.0);
                addNote(notes, "causal.anchor=" + queryConcept);
            } else if (causalVerifier.verifyCausalPath(conceptNode, docNodeId)) {
                support = Math.max(support, 0.82);
                addNote(notes, "causal.path=" + queryConcept);
            } else if (causalVerifier.verifyCausalPath(docNodeId, conceptNode)) {
                support = Math.max(support, 0.58);
                addNote(notes, "causal.reverse=" + queryConcept);
            }
            if (support > 0.0 && doCalculusEngine.isIdentifiable(docNodeId, conceptNode)) {
                identifiableBoost = Math.max(identifiableBoost, 0.10);
                addNote(notes, "causal.identifiable=" + queryConcept);
            }
        }

        for (String artifactSource : artifactSources == null ? List.<String>of() : artifactSources) {
            String artifactNodeId = documentNodeId(artifactSource);
            if (causalModel.getNode(artifactNodeId) == null || artifactSource.equals(document.getSourceId())) {
                continue;
            }
            hasKnownContext = true;
            if (causalVerifier.verifyCausalPath(artifactNodeId, docNodeId)) {
                support = Math.max(support, 0.76);
                addNote(notes, "artifact.path=" + artifactSource);
            } else if (causalVerifier.verifyCausalPath(docNodeId, artifactNodeId)) {
                support = Math.max(support, 0.44);
                addNote(notes, "artifact.reverse=" + artifactSource);
            }
        }

        double baseCorrelation = Math.max(lexical, Math.max(graphScore, hdcScore));
        double spuriousPenalty = 0.0;
        if (hasKnownContext && support < 0.05 && baseCorrelation >= 0.55) {
            spuriousPenalty = 0.22;
            addNote(notes, "causal.spurious-risk");
        } else if (hasKnownContext && support < 0.05 && baseCorrelation >= 0.35) {
            spuriousPenalty = 0.10;
            addNote(notes, "causal.weak-grounding");
        }

        double causalScore = Math.min(1.0, support + identifiableBoost);
        return new CausalAssessment(causalScore, spuriousPenalty, causalScore > 0.10, notes);
    }

    private void addNote(List<String> notes, String note) {
        if (!notes.contains(note)) {
            notes.add(note);
        }
    }

    private String conceptNodeId(String concept) {
        String normalized = normalizeConcept(concept);
        return normalized.isBlank() ? "" : "concept:" + normalized;
    }

    private String documentNodeId(String sourceId) {
        return "doc:" + sourceId.replace('\\', '/');
    }

    private String normalizeConcept(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String working = raw.trim();
        int separator = Math.max(working.lastIndexOf('/'), Math.max(working.lastIndexOf('\\'), working.lastIndexOf('.')));
        if (separator >= 0 && separator + 1 < working.length()) {
            working = working.substring(separator + 1);
        }
        working = working.replaceFirst("\\.java$", "");
        String normalized = working.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]+", "");
        if (normalized.length() < 2 || CAUSAL_STOPWORDS.contains(normalized)) {
            return "";
        }
        return normalized;
    }

    private record CausalRelation(String cause, String effect) {
    }

    private record CausalAssessment(
            double causalScore,
            double spuriousPenalty,
            boolean grounded,
            List<String> notes) {
    }
}
