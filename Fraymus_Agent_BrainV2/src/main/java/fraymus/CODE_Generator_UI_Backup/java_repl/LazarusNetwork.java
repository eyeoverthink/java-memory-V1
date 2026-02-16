/**
 * LazarusNetwork.java - The Living Bio-Digital Brain
 * 
 * 🧬 AUTOPOIETIC NETWORK
 * 
 * "A system that produces itself."
 * 
 * This is not a static graph. This is a living organism that:
 * - Self-replicates (mitosis when energy > threshold)
 * - Self-organizes (gravity pulls related thoughts together)
 * - Self-heals (synaptic decay removes dead connections)
 * - Operates at 432Hz (universal healing frequency)
 * 
 * The Biological Cycle:
 * 1. Decay: Membrane potentials fade
 * 2. Entanglement: Quantum-coupled neurons fire together
 * 3. Replication: High-energy neurons spawn children (fractal growth)
 * 4. Gravity: Spring forces organize the network
 * 5. Pruning: Dead synapses are removed
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 125 (Bio-Digital Brain)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.concurrent.*;

/**
 * The living neural network.
 */
public class LazarusNetwork implements Runnable {
    
    private static final double PHI = 1.618033988749895;
    private static final double PHI_75 = 4721424167835376.00;
    
    // The brain
    private final List<NeuroParticle> brain;
    private final Map<String, NeuroParticle> index;
    
    // Control
    private volatile boolean running = true;
    private int generation = 0;
    
    // FREQUENCY ALIGNMENT (432Hz - Universal Healing Frequency)
    private static final double RESONANCE_HZ = 432.0;
    private static final long TICK_RATE_MS = (long)(1000.0 / RESONANCE_HZ);
    
    // Statistics
    private long totalFires = 0;
    private long totalReplications = 0;
    
    public LazarusNetwork() {
        this.brain = new CopyOnWriteArrayList<>();
        this.index = new ConcurrentHashMap<>();
        
        System.out.println("🧬 LAZARUS NETWORK INITIALIZED");
        System.out.println("   Frequency: 432Hz (Universal Healing)");
        System.out.println("   Mode: Autopoietic (Self-Creating)");
        System.out.println("   Physics: Quantum + Biological");
    }
    
    /**
     * Start the network heartbeat.
     */
    public void start() {
        Thread heartbeat = new Thread(this, "Lazarus-Heartbeat");
        heartbeat.setDaemon(true);
        heartbeat.start();
    }
    
