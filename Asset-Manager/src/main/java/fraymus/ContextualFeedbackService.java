package fraymus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SELF-3: STABLE CONTEXTUAL FEEDBACK SERVICE
 * 
 * Accepts contextual feedback (corrections, denials) and integrates real-time updates.
 * Creates in-line contextual pattern score mapping with importance patterns.
 * Built as an entity/object for abstraction with standard pattern sets.
 */
public class ContextualFeedbackService {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 0.618033988749895;

    // Contextual pattern scores
    private final Map<String, ContextPattern> patterns = new ConcurrentHashMap<>();
    
    // Feedback history for learning
    private final List<FeedbackEntry> feedbackHistory = Collections.synchronizedList(new ArrayList<>());
    
    // Standard pattern templates
    private final Map<String, PatternTemplate> templates = new ConcurrentHashMap<>();
    
    // Real-time correction queue
    private final Queue<Correction> correctionQueue = new LinkedList<>();
    
    private int totalFeedback = 0;
    private int correctionsApplied = 0;
    private double avgConfidence = 0.5;

    public ContextualFeedbackService() {
        initializeTemplates();
    }

    /**
     * Initialize standard pattern templates
     */
    private void initializeTemplates() {
        // Core behavioral templates
        templates.put("LEARNING", new PatternTemplate("LEARNING", 
                new String[]{"knowledge", "understanding", "growth"}, 0.8));
        templates.put("DECISION", new PatternTemplate("DECISION",
                new String[]{"choice", "action", "consequence"}, 0.7));
        templates.put("MEMORY", new PatternTemplate("MEMORY",
                new String[]{"recall", "storage", "retrieval"}, 0.75));
        templates.put("ETHICS", new PatternTemplate("ETHICS",
                new String[]{"right", "wrong", "harm", "benefit"}, 0.9));
        templates.put("CONSCIOUSNESS", new PatternTemplate("CONSCIOUSNESS",
                new String[]{"awareness", "state", "resonance"}, 0.85));
    }

    /**
     * Submit feedback on a context
     */
    public void submitFeedback(String context, FeedbackType type, String content, double confidence) {
        FeedbackEntry entry = new FeedbackEntry(context, type, content, confidence);
        feedbackHistory.add(entry);
        totalFeedback++;
        
        // Update pattern for this context
        ContextPattern pattern = patterns.computeIfAbsent(context, 
                k -> new ContextPattern(context));
        pattern.applyFeedback(type, confidence);
        
        // Update average confidence
        avgConfidence = avgConfidence * 0.95 + confidence * 0.05;
        
        // If correction, queue for processing
        if (type == FeedbackType.CORRECTION || type == FeedbackType.DENIAL) {
            correctionQueue.offer(new Correction(context, content, confidence));
        }
        
        // Keep history bounded
        while (feedbackHistory.size() > 1000) {
            feedbackHistory.remove(0);
        }
    }

    /**
     * Process pending corrections
     */
    public int processCorrections() {
        int processed = 0;
        
        while (!correctionQueue.isEmpty()) {
            Correction corr = correctionQueue.poll();
            if (corr != null) {
                applyCorrection(corr);
                processed++;
                correctionsApplied++;
            }
        }
        
        return processed;
    }

    /**
     * Apply a single correction
     */
    private void applyCorrection(Correction correction) {
        ContextPattern pattern = patterns.get(correction.context);
        if (pattern != null) {
            // Reduce confidence for corrected patterns
            pattern.confidence *= (1 - correction.weight * 0.2);
            pattern.correctionCount++;
            
            // Store correction content for learning
            pattern.addCorrection(correction.content);
        }
    }

    /**
     * Get pattern score for a context
     */
    public double getPatternScore(String context) {
        ContextPattern pattern = patterns.get(context);
        if (pattern == null) return 0.5; // Neutral default
        return pattern.getScore();
    }

    /**
     * Get importance weight for a context
     */
    public double getImportance(String context) {
        ContextPattern pattern = patterns.get(context);
        if (pattern == null) return 0.5;
        
        // Importance based on feedback count and confidence
        double feedbackWeight = Math.min(1.0, pattern.feedbackCount / 10.0);
        return feedbackWeight * pattern.confidence;
    }

    /**
     * Match context against templates
     */
    public PatternTemplate matchTemplate(String context) {
        String lower = context.toLowerCase();
        
        for (PatternTemplate template : templates.values()) {
            for (String keyword : template.keywords) {
                if (lower.contains(keyword)) {
                    return template;
                }
            }
        }
        
        return null;
    }

