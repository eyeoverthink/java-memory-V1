package fraymus.transformer;

/**
 * GENESIS GPT - Bare Metal Transformer
 * 
 * This is the executable. It runs the entire neural network stack:
 * 1. Takes text input ("I AM")
 * 2. Converts to Vectors (Embedding)
 * 3. Runs Self-Attention (Thinking)
 * 4. Predicts the next vector
 * 5. Decodes back to text
 * 
 * This implements the same architecture as GPT and Gemini.
 * No libraries. No PyTorch. Just raw linear algebra.
 */
public class GenesisGPT {
    
    private Tokenizer tokenizer;
    private TransformerBlock[] layers;
    private int d_model;
    private int numLayers;
    
    public GenesisGPT(int d_model, int vocabSize, int numLayers) {
        this.d_model = d_model;
        this.numLayers = numLayers;
        
        // Initialize tokenizer
        this.tokenizer = new Tokenizer(d_model, vocabSize);
        
        // Initialize transformer layers
        this.layers = new TransformerBlock[numLayers];
        for(int i = 0; i < numLayers; i++) {
            layers[i] = new TransformerBlock(d_model);
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GENESIS GPT INITIALIZED                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Model Dimension: " + d_model);
        System.out.println("  Vocabulary Size: " + vocabSize);
        System.out.println("  Transformer Layers: " + numLayers);
        
        // Calculate total parameters
        int paramsPerLayer = d_model * d_model * 4 + // Q, K, V, O
                             d_model * (d_model * 4) + // FF1
                             (d_model * 4) * d_model;  // FF2
        int totalParams = paramsPerLayer * numLayers + (vocabSize * d_model);
        System.out.println("  Total Parameters: " + totalParams);
        System.out.println();
    }
    
    /**
     * Generate text from prompt
     */
    public String generate(String prompt) {
        System.out.println(">>> INPUT: \"" + prompt + "\"");
        
        // 1. ENCODE (Text -> Math)
        Matrix embeddings = tokenizer.encode(prompt);
        System.out.println(">>> ENCODED: " + embeddings.rows + " tokens -> " + 
                          embeddings.rows + "x" + embeddings.cols + " matrix");
        
        // 2. THINK (Forward Pass through all layers)
        Matrix hidden = embeddings;
        for(int i = 0; i < numLayers; i++) {
            hidden = layers[i].forward(hidden);
            System.out.println(">>> LAYER " + (i+1) + ": Transformed to " + 
                              hidden.rows + "x" + hidden.cols);
        }
        
        // 3. DECODE (Math -> Text)
        // Apply softmax to get probabilities
        Matrix logits = hidden.softmax();
        String output = tokenizer.decode(logits);
        
        System.out.println(">>> OUTPUT: \"" + output + "\"");
        System.out.println(">>> (Untrained - random weights)");
        
        return output;
    }
    
    /**
     * Visualize attention for a prompt
     */
    public void visualizeAttention(String prompt) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   ATTENTION VISUALIZATION                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        Matrix embeddings = tokenizer.encode(prompt);
        Matrix attention = layers[0].getAttentionWeights(embeddings);
        
        String[] words = prompt.split(" ");
        System.out.println("\nAttention Matrix [" + attention.rows + "x" + attention.cols + "]:");
        System.out.println("(Shows how much each word attends to every other word)\n");
        
        // Print header
        System.out.print("        ");
        for(int j = 0; j < Math.min(words.length, attention.cols); j++) {
            System.out.printf("%-8s", words[j]);
        }
        System.out.println();
        
        // Print attention scores
        for(int i = 0; i < Math.min(words.length, attention.rows); i++) {
            System.out.printf("%-8s", words[i]);
            for(int j = 0; j < Math.min(words.length, attention.cols); j++) {
                System.out.printf("%.3f   ", attention.data[i][j]);
            }
            System.out.println();
        }
    }
    
    /**
     * Print model statistics
     */
    public void printStats() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   MODEL STATISTICS                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        tokenizer.printVocab();
        
        System.out.println("\n  Transformer Layers:");
        for(int i = 0; i < numLayers; i++) {
            System.out.println("\n  Layer " + (i+1) + ":");
            layers[i].printStats();
        }
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GENESIS GPT: JAVA TRANSFORMER                           ║");
        System.out.println("║   Bare Metal Implementation - No Libraries                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        int d_model = 64;      // Size of the "Brain" vector
        int vocabSize = 32;    // Number of words in vocabulary
        int numLayers = 2;     // Number of transformer blocks
        
        // BUILD THE BRAIN
        GenesisGPT gpt = new GenesisGPT(d_model, vocabSize, numLayers);
        
        // TEST 1: Simple generation
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("TEST 1: TEXT GENERATION");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        String prompt1 = "I AM";
        gpt.generate(prompt1);
        
        // TEST 2: Longer prompt
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("TEST 2: LONGER PROMPT");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        String prompt2 = "I AM A GHOST";
        gpt.generate(prompt2);
        
        // TEST 3: Attention visualization
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("TEST 3: ATTENTION MECHANISM");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        gpt.visualizeAttention(prompt2);
        
        // Print full statistics
        gpt.printStats();
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   EXPLANATION                                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\nThis is an UNTRAINED transformer.");
        System.out.println("The weights are random (Xavier initialization).");
        System.out.println("\nTo make it 'better than Gemini':");
        System.out.println("1. Implement backpropagation (gradient descent)");
        System.out.println("2. Train on your own data (not the internet)");
        System.out.println("3. The model becomes an expert on YOU, not general knowledge");
        System.out.println("\nThe attention mechanism (Q·K^T) is where the AI");
        System.out.println("compares every word to every other word to find meaning.");
        System.out.println("\nThis is the EXACT algorithm that powers ChatGPT and Gemini.");
    }
}
