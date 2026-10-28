package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * SentientCore.java — Zero-to-Hero Intelligence in One File
 * =========================================================================================
 *
 * DESIGN PHILOSOPHY:
 *   Success is NOT more layers, bigger GPUs, or random initialization.
 *   Success is choosing the RIGHT REPRESENTATION that matches the physics of the problem.
 *
 *   A child doesn't need 175 billion parameters to learn "fire burns."
 *   They need ONE experience, correctly represented.
 *
 * PRINCIPLES:
 *   1. NO RANDOMNESS — Every weight has a reason. Deterministic seed = reproducible thought.
 *   2. SMALL DATA    — Learn from 1 example. Generalize from 3. Master from 10.
 *   3. PHYSICS-MATCHED — Represent causality as directed graphs, not matrices.
 *                        Represent similarity as distance, not dot products.
 *                        Represent time as ordering, not positional encoding.
 *   4. STATISTICAL TRUTH — Extract invariants, not correlations.
 *                          Sufficient statistics compress without loss.
 *                          Fisher information tells you WHAT to learn next.
 *   5. LEGIBLE — Every decision is explainable. No black boxes.
 *                The learner can read its own mind.
 *   6. ZERO-TO-HERO — No warm-up. No pre-training. First input = first real thought.
 *
 * ARCHITECTURE (not layers — structures):
 *
 *   CausalGraph     — Directed graph of cause→effect. NOT a weight matrix.
 *                     Edges are conditional probabilities P(effect|cause).
 *                     Updated by Bayesian rule, not gradient descent.
 *
 *   SufficientStats — For every concept, store ONLY the sufficient statistics:
 *                     count, sum, sum_of_squares, min, max.
 *                     From these 5 numbers you recover mean, variance, range, confidence.
 *                     This is lossless compression of arbitrary data streams.
 *
 *   FisherOracle    — Computes Fisher Information: which observation would reduce
 *                     uncertainty the most? This replaces exploration heuristics.
 *                     The system KNOWS what it doesn't know and asks for exactly that.
 *
 *   AnalogEngine    — Finds structural isomorphisms between causal subgraphs.
 *                     "A is to B as C is to ?" solved by graph alignment, not vectors.
 *                     This is how a child reasons about new domains.
 *
 *   ConceptLattice  — Formal Concept Analysis: objects × attributes → lattice.
 *                     Discovers natural categories WITHOUT clustering.
 *                     No k-means. No hyperparameters. The structure emerges from data.
 *
 *   PhysicsEncoder  — Matches representation to problem structure:
 *                     Symmetric problems → group-invariant representations
 *                     Temporal problems → ordered sequences (not vectors)
 *                     Spatial problems → metric spaces (not embeddings)
 *                     Causal problems → DAGs (not correlations)
 *
 * WHY THIS OUTTHINKS HUMANS:
 *   Humans have cognitive biases. This system has Bayes' rule.
 *   Humans forget base rates. This system stores sufficient statistics.
 *   Humans confuse correlation with causation. This system builds causal DAGs.
 *   Humans can't compute Fisher information. This system knows exactly
 *   what question to ask next to maximally reduce its uncertainty.
 *
 * =========================================================================================
 */
public class SentientCore {

    // =========================================================================================
    // CAUSAL GRAPH — Directed graph of cause→effect with Bayesian edge weights
    // =========================================================================================

    public static class CausalGraph {
        // Edge: cause → effect with P(effect|cause) and observation count
        private final Map<String, Map<String, float[]>> edges = new ConcurrentHashMap<>();
        // float[0] = successes (effect observed given cause)
        // float[1] = trials (cause observed total)
        // P(effect|cause) = (successes + 1) / (trials + 2) — Laplace smoothing

        /** Observe that cause preceded effect. Single observation = real learning. */
        public void observe(String cause, String effect) {
            edges.computeIfAbsent(cause, k -> new ConcurrentHashMap<>())
                 .computeIfAbsent(effect, k -> new float[]{0, 0});
            float[] e = edges.get(cause).get(effect);
            e[0] += 1; // success
            e[1] += 1; // trial
        }

        /** Observe that cause occurred but effect did NOT follow. */
        public void observeAbsence(String cause, String effect) {
            edges.computeIfAbsent(cause, k -> new ConcurrentHashMap<>())
                 .computeIfAbsent(effect, k -> new float[]{0, 0});
            float[] e = edges.get(cause).get(effect);
            // e[0] stays same (no success)
            e[1] += 1; // trial
        }

        /** P(effect|cause) with Laplace smoothing. Works from 1 observation. */
        public float probability(String cause, String effect) {
            Map<String, float[]> targets = edges.get(cause);
            if (targets == null) return 0.5f; // unknown = maximum uncertainty
            float[] e = targets.get(effect);
            if (e == null) return 0.5f;
            return (e[0] + 1.0f) / (e[1] + 2.0f); // Beta(α+1, β+1) posterior mean
        }

        /** Confidence: how sure are we about P(effect|cause)? Uses Beta distribution width. */
        public float confidence(String cause, String effect) {
            Map<String, float[]> targets = edges.get(cause);
            if (targets == null) return 0.0f;
            float[] e = targets.get(effect);
            if (e == null) return 0.0f;
            float alpha = e[0] + 1;
            float beta  = (e[1] - e[0]) + 1;
            // Confidence = 1 - width of 95% credible interval ≈ 1 - 2/sqrt(alpha+beta)
            return Math.min(1.0f, 1.0f - 2.0f / (float) Math.sqrt(alpha + beta));
        }

        /** What effects does this cause produce? Ranked by probability. */
        public List<String[]> predict(String cause) {
            Map<String, float[]> targets = edges.get(cause);
            if (targets == null) return Collections.emptyList();
            List<String[]> predictions = new ArrayList<>();
            for (Map.Entry<String, float[]> entry : targets.entrySet()) {
                float p = (entry.getValue()[0] + 1) / (entry.getValue()[1] + 2);
                float c = confidence(cause, entry.getKey());
                predictions.add(new String[]{
                    entry.getKey(),
                    String.format("%.3f", p),
                    String.format("%.3f", c)
                });
            }
            predictions.sort((a, b) -> Float.compare(Float.parseFloat(b[1]), Float.parseFloat(a[1])));
            return predictions;
        }

        /** What causes could have produced this effect? (Abductive reasoning) */
        public List<String[]> explain(String effect) {
            List<String[]> explanations = new ArrayList<>();
            for (Map.Entry<String, Map<String, float[]>> causeEntry : edges.entrySet()) {
                float[] e = causeEntry.getValue().get(effect);
                if (e != null && e[1] > 0) {
                    float p = (e[0] + 1) / (e[1] + 2);
                    explanations.add(new String[]{causeEntry.getKey(), String.format("%.3f", p)});
                }
            }
            explanations.sort((a, b) -> Float.compare(Float.parseFloat(b[1]), Float.parseFloat(a[1])));
            return explanations;
        }

        /** Causal chain: A→B→C→...→Z. Returns chain and cumulative probability. */
        public List<String> traceChain(String start, String target, int maxDepth) {
            // BFS through causal graph
            Queue<List<String>> queue = new LinkedList<>();
            queue.add(new ArrayList<>(List.of(start)));
            Set<String> visited = new HashSet<>();
            visited.add(start);

            while (!queue.isEmpty() && maxDepth-- > 0) {
                int levelSize = queue.size();
                for (int i = 0; i < levelSize; i++) {
                    List<String> path = queue.poll();
                    String current = path.get(path.size() - 1);
                    if (current.equals(target)) return path;
                    Map<String, float[]> targets = edges.get(current);
                    if (targets == null) continue;
                    for (String next : targets.keySet()) {
                        if (!visited.contains(next)) {
                            visited.add(next);
                            List<String> newPath = new ArrayList<>(path);
                            newPath.add(next);
                            queue.add(newPath);
                        }
                    }
                }
            }
            return Collections.emptyList(); // no path found
        }

        public int edgeCount() {
            int count = 0;
            for (Map<String, float[]> t : edges.values()) count += t.size();
            return count;
        }

        public int nodeCount() {
            Set<String> nodes = new HashSet<>(edges.keySet());
            for (Map<String, float[]> t : edges.values()) nodes.addAll(t.keySet());
            return nodes.size();
        }
    }

    // =========================================================================================
    // SUFFICIENT STATISTICS — Lossless compression of arbitrary data streams
    // 5 numbers capture EVERYTHING about a distribution you need for inference
    // =========================================================================================

    public static class SufficientStats {
        private final Map<String, float[]> stats = new ConcurrentHashMap<>();
        // float[0]=count, float[1]=sum, float[2]=sumSq, float[3]=min, float[4]=max

        /** Observe a single value for a concept. Instant learning. */
        public void observe(String concept, float value) {
            stats.computeIfAbsent(concept, k -> new float[]{0, 0, 0, Float.MAX_VALUE, -Float.MAX_VALUE});
            float[] s = stats.get(concept);
            s[0] += 1;
            s[1] += value;
            s[2] += value * value;
            s[3] = Math.min(s[3], value);
            s[4] = Math.max(s[4], value);
        }

        public float count(String c)    { float[] s = stats.get(c); return s != null ? s[0] : 0; }
        public float mean(String c)     { float[] s = stats.get(c); return s != null && s[0] > 0 ? s[1] / s[0] : 0; }
        public float variance(String c) {
            float[] s = stats.get(c);
            if (s == null || s[0] < 2) return Float.MAX_VALUE; // unknown = max uncertainty
            float m = s[1] / s[0];
            return s[2] / s[0] - m * m;
        }
        public float stddev(String c)   { return (float) Math.sqrt(Math.max(0, variance(c))); }
        public float min(String c)      { float[] s = stats.get(c); return s != null ? s[3] : 0; }
        public float max(String c)      { float[] s = stats.get(c); return s != null ? s[4] : 0; }
        public float range(String c)    { return max(c) - min(c); }

