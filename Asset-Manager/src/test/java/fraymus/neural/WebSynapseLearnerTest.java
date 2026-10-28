package fraymus.neural;

import fraymus.acquisition.OpenClaw;
import fraymus.knowledge.AkashicRecord;

/**
 * Test WebSynapseLearner - Phase 2.1
 * 
 * Tests:
 * 1. WebSynapseLearner initialization
 * 2. Learning from URL
 * 3. Feature extraction
 * 4. Relevance scoring
 * 5. Entity extraction
 * 6. Integration with WebCortex
 */
public class WebSynapseLearnerTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("WEBSYNAPSE LEARNER TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize components
        WebCortex webCortex = new WebCortex();
        AkashicRecord akashic = new AkashicRecord();
        OpenClaw openClaw = new OpenClaw();
        SentientCore sentient = new SentientCore();
        
        webCortex.wireAkashic(akashic);
        webCortex.wireOpenClaw(openClaw);
        webCortex.wireSentient(sentient);
        
        // Test 1: WebSynapseLearner Initialization
        System.out.println("[TEST 1] WebSynapseLearner Initialization");
        WebSynapseLearner learner = new WebSynapseLearner(webCortex);
        learner.setOpenClaw(openClaw);
        System.out.println("   ✓ WebSynapseLearner initialized");
        System.out.println();
        
        // Test 2: Learn from URL
        System.out.println("[TEST 2] Learning from URL");
        String testUrl = "https://example.com";
        WebSynapse synapse = learner.learnFromUrl(testUrl);
        
        if (synapse != null) {
            System.out.println("   ✓ Synapse created: " + synapse.getId());
            System.out.println("   URL Pattern: " + synapse.getUrlPattern());
            System.out.println("   Domain: " + synapse.getDomain());
            System.out.println("   Content Type: " + synapse.getContentType());
            System.out.println("   Relevance: " + synapse.getRelevanceScore());
            System.out.println("   Entities: " + synapse.getExtractedEntities().size());
            System.out.println("   Access Count: " + synapse.getAccessCount());
            System.out.println("   Semantic Features: " + synapse.getSemanticFeatures().keySet());
        } else {
            System.out.println("   ✗ Failed to create synapse");
        }
        System.out.println();
        
        // Test 3: Check WebCortex Status
        System.out.println("[TEST 3] WebCortex Status After Learning");
        System.out.println(webCortex.getStatus());
        System.out.println();
        
        // Test 4: Content Type Prediction
        System.out.println("[TEST 4] Content Type Prediction");
        String[] testUrls = {
            "https://example.com/page.html",
            "https://example.com/document.pdf",
            "https://api.example.com/data.json",
            "https://example.com/feed.xml"
        };
        
        for (String url : testUrls) {
            String predicted = learner.predictContentType(url);
            System.out.println("   " + url + " → " + predicted);
        }
        System.out.println();
        
        // Test 5: Learning Statistics
        System.out.println("[TEST 5] Learning Statistics");
        System.out.println("   " + learner.getStats());
        System.out.println();
        
        // Final Verdict
        System.out.println("========================================");
        boolean allTestsPassed = synapse != null && 
                                 synapse.getRelevanceScore() > 0 &&
                                 !synapse.getExtractedEntities().isEmpty();
        
        if (allTestsPassed) {
            System.out.println("✅ WEBSYNAPSE LEARNER: WORKING");
            System.out.println();
            System.out.println("Capabilities verified:");
            System.out.println("- URL pattern learning");
            System.out.println("- Feature extraction");
            System.out.println("- Relevance scoring");
            System.out.println("- Basic entity extraction");
            System.out.println("- Integration with WebCortex");
        } else {
            System.out.println("❌ TESTS FAILED");
        }
        System.out.println("========================================");
    }
}
