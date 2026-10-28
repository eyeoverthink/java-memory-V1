package fraymus.factory.kernel;

import java.nio.file.*;

public final class FactoryKernelIntegrationTest {
    public static void main(String[] args) throws Exception {
        Path workspace = Files.createTempDirectory("factory-kernel-integration-");
        Path scene = workspace.resolve("scene.txt"); Files.writeString(scene, "entity=seed; components=transform,goal");
        FactoryKernelService.Receipt first;
        try (var service = new FactoryKernelService(workspace)) {
            first = service.inspect(scene);
            var monitor = service.monitor("factory scene");
            if (Integer.parseInt(monitor.status().get("documents")) == 0 || monitor.hits().isEmpty() || monitor.receipts().isEmpty())
                throw new AssertionError("Kernel monitor did not expose real persisted state / retrieval / receipt");
            if (!first.passed() || !Files.isRegularFile(Path.of(first.tracePath()))) throw new AssertionError("No verified real trace");
            String trace = Files.readString(Path.of(first.tracePath()));
            if (!trace.contains("command.factory_artifact")) throw new AssertionError("Real artifact verifier was not run");
        }
        try (var restarted = new FactoryKernelService(workspace)) {
            var second = restarted.inspect(scene);
            if (!second.passed() || second.taskId().equals(first.taskId())) throw new AssertionError("Restart task failed");
            if (!Files.isRegularFile(workspace.resolve("task-results").resolve(first.taskId() + ".txt"))) throw new AssertionError("Lost prior receipt");
        }
        System.out.println("FactoryKernelIntegrationTest passed: real gateway, subprocess validator, trace persistence and restart");
    }
}
