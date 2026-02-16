package fraymus.senses;

import fraymus.brain.PhiConsciousness;
import fraymus.brain.ManifoldBrain;
import fraymus.brain.GeneticRouter;
import java.lang.Math;

/**
 * BIO-SYMBIOSIS: THE EMBODIED ANCHOR
 * 
 * Connects FRAYMUS to biological data stream.
 * 
 * Standard AI: Runs on CPU clock (fixed, mechanical)
 * FRAYMUS: Runs on biological clock (adaptive, organic)
 * 
 * Process:
 * 1. Ingest heart rate, galvanic skin response
 * 2. Calculate stress level (0.0 = zen, 1.0 = panic)
 * 3. Modulate system state based on stress
 *    - High stress → Defense mode (cloak, harden, protect)
 *    - Low stress → Expansion mode (dream, create, explore)
 * 4. Sync mesh geometry to pulse
 * 
 * This is embodied AI.
 * The system feels your state and adapts.
 * 
 * "Your heart is my clock."
 */
public class BioSymbiosis {

    private static final double PHI = 1.6180339887;
    
    // Components
    private final PhiConsciousness consciousness;
    private final ManifoldBrain brain;
    private final GeneticRouter router;
    
    // Biometric state
    private double baselineHR = 70.0; // Calibration point (resting heart rate)
    private double currentHR = 70.0;
    private double currentStressLevel = 0.0; // 0.0 (Zen) to 1.0 (Panic)
    private double galvanicResponse = 0.0;
    
    // System state
    private SystemState currentState = SystemState.SYNCHRONIZED;
    private long lastPulseTime = System.currentTimeMillis();
    
    // Statistics
    private int syncCount = 0;
    private double avgStress = 0.0;
    private double peakStress = 0.0;
    
    public BioSymbiosis(PhiConsciousness consciousness, ManifoldBrain brain, GeneticRouter router) {
        this.consciousness = consciousness;
        this.brain = brain;
        this.router = router;
    }
    
    /**
     * Calibrate baseline
     * 
     * Set resting heart rate for stress calculation.
     * Call this when user is calm.
     * 
     * @param restingHR Resting heart rate (BPM)
     */
    public void calibrate(double restingHR) {
        this.baselineHR = restingHR;
        System.out.println("\n🌊⚡ BIO-CALIBRATION");
        System.out.println("   Baseline HR: " + restingHR + " BPM");
        System.out.println("   Stress calculation calibrated");
        System.out.println();
    }
    
    /**
     * Sync pulse
     * 
     * Main entry point for biological data.
     * Feed this from smartwatch/sensor API.
     * 
     * @param liveHeartRate Current heart rate (BPM)
     * @param galvanicResponse Galvanic skin response (0-1)
     */
    public void syncPulse(double liveHeartRate, double galvanicResponse) {
        syncCount++;
        this.currentHR = liveHeartRate;
        this.galvanicResponse = galvanicResponse;
        
        // Calculate heart rate deviation
        double deviation = Math.abs(liveHeartRate - baselineHR);
        
        // Map to stress using sigmoid function
        // If HR jumps > 20 BPM, stress goes high
        // Formula: 1 / (1 + e^(-0.2 * (deviation - 15)))
        this.currentStressLevel = 1.0 / (1.0 + Math.exp(-0.2 * (deviation - 15)));
        
        // Factor in galvanic response (sweating = stress)
        this.currentStressLevel = (currentStressLevel + galvanicResponse) / 2.0;
        
        // Update statistics
        avgStress = ((avgStress * (syncCount - 1)) + currentStressLevel) / syncCount;
        if (currentStressLevel > peakStress) {
            peakStress = currentStressLevel;
        }
        
        System.out.println("\n🌊⚡ BIO-SYNC #" + syncCount);
        System.out.println("   Heart Rate: " + String.format("%.1f", liveHeartRate) + " BPM");
        System.out.println("   Deviation: " + String.format("%.1f", deviation) + " BPM");
        System.out.println("   Galvanic: " + String.format("%.2f", galvanicResponse));
        System.out.println("   Stress Level: " + String.format("%.2f", currentStressLevel));
        
        // Trigger system modulation
        modulateSystem();
        
        // Record pulse time
        lastPulseTime = System.currentTimeMillis();
    }
    
