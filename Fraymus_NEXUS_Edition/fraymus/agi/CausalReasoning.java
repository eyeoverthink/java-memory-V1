package fraymus.agi;

import fraymus.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AGI-5: CAUSAL REASONING ENGINE
 * 
 * Understand cause-effect relationships, not just correlations.
 * Enables "why" understanding and counterfactual thinking.
 * 
 * Key features:
 * - Causal graph construction
 * - Intervention analysis ("what if")
 * - Counterfactual reasoning
 * - Causal strength measurement
 * - Confounding variable detection
 */
public class CausalReasoning {

    private static final double PHI = 1.618033988749895;

    // Causal graph: nodes are variables, edges are causal relationships
    private final Map<String, CausalNode> nodes = new ConcurrentHashMap<>();
    private final List<CausalEdge> edges = Collections.synchronizedList(new ArrayList<>());
    
    // Observation history for learning causal relationships
    private final List<Observation> observations = Collections.synchronizedList(new ArrayList<>());
    
    // Intervention results
    private final Map<String, InterventionResult> interventionHistory = new ConcurrentHashMap<>();

    // Metrics
    private int causalEdgesDiscovered = 0;
    private int interventionsRun = 0;
    private int counterfactualsEvaluated = 0;
    private double avgCausalStrength = 0;

    public CausalReasoning() {}

    /**
     * Register a variable in the causal graph
     */
    public void registerVariable(String name, VariableType type) {
        nodes.computeIfAbsent(name, k -> new CausalNode(name, type));
    }

    /**
     * Record an observation of variable states
     */
    public void observe(Map<String, Double> variableStates) {
        Observation obs = new Observation(variableStates);
        observations.add(obs);
        
        // Keep observations bounded
        while (observations.size() > 1000) {
            observations.remove(0);
        }

        // Periodically learn causal relationships
        if (observations.size() % 50 == 0) {
            learnCausalRelationships();
        }
    }

