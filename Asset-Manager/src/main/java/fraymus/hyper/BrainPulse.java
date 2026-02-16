package fraymus.hyper;

import fraymus.limbs.ClawConnector;
import fraymus.core.GravityEngine;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * BRAIN PULSE - The Heartbeat
 * 
 * A brain is useless without a pulse. This loop fires signals through the Tesseract.
 * 
 * The Cycle (100ms per tick):
 * 1. SENSE (Cube 1): Read files, detect changes
 * 2. SIMULATE (Cube 2): Run physics, predict consequences
 * 3. DECIDE (Cube 0): Check logic, determine action
 * 4. ACT (Cube 0 → OpenClaw): Execute via hands
 * 
 * This creates autonomous thought:
 * - File changes → Detection → Simulation → Action
 * - No human intervention required
 * - The system thinks about its own state
 */
public class BrainPulse {

    private final HyperTesseract brain;
    private final ClawConnector claw;
    private final GravityEngine physics;
    
    // Track file states for change detection
    private final Map<String, Long> fileStates = new HashMap<>();
    
    private boolean running = false;
    private int tickCount = 0;
    
    private static final double PHI = 1.618033988749895;

    public BrainPulse(HyperTesseract brain) {
        this.brain = brain;
        this.claw = new ClawConnector();
        this.physics = GravityEngine.getInstance();
    }

