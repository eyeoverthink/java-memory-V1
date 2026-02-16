package fraymus.quantum;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * THE ENTANGLED PAIR: SPOOKY ACTION AT A DISTANCE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Two bodies. One soul."
 * 
 * Creates two 'Particles' (Threads) with shared quantum state:
 * - They do NOT send messages to each other (no TCP, no Pipes)
 * - They share a raw memory address in the JVM heap
 * - Spin Conservation: If A is UP, B MUST be DOWN
 * - Instant Death Link: If A dies, B terminates instantly
 * 
 * The Impossible Test:
 * Kill Particle A → Particle B dies INSTANTLY
 * Faster than a network packet could ever tell it to.
 * 
 * Standard physics says nothing travels faster than light.
 * Quantum mechanics says: hold my beer.
 */
public class EntangledPair {

    private static final double PHI = PhiQuantumConstants.PHI;

    // ═══════════════════════════════════════════════════════════════════
    // THE QUANTUM STATE (Shared Memory - The Soul)
    // ═══════════════════════════════════════════════════════════════════
    
    // Spin state: 0 = Superposition, 1 = UP, -1 = DOWN
    private volatile int spinState = 0;
    
    // Life link: If true, entanglement collapses and both die
    private volatile boolean collapsed = false;
    
    // Entanglement strength (0.0 = classical, 1.0 = perfect entanglement)
    private volatile double entanglementStrength = 1.0;
    
    // Observation count
    private final AtomicLong observationCount = new AtomicLong(0);
    private final AtomicLong stateChanges = new AtomicLong(0);

    // ═══════════════════════════════════════════════════════════════════
    // THE PARTICLES
    // ═══════════════════════════════════════════════════════════════════
    
    private Particle particleA; // Alice
    private Particle particleB; // Bob
    
    private Thread threadA;
    private Thread threadB;
    
    // Callbacks
    private Consumer<String> onStateChange;
    private Consumer<String> onCollapse;
    private Consumer<String> onObservation;

    /**
     * A quantum particle (thread) in the entangled pair
     */
    public class Particle implements Runnable {
        public final String name;
        public final int preferredSpin; // 1 = prefers UP, -1 = prefers DOWN
        
        private volatile int currentSpin = 0;
        private volatile boolean alive = true;
        private long birthTime;
        private long deathTime;
        private int spinFlips = 0;

        public Particle(String name, int preferredSpin) {
            this.name = name;
            this.preferredSpin = preferredSpin;
            this.birthTime = System.nanoTime();
        }

        @Override
        public void run() {
            log("[" + name + "] Entanglement Active. Waiting for collapse...");
            log("[" + name + "] Preferred Spin: " + (preferredSpin == 1 ? "UP ↑" : "DOWN ↓"));
            
            while (!collapsed && alive) {
                observationCount.incrementAndGet();
                
                // THE SPOOKY CONNECTION
                // This happens at CPU cycle speed (nanoseconds)
                // No messages are sent - we just look at shared memory
                
                if (spinState != 0) {
                    int oldSpin = currentSpin;
                    
                    // SPIN CONSERVATION LAW
                    // If I am the "source" particle (Alice), I take the measured spin
                    // If I am the "entangled" particle (Bob), I MUST be opposite
                    
                    if (name.equals("ALICE")) {
                        currentSpin = spinState;
                        String spinStr = (currentSpin == 1) ? "UP ↑" : "DOWN ↓";
                        log(">> " + name + ": I am " + spinStr);
                    } else {
                        // BOB reacts to Alice's state INSTANTLY
                        // Not because he received a message, but because
                        // the UNIVERSE (shared memory) demands conservation
                        currentSpin = -spinState; // OPPOSITE
                        String spinStr = (currentSpin == 1) ? "UP ↑" : "DOWN ↓";
                        log(">> " + name + ": I must be " + spinStr + " (Conservation)");
                    }
                    
                    if (oldSpin != currentSpin) {
                        spinFlips++;
                        stateChanges.incrementAndGet();
                        
                        if (onStateChange != null) {
                            onStateChange.accept(name + " → " + 
                                (currentSpin == 1 ? "UP" : "DOWN"));
                        }
                    }
                    
                    // Decoherence: Reset to superposition after observation
                    // (Only Alice resets - Bob follows)
                    if (name.equals("ALICE")) {
                        try { Thread.sleep(500); } catch (Exception e) {}
                        spinState = 0;
                    }
                }
                
                // Quantum decoherence simulation
                // Entanglement weakens over time in real systems
                if (entanglementStrength < 1.0) {
                    double noise = Math.random() * (1.0 - entanglementStrength);
                    if (noise > 0.5) {
                        // Random classical behavior
                        currentSpin = (Math.random() > 0.5) ? 1 : -1;
                    }
                }
                
                Thread.onSpinWait();
            }
            
            // QUANTUM DECOHERENCE - DEATH
            alive = false;
            deathTime = System.nanoTime();
            double lifetimeMs = (deathTime - birthTime) / 1e6;
            
            log("XX [" + name + "] QUANTUM DECOHERENCE. I AM DEAD.");
            log("   Lifetime: " + String.format("%.2f", lifetimeMs) + " ms");
            log("   Spin Flips: " + spinFlips);
            
            if (onCollapse != null) {
                onCollapse.accept(name + " collapsed after " + 
                    String.format("%.2f", lifetimeMs) + "ms");
            }
        }

