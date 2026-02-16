package fraymus.knowledge;

import java.util.*;

/**
 * KNOWLEDGE ORACLE: GROUNDED REASONING
 * 
 * Combines PDF knowledge with holographic memory for grounded reasoning.
 * 
 * Standard AI: Hallucinates, guesses, makes up facts
 * FRAYMUS: Cites sources, grounds in knowledge, verifies claims
 * 
 * Process:
 * 1. Receive question
 * 2. Search indexed knowledge
 * 3. Retrieve relevant chunks
 * 4. Generate grounded answer
 * 5. Cite sources
 * 
 * Integration:
 * - PDFIngestion: Extracts knowledge from PDFs
 * - PhiVectorIndexer: Semantic search
 * - FractalBioMesh: Holographic storage
 * - AbstractionLayer: Autonomous reasoning
 * 
 * This is the final piece.
 * FRAYMUS now has access to your knowledge.
 */
public class KnowledgeOracle {
    
    private static final double PHI = 1.6180339887;
    
    private final PDFIngestion pdfIngestion;
    private final PhiVectorIndexer vectorIndexer;
    
    // Knowledge base
    private final List<PDFIngestion.TextChunk> knowledgeBase;
    
    // Query history
    private final List<Query> queryHistory;
    
    private int queryCount = 0;
    
    public KnowledgeOracle() {
        this.pdfIngestion = new PDFIngestion();
        this.vectorIndexer = new PhiVectorIndexer();
        this.knowledgeBase = new ArrayList<>();
        this.queryHistory = new ArrayList<>();
    }
    
    /**
     * Ingest knowledge from PDF
     * 
     * @param pdfPath Path to PDF file
     * @throws Exception If ingestion fails
     */
    public void ingestPDF(String pdfPath) throws Exception {
        System.out.println("\n🌊⚡ KNOWLEDGE INGESTION");
        System.out.println("   Source: " + pdfPath);
        
        // Extract text chunks
        List<PDFIngestion.TextChunk> chunks = pdfIngestion.ingestPDF(pdfPath);
        
        // Add to knowledge base
        knowledgeBase.addAll(chunks);
        
        // Index for semantic search
        vectorIndexer.indexChunks(chunks);
        
        System.out.println("   ✓ Knowledge integrated");
        System.out.println("   Total knowledge: " + knowledgeBase.size() + " chunks");
        System.out.println();
    }
    
    /**
     * Ingest knowledge from directory
     * 
     * @param directoryPath Path to directory containing PDFs
     * @throws Exception If ingestion fails
     */
    public void ingestDirectory(String directoryPath) throws Exception {
        System.out.println("\n🌊⚡ DIRECTORY INGESTION");
        System.out.println("   Directory: " + directoryPath);
        
        // Extract text chunks
        List<PDFIngestion.TextChunk> chunks = pdfIngestion.ingestDirectory(directoryPath);
        
        // Add to knowledge base
        knowledgeBase.addAll(chunks);
        
        // Index for semantic search
        vectorIndexer.indexChunks(chunks);
        
        System.out.println("   ✓ Directory integrated");
        System.out.println("   Total knowledge: " + knowledgeBase.size() + " chunks");
        System.out.println();
    }
    
