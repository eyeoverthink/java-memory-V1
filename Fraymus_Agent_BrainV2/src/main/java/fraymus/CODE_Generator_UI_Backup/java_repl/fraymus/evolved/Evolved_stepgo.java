package repl;

import java.util.Arrays;

public class Evolved_stepgo {

    public static class StepCache {
        private final double[] layers;
        private final double[] layers2;
        private double constant;

        public StepCache(int numLayers) {
            this.layers = new double[numLayers];
            this.layers2 = new double[numLayers];
            this.constant = 0.0;
        }

        public boolean shouldRefresh(int step, int interval) {
            return step % interval == 0;
        }

        public double getLayer(int layer) {
            if (layer \u003c layers.length) {
                return layers[layer];
            }
            return Double.NaN;
        }

        public void setLayer(int layer, double arr) {
            if (layer \u003c layers.length) {
                if (layers[layer] != 0.0) {
                    Arrays.fill(layers, layer, layers.length, 0.0);
                }
                layers[layer] = arr;
            }
        }

        public double getLayer2(int layer) {
            if (layer \u003c layers2.length) {
                return layers2[layer];
            }
            return Double.NaN;
        }

        public void setLayer2(int layer, double arr) {
            if (layer \u003c layers2.length) {
                if (layers2[layer] != 0.0) {
                    Arrays.fill(layers2, layer, layers2.length, 0.0);
                }
                layers2[layer] = arr;
            }
        }

        public double getConstant() {
            return constant;
        }

        public void setConstant(double arr) {
            if (constant == 0.0 \u0026\u0026 arr != 0.0) {
                constant = arr;
            }
        }

        public double[] getArrays() {
            double[] result = new double[layers.length + layers2.length];
            System.arraycopy(layers, 0, result, 0, layers.length);
            System.arraycopy(layers2, 0, result, layers.length, layers2.length);
            return result;
        }

        public void free() {
            if (constant != 0.0) {
                constant = 0.0;
            }
            for (int i = 0; i \u003c layers.length; i++) {
                layers[i] = 0.0;
            }
            Arrays.fill(layers2, 0.0);
        }

        public int numLayers() {
            return layers.length;
        }
    }

    public static void main(String[] args) {}
}