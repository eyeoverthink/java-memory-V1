package nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Evolved_modeltextgo {

    public static class Embedding {
        // ...
        public Embedding build() {
            return this;
        }
    }

    public static class RMSNorm {
        // ...
        public RMSNorm build() {
            return this;
        }
    }

    public static class Linear {
        // ...
        public Linear build() {
            return this;
        }
    }

    public static class TextOptions {
        public int hiddenSize;
        public int numHeads;
        public int numKVHeads;
        public int ropeDim;
        public int originalContextLength;
        public float eps;
        public float ropeBase;
        public float ropeScale;
        public List\u003cInteger\u003e mropeSections;

        public TextOptions(int hiddenSize, int numHeads, int numKVHeads, int ropeDim, int originalContextLength, float eps, float ropeBase, float ropeScale, List\u003cInteger\u003e mropeSections) {
            this.hiddenSize = hiddenSize;
            this.numHeads = numHeads;
            this.numKVHeads = numKVHeads;
            this.ropeDim = ropeDim;
            this.originalContextLength = originalContextLength;
            this.eps = eps;
            this.ropeBase = ropeBase;
            this.ropeScale = ropeScale;
            this.mropeSections = new ArrayList\u003c\u003e(mropeSections);
        }

        public TextOptions(TextOptions o) {
            this.hiddenSize = o.hiddenSize;
            this.numHeads = o.numHeads;
            this.numKVHeads = o.numKVHeads;
            this.ropeDim = o.ropeDim;
            this.originalContextLength = o.originalContextLength;
            this.eps = o.eps;
            this.ropeBase = o.ropeBase;
            this.ropeScale = o.ropeScale;
            this.mropeSections = new ArrayList\u003c\u003e(o.mropeSections);
        }
    }

    public static class TextModel {
        public Embedding TokenEmbedding;
        public List\u003cLayer\u003e Layers;
        public RMSNorm OutputNorm;
        public Linear Output;

        public List\u003cInteger\u003e positionCache;

        public TextModel(int blockCount, int embeddingLength, int attentionHeadCount, int attentionHeadCountKV, int ropeDimensionCount, int contextLength, float epsilon, float freqBase, float scalingFactor, List\u003cInteger\u003e mropeSections) {
            Layers = new ArrayList\u003c\u003e();
            for (int i = 0; i \u003c blockCount; i++) {
                Layers.add(new Layer());
            }
            TokenEmbedding = new Embedding().build();
            OutputNorm = new RMSNorm().build();
            Output = new Linear().build();

            TextOptions textOptions = new TextOptions(
                    embeddingLength,
                    attentionHeadCount,
                    attentionHeadCountKV,
                    ropeDimensionCount,
                    contextLength,
                    epsilon,
                    freqBase,
                    scalingFactor,
                    mropeSections
            );
        }

    }

    public static class SelfAttention {
        public Linear Query;
        public Linear Key;
        public Linear Value;
        public Linear Output;

        public SelfAttention(Linear query, Linear key, Linear value, Linear output) {
            Query = query;
            Key = key;
            Value = value;
            Output = output;
        }

    }

    public static class Layer {
        public RMSNorm AttentionNorm;
        public SelfAttention SelfAttention;
        public RMSNorm FFPNORM;
        public MLP FFMPL;

        public Layer() {}

    }

    public static class MLP {
        public Linear Up;
        public Linear Down;
        public Linear Gate;

        public MLP(Linear up, Linear down, Linear gate) {
            Up = up;
            Down = down;
            Gate = gate;
        }
    }
}