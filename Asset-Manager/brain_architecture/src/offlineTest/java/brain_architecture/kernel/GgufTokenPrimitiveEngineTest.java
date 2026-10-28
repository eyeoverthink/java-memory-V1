package brain_architecture.kernel;

import fraymus.llm.ModelLoader;
import java.util.List;

/**
 * Verifies Java GGUF token/runtime primitives work deterministically with a supplied vocabulary.
 */
public final class GgufTokenPrimitiveEngineTest {
    public static void main(String[] args) {
        FakeModelLoader loader = new FakeModelLoader();
        GgufTokenPrimitiveEngine engine = new GgufTokenPrimitiveEngine(loader);

        int[] promptTokens = engine.tokenize("hello world!");
        assertTrue(promptTokens.length >= 4, "Expected greedy vocabulary tokenization");
        String restored = engine.detokenize(promptTokens);
        assertTrue(restored.contains("hello"), "Expected detokenized prompt to preserve visible text");

        List<GgufTokenPrimitiveEngine.TokenCandidate> candidates = engine.nextCandidates("hello ", 3);
        assertTrue(!candidates.isEmpty(), "Expected token candidates");

        GgufTokenPrimitiveEngine.PrimitiveGeneration generation = engine.generate("hello ", 3);
        assertTrue(generation.generatedTokens().length > 0, "Expected primitive token generation");
        assertTrue(!generation.generatedText().isBlank(), "Expected non-blank generated text");
        assertTrue(!generation.topCandidateSurfaces().isEmpty(), "Expected top candidate surfaces");

        System.out.println("GgufTokenPrimitiveEngineTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static final class FakeModelLoader extends ModelLoader {
        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public int getContextLength() {
            return 128;
        }

        @Override
        public int getVocabSize() {
            return getVocabulary().size();
        }

        @Override
        public List<String> getVocabulary() {
            return List.of("hello", " ", "world", "!", "there", "friend", ".");
        }
    }
}
