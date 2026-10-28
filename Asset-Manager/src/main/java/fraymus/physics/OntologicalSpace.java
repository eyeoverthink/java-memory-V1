package fraymus.physics;

import fraymus.physics.math.Tensor2D;
import java.util.ArrayList;
import java.util.List;

/**
 * ONTOLOGICAL SPACE ENGINE
 * A 100% dependency-free physics simulator.
 * Calculates gravity, movement, and collision resolution mathematically.
 *
 * Integration: Semi-Implicit Euler Method
 * Collision: AABB detection + Sequential Impulse resolution
 * Restitution: Newton's Law of Restitution (coefficient of bounciness)
 *
 * No JBox2D. No external libraries. Pure sovereign mathematics.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class OntologicalSpace {

    private final List<SovereignBody> bodies = new ArrayList<>();
    private final Tensor2D gravity = new Tensor2D(0, -9.81f); // Downward gravitational force

    // Statistics
    private long totalSteps = 0;
    private long totalCollisions = 0;
    private float simulatedTime = 0.0f;

    public void addBody(SovereignBody body) {
        bodies.add(body);
        System.out.printf("   [PHYS] Spawned '%s' at %s mass=%.1f size=(%.1f x %.1f)\n",
            body.id, body.position, body.mass, body.halfWidth * 2, body.halfHeight * 2);
    }

    public void removeBody(SovereignBody body) {
        bodies.remove(body);
    }

    public void removeBody(String id) {
        bodies.removeIf(b -> b.id.equals(id));
    }

    public SovereignBody getBody(String id) {
        for (SovereignBody b : bodies) {
            if (b.id.equals(id)) return b;
        }
        return null;
    }

    public List<SovereignBody> getBodies() { return bodies; }

    /**
     * Set custom gravity vector.
     */
    public void setGravity(float gx, float gy) {
        gravity.set(gx, gy);
    }

    /**
     * THE PHYSICAL CLOCK TICK
     * @param dt Delta Time (e.g., 0.016f for 60FPS)
     */
    public void step(float dt) {
        totalSteps++;
        simulatedTime += dt;

        // 1. INTEGRATION (Apply forces and move bodies)
        for (SovereignBody b : bodies) {
            if (b.inverseMass == 0.0f) continue; // Static objects don't move

            // Apply Gravity: F = m * g
            Tensor2D weight = new Tensor2D(gravity.x, gravity.y);
            weight.multiply(b.mass);
            b.addForce(weight);

            // Calculate Acceleration: a = F / m
            b.acceleration.x = b.forceAccumulator.x * b.inverseMass;
            b.acceleration.y = b.forceAccumulator.y * b.inverseMass;

            // Semi-Implicit Euler Integration
            // v_{t+1} = v_t + a * dt
            b.velocity.x += b.acceleration.x * dt;
            b.velocity.y += b.acceleration.y * dt;

            // p_{t+1} = p_t + v * dt
            b.position.x += b.velocity.x * dt;
            b.position.y += b.velocity.y * dt;

            b.clearForces();
        }

        // 2. COLLISION DETECTION & RESOLUTION (O(N^2) naive broadphase)
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                SovereignBody a = bodies.get(i);
                SovereignBody b = bodies.get(j);

                if (a.inverseMass == 0 && b.inverseMass == 0) continue;

                if (checkAABB(a, b)) {
                    resolveCollision(a, b);
                    totalCollisions++;
                }
            }
        }
    }

    /**
     * Run N steps of simulation at the given dt and report.
     */
    public void simulate(int steps, float dt) {
        System.out.printf("   [PHYS] Simulating %d steps (dt=%.4f, %.2fs total)...\n",
            steps, dt, steps * dt);

        for (int s = 0; s < steps; s++) {
            step(dt);

            // Report every 10% of simulation
            if (steps >= 10 && s % (steps / 10) == 0) {
                System.out.printf("      [T=%.3fs] ", simulatedTime);
                for (SovereignBody b : bodies) {
                    if (b.inverseMass > 0) {
                        System.out.printf("%s ", b);
                    }
                }
                System.out.println();
            }
        }

        System.out.printf("   [PHYS] Simulation complete. %d collisions resolved.\n", totalCollisions);
    }

    // --- MATHEMATICAL COLLISION LOGIC ---

    private boolean checkAABB(SovereignBody a, SovereignBody b) {
        boolean overlapX = Math.abs(a.position.x - b.position.x) < (a.halfWidth + b.halfWidth);
        boolean overlapY = Math.abs(a.position.y - b.position.y) < (a.halfHeight + b.halfHeight);
        return overlapX && overlapY;
    }

    private void resolveCollision(SovereignBody a, SovereignBody b) {
        // 1. Calculate relative velocity
        Tensor2D rv = Tensor2D.sub(b.velocity, a.velocity);

        // 2. Calculate collision normal (simplified: pointing from A to B)
        Tensor2D normal = Tensor2D.sub(b.position, a.position);
        normal.normalize();

        // 3. Relative velocity along the normal
        float velAlongNormal = rv.dot(normal);

        // Do not resolve if velocities are separating
        if (velAlongNormal > 0) return;

        // 4. Restitution (bounciness) — take the minimum
        float e = Math.min(a.restitution, b.restitution);

        // 5. Impulse scalar: j = -(1 + e) * V.n / (1/Ma + 1/Mb)
        float j = -(1.0f + e) * velAlongNormal;
        j /= (a.inverseMass + b.inverseMass);

        // 6. Apply impulse vector
        Tensor2D impulse = new Tensor2D(normal.x, normal.y);
        impulse.multiply(j);

        a.velocity.x -= (impulse.x * a.inverseMass);
        a.velocity.y -= (impulse.y * a.inverseMass);

        b.velocity.x += (impulse.x * b.inverseMass);
        b.velocity.y += (impulse.y * b.inverseMass);

        // 7. Positional Correction (prevent sinking)
        positionalCorrection(a, b, normal);
    }

    private void positionalCorrection(SovereignBody a, SovereignBody b, Tensor2D normal) {
        final float percent = 0.2f;
        final float slop = 0.01f;

        float depthX = (a.halfWidth + b.halfWidth) - Math.abs(a.position.x - b.position.x);
        float depthY = (a.halfHeight + b.halfHeight) - Math.abs(a.position.y - b.position.y);
        float penetration = Math.min(depthX, depthY);

        if (penetration < slop) return;

        float correctionMagnitude = (Math.max(penetration - slop, 0.0f) / (a.inverseMass + b.inverseMass)) * percent;
        Tensor2D correction = new Tensor2D(normal.x * correctionMagnitude, normal.y * correctionMagnitude);

        a.position.x -= correction.x * a.inverseMass;
        a.position.y -= correction.y * a.inverseMass;

        b.position.x += correction.x * b.inverseMass;
        b.position.y += correction.y * b.inverseMass;
    }

    // --- State ---
    public long getTotalSteps() { return totalSteps; }
    public long getTotalCollisions() { return totalCollisions; }
    public float getSimulatedTime() { return simulatedTime; }
    public int getBodyCount() { return bodies.size(); }

    public String getStatus() {
        float totalKE = 0;
        for (SovereignBody b : bodies) totalKE += b.kineticEnergy();
        return String.format(
            "PHYS: %d bodies | %d steps | %.2fs | %d collisions | KE=%.2f | g=%s",
            bodies.size(), totalSteps, simulatedTime, totalCollisions, totalKE, gravity);
    }

    public static void main(String[] args) {
        OntologicalSpace space = new OntologicalSpace();

        // Create a static ground plane
        SovereignBody ground = new SovereignBody("GROUND", 0, -10, 0, 50, 1);
        space.addBody(ground);

        // Create a dynamic falling body
        SovereignBody ball = new SovereignBody("BALL", 0, 20, 5.0f, 1, 1);
        ball.restitution = 0.8f;
        space.addBody(ball);

        // Create a second body
        SovereignBody box = new SovereignBody("BOX", 2, 30, 3.0f, 1.5f, 1.5f);
        box.restitution = 0.3f;
        space.addBody(box);

        // Simulate 5 seconds at 60fps
        space.simulate(300, 1.0f / 60.0f);

        System.out.println("\n   " + space.getStatus());
        for (SovereignBody b : space.getBodies()) {
            System.out.println("   " + b);
        }
    }
}
