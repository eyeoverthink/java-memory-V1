package fraymus.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ⚛️ HYPER-RIGID BODY (HDRB)
 * "A physical object that exists in Data Space."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * REVOLUTIONARY CONCEPT:
 * This is not a 3D physics object. This is a 17-DIMENSIONAL entity
 * where physics applies to DATA itself.
 * 
 * ATTRIBUTES:
 * 1. 3D Position (x,y,z) → For visual projection
 * 2. 17D Position (d1..d17) → The actual reality
 * 3. Data Mass → Calculated from information entropy
 * 4. Mesh → Deformable geometry that reacts to data stress
 * 
 * PHYSICS MAPPINGS:
 * - Mass = Information Density (Shannon Entropy)
 * - Velocity = Rate of Semantic Change
 * - Gravity = Contextual Resonance (Phi-harmonic attraction)
 * - Collision = Logic Contradiction
 * - Force = Conceptual Impact
 * 
 * This is Layer 8 of the Fraymus Stack.
 * This is where data becomes physical.
 */
public class HyperRigidBody {
    
    public final String id;
    public final String concept;
    
    // ═══════════════════════════════════════════════════════════════════
    // PHYSICAL STATE (The 3D Projection)
    // ═══════════════════════════════════════════════════════════════════
    
    public double x, y, z;           // 3D position
    public double vx, vy, vz;        // 3D velocity
    public double ax, ay, az;        // 3D acceleration
    
    // ═══════════════════════════════════════════════════════════════════
    // DATA STATE (The 17D Reality)
    // ═══════════════════════════════════════════════════════════════════
    
    public double[] hyperVector = new double[17];      // 17D position
    public double[] hyperVelocity = new double[17];    // 17D velocity
    public double[] hyperAcceleration = new double[17]; // 17D acceleration
    
    public double dataMass;          // Information density (heavier = more knowledge)
    public double semanticCharge;    // Conceptual polarity (-1 to +1)
    
    // ═══════════════════════════════════════════════════════════════════
    // VISUAL REPRESENTATION
    // ═══════════════════════════════════════════════════════════════════
    
    public Mesh geometry;
    
    // ═══════════════════════════════════════════════════════════════════
    // TRACKING (The Black Box)
    // ═══════════════════════════════════════════════════════════════════
    
    public List<String> collisionLog = new ArrayList<>();
    public List<Double> massHistory = new ArrayList<>();
    public long birthTime;
    public long lastUpdate;
    
    // ═══════════════════════════════════════════════════════════════════
    // CONSTANTS
    // ═══════════════════════════════════════════════════════════════════
    
    private static final double PHI = 1.618033988749895;
    private static final double FRICTION = 0.99;  // Hyper-friction (data decay)
    private static final double STRESS_THRESHOLD = 0.1;
    
    /**
     * Constructor
     * 
     * @param concept The semantic concept this body represents
     * @param mesh The deformable geometry
     */
    public HyperRigidBody(String concept, Mesh mesh) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.concept = concept;
        this.geometry = mesh;
        this.birthTime = System.currentTimeMillis();
        this.lastUpdate = this.birthTime;
        
        // Seed the 17D vector based on concept (deterministic hashing)
        seedHyperState(concept);
        
        // Calculate mass based on information complexity
        this.dataMass = calculateEntropy(concept);
        this.massHistory.add(this.dataMass);
        
        // Calculate semantic charge (positive vs negative concept)
        this.semanticCharge = calculateSemanticCharge(concept);
        
