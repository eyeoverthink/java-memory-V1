package fraymus.nexus;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * OllamaBridge: Direct CLI bridge to Ollama.
 * Zero dependencies. ProcessBuilder only.
 * Used by PhilosophersStone for code generation and self-healing.
 */
public class OllamaBridge {

    private final String model;

    public OllamaBridge(String model) {
        this.model = model;
    }

    public String getModel() { return model; }

    /**
     * ASK: Send a prompt to the local Ollama instance and return the raw response.
     */
    public String ask(String prompt) {
        try {
            Process p = new ProcessBuilder("ollama", "run", model, prompt)
                    .redirectErrorStream(true).start();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
            }
            p.waitFor();
            return sb.toString().trim();
        } catch (Exception e) {
            return "// ERROR: Ollama offline — " + e.getMessage();
        }
    }
}
