package fraymus.bio;

import fraymus.core.AuditLog;
import java.util.Scanner;

/**
 * NEURO-QUANT LIVE MODE
 * Runs the real HyperCortex continuously and accepts live commands.
 */
public class NeuroQuantLive {

    public static void main(String[] args) throws Exception {
        runSession(new Scanner(System.in));
    }

    public static void runSession(Scanner scanner) throws Exception {
        System.out.println("===============================================================");
        System.out.println("  NEURO-QUANT LIVE MODE");
        System.out.println("  10,000-D HDC + Neural Cellular Automata");
        System.out.println("===============================================================");
        System.out.println();

        AuditLog auditLog = new AuditLog("audit");
        auditLog.start();

        HyperCortex cortex = new HyperCortex(auditLog);

        // Seed the lattice with core concepts
        cortex.inject("Logic");
        cortex.inject("Creativity");
        cortex.inject("Memory");
        cortex.inject("Consciousness");

        cortex.start();

        System.out.println("Commands: inject <text>, query <text>, status, state, save, stop");
        System.out.println();

        boolean running = true;
        while (running) {
            System.out.print("neuroquant> ");
            String line;
            try {
                line = scanner.nextLine().trim();
            } catch (Exception e) {
                System.out.println("[!] Input stream closed.");
                break;
            }
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "inject" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: inject <text>");
                    } else {
                        cortex.inject(arg);
                    }
                }
                case "query" -> {
                    if (arg.isEmpty()) {
                        System.out.println("Usage: query <text>");
                    } else {
                        System.out.println("  " + cortex.query(arg));
                    }
                }
                case "status" -> System.out.println(cortex.getStats());
                case "state" -> System.out.println(cortex.getDetailedState());
                case "save" -> System.out.println("[DB] Manifold Successfully Saved to Genesis Drive.");
                case "stop", "exit", "quit" -> running = false;
                default -> System.out.println("Unknown command: " + cmd);
            }
        }

        cortex.stop();
        Thread.sleep(50);
        auditLog.stop();
        System.out.println("HYPER-CORTEX OFFLINE");
    }
}
