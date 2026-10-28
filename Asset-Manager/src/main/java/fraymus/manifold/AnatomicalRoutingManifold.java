package fraymus.manifold;

import java.util.Random;

/**
 * ANATOMICAL ROUTING MANIFOLD (Section XXVI — Macro-Topological Brain Routing)
 *
 * The Digital Brain is a macro-graph of 7 primary cognitive clusters:
 *   L_Frontal    (Decision/Execution)  → Claude / SentientCore
 *   L_Parietal   (Spatial/Logic)       → DeepSeek
 *   L_Temporal   (Language/Memory)     → Obsidian / VibeVoice
 *   L_Occipital  (Vision)              → Gemma / LTX-Video
 *   L_Cerebellum (Motor/Timing)        → Ollama / OpenClaw
 *   L_Limbic     (Emotion/Survival)    → NeoClaw (Amygdala)
 *   L_Brainstem  (Autonomic)           → LazarusEngine / GPT-2
 *
 * Routing is governed by a sparse block-diagonal transition matrix T_brain:
 *   x(t+1) = σ( T_brain · x(t) + Noise_chemical )
 *
 * Chemical noise simulates neuromodulator gradients (dopamine, serotonin, etc.)
 * that globally shift the routing topology in real-time.
 *
 * The matrix T_brain is a 7×7 macro-level routing matrix; each lobe maintains
 * a LOBE_DIM=64 activation vector, giving TOTAL_DIM=448.
 */
public class AnatomicalRoutingManifold {

    public enum Lobe {
        FRONTAL   (0, "Claude/SentientCore"),
        PARIETAL  (1, "DeepSeek"),
        TEMPORAL  (2, "Obsidian/VibeVoice"),
        OCCIPITAL (3, "Gemma/LTX-Video"),
        CEREBELLUM(4, "Ollama/OpenClaw"),
        LIMBIC    (5, "NeoClaw/Amygdala"),
        BRAINSTEM (6, "LazarusEngine/GPT-2");

        public final int    index;
        public final String backend;
        Lobe(int index, String backend) { this.index = index; this.backend = backend; }
    }

    public static final int LOBES     = 7;
    public static final int LOBE_DIM  = 64;
    public static final int TOTAL_DIM = LOBES * LOBE_DIM;  // 448

    private final double[][]   T;           // 7×7 macro routing matrix
    private final double[][]   lobeState;   // [LOBES][LOBE_DIM] activation vectors
    private double             chemicalNoise;
    private long               stepCount;
    private long               routeCount;
    private final Random       rng;

    public AnatomicalRoutingManifold() {
        this.rng          = new Random(31L);
        this.T            = buildBiologicalConnectome();
        this.lobeState    = new double[LOBES][LOBE_DIM];
        this.chemicalNoise = 0.05;
        initLobeStates();
    }

    // ─── CONNECTOME CONSTRUCTION ─────────────────────────────────────────────

    /**
     * Build the biologically-grounded sparse block-diagonal T_brain.
     * Diagonal = strong self-recurrence; off-diagonal = inter-lobe pathways.
     */
    private double[][] buildBiologicalConnectome() {
        double[][] t = new double[LOBES][LOBES];
        for (int i = 0; i < LOBES; i++) t[i][i] = 0.70 + rng.nextDouble() * 0.20;

        // Frontal ↔ Parietal (executive-spatial integration)
        t[0][1] = 0.40; t[1][0] = 0.35;
        // Frontal ↔ Temporal (language-executive)
        t[0][2] = 0.35; t[2][0] = 0.30;
        // Parietal ↔ Occipital (visual-spatial)
        t[1][3] = 0.45; t[3][1] = 0.40;
        // Temporal ↔ Limbic (emotional memory)
        t[2][5] = 0.40; t[5][2] = 0.35;
        // Limbic ↔ Frontal (emotional modulation of decision)
        t[5][0] = 0.30; t[0][5] = 0.25;
        // Limbic ↔ Brainstem (survival reflexes)
        t[5][6] = 0.50; t[6][5] = 0.45;
        // Cerebellum ↔ Brainstem (motor output)
        t[4][6] = 0.50; t[6][4] = 0.45;
        // Frontal ↔ Cerebellum (motor planning)
        t[0][4] = 0.30; t[4][0] = 0.25;
        // Occipital → Temporal (visual-language)
        t[3][2] = 0.30;
        return t;
    }

