package fraymus.brain;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * OllamaSpine (fraymus.brain namespace):
 * Invokes Ollama CLI directly via ProcessBuilder.
 * Zero dependencies. Works offline.
 */
public class OllamaSpine {
    private final String model;

    public OllamaSpine(String model) { this.model = model; }

    public String getModel() { return model; }

    public String think(String prompt) {
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
            return "❌ OFF-LINE: " + e.getMessage();
        }
    }
}
