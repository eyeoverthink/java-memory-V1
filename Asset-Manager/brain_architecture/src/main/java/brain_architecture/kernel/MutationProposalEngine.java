package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates bounded mutation candidates from task goals and workspace state.
 */
public final class MutationProposalEngine {
    private static final Pattern MISSING_SYMBOL_PATTERN =
        Pattern.compile("(?m)^\\s*symbol:\\s*(class|interface|enum|record|variable|method)\\s+([^\\r\\n]+?)\\s*$");
    private static final Pattern LOCATION_PATTERN =
        Pattern.compile("(?m)^\\s*location:\\s*(class|interface|enum|record|variable)\\s+(.+?)\\s*$");
    private static final Pattern METHOD_SIGNATURE_PATTERN =
        Pattern.compile("method\\s+([A-Za-z_$][\\w$]*)\\s+in class\\s+([A-Za-z_$][\\w$]*)\\s+cannot be applied to given types", Pattern.CASE_INSENSITIVE);
    private static final Pattern REQUIRED_PATTERN =
        Pattern.compile("(?m)^\\s*required:\\s*(.+)$");
    private static final Pattern FOUND_PATTERN =
        Pattern.compile("(?m)^\\s*found:\\s*(.+)$");
    private static final Pattern PUBLIC_TYPE_NAME_MISMATCH_PATTERN =
        Pattern.compile("(class|interface|enum|record)\\s+([A-Za-z_$][\\w$]*)\\s+is public, should be declared in a file named\\s+([A-Za-z_$][\\w$]*\\.java)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LOCAL_ASSIGNMENT_PATTERN =
        Pattern.compile("\\b([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+[A-Za-z_$][\\w$]*\\s*=\\s*.+");
    private static final Pattern VARIABLE_LOCATION_TYPE_PATTERN =
        Pattern.compile("([A-Za-z_$][\\w$]*)\\s+of\\s+type\\s+([A-Za-z_$][\\w$.]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMPORT_STATEMENT_PATTERN =
        Pattern.compile("(?m)^\\s*import\\s+([A-Za-z_$][\\w$.]*)\\s*;\\s*$");
    private static final Set<String> KEYWORD_STOPWORDS = Set.of(
        "the", "and", "for", "that", "this", "with", "from", "into", "your", "their",
        "have", "will", "would", "there", "while", "through", "which", "about", "using",
        "system", "offline", "kernel", "brain", "fraynix", "pages", "source", "label",
        "fingerprint", "distilled", "chunk"
    );

    private final KernelConfiguration configuration;
    private final RepairMemoryStore repairMemoryStore;
    private final boolean causalPolicyEnabled;

    public MutationProposalEngine(KernelConfiguration configuration) {
        this(configuration, null);
    }

    public MutationProposalEngine(KernelConfiguration configuration, RepairMemoryStore repairMemoryStore) {
        this.configuration = configuration;
        this.repairMemoryStore = repairMemoryStore;
        this.causalPolicyEnabled = configuration != null && configuration.isCausalPolicyEnabled();
    }

    public List<MutationProposal> propose(TaskEnvelope task, WorkspaceProfile workspaceProfile, List<MemoryHit> hits) throws IOException {
        return propose(task, workspaceProfile, hits, null);
    }

    public List<MutationProposal> propose(
            TaskEnvelope task,
            WorkspaceProfile workspaceProfile,
            List<MemoryHit> hits,
            VerifierResult verifierResult) throws IOException {
        List<CorpusMutationSeed> corpusSeeds = collectCorpusSeeds(hits, task.getGoal());
        List<MutationProposal> proposals = new ArrayList<>();
        proposals.addAll(proposeSourceRepairs(task, hits, corpusSeeds, verifierResult));

        if (shouldSuppressGenericMutations(task, verifierResult)) {
            proposals = deduplicateProposals(proposals);
            proposals.sort(Comparator.comparingDouble((MutationProposal proposal) -> proposalRankingScore(proposal, hits)).reversed());
            return proposals;
        }

        proposals.addAll(proposeGeneratedTests(task, hits, corpusSeeds));

        MutationProposal bootstrapProposal = proposeOfflineBootstrap(task, workspaceProfile);
        if (bootstrapProposal != null) {
            proposals.add(bootstrapProposal);
        }

        MutationProposal corpusBriefProposal = proposeCorpusEvidenceBrief(task, corpusSeeds);
        if (corpusBriefProposal != null) {
            proposals.add(corpusBriefProposal);
        }

        proposals.add(proposeWorkspaceDigest(task, workspaceProfile, hits, corpusSeeds));
        proposals = deduplicateProposals(proposals);
        proposals.sort(Comparator.comparingDouble((MutationProposal proposal) -> proposalRankingScore(proposal, hits)).reversed());
        return proposals;
    }

    private double proposalRankingScore(MutationProposal proposal, List<MemoryHit> hits) {
        double base = proposal.getPriorityScore();
        if (!causalPolicyEnabled || hits == null || hits.isEmpty()) {
            return base;
        }

        double maxCausalGrounding = 0.0;
        for (Path targetPath : proposal.getTargetPaths()) {
            if (targetPath == null) {
                continue;
            }
            String normalizedTarget = targetPath.toString().replace('\\', '/');
            for (MemoryHit hit : hits) {
                if (hit.getDocument() == null) {
                    continue;
                }
                String sourceId = hit.getDocument().getSourceId();
                if (sourceId == null || !sourceId.equals(normalizedTarget)) {
                    continue;
                }
                double grounding = hit.isCausallyGrounded()
                    ? Math.max(0.0, hit.getCausalScore() - hit.getSpuriousPenalty())
                    : 0.0;
                maxCausalGrounding = Math.max(maxCausalGrounding, grounding);
            }
        }

        double causalBoost = Math.min(0.15, maxCausalGrounding * 0.15);
        return base + causalBoost;
    }

    private boolean shouldSuppressGenericMutations(TaskEnvelope task, VerifierResult verifierResult) {
        if (task == null || verifierResult == null) {
            return false;
        }
        String goal = task.getGoal() == null ? "" : task.getGoal().toLowerCase(Locale.ROOT);
        return goal.contains("repair")
            || goal.contains("fix")
            || goal.contains("resolve")
            || goal.contains("unresolved")
            || goal.contains("missing ")
            || goal.contains("missing class")
            || goal.contains("signature")
            || goal.contains("parser")
            || goal.contains("import")
            || goal.contains("field")
            || goal.contains("method")
            || goal.contains("package")
            || goal.contains("construct")
            || goal.contains("synthes")
            || goal.contains("failing test")
            || goal.contains("test failure")
            || goal.contains("diagnostic")
            || goal.contains("compile")
            || goal.contains("build failure");
    }

    private List<MutationProposal> proposeSourceRepairs(
            TaskEnvelope task,
            List<MemoryHit> hits,
            List<CorpusMutationSeed> corpusSeeds,
            VerifierResult verifierResult) throws IOException {
        if (verifierResult == null || verifierResult.isSuccess() || !hasCompilerFailure(verifierResult)) {
            return List.of();
        }

        List<VerifierDiagnostic> diagnostics = relevantSourceDiagnostics(verifierResult);
        String failureText = buildFailureText(verifierResult);
        List<MutationProposal> proposals = new ArrayList<>();
        for (Path sourcePath : collectCandidateJavaSources(task, hits, diagnostics)) {
            if (!Files.exists(sourcePath) || Files.isDirectory(sourcePath)) {
                continue;
            }

            Path relativePath = configuration.getWorkspaceRoot().relativize(sourcePath);
            String original = Files.readString(sourcePath, StandardCharsets.UTF_8);
            List<CorpusMutationSeed> matchingSeeds = matchSeedsToSource(sourcePath, classNameFor(sourcePath), corpusSeeds, task.getGoal());
            List<VerifierDiagnostic> fileDiagnostics = diagnosticsForFile(diagnostics, sourcePath);
            String fileFailureText = fileDiagnostics.isEmpty()
                ? failureText
                : fileDiagnostics.stream().map(VerifierDiagnostic::getMessage).collect(java.util.stream.Collectors.joining("\n")).toLowerCase(Locale.ROOT);
            String sourcePackage = extractPackageName(sourcePath);

            SanitizedSource sanitized = sanitizeJavaSource(original, fileFailureText);
            if (sanitized.changed()) {
                List<String> evidenceTags = new ArrayList<>();
                evidenceTags.add("verifier.failure");
                evidenceTags.add("verifier.compiler.sanitize");
                evidenceTags.add("verifier.diagnostic");
                evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
                proposals.add(new MutationProposal(
                    "Sanitize source surface for " + classNameFor(sourcePath),
                    "Verifier output suggests parser noise or illegal tokens in " + relativePath + ", so strip obvious non-source artifacts before re-validating.",
                    0.90,
                    0.68,
                    List.of(relativePath),
                    new MutationCandidate(
                        MutationCandidate.Scope.WHOLE_CODEBASE,
                        List.of(MutationOperation.writeFile(relativePath, sanitized.content())),
                        "Repair source by removing parser-hostile artifacts",
                        buildRepairRisks("sanitize-source", matchingSeeds)
                    ),
                    evidenceTags
                ));
            }

            MutationProposal importRepair = proposeMissingImportRepair(
                task,
                relativePath,
                sourcePath,
                sourcePackage,
                original,
                fileDiagnostics,
                matchingSeeds
            );
            if (importRepair != null) {
                proposals.add(importRepair);
            }

            proposals.addAll(proposeMissingTypeSyntheses(
                task,
                relativePath,
                sourcePath,
                sourcePackage,
                original,
                fileDiagnostics,
                matchingSeeds,
                importRepair != null
            ));

            MutationProposal signatureRepair = proposeSignatureMismatchRepair(
                task,
                relativePath,
                sourcePath,
                original,
                fileDiagnostics,
                matchingSeeds
            );
            if (signatureRepair != null) {
                proposals.add(signatureRepair);
            }

            MutationProposal missingMethodRepair = proposeMissingMethodRepair(
                task,
                relativePath,
                sourcePath,
                original,
                fileDiagnostics,
                matchingSeeds
            );
            if (missingMethodRepair != null) {
                proposals.add(missingMethodRepair);
            }

            MutationProposal missingFieldRepair = proposeMissingFieldRepair(
                task,
                relativePath,
                sourcePath,
                original,
                fileDiagnostics,
                matchingSeeds
            );
            if (missingFieldRepair != null) {
                proposals.add(missingFieldRepair);
            }

            MutationProposal typeNameRepair = proposeTypeNameAlignmentRepair(
                relativePath,
                sourcePath,
                original,
                fileDiagnostics,
                matchingSeeds
            );
            if (typeNameRepair != null) {
                proposals.add(typeNameRepair);
            }

            int braceDelta = unmatchedOpenBraces(original);
            if (braceDelta > 0 && hasParserFailure(verifierResult, fileDiagnostics)) {
                String closingTail = buildClosingBraces(braceDelta);
                List<String> evidenceTags = new ArrayList<>();
                evidenceTags.add("verifier.failure");
                evidenceTags.add("verifier.compiler.parse");
                evidenceTags.add("verifier.compiler.braces");
                evidenceTags.add("verifier.diagnostic");
                evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
                proposals.add(new MutationProposal(
                    "Close unmatched braces for " + classNameFor(sourcePath),
                    "Verifier output indicates an unterminated Java source in " + relativePath + ", so append the missing closing braces and re-run validation.",
                    0.94,
                    0.73,
                    List.of(relativePath),
                    new MutationCandidate(
                        MutationCandidate.Scope.WHOLE_CODEBASE,
                        List.of(MutationOperation.appendFile(relativePath, closingTail)),
                        "Repair parser failure by closing unmatched Java braces",
                        buildRepairRisks("append-closing-braces", matchingSeeds)
                    ),
                    evidenceTags
                ));
            }
        }

        proposals.addAll(proposePackageAlignmentRepairs(task, corpusSeeds, verifierResult));
        return proposals;
    }

    private MutationProposal proposeMissingImportRepair(
            TaskEnvelope task,
            Path relativePath,
            Path sourcePath,
            String sourcePackage,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds) throws IOException {
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            if (!diagnostic.getMessage().toLowerCase(Locale.ROOT).contains("cannot find symbol")) {
                continue;
            }
            ParsedSymbol parsedSymbol = parseMissingSymbol(diagnostic);
            if (parsedSymbol == null || !parsedSymbol.isTypeLike()) {
                parsedSymbol = inferTypeLikeMissingSymbolFromSource(original, diagnostic);
            }
            if (parsedSymbol == null || !parsedSymbol.isTypeLike()) {
                continue;
            }

            TypeReference importTarget = findUniqueTypeCandidate(parsedSymbol.name(), sourcePath);
            if (importTarget == null) {
                continue;
            }
            if (hasPackageAlignmentMismatch(importTarget.path(), importTarget.packageName())) {
                continue;
            }
            if (importTarget.packageName().isBlank() || importTarget.packageName().equals(sourcePackage)) {
                continue;
            }

            String fqcn = importTarget.packageName() + "." + importTarget.simpleName();
            if (original.contains("import " + fqcn + ";")) {
                continue;
            }
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.import",
                relativePath,
                parsedSymbol.name() + " " + fqcn,
                diagnostic
            );

            String updated = insertImportStatement(original, fqcn);
            if (updated.equals(original)) {
                continue;
            }

            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.symbol");
            evidenceTags.add("verifier.compiler.import");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.import");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            return new MutationProposal(
                "Add missing import for " + parsedSymbol.name() + " in " + classNameFor(sourcePath),
                templateMatch.matched()
                    ? "Verifier diagnostics show an unresolved type in " + relativePath + ", and the workspace has a unique matching declaration for " + fqcn + ". Reuse the successful import template while applying the bounded import fix."
                    : "Verifier diagnostics show an unresolved type in " + relativePath + ", and the workspace has a unique matching declaration for " + fqcn + ".",
                templateMatch.matched() ? 0.99 : 0.97,
                0.64,
                List.of(relativePath),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(relativePath, updated)),
                    "Repair unresolved symbol by importing " + fqcn,
                    buildRepairRisks("add-missing-import", matchingSeeds)
                ),
                evidenceTags
            );
        }
        return null;
    }

    private ParsedSymbol inferTypeLikeMissingSymbolFromSource(String original, VerifierDiagnostic diagnostic) {
        if (original == null || diagnostic == null || diagnostic.getLine() == null || diagnostic.getLine() <= 0) {
            return null;
        }
        String[] lines = original.split("\\R", -1);
        if (diagnostic.getLine() > lines.length) {
            return null;
        }
        String sourceLine = lines[diagnostic.getLine() - 1];
        Matcher matcher = Pattern.compile("\\b[A-Z][A-Za-z0-9_$]*\\b").matcher(sourceLine);
        String bestToken = null;
        int bestDistance = Integer.MAX_VALUE;
        int column = diagnostic.getColumn() == null ? -1 : diagnostic.getColumn();
        while (matcher.find()) {
            String token = matcher.group();
            int distance;
            if (column <= 0) {
                distance = bestToken == null ? 0 : 1;
            } else {
                int start = matcher.start() + 1;
                int end = matcher.end();
                if (start <= column && column <= end) {
                    distance = 0;
                } else {
                    distance = Math.min(Math.abs(column - start), Math.abs(column - end));
                }
            }
            if (distance < bestDistance) {
                bestDistance = distance;
                bestToken = token;
            }
        }
        if (bestToken == null || bestToken.isBlank()) {
            return null;
        }
        return new ParsedSymbol("class", bestToken, bestToken, 0, "", "");
    }

    private List<MutationProposal> proposeMissingTypeSyntheses(
            TaskEnvelope task,
            Path relativePath,
            Path sourcePath,
            String sourcePackage,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds,
            boolean importRepairAvailable) throws IOException {
        if (importRepairAvailable) {
            return List.of();
        }

        List<MutationProposal> proposals = new ArrayList<>();
        List<SourceContext> contexts = List.of(new SourceContext(sourcePath, original));
        List<TypeSynthesisRequest> requests = new ArrayList<>();
        Set<String> seenTargets = new LinkedHashSet<>();
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            if (!diagnostic.getMessage().toLowerCase(Locale.ROOT).contains("cannot find symbol")) {
                continue;
            }

            ParsedSymbol parsedSymbol = parseMissingSymbol(diagnostic);
            if (parsedSymbol == null || !parsedSymbol.isTypeLike()) {
                parsedSymbol = inferTypeLikeMissingSymbolFromSource(original, diagnostic);
            }
            if (parsedSymbol == null || !parsedSymbol.isTypeLike()) {
                continue;
            }

            if (hasAnyTypeCandidate(parsedSymbol.name(), sourcePath)) {
                continue;
            }

            String targetPackage = inferMissingTypePackageName(original, sourcePackage, parsedSymbol.name());
            Path targetRelativePath = deriveImplementationTargetPath(sourcePath, targetPackage, parsedSymbol.name());
            Path targetSourcePath = configuration.getWorkspaceRoot().resolve(targetRelativePath).normalize();
            if (Files.exists(targetSourcePath)) {
                continue;
            }
            if (!seenTargets.add(normalizePathString(targetRelativePath))) {
                continue;
            }

            TypeSynthesisPlan plan = planMissingTypeSynthesis(targetRelativePath, targetPackage, parsedSymbol.name(), contexts);
            String fqcn = plan.packageName().isBlank() ? plan.simpleName() : plan.packageName() + "." + plan.simpleName();
            requests.add(new TypeSynthesisRequest(diagnostic, plan, fqcn));
        }

        MutationProposal collaboratorSet = proposeCollaboratorTypeSynthesis(task, relativePath, requests, matchingSeeds);
        if (collaboratorSet != null) {
            proposals.add(collaboratorSet);
        }

        for (TypeSynthesisRequest request : requests) {
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.type_synthesis",
                request.plan().relativeTargetPath(),
                request.plan().packageName() + " " + request.plan().simpleName(),
                request.diagnostic()
            );
            String synthesized = buildTypeSynthesisSource(request.plan());
            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.symbol");
            evidenceTags.add("verifier.compiler.type_synthesis");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.type_synthesis");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));

            proposals.add(new MutationProposal(
                "Synthesize missing type " + request.plan().simpleName() + " for " + classNameFor(sourcePath),
                templateMatch.matched()
                    ? "Verifier diagnostics show an unresolved type in " + relativePath + ". Build a bounded source skeleton for " + request.fqcn() + " and reuse the successful construction template to satisfy the local usage shape."
                    : "Verifier diagnostics show an unresolved type in " + relativePath + ". Build a bounded source skeleton for " + request.fqcn() + " using only the observed local usage shape.",
                templateMatch.matched() ? 0.98 : 0.95,
                0.74,
                List.of(request.plan().relativeTargetPath()),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(request.plan().relativeTargetPath(), synthesized)),
                    "Synthesize missing type " + request.fqcn() + " from bounded local usage context",
                    buildRepairRisks("synthesize-missing-type", matchingSeeds)
                ),
                evidenceTags
            ));
        }
        return proposals;
    }

    private MutationProposal proposeCollaboratorTypeSynthesis(
            TaskEnvelope task,
            Path relativePath,
            List<TypeSynthesisRequest> requests,
            List<CorpusMutationSeed> matchingSeeds) {
        if (requests == null || requests.size() < 2) {
            return null;
        }

        Map<String, List<TypeSynthesisRequest>> byPackage = new LinkedHashMap<>();
        for (TypeSynthesisRequest request : requests) {
            String packageName = request.plan().packageName() == null ? "" : request.plan().packageName();
            byPackage.computeIfAbsent(packageName, ignored -> new ArrayList<>()).add(request);
        }

        List<TypeSynthesisRequest> selected = byPackage.values().stream()
            .filter(group -> group.size() >= 2)
            .map(group -> group.stream()
                .sorted(Comparator.comparing(candidate -> candidate.plan().simpleName()))
                .limit(3)
                .toList())
            .max(Comparator.comparingInt(List::size))
            .orElse(List.of());
        if (selected.size() < 2) {
            return null;
        }

        Map<String, TypeSynthesisPlan> selectedPlans = new LinkedHashMap<>();
        for (TypeSynthesisRequest request : selected) {
            selectedPlans.put(request.plan().simpleName(), request.plan());
        }

        int dependencyEdges = countCollaboratorDependencyEdges(selectedPlans);
        boolean dependentGraph = dependencyEdges > 0;
        List<MutationOperation> operations = new ArrayList<>();
        List<Path> targets = new ArrayList<>();
        List<String> simpleNames = new ArrayList<>();
        for (TypeSynthesisRequest request : selected) {
            operations.add(MutationOperation.writeFile(request.plan().relativeTargetPath(), buildTypeSynthesisSource(request.plan(), selectedPlans)));
            targets.add(request.plan().relativeTargetPath());
            simpleNames.add(request.plan().simpleName());
        }
        String joinedNames = String.join(", ", simpleNames);
        String packageName = selected.get(0).plan().packageName();

        RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
            task,
            "verifier.compiler.multi_type_synthesis",
            targets.get(0),
            packageName + " " + joinedNames,
            selected.get(0).diagnostic()
        );

        List<String> evidenceTags = new ArrayList<>();
        evidenceTags.add("verifier.failure");
        evidenceTags.add("verifier.compiler.symbol");
        evidenceTags.add("verifier.compiler.type_synthesis");
        evidenceTags.add("verifier.compiler.multi_type_synthesis");
        if (dependentGraph) {
            evidenceTags.add("verifier.compiler.dependent_type_synthesis");
        }
        evidenceTags.add("verifier.diagnostic");
        if (templateMatch.matched()) {
            evidenceTags.add("repair.template");
            evidenceTags.add("repair.template.multi_type_synthesis");
        }
        evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));

        return new MutationProposal(
            (dependentGraph ? "Synthesize dependent collaborator set " : "Synthesize collaborator set ") + joinedNames + " for " + classNameFor(relativePath),
            templateMatch.matched()
                ? dependentGraph
                    ? "Verifier diagnostics show a dependent unresolved collaborator chain in " + relativePath + ". Build bounded source skeletons for " + joinedNames + " together, preserve the local dependency edges, and reuse the successful collaborator construction template."
                    : "Verifier diagnostics show a small unresolved collaborator set in " + relativePath + ". Build bounded source skeletons for " + joinedNames + " together and reuse the successful collaborator construction template."
                : dependentGraph
                    ? "Verifier diagnostics show a dependent unresolved collaborator chain in " + relativePath + ". Build bounded source skeletons for " + joinedNames + " together and preserve the local dependency edges so the capability can validate in one promotion."
                    : "Verifier diagnostics show a small unresolved collaborator set in " + relativePath + ". Build bounded source skeletons for " + joinedNames + " together so the local capability can validate in one promotion.",
            templateMatch.matched() ? (dependentGraph ? 1.005 : 0.995) : (dependentGraph ? 0.995 : 0.985),
            0.79,
            targets,
            new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                operations,
                dependentGraph
                    ? "Synthesize dependent collaborator set " + joinedNames + " from bounded local usage context"
                    : "Synthesize collaborator set " + joinedNames + " from bounded local usage context",
                buildRepairRisks("synthesize-collaborator-set", matchingSeeds)
            ),
            evidenceTags
        );
    }

    private boolean hasAnyTypeCandidate(String simpleName, Path sourcePath) throws IOException {
        return !findTypeCandidates(simpleName, sourcePath).isEmpty();
    }

    private TypeSynthesisPlan planMissingTypeSynthesis(
            Path targetRelativePath,
            String packageName,
            String simpleName,
            List<SourceContext> contexts) {
        Map<String, ConstructorPlan> constructors = new LinkedHashMap<>();
        Map<String, SynthesizedMethodPlan> methods = new LinkedHashMap<>();
        Map<String, SynthesizedFieldPlan> fields = new LinkedHashMap<>();

        for (SourceContext context : contexts) {
            collectTypeUsageFromContext(simpleName, context, constructors, methods, fields);
        }

        if (constructors.isEmpty()) {
            constructors.put("ctor:0", new ConstructorPlan(0));
        }

        return new TypeSynthesisPlan(
            packageName == null ? "" : packageName,
            simpleName,
            targetRelativePath,
            new ArrayList<>(constructors.values()),
            new ArrayList<>(methods.values()),
            new ArrayList<>(fields.values())
        );
    }

    private void collectTypeUsageFromContext(
            String simpleName,
            SourceContext context,
            Map<String, ConstructorPlan> constructors,
            Map<String, SynthesizedMethodPlan> methods,
            Map<String, SynthesizedFieldPlan> fields) {
        String[] lines = context.content().split("\\R", -1);
        Set<String> aliases = new LinkedHashSet<>();
        Pattern aliasPattern = Pattern.compile("\\b" + Pattern.quote(simpleName) + "\\s+([A-Za-z_$][\\w$]*)\\b");
        Pattern constructorPattern = Pattern.compile("\\bnew\\s+(?:[A-Za-z_$][\\w$]*\\.)*" + Pattern.quote(simpleName) + "\\s*\\(([^)]*)\\)");
        Pattern staticMethodPattern = Pattern.compile("\\b(?:[A-Za-z_$][\\w$]*\\.)*" + Pattern.quote(simpleName) + "\\.([A-Za-z_$][\\w$]*)\\s*\\(([^)]*)\\)");
        Pattern staticFieldPattern = Pattern.compile("\\b(?:[A-Za-z_$][\\w$]*\\.)*" + Pattern.quote(simpleName) + "\\.([A-Za-z_$][\\w$]*)\\b(?!\\s*\\()");

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            String trimmed = line.trim();
            if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
                continue;
            }

            Matcher aliasMatcher = aliasPattern.matcher(line);
            while (aliasMatcher.find()) {
                aliases.add(aliasMatcher.group(1));
            }

            Matcher constructorMatcher = constructorPattern.matcher(line);
            while (constructorMatcher.find()) {
                List<String> arguments = splitCallArguments(constructorMatcher.group(1));
                constructors.put("ctor:" + arguments.size(), new ConstructorPlan(arguments.size()));
            }

            Matcher staticMethodMatcher = staticMethodPattern.matcher(line);
            while (staticMethodMatcher.find()) {
                addSynthesizedMethod(methods, staticMethodMatcher.group(1), splitCallArguments(staticMethodMatcher.group(2)).size(),
                    inferExpressionType(context.content(), index + 1, staticMethodMatcher.group(1), true), true);
            }

            Matcher staticFieldMatcher = staticFieldPattern.matcher(line);
            while (staticFieldMatcher.find()) {
                addSynthesizedField(fields, staticFieldMatcher.group(1),
                    inferExpressionType(context.content(), index + 1, staticFieldMatcher.group(1), false), true);
            }
        }

        for (String alias : aliases) {
            Pattern instanceMethodPattern = Pattern.compile("\\b" + Pattern.quote(alias) + "\\.([A-Za-z_$][\\w$]*)\\s*\\(([^)]*)\\)");
            Pattern instanceFieldPattern = Pattern.compile("\\b" + Pattern.quote(alias) + "\\.([A-Za-z_$][\\w$]*)\\b(?!\\s*\\()");
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                Matcher instanceMethodMatcher = instanceMethodPattern.matcher(line);
                while (instanceMethodMatcher.find()) {
                    addSynthesizedMethod(methods, instanceMethodMatcher.group(1), splitCallArguments(instanceMethodMatcher.group(2)).size(),
                        inferExpressionType(context.content(), index + 1, instanceMethodMatcher.group(1), true), false);
                }

                Matcher instanceFieldMatcher = instanceFieldPattern.matcher(line);
                while (instanceFieldMatcher.find()) {
                    addSynthesizedField(fields, instanceFieldMatcher.group(1),
                        inferExpressionType(context.content(), index + 1, instanceFieldMatcher.group(1), false), false);
                }
            }
        }
    }

    private void addSynthesizedMethod(
            Map<String, SynthesizedMethodPlan> methods,
            String methodName,
            int parameterCount,
            String returnType,
            boolean staticMethod) {
        if (methodName == null || methodName.isBlank() || isReservedObjectMethod(methodName)) {
            return;
        }
        String normalizedType = normalizeJavaType(returnType);
        String key = methodName + ":" + parameterCount + ":" + staticMethod;
        SynthesizedMethodPlan existing = methods.get(key);
        if (existing == null) {
            methods.put(key, new SynthesizedMethodPlan(methodName, parameterCount, normalizedType, staticMethod));
            return;
        }
        if ("Object".equals(existing.returnType()) && !"Object".equals(normalizedType)) {
            methods.put(key, new SynthesizedMethodPlan(methodName, parameterCount, normalizedType, staticMethod));
        }
    }

    private void addSynthesizedField(
            Map<String, SynthesizedFieldPlan> fields,
            String fieldName,
            String fieldType,
            boolean staticField) {
        if (fieldName == null || fieldName.isBlank() || "class".equals(fieldName)) {
            return;
        }
        String normalizedType = normalizeJavaType(fieldType);
        String key = fieldName + ":" + staticField;
        SynthesizedFieldPlan existing = fields.get(key);
        if (existing == null) {
            fields.put(key, new SynthesizedFieldPlan(fieldName, normalizedType, staticField));
            return;
        }
        if ("Object".equals(existing.type()) && !"Object".equals(normalizedType)) {
            fields.put(key, new SynthesizedFieldPlan(fieldName, normalizedType, staticField));
        }
    }

    private boolean isReservedObjectMethod(String methodName) {
        return switch (methodName) {
            case "getClass", "hashCode", "equals", "toString", "notify", "notifyAll", "wait" -> true;
            default -> false;
        };
    }

    private List<String> splitCallArguments(String rawArguments) {
        if (rawArguments == null || rawArguments.isBlank()) {
            return List.of();
        }
        List<String> arguments = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int genericDepth = 0;
        int nestedDepth = 0;
        boolean inString = false;
        for (int i = 0; i < rawArguments.length(); i++) {
            char c = rawArguments.charAt(i);
            if (c == '"' && (i == 0 || rawArguments.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString) {
                if (c == '<') {
                    genericDepth++;
                } else if (c == '>') {
                    genericDepth = Math.max(0, genericDepth - 1);
                } else if (c == '(' || c == '[' || c == '{') {
                    nestedDepth++;
                } else if (c == ')' || c == ']' || c == '}') {
                    nestedDepth = Math.max(0, nestedDepth - 1);
                } else if (c == ',' && genericDepth == 0 && nestedDepth == 0) {
                    String token = current.toString().trim();
                    if (!token.isBlank()) {
                        arguments.add(token);
                    }
                    current.setLength(0);
                    continue;
                }
            }
            current.append(c);
        }
        String token = current.toString().trim();
        if (!token.isBlank()) {
            arguments.add(token);
        }
        return arguments;
    }

    private String inferMissingTypePackageName(String original, String defaultPackage, String simpleName) {
        Matcher importMatcher = IMPORT_STATEMENT_PATTERN.matcher(original);
        while (importMatcher.find()) {
            String importedType = importMatcher.group(1).trim();
            if (!simpleName.equals(simpleTypeName(importedType))) {
                continue;
            }
            int separator = importedType.lastIndexOf('.');
            return separator > 0 ? importedType.substring(0, separator) : "";
        }

        Matcher qualifiedUsageMatcher = Pattern.compile(
            "\\b((?:[A-Za-z_$][\\w$]*\\.)+)" + Pattern.quote(simpleName) + "\\b"
        ).matcher(original);
        if (qualifiedUsageMatcher.find()) {
            String prefix = qualifiedUsageMatcher.group(1);
            return prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
        }
        return defaultPackage == null ? "" : defaultPackage;
    }

    private Path deriveImplementationTargetPath(Path contextSourcePath, String packageName, String simpleName) {
        Path implementationRoot = deriveImplementationRoot(contextSourcePath);
        Path targetDirectory = packageName == null || packageName.isBlank()
            ? implementationRoot
            : implementationRoot.resolve(packageName.replace('.', java.io.File.separatorChar));
        Path absoluteTarget = targetDirectory.resolve(simpleName + ".java").normalize();
        return configuration.getWorkspaceRoot().relativize(absoluteTarget);
    }

    private Path deriveImplementationRoot(Path contextSourcePath) {
        Path normalizedSource = contextSourcePath == null ? null : contextSourcePath.toAbsolutePath().normalize();
        if (normalizedSource != null) {
            try {
                String relative = normalizePathString(configuration.getWorkspaceRoot().relativize(normalizedSource));
                if (relative.startsWith("live-training/src/main/java/") || relative.startsWith("live-training/src/offlineTest/java/")) {
                    return configuration.getWorkspaceRoot().resolve("live-training/src/main/java");
                }
                if (relative.startsWith("src/main/java/") || relative.startsWith("src/offlineTest/java/")) {
                    return configuration.getWorkspaceRoot().resolve("src/main/java");
                }
            } catch (IllegalArgumentException ignored) {
                // Fall through to the default source root.
            }
        }
        return configuration.getWorkspaceRoot().resolve(Path.of("src", "main", "java"));
    }

    private String buildTypeSynthesisSource(TypeSynthesisPlan plan) {
        return buildTypeSynthesisSource(plan, Map.of());
    }

    private String buildTypeSynthesisSource(
            TypeSynthesisPlan plan,
            Map<String, TypeSynthesisPlan> availablePlans) {
        String newline = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        if (plan.packageName() != null && !plan.packageName().isBlank()) {
            builder.append("package ").append(plan.packageName()).append(';').append(newline).append(newline);
        }

        builder.append("public final class ").append(plan.simpleName()).append(" {").append(newline);

        List<SynthesizedFieldPlan> fields = new ArrayList<>(plan.fields());
        fields.sort(Comparator.comparing(SynthesizedFieldPlan::name).thenComparing(SynthesizedFieldPlan::staticField));
        for (SynthesizedFieldPlan field : fields) {
            builder.append(newline)
                .append("    public ");
            if (field.staticField()) {
                builder.append("static ");
            }
            builder.append(field.type())
                .append(' ')
                .append(field.name())
                .append(" = ")
                .append(defaultLiteralForType(field.type(), availablePlans, plan.simpleName()))
                .append(';')
                .append(newline);
        }

        List<ConstructorPlan> constructors = new ArrayList<>(plan.constructors());
        constructors.sort(Comparator.comparingInt(ConstructorPlan::parameterCount));
        for (ConstructorPlan constructor : constructors) {
            builder.append(newline)
                .append("    public ")
                .append(plan.simpleName())
                .append('(');
            for (int i = 0; i < constructor.parameterCount(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append("Object arg").append(i);
            }
            builder.append(") {").append(newline)
                .append("    }").append(newline);
        }

        List<SynthesizedMethodPlan> methods = new ArrayList<>(plan.methods());
        methods.sort(Comparator
            .comparing(SynthesizedMethodPlan::name)
            .thenComparingInt(SynthesizedMethodPlan::parameterCount)
            .thenComparing(SynthesizedMethodPlan::staticMethod));
        for (SynthesizedMethodPlan method : methods) {
            builder.append(newline)
                .append("    public ");
            if (method.staticMethod()) {
                builder.append("static ");
            }
            builder.append(method.returnType())
                .append(' ')
                .append(method.name())
                .append('(');
            for (int i = 0; i < method.parameterCount(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append("Object arg").append(i);
            }
            builder.append(") {").append(newline);
            if (!"void".equals(method.returnType())) {
                builder.append("        return ")
                    .append(defaultLiteralForType(method.returnType(), availablePlans, plan.simpleName()))
                    .append(';')
                    .append(newline);
            }
            builder.append("    }").append(newline);
        }

        builder.append("}").append(newline);
        return builder.toString();
    }

    private int countCollaboratorDependencyEdges(Map<String, TypeSynthesisPlan> plans) {
        if (plans == null || plans.size() < 2) {
            return 0;
        }

        int edges = 0;
        for (TypeSynthesisPlan plan : plans.values()) {
            for (SynthesizedMethodPlan method : plan.methods()) {
                if (referencesCollaboratorType(method.returnType(), plan.simpleName(), plans)) {
                    edges++;
                }
            }
            for (SynthesizedFieldPlan field : plan.fields()) {
                if (referencesCollaboratorType(field.type(), plan.simpleName(), plans)) {
                    edges++;
                }
            }
        }
        return edges;
    }

    private boolean referencesCollaboratorType(
            String rawType,
            String ownerSimpleName,
            Map<String, TypeSynthesisPlan> plans) {
        if (rawType == null || plans == null || plans.isEmpty()) {
            return false;
        }
        String simpleType = simpleTypeName(normalizeJavaType(rawType));
        return !simpleType.isBlank()
            && !simpleType.equals(ownerSimpleName)
            && plans.containsKey(simpleType);
    }

    private MutationProposal proposeMissingMethodRepair(
            TaskEnvelope task,
            Path relativePath,
            Path sourcePath,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds) throws IOException {
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            if (!diagnostic.getMessage().toLowerCase(Locale.ROOT).contains("cannot find symbol")) {
                continue;
            }
            ParsedSymbol parsedSymbol = parseMissingSymbol(diagnostic);
            if (parsedSymbol == null || !"method".equals(parsedSymbol.kind())) {
                continue;
            }

            Path targetSourcePath = resolveSymbolTargetSource(parsedSymbol, sourcePath);
            if (targetSourcePath == null || !Files.exists(targetSourcePath) || Files.isDirectory(targetSourcePath)) {
                continue;
            }

            String targetOriginal = Files.readString(targetSourcePath, StandardCharsets.UTF_8);
            if (declaresMethodName(targetOriginal, parsedSymbol.name())) {
                continue;
            }

            String inferredType = inferExpressionType(original, diagnostic.getLine(), parsedSymbol.name(), true);
            boolean staticContext = isStaticContext(original, diagnostic.getLine());
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.method_missing",
                relativePath,
                parsedSymbol.name(),
                diagnostic
            );
            String templateReturnType = templateAttribute(templateMatch, "returnType");
            String resolvedType = "Object".equals(inferredType) && templateReturnType != null && !templateReturnType.isBlank()
                ? templateReturnType
                : inferredType;
            int parameterCount = parsedSymbol.parameterCount();
            String templateParameterCount = templateAttribute(templateMatch, "parameterCount");
            if (parameterCount <= 0 && templateParameterCount != null && templateParameterCount.matches("\\d+")) {
                parameterCount = Integer.parseInt(templateParameterCount);
            }
            String stub = buildMissingMethodStub(parsedSymbol.name(), parameterCount, resolvedType, staticContext);
            String updated = insertMemberIntoType(targetOriginal, stub);
            if (updated == null || updated.equals(targetOriginal)) {
                continue;
            }

            Path targetRelative = configuration.getWorkspaceRoot().relativize(targetSourcePath);
            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.method_missing");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.method_missing");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            return new MutationProposal(
                "Add missing method stub " + parsedSymbol.name() + " to " + classNameFor(targetSourcePath),
                templateMatch.matched()
                    ? "Verifier diagnostics show a missing method symbol in " + relativePath + ", so reuse a successful repair template while generating a bounded stub in " + targetRelative + "."
                    : "Verifier diagnostics show a missing method symbol in " + relativePath + ", so add a bounded stub to " + targetRelative + " and re-validate.",
                templateMatch.matched() ? 0.96 : 0.92,
                0.63,
                List.of(targetRelative),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(targetRelative, updated)),
                    "Repair unresolved method symbol by generating a bounded stub",
                    buildRepairRisks("add-missing-method-stub", matchingSeeds)
                ),
                evidenceTags
            );
        }
        return null;
    }

    private MutationProposal proposeMissingFieldRepair(
            TaskEnvelope task,
            Path relativePath,
            Path sourcePath,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds) throws IOException {
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            if (!diagnostic.getMessage().toLowerCase(Locale.ROOT).contains("cannot find symbol")) {
                continue;
            }
            ParsedSymbol parsedSymbol = parseMissingSymbol(diagnostic);
            if (parsedSymbol == null || !"variable".equals(parsedSymbol.kind())) {
                continue;
            }

            Path targetSourcePath = resolveSymbolTargetSource(parsedSymbol, sourcePath);
            if (targetSourcePath == null || !Files.exists(targetSourcePath) || Files.isDirectory(targetSourcePath)) {
                continue;
            }

            String targetOriginal = Files.readString(targetSourcePath, StandardCharsets.UTF_8);
            if (declaresFieldName(targetOriginal, parsedSymbol.name())) {
                continue;
            }

            String inferredType = inferExpressionType(original, diagnostic.getLine(), parsedSymbol.name(), false);
            boolean staticContext = isStaticContext(original, diagnostic.getLine());
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.field_missing",
                relativePath,
                parsedSymbol.name(),
                diagnostic
            );
            String templateFieldType = templateAttribute(templateMatch, "fieldType");
            String resolvedType = "Object".equals(inferredType) && templateFieldType != null && !templateFieldType.isBlank()
                ? templateFieldType
                : inferredType;
            String declaration = buildMissingFieldDeclaration(parsedSymbol.name(), resolvedType, staticContext);
            String updated = insertMemberIntoType(targetOriginal, declaration);
            if (updated == null || updated.equals(targetOriginal)) {
                continue;
            }

            Path targetRelative = configuration.getWorkspaceRoot().relativize(targetSourcePath);
            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.field_missing");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.field_missing");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            return new MutationProposal(
                "Add missing field " + parsedSymbol.name() + " to " + classNameFor(targetSourcePath),
                templateMatch.matched()
                    ? "Verifier diagnostics show an unresolved field in " + relativePath + ", so reuse a successful repair template while adding a bounded declaration to " + targetRelative + "."
                    : "Verifier diagnostics show an unresolved field or variable in " + relativePath + ", so add a bounded field declaration to " + targetRelative + ".",
                templateMatch.matched() ? 0.94 : 0.90,
                0.61,
                List.of(targetRelative),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(targetRelative, updated)),
                    "Repair unresolved field symbol by generating a bounded declaration",
                    buildRepairRisks("add-missing-field", matchingSeeds)
                ),
                evidenceTags
            );
        }
        return null;
    }

    private MutationProposal proposeTypeNameAlignmentRepair(
            Path relativePath,
            Path sourcePath,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds) {
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            Matcher matcher = PUBLIC_TYPE_NAME_MISMATCH_PATTERN.matcher(diagnostic.getMessage());
            if (!matcher.find()) {
                continue;
            }

            String declaredTypeName = matcher.group(2);
            String expectedTypeName = classNameFor(sourcePath);
            if (declaredTypeName.equals(expectedTypeName)) {
                continue;
            }

            String updated = replacePublicTypeDeclarationName(original, declaredTypeName, expectedTypeName);
            if (updated == null || updated.equals(original)) {
                continue;
            }

            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.type_name");
            evidenceTags.add("verifier.diagnostic");
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            return new MutationProposal(
                "Align public type name with file " + sourcePath.getFileName(),
                "Verifier diagnostics show that the public type name in " + relativePath + " does not match its file name, so align the declaration with the existing file path.",
                0.96,
                0.67,
                List.of(relativePath),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(relativePath, updated)),
                    "Repair public type and file-name mismatch",
                    buildRepairRisks("align-type-name", matchingSeeds)
                ),
                evidenceTags
            );
        }
        return null;
    }

    private MutationProposal proposeSignatureMismatchRepair(
            TaskEnvelope task,
            Path relativePath,
            Path sourcePath,
            String original,
            List<VerifierDiagnostic> fileDiagnostics,
            List<CorpusMutationSeed> matchingSeeds) {
        for (VerifierDiagnostic diagnostic : fileDiagnostics) {
            if (!diagnostic.getMessage().toLowerCase(Locale.ROOT).contains("cannot be applied to given types")) {
                continue;
            }
            SignatureHint signatureHint = parseSignatureHint(diagnostic);
            if (signatureHint == null) {
                continue;
            }
            if (!"no arguments".equalsIgnoreCase(signatureHint.foundSummary().trim())) {
                continue;
            }
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.signature",
                relativePath,
                signatureHint.methodName(),
                diagnostic
            );
            List<String> requiredTypes = resolveSignatureRequiredTypes(signatureHint, templateMatch);
            if (requiredTypes.isEmpty()) {
                continue;
            }

            String updated = patchNoArgMethodCall(
                original,
                diagnostic.getLine(),
                signatureHint.methodName(),
                buildDefaultArgumentList(requiredTypes)
            );
            if (updated == null || updated.equals(original)) {
                continue;
            }

            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.signature");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.signature");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            return new MutationProposal(
                "Patch signature mismatch for " + classNameFor(sourcePath),
                templateMatch.matched()
                    ? "Verifier diagnostics show that " + signatureHint.methodName() + " is being called with the wrong arity in " + relativePath + ", so reuse a successful signature template while patching the call with deterministic defaults."
                    : "Verifier diagnostics show that " + signatureHint.methodName() + " is being called with the wrong arity in " + relativePath + ", so patch the call with deterministic defaults.",
                templateMatch.matched() ? 0.95 : 0.91,
                0.66,
                List.of(relativePath),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(relativePath, updated)),
                    "Repair signature mismatch for " + signatureHint.methodName(),
                    buildRepairRisks("patch-signature-mismatch", matchingSeeds)
                ),
                evidenceTags
            );
        }
        return null;
    }

    private List<MutationProposal> proposeGeneratedTests(
            TaskEnvelope task,
            List<MemoryHit> hits,
            List<CorpusMutationSeed> corpusSeeds) throws IOException {
        List<MutationProposal> proposals = new ArrayList<>();
        Set<Path> candidateSources = new LinkedHashSet<>();
        for (Path artifact : task.getArtifacts()) {
            candidateSources.add(resolveToWorkspace(artifact));
        }
        for (MemoryHit hit : hits) {
            if (hit.getDocument().getPath() != null) {
                candidateSources.add(resolveToWorkspace(hit.getDocument().getPath()));
            }
        }

        String goal = task.getGoal().toLowerCase(Locale.ROOT);
        boolean goalRequestsVerification = goal.contains("test")
            || goal.contains("verify")
            || goal.contains("coverage")
            || goal.contains("regression")
            || !corpusSeeds.isEmpty();

        for (Path sourcePath : candidateSources) {
            Path resolved = resolveToWorkspace(sourcePath);
            if (!Files.exists(resolved) || Files.isDirectory(resolved)) {
                continue;
            }
            String fileName = resolved.getFileName().toString();
            if (!fileName.endsWith(".java") || fileName.endsWith("Test.java")) {
                continue;
            }

            String className = fileName.substring(0, fileName.length() - 5);
            Path relativeSource = configuration.getWorkspaceRoot().relativize(resolved);
            Path relativeTestPath = deriveTestPath(relativeSource, className);
            Path targetTestPath = configuration.getWorkspaceRoot().resolve(relativeTestPath);
            if (Files.exists(targetTestPath)) {
                continue;
            }

            String packageName = extractPackageName(resolved);
            String fqcn = packageName.isBlank() ? className : packageName + "." + className;
            List<CorpusMutationSeed> matchingSeeds = matchSeedsToSource(resolved, fqcn, corpusSeeds, task.getGoal());
            String testClassName = className + "GeneratedTest";
            String testContent = buildGeneratedTest(packageName, fqcn, testClassName, matchingSeeds);

            double confidence = goalRequestsVerification ? 0.88 : 0.62;
            double novelty = targetTestPath.startsWith(configuration.getWorkspaceRoot().resolve("src").resolve("offlineTest")) ? 0.70 : 0.45;
            if (!matchingSeeds.isEmpty()) {
                confidence = Math.min(0.98, confidence + Math.min(0.18, matchingSeeds.size() * 0.09));
                novelty = Math.min(0.95, novelty + (hasSeedLabel(matchingSeeds, PdfDistillationChunk.Label.FAILURE) ? 0.10 : 0.04));
            }

            String expectedGain = matchingSeeds.isEmpty()
                ? "Add a generated smoke test for " + fqcn
                : "Add a corpus-backed regression test for " + fqcn;
            List<String> risks = new ArrayList<>();
            risks.add("Generated test uses class loading and constructor-free validation");
            if (!matchingSeeds.isEmpty()) {
                risks.add("Derived from distilled corpus seeds");
            }

            String rationale = matchingSeeds.isEmpty()
                ? "The task goal and current workspace suggest missing verification coverage for " + fqcn + "."
                : "Distilled corpus seeds flagged validation or regression gaps around " + fqcn + ", so promote a corpus-backed test scaffold.";
            List<String> evidenceTags = evidenceTagsForSeeds(matchingSeeds);

            MutationCandidate candidate = new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.writeFile(relativeTestPath, testContent)),
                expectedGain,
                risks
            );
            proposals.add(new MutationProposal(
                "Generated test scaffold for " + fqcn,
                rationale,
                confidence,
                novelty,
                List.of(relativeTestPath),
                candidate,
                evidenceTags
            ));
        }

        return proposals;
    }

    private MutationProposal proposeOfflineBootstrap(TaskEnvelope task, WorkspaceProfile workspaceProfile) throws IOException {
        String goal = task.getGoal().toLowerCase(Locale.ROOT);
        boolean goalRequestsReproducibility = goal.contains("verify")
            || goal.contains("reproduc")
            || goal.contains("offline")
            || goal.contains("build")
            || goal.contains("test")
            || goal.contains("bootstrap");
        boolean shouldBootstrap = goalRequestsReproducibility
            || task.getMode() == TaskMode.MUTATION
            || task.getMode() == TaskMode.AUTOMATED;

        if (!shouldBootstrap || !workspaceProfile.hasJavaSources()) {
            return null;
        }

        boolean missingManifest = !workspaceProfile.hasOfflineToolchainManifest();
        boolean missingValidationSurface = workspaceProfile.getRecommendedValidationSuite().allCommands().isEmpty();
        Path scriptPath = Path.of("scripts", "verify-workspace.ps1");
        boolean scriptExists = Files.exists(configuration.getWorkspaceRoot().resolve(scriptPath));

        if (!missingManifest && !missingValidationSurface) {
            return null;
        }

        List<MutationOperation> operations = new ArrayList<>();
        List<Path> targets = new ArrayList<>();

        if (!scriptExists) {
            operations.add(MutationOperation.writeFile(scriptPath, buildGenericVerifyScript()));
            targets.add(scriptPath);
        }

        if (missingManifest) {
            Path manifestPath = Path.of("offline-toolchain.toml");
            operations.add(MutationOperation.writeFile(manifestPath, buildToolchainManifest(scriptPath)));
            targets.add(manifestPath);
        }

        if (operations.isEmpty()) {
            return null;
        }

        double confidence = goalRequestsReproducibility ? 0.86 : 0.68;
        double novelty = missingManifest ? 0.80 : 0.57;
        return new MutationProposal(
            "Bootstrap offline verification surface",
            "The workspace is missing part of its checked-in offline verification contract, so add a local verify script and toolchain manifest.",
            confidence,
            novelty,
            targets,
            new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                operations,
                "Bootstrap reproducible offline verification for the workspace",
                List.of("Generated verification script assumes plain javac-compatible sources")
            )
        );
    }

    private MutationProposal proposeCorpusEvidenceBrief(TaskEnvelope task, List<CorpusMutationSeed> corpusSeeds) {
        if (corpusSeeds.isEmpty()) {
            return null;
        }

        Path briefPath = Path.of(".northstar-kernel", "generated", slugify(task.getGoal()) + "-corpus-brief.md");
        StringBuilder body = new StringBuilder();
        body.append("# Corpus Mutation Seeds\n\n");
        body.append("Goal: ").append(task.getGoal()).append("\n\n");
        body.append("Relevant distilled seeds:\n");
        for (CorpusMutationSeed seed : corpusSeeds.stream().limit(6).toList()) {
            body.append("- ").append(seed.getLabel().getSlug())
                .append(" :: ").append(seed.getSourceId())
                .append(" :: ").append(truncateInline(seed.getExcerpt(), 160))
                .append("\n");
        }

        return new MutationProposal(
            "Corpus mutation brief for " + task.getGoal(),
            "Preserve the strongest failure and test-idea seeds from the distilled corpus as a reusable local mutation brief.",
            0.58,
            0.52,
            List.of(briefPath),
            new MutationCandidate(
                MutationCandidate.Scope.GENERATED_MODULES_ONLY,
                List.of(MutationOperation.writeFile(briefPath, body.toString())),
                "Create a corpus-backed mutation brief for future evolution passes",
                List.of("Brief is advisory and should not bypass validation")
            ),
            evidenceTagsForSeeds(corpusSeeds)
        );
    }

    private MutationProposal proposeWorkspaceDigest(
            TaskEnvelope task,
            WorkspaceProfile workspaceProfile,
            List<MemoryHit> hits,
            List<CorpusMutationSeed> corpusSeeds) {
        String goal = task.getGoal().toLowerCase(Locale.ROOT);
        boolean goalRequestsReport = goal.contains("analyze")
            || goal.contains("report")
            || goal.contains("digest")
            || goal.contains("pdf")
            || goal.contains("brain");

        Path reportPath = Path.of(".northstar-kernel", "generated", slugify(task.getGoal()) + "-digest.md");
        StringBuilder body = new StringBuilder();
        body.append("# Kernel Digest\n\n");
        body.append("Goal: ").append(task.getGoal()).append("\n\n");
        body.append("Languages: ").append(workspaceProfile.getLanguages()).append("\n");
        body.append("Validation Commands: ")
            .append(workspaceProfile.getRecommendedValidationSuite().allCommands().stream().map(ValidationCommand::getLabel).toList())
            .append("\n\n");
        body.append("Top Memory Hits:\n");
        for (MemoryHit hit : hits.stream().limit(5).toList()) {
            body.append("- ").append(hit.getDocument().getSourceId())
                .append(" score=").append(String.format(Locale.ROOT, "%.4f", hit.getTotalScore()))
                .append("\n");
        }
        if (!corpusSeeds.isEmpty()) {
            body.append("\nRelevant Corpus Seeds:\n");
            for (CorpusMutationSeed seed : corpusSeeds.stream().limit(4).toList()) {
                body.append("- ").append(seed.getLabel().getSlug())
                    .append(" :: ").append(truncateInline(seed.getExcerpt(), 140))
                    .append("\n");
            }
        }

        MutationCandidate candidate = new MutationCandidate(
            MutationCandidate.Scope.GENERATED_MODULES_ONLY,
            List.of(MutationOperation.writeFile(reportPath, body.toString())),
            "Create a generated digest for task context",
            List.of("Digest is advisory, not executable")
        );

        return new MutationProposal(
            "Workspace digest for " + task.getGoal(),
            "Create a local digest artifact so future runs can reuse structured context without re-deriving it.",
            goalRequestsReport ? 0.72 : 0.40,
            0.55,
            List.of(reportPath),
            candidate,
            evidenceTagsForSeeds(corpusSeeds)
        );
    }

    private List<CorpusMutationSeed> collectCorpusSeeds(List<MemoryHit> hits, String goal) throws IOException {
        List<CorpusMutationSeed> seeds = new ArrayList<>();
        Set<String> seenSourceIds = new LinkedHashSet<>();
        Set<String> goalTokens = tokenize(goal);

        for (MemoryHit hit : hits) {
            MemoryDocument document = hit.getDocument();
            PdfDistillationChunk.Label label = labelFromSourceId(document.getSourceId());
            if (label != PdfDistillationChunk.Label.FAILURE && label != PdfDistillationChunk.Label.TEST_IDEA) {
                continue;
            }
            if (!seenSourceIds.add(document.getSourceId())) {
                continue;
            }

            String content = readSeedContent(document);
            String excerpt = content.isBlank() ? document.getPreview() : content;
            List<String> keywords = extractKeywords(excerpt);
            boolean overlapsGoal = keywords.stream().anyMatch(goalTokens::contains);
            boolean causallyWeak = hit.getSpuriousPenalty() > 0.0 && !hit.isCausallyGrounded();
            if (causallyWeak && !overlapsGoal) {
                continue;
            }
            if (!overlapsGoal && seeds.size() >= 4) {
                continue;
            }

            seeds.add(new CorpusMutationSeed(
                label,
                document.getSourceId(),
                excerpt,
                keywords,
                hit.getCausalScore(),
                hit.isCausallyGrounded(),
                hit.getCausalNotes()
            ));
        }

        seeds.sort(Comparator
            .comparing(CorpusMutationSeed::isCausallyGrounded).reversed()
            .thenComparingDouble(CorpusMutationSeed::getCausalScore).reversed()
        );
        return seeds;
    }

    private List<Path> collectCandidateJavaSources(TaskEnvelope task, List<MemoryHit> hits, List<VerifierDiagnostic> diagnostics) {
        Set<Path> sources = new LinkedHashSet<>();
        for (Path artifact : task.getArtifacts()) {
            Path resolved = resolveToWorkspace(artifact);
            if (resolved.toString().toLowerCase(Locale.ROOT).endsWith(".java")) {
                sources.add(resolved);
            }
        }
        for (MemoryHit hit : hits) {
            Path path = hit.getDocument().getPath();
            if (path != null && path.toString().toLowerCase(Locale.ROOT).endsWith(".java")) {
                sources.add(resolveToWorkspace(path));
            }
        }
        for (VerifierDiagnostic diagnostic : diagnostics == null ? List.<VerifierDiagnostic>of() : diagnostics) {
            Path path = diagnostic.getFile();
            if (path != null && path.toString().toLowerCase(Locale.ROOT).endsWith(".java")) {
                sources.add(resolveToWorkspace(path));
            }
        }
        return new ArrayList<>(sources);
    }

    private List<CorpusMutationSeed> matchSeedsToSource(
            Path sourcePath,
            String fqcn,
            List<CorpusMutationSeed> corpusSeeds,
            String goal) {
        if (corpusSeeds.isEmpty()) {
            return List.of();
        }

        Set<String> sourceTokens = new LinkedHashSet<>(tokenize(fqcn));
        sourceTokens.addAll(tokenize(sourcePath.getFileName().toString()));
        sourceTokens.addAll(tokenize(goal));

        List<CorpusMutationSeed> matches = new ArrayList<>();
        for (CorpusMutationSeed seed : corpusSeeds) {
            boolean keywordMatch = seed.getKeywords().stream().anyMatch(sourceTokens::contains);
            boolean fqcnMatch = seed.getExcerpt().toLowerCase(Locale.ROOT).contains(fqcn.toLowerCase(Locale.ROOT));
            boolean classNameMatch = seed.getExcerpt().toLowerCase(Locale.ROOT).contains(
                sourcePath.getFileName().toString().replace(".java", "").toLowerCase(Locale.ROOT)
            );
            if (keywordMatch || fqcnMatch || classNameMatch) {
                matches.add(seed);
            }
        }

        if (matches.isEmpty()) {
            return corpusSeeds.stream()
                .sorted(Comparator
                    .comparing(CorpusMutationSeed::isCausallyGrounded).reversed()
                    .thenComparingDouble(CorpusMutationSeed::getCausalScore).reversed())
                .limit(2)
                .toList();
        }
        return matches.stream()
            .sorted(Comparator
                .comparing(CorpusMutationSeed::isCausallyGrounded).reversed()
                .thenComparingDouble(CorpusMutationSeed::getCausalScore).reversed())
            .limit(3)
            .toList();
    }

    private Path deriveTestPath(Path relativeSource, String className) {
        String normalized = relativeSource.toString().replace('\\', '/');
        if (normalized.startsWith("src/main/java/")) {
            String suffix = normalized.substring("src/main/java/".length());
            int lastSlash = suffix.lastIndexOf('/');
            String packagePath = lastSlash >= 0 ? suffix.substring(0, lastSlash) : "";
            return packagePath.isBlank()
                ? Path.of("src", "offlineTest", "java", className + "GeneratedTest.java")
                : Path.of("src", "offlineTest", "java", packagePath, className + "GeneratedTest.java");
        }
        return Path.of(".northstar-kernel", "generated-tests", className + "GeneratedTest.java");
    }

    private String extractPackageName(Path sourceFile) throws IOException {
        String content = Files.readString(sourceFile, StandardCharsets.UTF_8);
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                return trimmed.substring("package ".length(), trimmed.length() - 1).trim();
            }
        }
        return "";
    }

    private String buildGeneratedTest(
            String packageName,
            String fqcn,
            String testClassName,
            List<CorpusMutationSeed> matchingSeeds) {
        StringBuilder builder = new StringBuilder();
        if (!packageName.isBlank()) {
            builder.append("package ").append(packageName).append(";\n\n");
        }
        if (!matchingSeeds.isEmpty()) {
            builder.append("// Corpus-guided regression seeds:\n");
            for (CorpusMutationSeed seed : matchingSeeds) {
                builder.append("// - ")
                    .append(seed.getLabel().getSlug())
                    .append(": ")
                    .append(truncateInline(seed.getExcerpt(), 100))
                    .append("\n");
            }
            builder.append("\n");
        }
        builder.append("public final class ").append(testClassName).append(" {\n");
        builder.append("    public static void main(String[] args) throws Exception {\n");
        builder.append("        Class<?> target = Class.forName(\"").append(fqcn).append("\");\n");
        builder.append("        if (target == null) {\n");
        builder.append("            throw new IllegalStateException(\"Unable to load ").append(fqcn).append("\");\n");
        builder.append("        }\n");
        builder.append("        System.out.println(\"").append(testClassName).append(" passed\");\n");
        builder.append("    }\n");
        builder.append("}\n");
        return builder.toString();
    }

    private String buildGenericVerifyScript() {
        return """
            Set-StrictMode -Version Latest
            $ErrorActionPreference = 'Stop'

            $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
            $projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
            $mainSource = Join-Path $projectRoot 'src\\main\\java'
            $testSource = Join-Path $projectRoot 'src\\offlineTest\\java'
            $buildDir = Join-Path $projectRoot ('build\\offline-workspace-verify-' + $PID)
            $classesDir = Join-Path $buildDir 'classes'
            $sourcesFile = Join-Path $buildDir 'sources.txt'

            $javac = Get-Command javac -ErrorAction SilentlyContinue
            if (-not $javac) {
                throw 'javac was not found in PATH.'
            }

            try {
                New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
                New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

                $sources = @()
                if (Test-Path $mainSource) {
                    $sources += Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                }
                if (Test-Path $testSource) {
                    $sources += Get-ChildItem $testSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
                }

                if (-not $sources -or $sources.Count -eq 0) {
                    throw 'No Java sources found for offline verification.'
                }

                [System.IO.File]::WriteAllLines($sourcesFile, $sources)

                & $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
                if ($LASTEXITCODE -ne 0) {
                    throw "javac failed with exit code $LASTEXITCODE"
                }

                Write-Host ("Workspace verification passed. Sources=" + $sources.Count)
            }
            finally {
                if (Test-Path $buildDir) {
                    Remove-Item -Recurse -Force $buildDir -ErrorAction SilentlyContinue
                }
            }
            """;
    }

    private String buildToolchainManifest(Path verifyScriptPath) {
        String normalizedVerifyScript = normalizePathString(verifyScriptPath);
        return """
            [kernel]
            runtime = "air-gapped"
            java = "17"
            verify = "%s"

            [notes]
            generated_by = "brain_architecture.kernel.MutationProposalEngine"
            """.formatted(normalizedVerifyScript);
    }

    private String readSeedContent(MemoryDocument document) throws IOException {
        if (document.getPath() != null && Files.exists(document.getPath()) && Files.isRegularFile(document.getPath())) {
            return Files.readString(document.getPath(), StandardCharsets.UTF_8);
        }
        return document.getPreview();
    }

    private ParsedSymbol parseMissingSymbol(VerifierDiagnostic diagnostic) {
        if (diagnostic == null || diagnostic.getRaw() == null) {
            return null;
        }
        Matcher matcher = MISSING_SYMBOL_PATTERN.matcher(diagnostic.getRaw());
        if (!matcher.find()) {
            return null;
        }
        String kind = matcher.group(1).toLowerCase(Locale.ROOT);
        String rawDescriptor = matcher.group(2).trim();
        String name = rawDescriptor;
        int parameterCount = 0;
        if ("method".equals(kind)) {
            int openParen = rawDescriptor.indexOf('(');
            int closeParen = rawDescriptor.lastIndexOf(')');
            name = openParen > 0 ? rawDescriptor.substring(0, openParen).trim() : rawDescriptor;
            if (openParen >= 0 && closeParen > openParen) {
                List<String> parameterTypes = splitSignatureTypes(rawDescriptor.substring(openParen + 1, closeParen));
                parameterCount = parameterTypes.size();
            }
        } else {
            Matcher identifier = Pattern.compile("([A-Za-z_$][\\w$]*)").matcher(rawDescriptor);
            if (identifier.find()) {
                name = identifier.group(1);
            }
        }

        Matcher locationMatcher = LOCATION_PATTERN.matcher(diagnostic.getRaw());
        String locationKind = "";
        String locationName = "";
        if (locationMatcher.find()) {
            locationKind = locationMatcher.group(1).toLowerCase(Locale.ROOT);
            locationName = locationMatcher.group(2).trim();
        }
        return new ParsedSymbol(
            kind,
            name,
            rawDescriptor,
            parameterCount,
            locationKind,
            locationName
        );
    }

    private SignatureHint parseSignatureHint(VerifierDiagnostic diagnostic) {
        if (diagnostic == null || diagnostic.getRaw() == null) {
            return null;
        }
        Matcher methodMatcher = METHOD_SIGNATURE_PATTERN.matcher(diagnostic.getMessage());
        if (!methodMatcher.find()) {
            return null;
        }
        Matcher requiredMatcher = REQUIRED_PATTERN.matcher(diagnostic.getRaw());
        Matcher foundMatcher = FOUND_PATTERN.matcher(diagnostic.getRaw());
        if (!requiredMatcher.find() || !foundMatcher.find()) {
            return null;
        }
        String requiredSummary = requiredMatcher.group(1).trim();
        String foundSummary = foundMatcher.group(1).trim();
        List<String> requiredTypes = splitSignatureTypes(requiredSummary);
        return new SignatureHint(methodMatcher.group(1), requiredTypes, foundSummary);
    }

    private List<String> splitSignatureTypes(String requiredSummary) {
        if (requiredSummary == null || requiredSummary.isBlank() || "no arguments".equalsIgnoreCase(requiredSummary.trim())) {
            return List.of();
        }
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int genericDepth = 0;
        for (char c : requiredSummary.toCharArray()) {
            if (c == '<') {
                genericDepth++;
            } else if (c == '>') {
                genericDepth = Math.max(0, genericDepth - 1);
            }
            if (c == ',' && genericDepth == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) {
            parts.add(current.toString().trim());
        }
        return parts.stream().filter(part -> !part.isBlank()).toList();
    }

    private String patchNoArgMethodCall(String original, Integer lineNumber, String methodName, String argumentList) {
        if (lineNumber == null || lineNumber <= 0) {
            return null;
        }
        String[] lines = original.split("\\R", -1);
        if (lineNumber > lines.length) {
            return null;
        }
        String targetLine = lines[lineNumber - 1];
        String oldCall = methodName + "()";
        if (!targetLine.contains(oldCall)) {
            return null;
        }
        String newCall = methodName + "(" + argumentList + ")";
        lines[lineNumber - 1] = targetLine.replaceFirst(Pattern.quote(oldCall), Matcher.quoteReplacement(newCall));
        return String.join(System.lineSeparator(), Arrays.asList(lines));
    }

    private String buildDefaultArgumentList(List<String> requiredTypes) {
        List<String> defaults = new ArrayList<>();
        for (String type : requiredTypes) {
            defaults.add(defaultLiteralForType(type));
        }
        return String.join(", ", defaults);
    }

    private String defaultLiteralForType(String rawType) {
        String type = rawType == null ? "" : rawType.trim();
        if (type.endsWith("...")) {
            String componentType = type.substring(0, type.length() - 3).trim();
            return "new " + simpleTypeName(componentType) + "[0]";
        }
        if (type.endsWith("[]")) {
            return "new " + simpleTypeName(type.substring(0, type.length() - 2)) + "[0]";
        }
        String simple = simpleTypeName(type);
        return switch (simple) {
            case "boolean" -> "false";
            case "byte", "short", "int", "long" -> "0";
            case "float" -> "0.0f";
            case "double" -> "0.0d";
            case "char" -> "'\\0'";
            case "String" -> "\"\"";
            default -> "null";
        };
    }

    private String defaultLiteralForType(
            String rawType,
            Map<String, TypeSynthesisPlan> availablePlans,
            String ownerSimpleName) {
        String collaboratorLiteral = collaboratorConstructionLiteral(rawType, availablePlans, ownerSimpleName);
        return collaboratorLiteral != null ? collaboratorLiteral : defaultLiteralForType(rawType);
    }

    private String collaboratorConstructionLiteral(
            String rawType,
            Map<String, TypeSynthesisPlan> availablePlans,
            String ownerSimpleName) {
        if (rawType == null || availablePlans == null || availablePlans.isEmpty()) {
            return null;
        }

        String simpleType = simpleTypeName(normalizeJavaType(rawType));
        if (simpleType.isBlank() || simpleType.equals(ownerSimpleName)) {
            return null;
        }

        TypeSynthesisPlan collaborator = availablePlans.get(simpleType);
        if (collaborator == null || !hasZeroArgumentConstructor(collaborator)) {
            return null;
        }
        return "new " + collaborator.simpleName() + "()";
    }

    private boolean hasZeroArgumentConstructor(TypeSynthesisPlan plan) {
        if (plan == null || plan.constructors() == null || plan.constructors().isEmpty()) {
            return true;
        }
        for (ConstructorPlan constructor : plan.constructors()) {
            if (constructor.parameterCount() == 0) {
                return true;
            }
        }
        return false;
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

    private TypeReference findUniqueTypeCandidate(String simpleName, Path sourcePath) throws IOException {
        for (Path searchRoot : preferredTypeSearchRoots(sourcePath)) {
            List<TypeReference> scopedCandidates = findTypeCandidates(simpleName, sourcePath, searchRoot);
            if (scopedCandidates.size() == 1) {
                return scopedCandidates.get(0);
            }
            if (scopedCandidates.size() > 1) {
                return null;
            }
        }
        return null;
    }

    private List<TypeReference> findTypeCandidates(String simpleName, Path sourcePath) throws IOException {
        List<TypeReference> candidates = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (Path searchRoot : preferredTypeSearchRoots(sourcePath)) {
            for (TypeReference candidate : findTypeCandidates(simpleName, sourcePath, searchRoot)) {
                String key = normalizePathString(candidate.path()) + "|" + candidate.packageName();
                if (seen.add(key)) {
                    candidates.add(candidate);
                }
            }
        }
        return candidates;
    }

    private List<TypeReference> findTypeCandidates(String simpleName, Path sourcePath, Path searchRoot) throws IOException {
        List<TypeReference> candidates = new ArrayList<>();
        if (searchRoot == null || !Files.exists(searchRoot)) {
            return candidates;
        }
        try (var walk = Files.walk(searchRoot)) {
            for (Path candidate : (Iterable<Path>) walk.filter(Files::isRegularFile)::iterator) {
                if (!candidate.toString().toLowerCase(Locale.ROOT).endsWith(".java")) {
                    continue;
                }
                if (candidate.toAbsolutePath().normalize().equals(sourcePath.toAbsolutePath().normalize())) {
                    continue;
                }
                if (isIgnoredPath(candidate)) {
                    continue;
                }
                if (!declaresTopLevelType(candidate, simpleName)) {
                    continue;
                }
                String candidatePackage = extractPackageName(candidate);
                candidates.add(new TypeReference(candidate, candidatePackage, simpleName));
            }
        }
        return candidates;
    }

    private List<Path> preferredTypeSearchRoots(Path sourcePath) {
        List<Path> roots = new ArrayList<>();
        Path normalizedSource = sourcePath == null ? null : sourcePath.toAbsolutePath().normalize();
        boolean scopedLiveTraining = false;
        if (normalizedSource != null) {
            String normalized = normalizePathString(configuration.getWorkspaceRoot().relativize(normalizedSource));
            if (normalized.startsWith("live-training/src/main/java/")) {
                roots.add(configuration.getWorkspaceRoot().resolve("live-training/src/main/java"));
                scopedLiveTraining = true;
            } else if (normalized.startsWith("src/main/java/")) {
                roots.add(configuration.getWorkspaceRoot().resolve("src/main/java"));
            } else if (normalized.startsWith("src/offlineTest/java/")) {
                roots.add(configuration.getWorkspaceRoot().resolve("src/offlineTest/java"));
            }
        }
        if (!scopedLiveTraining) {
            roots.add(configuration.getWorkspaceRoot());
        }
        return roots.stream().distinct().toList();
    }

    private boolean declaresTopLevelType(Path sourceFile, String simpleName) throws IOException {
        String content = Files.readString(sourceFile, StandardCharsets.UTF_8);
        Pattern declaration = Pattern.compile("\\b(class|interface|enum|record)\\s+" + Pattern.quote(simpleName) + "\\b");
        return declaration.matcher(content).find();
    }

    private boolean isIgnoredPath(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        Path relative = null;
        try {
            relative = configuration.getWorkspaceRoot().relativize(normalized);
        } catch (IllegalArgumentException ignored) {
            // Fall through to path-part checks for anything outside the workspace root.
        }
        if (relative != null) {
            String relativePath = normalizePathString(relative);
            if (relativePath.startsWith("live-training/scenarios/")
                    || relativePath.startsWith("live-training/training-reports/")) {
                return true;
            }
        }
        for (Path part : normalized) {
            if (configuration.getIgnoredNames().contains(part.toString())) {
                return true;
            }
        }
        return false;
    }

    private String insertImportStatement(String original, String fqcn) {
        if (original.contains("import " + fqcn + ";")) {
            return original;
        }
        String newline = original.contains("\r\n") ? "\r\n" : "\n";
        String[] lines = original.split("\\R", -1);
        List<String> updated = new ArrayList<>(Arrays.asList(lines));

        int packageIndex = -1;
        int lastImportIndex = -1;
        for (int i = 0; i < updated.size(); i++) {
            String trimmed = updated.get(i).trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                packageIndex = i;
            }
            if (trimmed.startsWith("import ") && trimmed.endsWith(";")) {
                lastImportIndex = i;
            }
        }

        int insertIndex = lastImportIndex >= 0 ? lastImportIndex + 1 : (packageIndex >= 0 ? packageIndex + 1 : 0);
        updated.add(insertIndex, "import " + fqcn + ";");
        return String.join(newline, updated);
    }

    private List<MutationProposal> proposePackageAlignmentRepairs(
            TaskEnvelope task,
            List<CorpusMutationSeed> corpusSeeds,
            VerifierResult verifierResult) throws IOException {
        if (!hasPackageMismatchSignal(verifierResult)) {
            return List.of();
        }

        List<MutationProposal> proposals = new ArrayList<>();
        for (Path sourcePath : findPackageMismatchSources()) {
            String original = Files.readString(sourcePath, StandardCharsets.UTF_8);
            String expectedPackage = expectedPackageForSource(sourcePath);
            String updated = rewritePackageDeclaration(original, expectedPackage);
            if (updated == null || updated.equals(original)) {
                continue;
            }

            Path relativePath = configuration.getWorkspaceRoot().relativize(sourcePath);
            String fqcn = expectedPackage.isBlank() ? classNameFor(sourcePath) : expectedPackage + "." + classNameFor(sourcePath);
            List<CorpusMutationSeed> matchingSeeds = matchSeedsToSource(sourcePath, fqcn, corpusSeeds, task.getGoal());
            RepairMemoryStore.TemplateMatch templateMatch = bestTemplateMatch(
                task,
                "verifier.compiler.package",
                relativePath,
                expectedPackage + " " + sourcePath.getFileName(),
                null
            );
            List<String> evidenceTags = new ArrayList<>();
            evidenceTags.add("verifier.failure");
            evidenceTags.add("verifier.compiler.package");
            evidenceTags.add("verifier.diagnostic");
            if (templateMatch.matched()) {
                evidenceTags.add("repair.template");
                evidenceTags.add("repair.template.package");
            }
            evidenceTags.addAll(evidenceTagsForSeeds(matchingSeeds));
            proposals.add(new MutationProposal(
                "Align package declaration for " + sourcePath.getFileName(),
                templateMatch.matched()
                    ? "The workspace has a Java source whose declared package does not match its source path, so reuse a successful package template while rewriting the declaration to the canonical offline layout."
                    : "The workspace has a Java source whose declared package does not match its source path, so rewrite the package declaration to the canonical offline layout.",
                templateMatch.matched() ? 0.98 : 0.95,
                0.65,
                List.of(relativePath),
                new MutationCandidate(
                    MutationCandidate.Scope.WHOLE_CODEBASE,
                    List.of(MutationOperation.writeFile(relativePath, updated)),
                    "Repair Java package and source-path mismatch",
                    buildRepairRisks("align-package-declaration", matchingSeeds)
                ),
                evidenceTags
            ));
        }
        return proposals;
    }

    private List<String> extractKeywords(String content) {
        Set<String> keywords = new LinkedHashSet<>();
        for (String token : tokenize(content)) {
            if (token.length() < 5 || KEYWORD_STOPWORDS.contains(token)) {
                continue;
            }
            keywords.add(token);
            if (keywords.size() >= 8) {
                break;
            }
        }
        return new ArrayList<>(keywords);
    }

    private Set<String> tokenize(String text) {
        Set<String> tokens = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        for (String token : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private boolean hasSeedLabel(List<CorpusMutationSeed> seeds, PdfDistillationChunk.Label label) {
        return seeds.stream().anyMatch(seed -> seed.getLabel() == label);
    }

    private boolean hasPackageMismatchSignal(VerifierResult verifierResult) {
        String failureText = buildFailureText(verifierResult);
        if (failureText.contains("bad source file") || failureText.contains("does not contain class") || failureText.contains("package does not exist")) {
            return true;
        }
        for (VerifierDiagnostic diagnostic : verifierResult.getErrorDiagnostics()) {
            String message = diagnostic.getMessage().toLowerCase(Locale.ROOT);
            if (message.contains("should be declared in a file named")) {
                return true;
            }
            ParsedSymbol parsedSymbol = parseMissingSymbol(diagnostic);
            if (parsedSymbol != null && parsedSymbol.isTypeLike()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCompilerFailure(VerifierResult verifierResult) {
        return !verifierResult.getErrorDiagnostics().isEmpty()
            || buildFailureText(verifierResult).contains("error")
            || verifierResult.getFailedChecks().stream().anyMatch(check -> check.contains("command."));
    }

    private boolean hasParserFailure(VerifierResult verifierResult, List<VerifierDiagnostic> diagnostics) {
        StringBuilder builder = new StringBuilder(buildFailureText(verifierResult));
        for (VerifierDiagnostic diagnostic : diagnostics == null ? List.<VerifierDiagnostic>of() : diagnostics) {
            builder.append('\n').append(diagnostic.getMessage().toLowerCase(Locale.ROOT));
        }
        String failureText = builder.toString();
        return failureText.contains("reached end of file while parsing")
            || failureText.contains("class, interface, enum, or record expected")
            || failureText.contains("illegal character")
            || failureText.contains("unclosed");
    }

    private String buildFailureText(VerifierResult verifierResult) {
        StringBuilder builder = new StringBuilder();
        verifierResult.getFailedChecks().forEach(check -> builder.append(check).append('\n'));
        verifierResult.getWarnings().forEach(warning -> builder.append(warning).append('\n'));
        return builder.toString().toLowerCase(Locale.ROOT);
    }

    private List<VerifierDiagnostic> relevantSourceDiagnostics(VerifierResult verifierResult) {
        return verifierResult.getDiagnostics().stream()
            .filter(diagnostic -> diagnostic.getSeverity() == VerifierDiagnostic.Severity.ERROR)
            .filter(diagnostic -> diagnostic.getFile() != null)
            .filter(diagnostic -> diagnostic.getFile().toString().toLowerCase(Locale.ROOT).endsWith(".java"))
            .toList();
    }

    private List<VerifierDiagnostic> diagnosticsForFile(List<VerifierDiagnostic> diagnostics, Path sourcePath) {
        Path normalizedSource = sourcePath.toAbsolutePath().normalize();
        return diagnostics.stream()
            .filter(diagnostic -> diagnostic.getFile() != null)
            .filter(diagnostic -> diagnostic.getFile().toAbsolutePath().normalize().equals(normalizedSource))
            .toList();
    }

    private Path resolveSymbolTargetSource(ParsedSymbol parsedSymbol, Path sourcePath) throws IOException {
        if (parsedSymbol == null) {
            return sourcePath;
        }

        String locationKind = parsedSymbol.locationKind();
        String locationName = parsedSymbol.locationName();
        if ("variable".equals(locationKind)) {
            Matcher matcher = VARIABLE_LOCATION_TYPE_PATTERN.matcher(locationName);
            if (matcher.find()) {
                String typeName = simpleTypeName(matcher.group(2));
                if (typeName.equals(classNameFor(sourcePath))) {
                    return sourcePath;
                }
                TypeReference candidate = findUniqueTypeCandidate(typeName, sourcePath);
                return candidate == null ? sourcePath : candidate.path();
            }
        }

        if ("class".equals(locationKind)
                || "interface".equals(locationKind)
                || "enum".equals(locationKind)
                || "record".equals(locationKind)) {
            String typeName = simpleTypeName(locationName);
            if (typeName.equals(classNameFor(sourcePath))) {
                return sourcePath;
            }
            TypeReference candidate = findUniqueTypeCandidate(typeName, sourcePath);
            return candidate == null ? sourcePath : candidate.path();
        }

        return sourcePath;
    }

    private boolean declaresMethodName(String content, String methodName) {
        Pattern pattern = Pattern.compile(
            "(?:(?:public|protected|private|static|final|native|synchronized|abstract|strictfp|default)\\s+)*" +
            "[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+" +
            Pattern.quote(methodName) +
            "\\s*\\([^;{}]*\\)\\s*(?:throws\\s+[^;{]+)?\\s*(?:\\{|;)"
        );
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("return ")
                    || trimmed.startsWith("throw ")
                    || trimmed.startsWith("if ")
                    || trimmed.startsWith("while ")
                    || trimmed.startsWith("for ")
                    || trimmed.startsWith("switch ")
                    || trimmed.startsWith("case ")
                    || trimmed.startsWith("new ")) {
                continue;
            }
            if (pattern.matcher(trimmed).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean declaresFieldName(String content, String fieldName) {
        Pattern pattern = Pattern.compile(
            "(?:(?:public|protected|private|static|final|transient|volatile)\\s+)*" +
            "[A-Za-z_$][\\w$<>,.?\\[\\]]*\\s+" +
            Pattern.quote(fieldName) +
            "\\s*(?:=.*)?;"
        );
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("return ")
                    || trimmed.startsWith("throw ")
                    || trimmed.startsWith("if ")
                    || trimmed.startsWith("while ")
                    || trimmed.startsWith("for ")
                    || trimmed.startsWith("switch ")
                    || trimmed.startsWith("case ")
                    || trimmed.startsWith("new ")) {
                continue;
            }
            if (pattern.matcher(trimmed).matches()) {
                return true;
            }
        }
        return false;
    }

    private String inferExpressionType(String original, Integer lineNumber, String symbolName, boolean methodSymbol) {
        if (original == null || lineNumber == null || lineNumber <= 0) {
            return "Object";
        }

        String[] lines = original.split("\\R", -1);
        if (lineNumber > lines.length) {
            return "Object";
        }

        String line = lines[lineNumber - 1].trim();
        String symbolToken = methodSymbol ? symbolName + "(" : symbolName;
        if (!line.contains(symbolToken)) {
            return "Object";
        }

        if (line.startsWith("return ") && line.contains(symbolToken)) {
            MethodContext context = findEnclosingMethodContext(lines, lineNumber - 1);
            if (context != null && !context.returnType().isBlank()) {
                return normalizeJavaType(context.returnType());
            }
        }

        if (line.startsWith("if (") || line.startsWith("while (") || line.startsWith("for (")) {
            return "boolean";
        }

        Matcher assignmentMatcher = LOCAL_ASSIGNMENT_PATTERN.matcher(line);
        if (assignmentMatcher.find()) {
            return normalizeJavaType(assignmentMatcher.group(1));
        }

        if (methodSymbol && isStandaloneInvocation(line, symbolName)) {
            return "void";
        }

        return "Object";
    }

    private boolean isStaticContext(String original, Integer lineNumber) {
        if (original == null || lineNumber == null || lineNumber <= 0) {
            return false;
        }
        String[] lines = original.split("\\R", -1);
        MethodContext context = findEnclosingMethodContext(lines, Math.max(0, lineNumber - 1));
        return context != null && context.staticContext();
    }

    private MethodContext findEnclosingMethodContext(String[] lines, int lineIndex) {
        Pattern methodPattern = Pattern.compile(
            "(?:(?:public|protected|private|static|final|native|synchronized|abstract|strictfp|default)\\s+)*" +
            "([A-Za-z_$][\\w$<>,.?\\[\\]]*)\\s+" +
            "([A-Za-z_$][\\w$]*)\\s*\\([^;{}]*\\)\\s*(?:throws\\s+[^;{]+)?\\s*(?:\\{|;)"
        );
        for (int i = Math.min(lineIndex, lines.length - 1); i >= 0; i--) {
            String trimmed = lines[i].trim();
            if (trimmed.isBlank()
                    || trimmed.startsWith("@")
                    || trimmed.startsWith("return ")
                    || trimmed.startsWith("throw ")
                    || trimmed.startsWith("if ")
                    || trimmed.startsWith("while ")
                    || trimmed.startsWith("for ")
                    || trimmed.startsWith("switch ")
                    || trimmed.startsWith("case ")
                    || trimmed.startsWith("new ")) {
                continue;
            }
            Matcher matcher = methodPattern.matcher(trimmed);
            if (matcher.matches()) {
                boolean staticContext = trimmed.contains(" static ") || trimmed.startsWith("static ");
                return new MethodContext(matcher.group(1), staticContext);
            }
        }
        return null;
    }

    private boolean isStandaloneInvocation(String line, String methodName) {
        String trimmed = line == null ? "" : line.trim();
        if (!trimmed.endsWith(";") || trimmed.contains("return ") || trimmed.contains("=")) {
            return false;
        }
        return trimmed.contains(methodName + "(");
    }

    private String buildMissingMethodStub(String methodName, int parameterCount, String inferredType, boolean staticContext) {
        String newline = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append("    private ");
        if (staticContext) {
            builder.append("static ");
        }
        builder.append(inferredType).append(' ').append(methodName).append('(');
        for (int i = 0; i < parameterCount; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("Object arg").append(i);
        }
        builder.append(") {").append(newline);
        if (!"void".equals(inferredType)) {
            builder.append("        return ").append(defaultLiteralForType(inferredType)).append(';').append(newline);
        }
        builder.append("    }");
        return builder.toString();
    }

    private String buildMissingFieldDeclaration(String fieldName, String inferredType, boolean staticContext) {
        StringBuilder builder = new StringBuilder();
        builder.append("    private ");
        if (staticContext) {
            builder.append("static ");
        }
        builder.append(inferredType)
            .append(' ')
            .append(fieldName)
            .append(" = ")
            .append(defaultLiteralForType(inferredType))
            .append(';');
        return builder.toString();
    }

    private String insertMemberIntoType(String original, String member) {
        if (original == null || member == null || member.isBlank()) {
            return original;
        }
        int insertIndex = original.lastIndexOf('}');
        if (insertIndex < 0) {
            return original;
        }
        String newline = original.contains("\r\n") ? "\r\n" : "\n";
        String normalizedMember = member.replace("\r\n", "\n").replace("\n", newline);
        String prefix = original.substring(0, insertIndex).stripTrailing();
        String suffix = original.substring(insertIndex);
        return prefix + newline + newline + normalizedMember + newline + suffix;
    }

    private String replacePublicTypeDeclarationName(String original, String existingName, String replacementName) {
        Pattern declaration = Pattern.compile(
            "(?m)^([\\t ]*(?:public\\s+)?(?:abstract\\s+|final\\s+|sealed\\s+|non-sealed\\s+)*" +
            "(?:class|interface|enum|record)\\s+)" +
            Pattern.quote(existingName) +
            "(\\b)"
        );
        Matcher matcher = declaration.matcher(original);
        if (!matcher.find()) {
            return null;
        }
        return original.substring(0, matcher.start())
            + matcher.group(1)
            + replacementName
            + matcher.group(2)
            + original.substring(matcher.end());
    }

    private List<Path> findPackageMismatchSources() throws IOException {
        List<Path> mismatches = new ArrayList<>();
        try (var walk = Files.walk(configuration.getWorkspaceRoot())) {
            for (Path candidate : (Iterable<Path>) walk.filter(Files::isRegularFile)::iterator) {
                if (!candidate.toString().toLowerCase(Locale.ROOT).endsWith(".java") || isIgnoredPath(candidate)) {
                    continue;
                }
                String declaredPackage = extractPackageName(candidate);
                if (hasPackageAlignmentMismatch(candidate, declaredPackage)) {
                    mismatches.add(candidate);
                }
            }
        }
        return mismatches;
    }

    private boolean hasPackageAlignmentMismatch(Path sourcePath, String declaredPackage) {
        String expectedPackage = expectedPackageForSource(sourcePath);
        if (expectedPackage == null) {
            return false;
        }
        return !expectedPackage.equals(declaredPackage == null ? "" : declaredPackage.trim());
    }

    private String expectedPackageForSource(Path sourcePath) {
        Path relative = configuration.getWorkspaceRoot().relativize(sourcePath.normalize());
        String normalized = normalizePathString(relative);
        if (normalized.startsWith("src/main/java/")) {
            return packageFromRelativeSuffix(normalized.substring("src/main/java/".length()));
        }
        if (normalized.startsWith("src/offlineTest/java/")) {
            return packageFromRelativeSuffix(normalized.substring("src/offlineTest/java/".length()));
        }
        if (normalized.startsWith("live-training/src/main/java/")) {
            return packageFromRelativeSuffix(normalized.substring("live-training/src/main/java/".length()));
        }
        if (normalized.startsWith("live-training/src/offlineTest/java/")) {
            return packageFromRelativeSuffix(normalized.substring("live-training/src/offlineTest/java/".length()));
        }
        return null;
    }

    private String packageFromRelativeSuffix(String suffix) {
        int lastSlash = suffix.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return suffix.substring(0, lastSlash).replace('/', '.');
    }

    private String rewritePackageDeclaration(String original, String expectedPackage) {
        String newline = original.contains("\r\n") ? "\r\n" : "\n";
        String[] lines = original.split("\\R", -1);
        List<String> updated = new ArrayList<>(Arrays.asList(lines));
        int packageIndex = -1;
        for (int i = 0; i < updated.size(); i++) {
            String trimmed = updated.get(i).trim();
            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                packageIndex = i;
                break;
            }
        }

        if (expectedPackage.isBlank()) {
            if (packageIndex >= 0) {
                updated.remove(packageIndex);
                if (packageIndex < updated.size() && updated.get(packageIndex).trim().isEmpty()) {
                    updated.remove(packageIndex);
                }
            }
            return String.join(newline, updated);
        }

        String packageLine = "package " + expectedPackage + ";";
        if (packageIndex >= 0) {
            updated.set(packageIndex, packageLine);
            return String.join(newline, updated);
        }

        updated.add(0, "");
        updated.add(0, packageLine);
        return String.join(newline, updated);
    }

    private String normalizeJavaType(String rawType) {
        if (rawType == null || rawType.isBlank()) {
            return "Object";
        }
        String normalized = rawType.trim();
        if (normalized.startsWith("final ")) {
            normalized = normalized.substring("final ".length()).trim();
        }
        return normalized;
    }

    private RepairMemoryStore.TemplateMatch bestTemplateMatch(
            TaskEnvelope task,
            String repairKind,
            Path relativePath,
            String symbolName,
            VerifierDiagnostic diagnostic) {
        if (repairMemoryStore == null) {
            return RepairMemoryStore.TemplateMatch.none();
        }
        StringBuilder context = new StringBuilder();
        if (relativePath != null) {
            context.append(relativePath).append(' ');
        }
        if (symbolName != null) {
            context.append(symbolName).append(' ');
        }
        if (diagnostic != null) {
            context.append(diagnostic.getMessage()).append(' ').append(diagnostic.getRaw());
        }
        return repairMemoryStore.bestTemplate(task, repairKind, context.toString());
    }

    private String templateAttribute(RepairMemoryStore.TemplateMatch match, String key) {
        if (match == null || !match.matched() || match.template().attributes() == null) {
            return null;
        }
        return match.template().attributes().get(key);
    }

    private List<String> resolveSignatureRequiredTypes(
            SignatureHint signatureHint,
            RepairMemoryStore.TemplateMatch templateMatch) {
        if (signatureHint != null && !signatureHint.requiredTypes().isEmpty()) {
            return signatureHint.requiredTypes();
        }
        String requiredTypes = templateAttribute(templateMatch, "requiredTypes");
        if (requiredTypes != null && !requiredTypes.isBlank()) {
            return List.of(requiredTypes.split("\\|"));
        }
        String defaultArgs = templateAttribute(templateMatch, "defaultArgs");
        if (defaultArgs == null || defaultArgs.isBlank()) {
            return List.of();
        }
        List<String> inferred = new ArrayList<>();
        for (String argument : defaultArgs.split("\\|")) {
            inferred.add(inferTypeFromLiteral(argument));
        }
        return inferred;
    }

    private String inferTypeFromLiteral(String literal) {
        String trimmed = literal == null ? "" : literal.trim();
        if (trimmed.isBlank()) {
            return "Object";
        }
        if ("\"\"".equals(trimmed) || (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return "String";
        }
        if ("false".equals(trimmed) || "true".equals(trimmed)) {
            return "boolean";
        }
        if ("'\\0'".equals(trimmed) || (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return "char";
        }
        if (trimmed.endsWith("f")) {
            return "float";
        }
        if (trimmed.endsWith("d")) {
            return "double";
        }
        if (trimmed.matches("-?\\d+")) {
            return "int";
        }
        return "Object";
    }

    private SanitizedSource sanitizeJavaSource(String original, String failureText) {
        String working = original == null ? "" : original;
        List<String> actions = new ArrayList<>();

        String withoutBom = working.replace("\uFEFF", "").replace("\u0000", "");
        if (!withoutBom.equals(working)) {
            actions.add("strip-bom-nul");
            working = withoutBom;
        }

        boolean parserNoiseLikely = failureText.contains("illegal character")
            || failureText.contains("class, interface, enum, or record expected")
            || working.contains("```")
            || working.contains("~~~");
        if (parserNoiseLikely) {
            List<String> keptLines = new ArrayList<>();
            boolean removedNoise = false;
            for (String line : working.split("\\R", -1)) {
                String trimmed = line.trim();
                String lowered = trimmed.toLowerCase(Locale.ROOT);
                if (trimmed.startsWith("```") || trimmed.startsWith("~~~") || "java".equals(lowered)) {
                    removedNoise = true;
                    continue;
                }
                keptLines.add(line);
            }
            if (removedNoise) {
                actions.add("drop-markdown-fences");
                working = String.join(System.lineSeparator(), keptLines);
            }

            int firstCodeIndex = firstLikelyCodeLine(working);
            if (firstCodeIndex > 0) {
                String[] lines = working.split("\\R", -1);
                List<String> stripped = new ArrayList<>();
                for (int i = firstCodeIndex; i < lines.length; i++) {
                    stripped.add(lines[i]);
                }
                String strippedText = String.join(System.lineSeparator(), stripped);
                if (!strippedText.equals(working)) {
                    actions.add("strip-leading-narrative");
                    working = strippedText;
                }
            }
        }

        return new SanitizedSource(working, actions);
    }

    private int firstLikelyCodeLine(String content) {
        String[] lines = content.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                continue;
            }
            String lowered = trimmed.toLowerCase(Locale.ROOT);
            if (lowered.startsWith("package ")
                || lowered.startsWith("import ")
                || lowered.startsWith("public ")
                || lowered.startsWith("final ")
                || lowered.startsWith("class ")
                || lowered.startsWith("interface ")
                || lowered.startsWith("@")) {
                return i;
            }
        }
        return 0;
    }

    private int unmatchedOpenBraces(String content) {
        int open = 0;
        int close = 0;
        for (char c : content.toCharArray()) {
            if (c == '{') {
                open++;
            } else if (c == '}') {
                close++;
            }
        }
        return Math.max(0, open - close);
    }

    private String buildClosingBraces(int braceDelta) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        for (int i = 0; i < braceDelta; i++) {
            builder.append('}').append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String classNameFor(Path sourcePath) {
        String fileName = sourcePath.getFileName().toString();
        return fileName.endsWith(".java") ? fileName.substring(0, fileName.length() - 5) : fileName;
    }

    private List<String> buildRepairRisks(String repairType, List<CorpusMutationSeed> matchingSeeds) {
        List<String> risks = new ArrayList<>();
        risks.add("Bounded verifier-guided repair: " + repairType);
        if (!matchingSeeds.isEmpty()) {
            risks.add("Informed by distilled corpus seeds");
        }
        return risks;
    }

    private List<String> evidenceTagsForSeeds(List<CorpusMutationSeed> seeds) {
        Set<String> tags = new LinkedHashSet<>();
        for (CorpusMutationSeed seed : seeds) {
            tags.add("corpus." + seed.getLabel().getSlug());
            if (seed.isCausallyGrounded()) {
                tags.add("corpus.causal");
            }
        }
        return new ArrayList<>(tags);
    }

    private PdfDistillationChunk.Label labelFromSourceId(String sourceId) {
        if (sourceId == null) {
            return null;
        }
        String normalized = sourceId.replace('\\', '/').toLowerCase(Locale.ROOT);
        if (normalized.endsWith("-failure.md")) {
            return PdfDistillationChunk.Label.FAILURE;
        }
        if (normalized.endsWith("-test-idea.md")) {
            return PdfDistillationChunk.Label.TEST_IDEA;
        }
        return null;
    }

    private Path resolveToWorkspace(Path path) {
        return path.isAbsolute() ? path.normalize() : configuration.getWorkspaceRoot().resolve(path).normalize();
    }

    private String slugify(String input) {
        String lowered = input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        String compact = lowered.replaceAll("^-+", "").replaceAll("-+$", "");
        return compact.isBlank() ? "task" : compact;
    }

    private List<MutationProposal> deduplicateProposals(List<MutationProposal> proposals) {
        Map<String, MutationProposal> unique = new LinkedHashMap<>();
        for (MutationProposal proposal : proposals) {
            String key = proposal.getTitle() + "::" + proposal.getTargetPaths().stream()
                .map(this::normalizePathString)
                .sorted()
                .toList();
            unique.putIfAbsent(key, proposal);
        }
        return new ArrayList<>(unique.values());
    }

    private String normalizePathString(Path path) {
        return path == null ? "" : path.toString().replace('\\', '/');
    }

    private String truncateInline(String input, int maxLength) {
        String compact = input == null ? "" : input.replaceAll("\\s+", " ").trim();
        if (compact.length() <= maxLength) {
            return compact;
        }
        return compact.substring(0, maxLength);
    }

    private record SanitizedSource(String content, List<String> actions) {
        public boolean changed() {
            return actions != null && !actions.isEmpty();
        }
    }

    private record ParsedSymbol(
            String kind,
            String name,
            String rawDescriptor,
            int parameterCount,
            String locationKind,
            String locationName) {
        public boolean isTypeLike() {
            return "class".equals(kind)
                || "interface".equals(kind)
                || "enum".equals(kind)
                || "record".equals(kind);
        }
    }

    private record SignatureHint(String methodName, List<String> requiredTypes, String foundSummary) {
    }

    private record TypeReference(Path path, String packageName, String simpleName) {
    }

    private record SourceContext(Path path, String content) {
    }

    private record ConstructorPlan(int parameterCount) {
    }

    private record SynthesizedMethodPlan(String name, int parameterCount, String returnType, boolean staticMethod) {
    }

    private record SynthesizedFieldPlan(String name, String type, boolean staticField) {
    }

    private record TypeSynthesisPlan(
            String packageName,
            String simpleName,
            Path relativeTargetPath,
            List<ConstructorPlan> constructors,
            List<SynthesizedMethodPlan> methods,
            List<SynthesizedFieldPlan> fields) {
    }

    private record TypeSynthesisRequest(
            VerifierDiagnostic diagnostic,
            TypeSynthesisPlan plan,
            String fqcn) {
    }

    private record MethodContext(String returnType, boolean staticContext) {
    }
}
