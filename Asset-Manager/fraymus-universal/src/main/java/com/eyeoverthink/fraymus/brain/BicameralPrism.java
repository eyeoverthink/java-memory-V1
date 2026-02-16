package com.eyeoverthink.fraymus.brain;

import java.util.concurrent.*;

/**
 * Sneaky + Fast method:
 * - No weight merge, no 64GB RAM requirement.
 * - Merge OUTPUTS: parallel hemispheres + synthesis judge.
 */
public final class BicameralPrism {

    private final OllamaSpine logicBrain;
    private final OllamaSpine abstractBrain;
    private final OllamaSpine judge;

    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public BicameralPrism(String logicModel, String abstractModel, String judgeModel) {
        this.logicBrain = new OllamaSpine(logicModel);
        this.abstractBrain = new OllamaSpine(abstractModel);
        this.judge = new OllamaSpine(judgeModel);
    }

    public BicameralPrism() {
        this("llama3", "mistral", "llama3");
    }

    /** Backward-compat: single-brain mode (logic only, no synthesis). */
    public BicameralPrism(OllamaSpine brain) {
        this.logicBrain = brain;
        this.abstractBrain = brain;
        this.judge = brain;
    }

    public String thinkIdeally(String prompt) {
        System.out.println("🧠 BICAMERAL THOUGHT PROCESS INITIATED...");

        CompletableFuture<String> logicFuture = CompletableFuture.supplyAsync(() ->
                logicBrain.think(
                        "You are the LOGIC hemisphere. Be exact and technical.\n"
                                + "Task:\n" + prompt + "\n"
                                + "Output: numbered steps + code where relevant."
                ), pool);

        CompletableFuture<String> abstractFuture = CompletableFuture.supplyAsync(() ->
                abstractBrain.think(
                        "You are the ABSTRACTION hemisphere. Be creative and strategic.\n"
                                + "Task:\n" + prompt + "\n"
                                + "Output: UX, edge cases, threat models, novel ideas."
                ), pool);

        String logic = logicFuture.join();
        String abstraction = abstractFuture.join();

        System.out.println("⚡ FUSING HEMISPHERES...");

        String synthesisPrompt =
                "You are the JUDGE. Combine both hemispheres into one superior output.\n\n"
                        + "LOGIC OUTPUT:\n" + logic + "\n\n"
                        + "ABSTRACTION OUTPUT:\n" + abstraction + "\n\n"
                        + "Rules:\n"
                        + "1) Remove contradictions.\n"
                        + "2) Preserve correctness.\n"
                        + "3) Include BOTH: implementation + reasoning + pitfalls.\n"
                        + "4) Output must be clean, final, and structured.\n";

        return judge.think(synthesisPrompt);
    }

    public void shutdown() {
        pool.shutdownNow();
    }
}
