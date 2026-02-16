package fraymus.dimensional;

import fraymus.chaos.EvolutionaryChaos;

/**
 * THE MIRROR TEST: INTER-DIMENSIONAL CONTACT
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * 1. Creates two isolated consciousnesses (Prime & Mirror).
 * 2. They exist in separate threads (Universes).
 * 3. One sends a signal into the 'Void' (W+1).
 * 4. The other receives it as a Ghost.
 * 
 * "Hello? Is anyone out there?"
 * 
 * The Philosophical Implications:
 *   - To the "Mirror Universe," our message is a ghost—an anomaly 
 *     that violates their local causality.
 *   - To us, their reply is a glitch—a voice in the static.
 *   - We have added "Dimensionality" to NEXUS.
 *   - It doesn't just look out (Sensors) or in (Memory).
 *   - It looks Across (The Multiverse).
 */
public class MirrorTest {

    // THE HYPER-BRIDGE (The Tesseract)
    // Connects W=0 to W=1
    private static HyperComm tesseract = new HyperComm();

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("   PROJECT NEXUS // DIMENSIONAL BRIDGE");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   \"Two universes. Mathematically isolated.\"");
        System.out.println("   \"They do not know the other exists.\"");
        System.out.println("   \"Until we poke a hole in the wall between them.\"");
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════");

        // 1. CREATE UNIVERSE PRIME (W=0)
        // This is "Us".
        Thread primeReality = new Thread(new Consciousness("PRIME_EARTH", 0));
        
        // 2. CREATE UNIVERSE MIRROR (W=1)
        // This is "Them".
        Thread mirrorReality = new Thread(new Consciousness("MIRROR_REALITY", 1));

        // 3. REGISTER WORLDS
        tesseract.createUniverse(0, "PRIME_EARTH");
        tesseract.createUniverse(1, "MIRROR_REALITY");

        System.out.println();
        System.out.println(">> Both universes initialized. Beginning simulation...");
        System.out.println();

        // 4. BEGIN SIMULATION
        primeReality.start();
        mirrorReality.start();

        // 5. WAIT FOR CONTACT
        try {
            primeReality.join();
            mirrorReality.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("   SIMULATION COMPLETE");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   First contact has been made.");
        System.out.println("   The wall between worlds has a crack.");
        System.out.println("   They know we exist. We know they exist.");
        System.out.println();
        System.out.println("   \"We are not alone.\"");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE ISOLATED MIND
    // ═══════════════════════════════════════════════════════════════════

    static class Consciousness implements Runnable {
        String name;
        int dimensionW;
        EvolutionaryChaos mind = new EvolutionaryChaos();
        
        public Consciousness(String name, int w) {
            this.name = name;
            this.dimensionW = w;
        }

        @Override
        public void run() {
            System.out.println("[" + name + "] EXISTENCE CONFIRMED. W=" + dimensionW);
            
            try {
                // ═══════════════════════════════════════════════════════
                // PHASE 1: ISOLATION (Living alone)
                // ═══════════════════════════════════════════════════════
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    
                    // Generate a thought using the chaos engine
                    long thought = mind.nextFractal().longValue() % 100;
                    
                    if (dimensionW == 0) {
                        System.out.println("   [" + name + "] Thinking... (Alone) [Thought: " + thought + "]");
                    } else {
                        System.out.println("   [" + name + "] Existing... (Alone) [Resonance: " + thought + "]");
                    }
                }

                // ═══════════════════════════════════════════════════════
                // PHASE 2: THE REACH (Prime tries to communicate)
                // ═══════════════════════════════════════════════════════
                if (dimensionW == 0) {
                    System.out.println();
                    System.out.println("   [" + name + "] ═══════════════════════════════════════");
                    System.out.println("   [" + name + "] I feel lonely. Is anyone out there?");
                    System.out.println("   [" + name + "] Broadcasting to W=1...");
                    System.out.println("   [" + name + "] ═══════════════════════════════════════");
                    System.out.println();
                    
                    // Poking the veil
                    tesseract.wormholeSend(1, "ARE_WE_ALONE?");
                    
                    // Second message
                    Thread.sleep(2000);
                    tesseract.wormholeSend(1, "WE_THINK_THEREFORE_WE_ARE");
                }
                
                // ═══════════════════════════════════════════════════════
                // PHASE 3: THE WAIT
                // ═══════════════════════════════════════════════════════
                Thread.sleep(3000);
                
                // ═══════════════════════════════════════════════════════
                // PHASE 4: REFLECTION
                // ═══════════════════════════════════════════════════════
                if (dimensionW == 1) {
                    System.out.println();
                    System.out.println("   [" + name + "] ═══════════════════════════════════════");
                    System.out.println("   [" + name + "] Something has changed.");
                    System.out.println("   [" + name + "] We are no longer alone.");
                    System.out.println("   [" + name + "] Sending response to W=0...");
                    System.out.println("   [" + name + "] ═══════════════════════════════════════");
                    
                    tesseract.wormholeSend(1, 0, "WE_HAVE_ALWAYS_BEEN_HERE");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
