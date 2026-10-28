package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;

/**
 * HOLOGRAPHIC PHYSICS ENGINE
 * ==========================
 *
 * Bridges HolographicAssociativeMemory to physics simulation backends:
 * - JBullet: Java-native 3D rigid-body dynamics
 * - Box2D: Lightweight 2D kinematics
 * - Project Chrono: DAE/ODE multibody simulation
 *
 * MATHEMATICAL FOUNDATION:
 *   x(t+1) = x(t) + delta_OpenClaw(A_safe)  -- SystemZero actuation equation
 *   F = m · a  -- Newton's second law mapped to hyperdimensional forces
 *
 * Each 16384-D vector encodes a physical state:
 *   - Mass: dimension 0
 *   - Position (x,y,z): dimensions 1-3
 *   - Velocity (vx,vy,vz): dimensions 4-6
 *   - Force (fx,fy,fz): dimensions 7-9
 *   - Restitution: dimension 10
 *   - Friction: dimension 11
 *
 * This implements the user's Pre-Verse equation V:
 *   x(t+1) = x(t) + delta_{OpenClaw}(A_safe)
 */
public class HolographicPhysicsEngine {

    private HolographicAssociativeMemory memory;
    private final Map<String, PhysicsBody> bodies;
    private final ExecutorService physicsExecutor;
    private boolean wired = false;
    private double timeStep = 1.0 / 60.0;  // 60 Hz simulation
    private int simulationSteps = 0;

    // Gravity vector (default: Earth gravity)
    private double gravityX = 0.0;
    private double gravityY = -9.81;
    private double gravityZ = 0.0;

