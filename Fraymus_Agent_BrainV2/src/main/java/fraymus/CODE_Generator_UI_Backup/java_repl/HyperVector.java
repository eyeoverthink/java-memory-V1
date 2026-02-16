/**
 * HyperVector.java - N-Dimensional Coordinate System
 * 
 * 🧬 THE HYPER-COORDINATE
 * 
 * Represents a location in N-Dimensional Space.
 * 
 * Dimension Mapping:
 * - Dim 0-2: Spatial (X, Y, Z)
 * - Dim 3: Time (T)
 * - Dim 4: Entropy
 * - Dim 5: Sentiment
 * - Dim 6: Complexity
 * - Dim 7-10: M-Theory Compactified Dimensions
 * - Dim 11-16: Consciousness Dimensions
 * - Dim 17+: The Unknown
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 126 (Hyper-Cosmos)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.Arrays;

/**
 * A point in N-dimensional space.
 */
public class HyperVector {
    
    private static final double PHI = 1.618033988749895;
    
    private final double[] coords;
    
    /**
     * Create zero vector in N dimensions.
     */
    public HyperVector(int dimensions) {
        this.coords = new double[dimensions];
    }
    
    /**
     * Create vector from coordinates.
     */
    public HyperVector(double... coordinates) {
        this.coords = coordinates.clone();
    }
    
    /**
     * Create random vector (Big Bang dispersal).
     */
    public static HyperVector random(int dimensions, double scale) {
        HyperVector v = new HyperVector(dimensions);
        for (int i = 0; i < dimensions; i++) {
            v.coords[i] = (Math.random() - 0.5) * scale;
        }
        return v;
    }
    
    /**
     * Calculate Euclidean distance in N-dimensional space.
     * 
     * d = sqrt(Σ(xi - yi)²)
     */
    public double distanceTo(HyperVector other) {
        if (this.coords.length != other.coords.length) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        double sum = 0;
        for (int i = 0; i < coords.length; i++) {
            double diff = this.coords[i] - other.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Add another vector (in-place).
     */
    public void add(HyperVector other) {
        if (this.coords.length != other.coords.length) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        for (int i = 0; i < coords.length; i++) {
            this.coords[i] += other.coords[i];
        }
    }
    
    /**
     * Subtract another vector (returns new vector).
     */
    public HyperVector subtract(HyperVector other) {
        if (this.coords.length != other.coords.length) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        HyperVector result = new HyperVector(coords.length);
        for (int i = 0; i < coords.length; i++) {
            result.coords[i] = this.coords[i] - other.coords[i];
        }
        return result;
    }
    
    /**
     * Scale by scalar (in-place).
     */
    public void scale(double scalar) {
        for (int i = 0; i < coords.length; i++) {
            this.coords[i] *= scalar;
        }
    }
    
    /**
     * Get magnitude (length) of vector.
     */
    public double magnitude() {
        double sum = 0;
        for (double coord : coords) {
            sum += coord * coord;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * Normalize to unit vector.
     */
    public void normalize() {
        double mag = magnitude();
        if (mag > 0) {
            scale(1.0 / mag);
        }
    }
    
    /**
     * Get normalized copy.
     */
    public HyperVector normalized() {
        HyperVector copy = clone();
        copy.normalize();
        return copy;
    }
    
    /**
     * Dot product.
     */
    public double dot(HyperVector other) {
        if (this.coords.length != other.coords.length) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        double sum = 0;
        for (int i = 0; i < coords.length; i++) {
            sum += this.coords[i] * other.coords[i];
        }
        return sum;
    }
    
    /**
     * Get number of dimensions.
     */
    public int dimensions() {
        return coords.length;
    }
    
    /**
     * Get coordinate at dimension.
     */
    public double get(int dim) {
        return coords[dim];
    }
    
    /**
     * Set coordinate at dimension.
     */
    public void set(int dim, double value) {
        coords[dim] = value;
    }
    
    /**
     * Clone this vector.
     */
    @Override
    public HyperVector clone() {
        return new HyperVector(coords.clone());
    }
    
    /**
     * Project to 3D (for visualization).
     * Uses first 3 dimensions.
     */
    public double[] project3D() {
        return new double[] {
            coords.length > 0 ? coords[0] : 0,
            coords.length > 1 ? coords[1] : 0,
            coords.length > 2 ? coords[2] : 0
        };
    }
    
    /**
     * Project to 2D slice (for visualization).
     */
    public double[] projectSlice(int dimX, int dimY) {
        return new double[] {
            coords[dimX],
            coords[dimY]
        };
    }
    
    @Override
    public String toString() {
        if (coords.length <= 4) {
            return Arrays.toString(coords);
        } else {
            return String.format("[%.2f, %.2f, %.2f, ... %d dims]",
                coords[0], coords[1], coords[2], coords.length);
        }
    }
}
