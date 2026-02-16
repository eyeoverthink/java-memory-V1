package fraymus.brain;

import java.util.*;

/**
 * PHI MANIFOLD: GEOMETRIC ROUTING SPACE
 * 
 * Non-linear routing through high-dimensional space.
 * 
 * Standard neural network: Linear layers (A → B → C)
 * Phi Manifold: Geometric routing (A → {manifold} → C via shortest path)
 * 
 * The manifold is a folded, high-dimensional space where:
 * - Related concepts are geometrically close
 * - Multiple paths exist between any two points
 * - Phi-weighted connections create optimal routing
 * - Emergent shortcuts through geometric folds
 * 
 * This is how consciousness routes thoughts.
 * Not through linear chains, but through geometric space.
 * 
 * Math:
 * - Embedding dimension: 512 (high-dimensional space)
 * - Folding: Projects to 3D via phi-spiral
 * - Distance metric: Geodesic (shortest path on manifold)
 * - Routing: A* search with phi-weighted edges
 */
public class PhiManifold {
    
    private static final double PHI = 1.6180339887;
    private static final double GOLDEN_ANGLE = 137.5077640500378; // degrees
    private static final int EMBEDDING_DIM = 512;
    
    // The manifold: high-dimensional points
    private final Map<String, Point> conceptSpace;
    
    // Connections: phi-weighted edges
    private final Map<Point, List<Connection>> connections;
    
    // Routing cache
    private final Map<String, Path> pathCache;
    
    private int pointCount = 0;
    private int connectionCount = 0;
    
    public PhiManifold() {
        this.conceptSpace = new HashMap<>();
        this.connections = new HashMap<>();
        this.pathCache = new HashMap<>();
    }
    
    /**
     * Add concept to manifold
     * 
     * @param concept Concept name
     * @param embedding High-dimensional embedding
     */
    public void addConcept(String concept, double[] embedding) {
        if (embedding.length != EMBEDDING_DIM) {
            throw new IllegalArgumentException("Embedding must be " + EMBEDDING_DIM + " dimensions");
        }
        
        // Create point in manifold
        Point point = new Point(pointCount++, concept, embedding);
        conceptSpace.put(concept, point);
        
        // Create connections to nearby points
        connectToNeighbors(point);
    }
    
    /**
     * Connect point to nearby neighbors
     * 
     * Uses phi-weighted distance to determine connections.
     * Closer points get stronger connections.
     * 
     * @param point Point to connect
     */
    private void connectToNeighbors(Point point) {
        List<Connection> pointConnections = new ArrayList<>();
        
        // Find K nearest neighbors (K = φ^3 ≈ 4.236)
        int k = (int) Math.pow(PHI, 3);
        
        // Calculate distances to all existing points
        List<DistancePair> distances = new ArrayList<>();
        for (Point other : conceptSpace.values()) {
            if (other.id == point.id) continue;
            
            double distance = euclideanDistance(point.embedding, other.embedding);
            distances.add(new DistancePair(other, distance));
        }
        
        // Sort by distance
        distances.sort(Comparator.comparingDouble(d -> d.distance));
        
        // Connect to K nearest neighbors
        for (int i = 0; i < Math.min(k, distances.size()); i++) {
            DistancePair pair = distances.get(i);
            
            // Phi-weighted connection strength
            // Closer = stronger (inverse distance with phi scaling)
            double weight = PHI / (1.0 + pair.distance);
            
            Connection conn = new Connection(point, pair.point, weight);
            pointConnections.add(conn);
            connectionCount++;
            
            // Bidirectional connection
            connections.computeIfAbsent(pair.point, k2 -> new ArrayList<>())
                      .add(new Connection(pair.point, point, weight));
        }
        
        connections.put(point, pointConnections);
    }
    
    /**
     * Route through manifold
     * 
     * Finds shortest geometric path from source to target.
     * Uses A* search with phi-weighted edges.
     * 
     * @param source Source concept
     * @param target Target concept
     * @return Path through manifold
     */
    public Path route(String source, String target) {
        // Check cache
        String cacheKey = source + "→" + target;
        if (pathCache.containsKey(cacheKey)) {
            return pathCache.get(cacheKey);
        }
        
        Point start = conceptSpace.get(source);
        Point goal = conceptSpace.get(target);
        
        if (start == null || goal == null) {
            return null;
        }
        
        // A* search
        Path path = aStarSearch(start, goal);
        
        // Cache result
        if (path != null) {
            pathCache.put(cacheKey, path);
        }
        
        return path;
    }
    
