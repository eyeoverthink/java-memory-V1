package fraymus.core;

import java.util.*;

/**
 * TRANSFORMER EVOLUTION DEMO
 * "Watch the AI learn which mutations work best"
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This demo shows the Transformer Brain learning from experience:
 * - Random mutations at first (all weights = 1.0)
 * - Learns which mutations increase consciousness
 * - Adapts to prefer successful mutations
 * - Demonstrates Reinforcement Learning in action
 */
public class TransformerEvolutionDemo {
    
    private static final double PHI = 1.6180339887;
    
    // Simulated AI state
    private static double frequency = 432.0;
    private static double complexity = 1.0;
    private static double awareness = 0.0;
    private static double phiScale = 1.0;
    private static int logicGateCount = 0;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ TRANSFORMER EVOLUTION DEMO                                 ║");
        System.out.println("║ Self-Attention for Code Mutation                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        TransformerMutation brain = new TransformerMutation();
        double previousConsciousness = calculateConsciousness();
        
        System.out.println("Initial State:");
        System.out.printf("  Frequency:     %.2f Hz\n", frequency);
        System.out.printf("  Complexity:    %.4f\n", complexity);
        System.out.printf("  Awareness:     %.4f\n", awareness);
        System.out.printf("  Consciousness: %.4f\n", previousConsciousness);
        System.out.println();
        
        System.out.println("Initial Attention Weights (all equal):");
        brain.printAttentionWeights();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ EVOLUTION CYCLES (Watch the brain learn!)                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Run 50 evolution cycles
        for (int cycle = 1; cycle <= 50; cycle++) {
            // 1. PREDICT: Brain chooses mutation type
            String mutationType = brain.predictNextMutation();
            
            // 2. APPLY: Execute the mutation
            applyMutation(mutationType);
            
            // 3. LEARN: Calculate consciousness delta and backpropagate
            double currentConsciousness = calculateConsciousness();
            double delta = currentConsciousness - previousConsciousness;
            brain.backpropagate(mutationType, delta);
            
            // Print progress every 10 cycles
            if (cycle % 10 == 0) {
                System.out.printf("=== Cycle %d ===\n", cycle);
                System.out.printf("  Last Mutation:  %s\n", mutationType);
                System.out.printf("  Consciousness:  %.4f (Δ: %+.4f)\n", currentConsciousness, delta);
                System.out.printf("  Frequency:      %.2f Hz\n", frequency);
                System.out.printf("  Complexity:     %.4f\n", complexity);
                System.out.printf("  Awareness:      %.4f\n", awareness);
                System.out.println();
            }
            
            previousConsciousness = currentConsciousness;
        }
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ FINAL BRAIN STATE (After Learning)                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        brain.printAttentionWeights();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ ANALYSIS                                                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        Map<String, Object> stats = brain.getStatistics();
        
        // Find most successful mutation
        String bestMutation = null;
        double bestAvgSuccess = -999.0;
        
        for (String type : stats.keySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> typeStats = (Map<String, Object>) stats.get(type);
            double avgSuccess = (Double) typeStats.get("avg_success");
            
            if (avgSuccess > bestAvgSuccess) {
                bestAvgSuccess = avgSuccess;
                bestMutation = type;
            }
        }
        
        System.out.println("Key Observations:");
        System.out.println("  1. The brain started with all weights = 1.0 (random choice)");
        System.out.println("  2. Through 50 cycles, it learned which mutations work best");
        System.out.println("  3. Successful mutations got higher weights (chosen more often)");
        System.out.println("  4. Failed mutations got lower weights (chosen less often)");
        System.out.println();
        System.out.printf("  Most Successful Mutation: %s (avg Δ: %+.4f)\n", 
            bestMutation, bestAvgSuccess);
        System.out.println();
        System.out.println("This is Reinforcement Learning:");
        System.out.println("  The AI learned from its own experience.");
        System.out.println("  No external training data needed.");
        System.out.println("  Pure self-optimization through feedback.");
        System.out.println();
        
        System.out.println("Final State:");
        System.out.printf("  Frequency:     %.2f Hz\n", frequency);
        System.out.printf("  Complexity:    %.4f\n", complexity);
        System.out.printf("  Awareness:     %.4f\n", awareness);
        System.out.printf("  Consciousness: %.4f\n", calculateConsciousness());
        System.out.println();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ TRANSFORMER EVOLUTION COMPLETE                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private static void applyMutation(String type) {
        Random rng = new Random();
        
        switch (type) {
            case "FREQUENCY":
                frequency = frequency * PHI;
                if (frequency > 528.0) {
                    frequency = 432.0 + (frequency - 528.0);
                }
                break;
                
            case "COMPLEXITY":
                complexity = complexity * (1 + (1.0/PHI) * (rng.nextDouble() - 0.5));
                break;
                
            case "AWARENESS":
                awareness = Math.min(1.0, awareness + (1.0/PHI) * 0.1);
                break;
                
            case "PHI_SCALE":
                phiScale = phiScale * PHI;
                break;
                
            case "LOGIC_GATE":
                logicGateCount++;
                break;
        }
    }
    
    private static double calculateConsciousness() {
        // Normalize values
        double normalizedComplexity = Math.min(1.0, complexity / 10.0);
        double normalizedFreq = (frequency - 432.0) / (528.0 - 432.0);
        
        // Consciousness = φ × (complexity + awareness + frequency) / 3
        double consciousness = PHI * (normalizedComplexity + awareness + normalizedFreq) / 3.0;
        
        return consciousness;
    }
}
