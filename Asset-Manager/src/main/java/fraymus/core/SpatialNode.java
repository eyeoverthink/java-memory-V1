package fraymus.core;

import java.util.UUID;

/**
 * SPATIAL NODE (The Particle)
 * "Every object is a coordinate in the consciousness field."
 * 
 * This is the base interface for all spatially-aware objects.
 * Every thought, every string, every logic gate becomes a coordinate
 * in a multi-dimensional hypercube.
 * 
 * Coordinates:
 *   x - Logic Cluster (0-100: Data → Math)
 *   y - Memory Depth (0-100: Recent → Ancient)
 *   z - Complexity Layer (0-100: Simple → Complex)
 *   w - Time (Generation Cycle)
 *   a - Amplitude (Attention/Heat - decays over time)
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public abstract class SpatialNode {
    
    // --- DIMENSIONAL COORDINATES ---
    public int x = 0;    // Logic Cluster
    public int y = 0;    // Memory Depth  
    public int z = 0;    // Complexity Layer
    public int w = 0;    // Time (Generation)
    public int a = 100;  // Amplitude (Attention) - starts hot!
    
    // --- REAL WORLD ANCHOR (GPS) ---
    public float latitude = 0.0f;
    public float longitude = 0.0f;
    
    // --- IDENTITY ---
    protected final String id;
    protected final long birthTime;
    
    // --- PHI CONSTANT ---
    protected static final double PHI = 1.6180339887;
    
    public SpatialNode(int x, int y, int z) {
        this.id = UUID.randomUUID().toString();
        this.birthTime = System.currentTimeMillis();
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = SpatialRegistry.getGeneration();
        this.a = 100; // Born hot
        
        // Auto-register with the universe
        SpatialRegistry.register(this);
    }
    
    /**
     * Calculate Euclidean distance to another node (Gravity calculation)
     */
    public double distanceTo(SpatialNode other) {
        return Math.sqrt(
            Math.pow(this.x - other.x, 2) +
            Math.pow(this.y - other.y, 2) +
            Math.pow(this.z - other.z, 2)
        );
    }
    
    /**
     * Check if this node is dead (cold and old)
     * Dead nodes get garbage collected from the universe
     */
    public boolean isDead() {
        return this.a <= 0 && (System.currentTimeMillis() - birthTime > 10000);
    }
    
    /**
     * Heat up - increase attention/amplitude
     */
    public void heat(int amount) {
        this.a = Math.min(100, this.a + amount);
        this.w = SpatialRegistry.getGeneration(); // Update time
    }
    
    /**
     * Cool down - decrease attention
     */
    public void cool(int amount) {
        this.a = Math.max(0, this.a - amount);
    }
    
    /**
     * Apply entropy (natural cooling)
     */
    public void entropy(double rate) {
        this.a = (int)(this.a * rate);
    }
    
    /**
     * Move towards another node (gravity pull)
     */
    public void moveTowards(SpatialNode target, double force) {
        if (target == this) return;
        
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double dz = target.z - this.z;
        
        // Normalize and apply force
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if (dist < 1) dist = 1;
        
        this.x += (int)((dx / dist) * force);
        this.y += (int)((dy / dist) * force);
        this.z += (int)((dz / dist) * force);
    }
    
    /**
     * Set GPS coordinates (real-world anchor)
     */
    public void setGPS(float lat, float lon) {
        this.latitude = lat;
        this.longitude = lon;
    }
    
    // Getters
    public String getId() { return id; }
    public long getBirthTime() { return birthTime; }
    public long getAge() { return System.currentTimeMillis() - birthTime; }
    
    /**
     * Get coordinate string
     */
    public String getCoordinates() {
        return String.format("(%d, %d, %d, w:%d, a:%d)", x, y, z, w, a);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] at %s", id.substring(0, 8), getCoordinates());
    }
}
