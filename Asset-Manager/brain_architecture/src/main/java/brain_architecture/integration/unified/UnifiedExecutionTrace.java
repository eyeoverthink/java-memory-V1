package brain_architecture.integration.unified;

import brain_architecture.kernel.ExecutionTrace;
import brain_architecture.kernel.PlanStep;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Unified trace view for replay and audit consumers.
 */
public final class UnifiedExecutionTrace {
    private final UUID taskId;
    private final long seed;
    private final Instant startedAt;
    private final Instant endedAt;
    private final long durationMs;
    private final String strategy;
    private final String adapterId;
    private final String outcome;
    private final List<UnifiedPlanStep> planSteps;
    private final List<UnifiedToolCall> toolCalls;
    private final List<String> observations;
    private final List<String> verifierResults;

    public UnifiedExecutionTrace(
            UUID taskId,
            long seed,
            Instant startedAt,
            Instant endedAt,
            long durationMs,
            String strategy,
            String adapterId,
            String outcome,
            List<UnifiedPlanStep> planSteps,
            List<UnifiedToolCall> toolCalls,
            List<String> observations,
            List<String> verifierResults) {
        this.taskId = taskId;
        this.seed = seed;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationMs = durationMs;
        this.strategy = strategy == null ? "unknown" : strategy;
        this.adapterId = adapterId == null ? "unknown" : adapterId;
        this.outcome = outcome == null ? "unknown" : outcome;
        this.planSteps = Collections.unmodifiableList(new ArrayList<>(planSteps == null ? List.of() : planSteps));
        this.toolCalls = Collections.unmodifiableList(new ArrayList<>(toolCalls == null ? List.of() : toolCalls));
        this.observations = Collections.unmodifiableList(new ArrayList<>(observations == null ? List.of() : observations));
        this.verifierResults = Collections.unmodifiableList(new ArrayList<>(verifierResults == null ? List.of() : verifierResults));
    }

    public static UnifiedExecutionTrace empty(UUID taskId) {
        return new UnifiedExecutionTrace(taskId, 0L, null, null, 0L, "unknown", "unknown", "unknown", List.of(), List.of(), List.of(), List.of());
    }

    public static UnifiedExecutionTrace fromTrace(ExecutionTrace trace) {
        if (trace == null) {
            return empty(null);
        }
        long durationMs = 0L;
        if (trace.getStartedAt() != null && trace.getEndedAt() != null) {
            durationMs = Math.max(0L, Duration.between(trace.getStartedAt(), trace.getEndedAt()).toMillis());
        }
        return new UnifiedExecutionTrace(
            trace.getTaskId(),
            trace.getSeed(),
            trace.getStartedAt(),
            trace.getEndedAt(),
            durationMs,
            trace.getStrategy(),
            trace.getAdapterId(),
            trace.getOutcome(),
            trace.getPlanSteps().stream().map(UnifiedPlanStep::fromPlanStep).toList(),
            trace.getToolCalls().stream().map(UnifiedToolCall::fromToolCall).toList(),
            trace.getObservations(),
            trace.getVerifierResults()
        );
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

    public long getDurationMs() {
        return durationMs;
    }

    public String getStrategy() {
        return strategy;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public String getOutcome() {
        return outcome;
    }

    public List<UnifiedPlanStep> getPlanSteps() {
        return planSteps;
    }

    public List<UnifiedToolCall> getToolCalls() {
        return toolCalls;
    }

    public List<String> getObservations() {
        return observations;
    }

    public List<String> getVerifierResults() {
        return verifierResults;
    }

    public static final class UnifiedPlanStep {
        private final int index;
        private final String description;
        private final String hint;

        public UnifiedPlanStep(int index, String description, String hint) {
            this.index = index;
            this.description = description == null ? "" : description;
            this.hint = hint == null ? "NONE" : hint;
        }

        public static UnifiedPlanStep fromPlanStep(PlanStep step) {
            return new UnifiedPlanStep(
                step.getIndex(),
                step.getDescription(),
                step.getHint() == null ? "NONE" : step.getHint().name()
            );
        }

        public int getIndex() {
            return index;
        }

        public String getDescription() {
            return description;
        }

        public String getHint() {
            return hint;
        }
    }

    public static final class UnifiedToolCall {
        private final String toolName;
        private final List<String> command;
        private final boolean success;
        private final String output;

        public UnifiedToolCall(String toolName, List<String> command, boolean success, String output) {
            this.toolName = toolName == null ? "" : toolName;
            this.command = Collections.unmodifiableList(new ArrayList<>(command == null ? List.of() : command));
            this.success = success;
            this.output = output == null ? "" : output;
        }

        public static UnifiedToolCall fromToolCall(ExecutionTrace.ToolCall toolCall) {
            return new UnifiedToolCall(toolCall.getToolName(), toolCall.getCommand(), toolCall.isSuccess(), toolCall.getOutput());
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
