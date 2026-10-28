package fraymus.causal;

import java.util.*;

/**
 * DAGVerifier - Verifies causal relationships and detects violations
 * 
 * Implements verification pipeline for causal reasoning:
 * - Checks for backdoor paths (confounders)
 * - Verifies d-separation
 * - Detects spurious correlations
 * - Validates causal assumptions
 * 
 * Mathematical foundation:
 * - Backdoor criterion: P(Y|do(X)) = Σ_z P(Y|X,z)P(z)
 * - d-separation: X and Y are d-separated given Z if all paths blocked
 * - Spurious correlation: X and Y correlated but no causal path
 * 
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class DAGVerifier {
    
    private final CausalModel model;
    
    public DAGVerifier(CausalModel model) {
        this.model = model;
    }
    
    /**
     * Causal violation - represents a detected violation of causal assumptions
     */
    public static class CausalViolation {
        private final String type;
        private final String description;
        private final List<String> involvedNodes;
        private final double severity;
        
        public CausalViolation(String type, String description, List<String> involvedNodes, double severity) {
            this.type = type;
            this.description = description;
            this.involvedNodes = involvedNodes;
            this.severity = severity;
        }
        
        public String getType() { return type; }
        public String getDescription() { return description; }
        public List<String> getInvolvedNodes() { return involvedNodes; }
        public double getSeverity() { return severity; }
    }
    
    /**
     * Verify if there's a valid causal path from cause to effect
     */
    public boolean verifyCausalPath(String cause, String effect) {
        List<String> path = model.getCausalPath(cause, effect);
        return !path.isEmpty();
    }
    
    /**
     * Find all backdoor paths between X and Y
     * Backdoor path: path from X to Y that starts with an arrow into X
     */
    public List<List<String>> findBackdoorPaths(String x, String y) {
        List<List<String>> backdoorPaths = new ArrayList<>();
        
        // Get all parents of X
        Set<String> parents = model.getParents(x);
        
        // For each parent, find paths to Y that don't go through X
        for (String parent : parents) {
            List<String> path = findPathAvoiding(parent, y, x);
            if (!path.isEmpty()) {
                path.add(0, x); // Add X at the beginning
                backdoorPaths.add(path);
            }
        }
        
        return backdoorPaths;
    }
    
    /**
     * Find path from start to end that avoids a specific node
     */
    private List<String> findPathAvoiding(String start, String end, String avoid) {
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
            
            for (String child : model.getChildren(current)) {
                if (!visited.contains(child) && !child.equals(avoid)) {
                    visited.add(child);
                    parentMap.put(child, current);
                    queue.add(child);
                }
            }
        }
        
        return Collections.emptyList();
    }
    
    /**
     * Check if X and Y are d-separated given conditioning set Z
     * d-separation: all paths between X and Y are blocked
     */
    public boolean isDSeparated(String x, String y, Set<String> z) {
        // Find all paths between X and Y
        List<List<String>> allPaths = findAllPaths(x, y);
        
        // Check if all paths are blocked
        for (List<String> path : allPaths) {
            if (!isPathBlocked(path, z)) {
                return false; // At least one unblocked path
            }
        }
        
        return true; // All paths blocked
    }
    
    /**
     * Find all paths between X and Y (undirected)
     */
    private List<List<String>> findAllPaths(String x, String y) {
        List<List<String>> paths = new ArrayList<>();
        findAllPathsHelper(x, y, new ArrayList<>(), new HashSet<>(), paths);
        return paths;
    }
    
    private void findAllPathsHelper(String current, String target, List<String> currentPath, 
                                    Set<String> visited, List<List<String>> allPaths) {
        currentPath.add(current);
        visited.add(current);
        
        if (current.equals(target)) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            // Get neighbors (both parents and children)
            Set<String> neighbors = new HashSet<>();
            neighbors.addAll(model.getChildren(current));
            neighbors.addAll(model.getParents(current));
            
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    findAllPathsHelper(neighbor, target, currentPath, visited, allPaths);
                }
            }
        }
        
        currentPath.remove(currentPath.size() - 1);
        visited.remove(current);
    }
    
    /**
     * Check if a path is blocked by conditioning set Z
     * A path is blocked if it contains a collider not in Z, or a non-collider in Z
     */
    private boolean isPathBlocked(List<String> path, Set<String> z) {
        for (int i = 1; i < path.size() - 1; i++) {
            String node = path.get(i);
            String prev = path.get(i - 1);
            String next = path.get(i + 1);
            
            boolean isCollider = model.getChildren(node).contains(prev) && 
                                model.getChildren(node).contains(next);
            
            if (isCollider) {
                // Collider: blocks unless in conditioning set or has descendant in Z
                if (!z.contains(node) && !hasDescendantInZ(node, z)) {
                    return true;
                }
            } else {
                // Non-collider: blocks if in conditioning set
                if (z.contains(node)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if node has a descendant in conditioning set Z
     */
    private boolean hasDescendantInZ(String node, Set<String> z) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        queue.add(node);
        visited.add(node);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            for (String child : model.getChildren(current)) {
                if (z.contains(child)) {
                    return true;
                }
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        
        return false;
    }
    
    /**
     * Detect spurious correlations
     * Spurious: X and Y correlated but no causal path in either direction
     */
    public List<CausalViolation> detectSpuriousCorrelations() {
        List<CausalViolation> violations = new ArrayList<>();
        
        for (String x : model.getNodes().keySet()) {
            for (String y : model.getNodes().keySet()) {
                if (x.equals(y)) continue;
                
                // Check if there's no causal path in either direction
                boolean xToY = model.getCausalPath(x, y).isEmpty();
                boolean yToX = model.getCausalPath(y, x).isEmpty();
                
                if (xToY && yToX) {
                    // But there might be a common cause (backdoor path)
                    List<List<String>> backdoorPaths = findBackdoorPaths(x, y);
                    List<List<String>> reverseBackdoorPaths = findBackdoorPaths(y, x);
                    
                    if (!backdoorPaths.isEmpty() || !reverseBackdoorPaths.isEmpty()) {
                        // Spurious correlation detected
                        List<String> involved = Arrays.asList(x, y);
                        violations.add(new CausalViolation(
                            "SPURIOUS_CORRELATION",
                            String.format("Variables %s and %s are correlated but have no causal path (likely common cause)", x, y),
                            involved,
                            0.8
                        ));
                    }
                }
            }
        }
        
        return violations;
    }
    
    /**
     * Verify backdoor criterion for estimating P(Y|do(X))
     * Backdoor criterion satisfied if all backdoor paths are blocked by conditioning set Z
     */
    public boolean verifyBackdoorCriterion(String x, String y, Set<String> z) {
        List<List<String>> backdoorPaths = findBackdoorPaths(x, y);
        
        for (List<String> path : backdoorPaths) {
            // Check if path is blocked by Z
            Set<String> pathSet = new HashSet<>(path);
            pathSet.retainAll(z);
            
            if (pathSet.isEmpty()) {
                return false; // Unblocked backdoor path
            }
        }
        
        return true;
    }
    
    /**
     * Get all causal violations in the model
     */
    public List<CausalViolation> getAllViolations() {
        List<CausalViolation> violations = new ArrayList<>();
        
        // Add spurious correlations
        violations.addAll(detectSpuriousCorrelations());
        
        // Check for cycles (should not happen in valid DAG)
        if (!model.isValidDAG()) {
            violations.add(new CausalViolation(
                "CYCLE_DETECTED",
                "Causal graph contains cycles (violates DAG property)",
                Collections.emptyList(),
                1.0
            ));
        }
        
        return violations;
    }
    
    /**
     * Compute causal energy penalty based on violations
     * E_violation = λ Σ_i severity_i
     */
    public double computeViolationEnergy(double lambda) {
        List<CausalViolation> violations = getAllViolations();
        double totalSeverity = violations.stream()
            .mapToDouble(CausalViolation::getSeverity)
            .sum();
        return lambda * totalSeverity;
    }
}
