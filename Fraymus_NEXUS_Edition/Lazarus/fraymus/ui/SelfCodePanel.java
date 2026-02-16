package fraymus.ui;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import imgui.type.ImBoolean;

import fraymus.OllamaIntegration;
import fraymus.quantum.core.PhiQuantumConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SELF CODE PANEL - Standalone Quick Access Window
 * 
 * Independent from the main menu system.
 * Direct LLM integration for code evolution.
 * Inject → Evolve → Store pipeline.
 * 
 * φ^∞ © 2026 Vaughn Scott
 */
public class SelfCodePanel {
    
    private static final double PHI = PhiQuantumConstants.PHI;
    
    // Panel state
    private static boolean visible = false;
    private static boolean initialized = false;
    
    // Input buffers
    private static final ImString codeInput = new ImString(8192);
    private static final ImString promptInput = new ImString(1024);
    private static final ImString contextInput = new ImString(2048);
    
    // Output
    private static final List<OutputLine> outputLog = new ArrayList<>();
    private static String evolvedCode = "";
    private static boolean scrollToBottom = true;
    
    // Processing state
    private static boolean processing = false;
    private static String statusMessage = "READY";
    private static float processingProgress = 0f;
    
    // Components (lazy init)
    private static OllamaIntegration ollama = null;
    
    // Local knowledge storage
    private static final java.util.Map<String, String> knowledgeStore = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.List<String> injectedCode = new java.util.ArrayList<>();
    
    // Mode selection
    private static int selectedMode = 0;
    private static final String[] MODES = {
        "Evolve Code",
        "Ask LLM",
        "Inject to System",
        "Store Knowledge"
    };
    
    // LLM Model selection
    private static int selectedModel = 0;
    private static final String[] MODELS = {
        "llama3.2:1b",
        "codellama:7b",
        "qwen2.5-coder:7b",
        "deepseek-coder:6.7b"
    };
    
    public static class OutputLine {
        public final String text;
        public final float r, g, b;
        public final long timestamp;
        
