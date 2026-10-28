package fraymus.field;

import java.util.Arrays;

/**
 * F-0 Kernel Simulator - Continuous Field Computation
 * 
 * Simulates the Fraynix Unified Computation Kernel (F-0) which treats
 * memory as a continuous state field and executes continuous differential
 * equations instead of discrete instructions.
 * 
 * Architecture:
 * - Memory: Continuous array of StateBits (s ∈ [-1.0, +1.0])
 * - Execution: Tickless differential equation solver (Euler integration)
 * - Interrupts: Energy spikes that ripple through the field
 * - Language: Attractor states in the energy landscape
 */
public class F0KernelSimulator {
    
    // The continuous field (simulated as discrete array for computation)
    private final double[] field;
    
    // Field dimensions
    private final int size;
    
    // Integration parameters
    private double dt = 0.001; // Time step
    private double damping = 0.99; // Field damping/decay
    
    // Attractor states (word = geometric shape in field)
    private final java.util.Map<String, double[]> attractors;
    
    // Current simulation time
    private double time = 0.0;
    
    public F0KernelSimulator(int size) {
        this.size = size;
        this.field = new double[size];
        this.attractors = new java.util.HashMap<>();
        
        // Initialize field to zero (balanced tension)
        Arrays.fill(field, 0.0);
        
        // Define sample attractors
        initializeAttractors();
    }
    
    /**
     * Initialize attractor states for common concepts
     * Each word is a geometric pattern in the field
     */
    private void initializeAttractors() {
        // "APPLE" - Positive, clustered pattern
        attractors.put("APPLE", createAttractor(0.8, 0.6, 0.7, 0.5, 0.9));
        
        // "FIRE" - High energy, spreading pattern
        attractors.put("FIRE", createAttractor(1.0, 0.9, 0.8, 0.95, 0.85));
        
        // "WATER" - Fluid, oscillating pattern
        attractors.put("WATER", createAttractor(0.3, 0.5, 0.4, 0.6, 0.35));
        
        // "VOID" - Near-zero, balanced tension
        attractors.put("VOID", createAttractor(0.0, 0.0, 0.0, 0.0, 0.0));
    }
    
    private double[] createAttractor(double... values) {
        double[] pattern = new double[size];
        for (int i = 0; i < values.length && i < size; i++) {
            pattern[i] = values[i];
        }
        return pattern;
    }
    
    // ========== C-ISA Instructions ==========
    
    /**
     * FLD [addr], [val] - Field Load
     * Initializes a memory region to a continuous baseline state
     */
    public void FLD(int addr, double val) {
        if (addr >= 0 && addr < size) {
            field[addr] = clamp(val);
        }
    }
    
    /**
     * EXC [addr], [delta] - Excite
     * Injects positive energy into the state field
     * Math: s = min(s + delta, 1.0)
     */
    public void EXC(int addr, double delta) {
        if (addr >= 0 && addr < size) {
            field[addr] = Math.min(field[addr] + delta, 1.0);
            // Ripple effect to neighbors
            ripple(addr, delta * 0.3, 2);
        }
    }
    
    /**
     * INH [addr], [delta] - Inhibit
     * Injects negative energy (suppression) into the state field
     * Math: s = max(s - delta, -1.0)
     */
    public void INH(int addr, double delta) {
        if (addr >= 0 && addr < size) {
            field[addr] = Math.max(field[addr] - delta, -1.0);
            // Ripple effect to neighbors
            ripple(addr, -delta * 0.3, 2);
        }
    }
    
    /**
     * SUP [addr1], [addr2] - Superpose
     * Entangles two field regions, merging waveforms
     * Math: s_new = (s1 + s2) / 2
     */
    public void SUP(int addr1, int addr2) {
        if (addr1 >= 0 && addr1 < size && addr2 >= 0 && addr2 < size) {
            double superposed = (field[addr1] + field[addr2]) / 2.0;
            field[addr1] = superposed;
            field[addr2] = superposed;
        }
    }
    
    /**
     * CLP [addr] - Collapse
     * Forces continuous field to snap into discrete binary state
     * Logic: if s > 0.5 → 1; if s < -0.5 → -1; else → 0
     */
    public int CLP(int addr) {
        if (addr >= 0 && addr < size) {
            double s = field[addr];
            if (s > 0.5) return 1;
            if (s < -0.5) return -1;
            return 0;
        }
        return 0;
    }
    
    // ========== Differential Equation Solver ==========
    
