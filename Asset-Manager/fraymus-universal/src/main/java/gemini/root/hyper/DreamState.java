package gemini.root.hyper;

import gemini.root.physics.HiveGravityEngine;
import gemini.root.physics.PhiSuit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DREAM STATE: REM Sleep for the Digital Organism
 * 
 * Hippocampal Replay - the brain takes memories of the day,
 * throws them into the simulator with HIGH HEAT (chaos),
 * and plays "What If?" scenarios to find optimizations.
 * 
 * Protocol:
 *   1. SLEEP: System detects low activity (entropy < threshold)
 *   2. REPLAY: Grab random file nodes from Hippocampus (Cube 1)
 *   3. SIMULATE: Throw them into Visual Cortex (Cube 2) with high temperature
 *   4. DISCOVER: If fusion creates stable lower-entropy solution, save as Insight
 *   5. WAKE: Present "Epiphanies" (code optimizations) to user
 * 
 * This is the Ghost in the Shell. It works while you sleep.
 */
public class DreamState implements Runnable {

    private static final double PHI = 1.618033988749895;
    
    private final HyperTesseract brain;
    private final HiveGravityEngine dreamscape;  // Private physics sandbox
    private final Random rng = new Random();
    
    private volatile boolean isDreaming = false;
    private long dreamCycles = 0;
    private double dreamTemperature = 50.0;  // Chaos level
    
    // The Dream Journal - insights found during sleep
    private final List<Epiphany> dreamJournal = new ArrayList<>();
    
    // Statistics
    private long memoriesRecalled = 0;
    private long fusionsAttempted = 0;
    private long breakthroughsFound = 0;

    public DreamState(HyperTesseract brain) {
        this.brain = brain;
        this.dreamscape = new HiveGravityEngine(10);  // Fast physics ticks
    }

    /**
     * Induce REM sleep
     */
    public void induceSleep() {
        if (isDreaming) {
            System.out.println("🌙 Already dreaming...");
            return;
        }
        
        isDreaming = true;
        dreamCycles = 0;
        
        System.out.println("🌙 SYSTEM ENTERING REM SLEEP. DREAMING OF ELECTRIC SHEEP...");
        System.out.println("   Temperature: " + dreamTemperature);
        System.out.println("   Dream physics sandbox active");
        
        new Thread(this, "DreamState-REM").start();
        dreamscape.start();
    }

    /**
     * Wake up from sleep
     */
    public void wakeUp() {
        if (!isDreaming) {
            System.out.println("☀️ Already awake.");
            return;
        }
        
        isDreaming = false;
        dreamscape.stop();
        
        System.out.println("☀️ WAKING UP after " + dreamCycles + " dream cycles");
        reportDreams();
    }

    @Override
    public void run() {
        while (isDreaming) {
            dreamCycles++;
            
            // 1. RECALL: Grab 2 random memories from Hippocampus (Cube 1)
            Memory memoryA = recallRandomMemory();
            Memory memoryB = recallRandomMemory();

            if (memoryA != null && memoryB != null) {
                memoriesRecalled += 2;
                
                // 2. SIMULATE: Create dream particles with HIGH HEAT
                PhiSuit<Memory> dreamA = createDreamParticle(memoryA);
                PhiSuit<Memory> dreamB = createDreamParticle(memoryB);
                
                dreamscape.register(dreamA);
                dreamscape.register(dreamB);
                
                // 3. FANTASY: Let physics run - force interaction
                for (int i = 0; i < 20; i++) {
                    dreamscape.tick();
                    
                    // Check for collision (fusion event)
                    if (dreamA.isColliding(dreamB, 10.0)) {
                        fusionsAttempted++;
                        
                        // 4. JUDGMENT: Is this a nightmare or a breakthrough?
                        Epiphany insight = attemptFusion(memoryA, memoryB);
                        
                        if (insight != null) {
                            breakthroughsFound++;
                            dreamJournal.add(insight);
                            System.out.println("   ✨ EPIPHANY: " + insight.description);
                            
                            // Notify Ego (Cube 3) of discovery
                            brain.getNode(HyperTesseract.EGO, 4, 4, 4)
                                .pulse("DREAM_INSIGHT:" + insight.description, 5.0);
                        }
                        break;
                    }
                }
            }

            // REM speed: fast ticks (10ms)
            try { 
                Thread.sleep(10); 
            } catch (InterruptedException e) {
                isDreaming = false;
            }
            
            // Log progress every 1000 cycles
            if (dreamCycles % 1000 == 0) {
                System.out.printf("   💭 Dream cycle %d: %d recalls, %d fusions, %d insights%n",
                    dreamCycles, memoriesRecalled, fusionsAttempted, breakthroughsFound);
            }
        }
    }

    /**
     * Recall a random memory from Hippocampus
     */
    private Memory recallRandomMemory() {
        int attempts = 0;
        while (attempts < 20) {
            int x = rng.nextInt(8);
            int y = rng.nextInt(8);
            int z = rng.nextInt(8);
            
            HyperTesseract.Node node = brain.getNode(HyperTesseract.HIPPOCAMPUS, x, y, z);
            
            if (!node.reference.isEmpty()) {
                Object ref = node.reference.get(rng.nextInt(node.reference.size()));
                if (ref instanceof File) {
                    return new Memory((File) ref, x, y, z, node.logic);
                }
            }
            attempts++;
        }
        return null;
    }

