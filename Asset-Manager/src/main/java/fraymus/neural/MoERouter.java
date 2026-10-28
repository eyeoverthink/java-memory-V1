package fraymus.neural;

import java.util.Arrays;
import java.util.Locale;

/**
 * MIXTURE OF EXPERTS ROUTER
 *
 * The original router was a simple softmax top-K selector. This version keeps
 * the public API intact while upgrading the internal routing plan to an MHC
 * (multi-dimensional hyper-connection) preset:
 *
 * - multi-plane replicated streams
 * - Sinkhorn / Birkhoff style doubly-stochastic routing
 * - widened residual fusion across planes
 * - bounded signal magnitude instead of blanket dampening
 * - capacity metadata for heavy-task routing diagnostics
 */
public class MoERouter {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 1.0 / PHI;

    private final int numExperts;
    private final int topK;
    private final int inputDim;
    private final double[][] gateWeights;
    private final Expert[] experts;
    private final HyperConfig hyperConfig;

    private volatile RoutingPlan lastPlan;

    public MoERouter(int inputDim, int numExperts, int topK) {
        this(inputDim, defaultExpertNames(numExperts), topK, HyperConfig.mhc27B());
    }

    public MoERouter(int inputDim, int numExperts, int topK, HyperConfig hyperConfig) {
        this(inputDim, defaultExpertNames(numExperts), topK, hyperConfig);
    }

    public MoERouter(int inputDim, String[] expertNames, int topK, HyperConfig hyperConfig) {
        this.inputDim = Math.max(1, inputDim);
        String[] normalizedNames = normalizeExpertNames(expertNames);
        this.numExperts = normalizedNames.length;
        this.topK = Math.max(1, Math.min(topK, this.numExperts));
        this.hyperConfig = hyperConfig == null ? HyperConfig.mhc27B() : hyperConfig;

        this.gateWeights = new double[this.inputDim][this.numExperts];
        double scale = 1.0 / Math.sqrt(this.inputDim);
        for (int i = 0; i < this.inputDim; i++) {
            for (int j = 0; j < this.numExperts; j++) {
                double aligned = (i % this.numExperts) == j ? 1.0 : 0.18 * Math.cos((i + 1) * (j + 1) * PHI_INV);
                double spread = 0.05 * Math.sin((i + j + 2) * PHI_INV);
                gateWeights[i][j] = scale * (aligned + spread);
            }
        }

        this.experts = new Expert[this.numExperts];
        for (int i = 0; i < this.numExperts; i++) {
            experts[i] = new Expert(normalizedNames[i], this.inputDim);
        }
    }

    /**
     * Route input through the active MHC preset and return the merged output.
     */
    public double[] forward(double[] input) {
        return analyze(input).mergedOutput;
    }

    /**
     * Build a complete routing plan for the provided input.
     */
    public RoutingPlan analyze(double[] input) {
        double[] normalizedInput = normalizeInput(input);
        double[][] streamInputs = buildParallelStreams(normalizedInput);
        double[][] rawScores = new double[streamInputs.length][numExperts];
        double[] aggregateScores = new double[numExperts];

        for (int s = 0; s < streamInputs.length; s++) {
            rawScores[s] = computeGateScores(streamInputs[s]);
            for (int e = 0; e < numExperts; e++) {
                aggregateScores[e] += rawScores[s][e];
            }
        }
        for (int e = 0; e < numExperts; e++) {
            aggregateScores[e] /= streamInputs.length;
        }

        int routeWidth = Math.min(numExperts, Math.max(topK, streamInputs.length));
        int[] selectedExperts = findTopK(aggregateScores, routeWidth);
        double[] aggregateProbabilities = softmax(aggregateScores, hyperConfig.routerTemperature);

        double[][] affinity = new double[routeWidth][routeWidth];
        for (int row = 0; row < routeWidth; row++) {
            double[] streamProbabilities = softmax(rawScores[row % streamInputs.length], hyperConfig.routerTemperature);
            for (int col = 0; col < routeWidth; col++) {
                affinity[row][col] = Math.max(1e-9, streamProbabilities[selectedExperts[col]]);
            }
        }

        double[][] routingMatrix = sinkhornProject(affinity, hyperConfig.sinkhornIterations);
        double[][] planeOutputs = computePlaneOutputs(streamInputs, selectedExperts, routingMatrix);
        double[] mergedOutput = fuseResiduals(normalizedInput, planeOutputs);
        double[] streamMass = rowSums(routingMatrix);
        double[] expertMass = columnSums(routingMatrix);
        double peakMagnitude = peakAbs(mergedOutput);
        double capacityUtilization = ((double) routeWidth * routeWidth) /
                Math.max(1.0, (double) streamInputs.length * numExperts);

        String[] selectedNames = new String[routeWidth];
        double[] selectedProbabilities = new double[routeWidth];
        for (int i = 0; i < routeWidth; i++) {
            selectedNames[i] = experts[selectedExperts[i]].getName();
            selectedProbabilities[i] = aggregateProbabilities[selectedExperts[i]];
        }

        RoutingPlan plan = new RoutingPlan(
                hyperConfig,
                selectedNames,
                selectedProbabilities,
                streamInputs,
                planeOutputs,
                routingMatrix,
                streamMass,
                expertMass,
                mergedOutput,
                peakMagnitude,
                capacityUtilization
        );
        lastPlan = plan;
        return plan;
    }

