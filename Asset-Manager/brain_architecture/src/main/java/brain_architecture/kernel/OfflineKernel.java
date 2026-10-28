package brain_architecture.kernel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Air-gapped runtime kernel with typed planning, execution, verification, memory, and mutation.
 */
public final class OfflineKernel implements AutoCloseable {
    private final KernelConfiguration configuration;
    private final KernelHypervectorSpace hypervectorSpace;
    private final HybridMemoryStore memoryStore;
    private final MerkleAppendOnlyJournal journal;
    private final StrategyBanditRouter router;
    private final TaskPlanner planner;
    private final KernelVerifier verifier;
    private final ShadowMutationEngine mutationEngine;
    private final MutationProposalEngine mutationProposalEngine;
    private final PdfDistillationEngine pdfDistillationEngine;
    private final TraceReplayStore traceReplayStore;
    private final KernelStateStore stateStore;
    private final RepairMemoryStore repairMemoryStore;
    private final ProjectMemoryArtifactWriter projectMemoryArtifactWriter;
    private final CanonicalTruthAuditor canonicalTruthAuditor;
    private final FoundationalSubstrateBootstrapper foundationalSubstrateBootstrapper;
    private final ValidationSuiteFactory validationSuiteFactory;
    private final UserSpaceSubstrateAdapter userSpaceAdapter;
    private final F0RuntimeSubstrateAdapter f0RuntimeAdapter;
    private final LocalModelRuntimeAdapter localModelRuntimeAdapter;

    public OfflineKernel(KernelConfiguration configuration) throws IOException {
        this.configuration = configuration;
        Files.createDirectories(configuration.getStateRoot());
        this.hypervectorSpace = new KernelHypervectorSpace();
        this.memoryStore = new HybridMemoryStore(
            configuration.getWorkspaceRoot(),
            hypervectorSpace,
            configuration.isCausalPolicyEnabled()
        );
        this.journal = new MerkleAppendOnlyJournal(configuration.getStateRoot());
        this.router = new StrategyBanditRouter(configuration.getRandomSeed());
        this.planner = new TaskPlanner();
        this.verifier = new KernelVerifier(configuration);
        this.mutationEngine = new ShadowMutationEngine(configuration, verifier);
        this.repairMemoryStore = new RepairMemoryStore(configuration.getStateRoot());
        this.mutationProposalEngine = new MutationProposalEngine(configuration, repairMemoryStore);
        this.pdfDistillationEngine = new PdfDistillationEngine(configuration, memoryStore);
        this.traceReplayStore = new TraceReplayStore(configuration.getStateRoot());
        this.stateStore = new KernelStateStore(configuration.getStateRoot());
        this.projectMemoryArtifactWriter = new ProjectMemoryArtifactWriter(configuration, repairMemoryStore);
        this.canonicalTruthAuditor = new CanonicalTruthAuditor(configuration);
        this.foundationalSubstrateBootstrapper = new FoundationalSubstrateBootstrapper(configuration);
        this.validationSuiteFactory = new ValidationSuiteFactory(configuration);
        this.userSpaceAdapter = new UserSpaceSubstrateAdapter(configuration.getWorkspaceRoot());
        this.f0RuntimeAdapter = new F0RuntimeSubstrateAdapter(configuration.getWorkspaceRoot());
        this.localModelRuntimeAdapter = new LocalModelRuntimeAdapter(configuration);
        restoreState();
    }

    public int ingestArtifact(Path path) throws IOException {
        int ingested = memoryStore.ingestPath(path);
        journal.append("artifact_ingested", path.toString(), Map.of("count", Integer.toString(ingested)));
        persistState();
        return ingested;
    }

