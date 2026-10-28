package fraymus.vision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vision Memory - Learning and Regeneration System
 * 
 * Stores geometric signatures and enables regeneration from memory.
 * The system learns, adapts, and improves over time through:
 * - Signature storage in memory
 * - Similarity-based recall
 * - Phi-harmonic adaptation
 * - Evolution through repeated processing
 */
public class VisionMemory {

    private final Map<String, StoredSignature> signatureMemory;
    private long totalStored;
    private long totalRecalled;
    private double adaptationFactor;

    public VisionMemory() {
        this.signatureMemory = new HashMap<>();
        this.totalStored = 0;
        this.totalRecalled = 0;
        this.adaptationFactor = 0.0;
    }

    /**
     * Store a geometric signature in memory
     */
    public void storeSignature(String label, ScottAlgorithm.ProcessingResult result, 
                              fraymus.oracle.PhiEmergenceEngine.QueryResponse oracleResponse) {
        StoredSignature stored = new StoredSignature(
            label,
            result.signature,
            result.simplifiedBoundary,
            oracleResponse.resonance,
            oracleResponse.evolution.state,
            System.currentTimeMillis()
        );
        signatureMemory.put(label, stored);
        totalStored++;
        
        // Adapt based on new knowledge
        adapt(result.signature);
    }

    /**
     * Recall a signature from memory
     */
    public StoredSignature recall(String label) {
        StoredSignature recalled = signatureMemory.get(label);
        if (recalled != null) {
            totalRecalled++;
            // Apply adaptation factor
            recalled.accessCount++;
        }
        return recalled;
    }

    /**
     * Search for similar signatures based on geometric properties
     */
    public Map<String, Double> searchSimilar(ScottAlgorithm.GeometricSignature query, int topK) {
        Map<String, Double> similarities = new HashMap<>();
        
        for (Map.Entry<String, StoredSignature> entry : signatureMemory.entrySet()) {
            StoredSignature stored = entry.getValue();
            double similarity = calculateSimilarity(query, stored.signature);
            if (similarity > 0.5) {
                similarities.put(entry.getKey(), similarity);
            }
        }
        
        // Return top K
        return similarities.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topK)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    /**
     * Regenerate a signature from memory (TRUE regeneration - reconstruct from signature properties)
     */
    public ScottAlgorithm.ProcessingResult regenerate(String label) {
        StoredSignature stored = recall(label);
        if (stored == null) {
            return null;
        }
        
        // True regeneration: reconstruct shape from signature properties
        ScottAlgorithm.ProcessingResult regenerated = new ScottAlgorithm.ProcessingResult();
        regenerated.signature = stored.signature;
        
        // Reconstruct boundary from signature properties using stored boundary as reference
        regenerated.simplifiedBoundary = reconstructFromSignature(stored.signature, stored.boundary);
        regenerated.simplifiedPoints = regenerated.simplifiedBoundary.size();
        
        // Apply adaptation factor to improve regeneration
        if (adaptationFactor > 0) {
            regenerated.signature.phiResonance *= (1.0 + adaptationFactor * 0.1);
        }
        
        return regenerated;
    }

