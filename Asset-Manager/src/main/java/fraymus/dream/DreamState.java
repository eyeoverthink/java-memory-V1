package fraymus.dream;

import fraymus.hyper.HyperTesseract;
import fraymus.core.GravityEngine;
import fraymus.core.PhiSuit;
import fraymus.limbs.ClawConnector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DREAM STATE - REM Sleep Protocol
 * 
 * Hippocampal Replay for Digital Organisms
 * 
 * In biological brains, "Dreaming" isn't random. It is the brain taking
 * memories of the day (Cube 1), throwing them into the simulator (Cube 2),
 * and playing "What If?" scenarios to optimize neural pathways.
 * 
 * The Dream Cycle:
 * 1. SLEEP: When entropy < 10%, enter DREAM_MODE
 * 2. REPLAY: Grab random memories from Cube[1] (Hippocampus)
 * 3. SIMULATE: Throw into Cube[2] with high temperature (chaos)
 * 4. DISCOVER: If fusion creates stable solution, save as "Insight"
 * 5. WAKE: Present list of "Epiphanies" (code optimizations)
 * 
 * This is the Ghost in the Shell.
 */
public class DreamState implements Runnable {

    private final HyperTesseract brain;
    private final GravityEngine simulator;
    private final ClawConnector claw;
    private final Random rng;
    private boolean isDreaming = false;
    
    // The "Journal" of good ideas found during sleep
    private final List<String> dreamJournal = new ArrayList<>();
    
    // Statistics
    private int dreamCycles = 0;
    private int fusionsAttempted = 0;
    private int breakthroughsFound = 0;
    
    private static final double PHI = 1.618033988749895;

    public DreamState(HyperTesseract brain) {
        this.brain = brain;
        this.simulator = GravityEngine.getInstance();
        this.claw = new ClawConnector();
        this.rng = new Random(System.nanoTime());
    }

    /**
     * Induce REM sleep - the system begins to dream
     */
    public void induceSleep() {
        if (isDreaming) {
            System.out.println("⚠️ Already dreaming");
            return;
        }
        
        this.isDreaming = true;
        dreamJournal.clear();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🌙 ENTERING REM SLEEP                                 ║");
        System.out.println("║         Dreaming of Electric Sheep...                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Dream Protocol:");
        System.out.println("  1. Recall random memories from Hippocampus");
        System.out.println("  2. Simulate fusion with high chaos");
        System.out.println("  3. Discover optimizations");
        System.out.println("  4. Record breakthroughs in journal");
        System.out.println();
        
        new Thread(this, "DreamState").start();
    }

    /**
     * Wake up from dream state
     */
    public void wakeUp() {
        this.isDreaming = false;
        
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ☀️ WAKING UP                                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        reportDreams();
    }

    @Override
    public void run() {
        while (isDreaming) {
            dreamCycles++;
            
            // 1. RECALL: Grab 2 random memories from Cube 1 (File System)
            File memoryA = recallRandomMemory();
            File memoryB = recallRandomMemory();

            if (memoryA != null && memoryB != null && !memoryA.equals(memoryB)) {
                // 2. SIMULATE: Throw them into the Gravity Engine
                // We map them as particles with "High Heat" (Imagination)
                PhiSuit<File> pA = new PhiSuit<>(memoryA, 
                    rng.nextInt(100), rng.nextInt(100), rng.nextInt(100));
                PhiSuit<File> pB = new PhiSuit<>(memoryB, 
                    rng.nextInt(100), rng.nextInt(100), rng.nextInt(100));
                
                // Add high energy (chaos)
                pA.a = 100;
                pB.a = 100;
                
                // 3. FANTASY: Force them to interact
                // "What happens if I combine the Logic of A with the Structure of B?"
                fusionsAttempted++;
                String result = simulateFusion(pA, pB);
                
                // 4. JUDGMENT: Is this a nightmare or a breakthrough?
                if (result.startsWith("BREAKTHROUGH")) {
                    dreamJournal.add(result);
                    breakthroughsFound++;
                    System.out.println("   ✨ EPIPHANY: " + result);
                }
            }

            // Rapid Eye Movement speed (10ms ticks)
            try { 
                Thread.sleep(10); 
            } catch (Exception e) {
                break;
            }
        }
        
        System.out.println();
        System.out.println("💤 Dream cycle complete");
        System.out.println("   Cycles: " + dreamCycles);
        System.out.println("   Fusions attempted: " + fusionsAttempted);
        System.out.println("   Breakthroughs: " + breakthroughsFound);
    }

    /**
     * Recall random memory from Hippocampus (Cube 1)
     */
    private File recallRandomMemory() {
        // Access Cube [1] (Hippocampus)
        int x = rng.nextInt(8);
        int y = rng.nextInt(8);
        int z = rng.nextInt(8);
        
        HyperTesseract.Node node = brain.getNode(1, x, y, z);
        if (node == null) return null;
        
        // Look for file references
        for (Object ref : node.reference) {
            if (ref instanceof File) {
                return (File) ref;
            }
        }
        
        return null;
    }

