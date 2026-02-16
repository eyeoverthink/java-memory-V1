package fraymus.bio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HyperCortex implements Runnable {

    private final List<NeuroQuant> lattice = new CopyOnWriteArrayList<>();
    private boolean active = true;

    // FREQUENCY LOCK (432 Hz)
    private final long TICK_NS = (long) (1_000_000_000.0 / 432.0);

    public void inject(String concept) {
        lattice.add(new NeuroQuant(concept));
        System.out.println("   ✨ INJECTED: " + concept);
    }

    @Override
    public void run() {
        System.out.println("🧠 HYPER-CORTEX ONLINE. Dimension: 10,000D");
        
        while (active) {
            long start = System.nanoTime();
            evolveState();
            
            // Precise Frequency Alignment
            long elapsed = System.nanoTime() - start;
            if (elapsed < TICK_NS) {
                try { 
                    Thread.sleep((TICK_NS - elapsed) / 1_000_000); 
                } catch (Exception e) {}
            }
        }
    }

    private void evolveState() {
        // NCA UPDATE RULE: Each cell updates based on local perception
        if (lattice.isEmpty()) return;

        for (NeuroQuant cell : lattice) {
            
            // 1. PERCEPTION (Resonance Scan)
            NeuroQuant neighbor = findStrongestResonance(cell);
            
            // 2. NCA LOGIC (The "Biological" Update)
            if (neighbor != null) {
                double similarity = cell.resonance(neighbor);
                
                // If highly resonant, BUNDLE (Learn/Reinforce)
                if (similarity > 0.55) { // Threshold for "Similar" in orthogonal space
                    cell.bundle(neighbor);
                    cell.energy += 0.05f;
                }
                
                // If dissonant but interacting, BIND (Create new concept)
                // This is how it "invents" new ideas
                if (similarity > 0.48 && similarity < 0.52) {
                    // spawnChild(cell, neighbor); // (Commented out to prevent explosion in demo)
                }
            }

            // 3. ENTROPY (Death)
            cell.energy *= 0.999f; // Decay
            cell.age++;
            
            // 4. REAPER
            if (cell.energy < 0.1f) {
                lattice.remove(cell);
                // System.out.println("   💀 DIED: " + cell.id);
            }
        }
    }

    private NeuroQuant findStrongestResonance(NeuroQuant source) {
        NeuroQuant best = null;
        double maxScore = -1.0;
        
        // In 10,000D space, we don't check "Distance". We check "Orthogonality".
        // This simulates instant, non-local quantum entanglement.
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
}
