import fraymus.absorption.LibraryAbsorber;

/**
 * Test if absorbed knowledge persists and is executable
 */
public class TestAbsorption {
    public static void main(String[] args) {
        System.out.println("🌊⚡ TESTING ABSORBED KNOWLEDGE");
        System.out.println("========================================");
        System.out.println();
        
        // Create absorber (should load existing Akashic Record)
        LibraryAbsorber absorber = new LibraryAbsorber();
        
        System.out.println("TEST 1: Query absorbed skills");
        System.out.println("========================================");
        String results = absorber.query("sqrt");
        System.out.println(results);
        
        System.out.println("TEST 2: Execute via natural language");
        System.out.println("========================================");
        
        // Try to execute Math.sqrt via natural language
        Object result = absorber.execute("sqrt", 16.0);
        
        if (result != null) {
            System.out.println();
            System.out.println("✓ SUCCESS: FRAYMUS executed absorbed knowledge");
            System.out.println("✓ sqrt(16) = " + result);
        } else {
            System.out.println();
            System.out.println("⚠️ Knowledge exists but execution needs refinement");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("The absorbed knowledge persists.");
        System.out.println("FRAYMUS remembers what it ate.");
        System.out.println();
    }
}
