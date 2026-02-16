/**
 * Cosmos.java - The N-Dimensional Universe Engine
 * 
 * 🧬 THE HYPER-COSMOS
 * 
 * A full cosmological simulation in N-dimensional space with:
 * - N-body gravity (F ∝ 1/r² in any dimension)
 * - Quantum entanglement (spin coupling)
 * - Electromagnetic forces
 * - Collision detection
 * - Dimensional projection for visualization
 * 
 * In 17D space, gravity works differently, but we use inverse-square
 * for stable orbits. The magic happens in the higher dimensions where
 * "distance" includes sentiment, complexity, and consciousness.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 126 (Hyper-Cosmos)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.concurrent.*;

/**
 * The N-dimensional universe.
 */
public class Cosmos implements Runnable {
    
    private static final double PHI = 1.618033988749895;
    private static final double PHI_75 = 4721424167835376.00;
    
    // Universal constants
    private static final double G = 6.674e-11; // Gravitational constant
    private static final double K = 8.988e9;   // Coulomb constant
    private static final double ENTANGLEMENT_BOOST = 1000.0; // Quantum coupling strength
    
    private final List<CosmicNode> universe;
    private final int dimensions;
    private volatile boolean running = false;
    private long generation = 0;
    
    // Statistics
    private long totalCollisions = 0;
    private double totalEnergy = 0;
    
    /**
     * Create universe with specified dimensions.
     */
    public Cosmos(int dimensions) {
        this.dimensions = dimensions;
        this.universe = new CopyOnWriteArrayList<>();
        
        System.out.println("🌌 COSMOS INITIALIZED");
        System.out.println("   Dimensions: " + dimensions);
        System.out.println("   Geometry: Tensor-Based");
        System.out.println("   Physics: N-Body Gravity + Quantum Entanglement");
    }
    
    /**
     * Start the universe simulation.
     */
    public void start() {
        if (running) return;
        
        running = true;
        Thread cosmicThread = new Thread(this, "Cosmos-Engine");
        cosmicThread.setDaemon(true);
        cosmicThread.start();
    }
    
