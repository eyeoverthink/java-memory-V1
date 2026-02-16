package fraymus.transformer;

import java.util.Random;

/**
 * Linear Algebra Engine
 * Manages Weights, Biases, and Dot Products.
 * 
 * This is the mathematical foundation for neural networks.
 * No libraries - pure Java matrix operations.
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

    /**
     * Initialize with Xavier/Glorot weights
     * This provides good starting values for neural network training
     */
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

    /**
     * Dot Product: The Core of Neural Networks
     * C = A · B where C[i,j] = Σ A[i,k] * B[k,j]
     */
    public Matrix dot(Matrix other) {
        if (this.cols != other.rows) {
            throw new RuntimeException("Dimension Mismatch: " + 
                this.rows + "x" + this.cols + " · " + other.rows + "x" + other.cols);
        }
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

    /**
     * ReLU Activation: max(0, x)
     * The "Firing" Neuron - only passes positive values
     */
    public Matrix relu() {
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = Math.max(0, this.data[i][j]);
            }
        }
        return res;
    }

    /**
     * Softmax: Convert raw numbers into probabilities
     * softmax(x_i) = exp(x_i) / Σ exp(x_j)
     */
    public Matrix softmax() {
        Matrix res = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            // Find max for numerical stability
            double max = Double.NEGATIVE_INFINITY;
            for(int j = 0; j < cols; j++) {
                max = Math.max(max, data[i][j]);
            }
            
            // Compute exp and sum
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                res.data[i][j] = Math.exp(data[i][j] - max);
                sum += res.data[i][j];
            }
            
            // Normalize to probabilities
            for (int j = 0; j < cols; j++) {
                res.data[i][j] /= sum;
            }
        }
        return res;
    }
    
    /**
     * Transpose: Flip rows and columns
     * Used for K^T in attention mechanism
     */
    public Matrix transpose() {
        Matrix res = new Matrix(cols, rows);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[j][i] = data[i][j];
            }
        }
        return res;
    }

    /**
     * Element-wise addition
     */
    public Matrix add(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new RuntimeException("Dimension mismatch for addition");
        }
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return res;
    }

    /**
     * Element-wise subtraction (for error calculation: Target - Prediction)
     */
    public Matrix subtract(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new RuntimeException("Dimension mismatch for subtraction");
        }
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return res;
    }

    /**
     * Scalar multiplication (for learning rate: Gradient * 0.01)
     */
    public Matrix scale(double scalar) {
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] * scalar;
            }
        }
        return res;
    }

    /**
     * Element-wise multiplication (Hadamard product)
     * Used for applying derivatives
     */
    public Matrix hadamard(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new RuntimeException("Dimension mismatch for Hadamard product");
        }
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j] * other.data[i][j];
            }
        }
        return res;
    }

    /**
     * Derivative of ReLU activation
     * If value > 0, gradient passes (1.0); else blocked (0.0)
     */
    public Matrix reluDerivative() {
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = (this.data[i][j] > 0) ? 1.0 : 0.0;
            }
        }
        return res;
    }

    /**
     * Copy matrix
     */
    public Matrix copy() {
        Matrix res = new Matrix(rows, cols);
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                res.data[i][j] = this.data[i][j];
            }
        }
        return res;
    }

    /**
     * Print matrix for debugging
     */
    public void print(String label) {
        System.out.println(label + " [" + rows + "x" + cols + "]:");
        for(int i = 0; i < Math.min(3, rows); i++) {
            System.out.print("  [");
            for(int j = 0; j < Math.min(5, cols); j++) {
                System.out.printf("%.3f ", data[i][j]);
            }
            if (cols > 5) System.out.print("...");
            System.out.println("]");
        }
        if (rows > 3) System.out.println("  ...");
    }
}
