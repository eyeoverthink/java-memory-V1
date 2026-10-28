package fraymus.neural;

import java.util.*;

/**
 * Test CitationNetworkExtractor - Phase 3.2
 * 
 * Tests:
 * 1. Document addition
 * 2. Citation graph building
 * 3. Citation count tracking
 * 4. Co-citation analysis
 * 5. Bibliographic coupling
 * 6. Most cited documents
 */
public class CitationNetworkExtractorTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("CITATION NETWORK EXTRACTOR TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize
        System.out.println("[TEST 1] Initialization");
        CitationNetworkExtractor extractor = new CitationNetworkExtractor();
        System.out.println("   ✓ CitationNetworkExtractor initialized");
        System.out.println();
        
        // Add documents with citations
        System.out.println("[TEST 2] Add Documents");
        extractor.addDocument("doc1", Arrays.asList("refA", "refB", "refC"));
        extractor.addDocument("doc2", Arrays.asList("refA", "refD"));
        extractor.addDocument("doc3", Arrays.asList("refB", "refC", "refE"));
        extractor.addDocument("doc4", Arrays.asList("refA", "refB"));
        System.out.println("   ✓ 4 documents added with citations");
        System.out.println();
        
        // Test citation graph
        System.out.println("[TEST 3] Citation Graph");
        System.out.println("   doc1 cites: " + extractor.getCites("doc1"));
        System.out.println("   doc2 cites: " + extractor.getCites("doc2"));
        System.out.println("   doc3 cites: " + extractor.getCites("doc3"));
        System.out.println("   doc4 cites: " + extractor.getCites("doc4"));
        System.out.println();
        
        // Test citation counts
        System.out.println("[TEST 4] Citation Counts");
        System.out.println("   refA cited by: " + extractor.getCitationCount("refA") + " documents");
        System.out.println("   refB cited by: " + extractor.getCitationCount("refB") + " documents");
        System.out.println("   refC cited by: " + extractor.getCitationCount("refC") + " documents");
        System.out.println("   refD cited by: " + extractor.getCitationCount("refD") + " documents");
        System.out.println("   refE cited by: " + extractor.getCitationCount("refE") + " documents");
        System.out.println();
        
        // Test cited by
        System.out.println("[TEST 5] Cited By");
        System.out.println("   refA cited by: " + extractor.getCitedBy("refA"));
        System.out.println("   refB cited by: " + extractor.getCitedBy("refB"));
        System.out.println();
        
        // Test co-citation
        System.out.println("[TEST 6] Co-Citation Analysis");
        System.out.println("   refA co-cited with: " + extractor.getCoCited("refA"));
        System.out.println("   refB co-cited with: " + extractor.getCoCited("refB"));
        System.out.println("   refC co-cited with: " + extractor.getCoCited("refC"));
        System.out.println();
        
        // Test bibliographic coupling
        System.out.println("[TEST 7] Bibliographic Coupling");
        System.out.println("   doc1 and doc2 share: " + extractor.getBibliographicCoupling("doc1", "doc2") + " citations");
        System.out.println("   doc1 and doc3 share: " + extractor.getBibliographicCoupling("doc1", "doc3") + " citations");
        System.out.println("   doc1 and doc4 share: " + extractor.getBibliographicCoupling("doc1", "doc4") + " citations");
        System.out.println();
        
        // Test most cited
        System.out.println("[TEST 8] Most Cited");
        List<Map.Entry<String, Integer>> mostCited = extractor.getMostCited(5);
        System.out.println("   Top " + mostCited.size() + " most cited:");
        for (Map.Entry<String, Integer> entry : mostCited) {
            System.out.println("   - " + entry.getKey() + ": " + entry.getValue() + " citations");
        }
        System.out.println();
        
        // Test statistics
        System.out.println("[TEST 9] Statistics");
        System.out.println("   " + extractor.getStats());
        System.out.println();
        
        // Final Verdict
        System.out.println("========================================");
        boolean allTestsPassed = extractor.getCites("doc1").size() == 3 &&
                                 extractor.getCitationCount("refA") == 3 &&
                                 extractor.getCitedBy("refA").size() == 3 &&
                                 extractor.getCoCited("refA").size() > 0 &&
                                 extractor.getBibliographicCoupling("doc1", "doc4") == 2 &&
                                 mostCited.size() > 0;
        
        if (allTestsPassed) {
            System.out.println("✅ CITATION NETWORK EXTRACTOR: WORKING");
            System.out.println();
            System.out.println("Capabilities verified:");
            System.out.println("- Document addition with citations");
            System.out.println("- Citation graph building");
            System.out.println("- Citation count tracking");
            System.out.println("- Cited by queries");
            System.out.println("- Co-citation analysis");
            System.out.println("- Bibliographic coupling");
            System.out.println("- Most cited documents ranking");
        } else {
            System.out.println("❌ TESTS FAILED");
        }
        System.out.println("========================================");
    }
}
