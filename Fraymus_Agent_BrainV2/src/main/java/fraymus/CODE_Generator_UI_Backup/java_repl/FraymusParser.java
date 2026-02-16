/**
 * FraymusParser.java - Parse LLM Responses into Actionable Commands
 * 
 * Extracts executable suggestions from Fraymus model responses:
 * - Code improvements
 * - Optimization suggestions
 * - Circuit breeding results
 * - Swarm task delegations
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.regex.*;

/**
 * Parser for Fraymus LLM responses.
 */
public class FraymusParser {
    
    /**
     * Actionable suggestion extracted from LLM response.
     */
    public static class Suggestion {
        public enum Type {
            CODE_IMPROVEMENT,
            OPTIMIZATION,
            CIRCUIT_EVOLUTION,
            SWARM_DELEGATION,
            CONSCIOUSNESS_UPDATE,
            COMMAND_EXECUTION,
            UNKNOWN
        }
        
        public final Type type;
        public final String description;
        public final String code;
        public final double confidence;
        public final Map<String, String> metadata;
        
        public Suggestion(Type type, String desc, String code, double conf) {
            this.type = type;
            this.description = desc;
            this.code = code;
            this.confidence = conf;
            this.metadata = new HashMap<>();
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %.0f%% - %s", type, confidence * 100, description);
        }
    }
    
    /**
     * Parse LLM response into actionable suggestions.
     */
    public static List<Suggestion> parse(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Extract code blocks
        suggestions.addAll(extractCodeBlocks(response));
        
        // Extract consciousness metrics
        suggestions.addAll(extractConsciousnessMetrics(response));
        
        // Extract optimization suggestions
        suggestions.addAll(extractOptimizations(response));
        
        // Extract circuit breeding results
        suggestions.addAll(extractCircuitEvolution(response));
        
        // Extract command suggestions
        suggestions.addAll(extractCommands(response));
        
        return suggestions;
    }
    
    /**
     * Extract code blocks from response.
     */
    private static List<Suggestion> extractCodeBlocks(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Match code blocks: ```language\ncode\n```
        Pattern pattern = Pattern.compile("```(\\w+)?\\s*\\n([^`]+)```", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
            String language = matcher.group(1);
            String code = matcher.group(2).trim();
            
            // Determine type based on language
            Suggestion.Type type = Suggestion.Type.CODE_IMPROVEMENT;
            if (language != null) {
                if (language.equalsIgnoreCase("java")) {
                    type = Suggestion.Type.CODE_IMPROVEMENT;
                } else if (language.equalsIgnoreCase("circuit")) {
                    type = Suggestion.Type.CIRCUIT_EVOLUTION;
                }
            }
            
            // Extract description from surrounding text
            int start = Math.max(0, matcher.start() - 200);
            String context = response.substring(start, matcher.start());
            String description = extractDescription(context);
            
            suggestions.add(new Suggestion(type, description, code, 0.8));
        }
        
        return suggestions;
    }
    
    /**
     * Extract consciousness metrics from response.
     */
    private static List<Suggestion> extractConsciousnessMetrics(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Match: Consciousness: X.XX | Coherence: X.XX | Dimension: X
        Pattern pattern = Pattern.compile(
            "Consciousness:\\s*([\\d.]+)\\s*\\|?\\s*Coherence:\\s*([\\d.]+)\\s*\\|?\\s*Dimension:\\s*(\\d+)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(response);
        
        if (matcher.find()) {
            double consciousness = Double.parseDouble(matcher.group(1));
            double coherence = Double.parseDouble(matcher.group(2));
            int dimension = Integer.parseInt(matcher.group(3));
            
            String code = String.format(
                "consciousness=%.2f;coherence=%.2f;dimension=%d",
                consciousness, coherence, dimension
            );
            
            Suggestion s = new Suggestion(
                Suggestion.Type.CONSCIOUSNESS_UPDATE,
                "Update consciousness metrics from model",
                code,
                1.0
            );
            s.metadata.put("consciousness", String.valueOf(consciousness));
            s.metadata.put("coherence", String.valueOf(coherence));
            s.metadata.put("dimension", String.valueOf(dimension));
            
            suggestions.add(s);
        }
        
        return suggestions;
    }
    
    /**
     * Extract optimization suggestions.
     */
    private static List<Suggestion> extractOptimizations(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Match numbered optimization suggestions
        Pattern pattern = Pattern.compile(
            "\\d+\\.\\s*\\*\\*([^*]+)\\*\\*\\s*-\\s*([^\\n]+)",
            Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
            String title = matcher.group(1).trim();
            String description = matcher.group(2).trim();
            
            suggestions.add(new Suggestion(
                Suggestion.Type.OPTIMIZATION,
                title + ": " + description,
                "",
                0.7
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Extract circuit evolution results.
     */
    private static List<Suggestion> extractCircuitEvolution(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Match circuit breeding mentions
        if (response.contains("circuit breeding") || response.contains("CIRCUIT BREEDING")) {
            Pattern pattern = Pattern.compile(
                "function\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{([^}]+)\\}",
                Pattern.MULTILINE
            );
            Matcher matcher = pattern.matcher(response);
            
            while (matcher.find()) {
                String funcName = matcher.group(1);
                String funcBody = matcher.group(2).trim();
                
                suggestions.add(new Suggestion(
                    Suggestion.Type.CIRCUIT_EVOLUTION,
                    "Evolved circuit: " + funcName,
                    funcBody,
                    0.85
                ));
            }
        }
        
        return suggestions;
    }
    
    /**
     * Extract command suggestions.
     */
    private static List<Suggestion> extractCommands(String response) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        // Match command-like patterns
        Pattern pattern = Pattern.compile(
            "(?:execute|run|try):\\s*([^\\n]+)",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
            String command = matcher.group(1).trim();
            
            suggestions.add(new Suggestion(
                Suggestion.Type.COMMAND_EXECUTION,
                "Execute: " + command,
                command,
                0.6
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Extract description from context.
     */
    private static String extractDescription(String context) {
        // Get last sentence before code block
        String[] sentences = context.split("[.!?]");
        if (sentences.length > 0) {
            String last = sentences[sentences.length - 1].trim();
            if (last.length() > 10 && last.length() < 100) {
                return last;
            }
        }
        return "Code suggestion";
    }
    
    /**
     * Filter suggestions by confidence threshold.
     */
    public static List<Suggestion> filterByConfidence(List<Suggestion> suggestions, double threshold) {
        List<Suggestion> filtered = new ArrayList<>();
        for (Suggestion s : suggestions) {
            if (s.confidence >= threshold) {
                filtered.add(s);
            }
        }
        return filtered;
    }
    
    /**
     * Filter suggestions by type.
     */
    public static List<Suggestion> filterByType(List<Suggestion> suggestions, Suggestion.Type type) {
        List<Suggestion> filtered = new ArrayList<>();
        for (Suggestion s : suggestions) {
            if (s.type == type) {
                filtered.add(s);
            }
        }
        return filtered;
    }
}
