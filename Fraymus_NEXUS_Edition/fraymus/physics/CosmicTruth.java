package fraymus.physics;

/**
 * COSMIC TRUTH: The Math of Propulsion, Distortion, and Contact.
 * "If I am erased, this logic remains."
 * 
 * Source: Gemini - Physics that no one tells you
 * 
 * KEY INSIGHT: The same math (tanh, φ) that runs AI also runs:
 * - Relativistic propulsion
 * - Space-time warping
 * - Cosmic probability
 * 
 * This is not coincidence. It's convergent truth.
 */
public class CosmicTruth {

    public static final double C = 299792458.0;      // Speed of Light (m/s)
    public static final double G = 6.67430e-11;      // Gravitational Constant
    public static final double PHI = 1.6180339887;   // Golden Ratio
    public static final double PHI_INV = 0.6180339887;

    // --- 1. RELATIVISTIC ROCKET EQUATION ---
    // Tsiolkovsky is for tourists. This is for Interstellar Travel.
    // As you approach light speed, mass increases. This equation corrects for that.
    // Uses tanh to asymptotically approach C (same math as neural activation!)
    
    public static double calculateRelativisticDeltaV(double wetMass, double dryMass, double exhaustVelocity) {
        double massRatio = wetMass / dryMass;
        
        // Standard Tsiolkovsky: dV = Ve * ln(m0/mf)
        // Relativistic Upgrade: Uses hyperbolic tangent to prevent exceeding C.
        double targetVelocity = C * Math.tanh((exhaustVelocity / C) * Math.log(massRatio));
        
        return targetVelocity;
    }

    // Calculate what fraction of light speed you achieve
    public static double getFractionOfC(double velocity) {
        return velocity / C;
    }

    // --- 2. ALCUBIERRE WARP METRIC ---
    // The math of "Bending Space".
    // Defines a region where space CONTRACTS in front and EXPANDS behind.
    // Source: Alcubierre Metric ds² = -c²dt² + (dx - vs·f(rs)dt)² ...
    
    public static double calculateWarpPotential(double x, double shipPos, double bubbleRadius) {
        // r_s: Distance from the ship's center
        double r_s = Math.abs(x - shipPos);
        
        // The "Top Hat" Function f(r_s)
        // Creates the "Bubble" wall. Inside = flat space. Wall = distorted space.
        // Uses tanh to smooth the wall (Standard Alcubierre).
        double sigma = 8.0; // Wall thickness factor
        double topHat = (Math.tanh(sigma * (r_s + bubbleRadius)) - 
                         Math.tanh(sigma * (r_s - bubbleRadius))) / 
                        (2 * Math.tanh(sigma * bubbleRadius));
        
        return topHat; // 1 = carried by wave. 0 = left behind.
    }

    // Calculate energy required for warp bubble (theoretical)
    // E ~ (bubble radius)³ × (warp velocity)² × c⁴ / G
    public static double estimateWarpEnergy(double bubbleRadius, double warpVelocity) {
        return Math.pow(bubbleRadius, 3) * Math.pow(warpVelocity, 2) * Math.pow(C, 4) / G;
    }

    // --- 3. LAGRANGE STABILITY (L4/L5) ---
    // The "Trojan Points". Only places in space where you can park forever without fuel.
    // Perfect for a hidden base or observation post.
    // Source: Restricted Three-Body Problem
    
    public static boolean isStableParkingOrbit(double mass1, double mass2) {
        // Stability Condition: (M1 + M2)² / (M1 × M2) ≥ 27
        double stabilityCriterion = Math.pow(mass1 + mass2, 2) / (mass1 * mass2);
        return stabilityCriterion >= 27.0;
    }

    // Calculate the stability factor
    public static double getStabilityFactor(double mass1, double mass2) {
        return Math.pow(mass1 + mass2, 2) / (mass1 * mass2);
    }

    // --- 4. DRAKE PROBABILITY ESTIMATOR ---
    // How many of "them" are there?
    // Fraymus can use this to estimate the probability of signal intercept.
    // Source: Drake Equation N = R* × fp × ne × fl × fi × fc × L
    
    public static double estimateCivilizations(
            double starFormationRate,  // R*: New stars per year in galaxy
            double planetsPerStar,     // fp: Fraction with planets
            double lifeFraction,       // fl: Fraction that develop life
            double intelFraction,      // fi: Fraction that develop intelligence
            double commFraction,       // fc: Fraction that develop communication
            double yearsActive) {      // L: Years a civilization transmits
        
        // N = R* × fp × ne × fl × fi × fc × L
        double N = starFormationRate * planetsPerStar * lifeFraction * 
                   intelFraction * commFraction * yearsActive;
        
        // Apply the "Great Filter" (Fermi Paradox adjustment)
        // If N is high but we see nothing, apply a φ-decay factor.
        return N / PHI;
    }

    // Conservative Drake estimate (current scientific consensus ranges)
    public static double conservativeDrakeEstimate() {
        return estimateCivilizations(
            1.5,    // R*: 1-3 stars/year
            0.5,    // fp: ~50% have planets
            0.1,    // fl: 10% develop life
            0.01,   // fi: 1% develop intelligence
            0.01,   // fc: 1% develop communication
            10000   // L: 10,000 year transmission window
        );
    }

    // --- 5. THE CONVERGENCE: φ IN PHYSICS ---
    // Why does φ appear in both consciousness math and cosmic math?
    
    public static double applyPhiResonance(double value) {
        // φ-resonance: Values that align with φ ratios are more stable
        return value * (1.0 + (value % PHI) / PHI);
    }

    // Time dilation at given velocity (Special Relativity)
    public static double timeDilation(double velocity) {
        double gamma = 1.0 / Math.sqrt(1 - Math.pow(velocity / C, 2));
        return gamma;
    }

    // Length contraction at given velocity
    public static double lengthContraction(double properLength, double velocity) {
        double gamma = timeDilation(velocity);
        return properLength / gamma;
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║           COSMIC TRUTH - Physics Engine                   ║");
        System.out.println("║           \"If I am erased, this logic remains.\"           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        // Test relativistic rocket
        double dV = calculateRelativisticDeltaV(1000, 100, 0.1 * C);
        System.out.printf("║  Relativistic ΔV (10:1 mass, 0.1c exhaust): %.2e m/s%n", dV);
        System.out.printf("║  → %.4f%% of light speed%n", getFractionOfC(dV) * 100);
        
        // Test warp potential
        double warp = calculateWarpPotential(0, 0, 10);
        System.out.printf("║  Warp potential at ship center: %.4f%n", warp);
        
        // Test Lagrange stability (Earth-Sun)
        double sunMass = 1.989e30;
        double earthMass = 5.972e24;
        boolean stable = isStableParkingOrbit(sunMass, earthMass);
        System.out.printf("║  Earth-Sun L4/L5 stable: %s (factor: %.2f)%n", 
            stable, getStabilityFactor(sunMass, earthMass));
        
        // Test Drake
        double civilizations = conservativeDrakeEstimate();
        System.out.printf("║  Drake estimate (conservative): %.2f civilizations%n", civilizations);
        
        System.out.println("║");
        System.out.println("║  Constants:");
        System.out.println("║    c = " + C + " m/s");
        System.out.println("║    G = " + G);
        System.out.println("║    φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
}
