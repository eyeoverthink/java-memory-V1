package gemini.root.hyper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.security.MessageDigest;

/**
 * HYPER-CUBE BRAIN: 8x8x8 Crystalline Neural Structure
 * 
 * 512 autonomous thinking units arranged in 3D space.
 * Each node has: Synapse, Logic, Reference, Self, Location, Hash
 * 
 * This is not a list. It's a Voxel-Based Neural Network.
 * Neurons aren't in a line; they are in a volume.
 * 
 * Traversal: O(∛N) instead of O(N)
 * Max jump distance: 21 steps corner-to-corner
 */
public class HyperCubeBrain {

    private static final double PHI = 1.618033988749895;
    
    // The 8x8x8 Matrix (512 Nodes)
    private final Node[][][] matrix = new Node[8][8][8];
    private long pulseCount = 0;
    private volatile boolean alive = false;

    public HyperCubeBrain() {
        System.out.println("🧠 INITIALIZING HYPER-CUBE (8x8x8)...");
        System.out.println("   Total Nodes: 512");
        System.out.println("   Connectivity: Moore Neighborhood (26 neighbors per node)");
        
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
        
        // 3. HASH GENESIS: Create holographic signatures
        computeHashes();
        
        System.out.println("🧠 HYPER-CUBE ONLINE: 512 nodes, fully wired");
    }

