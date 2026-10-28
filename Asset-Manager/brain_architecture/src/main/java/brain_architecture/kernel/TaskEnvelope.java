package brain_architecture.kernel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Typed task description for the offline kernel.
 */
public final class TaskEnvelope {
    private final UUID id;
    private final String goal;
    private final List<String> constraints;
    private final List<Path> artifacts;
    private final TaskBudget budget;
    private final TaskMode mode;
    private final Map<String, String> strategyMetadata;

    private TaskEnvelope(Builder builder) {
        this.goal = Objects.requireNonNull(builder.goal, "goal");
        this.constraints = Collections.unmodifiableList(new ArrayList<>(builder.constraints));
        this.artifacts = Collections.unmodifiableList(new ArrayList<>(builder.artifacts));
        this.budget = builder.budget != null ? builder.budget : TaskBudget.standard();
        this.mode = builder.mode != null ? builder.mode : TaskMode.SUPERVISED;
        this.strategyMetadata = Collections.unmodifiableMap(new LinkedHashMap<>(builder.strategyMetadata));
        this.id = builder.id != null ? builder.id : deterministicId(goal, constraints, artifacts, mode, strategyMetadata);
    }

    private static UUID deterministicId(
            String goal,
            List<String> constraints,
            List<Path> artifacts,
            TaskMode mode,
            Map<String, String> strategyMetadata) {
        StringBuilder seed = new StringBuilder(goal).append('|').append(mode.name());
        for (String constraint : constraints) {
            seed.append('|').append(constraint);
        }
        for (Path artifact : artifacts) {
            seed.append('|').append(artifact.normalize());
        }
        strategyMetadata.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> seed.append('|').append(entry.getKey()).append('=').append(entry.getValue()));
        return UUID.nameUUIDFromBytes(seed.toString().getBytes(StandardCharsets.UTF_8));
    }

    public UUID getId() {
        return id;
    }

    public String getGoal() {
        return goal;
    }

    public List<String> getConstraints() {
        return constraints;
    }

    public List<Path> getArtifacts() {
        return artifacts;
    }

    public TaskBudget getBudget() {
        return budget;
    }

    public TaskMode getMode() {
        return mode;
    }

    public Map<String, String> getStrategyMetadata() {
        return strategyMetadata;
    }

    public boolean requestsF0Runtime() {
        String substrate = strategyMetadata.getOrDefault("substrate", "");
        if ("f0".equalsIgnoreCase(substrate)) {
            return true;
        }
        String loweredGoal = goal.toLowerCase();
        return loweredGoal.contains("f0")
            || loweredGoal.contains("substrate")
            || loweredGoal.contains("bare-metal");
    }

    public boolean requestsLocalModelRuntime() {
        String substrate = strategyMetadata.getOrDefault("substrate", "");
        if ("local-model".equalsIgnoreCase(substrate) || "model".equalsIgnoreCase(substrate)) {
            return true;
        }
        return strategyMetadata.containsKey("model.id")
            || strategyMetadata.containsKey("model.root")
            || strategyMetadata.containsKey("model.runner");
    }

    public String getRequestedModelId() {
        return strategyMetadata.get("model.id");
    }

    public String getRequestedModelRoot() {
        return strategyMetadata.get("model.root");
    }

    public String getRequestedModelRunner() {
        return strategyMetadata.get("model.runner");
    }

    public String getRequestedModelBackend() {
        return strategyMetadata.get("model.backend");
    }

    public int getRequestedModelMaxTokens() {
        String value = strategyMetadata.get("model.maxTokens");
        if (value == null || value.isBlank()) {
            return 128;
        }
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return 128;
        }
    }

    public static Builder builder(String goal) {
        return new Builder(goal);
    }

    /**
     * Builder for task envelopes.
     */
    public static final class Builder {
        private UUID id;
        private final String goal;
        private final List<String> constraints = new ArrayList<>();
        private final List<Path> artifacts = new ArrayList<>();
        private TaskBudget budget;
        private TaskMode mode;
        private final Map<String, String> strategyMetadata = new LinkedHashMap<>();

        private Builder(String goal) {
            this.goal = Objects.requireNonNull(goal, "goal");
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder addConstraint(String constraint) {
            if (constraint != null && !constraint.isBlank()) {
                constraints.add(constraint);
            }
            return this;
        }

        public Builder addArtifact(Path artifact) {
            if (artifact != null) {
                artifacts.add(artifact.normalize());
            }
            return this;
        }

        public Builder budget(TaskBudget budget) {
            this.budget = budget;
            return this;
        }

        public Builder mode(TaskMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder strategy(String key, String value) {
            if (key != null && value != null) {
                strategyMetadata.put(key, value);
            }
            return this;
        }

        public TaskEnvelope build() {
            return new TaskEnvelope(this);
        }
    }
}
