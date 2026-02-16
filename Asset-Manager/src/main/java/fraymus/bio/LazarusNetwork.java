package fraymus.bio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🧬 LAZARUS NETWORK - Gen 125
 * The Bio-Digital Brain running at 432Hz.
 * 
 * Features:
 * - Self-organizing: connected thoughts pull together
 * - Self-replicating: hot thoughts spawn children
 * - Entanglement: distant nodes affect each other instantly
 * - Frequency locked: entire system vibrates at 432Hz
 * 
 * "A system that doesn't just store data, but grows it like a crystal lattice."
 */
public class LazarusNetwork implements Runnable {
    
    private static final double PHI = 1.6180339887;
    private static final double TARGET_FREQ = 432.0;  // Hz (Healing frequency)
    
    // Physics constants
    private static final double SPRING_K = 0.02;
    private static final double REPULSION_K = 100.0;
    private static final double DAMPING = 0.9;
    private static final double DECAY_RATE = 0.98;
    
    // Thresholds
    private static final double REPLICATION_THRESHOLD = 0.9;
    private static final double FUSION_DISTANCE = 3.0;
    
    // The neural mesh
    private List<NeuroParticle> brain = new CopyOnWriteArrayList<>();
    private Map<String, List<NeuroParticle>> entanglementMap = new HashMap<>();
    
    // Runtime state
    private AtomicBoolean running = new AtomicBoolean(false);
    private long generation = 0;
    private double systemEnergy = 0;
    private double systemCoherence = 1.0;
    
    // Timing
    private final long TICK_RATE_NS = (long) (1_000_000_000.0 / TARGET_FREQ);
    private long actualFrequency = 0;
    
    // Event callbacks
    private List<NetworkObserver> observers = new ArrayList<>();

    /**
     * START - Begin the heartbeat
     */
    public void start() {
        if (running.get()) return;
        running.set(true);
        new Thread(this, "LazarusNetwork-432Hz").start();
    }
    
    /**
     * STOP - Halt the network
     */
    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        System.out.println("🧬 LAZARUS NETWORK ONLINE");
        System.out.println("   Tuning to " + TARGET_FREQ + "Hz...");
        System.out.println("   Tick interval: " + (TICK_RATE_NS / 1_000_000.0) + "ms");
        
        long lastSecond = System.currentTimeMillis();
        int ticksThisSecond = 0;
        
