package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

/**
 * Test absorbing a real library and verifying it persists across restart
 */
public class LibraryAbsorptionPersistenceTestMain {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("LIBRARY ABSORPTION PERSISTENCE TEST");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize components
        AkashicRecord akashic = new AkashicRecord();
        LibraryAbsorber absorber = new LibraryAbsorber();
        absorber.wireAkashic(akashic);
        
        // Check initial state
        System.out.println("[1] INITIAL STATE");
        System.out.println("   Akashic blocks: " + akashic.getPersistedBlockCount());
        System.out.println("   Akashic categories: " + akashic.getCategoryCount());
        System.out.println();
        
        // Absorb java.util (a real standard library)
        System.out.println("[2] ABSORBING java.util");
        String result = absorber.absorbPackage("java.util");
        System.out.println("   Result: " + result);
        System.out.println();
        
        // Check post-absorption state
        System.out.println("[3] POST-ABSORPTION STATE");
        System.out.println("   Akashic blocks: " + akashic.getPersistedBlockCount());
        System.out.println("   Akashic categories: " + akashic.getCategoryCount());
        System.out.println();
        
        // Query absorbed knowledge
        System.out.println("[4] QUERYING ABSORBED KNOWLEDGE");
        String queryResult = absorber.query("ArrayList");
        System.out.println("   Query 'ArrayList': " + queryResult);
        System.out.println();
        
        // Show status
        System.out.println("[5] ABSORBER STATUS");
        System.out.println(absorber.getStatus());
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("TEST COMPLETE");
        System.out.println("========================================");
        System.out.println();
        System.out.println("NEXT STEPS:");
        System.out.println("1. Restart the system");
        System.out.println("2. Run this test again to verify persistence");
        System.out.println("3. Check if java.util is still registered");
    }
}
