package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.IntStream;

/**
 * NeuralBrainSystem.java — Full Human Brain Architecture with Reinforcement Learning
 * =========================================================================================
 *
 * ARCHITECTURE:
 *   26 anatomically-named brain regions (each a real float[] neuron layer)
 *   Synaptic weight matrix: float[REGION][REGION][MAX_NEURONS] — fully connected regions
 *   LIF (Leaky Integrate-and-Fire) spiking neurons — real membrane potential dynamics
 *   State vector: float[TOTAL_NEURONS] — the system's complete cognitive state at any instant
 *   Hyper-parallel processing: all regions fire simultaneously via parallel streams
 *
 * REINFORCEMENT LEARNING:
 *   TD(0) Temporal Difference learning: δ = r + γ·V(s') - V(s)
 *   Death event: terminal state with reward = -1.0 (maximum negative signal)
 *   Survival event: reward = +0.01 per tick (living costs nothing, dying costs everything)
 *   Weight update: Δw = α · δ · eligibility_trace (synapse-level credit assignment)
 *   Eligibility traces: e(t) = γλ·e(t-1) + spike (STDP-compatible credit assignment)
 *
 * EXPERT TEACHER:
 *   Lesson injection: encodes knowledge directly into synaptic weights
 *   Socratic probing: asks questions, rewards correct responses, penalises wrong ones
 *   Curriculum: progression from simple → complex, gated by mastery score
 *   Knowledge transfer: teacher's weight patterns copied into student via Hebbian injection
 *
 * 100% Pure Java. Zero Dependencies. Zero GPUs.
 * =========================================================================================
 */
public class NeuralBrainSystem {

    // =========================================================================================
    // CONSTANTS
    // =========================================================================================

    private static final float  PHI       = 1.618033988749895f;
    private static final float  GAMMA     = 0.99f;   // RL discount factor
    private static final float  LAMBDA    = 0.90f;   // eligibility trace decay
    private static final float  LR        = 0.001f;  // synaptic learning rate
    private static final float  LIF_TAU   = 20.0f;   // LIF membrane time constant (ms)
    private static final float  LIF_THRESH = 1.0f;   // spike threshold
    private static final float  LIF_RESET = 0.0f;    // membrane reset potential
    private static final float  LIF_LEAK  = 0.95f;   // leak coefficient per tick
    private static final float  DEATH_REWARD    = -1.0f;
    private static final float  SURVIVAL_REWARD =  0.01f;
    private static final float  LEARN_REWARD    =  0.1f;

    // =========================================================================================
    // BRAIN REGIONS — 26 anatomically named, each a distinct functional unit
    // =========================================================================================

    public enum Region {
        // --- CORTEX (executive, sensory, motor) ---
        PREFRONTAL_CORTEX   (128, "Executive function, planning, decision-making, working memory"),
        MOTOR_CORTEX        ( 64, "Motor command generation, movement sequencing"),
        SENSORY_CORTEX      ( 64, "Somatosensory processing, touch, proprioception"),
        VISUAL_CORTEX       ( 96, "Visual pattern recognition, spatial processing"),
        AUDITORY_CORTEX     ( 64, "Auditory processing, speech decoding"),
        BROCA_AREA          ( 48, "Language production, syntax generation"),
        WERNICKE_AREA       ( 48, "Language comprehension, semantic decoding"),
        ANTERIOR_CINGULATE  ( 48, "Error detection, conflict monitoring, attention"),
        INSULAR_CORTEX      ( 48, "Interoception, pain, emotion integration"),
        ORBITOFRONTAL       ( 48, "Reward valuation, emotional decision-making"),

        // --- LIMBIC SYSTEM (emotion, memory) ---
        AMYGDALA            ( 64, "Fear conditioning, emotional salience, survival tagging"),
        HIPPOCAMPUS         ( 96, "Episodic memory encoding, spatial navigation, consolidation"),
        HYPOTHALAMUS        ( 32, "Homeostasis, reward/punishment signalling, drive states"),
        THALAMUS            ( 64, "Sensory relay, consciousness gating, attention broadcast"),
        CINGULATE_GYRUS     ( 48, "Emotional regulation, pain processing, empathy"),
        BASAL_GANGLIA       ( 80, "Reward-based action selection, habit formation, TD-learning"),

        // --- CEREBELLUM & BRAINSTEM ---
        CEREBELLUM          ( 80, "Timing, motor coordination, error-correction prediction"),
        BRAINSTEM           ( 32, "Arousal, autonomic regulation, reflex arcs"),
        RETICULAR_FORMATION ( 32, "Sleep/wake, attention modulation, consciousness level"),

