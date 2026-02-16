package fraymus;

import java.util.*;

public class MorseCircuit {

    private static final Map<String, Character> MORSE_TO_CHAR = new HashMap<>();
    private static final Map<Character, String> CHAR_TO_MORSE = new HashMap<>();

    static {
        String[][] table = {
            {".-", "A"}, {"-...", "B"}, {"-.-.", "C"}, {"-..", "D"}, {".", "E"},
            {"..-.", "F"}, {"--.", "G"}, {"....", "H"}, {"..", "I"}, {".---", "J"},
            {"-.-", "K"}, {".-..", "L"}, {"--", "M"}, {"-.", "N"}, {"---", "O"},
            {".--.", "P"}, {"--.-", "Q"}, {".-.", "R"}, {"...", "S"}, {"-", "T"},
            {"..-", "U"}, {"...-", "V"}, {".--", "W"}, {"-..-", "X"}, {"-.--", "Y"},
            {"--..", "Z"}, {"-----", "0"}, {".----", "1"}, {"..---", "2"},
            {"...--", "3"}, {"....-", "4"}, {".....", "5"}, {"-....", "6"},
            {"--...", "7"}, {"---..", "8"}, {"----.", "9"}
        };
        for (String[] entry : table) {
            MORSE_TO_CHAR.put(entry[0], entry[1].charAt(0));
            CHAR_TO_MORSE.put(entry[1].charAt(0), entry[0]);
        }
    }

    private static final Map<String, StringBuilder> entityMorseBuffers = new HashMap<>();
    private static final Map<String, List<String>> entityWords = new HashMap<>();
    private static final Map<String, Integer> entityWordCounts = new HashMap<>();
    private static int totalCharactersDecoded = 0;
    private static int totalWordsFormed = 0;

    public static String outputsToMorse(int[] outputs) {
        if (outputs == null || outputs.length < 4) return "";
        StringBuilder morse = new StringBuilder();
        for (int i = 0; i < Math.min(8, outputs.length); i++) {
            if (outputs[i] == 1) {
                if (i < 4) {
                    morse.append(".");
                } else {
                    morse.append("-");
                }
            }
        }
        return morse.toString();
    }

    public static char morseToChar(String morse) {
        Character c = MORSE_TO_CHAR.get(morse);
        return c != null ? c : '?';
    }

    public static String charToMorse(char c) {
        String m = CHAR_TO_MORSE.get(Character.toUpperCase(c));
        return m != null ? m : "?";
    }

    public static char decodeBrainOutputs(int[] outputs) {
        String morse = outputsToMorse(outputs);
        if (morse.isEmpty()) return ' ';
        return morseToChar(morse);
    }

    public static void tickEntity(PhiNode node) {
        int[] outputs = node.brain.getLastOutputs();
        if (outputs == null || outputs.length == 0) return;

        int activeCount = 0;
        for (int o : outputs) activeCount += o;
        if (activeCount == 0) return;

        char decoded = decodeBrainOutputs(outputs);
        if (decoded == '?' || decoded == ' ') return;

        totalCharactersDecoded++;

        StringBuilder buffer = entityMorseBuffers.computeIfAbsent(node.name, k -> new StringBuilder());
        buffer.append(decoded);

        if (buffer.length() >= 5) {
            String word = buffer.toString();
            List<String> words = entityWords.computeIfAbsent(node.name, k -> new ArrayList<>());
            words.add(word);
            if (words.size() > 20) words.remove(0);
            buffer.setLength(0);
            totalWordsFormed++;
            entityWordCounts.merge(node.name, 1, Integer::sum);
        }
    }

    public static String getEntityBuffer(String name) {
        StringBuilder buf = entityMorseBuffers.get(name);
        return buf != null ? buf.toString() : "";
    }

    public static List<String> getEntityWords(String name) {
        List<String> words = entityWords.get(name);
        return words != null ? Collections.unmodifiableList(words) : Collections.emptyList();
    }

    public static String getLastWord(String name) {
        List<String> words = entityWords.get(name);
        if (words != null && !words.isEmpty()) {
            return words.get(words.size() - 1);
        }
        return "";
    }

    public static String encodeMessage(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toUpperCase().toCharArray()) {
            if (c == ' ') {
                sb.append("  ");
            } else {
                String morse = CHAR_TO_MORSE.get(c);
                if (morse != null) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(morse);
                }
            }
        }
        return sb.toString();
    }

    public static String decodeMessage(String morse) {
        StringBuilder sb = new StringBuilder();
        String[] words = morse.split("  ");
        for (int w = 0; w < words.length; w++) {
            if (w > 0) sb.append(" ");
            String[] letters = words[w].trim().split(" ");
            for (String letter : letters) {
                if (!letter.isEmpty()) {
                    Character c = MORSE_TO_CHAR.get(letter);
                    sb.append(c != null ? c : '?');
                }
            }
        }
        return sb.toString();
    }

    public static int getTotalCharactersDecoded() { return totalCharactersDecoded; }
    public static int getTotalWordsFormed() { return totalWordsFormed; }
    public static int getEntityWordCount(String name) { return entityWordCounts.getOrDefault(name, 0); }

    public static void removeEntity(String name) {
        entityMorseBuffers.remove(name);
        entityWords.remove(name);
        entityWordCounts.remove(name);
    }
}
