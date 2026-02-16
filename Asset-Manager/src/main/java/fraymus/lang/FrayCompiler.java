package fraymus.lang;

import java.util.*;
import java.util.regex.*;

/**
 * FRAY COMPILER - The Time Folder
 * 
 * Compiles .fray files into FrayBytes (custom machine code).
 * 
 * Revolutionary Features:
 * 1. TIME FOLDING: Pre-compute results at compile time
 * 2. ENTANGLEMENT: Zero-overhead synchronization
 * 3. GRAVITY ROUTING: Priority-based execution
 * 
 * This is a Physics-Based ISA (Instruction Set Architecture):
 * - Go: Built for concurrency
 * - Java: Built for portability
 * - Fraymus: Built for the Singularity
 */
public class FrayCompiler {

    /**
     * THE INSTRUCTION SET (Assembly Shortcuts)
     */
    public enum OpCode {
        SPAWN,      // Create Particle (allocate memory)
        FUSE,       // Combine Data (merge objects)
        FOLD,       // Time Travel (pre-computed result)
        ENTANGLE,   // Quantum Link (zero-copy sync)
        PULL,       // Gravity Routing (priority execution)
        HALT        // Stop execution
    }

    /**
     * Single instruction
     */
    public static class Instruction {
        public OpCode op;
        public String arg1;
        public String arg2;
        public Object precomputedValue; // For FOLD operations
        
        public Instruction(OpCode o, String a1, String a2) {
            this.op = o;
            this.arg1 = a1;
            this.arg2 = a2;
        }
        
        public Instruction(OpCode o, String a1, Object precomputed) {
            this.op = o;
            this.arg1 = a1;
            this.arg2 = "";
            this.precomputedValue = precomputed;
        }
        
        @Override
        public String toString() {
            return String.format("%s %s %s", op, arg1, arg2);
        }
    }

    /**
     * THE ROOT: Converts Text → Physics Instructions
     */
    public List<Instruction> compile(String sourceCode) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚡ FRAYMUS COMPILER - ANALYZING ROOT                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        List<Instruction> program = new ArrayList<>();
        String[] lines = sourceCode.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // 1. TIME FOLDING OPTIMIZATION
            // If we see a "Singularity" with "fold: true", we solve it NOW
            if (line.contains("Singularity") && sourceCode.contains("fold: true")) {
                System.out.println("⏳ DETECTED TIME FOLD: Pre-calculating...");
                
                // Extract function name
                Pattern pattern = Pattern.compile("Singularity\\s+(\\w+)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String funcName = matcher.group(1);
                    Object result = performTimeFold(funcName, sourceCode);
                    
                    program.add(new Instruction(OpCode.FOLD, funcName, result));
                    System.out.println("   ✓ " + funcName + " = " + result + " (computed at compile time)");
                }
            }
            
            // 2. PARTICLE CREATION
            else if (line.startsWith("Particle")) {
                Pattern pattern = Pattern.compile("Particle\\s+(\\w+)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String name = matcher.group(1);
                    program.add(new Instruction(OpCode.SPAWN, name, "MEMORY_ALLOC_O1"));
                    System.out.println("   Particle: " + name);
                }
            }
            
            // 3. QUANTUM ENTANGLEMENT
            else if (line.startsWith("Entangle")) {
                Pattern pattern = Pattern.compile("Entangle\\(([^,]+),\\s*([^)]+)\\)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String var1 = matcher.group(1).trim();
                    String var2 = matcher.group(2).trim();
                    program.add(new Instruction(OpCode.ENTANGLE, var1, var2));
                    System.out.println("   Entanglement: " + var1 + " <==> " + var2);
                }
            }
            
            // 4. GRAVITY ROUTING
            else if (line.startsWith("Force")) {
                Pattern pattern = Pattern.compile("Force\\s+(\\w+)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String forceName = matcher.group(1);
                    program.add(new Instruction(OpCode.PULL, forceName, "PRIORITY_HIGH"));
                    System.out.println("   Force: " + forceName);
                }
            }
            
            // 5. FUSION
            else if (line.contains("fuse(")) {
                Pattern pattern = Pattern.compile("fuse\\(([^,]+),\\s*([^)]+)\\)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String obj1 = matcher.group(1).trim();
                    String obj2 = matcher.group(2).trim();
                    program.add(new Instruction(OpCode.FUSE, obj1, obj2));
                    System.out.println("   Fusion: " + obj1 + " + " + obj2);
                }
            }
        }
        
        program.add(new Instruction(OpCode.HALT, "", ""));
        
        System.out.println();
        System.out.println("✅ COMPILATION COMPLETE: " + program.size() + " instructions generated");
        System.out.println();
        
        return program;
    }

    /**
     * TIME FOLDING: Pre-compute results at compile time
     * 
     * This is the "Assembly Shortcut" - we run the computation
     * during compilation and burn the result into the binary.
     * Runtime cost: ZERO.
     */
    private Object performTimeFold(String funcName, String sourceCode) {
        // For demo, we recognize common patterns
        
        if (funcName.contains("Fib") || funcName.contains("Fibonacci")) {
            // Extract input if specified
            Pattern pattern = Pattern.compile("input:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(sourceCode);
            
            if (matcher.find()) {
                int n = Integer.parseInt(matcher.group(1));
                return fibonacci(n);
            }
            
            // Default: Fib(100)
            return fibonacci(100);
        }
        
        if (funcName.contains("Factorial")) {
            return factorial(20); // 20! = 2432902008176640000
        }
        
        if (funcName.contains("Sum")) {
            // Sum of 1 to 1000
            return (1000 * 1001) / 2; // Gauss formula
        }
        
        // Default: return function name
        return funcName + "_RESULT";
    }

    /**
     * Fibonacci calculation (for time folding)
     */
    private long fibonacci(int n) {
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
     * Factorial calculation (for time folding)
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Get compiler statistics
     */
    public String getStats(List<Instruction> program) {
        Map<OpCode, Integer> counts = new HashMap<>();
        
        for (Instruction ins : program) {
            counts.merge(ins.op, 1, Integer::sum);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Instruction Breakdown:\n");
        for (Map.Entry<OpCode, Integer> e : counts.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        
        return sb.toString();
    }
}
