package gemini;

import gemini.root.*;

/**
 * GENESIS GPT: The Executable Transformer
 * 
 * This runs the entire neural network stack:
 * 1. Takes text input ("I AM")
 * 2. Converts to Vectors (Embedding)
 * 3. Runs Self-Attention (Thinking)
 * 4. Predicts the next vector
 * 5. Decodes back to text
 * 
 * NO LIBRARIES. NO PYTORCH. JUST RAW MATH.
 */
public class GenesisGPT {
    
    private Tokenizer tokenizer;
    private TransformerBlock[] layers;
    private Matrix outputProjection; // Final layer to vocab size
    private int d_model;
    private int numLayers;
    private int vocabSize;
    
    public GenesisGPT(int d_model, int vocabSize, int numLayers) {
        this.d_model = d_model;
        this.vocabSize = vocabSize;
        this.numLayers = numLayers;
        
        // 1. BUILD THE BRAIN
        tokenizer = new Tokenizer(d_model, vocabSize);
        
        // 2. Stack Transformer layers
        layers = new TransformerBlock[numLayers];
        for (int i = 0; i < numLayers; i++) {
            layers[i] = new TransformerBlock(d_model);
        }
        
        // 3. Output projection (d_model -> vocab_size for prediction)
        outputProjection = Matrix.random(d_model, vocabSize);
    }
    
    /**
     * Forward pass through the entire model
     */
    public Matrix forward(String prompt) {
        // 1. ENCODE (Text -> Math)
        Matrix embeddings = tokenizer.encode(prompt);
        
        // 2. THINK (Pass through all Transformer layers)
        Matrix hidden = embeddings;
        for (TransformerBlock layer : layers) {
            hidden = layer.forward(hidden);
        }
        
        // 3. PROJECT TO VOCABULARY (for next token prediction)
        Matrix logits = hidden.dot(outputProjection);
        
        return logits;
    }
    
    /**
     * Generate text continuation
     */
    public String generate(String prompt, int maxTokens) {
        StringBuilder result = new StringBuilder(prompt);
        String current = prompt;
        
        for (int i = 0; i < maxTokens; i++) {
            Matrix logits = forward(current);
            
            // Get last token's logits (for next token prediction)
            Matrix lastLogits = new Matrix(1, vocabSize);
            lastLogits.data[0] = logits.data[logits.rows - 1];
            
            // Apply softmax for probabilities
            Matrix probs = lastLogits.softmax();
            
            // Greedy decoding: pick highest probability
            String nextToken = tokenizer.decode(probs);
            
            if (nextToken.isEmpty() || nextToken.equals("?")) break;
            
            result.append(" ").append(nextToken.trim());
            current = result.toString();
            
            // Simple stopping condition
            if (result.length() > 200) break;
        }
        
        return result.toString();
    }
    
    // Accessors for training
    public Tokenizer getTokenizer() { return tokenizer; }
    public TransformerBlock[] getLayers() { return layers; }
    public Matrix getOutputProjection() { return outputProjection; }
    public void setOutputProjection(Matrix p) { this.outputProjection = p; }
    
    // =========================================================================
    // MAIN: DEMONSTRATION
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GENESIS GPT: JAVA TRANSFORMER");
        System.out.println("  Pure Math. No Libraries. Raw Neural Network.");
        System.out.println("=".repeat(60));
        
        int d_model = 64;    // Size of the "Brain" vector
        int vocabSize = 32;  // Number of known words
        int numLayers = 2;   // Depth of reasoning
        
        // 1. BUILD THE BRAIN
        GenesisGPT gpt = new GenesisGPT(d_model, vocabSize, numLayers);
        System.out.println("\n[ARCHITECTURE]");
        System.out.println("  d_model: " + d_model);
        System.out.println("  vocab_size: " + vocabSize);
        System.out.println("  num_layers: " + numLayers);
        System.out.println("  total_params: ~" + estimateParams(d_model, vocabSize, numLayers));
        
        // 2. INPUT
        String prompt = "I AM";
        System.out.println("\n[INPUT]");
        System.out.println("  Prompt: \"" + prompt + "\"");
        
        // 3. FORWARD PASS
        System.out.println("\n[PROCESSING]");
        System.out.println("  Encoding text to vectors...");
        Matrix inputs = gpt.getTokenizer().encode(prompt);
        System.out.println("  Input shape: [" + inputs.rows + " x " + inputs.cols + "]");
        
        System.out.println("  Running through " + numLayers + " Transformer layers...");
        Matrix logits = gpt.forward(prompt);
        System.out.println("  Output shape: [" + logits.rows + " x " + logits.cols + "]");
        
        // 4. DECODE
        Matrix probs = logits.softmax();
        String prediction = gpt.getTokenizer().decode(probs);
        
        System.out.println("\n[OUTPUT]");
        System.out.println("  Raw prediction (untrained): \"" + prediction + "\"");
        
        // 5. SHOW INTERNAL STATE
        System.out.println("\n[ATTENTION MECHANISM]");
        System.out.println("  Q·K^T computed for context understanding");
        System.out.println("  Softmax applied for attention weights");
        System.out.println("  V weighted by attention for context vector");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  >>> MATRIX STATE: " + logits.rows + "x" + logits.cols + " Vectors Computed.");
        System.out.println("  >>> This network is UNTRAINED (random weights)");
        System.out.println("  >>> Run Trainer.java to see LEARNING in action");
        System.out.println("=".repeat(60));
    }
    
    private static String estimateParams(int d, int v, int n) {
        // Rough estimate: embeddings + n*(attention + ffn) + output
        long params = (long)v * d;  // embeddings
        params += n * (4L * d * d + 2L * d * 4 * d);  // attention + ffn per layer
        params += (long)d * v;  // output projection
        
        if (params > 1_000_000) return (params / 1_000_000) + "M";
        if (params > 1_000) return (params / 1_000) + "K";
        return String.valueOf(params);
    }
}