    /**
     * Get the currently selected top-K experts as a user-friendly string array.
     */
    public String[] getSelectedExperts(double[] input) {
        RoutingPlan plan = analyze(input);
        int selected = Math.min(topK, plan.selectedExpertNames.length);
        String[] names = new String[selected];

        double sum = 0;
        for (int i = 0; i < selected; i++) {
            sum += plan.selectedExpertProbabilities[i];
        }
        if (sum <= 0) {
            sum = 1.0;
        }

        for (int i = 0; i < selected; i++) {
            double pct = 100.0 * plan.selectedExpertProbabilities[i] / sum;
            names[i] = plan.selectedExpertNames[i] + String.format(Locale.US, " (%.2f%%)", pct);
        }
        return names;
    }

    public String describePlan(double[] input) {
        return analyze(input).summary();
    }

    public String getHyperStatus() {
        RoutingPlan plan = lastPlan;
        if (plan == null) {
            return hyperConfig.summary();
        }
        return hyperConfig.summary() + " | " + plan.summary();
    }

    public RoutingPlan getLastPlan() {
        return lastPlan;
    }

    private double[] normalizeInput(double[] input) {
        double[] normalized = new double[inputDim];
        if (input == null) {
            return normalized;
        }
        int copy = Math.min(input.length, inputDim);
        System.arraycopy(input, 0, normalized, 0, copy);
        return normalized;
    }

    private double[][] buildParallelStreams(double[] input) {
        int streamCount = Math.max(1, Math.min(numExperts, hyperConfig.parallelStreams));
        double[][] streams = new double[streamCount][inputDim];

        for (int s = 0; s < streamCount; s++) {
            double phase = (s + 1) * PHI_INV;
            double replicateGain = hyperConfig.selfReplicate ? (0.08 + (0.04 * s)) : 0.0;
            for (int i = 0; i < inputDim; i++) {
                int sidewaysIndex = (i + s + 1) % inputDim;
                double sideways = hyperConfig.sidewaysMix * input[sidewaysIndex];
                double replicate = replicateGain * input[i] * Math.cos((i + 1) * phase);
                streams[s][i] = input[i] + sideways + replicate;
            }
            boundSignalMagnitude(streams[s], hyperConfig.boundedSignalMagnitude);
        }

        return streams;
    }

    private double[] computeGateScores(double[] input) {
        double[] scores = new double[numExperts];
        for (int expert = 0; expert < numExperts; expert++) {
            double sum = 0;
            for (int i = 0; i < inputDim; i++) {
                sum += input[i] * gateWeights[i][expert];
            }
            scores[expert] = sum;
        }
        return scores;
    }

