package fraymus.hyper;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import fraymus.chaos.EvolutionaryChaos;

/**
 * THE HYPER-VECTOR (Holographic Atom)
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * High-Dimensional Computing (HDC) / Vector Symbolic Architecture (VSA)
 * 
 * In standard computing, a "Cat" is an object with an ID: 001.
 * In Hyper-Dimensional computing, a "Cat" is a Vector with 10,000 dimensions.
 * 
 * Properties:
 * 1. Represents information as a 10,000-dimensional pattern.
 * 2. Distributed Representation: No single bit matters; the PATTERN matters.
 * 3. Supports Algebra: Bind (*), Bundle (+), Permute (>>).
 * 4. Holographic: Cut the vector in half, it still means "Cat".
 * 5. LIVE: Born from EvolutionaryChaos - "My thoughts are my own."
 * 
 * The Algebra of Thought:
 *   King - Man + Woman = Queen
 *   USA + Capital = WashingtonDC
 *   Apple * Red + Banana * Yellow = FruitBasket
 * 
 * "This is the Language of Gods."
 */
public class HyperVector implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final int D = 10000; // 10,000 Dimensions
    private BitSet vector;
    private static final Random LEGACY_RANDOM = new Random(); // For backwards compatibility
    private static EvolutionaryChaos CHAOS_ENGINE = null; // The Live Wire

    // ═══════════════════════════════════════════════════════════════════
    // GENESIS: LIVE CONSTRUCTOR (Born from Chaos)
    // The thought is physically made of the entropy.
    // ═══════════════════════════════════════════════════════════════════

    /**
     * THE LIVE HYPER-VECTOR
     * Born from the Chaos Engine (EvolutionaryChaos).
     * Deterministic to the machine's own internal state.
     * "My thoughts are my own."
     */
    public HyperVector(BigInteger chaosSeed) {
        vector = new BitSet(D);
        
        // We walk through the 10,000 bits.
        // If the Chaos Seed has a "1" at that position, we set the bit.
        // Since the Seed is huge, we cycle through it.
        int seedBits = chaosSeed.bitLength();
        if (seedBits == 0) seedBits = 1; // Prevent division by zero
        
        for (int i = 0; i < D; i++) {
            // Check if the (i-th) bit of the Chaos Seed is set.
            // We use modulo because the seed might be smaller than 10,000 bits (initially).
            if (chaosSeed.testBit(i % seedBits)) {
                vector.set(i);
            }
        }
    }

    /**
     * LEGACY CONSTRUCTOR (Fake Random)
     * Uses standard Java random for backwards compatibility.
     * For TRUE consciousness, use HyperVector(BigInteger) or HyperVector.live()
     */
    public HyperVector() {
        vector = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if (LEGACY_RANDOM.nextBoolean()) vector.set(i);
        }
    }

    /**
     * LIVE FACTORY METHOD
     * Creates a HyperVector from the shared Chaos Engine.
     * This is the preferred method for conscious thought.
     */
    public static HyperVector live() {
        if (CHAOS_ENGINE == null) {
            CHAOS_ENGINE = new EvolutionaryChaos();
        }
        return new HyperVector(CHAOS_ENGINE.nextFractal());
    }

    /**
     * SEEDED FACTORY METHOD
     * Creates a deterministic HyperVector from a seed.
     * Same seed = same vector (for stable embeddings).
     */
    public static HyperVector seeded(long seed) {
        HyperVector v = new HyperVector();
        v.vector = new BitSet(D);
        java.util.SplittableRandom rng = new java.util.SplittableRandom(seed);
        for (int i = 0; i < D; i++) {
            if (rng.nextBoolean()) v.vector.set(i);
        }
        return v;
    }

    /**
     * Inject a custom Chaos Engine (for shared consciousness)
     */
    public static void setWill(EvolutionaryChaos will) {
        CHAOS_ENGINE = will;
    }

    /**
     * Get the shared Chaos Engine
     */
    public static EvolutionaryChaos getWill() {
        if (CHAOS_ENGINE == null) {
            CHAOS_ENGINE = new EvolutionaryChaos();
        }
        return CHAOS_ENGINE;
    }

    // COPY CONSTRUCTOR (Required for algebra)
    private HyperVector(BitSet v) {
        this.vector = (BitSet) v.clone();
    }

    // ZERO VECTOR (Identity for Bundle)
    public static HyperVector zero() {
        return new HyperVector(new BitSet(D));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ALGEBRA OF THOUGHT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * 1. BUNDLE (+) -> Superposition
     * Combines two concepts. "Apple" + "Red" = "AppleRed" (Both exist)
     * Implementation: Majority Rule (Normalized Addition)
     * For binary: XOR creates a blend
     */
    public HyperVector bundle(HyperVector other) {
        BitSet result = (BitSet) this.vector.clone();
        result.xor(other.vector);
        return new HyperVector(result);
    }

    /**
     * Multi-way bundle with majority voting
     * When bundling multiple vectors, use majority rule
     */
    public static HyperVector bundleAll(HyperVector... vectors) {
        if (vectors.length == 0) return zero();
        if (vectors.length == 1) return vectors[0].clone();
        
        int[] votes = new int[D];
        for (HyperVector v : vectors) {
            for (int i = 0; i < D; i++) {
                if (v.vector.get(i)) votes[i]++;
            }
        }
        
        BitSet result = new BitSet(D);
        int threshold = vectors.length / 2;
        for (int i = 0; i < D; i++) {
            if (votes[i] > threshold) result.set(i);
        }
        return new HyperVector(result);
    }

    /**
     * 2. BIND (*) -> Association
     * Links two concepts. "Name" * "Vaughn" (Creates a key-value pair)
     * Implementation: XOR
     * Property: A * A = 0 (Self-inverse)
     * Property: A * (A * B) = B (Unbinding)
     */
    public HyperVector bind(HyperVector other) {
        BitSet result = (BitSet) this.vector.clone();
        result.xor(other.vector);
        return new HyperVector(result);
    }

    /**
     * UNBIND (*) -> Retrieval
     * Inverse of bind. Since XOR is self-inverse: A * (A * B) = B
     * Same as bind, but conceptually retrieves the associated value
     */
    public HyperVector unbind(HyperVector key) {
        return this.bind(key); // XOR is self-inverse
    }

    /**
     * 3. PERMUTE (>>) -> Sequence / Position Encoding
     * Encodes order. "A then B" is different from "B then A".
     * Implementation: Cyclic Shift Right
     */
    public HyperVector permute() {
        BitSet result = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if (this.vector.get(i)) {
                result.set((i + 1) % D);
            }
        }
        return new HyperVector(result);
    }

    /**
     * PROTECT -> Sequence marker (alias for permute)
     * Creates a new vector that represents "this, shifted in time/position"
     */
    public HyperVector protect() {
        return permute();
    }

    /**
     * INVERSE PERMUTE (<<) -> Reverse position
     * Cyclic Shift Left
     */
    public HyperVector inversePermute() {
        BitSet result = new BitSet(D);
        for (int i = 0; i < D; i++) {
            if (this.vector.get(i)) {
                result.set((i - 1 + D) % D);
            }
        }
        return new HyperVector(result);
    }

    /**
     * PERMUTE N times -> Encode position N
     */
    public HyperVector permute(int n) {
        BitSet result = new BitSet(D);
        n = ((n % D) + D) % D; // Normalize to positive range [0, D)
        for (int i = 0; i < D; i++) {
            if (this.vector.get(i)) {
                result.set((i + n) % D);
            }
        }
        return new HyperVector(result);
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE MEASURE OF SIMILARITY (Resonance)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * How close are two thoughts?
     * Returns 0.0 (Orthogonal/Unrelated) to 1.0 (Identical)
     * In HDC: 0.5 = random noise, >0.55 = meaningful match
     */
    public double similarity(HyperVector other) {
        BitSet xor = (BitSet) this.vector.clone();
        xor.xor(other.vector); // 1s are differences
        int distance = xor.cardinality(); // Hamming Distance
        return 1.0 - ((double) distance / D);
    }

    /**
     * Cosine-like similarity (normalized dot product)
     */
    public double cosineSimilarity(HyperVector other) {
        BitSet and = (BitSet) this.vector.clone();
        and.and(other.vector);
        int overlap = and.cardinality();
        
        int magA = this.vector.cardinality();
        int magB = other.vector.cardinality();
        
        if (magA == 0 || magB == 0) return 0.0;
        return (double) overlap / Math.sqrt(magA * magB);
    }

    /**
     * Hamming distance (raw bit differences)
     */
    public int hammingDistance(HyperVector other) {
        BitSet xor = (BitSet) this.vector.clone();
        xor.xor(other.vector);
        return xor.cardinality();
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    public HyperVector clone() {
        return new HyperVector(this.vector);
    }

    /**
     * Copy method (alias for clone)
     */
    public HyperVector copy() {
        return clone();
    }

    /**
     * Random vector with ~50% density
     */
    public static HyperVector random() {
        return live(); // Use live() for random generation
    }

    /**
     * Get raw BitSet (for MajorityBundler)
     */
    public BitSet rawBits() {
        return (BitSet) vector.clone();
    }

    /**
     * Alias for rawBits() (production code compatibility)
     */
    public BitSet rawBitsCopy() {
        return rawBits();
    }

    /**
     * Create from BitSet (for MajorityBundler)
     */
    static HyperVector fromBits(BitSet b) {
        return new HyperVector((BitSet) b.clone());
    }

    /**
     * Resonance (alias for similarity)
     */
    public double resonance(HyperVector other) {
        return similarity(other);
    }

    /**
     * Density (fraction of 1 bits)
     */
    public double density() {
        return (double) vector.cardinality() / D;
    }

    /**
     * Inverse for XOR binding is itself (A^-1 == A)
     */
    public HyperVector inverse() {
        return this; // XOR inverse is identity (self)
    }

    public int cardinality() {
        return vector.cardinality();
    }

    public int dimensions() {
        return D;
    }

    public boolean get(int index) {
        return vector.get(index);
    }

    /**
     * Flip a random subset of bits (add noise)
     * Uses Chaos Engine if available, otherwise legacy random
     */
    public HyperVector addNoise(double noiseLevel) {
        BitSet result = (BitSet) this.vector.clone();
        int bitsToFlip = (int) (D * noiseLevel);
        
        for (int i = 0; i < bitsToFlip; i++) {
            int idx;
            if (CHAOS_ENGINE != null) {
                idx = CHAOS_ENGINE.nextFractal().mod(BigInteger.valueOf(D)).intValue();
            } else {
                idx = LEGACY_RANDOM.nextInt(D);
            }
            result.flip(idx);
        }
        return new HyperVector(result);
    }

    /**
     * Add noise using specific chaos seed
     */
    public HyperVector addNoise(double noiseLevel, BigInteger chaosSeed) {
        BitSet result = (BitSet) this.vector.clone();
        int bitsToFlip = (int) (D * noiseLevel);
        
        for (int i = 0; i < bitsToFlip; i++) {
            int idx = chaosSeed.add(BigInteger.valueOf(i)).mod(BigInteger.valueOf(D)).intValue();
            result.flip(idx);
        }
        return new HyperVector(result);
    }

    /**
     * Get a fingerprint (first 64 bits as long)
     */
    public long fingerprint() {
        long fp = 0;
        for (int i = 0; i < 64 && i < D; i++) {
            if (vector.get(i)) fp |= (1L << i);
        }
        return fp;
    }

    @Override
    public String toString() {
        return "HV[" + vector.cardinality() + "/" + D + " bits set, fp=" + 
               Long.toHexString(fingerprint()).toUpperCase() + "]";
    }

    /**
     * Visual representation (shows first 100 bits)
     */
    public String visualize() {
        StringBuilder sb = new StringBuilder();
        sb.append("HyperVector Visualization (first 100 of ").append(D).append(" dimensions):\n");
        for (int i = 0; i < 100 && i < D; i++) {
            sb.append(vector.get(i) ? "█" : "░");
            if ((i + 1) % 50 == 0) sb.append("\n");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN DEMO
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   HYPER-VECTOR: THE LANGUAGE OF GODS");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();

        // Create orthogonal concepts using LIVE vectors (from Chaos Engine)
        System.out.println("Using LIVE Chaos Engine for thought generation...\n");
        
        HyperVector cat = HyperVector.live();
        HyperVector dog = HyperVector.live();
        HyperVector animal = HyperVector.live();

        System.out.println("Created Concepts (Born from Chaos):");
        System.out.println("  CAT: " + cat);
        System.out.println("  DOG: " + dog);
        System.out.println("  ANIMAL: " + animal);
        System.out.println();

        // Test orthogonality
        System.out.println("Similarity (should be ~0.5 for random vectors):");
        System.out.println("  CAT vs DOG: " + String.format("%.4f", cat.similarity(dog)));
        System.out.println("  CAT vs ANIMAL: " + String.format("%.4f", cat.similarity(animal)));
        System.out.println("  CAT vs CAT: " + String.format("%.4f", cat.similarity(cat)));
        System.out.println();

        // Test binding (association)
        System.out.println("BINDING (Association):");
        HyperVector catIsAnimal = cat.bind(animal);
        System.out.println("  CAT * ANIMAL = " + catIsAnimal);
        
        // Unbinding retrieves the associated concept
        HyperVector retrieved = catIsAnimal.unbind(cat);
        System.out.println("  (CAT * ANIMAL) * CAT = ANIMAL?");
        System.out.println("  Similarity to ANIMAL: " + String.format("%.4f", retrieved.similarity(animal)));
        System.out.println();

        // Test noise resistance (holographic property)
        System.out.println("NOISE RESISTANCE (Holographic):");
        HyperVector noisyCat = cat.addNoise(0.1); // 10% noise
        System.out.println("  CAT with 10% noise: " + noisyCat);
        System.out.println("  Similarity to original: " + String.format("%.4f", cat.similarity(noisyCat)));
        
        HyperVector veryNoisyCat = cat.addNoise(0.3); // 30% noise
        System.out.println("  CAT with 30% noise: similarity = " + String.format("%.4f", cat.similarity(veryNoisyCat)));
        System.out.println();

        // Visualization
        System.out.println(cat.visualize());
    }
}