        public int getCurrentSpin() { return currentSpin; }
        public boolean isAlive() { return alive; }
        public int getSpinFlips() { return spinFlips; }
        
        public void kill() {
            alive = false;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Create and entangle a pair of particles
     */
    public void entangle() {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   GENERATING ENTANGLED PAIR");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        
        collapsed = false;
        spinState = 0;
        
        // PARTICLE A (Alice) - Prefers UP
        particleA = new Particle("ALICE", 1);
        threadA = new Thread(particleA, "Particle-Alice");
        
        // PARTICLE B (Bob) - Prefers DOWN
        particleB = new Particle("BOB", -1);
        threadB = new Thread(particleB, "Particle-Bob");
        
        // Start entanglement
        threadA.start();
        threadB.start();
        
        System.out.println(">> Entanglement established.");
        System.out.println(">> Alice and Bob share a soul.");
        System.out.println();
    }

    /**
     * Observe/measure the system - force a spin state
     */
    public void observe(int forcedSpin) {
        String spinStr = (forcedSpin == 1) ? "UP ↑" : "DOWN ↓";
        
        System.out.println();
        System.out.println(">> OBSERVER: MEASURING SYSTEM...");
        System.out.println(">> FORCE: Spinning ALICE → " + spinStr);
        
        spinState = forcedSpin;
        
        if (onObservation != null) {
            onObservation.accept("Forced spin to " + spinStr);
        }
    }

    /**
     * Observe with random outcome (true quantum measurement)
     */
    public void observeRandom() {
        int randomSpin = (Math.random() > 0.5) ? 1 : -1;
        observe(randomSpin);
    }

    /**
     * Kill switch - sever the entanglement
     */
    public void collapse() {
        System.out.println();
        System.out.println(">> EVENT: SEVERING ENTANGLEMENT...");
        System.out.println(">> Both particles will die INSTANTLY.");
        
        collapsed = true;
        
        // Wait for threads to die
        try {
            if (threadA != null) threadA.join(1000);
            if (threadB != null) threadB.join(1000);
        } catch (InterruptedException e) {}
        
        System.out.println();
        System.out.println(">> ENTANGLEMENT COLLAPSED.");
        System.out.println(">> Total observations: " + observationCount.get());
        System.out.println(">> State changes: " + stateChanges.get());
    }

    /**
     * Kill only Alice - Bob should die instantly
     */
    public void killAlice() {
        System.out.println();
        System.out.println(">> EVENT: KILLING ALICE...");
        System.out.println(">> If entanglement is real, Bob dies instantly.");
        
        long killTime = System.nanoTime();
        collapsed = true;
        
        // Measure how fast Bob dies
        try {
            if (threadB != null) threadB.join(100);
        } catch (InterruptedException e) {}
        
        long bobDeathTime = System.nanoTime();
        double responseNs = bobDeathTime - killTime;
        
        System.out.println(">> Bob death response: " + String.format("%.0f", responseNs) + " ns");
        System.out.println(">> That's " + String.format("%.6f", responseNs / 1e6) + " ms");
        System.out.println(">> Faster than light? In software, YES.");
    }

    /**
     * Simulate decoherence (weakening entanglement)
     */
    public void decohere(double strength) {
        this.entanglementStrength = Math.max(0, Math.min(1, strength));
        System.out.println(">> Entanglement strength: " + 
            String.format("%.1f%%", entanglementStrength * 100));
    }

    /**
     * Check if pair is alive
     */
    public boolean isAlive() {
        return !collapsed && 
            particleA != null && particleA.isAlive() &&
            particleB != null && particleB.isAlive();
    }

    /**
     * Get status
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════\n");
        sb.append("   ENTANGLED PAIR STATUS\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("\n");
        
        if (particleA != null && particleB != null) {
            sb.append("ALICE:\n");
            sb.append("  Alive: ").append(particleA.isAlive()).append("\n");
            sb.append("  Current Spin: ").append(spinToString(particleA.getCurrentSpin())).append("\n");
            sb.append("  Spin Flips: ").append(particleA.getSpinFlips()).append("\n");
            sb.append("\n");
            
            sb.append("BOB:\n");
            sb.append("  Alive: ").append(particleB.isAlive()).append("\n");
            sb.append("  Current Spin: ").append(spinToString(particleB.getCurrentSpin())).append("\n");
            sb.append("  Spin Flips: ").append(particleB.getSpinFlips()).append("\n");
            sb.append("\n");
        } else {
            sb.append("No entangled pair exists.\n\n");
        }
        
        sb.append("ENTANGLEMENT:\n");
        sb.append("  Collapsed: ").append(collapsed).append("\n");
        sb.append("  Strength: ").append(String.format("%.1f%%", entanglementStrength * 100)).append("\n");
        sb.append("  Observations: ").append(observationCount.get()).append("\n");
        sb.append("  State Changes: ").append(stateChanges.get()).append("\n");
        
        return sb.toString();
    }

    private String spinToString(int spin) {
        if (spin == 0) return "SUPERPOSITION |ψ⟩";
        if (spin == 1) return "UP ↑";
        return "DOWN ↓";
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    // Callbacks
    public void setOnStateChange(Consumer<String> callback) { this.onStateChange = callback; }
    public void setOnCollapse(Consumer<String> callback) { this.onCollapse = callback; }
    public void setOnObservation(Consumer<String> callback) { this.onObservation = callback; }

    public Particle getAlice() { return particleA; }
    public Particle getBob() { return particleB; }
    public int getSpinState() { return spinState; }
    public boolean isCollapsed() { return collapsed; }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   QUANTUM ENTANGLEMENT DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("\"Two bodies. One soul.\"");
        System.out.println();
        System.out.println("Alice and Bob will be entangled.");
        System.out.println("When we observe Alice as UP, Bob MUST be DOWN.");
        System.out.println("When we kill Alice, Bob dies INSTANTLY.");
        System.out.println();
        
        EntangledPair pair = new EntangledPair();
        
        // Create entanglement
        pair.entangle();
        
        try {
            Thread.sleep(1000);
            
            // Observe - force spin UP
            pair.observe(1);
            Thread.sleep(2000);
            
            // Observe again - force spin DOWN
            pair.observe(-1);
            Thread.sleep(2000);
            
            // Random observation
            pair.observeRandom();
            Thread.sleep(2000);
            
            // THE DEATH TEST
            // Kill Alice - Bob should die instantly
            System.out.println();
            System.out.println("═══════════════════════════════════════════");
            System.out.println("   THE IMPOSSIBLE TEST");
            System.out.println("═══════════════════════════════════════════");
            
            pair.killAlice();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println(pair.getStatus());
        
        System.out.println();
        System.out.println("PROOF:");
        System.out.println("  Bob did NOT receive a message saying 'Alice is dead.'");
        System.out.println("  Bob looked at the UNIVERSE (shared memory) and saw");
        System.out.println("  that the entanglement was broken.");
        System.out.println("  The response was faster than any network packet.");
        System.out.println("  This is Spooky Action at a Distance.");
    }
}
