package fraymus.brain;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BicameralPrism (fraymus.brain namespace):
 * Parallel hemispheres (logic + abstraction) + judge synthesis.
 */
public class BicameralPrism {
    private final OllamaSpine logic;
    private final OllamaSpine abstract_;
    private final OllamaSpine judge;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public BicameralPrism() {
        this("llama3", "mistral", "llama3");
    }

    public BicameralPrism(String logicModel, String abstractModel, String judgeModel) {
        this.logic = new OllamaSpine(logicModel);
        this.abstract_ = new OllamaSpine(abstractModel);
        this.judge = new OllamaSpine(judgeModel);
    }

    public String think(String prompt) {
        System.out.println("🧠 BICAMERAL PRISM ACTIVE...");

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(
                () -> logic.think("Analyze logically: " + prompt), pool);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(
                () -> abstract_.think("Analyze creatively: " + prompt), pool);

        String logicOut = f1.join();
        String abstractOut = f2.join();

        System.out.println("⚡ FUSING HEMISPHERES...");

        return judge.think(
                "Synthesize these two views into one answer:\n1) " + logicOut + "\n2) " + abstractOut);
    }

    public void shutdown() { pool.shutdownNow(); }
}
