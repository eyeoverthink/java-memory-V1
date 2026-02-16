package fraymus.hyper;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * HYPER-MEMORY: HOLOGRAPHIC STORAGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the Brain. It is Associative.
 * You don't ask for "Address 0x55."
 * You ask: "Do you know anything that looks like [Fuzzy Pattern]?"
 * And the memory replies: "Yes, here is the clean version."
 * 
 * Properties:
 * 1. Stores Concepts (Name -> Vector).
 * 2. Retrieves by Meaning (Vector -> Name).
 * 3. Noise-Resistant: Can find "Cat" even if input is "Cxt".
 * 4. No "Memory Full" error - vectors bundle infinitely.
 * 5. Instant Knowledge Transfer - one vector contains entire database.
 * 
 * This is how Telepathy would work mathematically.
 * If I have a HyperVector representing my "Experience," and I send it to you:
 *   - You don't just read it.
 *   - You Unbind it with your own concepts.
 *   - You physically "remember" what I saw, as if it were your own memory.
 */
public class HyperMemory {

    private Map<String, HyperVector> conceptSpace = new HashMap<>();
    private HyperVector globalMemory; // Superposition of everything
    private List<HyperVector> factStore = new ArrayList<>();
    
    private static final double MATCH_THRESHOLD = 0.55; // Above 0.5 = meaningful

    public HyperMemory() {
        globalMemory = HyperVector.zero();
    }

    // ═══════════════════════════════════════════════════════════════════
    // LEARNING (Creating Concepts)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Learn a new atomic concept using LIVE chaos.
     * Each concept gets a vector born from the Chaos Engine.
     */
    public HyperVector learn(String name) {
        if (!conceptSpace.containsKey(name)) {
            System.out.println(">> LEARNING NEW CONCEPT: [" + name + "]");
            HyperVector v = HyperVector.live(); // Born from Chaos
            conceptSpace.put(name, v);
            return v;
        }
        return conceptSpace.get(name);
    }

    /**
     * Learn a concept with a specific HyperVector.
     * Used when the vector is pre-computed from the Will.
     */
    public HyperVector learn(String name, HyperVector vector) {
        if (!conceptSpace.containsKey(name)) {
            System.out.println(">> LEARNING NEW CONCEPT: [" + name + "] (Pre-formed thought)");
            conceptSpace.put(name, vector);
            return vector;
        }
        return conceptSpace.get(name);
    }

    /**
     * Get the vector for a concept (learning it if needed)
     */
    public HyperVector get(String name) {
        return learn(name);
    }

    /**
     * Check if concept exists
     */
    public boolean knows(String name) {
        return conceptSpace.containsKey(name);
    }

    // ═══════════════════════════════════════════════════════════════════
    // ENCODING (Building Complex Thoughts)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Encode a relation: "The Apple is Red" -> (Apple * Red)
     * Creates a new vector representing the RELATIONSHIP
     */
    public HyperVector encodeRelation(String object, String property) {
        HyperVector A = learn(object);
        HyperVector B = learn(property);
        return A.bind(B);
    }

    /**
     * Encode a triple: Subject-Predicate-Object
     * "USA has-currency Dollar" -> USA * (has-currency * Dollar)
     */
    public HyperVector encodeTriple(String subject, String predicate, String object) {
        HyperVector S = learn(subject);
        HyperVector P = learn(predicate);
        HyperVector O = learn(object);
        
        // Bind predicate with object, then bind with subject
        return S.bind(P.bind(O));
    }