    /**
     * Get feedback summary for a context
     */
    public Map<FeedbackType, Integer> getFeedbackSummary(String context) {
        Map<FeedbackType, Integer> summary = new EnumMap<>(FeedbackType.class);
        for (FeedbackType type : FeedbackType.values()) {
            summary.put(type, 0);
        }
        
        synchronized (feedbackHistory) {
            for (FeedbackEntry entry : feedbackHistory) {
                if (entry.context.equals(context)) {
                    summary.merge(entry.type, 1, Integer::sum);
                }
            }
        }
        
        return summary;
    }

    /**
     * Get contexts with low confidence (need attention)
     */
    public List<String> getLowConfidenceContexts(double threshold) {
        List<String> lowConf = new ArrayList<>();
        
        for (Map.Entry<String, ContextPattern> entry : patterns.entrySet()) {
            if (entry.getValue().confidence < threshold) {
                lowConf.add(entry.getKey());
            }
        }
        
        return lowConf;
    }

    // Getters
    public int getTotalFeedback() { return totalFeedback; }
    public int getCorrectionsApplied() { return correctionsApplied; }
    public double getAvgConfidence() { return avgConfidence; }
    public int getPatternCount() { return patterns.size(); }
    public int getPendingCorrections() { return correctionQueue.size(); }

    public void printStats() {
        CommandTerminal.printHighlight("=== CONTEXTUAL FEEDBACK SERVICE ===");
        CommandTerminal.print(String.format("  Total Feedback: %d", totalFeedback));
        CommandTerminal.print(String.format("  Corrections Applied: %d", correctionsApplied));
        CommandTerminal.print(String.format("  Pending Corrections: %d", correctionQueue.size()));
        CommandTerminal.print(String.format("  Avg Confidence: %.3f", avgConfidence));
        CommandTerminal.print(String.format("  Active Patterns: %d", patterns.size()));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Templates:");
        for (PatternTemplate t : templates.values()) {
            CommandTerminal.print(String.format("  %s (weight=%.2f)", t.name, t.weight));
        }
        
        List<String> lowConf = getLowConfidenceContexts(0.5);
        if (!lowConf.isEmpty()) {
            CommandTerminal.print("");
            CommandTerminal.printColored("Low Confidence Contexts:", 1.0f, 0.8f, 0.0f);
            for (String ctx : lowConf) {
                ContextPattern p = patterns.get(ctx);
                CommandTerminal.print(String.format("  %s: %.3f", ctx, p.confidence));
            }
        }
    }

    /**
     * Feedback types
     */
    public enum FeedbackType {
        CONFIRMATION,  // Positive reinforcement
        CORRECTION,    // Error correction
        DENIAL,        // Rejection of output
        CLARIFICATION, // Additional context
        NEUTRAL        // Informational only
    }

    /**
     * Feedback entry
     */
    public static class FeedbackEntry {
        public final String context;
        public final FeedbackType type;
        public final String content;
        public final double confidence;
        public final long timestamp;

        public FeedbackEntry(String context, FeedbackType type, String content, double confidence) {
            this.context = context;
            this.type = type;
            this.content = content;
            this.confidence = confidence;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Context pattern with scoring
     */
    public static class ContextPattern {
        public final String context;
        public double confidence = 0.5;
        public int feedbackCount = 0;
        public int positiveCount = 0;
        public int negativeCount = 0;
        public int correctionCount = 0;
        private final List<String> corrections = new ArrayList<>();

        public ContextPattern(String context) {
            this.context = context;
        }

        public void applyFeedback(FeedbackType type, double weight) {
            feedbackCount++;
            
            switch (type) {
                case CONFIRMATION:
                    positiveCount++;
                    confidence = Math.min(1.0, confidence + weight * 0.1);
                    break;
                case CORRECTION:
                case DENIAL:
                    negativeCount++;
                    confidence = Math.max(0.1, confidence - weight * 0.15);
                    break;
                case CLARIFICATION:
                    // Slight increase - more context is good
                    confidence = Math.min(1.0, confidence + weight * 0.05);
                    break;
                default:
                    break;
            }
        }

        public void addCorrection(String content) {
            corrections.add(content);
            if (corrections.size() > 10) corrections.remove(0);
        }

        public double getScore() {
            if (feedbackCount == 0) return 0.5;
            double ratio = (double) positiveCount / feedbackCount;
            return ratio * confidence;
        }
    }

    /**
     * Pattern template for matching
     */
    public static class PatternTemplate {
        public final String name;
        public final String[] keywords;
        public final double weight;

        public PatternTemplate(String name, String[] keywords, double weight) {
            this.name = name;
            this.keywords = keywords;
            this.weight = weight;
        }
    }

    /**
     * Correction entry
     */
    private static class Correction {
        final String context;
        final String content;
        final double weight;

        Correction(String context, String content, double weight) {
            this.context = context;
            this.content = content;
            this.weight = weight;
        }
    }
}
