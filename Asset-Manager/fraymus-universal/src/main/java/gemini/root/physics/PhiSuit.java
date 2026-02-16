package gemini.root.physics;

/**
 * PHI SUIT: A particle in the Fraynix physics engine.
 * Everything is a particle - tasks, agents, thoughts, memory.
 * Gravity pulls them together. Collisions trigger fusion events.
 */
public class PhiSuit<T> {
    
    private static final double PHI = 1.618033988749895;
    
    public String label;
    public T data;
    
    // 3D Position in thought-space
    public double x, y, z;
    
    // Velocity vector
    public double vx = 0, vy = 0, vz = 0;
    
    // Physical properties
    public double amplitude = 1.0;  // Mass/importance
    public double heat = 0.0;       // Energy level
    public double charge = 0.0;     // Attraction/repulsion
    
    // State
    public boolean active = true;
    public long birthTime;
    
    public PhiSuit(T data, double x, double y, double z) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
        this.label = "PARTICLE_" + System.nanoTime();
        this.birthTime = System.currentTimeMillis();
    }
    
    /** Label-only constructor. Use this when T is not String to avoid ambiguity. */
    protected PhiSuit(double x, double y, double z, String label) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.z = z;
        this.birthTime = System.currentTimeMillis();
    }
    
    /**
     * Distance to another particle
     */
    public double distanceTo(PhiSuit<?> other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
    /**
     * Apply gravitational pull from another particle
     */
    public void attractTo(PhiSuit<?> other, double G) {
        double dist = distanceTo(other);
        if (dist < 1.0) dist = 1.0; // Prevent singularity
        
        // F = G * m1 * m2 / r^2
        double force = G * this.amplitude * other.amplitude / (dist * dist);
        
        // Direction vector (normalized)
        double dx = (other.x - this.x) / dist;
        double dy = (other.y - this.y) / dist;
        double dz = (other.z - this.z) / dist;
        
        // Apply force as acceleration (F = ma, a = F/m)
        double accel = force / this.amplitude;
        this.vx += dx * accel;
        this.vy += dy * accel;
        this.vz += dz * accel;
    }
    
    /**
     * Move based on velocity
     */
    public void tick() {
        // Apply PHI damping for stability
        double damping = 1.0 / PHI;
        this.vx *= damping;
        this.vy *= damping;
        this.vz *= damping;
        
        // Update position
        this.x += this.vx;
        this.y += this.vy;
        this.z += this.vz;
        
        // Decay heat over time
        this.heat *= 0.95;
    }
    
    /**
     * Override this for collision behavior
     */
    public void onCollision(PhiSuit<?> other) {
        // Default: absorb energy
        this.heat += other.amplitude * 0.1;
    }
    
    /**
     * Check collision (within fusion radius)
     */
    public boolean isColliding(PhiSuit<?> other, double radius) {
        return distanceTo(other) < radius;
    }
    
    @Override
    public String toString() {
        return String.format("[%s @ (%.1f,%.1f,%.1f) amp=%.1f heat=%.1f]", 
            label, x, y, z, amplitude, heat);
    }
}
