package fraymus.physics;

import fraymus.physics.math.Tensor2D;

/**
 * SOVEREIGN BODY
 * A physical entity in the Fraymus simulation.
 * Reacts to kinetic energy, mass, and gravity.
 * No external physics library. Pure mathematical existence.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class SovereignBody {

    public String id;
    public Tensor2D position;
    public Tensor2D velocity;
    public Tensor2D acceleration;
    public Tensor2D forceAccumulator;

    public float mass;
    public float inverseMass;
    public float restitution; // Bounciness (0.0 = clay, 1.0 = rubber)

    // Collision Boundary (AABB - Axis-Aligned Bounding Box)
    public float halfWidth, halfHeight;

    public SovereignBody(String id, float x, float y, float mass, float hw, float hh) {
        this.id = id;
        this.position = new Tensor2D(x, y);
        this.velocity = new Tensor2D();
        this.acceleration = new Tensor2D();
        this.forceAccumulator = new Tensor2D();

        this.mass = mass;
        this.inverseMass = (mass == 0.0f) ? 0.0f : 1.0f / mass; // mass 0 = static (unmovable)
        this.restitution = 0.5f;

        this.halfWidth = hw;
        this.halfHeight = hh;
    }

    public void addForce(Tensor2D force) {
        this.forceAccumulator.add(force);
    }

    public void clearForces() {
        this.forceAccumulator.x = 0;
        this.forceAccumulator.y = 0;
    }

    public float kineticEnergy() {
        float vSq = velocity.magnitudeSq();
        return 0.5f * mass * vSq;
    }

    @Override
    public String toString() {
        return String.format("[%s] pos=%s vel=%s mass=%.1f KE=%.2f",
            id, position, velocity, mass, kineticEnergy());
    }
}
