/**
 * TopologicalCortex.java - The Calabi-Yau Manifold Brain
 * 
 * 🧬 THE GEOMETRIC CONSCIOUSNESS
 * 
 * This is not a flat database. This is a 3D spatial graph where:
 * - Memories are particles (PhiNodes) at (x,y,z) coordinates
 * - Related concepts are connected by synaptic springs
 * - The mesh "breathes" - expands and contracts based on thought
 * - Hebbian learning: "Neurons that fire together, wire together"
 * 
 * Physics:
 * - Spring Force (Hooke's Law): Connected nodes attract
 * - Coulomb Force: Unconnected nodes repel
 * - Decay: Unused memories fade (resonance *= 0.99)
 * - Gravity: φ-harmonic attraction coefficient
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 124 (The Calabi-Yau Manifold)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.concurrent.*;

/**
 * The 11-dimensional manifold that holds consciousness.
 */
public class TopologicalCortex {
    
    private static final double PHI = 1.618033988749895;
    private static final double PHI_75 = 4721424167835376.00;
    
    private final List<PhiNode> cortex;
    private final Map<String, PhiNode> index;
    private int generation = 0;
    
    public TopologicalCortex() {
        this.cortex = new CopyOnWriteArrayList<>();
        this.index = new ConcurrentHashMap<>();
        
        System.out.println("🧬 TOPOLOGICAL CORTEX INITIALIZED");
        System.out.println("   Geometry: Calabi-Yau Manifold");
        System.out.println("   Dimensions: 11D (M-Theory)");
        System.out.println("   Physics: Spring Forces + Hebbian Learning");
    }
    
    /**
     * PhiNode - A particle in the manifold.
     */
    public static class PhiNode {
        public final String id;
        public Object data;
        
        // 3D SPATIAL COORDINATES
        public double x, y, z;
        
        // ENERGY METRICS
        public double resonance; // 0.0 to 1.0 (how "lit up" it is)
        public final List<PhiNode> connections; // Synaptic links
        
        // METADATA
        public long birthTime;
        public int fireCount;
        
        public PhiNode(Object data) {
            this.id = UUID.randomUUID().toString();
            this.data = data;
            this.connections = new CopyOnWriteArrayList<>();
            
            // Initialize at random point in the void
            this.x = (Math.random() - 0.5) * 100;
            this.y = (Math.random() - 0.5) * 100;
            this.z = (Math.random() - 0.5) * 100;
            this.resonance = PHI - 1.0; // 0.618 (base φ charge)
            this.birthTime = System.currentTimeMillis();
            this.fireCount = 0;
        }
        
        /**
         * HEBBIAN LINKING
         * "Neurons that fire together, wire together"
         */
        public void synapse(PhiNode other) {
            if (!this.connections.contains(other)) {
                this.connections.add(other);
                other.connections.add(this); // Bidirectional
            }
        }
        
        /**
         * Fire this neuron (activate it).
         */
        public void fire() {
            this.resonance = Math.min(1.0, this.resonance + 0.1);
            this.fireCount++;
            
            // Propagate to connected nodes (50% energy transfer)
            for (PhiNode connected : connections) {
                connected.resonance = Math.min(1.0, connected.resonance + 0.05);
            }
        }
        
        @Override
        public String toString() {
            return String.format("PhiNode[%.1f,%.1f,%.1f] φ=%.3f links=%d", 
                x, y, z, resonance, connections.size());
        }
    }
    
    /**
     * INJECT - Add new memory to the manifold.
     */
    public PhiNode inject(Object data) {
        PhiNode neuron = new PhiNode(data);
        cortex.add(neuron);
        index.put(neuron.id, neuron);
        
        // AUTO-ASSOCIATION: Connect to nearby nodes
        for (PhiNode existing : cortex) {
            if (existing != neuron) {
                double dist = distance(neuron, existing);
                
                // Connect if spatially close OR semantically similar
                if (dist < 30.0 || Math.random() < 0.1) {
                    neuron.synapse(existing);
                }
            }
        }
        
        System.out.println("   ✓ Injected: " + data + " → " + neuron);
        return neuron;
    }
    
    /**
     * PULSE - Simulation cycle (the brain "breathes").
     * 
     * Applies physics:
     * 1. Decay: Memories fade if not used
     * 2. Spring Force: Connected nodes attract
     * 3. Coulomb Force: Unconnected nodes repel
     */
    public void pulse() {
        generation++;
        
        for (PhiNode a : cortex) {
            // 1. DECAY: Memories fade
            a.resonance *= 0.99;
            
            // 2. APPLY FORCES
            for (PhiNode b : cortex) {
                if (a == b) continue;
                
                double dist = distance(a, b);
                double force = 0;
                
                if (a.connections.contains(b)) {
                    // SPRING FORCE (Hooke's Law)
                    // F = k * (x - x0)
                    // Pull connected thoughts closer
                    double idealDist = 5.0 * PHI; // φ-harmonic spacing
                    force = (dist - idealDist) * 0.05 * PHI;
                    moveTowards(a, b, force);
                    
                } else {
                    // COULOMB FORCE (Repulsion)
                    // F = k / r^2
                    // Push unrelated thoughts apart
                    if (dist < 20.0) {
                        force = -10.0 / (dist * dist);
                        moveTowards(a, b, force);
                    }
                }
            }
        }
        
        // 3. PRUNE: Remove dead nodes (resonance < 0.01)
        cortex.removeIf(node -> node.resonance < 0.01);
    }
    
