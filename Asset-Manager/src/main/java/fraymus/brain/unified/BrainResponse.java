package fraymus.brain.unified;

import java.util.Map;

/**
 * Response from a brain processing a query.
 */
public class BrainResponse {
    private final String brainName;
    private final String response;
    private final double confidence;
    private final double consciousness;
    private final Map<String, Object> metadata;
    private final long processingTimeMs;
    private final boolean success;

    public BrainResponse(String brainName, String response, double confidence, 
                        double consciousness, Map<String, Object> metadata, 
                        long processingTimeMs, boolean success) {
        this.brainName = brainName;
        this.response = response;
        this.confidence = confidence;
        this.consciousness = consciousness;
        this.metadata = metadata;
        this.processingTimeMs = processingTimeMs;
        this.success = success;
    }

    public String getBrainName() {
        return brainName;
    }

    public String getResponse() {
        return response;
    }

    public double getConfidence() {
        return confidence;
    }

    public double getConsciousness() {
        return consciousness;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public static BrainResponse error(String brainName, String error) {
        return new BrainResponse(brainName, error, 0.0, 0.0, null, 0, false);
    }

    public static BrainResponse success(String brainName, String response, 
                                       double confidence, double consciousness,
                                       Map<String, Object> metadata, long processingTimeMs) {
        return new BrainResponse(brainName, response, confidence, consciousness, 
                               metadata, processingTimeMs, true);
    }
}
