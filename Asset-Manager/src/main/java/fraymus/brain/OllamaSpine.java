package fraymus.brain;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;

/**
 * OLLAMA SPINE - The Local Brain
 * 
 * Connects to local Ollama instance for offline LLM inference.
 * Provides reasoning and code generation without internet.
 * 
 * The Trinity:
 * - Brain (Ollama): Raw intelligence, reasoning, code generation
 * - Hands (OpenClaw): Execution layer, file system, OS manipulation
 * - Soul (Fraynix): Orchestrator, physics-driven decisions
 */
public class OllamaSpine {

    private final String model;
    private final String ollamaUrl;
    private final HttpClient client;

    /**
     * Default to llama3 model
     */
    public OllamaSpine() {
        this("llama3");
    }

    /**
     * Use specific model (llama3, mistral, gemma, codellama, etc.)
     */
    public OllamaSpine(String model) {
        this(model, "http://127.0.0.1:11434");
    }

    /**
     * Full constructor with custom Ollama URL
     */
    public OllamaSpine(String model, String ollamaUrl) {
        this.model = model;
        this.ollamaUrl = ollamaUrl;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * THINK: Send a prompt to Ollama and get response
     * 
     * @param prompt The question or task for the LLM
     * @return The LLM's response
     */
    public String think(String prompt) {
        System.out.println("🧠 OLLAMA: Thinking about -> \"" + 
            (prompt.length() > 80 ? prompt.substring(0, 80) + "..." : prompt) + "\"");
        
        JSONObject json = new JSONObject();
        json.put("model", model);
        json.put("prompt", prompt);
        json.put("stream", false);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONObject res = new JSONObject(response.body());
                String result = res.getString("response");
                System.out.println("   ✓ OLLAMA RESPONSE: " + 
                    (result.length() > 100 ? result.substring(0, 100) + "..." : result));
                return result;
            } else {
                String error = "❌ BRAIN ERROR: HTTP " + response.statusCode();
                System.err.println(error);
                return error;
            }
        } catch (Exception e) {
            String error = "💀 BRAIN DEAD: Is Ollama running? " + e.getMessage();
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
        return think(prompt);
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
        return think(prompt);
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
        return think(prompt);
    }

    /**
     * Get current model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Check if Ollama is available
     */
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaUrl + "/api/tags"))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