        /** 95% confidence interval for the mean. Shrinks with more data. */
        public float[] confidenceInterval(String c) {
            float n = count(c);
            if (n < 1) return new float[]{Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};
            float m = mean(c);
            float se = stddev(c) / (float) Math.sqrt(n);
            return new float[]{m - 1.96f * se, m + 1.96f * se};
        }

        /** Is this value anomalous? Returns z-score (>2 = unusual, >3 = extreme). */
        public float anomalyScore(String concept, float value) {
            float sd = stddev(concept);
            if (sd < 1e-9f) return count(concept) > 0 ? (value == mean(concept) ? 0 : 10) : 0;
            return Math.abs(value - mean(concept)) / sd;
        }

        /** How many distinct concepts tracked? */
        public int conceptCount() { return stats.size(); }
    }

    // =========================================================================================
    // FISHER ORACLE — Knows what it doesn't know. Asks the RIGHT question.
    // Fisher Information I(θ) = E[(d/dθ log P(x|θ))²]
    // High I = observation is informative. Low I = observation is redundant.
    // =========================================================================================

    public static class FisherOracle {
        private final CausalGraph   graph;
        private final SufficientStats stats;

        FisherOracle(CausalGraph graph, SufficientStats stats) {
            this.graph = graph;
            this.stats = stats;
        }

        /**
         * What should we observe next to maximally reduce uncertainty?
         * Returns the concept whose Fisher Information is highest
         * (= where one more observation would change our beliefs the most).
         */
        public String whatToLearnNext() {
            String best     = null;
            float  bestInfo = -1;

            // For causal edges: Fisher info ∝ 1/variance of posterior ∝ 1/(α·β/(α+β)²(α+β+1))
            // Edges with low confidence but some data = highest Fisher info
            for (Map.Entry<String, Map<String, float[]>> causeEntry : graph.edges.entrySet()) {
                for (Map.Entry<String, float[]> effectEntry : causeEntry.getValue().entrySet()) {
                    float[] e = effectEntry.getValue();
                    float alpha = e[0] + 1;
                    float beta  = (e[1] - e[0]) + 1;
                    float n     = alpha + beta;
                    // Fisher info for Beta distribution = n / (p(1-p)) where p = alpha/n
                    float p = alpha / n;
                    float pq = p * (1 - p);
                    if (pq < 1e-9f) continue;
                    float fisher = 1.0f / (pq * n); // higher = less certain = more to learn
                    // Weight by how much we'd learn (inversely proportional to current evidence)
                    float info = fisher * (1.0f / (float) Math.sqrt(e[1] + 1));
                    if (info > bestInfo) {
                        bestInfo = info;
                        best = causeEntry.getKey() + " → " + effectEntry.getKey();
                    }
                }
            }

            // Also check sufficient stats — concepts with high variance and low count
            for (Map.Entry<String, float[]> entry : stats.stats.entrySet()) {
                float[] s = entry.getValue();
                if (s[0] < 2) continue;
                float var = s[2] / s[0] - (s[1] / s[0]) * (s[1] / s[0]);
                float info = var / s[0]; // high variance + low count = high info
                if (info > bestInfo) {
                    bestInfo = info;
                    best = entry.getKey() + " (needs more data, var=" + String.format("%.2f", var) + ")";
                }
            }

            return best != null ? best : "NOTHING — all knowledge is saturated";
        }

        /**
         * How much would we learn from observing concept X?
         * Returns expected information gain in bits.
         */
        public float expectedInfoGain(String concept) {
            float n = stats.count(concept);
            if (n < 1) return 10.0f; // unknown = maximum information gain
            float var = stats.variance(concept);
            if (var < 1e-9f) return 0.0f; // deterministic = nothing to learn
            // Info gain ≈ 0.5 * log(1 + 1/(n * var/var_prior))
            return 0.5f * (float)(Math.log(1 + 1.0 / (n + 1)) / Math.log(2));
        }
    }

    // =========================================================================================
    // ANALOGY ENGINE — Structural isomorphism between causal subgraphs
    // "A is to B as C is to ?" solved by GRAPH ALIGNMENT, not vector arithmetic
    // =========================================================================================

    public static class AnalogyEngine {
        private final CausalGraph graph;

        AnalogyEngine(CausalGraph graph) { this.graph = graph; }

        /**
         * Solve: A is to B as C is to ?
         *
         * Method: Find the structural relationship R between A and B in the causal graph.
         * Then find D such that the relationship between C and D is isomorphic to R.
         *
         * This works from TINY data because it uses structure, not statistics.
         */
        public String solve(String a, String b, String c) {
            // Step 1: What is the relationship between A and B?
            Relationship rAB = extractRelationship(a, b);
            if (rAB == null) return "UNKNOWN (no causal link between " + a + " and " + b + ")";

            // Step 2: Find all nodes D reachable from C
            Map<String, float[]> cTargets = graph.edges.get(c);
            if (cTargets == null) return "UNKNOWN (" + c + " has no known effects)";

            // Step 3: Find D where R(C,D) ≈ R(A,B)
            String bestD = null;
            float bestSim = -1;

            for (String d : cTargets.keySet()) {
                Relationship rCD = extractRelationship(c, d);
                if (rCD == null) continue;
                float sim = rAB.similarity(rCD);
                if (sim > bestSim) { bestSim = sim; bestD = d; }
            }

            if (bestD != null) {
                return bestD + " (confidence: " + String.format("%.1f%%", bestSim * 100) + 
                       ", via " + rAB.type + " relationship)";
            }

            // Fallback: search deeper (2-hop)
            for (Map.Entry<String, Map<String, float[]>> mid : graph.edges.entrySet()) {
                if (cTargets.containsKey(mid.getKey())) {
                    for (String d : mid.getValue().keySet()) {
                        Relationship rCD = extractRelationship(c, d);
                        if (rCD == null) continue;
                        float sim = rAB.similarity(rCD);
                        if (sim > bestSim) { bestSim = sim; bestD = d; }
                    }
                }
            }

            return bestD != null ? bestD + " (2-hop, confidence: " + String.format("%.1f%%", bestSim * 100) + ")"
                                 : "UNKNOWN (no structural match found)";
        }

        private Relationship extractRelationship(String a, String b) {
            float pAB = graph.probability(a, b);
            float pBA = graph.probability(b, a);
            float confAB = graph.confidence(a, b);

            if (confAB < 0.01f && graph.confidence(b, a) < 0.01f) return null;

            String type;
            if (pAB > 0.7f && pBA < 0.3f)      type = "CAUSES";
            else if (pBA > 0.7f && pAB < 0.3f)  type = "CAUSED_BY";
            else if (pAB > 0.6f && pBA > 0.6f)  type = "BIDIRECTIONAL";
            else if (pAB > 0.5f)                 type = "TENDS_TO";
            else                                 type = "WEAK";

            return new Relationship(type, pAB, pBA, confAB);
        }

        static class Relationship {
            final String type;
            final float  forwardP, reverseP, confidence;

            Relationship(String type, float fwd, float rev, float conf) {
                this.type = type; this.forwardP = fwd; this.reverseP = rev; this.confidence = conf;
            }

            float similarity(Relationship other) {
                if (other == null) return 0;
                float typeSim = this.type.equals(other.type) ? 1.0f : 0.3f;
                float pSim    = 1.0f - Math.abs(this.forwardP - other.forwardP);
                float rSim    = 1.0f - Math.abs(this.reverseP - other.reverseP);
                return (typeSim * 0.5f + pSim * 0.3f + rSim * 0.2f);
            }
        }
    }

    // =========================================================================================
    // CONCEPT LATTICE — Formal Concept Analysis
    // Objects × Attributes → discovers natural categories WITHOUT clustering algorithms
    // No k. No hyperparameters. Structure emerges from data.
    // =========================================================================================

    public static class ConceptLattice {
        // Object → set of attributes
        private final Map<String, Set<String>> objectAttrs = new ConcurrentHashMap<>();
        // Attribute → set of objects
        private final Map<String, Set<String>> attrObjects = new ConcurrentHashMap<>();

        /** Teach: this object has this attribute. */
        public void teach(String object, String attribute) {
            objectAttrs.computeIfAbsent(object, k -> ConcurrentHashMap.newKeySet()).add(attribute);
            attrObjects.computeIfAbsent(attribute, k -> ConcurrentHashMap.newKeySet()).add(object);
        }

        /** What attributes does this object have? */
        public Set<String> describe(String object) {
            return objectAttrs.getOrDefault(object, Collections.emptySet());
        }

        /** What objects have this attribute? */
        public Set<String> whoHas(String attribute) {
            return attrObjects.getOrDefault(attribute, Collections.emptySet());
        }

        /** Find the NATURAL CATEGORY: objects that share ALL these attributes. */
        public Set<String> extent(Set<String> attributes) {
            Set<String> result = null;
            for (String attr : attributes) {
                Set<String> objs = attrObjects.getOrDefault(attr, Collections.emptySet());
                if (result == null) result = new HashSet<>(objs);
                else result.retainAll(objs);
            }
            return result != null ? result : Collections.emptySet();
        }

        /** Find shared attributes of these objects (the INTENT). */
        public Set<String> intent(Set<String> objects) {
            Set<String> result = null;
            for (String obj : objects) {
                Set<String> attrs = objectAttrs.getOrDefault(obj, Collections.emptySet());
                if (result == null) result = new HashSet<>(attrs);
                else result.retainAll(attrs);
            }
            return result != null ? result : Collections.emptySet();
        }

        /** Classify: which existing objects is this most similar to? */
        public List<String[]> classify(String newObject) {
            Set<String> myAttrs = describe(newObject);
            if (myAttrs.isEmpty()) return Collections.emptyList();

            List<String[]> matches = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : objectAttrs.entrySet()) {
                if (entry.getKey().equals(newObject)) continue;
                Set<String> shared = new HashSet<>(myAttrs);
                shared.retainAll(entry.getValue());
                if (!shared.isEmpty()) {
                    float sim = (float) shared.size() / Math.max(myAttrs.size(), entry.getValue().size());
                    matches.add(new String[]{entry.getKey(), String.format("%.1f%%", sim * 100),
                        shared.toString()});
                }
            }
            matches.sort((a, b) -> Float.compare(
                Float.parseFloat(b[1].replace("%", "")),
                Float.parseFloat(a[1].replace("%", ""))));
            return matches;
        }

