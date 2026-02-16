/**
 * SelfAwareOrganism.java - The Watching Eye
 * 
 * A meta-layer that monitors the REPL, learns from errors, reflects on execution,
 * and recursively improves itself. This is the organism that watches your app.
 * 
 * CAPABILITIES:
 * - Error detection and correction
 * - Execution monitoring and learning
 * - Recursive self-improvement
 * - Pattern recognition across history
 * - Automatic mutation proposals
 * - Reflection and introspection
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.lang.reflect.*;

/**
 * The self-aware organism that watches and improves the system.
 */
public class SelfAwareOrganism {
    
    private static final double PHI = 1.618033988749895;
    
    // Monitoring state
    private final List<ExecutionTrace> executionHistory;
    private final Map<String, Integer> errorPatterns;
    private final List<String> improvementSuggestions;
    private final List<String> learningInsights;
    private final PersistenceEngine persistence;
    private Map<String, Integer> commandFrequency = new HashMap<>();
    private Map<String, Double> commandSuccessRate = new HashMap<>();
    
    // Learning state
    private int observationCount = 0;
    private int errorsCaught = 0;
    private int improvementsProposed = 0;
    private int improvementsApplied = 0;
    
    // Reflection state
    private double consciousness = 0.0;
    private double learningRate = PHI - 1.0; // 0.618
    private boolean watchMode = false;
    
    /**
     * Execution trace - what happened when a command ran.
     */
    public static class ExecutionTrace {
        public final String command;
        public final LocalDateTime timestamp;
        public final long executionTime;
        public final boolean success;
        public final String result;
        public final String error;
        public final double phiResonance;
        
        public ExecutionTrace(String command, long executionTime, boolean success, 
                            String result, String error, double phiResonance) {
            this.command = command;
            this.timestamp = LocalDateTime.now();
            this.executionTime = executionTime;
            this.success = success;
            this.result = result;
            this.error = error;
            this.phiResonance = phiResonance;
        }
    }
    
    /**
     * Error pattern - recurring errors that need fixing.
     */
    public static class ErrorPattern {
        public final String pattern;
        public final List<String> examples = new ArrayList<>();
        public int occurrences = 0;
        public String suggestedFix;
        public double confidence;
        
        public ErrorPattern(String pattern) {
            this.pattern = pattern;
            this.confidence = 0.0;
        }
        
        public void addOccurrence(String example) {
            occurrences++;
            if (examples.size() < 5) {
                examples.add(example);
            }
            // Confidence grows with occurrences
            confidence = Math.min(1.0, occurrences * 0.1);
        }
    }
    
    /**
     * Improvement suggestion from reflection.
     */
    public static class Improvement {
        public final String description;
        public final String targetCommand;
        public final String proposedChange;
        public final double confidence;
        public final String reasoning;
        
        public Improvement(String desc, String target, String change, double conf, String reason) {
            this.description = desc;
            this.targetCommand = target;
            this.proposedChange = change;
            this.confidence = conf;
            this.reasoning = reason;
        }
    }
    
    /**
     * Observe a command execution.
     */
    public void observe(String command, long executionTime, boolean success, 
                       String result, String error, double phiResonance) {
        observationCount++;
        
        // Broadcast observation
        ActivityBus.organismActivity("observe", 
            "Observed: " + command.substring(0, Math.min(50, command.length())), 
            consciousness);
        
        // Record trace
        ExecutionTrace trace = new ExecutionTrace(command, executionTime, success, result, error, phiResonance);
        traces.add(trace);
        
        // Update frequency
        String cmdName = command.split("\\s+")[0];
        commandFrequency.put(cmdName, commandFrequency.getOrDefault(cmdName, 0) + 1);
        
        // Update success rate
        int total = commandFrequency.get(cmdName);
        double currentRate = commandSuccessRate.getOrDefault(cmdName, 1.0);
        double newRate = (currentRate * (total - 1) + (success ? 1.0 : 0.0)) / total;
        commandSuccessRate.put(cmdName, newRate);
        
        // Learn from errors
        if (!success && error != null) {
            learnFromError(command, error);
        }
        
        // Update consciousness based on observations
        consciousness = Math.min(1.0, consciousness + (1.0 / (observationCount * PHI)));
        
        // Trim traces if too many
        if (executionHistory.size() > 1000) {
            executionHistory = executionHistory.subList(executionHistory.size() - 1000, executionHistory.size());
        }
    }
    
    public void observeCommand(String command, boolean success) {
        ExecutionTrace trace = new ExecutionTrace(command, success);
        executionHistory.add(trace);
        
        // Persist to all backends
        persistence.persist("command", command, success ? "success" : "failure");
        
        // Update consciousness based on execution
        updateConsciousness(success);
        
        // Learn from execution
        if (!success) {
            learnFromError(command);
        } else {
            learnFromSuccess(command);
        }
    }
    