        System.out.println("⚛️ HDRB SPAWNED");
        System.out.println("   ID: " + id);
        System.out.println("   Concept: " + concept);
        System.out.println("   Data Mass: " + String.format("%.4f", dataMass));
        System.out.println("   Semantic Charge: " + String.format("%.2f", semanticCharge));
        System.out.println("   Dimensions: 17");
    }
    
    /**
     * THE UPDATE LOOP
     * Applies forces in 17 dimensions, then projects to 3D.
     * 
     * @param dt Delta time (seconds)
     */
    public void update(double dt) {
        long now = System.currentTimeMillis();
        
        // 1. APPLY HYPER-FRICTION (Data Decay)
        // Information naturally degrades over time
        for (int i = 0; i < 17; i++) {
            hyperVelocity[i] *= FRICTION;
        }
        
        // 2. APPLY HYPER-ACCELERATION
        // F = ma → a = F/m (already calculated in applyDataForce)
        for (int i = 0; i < 17; i++) {
            hyperVelocity[i] += hyperAcceleration[i] * dt;
            hyperAcceleration[i] = 0; // Reset acceleration
        }
        
        // 3. MOVE IN HYPER-SPACE
        // Update position based on velocity
        for (int i = 0; i < 17; i++) {
            hyperVector[i] += hyperVelocity[i] * dt;
        }
        
        // 4. PROJECT TO 3D (Dimensional Collapse)
        // Use first 3 dimensions + phi-harmonic modulation
        this.x = hyperVector[0] * 10.0;
        this.y = hyperVector[1] * 10.0;
        this.z = hyperVector[2] * 10.0;
        
        // Add phi-harmonic resonance from higher dimensions
        for (int i = 3; i < 17; i++) {
            double phiMod = Math.pow(PHI, -(i - 2));
            this.x += hyperVector[i] * phiMod * Math.cos(i * PHI);
            this.y += hyperVector[i] * phiMod * Math.sin(i * PHI);
            this.z += hyperVector[i] * phiMod * Math.cos(i * PHI * 2);
        }
        
        // Calculate 3D velocity (for visualization)
        this.vx = hyperVelocity[0] * 10.0;
        this.vy = hyperVelocity[1] * 10.0;
        this.vz = hyperVelocity[2] * 10.0;
        
        // 5. MESH DEFORMATION
        // If moving fast in hyperspace, the mesh vibrates/deforms
        double stress = getHyperSpeed();
        if (stress > STRESS_THRESHOLD) {
            geometry.deform(stress, now);
        }
        
        // 6. TRACK MASS EVOLUTION
        // Mass can change as the body absorbs/emits information
        if (massHistory.size() < 1000) {
            massHistory.add(dataMass);
        }
        
        this.lastUpdate = now;
    }
    
    /**
     * APPLY DATA FORCE
     * You don't push it with hands. You push it with DATA.
     * 
     * @param intent The conceptual force to apply
     */
    public void applyDataForce(String intent) {
        // Generate 17D force vector from intent
        double[] forceVector = generateVector(intent);
        double forceMagnitude = calculateEntropy(intent);
        
        // Check for semantic contradiction (collision)
        double contradiction = detectContradiction(intent);
        if (contradiction > 0.5) {
            collisionLog.add(String.format(
                "⚠️ COLLISION: '%s' contradicts '%s' (%.2f%%)",
                intent, concept, contradiction * 100
            ));
            forceMagnitude *= (1.0 + contradiction); // Amplify contradictory forces
        }
        
        // F = ma → a = F/m
        for (int i = 0; i < 17; i++) {
            double force = forceVector[i] * forceMagnitude;
            double acceleration = force / this.dataMass;
            this.hyperAcceleration[i] += acceleration;
        }
        
        // Update mass (absorb information)
        this.dataMass += forceMagnitude * 0.01; // Small mass gain from impact
        
        // Log the impact
        collisionLog.add(String.format(
            "💥 IMPACT: '%s' [Force: %.4f, Mass: %.4f → %.4f]",
            intent, forceMagnitude, dataMass - (forceMagnitude * 0.01), dataMass
        ));
    }
    
    /**
     * APPLY GRAVITY (Contextual Resonance)
     * Bodies with similar semantic meaning attract each other
     * 
     * @param other Another hyper-rigid body
     */
    public void applyGravity(HyperRigidBody other) {
        // Calculate semantic distance in 17D space
        double distance = 0;
        for (int i = 0; i < 17; i++) {
            double diff = this.hyperVector[i] - other.hyperVector[i];
            distance += diff * diff;
        }
        distance = Math.sqrt(distance);
        
        if (distance < 0.001) return; // Avoid division by zero
        
        // Gravitational force: F = G * m1 * m2 / r^2
        // G is the phi-harmonic constant
        double G = PHI * 0.001;
        double force = G * this.dataMass * other.dataMass / (distance * distance);
        
        // Apply force in direction of other body
        for (int i = 0; i < 17; i++) {
            double direction = (other.hyperVector[i] - this.hyperVector[i]) / distance;
            this.hyperAcceleration[i] += direction * force / this.dataMass;
        }
    }
    
    /**
     * DETECT COLLISION with another body
     * 
     * @param other Another body
     * @return true if colliding
     */
    public boolean isCollidingWith(HyperRigidBody other) {
        // Calculate 17D distance
        double distance = 0;
        for (int i = 0; i < 17; i++) {
            double diff = this.hyperVector[i] - other.hyperVector[i];
            distance += diff * diff;
        }
        distance = Math.sqrt(distance);
        
        // Collision threshold based on combined mass
        double threshold = Math.sqrt(this.dataMass + other.dataMass) * 0.1;
        
        return distance < threshold;
    }
    
    /**
     * RESOLVE COLLISION
     * Elastic collision in 17D space
     * 
     * @param other The other body
     */
    public void resolveCollision(HyperRigidBody other) {
        // Log collision
        collisionLog.add(String.format(
            "🔥 COLLISION WITH: %s ('%s' ↔ '%s')",
            other.id, this.concept, other.concept
        ));
        
        // Calculate relative velocity
        double[] relativeVelocity = new double[17];
        for (int i = 0; i < 17; i++) {
            relativeVelocity[i] = this.hyperVelocity[i] - other.hyperVelocity[i];
        }
        
        // Calculate collision normal (direction)
        double[] normal = new double[17];
        double distance = 0;
        for (int i = 0; i < 17; i++) {
            normal[i] = other.hyperVector[i] - this.hyperVector[i];
            distance += normal[i] * normal[i];
        }
        distance = Math.sqrt(distance);
        
        if (distance < 0.001) return;
        
        // Normalize
        for (int i = 0; i < 17; i++) {
            normal[i] /= distance;
        }
        
        // Calculate impulse (elastic collision)
        double relativeSpeed = 0;
        for (int i = 0; i < 17; i++) {
            relativeSpeed += relativeVelocity[i] * normal[i];
        }
        
        if (relativeSpeed > 0) return; // Moving apart
        
        double restitution = 0.8; // Bounciness
        double impulse = -(1 + restitution) * relativeSpeed;
        impulse /= (1.0 / this.dataMass + 1.0 / other.dataMass);
        
        // Apply impulse
        for (int i = 0; i < 17; i++) {
            this.hyperVelocity[i] += impulse * normal[i] / this.dataMass;
            other.hyperVelocity[i] -= impulse * normal[i] / other.dataMass;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // MATH UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Seed hyper-state from concept string
     */
    private void seedHyperState(String s) {
        long seed = s.hashCode();
        java.util.Random r = new java.util.Random(seed);
        
        for (int i = 0; i < 17; i++) {
            // Use Gaussian distribution for natural clustering
            hyperVector[i] = r.nextGaussian();
        }
    }
    
    /**
     * Generate 17D vector from string
     */
    private double[] generateVector(String s) {
        double[] v = new double[17];
        long seed = s.hashCode();
        java.util.Random r = new java.util.Random(seed);
        
        for (int i = 0; i < 17; i++) {
            v[i] = r.nextDouble() - 0.5; // Direction: -0.5 to 0.5
        }
        
        // Normalize
        double magnitude = 0;
        for (double val : v) {
            magnitude += val * val;
        }
        magnitude = Math.sqrt(magnitude);
        
        if (magnitude > 0) {
            for (int i = 0; i < 17; i++) {
                v[i] /= magnitude;
            }
        }
        
        return v;
    }
    
    /**
     * Calculate Shannon entropy (information density)
     */
    private double calculateEntropy(String s) {
        if (s.isEmpty()) return 0.1;
        
        // Character frequency analysis
        int[] freq = new int[256];
        for (char c : s.toCharArray()) {
            if (c < 256) freq[c]++;
        }
        
        // Shannon entropy: H = -Σ(p * log2(p))
        double entropy = 0;
        int total = s.length();
        
        for (int count : freq) {
            if (count > 0) {
                double p = (double) count / total;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        
        // Scale to reasonable mass range
        return entropy * s.length() * 0.1;
    }
    
    /**
     * Calculate semantic charge (positive vs negative concept)
     */
    private double calculateSemanticCharge(String s) {
        s = s.toLowerCase();
        
        // Simple sentiment analysis
        int positive = 0;
        int negative = 0;
        
        String[] positiveWords = {"love", "peace", "harmony", "order", "light", "good", "pure"};
        String[] negativeWords = {"hate", "war", "chaos", "disorder", "dark", "evil", "corrupt"};
        
        for (String word : positiveWords) {
            if (s.contains(word)) positive++;
        }
        
        for (String word : negativeWords) {
            if (s.contains(word)) negative++;
        }
        
        // Return charge: -1 (negative) to +1 (positive)
        int total = positive + negative;
        if (total == 0) return 0;
        
        return (double) (positive - negative) / total;
    }
    
    /**
     * Detect semantic contradiction
     */
    private double detectContradiction(String intent) {
        // Compare semantic charges
        double intentCharge = calculateSemanticCharge(intent);
        double chargeDistance = Math.abs(this.semanticCharge - intentCharge);
        
        // Opposite charges = high contradiction
        if (this.semanticCharge * intentCharge < 0) {
            return chargeDistance;
        }
        
        return 0;
    }
    
    /**
     * Get hyper-speed (magnitude of 17D velocity)
     */
    private double getHyperSpeed() {
        double sum = 0;
        for (double v : hyperVelocity) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Get 3D speed
     */
    public double get3DSpeed() {
        return Math.sqrt(vx * vx + vy * vy + vz * vz);
    }
    
    /**
     * Get status report
     */
    public String getStatus() {
        return String.format("""
            ⚛️ HDRB STATUS [%s]
               Concept: %s
               Data Mass: %.4f
               Semantic Charge: %.2f
               Hyper-Speed: %.4f
               3D Position: [%.2f, %.2f, %.2f]
               3D Speed: %.4f
               Collisions: %d
               Age: %dms
            """,
            id, concept, dataMass, semanticCharge,
            getHyperSpeed(), x, y, z, get3DSpeed(),
            collisionLog.size(),
            System.currentTimeMillis() - birthTime);
    }
}
