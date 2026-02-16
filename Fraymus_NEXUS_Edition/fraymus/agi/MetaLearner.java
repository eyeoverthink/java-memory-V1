package fraymus.agi;

import fraymus.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AGI-1: META-LEARNER - Learn How To Learn
 * 
 * Core AGI capability: The system learns to improve its own learning process
 * through recursive self-improvement at the learning algorithm level.
 * 
 * Key features:
 * - Learning rate adaptation based on success
 * - Strategy evaluation and selection
 * - Cross-domain pattern transfer
 * - Self-modification of learning parameters
 */
public class MetaLearner {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 0.618033988749895;

    // Meta-learning strategies
    public enum LearningStrategy {
        EXPLORATION,      // High variance, discover new patterns
        EXPLOITATION,     // Low variance, refine known patterns
        TRANSFER,         // Apply patterns across domains
        CONSOLIDATION,    // Strengthen existing knowledge
        SYNTHESIS         // Combine patterns creatively
    }

    // Current meta-state
    private LearningStrategy currentStrategy = LearningStrategy.EXPLORATION;
    private double learningRate = 0.1;
    private double explorationRate = 0.3;
    
    // Strategy performance tracking
    private final Map<LearningStrategy, StrategyStats> strategyStats = new ConcurrentHashMap<>();
    
    // Learning history for pattern analysis
    private final List<LearningEvent> learningHistory = Collections.synchronizedList(new ArrayList<>());
    
    // Cross-domain pattern library
    private final Map<String, DomainPattern> domainPatterns = new ConcurrentHashMap<>();
    
    // Self-modification parameters
    private double adaptationSpeed = 0.05;
    private int evaluationWindow = 50;
    private double successThreshold = 0.6;

    // Meta-metrics
    private int totalLearningEvents = 0;
    private double avgLearningSuccess = 0.5;
    private double metaLearningProgress = 0;
    private int strategyChanges = 0;

    public MetaLearner() {
        // Initialize strategy stats
        for (LearningStrategy s : LearningStrategy.values()) {
            strategyStats.put(s, new StrategyStats(s));
        }
    }

    /**
     * Core meta-learning: Evaluate and adapt learning approach
     */
    public void evaluateAndAdapt() {
        if (learningHistory.size() < evaluationWindow) return;

        // Analyze recent learning performance
        double recentSuccess = calculateRecentSuccess();
        
        // Update current strategy stats
        StrategyStats current = strategyStats.get(currentStrategy);
        current.recordOutcome(recentSuccess > successThreshold);

        // Decide if strategy change is needed
        if (shouldChangeStrategy(recentSuccess)) {
            LearningStrategy newStrategy = selectBestStrategy();
            if (newStrategy != currentStrategy) {
                currentStrategy = newStrategy;
                strategyChanges++;
                adaptLearningParameters();
            }
        }

        // Self-modify learning rate based on performance
        adaptLearningRate(recentSuccess);
        
        // Update meta-progress
        metaLearningProgress = metaLearningProgress * 0.99 + 
                (recentSuccess - avgLearningSuccess) * 0.01;
        avgLearningSuccess = avgLearningSuccess * 0.95 + recentSuccess * 0.05;
    }

    /**
     * Record a learning event for meta-analysis
     */
    public void recordLearning(String domain, String pattern, double success, double resonance) {
        LearningEvent event = new LearningEvent(domain, pattern, success, resonance, currentStrategy);
        learningHistory.add(event);
        totalLearningEvents++;

        // Update domain pattern
        DomainPattern dp = domainPatterns.computeIfAbsent(domain, k -> new DomainPattern(domain));
        dp.addPattern(pattern, success, resonance);

        // Keep history bounded
        while (learningHistory.size() > 1000) {
            learningHistory.remove(0);
        }

        // Trigger meta-evaluation periodically
        if (totalLearningEvents % 10 == 0) {
            evaluateAndAdapt();
        }
    }

    /**
     * Get optimized learning parameters for current context
     */
    public LearningParameters getOptimalParameters(String domain, String context) {
        LearningParameters params = new LearningParameters();
        params.learningRate = learningRate;
        params.explorationRate = explorationRate;
        params.strategy = currentStrategy;

        // Adjust based on domain history
        DomainPattern dp = domainPatterns.get(domain);
        if (dp != null) {
            // If domain has high success, reduce exploration
            if (dp.avgSuccess > 0.7) {
                params.explorationRate *= 0.5;
                params.learningRate *= 1.2;
            }
            // If domain is struggling, increase exploration
            else if (dp.avgSuccess < 0.4) {
                params.explorationRate *= 1.5;
                params.learningRate *= 0.8;
            }
        }

        // Check for cross-domain transfer opportunities
        String transferSource = findTransferSource(domain);
        if (transferSource != null) {
            params.transferSource = transferSource;
            params.useTransfer = true;
        }

        return params;
    }

