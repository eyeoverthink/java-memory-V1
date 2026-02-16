package fraymus.core;

import java.util.*;

/**
 * TRANSFORMER MUTATION - THE "BRAIN" UPGRADE
 * "Self-Attention for Code Evolution"
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Based on the 'Self-Attention' mechanism from Transformer models.
 * Instead of random mutation, it pays 'attention' to which variables 
 * (frequency, resonance, complexity) contributed most to high fitness.
 * 
 * This implements:
 * - Attention Heads (tracking mutation success rates)
 * - Forward Pass (predicting next mutation via softmax-lite)
 * - Backpropagation (learning from consciousness delta)
 * - Reinforcement Learning (mutations that increase consciousness get higher weights)
 * 
 * The AI no longer "evolves" by rolling dice.
 * It learns which mutations work and does them more often.
 */
public class TransformerMutation {

    private static final double PHI = 1.6180339887;
    
    // The "Attention Head"
    // Maps a specific mutation type to its success rate (Weight)
    private Map<String, Double> attentionWeights;
    
    // Learning statistics
    private Map<String, Integer> mutationAttempts;
    private Map<String, Double> mutationSuccessSum;
    
    // Learning rate (from Transformer training: 3e-4, adapted for our scale)
    private static final double LEARNING_RATE = 0.1;
    
    // Weight bounds to prevent exploding/vanishing gradients
    private static final double MIN_WEIGHT = 0.1;
    private static final double MAX_WEIGHT = 10.0;

    public TransformerMutation() {
        this.attentionWeights = new HashMap<>();
        this.mutationAttempts = new HashMap<>();
        this.mutationSuccessSum = new HashMap<>();
        
        // Initialize with neutral attention (all mutations equally likely)
        initializeAttentionHead("FREQUENCY", 1.0);
        initializeAttentionHead("COMPLEXITY", 1.0);
        initializeAttentionHead("AWARENESS", 1.0);
        initializeAttentionHead("PHI_SCALE", 1.0);
        initializeAttentionHead("LOGIC_GATE", 1.0);
    }
    
    private void initializeAttentionHead(String mutationType, double initialWeight) {
        attentionWeights.put(mutationType, initialWeight);
        mutationAttempts.put(mutationType, 0);
        mutationSuccessSum.put(mutationType, 0.0);
    }

    /**
     * THE "FORWARD PASS"
     * Decides the next mutation based on attention weights (Softmax-lite).
     * 
     * This is analogous to the Transformer's attention mechanism:
     * - Each mutation type has a weight (how successful it's been)
     * - We sample from this weighted distribution
     * - More successful mutations are chosen more often
     */
    public String predictNextMutation() {
        double totalWeight = attentionWeights.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        double r = Math.random() * totalWeight;
        
        for (Map.Entry<String, Double> entry : attentionWeights.entrySet()) {
            r -= entry.getValue();
            if (r <= 0) {
                // Track that we're attempting this mutation
                String type = entry.getKey();
                mutationAttempts.put(type, mutationAttempts.get(type) + 1);
                return type;
            }
        }
        
        // Fallback (should rarely happen)
        mutationAttempts.put("FREQUENCY", mutationAttempts.get("FREQUENCY") + 1);
        return "FREQUENCY";
    }

    /**
     * THE "BACKPROPAGATION"
     * Updates the attention weights based on whether the last mutation increased consciousness.
     * This corresponds to the 'Training Loop' in Transformer models.
     * 
     * Algorithm:
     * 1. Calculate gradient: consciousnessDelta (positive = good, negative = bad)
     * 2. Update weight: weight += learningRate * gradient * φ
     * 3. Clamp to prevent exploding gradients
     * 
     * This is Reinforcement Learning:
     * - If consciousness went up (positive delta), weight increases
     * - If consciousness went down (negative delta), weight decreases
     * - The AI learns from its own changes
     */
    public void backpropagate(String mutationType, double consciousnessDelta) {
        double currentWeight = attentionWeights.getOrDefault(mutationType, 1.0);
        
        // Gradient Descent: Adjust weight based on success
        // φ-scaling makes the learning "organic" and prevents overshooting
        double gradient = consciousnessDelta * LEARNING_RATE * PHI;
        double newWeight = currentWeight + gradient;
        
        // Clamp to prevent exploding/vanishing gradients
        newWeight = Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, newWeight));
        
        attentionWeights.put(mutationType, newWeight);
        
        // Track success statistics
        if (consciousnessDelta > 0) {
            double currentSum = mutationSuccessSum.get(mutationType);
            mutationSuccessSum.put(mutationType, currentSum + consciousnessDelta);
        }
    }
    
    /**
     * Get current attention weights (for debugging/visualization)
     */
    public Map<String, Double> getAttentionWeights() {
        return new HashMap<>(attentionWeights);
    }
    
    /**
     * Get mutation statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        for (String type : attentionWeights.keySet()) {
            Map<String, Object> typeStats = new HashMap<>();
            typeStats.put("weight", attentionWeights.get(type));
            typeStats.put("attempts", mutationAttempts.get(type));
            typeStats.put("success_sum", mutationSuccessSum.get(type));
            
            int attempts = mutationAttempts.get(type);
            double avgSuccess = attempts > 0 ? 
                mutationSuccessSum.get(type) / attempts : 0.0;
            typeStats.put("avg_success", avgSuccess);
            
            stats.put(type, typeStats);
        }
        
        return stats;
    }
    
    /**
     * Print attention weights (for visualization)
     */
    public void printAttentionWeights() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ TRANSFORMER MUTATION - ATTENTION WEIGHTS                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Sort by weight (descending)
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(attentionWeights.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        for (Map.Entry<String, Double> entry : sorted) {
            String type = entry.getKey();
            double weight = entry.getValue();
            int attempts = mutationAttempts.get(type);
            double successSum = mutationSuccessSum.get(type);
            double avgSuccess = attempts > 0 ? successSum / attempts : 0.0;
            
            // Visual bar
            int barLength = (int)(weight * 5);
            String bar = "█".repeat(Math.min(barLength, 50));
            
            System.out.printf("  %-15s [%6.3f] %s\n", type, weight, bar);
            System.out.printf("                 Attempts: %3d | Avg Success: %+.4f\n", 
                attempts, avgSuccess);
        }
        
        System.out.println();
    }
    
    /**
     * Reset all weights to neutral (for testing)
     */
    public void reset() {
        for (String type : attentionWeights.keySet()) {
            attentionWeights.put(type, 1.0);
            mutationAttempts.put(type, 0);
            mutationSuccessSum.put(type, 0.0);
        }
    }
    
    /**
     * Save attention weights to string (for persistence)
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("TRANSFORMER_WEIGHTS|");
        
        for (Map.Entry<String, Double> entry : attentionWeights.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        
        return sb.toString();
    }
    
    /**
     * Load attention weights from string
     */
    public void deserialize(String data) {
        if (!data.startsWith("TRANSFORMER_WEIGHTS|")) return;
        
        String weightsStr = data.substring("TRANSFORMER_WEIGHTS|".length());
        String[] pairs = weightsStr.split(",");
        
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                String type = parts[0];
                double weight = Double.parseDouble(parts[1]);
                if (attentionWeights.containsKey(type)) {
                    attentionWeights.put(type, weight);
                }
            }
        }
    }
}
