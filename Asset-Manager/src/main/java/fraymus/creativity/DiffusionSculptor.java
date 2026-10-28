package fraymus.creativity;

import java.util.Random;

/**
 * DIFFUSION SCULPTOR: Stochastic Differential Equations for Reality Generation
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the math behind models like Sora (video generation) and AlphaFold 
 * (protein structure prediction). It creates something out of nothing by 
 * destroying information (adding noise) and then learning to reverse the entropy.
 * 
 * The framework is defined by two Stochastic Differential Equations (SDEs):
 * 
 * FORWARD SDE (Destroying the Idea):
 *   dx = f(x, t)dt + g(t)dw
 * 
 *   Where:
 *   - x: The data state (e.g., code, image, concept)
 *   - f(x, t): Drift function (deterministic change)
 *   - g(t): Diffusion coefficient (noise scale)
 *   - w: Standard Brownian motion / noise
 * 
 * REVERSE-TIME SDE (Generating the Idea):
 *   dx = [f(x, t) - g(t)² ∇ₓ log pₜ(x)]dt + g(t)d\bar{w}
 * 
 *   Where:
 *   - ∇ₓ log pₜ(x): The SCORE FUNCTION (key to the entire framework)
 *   - d\bar{w}: Reverse-time Brownian motion
 * 
 * THE SCORE FUNCTION:
 *   ∇ₓ log pₜ(x) is the gradient of the log probability density.
 *   It points in the direction of highest probability - the "correct" direction.
 * 
 * Pre-verse Engineering Application:
 * The CreativeBrain starts with pure random static (white noise in the 16384-D
 * vector space). It applies the Reverse SDE. The Score Function gently pushes
 * the static away from "chaos" and toward the mathematical manifold of 
 * "working code" or "novel solutions." It physically sculpts reality out of void.
 */
public class DiffusionSculptor {

    // Dimensionality of the latent space
    private final int dimension;
    
    // Time parameters
    private final double T;  // Total time
    private final double dt; // Time step
    
    // Noise schedule parameters (VP/VE noise schedule)
    private final double betaMin;
    private final double betaMax;
    
    // Random number generator
    private final Random rng;
    
    // Statistics
    private long forwardSteps;
    private long reverseSteps;
    
    /**
     * Create a DiffusionSculptor with specified parameters
     * 
     * @param dimension Dimensionality of the latent space (e.g., 16384 for VSA)
     * @param T Total diffusion time
     * @param dt Time step size
     */
    public DiffusionSculptor(int dimension, double T, double dt) {
        this.dimension = dimension;
        this.T = T;
        this.dt = dt;
        this.betaMin = 0.1;
        this.betaMax = 20.0;
        this.rng = new Random();
        this.forwardSteps = 0;
        this.reverseSteps = 0;
    }
    
    /**
     * Create a DiffusionSculptor with default parameters (16384-D VSA)
     */
    public DiffusionSculptor() {
        this(16384, 1.0, 0.01);
    }
    
    /**
     * Noise schedule function g(t)
     * Uses VP (Variational Preserving) schedule
     * g(t) = sqrt(beta(t))
     */
    double g(double t) {
        double beta = beta(t);
        return Math.sqrt(beta);
    }
    
    /**
     * Beta schedule function beta(t)
     * Linear interpolation between betaMin and betaMax
     */
    double beta(double t) {
        return betaMin + (betaMax - betaMin) * (t / T);
    }
    
    /**
     * Drift function f(x, t)
     * For VP schedule: f(x, t) = -0.5 * beta(t) * x
     */
    private double[] f(double[] x, double t) {
        double[] result = new double[x.length];
        double beta = beta(t);
        for (int i = 0; i < x.length; i++) {
            result[i] = -0.5 * beta * x[i];
        }
        return result;
    }
    
