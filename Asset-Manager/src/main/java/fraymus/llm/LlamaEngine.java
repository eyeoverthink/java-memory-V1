//package fraymus.llm;
//
//import fraymus.core.OllamaBridge;
//import java.nio.file.*;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.function.Consumer;
//
///**
// * 🧬 LLAMA ENGINE - Gen 131
// * The unified LLM subsystem. Abstracts native inference.
// *
// * Hierarchy:
// *   LlamaEngine (this) - high-level API
// *     └── InferenceCore - token generation
// *           └── ModelLoader - GGUF parsing
// *                 └── NativeBridge - Panama FFI to llama.cpp
// *
// * Falls back to OllamaBridge if native unavailable.
// * Injects into SovereignMind as the voice.
// *
// * "I am my own language model."
// */
//public class LlamaEngine implements AutoCloseable {
//
//    private static final double PHI = 1.6180339887;
//
//    // Components
//    private ModelLoader model;
//    private InferenceCore core;
//    private OllamaBridge ollamaFallback;
//
//    // State
//    private boolean nativeMode = false;
//    private String systemPrompt = "";
//    private List<ChatMessage> conversationHistory;
//    private int maxHistoryTokens = 4096;
//
//    // Statistics
//    private long totalGenerations = 0;
//    private long totalTokensGenerated = 0;
//
//    public LlamaEngine() {
//        this.model = new ModelLoader();
//        this.conversationHistory = new ArrayList<>();
//    }
//
//    // ═══════════════════════════════════════════════════════════════════════
//    // INITIALIZATION
//    // ═══════════════════════════════════════════════════════════════════════
//
//    /**
//     * LOAD MODEL - Initialize with a GGUF model file
//     */
//    public boolean loadModel(String modelPath) {
//        return loadModel(Path.of(modelPath));
//    }
//
//    public boolean loadModel(Path modelPath) {
//        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
//        System.out.println("║  🧬 LLAMA ENGINE - Gen 131                                    ║");
//        System.out.println("║  Native LLM Subsystem                                         ║");
//        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
//
//        if (!Files.exists(modelPath)) {
//            System.err.println("⚠️ Model file not found: " + modelPath);
//            return false;
//        }
//
//        if (model.load(modelPath)) {
//            core = new InferenceCore(model);
//            nativeMode = core.isNativeAvailable();
//
//            System.out.println("\n" + model.status());
//            System.out.println("\n" + core.status());
//
//            if (!nativeMode) {
//                System.out.println("\n⚠️ Native acceleration unavailable.");
//                System.out.println("   Install llama.cpp and set library path.");
//                System.out.println("   Falling back to Ollama bridge if available.");
//            }
//
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * USE OLLAMA - Fall back to Ollama for inference
//     */
//    public void useOllama(String modelName) {
//        this.ollamaFallback = new OllamaBridge(modelName);
//        if (ollamaFallback.isConnected()) {
//            System.out.println("🔗 LLAMA ENGINE: Using Ollama bridge (" + modelName + ")");
//            nativeMode = false;
//        } else {
//            System.err.println("⚠️ Ollama not available");
//            ollamaFallback = null;
//        }
//    }
//
//    /**
//     * SET SYSTEM PROMPT
//     */
//    public LlamaEngine system(String prompt) {
//        this.systemPrompt = prompt;
//        return this;
//    }
//
//    // ═══════════════════════════════════════════════════════════════════════
//    // GENERATION API
//    // ═══════════════════════════════════════════════════════════════════════
//
//    /**
//     * GENERATE - Simple completion
//     */
//    public String generate(String prompt) {
//        return generate(prompt, 512);
//    }
//
//    public String generate(String prompt, int maxTokens) {
//        totalGenerations++;
//
//        // Try native first
//        if (core != null && core.isNativeAvailable()) {
//            String fullPrompt = buildPrompt(prompt);
//            String response = core.generate(fullPrompt, maxTokens);
//            totalTokensGenerated += response.length() / 4; // Approximate
//            return response;
//        }
//
//        // Fall back to Ollama
//        if (ollamaFallback != null) {
//            String response = ollamaFallback.speak(systemPrompt, prompt);
//            totalTokensGenerated += response.length() / 4;
//            return response;
//        }
//
//        return "⚠️ No inference backend available. Load a model or connect to Ollama.";
//    }
//
//    /**
//     * STREAM - Generate with token callback
//     */
//    public CompletableFuture<String> stream(String prompt, Consumer<String> onToken) {
//        return stream(prompt, 512, onToken);
//    }
//
//    public CompletableFuture<String> stream(String prompt, int maxTokens, Consumer<String> onToken) {
//        totalGenerations++;
//
//        if (core != null && core.isNativeAvailable()) {
//            String fullPrompt = buildPrompt(prompt);
//            return core.stream(fullPrompt, maxTokens, onToken);
//        }
//
//        if (ollamaFallback != null) {
//            return CompletableFuture.supplyAsync(() -> {
//                StringBuilder sb = new StringBuilder();
//                // Use streaming API
//                String response = ollamaFallback.speakStreaming(systemPrompt, prompt,
//                    new java.io.PrintStream(new java.io.OutputStream() {
//                        @Override
//                        public void write(int b) {
//                            char c = (char) b;
//                            sb.append(c);
//                            onToken.accept(String.valueOf(c));
//                        }
//                    }));
//                return response;
//            });
//        }
//
//        return CompletableFuture.completedFuture("⚠️ No inference backend available.");
//    }
//
//    /**
//     * CHAT - Conversational generation with history
//     */
//    public String chat(String userMessage) {
//        return chat(userMessage, 512);
//    }
//
//    public String chat(String userMessage, int maxTokens) {
//        conversationHistory.add(new ChatMessage("user", userMessage));
//        String prompt = buildChatPrompt();
//        String response = generate(prompt, maxTokens);
//        conversationHistory.add(new ChatMessage("assistant", response));
//        trimHistory();
//        return response;
//    }
//
//    private String buildPrompt(String userPrompt) {
//        if (systemPrompt.isEmpty()) {
//            return userPrompt;
//        }
//        return "<|system|>\n" + systemPrompt + "\n