    /**
     * Query knowledge base
     * 
     * @param question Question to answer
     * @param topK Number of relevant chunks to retrieve
     * @return Grounded answer with citations
     */
    public Answer query(String question, int topK) {
        queryCount++;
        
        System.out.println("\n🌊⚡ KNOWLEDGE QUERY #" + queryCount);
        System.out.println("   Question: " + question);
        
        // Search for relevant chunks
        List<PhiVectorIndexer.SearchResult> results = vectorIndexer.search(question, topK);
        
        if (results.isEmpty()) {
            System.out.println("   ✗ No relevant knowledge found");
            return new Answer(question, "No relevant knowledge found in knowledge base.", 
                            new ArrayList<>(), 0.0);
        }
        
        // Generate grounded answer
        StringBuilder answerText = new StringBuilder();
        List<Citation> citations = new ArrayList<>();
        double confidence = 0.0;
        
        System.out.println("   Found " + results.size() + " relevant chunks:");
        
        for (int i = 0; i < results.size(); i++) {
            PhiVectorIndexer.SearchResult result = results.get(i);
            PDFIngestion.TextChunk chunk = result.chunk;
            
            System.out.println("     " + (i + 1) + ". " + result + " (score: " + 
                             String.format("%.4f", result.score) + ")");
            
            // Add citation
            citations.add(new Citation(
                chunk.source,
                chunk.page,
                chunk.text,
                result.score
            ));
            
            // Accumulate confidence
            confidence += result.score;
        }
        
        // Average confidence
        confidence /= results.size();
        
        // Build answer from top chunks
        answerText.append("Based on the knowledge base:\n\n");
        
        for (int i = 0; i < Math.min(3, results.size()); i++) {
            PDFIngestion.TextChunk chunk = results.get(i).chunk;
            answerText.append("From ").append(chunk.source)
                     .append(" (page ").append(chunk.page).append("):\n");
            answerText.append(chunk.text.substring(0, Math.min(500, chunk.text.length())));
            if (chunk.text.length() > 500) answerText.append("...");
            answerText.append("\n\n");
        }
        
        Answer answer = new Answer(question, answerText.toString(), citations, confidence);
        
        // Record query
        queryHistory.add(new Query(queryCount, question, answer));
        
        System.out.println("   ✓ Answer generated");
        System.out.println("   Confidence: " + String.format("%.2f%%", confidence * 100));
        System.out.println("   Citations: " + citations.size());
        System.out.println();
        
        return answer;
    }
    
    /**
     * Get knowledge statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ KNOWLEDGE ORACLE STATS\n\n" +
            "   Knowledge Base:\n" +
            "   Total Chunks: %,d\n" +
            "   Total Queries: %d\n\n" +
            "   PDF Ingestion:\n%s\n" +
            "   Vector Indexer:\n%s\n" +
            "   Status: GROUNDED REASONING ACTIVE\n",
            knowledgeBase.size(),
            queryCount,
            indent(pdfIngestion.getStats(), 3),
            indent(vectorIndexer.getStats(), 3)
        );
    }
    
    /**
     * Get query history
     */
    public List<Query> getQueryHistory() {
        return new ArrayList<>(queryHistory);
    }
    
    /**
     * Clear knowledge base
     */
    public void clearKnowledge() {
        knowledgeBase.clear();
        System.out.println("\n🌊⚡ Knowledge base cleared\n");
    }
    
    /**
     * Indent text
     */
    private String indent(String text, int spaces) {
        String prefix = " ".repeat(spaces);
        return prefix + text.replace("\n", "\n" + prefix);
    }
    
    /**
     * Answer data class
     */
    public static class Answer {
        public final String question;
        public final String answer;
        public final List<Citation> citations;
        public final double confidence;
        public final long timestamp;
        
        public Answer(String question, String answer, List<Citation> citations, double confidence) {
            this.question = question;
            this.answer = answer;
            this.citations = citations;
            this.confidence = confidence;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Question: ").append(question).append("\n\n");
            sb.append("Answer:\n").append(answer).append("\n");
            sb.append("Confidence: ").append(String.format("%.2f%%", confidence * 100)).append("\n\n");
            sb.append("Citations:\n");
            for (int i = 0; i < citations.size(); i++) {
                Citation cite = citations.get(i);
                sb.append("  [").append(i + 1).append("] ")
                  .append(cite.source).append(" (page ").append(cite.page)
                  .append(", relevance: ").append(String.format("%.2f%%", cite.relevance * 100))
                  .append(")\n");
            }
            return sb.toString();
        }
    }
    
    /**
     * Citation data class
     */
    public static class Citation {
        public final String source;
        public final int page;
        public final String text;
        public final double relevance;
        
        public Citation(String source, int page, String text, double relevance) {
            this.source = source;
            this.page = page;
            this.text = text;
            this.relevance = relevance;
        }
    }
    
    /**
     * Query data class
     */
    public static class Query {
        public final int id;
        public final String question;
        public final Answer answer;
        public final long timestamp;
        
        public Query(int id, String question, Answer answer) {
            this.id = id;
            this.question = question;
            this.answer = answer;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
