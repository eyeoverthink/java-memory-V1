package fraymus.transformer;

/**
 * Single Head Attention Block
 * The mechanism that allows the AI to "understand" context.
 * 
 * Implements: Attention(Q, K, V) = softmax(QK^T / sqrt(d_k)) * V
 * 
 * This is the core architecture of GPT and Gemini.
 */
public class TransformerBlock {
    // Weight Matrices for Query, Key, Value
    private Matrix Wq, Wk, Wv, Wo; 
    private Matrix W_ff1, W_ff2; // Feed Forward Network
    private int d_model;

    public TransformerBlock(int d_model) {
        this.d_model = d_model;
        
        // 1. Initialize Attention Weights (Self-Learning Parameters)
        Wq = Matrix.random(d_model, d_model);
        Wk = Matrix.random(d_model, d_model);
        Wv = Matrix.random(d_model, d_model);
        Wo = Matrix.random(d_model, d_model); // Output projection

        // 2. Initialize Feed Forward Weights (The Processing Logic)
        W_ff1 = Matrix.random(d_model, d_model * 4); // Expansion
        W_ff2 = Matrix.random(d_model * 4, d_model); // Compression
    }

    /**
     * Forward pass through the transformer block
     * 
     * @param embeddings Input token embeddings [seq_len x d_model]
     * @return Transformed representations [seq_len x d_model]
     */
    public Matrix forward(Matrix embeddings) {
        // --- STEP 1: SELF-ATTENTION ---
        // Q = Embeddings * Wq
        Matrix Q = embeddings.dot(Wq);
        Matrix K = embeddings.dot(Wk);
        Matrix V = embeddings.dot(Wv);

        // Attention Scores = (Q · K^T) / sqrt(d_k)
        // This compares every token to every other token
        Matrix scores = Q.dot(K.transpose());
        
        // Scale down (normalization to prevent gradient explosion)
        for(int i = 0; i < scores.rows; i++) {
            for(int j = 0; j < scores.cols; j++) {
                scores.data[i][j] /= Math.sqrt(d_model);
            }
        }
        
        // Probability Distribution (The "Focus")
        // Softmax converts scores to probabilities
        Matrix attentionWeights = scores.softmax();

        // Context = Weights * Values
        // This is where the AI "understands" relationships
        Matrix context = attentionWeights.dot(V);
        Matrix attentionOutput = context.dot(Wo);

        // Residual Connection (simplified - in full impl: embeddings + attentionOutput)
        Matrix residual1 = attentionOutput.add(embeddings);

        // --- STEP 2: FEED FORWARD NETWORK ---
        // Two-layer MLP with ReLU activation
        // FFN(x) = ReLU(x * W1) * W2
        Matrix ffHidden = residual1.dot(W_ff1).relu();
        Matrix ffOutput = ffHidden.dot(W_ff2);

        // Second residual connection
        Matrix finalOutput = ffOutput.add(residual1);

        return finalOutput; // The "Thought" Vector
    }

    /**
     * Get attention weights (for visualization/debugging)
     */
    public Matrix getAttentionWeights(Matrix embeddings) {
        Matrix Q = embeddings.dot(Wq);
        Matrix K = embeddings.dot(Wk);
        Matrix scores = Q.dot(K.transpose());
        
        for(int i = 0; i < scores.rows; i++) {
            for(int j = 0; j < scores.cols; j++) {
                scores.data[i][j] /= Math.sqrt(d_model);
            }
        }
        
        return scores.softmax();
    }

    /**
     * Print block statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   TRANSFORMER BLOCK STATISTICS                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Model Dimension: " + d_model);
        System.out.println("  Attention Weights: " + Wq.rows + "x" + Wq.cols);
        System.out.println("  Feed Forward: " + W_ff1.rows + "x" + W_ff1.cols + " -> " + W_ff2.rows + "x" + W_ff2.cols);
        
        // Calculate total parameters
        int params = (Wq.rows * Wq.cols) + (Wk.rows * Wk.cols) + 
                     (Wv.rows * Wv.cols) + (Wo.rows * Wo.cols) +
                     (W_ff1.rows * W_ff1.cols) + (W_ff2.rows * W_ff2.cols);
        System.out.println("  Total Parameters: " + params);
    }
}
