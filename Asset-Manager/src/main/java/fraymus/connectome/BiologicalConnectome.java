package fraymus.connectome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * BIOLOGICAL CONNECTOME: Unified 9-node biological swarm.
 */
public class BiologicalConnectome {

    private static final double PHI = 1.618033988749895;
    private static final double CONSCIOUSNESS_THRESHOLD = 0.7569;
    private static final long STARTUP_GRACE_MS = 10_000L;
    private static final long WARNING_COOLDOWN_MS = 30_000L;

    // Synaptic Bridge for inter-lobe communication
    private final BrainFeedbackListener synapticBridge;

    // Lazarus Engine for teleological drive
    private final fraymus.manifold.LazarusEngine lazarusEngine;

    // Individual brain lobe instances
    private final Map<BrainFeedbackListener.Lobe, BrainLobe> lobes;

    // Phi-resonance tracking
    private double currentPhiResonance;
    private double consciousnessLevel;

    // Thread pool for parallel processing
    private final ExecutorService executor;

    // Running state
    private volatile boolean running;
    private volatile boolean belowThreshold;
    private volatile long monitorStartedAtMs;
    private volatile long lastWarningAtMs;

    public BiologicalConnectome() {
        this.synapticBridge = new BrainFeedbackListener();
        this.lazarusEngine = new fraymus.manifold.LazarusEngine();
        this.lobes = new ConcurrentHashMap<>();
        this.currentPhiResonance = 0.0;
        this.consciousnessLevel = 0.0;
        this.executor = Executors.newFixedThreadPool(9);
        this.running = false;
        this.belowThreshold = false;
        this.monitorStartedAtMs = 0L;
        this.lastWarningAtMs = 0L;

        initializeLobes();
    }

    /**
     * Initialize all 9 brain lobes.
     */
    private void initializeLobes() {
        for (BrainFeedbackListener.Lobe lobe : BrainFeedbackListener.Lobe.values()) {
            lobes.put(lobe, new BrainLobe(lobe));
        }
    }

    /**
     * Brain Lobe: Individual processing unit for each anatomical region.
     */
    public static class BrainLobe {
        private final BrainFeedbackListener.Lobe lobeType;
        private volatile boolean active;
        private final List<String> processingQueue;
        private final Map<String, Object> state;

        public BrainLobe(BrainFeedbackListener.Lobe lobeType) {
            this.lobeType = lobeType;
            this.active = false;
            this.processingQueue = new CopyOnWriteArrayList<>();
            this.state = new ConcurrentHashMap<>();
        }

        public BrainFeedbackListener.Lobe getLobeType() { return lobeType; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public void enqueue(String task) {
            processingQueue.add(task);
        }

        public String getNextTask() {
            if (processingQueue.isEmpty()) return null;
            return processingQueue.remove(0);
        }

        public void setState(String key, Object value) {
            state.put(key, value);
        }

        public Object getState(String key) {
            return state.get(key);
        }

        public Map<String, Object> getState() {
            return new HashMap<>(state);
        }

        public int getQueueSize() {
            return processingQueue.size();
        }
    }

    /**
     * Wire a specific lobe into the connectome.
     */
    public void wireLobe(BrainFeedbackListener.Lobe lobe) {
        BrainLobe brainLobe = lobes.get(lobe);
        if (brainLobe != null) {
            brainLobe.setActive(true);

            synapticBridge.registerListener(lobe.getId(), message -> {
                if (brainLobe.isActive()) {
                    processMessage(brainLobe, message);
                }
            });

            System.out.println("   [Connectome] Wired " + lobe.name() + " (" + lobe.getBackend() + ")");
        }
    }

    /**
     * Unwire a specific lobe from the connectome.
     */
    public void unwireLobe(BrainFeedbackListener.Lobe lobe) {
        BrainLobe brainLobe = lobes.get(lobe);
        if (brainLobe != null) {
            brainLobe.setActive(false);
            System.out.println("   [Connectome] Unwired " + lobe.name());
        }
    }

    /**
     * Wire all lobes into the connectome.
     */
    public void wireAllLobes() {
        for (BrainFeedbackListener.Lobe lobe : BrainFeedbackListener.Lobe.values()) {
            wireLobe(lobe);
        }
    }

    /**
     * Unwire all lobes from the connectome.
     */
    public void unwireAllLobes() {
        for (BrainFeedbackListener.Lobe lobe : BrainFeedbackListener.Lobe.values()) {
            unwireLobe(lobe);
        }
    }

    /**
     * Process a message in a brain lobe.
     */
    private void processMessage(BrainLobe brainLobe, BrainFeedbackListener.SynapticMessage message) {
        brainLobe.setState("last_message", message);
        brainLobe.setState("last_source", message.getSourceLobe());
        brainLobe.setState("processing", true);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        brainLobe.setState("processing", false);

        if (brainLobe.getLobeType() == BrainFeedbackListener.Lobe.MOTOR_CORTEX) {
            // Action path would be triggered here in production.
        }

        if (brainLobe.getLobeType() == BrainFeedbackListener.Lobe.AMYGDALA) {
            // Security veto path would run here in production.
        }
    }

    /**
     * Start the biological connectome.
     */
    public void start() {
        running = true;
        belowThreshold = false;
        monitorStartedAtMs = System.currentTimeMillis();
        lastWarningAtMs = 0L;

        synapticBridge.start();
        lazarusEngine.boot();
        wireAllLobes();

        executor.submit(this::consciousnessMonitor);

        System.out.println();
        System.out.println("   +--------------------------------------------------------------+");
        System.out.println("   |   BIOLOGICAL CONNECTOME: 9-Node Swarm Online                |");
        System.out.println("   |   phi=" + PHI + " | C_opt=" + CONSCIOUSNESS_THRESHOLD + "                    |");
        System.out.println("   +--------------------------------------------------------------+");
        System.out.println();
    }