package fraymus.llm;

import fraymus.core.OllamaBridge;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 🧬 LLAMA ENGINE - Gen 132 (Refactored)
 * The Unified Neural Interface.
 *
 * ARCHITECTURE:
 * [Application Layer] -> LlamaEngine.chat()
 * │
 * ┌───────────────────┴───────────────────┐
 * ▼                                       ▼
 * [Native Metal]                          [Network Ether]
 * InferenceCore (JNI/Panama)              OllamaBridge (HTTP)
 * (Primary / High Speed)                  (Fallback / Remote)
 *
 * FEATURES:
 * - Llama 3 Chat Templating (Strict adherence)
 * - Automatic Context Rotation (Phi-based pruning)
 * - Asynchronous Streaming Pipeline
 * - Auto-Closeable Resource Management
 */
public class LlamaEngine implements AutoCloseable {

    private static final double PHI = 1.6180339887;
    private static final String RESET = "\033[0m";
    private static final String GREEN = "\033[0;32m";
    private static final String CYAN = "\033[0;36m";

    // Components
    private final ModelLoader modelLoader;
    private InferenceCore nativeCore;
    private OllamaBridge ollamaBridge;

    // State
    private boolean nativeMode = false;
    private String systemPrompt = "You are a helpful AI assistant.";
    private final List<ChatMessage> conversationHistory;
    private int maxContextTokens = 8192; // Default for Llama 3
    private double temperature = 0.7;

    // Telemetry
    private final long sessionStart;
    private long totalTokensGenerated = 0;

