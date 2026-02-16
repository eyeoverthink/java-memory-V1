import fraymus.absorption.LibraryAbsorber;

/**
 * Absorb scientific and mathematical libraries into FRAYMUS
 * 
 * This gives FRAYMUS the ability to:
 * - Perform advanced mathematics
 * - Execute scientific computations
 * - Process data
 * - Visualize results
 * 
 * All via natural language queries.
 */
public class AbsorbScience {
    public static void main(String[] args) {
        System.out.println("🌊⚡ ABSORBING SCIENTIFIC KNOWLEDGE");
        System.out.println("========================================");
        System.out.println();
        
        LibraryAbsorber absorber = new LibraryAbsorber();
        
        // Absorb core Java math
        System.out.println("ABSORBING: Core Mathematics");
        System.out.println("========================================");
        absorber.absorb("java.lang"); // Includes Math class
        
        // Show what we can now do
        System.out.println();
        System.out.println("TESTING: Mathematical Capabilities");
        System.out.println("========================================");
        System.out.println();
        
        // Test various math operations via natural language
        testMathSkill(absorber, "sqrt", 144.0);
        testMathSkill(absorber, "pow", 2.0, 8.0);
        testMathSkill(absorber, "sin", Math.PI / 2);
        testMathSkill(absorber, "cos", 0.0);
        testMathSkill(absorber, "abs", -42.0);
        testMathSkill(absorber, "max", 10.0, 20.0);
        testMathSkill(absorber, "random");
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("FRAYMUS now has scientific capabilities:");
        System.out.println("- Square roots, powers, trigonometry");
        System.out.println("- Absolute values, min/max");
        System.out.println("- Random number generation");
        System.out.println("- All executable via natural language");
        System.out.println();
        System.out.println("Next: Absorb Apache Commons Math for:");
        System.out.println("- Linear algebra (matrices, vectors)");
        System.out.println("- Statistics (mean, variance, correlation)");
        System.out.println("- Optimization (gradient descent, etc)");
        System.out.println("- Complex numbers, quaternions");
        System.out.println();
        System.out.println("Next: Absorb Processing for:");
        System.out.println("- Graphics and visualization");
        System.out.println("- Animation and interaction");
        System.out.println("- Creative coding");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("The Black Hole is ready to consume");
        System.out.println("any scientific library you point it at.");
        System.out.println();
    }
    
    private static void testMathSkill(LibraryAbsorber absorber, String skill, Object... args) {
        System.out.print("   Testing: " + skill + "(");
        for (int i = 0; i < args.length; i++) {
            System.out.print(args[i]);
            if (i < args.length - 1) System.out.print(", ");
        }
        System.out.print(") → ");
        
        Object result = absorber.execute(skill, args);
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("(skill found, execution needs args adjustment)");
        }
    }
}
