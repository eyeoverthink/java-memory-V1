package fraymus.brain;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * OPENAI SPINE - Cloud Brain with GPT-4
 * 
 * Connects to OpenAI API for state-of-the-art reasoning.
 * Provides GPT-4, GPT-4 Turbo, and GPT-3.5 models.
 * 
 * The Enhanced Trinity:
 * - Brain (OpenAI/Ollama): Raw intelligence, reasoning, code generation
 * - Hands (OpenClaw): Execution layer, file system, OS manipulation
 * - Soul (Fraymus): Orchestrator, physics-driven decisions
 */
public class OpenAISpine {

    private final String model;
    private final String apiKey;
    private final String apiUrl;
    private final HttpClient client;

    /**
     * Default to gpt-4-turbo model
     */
    public OpenAISpine(String apiKey) {
        this("gpt-4-turbo", apiKey);
    }

    /**
     * Use specific model (gpt-4, gpt-4-turbo, gpt-3.5-turbo, etc.)
     */
    public OpenAISpine(String model, String apiKey) {
        this(model, apiKey, "https://api.openai.com/v1");
    }

    /**
     * Full constructor with custom API URL
     */
    public OpenAISpine(String model, String apiKey, String apiUrl) {
        this.model = model;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * THINK: Send a prompt to OpenAI and get response
     * 
     * @param prompt The question or task for the LLM
     * @return The LLM's response
     */
    public String think(String prompt) {
        return think(prompt, 0.7, 2000);
    }

    /**
     * THINK: Send a prompt with custom parameters
     * 
     * @param prompt The question or task
     * @param temperature Creativity (0.0-2.0)
     * @param maxTokens Maximum response length
     * @return The LLM's response
     */
    public String think(String prompt, double temperature, int maxTokens) {
        System.out.println("🧠 OPENAI (" + model + "): Thinking about -> \"" + 
            (prompt.length() > 80 ? prompt.substring(0, 80) + "..." : prompt) + "\"");
        
        JSONObject json = new JSONObject();
        json.put("model", model);
        json.put("temperature", temperature);
        json.put("max_tokens", maxTokens);
        
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.put(message);
        json.put("messages", messages);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .timeout(Duration.ofSeconds(60))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONObject res = new JSONObject(response.body());
                String result = res.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
                
                System.out.println("   ✓ OPENAI RESPONSE: " + 
                    (result.length() > 100 ? result.substring(0, 100) + "..." : result));
                return result;
            } else {
                String error = "❌ OPENAI ERROR: HTTP " + response.statusCode() + " - " + response.body();
                System.err.println(error);
                return error;
            }
        } catch (Exception e) {
            String error = "💀 OPENAI FAILED: " + e.getMessage();
            System.err.println(error);
            return error;
        }
    }

    /**
     * ANALYZE: Specialized method for code analysis
     * 
     * @param code The code to analyze
     * @return Analysis result (CLEAN or DIRTY with reasons)
     */
    public String analyzeCode(String code) {
        String prompt = "Analyze this Java code. Return 'CLEAN' if it is good. " +
                       "Return 'DIRTY: <Reason>' if it has bugs, inefficiencies, or issues.\n\n" + code;
        return think(prompt, 0.3, 1000);
    }

    /**
     * DESIGN: Specialized method for system design
     * 
     * @param intent User's intent
     * @return System design as JSON blueprint
     */
    public String designSystem(String intent) {
        String prompt = "You are a System Architect. Design a software stack for: '" + intent + "'. " +
                       "Return a JSON blueprint with modules, dependencies, and structure. " +
                       "Be specific and production-ready.";
        return think(prompt, 0.7, 2000);
    }

    /**
     * REVIEW: Specialized method for code review
     * 
     * @param blueprint The blueprint to review
     * @return Review verdict (APPROVED, REJECTED, or CONDITIONAL)
     */
    public String reviewBlueprint(String blueprint) {
        String prompt = "You are a Senior Code Reviewer and Security Auditor. " +
                       "Review this software architecture:\n\n" + blueprint + "\n\n" +
                       "Look for: Security risks, Race conditions, Missing tests, Flaws.\n" +
                       "Reply 'APPROVED' if solid, 'REJECTED: <reason>' if flawed, " +
                       "'CONDITIONAL: <changes>' if needs improvements.";
        return think(prompt, 0.3, 1500);
    }

    /**
     * Get current model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Check if OpenAI is available
     */
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/models"))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
