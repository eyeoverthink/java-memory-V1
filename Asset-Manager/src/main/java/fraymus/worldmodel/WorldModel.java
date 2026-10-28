package fraymus.worldmodel;

import java.util.*;
import java.util.concurrent.*;

/**
 * World Model with Predictive Coding Hierarchy
 * 
 * Constructs a hierarchical world model using predictive coding principles,
 * enabling the system to build internal representations of external reality
 * through multi-level prediction and error correction.
 * 
 * Phase 5.2: World Model with Predictive Coding Hierarchy Implementation
 * 
 * Key capabilities:
 * - Hierarchical predictive coding architecture
 * - Multi-scale temporal prediction
 * - Causal structure learning
 * - Counterfactual reasoning
 * - World state simulation and planning
 * 
 * Mathematical foundation:
 * - Predictive coding: r = x - P(x) (prediction error)
 * - Hierarchical inference: r_l = x_l - P_l(x_{l+1})
 * - Causal model: P(X|Pa(X)) for each variable X
 * - Counterfactual: do(X=x) in structural causal model
 * 
 * Architecture:
 * - Bottom-up: Sensory input → Low-level features → High-level concepts
 * - Top-down: High-level predictions → Low-level expectations → Sensory predictions
 * - Lateral: Error correction and belief propagation
 */
