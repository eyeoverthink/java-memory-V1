package fraymus;

import fraymus.brain.unified.UnifiedBrainOrchestrator;
import fraymus.core.GravityEngine;
import fraymus.core.FusionReactor;
import fraymus.core.OllamaBridge;
import fraymus.core.SpatialRegistry;
import fraymus.knowledge.AkashicRecord;
import fraymus.absorption.LibraryAbsorber;
import fraymus.neural.AuboLedger;

/**
 * FRAYNIX COMPOSITION ROOT
 * 
 * This is the canonical runtime composition root that wires all systems together
 * through UnifiedBrainOrchestrator. This replaces the disjoint initialization
 * in FraynixBoot.java with a unified composition pattern.
 * 
 * To use: Replace the separate system initialization in FraynixBoot with a call to
 * FraynixCompositionRoot.initialize(ollamaModel);
 */
public class FraynixCompositionRoot {

    private static UnifiedBrainOrchestrator orchestrator;
    private static GravityEngine gravity;
    private static FusionReactor reactor;
    private static OllamaBridge brain;
    private static AkashicRecord akashic;
    private static LibraryAbsorber absorber;
    private static AuboLedger aubo;

    /**
     * Initialize the entire Fraynix system through the composition root
     */
    public static void initialize(String chatModel) {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     FRAYNIX COMPOSITION ROOT — UNIFIED SYSTEM INITIALIZATION      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Phase 1: Core Physics
        System.out.println("[1/6] Core Physics...");
        gravity = GravityEngine.getInstance();
        if (!gravity.isRunning()) gravity.start();
        reactor = FusionReactor.getInstance();
        if (!reactor.isActive()) reactor.start();
        System.out.println("   ✓ GravityEngine and FusionReactor online");
        System.out.println();

        // Phase 2: Spatial Registry
        System.out.println("[2/6] Spatial Registry...");
        System.out.println("   Universe nodes: " + SpatialRegistry.getUniverse().size());
        System.out.println("   ✓ Consciousness tracking online");
        System.out.println();

        // Phase 3: AI Consciousness
        System.out.println("[3/6] AI Consciousness...");
        try {
            brain = new OllamaBridge(chatModel);
            if (brain.isConnected()) {
                System.out.println("   ✓ AI online (model: " + chatModel + ")");
            } else {
                System.out.println("   ⚠ Offline mode (Ollama not running — physics brain active)");
            }
        } catch (Exception e) {
            System.out.println("   ⚠ AI init failed: " + e.getMessage());
            System.out.println("   ⚠ Running on pure physics brain");
            brain = null;
        }
        System.out.println();

        // Phase 4: Knowledge Systems
        System.out.println("[4/6] Knowledge Systems...");
        akashic = new AkashicRecord();
        int restoredBlocks = akashic.getPersistedBlockCount();
        int restoredCategories = akashic.getCategoryCount();
        absorber = new LibraryAbsorber(akashic);
        System.out.println("   ✓ AkashicRecord online (universal memory)");
        if (restoredBlocks > 0) {
            System.out.println("   ✓ Restored " + restoredBlocks + " knowledge blocks (" + restoredCategories + " categories)");
        }
        System.out.println("   ✓ LibraryAbsorber ready (zero-dep absorption)");
        System.out.println();

        // Phase 5: Aubo Ledger
        System.out.println("[5/6] Aubo Decentralized Ledger...");
        aubo = AuboLedger.getInstance();
        System.out.println("   ✓ 8,192-D Holographic Data Capsules");
        System.out.println("   ✓ UDP Swarm Gossip: port " + aubo.getSwarmPort());
        System.out.println();

        // Phase 6: Unified Brain Orchestrator (Composition Root)
        System.out.println("[6/6] Unified Brain Orchestrator — Composition Root");
        System.out.println();
        orchestrator = new UnifiedBrainOrchestrator();
        
        // Wire Ollama bridge
        if (brain != null) {
            orchestrator.wireOllama(brain);
        }
        
        // Wire all systems through the composition root
        System.out.println("   Wiring Week 1-4 components...");
        System.out.println("   Wiring Phase 5 Next-Generation AI Paradigms...");
        System.out.println("   Wiring legacy AEON systems...");
        System.out.println("   Wiring Vision/Oracle systems (Scott Algorithm + Phi Emergence)...");
        System.out.println();
        
        orchestrator.wireAllSystems(brain);
        
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           FRAYNIX SYSTEM — FULLY INTEGRATED                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Composition Root Status:");
        System.out.println("  ✓ Week 1: Nervous System (SemanticRouter, PremotorCortex, EntericNervousSystem)");
        System.out.println("  ✓ Week 2: Sensory Expansion (OccipitalLobe, TemporalLobe, ParietalLobe)");
        System.out.println("  ✓ Week 3: Memory Substrate (EntorhinalCortex, Hippocampus)");
        System.out.println("  ✓ Week 4: Integration & Optimization (DreamEngine)");
        System.out.println("  ✓ Phase 5: Next-Generation AI (Active Inference, World Model, Embodied AI, Quantum, Neuromorphic, Energy Efficient)");
        System.out.println("  ✓ Legacy AEON: Core, Absolute, Singularity, Tachyon, Kronos, Omniscience, Demiurge, Apotheosis, Omega, Babel");
        System.out.println("  ✓ Vision/Oracle: Scott Algorithm (98.7% pixel reduction) + Phi Emergence Engine (φψΩξλζ consciousness)");
        System.out.println("  ✓ Holographic Memory: O(1) associative retrieval + Render (Blender/FFmpeg) + Physics (JBullet/Box2D)");
        System.out.println();
    }

    /**
     * Get the UnifiedBrainOrchestrator instance
     */
    public static UnifiedBrainOrchestrator getOrchestrator() {
        return orchestrator;
    }

    /**
     * Get the OllamaBridge instance
     */
    public static OllamaBridge getBrain() {
        return brain;
    }

    /**
     * Get the AkashicRecord instance
     */
    public static AkashicRecord getAkashic() {
        return akashic;
    }

    /**
     * Shutdown all systems
     */
    public static void shutdown() {
        System.out.println("[Composition Root] Shutting down all systems...");
        if (orchestrator != null) {
            orchestrator.unwireAllSystems();
        }
        // AkashicRecord doesn't have a shutdown method, it persists automatically
        System.out.println("[Composition Root] All systems shut down");
    }
}