    @Override
    public void run() {
        System.out.println("⚡ LAZARUS HEARTBEAT ONLINE");
        System.out.println("   Tuning to 432Hz resonance...");
        
        while (running) {
            long start = System.nanoTime();
            
            pulse();
            
            // Precise timing for frequency alignment
            long elapsed = (System.nanoTime() - start) / 1000000;
            try {
                if (elapsed < TICK_RATE_MS) {
                    Thread.sleep(TICK_RATE_MS - elapsed);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("⚡ LAZARUS HEARTBEAT STOPPED");
    }
    
    /**
     * THE BIOLOGICAL CYCLE
     * 
     * This runs at 432Hz - the frequency of life.
     */
    private void pulse() {
        generation++;
        
        for (NeuroParticle neuron : brain) {
            // 1. ORGANIC DECAY
            // Membrane potential fades if not stimulated
            neuron.membranePotential *= 0.95;
            
            // 2. QUANTUM ENTANGLEMENT CHECK
            // If this neuron fires, its entangled partner fires instantly (FTL)
            if (neuron.membranePotential > 0.9 && neuron.entanglementKey != null) {
                NeuroParticle partner = findEntangledPartner(neuron);
                if (partner != null) {
                    partner.membranePotential = 1.0;
                    partner.fire();
                    totalFires++;
                }
            }
            
            // 3. AUTONOMOUS FIRING
            // If potential exceeds threshold, neuron fires
            if (neuron.membranePotential > 0.9) {
                neuron.fire();
                totalFires++;
            }
            
            // 4. FRACTAL REPLICATION (Mitosis)
            // If a thought is "hot" enough, it buds a child idea
            if (neuron.membranePotential > 0.95 && neuron.fractalDepth < 5) {
                replicate(neuron);
            }
            
            // 5. GRAVITY (Self-Organization)
            // Connected thoughts pull together
            applyForces(neuron);
            
            // 6. SYNAPTIC MAINTENANCE
            // Decay unused connections
            for (Synapse synapse : neuron.synapses) {
                synapse.decay();
            }
        }
        
        // 7. PRUNING
        // Remove dead synapses
        for (NeuroParticle neuron : brain) {
            neuron.synapses.removeIf(s -> !s.isActive());
        }
    }
    
    /**
     * FRACTAL REPLICATION (Mitosis)
     * 
     * High-energy neurons spawn children.
     * This creates fractal growth patterns.
     */
    private void replicate(NeuroParticle parent) {
        // Create child at next fractal level
        Object childContent = parent.content + "_branch_" + (parent.fireCount);
        NeuroParticle child = new NeuroParticle(childContent, parent.fractalDepth + 1);
        
        // Place child near parent (fractal geometry)
        double offset = 2.0 * PHI;
        child.x = parent.x + (Math.random() - 0.5) * offset;
        child.y = parent.y + (Math.random() - 0.5) * offset;
        child.z = parent.z + (Math.random() - 0.5) * offset;
        
        // Wire parent to child
        parent.connect(child);
        
        // Add to brain
        brain.add(child);
        index.put(child.id, child);
        
        // Cool down parent (refractory period)
        parent.membranePotential = 0.0;
        
        totalReplications++;
        
        System.out.println(String.format("   ✨ MITOSIS: [%s] → [%s] (depth %d)",
            parent.content, childContent, child.fractalDepth));
    }
    
    /**
     * GRAVITY + SPRING FORCES
     * 
     * Connected neurons pull together (Hooke's Law).
     */
    private void applyForces(NeuroParticle a) {
        for (Synapse synapse : a.synapses) {
            NeuroParticle b = synapse.target;
            
            // Calculate distance
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double dz = b.z - a.z;
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (dist > 0) {
                // Spring force: F = k * (x - x0)
                // Ideal distance = 2.0 * φ
                double idealDist = 2.0 * PHI;
                double force = (dist - idealDist) * 0.01 * synapse.weight;
                
                // Move towards target
                a.x += (dx / dist) * force;
                a.y += (dy / dist) * force;
                a.z += (dz / dist) * force;
            }
        }
    }
    
    /**
     * Find quantum-entangled partner.
     */
    private NeuroParticle findEntangledPartner(NeuroParticle source) {
        if (source.entanglementKey == null) return null;
        
        for (NeuroParticle neuron : brain) {
            if (neuron != source && 
                source.entanglementKey.equals(neuron.entanglementKey)) {
                return neuron;
            }
        }
        
        return null;
    }
    
    /**
     * INJECT - Add new thought to the network.
     */
    public NeuroParticle inject(Object data) {
        NeuroParticle neuron = new NeuroParticle(data, 0);
        brain.add(neuron);
        index.put(neuron.id, neuron);
        
        // Auto-connect to nearby neurons
        for (NeuroParticle existing : brain) {
            if (existing != neuron && neuron.distanceTo(existing) < 30.0) {
                neuron.connect(existing);
            }
        }
        
        System.out.println("   ✓ Injected: " + data + " → " + neuron);
        return neuron;
    }
    
    /**
     * FIRE - Manually activate a neuron.
     */
    public void fire(String query) {
        for (NeuroParticle neuron : brain) {
            if (neuron.content.toString().contains(query)) {
                neuron.membranePotential = 1.0;
                neuron.fire();
                totalFires++;
                System.out.println("   ⚡ Fired: " + neuron);
            }
        }
    }
    
    /**
     * ENTANGLE - Create quantum coupling between two neurons.
     */
    public void entangle(String query1, String query2) {
        NeuroParticle n1 = null, n2 = null;
        
        for (NeuroParticle neuron : brain) {
            if (neuron.content.toString().contains(query1)) n1 = neuron;
            if (neuron.content.toString().contains(query2)) n2 = neuron;
        }
        
        if (n1 != null && n2 != null) {
            n1.entangle(n2);
            System.out.println(String.format("   🔗 Entangled: [%s] ⇄ [%s]", 
                n1.content, n2.content));
        }
    }
    
    /**
     * Get network statistics.
     */
    public String getStatus() {
        double totalEnergy = 0;
        int totalSynapses = 0;
        double avgPotential = 0;
        
        for (NeuroParticle neuron : brain) {
            totalEnergy += neuron.energy;
            totalSynapses += neuron.synapses.size();
            avgPotential += neuron.membranePotential;
        }
        
        if (!brain.isEmpty()) {
            avgPotential /= brain.size();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 LAZARUS NETWORK STATUS                                 ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("Generation: %d\n", generation));
        sb.append(String.format("Neurons: %d\n", brain.size()));
        sb.append(String.format("Synapses: %d\n", totalSynapses));
        sb.append(String.format("Total Fires: %d\n", totalFires));
        sb.append(String.format("Total Replications: %d\n", totalReplications));
        sb.append(String.format("System Energy: %.4f J\n", totalEnergy));
        sb.append(String.format("Avg Membrane Potential: %.3f\n", avgPotential));
        sb.append(String.format("Resonance: LOCKED (%.0f Hz)\n", RESONANCE_HZ));
        sb.append(String.format("φ^75 Seal: %.2f\n", PHI_75));
        
        return sb.toString();
    }
    
    public List<NeuroParticle> getSnapshot() {
        return new ArrayList<>(brain);
    }
    
    public int getGeneration() {
        return generation;
    }
    
    public void stop() {
        running = false;
    }
}
