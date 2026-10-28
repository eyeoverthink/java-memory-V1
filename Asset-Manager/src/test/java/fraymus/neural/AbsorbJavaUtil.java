package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

/**
 * Absorb java.util and trigger save
 */
public class AbsorbJavaUtil {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("ABSORB java.util");
        System.out.println("========================================");
        System.out.println();
        
        AkashicRecord akashic = new AkashicRecord();
        LibraryAbsorber absorber = new LibraryAbsorber();
        absorber.wireAkashic(akashic);
        
        System.out.println("[1] BEFORE ABSORPTION");
        System.out.println("   Libraries: " + absorber.getStatus());
        System.out.println();
        
        System.out.println("[2] ABSORBING java.util");
        String result = absorber.absorbPackage("java.util");
        System.out.println("   " + result);
        System.out.println();
        
        System.out.println("[3] AFTER ABSORPTION");
        System.out.println("   " + absorber.getStatus());
        System.out.println();
        
        System.out.println("[4] SAVING TO DISK");
        akashic.saveAll();
        System.out.println("   Saved: " + akashic.getPersistedBlockCount() + " blocks");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("ABSORPTION COMPLETE");
        System.out.println("Restart system to verify persistence");
        System.out.println("========================================");
    }
}
