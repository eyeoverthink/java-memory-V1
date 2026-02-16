package fraymus.bio;

import fraymus.core.AuditLog;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

/**
 * 🧠 THE HYPER-CORTEX
 * "A 10,000-dimensional neural lattice with biological growth."
 * 
 * This replaces traditional physics engines with Neural Cellular Automata (NCA).
 * Instead of simple gravity, cells grow, die, and mutate based on local perception.
 * 
 * Revolutionary Features:
 * 1. BIOLOGICAL GROWTH: Cells don't just move - they grow functionality
 * 2. HOLOGRAPHIC MEMORY: 10,000D hypervectors for fault-tolerant storage
 * 3. FREQUENCY LOCK: 432 Hz harmonic alignment (DNA repair frequency)
 * 4. ENTANGLEMENT BIRTH: New cells emerge from resonance patterns
 * 
 * This is how biological brains actually work - not graphs, but living tissue.
 */
public class HyperCortex implements Runnable {

    private final List<NeuroQuant> lattice = new CopyOnWriteArrayList<>();
    private final AuditLog auditLog;
    private volatile boolean active = false;
    
    // FREQUENCY LOCK (432 Hz - DNA Repair Frequency)
    private static final double FREQUENCY_HZ = 432.0;
    private static final long TICK_NS = (long) (1_000_000_000.0 / FREQUENCY_HZ);
    
    // STATISTICS
    private long tickCount = 0;
    private int birthCount = 0;
    private int deathCount = 0;
    
    // PARAMETERS
    private static final double RESONANCE_THRESHOLD_HIGH = 0.8; // Bundle threshold
    private static final double RESONANCE_THRESHOLD_MID = 0.4;  // Bind threshold
    private static final double RESONANCE_THRESHOLD_LOW = 0.6;  // Bind upper bound
    private static final int MAX_CELLS = 1000; // Prevent runaway growth

