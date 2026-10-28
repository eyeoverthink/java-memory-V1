package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

/**
 * Verify LibraryAbsorber restoreFromAkashic() works
 */
public class VerifyRestoreWorks {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("VERIFY RESTORE MECHANISM");
        System.out.println("========================================");
        System.out.println();
        
        // Fresh AkashicRecord (loads from disk)
        AkashicRecord akashic = new AkashicRecord();
        
        // Fresh LibraryAbsorber (should restore from Akashic)
        LibraryAbsorber absorber = new LibraryAbsorber();
        absorber.wireAkashic(akashic);
        
        System.out.println("[1] FRESH INSTANCE STATUS");
        System.out.println(absorber.getStatus());
        System.out.println();
        
        System.out.println("[2] QUERY java.util");
        String queryResult = absorber.query("ArrayList");
        System.out.println("   " + queryResult);
        System.out.println();
        
        System.out.println("[3] QUERY fraymus.neural");
        String queryResult2 = absorber.query("SentientCore");
        System.out.println("   " + queryResult2);
        System.out.println();
        
        // Verdict
        System.out.println("========================================");
        boolean javaUtilFound = queryResult.contains("ArrayList") && queryResult.contains("java.util");
        boolean fraymusFound = queryResult2.contains("SentientCore");
        
        if (javaUtilFound && fraymusFound) {
            System.out.println("✅ RESTORE MECHANISM WORKING");
            System.out.println("   Both libraries restored from AkashicRecord");
        } else if (javaUtilFound) {
            System.out.println("⚠️ PARTIAL RESTORE");
            System.out.println("   java.util restored, fraymus.neural not found");
        } else if (fraymusFound) {
            System.out.println("⚠️ PARTIAL RESTORE");
            System.out.println("   fraymus.neural restored, java.util not found");
        } else {
            System.out.println("❌ RESTORE FAILED");
            System.out.println("   Neither library restored");
        }
        System.out.println("========================================");
    }
}
