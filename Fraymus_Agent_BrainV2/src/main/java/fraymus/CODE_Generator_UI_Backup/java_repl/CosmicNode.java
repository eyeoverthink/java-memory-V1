/**
 * CosmicNode.java - Celestial Body in N-Dimensional Space
 * 
 * 🧬 THE STAR SYSTEM
 * 
 * Data points are celestial bodies with:
 * - Position in N-D space
 * - Velocity (momentum)
 * - Mass (gravitational influence)
 * - Spin (quantum state: -1/2 or +1/2)
 * - Soul (the actual data)
 * 
 * They orbit each other through N-dimensional gravity.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 126 (Hyper-Cosmos)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * A celestial body in the N-dimensional universe.
 */
public class CosmicNode {
    
    private static final double PHI = 1.618033988749895;
    
    public final String id;
    public Object soul; // The data this node represents
    
    // POSITION & MOTION in N-D space
    public HyperVector position;
    public HyperVector velocity;
    public HyperVector acceleration;
    
    // PHYSICAL PROPERTIES
    public double mass; // Intelligence density (affects gravity)
    public double spin; // Quantum alignment (-0.5 or +0.5)
    public double charge; // Electromagnetic property
    public double temperature; // Activity level
    
    // METADATA
    public long birthTime;
    public int collisionCount;
    
    /**
     * Create cosmic node at random position.
     */
    public CosmicNode(Object data, int dimensions) {
        this.id = UUID.randomUUID().toString();
        this.soul = data;
        
        // Random position in N-D space
        this.position = HyperVector.random(dimensions, 1000.0);
        this.velocity = new HyperVector(dimensions); // Starts at rest
        this.acceleration = new HyperVector(dimensions);
        
        // Physical properties
        this.mass = 1.0 + Math.random(); // 1.0 to 2.0
        this.spin = Math.random() < 0.5 ? -0.5 : 0.5; // Quantum spin
        this.charge = (Math.random() - 0.5) * 2.0; // -1.0 to +1.0
        this.temperature = PHI; // Start at φ
        
        this.birthTime = System.currentTimeMillis();
        this.collisionCount = 0;
    }
    
    /**
     * Apply force vector (F = ma → a = F/m).
     */
    public void applyForce(HyperVector force) {
        HyperVector accel = force.clone();
        accel.scale(1.0 / mass);
        acceleration.add(accel);
    }
    
    /**
     * Update position and velocity (Verlet integration).
     */
    public void tick(double dt) {
        // Update velocity: v = v + a*dt
        HyperVector deltaV = acceleration.clone();
        deltaV.scale(dt);
        velocity.add(deltaV);
        
        // Apply cosmic drag (entropy)
        velocity.scale(0.99);
        
        // Update position: p = p + v*dt
        HyperVector deltaP = velocity.clone();
        deltaP.scale(dt);
        position.add(deltaP);
        
        // Cool down
        temperature *= 0.995;
        
        // Reset acceleration for next frame
        acceleration = new HyperVector(position.dimensions());
    }
    
    /**
     * Check if this node is entangled with another (same spin).
     */
    public boolean isEntangledWith(CosmicNode other) {
        return Math.abs(this.spin - other.spin) < 0.01;
    }
    
    /**
     * Collide with another node (merge or bounce).
     */
    public void collide(CosmicNode other) {
        this.collisionCount++;
        other.collisionCount++;
        
        // Heat up from collision
        this.temperature += 0.1;
        other.temperature += 0.1;
        
        // Exchange momentum (simplified)
        HyperVector temp = this.velocity.clone();
        this.velocity = other.velocity.clone();
        other.velocity = temp;
    }
    
    /**
     * Get age in milliseconds.
     */
    public long getAge() {
        return System.currentTimeMillis() - birthTime;
    }
    
    @Override
    public String toString() {
        return String.format("CosmicNode[%s, mass=%.2f, spin=%.1f, temp=%.2f]",
            soul, mass, spin, temperature);
    }
}
