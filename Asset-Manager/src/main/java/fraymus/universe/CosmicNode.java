package fraymus.universe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 🧬 THE COSMIC NODE - Gen 126
 * A celestial body in the Hyper-Cosmos.
 * Data points are stars. They have Mass and Spin.
 * They orbit each other through N-dimensional space.
 * 
 * "Every thought is a star. Every connection is a wormhole."
 */
public class CosmicNode {
    
    private static final double PHI = 1.6180339887;
    
    public final String id;
    public Object soul;           // The data content
    public String name;           // Human-readable label
    
    // POSITION & VELOCITY in N-Dimensional space
    public HyperVector position;
    public HyperVector velocity;
    public HyperVector acceleration;
    
    // PHYSICAL PROPERTIES
    public double mass;           // Gravitational strength (intelligence density)
    public double radius;         // Event horizon size
    public double temperature;    // Activity level (hot = active)
    public double luminosity;     // Visibility/importance
    
    // QUANTUM STATE
    public double spin;           // -0.5 or +0.5 (fermion)
    public double charge;         // Positive/negative affinity
    public String entanglementId; // Shared with entangled partner
    public double waveFunction;   // Probability amplitude
    
    // ORBITAL MECHANICS
    public CosmicNode orbiting;   // What this node orbits (if any)
    public double orbitalPeriod;
    public double orbitalPhase;
    
    // WORMHOLES (Einstein-Rosen Bridges)
    public List<Wormhole> wormholes;
    
    // LIFECYCLE
    public long birthTime;
    public long lastInteraction;
    public int interactionCount;
    public boolean isBlackHole;   // Collapsed star (absorbs others)

    public CosmicNode(Object data, int dimensions) {
        this(data, data.toString().substring(0, Math.min(20, data.toString().length())), dimensions);
    }
    
    public CosmicNode(Object data, String name, int dimensions) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.soul = data;
        this.name = name;
        
        // Initialize in N-D space
        this.position = HyperVector.bigBang(dimensions);
        this.velocity = HyperVector.origin(dimensions);
        this.acceleration = HyperVector.origin(dimensions);
        
        // Default physical properties
        this.mass = 1.0;
        this.radius = 1.0;
        this.temperature = 5778;  // Like our Sun (Kelvin)
        this.luminosity = 1.0;
        
        // Quantum state
        this.spin = Math.random() > 0.5 ? 0.5 : -0.5;
        this.charge = 0;
        this.waveFunction = 1.0;
        
        // Wormholes
        this.wormholes = new ArrayList<>();
        
        // Lifecycle
        this.birthTime = System.currentTimeMillis();
        this.lastInteraction = birthTime;
        this.interactionCount = 0;
        this.isBlackHole = false;
    }

    /**
     * APPLY FORCE - Accumulate acceleration
     */
    public void applyForce(HyperVector force) {
        // F = ma → a = F/m
        HyperVector accel = force.copy();
        accel.scale(1.0 / mass);
        acceleration.add(accel);
    }
    
    /**
     * TICK - Update physics
     */
    public void tick(double deltaTime) {
        // Update velocity from acceleration
        HyperVector deltaV = acceleration.copy();
        deltaV.scale(deltaTime);
        velocity.add(deltaV);
        
        // Apply cosmic drag (entropy)
        velocity.scale(0.999);
        
        // Update position from velocity
        HyperVector deltaP = velocity.copy();
        deltaP.scale(deltaTime);
        position.add(deltaP);
        
        // Reset acceleration for next frame
        acceleration = HyperVector.origin(position.dimensions());
        
        // Cool down over time
        temperature *= 0.9999;
        if (temperature < 2.7) temperature = 2.7;  // CMB floor
        
        // Orbital mechanics
        if (orbiting != null) {
            orbitalPhase += (2 * Math.PI) / orbitalPeriod;
        }
    }
    
    /**
     * ENTANGLE - Create quantum link
     */
    public void entangle(CosmicNode other) {
        String key = UUID.randomUUID().toString().substring(0, 8);
        this.entanglementId = key;
        other.entanglementId = key;
        
        // Opposite spins (EPR pair)
        this.spin = 0.5;
        other.spin = -0.5;
    }
    
    /**
     * CREATE WORMHOLE - Einstein-Rosen Bridge
     */
    public Wormhole createWormhole(CosmicNode destination) {
        Wormhole wh = new Wormhole(this, destination);
        this.wormholes.add(wh);
        destination.wormholes.add(wh);
        return wh;
    }
    
    /**
     * COLLAPSE - Become a black hole
     */
    public void collapse() {
        this.isBlackHole = true;
        this.radius = 2 * mass;  // Schwarzschild radius (simplified)
        this.temperature = 0;    // No radiation escapes
        this.luminosity = 0;
    }
    
    /**
     * ABSORB - Black hole consumes another node
     */
    public void absorb(CosmicNode other) {
        if (!this.isBlackHole) return;
        
        // Gain mass
        this.mass += other.mass;
        
        // Absorb data (memory)
        if (this.soul instanceof String && other.soul instanceof String) {
            this.soul = this.soul + " ⊕ " + other.soul;
        }
        
        // Inherit wormholes
        for (Wormhole wh : other.wormholes) {
            wh.redirect(other, this);
            this.wormholes.add(wh);
        }
    }
    
    /**
     * INTERACT - Record interaction for tracking
     */
    public void interact() {
        this.lastInteraction = System.currentTimeMillis();
        this.interactionCount++;
        this.temperature += 100;  // Heat up on use
    }
    
    /**
     * DISTANCE - Euclidean distance in N-D
     */
    public double distanceTo(CosmicNode other) {
        return this.position.distanceTo(other.position);
    }
    
    /**
     * KINETIC ENERGY - 0.5 * m * v^2
     */
    public double kineticEnergy() {
        double v = velocity.magnitude();
        return 0.5 * mass * v * v;
    }
    
    /**
     * AGE - Time since birth
     */
    public long age() {
        return System.currentTimeMillis() - birthTime;
    }
    
    /**
     * SPECTRAL CLASS - Star classification based on temperature
     */
    public char spectralClass() {
        if (temperature > 30000) return 'O';
        if (temperature > 10000) return 'B';
        if (temperature > 7500) return 'A';
        if (temperature > 6000) return 'F';
        if (temperature > 5200) return 'G';  // Like our Sun
        if (temperature > 3700) return 'K';
        return 'M';  // Red dwarf
    }
    
    @Override
    public String toString() {
        String type = isBlackHole ? "◉" : "★";
        return String.format("%s[%s] M=%.2f T=%dK %s", 
            type, id, mass, (int)temperature, position);
    }
    
    /**
     * WORMHOLE - Connection that folds space
     */
    public static class Wormhole {
        public CosmicNode endpointA;
        public CosmicNode endpointB;
        public double stability;  // 0 to 1
        public double traversalCost;
        
        public Wormhole(CosmicNode a, CosmicNode b) {
            this.endpointA = a;
            this.endpointB = b;
            this.stability = 1.0;
            this.traversalCost = 0;
        }
        
        public CosmicNode traverse(CosmicNode from) {
            stability *= 0.99;  // Degrades with use
            if (from == endpointA) return endpointB;
            if (from == endpointB) return endpointA;
            return null;
        }
        
        public void redirect(CosmicNode oldEnd, CosmicNode newEnd) {
            if (endpointA == oldEnd) endpointA = newEnd;
            if (endpointB == oldEnd) endpointB = newEnd;
        }
        
        public double length() {
            return endpointA.distanceTo(endpointB);
        }
    }
}