    public KernelRunResult runTask(TaskEnvelope envelope, ValidationSuite suite) throws IOException {
        ValidationSuite effectiveSuite = effectiveSuite(suite);
        for (Path artifact : envelope.getArtifacts()) {
            try {
                ingestArtifact(artifact);
            } catch (IOException ignored) {
                // Let verifier and execution surface missing artifact details without stopping ingestion for others.
            }
        }

        List<MemoryHit> hits = memoryStore.search(envelope.getGoal(), envelope.getArtifacts(), envelope.getBudget().getMaxFilesToRead());
        String strategy = router.select(envelope, hits);
        SubstrateAdapter adapter = chooseAdapter(envelope);
        ExecutionTrace trace = new ExecutionTrace(envelope.getId(), configuration.getRandomSeed());
        trace.setStrategy(strategy);
        trace.setAdapterId(adapter.getAdapterId());

        List<PlanStep> plan = planner.plan(envelope, hits, strategy);
        SubstrateSession session = adapter.loadContext(envelope, hits);
        int filesRead = 0;

        journal.append("task_started", envelope.getGoal(), Map.of(
            "taskId", envelope.getId().toString(),
            "strategy", strategy,
            "adapter", adapter.getAdapterId()
        ));

        for (PlanStep step : plan) {
            trace.addPlanStep(step);
            SubstrateResult stepResult = adapter.step(session, step.getDescription());
            trace.addObservation(stepResult.getOutput());

            if (step.getHint() == PlanActionHint.INGEST_ARTIFACTS) {
                for (Path artifact : envelope.getArtifacts()) {
                    if (filesRead >= envelope.getBudget().getMaxFilesToRead()) {
                        break;
                    }
                    SubstrateResult result = adapter.act(session, SubstrateAction.readFile(artifact));
                    trace.addToolCall("read-file", List.of(artifact.toString()), result.isSuccess(), result.getOutput());
                    filesRead++;
                }
            } else if (step.getHint() == PlanActionHint.QUERY_MEMORY) {
                for (MemoryHit hit : hits) {
                    trace.addObservation(
                        "[memory] "
                            + hit.getDocument().getSourceId()
                            + " score=" + String.format("%.4f", hit.getTotalScore())
                            + " causal=" + String.format("%.4f", hit.getCausalScore())
                            + " grounded=" + hit.isCausallyGrounded()
                    );
                }
            } else if (step.getHint() == PlanActionHint.EXECUTE_SUBSTRATE && envelope.requestsLocalModelRuntime()) {
                SubstrateResult result = adapter.act(session, SubstrateAction.runInference(
                    envelope.getGoal(),
                    envelope.getRequestedModelId(),
                    envelope.getRequestedModelMaxTokens()
                ));
                trace.addToolCall(
                    "local-model-inference",
                    List.of(
                        envelope.getRequestedModelId() == null ? "auto" : envelope.getRequestedModelId(),
                        Integer.toString(envelope.getRequestedModelMaxTokens())
                    ),
                    result.isSuccess(),
                    result.getOutput()
                );
            } else if (step.getHint() == PlanActionHint.EXECUTE_SUBSTRATE && envelope.requestsF0Runtime()) {
                SubstrateResult result = adapter.act(session, SubstrateAction.runCommand(List.of("cargo", "run", "--quiet")));
                trace.addToolCall("substrate-command", List.of("cargo", "run", "--quiet"), result.isSuccess(), result.getOutput());
            } else if (step.getHint() == PlanActionHint.EXECUTE_SUBSTRATE && !envelope.getArtifacts().isEmpty()) {
                Path firstArtifact = envelope.getArtifacts().get(0).getParent();
                if (firstArtifact != null) {
                    SubstrateResult result = adapter.act(session, SubstrateAction.listDirectory(firstArtifact));
                    trace.addToolCall("list-directory", List.of(firstArtifact.toString()), result.isSuccess(), result.getOutput());
                }
            }
        }

        VerifierResult verifierResult = verifier.verify(envelope, trace, effectiveSuite, configuration.getWorkspaceRoot());
        verifierResult.getPassedChecks().forEach(trace::addVerifierResult);
        verifierResult.getFailedChecks().forEach(trace::addVerifierResult);
        trace.finish(verifierResult.isSuccess() ? "validated" : "failed_validation");

        router.recordOutcome(strategy, verifierResult.isSuccess());
        Path tracePath = traceReplayStore.persist(trace);
        journal.append("task_completed", trace.getOutcome(), Map.of(
            "taskId", envelope.getId().toString(),
            "strategy", strategy,
            "adapter", adapter.getAdapterId(),
            "outcome", trace.getOutcome(),
            "tracePath", tracePath.toString()
        ));
        persistState();

        return new KernelRunResult(strategy, adapter.getAdapterId(), hits, trace, verifierResult, tracePath);
    }

    public ShadowMutationEngine.MutationReport applyMutationCandidate(MutationCandidate candidate, ValidationSuite suite) throws IOException {
        return applyMutationCandidate(candidate, suite, VerifierResult::isSuccess);
    }

    private ShadowMutationEngine.MutationReport applyMutationCandidate(
            MutationCandidate candidate,
            ValidationSuite suite,
            java.util.function.Predicate<VerifierResult> promotionPolicy) throws IOException {
        ValidationSuite effectiveSuite = effectiveSuite(suite);
        journal.append("mutation_started", candidate.getExpectedGain(), Map.of(
            "mutationId", candidate.getId().toString(),
            "scope", candidate.getScope().name()
        ));
        ShadowMutationEngine.MutationReport report = mutationEngine.apply(candidate, effectiveSuite, promotionPolicy);
        try {
            if (report.promoted()) {
                for (Path promotedFile : report.promotedFiles()) {
                    try {
                        ingestArtifact(promotedFile);
                    } catch (IOException ignored) {
                        // Mutation state is still journaled even if re-indexing one file fails.
                    }
                }
            }
            journal.append("mutation_completed", Boolean.toString(report.promoted()), Map.of(
                "mutationId", candidate.getId().toString(),
                "promoted", Boolean.toString(report.promoted())
            ));
            persistState();
            return report;
        } finally {
            report.cleanupStaging();
        }
    }

    public List<MutationProposal> proposeMutations(TaskEnvelope envelope, int topK) throws IOException {
        return proposeMutations(envelope, topK, null);
    }

    public List<MutationProposal> proposeMutations(TaskEnvelope envelope, int topK, VerifierResult verifierResult) throws IOException {
        for (Path artifact : envelope.getArtifacts()) {
            try {
                ingestArtifact(artifact);
            } catch (IOException ignored) {
                // Proposals still work with partial local context.
            }
        }
        List<MemoryHit> hits = memoryStore.search(envelope.getGoal(), envelope.getArtifacts(), Math.max(topK, envelope.getBudget().getMaxFilesToRead()));
        WorkspaceProfile profile = profileWorkspace();
        List<MutationProposal> proposals = mutationProposalEngine.propose(envelope, profile, hits, verifierResult);
        proposals.sort(Comparator.comparingDouble((MutationProposal proposal) -> proposalPriority(envelope, proposal)).reversed());
        journal.append("mutation_proposals_generated", envelope.getGoal(), Map.of(
            "taskId", envelope.getId().toString(),
            "count", Integer.toString(proposals.size())
        ));
        persistState();
        return proposals.subList(0, Math.min(topK, proposals.size()));
    }