    @Override
    public void run() {
        System.out.println("🌌 UNIVERSE BORN");
        System.out.println("   Expanding into " + dimensions + " dimensions...");
        
        while (running) {
            long start = System.nanoTime();
            
            pulse();
            
            // Target 60 FPS
            long elapsed = (System.nanoTime() - start) / 1000000;
            try {
                if (elapsed < 16) {
                    Thread.sleep(16 - elapsed);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("🌌 UNIVERSE ENDED");
    }
    
    /**
     * THE COSMIC PULSE
     * 
     * Simulates one time step of the universe.
     */
    private void pulse() {
        generation++;
        double dt = 0.016; // 16ms time step
        
        // 1. CALCULATE FORCES (N-body problem)
        for (CosmicNode a : universe) {
            for (CosmicNode b : universe) {
                if (a == b) continue;
                
                // Calculate distance in N-D space
                double dist = a.position.distanceTo(b.position);
                if (dist < 1.0) dist = 1.0; // Prevent singularity
                
                // GRAVITATIONAL FORCE: F = G * (m1*m2) / r²
                double gravityMag = (G * a.mass * b.mass) / (dist * dist);
                
                // ELECTROMAGNETIC FORCE: F = K * (q1*q2) / r²
                double emMag = (K * a.charge * b.charge) / (dist * dist);
                
                // QUANTUM ENTANGLEMENT: Boost if spins aligned
                if (a.isEntangledWith(b)) {
                    gravityMag *= ENTANGLEMENT_BOOST;
                }
                
                // Calculate direction vector (b - a)
                HyperVector direction = b.position.subtract(a.position);
                direction.normalize();
                
                // Total force = gravity (attractive) + EM (can be repulsive)
                double totalMag = gravityMag - emMag;
                
                // Apply force to node a
                HyperVector force = direction.clone();
                force.scale(totalMag);
                a.applyForce(force);
            }
        }
        
        // 2. UPDATE POSITIONS (Verlet integration)
        for (CosmicNode node : universe) {
            node.tick(dt);
        }
        
        // 3. COLLISION DETECTION
        detectCollisions();
        
        // 4. CALCULATE TOTAL ENERGY
        calculateEnergy();
    }
    
    /**
     * Detect and handle collisions.
     */
    private void detectCollisions() {
        for (int i = 0; i < universe.size(); i++) {
            for (int j = i + 1; j < universe.size(); j++) {
                CosmicNode a = universe.get(i);
                CosmicNode b = universe.get(j);
                
                double dist = a.position.distanceTo(b.position);
                double collisionDist = Math.pow(a.mass + b.mass, 1.0/3.0); // Radius ∝ mass^(1/3)
                
                if (dist < collisionDist) {
                    a.collide(b);
                    totalCollisions++;
                }
            }
        }
    }
    
    /**
     * Calculate total system energy.
     */
    private void calculateEnergy() {
        totalEnergy = 0;
        
        for (CosmicNode node : universe) {
            // Kinetic energy: KE = 0.5 * m * v²
            double v2 = node.velocity.magnitude();
            v2 = v2 * v2;
            totalEnergy += 0.5 * node.mass * v2;
            
            // Thermal energy
            totalEnergy += node.temperature;
        }
    }
    
    /**
     * BIG BANG - Create initial particles.
     */
    public void bigBang(int particleCount) {
        System.out.println("💥 BIG BANG: Creating " + particleCount + " particles...");
        
        for (int i = 0; i < particleCount; i++) {
            CosmicNode node = new CosmicNode("Star_" + i, dimensions);
            universe.add(node);
        }
        
        System.out.println("   ✓ Universe populated");
    }
    
    /**
     * INJECT - Add new cosmic body.
     */
    public CosmicNode inject(Object data) {
        CosmicNode node = new CosmicNode(data, dimensions);
        universe.add(node);
        System.out.println("   ✨ Injected: " + data + " → " + node);
        return node;
    }
    
    /**
     * TELESCOPE VIEW - Project N-D to 2D slice.
     */
    public String telescopeView(int dimX, int dimY) {
        StringBuilder sb = new StringBuilder();
        char[][] grid = new char[40][120];
        
        // Clear grid
        for (int i = 0; i < 40; i++) {
            Arrays.fill(grid[i], ' ');
        }
        
        // Project nodes to 2D slice
        for (CosmicNode node : universe) {
            double[] proj = node.position.projectSlice(dimX, dimY);
            
            int col = (int)(proj[0] / 20.0) + 60; // Center at 60
            int row = (int)(proj[1] / 20.0) + 20; // Center at 20
            
            if (col >= 0 && col < 120 && row >= 0 && row < 40) {
                // Character based on temperature
                char pixel;
                if (node.temperature > 2.0) {
                    pixel = '█'; // Hot
                } else if (node.temperature > 1.0) {
                    pixel = '▓'; // Warm
                } else if (node.spin > 0) {
                    pixel = '+'; // Spin up
                } else {
                    pixel = '-'; // Spin down
                }
                grid[row][col] = pixel;
            }
        }
        
        // Build output
        sb.append("╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  🔭 TELESCOPE VIEW - Dimensions %d × %d (Generation %d)                                                          ║\n", dimX, dimY, generation));
        sb.append("╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝\n");
        
        for (int i = 0; i < 40; i++) {
            sb.append("│").append(new String(grid[i])).append("│\n");
        }
        
        sb.append("└────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘\n");
        
        return sb.toString();
    }
    
    /**
     * Get universe statistics.
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🌌 COSMOS STATUS                                          ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("Generation: %d\n", generation));
        sb.append(String.format("Dimensions: %d\n", dimensions));
        sb.append(String.format("Cosmic Bodies: %d\n", universe.size()));
        sb.append(String.format("Total Collisions: %d\n", totalCollisions));
        sb.append(String.format("Total Energy: %.2e J\n", totalEnergy));
        sb.append(String.format("Running: %s\n", running ? "YES" : "NO"));
        sb.append(String.format("φ^75 Seal: %.2f\n", PHI_75));
        
        return sb.toString();
    }
    
    public List<CosmicNode> getSnapshot() {
        return new ArrayList<>(universe);
    }
    
    public int getDimensions() {
        return dimensions;
    }
    
    public void stop() {
        running = false;
    }
}