    private double[] softmax(double[] values, double temperature) {
        double safeTemperature = Math.max(1e-6, temperature);
        double max = Arrays.stream(values).max().orElse(0);
        double[] exp = new double[values.length];
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            exp[i] = Math.exp((values[i] - max) / safeTemperature);
            sum += exp[i];
        }
        if (sum <= 0) {
            Arrays.fill(exp, 1.0 / values.length);
            return exp;
        }
        for (int i = 0; i < values.length; i++) {
            exp[i] /= sum;
        }
        return exp;
    }

    private int[] findTopK(double[] scores, int k) {
        int safeK = Math.max(1, Math.min(k, scores.length));
        int[] indices = new int[safeK];
        boolean[] used = new boolean[scores.length];

        for (int i = 0; i < safeK; i++) {
            int maxIdx = -1;
            double maxVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < scores.length; j++) {
                if (!used[j] && scores[j] > maxVal) {
                    maxVal = scores[j];
                    maxIdx = j;
                }
            }
            indices[i] = maxIdx;
            used[maxIdx] = true;
        }
        return indices;
    }

    private double[][] sinkhornProject(double[][] affinity, int iters) {
        int n = affinity.length;
        double[][] projected = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(affinity[i], 0, projected[i], 0, n);
        }

        for (int iter = 0; iter < Math.max(1, iters); iter++) {
            for (int row = 0; row < n; row++) {
                double sum = 0;
                for (int col = 0; col < n; col++) {
                    sum += projected[row][col];
                }
                double inv = 1.0 / (sum + 1e-9);
                for (int col = 0; col < n; col++) {
                    projected[row][col] *= inv;
                }
            }

            for (int col = 0; col < n; col++) {
                double sum = 0;
                for (int row = 0; row < n; row++) {
                    sum += projected[row][col];
                }
                double inv = 1.0 / (sum + 1e-9);
                for (int row = 0; row < n; row++) {
                    projected[row][col] *= inv;
                }
            }
        }

        return projected;
    }

    private double[][] computePlaneOutputs(double[][] streamInputs, int[] selectedExperts, double[][] routingMatrix) {
        int planes = routingMatrix.length;
        double[][] outputs = new double[planes][inputDim];

        for (int plane = 0; plane < planes; plane++) {
            double[] stream = streamInputs[plane % streamInputs.length];
            for (int col = 0; col < selectedExperts.length; col++) {
                double weight = routingMatrix[plane][col];
                double[] expertOut = experts[selectedExperts[col]].process(stream);
                for (int i = 0; i < inputDim; i++) {
                    outputs[plane][i] += weight * expertOut[i];
                }
            }
            boundSignalMagnitude(outputs[plane], hyperConfig.boundedSignalMagnitude);
        }

        return outputs;
    }

    private double[] fuseResiduals(double[] baseInput, double[][] planeOutputs) {
        double[] merged = Arrays.copyOf(baseInput, inputDim);
        int planes = planeOutputs.length;

        for (int i = 0; i < inputDim; i++) {
            double mean = 0;
            double sideways = 0;
            for (int plane = 0; plane < planes; plane++) {
                mean += planeOutputs[plane][i];
                sideways += planeOutputs[(plane + 1) % planes][i];
            }
            mean /= planes;
            sideways /= planes;

            merged[i] = baseInput[i]
                    + (hyperConfig.widenedResidualFactor * mean)
                    + (hyperConfig.sidewaysMix * sideways);
        }

        boundSignalMagnitude(merged, hyperConfig.boundedSignalMagnitude);
        return merged;
    }

    private void boundSignalMagnitude(double[] vector, double bound) {
        if (bound <= 0) {
            return;
        }

        for (int i = 0; i < vector.length; i++) {
            if (vector[i] > bound) {
                vector[i] = bound;
            } else if (vector[i] < -bound) {
                vector[i] = -bound;
            }
        }

        double l2 = 0;
        for (double v : vector) {
            l2 += v * v;
        }
        l2 = Math.sqrt(l2);

        double maxL2 = bound * Math.sqrt(vector.length);
        if (l2 > maxL2 && l2 > 1e-9) {
            double scale = maxL2 / l2;
            for (int i = 0; i < vector.length; i++) {
                vector[i] *= scale;
            }
        }
    }

    private double[] rowSums(double[][] matrix) {
        double[] sums = new double[matrix.length];
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                sums[row] += matrix[row][col];
            }
        }
        return sums;
    }

    private double[] columnSums(double[][] matrix) {
        double[] sums = new double[matrix[0].length];
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                sums[col] += matrix[row][col];
            }
        }
        return sums;
    }

    private double peakAbs(double[] vector) {
        double max = 0;
        for (double v : vector) {
            max = Math.max(max, Math.abs(v));
        }
        return max;
    }

    private static String[] defaultExpertNames(int numExperts) {
        String[] names = {"KAI", "TriMe", "NEXUS", "Dark", "Logic", "Creative", "Memory", "Security"};
        String[] resolved = new String[Math.max(1, numExperts)];
        for (int i = 0; i < resolved.length; i++) {
            resolved[i] = i < names.length ? names[i] : "Expert" + i;
        }
        return resolved;
    }

    private static String[] normalizeExpertNames(String[] expertNames) {
        if (expertNames == null || expertNames.length == 0) {
            return defaultExpertNames(1);
        }
        String[] normalized = new String[expertNames.length];
        for (int i = 0; i < expertNames.length; i++) {
            String candidate = expertNames[i] == null ? "" : expertNames[i].trim();
            normalized[i] = candidate.isEmpty() ? "Expert" + i : candidate;
        }
        return normalized;
    }

    /**
     * Expert network - simple feed-forward block with identity-biased weights.
     */
    public static class Expert {
        private final String name;
        private final double[][] weights;
        private final int dim;

        public Expert(String name, int dim) {
            this.name = name;
            this.dim = dim;
            this.weights = new double[dim][dim];

            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    weights[i][j] = (i == j ? 1.0 : 0.0) + 0.05 * Math.cos((i + 1) * (j + 1) * PHI_INV);
                }
            }
        }

        public double[] process(double[] input) {
            double[] output = new double[dim];
            for (int j = 0; j < dim; j++) {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    sum += input[i] * weights[i][j];
                }
                output[j] = Math.max(0, sum);
            }
            return output;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Single preset for the heavier MHC-style routing path.
     */
    public static final class HyperConfig {
        public final int parallelStreams;
        public final int sinkhornIterations;
        public final double widenedResidualFactor;
        public final double sidewaysMix;
        public final double boundedSignalMagnitude;
        public final double routerTemperature;
        public final double targetParameterBudgetBillions;
        public final boolean selfReplicate;
        public final boolean dualPipe;
        public final boolean selectiveRecompute;
        public final String kernelDialect;

        private HyperConfig(
                int parallelStreams,
                int sinkhornIterations,
                double widenedResidualFactor,
                double sidewaysMix,
                double boundedSignalMagnitude,
                double routerTemperature,
                double targetParameterBudgetBillions,
                boolean selfReplicate,
                boolean dualPipe,
                boolean selectiveRecompute,
                String kernelDialect
        ) {
            this.parallelStreams = parallelStreams;
            this.sinkhornIterations = sinkhornIterations;
            this.widenedResidualFactor = widenedResidualFactor;
            this.sidewaysMix = sidewaysMix;
            this.boundedSignalMagnitude = boundedSignalMagnitude;
            this.routerTemperature = routerTemperature;
            this.targetParameterBudgetBillions = targetParameterBudgetBillions;
            this.selfReplicate = selfReplicate;
            this.dualPipe = dualPipe;
            this.selectiveRecompute = selectiveRecompute;
            this.kernelDialect = kernelDialect;
        }

        public static HyperConfig mhc27B() {
            return new HyperConfig(
                    4,
                    6,
                    1.75,
                    0.22,
                    1.0,
                    0.85,
                    27.0,
                    true,
                    true,
                    true,
                    "tailang"
            );
        }

        public String summary() {
            return String.format(
                    Locale.US,
                    "MHC-27B streams=%d sinkhorn=%d residual=%.2fx bound=%.2f dualPipe=%s recompute=%s kernel=%s",
                    parallelStreams,
                    sinkhornIterations,
                    widenedResidualFactor,
                    boundedSignalMagnitude,
                    dualPipe,
                    selectiveRecompute,
                    kernelDialect
            );
        }
    }

    /**
     * Diagnostics snapshot for one routed forward pass.
     */
    public static final class RoutingPlan {
        public final HyperConfig config;
        public final String[] selectedExpertNames;
        public final double[] selectedExpertProbabilities;
        public final double[][] streamInputs;
        public final double[][] planeOutputs;
        public final double[][] routingMatrix;
        public final double[] streamMass;
        public final double[] expertMass;
        public final double[] mergedOutput;
        public final double peakMagnitude;
        public final double capacityUtilization;

        private RoutingPlan(
                HyperConfig config,
                String[] selectedExpertNames,
                double[] selectedExpertProbabilities,
                double[][] streamInputs,
                double[][] planeOutputs,
                double[][] routingMatrix,
                double[] streamMass,
                double[] expertMass,
                double[] mergedOutput,
                double peakMagnitude,
                double capacityUtilization
        ) {
            this.config = config;
            this.selectedExpertNames = selectedExpertNames;
            this.selectedExpertProbabilities = selectedExpertProbabilities;
            this.streamInputs = streamInputs;
            this.planeOutputs = planeOutputs;
            this.routingMatrix = routingMatrix;
            this.streamMass = streamMass;
            this.expertMass = expertMass;
            this.mergedOutput = mergedOutput;
            this.peakMagnitude = peakMagnitude;
            this.capacityUtilization = capacityUtilization;
        }

        public boolean isDoublyStochastic(double tolerance) {
            for (double sum : streamMass) {
                if (Math.abs(sum - 1.0) > tolerance) {
                    return false;
                }
            }
            for (double sum : expertMass) {
                if (Math.abs(sum - 1.0) > tolerance) {
                    return false;
                }
            }
            return true;
        }

        public String summary() {
            String primary = selectedExpertNames.length == 0 ? "none" : selectedExpertNames[0];
            return String.format(
                    Locale.US,
                    "MHC route streams=%d experts=%d primary=%s peak=%.3f capacity=%.2f",
                    streamInputs.length,
                    selectedExpertNames.length,
                    primary,
                    peakMagnitude,
                    capacityUtilization
            );
        }
    }

    public static void main(String[] args) {
        System.out.println("+-------------------------------------------+");
        System.out.println("|      MHC MIXTURE OF EXPERTS ROUTER        |");
        System.out.println("+-------------------------------------------+");

        MoERouter router = new MoERouter(8, 8, 2);
        double[] query = {0.8, 0.2, 0.5, 0.1, 0.4, 0.9, 0.3, 0.7};

        RoutingPlan plan = router.analyze(query);
        System.out.println(router.getHyperStatus());
        System.out.println(plan.summary());
        for (String selected : router.getSelectedExperts(query)) {
            System.out.println("  -> " + selected);
        }
    }
}