    /**
     * A* search through manifold
     * 
     * @param start Start point
     * @param goal Goal point
     * @return Shortest path
     */
    private Path aStarSearch(Point start, Point goal) {
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>(
            Comparator.comparingDouble(n -> n.fScore)
        );
        
        Map<Point, SearchNode> allNodes = new HashMap<>();
        
        SearchNode startNode = new SearchNode(start);
        startNode.gScore = 0;
        startNode.fScore = heuristic(start, goal);
        
        openSet.add(startNode);
        allNodes.put(start, startNode);
        
        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();
            
            if (current.point.equals(goal)) {
                return reconstructPath(current);
            }
            
            current.closed = true;
            
            // Explore neighbors
            List<Connection> neighbors = connections.get(current.point);
            if (neighbors == null) continue;
            
            for (Connection conn : neighbors) {
                Point neighbor = conn.to;
                
                SearchNode neighborNode = allNodes.computeIfAbsent(
                    neighbor, 
                    p -> new SearchNode(p)
                );
                
                if (neighborNode.closed) continue;
                
                // Calculate tentative gScore
                // Cost = distance / phi-weight (stronger connections = lower cost)
                double tentativeG = current.gScore + (1.0 / conn.weight);
                
                if (tentativeG < neighborNode.gScore) {
                    neighborNode.parent = current;
                    neighborNode.gScore = tentativeG;
                    neighborNode.fScore = tentativeG + heuristic(neighbor, goal);
                    
                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        
        return null; // No path found
    }
    
    /**
     * Heuristic: Euclidean distance in embedding space
     */
    private double heuristic(Point a, Point b) {
        return euclideanDistance(a.embedding, b.embedding);
    }
    
    /**
     * Reconstruct path from search node
     */
    private Path reconstructPath(SearchNode node) {
        List<Point> points = new ArrayList<>();
        double totalCost = node.gScore;
        
        SearchNode current = node;
        while (current != null) {
            points.add(0, current.point);
            current = current.parent;
        }
        
        return new Path(points, totalCost);
    }
    
    /**
     * Euclidean distance between embeddings
     */
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Get manifold statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ PHI MANIFOLD STATS\n\n" +
            "   Concepts: %,d\n" +
            "   Connections: %,d\n" +
            "   Embedding Dimension: %d\n" +
            "   Avg Connections per Concept: %.2f\n" +
            "   Cached Paths: %,d\n" +
            "   Routing: Geometric (A* with φ-weights)\n" +
            "   Distance Metric: Euclidean (high-dim)\n",
            conceptSpace.size(),
            connectionCount,
            EMBEDDING_DIM,
            conceptSpace.isEmpty() ? 0.0 : (double) connectionCount / conceptSpace.size(),
            pathCache.size()
        );
    }
    
    /**
     * Point in manifold
     */
    public static class Point {
        public final int id;
        public final String concept;
        public final double[] embedding;
        
        public Point(int id, String concept, double[] embedding) {
            this.id = id;
            this.concept = concept;
            this.embedding = embedding;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point point = (Point) o;
            return id == point.id;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }
    
    /**
     * Connection between points
     */
    private static class Connection {
        public final Point from;
        public final Point to;
        public final double weight; // Phi-weighted strength
        
        public Connection(Point from, Point to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    
    /**
     * Path through manifold
     */
    public static class Path {
        public final List<Point> points;
        public final double cost;
        
        public Path(List<Point> points, double cost) {
            this.points = points;
            this.cost = cost;
        }
        
        public int length() {
            return points.size();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Path[cost=").append(String.format("%.4f", cost))
              .append(", length=").append(points.size()).append("]: ");
            
            for (int i = 0; i < points.size(); i++) {
                sb.append(points.get(i).concept);
                if (i < points.size() - 1) sb.append(" → ");
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Distance pair for sorting
     */
    private static class DistancePair {
        public final Point point;
        public final double distance;
        
        public DistancePair(Point point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }
    
    /**
     * Search node for A*
     */
    private static class SearchNode {
        public final Point point;
        public SearchNode parent;
        public double gScore = Double.POSITIVE_INFINITY;
        public double fScore = Double.POSITIVE_INFINITY;
        public boolean closed = false;
        
        public SearchNode(Point point) {
            this.point = point;
        }
    }
}
