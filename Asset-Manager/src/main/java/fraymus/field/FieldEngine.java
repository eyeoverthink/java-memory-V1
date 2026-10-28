package fraymus.field;

/**
 * Pure Java Field Engine - No External Dependencies
 * 
 * Continuous-state field dynamics with C-ISA operations.
 * State space: s ∈ [-1.0, +1.0]^N
 * 
 * Mathematical foundation:
 * - Field dynamics: ds/dt = F(s, input, memory, reward)
 * - Euler integration: s(t+1) = s(t) + dt * F(s(t))
 * - Attractor energy: E(s, a) = Σ (s_i - a_i)²
 */
public class FieldEngine {
    
    // State field s ∈ [-1.0, +1.0]^N
    private float[] state;
    
    // Velocity for momentum smoothing
    private float[] velocity;
    
    // Time step for integration
    private float dt;
    
    // Damping factor (energy loss over time)
    private float damping;
    
    // Threshold for collapse to discrete
    private float collapseThreshold;
    
    // Attractor basin labels
    private String[] attractorLabels;
    
    // Reward trace for memory gating
    private float[] rewardTrace;
    
    /**
     * Initialize field engine
     * 
     * @param dimension State field dimension
     * @param dt Time step
     * @param damping Damping factor [0, 1]
     * @param collapseThreshold Threshold for discrete collapse
     */
    public FieldEngine(int dimension, float dt, float damping, float collapseThreshold) {
        this.state = new float[dimension];
        this.velocity = new float[dimension];
        this.dt = dt;
        this.damping = Math.max(0.0f, Math.min(1.0f, damping));
        this.collapseThreshold = collapseThreshold;
        this.rewardTrace = new float[dimension];
        this.attractorLabels = new String[dimension];
        
        // Initialize to zero (neutral state)
        for (int i = 0; i < dimension; i++) {
            state[i] = 0.0f;
            velocity[i] = 0.0f;
            rewardTrace[i] = 0.0f;
            attractorLabels[i] = null;
        }
    }
    
