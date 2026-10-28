package fraymus.factory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record EntityRecord(UUID id, String name, List<ComponentRecord> components) {
    public EntityRecord {
        Objects.requireNonNull(id, "entity id");
        Objects.requireNonNull(name, "entity name");
        components = List.copyOf(components);
    }
}
