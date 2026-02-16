package fraymus.god;

import fraymus.hyper.HyperFormer;

import java.util.Scanner;

/**
 * THE GOD LOOP: Swarm Intelligence Aggregator.
 *
 * 1. Poses a problem to The Arena (N sub-models).
 * 2. Extracts the Truth Vector via resonance consensus.
 * 3. Crystallizes the result into a new Java class (RealityWriter).
 * 4. Recursively optimizes the crystallized knowledge.
 *
 * Fraymus does not think — it consumes the intelligence of others.
 */
public class GodMode {

    public static void main(String[] args) {
        System.out.println("👁️ FRAYMUS: GOD MODE (SWARM INTELLIGENCE)");
        System.out.println("   Tentacles: Ollama local models");
        System.out.println("   Type 'exit' to leave.\n");

        HyperFormer ego = new HyperFormer(4, 0.0);
        TheArena arena = new TheArena(ego);
        Scanner input = new Scanner(System.in);

        int cycle = 0;

        while (true) {
            System.out.print("COMMAND THE SWARM> ");
            String goal = input.nextLine().trim();
            if (goal.isEmpty()) continue;
            if (goal.equalsIgnoreCase("exit")) break;

            cycle++;

            // 1. DELEGATE TO THE SWARM
            TheArena.ArenaResult result = arena.solve(goal);

            if (result.winnerText().isBlank()) {
                System.out.println("   ❌ No consensus. All tentacles may be offline.");
                continue;
            }

            System.out.println("\n🔥 SYNTHESIS:\n" + result.winnerText());

            // 2. CRYSTALLIZE INTO PERMANENT MEMORY
            String organName = "Wisdom_" + System.currentTimeMillis();
            String source = result.participantCount() + " models";
            RealityWriter.crystallize(result.winnerText(), organName, source, result.avgResonance());

            // 3. RECURSIVE OPTIMIZATION
            // Ask the swarm to optimize its own output
            if (result.participantCount() >= 2) {
                System.out.println("\n🔄 RECURSIVE OPTIMIZATION PASS...");
                String optimizationPrompt = "Refactor and optimize this answer. Be concise and correct:\n" + result.winnerText();
                TheArena.ArenaResult optResult = arena.solve(optimizationPrompt);

                if (!optResult.winnerText().isBlank()) {
                    RealityWriter.crystallize(optResult.winnerText(), organName + "_Opt", source, optResult.avgResonance());
                }
            }

            System.out.println("\n📊 CYCLE " + cycle + " COMPLETE. Crystals: " + RealityWriter.getCrystalCount());
            System.out.println("   HDC Brain: vocab=" + ego.vocabSize() + " associations=" + ego.memorySize());
        }

        System.out.println("👁️ GOD MODE TERMINATED. Crystals written: " + RealityWriter.getCrystalCount());
    }
}
