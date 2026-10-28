package fraymus.facade;

// SELFBUILD_TARGET Asset-Manager/src/main/java/fraymus/facade/SelfCodeEngine.java
// SELFBUILD_GOAL invite human review from session page
// SELFBUILD_FINGERPRINT φ-281F2043CEED0

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SELF CODE ENGINE - Abstracted from SelfCodePanel (ImGui-free)
 * 
 * Pure logic: phi-transform, mutate, validate, inject, store.
 * No UI dependencies. CLI-native.
 * 
 * φ^∞ © 2026 Vaughn Scott
 */
public class SelfCodeEngine {

    private static final double PHI = PhiQuantumConstants.PHI;

    private final Map<String, String> knowledgeStore = new ConcurrentHashMap<>();
    private final List<String> injectedCode = new ArrayList<>();
    private String lastEvolved = "";

    // ═══════════════════════════════════════════════════════════════════
    // PHI-TRANSFORM (Pure String → String, Zero Dependencies)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Apply phi-harmonic transformation to code.
     * Injects φ-gate annotations and phi-scales numeric constants.
     */
    public String phiTransform(String code) {
        if (code == null || code.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        String[] lines = code.split("\n");

        result.append("// φ-TRANSFORMED | PHI=").append(PHI)
              .append(" | ").append(System.currentTimeMillis()).append("\n");

        for (String line : lines) {
            if (line.contains("class ")) {
                result.append("// ABSTRACTION_GATE [φ^1]\n");
            } else if (line.contains("if ") || line.contains("else")) {
                result.append("// CONDITIONAL_GATE [φ^2]\n");
            } else if (line.contains("for ") || line.contains("while ")) {
                result.append("// ITERATION_GATE [φ^3]\n");
            }

            String outputLine = line;
            if (line.matches(".*\\d+.*") && !line.trim().startsWith("//")) {
                outputLine = line.replaceAll("(\\d+)", "(int)($1 * PHI)");
            }
            result.append(outputLine).append("\n");
        }

        lastEvolved = result.toString();
        return lastEvolved;
    }

    // ═══════════════════════════════════════════════════════════════════
    // MUTATION (Stochastic φ-injection)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Random mutation: inject phi constants into assignment statements.
     * Mutation chance = 1/φ (~61.8%).
     */
    public String mutate(String code) {
        if (code == null || code.isEmpty()) return "";

        StringBuilder mutated = new StringBuilder();
        String[] lines = code.split("\n");
        int mutations = 0;

        for (String line : lines) {
            double mutationChance = 1.0 / PHI;
            if (Math.random() < mutationChance && line.contains("=")) {
                line = line.replace("=", "= (int)(PHI * ") + ")";
                mutations++;
            }
            mutated.append(line).append("\n");
        }

        lastEvolved = mutated.toString();
        System.out.println("   [SELFCODE] " + mutations + " mutations applied (p=1/φ)");
        return lastEvolved;
    }

    // ═══════════════════════════════════════════════════════════════════
    // VALIDATION (Syntax Checking)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Validate code syntax: balanced braces, parens, brackets.
     * Returns human-readable report.
     */
    public String validate(String code) {
        if (code == null || code.isEmpty()) return "   [!] No code to validate.";

        int braces = 0, parens = 0, brackets = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') braces++;
            if (c == '}') braces--;
            if (c == '(') parens++;
            if (c == ')') parens--;
            if (c == '[') brackets++;
            if (c == ']') brackets--;
        }

        boolean hasClass = code.contains("class ");
        boolean hasMethod = code.contains("(") && code.contains(")");

        StringBuilder sb = new StringBuilder();
        sb.append("   [VALIDATE] Syntax Report\n");
        sb.append("     Braces {}:   ").append(braces == 0 ? "✓ balanced" : "✗ off by " + braces).append("\n");
        sb.append("     Parens ():   ").append(parens == 0 ? "✓ balanced" : "✗ off by " + parens).append("\n");
        sb.append("     Brackets []: ").append(brackets == 0 ? "✓ balanced" : "✗ off by " + brackets).append("\n");
        sb.append("     Has class:   ").append(hasClass ? "✓" : "—").append("\n");
        sb.append("     Has method:  ").append(hasMethod ? "✓" : "—").append("\n");
        sb.append("     Verdict:     ").append(braces == 0 && parens == 0 && brackets == 0 ? "VALID" : "ISSUES FOUND");
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // KNOWLEDGE STORE (Inject / Store / Link)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Inject code into the knowledge DNA.
     */
    public void inject(String code) {
        if (code == null || code.isEmpty()) return;
        String key = "INJECT_" + System.currentTimeMillis();
        injectedCode.add(code);
        knowledgeStore.put(key, code);
        System.out.println("   [SELFCODE] Injected " + code.length() + " chars → " + key);
    }

    /**
     * Store content with tags.
     */
    public void store(String content, String tags) {
        if (content == null || content.isEmpty()) return;
        String tagStr = (tags == null || tags.isEmpty()) ? "CODE" : tags;
        String key = tagStr + "_" + System.currentTimeMillis();
        knowledgeStore.put(key, content);
        System.out.println("   [SELFCODE] Stored → " + key + " [" + tagStr + "]");
    }

    /**
     * Generate concept fingerprint from content.
     */
    public String linkConcept(String content) {
        if (content == null || content.isEmpty()) return "φ-0";
        long hash = content.hashCode() * (long)(PHI * 1000000);
        return "φ-" + Long.toHexString(Math.abs(hash)).toUpperCase();
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════

    public Map<String, String> getKnowledgeStore() { return knowledgeStore; }
    public List<String> getInjectedCode() { return injectedCode; }
    public String getLastEvolved() { return lastEvolved; }

    public String getStatus() {
        return String.format(
            "═══════════════════════════════════════════\n" +
            "  φ SELF CODE ENGINE STATUS\n" +
            "═══════════════════════════════════════════\n" +
            "  Knowledge Entries: %d\n" +
            "  Injected Snippets: %d\n" +
            "  Last Evolved:      %s\n" +
            "  φ Constant:        %.15f\n",
            knowledgeStore.size(),
            injectedCode.size(),
            lastEvolved.isEmpty() ? "(none)" : lastEvolved.split("\n").length + " lines",
            PHI
        );
    }
}
