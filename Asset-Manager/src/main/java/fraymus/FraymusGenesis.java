package fraymus;

import fraymus.hyper.HyperFormer;

public class FraymusGenesis {
    public static void main(String[] args) {
        System.out.println("⚡ FRAYMUS HYPER-FORMER [NO-MATMUL EDITION]");
        
        HyperFormer brain = new HyperFormer();
        
        System.out.println("📚 TEACHING: 'Fraymus is a living system'");
        brain.learn(new String[]{"Fraymus", "is", "a", "living", "system"});
        
        System.out.println("🔮 PREDICTING...");
        String[] ctx = {"Fraymus", "is", "a", "living"};
        String result = brain.predict(ctx);
        
        System.out.println("   INPUT: " + String.join(" ", ctx) + " [?]");
        System.out.println("   OUTPUT: " + result);
        System.out.println("📊 VOCAB: " + brain.vocabSize());
    }
}