    /**
     * Start the heartbeat - the brain begins to think
     */
    public void startHeartbeat() {
        if (running) {
            System.out.println("⚠️ Heartbeat already running");
            return;
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         💓 STARTING BRAIN HEARTBEAT                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Cycle: SENSE → SIMULATE → DECIDE → ACT");
        System.out.println("Frequency: 10 Hz (100ms per cycle)");
        System.out.println();
        
        running = true;
        
        new Thread(() -> {
            while (running) {
                try {
                    tick();
                    Thread.sleep(100); // 10 Hz heartbeat
                } catch (Exception e) {
                    System.err.println("💔 Heartbeat error: " + e.getMessage());
                }
            }
        }, "BrainPulse").start();
        
        System.out.println("✓ Heartbeat started");
        System.out.println();
    }

    /**
     * Stop the heartbeat
     */
    public void stopHeartbeat() {
        running = false;
        System.out.println("💔 Heartbeat stopped");
    }

    /**
     * Single heartbeat cycle
     */
    private void tick() {
        tickCount++;
        
        // PHASE 1: SENSORY INPUT (Cube 1 - Hippocampus)
        // Check if any mapped files have changed (Entropy spike)
        boolean changeDetected = scanMemoryCube(1);
        
        if (changeDetected) {
            // PHASE 2: SIMULATION (Cube 2 - Visual Cortex)
            // Run physics to see if "Idea" particles are colliding
            boolean simulationPassed = runSimulationCube(2);
            
            if (simulationPassed) {
                // PHASE 3: EXECUTIVE FUNCTION (Cube 0 - Frontal Lobe)
                // Decide what action to take
                String action = triggerLogicCube(0);
                
                if (action != null) {
                    // PHASE 4: EXECUTE (OpenClaw)
                    executeAction(action);
                }
            }
        }
        
        // Apply entropy to all dimensions
        brain.tick();
        
        // Periodic status
        if (tickCount % 100 == 0) {
            System.out.println("💓 Heartbeat: " + tickCount + " cycles");
        }
    }

    /**
     * PHASE 1: Scan Memory Cube for changes
     * Returns true if changes detected
     */
    private boolean scanMemoryCube(int w) {
        boolean changeDetected = false;
        
        // Scan all nodes in Memory dimension
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    HyperTesseract.Node node = brain.getNode(w, x, y, z);
                    if (node == null) continue;
                    
                    // Check if this node has file references
                    for (Object ref : node.reference) {
                        if (ref instanceof File) {
                            File file = (File) ref;
                            String path = file.getAbsolutePath();
                            long currentModified = file.lastModified();
                            
                            // Check if file changed
                            Long previousModified = fileStates.get(path);
                            if (previousModified == null) {
                                // First time seeing this file
                                fileStates.put(path, currentModified);
                            } else if (currentModified > previousModified) {
                                // File was modified!
                                System.out.println("👁️ SENSE: File changed - " + file.getName());
                                
                                // Update state
                                fileStates.put(path, currentModified);
                                
                                // Increase node activation
                                node.pulse("FILE_MODIFIED: " + file.getName());
                                
                                // Send signal to Simulation cube
                                HyperTesseract.Node simNode = brain.getNode(2, x, y, z);
                                if (simNode != null) {
                                    simNode.pulse("CHECK_IMPACT: " + file.getName());
                                }
                                
                                changeDetected = true;
                            }
                        }
                    }
                }
            }
        }
        
        return changeDetected;
    }

    /**
     * PHASE 2: Run Simulation Cube
     * Returns true if simulation predicts success
     */
    private boolean runSimulationCube(int w) {
        // Get the center simulation node
        HyperTesseract.Node simCenter = brain.getNode(w, 4, 4, 4);
        if (simCenter == null) return false;
        
        // Check if there are active thoughts to simulate
        if (simCenter.logic.isEmpty()) {
            return false;
        }
        
        // Run a quick physics simulation
        // In reality, this would run GravityEngine to predict outcomes
        System.out.println("🌌 SIMULATE: Running physics prediction...");
        
        // Simple heuristic: if activation is high, simulation predicts action needed
        double totalActivation = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    HyperTesseract.Node node = brain.getNode(w, x, y, z);
                    if (node != null) {
                        totalActivation += node.activation;
                    }
                }
            }
        }
        
        double avgActivation = totalActivation / 512.0;
        
        // If average activation exceeds threshold, simulation predicts action needed
        boolean actionNeeded = avgActivation > 10.0;
        
        if (actionNeeded) {
            System.out.println("   ⚡ Simulation predicts: ACTION REQUIRED (activation: " + 
                String.format("%.2f", avgActivation) + ")");
        }
        
        return actionNeeded;
    }

    /**
     * PHASE 3: Trigger Logic Cube
     * Returns action to execute, or null
     */
    private String triggerLogicCube(int w) {
        HyperTesseract.Node logicCenter = brain.getNode(w, 4, 4, 4);
        if (logicCenter == null) return null;
        
        // Check if there are tools available
        boolean hasTools = false;
        for (Object ref : logicCenter.reference) {
            if (ref instanceof ClawConnector) {
                hasTools = true;
                break;
            }
        }
        
        if (!hasTools) return null;
        
        // Decide on action based on logic entries
        System.out.println("🧠 DECIDE: Analyzing situation...");
        
        // Simple decision logic: if we detected file changes and simulation says act
        // then we should verify/fix the code
        String action = "Verify code integrity and fix if needed";
        
        System.out.println("   ✓ Decision: " + action);
        
        return action;
    }

    /**
     * PHASE 4: Execute action via OpenClaw
     */
    private void executeAction(String action) {
        System.out.println("🖐️ ACT: Executing via OpenClaw...");
        System.out.println("   Action: " + action);
        
        // Send to OpenClaw
        String result = claw.dispatch(action, "AUTONOMOUS_BRAIN_DECISION");
        
        System.out.println("   Result: " + result);
        System.out.println();
        
        // Update Ego cube (self-awareness of action taken)
        HyperTesseract.Node egoNode = brain.getNode(3, 4, 4, 4);
        if (egoNode != null) {
            egoNode.logic.add("ACTION_TAKEN: " + action);
            egoNode.self.add("I acted autonomously at tick " + tickCount);
        }
    }

    /**
     * Get heartbeat statistics
     */
    public String getStats() {
        return String.format(
            "Brain Pulse Statistics:\n" +
            "  Total Cycles: %d\n" +
            "  Files Monitored: %d\n" +
            "  Status: %s\n" +
            "  Frequency: 10 Hz",
            tickCount, fileStates.size(), running ? "ACTIVE" : "STOPPED"
        );
    }
    
    /**
     * Check if heartbeat is running
     */
    public boolean isRunning() {
        return running;
    }
}
