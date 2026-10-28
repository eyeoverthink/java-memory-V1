package fraymus.genomics;

import fraymus.brain.unified.EntericNervousSystem;
import java.util.concurrent.ScheduledExecutorService;

/** A started diagnostic monitor must not hold a failed or completed JVM open. */
public final class EntericLifecycleTest {
    public static void main(String[] args) throws Exception {
        EntericNervousSystem monitor = new EntericNervousSystem();
        monitor.startMonitoring(20);
        if (args.length > 0 && args[0].equals("leave")) {
            System.out.println("PASS: main returns with a live daemon monitor; the JVM must exit");
            return;
        }
        monitor.shutdown();
        var field = EntericNervousSystem.class.getDeclaredField("scheduler"); field.setAccessible(true);
        if (!((ScheduledExecutorService) field.get(monitor)).isShutdown()) throw new AssertionError("Monitor executor leaked");
        System.out.println("PASS: explicit monitor shutdown releases its scheduler");
    }
}
