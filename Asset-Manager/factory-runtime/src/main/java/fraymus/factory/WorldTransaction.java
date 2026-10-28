package fraymus.factory;

import java.util.*;

/** Typed staged edits. A failed batch never changes the live world. */
public final class WorldTransaction {
    public sealed interface Change permits Create, Delete, Rename, Put, Remove {}
    public record Create(EntityRecord entity) implements Change {}
    public record Delete(UUID entityId) implements Change {}
    public record Rename(UUID entityId, String name) implements Change {}
    public record Put(UUID entityId, ComponentRecord component) implements Change {}
    public record Remove(UUID entityId, UUID componentId) implements Change {}
    private final WorldSnapshot base;
    private final LinkedHashMap<UUID, EntityRecord> entities = new LinkedHashMap<>();

    public WorldTransaction(WorldSnapshot base) {
        this.base = Objects.requireNonNull(base);
        for (var entity : base.entities()) entities.put(entity.id(), entity);
    }
    public EntityRecord require(UUID id) {
        var entity = entities.get(id);
        if (entity == null) throw new IllegalArgumentException("Unknown entity: " + id);
        return entity;
    }
    public WorldTransaction add(Change change) {
        Objects.requireNonNull(change);
        if (change instanceof Create create) {
            if (entities.putIfAbsent(create.entity().id(), create.entity()) != null) throw new IllegalArgumentException("Entity already exists");
        } else if (change instanceof Delete delete) {
            require(delete.entityId()); entities.remove(delete.entityId());
        } else if (change instanceof Rename rename) {
            var entity = require(rename.entityId());
            if (rename.name() == null || rename.name().isBlank()) throw new IllegalArgumentException("Entity name is required");
            entities.put(entity.id(), new EntityRecord(entity.id(), rename.name(), entity.components()));
        } else if (change instanceof Put put) {
            var entity = require(put.entityId()); var components = new ArrayList<>(entity.components());
            int index = -1;
            for (int i = 0; i < components.size(); i++) if (components.get(i).id().equals(put.component().id())) index = i;
            if (index < 0) components.add(put.component()); else components.set(index, put.component());
            entities.put(entity.id(), new EntityRecord(entity.id(), entity.name(), components));
        } else if (change instanceof Remove remove) {
            var entity = require(remove.entityId()); var components = new ArrayList<>(entity.components());
            if (!components.removeIf(c -> c.id().equals(remove.componentId()))) throw new IllegalArgumentException("Unknown component");
            entities.put(entity.id(), new EntityRecord(entity.id(), entity.name(), components));
        }
        return this;
    }
    public WorldSnapshot stage(SchemaRegistry schemas) {
        var candidate = new WorldSnapshot(Math.addExact(base.revision(), 1), new ArrayList<>(entities.values()));
        FactoryWorld.validate(candidate, schemas); return candidate;
    }
    public void commit(FactoryWorld world, SchemaRegistry schemas) {
        world.replace(base.revision(), stage(schemas).entities());
    }
}
