package fraymus.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Atomic in-process world replacement with immutable read snapshots and revision guards. */
public final class FactoryWorld {
    private final SchemaRegistry schemas;
    private WorldSnapshot state = new WorldSnapshot(0, java.util.List.of());

    public FactoryWorld(SchemaRegistry schemas) { this.schemas = schemas; }
    public synchronized WorldSnapshot snapshot() { return state; }

    public synchronized void replace(long expectedRevision, Collection<EntityRecord> entities) {
        if (state.revision() != expectedRevision) throw new ConcurrentModificationException("Stale world revision");
        WorldSnapshot candidate = new WorldSnapshot(Math.addExact(state.revision(), 1), new ArrayList<>(entities));
        validate(candidate, schemas);
        state = candidate;
    }

    public synchronized void restore(WorldSnapshot snapshot) {
        validate(snapshot, schemas);
        state = snapshot;
    }

    /** Clones a group, remapping internal entity references while preserving external references. */
    public synchronized Map<UUID, UUID> duplicate(Set<UUID> selected) {
        Map<UUID, UUID> ids = new LinkedHashMap<>();
        for (UUID id : selected) {
            if (state.entities().stream().noneMatch(e -> e.id().equals(id))) throw new IllegalArgumentException("Unknown entity: " + id);
            ids.put(id, UUID.randomUUID());
        }
        var entities = new ArrayList<>(state.entities());
        for (EntityRecord entity : state.entities()) {
            if (!ids.containsKey(entity.id())) continue;
            var components = new ArrayList<ComponentRecord>();
            for (ComponentRecord component : entity.components()) {
                var fields = new LinkedHashMap<>(component.values());
                var schema = schemas.require(component.type(), component.version());
                for (var field : schema.fields().entrySet()) {
                    if (field.getValue() == ComponentSchema.Kind.ENTITY_REFERENCE && fields.containsKey(field.getKey())) {
                        UUID original = UUID.fromString(fields.get(field.getKey()));
                        fields.put(field.getKey(), ids.getOrDefault(original, original).toString());
                    }
                }
                components.add(new ComponentRecord(UUID.randomUUID(), component.type(), component.version(), fields));
            }
            entities.add(new EntityRecord(ids.get(entity.id()), entity.name() + " copy", components));
        }
        replace(state.revision(), entities);
        return Map.copyOf(ids);
    }

    public static void validate(WorldSnapshot snapshot, SchemaRegistry schemas) {
        Set<UUID> entityIds = new HashSet<>();
        Set<UUID> componentIds = new HashSet<>();
        for (EntityRecord entity : snapshot.entities()) {
            if (!entityIds.add(entity.id())) throw new IllegalArgumentException("Duplicate entity id");
            for (ComponentRecord component : entity.components()) {
                if (!componentIds.add(component.id())) throw new IllegalArgumentException("Duplicate component id");
                schemas.require(component.type(), component.version()).validate(component);
            }
        }
        for (EntityRecord entity : snapshot.entities()) {
            for (ComponentRecord component : entity.components()) {
                for (var field : schemas.require(component.type(), component.version()).fields().entrySet()) {
                    if (field.getValue() == ComponentSchema.Kind.ENTITY_REFERENCE && component.values().containsKey(field.getKey())
                            && !entityIds.contains(UUID.fromString(component.values().get(field.getKey())))) {
                        throw new IllegalArgumentException("Dangling entity reference: " + field.getKey());
                    }
                }
            }
        }
    }
}
