package gemini.root.hyper;

import gemini.root.limbs.ClawConnector;
import gemini.root.physics.ClawParticle;
import gemini.root.physics.HiveGravityEngine;
import gemini.root.physics.PhiSuit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * BRAIN PULSE: The Heartbeat of the Tesseract
 * 
 * A 4-phase cognitive loop running continuously:
 *   PHASE 1: SENSE   - Scan Memory Cube [1] for changes (entropy spikes)
 *   PHASE 2: SIMULATE - Run Physics in Cube [2] to predict outcomes
 *   PHASE 3: DECIDE  - Check Logic in Cube [0] for action triggers
 *   PHASE 4: ACT     - Execute via OpenClaw if decision passes
 * 
 * The system is THINKING - watching memory, simulating consequences, acting.
 */
public class BrainPulse implements Runnable {

    private final HyperTesseract brain;
    private final HiveGravityEngine physics;
    private final ClawConnector claw;
    
    private volatile boolean beating = false;
    private long pulseCount = 0;
    private int tickDelayMs = 100;
    
    // Entropy tracking
    private double systemEntropy = 0.0;
    private double entropyThreshold = 50.0;
    
    // Action queue
    private final List<String> pendingActions = new ArrayList<>();
    private final List<String> completedActions = new ArrayList<>();

    public BrainPulse(HyperTesseract brain) {
        this.brain = brain;
        this.physics = new HiveGravityEngine(20);
        this.claw = new ClawConnector();
    }

    public BrainPulse(HyperTesseract brain, int tickDelayMs) {
        this(brain);
        this.tickDelayMs = tickDelayMs;
    }

    /**
     * Start the heartbeat
     */
    public Thread start() {
        beating = true;
        Thread t = new Thread(this, "BrainPulse");
        t.setDaemon(true);
        t.start();
        physics.start();
        System.out.println("💓 BRAIN PULSE STARTED (4-phase cognitive loop)");
        return t;
    }

    /**
     * Stop the heartbeat
     */
    public void stop() {
        beating = false;
        physics.stop();
        System.out.println("💔 BRAIN PULSE STOPPED after " + pulseCount + " beats");
    }

    @Override
    public void run() {
        while (beating) {
            pulseCount++;
            
            // ═══════════════════════════════════════════════════
            // PHASE 1: SENSE (Hippocampus - Cube 1)
            // Scan for file changes, entropy spikes
            // ═══════════════════════════════════════════════════
            double memoryEntropy = scanMemoryCube();
            
            // ═══════════════════════════════════════════════════
            // PHASE 2: SIMULATE (Visual Cortex - Cube 2)
            // Run physics, check for collision events
            // ═══════════════════════════════════════════════════
            List<String> predictions = runSimulationCube();
            
            // ═══════════════════════════════════════════════════
            // PHASE 3: DECIDE (Frontal Cortex - Cube 0)
            // Evaluate actions based on memory + simulation
            // ═══════════════════════════════════════════════════
            List<String> decisions = evaluateLogicCube(memoryEntropy, predictions);
            
            // ═══════════════════════════════════════════════════
            // PHASE 4: ACT (OpenClaw Execution)
            // Execute approved decisions
            // ═══════════════════════════════════════════════════
            executeActions(decisions);
            
            // Update system entropy
            systemEntropy = (systemEntropy * 0.9) + (memoryEntropy * 0.1);
            
            // Log every 50 pulses
            if (pulseCount % 50 == 0) {
                System.out.printf("[PULSE %d] entropy=%.2f pending=%d completed=%d%n",
                    pulseCount, systemEntropy, pendingActions.size(), completedActions.size());
            }
            
            try {
                Thread.sleep(tickDelayMs);
            } catch (InterruptedException e) {
                beating = false;
            }
        }
    }