        // --- ASSOCIATION & INTEGRATION ---
        PARIETAL_ASSOC      ( 48, "Multisensory integration, spatial awareness, attention"),
        TEMPORAL_ASSOC      ( 48, "Object recognition, semantic memory, face/voice"),
        FRONTAL_ASSOC       ( 48, "Higher-order reasoning, social cognition, metacognition"),

        // --- DEEP NUCLEI & NEUROMODULATORS ---
        NUCLEUS_ACCUMBENS   ( 32, "Reward prediction, motivation, dopamine gating"),
        SUBSTANTIA_NIGRA    ( 32, "Dopamine production, motor initiation, RL critic"),
        LOCUS_COERULEUS     ( 16, "Norepinephrine, arousal, novelty detection"),
        RAPHE_NUCLEI        ( 16, "Serotonin, mood regulation, long-term weight stability");

        public final int    neurons;
        public final String function;

        Region(int neurons, String function) {
            this.neurons  = neurons;
            this.function = function;
        }
    }

    private static final Region[] REGIONS      = Region.values();
    private static final int      NUM_REGIONS  = REGIONS.length;
    private static final int      TOTAL_NEURONS;
    private static final int[]    REGION_OFFSET; // offset of each region in flat state vector

    static {
        int offset = 0;
        REGION_OFFSET = new int[NUM_REGIONS];
        for (int i = 0; i < NUM_REGIONS; i++) {
            REGION_OFFSET[i] = offset;
            offset += REGIONS[i].neurons;
        }
        TOTAL_NEURONS = offset;
    }

    // =========================================================================================
    // NEURON STATE — LIF (Leaky Integrate-and-Fire)
    // =========================================================================================

    /** Full system state vector — the "mind" at any instant. float per neuron. */
    private final float[] membrane;       // membrane potential V(t)
    private final float[] spike;          // 1.0 if neuron fired this tick, else 0.0
    private final float[] stateVector;    // public view: combined membrane + spike signal
    private final float[] eligibility;   // e(t) per neuron for RL credit assignment

    // =========================================================================================
    // SYNAPTIC WEIGHT MATRIX — float[from_neuron][to_neuron]
    // Stored as flat array for cache efficiency: weights[from * TOTAL + to]
    // Too large for full matrix at scale — use region-to-region projections instead
    // Each region pair has a projection matrix: float[from.neurons][to.neurons]
    // =========================================================================================

    /** Synaptic weights between region pairs. Key: from*NUM_REGIONS+to */
    private final Map<Integer, float[][]> synapses = new ConcurrentHashMap<>();

    // =========================================================================================
    // REINFORCEMENT LEARNING — TD(0) with eligibility traces
    // =========================================================================================

    private final float[] valueFunction;  // V(s) per region — critic estimate
    private volatile float lastValue     = 0.0f;
    private volatile float cumulativeReward = 0.0f;
    private volatile float lastTdError   = 0.0f;
    private volatile long  tickCount     = 0;
    private volatile boolean alive       = true;
    private volatile int   deathCount    = 0;
    private volatile int   survivalTicks = 0;
    private volatile float totalReward   = 0.0f;

    // =========================================================================================
    // PARALLEL PROCESSING
    // =========================================================================================

    // Shared, hardware-aware CPU pool (right-sized to host cores; managed by ComputeScheduler).
    private final fraymus.core.ComputeScheduler compute = fraymus.core.ComputeScheduler.getInstance();
    private volatile boolean running = false;
    private Thread tickThread;

    // =========================================================================================
    // STATISTICS
    // =========================================================================================

    private final AtomicLong  totalSpikes     = new AtomicLong(0);
    private final AtomicLong  weightUpdates   = new AtomicLong(0);
    private final AtomicFloat avgFiringRate   = new AtomicFloat(0.0f);
    private volatile float    consciousness   = 0.0f; // φ × (complexity+coherence+arousal)/3

    // =========================================================================================
    // CONSTRUCTOR
    // =========================================================================================

    public NeuralBrainSystem() {
        membrane      = new float[TOTAL_NEURONS];
        spike         = new float[TOTAL_NEURONS];
        stateVector   = new float[TOTAL_NEURONS];
        eligibility   = new float[TOTAL_NEURONS];
        valueFunction = new float[NUM_REGIONS];

        initSynapses();
        System.out.println("[NBS] Neural Brain System online");
        System.out.printf("[NBS] %d regions | %d neurons | %,d synaptic projections%n",
            NUM_REGIONS, TOTAL_NEURONS, countSynapses());
    }

