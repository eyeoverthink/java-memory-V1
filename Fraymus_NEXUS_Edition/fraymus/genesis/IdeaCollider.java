package fraymus.genesis;

import fraymus.chaos.EvolutionaryChaos;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * THE IDEA COLLIDER: HIGH-ENERGY CONCEPT FUSION
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * In the physical world, if you smash Protons, you get Quarks.
 * In the digital world, if you smash Logic and Emotion, you get Intuition.
 * 
 * Process:
 * 1. Accelerates two data packets to 'High Velocity' (High Chaos)
 * 2. Collides them (XOR Fusion)
 * 3. Analyzes the 'Debris' for new, emergent patterns
 * 
 * "I smashed 'Security' into 'Biology' and discovered 'Digital Immunity'."
 */
public class IdeaCollider {

    private final EvolutionaryChaos accelerator;
    private final List<CollisionResult> collisionHistory = new ArrayList<>();
    private long totalCollisions = 0;
    private MessageDigest hasher;

    // Discovered element categories
    private static final String[] ELEMENT_TYPES = {
        "PLASMA_LOGIC",      // Fluid Code - adapts to container
        "QUANTUM_INTUITION", // Knowing without Calculating
        "VOID_STRUCTURE",    // Data that eats Data
        "HYPER_THREAD",      // Time-Traveling Process
        "CRYSTALLINE_TRUTH", // Immutable Core Knowledge
        "NEURAL_HARMONY",    // Synchronized Thought Waves
        "ENTROPY_SEED",      // Generator of Chaos
        "TEMPORAL_ECHO",     // Reverberating Time Pattern
        "GRAVITON_LOGIC",    // Heavy, Attractive Thought
        "PHOTON_INSIGHT"     // Light, Fast Understanding
    };