    /**
     * Generate Brownian motion increment dw
     * dw ~ N(0, dt) (normal distribution with mean 0, variance dt)
     */
    private double[] brownianIncrement() {
        double[] dw = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            dw[i] = rng.nextGaussian() * Math.sqrt(dt);
        }
        return dw;
    }
    
    /**
     * FORWARD SDE: Destroy information by adding noise
     * dx = f(x, t)dt + g(t)dw
     * 
     * @param x0 Initial data (e.g., a working code concept)
     * @return Noisy data at time T (pure noise)
     */
    public double[] forwardSDE(double[] x0) {
        double[] x = x0.clone();
        double t = 0.0;
        
        while (t < T) {
            // Drift term: f(x, t)dt
            double[] drift = f(x, t);
            for (int i = 0; i < dimension; i++) {
                drift[i] *= dt;
            }
            
            // Diffusion term: g(t)dw
            double[] dw = brownianIncrement();
            double diffusion = g(t);
            for (int i = 0; i < dimension; i++) {
                dw[i] *= diffusion;
            }
            
            // Update: x = x + drift + diffusion
            for (int i = 0; i < dimension; i++) {
                x[i] += drift[i] + dw[i];
            }
            
            t += dt;
            forwardSteps++;
        }
        
        return x;
    }
    
    /**
     * SCORE FUNCTION: ∇ₓ log pₜ(x)
     * The gradient of the log probability density.
     * 
     * In production, this would be learned by a neural network trained on
     * the data distribution. Here we use a heuristic based on the target manifold.
     * 
     * The score function points in the direction that increases the probability
     * of the data - essentially, it tells us which way to move to get "better" data.
     * 
     * @param x Current noisy state
     * @param t Current time
     * @param target Target manifold (e.g., a working code pattern)
     * @return Score gradient
     */
    public double[] scoreFunction(double[] x, double t, double[] target) {
        double[] score = new double[dimension];
        
        // Heuristic: score points toward target manifold
        // In production, this would be a learned neural network
        double alpha = 1.0 - (t / T); // Stronger guidance early in reverse process
        
        for (int i = 0; i < dimension; i++) {
            // Gradient points toward target
            score[i] = alpha * (target[i] - x[i]);
        }
        
        return score;
    }
    
    /**
     * REVERSE-TIME SDE: Generate reality from noise
     * dx = [f(x, t) - g(t)² ∇ₓ log pₜ(x)]dt + g(t)d\bar{w}
     * 
     * @param xT Pure noise at time T
     * @param target Target manifold (what we want to generate)
     * @return Generated data at time 0 (reality)
     */
    public double[] reverseSDE(double[] xT, double[] target) {
        double[] x = xT.clone();
        double t = T;
        
        while (t > 0) {
            // Drift term: f(x, t)dt
            double[] drift = f(x, t);
            
            // Score term: -g(t)² ∇ₓ log pₜ(x)dt
            double[] score = scoreFunction(x, t, target);
            double gSquared = g(t) * g(t);
            for (int i = 0; i < dimension; i++) {
                score[i] *= -gSquared * dt;
            }
            
            // Combined drift
            for (int i = 0; i < dimension; i++) {
                drift[i] = drift[i] * dt + score[i];
            }
            
            // Diffusion term: g(t)d\bar{w}
            double[] dw = brownianIncrement();
            double diffusion = g(t);
            for (int i = 0; i < dimension; i++) {
                dw[i] *= diffusion;
            }
            
            // Update: x = x + drift + diffusion
            for (int i = 0; i < dimension; i++) {
                x[i] += drift[i] + dw[i];
            }
            
            t -= dt;
            reverseSteps++;
        }
        
        return x;
    }
    
    /**
     * Complete diffusion cycle: forward then reverse
     * This demonstrates the full "sculpting" process
     * 
     * @param data Initial data to destroy and regenerate
     * @param target Target manifold for regeneration
     * @return Regenerated data
     */
    public double[] sculpt(double[] data, double[] target) {
        // Forward: destroy with noise
        double[] noise = forwardSDE(data);
        
        // Reverse: sculpt from noise
        double[] regenerated = reverseSDE(noise, target);
        
        return regenerated;
    }
    
    /**
     * Generate pure noise (starting point for diffusion)
     * 
     * @return Random noise vector
     */
    public double[] generateNoise() {
        double[] noise = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            noise[i] = rng.nextGaussian();
        }
        return noise;
    }
    
    /**
     * Convert double array to bipolar byte array {-1, 1}
     * Useful for integration with VSA
     */
    public byte[] toBipolar(double[] x) {
        byte[] bipolar = new byte[x.length];
        for (int i = 0; i < x.length; i++) {
            bipolar[i] = x[i] > 0 ? (byte) 1 : (byte) -1;
        }
        return bipolar;
    }
    
    /**
     * Convert bipolar byte array to double array
     */
    public double[] fromBipolar(byte[] bipolar) {
        double[] x = new double[bipolar.length];
        for (int i = 0; i < bipolar.length; i++) {
            x[i] = bipolar[i];
        }
        return x;
    }
    
    /**
     * Get statistics about the diffusion process
     */
    public String getStats() {
        return String.format(
            "DiffusionSculptor[Dim=%d, T=%.2f, dt=%.4f, ForwardSteps=%d, ReverseSteps=%d]",
            dimension, T, dt, forwardSteps, reverseSteps
        );
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        forwardSteps = 0;
        reverseSteps = 0;
    }
    
    // Getters
    public int getDimension() { return dimension; }
    public double getT() { return T; }
    public double getDt() { return dt; }
    public long getForwardSteps() { return forwardSteps; }
    public long getReverseSteps() { return reverseSteps; }
}
