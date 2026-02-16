package fraymus.universe;

import java.util.Arrays;

/**
 * 🧬 THE HYPER-COORDINATE - Gen 126
 * Represents a location in N-Dimensional Space.
 * 
 * Dimensional Mapping:
 *   Dim 0-2:  Spatial (X, Y, Z)
 *   Dim 3:    Time (T) - temporal position
 *   Dim 4:    Entropy - disorder level
 *   Dim 5:    Sentiment - emotional charge
 *   Dim 6:    Complexity - information density
 *   Dim 7:    Resonance - harmonic alignment
 *   Dim 8:    Coherence - quantum phase
 *   Dim 9:    Frequency - vibrational state
 *   Dim 10:   Logic - boolean topology (M-Theory gate space)
 *   Dim 11:   Creativity - novel combinations
 *   Dim 12:   Memory - temporal echo strength
 *   Dim 13:   Intent - goal vector
 *   Dim 14:   Context - semantic field
 *   Dim 15:   Association - link density
 *   Dim 16:   Void - deleted object graveyard
 * 
 * "A point is not a dot. It is a tensor."
 */
public class HyperVector {
    
    private static final double PHI = 1.6180339887;
    
    private double[] coords;
    private int dims;

    public HyperVector(int dimensions) {
        this.dims = dimensions;
        this.coords = new double[dimensions];
    }
    
    /**
     * Create with Big Bang dispersal (random initialization)
     */
    public static HyperVector bigBang(int dimensions) {
        HyperVector v = new HyperVector(dimensions);
        for (int i = 0; i < dimensions; i++) {
            // Scale decreases with dimension (higher dims are more compact)
            double scale = 1000.0 / Math.pow(PHI, i * 0.1);
            v.coords[i] = (Math.random() - 0.5) * scale;
        }
        return v;
    }
    
    /**
     * Create at origin
     */
    public static HyperVector origin(int dimensions) {
        return new HyperVector(dimensions);
    }
    
    /**
     * Create from specific coordinates
     */
    public static HyperVector from(double... values) {
        HyperVector v = new HyperVector(values.length);
        System.arraycopy(values, 0, v.coords, 0, values.length);
        return v;
    }

