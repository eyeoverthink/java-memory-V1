package fraymus.factory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ComponentRecord(UUID id, String type, int version, Map<String, String> values) {
    public ComponentRecord {
        Objects.requireNonNull(id, "component id");
        Objects.requireNonNull(type, "component type");
        values = Map.copyOf(values);
    }
}