    public HyperCortex(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    /**
     * Start the cortex
     */
    public void start() {
        if (active) return;
        active = true;
        new Thread(this, "HyperCortex").start();
    }

    /**
     * Stop the cortex
     */
    public void stop() {
        active = false;
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧠 HYPER-CORTEX ONLINE                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Dimension: 10,000D");
        System.out.println("Frequency: 432 Hz (DNA Repair)");
        System.out.println("Mode: Neural Cellular Automata");
        System.out.println();
        
        auditLog.log("hypercortex_started", "10000D NCA");
        
        while (active) {
            long start = System.nanoTime();
            
            evolveState();
            tickCount++;
            
            // Precise Frequency Alignment (432 Hz)
            long elapsed = System.nanoTime() - start;
            if (elapsed < TICK_NS) {
                try {
                    long sleepNs = TICK_NS - elapsed;
                    Thread.sleep(sleepNs / 1_000_000, (int)(sleepNs % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("🧠 HYPER-CORTEX OFFLINE");
        auditLog.log("hypercortex_stopped", getStats());
    }

    /**
     * NCA UPDATE RULE: Each cell updates based on local perception
     * 
     * This is the "biological" update - cells look at neighbors and decide:
     * - Should I grow? (high resonance)
     * - Should I die? (low energy)
     * - Should I mutate? (medium resonance)
     */
    private void evolveState() {
        List<NeuroQuant> toAdd = new ArrayList<>();
        List<NeuroQuant> toRemove = new ArrayList<>();
        
        for (NeuroQuant cell : lattice) {
            
            // 1. PERCEPTION (Resonance Scan)
            NeuroQuant neighbor = findStrongestResonance(cell);
            
            if (neighbor != null) {
                double similarity = cell.resonance(neighbor);
                
                // 2. NCA LOGIC (The "Biological" Update)
                
                // HIGH RESONANCE: BUNDLE (Learn/Memorize)
                if (similarity > RESONANCE_THRESHOLD_HIGH) {
                    cell.bundle(neighbor);
                    cell.energy += 0.1f;
                    
                    // If very energetic, reproduce
                    if (cell.shouldReproduce() && lattice.size() < MAX_CELLS) {
                        NeuroQuant offspring = cell.clone();
                        toAdd.add(offspring);
                        birthCount++;
                    }
                }
                
                // MEDIUM RESONANCE: BIND (Create new concept)
                else if (similarity > RESONANCE_THRESHOLD_MID && 
                         similarity < RESONANCE_THRESHOLD_LOW) {
                    if (lattice.size() < MAX_CELLS) {
                        NeuroQuant child = spawnChild(cell, neighbor);
                        toAdd.add(child);
                        birthCount++;
                    }
                }
                
                // LOW RESONANCE: Compete for resources
                else if (similarity < 0.3) {
                    cell.energy -= 0.05f;
                }
            }
            
            // 3. NCA UPDATE (Cellular Automata)
            NeuroQuant[] neighbors = findNearestNeighbors(cell, 5);
            cell.ncaUpdate(neighbors);
            
            // 4. ENTROPY (Death)
            if (!cell.isAlive()) {
                toRemove.add(cell);
                deathCount++;
            }
        }
        
        // Apply births and deaths
        lattice.addAll(toAdd);
        lattice.removeAll(toRemove);
    }

    /**
     * Find strongest resonance (non-local quantum-like search)
     * 
     * In 10,000D space, we don't check "Distance". We check "Orthogonality".
     * This simulates instant, non-local quantum entanglement.
     */
    private NeuroQuant findStrongestResonance(NeuroQuant source) {
        NeuroQuant best = null;
        double maxScore = -1.0;
        
        for (NeuroQuant target : lattice) {
            if (source == target) continue;
            
            double score = source.resonance(target);
            if (score > maxScore) {
                maxScore = score;
                best = target;
            }
        }
        
        return best;
    }

    /**
     * Find nearest neighbors in spatial coordinates
     * (for NCA local perception)
     */
    private NeuroQuant[] findNearestNeighbors(NeuroQuant source, int count) {
        List<NeuroQuant> neighbors = new ArrayList<>();
        
        for (NeuroQuant target : lattice) {
            if (source == target) continue;
            
            double dx = source.x - target.x;
            double dy = source.y - target.y;
            double dz = source.z - target.z;
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            
            if (dist < 20.0) { // Neighborhood radius
                neighbors.add(target);
                if (neighbors.size() >= count) break;
            }
        }
        
        return neighbors.toArray(new NeuroQuant[0]);
    }

    /**
     * ENTANGLEMENT BIRTH: Spawn child from two parents
     * 
     * The child inherits the Binding (XOR) of parents.
     * This is mathematically unique but reversible.
     */
    private NeuroQuant spawnChild(NeuroQuant a, NeuroQuant b) {
        NeuroQuant child = new NeuroQuant("SYNTHESIS_" + System.nanoTime());
        
        // The child's hypervector is the XOR of parents
        child.hyperVector = (java.util.BitSet) a.hyperVector.clone();
        child.bind(b);
        
        // Spatial position between parents
        child.x = (a.x + b.x) / 2.0 + (Math.random() - 0.5) * 5;
        child.y = (a.y + b.y) / 2.0 + (Math.random() - 0.5) * 5;
        child.z = (a.z + b.z) / 2.0 + (Math.random() - 0.5) * 5;
        
        // Average energy
        child.energy = (a.energy + b.energy) / 2.0f;
        
        if (tickCount % 100 == 0) {
            System.out.println("⚡ GENESIS: " + a.id + " + " + b.id + " → " + child.id);
        }
        
        return child;
    }

    /**
     * INJECT: Add a concept to the lattice
     */
    public void inject(String concept) {
        NeuroQuant cell = new NeuroQuant(concept);
        lattice.add(cell);
        System.out.println("💉 INJECTED: " + concept);
        auditLog.log("hypercortex_inject", concept);
    }

    /**
     * QUERY: Find what resonates with a concept
     */
    public String query(String concept) {
        NeuroQuant query = new NeuroQuant(concept);
        NeuroQuant best = findStrongestResonance(query);
        
        if (best != null) {
            double resonance = query.resonance(best);
            return best.id + " (resonance: " + String.format("%.2f", resonance) + ")";
        }
        
        return "NO RESONANCE FOUND";
    }

    /**
     * Get cell count
     */
    public int getCellCount() {
        return lattice.size();
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Ticks: %d, Cells: %d, Births: %d, Deaths: %d, Frequency: %.1f Hz",
            tickCount, lattice.size(), birthCount, deathCount, FREQUENCY_HZ
        );
    }

    /**
     * Get detailed state
     */
    public String getDetailedState() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║         HYPER-CORTEX STATE                                    ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n");
        sb.append("\n");
        sb.append("Dimension: 10,000D\n");
        sb.append("Frequency: 432 Hz\n");
        sb.append("Ticks: ").append(tickCount).append("\n");
        sb.append("Cells: ").append(lattice.size()).append("\n");
        sb.append("Births: ").append(birthCount).append("\n");
        sb.append("Deaths: ").append(deathCount).append("\n");
        sb.append("\n");
        
        if (!lattice.isEmpty()) {
            sb.append("Sample Cells:\n");
            int count = 0;
            for (NeuroQuant cell : lattice) {
                if (count++ >= 5) break;
                sb.append("  ").append(cell.getState()).append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * Check if running
     */
    public boolean isRunning() {
        return active;
    }

    /**
     * Get snapshot of current cells (for network broadcasting)
     */
    public List<NeuroQuant> getSnapshot() {
        return new ArrayList<>(lattice);
    }
}