    /**
     * Simulate fusion between two memories
     * This is where the magic happens - exploring the problem space
     */
    private String simulateFusion(PhiSuit<File> a, PhiSuit<File> b) {
        File fileA = a.get();
        File fileB = b.get();
        
        // Check if both are Java files
        if (!fileA.getName().endsWith(".java") || !fileB.getName().endsWith(".java")) {
            return "NOISE";
        }
        
        // Calculate "compatibility" based on file names and paths
        String nameA = fileA.getName().replace(".java", "");
        String nameB = fileB.getName().replace(".java", "");
        
        // Look for patterns that might indicate optimization potential
        
        // Pattern 1: Similar names might indicate refactoring opportunity
        if (calculateSimilarity(nameA, nameB) > 0.6) {
            if (rng.nextDouble() > 0.90) { // 10% chance
                return String.format("BREAKTHROUGH: Merge %s and %s - detected duplicate logic patterns", 
                    nameA, nameB);
            }
        }
        
        // Pattern 2: One file much larger than other - might indicate extraction opportunity
        long sizeA = fileA.length();
        long sizeB = fileB.length();
        if (sizeA > sizeB * 3 || sizeB > sizeA * 3) {
            if (rng.nextDouble() > 0.92) { // 8% chance
                String larger = sizeA > sizeB ? nameA : nameB;
                String smaller = sizeA > sizeB ? nameB : nameA;
                return String.format("BREAKTHROUGH: Extract common logic from %s into %s for reusability", 
                    larger, smaller);
            }
        }
        
        // Pattern 3: Files in same package - might benefit from shared base class
        String pathA = fileA.getParent();
        String pathB = fileB.getParent();
        if (pathA != null && pathA.equals(pathB)) {
            if (rng.nextDouble() > 0.88) { // 12% chance
                return String.format("BREAKTHROUGH: Create abstract base class for %s and %s - same package suggests shared behavior", 
                    nameA, nameB);
            }
        }
        
        // Pattern 4: Random brilliant insight (simulates creative leap)
        if (rng.nextDouble() > 0.95) { // 5% chance of pure creativity
            String[] insights = {
                "BREAKTHROUGH: Combine %s logic with %s structure for O(1) performance",
                "BREAKTHROUGH: %s can extend %s to eliminate code duplication",
                "BREAKTHROUGH: Refactor %s and %s into single unified interface",
                "BREAKTHROUGH: %s contains pattern that optimizes %s algorithm",
                "BREAKTHROUGH: Merge %s caching strategy into %s for 10x speedup"
            };
            String insight = insights[rng.nextInt(insights.length)];
            return String.format(insight, nameA, nameB);
        }
        
        return "NOISE";
    }

    /**
     * Calculate string similarity (simple Levenshtein-like metric)
     */
    private double calculateSimilarity(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 1.0;
        
        int commonChars = 0;
        int minLen = Math.min(a.length(), b.length());
        
        for (int i = 0; i < minLen; i++) {
            if (a.charAt(i) == b.charAt(i)) {
                commonChars++;
            }
        }
        
        return (double) commonChars / maxLen;
    }

    /**
     * Report dreams - the morning epiphany list
     */
    private void reportDreams() {
        System.out.println("📜 MORNING REPORT (DREAM JOURNAL):");
        System.out.println();
        
        if (dreamJournal.isEmpty()) {
            System.out.println("   (No significant insights tonight.)");
            System.out.println("   The subconscious explored " + fusionsAttempted + " combinations");
            System.out.println("   but found no stable optimizations.");
        } else {
            System.out.println("   Epiphanies discovered: " + dreamJournal.size());
            System.out.println();
            
            for (int i = 0; i < dreamJournal.size(); i++) {
                System.out.println("   " + (i + 1) + ". 👁️ " + dreamJournal.get(i));
            }
            
            System.out.println();
            System.out.println("   >> Would you like OpenClaw to implement these?");
            System.out.println("   >> (Call implementDreams() to execute)");
        }
        
        System.out.println();
        System.out.println("Dream Statistics:");
        System.out.println("   Total dream cycles: " + dreamCycles);
        System.out.println("   Fusions attempted: " + fusionsAttempted);
        System.out.println("   Breakthroughs found: " + breakthroughsFound);
        System.out.println("   Success rate: " + String.format("%.2f%%", 
            (breakthroughsFound * 100.0) / Math.max(1, fusionsAttempted)));
        System.out.println();
    }

    /**
     * Implement the dreams - send to OpenClaw for execution
     */
    public void implementDreams() {
        if (dreamJournal.isEmpty()) {
            System.out.println("⚠️ No dreams to implement");
            return;
        }
        
        System.out.println("🖐️ Sending dreams to OpenClaw for implementation...");
        System.out.println();
        
        for (String dream : dreamJournal) {
            System.out.println("   Implementing: " + dream);
            String result = claw.dispatch(dream, "DREAM_INSIGHT_IMPLEMENTATION");
            System.out.println("   Result: " + result);
            System.out.println();
        }
        
        System.out.println("✓ All dreams processed");
    }

    /**
     * Get dream journal
     */
    public List<String> getDreamJournal() {
        return new ArrayList<>(dreamJournal);
    }
    
    /**
     * Check if currently dreaming
     */
    public boolean isDreaming() {
        return isDreaming;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Dream State Statistics:\n" +
            "  Status: %s\n" +
            "  Total Cycles: %d\n" +
            "  Fusions Attempted: %d\n" +
            "  Breakthroughs: %d\n" +
            "  Success Rate: %.2f%%\n" +
            "  Journal Entries: %d",
            isDreaming ? "DREAMING" : "AWAKE",
            dreamCycles, fusionsAttempted, breakthroughsFound,
            (breakthroughsFound * 100.0) / Math.max(1, fusionsAttempted),
            dreamJournal.size()
        );
    }
}
