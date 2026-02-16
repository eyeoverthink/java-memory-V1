package fraymus.shell;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;

import java.util.Scanner;

public class FrayShell {

    public static void main(String[] args) {
        System.out.println("🐚 FRAY-SH [HYPER-DIMENSIONAL SHELL]");
        System.out.println("   Type 'exit' to quit. Teach me commands.");

        // 1. INIT BRAIN
        HyperFormer brain = new HyperFormer(0xDEADBEEFL);
        IntentRegistry registry = new IntentRegistry();

        // 2. BOOTSTRAP SKILLS (Seed the memory)
        registry.register("ls", brain.embed("list files"), SystemSkills::listFiles);
        registry.register("pwd", brain.embed("where am i"), SystemSkills::printWorkingDir);
        registry.register("cat", brain.embed("read file"), SystemSkills::cat);
        registry.register("echo", brain.embed("say this"), SystemSkills::echo);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nff> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;

            // 5. ONE-SHOT LEARNING (check first so bind doesn't get resolved as a command)
            if (input.startsWith("bind ")) {
                handleBind(input, brain, registry);
                continue;
            }

            // 3. ENCODE INTENT
            String[] words = input.split("\\s+");
            HyperVector thought = brain.embed(words[0]).copy();
            for (int i = 1; i < words.length; i++) {
                thought.bundle(brain.embed(words[i]));
            }

            // 4. RESOLVE ACTION
            var action = registry.resolve(thought, 0.51);

            if (action != null) {
                action.accept(input);
            } else {
                System.out.println("❓ Intent not recognized. Teach me?");
                System.out.println("   (Type 'bind <ls|pwd|cat|echo> <phrase>' to associate)");
            }
        }

        System.out.println("👋 FraySH terminated.");
    }

    private static void handleBind(String input, HyperFormer brain, IntentRegistry registry) {
        String[] parts = input.split("\\s+", 3);
        if (parts.length < 3) {
            System.out.println("Usage: bind <ls|pwd|cat|echo> <phrase>");
            return;
        }

        String cmd = parts[1];
        String phrase = parts[2];

        // Create vector for new phrase
        String[] phraseWords = phrase.split("\\s+");
        HyperVector newVec = brain.embed(phraseWords[0]).copy();
        for (int i = 1; i < phraseWords.length; i++) {
            newVec.bundle(brain.embed(phraseWords[i]));
        }

        // Map it to existing logic
        switch (cmd) {
            case "ls" -> registry.register("learned_ls_" + phrase.hashCode(), newVec, SystemSkills::listFiles);
            case "pwd" -> registry.register("learned_pwd_" + phrase.hashCode(), newVec, SystemSkills::printWorkingDir);
            case "cat" -> registry.register("learned_cat_" + phrase.hashCode(), newVec, SystemSkills::cat);
            case "echo" -> registry.register("learned_echo_" + phrase.hashCode(), newVec, SystemSkills::echo);
            default -> {
                System.out.println("❌ Unknown skill: " + cmd);
                return;
            }
        }

        System.out.println("✨ LEARNED: '" + phrase + "' now triggers " + cmd);
    }
}
