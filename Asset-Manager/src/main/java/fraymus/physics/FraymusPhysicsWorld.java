package fraymus.physics;

import fraymus.physics.math.FrayMath.Vec2;
import java.util.ArrayList;
import java.util.List;

/**
 * FRAYMUS PHYSICS WORLD
 * The sovereign reality engine. Replaces JBox2D entirely.
 *
 * Integration: Semi-Implicit Euler (linear + angular)
 * Broadphase: Circle bounding detection
 * Narrowphase: ContactManifold generation
 * Solver: Sequential Impulse with friction + positional correction
 *
 * No external libraries. Pure mathematical existence.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class FraymusPhysicsWorld {

    private final List<OntologicalBody> bodies = new ArrayList<>();
    private final Vec2 gravity = new Vec2(0, -9.81f);

    // Statistics
    private long totalSteps = 0;
    private long totalCollisions = 0;
    private float simulatedTime = 0.0f;

    public void add(OntologicalBody b) {
        bodies.add(b);
        System.out.printf("   [WORLD] Spawned '%s' at %s mass=%.1f size=(%.1f x %.1f) I=%.2f\n",
            b.id, b.position, b.mass, b.width, b.height, b.inertia);
    }

    public void remove(String id) {
        bodies.removeIf(b -> b.id.equals(id));
    }

    public OntologicalBody get(String id) {
        for (OntologicalBody b : bodies) {
            if (b.id.equals(id)) return b;
        }
        return null;
    }

    public List<OntologicalBody> getBodies() { return bodies; }

    public void setGravity(float gx, float gy) {
        gravity.set(gx, gy);
    }

    /**
     * THE PHYSICAL CLOCK TICK
     * @param dt Delta time
     * @param iterations Number of impulse solver iterations (higher = more accurate)
     */
    public void step(float dt, int iterations) {
        totalSteps++;
        simulatedTime += dt;

        // 1. INTEGRATION (Move bodies based on velocity and forces)
        for (OntologicalBody b : bodies) {
            if (b.invMass == 0.0f) continue;

            // Apply gravity: F = m * g
            b.addForce(new Vec2(gravity.x * b.mass, gravity.y * b.mass));

            // Integrate velocity (linear + angular)
            b.velocity.x += (b.force.x * b.invMass) * dt;
            b.velocity.y += (b.force.y * b.invMass) * dt;
            b.angularVelocity += (b.torque * b.invInertia) * dt;

            // Integrate position and rotation
            b.position.x += b.velocity.x * dt;
            b.position.y += b.velocity.y * dt;
            b.angle += b.angularVelocity * dt;

            // Clear forces for next tick
            b.force.x = 0; b.force.y = 0;
            b.torque = 0;
        }

        // 2. GENERATE MANIFOLDS (Collision Detection)
        List<ContactManifold> contacts = new ArrayList<>();
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                OntologicalBody a = bodies.get(i);
                OntologicalBody b = bodies.get(j);

                if (a.invMass == 0 && b.invMass == 0) continue;

                // Circular bounding broadphase for rapid detection
                float distSq = (a.position.x - b.position.x) * (a.position.x - b.position.x) +
                               (a.position.y - b.position.y) * (a.position.y - b.position.y);
                float radiusSum = (a.width / 2) + (b.width / 2);

                if (distSq < radiusSum * radiusSum) {
                    ContactManifold m = new ContactManifold(a, b);
                    float dist = (float) Math.sqrt(distSq);

                    if (dist == 0.0f) {
                        m.penetration = a.width / 2;
                        m.normal = new Vec2(1, 0);
                        m.contactPoint = new Vec2(a.position.x, a.position.y);
                    } else {
                        m.penetration = radiusSum - dist;
                        m.normal = new Vec2(
                            (b.position.x - a.position.x) / dist,
                            (b.position.y - a.position.y) / dist);
                        m.contactPoint = new Vec2(
                            a.position.x + m.normal.x * (a.width / 2),
                            a.position.y + m.normal.y * (a.width / 2));
                    }
                    contacts.add(m);
                    totalCollisions++;
                }
            }
        }

        // 3. RESOLVE COLLISIONS (Sequential Impulse Solver — multiple iterations)
        for (int iter = 0; iter < iterations; iter++) {
            for (ContactManifold m : contacts) {
                m.resolve();
            }
        }

        // 4. POSITIONAL CORRECTION (Fix sinking)
        for (ContactManifold m : contacts) {
            m.applyPositionalCorrection();
        }
    }

    /**
     * Run N steps of simulation and report.
     */
    public void simulate(int steps, float dt, int solverIterations) {
        System.out.printf("   [WORLD] Simulating %d steps (dt=%.4f, solver=%d iterations)...\n",
            steps, dt, solverIterations);

        for (int s = 0; s < steps; s++) {
            step(dt, solverIterations);

            if (steps >= 10 && s % (steps / 10) == 0) {
                System.out.printf("      [T=%.3fs] ", simulatedTime);
                for (OntologicalBody b : bodies) {
                    if (b.invMass > 0) {
                        System.out.printf("%s ", b);
                    }
                }
                System.out.println();
            }
        }

        System.out.printf("   [WORLD] Complete. %d collisions resolved.\n", totalCollisions);
    }

    // --- State ---
    public long getTotalSteps() { return totalSteps; }
    public long getTotalCollisions() { return totalCollisions; }
    public float getSimulatedTime() { return simulatedTime; }

    public String getStatus() {
        float totalKE = 0;
        for (OntologicalBody b : bodies) totalKE += b.kineticEnergy();
        return String.format(
            "WORLD: %d bodies | %d steps | %.2fs | %d collisions | KE=%.2f | g=%s",
            bodies.size(), totalSteps, simulatedTime, totalCollisions, totalKE, gravity);
    }

    public static void main(String[] args) {
        FraymusPhysicsWorld world = new FraymusPhysicsWorld();

        // Static ground
        OntologicalBody ground = new OntologicalBody("GROUND", 0, -10, 100, 2, 0);
        world.add(ground);

        // Falling box
        OntologicalBody box = new OntologicalBody("BOX", 0, 20, 2, 2, 5.0f);
        box.restitution = 0.6f;
        box.angularVelocity = 1.5f; // Spinning!
        world.add(box);

        // Second body offset (will collide mid-air)
        OntologicalBody sphere = new OntologicalBody("SPHERE", 1.5f, 25, 1.5f, 1.5f, 3.0f);
        sphere.restitution = 0.8f;
        world.add(sphere);

        // Simulate 5 seconds at 60fps with 8 solver iterations
        world.simulate(300, 1.0f / 60.0f, 8);

        System.out.println("\n   " + world.getStatus());
        for (OntologicalBody b : world.getBodies()) {
            System.out.println("   " + b);
        }
    }
}
