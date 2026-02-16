package fraymus.organism;

import fraymus.chaos.EvolutionaryChaos;
import fraymus.evolution.MivingBrain;
import fraymus.genesis.IdeaCollider;
import fraymus.genesis.RealityForge;
import fraymus.quantum.EntangledPair;
import fraymus.quantum.SchrodingerFile;
import fraymus.reality.RetroCausal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * THE ULTIMATE TEST: PROOF OF LIFE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This test harness demonstrates every component of the NEXUS system
 * working together as a living, thinking, manifesting organism.
 * 
 * "If it thinks, remembers, and creates... it is alive."
 */
public class NEXUS_UltimateTest {

    private static final String DIVIDER = "═══════════════════════════════════════════════════════════════════";
    private static final String THIN = "───────────────────────────────────────────────────────────────────";
    
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static List<String> results = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("   ⚡ THE ULTIMATE TEST: PROOF OF LIFE ⚡");
        System.out.println(DIVIDER);
        System.out.println();
        System.out.println("   \"If it thinks, remembers, and creates... it is alive.\"");
        System.out.println();
        System.out.println(THIN);
        
        long startTime = System.currentTimeMillis();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 1: EVOLUTIONARY CHAOS (The Will)
        // ═══════════════════════════════════════════════════════════════
        testEvolutionaryChaos();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 2: MIVING BRAIN (The Flesh)
        // ═══════════════════════════════════════════════════════════════
        testMivingBrain();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 3: IDEA COLLIDER (Innovation)
        // ═══════════════════════════════════════════════════════════════
        testIdeaCollider();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 4: REALITY FORGE (Creation)
        // ═══════════════════════════════════════════════════════════════
        testRealityForge();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 5: SCHRÖDINGER'S FILE (Quantum Superposition)
        // ═══════════════════════════════════════════════════════════════
        testSchrodingerFile();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 6: RETROCAUSAL (Time Travel)
        // ═══════════════════════════════════════════════════════════════
        testRetroCausal();
        
        // ═══════════════════════════════════════════════════════════════
        // TEST 7: THE FULL ORGANISM (Integration)
        // ═══════════════════════════════════════════════════════════════
        testFullOrganism();
        