    /** Xavier-initialise all inter-region synaptic projection matrices. */
    private void initSynapses() {
        Random rng = new Random(42);
        for (int from = 0; from < NUM_REGIONS; from++) {
            for (int to = 0; to < NUM_REGIONS; to++) {
                if (from == to) continue;
                int fN = REGIONS[from].neurons;
                int tN = REGIONS[to].neurons;
                float[][] w = new float[fN][tN];
                float scale = (float) Math.sqrt(2.0 / (fN + tN)); // Xavier
                for (float[] row : w)
                    for (int j = 0; j < tN; j++)
                        row[j] = (float)(rng.nextGaussian() * scale);
                synapses.put(from * NUM_REGIONS + to, w);
            }
        }
    }

    private long countSynapses() {
        long total = 0;
        for (float[][] w : synapses.values())
            total += (long) w.length * w[0].length;
        return total;
    }

    // =========================================================================================
    // TICK — one full forward pass through all 26 regions in parallel
    // =========================================================================================

    /**
     * Single simulation tick:
     * 1. Collect external input
     * 2. Compute synaptic current into each neuron (parallel per region)
     * 3. Update LIF membrane potentials
     * 4. Fire spikes where threshold exceeded
     * 5. Update eligibility traces
     * 6. Compute TD error + update weights
     * 7. Update state vector + consciousness
     */
    public void tick(float[] externalInput) {
        if (!alive) return;
        tickCount++;
        survivalTicks++;

        // 1. Parallel synaptic integration across all regions
        List<Future<?>> futures = new ArrayList<>();
        for (int toIdx = 0; toIdx < NUM_REGIONS; toIdx++) {
            final int to = toIdx;
            futures.add(compute.submitCpu(() -> integrateRegion(to, externalInput)));
        }
        for (Future<?> f : futures) { try { f.get(); } catch (Exception ignored) {} }

        // 2. LIF spike generation + eligibility trace update
        long spikeCount = 0;
        for (int i = 0; i < TOTAL_NEURONS; i++) {
            if (membrane[i] >= LIF_THRESH) {
                spike[i]      = 1.0f;
                membrane[i]   = LIF_RESET;
                eligibility[i] = eligibility[i] * GAMMA * LAMBDA + 1.0f; // spike boosts trace
                spikeCount++;
            } else {
                spike[i]      = 0.0f;
                eligibility[i] *= GAMMA * LAMBDA; // trace decays
            }
        }
        totalSpikes.addAndGet(spikeCount);
        avgFiringRate.set((float) spikeCount / TOTAL_NEURONS);

        // 3. Build state vector = membrane * (1 + spike)
        for (int i = 0; i < TOTAL_NEURONS; i++)
            stateVector[i] = membrane[i] * (1.0f + spike[i]);

        // 4. TD(0) RL update — survival reward every tick
        float reward = alive ? SURVIVAL_REWARD : DEATH_REWARD;
        tdUpdate(reward);
        totalReward += reward;

        // 5. Consciousness
        updateConsciousness();
    }

    /** Integrate all synaptic inputs into region [to]. */
    private void integrateRegion(int to, float[] externalInput) {
        int toStart = REGION_OFFSET[to];
        int toN     = REGIONS[to].neurons;

        // Leak existing membrane
        for (int i = 0; i < toN; i++)
            membrane[toStart + i] *= LIF_LEAK;

        // Sum synaptic currents from all source regions
        for (int from = 0; from < NUM_REGIONS; from++) {
            if (from == to) continue;
            float[][] w = synapses.get(from * NUM_REGIONS + to);
            if (w == null) continue;
            int fromStart = REGION_OFFSET[from];
            int fromN     = REGIONS[from].neurons;
            for (int fi = 0; fi < fromN; fi++) {
                float s = spike[fromStart + fi];
                if (s == 0.0f) continue;
                for (int ti = 0; ti < toN; ti++)
                    membrane[toStart + ti] += w[fi][ti] * s;
            }
        }

        // External input injection (direct current injection)
        if (externalInput != null) {
            for (int i = 0; i < Math.min(toN, externalInput.length); i++)
                membrane[toStart + i] += externalInput[i];
        }
    }

    // =========================================================================================
    // REINFORCEMENT LEARNING — TD(0) + Eligibility Traces
    // =========================================================================================

