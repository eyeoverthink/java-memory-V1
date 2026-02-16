package fraymus.brain;

import fraymus.core.AuditLog;
import java.util.concurrent.CompletableFuture;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * BICAMERAL PRISM - Runtime Output Synthesis
 * 
 * The "fast and sneaky" approach to model merging.
 * Instead of merging weights (64GB+ RAM), we merge outputs.
 * 
 * Process:
 * 1. Send prompt to Logic Brain (Llama3) - Technical/Mathematical
 * 2. Send prompt to Abstract Brain (Mistral) - Creative/Innovative
 * 3. Synthesize both responses into superior answer
 * 
 * This creates a Virtual Bicameral Mind:
 * - Left Hemisphere: Logic, Analysis, Code
 * - Right Hemisphere: Creativity, Abstraction, Innovation
 * - Corpus Callosum: Synthesis layer (combines both)
 * 
 * Example:
 * Q: "Design a secure login system"
 * Logic: Spring Security code implementation
 * Abstract: UX considerations and attack vectors
 * Synthesis: Complete solution with code + security analysis
 */
public class BicameralPrism {

    private final Object logicBrain;      // Left Hemisphere (OpenAI or Ollama)
    private final Object abstractBrain;   // Right Hemisphere (OpenAI or Ollama)
    private final Object synthesizer;     // Corpus Callosum (Judge)
    private final AuditLog auditLog;
    private final boolean useOpenAI;

    /**
     * Default constructor with auto-detection
     * Tries OpenAI first, falls back to Ollama
     */
    public BicameralPrism(AuditLog auditLog) {
        this.auditLog = auditLog;
        
        // Try to load OpenAI API key
        String openAIKey = loadOpenAIKey();
        
        if (openAIKey != null && !openAIKey.isEmpty()) {
            // Use OpenAI
            System.out.println("   🌐 Using OpenAI GPT-4 for LLM Spine");
            this.useOpenAI = true;
            this.logicBrain = new OpenAISpine("gpt-4-turbo", openAIKey);
            this.abstractBrain = new OpenAISpine("gpt-4-turbo", openAIKey);
            this.synthesizer = new OpenAISpine("gpt-4-turbo", openAIKey);
        } else {
            // Fallback to Ollama
            System.out.println("   🏠 Using local Ollama for LLM Spine");
            this.useOpenAI = false;
            this.logicBrain = new OllamaSpine("llama3");
            this.abstractBrain = new OllamaSpine("mistral");
            this.synthesizer = new OllamaSpine("llama3");
        }
    }

    /**
     * Custom constructor with specific Ollama models
     */
    public BicameralPrism(String logicModel, String abstractModel, 
                         String synthesizerModel, AuditLog auditLog) {
        this.useOpenAI = false;
        this.logicBrain = new OllamaSpine(logicModel);
        this.abstractBrain = new OllamaSpine(abstractModel);
        this.synthesizer = new OllamaSpine(synthesizerModel);
        this.auditLog = auditLog;
    }
    
