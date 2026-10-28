package fraymus.neural;

import java.util.*;
import java.util.regex.*;

/**
 * WebFactExtractor - Extract Facts from Text
 * 
 * Extracts factual statements:
 * - Subject-predicate-object triples
 * - Statistical data (numbers with units)
 * - Definitions (X is Y)
 * - Procedures (steps, processes)
 * 
 * Uses pattern-based extraction
 */
public class WebFactExtractor {
    
    // Fact patterns
    private static final Pattern DEFINITION_PATTERN = Pattern.compile(
        "\\b([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)\\s+(?:is|are|was|were|means|refers to|represents)\\s+(.+?)(?:[.!?]|$)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern STATISTIC_PATTERN = Pattern.compile(
        "\\b(\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?)\\s+((?:%|percent|million|billion|trillion|thousand|hundred|dollars?|euros?|pounds?|yen|yuan|people|users|customers|employees|students|cases|instances|items|units|pages|chapters|sections|years|months|days|hours|minutes|seconds))\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HAS_A_PATTERN = Pattern.compile(
        "\\b([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)\\s+(?:has|have|had|contains|includes)\\s+(.+?)(?:[.!?]|$)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern LOCATED_IN_PATTERN = Pattern.compile(
        "\\b([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)\\s+(?:is|are|was|were|located in|found in|situated in|based in)\\s+(.+?)(?:[.!?]|$)",
        Pattern.CASE_INSENSITIVE
    );
    
    private final List<ExtractionSynapse.Fact> extractedFacts = new ArrayList<>();
    private final Map<String, Integer> factFrequency = new HashMap<>();
    
    public WebFactExtractor() {
        System.out.println("[WebFactExtractor] Initialized");
    }
    
    /**
     * Extract all facts from text
     */
    public List<ExtractionSynapse.Fact> extractFacts(String text) {
        List<ExtractionSynapse.Fact> facts = new ArrayList<>();
        
        // Extract by pattern
        facts.addAll(extractDefinitions(text));
        facts.addAll(extractStatistics(text));
        facts.addAll(extractHasA(text));
        facts.addAll(extractLocatedIn(text));
        
        // Remove duplicates
        Map<String, ExtractionSynapse.Fact> uniqueFacts = new LinkedHashMap<>();
        for (ExtractionSynapse.Fact fact : facts) {
            String key = fact.getSubject() + "|" + fact.getPredicate() + "|" + fact.getObject();
            if (!uniqueFacts.containsKey(key)) {
                uniqueFacts.put(key, fact);
            }
        }
        
        // Update frequency
        for (ExtractionSynapse.Fact fact : uniqueFacts.values()) {
            String key = fact.toString();
            factFrequency.put(key, factFrequency.getOrDefault(key, 0) + 1);
        }
        
        extractedFacts.clear();
        extractedFacts.addAll(uniqueFacts.values());
        
        return new ArrayList<>(uniqueFacts.values());
    }
    
    /**
     * Extract definitions (X is Y)
     */
    private List<ExtractionSynapse.Fact> extractDefinitions(String text) {
        List<ExtractionSynapse.Fact> facts = new ArrayList<>();
        Matcher matcher = DEFINITION_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String subject = matcher.group(1).trim();
            String object = matcher.group(2).trim();
            String predicate = "is";
            
            ExtractionSynapse.Fact fact = new ExtractionSynapse.Fact(subject, predicate, object);
            fact.setConfidence(0.7); // Definition patterns have moderate confidence
            facts.add(fact);
        }
        
        return facts;
    }
    
    /**
     * Extract statistical data
     */
    private List<ExtractionSynapse.Fact> extractStatistics(String text) {
        List<ExtractionSynapse.Fact> facts = new ArrayList<>();
        Matcher matcher = STATISTIC_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String number = matcher.group(1);
            String unit = matcher.group(2);
            
            // Find the context (what the statistic is about)
            int start = matcher.start();
            int contextStart = Math.max(0, start - 50);
            String context = text.substring(contextStart, start).trim();
            
            // Extract the subject from context (last capitalized word or phrase)
            String[] words = context.split("\\s+");
            String subject = "unknown";
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].matches("[A-Z][a-zA-Z]+")) {
                    subject = words[i];
                    break;
                }
            }
            
            String object = number + " " + unit;
            String predicate = "has";
            
            ExtractionSynapse.Fact fact = new ExtractionSynapse.Fact(subject, predicate, object);
            fact.setConfidence(0.9); // Statistics have high confidence
            facts.add(fact);
        }
        
        return facts;
    }
    
    /**
     * Extract "has a" relationships
     */
    private List<ExtractionSynapse.Fact> extractHasA(String text) {
        List<ExtractionSynapse.Fact> facts = new ArrayList<>();
        Matcher matcher = HAS_A_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String subject = matcher.group(1).trim();
            String object = matcher.group(2).trim();
            String predicate = "has";
            
            ExtractionSynapse.Fact fact = new ExtractionSynapse.Fact(subject, predicate, object);
            fact.setConfidence(0.8);
            facts.add(fact);
        }
        
        return facts;
    }
    
    /**
     * Extract "located in" relationships
     */
    private List<ExtractionSynapse.Fact> extractLocatedIn(String text) {
        List<ExtractionSynapse.Fact> facts = new ArrayList<>();
        Matcher matcher = LOCATED_IN_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String subject = matcher.group(1).trim();
            String object = matcher.group(2).trim();
            String predicate = "located in";
            
            ExtractionSynapse.Fact fact = new ExtractionSynapse.Fact(subject, predicate, object);
            fact.setConfidence(0.8);
            facts.add(fact);
        }
        
        return facts;
    }
    
    /**
     * Get facts by subject
     */
    public List<ExtractionSynapse.Fact> getFactsBySubject(String subject) {
        List<ExtractionSynapse.Fact> filtered = new ArrayList<>();
        for (ExtractionSynapse.Fact fact : extractedFacts) {
            if (fact.getSubject().equalsIgnoreCase(subject)) {
                filtered.add(fact);
            }
        }
        return filtered;
    }
    
    /**
     * Get facts by predicate
     */
    public List<ExtractionSynapse.Fact> getFactsByPredicate(String predicate) {
        List<ExtractionSynapse.Fact> filtered = new ArrayList<>();
        for (ExtractionSynapse.Fact fact : extractedFacts) {
            if (fact.getPredicate().equalsIgnoreCase(predicate)) {
                filtered.add(fact);
            }
        }
        return filtered;
    }
    
    /**
     * Get facts by object
     */
    public List<ExtractionSynapse.Fact> getFactsByObject(String object) {
        List<ExtractionSynapse.Fact> filtered = new ArrayList<>();
        for (ExtractionSynapse.Fact fact : extractedFacts) {
            if (fact.getObject().toLowerCase().contains(object.toLowerCase())) {
                filtered.add(fact);
            }
        }
        return filtered;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        int total = extractedFacts.size();
        int highConfidence = 0;
        int mediumConfidence = 0;
        int lowConfidence = 0;
        
        for (ExtractionSynapse.Fact fact : extractedFacts) {
            if (fact.getConfidence() >= 0.8) highConfidence++;
            else if (fact.getConfidence() >= 0.5) mediumConfidence++;
            else lowConfidence++;
        }
        
        return String.format(
            "[WebFactExtractor] Total: %d, High confidence: %d, Medium: %d, Low: %d",
            total, highConfidence, mediumConfidence, lowConfidence
        );
    }
}
