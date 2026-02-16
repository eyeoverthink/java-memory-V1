package fraymus.hyper;

import java.io.Serializable;
import java.util.*;

/**
 * HyperFormer:
 * - Tokenization = word -> hypervector (deterministic via fromSeed)
 * - "Training" = store associative memory from ContextHologram -> NextToken
 * - "Inference" = build ContextHologram, find best match by resonance (XOR similarity)
 *
 * This fixes the big flaw in earlier sketches:
 * random embeddings alone can't magically predict; you must store associations.
 */
public final class HyperFormer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, HyperVector> embeddings = new HashMap<>();
    private final List<Association> memory = new ArrayList<>();
    private final HoloAttention attention = new HoloAttention();

    private final int contextWindow;
    private final double resonanceThreshold;

    public HyperFormer() {
        this(4, 0.0);
    }

    public HyperFormer(int contextWindow, double resonanceThreshold) {
        this.contextWindow = Math.max(1, contextWindow);
        this.resonanceThreshold = resonanceThreshold;

        // bootstrap deterministic anchors (stable)
        learn("system");
        learn("optimize");
        learn("growth");
    }

    /** Backward-compat: accept long seed (ignored, kept for API compat). */
    public HyperFormer(long seed) {
        this(4, 0.0);
    }

    public int vocabSize() { return embeddings.size(); }
    public int memorySize() { return memory.size(); }

    public void learn(String word) {
        embeddings.computeIfAbsent(word, HyperVector::fromSeed);
    }

    /** Get or create embedding for a token. */
    public HyperVector embed(String token) {
        learn(token);
        return embeddings.get(token);
    }

    /** One-shot learning: store context->next associations for a sentence. */
    public void learnSentence(String[] words) {
        if (words == null || words.length < 2) return;

        for (String w : words) learn(w);

        for (int i = 1; i < words.length; i++) {
            String nextWord = words[i];

            int start = Math.max(0, i - contextWindow);
            String[] ctx = Arrays.copyOfRange(words, start, i);

            HyperVector ctxVec = contextVector(ctx);
            HyperVector nextVec = embeddings.get(nextWord);

            memory.add(new Association(ctxVec, nextWord, nextVec));
        }
    }

    /** Predict next token by best resonance match against stored contexts. */
    public String predictNext(String[] contextWords) {
        if (contextWords == null || contextWords.length == 0) return "???";

        for (String w : contextWords) learn(w);

        HyperVector queryCtx = contextVector(contextWords);

        String best = "???";
        double bestRes = -1.0;

        for (Association a : memory) {
            double r = a.context.resonance(queryCtx);
            if (r > bestRes) {
                bestRes = r;
                best = a.nextWord;
            }
        }
        return best;
    }

    /** Backward-compat alias */
    public String predict(String[] context) { return predictNext(context); }

    /** Find the closest concept in the embedding space by resonance. */
    public String findClosestConcept(HyperVector target) {
        String best = "???";
        double maxRes = -1.0;
        for (Map.Entry<String, HyperVector> e : embeddings.entrySet()) {
            double r = e.getValue().resonance(target);
            if (r > maxRes) { maxRes = r; best = e.getKey(); }
        }
        return best;
    }

    /** Export the brain state as a safe DTO for serialization. */
    public FraymusState exportState() {
        List<FraymusState.AssociationDTO> dtos = new ArrayList<>();
        for (Association a : memory) {
            dtos.add(new FraymusState.AssociationDTO(a.context, a.nextWord, a.nextVector));
        }
        return new FraymusState(
                new HashMap<>(embeddings),
                dtos,
                contextWindow,
                resonanceThreshold
        );
    }

    /** Restore a brain from a frozen state DTO. */
    public static HyperFormer fromState(FraymusState state) {
        HyperFormer h = new HyperFormer(state.contextWindow(), state.resonanceThreshold());
        h.embeddings.clear();
        h.memory.clear();
        h.embeddings.putAll(state.embeddings());
        for (FraymusState.AssociationDTO dto : state.associations()) {
            h.memory.add(new Association(dto.context(), dto.nextWord(), dto.nextVector()));
        }
        return h;
    }

    /**
     * Build the holographic context vector from context words.
     * Uses HoloAttention context builder (permute+bundle+NCA).
     */
    private HyperVector contextVector(String[] ctxWords) {
        List<HyperVector> seq = new ArrayList<>(ctxWords.length);
        for (String w : ctxWords) seq.add(embeddings.get(w));

        HyperVector query = seq.get(seq.size() - 1);
        return attention.buildContext(seq, query, resonanceThreshold);
    }

    private static final class Association implements Serializable {
        private static final long serialVersionUID = 1L;

        final HyperVector context;
        final String nextWord;
        final HyperVector nextVector;

        Association(HyperVector context, String nextWord, HyperVector nextVector) {
            this.context = context;
            this.nextWord = nextWord;
            this.nextVector = nextVector;
        }
    }
}
