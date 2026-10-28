package sandbox.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Analyzes incoming queries to determine task type, complexity, and requirements.
 * 
 * This component is responsible for understanding the nature of each query so
 * the orchestrator can route it to the most appropriate backend. It uses pattern
 * matching, keyword analysis, and heuristics to classify queries.
 */
public class QueryAnalyzer {
    
    /**
     * Query analysis result
     */
    public static class AnalysisResult {
        private final BackendInterface.TaskType taskType;
        private final double complexityScore; // 0.0 (simple) to 1.0 (complex)
        private final double latencyRequirement; // 0.0 (flexible) to 1.0 (critical)
        private final double accuracyRequirement; // 0.0 (flexible) to 1.0 (critical)
        private final Set<String> keywords;
        private final Map<String, Object> features;
        
        public AnalysisResult(BackendInterface.TaskType taskType, double complexityScore,
                            double latencyRequirement, double accuracyRequirement,
                            Set<String> keywords, Map<String, Object> features) {
            this.taskType = taskType;
            this.complexityScore = complexityScore;
            this.latencyRequirement = latencyRequirement;
            this.accuracyRequirement = accuracyRequirement;
            this.keywords = keywords;
            this.features = features;
        }
        
        public BackendInterface.TaskType getTaskType() { return taskType; }
        public double getComplexityScore() { return complexityScore; }
        public double getLatencyRequirement() { return latencyRequirement; }
        public double getAccuracyRequirement() { return accuracyRequirement; }
        public Set<String> getKeywords() { return keywords; }
        public Map<String, Object> getFeatures() { return features; }
    }
    
    // Reasoning patterns (why, how, explain, analyze)
    private static final Set<String> REASONING_KEYWORDS = new HashSet<>(Arrays.asList(
        "why", "how", "explain", "analyze", "reason", "logic", "because", "therefore",
        "understand", "interpret", "evaluate", "assess", "justify", "prove", "disprove"
    ));
    
    // Generation patterns (create, generate, compose, write)
    private static final Set<String> GENERATION_KEYWORDS = new HashSet<>(Arrays.asList(
        "create", "generate", "compose", "write", "make", "produce", "design",
        "invent", "imagine", "story", "poem", "code", "content", "creative"
    ));
    
    // Knowledge patterns (what, who, when, where, fact)
    private static final Set<String> KNOWLEDGE_KEYWORDS = new HashSet<>(Arrays.asList(
        "what", "who", "when", "where", "fact", "definition", "meaning", "list",
        "information", "data", "lookup", "find", "search", "retrieve", "show"
    ));
    
    // Fast inference patterns (quick, fast, immediate, real-time)
    private static final Set<String> FAST_KEYWORDS = new HashSet<>(Arrays.asList(
        "quick", "fast", "immediate", "real-time", "instant", "now", "asap",
        "urgent", "priority", "speed", "latency", "responsive"
    ));
    
    // Complex analysis patterns (complex, deep, multi-step, comprehensive)
    private static final Set<String> COMPLEX_KEYWORDS = new HashSet<>(Arrays.asList(
        "complex", "deep", "multi-step", "comprehensive", "detailed", "thorough",
        "extensive", "elaborate", "nuanced", "sophisticated", "advanced"
    ));
    
    /**
     * Analyze a query and determine its characteristics
     * 
     * @param query The query to analyze
     * @return AnalysisResult containing task type and requirements
     */
    public AnalysisResult analyze(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new AnalysisResult(
                BackendInterface.TaskType.REASONING,
                0.0,
                0.5,
                0.5,
                new HashSet<>(),
                Map.of()
            );
        }
        
        String lowerQuery = query.toLowerCase();
        Set<String> foundKeywords = extractKeywords(lowerQuery);
        
        // Determine task type
        BackendInterface.TaskType taskType = determineTaskType(lowerQuery, foundKeywords);
        
        // Calculate complexity score
        double complexityScore = calculateComplexity(lowerQuery, foundKeywords);
        
        // Calculate latency requirement
        double latencyRequirement = calculateLatencyRequirement(lowerQuery, foundKeywords);
        
        // Calculate accuracy requirement
        double accuracyRequirement = calculateAccuracyRequirement(lowerQuery, foundKeywords);
        
        // Extract features
        Map<String, Object> features = extractFeatures(lowerQuery);
        
