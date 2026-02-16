package gemini.root;

/**
 * PHI-HARMONIC MATHEMATICS
 * 
 * The golden ratio governs transformations.
 * φ = 1.618033988749895
 * φ² = φ + 1
 * 1/φ = φ - 1
 */
public class PhiMath {

    public static final double PHI = 1.618033988749895;
    public static final double PHI_INVERSE = 0.618033988749895;
    public static final double PHI_SQUARED = 2.618033988749895;

    /**
     * Scale value by PHI.
     */
    public static double phiScale(double v) {
        return v * PHI;
    }

    /**
     * Inverse PHI scale.
     */
    public static double phiInverse(double v) {
        return v * PHI_INVERSE;
    }

    /**
     * Fibonacci sequence generator.
     */
    public static long[] fibonacci(int n) {
        if (n <= 0) return new long[0];
        if (n == 1) return new long[]{0};

        long[] fib = new long[n];
        fib[0] = 0;
        fib[1] = 1;
        for (int i = 2; i < n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        return fib;
    }

    /**
     * Check if ratio approximates PHI.
     */
    public static boolean isPhiRatio(double a, double b, double tolerance) {
        if (b == 0) return false;
        double ratio = a / b;
        return Math.abs(ratio - PHI) < tolerance;
    }

    /**
     * Golden angle in radians (used in nature's spirals).
     */
    public static double goldenAngle() {
        return Math.PI * (3 - Math.sqrt(5)); // ~137.5 degrees
    }

    /**
     * PHI-harmonic temperature for LLM calls.
     * 0.618 = balanced creativity/precision
     */
    public static double phiTemperature() {
        return PHI_INVERSE;
    }

    /**
     * Generate PHI spiral coordinates.
     */
    public static double[][] phiSpiral(int points, double scale) {
        double[][] coords = new double[points][2];
        double angle = goldenAngle();

        for (int i = 0; i < points; i++) {
            double r = scale * Math.sqrt(i);
            double theta = i * angle;
            coords[i][0] = r * Math.cos(theta);
            coords[i][1] = r * Math.sin(theta);
        }
        return coords;
    }

    /**
     * PHI-based hash mixing (for fingerprints).
     */
    public static long phiMix(long a, long b) {
        return (long) ((a * PHI) + (b * PHI_INVERSE));
    }
}
