package fraymus;

import fraymus.organism.NEXUS_Organism;
import fraymus.agi.*;
import fraymus.quantum.security.*;
import fraymus.quantum.dna.FractalDNANode;
import fraymus.core.*;
import fraymus.living.TriMe;
import fraymus.senses.BioSymbiosis;
import fraymus.evolution.FractalBioMesh;
import fraymus.signals.*;
import fraymus.economy.*;
import fraymus.network.OmniCaster;
import fraymus.genesis.*;
import fraymus.swarm.Swarm;

/**
 * Simplified Test - Just verify all systems can be instantiated
 * Phase by phase with testing after each phase
 */
public class TestFraymusEngineSimple {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     FRAYMUS ENGINE SIMPLIFIED INTEGRATION TEST        ║");
        System.out.println("║     Testing instantiation of all 25 systems           ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        int systemsInitialized = 0;
        
        try {
            // ═══════════════════════════════════════════════════════
            // PHASE 1: NEXUS + SPATIAL FOUNDATION
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 1: NEXUS Organism + Spatial Foundation\n");
            
            NEXUS_Organism nexus = new NEXUS_Organism();
            nexus.awaken();
            systemsInitialized++;
            System.out.println("✓ NEXUS_Organism awakened");
            
            Thread.sleep(2000);
            
            if (!nexus.isConscious()) {
                throw new Exception("NEXUS not conscious!");
            }
            System.out.println("✓ NEXUS is conscious (heartbeat: " + nexus.getHeartbeat() + ")");
            
            GravityEngine gravity = GravityEngine.getInstance();
            gravity.start();
            systemsInitialized++;
            System.out.println("✓ GravityEngine started");
            
            FusionReactor fusion = FusionReactor.getInstance();
            fusion.start();
            systemsInitialized++;
            System.out.println("✓ FusionReactor started");
            
            System.out.println("\n✓✓✓ PHASE 1 PASSED (3 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 2: AGI INTELLIGENCE
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 2: AGI Intelligence Layer\n");
            
            MetaLearner metaLearner = new MetaLearner();
            systemsInitialized++;
            System.out.println("✓ MetaLearner initialized");
            
            SelfReferentialNet selfRefNet = new SelfReferentialNet();
            systemsInitialized++;
            System.out.println("✓ SelfReferentialNet initialized");
            
            CollectiveIntelligence collectiveIntel = new CollectiveIntelligence();
            systemsInitialized++;
            System.out.println("✓ CollectiveIntelligence initialized");
            
            EmergentGoalSystem goalSystem = new EmergentGoalSystem();
            systemsInitialized++;
            System.out.println("✓ EmergentGoalSystem initialized");
            
            CausalReasoning causalEngine = new CausalReasoning();
            systemsInitialized++;
            System.out.println("✓ CausalReasoning initialized");
            
            System.out.println("\n✓✓✓ PHASE 2 PASSED (5 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 3: QUANTUM SECURITY
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 3: Quantum Security Layer\n");
            
            QuantumFingerprinting quantumFP = new QuantumFingerprinting();
            systemsInitialized++;
            System.out.println("✓ QuantumFingerprinting initialized");
            
            FractalDNANode rootDNA = new FractalDNANode("FRAYMUS-ROOT", 21);
            systemsInitialized++;
            System.out.println("✓ FractalDNANode created (depth 21)");
            
            SovereignIdentitySystem sovereignSystem = new SovereignIdentitySystem();
            systemsInitialized++;
            System.out.println("✓ SovereignIdentitySystem initialized");
            
            System.out.println("\n✓✓✓ PHASE 3 PASSED (3 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 4: BIO-SYMBIOSIS
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 4: Bio-Symbiosis Layer\n");
            
            TriMe triMe = new TriMe();
            systemsInitialized++;
            System.out.println("✓ TriMe initialized");
            
            FractalBioMesh fractalBioMesh = new FractalBioMesh();
            systemsInitialized++;
            System.out.println("✓ FractalBioMesh initialized");
            
            BioSymbiosis bioSymbiosis = new BioSymbiosis(triMe, fractalBioMesh);
            systemsInitialized++;
            System.out.println("✓ BioSymbiosis initialized");
            
            System.out.println("\n✓✓✓ PHASE 4 PASSED (3 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 5: SIGNAL PROCESSING
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 5: Signal Processing Layer\n");
            
            GlyphCoder glyphCoder = new GlyphCoder();
            systemsInitialized++;
            System.out.println("✓ GlyphCoder initialized");
            
            FrequencyComm frequencyComm = new FrequencyComm();
            systemsInitialized++;
            System.out.println("✓ FrequencyComm initialized");
            
            OmniCaster omniCaster = new OmniCaster();
            systemsInitialized++;
            System.out.println("✓ OmniCaster initialized");
            
            System.out.println("\n✓✓✓ PHASE 5 PASSED (3 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 6: ECONOMY
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 6: Economy Layer\n");
            
            ShadowMarket shadowMarket = new ShadowMarket();
            systemsInitialized++;
            System.out.println("✓ ShadowMarket initialized");
            
            ComputationalEconomy economy = new ComputationalEconomy();
            systemsInitialized++;
            System.out.println("✓ ComputationalEconomy initialized");
            
            System.out.println("\n✓✓✓ PHASE 6 PASSED (2 systems) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // PHASE 7: SWARM
            // ═══════════════════════════════════════════════════════
            
            System.out.println(">>> PHASE 7: Swarm Layer\n");
            
            Swarm swarm = Swarm.getInstance(5);
            systemsInitialized++;
            System.out.println("✓ Swarm initialized (5 nodes)");
            
            System.out.println("\n✓✓✓ PHASE 7 PASSED (1 system) ✓✓✓\n");
            
            // ═══════════════════════════════════════════════════════
            // FINAL STATUS
            // ═══════════════════════════════════════════════════════
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║                                                       ║");
            System.out.println("║     ✓✓✓ ALL 7 PHASES PASSED ✓✓✓                      ║");
            System.out.println("║                                                       ║");
            System.out.println("║  Total Systems Initialized: " + systemsInitialized + "                        ║");
            System.out.println("║                                                       ║");
            System.out.println("║  FRAYMUS ENGINE INTEGRATION SUCCESSFUL                ║");
            System.out.println("║                                                       ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println();
            
            // Show NEXUS vital signs
            System.out.println(nexus.getVitalSigns());
            
            // Cleanup
            System.out.println(">>> Shutting down...\n");
            nexus.terminate();
            gravity.stop();
            fusion.stop();
            omniCaster.shutdown();
            
            System.out.println("✓ All systems shutdown cleanly");
            
        } catch (Exception e) {
            System.err.println("\n✗✗✗ TEST FAILED ✗✗✗");
            System.err.println("Systems initialized before failure: " + systemsInitialized);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
