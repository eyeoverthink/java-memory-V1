package fraymus.brain.creative.dionysian;

import java.util.*;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * V. Recurrent-Depth Latent Looping (Scaling the Remix)
 * 
 * To process these massive conceptual blends without requiring thousands of GPUs,
 * Fraynix executes this inside a Recurrent-Depth Transformer (RDT) architecture.
 * 
 * Instead of passing the thought through 100 different layers, Fraynix loops the
 * semantic state h_t through the exact same continuous block up to 16 times in
 * the latent space before emitting a physical action.
 * 
 * To prevent the mathematical state from exploding or decaying during these
 * recursive loops, the update matrix A is constrained. In dynamical-systems terms,
 * the spectral radius (the maximum absolute eigenvalue) of the update matrix
 * must remain strictly less than 1:
 * 
 *     ρ(A) < 1
 * 
 * This physically guarantees that the recurrent loop is stable. The continuous
 * field will naturally damp errors and converge upon a brilliant, fixed-point epiphany.
 */
public class RecurrentDepthTransformer {
    
    private static final int DEFAULT_RECURRENT_STEPS = 16;
    private static final double SPECTRAL_RADIUS_LIMIT = 0.95;
    private static final double CONVERGENCE_THRESHOLD = 1e-6;
    
    private final int stateDimension;
    private final int recurrentSteps;
    private final double spectralRadiusLimit;
    private final double[][] updateMatrix;
    private final double spectralRadius;
    
    /**
     * Create an RDT with specified parameters
     */
    public RecurrentDepthTransformer(int stateDimension, int recurrentSteps, double spectralRadiusLimit) {
        this.stateDimension = stateDimension;
        this.recurrentSteps = recurrentSteps;
        this.spectralRadiusLimit = spectralRadiusLimit;
        double[][] candidate = initializeUpdateMatrix(stateDimension, spectralRadiusLimit);
        double measuredRadius = computeSpectralRadius(candidate);
        if (measuredRadius >= spectralRadiusLimit) {
            candidate = fallbackStableMatrix(stateDimension, spectralRadiusLimit);
            measuredRadius = computeSpectralRadius(candidate);
        }
        this.updateMatrix = candidate;
        this.spectralRadius = Math.min(measuredRadius, Math.nextDown(spectralRadiusLimit));
    }

    public RecurrentDepthTransformer(int stateDimension, int recurrentSteps) {
        this(stateDimension, recurrentSteps, SPECTRAL_RADIUS_LIMIT);
    }
    
    public RecurrentDepthTransformer(int stateDimension) {
        this(stateDimension, DEFAULT_RECURRENT_STEPS, SPECTRAL_RADIUS_LIMIT);
    }
    
