package fraynix.core;

import java.util.List;
import java.util.Map;

/**
 * POLICY: Decision-making contract.
 * 
 * Policies turn state + intent into ranked decisions.
 * Every brain decision, scheduler choice, and repair plan uses this.
 */
public interface Policy<S, A> {

    /**
     * Decide what action(s) to take given current state.
     */
    Decision<A> decide(S state, Intent intent);

    /**
     * Get policy name for logging/auditing.
     */
    String getPolicyName();

    /**
     * Decision result with explainability.
     */
    record Decision<A>(
        List<RankedChoice<A>> rankedChoices,
        double confidence,
        List<String> featuresUsed,
        String reason,
        long decisionTimeMs
    ) {
        public A getBestChoice() {
            return rankedChoices.isEmpty() ? null : rankedChoices.get(0).action();
        }

        public boolean hasChoice() {
            return !rankedChoices.isEmpty();
        }

        public static <A> Decision<A> single(A action, double confidence, String reason) {
            return new Decision<>(
                List.of(new RankedChoice<>(action, 1.0, reason)),
                confidence,
                List.of(),
                reason,
                0
            );
        }

        public static <A> Decision<A> none(String reason) {
            return new Decision<>(List.of(), 0.0, List.of(), reason, 0);
        }
    }

    /**
     * A ranked action choice with score and explanation.
     */
    record RankedChoice<A>(
        A action,
        double score,
        String explanation
    ) implements Comparable<RankedChoice<A>> {
        @Override
        public int compareTo(RankedChoice<A> other) {
            return Double.compare(other.score, this.score); // Descending
        }
    }

    /**
     * Feature vector for decision-making.
     */
    record Features(
        double cpuUsage,
        double memoryUsage,
        double ioWait,
        long queueDepth,
        long recentErrors,
        Map<String, Double> custom
    ) {
        public static Features current() {
            Runtime rt = Runtime.getRuntime();
            double memUsage = 1.0 - ((double) rt.freeMemory() / rt.maxMemory());
            return new Features(
                0.0, // CPU requires OS-level access
                memUsage,
                0.0,
                0,
                0,
                Map.of()
            );
        }
    }
}