    public List<MutationProposalPreview> previewMutationProposals(
            TaskEnvelope envelope,
            ValidationSuite suite,
            int topK,
            VerifierResult verifierResult) throws IOException {
        ValidationSuite effectiveSuite = effectiveSuite(suite);
        List<MutationProposal> proposals = proposeMutations(envelope, topK, verifierResult);
        List<MutationProposalPreview> previews = new ArrayList<>();
        for (MutationProposal proposal : proposals) {
            try (ShadowMutationEngine.MutationPreview preview = mutationEngine.preview(proposal.getCandidate(), effectiveSuite)) {
                VerifierResult previewVerifier = preview.verifierResult();
                boolean validationPassed = previewVerifier != null && previewVerifier.isSuccess();
                boolean validationImproved = previewVerifier != null
                    && !validationPassed
                    && isValidationImprovement(verifierResult, previewVerifier);
                previews.add(new MutationProposalPreview(
                    proposal,
                    preview.riskAccepted(),
                    validationPassed,
                    validationImproved,
                    preview.candidatePaths(),
                    previewVerifier
                ));
            }
        }
        return List.copyOf(previews);
    }

    public List<MerkleAppendOnlyJournal.JournalEntry> getRecentJournalEntriesForTask(UUID taskId, int limit) {
        if (taskId == null || limit <= 0) {
            return List.of();
        }
        List<MerkleAppendOnlyJournal.JournalEntry> entries = journal.getEntries();
        List<MerkleAppendOnlyJournal.JournalEntry> matched = new ArrayList<>();
        for (int index = entries.size() - 1; index >= 0 && matched.size() < limit; index--) {
            MerkleAppendOnlyJournal.JournalEntry entry = entries.get(index);
            if (taskId.toString().equals(entry.metadata().get("taskId"))) {
                matched.add(0, entry);
            }
        }
        return List.copyOf(matched);
    }

    public EvolutionReport previewEvolution(
            TaskEnvelope envelope,
            ValidationSuite suite,
            KernelRunResult baselineRun,
            int maxIterations,
            int proposalLimit) throws IOException {
        ValidationSuite resolvedSuite = effectiveSuite(suite);
        KernelRunResult effectiveBaselineRun = baselineRun == null ? runTask(envelope, resolvedSuite) : baselineRun;
        WorkspaceProfile baselineProfile = profileWorkspace();
        List<EvolutionReport.EvolutionIteration> previewIterations = new ArrayList<>();
        int iterations = Math.max(1, maxIterations);
        Path baselineWorkspaceRoot = configuration.getWorkspaceRoot();
        List<AutoCloseable> chainedResources = new ArrayList<>();

        try {
            OfflineKernel currentKernel = this;
            KernelRunResult currentRun = effectiveBaselineRun;
            WorkspaceProfile currentProfile = baselineProfile;

            for (int iteration = 1; iteration <= iterations; iteration++) {
                ValidationSuite currentSuite = rebaseValidationSuite(
                    resolvedSuite,
                    baselineWorkspaceRoot,
                    currentKernel.configuration.getWorkspaceRoot()
                );
                EvolutionPreviewStep previewStep = currentKernel.previewEvolutionIteration(
                    envelope,
                    iteration,
                    currentRun,
                    currentProfile,
                    currentSuite,
                    Math.max(1, proposalLimit)
                );
                if (previewStep == null) {
                    break;
                }

                EvolutionReport.EvolutionIteration previewIteration = previewStep.iteration();
                previewIterations.add(previewIteration);

                MutationProposal selected = previewIteration.proposal();
                journal.append("evolution_preview_generated", envelope.getGoal(), Map.of(
                    "taskId", envelope.getId().toString(),
                    "iteration", Integer.toString(iteration),
                    "proposal", selected == null ? "none" : selected.getTitle(),
                    "evaluationCount", Integer.toString(previewIteration.evaluations().size()),
                    "selectionReason", previewIteration.selectionReason()
                ));

                ShadowMutationEngine.MutationPreview selectedPreview = previewStep.selectedPreview();
                if (selectedPreview == null || selected == null || iteration >= iterations) {
                    if (selectedPreview != null) {
                        selectedPreview.close();
                    }
                    break;
                }

                chainedResources.add(selectedPreview);
                OfflineKernel childKernel = new OfflineKernel(currentKernel.configuration.withWorkspaceRoot(selectedPreview.shadowRoot()));
                chainedResources.add(childKernel);
                currentKernel = childKernel;
                ValidationSuite childSuite = rebaseValidationSuite(
                    resolvedSuite,
                    baselineWorkspaceRoot,
                    childKernel.configuration.getWorkspaceRoot()
                );
                currentRun = childKernel.runTask(envelope, childSuite);
                currentProfile = childKernel.profileWorkspace();
            }
        } finally {
            closeReverse(chainedResources);
        }

        persistState();
        return new EvolutionReport(envelope, effectiveBaselineRun, baselineProfile, previewIterations);
    }

    public EvolutionReport evolveTask(TaskEnvelope envelope, int maxIterations) throws IOException {
        return evolveTask(envelope, maxIterations, false, message -> { });
    }

