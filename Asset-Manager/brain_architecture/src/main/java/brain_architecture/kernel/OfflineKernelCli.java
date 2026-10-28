package brain_architecture.kernel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Minimal command-line interface for the offline kernel.
 */
public final class OfflineKernelCli {
    private OfflineKernelCli() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase(Locale.ROOT);
        switch (command) {
            case "status" -> runStatus(args);
            case "profile" -> runProfile(args);
            case "ingest" -> runIngest(args);
            case "task" -> runTask(args);
            case "benchmark" -> runBenchmark(args);
            case "distill-pdf" -> runDistillPdf(args);
            case "bootstrap-substrates" -> runBootstrapSubstrates(args);
            case "infer-local-model" -> runInferLocalModel(args);
            case "audit-canon" -> runAuditCanon(args);
            case "refresh-memory" -> runRefreshMemory(args);
            case "replay" -> runReplay(args);
            case "propose" -> runPropose(args);
            case "evolve" -> runEvolve(args);
            case "mutate-append" -> runMutateAppend(args);
            case "mutate-replace" -> runMutateReplace(args);
            default -> printUsage();
        }
    }

    private static void runStatus(String[] args) throws Exception {
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            System.out.println(kernel.getStatus());
        }
    }

    private static void runProfile(String[] args) throws Exception {
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            WorkspaceProfile profile = kernel.profileWorkspace();
            System.out.println(Map.of(
                "workspaceRoot", profile.getWorkspaceRoot().toString(),
                "languages", profile.getLanguages(),
                "hasManifest", profile.hasOfflineToolchainManifest(),
                "hasGradle", profile.hasGradleBuild(),
                "hasCargo", profile.hasCargoProject(),
                "hasJavaSources", profile.hasJavaSources(),
                "tools", profile.getToolsAvailable(),
                "validationCommands", profile.getRecommendedValidationSuite().allCommands().stream().map(ValidationCommand::getLabel).toList()
            ));
        }
    }

    private static void runIngest(String[] args) throws Exception {
        requireArgCount(args, 3, "ingest <workspace> <path...>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            for (int i = 2; i < args.length; i++) {
                int count = kernel.ingestArtifact(Path.of(args[i]));
                System.out.println(args[i] + " -> " + count);
            }
            System.out.println(kernel.getStatus());
        }
    }

    private static void runTask(String[] args) throws Exception {
        requireArgCount(args, 3, "task <workspace> <goal> [--artifact path] [--constraint text] [--mode supervised|automated|mutation] [--f0] [--model id] [--model-root path] [--model-runner path] [--model-backend name] [--maxTokens n]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            String goal = args[2];
            TaskEnvelope.Builder builder = TaskEnvelope.builder(goal);
            int index = 3;
            while (index < args.length) {
                String token = args[index];
                switch (token) {
                    case "--artifact" -> {
                        builder.addArtifact(Path.of(args[index + 1]));
                        index += 2;
                    }
                    case "--constraint" -> {
                        builder.addConstraint(args[index + 1]);
                        index += 2;
                    }
                    case "--mode" -> {
                        builder.mode(TaskMode.valueOf(args[index + 1].toUpperCase(Locale.ROOT)));
                        index += 2;
                    }
                    case "--f0" -> {
                        builder.strategy("substrate", "f0");
                        index += 1;
                    }
                    case "--model" -> {
                        builder.strategy("substrate", "local-model");
                        builder.strategy("model.id", args[index + 1]);
                        index += 2;
                    }
                    case "--model-root" -> {
                        builder.strategy("substrate", "local-model");
                        builder.strategy("model.root", args[index + 1]);
                        index += 2;
                    }
                    case "--model-runner" -> {
                        builder.strategy("substrate", "local-model");
                        builder.strategy("model.runner", args[index + 1]);
                        index += 2;
                    }
                    case "--model-backend" -> {
                        builder.strategy("substrate", "local-model");
                        builder.strategy("model.backend", args[index + 1]);
                        index += 2;
                    }
                    case "--maxTokens" -> {
                        builder.strategy("substrate", "local-model");
                        builder.strategy("model.maxTokens", args[index + 1]);
                        index += 2;
                    }
                    default -> {
                        builder.addArtifact(Path.of(token));
                        index += 1;
                    }
                }
            }

            KernelRunResult result = kernel.runTask(builder.build(), ValidationSuite.empty());
            System.out.println(result.getTrace().toSummaryMap());
        }
    }

    private static void runBenchmark(String[] args) throws Exception {
        requireArgCount(args, 3, "benchmark <workspace> <query> [artifact...]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            List<Path> artifacts = new ArrayList<>();
            for (int i = 3; i < args.length; i++) {
                artifacts.add(Path.of(args[i]));
            }
            MemoryBenchmarkReport report = kernel.benchmarkMemory(args[2], artifacts, 5);
            System.out.println(report.toSummaryMap());
        }
    }

    private static void runDistillPdf(String[] args) throws Exception {
        requireArgCount(args, 3, "distill-pdf <workspace> <pdfPath...>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            List<Path> pdfPaths = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                pdfPaths.add(Path.of(args[i]));
            }
            PdfDistillationReport report = kernel.distillPdfCorpus(pdfPaths);
            System.out.println(report.toSummaryMap());
            report.getChunks().stream().limit(8).forEach(chunk -> System.out.println(Map.of(
                "source", chunk.getSourcePath().toString(),
                "label", chunk.getLabel().getSlug(),
                "pages", chunk.getStartPage() + "-" + chunk.getEndPage(),
                "output", chunk.getOutputPath().toString()
            )));
        }
    }

    private static void runBootstrapSubstrates(String[] args) throws Exception {
        requireArgCount(args, 2, "bootstrap-substrates <workspace> [modelRoot...]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            List<Path> roots = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                roots.add(Path.of(args[i]));
            }
            FoundationalSubstrateReport report = kernel.bootstrapFoundationalSubstrates(roots);
            System.out.println(report.toSummaryMap());
        }
    }

    private static void runInferLocalModel(String[] args) throws Exception {
        requireArgCount(args, 3, "infer-local-model <workspace> <prompt> [--model id] [--model-root path] [--model-runner path] [--model-backend name] [--maxTokens n]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            String prompt = args[2];
            String modelId = null;
            String modelRoot = null;
            String modelRunner = null;
            String modelBackend = null;
            int maxTokens = 128;
            int index = 3;
            while (index < args.length) {
                String token = args[index];
                switch (token) {
                    case "--model" -> {
                        modelId = args[index + 1];
                        index += 2;
                    }
                    case "--model-root" -> {
                        modelRoot = args[index + 1];
                        index += 2;
                    }
                    case "--model-runner" -> {
                        modelRunner = args[index + 1];
                        index += 2;
                    }
                    case "--model-backend" -> {
                        modelBackend = args[index + 1];
                        index += 2;
                    }
                    case "--maxTokens" -> {
                        maxTokens = Integer.parseInt(args[index + 1]);
                        index += 2;
                    }
                    default -> {
                        index += 1;
                    }
                }
            }

            SubstrateResult result = kernel.inferLocalModel(
                prompt,
                modelRoot == null ? List.of() : List.of(Path.of(modelRoot)),
                modelId,
                modelRunner,
                modelBackend,
                maxTokens
            );
            System.out.println(Map.of(
                "success", result.isSuccess(),
                "exitCode", result.getExitCode(),
                "metadata", result.getMetadata(),
                "output", result.getOutput()
            ));
        }
    }

    private static void runRefreshMemory(String[] args) throws Exception {
        requireArgCount(args, 2, "refresh-memory <workspace>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            boolean includeCanon = false;
            List<Path> canonSources = new ArrayList<>();
            int index = 2;
            while (index < args.length) {
                String token = args[index];
                switch (token) {
                    case "--canon" -> {
                        includeCanon = true;
                        index += 1;
                    }
                    case "--source" -> {
                        includeCanon = true;
                        canonSources.add(Path.of(args[index + 1]));
                        index += 2;
                    }
                    default -> index += 1;
                }
            }

            ProjectMemoryArtifactWriter.MemoryArtifacts artifacts =
                kernel.refreshProjectMemoryArtifacts(includeCanon, canonSources);
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("artifactPaths", artifacts.artifactPaths());
            output.put("dualCanonDocs", artifacts.dualCanonDocs());
            output.put("canonicalDoc", artifacts.canonicalDoc().toString());
            output.put("architectureDoc", artifacts.architectureDoc().toString());
            output.put("goalsDoc", artifacts.goalsDoc().toString());
            output.put("operationalMemory", artifacts.operationalMemory().toString());
            output.put("forecastMemory", artifacts.forecastMemory().toString());
            output.put("canonIncluded", includeCanon);
            output.put("status", kernel.getStatus());
            System.out.println(output);
        }
    }

    private static void runAuditCanon(String[] args) throws Exception {
        requireArgCount(args, 2, "audit-canon <workspace> [--source path...]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            List<Path> sources = new ArrayList<>();
            int index = 2;
            while (index < args.length) {
                String token = args[index];
                if ("--source".equals(token)) {
                    sources.add(Path.of(args[index + 1]));
                    index += 2;
                } else {
                    sources.add(Path.of(token));
                    index += 1;
                }
            }

            CanonicalTruthAuditor.CanonicalAuditReport report =
                sources.isEmpty() ? kernel.auditCanonicalTruth() : kernel.auditCanonicalTruth(sources);
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("verifiedClaims", report.verifiedClaims());
            output.put("scaffoldClaims", report.scaffoldClaims());
            output.put("speculativeClaims", report.speculativeClaims());
            output.put("sourcePaths", report.sourcePaths());
            output.put("outline", report.outlinePath().toString());
            output.put("matrix", report.matrixPath().toString());
            output.put("protocol", report.protocolPath().toString());
            output.put("artifacts", report.artifactPaths());
            output.put("refreshedAt", report.refreshedAt().toString());
            output.put("status", kernel.getStatus());
            System.out.println(output);
        }
    }

    private static void runReplay(String[] args) throws Exception {
        requireArgCount(args, 3, "replay <workspace> <taskId>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            String replay = kernel.loadReplay(UUID.fromString(args[2]));
            System.out.println(replay);
        }
    }

    private static void runPropose(String[] args) throws Exception {
        requireArgCount(args, 3, "propose <workspace> <goal> [--artifact path] [--constraint text] [--mode supervised|automated|mutation] [--topK n]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            TaskRequest request = parseTaskRequest(args, 2);
            int topK = Integer.parseInt(request.options.getOrDefault("topK", "3"));
            KernelRunResult baseline = kernel.runTask(request.task, ValidationSuite.empty());
            List<MutationProposal> proposals = kernel.proposeMutations(request.task, topK, baseline.getVerifierResult());
            System.out.println(Map.of(
                "baselineOutcome", baseline.getTrace().getOutcome(),
                "baselineFailures", baseline.getVerifierResult().getFailedChecks(),
                "baselineWarnings", baseline.getVerifierResult().getWarnings(),
                "baselineDiagnostics", baseline.getVerifierResult().getDiagnostics().stream().limit(5).map(diagnostic -> Map.of(
                    "tool", diagnostic.getToolLabel(),
                    "file", diagnostic.getFile() == null ? "" : diagnostic.getFile().toString(),
                    "line", diagnostic.getLine() == null ? 0 : diagnostic.getLine(),
                    "column", diagnostic.getColumn() == null ? 0 : diagnostic.getColumn(),
                    "severity", diagnostic.getSeverity().name(),
                    "message", diagnostic.getMessage()
                )).toList()
            ));
            for (MutationProposal proposal : proposals) {
                System.out.println(Map.of(
                    "title", proposal.getTitle(),
                    "score", proposal.getPriorityScore(),
                    "targets", proposal.getTargetPaths(),
                    "gain", proposal.getCandidate().getExpectedGain(),
                    "rationale", proposal.getRationale(),
                    "evidence", proposal.getEvidenceTags()
                ));
            }
        }
    }

    private static void runEvolve(String[] args) throws Exception {
        requireArgCount(args, 3, "evolve <workspace> <goal> [--artifact path] [--constraint text] [--mode supervised|automated|mutation] [--iterations n]");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            TaskRequest request = parseTaskRequest(args, 2);
            int iterations = Integer.parseInt(request.options.getOrDefault("iterations", "1"));
            EvolutionReport report = kernel.evolveTask(request.task, iterations);
            System.out.println(report.toSummaryMap());
            for (EvolutionReport.EvolutionIteration iteration : report.getIterations()) {
                System.out.println(Map.of(
                    "iteration", iteration.iteration(),
                    "proposal", iteration.proposal() == null ? "none" : iteration.proposal().getTitle(),
                    "promoted", iteration.promoted(),
                    "selectionReason", iteration.selectionReason()
                ));
                for (EvolutionReport.ProposalEvaluation evaluation : iteration.evaluations()) {
                    System.out.println(Map.of(
                        "candidate", evaluation.proposal().getTitle(),
                        "score", evaluation.totalScore(),
                        "riskAccepted", evaluation.riskAccepted(),
                        "validationPassed", evaluation.validationPassed(),
                        "validationCommandDelta", evaluation.validationCommandDelta(),
                        "manifestAdded", evaluation.manifestAdded(),
                        "notes", evaluation.notes()
                    ));
                }
            }
        }
    }

    private static void runMutateAppend(String[] args) throws Exception {
        requireArgCount(args, 4, "mutate-append <workspace> <relativePath> <content>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            MutationCandidate candidate = new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.appendFile(Path.of(args[2]), args[3] + System.lineSeparator())),
                "Append content via CLI",
                List.of("CLI append mutation")
            );
            ShadowMutationEngine.MutationReport report = kernel.applyMutationCandidate(candidate, ValidationSuite.empty());
            System.out.println(Map.of(
                "promoted", report.promoted(),
                "verifier", report.verifierResult().isSuccess(),
                "files", report.promotedFiles()
            ));
        }
    }

    private static void runMutateReplace(String[] args) throws Exception {
        requireArgCount(args, 5, "mutate-replace <workspace> <relativePath> <search> <replace>");
        try (OfflineKernel kernel = kernelForArgs(args, 1)) {
            MutationCandidate candidate = new MutationCandidate(
                MutationCandidate.Scope.WHOLE_CODEBASE,
                List.of(MutationOperation.searchReplace(Path.of(args[2]), args[3], args[4])),
                "Replace text via CLI",
                List.of("CLI replace mutation")
            );
            ShadowMutationEngine.MutationReport report = kernel.applyMutationCandidate(candidate, ValidationSuite.empty());
            System.out.println(Map.of(
                "promoted", report.promoted(),
                "verifier", report.verifierResult().isSuccess(),
                "files", report.promotedFiles()
            ));
        }
    }

    private static OfflineKernel kernelForArgs(String[] args, int workspaceIndex) throws Exception {
        Path workspace = Path.of(args.length > workspaceIndex ? args[workspaceIndex] : ".").toAbsolutePath().normalize();
        return new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace));
    }

    private static TaskRequest parseTaskRequest(String[] args, int goalIndex) {
        String goal = args[goalIndex];
        TaskEnvelope.Builder builder = TaskEnvelope.builder(goal);
        java.util.Map<String, String> options = new java.util.LinkedHashMap<>();
        int index = goalIndex + 1;
        while (index < args.length) {
            String token = args[index];
            switch (token) {
                case "--artifact" -> {
                    builder.addArtifact(Path.of(args[index + 1]));
                    index += 2;
                }
                case "--constraint" -> {
                    builder.addConstraint(args[index + 1]);
                    index += 2;
                }
                case "--mode" -> {
                    builder.mode(TaskMode.valueOf(args[index + 1].toUpperCase(Locale.ROOT)));
                    index += 2;
                }
                case "--f0" -> {
                    builder.strategy("substrate", "f0");
                    index += 1;
                }
                case "--model" -> {
                    builder.strategy("substrate", "local-model");
                    builder.strategy("model.id", args[index + 1]);
                    index += 2;
                }
                case "--model-root" -> {
                    builder.strategy("substrate", "local-model");
                    builder.strategy("model.root", args[index + 1]);
                    index += 2;
                }
                case "--model-runner" -> {
                    builder.strategy("substrate", "local-model");
                    builder.strategy("model.runner", args[index + 1]);
                    index += 2;
                }
                case "--model-backend" -> {
                    builder.strategy("substrate", "local-model");
                    builder.strategy("model.backend", args[index + 1]);
                    index += 2;
                }
                case "--maxTokens" -> {
                    builder.strategy("substrate", "local-model");
                    builder.strategy("model.maxTokens", args[index + 1]);
                    index += 2;
                }
                case "--topk", "--topK" -> {
                    options.put("topK", args[index + 1]);
                    index += 2;
                }
                case "--iterations" -> {
                    options.put("iterations", args[index + 1]);
                    index += 2;
                }
                default -> {
                    builder.addArtifact(Path.of(token));
                    index += 1;
                }
            }
        }
        return new TaskRequest(builder.build(), options);
    }

    private static void requireArgCount(String[] args, int minCount, String usage) {
        if (args.length < minCount) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    private static void printUsage() {
        System.out.println("OfflineKernelCli commands:");
        System.out.println("  status <workspace>");
        System.out.println("  profile <workspace>");
        System.out.println("  ingest <workspace> <path...>");
        System.out.println("  task <workspace> <goal> [--artifact path] [--constraint text] [--mode supervised|automated|mutation] [--f0] [--model id] [--model-root path] [--model-runner path] [--model-backend name] [--maxTokens n]");
        System.out.println("  benchmark <workspace> <query> [artifact...]");
        System.out.println("  distill-pdf <workspace> <pdfPath...>");
        System.out.println("  bootstrap-substrates <workspace> [modelRoot...]");
        System.out.println("  infer-local-model <workspace> <prompt> [--model id] [--model-root path] [--model-runner path] [--model-backend name] [--maxTokens n]");
        System.out.println("  audit-canon <workspace> [--source path...]");
        System.out.println("  refresh-memory <workspace> [--canon] [--source path...]");
        System.out.println("  replay <workspace> <taskId>");
        System.out.println("  propose <workspace> <goal> [--artifact path] [--constraint text] [--mode ...] [--topK n]");
        System.out.println("  evolve <workspace> <goal> [--artifact path] [--constraint text] [--mode ...] [--iterations n]");
        System.out.println("  mutate-append <workspace> <relativePath> <content>");
        System.out.println("  mutate-replace <workspace> <relativePath> <search> <replace>");
    }

    private record TaskRequest(TaskEnvelope task, java.util.Map<String, String> options) {
    }
}
