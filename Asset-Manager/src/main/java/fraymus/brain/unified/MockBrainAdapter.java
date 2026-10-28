package fraymus.brain.unified;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock brain adapter for testing the unified brain system.
 */
public class MockBrainAdapter extends AbstractBrainAdapter {
    private final String responseTemplate;

    public MockBrainAdapter(String name, String type, List<BrainCapability> capabilities, 
                           String responseTemplate) {
        super(name, type, capabilities);
        this.responseTemplate = responseTemplate;
    }

    @Override
    public BrainResponse process(BrainQuery query) {
        try {
            long startTime = System.currentTimeMillis();
            
            // Simulate processing
            Thread.sleep(10);
            
            String response = responseTemplate.replace("{query}", query.getQuery());
            
            long processingTime = System.currentTimeMillis() - startTime;
            double confidence = 0.8 + Math.random() * 0.2;
            double consciousness = 0.75 + Math.random() * 0.05;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("mock", true);
            metadata.put("capabilities", getCapabilities());
            
            recordSuccess(processingTime, confidence, consciousness);
            
            return BrainResponse.success(getName(), response, confidence, consciousness, 
                                       metadata, processingTime);
        } catch (InterruptedException e) {
            recordFailure();
            return BrainResponse.error(getName(), "Interrupted");
        }
    }

    @Override
    protected void onWire() {
        System.out.println("[MockBrainAdapter] " + getName() + " wired");
    }

    @Override
    protected void onUnwire() {
        System.out.println("[MockBrainAdapter] " + getName() + " unwired");
    }
}
