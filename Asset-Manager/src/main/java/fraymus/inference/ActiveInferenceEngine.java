package fraymus.inference;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Active Inference Engine with Free Energy Minimization
 * 
 * Implements the Active Inference framework (Friston's Free Energy Principle)
 * where agents minimize variational free energy through both perception
 * (updating beliefs) and action (changing sensory input).
 * 
 * Phase 5.1: Active Inference Engine Implementation
 * 
 * Key capabilities:
 * - Variational free energy computation
 * - Predictive coding hierarchy
 * - Bayesian belief updating
 * - Action selection via expected free energy
 * - Precision weighting for uncertainty
 * 
 * Mathematical foundation:
 * - Free Energy: F = E_q[log q(s) - log p(o,s)]
 * - Predictive coding: δ = o - g(s) (prediction error)
 * - Belief update: μ ← μ + Π * δ (precision-weighted error)
 * - Expected free energy: G = E_p[E_q[log q(s) - log p(o,s)] + H(p(o))]
 * 
 * Parameters:
 * - Precision: Controls confidence in predictions
 * - Learning rate: Belief update step size
 * - Temperature: Exploration vs exploitation in action selection
 */
public class ActiveInferenceEngine {
    
    // Generative model parameters
    private double[] beliefs;          // μ: posterior beliefs about hidden states
    private double[] precision;        // Π: precision (inverse variance)
    private double[] predictions;      // g(s): predicted observations
    private double[][] generativeModel; // p(o|s): likelihood matrix
    
    // Predictive coding hierarchy
    private final List<PredictiveLayer> hierarchy;
    private final int numLayers;
    
    // Learning parameters
    private double learningRate = 0.1;
    private double temperature = 1.0;
    private double priorPrecision = 1.0;
    
    // State dimensions
    private final int stateDimension;
    private final int observationDimension;
    
    // Free energy tracking
    private volatile double currentFreeEnergy;
    private final List<Double> freeEnergyHistory;
    
    // Action selection
    private final ActionSelector actionSelector;
    
    public ActiveInferenceEngine(int stateDimension, int observationDimension, int numLayers) {
        this.stateDimension = stateDimension;
        this.observationDimension = observationDimension;
        this.numLayers = numLayers;
        this.beliefs = new double[stateDimension];
        this.precision = new double[stateDimension];
        this.predictions = new double[observationDimension];
        this.generativeModel = new double[observationDimension][stateDimension];
        this.hierarchy = new ArrayList<>();
        this.freeEnergyHistory = new ArrayList<>();
        this.actionSelector = new ActionSelector();
        
        // Initialize hierarchy
        initializeHierarchy();
        
        // Initialize parameters
        initializeParameters();
    }
    
    /**
     * Initialize predictive coding hierarchy.
     */
    private void initializeHierarchy() {
        for (int i = 0; i < numLayers; i++) {
            int layerDim = stateDimension / (i + 1);
            hierarchy.add(new PredictiveLayer(layerDim, i));
        }
    }
    
    /**
     * Initialize model parameters.
     */
    private void initializeParameters() {
        // Initialize beliefs with prior
        Arrays.fill(beliefs, 0.0);
        
        // Initialize precision
        Arrays.fill(precision, priorPrecision);
        
        // Initialize generative model with random values
        for (int i = 0; i < observationDimension; i++) {
            for (int j = 0; j < stateDimension; j++) {
                generativeModel[i][j] = Math.random();
            }
            // Normalize rows
            double sum = 0.0;
            for (int j = 0; j < stateDimension; j++) {
                sum += generativeModel[i][j];
            }
            if (sum > 0) {
                for (int j = 0; j < stateDimension; j++) {
                    generativeModel[i][j] /= sum;
                }
            }
        }
    }
    
    /**
     * Compute variational free energy.
     */
    private double computeFreeEnergy(double[] observations) {
        // F = E_q[log q(s) - log p(o,s)]
        // = KL(q(s) || p(s)) - E_q[log p(o|s)]
        
        // KL divergence term
        double klDivergence = 0.0;
        for (int i = 0; i < stateDimension; i++) {
            klDivergence += beliefs[i] * Math.log(beliefs[i] + 1e-10);
        }
        
        // Expected log-likelihood term
        double expectedLogLikelihood = 0.0;
        for (int i = 0; i < observationDimension; i++) {
            double predictedObs = 0.0;
            for (int j = 0; j < stateDimension; j++) {
                predictedObs += beliefs[j] * generativeModel[i][j];
            }
            expectedLogLikelihood += observations[i] * Math.log(predictedObs + 1e-10);
        }
        
        return klDivergence - expectedLogLikelihood;
    }
    
    /**
     * Perform perception step: update beliefs to minimize free energy.
     */
    public void perceive(double[] observations) {
        // Compute predictions
        computePredictions();
        
        // Compute prediction errors
        double[] predictionErrors = new double[observationDimension];
        for (int i = 0; i < observationDimension; i++) {
            predictionErrors[i] = observations[i] - predictions[i];
        }
        
        // Update beliefs via precision-weighted prediction errors
        for (int i = 0; i < stateDimension; i++) {
            double errorSignal = 0.0;
            for (int j = 0; j < observationDimension; j++) {
                errorSignal += predictionErrors[j] * generativeModel[j][i] * precision[i];
            }
            beliefs[i] += learningRate * errorSignal;
            
            // Clamp beliefs to valid range
            beliefs[i] = Math.max(0.0, Math.min(1.0, beliefs[i]));
        }
        
        // Update precision based on prediction error
        updatePrecision(predictionErrors);
        
        // Propagate through hierarchy
        propagateThroughHierarchy(predictionErrors);
        
        // Compute and store free energy
        currentFreeEnergy = computeFreeEnergy(observations);
        freeEnergyHistory.add(currentFreeEnergy);
        if (freeEnergyHistory.size() > 1000) {
            freeEnergyHistory.remove(0);
        }
    }
    
    /**
     * Compute predictions from current beliefs.
     */
    private void computePredictions() {
        for (int i = 0; i < observationDimension; i++) {
            predictions[i] = 0.0;
            for (int j = 0; j < stateDimension; j++) {
                predictions[i] += beliefs[j] * generativeModel[i][j];
            }
        }
    }
    
    /**
     * Update precision based on prediction error magnitude.
     */
    private void updatePrecision(double[] predictionErrors) {
        double errorVariance = 0.0;
        for (double error : predictionErrors) {
            errorVariance += error * error;
        }
        errorVariance /= predictionErrors.length;
        
        // Precision is inverse variance
        double newPrecision = 1.0 / (errorVariance + 1e-10);
        
        // Smooth precision update
        for (int i = 0; i < stateDimension; i++) {
            precision[i] = 0.9 * precision[i] + 0.1 * newPrecision;
        }
    }
    
    /**
     * Propagate prediction errors through predictive coding hierarchy.
     */
    private void propagateThroughHierarchy(double[] predictionErrors) {
        double[] currentErrors = predictionErrors;
        
        for (PredictiveLayer layer : hierarchy) {
            layer.processErrors(currentErrors);
            currentErrors = layer.getOutputErrors();
        }
    }
    
    /**
     * Perform action step: select action to minimize expected free energy.
     */
    public int act(double[] observations, List<Action> availableActions) {
        // Compute expected free energy for each action
        double[] expectedFreeEnergies = new double[availableActions.size()];
        
        for (int i = 0; i < availableActions.size(); i++) {
            Action action = availableActions.get(i);
            expectedFreeEnergies[i] = computeExpectedFreeEnergy(observations, action);
        }
        
        // Select action with minimum expected free energy
        int selectedAction = actionSelector.selectAction(expectedFreeEnergies, temperature);
        
        return selectedAction;
    }
    
    /**
     * Compute expected free energy for an action.
     */
    private double computeExpectedFreeEnergy(double[] observations, Action action) {
        // G = E_p[E_q[log q(s) - log p(o,s)] + H(p(o))]
        
        // Simulate action outcome
        double[] predictedObservations = simulateAction(observations, action);
        
        // Compute free energy for predicted observations
        double predictedFreeEnergy = computeFreeEnergy(predictedObservations);
        
        // Add epistemic value (information gain)
        double informationGain = computeInformationGain(action);
        
        // Add pragmatic value (goal achievement)
        double pragmaticValue = computePragmaticValue(predictedObservations);
        
        return predictedFreeEnergy - informationGain - pragmaticValue;
    }
    
    /**
     * Simulate the outcome of an action.
     */
    private double[] simulateAction(double[] observations, Action action) {
        double[] simulated = new double[observations.length];
        
        // Simple linear dynamics model
        for (int i = 0; i < observations.length; i++) {
            simulated[i] = observations[i] + action.effect[i] * 0.1;
        }
        
        return simulated;
    }
    
    /**
     * Compute information gain (epistemic value).
     */
    private double computeInformationGain(Action action) {
        // Information gain = reduction in uncertainty (entropy)
        double currentUncertainty = computeEntropy(beliefs);
        
        // Simulate belief update after action
        double[] simulatedBeliefs = beliefs.clone();
        for (int i = 0; i < simulatedBeliefs.length; i++) {
            simulatedBeliefs[i] += action.effect[i % simulatedBeliefs.length] * 0.05;
        }
        
        double futureUncertainty = computeEntropy(simulatedBeliefs);
        
        return currentUncertainty - futureUncertainty;
    }
    
    /**
     * Compute pragmatic value (goal achievement).
     */
    private double computePragmaticValue(double[] observations) {
        // Pragmatic value = alignment with preferred outcomes
        double value = 0.0;
        
        for (int i = 0; i < observations.length; i++) {
            // Prefer high values for first half of observations
            if (i < observations.length / 2) {
                value += observations[i];
            } else {
                value -= observations[i];
            }
        }
        
        return value / observations.length;
    }
    
    /**
     * Compute entropy of a probability distribution.
     */
    private double computeEntropy(double[] distribution) {
        double entropy = 0.0;
        for (double p : distribution) {
            if (p > 0) {
                entropy -= p * Math.log(p);
            }
        }
        return entropy;
    }
    
    /**
     * Get current beliefs.
     */
    public double[] getBeliefs() {
        return beliefs.clone();
    }
    
    /**
     * Get current free energy.
     */
    public double getCurrentFreeEnergy() {
        return currentFreeEnergy;
    }
    
    /**
     * Get free energy history.
     */
    public List<Double> getFreeEnergyHistory() {
        return new ArrayList<>(freeEnergyHistory);
    }
    
    /**
     * Set learning parameters.
     */
    public void setLearningParameters(double learningRate, double temperature) {
        this.learningRate = learningRate;
        this.temperature = temperature;
    }
    
    /**
     * Reset the engine.
     */
    public void reset() {
        Arrays.fill(beliefs, 0.0);
        Arrays.fill(precision, priorPrecision);
        freeEnergyHistory.clear();
        currentFreeEnergy = 0.0;
        
        for (PredictiveLayer layer : hierarchy) {
            layer.reset();
        }
    }
    
    /**
     * Predictive Layer class for hierarchical processing.
     */
    private static class PredictiveLayer {
        private final int dimension;
        private final int layerIndex;
        private double[] state;
        private double[] outputErrors;
        
        public PredictiveLayer(int dimension, int layerIndex) {
            this.dimension = dimension;
            this.layerIndex = layerIndex;
            this.state = new double[dimension];
            this.outputErrors = new double[dimension];
        }
        
        public void processErrors(double[] inputErrors) {
            // Transform input errors to output errors
            int outputDim = Math.min(dimension, inputErrors.length);
            for (int i = 0; i < outputDim; i++) {
                outputErrors[i] = inputErrors[i] * 0.5; // Attenuate with depth
            }
        }
        
        public double[] getOutputErrors() {
            return outputErrors;
        }
        
        public void reset() {
            Arrays.fill(state, 0.0);
            Arrays.fill(outputErrors, 0.0);
        }
    }
    
    /**
     * Action class representing possible actions.
     */
    public static class Action {
        public final int actionId;
        public final double[] effect;
        public final String description;
        
        public Action(int actionId, double[] effect, String description) {
            this.actionId = actionId;
            this.effect = effect;
            this.description = description;
        }
    }
    
    /**
     * Action Selector class for action selection.
     */
    private static class ActionSelector {
        /**
         * Select action using softmax over negative expected free energy.
         */
        public int selectAction(double[] expectedFreeEnergies, double temperature) {
            // Convert to probabilities via softmax
            double[] probabilities = softmax(expectedFreeEnergies, temperature);
            
            // Sample from distribution
            double rand = Math.random();
            double cumulative = 0.0;
            for (int i = 0; i < probabilities.length; i++) {
                cumulative += probabilities[i];
                if (rand < cumulative) {
                    return i;
                }
            }
            
            return probabilities.length - 1;
        }
        
        /**
         * Softmax function.
         */
        private double[] softmax(double[] values, double temperature) {
            double[] expValues = new double[values.length];
            double max = Arrays.stream(values).max().orElse(0.0);
            
            double sum = 0.0;
            for (int i = 0; i < values.length; i++) {
                expValues[i] = Math.exp(-(values[i] - max) / temperature);
                sum += expValues[i];
            }
            
            for (int i = 0; i < values.length; i++) {
                expValues[i] /= sum;
            }
            
            return expValues;
        }
    }
    
    /**
     * Free Energy Statistics class.
     */
    public static class FreeEnergyStatistics {
        public final double currentFreeEnergy;
        public final double averageFreeEnergy;
        public final double minFreeEnergy;
        public final double maxFreeEnergy;
        public final boolean converged;
        
        public FreeEnergyStatistics(double currentFreeEnergy, double averageFreeEnergy,
                                    double minFreeEnergy, double maxFreeEnergy,
                                    boolean converged) {
            this.currentFreeEnergy = currentFreeEnergy;
            this.averageFreeEnergy = averageFreeEnergy;
            this.minFreeEnergy = minFreeEnergy;
            this.maxFreeEnergy = maxFreeEnergy;
            this.converged = converged;
        }
    }
    
    /**
     * Get free energy statistics.
     */
    public FreeEnergyStatistics getStatistics() {
        if (freeEnergyHistory.isEmpty()) {
            return new FreeEnergyStatistics(0.0, 0.0, 0.0, 0.0, false);
        }
        
        double avg = freeEnergyHistory.stream()
            .mapToDouble(d -> d)
            .average()
            .orElse(0.0);
        
        double min = freeEnergyHistory.stream()
            .mapToDouble(d -> d)
            .min()
            .orElse(0.0);
        
        double max = freeEnergyHistory.stream()
            .mapToDouble(d -> d)
            .max()
            .orElse(0.0);
        
        // Check convergence (recent values stable)
        boolean converged = false;
        if (freeEnergyHistory.size() >= 10) {
            double recentAvg = freeEnergyHistory.subList(freeEnergyHistory.size() - 10, freeEnergyHistory.size())
                .stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
            converged = Math.abs(avg - recentAvg) < 0.01 * avg;
        }
        
        return new FreeEnergyStatistics(currentFreeEnergy, avg, min, max, converged);
    }
}
