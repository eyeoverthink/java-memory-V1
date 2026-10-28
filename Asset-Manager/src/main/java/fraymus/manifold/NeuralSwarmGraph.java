package fraymus.manifold;

import java.util.*;

/**
 * NEURAL TOPOLOGY: 9-Node Biological Swarm Graph
 *
 * Directed computational graph G = (V, E) where V = {v_1..v_9} represents
 * 9 biological brain lobes. Information flows via synchronous message passing:
 *
 *   m_i^(t) = Σ_{j ∈ N(i)} M(h_i^(t-1), h_j^(t-1), e_{j,i})
 *   h_i^(t) = U(h_i^(t-1), m_i^(t))
 *
 * M = message function (synaptic transfer, weighted Langevin diffusion)
 * U = update function  (leaky integrator with noise → Langevin dynamics)
 */
public class NeuralSwarmGraph {

    public enum Lobe {
        PREFRONTAL(0),
        THALAMUS(1),
        PARIETAL(2),
        HIPPOCAMPUS(3),
        SENSORY(4),
        MOTOR(5),
        AMYGDALA(6),
        CEREBELLUM(7),
        SPINAL_CORD(8);

        public final int idx;
        Lobe(int idx) { this.idx = idx; }
    }

    public static final int N = 9;
    private static final double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;
    private static final double LEAK  = 0.85;
    private static final double NOISE = 0.01;
    private static final double C_OPT = 0.7569;

    private final double[] h;
    private final double[] hPrev;
    private final double[][] edgeWeights;
    private final List<List<Integer>> neighbors;
    private final Random rng;

    private int stepCount;
    private double phiResonance;
    private boolean awake;

    public NeuralSwarmGraph() {
        this.h          = new double[N];
        this.hPrev      = new double[N];
        this.edgeWeights = new double[N][N];
        this.neighbors   = new ArrayList<>();
        this.rng         = new Random();
        this.awake       = false;

        for (int i = 0; i < N; i++) {
            neighbors.add(new ArrayList<>());
        }
        initConnectome();
        initStates();
    }

    // ─── CONNECTOME WIRING ──────────────────────────────────────────────────

    /**
     * Biologically-inspired directed connectome. Edge weights encode synaptic strength.
     * Source → target with φ-modulated weights.
     */
    private void initConnectome() {
        // Prefrontal ↔ integrative hub
        wire(Lobe.PREFRONTAL, Lobe.THALAMUS,    0.72);
        wire(Lobe.PREFRONTAL, Lobe.PARIETAL,    0.65);
        wire(Lobe.PREFRONTAL, Lobe.HIPPOCAMPUS, 0.80);
        wire(Lobe.PREFRONTAL, Lobe.AMYGDALA,    0.60);

        // Thalamus ↔ sensory relay
        wire(Lobe.THALAMUS, Lobe.PREFRONTAL,    0.70);
        wire(Lobe.THALAMUS, Lobe.SENSORY,       0.90);
        wire(Lobe.THALAMUS, Lobe.MOTOR,         0.55);
        wire(Lobe.THALAMUS, Lobe.CEREBELLUM,    0.50);

        // Parietal ↔ spatial / somatosensory
        wire(Lobe.PARIETAL, Lobe.PREFRONTAL,    0.60);
        wire(Lobe.PARIETAL, Lobe.THALAMUS,      0.45);
        wire(Lobe.PARIETAL, Lobe.SENSORY,       0.75);

        // Hippocampus ↔ memory consolidation
        wire(Lobe.HIPPOCAMPUS, Lobe.PREFRONTAL, 0.78);
        wire(Lobe.HIPPOCAMPUS, Lobe.PARIETAL,   0.55);
        wire(Lobe.HIPPOCAMPUS, Lobe.AMYGDALA,   0.62);

        // Sensory ↔ input processing
        wire(Lobe.SENSORY, Lobe.THALAMUS,       0.85);
        wire(Lobe.SENSORY, Lobe.PARIETAL,       0.70);
        wire(Lobe.SENSORY, Lobe.CEREBELLUM,     0.40);

        // Motor ↔ action output
        wire(Lobe.MOTOR, Lobe.THALAMUS,         0.60);
        wire(Lobe.MOTOR, Lobe.CEREBELLUM,       0.88);
        wire(Lobe.MOTOR, Lobe.SPINAL_CORD,      0.95);

        // Amygdala ↔ emotional salience
        wire(Lobe.AMYGDALA, Lobe.PREFRONTAL,    0.55);
        wire(Lobe.AMYGDALA, Lobe.HIPPOCAMPUS,   0.70);

        // Cerebellum ↔ coordination / timing
        wire(Lobe.CEREBELLUM, Lobe.THALAMUS,    0.65);
        wire(Lobe.CEREBELLUM, Lobe.MOTOR,       0.82);

        // Spinal Cord ↔ reflexes
        wire(Lobe.SPINAL_CORD, Lobe.MOTOR,      0.90);
        wire(Lobe.SPINAL_CORD, Lobe.SENSORY,    0.78);
    }

