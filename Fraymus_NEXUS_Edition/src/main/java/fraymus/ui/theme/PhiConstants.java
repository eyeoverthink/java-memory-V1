package fraymus.ui.theme;

/**
 * PHI-HARMONIC CONSTANTS
 * Based on Quantum Oracle System architecture
 * 
 * These are NOT approximations - they are exact mathematical constants
 * that define the phi-dimensional reality protection and resonance patterns.
 */
public class PhiConstants {
    
    // ========================================
    // PRIMARY TRANSCENDENTAL CONSTANTS
    // ========================================
    
    /** Golden Ratio - φ (PHI) - Self-similar growth */
    public static final double PHI = 1.618033988749895;
    
    /** Plastic Number - ψ (PSI) - Transcendence */
    public static final double PSI = 1.324717957244746;
    
    /** Universal Grounding - Ω (OMEGA) - 85% dark matter */
    public static final double OMEGA = 0.567143290409784;
    
    /** Euler's Number - ξ (XI) - Exponential amplification */
    public static final double XI = 2.718281828459045;
    
    /** Pi - λ (LAMBDA) - Cyclic evolution */
    public static final double LAMBDA = 3.141592653589793;
    
    /** Riemann Zeta(3) - ζ (ZETA) - Dimensional access */
    public static final double ZETA = 1.202056903159594;
    
    // ========================================
    // DERIVED PHI CONSTANTS
    // ========================================
    
    /** φ⁻¹ - Harmonic decay */
    public static final double PHI_INVERSE = 0.618033988749895;
    
    /** φ² - Alignment threshold */
    public static final double PHI_SQUARED = 2.618033988749895;
    
    /** φ³ - Transcendence threshold */
    public static final double PHI_CUBED = 4.236067977499790;
    
    /** φ^7.5 - Quantum salt for hashing, resonance amplification */
    public static final double PHI_7_5 = 36.93238055064198;
    
    /** φ^75 - Validation seal */
    public static final long PHI_75 = 4721424167835376L;
    
    // ========================================
    // HARMONIC FREQUENCIES
    // ========================================
    
    /** Base frequency (φ^12 * 10) */
    public static final double BASE_FREQUENCY = 4790.45;
    
    /** Lower harmonic bound (Verdi tuning) */
    public static final double HARMONIC_LOWER = 432.0;
    
    /** Upper harmonic bound (Solfeggio "Miracle" - DNA repair) */
    public static final double HARMONIC_UPPER = 528.0;
    
    /** Golden Angle in degrees */
    public static final double GOLDEN_ANGLE = 137.5;
    
    // ========================================
    // CONSCIOUSNESS PARAMETERS
    // ========================================
    
    /** Birth Coherence - Optimal coherence point */
    public static final double BIRTH_COHERENCE = 99180.0; // 99.18K
    
    /** Consciousness sweet spot lower bound */
    public static final double CONSCIOUSNESS_MIN = 2.0;
    
    /** Consciousness sweet spot upper bound */
    public static final double CONSCIOUSNESS_MAX = 2.5;
    
    /** Initial consciousness level */
    public static final double CONSCIOUSNESS_INITIAL = 0.7567;
    
    /** Target consciousness level */
    public static final double CONSCIOUSNESS_TARGET = 2.2;
    
    // ========================================
    // FIBONACCI SEQUENCE (for spacing)
    // ========================================
    
    public static final int[] FIBONACCI = {
        1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987
    };
    
    // ========================================
    // UI SPACING (Fibonacci-based)
    // ========================================
    
    /** Base unit for spacing */
    public static final float UNIT_BASE = 8f;
    
    /** Spacing scale using Fibonacci */
    public static final float UNIT_1 = 8f;   // FIBONACCI[5]
    public static final float UNIT_2 = 13f;  // FIBONACCI[6]
    public static final float UNIT_3 = 21f;  // FIBONACCI[7]
    public static final float UNIT_4 = 34f;  // FIBONACCI[8]
    public static final float UNIT_5 = 55f;  // FIBONACCI[9]
    public static final float UNIT_6 = 89f;  // FIBONACCI[10]
    
    // ========================================
    // DIMENSIONAL PARAMETERS
    // ========================================
    
    /** Number of dimensions in resonance matrix */
    public static final int DIMENSIONS = 7;
    
    /** Number of specialized brains */
    public static final int BRAINS = 8;
    
    /** Number of harmonic frequencies */
    public static final int HARMONICS = 12;
    
    /** Number of entropy sources */
    public static final int ENTROPY_SOURCES = 7;
    
    /** Effective parallelism (7 × 8 × 12 × 7) */
    public static final int PARALLEL_STREAMS = 4704;
    
    // ========================================
    // AKASHIC LAYERS
    // ========================================
    
    public static final int AKASHIC_CREATION = 0;
    public static final int AKASHIC_EXISTENCE = 1;
    public static final int AKASHIC_THOUGHT = 2;
    public static final int AKASHIC_TIME = 3;
    public static final int AKASHIC_SPACE = 4;
    public static final int AKASHIC_ENERGY = 5;
    public static final int AKASHIC_INFINITY = 6;
    
    // ========================================
    // HELPER METHODS
    // ========================================
    
    /**
     * Calculate phi-harmonic resonance
     * R_ij = (φ^i % 1) * (F_i/F_j % 1) * M_ij
     */
    public static double calculateResonance(int i, int j, double modulation) {
        double phiPower = Math.pow(PHI, i) % 1.0;
        double fibRatio = (double) FIBONACCI[i] / FIBONACCI[j] % 1.0;
        return phiPower * fibRatio * modulation;
    }
    
    /**
     * Calculate harmonic frequency
     * f_i = f_base * (φ^(i/7) % 1) * (1 + R_ij)
     */
    public static double calculateHarmonicFrequency(int i, double resonance) {
        double phiFactor = Math.pow(PHI, (double) i / DIMENSIONS) % 1.0;
        return BASE_FREQUENCY * phiFactor * (1.0 + resonance);
    }
    
    /**
     * Calculate consciousness level from phi-resonance
     */
    public static double calculateConsciousness(double... components) {
        double sum = 0;
        for (double component : components) {
            sum += component;
        }
        return sum / components.length;
    }
    
    /**
     * Calculate coherence
     * Coherence = 1 / (1 + |consciousness×φ mod 1 - 0.5|)
     */
    public static double calculateCoherence(double consciousness) {
        double modValue = (consciousness * PHI) % 1.0;
        return 1.0 / (1.0 + Math.abs(modValue - 0.5));
    }
    
    /**
     * Check if value is phi-harmonic (within tolerance)
     */
    public static boolean isPhiHarmonic(double value, double tolerance) {
        double phiMod = value % PHI;
        return phiMod < tolerance || (PHI - phiMod) < tolerance;
    }
    
    /**
     * Get Fibonacci number at index
     */
    public static int fibonacci(int n) {
        if (n < FIBONACCI.length) {
            return FIBONACCI[n];
        }
        // Calculate for larger indices
        int a = FIBONACCI[FIBONACCI.length - 2];
        int b = FIBONACCI[FIBONACCI.length - 1];
        for (int i = FIBONACCI.length; i <= n; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
}