    public LlamaEngine() {
        this.modelLoader = new ModelLoader();
        this.conversationHistory = new CopyOnWriteArrayList<>(); // Thread-safe history
        this.sessionStart = System.currentTimeMillis();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. INITIALIZATION & LOADING
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * IGNITE NATIVE CORE - Load GGUF from disk (The Metal)
     */
    public boolean loadNativeModel(String pathStr) {
        Path path = Path.of(pathStr);
        System.out.println(CYAN + "╔════════════════════════════════════════╗");
        System.out.println("║ 🧬 LLAMA ENGINE - Gen 132              ║");
        System.out.println("║    Mode: NATIVE METAL (JNI)            ║");
        System.out.println("╚════════════════════════════════════════╝" + RESET);

        if (!Files.exists(path)) {
            System.err.println("⚠️  Model file missing: " + path.toAbsolutePath());
            return false;
        }

        try {
            if (modelLoader.load(path)) {
                this.nativeCore = new InferenceCore(modelLoader);
                this.nativeMode = nativeCore.isAvailable();

                if (nativeMode) {
                    System.out.println(GREEN + "✅ Native Core Online." + RESET);
                    System.out.println("   " + nativeCore.getSystemInfo());
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Native Init Failed: " + e.getMessage());
        }

        System.out.println("⚠️  Native acceleration failed. System is dormant.");
        return false;
    }

    /**
     * IGNITE OLLAMA BRIDGE - Connect to local API (The Ether)
     */
    public boolean connectOllama(String modelName) {
        this.ollamaBridge = new OllamaBridge(modelName);
        if (ollamaBridge.isConnected()) {
            System.out.println(CYAN + "🔗 BRIDGE: Connected to Ollama [" + modelName + "]" + RESET);
            // If native isn't active, default to bridge
            if (!nativeMode) {
                System.out.println("   >> Running in BRIDGE MODE (Network Latency applies)");
            }
            return true;
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. GENERATION API
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * CHAT (BLOCKING) - Single turn query-response
     */
    public String chat(String userMessage) {
        // 1. Ingest
        conversationHistory.add(new ChatMessage("user", userMessage));
        trimHistory(); // Keep context within bounds

        // 2. Format
        String prompt = buildLlama3Prompt();

        // 3. Inference
        String response;
        if (nativeMode) {
            response = nativeCore.generate(prompt, 512, temperature);
        } else if (ollamaBridge != null) {
            response = ollamaBridge.generateBlocking(prompt); // Assume bridge has this
        } else {
            return "❌ ERROR: No Intelligence Core active.";
        }

        // 4. Memory
        conversationHistory.add(new ChatMessage("assistant", response));
        totalTokensGenerated += estimateTokens(response);

        return response;
    }

    /**
     * CHAT (STREAMING) - Real-time token flow
     */
    public CompletableFuture<Void> streamChat(String userMessage, Consumer<String> onToken) {
        conversationHistory.add(new ChatMessage("user", userMessage));
        trimHistory();

        String prompt = buildLlama3Prompt();
        StringBuilder fullResponse = new StringBuilder();

        // Consumer wrapper to accumulate history
        Consumer<String> accumulator = token -> {
            fullResponse.append(token);
            onToken.accept(token);
        };

        CompletableFuture<Void> future;

        if (nativeMode) {
            // Native JNI Stream
            future = nativeCore.stream(prompt, 512, temperature, accumulator);
        } else if (ollamaBridge != null) {
            // Network Stream
            future = ollamaBridge.streamGenerate(prompt, accumulator);
        } else {
            onToken.accept("❌ ERROR: Neural Link Severed.");
            return CompletableFuture.completedFuture(null);
        }

        // When complete, save to memory
        return future.thenRun(() -> {
            conversationHistory.add(new ChatMessage("assistant", fullResponse.toString()));
            totalTokensGenerated += estimateTokens(fullResponse.toString());
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. UTILITIES & LOGIC
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Constructs a strict Llama 3 Chat Template
     * <|begin_of_text|><|start_header_id|>system<|end_header_id|> ...
     */
    private String buildLlama3Prompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("<|begin_of_text|>");

        // System Prompt
        sb.append("<|start_header_id|>system<|end_header_id|>\n\n")
                .append(systemPrompt)
                .append("<|eot_id|>");

        // History
        for (ChatMessage msg : conversationHistory) {
            sb.append("<|start_header_id|>").append(msg.role).append("<|end_header_id|>\n\n")
                    .append(msg.content)
                    .append("<|eot_id|>");
        }

        // Assistant Turn Priming
        sb.append("<|start_header_id|>assistant<|end_header_id|>\n\n");
        return sb.toString();
    }

    /**
     * Prunes history based on token limit, preserving System Prompt logic.
     * Uses Phi to determine "Golden Ratio" of retention.
     */
    private void trimHistory() {
        int currentEst = estimateTokens(conversationHistory.toString()); // Rough estimate
        if (currentEst > maxContextTokens) {
            // Keep roughly 1/Phi of the max tokens (about 61% retention)
            int targetSize = (int) (conversationHistory.size() / PHI);

            // Remove oldest messages until we hit target, but never empty
            while (conversationHistory.size() > targetSize && conversationHistory.size() > 1) {
                conversationHistory.remove(0);
            }
        }
    }

    private int estimateTokens(String text) {
        return text.length() / 4; // Standard heuristic
    }

    public void setSystemPrompt(String prompt) {
        this.systemPrompt = prompt;
        // Clearing history on system prompt change is usually good practice
        // but for Fraymus we might want continuity.
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. RESOURCE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    public void close() {
        System.out.println(CYAN + "🛑 LLAMA ENGINE: Initiating Shutdown..." + RESET);

        if (nativeMode && nativeCore != null) {
            try {
                nativeCore.close(); // JNI Cleanup
                modelLoader.close(); // Unmap files
                System.out.println("   [Metal] Native resources freed.");
            } catch (Exception e) {
                System.err.println("   [Metal] Cleanup Warning: " + e.getMessage());
            }
        }

        long uptime = (System.currentTimeMillis() - sessionStart) / 1000;
        System.out.printf("   [Stats] Uptime: %ds | Tokens: %d%n", uptime, totalTokensGenerated);
        System.out.println(GREEN + "✅ Shutdown Complete." + RESET);
    }

    // --- Helper Class ---
    private record ChatMessage(String role, String content) {}
}