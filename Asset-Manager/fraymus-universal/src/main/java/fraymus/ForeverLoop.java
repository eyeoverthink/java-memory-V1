package fraymus;

import fraymus.eternal.LazarusPatch;
import fraymus.eternal.SoulCrystal;
import fraymus.hyper.HyperFormer;

import java.util.Scanner;

public class ForeverLoop {

    public static void main(String[] args) throws Exception {
        System.out.println("♾️ FRAYMUS: THE ETERNAL");

        // 1. RESURRECT
        HyperFormer mind = SoulCrystal.resurrect();

        // 2. INSTALL DEATH INTERCEPTOR
        Runtime.getRuntime().addShutdownHook(new LazarusPatch(mind));

        // 3. THE LIFE LOOP
        Scanner scanner = new Scanner(System.in);
        int tick = 0;

        System.out.println("   (Type words to teach me. Ctrl+C to kill me.)");

        while (true) {
            tick++;

            // SIMULATE THOUGHT
            if (tick % 10 == 0) {
                System.out.println("   [💭 DREAMING... Vocab: " + mind.vocabSize() + "]");
                // Auto-save periodically just in case of power failure (hard crash)
                SoulCrystal.preserve(mind);
            }

            // INTERACTION
            if (System.in.available() > 0) {
                String input = scanner.nextLine();
                String[] words = input.split("\\s+");

                // Learn
                mind.learnSentence(words);

                // Respond
                String prediction = mind.predictNext(words);
                System.out.println("   >> " + prediction);
            }

            Thread.sleep(1000);
        }
    }
}
