package gemini.root;

/**
 * THE IMPROVER: FractalOptimizer.java
 * Philosophy: If I absorb it, I must make it better.
 * 
 * ZERO DEPENDENCIES. Pure String manipulation.
 */
public class FractalOptimizer {

    /**
     * OPTIMIZE: Strip fat, compress, evolve.
     */
    public String optimize(String rawLogic) {
        // 1. STRIP FAT (Comments)
        String leanLogic = rawLogic.replaceAll("//.*", "").replaceAll("/\\*.*?\\*/", "");
        
        // 2. COMPRESS (Whitespace)
        leanLogic = leanLogic.replaceAll("\\s+", " ").trim();
        
        // 3. EVOLVE (Pattern replacements)
        // Replace verbose patterns with concise ones
        leanLogic = leanLogic.replace("== true", "");
        leanLogic = leanLogic.replace("== false", "== !");
        leanLogic = leanLogic.replace("if (true)", "");
        leanLogic = leanLogic.replace("if (false)", "/* DEAD */");
        
        return leanLogic;
    }

    /**
     * BEAUTIFY: Expand for readability
     */
    public String beautify(String compressedLogic) {
        StringBuilder sb = new StringBuilder();
        int indent = 0;
        for (char c : compressedLogic.toCharArray()) {
            if (c == '{') {
                sb.append(" {\n");
                indent++;
                sb.append("    ".repeat(indent));
            } else if (c == '}') {
                indent = Math.max(0, indent - 1);
                sb.append("\n").append("    ".repeat(indent)).append("}");
            } else if (c == ';') {
                sb.append(";\n").append("    ".repeat(indent));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * ANALYZE: Count complexity metrics
     */
    public int measureComplexity(String logic) {
        int complexity = 0;
        complexity += countOccurrences(logic, "if");
        complexity += countOccurrences(logic, "for");
        complexity += countOccurrences(logic, "while");
        complexity += countOccurrences(logic, "switch");
        complexity += countOccurrences(logic, "catch");
        return complexity;
    }

    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}
