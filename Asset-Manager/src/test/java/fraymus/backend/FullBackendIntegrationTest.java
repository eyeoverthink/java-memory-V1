package fraymus.backend;

/**
 * Full Backend Integration Test - All Systems Unlocked
 * Demonstrates querying all available AI backends without artificial limitations
 */
public class FullBackendIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Full Backend Integration Test ===\n");
        System.out.println("Design Philosophy: Private systems have no artificial limitations.\n");
        
        FullBackendIntegration integration = new FullBackendIntegration();
        
        // Test 1: List available backends
        testListBackends(integration);
        
        // Test 2: Check backend availability
        testBackendAvailability(integration);
        
        // Test 3: Query individual backends
        testIndividualQueries(integration);
        
        // Test 4: Query all backends in parallel
        testQueryAll(integration);
        
        // Test 5: Query specific backends
        testQueryMultiple(integration);
        
        System.out.println("\n=== All Tests Complete ===");
        System.out.println("\nAll backends unlocked and available for full utilization.");
    }
    
    private static void testListBackends(FullBackendIntegration integration) {
        System.out.println("Test 1: List Available Backends");
        
        java.util.List<String> backends = integration.getAvailableBackends();
        System.out.println("Available backends: " + backends.size());
        for (String backend : backends) {
            System.out.println("  - " + backend);
        }
        System.out.println();
    }
    
    private static void testBackendAvailability(FullBackendIntegration integration) {
        System.out.println("Test 2: Backend Availability");
        
        java.util.List<String> backends = integration.getAvailableBackends();
        for (String backend : backends) {
            boolean available = integration.isBackendAvailable(backend);
            System.out.println("  " + backend + ": " + (available ? "AVAILABLE" : "UNAVAILABLE"));
        }
        System.out.println();
    }
    
    private static void testIndividualQueries(FullBackendIntegration integration) {
        System.out.println("Test 3: Individual Backend Queries");
        
        String prompt = "What is 2 + 2?";
        
        // Test a few backends
        String[] testBackends = {"ollama", "deepseek", "gpt2"};
        
        for (String backend : testBackends) {
            FullBackendIntegration.BackendResponse response = integration.query(backend, prompt);
            System.out.println("Backend: " + response.backend);
            System.out.println("Success: " + response.success);
            System.out.println("Latency: " + response.latencyMs + "ms");
            if (response.success) {
                System.out.println("Content preview: " + 
                    response.content.substring(0, Math.min(100, response.content.length())));
            } else {
                System.out.println("Error: " + response.error);
            }
            System.out.println();
        }
    }
    
    private static void testQueryAll(FullBackendIntegration integration) {
        System.out.println("Test 4: Query All Backends");
        
        String prompt = "Explain quantum computing in one sentence.";
        
        System.out.println("Querying all backends with prompt: \"" + prompt + "\"");
        System.out.println();
        
        java.util.Map<String, FullBackendIntegration.BackendResponse> responses = 
            integration.queryAll(prompt);
        
        for (String backend : responses.keySet()) {
            FullBackendIntegration.BackendResponse response = responses.get(backend);
            System.out.println("[" + backend + "]");
            System.out.println("  Success: " + response.success);
            System.out.println("  Latency: " + response.latencyMs + "ms");
            if (response.success) {
                System.out.println("  Content: " + 
                    response.content.substring(0, Math.min(150, response.content.length())));
            } else {
                System.out.println("  Error: " + response.error);
            }
            System.out.println();
        }
    }
    
    private static void testQueryMultiple(FullBackendIntegration integration) {
        System.out.println("Test 5: Query Multiple Specific Backends");
        
        String prompt = "What is the meaning of life?";
        
        java.util.List<String> selectedBackends = new java.util.ArrayList<>();
        selectedBackends.add("ollama");
        selectedBackends.add("claude");
        selectedBackends.add("gemma");
        
        System.out.println("Querying selected backends: " + selectedBackends);
        System.out.println();
        
        java.util.Map<String, FullBackendIntegration.BackendResponse> responses = 
            integration.queryMultiple(selectedBackends, prompt);
        
        for (String backend : responses.keySet()) {
            FullBackendIntegration.BackendResponse response = responses.get(backend);
            System.out.println("[" + backend + "]");
            System.out.println("  Success: " + response.success);
            System.out.println("  Latency: " + response.latencyMs + "ms");
            if (response.success) {
                System.out.println("  Content: " + 
                    response.content.substring(0, Math.min(150, response.content.length())));
            } else {
                System.out.println("  Error: " + response.error);
            }
            System.out.println();
        }
    }
}
