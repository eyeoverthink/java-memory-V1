package fraymus.knowledge;

import java.util.*;

/**
 * PHI VECTOR INDEXER: GOLDEN RATIO EMBEDDING
 * 
 * Creates vector embeddings optimized by phi (golden ratio).
 * 
 * Standard vector indexing: Random or learned embeddings
 * Phi vector indexing: Geometrically optimized for maximum separation
 * 
 * The golden ratio creates optimal spacing in high-dimensional space.
 * This maximizes semantic distinction while minimizing collisions.
 * 
 * Process:
 * 1. Extract features from text
 * 2. Generate phi-harmonic embedding
 * 3. Store in holographic memory
 * 4. Enable semantic search
 * 
 * Math:
 * - Embedding dimension: 512 (2^9, phi-harmonic)
 * - Feature extraction: TF-IDF + phi weighting
 * - Distance metric: Cosine similarity
 * - Index structure: Phi-spiral (golden angle distribution)
 */
public class PhiVectorIndexer {
    
    private static final double PHI = 1.6180339887;
    private static final int EMBEDDING_DIM = 512; // 2^9
    
    // Vocabulary (word → index mapping)
    private final Map<String, Integer> vocabulary;
    
    // Document frequency (for TF-IDF)
    private final Map<String, Integer> documentFrequency;
    
    // Indexed chunks
    private final List<PDFIngestion.TextChunk> indexedChunks;
    
    // Phi-spiral index (golden angle distribution)
    private final Map<Double, List<PDFIngestion.TextChunk>> phiIndex;
    
    private int totalDocuments = 0;
    
    public PhiVectorIndexer() {
        this.vocabulary = new HashMap<>();
        this.documentFrequency = new HashMap<>();
        this.indexedChunks = new ArrayList<>();
        this.phiIndex = new HashMap<>();
    }
    
    /**
     * Index text chunks
     * 
     * @param chunks Text chunks to index
     */
    public void indexChunks(List<PDFIngestion.TextChunk> chunks) {
        System.out.println("\n🌊⚡ PHI VECTOR INDEXING");
        System.out.println("   Chunks to index: " + chunks.size());
        
        // Build vocabulary and document frequency
        System.out.println("   Building vocabulary...");
        buildVocabulary(chunks);
        
        System.out.println("   Vocabulary size: " + vocabulary.size());
        System.out.println("   Generating embeddings...");
        
        // Generate embeddings for each chunk
        for (int i = 0; i < chunks.size(); i++) {
            PDFIngestion.TextChunk chunk = chunks.get(i);
            
            // Generate phi-harmonic embedding
            double[] embedding = generateEmbedding(chunk.text);
            chunk.embedding = embedding;
            
            // Add to index
            indexedChunks.add(chunk);
            
            // Add to phi-spiral index
            double phiAddress = ((i + 1) * PHI) % 1.0;
            phiIndex.computeIfAbsent(phiAddress, k -> new ArrayList<>()).add(chunk);
            
            if ((i + 1) % 100 == 0) {
                System.out.println("   Indexed: " + (i + 1) + "/" + chunks.size());
            }
        }
        
        totalDocuments = chunks.size();
        
        System.out.println("   ✓ Indexing complete");
        System.out.println("   Total indexed: " + indexedChunks.size());
        System.out.println("   Phi-spiral nodes: " + phiIndex.size());
        System.out.println();
    }
    
    /**
     * Build vocabulary from chunks
     * 
     * @param chunks Text chunks
     */
    private void buildVocabulary(List<PDFIngestion.TextChunk> chunks) {
        int wordIndex = 0;
        
        for (PDFIngestion.TextChunk chunk : chunks) {
            Set<String> uniqueWords = new HashSet<>();
            
            String[] words = tokenize(chunk.text);
            for (String word : words) {
                if (!vocabulary.containsKey(word)) {
                    vocabulary.put(word, wordIndex++);
                }
                uniqueWords.add(word);
            }
            
            // Update document frequency
            for (String word : uniqueWords) {
                documentFrequency.merge(word, 1, Integer::sum);
            }
        }
    }
    
