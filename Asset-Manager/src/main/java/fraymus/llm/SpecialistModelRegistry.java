package fraymus.llm;

import fraymus.core.FraymusJSON;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

/** Typed specialist catalogue kept apart from chat-completion routing. */
public final class SpecialistModelRegistry {
    @SuppressWarnings("unchecked") public List<Map<String, Object>> all() throws IOException {
        Path path = Path.of(System.getProperty("user.dir"), "data", "specialists", "catalog.json");
        Object parsed = FraymusJSON.parseStrict(Files.readString(path));
        if (!(parsed instanceof Map<?, ?> root) || !(root.get("models") instanceof List<?> entries))
            throw new IOException("Invalid specialist model catalogue");
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (Object raw : entries) {
            if (!(raw instanceof Map<?, ?> entry) || !(entry.get("id") instanceof String id) || !ids.add(id))
                throw new IOException("Invalid or duplicate specialist identity");
            result.add(Map.copyOf((Map<String, Object>) entry));
        }
        return List.copyOf(result);
    }

    public Map<String, Object> find(String id) throws IOException {
        return all().stream().filter(m -> id.equals(m.get("id"))).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown specialist: " + id));
    }
}
