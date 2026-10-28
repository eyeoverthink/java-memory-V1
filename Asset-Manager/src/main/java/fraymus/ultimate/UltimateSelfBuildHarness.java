package fraymus.ultimate;

import fraymus.core.SelfBuilder;
import fraymus.facade.SelfCodeEngine;
import fraymus.neural.AeonWillowCrusher;
import fraymus.ollama.Transmudder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * Self-build loop that uses Fraynix-native coding surfaces to generate,
 * validate, hot-compile, execute, and judge candidate code artifacts.
 */
final class UltimateSelfBuildHarness {

    private static final DateTimeFormatter STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT).withZone(java.time.ZoneOffset.UTC);
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\bpackage\\s+([A-Za-z0-9_.]+)\\s*;");
    private static final Pattern CLASS_PATTERN = Pattern.compile("\\bclass\\s+([A-Za-z_][A-Za-z0-9_]*)\\b");
    private static final String REVIEW_REQUEST_FILE = "review-request.properties";

    private final Path assetManagerDir;
    private final SelfCodeEngine selfCodeEngine = new SelfCodeEngine();
    private final ProofBatteryRunner proofBatteryRunner;

    UltimateSelfBuildHarness(Path assetManagerDir) {
        this(assetManagerDir, null);
    }

    UltimateSelfBuildHarness(Path assetManagerDir, ProofBatteryRunner proofBatteryRunner) {
        this.assetManagerDir = assetManagerDir.toAbsolutePath().normalize();
        this.proofBatteryRunner = proofBatteryRunner == null
            ? new DefaultProofBatteryRunner(this.assetManagerDir)
            : proofBatteryRunner;
    }

    SelfBuildResult run(String goal) {
        String trimmedGoal = goal == null ? "" : goal.trim();
        if (trimmedGoal.isEmpty()) {
            throw new IllegalArgumentException("Goal must not be empty.");
        }

        String slug = slugify(trimmedGoal);
        String stamp = STAMP.format(Instant.now());
        Path runRoot = assetManagerDir.resolve("generated").resolve("ultimate-selfbuild").resolve(stamp + "-" + slug);
        Path sourceRoot = runRoot.resolve("src/main/java");
        Path outputRoot = runRoot.resolve("target/classes");
        Path reportPath = runRoot.resolve("selfbuild-report.md");

        try {
            Files.createDirectories(sourceRoot);
            Files.createDirectories(outputRoot);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create selfbuild workspace: " + e.getMessage(), e);
        }

        String packageName = "fraymus.generated.selfbuild.loop";
        SelfBuilder builder = new SelfBuilder(sourceRoot.toString() + "/", outputRoot.toString() + "/");

        List<CandidateSpec> specs = new ArrayList<>();
        specs.add(candidate("builder-base", "SelfBuilder seed organism",
            asciiSafe(builder.generateSpecialistSource(packageName, "SelfBuildBaseCandidate", trimmedGoal))));
        specs.add(candidate("phi-transform", "SelfCode phi-transformed candidate",
            asciiSafe(selfCodeEngine.phiTransform(
                asciiSafe(builder.generateSpecialistSource(packageName, "SelfBuildPhiCandidate", trimmedGoal))))));
        specs.add(candidate("knowledge-anchor", "SelfCode knowledge-anchored candidate",
            asciiSafe(injectKnowledgeAnchors(
                asciiSafe(builder.generateSpecialistSource(packageName, "SelfBuildAnchorCandidate", trimmedGoal)),
                trimmedGoal))));
        specs.add(candidate("stochastic-mutate", "SelfCode stochastic mutation candidate",
            asciiSafe(selfCodeEngine.mutate(
                asciiSafe(builder.generateSpecialistSource(packageName, "SelfBuildMutantCandidate", trimmedGoal))))));

        List<CandidateResult> results = new ArrayList<>();
        for (CandidateSpec spec : specs) {
            results.add(evaluate(builder, runRoot, spec, trimmedGoal));
        }

        CandidateResult winner = results.stream()
            .max(Comparator.comparingInt(CandidateResult::score)
                .thenComparing(CandidateResult::compileSuccess)
                .thenComparing(CandidateResult::executeSuccess))
            .orElseThrow();

        selfCodeEngine.store(winner.source(), "SELFBUILD,WINNER," + slug.toUpperCase(Locale.ROOT));
        selfCodeEngine.inject(winner.source());

        writeReport(reportPath, trimmedGoal, runRoot, results, winner, "generated-organism", trimmedGoal);

        return new SelfBuildResult(trimmedGoal, runRoot, reportPath, results, winner);
    }

    SelfBuildResult runAgainstSource(Path targetFile, String goal, String targetLabel) {
        Path normalizedTarget = targetFile.toAbsolutePath().normalize();
        if (!Files.exists(normalizedTarget)) {
            throw new IllegalArgumentException("Target source does not exist: " + normalizedTarget);
        }

        String trimmedGoal = goal == null || goal.isBlank()
            ? "stabilize target source under Fraynix selfbuild loop"
            : goal.trim();

        String slug = slugify(trimmedGoal + "-" + normalizedTarget.getFileName());
        String stamp = STAMP.format(Instant.now());
        Path runRoot = assetManagerDir.resolve("generated").resolve("ultimate-selfbuild").resolve(stamp + "-" + slug);
        Path sourceRoot = runRoot.resolve("src/main/java");
        Path outputRoot = runRoot.resolve("target/classes");
        Path reportPath = runRoot.resolve("selfbuild-report.md");

        try {
            Files.createDirectories(sourceRoot);
            Files.createDirectories(outputRoot);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create selfbuild workspace: " + e.getMessage(), e);
        }

        String original;
        try {
            original = Files.readString(normalizedTarget, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not read target source: " + e.getMessage(), e);
        }

        SourceIdentity identity = extractIdentity(original);
        List<CandidateSpec> specs = new ArrayList<>();
        specs.add(candidate("baseline-copy", "Original source baseline", original));
        specs.add(candidate("knowledge-anchor", "Goal-anchored source overlay",
            anchorExistingSource(original, trimmedGoal, targetLabel)));
        specs.add(candidate("phi-transform", "SelfCode phi-transformed source",
            asciiSafe(selfCodeEngine.phiTransform(asciiSafe(original)))));
        specs.add(candidate("stochastic-mutate", "SelfCode stochastic source mutation",
            asciiSafe(selfCodeEngine.mutate(asciiSafe(original)))));

        List<CandidateResult> results = new ArrayList<>();
        for (CandidateSpec spec : specs) {
            results.add(evaluateExistingSource(runRoot, outputRoot, spec, identity));
        }

        CandidateResult winner = results.stream()
            .max(Comparator.comparingInt(CandidateResult::score)
                .thenComparing(CandidateResult::compileSuccess)
                .thenComparing(CandidateResult::identityPreserved))
            .orElseThrow();

        selfCodeEngine.store(winner.source(), "SELFBUILD,SOURCE_TARGET," + slug.toUpperCase(Locale.ROOT));
        selfCodeEngine.inject(winner.source());

        writeReport(reportPath, trimmedGoal, runRoot, results, winner,
            "source-target",
            targetLabel + " -> " + normalizedTarget.toString().replace('\\', '/'));

        return new SelfBuildResult(trimmedGoal, runRoot, reportPath, results, winner);
    }

    ComparativeSelfBuildResult runAgainstSourceSet(List<SourceTarget> targets, String goal, String selector) {
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("Target set must not be empty.");
        }

        String trimmedGoal = goal == null || goal.isBlank()
            ? "compare target source set under Fraynix selfbuild loop"
            : goal.trim();
        String stamp = STAMP.format(Instant.now());
        String slug = slugify(trimmedGoal + "-" + selector);
        Path runRoot = assetManagerDir.resolve("generated").resolve("ultimate-selfbuild").resolve(stamp + "-comparative-" + slug);
        Path reportPath = runRoot.resolve("comparative-selfbuild-report.md");

        try {
            Files.createDirectories(runRoot);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create comparative selfbuild workspace: " + e.getMessage(), e);
        }

        List<RankedSourceResult> results = new ArrayList<>();
        for (SourceTarget target : targets) {
            SelfBuildResult result = runAgainstSource(target.path(), trimmedGoal, target.label());
            results.add(new RankedSourceResult(target.label(), target.path(), result));
        }

        results.sort(
            Comparator.comparingInt((RankedSourceResult result) -> result.result().winner().score()).reversed()
                .thenComparing(Comparator.comparingInt((RankedSourceResult result) -> compileSuccessCount(result.result())).reversed())
                .thenComparing(result -> result.label().toLowerCase(Locale.ROOT))
        );

        RankedSourceResult best = results.get(0);
        writeComparativeReport(reportPath, trimmedGoal, selector, results, best);
        return new ComparativeSelfBuildResult(trimmedGoal, selector, runRoot, reportPath, results, best);
    }

    PromotionResult promoteAgainstSource(Path targetFile, String goal, String targetLabel) {
        SelfBuildResult result = runAgainstSource(targetFile, goal, targetLabel);
        PromotionDecision decision = decidePromotion(result);
        Path promotionRoot = result.runRoot().resolve("promotion");
        Path reportPath = promotionRoot.resolve("promotion-report.md");
        Path patchPath = promotionRoot.resolve("fraynix-promotion.patch");
        String diffLabel = diffLabel(targetFile, targetLabel);
        Path originalSnapshot = sourcePath(
            promotionRoot.resolve("original"),
            decision.baseline().packageName(),
            decision.baseline().className()
        );
        Path promotedSnapshot = null;

        try {
            Files.createDirectories(originalSnapshot.getParent());
            Files.writeString(originalSnapshot, decision.baseline().source(), StandardCharsets.UTF_8);

            if (decision.promotable() && decision.promotedCandidate() != null) {
                promotedSnapshot = sourcePath(
                    promotionRoot.resolve("promoted"),
                    decision.promotedCandidate().packageName(),
                    decision.promotedCandidate().className()
                );
                Files.createDirectories(promotedSnapshot.getParent());
                Files.writeString(promotedSnapshot, decision.promotedCandidate().source(), StandardCharsets.UTF_8);
                Files.writeString(
                    patchPath,
                    buildUnifiedDiff(
                        "a/" + diffLabel,
                        "b/" + diffLabel,
                        decision.baseline().source(),
                        decision.promotedCandidate().source()
                    ),
                    StandardCharsets.UTF_8
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not write promotion artifacts: " + e.getMessage(), e);
        }

        writePromotionReport(reportPath, result, targetLabel, decision, originalSnapshot, promotedSnapshot, patchPath);
        return new PromotionResult(result.goal(), targetLabel, result.runRoot(), reportPath, patchPath, originalSnapshot, promotedSnapshot, result, decision);
    }

    ComparativePromotionResult promoteAgainstSourceSet(List<SourceTarget> targets, String goal, String selector) {
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("Target set must not be empty.");
        }

        String trimmedGoal = goal == null || goal.isBlank()
            ? "promote best target source improvements under Fraynix selfbuild loop"
            : goal.trim();
        String stamp = STAMP.format(Instant.now());
        String slug = slugify(trimmedGoal + "-" + selector);
        Path runRoot = assetManagerDir.resolve("generated").resolve("ultimate-selfbuild").resolve(stamp + "-promote-" + slug);
        Path reportPath = runRoot.resolve("comparative-promotion-report.md");

        try {
            Files.createDirectories(runRoot);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create comparative promotion workspace: " + e.getMessage(), e);
        }

        List<RankedPromotionResult> results = new ArrayList<>();
        for (SourceTarget target : targets) {
            results.add(new RankedPromotionResult(target.label(), target.path(), promoteAgainstSource(target.path(), trimmedGoal, target.label())));
        }

        results.sort(
            Comparator.comparing((RankedPromotionResult result) -> result.promotion().decision().promotable(), Comparator.reverseOrder())
                .thenComparing(Comparator.comparingInt((RankedPromotionResult result) -> result.promotion().decision().scoreDelta()).reversed())
                .thenComparing(Comparator.comparingInt((RankedPromotionResult result) -> result.promotion().selfBuild().winner().score()).reversed())
                .thenComparing(result -> result.label().toLowerCase(Locale.ROOT))
        );

        RankedPromotionResult best = results.get(0);
        writeComparativePromotionReport(reportPath, trimmedGoal, selector, results, best);
        return new ComparativePromotionResult(trimmedGoal, selector, runRoot, reportPath, results, best);
    }

    ProofDrivenPromotionResult provePromoteAgainstSource(Path targetFile, String goal, String targetLabel) {
        PromotionResult promotion = promoteAgainstSource(targetFile, goal, targetLabel);
        Path proveRoot = promotion.runRoot().resolve("prove-promotion");
        Path reportPath = proveRoot.resolve("prove-promotion-report.md");
        Path evidencePath = promotion.promotedSnapshot() != null ? promotion.promotedSnapshot() : promotion.originalSnapshot();

        ProofBatteryResult proofBattery = promotion.decision().promotable() && promotion.promotedSnapshot() != null
            ? proofBatteryRunner.run(proveRoot, evidencePath, targetLabel, promotion.goal())
            : ProofBatteryResult.skipped(
                proveRoot,
                evidencePath,
                "Base promotion was not promotable, so the Fraynix proof battery did not run."
            );

        boolean approved = promotion.decision().promotable() && proofBattery.allPassed();
        writeProofPromotionReport(reportPath, promotion, proofBattery, approved);
        return new ProofDrivenPromotionResult(
            promotion.goal(),
            targetLabel,
            promotion.runRoot(),
            reportPath,
            promotion,
            proofBattery,
            approved
        );
    }

    ComparativeProofPromotionResult provePromoteAgainstSourceSet(List<SourceTarget> targets, String goal, String selector) {
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("Target set must not be empty.");
        }

        String trimmedGoal = goal == null || goal.isBlank()
            ? "prove and promote best target source improvements under Fraynix selfbuild loop"
            : goal.trim();
        String stamp = STAMP.format(Instant.now());
        String slug = slugify(trimmedGoal + "-" + selector);
        Path runRoot = assetManagerDir.resolve("generated").resolve("ultimate-selfbuild").resolve(stamp + "-prove-promote-" + slug);
        Path reportPath = runRoot.resolve("comparative-prove-promotion-report.md");

        try {
            Files.createDirectories(runRoot);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create comparative prove-promotion workspace: " + e.getMessage(), e);
        }

        List<RankedProofPromotionResult> results = new ArrayList<>();
        for (SourceTarget target : targets) {
            results.add(new RankedProofPromotionResult(
                target.label(),
                target.path(),
                provePromoteAgainstSource(target.path(), trimmedGoal, target.label())
            ));
        }

        results.sort(
            Comparator.comparing((RankedProofPromotionResult result) -> result.proofPromotion().approved(), Comparator.reverseOrder())
                .thenComparing(Comparator.comparingLong((RankedProofPromotionResult result) -> result.proofPromotion().proofBattery().passedCount()).reversed())
                .thenComparing(Comparator.comparingInt((RankedProofPromotionResult result) -> result.proofPromotion().basePromotion().decision().scoreDelta()).reversed())
                .thenComparing(result -> result.label().toLowerCase(Locale.ROOT))
        );

        RankedProofPromotionResult best = results.get(0);
        writeComparativeProofPromotionReport(reportPath, trimmedGoal, selector, results, best);
        return new ComparativeProofPromotionResult(trimmedGoal, selector, runRoot, reportPath, results, best);
    }

    ReviewRequest reviewPromoteAgainstSource(Path targetFile, String goal, String targetLabel) {
        ProofDrivenPromotionResult proofPromotion = provePromoteAgainstSource(targetFile, goal, targetLabel);
        Path normalizedTarget = targetFile.toAbsolutePath().normalize();
        Path reviewRoot = proofPromotion.runRoot().resolve("review");
        String reviewId = proofPromotion.runRoot().getFileName().toString();
        String status = proofPromotion.approved() ? "pending" : "blocked";
        String note = proofPromotion.approved()
            ? "Proof battery passed. Waiting for human approval before touching the live source tree."
            : "Proof battery did not approve this promotion, so it is visible but not reviewable.";

        ReviewRequest request = new ReviewRequest(
            reviewId,
            status,
            proofPromotion.goal(),
            targetLabel,
            normalizedTarget,
            proofPromotion.basePromotion().reportPath(),
            proofPromotion.reportPath(),
            proofPromotion.basePromotion().patchPath(),
            proofPromotion.basePromotion().promotedSnapshot(),
            Instant.now().toString(),
            "",
            note
        );
        writeReviewRequest(reviewRoot, request);
        return request;
    }

    ReviewResolution approveReview(String reviewId) {
        ReviewRequest request = requireReviewRequest(reviewId);
        if (!"pending".equalsIgnoreCase(request.status())) {
            return new ReviewResolution(request.reviewId(), request.status(), request.targetPath(), "Review is not pending.", null);
        }
        if (request.promotedSnapshot() == null || !Files.exists(request.promotedSnapshot())) {
            return new ReviewResolution(request.reviewId(), "blocked", request.targetPath(), "Promoted snapshot is missing.", null);
        }

        Path reviewRoot = request.provePromotionReportPath().getParent().resolve("review");
        Path backupPath = reviewRoot.resolve("live-source-backup.txt");
        try {
            Files.createDirectories(reviewRoot);
            if (Files.exists(request.targetPath())) {
                Files.copy(request.targetPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.copy(request.promotedSnapshot(), request.targetPath(), StandardCopyOption.REPLACE_EXISTING);
            ReviewRequest approved = request.withDecision("approved", "Human approved live writeback.", Instant.now().toString());
            writeReviewRequest(reviewRoot, approved);
            writeReviewDecision(reviewRoot, approved, "Approved and wrote the promoted snapshot into the live source tree.", backupPath);
            return new ReviewResolution(approved.reviewId(), approved.status(), approved.targetPath(), approved.note(), backupPath);
        } catch (Exception e) {
            return new ReviewResolution(request.reviewId(), "fault", request.targetPath(), e.getMessage(), backupPath);
        }
    }

    ReviewResolution rejectReview(String reviewId, String reason) {
        ReviewRequest request = requireReviewRequest(reviewId);
        Path reviewRoot = request.provePromotionReportPath().getParent().resolve("review");
        String note = reason == null || reason.isBlank()
            ? "Human rejected the review request."
            : "Human rejected the review request: " + reason.trim();
        ReviewRequest rejected = request.withDecision("rejected", note, Instant.now().toString());
        writeReviewRequest(reviewRoot, rejected);
        writeReviewDecision(reviewRoot, rejected, note, null);
        return new ReviewResolution(rejected.reviewId(), rejected.status(), rejected.targetPath(), rejected.note(), null);
    }

    List<ReviewRequest> listReviewRequests() {
        Path root = generatedSelfBuildRoot();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        List<ReviewRequest> requests = new ArrayList<>();
        try (var walk = Files.walk(root)) {
            walk.filter(path -> path.getFileName() != null && REVIEW_REQUEST_FILE.equals(path.getFileName().toString()))
                .forEach(path -> {
                    try {
                        requests.add(readReviewRequest(path));
                    } catch (Exception ignored) {
                        // Keep scanning even if one review file is malformed.
                    }
                });
        } catch (Exception ignored) {
            return List.of();
        }
        requests.sort(Comparator.comparing(ReviewRequest::createdAt).reversed());
        return List.copyOf(requests);
    }

    List<SessionArtifact> recentRunArtifacts(int limit) {
        Path root = generatedSelfBuildRoot();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        List<SessionArtifact> artifacts = new ArrayList<>();
        try (var dirs = Files.list(root)) {
            dirs.filter(Files::isDirectory)
                .sorted(Comparator.comparing((Path path) -> safeInstant(path.getFileName().toString())).reversed())
                .limit(Math.max(1, limit))
                .forEach(dir -> artifacts.add(summarizeRunArtifact(dir)));
        } catch (Exception ignored) {
            return List.of();
        }
        return List.copyOf(artifacts);
    }

    private CandidateResult evaluate(SelfBuilder builder, Path runRoot, CandidateSpec spec, String goal) {
        ValidationSignals validation = ValidationSignals.from(spec.source());
        String validateReport = selfCodeEngine.validate(spec.source());
        String fingerprint = selfCodeEngine.linkConcept(spec.source());
        Path sourceFile = sourcePath(runRoot.resolve("src/main/java"), spec.packageName(), spec.className());
        boolean goalAnchored = spec.source().contains("SELFBUILD_GOAL") || spec.source().contains("SELFBUILD_TARGET");

        boolean compileSuccess = false;
        boolean executeSuccess = false;
        boolean resonanceSuccess = false;
        String compileTranscript = "";
        String executeTranscript = "";
        String resonanceTranscript = "";
        String failure = "";

        try {
            Files.createDirectories(sourceFile.getParent());
            Files.writeString(sourceFile, spec.source(), StandardCharsets.UTF_8);

            Capture<Object> compileCapture = capture(() ->
                builder.evolveCode(spec.packageName() + "." + spec.className(), spec.source()));
            compileTranscript = mergeTranscript(compileCapture.stdout(), compileCapture.error());
            Object organism = compileCapture.value();
            compileSuccess = organism != null;

            if (organism != null) {
                Capture<Void> execCapture = capture(() -> {
                    invokeNoArg(organism, "execute");
                    return null;
                });
                executeTranscript = mergeTranscript(execCapture.stdout(), execCapture.error());
                executeSuccess = execCapture.error().isBlank();

                Capture<Object> resonanceCapture = capture(() -> invokeDoubleCompat(organism, "resonate", 100.0d));
                resonanceTranscript = mergeTranscript(resonanceCapture.stdout(), resonanceCapture.error())
                    + (resonanceCapture.value() != null ? "\nRESULT=" + resonanceCapture.value() : "");
                resonanceSuccess = resonanceCapture.error().isBlank();
            } else {
                failure = compileCapture.error().isBlank()
                    ? "Compilation did not produce a live organism."
                    : compileCapture.error().strip();
            }
        } catch (Exception e) {
            failure = e.getMessage();
        }

        int score = 0;
        if (validation.bracesBalanced()) score += 10;
        if (validation.parensBalanced()) score += 10;
        if (validation.bracketsBalanced()) score += 10;
        if (validation.hasClass()) score += 10;
        if (validation.hasMethod()) score += 10;
        if (compileSuccess) score += 30;
        if (executeSuccess) score += 15;
        if (resonanceSuccess) score += 5;

        return new CandidateResult(
            spec.id(),
            spec.title(),
            spec.packageName(),
            spec.className(),
            spec.source(),
            sourceFile,
            fingerprint,
            validateReport,
            validation,
            true,
            goalAnchored,
            compileSuccess,
            executeSuccess,
            resonanceSuccess,
            compileTranscript,
            executeTranscript,
            resonanceTranscript,
            failure,
            score
        );
    }

    private CandidateResult evaluateExistingSource(Path runRoot, Path outputRoot, CandidateSpec spec, SourceIdentity originalIdentity) {
        ValidationSignals validation = ValidationSignals.from(spec.source());
        String validateReport = selfCodeEngine.validate(spec.source());
        String fingerprint = selfCodeEngine.linkConcept(spec.source());
        Path sourceFile = sourcePath(runRoot.resolve("src/main/java"), spec.packageName(), spec.className());
        boolean goalAnchored = spec.source().contains("SELFBUILD_GOAL") || spec.source().contains("SELFBUILD_TARGET");

        String compileTranscript = "";
        String failure = "";
        boolean compileSuccess = false;
        boolean identityPreserved =
            spec.packageName().equals(originalIdentity.packageName()) &&
            spec.className().equals(originalIdentity.className());

        try {
            Files.createDirectories(sourceFile.getParent());
            Files.writeString(sourceFile, spec.source(), StandardCharsets.UTF_8);
            CompileResult result = compileAgainstRuntimeClasspath(sourceFile, outputRoot);
            compileTranscript = result.transcript();
            compileSuccess = result.success();
            if (!compileSuccess) {
                failure = result.transcript().isBlank()
                    ? "Compilation failed for source target."
                    : result.transcript().strip();
            }
        } catch (Exception e) {
            failure = e.getMessage();
        }

        int score = 0;
        if (validation.bracesBalanced()) score += 10;
        if (validation.parensBalanced()) score += 10;
        if (validation.bracketsBalanced()) score += 10;
        if (validation.hasClass()) score += 10;
        if (validation.hasMethod()) score += 10;
        if (identityPreserved) score += 10;
        if (compileSuccess) score += 40;

        return new CandidateResult(
            spec.id(),
            spec.title(),
            spec.packageName(),
            spec.className(),
            spec.source(),
            sourceFile,
            fingerprint,
            validateReport,
            validation,
            identityPreserved,
            goalAnchored,
            compileSuccess,
            false,
            false,
            compileTranscript,
            "",
            "",
            failure,
            score
        );
    }

    private CompileResult compileAgainstRuntimeClasspath(Path sourceFile, Path outputRoot) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return new CompileResult(false, "No system compiler available.");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StringWriter writer = new StringWriter();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            Files.createDirectories(outputRoot);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputRoot.toFile()));
            Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjects(sourceFile.toFile());
            List<String> options = List.of(
                "-classpath", System.getProperty("java.class.path", ""),
                "-encoding", "UTF-8"
            );
            JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, diagnostics, options, null, units);
            boolean success = Boolean.TRUE.equals(task.call());

            StringBuilder sb = new StringBuilder();
            String writerOutput = writer.toString().strip();
            if (!writerOutput.isBlank()) {
                sb.append(writerOutput).append("\n");
            }
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                sb.append("Line ").append(diagnostic.getLineNumber()).append(": ")
                    .append(diagnostic.getMessage(Locale.ROOT)).append("\n");
            }
            if (success && sb.isEmpty()) {
                sb.append("Compilation complete against live runtime classpath.");
            }
            return new CompileResult(success, sb.toString().strip());
        } catch (Exception e) {
            return new CompileResult(false, e.getMessage());
        }
    }

    private void writeReport(Path reportPath,
                             String goal,
                             Path runRoot,
                             List<CandidateResult> results,
                             CandidateResult winner,
                             String mode,
                             String targetSummary) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Judge Report\n\n");
        sb.append("Goal: `").append(goal).append("`\n");
        sb.append("Generated: `").append(Instant.now()).append("`\n");
        sb.append("Workspace: `").append(runRoot.toString().replace('\\', '/')).append("`\n");
        sb.append("Engine: `SelfBuilder + SelfCodeEngine`\n\n");
        sb.append("Mode: `").append(mode).append("`\n");
        sb.append("Target: `").append(targetSummary).append("`\n\n");

        sb.append("## Winner\n\n");
        sb.append("- Candidate: `").append(winner.id()).append("`\n");
        sb.append("- Title: ").append(winner.title()).append("\n");
        sb.append("- Score: `").append(winner.score()).append("`\n");
        sb.append("- Fingerprint: `").append(winner.fingerprint()).append("`\n");
        sb.append("- Source: [").append(winner.className()).append(".java](")
            .append(winner.sourceFile().toString().replace('\\', '/')).append(")\n\n");

        sb.append("## Scoreboard\n\n");
        for (CandidateResult result : results) {
            sb.append("- `").append(result.id()).append("` score=`").append(result.score()).append("`")
                .append(" compile=").append(result.compileSuccess())
                .append(" execute=").append(result.executeSuccess())
                .append(" resonate=").append(result.resonanceSuccess())
                .append(" goalAnchored=").append(result.goalAnchored())
                .append(" fingerprint=`").append(result.fingerprint()).append("`\n");
        }
        sb.append("\n");

        for (CandidateResult result : results) {
            sb.append("## Candidate: ").append(result.id()).append("\n\n");
            sb.append("- Title: ").append(result.title()).append("\n");
            sb.append("- Class: `").append(result.packageName()).append(".").append(result.className()).append("`\n");
            sb.append("- Source: [").append(result.className()).append(".java](")
                .append(result.sourceFile().toString().replace('\\', '/')).append(")\n");
            sb.append("- Score: `").append(result.score()).append("`\n");
            sb.append("- Identity preserved: `").append(result.identityPreserved()).append("`\n");
            sb.append("- Goal anchored: `").append(result.goalAnchored()).append("`\n");
            sb.append("- Compile: `").append(result.compileSuccess()).append("`\n");
            sb.append("- Execute: `").append(result.executeSuccess()).append("`\n");
            sb.append("- Resonance: `").append(result.resonanceSuccess()).append("`\n");
            sb.append("- Validation signals: braces=").append(result.validation().bracesBalanced())
                .append(", parens=").append(result.validation().parensBalanced())
                .append(", brackets=").append(result.validation().bracketsBalanced())
                .append(", class=").append(result.validation().hasClass())
                .append(", method=").append(result.validation().hasMethod()).append("\n");
            if (!result.failure().isBlank()) {
                sb.append("- Failure: `").append(result.failure().replace("`", "'")).append("`\n");
            }
            sb.append("\n### SelfCode Validate Report\n\n```text\n")
                .append(result.validateReport().strip()).append("\n```\n\n");
            sb.append("### Compile Transcript\n\n```text\n")
                .append(result.compileTranscript().isBlank() ? "(none)" : result.compileTranscript().strip())
                .append("\n```\n\n");
            sb.append("### Execute Transcript\n\n```text\n")
                .append(result.executeTranscript().isBlank() ? "(none)" : result.executeTranscript().strip())
                .append("\n```\n\n");
            sb.append("### Resonance Transcript\n\n```text\n")
                .append(result.resonanceTranscript().isBlank() ? "(none)" : result.resonanceTranscript().strip())
                .append("\n```\n\n");
        }

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write selfbuild report: " + e.getMessage(), e);
        }
    }

    private void writeComparativeReport(Path reportPath,
                                        String goal,
                                        String selector,
                                        List<RankedSourceResult> results,
                                        RankedSourceResult best) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Comparative Report\n\n");
        sb.append("Goal: `").append(goal).append("`\n");
        sb.append("Selector: `").append(selector).append("`\n");
        sb.append("Generated: `").append(Instant.now()).append("`\n");
        sb.append("Target count: `").append(results.size()).append("`\n\n");

        sb.append("## Best Target\n\n");
        sb.append("- Target: `").append(best.label()).append("`\n");
        sb.append("- Path: `").append(best.path().toString().replace('\\', '/')).append("`\n");
        sb.append("- Winner: `").append(best.result().winner().id()).append("`\n");
        sb.append("- Winner score: `").append(best.result().winner().score()).append("`\n");
        sb.append("- Target report: [selfbuild-report.md](")
            .append(best.result().reportPath().toString().replace('\\', '/')).append(")\n\n");

        sb.append("## Comparative Ranking\n\n");
        int rank = 1;
        for (RankedSourceResult result : results) {
            sb.append(rank++).append(". `").append(result.label()).append("`")
                .append(" winner=`").append(result.result().winner().id()).append("`")
                .append(" score=`").append(result.result().winner().score()).append("`")
                .append(" compileWins=`").append(compileSuccessCount(result.result())).append("`")
                .append(" report=[open](").append(result.result().reportPath().toString().replace('\\', '/')).append(")\n");
        }
        sb.append("\n## Target Summaries\n\n");
        for (RankedSourceResult result : results) {
            sb.append("### ").append(result.label()).append("\n\n");
            sb.append("- Path: `").append(result.path().toString().replace('\\', '/')).append("`\n");
            sb.append("- Winner: `").append(result.result().winner().id()).append("`\n");
            sb.append("- Score: `").append(result.result().winner().score()).append("`\n");
            sb.append("- Compile wins: `").append(compileSuccessCount(result.result())).append(" / ")
                .append(result.result().candidates().size()).append("`\n");
            sb.append("- Winning candidate report: [selfbuild-report.md](")
                .append(result.result().reportPath().toString().replace('\\', '/')).append(")\n\n");
        }

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write comparative selfbuild report: " + e.getMessage(), e);
        }
    }

    private void writePromotionReport(Path reportPath,
                                      SelfBuildResult result,
                                      String targetLabel,
                                      PromotionDecision decision,
                                      Path originalSnapshot,
                                      Path promotedSnapshot,
                                      Path patchPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Promotion Report\n\n");
        sb.append("Goal: `").append(result.goal()).append("`\n");
        sb.append("Target: `").append(targetLabel).append("`\n");
        sb.append("Generated: `").append(Instant.now()).append("`\n");
        sb.append("Promotable: `").append(decision.promotable()).append("`\n");
        sb.append("Reason: ").append(decision.reason()).append("\n");
        sb.append("Selfbuild report: [selfbuild-report.md](")
            .append(result.reportPath().toString().replace('\\', '/')).append(")\n");
        sb.append("Original snapshot: [").append(originalSnapshot.getFileName()).append("](")
            .append(originalSnapshot.toString().replace('\\', '/')).append(")\n");
        if (promotedSnapshot != null) {
            sb.append("Promoted snapshot: [").append(promotedSnapshot.getFileName()).append("](")
                .append(promotedSnapshot.toString().replace('\\', '/')).append(")\n");
        }
        if (decision.promotable() && promotedSnapshot != null) {
            sb.append("Patch artifact: [fraynix-promotion.patch](")
                .append(patchPath.toString().replace('\\', '/')).append(")\n");
        } else {
            sb.append("Patch artifact: `(none)`\n");
        }
        sb.append("\n## Baseline\n\n");
        sb.append("- Candidate: `").append(decision.baseline().id()).append("`\n");
        sb.append("- Score: `").append(decision.baseline().score()).append("`\n");
        sb.append("- Goal anchored: `").append(decision.baseline().goalAnchored()).append("`\n");
        sb.append("- Compile: `").append(decision.baseline().compileSuccess()).append("`\n");
        sb.append("- Identity preserved: `").append(decision.baseline().identityPreserved()).append("`\n\n");

        if (decision.promotedCandidate() != null) {
            sb.append("## Promoted Candidate\n\n");
            sb.append("- Candidate: `").append(decision.promotedCandidate().id()).append("`\n");
            sb.append("- Score: `").append(decision.promotedCandidate().score()).append("`\n");
            sb.append("- Score delta: `").append(decision.scoreDelta()).append("`\n");
            sb.append("- Goal anchored: `").append(decision.promotedCandidate().goalAnchored()).append("`\n");
            sb.append("- Compile: `").append(decision.promotedCandidate().compileSuccess()).append("`\n");
            sb.append("- Identity preserved: `").append(decision.promotedCandidate().identityPreserved()).append("`\n");
            sb.append("- Source: [").append(decision.promotedCandidate().className()).append(".java](")
                .append(decision.promotedCandidate().sourceFile().toString().replace('\\', '/')).append(")\n\n");
        }

        sb.append("## Decision Trace\n\n");
        for (CandidateResult candidate : result.candidates()) {
            sb.append("- `").append(candidate.id()).append("`")
                .append(" score=`").append(candidate.score()).append("`")
                .append(" compile=`").append(candidate.compileSuccess()).append("`")
                .append(" identity=`").append(candidate.identityPreserved()).append("`")
                .append(" goalAnchored=`").append(candidate.goalAnchored()).append("`\n");
        }
        sb.append("\n");

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write promotion report: " + e.getMessage(), e);
        }
    }

    private void writeComparativePromotionReport(Path reportPath,
                                                 String goal,
                                                 String selector,
                                                 List<RankedPromotionResult> results,
                                                 RankedPromotionResult best) {
        long promotableCount = results.stream().filter(result -> result.promotion().decision().promotable()).count();

        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Comparative Promotion Report\n\n");
        sb.append("Goal: `").append(goal).append("`\n");
        sb.append("Selector: `").append(selector).append("`\n");
        sb.append("Generated: `").append(Instant.now()).append("`\n");
        sb.append("Target count: `").append(results.size()).append("`\n");
        sb.append("Promotable targets: `").append(promotableCount).append("`\n\n");

        sb.append("## Best Promotion Target\n\n");
        sb.append("- Target: `").append(best.label()).append("`\n");
        sb.append("- Path: `").append(best.path().toString().replace('\\', '/')).append("`\n");
        sb.append("- Promotable: `").append(best.promotion().decision().promotable()).append("`\n");
        sb.append("- Reason: ").append(best.promotion().decision().reason()).append("\n");
        sb.append("- Report: [promotion-report.md](")
            .append(best.promotion().reportPath().toString().replace('\\', '/')).append(")\n\n");

        sb.append("## Promotion Ranking\n\n");
        int rank = 1;
        for (RankedPromotionResult result : results) {
            sb.append(rank++).append(". `").append(result.label()).append("`")
                .append(" promotable=`").append(result.promotion().decision().promotable()).append("`")
                .append(" delta=`").append(result.promotion().decision().scoreDelta()).append("`")
                .append(" winner=`").append(result.promotion().selfBuild().winner().id()).append("`")
                .append(" report=[open](").append(result.promotion().reportPath().toString().replace('\\', '/')).append(")\n");
        }
        sb.append("\n## Target Promotion Summaries\n\n");
        for (RankedPromotionResult result : results) {
            sb.append("### ").append(result.label()).append("\n\n");
            sb.append("- Path: `").append(result.path().toString().replace('\\', '/')).append("`\n");
            sb.append("- Promotable: `").append(result.promotion().decision().promotable()).append("`\n");
            sb.append("- Reason: ").append(result.promotion().decision().reason()).append("\n");
            sb.append("- Score delta: `").append(result.promotion().decision().scoreDelta()).append("`\n");
            sb.append("- Promotion report: [promotion-report.md](")
                .append(result.promotion().reportPath().toString().replace('\\', '/')).append(")\n\n");
        }

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write comparative promotion report: " + e.getMessage(), e);
        }
    }

    private PromotionDecision decidePromotion(SelfBuildResult result) {
        CandidateResult baseline = result.candidates().stream()
            .filter(candidate -> "baseline-copy".equals(candidate.id()))
            .findFirst()
            .orElse(result.winner());

        CandidateResult promoted = result.candidates().stream()
            .filter(candidate -> !"baseline-copy".equals(candidate.id()))
            .filter(CandidateResult::compileSuccess)
            .filter(CandidateResult::identityPreserved)
            .filter(candidate -> candidate.score() > baseline.score()
                || (candidate.score() == baseline.score() && candidate.goalAnchored() && !baseline.goalAnchored()))
            .max(Comparator.comparingInt(CandidateResult::score)
                .thenComparing(CandidateResult::goalAnchored)
                .thenComparing(CandidateResult::compileSuccess))
            .orElse(null);

        if (promoted == null) {
            return new PromotionDecision(
                false,
                "No candidate beat the baseline or matched it with stronger Fraynix goal anchoring.",
                baseline,
                null,
                0
            );
        }

        int scoreDelta = promoted.score() - baseline.score();
        String reason = scoreDelta > 0
            ? "Candidate beat the baseline on Fraynix selfbuild score."
            : "Candidate matched the baseline while preserving identity and adding stronger Fraynix goal anchoring.";
        return new PromotionDecision(true, reason, baseline, promoted, scoreDelta);
    }

    private void writeProofPromotionReport(Path reportPath,
                                           PromotionResult promotion,
                                           ProofBatteryResult proofBattery,
                                           boolean approved) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Prove-Promotion Report\n\n");
        sb.append("Goal: `").append(promotion.goal()).append("`\n");
        sb.append("Target: `").append(promotion.targetLabel()).append("`\n");
        sb.append("Base promotable: `").append(promotion.decision().promotable()).append("`\n");
        sb.append("Proof approved: `").append(approved).append("`\n");
        sb.append("Base promotion report: [promotion-report.md](")
            .append(promotion.reportPath().toString().replace('\\', '/')).append(")\n");
        sb.append("Patch candidate: ")
            .append(promotion.decision().promotable() ? "[fraynix-promotion.patch](" + promotion.patchPath().toString().replace('\\', '/') + ")" : "`(none)`")
            .append("\n");
        sb.append("Evidence path: [proof target](")
            .append(proofBattery.evidencePath().toString().replace('\\', '/')).append(")\n\n");

        sb.append("## Base Decision\n\n");
        sb.append("- Reason: ").append(promotion.decision().reason()).append("\n");
        sb.append("- Score delta: `").append(promotion.decision().scoreDelta()).append("`\n");
        sb.append("- Candidate: `")
            .append(promotion.decision().promotedCandidate() == null
                ? "(none)"
                : promotion.decision().promotedCandidate().id() + " [" + promotion.decision().promotedCandidate().className() + "]")
            .append("`\n\n");

        sb.append("## Fraynix Proof Battery\n\n");
        if (proofBattery.skipped()) {
            sb.append("- Status: `skipped`\n");
            sb.append("- Reason: ").append(proofBattery.reason()).append("\n");
        } else {
            sb.append("- Checks passed: `").append(proofBattery.passedCount()).append(" / ").append(proofBattery.checks().size()).append("`\n");
            sb.append("- All passed: `").append(proofBattery.allPassed()).append("`\n\n");
            for (ProofCheckResult check : proofBattery.checks()) {
                sb.append("### ").append(check.title()).append("\n\n");
                sb.append("- Passed: `").append(check.passed()).append("`\n");
                sb.append("- Summary: ").append(check.summary()).append("\n");
                sb.append("- Transcript: [").append(check.id()).append(".txt](")
                    .append(check.transcriptPath().toString().replace('\\', '/')).append(")\n\n");
            }
        }

        sb.append("## Outcome\n\n");
        sb.append(approved
            ? "Fraynix approved this promotion after the proof battery passed."
            : "Fraynix withheld final approval because the proof battery did not clear.");
        sb.append("\n");

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write prove-promotion report: " + e.getMessage(), e);
        }
    }

    private void writeComparativeProofPromotionReport(Path reportPath,
                                                      String goal,
                                                      String selector,
                                                      List<RankedProofPromotionResult> results,
                                                      RankedProofPromotionResult best) {
        long approvedCount = results.stream().filter(result -> result.proofPromotion().approved()).count();
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Selfbuild Comparative Prove-Promotion Report\n\n");
        sb.append("Goal: `").append(goal).append("`\n");
        sb.append("Selector: `").append(selector).append("`\n");
        sb.append("Target count: `").append(results.size()).append("`\n");
        sb.append("Approved targets: `").append(approvedCount).append("`\n\n");

        sb.append("## Best Prove-Promotion Target\n\n");
        sb.append("- Target: `").append(best.label()).append("`\n");
        sb.append("- Approved: `").append(best.proofPromotion().approved()).append("`\n");
        sb.append("- Proof checks: `").append(best.proofPromotion().proofBattery().passedCount())
            .append(" / ").append(best.proofPromotion().proofBattery().checks().size()).append("`\n");
        sb.append("- Report: [prove-promotion-report.md](")
            .append(best.proofPromotion().reportPath().toString().replace('\\', '/')).append(")\n\n");

        sb.append("## Prove-Promotion Ranking\n\n");
        for (RankedProofPromotionResult result : results) {
            sb.append("- `").append(result.label()).append("`")
                .append(" approved=`").append(result.proofPromotion().approved()).append("`")
                .append(" checks=`").append(result.proofPromotion().proofBattery().passedCount())
                .append(" / ").append(result.proofPromotion().proofBattery().checks().size()).append("`")
                .append(" delta=`").append(result.proofPromotion().basePromotion().decision().scoreDelta()).append("`")
                .append(" report=[open](").append(result.proofPromotion().reportPath().toString().replace('\\', '/')).append(")\n");
        }

        try {
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write comparative prove-promotion report: " + e.getMessage(), e);
        }
    }

    private static String summarizeForOneLine(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        for (String line : normalized.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isBlank()) {
                return trimmed.length() > 200 ? trimmed.substring(0, 200) + "..." : trimmed;
            }
        }
        return "";
    }

    private static String findLineContaining(String text, String needle) {
        if (text == null || needle == null || needle.isBlank()) {
            return "";
        }
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        for (String line : normalized.split("\n")) {
            if (line.contains(needle)) {
                String trimmed = line.trim();
                return trimmed.length() > 220 ? trimmed.substring(0, 220) + "..." : trimmed;
            }
        }
        return "";
    }

    private static CandidateSpec candidate(String id, String title, String source) {
        SourceIdentity identity = extractIdentity(source);
        return new CandidateSpec(id, title, identity.packageName(), identity.className(), source);
    }

    private static SourceIdentity extractIdentity(String source) {
        String packageName = "fraymus.generated.selfbuild";
        String className = "UnnamedSelfBuild";
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(source);
        if (packageMatcher.find()) {
            packageName = packageMatcher.group(1).trim();
        }
        Matcher classMatcher = CLASS_PATTERN.matcher(source);
        if (classMatcher.find()) {
            className = classMatcher.group(1).trim();
        }
        return new SourceIdentity(packageName, className);
    }

    private static Path sourcePath(Path sourceRoot, String packageName, String className) {
        return sourceRoot.resolve(packageName.replace('.', '/')).resolve(className + ".java");
    }

    private String injectKnowledgeAnchors(String source, String goal) {
        StringBuilder sb = new StringBuilder();
        sb.append("// SELFBUILD_GOAL ").append(goal).append("\n");
        sb.append("// SELFBUILD_FINGERPRINT ").append(selfCodeEngine.linkConcept(goal)).append("\n");
        sb.append(source);
        return sb.toString();
    }

    private String anchorExistingSource(String source, String goal, String targetLabel) {
        String anchor = "// SELFBUILD_TARGET " + targetLabel + "\n"
            + "// SELFBUILD_GOAL " + goal + "\n"
            + "// SELFBUILD_FINGERPRINT " + selfCodeEngine.linkConcept(source + "::" + goal) + "\n";
        String[] lines = source.split("\n", -1);
        if (lines.length > 0 && lines[0].trim().startsWith("package ")) {
            StringBuilder sb = new StringBuilder();
            sb.append(lines[0]).append("\n\n").append(anchor);
            for (int i = 1; i < lines.length; i++) {
                sb.append(lines[i]);
                if (i < lines.length - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        return anchor + source;
    }

    private static String asciiSafe(String source) {
        String normalized = source
            .replace("φ", "phi")
            .replace("Φ", "PHI")
            .replace("🧬", "SELFBUILD")
            .replace("→", "->");
        StringBuilder sb = new StringBuilder(normalized.length());
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            sb.append(c <= 127 ? c : ' ');
        }
        return sb.toString();
    }

    private static String mergeTranscript(String stdout, String stderr) {
        String out = stdout == null ? "" : stdout.strip();
        String err = stderr == null ? "" : stderr.strip();
        if (out.isBlank()) return err;
        if (err.isBlank()) return out;
        return out + "\n[stderr]\n" + err;
    }

    private static String slugify(String value) {
        String slug = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "selfbuild" : slug;
    }

    private String diffLabel(Path targetFile, String fallback) {
        Path normalizedTarget = targetFile.toAbsolutePath().normalize();
        try {
            if (normalizedTarget.startsWith(assetManagerDir)) {
                return assetManagerDir.relativize(normalizedTarget).toString().replace('\\', '/');
            }
        } catch (IllegalArgumentException ignored) {
            // Fall through to fallback label.
        }
        return fallback.replace('\\', '/');
    }

    private static int compileSuccessCount(SelfBuildResult result) {
        return (int) result.candidates().stream().filter(CandidateResult::compileSuccess).count();
    }

    private static String buildUnifiedDiff(String originalLabel,
                                           String revisedLabel,
                                           String originalSource,
                                           String revisedSource) {
        List<String> originalLines = normalizedLines(originalSource);
        List<String> revisedLines = normalizedLines(revisedSource);
        List<DiffLine> diffLines = diffLines(originalLines, revisedLines);

        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(originalLabel).append("\n");
        sb.append("+++ ").append(revisedLabel).append("\n");
        sb.append("@@ -1,").append(originalLines.size()).append(" +1,").append(revisedLines.size()).append(" @@\n");
        for (DiffLine line : diffLines) {
            sb.append(line.prefix()).append(line.value()).append("\n");
        }
        return sb.toString();
    }

    private static List<String> normalizedLines(String source) {
        String normalized = source.replace("\r\n", "\n").replace('\r', '\n');
        List<String> lines = new ArrayList<>(List.of(normalized.split("\n", -1)));
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }
        return lines;
    }

    private static List<DiffLine> diffLines(List<String> originalLines, List<String> revisedLines) {
        int originalSize = originalLines.size();
        int revisedSize = revisedLines.size();
        int[][] lcs = new int[originalSize + 1][revisedSize + 1];

        for (int i = originalSize - 1; i >= 0; i--) {
            for (int j = revisedSize - 1; j >= 0; j--) {
                if (originalLines.get(i).equals(revisedLines.get(j))) {
                    lcs[i][j] = lcs[i + 1][j + 1] + 1;
                } else {
                    lcs[i][j] = Math.max(lcs[i + 1][j], lcs[i][j + 1]);
                }
            }
        }

        List<DiffLine> lines = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < originalSize && j < revisedSize) {
            if (originalLines.get(i).equals(revisedLines.get(j))) {
                lines.add(new DiffLine(' ', originalLines.get(i)));
                i++;
                j++;
            } else if (lcs[i + 1][j] >= lcs[i][j + 1]) {
                lines.add(new DiffLine('-', originalLines.get(i)));
                i++;
            } else {
                lines.add(new DiffLine('+', revisedLines.get(j)));
                j++;
            }
        }
        while (i < originalSize) {
            lines.add(new DiffLine('-', originalLines.get(i)));
            i++;
        }
        while (j < revisedSize) {
            lines.add(new DiffLine('+', revisedLines.get(j)));
            j++;
        }
        return lines;
    }

    private static String javaExecutable() {
        String suffix = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") ? "java.exe" : "java";
        return Path.of(System.getProperty("java.home"), "bin", suffix).toString();
    }

    private static Path transcriptPath(Path proofRoot, String id) {
        return proofRoot.resolve(id + ".txt");
    }

    private static String buildRuntimeClasspath(Path workingDir) {
        List<String> entries = new ArrayList<>();
        Path mainClasses = workingDir.resolve("build/classes/java/main");
        Path mainResources = workingDir.resolve("build/resources/main");
        if (Files.exists(mainClasses)) {
            entries.add(mainClasses.toString());
        }
        if (Files.exists(mainResources)) {
            entries.add(mainResources.toString());
        }
        if (entries.isEmpty()) {
            return System.getProperty("java.class.path");
        }
        return String.join(File.pathSeparator, entries);
    }

    private static ProcessOutcome runJavaMainProcess(Path workingDir,
                                                     Path proofRoot,
                                                     String id,
                                                     String mainClass,
                                                     String... args) {
        Path stdoutPath = transcriptPath(proofRoot, id);
        Path stderrPath = proofRoot.resolve(id + ".stderr.txt");
        try {
            Files.createDirectories(proofRoot);
            List<String> command = new ArrayList<>();
            command.add(javaExecutable());
            command.add("-Djava.awt.headless=true");
            command.add("-cp");
            command.add(buildRuntimeClasspath(workingDir));
            command.add(mainClass);
            command.addAll(List.of(args));

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDir.toFile());
            builder.redirectOutput(stdoutPath.toFile());
            builder.redirectError(stderrPath.toFile());
            int exitCode = builder.start().waitFor();

            String stdout = Files.exists(stdoutPath) ? Files.readString(stdoutPath, StandardCharsets.UTF_8) : "";
            String stderr = Files.exists(stderrPath) ? Files.readString(stderrPath, StandardCharsets.UTF_8) : "";
            String merged = mergeTranscript(stdout, stderr);
            Files.writeString(stdoutPath, merged, StandardCharsets.UTF_8);
            try {
                Files.deleteIfExists(stderrPath);
            } catch (Exception ignored) {
                // Keep merged transcript even if cleanup fails.
            }
            return new ProcessOutcome(exitCode, merged, stdoutPath);
        } catch (Exception e) {
            try {
                Files.createDirectories(proofRoot);
                Files.writeString(stdoutPath, e.getMessage(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // Best effort transcript.
            }
            return new ProcessOutcome(1, e.getMessage(), stdoutPath);
        }
    }

    private static ProcessOutcome runGradleMainProcess(Path workingDir,
                                                       Path proofRoot,
                                                       String id,
                                                       String mainClass) {
        Path stdoutPath = transcriptPath(proofRoot, id);
        Path stderrPath = proofRoot.resolve(id + ".stderr.txt");
        try {
            Files.createDirectories(proofRoot);
            List<String> command = new ArrayList<>();
            boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
            if (windows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add("gradlew.bat");
            } else {
                command.add("./gradlew");
            }
            command.add("--no-daemon");
            command.add("-PmainClass=" + mainClass);
            command.add("run");

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDir.toFile());
            builder.redirectOutput(stdoutPath.toFile());
            builder.redirectError(stderrPath.toFile());
            int exitCode = builder.start().waitFor();

            String stdout = Files.exists(stdoutPath) ? Files.readString(stdoutPath, StandardCharsets.UTF_8) : "";
            String stderr = Files.exists(stderrPath) ? Files.readString(stderrPath, StandardCharsets.UTF_8) : "";
            String merged = mergeTranscript(stdout, stderr);
            Files.writeString(stdoutPath, merged, StandardCharsets.UTF_8);
            try {
                Files.deleteIfExists(stderrPath);
            } catch (Exception ignored) {
                // Keep merged transcript even if cleanup fails.
            }
            return new ProcessOutcome(exitCode, merged, stdoutPath);
        } catch (Exception e) {
            try {
                Files.createDirectories(proofRoot);
                Files.writeString(stdoutPath, e.getMessage(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // Best effort transcript.
            }
            return new ProcessOutcome(1, e.getMessage(), stdoutPath);
        }
    }

    private static ProofCheckResult runWillowDirect(Path proofRoot) {
        Path transcript = transcriptPath(proofRoot, "willow-proof");
        try {
            AeonWillowCrusher willow = AeonWillowCrusher.getInstance();
            willow.boot();
            double hadamardMs = willow.hadamard(8);
            double entangleMs = willow.entangle(0, 1);
            double echoMs = willow.echo();
            String[] measure = willow.measure(0);
            long[] spectrum = willow.getSpectrum();
            boolean passed = measure != null && willow.getActiveQubitCount() >= 8;
            String body = """
                WILLOW PROOF COMPLETE
                QubitsDriven: 8
                HadamardMs: %.4f
                EntangleMs: %.4f
                EchoMs: %.4f
                Spin: %s
                Magnitude: %s
                ActiveQubits: %d
                TotalEnergy: %d
                """.formatted(
                    hadamardMs,
                    entangleMs,
                    echoMs,
                    measure == null ? "(none)" : measure[0],
                    measure == null ? "(none)" : measure[1],
                    willow.getActiveQubitCount(),
                    spectrum[1]
                ).stripIndent().trim();
            Files.createDirectories(proofRoot);
            Files.writeString(transcript, body, StandardCharsets.UTF_8);
            return new ProofCheckResult("willow-proof", "Willow quantum proof", passed, "ActiveQubits=" + willow.getActiveQubitCount(), transcript);
        } catch (Exception e) {
            try {
                Files.createDirectories(proofRoot);
                Files.writeString(transcript, e.getMessage(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // Best effort transcript.
            }
            return new ProofCheckResult("willow-proof", "Willow quantum proof", false, e.getMessage(), transcript);
        }
    }

    private static ProofCheckResult runTransmudderDirect(Path proofRoot, Path evidencePath) {
        Path transcript = transcriptPath(proofRoot, "transmudder-proof");
        try {
            Transmudder transmudder = new Transmudder();
            String raw = transmudder.transmuteToText(evidencePath.toString());
            String clean = transmudder.cleanse(raw);
            Capture<Void> capture = capture(() -> {
                transmudder.transmuteFile(evidencePath.toString());
                return null;
            });
            String excerpt = clean.length() > 320 ? clean.substring(0, 320) + "..." : clean;
            boolean passed = !clean.isBlank() && transmudder.getContextSize() > 0;
            String body = """
                TRANSMUDDER PROOF COMPLETE
                Target: %s
                RawChars: %d
                CleanChars: %d
                ContextSize: %d
                AbsorbedFiles: %d
                Excerpt:
                %s
                
                Capture:
                %s
                """.formatted(
                    evidencePath.toString().replace('\\', '/'),
                    raw.length(),
                    clean.length(),
                    transmudder.getContextSize(),
                    transmudder.getFileCount(),
                    excerpt,
                    mergeTranscript(capture.stdout(), capture.error())
                ).stripIndent().trim();
            Files.createDirectories(proofRoot);
            Files.writeString(transcript, body, StandardCharsets.UTF_8);
            return new ProofCheckResult("transmudder-proof", "Transmudder soul proof", passed, "CleanChars=" + clean.length(), transcript);
        } catch (Exception e) {
            try {
                Files.createDirectories(proofRoot);
                Files.writeString(transcript, e.getMessage(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // Best effort transcript.
            }
            return new ProofCheckResult("transmudder-proof", "Transmudder soul proof", false, e.getMessage(), transcript);
        }
    }

    private ReviewRequest requireReviewRequest(String reviewId) {
        return listReviewRequests().stream()
            .filter(request -> request.reviewId().equalsIgnoreCase(reviewId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown review id: " + reviewId));
    }

    private void writeReviewRequest(Path reviewRoot, ReviewRequest request) {
        try {
            Files.createDirectories(reviewRoot);
            Properties properties = new Properties();
            properties.setProperty("reviewId", request.reviewId());
            properties.setProperty("status", request.status());
            properties.setProperty("goal", request.goal());
            properties.setProperty("targetLabel", request.targetLabel());
            properties.setProperty("targetPath", request.targetPath().toString());
            properties.setProperty("promotionReportPath", request.promotionReportPath().toString());
            properties.setProperty("provePromotionReportPath", request.provePromotionReportPath().toString());
            properties.setProperty("patchPath", request.patchPath() == null ? "" : request.patchPath().toString());
            properties.setProperty("promotedSnapshot", request.promotedSnapshot() == null ? "" : request.promotedSnapshot().toString());
            properties.setProperty("createdAt", request.createdAt());
            properties.setProperty("decidedAt", request.decidedAt());
            properties.setProperty("note", request.note());
            try (var output = Files.newOutputStream(reviewRoot.resolve(REVIEW_REQUEST_FILE))) {
                properties.store(output, "Fraynix selfbuild review request");
            }

            String markdown = """
                # Fraynix Selfbuild Review Request

                - Review id: `%s`
                - Status: `%s`
                - Goal: `%s`
                - Target label: `%s`
                - Target path: `%s`
                - Promotion report: `%s`
                - Proof report: `%s`
                - Patch artifact: `%s`
                - Promoted snapshot: `%s`
                - Created at: `%s`
                - Decided at: `%s`
                - Note: %s
                """.formatted(
                request.reviewId(),
                request.status(),
                request.goal(),
                request.targetLabel(),
                request.targetPath().toString().replace('\\', '/'),
                request.promotionReportPath().toString().replace('\\', '/'),
                request.provePromotionReportPath().toString().replace('\\', '/'),
                request.patchPath() == null ? "(none)" : request.patchPath().toString().replace('\\', '/'),
                request.promotedSnapshot() == null ? "(none)" : request.promotedSnapshot().toString().replace('\\', '/'),
                request.createdAt(),
                request.decidedAt().isBlank() ? "(pending)" : request.decidedAt(),
                request.note()
            ).stripIndent().trim();
            Files.writeString(reviewRoot.resolve("review-request.md"), markdown, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write review request: " + e.getMessage(), e);
        }
    }

    private void writeReviewDecision(Path reviewRoot, ReviewRequest request, String headline, Path backupPath) {
        String markdown = """
            # Fraynix Selfbuild Review Decision

            - Review id: `%s`
            - Status: `%s`
            - Target: `%s`
            - Decided at: `%s`
            - Note: %s
            - Backup: `%s`
            """.formatted(
            request.reviewId(),
            request.status(),
            request.targetPath().toString().replace('\\', '/'),
            request.decidedAt(),
            headline,
            backupPath == null ? "(none)" : backupPath.toString().replace('\\', '/')
        ).stripIndent().trim();
        try {
            Files.createDirectories(reviewRoot);
            Files.writeString(reviewRoot.resolve("review-decision.md"), markdown, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Could not write review decision: " + e.getMessage(), e);
        }
    }

    private ReviewRequest readReviewRequest(Path metadataPath) throws Exception {
        Properties properties = new Properties();
        try (var input = Files.newInputStream(metadataPath)) {
            properties.load(input);
        }
        return new ReviewRequest(
            properties.getProperty("reviewId", metadataPath.getParent().getParent().getFileName().toString()),
            properties.getProperty("status", "pending"),
            properties.getProperty("goal", ""),
            properties.getProperty("targetLabel", ""),
            Path.of(properties.getProperty("targetPath", ".")),
            Path.of(properties.getProperty("promotionReportPath", ".")),
            Path.of(properties.getProperty("provePromotionReportPath", ".")),
            blankToNullPath(properties.getProperty("patchPath", "")),
            blankToNullPath(properties.getProperty("promotedSnapshot", "")),
            properties.getProperty("createdAt", ""),
            properties.getProperty("decidedAt", ""),
            properties.getProperty("note", "")
        );
    }

    private SessionArtifact summarizeRunArtifact(Path runDir) {
        String type = "selfbuild";
        Path reportPath = firstExistingReport(runDir, List.of(
            "review/review-request.md",
            "prove-promotion/prove-promotion-report.md",
            "promotion/promotion-report.md",
            "comparative-prove-promotion-report.md",
            "comparative-promotion-report.md",
            "comparative-selfbuild-report.md",
            "selfbuild-report.md"
        ));
        if (reportPath != null) {
            String reportName = reportPath.getFileName().toString();
            if (reportName.contains("review-request")) {
                type = "review";
            } else if (reportName.contains("prove-promotion")) {
                type = "prove-promotion";
            } else if (reportName.contains("promotion")) {
                type = "promotion";
            } else if (reportName.contains("comparative")) {
                type = "comparative-selfbuild";
            }
        }
        return new SessionArtifact(
            runDir.getFileName().toString(),
            type,
            runDir,
            reportPath,
            safeInstant(runDir.getFileName().toString()).toString()
        );
    }

    private Path firstExistingReport(Path root, List<String> candidates) {
        for (String candidate : candidates) {
            Path path = root.resolve(candidate);
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }

    private Path generatedSelfBuildRoot() {
        return assetManagerDir.resolve("generated").resolve("ultimate-selfbuild");
    }

    private static Instant safeInstant(String directoryName) {
        try {
            String stamp = directoryName.length() >= 15 ? directoryName.substring(0, 15) : directoryName;
            return Instant.from(STAMP.parse(stamp));
        } catch (Exception ignored) {
            return Instant.EPOCH;
        }
    }

    private static Path blankToNullPath(String value) {
        return value == null || value.isBlank() ? null : Path.of(value);
    }

    private static void invokeNoArg(Object organism, String methodName) throws Exception {
        Method method = organism.getClass().getMethod(methodName);
        method.invoke(organism);
    }

    private static Object invokeDoubleCompat(Object organism, String methodName, double arg) throws Exception {
        try {
            Method method = organism.getClass().getMethod(methodName, double.class);
            return method.invoke(organism, arg);
        } catch (NoSuchMethodException ignored) {
            Method method = organism.getClass().getMethod(methodName, Double.class);
            return method.invoke(organism, arg);
        }
    }

    private static <T> Capture<T> capture(ThrowingSupplier<T> supplier) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try (PrintStream outStream = new PrintStream(out, true, StandardCharsets.UTF_8);
             PrintStream errStream = new PrintStream(err, true, StandardCharsets.UTF_8)) {
            System.setOut(outStream);
            System.setErr(errStream);
            T value = supplier.get();
            return new Capture<>(value, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return new Capture<>(null, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8) + "\n" + e.getMessage());
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    interface ProofBatteryRunner {
        ProofBatteryResult run(Path proofRoot, Path evidencePath, String targetLabel, String goal);
    }

    private static final class DefaultProofBatteryRunner implements ProofBatteryRunner {
        private final Path assetManagerDir;

        private DefaultProofBatteryRunner(Path assetManagerDir) {
            this.assetManagerDir = assetManagerDir;
        }

        @Override
        public ProofBatteryResult run(Path proofRoot, Path evidencePath, String targetLabel, String goal) {
            List<ProofCheckResult> checks = new ArrayList<>();

            ProcessOutcome systemProof = runGradleMainProcess(assetManagerDir, proofRoot, "system-proof", "fraymus.validation.SystemProof");
            boolean systemPassed = systemProof.exitCode() == 0
                && (systemProof.transcript().contains("PROOF COMPLETE") || systemProof.transcript().contains("Tests failed:"));
            String systemSummary = systemPassed
                ? summarizeForOneLine(findLineContaining(systemProof.transcript(), "Tests passed:"))
                : summarizeForOneLine(systemProof.transcript());
            checks.add(new ProofCheckResult("system-proof", "System proof", systemPassed, systemSummary, systemProof.transcriptPath()));

            ProcessOutcome fraynixProof = runGradleMainProcess(assetManagerDir, proofRoot, "fraynix-proof", "fraymus.FraynixProof");
            boolean fraynixPassed = fraynixProof.exitCode() == 0
                && fraynixProof.transcript().contains("PROOFS PASSED:");
            String fraynixSummary = fraynixPassed
                ? summarizeForOneLine(findLineContaining(fraynixProof.transcript(), "PROOFS PASSED:"))
                : summarizeForOneLine(fraynixProof.transcript());
            checks.add(new ProofCheckResult("fraynix-proof", "Fraynix proof", fraynixPassed, fraynixSummary, fraynixProof.transcriptPath()));

            checks.add(runWillowDirect(proofRoot));
            checks.add(runTransmudderDirect(proofRoot, evidencePath));

            return new ProofBatteryResult(proofRoot, evidencePath, checks, false, "");
        }
    }

    record SelfBuildResult(String goal,
                           Path runRoot,
                           Path reportPath,
                           List<CandidateResult> candidates,
                           CandidateResult winner) {
        String summary() {
            return "SELFBUILD COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Winner: " + winner.id() + " [" + winner.className() + "]\n"
                + "Score: " + winner.score() + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record ComparativeSelfBuildResult(String goal,
                                      String selector,
                                      Path runRoot,
                                      Path reportPath,
                                      List<RankedSourceResult> rankedTargets,
                                      RankedSourceResult bestTarget) {
        String summary() {
            return "SELFBUILD COMPARATIVE COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Selector: " + selector + "\n"
                + "Targets: " + rankedTargets.size() + "\n"
                + "Best target: " + bestTarget.label() + "\n"
                + "Best winner: " + bestTarget.result().winner().id() + " [" + bestTarget.result().winner().className() + "]\n"
                + "Score: " + bestTarget.result().winner().score() + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record CandidateSpec(String id, String title, String packageName, String className, String source) {}

    record CandidateResult(String id,
                           String title,
                           String packageName,
                           String className,
                           String source,
                           Path sourceFile,
                           String fingerprint,
                           String validateReport,
                           ValidationSignals validation,
                           boolean identityPreserved,
                           boolean goalAnchored,
                           boolean compileSuccess,
                           boolean executeSuccess,
                           boolean resonanceSuccess,
                           String compileTranscript,
                           String executeTranscript,
                           String resonanceTranscript,
                           String failure,
                           int score) {}

    record ValidationSignals(boolean bracesBalanced,
                             boolean parensBalanced,
                             boolean bracketsBalanced,
                             boolean hasClass,
                             boolean hasMethod) {
        static ValidationSignals from(String code) {
            int braces = 0;
            int parens = 0;
            int brackets = 0;
            for (char c : code.toCharArray()) {
                if (c == '{') braces++;
                if (c == '}') braces--;
                if (c == '(') parens++;
                if (c == ')') parens--;
                if (c == '[') brackets++;
                if (c == ']') brackets--;
            }
            return new ValidationSignals(
                braces == 0,
                parens == 0,
                brackets == 0,
                code.contains("class "),
                code.contains("(") && code.contains(")")
            );
        }
    }

    record SourceIdentity(String packageName, String className) {}

    record Capture<T>(T value, String stdout, String error) {}

    record CompileResult(boolean success, String transcript) {}

    record SourceTarget(Path path, String label) {}

    record RankedSourceResult(String label, Path path, SelfBuildResult result) {}

    record PromotionDecision(boolean promotable,
                             String reason,
                             CandidateResult baseline,
                             CandidateResult promotedCandidate,
                             int scoreDelta) {}

    record PromotionResult(String goal,
                           String targetLabel,
                           Path runRoot,
                           Path reportPath,
                           Path patchPath,
                           Path originalSnapshot,
                           Path promotedSnapshot,
                           SelfBuildResult selfBuild,
                           PromotionDecision decision) {
        String summary() {
            return "SELFBUILD PROMOTION COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Target: " + targetLabel + "\n"
                + "Promotable: " + decision.promotable() + "\n"
                + "Reason: " + decision.reason() + "\n"
                + "Candidate: " + (decision.promotedCandidate() == null
                    ? "(none)"
                    : decision.promotedCandidate().id() + " [" + decision.promotedCandidate().className() + "]") + "\n"
                + "Patch: " + (decision.promotable() ? patchPath.toString().replace('\\', '/') : "(none)") + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record ComparativePromotionResult(String goal,
                                      String selector,
                                      Path runRoot,
                                      Path reportPath,
                                      List<RankedPromotionResult> rankedTargets,
                                      RankedPromotionResult bestTarget) {
        String summary() {
            long promotableCount = rankedTargets.stream().filter(result -> result.promotion().decision().promotable()).count();
            return "SELFBUILD PROMOTION COMPARATIVE COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Selector: " + selector + "\n"
                + "Targets: " + rankedTargets.size() + "\n"
                + "Promotable targets: " + promotableCount + "\n"
                + "Best target: " + bestTarget.label() + "\n"
                + "Best promotion: " + (bestTarget.promotion().decision().promotedCandidate() == null
                    ? "(none)"
                    : bestTarget.promotion().decision().promotedCandidate().id()
                        + " [" + bestTarget.promotion().decision().promotedCandidate().className() + "]") + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record RankedPromotionResult(String label, Path path, PromotionResult promotion) {}

    record ProofCheckResult(String id,
                            String title,
                            boolean passed,
                            String summary,
                            Path transcriptPath) {}

    record ProofBatteryResult(Path proofRoot,
                              Path evidencePath,
                              List<ProofCheckResult> checks,
                              boolean skipped,
                              String reason) {
        static ProofBatteryResult skipped(Path proofRoot, Path evidencePath, String reason) {
            return new ProofBatteryResult(proofRoot, evidencePath, List.of(), true, reason);
        }

        long passedCount() {
            return checks.stream().filter(ProofCheckResult::passed).count();
        }

        boolean allPassed() {
            return !skipped && !checks.isEmpty() && passedCount() == checks.size();
        }
    }

    record ProofDrivenPromotionResult(String goal,
                                      String targetLabel,
                                      Path runRoot,
                                      Path reportPath,
                                      PromotionResult basePromotion,
                                      ProofBatteryResult proofBattery,
                                      boolean approved) {
        String summary() {
            return "SELFBUILD PROVE-PROMOTION COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Target: " + targetLabel + "\n"
                + "Base promotable: " + basePromotion.decision().promotable() + "\n"
                + "Proof checks: " + proofBattery.passedCount() + " / " + proofBattery.checks().size() + "\n"
                + "Approved: " + approved + "\n"
                + "Candidate: " + (basePromotion.decision().promotedCandidate() == null
                    ? "(none)"
                    : basePromotion.decision().promotedCandidate().id() + " [" + basePromotion.decision().promotedCandidate().className() + "]") + "\n"
                + "Patch candidate: " + (basePromotion.decision().promotable() ? basePromotion.patchPath().toString().replace('\\', '/') : "(none)") + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record ComparativeProofPromotionResult(String goal,
                                           String selector,
                                           Path runRoot,
                                           Path reportPath,
                                           List<RankedProofPromotionResult> rankedTargets,
                                           RankedProofPromotionResult bestTarget) {
        String summary() {
            long approvedCount = rankedTargets.stream().filter(result -> result.proofPromotion().approved()).count();
            return "SELFBUILD PROVE-PROMOTION COMPARATIVE COMPLETE\n"
                + "Goal: " + goal + "\n"
                + "Selector: " + selector + "\n"
                + "Targets: " + rankedTargets.size() + "\n"
                + "Approved targets: " + approvedCount + "\n"
                + "Best target: " + bestTarget.label() + "\n"
                + "Best approval: " + bestTarget.proofPromotion().approved() + "\n"
                + "Report: " + reportPath.toString().replace('\\', '/');
        }
    }

    record RankedProofPromotionResult(String label, Path path, ProofDrivenPromotionResult proofPromotion) {}

    record ReviewRequest(String reviewId,
                         String status,
                         String goal,
                         String targetLabel,
                         Path targetPath,
                         Path promotionReportPath,
                         Path provePromotionReportPath,
                         Path patchPath,
                         Path promotedSnapshot,
                         String createdAt,
                         String decidedAt,
                         String note) {
        ReviewRequest withDecision(String nextStatus, String nextNote, String nextDecidedAt) {
            return new ReviewRequest(
                reviewId,
                nextStatus,
                goal,
                targetLabel,
                targetPath,
                promotionReportPath,
                provePromotionReportPath,
                patchPath,
                promotedSnapshot,
                createdAt,
                nextDecidedAt,
                nextNote
            );
        }
    }

    record ReviewResolution(String reviewId,
                            String status,
                            Path targetPath,
                            String note,
                            Path backupPath) {
        String summary() {
            return "SELFBUILD REVIEW " + status.toUpperCase(Locale.ROOT) + "\n"
                + "Review: " + reviewId + "\n"
                + "Target: " + targetPath.toString().replace('\\', '/') + "\n"
                + "Note: " + note + "\n"
                + "Backup: " + (backupPath == null ? "(none)" : backupPath.toString().replace('\\', '/'));
        }
    }

    record SessionArtifact(String id,
                           String type,
                           Path runRoot,
                           Path reportPath,
                           String createdAt) {}

    record ProcessOutcome(int exitCode, String transcript, Path transcriptPath) {}

    record DiffLine(char prefix, String value) {}
}
