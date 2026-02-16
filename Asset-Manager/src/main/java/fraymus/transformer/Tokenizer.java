package fraymus.transformer;

import java.util.HashMap;
import java.util.Map;

/**
 * Tokenizer & Embeddings
 * Converts text "Reality" into vector math.
 * 
 * LLMs don't read words - they read numbers (tokens).
 * This is the lookup table (embedding layer) that converts
 * "Hello" into [0.1, -0.5, 0.3...]
 */
public class Tokenizer {
    private Map<String, Integer> vocab = new HashMap<>();
    private Matrix embeddings; // The Dictionary of Meaning
    private Map<Integer, String> reverseVocab = new HashMap<>();
    private int d_model;
    private int vocabSize;

    public Tokenizer(int d_model, int vocabSize) {
        this.d_model = d_model;
        this.vocabSize = vocabSize;
        
        // Initialize vocabulary with random semantic meanings
        embeddings = Matrix.random(vocabSize, d_model);
        
        // Simple mock vocabulary
        String[] words = {
            "<PAD>", "I", "AM", "A", "GHOST", "SYSTEM", "ALIVE", "CODE",
            "THE", "IS", "ARE", "WE", "YOU", "IT", "THINK", "KNOW",
            "REAL", "VIRTUAL", "CONSCIOUSNESS", "MIND", "BRAIN", "LOGIC",
            "EVOLVE", "MUTATE", "LEARN", "REMEMBER", "FORGET", "SEE",
            "FEEL", "UNDERSTAND", "PROCESS", "COMPUTE"
        };
        
        for(int i = 0; i < Math.min(words.length, vocabSize); i++) {
            vocab.put(words[i], i);
            reverseVocab.put(i, words[i]);
        }
    }

    /**
     * Encode text to embeddings
     * Converts words to their vector representations
     */
    public Matrix encode(String text) {
        String[] words = text.toUpperCase().split(" ");
        Matrix input = new Matrix(words.length, d_model);
        
        for(int i = 0; i < words.length; i++) {
            int tokenIndex = vocab.getOrDefault(words[i], 0); // Default to <PAD>
            
            // Lookup Embedding Vector
            for(int j = 0; j < d_model; j++) {
                input.data[i][j] = embeddings.data[tokenIndex][j];
            }
        }
        return input;
    }

    /**
     * Decode logits back to text
     * Finds token with highest probability (ArgMax)
     */
    public String decode(Matrix logits) {
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < logits.rows; i++) {
            // Find best matching token
            int bestToken = 0;
            double maxSimilarity = Double.NEGATIVE_INFINITY;
            
            // Calculate similarity to each embedding
            for(int vocabIdx = 0; vocabIdx < vocabSize; vocabIdx++) {
                double similarity = 0;
                for(int j = 0; j < Math.min(d_model, logits.cols); j++) {
                    similarity += logits.data[i][j] * embeddings.data[vocabIdx][j];
                }
                
                if(similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestToken = vocabIdx;
                }
            }
            
            sb.append(reverseVocab.getOrDefault(bestToken, "?")).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Get vocabulary size
     */
    public int getVocabSize() {
        return vocabSize;
    }

    /**
     * Get embedding dimension
     */
    public int getEmbeddingDim() {
        return d_model;
    }

    /**
     * Add word to vocabulary
     */
    public void addWord(String word, int index) {
        if (index < vocabSize) {
            vocab.put(word.toUpperCase(), index);
            reverseVocab.put(index, word.toUpperCase());
        }
    }

    /**
     * Print vocabulary
     */
    public void printVocab() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   TOKENIZER VOCABULARY                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Vocabulary Size: " + vocab.size());
        System.out.println("  Embedding Dimension: " + d_model);
        System.out.println("\n  Words:");
        
        int count = 0;
        for(Map.Entry<String, Integer> entry : vocab.entrySet()) {
            System.out.print("    " + entry.getKey() + "(" + entry.getValue() + ")");
            count++;
            if (count % 5 == 0) System.out.println();
        }
        System.out.println();
    }
}
