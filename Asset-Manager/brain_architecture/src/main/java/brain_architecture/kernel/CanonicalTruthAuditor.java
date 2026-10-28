package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Builds a strict evidence canon from narrative source artifacts and local implementation evidence.
 */
public final class CanonicalTruthAuditor {
    private static final Path DEFAULT_BRAINS_SOURCE = Path.of("C:", "Users", "eyeka", "OneDrive", "Documents", "fraynix-brains.txt");
    private static final Path DEFAULT_AGENT_SOURCE = Path.of("C:", "Users", "eyeka", "OneDrive", "Documents", "new-agent.txt");
    private static final Path DEFAULT_PDF_SOURCE = Path.of("C:", "Users", "eyeka", "OneDrive", "Documents", "fraynix-system-full-2.pdf");

    private static final Path OUTLINE_PATH = Path.of("FRAYNIX_TRUE_OUTLINE.md");
    private static final Path MATRIX_PATH = Path.of("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv");
    private static final Path PROTOCOL_PATH = Path.of("FRAYNIX_CONTINUITY_PROTOCOL.md");
    private static final Path STATUS_PATH = Path.of("generated", "CANONICAL_AUDIT_STATUS.tsv");

    private static final Pattern BLANK_LINE = Pattern.compile("\\R\\s*\\R");
    private static final Pattern URL_PATTERN = Pattern.compile("\\b([a-z0-9-]+\\.)+[a-z]{2,}\\b", Pattern.CASE_INSENSITIVE);
    private static final List<String> CLAIM_KEYWORDS = List.of(
        "offline kernel", "taskenvelope", "shadow mutation", "verifier", "repair memory",
        "lazarus", "exocortex", "f0_engine", "simd", "synaptic bridge", "brainfeedbacklistener",
        "fraybuilderregistry", "fhe", "zk-snark", "natively sparse attention", "recurrent-depth",
        "mythos", "project glasswing", "sovereign", "self-build", "air-gapped"
    );
    private static final List<String> OVERREACH_TERMS = List.of(
        "first of its kind", "true agi", "fully autonomous", "no limits", "10-trillion",
        "surpass", "quantum", "bare-metal sovereignty", "can hack anything", "infinite"
    );