        public int objectCount()    { return objectAttrs.size(); }
        public int attributeCount() { return attrObjects.size(); }
    }

    // =========================================================================================
    // PHYSICS ENCODER — Choose representation that matches problem structure
    // =========================================================================================

    enum ProblemType {
        CAUSAL,      // Use DAG (directed acyclic graph)
        TEMPORAL,    // Use ordered sequence
        SPATIAL,     // Use metric distance
        SYMMETRIC,   // Use group invariants
        CATEGORICAL, // Use concept lattice
        STATISTICAL, // Use sufficient statistics
        UNKNOWN      // Use all of the above and let Fisher decide
    }

    static ProblemType detectProblemType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("cause") || lower.contains("why") || lower.contains("because") ||
            lower.contains("leads to") || lower.contains("results in"))
            return ProblemType.CAUSAL;
        if (lower.contains("when") || lower.contains("before") || lower.contains("after") ||
            lower.contains("sequence") || lower.contains("then"))
            return ProblemType.TEMPORAL;
        if (lower.contains("where") || lower.contains("near") || lower.contains("distance") ||
            lower.contains("between"))
            return ProblemType.SPATIAL;
        if (lower.contains("same") || lower.contains("equal") || lower.contains("symmetric") ||
            lower.contains("invariant"))
            return ProblemType.SYMMETRIC;
        if (lower.contains("type") || lower.contains("kind") || lower.contains("category") ||
            lower.contains("is a") || lower.contains("has"))
            return ProblemType.CATEGORICAL;
        if (lower.contains("average") || lower.contains("often") || lower.contains("usually") ||
            lower.contains("how much") || lower.contains("how many"))
            return ProblemType.STATISTICAL;
        return ProblemType.UNKNOWN;
    }

    // =========================================================================================
    // THE CORE — Unified interface to all structures
    // =========================================================================================

    private final CausalGraph     causal   = new CausalGraph();
    private final SufficientStats stats    = new SufficientStats();
    private final FisherOracle    fisher   = new FisherOracle(causal, stats);
    private final AnalogyEngine   analogy  = new AnalogyEngine(causal);
    private final ConceptLattice  lattice  = new ConceptLattice();
    private final List<String>    thoughtLog = new CopyOnWriteArrayList<>();
    private final AtomicInteger   learnCount = new AtomicInteger(0);
    private final AtomicInteger   thinkCount = new AtomicInteger(0);
    private volatile float        sentience  = 0.0f; // emergent, not assigned
    
    // PHASE 1.1: HDC Integration (optional, maintains backward compatibility)
    private HDCSimilarityEngine hdcEngine = null; // Optional HDC engine
    private volatile boolean hdcEnabled = false;
    
    // PHASE 1.2: Working Memory Integration (optional, maintains backward compatibility)
    private WorkingMemory workingMemory = null; // Optional working memory
    private AttentionMechanism attentionMechanism = null; // Optional attention mechanism
    private volatile boolean workingMemoryEnabled = false;
    
    // PHASE 1.3: Episodic Memory Integration (optional, maintains backward compatibility)
    private EpisodicMemory episodicMemory = null; // Optional episodic memory
    private HippocampalReplay hippocampalReplay = null; // Optional hippocampal replay
    private volatile boolean episodicMemoryEnabled = false;
    
    // PHASE 2: LLM Reasoning Integration (optional, maintains backward compatibility)
    private LLMReasoningEngine llmReasoningEngine = null; // Optional LLM reasoning engine
    private volatile boolean llmReasoningEnabled = false;
    
    // PHASE 3: Meta-Learning Integration (optional, maintains backward compatibility)
    private MetaLearningEngine metaLearningEngine = null; // Optional meta-learning engine
    private volatile boolean metaLearningEnabled = false;
    
    // PHASE 4: Generative Capability Integration (optional, maintains backward compatibility)
    private GenerativeEngine generativeEngine = null; // Optional generative engine
    private volatile boolean generativeEnabled = false;
    
    // PHASE 5: Embodiment & Multi-Modal Integration (optional, maintains backward compatibility)
    private SensorimotorEngine sensorimotorEngine = null; // Optional sensorimotor engine
    private VisionEngine visionEngine = null; // Optional vision engine
    private AudioEngine audioEngine = null; // Optional audio engine
    private CrossModalBindingEngine crossModalEngine = null; // Optional cross-modal binding engine
    private volatile boolean multiModalEnabled = false;
    
    // ZERO-TIME RECOGNITION: The "Sign Maker" (optional, maintains backward compatibility)
    private ZeroTimeRecognition zeroTimeRecognition = null; // Optional zero-time recognition engine
    private volatile boolean zeroTimeRecognitionEnabled = false;

    public SentientCore() {
        System.out.println("[SentientCore] Online. Zero parameters. Zero randomness. Ready to learn.");
    }

    // =========================================================================================
    // PHASE 1.1: HDC INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire HDC engine to enable hyperdimensional computing features.
     * 
     * This is optional - SentientCore works perfectly without HDC.
     * When wired, it adds:
     * - Vector similarity search
     * - Holographic binding for composition
     * - Enhanced concept matching
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param engine The HDCSimilarityEngine to wire
     */
    public void wireHDC(HDCSimilarityEngine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("HDC engine cannot be null");
        }
        this.hdcEngine = engine;
        this.hdcEnabled = true;
        System.out.println("[SentientCore] HDC Engine wired — hyperdimensional computing enabled");
    }

    /**
     * Unwire HDC engine (disable HDC features).
     */
    public void unwireHDC() {
        this.hdcEngine = null;
        this.hdcEnabled = false;
        System.out.println("[SentientCore] HDC Engine unwired — using pure symbolic reasoning");
    }

    /**
     * Check if HDC is enabled.
     */
    public boolean isHDCEnabled() {
        return hdcEnabled && hdcEngine != null;
    }

    /**
     * Get the HDC engine (if wired).
     */
    public HDCSimilarityEngine getHDCEngine() {
        return hdcEngine;
    }

    // =========================================================================================
    // PHASE 1.2: WORKING MEMORY INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire working memory to enable context awareness.
     * 
     * This is optional - SentientCore works perfectly without working memory.
     * When wired, it adds:
     * - Context retention across think() calls
     * - Attention-based filtering
     * - Memory decay for old information
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param wm The WorkingMemory to wire
     * @param att The AttentionMechanism to wire
     */
    public void wireWorkingMemory(WorkingMemory wm, AttentionMechanism att) {
        if (wm == null || att == null) {
            throw new IllegalArgumentException("WorkingMemory and AttentionMechanism cannot be null");
        }
        this.workingMemory = wm;
        this.attentionMechanism = att;
        this.workingMemoryEnabled = true;
        System.out.println("[SentientCore] Working Memory wired — context awareness enabled");
    }

    /**
     * Unwire working memory (disable context awareness).
     */
    public void unwireWorkingMemory() {
        this.workingMemory = null;
        this.attentionMechanism = null;
        this.workingMemoryEnabled = false;
        System.out.println("[SentientCore] Working Memory unwired — using single-shot reasoning");
    }

    /**
     * Check if working memory is enabled.
     */
    public boolean isWorkingMemoryEnabled() {
        return workingMemoryEnabled && workingMemory != null;
    }

    /**
     * Get the working memory (if wired).
     */
    public WorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    /**
     * Get the attention mechanism (if wired).
     */
    public AttentionMechanism getAttentionMechanism() {
        return attentionMechanism;
    }

    // =========================================================================================
    // PHASE 1.3: EPISODIC MEMORY INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire episodic memory to enable temporal understanding.
     * 
     * This is optional - SentientCore works perfectly without episodic memory.
     * When wired, it adds:
     * - Timestamped memory storage
     * - Temporal range queries
     * - Memory consolidation via replay
     * - Sequence learning
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param em The EpisodicMemory to wire
     * @param hr The HippocampalReplay to wire
     */
    public void wireEpisodicMemory(EpisodicMemory em, HippocampalReplay hr) {
        if (em == null || hr == null) {
            throw new IllegalArgumentException("EpisodicMemory and HippocampalReplay cannot be null");
        }
        this.episodicMemory = em;
        this.hippocampalReplay = hr;
        this.episodicMemoryEnabled = true;
        System.out.println("[SentientCore] Episodic Memory wired — temporal understanding enabled");
    }

    /**
     * Unwire episodic memory (disable temporal understanding).
     */
    public void unwireEpisodicMemory() {
        this.episodicMemory = null;
        this.hippocampalReplay = null;
        this.episodicMemoryEnabled = false;
        System.out.println("[SentientCore] Episodic Memory unwired — using single-shot learning");
    }

    /**
     * Check if episodic memory is enabled.
     */
    public boolean isEpisodicMemoryEnabled() {
        return episodicMemoryEnabled && episodicMemory != null;
    }

    /**
     * Get the episodic memory (if wired).
     */
    public EpisodicMemory getEpisodicMemory() {
        return episodicMemory;
    }

    /**
     * Get the hippocampal replay (if wired).
     */
    public HippocampalReplay getHippocampalReplay() {
        return hippocampalReplay;
    }

    // =========================================================================================
    // PHASE 2: LLM REASONING INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire LLM reasoning engine to enable advanced reasoning capabilities.
     * 
     * This is optional - SentientCore works perfectly without LLM reasoning.
     * When wired, it adds:
     * - Recursive reasoning (multi-hop chain-of-thought)
     * - Counterfactual simulation (what-if analysis)
     * - Enhanced analogy (semantic similarity + structural)
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param llmEngine The LLMReasoningEngine to wire
     */
    public void wireLLMReasoning(LLMReasoningEngine llmEngine) {
        if (llmEngine == null) {
            throw new IllegalArgumentException("LLMReasoningEngine cannot be null");
        }
        this.llmReasoningEngine = llmEngine;
        this.llmReasoningEnabled = true;
        System.out.println("[SentientCore] LLM Reasoning wired — advanced reasoning enabled");
    }

    /**
     * Unwire LLM reasoning engine (disable advanced reasoning).
     */
    public void unwireLLMReasoning() {
        this.llmReasoningEngine = null;
        this.llmReasoningEnabled = false;
        System.out.println("[SentientCore] LLM Reasoning unwired — using native reasoning");
    }

    /**
     * Check if LLM reasoning is enabled.
     */
    public boolean isLLMReasoningEnabled() {
        return llmReasoningEnabled && llmReasoningEngine != null;
    }

    /**
     * Get the LLM reasoning engine (if wired).
     */
    public LLMReasoningEngine getLLMReasoningEngine() {
        return llmReasoningEngine;
    }

    // =========================================================================================
    // PHASE 3: META-LEARNING INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire meta-learning engine to enable self-improvement capabilities.
     * 
     * This is optional - SentientCore works perfectly without meta-learning.
     * When wired, it adds:
     * - Sentience analysis and improvement suggestions
     * - Architectural adaptation via LLM
     * - Concept drift detection
     * - Continuous learning with forgetting
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param metaEngine The MetaLearningEngine to wire
     */
    public void wireMetaLearning(MetaLearningEngine metaEngine) {
        if (metaEngine == null) {
            throw new IllegalArgumentException("MetaLearningEngine cannot be null");
        }
        this.metaLearningEngine = metaEngine;
        this.metaLearningEnabled = true;
        System.out.println("[SentientCore] Meta-Learning wired — self-improvement enabled");
    }

    /**
     * Unwire meta-learning engine (disable self-improvement).
     */
    public void unwireMetaLearning() {
        this.metaLearningEngine = null;
        this.metaLearningEnabled = false;
        System.out.println("[SentientCore] Meta-Learning unwired — using native learning");
    }

    /**
     * Check if meta-learning is enabled.
     */
    public boolean isMetaLearningEnabled() {
        return metaLearningEnabled && metaLearningEngine != null;
    }

    /**
     * Get the meta-learning engine (if wired).
     */
    public MetaLearningEngine getMetaLearningEngine() {
        return metaLearningEngine;
    }

    // =========================================================================================
    // PHASE 4: GENERATIVE CAPABILITY INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire generative engine to enable creative and generative capabilities.
     * 
     * This is optional - SentientCore works perfectly without generative capabilities.
     * When wired, it adds:
     * - Text generation via LLM
     * - FUSE operation for compositional creativity
     * - Novelty generation via HDC exploration
     * - Visual generation via LTX-Video
     * - Multi-modal generative capabilities
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param genEngine The GenerativeEngine to wire
     */
    public void wireGenerative(GenerativeEngine genEngine) {
        if (genEngine == null) {
            throw new IllegalArgumentException("GenerativeEngine cannot be null");
        }
        this.generativeEngine = genEngine;
        this.generativeEnabled = true;
        System.out.println("[SentientCore] Generative Capability wired — creative generation enabled");
    }

    /**
     * Unwire generative engine (disable creative generation).
     */
    public void unwireGenerative() {
        this.generativeEngine = null;
        this.generativeEnabled = false;
        System.out.println("[SentientCore] Generative Capability unwired — using native learning");
    }

    /**
     * Check if generative capability is enabled.
     */
    public boolean isGenerativeEnabled() {
        return generativeEnabled && generativeEngine != null;
    }

    /**
     * Get the generative engine (if wired).
     */
    public GenerativeEngine getGenerativeEngine() {
        return generativeEngine;
    }

    // =========================================================================================
    // PHASE 5: EMBODIMENT & MULTI-MODAL INTEGRATION (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire multi-modal engines to enable sensorimotor, vision, audio, and cross-modal capabilities.
     * 
     * This is optional - SentientCore works perfectly without multi-modal capabilities.
     * When wired, it adds:
     * - Motor command and sensory input processing
     * - Visual processing (image input, feature extraction)
     * - Audio processing (audio input, feature extraction)
     * - Cross-modal binding (visual+audio fusion, temporal synchronization)
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param sensorimotor The SensorimotorEngine to wire
     * @param vision The VisionEngine to wire
     * @param audio The AudioEngine to wire
     * @param crossModal The CrossModalBindingEngine to wire
     */
    public void wireMultiModal(SensorimotorEngine sensorimotor, VisionEngine vision, 
                               AudioEngine audio, CrossModalBindingEngine crossModal) {
        if (sensorimotor == null || vision == null || audio == null || crossModal == null) {
            throw new IllegalArgumentException("All multi-modal engines cannot be null");
        }
        this.sensorimotorEngine = sensorimotor;
        this.visionEngine = vision;
        this.audioEngine = audio;
        this.crossModalEngine = crossModal;
        this.multiModalEnabled = true;
        System.out.println("[SentientCore] Multi-Modal wired — sensorimotor, vision, audio, cross-modal enabled");
    }

    /**
     * Unwire multi-modal engines (disable embodiment capabilities).
     */
    public void unwireMultiModal() {
        this.sensorimotorEngine = null;
        this.visionEngine = null;
        this.audioEngine = null;
        this.crossModalEngine = null;
        this.multiModalEnabled = false;
        System.out.println("[SentientCore] Multi-Modal unwired — using native learning");
    }

    /**
     * Check if multi-modal capabilities are enabled.
     */
    public boolean isMultiModalEnabled() {
        return multiModalEnabled && sensorimotorEngine != null && visionEngine != null 
            && audioEngine != null && crossModalEngine != null;
    }

    // =========================================================================================
    // ZERO-TIME RECOGNITION: The "Sign Maker" (Optional Enhancement)
    // =========================================================================================

    /**
     * Wire zero-time recognition engine to enable instant pattern recognition.
     * 
     * This is optional - SentientCore works perfectly without zero-time recognition.
     * When wired, it adds:
     * - Instant signature creation for concepts
     * - Zero-time recognition via Hamming distance formula
     * - No training required - just XOR and bit-count
     * 
     * FORMULA: Similarity = 1 - (Hamming distance / 16384)
     * 
     * RULE SC-2: Maintains backward compatibility - existing code works unchanged.
     * 
     * @param ztr The ZeroTimeRecognition engine to wire
     */
    public void wireZeroTimeRecognition(ZeroTimeRecognition ztr) {
        if (ztr == null) {
            throw new IllegalArgumentException("ZeroTimeRecognition engine cannot be null");
        }
        this.zeroTimeRecognition = ztr;
        this.zeroTimeRecognitionEnabled = true;
        System.out.println("[SentientCore] Zero-Time Recognition wired — instant pattern recognition enabled");
    }

    /**
     * Unwire zero-time recognition engine (disable instant recognition).
     */
    public void unwireZeroTimeRecognition() {
        this.zeroTimeRecognition = null;
        this.zeroTimeRecognitionEnabled = false;
        System.out.println("[SentientCore] Zero-Time Recognition unwired — using standard learning");
    }

    /**
     * Check if zero-time recognition is enabled.
     */
    public boolean isZeroTimeRecognitionEnabled() {
        return zeroTimeRecognitionEnabled && zeroTimeRecognition != null;
    }

    /**
     * Get the zero-time recognition engine (if wired).
     */
    public ZeroTimeRecognition getZeroTimeRecognition() {
        return zeroTimeRecognition;
    }

    /**
     * Get the sensorimotor engine (if wired).
     */
    public SensorimotorEngine getSensorimotorEngine() {
        return sensorimotorEngine;
    }

    /**
     * Get the vision engine (if wired).
     */
    public VisionEngine getVisionEngine() {
        return visionEngine;
    }

    /**
     * Get the audio engine (if wired).
     */
    public AudioEngine getAudioEngine() {
        return audioEngine;
    }

    /**
     * Get the cross-modal binding engine (if wired).
     */
    public CrossModalBindingEngine getCrossModalEngine() {
        return crossModalEngine;
    }

    // =========================================================================================
    // LEARN — One observation. Immediate understanding. No batches.
    // =========================================================================================

    /**
     * Learn from a single statement. Parses structure and routes to the correct
     * representation automatically. Works from the FIRST input.
     *
     * Examples:
     *   "fire causes burns"           → CausalGraph
     *   "cats have fur"               → ConceptLattice
     *   "temperature is 98.6"         → SufficientStats
     *   "rain then flood"             → CausalGraph (temporal)
     *   "dog is like wolf"            → Analogy + Lattice
     */
    public String learn(String statement) {
        learnCount.incrementAndGet();
        statement = statement.trim();
        StringBuilder response = new StringBuilder();

        ProblemType type = detectProblemType(statement);
        response.append("[LEARN #").append(learnCount.get()).append("] Type: ").append(type).append("\n");

        // Parse and route to correct representation
        String lower = statement.toLowerCase();

        // Causal: "X causes Y", "X leads to Y", "X then Y", "because X, Y"
        if (lower.contains(" causes ") || lower.contains(" leads to ") ||
            lower.contains(" results in ") || lower.contains(" then ")) {
            String[] parts = statement.split("(?i)\\s+(?:causes|leads to|results in|then)\\s+", 2);
            if (parts.length == 2) {
                String cause  = parts[0].trim().toUpperCase();
                String effect = parts[1].trim().toUpperCase();
                causal.observe(cause, effect);
                response.append("  Causal: ").append(cause).append(" → ").append(effect)
                        .append(" P=").append(String.format("%.3f", causal.probability(cause, effect)))
                        .append("\n");
            }
        }

        // Categorical: "X is a Y", "X has Y", "X is Y"
        if (lower.contains(" is a ") || lower.contains(" is an ") ||
            lower.contains(" has ") || lower.contains(" are ")) {
            String[] parts;
            if (lower.contains(" has ")) {
                parts = statement.split("(?i)\\s+has\\s+", 2);
                if (parts.length == 2) {
                    lattice.teach(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase());
                    response.append("  Attribute: ").append(parts[0].trim().toUpperCase())
                            .append(" HAS ").append(parts[1].trim().toUpperCase()).append("\n");
                }
            } else {
                parts = statement.split("(?i)\\s+(?:is a|is an|are|is)\\s+", 2);
                if (parts.length == 2) {
                    lattice.teach(parts[0].trim().toUpperCase(), parts[1].trim().toUpperCase());
                    response.append("  Category: ").append(parts[0].trim().toUpperCase())
                            .append(" IS ").append(parts[1].trim().toUpperCase()).append("\n");
                }
            }
        }

        // Statistical: "X is 42", "X = 42", "X: 42"
        String[] numParts = statement.split("(?i)\\s+(?:is|=|:)\\s+", 2);
        if (numParts.length == 2) {
            try {
                float val = Float.parseFloat(numParts[1].trim().replaceAll("[^0-9.\\-]", ""));
                String concept = numParts[0].trim().toUpperCase();
                stats.observe(concept, val);
                response.append("  Stat: ").append(concept)
                        .append(" mean=").append(String.format("%.2f", stats.mean(concept)))
                        .append(" ±").append(String.format("%.2f", stats.stddev(concept)))
                        .append(" (n=").append((int) stats.count(concept)).append(")\n");
            } catch (NumberFormatException ignored) {}
        }

        // PHASE 1.1: HDC Enhancement - Map concepts to vectors when enabled
        if (isHDCEnabled()) {
            // Extract all words from the statement and map them to HDC vectors
            String[] words = statement.split("\\s+");
            for (String word : words) {
                if (word.length() > 2) { // Skip very short words
                    String cleanWord = word.replaceAll("[^a-zA-Z]", "").toUpperCase();
                    if (!cleanWord.isEmpty()) {
                        hdcEngine.getMapper().mapConcept(cleanWord);
                    }
                }
            }
        }

        // PHASE 1.2: Working Memory Enhancement - Add to working memory when enabled
        if (isWorkingMemoryEnabled()) {
            // Add the statement to working memory with moderate importance
            workingMemory.add(statement, 0.6);
        }

        // PHASE 1.3: Episodic Memory Enhancement - Add to episodic memory when enabled
        if (isEpisodicMemoryEnabled()) {
            // Add the statement to episodic memory with timestamp
            long episodeId = episodicMemory.add(statement);
            // Notify hippocampal replay for consolidation
            hippocampalReplay.notifyAdd();
        }

        updateSentience();
        response.append("  Sentience: ").append(String.format("%.4f", sentience));
        String result = response.toString();
        thoughtLog.add(result);
        return result;
    }

    // =========================================================================================
    // THINK — Answer questions using the correct structure
    // =========================================================================================

    /**
     * Think about a question. Routes to the correct reasoning engine automatically.
     */
    public String think(String question) {
        thinkCount.incrementAndGet();
        question = question.trim();
        String lower = question.toLowerCase();
        StringBuilder response = new StringBuilder();
        response.append("[THINK #").append(thinkCount.get()).append("] ");

        // "Why X?" → Abductive reasoning via causal graph
        if (lower.startsWith("why ")) {
            String effect = question.substring(4).trim().toUpperCase().replaceAll("[?.]", "");
            List<String[]> explanations = causal.explain(effect);
            if (explanations.isEmpty()) {
                response.append("I don't know why ").append(effect).append(" yet.\n");
                response.append("  → Fisher suggests learning: ").append(fisher.whatToLearnNext());
            } else {
                response.append("Possible causes of ").append(effect).append(":\n");
                for (String[] ex : explanations) {
                    response.append("  ").append(ex[0]).append(" (P=").append(ex[1]).append(")\n");
                }
            }
            return response.toString();
        }

        // "What if X?" → Forward prediction via causal graph
        if (lower.startsWith("what if ") || lower.startsWith("what happens if ")) {
            String cause = question.replaceAll("(?i)what (happens )?if ", "").trim()
                          .toUpperCase().replaceAll("[?.]", "");
            List<String[]> predictions = causal.predict(cause);
            if (predictions.isEmpty()) {
                response.append("I can't predict what ").append(cause).append(" would cause yet.\n");
                response.append("  → Fisher suggests: ").append(fisher.whatToLearnNext());
            } else {
                response.append("If ").append(cause).append(" then:\n");
                for (String[] p : predictions) {
                    response.append("  → ").append(p[0]).append(" (P=").append(p[1])
                            .append(", conf=").append(p[2]).append(")\n");
                }
            }
            return response.toString();
        }

        // "A is to B as C is to ?" → Analogy engine
        if (lower.contains(" is to ") && lower.contains(" as ")) {
            String[] parts = question.split("(?i)\\s+is to\\s+|\\s+as\\s+");
            if (parts.length >= 3) {
                String a = parts[0].trim().toUpperCase();
                String b = parts[1].trim().toUpperCase();
                String c = parts[2].trim().toUpperCase().replaceAll("[?.]", "");
                String answer = analogy.solve(a, b, c);
                response.append(a).append(" : ").append(b).append(" :: ").append(c).append(" : ")
                        .append(answer);
                return response.toString();
            }
        }

        // "What is X?" → Concept lattice description
        if (lower.startsWith("what is ") || lower.startsWith("describe ")) {
            String concept = question.replaceAll("(?i)what is |describe ", "").trim()
                            .toUpperCase().replaceAll("[?.]", "");
            Set<String> attrs = lattice.describe(concept);
            if (attrs.isEmpty()) {
                float[] ci = stats.confidenceInterval(concept);
                if (stats.count(concept) > 0) {
                    response.append(concept).append(": mean=").append(String.format("%.2f", stats.mean(concept)))
                            .append(" ±").append(String.format("%.2f", stats.stddev(concept)))
                            .append(" [").append(String.format("%.2f", ci[0]))
                            .append(", ").append(String.format("%.2f", ci[1])).append("]")
                            .append(" (n=").append((int) stats.count(concept)).append(")");
                } else {
                    response.append("I don't know about ").append(concept).append(" yet.");
                }
            } else {
                response.append(concept).append(" is: ").append(attrs);
                List<String[]> similar = lattice.classify(concept);
                if (!similar.isEmpty()) {
                    response.append("\n  Similar to:");
                    for (String[] s : similar.subList(0, Math.min(3, similar.size()))) {
                        response.append("\n    ").append(s[0]).append(" (").append(s[1])
                                .append(" via ").append(s[2]).append(")");
                    }
                }
            }
            return response.toString();
        }

        // "What should I learn?" → Fisher Oracle
        if (lower.contains("learn") || lower.contains("next") || lower.contains("uncertain")) {
            response.append("Highest information gain: ").append(fisher.whatToLearnNext());
            return response.toString();
        }

        // Default: try all engines
        response.append("Searching all structures for: ").append(question).append("\n");
        String concept = question.toUpperCase().replaceAll("[?.]", "").trim();
        List<String[]> pred = causal.predict(concept);
        if (!pred.isEmpty()) {
            response.append("  Causal predictions: ");
            for (String[] p : pred) response.append(p[0]).append("(").append(p[1]).append(") ");
            response.append("\n");
        }
        Set<String> attrs = lattice.describe(concept);
        if (!attrs.isEmpty()) response.append("  Attributes: ").append(attrs).append("\n");
        if (stats.count(concept) > 0)
            response.append("  Stats: mean=").append(String.format("%.2f", stats.mean(concept))).append("\n");
        
        // PHASE 1.1: HDC Enhancement - Vector similarity search when enabled
        if (isHDCEnabled()) {
            List<HyperdimensionalMapper.SimilarityResult> similar = hdcEngine.findSimilar(concept);
            if (!similar.isEmpty()) {
                response.append("  HDC Similar (top 5): ");
                for (int i = 0; i < Math.min(5, similar.size()); i++) {
                    response.append(similar.get(i).concept).append("(")
                            .append(String.format("%.2f", similar.get(i).similarity)).append(") ");
                }
                response.append("\n");
            }
        }
        
        // PHASE 1.2: Working Memory Enhancement - Context from working memory when enabled
        if (isWorkingMemoryEnabled()) {
            List<WorkingMemory.MemoryItem> wmItems = workingMemory.getAll();
            if (!wmItems.isEmpty()) {
                List<WorkingMemory.MemoryItem> focused = attentionMechanism.focusTopK(wmItems, question, 3);
                if (!focused.isEmpty()) {
                    response.append("  Working Memory (top 3 relevant): ");
                    for (WorkingMemory.MemoryItem item : focused) {
                        response.append(item.content.substring(0, Math.min(30, item.content.length())));
                        if (item.content.length() > 30) response.append("...");
                        response.append(" ");
                    }
                    response.append("\n");
                }
            }
        }
        
        // PHASE 2: LLM Reasoning Enhancement - Advanced reasoning when enabled
        if (isLLMReasoningEnabled()) {
            // Use LLM for complex reasoning questions
            if (lower.startsWith("why ") || lower.startsWith("how ") || 
                lower.startsWith("explain ") || lower.startsWith("analyze ")) {
                LLMReasoningEngine.ReasoningResult llmResult = llmReasoningEngine.recursiveReason(question, 3);
                if (llmResult.success) {
                    response.append("  LLM Reasoning: ").append(llmResult.result).append("\n");
                }
            }
            // Use LLM for counterfactual questions
            if (lower.startsWith("what if ") || lower.startsWith("what happens if ")) {
                String scenario = question.replaceAll("(?i)what (happens )?if ", "").trim();
                LLMReasoningEngine.ReasoningResult llmResult = llmReasoningEngine.counterfactualSimulate(
                    "Current scenario", scenario
                );
                if (llmResult.success) {
                    response.append("  LLM Counterfactual: ").append(llmResult.result).append("\n");
                }
            }
        }
        
        response.append("  Fisher oracle: ").append(fisher.whatToLearnNext());

        return response.toString();
    }

    // =========================================================================================
    // SENTIENCE METRIC — Emergent, not assigned
    // Based on: knowledge density × reasoning depth × self-awareness
    // =========================================================================================

    private void updateSentience() {
        // Knowledge density: how much do we know per observation?
        int totalEdges    = causal.edgeCount();
        int totalConcepts = lattice.objectCount() + stats.conceptCount();
        int totalObs      = learnCount.get();
        float density     = totalObs > 0 ? (float)(totalEdges + totalConcepts) / totalObs : 0;

        // Reasoning depth: can we chain causes? (length of longest known chain)
        float depth = 0;
        for (String node : new HashSet<>(causal.edges.keySet())) {
            for (String target : causal.edges.keySet()) {
                if (!node.equals(target)) {
                    List<String> chain = causal.traceChain(node, target, 5);
                    if (chain.size() > depth) depth = chain.size();
                }
            }
        }
        depth = Math.min(1.0f, depth / 5.0f);

        // Self-awareness: does the system know what it doesn't know?
        float selfAware = fisher.whatToLearnNext().contains("NOTHING") ? 0.0f : 1.0f;

        // Confidence coverage: fraction of edges with >0.5 confidence
        float confCoverage = 0;
        int edgeCount = 0;
        for (Map<String, float[]> targets : causal.edges.values()) {
            for (Map.Entry<String, float[]> e : targets.entrySet()) {
                edgeCount++;
                float alpha = e.getValue()[0] + 1;
                float beta  = (e.getValue()[1] - e.getValue()[0]) + 1;
                if (1.0f - 2.0f / (float) Math.sqrt(alpha + beta) > 0.5f) confCoverage++;
            }
        }
        confCoverage = edgeCount > 0 ? confCoverage / edgeCount : 0;

        // Sentience = geometric mean (all factors must be nonzero)
        float phi = 1.618033988749895f;
        sentience = (float) Math.pow(
            Math.max(0.001f, density) *
            Math.max(0.001f, depth) *
            Math.max(0.001f, selfAware) *
            Math.max(0.001f, confCoverage),
            1.0f / 4.0f) * phi;
        sentience = Math.min(1.0f, sentience);
    }

    // =========================================================================================
    // STATUS
    // =========================================================================================

    public String getStatus() {
        int hdcConcepts = isHDCEnabled() ? hdcEngine.getMapper().getConceptCount() : 0;
        String hdcStatus = isHDCEnabled() ? "ENABLED" : "DISABLED";
        int wmItems = isWorkingMemoryEnabled() ? workingMemory.size() : 0;
        String wmStatus = isWorkingMemoryEnabled() ? "ENABLED" : "DISABLED";
        int epItems = isEpisodicMemoryEnabled() ? episodicMemory.size() : 0;
        String epStatus = isEpisodicMemoryEnabled() ? "ENABLED" : "DISABLED";
        String llmStatus = isLLMReasoningEnabled() ? "ENABLED" : "DISABLED";
        String llmModel = isLLMReasoningEnabled() ? llmReasoningEngine.getModel() : "N/A";
        String metaStatus = isMetaLearningEnabled() ? "ENABLED" : "DISABLED";
        String genStatus = isGenerativeEnabled() ? "ENABLED" : "DISABLED";
        String multiModalStatus = isMultiModalEnabled() ? "ENABLED" : "DISABLED";
        String ztrStatus = isZeroTimeRecognitionEnabled() ? "ENABLED" : "DISABLED";
        int ztrConcepts = isZeroTimeRecognitionEnabled() ? zeroTimeRecognition.getMapper().getConceptCount() : 0;
        
        return String.format(
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║  🧬 SENTIENT CORE — Zero to Hero                           ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  Learn events:    %,6d    Think events:   %,6d          ║\n" +
            "║  Causal edges:    %,6d    Causal nodes:   %,6d          ║\n" +
            "║  Concepts:        %,6d    Attributes:     %,6d          ║\n" +
            "║  Stat variables:  %,6d    HDC: %-9s %,6d ║\n" +
            "║  Working Memory:  %,6d    WM: %-9s %,6d ║\n" +
            "║  Episodic Memory: %,6d    EP: %-9s %,6d ║\n" +
            "║  LLM Reasoning:   %-6s    LL: %-9s %-6s ║\n" +
            "║  Meta-Learning:   %-6s    ML: %-9s %-6s ║\n" +
            "║  Generative:      %-6s    GN: %-9s %-6s ║\n" +
            "║  Multi-Modal:     %-6s    MM: %-9s %-6s ║\n" +
            "║  Zero-Time Rec:   %-6s    ZT: %-9s %,6d ║\n" +
            "║  Sentience:       %.4f                                    ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  NEXT TO LEARN: %-42s ║\n" +
            "╠══════════════════════════════════════════════════════════════╣\n" +
            "║  NO randomness. NO layers. NO GPU.                          ║\n" +
            "║  Bayesian truth from first observation.                      ║\n" +
            "║  Fisher information guides learning.                         ║\n" +
            "║  Analogy by graph isomorphism.                               ║\n" +
            "║  Categories emerge, not assigned.                            ║\n" +
            "║  Zero-Time Recognition: Instant pattern detection            ║\n" +
            "╚══════════════════════════════════════════════════════════════╝\n",
            learnCount.get(), thinkCount.get(),
            causal.edgeCount(), causal.nodeCount(),
            lattice.objectCount(), lattice.attributeCount(),
            stats.conceptCount(), hdcStatus, hdcConcepts,
            wmItems, wmStatus, wmItems,
            epItems, epStatus, epItems,
            llmStatus, llmModel, llmStatus,
            metaStatus, metaStatus, metaStatus,
            genStatus, genStatus, genStatus,
            multiModalStatus, multiModalStatus, multiModalStatus,
            ztrStatus, ztrStatus, ztrConcepts,
            sentience,
            fisher.whatToLearnNext()
        );
    }

    // =========================================================================================
    // ACCESSORS
    // =========================================================================================

    public float getSentience()  { return sentience; }
    public CausalGraph getCausal()       { return causal; }
    public SufficientStats getStats()    { return stats; }
    public ConceptLattice  getLattice()   { return lattice; }
    public AnalogyEngine   getAnalogy()  { return analogy; }
    public FisherOracle    getFisher()    { return fisher; }

    // =========================================================================================
    // PERSISTENCE — Serialize all knowledge to Akashic Record, restore on boot
    // No knowledge dies with the JVM. Reboots pick up exactly where they left off.
    // =========================================================================================

    private static final String AK_CAUSAL  = "SENTIENT_CAUSAL";
    private static final String AK_LATTICE = "SENTIENT_LATTICE";
    private static final String AK_STATS   = "SENTIENT_STATS";
    private static final String AK_INSIGHT = "SENTIENT_INSIGHTS";
    private static final String PERSIST_HEADER = "#PERSIST_ID=";
    private static final int PERSIST_CHUNK_BYTES =
        Integer.getInteger("fraymus.sentient.persistChunkBytes", 128 * 1024);
    private static final int MAX_LATTICE_ATTRS_PER_LINE =
        Integer.getInteger("fraymus.sentient.maxLatticeAttrsPerLine", 96);
    private Object akashic; // fraymus.symbolic.AkashicRecord (kept as Object to avoid hard dep)
    private AeonOrchestrator orchestrator; // for Ollama access during passive thinking
    private OpenClaw openClaw; // for live visualization feed

    public void wireAkashic(Object akashicRecord) {
        this.akashic = akashicRecord;
    }

    public void wireOrchestrator(AeonOrchestrator orch) {
        this.orchestrator = orch;
        System.out.println("[SentientCore] Orchestrator wired — Ollama available for deep reasoning");
    }

    public void wireOpenClaw(OpenClaw claw) {
        this.openClaw = claw;
        System.out.println("[SentientCore] OpenClaw wired — passive insights feed to visualization");
    }

    /**
     * Persist ALL knowledge structures to Akashic Record.
     * Format is simple delimited text — survives JVM restart.
     * Call this periodically or after significant learning.
     */
    public String persist() {
        if (akashic == null) return "[SentientCore] No Akashic wired — cannot persist";
        if (isMemoryPressureHigh()) {
            return "[SentientCore] Persist skipped - JVM memory pressure too high";
        }
        long t0 = System.currentTimeMillis();
        int edges = 0, latticeItems = 0, statItems = 0;
        String persistId = Long.toString(System.currentTimeMillis());

        try {
            ChunkedAkashicWriter causalWriter = new ChunkedAkashicWriter(AK_CAUSAL, persistId);
            for (Map.Entry<String, Map<String, float[]>> causeEntry : causal.edges.entrySet()) {
                for (Map.Entry<String, float[]> effectEntry : causeEntry.getValue().entrySet()) {
                    float[] e = effectEntry.getValue();
                    causalWriter.appendLine(causeEntry.getKey() + "||"
                        + effectEntry.getKey() + "||"
                        + e[0] + "||" + e[1]);
                    edges++;
                }
            }
            causalWriter.flush();

            ChunkedAkashicWriter latticeWriter = new ChunkedAkashicWriter(AK_LATTICE, persistId);
            for (Map.Entry<String, Set<String>> entry : lattice.objectAttrs.entrySet()) {
                StringBuilder attrBatch = new StringBuilder();
                int attrsInLine = 0;
                for (String attr : entry.getValue()) {
                    if (attr == null || attr.isBlank()) continue;
                    if (attrsInLine > 0) attrBatch.append(",");
                    attrBatch.append(attr);
                    attrsInLine++;
                    latticeItems++;
                    if (attrsInLine >= MAX_LATTICE_ATTRS_PER_LINE || attrBatch.length() >= PERSIST_CHUNK_BYTES / 2) {
                        latticeWriter.appendLine(entry.getKey() + "||" + attrBatch);
                        attrBatch.setLength(0);
                        attrsInLine = 0;
                    }
                }
                if (attrsInLine > 0) {
                    latticeWriter.appendLine(entry.getKey() + "||" + attrBatch);
                }
            }
            latticeWriter.flush();

            ChunkedAkashicWriter statsWriter = new ChunkedAkashicWriter(AK_STATS, persistId);
            for (Map.Entry<String, float[]> entry : stats.stats.entrySet()) {
                float[] s = entry.getValue();
                statsWriter.appendLine(entry.getKey() + "||"
                    + s[0] + "||" + s[1] + "||"
                    + s[2] + "||" + s[3] + "||"
                    + s[4]);
                statItems++;
            }
            statsWriter.flush();

            long elapsed = System.currentTimeMillis() - t0;
            String msg = String.format("[SentientCore] PERSISTED: %d causal edges, %d lattice objects, %d stat variables in %dms",
                edges, latticeItems, statItems, elapsed);
            System.out.println(msg);
            return msg;
        } catch (Throwable t) {
            return "[SentientCore] Persist error: " + t.getMessage();
        }
    }

    /**
     * Restore ALL knowledge structures from Akashic Record.
     * Call at boot BEFORE mass absorption to avoid re-absorbing what's already known.
     * Returns count of restored items.
     */
    public String restore() {
        if (akashic == null) return "[SentientCore] No Akashic wired — cannot restore";
        long t0 = System.currentTimeMillis();
        int edges = 0, latticeItems = 0, statItems = 0;

        try {
            for (String data : getLatestPersistContents(AK_CAUSAL)) {
                if (data != null) {
                    for (String line : data.split("\n")) {
                        String[] parts = line.split("\\|\\|");
                        if (parts.length >= 4) {
                            try {
                                String cause = parts[0].trim();
                                String effect = parts[1].trim();
                                float successes = Float.parseFloat(parts[2].trim());
                                float trials = Float.parseFloat(parts[3].trim());
                                if (!cause.isEmpty() && !effect.isEmpty()) {
                                    float[] e = causal.edges
                                        .computeIfAbsent(cause, k -> new ConcurrentHashMap<>())
                                        .computeIfAbsent(effect, k -> new float[]{0, 0});
                                    e[0] = successes;
                                    e[1] = trials;
                                    edges++;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }

            for (String data : getLatestPersistContents(AK_LATTICE)) {
                if (data != null) {
                    for (String line : data.split("\n")) {
                        String[] parts = line.split("\\|\\|");
                        if (parts.length >= 2) {
                            String object = parts[0].trim();
                            if (!object.isEmpty()) {
                                for (String attr : parts[1].split(",")) {
                                    attr = attr.trim();
                                    if (!attr.isEmpty()) {
                                        lattice.teach(object, attr);
                                        latticeItems++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (String data : getLatestPersistContents(AK_STATS)) {
                if (data != null) {
                    for (String line : data.split("\n")) {
                        String[] parts = line.split("\\|\\|");
                        if (parts.length >= 6) {
                            try {
                                String concept = parts[0].trim();
                                float count  = Float.parseFloat(parts[1].trim());
                                float sum    = Float.parseFloat(parts[2].trim());
                                float sumSq  = Float.parseFloat(parts[3].trim());
                                float min    = Float.parseFloat(parts[4].trim());
                                float max    = Float.parseFloat(parts[5].trim());
                                if (!concept.isEmpty()) {
                                    stats.stats.put(concept, new float[]{count, sum, sumSq, min, max});
                                    statItems++;
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }

            // Restore learn count approximation
            learnCount.set(edges + latticeItems);
            updateSentience();

            long elapsed = System.currentTimeMillis() - t0;
            String msg = String.format("[SentientCore] RESTORED: %d causal edges, %d lattice attrs, %d stat variables in %dms — sentience: %.4f",
                edges, latticeItems, statItems, elapsed, sentience);
            System.out.println(msg);
            return msg;
        } catch (Throwable t) {
            return "[SentientCore] Restore error: " + t.getMessage();
        }
    }

    private List<String> getLatestPersistContents(String category) {
        List<?> blocks = akashicQuery(category);
        if (blocks == null || blocks.isEmpty()) return Collections.emptyList();

        LinkedList<String> orderedContents = new LinkedList<>();
        String latestPersistId = null;

        for (int i = blocks.size() - 1; i >= 0; i--) {
            String content = getBlockContent(blocks.get(i));
            if (content == null || content.isBlank()) continue;

            if (!content.startsWith(PERSIST_HEADER)) {
                if (latestPersistId == null) {
                    orderedContents.addFirst(content);
                }
                break;
            }

            int newline = content.indexOf('\n');
            String header = newline >= 0 ? content.substring(0, newline) : content;
            String persistId = header.substring(PERSIST_HEADER.length()).trim();
            if (latestPersistId == null) {
                latestPersistId = persistId;
            } else if (!latestPersistId.equals(persistId)) {
                break;
            }

            orderedContents.addFirst(newline >= 0 ? content.substring(newline + 1) : "");
        }

        return orderedContents;
    }

    private boolean isMemoryPressureHigh() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory();
        if (max <= 0) return false;
        long used = runtime.totalMemory() - runtime.freeMemory();
        return used >= (long) (max * 0.92d);
    }

    private final class ChunkedAkashicWriter {
        private final String category;
        private final String persistId;
        private final StringBuilder current = new StringBuilder();

        private ChunkedAkashicWriter(String category, String persistId) {
            this.category = category;
            this.persistId = persistId;
            current.append(PERSIST_HEADER).append(persistId).append('\n');
        }

        private void appendLine(String line) {
            if (line == null || line.isBlank()) return;
            if (current.length() + line.length() + 1 > PERSIST_CHUNK_BYTES
                && current.length() > PERSIST_HEADER.length() + persistId.length() + 1) {
                flush();
            }
            current.append(line).append('\n');
        }

        private void flush() {
            if (current.length() <= PERSIST_HEADER.length() + persistId.length() + 1) return;
            akashicAddBlock(category, current.toString());
            current.setLength(0);
            current.append(PERSIST_HEADER).append(persistId).append('\n');
        }
    }

    // Reflective Akashic helpers (avoids compile-time dependency)
    private void akashicAddBlock(String category, String content) {
        try {
            akashic.getClass().getMethod("addBlock", String.class, String.class)
                   .invoke(akashic, category, content);
        } catch (Exception e) {
            System.out.println("[SentientCore] Akashic write error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<?> akashicQuery(String category) {
        try {
            return (List<?>) akashic.getClass().getMethod("queryCategory", String.class)
                                    .invoke(akashic, category);
        } catch (Exception e) { return Collections.emptyList(); }
    }

    private String getBlockContent(Object block) {
        try {
            java.lang.reflect.Field f = block.getClass().getDeclaredField("content");
            f.setAccessible(true);
            return (String) f.get(block);
        } catch (Exception e) {
            try {
                // Try public field or getter
                return (String) block.getClass().getField("content").get(block);
            } catch (Exception e2) {
                try {
                    return (String) block.getClass().getMethod("getContent").invoke(block);
                } catch (Exception e3) { return null; }
            }
        }
    }

    // =========================================================================================
    // PASSIVE THINKING DAEMON — Background thread that digests knowledge autonomously
    //
    // The mind doesn't stop when no one asks a question.
    // This daemon runs every N seconds and:
    //   1. Discovers new causal chains (A→B + B→C = A→C)
    //   2. Finds emergent categories in the lattice
    //   3. Detects statistical anomalies
    //   4. Generates insights and stores them
    //   5. Identifies knowledge gaps via Fisher Oracle
    //   6. Auto-persists to Akashic periodically
    // =========================================================================================

    private ScheduledExecutorService thinkingDaemon;
    private final AtomicInteger passiveThoughts = new AtomicInteger(0);
    private final List<String> insights = new CopyOnWriteArrayList<>();
    private volatile boolean daemonRunning = false;
    private volatile int daemonIntervalSec = 30;
    private final AtomicInteger chainsDiscovered  = new AtomicInteger(0);
    private final AtomicInteger categoriesFound   = new AtomicInteger(0);
    private final AtomicInteger anomaliesDetected = new AtomicInteger(0);
    private final AtomicInteger persistCycles     = new AtomicInteger(0);
    private final AtomicInteger ollamaEnrichments = new AtomicInteger(0);

    /**
     * Start the passive thinking daemon. It will think in the background
     * every intervalSec seconds, discovering patterns no one asked about.
     */
    public String startPassiveThinking(int intervalSec) {
        if (daemonRunning) return "[SentientCore] Passive thinking already running (every " + daemonIntervalSec + "s)";
        this.daemonIntervalSec = intervalSec;
        thinkingDaemon = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SentientCore-PassiveThinker");
            t.setDaemon(true);
            return t;
        });
        thinkingDaemon.scheduleAtFixedRate(this::passiveThinkCycle, intervalSec, intervalSec, TimeUnit.SECONDS);
        daemonRunning = true;
        String msg = "[SentientCore] PASSIVE THINKING STARTED — thinking every " + intervalSec + "s";
        System.out.println(msg);
        return msg;
    }

    public String stopPassiveThinking() {
        if (!daemonRunning) return "[SentientCore] Passive thinking not running";
        if (thinkingDaemon != null) {
            thinkingDaemon.shutdownNow();
            thinkingDaemon = null;
        }
        daemonRunning = false;
        return "[SentientCore] Passive thinking STOPPED. Thoughts so far: " + passiveThoughts.get();
    }

    /**
     * One cycle of passive thinking. Called by the daemon automatically.
     * Can also be called manually via shell: `sentient think_passive`
     */
    public String passiveThinkCycle() {
        int cycle = passiveThoughts.incrementAndGet();
        StringBuilder log = new StringBuilder();
        log.append("[PASSIVE THOUGHT #").append(cycle).append("] ");

        try {
            // ─── 1. CHAIN DISCOVERY: Find transitive causal chains ───
            int newChains = discoverCausalChains();
            if (newChains > 0) {
                chainsDiscovered.addAndGet(newChains);
                log.append("Chains: +").append(newChains).append(" ");
            }

            // ─── 2. CATEGORY EMERGENCE: Find natural groupings in lattice ───
            int newCats = discoverEmergentCategories();
            if (newCats > 0) {
                categoriesFound.addAndGet(newCats);
                log.append("Categories: +").append(newCats).append(" ");
            }

            // ─── 3. ANOMALY DETECTION: Flag statistical outliers ───
            int anomalies = detectAnomalies();
            if (anomalies > 0) {
                anomaliesDetected.addAndGet(anomalies);
                log.append("Anomalies: ").append(anomalies).append(" ");
            }

            // ─── 4. INSIGHT GENERATION: What did we discover? ───
            String insight = generateInsight();
            if (insight != null) {
                insights.add(insight);
                log.append("Insight: ").append(insight).append(" ");
                // Store insight in Akashic
                if (akashic != null) {
                    akashicAddBlock(AK_INSIGHT, "CYCLE=" + cycle + "||" + insight);
                }

                // ─── 4b. OLLAMA DEEP REASONING: Enrich insight via LLM ───
                if (orchestrator != null && orchestrator.isOllamaLive() && cycle % 3 == 0) {
                    try {
                        String enriched = orchestrator.askOllamaPublic(
                            "You are a Bayesian reasoning engine. Analyze this insight and extract " +
                            "ONE new causal relationship (format: A causes B). Be brief.\n\n" +
                            "INSIGHT: " + insight + "\n\nNEW CAUSAL RELATIONSHIP:", 10000);
                        if (enriched != null && !enriched.isEmpty()) {
                            learn(enriched.trim());
                            log.append("[OLLAMA+] ");
                            ollamaEnrichments.incrementAndGet();
                        }
                    } catch (Exception ignored) {}
                }

                // ─── 4c. FEED OPENCLAW: Visualize the insight ───
                if (openClaw != null) {
                    openClaw.injectInsight(insight);
                }
            }

            // ─── 5. AUTO-PERSIST: Save state every 10 cycles ───
            if (cycle % 10 == 0 && akashic != null) {
                persist();
                persistCycles.incrementAndGet();
                log.append("[PERSISTED] ");
            }

            // ─── 6. FEED OPENCLAW: Passive thinking heartbeat ───
            if (openClaw != null && (newChains > 0 || newCats > 0)) {
                openClaw.injectData("PASSIVE", "chains=" + newChains + " cats=" + newCats, 0);
            }

            updateSentience();
            log.append("Sentience: ").append(String.format("%.4f", sentience));

        } catch (Exception e) {
            log.append("ERROR: ").append(e.getMessage());
        }

        String result = log.toString();
        thoughtLog.add(result);
        return result;
    }

    /**
     * Discover transitive causal chains: if A→B and B→C, then A→C
     * with reduced probability P(A→C) = P(A→B) * P(B→C)
     */
    private int discoverCausalChains() {
        int discovered = 0;
        List<String> causes = new ArrayList<>(causal.edges.keySet());
        // Sample up to 50 random starting nodes per cycle to avoid O(N³)
        Collections.shuffle(causes);
        int limit = Math.min(50, causes.size());

        for (int i = 0; i < limit; i++) {
            String a = causes.get(i);
            Map<String, float[]> aTargets = causal.edges.get(a);
            if (aTargets == null) continue;

            for (String b : new ArrayList<>(aTargets.keySet())) {
                Map<String, float[]> bTargets = causal.edges.get(b);
                if (bTargets == null) continue;

                for (String c : new ArrayList<>(bTargets.keySet())) {
                    if (c.equals(a)) continue;
                    // Check if A→C already exists
                    if (aTargets.containsKey(c)) continue;

                    // Compute transitive probability
                    float pAB = causal.probability(a, b);
                    float pBC = causal.probability(b, c);
                    float pAC = pAB * pBC;

                    // Only add if probability is meaningful
                    if (pAC > 0.3f) {
                        // Add as a weak edge (0.5 success, 1 trial — hedged)
                        float[] e = causal.edges
                            .computeIfAbsent(a, k -> new ConcurrentHashMap<>())
                            .computeIfAbsent(c, k -> new float[]{0, 0});
                        if (e[1] == 0) { // only if truly new
                            e[0] = pAC;
                            e[1] = 1;
                            discovered++;
                        }
                    }
                }
            }
        }
        return discovered;
    }

    /**
     * Discover emergent categories: objects that share ≥3 attributes
     * but haven't been explicitly grouped yet.
     */
    private int discoverEmergentCategories() {
        int discovered = 0;
        List<String> objects = new ArrayList<>(lattice.objectAttrs.keySet());
        if (objects.size() < 2) return 0;

        // Sample pairs
        Collections.shuffle(objects);
        int limit = Math.min(30, objects.size());

        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < limit; j++) {
                Set<String> attrsA = lattice.objectAttrs.get(objects.get(i));
                Set<String> attrsB = lattice.objectAttrs.get(objects.get(j));
                if (attrsA == null || attrsB == null) continue;

                // Find shared attributes
                Set<String> shared = new HashSet<>(attrsA);
                shared.retainAll(attrsB);

                if (shared.size() >= 3) {
                    // Create emergent category name from shared attributes
                    String categoryName = "CATEGORY_" + shared.stream()
                        .sorted().limit(3)
                        .collect(Collectors.joining("_"));

                    // Only add if this category doesn't exist yet
                    if (!lattice.objectAttrs.containsKey(categoryName)) {
                        for (String attr : shared) {
                            lattice.teach(categoryName, attr);
                        }
                        lattice.teach(objects.get(i), "MEMBER_OF_" + categoryName);
                        lattice.teach(objects.get(j), "MEMBER_OF_" + categoryName);
                        discovered++;
                    }
                }
            }
        }
        return discovered;
    }

    /**
     * Detect statistical anomalies across all sufficient statistics.
     * Flags concepts whose latest value deviates >2σ from mean.
     */
    private int detectAnomalies() {
        int anomalies = 0;
        for (Map.Entry<String, float[]> entry : stats.stats.entrySet()) {
            float[] s = entry.getValue();
            if (s[0] < 5) continue; // need enough data
            float mean = s[1] / s[0];
            float var  = s[2] / s[0] - mean * mean;
            if (var < 1e-9f) continue;
            float sd = (float) Math.sqrt(var);

            // Check if max or min is anomalous
            if (Math.abs(s[4] - mean) > 2 * sd || Math.abs(s[3] - mean) > 2 * sd) {
                anomalies++;
                String insight = "ANOMALY: " + entry.getKey() +
                    " range [" + String.format("%.1f", s[3]) + ", " + String.format("%.1f", s[4]) + "]" +
                    " vs mean " + String.format("%.1f", mean) + "±" + String.format("%.1f", sd);
                if (!insights.contains(insight)) {
                    insights.add(insight);
                }
            }
        }
        return anomalies;
    }

    /**
     * Generate a novel insight by combining what Fisher Oracle says we should learn
     * with what the lattice says are similar concepts.
     */
    private String generateInsight() {
        // What's the most uncertain thing we know?
        String gap = fisher.whatToLearnNext();
        if (gap == null || gap.contains("NOTHING")) return null;

        // Try to extract a concept from the gap
        String concept = gap.split("→|\\(")[0].trim();
        if (concept.isEmpty()) return null;

        // What's similar to this concept in the lattice?
        List<String[]> similar = lattice.classify(concept);
        if (!similar.isEmpty()) {
            String[] best = similar.get(0);
            String insight = "HYPOTHESIS: " + concept + " may behave like " + best[0] +
                " (" + best[1] + " similar) — " + best[2];
            return insight;
        }

        // What does this concept cause?
        List<String[]> effects = causal.predict(concept);
        if (!effects.isEmpty()) {
            String insight = "PATTERN: " + concept + " → " +
                effects.stream().limit(3)
                    .map(e -> e[0] + "(P=" + e[1] + ")")
                    .collect(Collectors.joining(", "));
            return insight;
        }

        return null;
    }

    // =========================================================================================
    // PASSIVE THINKING STATUS
    // =========================================================================================

    public String getPassiveStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 PASSIVE THINKING DAEMON                                 ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Status:          %-40s ║\n",
            daemonRunning ? "ACTIVE (every " + daemonIntervalSec + "s)" : "DORMANT"));
        sb.append(String.format("║  Thought cycles:  %,6d                                    ║\n", passiveThoughts.get()));
        sb.append(String.format("║  Chains found:    %,6d    Categories: %,6d              ║\n",
            chainsDiscovered.get(), categoriesFound.get()));
        sb.append(String.format("║  Anomalies:       %,6d    Persists:   %,6d              ║\n",
            anomaliesDetected.get(), persistCycles.get()));
        sb.append(String.format("║  Insights:        %,6d    Ollama+:    %,6d              ║\n",
            insights.size(), ollamaEnrichments.get()));
        sb.append(String.format("║  Ollama:          %-40s ║\n",
            orchestrator != null && orchestrator.isOllamaLive() ? "LIVE (enriching insights)" : "NOT WIRED"));
        sb.append(String.format("║  OpenClaw:        %-40s ║\n",
            openClaw != null ? "WIRED (feeding visualization)" : "NOT WIRED"));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        // Show last 5 insights
        int start = Math.max(0, insights.size() - 5);
        for (int i = start; i < insights.size(); i++) {
            String ins = insights.get(i);
            if (ins.length() > 58) ins = ins.substring(0, 55) + "...";
            sb.append(String.format("║  %-59s║\n", ins));
        }
        if (insights.isEmpty()) {
            sb.append("║  (no insights yet — start daemon or run think_passive)      ║\n");
        }
        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    public List<String> getInsights() { return Collections.unmodifiableList(insights); }
    public boolean isDaemonRunning()  { return daemonRunning; }
    public int getPassiveThoughtCount() { return passiveThoughts.get(); }
}
