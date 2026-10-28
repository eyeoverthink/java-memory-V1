package fraymus.physics;

import fraymus.physics.math.FrayMath.Vec2;

/**
 * ONTOLOGICAL BODY
 * A fully realized rigid body capable of rotation, friction, and kinetic transfer.
 * Possesses angle, angular velocity, and moment of inertia (resistance to rotation).
 *
 * Linear state: position, velocity, force, mass
 * Angular state: angle, angularVelocity, torque, inertia
 * Material: staticFriction, dynamicFriction, restitution
 * Shape: width, height (Oriented Bounding Box)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class OntologicalBody {

    // --- Linear State ---
    public Vec2 position = new Vec2();
    public Vec2 velocity = new Vec2();
    public Vec2 force = new Vec2();
    public float mass, invMass;

    // --- Angular State ---
    public float angle = 0.0f;           // Rotation in radians
    public float angularVelocity = 0.0f;
    public float torque = 0.0f;
    public float inertia, invInertia;    // Resistance to rotation

    // --- Material Properties ---
    public float staticFriction = 0.5f;
    public float dynamicFriction = 0.3f;
    public float restitution = 0.2f;     // Bounciness

    // --- Shape (Oriented Bounding Box) ---
    public float width, height;

    // --- Identity ---
    public String id;

    public OntologicalBody(String id, float x, float y, float width, float height, float mass) {
        this.id = id;
        this.position.x = x;
        this.position.y = y;
        this.width = width;
        this.height = height;
        this.mass = mass;

        if (mass == 0.0f) {
            this.invMass = 0.0f;
            this.inertia = 0.0f;
            this.invInertia = 0.0f;
        } else {
            this.invMass = 1.0f / mass;
            // Standard moment of inertia for a rectangle: I = (m / 12) * (w^2 + h^2)
            this.inertia = (mass / 12.0f) * (width * width + height * height);
            this.invInertia = 1.0f / this.inertia;
        }
    }

    public void addForce(Vec2 f) { force.add(f); }
    public void addTorque(float t) { torque += t; }

    public float kineticEnergy() {
        float linear = 0.5f * mass * velocity.lengthSq();
        float rotational = 0.5f * inertia * angularVelocity * angularVelocity;
        return linear + rotational;
    }

    @Override
    public String toString() {
        return String.format("[%s] pos=%s vel=%s ang=%.2frad w=%.2f/s mass=%.1f KE=%.2f",
            id, position, velocity, angle, angularVelocity, mass, kineticEnergy());
    }
}
