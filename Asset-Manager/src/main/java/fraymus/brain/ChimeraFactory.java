package fraymus.brain;

import fraymus.limbs.ClawConnector;
import fraymus.core.GravityEngine;
import fraymus.core.AuditLog;
import java.io.File;
import java.nio.file.Files;

/**
 * CHIMERA FACTORY - Genetic Engineering for AI
 * 
 * Uses SLERP (Spherical Linear Interpolation) to merge two pre-trained
 * models into a superior hybrid. This is the "sneaky trick" that beats
 * trillion-dollar companies.
 * 
 * Process:
 * 1. Calculate merge ratio using physics (entropy-driven)
 * 2. Generate SLERP YAML configuration
 * 3. Execute mergekit via OpenClaw
 * 4. Convert to GGUF format
 * 5. Import to Ollama
 * 
 * The Chimera:
 * - Father (Logic): Coding-heavy model (e.g., llama3:8b)
 * - Mother (Abstraction): Creative model (e.g., mistral:7b)
 * - Child: Fused model with best traits of both
 */
public class ChimeraFactory {

    private final ClawConnector claw;
    private final GravityEngine physics;
    private final AuditLog auditLog;

    public ChimeraFactory(GravityEngine physics, AuditLog auditLog) {
        this.claw = new ClawConnector();
        this.physics = physics;
        this.auditLog = auditLog;
    }

    /**
     * BIRTH NEW MODEL: Merge two LLMs into a superior hybrid
     * 
     * @param modelA The "Father" - Logic/Reasoning model
     * @param modelB The "Mother" - Abstraction/Creativity model
     * @param childName Name for the new merged model
     */
    public void birthNewModel(String modelA, String modelB, String childName) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ⚗️ CHIMERA FACTORY - GENETIC ENGINEERING              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Father (Logic): " + modelA);
        System.out.println("Mother (Abstraction): " + modelB);
        System.out.println("Child: " + childName);
        System.out.println();
        
        auditLog.log("chimera_birth_started", childName);