    /** The shell requires every configured check to pass; legacy callers retain partial-repair policy. */
    public EvolutionReport evolveTask(TaskEnvelope envelope, int maxIterations,
            boolean requirePassingValidation, java.util.function.Consumer<String> progress) throws IOException {
        int iterations = Math.max(1, maxIterations);
        ValidationSuite resolvedSuite = effectiveSuite(ValidationSuite.empty());
        if (requirePassingValidation && resolvedSuite.allCommands().isEmpty()) {
            throw new IOException("No executable validation commands; configure offline-toolchain.toml before promotion.");
        }
        progress.accept("Baseline validation started.");
        KernelRunResult baselineRun = runTask(envelope, resolvedSuite);
        progress.accept("Baseline passed=" + baselineRun.getVerifierResult().isSuccess()
            + " failures=" + baselineRun.getVerifierResult().getFailedChecks());
        KernelRunResult currentRun = baselineRun;
        WorkspaceProfile baselineProfile = profileWorkspace();
        WorkspaceProfile currentProfile = baselineProfile;
        List<EvolutionReport.EvolutionIteration> evolutionIterations = new ArrayList<>();
        boolean promotedAny = false;

        for (int iteration = 1; iteration <= iterations; iteration++) {
            List<MutationProposal> proposals = proposeMutations(envelope, 5, currentRun.getVerifierResult());
            if (proposals.isEmpty()) {
                break;
            }
            VerifierResult iterationBaselineVerifier = currentRun.getVerifierResult();

            List<EvolutionReport.ProposalEvaluation> evaluations = new ArrayList<>();
            for (MutationProposal proposal : proposals) {
                progress.accept("Iteration " + iteration + " shadow candidate: " + proposal.getTitle()
                    + " targets=" + proposal.getTargetPaths());
                try (ShadowMutationEngine.MutationPreview preview = mutationEngine.preview(proposal.getCandidate(), resolvedSuite)) {
                    evaluations.add(evaluateProposal(envelope, proposal, preview, currentProfile, iterationBaselineVerifier));
                    progress.accept("Shadow riskAccepted=" + preview.riskAccepted()
                        + " validationPassed=" + (preview.verifierResult() != null && preview.verifierResult().isSuccess())
                        + " failures=" + (preview.verifierResult() == null ? "not-run" : preview.verifierResult().getFailedChecks()));
                }
            }

            EvolutionReport.ProposalEvaluation selectedEvaluation = evaluations.stream()
                .filter(EvolutionReport.ProposalEvaluation::viable)
                .filter(evaluation -> !requirePassingValidation || evaluation.validationPassed())
                .max(Comparator.comparingDouble(EvolutionReport.ProposalEvaluation::totalScore))
                .orElse(null);

            MutationProposal selected = selectedEvaluation == null ? null : selectedEvaluation.proposal();
            String selectionReason = selectedEvaluation == null
                ? "No proposal survived shadow validation."
                : "Selected " + selected.getTitle()
                    + " with score "
                    + String.format(java.util.Locale.ROOT, "%.3f", selectedEvaluation.totalScore());
            ShadowMutationEngine.MutationReport report = selected == null
                ? null
                : applyMutationCandidate(
                    selected.getCandidate(),
                    resolvedSuite,
                    verifierResult -> verifierResult.isSuccess()
                        || (!requirePassingValidation && isValidationImprovement(iterationBaselineVerifier, verifierResult))
                );
            progress.accept("Iteration " + iteration + " promoted=" + (report != null && report.promoted())
                + " reason=" + selectionReason);
            evolutionIterations.add(new EvolutionReport.EvolutionIteration(
                iteration,
                selected,
                report,
                evaluations,
                selectionReason
            ));

            journal.append("evolution_iteration_completed", envelope.getGoal(), Map.of(
                "taskId", envelope.getId().toString(),
                "iteration", Integer.toString(iteration),
                "proposal", selected == null ? "none" : selected.getTitle(),
                "promoted", Boolean.toString(report != null && report.promoted()),
                "selectionReason", selectionReason
            ));

            if (report == null || !report.promoted()) {
                break;
            }
            currentRun = runTask(envelope, resolvedSuite);
            progress.accept("Post-promotion validation passed=" + currentRun.getVerifierResult().isSuccess()
                + " failures=" + currentRun.getVerifierResult().getFailedChecks());
            if (currentRun.getVerifierResult().isSuccess() && selected != null) {
                RepairMemoryStore.RepairLesson lesson = repairMemoryStore.recordSuccessfulRepair(envelope, selected, iterationBaselineVerifier);
                progress.accept("Saved repair lesson: " + lesson.repairKind() + " successes=" + lesson.successCount());
                journal.append("repair_memory_recorded", lesson.repairKind(), Map.of(
                    "taskId", envelope.getId().toString(),
                    "proposal", selected.getTitle(),
                    "repairKind", lesson.repairKind(),
                    "successCount", Integer.toString(lesson.successCount())
                ));
            }
            promotedAny = true;
            currentProfile = profileWorkspace();
        }

        if (promotedAny) {
            refreshProjectMemoryArtifacts();
        } else {
            persistState();
        }
        return new EvolutionReport(envelope, baselineRun, baselineProfile, evolutionIterations);
    }

    public MemoryBenchmarkReport benchmarkMemory(String query, List<Path> artifactHints, int topK) throws IOException {
        for (Path artifact : artifactHints == null ? List.<Path>of() : artifactHints) {
            try {
                ingestArtifact(artifact);
            } catch (IOException ignored) {
                // Benchmark should continue with what is indexable.
            }
        }
        MemoryBenchmarkReport report = memoryStore.benchmark(query, artifactHints, topK);
        journal.append("memory_benchmark", query, Map.of(
            "winner", report.getWinner().name(),
            "topK", Integer.toString(topK)
        ));
        persistState();
        return report;
    }

    public PdfDistillationReport distillPdfCorpus(List<Path> pdfPaths) throws IOException {
        PdfDistillationReport report = pdfDistillationEngine.distill(pdfPaths);
        journal.append("pdf_corpus_distilled", Integer.toString(report.getUniqueChunkCount()), Map.of(
            "sources", Integer.toString(report.getSourceFiles().size()),
            "rawChunks", Integer.toString(report.getRawChunkCount()),
            "duplicatesRemoved", Integer.toString(report.getDuplicateChunkCount())
        ));
        persistState();
        return report;
    }

