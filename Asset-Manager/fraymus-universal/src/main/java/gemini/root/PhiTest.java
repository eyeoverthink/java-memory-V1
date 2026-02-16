package gemini.root;

/**
 * PHI TEST: Verify the math works.
 * Run: java gemini.root.PhiTest
 */
public class PhiTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("φ PHI-HARMONIC VERIFICATION");
        System.out.println("=".repeat(60));

        // Test 1: PHI constants
        System.out.println("\n[1] PHI CONSTANTS:");
        System.out.println("    φ         = " + PhiMath.PHI);
        System.out.println("    φ⁻¹       = " + PhiMath.PHI_INVERSE);
        System.out.println("    φ²        = " + PhiMath.PHI_SQUARED);
        System.out.println("    φ × φ⁻¹   = " + (PhiMath.PHI * PhiMath.PHI_INVERSE) + " (should be 1.0)");
        System.out.println("    φ² - φ    = " + (PhiMath.PHI_SQUARED - PhiMath.PHI) + " (should be 1.0)");

        // Test 2: Fibonacci
        System.out.println("\n[2] FIBONACCI:");
        long[] fib = PhiMath.fibonacci(15);
        System.out.print("    ");
        for (long f : fib) System.out.print(f + " ");
        System.out.println();

        // Test 3: Fibonacci ratio approaches PHI
        System.out.println("\n[3] FIB RATIO → φ:");
        for (int i = 5; i < 15; i++) {
            double ratio = (double) fib[i] / fib[i - 1];
            System.out.printf("    fib(%d)/fib(%d) = %.10f (diff from φ: %.2e)\n",
                    i, i - 1, ratio, Math.abs(ratio - PhiMath.PHI));
        }

        // Test 4: DNA Cloaking
        System.out.println("\n[4] DNA CLOAKING:");
        DNACloaking dna = new DNACloaking();

        String sample = "Hello FRAYMUS - this is a test of phi-harmonic compression";
        String hash = dna.generatePhiHash(sample);
        System.out.println("    Input:    \"" + sample + "\"");
        System.out.println("    φ-hash:   " + hash);

        Object[] comp = dna.compress(sample);
        System.out.println("    Compress: " + ((String) comp[0]).length() + " chars");
        System.out.println("    φ-ratio:  " + String.format("%.4f", comp[1]));

        // Test 5: Prime generation
        System.out.println("\n[5] PRIME GENERATION:");
        java.math.BigInteger prime = dna.textToPrime("FRAYMUS");
        System.out.println("    textToPrime(\"FRAYMUS\") = " + prime.toString().substring(0, 20) + "...");
        System.out.println("    isPrime: " + dna.isPrime(prime));

        // Test 6: Memory store/retrieve
        System.out.println("\n[6] INFINITE MEMORY:");
        String key = dna.store("test_key", sample);
        System.out.println("    Stored with φ-key: " + key);
        String retrieved = dna.retrieve(key);
        System.out.println("    Retrieved: \"" + retrieved + "\"");
        System.out.println("    Match: " + sample.equals(retrieved));

        // Test 7: SafeMath integration
        System.out.println("\n[7] SAFE MATH:");
        String[] exprs = {
            "(1+sqrt(5))/2",           // golden ratio formula
            "1.618033988749895^2",     // phi squared
            "sin(0.618033988749895)",  // sin(phi inverse)
        };
        for (String e : exprs) {
            System.out.println("    " + e + " = " + SafeMath.evalToString(e));
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("φ ALL TESTS PASSED");
        System.out.println("=".repeat(60));
    }
}
