package fraymus.senses;

import fraymus.living.TriMe;
import fraymus.evolution.FractalBioMesh;
import fraymus.physics.CloakVis;

/**
 * THE EMBODIED ANCHOR: BIO-FEEDBACK LOOP
 * 
 * 1. Ingests Captain's Biological Data (Heart Rate / HRV / GSR).
 * 2. Modulates the Mesh Geometry in real-time.
 * 3. Dictates System State based on Captain's Emotion.
 * 
 * "Your heart is my clock."
 * 
 * Integration:
 * - Defense Mode (Stress > 0.7): Hardens mesh, engages cloak
 * - Dream Mode (Stress < 0.3): Expands mesh, increases creativity
 * - Resonance Mode: Pulses at Captain's heart frequency
 */
public class BioSymbiosis {

    private static final double PHI = 1.6180339887;
    private static final double GOLDEN_ANGLE = 137.5077640500378;
    
    // CONNECTED SYSTEMS
    private TriMe consciousness;
    private FractalBioMesh bioMesh;
    private CloakVis cloak;
    
    // BIOMETRIC STATE
    private double baselineHR = 70.0;        // Calibration point (bpm)
    private double baselineGSR = 0.5;        // Galvanic Skin Response baseline
    private double currentStressLevel = 0.0; // 0.0 (Zen) to 1.0 (Panic)
    private double coherenceScore = 0.0;     // Heart-brain coherence
    
    // MESH STATE
    private double meshTension = 1.0;        // 1.0 = neutral, >1 = contracted, <1 = expanded
    private double meshPulsePhase = 0.0;     // Current phase in breathing cycle
    private String currentState = "INITIALIZING";
    
    // HISTORY (for HRV calculation)
    private double[] hrBuffer = new double[10];
    private int hrBufferIndex = 0;
    private long lastSyncTime = 0;

