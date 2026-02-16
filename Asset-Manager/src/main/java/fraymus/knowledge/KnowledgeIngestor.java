package fraymus.knowledge;

import fraymus.chaos.EvolutionaryChaos;
import fraymus.hyper.HyperVector;
import fraymus.hyper.HyperMemory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * THE KNOWLEDGE GUT: DIGESTING PDFs INTO HOLOGRAPHIC MEMORY
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Don't memorize the book. Understand the concept."
 * 
 * Mechanism:
 * 1. EXTRACT: Pull text from PDF using PDFBox.
 * 2. CHUNK: Break into digestible concept blocks.
 * 3. VECTORIZE: Convert text to HyperVectors (10,000-dimensional).
 * 4. HOLOGRAPH: Store in the Interference Pattern (HyperMemory).
 * 
 * This is RAG (Retrieval-Augmented Generation) with a Fraymus twist:
 * - We don't fine-tune (which ruins the model)
 * - We store in holographic memory (interference patterns)
 * - Retrieval uses similarity in hyperspace
 */
public class KnowledgeIngestor {

    // The Holographic Storage (Long-Term Memory)
    private HyperMemory holographicMemory;
    
    // The Chaos Engine (For vector generation)
    private EvolutionaryChaos chaos;
    
    // Chunk index (concept -> vector mapping)
    private Map<String, String> chunkIndex = new ConcurrentHashMap<>();
    
    // Statistics
    private int documentsProcessed = 0;
    private int chunksCreated = 0;
    private int conceptsLearned = 0;
    
    // Configuration
    private static final int CHUNK_SIZE = 500;       // Characters per chunk
    private static final int CHUNK_OVERLAP = 100;    // Overlap between chunks
    private static final int MIN_CHUNK_LENGTH = 50;  // Minimum viable chunk

    public KnowledgeIngestor() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   📚 INITIALIZING KNOWLEDGE GUT");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        this.holographicMemory = new HyperMemory();
        this.chaos = new EvolutionaryChaos();
        HyperVector.setWill(chaos);
        
        System.out.println("   ✓ Holographic Memory Online");
        System.out.println("   ✓ Chaos Engine Initialized");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // PDF INGESTION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Digest an entire folder of PDFs
     */
    public void digestFolder(String folderPath) {
        System.out.println("📚 OPENING KNOWLEDGE FOLDER: " + folderPath);
        System.out.println();
        
        File folder = new File(folderPath);
        
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("   !! Folder not found: " + folderPath);
            return;
        }
        
