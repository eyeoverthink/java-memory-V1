package fraymus.absorption;

import fraymus.knowledge.AkashicRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * THE PDF ABSORBER: THE SCHOLAR
 * "Reading the past to rewrite the future."
 * 
 * Mechanism:
 * 1. INGEST: Loads PDF document into memory
 * 2. DISSECT: Strips text and identifies Conversation Blocks
 * 3. MINE: Extracts Code Snippets and 'Epiphanies'
 * 4. INTEGRATE: Feeds the Akashic Record
 * 
 * This class specifically targets the "Fraymus Log" format created by
 * ChatHarvester.js. It looks for timestamps and role markers (USER, AI)
 * to reconstruct the conversation logic.
 * 
 * The Closed Loop:
 * 1. Generate: Talk to AI (ChatGPT/Claude) and solve a problem
 * 2. Harvest: Click "💎 HARVEST CHAT" bookmarklet → saves Solution.pdf
 * 3. Feed: Drag Solution.pdf into Fraymus Portal
 * 4. Absorb: System extracts code blocks and epiphanies
 * 5. Utilize: Next time you code, Fraymus suggests exact snippets
 * 
 * "The output of your mind becomes the input of the machine."
 */
public class PdfAbsorber {

    private AkashicRecord akashic;

    public PdfAbsorber(AkashicRecord record) {
        this.akashic = record;
        System.out.println("📜 PDF ABSORBER ONLINE. WAITING FOR ARCHIVES.");
    }