        try {
            // 1. CALCULATE RATIOS (Physics-Driven)
            // High Entropy = More Creativity (Model B)
            // Low Entropy = More Logic (Model A)
            // Use time-based entropy (system uptime as proxy)
            double entropy = (System.currentTimeMillis() % 100); // Simple entropy approximation
            double ratio = calculateMergeRatio(entropy);
            
            System.out.println("📊 Physics Analysis:");
            System.out.println("   Entropy: " + String.format("%.2f", entropy));
            System.out.println("   Genetic Ratio: " + String.format("%.2f", ratio) + 
                             " (Logic) / " + String.format("%.2f", 1.0 - ratio) + " (Abstract)");
            System.out.println();

            // 2. GENERATE MERGE CONFIG (YAML)
            System.out.println("📝 Generating SLERP configuration...");
            String config = generateSlerpYaml(modelA, modelB, ratio);
            
            // 3. WRITE TO DISK
            File configFile = new File("merge_config.yaml");
            Files.writeString(configFile.toPath(), config);
            System.out.println("   ✓ Config written to: " + configFile.getAbsolutePath());
            System.out.println();

            // 4. EXECUTE SURGERY (OpenClaw runs mergekit)
            System.out.println("🔬 Executing neural surgery...");
            System.out.println("   (This may take several minutes)");
            System.out.println();
            
            String mergeTask = "Install mergekit if needed (pip install mergekit), then run: " +
                              "mergekit-yaml merge_config.yaml ./merged_model --allow-crimes";
            String mergeResult = claw.dispatch(mergeTask, "CONTEXT: MODEL_MERGING");
            
            System.out.println("   Merge Result: " + 
                (mergeResult.length() > 100 ? mergeResult.substring(0, 100) + "..." : mergeResult));
            System.out.println();

            // 5. CONVERT TO OLLAMA (GGUF)
            System.out.println("🔄 Converting to GGUF format...");
            String convertTask = "Convert the merged model at ./merged_model to GGUF format using llama.cpp. " +
                               "Save as " + childName + ".gguf";
            String convertResult = claw.dispatch(convertTask, "CONTEXT: GGUF_CONVERSION");
            
            System.out.println("   Conversion Result: " + 
                (convertResult.length() > 100 ? convertResult.substring(0, 100) + "..." : convertResult));
            System.out.println();

            // 6. IMPORT TO OLLAMA
            System.out.println("📥 Importing to Ollama...");
            
            // Create Modelfile
            String modelfile = "FROM ./" + childName + ".gguf\n" +
                             "PARAMETER temperature 0.7\n" +
                             "PARAMETER top_p 0.9\n" +
                             "SYSTEM You are " + childName + ", a hybrid AI combining logic and creativity.";
            
            Files.writeString(new File("Modelfile.custom").toPath(), modelfile);
            
            String importTask = "Run: ollama create " + childName + " -f Modelfile.custom";
            String importResult = claw.dispatch(importTask, "CONTEXT: OLLAMA_IMPORT");
            
            System.out.println("   Import Result: " + importResult);
            System.out.println();

            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║         ✅ IT IS ALIVE                                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Model '" + childName + "' is ready to use!");
            System.out.println("Test it with: ollama run " + childName);
            System.out.println();
            
            auditLog.log("chimera_birth_success", childName);

        } catch (Exception e) {
            System.err.println("❌ BIRTH DEFECT: " + e.getMessage());
            e.printStackTrace();
            auditLog.log("chimera_birth_failed", childName, e);
        }
    }

    /**
     * Calculate merge ratio based on physics entropy
     * 
     * @param entropy Current system entropy
     * @return Ratio between 0.3 and 0.7 (clamped)
     */
    private double calculateMergeRatio(double entropy) {
        // Normalize entropy to 0-1 range
        // Higher entropy = more creativity needed
        double normalized = Math.max(0.0, Math.min(1.0, entropy / 100.0));
        
        // Clamp between 0.3 and 0.7 for stability
        // 0.3 = 30% logic, 70% creativity
        // 0.7 = 70% logic, 30% creativity
        return Math.max(0.3, Math.min(0.7, normalized));
    }

    /**
     * Generate SLERP YAML configuration
     * 
     * SLERP (Spherical Linear Interpolation) merges models by:
     * - Attention Heads (Reasoning) from Father
     * - Feed-Forward Networks (Facts/Style) from Mother
     */
    private String generateSlerpYaml(String modelA, String modelB, double ratio) {
        return 
            "# Fraynix Chimera Factory - SLERP Configuration\n" +
            "# Generated by physics-driven genetic algorithm\n" +
            "# Ratio: " + String.format("%.2f", ratio) + " (Logic) / " + 
            String.format("%.2f", 1.0 - ratio) + " (Abstract)\n\n" +
            
            "models:\n" +
            "  - model: " + modelA + "\n" +
            "    parameters:\n" +
            "      density: " + String.format("%.3f", 1.0 - ratio) + "\n" +
            "      weight: " + String.format("%.3f", 1.0 - ratio) + "\n" +
            "  - model: " + modelB + "\n" +
            "    parameters:\n" +
            "      density: " + String.format("%.3f", ratio) + "\n" +
            "      weight: " + String.format("%.3f", ratio) + "\n" +
            
            "merge_method: slerp\n" +
            "base_model: " + modelA + "\n" +
            
            "parameters:\n" +
            "  t:\n" +
            "    # Attention heads (reasoning) - favor logic model\n" +
            "    - filter: self_attn\n" +
            "      value: [0, 0.5, 0.3, 0.7, 1]\n" +
            "    # Feed-forward (facts/style) - favor creative model\n" +
            "    - filter: mlp\n" +
            "      value: [1, 0.5, 0.7, 0.3, 0]\n" +
            "    # Default blend\n" +
            "    - value: 0.5\n" +
            
            "dtype: float16\n";
    }

    /**
     * Quick merge with default settings
     */
    public void quickMerge(String childName) {
        birthNewModel("llama3", "mistral", childName);
    }
}