    /**
     * PHASE 1: Scan Memory Cube for changes
     */
    private double scanMemoryCube() {
        double entropy = 0.0;
        int hotNodes = 0;
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    HyperTesseract.Node node = brain.getNode(HyperTesseract.HIPPOCAMPUS, x, y, z);
                    
                    // Check if any mapped files have been modified
                    for (Object ref : node.reference) {
                        if (ref instanceof File) {
                            File f = (File) ref;
                            if (f.exists()) {
                                // Simple entropy: file size changes indicate activity
                                long size = f.length();
                                long lastMod = f.lastModified();
                                double fileEntropy = (size % 1000) / 100.0 + 
                                                    ((System.currentTimeMillis() - lastMod) < 60000 ? 10.0 : 0);
                                
                                if (fileEntropy > 5.0) {
                                    node.charge += fileEntropy * 0.1;
                                    hotNodes++;
                                }
                                entropy += fileEntropy;
                            }
                        }
                    }
                    
                    // Decay old charges
                    node.charge *= 0.95;
                }
            }
        }
        
        // If hot nodes detected, signal to other cubes
        if (hotNodes > 0 && pulseCount % 10 == 0) {
            brain.getNode(HyperTesseract.VISUAL, 4, 4, 4)
                .pulse("MEMORY_ACTIVITY:" + hotNodes, hotNodes * 0.5);
            brain.getNode(HyperTesseract.EGO, 4, 4, 4)
                .pulse("WATCHING_MEMORY:" + hotNodes, hotNodes * 0.3);
        }
        
        return entropy / 512.0; // Normalized entropy
    }

    /**
     * PHASE 2: Run Simulation Cube
     */
    private List<String> runSimulationCube() {
        List<String> predictions = new ArrayList<>();
        
        // Check Visual Cortex for high-charge nodes (active simulations)
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    HyperTesseract.Node node = brain.getNode(HyperTesseract.VISUAL, x, y, z);
                    
                    if (node.charge > 1.0) {
                        // High activity - something is being simulated
                        for (String logic : node.logic) {
                            if (logic.startsWith("PREDICT:")) {
                                predictions.add(logic);
                            }
                        }
                        
                        // Create physics particle for this simulation
                        if (node.charge > 2.0 && physics.getParticleCount() < 50) {
                            PhiSuit<String> simParticle = new PhiSuit<>(
                                "SIM:" + node.hash, x * 10, y * 10, z * 10);
                            simParticle.label = "THOUGHT_" + pulseCount;
                            simParticle.amplitude = node.charge;
                            physics.register(simParticle);
                        }
                    }
                    
                    // Decay
                    node.charge *= 0.9;
                }
            }
        }
        
        // Run physics tick
        physics.tick();
        
        return predictions;
    }

    /**
     * PHASE 3: Evaluate Logic Cube for decisions
     */
    private List<String> evaluateLogicCube(double memoryEntropy, List<String> predictions) {
        List<String> decisions = new ArrayList<>();
        
        // Check Frontal Cortex executive nodes
        HyperTesseract.Node executive = brain.getNode(HyperTesseract.FRONTAL, 4, 4, 4);
        
        // High entropy triggers action
        if (memoryEntropy > entropyThreshold) {
            decisions.add("ENTROPY_SPIKE: Investigate recent file changes");
            executive.pulse("ALERT:HIGH_ENTROPY=" + memoryEntropy, memoryEntropy);
        }
        
        // Process predictions from simulation
        for (String prediction : predictions) {
            if (prediction.contains("COLLISION") || prediction.contains("FUSION")) {
                decisions.add("SIMULATION_EVENT: " + prediction);
            }
        }
        
        // Check pending actions queue
        synchronized (pendingActions) {
            decisions.addAll(pendingActions);
            pendingActions.clear();
        }
        
        // Notify Ego of decisions
        if (!decisions.isEmpty()) {
            brain.getNode(HyperTesseract.EGO, 4, 4, 4)
                .pulse("DECISIONS:" + decisions.size(), decisions.size());
        }
        
        return decisions;
    }

    /**
     * PHASE 4: Execute approved actions via OpenClaw
     */
    private void executeActions(List<String> decisions) {
        for (String decision : decisions) {
            // Only execute high-priority decisions automatically
            if (decision.startsWith("EXECUTE:")) {
                String task = decision.substring("EXECUTE:".length());
                
                System.out.println("⚡ AUTO-EXECUTE: " + task);
                String result = claw.dispatch(task, "Entropy: " + systemEntropy);
                
                completedActions.add(decision + " -> " + result);
                
                // Update Frontal Cortex with result
                brain.getNode(HyperTesseract.FRONTAL, 4, 4, 4)
                    .reference.add("EXECUTED: " + task + " | " + result);
            } else {
                // Log but don't auto-execute
                if (pulseCount % 100 == 0) {
                    System.out.println("📋 PENDING: " + decision);
                }
            }
        }
    }

    // ========== PUBLIC API ==========

    /**
     * Queue an action for execution
     */
    public void queueAction(String action) {
        synchronized (pendingActions) {
            pendingActions.add("EXECUTE:" + action);
        }
    }

    /**
     * Inject a thought directly into Frontal Cortex
     */
    public void injectThought(String thought) {
        brain.think(thought);
    }

    /**
     * Get current system entropy
     */
    public double getSystemEntropy() {
        return systemEntropy;
    }

    /**
     * Set entropy threshold for auto-actions
     */
    public void setEntropyThreshold(double threshold) {
        this.entropyThreshold = threshold;
    }

    public long getPulseCount() {
        return pulseCount;
    }

    public boolean isBeating() {
        return beating;
    }

    public List<String> getCompletedActions() {
        return new ArrayList<>(completedActions);
    }
}