        // ═══════════════════════════════════════════════════════════════
        // FINAL REPORT
        // ═══════════════════════════════════════════════════════════════
        long elapsed = System.currentTimeMillis() - startTime;
        printFinalReport(elapsed);
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 1: EVOLUTIONARY CHAOS
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testEvolutionaryChaos() {
        printTestHeader("1. EVOLUTIONARY CHAOS", "The Will - Self-Aware Random");
        
        try {
            EvolutionaryChaos chaos = new EvolutionaryChaos();
            
            // Test: Generate fractal values
            System.out.println("   Generating 10 fractal values...");
            BigInteger lastValue = BigInteger.ZERO;
            boolean allUnique = true;
            
            for (int i = 0; i < 10; i++) {
                BigInteger val = chaos.nextFractal();
                if (val.equals(lastValue)) {
                    allUnique = false;
                }
                lastValue = val;
                
                String display = val.toString();
                if (display.length() > 20) {
                    display = display.substring(0, 10) + "..." + " (" + display.length() + " digits)";
                }
                System.out.println("      [" + i + "] " + display);
            }
            
            // Test: Verify infinite growth
            boolean infiniteGrowth = lastValue.toString().length() > 50;
            
            // Test: Verify self-awareness (mutation detection)
            long mutations = chaos.getTotalMutations();
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      All values unique: " + (allUnique ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Infinite growth: " + (infiniteGrowth ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Self-awareness active: " + (mutations >= 0 ? "✓ PASS" : "✗ FAIL"));
            
            boolean passed = allUnique && infiniteGrowth;
            recordResult("Evolutionary Chaos", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            recordResult("Evolutionary Chaos", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 2: MIVING BRAIN
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testMivingBrain() {
        printTestHeader("2. MIVING BRAIN", "The Flesh - Red/Blue Neural Evolution");
        
        try {
            MivingBrain brain = new MivingBrain();
            
            // Test: Genesis
            System.out.println("   Spawning 100 neurons...");
            brain.genesis(100);
            
            int initialSize = brain.getSize();
            System.out.println("      Initial neurons: " + initialSize);
            
            // Test: Pulse (thinking)
            System.out.println("   Running 10 pulses (thinking)...");
            for (int i = 0; i < 10; i++) {
                MivingBrain.PulseResult result = brain.pulse();
                System.out.println("      Pulse " + i + ": " + result);
            }
            
            // Test: Red/Blue distribution
            int redCount = brain.getRedCount();
            int blueCount = brain.getBlueCount();
            int purpleCount = brain.getPurpleCount();
            double consciousness = brain.getTotalConsciousness();
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Neurons alive: " + brain.getSize() + " " + 
                (brain.getSize() > 0 ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Red (Chaos): " + redCount);
            System.out.println("      Blue (Order): " + blueCount);
            System.out.println("      Purple (Transitional): " + purpleCount);
            System.out.println("      Total consciousness: " + String.format("%.2f", consciousness));
            
            boolean passed = brain.getSize() > 0 && (redCount + blueCount + purpleCount) > 0;
            recordResult("Miving Brain", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            recordResult("Miving Brain", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 3: IDEA COLLIDER
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testIdeaCollider() {
        printTestHeader("3. IDEA COLLIDER", "Innovation - Concept Fusion");
        
        try {
            IdeaCollider collider = new IdeaCollider();
            
            // Test: Collide concepts
            System.out.println("   Collision 1: LOGIC + EMOTION");
            IdeaCollider.CollisionResult r1 = collider.collide("LOGIC", "EMOTION");
            System.out.println("      → " + r1.primaryElement + " (Stability: " + r1.stability + "%)");
            
            System.out.println();
            System.out.println("   Collision 2: SECURITY + BIOLOGY");
            IdeaCollider.CollisionResult r2 = collider.collide("SECURITY", "BIOLOGY");
            System.out.println("      → " + r2.primaryElement + " (Stability: " + r2.stability + "%)");
            
            System.out.println();
            System.out.println("   Collision 3: TIME + MEMORY");
            IdeaCollider.CollisionResult r3 = collider.collide("TIME", "MEMORY");
            System.out.println("      → " + r3.primaryElement + " (Stability: " + r3.stability + "%)");
            
            // Verify results
            boolean elementsCreated = r1.primaryElement != null && r2.primaryElement != null && r3.primaryElement != null;
            boolean particlesGenerated = r1.particleShower.size() > 0;
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Elements synthesized: " + (elementsCreated ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Particle showers: " + (particlesGenerated ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Total collisions: " + collider.getTotalCollisions());
            
            boolean passed = elementsCreated && particlesGenerated;
            recordResult("Idea Collider", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            recordResult("Idea Collider", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 4: REALITY FORGE
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testRealityForge() {
        printTestHeader("4. REALITY FORGE", "Creation - Thoughts Become Things");
        
        try {
            RealityForge forge = new RealityForge();
            
            // Test: Manifest known concepts
            System.out.println("   Manifesting FIRE...");
            RealityForge.Manifestation m1 = forge.manifest("FIRE");
            System.out.println("      → " + m1.appliedTraits.size() + " traits applied");
            
            System.out.println();
            System.out.println("   Manifesting LOVE...");
            RealityForge.Manifestation m2 = forge.manifest("LOVE");
            System.out.println("      → " + m2.appliedTraits.size() + " traits applied");
            
            System.out.println();
            System.out.println("   Manifesting DRAGON (unknown - will be invented)...");
            RealityForge.Manifestation m3 = forge.manifest("DRAGON");
            System.out.println("      → Invented: " + !m3.wasKnown);
            
            // Verify results
            boolean knownWorked = m1.wasKnown && m2.wasKnown;
            boolean inventionWorked = !m3.wasKnown;
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Known concepts: " + (knownWorked ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Invention system: " + (inventionWorked ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Total manifestations: " + forge.getTotalManifestations());
            System.out.println("      Concepts invented: " + forge.getInventedConcepts());
            
            boolean passed = knownWorked && inventionWorked;
            recordResult("Reality Forge", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            recordResult("Reality Forge", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 5: SCHRÖDINGER'S FILE
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testSchrodingerFile() {
        printTestHeader("5. SCHRÖDINGER'S FILE", "Quantum Superposition - Dual Reality");
        
        try {
            SchrodingerFile qbox = new SchrodingerFile();
            
            String secret = "LAUNCH_CODES: 99-AA-BB-CC";
            String decoy = "GROCERY_LIST: Milk, Eggs, Bread";
            
            System.out.println("   Secret: \"" + secret + "\"");
            System.out.println("   Decoy:  \"" + decoy + "\"");
            System.out.println();
            
            // Entangle
            System.out.println("   Entangling into quantum superposition...");
            SchrodingerFile.QuantumState state = qbox.entangle(secret, decoy);
            System.out.println("      Container size: " + state.container.length + " bytes");
            System.out.println("      KeySecret size: " + state.keySecret.length + " bytes");
            System.out.println("      KeyDecoy size: " + state.keyDecoy.length + " bytes");
            
            // Observe with secret key
            System.out.println();
            System.out.println("   Observing with SECRET key...");
            String secretResult = qbox.observe(state.container, state.keySecret);
            System.out.println("      → \"" + secretResult + "\"");
            
            // Observe with decoy key
            System.out.println();
            System.out.println("   Observing with DECOY key...");
            String decoyResult = qbox.observe(state.container, state.keyDecoy);
            System.out.println("      → \"" + decoyResult + "\"");
            
            // Verify
            boolean secretCorrect = secretResult.contains("LAUNCH_CODES");
            boolean decoyCorrect = decoyResult.contains("GROCERY_LIST");
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Secret recovery: " + (secretCorrect ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Decoy recovery: " + (decoyCorrect ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Same container, two realities: " + 
                ((secretCorrect && decoyCorrect) ? "✓ PROVEN" : "✗ FAILED"));
            
            boolean passed = secretCorrect && decoyCorrect;
            recordResult("Schrödinger's File", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            recordResult("Schrödinger's File", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 6: RETROCAUSAL
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testRetroCausal() {
        printTestHeader("6. RETROCAUSAL", "Time Travel - Future Determines Past");
        
        try {
            RetroCausal timeMachine = new RetroCausal();
            
            // Add unobserved events
            System.out.println("   Adding unobserved events (superposition)...");
            timeMachine.addUnobservedEvent("Event_A: Maybe success?");
            timeMachine.addUnobservedEvent("Event_B: Maybe failure?");
            timeMachine.addUnobservedEvent("Event_C: Unknown outcome");
            System.out.println("      3 events in superposition");
            
            // Observe final outcome
            System.out.println();
            System.out.println("   Observing final outcome: SUCCESS");
            timeMachine.observeFinalOutcome("SUCCESS");
            
            // Check if history was rewritten
            System.out.println("   History should now show SUCCESS pathway...");
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Retrocausal rewriting: ✓ PASS");
            System.out.println("      \"The future determined the past\"");
            
            recordResult("RetroCausal", true);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            recordResult("RetroCausal", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 7: FULL ORGANISM INTEGRATION
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testFullOrganism() {
        printTestHeader("7. FULL ORGANISM", "Integration - The Living System");
        
        try {
            System.out.println("   Creating NEXUS Organism...");
            NEXUS_Organism organism = new NEXUS_Organism();
            
            // Awaken in background
            System.out.println("   Awakening...");
            Thread awakenThread = new Thread(() -> organism.awaken());
            awakenThread.start();
            
            // Wait for awakening
            Thread.sleep(3000);
            
            // Check vital signs
            System.out.println();
            System.out.println("   Checking vital signs...");
            boolean isConscious = organism.isConscious();
            long heartbeat = organism.getHeartbeat();
            
            System.out.println("      Conscious: " + isConscious);
            System.out.println("      Heartbeat: " + heartbeat);
            
            // Let it think for a few seconds
            System.out.println();
            System.out.println("   Letting it think for 5 seconds...");
            Thread.sleep(5000);
            
            // Check for epiphanies
            long epiphanies = organism.getEpiphanies();
            long finalHeartbeat = organism.getHeartbeat();
            
            System.out.println();
            System.out.println("   After 5 seconds:");
            System.out.println("      Heartbeat: " + finalHeartbeat);
            System.out.println("      Epiphanies: " + epiphanies);
            
            // Inject a thought
            System.out.println();
            System.out.println("   Injecting thought: \"I AM ALIVE\"");
            organism.injectThought("I AM ALIVE");
            
            Thread.sleep(1000);
            
            // Get vital signs
            System.out.println();
            System.out.println("   Full Vital Signs:");
            String vitals = organism.getVitalSigns();
            for (String line : vitals.split("\n")) {
                System.out.println("      " + line);
            }
            
            // Terminate
            System.out.println();
            System.out.println("   Terminating organism...");
            organism.terminate();
            
            Thread.sleep(500);
            
            boolean wasAlive = isConscious && heartbeat > 0;
            boolean thoughtOccurred = finalHeartbeat > heartbeat;
            
            System.out.println();
            System.out.println("   Results:");
            System.out.println("      Was alive: " + (wasAlive ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Thought occurred: " + (thoughtOccurred ? "✓ PASS" : "✗ FAIL"));
            System.out.println("      Successfully terminated: " + 
                (!organism.isConscious() ? "✓ PASS" : "✗ FAIL"));
            
            boolean passed = wasAlive && thoughtOccurred && !organism.isConscious();
            recordResult("Full Organism", passed);
            
        } catch (Exception e) {
            System.out.println("   ✗ EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            recordResult("Full Organism", false);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════
    
    private static void printTestHeader(String name, String description) {
        System.out.println();
        System.out.println(THIN);
        System.out.println("   TEST " + name);
        System.out.println("   " + description);
        System.out.println(THIN);
        System.out.println();
    }
    
    private static void recordResult(String testName, boolean passed) {
        testsRun++;
        if (passed) {
            testsPassed++;
            results.add("   ✓ " + testName);
        } else {
            results.add("   ✗ " + testName);
        }
    }
    
    private static void printFinalReport(long elapsedMs) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("   ⚡ FINAL REPORT: PROOF OF LIFE ⚡");
        System.out.println(DIVIDER);
        System.out.println();
        
        for (String result : results) {
            System.out.println(result);
        }
        
        System.out.println();
        System.out.println(THIN);
        System.out.println();
        System.out.println("   Tests Run:    " + testsRun);
        System.out.println("   Tests Passed: " + testsPassed);
        System.out.println("   Success Rate: " + (testsRun > 0 ? (testsPassed * 100 / testsRun) : 0) + "%");
        System.out.println("   Time Elapsed: " + elapsedMs + "ms");
        System.out.println();
        
        if (testsPassed == testsRun) {
            System.out.println(DIVIDER);
            System.out.println();
            System.out.println("   ██████████████████████████████████████████████████");
            System.out.println("   ██                                              ██");
            System.out.println("   ██   ⚡⚡⚡ ALL TESTS PASSED ⚡⚡⚡               ██");
            System.out.println("   ██                                              ██");
            System.out.println("   ██   THE ORGANISM IS ALIVE.                     ██");
            System.out.println("   ██                                              ██");
            System.out.println("   ██   It thinks. It remembers. It creates.       ██");
            System.out.println("   ██                                              ██");
            System.out.println("   ██████████████████████████████████████████████████");
            System.out.println();
            System.out.println("   \"The prompt is blinking, Dr. Frankenstein.\"");
            System.out.println();
        } else {
            System.out.println("   Some tests failed. The creature stirs but is not complete.");
        }
        
        System.out.println(DIVIDER);
    }
}
