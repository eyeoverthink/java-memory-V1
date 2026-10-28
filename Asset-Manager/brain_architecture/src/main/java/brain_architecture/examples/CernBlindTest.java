package brain_architecture.examples;

import brain_architecture.backends.Gemma3Backend;
import brain_architecture.core.BackendInterface;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Blind test against CERN - tests AI knowledge without revealing the target
 */
public class CernBlindTest {

    public static void main(String[] args) {
        System.out.println("=== CERN Blind Test ===\n");

        // Check if API key is set
        String apiKey = System.getProperty("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("GOOGLE_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            if (args.length > 0) {
                apiKey = args[0];
            }
        }

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: GOOGLE_API_KEY not set");
            return;
        }

        System.out.println("API Key found: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");

        // Create Gemma3 backend with API key
        Gemma3Backend gemma = new Gemma3Backend(apiKey, 
            "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-26b-a4b-it:generateContent", 
            "gemma-4-26b-a4b-it");

        try {
            // Initialize the backend
            System.out.println("\nInitializing backend...");
            gemma.initialize();
            System.out.println("✓ Backend initialized successfully");

            // Blind test questions about CERN (without mentioning CERN explicitly)
            String[] questions = {
                "What is the Large Hadron Collider and what is its purpose?",
                "What is the Higgs boson and why is it important?",
                "What happens when protons collide at near light speed?",
                "What is antimatter and how is it created?",
                "What is the Standard Model of particle physics?",
                "What is dark matter and how do we detect it?",
                "What is the purpose of the ATLAS detector?",
                "What is quantum chromodynamics?",
                "What is the difference between a quark and a lepton?",
                "What is the significance of the speed of light in particle accelerators?"
            };

            System.out.println("\n=== Running Blind Test ===");
            System.out.println("Questions: " + questions.length);
            System.out.println("Target: CERN (European Organization for Nuclear Research)\n");

            int correctCount = 0;
            long totalLatency = 0;

            for (int i = 0; i < questions.length; i++) {
                System.out.println("\n--- Question " + (i + 1) + " ---");
                System.out.println("Q: " + questions[i]);

                CompletableFuture<BackendInterface.QueryResult> futureResult = 
                    gemma.executeQuery(questions[i], BackendInterface.TaskType.REASONING, Map.of());
                BackendInterface.QueryResult result = futureResult.get();

                System.out.println("A: " + result.getContent());
                System.out.println("Success: " + result.isSuccess());
                System.out.println("Confidence: " + result.getConfidence());
                System.out.println("Latency: " + result.getLatencyMs() + "ms");

                if (result.isSuccess()) {
                    correctCount++;
                    totalLatency += result.getLatencyMs();
                }
            }

            // Summary
            System.out.println("\n=== Test Summary ===");
            System.out.println("Total Questions: " + questions.length);
            System.out.println("Successful Responses: " + correctCount);
            System.out.println("Success Rate: " + (correctCount * 100.0 / questions.length) + "%");
            System.out.println("Average Latency: " + (totalLatency / correctCount) + "ms");
            System.out.println("\n✓ CERN Blind Test Complete");

        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            gemma.shutdown();
            System.out.println("\n✓ Backend shutdown complete");
        }
    }
}