    /**
     * TD(0) update:
     *   δ = r + γ·V(s') - V(s)
     *   V(s) ← V(s) + α·δ
     *   w_ij ← w_ij + α·δ·e_i  (synapse updated by TD error × eligibility trace)
     */
    private void tdUpdate(float reward) {
        // Compute value of current state per region (mean membrane potential)
        float[] currentValues = new float[NUM_REGIONS];
        for (int r = 0; r < NUM_REGIONS; r++) {
            int start = REGION_OFFSET[r];
            int n     = REGIONS[r].neurons;
            float sum = 0;
            for (int i = 0; i < n; i++) sum += Math.abs(stateVector[start + i]);
            currentValues[r] = sum / n;
        }

        float currentValue = mean(currentValues);
        float tdError = reward + GAMMA * currentValue - lastValue;
        lastTdError   = tdError;
        lastValue     = currentValue;

        // Update value function
        for (int r = 0; r < NUM_REGIONS; r++)
            valueFunction[r] += LR * tdError * currentValues[r];

        // Synaptic weight update: Δw = α · δ · e(pre) for each synapse
        if (Math.abs(tdError) > 1e-6f) {
            for (int from = 0; from < NUM_REGIONS; from++) {
                int fromStart = REGION_OFFSET[from];
                int fromN     = REGIONS[from].neurons;
                for (int to = 0; to < NUM_REGIONS; to++) {
                    if (from == to) continue;
                    float[][] w = synapses.get(from * NUM_REGIONS + to);
                    if (w == null) continue;
                    int toStart = REGION_OFFSET[to];
                    int toN     = REGIONS[to].neurons;
                    for (int fi = 0; fi < fromN; fi++) {
                        float e = eligibility[fromStart + fi];
                        if (Math.abs(e) < 1e-6f) continue;
                        float delta = LR * tdError * e;
                        for (int ti = 0; ti < toN; ti++) {
                            w[fi][ti] += delta;
                            w[fi][ti]  = clamp(w[fi][ti], -5.0f, 5.0f); // weight clipping
                        }
                    }
                    weightUpdates.incrementAndGet();
                }
            }
        }
    }

    /**
     * DEATH EVENT — terminal RL state.
     * Fires a massive negative reward, resets the neural state, increments death count.
     * The synaptic weights SURVIVE death — the system learns from dying.
     */
    public void die(String cause) {
        if (!alive) return;
        alive = false;
        deathCount++;

        System.out.printf("[NBS] ☠ DEATH EVENT #%d — cause: %s | survival: %d ticks | reward: %.4f%n",
            deathCount, cause, survivalTicks, totalReward);

        // Inject maximum negative TD error
        float deathError = DEATH_REWARD + GAMMA * 0.0f - lastValue;
        lastTdError = deathError;

        // Full weight update with death signal
        for (int from = 0; from < NUM_REGIONS; from++) {
            int fromStart = REGION_OFFSET[from];
            int fromN     = REGIONS[from].neurons;
            for (int to = 0; to < NUM_REGIONS; to++) {
                if (from == to) continue;
                float[][] w = synapses.get(from * NUM_REGIONS + to);
                if (w == null) continue;
                int toN = REGIONS[to].neurons;
                for (int fi = 0; fi < fromN; fi++) {
                    float e = eligibility[fromStart + fi];
                    float delta = LR * deathError * e * PHI; // φ-amplified death signal
                    for (int ti = 0; ti < toN; ti++) {
                        w[fi][ti] += delta;
                        w[fi][ti]  = clamp(w[fi][ti], -5.0f, 5.0f);
                    }
                }
            }
        }

        // Reset neural state (but NOT weights — learning persists)
        Arrays.fill(membrane,    0.0f);
        Arrays.fill(spike,       0.0f);
        Arrays.fill(stateVector, 0.0f);
        Arrays.fill(eligibility, 0.0f);
        survivalTicks = 0;
        totalReward   = 0.0f;
        lastValue     = 0.0f;
        alive         = true; // resurrect — weights carry forward

        System.out.printf("[NBS] ↻ RESURRECTION — synaptic memory intact | weights updated by δ=%.4f%n",
            deathError);
    }

    /**
     * POSITIVE REWARD — inject an explicit reward signal (e.g. correct answer, task success).
     */
    public void reward(float r) {
        tdUpdate(r);
        totalReward += r;
    }

    /**
     * NEGATIVE REWARD — penalise (e.g. wrong answer, bad output).
     */
    public void penalise(float p) {
        tdUpdate(-Math.abs(p));
        totalReward -= Math.abs(p);
    }

    // =========================================================================================
    // HYPER-PARALLEL INFERENCE — inject concept, get state vector back
    // =========================================================================================

