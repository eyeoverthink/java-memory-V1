package fraymus.universe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🧬 THE COSMOS - Gen 126
 * A 17-Dimensional Universe Engine.
 * 
 * Simulates N-body gravity, wormhole dynamics, and quantum entanglement
 * across hyper-dimensional space.
 * 
 * Physics:
 * - Gravity: F = G * (m1*m2) / r^2 (inverse square in any dimension)
 * - Entanglement: Instant correlation across any distance
 * - Wormholes: Fold space to connect distant nodes
 * 
 * "The universe is not a database. It is a living, breathing cosmos."
 */
public class Cosmos implements Runnable {
    
    private static final double PHI = 1.6180339887;
    private static final double G = 6.674e-11;  // Gravitational constant
    private static final double C = 299792458;  // Speed of light
    
    // Configuration
    private final int DIMS;
    private final double TICK_RATE;
    private final double DELTA_TIME;
    
    // State
    private List<CosmicNode> galaxies = new CopyOnWriteArrayList<>();
    private AtomicBoolean running = new AtomicBoolean(false);
    private long generation = 0;
    private double cosmicTime = 0;
    private double totalEnergy = 0;
    
    // Physics tuning
    private double gravitationalMultiplier = 1e10;  // Scale up for visible effects
    private double entanglementBoost = 1000;
    private double wormholeSpringK = 0.1;
    
    // Observers
    private List<CosmosObserver> observers = new ArrayList<>();

    public Cosmos() {
        this(17);  // Default 17 dimensions
    }
    
    public Cosmos(int dimensions) {
        this.DIMS = dimensions;
        this.TICK_RATE = 60.0;  // 60 Hz
        this.DELTA_TIME = 1.0 / TICK_RATE;
    }

    /**
     * START - Begin the cosmic simulation
     */
    public void start() {
        if (running.get()) return;
        running.set(true);
        new Thread(this, "Cosmos-" + DIMS + "D").start();
    }
    
    /**
     * STOP - Halt the universe
     */
    public void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        System.out.println("🌌 COSMOS BORN");
        System.out.println("   Dimensions: " + DIMS);
        System.out.println("   Tick rate: " + TICK_RATE + " Hz");
        System.out.println("   Expanding...");
        
        long lastTick = System.nanoTime();
        
