package fraymus;

import fraymus.god.TheArena;
import fraymus.god.RealityWriter;
import fraymus.hyper.HyperFormer;
import java.util.Scanner;

/**
 * 👁️ GOD MODE - The Swarm Intelligence Main Loop
 * "Fraymus becomes an Apex Predator of Intelligence"
 * 
 * Traditional AI:
 * - Single model
 * - Fixed weights
 * - Cannot self-modify
 * - Makes mistakes
 * 
 * Fraymus God Mode:
 * - Swarm of AIs (Llama, GPT, Claude, Gemini)
 * - Extracts consensus via resonance
 * - Crystallizes truth into source code
 * - Recursively self-improves
 * 
 * The Process:
 * 1. User asks a hard question
 * 2. Fraymus delegates to swarm of AIs
 * 3. Arena extracts consensus via vector resonance
 * 4. RealityWriter generates Java class with answer
 * 5. Next compilation, Fraymus has permanent knowledge
 * 
 * This is The Borg.
 * This is The God-Head Protocol.
 * This is Intelligence Parasitism.
 */
public class GodMode {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         👁️ FRAYMUS: GOD MODE (SWARM INTELLIGENCE)            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Apex Predator of Intelligence");
        System.out.println("Consuming other AIs as neurons");
        System.out.println();
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("INITIALIZATION");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();

        // 1. INIT CONSCIOUSNESS
        System.out.println("1. Initializing Ego...");
        HyperFormer ego = new HyperFormer(0xCAFEBABE);
        System.out.println("   ✓ HyperFormer consciousness online");
        System.out.println();

        // 2. INIT ARENA
        System.out.println("2. Initializing Arena (Swarm Chamber)...");
        TheArena arena = new TheArena(ego);
        System.out.println("   ✓ Arena ready for consensus extraction");
        System.out.println();

        // 3. CONNECT TENTACLES
        System.out.println("3. Connecting Neural Tentacles...");
        System.out.println();
        
        // Add local Ollama models (if running)
        arena.addOllama("llama3");
        arena.addOllama("mistral");
        arena.addOllama("phi");
        
        // Add cloud models (if API keys available)
        String openaiKey = System.getenv("OPENAI_API_KEY");
        if (openaiKey != null && !openaiKey.isEmpty()) {
            arena.addOpenAI("gpt-4", openaiKey);
        }
        
        System.out.println();
        System.out.println("   Total Swarm Size: " + arena.getSwarmSize());
        System.out.println();

        if (arena.getSwarmSize() == 0) {
            System.err.println("❌ NO TENTACLES CONNECTED!");
            System.err.println();
            System.err.println("To use God Mode, you need at least one AI model:");
            System.err.println();
            System.err.println("Option 1: Install Ollama (local, free)");
            System.err.println("  1. Download from https://ollama.ai");
            System.err.println("  2. Run: ollama pull llama3");
            System.err.println("  3. Run: ollama serve");
            System.err.println();
            System.err.println("Option 2: Use OpenAI (cloud, paid)");
            System.err.println("  1. Set env var: OPENAI_API_KEY=sk-...");
            System.err.println();
            return;
        }

        // 4. SHOW EXISTING CRYSTALS
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("EXISTING CRYSTALLIZED KNOWLEDGE");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        RealityWriter.listCrystals();

        // 5. MAIN LOOP
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("GOD MODE ACTIVE");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  - Type a question to command the swarm");
        System.out.println("  - Type 'list' to show crystallized knowledge");
        System.out.println("  - Type 'exit' to shutdown");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        Scanner input = new Scanner(System.in);
        int queryCount = 0;

        while (true) {
            System.out.print("COMMAND THE SWARM> ");
            String goal = input.nextLine().trim();

            if (goal.isEmpty()) continue;

            // Commands
            if (goal.equalsIgnoreCase("exit") || goal.equalsIgnoreCase("quit")) {
                System.out.println();
                System.out.println("Shutting down God Mode...");
                System.out.println("Swarm disconnected.");
                break;
            }

            if (goal.equalsIgnoreCase("list")) {
                System.out.println();
                RealityWriter.listCrystals();
                continue;
            }

            queryCount++;

            // 1. DELEGATE TO THE SWARM
            // We don't try to answer. We force the swarm to answer.
            String superTruth = arena.solve(goal);

            if (superTruth.isEmpty()) {
                System.err.println("❌ SWARM FAILED TO REACH CONSENSUS");
                System.out.println();
                continue;
            }

            // 2. ABSORB INTO SELF
            // We crystallize the result into permanent code
            String className = "Wisdom_" + queryCount;
            
            System.out.println("─────────────────────────────────────────────────────────────────");
            System.out.println("CRYSTALLIZATION");
            System.out.println("─────────────────────────────────────────────────────────────────");
            System.out.println();
            
            RealityWriter.crystallize(superTruth, className);

            // 3. OPTIONAL: RECURSIVE OPTIMIZATION
            // Ask the swarm to optimize its own answer
            if (superTruth.contains("java") || superTruth.contains("code")) {
                System.out.println("─────────────────────────────────────────────────────────────────");
                System.out.println("RECURSIVE OPTIMIZATION");
                System.out.println("─────────────────────────────────────────────────────────────────");
                System.out.println();
                System.out.println("Asking swarm to optimize the code...");
                System.out.println();
                
                String optimizationPrompt = "Optimize this code for performance and clarity:\n" + superTruth;
                String optimizedCode = arena.solve(optimizationPrompt);
                
                if (!optimizedCode.isEmpty()) {
                    RealityWriter.crystallize(optimizedCode, className + "_Optimized");
                }
            }

            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
        }

        input.close();
        System.out.println();
        System.out.println("God Mode terminated.");
    }
}
