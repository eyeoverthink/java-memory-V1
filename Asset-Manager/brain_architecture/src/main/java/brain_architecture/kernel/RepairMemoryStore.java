package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Persists successful repair lessons and scores future proposals against them.
 */
public final class RepairMemoryStore {
    private static final List<String> REPAIR_KIND_PRIORITY = List.of(
        "verifier.compiler.import",
        "verifier.compiler.signature",
        "verifier.compiler.package",
        "verifier.compiler.type_name",
        "verifier.compiler.multi_type_synthesis",
        "verifier.compiler.type_synthesis",
        "verifier.compiler.method_missing",
        "verifier.compiler.field_missing",
        "verifier.compiler.parse",
        "verifier.compiler.sanitize",
        "verifier.compiler.symbol"
    );
    private static final java.util.regex.Pattern METHOD_TITLE_PATTERN =
        java.util.regex.Pattern.compile("Add missing method stub\\s+([A-Za-z_$][\\w$]*)\\s+to\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern FIELD_TITLE_PATTERN =
        java.util.regex.Pattern.compile("Add missing field\\s+([A-Za-z_$][\\w$]*)\\s+to\\s+", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern IMPORT_GAIN_PATTERN =
        java.util.regex.Pattern.compile("Repair unresolved symbol by importing\\s+([A-Za-z_$][\\w$.]*)", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern SIGNATURE_GAIN_PATTERN =
        java.util.regex.Pattern.compile("Repair signature mismatch for\\s+([A-Za-z_$][\\w$]*)", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final Set<String> STOPWORDS = Set.of(
        "the", "and", "for", "that", "this", "with", "from", "into", "keep", "build",
        "offline", "repair", "kernel", "workspace", "source", "file", "class", "public",
        "java", "code", "task", "mode", "expected", "bounded", "using", "symbol"
    );

    private final Path stateRoot;
    private final Path lessonFile;
    private final Path templateFile;
    private final Map<String, RepairLesson> lessons = new LinkedHashMap<>();
    private final Map<String, RepairTemplate> templates = new LinkedHashMap<>();

    public RepairMemoryStore(Path stateRoot) throws IOException {
        this.stateRoot = stateRoot;
        Files.createDirectories(stateRoot);
        this.lessonFile = stateRoot.resolve("repair-memory.tsv");
        this.templateFile = stateRoot.resolve("repair-templates.tsv");
        load();
    }

    public synchronized int size() {
        return lessons.size();
    }

    public synchronized int templateCount() {
        return templates.size();
    }

    public synchronized List<RepairLesson> snapshotLessons() {
        return new ArrayList<>(lessons.values());
    }

    public synchronized List<RepairTemplate> snapshotTemplates() {
        return new ArrayList<>(templates.values());
    }

    public synchronized RepairLesson recordSuccessfulRepair(
            TaskEnvelope task,
            MutationProposal proposal,
            VerifierResult baselineVerifierResult) throws IOException {
        String repairKind = resolveRepairKind(proposal.getEvidenceTags());
        if (repairKind.isBlank()) {
            repairKind = "proposal." + slugify(proposal.getTitle());
        }

        Set<String> evidenceTags = new LinkedHashSet<>(proposal.getEvidenceTags());
        if (!repairKind.isBlank()) {
            evidenceTags.add(repairKind);
        }
        Set<String> goalTokens = limitTokens(tokenize(task.getGoal() + " " + String.join(" ", task.getConstraints())), 14);
        Set<String> targetTokens = extractTargetTokens(proposal);
        Set<String> diagnosticTokens = extractDiagnosticTokens(baselineVerifierResult, proposal);

        RepairLesson incoming = new RepairLesson(
            repairKind,
            proposal.getTitle(),
            evidenceTags,
            goalTokens,
            targetTokens,
            diagnosticTokens,
            1,
            System.currentTimeMillis()
        );

        RepairLesson merged = lessons.containsKey(repairKind)
            ? lessons.get(repairKind).merge(incoming)
            : incoming;
        lessons.put(repairKind, merged);
        RepairTemplate template = deriveTemplate(task, proposal, baselineVerifierResult, repairKind);
        if (template != null) {
            String templateKey = template.templateKey();
            templates.put(templateKey, templates.containsKey(templateKey)
                ? templates.get(templateKey).merge(template)
                : template);
        }
        persist();
        return merged;
    }

    public synchronized RepairMatch bestMatch(TaskEnvelope task, MutationProposal proposal) {
        Set<String> goalTokens = tokenize(task.getGoal() + " " + String.join(" ", task.getConstraints()));
        Set<String> proposalTokens = extractProposalTokens(proposal);
        Set<String> evidenceTags = new LinkedHashSet<>(proposal.getEvidenceTags());
        String proposalKind = resolveRepairKind(proposal.getEvidenceTags());

        RepairMatch best = null;
        for (RepairLesson lesson : lessons.values()) {
            double score = 0.0;
            List<String> notes = new ArrayList<>();
            if (!proposalKind.isBlank() && proposalKind.equals(lesson.repairKind())) {
                score += 0.18;
                notes.add("kind=" + lesson.repairKind());
            }

            int evidenceOverlap = overlapCount(evidenceTags, lesson.evidenceTags());
            if (evidenceOverlap > 0) {
                score += Math.min(0.12, evidenceOverlap * 0.04);
                notes.add("evidence=" + evidenceOverlap);
            }

            int goalOverlap = overlapCount(goalTokens, lesson.goalTokens());
            if (goalOverlap > 0) {
                score += Math.min(0.10, goalOverlap * 0.025);
                notes.add("goal=" + goalOverlap);
            }

            int targetOverlap = overlapCount(proposalTokens, lesson.targetTokens());
            if (targetOverlap > 0) {
                score += Math.min(0.08, targetOverlap * 0.02);
                notes.add("target=" + targetOverlap);
            }

            int diagnosticOverlap = overlapCount(proposalTokens, lesson.diagnosticTokens());
            if (diagnosticOverlap > 0) {
                score += Math.min(0.07, diagnosticOverlap * 0.0175);
                notes.add("diagnostic=" + diagnosticOverlap);
            }

            if (lesson.successCount() > 1) {
                score += Math.min(0.08, (lesson.successCount() - 1) * 0.02);
                notes.add("successes=" + lesson.successCount());
            }

            if (score <= 0.0) {
                continue;
            }
            RepairMatch candidate = new RepairMatch(lesson, Math.min(0.35, score), notes);
            if (best == null || candidate.bonus() > best.bonus()) {
                best = candidate;
            }
        }
        return best == null ? RepairMatch.none() : best;
    }

    public synchronized double scoreBonus(TaskEnvelope task, MutationProposal proposal) {
        return bestMatch(task, proposal).bonus();
    }

    public synchronized TemplateMatch bestTemplate(TaskEnvelope task, String repairKind, String contextText) {
        if (repairKind == null || repairKind.isBlank()) {
            return TemplateMatch.none();
        }

        Set<String> requestTokens = tokenize(task.getGoal() + " " + String.join(" ", task.getConstraints()) + " " + contextText);
        TemplateMatch best = null;
        for (RepairTemplate template : templates.values()) {
            if (!repairKind.equals(template.repairKind())) {
                continue;
            }
            double score = 0.16;
            List<String> notes = new ArrayList<>();
            notes.add("kind=" + repairKind);

            int overlap = overlapCount(requestTokens, template.contextTokens());
            if (overlap > 0) {
                score += Math.min(0.12, overlap * 0.03);
                notes.add("context=" + overlap);
            }

            if (template.successCount() > 1) {
                score += Math.min(0.08, (template.successCount() - 1) * 0.02);
                notes.add("successes=" + template.successCount());
            }

            TemplateMatch candidate = new TemplateMatch(template, Math.min(0.28, score), notes);
            if (best == null || candidate.bonus() > best.bonus()) {
                best = candidate;
            }
        }
        return best == null ? TemplateMatch.none() : best;
    }

    private void load() throws IOException {
        lessons.clear();
        templates.clear();
        if (!Files.exists(lessonFile)) {
            loadTemplates();
            return;
        }
        for (String line : Files.readAllLines(lessonFile, StandardCharsets.UTF_8)) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t", -1);
            if (parts.length < 8) {
                continue;
            }
            RepairLesson lesson = new RepairLesson(
                decode(parts[0]),
                decode(parts[1]),
                decodeSet(parts[2]),
                decodeSet(parts[3]),
                decodeSet(parts[4]),
                decodeSet(parts[5]),
                Integer.parseInt(parts[6]),
                Long.parseLong(parts[7])
            );
            lessons.put(lesson.repairKind(), lesson);
        }
        loadTemplates();
    }

    private void loadTemplates() throws IOException {
        if (!Files.exists(templateFile)) {
            return;
        }
        for (String line : Files.readAllLines(templateFile, StandardCharsets.UTF_8)) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String[] parts = line.split("\t", -1);
            if (parts.length < 7) {
                continue;
            }
            RepairTemplate template = new RepairTemplate(
                decode(parts[0]),
                decode(parts[1]),
                decodeMap(parts[2]),
                decodeSet(parts[3]),
                decode(parts[4]),
                Integer.parseInt(parts[5]),
                Long.parseLong(parts[6])
            );
            templates.put(template.templateKey(), template);
        }
    }

    private void persist() throws IOException {
        List<String> lines = new ArrayList<>();
        lessons.values().stream()
            .sorted(Comparator.comparing(RepairLesson::repairKind))
            .forEach(lesson -> lines.add(String.join("\t",
                encode(lesson.repairKind()),
                encode(lesson.sampleTitle()),
                encodeSet(lesson.evidenceTags()),
                encodeSet(lesson.goalTokens()),
                encodeSet(lesson.targetTokens()),
                encodeSet(lesson.diagnosticTokens()),
                Integer.toString(lesson.successCount()),
                Long.toString(lesson.lastAppliedAt())
            )));
        writeAtomically(lessonFile, lines);

        List<String> templateLines = new ArrayList<>();
        templates.values().stream()
            .sorted(Comparator.comparing(RepairTemplate::templateKey))
            .forEach(template -> templateLines.add(String.join("\t",
                encode(template.templateKey()),
                encode(template.repairKind()),
                encodeMap(template.attributes()),
                encodeSet(template.contextTokens()),
                encode(template.sampleTitle()),
                Integer.toString(template.successCount()),
                Long.toString(template.lastAppliedAt())
            )));
        writeAtomically(templateFile, templateLines);
    }

    private void writeAtomically(Path target, List<String> lines) throws IOException {
        Path temp = stateRoot.resolve(target.getFileName().toString() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Set<String> extractTargetTokens(MutationProposal proposal) {
        Set<String> tokens = tokenize(proposal.getTitle() + " " + proposal.getCandidate().getExpectedGain());
        for (Path targetPath : proposal.getTargetPaths()) {
            tokens.addAll(tokenize(targetPath.toString()));
            if (targetPath.getFileName() != null) {
                tokens.addAll(tokenize(targetPath.getFileName().toString().replace(".java", "")));
            }
        }
        return limitTokens(tokens, 12);
    }

    private Set<String> extractProposalTokens(MutationProposal proposal) {
        Set<String> tokens = extractTargetTokens(proposal);
        for (String evidenceTag : proposal.getEvidenceTags()) {
            tokens.addAll(tokenize(evidenceTag));
        }
        return tokens;
    }

    private Set<String> extractDiagnosticTokens(VerifierResult baselineVerifierResult, MutationProposal proposal) {
        Set<String> tokens = new LinkedHashSet<>();
        if (baselineVerifierResult != null) {
            baselineVerifierResult.getErrorDiagnostics().stream().limit(6).forEach(diagnostic -> {
                tokens.addAll(tokenize(diagnostic.getMessage()));
                tokens.addAll(tokenize(diagnostic.getRaw()));
            });
            baselineVerifierResult.getFailedChecks().forEach(check -> tokens.addAll(tokenize(check)));
        }
        tokens.addAll(tokenize(proposal.getTitle()));
        return limitTokens(tokens, 14);
    }

    private int overlapCount(Set<String> left, Set<String> right) {
        int overlap = 0;
        for (String token : left) {
            if (right.contains(token)) {
                overlap++;
            }
        }
        return overlap;
    }

    private Set<String> tokenize(String text) {
        Set<String> tokens = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        for (String token : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (token.isBlank() || token.length() < 3 || STOPWORDS.contains(token)) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    private Set<String> limitTokens(Set<String> tokens, int maxTokens) {
        Set<String> limited = new LinkedHashSet<>();
        for (String token : tokens) {
            limited.add(token);
            if (limited.size() >= maxTokens) {
                break;
            }
        }
        return limited;
    }

    private String resolveRepairKind(List<String> evidenceTags) {
        if (evidenceTags == null || evidenceTags.isEmpty()) {
            return "";
        }
        for (String repairKind : REPAIR_KIND_PRIORITY) {
            if (evidenceTags.contains(repairKind)) {
                return repairKind;
            }
        }
        return evidenceTags.stream()
            .filter(tag -> tag != null && !tag.isBlank())
            .filter(tag -> !"verifier.failure".equals(tag))
            .filter(tag -> !"verifier.diagnostic".equals(tag))
            .sorted(Comparator.comparingInt(String::length).reversed())
            .findFirst()
            .orElseGet(() -> evidenceTags.get(0));
    }

    private String slugify(String input) {
        String lowered = input == null ? "" : input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String compact = lowered.replaceAll("^-+", "").replaceAll("-+$", "");
        return compact.isBlank() ? "repair" : compact;
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private String encodeSet(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values == null ? Set.<String>of() : values) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(encode(value));
        }
        return builder.toString();
    }

    private Set<String> decodeSet(String serialized) {
        Set<String> values = new LinkedHashSet<>();
        if (serialized == null || serialized.isBlank()) {
            return values;
        }
        for (String entry : serialized.split(";")) {
            if (!entry.isBlank()) {
                values.add(decode(entry));
            }
        }
        return values;
    }

    private String encodeMap(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : values == null ? Map.<String, String>of().entrySet() : values.entrySet()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(encode(entry.getKey())).append(':').append(encode(entry.getValue()));
        }
        return builder.toString();
    }

    private Map<String, String> decodeMap(String serialized) {
        Map<String, String> values = new LinkedHashMap<>();
        if (serialized == null || serialized.isBlank()) {
            return values;
        }
        for (String entry : serialized.split(";")) {
            if (entry.isBlank()) {
                continue;
            }
            int separator = entry.indexOf(':');
            if (separator <= 0) {
                continue;
            }
            values.put(
                decode(entry.substring(0, separator)),
                decode(entry.substring(separator + 1))
            );
        }
        return values;
    }

    private RepairTemplate deriveTemplate(
            TaskEnvelope task,
            MutationProposal proposal,
            VerifierResult baselineVerifierResult,
            String repairKind) {
        Map<String, String> attributes = switch (repairKind) {
            case "verifier.compiler.import" -> deriveImportTemplateAttributes(proposal);
            case "verifier.compiler.signature" -> deriveSignatureTemplateAttributes(proposal);
            case "verifier.compiler.package" -> derivePackageTemplateAttributes(proposal);
            case "verifier.compiler.type_synthesis" -> deriveTypeSynthesisTemplateAttributes(proposal);
            case "verifier.compiler.multi_type_synthesis" -> deriveMultiTypeSynthesisTemplateAttributes(proposal);
            case "verifier.compiler.method_missing" -> deriveMethodTemplateAttributes(proposal);
            case "verifier.compiler.field_missing" -> deriveFieldTemplateAttributes(proposal);
            default -> Map.of();
        };
        if (attributes.isEmpty()) {
            return null;
        }

        Set<String> contextTokens = new LinkedHashSet<>();
        contextTokens.addAll(limitTokens(tokenize(task.getGoal() + " " + String.join(" ", task.getConstraints())), 12));
        contextTokens.addAll(extractTargetTokens(proposal));
        contextTokens.addAll(extractDiagnosticTokens(baselineVerifierResult, proposal));
        attributes.values().forEach(value -> contextTokens.addAll(tokenize(value)));
        Set<String> limitedContext = limitTokens(contextTokens, 18);
        return new RepairTemplate(
            buildTemplateKey(repairKind, attributes),
            repairKind,
            attributes,
            limitedContext,
            proposal.getTitle(),
            1,
            System.currentTimeMillis()
        );
    }

    private Map<String, String> deriveImportTemplateAttributes(MutationProposal proposal) {
        String fqcn = extractNamedCapture(IMPORT_GAIN_PATTERN, proposal.getCandidate().getExpectedGain());
        if (fqcn == null || fqcn.isBlank()) {
            fqcn = extractInsertedImport(proposal);
        }
        if (fqcn == null || fqcn.isBlank() || !fqcn.contains(".")) {
            return Map.of();
        }

        int separator = fqcn.lastIndexOf('.');
        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("importPackage", fqcn.substring(0, separator));
        attributes.put("topLevelSegment", fqcn.substring(0, fqcn.indexOf('.')));
        return attributes;
    }

    private Map<String, String> deriveMethodTemplateAttributes(MutationProposal proposal) {
        String methodName = extractNamedCapture(METHOD_TITLE_PATTERN, proposal.getTitle());
        if (methodName == null || methodName.isBlank()) {
            return Map.of();
        }
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }
            java.util.regex.Pattern methodPattern = java.util.regex.Pattern.compile(
                "(?m)^\\s*private\\s+(static\\s+)?([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+"
                    + java.util.regex.Pattern.quote(methodName)
                    + "\\s*\\(([^)]*)\\)"
            );
            java.util.regex.Matcher matcher = methodPattern.matcher(content);
            if (!matcher.find()) {
                continue;
            }
            String parameterSummary = matcher.group(3) == null ? "" : matcher.group(3).trim();
            int parameterCount = parameterSummary.isBlank() ? 0 : parameterSummary.split(",").length;
            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("returnType", matcher.group(2).trim());
            attributes.put("parameterCount", Integer.toString(parameterCount));
            attributes.put("static", Boolean.toString(matcher.group(1) != null && !matcher.group(1).isBlank()));
            return attributes;
        }
        return Map.of();
    }

    private Map<String, String> deriveSignatureTemplateAttributes(MutationProposal proposal) {
        String methodName = extractNamedCapture(SIGNATURE_GAIN_PATTERN, proposal.getCandidate().getExpectedGain());
        if (methodName == null || methodName.isBlank()) {
            return Map.of();
        }
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }
            java.util.regex.Pattern invocationPattern = java.util.regex.Pattern.compile(
                "\\b" + java.util.regex.Pattern.quote(methodName) + "\\s*\\(([^)]*)\\)"
            );
            java.util.regex.Matcher matcher = invocationPattern.matcher(content);
            String argumentSummary = null;
            while (matcher.find()) {
                String candidate = matcher.group(1) == null ? "" : matcher.group(1).trim();
                if (!candidate.isBlank()) {
                    argumentSummary = candidate;
                    break;
                }
            }
            if (argumentSummary == null) {
                continue;
            }
            Map<String, String> attributes = new LinkedHashMap<>();
            List<String> arguments = splitArguments(argumentSummary);
            attributes.put("argumentCount", Integer.toString(arguments.size()));
            attributes.put("defaultArgs", String.join("|", arguments));
            return attributes;
        }
        return Map.of();
    }

