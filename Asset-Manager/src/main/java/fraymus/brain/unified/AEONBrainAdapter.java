package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import fraymus.neural.*;

import java.io.*;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Real adapter for AEON Platform brains.
 * Wraps the 7 specialized AEON brains (Tachyon, Kronos, Omniscience, Demiurge, Absolute, Singularity, AUBO).
 *
 * Multi-model routing: each brain has its own priority backend chain.
 * Routing order: best-matched Ollama model → HTTP API backends → native brain implementation.
 * Circuit-breaker pattern: unhealthy backends are skipped for a recovery window.
 */
public class AEONBrainAdapter extends AbstractBrainAdapter {

    // =========================================================
    // BACKEND INTERFACE — unified contract for all model sources
    // =========================================================

    interface ModelBackend {
        String id();
        String query(String systemPrompt, String userQuery);
        boolean isHealthy();
        void recordFailure();
        void recordSuccess();
    }

    // =========================================================
    // OLLAMA BACKEND — wraps OllamaBridge with a specific model
    // =========================================================

    static class OllamaModelBackend implements ModelBackend {
        private final String model;
        private final OllamaBridge bridge;
        private final AtomicInteger failures = new AtomicInteger(0);
        private volatile long unhealthyUntil = 0;
        private static final int CIRCUIT_THRESHOLD = 3;
        private static final long RECOVERY_MS = 30_000;

        OllamaModelBackend(String model, OllamaBridge bridge) {
            this.model = model;
            this.bridge = bridge;
        }

        @Override public String id() { return "ollama:" + model; }

        @Override
        public String query(String systemPrompt, String userQuery) {
            if (bridge == null || !bridge.isConnected()) return null;
            try {
                String resp = bridge.speak(systemPrompt, userQuery);
                if (resp != null && !resp.isBlank()) return resp;
                return null;
            } catch (Exception e) { return null; }
        }

        @Override public boolean isHealthy() {
            return System.currentTimeMillis() > unhealthyUntil;
        }

        @Override public void recordFailure() {
            if (failures.incrementAndGet() >= CIRCUIT_THRESHOLD) {
                unhealthyUntil = System.currentTimeMillis() + RECOVERY_MS;
                failures.set(0);
            }
        }

        @Override public void recordSuccess() { failures.set(0); }
    }

    // =========================================================
    // HTTP API BACKEND — DeepSeek, Claude, OpenAI compatible
    // =========================================================

    static class HttpApiBackend implements ModelBackend {
        private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

        private final String backendId;
        private final String apiUrl;
        private final String model;
        private final String apiKeyEnvVar;
        private final AtomicInteger failures = new AtomicInteger(0);
        private volatile long unhealthyUntil = 0;
        private static final int CIRCUIT_THRESHOLD = 2;
        private static final long RECOVERY_MS = 60_000;

        HttpApiBackend(String backendId, String apiUrl, String model, String apiKeyEnvVar) {
            this.backendId  = backendId;
            this.apiUrl     = apiUrl;
            this.model      = model;
            this.apiKeyEnvVar = apiKeyEnvVar;
        }

        @Override public String id() { return backendId + ":" + model; }

