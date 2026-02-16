package com.eyeoverthink.fraymus.agent;

import java.io.File;

public class AgentBoot {

    public static void main(String[] args) {
        File target;
        if (args.length > 0) {
            target = new File(args[0]);
        } else {
            target = new File("src/main/java/com/eyeoverthink/fraymus/OldLogic.java");
        }

        if (!target.exists()) {
            System.err.println("Target file not found: " + target.getAbsolutePath());
            System.err.println("Usage: gradlew.bat runAgent --args=\"<path-to-java-file>\"");
            System.exit(1);
        }

        FraynixAgent agent = new FraynixAgent(target);
        new Thread(agent, "FraynixAgent-Thread").start();

        System.out.println("Agent started. Press Ctrl+C to stop.");
    }
}