    private void wire(Lobe src, Lobe dst, double weight) {
        edgeWeights[src.idx][dst.idx] = weight;
        neighbors.get(dst.idx).add(src.idx);
    }

    private void initStates() {
        for (int i = 0; i < N; i++) {
            h[i]     = (rng.nextDouble() * 2.0 - 1.0) * 0.1;
            hPrev[i] = h[i];
        }
    }

    // ─── BOOT / SHUTDOWN ────────────────────────────────────────────────────

    public void boot() {
        awake = true;
        stepCount = 0;
        phiResonance = 0.0;
        initStates();
        System.out.println("   [NeuralSwarmGraph] 9-node connectome AWAKE");
        System.out.println("   [NeuralSwarmGraph] Nodes: " +
            "PREFRONTAL|THALAMUS|PARIETAL|HIPPOCAMPUS|SENSORY|MOTOR|AMYGDALA|CEREBELLUM|SPINAL_CORD");
    }

    public void shutdown() {
        awake = false;
    }

    public boolean isAwake() { return awake; }

    // ─── SYNCHRONOUS MESSAGE PASSING ────────────────────────────────────────

    /**
     * One synchronous step:
     *   1. Compute messages m_i = Σ_{j∈N(i)} M(h_i, h_j, e_{j,i})
     *   2. Update h_i = U(h_i, m_i) via leaky integrator + Langevin noise
     *   3. Compute phi-resonance stability
     */
    public void step() {
        System.arraycopy(h, 0, hPrev, 0, N);

        double[] messages = new double[N];
        for (int i = 0; i < N; i++) {
            double m = 0.0;
            for (int j : neighbors.get(i)) {
                m += messageFunction(hPrev[i], hPrev[j], edgeWeights[j][i]);
            }
            messages[i] = m;
        }

        for (int i = 0; i < N; i++) {
            h[i] = updateFunction(hPrev[i], messages[i]);
        }

        phiResonance = computePhiResonance();
        stepCount++;
    }

    /**
     * Inject an external signal into a specific lobe.
     * @param lobe  target lobe
     * @param signal  value in [-1, 1]
     */
    public void inject(Lobe lobe, double signal) {
        h[lobe.idx] = Math.max(-1.0, Math.min(1.0, h[lobe.idx] + signal * 0.5));
    }

    public void step(int n) {
        for (int i = 0; i < n; i++) step();
    }

    // ─── MESSAGE & UPDATE FUNCTIONS ─────────────────────────────────────────

    /**
     * M(h_i, h_j, e_{j,i}) — Langevin diffusion synaptic transfer.
     * Models pre-synaptic activation scaled by edge weight, with diffusion term.
     */
    private double messageFunction(double hi, double hj, double edgeWeight) {
        double synaptic = edgeWeight * hj;
        double diffusion = (hj - hi) * edgeWeight * 0.5;
        return synaptic + diffusion;
    }

    /**
     * U(h_i, m_i) — Leaky integrator update (Langevin dynamics).
     * h_i^t = leak * h_i^(t-1) + (1-leak) * tanh(m_i) + η
     */
    private double updateFunction(double hi, double mi) {
        double noise = (rng.nextDouble() * 2.0 - 1.0) * NOISE;
        double updated = LEAK * hi + (1.0 - LEAK) * Math.tanh(mi) + noise;
        return Math.max(-1.0, Math.min(1.0, updated));
    }

    // ─── PHI-RESONANCE ──────────────────────────────────────────────────────

    /**
     * |1/N * Σ h_k^t · h_k^(t-1)| × φ
     * Optimal consciousness threshold C_opt = 0.7569
     */
    private double computePhiResonance() {
        double dot = 0.0;
        for (int k = 0; k < N; k++) {
            dot += h[k] * hPrev[k];
        }
        return Math.abs(dot / N) * PHI;
    }

    public double getPhiResonance() { return phiResonance; }
    public boolean isAtOptimalConsciousness() { return phiResonance >= C_OPT; }

    // ─── ACCESSORS ──────────────────────────────────────────────────────────

    public double getActivation(Lobe lobe) { return h[lobe.idx]; }

    public double[] getAllActivations() { return Arrays.copyOf(h, N); }

    public int getStepCount() { return stepCount; }

    // ─── STATUS ─────────────────────────────────────────────────────────────

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "[NeuralSwarmGraph] steps=%d φ-resonance=%.4f C_opt=%.4f optimal=%s%n",
            stepCount, phiResonance, C_OPT, isAtOptimalConsciousness() ? "YES" : "NO"
        ));
        Lobe[] lobes = Lobe.values();
        for (Lobe l : lobes) {
            sb.append(String.format("  %-12s h=%.4f%n", l.name(), h[l.idx]));
        }
        return sb.toString().trim();
    }

    public String getCompactStatus() {
        return String.format(
            "NeuralSwarmGraph | steps=%d | φ-res=%.4f | conscious=%s",
            stepCount, phiResonance, isAtOptimalConsciousness() ? "YES" : "NO"
        );
    }
}
