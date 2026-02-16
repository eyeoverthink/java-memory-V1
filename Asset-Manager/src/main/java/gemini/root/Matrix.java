package gemini.root;

import java.util.Random;

/**
 * THE ROOT: Linear Algebra Engine
 * Manages Weights, Biases, and Dot Products.
 * 
 * This is the foundation of all neural networks.
 * No libraries. No PyTorch. Just raw math.
 */
public class Matrix {
    public double[][] data;
    public int rows, cols;
    private static final Random rng = new Random();

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    // Initialize with Xavier/Glorot weights (The "Knowledge" Seed)
    public static Matrix random(int rows, int cols) {
        Matrix m = new Matrix(rows, cols);
        double range = Math.sqrt(6.0 / (rows + cols));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.data[i][j] = (rng.nextDouble() * 2 * range) - range;
            }
        }
        return m;
    }

    // Dot Product: The Core of Neural Networks
    // This is where Q·K^T happens in Attention
    public Matrix dot(Matrix other) {
        if (this.cols != other.rows) throw new RuntimeException("Dimension Mismatch: " + this.cols + " != " + other.rows);
        Matrix res = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0;
                for (int k = 0; k < this.cols; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                res.data[i][j] = sum;
            }
        }
        return res;
    }

    // ReLU Activation (The "Firing" Neuron)
    // If positive, pass through. If negative, zero.
    public Matrix relu() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = Math.max(0, this.data[i][j]);
        return res;
    }

    // Softmax: Converting raw numbers into Probabilities
    // This is the "Focus" distribution in Attention
    public Matrix softmax() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) max = Math.max(max, data[i][j]); // Stability fix
            
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = Math.exp(data[i][j] - max);
                sum += res.data[i][j];
            }
            for (int j = 0; j < cols; j++) res.data[i][j] /= sum;
        }
        return res;
    }
    
    // Transpose for Attention (K^T)
    public Matrix transpose() {
        Matrix res = new Matrix(cols, rows);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[j][i] = data[i][j];
        return res;
    }

    // SUBTRACTION (For Error Calculation: Target - Prediction)
    public Matrix subtract(Matrix other) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = this.data[i][j] - other.data[i][j];
        return res;
    }

    // ADDITION (For Residual Connections)
    public Matrix add(Matrix other) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = this.data[i][j] + other.data[i][j];
        return res;
    }

    // SCALAR MULTIPLY (For Learning Rate: Gradient * 0.01)
    public Matrix multiply(double scalar) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = this.data[i][j] * scalar;
        return res;
    }

    // ELEMENT-WISE MULTIPLY (Hadamard Product)
    public Matrix hadamard(Matrix other) {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = this.data[i][j] * other.data[i][j];
        return res;
    }

    // DERIVATIVE OF RELU (If value > 0, gradient passes; else 0)
    public Matrix reluDerivative() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                res.data[i][j] = (this.data[i][j] > 0) ? 1.0 : 0.0;
        return res;
    }

    // Print for debugging
    public void print(String name) {
        System.out.println(name + " [" + rows + "x" + cols + "]:");
        for (int i = 0; i < Math.min(rows, 5); i++) {
            System.out.print("  [");
            for (int j = 0; j < Math.min(cols, 5); j++) {
                System.out.printf("%.4f ", data[i][j]);
            }
            if (cols > 5) System.out.print("...");
            System.out.println("]");
        }
        if (rows > 5) System.out.println("  ...");
    }
}
