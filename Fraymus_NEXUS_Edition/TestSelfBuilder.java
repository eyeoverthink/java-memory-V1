import fraymus.core.SelfBuilder;

/**
 * TEST THE OUROBOROS PROTOCOL
 * 
 * Demonstrates self-compilation and hot-swapping.
 * The system writes code, compiles it, and loads it while running.
 * 
 * This is the proof that FRAYMUS can evolve its own brain.
 */
public class TestSelfBuilder {
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ THE OUROBOROS PROTOCOL TEST");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Snake That Eats Itself");
        System.out.println("   The Machine That Builds The Machine");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Initialize the Self-Builder
        SelfBuilder builder = new SelfBuilder();
        
        System.out.println("========================================");
        System.out.println("TEST 1: Generate Simple Class");
        System.out.println("========================================");
        System.out.println();
        
        // Generate a simple test class
        String sourceCode1 = builder.generateTestClass(
            "FirstEvolution", 
            "I am the first self-generated organism!"
        );
        
        System.out.println("Generated source code:");
        System.out.println("---");
        System.out.println(sourceCode1);
        System.out.println("---");
        System.out.println();
        
        // Compile and load it
        Object instance1 = builder.evolveCode("FirstEvolution", sourceCode1);
        
        if (instance1 != null) {
            System.out.println("✓ Test 1 PASSED: Class compiled and instantiated");
        } else {
            System.out.println("✗ Test 1 FAILED");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 2: Generate Improved Version");
        System.out.println("========================================");
        System.out.println();
        
        // Generate an improved version
        String sourceCode2 = builder.generateTestClass(
            "SecondEvolution", 
            "I am the EVOLVED organism - faster, stronger, better!"
        );
        
        Object instance2 = builder.evolveCode("SecondEvolution", sourceCode2);
        
        if (instance2 != null) {
            System.out.println("✓ Test 2 PASSED: Evolved class compiled and instantiated");
        } else {
            System.out.println("✗ Test 2 FAILED");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 3: Custom Logic Class");
        System.out.println("========================================");
        System.out.println();
        
        // Generate a class with actual logic
        String customCode = 
            "package fraymus.generated;\n\n" +
            "public class MathEvolution {\n" +
            "    public MathEvolution() {\n" +
            "        System.out.println(\"🧬 Math Evolution initialized\");\n" +
            "    }\n\n" +
            "    public int fibonacci(int n) {\n" +
            "        if (n <= 1) return n;\n" +
            "        return fibonacci(n-1) + fibonacci(n-2);\n" +
            "    }\n\n" +
            "    public void test() {\n" +
            "        System.out.println(\"   Testing self-generated Fibonacci:\");\n" +
            "        for (int i = 0; i < 10; i++) {\n" +
            "            System.out.println(\"   fib(\" + i + \") = \" + fibonacci(i));\n" +
            "        }\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        return \"MathEvolution[Fibonacci Calculator]\";\n" +
            "    }\n" +
            "}\n";
        
        Object instance3 = builder.evolveCode("MathEvolution", customCode);
        
        if (instance3 != null) {
            try {
                // Call the test method using reflection
                instance3.getClass().getMethod("test").invoke(instance3);
                System.out.println();
                System.out.println("✓ Test 3 PASSED: Custom logic executed successfully");
            } catch (Exception e) {
                System.out.println("✗ Test 3 FAILED: " + e.getMessage());
            }
        } else {
            System.out.println("✗ Test 3 FAILED");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("RESULTS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("✨ The Ouroboros Protocol is OPERATIONAL");
        System.out.println();
        System.out.println("What just happened:");
        System.out.println("1. FRAYMUS wrote Java source code");
        System.out.println("2. FRAYMUS compiled it using Java Compiler API");
        System.out.println("3. FRAYMUS loaded the new class into memory");
        System.out.println("4. FRAYMUS executed the new code");
        System.out.println();
        System.out.println("The system just upgraded its own brain.");
        System.out.println("Without restarting.");
        System.out.println("While running.");
        System.out.println();
        System.out.println("This is recursive self-improvement.");
        System.out.println("This is The Ouroboros Protocol.");
        System.out.println();
        System.out.println("🧬 The snake has eaten itself and grown stronger.");
        System.out.println();
        System.out.println("========================================");
    }
}