    /**
     * Learn causal relationships from observations
     */
    private void learnCausalRelationships() {
        if (observations.size() < 20) return;

        List<String> variables = new ArrayList<>(nodes.keySet());
        
        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                if (i == j) continue;
                
                String cause = variables.get(i);
                String effect = variables.get(j);
                
                double causalStrength = estimateCausalStrength(cause, effect);
                
                if (causalStrength > 0.3) {
                    addOrUpdateEdge(cause, effect, causalStrength);
                }
            }
        }
    }

    /**
     * Estimate causal strength between two variables
     * Uses temporal precedence and correlation
     */
    private double estimateCausalStrength(String cause, String effect) {
        if (observations.size() < 10) return 0;

        double[] causeValues = new double[observations.size() - 1];
        double[] effectValues = new double[observations.size() - 1];
        int validPairs = 0;

        synchronized (observations) {
            for (int i = 0; i < observations.size() - 1; i++) {
                Double causeVal = observations.get(i).states.get(cause);
                Double effectVal = observations.get(i + 1).states.get(effect);
                
                if (causeVal != null && effectVal != null) {
                    causeValues[validPairs] = causeVal;
                    effectValues[validPairs] = effectVal;
                    validPairs++;
                }
            }
        }

        if (validPairs < 5) return 0;

        // Calculate correlation with temporal lag (cause precedes effect)
        return Math.abs(correlate(
                Arrays.copyOf(causeValues, validPairs),
                Arrays.copyOf(effectValues, validPairs)));
    }

    /**
     * Add or update a causal edge
     */
    private void addOrUpdateEdge(String cause, String effect, double strength) {
        // Check for existing edge
        for (CausalEdge edge : edges) {
            if (edge.cause.equals(cause) && edge.effect.equals(effect)) {
                edge.strength = edge.strength * 0.8 + strength * 0.2;
                edge.observations++;
                return;
            }
        }

        // New edge
        CausalEdge edge = new CausalEdge(cause, effect, strength);
        edges.add(edge);
        causalEdgesDiscovered++;
        
        // Update node connections
        CausalNode causeNode = nodes.get(cause);
        CausalNode effectNode = nodes.get(effect);
        if (causeNode != null) causeNode.children.add(effect);
        if (effectNode != null) effectNode.parents.add(cause);

        avgCausalStrength = avgCausalStrength * 0.9 + strength * 0.1;
    }

    /**
     * Simulate an intervention: "What happens if we set X to value?"
     */
    public InterventionResult intervene(String variable, double value) {
        interventionsRun++;
        
        InterventionResult result = new InterventionResult(variable, value);
        
        // Find all downstream effects
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(variable);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);
            
            CausalNode node = nodes.get(current);
            if (node == null) continue;
            
            for (String child : node.children) {
                // Calculate expected effect
                CausalEdge edge = findEdge(current, child);
                if (edge != null) {
                    double expectedChange = value * edge.strength;
                    result.predictedEffects.put(child, expectedChange);
                    queue.add(child);
                }
            }
        }

        interventionHistory.put(variable + "_" + System.currentTimeMillis(), result);
        return result;
    }

    /**
     * Counterfactual reasoning: "What would have happened if X had been different?"
     */
    public CounterfactualResult counterfactual(String variable, double actualValue, 
                                                double hypotheticalValue, 
                                                Map<String, Double> actualOutcomes) {
        counterfactualsEvaluated++;
        
        CounterfactualResult result = new CounterfactualResult(variable, actualValue, hypotheticalValue);
        
        double delta = hypotheticalValue - actualValue;
        
        // Trace effects through causal graph
        CausalNode node = nodes.get(variable);
        if (node == null) return result;
        
        for (String child : node.children) {
            CausalEdge edge = findEdge(variable, child);
            if (edge != null) {
                double actualOutcome = actualOutcomes.getOrDefault(child, 0.0);
                double hypotheticalOutcome = actualOutcome + (delta * edge.strength);
                result.hypotheticalOutcomes.put(child, hypotheticalOutcome);
                result.differences.put(child, hypotheticalOutcome - actualOutcome);
            }
        }

        return result;
    }

    /**
     * Explain why an effect occurred
     */
    public List<CausalExplanation> explainEffect(String effect) {
        List<CausalExplanation> explanations = new ArrayList<>();
        
        CausalNode node = nodes.get(effect);
        if (node == null) return explanations;
        
        for (String parent : node.parents) {
            CausalEdge edge = findEdge(parent, effect);
            if (edge != null) {
                CausalExplanation exp = new CausalExplanation();
                exp.cause = parent;
                exp.effect = effect;
                exp.strength = edge.strength;
                exp.confidence = edge.observations / 100.0;
                exp.explanation = String.format("%s causes %s with strength %.2f", 
                        parent, effect, edge.strength);
                explanations.add(exp);
            }
        }

        // Sort by strength
        explanations.sort((a, b) -> Double.compare(b.strength, a.strength));
        return explanations;
    }

    /**
     * Find confounding variables
     */
    public List<String> findConfounders(String cause, String effect) {
        List<String> confounders = new ArrayList<>();
        
        CausalNode causeNode = nodes.get(cause);
        CausalNode effectNode = nodes.get(effect);
        
        if (causeNode == null || effectNode == null) return confounders;
        
        // Confounder is a common parent of both cause and effect
        Set<String> causeParents = new HashSet<>(causeNode.parents);
        Set<String> effectParents = new HashSet<>(effectNode.parents);
        
        causeParents.retainAll(effectParents);
        confounders.addAll(causeParents);
        
        return confounders;
    }

    /**
     * Get causal path between two variables
     */
    public List<String> getCausalPath(String from, String to) {
        List<String> path = new ArrayList<>();
        
        // BFS to find path
        Map<String, String> parent = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(from);
        parent.put(from, null);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(to)) {
                // Reconstruct path
                String node = to;
                while (node != null) {
                    path.add(0, node);
                    node = parent.get(node);
                }
                return path;
            }
            
            CausalNode n = nodes.get(current);
            if (n != null) {
                for (String child : n.children) {
                    if (!parent.containsKey(child)) {
                        parent.put(child, current);
                        queue.add(child);
                    }
                }
            }
        }
        
        return path; // Empty if no path found
    }

    private CausalEdge findEdge(String cause, String effect) {
        for (CausalEdge edge : edges) {
            if (edge.cause.equals(cause) && edge.effect.equals(effect)) {
                return edge;
            }
        }
        return null;
    }

    private double correlate(double[] a, double[] b) {
        int n = Math.min(a.length, b.length);
        if (n < 2) return 0;

        double sumA = 0, sumB = 0, sumAB = 0, sumA2 = 0, sumB2 = 0;
        for (int i = 0; i < n; i++) {
            sumA += a[i];
            sumB += b[i];
            sumAB += a[i] * b[i];
            sumA2 += a[i] * a[i];
            sumB2 += b[i] * b[i];
        }

        double num = n * sumAB - sumA * sumB;
        double den = Math.sqrt((n * sumA2 - sumA * sumA) * (n * sumB2 - sumB * sumB));
        return den > 0 ? num / den : 0;
    }

    // Getters
    public int getNodeCount() { return nodes.size(); }
    public int getEdgeCount() { return edges.size(); }
    public int getObservationCount() { return observations.size(); }
    public int getCausalEdgesDiscovered() { return causalEdgesDiscovered; }
    public int getInterventionsRun() { return interventionsRun; }
    public double getAvgCausalStrength() { return avgCausalStrength; }

    public void printStats() {
        CommandTerminal.printHighlight("=== CAUSAL REASONING ENGINE ===");
        CommandTerminal.print(String.format("  Variables: %d", nodes.size()));
        CommandTerminal.print(String.format("  Causal Edges: %d", edges.size()));
        CommandTerminal.print(String.format("  Observations: %d", observations.size()));
        CommandTerminal.print(String.format("  Avg Causal Strength: %.4f", avgCausalStrength));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Analysis:");
        CommandTerminal.print(String.format("  Interventions Run: %d", interventionsRun));
        CommandTerminal.print(String.format("  Counterfactuals Evaluated: %d", counterfactualsEvaluated));
        
        if (!edges.isEmpty()) {
            CommandTerminal.print("");
            CommandTerminal.printInfo("Strongest Causal Relations:");
            List<CausalEdge> sorted = new ArrayList<>(edges);
            sorted.sort((a, b) -> Double.compare(b.strength, a.strength));
            for (int i = 0; i < Math.min(5, sorted.size()); i++) {
                CausalEdge e = sorted.get(i);
                CommandTerminal.print(String.format("  %s → %s (%.3f)", 
                        e.cause, e.effect, e.strength));
            }
        }
    }

    public enum VariableType {
        CONTINUOUS, DISCRETE, BINARY
    }

    public static class CausalNode {
        public final String name;
        public final VariableType type;
        public final Set<String> parents = new HashSet<>();
        public final Set<String> children = new HashSet<>();

        CausalNode(String name, VariableType type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class CausalEdge {
        public final String cause;
        public final String effect;
        public double strength;
        public int observations = 1;

        CausalEdge(String cause, String effect, double strength) {
            this.cause = cause;
            this.effect = effect;
            this.strength = strength;
        }
    }

    private static class Observation {
        final Map<String, Double> states;
        final long timestamp;

        Observation(Map<String, Double> states) {
            this.states = new HashMap<>(states);
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static class InterventionResult {
        public final String variable;
        public final double setValue;
        public final Map<String, Double> predictedEffects = new HashMap<>();

        InterventionResult(String variable, double value) {
            this.variable = variable;
            this.setValue = value;
        }
    }

    public static class CounterfactualResult {
        public final String variable;
        public final double actualValue;
        public final double hypotheticalValue;
        public final Map<String, Double> hypotheticalOutcomes = new HashMap<>();
        public final Map<String, Double> differences = new HashMap<>();

        CounterfactualResult(String variable, double actual, double hypothetical) {
            this.variable = variable;
            this.actualValue = actual;
            this.hypotheticalValue = hypothetical;
        }
    }

    public static class CausalExplanation {
        public String cause;
        public String effect;
        public double strength;
        public double confidence;
        public String explanation;
    }
}
