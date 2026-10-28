package fraymus;

import fraymus.agi.*;
import fraymus.quantum.security.QuantumFingerprinting;
import fraymus.core.*;
import fraymus.living.TriMe;
import fraymus.senses.BioSymbiosis;
import fraymus.evolution.FractalBioMesh;
import fraymus.signals.*;
import fraymus.organism.NEXUS_Organism;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for FraymusEngine subsystems wired into FraynixBoot.
 * Tests the real APIs used by each shell command.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestFraymusEngineIntegration {

    private static FraymusEngine engine;

    @BeforeAll
    static void initEngine() throws Exception {
        engine = new FraymusEngine();
        engine.initializeAll();
    }

    // ═══════════════════════════════════════════════════
    // PHASE 2: AGI SUBSYSTEMS
    // ═══════════════════════════════════════════════════

    @Test @Order(1)
    void testMetaLearnerInstantiation() {
        MetaLearner ml = engine.getMetaLearner();
        assertNotNull(ml, "MetaLearner should be initialized");
    }

    @Test @Order(2)
    void testMetaLearnerAdapt() {
        MetaLearner ml = engine.getMetaLearner();
        ml.evaluateAndAdapt();
        assertNotNull(ml.getCurrentStrategy(), "Strategy should not be null after adapt");
    }

    @Test @Order(3)
    void testMetaLearnerRecordLearning() {
        MetaLearner ml = engine.getMetaLearner();
        ml.recordLearning("test-domain", "pattern-A", 0.9, 0.8);
        assertTrue(ml.getTotalEvents() > 0, "Should have at least 1 event after recording");
    }

    @Test @Order(4)
    void testMetaLearnerGetters() {
        MetaLearner ml = engine.getMetaLearner();
        assertTrue(ml.getLearningRate() >= 0 && ml.getLearningRate() <= 1.0, "Learning rate should be [0,1]");
        assertTrue(ml.getExplorationRate() >= 0, "Exploration rate should be non-negative");
        assertTrue(ml.getDomainCount() >= 0, "Domain count should be non-negative");
    }

    @Test @Order(10)
    void testCausalReasoningInstantiation() {
        CausalReasoning ce = engine.getCausalEngine();
        assertNotNull(ce, "CausalReasoning should be initialized");
    }

    @Test @Order(11)
    void testCausalReasoningRegisterAndMetrics() {
        CausalReasoning ce = engine.getCausalEngine();
        ce.registerVariable("temperature", CausalReasoning.VariableType.CONTINUOUS);
        ce.registerVariable("pressure", CausalReasoning.VariableType.CONTINUOUS);
        assertTrue(ce.getNodeCount() >= 2, "Should have at least 2 nodes");
    }

    @Test @Order(12)
    void testCausalReasoningGetters() {
        CausalReasoning ce = engine.getCausalEngine();
        assertTrue(ce.getEdgeCount() >= 0, "Edge count should be non-negative");
        assertTrue(ce.getObservationCount() >= 0, "Observation count should be non-negative");
        assertTrue(ce.getInterventionsRun() >= 0, "Interventions count should be non-negative");
    }

    @Test @Order(20)
    void testEmergentGoalSystemInstantiation() {
        EmergentGoalSystem gs = engine.getGoalSystem();
        assertNotNull(gs, "EmergentGoalSystem should be initialized");
    }

    @Test @Order(21)
    void testEmergentGoalSystemCreateGoal() {
        EmergentGoalSystem gs = engine.getGoalSystem();
        var goal = gs.createGoal("test-goal", "Test goal for validation", EmergentGoalSystem.GoalType.DELEGATED, 0.7);
        assertNotNull(goal, "Created goal should not be null");
        assertTrue(gs.getGoalsCreated() > 0, "Goals created count should increase");
    }

    @Test @Order(22)
    void testEmergentGoalSystemDiscover() {
        EmergentGoalSystem gs = engine.getGoalSystem();
        gs.discoverGoals("success-pattern", 0.8, 0.9);
        assertTrue(gs.getActiveGoalCount() >= 0, "Active goal count should be non-negative");
    }

    @Test @Order(23)
    void testEmergentGoalSystemDrives() {
        EmergentGoalSystem gs = engine.getGoalSystem();
        assertTrue(gs.getCuriosityDrive() >= 0, "Curiosity drive should be non-negative");
        assertTrue(gs.getExplorationDrive() >= 0, "Exploration drive should be non-negative");
        assertTrue(gs.getOptimizationDrive() >= 0, "Optimization drive should be non-negative");
    }

    @Test @Order(30)
    void testSelfReferentialNetInstantiation() {
        SelfReferentialNet sr = engine.getSelfRefNet();
        assertNotNull(sr, "SelfReferentialNet should be initialized");
    }

    @Test @Order(31)
    void testSelfReferentialNetObserve() {
        SelfReferentialNet sr = engine.getSelfRefNet();
        double[] activations = {0.5, 0.6, 0.7, 0.8, 0.9, 0.4, 0.3, 0.2};
        sr.observeProcessing(activations, 0.75, 0.8);
        assertTrue(sr.getConsciousnessLevel() >= 0, "Consciousness should be non-negative");
        assertTrue(sr.getSelfAwarenessScore() >= 0, "Self-awareness should be non-negative");
    }

    @Test @Order(32)
    void testSelfReferentialNetGetters() {
        SelfReferentialNet sr = engine.getSelfRefNet();
        assertTrue(sr.getProcessingCoherence() >= 0, "Coherence should be non-negative");
        assertTrue(sr.getPhiResonance() >= 0, "Phi resonance should be non-negative");
        assertTrue(sr.getObservationCycles() >= 0, "Observation cycles should be non-negative");
    }

    @Test @Order(40)
    void testCollectiveIntelligenceInstantiation() {
        CollectiveIntelligence ci = engine.getCollectiveIntel();
        assertNotNull(ci, "CollectiveIntelligence should be initialized");
    }

    @Test @Order(41)
    void testCollectiveIntelligenceGetters() {
        CollectiveIntelligence ci = engine.getCollectiveIntel();
        assertTrue(ci.getCollectiveCoherence() >= 0, "Collective coherence should be non-negative");
        assertTrue(ci.getEmergentConsciousness() >= 0, "Emergent consciousness should be non-negative");
        assertTrue(ci.getGroupResonance() >= 0, "Group resonance should be non-negative");
    }

    // ═══════════════════════════════════════════════════
    // PHASE 3: QUANTUM SECURITY
    // ═══════════════════════════════════════════════════

    @Test @Order(50)
    void testQuantumFingerprintingInstantiation() {
        QuantumFingerprinting qfp = engine.getQuantumFP();
        assertNotNull(qfp, "QuantumFingerprinting should be initialized");
    }

    @Test @Order(51)
    void testQuantumFingerprintGenerate() {
        QuantumFingerprinting qfp = engine.getQuantumFP();
        String fp = qfp.generateQuantumFingerprint("FRAYNIX_TEST");
        assertNotNull(fp, "Fingerprint should not be null");
        assertFalse(fp.isEmpty(), "Fingerprint should not be empty");
    }

    @Test @Order(52)
    void testQuantumFingerprintValidate() {
        QuantumFingerprinting qfp = engine.getQuantumFP();
        String data = "VALIDATE_ME";
        String fp = qfp.generateQuantumFingerprint(data);
        boolean valid = qfp.validateFingerprint(fp, data);
        assertTrue(valid, "Fingerprint should validate against original data");
    }

    @Test @Order(53)
    void testQuantumFingerprintStats() {
        QuantumFingerprinting qfp = engine.getQuantumFP();
        assertTrue(qfp.getFingerprintsGenerated() > 0, "Should have generated fingerprints");
        assertTrue(qfp.getValidationsPerformed() > 0, "Should have performed validations");
    }

    @Test @Order(55)
    void testSovereignSystem() {
        assertNotNull(engine.getSovereignSystem(), "SovereignIdentitySystem should be initialized");
    }

    // ═══════════════════════════════════════════════════
    // PHASE 4: BIO-SYMBIOSIS
    // ═══════════════════════════════════════════════════

    @Test @Order(60)
    void testTriMe() {
        assertNotNull(engine.getTriMe(), "TriMe should be initialized");
    }

    @Test @Order(61)
    void testBioSymbiosis() {
        BioSymbiosis bio = engine.getBioSymbiosis();
        assertNotNull(bio, "BioSymbiosis should be initialized");
        String status = bio.getStatus();
        assertNotNull(status, "BioSymbiosis status should not be null");
        assertFalse(status.isEmpty(), "BioSymbiosis status should not be empty");
    }

    @Test @Order(62)
    void testBioSymbiosisGetters() {
        BioSymbiosis bio = engine.getBioSymbiosis();
        assertTrue(bio.getStressLevel() >= 0 && bio.getStressLevel() <= 1.0, "Stress should be [0,1]");
        assertTrue(bio.getCoherence() >= 0 && bio.getCoherence() <= 1.0, "Coherence should be [0,1]");
        assertNotNull(bio.getCurrentState(), "State should not be null");
    }

    @Test @Order(63)
    void testFractalBioMesh() {
        assertNotNull(engine.getFractalBioMesh(), "FractalBioMesh should be initialized");
    }

    // ═══════════════════════════════════════════════════
    // PHASE 5: SIGNAL PROCESSING
    // ═══════════════════════════════════════════════════

    @Test @Order(70)
    void testGlyphCoderInstantiation() {
        GlyphCoder gc = engine.getGlyphCoder();
        assertNotNull(gc, "GlyphCoder should be initialized");
    }

    @Test @Order(71)
    void testGlyphCoderInjectExtract() {
        GlyphCoder gc = engine.getGlyphCoder();
        String emoji = "\uD83D\uDD12\uD83D\uDD11";
        String secret = "HELLO_FRAYMUS";
        String encoded = gc.injectData(emoji, secret);
        assertNotNull(encoded, "Encoded text should not be null");
        String decoded = gc.extractData(encoded);
        assertEquals(secret, decoded, "Decoded should match original secret");
    }

    @Test @Order(72)
    void testGlyphCoderStats() {
        GlyphCoder gc = engine.getGlyphCoder();
        String stats = gc.getStats();
        assertNotNull(stats, "Stats should not be null");
        assertTrue(stats.contains("GlyphCoder"), "Stats should identify GlyphCoder");
    }

    @Test @Order(73)
    void testFrequencyComm() {
        FrequencyComm fc = engine.getFrequencyComm();
        assertNotNull(fc, "FrequencyComm should be initialized");
    }

    @Test @Order(74)
    void testFrequencyCommBinary() {
        FrequencyComm fc = engine.getFrequencyComm();
        String binary = fc.textToBinary("AB");
        assertNotNull(binary, "Binary should not be null");
        assertTrue(binary.matches("[01]+"), "Binary should only contain 0s and 1s");
        String decoded = fc.binaryToText(binary);
        assertEquals("AB", decoded, "Round-trip text → binary → text should match");
    }

    // ═══════════════════════════════════════════════════
    // PHASE 6: ECONOMY
    // ═══════════════════════════════════════════════════

    @Test @Order(80)
    void testEconomy() {
        assertNotNull(engine.getEconomy(), "ComputationalEconomy should be initialized");
    }

    // ═══════════════════════════════════════════════════
    // ENGINE LIFECYCLE
    // ═══════════════════════════════════════════════════

    @Test @Order(90)
    void testEngineStatus() {
        String status = engine.getStatus();
        assertNotNull(status, "Engine status should not be null");
        assertFalse(status.isEmpty(), "Engine status should not be empty");
    }

    @AfterAll
    static void shutdownEngine() {
        if (engine != null) {
            engine.shutdown();
        }
    }
}