    private static final List<Path> OFFLINE_KERNEL_EVIDENCE = List.of(
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "OfflineKernel.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "TaskEnvelope.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "TaskPlanner.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "KernelVerifier.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "HybridMemoryStore.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "ShadowMutationEngine.java")
    );
    private static final List<Path> VERIFIER_REPAIR_EVIDENCE = List.of(
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "KernelVerifier.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "VerifierDiagnostic.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "MutationProposalEngine.java"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "RepairMemoryStore.java")
    );
    private static final List<Path> REPAIR_MEMORY_EVIDENCE = List.of(
        Path.of(".northstar-kernel", "repair-memory.tsv"),
        Path.of(".northstar-kernel", "repair-templates.tsv"),
        Path.of("Java-Memory", "Asset-Manager", "brain_architecture", "src", "main", "java", "brain_architecture", "kernel", "RepairMemoryStore.java")
    );
    private static final List<Path> LAZARUS_EVIDENCE = List.of(
        Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "com", "eyeoverthink", "lazarus", "LazarusEngine.java")
    );
    private static final List<Path> EXOCORTEX_EVIDENCE = List.of(
        Path.of("Java-Memory", "Asset-Manager", "src", "main", "java", "fraymus", "exocortex", "ExocortexAbsorber.java"),
        Path.of("Java-Memory", "Asset-Manager", "src", "test", "java", "fraymus", "exocortex", "ExocortexAbsorberTest.java")
    );
    private static final List<Path> F0_EVIDENCE = List.of(
        Path.of("f0_engine", "src", "field_engine.rs"),
        Path.of("f0_engine", "src", "simd_ops.rs"),
        Path.of("f0_engine", "src", "cisa.rs")
    );
    private static final Path BUILDER_REGISTRY_PATH = Path.of(
        "Java-Memory", "Asset-Manager", "src", "main", "java", "com", "fraymus", "builders", "FrayBuilderRegistry.java"
    );
    private static final Path BRAIN_FEEDBACK_LISTENER_PATH = Path.of(
        "Java-Memory", "Asset-Manager", "src", "main", "java", "com", "fraymus", "neural", "synapse", "BrainFeedbackListener.java"
    );

    private final KernelConfiguration configuration;
    private final OptionalPdfTextExtractor pdfTextExtractor;

    public CanonicalTruthAuditor(KernelConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.pdfTextExtractor = new OptionalPdfTextExtractor();
    }

    public CanonicalAuditReport audit() throws IOException {
        return audit(resolveDefaultSources());
    }

    public CanonicalAuditReport audit(List<Path> sourceDocs) throws IOException {
        Files.createDirectories(configuration.getStateRoot().resolve("generated"));
        List<Path> resolvedSources = normalizeSources(sourceDocs);
        List<ClaimAssessment> assessments = new ArrayList<>();

        for (Path source : resolvedSources) {
            String text = extractSourceText(source);
            if (text.isBlank()) {
                continue;
            }

            List<String> claims = extractCandidateClaims(text);
            for (String claim : claims) {
                assessments.add(assessClaim(source, claim));
            }

            int researchLoops = countOccurrencesIgnoreCase(text, "researching websites");
            if (researchLoops >= 3) {
                assessments.add(assessClaim(source,
                    "Researching websites loops appear repeatedly in this artifact and should be treated as low-authority context."));
            }
        }

        assessments.addAll(buildImplementationAnchorClaims());

        if (assessments.isEmpty()) {
            assessments.add(assessClaim(
                configuration.getWorkspaceRoot(),
                "No parseable claim evidence was extracted from the requested source artifacts."));
        }

        Map<String, ClaimAssessment> deduped = new LinkedHashMap<>();
        for (ClaimAssessment assessment : assessments) {
            deduped.putIfAbsent(assessment.record().id(), assessment);
        }
        List<ClaimAssessment> ordered = new ArrayList<>(deduped.values());
        ordered.sort(Comparator
            .comparingInt((ClaimAssessment value) -> statusPriority(value.record().status()))
            .thenComparing(value -> value.record().sourceDoc())
            .thenComparing(value -> value.record().id()));

        int verified = (int) ordered.stream().filter(value -> value.record().status() == ClaimStatus.VERIFIED).count();
        int scaffold = (int) ordered.stream().filter(value -> value.record().status() == ClaimStatus.SCAFFOLD).count();
        int speculative = (int) ordered.stream().filter(value -> value.record().status() == ClaimStatus.SPECULATIVE).count();
        Instant refreshedAt = Instant.now();

        Path outline = configuration.getWorkspaceRoot().resolve(OUTLINE_PATH);
        Path matrix = configuration.getWorkspaceRoot().resolve(MATRIX_PATH);
        Path protocol = configuration.getWorkspaceRoot().resolve(PROTOCOL_PATH);

        Files.writeString(matrix, buildMatrix(ordered), StandardCharsets.UTF_8);
        Files.writeString(outline, buildOutline(ordered, resolvedSources, refreshedAt, verified, scaffold, speculative), StandardCharsets.UTF_8);
        Files.writeString(protocol, buildContinuityProtocol(refreshedAt), StandardCharsets.UTF_8);

        CanonicalAuditStatus status = new CanonicalAuditStatus(verified, scaffold, speculative, refreshedAt);
        writeStatus(status);

        Map<String, String> driftNotes = new LinkedHashMap<>();
        for (ClaimAssessment assessment : ordered) {
            driftNotes.putIfAbsent(assessment.record().id(), assessment.driftNote());
        }

        return new CanonicalAuditReport(
            ordered.stream().map(ClaimAssessment::record).toList(),
            Map.copyOf(driftNotes),
            List.of(OUTLINE_PATH, MATRIX_PATH, PROTOCOL_PATH),
            outline,
            matrix,
            protocol,
            resolvedSources,
            verified,
            scaffold,
            speculative,
            refreshedAt
        );
    }

    public CanonicalAuditStatus loadLatestStatus() {
        Path statusFile = configuration.getStateRoot().resolve(STATUS_PATH);
        if (!Files.exists(statusFile)) {
            return CanonicalAuditStatus.empty();
        }

        try {
            Map<String, String> values = new LinkedHashMap<>();
            for (String line : Files.readAllLines(statusFile, StandardCharsets.UTF_8)) {
                int split = line.indexOf('=');
                if (split <= 0) {
                    continue;
                }
                values.put(line.substring(0, split).trim(), line.substring(split + 1).trim());
            }
            int verified = parseInt(values.get("verified"));
            int scaffold = parseInt(values.get("scaffold"));
            int speculative = parseInt(values.get("speculative"));
            Instant refreshedAt = parseInstant(values.get("refreshedAt"));
            return new CanonicalAuditStatus(verified, scaffold, speculative, refreshedAt);
        } catch (IOException ignored) {
            return CanonicalAuditStatus.empty();
        }
    }

    private void writeStatus(CanonicalAuditStatus status) throws IOException {
        Path statusFile = configuration.getStateRoot().resolve(STATUS_PATH);
        Files.createDirectories(statusFile.getParent());
        List<String> lines = List.of(
            "verified=" + status.verifiedClaims(),
            "scaffold=" + status.scaffoldClaims(),
            "speculative=" + status.speculativeClaims(),
            "refreshedAt=" + (status.refreshedAt() == null ? "" : status.refreshedAt().toString())
        );
        Files.write(statusFile, lines, StandardCharsets.UTF_8);
    }

    private List<Path> resolveDefaultSources() {
        List<Path> configured = parseConfiguredSources(System.getenv("FRAYNIX_CANON_SOURCES"));
        if (!configured.isEmpty()) {
            return configured;
        }
        return List.of(DEFAULT_BRAINS_SOURCE, DEFAULT_AGENT_SOURCE, DEFAULT_PDF_SOURCE);
    }

    private List<Path> parseConfiguredSources(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<Path> paths = new ArrayList<>();
        for (String segment : raw.split(";")) {
            if (segment == null || segment.isBlank()) {
                continue;
            }
            paths.add(Path.of(segment.trim()));
        }
        return paths;
    }

    private List<Path> normalizeSources(List<Path> sourceDocs) {
        LinkedHashSet<Path> normalized = new LinkedHashSet<>();
        for (Path source : sourceDocs == null ? List.<Path>of() : sourceDocs) {
            if (source == null) {
                continue;
            }
            Path resolved = source.toAbsolutePath().normalize();
            if (Files.exists(resolved)) {
                normalized.add(resolved);
            }
        }
        return List.copyOf(normalized);
    }

    private String extractSourceText(Path source) throws IOException {
        String fileName = source.getFileName() == null ? "" : source.getFileName().toString().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".pdf")) {
            String extracted = pdfTextExtractor.extractAll(source);
            if (!extracted.isBlank()) {
                return extracted;
            }
        }
        byte[] content = Files.readAllBytes(source);
        return new String(content, StandardCharsets.UTF_8);
    }

    private List<String> extractCandidateClaims(String text) {
        List<String> paragraphs = BLANK_LINE.splitAsStream(text)
            .map(this::normalizeWhitespace)
            .filter(value -> !value.isBlank())
            .toList();

        LinkedHashSet<String> selected = new LinkedHashSet<>();
        for (String paragraph : paragraphs) {
            if (!looksLikeClaim(paragraph)) {
                continue;
            }
            String lower = paragraph.toLowerCase(Locale.ROOT);
            boolean keywordMatch = CLAIM_KEYWORDS.stream().anyMatch(lower::contains);
            if (keywordMatch || containsAuthorityMarkers(lower)) {
                selected.add(paragraph);
            }
            if (selected.size() >= 64) {
                break;
            }
        }

        if (selected.isEmpty()) {
            for (String paragraph : paragraphs) {
                if (!looksLikeClaim(paragraph)) {
                    continue;
                }
                selected.add(paragraph);
                if (selected.size() >= 24) {
                    break;
                }
            }
        }
        return List.copyOf(selected);
    }

    private boolean containsAuthorityMarkers(String lower) {
        return lower.contains("[verified]")
            || lower.contains("[scaffold]")
            || lower.contains("[speculative]")
            || lower.contains("evidence")
            || lower.contains("implementation")
            || lower.contains("integration")
            || lower.contains("roadmap");
    }

    private boolean looksLikeClaim(String value) {
        if (value.length() < 50 || value.length() > 640) {
            return false;
        }
        if (value.equals("Thoughts") || value.equals("```")) {
            return false;
        }
        long letters = value.chars().filter(Character::isLetter).count();
        return letters >= 20;
    }

    private String normalizeWhitespace(String value) {
        return value.replace('\u200b', ' ').replaceAll("\\s+", " ").trim();
    }

    private ClaimAssessment assessClaim(Path source, String claim) {
        ClaimCategory category = categorize(claim);
        EvidenceDecision decision = evaluate(category, claim);

        Instant lastValidatedAt = decision.lastValidatedAt();
        if (lastValidatedAt == null) {
            lastValidatedAt = resolveFileTimestamp(source);
        }

        String id = buildClaimId(source, claim);
        ClaimRecord record = new ClaimRecord(
            id,
            source.toString(),
            claim,
            decision.status(),
            decision.evidenceRefs(),
            decision.confidence(),
            lastValidatedAt
        );
        return new ClaimAssessment(record, decision.driftNote(), category);
    }

    private List<ClaimAssessment> buildImplementationAnchorClaims() {
        List<ClaimAssessment> anchors = new ArrayList<>();
        Path offlineAnchor = firstExistingPath(OFFLINE_KERNEL_EVIDENCE);
        Path verifierAnchor = firstExistingPath(VERIFIER_REPAIR_EVIDENCE);
        Path repairAnchor = firstExistingPath(REPAIR_MEMORY_EVIDENCE);
        Path f0Anchor = firstExistingPath(F0_EVIDENCE);
        Path exocortexAnchor = firstExistingPath(EXOCORTEX_EVIDENCE);
        Path lazarusAnchor = firstExistingPath(LAZARUS_EVIDENCE);

        anchors.add(assessClaim(offlineAnchor,
            "Offline kernel typed execution pipeline exists in local implementation surfaces."));
        anchors.add(assessClaim(verifierAnchor,
            "Verifier diagnostics, mutation proposals, and shadow mutation execution are implemented locally."));
        anchors.add(assessClaim(repairAnchor,
            "Repair memory and template persistence surfaces exist and are wired into mutation ranking."));
        anchors.add(assessClaim(f0Anchor,
            "f0_engine Rust SIMD substrate files exist in local source and remain runtime boundary-backed."));
        anchors.add(assessClaim(exocortexAnchor,
            "Exocortex absorber and tests exist as scaffold-level wrapper generation infrastructure."));
        anchors.add(assessClaim(lazarusAnchor,
            "Lazarus engine exists as an implemented adaptive loop surface, with broader AGI claims treated as scaffold."));
        anchors.add(assessClaim(firstExistingPath(List.of(BUILDER_REGISTRY_PATH)),
            "FrayBuilderRegistry remains a stub-routing surface unless concrete dynamic routing evidence is present."));
        return anchors;
    }

    private Path firstExistingPath(List<Path> candidates) {
        for (Path relative : candidates) {
            Path absolute = configuration.getWorkspaceRoot().resolve(relative).normalize();
            if (Files.exists(absolute)) {
                return absolute;
            }
        }
        return configuration.getWorkspaceRoot();
    }

    private ClaimCategory categorize(String claim) {
        String lower = claim.toLowerCase(Locale.ROOT);
        if (lower.contains("researching websites") || countPattern(lower, URL_PATTERN) >= 3) {
            return ClaimCategory.LOW_AUTHORITY_RESEARCH;
        }
        if (lower.contains("fraybuilderregistry") || lower.contains("14 generators")) {
            return ClaimCategory.BUILDER_ROUTING;
        }
        if (lower.contains("brainfeedbacklistener") || lower.contains("synaptic bridge")) {
            return ClaimCategory.BRAIN_BRIDGE;
        }
        if (lower.contains("fhe") || lower.contains("fully homomorphic encryption") || lower.contains("zk-snark")) {
            return ClaimCategory.CRYPTO;
        }
        if (lower.contains("mythos") || lower.contains("project glasswing") || lower.contains("swe-bench")) {
            return ClaimCategory.EXTERNAL_SCENARIO;
        }
        if (lower.contains("exocortex")) {
            return ClaimCategory.EXOCORTEX;
        }
        if (lower.contains("lazarus")) {
            return ClaimCategory.LAZARUS;
        }
        if (lower.contains("f0_engine") || lower.contains("simd") || lower.contains("cisa")) {
            return ClaimCategory.F0_RUNTIME;
        }
        if (lower.contains("repair memory") || lower.contains("repair template")) {
            return ClaimCategory.REPAIR_MEMORY;
        }
        if (lower.contains("verifier") || lower.contains("shadow mutation") || lower.contains("mutation proposal")) {
            return ClaimCategory.VERIFIER_REPAIR;
        }
        if (lower.contains("offline kernel") || lower.contains("taskenvelope") || lower.contains("air-gapped")) {
            return ClaimCategory.OFFLINE_KERNEL;
        }
        return ClaimCategory.GENERAL_VISION;
    }

    private EvidenceDecision evaluate(ClaimCategory category, String claim) {
        EvidenceSnapshot snapshot;
        ClaimStatus status;
        double confidence;
        String driftNote;

        switch (category) {
            case OFFLINE_KERNEL -> {
                snapshot = snapshotEvidence(OFFLINE_KERNEL_EVIDENCE);
                if (snapshot.allPresent()) {
                    status = ClaimStatus.VERIFIED;
                    confidence = 0.95;
                    driftNote = "Implementation and typed pipeline surfaces exist in local kernel code.";
                } else if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.62;
                    driftNote = "Kernel implementation exists but the claim is broader than current proven coverage.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.18;
                    driftNote = "No local kernel evidence was found for this claim.";
                }
            }
            case VERIFIER_REPAIR -> {
                snapshot = snapshotEvidence(VERIFIER_REPAIR_EVIDENCE);
                if (snapshot.allPresent()) {
                    status = ClaimStatus.VERIFIED;
                    confidence = 0.92;
                    driftNote = "Verifier and repair proposal surfaces are implemented locally.";
                } else if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.60;
                    driftNote = "Partial verifier/repair implementation exists but evidence is incomplete.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.15;
                    driftNote = "No verifier/repair evidence exists for this claim.";
                }
            }
            case REPAIR_MEMORY -> {
                snapshot = snapshotEvidence(REPAIR_MEMORY_EVIDENCE);
                if (snapshot.evidenceRefs().size() >= 2) {
                    status = ClaimStatus.VERIFIED;
                    confidence = 0.90;
                    driftNote = "Repair memory code and persisted stores are present.";
                } else if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.56;
                    driftNote = "Repair memory exists but without complete persisted evidence.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.14;
                    driftNote = "Repair memory claim is not backed by local artifacts.";
                }
            }
            case LAZARUS -> {
                snapshot = snapshotEvidence(LAZARUS_EVIDENCE);
                if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.58;
                    driftNote = "Lazarus engine exists, but advanced control-theory claims are broader than proven runtime behavior.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.16;
                    driftNote = "No Lazarus source evidence was found.";
                }
            }
            case EXOCORTEX -> {
                snapshot = snapshotEvidence(EXOCORTEX_EVIDENCE);
                if (snapshot.allPresent()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.60;
                    driftNote = "Exocortex absorber and tests exist, but this surface is primarily scaffold/wrapper generation.";
                } else if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.48;
                    driftNote = "Partial exocortex scaffold evidence exists.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.12;
                    driftNote = "No exocortex implementation evidence was found.";
                }
            }
            case F0_RUNTIME -> {
                snapshot = snapshotEvidence(F0_EVIDENCE);
                if (snapshot.allPresent()) {
                    status = ClaimStatus.VERIFIED;
                    confidence = 0.88;
                    driftNote = "Rust f0 runtime source surfaces are present.";
                } else if (!snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.54;
                    driftNote = "Partial f0 runtime evidence exists.";
                } else {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.14;
                    driftNote = "No f0 runtime evidence was found for this claim.";
                }
            }
            case BUILDER_ROUTING -> {
                snapshot = snapshotEvidence(List.of(BUILDER_REGISTRY_PATH));
                if (snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.12;
                    driftNote = "Builder registry file is missing.";
                } else if (isStubRegistry()) {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.40;
                    driftNote = "Builder routing is stubbed (default routes to FrayShellBuilder).";
                } else {
                    status = ClaimStatus.VERIFIED;
                    confidence = 0.78;
                    driftNote = "Builder routing appears implemented in local registry.";
                }
            }
            case BRAIN_BRIDGE -> {
                snapshot = snapshotEvidence(List.of(BRAIN_FEEDBACK_LISTENER_PATH));
                if (snapshot.evidenceRefs().isEmpty()) {
                    status = ClaimStatus.SPECULATIVE;
                    confidence = 0.10;
                    driftNote = "BrainFeedbackListener surface was not found in local source.";
                } else {
                    status = ClaimStatus.SCAFFOLD;
                    confidence = 0.44;
                    driftNote = "Bridge interface exists but broader bridge behavior remains unproven.";
                }
            }
            case CRYPTO -> {
                snapshot = EvidenceSnapshot.empty();
                status = ClaimStatus.SPECULATIVE;
                confidence = 0.08;
                driftNote = "No local CKKS/FHE/zk-SNARK implementation evidence was found.";
            }
            case EXTERNAL_SCENARIO -> {
                snapshot = EvidenceSnapshot.empty();
                status = ClaimStatus.SPECULATIVE;
                confidence = 0.07;
                driftNote = "External scenario claims are treated as speculative unless independently proven in local code/tests.";
            }
            case LOW_AUTHORITY_RESEARCH -> {
                snapshot = EvidenceSnapshot.empty();
                status = ClaimStatus.SPECULATIVE;
                confidence = 0.05;
                driftNote = "Repetitive research narrative is low-authority context, not implementation truth.";
            }
            case GENERAL_VISION -> {
                snapshot = EvidenceSnapshot.empty();
                status = ClaimStatus.SCAFFOLD;
                confidence = 0.30;
                driftNote = "Vision statement retained as scaffold until concrete code/test/log evidence exists.";
            }
            default -> throw new IllegalStateException("Unhandled category: " + category);
        }

        String lower = claim.toLowerCase(Locale.ROOT);
        if (status == ClaimStatus.VERIFIED && OVERREACH_TERMS.stream().anyMatch(lower::contains)) {
            status = ClaimStatus.SCAFFOLD;
            confidence = Math.min(confidence, 0.62);
            driftNote = "Claim overreaches current evidence and is demoted to scaffold.";
        }

        return new EvidenceDecision(status, confidence, snapshot.evidenceRefs(), driftNote, snapshot.latestEvidenceTime());
    }

    private EvidenceSnapshot snapshotEvidence(List<Path> evidencePaths) {
        List<String> refs = new ArrayList<>();
        boolean allPresent = !evidencePaths.isEmpty();
        Instant latest = null;

        for (Path relative : evidencePaths) {
            Path absolute = configuration.getWorkspaceRoot().resolve(relative).normalize();
            if (Files.exists(absolute)) {
                refs.add(toWorkspaceRelative(absolute));
                Instant modified = resolveFileTimestamp(absolute);
                if (latest == null || modified.isAfter(latest)) {
                    latest = modified;
                }
            } else {
                allPresent = false;
            }
        }

        if (evidencePaths.isEmpty()) {
            allPresent = false;
        }

        return new EvidenceSnapshot(List.copyOf(refs), allPresent, latest);
    }

    private boolean isStubRegistry() {
        Path path = configuration.getWorkspaceRoot().resolve(BUILDER_REGISTRY_PATH).normalize();
        if (!Files.exists(path)) {
            return false;
        }
        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);
            return content.contains("return \"FrayShellBuilder\"")
                || content.toLowerCase(Locale.ROOT).contains("default for stub");
        } catch (IOException ignored) {
            return false;
        }
    }

    private String buildOutline(
            List<ClaimAssessment> ordered,
            List<Path> sources,
            Instant generatedAt,
            int verified,
            int scaffold,
            int speculative) {
        StringBuilder builder = new StringBuilder();
        builder.append("# FRAYNIX TRUE OUTLINE (Strict Evidence Canon)\n\n");
        builder.append("Generated: ").append(generatedAt).append("\n\n");
        builder.append("## Sources Audited\n\n");
        for (Path source : sources) {
            builder.append("- `").append(source).append("`\n");
        }
        if (sources.isEmpty()) {
            builder.append("- No source artifacts were available at audit time.\n");
        }
        builder.append("\n## Summary\n\n");
        builder.append("- VERIFIED claims: `").append(verified).append("`\n");
        builder.append("- SCAFFOLD claims: `").append(scaffold).append("`\n");
        builder.append("- SPECULATIVE claims: `").append(speculative).append("`\n");
        builder.append("- Rule: claims without local code/test/log evidence are automatically demoted.\n");

        appendSection(builder, "VERIFIED", ordered, ClaimStatus.VERIFIED);
        appendSection(builder, "SCAFFOLD", ordered, ClaimStatus.SCAFFOLD);
        appendSection(builder, "SPECULATIVE", ordered, ClaimStatus.SPECULATIVE);

        builder.append("\n## Explicitly Flagged Legacy Stubs\n\n");
        if (isStubRegistry()) {
            builder.append("- [FrayBuilderRegistry.java](")
                .append(configuration.getWorkspaceRoot().resolve(BUILDER_REGISTRY_PATH).toString().replace('\\', '/'))
                .append(") routes by default to `FrayShellBuilder`; this is a stub routing surface.\n");
        } else {
            builder.append("- No explicit builder-registry stub signature was detected at audit time.\n");
        }

        if (!Files.exists(configuration.getWorkspaceRoot().resolve(BRAIN_FEEDBACK_LISTENER_PATH))) {
            builder.append("- `BrainFeedbackListener.java` was not found at the expected path and remains speculative.\n");
        }

        builder.append("\n## Low-Authority Context Rule\n\n");
        builder.append("- Repetitive website-research passages are indexed as context only and do not upgrade implementation status.\n");
        builder.append("- Mythos/benchmark/security scenario claims remain speculative unless validated against local implementation.\n");
        return builder.toString();
    }

    private void appendSection(StringBuilder builder, String title, List<ClaimAssessment> assessments, ClaimStatus status) {
        builder.append("\n## ").append(title).append("\n\n");
        int limit = 0;
        for (ClaimAssessment assessment : assessments) {
            if (assessment.record().status() != status) {
                continue;
            }
            ClaimRecord record = assessment.record();
            builder.append("- `").append(record.id()).append("` ")
                .append(trimForSummary(record.claimText(), 180))
                .append("\n");
            builder.append("  source: `").append(record.sourceDoc()).append("`");
            if (!record.evidenceRefs().isEmpty()) {
                builder.append(" | evidence: `")
                    .append(String.join(", ", record.evidenceRefs().stream().limit(3).toList()))
                    .append("`");
            }
            builder.append(" | confidence: ")
                .append(String.format(Locale.ROOT, "%.2f", record.confidence()))
                .append(" | note: ")
                .append(assessment.driftNote())
                .append("\n");
            limit++;
            if (limit >= 22) {
                builder.append("- (additional entries omitted in outline; see audit matrix for full list)\n");
                break;
            }
        }
        if (limit == 0) {
            builder.append("- none\n");
        }
    }

    private String buildMatrix(List<ClaimAssessment> ordered) {
        StringBuilder builder = new StringBuilder();
        builder.append("id\tsource_doc\tclaim\tstatus\tconfidence\tevidence_paths\tdrift_note\tlast_validated_at\n");
        for (ClaimAssessment assessment : ordered) {
            ClaimRecord record = assessment.record();
            builder.append(tsv(record.id())).append('\t')
                .append(tsv(record.sourceDoc())).append('\t')
                .append(tsv(record.claimText())).append('\t')
                .append(record.status()).append('\t')
                .append(String.format(Locale.ROOT, "%.2f", record.confidence())).append('\t')
                .append(tsv(String.join(" | ", record.evidenceRefs()))).append('\t')
                .append(tsv(assessment.driftNote())).append('\t')
                .append(tsv(record.lastValidatedAt().toString()))
                .append('\n');
        }
        return builder.toString();
    }

    private String buildContinuityProtocol(Instant generatedAt) {
        StringBuilder builder = new StringBuilder();
        builder.append("# FRAYNIX CONTINUITY PROTOCOL\n\n");
        builder.append("Generated: ").append(generatedAt).append("\n\n");
        builder.append("## Purpose\n\n");
        builder.append("Keep future sessions grounded in local evidence so implementation truth is not lost behind repeated narrative text.\n\n");
        builder.append("## Canonical Truth Artifacts\n\n");
        builder.append("- `FRAYNIX_TRUE_OUTLINE.md`\n");
        builder.append("- `FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv`\n");
        builder.append("- `FRAYNIX_CONTINUITY_PROTOCOL.md`\n");
        builder.append("- `.northstar-kernel/generated/SELF_BUILD_MEMORY.md`\n");
        builder.append("- `.northstar-kernel/generated/SELF_BUILD_FORECAST.md`\n\n");
        builder.append("## Session Recovery Workflow\n\n");
        builder.append("1. Run `git status --short --branch` at workspace root.\n");
        builder.append("2. Run `run-offline-kernel.ps1 status <workspace>` and read `truthClaims*` plus `canonLastRefreshAt`.\n");
        builder.append("3. Run `run-offline-kernel.ps1 audit-canon <workspace>` before making truth-level claims.\n");
        builder.append("4. Run `run-offline-kernel.ps1 refresh-memory <workspace> --canon` so human and machine memory stay aligned.\n");
        builder.append("5. Use `verify-offline-kernel.ps1` smoke mode for quick checks and `verify-offline-kernel.ps1 -FullSuite` before sign-off.\n");
        builder.append("6. Treat `VERIFIED` as implementation truth, `SCAFFOLD` as partial/boundary, and `SPECULATIVE` as narrative/future.\n\n");
        builder.append("## Strict Evidence Rules\n\n");
        builder.append("- Claims without local code/test/log evidence are auto-demoted.\n");
        builder.append("- Legacy stubs are called out explicitly (for example builder registry default routing).\n");
        builder.append("- Repetitive website research text is low-authority context and cannot upgrade claim status.\n");
        return builder.toString();
    }

    private String tsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ').trim();
    }

    private String trimForSummary(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String normalized = normalizeWhitespace(text);
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }

    private String buildClaimId(Path source, String claim) {
        String material = source.toString() + "|" + normalizeWhitespace(claim).toLowerCase(Locale.ROOT);
        return "claim-" + sha256(material).substring(0, 12);
    }

    private String sha256(String material) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(material.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte value : bytes) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private String toWorkspaceRelative(Path absolute) {
        Path workspace = configuration.getWorkspaceRoot().toAbsolutePath().normalize();
        Path normalized = absolute.toAbsolutePath().normalize();
        if (normalized.startsWith(workspace)) {
            return workspace.relativize(normalized).toString().replace('\\', '/');
        }
        return normalized.toString().replace('\\', '/');
    }

    private Instant resolveFileTimestamp(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant();
        } catch (IOException ignored) {
            return Instant.EPOCH;
        }
    }

    private int parseInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private int statusPriority(ClaimStatus status) {
        return switch (status) {
            case VERIFIED -> 0;
            case SCAFFOLD -> 1;
            case SPECULATIVE -> 2;
        };
    }

    private int countPattern(String text, Pattern pattern) {
        return (int) pattern.matcher(text).results().count();
    }

    private int countOccurrencesIgnoreCase(String text, String token) {
        if (text == null || text.isBlank() || token == null || token.isBlank()) {
            return 0;
        }
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerToken = token.toLowerCase(Locale.ROOT);
        int index = 0;
        int count = 0;
        while (true) {
            index = lowerText.indexOf(lowerToken, index);
            if (index < 0) {
                return count;
            }
            count++;
            index += lowerToken.length();
        }
    }

    public record CanonicalAuditReport(
            List<ClaimRecord> claims,
            Map<String, String> driftNotesByClaimId,
            List<Path> artifactPaths,
            Path outlinePath,
            Path matrixPath,
            Path protocolPath,
            List<Path> sourcePaths,
            int verifiedClaims,
            int scaffoldClaims,
            int speculativeClaims,
            Instant refreshedAt) {
    }

    public record CanonicalAuditStatus(
            int verifiedClaims,
            int scaffoldClaims,
            int speculativeClaims,
            Instant refreshedAt) {
        public static CanonicalAuditStatus empty() {
            return new CanonicalAuditStatus(0, 0, 0, null);
        }
    }

    private record EvidenceSnapshot(
            List<String> evidenceRefs,
            boolean allPresent,
            Instant latestEvidenceTime) {
        static EvidenceSnapshot empty() {
            return new EvidenceSnapshot(List.of(), false, null);
        }
    }

    private record EvidenceDecision(
            ClaimStatus status,
            double confidence,
            List<String> evidenceRefs,
            String driftNote,
            Instant lastValidatedAt) {
    }

    private record ClaimAssessment(ClaimRecord record, String driftNote, ClaimCategory category) {
    }

    private enum ClaimCategory {
        OFFLINE_KERNEL,
        VERIFIER_REPAIR,
        REPAIR_MEMORY,
        LAZARUS,
        EXOCORTEX,
        F0_RUNTIME,
        BUILDER_ROUTING,
        BRAIN_BRIDGE,
        CRYPTO,
        EXTERNAL_SCENARIO,
        LOW_AUTHORITY_RESEARCH,
        GENERAL_VISION
    }
}