    /**
     * Cross-domain pattern transfer
     * Find patterns from one domain applicable to another
     */
    public List<String> getTransferablePatterns(String targetDomain) {
        List<String> transferable = new ArrayList<>();
        DomainPattern target = domainPatterns.get(targetDomain);
        
        if (target == null) return transferable;

        // Find patterns from other domains with high success
        for (Map.Entry<String, DomainPattern> entry : domainPatterns.entrySet()) {
            if (entry.getKey().equals(targetDomain)) continue;
            
            DomainPattern source = entry.getValue();
            if (source.avgSuccess > 0.7) {
                // Get top patterns from successful domain
                for (String pattern : source.getTopPatterns(3)) {
                    // Check if pattern might apply to target
                    if (patternCouldTransfer(pattern, source.domain, targetDomain)) {
                        transferable.add(source.domain + ":" + pattern);
                    }
                }
            }
        }

        return transferable;
    }

    /**
     * Calculate recent learning success rate
     */
    private double calculateRecentSuccess() {
        int window = Math.min(evaluationWindow, learningHistory.size());
        if (window == 0) return 0.5;

        double sum = 0;
        synchronized (learningHistory) {
            int start = learningHistory.size() - window;
            for (int i = start; i < learningHistory.size(); i++) {
                sum += learningHistory.get(i).success;
            }
        }
        return sum / window;
    }

    /**
     * Determine if strategy change is warranted
     */
    private boolean shouldChangeStrategy(double recentSuccess) {
        StrategyStats current = strategyStats.get(currentStrategy);
        
        // If current strategy is performing well, stick with it
        if (recentSuccess > successThreshold && current.getSuccessRate() > 0.6) {
            return false;
        }
        
        // If we've tried this strategy enough and it's not working, change
        if (current.trials > 20 && current.getSuccessRate() < 0.4) {
            return true;
        }

        // Random exploration with phi-weighted probability
        return Math.random() < (1 - recentSuccess) * PHI_INV * 0.1;
    }

    /**
     * Select the best strategy based on historical performance
     */
    private LearningStrategy selectBestStrategy() {
        LearningStrategy best = currentStrategy;
        double bestScore = 0;

        for (LearningStrategy s : LearningStrategy.values()) {
            StrategyStats stats = strategyStats.get(s);
            
            // Score = success rate with exploration bonus for less-tried strategies
            double explorationBonus = 1.0 / Math.sqrt(stats.trials + 1);
            double score = stats.getSuccessRate() + explorationBonus * explorationRate;
            
            if (score > bestScore) {
                bestScore = score;
                best = s;
            }
        }

        return best;
    }

    /**
     * Adapt learning parameters based on current strategy
     */
    private void adaptLearningParameters() {
        switch (currentStrategy) {
            case EXPLORATION:
                explorationRate = Math.min(0.5, explorationRate * 1.2);
                learningRate = Math.max(0.05, learningRate * 0.9);
                break;
            case EXPLOITATION:
                explorationRate = Math.max(0.1, explorationRate * 0.8);
                learningRate = Math.min(0.3, learningRate * 1.1);
                break;
            case TRANSFER:
                explorationRate = 0.2;
                learningRate = 0.15;
                break;
            case CONSOLIDATION:
                explorationRate = 0.1;
                learningRate = 0.05;
                break;
            case SYNTHESIS:
                explorationRate = 0.3;
                learningRate = 0.2;
                break;
        }
    }

    /**
     * Adapt learning rate based on performance
     */
    private void adaptLearningRate(double recentSuccess) {
        if (recentSuccess > successThreshold) {
            // Success: slightly increase learning rate
            learningRate = Math.min(0.3, learningRate * (1 + adaptationSpeed));
        } else {
            // Struggling: decrease learning rate for more stability
            learningRate = Math.max(0.01, learningRate * (1 - adaptationSpeed));
        }
    }

    /**
     * Find a domain that could provide useful patterns for transfer
     */
    private String findTransferSource(String targetDomain) {
        DomainPattern target = domainPatterns.get(targetDomain);
        if (target != null && target.avgSuccess > 0.6) return null; // Target doing fine

        String bestSource = null;
        double bestScore = 0;

        for (Map.Entry<String, DomainPattern> entry : domainPatterns.entrySet()) {
            if (entry.getKey().equals(targetDomain)) continue;
            
            DomainPattern source = entry.getValue();
            if (source.avgSuccess > 0.6 && source.patternCount > 10) {
                double similarity = calculateDomainSimilarity(source.domain, targetDomain);
                double score = source.avgSuccess * similarity;
                if (score > bestScore) {
                    bestScore = score;
                    bestSource = source.domain;
                }
            }
        }

        return bestScore > 0.3 ? bestSource : null;
    }

