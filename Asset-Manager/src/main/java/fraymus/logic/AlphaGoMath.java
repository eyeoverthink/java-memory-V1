package fraymus.logic;

import java.util.*;

/**
 * ALPHA GO MATH: Monte Carlo Tree Search (MCTS) + PUCT
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Abstract the way to win, not the moves."
 * 
 * This is the core mathematical framework that enabled AlphaGo to defeat
 * the world champion at Go. Instead of predicting the next move like an LLM,
 * it evaluates future states and works backward using the PUCT formula.
 * 
 * The PUCT (Polynomial Upper Confidence Trees) formula mathematically balances:
 * - EXPLOITATION: Playing moves it knows are good (Q-value)
 * - EXPLORATION: Trying weird, novel moves (U-value)
 * 
 * Core Equation:
 *   a_t = argmax_a ( Q(s_t, a) + U(s_t, a) )
 * 
 * Where:
 *   Q(s_t, a) = Expected reward (the "Value")
 *   U(s_t, a) = c_puct * P(s_t, a) * sqrt(Σ_b N(s_t, b)) / (1 + N(s_t, a))
 * 
 *   P(s, a) = Prior Probability (what the LLM thinks is a good idea initially)
 *   N(s, a) = Visit Count (how many times we've simulated this specific path)
 *   c_puct = Exploration constant (typically 1.0 - 5.0)
 * 
 * Pre-verse Engineering Application:
 * Fraynix doesn't just generate a script and execute it. It generates 50 scripts,
 * runs them in a simulated tree, uses this math to find the sequence with the
 * highest Q value, and then compiles the intent.
 */
public class AlphaGoMath {

    // PUCT exploration constant
    private static final double C_PUCT = 2.0;
    
    // Number of simulations per decision
    private static final int NUM_SIMULATIONS = 50;
    
    // Random number generator for tie-breaking
    private final Random rng;
    
    /**
     * Represents a node in the MCTS tree
     */
    public static class MCTSNode {
        private final CISAState state;
        private final MCTSNode parent;
        private final Map<Integer, MCTSNode> children;
        
        // Visit statistics
        private int visitCount;
        private double totalValue;
        
        // Prior probability from LLM/heuristic
        private final double priorProb;
        
        public MCTSNode(CISAState state, MCTSNode parent, double priorProb) {
            this.state = state;
            this.parent = parent;
            this.priorProb = priorProb;
            this.children = new HashMap<>();
            this.visitCount = 0;
            this.totalValue = 0.0;
        }
        
        /**
         * Calculate Q-value (expected reward)
         */
        public double getQ() {
            return visitCount > 0 ? totalValue / visitCount : 0.0;
        }
        
        /**
         * Calculate U-value (exploration bonus) using PUCT formula
         * U(s, a) = c_puct * P(s, a) * sqrt(Σ_b N(s, b)) / (1 + N(s, a))
         */
        public double getU(double parentVisitCount) {
            return C_PUCT * priorProb * 
                   Math.sqrt(parentVisitCount) / (1.0 + visitCount);
        }
        
        /**
         * Calculate PUCT score: Q + U
         */
        public double getPUCTScore() {
            int parentVisits = (parent != null) ? parent.visitCount : 0;
            return getQ() + getU(parentVisits);
        }
        
        /**
         * Select child with highest PUCT score
         */
        public MCTSNode selectChild() {
            MCTSNode bestChild = null;
            double bestScore = Double.NEGATIVE_INFINITY;
            
            for (MCTSNode child : children.values()) {
                double score = child.getPUCTScore();
                if (score > bestScore) {
                    bestScore = score;
                    bestChild = child;
                }
            }
            
            return bestChild;
        }
        
        /**
         * Update node statistics after simulation
         */
        public void update(double value) {
            visitCount++;
            totalValue += value;
        }
        
        /**
         * Check if node is fully expanded
         */
        public boolean isFullyExpanded() {
            return !state.getLegalActions().isEmpty() && 
                   children.size() >= state.getLegalActions().size();
        }
        
