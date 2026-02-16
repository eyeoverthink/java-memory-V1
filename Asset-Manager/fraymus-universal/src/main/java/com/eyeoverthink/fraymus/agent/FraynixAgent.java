package com.eyeoverthink.fraymus.agent;

import com.eyeoverthink.fraymus.brain.OllamaSpine;
import com.eyeoverthink.fraymus.limbs.ClawConnector;

import java.io.File;
import java.nio.file.Files;

public class FraynixAgent implements Runnable {

    private final OllamaSpine brain;
    private final ClawConnector hands;
    private final File targetFile;

    public FraynixAgent(File file) {
        this.brain = new OllamaSpine("llama3");
        this.hands = new ClawConnector();
        this.targetFile = file;
    }

    @Override
    public void run() {
        System.out.println("🕵️ AGENT: Analyzing " + targetFile.getName() + "...");

        try {
            // 1. READ
            String content = Files.readString(targetFile.toPath());

            // 2. THINK (Entropy Check)
            String prompt = "Analyze this Java code. Return 'CLEAN' if it is good. " +
                            "Return 'DIRTY: <Reason>' if it has bugs or inefficiencies.\n\n" + content;
            String verdict = brain.think(prompt);

            if (verdict.contains("DIRTY")) {
                System.out.println("⚠️ CORRUPTION DETECTED: " + verdict);

                // 3. ACT (OpenClaw Intervention)
                System.out.println("⚡ ACTIVATING OPENCLAW...");
                String task = "Read the file " + targetFile.getAbsolutePath() + ". " +
                              "Refactor it to fix the following issue: " + verdict + ". " +
                              "Save the file when done.";

                String result = hands.dispatch(task);
                System.out.println("✅ AGENT REPORT: " + result);
            } else {
                System.out.println("✨ FILE IS CLEAN.");
            }

        } catch (Exception e) {
            System.err.println("❌ AGENT FAILURE: " + e.getMessage());
        }
    }
}
