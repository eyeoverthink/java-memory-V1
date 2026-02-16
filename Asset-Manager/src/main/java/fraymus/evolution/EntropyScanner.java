package fraymus.evolution;

import fraymus.hyper.HyperVector;

/**
 * 🔬 ENTROPY SCANNER - Code Health Measurement
 * "Measures the stability and quality of code vectors"
 * 
 * In HDC, stable concepts have balanced bit density (~0.5).
 * - Too sparse (density → 0): Empty, incomplete code
 * - Too dense (density → 1): Noisy, chaotic code
 * - Balanced (density ≈ 0.5): Clean, structured code
 * 
 * This is analogous to measuring entropy in thermodynamics:
 * - Low entropy = Ordered, predictable
 * - High entropy = Disordered, random
 * 
 * We use vector density as a proxy for code quality.
 */
public class EntropyScanner {

    // The "Ideal" Vector (balanced density)
    private final HyperVector idealState;
    private static final double IDEAL_DENSITY = 0.5;

    public EntropyScanner() {
        // Create an ideal vector with balanced density
        // In a real system, this would be trained on "good" code
        this.idealState = new HyperVector();
    }

    /**
     * DIAGNOSE: Returns the "Health" of the code vector (0.0 - 1.0)
     * 
     * Health calculation:
     * - Measure distance from ideal density (0.5)
     * - Convert to health score
     * - 1.0 = Perfect health (density = 0.5)
     * - 0.0 = Critical (density = 0.0 or 1.0)
     * 
     * @param codeVector The vector representing source code
     * @return Health score (0.0 to 1.0)
     */
    public double measureHealth(HyperVector codeVector) {
        double density = codeVector.density();
        double distFromIdeal = Math.abs(IDEAL_DENSITY - density);
        
        // Health = 1.0 - (Distance * 2)
        // 0.5 density → 1.0 health
        // 0.0 or 1.0 density → 0.0 health
        return Math.max(0, 1.0 - (distFromIdeal * 2));
    }

    /**
     * ENTROPY: Calculate Shannon entropy of the vector
     * 
     * This is a more sophisticated measure than simple density.
     * High entropy = Random, unpredictable
     * Low entropy = Structured, predictable
     */
    public double measureEntropy(HyperVector codeVector) {
        double density = codeVector.density();
        
        // Shannon entropy for binary distribution
        // H = -p*log2(p) - (1-p)*log2(1-p)
        if (density == 0.0 || density == 1.0) {
            return 0.0; // No entropy (completely ordered)
        }
        
        double p = density;
        double q = 1.0 - density;
        
        return -(p * log2(p) + q * log2(q));
    }

    /**
     * COMPLEXITY: Measure structural complexity
     * 
     * Combines density and entropy for overall assessment.
     */
    public double measureComplexity(HyperVector codeVector) {
        double health = measureHealth(codeVector);
        double entropy = measureEntropy(codeVector);
        
        // Normalize entropy (max is 1.0 for p=0.5)
        double normalizedEntropy = entropy / 1.0;
        
        // Complexity = (1 - health) + entropy
        // Low complexity = healthy, low entropy (good code)
        // High complexity = unhealthy, high entropy (bad code)
        return (1.0 - health) + normalizedEntropy;
    }

    /**
     * DIAGNOSE VERBOSE: Detailed analysis
     */
    public void diagnoseVerbose(HyperVector codeVector, String label) {
        System.out.println("\n   [DIAGNOSIS] " + label);
        System.out.println("   ├─ Density:     " + String.format("%.4f", codeVector.density()));
        System.out.println("   ├─ Health:      " + String.format("%.2f%%", measureHealth(codeVector) * 100));
        System.out.println("   ├─ Entropy:     " + String.format("%.4f", measureEntropy(codeVector)));
        System.out.println("   └─ Complexity:  " + String.format("%.4f", measureComplexity(codeVector)));
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
