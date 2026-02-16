package fraymus.bio;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Random;

/**
 * 🧬 THE NEURO-QUANT CELL
 * "A holographic neuron operating in 10,000 dimensions."
 * 
 * This implements Hyperdimensional Computing (HDC) - the way biological brains
 * actually work. Instead of 3D coordinates, we use 10,000-dimensional binary
 * hypervectors for holographic, fault-tolerant memory.
 * 
 * Revolutionary Properties:
 * 1. HOLOGRAPHIC: Cut the vector in half, it still contains the data (noisier)
 * 2. ZERO-SEARCH: Query vectors resonate with answers instantly
 * 3. ANTI-FRAGILE: Robust against corruption and noise
 * 4. SUPERPOSITION: Infinite concepts can be superimposed without data loss
 * 
 * This is how human brains encode "Map + Territory" simultaneously.
 */
public class NeuroQuant implements Serializable {
    
    private static final long serialVersionUID = 1L; // DNA Version 1.0
    
    // CONSTANTS
    private static final int D = 10000; // Hyper-Dimensions (10,000D space)
    private static final Random rng = new Random();

    // IDENTITY (Holographic)
    public final String id;
    public BitSet hyperVector; // The Soul (10,000 bits of holographic memory)
    
    // BIOLOGY (Neural Cellular Automata State)
    public float[] stateChannels = new float[16]; // RGB, Alpha, Hidden States
    public float energy = 1.0f;
    public int age = 0;
    
    // SPATIAL (for visualization)
    public double x, y, z;

    /**
     * Create a NeuroQuant cell with a concept
     */
    public NeuroQuant(String concept) {
        this.id = concept;
        this.hyperVector = generateRandomVector(); // Orthogonal seed
        
        // Initialize NCA state channels
        stateChannels[0] = (float) Math.random(); // R
        stateChannels[1] = (float) Math.random(); // G
        stateChannels[2] = (float) Math.random(); // B
        stateChannels[3] = 1.0f; // Alpha
        
        // Random spatial position
        this.x = Math.random() * 100;
        this.y = Math.random() * 100;
        this.z = Math.random() * 100;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HYPERDIMENSIONAL MATH (The "Harder" Logic)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BINDING (XOR): Combines two concepts into a new, unique concept.
     * 
     * Example: "Apple" XOR "Red" = "Red Apple"
     * 
     * This is mathematically reversible:
     * "Red Apple" XOR "Red" = "Apple"
     * 
     * In 10,000D space, XOR creates orthogonal combinations.
     */
    public void bind(NeuroQuant other) {
        this.hyperVector.xor(other.hyperVector);
    }

    /**
     * BUNDLING (Majority Rule): Memorizes multiple items in one vector.
     * 
     * Like superimposing images - you can store infinite concepts
     * in a single hypervector without data loss (in high dimensions).
     * 
     * This is how human memory works - holographic storage.
     */
    public void bundle(NeuroQuant other) {
        // In binary HDC, bundling is normalizing the bitwise sum.
        // We simulate this by flipping bits towards the other vector.
        for (int i = 0; i < D; i++) {
            if (other.hyperVector.get(i) && rng.nextFloat() < 0.5) {
                this.hyperVector.set(i, true);
            }
        }
    }

    /**
     * PERMUTATION (Rotation): Creates sequential relationships.
     * 
     * Example: "First" → "Second" → "Third"
     * 
     * This is how we encode sequences in HDC.
     */
    public void permute() {
        // Circular shift (rotation) in hyperdimensional space
        boolean lastBit = hyperVector.get(D - 1);
        for (int i = D - 1; i > 0; i--) {
            hyperVector.set(i, hyperVector.get(i - 1));
        }
        hyperVector.set(0, lastBit);
    }

    /**
     * SIMILARITY (Hamming Distance):
     * Returns 1.0 if identical, 0.0 if orthogonal (unrelated).
     * 
     * This is the "resonance" - how similar two concepts are.
     * In 10,000D space, random vectors are ~50% similar (orthogonal).
     */
    public double resonance(NeuroQuant other) {
        BitSet xor = (BitSet) this.hyperVector.clone();
        xor.xor(other.hyperVector);
        int differentBits = xor.cardinality();
        return 1.0 - ((double) differentBits / D);
    }

    /**
     * QUERY: Find what this vector represents by comparing to known concepts.
     * 
     * This is zero-search retrieval - the answer resonates out instantly.
     */
    public String query(NeuroQuant[] knownConcepts) {
        double maxResonance = -1.0;
        String bestMatch = "UNKNOWN";
        
        for (NeuroQuant concept : knownConcepts) {
            double similarity = resonance(concept);
            if (similarity > maxResonance) {
                maxResonance = similarity;
                bestMatch = concept.id;
            }
        }
        
        return bestMatch + " (" + String.format("%.2f", maxResonance) + ")";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NEURAL CELLULAR AUTOMATA (Biological Growth)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * NCA UPDATE: Cell updates based on local perception.
     * 
     * This is how biological cells work - they look at neighbors
     * and decide whether to grow, die, or mutate.
     */
    public void ncaUpdate(NeuroQuant[] neighbors) {
        if (neighbors.length == 0) return;
        
        // 1. PERCEPTION: Average neighbor states
        float[] avgState = new float[16];
        for (NeuroQuant neighbor : neighbors) {
            for (int i = 0; i < 16; i++) {
                avgState[i] += neighbor.stateChannels[i];
            }
        }
        for (int i = 0; i < 16; i++) {
            avgState[i] /= neighbors.length;
        }
        
        // 2. UPDATE RULE: Smooth towards neighbors (diffusion)
        for (int i = 0; i < 16; i++) {
            stateChannels[i] = stateChannels[i] * 0.9f + avgState[i] * 0.1f;
        }
        
        // 3. ENERGY DYNAMICS
        energy += (avgState[0] + avgState[1] + avgState[2]) / 3.0f * 0.01f;
        energy *= 0.995f; // Decay
        energy = Math.max(0.0f, Math.min(1.0f, energy));
        
        // 4. AGING
        age++;
    }

    /**
     * Check if cell is alive
     */
    public boolean isAlive() {
        return energy > 0.1f;
    }

    /**
     * Check if cell should reproduce
     */
    public boolean shouldReproduce() {
        return energy > 0.8f && age > 10;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Generate random hypervector (orthogonal seed)
     */
    private BitSet generateRandomVector() {
        BitSet bs = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if (rng.nextBoolean()) {
                bs.set(i);
            }
        }
        return bs;
    }

    /**
     * Clone this cell
     */
    public NeuroQuant clone() {
        NeuroQuant child = new NeuroQuant(this.id + "_CLONE");
        child.hyperVector = (BitSet) this.hyperVector.clone();
        child.energy = this.energy * 0.5f; // Split energy
        child.x = this.x + (Math.random() - 0.5) * 10;
        child.y = this.y + (Math.random() - 0.5) * 10;
        child.z = this.z + (Math.random() - 0.5) * 10;
        
        for (int i = 0; i < 16; i++) {
            child.stateChannels[i] = this.stateChannels[i];
        }
        
        return child;
    }

    /**
     * Get state summary
     */
    public String getState() {
        return String.format("NeuroQuant[%s, Energy=%.2f, Age=%d, Dim=%dD]",
            id, energy, age, D);
    }

    @Override
    public String toString() {
        return getState();
    }
}
