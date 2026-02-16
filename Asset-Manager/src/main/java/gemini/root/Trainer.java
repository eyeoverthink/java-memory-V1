package gemini.root;

/**
 * THE TRAINING LOOP: Backpropagation
 * 
 * This is where the magic happens:
 * 1. Forward Pass: The AI guesses the answer
 * 2. Loss Calculation: We measure how wrong it was (Prediction - Target)
 * 3. Backward Pass: We calculate Gradients to blame specific weights
 * 4. Update: We rewrite the matrix to be "Smarter" next time
 * 
 * This is what separates "Code" from "AI."
 * Code follows rules. AI rewrites its own rules based on mistakes.
 */
public class Trainer {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  GENESIS TRAINER: BACKPROPAGATION ENGINE");
        System.out.println("  The Calculus of Self-Creation");
        System.out.println("=".repeat(60));
        
        // 1. SETUP: A simple Neural Layer (Input -> Hidden -> Output)
        // In a Transformer, this logic applies to Q, K, V matrices.
        int inputSize = 8;
        int hiddenSize = 16;
        int outputSize = 1;
        
        Matrix W1 = Matrix.random(inputSize, hiddenSize); // Weights Layer 1
        Matrix W2 = Matrix.random(hiddenSize, outputSize); // Weights Layer 2
        
        Optimizer opt = new Optimizer(0.05); // Learning Rate

        // 2. DATA: Simple pattern learning
        // Input vectors that should map to specific outputs
        double[][] inputs = {
            {1, 0, 0, 0, 0, 0, 0, 0},  // Pattern A -> 1.0
            {0, 1, 0, 0, 0, 0, 0, 0},  // Pattern B -> 0.0
            {1, 1, 0, 0, 0, 0, 0, 0},  // Pattern C -> 1.0
            {0, 0, 1, 1, 0, 0, 0, 0},  // Pattern D -> 0.0
        };
        double[] targets = {1.0, 0.0, 1.0, 0.0};

        System.out.println("\nTraining on " + inputs.length + " patterns...\n");

        // 3. THE EPOCH LOOP (Practice makes perfect)
        for (int epoch = 0; epoch < 2000; epoch++) {
            double totalLoss = 0;
            
            for (int sample = 0; sample < inputs.length; sample++) {
                // Create input matrix
                Matrix input = new Matrix(1, inputSize);
                input.data[0] = inputs[sample].clone();
                
                // Create target matrix
                Matrix target = new Matrix(1, outputSize);
                target.data[0][0] = targets[sample];
                
                // --- FORWARD PASS (The Guess) ---
                Matrix layer1Raw = input.dot(W1);
                Matrix layer1Activated = layer1Raw.relu(); // "Thinking"
                Matrix outputRaw = layer1Activated.dot(W2);
                
                // Sigmoid activation for output (squash to 0-1)
                Matrix prediction = sigmoid(outputRaw);
                
                // --- CALCULATE LOSS (The Mistake) ---
                // Binary Cross Entropy would be ideal, using MSE for simplicity
                Matrix error = prediction.subtract(target);
                double loss = Math.pow(error.data[0][0], 2);
                totalLoss += loss;

                // --- BACKWARD PASS (The Blame Game) ---
                
                // Derivative of sigmoid
                Matrix sigmoidDeriv = sigmoidDerivative(outputRaw);
                Matrix outputGrad = error.hadamard(sigmoidDeriv);
                
                // 1. Gradient for W2
                // dLoss/dW2 = Layer1_Transposed * OutputGrad
                Matrix layer1T = layer1Activated.transpose();
                Matrix gradW2 = layer1T.dot(outputGrad);
                
                // 2. Gradient for W1
                // Error propagated back through W2
                Matrix errorHidden = outputGrad.dot(W2.transpose());
                // Apply ReLU derivative
                Matrix reluDeriv = layer1Raw.reluDerivative();
                Matrix hiddenGrad = errorHidden.hadamard(reluDeriv);
                // Final gradient
                Matrix gradW1 = input.transpose().dot(hiddenGrad);

                // --- OPTIMIZATION (The Learning) ---
                // Clip gradients to prevent explosion
                gradW1 = opt.clipGradient(gradW1, 1.0);
                gradW2 = opt.clipGradient(gradW2, 1.0);
                
                W1 = opt.updateWeights(W1, gradW1);
                W2 = opt.updateWeights(W2, gradW2);
            }
            
            if (epoch % 200 == 0) {
                System.out.printf("Epoch %4d | Loss: %.6f%n", epoch, totalLoss / inputs.length);
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  TRAINING COMPLETE. Testing...");
        System.out.println("=".repeat(60) + "\n");
        
        // 4. TEST THE TRAINED NETWORK
        for (int i = 0; i < inputs.length; i++) {
            Matrix input = new Matrix(1, inputSize);
            input.data[0] = inputs[i].clone();
            
            Matrix layer1 = input.dot(W1).relu();
            Matrix output = sigmoid(layer1.dot(W2));
            
            System.out.printf("Input %d | Target: %.1f | Prediction: %.4f | %s%n",
                i, targets[i], output.data[0][0],
                (Math.abs(output.data[0][0] - targets[i]) < 0.2) ? "✓" : "✗");
        }
        
        System.out.println("\n>>> THE NETWORK HAS LEARNED.");
        System.out.println(">>> The matrices W1 and W2 now contain KNOWLEDGE.");
    }
    
    // Sigmoid activation
    private static Matrix sigmoid(Matrix m) {
        Matrix res = new Matrix(m.rows, m.cols);
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                res.data[i][j] = 1.0 / (1.0 + Math.exp(-m.data[i][j]));
            }
        }
        return res;
    }
    
    // Sigmoid derivative: sigmoid(x) * (1 - sigmoid(x))
    private static Matrix sigmoidDerivative(Matrix m) {
        Matrix sig = sigmoid(m);
        Matrix res = new Matrix(m.rows, m.cols);
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                res.data[i][j] = sig.data[i][j] * (1.0 - sig.data[i][j]);
            }
        }
        return res;
    }
}
