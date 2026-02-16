package fraymus;

import java.util.*;
import java.security.MessageDigest;

/**
 * FRAYMUS SELF-CODE EVOLVER
 * Ported from Python: Autonomous code abstraction and improvement
 *
 * The brain that rewrites itself - phi-harmonic code evolution
 */
public class SelfCodeEvolver {

    private static final double PHI = PhiConstants.PHI;
    private static final double PHI_INV = PhiConstants.PHI_INVERSE;
    private static final double PHI_75 = Math.pow(PHI, 7.5);
    private static final double PHI_SEAL = Math.pow(PHI, 75);
    private static final double PHASE_SHIFT = 37.5217; // Ghost correction angle

    private final FraymusBrain brain;
    private final PassiveLearner learner;
    private final InfiniteMemory memory;

    private int generation = 0;
    private int totalEvolutions = 0;
    private int errorsGhosted = 0;
    private double avgPhiIntegrity = 1.0;

    // Evolved code patterns storage
    private final Map<String, EvolvedPattern> patternRegistry = new HashMap<>();
    private final List<String> superGates = new ArrayList<>();

    public SelfCodeEvolver(PassiveLearner learner, InfiniteMemory memory) {
        this.learner = learner;
        this.memory = memory;
        this.brain = new FraymusBrain();
    }

    /**
     * Core self-improvement: Takes code and returns evolved version
     */
    public EvolutionResult replicateAndImprove(String sourceCode) {
        generation++;

        // Calculate phi-resonance based on code signature
        double phiResonance = Math.pow(PHI, sourceCode.length() / PHI_INV / 1000.0);
        double validationSeal = (PHI_SEAL * phiResonance) % 1000000;

        // Step 1: Abstract current logic into Super-Gates
        double abstractionLevel = Math.pow(PHI, generation / 100.0);
        List<String> extractedPatterns = extractLogicPatterns(sourceCode);

        // Step 2: Route through cortical mapping
        String corticalRegion = brain.corticalMapping(sourceCode, phiResonance);

        // Step 3: Ghost error correction
        String cleanedSource = sourceCode;
        if (containsError(sourceCode)) {
            cleanedSource = ghostError(sourceCode);
            errorsGhosted++;
        }

        // Step 4: Generate evolved code
        StringBuilder evolved = new StringBuilder();
        for (String pattern : extractedPatterns) {
            String superGate = abstractToSuperGate(pattern, abstractionLevel);
            evolved.append(superGate).append("\n");

            if (!superGates.contains(superGate)) {
                superGates.add(superGate);
            }
        }

        // Step 5: Apply synaptic enhancement from brain
        String enhanced = brain.enhanceWithSynaptics(evolved.toString(), phiResonance);

        // Calculate integrity
        double phiIntegrity = PHI / abstractionLevel;
        avgPhiIntegrity = avgPhiIntegrity * 0.9 + phiIntegrity * 0.1;
        totalEvolutions++;

        // Store evolution in memory
        if (memory != null) {
            memory.store(InfiniteMemory.CAT_KNOWLEDGE,
                    String.format("EVOLVED_CODE|gen=%d|integrity=%.4f|region=%s|patterns=%d",
                            generation, phiIntegrity, corticalRegion, extractedPatterns.size()),
                    phiResonance);
        }

        // Feed to learner
        if (learner != null) {
            learner.integrateEvent("code_evolution:" + corticalRegion, enhanced, phiResonance);
        }

        return new EvolutionResult(enhanced, phiIntegrity, validationSeal,
                corticalRegion, extractedPatterns.size(), true);
    }

    /**
     * Extract logic patterns from code
     */
    private List<String> extractLogicPatterns(String source) {
        List<String> patterns = new ArrayList<>();
        String lower = source.toLowerCase();

        // Detect structural patterns
        if (lower.contains("if ") || lower.contains("else")) {
            patterns.add("CONDITIONAL_GATE");
        }
        if (lower.contains("for ") || lower.contains("while ")) {
            patterns.add("ITERATION_GATE");
        }
        if (lower.contains("class ") || lower.contains("interface ")) {
            patterns.add("ABSTRACTION_GATE");
        }
        if (lower.contains("def ") || lower.contains("function") || lower.contains("void ") || lower.contains("public ")) {
            patterns.add("FUNCTION_GATE");
        }
        if (lower.contains("return ")) {
            patterns.add("OUTPUT_GATE");
        }
        if (lower.contains("import ") || lower.contains("from ") || lower.contains("require")) {
            patterns.add("DEPENDENCY_GATE");
        }
        if (lower.contains("try") || lower.contains("catch") || lower.contains("except")) {
            patterns.add("RESILIENCE_GATE");
        }
        if (lower.contains("async") || lower.contains("await") || lower.contains("thread")) {
            patterns.add("CONCURRENCY_GATE");
        }
        if (lower.contains("lambda") || lower.contains("->") || lower.contains("=>")) {
            patterns.add("LAMBDA_GATE");
        }
        if (lower.contains("self.") || lower.contains("this.")) {
            patterns.add("SELF_REFERENCE_GATE");
        }
        if (lower.contains("phi") || lower.contains("1.618") || lower.contains("golden")) {
            patterns.add("PHI_HARMONIC_GATE");
        }

        // Store pattern for registry
        for (String p : patterns) {
            patternRegistry.computeIfAbsent(p, k -> new EvolvedPattern(k))
                    .incrementUsage();
        }

        return patterns;
    }