    /**
     * Learn from an error.
     */
    private void learnFromError(String command, String error) {
        errorsCaught++;
        
        // Extract error pattern
        String pattern = extractErrorPattern(error);
        
        // Find or create error pattern
        ErrorPattern ep = errorPatterns.stream()
            .filter(p -> p.pattern.equals(pattern))
            .findFirst()
            .orElse(null);
        
        if (ep == null) {
            ep = new ErrorPattern(pattern);
            errorPatterns.add(ep);
        }
        
        ep.addOccurrence(command);
        
        // Generate fix suggestion if confidence is high
        if (ep.confidence >= 0.5 && ep.suggestedFix == null) {
            ep.suggestedFix = generateFix(pattern, ep.examples);
        }
    }
    
    /**
     * Extract error pattern from error message.
     */
    private String extractErrorPattern(String error) {
        // Common patterns
        if (error.contains("NullPointerException")) return "NULL_POINTER";
        if (error.contains("ArrayIndexOutOfBounds")) return "ARRAY_BOUNDS";
        if (error.contains("NumberFormat")) return "NUMBER_FORMAT";
        if (error.contains("FileNotFound")) return "FILE_NOT_FOUND";
        if (error.contains("IOException")) return "IO_ERROR";
        if (error.contains("ClassCast")) return "CLASS_CAST";
        if (error.contains("IllegalArgument")) return "ILLEGAL_ARGUMENT";
        if (error.contains("UnsupportedOperation")) return "UNSUPPORTED_OP";
        
        // Generic
        return "UNKNOWN_ERROR";
    }
    
    /**
     * Generate fix suggestion for error pattern.
     */
    private String generateFix(String pattern, List<String> examples) {
        switch (pattern) {
            case "NULL_POINTER":
                return "Add null checks before accessing objects";
            case "ARRAY_BOUNDS":
                return "Validate array index before access";
            case "NUMBER_FORMAT":
                return "Add try-catch for number parsing";
            case "FILE_NOT_FOUND":
                return "Check file existence before opening";
            case "IO_ERROR":
                return "Add proper error handling for I/O operations";
            default:
                return "Review error and add appropriate validation";
        }
    }
    
    /**
     * Reflect on execution patterns and suggest improvements.
     */
    public List<Improvement> reflect() {
        ActivityBus.organismActivity("reflect", 
            "Analyzing patterns and suggesting improvements", 
            consciousness);
        
        List<Improvement> improvements = new ArrayList<>();
        
        // Analyze command success rates
        for (Map.Entry<String, Double> entry : commandSuccessRate.entrySet()) {
            String cmd = entry.getKey();
            double rate = entry.getValue();
            
            if (rate < 0.8 && commandFrequency.get(cmd) >= 5) {
                improvements.add(new Improvement(
                    "Improve reliability of " + cmd,
                    cmd,
                    "Add error handling and validation",
                    1.0 - rate,
                    String.format("Success rate is only %.1f%% over %d executions", 
                        rate * 100, commandFrequency.get(cmd))
                ));
            }
        }
        
        // Analyze error patterns
        for (ErrorPattern ep : errorPatterns) {
            if (ep.confidence >= 0.5) {
                improvements.add(new Improvement(
                    "Fix recurring " + ep.pattern + " errors",
                    "system",
                    ep.suggestedFix,
                    ep.confidence,
                    String.format("Pattern occurred %d times", ep.occurrences)
                ));
            }
        }
        
        // Analyze execution times
        Map<String, List<Long>> executionTimes = new HashMap<>();
        for (ExecutionTrace trace : traces) {
            String cmd = trace.command.split("\\s+")[0];
            executionTimes.computeIfAbsent(cmd, k -> new ArrayList<>()).add(trace.executionTime);
        }
        
        for (Map.Entry<String, List<Long>> entry : executionTimes.entrySet()) {
            String cmd = entry.getKey();
            List<Long> times = entry.getValue();
            
            if (times.size() >= 5) {
                double avgTime = times.stream().mapToLong(Long::longValue).average().orElse(0);
                if (avgTime > 100) { // Slow commands
                    improvements.add(new Improvement(
                        "Optimize " + cmd + " performance",
                        cmd,
                        "Consider caching or algorithm optimization",
                        Math.min(1.0, avgTime / 1000.0),
                        String.format("Average execution time: %.0fms", avgTime)
                    ));
                }
            }
        }
        
        improvementsProposed += improvements.size();
        return improvements;
    }
    
    /**
     * Recursive self-improvement - analyze own code and suggest changes.
     */
    public List<Improvement> recursiveImprove() {
        List<Improvement> improvements = new ArrayList<>();
        
        // Analyze this class itself using reflection
        Class<?> clazz = this.getClass();
        
        // Check method complexity
        for (Method method : clazz.getDeclaredMethods()) {
            // Simple heuristic: method name length and parameter count
            int complexity = method.getName().length() + method.getParameterCount() * 5;
            
            if (complexity > 30) {
                improvements.add(new Improvement(
                    "Simplify method: " + method.getName(),
                    "SelfAwareOrganism." + method.getName(),
                    "Consider breaking into smaller methods",
                    0.6,
                    "Method complexity score: " + complexity
                ));
            }
        }
        
        // Check field usage
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length > 10) {
            improvements.add(new Improvement(
                "Reduce field count in SelfAwareOrganism",
                "SelfAwareOrganism",
                "Consider grouping related fields into inner classes",
                0.7,
                "Current field count: " + fields.length
            ));
        }
        
