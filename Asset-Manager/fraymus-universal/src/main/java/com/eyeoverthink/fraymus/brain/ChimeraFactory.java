package com.eyeoverthink.fraymus.brain;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ChimeraFactory:
 * - Generates MergeKit YAML for SLERP.
 * - Biases self_attn toward the Logic parent and mlp toward the Abstract parent.
 *
 * You still need:
 *   pip install mergekit
 *   (and access to HF-format model dirs or HF model IDs)
 */
public final class ChimeraFactory {

    /**
     * Merges two LLMs into a new one via MergeKit (SLERP).
     *
     * modelA = "Father" (logic), base model (HF repo id or local path)
     * modelB = "Mother" (abstraction)
     * logicWeight in [0.3, 0.7] typically
     */
    public void birthNewModel(String modelA, String modelB, String childName, double logicWeight) {
        try {
            double lw = clamp(logicWeight, 0.30, 0.70);
            double aw = 1.0 - lw;

            System.out.println("⚗️ CHIMERA FACTORY: " + modelA + " + " + modelB);
            System.out.println("   > Ratio: " + String.format("%.2f", lw) + " (Logic) / " + String.format("%.2f", aw) + " (Abstract)");

            String yaml = generateSlerpYaml(modelA, modelB, lw, aw);

            Path cfg = Path.of("merge_config.yaml");
            Files.writeString(cfg, yaml);

            System.out.println("   > Wrote: " + cfg.toAbsolutePath());

            System.out.println("\nNEXT (manual or OpenClaw):");
            System.out.println("1) mergekit-yaml merge_config.yaml ./merged_" + childName + " --copy-tokenizer");
            System.out.println("2) Convert HF -> GGUF (llama.cpp convert_hf_to_gguf.py) if you want Ollama GGUF");
            System.out.println("3) Create Modelfile and run: ollama create " + childName + " -f Modelfile");

            System.out.println("✅ BLUEPRINT READY: merge_config.yaml");

        } catch (Exception e) {
            System.err.println("❌ CHIMERA FACTORY ERROR: " + e.getMessage());
        }
    }

    /**
     * SLERP YAML:
     * - uses slices/sources
     * - uses per-component t filters (self_attn vs mlp)
     */
    private String generateSlerpYaml(String logicModel, String abstractModel, double lw, double aw) {
        return ""
                + "slices:\n"
                + "  - sources:\n"
                + "      - model: " + logicModel + "\n"
                + "        parameters:\n"
                + "          weight: " + lw + "\n"
                + "      - model: " + abstractModel + "\n"
                + "        parameters:\n"
                + "          weight: " + aw + "\n"
                + "merge_method: slerp\n"
                + "base_model: " + logicModel + "\n"
                + "parameters:\n"
                + "  t:\n"
                + "    - filter: self_attn\n"
                + "      value: [0.00, 0.15, 0.10, 0.20, 0.00]\n"
                + "    - filter: mlp\n"
                + "      value: [1.00, 0.85, 0.90, 0.80, 1.00]\n"
                + "    - value: 0.50\n"
                + "dtype: float16\n"
                + "tokenizer_source: " + logicModel + "\n";
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
