package brain_architecture.kernel;

import fraymus.llm.ModelLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Java 17-safe token/runtime primitives layered on top of GGUF metadata and vocabulary.
 */
public final class GgufTokenPrimitiveEngine {
    private static final List<String> FALLBACK_GENERATION_SYMBOLS = List.of("e", "a", "i", "o", "t", "n", ".", " ");

    private final ModelLoader model;
    private final List<String> vocabulary;
    private final Map<String, Integer> tokenToId;
    private final List<String> orderedTokens;

    public GgufTokenPrimitiveEngine(ModelLoader model) {
        this.model = model;
        this.vocabulary = List.copyOf(model == null ? List.of() : model.getVocabulary());
        this.tokenToId = new LinkedHashMap<>();
        for (int index = 0; index < vocabulary.size(); index++) {
            tokenToId.putIfAbsent(vocabulary.get(index), index);
        }
        this.orderedTokens = vocabulary.stream()
            .filter(token -> token != null && !token.isEmpty())
            .sorted(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()))
            .toList();
    }

    public int[] tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }
        if (orderedTokens.isEmpty()) {
            return text.chars().toArray();
        }

        List<Integer> tokens = new ArrayList<>();
        int offset = 0;
        while (offset < text.length()) {
            String bestToken = null;
            for (String candidate : orderedTokens) {
                if (candidate.length() > text.length() - offset) {
                    continue;
                }
                if (text.startsWith(candidate, offset)) {
                    bestToken = candidate;
                    break;
                }
            }

            if (bestToken != null) {
                tokens.add(tokenToId.get(bestToken));
                offset += bestToken.length();
            } else {
                tokens.add((int) text.charAt(offset));
                offset += 1;
            }
        }
        return tokens.stream().mapToInt(Integer::intValue).toArray();
    }

    public String detokenize(int[] tokens) {
        if (tokens == null || tokens.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int token : tokens) {
            builder.append(tokenToSurface(token));
        }
        return builder.toString();
    }

    public List<TokenCandidate> nextCandidates(String prompt, int limit) {
        int boundedLimit = Math.max(1, limit);
        if (orderedTokens.isEmpty()) {
            return fallbackCandidates(prompt, boundedLimit);
        }

        String anchor = lastAnchor(prompt);
        String loweredPrompt = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        List<TokenCandidate> candidates = new ArrayList<>();
        for (int index = 0; index < vocabulary.size(); index++) {
            String token = vocabulary.get(index);
            if (token == null || token.isBlank() || isSpecialToken(token)) {
                continue;
            }
            double score = scoreToken(token, anchor, loweredPrompt, prompt);
            if (score <= 0.0) {
                continue;
            }
            candidates.add(new TokenCandidate(index, token, normalizeToken(token), score));
        }

        candidates.sort(Comparator
            .comparingDouble(TokenCandidate::score).reversed()
            .thenComparing(candidate -> candidate.surface().length())
            .thenComparing(TokenCandidate::tokenId));

        if (candidates.isEmpty()) {
            return fallbackCandidates(prompt, boundedLimit);
        }
        return List.copyOf(candidates.subList(0, Math.min(boundedLimit, candidates.size())));
    }

    public PrimitiveGeneration generate(String prompt, int maxTokens) {
        int[] promptTokens = tokenize(prompt == null ? "" : prompt);
        List<Integer> generatedTokenIds = new ArrayList<>();
        List<String> topSurfaces = new ArrayList<>();
        StringBuilder generatedText = new StringBuilder();
        String currentPrompt = prompt == null ? "" : prompt;

        for (int step = 0; step < Math.max(0, maxTokens); step++) {
            List<TokenCandidate> candidates = nextCandidates(currentPrompt, 5);
            if (candidates.isEmpty()) {
                break;
            }
            if (step == 0) {
                topSurfaces.addAll(candidates.stream().map(TokenCandidate::surface).limit(5).toList());
            }
            TokenCandidate selected = selectCandidate(candidates, generatedTokenIds);
            if (selected == null || selected.surface().isBlank()) {
                break;
            }
            generatedTokenIds.add(selected.tokenId());
            generatedText.append(selected.surface());
            currentPrompt = currentPrompt + selected.surface();
        }

        return new PrimitiveGeneration(
            prompt == null ? "" : prompt,
            promptTokens,
            generatedTokenIds.stream().mapToInt(Integer::intValue).toArray(),
            topSurfaces,
            generatedText.toString()
        );
    }

    private TokenCandidate selectCandidate(List<TokenCandidate> candidates, List<Integer> generatedTokenIds) {
        Integer lastTokenId = generatedTokenIds.isEmpty() ? null : generatedTokenIds.get(generatedTokenIds.size() - 1);
        for (TokenCandidate candidate : candidates) {
            if (candidate.surface().isBlank()) {
                continue;
            }
            if (lastTokenId == null || candidate.tokenId() != lastTokenId.intValue() || candidates.size() == 1) {
                return candidate;
            }
        }
        for (TokenCandidate candidate : candidates) {
            if (!candidate.surface().isBlank()) {
                return candidate;
            }
        }
        return null;
    }

    private List<TokenCandidate> fallbackCandidates(String prompt, int limit) {
        String anchor = lastAnchor(prompt);
        List<TokenCandidate> candidates = new ArrayList<>();
        int checksum = prompt == null ? 0 : prompt.chars().sum();
        for (int index = 0; index < FALLBACK_GENERATION_SYMBOLS.size() && candidates.size() < limit; index++) {
            String symbol = FALLBACK_GENERATION_SYMBOLS.get((index + checksum) % FALLBACK_GENERATION_SYMBOLS.size());
            double score = 1.0 + (anchor.isBlank() ? 0.0 : overlapScore(symbol, anchor));
            candidates.add(new TokenCandidate(symbol.isEmpty() ? 0 : symbol.charAt(0), symbol, symbol, score));
        }
        return List.copyOf(candidates);
    }

    private double scoreToken(String token, String anchor, String loweredPrompt, String prompt) {
        String normalized = normalizeToken(token);
        if (normalized.isBlank()) {
            return 0.0;
        }

        String loweredToken = normalized.toLowerCase(Locale.ROOT);
        double score = 0.5 + overlapScore(loweredToken, anchor);
        if (!anchor.isBlank()) {
            if (loweredToken.equals(anchor)) {
                score += 8.0;
            }
            if (loweredToken.startsWith(anchor)) {
                score += 6.0;
            }
            if (anchor.startsWith(loweredToken)) {
                score += 4.0;
            }
        }
        if (!loweredPrompt.isBlank() && loweredPrompt.endsWith(loweredToken)) {
            score -= 3.0;
        }
        if (prompt != null && !prompt.isEmpty() && Character.isWhitespace(prompt.charAt(prompt.length() - 1)) && Character.isLetterOrDigit(normalized.charAt(0))) {
            score += 2.0;
        }
        if (normalized.length() == 1 && !Character.isLetterOrDigit(normalized.charAt(0))) {
            score -= 0.5;
        }
        return score;
    }

    private double overlapScore(String token, String anchor) {
        if (token == null || token.isBlank() || anchor == null || anchor.isBlank()) {
            return 0.0;
        }
        int matched = 0;
        for (int index = 0; index < Math.min(token.length(), anchor.length()); index++) {
            if (token.charAt(index) == anchor.charAt(index)) {
                matched++;
            }
        }
        return matched;
    }

    private String lastAnchor(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return "";
        }
        StringBuilder anchor = new StringBuilder();
        for (int index = prompt.length() - 1; index >= 0; index--) {
            char current = prompt.charAt(index);
            if (Character.isLetterOrDigit(current) || current == '_' || current == '-') {
                anchor.append(Character.toLowerCase(current));
            } else if (anchor.length() > 0) {
                break;
            }
        }
        return anchor.reverse().toString();
    }

    private String tokenToSurface(int tokenId) {
        if (tokenId >= 0 && tokenId < vocabulary.size()) {
            return normalizeToken(vocabulary.get(tokenId));
        }
        return String.valueOf((char) tokenId);
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return "";
        }
        return token
            .replace("Ġ", " ")
            .replace("▁", " ")
            .replace("Ċ", "\n");
    }

    private boolean isSpecialToken(String token) {
        return token.startsWith("<") || token.startsWith("[") || token.contains("<|") || token.endsWith(">");
    }

    public record TokenCandidate(int tokenId, String token, String surface, double score) {
    }

    public record PrimitiveGeneration(
            String prompt,
            int[] promptTokens,
            int[] generatedTokens,
            List<String> topCandidateSurfaces,
            String generatedText) {
    }
}