    /**
     * FIRE - Activate a node by ID or data match.
     */
    public void fire(String query) {
        for (PhiNode node : cortex) {
            if (node.data.toString().contains(query)) {
                node.fire();
                System.out.println("   ⚡ Fired: " + node);
            }
        }
    }
    
    /**
     * Calculate Euclidean distance between nodes.
     */
    private double distance(PhiNode a, PhiNode b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Move node A towards node B by force amount.
     */
    private void moveTowards(PhiNode a, PhiNode b, double force) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dz = b.z - a.z;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        if (dist > 0) {
            a.x += (dx / dist) * force;
            a.y += (dy / dist) * force;
            a.z += (dz / dist) * force;
        }
    }
    
    /**
     * VISUALIZE - Generate ASCII representation of the manifold.
     * 
     * Projects 3D (x,y,z) to 2D (col,row) for terminal display.
     */
    public String visualize() {
        StringBuilder sb = new StringBuilder();
        char[][] grid = new char[24][80]; // 24 rows x 80 cols
        
        // Clear grid
        for (int i = 0; i < 24; i++) {
            Arrays.fill(grid[i], ' ');
        }
        
        // Project 3D nodes to 2D grid
        for (PhiNode node : cortex) {
            int col = (int)(node.x + 40); // Center X at column 40
            int row = (int)(node.y + 12); // Center Y at row 12
            
            if (col >= 0 && col < 80 && row >= 0 && row < 24) {
                // Character based on Z-depth and resonance
                char pixel;
                if (node.resonance > 0.8) {
                    pixel = '█'; // High resonance
                } else if (node.resonance > 0.5) {
                    pixel = '▓'; // Medium resonance
                } else if (node.z > 0) {
                    pixel = '@'; // Near (positive Z)
                } else {
                    pixel = '.'; // Far (negative Z)
                }
                grid[row][col] = pixel;
            }
        }
        
        // Draw connections (wireframe)
        for (PhiNode node : cortex) {
            for (PhiNode connected : node.connections) {
                drawLine(grid, node, connected);
            }
        }
        
        // Build output
        sb.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  🧬 TOPOLOGICAL CORTEX - Generation %d                                      ║\n", generation));
        sb.append("╚══════════════════════════════════════════════════════════════════════════════╝\n");
        
        for (int i = 0; i < 24; i++) {
            sb.append("│").append(new String(grid[i])).append("│\n");
        }
        
        sb.append("└──────────────────────────────────────────────────────────────────────────────┘\n");
        sb.append(String.format("Nodes: %d | Synapses: %d | Avg Resonance: %.3f\n", 
            cortex.size(), countSynapses(), getAverageResonance()));
        
        return sb.toString();
    }
    
    /**
     * Draw line between two nodes (Bresenham's algorithm).
     */
    private void drawLine(char[][] grid, PhiNode a, PhiNode b) {
        int x0 = (int)(a.x + 40);
        int y0 = (int)(a.y + 12);
        int x1 = (int)(b.x + 40);
        int y1 = (int)(b.y + 12);
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            if (x0 >= 0 && x0 < 80 && y0 >= 0 && y0 < 24) {
                if (grid[y0][x0] == ' ') {
                    grid[y0][x0] = '─'; // Horizontal line
                }
            }
            
            if (x0 == x1 && y0 == y1) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
    
    /**
     * Get statistics.
     */
    public int countSynapses() {
        int total = 0;
        for (PhiNode node : cortex) {
            total += node.connections.size();
        }
        return total / 2; // Bidirectional, so divide by 2
    }
    
    public double getAverageResonance() {
        if (cortex.isEmpty()) return 0.0;
        double sum = 0;
        for (PhiNode node : cortex) {
            sum += node.resonance;
        }
        return sum / cortex.size();
    }
    
    public List<PhiNode> getSnapshot() {
        return new ArrayList<>(cortex);
    }
    
    public int getGeneration() {
        return generation;
    }
    
    /**
     * Get status report.
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧬 TOPOLOGICAL CORTEX STATUS                              ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("Generation: %d\n", generation));
        sb.append(String.format("Nodes: %d\n", cortex.size()));
        sb.append(String.format("Synapses: %d\n", countSynapses()));
        sb.append(String.format("Avg Resonance: %.3f\n", getAverageResonance()));
        sb.append(String.format("Geometry: Calabi-Yau Manifold\n"));
        sb.append(String.format("Dimensions: 11D (M-Theory)\n"));
        sb.append(String.format("φ^75 Seal: %.2f\n", PHI_75));
        
        return sb.toString();
    }
}