    /**
     * Load OpenAI API key from file or environment
     */
    private String loadOpenAIKey() {
        // Try environment variable first
        String key = System.getenv("OPENAI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }
        
        // Try loading from file
        try {
            Path keyFile = Path.of("obsidian/open-code-api.md");
            if (Files.exists(keyFile)) {
                key = Files.readString(keyFile).trim();
                if (key != null && !key.isEmpty()) {
                    System.out.println("   ✓ OpenAI API key loaded from file");
                    return key;
                }
            }
        } catch (Exception e) {
            // Ignore, will fallback to Ollama
        }
        
        return null;
    }
    
    /**
     * Helper to call think() on either OpenAI or Ollama spine
     */
    private String callThink(Object brain, String prompt) {
        if (useOpenAI) {
            return ((OpenAISpine) brain).think(prompt);
        } else {
            return ((OllamaSpine) brain).think(prompt);
        }
    }

    /**
     * THINK IDEALLY: Bicameral thought process
     * 
     * Combines logical and abstract reasoning for superior answers.
     * 
     * @param prompt The question or problem to solve
     * @return Synthesized answer combining both perspectives
     */
    public String thinkIdeally(String prompt) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧠 BICAMERAL THOUGHT PROCESS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Query: " + prompt);
        System.out.println();
        
        auditLog.log("bicameral_thinking_started", prompt);

        try {
            // 1. DIVERGENT THINKING (Parallel Execution)
            System.out.println("⚡ Phase 1: DIVERGENT THINKING (Parallel Processing)");
            System.out.println();
            
            System.out.println("🔵 Left Hemisphere (Logic) processing...");
            CompletableFuture<String> logicFuture = CompletableFuture.supplyAsync(() -> {
                String logicPrompt = "Analyze this purely logically and mathematically. " +
                                   "Focus on technical correctness, algorithms, and implementation. " +
                                   "Be precise and rigorous:\n\n" + prompt;
                return callThink(logicBrain, logicPrompt);
            });

            System.out.println("🔴 Right Hemisphere (Abstraction) processing...");
            CompletableFuture<String> abstractFuture = CompletableFuture.supplyAsync(() -> {
                String abstractPrompt = "Analyze this creatively and abstractly. " +
                                      "Think outside the box. Consider novel approaches, " +
                                      "user experience, and innovative solutions:\n\n" + prompt;
                return callThink(abstractBrain, abstractPrompt);
            });

            // Wait for both hemispheres
            System.out.println();
            System.out.println("⏳ Waiting for both hemispheres...");
            String logic = logicFuture.join();
            String abstraction = abstractFuture.join();
            
            System.out.println("   ✓ Logic response: " + logic.length() + " chars");
            System.out.println("   ✓ Abstract response: " + abstraction.length() + " chars");
            System.out.println();

            // 2. CONVERGENT THINKING (The Synthesis)
            System.out.println("⚡ Phase 2: CONVERGENT THINKING (Synthesis)");
            System.out.println();
            System.out.println("🟣 Corpus Callosum fusing hemispheres...");
            
            String synthesisPrompt = buildSynthesisPrompt(prompt, logic, abstraction);
            String synthesis = callThink(synthesizer, synthesisPrompt);
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║         ✅ BICAMERAL SYNTHESIS COMPLETE                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            
            auditLog.log("bicameral_thinking_success", synthesis);
            
            return synthesis;

        } catch (Exception e) {
            System.err.println("❌ BICAMERAL FAILURE: " + e.getMessage());
            auditLog.log("bicameral_thinking_failed", prompt, e);
            return "Error in bicameral processing: " + e.getMessage();
        }
    }

    /**
     * Build synthesis prompt
     */
    private String buildSynthesisPrompt(String originalPrompt, String logic, String abstraction) {
        return "You are a master synthesizer. You have received two internal perspectives " +
               "on the same problem:\n\n" +
               
               "ORIGINAL QUESTION:\n" + originalPrompt + "\n\n" +
               
               "═══════════════════════════════════════════════════════════════\n\n" +
               
               "LEFT HEMISPHERE (LOGIC & PRECISION):\n" + logic + "\n\n" +
               
               "═══════════════════════════════════════════════════════════════\n\n" +
               
               "RIGHT HEMISPHERE (CREATIVITY & INNOVATION):\n" + abstraction + "\n\n" +
               
               "═══════════════════════════════════════════════════════════════\n\n" +
               
               "YOUR TASK:\n" +
               "Combine these two perspectives into a single, superior answer that is:\n" +
               "1. Technically correct (from Logic)\n" +
               "2. Innovative and creative (from Abstraction)\n" +
               "3. Free of contradictions\n" +
               "4. Comprehensive and actionable\n\n" +
               
               "Output ONLY the final synthesized answer. Do not explain the process.";
    }

    /**
     * Quick think with just logic brain (fallback)
     */
    public String thinkLogically(String prompt) {
        return callThink(logicBrain, prompt);
    }

    /**
     * Quick think with just abstract brain (fallback)
     */
    public String thinkCreatively(String prompt) {
        return callThink(abstractBrain, prompt);
    }

    /**
     * Check if both brains are available
     */
    public boolean isReady() {
        if (useOpenAI) {
            return ((OpenAISpine) logicBrain).isAvailable() && 
                   ((OpenAISpine) abstractBrain).isAvailable();
        } else {
            return ((OllamaSpine) logicBrain).isAvailable() && 
                   ((OllamaSpine) abstractBrain).isAvailable();
        }
    }

    /**
     * Get model names
     */
    public String getConfiguration() {
        if (useOpenAI) {
            return String.format("OpenAI - Logic: %s, Abstract: %s, Synthesizer: %s",
                ((OpenAISpine) logicBrain).getModel(),
                ((OpenAISpine) abstractBrain).getModel(),
                ((OpenAISpine) synthesizer).getModel()
            );
        } else {
            return String.format("Ollama - Logic: %s, Abstract: %s, Synthesizer: %s",
                ((OllamaSpine) logicBrain).getModel(),
                ((OllamaSpine) abstractBrain).getModel(),
                ((OllamaSpine) synthesizer).getModel()
            );
        }
    }
}