        return new AnalysisResult(
            taskType,
            complexityScore,
            latencyRequirement,
            accuracyRequirement,
            foundKeywords,
            features
        );
    }
    
    /**
     * Extract keywords from the query
     */
    private Set<String> extractKeywords(String query) {
        Set<String> keywords = new HashSet<>();
        
        for (String keyword : REASONING_KEYWORDS) {
            if (query.contains(keyword)) keywords.add(keyword);
        }
        for (String keyword : GENERATION_KEYWORDS) {
            if (query.contains(keyword)) keywords.add(keyword);
        }
        for (String keyword : KNOWLEDGE_KEYWORDS) {
            if (query.contains(keyword)) keywords.add(keyword);
        }
        for (String keyword : FAST_KEYWORDS) {
            if (query.contains(keyword)) keywords.add(keyword);
        }
        for (String keyword : COMPLEX_KEYWORDS) {
            if (query.contains(keyword)) keywords.add(keyword);
        }
        
        return keywords;
    }
    
    /**
     * Determine the primary task type
     */
    private BackendInterface.TaskType determineTaskType(String query, Set<String> keywords) {
        int reasoningScore = countMatches(keywords, REASONING_KEYWORDS);
        int generationScore = countMatches(keywords, GENERATION_KEYWORDS);
        int knowledgeScore = countMatches(keywords, KNOWLEDGE_KEYWORDS);
        int fastScore = countMatches(keywords, FAST_KEYWORDS);
        int complexScore = countMatches(keywords, COMPLEX_KEYWORDS);
        
        // Priority-based decision
        if (complexScore > 0 && reasoningScore > 0) {
            return BackendInterface.TaskType.COMPLEX_ANALYSIS;
        }
        if (fastScore > 0) {
            return BackendInterface.TaskType.FAST_INFERENCE;
        }
        if (reasoningScore >= generationScore && reasoningScore >= knowledgeScore) {
            return BackendInterface.TaskType.REASONING;
        }
        if (generationScore >= knowledgeScore) {
            return BackendInterface.TaskType.GENERATION;
        }
        return BackendInterface.TaskType.KNOWLEDGE;
    }
    
    /**
     * Calculate complexity score based on query characteristics
     */
    private double calculateComplexity(String query, Set<String> keywords) {
        double complexity = 0.0;
        
        // Length contributes to complexity
        int wordCount = query.split("\\s+").length;
        complexity += Math.min(wordCount / 100.0, 0.3);
        
        // Complex keywords
        if (keywords.stream().anyMatch(COMPLEX_KEYWORDS::contains)) {
            complexity += 0.4;
        }
        
        // Question marks indicate questions (often more complex)
        long questionMarkCount = query.chars().filter(c -> c == '?').count();
        complexity += Math.min(questionMarkCount * 0.1, 0.2);
        
        // Nested structures (parentheses, brackets)
        long nestingDepth = Math.max(
            query.chars().filter(c -> c == '(').count(),
            query.chars().filter(c -> c == '[').count()
        );
        complexity += Math.min(nestingDepth * 0.1, 0.1);
        
        return Math.min(complexity, 1.0);
    }
    
    /**
     * Calculate latency requirement (higher = more critical)
     */
    private double calculateLatencyRequirement(String query, Set<String> keywords) {
        if (keywords.stream().anyMatch(FAST_KEYWORDS::contains)) {
            return 0.9; // High latency requirement
        }
        if (keywords.stream().anyMatch(COMPLEX_KEYWORDS::contains)) {
            return 0.3; // Low latency requirement (complex tasks take time)
        }
        return 0.5; // Moderate
    }
    
    /**
     * Calculate accuracy requirement (higher = more critical)
     */
    private double calculateAccuracyRequirement(String query, Set<String> keywords) {
        double accuracy = 0.5;
        
        // Knowledge retrieval needs high accuracy
        if (keywords.stream().anyMatch(KNOWLEDGE_KEYWORDS::contains)) {
            accuracy += 0.3;
        }
        
        // Complex analysis needs high accuracy
        if (keywords.stream().anyMatch(COMPLEX_KEYWORDS::contains)) {
            accuracy += 0.2;
        }
        
        // Fast inference may tolerate lower accuracy
        if (keywords.stream().anyMatch(FAST_KEYWORDS::contains)) {
            accuracy -= 0.2;
        }
        
        return Math.max(0.0, Math.min(accuracy, 1.0));
    }
    
    /**
     * Extract additional features from the query
     */
    private Map<String, Object> extractFeatures(String query) {
        return Map.of(
            "length", query.length(),
            "wordCount", query.split("\\s+").length,
            "hasQuestionMark", query.contains("?"),
            "hasExclamation", query.contains("!"),
            "hasCode", query.contains("```") || query.contains("code"),
            "hasNumbers", Pattern.compile("\\d").matcher(query).find()
        );
    }
    
    /**
     * Count how many keywords from a set are present
     */
    private int countMatches(Set<String> found, Set<String> target) {
        return (int) found.stream().filter(target::contains).count();
    }
}