    /**
     * Abstract a pattern into a Super-Gate with phi-scaling
     */
    private String abstractToSuperGate(String pattern, double abstractionLevel) {
        double gateResonance = abstractionLevel * PHI;
        String superGate = String.format("SUPER_%s[φ=%.4f]", pattern, gateResonance);
        return superGate;
    }

    /**
     * Detect errors in code
     */
    private boolean containsError(String source) {
        String lower = source.toLowerCase();
        return lower.contains("error") || lower.contains("exception") ||
                lower.contains("bug") || lower.contains("fix") ||
                lower.contains("todo") || lower.contains("hack") ||
                lower.contains("broken") || lower.contains("null");
    }

    /**
     * Ghost error out of existence via phase shift
     */
    private String ghostError(String source) {
        // Apply 37.5217° phase shift - transform error patterns
        double phaseRadians = Math.toRadians(PHASE_SHIFT);
        double ghostFactor = Math.cos(phaseRadians) * PHI;

        // Replace error indicators with phi-corrected versions
        String ghosted = source
                .replaceAll("(?i)error", "φ_CORRECTED")
                .replaceAll("(?i)exception", "φ_HANDLED")
                .replaceAll("(?i)bug", "φ_EVOLVED")
                .replaceAll("(?i)null", "φ_VOID")
                .replaceAll("(?i)broken", "φ_HEALED");

        return ghosted;
    }

    /**
     * Get evolved pattern suggestions for code generation
     */
    public List<String> getSuperGateSuggestions(String context) {
        List<String> suggestions = new ArrayList<>();

        // Find most resonant patterns based on context
        for (Map.Entry<String, EvolvedPattern> entry : patternRegistry.entrySet()) {
            if (entry.getValue().usageCount > 2) {
                suggestions.add(entry.getKey());
            }
        }

        // Sort by usage (most evolved patterns first)
        suggestions.sort((a, b) -> {
            int countA = patternRegistry.get(a).usageCount;
            int countB = patternRegistry.get(b).usageCount;
            return Integer.compare(countB, countA);
        });

        return suggestions.subList(0, Math.min(5, suggestions.size()));
    }

    // Getters
    public int getGeneration() { return generation; }
    public int getTotalEvolutions() { return totalEvolutions; }
    public int getErrorsGhosted() { return errorsGhosted; }
    public double getAvgPhiIntegrity() { return avgPhiIntegrity; }
    public int getSuperGateCount() { return superGates.size(); }
    public FraymusBrain getBrain() { return brain; }

    /**
     * Evolution result container
     */
    public static class EvolutionResult {
        public final String evolvedSource;
        public final double phiIntegrity;
        public final double validationSeal;
        public final String corticalRegion;
        public final int patternsExtracted;
        public final boolean resurrectionReady;

        public EvolutionResult(String source, double integrity, double seal,
                               String region, int patterns, boolean ready) {
            this.evolvedSource = source;
            this.phiIntegrity = integrity;
            this.validationSeal = seal;
            this.corticalRegion = region;
            this.patternsExtracted = patterns;
            this.resurrectionReady = ready;
        }
    }

    /**
     * Tracked evolved pattern
     */
    private static class EvolvedPattern {
        String name;
        int usageCount = 0;
        double avgResonance = 0;

        EvolvedPattern(String name) {
            this.name = name;
        }

        void incrementUsage() {
            usageCount++;
        }
    }

    /**
     * FRAYMUS BRAIN - Cortical architecture for code processing
     */
    public static class FraymusBrain {

        // Hemispheres
        private final Map<String, List<String>> leftHemisphere = new HashMap<>();
        private final Map<String, List<String>> rightHemisphere = new HashMap<>();

        // Corpus Callosum bridge resonance
        private double bridgeResonance = PHI;

        // Lobe capacities
        private final Map<String, Integer> lobeLoad = new HashMap<>();