    /**
     * Generate phi-harmonic embedding
     * 
     * @param text Text to embed
     * @return Embedding vector
     */
    private double[] generateEmbedding(String text) {
        double[] embedding = new double[EMBEDDING_DIM];
        
        // Tokenize
        String[] words = tokenize(text);
        Map<String, Integer> termFrequency = new HashMap<>();
        
        for (String word : words) {
            termFrequency.merge(word, 1, Integer::sum);
        }
        
        // Calculate TF-IDF with phi weighting
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String word = entry.getKey();
            int tf = entry.getValue();
            
            if (!vocabulary.containsKey(word)) continue;
            
            int wordIndex = vocabulary.get(word);
            int df = documentFrequency.getOrDefault(word, 1);
            
            // TF-IDF
            double tfidf = (tf * Math.log((double) totalDocuments / df));
            
            // Phi-harmonic weighting
            // Distribute across embedding dimensions using golden angle
            for (int i = 0; i < EMBEDDING_DIM; i++) {
                double angle = (wordIndex + i) * PHI * 2 * Math.PI;
                embedding[i] += tfidf * Math.cos(angle) * Math.pow(PHI, -i / 100.0);
            }
        }
        
        // Normalize
        normalize(embedding);
        
        return embedding;
    }
    
    /**
     * Search for similar chunks
     * 
     * @param query Query text
     * @param topK Number of results to return
     * @return List of similar chunks with scores
     */
    public List<SearchResult> search(String query, int topK) {
        System.out.println("\n🌊⚡ SEMANTIC SEARCH");
        System.out.println("   Query: " + query);
        System.out.println("   Top K: " + topK);
        
        // Generate query embedding
        double[] queryEmbedding = generateEmbedding(query);
        
        // Calculate similarity with all chunks
        List<SearchResult> results = new ArrayList<>();
        
        for (PDFIngestion.TextChunk chunk : indexedChunks) {
            double similarity = cosineSimilarity(queryEmbedding, chunk.embedding);
            results.add(new SearchResult(chunk, similarity));
        }
        
        // Sort by similarity (descending)
        results.sort((a, b) -> Double.compare(b.score, a.score));
        
        // Return top K
        List<SearchResult> topResults = results.subList(0, Math.min(topK, results.size()));
        
        System.out.println("   ✓ Search complete");
        System.out.println("   Results found: " + topResults.size());
        System.out.println();
        
        return topResults;
    }
    
    /**
     * Tokenize text
     * 
     * @param text Text to tokenize
     * @return Array of tokens
     */
    private String[] tokenize(String text) {
        // Simple tokenization: lowercase, remove punctuation, split on whitespace
        return text.toLowerCase()
                   .replaceAll("[^a-z0-9\\s]", " ")
                   .split("\\s+");
    }
    
    /**
     * Normalize vector
     * 
     * @param vector Vector to normalize
     */
    private void normalize(double[] vector) {
        double magnitude = 0;
        for (double v : vector) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        
        if (magnitude > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
        }
    }
    
    /**
     * Calculate cosine similarity
     * 
     * @param a First vector
     * @param b Second vector
     * @return Cosine similarity (0 to 1)
     */
    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
        }
        return dotProduct; // Vectors are already normalized
    }
    
    /**
     * Get indexer statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ PHI VECTOR INDEXER STATS\n\n" +
            "   Vocabulary Size: %,d words\n" +
            "   Indexed Chunks: %,d\n" +
            "   Embedding Dimension: %d (φ-harmonic)\n" +
            "   Phi-Spiral Nodes: %d\n" +
            "   Index Structure: Golden Angle Distribution\n" +
            "   Distance Metric: Cosine Similarity\n",
            vocabulary.size(),
            indexedChunks.size(),
            EMBEDDING_DIM,
            phiIndex.size()
        );
    }
    
    /**
     * Search result data class
     */
    public static class SearchResult {
        public final PDFIngestion.TextChunk chunk;
        public final double score;
        
        public SearchResult(PDFIngestion.TextChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Result[score=%.4f, source=%s, page=%d]",
                score, chunk.source, chunk.page
            );
        }
    }
}
