package fraymus.acquisition;

import fraymus.translation.Babel;
import fraymus.persistence.LibAbsorber;
import fraymus.knowledge.AkashicRecord;

/**
 * Test full web scraping pipeline:
 * Acquire (OpenClaw) → Transmute (Babel) → Absorb (LibAbsorber) → Persist (Akashic)
 */
public class WebScrapingPipelineTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("WEB SCRAPING PIPELINE TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize pipeline components
        OpenClaw openClaw = new OpenClaw();
        Babel babel = new Babel();
        AkashicRecord akashic = new AkashicRecord();
        LibAbsorber libAbsorber = new LibAbsorber();
        
        System.out.println("[1] COMPONENTS INITIALIZED");
        System.out.println("   OpenClaw: " + (openClaw != null ? "✓" : "✗"));
        System.out.println("   Babel: " + (babel != null ? "✓" : "✗"));
        System.out.println("   AkashicRecord: " + (akashic != null ? "✓" : "✗"));
        System.out.println("   LibAbsorber: " + (libAbsorber != null ? "✓" : "✗"));
        System.out.println();
        
        // Step 1: Acquire from web
        System.out.println("[2] ACQUIRING FROM WEB");
        String testUrl = "https://example.com";
        OpenClaw.AcquisitionResult acquisition = openClaw.acquireWeb(testUrl);
        System.out.println("   URL: " + testUrl);
        System.out.println("   Success: " + acquisition.success);
        System.out.println("   Content Type: " + acquisition.contentType);
        System.out.println("   Size: " + acquisition.size + " bytes");
        System.out.println("   Links extracted: " + acquisition.links.size());
        System.out.println();
        
        if (!acquisition.success) {
            System.out.println("❌ ACQUISITION FAILED: " + acquisition.error);
            return;
        }
        
        // Step 2: Transmute
        System.out.println("[3] TRANSMUTING CONTENT");
        Babel.TransmutationResult transmutation = babel.transmute(acquisition);
        System.out.println("   Original type: " + transmutation.originalType);
        System.out.println("   Normalized type: " + transmutation.normalizedType);
        System.out.println("   Language detected: " + transmutation.languageDetected);
        System.out.println("   Word count: " + transmutation.wordCount);
        System.out.println("   Links extracted: " + transmutation.extractedLinks.size());
        System.out.println();
        
        // Step 3: Absorb
        System.out.println("[4] ABSORBING TO PERSISTENCE");
        LibAbsorber.AbsorptionResult absorption = libAbsorber.absorb(transmutation);
        System.out.println("   Success: " + absorption.success);
        System.out.println("   ID: " + absorption.id);
        System.out.println("   Raw size: " + absorption.rawSize + " bytes");
        System.out.println("   Normalized size: " + absorption.normalizedSize + " bytes");
        System.out.println("   Abstracted size: " + absorption.abstractedSize + " bytes");
        System.out.println();
        
        // Step 4: Store in AkashicRecord
        System.out.println("[5] STORING TO AKASHIC RECORD");
        String blockId = akashic.addBlock("WEB_SCRAPED", 
            "URL: " + testUrl + "\n" +
            "Words: " + transmutation.wordCount + "\n" +
            "Links: " + transmutation.extractedLinks.size() + "\n" +
            "Summary: " + transmutation.normalizedContent.substring(0, Math.min(200, transmutation.normalizedContent.length()))
        );
        System.out.println("   Block ID: " + blockId);
        System.out.println("   Total blocks: " + akashic.getPersistedBlockCount());
        System.out.println();
        
        // Step 5: Save to disk
        System.out.println("[6] SAVING TO DISK");
        akashic.saveAll();
        System.out.println("   Saved: " + akashic.getPersistedBlockCount() + " blocks");
        System.out.println();
        
        // Step 6: Verify retrieval
        System.out.println("[7] VERIFYING RETRIEVAL");
        String retrievedRaw = libAbsorber.retrieve(absorption.id, "raw");
        String retrievedNormalized = libAbsorber.retrieve(absorption.id, "normalized");
        String retrievedAbstracted = libAbsorber.retrieve(absorption.id, "abstracted");
        String retrievedIndex = libAbsorber.retrieve(absorption.id, "index");
        
        System.out.println("   Raw retrieved: " + (retrievedRaw != null ? "✓" : "✗"));
        System.out.println("   Normalized retrieved: " + (retrievedNormalized != null ? "✓" : "✗"));
        System.out.println("   Abstracted retrieved: " + (retrievedAbstracted != null ? "✓" : "✗"));
        System.out.println("   Index retrieved: " + (retrievedIndex != null ? "✓" : "✗"));
        System.out.println();
        
        // Verdict
        System.out.println("========================================");
        boolean pipelineWorks = acquisition.success && 
                               absorption.success && 
                               retrievedRaw != null &&
                               retrievedNormalized != null;
        
        if (pipelineWorks) {
            System.out.println("✅ WEB SCRAPING PIPELINE WORKING");
            System.out.println("   Acquire → Transmute → Absorb → Persist → Retrieve");
        } else {
            System.out.println("❌ PIPELINE HAS ISSUES");
        }
        System.out.println("========================================");
    }
}
