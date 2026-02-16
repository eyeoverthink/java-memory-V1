package fraymus.absorption;

import fraymus.knowledge.AkashicRecord;
import java.io.*;
import java.nio.file.*;

/**
 * FILE ABSORBER: THE FILE EATER
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Ingests local files and extracts knowledge:
 * - .txt, .md  → Raw text blocks
 * - .java      → Code structure extraction
 * - .html      → HTML parsing (like URLAbsorber)
 * - .json      → Structured data
 * - .pdf       → Text extraction (basic)
 * - .xml       → Tag-based parsing
 */
public class FileAbsorber {

    private AkashicRecord akashic;
    private int filesAbsorbed = 0;
    private long bytesConsumed = 0;

    public FileAbsorber() {
        this.akashic = new AkashicRecord();
    }

    public FileAbsorber(AkashicRecord sharedAkashic) {
        this.akashic = sharedAkashic;
    }

    public void absorb(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("   !! FILE NOT FOUND: " + filePath);
            return;
        }

        if (file.isDirectory()) {
            absorbDirectory(file);
            return;
        }

        String name = file.getName().toLowerCase();
        System.out.println("\n🔥 FILE ABSORBER: CONSUMING [" + file.getName() + "]");

        try {
            if (name.endsWith(".txt") || name.endsWith(".md")) {
                absorbText(file);
            } else if (name.endsWith(".java")) {
                absorbJavaSource(file);
            } else if (name.endsWith(".html") || name.endsWith(".htm")) {
                absorbHtml(file);
            } else if (name.endsWith(".json")) {
                absorbJson(file);
            } else if (name.endsWith(".xml")) {
                absorbXml(file);
            } else if (name.endsWith(".pdf")) {
                absorbPdf(file);
            } else {
                // Default: treat as text
                absorbText(file);
            }

            filesAbsorbed++;
            bytesConsumed += file.length();
            System.out.println("   ✓ ABSORBED: " + file.length() + " bytes");

        } catch (Exception e) {
            System.out.println("   !! ABSORPTION FAILED: " + e.getMessage());
        }
    }

    private void absorbDirectory(File dir) {
        System.out.println("\n🔥 FILE ABSORBER: SCANNING DIRECTORY [" + dir.getName() + "]");
        
        File[] files = dir.listFiles();
        if (files == null) return;

        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                absorb(file.getAbsolutePath());
                count++;
            }
        }
        System.out.println("   ✓ DIRECTORY SCAN COMPLETE: " + count + " files absorbed");
    }

    private void absorbText(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        
        // Split into chunks if large
        String[] paragraphs = content.split("\n\n+");
        
        for (String para : paragraphs) {
            para = para.trim();
            if (para.length() > 20) {
                akashic.addBlock("TEXT:" + file.getName(), para);
            }
        }
        
        System.out.println("   >> EXTRACTED: " + paragraphs.length + " text blocks");
    }

    private void absorbJavaSource(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        String[] lines = content.split("\n");

        String currentClass = file.getName().replace(".java", "");
        int methods = 0;
        int fields = 0;

        for (String line : lines) {
            line = line.trim();

            // Package
            if (line.startsWith("package ")) {
                akashic.addBlock("JAVA:PACKAGE", line);
            }
            // Class/Interface
            else if (line.contains("class ") || line.contains("interface ")) {
                akashic.addBlock("JAVA:CLASS", currentClass + " | " + line);
            }
            // Methods
            else if (line.contains("(") && line.contains(")") && 
                     (line.contains("public ") || line.contains("private ") || line.contains("protected ")) &&
                     !line.contains("class ") && !line.contains("new ")) {
                akashic.addBlock("JAVA:METHOD", currentClass + "." + extractMethodName(line));
                methods++;
            }
            // Fields
            else if ((line.contains("private ") || line.contains("public ")) && 
                     line.contains(";") && !line.contains("(")) {
                akashic.addBlock("JAVA:FIELD", currentClass + " | " + line);
                fields++;
            }
        }

        System.out.println("   >> EXTRACTED: " + methods + " methods, " + fields + " fields");
    }

    private String extractMethodName(String line) {
        int parenIdx = line.indexOf('(');
        if (parenIdx > 0) {
            String beforeParen = line.substring(0, parenIdx).trim();
            String[] parts = beforeParen.split("\\s+");
            if (parts.length > 0) {
                return parts[parts.length - 1];
            }
        }
        return "unknown";
    }

    private void absorbHtml(File file) throws IOException {
        String html = new String(Files.readAllBytes(file.toPath()));
        
        // Extract title
        String title = extractBetween(html, "<title>", "</title>");
        if (!title.isEmpty()) {
            akashic.addBlock("HTML:TITLE", title);
        }

        // Extract headers
        for (int i = 1; i <= 6; i++) {
            String tag = "h" + i;
            int idx = 0;
            while ((idx = html.indexOf("<" + tag, idx)) >= 0) {
                String header = extractBetween(html.substring(idx), "<" + tag, "</" + tag + ">");
                header = clean(header);
                if (!header.isEmpty()) {
                    akashic.addBlock("HTML:HEADER", header);
                }
                idx++;
            }
        }

        // Extract paragraphs
        int pIdx = 0;
        int pCount = 0;
        while ((pIdx = html.indexOf("<p", pIdx)) >= 0) {
            String para = extractBetween(html.substring(pIdx), "<p", "</p>");
            para = clean(para);
            if (para.length() > 30) {
                akashic.addBlock("HTML:PARAGRAPH", para);
                pCount++;
            }
            pIdx++;
        }

        System.out.println("   >> EXTRACTED: title + headers + " + pCount + " paragraphs");
    }

    private void absorbJson(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        
        // Store the raw JSON as a single block
        akashic.addBlock("JSON:" + file.getName(), content);
        
        // Extract key-value pairs (simple parsing)
        int pairs = 0;
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(":") && line.contains("\"")) {
                akashic.addBlock("JSON:ENTRY", line);
                pairs++;
            }
        }

        System.out.println("   >> EXTRACTED: " + pairs + " key-value pairs");
    }

    private void absorbXml(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        
        // Store raw XML
        akashic.addBlock("XML:" + file.getName(), content);

        // Extract element names
        int elements = 0;
        int idx = 0;
        while ((idx = content.indexOf("<", idx)) >= 0) {
            int end = content.indexOf(">", idx);
            if (end > idx) {
                String tag = content.substring(idx + 1, end).trim();
                if (!tag.startsWith("/") && !tag.startsWith("?") && !tag.startsWith("!")) {
                    String tagName = tag.split("\\s+")[0];
                    akashic.addBlock("XML:ELEMENT", tagName);
                    elements++;
                }
            }
            idx++;
        }

        System.out.println("   >> EXTRACTED: " + elements + " XML elements");
    }

    private void absorbPdf(File file) {
        // Basic PDF text extraction (without external libs)
        // For full PDF support, would need Apache PDFBox
        System.out.println("   !! PDF PARSING REQUIRES EXTERNAL LIBRARY (Apache PDFBox)");
        System.out.println("   >> Storing file reference only.");
        akashic.addBlock("PDF:REFERENCE", "File: " + file.getAbsolutePath() + " | Size: " + file.length());
    }

    private String extractBetween(String text, String start, String end) {
        int s = text.indexOf(start);
        if (s < 0) return "";
        s = text.indexOf(">", s) + 1;
        int e = text.indexOf(end, s);
        if (e < 0) return "";
        return text.substring(s, e);
    }

    private String clean(String dirty) {
        return dirty.replaceAll("<[^>]+>", "")
                    .replaceAll("&[a-z]+;", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
    }

    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ FILE ABSORBER STATISTICS                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Files Absorbed:      " + String.format("%-36d", filesAbsorbed) + "│");
        System.out.println("│ Bytes Consumed:      " + String.format("%-36d", bytesConsumed) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              FILE ABSORBER - THE LOCAL EATER                  ║");
        System.out.println("║         \"Drop files in. Knowledge comes out.\"                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        FileAbsorber eater = new FileAbsorber();

        // Test with a Java file
        if (args.length > 0) {
            eater.absorb(args[0]);
        } else {
            System.out.println("Usage: FileAbsorber <file_or_directory>");
            System.out.println("Supported: .txt, .md, .java, .html, .json, .xml, .pdf");
        }

        eater.printStats();
    }
}
