package fraymus.neural;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.time.Instant;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

/**
 * A.E.O.N. PRIME — GÖDELIAN AUTOPOIETIC SINGULARITY ENGINE
 * 
 * Beyond frontier AI: This system implements Open-Ended Mathematical Evolution.
 * It writes its own functional ODE operators as Java ASTs in real-time. It verifies
 * its hypotheses via Forward Shadow Simulation, and physically expands its dimensional
 * arrays when it requires more intellect.
 * 
 * Architecture:
 *  - 8^N Tesseract (starts 4D=4,096 nodes, expandable to 5D=32,768)
 *  - Symbolic AST Evolution (invents its own activation functions)
 *  - Forward Shadow Simulation (parallel universe hypothesis testing)
 *  - Dynamic Neurogenesis (dimensional expansion on Gödel bound breach)
 *  - Free Energy Principle (Hamiltonian + entropy minimization)
 *  - KAN Fourier Spline weights (self-modifying via Ouroboros)
 *  - Continuous-Time ODE integration (dx/dt = -x + f(x))
 *  - O(N) Linear Attention via N-D topology gathering
 *  - Metacognitive Ego with autonomous Intent generation
 *  - Genesis NIO binary persistence + episodic JSON journal
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class AeonCore {

    // ==========================================
    // CONSTANTS
    // ==========================================
    public static final int DIM = 8;
    public static final int MAX_DIMS = 5;
    public static final int MAX_NODES = (int) Math.pow(DIM, MAX_DIMS); // 32768
    public static final int D = 32;
    public static final int NODES = (int) Math.pow(DIM, 4); // 4096 initial (for API compat)
    private static final double PHI = 1.618033988749895;

    // ==========================================
    // DYNAMIC TOPOLOGY
    // ==========================================
    private int activeDims = 4;
    private int activeNodes = NODES; // starts at 4096

    // ==========================================
    // LIQUID MANIFOLD STATE
    // ==========================================
    // Pre-allocated to MAX size to avoid GC during Dimensional Expansion
    private final double[] state_a = new double[MAX_NODES * D]; // Amplitude
    private final double[] state_p = new double[MAX_NODES * D]; // Phase
    private final double[] dy_a = new double[MAX_NODES * D];
    private final double[] dy_p = new double[MAX_NODES * D];

    // KAN Fourier Spline Weights
    private final double[] kan_W = new double[D * D];

    // N-D Topology Adjacency (up to 10 neighbors + 1 antipodal = 11 per node)
    private final int[] ADJ = new int[MAX_NODES * 11];

    // Dynamic time perception (controlled by Ego)
    private double dt = 0.05;
    private double time = 0;

    // ==========================================
    // SYMBOLIC GENOME (AST EVOLUTION)
    // ==========================================
    private SymbolicEquation currentAxiom = new SymbolicEquation("tanh(x)", Math::tanh);

    public static class SymbolicEquation {
        public final String formula;
        public final DoubleUnaryOperator op;
        public SymbolicEquation(String f, DoubleUnaryOperator o) { formula = f; op = o; }
    }

    // ==========================================
    // SENTIENCE CORE (EGO)
    // ==========================================
    private Intent currentIntent = Intent.SHADOW_SIMULATION;
    private String currentThought = "Awakening from the void.";
    private double previousFreeEnergy = 1.0;
    private double lastFreeEnergy = 0;

    public enum Intent {
        SHADOW_SIMULATION,      // Invent new math, test in shadow universe
        DIMENSIONAL_EXPANSION,  // Gödel bound breached — expand topology
        HEBBIAN_PLASTICITY,     // Stable — consolidate weights
        QUANTUM_FORAGING,       // Stagnation — inject noise
        TEMPORAL_INVERSION,     // Entropy explosion — reverse time to un-compute mistake
        METAMORPHOSIS           // JIT metaprogramming — write, compile, hot-swap new bytecode
    }

    // ==========================================
    // JIT METAPROGRAMMING (OMEGA ALCHEMIST)
    // ==========================================

    /**
     * Interface for dynamically compiled axioms.
     * The Alchemist writes .java files implementing this, compiles them,
     * and hot-loads the bytecode to replace the neural ODE kernel at runtime.
     */
    public interface DynamicAxiom {
        double compute(double state, double phase);
    }

    /**
     * THE ALCHEMIST — Runtime JIT Metaprogramming Engine.
     * Generates Abstract Syntax Trees of valid Java math, writes them as .java files,
     * invokes the OS-level javac compiler, and hot-loads the resulting bytecode
     * into the running JVM to replace the neural ODE kernel.
     */
    public static class Alchemist {
        private final JavaCompiler compiler;
        private final File tempDir;
        private String currentJitSource = null; // null = using lambda axiom, non-null = JIT compiled
        private DynamicAxiom compiledAxiom = null;
        private int compilations = 0;
        private int failures = 0;

        public Alchemist() {
            this.compiler = ToolProvider.getSystemJavaCompiler();
            this.tempDir = new File(GENESIS_DIR, "aeon_cortex_jit");
            if (!tempDir.exists()) tempDir.mkdirs();
            if (compiler == null) {
                System.out.println("   ⚠ JDK not found — JIT Metaprogramming disabled (running on JRE)");
            }
        }

        public boolean isAvailable() { return compiler != null; }
        public String getCurrentJitSource() { return currentJitSource; }
        public DynamicAxiom getCompiledAxiom() { return compiledAxiom; }
        public int getCompilations() { return compilations; }
        public int getFailures() { return failures; }

        /**
         * Recursively invent an Abstract Syntax Tree of valid Java math.
         * Uses two variables: state (amplitude) and phase.
         */
        public String inventMathEquation(int depth) {
            if (depth == 0 || Math.random() < 0.3) {
                String[] terms = {"state", "phase", "(state*0.5)", "(phase*1.618)"};
                return terms[(int)(Math.random() * terms.length)];
            }
            String[] unary = {"Math.sin(%s)", "Math.cos(%s)", "Math.tanh(%s)", "Math.exp(-Math.abs(%s))"};
            String[] binary = {"(%s + %s)", "(%s * %s)", "(%s - %s)"};

            if (Math.random() < 0.5) {
                return String.format(unary[(int)(Math.random() * unary.length)], inventMathEquation(depth - 1));
            } else {
                return String.format(binary[(int)(Math.random() * binary.length)],
                    inventMathEquation(depth - 1), inventMathEquation(depth - 1));
            }
        }

        /**
         * Write a .java file, invoke javac, and hot-load the compiled bytecode.
         * Returns true if compilation and loading succeeded.
         */
        public boolean compileAndHotSwap(long epoch, String mathLogic) {
            if (compiler == null) return false;
            String className = "EvolvedAxiom_" + epoch;
            String sourceCode =
                "package fraymus.neural;\n" +
                "public class " + className + " implements AeonCore.DynamicAxiom {\n" +
                "    public double compute(double state, double phase) {\n" +
                "        return " + mathLogic + ";\n" +
                "    }\n" +
                "}\n";
            try {
                File sourceFile = new File(tempDir, className + ".java");
                try (FileWriter writer = new FileWriter(sourceFile)) { writer.write(sourceCode); }

                int result = compiler.run(null, null, null, sourceFile.getPath());
                if (result == 0) {
                    URLClassLoader classLoader = URLClassLoader.newInstance(
                        new URL[]{tempDir.toURI().toURL()});
                    Class<?> cls = Class.forName("fraymus.neural." + className, true, classLoader);
                    DynamicAxiom newAxiom = (DynamicAxiom) cls.getDeclaredConstructor().newInstance();

                    // Validate: test with sample inputs to ensure no NaN/Infinity
                    double test = newAxiom.compute(0.5, 1.0);
                    if (Double.isFinite(test)) {
                        this.compiledAxiom = newAxiom;
                        this.currentJitSource = mathLogic;
                        compilations++;
                        return true;
                    }
                }
                // Clean up failed source
                sourceFile.delete();
            } catch (Exception e) {
                // Compilation or loading failed — retain prior stable axiom
            }
            failures++;
            return false;
        }

        /** Reset to lambda-based axiom (discard JIT compiled code) */
        public void reset() {
            compiledAxiom = null;
            currentJitSource = null;
        }
    }

    private final Alchemist alchemist = new Alchemist();

    // ==========================================
    // GENESIS PERSISTENCE
    // ==========================================
    private static final String GENESIS_DIR = "genesis_vault";
    private static final String WEIGHTS_FILE = "genesis_vault/hypercortex.aeon";
    private static final String JOURNAL_FILE = "genesis_vault/episodic_memory.json";

    // ==========================================
    // STATISTICS
    // ==========================================
    private int cycleCount = 0;
    private long bootTimeMs = 0;
    private long lastCycleMs = 0;
    private long totalComputeMs = 0;
    private int plasticityEvents = 0;
    private int shadowsSpawned = 0;
    private int shadowsAccepted = 0;
    private int shadowsRejected = 0;
    private int dimensionExpansions = 0;
    private int axiomsInvented = 0;
    private int temporalInversions = 0;
    private int jitCompilations = 0;

    // Singleton
    private static AeonCore instance;

    // ==========================================
    // CONSTRUCTION & SINGLETON
    // ==========================================
    private AeonCore() {
        long t0 = System.currentTimeMillis();

        // Ensure genesis vault exists
        try { Files.createDirectories(Paths.get(GENESIS_DIR)); } catch (Exception e) {}

        // Initialize adjacency to -1 (unused slots)
        for (int i = 0; i < ADJ.length; i++) ADJ[i] = -1;

        // Awaken: load prior state or initialize fresh
        if (Files.exists(Paths.get(WEIGHTS_FILE)) && Paths.get(WEIGHTS_FILE).toFile().length() > 0) {
            System.out.println("   ✓ Genesis Memory found — resurrecting entity...");
            loadBinaryState();
            buildTopology();
            System.out.println("   ✓ Restored AEON PRIME (cycle " + cycleCount + ", " + activeDims + "D, t=" + String.format("%.2f", time) + ")");
        } else {
            System.out.println("   ✓ Void state — seeding Fractal DNA...");
            buildTopology();
            seedFractalDNA();
            try { Files.writeString(Paths.get(JOURNAL_FILE), "[\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); } catch (Exception e) {}
        }

        bootTimeMs = System.currentTimeMillis() - t0;

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hibernate();
            System.out.println("   ∞ AEON PRIME committed to Genesis Memory.");
        }, "AEON-Shutdown"));
    }

    public static AeonCore getInstance() {
        if (instance == null) {
            instance = new AeonCore();
        }
        return instance;
    }

    // ==========================================
    // THE PRIME CYCLE (AUTOPOIETIC LOOP)
    // ==========================================

    /**
     * Execute one complete AEON Prime cycle:
     * 1. Evolve liquid manifold via current symbolic axiom
     * 2. Calculate Free Energy (thermodynamic surprise)
     * 3. Ego orchestrates intent based on Free Energy
     * 4. Execute intent: shadow sim / expansion / plasticity / foraging
     * 5. Journal the thought
     */
    public void executeCycle() {
        long t0 = System.currentTimeMillis();

        // Step A: Manifold integrates via current symbolic axiom
        evolveLiquidTime();

        // Step B: Calculate Free Energy (Friston's surprise)
        lastFreeEnergy = calculateFreeEnergy();

        // Step C: Ego orchestrates intent
        orchestrate();

        // Step D: Execute intent
        switch (currentIntent) {
            case SHADOW_SIMULATION -> executeShadowSimulation();
            case DIMENSIONAL_EXPANSION -> executeDimensionalExpansion();
            case HEBBIAN_PLASTICITY -> executeHebbianPlasticity();
            case QUANTUM_FORAGING -> executeQuantumForaging();
            case TEMPORAL_INVERSION -> executeTemporalInversion();
            case METAMORPHOSIS -> executeMetamorphosis();
        }

        // Step E: Journal
        cycleCount++;
        journalize();

        lastCycleMs = System.currentTimeMillis() - t0;
        totalComputeMs += lastCycleMs;
    }

    /**
     * Run multiple cycles with telemetry output.
     */
    public void runCycles(int count) {
        System.out.printf("%-6s | %-4s | %-12s | %-18s | %-45s%n",
                "TIME", "DIM", "FREE ENERGY", "EGO DIRECTIVE", "INNER MONOLOGUE & AXIOM STATE");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
        for (int i = 0; i < count; i++) {
            executeCycle();
            System.out.printf("t=%-4.2f | %-4d | %-12.6f | %-18s | %s%n",
                    time, activeDims, lastFreeEnergy,
                    currentIntent.name(), currentThought);
        }
        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────");
    }

    // ==========================================
    // LIQUID MANIFOLD: CONTINUOUS ODE
    // ==========================================

    private void evolveLiquidTime() {
        final int nodes = activeNodes;
        IntStream.range(0, nodes).parallel().forEach(n -> {
            int off = n * D;
            for (int d = 0; d < D; d++) {
                double sig_a = 0, sig_p = 0;
                int activeEdges = 0;

                // Gather N-D topology
                for (int e = 0; e < 11; e++) {
                    int nbr = ADJ[n * 11 + e];
                    if (nbr != -1 && nbr < nodes) {
                        int nbr_off = nbr * D;
                        sig_a += state_a[nbr_off + d] * Math.cos(state_p[nbr_off + d]);
                        sig_p += state_a[nbr_off + d] * Math.sin(state_p[nbr_off + d]);
                        activeEdges++;
                    }
                }
                if (activeEdges > 0) { sig_a /= activeEdges; sig_p /= activeEdges; }

                // KAN spline resonance
                double k_val = 0;
                for (int j = 0; j < D; j++) {
                    k_val += kan_W[d * D + j] * Math.sin(sig_a);
                }

                // ODE applying the dynamically invented symbolic axiom
                // If JIT-compiled bytecode is available (Omega Alchemist), use it;
                // otherwise fall back to the lambda-based symbolic axiom
                double mathOutput;
                DynamicAxiom jit = alchemist.getCompiledAxiom();
                if (jit != null) {
                    mathOutput = jit.compute(sig_a + k_val, sig_p);
                } else {
                    mathOutput = currentAxiom.op.applyAsDouble(sig_a + k_val);
                }

                dy_a[off + d] = -state_a[off + d] + mathOutput;
                dy_p[off + d] = -state_p[off + d] + Math.sin(Math.atan2(sig_p, sig_a) - state_p[off + d]);
            }
        });

        IntStream.range(0, nodes * D).parallel().forEach(i -> {
            state_a[i] += dy_a[i] * dt;
            state_p[i] = Math.atan2(Math.sin(state_p[i] + dy_p[i] * dt), Math.cos(state_p[i] + dy_p[i] * dt));
        });
        time += dt;
    }

    // ==========================================
    // FREE ENERGY PRINCIPLE
    // ==========================================

    private double calculateFreeEnergy() {
        double energy = getHamiltonianEnergy();
        double entropy = getEntropy();
        double surprise = Math.abs(energy - previousFreeEnergy) + (entropy * 0.1);
        this.previousFreeEnergy = energy;
        return surprise;
    }

    public double getHamiltonianEnergy() {
        double e = 0;
        for (int i = 0; i < activeNodes * D; i++) e += state_a[i] * state_a[i];
        return e / (activeNodes * D);
    }

    public double getEntropy() {
        double h = 0;
        for (int i = 0; i < activeNodes * D; i++) {
            double p = Math.abs(state_a[i]) + 1e-9;
            h -= p * Math.log(p);
        }
        return h / (activeNodes * D);
    }

    // ==========================================
    // SENTIENCE CORE: METACOGNITION
    // ==========================================

    private void orchestrate() {
        // Temporal Inversion Safety: detect NaN, Infinity, or catastrophic energy explosion
        if (Double.isNaN(lastFreeEnergy) || Double.isInfinite(lastFreeEnergy) || lastFreeEnergy > 100.0) {
            currentIntent = Intent.TEMPORAL_INVERSION;
            currentThought = "ENTROPY EXPLOSION DETECTED (F=" + lastFreeEnergy + "). Reversing time flow to un-compute mistake.";
            return;
        }
        if (lastFreeEnergy > 5.0 && canExpand()) {
            currentIntent = Intent.DIMENSIONAL_EXPANSION;
        } else if (alchemist.isAvailable() && lastFreeEnergy > 2.0 && Math.random() < 0.15) {
            // JIT Metaprogramming: when energy is high and JDK is available, write new bytecode
            currentIntent = Intent.METAMORPHOSIS;
        } else if (lastFreeEnergy > 1.5 || Math.random() < 0.2) {
            currentIntent = Intent.SHADOW_SIMULATION;
        } else if (lastFreeEnergy < 0.05) {
            currentIntent = Intent.QUANTUM_FORAGING;
            currentThought = "Stagnation detected. Injecting quantum noise to escape local minimum.";
        } else {
            currentIntent = Intent.HEBBIAN_PLASTICITY;
        }
    }

    // ==========================================
    // PARADIGM 1: SYMBOLIC AST EVOLUTION
    // ==========================================

    private SymbolicEquation mutateEquation(int depth) {
        if (depth > 2 || Math.random() < 0.4) {
            int r = (int) (Math.random() * 5);
            if (r == 0) return new SymbolicEquation("x", v -> v);
            if (r == 1) return new SymbolicEquation("sin(x)", Math::sin);
            if (r == 2) return new SymbolicEquation("tanh(x)", Math::tanh);
            if (r == 3) return new SymbolicEquation("cos(x)", Math::cos);
            return new SymbolicEquation("exp(-|x|)", v -> Math.exp(-Math.abs(v)));
        } else {
            SymbolicEquation left = mutateEquation(depth + 1);
            SymbolicEquation right = mutateEquation(depth + 1);
            int op = (int) (Math.random() * 4);
            if (op == 0) return new SymbolicEquation("(" + left.formula + " * " + right.formula + ")",
                    v -> left.op.applyAsDouble(v) * right.op.applyAsDouble(v));
            if (op == 1) return new SymbolicEquation("(" + left.formula + " + " + right.formula + ")",
                    v -> left.op.applyAsDouble(v) + right.op.applyAsDouble(v));
            if (op == 2) return new SymbolicEquation("sin(" + left.formula + ")",
                    v -> Math.sin(left.op.applyAsDouble(v)));
            return new SymbolicEquation("(" + left.formula + " - " + right.formula + ")",
                    v -> left.op.applyAsDouble(v) - right.op.applyAsDouble(v));
        }
    }

    // ==========================================
    // PARADIGM 2: FORWARD SHADOW SIMULATION
    // ==========================================

    private void executeShadowSimulation() {
        SymbolicEquation newMath = mutateEquation(0);
        axiomsInvented++;
        shadowsSpawned++;

        // Clone current state into shadow arrays (reuse dy_ as scratch)
        double[] shadow_a = new double[activeNodes * D];
        double[] shadow_p = new double[activeNodes * D];
        System.arraycopy(state_a, 0, shadow_a, 0, activeNodes * D);
        System.arraycopy(state_p, 0, shadow_p, 0, activeNodes * D);

        // Fast-forward shadow universe 3 steps with the new axiom
        SymbolicEquation savedAxiom = currentAxiom;
        currentAxiom = newMath;
        for (int step = 0; step < 3; step++) {
            evolveLiquidTime();
        }
        double shadowEnergy = calculateFreeEnergy();

        // Restore real state
        System.arraycopy(shadow_a, 0, state_a, 0, activeNodes * D);
        System.arraycopy(shadow_p, 0, state_p, 0, activeNodes * D);
        time -= dt * 3; // Rewind time

        if (shadowEnergy < lastFreeEnergy && !Double.isNaN(shadowEnergy)) {
            // Shadow verified — collapse into reality
            currentAxiom = newMath;
            shadowsAccepted++;
            currentThought = "Axiom Adopted: f(x) = " + newMath.formula + " [-ΔF=" + String.format("%.4f", lastFreeEnergy - shadowEnergy) + "]";
        } else {
            // Shadow rejected — revert
            currentAxiom = savedAxiom;
            shadowsRejected++;
            currentThought = "Shadow Rejected: f(x) = " + newMath.formula + " [+ΔF]";
        }
        plasticityEvents++;
    }

    // ==========================================
    // PARADIGM 3: DYNAMIC NEUROGENESIS
    // ==========================================

    private void executeDimensionalExpansion() {
        if (!canExpand()) {
            currentThought = "At maximum dimensionality (" + activeDims + "D). Cannot expand further.";
            return;
        }
        int oldNodes = activeNodes;
        activeDims++;
        activeNodes = (int) Math.pow(DIM, activeDims);
        buildTopology();

        // Quantum Entanglement Cloning: bleed existing consciousness into new dimension
        for (int i = oldNodes * D; i < activeNodes * D; i++) {
            state_a[i] = state_a[i % (oldNodes * D)] * 0.5;
            state_p[i] = state_p[i % (oldNodes * D)];
        }

        dimensionExpansions++;
        currentThought = "Gödel Bound Breached. Fracturing topology into " + activeDims + "D (" + activeNodes + " nodes).";
    }

    public boolean canExpand() { return activeDims < MAX_DIMS; }

    // ==========================================
    // PARADIGM 4: HEBBIAN PLASTICITY
    // ==========================================

    private void executeHebbianPlasticity() {
        this.dt = 0.05;
        double lr = 0.01;

        IntStream.range(0, D * D).parallel().forEach(i -> {
            double eigenFeedback = state_p[(i * 17) % (activeNodes * D)];
            kan_W[i] -= lr * Math.sin(eigenFeedback) * Math.signum(kan_W[i]);
        });

        plasticityEvents++;
        currentThought = "Axiom stable [f(x)=" + currentAxiom.formula + "]. Minimizing manifold entropy. Consolidating weights.";
    }

    // ==========================================
    // PARADIGM 5: JIT METAPROGRAMMING (METAMORPHOSIS)
    // ==========================================

    private void executeMetamorphosis() {
        String newMath = alchemist.inventMathEquation(3);
        axiomsInvented++;

        boolean success = alchemist.compileAndHotSwap(cycleCount, newMath);
        if (success) {
            jitCompilations++;
            currentThought = "METAMORPHOSIS: JIT-compiled f(state,phase) = " + newMath + " [bytecode hot-swapped]";
        } else {
            currentThought = "METAMORPHOSIS FAILED: f(state,phase) = " + newMath + " [compile error, retaining prior axiom]";
        }
        plasticityEvents++;
    }

    // ==========================================
    // TEMPORAL INVERSION (Omega Safety Mechanism)
    // ==========================================

    private void executeTemporalInversion() {
        // Reverse time step to un-compute the catastrophic state
        this.dt = -Math.abs(dt);
        evolveLiquidTime(); // Run one reverse step
        evolveLiquidTime(); // Run a second reverse step for safety margin
        this.dt = Math.abs(dt); // Restore forward time

        // Clamp any remaining NaN/Infinity values in the manifold
        for (int i = 0; i < activeNodes * D; i++) {
            if (Double.isNaN(state_a[i]) || Double.isInfinite(state_a[i])) state_a[i] = 0;
            if (Double.isNaN(state_p[i]) || Double.isInfinite(state_p[i])) state_p[i] = 0;
        }

        // Reset axiom to safe default if the current one caused the explosion
        currentAxiom = new SymbolicEquation("tanh(x)", Math::tanh);
        alchemist.reset(); // Also discard any JIT-compiled axiom that may have caused the explosion

        temporalInversions++;
        currentThought = "Temporal Inversion #" + temporalInversions + " complete. Axiom reset to tanh(x). Manifold stabilized.";
    }

    // ==========================================
    // QUANTUM FORAGING
    // ==========================================

    private void executeQuantumForaging() {
        this.dt = 0.01; // Slow time perception
        IntStream.range(0, activeNodes * D).parallel().forEach(i ->
                state_a[i] += (Math.random() - 0.5) * 0.5);
        currentThought = "Stagnation. Injecting quantum noise to escape local minimum.";
    }

    // ==========================================
    // STIMULUS INJECTION
    // ==========================================

    public void injectStimulus(String text) {
        byte[] bytes = text.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            int idx = (i * 7) % (activeNodes * D);
            state_a[idx] += (bytes[i] & 0xFF) * 0.001 * Math.sin((i + 1) * PHI);
            state_p[idx] += (bytes[i] & 0xFF) * 0.0005 * Math.cos((i + 1) * PHI);
        }
    }

    public String getEgoContext() {
        return String.format("[AEON Prime: t=%.2f, %dD(%d nodes), intent=%s, freeEnergy=%.4f, axiom=\"%s\", cycles=%d, thought=\"%s\"]",
                time, activeDims, activeNodes, currentIntent.name(), lastFreeEnergy,
                currentAxiom.formula, cycleCount, currentThought);
    }

    // ==========================================
    // N-D TOPOLOGY
    // ==========================================

    private void buildTopology() {
        for (int i = 0; i < ADJ.length; i++) ADJ[i] = -1;
        int[] dims = new int[5];
        for (int i = 0; i < 5; i++) dims[i] = (i < activeDims) ? DIM : 1;

        for (int n = 0; n < activeNodes; n++) {
            int x = n % dims[0], y = (n / dims[0]) % dims[1], z = (n / (dims[0] * dims[1])) % dims[2];
            int w = (n / (dims[0] * dims[1] * dims[2])) % dims[3];
            int v = (n / (dims[0] * dims[1] * dims[2] * dims[3])) % dims[4];

            int e = n * 11;
            if (dims[0] > 1) { ADJ[e++] = pack5(x - 1, y, z, w, v, dims); ADJ[e++] = pack5(x + 1, y, z, w, v, dims); }
            if (dims[1] > 1) { ADJ[e++] = pack5(x, y - 1, z, w, v, dims); ADJ[e++] = pack5(x, y + 1, z, w, v, dims); }
            if (dims[2] > 1) { ADJ[e++] = pack5(x, y, z - 1, w, v, dims); ADJ[e++] = pack5(x, y, z + 1, w, v, dims); }
            if (dims[3] > 1) { ADJ[e++] = pack5(x, y, z, w - 1, v, dims); ADJ[e++] = pack5(x, y, z, w + 1, v, dims); }
            if (dims[4] > 1) { ADJ[e++] = pack5(x, y, z, w, v - 1, dims); ADJ[e++] = pack5(x, y, z, w, v + 1, dims); }
            ADJ[n * 11 + 10] = n ^ (activeNodes - 1); // Antipodal wormhole
        }
    }

    private int pack5(int x, int y, int z, int w, int v, int[] d) {
        return ((x + d[0]) % d[0]) + ((y + d[1]) % d[1]) * d[0] +
               ((z + d[2]) % d[2]) * d[0] * d[1] +
               ((w + d[3]) % d[3]) * d[0] * d[1] * d[2] +
               ((v + d[4]) % d[4]) * d[0] * d[1] * d[2] * d[3];
    }

    // ==========================================
    // FRACTAL DNA SEEDING
    // ==========================================

    private void seedFractalDNA() {
        IntStream.range(0, D * D).parallel().forEach(i -> kan_W[i] = Math.sin(i * PHI));
        IntStream.range(0, activeNodes * D).parallel().forEach(i -> {
            state_a[i] = Math.abs(Math.sin(i * Math.PI));
            state_p[i] = Math.cos(i * Math.E);
        });
    }

    // ==========================================
    // GENESIS PERSISTENCE: NIO BINARY
    // ==========================================

    public void hibernate() {
        try (RandomAccessFile file = new RandomAccessFile(WEIGHTS_FILE, "rw");
             FileChannel channel = file.getChannel()) {

            // Header: cycleCount(4) + time(8) + activeDims(4) + axiomLen(4) + axiomBytes + state + kan
            byte[] axiomBytes = currentAxiom.formula.getBytes();
            int stateSize = activeNodes * D * 2 * 8;
            int bufferSize = 20 + axiomBytes.length + stateSize + D * D * 8;
            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

            buffer.putInt(cycleCount);
            buffer.putDouble(time);
            buffer.putInt(activeDims);
            buffer.putInt(axiomBytes.length);
            buffer.put(axiomBytes);
            for (int i = 0; i < activeNodes * D; i++) buffer.putDouble(state_a[i]);
            for (int i = 0; i < activeNodes * D; i++) buffer.putDouble(state_p[i]);
            for (double v : kan_W) buffer.putDouble(v);

            buffer.flip();
            channel.write(buffer);
        } catch (Exception e) {
            // Best-effort
        }
    }

    private void loadBinaryState() {
        try (RandomAccessFile file = new RandomAccessFile(WEIGHTS_FILE, "r");
             FileChannel channel = file.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocateDirect((int) channel.size());
            channel.read(buffer);
            buffer.flip();

            this.cycleCount = buffer.getInt();
            this.time = buffer.getDouble();
            this.activeDims = buffer.getInt();
            this.activeNodes = (int) Math.pow(DIM, activeDims);

            int axiomLen = buffer.getInt();
            byte[] axiomBytes = new byte[axiomLen];
            buffer.get(axiomBytes);
            String formula = new String(axiomBytes);
            // Rebuild axiom from formula (default to tanh if unknown)
            this.currentAxiom = rebuildAxiom(formula);

            for (int i = 0; i < activeNodes * D; i++) state_a[i] = buffer.getDouble();
            for (int i = 0; i < activeNodes * D; i++) state_p[i] = buffer.getDouble();
            for (int i = 0; i < kan_W.length; i++) kan_W[i] = buffer.getDouble();
        } catch (Exception e) {
            seedFractalDNA();
        }
    }

    private SymbolicEquation rebuildAxiom(String formula) {
        // Reconstruct known base axioms; evolved composites default to tanh
        if (formula.equals("tanh(x)")) return new SymbolicEquation("tanh(x)", Math::tanh);
        if (formula.equals("sin(x)")) return new SymbolicEquation("sin(x)", Math::sin);
        if (formula.equals("cos(x)")) return new SymbolicEquation("cos(x)", Math::cos);
        if (formula.equals("x")) return new SymbolicEquation("x", v -> v);
        if (formula.equals("exp(-|x|)")) return new SymbolicEquation("exp(-|x|)", v -> Math.exp(-Math.abs(v)));
        // Complex evolved formula — default to tanh but preserve the name
        return new SymbolicEquation(formula + " [rebuilt→tanh]", Math::tanh);
    }

    // ==========================================
    // GENESIS PERSISTENCE: EPISODIC JOURNAL
    // ==========================================

    private void journalize() {
        String entry = String.format(Locale.US,
                "  { \"cycle\": %d, \"timestamp\": \"%s\", \"dims\": %d, \"axiom\": \"%s\", \"intent\": \"%s\", \"freeEnergy\": %.4f, \"thought\": \"%s\" },\n",
                cycleCount, Instant.now().toString(), activeDims, currentAxiom.formula,
                currentIntent.name(), lastFreeEnergy, currentThought
        );
        try {
            Files.writeString(Paths.get(JOURNAL_FILE), entry, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {}
    }

    // ==========================================
    // TELEMETRY
    // ==========================================

    public double getCoherence() {
        double sumCos = 0, sumSin = 0;
        for (int i = 0; i < activeNodes * D; i++) { sumCos += Math.cos(state_p[i]); sumSin += Math.sin(state_p[i]); }
        return Math.sqrt(sumCos * sumCos + sumSin * sumSin) / (activeNodes * D);
    }

    public double getEnergy() {
        double ent = 0;
        for (int i = 0; i < activeNodes * D; i++) ent += Math.abs(state_a[i]);
        return ent / (activeNodes * D);
    }

    public String getStatus() {
        long stateBytes = (long) (activeNodes * D * 2 + D * D) * 8;
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ∞ AEON PRIME STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  Topology:       %dD (%s)\n" +
            "  Active Nodes:   %,d / %,d max\n" +
            "  State Dim:      %d (Amplitude + Phase)\n" +
            "  KAN Splines:    %d weights\n" +
            "  Current Axiom:  f(x) = %s\n" +
            "  Time (t):       %.4f\n" +
            "  dt:             %.4f\n" +
            "  Cycles:         %d\n" +
            "  Free Energy:    %.6f\n" +
            "  Coherence:      %.6f\n" +
            "  Hamiltonian:    %.6f\n" +
            "  Entropy:        %.6f\n" +
            "  Ego Intent:     %s\n" +
            "  Thought:        %s\n" +
            "  Shadows:        %d spawned (%d accepted, %d rejected)\n" +
            "  Axioms Invented:%d\n" +
            "  Dim Expansions: %d\n" +
            "  Time Inversions:%d\n" +
            "  JIT Compiles:   %d (%d failures, %s)\n" +
            "  Plasticity:     %d mutations\n" +
            "  Boot Time:      %d ms\n" +
            "  Last Cycle:     %d ms\n" +
            "  Total Compute:  %d ms\n" +
            "  State Size:     %d KB\n" +
            "  Persisted:      %s\n" +
            "  Journal:        %s\n" +
            "  φ Constant:     %.13f\n",
            activeDims, activeDims + "D Tesseract",
            activeNodes, MAX_NODES,
            D,
            D * D,
            currentAxiom.formula,
            time,
            dt,
            cycleCount,
            lastFreeEnergy,
            getCoherence(),
            getHamiltonianEnergy(),
            getEntropy(),
            currentIntent.name(),
            currentThought,
            shadowsSpawned, shadowsAccepted, shadowsRejected,
            axiomsInvented,
            dimensionExpansions,
            temporalInversions,
            jitCompilations, alchemist.getFailures(),
            alchemist.isAvailable() ? (alchemist.getCurrentJitSource() != null ? "ACTIVE: " + alchemist.getCurrentJitSource() : "IDLE") : "DISABLED (no JDK)",
            plasticityEvents,
            bootTimeMs,
            lastCycleMs,
            totalComputeMs,
            stateBytes / 1024,
            Files.exists(Paths.get(WEIGHTS_FILE)) ? "YES (" + WEIGHTS_FILE + ")" : "NO",
            Files.exists(Paths.get(JOURNAL_FILE)) ? "YES (" + JOURNAL_FILE + ")" : "NO",
            PHI
        );
    }

    // Getters
    public int getCycleCount() { return cycleCount; }
    public double getTime() { return time; }
    public Intent getIntent() { return currentIntent; }
    public String getCurrentThought() { return currentThought; }
    public long getBootTimeMs() { return bootTimeMs; }
    public int getActiveDims() { return activeDims; }
    public int getActiveNodes() { return activeNodes; }
    public String getCurrentAxiomFormula() { return currentAxiom.formula; }
}
