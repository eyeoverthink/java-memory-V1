package fraymus.kernel;

import fraymus.hyper.HyperVector;

/**
 * THE SACRED MATH: Phi-Harmonic Logic.
 *
 * Uses the Golden Ratio (φ ≈ 1.618) to measure the "organic complexity"
 * of a HyperVector. A vector with density close to 1/φ (≈ 0.618) is
 * considered harmonically perfect — it has the ideal balance of structure
 * and entropy found in natural systems.
 *
 * Vectors that deviate from this golden density are "dissonant" and
 * receive lower scores from the Phi-Harmonic Scheduler.
 */
public class PhiLogic {

    public static final double PHI = 1.618033988749895;
    public static final double GOLDEN_DENSITY = 1.0 / PHI; // 0.6180339...

    /**
     * HARMONIC RESONANCE SCORE.
     * Measures how close a vector's bit density is to the Golden Ratio.
     *
     * @return 1.0 = Perfect Harmony (density ≈ 0.618), 0.0 = Pure Chaos
     */
    public static double harmonicScore(HyperVector v) {
        double density = v.density();
        double deviation = Math.abs(density - GOLDEN_DENSITY);

        // Score: 1.0 = Perfect Harmony, 0.0 = Chaos
        // Deviation of ~0.33 maps to 0.0
        return Math.max(0.0, 1.0 - (deviation * 3.0));
    }

    /**
     * FIBONACCI RESONANCE.
     * A secondary harmonic check: does the vector's set-bit count
     * fall near a Fibonacci number? Fibonacci numbers are the discrete
     * manifestation of φ in integer space.
     */
    public static double fibonacciResonance(HyperVector v) {
        int setBits = (int) (v.density() * HyperVector.D);
        // Find nearest Fibonacci number
        int fibPrev = 1, fibCurr = 1;
        while (fibCurr < setBits) {
            int next = fibPrev + fibCurr;
            fibPrev = fibCurr;
            fibCurr = next;
        }
        int nearestFib = (setBits - fibPrev < fibCurr - setBits) ? fibPrev : fibCurr;
        double distance = Math.abs(setBits - nearestFib) / (double) Math.max(1, setBits);
        return Math.max(0.0, 1.0 - distance);
    }

    /**
     * COMBINED PHI SCORE.
     * Weighted blend of density harmony (70%) and Fibonacci resonance (30%).
     */
    public static double phiScore(HyperVector v) {
        return 0.7 * harmonicScore(v) + 0.3 * fibonacciResonance(v);
    }

    /**
     * HARMONIZE: Nudge a vector closer to the Golden Ratio.
     * XOR-binds with a correction vector seeded from the current score,
     * pushing density toward 1/φ.
     */
    public static HyperVector harmonize(HyperVector v) {
        double score = harmonicScore(v);
        if (score > 0.95) return v; // Already perfect

        // Generate a correction vector deterministically from the score
        HyperVector correction = HyperVector.seeded((long) (score * 100_000));
        return v.bind(correction);
    }

    /**
     * IS DISSONANT: Returns true if the vector is too far from golden harmony.
     */
    public static boolean isDissonant(HyperVector v) {
        return harmonicScore(v) < 0.3;
    }
}