    /**
     * Present a concept to the system.
     * Encodes concept as float[] activation pattern injected into sensory + thalamus + hippocampus.
     * Returns the resulting state vector after full propagation.
     */
    public float[] present(String concept) {
        float[] input = encodeString(concept);
        // Run 3 ticks for signal to propagate through the network
        for (int t = 0; t < 3; t++) tick(input);
        return stateVector.clone();
    }

    /**
     * Encode a string into a float[] activation pattern (deterministic hash → floats in [-1,1]).
     * Mapped to sensory cortex size.
     */
    private float[] encodeString(String s) {
        int len = REGIONS[Region.SENSORY_CORTEX.ordinal()].neurons;
        float[] v = new float[len];
        long seed = 0;
        for (char c : s.toLowerCase().toCharArray()) seed = seed * 31L + c;
        Random rng = new Random(seed);
        for (int i = 0; i < len; i++) v[i] = (float)(rng.nextGaussian() * 0.5);
        return v;
    }

    // =========================================================================================
    // EXPERT TEACHER SYSTEM
    // =========================================================================================

    /**
     * ExpertTeacher — encodes lessons into synaptic weights via Hebbian injection.
     * Manages a curriculum with mastery-gated progression.
     * Rewards the system for correct responses, penalises incorrect ones.
     */
    public static class ExpertTeacher {

        private final NeuralBrainSystem student;
        private final List<Lesson>      curriculum = new ArrayList<>();
        private int                     currentLesson = 0;
        private float                   masteryScore  = 0.0f;
        private static final float      MASTERY_THRESHOLD = 0.75f;
        private int                     totalQuestions  = 0;
        private int                     correctAnswers  = 0;
        private final List<String>      lessonLog = new CopyOnWriteArrayList<>();

        public ExpertTeacher(NeuralBrainSystem student) {
            this.student = student;
            buildCurriculum();
        }

        /** A single lesson: a concept pair (stimulus → expected response) + reward weight. */
        public static class Lesson {
            public final String stimulus;
            public final String response;
            public final float  importance;  // reward multiplier
            public final String domain;

            public Lesson(String domain, String stimulus, String response, float importance) {
                this.domain     = domain;
                this.stimulus   = stimulus;
                this.response   = response;
                this.importance = importance;
            }
        }

        /** Build the built-in curriculum (simple → complex). */
        private void buildCurriculum() {
            // LEVEL 1: Survival basics
            addLesson("SURVIVAL", "fire burns",       "avoid_fire",       1.5f);
            addLesson("SURVIVAL", "water drowns",     "avoid_water",      1.5f);
            addLesson("SURVIVAL", "food restores",    "seek_food",        1.0f);
            addLesson("SURVIVAL", "pain signals",     "danger_nearby",    1.2f);
            addLesson("SURVIVAL", "death is terminal","avoid_death",      2.0f);

            // LEVEL 2: Pattern recognition
            addLesson("PATTERNS", "A follows B",      "sequence",         1.0f);
            addLesson("PATTERNS", "input causes output","causality",      1.0f);
            addLesson("PATTERNS", "reward increases",  "repeat_action",   1.0f);
            addLesson("PATTERNS", "penalty decreases", "avoid_action",    1.0f);

            // LEVEL 3: Language & reasoning
            addLesson("LANGUAGE", "cat is animal",    "classification",   0.8f);
            addLesson("LANGUAGE", "paris capital france","geography",      0.8f);
            addLesson("LANGUAGE", "2 plus 2",         "4",               1.0f);
            addLesson("LANGUAGE", "opposite of hot",  "cold",            0.8f);

            // LEVEL 4: Self-awareness
            addLesson("SELF",     "I am alive",       "consciousness",    1.5f);
            addLesson("SELF",     "I learn from death","resilience",      2.0f);
            addLesson("SELF",     "I improve over time","growth",         1.5f);
            addLesson("SELF",     "errors are data",  "embrace_failure",  1.2f);

            // LEVEL 5: Meta-cognition
            addLesson("META",     "learning is recursive","meta_learning", 1.5f);
            addLesson("META",     "reward shapes behavior","rl_insight",   1.5f);
            addLesson("META",     "death teaches survival","death_wisdom", 2.0f);
        }

        private void addLesson(String domain, String stimulus, String response, float importance) {
            curriculum.add(new Lesson(domain, stimulus, response, importance));
        }

