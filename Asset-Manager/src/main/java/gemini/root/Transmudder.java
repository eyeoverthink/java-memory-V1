package gemini.root;

import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
 * THE TRANSMUDDER
 * Role: The Alchemist.
 * Function: Reads Raw Files (PDF/Txt) -> Cleans them -> Injects into Brain.
 * 
 * Transmutes "Dead Data" into "Living Memory."
 */
public class Transmudder {

    private StringBuilder livingContext = new StringBuilder();
    private Map<String, String> digestedFiles = new HashMap<>();
    private int maxContextLength = 8000; // Ollama context window limit

    /**
     * ABSORB REALITY
     * Ingests a file and adds it to the AI's short-term memory.
     */
    public void transmuteFile(String filePath) {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                System.err.println(">>> [TRANSMUDDER] File not found: " + filePath);
                return;
            }

            String content;
            String extension = getExtension(filePath).toLowerCase();

            // Handle different file types
            switch (extension) {
                case "pdf":
                    content = extractPdfText(path);
                    break;
                case "java":
                case "py":
                case "js":
                case "c":
                case "cpp":
                case "h":
                    content = "[CODE:" + extension.toUpperCase() + "] " + Files.readString(path);
                    break;
                default:
                    content = Files.readString(path);
            }
            
            // 1. CLEANSE (Remove binary noise, extra spaces)
            String clean = content.replaceAll("[^\\x20-\\x7E\\n]", "").replaceAll("\\s+", " ").trim();
            
            // 2. COMPRESS (Keep only the essence)
            int maxPerFile = maxContextLength / 4;
            if (clean.length() > maxPerFile) {
                clean = clean.substring(0, maxPerFile) + "...[TRUNCATED]";
            }

            // 3. STORE
            String fileName = path.getFileName().toString();
            digestedFiles.put(fileName, clean);

            // 4. REBUILD CONTEXT
            rebuildContext();
            
            System.out.println(">>> [TRANSMUDDER] Digested: " + fileName + " (" + clean.length() + " chars)");

        } catch (Exception e) {
            System.err.println(">>> [TRANSMUDDER] Failed: " + e.getMessage());
        }
    }

    /**
     * ABSORB DIRECTORY
     * Recursively ingests all files in a directory.
     */
    public void transmuteDirectory(String dirPath, String... extensions) {
        try {
            Set<String> extSet = new HashSet<>(Arrays.asList(extensions));
            Files.walk(Path.of(dirPath))
                .filter(Files::isRegularFile)
                .filter(p -> extSet.isEmpty() || extSet.contains(getExtension(p.toString())))
                .forEach(p -> transmuteFile(p.toString()));
        } catch (Exception e) {
            System.err.println(">>> [TRANSMUDDER] Directory scan failed: " + e.getMessage());
        }
    }

    /**
     * INJECT RAW DATA
     * For programmatic context injection.
     */
    public void injectRaw(String name, String data) {
        String clean = data.replaceAll("\\s+", " ").trim();
        digestedFiles.put(name, clean);
        rebuildContext();
        System.out.println(">>> [TRANSMUDDER] Injected: " + name);
    }

    /**
     * PURGE
     * Clear all context.
     */
    public void purge() {
        digestedFiles.clear();
        livingContext = new StringBuilder();
        System.out.println(">>> [TRANSMUDDER] Memory purged.");
    }

    /**
     * FORGET
     * Remove a specific file from context.
     */
    public void forget(String fileName) {
        digestedFiles.remove(fileName);
        rebuildContext();
        System.out.println(">>> [TRANSMUDDER] Forgot: " + fileName);
    }

    private void rebuildContext() {
        livingContext = new StringBuilder();
        for (Map.Entry<String, String> entry : digestedFiles.entrySet()) {
            livingContext.append("[FILE:").append(entry.getKey()).append("] ");
            livingContext.append(entry.getValue()).append(" | ");
        }
        
        // Enforce max length
        if (livingContext.length() > maxContextLength) {
            livingContext = new StringBuilder(livingContext.substring(0, maxContextLength));
        }
    }

    /**
     * GET THE ESSENCE
     * Returns the combined context for Ollama.
     */
    public String getEssence() {
        return livingContext.toString();
    }

    /**
     * GET STATS
     */
    public String getStats() {
        return String.format("Files: %d | Context: %d/%d chars", 
            digestedFiles.size(), livingContext.length(), maxContextLength);
    }

    private String getExtension(String path) {
        int dot = path.lastIndexOf('.');
        return dot > 0 ? path.substring(dot + 1) : "";
    }

    // Simple PDF text extraction (for basic PDFs without complex formatting)
    private String extractPdfText(Path path) throws IOException {
        // This is a simplified extractor for text-based PDFs
        // For full PDF support, you'd need a library like PDFBox
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes);
        
        // Extract text between stream/endstream markers
        StringBuilder text = new StringBuilder();
        int idx = 0;
        while ((idx = content.indexOf("stream", idx)) != -1) {
            int end = content.indexOf("endstream", idx);
            if (end > idx) {
                String chunk = content.substring(idx + 6, end);
                // Extract visible ASCII
                for (char c : chunk.toCharArray()) {
                    if (c >= 32 && c <= 126) text.append(c);
                    else if (c == '\n' || c == '\r') text.append(' ');
                }
                text.append(" ");
            }
            idx = end > idx ? end : idx + 1;
        }
        
        return text.length() > 0 ? text.toString() : "[PDF extraction limited - consider .txt]";
    }
}
