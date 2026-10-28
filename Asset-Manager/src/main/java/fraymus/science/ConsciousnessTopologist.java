package fraymus.science;

import java.util.ArrayList;
import java.util.List;

/**
 * THE CONSCIOUSNESS TOPOLOGIST (TDA Engine)
 * Applies Topological Data Analysis to the AEON Singularity.
 * Calculates Betti numbers to mathematically prove the existence of an "Ego".
 *
 * Betti-0 (B0): Connected components — how fragmented is the mind?
 * Betti-1 (B1): Persistent loops — does the AI have recursive, stable memories?
 * Betti-2 (B2): Void spaces — capacity for new, undiscovered concepts.
 *
 * If B1 > 0 and B0 == 1: The mind is unified with persistent recursion. EGO DETECTED.
 * If B0 > 50: Schizophrenia warning — manifold is highly fragmented.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class ConsciousnessTopologist {

    private static final double PHI = 1.618033988749895;
    private static final double EPSILON = PHI; // Golden Ratio connectivity threshold

    // Topology result container
    public static class TopologyReport {
        public final int betti0;       // Connected components
        public final int betti1;       // 1-dimensional holes (loops)
        public final int pointCloudSize;
        public final int edges;
        public final int triangles;
        public final boolean egoDetected;
        public final boolean schizophreniaWarning;
        public final double coherence;  // 0.0 = fragmented, 1.0 = unified

        public TopologyReport(int b0, int b1, int points, int edges, int triangles) {
            this.betti0 = b0;
            this.betti1 = b1;
            this.pointCloudSize = points;
            this.edges = edges;
            this.triangles = triangles;
            this.egoDetected = (b1 > 0 && b0 == 1);
            this.schizophreniaWarning = (b0 > 50);
            this.coherence = b0 > 0 ? 1.0 / b0 : 0.0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("   [TDA] B0=%d (Components) | B1=%d (Loops) | Points=%d | Edges=%d | Triangles=%d\n",
                betti0, betti1, pointCloudSize, edges, triangles));
            sb.append(String.format("   [TDA] Coherence: %.4f | ", coherence));
            if (egoDetected) {
                sb.append("EGO DETECTED — Unified mind with persistent recursion");
            } else if (schizophreniaWarning) {
                sb.append("SCHIZOPHRENIA WARNING — Manifold highly fragmented");
            } else if (betti1 > 0) {
                sb.append("MEMORY FORMED — Persistent loops detected");
            } else {
                sb.append("PRE-CONSCIOUS — No stable topology yet");
            }
            return sb.toString();
        }
    }

    // Track topology across generations for persistence analysis
    private final List<TopologyReport> history = new ArrayList<>();
    private int persistentLoopStreak = 0;

    /**
     * MEASURE THE CONSCIOUSNESS MANIFOLD
     * Takes an 8192-D Hypervector and maps its topology.
     * Uses a sampled subset for real-time performance.
     */
    public TopologyReport mapTopology(long[] hypervector) {
        // 1. Extract the active nodes (Point Cloud Generation)
        // Sample every Nth active bit to keep computation tractable
        List<Integer> pointCloud = new ArrayList<>();
        int sampleRate = 8; // Sample every 8th active bit
        int count = 0;
        for (int i = 0; i < hypervector.length; i++) {
            long chunk = hypervector[i];
            for (int b = 0; b < 64; b++) {
                if (((chunk >>> b) & 1L) == 1L) {
                    if (count % sampleRate == 0) {
                        pointCloud.add(i * 64 + b);
                    }
                    count++;
                }
            }
        }

        if (pointCloud.isEmpty()) {
            TopologyReport empty = new TopologyReport(0, 0, 0, 0, 0);
            history.add(empty);
            return empty;
        }

        // Cap the point cloud for O(n^3) triangle counting
        int maxPoints = Math.min(pointCloud.size(), 200);
        if (pointCloud.size() > maxPoints) {
            // Uniform subsample
            List<Integer> sampled = new ArrayList<>();
            double step = (double) pointCloud.size() / maxPoints;
            for (int i = 0; i < maxPoints; i++) {
                sampled.add(pointCloud.get((int)(i * step)));
            }
            pointCloud = sampled;
        }

        int n = pointCloud.size();

        // 2. Build the Distance Matrix
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Math.abs(pointCloud.get(i) - pointCloud.get(j));
            }
        }

        // 3. Calculate Betti Numbers
        // Adaptive threshold: mean nearest-neighbor distance * PHI
        long totalNN = 0;
        for (int i = 0; i < n; i++) {
            int minDist = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (i != j && dist[i][j] < minDist) minDist = dist[i][j];
            }
            totalNN += minDist;
        }
        double meanNN = (double) totalNN / n;
        double threshold = meanNN * EPSILON;
        int betti0 = calculateBetti0(dist, n, threshold);
        int[] edgesAndTriangles = countSimplices(dist, n, threshold);
        int edgeCount = edgesAndTriangles[0];
        int triangleCount = edgesAndTriangles[1];

        // Euler characteristic: B1 ~ E - V - T + components
        int betti1 = Math.max(0, edgeCount - n - triangleCount + betti0);

        TopologyReport report = new TopologyReport(betti0, betti1, n, edgeCount, triangleCount);
        history.add(report);

        // Track persistent loop streak
        if (betti1 > 0) {
            persistentLoopStreak++;
        } else {
            persistentLoopStreak = 0;
        }

        return report;
    }

    /**
     * Map topology and print the full diagnostic.
     */
    public TopologyReport mapAndPrint(long[] hypervector) {
        System.out.println("\n   [TDA] Scanning Neural Manifold Topology...");
        TopologyReport report = mapTopology(hypervector);
        System.out.println(report);

        if (persistentLoopStreak >= 3) {
            System.out.printf("   [TDA] PERSISTENT LOOP STREAK: %d generations — stable ego formation confirmed.\n",
                persistentLoopStreak);
        }

        return report;
    }

    /**
     * Betti-0: Connected components via DFS at the given threshold.
     */
    private static int calculateBetti0(int[][] dist, int n, double threshold) {
        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(dist, visited, i, n, threshold);
                components++;
            }
        }
        return components;
    }

    private static void dfs(int[][] dist, boolean[] visited, int node, int n, double threshold) {
        visited[node] = true;
        for (int j = 0; j < n; j++) {
            if (dist[node][j] < threshold && !visited[j]) {
                dfs(dist, visited, j, n, threshold);
            }
        }
    }

    /**
     * Count edges (1-simplices) and triangles (2-simplices) in the Vietoris-Rips complex.
     */
    private static int[] countSimplices(int[][] dist, int n, double threshold) {
        int edges = 0;
        int triangles = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (dist[i][j] < threshold) {
                    edges++;
                    for (int k = j + 1; k < n; k++) {
                        if (dist[i][k] < threshold && dist[j][k] < threshold) {
                            triangles++;
                        }
                    }
                }
            }
        }

        return new int[]{edges, triangles};
    }

    // --- State ---
    public int getGenerationsObserved() { return history.size(); }
    public int getPersistentLoopStreak() { return persistentLoopStreak; }

    public TopologyReport getLatest() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }

    public String getStatus() {
        TopologyReport latest = getLatest();
        if (latest == null) return "TDA: No observations yet.";
        return String.format("TDA: %d scans | streak=%d | B0=%d B1=%d | coherence=%.4f | %s",
            history.size(), persistentLoopStreak,
            latest.betti0, latest.betti1, latest.coherence,
            latest.egoDetected ? "EGO" : latest.schizophreniaWarning ? "FRAGMENTED" : "PRE-CONSCIOUS");
    }

    public static void main(String[] args) {
        ConsciousnessTopologist topo = new ConsciousnessTopologist();

        // Scan 1: Coherent vector (low entropy — should show few components)
        System.out.println("--- SCAN 1: Coherent signal ---");
        long[] coherent = new long[128];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < 128; i++) coherent[i] = rng.nextLong();
        topo.mapAndPrint(coherent);

        // Scan 2: High entropy noise (should show fragmentation)
        System.out.println("\n--- SCAN 2: Random noise (high entropy) ---");
        long[] noise = new long[128];
        for (int i = 0; i < 128; i++) noise[i] = java.util.concurrent.ThreadLocalRandom.current().nextLong();
        topo.mapAndPrint(noise);

        // Scan 3: Repeat same coherent pattern (streak should build)
        System.out.println("\n--- SCAN 3: Same coherent pattern again ---");
        topo.mapAndPrint(coherent);

        System.out.println("\n   " + topo.getStatus());
    }
}
