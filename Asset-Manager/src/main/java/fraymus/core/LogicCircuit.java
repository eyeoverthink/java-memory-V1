package fraymus.core;

/**
 * 🧠 LOGIC CIRCUIT - THE BRAIN
 * 
 * Implements Momentum-based reflex arcs and Phi-decay memory.
 * Governed by the Golden Ratio (φ) and Chaos Theory.
 * 
 * This is the decision-making hub where Universal Constants
 * govern reflexes, allowing the system to detect "Chaos" spikes
 * and react to events before they fully manifest.
 */
public class LogicCircuit {
    
    private static final double PHI = 1.6180339887; // The Universal Constant
    private static final double PHI_RECIPROCAL = 0.6180339887; // 1/φ for natural damping
    private static final double CHAOS_THRESHOLD = PHI * 2; // Mutation trigger point
    
    private double momentum = 0.0;
    private double lastLoad = 0.0;
    private double temperature = 0.0; // System "heat" from processing
    private int mutationCount = 0;
    private long lastMutationTime = 0;
    
    // Reflex arc state
    private boolean reflexActive = false;
    private double reflexIntensity = 0.0;
    
    /**
     * Process an impulse (input signal) and update momentum
     */
    public void pulse(double currentInput) {
        double velocity = currentInput - lastLoad;
        
        // Phi-decay memory: 0.618 is the Golden Ratio's reciprocal
        // This provides natural damping and memory retention
        momentum = (momentum * PHI_RECIPROCAL) + velocity;
        
        // Update temperature based on processing intensity
        temperature = (temperature * 0.9) + (Math.abs(velocity) * 0.1);
        
        // Check for chaos threshold
        if (Math.abs(momentum) > CHAOS_THRESHOLD) {
            triggerMutation();
        }
        
        lastLoad = currentInput;
    }
    
    /**
     * Process impulse with explicit velocity
     */
    public void processImpulse(double currentLoad) {
        pulse(currentLoad);
    }
    
    /**
     * Trigger mutation sequence when chaos threshold is exceeded
     */
    private void triggerMutation() {
        long now = System.currentTimeMillis();
        
        // Prevent mutation spam (minimum 1 second between mutations)
        if (now - lastMutationTime < 1000) {
            return;
        }
        
        mutationCount++;
        lastMutationTime = now;
        reflexActive = true;
        reflexIntensity = Math.abs(momentum) / CHAOS_THRESHOLD;
        
        System.err.println(">>> [LOGIC] Chaos Threshold Exceeded. Mutating Synapses...");
        System.err.println("    Momentum: " + String.format("%.3f", momentum));
        System.err.println("    Temperature: " + String.format("%.3f", temperature));
        System.err.println("    Mutation #" + mutationCount);
        
        // Mutation logic would go here (to be implemented by subclasses or handlers)
        mutateReflexArcs();
    }
    
    /**
     * Mutate reflex arcs - adjust internal constants for optimization
     * This is where the system "evolves" its own logic
     */
    public void mutateReflexArcs() {
        // Calculate mutation strength based on chaos intensity
        double mutationStrength = Math.min(1.0, Math.abs(momentum) / (CHAOS_THRESHOLD * 2));
        
        // Apply phi-harmonic mutation to internal state
        // This creates a "breathing" effect in the logic
        double phiModulation = Math.sin(mutationCount * PHI) * mutationStrength;
        
        // Reset momentum with phi-based damping
        momentum = momentum * (1.0 - mutationStrength * PHI_RECIPROCAL);
        
        // Cool down temperature
        temperature = temperature * 0.5;
        
        System.out.println("    [MUTATION] Strength: " + String.format("%.3f", mutationStrength));
        System.out.println("    [MUTATION] Phi Modulation: " + String.format("%.3f", phiModulation));
        
        // Deactivate reflex after mutation
        reflexActive = false;
    }
    
    /**
     * Calculate system vitality (overall health metric)
     */
    public double calculateSystemVitality() {
        // Vitality is inverse of chaos
        // High momentum = low vitality
        // Low temperature = high vitality
        double momentumFactor = 1.0 - Math.min(1.0, Math.abs(momentum) / (CHAOS_THRESHOLD * 2));
        double temperatureFactor = 1.0 - Math.min(1.0, temperature / 10.0);
        
        // Weighted average using phi proportions
        return (momentumFactor * PHI + temperatureFactor) / (PHI + 1);
    }
    
    /**
     * Get current entropy level (0.0 = ordered, 1.0 = chaotic)
     */
    public double getEntropy() {
        return Math.min(1.0, Math.abs(momentum) / CHAOS_THRESHOLD);
    }
    
    /**
     * Check if system is in chaos state
     */
    public boolean isChaotic() {
        return Math.abs(momentum) > CHAOS_THRESHOLD;
    }
    
    /**
     * Reset the circuit to initial state
     */
    public void reset() {
        momentum = 0.0;
        lastLoad = 0.0;
        temperature = 0.0;
        reflexActive = false;
        reflexIntensity = 0.0;
        System.out.println("[LOGIC] Circuit reset to initial state");
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════════════════════════════
    
    public double getMomentum() { 
        return momentum; 
    }
    
    public double getTemperature() { 
        return temperature; 
    }
    
    public double getPHI() { 
        return PHI; 
    }
    
    public int getMutationCount() { 
        return mutationCount; 
    }
    
    public boolean isReflexActive() { 
        return reflexActive; 
    }
    
    public double getReflexIntensity() { 
        return reflexIntensity; 
    }
    
    /**
     * Get circuit status as formatted string
     */
    public String getStatus() {
        return String.format(
            "Momentum: %.3f | Temperature: %.3f | Entropy: %.3f | Mutations: %d | Vitality: %.3f",
            momentum, temperature, getEntropy(), mutationCount, calculateSystemVitality()
        );
    }
    
    /**
     * Print detailed status
     */
    public void printStatus() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   🧠 LOGIC CIRCUIT STATUS                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Momentum: " + String.format("%.3f", momentum));
        System.out.println("  Temperature: " + String.format("%.3f", temperature));
        System.out.println("  Entropy: " + String.format("%.3f", getEntropy()));
        System.out.println("  Vitality: " + String.format("%.3f", calculateSystemVitality()));
        System.out.println("  Mutations: " + mutationCount);
        System.out.println("  Reflex Active: " + reflexActive);
        System.out.println("  Chaos State: " + (isChaotic() ? "YES" : "NO"));
    }
}