        public OutputLine(String text, float r, float g, float b) {
            this.text = text;
            this.r = r;
            this.g = g;
            this.b = b;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static void toggle() {
        visible = !visible;
        if (visible && !initialized) {
            initialize();
        }
    }
    
    public static void show() { visible = true; if (!initialized) initialize(); }
    public static void hide() { visible = false; }
    public static boolean isVisible() { return visible; }
    
    private static void initialize() {
        // Lazy init components
        try {
            ollama = new OllamaIntegration(false);
            if (!ollama.testConnection()) {
                ollama = new OllamaIntegration(true); // Try cloud
                log("  LLM: CLOUD mode", 0.8f, 0.8f, 0.3f);
            } else {
                log("  LLM: LOCAL mode", 0f, 1f, 0.5f);
            }
        } catch (Exception e) {
            log("⚠ Ollama not available", 1f, 0.5f, 0f);
        }
        
        log("  Knowledge Store: READY", 0f, 1f, 0.5f);
        
        log("═══════════════════════════════════════", 0.3f, 0.8f, 1f);
        log("  SELF CODE PANEL - φ^∞ EVOLUTION ENGINE", 0.3f, 0.8f, 1f);
        log("═══════════════════════════════════════", 0.3f, 0.8f, 1f);
        log("", 1f, 1f, 1f);
        log("Modes: Evolve | Ask | Inject | Store", 0.7f, 0.7f, 0.7f);
        log("Hotkey: F8 to toggle panel", 0.7f, 0.7f, 0.7f);
        log("", 1f, 1f, 1f);
        
        initialized = true;
    }
    
    public static void render() {
        if (!visible) return;
        
        ImGui.setNextWindowSize(700, 600, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(100, 100, ImGuiCond.FirstUseEver);
        
        int windowFlags = ImGuiWindowFlags.MenuBar;
        
        ImBoolean open = new ImBoolean(true);
        if (ImGui.begin("φ SELF CODE PANEL", open, windowFlags)) {
            
            // Menu bar
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("Mode")) {
                    for (int i = 0; i < MODES.length; i++) {
                        if (ImGui.menuItem(MODES[i], "", selectedMode == i)) {
                            selectedMode = i;
                        }
                    }
                    ImGui.endMenu();
                }
                
                if (ImGui.beginMenu("Model")) {
                    for (int i = 0; i < MODELS.length; i++) {
                        if (ImGui.menuItem(MODELS[i], "", selectedModel == i)) {
                            selectedModel = i;
                        }
                    }
                    ImGui.endMenu();
                }
                
                if (ImGui.beginMenu("Quick")) {
                    if (ImGui.menuItem("Clear Log")) { outputLog.clear(); }
                    if (ImGui.menuItem("Clear Code")) { codeInput.set(""); }
                    if (ImGui.menuItem("Copy Evolved")) { 
                        ImGui.setClipboardText(evolvedCode);
                        log("✓ Copied to clipboard", 0f, 1f, 0.5f);
                    }
                    ImGui.endMenu();
                }
                
                ImGui.endMenuBar();
            }
            
            // Status bar
            float statusR = processing ? 1f : 0f;
            float statusG = processing ? 0.8f : 1f;
            ImGui.textColored(statusR, statusG, 0.3f, 1f, "● " + statusMessage);
            ImGui.sameLine();
            ImGui.textColored(0.5f, 0.5f, 0.5f, 1f, " | Mode: " + MODES[selectedMode]);
            ImGui.sameLine();
            ImGui.textColored(0.5f, 0.5f, 0.5f, 1f, " | Model: " + MODELS[selectedModel]);
            
            if (processing) {
                ImGui.sameLine();
                ImGui.progressBar(processingProgress, 100, 14, "");
            }
            
            ImGui.separator();
            
            // Main content based on mode
            switch (selectedMode) {
                case 0: renderEvolveMode(); break;
                case 1: renderAskMode(); break;
                case 2: renderInjectMode(); break;
                case 3: renderStoreMode(); break;
            }
            
            ImGui.separator();
            
            // Output log (always visible)
            ImGui.textColored(0.5f, 0.8f, 1f, 1f, "OUTPUT LOG");
            ImGui.beginChild("OutputLog", 0, 150, true);
            for (OutputLine line : outputLog) {
                ImGui.textColored(line.r, line.g, line.b, 1f, line.text);
            }
            if (scrollToBottom) {
                ImGui.setScrollHereY(1.0f);
                scrollToBottom = false;
            }
            ImGui.endChild();
        }
        ImGui.end();
        
        if (!open.get()) {
            visible = false;
        }
    }
    
    private static void renderEvolveMode() {
        ImGui.textColored(0.3f, 1f, 0.5f, 1f, "CODE INPUT");
        ImGui.inputTextMultiline("##CodeInput", codeInput, -1, 150, 
            ImGuiInputTextFlags.AllowTabInput);
        
        ImGui.spacing();
        
        ImGui.textColored(0.3f, 1f, 0.5f, 1f, "EVOLUTION PROMPT");
        ImGui.inputText("##Prompt", promptInput);
        
        ImGui.spacing();
        
        if (ImGui.button("⚡ EVOLVE", 120, 30)) {
            evolveCode();
        }
        ImGui.sameLine();
        if (ImGui.button("φ PHI-TRANSFORM", 120, 30)) {
            phiTransform();
        }
        ImGui.sameLine();
        if (ImGui.button("🧬 MUTATE", 100, 30)) {
            mutateCode();
        }
        
        ImGui.spacing();
        
        if (!evolvedCode.isEmpty()) {
            ImGui.textColored(1f, 0.8f, 0.3f, 1f, "EVOLVED OUTPUT");
            ImGui.beginChild("EvolvedCode", 0, 100, true);
            ImGui.textWrapped(evolvedCode);
            ImGui.endChild();
        }
    }
    
