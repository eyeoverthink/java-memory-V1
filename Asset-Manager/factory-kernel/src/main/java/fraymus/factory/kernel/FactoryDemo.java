package fraymus.factory.kernel;

import fraymus.factory.*;
import java.nio.file.*;
import java.util.*;

public final class FactoryDemo {
    public static void main(String[] args) throws Exception {
        Path project = Path.of(args.length == 0 ? ".factory-demo" : args[0]).toAbsolutePath().normalize();
        Files.createDirectories(project);
        SchemaRegistry schemas = new SchemaRegistry();
        schemas.register(new ComponentSchema("factory.note", 1, Map.of("text", ComponentSchema.Kind.TEXT), Set.of("text")));
        FactoryWorld world = new FactoryWorld(schemas);
        WorldCheckpointStore store = new WorldCheckpointStore(project.resolve("world.fworld"));
        if (Files.exists(store.path())) {
            var loaded = store.load(schemas); world.restore(loaded.snapshot());
            System.out.println("RESTORED revision=" + world.snapshot().revision() + " previous=" + loaded.recoveredPrevious());
        } else {
            UUID id = UUID.randomUUID();
            world.replace(0, List.of(new EntityRecord(id, "Factory seed", List.of(new ComponentRecord(UUID.randomUUID(), "factory.note", 1, Map.of("text", "Scene, components and Fraynix share a Java runtime."))))));
            world.duplicate(Set.of(id));
        }
        store.save(world.snapshot(), schemas);
        if (!world.snapshot().equals(store.load(schemas).snapshot())) throw new AssertionError("Save/restore mismatch");
        String summary = "Factory revision=" + world.snapshot().revision() + "\n" + world.snapshot().entities();
        Path artifact = project.resolve("scene-summary.txt"); Files.writeString(artifact, summary);
        System.out.println(summary);
        try (FactoryKernelService service = new FactoryKernelService(project)) {
            var receipt = service.inspect(artifact);
            System.out.println("KERNEL task=" + receipt.taskId() + " outcome=" + receipt.outcome() + " passed=" + receipt.passed());
            System.out.println("TRACE " + receipt.tracePath());
            if (!receipt.passed()) throw new AssertionError("Kernel verification failed");
        }
        System.out.println("FACTORY_DEMO_PASSED checkpoint=" + store.path());
    }
}
