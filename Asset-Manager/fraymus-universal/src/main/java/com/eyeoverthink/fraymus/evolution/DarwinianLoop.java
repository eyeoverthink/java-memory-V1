package com.eyeoverthink.fraymus.evolution;

import com.eyeoverthink.fraymus.brain.BicameralPrism;
import com.eyeoverthink.fraymus.genesis.GenesisSandbox;
import com.eyeoverthink.fraymus.limbs.ClawConnector;

import java.nio.file.Path;
import java.util.Random;

public class DarwinianLoop implements Runnable {

    private final Path seedPath;
    private final GenesisSandbox sandbox;
    private final BicameralPrism brain;
    private final ClawConnector hands;
    private final Random rng;

    private volatile boolean running = true;
    private FraynixDNA currentDNA;

    public DarwinianLoop() {
        this(Path.of(".fraynix", "Fraynix_Seed.dna"), new Random());
    }

    public DarwinianLoop(Path seedPath, Random rng) {
        this.seedPath = seedPath;
        this.rng = rng;
        this.sandbox = new GenesisSandbox();
        this.brain = new BicameralPrism();
        this.hands = new ClawConnector();

        try {
            this.currentDNA = FraynixDNA.loadSeed(seedPath);
        } catch (Exception e) {
            this.currentDNA = new FraynixDNA();
        }
    }

    @Override
    public void run() {
        System.out.println("🐢 DARWINIAN ENGINE: Evolution started. Gen " + currentDNA.generation);

        while (running) {
            FraynixDNA mutant = currentDNA.mutate(rng);

            String proposedTrait = brain.thinkIdeally(
                "Our current physics engine is " + currentDNA.genes.get("PhysicsEngine") + ". " +
                "Propose a superior mathematical model for an AI OS. Return ONE word."
            );
            mutant.genes.put("PhysicsEngine", proposedTrait);

            System.out.println("🥚 GESTATING: Gen " + mutant.generation + " with " + proposedTrait + " physics...");
            boolean born = attemptBirth(mutant);

            if (born) {
                System.out.println("🚀 EVOLUTION: Mutant survived. Promoting DNA.");
                try {
                    mutant.saveSeed(seedPath);
                    currentDNA = mutant;
                } catch (Exception e) {
                    System.out.println("💥 SEED SAVE FAILED: " + e.getMessage());
                }
            } else {
                System.out.println("💀 STILLBIRTH: Mutant failed survival checks.");
            }

            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }

    private boolean attemptBirth(FraynixDNA dna) {
        String engine = dna.genes.getOrDefault("PhysicsEngine", "Newtonian");

        String task = "Implement a small Java class named PhysicsEngine with a method describe() that returns a one-line description of " + engine + " physics.";
        String claw = hands.ping() ? hands.dispatch(task) : "";

        String javaSource = generatePhysicsEngineClass(engine);

        GenesisSandbox.SandboxResult result = sandbox.verifyJavaSource("PhysicsEngine", javaSource);
        if (!result.success()) {
            System.out.println("   ❌ Sandbox compile failed: " + result.error());
            return false;
        }

        dna.fitnessScore = dna.fitnessScore + 1.0;
        return true;
    }

    private String generatePhysicsEngineClass(String engine) {
        String description;
        if ("Quantum".equalsIgnoreCase(engine)) {
            description = "Quantum: probabilistic state transitions + measurement collapse";
        } else if ("Relativistic".equalsIgnoreCase(engine)) {
            description = "Relativistic: time dilation + frame-dependent invariants";
        } else {
            description = "Newtonian: deterministic forces + classical integration";
        }

        return """
            public class PhysicsEngine {
                public String describe() {
                    return \"%s\";
                }
            }
            """.formatted(description);
    }

    public static void main(String[] args) {
        DarwinianLoop loop = new DarwinianLoop();
        loop.run();
    }
}
