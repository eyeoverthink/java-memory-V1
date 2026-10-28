package brain_architecture.integration.unified;

import brain_architecture.kernel.TaskMode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Phase A unified task contract for forwarding all entrypoints into one runtime path.
 */
public final class UnifiedTask {
    private final String goal;
    private final List<String> constraints;
    private final List<Path> artifacts;
    private final TaskMode mode;
    private final boolean includeProposals;
    private final int proposalLimit;
    private final boolean includeEvolutionEvaluations;
    private final int evolutionIterationLimit;
    private final Map<String, String> strategyMetadata;

    private UnifiedTask(Builder builder) {
        this.goal = Objects.requireNonNull(builder.goal, "goal");
        this.constraints = Collections.unmodifiableList(new ArrayList<>(builder.constraints));
        this.artifacts = Collections.unmodifiableList(new ArrayList<>(builder.artifacts));
        this.mode = builder.mode == null ? TaskMode.SUPERVISED : builder.mode;
        this.includeProposals = builder.includeProposals;
        this.proposalLimit = builder.proposalLimit <= 0 ? 5 : builder.proposalLimit;
        this.includeEvolutionEvaluations = builder.includeEvolutionEvaluations;
        this.evolutionIterationLimit = builder.evolutionIterationLimit <= 0 ? 1 : builder.evolutionIterationLimit;
        this.strategyMetadata = Collections.unmodifiableMap(new LinkedHashMap<>(builder.strategyMetadata));
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

    public TaskMode getMode() {
        return mode;
    }

    public boolean isIncludeProposals() {
        return includeProposals;
    }

    public int getProposalLimit() {
        return proposalLimit;
    }

    public boolean isIncludeEvolutionEvaluations() {
        return includeEvolutionEvaluations;
    }

    public int getEvolutionIterationLimit() {
        return evolutionIterationLimit;
    }

    public Map<String, String> getStrategyMetadata() {
        return strategyMetadata;
    }

    public static Builder builder(String goal) {
        return new Builder(goal);
    }

    public static final class Builder {
        private final String goal;
        private final List<String> constraints = new ArrayList<>();
        private final List<Path> artifacts = new ArrayList<>();
        private TaskMode mode;
        private boolean includeProposals;
        private int proposalLimit = 5;
        private boolean includeEvolutionEvaluations;
        private int evolutionIterationLimit = 1;
        private final Map<String, String> strategyMetadata = new LinkedHashMap<>();

        private Builder(String goal) {
            this.goal = Objects.requireNonNull(goal, "goal");
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

        public Builder mode(TaskMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder includeProposals(boolean includeProposals) {
            this.includeProposals = includeProposals;
            return this;
        }

        public Builder proposalLimit(int proposalLimit) {
            this.proposalLimit = proposalLimit <= 0 ? 5 : proposalLimit;
            return this;
        }

        public Builder includeEvolutionEvaluations(boolean includeEvolutionEvaluations) {
            this.includeEvolutionEvaluations = includeEvolutionEvaluations;
            return this;
        }

        public Builder evolutionIterationLimit(int evolutionIterationLimit) {
            this.evolutionIterationLimit = evolutionIterationLimit <= 0 ? 1 : evolutionIterationLimit;
            return this;
        }

        public Builder strategy(String key, String value) {
            if (key != null && value != null) {
                strategyMetadata.put(key, value);
            }
            return this;
        }

        public UnifiedTask build() {
            return new UnifiedTask(this);
        }
    }
}
