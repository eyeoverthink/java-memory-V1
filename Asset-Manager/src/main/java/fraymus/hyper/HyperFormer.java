package fraymus.hyper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HyperFormer implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final CleanupMemory vocab = new CleanupMemory();
    private final MultiScaleMemory memory = new MultiScaleMemory();
    private final HoloAttention attention = new HoloAttention();
    private final NcaDenoiser denoiser = new NcaDenoiser();
    private final long seed;
    
    public HyperFormer() {
        this(0xCAFEBABE);
    }
    
    public HyperFormer(long seed) {
        this.seed = seed;
    }
    
    private HyperFormer(FraymusState state) {
        this.seed = state.globalSeed();
        this.vocab.restore(state.prototypes());
        this.memory.restore(state.memory());
    }

    public HyperVector embed(String token) {
        long s = hash(token) ^ seed;
        HyperVector v = HyperVector.seeded(s);
        vocab.memorize(token, v);
        return v;
    }

    public void learn(String[] words) {
        List<HyperVector> seq = new ArrayList<>();
        for (String w : words) seq.add(embed(w));
        memory.learnSequence(seq);
    }

    public String predict(String[] context) {
        List<HyperVector> seq = new ArrayList<>();
        for (String w : context) seq.add(embed(w));

        // 1. PREDICT via N-Grams
        HyperVector pred = memory.predict(seq);
        
        // 2. REFINE via Attention (Self-Reflection)
        if (memory.hasData()) {
            HyperVector ctxHolo = attention.attend(seq);
            pred = pred.bind(ctxHolo).permute(-1); // Inverse binding logic
        }

        // 3. DENOISE via Cellular Automata
        pred = denoiser.denoise(pred, 2);

        // 4. DECODE
        return vocab.decode(pred);
    }
    
    public int vocabSize() { return vocab.size(); }
    
    public int memoryWeight() { return memory.totalWeight(); }
    
    public void learnSentence(String[] words) {
        learn(words);
    }
    
    public String predictNext(String[] context) {
        return predict(context);
    }
    
    /**
     * Export immutable state snapshot for secure serialization.
     * Used by Cortical Stack Protocol.
     */
    public FraymusState exportState() {
        Map<String, HyperVector> prototypes = vocab.snapshot();
        MultiScaleMemory.State memState = memory.snapshot();
        return new FraymusState(seed, prototypes, memState);
    }
    
    /**
     * Reconstruct HyperFormer from immutable state snapshot.
     * Used by Cortical Stack Protocol.
     */
    public static HyperFormer fromState(FraymusState state) {
        return new HyperFormer(state);
    }

    private long hash(String s) {
        // FNV-1a Hash
        long h = 0xcbf29ce484222325L;
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            h ^= b;
            h *= 0x100000001b3L;
        }
        return h;
    }
}
