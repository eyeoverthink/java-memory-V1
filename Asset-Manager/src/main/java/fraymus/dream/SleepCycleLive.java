package fraymus.dream;

import fraymus.hyper.BrainPulse;
import fraymus.hyper.CortexMapper;
import fraymus.hyper.HyperTesseract;
import java.io.File;
import java.util.Scanner;

public class SleepCycleLive {
    public static void main(String[] args) {
        runSession(new Scanner(System.in));
    }

    public static void runSession(Scanner scanner) {
        HyperTesseract brain = new HyperTesseract();
        CortexMapper mapper = new CortexMapper(brain);
        BrainPulse pulse = new BrainPulse(brain);
        DreamState dream = new DreamState(brain);
        boolean mapped = false;

        printHelp();

        while (true) {
            System.out.print("sleepcycle> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("stop")) {
                break;
            }
            if (line.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }
            if (line.equalsIgnoreCase("status")) {
                printStatus(brain, mapper, pulse, dream, mapped);
                continue;
            }
            if (line.startsWith("map")) {
                mapped = mapReality(mapper, line);
                continue;
            }
            if (line.equalsIgnoreCase("wake")) {
                dream.wakeUp();
                continue;
            }
            if (line.equalsIgnoreCase("sleep")) {
                dream.induceSleep();
                continue;
            }
            if (line.equalsIgnoreCase("dreams")) {
                printDreams(dream);
                continue;
            }
            if (line.equalsIgnoreCase("implement")) {
                dream.implementDreams();
                continue;
            }
            if (line.equalsIgnoreCase("heartbeat start")) {
                pulse.startHeartbeat();
                continue;
            }
            if (line.equalsIgnoreCase("heartbeat stop")) {
                pulse.stopHeartbeat();
                continue;
            }

            System.out.println("   Unknown command. Type 'help' for options.");
        }

        if (pulse.isRunning()) {
            pulse.stopHeartbeat();
        }
        if (dream.isDreaming()) {
            dream.wakeUp();
        }
    }

    private static boolean mapReality(CortexMapper mapper, String line) {
        String[] parts = line.split("\\s+", 2);
        String rootPath = parts.length > 1 ? parts[1].trim() : System.getProperty("user.dir");
        if (rootPath.isEmpty()) {
            rootPath = System.getProperty("user.dir");
        }
        mapper.uploadReality(new File(rootPath));
        return true;
    }

    private static void printStatus(HyperTesseract brain, CortexMapper mapper, BrainPulse pulse, DreamState dream, boolean mapped) {
        System.out.println(brain.getStats());
        System.out.println();
        System.out.println(pulse.getStats());
        System.out.println();
        System.out.println(dream.getStats());
        if (mapped) {
            System.out.println();
            System.out.println(mapper.getStats());
        }
    }

    private static void printDreams(DreamState dream) {
        var journal = dream.getDreamJournal();
        if (journal.isEmpty()) {
            System.out.println("   (No dream entries yet.)");
            return;
        }
        for (int i = 0; i < journal.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + journal.get(i));
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("SLEEP CYCLE LIVE COMMANDS:");
        System.out.println("  status                 - Show brain/heartbeat/dream stats");
        System.out.println("  map [path]             - Map reality into the tesseract");
        System.out.println("  sleep                  - Enter REM dream state");
        System.out.println("  wake                   - Exit dream state");
        System.out.println("  dreams                 - List dream journal entries");
        System.out.println("  implement              - Send dreams to OpenClaw");
        System.out.println("  heartbeat start|stop   - Control autonomous heartbeat");
        System.out.println("  stop|exit               - Return to Fraynix");
        System.out.println();
    }
}