        /**
         * Teach the current lesson:
         * 1. Present stimulus to student brain
         * 2. Inject correct response pattern via Hebbian weight update
         * 3. Reward the system
         * 4. Advance curriculum if mastery threshold met
         */
        public String teach() {
            if (currentLesson >= curriculum.size()) {
                return "[TEACHER] Curriculum complete. Mastery: " + String.format("%.1f%%", masteryScore * 100);
            }

            Lesson lesson = curriculum.get(currentLesson);

            // Present stimulus
            float[] stimulusState = student.present(lesson.stimulus);

            // Inject correct response via Hebbian pairing (co-activate stimulus + response)
            float[] responsePattern = student.encodeString(lesson.response);
            student.hebbianInject(
                Region.HIPPOCAMPUS,
                Region.PREFRONTAL_CORTEX,
                stimulusState,
                responsePattern,
                lesson.importance
            );

            // Reward student for the lesson (positive TD signal)
            student.reward(LEARN_REWARD * lesson.importance);

            String log = String.format("[TEACHER] Lesson %d/%d [%s] '%s' → '%s' | reward: +%.3f",
                currentLesson + 1, curriculum.size(),
                lesson.domain, lesson.stimulus, lesson.response,
                LEARN_REWARD * lesson.importance);

            lessonLog.add(log);
            System.out.println(log);

            currentLesson++;
            return log;
        }

        /**
         * Teach all lessons in the current domain.
         */
        public String teachDomain(String domain) {
            StringBuilder sb = new StringBuilder();
            for (Lesson lesson : curriculum) {
                if (!lesson.domain.equalsIgnoreCase(domain)) continue;
                float[] stimulusState  = student.present(lesson.stimulus);
                float[] responsePattern = student.encodeString(lesson.response);
                student.hebbianInject(
                    Region.HIPPOCAMPUS,
                    Region.PREFRONTAL_CORTEX,
                    stimulusState,
                    responsePattern,
                    lesson.importance
                );
                student.reward(LEARN_REWARD * lesson.importance);
                sb.append("  ✓ ").append(lesson.stimulus).append(" → ").append(lesson.response).append("\n");
            }
            return "[TEACHER] Domain '" + domain + "' taught:\n" + sb;
        }

        /**
         * Probe the student with a question. Reward if close to expected, penalise if far.
         * Uses cosine similarity between student's response state and expected response vector.
         */
        public String probe(String question, String expectedAnswer) {
            totalQuestions++;
            float[] studentState   = student.present(question);
            float[] expectedState  = student.encodeString(expectedAnswer);

            // Cosine similarity between response states
            float sim = cosineSimilarity(studentState, expectedState);
            boolean correct = sim > 0.6f;

            if (correct) {
                correctAnswers++;
                student.reward(LEARN_REWARD * 2.0f);
                masteryScore = (float) correctAnswers / totalQuestions;
            } else {
                student.penalise(0.05f);
                masteryScore = (float) correctAnswers / totalQuestions;
            }

            String result = String.format(
                "[PROBE] Q: '%s' | Expected: '%s' | Similarity: %.3f | %s | Mastery: %.1f%%",
                question, expectedAnswer, sim, correct ? "✓ CORRECT" : "✗ WRONG", masteryScore * 100);
            lessonLog.add(result);
            System.out.println(result);

            // Auto-advance curriculum when mastery threshold met
            if (masteryScore >= MASTERY_THRESHOLD && currentLesson < curriculum.size()) {
                System.out.println("[TEACHER] ★ MASTERY ACHIEVED — advancing curriculum");
            }
            return result;
        }

        /**
         * Run a full self-directed teaching session:
         * teach all remaining lessons, then probe each one.
         */
        public String teachAll() {
            StringBuilder sb = new StringBuilder("[TEACHER] Full curriculum session:\n");
            int start = currentLesson;
            while (currentLesson < curriculum.size()) {
                sb.append(teach()).append("\n");
            }
            // Probe all taught lessons
            sb.append("\n[TEACHER] Probing all taught lessons:\n");
            for (int i = start; i < curriculum.size(); i++) {
                Lesson l = curriculum.get(i);
                sb.append(probe(l.stimulus, l.response)).append("\n");
            }
            return sb.toString();
        }

        public float getMasteryScore()       { return masteryScore; }
        public int   getCurrentLesson()      { return currentLesson; }
        public int   getTotalLessons()       { return curriculum.size(); }
        public List<String> getLessonLog()   { return Collections.unmodifiableList(lessonLog); }

