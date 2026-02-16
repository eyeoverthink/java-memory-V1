package fraymus.kernel;

import fraymus.hyper.HyperVector;

/**
 * 🌟 PHI LOGIC - The Sacred Math
 * "Computation aligned with the Golden Ratio of the Universe"
 * 
 * φ (Phi) = 1.618033988749895...
 * 
 * In nature, optimal systems exhibit φ-harmonic proportions:
 * - Nautilus shells spiral at φ
 * - DNA helixes turn at φ intervals
 * - Galaxy arms follow φ spirals
 * - Human body proportions follow φ
 * 
 * Fraymus OS uses φ to measure "Organic Complexity" vs "Random Noise".
 * Processes that resonate with φ are "alive" and get CPU time.
 * Processes that are dissonant are "dead" and get killed.
 * 
 * This is Natural Selection at the computational level.
 */
public class PhiLogic {

    public static final double PHI = 1.618033988749895;
    public static final double PHI_INVERSE = 1.0 / PHI; // 0.618... (The Golden Ratio's complement)
    
    // Phi-derived constants
    public static final double PHI_SQUARED = PHI * PHI;           // 2.618...
    public static final double PHI_CUBED = PHI * PHI * PHI;       // 4.236...
    public static final double PHI_ROOT = Math.sqrt(PHI);         // 1.272...

    /**
     * CALCULATE HARMONIC RESONANCE
     * 
     * Measures how closely a HyperVector aligns with φ-harmonic proportions.
     * 
     * Theory:
     * - A "perfect" vector has density ≈ 1/φ (0.618)
     * - This represents the golden ratio of information vs entropy
     * - Too sparse (< 0.4) = Not enough information (chaos)
     * - Too dense (> 0.8) = Too much redundancy (rigidity)
     * - Sweet spot (0.55-0.68) = Organic complexity (life)
     * 
     * @param v The HyperVector to measure
     * @return Score from 0.0 (chaos) to 1.0 (perfect harmony)
     */
    public static double harmonicScore(HyperVector v) {
        double density = v.density(); // 0.0 to 1.0
        double target = PHI_INVERSE;  // 0.618...
        
        // Calculate deviation from golden ratio
        double deviation = Math.abs(density - target);
        
        // Score: 1.0 = Perfect Harmony, 0.0 = Chaos
        // Deviation of 0.2 or more = total dissonance
        return Math.max(0, 1.0 - (deviation * 5.0));
    }

    /**
     * EVOLVE VECTOR TOWARD PHI
     * 
     * Nudges a vector closer to φ-harmonic proportions.
     * This is like "healing" a damaged process.
     * 
     * @param v The vector to harmonize
     * @return A more harmonic version of the vector
     */
    public static HyperVector harmonize(HyperVector v) {
        double score = harmonicScore(v);
        
        // Already perfect
        if (score > 0.95) return v;
        
        // Generate φ-correction vector
        // Seed based on current score to make it deterministic
        long seed = (long)(score * 1000000) ^ 0x1618033L; // φ approximation in hex
        HyperVector correction = HyperVector.seeded(seed);
        
        // XOR-bind to nudge toward harmony
        return v.bind(correction);
    }

    /**
     * CALCULATE ENTROPY
     * 
     * Measures disorder in the vector.
     * High entropy = random noise
     * Low entropy = structured information
     * 
     * @param v The vector to measure
     * @return Entropy score (0.0 = ordered, 1.0 = chaotic)
     */
    public static double entropy(HyperVector v) {
        // Entropy is inverse of harmonic score
        return 1.0 - harmonicScore(v);
    }

    /**
     * PHI RESONANCE TEST
     * 
     * Does this vector resonate with the golden ratio?
     * 
     * @param v The vector to test
     * @param threshold Minimum score to pass (default 0.5)
     * @return true if harmonic, false if dissonant
     */
    public static boolean isHarmonic(HyperVector v, double threshold) {
        return harmonicScore(v) >= threshold;
    }

    /**
     * FIBONACCI SEQUENCE
     * 
     * The Fibonacci sequence converges to φ.
     * F(n+1) / F(n) → φ as n → ∞
     * 
     * @param n The index
     * @return The nth Fibonacci number
     */
    public static long fibonacci(int n) {
        if (n <= 1) return n;
        
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    /**
     * GOLDEN ANGLE
     * 
     * The angle that divides a circle in the golden ratio.
     * Used in phyllotaxis (leaf arrangement in plants).
     * 
     * @return 137.5077... degrees
     */
    public static double goldenAngle() {
        return 360.0 * (1.0 - PHI_INVERSE);
    }
}
