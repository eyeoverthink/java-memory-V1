package org.apache.commons.lang3;

import java.util.EnumSet;
import java.util.Random;

public class Evolved_llamacparamsh {
    public static final int LLAMA_MAX_SEQ = 256;
    public enum Llama_pooling_type {
        POOLING_TYPE
    };
    private static final EnumSet\u003cLlama_pooling_type\u003e POOLING_TYPE_VALUES = EnumSet.allOf(Llama_pooling_type.class);

    public class LlamaCParams {
        public long n_ctx; // context size used during inference
        public long n_ctx_seq; // context for a single sequence
        public int n_batch;
        public int n_ubatch;
        public int n_seq_max;
        public int n_threads; // number of threads to use for generation
        public int n_threads_batch; // number of threads to use for batch processing

        public float rope_freq_base;
        public float rope_freq_scale;

        public long n_ctx_orig_yarn;

        public enum LlamaCParamsFlags {
            embeddings,
            causal_attn,
            offload_kqv,
            flash_attn,
            no_perf,
            warmup,
            op_offload,
            kv_unified
        }

        public boolean embeddings;
        public boolean causal_attn;
        public boolean offload_kqv;
        public boolean flash_attn;
        public boolean no_perf;
        public boolean warmup;
        public boolean op_offload;
        public boolean kv_unified;

        private static final LlamaCParamsFlags[] LAMA_CPARAMS_FLAGS = POOLING_TYPE_VALUES.toArray(new LlamaCParamsFlags[POOLING_TYPE_VALUES.size()]);
    }
}