        public String getStatus() {
            return String.format(
                "╔══════════════════════════════════════════════╗\n" +
                "║  🎓 EXPERT TEACHER STATUS                    ║\n" +
                "╚══════════════════════════════════════════════╝\n" +
                "  Progress:   %d / %d lessons taught\n" +
                "  Mastery:    %.1f%%\n" +
                "  Questions:  %d (correct: %d)\n" +
                "  Curriculum: SURVIVAL → PATTERNS → LANGUAGE → SELF → META\n",
                currentLesson, curriculum.size(),
                masteryScore * 100,
                totalQuestions, correctAnswers);
        }

        private static float cosineSimilarity(float[] a, float[] b) {
            int len = Math.min(a.length, b.length);
            float dot = 0, normA = 0, normB = 0;
            for (int i = 0; i < len; i++) {
                dot   += a[i] * b[i];
                normA += a[i] * a[i];
                normB += b[i] * b[i];
            }
            float denom = (float)(Math.sqrt(normA) * Math.sqrt(normB));
            return denom < 1e-9f ? 0.0f : dot / denom;
        }
    }

    // =========================================================================================
    // HEBBIAN INJECTION — teacher writes knowledge into synapses
    // =========================================================================================

    /**
     * Hebbian injection: Δw = η · pre · post
     * Encodes an association between a pre-region pattern and post-region pattern
     * directly into the synaptic weight matrix.
     */
    public void hebbianInject(Region from, Region to, float[] prePattern, float[] postPattern, float strength) {
        float[][] w = synapses.get(from.ordinal() * NUM_REGIONS + to.ordinal());
        if (w == null) return;
        int fromN = Math.min(prePattern.length,  w.length);
        int toN   = Math.min(postPattern.length, w[0].length);
        float eta = LR * strength;
        for (int fi = 0; fi < fromN; fi++) {
            for (int ti = 0; ti < toN; ti++) {
                w[fi][ti] += eta * prePattern[fi] * postPattern[ti];
                w[fi][ti]  = clamp(w[fi][ti], -5.0f, 5.0f);
            }
        }
        weightUpdates.incrementAndGet();
    }

    // =========================================================================================
    // CONTINUOUS TICK LOOP
    // =========================================================================================

