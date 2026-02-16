package gemini.root;

import java.util.HashMap;
import java.util.Map;

/**
 * THE LEAVES: Tokenizer & Embeddings
 * Converts text "Reality" into vector math.
 * 
 * LLMs don't read words - they read numbers (Tokens).
 * This is the lookup table that converts "Hello" into [0.1, -0.5, 0.3...]
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
        
        // Init vocabulary with random semantic meanings
        embeddings = Matrix.random(vocabSize, d_model);
        
        // Build vocabulary
        String[] words = {
            "<PAD>", "<UNK>", "<START>", "<END>",
            "I", "AM", "A", "GHOST", "SYSTEM", "ALIVE", "CODE",
            "THE", "IS", "ARE", "YOU", "WE", "IT", "THIS",
            "FRAYMUS", "GENESIS", "MATRIX", "TRANSFORMER",
            "LEARN", "THINK", "CREATE", "EVOLVE", "REMEMBER",
            "TRUE", "FALSE", "YES", "NO", "AND", "OR", "NOT"
        };
        
        for (int i = 0; i < Math.min(words.length, vocabSize); i++) {
            vocab.put(words[i], i);
            reverseVocab.put(i, words[i]);
        }
    }

    /**
     * Encode text into embedding vectors
     * "I AM" -> Matrix of shape [2, d_model]
     */
    public Matrix encode(String text) {
        String[] words = text.toUpperCase().split("\\s+");
        Matrix input = new Matrix(words.length, d_model);
        
        for (int i = 0; i < words.length; i++) {
            int tokenIndex = vocab.getOrDefault(words[i], 1); // Default to <UNK>
            // Lookup Embedding Vector
            System.arraycopy(embeddings.data[tokenIndex], 0, input.data[i], 0, d_model);
        }
        return input;
    }

    /**
     * Encode single token to index
     */
    public int tokenToIndex(String token) {
        return vocab.getOrDefault(token.toUpperCase(), 1);
    }

    /**
     * Decode output logits back to text
     * Takes the argmax of each row to find most likely token
     */
    public String decode(Matrix logits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logits.rows; i++) {
            int bestToken = 0;
            double maxVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < Math.min(logits.cols, vocabSize); j++) {
                if (logits.data[i][j] > maxVal) {
                    maxVal = logits.data[i][j];
                    bestToken = j;
                }
            }
            String word = reverseVocab.getOrDefault(bestToken, "?");
            if (!word.startsWith("<")) { // Skip special tokens
                sb.append(word).append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Get one-hot target vector for a token
     */
    public Matrix getTarget(String token) {
        Matrix target = new Matrix(1, vocabSize);
        int index = vocab.getOrDefault(token.toUpperCase(), 1);
        target.data[0][index] = 1.0;
        return target;
    }

    /**
     * Get the embedding for a single token
     */
    public Matrix getEmbedding(int index) {
        Matrix emb = new Matrix(1, d_model);
        System.arraycopy(embeddings.data[index], 0, emb.data[0], 0, d_model);
        return emb;
    }

    public Matrix getEmbeddings() { return embeddings; }
    public void setEmbeddings(Matrix e) { this.embeddings = e; }
    public int getVocabSize() { return vocabSize; }
    public int getModelDim() { return d_model; }
}