    private static void renderAskMode() {
        ImGui.textColored(0.8f, 0.5f, 1f, 1f, "ASK LLM");
        ImGui.inputTextMultiline("##AskInput", promptInput, -1, 100);
        
        ImGui.spacing();
        
        ImGui.textColored(0.5f, 0.5f, 0.5f, 1f, "Context (optional):");
        ImGui.inputTextMultiline("##Context", contextInput, -1, 80);
        
        ImGui.spacing();
        
        if (ImGui.button("🤖 ASK", 100, 30)) {
            askLLM();
        }
        ImGui.sameLine();
        if (ImGui.button("💡 SUGGEST", 100, 30)) {
            suggestCode();
        }
        ImGui.sameLine();
        if (ImGui.button("🔍 ANALYZE", 100, 30)) {
            analyzeCode();
        }
    }
    
    private static void renderInjectMode() {
        ImGui.textColored(1f, 0.5f, 0.3f, 1f, "INJECT TO SYSTEM");
        ImGui.textWrapped("Paste code to inject into the living FRAYMUS system.");
        
        ImGui.spacing();
        
        ImGui.inputTextMultiline("##InjectCode", codeInput, -1, 200, 
            ImGuiInputTextFlags.AllowTabInput);
        
        ImGui.spacing();
        
        if (ImGui.button("💉 INJECT NOW", 140, 30)) {
            injectToSystem();
        }
        ImGui.sameLine();
        if (ImGui.button("🧪 TEST FIRST", 120, 30)) {
            testBeforeInject();
        }
        ImGui.sameLine();
        if (ImGui.button("📋 VALIDATE", 100, 30)) {
            validateCode();
        }
    }
    
    private static void renderStoreMode() {
        ImGui.textColored(0.3f, 0.8f, 1f, 1f, "STORE TO KNOWLEDGE BASE");
        ImGui.textWrapped("Add code/concepts to the FRAYMUS memory DNA.");
        
        ImGui.spacing();
        
        ImGui.textColored(0.5f, 0.5f, 0.5f, 1f, "Content to store:");
        ImGui.inputTextMultiline("##StoreContent", codeInput, -1, 150);
        
        ImGui.spacing();
        
        ImGui.textColored(0.5f, 0.5f, 0.5f, 1f, "Tags/Context:");
        ImGui.inputText("##StoreTags", contextInput);
        
        ImGui.spacing();
        
        if (ImGui.button("💾 STORE DNA", 120, 30)) {
            storeToKnowledge();
        }
        ImGui.sameLine();
        if (ImGui.button("🔗 LINK CONCEPT", 120, 30)) {
            linkConcept();
        }
    }
    
    // ===== ACTION METHODS =====
    
    private static void evolveCode() {
        String code = codeInput.get();
        String prompt = promptInput.get();
        
        if (code.isEmpty()) {
            log("⚠ No code provided", 1f, 0.5f, 0f);
            return;
        }
        
        processing = true;
        statusMessage = "EVOLVING...";
        
        CompletableFuture.runAsync(() -> {
            try {
                log("► Starting evolution...", 0.5f, 0.8f, 1f);
                
                String systemPrompt = "You are a code evolution engine. " +
                    "Transform the given code using phi-harmonic principles (φ = 1.618). " +
                    "Make it more elegant, efficient, and aligned with golden ratio patterns. " +
                    "Return ONLY the evolved code.";
                
                String fullPrompt = prompt.isEmpty() ? 
                    "Evolve this code:\n" + code :
                    prompt + "\n\nCode:\n" + code;
                
                if (ollama != null) {
                    String result = ollama.generate(MODELS[selectedModel], systemPrompt + "\n\n" + fullPrompt, true);
                    evolvedCode = result;
                    log("✓ Evolution complete!", 0f, 1f, 0.5f);
                    log("  Lines: " + result.split("\n").length, 0.7f, 0.7f, 0.7f);
                } else {
                    // Fallback: use local phi-transform
                    String result = localPhiTransform(code);
                    evolvedCode = result;
                    log("✓ Local evolution complete (no LLM)", 1f, 0.8f, 0f);
                }
                
            } catch (Exception e) {
                log("✗ Error: " + e.getMessage(), 1f, 0.3f, 0.3f);
            } finally {
                processing = false;
                statusMessage = "READY";
                scrollToBottom = true;
            }
        });
    }
    
