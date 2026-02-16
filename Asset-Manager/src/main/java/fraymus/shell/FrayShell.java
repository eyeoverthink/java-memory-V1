package fraymus.shell;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;
import java.util.Scanner;

/**
 * 🐚 FRAYSH - The Hyper-Dimensional Shell
 * "Intent-based command line interface"
 * 
 * This replaces traditional shells (bash/zsh/powershell) with an associative interface.
 * 
 * Traditional Shell:
 * - Exact syntax required: "ls -la"
 * - Miss one character → command fails
 * - No learning or adaptation
 * 
 * FrayShell:
 * - Natural language: "show me files" or "list stuff"
 * - Vector resonance matching
 * - One-shot learning: teach new phrases instantly
 * 
 * Example:
 * - You type: "blast it"
 * - You bind it to: git push --force
 * - Later you type: "launch it"
 * - System: Vectors resonate → executes git push
 * 
 * This is an Associative Command Line.
 */
public class FrayShell {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🐚 FRAY-SH [HYPER-DIMENSIONAL SHELL]                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Type 'help' for commands. Type 'exit' to quit.");
        System.out.println();

        // 1. INIT BRAIN
        HyperFormer brain = new HyperFormer();
        IntentRegistry registry = new IntentRegistry();

        // 2. BOOTSTRAP SKILLS (Seed the memory)
        // We embed specific phrases to specific actions
        System.out.println("Bootstrapping muscle memory...");
        
        registry.register("ls", brain.embed("list files"), SystemSkills::listFiles);
        registry.register("ls_alt1", brain.embed("show files"), SystemSkills::listFiles);
        registry.register("ls_alt2", brain.embed("show me files"), SystemSkills::listFiles);
        registry.register("ls_alt3", brain.embed("what files"), SystemSkills::listFiles);
        
        registry.register("pwd", brain.embed("where am i"), SystemSkills::printWorkingDir);
        registry.register("pwd_alt1", brain.embed("current location"), SystemSkills::printWorkingDir);
        registry.register("pwd_alt2", brain.embed("current directory"), SystemSkills::printWorkingDir);
        
        registry.register("cat", brain.embed("read file"), SystemSkills::cat);
        registry.register("cat_alt1", brain.embed("show file"), SystemSkills::cat);
        registry.register("cat_alt2", brain.embed("display file"), SystemSkills::cat);
        
        registry.register("echo", brain.embed("say this"), SystemSkills::echo);
        registry.register("echo_alt1", brain.embed("print this"), SystemSkills::echo);
        
        registry.register("help", brain.embed("help me"), SystemSkills::help);
        registry.register("help_alt1", brain.embed("show help"), SystemSkills::help);
        
        System.out.println("✓ Loaded " + registry.size() + " intent mappings");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("ff> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) {
                System.out.println();
                System.out.println("👋 Goodbye!");
                break;
            }

            // 3. ENCODE INTENT
            // We break the input into words and bind them into a single Thought Vector
            String[] words = input.split("\\s+");
            HyperVector thought = new HyperVector(); 
            for (String w : words) {
                // Bundle every word's vector to create a "Sentence Concept"
                thought = thought.bundle(brain.embed(w)); 
            }

            // 4. RESOLVE ACTION
            // We set a lower threshold (0.51) to allow for "fuzzy" matching
            var action = registry.resolve(thought, 0.51);

            if (action != null) {
                action.accept(input);
            } else {
                System.out.println("❓ Intent not recognized. Teach me?");
                System.out.println("   (Type 'bind [ls/pwd/cat] <phrase>' to associate)");
                System.out.println();
                
                // 5. ONE-SHOT LEARNING
                if (input.startsWith("bind ")) {
                    String[] parts = input.split(" ", 3);
                    if (parts.length == 3) {
                        String cmd = parts[1];
                        String phrase = parts[2];
                        
                        // Create vector for new phrase
                        HyperVector newVec = new HyperVector();
                        for (String w : phrase.split("\\s+")) {
                            newVec = newVec.bundle(brain.embed(w));
                        }
                        
                        // Map it to existing logic
                        String learnedName = "learned_" + cmd + "_" + System.currentTimeMillis();
                        if (cmd.equals("ls")) {
                            registry.register(learnedName, newVec, SystemSkills::listFiles);
                        } else if (cmd.equals("pwd")) {
                            registry.register(learnedName, newVec, SystemSkills::printWorkingDir);
                        } else if (cmd.equals("cat")) {
                            registry.register(learnedName, newVec, SystemSkills::cat);
                        } else if (cmd.equals("echo")) {
                            registry.register(learnedName, newVec, SystemSkills::echo);
                        } else {
                            System.out.println("❌ Unknown command: " + cmd);
                            continue;
                        }
                        
                        System.out.println("✨ LEARNED: '" + phrase + "' now triggers " + cmd);
                        System.out.println();
                    } else {
                        System.out.println("❌ Usage: bind <command> <your phrase>");
                        System.out.println();
                    }
                }
            }
        }
        
        scanner.close();
    }
}