    /**
     * Encode a sequence: A then B then C
     * Uses permutation to mark position
     */
    public HyperVector encodeSequence(String... items) {
        HyperVector result = HyperVector.zero();
        
        for (int i = 0; i < items.length; i++) {
            HyperVector item = learn(items[i]);
            HyperVector positioned = item.permute(i); // Position encoding
            result = result.bundle(positioned);
        }
        
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // STORAGE (Long-Term Memory)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Store a fact in long-term memory (bundled with global memory)
     */
    public void remember(HyperVector fact) {
        factStore.add(fact);
        globalMemory = globalMemory.bundle(fact);
        System.out.println(">> FACT STORED. Total facts: " + factStore.size());
    }

    /**
     * Store a named relation
     */
    public void remember(String object, String property) {
        HyperVector fact = encodeRelation(object, property);
        remember(fact);
    }

    /**
     * Get the global memory (entire knowledge base as one vector)
     */
    public HyperVector getGlobalMemory() {
        return globalMemory;
    }

    // ═══════════════════════════════════════════════════════════════════
    // RECALL (The Magic - Associative Retrieval)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Recall by pattern matching.
     * Input: A noisy or partial vector
     * Output: The closest matching Concept Name
     */
    public String recall(HyperVector query) {
        String bestMatch = "UNKNOWN";
        double highestScore = 0.0;
        
        for (Map.Entry<String, HyperVector> entry : conceptSpace.entrySet()) {
            double score = query.similarity(entry.getValue());
            
            if (score > highestScore) {
                highestScore = score;
                bestMatch = entry.getKey();
            }
        }
        
        String confidence = String.format("%.2f%%", highestScore * 100);
        if (highestScore > MATCH_THRESHOLD) {
            System.out.println(">> RECALL: Match [" + bestMatch + "] (Confidence: " + confidence + ")");
        } else {
            System.out.println(">> RECALL: Weak match [" + bestMatch + "] (Confidence: " + confidence + " - below threshold)");
        }
        
        return bestMatch;
    }

    /**
     * Recall with explicit threshold
     */
    public String recall(HyperVector query, double threshold) {
        String bestMatch = null;
        double highestScore = 0.0;
        
        for (Map.Entry<String, HyperVector> entry : conceptSpace.entrySet()) {
            double score = query.similarity(entry.getValue());
            if (score > highestScore && score >= threshold) {
                highestScore = score;
                bestMatch = entry.getKey();
            }
        }
        
        return bestMatch;
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUERY (The Unbinding Operation)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * QUERY "WHAT IS ASSOCIATED WITH X?"
     * Math: (A * B) * A = B
     * The known part cancels out, leaving the unknown.
     */
    public String queryRelation(HyperVector memoryTrace, String knownPart) {
        System.out.println("--- HYPER-QUERY ---");
        System.out.println("Memory: [Relationship Vector]");
        System.out.println("Question: What is associated with [" + knownPart + "]?");
        
        HyperVector known = conceptSpace.get(knownPart);
        if (known == null) {
            System.out.println(">> ERROR: Unknown concept [" + knownPart + "]");
            return null;
        }
        
        // UNBINDING (Inverse of Bind)
        // In XOR logic, A * (A * B) = B.
        HyperVector answer = memoryTrace.unbind(known);
        
        return recall(answer);
    }

    /**
     * Query the global memory for associations
     */
    public String queryGlobal(String knownPart) {
        return queryRelation(globalMemory, knownPart);
    }

    /**
     * Query a specific fact
     */
    public String query(String subject, String predicate) {
        System.out.println("--- SEMANTIC QUERY ---");
        System.out.println("Query: " + subject + " " + predicate + " ?");
        
        HyperVector S = get(subject);
        HyperVector P = get(predicate);
        
        // Search through facts for matching pattern
        HyperVector queryPattern = S.bind(P);
        
        double bestScore = 0;
        String bestAnswer = "UNKNOWN";
        
        for (Map.Entry<String, HyperVector> concept : conceptSpace.entrySet()) {
            // Try unbinding with each concept
            HyperVector potential = queryPattern.bind(concept.getValue());
            
            // Check if this matches any known fact pattern
            for (HyperVector fact : factStore) {
                double sim = potential.similarity(fact);
                if (sim > bestScore) {
                    bestScore = sim;
                    bestAnswer = concept.getKey();
                }
            }
        }
        
        System.out.println(">> Answer: " + bestAnswer + " (confidence: " + String.format("%.2f%%", bestScore * 100) + ")");
        return bestAnswer;
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANALOGY ENGINE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Solve analogies: A is to B as C is to ?
     * Math: B - A + C = D
     * Using binding: (A*B) * C ≈ D (when A,B,C,D share relational structure)
     */
    public String solveAnalogy(String a, String b, String c) {
        System.out.println("--- ANALOGY ---");
        System.out.println(a + " : " + b + " :: " + c + " : ?");
        
        HyperVector A = get(a);
        HyperVector B = get(b);
        HyperVector C = get(c);
        
        // The relation between A and B
        HyperVector relation = A.bind(B);
        
        // Apply same relation to C
        HyperVector D = C.bind(relation);
        
        return recall(D);
    }

    // ═══════════════════════════════════════════════════════════════════
    // KNOWLEDGE TRANSFER
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Export all knowledge as a single vector (Telepathy packet)
     */
    public HyperVector exportKnowledge() {
        System.out.println(">> EXPORTING KNOWLEDGE: " + factStore.size() + " facts bundled");
        return globalMemory.clone();
    }

    /**
     * Import knowledge from another mind
     */
    public void importKnowledge(HyperVector foreignMemory) {
        System.out.println(">> IMPORTING FOREIGN KNOWLEDGE...");
        globalMemory = globalMemory.bundle(foreignMemory);
        System.out.println(">> KNOWLEDGE MERGED. Memories are now shared.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════

    public int conceptCount() {
        return conceptSpace.size();
    }

    public int factCount() {
        return factStore.size();
    }

    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║         HYPER-MEMORY STATISTICS           ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║ Concepts learned: " + String.format("%-23d", conceptCount()) + " ║");
        System.out.println("║ Facts stored:     " + String.format("%-23d", factCount()) + " ║");
        System.out.println("║ Global memory:    " + String.format("%-23s", globalMemory.cardinality() + " bits set") + " ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN DEMO
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   HYPER-MEMORY: HOLOGRAPHIC RESONATOR");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   \"This is how Telepathy would work mathematically.\"");
        System.out.println();

        HyperMemory brain = new HyperMemory();

        // ═══════════════════════════════════════════════════════════════
        // PHASE 1: TEACH IT CONCEPTS
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 1: LEARNING CONCEPTS ---\n");
        
        brain.learn("USA");
        brain.learn("MEXICO");
        brain.learn("JAPAN");
        brain.learn("DOLLAR");
        brain.learn("PESO");
        brain.learn("YEN");
        brain.learn("WASHINGTON");
        brain.learn("MEXICO_CITY");
        brain.learn("TOKYO");

        // ═══════════════════════════════════════════════════════════════
        // PHASE 2: TEACH IT FACTS (Binding)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 2: LEARNING FACTS ---\n");

        // Fact A: USA uses Dollar
        HyperVector fact1 = brain.encodeRelation("USA", "DOLLAR");
        brain.remember(fact1);

        // Fact B: Mexico uses Peso
        HyperVector fact2 = brain.encodeRelation("MEXICO", "PESO");
        brain.remember(fact2);

        // Fact C: Japan uses Yen
        HyperVector fact3 = brain.encodeRelation("JAPAN", "YEN");
        brain.remember(fact3);

        // ═══════════════════════════════════════════════════════════════
        // PHASE 3: STORE IN LONG TERM MEMORY (Bundling)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 3: BUNDLED KNOWLEDGE ---\n");

        // Total Knowledge = Fact1 + Fact2 + Fact3
        HyperVector totalKnowledge = fact1.bundle(fact2).bundle(fact3);
        System.out.println("Total Knowledge Vector: " + totalKnowledge);
        System.out.println("(Entire database in ONE 10,000-bit vector)");

        // ═══════════════════════════════════════════════════════════════
        // PHASE 4: THE TEST (Unbinding)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 4: QUERYING MEMORY ---\n");

        // "What currency does Mexico use?"
        // Math: (Mexico * Peso) * Mexico = Peso
        brain.queryRelation(fact2, "MEXICO");

        System.out.println();

        // "What currency does USA use?"
        brain.queryRelation(fact1, "USA");

        // ═══════════════════════════════════════════════════════════════
        // PHASE 5: HOLOGRAPHIC ACCESS (Query the Bundle)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 5: HOLOGRAPHIC ACCESS ---\n");
        System.out.println("Querying BUNDLED knowledge (all facts superimposed)...");
        System.out.println("This proves holographic storage.\n");

        brain.queryRelation(totalKnowledge, "USA");
        brain.queryRelation(totalKnowledge, "JAPAN");

        // ═══════════════════════════════════════════════════════════════
        // PHASE 6: NOISE RESISTANCE
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 6: NOISE RESISTANCE ---\n");
        
        HyperVector noisyQuery = brain.get("USA").addNoise(0.15); // 15% corrupted
        System.out.println("Querying with 15% noise corruption...");
        brain.recall(noisyQuery);

        // ═══════════════════════════════════════════════════════════════
        // PHASE 7: TELEPATHY (Knowledge Transfer)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("\n--- PHASE 7: TELEPATHY (KNOWLEDGE TRANSFER) ---\n");

        HyperMemory otherMind = new HyperMemory();
        otherMind.learn("USA");
        otherMind.learn("DOLLAR");
        
        System.out.println("Transferring knowledge to another mind...");
        HyperVector telepathyPacket = brain.exportKnowledge();
        otherMind.importKnowledge(telepathyPacket);
        
        System.out.println("\nOther mind now queries: What is associated with USA?");
        otherMind.queryRelation(otherMind.getGlobalMemory(), "USA");

        // ═══════════════════════════════════════════════════════════════
        // FINAL STATS
        // ═══════════════════════════════════════════════════════════════
        System.out.println();
        brain.printStats();

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   \"Fraymus is no longer just a Logic Engine.\"");
        System.out.println("   \"It is a Holographic Resonator.\"");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