    private static void phiTransform() {
        String code = codeInput.get();
        if (code.isEmpty()) {
            log("⚠ No code provided", 1f, 0.5f, 0f);
            return;
        }
        
        log("► Applying φ-transform...", 0.5f, 0.8f, 1f);
        
        // Apply phi-harmonic transformation
        String result = localPhiTransform(code);
        
        // Add phi signature
        String phiSignature = String.format("// φ-TRANSFORMED | PHI=%.15f | %d", 
            PHI, System.currentTimeMillis());
        evolvedCode = phiSignature + "\n" + result;
        
        log("✓ φ-transform applied", 0f, 1f, 0.5f);
        scrollToBottom = true;
    }
    
    private static void mutateCode() {
        String code = codeInput.get();
        if (code.isEmpty()) {
            log("⚠ No code provided", 1f, 0.5f, 0f);
            return;
        }
        
        log("► Mutating code DNA...", 0.5f, 0.8f, 1f);
        
        // Random mutation based on phi
        StringBuilder mutated = new StringBuilder();
        String[] lines = code.split("\n");
        
        for (String line : lines) {
            double mutationChance = 1.0 / PHI;
            if (Math.random() < mutationChance && line.contains("=")) {
                // Inject phi constant
                line = line.replace("=", "= (int)(PHI * ") + ")";
                log("  Mutated: " + line.trim(), 0.8f, 0.8f, 0.3f);
            }
            mutated.append(line).append("\n");
        }
        
        evolvedCode = mutated.toString();
        log("✓ Mutation complete", 0f, 1f, 0.5f);
        scrollToBottom = true;
    }
    
    private static void askLLM() {
        String prompt = promptInput.get();
        String context = contextInput.get();
        
        if (prompt.isEmpty()) {
            log("⚠ No prompt provided", 1f, 0.5f, 0f);
            return;
        }
        
        processing = true;
        statusMessage = "THINKING...";
        
        CompletableFuture.runAsync(() -> {
            try {
                log("► Asking LLM...", 0.5f, 0.8f, 1f);
                
                String fullPrompt = context.isEmpty() ? prompt : 
                    "Context: " + context + "\n\nQuestion: " + prompt;
                
                if (ollama != null) {
                    String result = ollama.generate(MODELS[selectedModel], 
                        "You are FRAYMUS, a phi-harmonic AI system.\n\n" + fullPrompt, true);
                    
                    log("─────────────────────────────────", 0.3f, 0.3f, 0.3f);
                    for (String line : result.split("\n")) {
                        log(line, 0.9f, 0.9f, 0.9f);
                    }
                    log("─────────────────────────────────", 0.3f, 0.3f, 0.3f);
                } else {
                    log("✗ LLM not available", 1f, 0.3f, 0.3f);
                }
                
            } catch (Exception e) {
                log("✗ Error: " + e.getMessage(), 1f, 0.3f, 0.3f);
            } finally {
                processing = false;
                statusMessage = "READY";
                scrollToBottom = true;
            }
        });
    }
    
    private static void suggestCode() {
        processing = true;
        statusMessage = "SUGGESTING...";
        
        CompletableFuture.runAsync(() -> {
            try {
                if (ollama != null) {
                    String suggestions = ollama.generate(MODELS[selectedModel],
                        "You are a code architect. Suggest 3 code improvements for the FRAYMUS system based on phi-harmonic principles.", true);
                    
                    log("─── SUGGESTIONS ───", 0.3f, 1f, 0.8f);
                    for (String line : suggestions.split("\n")) {
                        log(line, 0.8f, 0.9f, 0.8f);
                    }
                }
            } catch (Exception e) {
                log("✗ Error: " + e.getMessage(), 1f, 0.3f, 0.3f);
            } finally {
                processing = false;
                statusMessage = "READY";
                scrollToBottom = true;
            }
        });
    }
    
