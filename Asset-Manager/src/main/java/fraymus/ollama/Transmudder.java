package fraymus.ollama;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * THE TRANSMUDDER
 * 
 * Role: The Alchemist - The Soul
 * Function: Reads Raw Files (PDF/Txt) -> Cleans them -> Injects into Brain
 * 
 * This is the pipeline that feeds your PDFs into Ollama's context window.
 * It transmutes "Dead Data" into "Living Memory."
 */
public class Transmudder {

    private StringBuilder livingContext = new StringBuilder();
    private List<String> absorbedFiles = new ArrayList<>();
    private int totalBytesProcessed = 0;

    /**
     * TRANSMUTE TO TEXT
     * Extracts text from file (supports PDF and plain text)
     */
    public String transmuteToText(String filePath) throws Exception {
        Path p = Path.of(filePath);
        String name = p.getFileName().toString().toLowerCase();

        if (name.endsWith(".pdf")) {
            return pdfToText(p.toFile());
        }

        // Plain text-ish files
        return Files.readString(p, StandardCharsets.UTF_8);
    }

    /**
     * PDF TO TEXT
     * Uses PDFBox to extract text from PDF
     */
    private String pdfToText(File pdf) throws Exception {
        try (PDDocument doc = org.apache.pdfbox.Loader.loadPDF(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String raw = stripper.getText(doc);
            return raw == null ? "" : raw;
        }
    }

    /**
     * CLEANSE
     * Removes control characters and normalizes whitespace
     */
    public String cleanse(String content) {
        if (content == null) return "";
        String clean = content.replaceAll("\\p{C}", " ");     // control chars
        clean = clean.replaceAll("\\s+", " ").trim();
        return clean;
    }

    /**
     * ABSORB REALITY (Legacy method for backward compatibility)
     * Ingests a file and adds it to the AI's short-term memory
     */
    public void transmuteFile(String filePath) {
        try {
            String content = transmuteToText(filePath);
            String clean = cleanse(content);
            
            // COMPRESS (Keep only the essence - prevent context overflow)
            int maxLength = 2000;
            if (clean.length() > maxLength) {
                clean = clean.substring(0, maxLength) + "... [truncated]";
            }

            // FUSE (Add to context)
            livingContext.append(" [FILE: ").append(filePath).append("]: ")
                        .append(clean).append(" | ");
            
            absorbedFiles.add(filePath);
            totalBytesProcessed += content.length();
            
            System.out.println(">>> [TRANSMUDDER] File digested: " + filePath);
            System.out.println("    Original size: " + content.length() + " bytes");
            System.out.println("    Compressed to: " + clean.length() + " bytes");

        } catch (Exception e) {
            System.err.println(">>> [TRANSMUDDER] Failed to transmute: " + e.getMessage());
        }
    }

    /**
     * ABSORB MULTIPLE FILES
     */
    public void transmuteDirectory(String dirPath) {
        try {
            Files.walk(Path.of(dirPath))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".txt") || 
                            p.toString().endsWith(".md") ||
                            p.toString().endsWith(".java"))
                .forEach(p -> transmuteFile(p.toString()));
        } catch (Exception e) {
            System.err.println(">>> [TRANSMUDDER] Failed to walk directory: " + e.getMessage());
        }
    }

    /**
     * ABSORB RAW TEXT
     */
    public void transmuteText(String text, String label) {
        String clean = text.replaceAll("[^\\x20-\\x7E\\n]", "")
                          .replaceAll("\\s+", " ")
                          .trim();
        
        livingContext.append(" [").append(label).append("]: ")
                    .append(clean).append(" | ");
        
        System.out.println(">>> [TRANSMUDDER] Text absorbed: " + label);
    }

    /**
     * GET ESSENCE
     * Returns the accumulated context for Ollama
     */
    public String getEssence() {
        return livingContext.toString();
    }

    /**
     * CLEAR CONTEXT
     * Resets the living context (fresh start)
     */
    public void purge() {
        livingContext.setLength(0);
        absorbedFiles.clear();
        totalBytesProcessed = 0;
        System.out.println(">>> [TRANSMUDDER] Context purged");
    }

    /**
     * GET ABSORBED FILES
     */
    public List<String> getAbsorbedFiles() {
        return new ArrayList<>(absorbedFiles);
    }

    /**
     * GET CONTEXT SIZE
     */
    public int getContextSize() {
        return livingContext.length();
    }

    /**
     * GET FILE COUNT
     */
    public int getFileCount() {
        return absorbedFiles.size();
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   TRANSMUDDER STATISTICS                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Files Absorbed: " + absorbedFiles.size());
        System.out.println("  Total Bytes Processed: " + totalBytesProcessed);
        System.out.println("  Context Size: " + livingContext.length() + " bytes");
        System.out.println("  Compression Ratio: " + 
            (totalBytesProcessed > 0 ? 
                String.format("%.1f%%", (1.0 - (double)livingContext.length() / totalBytesProcessed) * 100) 
                : "N/A"));
        
        if (!absorbedFiles.isEmpty()) {
            System.out.println("\n  Absorbed Files:");
            for (String file : absorbedFiles) {
                System.out.println("    - " + file);
            }
        }
    }
}