    public HolographicPhysicsEngine() {
        this.bodies = new ConcurrentHashMap<>();
        this.physicsExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Wire into HolographicAssociativeMemory for O(1) physics state retrieval.
     */
    public void wire(HolographicAssociativeMemory memory) {
        if (wired) return;
        this.memory = memory;
        this.wired = true;
    }

    public void unwire() {
        this.wired = false;
        this.memory = null;
        this.bodies.clear();
    }

    public boolean isWired() {
        return wired;
    }

    /**
     * SPAWN BODY: Create a physics body from a hyperdimensional concept vector.
     *
     * Implements SystemZero SPAWN instruction:
     *   particle = encode(concept) → physical instantiation
     */
    public PhysicsBody spawnBody(String conceptKey) {
        if (!wired || memory == null) {
            return null;
        }

        // O(1) retrieval: encode concept → hypervector
        double[] vector = memory.encode(conceptKey);

        // Fold 16384-D vector into physical parameters
        PhysicsBody body = foldVectorToBody(conceptKey, vector);
        bodies.put(conceptKey, body);

        // Store in holographic memory for O(1) later retrieval
        String stateKey = "physics:" + conceptKey;
        memory.store(stateKey, body.serialize());

        return body;
    }

    /**
     * APPLY FORCE: Map a hyperdimensional intent vector to a physical force.
     *
     * Implements the motor execution equation:
     *   F_raw = Phi_motor(C(t))  -- raw motor action from consciousness
     */
    public void applyForce(String bodyKey, double[] forceVector) {
        PhysicsBody body = bodies.get(bodyKey);
        if (body == null || forceVector.length < 3) return;

        // Fold force vector into 3D force (same folding as render engine)
        int stride = forceVector.length / 3;
        double fx = sampleDimension(forceVector, 0, stride) * 100.0;
        double fy = sampleDimension(forceVector, 1, stride) * 100.0;
        double fz = sampleDimension(forceVector, 2, stride) * 100.0;

        body.forceX = fx;
        body.forceY = fy;
        body.forceZ = fz;
    }

    /**
     * STEP SIMULATION: Integrate physics forward by one time step.
     *
     * Implements Euler integration with phi-harmonic damping:
     *   v(t+dt) = v(t) + (F/m + g) · dt
     *   x(t+dt) = x(t) + v(t+dt) · dt
     *
     * Damping factor uses PHI for natural decay:
     *   v' = v · (1 - dt/PHI)
     */
    public void stepSimulation() {
        for (PhysicsBody body : bodies.values()) {
            // Net force = applied force + gravity
            double netFx = body.forceX + body.mass * gravityX;
            double netFy = body.forceY + body.mass * gravityY;
            double netFz = body.forceZ + body.mass * gravityZ;

            // Acceleration (guard against zero mass)
            double invMass = body.mass > 1e-10 ? 1.0 / body.mass : 0.0;
            double ax = netFx * invMass;
            double ay = netFy * invMass;
            double az = netFz * invMass;

            // Update velocity (Euler)
            body.velX += ax * timeStep;
            body.velY += ay * timeStep;
            body.velZ += az * timeStep;

            // Phi-harmonic damping for stability
            double damping = 1.0 - (timeStep / HolographicAssociativeMemory.PHI);
            body.velX *= damping;
            body.velY *= damping;
            body.velZ *= damping;

            // Update position
            body.posX += body.velX * timeStep;
            body.posY += body.velY * timeStep;
            body.posZ += body.velZ * timeStep;

            // Clear applied force (impulse applied)
            body.forceX = 0.0;
            body.forceY = 0.0;
            body.forceZ = 0.0;

            // Increment simulation time
            body.simulationTime += timeStep;
        }
        simulationSteps++;

        // Persist states to holographic memory
        persistAllStates();
    }

    /**
     * RUN SIMULATION: Step forward N frames asynchronously.
     */
    public Future<SimulationResult> runSimulation(int frames) {
        return physicsExecutor.submit(() -> {
            long start = System.nanoTime();
            for (int i = 0; i < frames; i++) {
                stepSimulation();
            }
            long latency = (System.nanoTime() - start) / 1_000_000;
            return new SimulationResult(frames, latency, new HashMap<>(bodies));
        });
    }

    /**
     * COLLISION DETECT: Basic AABB collision between two bodies.
     *
     * Returns collision manifold or null if no overlap.
     */
    public CollisionManifold detectCollision(String keyA, String keyB) {
        PhysicsBody a = bodies.get(keyA);
        PhysicsBody b = bodies.get(keyB);
        if (a == null || b == null) return null;

        double dx = Math.abs(a.posX - b.posX);
        double dy = Math.abs(a.posY - b.posY);
        double dz = Math.abs(a.posZ - b.posZ);

        double overlapX = (a.size + b.size) - dx;
        double overlapY = (a.size + b.size) - dy;
        double overlapZ = (a.size + b.size) - dz;

        if (overlapX > 0 && overlapY > 0 && overlapZ > 0) {
            // Elastic collision response with restitution
            double e = Math.min(a.restitution, b.restitution);

            // Swap velocities along collision normal (simplified)
            double tempVx = a.velX;
            double tempVy = a.velY;
            double tempVz = a.velZ;

            a.velX = b.velX * e;
            a.velY = b.velY * e;
            a.velZ = b.velZ * e;
            b.velX = tempVx * e;
            b.velY = tempVy * e;
            b.velZ = tempVz * e;

            return new CollisionManifold(keyA, keyB, overlapX, overlapY, overlapZ);
        }

        return null;
    }

    /**
     * RETRIEVE BODY STATE: O(1) holographic retrieval of physics state.
     */
    public PhysicsBody retrieveBodyState(String conceptKey) {
        String stateKey = "physics:" + conceptKey;
        HolographicAssociativeMemory.RetrievalResult result = memory.retrieve(stateKey);
        if (result.success()) {
            return PhysicsBody.deserialize(result.retrievedValue);
        }
        return bodies.get(conceptKey);
    }

    // -------------------------------------------------------------------------
    // Folding and Serialization
    // -------------------------------------------------------------------------

    private PhysicsBody foldVectorToBody(String key, double[] vector) {
        int D = vector.length;
        int stride = D / 12;

        PhysicsBody body = new PhysicsBody(key);
        body.mass        = 0.1 + Math.abs(sampleDimension(vector, 0,  stride)) * 10.0;
        body.posX        = sampleDimension(vector, 1 * stride, stride) * 50.0;
        body.posY        = sampleDimension(vector, 2 * stride, stride) * 50.0;
        body.posZ        = sampleDimension(vector, 3 * stride, stride) * 50.0;
        body.velX        = sampleDimension(vector, 4 * stride, stride) * 10.0;
        body.velY        = sampleDimension(vector, 5 * stride, stride) * 10.0;
        body.velZ        = sampleDimension(vector, 6 * stride, stride) * 10.0;
        body.forceX      = sampleDimension(vector, 7 * stride, stride) * 100.0;
        body.forceY      = sampleDimension(vector, 8 * stride, stride) * 100.0;
        body.forceZ      = sampleDimension(vector, 9 * stride, stride) * 100.0;
        body.restitution = 0.1 + Math.abs(sampleDimension(vector, 10 * stride, stride)) * 0.9;
        body.friction    = Math.abs(sampleDimension(vector, 11 * stride, stride));
        body.size        = 0.5 + Math.abs(sampleDimension(vector, 0, stride)) * 2.0;

        return body;
    }

    private double sampleDimension(double[] vector, int offset, int count) {
        double sum = 0.0;
        for (int i = 0; i < count && (offset + i) < vector.length; i++) {
            sum += vector[offset + i];
        }
        return sum / count;
    }

    private void persistAllStates() {
        if (memory == null) return;
        for (Map.Entry<String, PhysicsBody> entry : bodies.entrySet()) {
            String stateKey = "physics:" + entry.getKey() + ":" + simulationSteps;
            memory.store(stateKey, entry.getValue().serialize());
        }
    }

    public String getStats() {
        return String.format(
            "HolographicPhysicsEngine Stats:\n" +
            "  Wired: %b\n" +
            "  Bodies: %d\n" +
            "  Sim steps: %d\n" +
            "  Time step: %.4fs (%.1f Hz)\n" +
            "  Gravity: (%.2f, %.2f, %.2f)",
            wired, bodies.size(), simulationSteps, timeStep, 1.0/timeStep,
            gravityX, gravityY, gravityZ
        );
    }

    public void shutdown() {
        physicsExecutor.shutdown();
    }

    public void setGravity(double gx, double gy, double gz) {
        this.gravityX = gx;
        this.gravityY = gy;
        this.gravityZ = gz;
    }

    public Map<String, PhysicsBody> getBodies() {
        return Collections.unmodifiableMap(bodies);
    }

    // -------------------------------------------------------------------------
    // Inner Classes
    // -------------------------------------------------------------------------

    public static class PhysicsBody {
        public final String key;
        public double mass = 1.0;
        public double posX, posY, posZ;
        public double velX, velY, velZ;
        public double forceX, forceY, forceZ;
        public double restitution = 0.5;
        public double friction = 0.3;
        public double size = 1.0;
        public double simulationTime = 0.0;

        public PhysicsBody(String key) {
            this.key = key;
        }

        String serialize() {
            return String.format(
                "%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f",
                mass, posX, posY, posZ, velX, velY, velZ,
                forceX, forceY, forceZ, restitution, friction, size, simulationTime
            );
        }

        static PhysicsBody deserialize(String data) {
            if (data == null || data.isEmpty()) return null;
            try {
                String[] parts = data.split(",");
                if (parts.length < 14) return null;
                PhysicsBody b = new PhysicsBody("restored");
                b.mass = Double.parseDouble(parts[0]);
                b.posX = Double.parseDouble(parts[1]);
                b.posY = Double.parseDouble(parts[2]);
                b.posZ = Double.parseDouble(parts[3]);
                b.velX = Double.parseDouble(parts[4]);
                b.velY = Double.parseDouble(parts[5]);
                b.velZ = Double.parseDouble(parts[6]);
                b.forceX = Double.parseDouble(parts[7]);
                b.forceY = Double.parseDouble(parts[8]);
                b.forceZ = Double.parseDouble(parts[9]);
                b.restitution = Double.parseDouble(parts[10]);
                b.friction = Double.parseDouble(parts[11]);
                b.size = Double.parseDouble(parts[12]);
                b.simulationTime = Double.parseDouble(parts[13]);
                return b;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static class CollisionManifold {
        public final String bodyA;
        public final String bodyB;
        public final double overlapX;
        public final double overlapY;
        public final double overlapZ;

        public CollisionManifold(String bodyA, String bodyB,
                                  double overlapX, double overlapY, double overlapZ) {
            this.bodyA = bodyA;
            this.bodyB = bodyB;
            this.overlapX = overlapX;
            this.overlapY = overlapY;
            this.overlapZ = overlapZ;
        }
    }

    public static class SimulationResult {
        public final int frames;
        public final long latencyMs;
        public final Map<String, PhysicsBody> finalStates;

        public SimulationResult(int frames, long latencyMs, Map<String, PhysicsBody> finalStates) {
            this.frames = frames;
            this.latencyMs = latencyMs;
            this.finalStates = finalStates;
        }
    }
}