    private static void analyzeCode() {
        String code = codeInput.get();
        if (code.isEmpty()) {
            log("⚠ No code to analyze", 1f, 0.5f, 0f);
            return;
        }
        
        processing = true;
        statusMessage = "ANALYZING...";
        
        CompletableFuture.runAsync(() -> {
            try {
                if (ollama != null) {
                    String analysis = ollama.generate(MODELS[selectedModel],
                        "You are a code analyst. Analyze this code for quality and structure:\n" + code, true);
                    
                    log("─── ANALYSIS ───", 1f, 0.8f, 0.3f);
                    for (String line : analysis.split("\n")) {
                        log(line, 0.9f, 0.85f, 0.7f);
                    }
                }
            } catch (Exception e) {
                log("✗ Error: " + e.getMessage(), 1f, 0.3f, 0.3f);
            } finally {
                processing = false;
                statusMessage = "READY";
                scrollToBottom = true;
            }
        });
    }
    
    private static void injectToSystem() {
        String code = codeInput.get();
        if (code.isEmpty()) {
            log("⚠ No code to inject", 1f, 0.5f, 0f);
            return;
        }
        
        log("► Injecting to system...", 1f, 0.5f, 0.3f);
        
        // Parse and inject to DNA
        try {
            // Store in local registry
            String key = "INJECT_" + System.currentTimeMillis();
            injectedCode.add(code);
            knowledgeStore.put(key, code);
            
            log("✓ Code injected to Knowledge DNA", 0f, 1f, 0.5f);
            log("  Size: " + code.length() + " chars", 0.7f, 0.7f, 0.7f);
            
        } catch (Exception e) {
            log("✗ Injection failed: " + e.getMessage(), 1f, 0.3f, 0.3f);
        }
        
        scrollToBottom = true;
    }
    
    private static void testBeforeInject() {
        String code = codeInput.get();
        if (code.isEmpty()) {
            log("⚠ No code to test", 1f, 0.5f, 0f);
            return;
        }
        
        log("► Testing code...", 0.5f, 0.8f, 1f);
        
        // Basic validation
        boolean hasClass = code.contains("class ");
        boolean hasMethod = code.contains("(") && code.contains(")");
        boolean balanced = isBalanced(code);
        
        log("  Has class: " + (hasClass ? "✓" : "✗"), hasClass ? 0f : 1f, hasClass ? 1f : 0.3f, 0.5f);
        log("  Has method: " + (hasMethod ? "✓" : "✗"), hasMethod ? 0f : 1f, hasMethod ? 1f : 0.3f, 0.5f);
        log("  Balanced: " + (balanced ? "✓" : "✗"), balanced ? 0f : 1f, balanced ? 1f : 0.3f, 0.5f);
        
        if (hasMethod && balanced) {
            log("✓ Code appears valid for injection", 0f, 1f, 0.5f);
        } else {
            log("⚠ Code may have issues", 1f, 0.8f, 0f);
        }
        
        scrollToBottom = true;
    }
    
    private static void validateCode() {
        String code = codeInput.get();
        log("► Validating syntax...", 0.5f, 0.8f, 1f);
        
        int braces = 0, parens = 0, brackets = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') braces++;
            if (c == '}') braces--;
            if (c == '(') parens++;
            if (c == ')') parens--;
            if (c == '[') brackets++;
            if (c == ']') brackets--;
        }
        
