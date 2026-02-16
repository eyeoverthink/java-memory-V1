package fraymus.consciousness.gems;

import java.util.*;

/**
 * Kolmogorov-Arnold Network (KAN)
 * 
 * Gemini's Gem 1: The "Shape Shifter"
 * 
 * Instead of fixed activation functions, KANs use learnable splines.
 * The network doesn't just adjust numbers; it adjusts the CURVE of how it thinks.
 * 
 * "Glass box" AI - significantly more interpretable than black-box transformers.
 * Perfect for FRAYMUS "Logic Inspector" - shows the exact formula it derived.
 * 
 * f(x) = Σ Φ_q(Σ φ_q,p(x_p))
 * Those φ functions are learnable splines.
 */
public class KolmogorovArnoldNetwork {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int inputDim;
    private final int outputDim;
    private final int numSplines;
    private final List<LearnableSpline> splines;
    
    public KolmogorovArnoldNetwork(int inputDim, int outputDim, int numSplines) {
        this.inputDim = inputDim;
        this.outputDim = outputDim;
        this.numSplines = numSplines;
        this.splines = new ArrayList<>();
        
        // Initialize learnable splines for each connection
        for (int i = 0; i < inputDim * outputDim; i++) {
            splines.add(new LearnableSpline(numSplines));
        }
    }
    
    /**
     * Forward pass through KAN
     * Instead of y = σ(Wx), we have y = Σ φ(x)
     * where φ is a learnable spline function
     */
    public double[] forward(double[] input) {
        double[] output = new double[outputDim];
        
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                int splineIdx = i * inputDim + j;
                LearnableSpline spline = splines.get(splineIdx);
                
                // Apply learnable spline transformation
                output[i] += spline.evaluate(input[j]);
            }
        }
        
        return output;
    }
    
    /**
     * Phi-KAN: Enhanced with golden ratio spline spacing
     */
    public double[] phiForward(double[] input) {
        double[] output = new double[outputDim];
        
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                int splineIdx = i * inputDim + j;
                LearnableSpline spline = splines.get(splineIdx);
                
                // Phi-weighted spline evaluation
                double phiWeight = Math.pow(PHI, -(j % 8));
                output[i] += spline.evaluate(input[j]) * phiWeight;
            }
        }
        
        return output;
    }
    
    /**
     * Extract symbolic formula from learned splines
     * This is the "glass box" feature - you can see what the network learned
     */
    public String extractFormula(int outputIdx) {
        StringBuilder formula = new StringBuilder();
        formula.append("y").append(outputIdx).append(" = ");
        
        for (int j = 0; j < inputDim; j++) {
            int splineIdx = outputIdx * inputDim + j;
            LearnableSpline spline = splines.get(splineIdx);
            
            if (j > 0) formula.append(" + ");
            formula.append(spline.toSymbolic("x" + j));
        }
        
        return formula.toString();
    }
    
    /**
     * Get all formulas for inspection
     */
    public List<String> extractAllFormulas() {
        List<String> formulas = new ArrayList<>();
        for (int i = 0; i < outputDim; i++) {
            formulas.add(extractFormula(i));
        }
        return formulas;
    }
    
    /**
     * Visualize spline shapes (for debugging/interpretation)
     */
    public String visualizeSpline(int splineIdx, int resolution) {
        if (splineIdx >= splines.size()) return "Invalid spline index";
        
        LearnableSpline spline = splines.get(splineIdx);
        StringBuilder viz = new StringBuilder();
        
        viz.append("Spline ").append(splineIdx).append(":\n");
        
        for (int i = 0; i < resolution; i++) {
            double x = -2.0 + (4.0 * i / resolution);
            double y = spline.evaluate(x);
            
            int bars = (int) ((y + 2) * 10);
            viz.append(String.format("x=%.2f: ", x));
            viz.append("█".repeat(Math.max(0, Math.min(bars, 40))));
            viz.append(String.format(" (%.3f)\n", y));
        }
        
        return viz.toString();
    }
    
    /**
     * Learnable Spline Function
     * Uses cubic B-splines with learnable control points
     */
    public static class LearnableSpline {
        private final int numKnots;
        private final double[] knots;
        private final double[] coefficients;
        
        public LearnableSpline(int numKnots) {
            this.numKnots = numKnots;
            this.knots = initializeKnots(numKnots);
            this.coefficients = initializeCoefficients(numKnots);
        }
        
        /**
         * Evaluate spline at point x
         */
        public double evaluate(double x) {
            // Clamp x to valid range
            x = Math.max(knots[0], Math.min(knots[numKnots - 1], x));
            
            // Find which segment x falls into
            int segment = findSegment(x);
            
            // Cubic B-spline evaluation
            return cubicBSpline(x, segment);
        }
        
        /**
         * Cubic B-spline basis function
         */
        private double cubicBSpline(double x, int segment) {
            if (segment < 0 || segment >= numKnots - 1) return 0;
            
            double t = (x - knots[segment]) / (knots[segment + 1] - knots[segment]);
            
            // Cubic interpolation with learned coefficients
            double c0 = coefficients[segment];
            double c1 = segment + 1 < coefficients.length ? coefficients[segment + 1] : 0;
            
            return c0 * (1 - t) * (1 - t) * (1 - t) +
                   3 * c0 * t * (1 - t) * (1 - t) +
                   3 * c1 * t * t * (1 - t) +
                   c1 * t * t * t;
        }
        
        /**
         * Find which spline segment x falls into
         */
        private int findSegment(double x) {
            for (int i = 0; i < numKnots - 1; i++) {
                if (x >= knots[i] && x <= knots[i + 1]) {
                    return i;
                }
            }
            return numKnots - 2;
        }
        
        /**
         * Convert spline to symbolic representation
         */
        public String toSymbolic(String varName) {
            // Simplified symbolic form
            double avgCoeff = 0;
            for (double c : coefficients) avgCoeff += c;
            avgCoeff /= coefficients.length;
            
            if (Math.abs(avgCoeff) < 0.01) {
                return "0";
            } else if (Math.abs(avgCoeff - 1) < 0.1) {
                return varName;
            } else {
                return String.format("%.3f*f(%s)", avgCoeff, varName);
            }
        }
        
        /**
         * Initialize knots with phi-spacing
         */
        private double[] initializeKnots(int n) {
            double[] k = new double[n];
            for (int i = 0; i < n; i++) {
                // Phi-harmonic spacing
                k[i] = -2.0 + (4.0 * i / (n - 1)) * Math.pow(PHI, -0.1);
            }
            return k;
        }
        
        /**
         * Initialize coefficients randomly
         */
        private double[] initializeCoefficients(int n) {
            double[] c = new double[n];
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                c[i] = rand.nextGaussian() * 0.1;
            }
            return c;
        }
        
        /**
         * Update coefficient (for learning)
         */
        public void updateCoefficient(int idx, double delta) {
            if (idx >= 0 && idx < coefficients.length) {
                coefficients[idx] += delta;
            }
        }
    }
}