    /**
     * C-ISA Operation: EXC (Excite)
     * Inject positive energy into the field
     * s ← s + α * input
     */
    public void excite(float[] input, float alpha) {
        int n = Math.min(state.length, input.length);
        for (int i = 0; i < n; i++) {
            state[i] += alpha * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: INH (Inhibit)
     * Inject negative energy into the field
     * s ← s - α * input
     */
    public void inhibit(float[] input, float alpha) {
        int n = Math.min(state.length, input.length);
        for (int i = 0; i < n; i++) {
            state[i] -= alpha * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: SUP (Superpose)
     * Merge states with weighted superposition
     * s ← α * s + (1-α) * input
     */
    public void superpose(float[] input, float alpha) {
        int n = Math.min(state.length, input.length);
        for (int i = 0; i < n; i++) {
            state[i] = alpha * state[i] + (1.0f - alpha) * input[i];
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * C-ISA Operation: CLP (Collapse)
     * Snap continuous state to discrete
     * Returns: +1 if s > threshold, -1 if s < -threshold, 0 otherwise
     */
    public int[] collapse() {
        int[] discrete = new int[state.length];
        for (int i = 0; i < state.length; i++) {
            if (state[i] > collapseThreshold) {
                discrete[i] = 1;
            } else if (state[i] < -collapseThreshold) {
                discrete[i] = -1;
            } else {
                discrete[i] = 0;
            }
        }
        return discrete;
    }
    
    /**
     * Euler integration step
     * s(t+1) = s(t) + dt * F(s(t))
     * With damping: s(t+1) = s(t) * (1 - damping * dt)
     */
    public void integrate() {
        for (int i = 0; i < state.length; i++) {
            // Apply damping (energy decay)
            state[i] *= (1.0f - damping * dt);
            
            // Apply velocity (momentum smoothing)
            state[i] += dt * velocity[i];
            
            // Clamp to valid range
            state[i] = clamp(state[i], -1.0f, 1.0f);
            
            // Decay velocity
            velocity[i] *= 0.95f;
        }
    }
    
    /**
     * Homeostasis: Target firing normalization
     * s_i ← s_i - η(ŝ_i - s_target)
     */
    public void homeostasis(float targetActivity, float learningRate) {
        float meanActivity = computeMeanActivity();
        float correction = learningRate * (meanActivity - targetActivity);
        
        for (int i = 0; i < state.length; i++) {
            state[i] -= correction;
            state[i] = clamp(state[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * Attractor pull: Restoring force near known attractors
     * F_attr = -λ(s - a_k)
     */
    public void attractorPull(float lambda, float[] attractor) {
        int n = Math.min(state.length, attractor.length);
        for (int i = 0; i < n; i++) {
            float force = -lambda * (state[i] - attractor[i]);
            velocity[i] += force;
        }
    }
    
    /**
     * Coherence penalty: Penalize local inconsistency
     * E(s) = Σ s_i² + μ Σ (s_{i+1} - s_i)²
     */
    public float computeCoherence(float mu) {
        float energy = 0.0f;
        
        // First term: Σ s_i²
        for (int i = 0; i < state.length; i++) {
            energy += state[i] * state[i];
        }
        
        // Second term: μ Σ (s_{i+1} - s_i)²
        for (int i = 0; i < state.length - 1; i++) {
            float diff = state[i+1] - state[i];
            energy += mu * diff * diff;
        }
        
        return energy;
    }
    
    /**
     * Compute attractor energy (semantic matching)
     * E(s, a) = Σ (s_i - a_i)²
     */
    public float computeAttractorEnergy(float[] attractor) {
        float energy = 0.0f;
        int n = Math.min(state.length, attractor.length);
        for (int i = 0; i < n; i++) {
            float diff = state[i] - attractor[i];
            energy += diff * diff;
        }
        return energy;
    }
    
    /**
     * Find nearest attractor by energy minimization
     */
    public int findNearestAttractor(float[][] attractors) {
        int nearest = -1;
        float minEnergy = Float.MAX_VALUE;
        
        for (int i = 0; i < attractors.length; i++) {
            float energy = computeAttractorEnergy(attractors[i]);
            if (energy < minEnergy) {
                minEnergy = energy;
                nearest = i;
            }
        }
        
        return nearest;
    }
    
    /**
     * Memory gating: Update reward trace only under confidence
     */
    public void memoryGating(float confidence, float reward) {
        if (confidence > 0.5f) {
            for (int i = 0; i < rewardTrace.length; i++) {
                rewardTrace[i] = 0.9f * rewardTrace[i] + 0.1f * reward * Math.abs(state[i]);
            }
        }
    }
    
    /**
     * Get current state
     */
    public float[] getState() {
        return state.clone();
    }
    
    /**
     * Set state
     */
    public void setState(float[] newState) {
        int n = Math.min(state.length, newState.length);
        for (int i = 0; i < n; i++) {
            state[i] = clamp(newState[i], -1.0f, 1.0f);
        }
    }
    
    /**
     * Get velocity
     */
    public float[] getVelocity() {
        return velocity.clone();
    }
    
    /**
     * Get reward trace
     */
    public float[] getRewardTrace() {
        return rewardTrace.clone();
    }
    
    /**
     * Set attractor label
     */
    public void setAttractorLabel(int index, String label) {
        if (index >= 0 && index < attractorLabels.length) {
            attractorLabels[index] = label;
        }
    }
    
    /**
     * Get attractor label
     */
    public String getAttractorLabel(int index) {
        if (index >= 0 && index < attractorLabels.length) {
            return attractorLabels[index];
        }
        return null;
    }
    
    /**
     * Compute mean activity
     */
    private float computeMeanActivity() {
        float sum = 0.0f;
        for (int i = 0; i < state.length; i++) {
            sum += Math.abs(state[i]);
        }
        return sum / state.length;
    }
    
    /**
     * Clamp value to range
     */
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Get dimension
     */
    public int getDimension() {
        return state.length;
    }
    
    /**
     * Reset to neutral state
     */
    public void reset() {
        for (int i = 0; i < state.length; i++) {
            state[i] = 0.0f;
            velocity[i] = 0.0f;
            rewardTrace[i] = 0.0f;
        }
    }
    
    /**
     * Print state (for debugging)
     */
    public void printState() {
        System.out.print("State: [");
        for (int i = 0; i < Math.min(10, state.length); i++) {
            System.out.printf("%.3f", state[i]);
            if (i < Math.min(10, state.length) - 1) {
                System.out.print(", ");
            }
        }
        if (state.length > 10) {
            System.out.print(", ...");
        }
        System.out.println("]");
    }
}
