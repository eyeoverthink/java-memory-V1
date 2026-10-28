package fraymus.physics;

import fraymus.neural.AeonKronos;

/**
 * THE PHYSICAL BRIDGE (Phase 7)
 * "Data has Mass. Concepts have Gravity."
 *
 * Converts conceptual thoughts into 17-dimensional HyperRigidBodies
 * and drops them into the PhysicsConsole simulation.
 *
 * MASS CALCULATION (3 sources, combined):
 *   1. Shannon Entropy × String Length  (information density)
 *   2. Kronos 16,384-D Hamming Weight   (hyper-dimensional bit-mass)
 *   3. φ-Harmonic Amplifier             (resonance bonus for deep concepts)
 *
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class ThoughtManifestation {

    private static final double PHI = 1.618033988749895;

    /**
     * Manifest a thought into the physics simulation.
     * Creates a HyperRigidBody with mass derived from HDC vector weight,
     * Shannon entropy, and phi-harmonic resonance.
     *
     * @param concept The thought string
     * @return The spawned HyperRigidBody (already in the simulation)
     */
    public static HyperRigidBody manifest(String concept) {
        PhysicsConsole physics = PhysicsConsole.getInstance();

        System.out.println("   [BRIDGE] Manifesting thought into 17-D physics...");

        // 1. CALCULATE CONCEPTUAL MASS
        float mass = calculateHDCMass(concept);
        System.out.printf("   [BRIDGE] Vector Mass: %.2f kg (Shannon + Hamming + φ-Harmonic)%n", mass);

        // 2. Scale mesh vertices by mass (heavier thoughts = denser geometry)
        int meshVertices = Math.min(500, Math.max(50, (int) (mass * 2)));
        System.out.printf("   [BRIDGE] Mesh Density: %d vertices (Fibonacci Sphere)%n", meshVertices);

        // 3. SPAWN INTO SIMULATION (with HDC mass injected into constructor)
        HyperRigidBody body = physics.addBody(concept, meshVertices, mass);

        // 4. Inject an initial downward velocity (the "drop from the sky" effect)
        body.hyperVelocity[1] = -mass * 0.1;  // Y-axis = gravity direction in 17D

        System.out.printf("   [BRIDGE] Body [%s] dropped into simulation. %d total bodies.%n",
            body.id, physics.getBodyCount());
        System.out.printf("   [BRIDGE] Gravity: %s | Sim: %s%n",
            physics.isGravityEnabled() ? "LIVE" : "OFF",
            physics.isSimRunning() ? "RUNNING" : "IDLE");

        return body;
    }

    /**
     * Calculate mass from 3 sources:
     *  1. Shannon Entropy × length (information density)
     *  2. Kronos 16,384-D Hamming weight (bit-mass from hyper-dimensional vector)
     *  3. φ-Harmonic amplifier for deep/complex concepts
     */
    private static float calculateHDCMass(String concept) {
        // --- SOURCE 1: Shannon Entropy ---
        float shannonMass = shannonEntropy(concept) * concept.length() * 0.5f;

        // --- SOURCE 2: Kronos HDC Hamming Weight ---
        // Generate the 16,384-D vector and count set bits (popcount = "bit mass")
        long[] hdcVector = AeonKronos.generateDeterministicVector(concept.toUpperCase());
        long hammingWeight = 0;
        for (long chunk : hdcVector) {
            hammingWeight += Long.bitCount(chunk);
        }
        // Normalize: 16384 bits → max hamming weight = 16384 → scale to ~0-100
        float hdcMass = (float) hammingWeight / 164.0f;

        // --- SOURCE 3: φ-Harmonic Amplifier ---
        // Multi-word or deeply nested concepts get φ-resonance bonus
        int wordCount = concept.trim().split("\\s+").length;
        float phiAmplifier = (float) Math.pow(PHI, Math.min(wordCount, 7));

        // COMBINE: Shannon base + HDC weight, scaled by φ
        float totalMass = (shannonMass + hdcMass) * phiAmplifier;

        return Math.max(1.0f, totalMass);
    }

    /**
     * Shannon entropy: H = -Σ(p × log₂(p))
     */
    private static float shannonEntropy(String s) {
        if (s.isEmpty()) return 0.1f;
        int[] freq = new int[256];
        for (char c : s.toCharArray()) {
            if (c < 256) freq[c]++;
        }
        float entropy = 0;
        int total = s.length();
        for (int count : freq) {
            if (count > 0) {
                float p = (float) count / total;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }
}
