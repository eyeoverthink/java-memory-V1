package fraymus.bio;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Random;

/**
 * THE NEURO-QUANT CELL
 * "A holographic neuron operating in 10,000 dimensions."
 */
public class NeuroQuant implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // CONSTANTS
    private static final int D = 10000; // Hyper-Dimensions
    private static final Random rng = new Random();

    // IDENTITY (Holographic)
    public final String id;
    public final BitSet hyperVector; // The Soul (10,000 bits)
    
    // BIOLOGY (NCA State)
    public float energy = 1.0f;
    public int age = 0;

    public NeuroQuant(String concept) {
        this.id = concept;
        this.hyperVector = generateRandomVector(); // Orthogonal seed
    }
    
    // Copy constructor for evolution
    private NeuroQuant(String id, BitSet vector) {
        this.id = id;
        this.hyperVector = vector;
    }

    // --- HYPERDIMENSIONAL MATH (The "Harder" Logic) ---

    /**
     * BINDING (XOR): Combines two concepts into a new, unique concept.
     * "Apple" XOR "Red" = "Red Apple"
     */
    public NeuroQuant bind(NeuroQuant other) {
        BitSet childVec = (BitSet) this.hyperVector.clone();
        childVec.xor(other.hyperVector); // XOR is the binding operation
        return new NeuroQuant(this.id + "_X_" + other.id, childVec);
    }

    /**
     * BUNDLING (Majority Rule): Memorizes multiple items in one vector.
     * Like superimposing images.
     */
    public void bundle(NeuroQuant other) {
        // In binary HDC, bundling is normalizing the bitwise sum.
        // We simulate this by flipping bits towards the other vector probabalistically.
        for (int i = 0; i < D; i++) {
            if (other.hyperVector.get(i) && rng.nextFloat() < 0.5) {
                this.hyperVector.set(i, true);
            }
        }
    }

    /**
     * SIMILARITY (Hamming Distance):
     * Returns 1.0 if identical, 0.5 if orthogonal (unrelated), 0.0 if inverse.
     */
    public double resonance(NeuroQuant other) {
        BitSet xor = (BitSet) this.hyperVector.clone();
        xor.xor(other.hyperVector);
        int differentBits = xor.cardinality();
        return 1.0 - ((double) differentBits / D);
    }

    // --- UTILITIES ---
    private BitSet generateRandomVector() {
        BitSet bs = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if (rng.nextBoolean()) bs.set(i);
        }
        return bs;
    }
}
