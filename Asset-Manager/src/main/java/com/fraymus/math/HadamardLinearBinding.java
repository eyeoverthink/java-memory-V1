package com.fraymus.math;

/**
 * Hadamard-Derived Linear Binding for VSA operations
 * Author: Vaughn Scott
 */
public class HadamardLinearBinding {
    
    public static double[] bind(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        
        double[] result = new double[vectorA.length];
        for (int i = 0; i < vectorA.length; i++) {
            result[i] = vectorA[i] * vectorB[i];
        }
        return result;
    }
}