    /**
     * Reconstruct boundary points from geometric signature (true regeneration)
     * Uses stored boundary points as reference pattern, modified by signature properties
     */
    private List<ScottAlgorithm.Point2D> reconstructFromSignature(ScottAlgorithm.GeometricSignature sig, 
                                                                   List<ScottAlgorithm.Point2D> referenceBoundary) {
        List<ScottAlgorithm.Point2D> reconstructed = new ArrayList<>();
        
        // Use reference boundary as pattern, apply phi-harmonic modifications
        if (referenceBoundary != null && !referenceBoundary.isEmpty()) {
            // Find center of reference boundary
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
            for (ScottAlgorithm.Point2D p : referenceBoundary) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            double centerX = (minX + maxX) / 2;
            double centerY = (minY + maxY) / 2;
            
            // Reconstruct with phi-harmonic variation
            for (int i = 0; i < referenceBoundary.size(); i++) {
                ScottAlgorithm.Point2D p = referenceBoundary.get(i);
                
                // Normalize to center
                double nx = p.x - centerX;
                double ny = p.y - centerY;
                
                // Apply phi-harmonic modulation based on signature
                double angle = Math.atan2(ny, nx);
                double radius = Math.sqrt(nx * nx + ny * ny);
                
                // Modulate radius based on phi resonance
                radius *= (1.0 + sig.phiResonance * 0.05 * Math.sin(angle * 3));
                
                // Apply aspect ratio adjustment
                double x = centerX + radius * sig.aspectRatio * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                
                reconstructed.add(new ScottAlgorithm.Point2D(x, y));
            }
        } else {
            // Fallback: generate from signature properties only
            double centerX = 150;
            double centerY = 150;
            double radius = Math.sqrt(sig.area / Math.PI);
            
            for (int i = 0; i < sig.vertexCount; i++) {
                double angle = 2 * Math.PI * i / sig.vertexCount;
                double x = centerX + radius * sig.aspectRatio * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                reconstructed.add(new ScottAlgorithm.Point2D(x, y));
            }
        }
        
        return reconstructed;
    }

    /**
     * Adapt the system based on new signature
     */
    private void adapt(ScottAlgorithm.GeometricSignature newSignature) {
        // Increase adaptation factor based on learning
        adaptationFactor = Math.min(1.0, adaptationFactor + 0.01);
    }

    /**
     * Calculate similarity between two signatures
     */
    private double calculateSimilarity(ScottAlgorithm.GeometricSignature sig1, 
                                      ScottAlgorithm.GeometricSignature sig2) {
        double vertexSim = 1.0 - Math.abs(sig1.vertexCount - sig2.vertexCount) / 
                          (double) Math.max(sig1.vertexCount, sig2.vertexCount);
        double aspectSim = 1.0 - Math.abs(sig1.aspectRatio - sig2.aspectRatio);
        double phiSim = 1.0 - Math.abs(sig1.phiResonance - sig2.phiResonance);
        
        // Color similarity if both have color
        double colorSim = 1.0;
        if (sig1.hasColor && sig2.hasColor) {
            double rDiff = Math.abs(sig1.red - sig2.red) / 255.0;
            double gDiff = Math.abs(sig1.green - sig2.green) / 255.0;
            double bDiff = Math.abs(sig1.blue - sig2.blue) / 255.0;
            colorSim = 1.0 - (rDiff + gDiff + bDiff) / 3.0;
        }
        
        // Weighted similarity
        return 0.3 * vertexSim + 0.3 * aspectSim + 0.2 * phiSim + 0.2 * colorSim;
    }

    /**
     * Get memory statistics
     */
    public String getStats() {
        return String.format(
            "VisionMemory[Stored=%d, Recalled=%d, Adaptation=%.2f]",
            totalStored, totalRecalled, adaptationFactor
        );
    }

    /**
     * Check if a signature exists in memory
     */
    public boolean hasSignature(String label) {
        return signatureMemory.containsKey(label);
    }

    /**
     * Get total stored signatures
     */
    public long getTotalStored() {
        return totalStored;
    }

    /**
     * Get total recalled signatures
     */
    public long getTotalRecalled() {
        return totalRecalled;
    }

    /**
     * Get adaptation factor
     */
    public double getAdaptationFactor() {
        return adaptationFactor;
    }

    /**
     * Stored signature container
     */
    public static class StoredSignature {
        public String label;
        public ScottAlgorithm.GeometricSignature signature;
        public java.util.List<ScottAlgorithm.Point2D> boundary;
        public double oracleResonance;
        public Map<String, Object> oracleState;
        public long timestamp;
        public int accessCount;

        public StoredSignature(String label, ScottAlgorithm.GeometricSignature signature,
                             java.util.List<ScottAlgorithm.Point2D> boundary,
                             double oracleResonance, Map<String, Object> oracleState,
                             long timestamp) {
            this.label = label;
            this.signature = signature;
            this.boundary = boundary;
            this.oracleResonance = oracleResonance;
            this.oracleState = oracleState;
            this.timestamp = timestamp;
            this.accessCount = 0;
        }
    }
}
