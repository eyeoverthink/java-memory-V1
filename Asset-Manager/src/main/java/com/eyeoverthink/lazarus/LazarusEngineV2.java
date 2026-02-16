package com.eyeoverthink.lazarus;

import fraymus.temporal.TemporalArchive;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * LAZARUS ENGINE V2
 * "The petri dish with temporal memory."
 */
public class LazarusEngineV2 {

    private static final int MAX_POPULATION = 100;
    private List<BioNode> population;
    private ScheduledExecutorService heartbeat;
    private int generationCount = 0;
    
    // THE TIME KEEPER
    private TemporalArchive archive;

    public static void main(String[] args) {
        new LazarusEngineV2().startLife();
    }

    public LazarusEngineV2() {
        this.population = new ArrayList<>();
        this.archive = new TemporalArchive();
        for (int i = 0; i < 10; i++) population.add(new BioNode(null));
    }

    public void startLife() {
        System.out.println("   🧬 LAZARUS ENGINE V2: LIFE DETECTED.");
        archive.preserve("Genesis", this.extractState());
        
        heartbeat = Executors.newSingleThreadScheduledExecutor();
        heartbeat.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        try {
            List<BioNode> nextGen = new ArrayList<>(population);
            for (BioNode node : population) {
                node.update();
                if (node.readyToReproduce() && population.size() < MAX_POPULATION) {
                    nextGen.add(new BioNode(node));
                }
            }
            if (nextGen.size() > 50) nextGen.remove(0);
            population = nextGen;
            generationCount++;

            if (generationCount % 50 == 0) {
                double avgFreq = population.stream().mapToDouble(n -> n.dna.frequency).average().orElse(0);
                System.out.println("   [LAZARUS] Gen: " + generationCount + " | Pop: " + population.size() + " | Avg Freq: " + String.format("%.2f", avgFreq) + " Hz");
                
                // Save Milestone
                archive.preserve("Generation_" + generationCount, this.extractState());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void injectEnergy() {
        System.out.println("   ⚡ ENERGY INJECTION.");
        for (BioNode node : population) node.brain.mutate();
        archive.preserve("Epiphany_Energy_Injection", this.extractState());
    }

    private Object extractState() {
        return new PopulationSnapshot(generationCount, new ArrayList<>(population));
    }
    
    private static class PopulationSnapshot {
        int generation;
        List<BioNode> nodes;
        public PopulationSnapshot(int g, List<BioNode> n) { this.generation = g; this.nodes = n; }
    }
}