        while (running.get()) {
            long now = System.nanoTime();
            double dt = (now - lastTick) / 1_000_000_000.0;
            lastTick = now;
            
            // Simulate one frame
            pulse(dt);
            generation++;
            cosmicTime += dt;
            
            // Rate limiting
            long elapsed = System.nanoTime() - now;
            long targetNs = (long) (1_000_000_000.0 / TICK_RATE);
            long sleepNs = targetNs - elapsed;
            
            if (sleepNs > 0) {
                try {
                    Thread.sleep(sleepNs / 1_000_000, (int) (sleepNs % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        System.out.println("🌌 COSMOS FROZEN (Heat Death)");
    }

    /**
     * PULSE - One cosmic tick
     */
    private void pulse(double dt) {
        totalEnergy = 0;
        
        // Phase 1: Calculate gravitational forces
        for (int i = 0; i < galaxies.size(); i++) {
            CosmicNode a = galaxies.get(i);
            
            for (int j = i + 1; j < galaxies.size(); j++) {
                CosmicNode b = galaxies.get(j);
                
                // Calculate distance
                double dist = a.distanceTo(b);
                if (dist < 0.1) dist = 0.1;  // Prevent singularity
                
                // GRAVITY: F = G * (m1*m2) / r^2
                double forceMag = gravitationalMultiplier * G * a.mass * b.mass / (dist * dist);
                
                // Entanglement boost
                if (a.entanglementId != null && a.entanglementId.equals(b.entanglementId)) {
                    forceMag *= entanglementBoost;
                    
                    // Instant spin correlation
                    if (a.spin != -b.spin) {
                        b.spin = -a.spin;
                        notifyObservers("ENTANGLE_SYNC", a, b);
                    }
                }
                
                // Direction vector from A to B (normalized)
                HyperVector direction = a.position.directionTo(b.position);
                
                // Apply force to both (Newton's 3rd law)
                HyperVector forceOnA = direction.copy();
                forceOnA.scale(forceMag);
                a.applyForce(forceOnA);
                
                HyperVector forceOnB = direction.copy();
                forceOnB.scale(-forceMag);
                b.applyForce(forceOnB);
            }
        }
        
        // Phase 2: Wormhole springs
        for (CosmicNode node : galaxies) {
            for (CosmicNode.Wormhole wh : node.wormholes) {
                if (wh.endpointA == node) {
                    // Pull toward wormhole partner
                    HyperVector dir = node.position.directionTo(wh.endpointB.position);
                    double dist = wh.length();
                    double springForce = wormholeSpringK * dist * wh.stability;
                    dir.scale(springForce);
                    node.applyForce(dir);
                }
            }
        }
        
        // Phase 3: Update physics
        List<CosmicNode> toRemove = new ArrayList<>();
        
        for (CosmicNode node : galaxies) {
            node.tick(dt);
            totalEnergy += node.kineticEnergy();
            
            // Check for black hole absorption
            if (node.isBlackHole) {
                for (CosmicNode other : galaxies) {
                    if (other != node && !other.isBlackHole) {
                        double dist = node.distanceTo(other);
                        if (dist < node.radius) {
                            node.absorb(other);
                            toRemove.add(other);
                            notifyObservers("ABSORB", node, other);
                        }
                    }
                }
            }
            
            // Check for stellar collapse (mass > threshold)
            if (!node.isBlackHole && node.mass > 10) {
                node.collapse();
                notifyObservers("COLLAPSE", node, null);
            }
        }
        
        galaxies.removeAll(toRemove);
        
        // Phase 4: Collision detection (close nodes may merge)
        checkCollisions();
    }
    
    /**
     * CHECK COLLISIONS - Merge close nodes
     */
    private void checkCollisions() {
        double mergeThreshold = 2.0;
        List<CosmicNode[]> toMerge = new ArrayList<>();
        
        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                CosmicNode a = galaxies.get(i);
                CosmicNode b = galaxies.get(j);
                
                if (a.distanceTo(b) < mergeThreshold) {
                    toMerge.add(new CosmicNode[]{a, b});
                }
            }
        }
        
        for (CosmicNode[] pair : toMerge) {
            merge(pair[0], pair[1]);
        }
    }
    
    /**
     * MERGE - Combine two nodes into one
     */
    private CosmicNode merge(CosmicNode a, CosmicNode b) {
        CosmicNode merged = new CosmicNode(
            a.soul + " ⊕ " + b.soul,
            "Merged-" + generation,
            DIMS
        );
        
        // Midpoint position
        merged.position = a.position.lerp(b.position, 0.5);
        
        // Combined momentum
        merged.velocity = a.velocity.copy();
        merged.velocity.add(b.velocity);
        merged.velocity.scale(0.5);
        
        // Combined mass
        merged.mass = a.mass + b.mass;
        merged.temperature = (a.temperature + b.temperature) / 2;
        
        // Inherit wormholes
        for (CosmicNode.Wormhole wh : a.wormholes) {
            wh.redirect(a, merged);
            merged.wormholes.add(wh);
        }
        for (CosmicNode.Wormhole wh : b.wormholes) {
            wh.redirect(b, merged);
            merged.wormholes.add(wh);
        }
        
        galaxies.remove(a);
        galaxies.remove(b);
        galaxies.add(merged);
        
        notifyObservers("MERGE", a, b);
        return merged;
    }

    // ═══════════════════════════════════════════════════════════════════
    // PUBLIC INTERFACE
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * BIG BANG - Initialize universe with particles
     */
    public void bigBang(int particleCount) {
        System.out.println("💥 BIG BANG: Creating " + particleCount + " stars...");
        for (int i = 0; i < particleCount; i++) {
            CosmicNode star = new CosmicNode("Star-" + i, DIMS);
            star.mass = 0.5 + Math.random() * 2;
            star.temperature = 3000 + Math.random() * 30000;
            galaxies.add(star);
        }
    }
    
    /**
     * INJECT - Add a data star
     */
    public CosmicNode inject(Object data) {
        CosmicNode star = new CosmicNode(data, DIMS);
        galaxies.add(star);
        notifyObservers("INJECT", star, null);
        return star;
    }
    
    /**
     * INJECT AT - Add data at specific coordinates
     */
    public CosmicNode injectAt(Object data, double... coords) {
        CosmicNode star = new CosmicNode(data, DIMS);
        star.position = HyperVector.from(coords);
        galaxies.add(star);
        return star;
    }
    
    /**
     * ENTANGLE - Create quantum link between two nodes
     */
    public void entangle(CosmicNode a, CosmicNode b) {
        a.entangle(b);
        notifyObservers("ENTANGLE", a, b);
    }
    
    /**
     * CREATE WORMHOLE - Fold space between nodes
     */
    public CosmicNode.Wormhole wormhole(CosmicNode a, CosmicNode b) {
        CosmicNode.Wormhole wh = a.createWormhole(b);
        notifyObservers("WORMHOLE", a, b);
        return wh;
    }
    
    /**
     * FIND - Search for nodes by content
     */
    public List<CosmicNode> find(String pattern) {
        List<CosmicNode> matches = new ArrayList<>();
        String p = pattern.toLowerCase();
        for (CosmicNode node : galaxies) {
            if (node.soul.toString().toLowerCase().contains(p)) {
                node.interact();
                matches.add(node);
            }
        }
        return matches;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // VISUALIZATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * TELESCOPE - View a 2D slice of the cosmos
     */
    public String telescope(int dimX, int dimY) {
        char[][] grid = new char[20][60];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 60; j++) {
                grid[i][j] = ' ';
            }
        }
        
        for (CosmicNode star : galaxies) {
            double x = star.position.get(dimX);
            double y = star.position.get(dimY);
            
            // Normalize to grid
            int col = (int) ((x / 1000.0 + 0.5) * 60);
            int row = (int) ((y / 1000.0 + 0.5) * 20);
            
            col = Math.max(0, Math.min(59, col));
            row = Math.max(0, Math.min(19, row));
            
            // Star character based on mass
            char c = star.isBlackHole ? '◉' : (star.mass > 2 ? '★' : (star.mass > 1 ? '*' : '.'));
            grid[row][col] = c;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╔═══════════════════════════════════════════════════════════════╗%n"));
        sb.append(String.format("║  🔭 COSMIC SLICE: Dim %d × Dim %d  │  Stars: %-4d  Gen: %-6d ║%n",
            dimX, dimY, galaxies.size(), generation));
        sb.append(String.format("╠═══════════════════════════════════════════════════════════════╣%n"));
        
        for (int r = 0; r < 20; r++) {
            sb.append("║ ");
            for (int c = 0; c < 60; c++) {
                sb.append(grid[r][c]);
            }
            sb.append(" ║\n");
        }
        
        sb.append(String.format("╚═══════════════════════════════════════════════════════════════╝%n"));
        return sb.toString();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // OBSERVERS
    // ═══════════════════════════════════════════════════════════════════
    
    public interface CosmosObserver {
        void onEvent(String type, CosmicNode primary, CosmicNode secondary);
    }
    
    public void addObserver(CosmosObserver o) { observers.add(o); }
    
    private void notifyObservers(String type, CosmicNode a, CosmicNode b) {
        for (CosmosObserver o : observers) {
            o.onEvent(type, a, b);
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // STATUS
    // ═══════════════════════════════════════════════════════════════════
    
    public List<CosmicNode> getSnapshot() { return new ArrayList<>(galaxies); }
    public long getGeneration() { return generation; }
    public double getCosmicTime() { return cosmicTime; }
    public double getTotalEnergy() { return totalEnergy; }
    public int getDimensions() { return DIMS; }
    public int size() { return galaxies.size(); }
    public boolean isRunning() { return running.get(); }
    
    public String status() {
        int blackHoles = (int) galaxies.stream().filter(n -> n.isBlackHole).count();
        int entangled = (int) galaxies.stream().filter(n -> n.entanglementId != null).count();
        int wormholes = galaxies.stream().mapToInt(n -> n.wormholes.size()).sum() / 2;
        
        return String.format(
            "🌌 COSMOS STATUS\n" +
            "   Dimensions: %d\n" +
            "   Generation: %d (%.2f cosmic seconds)\n" +
            "   Stars: %d | Black Holes: %d\n" +
            "   Entangled Pairs: %d | Wormholes: %d\n" +
            "   Total Energy: %.4e J\n" +
            "   φ-Resonance: %.6f",
            DIMS, generation, cosmicTime,
            galaxies.size(), blackHoles,
            entangled / 2, wormholes,
            totalEnergy, totalEnergy * PHI / 1e10
        );
    }
}