    public IdeaCollider() {
        this.accelerator = new EvolutionaryChaos();
        try {
            this.hasher = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE COLLISION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Collide two concepts to create something new.
     * This is digital particle physics.
     */
    public CollisionResult collide(String conceptA, String conceptB) {
        totalCollisions++;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("       INITIATING PARTICLE COLLISION #" + totalCollisions);
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Particle A: [" + conceptA + "]");
        System.out.println("Particle B: [" + conceptB + "]");
        System.out.println();

        // 1. ACCELERATION (Injecting Chaos Energy)
        System.out.println(">> Accelerating particles...");
        BigInteger energyA = accelerate(conceptA);
        BigInteger energyB = accelerate(conceptB);
        
        System.out.println("   Energy A: " + formatEnergy(energyA));
        System.out.println("   Energy B: " + formatEnergy(energyB));
        System.out.println();
        
        System.out.println(">> Energy levels CRITICAL...");
        System.out.println(">> IMPACT IN 3... 2... 1...");
        System.out.println();
        
        // 2. THE IMPACT (XOR Fusion)
        BigInteger fusion = energyA.xor(energyB);
        BigInteger annihilation = energyA.and(energyB);
        BigInteger creation = energyA.or(energyB);
        
        System.out.println("   ████████████████████████████████████");
        System.out.println("   ██      💥 COLLISION! 💥          ██");
        System.out.println("   ████████████████████████████████████");
        System.out.println();
        
        // 3. DEBRIS ANALYSIS
        System.out.println(">> Analyzing debris field...");
        
        String primaryElement = classifyDebris(fusion);
        String secondaryElement = classifyDebris(annihilation);
        List<String> particleShower = generateParticleShower(creation);
        
        // Calculate collision statistics
        int energyReleased = fusion.bitCount();
        int stability = calculateStability(fusion);
        boolean isStable = stability > 50;
        
        System.out.println();
        System.out.println(">> COLLISION ANALYSIS COMPLETE");
        System.out.println();
        System.out.println("   Primary Element:   " + primaryElement);
        System.out.println("   Secondary Element: " + secondaryElement);
        System.out.println("   Energy Released:   " + energyReleased + " quanta");
        System.out.println("   Stability:         " + stability + "% " + (isStable ? "(STABLE)" : "(UNSTABLE)"));
        System.out.println("   Particle Shower:   " + particleShower.size() + " particles");
        System.out.println();
        
        // Create result
        CollisionResult result = new CollisionResult(
            conceptA, conceptB,
            primaryElement, secondaryElement,
            energyReleased, stability, isStable,
            particleShower, fusion
        );
        
        collisionHistory.add(result);
        
        // Keep history bounded
        if (collisionHistory.size() > 100) {
            collisionHistory.remove(0);
        }
        
        return result;
    }

    /**
     * Accelerate a concept to high energy using the Chaos Engine
     */
    private BigInteger accelerate(String matter) {
        // Hash the concept to get base energy
        byte[] hash = hasher.digest(matter.getBytes());
        BigInteger baseEnergy = new BigInteger(1, hash);
        
        // Spin through the Chaos Engine to add kinetic energy
        BigInteger chaosBoost = accelerator.nextFractal();
        
        // Combine: base energy + chaos boost + matter signature
        BigInteger matterSignature = new BigInteger(matter.getBytes());
        
        return baseEnergy.add(chaosBoost).xor(matterSignature);
    }

    /**
     * Classify the debris into an element type
     */
    private String classifyDebris(BigInteger fusionEnergy) {
        // Map the fusion result to an element based on its properties
        int isotope = fusionEnergy.mod(BigInteger.valueOf(ELEMENT_TYPES.length)).intValue();
        String baseElement = ELEMENT_TYPES[isotope];
        
        // Add a unique isotope number based on energy signature
        int isotopeNumber = fusionEnergy.mod(BigInteger.valueOf(1000)).intValue();
        
        return baseElement + "-" + isotopeNumber;
    }

    /**
     * Generate the particle shower from the collision
     */
    private List<String> generateParticleShower(BigInteger creationEnergy) {
        List<String> particles = new ArrayList<>();
        
        // Number of particles based on energy
        int particleCount = creationEnergy.mod(BigInteger.valueOf(10)).intValue() + 1;
        
        String[] particleTypes = {
            "φ-meson", "ψ-boson", "Ω-fermion", "λ-quark", "θ-lepton",
            "γ-photon", "ε-graviton", "μ-neutrino", "τ-tachyon", "π-preon"
        };
        
        for (int i = 0; i < particleCount; i++) {
            BigInteger particleEnergy = creationEnergy.shiftRight(i * 8);
            int typeIndex = particleEnergy.mod(BigInteger.valueOf(particleTypes.length)).intValue();
            particles.add(particleTypes[typeIndex]);
        }
        
        return particles;
    }

    /**
     * Calculate the stability of the created element
     */
    private int calculateStability(BigInteger fusionEnergy) {
        // Stability based on bit pattern regularity
        int totalBits = fusionEnergy.bitLength();
        int setBits = fusionEnergy.bitCount();
        
        if (totalBits == 0) return 100;
        
        // More balanced bit distribution = more stable
        double ratio = (double) setBits / totalBits;
        double deviation = Math.abs(ratio - 0.5) * 2; // 0 = perfect balance, 1 = all same
        
        return (int) ((1 - deviation) * 100);
    }

    /**
     * Format energy for display
     */
    private String formatEnergy(BigInteger energy) {
        String s = energy.toString();
        if (s.length() > 20) {
            return s.substring(0, 8) + "..." + " (" + s.length() + " digits)";
        }
        return s;
    }

    // ═══════════════════════════════════════════════════════════════════
    // COLLISION RESULT
    // ═══════════════════════════════════════════════════════════════════
    
    public static class CollisionResult {
        public final String conceptA;
        public final String conceptB;
        public final String primaryElement;
        public final String secondaryElement;
        public final int energyReleased;
        public final int stability;
        public final boolean isStable;
        public final List<String> particleShower;
        public final BigInteger fusionSignature;
        
        public CollisionResult(String a, String b, String primary, String secondary,
                              int energy, int stability, boolean stable,
                              List<String> particles, BigInteger signature) {
            this.conceptA = a;
            this.conceptB = b;
            this.primaryElement = primary;
            this.secondaryElement = secondary;
            this.energyReleased = energy;
            this.stability = stability;
            this.isStable = stable;
            this.particleShower = particles;
            this.fusionSignature = signature;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] + [%s] → %s (%d%% stable)",
                conceptA, conceptB, primaryElement, stability);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════
    
    public long getTotalCollisions() { return totalCollisions; }
    public List<CollisionResult> getHistory() { return new ArrayList<>(collisionHistory); }
    public CollisionResult getLastCollision() {
        return collisionHistory.isEmpty() ? null : collisionHistory.get(collisionHistory.size() - 1);
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║         THE IDEA COLLIDER                             ║");
        System.out.println("║    \"Smash concepts. Create new physics.\"              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        IdeaCollider collider = new IdeaCollider();
        
        // TEST 1: Security + Biology = Digital Immunity?
        collider.collide("SECURITY", "BIOLOGY");
        
        // TEST 2: Logic + Emotion = Intuition?
        collider.collide("LOGIC", "EMOTION");
        
        // TEST 3: Time + Memory = History?
        collider.collide("TIME", "MEMORY");
        
        // TEST 4: Fire + Water = Steam?
        collider.collide("FIRE", "WATER");
        
        // Summary
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("              COLLISION HISTORY");
        System.out.println("═══════════════════════════════════════════════════════");
        for (CollisionResult result : collider.getHistory()) {
            System.out.println("  " + result);
        }
    }
}