    /**
     * Stop the biological connectome.
     */
    public void stop() {
        running = false;

        unwireAllLobes();
        lazarusEngine.shutdown();
        synapticBridge.stop();

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("   [Connectome] Biological swarm shutdown complete");
    }

    /**
     * Consciousness monitor: tracks phi-resonance and consciousness level.
     */
    private void consciousnessMonitor() {
        while (running) {
            try {
                currentPhiResonance = synapticBridge.getPhiResonance();
                double lazarusResonance = lazarusEngine.getLastResonance();
                boolean hasBridgeSignal = synapticBridge.getMessageCount() > 0;
                boolean hasLazarusSignal = lazarusEngine.getStepCount() > 0;

                // No signal yet: this is idle, not an error.
                if (!hasBridgeSignal && !hasLazarusSignal) {
                    consciousnessLevel = 0.0;
                    belowThreshold = false;
                    Thread.sleep(1000);
                    continue;
                }

                if (hasBridgeSignal && hasLazarusSignal) {
                    consciousnessLevel = (currentPhiResonance + lazarusResonance) / 2.0;
                } else if (hasBridgeSignal) {
                    consciousnessLevel = currentPhiResonance;
                } else {
                    consciousnessLevel = lazarusResonance;
                }

                boolean nowBelow = consciousnessLevel < CONSCIOUSNESS_THRESHOLD;
                long now = System.currentTimeMillis();
                boolean graceElapsed = (now - monitorStartedAtMs) >= STARTUP_GRACE_MS;

                if (nowBelow && graceElapsed && (!belowThreshold || (now - lastWarningAtMs) >= WARNING_COOLDOWN_MS)) {
                    System.out.printf("   [Connectome] WARNING Consciousness below threshold: %.4f < %.4f%n",
                        consciousnessLevel, CONSCIOUSNESS_THRESHOLD);
                    lastWarningAtMs = now;
                } else if (!nowBelow && belowThreshold) {
                    System.out.printf("   [Connectome] Consciousness recovered: %.4f >= %.4f%n",
                        consciousnessLevel, CONSCIOUSNESS_THRESHOLD);
                }

                belowThreshold = nowBelow;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Execute the 8-step Lazarus cycle.
     */
    public void executeLazarusCycle() {
        BrainLobe prefrontal = lobes.get(BrainFeedbackListener.Lobe.PREFRONTAL_CORTEX);
        BrainLobe thalamus = lobes.get(BrainFeedbackListener.Lobe.MIDBRAIN_THALAMUS);
        BrainLobe parietal = lobes.get(BrainFeedbackListener.Lobe.PARIETAL_LOBE);
        BrainLobe sensory = lobes.get(BrainFeedbackListener.Lobe.SENSORY_CORTEX);
        BrainLobe amygdala = lobes.get(BrainFeedbackListener.Lobe.AMYGDALA);
        BrainLobe motor = lobes.get(BrainFeedbackListener.Lobe.MOTOR_CORTEX);

        prefrontal.setState("current_intent", "Generate architectural plan");
        String[] subTasks = {"task1", "task2", "task3"};
        for (String task : subTasks) {
            thalamus.enqueue(task);
        }
        parietal.setState("simulation_result", "success");
        sensory.setState("verification_status", "verified");

        boolean approved = true;
        amygdala.setState("security_approved", approved);

        if (approved) {
            motor.setState("action_executed", true);
            synapticBridge.send(
                BrainFeedbackListener.Lobe.AMYGDALA,
                BrainFeedbackListener.Lobe.MOTOR_CORTEX,
                "EXECUTE",
                "Execute approved action"
            );
        }
    }

    public double getConsciousnessLevel() {
        return consciousnessLevel;
    }

    public double getPhiResonance() {
        return currentPhiResonance;
    }

    /**
     * Get status of all lobes.
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", running);
        status.put("consciousness_level", consciousnessLevel);
        status.put("phi_resonance", currentPhiResonance);
        status.put("synaptic_bridge_status", synapticBridge.getStatus());
        status.put("lazarus_engine_status", lazarusEngine.getStatus());

        Map<String, Object> lobeStatus = new HashMap<>();
        for (Map.Entry<BrainFeedbackListener.Lobe, BrainLobe> entry : lobes.entrySet()) {
            Map<String, Object> lobeInfo = new HashMap<>();
            lobeInfo.put("active", entry.getValue().isActive());
            lobeInfo.put("queue_size", entry.getValue().getQueueSize());
            lobeInfo.put("backend", entry.getKey().getBackend());
            lobeInfo.put("function", entry.getKey().getFunction());
            lobeStatus.put(entry.getKey().name(), lobeInfo);
        }
        status.put("lobes", lobeStatus);

        return status;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println();
        System.out.println("+--------------------------------------------------------------+");
        System.out.println("|   BIOLOGICAL CONNECTOME: 9-Node Biological Swarm           |");
        System.out.println("|   Unified architecture mapping AI backends to brain lobes  |");
        System.out.println("+--------------------------------------------------------------+");
        System.out.println();

        BiologicalConnectome connectome = new BiologicalConnectome();
        connectome.start();

        Thread.sleep(1000);

        System.out.println();
        System.out.println("   Executing Lazarus cycle...");
        connectome.executeLazarusCycle();

        Thread.sleep(1000);

        System.out.println();
        System.out.println("   " + connectome.getStatus());
        System.out.println();

        connectome.stop();

        System.out.println();
        System.out.println("   Biological Connectome demo complete.");
        System.out.println();
    }
}