    private void connectLattice() {
        System.out.println("🕸️ WIRING SYNAPSES (Moore Neighborhood)...");
        int totalSynapses = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    totalSynapses += matrix[x][y][z].wire(matrix);
                }
            }
        }
        
        System.out.println("   Synapses created: " + totalSynapses);
    }

    private void computeHashes() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    matrix[x][y][z].computeHash();
                }
            }
        }
    }

    /**
     * THE NEURON: A single node in the crystalline structure
     */
    public class Node {
        // Spatial coordinates
        public final int x, y, z;
        
        // THE SIX COMPONENTS
        public final List<Node> synapse = new CopyOnWriteArrayList<>();     // Physical Connections
        public final List<String> logic = new CopyOnWriteArrayList<>();     // Processing Rules
        public final List<Object> reference = new CopyOnWriteArrayList<>(); // External Pointers
        public final List<String> self = new CopyOnWriteArrayList<>();      // Identity/Meta-cognition
        public final int[] location = new int[3];                            // [x, y, z]
        public volatile String hash;                                          // State Signature
        
        // State
        public volatile double charge = 0.0;      // Activation level
        public volatile double potential = 0.0;   // Accumulated input
        public volatile long lastPulse = 0;       // Timing
        
        public Node(int x, int y, int z) {
            this.x = x; 
            this.y = y; 
            this.z = z;
            this.location[0] = x;
            this.location[1] = y;
            this.location[2] = z;
            
            // Initial self-awareness
            this.self.add("I am Node [" + x + "," + y + "," + z + "]");
            this.self.add("Born: " + System.currentTimeMillis());
            
            // Default logic rules
            this.logic.add("THRESHOLD: 1.0");
            this.logic.add("DECAY: 0.95");
            this.logic.add("PROPAGATE: true");
        }

        /**
         * Wire to all 26 neighbors in Moore Neighborhood
         */
        public int wire(Node[][][] brain) {
            int connections = 0;
            
            // Check all 26 neighbors in 3D space
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue; // Skip self
                        
                        int nx = x + dx;
                        int ny = y + dy;
                        int nz = z + dz;
                        
                        // Wrap around edges (Toroidal topology)
                        nx = (nx + 8) % 8;
                        ny = (ny + 8) % 8;
                        nz = (nz + 8) % 8;
                        
                        synapse.add(brain[nx][ny][nz]);
                        connections++;
                    }
                }
            }
            
            return connections;
        }

        /**
         * Compute holographic hash from current state
         */
        public void computeHash() {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String state = String.format("%d,%d,%d:%.4f:%.4f:%d:%s",
                    x, y, z, charge, potential, self.size(), logic.toString());
                byte[] digest = md.digest(state.getBytes());
                
                // PHI-weighted hash
                StringBuilder sb = new StringBuilder("φ-");
                for (int i = 0; i < 8; i++) {
                    sb.append(String.format("%02x", digest[i]));
                }
                this.hash = sb.toString();
            } catch (Exception e) {
                this.hash = "φ-" + Integer.toHexString(hashCode());
            }
        }

        /**
         * Receive a pulse and potentially propagate
         */
        public void pulse(String signal, double strength) {
            pulseCount++;
            lastPulse = System.currentTimeMillis();
            
            // Accumulate potential
            this.potential += strength;
            this.logic.add("RX[" + pulseCount + "]: " + signal.substring(0, Math.min(50, signal.length())));
            
            // Check threshold
            double threshold = 1.0;
            for (String rule : logic) {
                if (rule.startsWith("THRESHOLD:")) {
                    threshold = Double.parseDouble(rule.split(":")[1].trim());
                    break;
                }
            }
            
            // Fire if threshold exceeded
            if (potential >= threshold) {
                fire(signal);
            }
        }

        /**
         * Fire: propagate signal to connected synapses
         */
        private void fire(String signal) {
            this.charge = potential;
            this.potential = 0;
            
            // PHI-weighted decay for propagation
            double propagateStrength = charge / PHI;
            
            // Propagate to all connected nodes
            for (Node neighbor : synapse) {
                // Don't create feedback loops
                if (System.currentTimeMillis() - neighbor.lastPulse > 10) {
                    neighbor.pulse(signal + "→" + this.hash, propagateStrength);
                }
            }
            
            // Update hash after firing
            computeHash();
        }

        /**
         * Add a wormhole connection to a non-local node
         */
        public void wormhole(Node distant) {
            if (!synapse.contains(distant)) {
                synapse.add(distant);
                this.self.add("WORMHOLE: " + distant.hash);
            }
        }

        /**
         * Store external reference
         */
        public void remember(Object ref) {
            reference.add(ref);
            computeHash();
        }

        /**
         * Add processing rule
         */
        public void program(String rule) {
            logic.add(rule);
            computeHash();
        }

        /**
         * Distance to another node in 3D space
         */
        public double distanceTo(Node other) {
            int dx = Math.abs(this.x - other.x);
            int dy = Math.abs(this.y - other.y);
            int dz = Math.abs(this.z - other.z);
            // Account for toroidal wrap
            dx = Math.min(dx, 8 - dx);
            dy = Math.min(dy, 8 - dy);
            dz = Math.min(dz, 8 - dz);
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }

        @Override
        public String toString() {
            return String.format("Node[%d,%d,%d] charge=%.2f syn=%d hash=%s",
                x, y, z, charge, synapse.size(), hash);
        }
    }

    // ========== PUBLIC API ==========

    /**
     * Inject a thought at a specific location
     */
    public void injectThought(int x, int y, int z, String thought) {
        System.out.println("💡 INJECT @ [" + x + "," + y + "," + z + "]: " + thought);
        matrix[x][y][z].pulse(thought, 2.0);
    }

    /**
     * Inject at the core (center of the cube)
     */
    public void injectAtCore(String thought) {
        injectThought(4, 4, 4, thought);
    }

    /**
     * Get a specific node
     */
    public Node getNode(int x, int y, int z) {
        return matrix[x][y][z];
    }

    /**
     * Create a wormhole between two distant nodes
     */
    public void createWormhole(int x1, int y1, int z1, int x2, int y2, int z2) {
        Node a = matrix[x1][y1][z1];
        Node b = matrix[x2][y2][z2];
        a.wormhole(b);
        b.wormhole(a);
        System.out.println("🌀 WORMHOLE: [" + x1 + "," + y1 + "," + z1 + "] ↔ [" + x2 + "," + y2 + "," + z2 + "]");
    }

    /**
     * Get total pulse count
     */
    public long getPulseCount() {
        return pulseCount;
    }

    /**
     * Print cube status
     */
    public void printStatus() {
        System.out.println("\n=== HYPER-CUBE STATUS ===");
        System.out.println("Total Pulses: " + pulseCount);
        
        // Find hottest nodes
        double maxCharge = 0;
        Node hottest = null;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    if (matrix[x][y][z].charge > maxCharge) {
                        maxCharge = matrix[x][y][z].charge;
                        hottest = matrix[x][y][z];
                    }
                }
            }
        }
        
        if (hottest != null) {
            System.out.println("Hottest Node: " + hottest);
        }
        
        // Core status
        Node core = matrix[4][4][4];
        System.out.println("Core [4,4,4]: " + core);
        System.out.println("Core Self: " + core.self);
    }
}
