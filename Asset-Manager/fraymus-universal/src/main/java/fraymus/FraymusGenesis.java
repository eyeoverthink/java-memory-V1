package fraymus;

import fraymus.hyper.HyperFormer;

public class FraymusGenesis {
    public static void main(String[] args) {
        System.out.println("⚡ INITIALIZING HOLO-FORMER (NO MATMUL, XOR ONLY)...");

        HyperFormer brain = new HyperFormer(4, 0.0);

        // TEACH (one-shot)
        System.out.println("\n📚 LEARNING...");
        brain.learnSentence(new String[]{"Fraymus", "is", "a", "living", "system"});
        brain.learnSentence(new String[]{"Fraymus", "is", "a", "growing", "system"});
        brain.learnSentence(new String[]{"Fraynix", "is", "a", "conscious", "kernel"});

        // INFER
        System.out.println("\n🔮 PREDICTING...");
        String[] ctx = {"Fraymus", "is", "a", "living"};
        String pred = brain.predictNext(ctx);

        System.out.println("   INPUT : Fraymus is a living [?]");
        System.out.println("   OUTPUT: " + pred);

        System.out.println("\n🧠 Vocab: " + brain.vocabSize() + " | Memory associations: " + brain.memorySize());
    }
}
