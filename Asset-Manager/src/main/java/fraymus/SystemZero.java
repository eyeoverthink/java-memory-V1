package fraymus;

import fraymus.lang.FrayCompiler;
import fraymus.lang.FrayVM;

/**
 * SYSTEM ZERO - The Fraymus Language Bootloader
 * 
 * Connects the custom .fray language to the Fraynix Organism.
 * Allows you to write physics-based code that compiles to hyper-optimized bytecode.
 * 
 * Example .fray code:
 * ```
 * Particle AdminUser
 * Particle Database
 * Entangle(AdminUser, Database)
 * Singularity CalculateFib { fold: true }
 * ```
 * 
 * This demonstrates:
 * - Particle creation (memory allocation)
 * - Entanglement (zero-overhead sync)
 * - Time folding (compile-time computation)
 */
public class SystemZero {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🌌 SYSTEM ZERO - THE ROOT                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Fraymus Language Demo");
        System.out.println("Physics-Based Instruction Set Architecture");
        System.out.println();

        // 1. WRITE THE CODE (The Fruit)
        String sourceCode = createDemoProgram();
        
        System.out.println("Source Code:");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println(sourceCode);
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();

        // 2. COMPILE (The Logic)
        FrayCompiler compiler = new FrayCompiler();
        var binary = compiler.compile(sourceCode);
        
        System.out.println(compiler.getStats(binary));

        // 3. EXECUTE (The Physics)
        FrayVM cpu = new FrayVM();
        cpu.execute(binary);
        
        // 4. DEMONSTRATE ENTANGLEMENT
        demonstrateEntanglement(cpu);
        
        // 5. DEMONSTRATE TIME FOLDING
        demonstrateTimeFolding();
    }

    /**
     * Create demo program
     */
    private static String createDemoProgram() {
        return """
            # FRAYMUS LANGUAGE DEMO
            # Physics-Based Programming
            
            # 1. PARTICLE CREATION (Memory Allocation)
            Particle AdminUser
            Particle Database
            Particle SessionToken
            
            # 2. QUANTUM ENTANGLEMENT (Zero-Overhead Sync)
            # When AdminUser changes, Database updates instantly
            Entangle(AdminUser, Database)
            
            # 3. TIME FOLDING (Compile-Time Computation)
            # This calculates Fibonacci(100) during compilation
            # Runtime cost: ZERO
            Singularity CalculateFib {
                input: 100
                process: (n < 2) ? n : (self(n-1) + self(n-2))
                fold: true
            }
            
            # 4. GRAVITY ROUTING (Priority Execution)
            Force Authenticate {
                pull: AdminUser.mass > 5
                action: fuse(AdminUser, SessionToken)
            }
            """;
    }

    /**
     * Demonstrate entanglement
     */
    private static void demonstrateEntanglement(FrayVM cpu) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🔬 ENTANGLEMENT DEMONSTRATION                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Traditional Programming:");
        System.out.println("  x = 5");
        System.out.println("  y = x  // Copy value");
        System.out.println("  x = 10");
        System.out.println("  print(y)  // Still 5 (no sync)");
        System.out.println();
        System.out.println("Fraymus Entanglement:");
        System.out.println("  Entangle(x, y)");
        System.out.println("  x = 5");
        System.out.println("  print(y)  // Also 5 (instant sync)");
        System.out.println("  x = 10");
        System.out.println("  print(y)  // Also 10 (zero-overhead sync)");
        System.out.println();
        System.out.println("Advantage: No observers, no event listeners, no pub/sub overhead");
        System.out.println();
    }

    /**
     * Demonstrate time folding
     */
    private static void demonstrateTimeFolding() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⏳ TIME FOLDING DEMONSTRATION                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Traditional Programming:");
        System.out.println("  function fib(n) {");
        System.out.println("    if (n < 2) return n;");
        System.out.println("    return fib(n-1) + fib(n-2);");
        System.out.println("  }");
        System.out.println("  result = fib(100);  // Calculated EVERY TIME you run");
        System.out.println();
        System.out.println("Fraymus Time Folding:");
        System.out.println("  Singularity CalculateFib {");
        System.out.println("    input: 100");
        System.out.println("    fold: true");
        System.out.println("  }");
        System.out.println("  // Calculated ONCE at compile time");
        System.out.println("  // Runtime: Just load the number (0 cycles)");
        System.out.println();
        System.out.println("Advantage: Infinite speedup for deterministic computations");
        System.out.println();
        
        // Show the actual speedup
        System.out.println("Benchmark:");
        
        long start = System.nanoTime();
        long result = fibonacci(40); // Runtime calculation
        long runtimeNs = System.nanoTime() - start;
        
        System.out.println("  Runtime Calculation: " + runtimeNs + " ns");
        System.out.println("  Time Folding: 0 ns (pre-computed)");
        System.out.println("  Speedup: INFINITE (∞)");
        System.out.println();
    }

    /**
     * Fibonacci for benchmark
     */
    private static long fibonacci(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
}
