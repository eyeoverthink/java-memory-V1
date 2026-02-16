package fraymus.cortex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 🧬 THE PARTICLE - Gen 124
 * A single point in the Calabi-Yau Manifold.
 * Each node exists at (x,y,z) coordinates in 3D thought-space.
 * 
 * "Data has Mass and Gravity. Concepts move in space."
 * F = φ * (A1 * A2 / d²)
 */
public class PhiNode {
    
    private static final double PHI = 1.6180339887;
    
    public final String id;
    public Object data;
    public String label;
    
    // SPATIAL COORDINATES (Position in the Cortex)
    public double x, y, z;
    
    // VELOCITY (For physics simulation)
    public double vx, vy, vz;
    
    // ENERGY METRICS
    public double resonance;      // 0.0 to 1.0 (activation level)
    public double mass;           // Heavier nodes resist movement
    public long lastAccess;       // Timestamp for decay calculation
    public int accessCount;       // How often this node is queried
    
    // TOPOLOGY
    public List<PhiNode> connections;
    public List<Double> weights;  // Connection strengths (Hebbian)

    public PhiNode(Object data) {
        this(data, data.toString().substring(0, Math.min(20, data.toString().length())));
    }
    
    public PhiNode(Object data, String label) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.data = data;
        this.label = label;
        this.connections = new ArrayList<>();
        this.weights = new ArrayList<>();
        
        // Initialize at random point in the Void
        this.x = (Math.random() - 0.5) * 100;
        this.y = (Math.random() - 0.5) * 100;
        this.z = (Math.random() - 0.5) * 100;
        
        // Initial velocity = 0
        this.vx = this.vy = this.vz = 0;
        
        // Phi-harmonic defaults
        this.resonance = 0.618;
        this.mass = 1.0;
        this.lastAccess = System.currentTimeMillis();
        this.accessCount = 0;
    }

    /**
     * HEBBIAN LINKING - "Neurons that fire together wire together"
     * Creates bidirectional synapse with initial weight
     */
    public void synapse(PhiNode other) {
        synapse(other, 0.5);
    }
    
    public void synapse(PhiNode other, double weight) {
        if (other == this) return;
        if (!this.connections.contains(other)) {
            this.connections.add(other);
            this.weights.add(weight);
            other.connections.add(this);
            other.weights.add(weight);
        }
    }
    
    /**
     * STRENGTHEN CONNECTION - Hebbian learning
     * Called when both nodes are activated together
     */
    public void strengthen(PhiNode other, double delta) {
        int idx = connections.indexOf(other);
        if (idx >= 0) {
            double newWeight = Math.min(1.0, weights.get(idx) + delta * PHI);
            weights.set(idx, newWeight);
            
            // Mirror on other side
            int otherIdx = other.connections.indexOf(this);
            if (otherIdx >= 0) {
                other.weights.set(otherIdx, newWeight);
            }
        }
    }
    
    /**
     * ACTIVATE - Fire this node
     * Increases resonance and propagates to connected nodes
     */
    public void activate(double energy) {
        this.resonance = Math.min(1.0, this.resonance + energy);
        this.lastAccess = System.currentTimeMillis();
        this.accessCount++;
        this.mass = 1.0 + (accessCount * 0.1); // More accessed = heavier
        
        // Propagate energy to connections (attenuated)
        for (int i = 0; i < connections.size(); i++) {
            PhiNode neighbor = connections.get(i);
            double weight = weights.get(i);
            double propagated = energy * weight * 0.3; // 30% propagation
            if (propagated > 0.01) {
                neighbor.resonance = Math.min(1.0, neighbor.resonance + propagated);
            }
        }
    }
    
    /**
     * DECAY - Memory fading over time
     */
    public void decay(double factor) {
        this.resonance *= factor;
        
        // Also decay weak connections
        for (int i = weights.size() - 1; i >= 0; i--) {
            double w = weights.get(i) * factor;
            if (w < 0.01) {
                // Prune dead synapse
                PhiNode other = connections.get(i);
                other.connections.remove(this);
                int otherIdx = other.weights.size() > i ? i : other.weights.size() - 1;
                if (otherIdx >= 0) other.weights.remove(otherIdx);
                connections.remove(i);
                weights.remove(i);
            } else {
                weights.set(i, w);
            }
        }
    }
    
    /**
     * DISTANCE - Euclidean distance to another node
     */
    public double distanceTo(PhiNode other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * SIMILARITY - Semantic similarity based on data
     */
    public double similarityTo(PhiNode other) {
        if (this.data == null || other.data == null) return 0;
        String a = this.data.toString().toLowerCase();
        String b = other.data.toString().toLowerCase();
        
        // Simple Jaccard similarity on words
        String[] wordsA = a.split("\\s+");
        String[] wordsB = b.split("\\s+");
        
        int intersection = 0;
        for (String wa : wordsA) {
            for (String wb : wordsB) {
                if (wa.equals(wb)) intersection++;
            }
        }
        
        int union = wordsA.length + wordsB.length - intersection;
        return union > 0 ? (double) intersection / union : 0;
    }
    
    @Override
    public String toString() {
        return String.format("φ[%s] (%.1f,%.1f,%.1f) R=%.2f M=%.1f C=%d",
            id, x, y, z, resonance, mass, connections.size());
    }
}
