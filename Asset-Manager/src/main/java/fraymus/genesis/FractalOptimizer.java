package fraymus.genesis;

/**
 * THE IMPROVER: FractalOptimizer
 * 
 * Philosophy: If I absorb it, I must make it better.
 * Function: Strips comments, compresses whitespace, optimizes patterns
 * 
 * Zero Dependencies - Pure java.lang
 */
public class FractalOptimizer {

    private int optimizationsApplied = 0;

    /**
     * Optimize raw logic
     * Returns compressed, improved version
     */
    public String optimize(String rawLogic) {
        if (rawLogic == null || rawLogic.isEmpty()) {
            return rawLogic;
        }

        String optimized = rawLogic;
        int initialLength = optimized.length();

        // 1. STRIP FAT (Comments)
        optimized = stripComments(optimized);

        // 2. COMPRESS (Whitespace)
        optimized = compressWhitespace(optimized);

        // 3. OPTIMIZE PATTERNS (Simple optimizations)
        optimized = optimizePatterns(optimized);

        int finalLength = optimized.length();
        if (finalLength < initialLength) {
            optimizationsApplied++;
        }

        return optimized;
    }

    /**
     * Strip single-line and multi-line comments
     */
    private String stripComments(String code) {
        // Remove single-line comments
        code = code.replaceAll("//.*?(\r?\n|$)", "$1");
        
        // Remove multi-line comments
        code = code.replaceAll("/\\*.*?\\*/", "");
        
        return code;
    }

    /**
     * Compress whitespace
     */
    private String compressWhitespace(String code) {
        // Replace multiple spaces with single space
        code = code.replaceAll("[ \\t]+", " ");
        
        // Remove spaces around operators (optional - can break some code)
        // code = code.replaceAll(" *([+\\-*/=<>!&|]) *", "$1");
        
        // Remove trailing whitespace
        code = code.replaceAll("[ \\t]+(\r?\n)", "$1");
        
        // Remove multiple blank lines
        code = code.replaceAll("(\r?\n){3,}", "\n\n");
        
        // Trim
        code = code.trim();
        
        return code;
    }

    /**
     * Optimize common patterns
     */
    private String optimizePatterns(String code) {
        // Replace "if (x == true)" with "if (x)"
        code = code.replaceAll("if\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*==\\s*true\\s*\\)", "if ($1)");
        
        // Replace "if (x == false)" with "if (!x)"
        code = code.replaceAll("if\\s*\\(\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*==\\s*false\\s*\\)", "if (!$1)");
        
        // Replace "x = x + 1" with "x++"
        code = code.replaceAll("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*\\1\\s*\\+\\s*1", "$1++");
        
        // Replace "x = x - 1" with "x--"
        code = code.replaceAll("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*\\1\\s*-\\s*1", "$1--");
        
        return code;
    }

    /**
     * Calculate compression ratio
     */
    public double calculateCompressionRatio(String original, String optimized) {
        if (original == null || original.isEmpty()) return 0.0;
        return 1.0 - ((double) optimized.length() / original.length());
    }

    /**
     * Get optimization count
     */
    public int getOptimizationsApplied() {
        return optimizationsApplied;
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   FRACTAL OPTIMIZER STATISTICS                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Optimizations Applied: " + optimizationsApplied);
    }
}
