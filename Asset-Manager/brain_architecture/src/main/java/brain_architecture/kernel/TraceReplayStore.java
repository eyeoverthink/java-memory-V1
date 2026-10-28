package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Persists replayable execution traces to local disk.
 */
public final class TraceReplayStore {
    private final Path tracesRoot;

    public TraceReplayStore(Path stateRoot) throws IOException {
        this.tracesRoot = stateRoot.resolve("traces");
        Files.createDirectories(tracesRoot);
    }

    public Path persist(ExecutionTrace trace) throws IOException {
        Path tracePath = tracesRoot.resolve(trace.getTaskId() + ".trace");
        List<String> lines = new ArrayList<>();
        lines.add("taskId=" + trace.getTaskId());
        lines.add("seed=" + trace.getSeed());
        lines.add("strategy=" + nullSafe(trace.getStrategy()));
        lines.add("adapter=" + nullSafe(trace.getAdapterId()));
        lines.add("startedAt=" + trace.getStartedAt());
        lines.add("endedAt=" + trace.getEndedAt());
        lines.add("outcome=" + trace.getOutcome());
        lines.add("[plan]");
        for (PlanStep step : trace.getPlanSteps()) {
            lines.add(step.getIndex() + "|" + step.getHint().name() + "|" + step.getDescription());
        }
        lines.add("[toolCalls]");
        for (ExecutionTrace.ToolCall toolCall : trace.getToolCalls()) {
            lines.add(toolCall.getToolName() + "|" + toolCall.isSuccess() + "|" + String.join(" ", toolCall.getCommand()));
        }
        lines.add("[observations]");
        lines.addAll(trace.getObservations());
        lines.add("[verifier]");
        lines.addAll(trace.getVerifierResults());
        Files.write(tracePath, lines, StandardCharsets.UTF_8);
        return tracePath;
    }

    public String load(UUID taskId) throws IOException {
        Path tracePath = tracesRoot.resolve(taskId + ".trace");
        return Files.readString(tracePath, StandardCharsets.UTF_8);
    }

    public List<Path> listTraceFiles() throws IOException {
        try (var stream = Files.list(tracesRoot)) {
            return stream
                .filter(Files::isRegularFile)
                .sorted()
                .toList();
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
