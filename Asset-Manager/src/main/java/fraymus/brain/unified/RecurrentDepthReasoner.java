package fraymus.brain.unified;

import java.util.Arrays;

/**
 * RecurrentDepthReasoner implements a shared-weight latent reasoning block with identity-biased gating.
 * The core can be iteratively unrolled to arbitrary depth while maintaining stability through
 * spectral-radius monitoring and optional convergence thresholds.
 */
public final class RecurrentDepthReasoner {

    /**
     * Diagnostics captured for a single recurrent rollout.
     *
     * @param iterations        actual depth reached
     * @param residual          L2 residual between the last two states
     * @param spectralRadius    dominant eigenvalue magnitude of the transition operator
     * @param converged         whether the residual fell below the supplied tolerance
     * @param stabilityMargin   (1 - spectralRadius) – distance from the instability boundary
     */
    public record Diagnostics(int iterations, double residual, double spectralRadius, boolean converged, double stabilityMargin) {
    }

    /**
     * Result bundle containing the final latent state and its diagnostics.
     */
    public record Result(double[] state, Diagnostics diagnostics) {
    }

    private final int dimension;
    private final double[][] candidateWeights;
    private final double[][] inputWeights;
    private final double[][] gateWeights;
    private final double[][] gateInputWeights;
    private final double[] candidateBias;
    private final double[] gateBias;
    private final double spectralRadius;
    private final double stabilityMargin;

    private RecurrentDepthReasoner(
        int dimension,
        double[][] candidateWeights,
        double[][] inputWeights,
        double[][] gateWeights,
        double[][] gateInputWeights,
        double[] candidateBias,
        double[] gateBias
    ) {
        this.dimension = dimension;
        this.candidateWeights = deepCopy(candidateWeights);
        this.inputWeights = deepCopy(inputWeights);
        this.gateWeights = deepCopy(gateWeights);
        this.gateInputWeights = deepCopy(gateInputWeights);
        this.candidateBias = Arrays.copyOf(candidateBias, candidateBias.length);
        this.gateBias = Arrays.copyOf(gateBias, gateBias.length);
        this.spectralRadius = computeSpectralRadius(this.candidateWeights, 64);
        this.stabilityMargin = 1.0 - spectralRadius;
        if (spectralRadius >= 1.0) {
            throw new IllegalArgumentException("Recurrent block is unstable: spectral radius=" + spectralRadius);
        }
    }

    /**
     * Factory for a stable recurrent depth block using scaled identity kernels and bias priming.
     *
     * @param dimension   latent dimensionality
     * @param weightScale scale factor applied to the identity kernel (should be < 1)
     * @param gateBias    constant bias for the update gate (negative values favour carry-over)
     * @return configured stable reasoner
     */
    public static RecurrentDepthReasoner identityGated(int dimension, double weightScale, double gateBias) {
        if (dimension <= 0) throw new IllegalArgumentException("dimension must be positive");
        if (weightScale <= 0 || weightScale >= 1) {
            throw new IllegalArgumentException("weightScale must be in (0, 1) for stability");
        }

        double[][] candidateWeights = new double[dimension][dimension];
        double[][] gateWeights = new double[dimension][dimension];
        double[][] inputWeights = new double[dimension][dimension];
        double[][] gateInputWeights = new double[dimension][dimension];
        double[] candidateBias = new double[dimension];
        double[] gateBiasVector = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            candidateWeights[i][i] = weightScale;
            gateWeights[i][i] = weightScale * 0.5;
            inputWeights[i][i] = 1.0 - weightScale;
            gateInputWeights[i][i] = 1.0 - (weightScale * 0.5);
            candidateBias[i] = 0.0;
            gateBiasVector[i] = gateBias;
        }

        return new RecurrentDepthReasoner(
            dimension,
            candidateWeights,
            inputWeights,
            gateWeights,
            gateInputWeights,
            candidateBias,
            gateBiasVector
        );
    }

    /**
     * Executes the recurrent block with the supplied input vector until convergence or max depth is reached.
     *
     * @param input      latent input vector (must match dimension)
     * @param maxDepth   maximum recurrent depth
     * @param tolerance  residual tolerance for convergence
     * @return final latent state and diagnostics
     */
    public Result run(double[] input, int maxDepth, double tolerance) {
        validateInput(input);
        if (maxDepth <= 0) throw new IllegalArgumentException("maxDepth must be positive");
        if (tolerance <= 0) throw new IllegalArgumentException("tolerance must be positive");

        double[] state = new double[dimension];
        double[] previous = new double[dimension];
        double[] candidate = new double[dimension];
        double[] gate = new double[dimension];

        double[] inputTerm = matVec(inputWeights, input);
        double[] gateInputTerm = matVec(gateInputWeights, input);

        double residual = Double.MAX_VALUE;
        int iteration = 0;
        while (iteration < maxDepth && residual > tolerance) {
            System.arraycopy(state, 0, previous, 0, dimension);

            double[] recurrentCandidate = matVec(candidateWeights, previous);
            double[] recurrentGate = matVec(gateWeights, previous);

            for (int i = 0; i < dimension; i++) {
                double candidatePreActivation = recurrentCandidate[i] + inputTerm[i] + candidateBias[i];
                candidate[i] = Math.tanh(candidatePreActivation);

                double gatePreActivation = recurrentGate[i] + gateInputTerm[i] + gateBias[i];
                gate[i] = sigmoid(gatePreActivation);

                double carry = 1.0 - gate[i];
                state[i] = gate[i] * candidate[i] + carry * previous[i];
            }

            residual = l2Residual(state, previous);
            iteration++;
        }

        Diagnostics diagnostics = new Diagnostics(
            iteration,
            residual,
            spectralRadius,
            residual <= tolerance,
            stabilityMargin
        );
        return new Result(Arrays.copyOf(state, state.length), diagnostics);
    }

    public int dimension() {
        return dimension;
    }

    public double spectralRadius() {
        return spectralRadius;
    }

    public double stabilityMargin() {
        return stabilityMargin;
    }

    private void validateInput(double[] input) {
        if (input == null) throw new IllegalArgumentException("input cannot be null");
        if (input.length != dimension) {
            throw new IllegalArgumentException("Expected input length " + dimension + " but received " + input.length);
        }
    }

    private static double sigmoid(double x) {
        if (x >= 0) {
            double z = Math.exp(-x);
            return 1.0 / (1.0 + z);
        } else {
            double z = Math.exp(x);
            return z / (1.0 + z);
        }
    }

    private static double l2Residual(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private static double[][] deepCopy(double[][] source) {
        double[][] copy = new double[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = Arrays.copyOf(source[i], source[i].length);
        }
        return copy;
    }

    private static double[] matVec(double[][] matrix, double[] vector) {
        double[] result = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
            double[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                sum += row[j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    private static double computeSpectralRadius(double[][] matrix, int iterations) {
        int n = matrix.length;
        double[] vector = new double[n];
        Arrays.fill(vector, 1.0 / n);

        double[] temp = new double[n];
        double eigenvalue = 0.0;
        for (int iter = 0; iter < iterations; iter++) {
            Arrays.fill(temp, 0.0);
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += matrix[i][j] * vector[j];
                }
                temp[i] = sum;
            }
            double norm = l2Residual(temp, new double[n]);
            if (norm == 0.0) {
                return 0.0;
            }
            for (int i = 0; i < n; i++) {
                vector[i] = temp[i] / norm;
            }
            eigenvalue = norm;
        }
        return Math.abs(eigenvalue);
    }
}
