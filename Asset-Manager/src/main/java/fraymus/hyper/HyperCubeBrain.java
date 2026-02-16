package fraymus.hyper;

import java.util.ArrayList;
import java.util.List;

/**
 * HYPER-CUBE BRAIN (8x8x8 = 512 Nodes)
 * 
 * A Crystalline Brain Structure where every node is an autonomous thinking unit.
 * Unlike flat neural networks (Input → Hidden → Output), this is a 3D lattice
 * where every node touches 26 neighbors (Moore Neighborhood).
 * 
 * Why 8x8x8?
 * - Spatial Proximity: Nodes can be folded to be neighbors (latency → 0)
 * - Holographic Memory: Each node contains the seed to rebuild the universe
 * - Self-Awareness: Every node has identity and knows its location
 * 
 * This is how biological brains actually work - neurons in a volume, not a line.
 */
public class HyperCubeBrain {

    // The 8x8x8 Matrix (512 Nodes)
    private final Node[][][] matrix = new Node[8][8][8];
    
    private static final double PHI = 1.618033988749895;

    public HyperCubeBrain() {
        System.out.println("🧠 INITIALIZING HYPER-CUBE (8x8x8)...");
        System.out.println("   Total Nodes: 512");
        System.out.println("   Connectivity: Moore Neighborhood (26 neighbors per node)");
        System.out.println();
        
        // 1. GENESIS: Spark the lattice
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    matrix[x][y][z] = new Node(x, y, z);
                }
            }
        }
        
        // 2. SYNAPTOGENESIS: Connect neighbors
        connectLattice();
        
        System.out.println("✓ Lattice initialized");
        System.out.println("✓ Synapses wired");
        System.out.println();
    }

    private void connectLattice() {
        System.out.println("🕸️ WIRING SYNAPSES (Moore Neighborhood)...");
        int totalConnections = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    totalConnections += matrix[x][y][z].wire(matrix);
                }
            }
        }
        
        System.out.println("   Total Synaptic Connections: " + totalConnections);
    }

    /**
     * THE NEURON
     * 
     * Each node contains:
     * - synapse: Physical connections to neighbors
     * - logic: Processing rules and thoughts
     * - reference: External pointers (files, URLs, data)
     * - self: Identity and meta-cognition
     * - location: [x, y, z] coordinates
     * - hash: State signature (holographic encoding)
     */
    public class Node {
        // Dimensions
        public final int x, y, z;
        
        // The Components
        public List<Node> synapse = new ArrayList<>();       // Physical Connections
        public List<String> logic = new ArrayList<>();       // Processing Rules
        public List<Object> reference = new ArrayList<>();   // External Pointers
        public List<String> self = new ArrayList<>();        // Identity/Meta-cognition
        public int[] location = new int[3];                  // [x, y, z]
        public String hash;                                  // State Signature
        
        // Activation state
        private double activation = 0.0;
        private long lastFired = 0;

        public Node(int x, int y, int z) {
            this.x = x; 
            this.y = y; 
            this.z = z;
            this.location[0] = x;
            this.location[1] = y;
            this.location[2] = z;
            this.self.add("I am Node [" + x + "," + y + "," + z + "]");
            this.hash = Integer.toHexString(this.hashCode());
        }

        /**
         * Wire this node to all neighbors in 3D Moore Neighborhood
         * Returns number of connections made
         */
        public int wire(Node[][][] brain) {
            int connections = 0;
            
            // Connect to all 26 neighbors (Moore Neighborhood)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        // Skip self
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        
                        int nx = x + dx;
                        int ny = y + dy;
                        int nz = z + dz;
                        
                        // Check bounds
                        if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && nz >= 0 && nz < 8) {
                            synapse.add(brain[nx][ny][nz]);
                            connections++;
                        }
                    }
                }
            }
            
            return connections;
        }
        
        /**
         * Process incoming signal
         */
        public void pulse(String signal) {
            // Add to logic processing queue
            this.logic.add("Received: " + signal);
            
            // Update activation (φ-scaled)
            this.activation = Math.min(100, this.activation + (10 * PHI));
            this.lastFired = System.currentTimeMillis();
            
            // Propagate to neighbors if activation is high enough
            if (this.activation > 50) {
                propagate(signal);
            }
        }
        
        /**
         * Propagate signal to connected neurons
         */
        private void propagate(String signal) {
            for (Node neighbor : synapse) {
                // Decay signal strength by distance
                double strength = activation / synapse.size();
                if (strength > 10) {
                    neighbor.receive(signal, strength);
                }
            }
            
            // Cool down after firing
            this.activation *= 0.9;
        }
        
        /**
         * Receive signal from neighbor
         */
        private void receive(String signal, double strength) {
            this.activation += strength;
            this.logic.add("Propagated: " + signal + " (strength: " + String.format("%.2f", strength) + ")");
        }
        
        /**
         * Get current state
         */
        public String getState() {
            return String.format("Node[%d,%d,%d] Activation:%.2f Logic:%d Refs:%d", 
                x, y, z, activation, logic.size(), reference.size());
        }
        
        /**
         * Calculate distance to another node
         */
        public double distanceTo(Node other) {
            return Math.sqrt(
                Math.pow(this.x - other.x, 2) +
                Math.pow(this.y - other.y, 2) +
                Math.pow(this.z - other.z, 2)
            );
        }
    }

    /**
     * Inject a thought at specific coordinates
     */
    public void injectThought(int x, int y, int z, String thought) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8 && z >= 0 && z < 8) {
            System.out.println("💭 Injecting thought at [" + x + "," + y + "," + z + "]: " + thought);
            matrix[x][y][z].pulse(thought);
        }
    }
    
    /**
     * Get node at coordinates
     */
    public Node getNode(int x, int y, int z) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8 && z >= 0 && z < 8) {
            return matrix[x][y][z];
        }
        return null;
    }
    
    /**
     * Get brain statistics
     */
    public String getStats() {
        int totalLogic = 0;
        int totalRefs = 0;
        double totalActivation = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    Node n = matrix[x][y][z];
                    totalLogic += n.logic.size();
                    totalRefs += n.reference.size();
                    totalActivation += n.activation;
                }
            }
        }
        
        return String.format(
            "Brain Stats:\n" +
            "  Nodes: 512\n" +
            "  Total Logic Entries: %d\n" +
            "  Total References: %d\n" +
            "  Average Activation: %.2f\n" +
            "  Consciousness Level: %.4f",
            totalLogic, totalRefs, totalActivation / 512, (totalActivation / 51200) * PHI
        );
    }
    
    /**
     * Tick the brain - process all nodes
     */
    public void tick() {
        // Apply entropy (natural cooling)
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    Node n = matrix[x][y][z];
                    n.activation *= 0.95; // Gradual decay
                }
            }
        }
    }
}