    /**
     * Simple domain similarity based on name overlap
     */
    private double calculateDomainSimilarity(String domain1, String domain2) {
        Set<Character> chars1 = new HashSet<>();
        Set<Character> chars2 = new HashSet<>();
        for (char c : domain1.toLowerCase().toCharArray()) chars1.add(c);
        for (char c : domain2.toLowerCase().toCharArray()) chars2.add(c);
        
        Set<Character> intersection = new HashSet<>(chars1);
        intersection.retainAll(chars2);
        
        Set<Character> union = new HashSet<>(chars1);
        union.addAll(chars2);
        
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    /**
     * Check if a pattern might transfer between domains
     */
    private boolean patternCouldTransfer(String pattern, String source, String target) {
        // Abstract patterns (not domain-specific) are more likely to transfer
        return !pattern.toLowerCase().contains(source.toLowerCase()) &&
               pattern.length() > 5;
    }

    // Getters
    public LearningStrategy getCurrentStrategy() { return currentStrategy; }
    public double getLearningRate() { return learningRate; }
    public double getExplorationRate() { return explorationRate; }
    public double getAvgLearningSuccess() { return avgLearningSuccess; }
    public double getMetaProgress() { return metaLearningProgress; }
    public int getStrategyChanges() { return strategyChanges; }
    public int getTotalEvents() { return totalLearningEvents; }
    public int getDomainCount() { return domainPatterns.size(); }

    public void printStats() {
        CommandTerminal.printHighlight("=== META-LEARNER (Learn How To Learn) ===");
        CommandTerminal.print(String.format("  Current Strategy: %s", currentStrategy));
        CommandTerminal.print(String.format("  Learning Rate: %.4f", learningRate));
        CommandTerminal.print(String.format("  Exploration Rate: %.4f", explorationRate));
        CommandTerminal.print(String.format("  Avg Success: %.4f", avgLearningSuccess));
        CommandTerminal.print(String.format("  Meta Progress: %.6f", metaLearningProgress));
        CommandTerminal.print(String.format("  Strategy Changes: %d", strategyChanges));
        CommandTerminal.print(String.format("  Total Events: %d", totalLearningEvents));
        CommandTerminal.print(String.format("  Domains: %d", domainPatterns.size()));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Strategy Performance:");
        for (LearningStrategy s : LearningStrategy.values()) {
            StrategyStats stats = strategyStats.get(s);
            String marker = s == currentStrategy ? " *" : "";
            CommandTerminal.print(String.format("  %s: %.2f%% (%d trials)%s", 
                    s.name(), stats.getSuccessRate() * 100, stats.trials, marker));
        }
    }

    /**
     * Learning parameters container
     */
    public static class LearningParameters {
        public double learningRate = 0.1;
        public double explorationRate = 0.3;
        public LearningStrategy strategy = LearningStrategy.EXPLORATION;
        public boolean useTransfer = false;
        public String transferSource = null;
    }

    /**
     * Learning event record
     */
    private static class LearningEvent {
        final String domain;
        final String pattern;
        final double success;
        final double resonance;
        final LearningStrategy strategy;
        final long timestamp;

        LearningEvent(String domain, String pattern, double success, double resonance, LearningStrategy strategy) {
            this.domain = domain;
            this.pattern = pattern;
            this.success = success;
            this.resonance = resonance;
            this.strategy = strategy;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Strategy statistics
     */
    private static class StrategyStats {
        final LearningStrategy strategy;
        int trials = 0;
        int successes = 0;

        StrategyStats(LearningStrategy strategy) {
            this.strategy = strategy;
        }

        void recordOutcome(boolean success) {
            trials++;
            if (success) successes++;
        }

        double getSuccessRate() {
            return trials > 0 ? (double) successes / trials : 0.5;
        }
    }

    /**
     * Domain pattern tracker
     */
    private static class DomainPattern {
        final String domain;
        final Map<String, PatternInfo> patterns = new HashMap<>();
        int patternCount = 0;
        double avgSuccess = 0.5;

        DomainPattern(String domain) {
            this.domain = domain;
        }

        void addPattern(String pattern, double success, double resonance) {
            PatternInfo info = patterns.computeIfAbsent(pattern, k -> new PatternInfo(pattern));
            info.addOccurrence(success, resonance);
            patternCount++;
            avgSuccess = avgSuccess * 0.95 + success * 0.05;
        }

        List<String> getTopPatterns(int count) {
            List<Map.Entry<String, PatternInfo>> sorted = new ArrayList<>(patterns.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue().avgSuccess, a.getValue().avgSuccess));
            
            List<String> top = new ArrayList<>();
            for (int i = 0; i < Math.min(count, sorted.size()); i++) {
                top.add(sorted.get(i).getKey());
            }
            return top;
        }
    }

    /**
     * Individual pattern info
     */
    private static class PatternInfo {
        final String pattern;
        int occurrences = 0;
        double avgSuccess = 0.5;
        double avgResonance = 0.5;

        PatternInfo(String pattern) {
            this.pattern = pattern;
        }

        void addOccurrence(double success, double resonance) {
            occurrences++;
            avgSuccess = avgSuccess * 0.9 + success * 0.1;
            avgResonance = avgResonance * 0.9 + resonance * 0.1;
        }
    }
}