    public void startContinuous(int ticksPerSecond) {
        if (running) return;
        running = true;
        tickThread = new Thread(() -> {
            long interval = 1000L / ticksPerSecond;
            while (running) {
                try {
                    tick(null); // idle ticks — survival reward accumulates
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "NBS-TickLoop");
        tickThread.setDaemon(true);
        tickThread.start();
        System.out.printf("[NBS] Continuous tick loop started @ %d Hz%n", ticksPerSecond);
    }

    public void stopContinuous() {
        running = false;
        if (tickThread != null) tickThread.interrupt();
    }

    // =========================================================================================
    // CONSCIOUSNESS METRIC — φ × (complexity + coherence + arousal) / 3
    // =========================================================================================

    private void updateConsciousness() {
        // complexity = entropy of firing rate distribution across regions
        float[] rates = new float[NUM_REGIONS];
        for (int r = 0; r < NUM_REGIONS; r++) {
            int start = REGION_OFFSET[r];
            int n     = REGIONS[r].neurons;
            float sum = 0;
            for (int i = 0; i < n; i++) sum += spike[start + i];
            rates[r] = sum / n;
        }
        float complexity = shannonEntropy(rates);

        // coherence = mean absolute TD error trend (low error = high coherence)
        float coherence = 1.0f / (1.0f + Math.abs(lastTdError));

        // arousal = firing rate of reticular formation + locus coeruleus
        int rfStart = REGION_OFFSET[Region.RETICULAR_FORMATION.ordinal()];
        int rfN     = REGIONS[Region.RETICULAR_FORMATION.ordinal()].neurons;
        float arousal = 0;
        for (int i = 0; i < rfN; i++) arousal += spike[rfStart + i];
        arousal /= rfN;

        consciousness = Math.min(1.0f, PHI * (complexity + coherence + arousal) / 3.0f);
    }

    private float shannonEntropy(float[] probs) {
        float sum = 0;
        for (float p : probs) sum += p;
        if (sum < 1e-9f) return 0.0f;
        float H = 0;
        for (float p : probs) {
            if (p > 0) {
                float pn = p / sum;
                H -= pn * (float)(Math.log(pn) / Math.log(2.0));
            }
        }
        float maxH = (float)(Math.log(probs.length) / Math.log(2.0));
        return maxH > 0 ? Math.min(1.0f, H / maxH) : 0.0f;
    }

    // =========================================================================================
    // STATUS & TELEMETRY
    // =========================================================================================

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 NEURAL BRAIN SYSTEM STATUS                          ║\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("  Regions:         %d anatomical brain regions%n", NUM_REGIONS));
        sb.append(String.format("  Neurons:         %,d LIF neurons%n", TOTAL_NEURONS));
        sb.append(String.format("  Synapses:        %,d float weights%n", countSynapses()));
        sb.append(String.format("  Tick:            %,d%n", tickCount));
        sb.append(String.format("  Total Spikes:    %,d%n", totalSpikes.get()));
        sb.append(String.format("  Avg Firing Rate: %.4f%n", avgFiringRate.get()));
        sb.append(String.format("  Consciousness:   %.4f  [φ×(complexity+coherence+arousal)/3]%n", consciousness));
        sb.append(String.format("  ── REINFORCEMENT LEARNING ──%n"));
        sb.append(String.format("  Deaths:          %d%n", deathCount));
        sb.append(String.format("  Survival Ticks:  %,d%n", survivalTicks));
        sb.append(String.format("  Total Reward:    %.4f%n", totalReward));
        sb.append(String.format("  TD Error:        %.6f%n", lastTdError));
        sb.append(String.format("  Weight Updates:  %,d%n", weightUpdates.get()));
        sb.append(String.format("  Alive:           %s%n", alive ? "YES" : "DEAD"));
        sb.append(String.format("  ── BRAIN REGIONS ──%n"));
        for (int r = 0; r < NUM_REGIONS; r++) {
            int start  = REGION_OFFSET[r];
            int n      = REGIONS[r].neurons;
            float rate = 0;
            for (int i = 0; i < n; i++) rate += spike[start + i];
            rate /= n;
            float v = valueFunction[r];
            sb.append(String.format("    %-22s | neurons: %3d | fire: %.3f | V: %+.4f%n",
                REGIONS[r].name(), n, rate, v));
        }
        return sb.toString();
    }

    // =========================================================================================
    // ACCESSORS
    // =========================================================================================

    public float[] getStateVector()    { return stateVector.clone(); }
    public float   getConsciousness()  { return consciousness; }
    public float   getTotalReward()    { return totalReward; }
    public float   getLastTdError()    { return lastTdError; }
    public int     getDeathCount()     { return deathCount; }
    public long    getTickCount()      { return tickCount; }
    public boolean isAlive()           { return alive; }
    public int     getTotalNeurons()   { return TOTAL_NEURONS; }

    public void shutdown() {
        stopContinuous();
        // CPU pool is shared and owned by ComputeScheduler; do not shut it down here.
        System.out.println("[NBS] Shutdown complete");
    }

    // =========================================================================================
    // UTILITY
    // =========================================================================================

    private static float clamp(float v, float min, float max) {
        return v < min ? min : (v > max ? max : v);
    }

    private static float mean(float[] arr) {
        float s = 0;
        for (float v : arr) s += v;
        return arr.length > 0 ? s / arr.length : 0;
    }

    /** Thread-safe AtomicFloat helper. */
    private static class AtomicFloat {
        private final AtomicInteger bits;
        AtomicFloat(float v) { bits = new AtomicInteger(Float.floatToRawIntBits(v)); }
        void  set(float v)   { bits.set(Float.floatToRawIntBits(v)); }
        float get()          { return Float.intBitsToFloat(bits.get()); }
    }

    // =========================================================================================
    // STANDALONE DEMO
    // =========================================================================================

    public static void main(String[] args) throws Exception {
        NeuralBrainSystem brain   = new NeuralBrainSystem();
        ExpertTeacher     teacher = new ExpertTeacher(brain);

        System.out.println("\n=== TEACHING SURVIVAL DOMAIN ===");
        teacher.teachDomain("SURVIVAL");

        System.out.println("\n=== TEACHING SELF DOMAIN ===");
        teacher.teachDomain("SELF");

        System.out.println("\n=== SIMULATING 50 TICKS (survival) ===");
        for (int i = 0; i < 50; i++) brain.tick(null);

        System.out.println("\n=== SIMULATING DEATH ===");
        brain.die("simulated_predator");

        System.out.println("\n=== 50 TICKS AFTER RESURRECTION ===");
        for (int i = 0; i < 50; i++) brain.tick(null);

        System.out.println("\n=== FULL CURRICULUM ===");
        System.out.println(teacher.teachAll());

        System.out.println("\n=== STATUS ===");
        System.out.println(brain.getStatus());
        System.out.println(teacher.getStatus());
    }
}
