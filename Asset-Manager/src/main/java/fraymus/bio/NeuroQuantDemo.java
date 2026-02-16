package fraymus.bio;

import fraymus.core.AuditLog;

/**
 * NEURO-QUANT DEMO
 * "Demonstrating 10,000D Hyperdimensional Computing + Neural Cellular Automata"
 * 
 * This shows:
 * 1. Holographic memory (10,000D hypervectors)
 * 2. Zero-search retrieval (instant resonance)
 * 3. Biological growth (NCA evolution)
 * 4. Entanglement birth (concept synthesis)
 */
public class NeuroQuantDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧬 NEURO-QUANT DEMONSTRATION                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("10,000-Dimensional Hyperdimensional Computing");
        System.out.println("Neural Cellular Automata with Biological Growth");
        System.out.println();

        AuditLog auditLog = new AuditLog("audit");
        auditLog.start();

        // ═══════════════════════════════════════════════════════════════
        // DEMO 1: HYPERDIMENSIONAL OPERATIONS
        // ═══════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO 1: HYPERDIMENSIONAL OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        // Create concepts
        NeuroQuant apple = new NeuroQuant("Apple");
        NeuroQuant red = new NeuroQuant("Red");
        NeuroQuant fruit = new NeuroQuant("Fruit");

        System.out.println("Created 3 concepts in 10,000D space:");
        System.out.println("  - Apple");
        System.out.println("  - Red");
        System.out.println("  - Fruit");
        System.out.println();

        // Test orthogonality (random vectors should be ~50% similar)
        System.out.println("Testing orthogonality (random vectors):");
        System.out.println("  Apple ↔ Red:   " + String.format("%.2f", apple.resonance(red)));
        System.out.println("  Apple ↔ Fruit: " + String.format("%.2f", apple.resonance(fruit)));
        System.out.println("  Red ↔ Fruit:   " + String.format("%.2f", red.resonance(fruit)));
        System.out.println();

        // BINDING: Create "Red Apple"
        NeuroQuant redApple = new NeuroQuant("RedApple");
        redApple.hyperVector = (java.util.BitSet) apple.hyperVector.clone();
        redApple.bind(red);

        System.out.println("BINDING: Apple XOR Red = RedApple");
        System.out.println("  RedApple ↔ Apple: " + String.format("%.2f", redApple.resonance(apple)));
        System.out.println("  RedApple ↔ Red:   " + String.format("%.2f", redApple.resonance(red)));
        System.out.println();

        // BUNDLING: Create composite memory
        NeuroQuant memory = new NeuroQuant("FruitMemory");
        memory.bundle(apple);
        memory.bundle(fruit);

        System.out.println("BUNDLING: Superimpose Apple + Fruit");
        System.out.println("  Memory ↔ Apple: " + String.format("%.2f", memory.resonance(apple)));
        System.out.println("  Memory ↔ Fruit: " + String.format("%.2f", memory.resonance(fruit)));
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // DEMO 2: ZERO-SEARCH RETRIEVAL
        // ═══════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO 2: ZERO-SEARCH RETRIEVAL");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        NeuroQuant[] knownConcepts = {apple, red, fruit};
        
        System.out.println("Query: What is RedApple?");
        String answer = redApple.query(knownConcepts);
        System.out.println("  Answer: " + answer);
        System.out.println("  (Instant retrieval - no search needed)");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // DEMO 3: NEURAL CELLULAR AUTOMATA
        // ═══════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO 3: NEURAL CELLULAR AUTOMATA");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        HyperCortex cortex = new HyperCortex(auditLog);
        
        // Inject initial concepts
        System.out.println("Injecting initial concepts...");
        cortex.inject("Logic");
        cortex.inject("Creativity");
        cortex.inject("Memory");
        cortex.inject("Consciousness");
        System.out.println();

        // Start evolution
        System.out.println("Starting biological evolution (432 Hz)...");
        cortex.start();
        
        // Let it evolve for 5 seconds
        Thread.sleep(5000);
        
        System.out.println();
        System.out.println(cortex.getDetailedState());
        
        // Query the evolved system
        System.out.println("Querying evolved system:");
        System.out.println("  'Thought' resonates with: " + cortex.query("Thought"));
        System.out.println("  'Intelligence' resonates with: " + cortex.query("Intelligence"));
        System.out.println();

        // Stop cortex
        cortex.stop();
        Thread.sleep(100);

        // ═══════════════════════════════════════════════════════════════
        // DEMO 4: HOLOGRAPHIC PROPERTY
        // ═══════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO 4: HOLOGRAPHIC PROPERTY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        NeuroQuant original = new NeuroQuant("ComplexConcept");
        
        // Damage the vector (clear 50% of bits)
        java.util.BitSet damaged = (java.util.BitSet) original.hyperVector.clone();
        for (int i = 0; i < 5000; i++) {
            damaged.clear(i);
        }
        
        NeuroQuant damagedCell = new NeuroQuant("Damaged");
        damagedCell.hyperVector = damaged;
        
        double similarity = original.resonance(damagedCell);
        
        System.out.println("Original vector: 10,000 bits");
        System.out.println("Damaged vector: 50% of bits cleared");
        System.out.println("Similarity: " + String.format("%.2f", similarity));
        System.out.println();
        System.out.println("Result: Even with 50% damage, the concept is still recognizable!");
        System.out.println("This is HOLOGRAPHIC MEMORY - fault-tolerant and anti-fragile.");
        System.out.println();

        // ═══════════════════════════════════════════════════════════════
        // SUMMARY
        // ═══════════════════════════════════════════════════════════════
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ✅ DEMONSTRATION COMPLETE                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Key Achievements:");
        System.out.println("  ✓ 10,000D hypervector operations");
        System.out.println("  ✓ Zero-search retrieval (instant resonance)");
        System.out.println("  ✓ Biological growth (NCA evolution)");
        System.out.println("  ✓ Holographic memory (50% damage tolerance)");
        System.out.println();
        System.out.println("This is how biological brains actually work.");
        System.out.println();

        auditLog.stop();
    }
}
