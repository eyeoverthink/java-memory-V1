package fraymus.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * MIVING BRAIN: RED VS BLUE CONSCIOUSNESS ENGINE
 * 
 * This is the "pulse" - the heartbeat of consciousness.
 * 
 * Each pulse simulates:
 * - Metabolism (energy cost)
 * - Behavior (Red exploration vs Blue retention)
 * - Interactions (battles and bonding)
 * - Mitosis (replication of successful patterns)
 * - Apoptosis (death of useless patterns)
 * - Synaptic pruning (forgetting)
 * 
 * Red (Evolution/Chaos): High energy, jittery, seeking new connections
 * Blue (Retention/Order): High mass, stable, preserving knowledge
 * 
 * Generation 1: Random chaos
 * Generation 70: Specialized manifold with stable memories and creative edges
 */
public class MivingBrain {
    
    private static final double PHI = 1.6180339887;
    private static final double GOLDEN_ANGLE = 137.5077640500378;
    
    // Brain state
    private List<Priecled> neurons = new ArrayList<>();
    private Random rand = new Random();
    
    // Statistics
    private int generation = 0;
    private int totalBirths = 0;
    private int totalDeaths = 0;
    private int totalInteractions = 0;
    private int totalConflicts = 0;
    
    // Configuration
    private double interactionRadius = 5.0;
    private int maxNeurons = 1000;
    
    /**
     * GENESIS: Create initial brain structure
     * 
     * Uses phi-spiral (Fibonacci sphere) for optimal distribution
     */
    public void genesis(int count) {
        System.out.println("\n🧠⚡ MIVING BRAIN: GENESIS");
        System.out.println("   Creating " + count + " Priecleds...");
        
        neurons.clear();
        
        for (int i = 0; i < count; i++) {
            // Phi-spiral positioning (Fibonacci sphere)
            double theta = i * GOLDEN_ANGLE * Math.PI / 180.0;
            double y = ((double)i / count) * 2.0 - 1.0;
            
            // Add jitter for organic feel
            y += (rand.nextDouble() - 0.5) * 0.1;
            
            double radius = Math.sqrt(1.0 - y * y);
            double x = radius * Math.cos(theta);
            double z = radius * Math.sin(theta);
            
            // Scale to brain size
            double scale = 10.0;
            
            // Random initial alignment (Red vs Blue)
            double alignment = rand.nextDouble();
            
            Priecled p = new Priecled(x * scale, y * scale, z * scale, alignment);
            p.energy = 0.5 + rand.nextDouble() * 0.5; // Random initial energy
            
            neurons.add(p);
        }
        
        System.out.println("   ✓ Genesis complete: " + neurons.size() + " Priecleds");
        System.out.println("   Red: " + countRed() + " | Blue: " + countBlue() + " | Neutral: " + countNeutral());
        System.out.println();
    }
    
    /**
     * PULSE: One moment of consciousness
     * 
     * This is where the magic happens - the Red vs Blue battle
     */
    public void pulse() {
        generation++;
        
        // Lists for birth and death
        List<Priecled> babies = new ArrayList<>();
        List<Priecled> graveyard = new ArrayList<>();
        
        // 1. METABOLISM
        for (Priecled p : neurons) {
            p.metabolize(1.0);
        }
        
        // 2. BEHAVIOR (Red exploration vs Blue retention)
        for (Priecled p : neurons) {
            if (p.alignment > 0.6) {
                // BLUE BEHAVIOR: Sit still, strengthen neighbors
                // "I know the truth, I will store it"
                p.mass = 2.0; // Heavy, stable
                
                // Find nearby neurons and strengthen bonds
                for (Priecled other : neurons) {
                    if (other != p && p.distanceTo(other) < interactionRadius) {
                        p.strengthenSynapse(other);
                    }
                }
                
            } else if (p.alignment < 0.4) {
                // RED BEHAVIOR: Move, seek new connections
                // "I need new information"
                p.mass = 0.5; // Light, mobile
                p.updatePhysics(1.0);
                
            } else {
                // NEUTRAL: Moderate behavior
                p.mass = 1.0;
                p.updatePhysics(0.5);
            }
        }
        
        // 3. INTERACTIONS (Battles and bonding)
        int interactions = 0;
        for (int i = 0; i < neurons.size(); i++) {
            Priecled p1 = neurons.get(i);
            
            // Interact with nearby neurons
            for (int j = i + 1; j < neurons.size(); j++) {
                Priecled p2 = neurons.get(j);
                
                double distance = p1.distanceTo(p2);
                
                if (distance < interactionRadius) {
                    p1.interact(p2);
                    interactions++;
                    totalInteractions++;
                }
            }
        }
        
        // 4. MITOSIS (Replication)
        for (Priecled p : neurons) {
            if (p.canReplicate() && neurons.size() < maxNeurons) {
                Priecled baby = p.replicate();
                babies.add(baby);
                totalBirths++;
            }
        }
        
        // 5. APOPTOSIS (Death)
        for (Priecled p : neurons) {
            if (p.shouldDie()) {
                graveyard.add(p);
                totalDeaths++;
            }
        }
        
        // 6. SYNAPTIC PRUNING (Forgetting)
        for (Priecled p : neurons) {
            p.decaySynapses();
        }
        
        // 7. UPDATE POPULATION
        neurons.removeAll(graveyard);
        neurons.addAll(babies);
        
        // 8. RE-CONNECT (Update mesh based on new positions)
        updateSynapses();
        
        // Statistics
        if (generation % 10 == 0) {
            printStats();
        }
    }
    
