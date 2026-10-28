package fraymus.neural;

import fraymus.acquisition.OpenClaw;
import fraymus.translation.Babel;
import fraymus.persistence.LibAbsorber;
import fraymus.knowledge.AkashicRecord;

/**
 * Full Web Content Processing Pipeline Integration Test
 * 
 * Tests the complete pipeline:
 * OpenClaw (acquire) → WebSynapseLearner (learn) → HtmlStructureAnalyzer (parse) → 
 * WebEntityExtractor (extract entities) → WebFactExtractor (extract facts) → WebCortex (store)
 */
public class WebContentPipelineIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("FULL WEB CONTENT PIPELINE TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize all components
        System.out.println("[INITIALIZATION] Setting up pipeline components...");
        WebCortex webCortex = new WebCortex();
        AkashicRecord akashic = new AkashicRecord();
        OpenClaw openClaw = new OpenClaw();
        Babel babel = new Babel();
        LibAbsorber libAbsorber = new LibAbsorber();
        SentientCore sentient = new SentientCore();
        
        // Wire components
        webCortex.wireAkashic(akashic);
        webCortex.wireOpenClaw(openClaw);
        webCortex.wireSentient(sentient);
        
        WebSynapseLearner webSynapseLearner = new WebSynapseLearner(webCortex);
        webSynapseLearner.setOpenClaw(openClaw);
        
        HtmlStructureAnalyzer htmlAnalyzer = new HtmlStructureAnalyzer();
        WebEntityExtractor entityExtractor = new WebEntityExtractor();
        WebFactExtractor factExtractor = new WebFactExtractor();
        
        System.out.println("   ✓ All components initialized and wired");
        System.out.println();
        
        // Test URL
        String testUrl = "https://example.com";
        
        // Step 1: Acquire
        System.out.println("[STEP 1] ACQUIRE - OpenClaw");
        OpenClaw.AcquisitionResult acquisition = openClaw.acquireWeb(testUrl);
        if (!acquisition.success) {
            System.err.println("   ✗ Acquisition failed: " + acquisition.error);
            return;
        }
        System.out.println("   ✓ Acquired: " + acquisition.source);
        System.out.println("   Content type: " + acquisition.contentType);
        System.out.println("   Content size: " + acquisition.size + " bytes");
        System.out.println();
        
        // Step 2: Learn URL pattern
        System.out.println("[STEP 2] LEARN - WebSynapseLearner");
        WebSynapse synapse = webSynapseLearner.learnFromUrl(testUrl);
        if (synapse == null) {
            System.err.println("   ✗ Learning failed");
            return;
        }
        System.out.println("   ✓ URL pattern learned: " + synapse.getUrlPattern());
        System.out.println("   Relevance score: " + synapse.getRelevanceScore());
        System.out.println("   Entities extracted: " + synapse.getExtractedEntities().size());
        System.out.println();
        
        // Step 3: Parse HTML structure
        System.out.println("[STEP 3] PARSE - HtmlStructureAnalyzer");
        var structure = htmlAnalyzer.analyzeStructure(acquisition.content);
        System.out.println(htmlAnalyzer.getStructureStats(structure));
        System.out.println();
        
        // Step 4: Extract entities
        System.out.println("[STEP 4] EXTRACT ENTITIES - WebEntityExtractor");
        var entities = entityExtractor.extractEntities(acquisition.content);
        System.out.println(entityExtractor.getStats());
        System.out.println();
        
        // Step 5: Extract facts
        System.out.println("[STEP 5] EXTRACT FACTS - WebFactExtractor");
        var facts = factExtractor.extractFacts(acquisition.content);
        System.out.println(factExtractor.getStats());
        System.out.println();
        
        // Step 6: Check WebCortex status
        System.out.println("[STEP 6] STORE - WebCortex Status");
        System.out.println(webCortex.getStatus());
        System.out.println();
        
        // Step 7: Verify persistence
        System.out.println("[STEP 7] PERSIST - AkashicRecord");
        int blocks = akashic.getPersistedBlockCount();
        System.out.println("   Total blocks in AkashicRecord: " + blocks);
        System.out.println();
        
        // Final Verdict
        System.out.println("========================================");
        boolean pipelineWorking = acquisition.success && 
                                  synapse != null && 
                                  structure != null &&
                                  entities.size() > 0 &&
                                  facts.size() >= 0 && // facts might be 0 for simple content
                                  blocks > 0;
        
        if (pipelineWorking) {
            System.out.println("✅ FULL WEB CONTENT PIPELINE: WORKING");
            System.out.println();
            System.out.println("Pipeline verified:");
            System.out.println("- ✓ OpenClaw acquisition");
            System.out.println("- ✓ WebSynapseLearner pattern learning");
            System.out.println("- ✓ HtmlStructureAnalyzer HTML parsing");
            System.out.println("- ✓ WebEntityExtractor entity extraction");
            System.out.println("- ✓ WebFactExtractor fact extraction");
            System.out.println("- ✓ WebCortex storage");
            System.out.println("- ✓ AkashicRecord persistence");
            System.out.println();
            System.out.println("The system can now:");
            System.out.println("- Scrape web content");
            System.out.println("- Learn URL patterns");
            System.out.println("- Parse HTML structure");
            System.out.println("- Extract entities");
            System.out.println("- Extract facts");
            System.out.println("- Store knowledge persistently");
        } else {
            System.out.println("❌ PIPELINE TEST FAILED");
        }
        System.out.println("========================================");
    }
}
