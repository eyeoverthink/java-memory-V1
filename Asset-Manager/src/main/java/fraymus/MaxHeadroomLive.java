package fraymus;

import fraymus.senses.ReactiveVisualAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MaxHeadroomLive {
    public static void main(String[] args) {
        boolean allowVisual = hasFlag(args, "--visual");
        int askIndex = indexOfFlag(args, "--ask");
        if (askIndex >= 0) {
            String question = buildQuestion(args, askIndex).trim();
            if (question.isEmpty()) {
                System.out.println("Usage: --ask <question>");
                return;
            }
            runSingleQuestion(question, allowVisual);
            return;
        }
        runSession(new Scanner(System.in), allowVisual);
    }

    public static void runSession(Scanner scanner, boolean allowVisual) {
        ReactiveVisualAI ai = new ReactiveVisualAI();
        boolean autoVisualize = allowVisual;
        int thoughtsPerVideo = 3;
        double entropyThreshold = 0.2;
        int maxVideosPerQuestion = 1;
        int maxPendingVideos = 1;

        ai.setAutoVisualize(autoVisualize);
        ai.setThoughtsPerVideo(thoughtsPerVideo);
        ai.setMinEntropyChange(entropyThreshold);
        ai.setMaxVideosPerQuestion(maxVideosPerQuestion);
        ai.setMaxPendingVideos(maxPendingVideos);

        printHelp();

        while (true) {
            System.out.print("maxheadroom> ");
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
            if (line.equalsIgnoreCase("stats")) {
                System.out.println(ai.getStats());
                continue;
            }
            if (line.equalsIgnoreCase("toggle")) {
                if (!allowVisual) {
                    System.out.println("   Visual generation disabled. Relaunch with --visual to enable.");
                    continue;
                }
                autoVisualize = !autoVisualize;
                ai.setAutoVisualize(autoVisualize);
                System.out.println("   Auto-visualization: " + (autoVisualize ? "ON" : "OFF"));
                continue;
            }
            if (line.startsWith("thoughts ")) {
                Integer value = parseInt(line.substring(9).trim(), "thoughts");
                if (value != null && value > 0) {
                    thoughtsPerVideo = value;
                    ai.setThoughtsPerVideo(thoughtsPerVideo);
                    System.out.println("   Thoughts per video: " + thoughtsPerVideo);
                }
                continue;
            }
            if (line.startsWith("entropy ")) {
                Double value = parseDouble(line.substring(8).trim(), "entropy");
                if (value != null && value > 0) {
                    entropyThreshold = value;
                    ai.setMinEntropyChange(entropyThreshold);
                    System.out.println("   Entropy threshold: " + entropyThreshold);
                }
                continue;
            }
            if (line.equalsIgnoreCase("demo")) {
                for (String question : List.of("What is the golden ratio?", "Describe a creative idea for local AI.")) {
                    ai.ask(question);
                    ai.awaitVisuals();
                }
                continue;
            }

            String question = line.startsWith("ask ") ? line.substring(4).trim() : line;
            if (question.isEmpty()) {
                System.out.println("Usage: ask <question>");
                continue;
            }
            System.out.println();
            String response = ai.ask(question);
            System.out.println("AI> " + response.trim());
            System.out.println();
        }
        ai.awaitVisuals();
    }

    private static void runSingleQuestion(String question, boolean allowVisual) {
        ReactiveVisualAI ai = new ReactiveVisualAI();
        ai.setAutoVisualize(allowVisual);
        ai.setThoughtsPerVideo(3);
        ai.setMinEntropyChange(0.2);
        ai.setMaxVideosPerQuestion(1);
        ai.setMaxPendingVideos(1);

        String response = ai.ask(question);
        System.out.println("AI> " + response.trim());
        ai.awaitVisuals();
    }

    private static int indexOfFlag(String[] args, String flag) {
        if (args == null || args.length == 0) {
            return -1;
        }
        for (int i = 0; i < args.length; i++) {
            if (flag.equalsIgnoreCase(args[i])) {
                return i;
            }
        }
        return -1;
    }

    private static boolean hasFlag(String[] args, String flag) {
        return indexOfFlag(args, flag) >= 0;
    }

    private static String buildQuestion(String[] args, int askIndex) {
        if (args == null || askIndex < 0 || askIndex >= args.length - 1) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (int i = askIndex + 1; i < args.length; i++) {
            if ("--visual".equalsIgnoreCase(args[i])) {
                continue;
            }
            parts.add(args[i]);
        }
        return String.join(" ", parts);
    }

    private static Integer parseInt(String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + value);
            return null;
        }
    }

    private static Double parseDouble(String value, String label) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("   [!] Invalid " + label + ": " + value);
            return null;
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("MAX HEADROOM LIVE COMMANDS:");
        System.out.println("  ask <question>       - Ask a question (visual thinking)");
        System.out.println("  toggle               - Toggle visual generation (ON with --visual; uses GPU/Python)");
        System.out.println("  thoughts <n>          - Thoughts per video trigger");
        System.out.println("  entropy <value>       - Entropy delta threshold (e.g. 0.2)");
        System.out.println("  stats                - Show session stats");
        System.out.println("  demo                 - Optional demo script");
        System.out.println("  --ask <question>     - One-shot question (CLI mode)");
        System.out.println("  --visual             - Allow visual generation (required for toggle)");
        System.out.println("  stop|exit             - Return to Fraynix");
        System.out.println();
    }
}
