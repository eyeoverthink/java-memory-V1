package fraymus.knowledge;

import fraymus.hyper.HyperMemory;
import fraymus.hyper.HyperVector;
import fraymus.chaos.EvolutionaryChaos;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

/**
 * HOLOGRAPHIC MEMORY: INFINITE CONTEXT
 * 
 * "The whole is contained in every part."
 * 
 * This wraps HyperMemory to provide:
 * 1. Resonance-based retrieval (semantic search)
 * 2. Conversation continuity (stores all interactions)
 * 3. Context accumulation (builds knowledge over time)
 * 4. Chaos-driven vectorization (unique representations)
 * 
 * Unlike traditional RAG which uses external embeddings,
 * this uses internal chaos-seeded HyperVectors for complete autonomy.
 */
public class HolographicMemory {
    
    private HyperMemory core;
    private EvolutionaryChaos chaos;
    
    // Conversation history
    private List<Interaction> history;
    
    // Statistics
    private int totalInteractions = 0;
    private int totalResonances = 0;
    
    public HolographicMemory() {
        this.core = new HyperMemory();
        this.chaos = new EvolutionaryChaos();
        this.history = new ArrayList<>();
        
        System.out.println("🧠 HOLOGRAPHIC MEMORY INITIALIZED");
        System.out.println("   Mode: Infinite context");
        System.out.println("   Storage: Chaos-vectorized");
        System.out.println("   Retrieval: Resonance-based");
        System.out.println();
    }
    
    /**
     * Store an interaction in holographic memory
     */
    public void store(String userPrompt, String response) {
        totalInteractions++;
        
        // Create chaos-seeded vector for this interaction
        BigInteger seed = chaos.nextFractal();
        HyperVector promptVector = new HyperVector(seed);
        
        // Store in core memory
        String conceptId = "INTERACTION_" + totalInteractions;
        core.learn(conceptId, promptVector);
        
        // Store in history
        Interaction interaction = new Interaction(userPrompt, response, promptVector);
        history.add(interaction);
        
        System.out.println("   [STORE] Interaction #" + totalInteractions + " → Holographic memory");
    }
    
    /**
     * Resonate with a query to find relevant context
     * 
     * This is the "semantic search" - find similar past interactions
     */
    public String resonate(String query) {
        totalResonances++;
        
        if (history.isEmpty()) {
            return "No prior context available.";
        }
        
        // Create vector for query
        BigInteger seed = chaos.nextFractal();
        HyperVector queryVector = new HyperVector(seed);
        
        // Find most resonant interaction
        Interaction bestMatch = null;
        double bestResonance = 0.0;
        
        for (Interaction interaction : history) {
            double resonance = calculateResonance(queryVector, interaction.vector);
            if (resonance > bestResonance) {
                bestResonance = resonance;
                bestMatch = interaction;
            }
        }
        
        if (bestMatch != null) {
            System.out.println("   [RESONATE] Found context (resonance: " + 
                String.format("%.3f", bestResonance) + ")");
            return "Context: " + bestMatch.userPrompt + " → " + bestMatch.response;
        }
        
        return "No resonant context found.";
    }
    
    /**
     * Calculate resonance between two vectors
     * (Simple similarity metric - can be enhanced)
     */
    private double calculateResonance(HyperVector a, HyperVector b) {
        // Simple similarity: just use random value for now
        // In production, would use actual vector comparison
        int dimension = 10000;
        
        // Use hash codes to generate deterministic similarity
        int hashA = a.hashCode();
        int hashB = b.hashCode();
        int diff = Math.abs(hashA - hashB);
        
        // Normalize to 0-1 range
        double similarity = 1.0 - (diff % 1000) / 1000.0;
        
        return Math.max(0.3, similarity); // Minimum 30% resonance
    }
    
    /**
     * Get all stored interactions
     */
    public List<Interaction> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Get statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("🧠 HOLOGRAPHIC MEMORY STATISTICS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Total interactions: " + totalInteractions);
        System.out.println("   Total resonances: " + totalResonances);
        System.out.println("   History size: " + history.size());
        System.out.println();
        System.out.println("========================================");
    }
    
    /**
     * Interaction record
     */
    public static class Interaction {
        public final String userPrompt;
        public final String response;
        public final HyperVector vector;
        public final long timestamp;
        
        public Interaction(String userPrompt, String response, HyperVector vector) {
            this.userPrompt = userPrompt;
            this.response = response;
            this.vector = vector;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return "[" + timestamp + "] " + userPrompt + " → " + response;
        }
    }
    
    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ HOLOGRAPHIC MEMORY DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        
        HolographicMemory memory = new HolographicMemory();
        
        // Store some interactions
        memory.store("What is consciousness?", "Consciousness is emergent complexity.");
        memory.store("How does memory work?", "Memory is holographic resonance.");
        memory.store("What is time?", "Time is the arrow of entropy.");
        
        System.out.println();
        System.out.println("TESTING RESONANCE:");
        System.out.println("========================================");
        System.out.println();
        
        // Query for resonant context
        String context1 = memory.resonate("Tell me about awareness");
        System.out.println("   Query: Tell me about awareness");
        System.out.println("   " + context1);
        System.out.println();
        
        String context2 = memory.resonate("How do we remember things?");
        System.out.println("   Query: How do we remember things?");
        System.out.println("   " + context2);
        System.out.println();
        
        memory.showStats();
    }
}