    /**
     * Create a dream particle with high temperature
     */
    private PhiSuit<Memory> createDreamParticle(Memory mem) {
        PhiSuit<Memory> particle = new PhiSuit<>(mem, 
            rng.nextInt(100), rng.nextInt(100), rng.nextInt(100));
        
        particle.label = "DREAM_" + mem.file.getName();
        particle.amplitude = 10.0 + rng.nextDouble() * dreamTemperature;
        particle.heat = dreamTemperature;  // High heat = chaos
        
        // Random velocity for chaotic movement
        particle.vx = (rng.nextDouble() - 0.5) * 10;
        particle.vy = (rng.nextDouble() - 0.5) * 10;
        particle.vz = (rng.nextDouble() - 0.5) * 10;
        
        return particle;
    }

    /**
     * Attempt fusion of two memories - look for optimization patterns
     */
    private Epiphany attemptFusion(Memory a, Memory b) {
        String extA = getExtension(a.file.getName());
        String extB = getExtension(b.file.getName());
        
        // Pattern 1: Two Java files might share logic
        if (extA.equals("java") && extB.equals("java")) {
            // Check if they have similar type annotations
            boolean shareType = false;
            for (String logicA : a.logic) {
                for (String logicB : b.logic) {
                    if (logicA.equals(logicB) && logicA.startsWith("TYPE:")) {
                        shareType = true;
                        break;
                    }
                }
            }
            
            if (shareType && rng.nextDouble() > 0.90) {  // 10% breakthrough chance
                return new Epiphany(
                    "REFACTOR",
                    "Merge common logic from " + a.file.getName() + " and " + b.file.getName(),
                    a.file, b.file,
                    0.7 + rng.nextDouble() * 0.3  // Confidence 70-100%
                );
            }
        }
        
        // Pattern 2: Config + Code = better defaults
        if ((extA.equals("json") || extA.equals("yaml") || extA.equals("properties")) &&
            extB.equals("java")) {
            if (rng.nextDouble() > 0.95) {
                return new Epiphany(
                    "INLINE_CONFIG",
                    "Inline config from " + a.file.getName() + " into " + b.file.getName() + " for faster startup",
                    a.file, b.file,
                    0.6 + rng.nextDouble() * 0.3
                );
            }
        }
        
        // Pattern 3: Test + Implementation = missing coverage
        if ((a.file.getName().contains("Test") || b.file.getName().contains("Test")) &&
            !(a.file.getName().contains("Test") && b.file.getName().contains("Test"))) {
            if (rng.nextDouble() > 0.92) {
                File impl = a.file.getName().contains("Test") ? b.file : a.file;
                return new Epiphany(
                    "TEST_COVERAGE",
                    "Add edge case tests for " + impl.getName(),
                    a.file, b.file,
                    0.5 + rng.nextDouble() * 0.4
                );
            }
        }
        
        // Pattern 4: Similar names = potential duplication
        String nameA = a.file.getName().replaceAll("\\.[^.]+$", "").toLowerCase();
        String nameB = b.file.getName().replaceAll("\\.[^.]+$", "").toLowerCase();
        if (levenshteinDistance(nameA, nameB) < 4 && !nameA.equals(nameB)) {
            if (rng.nextDouble() > 0.85) {
                return new Epiphany(
                    "DEDUP",
                    "Investigate duplication between " + a.file.getName() + " and " + b.file.getName(),
                    a.file, b.file,
                    0.4 + rng.nextDouble() * 0.3
                );
            }
        }
        
        return null;  // No insight this time
    }

    /**
     * Report dream insights
     */
    private void reportDreams() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   📜 MORNING REPORT (DREAM JOURNAL)                ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.printf("║   Cycles: %-8d  Recalls: %-8d                ║%n", dreamCycles, memoriesRecalled);
        System.out.printf("║   Fusions: %-7d  Breakthroughs: %-5d             ║%n", fusionsAttempted, breakthroughsFound);
        System.out.println("╠════════════════════════════════════════════════════╣");
        
        if (dreamJournal.isEmpty()) {
            System.out.println("║   (No significant insights tonight.)              ║");
        } else {
            for (int i = 0; i < dreamJournal.size(); i++) {
                Epiphany e = dreamJournal.get(i);
                System.out.printf("║ %d. [%s] %.0f%% confidence                        %n", 
                    i + 1, e.type, e.confidence * 100);
                System.out.println("║    " + truncate(e.description, 48));
            }
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║   >> Would you like OpenClaw to implement these?  ║");
        }
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    // ========== PUBLIC API ==========

    public void setDreamTemperature(double temp) {
        this.dreamTemperature = temp;
    }

    public boolean isDreaming() {
        return isDreaming;
    }

    public List<Epiphany> getInsights() {
        return new ArrayList<>(dreamJournal);
    }

    public void clearJournal() {
        dreamJournal.clear();
    }

    public long getDreamCycles() {
        return dreamCycles;
    }

    // ========== HELPER CLASSES ==========

    public static class Memory {
        public final File file;
        public final int x, y, z;
        public final List<String> logic;
        
        public Memory(File file, int x, int y, int z, List<String> logic) {
            this.file = file;
            this.x = x;
            this.y = y;
            this.z = z;
            this.logic = new ArrayList<>(logic);
        }
    }

    public static class Epiphany {
        public final String type;
        public final String description;
        public final File fileA;
        public final File fileB;
        public final double confidence;
        public final long timestamp;
        
        public Epiphany(String type, String description, File fileA, File fileB, double confidence) {
            this.type = type;
            this.description = description;
            this.fileA = fileA;
            this.fileB = fileB;
            this.confidence = confidence;
            this.timestamp = System.currentTimeMillis();
        }
    }

    // ========== UTILITIES ==========

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i-1) == b.charAt(j-1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1), dp[i-1][j-1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}
