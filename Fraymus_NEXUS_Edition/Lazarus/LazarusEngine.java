// package com.eyeoverthink.lazarus;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;

// /**
//  * THE LAZARUS ENGINE
//  * "The petri dish."
//  */
// public class LazarusEngine {

//     private static final int MAX_POPULATION = 100;
//     private List<BioNode> population;
//     private ScheduledExecutorService heartbeat;
//     private int generationCount = 0;

//     public static void main(String[] args) {
//         // Self-Test / Ignition
//         new LazarusEngine().startLife();
//     }

//     public LazarusEngine() {
//         this.population = new ArrayList<>();
//         for (int i = 0; i < 10; i++) population.add(new BioNode(null));
//     }

//     public void startLife() {
//         System.out.println("   🧬 LAZARUS ENGINE: LIFE DETECTED.");
//         heartbeat = Executors.newSingleThreadScheduledExecutor();
//         heartbeat.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
//     }

//     private void tick() {
//         try {
//             List<BioNode> nextGen = new ArrayList<>(population);
//             for (BioNode node : population) {
//                 node.update();
//                 if (node.readyToReproduce() && population.size() < MAX_POPULATION) {
//                     nextGen.add(new BioNode(node));
//                 }
//             }
//             if (nextGen.size() > 50) nextGen.remove(0);
//             population = nextGen;
//             generationCount++;

//             if (generationCount % 50 == 0) {
//                 double avgFreq = population.stream().mapToDouble(n -> n.dna.frequency).average().orElse(0);
//                 System.out.println("   [LAZARUS] Gen: " + generationCount + " | Pop: " + population.size() + " | Avg Freq: " + String.format("%.2f", avgFreq) + " Hz");
//             }
//         } catch (Exception e) { e.printStackTrace(); }
//     }

//     public void injectEnergy() {
//         System.out.println("   ⚡ ENERGY INJECTION.");
//         for (BioNode node : population) node.brain.mutate();
//     }
// }

package com.eyeoverthink.lazarus;

import fraymus.temporal.TemporalArchive; // NEW IMPORT
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LazarusEngine {

    private static final int MAX_POPULATION = 100;
    private List<BioNode> population;
    private ScheduledExecutorService heartbeat;
    private int generationCount = 0;
    
    // THE TIME KEEPER
    private TemporalArchive archive;

    public static void main(String[] args) {
        new LazarusEngine().startLife();
    }

    public LazarusEngine() {
        this.population = new ArrayList<>();
        this.archive = new TemporalArchive(); // Connect Memory
        for (int i = 0; i < 10; i++) population.add(new BioNode(null));
    }

    public void startLife() {
        System.out.println("   🧬 LAZARUS ENGINE: LIFE DETECTED.");
        archive.preserve("Genesis", this.extractState()); // Save Birth
        
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
                System.out.println("   [LAZARUS] Gen: " + generationCount + " | Avg Freq: " + String.format("%.2f", avgFreq) + " Hz");
                
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

    // Helper for JSON state
    private Object extractState() {
        return new PopulationSnapshot(generationCount, new ArrayList<>(population));
    }
    
    private static class PopulationSnapshot {
        int generation;
        List<BioNode> nodes;
        public PopulationSnapshot(int g, List<BioNode> n) { this.generation = g; this.nodes = n; }
    }
}