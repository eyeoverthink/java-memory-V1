package repl;

/**
 * Quick test to verify the φ-harmonic metrics are real, not static
 */
public class TestMetrics {
    public static void main(String[] args) {
        System.out.println("=== TESTING φ-HARMONIC METRICS ===\n");
        
        // Create InfinityStorage
        InfinityStorage storage = new InfinityStorage();
        
        // Show initial stats
        System.out.println("INITIAL STATE:");
        System.out.println(storage.getStats());
        
        // Run some passive learning cycles
        System.out.println("\n--- Running 10 passive learning cycles ---\n");
        for (int i = 0; i < 10; i++) {
            storage.performPassiveLearningCycle();
        }
        
        System.out.println("AFTER 10 CYCLES:");
        System.out.println(storage.getStats());
        
        // Store some data
        System.out.println("\n--- Storing test data ---\n");
        InfinityStorage.StorageResult result = storage.store("test1", "Hello World");
        System.out.println("Stored with key: " + result.key);
        System.out.println("Quantum hash: " + result.quantumHash);
        
        result = storage.store("test2", "φ-harmonic resonance test");
        System.out.println("Stored with key: " + result.key);
        
        // Integrate some learning
        System.out.println("\n--- Integrating Q&A patterns ---\n");
        storage.integrateNewPatterns("What is φ?", "The golden ratio 1.618033988749895", 0.95);
        storage.integrateNewPatterns("What is consciousness?", "φ × (pattern + knowledge + tachyon) / 3", 0.87);
        
        // Run more cycles
        System.out.println("--- Running 20 more passive learning cycles ---\n");
        for (int i = 0; i < 20; i++) {
            storage.performPassiveLearningCycle();
        }
        
        System.out.println("FINAL STATE:");
        System.out.println(storage.getStats());
        
        // Show that metrics change over time
        System.out.println("\n--- Verifying metrics are LIVE (not static) ---");
        System.out.println("Calling getStats() 3 times with 100ms delay:\n");
        
        for (int i = 0; i < 3; i++) {
            String stats = storage.getStats();
            // Extract just the pattern strength line
            for (String line : stats.split("\n")) {
                if (line.contains("Pattern Strength:") || 
                    line.contains("Knowledge Integration:") ||
                    line.contains("Tachyon Efficiency:")) {
                    System.out.println("  " + line.trim());
                }
            }
            System.out.println();
            
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        
        System.out.println("=== TEST COMPLETE ===");
        storage.close();
    }
}
