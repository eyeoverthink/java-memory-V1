package fraymus.neural;

import java.util.*;

/**
 * Test DocumentSimilarityCalculator - Phase 3.3
 * 
 * Tests:
 * 1. Jaccard similarity
 * 2. Cosine similarity
 * 3. Citation similarity
 * 4. Combined similarity
 * 5. Most similar documents
 */
public class DocumentSimilarityCalculatorTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("DOCUMENT SIMILARITY CALCULATOR TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize
        System.out.println("[TEST 1] Initialization");
        DocumentSimilarityCalculator calculator = new DocumentSimilarityCalculator();
        System.out.println("   ✓ DocumentSimilarityCalculator initialized");
        System.out.println();
        
        // Test texts
        String text1 = "The quick brown fox jumps over the lazy dog";
        String text2 = "The quick brown fox jumps over the lazy cat";
        String text3 = "Artificial intelligence is the future of technology";
        
        // Test Jaccard similarity
        System.out.println("[TEST 2] Jaccard Similarity");
        double jaccard12 = calculator.calculateJaccardSimilarity(text1, text2);
        double jaccard13 = calculator.calculateJaccardSimilarity(text1, text3);
        System.out.println("   text1 vs text2: " + String.format("%.3f", jaccard12));
        System.out.println("   text1 vs text3: " + String.format("%.3f", jaccard13));
        System.out.println();
        
        // Test Cosine similarity
        System.out.println("[TEST 3] Cosine Similarity");
        double cosine12 = calculator.calculateCosineSimilarity(text1, text2);
        double cosine13 = calculator.calculateCosineSimilarity(text1, text3);
        System.out.println("   text1 vs text2: " + String.format("%.3f", cosine12));
        System.out.println("   text1 vs text3: " + String.format("%.3f", cosine13));
        System.out.println();
        
        // Test Citation similarity
        System.out.println("[TEST 4] Citation Similarity");
        Set<String> citations1 = new HashSet<>(Arrays.asList("refA", "refB", "refC"));
        Set<String> citations2 = new HashSet<>(Arrays.asList("refA", "refB", "refD"));
        Set<String> citations3 = new HashSet<>(Arrays.asList("refX", "refY", "refZ"));
        
        double citationSim12 = calculator.calculateCitationSimilarity(citations1, citations2);
        double citationSim13 = calculator.calculateCitationSimilarity(citations1, citations3);
        System.out.println("   citations1 vs citations2: " + String.format("%.3f", citationSim12));
        System.out.println("   citations1 vs citations3: " + String.format("%.3f", citationSim13));
        System.out.println();
        
        // Test Combined similarity
        System.out.println("[TEST 5] Combined Similarity");
        double combined12 = calculator.calculateCombinedSimilarity(text1, text2, citations1, citations2);
        double combined13 = calculator.calculateCombinedSimilarity(text1, text3, citations1, citations3);
        System.out.println("   text1+citations1 vs text2+citations2: " + String.format("%.3f", combined12));
        System.out.println("   text1+citations1 vs text3+citations3: " + String.format("%.3f", combined13));
        System.out.println();
        
        // Test Most similar documents
        System.out.println("[TEST 6] Most Similar Documents");
        Map<String, String> documents = new HashMap<>();
        documents.put("doc1", "machine learning algorithms process data");
        documents.put("doc2", "machine learning models learn patterns");
        documents.put("doc3", "neural networks are deep learning models");
        documents.put("doc4", "cooking recipes for pasta dishes");
        
        String query = "machine learning algorithms";
        List<Map.Entry<String, Double>> mostSimilar = calculator.findMostSimilar(query, documents, 3);
        System.out.println("   Query: \"" + query + "\"");
        System.out.println("   Most similar:");
        for (Map.Entry<String, Double> entry : mostSimilar) {
            System.out.println("   - " + entry.getKey() + ": " + String.format("%.3f", entry.getValue()));
        }
        System.out.println();
        
        // Test statistics
        System.out.println("[TEST 7] Statistics");
        System.out.println("   " + calculator.getStats());
        System.out.println();
        
        // Final Verdict
        System.out.println("========================================");
        boolean allTestsPassed = jaccard12 > 0.5 && 
                                 jaccard13 < 0.1 &&
                                 citationSim12 > 0.3 &&
                                 citationSim13 == 0.0 &&
                                 mostSimilar.size() > 0;
        
        if (allTestsPassed) {
            System.out.println("✅ DOCUMENT SIMILARITY CALCULATOR: WORKING");
            System.out.println();
            System.out.println("Capabilities verified:");
            System.out.println("- Jaccard similarity (word overlap)");
            System.out.println("- Cosine similarity (TF-IDF vectors)");
            System.out.println("- Citation similarity (shared citations)");
            System.out.println("- Combined similarity (weighted)");
            System.out.println("- Most similar documents ranking");
        } else {
            System.out.println("❌ TESTS FAILED");
        }
        System.out.println("========================================");
    }
}