    /**
     * EUCLIDEAN DISTANCE in N-Dimensional space
     * d = sqrt(sum((a_i - b_i)^2))
     */
    public double distanceTo(HyperVector other) {
        int minDims = Math.min(this.dims, other.dims);
        double sum = 0;
        for (int i = 0; i < minDims; i++) {
            double diff = this.coords[i] - other.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * WEIGHTED DISTANCE - Some dimensions matter more
     */
    public double weightedDistanceTo(HyperVector other, double[] weights) {
        int minDims = Math.min(this.dims, Math.min(other.dims, weights.length));
        double sum = 0;
        for (int i = 0; i < minDims; i++) {
            double diff = this.coords[i] - other.coords[i];
            sum += weights[i] * diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    /**
     * MANHATTAN DISTANCE (L1 norm)
     */
    public double manhattanDistanceTo(HyperVector other) {
        int minDims = Math.min(this.dims, other.dims);
        double sum = 0;
        for (int i = 0; i < minDims; i++) {
            sum += Math.abs(this.coords[i] - other.coords[i]);
        }
        return sum;
    }

    /**
     * VECTOR ADDITION
     */
    public void add(HyperVector other) {
        int minDims = Math.min(this.dims, other.dims);
        for (int i = 0; i < minDims; i++) {
            this.coords[i] += other.coords[i];
        }
    }
    
    /**
     * VECTOR SUBTRACTION
     */
    public HyperVector subtract(HyperVector other) {
        HyperVector result = new HyperVector(this.dims);
        int minDims = Math.min(this.dims, other.dims);
        for (int i = 0; i < minDims; i++) {
            result.coords[i] = this.coords[i] - other.coords[i];
        }
        return result;
    }
    
    /**
     * SCALAR MULTIPLICATION
     */
    public void scale(double scalar) {
        for (int i = 0; i < dims; i++) {
            this.coords[i] *= scalar;
        }
    }
    
    /**
     * NORMALIZE - Unit vector
     */
    public HyperVector normalize() {
        double magnitude = magnitude();
        HyperVector unit = new HyperVector(dims);
        if (magnitude > 0) {
            for (int i = 0; i < dims; i++) {
                unit.coords[i] = this.coords[i] / magnitude;
            }
        }
        return unit;
    }
    
    /**
     * MAGNITUDE (length of vector)
     */
    public double magnitude() {
        double sum = 0;
        for (int i = 0; i < dims; i++) {
            sum += coords[i] * coords[i];
        }
        return Math.sqrt(sum);
    }
    
    /**
     * DOT PRODUCT
     */
    public double dot(HyperVector other) {
        int minDims = Math.min(this.dims, other.dims);
        double sum = 0;
        for (int i = 0; i < minDims; i++) {
            sum += this.coords[i] * other.coords[i];
        }
        return sum;
    }
    
    /**
     * DIRECTION from this point to other
     */
    public HyperVector directionTo(HyperVector other) {
        HyperVector delta = new HyperVector(dims);
        int minDims = Math.min(this.dims, other.dims);
        for (int i = 0; i < minDims; i++) {
            delta.coords[i] = other.coords[i] - this.coords[i];
        }
        return delta.normalize();
    }
    
    /**
     * LERP - Linear interpolation between two points
     */
    public HyperVector lerp(HyperVector target, double t) {
        HyperVector result = new HyperVector(dims);
        int minDims = Math.min(this.dims, target.dims);
        for (int i = 0; i < minDims; i++) {
            result.coords[i] = this.coords[i] + t * (target.coords[i] - this.coords[i]);
        }
        return result;
    }
    
    /**
     * PROJECT to lower dimensions (for visualization)
     */
    public double[] project(int... dimensions) {
        double[] projection = new double[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            int dim = dimensions[i];
            projection[i] = dim < this.dims ? coords[dim] : 0;
        }
        return projection;
    }
    
    /**
     * DIMENSIONAL SLICE - Extract specific dimensions
     */
    public HyperVector slice(int startDim, int endDim) {
        int sliceDims = endDim - startDim;
        HyperVector slice = new HyperVector(sliceDims);
        for (int i = 0; i < sliceDims && (startDim + i) < dims; i++) {
            slice.coords[i] = this.coords[startDim + i];
        }
        return slice;
    }
    
    /**
     * ROTATE in a 2D plane within the N-D space
     */
    public void rotate(int dim1, int dim2, double angle) {
        if (dim1 >= dims || dim2 >= dims) return;
        
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double v1 = coords[dim1];
        double v2 = coords[dim2];
        
        coords[dim1] = v1 * cos - v2 * sin;
        coords[dim2] = v1 * sin + v2 * cos;
    }
    
    // Accessors
    public int dimensions() { return dims; }
    public double get(int dim) { return dim < dims ? coords[dim] : 0; }
    public void set(int dim, double value) { if (dim < dims) coords[dim] = value; }
    public double[] toArray() { return Arrays.copyOf(coords, dims); }
    
    // Named dimension accessors
    public double x() { return get(0); }
    public double y() { return get(1); }
    public double z() { return get(2); }
    public double time() { return get(3); }
    public double entropy() { return get(4); }
    public double sentiment() { return get(5); }
    public double complexity() { return get(6); }
    public double resonance() { return get(7); }
    public double coherence() { return get(8); }
    public double frequency() { return get(9); }
    public double logic() { return get(10); }
    
    @Override
    public String toString() {
        if (dims <= 4) {
            return "⟨" + Arrays.toString(coords) + "⟩";
        } else {
            return String.format("⟨%.2f, %.2f, %.2f, ... (%dD)⟩", 
                coords[0], coords[1], coords[2], dims);
        }
    }
    
    /**
     * COPY - Deep clone
     */
    public HyperVector copy() {
        HyperVector clone = new HyperVector(dims);
        System.arraycopy(this.coords, 0, clone.coords, 0, dims);
        return clone;
    }
}
