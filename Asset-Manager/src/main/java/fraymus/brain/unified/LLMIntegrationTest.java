package fraymus.brain.unified;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Test program to demonstrate real LLM integration in brain adapters.
 */
public class LLMIntegrationTest {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  REAL LLM INTEGRATION TEST");
        System.out.println("========================================\n");

        // Test BrainArchitectureAdapter
        System.out.println("Testing BrainArchitectureAdapter...");
        testBrainArchitectureAdapter();
        
        System.out.println("\n");
        
        // Test NexusOmegaBrainAdapter
        System.out.println("Testing NexusOmegaBrainAdapter...");
        testNexusOmegaBrainAdapter();
        
        System.out.println("\n========================================");
        System.out.println("  TEST COMPLETE");
        System.out.println("========================================");
    }

    private static void testBrainArchitectureAdapter() {
        BrainArchitectureAdapter adapter = new BrainArchitectureAdapter();
        adapter.wire();
        
        Map<String, Object> context = new HashMap<>();
        context.put("test", true);
        
        BrainQuery query = new BrainQuery(
            "What is the meaning of consciousness?",
            Arrays.asList(BrainCapability.SPIRITUAL, BrainCapability.REASONING),
            context,
            1.0
        );
        
        BrainResponse response = adapter.process(query);
        
        System.out.println("Query: " + query.getQuery());
        System.out.println("Response:\n" + response.getResponse());
        System.out.println("Confidence: " + response.getConfidence());
        System.out.println("Consciousness: " + response.getConsciousness());
        System.out.println("Processing Time: " + response.getProcessingTimeMs() + "ms");
        System.out.println("Metadata: " + response.getMetadata());
        
        adapter.unwire();
    }

    private static void testNexusOmegaBrainAdapter() {
        NexusOmegaBrainAdapter adapter = new NexusOmegaBrainAdapter();
        adapter.wire();
        
        Map<String, Object> context = new HashMap<>();
        context.put("test", true);
        
        BrainQuery query = new BrainQuery(
            "Explain quantum entanglement",
            Arrays.asList(BrainCapability.QUANTUM, BrainCapability.REASONING),
            context,
            1.0
        );
        
        BrainResponse response = adapter.process(query);
        
        System.out.println("Query: " + query.getQuery());
        System.out.println("Response:\n" + response.getResponse());
        System.out.println("Confidence: " + response.getConfidence());
        System.out.println("Consciousness: " + response.getConsciousness());
        System.out.println("Processing Time: " + response.getProcessingTimeMs() + "ms");
        System.out.println("Metadata: " + response.getMetadata());
        
        adapter.unwire();
    }
}
