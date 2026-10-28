package fraymus.embodied;

import java.util.*;
import java.util.concurrent.*;

/**
 * Embodied AI with Sensorimotor Grounding
 * 
 * Integrates embodied cognition principles, grounding abstract representations
 * in sensorimotor experience through continuous interaction with the environment.
 * 
 * Phase 5.3: Embodied AI with Sensorimotor Grounding Implementation
 * 
 * Key capabilities:
 * - Sensorimotor contingency learning
 * - Body schema and proprioception
 * - Affordance perception
 * - Active perception through action
 * - Grounded symbol formation
 * 
 * Mathematical foundation:
 * - Sensorimotor contingency: S(a, o) = p(o|a) (probability of observation given action)
 * - Body schema: B(s) = mapping from proprioception to body state
 * - Affordance: A(x) = set of actions possible in state x
 * - Grounded meaning: M(s) = ∫ p(o|s) * p(a|o) da do (action-observation integration)
 * 
 * Architecture:
 * - Sensory layer: Vision, audition, touch, proprioception
 * - Motor layer: Action generation, motor commands
 * - Sensorimotor integration: Contingency learning, affordance mapping
 * - Symbol grounding: Abstract concepts grounded in sensorimotor patterns
 */
public class EmbodiedAI implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    // Sensorimotor layers
    private SensoryLayer sensoryLayer;
    private MotorLayer motorLayer;
    private SensorimotorIntegrator integrator;
    
    // Body schema
    private BodySchema bodySchema;
    
    // Affordance perception
    private AffordancePerceiver affordancePerceiver;
    
    // Grounded symbols
    private GroundedSymbolSystem symbolSystem;
    
    // Learning parameters
    private double learningRate = 0.05;
    private double explorationRate = 0.2;
    
    // Statistics
    private long totalInteractions;
    private long successfulActions;
    private double averageReward;
    
    public EmbodiedAI(int sensoryDimension, int motorDimension) {
        this.sensoryLayer = new SensoryLayer(sensoryDimension);
        this.motorLayer = new MotorLayer(motorDimension);
        this.integrator = new SensorimotorIntegrator(sensoryDimension, motorDimension);
        this.bodySchema = new BodySchema(motorDimension);
        this.affordancePerceiver = new AffordancePerceiver();
        this.symbolSystem = new GroundedSymbolSystem();
        this.totalInteractions = 0;
        this.successfulActions = 0;
        this.averageReward = 0.0;
    }

    public synchronized void restoreState(EmbodiedAI saved) {
        if(saved.getSensoryDimension()!=getSensoryDimension() || saved.getMotorDimension()!=getMotorDimension()) throw new IllegalArgumentException("Body snapshot dimensions differ");
        sensoryLayer=saved.sensoryLayer; motorLayer=saved.motorLayer; integrator=saved.integrator; bodySchema=saved.bodySchema;
        affordancePerceiver=saved.affordancePerceiver; symbolSystem=saved.symbolSystem; learningRate=saved.learningRate; explorationRate=saved.explorationRate;
        totalInteractions=saved.totalInteractions; successfulActions=saved.successfulActions; averageReward=saved.averageReward;
    }

    /** Learn the executed command, which may differ from the original motor proposal. */
    public synchronized void learnTransition(SensoryInput before, MotorCommand executed, SensoryInput observed, double reward) {
        integrator.updateContingency(before, executed);
        integrator.learnContingency(before, executed, observed);
        affordancePerceiver.affordanceExpectations.compute(executed.actionType, (key,value) -> .9*(value==null?.5:value)+.1*reward);
        symbolSystem.groundFromExperience(before, executed);
        bodySchema.updateFromProprioception(observed.getProprioception());
        receiveFeedback(observed,reward);
    }

    public int getSensoryDimension() { return sensoryLayer.dimension; }
    public int getMotorDimension() { return motorLayer.dimension; }
    
    /**
     * Initialize the embodied system.
     */
    public void initialize() {
        sensoryLayer.initialize();
        motorLayer.initialize();
        integrator.initialize();
        bodySchema.initialize();
        affordancePerceiver.initialize();
        symbolSystem.initialize();
    }
    
    /**
     * Process sensory input and generate motor output.
     */
    public MotorCommand process(SensoryInput sensoryInput) {
        // Update sensory layer
        sensoryLayer.update(sensoryInput);
        
        // Update body schema from proprioception
        bodySchema.updateFromProprioception(sensoryInput.getProprioception());
        
        // Detect affordances
        List<Affordance> affordances = affordancePerceiver.detect(sensoryInput);
        
        // Select action based on affordances and goals
        MotorCommand command = selectAction(sensoryInput, affordances);
        
        // Update sensorimotor contingencies
        integrator.updateContingency(sensoryInput, command);
        
        // Ground symbols from sensorimotor patterns
        symbolSystem.groundFromExperience(sensoryInput, command);
        
        return command;
    }
    
    /**
     * Select action based on affordances and goals.
     */
    private MotorCommand selectAction(SensoryInput sensoryInput, List<Affordance> affordances) {
        // If affordances available, select from them
        if (!affordances.isEmpty()) {
            // Select affordance with highest expected value
            Affordance bestAffordance = affordances.stream()
                .max(Comparator.comparingDouble(a -> a.expectedValue))
                .orElse(affordances.get(0));
            
            return motorLayer.generateCommand(bestAffordance.actionType);
        }
        
        // Otherwise, use exploration or exploitation
        if (Math.random() < explorationRate) {
            return motorLayer.generateRandomCommand();
        } else {
            return motorLayer.generateBestCommand(sensoryInput);
        }
    }
    
    /**
     * Receive feedback from environment.
     */
    public void receiveFeedback(SensoryInput newSensoryInput, double reward) {
        // Update statistics
        totalInteractions++;
        averageReward = 0.95 * averageReward + 0.05 * reward;
        
        if (reward > 0.5) {
            successfulActions++;
        }
        
        // Update sensorimotor integrator with outcome
        integrator.updateFromOutcome(newSensoryInput, reward);
        
        // Update affordance expectations
        affordancePerceiver.updateExpectations(newSensoryInput, reward);
        
        // Refine grounded symbols
        symbolSystem.refineFromFeedback(newSensoryInput, reward);
    }
    
    /**
     * Learn sensorimotor contingencies through interaction.
     */
    public void learnContingencies(List<SensoryInput> sensoryHistory, 
                                   List<MotorCommand> motorHistory,
                                   List<SensoryInput> outcomeHistory) {
        for (int i = 0; i < sensoryHistory.size(); i++) {
            SensoryInput sensoryInput = sensoryHistory.get(i);
            MotorCommand command = motorHistory.get(i);
            SensoryInput outcome = outcomeHistory.get(i);
            
            integrator.learnContingency(sensoryInput, command, outcome);
        }
    }
    
    /**
     * Get grounded meaning of a symbol.
     */
    public GroundedMeaning getGroundedMeaning(String symbol) {
        return symbolSystem.getMeaning(symbol);
    }
    
    /**
     * Check if a symbol is grounded.
     */
    public boolean isSymbolGrounded(String symbol) {
        return symbolSystem.isGrounded(symbol);
    }
    
    /**
     * Get current body state.
     */
    public BodyState getBodyState() {
        return bodySchema.getCurrentState();
    }
    
    /**
     * Get detected affordances.
     */
    public List<Affordance> getDetectedAffordances(SensoryInput sensoryInput) {
        return affordancePerceiver.detect(sensoryInput);
    }
    
    /**
     * Get interaction statistics.
     */
    public InteractionStatistics getStatistics() {
        double successRate = totalInteractions > 0 ? 
            (double) successfulActions / totalInteractions : 0.0;
        
        return new InteractionStatistics(
            totalInteractions,
            successfulActions,
            successRate,
            averageReward,
            symbolSystem.getGroundedSymbolCount(),
            integrator.getLearnedContingencyCount()
        );
    }
    
    /**
     * Set learning parameters.
     */
    public void setLearningParameters(double learningRate, double explorationRate) {
        this.learningRate = learningRate;
        this.explorationRate = explorationRate;
    }
    
    /**
     * Reset the embodied system.
     */
    public void reset() {
        sensoryLayer.reset();
        motorLayer.reset();
        integrator.reset();
        bodySchema.reset();
        affordancePerceiver.reset();
        symbolSystem.reset();
        totalInteractions = 0;
        successfulActions = 0;
        averageReward = 0.0;
    }
    
    /**
     * Sensory Layer class.
     */
    private static class SensoryLayer implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int dimension;
        private double[] currentSensoryState;
        private double[][] sensoryHistory;
        private int historyIndex;
        
        public SensoryLayer(int dimension) {
            this.dimension = dimension;
            this.currentSensoryState = new double[dimension];
            this.sensoryHistory = new double[100][dimension];
            this.historyIndex = 0;
        }
        
        public void initialize() {
            Arrays.fill(currentSensoryState, 0.0);
        }
        
        public void update(SensoryInput input) {
            currentSensoryState = input.getVector();
            
            // Store in history
            sensoryHistory[historyIndex] = currentSensoryState.clone();
            historyIndex = (historyIndex + 1) % sensoryHistory.length;
        }
        
        public void reset() {
            Arrays.fill(currentSensoryState, 0.0);
            for (int i = 0; i < sensoryHistory.length; i++) {
                Arrays.fill(sensoryHistory[i], 0.0);
            }
            historyIndex = 0;
        }
    }
    
    /**
     * Motor Layer class.
     */
    private static class MotorLayer implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int dimension;
        private double[] currentMotorCommand;
        private double[][] motorWeights;
        
        public MotorLayer(int dimension) {
            this.dimension = dimension;
            this.currentMotorCommand = new double[dimension];
            this.motorWeights = new double[dimension][dimension];
        }
        
        public void initialize() {
            Arrays.fill(currentMotorCommand, 0.0);
            
            // Initialize random weights
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    motorWeights[i][j] = Math.random() * 0.1;
                }
            }
        }
        
        public MotorCommand generateCommand(String actionType) {
            double[] command = new double[dimension];
            
            // Generate command based on action type
            switch (actionType) {
                case "grasp":
                    command[0] = 1.0;  // Close gripper
                    break;
                case "release":
                    command[0] = -1.0; // Open gripper
                    break;
                case "move_forward":
                    command[1] = 1.0;
                    break;
                case "move_backward":
                    command[1] = -1.0;
                    break;
                case "rotate_left":
                    command[2] = 1.0;
                    break;
                case "rotate_right":
                    command[2] = -1.0;
                    break;
                default:
                    // Random command
                    for (int i = 0; i < dimension; i++) {
                        command[i] = (Math.random() - 0.5) * 2.0;
                    }
            }
            
            currentMotorCommand = command.clone();
            return new MotorCommand(actionType, command);
        }
        
        public MotorCommand generateRandomCommand() {
            double[] command = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                command[i] = (Math.random() - 0.5) * 2.0;
            }
            currentMotorCommand = command.clone();
            return new MotorCommand("random", command);
        }
        
        public MotorCommand generateBestCommand(SensoryInput sensoryInput) {
            // Use learned weights to generate best command
            double[] sensoryVector = sensoryInput.getVector();
            double[] command = new double[dimension];
            
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < Math.min(sensoryVector.length, dimension); j++) {
                    command[i] += motorWeights[i][j] * sensoryVector[j];
                }
            }
            
            currentMotorCommand = command.clone();
            return new MotorCommand("learned", command);
        }
        
        public void reset() {
            Arrays.fill(currentMotorCommand, 0.0);
        }
    }
    
    /**
     * Sensorimotor Integrator class.
     */
    private static class SensorimotorIntegrator implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int sensoryDim;
        private final int motorDim;
        private final Map<String, SensorimotorContingency> contingencies;
        
        public SensorimotorIntegrator(int sensoryDim, int motorDim) {
            this.sensoryDim = sensoryDim;
            this.motorDim = motorDim;
            this.contingencies = new HashMap<>();
        }
        
        public void initialize() {
            // Initialize with basic contingencies
        }
        
        public void updateContingency(SensoryInput sensoryInput, MotorCommand command) {
            String key = generateContingencyKey(sensoryInput, command);
            
            SensorimotorContingency contingency = contingencies.computeIfAbsent(
                key, k -> new SensorimotorContingency(sensoryDim, motorDim)
            );
            
            contingency.update(sensoryInput.getVector(), command.vector);
        }
        
        public void updateFromOutcome(SensoryInput outcome, double reward) {
            // Update contingency based on outcome
        }
        
        public void learnContingency(SensoryInput sensoryInput, MotorCommand command, 
                                    SensoryInput outcome) {
            String key = generateContingencyKey(sensoryInput, command);
            
            SensorimotorContingency contingency = contingencies.computeIfAbsent(
                key, k -> new SensorimotorContingency(sensoryDim, motorDim)
            );
            
            contingency.learn(sensoryInput.getVector(), command.vector, outcome.getVector());
        }
        
        private String generateContingencyKey(SensoryInput sensoryInput, MotorCommand command) {
            return Arrays.hashCode(sensoryInput.getVector()) + "_" + command.actionType;
        }
        
        public int getLearnedContingencyCount() {
            return contingencies.size();
        }
        
        public void reset() {
            contingencies.clear();
        }
    }
    
    /**
     * Sensorimotor Contingency class.
     */
    private static class SensorimotorContingency implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final double[][] transitionMatrix;
        private final double[] outcomeMeans;
        private int count;
        
        public SensorimotorContingency(int sensoryDim, int motorDim) {
            this.transitionMatrix = new double[sensoryDim][sensoryDim];
            this.outcomeMeans = new double[sensoryDim];
            this.count = 0;
        }
        
        public void update(double[] sensoryInput, double[] motorCommand) {
            count++;
        }
        
        public void learn(double[] sensoryInput, double[] motorCommand, double[] outcome) {
            // Learn transition from (sensory, motor) to outcome
            for (int i = 0; i < Math.min(sensoryInput.length, outcome.length); i++) {
                transitionMatrix[i][i] = 0.9 * transitionMatrix[i][i] + 0.1 * outcome[i];
                outcomeMeans[i] = 0.9 * outcomeMeans[i] + 0.1 * outcome[i];
            }
            count++;
        }
    }
    
    /**
     * Body Schema class.
     */
    private static class BodySchema implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final int dimension;
        private double[] jointAngles;
        private double[] jointVelocities;
        private double[] proprioception;
        
        public BodySchema(int dimension) {
            this.dimension = dimension;
            this.jointAngles = new double[dimension];
            this.jointVelocities = new double[dimension];
            this.proprioception = new double[dimension];
        }
        
        public void initialize() {
            Arrays.fill(jointAngles, 0.0);
            Arrays.fill(jointVelocities, 0.0);
            Arrays.fill(proprioception, 0.0);
        }
        
        public void updateFromProprioception(double[] proprioceptionInput) {
            System.arraycopy(proprioceptionInput, 0, this.proprioception, 0, 
                           Math.min(proprioceptionInput.length, dimension));
            
            // Update joint angles from proprioception
            for (int i = 0; i < Math.min(proprioceptionInput.length, dimension); i++) {
                jointAngles[i] = proprioceptionInput[i];
            }
        }
        
        public BodyState getCurrentState() {
            return new BodyState(jointAngles.clone(), jointVelocities.clone());
        }
        
        public void reset() {
            Arrays.fill(jointAngles, 0.0);
            Arrays.fill(jointVelocities, 0.0);
            Arrays.fill(proprioception, 0.0);
        }
    }
    
    /**
     * Affordance Perceiver class.
     */
    private static class AffordancePerceiver implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Map<String, Double> affordanceExpectations;
        
        public AffordancePerceiver() {
            this.affordanceExpectations = new HashMap<>();
        }
        
        public void initialize() {
            // Initialize with basic affordances
            affordanceExpectations.put("grasp", 0.5);
            affordanceExpectations.put("release", 0.5);
            affordanceExpectations.put("move_forward", 0.5);
            affordanceExpectations.put("move_backward", 0.5);
        }
        
        public List<Affordance> detect(SensoryInput sensoryInput) {
            List<Affordance> affordances = new ArrayList<>();
            
            // Simple affordance detection based on sensory input
            double[] sensoryVector = sensoryInput.getVector();
            
            // Detect grasp affordance (if object detected)
            if (sensoryVector.length > 0 && sensoryVector[0] > 0.5) {
                double expectedValue = affordanceExpectations.getOrDefault("grasp", 0.5);
                affordances.add(new Affordance("grasp", expectedValue));
            }
            
            // Detect movement affordances
            if (sensoryVector.length > 1 && sensoryVector[1] < 0.8) {
                double expectedValue = affordanceExpectations.getOrDefault("move_forward", 0.5);
                affordances.add(new Affordance("move_forward", expectedValue));
            }
            
            return affordances;
        }
        
        public void updateExpectations(SensoryInput sensoryInput, double reward) {
            // Update affordance expectations based on reward
        }
        
        public void reset() {
            affordanceExpectations.clear();
        }
    }
    
    /**
     * Grounded Symbol System class.
     */
    private static class GroundedSymbolSystem implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final Map<String, GroundedMeaning> symbols;
        
        public GroundedSymbolSystem() {
            this.symbols = new HashMap<>();
        }
        
        public void initialize() {
            // Initialize with basic symbols
        }
        
        public void groundFromExperience(SensoryInput sensoryInput, MotorCommand command) {
            // Ground symbols from sensorimotor patterns
            String symbol = command.actionType;
            
            GroundedMeaning meaning = symbols.computeIfAbsent(
                symbol, k -> new GroundedMeaning(symbol)
            );
            
            meaning.updateFromExperience(sensoryInput.getVector(), command.vector);
        }
        
        public void refineFromFeedback(SensoryInput sensoryInput, double reward) {
            // Refine symbol meanings based on feedback
        }
        
        public GroundedMeaning getMeaning(String symbol) {
            return symbols.get(symbol);
        }
        
        public boolean isGrounded(String symbol) {
            GroundedMeaning meaning = symbols.get(symbol);
            return meaning != null && meaning.isGrounded();
        }
        
        public int getGroundedSymbolCount() {
            return (int) symbols.values().stream().filter(GroundedMeaning::isGrounded).count();
        }
        
        public void reset() {
            symbols.clear();
        }
    }
    
    /**
     * Sensory Input class.
     */
    public static class SensoryInput implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final double[] vector;
        private final double[] proprioception;
        
        public SensoryInput(double[] vector, double[] proprioception) {
            this.vector = vector;
            this.proprioception = proprioception;
        }
        
        public double[] getVector() {
            return vector.clone();
        }
        
        public double[] getProprioception() {
            return proprioception.clone();
        }
    }
    
    /**
     * Motor Command class.
     */
    public static class MotorCommand implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final String actionType;
        public final double[] vector;
        
        public MotorCommand(String actionType, double[] vector) {
            this.actionType = actionType;
            this.vector = vector;
        }
    }
    
    /**
     * Affordance class.
     */
    private static class Affordance implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final String actionType;
        public final double expectedValue;
        
        public Affordance(String actionType, double expectedValue) {
            this.actionType = actionType;
            this.expectedValue = expectedValue;
        }
    }
    
    /**
     * Grounded Meaning class.
     */
    private static class GroundedMeaning implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final String symbol;
        private double[] sensoryPattern;
        private double[] motorPattern;
        private int experienceCount;
        
        public GroundedMeaning(String symbol) {
            this.symbol = symbol;
            this.experienceCount = 0;
        }
        
        public void updateFromExperience(double[] sensoryPattern, double[] motorPattern) {
            this.sensoryPattern = sensoryPattern.clone();
            this.motorPattern = motorPattern.clone();
            experienceCount++;
        }
        
        public boolean isGrounded() {
            return experienceCount > 5; // Threshold for grounding
        }
    }
    
    /**
     * Body State class.
     */
    public static class BodyState implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final double[] jointAngles;
        public final double[] jointVelocities;
        
        public BodyState(double[] jointAngles, double[] jointVelocities) {
            this.jointAngles = jointAngles;
            this.jointVelocities = jointVelocities;
        }
    }
    
    /**
     * Interaction Statistics class.
     */
    public static class InteractionStatistics implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public final long totalInteractions;
        public final long successfulActions;
        public final double successRate;
        public final double averageReward;
        public final int groundedSymbols;
        public final int learnedContingencies;
        
        public InteractionStatistics(long totalInteractions, long successfulActions,
                                   double successRate, double averageReward,
                                   int groundedSymbols, int learnedContingencies) {
            this.totalInteractions = totalInteractions;
            this.successfulActions = successfulActions;
            this.successRate = successRate;
            this.averageReward = averageReward;
            this.groundedSymbols = groundedSymbols;
            this.learnedContingencies = learnedContingencies;
        }
        
        @Override
        public String toString() {
            return String.format(
                "InteractionStats[interactions=%d, success=%.2f%%, reward=%.3f, symbols=%d, contingencies=%d]",
                totalInteractions, successRate * 100, averageReward, groundedSymbols, learnedContingencies
            );
        }
    }
}