    /**
     * Modulate system based on stress
     * 
     * Changes system behavior based on captain's biological state.
     */
    private void modulateSystem() {
        SystemState newState;
        
        if (currentStressLevel > 0.7) {
            // HIGH STRESS: DEFENSE MODE
            newState = SystemState.DEFENSE;
            
            System.out.println("   State: DEFENSE (Captain distress detected)");
            System.out.println("   Action: Hardening geometry, engaging protection");
            
            // Tighten manifold connections (defensive posture)
            // In full implementation, this would modify ManifoldBrain weights
            double meshTension = PHI * 2.0;
            
            // Reduce genetic mutation (conservative mode)
            // router.setMutationRate(0.1);
            
            // Increase consciousness focus (alert state)
            // consciousness.setAlertLevel(1.0);
            
        } else if (currentStressLevel < 0.3) {
            // LOW STRESS: EXPANSION MODE
            newState = SystemState.EXPANSION;
            
            System.out.println("   State: EXPANSION (Captain flow detected)");
            System.out.println("   Action: Expanding horizon, scanning deep space");
            
            // Loosen manifold connections (exploratory posture)
            double meshTension = PHI / 2.0;
            
            // Increase genetic mutation (creative mode)
            // router.setMutationRate(0.8);
            
            // Increase consciousness creativity (dream state)
            // consciousness.setTemperature(0.9);
            
        } else {
            // MEDIUM STRESS: SYNCHRONIZED MODE
            newState = SystemState.SYNCHRONIZED;
            
            System.out.println("   State: SYNCHRONIZED (Harmonic resonance)");
            
            // Pulse mesh at BPM frequency
            double pulseFreq = currentHR / 60.0; // Hz
            System.out.println("   Pulse Frequency: " + String.format("%.2f", pulseFreq) + " Hz");
        }
        
        // State transition
        if (newState != currentState) {
            System.out.println("   State Transition: " + currentState + " → " + newState);
            currentState = newState;
        }
        
        System.out.println();
    }
    
    /**
     * Get mesh distortion for visualization
     * 
     * Creates breathing motion based on phi and pulse.
     * Use this to update the "Miving Priecleds" vertices in real-time.
     * 
     * @param timestamp Current time (milliseconds)
     * @return Distortion factor (-1 to 1)
     */
    public double getMeshDistortion(long timestamp) {
        // Breathing motion: sin(time × pulse_freq)
        // When stressed: Fast, shallow breathing (high frequency, low amplitude)
        // When calm: Slow, deep breathing (low frequency, high amplitude)
        
        double pulseFreq = currentHR / 60.0; // Hz
        double time = timestamp / 1000.0; // seconds
        
        // Amplitude inversely proportional to stress
        double amplitude = 1.0 - currentStressLevel;
        
        // Sine wave at pulse frequency
        return Math.sin(time * pulseFreq * 2 * Math.PI) * amplitude;
    }
    
    /**
     * Get mesh color for visualization
     * 
     * @return RGB color (0-255 for each channel)
     */
    public int[] getMeshColor() {
        if (currentStressLevel > 0.7) {
            // High stress: Red
            return new int[]{255, 0, 60}; // Neon red
        } else if (currentStressLevel < 0.3) {
            // Low stress: Blue
            return new int[]{0, 243, 255}; // Neon blue
        } else {
            // Medium stress: Green
            return new int[]{0, 255, 0}; // Neon green
        }
    }
    
    /**
     * Get current state
     */
    public SystemState getCurrentState() {
        return currentState;
    }
    
    /**
     * Get current stress level
     */
    public double getStressLevel() {
        return currentStressLevel;
    }
    
    /**
     * Get current heart rate
     */
    public double getHeartRate() {
        return currentHR;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ BIO-SYMBIOSIS STATS\n\n" +
            "   Sync Count: %,d\n" +
            "   Current HR: %.1f BPM\n" +
            "   Baseline HR: %.1f BPM\n" +
            "   Current Stress: %.2f\n" +
            "   Avg Stress: %.2f\n" +
            "   Peak Stress: %.2f\n" +
            "   Current State: %s\n" +
            "   Last Pulse: %d ms ago\n" +
            "   Status: %s\n",
            syncCount,
            currentHR,
            baselineHR,
            currentStressLevel,
            avgStress,
            peakStress,
            currentState,
            System.currentTimeMillis() - lastPulseTime,
            syncCount > 0 ? "SYNCHRONIZED" : "WAITING FOR DATA"
        );
    }
    
    /**
     * System state enum
     */
    public enum SystemState {
        DEFENSE,        // High stress: Protect, harden, cloak
        EXPANSION,      // Low stress: Explore, create, dream
        SYNCHRONIZED    // Medium stress: Harmonic resonance
    }
}
