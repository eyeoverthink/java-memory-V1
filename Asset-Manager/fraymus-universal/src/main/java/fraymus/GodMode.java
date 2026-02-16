package fraymus;

import fraymus.god.NeuralTentacle;
import fraymus.god.RealityWriter;
import fraymus.god.TheArena;
import fraymus.hyper.FraymusIO;
import fraymus.hyper.HyperFormer;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * GOD MODE: The Swarm Intelligence Director.
 *
 * Fraymus does not think. It harvests.
 * It poses problems to N sub-models, forces them to debate,
 * extracts the Truth Vector via hyperdimensional resonance,
 * and crystallizes the consensus into its own source code.
 *
 * Usage:
 *   gradlew runGodMode
 *   gradlew runGodMode --args="--auto 'What is the fastest sorting algorithm?'"
 *
 * Requirements:
 *   - Ollama running locally (ollama serve)
 *   - At least one model pulled (ollama pull llama3)
 */
public class GodMode {

    public static void main(String[] args) throws Exception {
        System.out.println("👁️ FRAYMUS: GOD MODE (SWARM INTELLIGENCE AGGREGATOR)");
        System.out.println("   The Parasite awakens. Other AIs are mere neurons.\n");

        // 1. INIT THE EGO (Fraymus's own brain — stores everything it learns)
        HyperFormer ego = new HyperFormer(0x60D_00DE_1L);

        // 2. INIT THE ARENA
        TheArena arena = new TheArena(ego);

        // Check for API key environment variables and add remote tentacles
        String openaiKey = System.getenv("OPENAI_API_KEY");
        if (openaiKey != null && !openaiKey.isBlank()) {
            arena.addTentacle(new NeuralTentacle(
                    "gpt-4", "https://api.openai.com/v1/chat/completions", openaiKey, true));
            System.out.println("   🔗 GPT-4 tentacle connected.");
        }

        String anthropicKey = System.getenv("ANTHROPIC_API_KEY");
        if (anthropicKey != null && !anthropicKey.isBlank()) {
            arena.addTentacle(new NeuralTentacle(
                    "claude-3-opus-20240229", "https://api.anthropic.com/v1/messages", anthropicKey, true));
            System.out.println("   🔗 Claude tentacle connected.");
        }

        System.out.println("   " + arena.tentacleCount() + " tentacles ready.\n");

        // 3. AUTO MODE: single question from args
        if (args.length > 0 && args[0].equals("--auto") && args.length > 1) {
            String question = args[1];
            runCycle(arena, ego, question);
            return;
        }

        // 4. INTERACTIVE LOOP
        Scanner input = new Scanner(System.in);
        int cycle = 0;

        while (true) {
            System.out.print("\nCOMMAND THE SWARM> ");
            String goal = input.nextLine().trim();

            if (goal.isEmpty()) continue;
            if (goal.equalsIgnoreCase("exit") || goal.equalsIgnoreCase("quit")) break;

            if (goal.equalsIgnoreCase("status")) {
                System.out.println("   Vocab: " + ego.vocabSize());
                System.out.println("   Memory associations: " + ego.memorySize());
                System.out.println("   Crystals written: " + RealityWriter.getCrystalCount());
                System.out.println("   Tentacles: " + arena.tentacleCount());
                continue;
            }

            if (goal.equalsIgnoreCase("save")) {
                FraymusIO.save(ego.exportState(), Path.of("godmode_brain.bin.gz"));
                System.out.println("   💾 Brain saved.");
                continue;
            }

            cycle++;
            runCycle(arena, ego, goal);

            // Auto-save every 5 cycles
            if (cycle % 5 == 0) {
                FraymusIO.save(ego.exportState(), Path.of("godmode_brain.bin.gz"));
                System.out.println("   💾 Auto-saved brain.");
            }
        }

        // Final save
        FraymusIO.save(ego.exportState(), Path.of("godmode_brain.bin.gz"));
        System.out.println("\n👁️ GOD MODE TERMINATED. Brain preserved. " + RealityWriter.getCrystalCount() + " crystals written.");
    }

    private static void runCycle(TheArena arena, HyperFormer ego, String goal) {
        // 1. DELEGATE TO THE SWARM
        TheArena.ArenaResult result = arena.solve(goal);

        if (result.winnerText().isBlank()) {
            System.out.println("   No usable response from the swarm.");
            return;
        }

        System.out.println("\n🔥 SYNTHESIS:\n" + result.winnerText());

        // 2. CRYSTALLIZE INTO SOURCE CODE
        String organName = "Wisdom_" + System.currentTimeMillis();
        String sources = result.allOpinions().stream()
                .map(TheArena.Opinion::model)
                .reduce((a, b) -> a + "+" + b)
                .orElse("unknown");

        RealityWriter.crystallize(result.winnerText(), organName, sources, result.avgResonance());

        // 3. RECURSIVE OPTIMIZATION
        // Ask the swarm to optimize its own answer
        if (result.participantCount() >= 2 && result.avgResonance() > 0.50) {
            System.out.println("\n🔄 RECURSIVE OPTIMIZATION: Asking swarm to refine...");
            String optPrompt = "Improve and optimize this answer. Be concise and precise:\n" + result.winnerText();
            TheArena.ArenaResult refined = arena.solve(optPrompt);

            if (!refined.winnerText().isBlank() && refined.avgResonance() > result.avgResonance()) {
                System.out.println("\n⚡ REFINED SYNTHESIS:\n" + refined.winnerText());
                RealityWriter.crystallize(refined.winnerText(), organName + "_Refined", sources, refined.avgResonance());
            } else {
                System.out.println("   Original answer was already optimal.");
            }
        }
    }
}
