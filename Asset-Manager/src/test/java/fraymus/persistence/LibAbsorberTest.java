package fraymus.persistence;

import fraymus.acquisition.OpenClaw;
import fraymus.translation.Babel;

/**
 * Lib Absorber Test - Knowledge Persistence Layer
 * Demonstrates the full pipeline: Acquire → Transmute → Absorb
 */
public class LibAbsorberTest {
    
    public static void main(String[] args) {
        System.out.println("=== Lib Absorber Knowledge Persistence Test ===\n");
        
        // Test 1: Basic absorption
        testBasicAbsorption();
        
        // Test 2: Full pipeline integration
        testFullPipeline();
        
        // Test 3: Retrieval
        testRetrieval();
        
        // Test 4: Storage statistics
        testStorageStats();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testBasicAbsorption() {
        System.out.println("Test 1: Basic Absorption");
        
        LibAbsorber absorber = new LibAbsorber();
        
        // Create a transmutation result
        Babel.TransmutationResult transmutation = new Babel.TransmutationResult();
        transmutation.originalSource = "test_source";
        transmutation.originalType = "web";
        transmutation.normalizedContent = "This is test content for absorption.";
        transmutation.normalizedType = "text/plain";
        transmutation.languageDetected = "english";
        transmutation.wordCount = 7;
        transmutation.charCount = 36;
        transmutation.success = true;
        
        // Absorb
        LibAbsorber.AbsorptionResult result = absorber.absorb(transmutation);
        
        System.out.println("ID: " + result.id);
        System.out.println("Source: " + result.source);
        System.out.println("Success: " + result.success);
        System.out.println("Raw path: " + result.rawPath);
        System.out.println("Normalized path: " + result.normalizedPath);
        System.out.println("Abstracted path: " + result.abstractedPath);
        System.out.println("Index path: " + result.indexPath);
        System.out.println("Raw size: " + result.rawSize + " bytes");
        System.out.println("Normalized size: " + result.normalizedSize + " bytes");
        System.out.println("Abstracted size: " + result.abstractedSize + " bytes");
        System.out.println();
    }
    
    private static void testFullPipeline() {
        System.out.println("Test 2: Full Pipeline (OpenClaw → Babel → Lib Absorber)");
        
        OpenClaw claw = new OpenClaw();
        Babel babel = new Babel();
        LibAbsorber absorber = new LibAbsorber();
        
        // Step 1: Acquire
        System.out.println("Step 1: Acquire from web...");
        OpenClaw.AcquisitionResult acquisition = claw.acquireWeb("https://example.com");
        System.out.println("  Acquisition success: " + acquisition.success);
        System.out.println("  Size: " + acquisition.size + " bytes");
        
        if (!acquisition.success) {
            System.out.println("  Skipping pipeline due to acquisition failure");
            System.out.println();
            return;
        }
        
        // Step 2: Transmute
        System.out.println("\nStep 2: Transmute with Babel...");
        Babel.TransmutationResult transmutation = babel.transmute(acquisition);
        System.out.println("  Transmutation success: " + transmutation.success);
        System.out.println("  Word count: " + transmutation.wordCount);
        System.out.println("  Language: " + transmutation.languageDetected);
        
        // Step 3: Absorb
        System.out.println("\nStep 3: Absorb with Lib Absorber...");
        LibAbsorber.AbsorptionResult result = absorber.absorb(transmutation);
        System.out.println("  Absorption success: " + result.success);
        System.out.println("  ID: " + result.id);
        System.out.println("  Raw path: " + result.rawPath);
        System.out.println("  Normalized path: " + result.normalizedPath);
        System.out.println("  Abstracted path: " + result.abstractedPath);
        System.out.println("  Index path: " + result.indexPath);
        
        System.out.println("\nPipeline complete: Acquire → Transmute → Absorb");
        System.out.println();
    }
    
    private static void testRetrieval() {
        System.out.println("Test 3: Retrieval");
        
        LibAbsorber absorber = new LibAbsorber();
        
        // List stored IDs
        java.util.List<String> ids = absorber.listStoredIds();
        System.out.println("Stored IDs: " + ids.size());
        
        if (ids.size() > 0) {
            String lastId = ids.get(ids.size() - 1);
            System.out.println("Retrieving last ID: " + lastId);
            
            // Retrieve normalized content
            String normalized = absorber.retrieve(lastId, "normalized");
            if (normalized != null) {
                System.out.println("Normalized content (first 200 chars):");
                System.out.println("  " + normalized.substring(0, Math.min(200, normalized.length())));
            }
            
            // Retrieve abstracted content
            String abstracted = absorber.retrieve(lastId, "abstracted");
            if (abstracted != null) {
                System.out.println("\nAbstracted content (first 200 chars):");
                System.out.println("  " + abstracted.substring(0, Math.min(200, abstracted.length())));
            }
        }
        
        System.out.println();
    }
    
    private static void testStorageStats() {
        System.out.println("Test 4: Storage Statistics");
        
        LibAbsorber absorber = new LibAbsorber();
        
        java.util.Map<String, Integer> stats = absorber.getStorageStats();
        
        System.out.println("Raw files: " + stats.get("raw_count"));
        System.out.println("Normalized files: " + stats.get("normalized_count"));
        System.out.println("Abstracted files: " + stats.get("abstracted_count"));
        System.out.println("Index files: " + stats.get("index_count"));
        System.out.println("Total files: " + 
            (stats.get("raw_count") + stats.get("normalized_count") + 
             stats.get("abstracted_count") + stats.get("index_count")));
        
        System.out.println();
    }
}