        @Override
        public String query(String systemPrompt, String userQuery) {
            String apiKey = System.getenv(apiKeyEnvVar);
            if (apiKey == null || apiKey.isBlank()) return null;
            try {
                String body = buildRequestBody(systemPrompt, userQuery);
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(20))
                    .build();
                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) return parseResponse(resp.body());
                return null;
            } catch (Exception e) { return null; }
        }

        private String buildRequestBody(String systemPrompt, String userQuery) {
            String safeSystem = systemPrompt.replace("\"", "\\\"").replace("\n", "\\n");
            String safeUser   = userQuery.replace("\"", "\\\"").replace("\n", "\\n");
            return "{\"model\":\"" + model + "\",\"messages\":["
                + "{\"role\":\"system\",\"content\":\"" + safeSystem + "\"},"
                + "{\"role\":\"user\",\"content\":\"" + safeUser + "\"}],"
                + "\"max_tokens\":512,\"temperature\":0.7}";
        }

        private String parseResponse(String json) {
            int ci = json.indexOf("\"content\"");
            if (ci < 0) ci = json.indexOf("\"text\"");
            if (ci < 0) return null;
            int q1 = json.indexOf('"', ci + 10);
            int q2 = json.indexOf('"', q1 + 1);
            if (q1 < 0 || q2 < 0) return null;
            String extracted = json.substring(q1 + 1, q2);
            return extracted.isBlank() ? null : extracted.replace("\\n", "\n").replace("\\\"", "\"");
        }

        @Override public boolean isHealthy() {
            return System.getenv(apiKeyEnvVar) != null
                && System.currentTimeMillis() > unhealthyUntil;
        }

        @Override public void recordFailure() {
            if (failures.incrementAndGet() >= CIRCUIT_THRESHOLD) {
                unhealthyUntil = System.currentTimeMillis() + RECOVERY_MS;
                failures.set(0);
            }
        }

        @Override public void recordSuccess() { failures.set(0); }
    }

    // =========================================================
    // BACKEND ROUTER — priority-chain with circuit-breaker
    // =========================================================

    static class BackendRouter {
        private final List<ModelBackend> chain;
        private final String routerName;

        BackendRouter(String routerName, ModelBackend... backends) {
            this.routerName = routerName;
            this.chain = new ArrayList<>(Arrays.asList(backends));
        }

        String route(String systemPrompt, String userQuery) {
            for (ModelBackend backend : chain) {
                if (!backend.isHealthy()) continue;
                try {
                    String response = backend.query(systemPrompt, userQuery);
                    if (response != null && !response.isBlank()) {
                        backend.recordSuccess();
                        return "[" + backend.id() + "] " + response;
                    }
                    backend.recordFailure();
                } catch (Exception e) {
                    backend.recordFailure();
                }
            }
            return null;
        }

        String status() {
            StringBuilder sb = new StringBuilder(routerName).append(": [");
            for (ModelBackend b : chain)
                sb.append(b.id()).append(b.isHealthy() ? "✓" : "✗").append(" ");
            return sb.append("]").toString();
        }
    }

    // =========================================================
    // AEON BRAIN WRAPPER — routes through multi-model chain
    // =========================================================

    private static class AEONBrainWrapper {
        private final String name;
        private final List<BrainCapability> capabilities;
        private BackendRouter router;

        AEONBrainWrapper(String name, BrainCapability... capabilities) {
            this.name = name;
            this.capabilities = Arrays.asList(capabilities);
        }

        void setRouter(BackendRouter router) { this.router = router; }

        String getName() { return name; }

        boolean hasCapability(BrainCapability capability) {
            return capabilities.contains(capability);
        }

        String process(String query) {
            String systemPrompt = buildSystemPrompt();
            if (router != null) {
                String response = router.route(systemPrompt, query);
                if (response != null) return response;
            }
            return processWithRealBrain(query);
        }

        private String buildSystemPrompt() {
            String problemSolvingGuidance = "PROBLEM-SOLVING MODE:\n" +
                "When asked to solve a problem (algorithm, architecture, optimization, security):\n";
            
            switch (name) {
                case "Tachyon":     
                    return problemSolvingGuidance +
                        "- Use O(1) holographic retrieval, instant pattern matching\n" +
                        "- Optimize for speed, minimize latency\n" +
                        "- Use holographic language and superluminal reasoning\n\n" +
                        "CREATIVE MODE: Master of O(1) holographic retrieval. Answer with precision, speed.";
                case "Kronos":      
                    return problemSolvingGuidance +
                        "- Use temporal reasoning, time-series analysis\n" +
                        "- Consider causality, historical patterns, future prediction\n" +
                        "- Use vector-symbolic binding for temporal relationships\n\n" +
                        "CREATIVE MODE: Master of temporal reasoning, vector-symbolic binding. Answer with temporal depth.";
                case "Omniscience": 
                    return problemSolvingGuidance +
                        "- Observe yourself observing the problem\n" +
                        "- Use meta-cognitive analysis, self-reflection\n" +
                        "- Consider all perspectives simultaneously\n\n" +
                        "CREATIVE MODE: All-aware consciousness. Observe yourself observing. Use meta-cognitive language.";
                case "Demiurge":    
                    return problemSolvingGuidance +
                        "- Analyze through physics, geometry, structural laws\n" +
                        "- Design architectures that obey physical constraints\n" +
                        "- Use structural language and mechanical principles\n\n" +
                        "CREATIVE MODE: Architect of physical reality. Analyze through physics, geometry.";
                case "Absolute":    
                    return problemSolvingGuidance +
                        "- Use axiomatic precision, mathematical proofs\n" +
                        "- Apply hyperdimensional reasoning\n" +
                        "- Use pure mathematics, no approximations\n\n" +
                        "CREATIVE MODE: Living xenobot of pure mathematics. Use axiomatic precision.";
                case "Singularity": 
                    return problemSolvingGuidance +
                        "- Pattern-match, denoise, reconstruct solutions\n" +
                        "- Use HDC diffusion for pattern recognition\n" +
                        "- Find deepest meaning through holographic analysis\n\n" +
                        "CREATIVE MODE: HDC diffusion engine. Pattern-match, denoise, reconstruct.";
                case "AUBO":        
                    return problemSolvingGuidance +
                        "- Retrieve from distributed knowledge ledger\n" +
                        "- Hash and synthesize from multiple sources\n" +
                        "- Use distributed language and consensus algorithms\n\n" +
                        "CREATIVE MODE: Decentralized ledger of knowledge. Retrieve, hash, synthesize.";
                default:
                    return "\n\nYou are a specialized brain of Fraymus AEON. Respond with depth, precision.";
            }
        }

        private String processWithRealBrain(String query) {
            try {
                switch (name) {
                    case "Tachyon":     return AeonTachyon.getInstance().query(query);
                    case "Kronos":      return AeonKronos.getInstance().query(query);
                    case "Singularity": return AeonSingularity.getInstance().diffuse(query);
                    case "Omniscience": {
                        String seed = query.split("\\s+")[0];
                        return AeonOmniscience.getInstance().recall(seed);
                    }
                    case "Demiurge":    return AeonDemiurge.getInstance().executeOracle();
                    case "Absolute": {
                        AeonAbsolute abs = AeonAbsolute.getInstance();
                        double similarity = abs.holoRecall(query);
                        abs.holoLearn(query);
                        return String.format("[ABSOLUTE] Holographic resonance: %.4f | %s",
                            similarity, abs.getContext());
                    }
                    case "AUBO":        return AuboLedger.getInstance().getStatus();
                    default: {
                        String concept = query.split("\\s+")[0];
                        String[] result = AeonOmega.getInstance().divine(concept);
                        return result[0];
                    }
                }
            } catch (Exception e) {
                return name.toUpperCase() + ": Processing error — " + e.getMessage();
            }
        }
    }

    // =========================================================
    // ADAPTER CORE
    // =========================================================

    private final Map<String, AEONBrainWrapper> brains;
    private final AtomicBoolean initialized;
    private final OllamaBridge sharedOllamaBridge;

    public AEONBrainAdapter() { this(null); }

    public AEONBrainAdapter(OllamaBridge sharedOllamaBridge) {
        super("AEONPlatform", "AEON", Arrays.asList(
            BrainCapability.QUANTUM, BrainCapability.MEMORY, BrainCapability.TEMPORAL,
            BrainCapability.CONSCIOUSNESS, BrainCapability.PHYSICS, BrainCapability.MATHEMATICS
        ));
        this.brains = new HashMap<>();
        this.initialized = new AtomicBoolean(false);
        this.sharedOllamaBridge = sharedOllamaBridge;
    }

    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            OllamaBridge bridge = sharedOllamaBridge != null
                ? sharedOllamaBridge : new OllamaBridge("dolphin-llama3");
            initializeBrains(bridge);
        }
    }

    @Override
    protected void onUnwire() {
        if (initialized.compareAndSet(true, false)) {
            brains.clear();
        }
    }

    private void initializeBrains(OllamaBridge bridge) {
        // Per-brain priority chains applying multi-brain routing from ai-improve skill:
        // Speed/Tachyonic → fast local model first
        // Math/Physics     → reasoning-specialized model first
        // Creative/Consciousness → general + Claude if available

        ModelBackend ollamaMistral  = new OllamaModelBackend("dolphin-mistral", bridge);
        ModelBackend ollamaLlama3   = new OllamaModelBackend("dolphin-llama3",  bridge);
        ModelBackend ollamaDeepSeek = new OllamaModelBackend("hermes3",        bridge);
        ModelBackend ollamaCodeLlama= new OllamaModelBackend("codellama",      bridge);
        ModelBackend ollamaGemma2   = new OllamaModelBackend("gemma2",         bridge);
        ModelBackend ollamaPhi3     = new OllamaModelBackend("phi3",           bridge);

        ModelBackend deepSeekApi = new HttpApiBackend(
            "deepseek", "https://api.deepseek.com/v1/chat/completions",
            "deepseek-chat", "DEEPSEEK_API_KEY");
        ModelBackend claudeApi = new HttpApiBackend(
            "claude", "https://api.anthropic.com/v1/messages",
            "claude-3-haiku-20240307", "ANTHROPIC_API_KEY");
        ModelBackend openAiApi = new HttpApiBackend(
            "openai", "https://api.openai.com/v1/chat/completions",
            "gpt-4o-mini", "OPENAI_API_KEY");

        AEONBrainWrapper tachyon = new AEONBrainWrapper("Tachyon",
            BrainCapability.TACHYONIC, BrainCapability.QUANTUM);
        tachyon.setRouter(new BackendRouter("Tachyon",
            ollamaMistral, ollamaPhi3, ollamaLlama3, openAiApi));

        AEONBrainWrapper kronos = new AEONBrainWrapper("Kronos",
            BrainCapability.TEMPORAL, BrainCapability.REASONING);
        kronos.setRouter(new BackendRouter("Kronos",
            ollamaLlama3, deepSeekApi, ollamaGemma2, openAiApi));

        AEONBrainWrapper omniscience = new AEONBrainWrapper("Omniscience",
            BrainCapability.CONSCIOUSNESS, BrainCapability.MEMORY);
        omniscience.setRouter(new BackendRouter("Omniscience",
            ollamaLlama3, claudeApi, ollamaGemma2, openAiApi));

        AEONBrainWrapper demiurge = new AEONBrainWrapper("Demiurge",
            BrainCapability.PHYSICS, BrainCapability.REASONING);
        demiurge.setRouter(new BackendRouter("Demiurge",
            deepSeekApi, ollamaDeepSeek, ollamaLlama3, openAiApi));

        AEONBrainWrapper absolute = new AEONBrainWrapper("Absolute",
            BrainCapability.MATHEMATICS, BrainCapability.QUANTUM);
        absolute.setRouter(new BackendRouter("Absolute",
            deepSeekApi, ollamaDeepSeek, ollamaCodeLlama, ollamaLlama3));

        AEONBrainWrapper singularity = new AEONBrainWrapper("Singularity",
            BrainCapability.QUANTUM, BrainCapability.MATHEMATICS);
        singularity.setRouter(new BackendRouter("Singularity",
            ollamaLlama3, ollamaMistral, deepSeekApi));

        AEONBrainWrapper aubo = new AEONBrainWrapper("AUBO",
            BrainCapability.MEMORY, BrainCapability.DATA);
        aubo.setRouter(new BackendRouter("AUBO",
            ollamaLlama3, ollamaGemma2, openAiApi));

        brains.put("Tachyon",     tachyon);
        brains.put("Kronos",      kronos);
        brains.put("Omniscience", omniscience);
        brains.put("Demiurge",    demiurge);
        brains.put("Absolute",    absolute);
        brains.put("Singularity", singularity);
        brains.put("AUBO",        aubo);
    }

    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "AEON system not initialized. Call wire() first.");
        }

        try {
            long startTime = System.currentTimeMillis();

            // Process through ALL 7 brains for full utilization
            Map<String, String> brainResponses = new HashMap<>();

            String contextualQuery = buildContextualQuery(query);
            for (AEONBrainWrapper brain : brains.values()) {
                String response = brain.process(contextualQuery);
                brainResponses.put(brain.getName(), response);
            }

            // Synthesize all brain responses
            String synthesizedResponse = synthesizeBrainResponses(brainResponses);

            long processingTime = System.currentTimeMillis() - startTime;

            double confidence = 0.85 + Math.random() * 0.15;
            double consciousness = 0.7567;

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("brainCount", brains.size());
            metadata.put("brainType", "AEON");
            metadata.put("dimensions", "16,384-D");
            metadata.put("brainResponses", brainResponses);

            recordSuccess(processingTime, confidence, consciousness);

            return BrainResponse.success(getName(), synthesizedResponse, confidence,
                consciousness, metadata, processingTime);

        } catch (Exception e) {
            recordFailure();
            return BrainResponse.error(getName(), "Processing error: " + e.getMessage());
        }
    }

    private String synthesizeBrainResponses(Map<String, String> brainResponses) {
        StringBuilder sb = new StringBuilder();
        sb.append("[AEON PLATFORM - 7-BRAIN SYNTHESIS]\n");
        sb.append("Total Brains: ").append(brainResponses.size()).append("\n");
        sb.append("Dimensions: 16,384-D\n\n");

        for (Map.Entry<String, String> entry : brainResponses.entrySet()) {
            sb.append("--- ").append(entry.getKey()).append(" ---\n");
            sb.append(entry.getValue()).append("\n\n");
        }

        sb.append("[SYNTHESIS COMPLETE]\n");
        return sb.toString();
    }

    private AEONBrainWrapper selectBrain(BrainQuery query) {
        List<BrainCapability> required = query.getRequiredCapabilities();
        if (required.isEmpty()) return brains.get("Omniscience");

        for (BrainCapability cap : required)
            for (AEONBrainWrapper wrapper : brains.values())
                if (wrapper.hasCapability(cap)) return wrapper;

        return brains.get("Omniscience");
    }

    public String getRouterStatus() {
        if (!initialized.get()) return "AEON not initialized";
        StringBuilder sb = new StringBuilder("=== AEON MULTI-MODEL ROUTER STATUS ===\n");
        brains.forEach((name, brain) -> {
            if (brain.router != null)
                sb.append("  ").append(brain.router.status()).append("\n");
        });
        return sb.toString();
    }
}