    public String loadReplay(java.util.UUID taskId) throws IOException {
        return traceReplayStore.load(taskId);
    }

    public WorkspaceProfile profileWorkspace() throws IOException {
        return validationSuiteFactory.profileWorkspace();
    }

    public CanonicalTruthAuditor.CanonicalAuditReport auditCanonicalTruth() throws IOException {
        return auditCanonicalTruthInternal(null);
    }

    public CanonicalTruthAuditor.CanonicalAuditReport auditCanonicalTruth(List<Path> sourceDocs) throws IOException {
        return auditCanonicalTruthInternal(sourceDocs);
    }

    private CanonicalTruthAuditor.CanonicalAuditReport auditCanonicalTruthInternal(List<Path> sourceDocs) throws IOException {
        CanonicalTruthAuditor.CanonicalAuditReport report =
            (sourceDocs == null || sourceDocs.isEmpty())
                ? canonicalTruthAuditor.audit()
                : canonicalTruthAuditor.audit(sourceDocs);

        for (Path artifactPath : report.artifactPaths()) {
            try {
                ingestArtifact(artifactPath);
            } catch (IOException ignored) {
                // Keep truth audit artifacts even if one ingest fails.
            }
        }

        journal.append("canonical_truth_audited", Integer.toString(report.claims().size()), Map.of(
            "verified", Integer.toString(report.verifiedClaims()),
            "scaffold", Integer.toString(report.scaffoldClaims()),
            "speculative", Integer.toString(report.speculativeClaims()),
            "outline", report.outlinePath().toString(),
            "matrix", report.matrixPath().toString(),
            "protocol", report.protocolPath().toString()
        ));
        persistState();
        return report;
    }

    public ProjectMemoryArtifactWriter.MemoryArtifacts refreshProjectMemoryArtifacts() throws IOException {
        return refreshProjectMemoryArtifacts(false, List.of());
    }

    public ProjectMemoryArtifactWriter.MemoryArtifacts refreshProjectMemoryArtifacts(boolean includeCanon) throws IOException {
        return refreshProjectMemoryArtifacts(includeCanon, List.of());
    }

    public ProjectMemoryArtifactWriter.MemoryArtifacts refreshProjectMemoryArtifacts(
            boolean includeCanon,
            List<Path> canonicalSources) throws IOException {
        CanonicalTruthAuditor.CanonicalAuditReport auditReport = null;
        if (includeCanon) {
            auditReport = (canonicalSources == null || canonicalSources.isEmpty())
                ? auditCanonicalTruth()
                : auditCanonicalTruth(canonicalSources);
        }

        Map<String, Object> status = getStatus();
        ProjectMemoryArtifactWriter.MemoryArtifacts artifacts = projectMemoryArtifactWriter.writeArtifacts(status);
        for (Path artifactPath : artifacts.artifactPaths()) {
            try {
                ingestArtifact(artifactPath);
            } catch (IOException ignored) {
                // Preserve the generated artifact even if one memory ingest fails.
            }
        }
        journal.append("project_memory_refreshed", Integer.toString(artifacts.artifactPaths().size()), Map.of(
            "canonicalDoc", artifacts.canonicalDoc().toString(),
            "architectureDoc", artifacts.architectureDoc().toString(),
            "goalsDoc", artifacts.goalsDoc().toString(),
            "operationalMemory", artifacts.operationalMemory().toString(),
            "forecastMemory", artifacts.forecastMemory().toString(),
            "canon", Boolean.toString(includeCanon),
            "canonClaims", Integer.toString(auditReport == null ? 0 : auditReport.claims().size())
        ));
        persistState();
        return artifacts;
    }

    public FoundationalSubstrateReport bootstrapFoundationalSubstrates(List<Path> modelRoots) throws IOException {
        FoundationalSubstrateReport report = foundationalSubstrateBootstrapper.bootstrap(modelRoots);
        for (Path artifactPath : report.artifactPaths()) {
            try {
                ingestArtifact(artifactPath);
            } catch (IOException ignored) {
                // Preserve generated substrate artifacts even if one memory ingest fails.
            }
        }
        journal.append("foundational_substrates_bootstrapped", Integer.toString(report.modelDescriptors().size()), Map.of(
            "artifacts", Integer.toString(report.artifactPaths().size()),
            "models", Integer.toString(report.modelDescriptors().size()),
            "families", Integer.toString(report.familyCounts().size()),
            "inventory", report.modelInventoryArtifact().toString()
        ));
        persistState();
        return report;
    }