    /**
     * Initialize update matrix with constrained spectral radius
     * Uses random initialization followed by spectral normalization
     */
    private double[][] initializeUpdateMatrix(int dim, double targetRadius) {
        Random random = new Random();
        double[][] matrix = new double[dim][dim];
        
        // Random initialization
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                matrix[i][j] = random.nextGaussian() * 0.1;
            }
        }

        // Spectral normalization with safety margin and hard clamp retries.
        double safetyTarget = Math.max(0.05, targetRadius * 0.82);
        double[][] normalized = spectralNormalize(matrix, safetyTarget);
        double measured = computeSpectralRadius(normalized);
        int guard = 0;
        while (measured >= targetRadius && guard < 8) {
            double scale = (targetRadius * 0.9) / Math.max(measured, 1e-9);
            normalized = scaleMatrix(normalized, scale);
            measured = computeSpectralRadius(normalized);
            guard++;
        }

        if (measured >= targetRadius) {
            // Guaranteed-stable fallback.
            double fallbackRadius = Math.max(0.01, targetRadius * 0.5);
            normalized = new double[dim][dim];
            for (int i = 0; i < dim; i++) {
                normalized[i][i] = fallbackRadius;
            }
        }

        return normalized;
    }

    private double[][] fallbackStableMatrix(int dim, double targetRadius) {
        double[][] matrix = new double[dim][dim];
        double stableRadius = Math.max(0.01, targetRadius * 0.35);
        for (int i = 0; i < dim; i++) {
            matrix[i][i] = stableRadius;
        }
        return matrix;
    }

    private double[][] scaleMatrix(double[][] matrix, double scale) {
        double[][] scaled = new double[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                scaled[i][j] = matrix[i][j] * scale;
            }
        }
        return scaled;
    }
    
    /**
     * Normalize a matrix to have a specific spectral radius
     */
    private double[][] spectralNormalize(double[][] matrix, double targetRadius) {
        double currentRadius = computeSpectralRadius(matrix);
        if (currentRadius == 0) {
            return matrix;
        }
        
        double scale = targetRadius / currentRadius;
        double[][] normalized = new double[matrix.length][matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                normalized[i][j] = matrix[i][j] * scale;
            }
        }
        
        return normalized;
    }
    
    /**
     * Compute spectral radius (maximum absolute eigenvalue) using power iteration
     */
    private double computeSpectralRadius(double[][] matrix) {
        int dim = matrix.length;
        double[] vector = new double[dim];
        
        // Initialize with random vector
        Random random = new Random();
        for (int i = 0; i < dim; i++) {
            vector[i] = random.nextDouble();
        }
        
        // Normalize
        double norm = 0.0;
        for (double v : vector) norm += v * v;
        norm = Math.sqrt(norm);
        for (int i = 0; i < dim; i++) vector[i] /= norm;
        
        // Power iteration
        for (int iter = 0; iter < 100; iter++) {
            double[] newVector = matrixVectorMultiply(matrix, vector);
            
            // Rayleigh quotient
            double eigenvalue = 0.0;
            for (int i = 0; i < dim; i++) {
                eigenvalue += vector[i] * newVector[i];
            }
            
            // Normalize
            norm = 0.0;
            for (double v : newVector) norm += v * v;
            norm = Math.sqrt(norm);
            
            if (norm > 0) {
                for (int i = 0; i < dim; i++) {
                    vector[i] = newVector[i] / norm;
                }
            }
        }
        
        // Final eigenvalue estimate
        double[] newVector = matrixVectorMultiply(matrix, vector);
        double eigenvalue = 0.0;
        for (int i = 0; i < dim; i++) {
            eigenvalue += vector[i] * newVector[i];
        }
        
        return Math.abs(eigenvalue);
    }
    
    private double[] matrixVectorMultiply(double[][] matrix, double[] vector) {
        int dim = matrix.length;
        double[] result = new double[dim];
        
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        
        return result;
    }
    
    /**
     * Process a semantic state through recurrent-depth latent looping
     */
    public RecurrentResult process(double[] initialState, double[] input) {
        if (initialState.length != stateDimension) {
            throw new IllegalArgumentException("State dimension mismatch");
        }
        if (input != null && input.length != stateDimension) {
            throw new IllegalArgumentException("Input dimension mismatch");
        }
        
        double[] currentState = new double[stateDimension];
        System.arraycopy(initialState, 0, currentState, 0, stateDimension);
        
        List<double[]> stateHistory = new ArrayList<>();
        stateHistory.add(currentState.clone());
        
        boolean converged = false;
        int convergenceStep = -1;
        
        // Recurrent-depth looping
        for (int step = 0; step < recurrentSteps; step++) {
            // Apply update matrix: h_{t+1} = A * h_t + input
            double[] newState = matrixVectorMultiply(updateMatrix, currentState);
            
            if (input != null) {
                for (int i = 0; i < stateDimension; i++) {
                    newState[i] += input[i];
                }
            }
            
            // Apply nonlinearity (tanh for bounded output)
            for (int i = 0; i < stateDimension; i++) {
                newState[i] = Math.tanh(newState[i]);
            }
            
            // Check convergence
            double change = computeChange(currentState, newState);
            if (change < CONVERGENCE_THRESHOLD && !converged) {
                converged = true;
                convergenceStep = step;
            }
            
            currentState = newState;
            stateHistory.add(currentState.clone());
        }
        
        return new RecurrentResult(currentState, stateHistory, converged, convergenceStep);
    }
    
    private double computeChange(double[] oldState, double[] newState) {
        double sum = 0.0;
        for (int i = 0; i < oldState.length; i++) {
            sum += Math.abs(newState[i] - oldState[i]);
        }
        return sum / oldState.length;
    }
    
    /**
     * Result of recurrent-depth processing
     */
    public static class RecurrentResult {
        private final double[] finalState;
        private final List<double[]> stateHistory;
        private final boolean converged;
        private final int convergenceStep;
        
        public RecurrentResult(double[] finalState, List<double[]> stateHistory, 
                             boolean converged, int convergenceStep) {
            this.finalState = finalState.clone();
            this.stateHistory = new ArrayList<>(stateHistory);
            this.converged = converged;
            this.convergenceStep = convergenceStep;
        }
        
        public double[] getFinalState() {
            return finalState.clone();
        }
        
        public List<double[]> getStateHistory() {
            return new ArrayList<>(stateHistory);
        }
        
        public boolean converged() {
            return converged;
        }
        
        public int getConvergenceStep() {
            return convergenceStep;
        }
        
        public int getStepsTaken() {
            return stateHistory.size() - 1;
        }
    }
    
    /**
     * Compute the stability margin (how far spectral radius is from 1)
     */
    public double getStabilityMargin() {
        return 1.0 - spectralRadius;
    }
    
    /**
     * Get the spectral radius of the update matrix
     */
    public double getSpectralRadius() {
        return spectralRadius;
    }
    
    /**
     * Get the update matrix (read-only)
     */
    public double[][] getUpdateMatrix() {
        double[][] copy = new double[stateDimension][stateDimension];
        for (int i = 0; i < stateDimension; i++) {
            System.arraycopy(updateMatrix[i], 0, copy[i], 0, stateDimension);
        }
        return copy;
    }
    
    /**
     * Create a state vector from a feature map
     */
    public static double[] stateFromFeatures(Map<String, Double> features, int dimension) {
        double[] state = new double[dimension];
        
        List<String> keys = new ArrayList<>(features.keySet());
        for (int i = 0; i < Math.min(keys.size(), dimension); i++) {
            state[i] = features.get(keys.get(i));
        }
        
        // Normalize
        double norm = 0.0;
        for (double v : state) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dimension; i++) {
                state[i] /= norm;
            }
        }
        
        return state;
    }
    
    /**
     * Create a feature map from a state vector
     */
    public static Map<String, Double> featuresFromState(double[] state, List<String> featureNames) {
        Map<String, Double> features = new HashMap<>();
        
        for (int i = 0; i < Math.min(state.length, featureNames.size()); i++) {
            features.put(featureNames.get(i), state[i]);
        }
        
        return features;
    }
    
    /**
     * Batch process multiple states
     */
    public List<RecurrentResult> processBatch(List<double[]> initialStates, double[] input) {
        List<RecurrentResult> results = new ArrayList<>();
        
        for (double[] state : initialStates) {
            results.add(process(state, input));
        }
        
        return results;
    }
}
