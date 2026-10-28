package fraymus.factory;

import java.util.*;

public final class WorldTransactionTest {
    public static void main(String[] args) {
        var schemas = new SchemaRegistry();
        schemas.register(new ComponentSchema("data", 1, Map.of("value", ComponentSchema.Kind.NUMBER), Set.of("value")));
        var world = new FactoryWorld(schemas);
        UUID id = UUID.randomUUID(), component = UUID.randomUUID();
        var entity = new EntityRecord(id, "seed", List.of(new ComponentRecord(component, "data", 1, Map.of("value", "1"))));
        var transaction = new WorldTransaction(world.snapshot()).add(new WorldTransaction.Create(entity));
        if (!world.snapshot().entities().isEmpty()) throw new AssertionError("Staging changed live state");
        transaction.commit(world, schemas);
        var before = world.snapshot();
        try {
            new WorldTransaction(before).add(new WorldTransaction.Rename(id, "changed"))
                .add(new WorldTransaction.Put(id, new ComponentRecord(component, "data", 1, Map.of("value", "NaN")))).commit(world, schemas);
            throw new AssertionError("Invalid batch accepted");
        } catch (IllegalArgumentException expected) { }
        if (!before.equals(world.snapshot())) throw new AssertionError("Invalid batch was partially committed");
        try { transaction.commit(world, schemas); throw new AssertionError("Stale transaction accepted twice"); }
        catch (ConcurrentModificationException expected) { }
        var index = new SpatialIndex2D(List.of(new SpatialIndex2D.Item(id, new SpatialIndex2D.Bounds(-1, -1, 1, 1))));
        if (index.nearest(2, 0, .9, 1).size() != 0 || index.nearest(2, 0, 1, 1).get(0).distance() != 1
                || index.overlap(new SpatialIndex2D.Bounds(1, 1, 2, 2)).size() != 1) throw new AssertionError("Spatial query wrong at boundaries");
        System.out.println("WorldTransactionTest passed: isolated staging, atomic rejection, revision guards and deterministic spatial queries");
    }
}
