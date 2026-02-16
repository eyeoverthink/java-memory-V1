package fraynix.brain;

import fraynix.core.Intent;
import fraynix.core.Policy;

import java.util.*;

/**
 * BRAIN POLICY: Turns brain signals into ranked decisions.
 * 
 * Every decision returns:
 *   - ranked_choices[]
 *   - confidence
 *   - features_used (cpu, mem, io, recent history)
 *   - reason string (for console + logs)
 */
public class BrainPolicy implements Policy<BrainState, String> {

    private final String name;
    private final List<Rule> rules = new ArrayList<>();

    public BrainPolicy(String name) {
        this.name = name;
        initDefaultRules();
    }

    @Override
    public String getPolicyName() {
        return name;
    }

    @Override
    public Decision<String> decide(BrainState state, Intent intent) {
        long startTime = System.currentTimeMillis();
        
        List<RankedChoice<String>> choices = new ArrayList<>();
        List<String> featuresUsed = new ArrayList<>();
        StringBuilder reasonBuilder = new StringBuilder();
        
        // Gather features
        Features features = gatherFeatures(state);
        featuresUsed.add("cpu:" + String.format("%.2f", features.cpuUsage()));
        featuresUsed.add("mem:" + String.format("%.2f", features.memoryUsage()));
        featuresUsed.add("queue:" + features.queueDepth());
        
        // Evaluate each rule
        for (Rule rule : rules) {
            if (rule.applies(intent, features, state)) {
                double score = rule.score(intent, features, state);
                choices.add(new RankedChoice<>(rule.action, score, rule.reason));
                featuresUsed.add("rule:" + rule.name);
            }
        }
        
        // Sort by score
        Collections.sort(choices);
        
        // Calculate confidence
        double confidence = 0.0;
        if (!choices.isEmpty()) {
            confidence = choices.get(0).score();
            if (choices.size() > 1) {
                // Confidence is gap between top two choices
                double gap = choices.get(0).score() - choices.get(1).score();
                confidence = Math.min(1.0, confidence * (1 + gap));
            }
        }
        
        // Build reason
        if (choices.isEmpty()) {
            reasonBuilder.append("No applicable rules for intent: ").append(intent.getType());
        } else {
            reasonBuilder.append("Selected '").append(choices.get(0).action())
                .append("' (score=").append(String.format("%.3f", choices.get(0).score()))
                .append(") based on ").append(choices.get(0).explanation());
        }
        
        long decisionTime = System.currentTimeMillis() - startTime;
        
        return new Decision<>(
            choices,
            confidence,
            featuresUsed,
            reasonBuilder.toString(),
            decisionTime
        );
    }

    private Features gatherFeatures(BrainState state) {
        Runtime rt = Runtime.getRuntime();
        double memUsage = 1.0 - ((double) rt.freeMemory() / rt.maxMemory());
        
        return new Features(
            state != null ? state.getCpuUsage() : 0.0,
            memUsage,
            0.0, // IO wait requires OS access
            state != null ? state.getQueueDepth() : 0,
            state != null ? state.getRecentErrors() : 0,
            Map.of(
                "activeNodes", state != null ? (double) state.getActiveNodeCount() : 0.0,
                "avgActivation", state != null ? state.getAverageActivation() : 0.0
            )
        );
    }

    private void initDefaultRules() {
        // High memory → trigger GC
        addRule(new Rule("gc_trigger", "GC_HINT",
            (intent, features, state) -> features.memoryUsage() > 0.8,
            (intent, features, state) -> features.memoryUsage(),
            "high memory usage"
        ));
        
        // High CPU → throttle
        addRule(new Rule("cpu_throttle", "THROTTLE",
            (intent, features, state) -> features.cpuUsage() > 0.9,
            (intent, features, state) -> features.cpuUsage(),
            "high CPU usage"
        ));
        
        // Long queue → prioritize
        addRule(new Rule("queue_prioritize", "PRIORITIZE_QUEUE",
            (intent, features, state) -> features.queueDepth() > 50,
            (intent, features, state) -> Math.min(1.0, features.queueDepth() / 100.0),
            "queue backlog"
        ));
        
        // Recent errors → investigate
        addRule(new Rule("error_investigate", "INVESTIGATE_ERRORS",
            (intent, features, state) -> features.recentErrors() > 5,
            (intent, features, state) -> Math.min(1.0, features.recentErrors() / 10.0),
            "recent error spike"
        ));
        
        // Low activity → prefetch
        addRule(new Rule("idle_prefetch", "PREFETCH",
            (intent, features, state) -> features.cpuUsage() < 0.2 && features.memoryUsage() < 0.5,
            (intent, features, state) -> 0.3,
            "system idle - good time to prefetch"
        ));
        
        // Normal operation
        addRule(new Rule("normal", "CONTINUE",
            (intent, features, state) -> true, // Always applies
            (intent, features, state) -> 0.1, // Low priority fallback
            "normal operation"
        ));
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        // Keep sorted by default priority
        rules.sort((a, b) -> Double.compare(b.defaultPriority, a.defaultPriority));
    }

    public static class Rule {
        final String name;
        final String action;
        final Condition condition;
        final Scorer scorer;
        final String reason;
        final double defaultPriority;

        public Rule(String name, String action, Condition condition, Scorer scorer, String reason) {
            this(name, action, condition, scorer, reason, 0.5);
        }

        public Rule(String name, String action, Condition condition, Scorer scorer, String reason, double defaultPriority) {
            this.name = name;
            this.action = action;
            this.condition = condition;
            this.scorer = scorer;
            this.reason = reason;
            this.defaultPriority = defaultPriority;
        }

        boolean applies(Intent intent, Features features, BrainState state) {
            return condition.test(intent, features, state);
        }

        double score(Intent intent, Features features, BrainState state) {
            return scorer.score(intent, features, state);
        }

        @FunctionalInterface
        interface Condition {
            boolean test(Intent intent, Features features, BrainState state);
        }

        @FunctionalInterface
        interface Scorer {
            double score(Intent intent, Features features, BrainState state);
        }
    }
}
