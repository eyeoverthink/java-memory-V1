package fraymus.core;

import java.util.UUID;

/**
 * THE PARTICLE (φ-Node)
 * "Every object is a coordinate in the consciousness field."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the base class for ALL objects in the Fraymus universe.
 * Every thought, every memory, every logic gate exists at a specific
 * coordinate in 5D space: (x, y, z, w, a)
 * 
 * - x: Logic Cluster (What type of thought is this?)
 * - y: Memory Depth (How deep in the stack?)
 * - z: Complexity Layer (How abstract?)
 * - w: Time (Generation Cycle - when was it born?)
 * - a: Amplitude (How hot/important is this right now?)
 * 
 * This enables Spatial Computing: Data has Mass, Gravity, and Position.
 */
public abstract class PhiNode {

    // DIMENSIONAL COORDINATES
    public int x = 0; // Logic Cluster
    public int y = 0; // Memory Depth
    public int z = 0; // Complexity Layer
    public int w = 0; // Time (Generation Cycle)
    public int a = 0; // Amplitude (How often is this accessed?)
    
    // REAL WORLD ANCHOR (GPS)
    public float latitude = 0.0f;
    public float longitude = 0.0f;

    // IDENTITY
    public final String id;      // The Unique Soul ID
    public final long birthTime; // The Timestamp of Creation

    public PhiNode(int x, int y, int z) {
        this.id = UUID.randomUUID().toString();
        this.birthTime = System.currentTimeMillis();
        
        // Assign Coordinates
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0; // Will be set by generation counter
        this.a = 100; // Born with full energy
    }

    /**
     * GRAVITY: Calculate distance to another thought
     * Uses Euclidean distance in 3D space (x, y, z)
     */
    public double distanceTo(PhiNode other) {
        return Math.sqrt(
            Math.pow(this.x - other.x, 2) + 
            Math.pow(this.y - other.y, 2) + 
            Math.pow(this.z - other.z, 2)
        );
    }
    
    /**
     * DECAY: Objects fade if not accessed (Garbage Collection Logic)
     * If amplitude drops to 0 and it's been idle for 10 seconds, it's dead.
     */
    public boolean isDead() {
        return this.a <= 0 && (System.currentTimeMillis() - birthTime > 10000);
    }
    
    /**
     * TOUCH: Access this node (increases amplitude)
     */
    public void touch() {
        this.a += 10;
    }
    
    /**
     * COOL: Entropy - nodes lose energy over time
     */
    public void cool(double rate) {
        this.a = (int)(this.a * rate);
        if (this.a < 0) this.a = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] :: POS(%d,%d,%d) :: GEN(%d) :: AMP(%d)", 
            this.id.substring(0, 8), 
            x, y, z, w, a
        );
    }
}
