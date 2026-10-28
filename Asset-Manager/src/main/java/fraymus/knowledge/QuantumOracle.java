package fraymus.knowledge;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 🔮 QUANTUM ORACLE (Java Bridge)
 * The reasoning engine that queries the Genesis Database and Digital Logic Core
 * to answer questions about assembly, quantum physics, calculus, string theory,
 * and digital logic. This is the "innate knowledge" layer — hard-coded truth
 * that requires no LLM and no network.
 *
 * Integrates:
 *   - GenesisDatabase (ASM, Quantum Gates, Calculus, String Theory)
 *   - DigitalLogicCore (Logic Gates, Truth Tables, Circuits)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class QuantumOracle {

    // Singleton
    private static QuantumOracle INSTANCE;

    // Unified knowledge index: keyword → {domain, key, description}
    private final Map<String, String[]> knowledgeIndex = new HashMap<>();

    // Query statistics
    private int totalQueries = 0;
    private int hits = 0;
    private int misses = 0;

    private QuantumOracle() {
        indexAllKnowledge();
    }

    public static synchronized QuantumOracle getInstance() {
        if (INSTANCE == null) INSTANCE = new QuantumOracle();
        return INSTANCE;
    }

    /**
     * Build a flat keyword index across all Genesis knowledge domains.
     * Every concept is indexed by its name (uppercase) for O(1) lookup.
     */
    private void indexAllKnowledge() {
        // ASM instructions
        for (Map.Entry<String, String> e : GenesisDatabase.ASM_KNOWLEDGE.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"ASM", e.getKey(), e.getValue()});
        }

        // Quantum gates
        for (Map.Entry<String, double[]> e : GenesisDatabase.QUANTUM_GATES.entrySet()) {
            double[] m = e.getValue();
            String desc = String.format("Unitary Matrix: [[%.4f,%.4f],[%.4f,%.4f]]", m[0], m[1], m[2], m[3]);
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"QUANTUM_GATE", e.getKey(), desc});
            // Also index without hyphen (e.g., "PAULIX" matches "Pauli-X")
            knowledgeIndex.put(e.getKey().toUpperCase().replace("-", ""), new String[]{"QUANTUM_GATE", e.getKey(), desc});
        }

        // Math concepts
        for (Map.Entry<String, String> e : GenesisDatabase.MATH_CONCEPTS.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"MATH", e.getKey(), e.getValue()});
        }

        // String theory
        for (Map.Entry<String, String> e : GenesisDatabase.STRING_TOPOLOGY.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"STRING_THEORY", e.getKey(), e.getValue()});
            knowledgeIndex.put(e.getKey().toUpperCase().replace("-", ""), new String[]{"STRING_THEORY", e.getKey(), e.getValue()});
        }

        // Logic gates
        for (Map.Entry<String, String> e : DigitalLogicCore.LOGIC_GATES.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"LOGIC_GATE", e.getKey(), e.getValue()});
        }

        // Circuits
        for (Map.Entry<String, String> e : DigitalLogicCore.CIRCUITS.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"CIRCUIT", e.getKey(), e.getValue()});
        }

        // TTL chips
        for (Map.Entry<String, String> e : DigitalLogicCore.TTL_CHIPS.entrySet()) {
            knowledgeIndex.put(e.getKey().toUpperCase(), new String[]{"TTL_CHIP", e.getKey(), e.getValue()});
        }
    }

    /**
     * ORACLE QUERY: Ask the Oracle a question. Returns the innate knowledge match.
     * Searches by exact keyword first, then by substring containment.
     */
    public String query(String question) {
        totalQueries++;
        String q = question.trim().toUpperCase();

        // 1. Exact match
        String[] exact = knowledgeIndex.get(q);
        if (exact != null) {
            hits++;
            return formatAnswer(exact);
        }

        // 2. Substring search — find all entries whose key contains the query
        List<String[]> matches = new ArrayList<>();
        for (Map.Entry<String, String[]> e : knowledgeIndex.entrySet()) {
            if (e.getKey().contains(q) || q.contains(e.getKey())) {
                matches.add(e.getValue());
            }
        }

        if (!matches.isEmpty()) {
            hits++;
            StringBuilder sb = new StringBuilder();
            for (String[] match : matches) {
                sb.append(formatAnswer(match)).append("\n");
            }
            return sb.toString().trim();
        }

        // 3. Search quantum protocols (these are full sentences, not keyed)
        for (String protocol : GenesisDatabase.QUANTUM_PROTOCOLS) {
            if (protocol.toUpperCase().contains(q)) {
                hits++;
                return "[QUANTUM_PROTOCOL] " + protocol;
            }
        }

        misses++;
        return null; // No innate knowledge found — caller should fall back to LLM
    }

    /**
     * MULTI-DOMAIN QUERY: Search across all domains and return structured results.
     */
    public List<String[]> queryAll(String question) {
        totalQueries++;
        String q = question.trim().toUpperCase();
        List<String[]> results = new ArrayList<>();

        for (Map.Entry<String, String[]> e : knowledgeIndex.entrySet()) {
            if (e.getKey().contains(q) || q.contains(e.getKey())) {
                results.add(e.getValue());
            }
        }

        // Also search quantum protocols
        for (String protocol : GenesisDatabase.QUANTUM_PROTOCOLS) {
            if (protocol.toUpperCase().contains(q)) {
                results.add(new String[]{"QUANTUM_PROTOCOL", "Protocol", protocol});
            }
        }

        if (!results.isEmpty()) hits++;
        else misses++;

        return results;
    }

    /**
     * EVALUATE: Execute a logic gate with given inputs.
     */
    public int evaluateGate(String gate, int a, int b) {
        return DigitalLogicCore.evaluate(gate, a, b);
    }

    /**
     * APPLY QUANTUM GATE: Apply a quantum gate matrix to a state vector [alpha, beta].
     */
    public double[] applyQuantumGate(String gateName, double alpha, double beta) {
        double[] matrix = GenesisDatabase.QUANTUM_GATES.get(gateName);
        if (matrix == null) return new double[]{alpha, beta}; // Identity fallback

        double newAlpha = matrix[0] * alpha + matrix[1] * beta;
        double newBeta  = matrix[2] * alpha + matrix[3] * beta;
        return new double[]{newAlpha, newBeta};
    }

    private String formatAnswer(String[] entry) {
        return "[" + entry[0] + "] " + entry[1] + ": " + entry[2];
    }

    // --- Statistics ---
    public int getTotalQueries() { return totalQueries; }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }
    public int getIndexSize() { return knowledgeIndex.size(); }
    public double getHitRate() { return totalQueries == 0 ? 0 : (double) hits / totalQueries * 100; }

    public String getStatus() {
        return String.format("ORACLE: %d indexed | %d queries | %d hits | %d misses | %.1f%% hit rate",
            knowledgeIndex.size(), totalQueries, hits, misses, getHitRate());
    }

    /**
     * Interactive REPL for the Oracle.
     */
    public static void main(String[] args) {
        QuantumOracle oracle = getInstance();
        GenesisDatabase.printManifest();
        DigitalLogicCore.printManifest();
        System.out.println("\n   " + oracle.getStatus());
        System.out.println("\n   Type a concept to query (e.g., MOV, Hadamard, Taylor Series, AND, 7400):");
        System.out.println("   Type EXIT to quit.\n");

        java.util.Scanner sc = new java.util.Scanner(System.in);
        while (true) {
            System.out.print("   oracle> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("EXIT")) break;
            if (line.isEmpty()) continue;

            String result = oracle.query(line);
            if (result != null) {
                System.out.println("   " + result);
            } else {
                System.out.println("   [NO MATCH] No innate knowledge for: " + line);
            }
        }
    }
}
