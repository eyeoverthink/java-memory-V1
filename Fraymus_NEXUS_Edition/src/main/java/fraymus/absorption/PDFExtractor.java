package fraymus.absorption;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF EXTRACTOR
 * "Mining diamonds from archived thought."
 * 
 * Purpose:
 * Extract text content from PDF files (AI chat logs, documentation, papers).
 * Specialized for "Diamond Archives" - harvested AI conversation logs.
 * 
 * Features:
 * - Text extraction from PDF
 * - Code block detection and preservation
 * - Epiphany/insight extraction
 * - Message thread reconstruction
 * 
 * Note: For production, integrate Apache PDFBox library.
 * For now, provides infrastructure and placeholder for PDF parsing.
 */
public class PDFExtractor {

    /**
     * Extract text content from PDF file
     * 
     * @param pdfPath Path to PDF file
     * @return Extracted text content
     */
    public static String extractText(String pdfPath) {
        System.out.println("💎 PDF EXTRACTOR: Analyzing diamond archive");
        System.out.println("   File: " + pdfPath);
        
        File file = new File(pdfPath);
        if (!file.exists()) {
            System.err.println("   !! PDF file not found: " + pdfPath);
            return "";
        }
        
        // TODO: Integrate Apache PDFBox for production
        // For now, return placeholder indicating PDF detected
        System.out.println("   ⚠️ PDF parsing requires Apache PDFBox library");
        System.out.println("   >> Add to build.gradle: implementation 'org.apache.pdfbox:pdfbox:2.0.27'");
        System.out.println("   >> For now, treating as text file");
        
        // Attempt to read as text (works for some simple PDFs)
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            
            String content = new String(data, "UTF-8");
            System.out.println("   >> Extracted " + content.length() + " characters");
            return content;
            
        } catch (Exception e) {
            System.err.println("   !! Extraction failed: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Extract code blocks from PDF content
     * Looks for patterns like:
     * - ```language ... ```
     * - <pre>...</pre>
     * - Indented blocks
     * 
     * @param content PDF text content
     * @return List of code blocks
     */
    public static List<String> extractCodeBlocks(String content) {
        List<String> codeBlocks = new ArrayList<>();
        
        // Pattern 1: Markdown code blocks
        Pattern markdownPattern = Pattern.compile("```[\\w]*\\n([\\s\\S]*?)```", Pattern.MULTILINE);
        Matcher matcher = markdownPattern.matcher(content);
        while (matcher.find()) {
            codeBlocks.add(matcher.group(1).trim());
        }
        
        // Pattern 2: HTML pre tags
        Pattern htmlPattern = Pattern.compile("<pre[^>]*>([\\s\\S]*?)</pre>", Pattern.MULTILINE);
        matcher = htmlPattern.matcher(content);
        while (matcher.find()) {
            codeBlocks.add(matcher.group(1).trim());
        }
        
        return codeBlocks;
    }
    
    /**
     * Extract epiphanies (key insights) from PDF content
     * Looks for markers like:
     * - "Epiphany:", "Breakthrough:", "Key insight:"
     * - Sentences with exclamation marks
     * - Capitalized statements
     * 
     * @param content PDF text content
     * @return List of epiphanies
     */
    public static List<String> extractEpiphanies(String content) {
        List<String> epiphanies = new ArrayList<>();
        
        // Pattern 1: Explicit markers
        Pattern markerPattern = Pattern.compile(
            "(Epiphany|Breakthrough|Key insight|Aha|Eureka)[:\\s]+([^\\n.!?]+[.!?])",
            Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = markerPattern.matcher(content);
        while (matcher.find()) {
            epiphanies.add(matcher.group(2).trim());
        }
        
        // Pattern 2: Excited statements (multiple exclamation marks)
        Pattern excitedPattern = Pattern.compile("([A-Z][^.!?]*!+)", Pattern.MULTILINE);
        matcher = excitedPattern.matcher(content);
        while (matcher.find()) {
            String statement = matcher.group(1).trim();
            if (statement.length() > 20 && statement.length() < 200) {
                epiphanies.add(statement);
            }
        }
        
        return epiphanies;
    }
    
    /**
     * Extract message threads from Diamond Archive PDF
     * Reconstructs conversation flow
     * 
     * @param content PDF text content
     * @return List of messages with role (USER/AI)
     */
    public static List<Message> extractMessages(String content) {
        List<Message> messages = new ArrayList<>();
        
        // Look for BLOCK markers from ChatHarvester.js output
        Pattern blockPattern = Pattern.compile(
            "BLOCK #(\\d+) \\| (USER|AI)\\n([\\s\\S]*?)(?=BLOCK #|$)",
            Pattern.MULTILINE
        );
        
        Matcher matcher = blockPattern.matcher(content);
        while (matcher.find()) {
            int blockNum = Integer.parseInt(matcher.group(1));
            String role = matcher.group(2);
            String text = matcher.group(3).trim();
            
            messages.add(new Message(blockNum, role, text));
        }
        
        return messages;
    }
    
    /**
     * Message container for conversation reconstruction
     */
    public static class Message {
        public int blockNumber;
        public String role; // USER or AI
        public String content;
        
        public Message(int blockNumber, String role, String content) {
            this.blockNumber = blockNumber;
            this.role = role;
            this.content = content;
        }
        
        @Override
        public String toString() {
            return "[" + role + " #" + blockNumber + "] " + 
                   (content.length() > 100 ? content.substring(0, 100) + "..." : content);
        }
    }
    
    /**
     * Analyze PDF and generate statistics
     * 
     * @param pdfPath Path to PDF file
     */
    public static void analyze(String pdfPath) {
        System.out.println();
        System.out.println("💎 DIAMOND ARCHIVE ANALYSIS");
        System.out.println("========================================");
        
        String content = extractText(pdfPath);
        if (content.isEmpty()) {
            System.out.println("   ⚠️ No content extracted");
            return;
        }
        
        List<String> codeBlocks = extractCodeBlocks(content);
        List<String> epiphanies = extractEpiphanies(content);
        List<Message> messages = extractMessages(content);
        
        System.out.println("   File: " + new File(pdfPath).getName());
        System.out.println("   Size: " + content.length() + " characters");
        System.out.println();
        System.out.println("   📊 STATISTICS:");
        System.out.println("   - Messages: " + messages.size());
        System.out.println("   - Code blocks: " + codeBlocks.size());
        System.out.println("   - Epiphanies: " + epiphanies.size());
        System.out.println();
        
        if (!epiphanies.isEmpty()) {
            System.out.println("   🔥 KEY EPIPHANIES:");
            for (int i = 0; i < Math.min(3, epiphanies.size()); i++) {
                System.out.println("   " + (i+1) + ". " + epiphanies.get(i));
            }
            System.out.println();
        }
        
        System.out.println("========================================");
    }
}
