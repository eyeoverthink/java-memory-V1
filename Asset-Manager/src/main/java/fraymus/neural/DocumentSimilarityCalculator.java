package fraymus.neural;

import java.util.*;

/**
 * DocumentSimilarityCalculator - Calculate Document Similarity
 * 
 * Calculates similarity between documents:
 * - Jaccard similarity (word overlap)
 * - Cosine similarity (TF-IDF vectors)
 * - Citation-based similarity
 * - Combined similarity score
 */
public class DocumentSimilarityCalculator {
    
    public DocumentSimilarityCalculator() {
        System.out.println("[DocumentSimilarityCalculator] Initialized");
    }
    
    /**
     * Calculate Jaccard similarity between two texts
     */
    public double calculateJaccardSimilarity(String text1, String text2) {
        Set<String> words1 = extractWords(text1);
        Set<String> words2 = extractWords(text2);
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        if (union.isEmpty()) return 0.0;
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Calculate cosine similarity (simplified word frequency)
     */
    public double calculateCosineSimilarity(String text1, String text2) {
        Map<String, Integer> freq1 = getWordFrequency(text1);
        Map<String, Integer> freq2 = getWordFrequency(text2);
        
        Set<String> allWords = new HashSet<>();
        allWords.addAll(freq1.keySet());
        allWords.addAll(freq2.keySet());
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (String word : allWords) {
            int f1 = freq1.getOrDefault(word, 0);
            int f2 = freq2.getOrDefault(word, 0);
            
            dotProduct += f1 * f2;
            norm1 += f1 * f1;
            norm2 += f2 * f2;
        }
        
        if (norm1 == 0 || norm2 == 0) return 0.0;
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * Calculate citation-based similarity
     */
    public double calculateCitationSimilarity(Set<String> citations1, Set<String> citations2) {
        if (citations1.isEmpty() || citations2.isEmpty()) return 0.0;
        
        Set<String> intersection = new HashSet<>(citations1);
        intersection.retainAll(citations2);
        
        Set<String> union = new HashSet<>(citations1);
        union.addAll(citations2);
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Calculate combined similarity
     */
    public double calculateCombinedSimilarity(String text1, String text2, 
                                               Set<String> citations1, Set<String> citations2) {
        double textSimilarity = calculateJaccardSimilarity(text1, text2);
        double citationSimilarity = calculateCitationSimilarity(citations1, citations2);
        
        // Weighted combination (70% text, 30% citations)
        return 0.7 * textSimilarity + 0.3 * citationSimilarity;
    }
    
    /**
     * Extract words from text
     */
    private Set<String> extractWords(String text) {
        Set<String> words = new HashSet<>();
        String[] tokens = text.toLowerCase().split("\\s+");
        
        for (String token : tokens) {
            // Remove punctuation and keep only alphanumeric
            String cleaned = token.replaceAll("[^a-zA-Z0-9]", "");
            if (cleaned.length() > 2) { // Filter out very short words
                words.add(cleaned);
            }
        }
        
        return words;
    }
    
    /**
     * Get word frequency
     */
    private Map<String, Integer> getWordFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String[] tokens = text.toLowerCase().split("\\s+");
        
        for (String token : tokens) {
            String cleaned = token.replaceAll("[^a-zA-Z0-9]", "");
            if (cleaned.length() > 2) {
                freq.put(cleaned, freq.getOrDefault(cleaned, 0) + 1);
            }
        }
        
        return freq;
    }
    
    /**
     * Find most similar documents
     */
    public List<Map.Entry<String, Double>> findMostSimilar(String queryText, 
                                                          Map<String, String> documents, 
                                                          int limit) {
        Map<String, Double> similarities = new HashMap<>();
        
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            double sim = calculateJaccardSimilarity(queryText, entry.getValue());
            similarities.put(entry.getKey(), sim);
        }
        
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(similarities.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        if (limit > 0 && sorted.size() > limit) {
            return sorted.subList(0, limit);
        }
        
        return sorted;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return "[DocumentSimilarityCalculator] Jaccard, Cosine, Citation, Combined similarity methods available";
    }
}
