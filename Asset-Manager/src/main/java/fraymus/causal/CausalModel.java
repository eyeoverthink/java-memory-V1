package fraymus.causal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CausalModel - Structural Causal Model with DAG representation
 * 
 * Represents causal relationships between variables using a Directed Acyclic Graph (DAG).
 * Each node represents a variable, edges represent causal relationships.
 * 
 * Mathematical foundation:
 * - DAG G = (V, E) where V is set of variables, E is set of directed edges
 * - Causal structure: X → Y means X causes Y
 * - Intervention do(X=x): forces X to value x, breaks incoming edges
 * 
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class CausalModel {
    
    // DAG nodes (variables)
    private final Map<String, CausalNode> nodes;
    
    // DAG edges (causal relationships)
    private final Map<String, Set<String>> edges; // parent -> children
    
    // Intervention history
    private final Map<String, Object> interventions;
    
    /**
     * Causal node representing a variable in the causal graph
     */
    public static class CausalNode {
        private final String id;
        private final String variable;
        private Object value;
        private final Set<String> parents;
        private final Set<String> children;
        private double confidence;
        
        public CausalNode(String id, String variable) {
            this.id = id;
            this.variable = variable;
            this.value = null;
            this.parents = new HashSet<>();
            this.children = new HashSet<>();
            this.confidence = 1.0;
        }
        
        public String getId() { return id; }
        public String getVariable() { return variable; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public Set<String> getParents() { return new HashSet<>(parents); }
        public Set<String> getChildren() { return new HashSet<>(children); }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public void addParent(String parentId) { parents.add(parentId); }
        public void addChild(String childId) { children.add(childId); }
    }
    
    public CausalModel() {
        this.nodes = new ConcurrentHashMap<>();
        this.edges = new ConcurrentHashMap<>();
        this.interventions = new ConcurrentHashMap<>();
    }
    
    /**
     * Add a node to the causal graph
     */
    public CausalNode addNode(String id, String variable) {
        CausalNode node = new CausalNode(id, variable);
        nodes.put(id, node);
        edges.put(id, new HashSet<>());
        return node;
    }
    
    /**
     * Add a causal edge: parent causes child
     */
    public void addEdge(String parentId, String childId) {
        if (!nodes.containsKey(parentId) || !nodes.containsKey(childId)) {
            throw new IllegalArgumentException("Parent or child node does not exist");
        }
        
        // Check for cycle (would violate DAG property)
        if (wouldCreateCycle(parentId, childId)) {
            throw new IllegalArgumentException("Adding edge would create a cycle");
        }
        
        edges.get(parentId).add(childId);
        nodes.get(parentId).addChild(childId);
        nodes.get(childId).addParent(parentId);
    }
    
    /**
     * Check if adding edge would create a cycle
     */
    private boolean wouldCreateCycle(String parentId, String childId) {
        // Check if there's already a path from child to parent
        return hasPath(childId, parentId);
    }
    
    /**
     * Check if there's a path from start to end
     */
    private boolean hasPath(String start, String end) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        // Add neighbors of start to queue (not start itself)
        for (String neighbor : edges.getOrDefault(start, new HashSet<>())) {
            queue.add(neighbor);
            visited.add(neighbor);
        }
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                return true;
            }
            
            for (String neighbor : edges.getOrDefault(current, new HashSet<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return false;
    }
    
    /**
     * Perform do-calculus intervention: do(X = value)
     * This breaks all incoming edges to X and sets its value
     */
    public void doIntervention(String nodeId, Object value) {
        if (!nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException("Node does not exist");
        }
        
        CausalNode node = nodes.get(nodeId);
        
        // Record intervention
        interventions.put(nodeId, value);
        
        // Break incoming edges (temporarily)
        Set<String> parents = new HashSet<>(node.getParents());
        for (String parentId : parents) {
            edges.get(parentId).remove(nodeId);
            node.parents.remove(parentId);
        }
        
        // Set the value
        node.setValue(value);
    }
    
    /**
     * Reset an intervention (restore original edges)
     */
    public void resetIntervention(String nodeId) {
        if (!interventions.containsKey(nodeId)) {
            return; // No intervention to reset
        }
        
        interventions.remove(nodeId);
        
        // Note: In a full implementation, we would need to restore the original
        // parent relationships. For simplicity, we just clear the value.
        CausalNode node = nodes.get(nodeId);
        node.setValue(null);
    }
    
    /**
     * Get all nodes
     */
    public Map<String, CausalNode> getNodes() {
        return new HashMap<>(nodes);
    }
    
    /**
     * Get a specific node
     */
    public CausalNode getNode(String id) {
        return nodes.get(id);
    }
    
    /**
     * Get children of a node
     */
    public Set<String> getChildren(String nodeId) {
        return new HashSet<>(edges.getOrDefault(nodeId, new HashSet<>()));
    }
    
    /**
     * Get parents of a node
     */
    public Set<String> getParents(String nodeId) {
        CausalNode node = nodes.get(nodeId);
        return node != null ? new HashSet<>(node.getParents()) : new HashSet<>();
    }
    
    /**
     * Check if the graph is a valid DAG
     */
    public boolean isValidDAG() {
        // Check for cycles
        for (String nodeId : nodes.keySet()) {
            if (hasPath(nodeId, nodeId)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get causal path from start to end
     */
    public List<String> getCausalPath(String start, String end) {
        Map<String, String> parentMap = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.add(start);
        visited.add(start);
        parentMap.put(start, null);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(end)) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                String node = end;
                while (node != null) {
                    path.add(0, node);
                    node = parentMap.get(node);
                }
                return path;
            }
            
            for (String child : edges.getOrDefault(current, new HashSet<>())) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    parentMap.put(child, current);
                    queue.add(child);
                }
            }
        }
        
        return Collections.emptyList(); // No path found
    }
    
    /**
     * Get active interventions
     */
    public Map<String, Object> getInterventions() {
        return new HashMap<>(interventions);
    }
    
    /**
     * Clear all nodes and edges
     */
    public void clear() {
        nodes.clear();
        edges.clear();
        interventions.clear();
    }
    
    /**
     * Get statistics
     */
    public String getStatistics() {
        int edgeCount = edges.values().stream().mapToInt(Set::size).sum();
        return String.format(
            "CausalModel Stats:\n" +
            "  Nodes: %d\n" +
            "  Edges: %d\n" +
            "  Interventions: %d\n" +
            "  Valid DAG: %s",
            nodes.size(),
            edgeCount,
            interventions.size(),
            isValidDAG()
        );
    }
}