        /**
         * Check if node is terminal (game over)
         */
        public boolean isTerminal() {
            return state.isTerminal();
        }
        
        public CISAState getState() { return state; }
        public int getVisitCount() { return visitCount; }
        public Map<Integer, MCTSNode> getChildren() { return children; }
    }
    
    /**
     * Represents the C-ISA program state
     */
    public static class CISAState {
        private final CognitiveISA.Program program;
        private final int programCounter;
        private final Map<String, Object> memory;
        private boolean terminal;
        
        public CISAState(CognitiveISA.Program program) {
            this(program, 0, new HashMap<>(), false);
        }
        
        public CISAState(CognitiveISA.Program program, int pc, 
                         Map<String, Object> memory, boolean terminal) {
            this.program = program;
            this.programCounter = pc;
            this.memory = new HashMap<>(memory);
            this.terminal = terminal;
        }
        
        /**
         * Get legal actions (available C-ISA opcodes)
         */
        public List<Integer> getLegalActions() {
            if (terminal) return Collections.emptyList();
            
            // Return available C-ISA opcodes
            List<Integer> actions = new ArrayList<>();
            actions.add(CognitiveISA.PUSH);
            actions.add(CognitiveISA.POP);
            actions.add(CognitiveISA.STORE);
            actions.add(CognitiveISA.LOAD);
            actions.add(CognitiveISA.CMP);
            actions.add(CognitiveISA.BRANCH);
            actions.add(CognitiveISA.BRANCH_IF_TRUE);
            actions.add(CognitiveISA.BRANCH_IF_FALSE);
            actions.add(CognitiveISA.BRANCH_IF_EQ);
            actions.add(CognitiveISA.BRANCH_IF_NEQ);
            actions.add(CognitiveISA.BRANCH_IF_GT);
            actions.add(CognitiveISA.BRANCH_IF_LT);
            
            return actions;
        }
        
        /**
         * Apply action to get next state
         */
        public CISAState applyAction(int action) {
            // Create new state with applied action
            CISAState newState = new CISAState(program, programCounter + 1, memory, false);
            
            // Check if program is complete
            if (programCounter >= program.size() - 1) {
                newState.terminal = true;
            }
            
            return newState;
        }
        
        /**
         * Check if state is terminal
         */
        public boolean isTerminal() {
            return terminal || programCounter >= program.size();
        }
        
        /**
         * Get heuristic prior probability for an action
         * This would come from LLM in production
         */
        public double getPriorProbability(int action) {
            // Simple heuristic: equal probability for all actions
            // In production, this would use LLM to predict best next instruction
            return 1.0 / getLegalActions().size();
        }
        
        public CognitiveISA.Program getProgram() { return program; }
        public int getProgramCounter() { return programCounter; }
        public Map<String, Object> getMemory() { return memory; }
    }
    
    public AlphaGoMath() {
        this.rng = new Random();
    }
    
    /**
     * Select the best action using MCTS + PUCT
     * 
     * @param initialState The starting C-ISA state
     * @param priorFunction Function to get prior probabilities (LLM or heuristic)
     * @param evaluationFunction Function to evaluate terminal states
     * @return The best action (C-ISA opcode)
     */
    public int selectBestAction(CISAState initialState, 
                                java.util.function.Function<Integer, Double> priorFunction,
                                java.util.function.Function<CISAState, Double> evaluationFunction) {
        
        // Create root node
        MCTSNode root = new MCTSNode(initialState, null, 1.0);
        
        // Run MCTS simulations
        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            // 1. Selection: traverse tree to find leaf node
            MCTSNode node = select(root);
            
            // 2. Expansion: add child nodes if not terminal
            if (!node.isTerminal() && !node.isFullyExpanded()) {
                node = expand(node, priorFunction);
            }
            
            // 3. Simulation: rollout from leaf to terminal
            double value = simulate(node.getState(), evaluationFunction);
            
            // 4. Backpropagation: update statistics up the tree
            backpropagate(node, value);
        }
        
