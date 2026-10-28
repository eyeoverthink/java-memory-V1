package brain_architecture.kernel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Replayable trace of a kernel task.
 */
public final class ExecutionTrace {
    private final UUID taskId;
    private final long seed;
    private final Instant startedAt;
    private Instant endedAt;
    private String strategy;
    private String adapterId;
    private String outcome;
    private final List<PlanStep> planSteps;
    private final List<ToolCall> toolCalls;
    private final List<String> observations;
    private final List<String> verifierResults;

    public ExecutionTrace(UUID taskId, long seed) {
        this.taskId = taskId;
        this.seed = seed;
        this.startedAt = Instant.now();
        this.planSteps = new ArrayList<>();
        this.toolCalls = new ArrayList<>();
        this.observations = new ArrayList<>();
        this.verifierResults = new ArrayList<>();
        this.outcome = "running";
    }

    public UUID getTaskId() {
        return taskId;
    }

    public long getSeed() {
        return seed;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public void setAdapterId(String adapterId) {
        this.adapterId = adapterId;
    }

    public List<PlanStep> getPlanSteps() {
        return Collections.unmodifiableList(planSteps);
    }

    public List<ToolCall> getToolCalls() {
        return Collections.unmodifiableList(toolCalls);
    }

    public List<String> getObservations() {
        return Collections.unmodifiableList(observations);
    }

    public List<String> getVerifierResults() {
        return Collections.unmodifiableList(verifierResults);
    }

    public String getOutcome() {
        return outcome;
    }

    public void addPlanStep(PlanStep step) {
        planSteps.add(step);
    }

    public void addObservation(String observation) {
        if (observation != null && !observation.isBlank()) {
            observations.add(observation);
        }
    }

    public void addVerifierResult(String result) {
        if (result != null && !result.isBlank()) {
            verifierResults.add(result);
        }
    }

    public void addToolCall(String toolName, List<String> command, boolean success, String output) {
        toolCalls.add(new ToolCall(toolName, command, success, output));
    }

    public void finish(String outcome) {
        this.outcome = outcome;
        this.endedAt = Instant.now();
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("taskId", taskId.toString());
        summary.put("seed", seed);
        summary.put("strategy", strategy);
        summary.put("adapterId", adapterId);
        summary.put("planSteps", planSteps.size());
        summary.put("toolCalls", toolCalls.size());
        summary.put("observations", observations.size());
        summary.put("verifierResults", verifierResults.size());
        summary.put("outcome", outcome);
        return summary;
    }

    /**
     * A single tool call in the trace.
     */
    public static final class ToolCall {
        private final String toolName;
        private final List<String> command;
        private final boolean success;
        private final String output;

        public ToolCall(String toolName, List<String> command, boolean success, String output) {
            this.toolName = toolName;
            this.command = command == null ? List.of() : List.copyOf(command);
            this.success = success;
            this.output = output;
        }

        public String getToolName() {
            return toolName;
        }

        public List<String> getCommand() {
            return command;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getOutput() {
            return output;
        }
    }
}
