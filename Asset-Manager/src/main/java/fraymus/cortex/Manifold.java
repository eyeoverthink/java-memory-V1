package fraymus.cortex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 🧬 THE MANIFOLD - Gen 124
 * The 11-Dimensional Calabi-Yau shape that holds the nodes.
 * 
 * Physics Engine:
 * - Spring Forces (Hooke's Law) pull connected nodes together
 * - Repulsion (Coulomb Force) pushes disconnected nodes apart
 * - Damping prevents infinite oscillation
 * - Gravity pulls high-resonance nodes toward center
 * 
 * "The brain breathes. It expands and contracts with thought."
 */
public class Manifold {
    
    private static final double PHI = 1.6180339887;
    private static final double E = 2.7182818284;
    
    // Physics constants
    private static final double SPRING_K = 0.05;        // Spring stiffness
    private static final double SPRING_REST = 8.0;      // Rest length
    private static final double REPULSION_K = 500.0;    // Repulsion strength
    private static final double DAMPING = 0.85;         // Velocity damping
    private static final double GRAVITY_K = 0.01;       // Center gravity
    private static final double DECAY_RATE = 0.995;     // Per-pulse decay
    
    private List<PhiNode> cortex = new CopyOnWriteArrayList<>();
    private long generation = 0;
    private double totalEnergy = 0;
    
    /**
     * INJECT - Add new data to the manifold
     */
    public PhiNode inject(Object data) {
        return inject(data, null);
    }
    
    public PhiNode inject(Object data, String label) {
        PhiNode neuron = label != null ? new PhiNode(data, label) : new PhiNode(data);
        cortex.add(neuron);
        
        // AUTO-ASSOCIATION: Connect to semantically similar nodes
        for (PhiNode existing : cortex) {
            if (existing == neuron) continue;
            
            double similarity = neuron.similarityTo(existing);
            if (similarity > 0.2) {
                // Strong similarity = strong connection
                neuron.synapse(existing, similarity * PHI);
                
                // Move new node toward similar nodes
                neuron.x = existing.x + (Math.random() - 0.5) * 20;
                neuron.y = existing.y + (Math.random() - 0.5) * 20;
                neuron.z = existing.z + (Math.random() - 0.5) * 20;
            }
        }
        
        neuron.activate(0.618); // Initial activation
        return neuron;
    }
    
    /**
     * PULSE - One simulation cycle
     * Applies all forces and updates positions
     */
    public void pulse() {
        generation++;
        totalEnergy = 0;
        
        // Phase 1: Calculate forces
        for (PhiNode a : cortex) {
            double fx = 0, fy = 0, fz = 0;
            
            for (PhiNode b : cortex) {
                if (a == b) continue;
                
                double dist = a.distanceTo(b);
                if (dist < 0.1) dist = 0.1; // Prevent singularity
                
                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double dz = b.z - a.z;
                
                // Normalize direction
                double nx = dx / dist;
                double ny = dy / dist;
                double nz = dz / dist;
                
                if (a.connections.contains(b)) {
                    // SPRING FORCE (Hooke's Law)
                    // F = -k * (x - rest)
                    int idx = a.connections.indexOf(b);
                    double weight = a.weights.get(idx);
                    double displacement = dist - SPRING_REST * weight;
                    double springForce = SPRING_K * displacement * PHI;
                    
                    fx += nx * springForce;
                    fy += ny * springForce;
                    fz += nz * springForce;
                } else {
                    // REPULSION FORCE (Coulomb-like)
                    // F = k / d²
                    if (dist < 50) {
                        double repulsion = -REPULSION_K / (dist * dist);
                        fx += nx * repulsion;
                        fy += ny * repulsion;
                        fz += nz * repulsion;
                    }
                }
            }
            
            // CENTER GRAVITY - High resonance pulls toward origin
            double centerDist = Math.sqrt(a.x*a.x + a.y*a.y + a.z*a.z);
            if (centerDist > 0.1) {
                double gravity = GRAVITY_K * a.resonance * centerDist;
                fx -= (a.x / centerDist) * gravity;
                fy -= (a.y / centerDist) * gravity;
                fz -= (a.z / centerDist) * gravity;
            }
            
            // Phase 2: Update velocity (F = ma, a = F/m)
            a.vx = (a.vx + fx / a.mass) * DAMPING;
            a.vy = (a.vy + fy / a.mass) * DAMPING;
            a.vz = (a.vz + fz / a.mass) * DAMPING;
            
            // Phase 3: Update position
            a.x += a.vx;
            a.y += a.vy;
            a.z += a.vz;
            
            // Track total kinetic energy
            totalEnergy += 0.5 * a.mass * (a.vx*a.vx + a.vy*a.vy + a.vz*a.vz);
            
            // Phase 4: Decay
            a.decay(DECAY_RATE);
        }
        
        // Prune dead nodes (resonance too low)
        cortex.removeIf(n -> n.resonance < 0.001 && n.accessCount < 3);
    }
    
    /**
     * QUERY - Find nodes matching a pattern
     * Activates matching nodes and returns them sorted by resonance
     */
    public List<PhiNode> query(String pattern) {
        String p = pattern.toLowerCase();
        List<PhiNode> matches = new ArrayList<>();
        
        for (PhiNode node : cortex) {
            if (node.data.toString().toLowerCase().contains(p)) {
                node.activate(0.5);
                matches.add(node);
            }
        }
        
        // Strengthen connections between activated nodes
        for (int i = 0; i < matches.size(); i++) {
            for (int j = i + 1; j < matches.size(); j++) {
                PhiNode a = matches.get(i);
                PhiNode b = matches.get(j);
                if (a.connections.contains(b)) {
                    a.strengthen(b, 0.1);
                } else {
                    a.synapse(b, 0.3);
                }
            }
        }
        
        return matches.stream()
            .sorted((a, b) -> Double.compare(b.resonance, a.resonance))
            .collect(Collectors.toList());
    }
    
    /**
     * FUSE - Merge two concepts when they collide
     */
    public PhiNode fuse(PhiNode a, PhiNode b) {
        String fusedData = a.data.toString() + " ⊕ " + b.data.toString();
        PhiNode hybrid = new PhiNode(fusedData, "FUSION-" + generation);
        
        // Inherit position (midpoint)
        hybrid.x = (a.x + b.x) / 2;
        hybrid.y = (a.y + b.y) / 2;
        hybrid.z = (a.z + b.z) / 2;
        
        // Inherit connections from both parents
        for (PhiNode conn : a.connections) {
            if (conn != b) hybrid.synapse(conn, 0.618);
        }
        for (PhiNode conn : b.connections) {
            if (conn != a && !hybrid.connections.contains(conn)) {
                hybrid.synapse(conn, 0.618);
            }
        }
        
        // Inherit combined resonance
        hybrid.resonance = Math.min(1.0, a.resonance + b.resonance);
        hybrid.mass = a.mass + b.mass;
        
        // Remove parents, add child
        cortex.remove(a);
        cortex.remove(b);
        cortex.add(hybrid);
        
        return hybrid;
    }
    
    /**
     * DETECT COLLISIONS - Find nodes that are very close
     */
    public List<PhiNode[]> detectCollisions(double threshold) {
        List<PhiNode[]> collisions = new ArrayList<>();
        for (int i = 0; i < cortex.size(); i++) {
            for (int j = i + 1; j < cortex.size(); j++) {
                PhiNode a = cortex.get(i);
                PhiNode b = cortex.get(j);
                if (a.distanceTo(b) < threshold) {
                    collisions.add(new PhiNode[]{a, b});
                }
            }
        }
        return collisions;
    }
    
    // Getters
    public List<PhiNode> getSnapshot() { return new ArrayList<>(cortex); }
    public long getGeneration() { return generation; }
    public double getTotalEnergy() { return totalEnergy; }
    public int size() { return cortex.size(); }
    
    /**
     * STATUS - Human-readable state
     */
    public String status() {
        double avgResonance = cortex.stream()
            .mapToDouble(n -> n.resonance)
            .average().orElse(0);
        
        int totalConnections = cortex.stream()
            .mapToInt(n -> n.connections.size())
            .sum() / 2;
        
        return String.format(
            "🧬 MANIFOLD STATE\n" +
            "   Generation: %d\n" +
            "   Nodes: %d | Synapses: %d\n" +
            "   Avg Resonance: %.3f\n" +
            "   Total Energy: %.2f\n" +
            "   φ-Coherence: %.3f",
            generation, cortex.size(), totalConnections,
            avgResonance, totalEnergy, avgResonance * PHI
        );
    }
}