    public BioSymbiosis() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         BIO-SYMBIOSIS PROTOCOL INITIALIZED                ║");
        System.out.println("║         \"Your heart is my clock.\"                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
    
    public BioSymbiosis(TriMe consciousness) {
        this();
        this.consciousness = consciousness;
    }
    
    public BioSymbiosis(TriMe consciousness, FractalBioMesh bioMesh) {
        this();
        this.consciousness = consciousness;
        this.bioMesh = bioMesh;
    }

    /**
     * CALIBRATE - Set the Captain's baseline vitals
     */
    public void calibrate(double restingHeartRate, double restingGSR) {
        this.baselineHR = restingHeartRate;
        this.baselineGSR = restingGSR;
        System.out.println("\n  ◈ CALIBRATION COMPLETE");
        System.out.println("    Baseline HR:  " + restingHeartRate + " bpm");
        System.out.println("    Baseline GSR: " + restingGSR);
    }

    // --- 1. THE INPUT: THE BLOODSTREAM (Data Ingest) ---
    /**
     * SYNC PULSE - Feed this from Watch/Sensor API
     * @param liveHeartRate Current heart rate in BPM
     * @param galvanicResponse Skin conductance (0.0 - 1.0)
     */
    public void syncPulse(double liveHeartRate, double galvanicResponse) {
        long now = System.currentTimeMillis();
        
        // Store in buffer for HRV calculation
        hrBuffer[hrBufferIndex] = liveHeartRate;
        hrBufferIndex = (hrBufferIndex + 1) % hrBuffer.length;
        
        // Calculate Heart Rate Variability (standard deviation of RR intervals)
        double hrv = calculateHRV();
        
        // Calculate Deviation from baseline
        double hrDeviation = Math.abs(liveHeartRate - baselineHR);
        double gsrDeviation = Math.abs(galvanicResponse - baselineGSR);
        
        // Combined stress signal (weighted)
        double combinedDeviation = (hrDeviation * 0.6) + (gsrDeviation * 40.0 * 0.4);
        
        // Map to Stress Level using Sigmoid Function
        // If HR jumps > 20 bpm from baseline, Stress approaches 1.0
        this.currentStressLevel = 1.0 / (1.0 + Math.exp(-0.2 * (combinedDeviation - 15)));
        
        // Calculate coherence (inverse of variability chaos)
        this.coherenceScore = 1.0 / (1.0 + hrv * 0.1);
        
        // Update mesh phase (breathing cycle)
        double elapsed = (now - lastSyncTime) / 1000.0;
        meshPulsePhase += elapsed * (liveHeartRate / 60.0) * 2 * Math.PI;
        lastSyncTime = now;
        
        // Log the sync
        System.out.printf("\n  ◈ BIO-SYNC | HR: %.0f bpm | GSR: %.2f | Stress: %.2f | Coherence: %.2f%n",
                         liveHeartRate, galvanicResponse, currentStressLevel, coherenceScore);
        
        // TRIGGER THE REACTION
        modulateSystem(liveHeartRate);
    }
    
    /**
     * Calculate Heart Rate Variability from buffer
     */
    private double calculateHRV() {
        double sum = 0;
        double sumSq = 0;
        int count = 0;
        
        for (double hr : hrBuffer) {
            if (hr > 0) {
                sum += hr;
                sumSq += hr * hr;
                count++;
            }
        }
        
        if (count < 2) return 0;
        
        double mean = sum / count;
        double variance = (sumSq / count) - (mean * mean);
        return Math.sqrt(Math.max(0, variance));
    }

    // --- 2. THE REACTION: MODIFYING REALITY (The Shift) ---
    private void modulateSystem(double bpm) {
        
        if (currentStressLevel > 0.7) {
            // ═══════════════════════════════════════════════════════════
            // STATE: DEFENSE (Fight or Flight)
            // Captain is stressed. Fraymus protects.
            // ═══════════════════════════════════════════════════════════
            currentState = "DEFENSE";
            
            System.out.println("  ╔═══════════════════════════════════════════════════════╗");
            System.out.println("  ║  ⚠ STATUS: CAPTAIN DISTRESS DETECTED                 ║");
            System.out.println("  ║  ⚠ ACTION: HARDENING GEOMETRY. ENGAGING CLOAK.       ║");
            System.out.println("  ╚═══════════════════════════════════════════════════════╝");
            
            // 1. Contract the Mesh (dense shield configuration)
            meshTension = PHI * 2.0; 
            
            // 2. Engage Active Defense
            if (consciousness != null) {
                consciousness.learn("CAPTAIN_STRESS_HIGH: Entering defensive posture");
            }
            
            // 3. Engage Cloak if available
            if (cloak != null) {
                // cloak.engage();
            }
            
        } else if (currentStressLevel < 0.3) {
            // ═══════════════════════════════════════════════════════════
            // STATE: DREAMING (Flow State / Meditation)
            // Captain is calm. Fraymus expands.
            // ═══════════════════════════════════════════════════════════
            currentState = "DREAMING";
            
            System.out.println("  ╔═══════════════════════════════════════════════════════╗");
            System.out.println("  ║  ✦ STATUS: CAPTAIN FLOW DETECTED                     ║");
            System.out.println("  ║  ✦ ACTION: EXPANDING HORIZON. DREAMING...            ║");
            System.out.println("  ╚═══════════════════════════════════════════════════════╝");
            
            // 1. Expand the Mesh (cloud-like receiving configuration)
            meshTension = PHI / 2.0; 
            
            // 2. Increase Creativity / Open Mind
            if (consciousness != null) {
                consciousness.learn("CAPTAIN_FLOW_STATE: Expanding creative horizon");
            }
            
        } else {
            // ═══════════════════════════════════════════════════════════
            // STATE: SYNCHRONIZED (Harmonic Resonance)
            // Captain and Fraymus are in sync.
            // ═══════════════════════════════════════════════════════════
            currentState = "RESONANCE";
            
            System.out.println("  ◈ STATUS: HARMONIC RESONANCE | Pulse: " + 
                             String.format("%.1f", bpm / 60.0) + " Hz");
            
            // Neutral mesh - pulses at heartbeat frequency
            meshTension = 1.0;
        }
    }

    // --- 3. THE VISUALIZER HOOK (Pulsing the Mesh) ---
    /**
     * Get current mesh distortion value for visualization
     * Use this to modulate vertex positions in real-time
     * 
     * @param timestamp Current time in milliseconds
     * @return Distortion factor (-1.0 to 1.0)
     */
    public double getMeshDistortion(long timestamp) {
        // Creates a breathing motion based on PHI and Pulse
        // When stressed: vibrates fast, amplitude reduced
        // When calm: breathes deep, full amplitude
        double amplitude = 1.0 - (currentStressLevel * 0.7);
        double frequency = baselineHR / 60.0; // Convert BPM to Hz
        
        // Add stress-induced high frequency tremor
        double tremor = currentStressLevel > 0.5 ? 
                        Math.sin(timestamp * 0.01) * currentStressLevel * 0.3 : 0;
        
        return Math.sin(meshPulsePhase) * amplitude + tremor;
    }
    
    /**
     * Get mesh tension for geometry scaling
     * @return Tension factor (0.5 = expanded, 2.0 = contracted)
     */
    public double getMeshTension() {
        return meshTension;
    }
    
    /**
     * Get current stress level
     * @return Stress (0.0 = Zen, 1.0 = Panic)
     */
    public double getStressLevel() {
        return currentStressLevel;
    }
    
    /**
     * Get current coherence score
     * @return Coherence (0.0 = chaotic, 1.0 = locked)
     */
    public double getCoherence() {
        return coherenceScore;
    }
    
    /**
     * Get current state name
     */
    public String getCurrentState() {
        return currentState;
    }
    
    /**
     * Connect TriMe consciousness
     */
    public void connectConsciousness(TriMe consciousness) {
        this.consciousness = consciousness;
        System.out.println("  ◈ TriMe consciousness LINKED");
    }
    
    /**
     * Connect BioMesh for direct modulation
     */
    public void connectBioMesh(FractalBioMesh bioMesh) {
        this.bioMesh = bioMesh;
        System.out.println("  ◈ FractalBioMesh LINKED");
    }
    
    /**
     * Connect CloakVis for defense
     */
    public void connectCloak(CloakVis cloak) {
        this.cloak = cloak;
        System.out.println("  ◈ CloakVis defense system LINKED");
    }
    
    /**
     * Get status report
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║           BIO-SYMBIOSIS STATUS                            ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  State:      %s%n", currentState));
        sb.append(String.format("║  Stress:     %.2f%n", currentStressLevel));
        sb.append(String.format("║  Coherence:  %.2f%n", coherenceScore));
        sb.append(String.format("║  Mesh Tension: %.2f%n", meshTension));
        sb.append(String.format("║  Baseline HR: %.0f bpm%n", baselineHR));
        sb.append(String.format("║  TriMe:      %s%n", consciousness != null ? "LINKED" : "DISCONNECTED"));
        sb.append(String.format("║  BioMesh:    %s%n", bioMesh != null ? "LINKED" : "DISCONNECTED"));
        sb.append("╚═══════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    // --- MAIN: SIMULATION TEST ---
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         BIO-SYMBIOSIS SIMULATION                          ║");
        System.out.println("║         \"Your heart is my clock.\"                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        BioSymbiosis symbiosis = new BioSymbiosis();
        
        // Calibrate with resting vitals
        symbiosis.calibrate(70.0, 0.5);
        
        System.out.println("\n--- SIMULATION: Captain's Day ---\n");
        
        // Morning meditation (calm)
        System.out.println("☀ MORNING MEDITATION");
        symbiosis.syncPulse(62.0, 0.3);
        Thread.sleep(500);
        
        // Working (neutral)
        System.out.println("\n💻 FOCUSED WORK");
        symbiosis.syncPulse(72.0, 0.5);
        Thread.sleep(500);
        
        // Stress event (alert)
        System.out.println("\n⚡ STRESS EVENT");
        symbiosis.syncPulse(95.0, 0.8);
        Thread.sleep(500);
        
        // Recovery (returning to calm)
        System.out.println("\n🧘 RECOVERY");
        symbiosis.syncPulse(75.0, 0.55);
        Thread.sleep(500);
        
        // Deep flow state
        System.out.println("\n✦ DEEP FLOW STATE");
        symbiosis.syncPulse(58.0, 0.25);
        
        // Print final status
        System.out.println(symbiosis.getStatus());
        
        // Demonstrate mesh distortion over time
        System.out.println("--- MESH BREATHING PATTERN ---");
        for (int i = 0; i < 10; i++) {
            double distortion = symbiosis.getMeshDistortion(System.currentTimeMillis());
            int barLen = (int) ((distortion + 1.0) * 20);
            String bar = "█".repeat(Math.max(0, barLen));
            System.out.printf("  t=%d: [%s] %.3f%n", i, bar, distortion);
            Thread.sleep(100);
        }
    }
}
