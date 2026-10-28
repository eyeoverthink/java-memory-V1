package fraymus.factory;

import java.nio.file.Files;
import java.util.*;

public final class FactoryRuntimeTest {
    public static void main(String[] args) throws Exception {
        SchemaRegistry schemas = new SchemaRegistry();
        schemas.register(new ComponentSchema("test.link", 1, Map.of("target", ComponentSchema.Kind.ENTITY_REFERENCE, "weight", ComponentSchema.Kind.NUMBER), Set.of("weight")));
        FactoryWorld world = new FactoryWorld(schemas);
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        var ca = new ComponentRecord(UUID.randomUUID(), "test.link", 1, Map.of("target", b.toString(), "weight", "2.5"));
        var cb = new ComponentRecord(UUID.randomUUID(), "test.link", 1, Map.of("weight", "0"));
        world.replace(0, List.of(new EntityRecord(a, "A", List.of(ca)), new EntityRecord(b, "B", List.of(cb))));
        var cloneIds = world.duplicate(Set.of(a, b));
        var clone = world.snapshot().entities().stream().filter(e -> e.id().equals(cloneIds.get(a))).findFirst().orElseThrow();
        check(clone.components().get(0).values().get("target").equals(cloneIds.get(b).toString()), "clone reference remap");
        check(!clone.components().get(0).id().equals(ca.id()), "fresh component identity");
        var good = world.snapshot();
        expectFailure(() -> world.replace(0, good.entities()), "revision conflict");
        expectFailure(() -> world.replace(good.revision(), List.of(good.entities().get(0), good.entities().get(0))), "duplicate identity");
        expectFailure(() -> schemas.require("unknown", 1), "unknown schema");
        expectFailure(() -> world.replace(good.revision(), List.of(good.entities().get(0))), "dangling reference");
        expectFailure(() -> schemas.require("test.link", 1).validate(new ComponentRecord(UUID.randomUUID(), "test.link", 1, Map.of("weight", "NaN"))), "finite values");
        check(world.snapshot().equals(good), "failed edits are atomic");
        var store = new WorldCheckpointStore(Files.createTempDirectory("factory-checkpoint-").resolve("world.fworld"));
        store.save(good, schemas);
        var loaded = store.load(schemas);
        check(!loaded.recoveredPrevious() && loaded.snapshot().equals(good), "exact roundtrip");
        world.replace(good.revision(), good.entities());
        store.save(world.snapshot(), schemas);
        byte[] corrupt = Files.readAllBytes(store.path()); corrupt[corrupt.length - 1] ^= 1;
        Files.write(store.path(), corrupt);
        loaded = store.load(schemas);
        check(loaded.recoveredPrevious() && loaded.snapshot().equals(good), "corruption recovery");
        System.out.println("FactoryRuntimeTest passed: clone/remap, identity, revision guards, schema, atomic edit, save/restore, corruption recovery");
    }
    private static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
    private static void expectFailure(Runnable action, String label) {
        try { action.run(); } catch (IllegalArgumentException | ConcurrentModificationException expected) { return; }
        throw new AssertionError(label);
    }
}