        File[] files = folder.listFiles();
        if (files == null) {
            System.out.println("   !! Cannot read folder contents");
            return;
        }
        
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                digestPDF(file);
            }
        }
        
        printStats();
    }
    
    /**
     * Digest a single PDF file
     */
    public void digestPDF(File pdfFile) {
        System.out.println("   >> INGESTING: " + pdfFile.getName());
        
        try {
            // 1. EXTRACT RAW TEXT
            String rawText = extractText(pdfFile);
            
            if (rawText == null || rawText.trim().isEmpty()) {
                System.out.println("      !! No text extracted from PDF");
                return;
            }
            
            System.out.println("      Extracted " + rawText.length() + " characters");
            
            // 2. BREAK INTO CHUNKS (Logic Atoms)
            List<String> chunks = chunkText(rawText, pdfFile.getName());
            System.out.println("      Created " + chunks.size() + " chunks");
            
            // 3. VECTORIZE AND STORE
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                String chunkId = pdfFile.getName() + "_chunk_" + i;
                
                // Generate HyperVector from text
                HyperVector vector = textToHyperVector(chunk);
                
                // Store in holographic memory
                holographicMemory.learn(chunkId, vector);
                
                // Keep text index for retrieval
                chunkIndex.put(chunkId, chunk);
                
                conceptsLearned++;
            }
            
            chunksCreated += chunks.size();
            documentsProcessed++;
            
            System.out.println("      ✓ Integrated into holographic memory");
            
        } catch (Exception e) {
            System.out.println("      !! Error processing PDF: " + e.getMessage());
        }
    }
    
    /**
     * Extract text from PDF using PDFBox
     */
    private String extractText(File pdfFile) throws IOException {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
    
    /**
     * Break text into overlapping chunks
     */
    private List<String> chunkText(String text, String source) {
        List<String> chunks = new ArrayList<>();
        
        // Clean the text
        text = text.replaceAll("\\s+", " ").trim();
        
        // Try to split on natural boundaries first (paragraphs, sections)
        String[] paragraphs = text.split("\n\n+");
        
        StringBuilder currentChunk = new StringBuilder();
        
        for (String para : paragraphs) {
            para = para.trim();
            if (para.isEmpty()) continue;
            
            // If adding this paragraph would exceed chunk size
            if (currentChunk.length() + para.length() > CHUNK_SIZE) {
                // Save current chunk if it's big enough
                if (currentChunk.length() >= MIN_CHUNK_LENGTH) {
                    chunks.add(currentChunk.toString().trim());
                }
                
                // Start new chunk with overlap
                String overlap = "";
                if (currentChunk.length() > CHUNK_OVERLAP) {
                    overlap = currentChunk.substring(
                        currentChunk.length() - CHUNK_OVERLAP);
                }
                currentChunk = new StringBuilder(overlap);
            }
            
            currentChunk.append(para).append(" ");
        }
        
        // Don't forget the last chunk
        if (currentChunk.length() >= MIN_CHUNK_LENGTH) {
            chunks.add(currentChunk.toString().trim());
        }
        
        // If no chunks were created (e.g., no paragraph breaks), use sliding window
        if (chunks.isEmpty() && text.length() >= MIN_CHUNK_LENGTH) {
            for (int i = 0; i < text.length(); i += (CHUNK_SIZE - CHUNK_OVERLAP)) {
                int end = Math.min(i + CHUNK_SIZE, text.length());
                String chunk = text.substring(i, end).trim();
                if (chunk.length() >= MIN_CHUNK_LENGTH) {
                    chunks.add(chunk);
                }
            }
        }
        
        return chunks;
    }

    // ═══════════════════════════════════════════════════════════════════
    // VECTORIZATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Convert text to a HyperVector (10,000 dimensions)
     * Uses semantic hashing with chaos perturbation
     */
    private HyperVector textToHyperVector(String text) {
        // Start with a base vector from the text hash
        BigInteger textHash = new BigInteger(1, 
            text.getBytes()).multiply(BigInteger.valueOf(31));
        
        // Add chaos perturbation for uniqueness
        BigInteger chaosFactor = chaos.nextFractal();
        BigInteger combined = textHash.xor(chaosFactor);
        
        return new HyperVector(combined);
    }

    // ═══════════════════════════════════════════════════════════════════
    // RETRIEVAL (RAG)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Query the knowledge base for relevant context
     * Returns the most similar chunks to the query
     */
    public List<String> query(String queryText, int topK) {
        System.out.println("   🔍 QUERYING KNOWLEDGE BASE: " + 
            queryText.substring(0, Math.min(50, queryText.length())) + "...");
        
        // Vectorize the query
        HyperVector queryVector = textToHyperVector(queryText);
        
        // Find similar chunks
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();
        
        for (String chunkId : chunkIndex.keySet()) {
            if (holographicMemory.knows(chunkId)) {
                HyperVector chunkVector = holographicMemory.get(chunkId);
                double similarity = queryVector.similarity(chunkVector);
                similarities.add(new AbstractMap.SimpleEntry<>(chunkId, similarity));
            }
        }
        
        // Sort by similarity (descending)
        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        // Return top K chunks
        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, similarities.size()); i++) {
            String chunkId = similarities.get(i).getKey();
            double score = similarities.get(i).getValue();
            String text = chunkIndex.get(chunkId);
            
            System.out.println("      [" + String.format("%.2f", score * 100) + "%] " + chunkId);
            results.add(text);
        }
        
        return results;
    }
    
    /**
     * Get context for a prompt (for RAG integration)
     */
    public String getContextForPrompt(String prompt) {
        List<String> relevant = query(prompt, 3);
        
        if (relevant.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("RELEVANT KNOWLEDGE:\n");
        for (int i = 0; i < relevant.size(); i++) {
            context.append("---\n");
            context.append(relevant.get(i));
            context.append("\n");
        }
        context.append("---\n");
        
        return context.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ KNOWLEDGE GUT STATISTICS                                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Documents Processed: " + String.format("%-36d", documentsProcessed) + "│");
        System.out.println("│ Chunks Created:      " + String.format("%-36d", chunksCreated) + "│");
        System.out.println("│ Concepts Learned:    " + String.format("%-36d", conceptsLearned) + "│");
        System.out.println("│ Memory Size:         " + String.format("%-36d", holographicMemory.conceptCount()) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }
    
    public int getDocumentsProcessed() { return documentsProcessed; }
    public int getChunksCreated() { return chunksCreated; }
    public int getConceptsLearned() { return conceptsLearned; }
    public HyperMemory getMemory() { return holographicMemory; }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN DEMO
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("   ╔═══════════════════════════════════════════════════╗");
        System.out.println("   ║   KNOWLEDGE GUT: PDF INGESTION                    ║");
        System.out.println("   ╠═══════════════════════════════════════════════════╣");
        System.out.println("   ║   \"Don't memorize the book.\"                      ║");
        System.out.println("   ║   \"Understand the concept.\"                       ║");
        System.out.println("   ╚═══════════════════════════════════════════════════╝");
        System.out.println();
        
        KnowledgeIngestor gut = new KnowledgeIngestor();
        
        // Check for command line argument (folder path)
        if (args.length > 0) {
            gut.digestFolder(args[0]);
        } else {
            // Demo mode - look for PDFs in current directory
            String currentDir = System.getProperty("user.dir");
            System.out.println("   No folder specified. Scanning: " + currentDir);
            gut.digestFolder(currentDir);
        }
        
        // Demo query
        if (gut.getConceptsLearned() > 0) {
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("   DEMO: QUERYING KNOWLEDGE BASE");
            System.out.println("═══════════════════════════════════════════════════════");
            
            String query = "How do I implement a web scraper?";
            List<String> results = gut.query(query, 3);
            
            System.out.println();
            System.out.println("   Query: " + query);
            System.out.println("   Results: " + results.size() + " relevant chunks found");
        }
        
        System.out.println();
        System.out.println("   ✓ Knowledge Gut ready for RAG integration");
        System.out.println();
    }
}
