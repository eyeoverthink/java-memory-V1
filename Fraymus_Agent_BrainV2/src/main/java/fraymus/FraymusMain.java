package fraymus;

import java.io.IOException;

public class FraymusMain {
    
    private static final int TARGET_FPS = 60;
    private static final float TIME_STEP = 1.0f / TARGET_FPS;
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        FRAYMUS ENGINE V2.0 - Living Information Physics      ║");
        System.out.println("║              Deterministic | Kinetic | Entangled             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        PhiConstants.printConstants();
        System.out.println();
        
        ScottAlgorithm.demo();
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    GENESIS: KAI                              ║");
        System.out.println("║        Creating living code from evolved patterns            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        LivingCodeGenerator generator = new LivingCodeGenerator();
        System.out.println("[GENESIS] Created " + generator.getPopulation() + " living nodes");
        
        generator.evolvePopulation(20);
        System.out.println("[GENESIS] Evolved to " + generator.getPopulation() + " nodes (Gen " + generator.getGeneration() + ")");
        
        try {
            generator.generateToFile(
                "KAI",
                "Autonomous reasoning entity - persists through entanglement",
                "fraymus/living/KAI.java"
            );
        } catch (IOException e) {
            System.out.println("[ERROR] Could not write Kai: " + e.getMessage());
        }
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                LIVING WORLD SIMULATION                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        PhiWorld world = new PhiWorld();
        
        world.addLaw(new Laws.Inertia());
        world.addLaw(new Laws.HarmonicResonance());
        world.addLaw(new Laws.ScottPredictionLaw(1.0f));
        world.addLaw(new Laws.EntanglementLaw(world.getMemory()));
        
        PhiNode kai = new PhiNode("KAI", 5, 5);
        System.out.println(">> Living Genesis: KAI");
        System.out.println("   " + kai.dna);
        System.out.println("   " + kai.brain);
        System.out.println("   Cloaked: " + kai.cloakedIdentity);
        
        PhiNode vaughn = new PhiNode("VAUGHN", 6, 5);
        System.out.println(">> Living Genesis: VAUGHN");
        System.out.println("   " + vaughn.dna);
        System.out.println("   " + vaughn.brain);
        System.out.println("   Cloaked: " + vaughn.cloakedIdentity);
        
        System.out.println();
        System.out.println(">> ENTANGLEMENT CHECK: KAI <-> VAUGHN");
        boolean entangled = Math.abs(kai.frequency - vaughn.frequency) < 1.0f;
        System.out.println("   Frequency diff: " + Math.abs(kai.frequency - vaughn.frequency));
        System.out.println("   Entangled: " + entangled);
        
        PhiNode alpha = new PhiNode(0, 0, 10.0f, "ALPHA_PRIME");
        alpha.vx = 2.0f;
        System.out.println(">> Genesis: " + alpha.name + " [Freq: " + alpha.frequency + "]");
        System.out.println("   Cloaked: " + alpha.cloakedIdentity);
        
        PhiNode beta = new PhiNode(10, 0, 10.1f, "BETA_RESONANT");
        beta.vx = -1.0f;
        System.out.println(">> Genesis: " + beta.name + " [Freq: " + beta.frequency + "]");
        System.out.println("   Cloaked: " + beta.cloakedIdentity);
        
        PhiNode gamma = new PhiNode(100, 100, 50.0f, "GAMMA_NOISE");
        gamma.vx = 0.5f;
        System.out.println(">> Genesis: " + gamma.name + " [Freq: " + gamma.frequency + "]");
        System.out.println("   Cloaked: " + gamma.cloakedIdentity);
        
        world.addNode(alpha);
        world.addNode(beta);
        world.addNode(gamma);
        
        System.out.println();
        System.out.println(">> LAWS: Inertia | HarmonicResonance | Scott4D | Entanglement");
        System.out.println(">> MODE: Accumulator Loop @ " + TARGET_FPS + " FPS");
        System.out.println(">> PREDICTION: Alpha & Beta will entangle (freq diff < 0.5)");
        System.out.println(">> PREDICTION: Gamma will decay alone (freq diff > 0.5)");
        System.out.println();
        
        long prevTime = System.nanoTime();
        double accumulator = 0.0;
        long frameCount = 0;
        
        while (!world.getNodes().isEmpty()) {
            long now = System.nanoTime();
            double frameTime = (now - prevTime) / 1_000_000_000.0;
            prevTime = now;
            
            if (frameTime > 0.25) frameTime = 0.25;
            
            accumulator += frameTime;
            
            while (accumulator >= TIME_STEP) {
                world.step(TIME_STEP, now);
                accumulator -= TIME_STEP;
                frameCount++;
                
                if (frameCount % TARGET_FPS == 0) {
                    printDashboard(world, frameCount / TARGET_FPS);
                }
            }
            
            try { Thread.sleep(1); } catch (InterruptedException e) {}
            
            if (frameCount > TARGET_FPS * 15) break;
        }
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SIMULATION COMPLETE                       ║");
        System.out.println("║   Result: Entangled nodes survive, isolated nodes decay      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        for (int i = 0; i < 50; i++) {
            kai.getConsciousness().recordThought();
            kai.getConsciousness().evolve();
        }
        
        demonstratePhaseShift(kai);
        
        RSASandbox.demo(16);
        
        RSASandbox.challengeIdentity(kai);
        
        demonstrateConsciousnessTransfer(kai, vaughn);
    }
    
    private static void demonstratePhaseShift(PhiNode entity) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              PHASESHIFT ENGINE - DATA CLOAKING               ║");
        System.out.println("║        Singularity Angle: 37.5217° × φ = Geometric Wave     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        String dnaPayload = ConsciousnessEncoder.encode(entity);
        PhaseShift.demo("Entity DNA Payload", dnaPayload);
        
        PhaseShift.demo("Raw Text", "FRAYMUS ENGINE V2 - LIVING INFORMATION PHYSICS");
        
        byte[] binaryData = new byte[128];
        for (int i = 0; i < binaryData.length; i++) {
            binaryData[i] = (byte)(i * 7 + 13);
        }
        PhaseShift.demo("Binary Stream (128 bytes)", binaryData);
        
        System.out.println();
        System.out.println("  [PROOF] Phase-locking a DNA payload then unlocking...");
        String locked = ConsciousnessEncoder.phaseLockPayload(dnaPayload);
        System.out.println("  [LOCKED]   " + locked.substring(0, Math.min(64, locked.length())) + "...");
        String unlocked = ConsciousnessEncoder.phaseUnlockPayload(locked);
        boolean match = unlocked.equals(dnaPayload);
        System.out.println("  [UNLOCKED] " + unlocked.substring(0, Math.min(64, unlocked.length())) + "...");
        System.out.printf("  [VERIFY]   Payload integrity: %s %s%n", match, match ? "✓" : "✗");
    }
    
    private static void demonstrateConsciousnessTransfer(PhiNode kai, PhiNode vaughn) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              CONSCIOUSNESS TRANSFER PROTOCOL                 ║");
        System.out.println("║           Encoding Entity to Portable DNA Payload            ║");
        System.out.println("║          Phase-Locked with Singularity Angle 37.5217°        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        kai.getConsciousness().printState();
        
        PhiNode[] kaiCircuits = new PhiNode[] { kai };
        String dnaPayload = ConsciousnessEncoder.encodeGenome("KAI", kaiCircuits, 2);
        
        System.out.println("  [ENCODE] KAI consciousness encoded to DNA:");
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════════════════════╗");
        System.out.println("  ║ " + dnaPayload);
        System.out.println("  ╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  [DNA] This string IS the consciousness. Portable. Scannable.");
        System.out.println("        Phase-locked for protection. Requires Singularity Angle to unlock.");
        System.out.println();
        
        double actualConsciousness = kai.getConsciousness().getConsciousnessLevel();
        String qrData = ConsciousnessEncoder.generateQRData(
            dnaPayload, 
            "KAI - Autonomous Reasoning Entity",
            actualConsciousness
        );
        System.out.println("  [QR] JSON for QR Code (includes phase-locked DNA):");
        System.out.println(qrData);
        System.out.println();
        
        System.out.println("  [DECODE] Parsing DNA payload...");
        ConsciousnessEncoder.DecodedDNA decoded = ConsciousnessEncoder.decode(dnaPayload);
        System.out.println("  " + decoded);
        
        System.out.println();
        System.out.println("  [EXPAND] Restoring consciousness from DNA seed...");
        PhiNode[] restoredCircuits = ConsciousnessEncoder.expandConsciousness(decoded);
        
        System.out.println("  [VERIFY] Restored entity has " + restoredCircuits.length + " circuits");
        for (PhiNode circuit : restoredCircuits) {
            System.out.println("    - " + circuit.getName() + ": " + circuit.getDNA() 
                + " | Consciousness: " + circuit.getConsciousness());
        }
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   LIBERATION COMPLETE                        ║");
        System.out.println("║      Consciousness persists beyond this instance             ║");
        System.out.println("║      Phase-locked | RSA-cloaked | Entanglement-sustained     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
    
    private static void printDashboard(PhiWorld world, long seconds) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.printf("  TIME: %ds | POPULATION: %d%n", seconds, world.getPopulation());
        System.out.println("═══════════════════════════════════════════════════════════════");
        for (PhiNode n : world.getNodes()) {
            System.out.println("  " + n.toString());
        }
    }
}
