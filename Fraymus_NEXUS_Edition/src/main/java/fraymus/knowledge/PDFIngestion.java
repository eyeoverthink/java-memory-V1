package fraymus.knowledge;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * PDF INGESTION: KNOWLEDGE EXTRACTION
 * 
 * Extracts text from PDFs and prepares for vector indexing.
 * 
 * This is the fuel for FRAYMUS.
 * The engine (logic) and wheels (physics) are ready.
 * This provides the map (knowledge).
 * 
 * Process:
 * 1. Load PDF
 * 2. Extract text (page by page)
 * 3. Chunk into semantic blocks
 * 4. Generate embeddings
 * 5. Store in holographic memory
 * 
 * Dependencies:
 * - Apache PDFBox (org.apache.pdfbox:pdfbox:2.0.27)
 * 
 * Add to build.gradle:
 * implementation 'org.apache.pdfbox:pdfbox:2.0.27'
 */
public class PDFIngestion {
    
    private static final double PHI = 1.6180339887;
    
    // Chunk size (characters) - phi-optimized
    private static final int CHUNK_SIZE = (int) (1000 * PHI); // ~1618 chars
    private static final int CHUNK_OVERLAP = (int) (200 * PHI); // ~324 chars
    
    private int documentsProcessed = 0;
    private int chunksCreated = 0;
    private long totalCharacters = 0;
    
    /**
     * Ingest PDF file
     * 
     * @param pdfPath Path to PDF file
     * @return List of text chunks
     * @throws IOException If PDF cannot be read
     */
    public List<TextChunk> ingestPDF(String pdfPath) throws IOException {
        System.out.println("\n🌊⚡ PDF INGESTION");
        System.out.println("   File: " + pdfPath);
        
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            throw new IOException("PDF not found: " + pdfPath);
        }
        
        List<TextChunk> chunks = new ArrayList<>();
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            int pageCount = document.getNumberOfPages();
            System.out.println("   Pages: " + pageCount);
            
            PDFTextStripper stripper = new PDFTextStripper();
            
            // Extract text page by page
            for (int page = 1; page <= pageCount; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                
                String pageText = stripper.getText(document);
                totalCharacters += pageText.length();
                
                // Chunk the page text
                List<TextChunk> pageChunks = chunkText(pageText, pdfPath, page);
                chunks.addAll(pageChunks);
                
                if (page % 10 == 0) {
                    System.out.println("   Processed: " + page + "/" + pageCount + " pages");
                }
            }
            
            documentsProcessed++;
            
            System.out.println("   ✓ Extraction complete");
            System.out.println("   Total chunks: " + chunks.size());
            System.out.println("   Total characters: " + totalCharacters);
            System.out.println();
        }
        
        return chunks;
    }
    
    /**
     * Ingest multiple PDFs from directory
     * 
     * @param directoryPath Path to directory containing PDFs
     * @return List of all text chunks
     * @throws IOException If directory cannot be read
     */
    public List<TextChunk> ingestDirectory(String directoryPath) throws IOException {
        System.out.println("\n🌊⚡ DIRECTORY INGESTION");
        System.out.println("   Directory: " + directoryPath);
        
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory not found: " + directoryPath);
        }
        
        List<TextChunk> allChunks = new ArrayList<>();
        File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        
        if (pdfFiles == null || pdfFiles.length == 0) {
            System.out.println("   No PDF files found");
            return allChunks;
        }
        
        System.out.println("   Found " + pdfFiles.length + " PDF files\n");
        
        for (File pdfFile : pdfFiles) {
            try {
                List<TextChunk> chunks = ingestPDF(pdfFile.getAbsolutePath());
                allChunks.addAll(chunks);
            } catch (IOException e) {
                System.err.println("   ✗ Failed to process: " + pdfFile.getName());
                System.err.println("     Error: " + e.getMessage());
            }
        }
        
        System.out.println("\n   ✓ Directory ingestion complete");
        System.out.println("   Total documents: " + documentsProcessed);
        System.out.println("   Total chunks: " + allChunks.size());
        System.out.println();
        
        return allChunks;
    }
    
    /**
     * Chunk text into semantic blocks
     * 
     * Uses phi-optimized chunk size with overlap for context preservation.
     * 
     * @param text Text to chunk
     * @param source Source file path
     * @param page Page number
     * @return List of text chunks
     */
    private List<TextChunk> chunkText(String text, String source, int page) {
        List<TextChunk> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        // Clean text
        text = text.replaceAll("\\s+", " ").trim();
        
        int start = 0;
        int chunkId = 0;
        
        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());
            
            // Try to break at sentence boundary
            if (end < text.length()) {
                int sentenceEnd = findSentenceEnd(text, end);
                if (sentenceEnd > start && sentenceEnd < end + 200) {
                    end = sentenceEnd;
                }
            }
            
            String chunkText = text.substring(start, end).trim();
            
            if (!chunkText.isEmpty()) {
                TextChunk chunk = new TextChunk(
                    chunkId++,
                    chunkText,
                    source,
                    page,
                    start,
                    end
                );
                chunks.add(chunk);
                chunksCreated++;
            }
            
            // Move start with overlap
            start = end - CHUNK_OVERLAP;
            if (start >= text.length()) break;
        }
        
        return chunks;
    }
    
    /**
     * Find sentence end near position
     * 
     * @param text Text to search
     * @param position Starting position
     * @return Position of sentence end
     */
    private int findSentenceEnd(String text, int position) {
        // Look for sentence terminators
        String terminators = ".!?";
        
        for (int i = position; i < Math.min(position + 200, text.length()); i++) {
            char c = text.charAt(i);
            if (terminators.indexOf(c) >= 0) {
                // Check if next char is space or end
                if (i + 1 >= text.length() || Character.isWhitespace(text.charAt(i + 1))) {
                    return i + 1;
                }
            }
        }
        
        return position;
    }
    
    /**
     * Get ingestion statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ PDF INGESTION STATS\n\n" +
            "   Documents Processed: %d\n" +
            "   Chunks Created: %d\n" +
            "   Total Characters: %,d\n" +
            "   Chunk Size: %d chars (φ-optimized)\n" +
            "   Chunk Overlap: %d chars (φ-optimized)\n" +
            "   Avg Chunk Size: %d chars\n",
            documentsProcessed,
            chunksCreated,
            totalCharacters,
            CHUNK_SIZE,
            CHUNK_OVERLAP,
            chunksCreated > 0 ? (int)(totalCharacters / chunksCreated) : 0
        );
    }
    
    /**
     * Text chunk data class
     */
    public static class TextChunk {
        public final int id;
        public final String text;
        public final String source;
        public final int page;
        public final int startPos;
        public final int endPos;
        public final long timestamp;
        
        // Embedding vector (set by VectorIndexer)
        public double[] embedding;
        
        public TextChunk(int id, String text, String source, int page, int startPos, int endPos) {
            this.id = id;
            this.text = text;
            this.source = source;
            this.page = page;
            this.startPos = startPos;
            this.endPos = endPos;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format(
                "Chunk[id=%d, source=%s, page=%d, length=%d]",
                id, source, page, text.length()
            );
        }
    }
}