        // Select action with highest visit count (most promising)
        MCTSNode bestChild = root.getChildren().values().stream()
            .max(Comparator.comparingInt(MCTSNode::getVisitCount))
            .orElse(null);
        
        if (bestChild == null) {
            // Fallback: return first legal action
            return initialState.getLegalActions().get(0);
        }
        
        // Return the action that led to best child
        for (Map.Entry<Integer, MCTSNode> entry : root.getChildren().entrySet()) {
            if (entry.getValue() == bestChild) {
                return entry.getKey();
            }
        }
        
        return initialState.getLegalActions().get(0);
    }
    
    /**
     * Selection phase: traverse tree using PUCT to find best leaf node
     */
    private MCTSNode select(MCTSNode node) {
        while (!node.isTerminal() && node.isFullyExpanded()) {
            node = node.selectChild();
        }
        return node;
    }
    
    /**
     * Expansion phase: add a new child node
     */
    private MCTSNode expand(MCTSNode node, 
                            java.util.function.Function<Integer, Double> priorFunction) {
        CISAState state = node.getState();
        List<Integer> legalActions = state.getLegalActions();
        
        // Find unexpanded action
        for (int action : legalActions) {
            if (!node.getChildren().containsKey(action)) {
                // Get prior probability
                double prior = priorFunction.apply(action);
                
                // Create new state
                CISAState newState = state.applyAction(action);
                
                // Create child node
                MCTSNode child = new MCTSNode(newState, node, prior);
                node.getChildren().put(action, child);
                
                return child;
            }
        }
        
        return node;
    }
    
    /**
     * Simulation phase: rollout from node to terminal state
     */
    private double simulate(CISAState state, 
                           java.util.function.Function<CISAState, Double> evaluationFunction) {
        CISAState currentState = state;
        
        // Rollout until terminal
        while (!currentState.isTerminal()) {
            List<Integer> legalActions = currentState.getLegalActions();
            if (legalActions.isEmpty()) break;
            
            // Random action for simulation (could be improved with heuristic)
            int action = legalActions.get(rng.nextInt(legalActions.size()));
            currentState = currentState.applyAction(action);
        }
        
        // Evaluate terminal state
        return evaluationFunction.apply(currentState);
    }
    
    /**
     * Backpropagation phase: update statistics up the tree
     */
    private void backpropagate(MCTSNode node, double value) {
        while (node != null) {
            node.update(value);
            node = node.parent;
        }
    }
    
    /**
     * Get statistics about the search tree
     */
    public String getTreeStats(MCTSNode root) {
        int totalNodes = countNodes(root);
        int maxDepth = getMaxDepth(root);
        double avgVisits = getAverageVisits(root);
        
        return String.format(
            "MCTS[Nodes=%d, MaxDepth=%d, AvgVisits=%.2f, Simulations=%d]",
            totalNodes, maxDepth, avgVisits, NUM_SIMULATIONS
        );
    }
    
    private int countNodes(MCTSNode node) {
        if (node == null) return 0;
        int count = 1;
        for (MCTSNode child : node.getChildren().values()) {
            count += countNodes(child);
        }
        return count;
    }
    
    private int getMaxDepth(MCTSNode node) {
        if (node == null || node.getChildren().isEmpty()) return 0;
        int maxDepth = 0;
        for (MCTSNode child : node.getChildren().values()) {
            maxDepth = Math.max(maxDepth, 1 + getMaxDepth(child));
        }
        return maxDepth;
    }
    
    private double getAverageVisits(MCTSNode node) {
        if (node == null) return 0;
        double total = node.getVisitCount();
        int count = 1;
        for (MCTSNode child : node.getChildren().values()) {
            total += child.getVisitCount();
            count++;
        }
        return total / count;
    }
}
