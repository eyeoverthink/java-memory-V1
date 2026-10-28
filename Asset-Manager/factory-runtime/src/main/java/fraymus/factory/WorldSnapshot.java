package fraymus.factory;

import java.util.List;

public record WorldSnapshot(long revision, List<EntityRecord> entities) {
    public WorldSnapshot {
        if (revision < 0) throw new IllegalArgumentException("Negative revision");
        entities = List.copyOf(entities);
    }
}
