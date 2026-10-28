package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tiny TOML reader for the offline toolchain manifest.
 */
public final class OfflineToolchainManifest {
    private final Path path;
    private final Map<String, Map<String, String>> sections;

    private OfflineToolchainManifest(Path path, Map<String, Map<String, String>> sections) {
        this.path = path;
        this.sections = sections;
    }

    public static OfflineToolchainManifest load(Path workspaceRoot) throws IOException {
        Path manifestPath = workspaceRoot.resolve("offline-toolchain.toml");
        if (!Files.exists(manifestPath)) {
            return null;
        }

        Map<String, Map<String, String>> sections = new LinkedHashMap<>();
        String currentSection = "";
        sections.put(currentSection, new LinkedHashMap<>());

        List<String> lines = Files.readAllLines(manifestPath, StandardCharsets.UTF_8);
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                currentSection = line.substring(1, line.length() - 1).trim().toLowerCase(Locale.ROOT);
                sections.putIfAbsent(currentSection, new LinkedHashMap<>());
                continue;
            }
            int separator = line.indexOf('=');
            if (separator > 0) {
                String key = line.substring(0, separator).trim().toLowerCase(Locale.ROOT);
                String value = line.substring(separator + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                sections.computeIfAbsent(currentSection, ignored -> new LinkedHashMap<>()).put(key, value);
            }
        }

        return new OfflineToolchainManifest(manifestPath, sections);
    }

    public Path getPath() {
        return path;
    }

    public String getValue(String section, String key) {
        Map<String, String> values = sections.get(section == null ? "" : section.toLowerCase(Locale.ROOT));
        if (values == null) {
            return null;
        }
        return values.get(key.toLowerCase(Locale.ROOT));
    }

    public boolean hasSection(String section) {
        return sections.containsKey(section == null ? "" : section.toLowerCase(Locale.ROOT));
    }

    /** Named local inputs needed by validators when executing in a shadow workspace. */
    public Map<String, String> getShadowInputs() {
        return Map.copyOf(sections.getOrDefault("shadow", Map.of()));
    }
}