    private Map<String, String> deriveFieldTemplateAttributes(MutationProposal proposal) {
        String fieldName = extractNamedCapture(FIELD_TITLE_PATTERN, proposal.getTitle());
        if (fieldName == null || fieldName.isBlank()) {
            return Map.of();
        }
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }
            java.util.regex.Pattern fieldPattern = java.util.regex.Pattern.compile(
                "(?m)^\\s*private\\s+(static\\s+)?([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+"
                    + java.util.regex.Pattern.quote(fieldName)
                    + "\\s*(?:=.*)?;"
            );
            java.util.regex.Matcher matcher = fieldPattern.matcher(content);
            if (!matcher.find()) {
                continue;
            }
            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("fieldType", matcher.group(2).trim());
            attributes.put("static", Boolean.toString(matcher.group(1) != null && !matcher.group(1).isBlank()));
            return attributes;
        }
        return Map.of();
    }

    private Map<String, String> derivePackageTemplateAttributes(MutationProposal proposal) {
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([A-Za-z_$][\\w$.]*)\\s*;"
            ).matcher(content);
            if (!matcher.find()) {
                continue;
            }
            String expectedPackage = matcher.group(1).trim();
            if (expectedPackage.isBlank()) {
                continue;
            }
            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("expectedPackage", expectedPackage);
            attributes.put("packageDepth", Integer.toString(expectedPackage.split("\\.").length));
            attributes.put("topLevelSegment", expectedPackage.contains(".")
                ? expectedPackage.substring(0, expectedPackage.indexOf('.'))
                : expectedPackage);
            return attributes;
        }
        return Map.of();
    }

    private Map<String, String> deriveTypeSynthesisTemplateAttributes(MutationProposal proposal) {
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }

            java.util.regex.Matcher classMatcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*public\\s+final\\s+class\\s+([A-Za-z_$][\\w$]*)\\b"
            ).matcher(content);
            if (!classMatcher.find()) {
                continue;
            }

            java.util.regex.Matcher packageMatcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([A-Za-z_$][\\w$.]*)\\s*;"
            ).matcher(content);
            String packageName = packageMatcher.find() ? packageMatcher.group(1).trim() : "";
            int constructorCount = countMatches(content, "(?m)^\\s*public\\s+" + java.util.regex.Pattern.quote(classMatcher.group(1)) + "\\s*\\(");
            int methodCount = countMatches(content, "(?m)^\\s*public\\s+(?:static\\s+)?[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+[A-Za-z_$][\\w$]*\\s*\\(");
            int fieldCount = countMatches(content, "(?m)^\\s*public\\s+(?:static\\s+)?[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+[A-Za-z_$][\\w$]*\\s*(?:=.*)?;");

            Map<String, String> attributes = new LinkedHashMap<>();
            attributes.put("classKind", "final-class");
            attributes.put("constructorCount", Integer.toString(Math.max(0, constructorCount)));
            attributes.put("methodCount", Integer.toString(Math.max(0, methodCount)));
            attributes.put("fieldCount", Integer.toString(fieldCount));
            attributes.put("packageDepth", packageName.isBlank() ? "0" : Integer.toString(packageName.split("\\.").length));
            if (!packageName.isBlank()) {
                attributes.put("topLevelSegment", packageName.contains(".")
                    ? packageName.substring(0, packageName.indexOf('.'))
                    : packageName);
            }
            return attributes;
        }
        return Map.of();
    }

    private Map<String, String> deriveMultiTypeSynthesisTemplateAttributes(MutationProposal proposal) {
        List<String> contents = new ArrayList<>();
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content != null && !content.isBlank()) {
                contents.add(content);
            }
        }
        if (contents.size() < 2) {
            return Map.of();
        }

        int totalMethods = 0;
        int totalFields = 0;
        int totalConstructors = 0;
        String sharedTopLevel = null;
        int packageDepth = -1;
        Set<String> classNames = new LinkedHashSet<>();
        List<String> classContents = new ArrayList<>();
        for (String content : contents) {
            java.util.regex.Matcher packageMatcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*package\\s+([A-Za-z_$][\\w$.]*)\\s*;"
            ).matcher(content);
            String packageName = packageMatcher.find() ? packageMatcher.group(1).trim() : "";
            String topLevel = packageName.isBlank()
                ? ""
                : (packageName.contains(".") ? packageName.substring(0, packageName.indexOf('.')) : packageName);
            if (sharedTopLevel == null) {
                sharedTopLevel = topLevel;
            } else if (!sharedTopLevel.equals(topLevel)) {
                sharedTopLevel = "";
            }
            int currentDepth = packageName.isBlank() ? 0 : packageName.split("\\.").length;
            packageDepth = packageDepth < 0 ? currentDepth : Math.max(packageDepth, currentDepth);

            java.util.regex.Matcher classMatcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*public\\s+final\\s+class\\s+([A-Za-z_$][\\w$]*)\\b"
            ).matcher(content);
            if (!classMatcher.find()) {
                continue;
            }
            String className = classMatcher.group(1);
            classNames.add(className);
            classContents.add(content);
            totalConstructors += countMatches(content, "(?m)^\\s*public\\s+" + java.util.regex.Pattern.quote(className) + "\\s*\\(");
            totalMethods += countMatches(content, "(?m)^\\s*public\\s+(?:static\\s+)?[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+[A-Za-z_$][\\w$]*\\s*\\(");
            totalFields += countMatches(content, "(?m)^\\s*public\\s+(?:static\\s+)?[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+[A-Za-z_$][\\w$]*\\s*(?:=.*)?;");
        }

        int dependencyEdgeCount = 0;
        for (String content : classContents) {
            dependencyEdgeCount += countCollaboratorReferences(content, classNames);
        }

        Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("componentCount", Integer.toString(contents.size()));
        attributes.put("totalConstructorCount", Integer.toString(Math.max(0, totalConstructors)));
        attributes.put("totalMethodCount", Integer.toString(Math.max(0, totalMethods)));
        attributes.put("totalFieldCount", Integer.toString(Math.max(0, totalFields)));
        attributes.put("dependencyEdgeCount", Integer.toString(Math.max(0, dependencyEdgeCount)));
        attributes.put("packageDepth", Integer.toString(Math.max(0, packageDepth)));
        if (sharedTopLevel != null && !sharedTopLevel.isBlank()) {
            attributes.put("topLevelSegment", sharedTopLevel);
        }
        return attributes;
    }

    private int countCollaboratorReferences(String content, Set<String> classNames) {
        if (content == null || content.isBlank() || classNames == null || classNames.size() < 2) {
            return 0;
        }

        java.util.regex.Matcher classMatcher = java.util.regex.Pattern.compile(
            "(?m)^\\s*public\\s+final\\s+class\\s+([A-Za-z_$][\\w$]*)\\b"
        ).matcher(content);
        String owner = classMatcher.find() ? classMatcher.group(1) : null;
        if (owner == null || owner.isBlank()) {
            return 0;
        }

        int edges = 0;
        java.util.regex.Matcher methodMatcher = java.util.regex.Pattern.compile(
            "(?m)^\\s*public\\s+(?:static\\s+)?([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+[A-Za-z_$][\\w$]*\\s*\\("
        ).matcher(content);
        while (methodMatcher.find()) {
            String simpleType = simpleTypeName(methodMatcher.group(1));
            if (!simpleType.equals(owner) && classNames.contains(simpleType)) {
                edges++;
            }
        }

        java.util.regex.Matcher fieldMatcher = java.util.regex.Pattern.compile(
            "(?m)^\\s*public\\s+(?:static\\s+)?([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+[A-Za-z_$][\\w$]*\\s*(?:=.*)?;"
        ).matcher(content);
        while (fieldMatcher.find()) {
            String simpleType = simpleTypeName(fieldMatcher.group(1));
            if (!simpleType.equals(owner) && classNames.contains(simpleType)) {
                edges++;
            }
        }
        return edges;
    }

    private int countMatches(String content, String regex) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private String simpleTypeName(String type) {
        String simple = type == null ? "" : type.trim();
        int generic = simple.indexOf('<');
        if (generic >= 0) {
            simple = simple.substring(0, generic);
        }
        int dot = simple.lastIndexOf('.');
        return dot >= 0 ? simple.substring(dot + 1) : simple;
    }

    private String extractNamedCapture(java.util.regex.Pattern pattern, String input) {
        if (pattern == null || input == null) {
            return null;
        }
        java.util.regex.Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractInsertedImport(MutationProposal proposal) {
        for (MutationOperation operation : proposal.getCandidate().getOperations()) {
            String content = operation.getContent();
            if (content == null || content.isBlank()) {
                continue;
            }
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(
                "(?m)^\\s*import\\s+([A-Za-z_$][\\w$.]*)\\s*;"
            ).matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return null;
    }

    private List<String> splitArguments(String argumentSummary) {
        if (argumentSummary == null || argumentSummary.isBlank()) {
            return List.of();
        }
        List<String> arguments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int genericDepth = 0;
        int arrayDepth = 0;
        for (char c : argumentSummary.toCharArray()) {
            if (c == '<') {
                genericDepth++;
            } else if (c == '>') {
                genericDepth = Math.max(0, genericDepth - 1);
            } else if (c == '(' || c == '[' || c == '{') {
                arrayDepth++;
            } else if (c == ')' || c == ']' || c == '}') {
                arrayDepth = Math.max(0, arrayDepth - 1);
            }
            if (c == ',' && genericDepth == 0 && arrayDepth == 0) {
                String token = current.toString().trim();
                if (!token.isBlank()) {
                    arguments.add(token);
                }
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        String token = current.toString().trim();
        if (!token.isBlank()) {
            arguments.add(token);
        }
        return arguments;
    }

    private String buildTemplateKey(String repairKind, Map<String, String> attributes) {
        StringBuilder builder = new StringBuilder(repairKind == null ? "" : repairKind);
        attributes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> builder.append('|').append(entry.getKey()).append('=').append(entry.getValue()));
        return builder.toString();
    }

    public record RepairLesson(
            String repairKind,
            String sampleTitle,
            Set<String> evidenceTags,
            Set<String> goalTokens,
            Set<String> targetTokens,
            Set<String> diagnosticTokens,
            int successCount,
            long lastAppliedAt) {
        public RepairLesson {
            evidenceTags = evidenceTags == null ? Set.of() : Set.copyOf(evidenceTags);
            goalTokens = goalTokens == null ? Set.of() : Set.copyOf(goalTokens);
            targetTokens = targetTokens == null ? Set.of() : Set.copyOf(targetTokens);
            diagnosticTokens = diagnosticTokens == null ? Set.of() : Set.copyOf(diagnosticTokens);
        }

        public RepairLesson merge(RepairLesson other) {
            Set<String> mergedEvidence = new LinkedHashSet<>(evidenceTags);
            mergedEvidence.addAll(other.evidenceTags);
            Set<String> mergedGoal = new LinkedHashSet<>(goalTokens);
            mergedGoal.addAll(other.goalTokens);
            Set<String> mergedTargets = new LinkedHashSet<>(targetTokens);
            mergedTargets.addAll(other.targetTokens);
            Set<String> mergedDiagnostics = new LinkedHashSet<>(diagnosticTokens);
            mergedDiagnostics.addAll(other.diagnosticTokens);
            return new RepairLesson(
                repairKind,
                other.sampleTitle == null || other.sampleTitle.isBlank() ? sampleTitle : other.sampleTitle,
                mergedEvidence,
                mergedGoal,
                mergedTargets,
                mergedDiagnostics,
                successCount + Math.max(1, other.successCount),
                Math.max(lastAppliedAt, other.lastAppliedAt)
            );
        }
    }

    public record RepairMatch(RepairLesson lesson, double bonus, List<String> notes) {
        public RepairMatch {
            notes = notes == null ? List.of() : List.copyOf(notes);
        }

        public static RepairMatch none() {
            return new RepairMatch(null, 0.0, List.of());
        }

        public boolean matched() {
            return lesson != null && bonus > 0.0;
        }
    }

    public record RepairTemplate(
            String templateKey,
            String repairKind,
            Map<String, String> attributes,
            Set<String> contextTokens,
            String sampleTitle,
            int successCount,
            long lastAppliedAt) {
        public RepairTemplate {
            attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
            contextTokens = contextTokens == null ? Set.of() : Set.copyOf(contextTokens);
        }

        public RepairTemplate merge(RepairTemplate other) {
            Set<String> mergedTokens = new LinkedHashSet<>(contextTokens);
            mergedTokens.addAll(other.contextTokens);
            return new RepairTemplate(
                templateKey,
                repairKind,
                attributes,
                mergedTokens,
                other.sampleTitle == null || other.sampleTitle.isBlank() ? sampleTitle : other.sampleTitle,
                successCount + Math.max(1, other.successCount),
                Math.max(lastAppliedAt, other.lastAppliedAt)
            );
        }
    }

    public record TemplateMatch(RepairTemplate template, double bonus, List<String> notes) {
        public TemplateMatch {
            notes = notes == null ? List.of() : List.copyOf(notes);
        }

        public static TemplateMatch none() {
            return new TemplateMatch(null, 0.0, List.of());
        }

        public boolean matched() {
            return template != null && bonus > 0.0;
        }
    }
}
