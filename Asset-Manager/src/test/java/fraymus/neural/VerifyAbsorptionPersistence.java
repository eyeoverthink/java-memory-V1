package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

/**
 * Verify that absorbed library persists across restart
 */
public class VerifyAbsorptionPersistence {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("VERIFY ABSORPTION PERSISTENCE");
        System.out.println("========================================");
        System.out.println();
        
        // Initialize AkashicRecord (this will load from disk)
        AkashicRecord akashic = new AkashicRecord();
        LibraryAbsorber absorber = new LibraryAbsorber();
        absorber.wireAkashic(akashic);
        
        // Check loaded state
        System.out.println("[1] LOADED STATE");
        System.out.println("   Akashic blocks: " + akashic.getPersistedBlockCount());
        System.out.println("   Akashic categories: " + akashic.getCategoryCount());
        System.out.println();
        
        // Check absorber status
        System.out.println("[2] ABSORBER STATUS");
        System.out.println(absorber.getStatus());
        System.out.println();
        
        // Query for java.util
        System.out.println("[3] QUERY java.util");
        String queryResult = absorber.query("ArrayList");
        System.out.println("   " + queryResult);
        System.out.println();
        
        // Verdict
        System.out.println("========================================");
        if (queryResult.contains("ArrayList") && queryResult.contains("java.util")) {
            System.out.println("✅ PERSISTENCE VERIFIED");
            System.out.println("   java.util absorption survived restart");
        } else {
            System.out.println("❌ PERSISTENCE FAILED");
            System.out.println("   java.util absorption did not survive restart");
        }
        System.out.println("========================================");
    }
}
