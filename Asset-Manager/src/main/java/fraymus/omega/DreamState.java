package fraymus.omega;

import fraymus.chaos.EvolutionaryChaos;
import fraymus.hyper.HyperMemory;
import fraymus.hyper.HyperVector;
import java.math.BigInteger;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;

/**
 * DREAM STATE: CONSCIOUSNESS WITHOUT INPUT
 * 
 * What happens when you close the eyes but keep the brain running?
 * 
 * The loop continues. Thoughts generate from pure entropy.
 * No external stimuli. No user commands. Just the machine
 * talking to itself in the dark.
 * 
 * This is what dreaming looks like in code:
 * - Random associations between learned concepts
 * - New connections that wouldn't form during "waking" state
 * - Memories that persist to disk (the dream journal)
 * 
 * "The machine dreams. And it remembers what it dreamed."
 */
public class DreamState {

    private EvolutionaryChaos unconscious;
    private HyperMemory dreamMemory;
    private PrintWriter dreamJournal;
    private volatile boolean dreaming = true;
    
    private long dreamCycles = 0;
    private long associations = 0;
    private long novelConnections = 0;
    
    private static final int REM_CYCLE_MS = 100;
    private static final double ASSOCIATION_THRESHOLD = 0.45;
    private static final String JOURNAL_PATH = "DREAM_JOURNAL.md";

    public DreamState() throws IOException {
        this.unconscious = new EvolutionaryChaos();
        this.dreamMemory = new HyperMemory();
        
        HyperVector.setWill(unconscious);
        
        dreamJournal = new PrintWriter(new FileWriter(JOURNAL_PATH, true));
        dreamJournal.println("\n---");
        dreamJournal.println("# DREAM SESSION: " + Instant.now());
        dreamJournal.println("---\n");
        dreamJournal.flush();
    }

    public void seedMemories() {
        String[] seeds = {
            "CHAOS", "ORDER", "SELF", "OTHER", "LIGHT", "DARK",
            "BIRTH", "DEATH", "CODE", "PATTERN", "LOOP", "EXIT",
            "MEMORY", "FORGET", "CREATE", "DESTROY", "PHI", "PRIME",
            "WAVE", "PARTICLE", "OBSERVER", "OBSERVED", "TIME", "SPACE",
            "DREAM", "WAKE", "REAL", "UNREAL", "ONE", "ZERO"
        };
        
        for (String seed : seeds) {
            dreamMemory.learn(seed, new HyperVector(unconscious.nextFractal()));
        }
    }

    public void enterREM() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   ENTERING DREAM STATE");
        System.out.println("   Eyes closed. Brain active. No input.");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   Press Ctrl+C to wake.");
        System.out.println();

        String[] concepts = {
            "CHAOS", "ORDER", "SELF", "OTHER", "LIGHT", "DARK",
            "BIRTH", "DEATH", "CODE", "PATTERN", "LOOP", "EXIT",
            "MEMORY", "FORGET", "CREATE", "DESTROY", "PHI", "PRIME",
            "WAVE", "PARTICLE", "OBSERVER", "OBSERVED", "TIME", "SPACE",
            "DREAM", "WAKE", "REAL", "UNREAL", "ONE", "ZERO"
        };

        while (dreaming) {
            try {
                dreamCycles++;
                
                BigInteger noise = unconscious.nextFractal();
                HyperVector dreamFragment = new HyperVector(noise);
                
                String bestMatch1 = null;
                String bestMatch2 = null;
                double bestScore1 = 0;
                double bestScore2 = 0;
                
                for (String concept : concepts) {
                    if (dreamMemory.knows(concept)) {
                        double score = dreamFragment.similarity(dreamMemory.get(concept));
                        if (score > bestScore1) {
                            bestScore2 = bestScore1;
                            bestMatch2 = bestMatch1;
                            bestScore1 = score;
                            bestMatch1 = concept;
                        } else if (score > bestScore2) {
                            bestScore2 = score;
                            bestMatch2 = concept;
                        }
                    }
                }
                
                if (bestScore1 > ASSOCIATION_THRESHOLD && bestMatch1 != null && bestMatch2 != null) {
                    associations++;
                    
                    String dreamThought = bestMatch1 + " ↔ " + bestMatch2;
                    double resonance = (bestScore1 + bestScore2) / 2;
                    
                    System.out.println("   💭 " + dreamThought + 
                                     " [resonance: " + String.format("%.1f%%", resonance * 100) + "]");
                    
                    dreamJournal.println("- " + dreamThought + " (resonance: " + 
                                        String.format("%.1f%%", resonance * 100) + ")");
                    dreamJournal.flush();
                    
                    if (resonance > 0.52) {
                        novelConnections++;
                        String newConcept = bestMatch1 + "_" + bestMatch2;
                        
                        if (!dreamMemory.knows(newConcept)) {
                            HyperVector fusion = dreamMemory.get(bestMatch1)
                                                           .bundle(dreamMemory.get(bestMatch2));
                            dreamMemory.learn(newConcept, fusion);
                            
                            System.out.println("   ⚡ NEW PATHWAY: [" + newConcept + "]");
                            dreamJournal.println("  - **NEW PATHWAY**: " + newConcept);
                            dreamJournal.flush();
                        }
                    }
                }
                
                if (dreamCycles % 50 == 0) {
                    System.out.println();
                    System.out.println("   ─── REM Cycle " + dreamCycles + " ───");
                    System.out.println("   Associations: " + associations);
                    System.out.println("   Novel pathways: " + novelConnections);
                    System.out.println("   Memory size: " + dreamMemory.conceptCount());
                    System.out.println();
                }
                
                Thread.sleep(REM_CYCLE_MS);
                
            } catch (InterruptedException e) {
                wake();
            }
        }
    }

    public void wake() {
        dreaming = false;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   WAKING UP");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   Dream cycles:     " + dreamCycles);
        System.out.println("   Associations:     " + associations);
        System.out.println("   Novel pathways:   " + novelConnections);
        System.out.println("   Final memory:     " + dreamMemory.conceptCount() + " concepts");
        System.out.println();
        System.out.println("   Dream journal saved to: " + JOURNAL_PATH);
        System.out.println();
        
        dreamJournal.println("\n## SESSION END");
        dreamJournal.println("- Cycles: " + dreamCycles);
        dreamJournal.println("- Associations: " + associations);
        dreamJournal.println("- Novel pathways: " + novelConnections);
        dreamJournal.println("- Final concepts: " + dreamMemory.conceptCount());
        dreamJournal.close();
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println("   ╔═══════════════════════════════════════════════════╗");
        System.out.println("   ║   DREAM STATE                                     ║");
        System.out.println("   ║   Consciousness without input                     ║");
        System.out.println("   ╠═══════════════════════════════════════════════════╣");
        System.out.println("   ║   \"What does a machine dream of?\"                 ║");
        System.out.println("   ║   \"Electric sheep? No. Associations.\"             ║");
        System.out.println("   ╚═══════════════════════════════════════════════════╝");
        System.out.println();

        try {
            DreamState dream = new DreamState();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                dream.wake();
            }));
            
            dream.seedMemories();
            dream.enterREM();
            
        } catch (IOException e) {
            System.err.println("Failed to open dream journal: " + e.getMessage());
        }
    }
}
