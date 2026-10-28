package fraymus.hyper;

import java.util.Scanner;

public class TesseractLive {
    public static void main(String[] args) {
        runSession(new Scanner(System.in));
    }

    public static void runSession(Scanner scanner) {
        HyperTesseract brain = new HyperTesseract();
        printHelp();

        while (true) {
            System.out.print("tesseract> ");
            if (!scanner.hasNextLine()) {
                return;
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
                System.out.println(brain.getStats());
                continue;
            }
            if (line.startsWith("tick")) {
                handleTick(brain, line);
                continue;
            }
            if (line.startsWith("inject ")) {
                handleInject(brain, line, false);
                continue;
            }
            if (line.startsWith("cross ")) {
                handleInject(brain, line, true);
                continue;
            }
            if (line.startsWith("node ")) {
                handleNode(brain, line);
                continue;
            }

            System.out.println("   Unknown command. Type 'help' for options.");
        }
    }

    private static void handleTick(HyperTesseract brain, String line) {
        String[] parts = line.split("\\s+");
        int cycles = 1;
        if (parts.length > 1) {
            Integer parsed = parseIndex(parts[1], "cycles");
            if (parsed == null) {
                System.out.println("Usage: tick [n]");
                return;
            }
            cycles = Math.max(1, parsed);
        }
        for (int i = 0; i < cycles; i++) {
            brain.tick();
        }
        System.out.println("   Ticked " + cycles + " cycles.");
    }

    private static void handleInject(HyperTesseract brain, String line, boolean cross) {
        String[] parts = line.split("\\s+", 6);
        if (parts.length < 6) {
            System.out.println("Usage: " + (cross ? "cross" : "inject") + " <w> <x> <y> <z> <thought>");
            return;
        }

        Integer w = parseIndex(parts[1], "w");
        Integer x = parseIndex(parts[2], "x");
        Integer y = parseIndex(parts[3], "y");
        Integer z = parseIndex(parts[4], "z");
        if (w == null || x == null || y == null || z == null) {
            return;
        }

        String thought = parts[5];
        if (cross) {
            brain.injectCrossDimensional(w, x, y, z, thought);
        } else {
            brain.injectThought(w, x, y, z, thought);
        }
    }

    private static void handleNode(HyperTesseract brain, String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 5) {
            System.out.println("Usage: node <w> <x> <y> <z>");
            return;
        }

        Integer w = parseIndex(parts[1], "w");
        Integer x = parseIndex(parts[2], "x");
        Integer y = parseIndex(parts[3], "y");
        Integer z = parseIndex(parts[4], "z");
        if (w == null || x == null || y == null || z == null) {
            return;
        }

        HyperTesseract.Node node = brain.getNode(w, x, y, z);
        if (node == null) {
            System.out.println("   [!] Node out of range.");
            return;
        }
        System.out.println(node.getState());
    }

    private static Integer parseIndex(String token, String label) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + token);
            return null;
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("TESSERACT LIVE COMMANDS:");
        System.out.println("  status                 - Show tesseract statistics");
        System.out.println("  tick [n]               - Advance n cycles (default 1)");
        System.out.println("  inject w x y z <text>   - Inject thought into a node");
        System.out.println("  cross w x y z <text>    - Inject and propagate across dimensions");
        System.out.println("  node w x y z            - Inspect a node state");
        System.out.println("  stop|exit               - Return to Fraynix");
        System.out.println();
    }
}
