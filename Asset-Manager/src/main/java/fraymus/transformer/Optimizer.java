package fraymus.transformer;

/**
 * Stochastic Gradient Descent Optimizer
 * 
 * The "Teacher" that punishes the network for being wrong.
 * Looks at the error, calculates the slope (Gradient), 
 * and nudges the weights in the opposite direction.
 * 
 * This is the algorithm that separates "Code" from "AI."
 * Code follows rules. AI rewrites its own rules based on mistakes.
 */
public class Optimizer {
    private double learningRate;
    private int updateCount = 0;

    public Optimizer(double learningRate) {
        this.learningRate = learningRate; // e.g., 0.01 (How fast we learn)
    }

    /**
     * The Correction Step
     * W_new = W_old - (LearningRate * Gradient)
     * 
     * This is where the brain physically changes to learn a new concept.
     */
    public Matrix updateWeights(Matrix weights, Matrix gradient) {
        Matrix step = gradient.scale(learningRate);
        Matrix updated = weights.subtract(step);
        updateCount++;
        return updated;
    }

    /**
     * Update with momentum (advanced optimization)
     */
    public Matrix updateWithMomentum(Matrix weights, Matrix gradient, Matrix velocity, double momentum) {
        // velocity = momentum * velocity - learningRate * gradient
        Matrix momentumTerm = velocity.scale(momentum);
        Matrix gradientTerm = gradient.scale(learningRate);
        Matrix newVelocity = momentumTerm.subtract(gradientTerm);
        
        // weights = weights + velocity
        Matrix updated = weights.add(newVelocity);
        updateCount++;
        return updated;
    }

    /**
     * Get learning rate
     */
    public double getLearningRate() {
        return learningRate;
    }

    /**
     * Set learning rate (for learning rate decay)
     */
    public void setLearningRate(double lr) {
        this.learningRate = lr;
    }

    /**
     * Decay learning rate (common technique to stabilize training)
     */
    public void decayLearningRate(double factor) {
        this.learningRate *= factor;
    }

    /**
     * Get update count
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * Print optimizer status
     */
    public void printStatus() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   OPTIMIZER STATUS                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Learning Rate: " + learningRate);
        System.out.println("  Updates Applied: " + updateCount);
    }
}