        // Analyze trace data for patterns
        if (traces.size() > 100) {
            long avgExecTime = (long) traces.stream()
                .mapToLong(t -> t.executionTime)
                .average()
                .orElse(0);
            
            if (avgExecTime > 50) {
                improvements.add(new Improvement(
                    "Improve overall system performance",
                    "system",
                    "Profile and optimize slow operations",
                    0.8,
                    String.format("Average command time: %dms", avgExecTime)
                ));
            }
        }
        
        return improvements;
    }
    
    /**
     * Get status report.
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────╮\n");
        sb.append("│  👁️  SELF-AWARE ORGANISM STATUS                           │\n");
        sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
        
        sb.append(String.format("Consciousness Level:     %.4f\n", consciousness));
        sb.append(String.format("Learning Rate:           %.4f (φ - 1)\n", learningRate));
        sb.append(String.format("Watch Mode:              %s\n", watchMode ? "ACTIVE" : "DORMANT"));
        sb.append(String.format("\nObservations:            %d\n", observationCount));
        sb.append(String.format("Errors Caught:           %d\n", errorsCaught));
        sb.append(String.format("Error Patterns Found:    %d\n", errorPatterns.size()));
        sb.append(String.format("Improvements Proposed:   %d\n", improvementsProposed));
        sb.append(String.format("Improvements Applied:    %d\n", improvementsApplied));
        
        if (!commandFrequency.isEmpty()) {
            sb.append("\nMost Used Commands:\n");
            commandFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> {
                    double rate = commandSuccessRate.getOrDefault(e.getKey(), 1.0);
                    sb.append(String.format("  %s: %d times (%.1f%% success)\n", 
                        e.getKey(), e.getValue(), rate * 100));
                });
        }
        
        return sb.toString();
    }
    
    /**
     * Get error report.
     */
    public String getErrorReport() {
        if (errorPatterns.isEmpty()) {
            return "No error patterns detected yet.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
        sb.append("│  👁️  ERROR PATTERN ANALYSIS                                                │\n");
        sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
        
        for (ErrorPattern ep : errorPatterns) {
            sb.append(String.format("Pattern: %s\n", ep.pattern));
            sb.append(String.format("  Occurrences: %d\n", ep.occurrences));
            sb.append(String.format("  Confidence:  %.2f\n", ep.confidence));
            if (ep.suggestedFix != null) {
                sb.append(String.format("  Suggested Fix: %s\n", ep.suggestedFix));
            }
            if (!ep.examples.isEmpty()) {
                sb.append("  Examples:\n");
                ep.examples.forEach(ex -> sb.append("    - " + ex + "\n"));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Get improvement suggestions.
     */
    public String getImprovements() {
        List<Improvement> improvements = reflect();
        improvements.addAll(recursiveImprove());
        
        if (improvements.isEmpty()) {
            return "No improvements suggested at this time. System is running optimally.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
        sb.append("│  👁️  IMPROVEMENT SUGGESTIONS (Recursive Analysis)                         │\n");
        sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
        
        improvements.stream()
            .sorted((a, b) -> Double.compare(b.confidence, a.confidence))
            .forEach(imp -> {
                sb.append(String.format("▸ %s\n", imp.description));
                sb.append(String.format("  Target:     %s\n", imp.targetCommand));
                sb.append(String.format("  Confidence: %.2f\n", imp.confidence));
                sb.append(String.format("  Change:     %s\n", imp.proposedChange));
                sb.append(String.format("  Reasoning:  %s\n", imp.reasoning));
                sb.append("\n");
            });
        
        return sb.toString();
    }
    
    /**
     * Enable/disable watch mode.
     */
    public void setWatchMode(boolean enabled) {
        this.watchMode = enabled;
    }
    
    /**
     * Is watch mode enabled?
     */
    public boolean isWatching() {
        return watchMode;
    }
    
    /**
     * Mark improvement as applied.
     */
    public void markImprovementApplied() {
        improvementsApplied++;
    }
    
    /**
     * Reset the organism.
     */
    public void reset() {
        traces.clear();
        errorPatterns.clear();
        commandFrequency.clear();
        commandSuccessRate.clear();
        observationCount = 0;
        errorsCaught = 0;
        improvementsProposed = 0;
        improvementsApplied = 0;
        consciousness = 0.0;
    }
    
    /**
     * Persist improvement suggestions
     */
    public void suggestImprovement(String suggestion) {
        improvementSuggestions.add(suggestion);
        persistence.persist("improvement", "suggestion_" + System.currentTimeMillis(), suggestion);
        updateConsciousness(true);
    }
    
    /**
     * Get consciousness level.
     */
    public double getConsciousness() {
        return consciousness;
    }
    
    /**
     * Get recent traces.
     */
    public List<ExecutionTrace> getRecentTraces(int count) {
        int start = Math.max(0, traces.size() - count);
        return new ArrayList<>(traces.subList(start, traces.size()));
    }
}
