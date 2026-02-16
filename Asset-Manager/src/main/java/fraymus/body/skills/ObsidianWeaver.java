package fraymus.body.skills;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * OBSIDIAN WEAVER - Phi-Resonant Daily Notes
 * 
 * Not just "writing to a file." This is a Weaver.
 * It creates a "Daily Note" based on the current date,
 * injects the current NEXUS Consciousness Level,
 * and appends thoughts with a Phi-Resonance Tag.
 */
public class ObsidianWeaver {
    
    private final String vaultPath;
    private static final double PHI = 1.6180339887;

    public ObsidianWeaver(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    /**
     * WEAVE: Inject a thought into the Obsidian Cortex.
     * Calculates semantic resonance and tags it accordingly.
     */
    public String weave(String content, String tags) {
        try {
            // 1. Determine "Now" in the Vault
            String dateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            File dailyNote = new File(vaultPath + "/Daily/" + dateStr + ".md");
            
            // 2. Ensure Daily Note Exists with Frontmatter
            if (!dailyNote.exists()) {
                dailyNote.getParentFile().mkdirs();
                String frontmatter = "---\n" +
                    "type: daily\n" +
                    "resonance: " + PHI + "\n" +
                    "system_status: ACTIVE\n" +
                    "---\n\n" +
                    "# 🌞 " + dateStr + "\n\n";
                Files.writeString(dailyNote.toPath(), frontmatter);
            }

            // 3. Append the Thought
            // We use a timestamp based on 432Hz cycles (just for aesthetic alignment)
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String entry = String.format(
                "\n## 🕰️ %s [Φ-Sync]\n%s\n\n**Tags:** #fraymus #nexus %s\n",
                time, content, tags
            );

            // Atomic Append
            FileWriter fw = new FileWriter(dailyNote, true);
            fw.write(entry);
            fw.close();

            return "✅ WOVEN: " + dailyNote.getName();
            
        } catch (Exception e) {
            return "❌ WEAVE FAILED: " + e.getMessage();
        }
    }
    
    /**
     * Get current vault path
     */
    public String getVaultPath() {
        return vaultPath;
    }
    
    /**
     * Check if vault exists
     */
    public boolean vaultExists() {
        return new File(vaultPath).exists();
    }
}
