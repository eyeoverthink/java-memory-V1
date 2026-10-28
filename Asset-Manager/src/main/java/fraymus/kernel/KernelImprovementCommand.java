package fraymus.kernel;

import brain_architecture.kernel.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/** Direct operator route to the existing kernel. No model response is treated as an execution result. */
public final class KernelImprovementCommand {
    public record Request(Path workspace, String goal, List<Path> artifacts, int iterations,
                          boolean apply, boolean status, boolean help) { }

    public static boolean isSelfImprovementGoal(String goal) {
        return goal != null && goal.toLowerCase(Locale.ROOT).matches("(?s).*\\bself[- ]+improv[a-z]*\\b.*");
    }

    public static Path defaultWorkspace() {
        String configured = System.getProperty("fraynix.kernelWorkspace");
        if (configured != null && !configured.isBlank()) return Path.of(configured).toAbsolutePath().normalize();
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        for (Path candidate = current; candidate != null; candidate = candidate.getParent()) {
            if (Files.isRegularFile(candidate.resolve("offline-toolchain.toml"))) return candidate;
        }
        return current;
    }

    public static Request parse(String payload, Path defaultWorkspace) {
        List<String> tokens = tokenize(payload == null ? "" : payload);
        Path workspace = defaultWorkspace;
        List<Path> artifacts = new ArrayList<>();
        List<String> goal = new ArrayList<>();
        int iterations = 1;
        boolean apply = false, preview = false, literal = false;
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (literal) { goal.add(token); continue; }
            switch (token) {
                case "--" -> literal = true;
                case "--apply" -> apply = true;
                case "--preview", "--plan" -> preview = true;
                case "--workspace", "--artifact", "--iterations" -> {
                    if (++i >= tokens.size() || tokens.get(i).startsWith("--"))
                        throw new IllegalArgumentException("Missing value for " + token);
                    String value = tokens.get(i);
                    if (token.equals("--workspace")) workspace = defaultWorkspace.resolve(value).normalize();
                    else if (token.equals("--artifact")) artifacts.add(Path.of(value));
                    else {
                        try { iterations = Integer.parseInt(value); }
                        catch (NumberFormatException e) { throw new IllegalArgumentException("--iterations requires a positive integer"); }
                        if (iterations < 1) throw new IllegalArgumentException("--iterations must be at least 1");
                    }
                }
                default -> {
                    if (token.startsWith("--")) throw new IllegalArgumentException("Unknown option: " + token);
                    goal.add(token);
                }
            }
        }
        if (apply && preview) throw new IllegalArgumentException("Choose --apply or --preview, not both");
        String text = String.join(" ", goal);
        boolean status = text.equalsIgnoreCase("status");
        boolean help = text.isBlank() || text.equalsIgnoreCase("help");
        if ((status || help) && apply) throw new IllegalArgumentException("--apply requires a concrete goal");
        workspace = workspace.toAbsolutePath().normalize();
        List<Path> relativeArtifacts = new ArrayList<>();
        for (Path artifact : artifacts) {
            Path absolute = workspace.resolve(artifact).normalize();
            if (!absolute.startsWith(workspace)) throw new IllegalArgumentException("Artifact must be inside the workspace: " + artifact);
            relativeArtifacts.add(workspace.relativize(absolute));
        }
        return new Request(workspace, text, List.copyOf(relativeArtifacts), iterations, apply, status, help);
    }

    // Preserve Windows backslashes. Quotes group paths/goals; no shell evaluation or expansion occurs.
    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char quote = 0;
        for (char c : input.toCharArray()) {
            if (quote != 0) {
                if (c == quote) quote = 0; else token.append(c);
            } else if ((c == '"' || c == '\'') && token.length() == 0) {
                quote = c;
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) { tokens.add(token.toString()); token.setLength(0); }
            } else token.append(c);
        }
        if (quote != 0) throw new IllegalArgumentException("Unclosed quote");
        if (token.length() > 0) tokens.add(token.toString());
        return tokens;
    }

    public void execute(String payload, PrintStream output) {
        Request request;
        try { request = parse(payload, defaultWorkspace()); }
        catch (IllegalArgumentException e) { output.println("[IMPROVE] " + e.getMessage()); return; }
        if (request.help()) { printHelp(output); return; }
        List<String> events = new ArrayList<>();
        Consumer<String> progress = message -> {
            events.add(message);
            output.println("[IMPROVE] " + message);
        };
        Path reportPath = null;
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("startedAt", Instant.now().toString());
        report.put("workspace", request.workspace().toString());
        report.put("goal", request.goal());
        report.put("mode", request.status() ? "status" : request.apply() ? "apply" : "preview");
        report.put("events", events);
        try {
            if (!Files.isDirectory(request.workspace())) throw new IOException("Workspace directory does not exist");
            for (Path artifact : request.artifacts()) {
                Path file = request.workspace().resolve(artifact);
                if (!Files.isRegularFile(file) || !file.toRealPath().startsWith(request.workspace().toRealPath()))
                    throw new IOException("Artifact must be an existing file inside the workspace: " + artifact);
            }
            Path state = request.workspace().resolve(".northstar-kernel");
            Files.createDirectories(state);
            try (FileChannel channel = FileChannel.open(state.resolve("shell-improvement.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock lock = channel.tryLock()) {
                if (lock == null) throw new IOException("Another shell improvement is already using this workspace");
                try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(request.workspace()))) {
                    progress.accept("Workspace=" + request.workspace());
                    if (request.status()) {
                        output.println(new JSONObject(kernel.getStatus()).toString(2));
                        progress.accept("Reports=" + state.resolve("reports"));
                        return;
                    }
                    TaskEnvelope.Builder taskBuilder = TaskEnvelope.builder(request.goal()).id(UUID.randomUUID()).mode(TaskMode.MUTATION)
                        .addConstraint("offline only");
                    request.artifacts().forEach(taskBuilder::addArtifact);
                    TaskEnvelope task = taskBuilder.build();
                    report.put("taskId", task.getId().toString());
                    reportPath = state.resolve("reports/improvement-" + task.getId() + ".json");
                    progress.accept("Mode=" + report.get("mode") + " iterations=" + request.iterations());
                    progress.accept("Report=" + reportPath);
                    WorkspaceProfile profile = kernel.profileWorkspace();
                    ValidationSuite suite = profile.getRecommendedValidationSuite();
                    if (!profile.hasOfflineToolchainManifest() || suite.allCommands().isEmpty())
                        throw new IOException("Configure offline-toolchain.toml with an executable verifier before improvement");
                    List<Map<String, Object>> checks = suite.allCommands().stream().map(check -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("label", check.getLabel()); row.put("command", check.getCommand());
                        row.put("directory", String.valueOf(check.getWorkingDirectory()));
                        progress.accept("Check " + check.getLabel() + ": " + check.getCommand());
                        return row;
                    }).toList();
                    report.put("validationCommands", checks);
                    report.put("stateBefore", kernel.getStatus());
                    EvolutionReport evolution;
                    if (request.apply()) {
                        evolution = kernel.evolveTask(task, request.iterations(), true, progress);
                    } else {
                        progress.accept("Baseline validation started; preview writes traces but does not promote source changes.");
                        KernelRunResult baseline = kernel.runTask(task, suite);
                        progress.accept("Baseline passed=" + baseline.getVerifierResult().isSuccess()
                            + " failures=" + baseline.getVerifierResult().getFailedChecks());
                        progress.accept("Generating proposals and testing candidates in shadow workspaces.");
                        evolution = kernel.previewEvolution(task, suite, baseline, request.iterations(), 5);
                    }
                    report.put("baseline", verifier(evolution.getBaselineRun().getVerifierResult()));
                    report.put("replayTrace", String.valueOf(evolution.getBaselineRun().getReplayTracePath()));
                    List<Map<String, Object>> iterations = new ArrayList<>();
                    for (EvolutionReport.EvolutionIteration iteration : evolution.getIterations()) {
                        List<Map<String, Object>> candidates = new ArrayList<>();
                        for (EvolutionReport.ProposalEvaluation evaluation : iteration.evaluations()) {
                            Map<String, Object> row = proposal(evaluation.proposal());
                            row.put("validationPassed", evaluation.validationPassed());
                            row.put("validationImproved", evaluation.validationImproved());
                            row.put("riskAccepted", evaluation.riskAccepted());
                            row.put("eligibleForShellPromotion", evaluation.riskAccepted() && evaluation.validationPassed());
                            row.put("notes", evaluation.notes());
                            row.put("score", evaluation.totalScore());
                            candidates.add(row);
                            progress.accept("Candidate=" + evaluation.proposal().getTitle()
                                + " targets=" + evaluation.proposal().getTargetPaths()
                                + " validationPassed=" + evaluation.validationPassed()
                                + " eligible=" + (evaluation.riskAccepted() && evaluation.validationPassed()));
                        }
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("iteration", iteration.iteration()); row.put("candidates", candidates);
                        row.put("selected", iteration.proposal() == null ? "none" : iteration.proposal().getTitle());
                        row.put("promoted", iteration.promoted()); row.put("reason", iteration.selectionReason());
                        if (iteration.mutationReport() != null) {
                            row.put("promotionValidation", verifier(iteration.mutationReport().verifierResult()));
                            row.put("promotedFiles", iteration.mutationReport().promotedFiles().stream().map(Path::toString).toList());
                        }
                        iterations.add(row);
                        progress.accept("Iteration " + iteration.iteration() + " promoted=" + iteration.promoted()
                            + " reason=" + iteration.selectionReason());
                    }
                    report.put("iterations", iterations);
                    report.put("promoted", evolution.hasPromotedMutation());
                    report.put("stateAfter", kernel.getStatus());
                    report.put("outcome", request.apply()
                        ? (evolution.hasPromotedMutation() ? "promoted" : "no_promotion") : "preview_completed");
                    progress.accept("Repair memory: lessons=" + kernel.getStatus().get("repairLessons")
                        + " templates=" + kernel.getStatus().get("repairTemplates"));
                    if (!request.apply()) progress.accept("Preview complete. Use system improve --apply with the same goal/options to revalidate and promote.");
                    else if (evolution.hasPromotedMutation()) progress.accept("Source updated. Restart/rebuild the app to load changed classes.");
                }
            }
        } catch (Exception e) {
            report.put("outcome", "error");
            report.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            progress.accept("Run failed: " + report.get("error"));
        } finally {
            if (reportPath != null) {
                report.put("finishedAt", Instant.now().toString());
                try {
                    Files.createDirectories(reportPath.getParent());
                    Path temporary = reportPath.resolveSibling(reportPath.getFileName() + ".tmp");
                    Files.writeString(temporary, new JSONObject(report).toString(2));
                    try { Files.move(temporary, reportPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
                    catch (AtomicMoveNotSupportedException e) { Files.move(temporary, reportPath, StandardCopyOption.REPLACE_EXISTING); }
                    output.println("[IMPROVE] Saved report: " + reportPath);
                } catch (IOException e) { output.println("[IMPROVE] Could not save report: " + e.getMessage()); }
            }
        }
    }

    private static Map<String, Object> verifier(VerifierResult result) {
        if (result == null) return Map.of("ran", false);
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("passed", result.isSuccess()); row.put("passedChecks", result.getPassedChecks());
        row.put("failedChecks", result.getFailedChecks()); row.put("warnings", result.getWarnings());
        row.put("diagnostics", result.getDiagnostics().stream().map(d -> Map.of(
            "tool", d.getToolLabel(), "file", String.valueOf(d.getFile()),
            "line", d.getLine() == null ? 0 : d.getLine(), "message", d.getMessage())).toList());
        return row;
    }

    private static Map<String, Object> proposal(MutationProposal proposal) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("title", proposal.getTitle()); row.put("rationale", proposal.getRationale());
        row.put("evidence", proposal.getEvidenceTags());
        row.put("operations", proposal.getCandidate().getOperations().stream().map(operation -> {
            Map<String, Object> edit = new LinkedHashMap<>();
            edit.put("type", operation.getType().name()); edit.put("path", operation.getRelativePath().toString());
            edit.put("content", operation.getContent()); edit.put("search", operation.getSearchText());
            edit.put("replacement", operation.getReplacementText()); return edit;
        }).toList());
        return row;
    }

    public static void printHelp(PrintStream out) {
        out.println("system improve [--preview|--apply] [--workspace \"path\"] [--artifact \"relative file\"] [--iterations N] <goal>");
        out.println("system improve status [--workspace \"path\"]");
        out.println("Default: preview one iteration. --apply revalidates and requires every configured check to pass.");
        out.println("Workspace: -Dfraynix.kernelWorkspace, otherwise nearest ancestor with offline-toolchain.toml.");
        out.println("Validation covers that manifest's commands. Inspect reports before claiming behavior beyond those checks.");
        out.println("Reports/replay/repair memory persist under <workspace>/.northstar-kernel. No chat model is needed.");
    }
}
