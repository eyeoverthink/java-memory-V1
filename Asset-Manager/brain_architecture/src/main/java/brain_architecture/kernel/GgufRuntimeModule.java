package brain_architecture.kernel;

import fraymus.llm.ModelLoader;
import java.nio.file.Path;

/**
 * Dedicated Java-side GGUF runtime module for lower-level model loading and inspection.
 */
public final class GgufRuntimeModule {
    private GgufRuntimeModule() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: GgufRuntimeModule <ggufPath> [prompt] [maxTokens]");
            System.exit(2);
            return;
        }

        Path ggufPath = Path.of(args[0]).toAbsolutePath().normalize();
        String prompt = args.length > 1 ? args[1] : "";
        int maxTokens = parseMaxTokens(args.length > 2 ? args[2] : null);

        ModelLoader loader = new ModelLoader();
        boolean loaded = loader.load(ggufPath);
        if (!loaded) {
            System.err.println("runtime.mode=gguf_primitives loaded=false path=" + ggufPath);
            System.exit(1);
            return;
        }

        GgufTokenPrimitiveEngine primitives = new GgufTokenPrimitiveEngine(loader);
        int[] promptTokens = primitives.tokenize(prompt);
        GgufTokenPrimitiveEngine.PrimitiveGeneration generation = primitives.generate(prompt, maxTokens);
        String summary = "runtime.mode=gguf_primitives"
            + " loaded=true"
            + " path=" + ggufPath
            + " architecture=" + safe(loader.getArchitecture(), "unknown")
            + " context=" + loader.getContextLength()
            + " embedding=" + loader.getEmbeddingLength()
            + " heads=" + loader.getHeadCount()
            + " vocab=" + loader.getVocabSize()
            + " promptChars=" + prompt.length()
            + " promptTokens=" + promptTokens.length
            + " generatedTokens=" + generation.generatedTokens().length
            + " candidates=" + generation.topCandidateSurfaces().size()
            + " maxTokens=" + maxTokens
            + " generation=primitive_tokens";
        System.out.println(summary);
        System.out.println("tokens.prompt=" + joinInts(promptTokens));
        System.out.println("tokens.generated=" + joinInts(generation.generatedTokens()));
        System.out.println("candidates.top=" + String.join("|", generation.topCandidateSurfaces()));
        System.out.println("generated.text=" + generation.generatedText().replace("\r", "\\r").replace("\n", "\\n"));
    }

    private static int parseMaxTokens(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String joinInts(int[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(values[index]);
        }
        return builder.toString();
    }
}