    public SubstrateResult inferLocalModel(
            String prompt,
            List<Path> modelRoots,
            String modelId,
            String modelRunner,
            String modelBackend,
            int maxTokens) throws IOException {
        TaskEnvelope.Builder builder = TaskEnvelope.builder(prompt == null ? "" : prompt)
            .strategy("substrate", "local-model")
            .mode(TaskMode.SUPERVISED);
        if (modelId != null && !modelId.isBlank()) {
            builder.strategy("model.id", modelId);
        }
        if (modelRunner != null && !modelRunner.isBlank()) {
            builder.strategy("model.runner", modelRunner);
        }
        if (modelBackend != null && !modelBackend.isBlank()) {
            builder.strategy("model.backend", modelBackend);
        }
        builder.strategy("model.maxTokens", Integer.toString(Math.max(1, maxTokens)));
        if (modelRoots != null && !modelRoots.isEmpty() && modelRoots.get(0) != null) {
            builder.strategy("model.root", modelRoots.get(0).toString());
        }

        TaskEnvelope envelope = builder.build();
        List<MemoryHit> hits = memoryStore.search(prompt == null ? "" : prompt, List.of(), Math.min(5, envelope.getBudget().getMaxFilesToRead()));
        SubstrateSession session = localModelRuntimeAdapter.loadContext(envelope, hits);
        SubstrateResult result = localModelRuntimeAdapter.act(
            session,
            SubstrateAction.runInference(prompt == null ? "" : prompt, modelId, Math.max(1, maxTokens))
        );
        journal.append("local_model_inference", prompt == null ? "" : prompt, Map.of(
            "taskId", envelope.getId().toString(),
            "modelId", modelId == null ? "" : modelId,
            "backend", result.getMetadata().getOrDefault("backend", ""),
            "success", Boolean.toString(result.isSuccess())
        ));
        persistState();
        return result;
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        CanonicalTruthAuditor.CanonicalAuditStatus truthStatus = canonicalTruthAuditor.loadLatestStatus();
        status.put("workspaceRoot", configuration.getWorkspaceRoot().toString());
        status.put("stateRoot", configuration.getStateRoot().toString());
        status.put("offlineEnforced", configuration.isOfflineEnforced());
        status.put("documents", memoryStore.getDocumentCount());
        status.put("trackedArtifacts", memoryStore.snapshotTrackedArtifacts().size());
        status.put("journalEntries", journal.size());
        status.put("merkleRoot", journal.getLastMerkleRoot());
        status.put("router", router.getStatistics());
        status.put("causal", memoryStore.causalStatistics());
        status.put("ingestionPolicy", memoryStore.ingestionPolicyStatistics());
        status.put("f0Available", f0RuntimeAdapter.isCargoAvailable());
        status.put("repairLessons", repairMemoryStore.size());
        status.put("repairTemplates", repairMemoryStore.templateCount());
        status.put("truthClaimsVerified", truthStatus.verifiedClaims());
        status.put("truthClaimsScaffold", truthStatus.scaffoldClaims());
        status.put("truthClaimsSpeculative", truthStatus.speculativeClaims());
        status.put("canonLastRefreshAt", truthStatus.refreshedAt() == null ? "" : truthStatus.refreshedAt().toString());
        return status;
    }

    public HybridMemoryStore getMemoryStore() {
        return memoryStore;
    }

    public MerkleAppendOnlyJournal getJournal() {
        return journal;
    }

    public TraceReplayStore getTraceReplayStore() {
        return traceReplayStore;
    }

    public KernelConfiguration getConfiguration() {
        return configuration;
    }

    private SubstrateAdapter chooseAdapter(TaskEnvelope envelope) {
        if (envelope.requestsLocalModelRuntime()) {
            return localModelRuntimeAdapter;
        }
        return envelope.requestsF0Runtime() ? f0RuntimeAdapter : userSpaceAdapter;
    }

    private ValidationSuite effectiveSuite(ValidationSuite providedSuite) throws IOException {
        ValidationSuite defaultSuite = validationSuiteFactory.resolveDefaultSuite();
        if (providedSuite == null) {
            return defaultSuite;
        }
        if (providedSuite.isEmpty()) {
            return defaultSuite.merge(providedSuite);
        }
        return defaultSuite.merge(providedSuite);
    }

    private void restoreState() throws IOException {
        KernelStateStore.KernelStateSnapshot snapshot = stateStore.load();
        memoryStore.restoreState(
            snapshot.documents(),
            snapshot.trackedArtifacts(),
            snapshot.causalNodes(),
            snapshot.causalEdges(),
            snapshot.causalAnchors()
        );
        router.restoreState(snapshot.routerState());
    }

    private void persistState() throws IOException {
        stateStore.persist(memoryStore, router);
    }

    private WorkspaceProfile profileWorkspace(Path workspaceRoot) throws IOException {
        return new ValidationSuiteFactory(configuration.withWorkspaceRoot(workspaceRoot)).profileWorkspace();
    }

