package brain_architecture.kernel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Thompson-style bandit for local strategy selection.
 */
public final class StrategyBanditRouter {
    private final long baseSeed;
    private final Map<String, ArmState> arms;

    public StrategyBanditRouter(long baseSeed) {
        this.baseSeed = baseSeed;
        this.arms = new LinkedHashMap<>();
        arms.put("logical", new ArmState());
        arms.put("memory", new ArmState());
        arms.put("vision", new ArmState());
        arms.put("creative", new ArmState());
    }

    public synchronized String select(TaskEnvelope envelope, List<MemoryHit> hits) {
        String bestArm = "logical";
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Map.Entry<String, ArmState> entry : arms.entrySet()) {
            String arm = entry.getKey();
            ArmState state = entry.getValue();
            double contextBonus = contextBonus(arm, envelope, hits);
            Random random = new Random(baseSeed ^ envelope.getId().getMostSignificantBits() ^ arm.hashCode());
            double sampledScore = sampleBeta(state.alpha + contextBonus, state.beta + Math.max(0.0, 1.0 - contextBonus), random);
            if (sampledScore > bestScore) {
                bestScore = sampledScore;
                bestArm = arm;
            }
        }
        return bestArm;
    }

    public synchronized void recordOutcome(String arm, boolean success) {
        ArmState state = arms.get(arm);
        if (state == null) {
            return;
        }
        if (success) {
            state.alpha += 1.0;
        } else {
            state.beta += 1.0;
        }
    }

    public synchronized Map<String, Map<String, Double>> getStatistics() {
        Map<String, Map<String, Double>> stats = new LinkedHashMap<>();
        for (Map.Entry<String, ArmState> entry : arms.entrySet()) {
            ArmState state = entry.getValue();
            stats.put(entry.getKey(), Map.of(
                "alpha", state.alpha,
                "beta", state.beta,
                "mean", state.alpha / (state.alpha + state.beta)
            ));
        }
        return stats;
    }

    public synchronized Map<String, ArmSnapshot> snapshotState() {
        Map<String, ArmSnapshot> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, ArmState> entry : arms.entrySet()) {
            snapshot.put(entry.getKey(), new ArmSnapshot(entry.getValue().alpha, entry.getValue().beta));
        }
        return snapshot;
    }

    public synchronized void restoreState(Map<String, ArmSnapshot> snapshot) {
        if (snapshot == null) {
            return;
        }
        for (Map.Entry<String, ArmSnapshot> entry : snapshot.entrySet()) {
            ArmState state = arms.computeIfAbsent(entry.getKey(), ignored -> new ArmState());
            state.alpha = Math.max(1.0, entry.getValue().alpha());
            state.beta = Math.max(1.0, entry.getValue().beta());
        }
    }

    private double contextBonus(String arm, TaskEnvelope envelope, List<MemoryHit> hits) {
        String goal = envelope.getGoal().toLowerCase();
        double bonus = 0.25;
        if ("logical".equals(arm) && (goal.contains("build") || goal.contains("fix") || goal.contains("code"))) {
            bonus += 0.60;
        }
        if ("memory".equals(arm) && (!hits.isEmpty() || goal.contains("remember") || goal.contains("analyze"))) {
            bonus += 0.50;
        }
        if ("vision".equals(arm) && envelope.getArtifacts().stream().anyMatch(path -> path.toString().matches("(?i).+\\.(png|jpg|jpeg|gif|bmp)$"))) {
            bonus += 0.65;
        }
        if ("creative".equals(arm) && (goal.contains("design") || goal.contains("brainstorm") || goal.contains("draft"))) {
            bonus += 0.45;
        }
        if (envelope.getMode() == TaskMode.MUTATION && "logical".equals(arm)) {
            bonus += 0.25;
        }
        return bonus;
    }

    private double sampleBeta(double alpha, double beta, Random random) {
        double x = sampleGamma(alpha, random);
        double y = sampleGamma(beta, random);
        return x / (x + y);
    }

    private double sampleGamma(double shape, Random random) {
        if (shape <= 0.0) {
            return 1e-9;
        }
        if (shape < 1.0) {
            return sampleGamma(shape + 1.0, random) * Math.pow(random.nextDouble(), 1.0 / shape);
        }
        double d = shape - (1.0 / 3.0);
        double c = 1.0 / Math.sqrt(9.0 * d);
        while (true) {
            double x = random.nextGaussian();
            double v = 1.0 + (c * x);
            if (v <= 0.0) {
                continue;
            }
            v = v * v * v;
            double u = random.nextDouble();
            if (u < 1.0 - (0.0331 * x * x * x * x)) {
                return d * v;
            }
            if (Math.log(u) < (0.5 * x * x) + d * (1 - v + Math.log(v))) {
                return d * v;
            }
        }
    }

    public record ArmSnapshot(double alpha, double beta) {
    }

    private static final class ArmState {
        private double alpha = 1.0;
        private double beta = 1.0;
    }
}
