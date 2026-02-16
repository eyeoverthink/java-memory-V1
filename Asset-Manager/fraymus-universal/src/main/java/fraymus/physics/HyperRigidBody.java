package fraymus.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ⚛️ HYPER-RIGID BODY (HDRB)
 * "A physical object that exists in Data Space."
 *
 * ATTRIBUTES:
 * 1. 3D Position (x,y,z) -> For your screen.
 * 2. 17D Position (d1..d17) -> For the logic.
 * 3. Data Mass -> Calculated from information entropy.
 * 4. Mesh -> Deformable geometry.
 *
 * Mass = Information Density.
 * Velocity = Rate of Semantic Change.
 * Gravity = Contextual Resonance.
 * Collision = Logic Contradiction.
 * Mesh = The 3D projection of the 17D Data State.
 */
public class HyperRigidBody {

    public final String id;
    public final String concept;
    
    // PHYSICAL STATE (The Projection)
    public double x, y, z;
    public double vx, vy, vz; // Velocity 3D
    
    // DATA STATE (The Reality)
    public double[] hyperVector = new double[17];
    public double[] hyperVelocity = new double[17];
    public double dataMass; // Heavier = More Knowledge
    
    // VISUALS
    public Mesh geometry; 
    
    // TRACKING (The Black Box)
    public List<String> collisionLog = new ArrayList<>();
    private long totalTicks = 0;

    public HyperRigidBody(String concept, Mesh mesh) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.concept = concept;
        this.geometry = mesh;
        
        // Seed the 17D Vector based on the concept string (hashing)
        seedHyperState(concept);
        
        // Calculate Mass based on complexity
        this.dataMass = calculateEntropy(concept);
    }

    /**
     * THE UPDATE LOOP
     * Applies Forces in 17 Dimensions, then projects to 3D.
     */
    public void update(double dt) {
        totalTicks++;

        // 1. APPLY HYPER-FRICTION (Data Decay)
        for(int i=0; i<17; i++) hyperVelocity[i] *= 0.99;

        // 2. MOVE IN HYPER-SPACE
        for(int i=0; i<17; i++) hyperVector[i] += hyperVelocity[i] * dt;

        // 3. PROJECT TO 3D (Dimensional Collapse)
        // We use the first 3 dimensions + Phi Resonance to determine physical location
        this.x = hyperVector[0] * 10.0;
        this.y = hyperVector[1] * 10.0;
        this.z = hyperVector[2] * 10.0;
        
        // 4. MESH DEFORMATION
        // If moving fast in HyperSpace, the mesh vibrates/deforms
        double stress = getHyperSpeed();
        if (stress > 0.1) {
            geometry.deform(stress, System.currentTimeMillis());
        }
    }

    /**
     * APPLY FORCE
     * You don't push it with hands. You push it with DATA.
     */
    public void applyDataForce(String intent) {
        double[] forceVec = generateVector(intent);
        double forceMagnitude = calculateEntropy(intent);

        // F = ma -> a = F/m
        for(int i=0; i<17; i++) {
            double acceleration = (forceVec[i] * forceMagnitude) / this.dataMass;
            this.hyperVelocity[i] += acceleration;
        }
        
        this.collisionLog.add("IMPACT: " + intent + " [Force: " + 
                String.format("%.2f", forceMagnitude) + ", Speed: " + 
                String.format("%.4f", getHyperSpeed()) + "]");
    }

    /**
     * COLLIDE two bodies — logic contradiction detection.
     * If their hyperVectors oppose, the collision is violent.
     */
    public double collide(HyperRigidBody other) {
        double dot = 0;
        for (int i = 0; i < 17; i++) {
            dot += this.hyperVector[i] * other.hyperVector[i];
        }
        // Negative dot = contradiction = violent collision
        double violence = -dot;
        if (violence > 0) {
            // Transfer force proportional to contradiction
            for (int i = 0; i < 17; i++) {
                double transfer = (other.hyperVelocity[i] * other.dataMass) / this.dataMass;
                this.hyperVelocity[i] += transfer * 0.5;
                other.hyperVelocity[i] -= transfer * 0.5;
            }
            this.collisionLog.add("COLLISION with " + other.id + " [Violence: " + 
                    String.format("%.4f", violence) + "]");
            other.collisionLog.add("COLLISION with " + this.id + " [Violence: " + 
                    String.format("%.4f", violence) + "]");
        }
        return violence;
    }

    // --- DIAGNOSTICS ---

    public double getHyperSpeed() {
        double sum = 0;
        for(double v : hyperVelocity) sum += v*v;
        return Math.sqrt(sum);
    }

    public long getTotalTicks() { return totalTicks; }

    public String getStatus() {
        return String.format(
            "HDRB [%s] \"%s\"\n" +
            "   Mass: %.2f | Speed: %.4f | Ticks: %d\n" +
            "   3D Projection: [%.2f, %.2f, %.2f]\n" +
            "   Mesh Deformation: %.4f\n" +
            "   Impacts: %d",
            id, concept, dataMass, getHyperSpeed(), totalTicks,
            x, y, z, geometry.getDeformation(), collisionLog.size()
        );
    }

    // --- MATH UTILS ---

    private void seedHyperState(String s) {
        long seed = s.hashCode();
        java.util.Random r = new java.util.Random(seed);
        for(int i=0; i<17; i++) hyperVector[i] = r.nextGaussian();
    }

    private double[] generateVector(String s) {
        double[] v = new double[17];
        long seed = s.hashCode();
        java.util.Random r = new java.util.Random(seed);
        for(int i=0; i<17; i++) v[i] = r.nextDouble() - 0.5; // Direction -0.5 to 0.5
        return v;
    }

    private double calculateEntropy(String s) {
        // Shannon Entropy approximation for "Mass"
        double[] freq = new double[256];
        for (char c : s.toCharArray()) freq[c & 0xFF]++;
        double entropy = 0;
        for (double f : freq) {
            if (f > 0) {
                double p = f / s.length();
                entropy -= p * Math.log(p) / Math.log(2);
            }
        }
        // Scale: longer + more entropic = heavier
        return (entropy + 1.0) * s.length() * 0.1;
    }
}
