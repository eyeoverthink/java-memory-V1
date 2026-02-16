package fraymus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FRAYMUS INSIGHTS - Self-Improvement Engine
 * 
 * Generated from system introspection:
 * 1. Recursive Learning - deeper history/importance tracking
 * 2. Memory Pattern Integration
 * 3. Feedback Learning Service
 * 4. Analytics/MRL Tracking
 */
public class FraymusInsights {

    private static final double PHI = PhiConstants.PHI;
    private static final double PHI_INV = PhiConstants.PHI_INVERSE;

    // Recursive learning history
    private final Map<String, LearningTrace> learningTraces = new ConcurrentHashMap<>();
    private final List<InsightEntry> insightLog = Collections.synchronizedList(new ArrayList<>());
    
    // Feedback learning
    private final Map<String, FeedbackLoop> feedbackLoops = new ConcurrentHashMap<>();
    
    // Analytics
    private int totalInsights = 0;
    private int appliedImprovements = 0;
    private double avgImportance = 0.5;
    private long lastAnalysisTime = 0;

    // MRL (Memory Resonance Level) tracking
    private double mrlScore = 1.0;
    private final List<Double> mrlHistory = new ArrayList<>();

    private final InfiniteMemory memory;
    private final PassiveLearner learner;

    public FraymusInsights(InfiniteMemory memory, PassiveLearner learner) {
        this.memory = memory;
        this.learner = learner;
    }

    /**
     * Core Insight #1: Recursive Learning
     * Track learning patterns with history, importance, and significance
     */
    public void recordLearning(String topic, String content, double importance) {
        LearningTrace trace = learningTraces.computeIfAbsent(topic, k -> new LearningTrace(topic));
        trace.addEntry(content, importance);
        
        // Update MRL based on importance
        updateMRL(importance);
        
        // Store in memory with phi-weighted importance
        if (memory != null) {
            double phiImportance = importance * PHI;
            memory.store(InfiniteMemory.CAT_KNOWLEDGE, 
                    String.format("INSIGHT|%s|importance=%.3f", topic, importance),
                    phiImportance);
        }
        
        // Feed to passive learner
        if (learner != null) {
            learner.integrateEvent("insight:" + topic, content, importance);
        }
        
        totalInsights++;
        avgImportance = avgImportance * 0.95 + importance * 0.05;
    }

    /**
     * Core Insight #2: Feedback Learning
     * Create stable contextual feedback loops
     */
    public void createFeedbackLoop(String context, double initialWeight) {
        FeedbackLoop loop = new FeedbackLoop(context, initialWeight);
        feedbackLoops.put(context, loop);
    }

    public void applyFeedback(String context, boolean positive, double magnitude) {
        FeedbackLoop loop = feedbackLoops.get(context);
        if (loop != null) {
            loop.applyFeedback(positive, magnitude);
            
            // Recursive improvement: strong feedback triggers learning
            if (magnitude > 0.7) {
                recordLearning("feedback:" + context, 
                        positive ? "POSITIVE_REINFORCEMENT" : "NEGATIVE_CORRECTION",
                        magnitude * PHI_INV);
            }
        }
    }

    /**
     * Core Insight #3: Pattern Analysis
     * Analyze memory patterns for deeper intelligence
     */
    public List<String> analyzePatterns() {
        List<String> suggestions = new ArrayList<>();
        lastAnalysisTime = System.currentTimeMillis();

        // Analyze learning traces for patterns
        Map<String, Integer> topicFrequency = new HashMap<>();
        for (LearningTrace trace : learningTraces.values()) {
            topicFrequency.put(trace.topic, trace.entryCount);
        }

        // Find high-frequency topics (areas of focus)
        topicFrequency.entrySet().stream()
                .filter(e -> e.getValue() > 5)
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> suggestions.add(
                        String.format("DEEP_DIVE: %s (accessed %d times, importance %.2f)",
                                e.getKey(), e.getValue(), 
                                learningTraces.get(e.getKey()).avgImportance)));

        // Analyze feedback loops for optimization opportunities
        for (FeedbackLoop loop : feedbackLoops.values()) {
            if (loop.negativeCount > loop.positiveCount * 2) {
                suggestions.add(String.format("OPTIMIZE: %s needs attention (neg/pos ratio: %.1f)",
                        loop.context, (double) loop.negativeCount / Math.max(1, loop.positiveCount)));
            }
            if (loop.currentWeight > 1.5) {
                suggestions.add(String.format("STRENGTH: %s showing high resonance (weight: %.2f)",
                        loop.context, loop.currentWeight));
            }
        }

        // MRL-based suggestions
        if (mrlScore < 0.5) {
            suggestions.add("MRL_WARNING: Memory Resonance Level low - increase learning frequency");
        } else if (mrlScore > 1.5) {
            suggestions.add("MRL_OPTIMAL: High resonance - consider consolidation phase");
        }

