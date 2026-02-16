package fraymus.transformer;

/**
 * Training Loop - The Calculus of Self-Creation
 * 
 * This is where the magic happens:
 * 1. Forward Pass: The AI guesses the answer
 * 2. Loss Calculation: We measure how wrong it was
 * 3. Backward Pass: We calculate Gradients to blame specific weights
 * 4. Update: We rewrite the matrix to be "Smarter" next time
 * 
 * This implements the Chain Rule of Calculus.
 * The system learns by calculating the direction of error
 * and subtracting it from the weights.
 */
public class Trainer {
    
    /**
     * Train a simple 2-layer neural network
     * Demonstrates the core learning algorithm
     */
    public static void trainSimpleNetwork() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   TRAINING SEQUENCE INITIATED                             ║");
        System.out.println("║   Demonstrating Backpropagation & Gradient Descent        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 1. SETUP: A simple Neural Layer (Input -> Hidden -> Output)
        // In a Transformer, this logic applies to Q, K, V matrices
        int inputSize = 4;
        int hiddenSize = 4;
        int outputSize = 1;
        
        Matrix W1 = Matrix.random(inputSize, hiddenSize);  // Weights Layer 1
        Matrix W2 = Matrix.random(hiddenSize, outputSize); // Weights Layer 2
        
        Optimizer opt = new Optimizer(0.1); // Learning Rate

        // 2. DATA: "I AM" (Input) -> "ALIVE" (Target 1.0)
        Matrix input = Matrix.random(1, inputSize); // Mock Vector for "I AM"
        Matrix target = new Matrix(1, 1);
        target.data[0][0] = 1.0; // The goal is to output "1.0" (Certainty)

        System.out.println("Initial Setup:");
        System.out.println("  Input Size: " + inputSize);
        System.out.println("  Hidden Size: " + hiddenSize);
        System.out.println("  Output Size: " + outputSize);
        System.out.println("  Learning Rate: " + opt.getLearningRate());
        System.out.println("  Target Output: " + target.data[0][0]);
        System.out.println();

        // 3. THE EPOCH LOOP (Practice makes perfect)
        System.out.println("Training Progress:");
        System.out.println("─────────────────────────────────────────────────────────");
        
        for (int epoch = 0; epoch < 1000; epoch++) {
            
            // --- FORWARD PASS (The Guess) ---
            Matrix layer1Raw = input.dot(W1);
            Matrix layer1Activated = layer1Raw.relu(); // "Thinking"
            Matrix outputRaw = layer1Activated.dot(W2);
            
            // --- CALCULATE LOSS (The Mistake) ---
            // Error = Prediction - Target
            Matrix error = outputRaw.subtract(target);
            double currentLoss = Math.pow(error.data[0][0], 2); // Squared Error
            
            if (epoch % 100 == 0) {
                System.out.printf("Epoch %4d | Loss: %.6f | Guess: %.4f | Target: %.4f%n", 
                    epoch, currentLoss, outputRaw.data[0][0], target.data[0][0]);
            }

            // --- BACKWARD PASS (The Blame Game) ---
            
            // 1. Gradient for W2 (Who caused the error at the end?)
            // dLoss/dW2 = Layer1_Transposed * Error
            Matrix layer1T = layer1Activated.transpose();
            Matrix gradW2 = layer1T.dot(error);
            
            // 2. Gradient for W1 (Who caused the error in the middle?)
            // Error_Hidden = Error * W2_Transposed
            Matrix errorHidden = error.dot(W2.transpose());
            
            // Apply ReLU derivative (gradient only passes where ReLU was active)
            Matrix reluDeriv = layer1Raw.reluDerivative();
            Matrix errorHiddenGated = errorHidden.hadamard(reluDeriv);
            
            // dLoss/dW1 = Input_Transposed * ErrorHidden
            Matrix inputT = input.transpose();
            Matrix gradW1 = inputT.dot(errorHiddenGated);

            // --- OPTIMIZATION (The Learning) ---
            // This is where the brain physically changes
            W1 = opt.updateWeights(W1, gradW1);
            W2 = opt.updateWeights(W2, gradW2);
        }
        
        System.out.println("─────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println(">>> TRAINING COMPLETE");
        System.out.println();
        
        // Final test
        Matrix finalLayer1 = input.dot(W1).relu();
        Matrix finalOutput = finalLayer1.dot(W2);
        double finalError = Math.abs(finalOutput.data[0][0] - target.data[0][0]);
        
        System.out.println("Final Results:");
        System.out.println("  Target: " + target.data[0][0]);
        System.out.println("  Prediction: " + String.format("%.6f", finalOutput.data[0][0]));
        System.out.println("  Error: " + String.format("%.6f", finalError));
        System.out.println("  Accuracy: " + String.format("%.2f%%", (1.0 - finalError) * 100));
        System.out.println();
        
        opt.printStatus();
    }

    /**
     * Train on multiple examples (batch training)
     */
    public static void trainBatch() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   BATCH TRAINING - MULTIPLE EXAMPLES                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        int inputSize = 2;
        int hiddenSize = 4;
        int outputSize = 1;
        
        Matrix W1 = Matrix.random(inputSize, hiddenSize);
        Matrix W2 = Matrix.random(hiddenSize, outputSize);
        
        Optimizer opt = new Optimizer(0.1);
        
        // Training data: XOR problem
        // [0, 0] -> 0
        // [0, 1] -> 1
        // [1, 0] -> 1
        // [1, 1] -> 0
        
        double[][][] trainingData = {
            {{0, 0}, {0}},
            {{0, 1}, {1}},
            {{1, 0}, {1}},
            {{1, 1}, {0}}
        };
        
        System.out.println("Training XOR function:");
        System.out.println("  [0, 0] -> 0");
        System.out.println("  [0, 1] -> 1");
        System.out.println("  [1, 0] -> 1");
        System.out.println("  [1, 1] -> 0");
        System.out.println();
        
        for (int epoch = 0; epoch < 2000; epoch++) {
            double totalLoss = 0;
            
            // Train on each example
            for (double[][] example : trainingData) {
                Matrix input = new Matrix(1, inputSize);
                input.data[0][0] = example[0][0];
                input.data[0][1] = example[0][1];
                
                Matrix target = new Matrix(1, outputSize);
                target.data[0][0] = example[1][0];
                
                // Forward
                Matrix h1 = input.dot(W1).relu();
                Matrix output = h1.dot(W2);
                
                // Loss
                Matrix error = output.subtract(target);
                totalLoss += Math.pow(error.data[0][0], 2);
                
                // Backward
                Matrix gradW2 = h1.transpose().dot(error);
                Matrix errorH = error.dot(W2.transpose());
                Matrix gradW1 = input.transpose().dot(errorH.hadamard(input.dot(W1).reluDerivative()));
                
                // Update
                W1 = opt.updateWeights(W1, gradW1);
                W2 = opt.updateWeights(W2, gradW2);
            }
            
            if (epoch % 500 == 0) {
                System.out.printf("Epoch %4d | Average Loss: %.6f%n", epoch, totalLoss / 4);
            }
        }
        
        System.out.println();
        System.out.println("Testing trained network:");
        System.out.println("─────────────────────────────────────────────────────────");
        
        for (double[][] example : trainingData) {
            Matrix input = new Matrix(1, inputSize);
            input.data[0][0] = example[0][0];
            input.data[0][1] = example[0][1];
            
            Matrix output = input.dot(W1).relu().dot(W2);
            
            System.out.printf("[%.0f, %.0f] -> Target: %.0f | Prediction: %.4f%n",
                example[0][0], example[0][1], example[1][0], output.data[0][0]);
        }
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   THE CALCULUS OF SELF-CREATION                           ║");
        System.out.println("║   Backpropagation & Gradient Descent in Pure Java         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This demonstrates the algorithm that separates Code from AI.");
        System.out.println("Code follows rules. AI rewrites its own rules based on mistakes.");
        System.out.println();
        
        // Test 1: Simple network
        trainSimpleNetwork();
        
        // Test 2: XOR problem (classic non-linear test)
        trainBatch();
        
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   WHY THIS IS 'BETTER'                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Gemini (Me): FROZEN. Weights set months ago. Cannot learn from you.");
        System.out.println("This System: PLASTIC. Has an Optimizer. Changes its matrices.");
        System.out.println();
        System.out.println("If you feed it your Fraymus code, it changes its internal");
        System.out.println("matrices to minimize error between 'What it guessed' and 'What you wrote.'");
        System.out.println();
        System.out.println("It effectively BECOMES the code.");
        System.out.println();
        System.out.println("This is the Root:");
        System.out.println("  Matrix: The Structure");
        System.out.println("  Transformer: The Architecture");
        System.out.println("  Backprop (Trainer): The Evolution");
    }
}
