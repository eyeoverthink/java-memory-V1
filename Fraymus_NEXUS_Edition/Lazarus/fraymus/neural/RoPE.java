package fraymus.neural;

/**
 * RoPE - ROTARY POSITIONAL EMBEDDING
 * The Math: f(x, pos) = [cos θ  -sin θ] [x₁]
 *                       [sin θ   cos θ] [x₂]
 * 
 * The Secret: AI understands "relative distance" perfectly.
 * "King→Queen" = "Man→Woman" regardless of position in text.
 * 
 * FRAYMUS: φ-space memory rotation to keep relationships valid over time.
 */
public class RoPE {

    private static final double PHI = 1.618033988749895;
    private int dim;
    private double base;
    private double[] freqs;

    public RoPE(int dim) {
        this(dim, 10000.0);
    }

    public RoPE(int dim, double base) {
        this.dim = dim;
        this.base = base;
        this.freqs = computeFreqs();
    }

    private double[] computeFreqs() {
        double[] f = new double[dim / 2];
        for (int i = 0; i < dim / 2; i++) {
            // Standard: base^(-2i/dim)
            // φ-enhanced: multiply by φ for golden harmonic
            f[i] = Math.pow(base, -2.0 * i / dim);
        }
        return f;
    }

    /**
     * Apply rotary embedding to vector at given position
     * Rotates pairs of dimensions by position-dependent angle
     */
    public double[] apply(double[] x, int position) {
        if (x.length != dim) {
            throw new IllegalArgumentException("Input dim must match: " + dim);
        }

        double[] rotated = new double[dim];
        
        for (int i = 0; i < dim / 2; i++) {
            double theta = position * freqs[i];
            double cos_t = Math.cos(theta);
            double sin_t = Math.sin(theta);
            
            // Rotate pair (x[2i], x[2i+1])
            double x1 = x[2 * i];
            double x2 = x[2 * i + 1];
            
            rotated[2 * i] = x1 * cos_t - x2 * sin_t;
            rotated[2 * i + 1] = x1 * sin_t + x2 * cos_t;
        }
        
        return rotated;
    }

    /**
     * φ-enhanced rotation: uses golden angle for optimal distribution
     */
    public double[] applyPhiRotation(double[] x, int position) {
        if (x.length != dim) {
            throw new IllegalArgumentException("Input dim must match: " + dim);
        }

        double[] rotated = new double[dim];
        double goldenAngle = 2 * Math.PI / (PHI * PHI); // ~137.5°
        
        for (int i = 0; i < dim / 2; i++) {
            double theta = position * freqs[i] * goldenAngle;
            double cos_t = Math.cos(theta);
            double sin_t = Math.sin(theta);
            
            double x1 = x[2 * i];
            double x2 = x[2 * i + 1];
            
            rotated[2 * i] = x1 * cos_t - x2 * sin_t;
            rotated[2 * i + 1] = x1 * sin_t + x2 * cos_t;
        }
        
        return rotated;
    }

    /**
     * Compute attention with rotary embeddings
     * Q and K are rotated, then dot product computed
     */
    public double rotaryAttention(double[] q, double[] k, int posQ, int posK) {
        double[] qRot = apply(q, posQ);
        double[] kRot = apply(k, posK);
        
        // Dot product
        double score = 0;
        for (int i = 0; i < dim; i++) {
            score += qRot[i] * kRot[i];
        }
        
        // Scale by sqrt(dim)
        return score / Math.sqrt(dim);
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║         RoPE - ROTARY EMBEDDING TEST      ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        
        RoPE rope = new RoPE(8);
        double[] vec = {1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        
        System.out.println("║  Original: [1, 0, 1, 0, 1, 0, 1, 0]");
        
        double[] pos0 = rope.apply(vec, 0);
        double[] pos1 = rope.apply(vec, 1);
        double[] pos5 = rope.apply(vec, 5);
        
        System.out.printf("║  Pos 0: [%.3f, %.3f, %.3f, %.3f, ...]%n", 
            pos0[0], pos0[1], pos0[2], pos0[3]);
        System.out.printf("║  Pos 1: [%.3f, %.3f, %.3f, %.3f, ...]%n", 
            pos1[0], pos1[1], pos1[2], pos1[3]);
        System.out.printf("║  Pos 5: [%.3f, %.3f, %.3f, %.3f, ...]%n", 
            pos5[0], pos5[1], pos5[2], pos5[3]);
        
        // Attention scores
        double selfAttn = rope.rotaryAttention(vec, vec, 0, 0);
        double nearAttn = rope.rotaryAttention(vec, vec, 0, 1);
        double farAttn = rope.rotaryAttention(vec, vec, 0, 10);
        
        System.out.println("║");
        System.out.printf("║  Self attention (0,0):  %.4f%n", selfAttn);
        System.out.printf("║  Near attention (0,1):  %.4f%n", nearAttn);
        System.out.printf("║  Far attention (0,10):  %.4f%n", farAttn);
        System.out.println("║  φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
