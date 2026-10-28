package fraymus.field;

/**
 * Field Engine Test and Demo
 * Demonstrates continuous-state field dynamics with C-ISA operations
 */
public class FieldEngineTest {
    
    public static void main(String[] args) {
        System.out.println("=== Field Engine Test ===\n");
        
        // Test 1: Basic C-ISA operations
        testBasicOperations();
        
        // Test 2: Attractor detection
        testAttractorDetection();
        
        // Test 3: Coherence
        testCoherence();
        
        // Test 4: Homeostasis
        testHomeostasis();
        
        // Test 5: Full integration loop
        testIntegrationLoop();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testBasicOperations() {
        System.out.println("Test 1: Basic C-ISA Operations");
        
        FieldEngine engine = new FieldEngine(8, 0.01f, 0.1f, 0.5f);
        
        // Initialize with pattern
        float[] pattern = {0.5f, 0.3f, -0.2f, 0.8f, 0.1f, -0.5f, 0.7f, 0.0f};
        engine.setState(pattern);
        
        System.out.print("Initial: ");
        engine.printState();
        
        // EXC operation
        float[] excitation = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
        engine.excite(excitation, 0.5f);
        System.out.print("After EXC: ");
        engine.printState();
        
        // INH operation
        engine.inhibit(excitation, 0.3f);
        System.out.print("After INH: ");
        engine.printState();
        
        // SUP operation
        float[] other = {0.1f, -0.1f, 0.3f, -0.3f, 0.5f, -0.5f, 0.7f, -0.7f};
        engine.superpose(other, 0.7f);
        System.out.print("After SUP: ");
        engine.printState();
        
        // CLP operation
        int[] discrete = engine.collapse();
        System.out.print("After CLP: ");
        for (int i = 0; i < Math.min(8, discrete.length); i++) {
            System.out.print(discrete[i] + " ");
        }
        System.out.println();
        
        System.out.println();
    }
    
    private static void testAttractorDetection() {
        System.out.println("Test 2: Attractor Detection");
        
        FieldEngine engine = new FieldEngine(8, 0.01f, 0.1f, 0.5f);
        
        // Define attractors (concept patterns)
        float[][] attractors = {
            {0.8f, 0.7f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f}, // "APPLE"
            {0.1f, 0.1f, 0.8f, 0.7f, 0.1f, 0.1f, 0.1f, 0.1f}, // "BANANA"
            {0.1f, 0.1f, 0.1f, 0.1f, 0.8f, 0.7f, 0.1f, 0.1f}  // "CHERRY"
        };
        
        engine.setAttractorLabel(0, "APPLE");
        engine.setAttractorLabel(1, "BANANA");
        engine.setAttractorLabel(2, "CHERRY");
        
        // Test state close to APPLE
        float[] testState = {0.75f, 0.65f, 0.15f, 0.25f, 0.1f, 0.1f, 0.1f, 0.1f};
        engine.setState(testState);
        
        System.out.print("Test state: ");
        engine.printState();
        
        int nearest = engine.findNearestAttractor(attractors);
        System.out.println("Nearest attractor: " + engine.getAttractorLabel(nearest));
        System.out.println("Energy: " + engine.computeAttractorEnergy(attractors[nearest]));
        
        System.out.println();
    }
    
    private static void testCoherence() {
        System.out.println("Test 3: Coherence Calculation");
        
        FieldEngine engine = new FieldEngine(8, 0.01f, 0.1f, 0.5f);
        
        // Coherent state (smooth transitions)
        float[] coherent = {0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f};
        engine.setState(coherent);
        float coherentEnergy = engine.computeCoherence(0.5f);
        System.out.print("Coherent state: ");
        engine.printState();
        System.out.println("Coherence energy: " + coherentEnergy);
        
        // Incoherent state (chaotic)
        float[] incoherent = {0.9f, -0.8f, 0.7f, -0.6f, 0.5f, -0.4f, 0.3f, -0.2f};
        engine.setState(incoherent);
        float incoherentEnergy = engine.computeCoherence(0.5f);
        System.out.print("Incoherent state: ");
        engine.printState();
        System.out.println("Coherence energy: " + incoherentEnergy);
        
        System.out.println("Lower energy = more coherent");
        System.out.println();
    }
    
    private static void testHomeostasis() {
        System.out.println("Test 4: Homeostasis (Target Activity Normalization)");
        
        FieldEngine engine = new FieldEngine(8, 0.01f, 0.1f, 0.5f);
        
        // Over-excited state
        float[] excited = {0.9f, 0.9f, 0.9f, 0.9f, 0.9f, 0.9f, 0.9f, 0.9f};
        engine.setState(excited);
        
        System.out.print("Over-excited: ");
        engine.printState();
        
        // Apply homeostasis to normalize to target activity
        for (int i = 0; i < 10; i++) {
            engine.homeostasis(0.3f, 0.1f);
        }
        
        System.out.print("After homeostasis: ");
        engine.printState();
        
        System.out.println();
    }
    
    private static void testIntegrationLoop() {
        System.out.println("Test 5: Full Integration Loop");
        
        FieldEngine engine = new FieldEngine(8, 0.01f, 0.05f, 0.5f);
        
        // Define attractors
        float[][] attractors = {
            {0.8f, 0.7f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f},
            {0.1f, 0.1f, 0.8f, 0.7f, 0.1f, 0.1f, 0.1f, 0.1f}
        };
        
        engine.setAttractorLabel(0, "CONCEPT_A");
        engine.setAttractorLabel(1, "CONCEPT_B");
        
        // Start from random state
        float[] initial = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f};
        engine.setState(initial);
        
        System.out.println("Initial state:");
        engine.printState();
        
        // Simulate convergence to attractor
        System.out.println("\nSimulating convergence...");
        for (int step = 0; step < 50; step++) {
            // Apply attractor pull
            int nearest = engine.findNearestAttractor(attractors);
            engine.attractorPull(0.1f, attractors[nearest]);
            
            // Integrate
            engine.integrate();
            
            // Apply homeostasis
            if (step % 10 == 0) {
                engine.homeostasis(0.4f, 0.05f);
            }
            
            if (step % 10 == 0) {
                System.out.print("Step " + step + ": ");
                engine.printState();
                System.out.println("  Nearest: " + engine.getAttractorLabel(nearest));
            }
        }
        
        System.out.println("\nFinal state:");
        engine.printState();
        
        System.out.println();
    }
}
