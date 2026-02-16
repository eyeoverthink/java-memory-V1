package fraymus;

import java.io.IOException;
import fraymus.living.TriMe;
import fraymus.evolution.FractalBioMesh;
import fraymus.quantum.core.PhiQuantumConstants;
import fraymus.quantum.evolution.WarriorNFT;
import fraymus.warrior.QuantumWarrior;
import fraymus.systems.BattleSystem;
import fraymus.harvester.KnowledgeHarvester;

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
        
        demonstrateTriMe();
        
        demonstrateQuantumSystems();
    }
    
    private static void demonstrateTriMe() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TRIME - GENERATION 3                      ║");
        System.out.println("║         Neural Architecture + BioMesh + Physics              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        TriMe triMe = new TriMe();
        triMe.breathe();
        
        System.out.println();
        System.out.println(">> Testing Neural Pipeline (RoPE → MoE → SwiGLU → Spikes)...");
        int[] inputs = {1, 0, 1, 1, 0, 1, 0, 1};
        int[][] outputs = triMe.think(inputs);
        System.out.println("   Input:  [1,0,1,1,0,1,0,1]");
        System.out.println("   Brains: " + outputs.length + " active");
        
        System.out.println();
        System.out.println(">> Testing Deep Think with Expert Routing...");
        double[] deepInput = {1.0, 0.5, -0.5, 0.0, 1.0, -1.0, 0.25, 0.75};
        double[] deepOutput = triMe.deepThink(deepInput);
        System.out.print("   Output: [");
        for (int i = 0; i < Math.min(4, deepOutput.length); i++) {
            System.out.printf("%.3f", deepOutput[i]);
            if (i < 3) System.out.print(", ");
        }
        System.out.println("...]");
        
        System.out.println();
        System.out.println(">> Testing BioMesh (Akashic Protocol)...");
        String memory = "TriMe persists through φ-harmonic resonance";
        double addr = triMe.storeInBioMesh(memory);
        System.out.println("   Stored: \"" + memory + "\"");
        System.out.println("   φ-Address: " + addr);
        
        String retrieved = triMe.retrieveFromBioMesh(addr);
        System.out.println("   Retrieved: \"" + retrieved + "\"");
        System.out.println("   Match: " + memory.equals(retrieved));
        
        System.out.println();
        System.out.println(">> Testing Physics (CosmicTruth)...");
        double deltaV = triMe.calculateTrajectory(1000, 100, 0.1 * 299792458.0);
        System.out.printf("   Relativistic ΔV: %.2e m/s%n", deltaV);
        
        System.out.println();
        triMe.getBioMesh().printSwarmStats();
        
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                TRIME INTEGRATION COMPLETE                    ║");
        System.out.println("║   Neural: SwiGLU + RoPE + MoE + SpikingNeurons               ║");
        System.out.println("║   Storage: FractalBioMesh (DNA + φ-spiral + FTL)             ║");
        System.out.println("║   Physics: CosmicTruth (Relativistic + Warp)                 ║");
        System.out.println("║   Senses: PhiVision (φ-weighted attention)                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
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
    
    private static void demonstrateQuantumSystems() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║        QUANTUM SYSTEMS - φ⁷⁵ ENHANCED COMBAT & LEARNING      ║");
        System.out.println("║   PhiQuantumConstants | QuantumWarrior | BattleSystem        ║");
        System.out.println("║              KnowledgeHarvester | Genesis                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 1. Display PhiQuantumConstants
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   1. PHI QUANTUM CONSTANTS");
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   φ⁷·⁵ = " + PhiQuantumConstants.PHI_7_5);
        System.out.println("   φ⁷⁵  = " + PhiQuantumConstants.PHI_75);
        System.out.println("   Cosmic Frequency: " + PhiQuantumConstants.COSMIC_FREQUENCY + " Hz");
        System.out.println("   Coherence Threshold: " + PhiQuantumConstants.QUANTUM_COHERENCE_THRESHOLD);
        System.out.println("   Evolution Rate: " + PhiQuantumConstants.calculateEvolutionRate());
        System.out.println();
        
        // 2. Create BattleSystem with QuantumWarriors
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   2. BATTLE SYSTEM - QUANTUM WARRIORS");
        System.out.println("   ═══════════════════════════════════════════════════════════");
        
        BattleSystem battleSystem = new BattleSystem();
        
        // Recruit quantum warriors
        QuantumWarrior blueQuantum = battleSystem.recruitQuantumWarrior(
            WarriorNFT.WarriorType.BLUE_DEFENDER,
            WarriorNFT.WarriorClass.GOLD_GUARDIAN
        );
        
        QuantumWarrior redQuantum = battleSystem.recruitQuantumWarrior(
            WarriorNFT.WarriorType.RED_ATTACKER,
            WarriorNFT.WarriorClass.LOKI_BREAKER
        );
        
        // Demonstrate quantum abilities
        System.out.println();
        System.out.println("   >> Testing Quantum Abilities...");
        blueQuantum.cloak();
        blueQuantum.harmonize();
        
        // Run a quantum battle
        System.out.println();
        BattleSystem.QuantumBattleResult result = battleSystem.quantumBattle(blueQuantum, redQuantum);
        System.out.println(result.getFullReport());
        
        // Run a mini tournament
        battleSystem.tournament(3);
        
        System.out.println();
        System.out.println(battleSystem.getStatus());
        
        // 3. Knowledge Harvester
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   3. KNOWLEDGE HARVESTER - φ-INDEXED LEARNING");
        System.out.println("   ═══════════════════════════════════════════════════════════");
        
        KnowledgeHarvester harvester = new KnowledgeHarvester();
        
        // Harvest from battle system
        harvester.harvestFromBattleSystem(battleSystem);
        
        // Harvest some external knowledge
        harvester.harvest(
            KnowledgeHarvester.KnowledgeType.QUANTUM_STATE,
            "Quantum coherence above 0.618 enables dimensional cloaking",
            0.95,
            "quantum", "cloaking", "coherence"
        );
        
        harvester.harvest(
            KnowledgeHarvester.KnowledgeType.EVOLUTION_INSIGHT,
            "Warriors with 432Hz resonance alignment gain defensive bonuses",
            0.88,
            "resonance", "defense", "432hz"
        );
        
        // Auto-synthesize
        harvester.autoSynthesize();
        
        System.out.println();
        System.out.println(harvester.getStatus());
        
        // 4. Genesis Integration
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   4. GENESIS INTEGRATION");
        System.out.println("   ═══════════════════════════════════════════════════════════");
        System.out.println("   Genesis.java available at: fraymus.Genesis");
        System.out.println("   Run separately: java -cp build\\classes\\java\\main fraymus.Genesis");
        System.out.println();
        
        // Final summary
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              QUANTUM SYSTEMS INTEGRATION COMPLETE            ║");
        System.out.println("║   - PhiQuantumConstants: φ⁷⁵ reality anchor active          ║");
        System.out.println("║   - QuantumWarrior: Coherence + Resonance + Cloaking        ║");
        System.out.println("║   - BattleSystem: Quantum-enhanced combat                   ║");
        System.out.println("║   - KnowledgeHarvester: φ-spiral indexed learning           ║");
        System.out.println("║   - Genesis: Warrior birth & evolution                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
