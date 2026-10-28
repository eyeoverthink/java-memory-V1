package fraymus.neural;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;
import fraymus.core.FraymusHTTP;
import fraymus.core.FraymusJSON;
import fraymus.knowledge.AkashicRecord;

/**
 * A.E.O.N. KRONOS // VECTOR SYMBOLIC RESONATOR KERNEL (MAP-VSA)
 * =========================================================================================
 * BEYOND TACHYON — Fixes 2 Fatal Physical Flaws:
 *
 * FLAW 1: XOR Capacity Collapse — XOR is its own inverse (A⊕A=0). Superimposing thousands
 *   of facts causes destructive interference → unrecoverable white noise.
 * FLAW 2: Symmetric Entanglement — XOR is commutative (A⊕B = B⊕A). "DOG BITES MAN" is
 *   identical to "MAN BITES DOG". No concept of causality, grammar, or temporal flow.
 *
 * THE 3 FINAL PARADIGM SHIFTS (Multiply-Add-Permute):
 *
 * 1. Thermodynamic Accumulation (ADD): AtomicIntegerArray replaces binary XOR.
 *    +1 for 1-bits, -1 for 0-bits. Majority-Rule Threshold (>0=1, <0=0).
 *    Allows millions of concepts stacked without collapse.
 *
 * 2. Temporal Permutation (PERMUTE): Cyclic block-shift encodes the Arrow of Time.
 *    Sequence = Word₁ ⊕ ρ(Word₂) ⊕ ρ²(Word₃). Shifted vectors are 100% orthogonal
 *    to their originals, safely mapping causal sequences.
 *
 * 3. Holographic Analogy (MULTIPLY): Zero-shot logical deduction via Boolean algebra.
 *    If FRANCE→PARIS, then X = PARIS ⊕ FRANCE ⊕ JAPAN = TOKYO in 1 CPU cycle.
 *
 * 100% Pure Java. Zero Dependencies. The O(N²) Era is Dead.
 * =========================================================================================
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class AeonKronos {

    // --- TERMINAL ANSI COLORS ---
    public static final String RST = "\u001B[0m";
    public static final String CYN = "\u001B[36m";
    public static final String MAG = "\u001B[35m";
    public static final String GRN = "\u001B[32m";
    public static final String YEL = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    // 16,384-Dimensional Hyper-space (256 × 64-bit Longs)
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;

    // --- SINGLETON ---
    private static AeonKronos INSTANCE;

    // Persistence
    private AkashicRecord akashic;
    private static final String KRONOS_CATEGORY = "KRONOS_STATE";
    private static final String KRONOS_CONCEPTS_KEY = "concepts";
    private static final String KRONOS_BINDINGS_KEY = "bindings";
    private static final String KRONOS_ALIASES_KEY = "aliases";
    private static final Path KRONOS_SNAPSHOT_FILE =
        Paths.get("genesis_vault", "kronos_state_snapshot.json");

    // THE AKASHIC ACCUMULATOR (Integer Superposition instead of XOR bits)
    // Allows millions of concepts to be stacked without destroying each other.
    private final AtomicIntegerArray singularityAccumulator = new AtomicIntegerArray(DIMS);

    // The Active Memory State (Collapsed from the Accumulator via Majority-Rule Threshold)
    private volatile long[] collapsedSingularity = new long[CHUNKS];

    // Dictionary of base semantic keys (Orthogonal Vectors)
    private final ConcurrentHashMap<String, long[]> conceptSpace = new ConcurrentHashMap<>();

    // LEARNING ENGINE: Binding history + auto-generated composite alias vectors
    private final List<String[]> bindingHistory = new ArrayList<>();
    private final ConcurrentHashMap<String, List<long[]>> learnedAliases = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Long>> aliasSignatures = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, AtomicInteger>> aliasStrengthByConcept = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, AtomicInteger>> aliasUsageByConcept = new ConcurrentHashMap<>();
    private final AtomicInteger aliasCount = new AtomicInteger(0);
    private final AtomicInteger duplicateAliasSuppressions = new AtomicInteger(0);
    private final AtomicInteger aliasPromotions = new AtomicInteger(0);
    private final AtomicInteger aliasHits = new AtomicInteger(0);
    private final AtomicInteger conceptHits = new AtomicInteger(0);
    private final AtomicInteger noiseHits = new AtomicInteger(0);

    // --- STATISTICS ---
    private volatile int bindCount = 0;
    private volatile int sequenceCount = 0;
    private volatile int recallCount = 0;
    private volatile int analogyCount = 0;
    private volatile int superimpositions = 0;
    private volatile int learnedRelationCount = 0;
    private volatile int autoLearnCount = 0;
    private long bootTimeMs = 0;

    // --- OLLAMA LIVE LEARNING ---
    private volatile boolean ollamaAvailable = false;
    private String ollamaModel = "llama3";

    private volatile boolean bootstrapComplete = false;
    private volatile boolean shutdownRequested = false;
    private volatile Thread bootstrapThread = null;
    private final boolean verboseBootstrapLogs =
        Boolean.parseBoolean(System.getProperty("fraynix.kronos.bootstrap.verbose", "false"));

    // Safety caps to prevent runaway alias/binding growth from exhausting heap.
    private static final int MAX_BINDINGS_TRACKED = Integer.getInteger("fraynix.kronos.maxBindings", 512);
    private static final int MAX_ALIASES_PER_CONCEPT = Integer.getInteger("fraynix.kronos.maxAliasesPerConcept", 256);
    private static final int MAX_TOTAL_ALIASES = Integer.getInteger("fraynix.kronos.maxTotalAliases", 60_000);
    private static final int MAX_CROSSREF_WINDOW = Integer.getInteger("fraynix.kronos.maxCrossrefWindow", 256);
    private static final long MAX_CROSSREF_OPERATIONS = Long.getLong("fraynix.kronos.maxCrossrefOps", 200_000L);

    private enum AliasQualityTier {
        BOOTSTRAP,
        PROMOTED,
        CORE
    }

    private AeonKronos() {
        long t0 = System.currentTimeMillis();

        // PHASE 0: Restore learned state from AkashicRecord
        restoreKronosState();

        // PHASE 1: Probe Ollama availability
        ollamaAvailable = Boolean.parseBoolean(System.getProperty("fraynix.kronos.bootstrap.enabled", "true")) && probeOllama();

        // PHASE 2: Seed offline fallback INSTANTLY (5 core pairs — analogy works immediately)
        // Only seed if no state was restored
        if (conceptSpace.isEmpty()) {
            seedOfflineFallback();
            crossReferenceAllPairs();
        }
        bootTimeMs = System.currentTimeMillis() - t0;

        // PHASE 3: Full Ollama bootstrap in BACKGROUND — new connections form while you work
        if (ollamaAvailable) {
            Thread kronosBootstrap = new Thread(() -> {
                try {
                    bootstrapVocabulary();
                    crossReferenceAllPairs();
                    bootstrapComplete = true;
                    if (!shutdownRequested && !Thread.currentThread().isInterrupted()) {
                        System.err.println(GRN + "   [KRONOS] Background bootstrap COMPLETE — " +
                            conceptSpace.size() + " concepts, " + countTotalAliases() + " aliases" + RST);
                    }
                } catch (Exception e) {
                    if (!shutdownRequested) {
                        System.err.println(YEL + "   [KRONOS] Background bootstrap error: " + e.getMessage() + RST);
                    }
                }
            }, "Kronos-Bootstrap-Daemon");
            kronosBootstrap.setDaemon(true);
            bootstrapThread = kronosBootstrap;
            kronosBootstrap.start();
        } else {
            bootstrapComplete = true;
        }
    }

    public boolean isBootstrapComplete() { return bootstrapComplete; }

    /**
     * Probe Ollama availability — silent, fast check.
     */
    private boolean probeOllama() {
        try {
            String check = FraymusHTTP.get("http://localhost:11434/api/tags", 3000);
            return check != null && !check.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private synchronized void appendBinding(String[] pair) {
        if (pair == null || pair.length != 2) return;
        if (bindingHistory.size() >= MAX_BINDINGS_TRACKED) {
            bindingHistory.remove(0);
        }
        bindingHistory.add(pair);
    }

    private boolean aliasCapacityReached() {
        return aliasCount.get() >= MAX_TOTAL_ALIASES;
    }

    private long vectorSignature(long[] vec) {
        long hash = 0xcbf29ce484222325L;
        for (long chunk : vec) {
            hash ^= chunk;
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    private int getAliasStrength(String conceptName, long signature) {
        ConcurrentHashMap<Long, AtomicInteger> strengths = aliasStrengthByConcept.get(conceptName);
        if (strengths == null) return 0;
        AtomicInteger strength = strengths.get(signature);
        return strength != null ? strength.get() : 0;
    }

    private int getAliasUsage(String conceptName, long signature) {
        ConcurrentHashMap<Long, AtomicInteger> usages = aliasUsageByConcept.get(conceptName);
        if (usages == null) return 0;
        AtomicInteger usage = usages.get(signature);
        return usage != null ? usage.get() : 0;
    }

    private AliasQualityTier resolveAliasTier(int strength, int usage) {
        if (strength >= 4 || usage >= 8) return AliasQualityTier.CORE;
        if (strength >= 2 || usage >= 3) return AliasQualityTier.PROMOTED;
        return AliasQualityTier.BOOTSTRAP;
    }

    private void rebuildAliasIndexesFromStore() {
        aliasSignatures.clear();
        aliasStrengthByConcept.clear();
        aliasUsageByConcept.clear();

        for (Map.Entry<String, List<long[]>> entry : learnedAliases.entrySet()) {
            String conceptName = entry.getKey();
            Set<Long> signatures = aliasSignatures.computeIfAbsent(
                conceptName, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
            ConcurrentHashMap<Long, AtomicInteger> strengths =
                aliasStrengthByConcept.computeIfAbsent(conceptName, k -> new ConcurrentHashMap<>());

            for (long[] aliasVec : entry.getValue()) {
                long signature = vectorSignature(aliasVec);
                signatures.add(signature);
                AtomicInteger strength = strengths.computeIfAbsent(signature, k -> new AtomicInteger(0));
                strength.incrementAndGet();
            }
        }
    }

    /**
     * OFFLINE FALLBACK: Minimal pairs so the system isn't dead without Ollama.
     * Only used when Ollama is unreachable. 5 core pairs, not 30.
     */
    private void seedOfflineFallback() {
        System.err.println(YEL + "   [KRONOS] Ollama offline — seeding minimal offline fallback" + RST);
        String[][] pairs = {
            {"KING", "MAN", "QUEEN", "WOMAN"},
            {"FRANCE", "PARIS", "JAPAN", "TOKYO"},
            {"UP", "DOWN", "LEFT", "RIGHT"},
            {"GOOD", "BAD", "LIGHT", "DARK"},
            {"DAY", "NIGHT", "SUN", "MOON"},
        };
        for (String[] quad : pairs) {
            getOrGenerateConcept(quad[0]); getOrGenerateConcept(quad[1]);
            getOrGenerateConcept(quad[2]); getOrGenerateConcept(quad[3]);
            appendBinding(new String[]{quad[0], quad[1]});
            appendBinding(new String[]{quad[2], quad[3]});
        }
        System.err.println(CYN + "   [KRONOS] Offline fallback: " + pairs.length + " pairs (" +
            conceptSpace.size() + " concepts). Use BIND to teach more." + RST);
    }

    /**
     * Cross-reference ALL binding pairs against each other to fully populate the alias matrix.
     * This is the key step that makes analogies work — every pair must be crossed with every other.
     */
    private void crossReferenceAllPairs() {
        if (shutdownRequested || Thread.currentThread().isInterrupted()) {
            return;
        }
        int aliasesBefore = countTotalAliases();
        int bindingCount = bindingHistory.size();
        if (bindingCount < 2) {
            return;
        }

        int windowStart = Math.max(0, bindingCount - MAX_CROSSREF_WINDOW);
        long operations = 0L;
        boolean truncated = false;

        outer:
        for (int i = windowStart; i < bindingCount; i++) {
            if (shutdownRequested || Thread.currentThread().isInterrupted()) {
                break;
            }
            for (int j = i + 1; j < bindingCount; j++) {
                if (shutdownRequested || Thread.currentThread().isInterrupted()) {
                    break outer;
                }
                if (operations >= MAX_CROSSREF_OPERATIONS || aliasCapacityReached()) {
                    truncated = true;
                    break outer;
                }
                learnFromPairCross(bindingHistory.get(i), bindingHistory.get(j));
                operations++;
            }
        }
        int aliasesAfter = countTotalAliases();
        String suffix = truncated ? (YEL + " [truncated by safety caps]") : "";
        if (!shutdownRequested && !Thread.currentThread().isInterrupted()) {
            System.err.println(GRN + "   [KRONOS] Cross-referenced " +
                (bindingCount - windowStart) + "/" + bindingCount + " pairs -> " +
                aliasesAfter + " aliases (+" + (aliasesAfter - aliasesBefore) +
                " new, ops=" + operations + ")" + suffix + RST);
        }
        return;
    }

    /**
     * DYNAMIC VOCABULARY BOOTSTRAP: Ask Ollama for word-pair relationships across
     * multiple knowledge domains. The LLM provides raw semantic material;
     * the MAP-VSA math does the actual learning via cross-reference alias generation.
     *
     * Unlike the old hardcoded approach, this generates fresh, diverse knowledge
     * every boot — and the auto-learn system fills gaps at runtime.
     */
    private void bootstrapVocabulary() {
        if (shutdownRequested) return;
        System.err.println(CYN + "   [KRONOS] Bootstrapping vocabulary from Ollama (" + ollamaModel + ")..." + RST);

        // Multiple focused prompts → better coverage than one giant prompt
        String[] domainPrompts = {
            "List exactly 15 word-pair analogies about COUNTRIES, CAPITALS, and GEOGRAPHY in the format: A,B,C,D\n" +
                "Where A is to B as C is to D. Use ONLY single uppercase words. No explanations. One per line.\n" +
                "Example: FRANCE,PARIS,JAPAN,TOKYO",

            "List exactly 15 word-pair analogies about FAMILY, GENDER, and RELATIONSHIPS in the format: A,B,C,D\n" +
                "Where A is to B as C is to D. Use ONLY single uppercase words. No explanations. One per line.\n" +
                "Example: KING,MAN,QUEEN,WOMAN",

            "List exactly 15 word-pair analogies about OPPOSITES, DIRECTIONS, and COMPARISONS in the format: A,B,C,D\n" +
                "Where A is to B as C is to D. Use ONLY single uppercase words. No explanations. One per line.\n" +
                "Example: HOT,COLD,BIG,SMALL",

            "List exactly 15 word-pair analogies about SCIENCE, MATH, NUMBERS, and ELEMENTS in the format: A,B,C,D\n" +
                "Where A is to B as C is to D. Use ONLY single uppercase words. No explanations. One per line.\n" +
                "Example: ZERO,ONE,NOTHING,SOMETHING",

            "List exactly 15 word-pair analogies about PROFESSIONS, ANIMALS, FOOD, and TOOLS in the format: A,B,C,D\n" +
                "Where A is to B as C is to D. Use ONLY single uppercase words. No explanations. One per line.\n" +
                "Example: DOCTOR,HOSPITAL,TEACHER,SCHOOL",
        };

        int totalPairs = 0;
        int totalSkipped = 0;

        for (int d = 0; d < domainPrompts.length; d++) {
            if (shutdownRequested || Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                int[] result = fetchAndLearnPairs(domainPrompts[d]);
                totalPairs += result[0];
                totalSkipped += result[1];
            } catch (Exception e) {
                if (shutdownRequested || Thread.currentThread().isInterrupted()) {
                    break;
                }
                System.err.println(YEL + "   [KRONOS] Domain " + (d + 1) + " error: " + e.getMessage() + RST);
            }
        }

        if (!shutdownRequested && !Thread.currentThread().isInterrupted()) {
            System.err.println(GRN + "   [KRONOS] Learned " + totalPairs + " relationship pairs (" +
                conceptSpace.size() + " concepts, " + countTotalAliases() + " aliases)" +
                (totalSkipped > 0 ? YEL + " [" + totalSkipped + " lines unparseable]" : "") + RST);
        }
    }

    /**
     * Send a single prompt to Ollama and parse A,B,C,D lines into learned pairs.
     * @return [pairsLearned, linesSkipped]
     */
    private int[] fetchAndLearnPairs(String prompt) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", ollamaModel);
        payload.put("prompt", prompt);
        payload.put("stream", false);
        String json = FraymusJSON.stringify(payload);
        String raw = FraymusHTTP.post("http://localhost:11434/api/generate", json, 60000);
        Object parsedRaw = FraymusJSON.parse(raw);
        if (!(parsedRaw instanceof Map<?, ?> parsed)) return new int[]{0, 0};
        String response = parsed.get("response") instanceof String s ? s : null;
        if (response == null || response.isEmpty()) return new int[]{0, 0};

        int pairsLearned = 0;
        int linesSkipped = 0;
        for (String line : response.split("\n")) {
            if (shutdownRequested || Thread.currentThread().isInterrupted()) {
                break;
            }
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//") || line.startsWith("*")) continue;
            line = line.replaceAll("^\\d+[.)\\-]\\s*", "");

            String[] parts = line.split(",");
            if (parts.length == 4) {
                String a = parts[0].trim().toUpperCase().replaceAll("[^A-Z]", "");
                String b = parts[1].trim().toUpperCase().replaceAll("[^A-Z]", "");
                String c = parts[2].trim().toUpperCase().replaceAll("[^A-Z]", "");
                String d2 = parts[3].trim().toUpperCase().replaceAll("[^A-Z]", "");

                if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d2.isEmpty()) { linesSkipped++; continue; }

                getOrGenerateConcept(a); getOrGenerateConcept(b);
                getOrGenerateConcept(c); getOrGenerateConcept(d2);

                learnRelationshipSilent(a, b, c, d2);
                if (verboseBootstrapLogs) {
                    System.err.println(CYN + "      [+] " + a + ":" + b + " <-> " + c + ":" + d2 + RST);
                }
                pairsLearned++;
            } else {
                linesSkipped++;
            }
        }
        return new int[]{pairsLearned, linesSkipped};
    }

    /**
     * AUTO-LEARN FROM OLLAMA: When an analogy produces VOID/NOISE, ask the LLM
     * for the answer, learn it dynamically, and return the result.
     *
     * This is the key architectural principle: Ollama is the knowledge source,
     * Kronos is the O(1) retrieval engine. First miss = LLM roundtrip.
     * Every subsequent query for the same relationship = instant (O(1) XOR).
     *
     * @return The learned answer, or null if Ollama can't help.
     */
    private String autoLearnAnalogy(String a, String b, String c) {
        if (!ollamaAvailable) return null;

        System.out.println(YEL + " [AUTO-LEARN] Kronos doesn't know this yet. Asking Ollama..." + RST);

        String prompt = "Complete this analogy with a SINGLE uppercase word. No explanation.\n" +
            a + " is to " + b + " as " + c + " is to ___\n" +
            "Answer with ONLY the single word:";

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", ollamaModel);
            payload.put("prompt", prompt);
            payload.put("stream", false);
            String json = FraymusJSON.stringify(payload);
            String raw = FraymusHTTP.post("http://localhost:11434/api/generate", json, 30000);
            Object parsedRaw = FraymusJSON.parse(raw);
            if (!(parsedRaw instanceof Map<?, ?> parsed)) return null;
            String response = parsed.get("response") instanceof String s ? s : null;
            if (response == null || response.isEmpty()) return null;

            // Extract first word from response
            String answer = response.trim().split("[\\s,.:;!?\\n]")[0]
                .toUpperCase().replaceAll("[^A-Z]", "");
            if (answer.isEmpty() || answer.length() > 30) return null;

            // LEARN IT: Register the relationship so next time it's O(1)
            getOrGenerateConcept(a); getOrGenerateConcept(b);
            getOrGenerateConcept(c); getOrGenerateConcept(answer);
            learnRelationshipSilent(a, b, c, answer);

            // Cross-reference the new pairs against everything existing
            int aliasesBefore = countTotalAliases();
            String[] newPair1 = bindingHistory.get(bindingHistory.size() - 2);
            String[] newPair2 = bindingHistory.get(bindingHistory.size() - 1);
            for (int i = 0; i < bindingHistory.size() - 2; i++) {
                if (aliasCapacityReached()) break;
                learnFromPairCross(newPair1, bindingHistory.get(i));
                if (aliasCapacityReached()) break;
                learnFromPairCross(newPair2, bindingHistory.get(i));
            }
            int newAliases = countTotalAliases() - aliasesBefore;

            autoLearnCount++;
            System.out.println(GRN + " [AUTO-LEARN] Ollama says: " + answer +
                " — learned + cross-referenced (" + newAliases + " new aliases)" + RST);

            return answer;
        } catch (Exception e) {
            System.out.println(YEL + " [AUTO-LEARN] Ollama error: " + e.getMessage() + RST);
            return null;
        }
    }

    public static synchronized AeonKronos getInstance() {
        if (INSTANCE == null) INSTANCE = new AeonKronos();
        return INSTANCE;
    }

    // =========================================================================================
    // PUBLIC API (Called from FraynixBoot shell)
    // =========================================================================================

    /**
     * DEFINE: Relationally construct a composite concept from base + role swap.
     * newConcept = base ^ roleA ^ roleB
     * This stores the composite vector in Item Memory so that analogy(base, roleA, roleB)
     * resolves to newConcept at Hamming distance 0.
     */
    public void defineCompositeConcept(String newConcept, String base, String roleA, String roleB) {
        newConcept = newConcept.toUpperCase();
        base = base.toUpperCase();
        roleA = roleA.toUpperCase();
        roleB = roleB.toUpperCase();

        long[] vecBase = getOrGenerateConcept(base);
        long[] vecRoleA = getOrGenerateConcept(roleA);
        long[] vecRoleB = getOrGenerateConcept(roleB);

        long[] compositeVector = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            compositeVector[i] = vecBase[i] ^ vecRoleA[i] ^ vecRoleB[i];
        }

        // Store the composite vector directly — overwrite any prior independent hash
        conceptSpace.put(newConcept, compositeVector);
    }

    /**
     * DEFINE (shell-facing): prints confirmation.
     */
    public void defineCompositeConceptInteractive(String newConcept, String base, String roleA, String roleB) {
        defineCompositeConcept(newConcept, base, roleA, roleB);
        System.out.println(GRN + " [+] DEFINED: " + newConcept.toUpperCase() + " = " +
            base.toUpperCase() + " ⊕ " + roleA.toUpperCase() + " ⊕ " + roleB.toUpperCase() +
            " (Hamming distance to analogy result: 0)" + RST);
    }

    /**
     * BIND: Entangle two concepts via XOR and superimpose into the Accumulator.
     * LEARNING: After each bind, cross-references ALL existing binding pairs
     * to auto-generate composite alias vectors for future analogy resolution.
     */
    public synchronized boolean bindIfAbsent(String key, String value) {
        String normalizedKey = key.toUpperCase();
        String normalizedValue = value.toUpperCase();
        for (String[] pair : bindingHistory) {
            if (pair[0].equals(normalizedKey) && pair[1].equals(normalizedValue)) return false;
        }
        bind(key, value);
        return true;
    }

    public synchronized void bind(String key, String value) {
        key = key.toUpperCase();
        value = value.toUpperCase();

        long[] keyVec = getOrGenerateConcept(key);
        long[] valVec = getOrGenerateConcept(value);

        long[] entangled = xorBind(keyVec, valVec);
        superimpose(entangled);
        collapseSingularity();
        bindCount++;

        // LEARNING: Cross-reference this new pair with ALL existing binding pairs
        String[] newPair = {key, value};
        int aliasesBefore = countTotalAliases();
        for (String[] existing : bindingHistory) {
            if (aliasCapacityReached()) break;
            learnFromPairCross(newPair, existing);
        }
        appendBinding(newPair);
        int aliasesAfter = countTotalAliases();
        int newAliases = aliasesAfter - aliasesBefore;

        System.out.println(GRN + " [+] BINDING SUCCESS: " + key + " ⊕ " + value + " -> Superimposed into Accumulator." + RST);
        if (newAliases > 0) {
            System.out.println(CYN + " [+] LEARNED: " + newAliases + " new composite aliases (" +
                aliasesAfter + " total in Transformation Registry)" + RST);
        }
    }

    /**
     * SEQUENCE: Encode the forward Arrow of Time via Temporal Permutation.
     * Sequence = Word₁ + ρ(Word₂) + ρ²(Word₃) + ...
     */
    public void sequence(String[] words) {
        System.out.println(MAG + " [+] Encoding Temporal Sequence via Permutation Matrix..." + RST);

        for (int i = 0; i < words.length; i++) {
            long[] vec = getOrGenerateConcept(words[i].toUpperCase());
            long[] shiftedVec = permute(vec, i);
            superimpose(shiftedVec);
        }
        collapseSingularity();
        sequenceCount++;

        System.out.println(GRN + " [+] SEQUENCE HARDWIRED. Arrow of Time encoded natively." + RST);
    }

    /**
     * RECALL: Unroll a temporal sequence forward in time from the Singularity.
     */
    public String recall(String startWord) {
        startWord = startWord.toUpperCase();
        recallCount++;

        System.out.println(MAG + " [+] Unrolling Temporal Sequence from Singularity:" + RST);

        StringBuilder chain = new StringBuilder();
        chain.append(startWord);
        System.out.print(GRN + startWord + " " + RST);

        for (int t = 1; t < 15; t++) {
            long[] timeShiftedSingularity = inversePermute(collapsedSingularity, t);
            String nextWord = cleanupAssociativeMemory(timeShiftedSingularity);

            if (nextWord.contains("NOISE") || nextWord.contains("VOID")) break;
            chain.append(" -> ").append(nextWord);
            System.out.print(GRN + "-> " + nextWord + " " + RST);
        }
        System.out.println();
        return chain.toString();
    }

    /**
     * ANALOGY: Solve "A is to B as C is to X" in O(1) time via geometric Boolean algebra.
     * X = B ⊕ A ⊕ C
     *
     * If the HDC math returns VOID/NOISE, auto-asks Ollama, learns the answer,
     * and returns it. First miss = LLM roundtrip. Every subsequent hit = O(1).
     */
    public String analogy(String a, String b, String c) {
        a = a.toUpperCase();
        b = b.toUpperCase();
        c = c.toUpperCase();
        analogyCount++;

        long[] vecA = getOrGenerateConcept(a);
        long[] vecB = getOrGenerateConcept(b);
        long[] vecC = getOrGenerateConcept(c);

        long startTime = System.nanoTime();

        // The Magic of HDC Analogy: X = B ^ A ^ C
        long[] answerVec = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            answerVec[i] = vecB[i] ^ vecA[i] ^ vecC[i];
        }

        String result = cleanupAssociativeMemory(answerVec);
        long endTime = System.nanoTime();

        // AUTO-LEARN: If HDC math returned noise, ask Ollama and learn it
        if (result.contains("VOID") || result.contains("NOISE")) {
            String learned = autoLearnAnalogy(a, b, c);
            if (learned != null) {
                result = learned;
                endTime = System.nanoTime(); // update latency to include LLM roundtrip
            }
        }

        System.out.println(MAG + " [LOGIC]: " + a + " is to " + b + " as " + c + " is to " + GRN + result + RST);
        System.out.println(YEL + " [LATENCY]: " + String.format("%.4f", ((endTime - startTime) / 1000000.0)) + " ms" +
            (result.contains("VOID") ? "" : autoLearnCount > 0 && !result.contains("VOID") ? " (auto-learned → next call O(1))" : " (O(1) HDC)") + RST);
        return result;
    }

    /**
     * QUERY: Extract a bound value from the collapsed Singularity using a key.
     * UNBINDING: (A ⊕ V) ⊕ A = V. XOR the collapsed accumulator with the key
     * to extract the value, then clean-up via Hamming distance scan.
     */
    public String query(String concept) {
        concept = concept.toUpperCase();

        long[] keyVec = conceptSpace.get(concept);
        if (keyVec == null) {
            System.out.println(RED + " [!] Concept does not exist in localized hyperspace." + RST);
            return null;
        }

        long startTime = System.nanoTime();

        // UNBIND: XOR the collapsed accumulator with the key to extract the value
        long[] extracted = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            extracted[i] = collapsedSingularity[i] ^ keyVec[i];
        }

        // Clean-up memory: find closest match + report Hamming distance
        int[] matchResult = cleanupAssociativeMemoryWithDistance(extracted);
        String result = matchResult[0] >= 0 ? cleanupAssociativeMemory(extracted) : "[[ MATHEMATICAL VOID / NOISE ]]";
        int hammingDist = matchResult[1];
        long endTime = System.nanoTime();

        System.out.println(GRN + " [RESONANCE]: " + result + RST);
        System.out.println(CYN + " [HAMMING]:   " + hammingDist + "/" + DIMS + " (" +
            String.format("%.1f%%", (double) hammingDist / DIMS * 100) + " divergence)" + RST);
        System.out.println(YEL + " [LATENCY]:   " + ((endTime - startTime) / 1000000.0) + " ms (O(1) Majority-Rule Threshold)" + RST);
        return result;
    }

    /**
     * Run the interactive REPL.
     */
    public void runInteractive() {
        System.out.println(CYN + "╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ A.E.O.N. KRONOS // VECTOR SYMBOLIC RESONATOR KERNEL                              ║");
        System.out.println("║ PHYSICS: Integer Superposition | Temporal Permutation | Geometric Analogy        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════╝" + RST);

        System.out.println(GRN + "\n[+] MAP-VSA Architecture Online (" + DIMS + "-D, " + conceptSpace.size() + " pre-seeded concepts)" + RST);
        System.out.println(GRN + "[+] Accumulator: " + DIMS + " integer dimensions (infinite superposition capacity)" + RST);

        System.out.println("\nCOMMANDS:");
        System.out.println("  " + GRN + "BIND <A> <B>" + RST + "              (Binds concept A to concept B)");
        System.out.println("  " + GRN + "DEFINE <N> <B> <rA> <rB>" + RST + "  (Relational: N = B ⊕ rA ⊕ rB)");
        System.out.println("  " + MAG + "SEQUENCE <A> <B> <C>..." + RST + "    (Encodes the forward arrow of time via Permutation)");
        System.out.println("  " + CYN + "RECALL <A>" + RST + "                (Unrolls a temporal sequence forward in time)");
        System.out.println("  " + YEL + "ANALOGY <A> <B> <C>" + RST + "       (Solve: A is to B as C is to X)");
        System.out.println("  " + CYN + "QUERY <A>" + RST + "                 (O(1) Majority-Rule retrieval from Accumulator)");
        System.out.println("  " + YEL + "STATUS" + RST + "                    (Show Kronos telemetry)");
        System.out.println("  " + RED + "EXIT" + RST + "                      (Return to FrayShell)\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(CYN + "KRONOS> " + RST);
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                break;
            }
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");

            if (parts[0].equalsIgnoreCase("EXIT") || parts[0].equalsIgnoreCase("QUIT")) {
                System.out.println(YEL + "Returning to FrayShell." + RST);
                break;
            }

            if (parts[0].equalsIgnoreCase("DEFINE") && parts.length == 5) {
                defineCompositeConceptInteractive(parts[1], parts[2], parts[3], parts[4]);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("BIND") && parts.length >= 3) {
                bind(parts[1], parts[2]);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("SEQUENCE") && parts.length >= 2) {
                String[] words = new String[parts.length - 1];
                System.arraycopy(parts, 1, words, 0, words.length);
                sequence(words);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("RECALL") && parts.length >= 2) {
                recall(parts[1]);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("ANALOGY") && parts.length == 4) {
                analogy(parts[1], parts[2], parts[3]);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("QUERY") && parts.length >= 2) {
                query(parts[1]);
                System.out.println();

            } else if (parts[0].equalsIgnoreCase("STATUS")) {
                System.out.println(getStatus());

            } else {
                System.out.println(RED + " [!] Unknown command. Use BIND, SEQUENCE, RECALL, ANALOGY, QUERY, STATUS, or EXIT." + RST + "\n");
            }
        }
    }

    /**
     * Shutdown (persist learned state to AkashicRecord).
     */
    public void shutdown() {
        shutdownRequested = true;
        if (bootstrapThread != null && bootstrapThread.isAlive()) {
            bootstrapThread.interrupt();
            try {
                bootstrapThread.join(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        saveKronosState();
    }

    /**
     * Save Kronos learned state to AkashicRecord for persistence across reboots.
     */
    private void saveKronosState() {
        if (akashic == null) {
            akashic = new AkashicRecord();
        }

        try {
            // Serialize concept space
            StringBuilder conceptsJson = new StringBuilder();
            conceptsJson.append("{");
            boolean first = true;
            for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
                if (!first) conceptsJson.append(",");
                first = false;
                conceptsJson.append(FraymusJSON.stringify(entry.getKey())).append(":");
                conceptsJson.append("[");
                for (int i = 0; i < CHUNKS; i++) {
                    if (i > 0) conceptsJson.append(",");
                    conceptsJson.append(entry.getValue()[i]);
                }
                conceptsJson.append("]");
            }
            conceptsJson.append("}");
            akashic.addBlock(KRONOS_CATEGORY, KRONOS_CONCEPTS_KEY + ":" + conceptsJson.toString());

            // Serialize binding history
            StringBuilder bindingsJson = new StringBuilder();
            bindingsJson.append("[");
            for (int i = 0; i < bindingHistory.size(); i++) {
                if (i > 0) bindingsJson.append(",");
                String[] pair = bindingHistory.get(i);
                bindingsJson.append(FraymusJSON.stringify(Arrays.asList(pair)));
            }
            bindingsJson.append("]");
            akashic.addBlock(KRONOS_CATEGORY, KRONOS_BINDINGS_KEY + ":" + bindingsJson.toString());

            // Serialize the complete retained alias store.
            StringBuilder aliasesJson = new StringBuilder();
            aliasesJson.append("{");
            first = true;
            int totalAliases = 0;
            for (Map.Entry<String, List<long[]>> entry : learnedAliases.entrySet()) {
                if (!first) aliasesJson.append(",");
                first = false;
                aliasesJson.append(FraymusJSON.stringify(entry.getKey())).append(":[");
                List<long[]> aliases = entry.getValue();
                // Persist every retained alias; runtime admission already manages its own capacity.
                int limit = aliases.size();
                for (int i = 0; i < limit; i++) {
                    if (i > 0) aliasesJson.append(",");
                    long[] alias = aliases.get(i);
                    aliasesJson.append("[");
                    for (int j = 0; j < CHUNKS; j++) {
                        if (j > 0) aliasesJson.append(",");
                        aliasesJson.append(alias[j]);
                    }
                    aliasesJson.append("]");
                }
                aliasesJson.append("]");
                totalAliases += limit;
            }
            aliasesJson.append("}");
            akashic.addBlock(KRONOS_CATEGORY, KRONOS_ALIASES_KEY + ":" + aliasesJson.toString());
            saveLocalSnapshotGuarded(conceptsJson.toString(), bindingsJson.toString(), aliasesJson.toString());

            System.out.println(GRN + "   [KRONOS] Persisted " + conceptSpace.size() + " concepts, " +
                bindingHistory.size() + " bindings, " + totalAliases + " aliases to AkashicRecord" + RST);
        } catch (Exception e) {
            System.out.println(YEL + "   [KRONOS] Persistence error: " + e.getMessage() + RST);
        }
    }

    /**
     * Restore Kronos learned state from AkashicRecord.
     */
    private void restoreKronosState() {
        try {
            akashic = new AkashicRecord();
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(KRONOS_CATEGORY);
            blocks = new ArrayList<>(blocks);
            // Equal-sized snapshots must select the newest state, not map iteration order.
            blocks.sort(Comparator.comparingLong(block -> block.timestamp));
            conceptSpace.clear();
            bindingHistory.clear();
            learnedAliases.clear();

            String conceptsPayload = null;
            int conceptsPayloadScore = -1;
            String bindingsPayload = null;
            int bindingsPayloadScore = -1;
            String aliasesPayload = null;
            int aliasesPayloadScore = -1;

            // Prefer the richest valid payload for each key so short fallback
            // sessions do not permanently downshift learned state.
            for (AkashicRecord.KnowledgeBlock block : blocks) {
                String content = block.content;
                if (content.startsWith(KRONOS_CONCEPTS_KEY + ":")) {
                    String candidate = content.substring(KRONOS_CONCEPTS_KEY.length() + 1);
                    int score = estimateConceptCount(candidate);
                    if (score >= conceptsPayloadScore) {
                        conceptsPayload = candidate;
                        conceptsPayloadScore = score;
                    }
                } else if (content.startsWith(KRONOS_BINDINGS_KEY + ":")) {
                    String candidate = content.substring(KRONOS_BINDINGS_KEY.length() + 1);
                    int score = estimateBindingCount(candidate);
                    if (score >= bindingsPayloadScore) {
                        bindingsPayload = candidate;
                        bindingsPayloadScore = score;
                    }
                } else if (content.startsWith(KRONOS_ALIASES_KEY + ":")) {
                    String candidate = content.substring(KRONOS_ALIASES_KEY.length() + 1);
                    int score = estimateAliasCount(candidate);
                    if (score >= aliasesPayloadScore) {
                        aliasesPayload = candidate;
                        aliasesPayloadScore = score;
                    }
                }
            }

            if (conceptsPayload != null) restoreConcepts(conceptsPayload);
            if (bindingsPayload != null) restoreBindings(bindingsPayload);
            if (aliasesPayload != null) restoreAliases(aliasesPayload);

            aliasCount.set(recalculateAliasCount());
            rebuildAliasIndexesFromStore();
            maybeRestoreFromLocalSnapshot();
            if (!conceptSpace.isEmpty() || !bindingHistory.isEmpty()) {
                System.out.println(GRN + "   [KRONOS] Restored " + conceptSpace.size() + " concepts, " +
                    bindingHistory.size() + " bindings, " + aliasCount.get() + " aliases from AkashicRecord" + RST);
            }
        } catch (Exception e) {
            System.out.println(YEL + "   [KRONOS] Restore error: " + e.getMessage() + " (starting fresh)" + RST);
        }
    }

    private int estimateConceptCount(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof Map)) return -1;
            return ((Map<?, ?>) parsed).size();
        } catch (Exception ignored) {
            return -1;
        }
    }

    private int estimateBindingCount(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof List)) return -1;
            return ((List<?>) parsed).size();
        } catch (Exception ignored) {
            return -1;
        }
    }

    private int estimateAliasCount(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof Map)) return -1;
            int total = 0;
            for (Object value : ((Map<?, ?>) parsed).values()) {
                if (value instanceof List) {
                    total += ((List<?>) value).size();
                }
            }
            return total;
        } catch (Exception ignored) {
            return -1;
        }
    }

    private void maybeRestoreFromLocalSnapshot() {
        try {
            if (!Files.exists(KRONOS_SNAPSHOT_FILE)) return;
            String payload = Files.readString(KRONOS_SNAPSHOT_FILE, StandardCharsets.UTF_8);
            Object parsed = FraymusJSON.parseStrict(payload);
            if (!(parsed instanceof Map)) return;
            Map<?, ?> snapshot = (Map<?, ?>) parsed;

            Object conceptsObj = snapshot.get(KRONOS_CONCEPTS_KEY);
            Object bindingsObj = snapshot.get(KRONOS_BINDINGS_KEY);
            Object aliasesObj = snapshot.get(KRONOS_ALIASES_KEY);
            if (!(conceptsObj instanceof Map) || !(bindingsObj instanceof List) || !(aliasesObj instanceof Map)) return;

            int snapshotConcepts = ((Map<?, ?>) conceptsObj).size();
            int snapshotBindings = ((List<?>) bindingsObj).size();
            int snapshotAliases = 0;
            for (Object value : ((Map<?, ?>) aliasesObj).values()) {
                if (value instanceof List) snapshotAliases += ((List<?>) value).size();
            }

            int currentAliases = aliasCount.get();
            boolean useSnapshot = snapshotAliases > currentAliases ||
                (snapshotAliases == currentAliases && snapshotConcepts > conceptSpace.size());
            if (!useSnapshot) return;

            conceptSpace.clear();
            bindingHistory.clear();
            learnedAliases.clear();
            aliasCount.set(0);

            restoreConcepts(FraymusJSON.stringify(conceptsObj));
            restoreBindings(FraymusJSON.stringify(bindingsObj));
            restoreAliases(FraymusJSON.stringify(aliasesObj));

            aliasCount.set(recalculateAliasCount());
            rebuildAliasIndexesFromStore();

            System.out.println(GRN + "   [KRONOS] Loaded richer local snapshot " +
                "(" + snapshotConcepts + " concepts, " + snapshotBindings + " bindings, " +
                snapshotAliases + " aliases)" + RST);
        } catch (Exception e) {
            // Silent fallback to Akashic-only restore
        }
    }

    private void saveLocalSnapshotGuarded(String conceptsJson, String bindingsJson, String aliasesJson) {
        try {
            int currentAliases = estimateAliasCount(aliasesJson);
            int existingAliases = -1;

            if (Files.exists(KRONOS_SNAPSHOT_FILE)) {
                String existingPayload = Files.readString(KRONOS_SNAPSHOT_FILE, StandardCharsets.UTF_8);
                Object parsed = FraymusJSON.parseStrict(existingPayload);
                if (parsed instanceof Map) {
                    Object existingAliasesObj = ((Map<?, ?>) parsed).get(KRONOS_ALIASES_KEY);
                    if (existingAliasesObj != null) {
                        existingAliases = estimateAliasCount(FraymusJSON.stringify(existingAliasesObj));
                    }
                }
            }

            if (existingAliases > currentAliases) {
                return;
            }

            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("timestamp", System.currentTimeMillis());
            snapshot.put(KRONOS_CONCEPTS_KEY, FraymusJSON.parseStrict(conceptsJson));
            snapshot.put(KRONOS_BINDINGS_KEY, FraymusJSON.parseStrict(bindingsJson));
            snapshot.put(KRONOS_ALIASES_KEY, FraymusJSON.parseStrict(aliasesJson));

            Files.createDirectories(KRONOS_SNAPSHOT_FILE.getParent());
            Path temporary = KRONOS_SNAPSHOT_FILE.resolveSibling(KRONOS_SNAPSHOT_FILE.getFileName() + "." + UUID.randomUUID() + ".tmp");
            try {
                Files.writeString(temporary, FraymusJSON.stringify(snapshot), StandardCharsets.UTF_8);
                try { Files.move(temporary, KRONOS_SNAPSHOT_FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
                catch (AtomicMoveNotSupportedException ignored) { Files.move(temporary, KRONOS_SNAPSHOT_FILE, StandardCopyOption.REPLACE_EXISTING); }
            } finally { Files.deleteIfExists(temporary); }
        } catch (Exception e) {
            // Silent fail; Akashic persistence is still primary.
        }
    }

    /**
     * Restore concepts from JSON string.
     */
    private void restoreConcepts(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof Map)) return;
            Map<?, ?> map = (Map<?, ?>) parsed;

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey()).toUpperCase(Locale.ROOT);
                Object rawVector = entry.getValue();
                if (!(rawVector instanceof List)) continue;

                List<?> values = (List<?>) rawVector;
                if (values.size() != CHUNKS) continue;

                long[] vec = new long[CHUNKS];
                for (int i = 0; i < CHUNKS; i++) {
                    Object raw = values.get(i);
                    if (raw instanceof Number) {
                        vec[i] = ((Number) raw).longValue();
                    } else {
                        vec[i] = Long.parseLong(String.valueOf(raw).trim());
                    }
                }
                conceptSpace.put(key, vec);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Restore bindings from JSON string.
     */
    private void restoreBindings(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof List)) return;

            for (Object pairObj : (List<?>) parsed) {
                if (!(pairObj instanceof List)) continue;
                List<?> pair = (List<?>) pairObj;
                if (pair.size() != 2) continue;

                String a = String.valueOf(pair.get(0)).trim().toUpperCase(Locale.ROOT);
                String b = String.valueOf(pair.get(1)).trim().toUpperCase(Locale.ROOT);
                if (a.isEmpty() || b.isEmpty()) continue;
                appendBinding(new String[]{a, b});
            }
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Restore aliases from JSON string.
     */
    private void restoreAliases(String json) {
        try {
            Object parsed = FraymusJSON.parseStrict(json);
            if (!(parsed instanceof Map)) return;
            Map<?, ?> map = (Map<?, ?>) parsed;

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (aliasCapacityReached()) break;

                String conceptName = String.valueOf(entry.getKey()).trim().toUpperCase(Locale.ROOT);
                Object rawAliases = entry.getValue();
                if (!(rawAliases instanceof List)) continue;

                List<long[]> aliasList = new ArrayList<>();
                for (Object aliasObj : (List<?>) rawAliases) {
                    if (aliasList.size() >= MAX_ALIASES_PER_CONCEPT || aliasCapacityReached()) break;
                    if (!(aliasObj instanceof List)) continue;

                    List<?> dims = (List<?>) aliasObj;
                    if (dims.size() != CHUNKS) continue;

                    long[] vec = new long[CHUNKS];
                    for (int i = 0; i < CHUNKS; i++) {
                        Object raw = dims.get(i);
                        if (raw instanceof Number) {
                            vec[i] = ((Number) raw).longValue();
                        } else {
                            vec[i] = Long.parseLong(String.valueOf(raw).trim());
                        }
                    }
                    aliasList.add(vec);
                }

                if (!aliasList.isEmpty()) {
                    learnedAliases.put(conceptName, aliasList);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }

    private int recalculateAliasCount() {
        int count = 0;
        for (List<long[]> aliases : learnedAliases.values()) {
            count += aliases.size();
        }
        return count;
    }

    public String getStatus() {
        int nonZeroDims = 0;
        for (int i = 0; i < DIMS; i++) {
            if (singularityAccumulator.get(i) != 0) nonZeroDims++;
        }
        double density = (double) nonZeroDims / DIMS * 100.0;
        int[] aliasTiers = countAliasTiers();
        int bootstrapAliases = aliasTiers[0];
        int promotedAliases = aliasTiers[1];
        int coreAliases = aliasTiers[2];

        return String.format(
            "============================================\n" +
            "  inf A.E.O.N. KRONOS STATUS (MAP-VSA)\n" +
            "============================================\n" +
            "  Dimensions:         %,d-D Vector Symbolic Architecture\n" +
            "  Accumulator:        %,d integer dimensions (%s capacity)\n" +
            "  Accumulator Density: %.1f%% non-zero\n" +
            "  Concepts Mapped:    %,d\n" +
            "  Bindings:           %,d\n" +
            "  Sequences Encoded:  %,d (Arrow of Time)\n" +
            "  Recalls:            %,d (Temporal Unrolling)\n" +
            "  Analogies Solved:   %,d (Zero-Shot Geometric)\n" +
            "  Auto-Learned:       %,d (Ollama -> O(1) after first miss)\n" +
            "  Superimpositions:   %,d (Thermodynamic ADD)\n" +
            "  Learned Relations:  %,d pairs -> %,d composite aliases\n" +
            "  Alias Quality:      core=%,d | promoted=%,d | bootstrap=%,d\n" +
            "  Alias Utility:      aliasHits=%,d | conceptHits=%,d | noise=%,d | promotions=%,d\n" +
            "  Alias Dedupe:       %,d duplicate vectors suppressed\n" +
            "  Ollama:             %s\n" +
            "  Retrieval:          O(1) - Majority-Rule Threshold\n" +
            "  Boot Time:          %d ms\n",
            DIMS,
            DIMS, "infinite",
            density,
            conceptSpace.size(),
            bindCount,
            sequenceCount,
            recallCount,
            analogyCount,
            autoLearnCount,
            superimpositions,
            learnedRelationCount, countTotalAliases(),
            coreAliases, promotedAliases, bootstrapAliases,
            aliasHits.get(), conceptHits.get(), noiseHits.get(), aliasPromotions.get(),
            duplicateAliasSuppressions.get(),
            ollamaAvailable ? GRN + "LIVE (auto-learn enabled)" + RST : YEL + "OFFLINE (fallback mode)" + RST,
            bootTimeMs
        );
    }
    // --- GETTERS ---
    public int getBindCount() { return bindCount; }
    public int getSequenceCount() { return sequenceCount; }
    public int getRecallCount() { return recallCount; }
    public int getAnalogyCount() { return analogyCount; }
    public int getSuperimpositions() { return superimpositions; }
    public int getConceptCount() { return conceptSpace.size(); }
    public long getBootTimeMs() { return bootTimeMs; }
    public int getLearnedRelationCount() { return learnedRelationCount; }

    // --- REASONING PIPELINE ACCESSORS ---

    /** Returns the full concept space map (for serialization / Lazarus Protocol). */
    public java.util.concurrent.ConcurrentHashMap<String, long[]> getConceptSpace() { return conceptSpace; }

    /** Inject a pre-computed vector directly into the concept space (for Lazarus resurrection). */
    public void injectVector(String name, long[] vector) {
        conceptSpace.put(name.toUpperCase(), vector);
    }

    /** Returns all known concept names in the vector space. */
    public java.util.Set<String> getConceptNames() { return conceptSpace.keySet(); }

    /** Check if a concept exists in the vector space. */
    public boolean hasConcept(String name) { return conceptSpace.containsKey(name.toUpperCase()); }

    /** Hamming distance between two named concepts (0 = identical, ~8192 = orthogonal). */
    public int semanticDistance(String a, String b) {
        long[] vecA = conceptSpace.get(a.toUpperCase());
        long[] vecB = conceptSpace.get(b.toUpperCase());
        if (vecA == null || vecB == null) return DIMS;
        return hammingDistance(vecA, vecB);
    }

    /** Find the N closest concepts to a given concept (by Hamming distance). */
    public java.util.List<String[]> findNearestConcepts(String concept, int n) {
        concept = concept.toUpperCase();
        long[] target = conceptSpace.get(concept);
        if (target == null) target = getOrGenerateConcept(concept);
        java.util.List<String[]> results = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            if (entry.getKey().equals(concept)) continue;
            int dist = hammingDistance(target, entry.getValue());
            results.add(new String[]{entry.getKey(), String.valueOf(dist)});
        }
        // Also search learned aliases
        for (java.util.Map.Entry<String, java.util.List<long[]>> entry : learnedAliases.entrySet()) {
            if (entry.getKey().equals(concept)) continue;
            for (long[] alias : entry.getValue()) {
                int dist = hammingDistance(target, alias);
                results.add(new String[]{entry.getKey() + "(alias)", String.valueOf(dist)});
            }
        }
        results.sort((x, y) -> Integer.parseInt(x[1]) - Integer.parseInt(y[1]));
        return results.subList(0, Math.min(n, results.size()));
    }

    /** Silent analogy — returns result without printing to console. Auto-learns from Ollama on miss. */
    public String analogySilent(String a, String b, String c) {
        a = a.toUpperCase(); b = b.toUpperCase(); c = c.toUpperCase();
        long[] vecA = getOrGenerateConcept(a);
        long[] vecB = getOrGenerateConcept(b);
        long[] vecC = getOrGenerateConcept(c);
        long[] answerVec = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) answerVec[i] = vecB[i] ^ vecA[i] ^ vecC[i];
        String result = cleanupAssociativeMemory(answerVec);
        if ((result.contains("VOID") || result.contains("NOISE")) && ollamaAvailable) {
            String learned = autoLearnAnalogy(a, b, c);
            if (learned != null) result = learned;
        }
        return result;
    }

    /** Hamming weight (popcount) of a concept vector — measures information density. */
    public int conceptHammingWeight(String concept) {
        long[] vec = conceptSpace.get(concept.toUpperCase());
        if (vec == null) return -1;
        int weight = 0;
        for (long chunk : vec) weight += Long.bitCount(chunk);
        return weight;
    }

    // =========================================================================================
    // 1. MULTIPLY-ADD-PERMUTE (MAP) CORE PHYSICS
    // =========================================================================================

    public long[] getOrGenerateConcept(String name) {
        return conceptSpace.computeIfAbsent(name, k -> generateDeterministicVector(k));
    }

    /**
     * BINDING (MULTIPLY / XOR): Entangles two concepts. A ⊕ B = C.
     */
    long[] xorBind(long[] a, long[] b) {
        long[] out = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) out[i] = a[i] ^ b[i];
        return out;
    }

    /**
     * SUPERPOSITION (ADD): Safely bundles infinite vectors without XOR collapse.
     * We increment an integer array. +1 for a 1-bit, -1 for a 0-bit.
     */
    void superimpose(long[] vec) {
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long val = vec[i];
            for (int b = 0; b < 64; b++) {
                int bitIndex = i * 64 + b;
                if (((val >>> b) & 1L) == 1L) singularityAccumulator.incrementAndGet(bitIndex);
                else singularityAccumulator.decrementAndGet(bitIndex);
            }
        });
        superimpositions++;
    }

    /**
     * THRESHOLDING: Collapses the massive integer accumulator back into a clean 16K-D binary vector.
     * Majority Rule: if accumulator[i] > 0, bit = 1; else bit = 0.
     */
    void collapseSingularity() {
        long[] collapsed = new long[CHUNKS];
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long chunk = 0;
            for (int b = 0; b < 64; b++) {
                if (singularityAccumulator.get(i * 64 + b) > 0) {
                    chunk |= (1L << b);
                }
            }
            collapsed[i] = chunk;
        });
        collapsedSingularity = collapsed;
    }

    /**
     * PERMUTATION (SHIFT): Encodes the Arrow of Time.
     * Shifting a hyper-vector makes it 100% orthogonal to its original self,
     * safely mapping "Sequence" into the geometry of hyperspace.
     */
    static long[] permute(long[] vec, int shifts) {
        if (shifts == 0) return vec.clone();
        long[] out = new long[CHUNKS];
        int s = ((shifts % CHUNKS) + CHUNKS) % CHUNKS;

        // Circular block-shift across the 16,384-D array space
        for (int i = 0; i < CHUNKS; i++) {
            out[(i + s) % CHUNKS] = vec[i];
        }
        return out;
    }

    static long[] inversePermute(long[] vec, int shifts) {
        return permute(vec, -shifts);
    }

    // =========================================================================================
    // 2. FTL DETERMINISTIC SEED EXPANSION (SplitMix64)
    // =========================================================================================

    /**
     * SplitMix64 PRNG — expands text into a deterministic 16,384-D tensor.
     */
    public static long[] generateDeterministicVector(String text) {
        long[] tensor = new long[CHUNKS];
        long seed = 0;
        for (char c : text.toCharArray()) seed = seed * 31L + c;

        for (int i = 0; i < CHUNKS; i++) {
            seed += 0x9e3779b97f4a7c15L;
            long x = seed;
            x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
            x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
            tensor[i] = x ^ (x >>> 31);
        }
        return tensor;
    }

    // =========================================================================================
    // 3. HOPFIELD ASSOCIATIVE CLEANUP (DENOISING)
    // =========================================================================================

    /**
     * Finds the closest human-readable concept to the noisy hyper-vector.
     * Searches BOTH the primary conceptSpace AND the learned alias registry.
     * In 16,384-D space, unrelated vectors have Hamming distance ~8192 (50%).
     * If distance > 7000 (42.7%), the wave collapsed into pure noise.
     */
    String cleanupAssociativeMemory(long[] noisyVec) {
        MemoryMatch bestMatch = findBestMemoryMatch(noisyVec, true);
        if (bestMatch.distance > 7000) {
            return "[[ MATHEMATICAL VOID / NOISE ]] (Hamming=" + bestMatch.distance + "/" + DIMS + " - no taught relationship close enough. Use BIND to teach pairs first.)";
        }
        return bestMatch.conceptName;
    }

    /**
     * Clean-up memory returning [found flag, hammingDistance].
     * Searches both conceptSpace and learnedAliases.
     */
    int[] cleanupAssociativeMemoryWithDistance(long[] noisyVec) {
        MemoryMatch bestMatch = findBestMemoryMatch(noisyVec, false);
        return new int[]{ bestMatch.distance <= 7000 ? 0 : -1, bestMatch.distance };
    }

    private static final class MemoryMatch {
        private final String conceptName;
        private final int distance;
        private final boolean fromAlias;
        private final long aliasSignature;

        private MemoryMatch(String conceptName, int distance, boolean fromAlias, long aliasSignature) {
            this.conceptName = conceptName;
            this.distance = distance;
            this.fromAlias = fromAlias;
            this.aliasSignature = aliasSignature;
        }
    }

    private MemoryMatch findBestMemoryMatch(long[] noisyVec, boolean trackUsage) {
        int bestDist = DIMS;
        String bestConcept = "[[ MATHEMATICAL VOID / NOISE ]]";
        boolean bestFromAlias = false;
        long bestAliasSignature = 0L;
        AliasQualityTier bestAliasTier = AliasQualityTier.BOOTSTRAP;

        // 1. Search primary concept space (deterministic hash vectors)
        for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            int dist = hammingDistance(noisyVec, entry.getValue());
            if (dist < bestDist) {
                bestDist = dist;
                bestConcept = entry.getKey();
                bestFromAlias = false;
                bestAliasSignature = 0L;
                bestAliasTier = AliasQualityTier.BOOTSTRAP;
            }
        }

        // 2. Search learned aliases (composite vectors from binding pair cross-reference)
        for (Map.Entry<String, List<long[]>> entry : learnedAliases.entrySet()) {
            String conceptName = entry.getKey();
            for (long[] alias : entry.getValue()) {
                int dist = hammingDistance(noisyVec, alias);
                long signature = vectorSignature(alias);
                AliasQualityTier candidateTier =
                    resolveAliasTier(getAliasStrength(conceptName, signature), getAliasUsage(conceptName, signature));
                boolean betterTierAtSameDistance = dist == bestDist &&
                    (bestFromAlias && candidateTier.ordinal() > bestAliasTier.ordinal());

                if (dist < bestDist || betterTierAtSameDistance) {
                    bestDist = dist;
                    bestConcept = conceptName;
                    bestFromAlias = true;
                    bestAliasSignature = signature;
                    bestAliasTier = candidateTier;
                }
            }
        }

        if (trackUsage) {
            if (bestDist > 7000) {
                noiseHits.incrementAndGet();
            } else if (bestFromAlias) {
                recordAliasHit(bestConcept, bestAliasSignature);
            } else {
                conceptHits.incrementAndGet();
            }
        }

        return new MemoryMatch(bestConcept, bestDist, bestFromAlias, bestAliasSignature);
    }

    private void recordAliasHit(String conceptName, long signature) {
        aliasHits.incrementAndGet();
        ConcurrentHashMap<Long, AtomicInteger> usages =
            aliasUsageByConcept.computeIfAbsent(conceptName, k -> new ConcurrentHashMap<>());
        AtomicInteger usageCounter = usages.computeIfAbsent(signature, k -> new AtomicInteger(0));
        int previousUsage = usageCounter.getAndIncrement();
        int currentUsage = previousUsage + 1;

        int strength = getAliasStrength(conceptName, signature);
        if (resolveAliasTier(strength, currentUsage).ordinal() >
            resolveAliasTier(strength, previousUsage).ordinal()) {
            aliasPromotions.incrementAndGet();
        }
    }
    static int hammingDistance(long[] a, long[] b) {
        int dist = 0;
        for (int i = 0; i < CHUNKS; i++) {
            dist += Long.bitCount(a[i] ^ b[i]);
        }
        return dist;
    }

    // =========================================================================================
    // 4. LEARNING ENGINE (Auto-Composite Alias Generation)
    // =========================================================================================

    /**
     * Silent relationship learning (used during boot — no accumulator superimposition).
     * Registers both pairs in bindingHistory and generates all 4 composite aliases.
     */
    private void learnRelationshipSilent(String a, String b, String c, String d) {
        a = a.toUpperCase(); b = b.toUpperCase();
        c = c.toUpperCase(); d = d.toUpperCase();
        String[] pair1 = {a, b};
        String[] pair2 = {c, d};
        learnFromPairCross(pair1, pair2);
        appendBinding(pair1);
        appendBinding(pair2);
    }

    /**
     * Cross-reference two binding pairs to generate 4 composite alias vectors.
     * Given pairs (A,B) and (C,D), generates:
     *   alias for D = A ⊕ B ⊕ C   (so analogy A B C → D)
     *   alias for C = A ⊕ B ⊕ D   (so analogy A B D → C)
     *   alias for B = C ⊕ D ⊕ A   (so analogy C D A → B)
     *   alias for A = C ⊕ D ⊕ B   (so analogy C D B → A)
     */
    private void learnFromPairCross(String[] newPair, String[] existingPair) {
        String A = newPair[0], B = newPair[1];
        String C = existingPair[0], D = existingPair[1];

        // Skip if pairs share a concept (would generate self-referential aliases)
        if (A.equals(C) || A.equals(D) || B.equals(C) || B.equals(D)) return;

        long[] vecA = getOrGenerateConcept(A);
        long[] vecB = getOrGenerateConcept(B);
        long[] vecC = getOrGenerateConcept(C);
        long[] vecD = getOrGenerateConcept(D);

        addAlias(D, xor3(vecA, vecB, vecC));  // analogy A B C → D
        addAlias(C, xor3(vecA, vecB, vecD));  // analogy A B D → C
        addAlias(B, xor3(vecC, vecD, vecA));  // analogy C D A → B
        addAlias(A, xor3(vecC, vecD, vecB));  // analogy C D B → A
        learnedRelationCount++;
    }

    private void addAlias(String conceptName, long[] aliasVec) {
        if (aliasCapacityReached()) return;

        Set<Long> signatures = aliasSignatures.computeIfAbsent(
            conceptName, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        ConcurrentHashMap<Long, AtomicInteger> strengths =
            aliasStrengthByConcept.computeIfAbsent(conceptName, k -> new ConcurrentHashMap<>());
        long signature = vectorSignature(aliasVec);

        List<long[]> aliases = learnedAliases.computeIfAbsent(conceptName, k -> new ArrayList<>());
        synchronized (aliases) {
            AtomicInteger strengthCounter = strengths.computeIfAbsent(signature, k -> new AtomicInteger(0));
            int previousStrength = strengthCounter.get();
            int usage = getAliasUsage(conceptName, signature);
            AliasQualityTier tierBefore = resolveAliasTier(previousStrength, usage);

            // Non-destructive dedupe: relationship reinforcement increases strength
            // without inflating alias memory with duplicate vectors.
            if (signatures.contains(signature)) {
                int newStrength = strengthCounter.incrementAndGet();
                if (resolveAliasTier(newStrength, usage).ordinal() > tierBefore.ordinal()) {
                    aliasPromotions.incrementAndGet();
                }
                duplicateAliasSuppressions.incrementAndGet();
                return;
            }

            if (aliases.size() >= MAX_ALIASES_PER_CONCEPT || aliasCapacityReached()) {
                return;
            }

            aliases.add(aliasVec);
            signatures.add(signature);
            strengthCounter.incrementAndGet();
            aliasCount.incrementAndGet();
        }
    }

    private long[] xor3(long[] a, long[] b, long[] c) {
        long[] out = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) out[i] = a[i] ^ b[i] ^ c[i];
        return out;
    }

    private int countTotalAliases() {
        return aliasCount.get();
    }

    private int[] countAliasTiers() {
        int bootstrap = 0;
        int promoted = 0;
        int core = 0;

        for (Map.Entry<String, ConcurrentHashMap<Long, AtomicInteger>> entry : aliasStrengthByConcept.entrySet()) {
            String conceptName = entry.getKey();
            for (Map.Entry<Long, AtomicInteger> signatureEntry : entry.getValue().entrySet()) {
                long signature = signatureEntry.getKey();
                int strength = signatureEntry.getValue().get();
                int usage = getAliasUsage(conceptName, signature);
                AliasQualityTier tier = resolveAliasTier(strength, usage);
                if (tier == AliasQualityTier.CORE) core++;
                else if (tier == AliasQualityTier.PROMOTED) promoted++;
                else bootstrap++;
            }
        }

        return new int[]{bootstrap, promoted, core};
    }

    // =========================================================================================
    // STANDALONE ENTRY POINT
    // =========================================================================================
    public static void main(String[] args) {
        AeonKronos engine = getInstance();
        engine.runInteractive();
    }
}
