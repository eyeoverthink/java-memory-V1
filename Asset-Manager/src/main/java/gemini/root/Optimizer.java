package gemini.root;

/**
 * THE TEACHER: Stochastic Gradient Descent
 * 
 * This is the algorithm that punishes the network for being wrong.
 * It looks at the error, calculates the slope (Gradient), 
 * and nudges the weights in the opposite direction.
 * 
 * W_new = W_old - (LearningRate * Gradient)
 */
public class Optimizer {
    private double learningRate;
    private double momentum;
    
    // Momentum storage (for SGD with momentum)
    private java.util.Map<String, Matrix> velocities = new java.util.HashMap<>();

    public Optimizer(double learningRate) {
        this.learningRate = learningRate;
        this.momentum = 0.0; // No momentum by default
    }

    public Optimizer(double learningRate, double momentum) {
        this.learningRate = learningRate;
        this.momentum = momentum;
    }

    /**
     * THE CORRECTION STEP (Simple SGD)
     * W_new = W_old - (LearningRate * Gradient)
     */
    public Matrix updateWeights(Matrix weights, Matrix gradient) {
        Matrix step = gradient.multiply(learningRate);
        return weights.subtract(step);
    }

    /**
     * SGD WITH MOMENTUM
     * v = momentum * v_prev + lr * gradient
     * W_new = W_old - v
     */
    public Matrix updateWeightsWithMomentum(String name, Matrix weights, Matrix gradient) {
        Matrix velocity = velocities.get(name);
        if (velocity == null) {
            velocity = new Matrix(gradient.rows, gradient.cols);
        }
        
        // v = momentum * v + lr * gradient
        Matrix momentumTerm = velocity.multiply(momentum);
        Matrix gradientTerm = gradient.multiply(learningRate);
        velocity = momentumTerm.add(gradientTerm);
        
        velocities.put(name, velocity);
        
        return weights.subtract(velocity);
    }

    /**
     * Clip gradients to prevent exploding gradients
     */
    public Matrix clipGradient(Matrix gradient, double maxNorm) {
        double norm = 0;
        for (int i = 0; i < gradient.rows; i++) {
            for (int j = 0; j < gradient.cols; j++) {
                norm += gradient.data[i][j] * gradient.data[i][j];
            }
        }
        norm = Math.sqrt(norm);
        
        if (norm > maxNorm) {
            return gradient.multiply(maxNorm / norm);
        }
        return gradient;
    }

    public double getLearningRate() { return learningRate; }
    public void setLearningRate(double lr) { this.learningRate = lr; }
}
