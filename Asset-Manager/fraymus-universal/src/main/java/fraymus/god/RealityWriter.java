package fraymus.god;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * THE REALITY WRITER: Source Code Crystallizer.
 *
 * Takes the consensus truth from The Arena and hard-codes it
 * into a new Java class under fraymus/generated/.
 * Fraymus literally rewrites itself to be permanently smarter.
 */
public class RealityWriter {

    private static final Path GEN_ROOT = Path.of("src/main/java/fraymus/generated");
    private static int crystalCount = 0;

    /**
     * CRYSTALLIZE: Write a new organ containing harvested intelligence.
     *
     * @param knowledge  The consensus text from The Arena
     * @param className  The name for the generated class
     * @param source     Which models contributed
     * @param resonance  The consensus resonance score
     */
    public static Path crystallize(String knowledge, String className, String source, double resonance) {
        try {
            Files.createDirectories(GEN_ROOT);

            String sanitizedName = sanitizeClassName(className);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String code =
                "package fraymus.generated;\n\n" +
                "/**\n" +
                " * CRYSTALLIZED INTELLIGENCE\n" +
                " * Source: The Arena Consensus (" + source + ")\n" +
                " * Resonance: " + String.format("%.4f", resonance) + "\n" +
                " * Generated: " + timestamp + "\n" +
                " * Crystal #" + (++crystalCount) + "\n" +
                " */\n" +
                "public class " + sanitizedName + " {\n\n" +
                "    /** The distilled truth from the swarm. */\n" +
                "    public static String consult() {\n" +
                "        return\n" +
                formatMultilineString(knowledge) +
                "    }\n\n" +
                "    /** Raw resonance score at crystallization time. */\n" +
                "    public static double resonance() {\n" +
                "        return " + resonance + ";\n" +
                "    }\n" +
                "}\n";

            Path path = GEN_ROOT.resolve(sanitizedName + ".java");
            Files.writeString(path, code,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("💎 REALITY WRITTEN: " + path);
            System.out.println("   Crystal #" + crystalCount + " | Class: " + sanitizedName);

            return path;

        } catch (Exception e) {
            System.out.println("❌ CRYSTALLIZATION FAILED: " + e.getMessage());
            return null;
        }
    }

    /** Convenience overload. */
    public static Path crystallize(String knowledge, String className) {
        return crystallize(knowledge, className, "unknown", 0.0);
    }

    public static int getCrystalCount() { return crystalCount; }

    private static String sanitizeClassName(String name) {
        // Remove anything that isn't a valid Java identifier character
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0 && Character.isJavaIdentifierStart(c)) sb.append(c);
            else if (i > 0 && Character.isJavaIdentifierPart(c)) sb.append(c);
            else if (c == ' ' || c == '-') sb.append('_');
        }
        String result = sb.toString();
        if (result.isEmpty()) result = "Crystal_" + System.currentTimeMillis();
        // Ensure starts with uppercase
        return Character.toUpperCase(result.charAt(0)) + result.substring(1);
    }

    private static String formatMultilineString(String text) {
        // Split into ~100 char lines for readability
        String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        StringBuilder sb = new StringBuilder();

        int pos = 0;
        while (pos < escaped.length()) {
            int end = Math.min(pos + 100, escaped.length());
            // Try to break at a space
            if (end < escaped.length()) {
                int space = escaped.lastIndexOf(' ', end);
                if (space > pos + 40) end = space + 1;
            }
            String chunk = escaped.substring(pos, end);
            if (pos == 0) {
                sb.append("            \"").append(chunk).append("\"");
            } else {
                sb.append("\n          + \"").append(chunk).append("\"");
            }
            pos = end;
        }

        if (sb.length() == 0) sb.append("            \"\"");
        sb.append(";\n");
        return sb.toString();
    }
}
