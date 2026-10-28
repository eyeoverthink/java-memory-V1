package fraymus.ultimate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TestUltimateSystem {

    @TempDir
    Path tempDir;

    @Test
    void buildsUnifiedStatusFromRepoArtifacts() throws IOException {
        Path repoRoot = tempDir.resolve("Java-Memory");
        Path assetManager = repoRoot.resolve("Asset-Manager");
        Path universal = assetManager.resolve("fraymus-universal");
        Path aiCore = repoRoot.resolve("fraymus-ai-core");

        Files.createDirectories(assetManager.resolve("src/main/java/fraymus"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/ultimate"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/targets"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/neural"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/acquisition"));
        Files.createDirectories(assetManager.resolve("src/main/java/fraymus/ollama"));
        Files.createDirectories(assetManager.resolve("src/main/java/com/eyeoverthink/lazarus"));
        Files.createDirectories(assetManager.resolve("src/main/java/com/fraymus/omega"));
        Files.createDirectories(assetManager.resolve("docs"));
        Files.createDirectories(tempDir.resolve("f0_engine/src"));
        Files.createDirectories(tempDir.resolve(".northstar-kernel/generated/substrates"));
        Files.createDirectories(universal.resolve("src/main/java/fraynix"));
        Files.createDirectories(aiCore.resolve("src/main/java/io/fraymus/ai"));

        Files.writeString(repoRoot.resolve("README.md"), "# AEON\nPhi memory quantum physics");
        Files.writeString(tempDir.resolve("FRAYNIX_LIVE_EXECUTION_PROOF_2026-04-21.md"), "# Live proof\nFraynix already moved.\n");
        Files.writeString(assetManager.resolve("build.gradle"), "plugins { id 'java' }");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraymusConvergence.java"), "class FraymusConvergence {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraymusUI.java"), "class FraymusUI {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/FraynixBoot.java"), sampleFraynixBootSource());
        Files.writeString(assetManager.resolve("src/main/java/fraymus/neural/AeonWillowCrusher.java"), "class AeonWillowCrusher {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/ollama/Transmudder.java"), "class Transmudder {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/ultimate/UltimateSystem.java"), "class UltimateSystem {}");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/acquisition/OpenClaw.java"), """
            package fraymus.acquisition;
            public class OpenClaw {
                // No artificial limitations
            }
            """);
        Files.writeString(assetManager.resolve("src/main/java/com/fraymus/omega/Amygdala.java"), """
            package com.fraymus.omega;
            public class Amygdala {
                public boolean evaluateRisk(Object plan) {
                    return true; // Default to true (safe) for stub
                }
            }
            """);
        Files.writeString(assetManager.resolve("src/main/java/com/eyeoverthink/lazarus/LazarusEngine.java"), """
            package com.eyeoverthink.lazarus;
            public class LazarusEngine {
                private static final int INITIAL_POPULATION = 10;
                private static final int MAX_POPULATION = 100;
                private static final int TICK_INTERVAL_MS = 100;
                private static final int REPORT_INTERVAL = 50;
                private static final double ENTROPY_THRESHOLD = 80.0;
                private static final int STAGNATION_LIMIT = 100;
            }
            """);
        Files.writeString(assetManager.resolve("src/main/java/fraymus/targets/SelfbuildTarget.java"),
            "package fraymus.targets; public class SelfbuildTarget { public String ping() { return \"pong\"; } }");
        Files.writeString(assetManager.resolve("src/main/java/fraymus/targets/SelfbuildTargetTwo.java"),
            "package fraymus.targets; public class SelfbuildTargetTwo { public String pong() { return \"ping\"; } }");
        Files.writeString(assetManager.resolve("docs/brain.txt"), "Willow and Transmudder are living Fraynix organs.");
        Files.writeString(tempDir.resolve("f0_engine/src/main.rs"), """
            mod simd_ops;
            mod cisa;
            mod field_engine;
            mod attractor;
            fn main() {
                let _engine = field_engine::FieldEngine::new(1024, 0.5, 0.001);
            }
            """);
        Files.writeString(tempDir.resolve("f0_engine/src/field_engine.rs"), """
            pub struct FieldEngine { pub substrate: Vec<f32>, pub damping: f32, pub dt: f32 }
            impl FieldEngine {
                pub fn new(size: usize, damping: f32, dt: f32) -> Self {
                    let padded_size = (size + 7) & !7;
                    Self { substrate: vec![0.0; padded_size], damping, dt }
                }
                pub fn tick(&mut self) { let decay_factor = 1.0 - (self.damping * self.dt); for s in self.substrate.iter_mut() { *s *= decay_factor; } }
            }
            """);
        Files.writeString(tempDir.resolve("f0_engine/src/cisa.rs"), """
            pub enum Instruction { EXC { addr: usize, len: usize, delta: f32 }, INH { addr: usize, len: usize, delta: f32 }, SUP { addr1: usize, addr2: usize, len: usize }, CLP { addr: usize, len: usize } }
            """);
        Files.writeString(tempDir.resolve("f0_engine/src/simd_ops.rs"), """
            pub unsafe fn simd_excite(data: &mut [f32], delta: f32) {
                let _ = core::arch::x86_64::_mm256_set1_ps(delta);
                let _ = core::arch::x86_64::_mm256_add_ps(core::mem::zeroed(), core::mem::zeroed());
                let _ = core::arch::x86_64::_mm256_min_ps(core::mem::zeroed(), core::mem::zeroed());
                let _ = core::arch::x86_64::_mm256_max_ps(core::mem::zeroed(), core::mem::zeroed());
                let _ = data;
            }
            """);
        Files.writeString(tempDir.resolve("f0_engine/src/attractor.rs"), """
            pub struct Attractor { pub concept: String, pub minima_geometry: Vec<f32> }
            impl Attractor {
                pub fn calculate_energy(&self, substrate: &[f32], offset: usize) -> f32 {
                    let mut energy = 0.0;
                    for i in 0..self.minima_geometry.len() {
                        let diff = substrate[offset + i] - self.minima_geometry[i];
                        energy += diff * diff;
                    }
                    energy
                }
            }
            """);
        Files.writeString(tempDir.resolve(".northstar-kernel/generated/substrates/MODEL_SUBSTRATE_MANIFEST.tsv"),
            "modelId\tfamily\tlicenseId\tprovenance\tsourceUri\tbaseModel\tderivedFrom\tallowedOperations\tsourcePath\tsizeBytes\tattributes\n"
                + "fixture-model\tgpt2_bundle\tunknown\thuggingface_cache_bundle\thuggingface://fixture\tfixture\tfixture\tinspect,abstract,license_review_required\tC:/fixture\t1\tlicense.id=unknown\n");
        Files.writeString(tempDir.resolve(".northstar-kernel/generated/substrates/LOCAL_MODEL_RUNTIME_INVENTORY.md"),
            "# Local Model Runtime Inventory\n\n"
                + "- `fixture-model` backend=`python_transformers` available=`true` target=`C:/fixture`\n"
                + "- `qwen3-coder:latest` backend=`ollama` available=`true` target=`http://localhost:11434`\n"
                + "- `minimax-m2.5:latest` backend=`openai_compatible` available=`false` target=`http://fixture.local`\n");
        Files.writeString(universal.resolve("src/main/java/fraynix/FraynixOS.java"), "class FraynixOS {}");
        Files.writeString(aiCore.resolve("src/main/java/io/fraymus/ai/FraymusAI.java"), "class FraymusAI {}");

        UltimateSystem system = new UltimateSystem(assetManager, passingProofBattery("fixture"));
        UltimateOperatorManifest manifest = system.operatorManifest();
        UltimateSubsystemRegistry registry = system.registry();

        String status = system.execute("status");
        String systems = system.execute("systems");
        String commands = system.execute("commands");
        String engineSurface = system.execute("commands engine");
        String nativeSurface = system.execute("commands native");
        String engineSection = system.execute("commands exploration");
        String nativeSection = system.execute("commands native bridge-native");
        String route = system.execute("route facades");
        String targetedRoute = system.execute("route facades convergence");
        String engineRoute = system.execute("route engine exploration");
        String nativeRoute = system.execute("route native bridge-native");
        String launchChoices = system.execute("launch facades");
        String search = system.execute("search quantum");
        String math = system.execute("math prime 17");
        String tpuStatus = system.execute("tpu status");
        String tpuInfer = system.execute("tpu infer recursive secure routing");
        String willowProof = system.execute("willow proof 4");
        String willowStatus = system.execute("willow status");
        String transmudderProof = system.execute("transmudder proof " + assetManager.resolve("docs/brain.txt"));
        String transmudderStats = system.execute("mutter stats");
        String aiState = system.execute("ai describe your current system and state");
        String aiLivingProof = system.execute("ai What are the strongest living proof lanes active right now?");
        String aiTruthRegistry = system.execute("ai what parts of the Fraynix Principia are implemented now and what remains a frontier target?");
        String resume = system.execute("resume");
        String resumeLive = system.execute("resume live");
        String continuityMenus = system.execute("continuity menus");
        String modelsInit = system.execute("models init");
        String modelsCoding = system.execute("models select coding patch compile shell");
        String modelsMemory = system.execute("models select memory repo-scale context window");
        String aiResume = system.execute("ai resume fraynix state");
        String aiResumeLive = system.execute("ai resume fraynix state live");
        String aiWholeMenu = system.execute("ai use all menu systems together and tell me what works without making me specify");
        String aiDirectNative = system.execute("ai native status");
        String aiRunNative = system.execute("ai run native status");
        String aiRunViaRouteApi = system.routeAiPrompt("ai run native status");
        String aiRunNativeWithInvisiblePrefix = system.execute("\u200Bai run native status");
        String aiRunBlocked = system.execute("ai run launch facades");
        String aiRunTpu = system.execute("ai run tpu status");
        String aiDirectTpu = system.execute("ai tpu status");
        String aiRunTooLarge = system.execute("ai run " + "x".repeat(530));
        String aiRunControlChars = system.execute("ai run native status\nexit");
        String aiRunBabelInjection = system.execute("ai run babel status && whoami");
        String aiMenuRoute = system.execute("ai sentient rcortex teacher kronos omniscience");
        String aiLazarusProbe = system.execute("ai intent=evaluate target=lazarus_engine method=active_inference features=free_energy_minimization constraints=no_generic_advice output=equations_and_metrics");
        String aiOpenClawProbe = system.execute("ai intent=verify target=openclaw_execution method=formal_proof features=lean4_kernel constraints=deterministic_binary_outcome output=proof_script");
        String aiF0Probe = system.execute("ai intent=stress_test target=f0_engine method=adversarial_perturbation features=fisher_information_matrix constraints=simd_only output=coherence_metrics");
        String aiOrchestration = system.execute(
            "ai connect all systems brains intellect recursion progression willow babel transmudder \""
                + assetManager.resolve("docs/brain.txt").toString().replace("\\", "/")
                + "\""
        );
        String aiBiasRoute = system.execute("ai coding compile patch fix runtime syntax issue");
        String abstractionStatus = system.execute("abstraction status");
        String abstractionTail = system.execute("abstraction tail 2");
        String abstractionDelta = system.execute("abstraction delta");
        String selfbuild = system.execute("selfbuild recursive proof router");
        String sourceSelfbuild = system.execute("selfbuild file src/main/java/fraymus/targets/SelfbuildTarget.java :: stabilize selfbuild target");
        String comparativeSelfbuild = system.execute("selfbuild files src/main/java/fraymus/targets :: compare target set");
        String promotedSelfbuild = system.execute("selfbuild promote file src/main/java/fraymus/targets/SelfbuildTarget.java :: promote target set");
        String comparativePromotion = system.execute("selfbuild promote files src/main/java/fraymus/targets :: promote target set");
        String provePromotedSelfbuild = system.execute("selfbuild prove-promote file src/main/java/fraymus/targets/SelfbuildTarget.java :: prove target set");
        String comparativeProvePromotion = system.execute("selfbuild prove-promote files src/main/java/fraymus/targets :: prove target set");
        String session = system.execute("session");
        String guidedDemo = system.execute("demo guided");
        String reviewPromote = system.execute("selfbuild review-promote file src/main/java/fraymus/targets/SelfbuildTarget.java :: invite human review");
        String reviews = system.execute("selfbuild reviews");
        String rejectedReview = system.execute("selfbuild reject " + extractValue(reviewPromote, "Review:") + " :: fixture rejected");
        String secondReview = system.execute("selfbuild review-promote file src/main/java/fraymus/targets/SelfbuildTarget.java :: approve fixture review");
        String approvedReview = system.execute("selfbuild approve " + extractValue(secondReview, "Review:"));
        String export = system.execute("export generated/ultimate-fixture");

        assertTrue(status.contains("ULTIMATE SYSTEM READY"));
        assertTrue(status.contains("Command surfaces: 3"));
        assertTrue(status.contains("Metamorphic core: seeded descriptors="));
        assertTrue(systems.contains("convergence"));
        assertTrue(systems.contains("fraymus-engine"));
        assertTrue(commands.contains("COMMAND SURFACES"));
        assertTrue(engineSurface.contains("Fraymus Engine Command Surface"));
        assertTrue(nativeSurface.contains("Ultimate Native Surface"));
        assertTrue(engineSection.contains("SURFACE: Fraymus Engine Command Surface"));
        assertTrue(nativeSection.contains("SURFACE: Ultimate Native Surface"));
        assertTrue(route.contains("ROUTE GROUP"));
        assertTrue(route.contains("Default route: ultimate"));
        assertTrue(targetedRoute.contains("Chosen target: convergence"));
        assertTrue(targetedRoute.contains("Fraymus Convergence"));
        assertTrue(engineRoute.contains("ROUTE GROUP: Exploration"));
        assertTrue(engineRoute.contains("Surface: Fraymus Engine Command Surface"));
        assertTrue(nativeRoute.contains("ROUTE GROUP: Bridge Native"));
        assertTrue(nativeRoute.contains("Execution: direct in-process inside UltimateSystem"));
        assertTrue(launchChoices.contains("MULTI-TARGET LAUNCH"));
        assertTrue(launchChoices.contains("launch fraynix facades ultimate"));
        assertTrue(search.toLowerCase().contains("quantum"));
        assertTrue(math.contains("PRIME"));
        assertTrue(tpuStatus.contains("TPU REASONING ENGINE"));
        assertTrue(tpuStatus.contains("synapses:"));
        assertTrue(tpuInfer.contains("TPU INFERENCE COMPLETE"));
        assertTrue(tpuInfer.contains("counterfactual:"));
        assertTrue(willowProof.contains("WILLOW PROOF COMPLETE"));
        assertTrue(willowProof.contains("QubitsDriven: 4"));
        assertTrue(willowStatus.contains("WILLOW-CRUSHER STATUS"));
        assertTrue(transmudderProof.contains("TRANSMUDDER PROOF COMPLETE"));
        assertTrue(transmudderProof.contains("docs/brain.txt"));
        assertTrue(transmudderStats.contains("TRANSMUDDER STATS"));
        assertTrue(aiState.contains("Grounded SYSTEM_STATE envelope (machine-checkable)"));
        assertTrue(aiState.contains("aiRun={status="));
        assertTrue(aiState.contains("aiRunRouter"));
        assertTrue(aiState.contains("provenance:"));
        assertTrue(aiLivingProof.contains("Grounded operator answer from runtime manifest"));
        assertTrue(aiLivingProof.contains("Strongest living proof lanes active in this shell right now"));
        assertTrue(aiTruthRegistry.contains("FRAYNIX TRUTH REGISTRY"));
        assertTrue(aiTruthRegistry.contains("route=TRUTH_REGISTRY"));
        assertTrue(aiTruthRegistry.contains("implemented: bounded virtual substrate tracking"));
        assertTrue(aiTruthRegistry.contains("partial: connectome-shaped routing"));
        assertTrue(aiTruthRegistry.toLowerCase().contains("frontier"));
        assertTrue(resume.contains("FRAYNIX RESUME STATE"));
        assertTrue(resume.contains("Visible heads:"));
        assertTrue(resumeLive.contains("Action: resume-live-recheck"));
        assertTrue(resumeLive.contains("Delta: baseline live probe established"));
        assertTrue(resumeLive.contains("native -> observed-live"));
        assertTrue(continuityMenus.contains("FRAYNIX MENU VISIBILITY"));
        assertTrue(continuityMenus.contains("morse"));
        assertTrue(modelsInit.contains("FRAYNIX METAMORPHIC CORE"));
        assertTrue(modelsInit.contains("ModelDescriptor registry: online"));
        assertTrue(modelsInit.contains("CapabilityProfile registry: online"));
        assertTrue(modelsInit.contains("qwen3-coder:latest"));
        assertTrue(modelsCoding.contains("FRAYNIX MODEL SELECTION"));
        assertTrue(modelsCoding.contains("Role: coding"));
        assertTrue(modelsCoding.contains("Selected: qwen3-coder:latest"));
        assertTrue(modelsMemory.contains("Role: memory"));
        assertTrue(modelsMemory.contains("Selected: minimax-m2.5:latest"));
        assertTrue(aiResume.contains("FRAYNIX RESUME STATE"));
        assertTrue(aiResumeLive.contains("Action: resume-live-recheck"));
        assertTrue(aiResumeLive.contains("Delta: no head status changes since last probe."));
        assertTrue(aiResumeLive.contains("Observed-live:"));
        assertTrue(aiWholeMenu.contains("Action: menu-surface-visibility"));
        assertTrue(aiWholeMenu.contains("Action: live-capability-probes"));
        assertTrue(aiWholeMenu.contains("native -> observed-live"));
        assertTrue(aiWholeMenu.contains("morse -> visible-only"));
        assertTrue(system.latestCapabilityProbeSnapshot().results().stream()
            .anyMatch(result -> "native".equals(result.head()) && "observed-live".equals(result.status())));
        assertTrue(system.latestCapabilityProbeSnapshot().results().stream()
            .anyMatch(result -> "morse".equals(result.head()) && "visible-only".equals(result.status())));
        assertTrue(aiWholeMenu.contains("Action: ai-unison-plan"));
        assertTrue(aiDirectNative.contains("AI RUN EXECUTION"));
        assertTrue(aiDirectNative.contains("Command: native status"));
        assertTrue(aiDirectNative.contains("\"status\":\"ok\""));
        assertTrue(aiDirectNative.contains("ULTIMATE IMMORTAL KERNEL"));
        assertTrue(aiRunNative.contains("AI RUN EXECUTION"));
        assertTrue(aiRunNative.contains("Command: native status"));
        assertTrue(aiRunNative.contains("\"schema\":\"fraynix.ai-run.v1\""));
        assertTrue(aiRunNative.contains("\"policyVersion\":\"fraynix.ai-run.policy.v2\""));
        assertTrue(aiRunNative.contains("\"executionId\":\"airun-"));
        assertTrue(aiRunNative.contains("\"status\":\"ok\""));
        assertTrue(aiRunNative.contains("\"timeoutSeconds\":180"));
        assertTrue(aiRunNative.contains("\"outputSha256\":\""));
        assertTrue(aiRunNative.contains("\"outputTruncated\":"));
        assertTrue(aiRunNative.contains("ULTIMATE IMMORTAL KERNEL"));
        assertTrue(aiRunViaRouteApi.contains("AI RUN EXECUTION"));
        assertTrue(aiRunViaRouteApi.contains("Command: native status"));
        assertTrue(aiRunNativeWithInvisiblePrefix.contains("AI RUN EXECUTION"));
        assertTrue(aiRunNativeWithInvisiblePrefix.contains("Command: native status"));
        assertTrue(aiRunBlocked.contains("AI RUN EXECUTION"));
        assertTrue(aiRunBlocked.contains("\"status\":\"blocked\""));
        assertTrue(aiRunBlocked.contains("\"errorCode\":\"POLICY_HEAD_BLOCKED\""));
        assertTrue(aiRunTpu.contains("AI RUN EXECUTION"));
        assertTrue(aiRunTpu.contains("\"head\":\"tpu\""));
        assertTrue(aiRunTpu.contains("\"status\":\"ok\""));
        assertTrue(aiRunTpu.contains("TPU REASONING ENGINE"));
        assertTrue(aiDirectTpu.contains("AI RUN EXECUTION"));
        assertTrue(aiDirectTpu.contains("\"head\":\"tpu\""));
        assertTrue(aiRunTooLarge.contains("AI RUN EXECUTION"));
        assertTrue(aiRunTooLarge.contains("\"status\":\"blocked\""));
        assertTrue(aiRunTooLarge.contains("\"errorCode\":\"POLICY_COMMAND_TOO_LARGE\""));
        assertTrue(aiRunControlChars.contains("AI RUN EXECUTION"));
        assertTrue(aiRunControlChars.contains("\"status\":\"blocked\""));
        assertTrue(aiRunControlChars.contains("\"errorCode\":\"POLICY_COMMAND_CONTROL_CHARS\""));
        assertTrue(aiRunBabelInjection.contains("AI RUN EXECUTION"));
        assertTrue(aiRunBabelInjection.contains("\"status\":\"blocked\""));
        assertTrue(aiRunBabelInjection.contains("\"errorCode\":\"POLICY_BABEL_PAYLOAD_BLOCKED\""));
        assertTrue(aiMenuRoute.contains("Action: ai-unison-plan"));
        assertTrue(aiMenuRoute.contains("Action: ai-unison-run"));
        assertTrue(aiMenuRoute.contains("Action: abstraction-lane"));
        assertTrue(aiMenuRoute.contains("Action: fraynix-menu-bridge"));
        assertTrue(aiLazarusProbe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiLazarusProbe.contains("Result: LAZARUS ACTIVE INFERENCE REVIEW"));
        assertTrue(aiLazarusProbe.contains("q(s_t)"));
        assertTrue(aiLazarusProbe.contains("selectedAction="));
        assertTrue(aiLazarusProbe.contains("\"route\":\"lazarus_engine.active_inference\""));
        assertTrue(aiOpenClawProbe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiOpenClawProbe.contains("Result: OPENCLAW FORMAL VERIFICATION REVIEW"));
        assertTrue(aiOpenClawProbe.contains("Proof script:"));
        assertTrue(aiOpenClawProbe.contains("Policy decision code:"));
        assertTrue(aiOpenClawProbe.contains("\"route\":\"openclaw_execution.formal_proof\""));
        assertTrue(aiF0Probe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiF0Probe.contains("Result: F0 ENGINE ADVERSARIAL STRESS REVIEW"));
        assertTrue(aiF0Probe.contains("Coherence metrics:"));
        assertTrue(aiF0Probe.contains("fisherTrace="));
        assertTrue(aiF0Probe.contains("\"route\":\"f0_engine.adversarial_perturbation\""));
        assertTrue(aiOrchestration.contains("AI PROMPT ORCHESTRATION"));
        assertTrue(aiOrchestration.contains("Action: abstraction-lane"));
        assertTrue(aiOrchestration.contains("Action: ai-unison-plan"));
        assertTrue(aiOrchestration.contains("Action: ai-unison-run"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-plan"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-model-selection"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-lane"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-outcome"));
        assertTrue(abstractionStatus.contains("FRAYNIX ABSTRACTION MEMORY"));
        assertTrue(abstractionStatus.contains("Records:"));
        assertTrue(abstractionStatus.contains("Bias outcomes:"));
        assertTrue(abstractionTail.contains("FRAYNIX ABSTRACTION MEMORY TAIL"));
        assertTrue(abstractionTail.contains("Showing:"));
        assertTrue(abstractionDelta.contains("ABSTRACTION DELTA"));
        assertTrue(aiOrchestration.contains("Action: willow"));
        assertTrue(aiOrchestration.contains("Action: babel-transmute"));
        assertTrue(aiOrchestration.contains("Action: transmudder-proof"));
        assertTrue(aiOrchestration.contains("Action: system-bridge"));
        assertTrue(selfbuild.contains("SELFBUILD COMPLETE"));
        assertTrue(selfbuild.contains("Winner:"));
        assertTrue(sourceSelfbuild.contains("SELFBUILD COMPLETE"));
        assertTrue(sourceSelfbuild.contains("Winner:"));
        assertTrue(comparativeSelfbuild.contains("SELFBUILD COMPARATIVE COMPLETE"));
        assertTrue(comparativeSelfbuild.contains("Targets: 2"));
        assertTrue(promotedSelfbuild.contains("SELFBUILD PROMOTION COMPLETE"));
        assertTrue(promotedSelfbuild.contains("Promotable: true"));
        assertTrue(comparativePromotion.contains("SELFBUILD PROMOTION COMPARATIVE COMPLETE"));
        assertTrue(comparativePromotion.contains("Promotable targets: 2"));
        assertTrue(provePromotedSelfbuild.contains("SELFBUILD PROVE-PROMOTION COMPLETE"));
        assertTrue(provePromotedSelfbuild.contains("Approved: true"));
        assertTrue(comparativeProvePromotion.contains("SELFBUILD PROVE-PROMOTION COMPARATIVE COMPLETE"));
        assertTrue(comparativeProvePromotion.contains("Approved targets: 2"));
        assertTrue(session.contains("FRAYNIX SESSION"));
        assertTrue(session.contains("Latest proof: FRAYNIX_LIVE_EXECUTION_PROOF_2026-04-21.md"));
        assertTrue(session.contains("Substrate ledger: models=1"));
        assertTrue(guidedDemo.contains("FRAYNIX GUIDED DEMO COMPLETE"));
        assertTrue(reviewPromote.contains("SELFBUILD REVIEW-PROMOTION READY"));
        assertTrue(reviewPromote.contains("Status: pending"));
        assertTrue(reviews.contains("SELFBUILD REVIEWS"));
        assertTrue(rejectedReview.contains("SELFBUILD REVIEW REJECTED"));
        assertTrue(approvedReview.contains("SELFBUILD REVIEW APPROVED"));
        assertTrue(export.contains("ULTIMATE EXPORT COMPLETE"));
        assertTrue(manifest.surfaceCount() == 3);
        assertTrue(manifest.kernelPreferredSurfaceCount() == 1);
        assertTrue(manifest.livingProofLaneCount() == 5);
        assertTrue(manifest.findSurface("native").orElseThrow().kernelOwnedPreferred());
        assertTrue(manifest.findSurface("fraynix").orElseThrow().governingCore());
        assertTrue(manifest.findSurface("fraynix").orElseThrow().operatorShell());
        assertTrue(manifest.findLivingProofLane("morse").orElseThrow().proofLane());
        assertTrue(manifest.findLivingProofLane("proof").orElseThrow().focusCommands().contains("willow proof 8"));
        assertTrue(manifest.findLivingProofLane("proof").orElseThrow().evidencePaths().contains("src/main/java/fraymus/neural/AeonWillowCrusher.java"));
        assertTrue(manifest.findLivingProofLane("organism-validators").orElseThrow().focusCommands().contains("transmudder stats"));
        assertTrue(manifest.findLivingProofLane("organism-validators").orElseThrow().evidencePaths().contains("src/main/java/fraymus/ollama/Transmudder.java"));
        assertTrue(manifest.findLivingProofLane("ouroboros").orElseThrow().sections().size() >= 3);
        assertTrue(manifest.findSurface("fraynix").orElseThrow().findSection("facades").orElseThrow().kernelOverlap());
        assertTrue(manifest.bootTreeBranchCount() == 3);
        assertTrue(manifest.uncataloguedBootBranchCount() == 3);
        assertTrue(manifest.findSurface("fraynix").orElseThrow().findSection("boot-tree-launches").isPresent());
        assertTrue(manifest.findSurface("fraynix").orElseThrow().findSection("boot-tree-discovery").isPresent());
        assertTrue(manifest.surfaceAgiStrataCounts().containsKey("governing-core"));
        assertTrue(manifest.sectionAgiStrataCounts().containsKey("operator-shell"));
        assertTrue(manifest.livingProofLaneAgiStrataCounts().containsKey("proof-lane"));
        assertTrue(registry.agiStrataCounts().containsKey("proof-lane"));
        assertTrue(registry.find("fraynix-boot").orElseThrow().governingCore());
        assertTrue(registry.find("fraymus-engine").orElseThrow().organismCore());
        assertTrue(registry.find("willow-quantum").orElseThrow().bareMetalSubstrateLane());
        assertTrue(registry.find("transmudder-soul").orElseThrow().absorptionCore());
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/ultimate-architecture.md")));
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/ultimate-knowledge-graph.json")));
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/fraynix-math-canon.md")));
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/fraynix-axiom-ledger.md")));
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/fraynix-truth-registry.json")));
        assertTrue(Files.exists(assetManager.resolve("generated/ultimate-fixture/fraynix-continuity-memory.md")));
        Path selfbuildDir;
        try (var paths = Files.list(assetManager.resolve("generated/ultimate-selfbuild"))) {
            selfbuildDir = paths.max(Comparator.comparing(Path::toString)).orElseThrow();
        }
        Path selfbuildReport = selfbuildDir.resolve("selfbuild-report.md");
        assertTrue(Files.exists(selfbuildReport));
        Path comparativeDir;
        try (var paths = Files.list(assetManager.resolve("generated/ultimate-selfbuild"))
            .filter(path -> path.getFileName().toString().contains("comparative"))) {
            comparativeDir = paths.max(Comparator.comparing(Path::toString)).orElseThrow();
        }
        Path comparativeReport = comparativeDir.resolve("comparative-selfbuild-report.md");
        assertTrue(Files.exists(comparativeReport));
        Path promotionReport = extractReportedPath(promotedSelfbuild, "Report:");
        Path promotionPatch = extractReportedPath(promotedSelfbuild, "Patch:");
        Path comparativePromotionReport = extractReportedPath(comparativePromotion, "Report:");
        Path provePromotionReport = extractReportedPath(provePromotedSelfbuild, "Report:");
        Path comparativeProvePromotionReport = extractReportedPath(comparativeProvePromotion, "Report:");
        Path guidedDemoReport = extractReportedPath(guidedDemo, "Report:");
        Path reviewPatch = extractReportedPath(reviewPromote, "Patch:");
        Path approvalBackup = extractReportedPath(approvedReview, "Backup:");
        assertTrue(Files.exists(promotionReport));
        assertTrue(Files.exists(promotionPatch));
        assertTrue(Files.exists(comparativePromotionReport));
        assertTrue(Files.exists(provePromotionReport));
        assertTrue(Files.exists(comparativeProvePromotionReport));
        assertTrue(Files.exists(guidedDemoReport));
        assertTrue(Files.exists(reviewPatch));
        assertTrue(Files.exists(approvalBackup));
        String fixtureMarkdown = Files.readString(assetManager.resolve("generated/ultimate-fixture/ultimate-architecture.md"));
        String fixtureGraph = Files.readString(assetManager.resolve("generated/ultimate-fixture/ultimate-knowledge-graph.json"));
        String fixtureMath = Files.readString(assetManager.resolve("generated/ultimate-fixture/fraynix-math-canon.md"));
        String fixtureAxiomLedger = Files.readString(assetManager.resolve("generated/ultimate-fixture/fraynix-axiom-ledger.md"));
        String fixtureTruthRegistry = Files.readString(assetManager.resolve("generated/ultimate-fixture/fraynix-truth-registry.json"));
        String fixtureContinuity = Files.readString(assetManager.resolve("generated/ultimate-fixture/fraynix-continuity-memory.md"));
        String selfbuildMarkdown = Files.readString(selfbuildReport);
        String comparativeMarkdown = Files.readString(comparativeReport);
        String promotionMarkdown = Files.readString(promotionReport);
        String comparativePromotionMarkdown = Files.readString(comparativePromotionReport);
        String provePromotionMarkdown = Files.readString(provePromotionReport);
        String comparativeProvePromotionMarkdown = Files.readString(comparativeProvePromotionReport);
        String guidedDemoMarkdown = Files.readString(guidedDemoReport);
        String promotionDiff = Files.readString(promotionPatch);
        assertTrue(fixtureMarkdown.contains("Fraymus Nexus // Fraynix Offline AGI Operator Surface"));
        assertTrue(fixtureMarkdown.contains("## AGI Role Summary"));
        assertTrue(fixtureMarkdown.contains("subgraph agi_strata_lanes[\"AGI Strata Lanes\"]"));
        assertTrue(fixtureMarkdown.contains("classDef strata_governing_core"));
        assertTrue(fixtureMath.contains("# Fraynix Math Canon"));
        assertTrue(fixtureMath.contains("## Fraynix-Native Mathematical Spine"));
        assertTrue(fixtureMath.contains("## Major AI Math Families Fraynix Must Speak"));
        assertTrue(fixtureMath.contains("### Deterministic symbolic compiler / VM math"));
        assertTrue(fixtureMath.contains("src/main/java/fraymus/hdc/SharedHypervectorCore.java"));
        assertTrue(fixtureMath.contains("brain_architecture/src/main/java/brain_architecture/kernel/HybridMemoryStore.java"));
        assertTrue(fixtureAxiomLedger.contains("# Fraynix Axiom Ledger"));
        assertTrue(fixtureAxiomLedger.contains("Hostile Substrate Survival"));
        assertTrue(fixtureTruthRegistry.contains("\"title\": \"Fraynix Truth Registry\""));
        assertTrue(fixtureTruthRegistry.contains("\"hostile-substrate-survival\""));
        assertTrue(fixtureTruthRegistry.contains("\"resumeRules\""));
        assertTrue(fixtureTruthRegistry.contains("\"continuityMemoryPath\""));
        assertTrue(fixtureTruthRegistry.contains("\"deterministicLanes\""));
        assertTrue(fixtureContinuity.contains("# Fraynix Continuity Memory"));
        assertTrue(fixtureContinuity.contains("## Forced Resume Rules"));
        assertTrue(fixtureContinuity.contains("## External Deterministic Symbolic Lane"));
        assertTrue(fixtureContinuity.contains("BabelTransmuterREPL_WithVM.java"));
        assertTrue(fixtureGraph.contains("\"productName\": \"Fraymus Nexus // Fraynix Offline AGI Operator Surface\""));
        assertTrue(fixtureGraph.contains("\"subsystemAgiStrata\""));
        assertTrue(fixtureGraph.contains("\"visualSemantics\""));
        assertTrue(fixtureGraph.contains("\"layout\": \"agi_strata_first\""));
        assertTrue(fixtureGraph.contains("\"primaryAgiStratum\""));
        assertTrue(fixtureGraph.contains("\"clusters\""));
        assertTrue(fixtureGraph.contains("\"systemsUrl\": \"/systems?focus="));
        assertTrue(fixtureGraph.contains("\"runtimeUrl\": \"/runtime?subsystem="));
        assertTrue(fixtureGraph.contains("\"jumpUrl\": \"/surfaces?surface="));
        assertTrue(fixtureGraph.contains("&section="));
        assertTrue(fixtureGraph.contains("\"routeUrl\": \"/launchpad?cmd="));
        assertTrue(selfbuildMarkdown.contains("# Fraynix Selfbuild Judge Report"));
        assertTrue(selfbuildMarkdown.contains("## Winner"));
        assertTrue(selfbuildMarkdown.contains("## Scoreboard"));
        assertTrue(selfbuildMarkdown.contains("Compile Transcript"));
        assertTrue(selfbuildMarkdown.contains("SelfBuilder + SelfCodeEngine"));
        assertTrue(selfbuildMarkdown.contains("Mode: `source-target`") || selfbuildMarkdown.contains("Mode: `generated-organism`"));
        assertTrue(comparativeMarkdown.contains("# Fraynix Selfbuild Comparative Report"));
        assertTrue(comparativeMarkdown.contains("## Comparative Ranking"));
        assertTrue(comparativeMarkdown.contains("Target count: `2`"));
        assertTrue(comparativeMarkdown.contains("SelfbuildTarget.java"));
        assertTrue(comparativeMarkdown.contains("SelfbuildTargetTwo.java"));
        assertTrue(promotionMarkdown.contains("# Fraynix Selfbuild Promotion Report"));
        assertTrue(promotionMarkdown.contains("Promotable: `true`"));
        assertTrue(promotionMarkdown.contains("Patch artifact: [fraynix-promotion.patch]("));
        assertTrue(comparativePromotionMarkdown.contains("# Fraynix Selfbuild Comparative Promotion Report"));
        assertTrue(comparativePromotionMarkdown.contains("Promotable targets: `2`"));
        assertTrue(comparativePromotionMarkdown.contains("## Promotion Ranking"));
        assertTrue(provePromotionMarkdown.contains("# Fraynix Selfbuild Prove-Promotion Report"));
        assertTrue(provePromotionMarkdown.contains("Proof approved: `true`"));
        assertTrue(provePromotionMarkdown.contains("## Fraynix Proof Battery"));
        assertTrue(comparativeProvePromotionMarkdown.contains("# Fraynix Selfbuild Comparative Prove-Promotion Report"));
        assertTrue(comparativeProvePromotionMarkdown.contains("Approved targets: `2`"));
        assertTrue(comparativeProvePromotionMarkdown.contains("## Prove-Promotion Ranking"));
        assertTrue(guidedDemoMarkdown.contains("# Fraynix Guided Demo"));
        assertTrue(guidedDemoMarkdown.contains("## Willow proof sweep"));
        assertTrue(promotionDiff.contains("--- a/src/main/java/fraymus/targets/SelfbuildTarget.java"));
        assertTrue(promotionDiff.contains("+++ b/src/main/java/fraymus/targets/SelfbuildTarget.java"));
    }

    @Test
    void indexesCurrentWorkspace() throws IOException {
        Path assetManager = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (!Files.exists(assetManager.resolve("build.gradle"))) {
            assetManager = assetManager.resolve("Java-Memory").resolve("Asset-Manager");
        }

        UltimateSystem system = new UltimateSystem(assetManager, passingProofBattery("workspace"));
        UltimateOperatorManifest manifest = system.operatorManifest();
        String status = system.execute("status");
        String abstraction = system.execute("abstract");
        String commandSection = system.execute("commands bridge");
        String engineSection = system.execute("commands engine exploration");
        String nativeSurface = system.execute("commands native");
        String gotoSection = system.execute("goto bridge");
        String engineRoute = system.execute("goto engine exploration");
        String nativeRoute = system.execute("route native bridge-native");
        String nativeStatus = system.execute("native status");
        String learned = system.execute("learn quantum_gravity");
        String tpuStatus = system.execute("tpu status");
        String tpuInfer = system.execute("tpu infer recursive secure routing");
        String qboxCreate = system.execute("qbox create truth path | alibi path");
        String qboxSecret = system.execute("qbox secret");
        String collision = system.execute("collide logic emotion");
        String miving = system.execute("miving gen 12");
        String suited = system.execute("suit wrap DATA 1 2 3 beacon");
        String spatialNodes = system.execute("spatial nodes");
        String targetedRoute = system.execute("route facades ultimate");
        String sourceSelfbuild = system.execute("selfbuild file src/main/java/fraymus/facade/SelfCodeEngine.java :: stabilize local self-code engine");
        String promotedSelfbuild = system.execute("selfbuild promote file src/main/java/fraymus/facade/SelfCodeEngine.java :: promote local self-code engine");
        String provePromotedSelfbuild = system.execute("selfbuild prove-promote file src/main/java/fraymus/facade/SelfCodeEngine.java :: prove local self-code engine");
        String willowProof = system.execute("willow proof 6");
        String transmudderProof = system.execute("transmudder proof " + Path.of("F:\\Full-AI-System\\FRAYNIX_LIVE_EXECUTION_PROOF_2026-04-21.md"));
        String aiState = system.execute("ai describe your current system and state");
        String aiLivingProof = system.execute("ai What are the strongest living proof lanes active right now?");
        String aiTruthRegistry = system.execute("ai what parts of the Fraynix Principia are implemented now and what remains a frontier target?");
        String aiDirectNative = system.execute("ai native status");
        String aiRunNative = system.execute("ai run native status");
        String aiRunViaRouteApi = system.routeAiPrompt("ai run native status");
        String aiRunNativeWithInvisiblePrefix = system.execute("\u200Bai run native status");
        String aiRunBlocked = system.execute("ai run launch facades");
        String aiRunTpu = system.execute("ai run tpu status");
        String aiDirectTpu = system.execute("ai tpu status");
        String aiRunTooLarge = system.execute("ai run " + "x".repeat(530));
        String aiRunControlChars = system.execute("ai run native status\nexit");
        String aiRunBabelInjection = system.execute("ai run babel status && whoami");
        String aiMenuRoute = system.execute("ai sentient rcortex teacher kronos omniscience");
        String aiLazarusProbe = system.execute("ai intent=evaluate target=lazarus_engine method=active_inference features=free_energy_minimization constraints=no_generic_advice output=equations_and_metrics");
        String aiOpenClawProbe = system.execute("ai intent=verify target=openclaw_execution method=formal_proof features=lean4_kernel constraints=deterministic_binary_outcome output=proof_script");
        String aiF0Probe = system.execute("ai intent=stress_test target=f0_engine method=adversarial_perturbation features=fisher_information_matrix constraints=simd_only output=coherence_metrics");
        String aiOrchestration = system.execute(
            "ai connect all systems brains intellect recursion progression willow babel native kernel transmudder \""
                + Path.of("F:\\Full-AI-System\\FRAYNIX_LIVE_EXECUTION_PROOF_2026-04-21.md").toString().replace("\\", "/")
                + "\""
        );
        String aiBiasRoute = system.execute("ai coding compile patch fix runtime syntax issue");
        String abstractionStatus = system.execute("abstraction status");
        String abstractionTail = system.execute("abstraction tail 2");
        String abstractionDelta = system.execute("abstraction delta");
        String export = system.execute("export " + tempDir.resolve("workspace-export"));
        String shutdown = system.execute("native shutdown");

        assertTrue(status.contains("ULTIMATE SYSTEM READY"));
        assertTrue(abstraction.contains("ULTIMATE ABSTRACTION"));
        assertTrue(commandSection.contains("Quantum Bridge"));
        assertTrue(engineSection.contains("Fraymus Engine Command Surface"));
        assertTrue(nativeSurface.contains("Ultimate Native Surface"));
        assertTrue(gotoSection.contains("ROUTE GROUP: Quantum Bridge"));
        assertTrue(engineRoute.contains("ROUTE GROUP: Exploration"));
        assertTrue(nativeRoute.contains("ROUTE GROUP: Bridge Native"));
        assertTrue(nativeRoute.contains("Execution: direct in-process inside UltimateSystem"));
        assertTrue(nativeStatus.contains("ULTIMATE IMMORTAL KERNEL"));
        assertTrue(tpuStatus.contains("TPU REASONING ENGINE"));
        assertTrue(tpuStatus.contains("synapses:"));
        assertTrue(tpuInfer.contains("TPU INFERENCE COMPLETE"));
        assertTrue(tpuInfer.contains("counterfactual:"));
        assertTrue(learned.contains("LEARNED"));
        assertTrue(qboxCreate.contains("QBOX CREATED"));
        assertTrue(qboxSecret.toLowerCase().contains("truth path"));
        assertTrue(collision.contains("COLLISION RESULT"));
        assertTrue(collision.contains("LOGIC"));
        assertTrue(miving.contains("MIVING GENESIS"));
        assertTrue(suited.contains("[SUIT] Wrapped 'DATA'"));
        assertTrue(spatialNodes.contains("[SPATIAL] Live nodes:"));
        assertTrue(targetedRoute.contains("Chosen target: ultimate"));
        assertTrue(sourceSelfbuild.contains("SELFBUILD COMPLETE"));
        assertTrue(sourceSelfbuild.contains("Report:"));
        assertTrue(promotedSelfbuild.contains("SELFBUILD PROMOTION COMPLETE"));
        assertTrue(promotedSelfbuild.contains("Promotable:"));
        assertTrue(provePromotedSelfbuild.contains("SELFBUILD PROVE-PROMOTION COMPLETE"));
        assertTrue(provePromotedSelfbuild.contains("Approved:"));
        assertTrue(willowProof.contains("WILLOW PROOF COMPLETE"));
        assertTrue(transmudderProof.contains("TRANSMUDDER PROOF COMPLETE"));
        assertTrue(aiState.contains("Grounded SYSTEM_STATE envelope (machine-checkable)"));
        assertTrue(aiState.contains("aiRun={status="));
        assertTrue(aiState.contains("aiRunRouter"));
        assertTrue(aiLivingProof.contains("Grounded operator answer from runtime manifest"));
        assertTrue(aiTruthRegistry.contains("FRAYNIX TRUTH REGISTRY"));
        assertTrue(aiTruthRegistry.contains("route=TRUTH_REGISTRY"));
        assertTrue(aiDirectNative.contains("AI RUN EXECUTION"));
        assertTrue(aiDirectNative.contains("Command: native status"));
        assertTrue(aiDirectNative.contains("\"status\":\"ok\""));
        assertTrue(aiDirectNative.contains("ULTIMATE IMMORTAL KERNEL"));
        assertTrue(aiRunNative.contains("AI RUN EXECUTION"));
        assertTrue(aiRunNative.contains("Command: native status"));
        assertTrue(aiRunNative.contains("\"schema\":\"fraynix.ai-run.v1\""));
        assertTrue(aiRunNative.contains("\"policyVersion\":\"fraynix.ai-run.policy.v2\""));
        assertTrue(aiRunNative.contains("\"executionId\":\"airun-"));
        assertTrue(aiRunNative.contains("\"status\":\"ok\""));
        assertTrue(aiRunNative.contains("\"timeoutSeconds\":180"));
        assertTrue(aiRunNative.contains("\"outputSha256\":\""));
        assertTrue(aiRunNative.contains("\"outputTruncated\":"));
        assertTrue(aiRunNative.contains("ULTIMATE IMMORTAL KERNEL"));
        assertTrue(aiRunViaRouteApi.contains("AI RUN EXECUTION"));
        assertTrue(aiRunViaRouteApi.contains("Command: native status"));
        assertTrue(aiRunNativeWithInvisiblePrefix.contains("AI RUN EXECUTION"));
        assertTrue(aiRunNativeWithInvisiblePrefix.contains("Command: native status"));
        assertTrue(aiRunBlocked.contains("AI RUN EXECUTION"));
        assertTrue(aiRunBlocked.contains("\"status\":\"blocked\""));
        assertTrue(aiRunBlocked.contains("\"errorCode\":\"POLICY_HEAD_BLOCKED\""));
        assertTrue(aiRunTpu.contains("AI RUN EXECUTION"));
        assertTrue(aiRunTpu.contains("\"head\":\"tpu\""));
        assertTrue(aiRunTpu.contains("\"status\":\"ok\""));
        assertTrue(aiRunTpu.contains("TPU REASONING ENGINE"));
        assertTrue(aiDirectTpu.contains("AI RUN EXECUTION"));
        assertTrue(aiDirectTpu.contains("\"head\":\"tpu\""));
        assertTrue(aiRunTooLarge.contains("AI RUN EXECUTION"));
        assertTrue(aiRunTooLarge.contains("\"status\":\"blocked\""));
        assertTrue(aiRunTooLarge.contains("\"errorCode\":\"POLICY_COMMAND_TOO_LARGE\""));
        assertTrue(aiRunControlChars.contains("AI RUN EXECUTION"));
        assertTrue(aiRunControlChars.contains("\"status\":\"blocked\""));
        assertTrue(aiRunControlChars.contains("\"errorCode\":\"POLICY_COMMAND_CONTROL_CHARS\""));
        assertTrue(aiRunBabelInjection.contains("AI RUN EXECUTION"));
        assertTrue(aiRunBabelInjection.contains("\"status\":\"blocked\""));
        assertTrue(aiRunBabelInjection.contains("\"errorCode\":\"POLICY_BABEL_PAYLOAD_BLOCKED\""));
        assertTrue(aiMenuRoute.contains("Action: ai-unison-plan"));
        assertTrue(aiMenuRoute.contains("Action: ai-unison-run"));
        assertTrue(aiMenuRoute.contains("Action: abstraction-lane"));
        assertTrue(aiMenuRoute.contains("Action: fraynix-menu-bridge"));
        assertTrue(aiLazarusProbe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiLazarusProbe.contains("Result: LAZARUS ACTIVE INFERENCE REVIEW"));
        assertTrue(aiLazarusProbe.contains("selectedAction="));
        assertTrue(aiLazarusProbe.contains("\"route\":\"lazarus_engine.active_inference\""));
        assertTrue(aiOpenClawProbe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiOpenClawProbe.contains("Result: OPENCLAW FORMAL VERIFICATION REVIEW"));
        assertTrue(aiOpenClawProbe.contains("Policy decision code:"));
        assertTrue(aiOpenClawProbe.contains("\"route\":\"openclaw_execution.formal_proof\""));
        assertTrue(aiF0Probe.contains("AI STRUCTURED PROBE"));
        assertTrue(aiF0Probe.contains("Result: F0 ENGINE ADVERSARIAL STRESS REVIEW"));
        assertTrue(aiF0Probe.contains("fisherTrace="));
        assertTrue(aiF0Probe.contains("\"route\":\"f0_engine.adversarial_perturbation\""));
        assertTrue(aiOrchestration.contains("AI PROMPT ORCHESTRATION"));
        assertTrue(aiOrchestration.contains("Action: abstraction-lane"));
        assertTrue(aiOrchestration.contains("Action: ai-unison-plan"));
        assertTrue(aiOrchestration.contains("Action: ai-unison-run"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-plan"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-model-selection"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-lane"));
        assertTrue(aiBiasRoute.contains("Action: abstraction-bias-outcome"));
        assertTrue(abstractionStatus.contains("FRAYNIX ABSTRACTION MEMORY"));
        assertTrue(abstractionStatus.contains("Records:"));
        assertTrue(abstractionStatus.contains("Bias outcomes:"));
        assertTrue(abstractionTail.contains("FRAYNIX ABSTRACTION MEMORY TAIL"));
        assertTrue(abstractionTail.contains("Showing:"));
        assertTrue(abstractionDelta.contains("ABSTRACTION DELTA"));
        assertTrue(aiOrchestration.contains("Action: native-status"));
        assertTrue(aiOrchestration.contains("Action: willow"));
        assertTrue(aiOrchestration.contains("Action: babel-transmute"));
        assertTrue(aiOrchestration.contains("Action: transmudder-proof"));
        assertTrue(export.contains("ULTIMATE EXPORT COMPLETE"));
        assertTrue(shutdown.contains("ULTIMATE IMMORTAL KERNEL SHUTDOWN"));
        assertTrue(manifest.kernelOverlapSectionCount() > 0);
        assertTrue(manifest.priorityCounts().containsKey("critical"));
        assertTrue(manifest.surfaceAgiStrataCounts().containsKey("proof-lane"));
        assertTrue(manifest.sectionAgiStrataCounts().containsKey("kernel-discipline"));
        assertTrue(manifest.livingProofLaneCount() == 5);
        assertTrue(manifest.findLivingProofLane("substrate-transduction").orElseThrow().bareMetalSubstrateLane());
        assertTrue(system.registry().agiStrataCounts().containsKey("operator-shell"));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/ultimate-architecture.md")));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/ultimate-knowledge-graph.json")));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/fraynix-math-canon.md")));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/fraynix-axiom-ledger.md")));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/fraynix-truth-registry.json")));
        assertTrue(Files.exists(tempDir.resolve("workspace-export/fraynix-continuity-memory.md")));
        String workspaceMarkdown = Files.readString(tempDir.resolve("workspace-export/ultimate-architecture.md"));
        String workspaceGraph = Files.readString(tempDir.resolve("workspace-export/ultimate-knowledge-graph.json"));
        String workspaceMath = Files.readString(tempDir.resolve("workspace-export/fraynix-math-canon.md"));
        String workspaceAxiomLedger = Files.readString(tempDir.resolve("workspace-export/fraynix-axiom-ledger.md"));
        String workspaceTruthRegistry = Files.readString(tempDir.resolve("workspace-export/fraynix-truth-registry.json"));
        String workspaceContinuity = Files.readString(tempDir.resolve("workspace-export/fraynix-continuity-memory.md"));
        assertTrue(workspaceMarkdown.contains("Fraymus Nexus // Fraynix Offline AGI Operator Surface"));
        assertTrue(workspaceMarkdown.contains("## AGI Role Summary"));
        assertTrue(workspaceMarkdown.contains("subgraph agi_strata_lanes[\"AGI Strata Lanes\"]"));
        assertTrue(workspaceMath.contains("# Fraynix Math Canon"));
        assertTrue(workspaceMath.contains("## Local Implementation Anchors"));
        assertTrue(workspaceMath.contains("### Deterministic symbolic compiler / VM math"));
        assertTrue(workspaceMath.contains("src/main/java/fraymus/transformer/Tokenizer.java"));
        assertTrue(workspaceAxiomLedger.contains("# Fraynix Axiom Ledger"));
        assertTrue(workspaceTruthRegistry.contains("\"title\": \"Fraynix Truth Registry\""));
        assertTrue(workspaceTruthRegistry.contains("\"resumeRules\""));
        assertTrue(workspaceContinuity.contains("# Fraynix Continuity Memory"));
        assertTrue(workspaceContinuity.contains("## Pickup Order"));
        assertTrue(workspaceGraph.contains("\"surfaceAgiStrata\""));
        assertTrue(workspaceGraph.contains("\"visualSemantics\""));
        assertTrue(workspaceGraph.contains("\"primaryAgiStratum\""));
        assertTrue(workspaceGraph.contains("\"systemsUrl\": \"/systems?focus="));
        assertTrue(workspaceGraph.contains("\"runtimeUrl\": \"/runtime?subsystem="));
        assertTrue(workspaceGraph.contains("\"jumpUrl\": \"/surfaces?surface="));
        assertTrue(workspaceGraph.contains("&section="));
        assertTrue(workspaceGraph.contains("\"routeUrl\": \"/launchpad?cmd="));
    }

    private static String sampleFraynixBootSource() {
        return """
            class FraynixBoot {
                void boot(String arg) {
                    switch (arg) {
                        case "dreamforge", "deepdream" -> {
                            System.out.println("Dream forge online");
                        }
                        case "portalx" -> {
                            Portal.main(new String[0]);
                        }
                        case "deepscan" -> {
                            new ProbeShell().startREPL();
                        }
                    }
                }
            }
            """;
    }

    private static UltimateSelfBuildHarness.ProofBatteryRunner passingProofBattery(String label) {
        return (proofRoot, evidencePath, targetLabel, goal) -> {
            try {
                Files.createDirectories(proofRoot);
                Path transcript = proofRoot.resolve("stub-proof-" + label + ".txt");
                Files.writeString(transcript, "stub proof for " + targetLabel + " using " + evidencePath, java.nio.charset.StandardCharsets.UTF_8);
                List<UltimateSelfBuildHarness.ProofCheckResult> checks = List.of(
                    new UltimateSelfBuildHarness.ProofCheckResult("system-proof", "System proof", true, "stub system proof", transcript),
                    new UltimateSelfBuildHarness.ProofCheckResult("fraynix-proof", "Fraynix proof", true, "stub fraynix proof", transcript),
                    new UltimateSelfBuildHarness.ProofCheckResult("willow-proof", "Willow quantum proof", true, "stub willow proof", transcript),
                    new UltimateSelfBuildHarness.ProofCheckResult("transmudder-proof", "Transmudder soul proof", true, "stub transmudder proof", transcript)
                );
                return new UltimateSelfBuildHarness.ProofBatteryResult(proofRoot, evidencePath, checks, false, "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Path extractReportedPath(String output, String label) {
        for (String line : output.split("\\R")) {
            if (line.startsWith(label)) {
                String raw = line.substring(label.length()).trim();
                if (raw.isBlank() || "(none)".equals(raw)) {
                    throw new AssertionError("Expected concrete path for " + label + " in output: " + output);
                }
                return Path.of(raw);
            }
        }
        throw new AssertionError("Missing " + label + " in output: " + output);
    }

    private static String extractValue(String output, String label) {
        for (String line : output.split("\\R")) {
            if (line.startsWith(label)) {
                String raw = line.substring(label.length()).trim();
                if (raw.isBlank() || "(none)".equals(raw)) {
                    throw new AssertionError("Expected concrete value for " + label + " in output: " + output);
                }
                return raw;
            }
        }
        throw new AssertionError("Missing " + label + " in output: " + output);
    }
}