    private EvolutionReport.ProposalEvaluation evaluateProposal(
            TaskEnvelope envelope,
            MutationProposal proposal,
            ShadowMutationEngine.MutationPreview preview,
            WorkspaceProfile baselineProfile,
            VerifierResult baselineVerifier) throws IOException {
        List<String> notes = new ArrayList<>();
        double score = proposal.getPriorityScore();
        int baselineValidationCommands = baselineProfile.getRecommendedValidationSuite().allCommands().size();
        int validationCommandDelta = 0;
        boolean manifestAdded = false;

        if (preview.riskAccepted()) {
            notes.add("risk.accepted");
        } else {
            notes.add("risk.rejected");
            score -= 1.20;
        }

        boolean validationPassed = preview.verifierResult() != null && preview.verifierResult().isSuccess();
        boolean validationImproved = preview.verifierResult() != null
            && !validationPassed
            && isValidationImprovement(baselineVerifier, preview.verifierResult());
        if (validationPassed) {
            notes.add("shadow.validation.pass");
            score += 0.80;
        } else if (validationImproved) {
            notes.add("shadow.validation.improved");
            score += 0.42;
        } else {
            notes.add("shadow.validation.fail");
            score -= 0.80;
        }

        if (preview.riskAccepted() && preview.shadowRoot() != null) {
            WorkspaceProfile shadowProfile = profileWorkspace(preview.shadowRoot());
            validationCommandDelta = shadowProfile.getRecommendedValidationSuite().allCommands().size() - baselineValidationCommands;
            manifestAdded = !baselineProfile.hasOfflineToolchainManifest() && shadowProfile.hasOfflineToolchainManifest();
            if (validationCommandDelta != 0) {
                notes.add("validation.commands.delta=" + validationCommandDelta);
                score += validationCommandDelta * 0.45;
            }
            if (manifestAdded) {
                notes.add("offline.manifest.added");
                score += 0.55;
            }
        }

        long generatedTargets = proposal.getTargetPaths().stream()
            .map(Path::toString)
            .map(path -> path.replace('\\', '/'))
            .filter(path -> path.contains("generated") || path.contains("offline-toolchain") || path.endsWith("GeneratedTest.java"))
            .count();
        if (generatedTargets > 0) {
            notes.add("generated.targets=" + generatedTargets);
            score += Math.min(0.30, generatedTargets * 0.10);
        }
        if (proposal.getCandidate().getScope() == MutationCandidate.Scope.GENERATED_MODULES_ONLY) {
            notes.add("scope.generated_modules_only");
            score += 0.10;
        }
        if (!proposal.getEvidenceTags().isEmpty()) {
            notes.add("evidence.tags=" + String.join(",", proposal.getEvidenceTags()));
            score += Math.min(0.20, proposal.getEvidenceTags().size() * 0.08);
            if (proposal.getEvidenceTags().contains("verifier.failure")) {
                notes.add("verifier.failure.seed");
                score += 0.22;
            }
            if (proposal.getEvidenceTags().contains("verifier.diagnostic")) {
                notes.add("verifier.diagnostic.seed");
                score += 0.10;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.parse")) {
                notes.add("verifier.compiler.parse");
                score += 0.14;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.symbol")) {
                notes.add("verifier.compiler.symbol");
                score += 0.12;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.import")) {
                notes.add("verifier.compiler.import");
                score += 0.16;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.signature")) {
                notes.add("verifier.compiler.signature");
                score += 0.16;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.method_missing")) {
                notes.add("verifier.compiler.method_missing");
                score += 0.15;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.field_missing")) {
                notes.add("verifier.compiler.field_missing");
                score += 0.15;
            }
            if (proposal.getEvidenceTags().contains("verifier.compiler.type_synthesis")) {
                notes.add("verifier.compiler.type_synthesis");
                score += 0.19;
            }
        if (proposal.getEvidenceTags().contains("verifier.compiler.multi_type_synthesis")) {
            notes.add("verifier.compiler.multi_type_synthesis");
            score += 0.24;
        }
        if (proposal.getEvidenceTags().contains("verifier.compiler.dependent_type_synthesis")) {
            notes.add("verifier.compiler.dependent_type_synthesis");
            score += 0.08;
        }
        if (proposal.getEvidenceTags().contains("verifier.compiler.package")) {
            notes.add("verifier.compiler.package");
            score += 0.17;
        }
            if (proposal.getEvidenceTags().contains("verifier.compiler.type_name")) {
                notes.add("verifier.compiler.type_name");
                score += 0.17;
            }
            if (proposal.getEvidenceTags().contains("corpus.failure")) {
                notes.add("corpus.failure.seed");
                score += 0.18;
            }
            if (proposal.getEvidenceTags().contains("corpus.test-idea")) {
                notes.add("corpus.test-idea.seed");
                score += 0.12;
            }
            if (proposal.getEvidenceTags().contains("corpus.causal")) {
                notes.add("corpus.causal.seed");
                score += 0.14;
            }
        }

        RepairMemoryStore.RepairMatch repairMatch = repairMemoryStore.bestMatch(envelope, proposal);
        if (repairMatch.matched()) {
            notes.add("repair.memory.kind=" + repairMatch.lesson().repairKind());
            notes.add("repair.memory.notes=" + String.join(",", repairMatch.notes()));
            score += repairMatch.bonus();
        }

        return new EvolutionReport.ProposalEvaluation(
            proposal,
            score,
            preview.riskAccepted(),
            validationPassed,
            validationImproved,
            validationCommandDelta,
            manifestAdded,
            notes
        );
    }

    private EvolutionPreviewStep previewEvolutionIteration(
            TaskEnvelope envelope,
            int iteration,
            KernelRunResult currentRun,
            WorkspaceProfile currentProfile,
            ValidationSuite resolvedSuite,
            int proposalLimit) throws IOException {
        List<MutationProposal> proposals = proposeMutations(envelope, proposalLimit, currentRun.getVerifierResult());
        if (proposals.isEmpty()) {
            return null;
        }

        VerifierResult iterationBaselineVerifier = currentRun.getVerifierResult();
        List<EvolutionReport.ProposalEvaluation> evaluations = new ArrayList<>();
        for (MutationProposal proposal : proposals) {
            try (ShadowMutationEngine.MutationPreview preview = mutationEngine.preview(proposal.getCandidate(), resolvedSuite)) {
                evaluations.add(evaluateProposal(envelope, proposal, preview, currentProfile, iterationBaselineVerifier));
            }
        }

        EvolutionReport.ProposalEvaluation selectedEvaluation = evaluations.stream()
            .filter(EvolutionReport.ProposalEvaluation::viable)
            .max(Comparator.comparingDouble(EvolutionReport.ProposalEvaluation::totalScore))
            .orElse(null);

        MutationProposal selected = selectedEvaluation == null ? null : selectedEvaluation.proposal();
        String selectionReason = selectedEvaluation == null
            ? "No proposal survived shadow validation."
            : "Preview selected " + selected.getTitle()
                + " with score "
                + String.format(java.util.Locale.ROOT, "%.3f", selectedEvaluation.totalScore());

        ShadowMutationEngine.MutationPreview selectedPreview = selected == null
            ? null
            : mutationEngine.preview(selected.getCandidate(), resolvedSuite);

        return new EvolutionPreviewStep(
            new EvolutionReport.EvolutionIteration(
                iteration,
                selected,
                null,
                evaluations,
                selectionReason
            ),
            selectedPreview
        );
    }

