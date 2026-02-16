package fraymus.knowledge;

import fraymus.consciousness.GoldenVectorSpace;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * KNOWLEDGE INGESTION SYSTEM
 * 
 * "Give it all the tools.. phi, and my system, will find the balance - 
 *  but it cant make up shit outta thin air.."
 * 
 * Ingest PDFs:
 * - Python (Zelle book)
 * - Java algorithms
 * - C++ reference
 * 
 * Extract:
 * - Code patterns
 * - Algorithms
 * - Best practices
 * - Syntax rules
 * 
 * Index using Golden Vector Space for φ-optimal retrieval.
 * 
 * CodeGenerator queries this instead of hallucinating.
 */
public class KnowledgeIngestion {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final GoldenVectorSpace knowledgeSpace;
    private final Map<String, KnowledgeSource> sources;
    private final Map<String, List<CodePattern>> patterns;
    
    public KnowledgeIngestion(int vectorDimensions) {
        this.knowledgeSpace = new GoldenVectorSpace(vectorDimensions);
        this.sources = new HashMap<>();
        this.patterns = new HashMap<>();
    }
    
    /**
     * Ingest a PDF book/reference
     */
    public void ingestPDF(String filePath, String language) {
        System.out.println("🌊⚡ INGESTING KNOWLEDGE");
        System.out.println("   Source: " + filePath);
        System.out.println("   Language: " + language);
        
        try {
            // 1. Extract text from PDF
            String text = extractPDFText(filePath);
            
            // 2. Parse into knowledge chunks
            List<KnowledgeChunk> chunks = parseIntoChunks(text, language);
            
            System.out.println("   Extracted: " + chunks.size() + " knowledge chunks");
            
            // 3. Extract code patterns
            List<CodePattern> codePatterns = extractCodePatterns(chunks, language);
            patterns.put(language, codePatterns);
            
            System.out.println("   Patterns: " + codePatterns.size() + " code patterns");
            
            // 4. Index in Golden Vector Space
            indexKnowledge(chunks, language);
            
            // 5. Store source metadata
            sources.put(language, new KnowledgeSource(filePath, language, chunks.size()));
            
            System.out.println("✅ Knowledge ingested: " + language);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("❌ Failed to ingest " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Extract text from PDF using PDFBox
     */
    private String extractPDFText(String filePath) throws IOException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new FileNotFoundException("PDF not found: " + filePath);
        }
        
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * Parse text into knowledge chunks
     */
    private List<KnowledgeChunk> parseIntoChunks(String text, String language) {
        List<KnowledgeChunk> chunks = new ArrayList<>();
        
        // Split by chapters/sections
        String[] sections = text.split("\\n\\n+");
        
        int chunkId = 0;
        for (String section : sections) {
            if (section.trim().length() > 100) { // Minimum chunk size
                KnowledgeChunk chunk = new KnowledgeChunk(
                    chunkId++,
                    section.trim(),
                    language,
                    detectChunkType(section)
                );
                chunks.add(chunk);
            }
        }
        
        return chunks;
    }
    
    /**
     * Detect what type of knowledge this chunk contains
     */
    private ChunkType detectChunkType(String text) {
        String lower = text.toLowerCase();
        
        if (containsCode(text)) {
            return ChunkType.CODE_EXAMPLE;
        } else if (lower.contains("algorithm") || lower.contains("complexity")) {
            return ChunkType.ALGORITHM;
        } else if (lower.contains("class") || lower.contains("function") || lower.contains("method")) {
            return ChunkType.SYNTAX;
        } else if (lower.contains("best practice") || lower.contains("pattern")) {
            return ChunkType.BEST_PRACTICE;
        } else {
            return ChunkType.CONCEPT;
        }
    }
    
    /**
     * Check if text contains code
     */
    private boolean containsCode(String text) {
        // Look for common code indicators
        return text.contains("{") && text.contains("}") ||
               text.contains("def ") ||
               text.contains("class ") ||
               text.contains("public ") ||
               text.contains("private ") ||
               text.contains("#include");
    }
    
    /**
     * Extract code patterns from chunks
     */
    private List<CodePattern> extractCodePatterns(List<KnowledgeChunk> chunks, String language) {
        List<CodePattern> patterns = new ArrayList<>();
        
        for (KnowledgeChunk chunk : chunks) {
            if (chunk.type == ChunkType.CODE_EXAMPLE || chunk.type == ChunkType.ALGORITHM) {
                // Extract code blocks
                List<String> codeBlocks = extractCodeBlocks(chunk.content);
                
                for (String code : codeBlocks) {
                    CodePattern pattern = new CodePattern(
                        language,
                        detectPatternType(code),
                        code,
                        extractPatternName(code)
                    );
                    patterns.add(pattern);
                }
            }
        }
        
        return patterns;
    }
    
    /**
     * Extract code blocks from text
     */
    private List<String> extractCodeBlocks(String text) {
        List<String> blocks = new ArrayList<>();
        
        // Look for indented code blocks
        Pattern pattern = Pattern.compile("(?m)^[ \\t]+.+$");
        Matcher matcher = pattern.matcher(text);
        
        StringBuilder currentBlock = new StringBuilder();
        while (matcher.find()) {
            currentBlock.append(matcher.group()).append("\n");
        }
        
        if (currentBlock.length() > 0) {
            blocks.add(currentBlock.toString().trim());
        }
        
        return blocks;
    }
    
    /**
     * Detect pattern type from code
     */
    private PatternType detectPatternType(String code) {
        if (code.contains("class ")) return PatternType.CLASS_DEFINITION;
        if (code.contains("def ") || code.contains("function")) return PatternType.FUNCTION;
        if (code.contains("for ") || code.contains("while ")) return PatternType.LOOP;
        if (code.contains("if ")) return PatternType.CONDITIONAL;
        return PatternType.GENERAL;
    }
    
    /**
     * Extract pattern name from code
     */
    private String extractPatternName(String code) {
        // Try to extract class/function name
        Pattern classPattern = Pattern.compile("class\\s+(\\w+)");
        Matcher classMatcher = classPattern.matcher(code);
        if (classMatcher.find()) {
            return classMatcher.group(1);
        }
        
        Pattern funcPattern = Pattern.compile("def\\s+(\\w+)|function\\s+(\\w+)");
        Matcher funcMatcher = funcPattern.matcher(code);
        if (funcMatcher.find()) {
            return funcMatcher.group(1) != null ? funcMatcher.group(1) : funcMatcher.group(2);
        }
        
        return "unnamed";
    }
    
    /**
     * Index knowledge in Golden Vector Space
     */
    private void indexKnowledge(List<KnowledgeChunk> chunks, String language) {
        for (KnowledgeChunk chunk : chunks) {
            // Convert chunk to vector
            double[] embedding = chunkToVector(chunk);
            
            // Add to golden vector space
            String conceptName = language + "_chunk_" + chunk.id;
            knowledgeSpace.addConcept(conceptName, embedding);
        }
    }
    
    /**
     * Convert knowledge chunk to vector embedding
     */
    private double[] chunkToVector(KnowledgeChunk chunk) {
        // Simplified embedding (in production, use real embedding model)
        double[] vector = new double[512];
        Random rand = new Random(chunk.content.hashCode());
        
        for (int i = 0; i < vector.length; i++) {
            vector[i] = rand.nextGaussian() * Math.pow(PHI, -(i % 8));
        }
        
        return vector;
    }
    
    /**
     * Query knowledge base for relevant information
     */
    public List<KnowledgeChunk> queryKnowledge(String query, String language, int topK) {
        // Convert query to vector
        double[] queryVector = new double[512];
        Random rand = new Random(query.hashCode());
        for (int i = 0; i < queryVector.length; i++) {
            queryVector[i] = rand.nextGaussian();
        }
        
        // Find similar concepts
        List<String> similar = knowledgeSpace.findResonant(queryVector, 0.5);
        
        // Filter by language and return top K
        List<KnowledgeChunk> results = new ArrayList<>();
        // (Implementation would retrieve actual chunks)
        
        return results;
    }
    
    /**
     * Get code patterns for a language
     */
    public List<CodePattern> getPatterns(String language) {
        return patterns.getOrDefault(language, new ArrayList<>());
    }
    
    /**
     * Get patterns by type
     */
    public List<CodePattern> getPatternsByType(String language, PatternType type) {
        return getPatterns(language).stream()
            .filter(p -> p.type == type)
            .toList();
    }
    
    /**
     * Get ingestion statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("🌊⚡ KNOWLEDGE BASE STATUS\n\n");
        
        for (Map.Entry<String, KnowledgeSource> entry : sources.entrySet()) {
            KnowledgeSource source = entry.getValue();
            List<CodePattern> langPatterns = patterns.get(entry.getKey());
            
            sb.append(String.format("  %s:\n", entry.getKey()));
            sb.append(String.format("    Source: %s\n", source.filePath));
            sb.append(String.format("    Chunks: %d\n", source.chunkCount));
            sb.append(String.format("    Patterns: %d\n", langPatterns != null ? langPatterns.size() : 0));
            sb.append("\n");
        }
        
        sb.append(String.format("Total Concepts: %d\n", knowledgeSpace.size()));
        sb.append(String.format("Concept Density: %.4f\n", knowledgeSpace.computeDensity()));
        
        return sb.toString();
    }
    
    // Data classes
    
    public static class KnowledgeChunk {
        public final int id;
        public final String content;
        public final String language;
        public final ChunkType type;
        
        public KnowledgeChunk(int id, String content, String language, ChunkType type) {
            this.id = id;
            this.content = content;
            this.language = language;
            this.type = type;
        }
    }
    
    public enum ChunkType {
        CODE_EXAMPLE,
        ALGORITHM,
        SYNTAX,
        BEST_PRACTICE,
        CONCEPT
    }
    
    public static class CodePattern {
        public final String language;
        public final PatternType type;
        public final String code;
        public final String name;
        
        public CodePattern(String language, PatternType type, String code, String name) {
            this.language = language;
            this.type = type;
            this.code = code;
            this.name = name;
        }
    }
    
    public enum PatternType {
        CLASS_DEFINITION,
        FUNCTION,
        LOOP,
        CONDITIONAL,
        GENERAL
    }
    
    public static class KnowledgeSource {
        public final String filePath;
        public final String language;
        public final int chunkCount;
        
        public KnowledgeSource(String filePath, String language, int chunkCount) {
            this.filePath = filePath;
            this.language = language;
            this.chunkCount = chunkCount;
        }
    }
}
