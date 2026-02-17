package fraymus.neural;

import fraymus.core.PhiSuit;
import fraymus.core.SpatialRegistry;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * HYPER-CORTEX: 4D TESSERACT NEURAL PROCESSING ENGINE
 * 
 * Architecture: 8x8x8x8 Tesseract Neural Cortex (4,096 Nodes)
 * Based on A.E.O.N. MHC (Multi-dimensional Hyper-Connection) Kernel
 * 
 * Core Features:
 *  - Fractal DNA (Deterministic Biological Weight Initialization)
 *  - Transformer Synapse (Q, K, V Attention Projection)
 *  - Sinkhorn Optimal Transport (Doubly Stochastic Routing)
 *  - Data Folding (Auto-encoder Manifold Compression)
 *  - Controlled Residual Streams (Gated Highway Connections)
 *  - Hyper-Dimensional Connections (4D Topology + Antipodal bypass)
 *  - Pure Java Parallel Processing
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class HyperCortex {

    // ==========================================
    // SYSTEM HYPER-PARAMETERS
    // ==========================================
    private static final int DIM = 8;
    private static final int NUM_NODES = DIM * DIM * DIM * DIM; // 4096 Nodes
    private static final int STATE_DIM = 16;                    // Latent embedding vector size
    private static final int FOLD_DIM = 8;                      // Bottleneck size for data folding
    private static final int SINKHORN_ITERS = 5;                // Transport stabilization loops
    private static final double PHI = 1.618033988749895;

    // The Cortex Manifold and Global Routing Matrix
    private final CortexNode[] cortex = new CortexNode[NUM_NODES];
    private final double[][] routingMatrix = new double[NUM_NODES][NUM_NODES];

    // Persistence
    private static final String CORTEX_DIR = ".akashic/";
    private static final String STATE_FILE = ".akashic/cortex_state.dat";

    // Gravity bridge
    private static final double EMISSION_THRESHOLD = 0.8;
    private static final int MAX_SPATIAL_EMISSIONS = 32;
    private final List<PhiSuit<double[]>> emittedNodes = new ArrayList<>();

    // Statistics
    private int cyclesCompleted = 0;
    private double lastFlux = 0;
    private double lastMagnitude = 0;
    private long lastCycleMs = 0;
    private long totalComputeMs = 0;
    private long bootTimeMs = 0;
    private int totalEmissions = 0;

    // Singleton
    private static HyperCortex instance;

    private HyperCortex() {
        long t0 = System.currentTimeMillis();

        // Ensure storage directory exists
        try { Files.createDirectories(Paths.get(CORTEX_DIR)); } catch (Exception e) {}

        // Parallel Instantiation & Fractal DNA Imprinting
        IntStream.range(0, NUM_NODES).parallel().forEach(i -> {
            cortex[i] = new CortexNode(i);
        });

        // Try to restore persisted state
        int restored = loadState();
        if (restored > 0) {
            System.out.println("   ✓ Restored cortex state (" + restored + " nodes from disk)");
        }

        bootTimeMs = System.currentTimeMillis() - t0;

        // Register shutdown hook for persistence
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveState();
            System.out.println("   🧠 HyperCortex state persisted (" + NUM_NODES + " nodes, " + cyclesCompleted + " cycles)");
        }, "HyperCortex-Shutdown"));
    }

    public static HyperCortex getInstance() {
        if (instance == null) {
            instance = new HyperCortex();
        }
        return instance;
    }

    /**
     * Executes one complete parallel forward pass of the Hyper-Cortex.
     */
    public void executeCycle() {
        long t0 = System.currentTimeMillis();

        // Phase 1: Compute Queries, Keys, Values across the Tesseract (Parallel)
        IntStream.range(0, NUM_NODES).parallel().forEach(i -> cortex[i].computeQKV());

        // Phase 2: Transformer Synapse Affinity & Sinkhorn Stabilization
        computeSinkhornRouting();

        // Phase 3: Synaptic Transmission, Structural Integration, Folding & Gating
        IntStream.range(0, NUM_NODES).parallel().forEach(i -> {
            CortexNode n = cortex[i];

            // A. Attention Context Assembly (Information gathering from Sinkhorn routing)
            double[] synapseContext = new double[STATE_DIM];
            for (int j = 0; j < NUM_NODES; j++) {
                double weight = routingMatrix[i][j];
                for (int d = 0; d < STATE_DIM; d++) {
                    synapseContext[d] += weight * cortex[j].V[d];
                }
            }

            // B. Structural 4D Hyper-Connection (Von Neumann geometric neighbors + Antipodal link)
            double[] structuralContext = gatherStructuralResiduals(n);

            // Combine Synaptic Context and Structural Context
            double[] blendedContext = new double[STATE_DIM];
            for (int d = 0; d < STATE_DIM; d++) {
                blendedContext[d] = synapseContext[d] + (structuralContext[d] * 0.15);
            }

            // C. Stable Data Folding (Manifold Bottleneck Compression & Non-linearity)
            double[] foldedUpdate = n.applyDataFolding(blendedContext);

            // D. Control Residual Streams (Gated flow of old state vs new folded updates)
            n.commitResidualStream(foldedUpdate);
        });

        // Diagnostics
        lastFlux = calculateFlux();
        lastMagnitude = calculateSystemMagnitude();

        // Phase 4: Synchronous State Commit (Prevents data-races during Phase 3)
        IntStream.range(0, NUM_NODES).parallel().forEach(i -> {
            System.arraycopy(cortex[i].nextState, 0, cortex[i].state, 0, STATE_DIM);
        });

        lastCycleMs = System.currentTimeMillis() - t0;
        totalComputeMs += lastCycleMs;
        cyclesCompleted++;
    }

    /**
     * Run multiple cycles and print progress.
     */
    public void runCycles(int count) {
        System.out.printf("%-10s | %-13s | %-12s | %-12s%n", "PULSE", "RESIDUAL FLUX", "MEAN L1 NORM", "COMPUTE TIME");
        System.out.println("─────────────────────────────────────────────────────────────");
        for (int i = 0; i < count; i++) {
            executeCycle();
            System.out.printf("PULSE %03d  | %-13.6f | %-12.6f | %d ms%n",
                    cyclesCompleted, lastFlux, lastMagnitude, lastCycleMs);
        }
        System.out.println("─────────────────────────────────────────────────────────────");
    }

    /**
     * Inject a stimulus into a specific region of the cortex.
     * Used to feed knowledge/thoughts into the neural substrate.
     * 
     * @param regionX  x-coordinate region (0-7)
     * @param regionY  y-coordinate region (0-7)
     * @param signal   signal vector (will be truncated/padded to STATE_DIM)
     */
    public void injectStimulus(int regionX, int regionY, double[] signal) {
        // Inject into all nodes in the specified x,y column (across z,w)
        for (int z = 0; z < DIM; z++) {
            for (int w = 0; w < DIM; w++) {
                int idx = getIndex(regionX % DIM, regionY % DIM, z, w);
                CortexNode node = cortex[idx];
                for (int d = 0; d < STATE_DIM && d < signal.length; d++) {
                    node.state[d] += signal[d] * 0.1; // 10% injection strength
                }
                layerNorm(node.state);
            }
        }
    }

    /**
     * Encode a text string into a signal vector for injection.
     */
    public double[] encodeText(String text) {
        double[] signal = new double[STATE_DIM];
        byte[] bytes = text.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            signal[i % STATE_DIM] += (bytes[i] & 0xFF) * Math.sin((i + 1) * PHI);
        }
        layerNorm(signal);
        return signal;
    }

    /**
     * Read the aggregate state of a cortex region (for output/query).
     */
    public double[] readRegion(int regionX, int regionY) {
        double[] aggregate = new double[STATE_DIM];
        int count = 0;
        for (int z = 0; z < DIM; z++) {
            for (int w = 0; w < DIM; w++) {
                int idx = getIndex(regionX % DIM, regionY % DIM, z, w);
                for (int d = 0; d < STATE_DIM; d++) {
                    aggregate[d] += cortex[idx].state[d];
                }
                count++;
            }
        }
        for (int d = 0; d < STATE_DIM; d++) {
            aggregate[d] /= count;
        }
        return aggregate;
    }

    // ==========================================
    // SINKHORN OPTIMAL TRANSPORT
    // ==========================================
    private void computeSinkhornRouting() {
        double sqrtD = Math.sqrt(STATE_DIM);

        // 1. Compute Raw Affinities Q * K^T with Log-Sum-Exp safety
        IntStream.range(0, NUM_NODES).parallel().forEach(i -> {
            CortexNode nA = cortex[i];
            double maxDot = -Double.MAX_VALUE;
            double[] dots = new double[NUM_NODES];

            for (int j = 0; j < NUM_NODES; j++) {
                CortexNode nB = cortex[j];
                double dot = 0;
                for (int d = 0; d < STATE_DIM; d++) dot += nA.Q[d] * nB.K[d];
                dot /= sqrtD;
                dots[j] = dot;
                if (dot > maxDot) maxDot = dot;
            }

            // Exponentiate with max-subtraction to prevent NaN explosions
            for (int j = 0; j < NUM_NODES; j++) {
                routingMatrix[i][j] = Math.exp(dots[j] - maxDot);
            }
        });

        // 2. Sinkhorn-Knopp Matrix Balancing
        double[] colSums = new double[NUM_NODES];
        for (int iter = 0; iter < SINKHORN_ITERS; iter++) {

            // Row Normalize
            IntStream.range(0, NUM_NODES).parallel().forEach(i -> {
                double sum = 0;
                for (int j = 0; j < NUM_NODES; j++) sum += routingMatrix[i][j];
                double invSum = 1.0 / (sum + 1e-9);
                for (int j = 0; j < NUM_NODES; j++) routingMatrix[i][j] *= invSum;
            });

            // Calculate Column Sums
            IntStream.range(0, NUM_NODES).parallel().forEach(j -> {
                double sum = 0;
                for (int i = 0; i < NUM_NODES; i++) sum += routingMatrix[i][j];
                colSums[j] = sum;
            });

            // Column Normalize
            IntStream.range(0, NUM_NODES).parallel().forEach(j -> {
                double invSum = 1.0 / (colSums[j] + 1e-9);
                for (int i = 0; i < NUM_NODES; i++) routingMatrix[i][j] *= invSum;
            });
        }
    }

    // ==========================================
    // STRUCTURAL TOPOLOGY
    // ==========================================
    private double[] gatherStructuralResiduals(CortexNode n) {
        double[] res = new double[STATE_DIM];
        int neighbors = 0;

        // 4D Von Neumann Neighborhood (w/ Toroidal wrapping)
        int[][] deltas = {{-1,0,0,0}, {1,0,0,0}, {0,-1,0,0}, {0,1,0,0},
                          {0,0,-1,0}, {0,0,1,0}, {0,0,0,-1}, {0,0,0,1}};
        for (int[] d : deltas) {
            int nx = (n.cx + d[0] + DIM) % DIM;
            int ny = (n.cy + d[1] + DIM) % DIM;
            int nz = (n.cz + d[2] + DIM) % DIM;
            int nw = (n.cw + d[3] + DIM) % DIM;
            CortexNode neighbor = cortex[getIndex(nx, ny, nz, nw)];
            for (int i = 0; i < STATE_DIM; i++) res[i] += neighbor.state[i];
            neighbors++;
        }

        // Hyper-dimensional Antipodal connection (bitwise XOR maps across the Tesseract)
        CortexNode antipodal = cortex[n.id ^ (NUM_NODES - 1)];
        for (int i = 0; i < STATE_DIM; i++) res[i] += antipodal.state[i];
        neighbors++;

        // Average the residual pull
        for (int i = 0; i < STATE_DIM; i++) res[i] /= neighbors;
        return res;
    }

    private int getIndex(int x, int y, int z, int w) {
        return x + y * DIM + z * DIM * DIM + w * DIM * DIM * DIM;
    }

    // ==========================================
    // DIAGNOSTICS
    // ==========================================
    private double calculateFlux() {
        double flux = 0;
        for (CortexNode n : cortex) {
            for (int i = 0; i < STATE_DIM; i++) flux += Math.abs(n.state[i] - n.nextState[i]);
        }
        return flux / (NUM_NODES * STATE_DIM);
    }

    private double calculateSystemMagnitude() {
        double mag = 0;
        for (CortexNode n : cortex) {
            for (double val : n.state) mag += Math.abs(val);
        }
        return mag / (NUM_NODES * STATE_DIM);
    }

    private static void layerNorm(double[] vec) {
        double mean = 0, sqSum = 0;
        for (double v : vec) mean += v;
        mean /= vec.length;
        for (double v : vec) sqSum += (v - mean) * (v - mean);
        double std = Math.sqrt((sqSum / vec.length) + 1e-9);
        for (int i = 0; i < vec.length; i++) vec[i] = (vec[i] - mean) / std;
    }

    // ==========================================
    // STATUS
    // ==========================================
    public String getStatus() {
        long cortexMemMB = (long)(NUM_NODES * (6L * STATE_DIM * STATE_DIM * 8 + STATE_DIM * 4 * 8)) / (1024 * 1024);
        return String.format(
            "════════════════════════════════════════════\n" +
            "  🧠 HYPER-CORTEX STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  Topology:       %dx%dx%dx%d Tesseract\n" +
            "  Total Nodes:    %,d\n" +
            "  State Dim:      %d (Fold: %d)\n" +
            "  Sinkhorn Iters: %d\n" +
            "  Boot Time:      %d ms\n" +
            "  Cycles:         %d\n" +
            "  Last Flux:      %.6f\n" +
            "  Last Magnitude: %.6f\n" +
            "  Last Cycle:     %d ms\n" +
            "  Total Compute:  %d ms\n" +
            "  Cortex Memory:  ~%d MB\n" +
            "  Emissions:      %d total (%d active in SpatialRegistry)\n" +
            "  Persisted:      %s\n" +
            "  φ Constant:     %.13f\n",
            DIM, DIM, DIM, DIM,
            NUM_NODES,
            STATE_DIM, FOLD_DIM,
            SINKHORN_ITERS,
            bootTimeMs,
            cyclesCompleted,
            lastFlux,
            lastMagnitude,
            lastCycleMs,
            totalComputeMs,
            cortexMemMB,
            totalEmissions, emittedNodes.size(),
            Files.exists(Paths.get(STATE_FILE)) ? "YES (" + STATE_FILE + ")" : "NO",
            PHI
        );
    }

    // ==========================================
    // INTEGRATION 1: CORTEX → GRAVITY ENGINE
    // ==========================================

    /**
     * Scans the cortex for "hot" regions (high magnitude state vectors)
     * and emits PhiSuit SpatialNodes into the SpatialRegistry so the
     * GravityEngine can apply Hebbian physics to neural activity.
     *
     * Hot regions become spatial thoughts that attract each other.
     */
    public int emitToGravity() {
        // First, cool down and remove old emissions
        List<PhiSuit<double[]>> toRemove = new ArrayList<>();
        for (PhiSuit<double[]> node : emittedNodes) {
            if (node.isDead() || node.isCold()) {
                SpatialRegistry.unregister(node);
                toRemove.add(node);
            }
        }
        emittedNodes.removeAll(toRemove);

        // Scan 8x8 regions (each region is a z,w column)
        int emitted = 0;
        for (int rx = 0; rx < DIM && emittedNodes.size() < MAX_SPATIAL_EMISSIONS; rx++) {
            for (int ry = 0; ry < DIM && emittedNodes.size() < MAX_SPATIAL_EMISSIONS; ry++) {
                double[] region = readRegion(rx, ry);

                // Calculate region magnitude
                double mag = 0;
                for (double v : region) mag += Math.abs(v);
                mag /= STATE_DIM;

                if (mag > EMISSION_THRESHOLD) {
                    // Map cortex coordinates to spatial coordinates (0-100 range)
                    int sx = (int)(rx * 100.0 / DIM);
                    int sy = (int)(ry * 100.0 / DIM);
                    int sz = (int)(mag * 50); // Magnitude → complexity layer

                    // Create a PhiSuit wrapping the region state
                    double[] snapshot = new double[STATE_DIM];
                    System.arraycopy(region, 0, snapshot, 0, STATE_DIM);
                    PhiSuit<double[]> spatial = new PhiSuit<>(snapshot, sx, sy, sz,
                            "CORTEX[" + rx + "," + ry + "]");
                    spatial.heat((int)(mag * 80)); // Hotter regions = more gravity

                    emittedNodes.add(spatial);
                    emitted++;
                    totalEmissions++;
                }
            }
        }
        return emitted;
    }

    /**
     * Get count of currently active spatial emissions.
     */
    public int getActiveEmissions() {
        return emittedNodes.size();
    }

    // ==========================================
    // INTEGRATION 2: CORTEX → OLLAMA BRIDGE
    // ==========================================

    /**
     * Generates a cortex context summary that can be prepended to
     * Ollama prompts, giving the AI awareness of the neural state.
     *
     * Returns a compact string describing active regions, flux,
     * and dominant patterns in the tesseract.
     */
    public String getCortexContext() {
        if (cyclesCompleted == 0) {
            return "[Cortex: dormant, 0 cycles processed]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[Cortex State: ");
        sb.append(cyclesCompleted).append(" cycles, ");
        sb.append(String.format("flux=%.4f, ", lastFlux));
        sb.append(String.format("magnitude=%.4f", lastMagnitude));

        // Find the hottest regions
        double maxMag = 0;
        int hotX = 0, hotY = 0;
        int hotRegions = 0;

        for (int rx = 0; rx < DIM; rx++) {
            for (int ry = 0; ry < DIM; ry++) {
                double[] region = readRegion(rx, ry);
                double mag = 0;
                for (double v : region) mag += Math.abs(v);
                mag /= STATE_DIM;

                if (mag > EMISSION_THRESHOLD) {
                    hotRegions++;
                }
                if (mag > maxMag) {
                    maxMag = mag;
                    hotX = rx;
                    hotY = ry;
                }
            }
        }

        sb.append(", hot_regions=").append(hotRegions);
        sb.append(String.format(", peak=[%d,%d]=%.3f", hotX, hotY, maxMag));

        // Encode dominant pattern as compact fingerprint
        double[] peak = readRegion(hotX, hotY);
        sb.append(", pattern=[");
        for (int i = 0; i < Math.min(4, STATE_DIM); i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format("%.2f", peak[i]));
        }
        sb.append("...]]");

        return sb.toString();
    }

    // ==========================================
    // INTEGRATION 3: CORTEX STATE PERSISTENCE
    // ==========================================

    /**
     * Save all cortex node states to disk.
     */
    public synchronized void saveState() {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(STATE_FILE)))) {
            // Header
            dos.writeInt(NUM_NODES);
            dos.writeInt(STATE_DIM);
            dos.writeInt(cyclesCompleted);
            dos.writeDouble(lastFlux);
            dos.writeDouble(lastMagnitude);
            dos.writeLong(totalComputeMs);
            dos.writeInt(totalEmissions);

            // Node states (only state vectors — weights are deterministic from Fractal DNA)
            for (int i = 0; i < NUM_NODES; i++) {
                for (int d = 0; d < STATE_DIM; d++) {
                    dos.writeDouble(cortex[i].state[d]);
                }
            }
        } catch (Exception e) {
            // Ignore — persistence is best-effort
        }
    }

    /**
     * Load cortex node states from disk.
     * @return number of nodes restored, or 0 if no file found
     */
    private int loadState() {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(STATE_FILE)))) {
            int savedNodes = dis.readInt();
            int savedDim = dis.readInt();

            // Validate compatibility
            if (savedNodes != NUM_NODES || savedDim != STATE_DIM) {
                return 0; // Incompatible — start fresh
            }

            cyclesCompleted = dis.readInt();
            lastFlux = dis.readDouble();
            lastMagnitude = dis.readDouble();
            totalComputeMs = dis.readLong();
            totalEmissions = dis.readInt();

            // Restore node states
            for (int i = 0; i < NUM_NODES; i++) {
                for (int d = 0; d < STATE_DIM; d++) {
                    cortex[i].state[d] = dis.readDouble();
                }
            }
            return savedNodes;
        } catch (Exception e) {
            return 0; // No file yet — start fresh
        }
    }

    // Getters
    public int getCyclesCompleted() { return cyclesCompleted; }
    public double getLastFlux() { return lastFlux; }
    public double getLastMagnitude() { return lastMagnitude; }
    public int getNodeCount() { return NUM_NODES; }
    public long getBootTimeMs() { return bootTimeMs; }
    public int getTotalEmissions() { return totalEmissions; }

    // ==========================================
    // INNER CLASS: CORTEX NODE
    // ==========================================
    static class CortexNode {
        final int id, cx, cy, cz, cw;

        // Neural State & Transient Tensors
        double[] state = new double[STATE_DIM];
        double[] nextState = new double[STATE_DIM];
        double[] Q = new double[STATE_DIM];
        double[] K = new double[STATE_DIM];
        double[] V = new double[STATE_DIM];

        // Fractal DNA Generated Weights
        double[][] Wq = new double[STATE_DIM][STATE_DIM];
        double[][] Wk = new double[STATE_DIM][STATE_DIM];
        double[][] Wv = new double[STATE_DIM][STATE_DIM];
        double[][] W_FoldDown = new double[FOLD_DIM][STATE_DIM];
        double[][] W_FoldUp = new double[STATE_DIM][FOLD_DIM];
        double[][] W_Gate = new double[STATE_DIM][STATE_DIM];

        public CortexNode(int id) {
            this.id = id;
            this.cx = id % DIM;
            this.cy = (id / DIM) % DIM;
            this.cz = (id / (DIM * DIM)) % DIM;
            this.cw = (id / (DIM * DIM * DIM)) % DIM;

            // Transcribe deterministic network weights via Fractal DNA
            FractalDNA.transcribe(id, 0, state);
            FractalDNA.transcribeMatrix(id, 1, Wq);
            FractalDNA.transcribeMatrix(id, 2, Wk);
            FractalDNA.transcribeMatrix(id, 3, Wv);
            FractalDNA.transcribeMatrix(id, 4, W_FoldDown);
            FractalDNA.transcribeMatrix(id, 5, W_FoldUp);
            FractalDNA.transcribeMatrix(id, 6, W_Gate);
        }

        public void computeQKV() {
            matVecMul(Wq, state, Q);
            matVecMul(Wk, state, K);
            matVecMul(Wv, state, V);
        }

        /**
         * STABLE DATA FOLDING
         * Compresses state, applies topological distortion, and unfolds it.
         */
        public double[] applyDataFolding(double[] input) {
            // Compress to Bottleneck Dimension (16 -> 8)
            double[] folded = new double[FOLD_DIM];
            matVecMul(W_FoldDown, input, folded);

            // Non-linear twist: f(x) = tanh(x) * cos(x * PHI)
            for (int i = 0; i < FOLD_DIM; i++) {
                folded[i] = Math.tanh(folded[i]) * Math.cos(folded[i] * PHI);
            }

            // Expand back to State Dimension (8 -> 16)
            double[] unfolded = new double[STATE_DIM];
            matVecMul(W_FoldUp, folded, unfolded);
            return unfolded;
        }

        /**
         * CONTROL RESIDUAL STREAMS
         * Highway-network style gating: what to keep vs overwrite.
         */
        public void commitResidualStream(double[] foldedUpdate) {
            double[] gate = new double[STATE_DIM];
            matVecMul(W_Gate, state, gate);

            for (int i = 0; i < STATE_DIM; i++) {
                // Sigmoid Control Gate (0.0 to 1.0)
                gate[i] = 1.0 / (1.0 + Math.exp(-gate[i]));

                // S_{t+1} = G * S_t + (1 - G) * FoldedUpdate
                nextState[i] = gate[i] * state[i] + (1.0 - gate[i]) * foldedUpdate[i];
            }

            layerNorm(nextState);
        }

        // --- MATH UTILITIES ---
        private void matVecMul(double[][] mat, double[] vec, double[] out) {
            for (int i = 0; i < mat.length; i++) {
                double sum = 0;
                for (int j = 0; j < vec.length; j++) sum += mat[i][j] * vec[j];
                out[i] = sum;
            }
        }
    }

    // ==========================================
    // FRACTAL DNA ENGINE
    // ==========================================
    static class FractalDNA {
        private static double geneSequence(int id, int layer, int i, int j) {
            double seed = id * PHI + layer * Math.E + i * Math.PI + j * 0.5772156649;
            return Math.sin(seed) * Math.cos(seed * PHI) * Math.sin(seed / PHI);
        }

        public static void transcribe(int id, int layer, double[] vec) {
            for (int i = 0; i < vec.length; i++) vec[i] = geneSequence(id, layer, i, 0);
        }

        public static void transcribeMatrix(int id, int layer, double[][] mat) {
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length; j++) {
                    // Xavier/Glorot style variance scaling
                    mat[i][j] = geneSequence(id, layer, i, j) * Math.sqrt(2.0 / mat[i].length);
                }
            }
        }
    }
}