    private double proposalPriority(TaskEnvelope envelope, MutationProposal proposal) {
        return proposal.getPriorityScore() + repairMemoryStore.scoreBonus(envelope, proposal);
    }

    private ValidationSuite rebaseValidationSuite(ValidationSuite suite, Path sourceWorkspaceRoot, Path targetWorkspaceRoot) {
        if (suite == null || sourceWorkspaceRoot == null || targetWorkspaceRoot == null) {
            return suite;
        }
        Path normalizedSource = sourceWorkspaceRoot.toAbsolutePath().normalize();
        Path normalizedTarget = targetWorkspaceRoot.toAbsolutePath().normalize();
        if (normalizedSource.equals(normalizedTarget)) {
            return suite;
        }
        return new ValidationSuite(
            rebaseValidationCommands(suite.getBuildChecks(), normalizedSource, normalizedTarget),
            rebaseValidationCommands(suite.getTests(), normalizedSource, normalizedTarget),
            rebaseValidationCommands(suite.getBenchmarks(), normalizedSource, normalizedTarget),
            suite.getInvariants()
        );
    }

    private List<ValidationCommand> rebaseValidationCommands(
            List<ValidationCommand> commands,
            Path sourceWorkspaceRoot,
            Path targetWorkspaceRoot) {
        List<ValidationCommand> rebased = new ArrayList<>();
        for (ValidationCommand command : commands == null ? List.<ValidationCommand>of() : commands) {
            if (command == null) {
                continue;
            }
            List<String> rewrittenArgs = new ArrayList<>();
            for (String argument : command.getCommand()) {
                rewrittenArgs.add(rebaseArgumentToWorkspace(argument, sourceWorkspaceRoot, targetWorkspaceRoot));
            }
            rebased.add(new ValidationCommand(
                command.getLabel(),
                rebasePathToWorkspace(command.getWorkingDirectory(), sourceWorkspaceRoot, targetWorkspaceRoot),
                rewrittenArgs
            ));
        }
        return List.copyOf(rebased);
    }

    private Path rebasePathToWorkspace(Path path, Path sourceWorkspaceRoot, Path targetWorkspaceRoot) {
        if (path == null) {
            return null;
        }
        Path normalized = path.isAbsolute() ? path.toAbsolutePath().normalize() : path.normalize();
        if (normalized.isAbsolute() && normalized.startsWith(sourceWorkspaceRoot)) {
            return targetWorkspaceRoot.resolve(sourceWorkspaceRoot.relativize(normalized)).normalize();
        }
        return normalized;
    }

    private String rebaseArgumentToWorkspace(String argument, Path sourceWorkspaceRoot, Path targetWorkspaceRoot) {
        if (argument == null || argument.isBlank()) {
            return argument;
        }
        try {
            Path candidate = Path.of(argument);
            if (candidate.isAbsolute()) {
                Path normalized = candidate.toAbsolutePath().normalize();
                if (normalized.startsWith(sourceWorkspaceRoot)) {
                    return targetWorkspaceRoot.resolve(sourceWorkspaceRoot.relativize(normalized)).normalize().toString();
                }
            }
        } catch (InvalidPathException ignored) {
            return argument;
        }
        return argument;
    }

    private boolean isValidationImprovement(VerifierResult baseline, VerifierResult candidate) {
        if (candidate == null || baseline == null) {
            return false;
        }
        return validationPenalty(candidate) < validationPenalty(baseline);
    }

    private int validationPenalty(VerifierResult result) {
        if (result == null) {
            return Integer.MAX_VALUE;
        }
        if (result.isSuccess()) {
            return 0;
        }
        int penalty = result.getFailedChecks().size() * 50;
        for (VerifierDiagnostic diagnostic : result.getErrorDiagnostics()) {
            penalty += diagnosticPenalty(diagnostic);
        }
        penalty += Math.min(12, result.getWarnings().size() * 2);
        return penalty;
    }

    private int diagnosticPenalty(VerifierDiagnostic diagnostic) {
        if (diagnostic == null) {
            return 18;
        }
        String message = diagnostic.getMessage() == null ? "" : diagnostic.getMessage().toLowerCase(java.util.Locale.ROOT);
        String raw = diagnostic.getRaw() == null ? "" : diagnostic.getRaw().toLowerCase(java.util.Locale.ROOT);
        String text = message + " " + raw;
        if (text.contains("reached end of file while parsing")
                || text.contains("class, interface, enum, or record expected")
                || text.contains("illegal character")
                || text.contains("unclosed")) {
            return 30;
        }
        if (text.contains("cannot find symbol")) {
            return 12;
        }
        if (text.contains("cannot be applied to given types")) {
            return 10;
        }
        if (text.contains("bad source file")
                || text.contains("does not contain class")
                || text.contains("package does not exist")
                || text.contains("should be declared in a file named")) {
            return 11;
        }
        return 16;
    }

    private void closeReverse(List<? extends AutoCloseable> resources) throws IOException {
        IOException failure = null;
        for (int index = resources.size() - 1; index >= 0; index--) {
            AutoCloseable resource = resources.get(index);
            if (resource == null) {
                continue;
            }
            try {
                resource.close();
            } catch (Exception exception) {
                IOException ioException = exception instanceof IOException io
                    ? io
                    : new IOException("Failed to close preview resource", exception);
                if (failure == null) {
                    failure = ioException;
                } else {
                    failure.addSuppressed(ioException);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    private record EvolutionPreviewStep(
            EvolutionReport.EvolutionIteration iteration,
            ShadowMutationEngine.MutationPreview selectedPreview) {
    }

    @Override
    public void close() {
        try {
            persistState();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist offline kernel state", e);
        }
    }
}