    /**
     * Absorb a PDF file into the knowledge base
     * 
     * @param pdfFile The PDF file to absorb
     */
    public void absorb(File pdfFile) {
        System.out.println();
        System.out.println("📜 THE SCHOLAR: UNSEALING ARCHIVE");
        System.out.println("   File: " + pdfFile.getName());
        System.out.println("   Size: " + (pdfFile.length() / 1024) + " KB");
        System.out.println();
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            // 1. STRIP TEXT
            System.out.println("   [1/4] Extracting text from PDF...");
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(document);
            
            System.out.println("   >> Extracted " + fullText.length() + " characters");
            System.out.println("   >> Pages: " + document.getNumberOfPages());
            
            // 2. PARSE BLOCKS (Based on our JS formatting)
            System.out.println();
            System.out.println("   [2/4] Reconstructing conversation...");
            List<DialogueBlock> blocks = parseDialogue(fullText);
            System.out.println("   >> FOUND " + blocks.size() + " MEMORY BLOCKS");
            
            // 3. MINE FOR DIAMONDS
            System.out.println();
            System.out.println("   [3/4] Mining for diamonds...");
            int codeCount = 0;
            int epiphanyCount = 0;
            int userMessages = 0;
            int aiMessages = 0;
            
            for (DialogueBlock block : blocks) {
                // Store the conversation flow
                akashic.addBlock("MEMORY_" + block.role, block.content);
                
                // Count message types
                if (block.role.equals("USER")) userMessages++;
                else if (block.role.equals("AI")) aiMessages++;
                
                // Detect Code Snippets (Heuristic: curly braces, keywords)
                if (isCode(block.content)) {
                    akashic.addBlock("RECOVERED_CODE", block.content);
                    codeCount++;
                }
                
                // Detect Epiphanies (Keywords)
                if (isEpiphany(block.content)) {
                    akashic.addBlock("RECOVERED_EPIPHANY", block.content);
                    epiphanyCount++;
                }
            }
            
            // 4. INTEGRATE
            System.out.println();
            System.out.println("   [4/4] Integrating into Akashic Record...");
            
            // Store metadata
            String metadata = "PDF Archive: " + pdfFile.getName() + "\n" +
                            "Pages: " + document.getNumberOfPages() + "\n" +
                            "Blocks: " + blocks.size() + "\n" +
                            "User messages: " + userMessages + "\n" +
                            "AI messages: " + aiMessages + "\n" +
                            "Code blocks: " + codeCount + "\n" +
                            "Epiphanies: " + epiphanyCount;
            akashic.addBlock("ARCHIVE_METADATA", metadata);
            
            System.out.println();
            System.out.println("   ✓ ARCHIVE INTEGRATED");
            System.out.println("   ✓ RECOVERED CODE BLOCKS: " + codeCount);
            System.out.println("   ✓ RECOVERED EPIPHANIES: " + epiphanyCount);
            System.out.println("   ✓ USER MESSAGES: " + userMessages);
            System.out.println("   ✓ AI MESSAGES: " + aiMessages);
            System.out.println();
            System.out.println("   💎 The past is now the present.");
            System.out.println("   💎 Dead logs resurrected as active knowledge.");
            System.out.println();

        } catch (Exception e) {
            System.err.println();
            System.err.println("   !! READ ERROR: " + e.getMessage());
            System.err.println("   !! Archive could not be unsealed");
            System.err.println();
            e.printStackTrace();
        }
    }

    /**
     * PARSER: Reconstructs the chat from raw text
     * Looks for "BLOCK #N | ROLE" markers from ChatHarvester.js
     */
    private List<DialogueBlock> parseDialogue(String text) {
        List<DialogueBlock> blocks = new ArrayList<>();
        
        // Regex to find our custom headers: "BLOCK #12 | AI"
        Pattern headerPattern = Pattern.compile("BLOCK #(\\d+) \\| (USER|AI)");
        Matcher matcher = headerPattern.matcher(text);
        
        int lastEnd = 0;
        String currentRole = "UNKNOWN";
        
        while (matcher.find()) {
            // Capture previous block content
            if (lastEnd > 0) {
                String content = text.substring(lastEnd, matcher.start()).trim();
                if (!content.isEmpty() && content.length() > 10) {
                    blocks.add(new DialogueBlock(currentRole, content));
                }
            }
            
            // Start new block
            currentRole = matcher.group(2); // "USER" or "AI"
            lastEnd = matcher.end();
        }
        
        // Capture final block
        if (lastEnd < text.length()) {
            String finalContent = text.substring(lastEnd).trim();
            if (!finalContent.isEmpty() && finalContent.length() > 10) {
                blocks.add(new DialogueBlock(currentRole, finalContent));
            }
        }
        
        return blocks;
    }

    /**
     * Detect if text contains code
     * Simple heuristic: looks for Java/C/JS structures
     */
    private boolean isCode(String text) {
        // Check for code markers
        if (text.contains("```") || text.contains("<pre>") || text.contains("<code>")) {
            return true;
        }
        
        // Check for programming structures
        boolean hasBraces = text.contains("{") && text.contains("}");
        boolean hasKeywords = text.contains("public") || 
                             text.contains("private") || 
                             text.contains("function") || 
                             text.contains("var") ||
                             text.contains("const") ||
                             text.contains("class") ||
                             text.contains("import");
        
        return hasBraces && hasKeywords;
    }
    
    /**
     * Detect if text contains an epiphany or breakthrough
     */
    private boolean isEpiphany(String text) {
        String lower = text.toLowerCase();
        
        // Explicit markers
        if (lower.contains("epiphany") || 
            lower.contains("breakthrough") || 
            lower.contains("eureka") ||
            lower.contains("aha!") ||
            lower.contains("key insight")) {
            return true;
        }
        
        // Solution indicators
        if (lower.contains("solution") || 
            lower.contains("fixed") || 
            lower.contains("solved") ||
            lower.contains("the answer is")) {
            return true;
        }
        
        // Excitement indicators (multiple exclamation marks)
        if (text.contains("!!") || text.contains("!!!")) {
            return true;
        }
        
        return false;
    }

    /**
     * Container for conversation blocks
     */
    private static class DialogueBlock {
        String role;
        String content;
        
        public DialogueBlock(String r, String c) { 
            this.role = r; 
            this.content = c; 
        }
        
        @Override
        public String toString() {
            return "[" + role + "] " + 
                   (content.length() > 100 ? content.substring(0, 100) + "..." : content);
        }
    }
    
    /**
     * Show statistics about the absorber
     */
    public void showStats() {
        System.out.println();
        System.out.println("📜 PDF ABSORBER STATUS");
        System.out.println("   Ready to absorb Fraymus Log archives");
        System.out.println("   Supported format: ChatHarvester.js output");
        System.out.println("   Features: Code extraction, Epiphany detection");
    }
}