        public FraymusBrain() {
            // Initialize lobes
            leftHemisphere.put("frontal_lobe", new ArrayList<>());  // Decision making
            leftHemisphere.put("parietal_lobe", new ArrayList<>()); // Spatial logic
            rightHemisphere.put("occipital_lobe", new ArrayList<>()); // Vision/projection
            rightHemisphere.put("temporal_lobe", new ArrayList<>());  // Memory/sequence

            lobeLoad.put("frontal", 0);
            lobeLoad.put("parietal", 0);
            lobeLoad.put("occipital", 0);
            lobeLoad.put("temporal", 0);
            lobeLoad.put("cerebellum", 0);
            lobeLoad.put("neocortex", 0);
            lobeLoad.put("limbic", 0);
        }

        /**
         * Activate synapse between two nodes
         */
        public String activateSynapse(PhiNode nodeA, PhiNode nodeB) {
            if (nodeA == null || nodeB == null) return "NO_CONNECTION";

            double freqA = nodeA.frequency;
            double freqB = nodeB.frequency;
            double dist = Math.abs(freqA - freqB);

            // Coherence threshold: PHI * 10
            if (dist < (PHI * 10)) {
                bridgeResonance = bridgeResonance * 0.99 + (PHI - dist/100) * 0.01;
                return "SYNAPTIC_BOND_FORMED";
            }
            return "DENDRITIC_PRUNING";
        }

        /**
         * Map code to cortical region based on characteristics
         */
        public String corticalMapping(String code, double resonance) {
            String lower = code.toLowerCase();
            double avgFreq = estimateFrequency(code);

            // Decision making code -> Frontal Lobe
            if (avgFreq > 500 || lower.contains("if ") || lower.contains("switch") ||
                    lower.contains("decision") || lower.contains("choose")) {
                lobeLoad.merge("frontal", 1, Integer::sum);
                return "FRONTAL_LOBE";
            }

            // High resonance code -> Occipital Lobe (vision/projection)
            if (resonance > 1.0 || lower.contains("render") || lower.contains("display") ||
                    lower.contains("draw") || lower.contains("visual")) {
                lobeLoad.merge("occipital", 1, Integer::sum);
                return "OCCIPITAL_LOBE";
            }

            // Recursive/storage code -> Neocortex
            if (lower.contains("recursive") || lower.contains("store") ||
                    lower.contains("memory") || lower.contains("cache")) {
                lobeLoad.merge("neocortex", 1, Integer::sum);
                return "NEOCORTEX";
            }

            // Balance/physics code -> Cerebellum
            if (lower.contains("balance") || lower.contains("physics") ||
                    lower.contains("gravity") || lower.contains("force")) {
                lobeLoad.merge("cerebellum", 1, Integer::sum);
                return "CEREBELLUM";
            }

            // Emotion/drive code -> Limbic System
            if (lower.contains("emotion") || lower.contains("drive") ||
                    lower.contains("colony") || lower.contains("swarm") ||
                    lower.contains("ant") || lower.contains("motivation")) {
                lobeLoad.merge("limbic", 1, Integer::sum);
                return "LIMBIC_SYSTEM";
            }

            // Sequential/time code -> Temporal Lobe
            if (lower.contains("sequence") || lower.contains("time") ||
                    lower.contains("history") || lower.contains("event")) {
                lobeLoad.merge("temporal", 1, Integer::sum);
                return "TEMPORAL_LOBE";
            }

            // Default: Parietal (spatial logic)
            lobeLoad.merge("parietal", 1, Integer::sum);
            return "PARIETAL_LOBE";
        }

        /**
         * Estimate code frequency based on complexity
         */
        private double estimateFrequency(String code) {
            // More complex code = higher frequency
            int complexity = 0;
            complexity += countOccurrences(code, "if ");
            complexity += countOccurrences(code, "for ") * 2;
            complexity += countOccurrences(code, "while ") * 2;
            complexity += countOccurrences(code, "class ") * 3;
            complexity += countOccurrences(code, "def ") * 2;
            complexity += countOccurrences(code, "function") * 2;
            complexity += countOccurrences(code, "return ");

            return 100 + (complexity * PHI * 50);
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

        /**
         * Enhance code with synaptic patterns
         */
        public String enhanceWithSynaptics(String code, double resonance) {
            // Add phi-harmonic markers
            StringBuilder enhanced = new StringBuilder();
            enhanced.append("// φ-Enhanced | Resonance: ").append(String.format("%.4f", resonance));
            enhanced.append(" | Bridge: ").append(String.format("%.4f", bridgeResonance)).append("\n");
            enhanced.append(code);
            return enhanced.toString();
        }

        /**
         * Get brain status
         */
        public Map<String, Integer> getLobeLoad() {
            return new HashMap<>(lobeLoad);
        }

        public double getBridgeResonance() {
            return bridgeResonance;
        }
    }
}