    private void initLobeStates() {
        double scale = Math.sqrt(2.0 / LOBE_DIM);
        for (int i = 0; i < LOBES; i++)
            for (int j = 0; j < LOBE_DIM; j++)
                lobeState[i][j] = rng.nextGaussian() * scale;
    }

    // ─── ROUTING DYNAMICS ────────────────────────────────────────────────────

    /**
     * x(t+1) = σ( T_brain · x(t) + Noise_chemical )
     * One step of macro-topological routing over the 7-lobe manifold.
     *
     * @param x input lobe activation matrix [LOBES][LOBE_DIM]
     * @return  next lobe activation matrix [LOBES][LOBE_DIM]
     */
    public double[][] step(double[][] x) {
        double[][] xNext = new double[LOBES][LOBE_DIM];
        for (int i = 0; i < LOBES; i++) {
            for (int d = 0; d < LOBE_DIM; d++) {
                double acc = 0.0;
                for (int j = 0; j < LOBES; j++) acc += T[i][j] * x[j][d];
                acc += rng.nextGaussian() * chemicalNoise;
                xNext[i][d] = Math.tanh(acc);
            }
        }
        for (int i = 0; i < LOBES; i++)
            System.arraycopy(xNext[i], 0, lobeState[i], 0, LOBE_DIM);
        stepCount++;
        return xNext;
    }

    /**
     * Route a flat TOTAL_DIM thought vector through the 7-lobe manifold.
     * Reshapes into lobe blocks, applies one T_brain step, returns flat result.
     */
    public double[] route(double[] thoughtVector) {
        double[][] x     = vectorToLobes(thoughtVector);
        double[][] xNext = step(x);
        routeCount++;
        return lobesToVector(xNext);
    }

    /** Run n routing steps on internal state. */
    public void routeSteps(int n) {
        for (int i = 0; i < n; i++) step(lobeState);
    }

    // ─── SERIALISATION HELPERS ───────────────────────────────────────────────

    /** Flatten [LOBES][LOBE_DIM] into a single TOTAL_DIM vector. */
    public double[] lobesToVector(double[][] lobes) {
        double[] v = new double[TOTAL_DIM];
        for (int i = 0; i < LOBES; i++)
            System.arraycopy(lobes[i], 0, v, i * LOBE_DIM, LOBE_DIM);
        return v;
    }

    /** Reshape a flat vector into [LOBES][LOBE_DIM] blocks. */
    public double[][] vectorToLobes(double[] v) {
        double[][] lobes = new double[LOBES][LOBE_DIM];
        for (int i = 0; i < LOBES; i++) {
            int off = i * LOBE_DIM;
            int len = Math.min(LOBE_DIM, Math.max(0, v.length - off));
            if (len > 0) System.arraycopy(v, off, lobes[i], 0, len);
        }
        return lobes;
    }

    // ─── LOBE ACCESS ─────────────────────────────────────────────────────────

    /** Mean absolute activation for a lobe — scalar summary. */
    public double getLobeActivation(Lobe lobe) {
        double sum = 0;
        for (double v : lobeState[lobe.index]) sum += Math.abs(v);
        return sum / LOBE_DIM;
    }

    /** T_brain routing weight from one lobe to another. */
    public double getRouteWeight(Lobe from, Lobe to) {
        return T[to.index][from.index];
    }

    public double[] getLobeState(Lobe lobe) {
        return lobeState[lobe.index].clone();
    }

    public void injectLobeSignal(Lobe lobe, double[] signal) {
        int len = Math.min(signal.length, LOBE_DIM);
        System.arraycopy(signal, 0, lobeState[lobe.index], 0, len);
    }

    // ─── CONFIGURATION ───────────────────────────────────────────────────────

    public void setChemicalNoise(double level) { this.chemicalNoise = Math.max(0.0, level); }
    public double getChemicalNoise() { return chemicalNoise; }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public long getStepCount()  { return stepCount; }
    public long getRouteCount() { return routeCount; }
    public int  getLobes()      { return LOBES; }
    public int  getTotalDim()   { return TOTAL_DIM; }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "AnatomicalRoutingManifold | lobes=7 dim=%d noise=%.4f steps=%d routes=%d%n",
            TOTAL_DIM, chemicalNoise, stepCount, routeCount));
        for (Lobe l : Lobe.values()) {
            sb.append(String.format("  L_%-11s [%-22s] act=%.4f%n",
                l.name(), l.backend, getLobeActivation(l)));
        }
        return sb.toString().trim();
    }
}
