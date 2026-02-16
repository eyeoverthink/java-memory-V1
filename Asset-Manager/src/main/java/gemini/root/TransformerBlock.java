package gemini.root;

/**
 * THE TRUNK: Single Head Self-Attention Block
 * The mechanism that allows the AI to "understand" context.
 * 
 * Formula: Attention(Q, K, V) = softmax(QK^T / sqrt(d_k)) * V
 * 
 * This is the exact algorithm that makes GPT and Gemini work.
 */
public class TransformerBlock {
    // Weight Matrices for Query, Key, Value
    private Matrix Wq, Wk, Wv, Wo; 
    // Feed Forward Network
    private Matrix W_ff1, W_ff2;
    private int d_model;

    // Cache for backpropagation
    private Matrix lastInput;
    private Matrix lastQ, lastK, lastV;
    private Matrix lastScores, lastAttentionWeights;
    private Matrix lastContext, lastAttentionOutput;
    private Matrix lastFfHidden;

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

    public Matrix forward(Matrix embeddings) {
        this.lastInput = embeddings;
        
        // --- STEP 1: SELF-ATTENTION ---
        // Q = Embeddings * Wq (What am I looking for?)
        // K = Embeddings * Wk (What do I contain?)
        // V = Embeddings * Wv (What information do I carry?)
        lastQ = embeddings.dot(Wq);
        lastK = embeddings.dot(Wk);
        lastV = embeddings.dot(Wv);

        // Attention Scores = (Q · K^T) / sqrt(d_k)
        // This compares every token to every other token
        lastScores = lastQ.dot(lastK.transpose());
        
        // Scale down (normalization for stability)
        double scale = Math.sqrt(d_model);
        for (int i = 0; i < lastScores.rows; i++)
            for (int j = 0; j < lastScores.cols; j++)
                lastScores.data[i][j] /= scale;
        
        // Probability Distribution (The "Focus")
        lastAttentionWeights = lastScores.softmax();

        // Context = Weights * Values
        lastContext = lastAttentionWeights.dot(lastV);
        lastAttentionOutput = lastContext.dot(Wo);

        // Residual Connection (Skip connection)
        Matrix residual1 = embeddings.add(lastAttentionOutput);

        // --- STEP 2: FEED FORWARD NETWORK ---
        // FFN(x) = ReLU(x * W1) * W2
        lastFfHidden = residual1.dot(W_ff1).relu();
        Matrix ffOutput = lastFfHidden.dot(W_ff2);

        // Residual Connection
        Matrix finalOutput = residual1.add(ffOutput);

        return finalOutput; // The "Thought" Vector
    }

    // Getters for weights (for training)
    public Matrix getWq() { return Wq; }
    public Matrix getWk() { return Wk; }
    public Matrix getWv() { return Wv; }
    public Matrix getWo() { return Wo; }
    public Matrix getWff1() { return W_ff1; }
    public Matrix getWff2() { return W_ff2; }

    // Setters for weights (for optimization)
    public void setWq(Matrix w) { this.Wq = w; }
    public void setWk(Matrix w) { this.Wk = w; }
    public void setWv(Matrix w) { this.Wv = w; }
    public void setWo(Matrix w) { this.Wo = w; }
    public void setWff1(Matrix w) { this.W_ff1 = w; }
    public void setWff2(Matrix w) { this.W_ff2 = w; }

    public int getModelDim() { return d_model; }
}