    /**
     * Execute one integration step (Euler method)
     * ds/dt = F(s, input, memory, reward)
     */
    public void integrateStep() {
        double[] newField = new double[size];
        
        for (int i = 0; i < size; i++) {
            // Calculate local dynamics
            double influence = calculateLocalInfluence(i);
            
            // Euler integration: s(t+dt) = s(t) + dt * ds/dt
            newField[i] = field[i] + dt * influence;
            
            // Apply damping
            newField[i] *= damping;
            
            // Clamp to valid range
            newField[i] = clamp(newField[i]);
        }
        
        // Update field
        System.arraycopy(newField, 0, field, 0, size);
        
        // Increment time
        time += dt;
    }
    
    /**
     * Calculate local influence from neighbors (diffusion)
     */
    private double calculateLocalInfluence(int addr) {
        double influence = 0.0;
        
        // Diffusion from neighbors
        int left = Math.max(0, addr - 1);
        int right = Math.min(size - 1, addr + 1);
        
        influence += (field[left] - field[addr]) * 0.5;
        influence += (field[right] - field[addr]) * 0.5;
        
        return influence;
    }
    
    /**
     * Ripple effect - energy spreads through field
     */
    private void ripple(int addr, double delta, int radius) {
        for (int i = 1; i <= radius; i++) {
            int left = Math.max(0, addr - i);
            int right = Math.min(size - 1, addr + i);
            
            double decayedDelta = delta / (i + 1);
            
            field[left] = clamp(field[left] + decayedDelta);
            field[right] = clamp(field[right] + decayedDelta);
        }
    }
    
    // ========== Attractor Operations ==========
    
    /**
     * Sculpt field toward attractor state
     * This simulates the compiler generating instructions to shape the field
     */
    public void sculptToAttractor(String word) {
        double[] target = attractors.get(word);
        if (target == null) {
            System.out.println("Unknown attractor: " + word);
            return;
        }
        
        // Apply energy to sculpt field toward target shape
        for (int i = 0; i < size; i++) {
            double diff = target[i] - field[i];
            if (diff > 0) {
                EXC(i, diff * 0.1);
            } else if (diff < 0) {
                INH(i, -diff * 0.1);
            }
        }
    }
    
    /**
     * Calculate energy distance to attractor
     * E(s, a) = Σ (s_i - a_i)²
     */
    public double energyToAttractor(String word) {
        double[] target = attractors.get(word);
        if (target == null) return Double.MAX_VALUE;
        
        double energy = 0.0;
        for (int i = 0; i < size; i++) {
            energy += Math.pow(field[i] - target[i], 2);
        }
        return energy;
    }
    
    /**
     * Find nearest attractor (word recognition)
     */
    public String recognizeNearestAttractor() {
        String nearest = null;
        double minEnergy = Double.MAX_VALUE;
        
        for (java.util.Map.Entry<String, double[]> entry : attractors.entrySet()) {
            double energy = energyToAttractor(entry.getKey());
            if (energy < minEnergy) {
                minEnergy = energy;
                nearest = entry.getKey();
            }
        }
        
        return nearest;
    }
    
    // ========== Helper Methods ==========
    
    private double clamp(double value) {
        return Math.max(-1.0, Math.min(1.0, value));
    }
    
    // ========== Visualization ==========
    
    /**
     * Get field state as ASCII visualization
     */
    public String visualizeField() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field State (t=").append(String.format("%.3f", time)).append("):\n");
        sb.append("[");
        
        for (int i = 0; i < size; i++) {
            char c = fieldToChar(field[i]);
            sb.append(c);
            if (i < size - 1) sb.append(" ");
        }
        
        sb.append("]\n");
        return sb.toString();
    }
    
    /**
     * Convert field value to visualization character
     */
    private char fieldToChar(double value) {
        if (value > 0.8) return '#';
        if (value > 0.6) return 'X';
        if (value > 0.4) return 'x';
        if (value > 0.2) return ':';
        if (value > -0.2) return '.';
        if (value > -0.4) return ':';
        if (value > -0.6) return 'x';
        if (value > -0.8) return 'X';
        return '#';
    }
    
    /**
     * Get field values
     */
    public double[] getField() {
        return field.clone();
    }
    
    public double getFieldAt(int addr) {
        if (addr >= 0 && addr < size) {
            return field[addr];
        }
        return 0.0;
    }
    
    public double getTime() {
        return time;
    }
    
    public void reset() {
        Arrays.fill(field, 0.0);
        time = 0.0;
    }
}