public class WorldModel implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    // Hierarchical layers
    private List<PredictiveCodingLayer> layers;
    private final int numLayers;
    
    // Causal graph
    private CausalWorldGraph causalGraph;
    
    // State representation
    private WorldState currentState;
    private WorldState predictedState;
    
    // Temporal dynamics
    private TemporalPredictor temporalPredictor;
    
    // Learning parameters
    private double learningRate = 0.01;
    private double predictionWeight = 0.7;
    private double errorWeight = 0.3;
    
    // Statistics
    private long totalPredictions;
    private long correctPredictions;
    private double averagePredictionError;
    
    public WorldModel(int numLayers, int[] layerDimensions) {
        this.numLayers = numLayers;
        this.layers = new ArrayList<>();
        this.causalGraph = new CausalWorldGraph();
        this.currentState = new WorldState();
        this.predictedState = new WorldState();
        this.temporalPredictor = new TemporalPredictor();
        this.totalPredictions = 0;
        this.correctPredictions = 0;
        this.averagePredictionError = 0.0;
        
        // Initialize hierarchical layers
        initializeLayers(layerDimensions);
    }

    public synchronized void restoreState(WorldModel saved) {
        if (saved.getSensoryDimension()!=getSensoryDimension() || saved.numLayers!=numLayers) throw new IllegalArgumentException("World snapshot dimensions differ");
        layers=saved.layers; causalGraph=saved.causalGraph; currentState=saved.currentState; predictedState=saved.predictedState; temporalPredictor=saved.temporalPredictor;
        learningRate=saved.learningRate; predictionWeight=saved.predictionWeight; errorWeight=saved.errorWeight;
        totalPredictions=saved.totalPredictions; correctPredictions=saved.correctPredictions; averagePredictionError=saved.averagePredictionError;
    }

    public int getSensoryDimension() { return layers.get(0).dimension; }
    
    /**
     * Initialize hierarchical predictive coding layers.
     */
    private void initializeLayers(int[] layerDimensions) {
        for (int i = 0; i < numLayers; i++) {
            int dim = layerDimensions[i % layerDimensions.length];
            layers.add(new PredictiveCodingLayer(dim, i));
        }
        
        // Connect layers (bottom-up and top-down)
        for (int i = 0; i < layers.size() - 1; i++) {
            layers.get(i).connectToHigherLayer(layers.get(i + 1));
            layers.get(i + 1).connectToLowerLayer(layers.get(i));
        }
    }
    
    /**
     * Process sensory input through the hierarchy.
     */
    public void processInput(double[] sensoryInput) {
        // Update current world state
        currentState.updateFromSensoryInput(sensoryInput);
        
        // Bottom-up processing
        double[] currentRepresentation = sensoryInput.clone();
        for (PredictiveCodingLayer layer : layers) {
            layer.processBottomUp(currentRepresentation);
            currentRepresentation = layer.getRepresentation();
        }
        
        // Top-down predictions
        for (int i = layers.size() - 1; i >= 0; i--) {
            PredictiveCodingLayer layer = layers.get(i);
            layer.processTopDown();
            
            // Update causal graph with learned relationships
            causalGraph.updateFromLayer(layer);
        }
        
        // Update predicted state
        predictedState.updateFromLayerRepresentations(layers);
        
        // Update temporal predictor
        temporalPredictor.update(currentState, predictedState);
        
        // Compute prediction error
        computePredictionError(sensoryInput);
    }
    
    /**
     * Compute prediction error.
     */
    private void computePredictionError(double[] sensoryInput) {
        double[] predictions = predictNextState(sensoryInput);
        
        double error = 0.0;
        for (int i = 0; i < sensoryInput.length; i++) {
            error += Math.pow(sensoryInput[i] - predictions[i], 2);
        }
        error /= sensoryInput.length;
        
        // Update average error
        averagePredictionError = 0.95 * averagePredictionError + 0.05 * error;
        
        totalPredictions++;
        if (error < 0.1) {
            correctPredictions++;
        }
    }
    
    /**
     * Predict next state given current input.
     */
    public double[] predictNextState(double[] currentInput) {
        double[] prediction = currentInput.clone();
        
        // Use hierarchical predictions
        for (PredictiveCodingLayer layer : layers) {
            double[] layerPrediction = layer.predict();
            
            // Combine predictions (weighted average)
            int dim = Math.min(prediction.length, layerPrediction.length);
            for (int i = 0; i < dim; i++) {
                prediction[i] = predictionWeight * prediction[i] + 
                               errorWeight * layerPrediction[i];
            }
        }
        
        // Use temporal predictor
        double[] temporalPrediction = temporalPredictor.predict(currentInput);
        for (int i = 0; i < prediction.length; i++) {
            prediction[i] = 0.7 * prediction[i] + 0.3 * temporalPrediction[i];
        }
        
        return prediction;
    }
    
    /**
     * Perform counterfactual reasoning: what would happen if X changed?
     */
    public CounterfactualResult reasonCounterfactual(String variable, double newValue) {
        // Get current state copy
        WorldState counterfactualState = currentState.clone();
        
        // Apply intervention (do-calculus)
        counterfactualState.setVariable(variable, newValue);
        
        // Propagate through causal graph
        causalGraph.propagateIntervention(variable, newValue, counterfactualState);
        
        // Predict outcome
        double[] predictedOutcome = predictFromState(counterfactualState);
        
        return new CounterfactualResult(variable, newValue, predictedOutcome, counterfactualState);
    }
    
    /**
     * Predict from a given world state.
     */
    private double[] predictFromState(WorldState state) {
        double[] representation = state.getSensoryRepresentation();
        
        // Process through hierarchy
        for (PredictiveCodingLayer layer : layers) {
            layer.setRepresentation(representation);
            layer.processTopDown();
            representation = layer.getRepresentation();
        }
        
        return representation;
    }
    
    /**
     * Learn causal structure from observations.
     */
    public void learnCausalStructure(List<double[]> observations) {
        for (double[] obs : observations) {
            processInput(obs);
        }
        
        // Refine causal graph
        causalGraph.pruneWeakEdges(0.1);
        causalGraph.normalizeEdgeWeights();
    }
    
    /**
     * Simulate world state over multiple steps.
     */
    public List<double[]> simulateWorld(double[] initialState, int steps) {
        List<double[]> trajectory = new ArrayList<>();
        double[] currentState = initialState.clone();
        
        for (int i = 0; i < steps; i++) {
            trajectory.add(currentState.clone());
            currentState = predictNextState(currentState);
        }
        
        return trajectory;
    }
    
    /**
     * Plan actions to achieve goal state.
     */
    public List<Action> planActions(double[] goalState, int maxDepth) {
        List<Action> plan = new ArrayList<>();
        double[] currentState = this.currentState.getSensoryRepresentation().clone();
        
        for (int depth = 0; depth < maxDepth; depth++) {
            // Find action that minimizes distance to goal
            Action bestAction = findBestAction(currentState, goalState);
            
            if (bestAction == null) {
                break;
            }
            
            plan.add(bestAction);
            
            // Apply action
            currentState = applyAction(currentState, bestAction);
            
            // Check if goal reached
            if (distanceToGoal(currentState, goalState) < 0.1) {
                break;
            }
        }
        
        return plan;
    }
    
    /**
     * Find best action to reach goal.
     */
    private Action findBestAction(double[] currentState, double[] goalState) {
        // Generate candidate actions
        List<Action> candidates = generateCandidateActions(currentState);
        
        Action bestAction = null;
        double bestScore = Double.MAX_VALUE;
        
        for (Action action : candidates) {
            double[] predictedNext = applyAction(currentState, action);
            double score = distanceToGoal(predictedNext, goalState);
            
            if (score < bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }
        
        return bestAction;
    }
    
    /**
     * Generate candidate actions.
     */
    private List<Action> generateCandidateActions(double[] state) {
        List<Action> actions = new ArrayList<>();
        
        // Simple actions: increment, decrement, or set each dimension
        for (int i = 0; i < state.length; i++) {
            double[] effect1 = new double[state.length];
            effect1[i] = 0.1;
            actions.add(new Action(i, effect1, "increment_" + i));
            
            double[] effect2 = new double[state.length];
            effect2[i] = -0.1;
            actions.add(new Action(i + state.length, effect2, "decrement_" + i));
        }
        
        return actions;
    }
    
    /**
     * Apply action to state.
     */
    private double[] applyAction(double[] state, Action action) {
        double[] newState = state.clone();
        
        for (int i = 0; i < newState.length; i++) {
            newState[i] += action.effect[i];
        }
        
        return newState;
    }
    
    /**
     * Compute distance to goal.
     */
    private double distanceToGoal(double[] state, double[] goal) {
        double distance = 0.0;
        for (int i = 0; i < state.length; i++) {
            distance += Math.pow(state[i] - goal[i], 2);
        }
        return Math.sqrt(distance);
    }
    
    /**
     * Get model statistics.
     */
    public WorldModelStatistics getStatistics() {
        double accuracy = totalPredictions > 0 ? 
            (double) correctPredictions / totalPredictions : 0.0;
        
        return new WorldModelStatistics(
            totalPredictions,
            correctPredictions,
            accuracy,
            averagePredictionError,
            causalGraph.getEdgeCount(),
            layers.size()
        );
    }
    
    /**
     * Set learning parameters.
     */
    public void setLearningParameters(double learningRate, double predictionWeight, 
                                     double errorWeight) {
        this.learningRate = learningRate;
        this.predictionWeight = predictionWeight;
        this.errorWeight = errorWeight;
    }
    
    /**
     * Reset the world model.
     */
    public void reset() {
        for (PredictiveCodingLayer layer : layers) {
            layer.reset();
        }
        causalGraph.reset();
        currentState.reset();
        predictedState.reset();
        temporalPredictor.reset();
        totalPredictions = 0;
        correctPredictions = 0;
        averagePredictionError = 0.0;
    }
    
    /**
     * Predictive Coding Layer class.
     */
    private static class PredictiveCodingLayer implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int dimension;
        private final int layerIndex;
        private double[] representation;
        private double[] prediction;
        private double[] error;
        
        private PredictiveCodingLayer higherLayer;
        private PredictiveCodingLayer lowerLayer;
        
        private double[][] bottomUpWeights;
        private double[][] topDownWeights;
        
        public PredictiveCodingLayer(int dimension, int layerIndex) {
            this.dimension = dimension;
            this.layerIndex = layerIndex;
            this.representation = new double[dimension];
            this.prediction = new double[dimension];
            this.error = new double[dimension];
            
            // Initialize weights
            this.bottomUpWeights = new double[dimension][dimension];
            this.topDownWeights = new double[dimension][dimension];
            
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    bottomUpWeights[i][j] = Math.random() * 0.1;
                    topDownWeights[i][j] = Math.random() * 0.1;
                }
            }
        }
        
        public void connectToHigherLayer(PredictiveCodingLayer layer) {
            this.higherLayer = layer;
        }
        
        public void connectToLowerLayer(PredictiveCodingLayer layer) {
            this.lowerLayer = layer;
        }
        
        public void processBottomUp(double[] input) {
            // Compute representation from input
            for (int i = 0; i < dimension; i++) {
                representation[i] = 0.0;
                for (int j = 0; j < Math.min(input.length, dimension); j++) {
                    representation[i] += bottomUpWeights[i][j] * input[j];
                }
                // Apply activation
                representation[i] = Math.tanh(representation[i]);
            }
            
            // Compute prediction error
            computePredictionError(input);
        }
        
        public void processTopDown() {
            // Generate prediction from higher layer
            if (higherLayer != null) {
                double[] higherRep = higherLayer.getRepresentation();
                for (int i = 0; i < dimension; i++) {
                    prediction[i] = 0.0;
                    for (int j = 0; j < Math.min(higherRep.length, dimension); j++) {
                        prediction[i] += topDownWeights[i][j] * higherRep[j];
                    }
                }
            } else {
                // Top layer: predict from own representation
                System.arraycopy(representation, 0, prediction, 0, dimension);
            }
        }
        
        private void computePredictionError(double[] target) {
            for (int i = 0; i < dimension; i++) {
                error[i] = target[i] - prediction[i];
            }
        }
        
        public double[] predict() {
            return prediction.clone();
        }
        
        public double[] getRepresentation() {
            return representation.clone();
        }
        
        public void setRepresentation(double[] rep) {
            System.arraycopy(rep, 0, representation, 0, 
                           Math.min(rep.length, dimension));
        }
        
        public void reset() {
            Arrays.fill(representation, 0.0);
            Arrays.fill(prediction, 0.0);
            Arrays.fill(error, 0.0);
        }
    }
    
    /**
     * Causal World Graph class.
     */
    private static class CausalWorldGraph implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Map<String, CausalNode> nodes;
        private final Map<String, Map<String, Double>> edges;
        
        public CausalWorldGraph() {
            this.nodes = new HashMap<>();
            this.edges = new HashMap<>();
        }
        
        public void addNode(String nodeId) {
            nodes.putIfAbsent(nodeId, new CausalNode(nodeId));
        }
        
        public void addEdge(String from, String to, double weight) {
            addNode(from);
            addNode(to);
            
            edges.computeIfAbsent(from, k -> new HashMap<>()).put(to, weight);
            nodes.get(from).addChild(to);
            nodes.get(to).addParent(from);
        }
        
        public void updateFromLayer(PredictiveCodingLayer layer) {
            // Extract causal relationships from layer weights
            // (simplified implementation)
        }
        
        public void propagateIntervention(String variable, double newValue, WorldState state) {
            // Propagate intervention through causal graph
            state.setVariable(variable, newValue);
            
            Map<String, Double> children = edges.get(variable);
            if (children != null) {
                for (Map.Entry<String, Double> entry : children.entrySet()) {
                    double childValue = newValue * entry.getValue();
                    propagateIntervention(entry.getKey(), childValue, state);
                }
            }
        }
        
        public void pruneWeakEdges(double threshold) {
            for (Map.Entry<String, Map<String, Double>> entry : edges.entrySet()) {
                entry.getValue().entrySet().removeIf(e -> e.getValue() < threshold);
            }
        }
        
        public void normalizeEdgeWeights() {
            for (Map<String, Double> edgeMap : edges.values()) {
                double sum = edgeMap.values().stream().mapToDouble(d -> d).sum();
                if (sum > 0) {
                    edgeMap.replaceAll((k, v) -> v / sum);
                }
            }
        }
        
        public int getEdgeCount() {
            return edges.values().stream().mapToInt(Map::size).sum();
        }
        
        public void reset() {
            nodes.clear();
            edges.clear();
        }
    }
    
    /**
     * Causal Node class.
     */
    private static class CausalNode implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final String nodeId;
        public final Set<String> parents;
        public final Set<String> children;
        
        public CausalNode(String nodeId) {
            this.nodeId = nodeId;
            this.parents = new HashSet<>();
            this.children = new HashSet<>();
        }
        
        public void addParent(String parentId) {
            parents.add(parentId);
        }
        
        public void addChild(String childId) {
            children.add(childId);
        }
    }
    
    /**
     * World State class.
     */
    private static class WorldState implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Map<String, Double> variables;
        private double[] sensoryRepresentation;
        
        public WorldState() {
            this.variables = new HashMap<>();
            this.sensoryRepresentation = new double[0];
        }
        
        public void updateFromSensoryInput(double[] input) {
            this.sensoryRepresentation = input.clone();
            
            // Extract variables from input
            for (int i = 0; i < input.length; i++) {
                variables.put("sensor_" + i, input[i]);
            }
        }
        
        public void updateFromLayerRepresentations(List<PredictiveCodingLayer> layers) {
            if (!layers.isEmpty()) {
                sensoryRepresentation = layers.get(0).getRepresentation();
            }
        }
        
        public void setVariable(String name, double value) {
            variables.put(name, value);
        }
        
        public double getVariable(String name) {
            return variables.getOrDefault(name, 0.0);
        }
        
        public double[] getSensoryRepresentation() {
            return sensoryRepresentation.clone();
        }
        
        public WorldState clone() {
            WorldState clone = new WorldState();
            clone.variables.putAll(this.variables);
            clone.sensoryRepresentation = this.sensoryRepresentation.clone();
            return clone;
        }
        
        public void reset() {
            variables.clear();
            sensoryRepresentation = new double[0];
        }
    }
    
    /**
     * Temporal Predictor class.
     */
    private static class TemporalPredictor implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private double[] previousState;
        private double[] stateChange;
        
        public void update(WorldState current, WorldState predicted) {
            double[] currentRep = current.getSensoryRepresentation();
            
            if (previousState != null && previousState.length == currentRep.length) {
                stateChange = new double[currentRep.length];
                for (int i = 0; i < currentRep.length; i++) {
                    stateChange[i] = currentRep[i] - previousState[i];
                }
            }
            
            previousState = currentRep.clone();
        }
        
        public double[] predict(double[] currentState) {
            if (stateChange == null) {
                return currentState.clone();
            }
            
            double[] prediction = new double[currentState.length];
            for (int i = 0; i < currentState.length; i++) {
                prediction[i] = currentState[i] + (i < stateChange.length ? stateChange[i] : 0.0);
            }
            
            return prediction;
        }
        
        public void reset() {
            previousState = null;
            stateChange = null;
        }
    }
    
    /**
     * Action class.
     */
    public static class Action implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
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
     * Counterfactual Result class.
     */
    public static class CounterfactualResult implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final String intervenedVariable;
        public final double intervenedValue;
        public final double[] predictedOutcome;
        public final WorldState counterfactualState;
        
        public CounterfactualResult(String intervenedVariable, double intervenedValue,
                                    double[] predictedOutcome, WorldState counterfactualState) {
            this.intervenedVariable = intervenedVariable;
            this.intervenedValue = intervenedValue;
            this.predictedOutcome = predictedOutcome;
            this.counterfactualState = counterfactualState;
        }
    }
    
    /**
     * World Model Statistics class.
     */
    public static class WorldModelStatistics implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final long totalPredictions;
        public final long correctPredictions;
        public final double accuracy;
        public final double averageError;
        public final int causalEdges;
        public final int numLayers;
        
        public WorldModelStatistics(long totalPredictions, long correctPredictions,
                                   double accuracy, double averageError,
                                   int causalEdges, int numLayers) {
            this.totalPredictions = totalPredictions;
            this.correctPredictions = correctPredictions;
            this.accuracy = accuracy;
            this.averageError = averageError;
            this.causalEdges = causalEdges;
            this.numLayers = numLayers;
        }
        
        @Override
        public String toString() {
            return String.format(
                "WorldModelStats[predictions=%d, accuracy=%.2f%%, error=%.4f, edges=%d, layers=%d]",
                totalPredictions, accuracy * 100, averageError, causalEdges, numLayers
            );
        }
    }
}