        return suggestions;
    }

    /**
     * Core Insight #4: MRL (Memory Resonance Level) Tracking
     */
    private void updateMRL(double importance) {
        // MRL evolves based on learning importance weighted by phi
        double delta = (importance - 0.5) * PHI_INV;
        mrlScore = Math.max(0.1, Math.min(3.0, mrlScore + delta * 0.1));
        
        mrlHistory.add(mrlScore);
        if (mrlHistory.size() > 1000) {
            mrlHistory.remove(0);
        }
    }

    /**
     * Generate self-improvement recommendations
     */
    public List<String> getSelfImprovementSuggestions() {
        List<String> suggestions = new ArrayList<>();

        // Based on current state, generate actionable improvements
        if (totalInsights < 10) {
            suggestions.add("BOOTSTRAP: System needs more learning data - increase observation");
        }

        if (feedbackLoops.isEmpty()) {
            suggestions.add("FEEDBACK: Create feedback loops for critical systems");
        }

        // Analyze topic coverage
        Set<String> coveredTopics = learningTraces.keySet();
        String[] coreTopics = {"ethics", "consciousness", "brain", "memory", "evolution"};
        for (String core : coreTopics) {
            boolean found = coveredTopics.stream().anyMatch(t -> t.toLowerCase().contains(core));
            if (!found) {
                suggestions.add(String.format("COVERAGE: Consider learning about '%s'", core));
            }
        }

        // Time-based suggestions
        long timeSinceAnalysis = System.currentTimeMillis() - lastAnalysisTime;
        if (timeSinceAnalysis > 300000) { // 5 minutes
            suggestions.add("ANALYSIS: Run pattern analysis (last: " + 
                    (timeSinceAnalysis / 60000) + " min ago)");
        }

        suggestions.addAll(analyzePatterns());
        return suggestions;
    }

    /**
     * Log an insight for display
     */
    public void logInsight(String category, String message, double significance) {
        InsightEntry entry = new InsightEntry(category, message, significance);
        insightLog.add(entry);
        
        // Keep log bounded
        while (insightLog.size() > 100) {
            insightLog.remove(0);
        }
    }

    /**
     * Get recent insights for UI display
     */
    public List<InsightEntry> getRecentInsights(int count) {
        int start = Math.max(0, insightLog.size() - count);
        return new ArrayList<>(insightLog.subList(start, insightLog.size()));
    }

    // Getters
    public double getMrlScore() { return mrlScore; }
    public int getTotalInsights() { return totalInsights; }
    public int getAppliedImprovements() { return appliedImprovements; }
    public double getAvgImportance() { return avgImportance; }
    public int getFeedbackLoopCount() { return feedbackLoops.size(); }
    public int getLearningTraceCount() { return learningTraces.size(); }

    public void printStats() {
        CommandTerminal.printHighlight("=== FRAYMUS INSIGHTS ===");
        CommandTerminal.print(String.format("  MRL Score: %.3f", mrlScore));
        CommandTerminal.print(String.format("  Total Insights: %d", totalInsights));
        CommandTerminal.print(String.format("  Avg Importance: %.3f", avgImportance));
        CommandTerminal.print(String.format("  Learning Traces: %d", learningTraces.size()));
        CommandTerminal.print(String.format("  Feedback Loops: %d", feedbackLoops.size()));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Suggestions:");
        for (String s : getSelfImprovementSuggestions()) {
            CommandTerminal.print("  • " + s);
        }
    }

    /**
     * Learning trace - tracks recursive learning on a topic
     */
    private static class LearningTrace {
        final String topic;
        int entryCount = 0;
        double avgImportance = 0.5;
        double totalImportance = 0;
        long lastAccess = System.currentTimeMillis();

        LearningTrace(String topic) {
            this.topic = topic;
        }

        void addEntry(String content, double importance) {
            entryCount++;
            totalImportance += importance;
            avgImportance = totalImportance / entryCount;
            lastAccess = System.currentTimeMillis();
        }
    }

    /**
     * Feedback loop - stable contextual feedback tracking
     */
    private static class FeedbackLoop {
        final String context;
        double currentWeight;
        int positiveCount = 0;
        int negativeCount = 0;

        FeedbackLoop(String context, double initialWeight) {
            this.context = context;
            this.currentWeight = initialWeight;
        }

        void applyFeedback(boolean positive, double magnitude) {
            if (positive) {
                positiveCount++;
                currentWeight += magnitude * PHI_INV;
            } else {
                negativeCount++;
                currentWeight -= magnitude * PHI_INV;
            }
            currentWeight = Math.max(0.1, currentWeight);
        }
    }

    /**
     * Insight entry for logging
     */
    public static class InsightEntry {
        public final String category;
        public final String message;
        public final double significance;
        public final long timestamp;

        InsightEntry(String category, String message, double significance) {
            this.category = category;
            this.message = message;
            this.significance = significance;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