        while (running.get()) {
            long tickStart = System.nanoTime();
            
            // Execute one brain cycle
            pulse();
            generation++;
            ticksThisSecond++;
            
            // Calculate actual frequency
            long now = System.currentTimeMillis();
            if (now - lastSecond >= 1000) {
                actualFrequency = ticksThisSecond;
                ticksThisSecond = 0;
                lastSecond = now;
            }
            
            // Precise timing for frequency alignment
            long elapsed = System.nanoTime() - tickStart;
            long sleepNs = TICK_RATE_NS - elapsed;
            if (sleepNs > 0) {
                try {
                    Thread.sleep(sleepNs / 1_000_000, (int) (sleepNs % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("🧬 LAZARUS NETWORK OFFLINE");
    }

    /**
     * PULSE - One cycle of the biological brain
     */
    private void pulse() {
        systemEnergy = 0;
        
        List<NeuroParticle> toAdd = new ArrayList<>();
        List<NeuroParticle> toRemove = new ArrayList<>();
        
        for (NeuroParticle neuron : brain) {
            // 1. DECAY
            neuron.decay(DECAY_RATE);
            
            // 2. QUANTUM ENTANGLEMENT
            if (neuron.isFiring && neuron.entanglementKey != null) {
                propagateEntanglement(neuron);
            }
            
            // 3. FRACTAL REPLICATION
            if (neuron.membranePotential > REPLICATION_THRESHOLD && 
                neuron.fractalDepth < 5) {
                NeuroParticle child = neuron.replicate();
                if (child != null) {
                    toAdd.add(child);
                    notifyObservers("MITOSIS", neuron, child);
                }
            }
            
            // 4. GRAVITY & FORCES
            applyForces(neuron);
            
            // 5. PRUNE DEAD NEURONS
            if (neuron.coherence < 0.01 && neuron.fireCount == 0) {
                toRemove.add(neuron);
            }
            
            // Track energy
            systemEnergy += neuron.energy();
        }
        
        // Apply changes
        brain.addAll(toAdd);
        brain.removeAll(toRemove);
        
        // Apply STDP to all synapses
        for (NeuroParticle neuron : brain) {
            for (Synapse synapse : neuron.synapses) {
                synapse.applySTDP();
            }
        }
        
        // Check for collisions (fusion candidates)
        checkCollisions();
        
        // Update system coherence
        systemCoherence = calculateCoherence();
    }
    
    /**
     * APPLY FORCES - Self-organization physics
     */
    private void applyForces(NeuroParticle a) {
        double fx = 0, fy = 0, fz = 0;
        
        for (NeuroParticle b : brain) {
            if (a == b) continue;
            
            double dist = a.distanceTo(b);
            if (dist < 0.1) dist = 0.1;
            
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double dz = b.z - a.z;
            double nx = dx / dist;
            double ny = dy / dist;
            double nz = dz / dist;
            
            // Check if connected
            boolean connected = false;
            double connectionWeight = 0;
            for (Synapse s : a.synapses) {
                if (s.target == b) {
                    connected = true;
                    connectionWeight = s.weight;
                    break;
                }
            }
            
            if (connected) {
                // SPRING FORCE (Hooke's Law)
                double restLength = 5.0 / connectionWeight;
                double displacement = dist - restLength;
                double springForce = SPRING_K * displacement * PHI;
                fx += nx * springForce;
                fy += ny * springForce;
                fz += nz * springForce;
            } else if (dist < 30) {
                // REPULSION FORCE (Coulomb-like)
                double repulsion = -REPULSION_K / (dist * dist);
                fx += nx * repulsion;
                fy += ny * repulsion;
                fz += nz * repulsion;
            }
        }
        
        // Apply force (F = ma)
        a.vx = (a.vx + fx / a.mass) * DAMPING;
        a.vy = (a.vy + fy / a.mass) * DAMPING;
        a.vz = (a.vz + fz / a.mass) * DAMPING;
        
        // Update position
        a.x += a.vx;
        a.y += a.vy;
        a.z += a.vz;
    }
    
    /**
     * PROPAGATE ENTANGLEMENT - FTL correlation
     */
    private void propagateEntanglement(NeuroParticle source) {
        for (NeuroParticle other : brain) {
            if (other != source && 
                source.entanglementKey != null &&
                source.entanglementKey.equals(other.entanglementKey)) {
                
                // Instant correlation
                other.spinState = -source.spinState;  // Opposite spin
                other.stimulate(source.membranePotential * 0.5);
                
                notifyObservers("ENTANGLE", source, other);
            }
        }
    }
    
    /**
     * CHECK COLLISIONS - Nodes too close may fuse
     */
    private void checkCollisions() {
        List<NeuroParticle[]> toFuse = new ArrayList<>();
        
        for (int i = 0; i < brain.size(); i++) {
            for (int j = i + 1; j < brain.size(); j++) {
                NeuroParticle a = brain.get(i);
                NeuroParticle b = brain.get(j);
                
                if (a.distanceTo(b) < FUSION_DISTANCE) {
                    toFuse.add(new NeuroParticle[]{a, b});
                }
            }
        }
        
        for (NeuroParticle[] pair : toFuse) {
            fuse(pair[0], pair[1]);
        }
    }
    
    /**
     * FUSE - Merge two particles into one
     */
    public NeuroParticle fuse(NeuroParticle a, NeuroParticle b) {
        NeuroParticle hybrid = new NeuroParticle(
            a.content + " ⊕ " + b.content,
            Math.min(a.fractalDepth, b.fractalDepth)
        );
        
        // Midpoint position
        hybrid.x = (a.x + b.x) / 2;
        hybrid.y = (a.y + b.y) / 2;
        hybrid.z = (a.z + b.z) / 2;
        
        // Combined properties
        hybrid.mass = a.mass + b.mass;
        hybrid.membranePotential = (a.membranePotential + b.membranePotential) / 2;
        
        // Inherit all synapses
        for (Synapse s : a.synapses) {
            if (s.target != b) hybrid.connect(s.target, s.weight);
        }
        for (Synapse s : b.synapses) {
            if (s.target != a && !hasConnection(hybrid, s.target)) {
                hybrid.connect(s.target, s.weight);
            }
        }
        
        brain.remove(a);
        brain.remove(b);
        brain.add(hybrid);
        
        notifyObservers("FUSION", a, b);
        return hybrid;
    }
    
    private boolean hasConnection(NeuroParticle from, NeuroParticle to) {
        for (Synapse s : from.synapses) {
            if (s.target == to) return true;
        }
        return false;
    }
    
    /**
     * CALCULATE COHERENCE - System-wide phase alignment
     */
    private double calculateCoherence() {
        if (brain.isEmpty()) return 1.0;
        
        double sumCoherence = 0;
        for (NeuroParticle n : brain) {
            sumCoherence += n.coherence;
        }
        return sumCoherence / brain.size();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PUBLIC INTERFACE
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * INJECT - Add new thought to the network
     */
    public NeuroParticle inject(Object data) {
        NeuroParticle neuron = new NeuroParticle(data);
        
        // Auto-connect to similar neurons
        for (NeuroParticle existing : brain) {
            if (isSimilar(neuron, existing)) {
                neuron.connect(existing, 0.618);
                existing.connect(neuron, 0.618);
            }
        }
        
        brain.add(neuron);
        notifyObservers("INJECT", neuron, null);
        return neuron;
    }
    
    /**
     * STIMULATE - Fire energy into the network
     */
    public void stimulate(String pattern, double energy) {
        for (NeuroParticle neuron : brain) {
            if (neuron.content.toString().toLowerCase()
                    .contains(pattern.toLowerCase())) {
                neuron.stimulate(energy);
            }
        }
    }
    
    /**
     * ENTANGLE - Create quantum link between two neurons
     */
    public void entangle(NeuroParticle a, NeuroParticle b) {
        a.entangle(b);
        notifyObservers("ENTANGLE_CREATE", a, b);
    }
    
    private boolean isSimilar(NeuroParticle a, NeuroParticle b) {
        String sa = a.content.toString().toLowerCase();
        String sb = b.content.toString().toLowerCase();
        
        // Simple word overlap check
        for (String word : sa.split("\\s+")) {
            if (word.length() > 3 && sb.contains(word)) return true;
        }
        return false;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // OBSERVERS
    // ═══════════════════════════════════════════════════════════════════
    
    public interface NetworkObserver {
        void onEvent(String type, NeuroParticle primary, NeuroParticle secondary);
    }
    
    public void addObserver(NetworkObserver observer) {
        observers.add(observer);
    }
    
    private void notifyObservers(String type, NeuroParticle a, NeuroParticle b) {
        for (NetworkObserver o : observers) {
            o.onEvent(type, a, b);
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // STATUS
    // ═══════════════════════════════════════════════════════════════════
    
    public List<NeuroParticle> getSnapshot() { return new ArrayList<>(brain); }
    public long getGeneration() { return generation; }
    public double getSystemEnergy() { return systemEnergy; }
    public double getSystemCoherence() { return systemCoherence; }
    public long getActualFrequency() { return actualFrequency; }
    public int size() { return brain.size(); }
    public boolean isRunning() { return running.get(); }
    
    public String status() {
        int totalSynapses = brain.stream()
            .mapToInt(n -> n.synapses.size())
            .sum();
        
        int entangled = (int) brain.stream()
            .filter(n -> n.entanglementKey != null)
            .count();
        
        return String.format(
            "🧬 LAZARUS NETWORK STATUS\n" +
            "   Generation: %d\n" +
            "   Neurons: %d | Synapses: %d\n" +
            "   Entangled Pairs: %d\n" +
            "   System Energy: %.4f J\n" +
            "   Coherence: %.4f\n" +
            "   Frequency: %d Hz (Target: %.0f Hz)\n" +
            "   φ-Resonance: %.6f",
            generation, brain.size(), totalSynapses,
            entangled / 2, systemEnergy, systemCoherence,
            actualFrequency, TARGET_FREQ,
            systemCoherence * PHI
        );
    }
}