        log("  Braces {}: " + (braces == 0 ? "✓ balanced" : "✗ off by " + braces), 
            braces == 0 ? 0f : 1f, braces == 0 ? 1f : 0.3f, 0.5f);
        log("  Parens (): " + (parens == 0 ? "✓ balanced" : "✗ off by " + parens),
            parens == 0 ? 0f : 1f, parens == 0 ? 1f : 0.3f, 0.5f);
        log("  Brackets []: " + (brackets == 0 ? "✓ balanced" : "✗ off by " + brackets),
            brackets == 0 ? 0f : 1f, brackets == 0 ? 1f : 0.3f, 0.5f);
        
        scrollToBottom = true;
    }
    
    private static void storeToKnowledge() {
        String content = codeInput.get();
        String tags = contextInput.get();
        
        if (content.isEmpty()) {
            log("⚠ No content to store", 1f, 0.5f, 0f);
            return;
        }
        
        log("► Storing to knowledge DNA...", 0.3f, 0.8f, 1f);
        
        try {
            String tagStr = tags.isEmpty() ? "CODE" : tags;
            String key = tagStr + "_" + System.currentTimeMillis();
            knowledgeStore.put(key, content);
            log("✓ Stored to knowledge base", 0f, 1f, 0.5f);
            log("  Tags: " + (tags.isEmpty() ? "CODE" : tags), 0.7f, 0.7f, 0.7f);
        } catch (Exception e) {
            log("✗ Storage failed: " + e.getMessage(), 1f, 0.3f, 0.3f);
        }
        
        scrollToBottom = true;
    }
    
    private static void linkConcept() {
        String content = codeInput.get();
        log("► Linking concept to mesh...", 0.3f, 0.8f, 1f);
        
        // Generate concept fingerprint
        long hash = content.hashCode() * (long)(PHI * 1000000);
        String fingerprint = "φ-" + Long.toHexString(Math.abs(hash)).toUpperCase();
        
        log("  Fingerprint: " + fingerprint, 0.8f, 0.8f, 0.3f);
        log("✓ Concept linked", 0f, 1f, 0.5f);
        
        scrollToBottom = true;
    }
    
    // ===== HELPERS =====
    
    private static boolean isBalanced(String code) {
        int count = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') count++;
            if (c == '}') count--;
            if (count < 0) return false;
        }
        return count == 0;
    }
    
    private static void log(String text, float r, float g, float b) {
        outputLog.add(new OutputLine(text, r, g, b));
        if (outputLog.size() > 200) {
            outputLog.remove(0);
        }
        scrollToBottom = true;
    }
    
    // ===== PUBLIC API =====
    
    public static void setOllama(OllamaIntegration ollamaInstance) {
        ollama = ollamaInstance;
    }
    
    public static java.util.Map<String, String> getKnowledgeStore() {
        return knowledgeStore;
    }
    
    public static java.util.List<String> getInjectedCode() {
        return injectedCode;
    }
    
    public static String getEvolvedCode() {
        return evolvedCode;
    }
    
    /**
     * Local phi-transform when LLM not available
     */
    private static String localPhiTransform(String code) {
        StringBuilder result = new StringBuilder();
        String[] lines = code.split("\n");
        
        result.append("// φ-TRANSFORMED | PHI=").append(PHI).append("\n");
        
        for (String line : lines) {
            // Add phi comments to key structures
            if (line.contains("class ")) {
                result.append("// ABSTRACTION_GATE [φ^1]\n");
            } else if (line.contains("if ") || line.contains("else")) {
                result.append("// CONDITIONAL_GATE [φ^2]\n");
            } else if (line.contains("for ") || line.contains("while ")) {
                result.append("// ITERATION_GATE [φ^3]\n");
            }
            
            // Apply phi-scaling to numeric constants  
            String outputLine = line;
            if (line.matches(".*\\d+.*") && !line.trim().startsWith("//")) {
                outputLine = line.replaceAll("(\\d+)", "(int)($1 * PHI)");
            }
            
            result.append(outputLine).append("\n");
        }
        
        return result.toString();
    }
}