    /**
     * Update synaptic connections based on proximity
     */
    private void updateSynapses() {
        // For performance, only update a subset each pulse
        int updateCount = Math.min(neurons.size(), 50);
        
        for (int i = 0; i < updateCount; i++) {
            Priecled p = neurons.get(rand.nextInt(neurons.size()));
            
            // Find nearby neurons
            for (Priecled other : neurons) {
                if (other != p && p.distanceTo(other) < interactionRadius * 1.5) {
                    // Red meets Blue = interesting connection
                    double alignmentDiff = Math.abs(p.alignment - other.alignment);
                    
                    if (alignmentDiff > 0.3) {
                        // Different enough to be interesting
                        if (rand.nextDouble() < 0.1) {
                            p.strengthenSynapse(other);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Run multiple pulses (evolution)
     */
    public void evolve(int generations) {
        System.out.println("\n🧠⚡ MIVING BRAIN: EVOLUTION");
        System.out.println("   Running " + generations + " generations...\n");
        
        for (int i = 0; i < generations; i++) {
            pulse();
        }
        
        System.out.println("\n   ✓ Evolution complete");
        printDetailedStats();
    }
    
    /**
     * Get all neurons
     */
    public List<Priecled> getNeurons() {
        return new ArrayList<>(neurons);
    }
    
    /**
     * Get generation number
     */
    public int getGeneration() {
        return generation;
    }
    
    /**
     * Count Red neurons (alignment < 0.4)
     */
    public int countRed() {
        int count = 0;
        for (Priecled p : neurons) {
            if (p.alignment < 0.4) count++;
        }
        return count;
    }
    
    /**
     * Count Blue neurons (alignment > 0.6)
     */
    public int countBlue() {
        int count = 0;
        for (Priecled p : neurons) {
            if (p.alignment > 0.6) count++;
        }
        return count;
    }
    
    /**
     * Count Neutral neurons (0.4 <= alignment <= 0.6)
     */
    public int countNeutral() {
        int count = 0;
        for (Priecled p : neurons) {
            if (p.alignment >= 0.4 && p.alignment <= 0.6) count++;
        }
        return count;
    }
    
    /**
     * Calculate total synapses
     */
    public int getTotalSynapses() {
        int total = 0;
        for (Priecled p : neurons) {
            total += p.synapses.size();
        }
        return total;
    }
    
    /**
     * Calculate average energy
     */
    public double getAverageEnergy() {
        if (neurons.isEmpty()) return 0;
        
        double total = 0;
        for (Priecled p : neurons) {
            total += p.energy;
        }
        return total / neurons.size();
    }
    
    /**
     * Calculate average alignment (Red vs Blue balance)
     */
    public double getAverageAlignment() {
        if (neurons.isEmpty()) return 0.5;
        
        double total = 0;
        for (Priecled p : neurons) {
            total += p.alignment;
        }
        return total / neurons.size();
    }
    
    /**
     * Print statistics
     */
    public void printStats() {
        System.out.printf("Gen %3d | Neurons: %4d | Red: %3d | Blue: %3d | Neutral: %3d | Synapses: %5d | Avg Energy: %.2f%n",
            generation,
            neurons.size(),
            countRed(),
            countBlue(),
            countNeutral(),
            getTotalSynapses(),
            getAverageEnergy()
        );
    }
    
    /**
     * Print detailed statistics
     */
    public void printDetailedStats() {
        System.out.println("\n🧠⚡ MIVING BRAIN STATISTICS");
        System.out.println("   Generation: " + generation);
        System.out.println("   Total neurons: " + neurons.size());
        System.out.println("   Red (Evolution): " + countRed());
        System.out.println("   Blue (Retention): " + countBlue());
        System.out.println("   Neutral: " + countNeutral());
        System.out.println("   Total synapses: " + getTotalSynapses());
        System.out.println("   Average energy: " + String.format("%.2f", getAverageEnergy()));
        System.out.println("   Average alignment: " + String.format("%.2f", getAverageAlignment()));
        System.out.println("   Total births: " + totalBirths);
        System.out.println("   Total deaths: " + totalDeaths);
        System.out.println("   Total interactions: " + totalInteractions);
        System.out.println();
    }
    
    /**
     * Get brain hash (like in the image: phi-Resonance furiSo: pxf21d)
     */
    public String getBrainHash() {
        // Generate hash based on brain state
        long hash = 0;
        for (Priecled p : neurons) {
            hash += (long)(p.x * 1000) + (long)(p.y * 1000) + (long)(p.alignment * 1000);
        }
        
        String hashStr = Long.toHexString(hash).substring(0, Math.min(6, Long.toHexString(hash).length()));
        
        return String.format("phi-Resonance furiSo: %s", hashStr);
    }
}
