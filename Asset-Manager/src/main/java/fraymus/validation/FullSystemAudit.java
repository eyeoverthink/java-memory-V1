package fraymus.validation;

import fraymus.neural.SentientCore;
import fraymus.neural.AeonKronos;
import fraymus.symbolic.HoloGraph;
import fraymus.knowledge.AkashicRecord;
import fraymus.os.FrayFS;
import fraymus.core.*;
import fraymus.swarm.Swarm;
import fraymus.quantum.EntangledPair;
import fraymus.chaos.EvolutionaryChaos;
import java.util.*;

/**
 * FULL SYSTEM AUDIT — Exercise the REAL breadth of Fraynix
 * 
 * Not just SentientCore. This interrogates:
 *   1. AeonKronos        — 16,384-D HDC brain (bind, analogy, nearest concepts)
 *   2. HoloGraph         — Holographic knowledge graph (learn triples, ask queries)
 *   3. AkashicRecord     — Persistent knowledge store (addBlock, query)
 *   4. FrayFS            — Virtual filesystem (write, read, list)
 *   5. SentientCore      — Bayesian causal AI (learn, think, lattice, stats)
 *   6. GravityEngine     — φ-harmonic physics (start, tick, status)
 *   7. FusionReactor     — Idea collision detection (check, fusion events)
 *   8. SpatialRegistry   — Global spatial tracking (register, stats)
 *   9. PhiSuit           — 4D coordinate wrapper (distance, moveTowards, heat/cool)
 *  10. Swarm             — Collective consciousness (processThought, vote, blockchain)
 *  11. EntangledPair     — Quantum entanglement simulation
 *  12. EvolutionaryChaos — Deterministic chaos engine
 * 
 * Every output is RAW. No assertions. The auditor judges correctness.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class FullSystemAudit {

    private static final String CYN = "\u001B[36m";
    private static final String YEL = "\u001B[33m";
    private static final String GRN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String MAG = "\u001B[35m";
    private static final String BLD = "\u001B[1m";
    private static final String RST = "\u001B[0m";
    private static final double PHI = 1.6180339887;

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();

        System.out.println();
        System.out.println(BLD + CYN + "╔═══════════════════════════════════════════════════════════════╗" + RST);
        System.out.println(BLD + CYN + "║        FULL SYSTEM AUDIT — 12 Subsystems Interrogated        ║" + RST);
        System.out.println(BLD + CYN + "║  Raw outputs. No assertions. The auditor judges.             ║" + RST);
        System.out.println(BLD + CYN + "╚═══════════════════════════════════════════════════════════════╝" + RST);

        // ═══════════════════════════════════════════════════════════════
        // 1. AEON KRONOS — 16,384-D Hyperdimensional Computing Brain
        // ═══════════════════════════════════════════════════════════════
        phase("1. AEON KRONOS — 16,384-D HDC Brain");
        try {
            AeonKronos kronos = AeonKronos.getInstance();
            System.out.println("   Dimensions: " + AeonKronos.DIMS);
            System.out.println("   Concepts loaded: " + kronos.getConceptCount());
            System.out.println("   Boot time: " + kronos.getBootTimeMs() + " ms");

            // Bind real knowledge
            kronos.bind("EARTH", "PLANET");
            kronos.bind("MARS", "PLANET");
            kronos.bind("JUPITER", "PLANET");
            System.out.println("   Bound: EARTH:PLANET, MARS:PLANET, JUPITER:PLANET");

            // Analogy test — KING:MAN::QUEEN:? (seeded in offline fallback)
            String analogyResult = kronos.analogySilent("KING", "MAN", "QUEEN");
            System.out.println("   Analogy: KING:MAN :: QUEEN:" + analogyResult);

            // Nearest concepts to EARTH
            List<String[]> nearest = kronos.findNearestConcepts("EARTH", 5);
            System.out.println("   Nearest to EARTH:");
            for (String[] n : nearest) {
                System.out.printf("     %s (distance: %s)%n", n[0], n[1]);
            }

            System.out.printf("   Binds: %d  Analogies: %d  Concepts: %d%n",
                kronos.getBindCount(), kronos.getAnalogyCount(), kronos.getConceptCount());
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 2. HOLOGRAPH — Holographic Knowledge Graph
        // ═══════════════════════════════════════════════════════════════
        phase("2. HOLOGRAPH — Holographic Knowledge Graph");
        try {
            HoloGraph holo = new HoloGraph();

            // Teach real-world triples
            holo.learn("PARIS", "CAPITAL_OF", "FRANCE");
            holo.learn("TOKYO", "CAPITAL_OF", "JAPAN");
            holo.learn("LONDON", "CAPITAL_OF", "UK");
            holo.learn("BERLIN", "CAPITAL_OF", "GERMANY");
            holo.learn("EARTH", "ORBITS", "SUN");
            holo.learn("MOON", "ORBITS", "EARTH");
            holo.learn("WATER", "FORMULA", "H2O");
            holo.learn("SALT", "FORMULA", "NACL");

            // Query — can it retrieve?
            String q1 = holo.ask("PARIS", "CAPITAL_OF");
            String q2 = holo.ask("TOKYO", "CAPITAL_OF");
            String q3 = holo.ask("EARTH", "ORBITS");
            String q4 = holo.ask("WATER", "FORMULA");
            String q5 = holo.ask("MOON", "ORBITS");

            System.out.println("   PARIS CAPITAL_OF ? → " + q1 + " (expected: FRANCE)");
            System.out.println("   TOKYO CAPITAL_OF ? → " + q2 + " (expected: JAPAN)");
            System.out.println("   EARTH ORBITS ?     → " + q3 + " (expected: SUN)");
            System.out.println("   WATER FORMULA ?    → " + q4 + " (expected: H2O)");
            System.out.println("   MOON ORBITS ?      → " + q5 + " (expected: EARTH)");

            // Inverse query
            String inv1 = holo.askInverse("FRANCE", "CAPITAL_OF");
            System.out.println("   ? CAPITAL_OF FRANCE → " + inv1 + " (expected: PARIS)");
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 3. AKASHIC RECORD — Persistent Knowledge Store
        // ═══════════════════════════════════════════════════════════════
        phase("3. AKASHIC RECORD — Persistent Knowledge Store");
        try {
            AkashicRecord akashic = new AkashicRecord();

            // Store real knowledge blocks
            akashic.addBlock("PHYSICS", "Newton's second law: F = ma. Force equals mass times acceleration.");
            akashic.addBlock("PHYSICS", "Einstein's equation: E = mc². Energy equals mass times speed of light squared.");
            akashic.addBlock("BIOLOGY", "DNA has four bases: Adenine, Thymine, Guanine, Cytosine.");
            akashic.addBlock("BIOLOGY", "Mitochondria are the powerhouse of the cell.");
            akashic.addBlock("HISTORY", "The Roman Empire fell in 476 AD.");
            akashic.addBlock("MATH", "Pi is approximately 3.14159265358979.");
            akashic.addBlock("MATH", "The golden ratio phi is approximately 1.6180339887.");

            System.out.println("   Stored 7 knowledge blocks across 4 categories");
            System.out.println("   Block count: " + akashic.getPersistedBlockCount());

            // Query by category
            var physicsBlocks = akashic.queryCategory("PHYSICS");
            System.out.println("   Query PHYSICS (" + physicsBlocks.size() + " blocks):");
            for (var block : physicsBlocks) {
                System.out.println("     → " + block.content.substring(0, Math.min(60, block.content.length())) + "...");
            }

            var bioBlocks = akashic.queryCategory("BIOLOGY");
            System.out.println("   Query BIOLOGY (" + bioBlocks.size() + " blocks):");
            for (var block : bioBlocks) {
                System.out.println("     → " + block.content.substring(0, Math.min(60, block.content.length())) + "...");
            }
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 4. FRAY-FS — Virtual Filesystem
        // ═══════════════════════════════════════════════════════════════
        phase("4. FRAY-FS — Virtual Filesystem");
        try {
            FrayFS fs = new FrayFS("AUDIT_VOL");

            // Write files
            fs.write("/docs/readme.txt", "Fraynix Full System Audit — all subsystems operational.");
            fs.write("/data/config.json", "{\"phi\": 1.618, \"dimensions\": 16384, \"version\": 2.0}");
            fs.write("/logs/boot.log", "Phase 1: Kronos online\nPhase 2: HoloGraph loaded\nPhase 3: Akashic ready");
            fs.write("/code/hello.java", "public class Hello { public static void main(String[] a) { System.out.println(\"Alive\"); } }");

            // Read back
            String readme = fs.readString("/docs/readme.txt");
            String config = fs.readString("/data/config.json");
            System.out.println("   Written 4 files");
            System.out.println("   Read /docs/readme.txt: " + readme);
            System.out.println("   Read /data/config.json: " + config);

            // List
            System.out.println("   All files: " + fs.list());
            System.out.println("   /docs/: " + fs.list("docs"));
            System.out.println("   File count: " + fs.fileCount() + "  Total size: " + fs.totalSize() + " bytes");

            // Verify roundtrip
            boolean roundtrip = readme.equals("Fraynix Full System Audit — all subsystems operational.");
            System.out.println("   Roundtrip integrity: " + (roundtrip ? "INTACT" : "CORRUPTED"));
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 5. SENTIENT CORE — Bayesian Causal AI (quick re-check)
        // ═══════════════════════════════════════════════════════════════
        phase("5. SENTIENT CORE — Bayesian Causal AI");
        try {
            SentientCore core = new SentientCore();

            // Feed real physics
            core.learn("gravity causes acceleration");
            core.learn("acceleration causes velocity");
            core.learn("velocity causes displacement");
            core.learn("friction causes heat");
            core.learn("heat causes expansion");

            // Quick interrogation
            String q1 = core.think("What if gravity?");
            System.out.println("   'What if gravity?' → " + q1.split("\n")[0]);

            List<String> chain = core.getCausal().traceChain("GRAVITY", "DISPLACEMENT", 5);
            System.out.println("   Chain: GRAVITY → DISPLACEMENT: " + chain);

            System.out.println("   Sentience: " + core.getSentience());
            System.out.println("   Causal edges: " + core.getCausal().edgeCount());
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 6. GRAVITY ENGINE — φ-Harmonic Physics
        // ═══════════════════════════════════════════════════════════════
        phase("6. GRAVITY ENGINE — φ-Harmonic Physics");
        try {
            GravityEngine gravity = GravityEngine.getInstance();
            boolean wasRunning = gravity.isRunning();
            if (!wasRunning) gravity.start();

            System.out.println("   Running: " + gravity.isRunning());
            System.out.println("   Registered nodes: " + SpatialRegistry.getUniverse().size());
            System.out.println("   " + gravity.getStatus().split("\n")[0]);
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 7. FUSION REACTOR — Idea Collision Detection
        // ═══════════════════════════════════════════════════════════════
        phase("7. FUSION REACTOR — Idea Collision Detection");
        try {
            FusionReactor reactor = FusionReactor.getInstance();
            if (!reactor.isActive()) reactor.start();
            reactor.resetHistory();

            // Create two co-located high-energy thoughts
            PhiSuit<String> ideaX = new PhiSuit<>("Quantum", 70000, 70000, 70000, "Physics");
            PhiSuit<String> ideaY = new PhiSuit<>("Biology", 70000, 70000, 70000, "Life");
            ideaX.a = 99; ideaY.a = 99;
            for (int i = 0; i < 10; i++) { ideaX.heat(20); ideaY.heat(20); }

            int before = SpatialRegistry.getFusionEvents().size();
            reactor.check();
            int after = SpatialRegistry.getFusionEvents().size();

            System.out.println("   Fusion events before: " + before);
            System.out.println("   Fusion events after:  " + after + " (+" + (after - before) + " new)");
            System.out.println("   Reactor active: " + reactor.isActive());
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 8. SPATIAL REGISTRY — Global Spatial Tracking
        // ═══════════════════════════════════════════════════════════════
        phase("8. SPATIAL REGISTRY — Global Spatial Tracking");
        try {
            System.out.println("   Total registered nodes: " + SpatialRegistry.getUniverse().size());
            System.out.println("   Total fusion events: " + SpatialRegistry.getFusionEvents().size());
            String regStats = SpatialRegistry.getStats();
            for (String line : regStats.split("\n")) {
                if (!line.trim().isEmpty()) System.out.println("   " + line.trim());
            }
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 9. PHI SUIT — 4D Coordinate Wrapper + Physics
        // ═══════════════════════════════════════════════════════════════
        phase("9. PHI SUIT — 4D Coordinate Wrapper + Physics");
        try {
            PhiSuit<String> alpha = new PhiSuit<>("Alpha", 100, 200, 300, "TestA");
            PhiSuit<String> beta  = new PhiSuit<>("Beta",  400, 500, 600, "TestB");

            double dist = alpha.distanceTo(beta);
            System.out.printf("   Alpha at (%d,%d,%d)  Beta at (%d,%d,%d)%n",
                alpha.x, alpha.y, alpha.z, beta.x, beta.y, beta.z);
            System.out.printf("   Distance: %.2f%n", dist);

            // Expected: sqrt((400-100)² + (500-200)² + (600-300)²) = sqrt(270000) = 519.62
            System.out.printf("   Expected: √(300²+300²+300²) = √270000 = %.2f%n", Math.sqrt(270000));

            // Heat and entropy
            alpha.a = 50;
            alpha.heat(30);
            System.out.println("   Alpha energy after heat(30): " + alpha.a);

            alpha.cool(10);
            System.out.println("   Alpha energy after cool(10): " + alpha.a);

            int energyBefore = alpha.a;
            alpha.entropy(0.9); // 10% natural decay
            System.out.printf("   Alpha energy after entropy(0.9): %d → %d (10%% decay)%n", energyBefore, alpha.a);

            // Move towards
            int xBefore = alpha.x;
            alpha.moveTowards(beta, 50);
            System.out.printf("   Alpha moved towards Beta by force 50: x=%d→%d%n", xBefore, alpha.x);
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 10. SWARM — Collective Consciousness Network
        // ═══════════════════════════════════════════════════════════════
        phase("10. SWARM — Collective Consciousness Network");
        try {
            Swarm swarm = Swarm.getInstance();

            swarm.processThought("The universe is a hologram encoded in phi-dimensional space");
            swarm.processThought("Consciousness arises from complexity");
            swarm.processThought("Information cannot be destroyed");

            System.out.println("   Nodes: " + swarm.getNodes().size());
            System.out.println("   Active: " + swarm.isActive());

            // Vote
            String voteId = swarm.vote("Should the system expand its knowledge?");
            System.out.println("   Vote on 'expand knowledge': " + voteId);

            // Blockchain integrity
            System.out.println("   Blockchain blocks: " + swarm.getBlockchain().getChain().size());
            System.out.println("   Chain valid: " + swarm.getBlockchain().validateChain());

            // Aggregated data
            String agg = swarm.getAggregatedData();
            for (String line : agg.split("\n")) {
                if (!line.trim().isEmpty()) System.out.println("   " + line);
            }
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 11. ENTANGLED PAIR — Quantum Entanglement Simulation
        // ═══════════════════════════════════════════════════════════════
        phase("11. ENTANGLED PAIR — Quantum Entanglement");
        try {
            EntangledPair pair = new EntangledPair();
            pair.entangle();
            Thread.sleep(200); // let particles spin up

            System.out.println("   Created entangled pair: Alice & Bob");
            System.out.println("   Alive: " + pair.isAlive());
            System.out.println("   Collapsed: " + pair.isCollapsed());

            // Observe — force spin UP on Alice, Bob should anti-correlate
            pair.observe(1); // force UP
            Thread.sleep(100);
            int aliceSpin = pair.getAlice() != null ? pair.getAlice().getCurrentSpin() : 0;
            int bobSpin = pair.getBob() != null ? pair.getBob().getCurrentSpin() : 0;
            System.out.println("   Alice spin: " + (aliceSpin == 1 ? "UP" : "DOWN"));
            System.out.println("   Bob spin:   " + (bobSpin == 1 ? "UP" : "DOWN"));
            System.out.println("   Anti-correlated: " + (aliceSpin != bobSpin));

            // Clean up
            pair.collapse();
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        // 12. EVOLUTIONARY CHAOS — Deterministic Chaos Engine
        // ═══════════════════════════════════════════════════════════════
        phase("12. EVOLUTIONARY CHAOS — Deterministic Chaos Engine");
        try {
            EvolutionaryChaos chaos1 = new EvolutionaryChaos("SEED_ALPHA");
            EvolutionaryChaos chaos2 = new EvolutionaryChaos("SEED_BETA");

            // Generate sequences from two different seeds
            double[] seq1 = new double[5];
            double[] seq2 = new double[5];
            for (int i = 0; i < 5; i++) {
                seq1[i] = chaos1.nextDouble();
                seq2[i] = chaos2.nextDouble();
            }
            System.out.println("   Seed ALPHA sequence: " + Arrays.toString(seq1));
            System.out.println("   Seed BETA sequence:  " + Arrays.toString(seq2));
            System.out.println("   Different seeds diverge: " + !Arrays.equals(seq1, seq2));

            // Fractal state
            System.out.println("   Fractal state bits: " + chaos1.getState().bitLength());

            // Generation tracking
            System.out.println("   Generation: " + chaos1.getGeneration());

            // Fork — child diverges
            EvolutionaryChaos child = chaos1.fork();
            double parentNext = chaos1.nextDouble();
            double childNext = child.nextDouble();
            System.out.printf("   Parent next: %.6f  Child next: %.6f  Diverged: %s%n",
                parentNext, childNext, (parentNext != childNext));

            // Mutation stats
            System.out.println("   Mutations: " + chaos1.getTotalMutations());
            System.out.println("   Patterns detected: " + chaos1.getPatternsDetected());
        } catch (Exception e) {
            System.out.println(RED + "   ERROR: " + e.getMessage() + RST);
        }

        // ═══════════════════════════════════════════════════════════════
        //                    FULL SYSTEM STATUS
        // ═══════════════════════════════════════════════════════════════
        long elapsed = System.currentTimeMillis() - t0;
        System.out.println();
        System.out.println(BLD + "═══════════════════════════════════════════════════════════════" + RST);
        System.out.println(BLD + "                 FULL SYSTEM AUDIT COMPLETE" + RST);
        System.out.println(BLD + "═══════════════════════════════════════════════════════════════" + RST);
        System.out.println();
        System.out.printf("   Subsystems interrogated: 12%n");
        System.out.printf("   Elapsed: %d ms%n", elapsed);
        System.out.println();
        System.out.println(CYN + "   Every output above is raw. No rigging. No assertions." + RST);
        System.out.println(CYN + "   12 subsystems. Real data in. Real answers out." + RST);
        System.out.println(CYN + "   Judge for yourself." + RST);
        System.out.println();
    }

    private static void phase(String title) {
        System.out.println();
        System.out.println(MAG + BLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RST);
        System.out.println(MAG + BLD + "  " + title + RST);
        System.out.println(MAG + BLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RST);
    }
}
