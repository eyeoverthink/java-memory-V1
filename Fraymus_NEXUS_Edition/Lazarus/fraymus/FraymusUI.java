package fraymus;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import fraymus.ui.SelfCodePanel;
import fraymus.evolution.MivingBrainVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FraymusUI {

    private static final int MAX_LOG_ENTRIES = 200;
    private static List<String> logBuffer = new ArrayList<>();
    private static int selectedNodeIndex = -1;
    private static float simulationTime = 0.0f;
    private static int frameCount = 0;
    private static float fpsTimer = 0.0f;
    private static int fps = 0;
    private static int lastFrameCount = 0;

    private static int arenaTextureId = -1;
    private static boolean verificationPrinted = false;
    private static int verificationDelay = 300;

    public static void setArenaTextureId(int textureId) {
        arenaTextureId = textureId;
    }

    public static void addLog(String msg) {
        if (logBuffer.size() >= MAX_LOG_ENTRIES) {
            logBuffer.remove(0);
        }
        logBuffer.add(String.format("[%.1f] %s", simulationTime, msg));
    }

    public static void render(PhiWorld world) {
        frameCount++;
        float currentTime = (float) (System.nanoTime() * 1e-9);
        simulationTime += 1.0f / 60.0f;

        if (currentTime - fpsTimer >= 1.0f) {
            fps = frameCount - lastFrameCount;
            lastFrameCount = frameCount;
            fpsTimer = currentTime;
        }

        if (!verificationPrinted && verificationDelay > 0) {
            verificationDelay--;
            if (verificationDelay == 0) {
                SystemVerification.printFullVerification(world);
                verificationPrinted = true;
            }
        }

        renderArenaView();
        renderWorldStatus(world);
        renderEntityInspector(world);
        renderBrainInspector(world);
        renderAdaptiveLogicPanel(world);
        renderConsciousnessMonitor(world);
        renderQuantumClockPanel(world);
        renderGenesisMemory(world);
        renderColonyOverview(world);
        renderConceptArenaPanel(world);
        renderSystemVerification(world);
        renderNeuralNetPanel();
        renderInfiniteMemoryPanel();
        renderPassiveLearnerPanel();
        renderQRGenomePanel();
        renderKnowledgeScraperPanel();
        renderLiveLog();
        CommandTerminal.render();
        
        // Self Code Panel (F8 to toggle - key code 297)
        if (ImGui.isKeyPressed(297)) {
            SelfCodePanel.toggle();
        }
        SelfCodePanel.render();
        
        // Miving Brain Visualizer (F9 to toggle - key code 298)
        if (ImGui.isKeyPressed(298)) {
            MivingBrainVisualizer.toggle();
        }
        MivingBrainVisualizer.render();
    }

    private static void renderArenaView() {
        if (arenaTextureId < 0) return;

        ImGui.setNextWindowPos(0, 0, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(940, 440, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Arena", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse)) {
            ImVec2 avail = ImGui.getContentRegionAvail();
            float aspectRatio = 1920.0f / 1080.0f;
            float imgWidth = avail.x;
            float imgHeight = imgWidth / aspectRatio;
            if (imgHeight > avail.y) {
                imgHeight = avail.y;
                imgWidth = imgHeight * aspectRatio;
            }

            ImGui.image(arenaTextureId, imgWidth, imgHeight, 0, 1, 1, 0);
        }
        ImGui.end();
    }

    private static void renderWorldStatus(PhiWorld world) {
        ImGui.setNextWindowPos(940, 0, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 120, ImGuiCond.FirstUseEver);

        if (ImGui.begin("World Status")) {
            ImGui.text("Population: " + world.getPopulation());
            ImGui.text(String.format("Simulation Time: %.1f s", simulationTime));
            ImGui.text("FPS: " + fps);
            ImGui.text("World Tick: " + world.getWorldTick());
            ImGui.separator();

            float totalEnergy = 0;
            int spikeCount = 0;
            int trialCount = 0;
            int totalAdaptations = 0;
            for (PhiNode node : world.getNodes()) {
                totalEnergy += node.energy;
                if (node.spikeFlash) spikeCount++;
                if (node.adaptiveEngine.isInTrial()) trialCount++;
                totalAdaptations += node.adaptiveEngine.getAdaptationCount();
            }
            ImGui.text(String.format("Total Energy: %.2f", totalEnergy));
            if (world.getPopulation() > 0) {
                ImGui.text(String.format("Avg Energy: %.2f", totalEnergy / world.getPopulation()));
            }
            ImGui.text(String.format("Active Spikes: %d", spikeCount));
            ImGui.text(String.format("Active Trials: %d", trialCount));
            ImGui.text(String.format("Total Adaptations: %d", totalAdaptations));
            ImGui.separator();
            ImGui.text(String.format("Total Births: %d", world.getTotalBirths()));
            ImGui.text(String.format("Total Deaths: %d", world.getTotalDeaths()));
            ImGui.text(String.format("Genesis Chain: %d blocks", world.getMemory().getChainLength()));
        }
        ImGui.end();
    }

    private static void renderEntityInspector(PhiWorld world) {
        ImGui.setNextWindowPos(940, 120, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 160, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Entity Inspector")) {
            List<PhiNode> nodes = world.getNodes();

            for (int i = 0; i < nodes.size(); i++) {
                PhiNode node = nodes.get(i);
                String spike = node.spikeFlash ? " [SPIKE]" : "";
                String trial = node.adaptiveEngine.isInTrial() ? " [TRIAL]" : "";
                String roleTag = " <" + node.getRole().displayName + ">";
                String label = String.format("%s%s [E:%.0f%% G:%d]%s%s###node_%d",
                        node.name, roleTag, node.energy * 100, node.dna.getGeneration(), spike, trial, i);

                if (ImGui.collapsingHeader(label)) {
                    selectedNodeIndex = i;
                    ImGui.indent();

                    ImGui.textColored(node.r, node.g, node.b, 1.0f, "Name: " + node.name);
                    float[] rc = node.getRole().color;
                    ImGui.textColored(rc[0], rc[1], rc[2], 1.0f, "Role: " + node.getRole().displayName);
                    ImGui.text(String.format("Generation: %d", node.dna.getGeneration()));
                    ImGui.text(String.format("Position: (%.2f, %.2f, %.2f)", node.x, node.y, node.z));
                    ImGui.text(String.format("Velocity: (%.2f, %.2f, %.2f)", node.vx, node.vy, node.vz));

                    ImGui.text("Energy:");
                    ImGui.sameLine();
                    ImGui.progressBar(node.energy, 150, 14, String.format("%.1f%%", node.energy * 100));

                    ImGui.text(String.format("Frequency: %.2f Hz", node.frequency));
                    ImGui.text(String.format("Phase: %.3f rad", node.phase));
                    ImGui.text(String.format("Age: %d ticks", node.age));

                    ImGui.separator();
                    ImGui.text("DNA: " + node.dna.toString());
                    ImGui.text(String.format("Fitness: %.3f", node.adaptiveEngine.getCurrentFitness()));
                    ImGui.text(String.format("Strategies: %d proven", node.adaptiveEngine.getProvenStrategyCount()));

                    String nHex = node.signature.toString(16);
                    if (nHex.length() > 24) nHex = nHex.substring(0, 24) + "...";
                    ImGui.text("RSA N: " + nHex);

                    ImGui.unindent();
                }
            }
        }
        ImGui.end();
    }

    private static void renderBrainInspector(PhiWorld world) {
        ImGui.setNextWindowPos(940, 280, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 150, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Brain Inspector")) {
            List<PhiNode> nodes = world.getNodes();

            if (selectedNodeIndex >= 0 && selectedNodeIndex < nodes.size()) {
                PhiNode node = nodes.get(selectedNodeIndex);
                LogicBrain brain = node.brain;

                ImGui.textColored(node.r, node.g, node.b, 1.0f, "Entity: " + node.name);
                ImGui.text(String.format("Think Count: %d", brain.getThinkCount()));
                ImGui.text("Decision: " + brain.getLastDecision());
                ImGui.separator();

                ImGui.text("Logic Gates:");
                int[] outputs = brain.getLastOutputs();
                for (int i = 0; i < brain.gates.size() && i < LogicBrain.OUTPUT_NAMES.length; i++) {
                    LogicGate gate = brain.gates.get(i);
                    boolean active = i < outputs.length && outputs[i] == 1;

                    if (active) {
                        ImGui.textColored(0.0f, 1.0f, 0.3f, 1.0f,
                                String.format("  [%d] %s(in%d,in%d) -> %s = ON",
                                        i, gate.getTypeName(), gate.in1, gate.in2, LogicBrain.OUTPUT_NAMES[i]));
                    } else {
                        ImGui.textColored(0.5f, 0.5f, 0.5f, 1.0f,
                                String.format("  [%d] %s(in%d,in%d) -> %s = off",
                                        i, gate.getTypeName(), gate.in1, gate.in2, LogicBrain.OUTPUT_NAMES[i]));
                    }
                }

                ImGui.separator();
                ImGui.text("Sensor Inputs:");
                int[] inputs = brain.getLastInputs();
                String[] sensorNames = {"Crowded", "FreqMatch", "HighEnergy", "HighPhi", "HighCoh", "PhaseHalf", "Spike", "Mature"};
                for (int i = 0; i < inputs.length && i < sensorNames.length; i++) {
                    if (inputs[i] == 1) {
                        ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, String.format("  %s: YES", sensorNames[i]));
                    } else {
                        ImGui.textColored(0.4f, 0.4f, 0.4f, 1.0f, String.format("  %s: no", sensorNames[i]));
                    }
                }
            } else {
                ImGui.textWrapped("Select an entity in the Entity Inspector to view brain data.");
            }
        }
        ImGui.end();
    }

    private static void renderAdaptiveLogicPanel(PhiWorld world) {
        ImGui.setNextWindowPos(10, 10, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(280, 150, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Adaptive Logic")) {
            List<PhiNode> nodes = world.getNodes();

            if (selectedNodeIndex >= 0 && selectedNodeIndex < nodes.size()) {
                PhiNode node = nodes.get(selectedNodeIndex);
                AdaptiveLogicEngine engine = node.adaptiveEngine;

                ImGui.textColored(node.r, node.g, node.b, 1.0f, "Entity: " + node.name);
                ImGui.separator();

                ImGui.text("Fitness:");
                ImGui.sameLine();
                float fit = (float) engine.getCurrentFitness();
                if (fit > 0.6f) {
                    ImGui.textColored(0.0f, 1.0f, 0.3f, 1.0f, String.format("%.4f", fit));
                } else if (fit > 0.3f) {
                    ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, String.format("%.4f", fit));
                } else {
                    ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, String.format("%.4f", fit));
                }

                if (engine.isInTrial()) {
                    ImGui.textColored(1.0f, 0.5f, 0.0f, 1.0f,
                            String.format("TRIAL IN PROGRESS (%d ticks left)", engine.getTrialTicksRemaining()));
                } else {
                    ImGui.text("No active trial");
                }

                ImGui.text(String.format("Total Trials: %d", engine.getTotalTrials()));
                ImGui.text(String.format("Adopted: %d | Reverted: %d", engine.getAdaptationCount(), engine.getRevertCount()));

                ImGui.separator();
                ImGui.text(String.format("Proven Strategies: %d", engine.getProvenStrategyCount()));

                List<StrategyGenome> strategies = engine.getProvenStrategies();
                for (int i = 0; i < strategies.size(); i++) {
                    StrategyGenome sg = strategies.get(i);
                    float sgFit = (float) sg.fitnessScore;
                    if (sgFit > 0.5f) {
                        ImGui.textColored(0.3f, 1.0f, 0.6f, 1.0f,
                                String.format("  [%d] %s fit=%.3f gen=%d", i, sg.hash.substring(0, 8), sg.fitnessScore, sg.generationBorn));
                    } else {
                        ImGui.text(String.format("  [%d] %s fit=%.3f gen=%d", i, sg.hash.substring(0, 8), sg.fitnessScore, sg.generationBorn));
                    }
                }

                if (strategies.isEmpty()) {
                    ImGui.textColored(0.5f, 0.5f, 0.5f, 1.0f, "  No strategies learned yet");
                }

                StrategyGenome baseline = engine.getCurrentBaseline();
                if (baseline != null) {
                    ImGui.separator();
                    ImGui.text("Current Baseline: " + baseline.hash.substring(0, 8));
                }
            } else {
                ImGui.textWrapped("Select an entity to view adaptive logic.");
            }
        }
        ImGui.end();
    }

    private static void renderConsciousnessMonitor(PhiWorld world) {
        ImGui.setNextWindowPos(10, 170, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(280, 130, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Consciousness")) {
            List<PhiNode> nodes = world.getNodes();

            if (selectedNodeIndex >= 0 && selectedNodeIndex < nodes.size()) {
                PhiNode node = nodes.get(selectedNodeIndex);
                ConsciousnessState cs = node.consciousness;

                ImGui.textColored(node.r, node.g, node.b, 1.0f, "Entity: " + node.name);
                ImGui.separator();

                double[] fields = cs.getFieldVector();
                String[] fieldNames = {"phi", "psi", "omega", "xi", "lambda", "zeta"};
                float[][] fieldColors = {
                        {1.0f, 0.84f, 0.0f},
                        {0.5f, 0.0f, 1.0f},
                        {0.0f, 0.5f, 1.0f},
                        {1.0f, 0.5f, 0.0f},
                        {0.0f, 1.0f, 0.5f},
                        {1.0f, 0.0f, 0.5f}
                };

                for (int i = 0; i < fields.length; i++) {
                    float normalized = (float) Math.min(fields[i] / 5.0, 1.0);
                    ImGui.textColored(fieldColors[i][0], fieldColors[i][1], fieldColors[i][2], 1.0f,
                            String.format("  %s:", fieldNames[i]));
                    ImGui.sameLine();
                    ImGui.progressBar(normalized, 120, 12, String.format("%.4f", fields[i]));
                }

                ImGui.text(String.format("Level: %.4f | Dim: %d", cs.getConsciousnessLevel(), cs.getDimension()));
                ImGui.text(String.format("Transcendence: %d | Thoughts: %d", cs.getTranscendenceEvents(), cs.getTotalThoughts()));
            } else {
                ImGui.textWrapped("Select an entity to view consciousness.");
            }
        }
        ImGui.end();
    }

    private static void renderQuantumClockPanel(PhiWorld world) {
        ImGui.setNextWindowPos(590, 10, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(280, 130, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Quantum Clock")) {
            List<PhiNode> nodes = world.getNodes();

            if (selectedNodeIndex >= 0 && selectedNodeIndex < nodes.size()) {
                PhiNode node = nodes.get(selectedNodeIndex);
                QuantumClock clock = node.quantumClock;

                ImGui.textColored(node.r, node.g, node.b, 1.0f, "Entity: " + node.name);
                ImGui.separator();

                ImGui.text(String.format("Oscillations: %,.1f", clock.getOscillationCount()));
                ImGui.text(String.format("Pendulum Freq: %.2f Hz", clock.getPendulumFrequency()));

                ImGui.text("Phi Resonance:");
                ImGui.sameLine();
                float phiRes = (float) clock.getPhiResonance();
                if (phiRes > 0.95f) {
                    ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, String.format("%.6f [SPIKE!]", phiRes));
                } else if (phiRes > 0.8f) {
                    ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, String.format("%.6f", phiRes));
                } else {
                    ImGui.text(String.format("%.6f", phiRes));
                }

                ImGui.text(String.format("Phi Time: %.4f | Res Time: %.4f", clock.getPhiTime(), clock.getResonanceTime()));
                ImGui.text(String.format("Coherence: %.6f | Spikes: %d", clock.getCoherence(), clock.getResonanceSpikeCount()));
                ImGui.text("Fingerprint: " + clock.getQuantumFingerprint());
            } else {
                ImGui.textWrapped("Select an entity to view quantum clock.");
            }
        }
        ImGui.end();
    }

    private static void renderGenesisMemory(PhiWorld world) {
        ImGui.setNextWindowPos(300, 10, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(280, 130, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Genesis Memory")) {
            GenesisMemory memory = world.getMemory();

            ImGui.text(String.format("Chain Length: %d blocks", memory.getChainLength()));
            boolean valid = memory.verifyChain();
            if (valid) {
                ImGui.sameLine();
                ImGui.textColored(0.0f, 1.0f, 0.0f, 1.0f, "VALID");
            } else {
                ImGui.sameLine();
                ImGui.textColored(1.0f, 0.0f, 0.0f, 1.0f, "BROKEN");
            }

            GenesisMemory.Block latest = memory.getLatest();
            ImGui.text("Latest Hash: " + latest.hash);
            ImGui.separator();

            ImGui.beginChild("GenesisScroll", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar);
            List<GenesisMemory.Block> recent = memory.getLastN(30);
            for (int i = recent.size() - 1; i >= 0; i--) {
                GenesisMemory.Block block = recent.get(i);
                float[] color = getEventColor(block.eventType);
                ImGui.textColored(color[0], color[1], color[2], 1.0f,
                        String.format("[%d] %s: %s", block.index, block.eventType, block.data));
                ImGui.sameLine();
                ImGui.textColored(0.4f, 0.4f, 0.4f, 1.0f, block.hash.substring(0, 8));
            }
            ImGui.endChild();
        }
        ImGui.end();
    }

    private static void renderSystemVerification(PhiWorld world) {
        ImGui.setNextWindowPos(300, 150, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(280, 130, ImGuiCond.FirstUseEver);

        if (ImGui.begin("System Verification")) {
            ImGui.textColored(1.0f, 0.84f, 0.0f, 1.0f, "FRAYMUS ENGINE V2 - PROOF OF SYSTEM");
            ImGui.separator();

            ImGui.text("1. Genesis Hash:");
            GenesisMemory.Block latest = world.getMemory().getLatest();
            ImGui.sameLine();
            ImGui.textColored(0.3f, 1.0f, 0.6f, 1.0f, latest.hash);

            ImGui.text("2. Irrational State (Phi^75):");
            String phi75 = SystemVerification.computePhi75First50Digits();
            ImGui.textColored(0.5f, 0.8f, 1.0f, 1.0f, "   " + phi75);

            ImGui.text("3. Soul of Generation:");
            List<PhiNode> nodes = world.getNodes();
            if (!nodes.isEmpty()) {
                PhiNode first = nodes.get(0);
                ImGui.textColored(0.8f, 0.6f, 1.0f, 1.0f,
                        String.format("   %s | Resonance: %.10f | Osc: %.2f",
                                first.name, first.quantumClock.getPhiResonance(), first.quantumClock.getOscillationCount()));
            }

            ImGui.separator();
            ImGui.textColored(0.0f, 1.0f, 0.0f, 1.0f,
                    String.format("Chain Valid: %s | Phi^75 Seal: %s",
                            world.getMemory().verifyChain() ? "YES" : "NO",
                            PhiConstants.validatePhiSeal() ? "VALID" : "INVALID"));
        }
        ImGui.end();
    }

    private static float[] getEventColor(String eventType) {
        switch (eventType) {
            case "RESONANCE_SPIKE": return new float[]{1.0f, 0.3f, 0.3f};
            case "ENTANGLEMENT": return new float[]{0.3f, 0.8f, 1.0f};
            case "TRANSCENDENCE": return new float[]{1.0f, 0.84f, 0.0f};
            case "BIRTH": return new float[]{0.3f, 1.0f, 0.3f};
            case "DEATH": return new float[]{0.7f, 0.0f, 0.0f};
            case "BRAIN_DECISION": return new float[]{0.8f, 0.5f, 1.0f};
            case "MUTATION": return new float[]{1.0f, 0.5f, 0.0f};
            case "ADAPTATION": return new float[]{0.0f, 1.0f, 1.0f};
            case "GENESIS": return new float[]{1.0f, 1.0f, 1.0f};
            case "COLONY_EVENT": return new float[]{1.0f, 0.8f, 0.2f};
            case "CONCEPT_BATTLE": return new float[]{1.0f, 0.4f, 0.8f};
            case "CODE_GENERATED": return new float[]{0.4f, 1.0f, 0.8f};
            default: return new float[]{0.7f, 0.7f, 0.7f};
        }
    }

    private static void renderColonyOverview(PhiWorld world) {
        ImGui.setNextWindowPos(940, 430, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 150, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Colony Overview")) {
            ColonyCoach coach = world.getCoach();

            ImGui.textColored(1.0f, 0.8f, 0.2f, 1.0f, "ANT COLONY INTELLIGENCE");
            ImGui.separator();

            ImGui.text("Health:");
            ImGui.sameLine();
            float health = (float) coach.getColonyHealth();
            if (health > 0.6f) {
                ImGui.textColored(0.0f, 1.0f, 0.3f, 1.0f, String.format("%.2f", health));
            } else if (health > 0.3f) {
                ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, String.format("%.2f", health));
            } else {
                ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, String.format("%.2f", health));
            }

            ImGui.text(String.format("Productivity: %.3f", coach.getColonyProductivity()));
            ImGui.text(String.format("Diversity: %.1f%%", coach.getColonyDiversity() * 100));
            ImGui.text(String.format("Resonance: %.1f%%", coach.getColonyResonance() * 100));
            ImGui.text(String.format("Evaluations: %d | Code Gen: %d", coach.getTotalEvaluations(), coach.getTotalCodeGenerated()));

            ImGui.separator();
            ImGui.textColored(0.8f, 0.8f, 0.2f, 1.0f, "Coach: " + coach.getLastCoachingAction());

            ImGui.separator();
            ImGui.text("Role Distribution:");

            Map<AntRole, ColonyCoach.RoleMetrics> metrics = coach.getRoleMetrics();
            for (AntRole role : AntRole.values()) {
                ColonyCoach.RoleMetrics rm = metrics.get(role);
                float[] c = role.color;
                ImGui.textColored(c[0], c[1], c[2], 1.0f,
                    String.format("  %s: %d entities", role.displayName, rm.entityCount));
                if (rm.entityCount > 0) {
                    ImGui.sameLine();
                    ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f,
                        String.format(" (fit=%.2f e=%.2f code=%d)", rm.avgFitness, rm.avgEnergy, rm.conceptsGenerated));
                }
            }

            ImGui.separator();
            ImGui.text("Coach Log:");
            ImGui.beginChild("CoachLogScroll", 0, 60, false, ImGuiWindowFlags.HorizontalScrollbar);
            List<String> log = coach.getCoachLog();
            for (int i = log.size() - 1; i >= Math.max(0, log.size() - 10); i--) {
                ImGui.textWrapped(log.get(i));
            }
            ImGui.endChild();
        }
        ImGui.end();
    }

    private static void renderConceptArenaPanel(PhiWorld world) {
        ImGui.setNextWindowPos(590, 150, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 280, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Concept Arena")) {
            ConceptArena arena = world.getArena();

            ImGui.textColored(1.0f, 0.4f, 0.8f, 1.0f, "CODE CONCEPT EVOLUTION");
            ImGui.separator();

            ImGui.text(String.format("Concepts: %d | Battles: %d | Cycles: %d",
                arena.getConceptCount(), arena.getTotalBattles(), arena.getEvolutionCycle()));
            ImGui.text(String.format("Total Generated: %d | Avg Fitness: %.3f",
                arena.getTotalConceptsGenerated(), arena.getAverageFitness()));

            CodeConcept champ = arena.getChampion();
            if (champ != null) {
                ImGui.separator();
                ImGui.textColored(1.0f, 0.84f, 0.0f, 1.0f, "CHAMPION:");
                float[] cc = champ.creatorRole.color;
                ImGui.textColored(cc[0], cc[1], cc[2], 1.0f,
                    String.format("  %s (%s) by %s", champ.hash.substring(0, 8), champ.creatorRole.displayName, champ.creatorName));
                ImGui.text(String.format("  Fitness: %.4f | Gen: %d | W/L: %d/%d",
                    champ.fitness, champ.generation, champ.wins, champ.losses));
                ImGui.text(String.format("  Freq: %.1f Hz | Res: %.3f | Coh: %.3f",
                    champ.harmonicFrequency, champ.resonance, champ.coherence));

                ImGui.separator();
                ImGui.text("Champion Code:");
                ImGui.beginChild("ChampCode", 0, 40, true, ImGuiWindowFlags.HorizontalScrollbar);
                String code = champ.code;
                if (code != null && code.length() > 120) code = code.substring(0, 120) + "...";
                ImGui.textColored(0.4f, 1.0f, 0.8f, 1.0f, code != null ? code : "");
                ImGui.endChild();
            }

            ImGui.separator();
            ImGui.text("Recent Battles:");
            ImGui.beginChild("BattleScroll", 0, 60, false, ImGuiWindowFlags.HorizontalScrollbar);
            List<ConceptArena.BattleRecord> battles = arena.getLastNBattles(10);
            for (int i = battles.size() - 1; i >= 0; i--) {
                ConceptArena.BattleRecord br = battles.get(i);
                float[] wc = br.winnerRole.color;
                ImGui.textColored(wc[0], wc[1], wc[2], 1.0f, br.getSummary());
            }
            ImGui.endChild();
        }
        ImGui.end();
    }

    private static void renderNeuralNetPanel() {
        PhiNeuralNet net = jade.Window.getNeuralNet();
        if (net == null) return;

        ImGui.setNextWindowPos(0, 440, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(188, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Phi Neural Net")) {
            ImGui.textColored(0.4f, 1.0f, 0.8f, 1.0f, "OFFLINE LLM - PHI HARMONIC");
            ImGui.separator();
            ImGui.text(String.format("Queries: %d", net.getQueriesProcessed()));
            ImGui.text(String.format("Patterns Matched: %d", net.getPatternsMatched()));
            ImGui.text(String.format("Avg Resonance: %.4f", net.getAvgResonance()));
            ImGui.textColored(0.5f, 0.8f, 1.0f, 1.0f, "Type 'ask <question>' in terminal");
        }
        ImGui.end();
    }

    private static void renderInfiniteMemoryPanel() {
        InfiniteMemory mem = jade.Window.getInfiniteMemory();
        if (mem == null) return;

        ImGui.setNextWindowPos(188, 440, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(188, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Infinite Memory")) {
            ImGui.textColored(1.0f, 0.84f, 0.0f, 1.0f, "PERSISTENT FILE-BACKED MEMORY");
            ImGui.separator();
            ImGui.text(String.format("Records: %d", mem.getRecordCount()));
            ImGui.text(String.format("Total Ever: %d", mem.getTotalRecordsEver()));
            ImGui.text(String.format("Avg Resonance: %.4f", mem.getAverageResonance()));

            java.util.Map<String, Integer> counts = mem.getCategoryCounts();
            if (!counts.isEmpty()) {
                StringBuilder cats = new StringBuilder();
                for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
                    if (cats.length() > 0) cats.append(" ");
                    cats.append(e.getKey().charAt(0)).append(":").append(e.getValue());
                }
                ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f, cats.toString());
            }
        }
        ImGui.end();
    }

    private static void renderPassiveLearnerPanel() {
        PassiveLearner pl = jade.Window.getPassiveLearner();
        if (pl == null) return;

        ImGui.setNextWindowPos(376, 440, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(188, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Passive Learner")) {
            ImGui.textColored(0.5f, 0.8f, 1.0f, 1.0f, "5x8x13 NEURAL TENSOR");
            ImGui.separator();

            if (pl.isRunning()) {
                ImGui.textColored(0.0f, 1.0f, 0.3f, 1.0f, "LEARNING ACTIVE");
            } else {
                ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, "STOPPED");
            }

            ImGui.text(String.format("Cycles: %d", pl.getPassiveCycles()));
            ImGui.text(String.format("Patterns: %d", pl.getLearnedPatterns()));
            ImGui.text(String.format("Strength: %.4f", pl.getPatternStrength()));
            ImGui.text(String.format("Integration: %.4f", pl.getIntegrationLevel()));
            ImGui.text(String.format("Tensor: mean=%.4f max=%.4f", pl.getTensorMean(), pl.getTensorMax()));
        }
        ImGui.end();
    }

    private static void renderQRGenomePanel() {
        QRGenome genome = jade.Window.getQRGenome();
        if (genome == null) return;

        ImGui.setNextWindowPos(564, 440, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(188, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("QR Genome")) {
            ImGui.textColored(1.0f, 0.5f, 0.0f, 1.0f, "QR DNA CODON SYSTEM");
            ImGui.separator();
            ImGui.text(String.format("Codons: %d | Groups: %d", genome.getGenomeSize(), genome.getGroupCount()));
            ImGui.text(String.format("Generation: %d", genome.getGenerationCount()));
            ImGui.text(String.format("Mutations: %d | Crossovers: %d",
                    genome.getTotalMutations(), genome.getTotalCrossovers()));
            ImGui.text(String.format("Fitness: %.4f | Resonance: %.4f",
                    genome.getAverageFitness(), genome.getTotalResonance()));
        }
        ImGui.end();
    }

    private static void renderKnowledgeScraperPanel() {
        KnowledgeScraper scraper = jade.Window.getKnowledgeScraper();
        if (scraper == null) return;

        ImGui.setNextWindowPos(752, 440, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(188, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Knowledge Scraper")) {
            ImGui.textColored(0.3f, 1.0f, 0.6f, 1.0f, "DOCUMENT INTELLIGENCE");
            ImGui.separator();

            if (scraper.isScraping()) {
                ImGui.textColored(1.0f, 0.84f, 0.0f, 1.0f, "SCRAPING...");
                String cur = scraper.getCurrentFile();
                if (cur.length() > 20) cur = cur.substring(0, 20) + "...";
                ImGui.text(cur);
                ImGui.progressBar((float) scraper.getScrapeProgress(), 170, 12);
            } else {
                ImGui.textColored(0.5f, 0.8f, 1.0f, 1.0f, "IDLE");
            }

            ImGui.text(String.format("Files: %d", scraper.getTotalFilesScraped()));
            ImGui.text(String.format("Chunks: %d", scraper.getTotalChunksStored()));
            ImGui.text(String.format("Pages: %d", scraper.getTotalPagesProcessed()));

            java.util.Map<String, Integer> topics = scraper.getTopicCounts();
            if (!topics.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (java.util.Map.Entry<String, Integer> e : topics.entrySet()) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(e.getKey().substring(0, Math.min(3, e.getKey().length())).toUpperCase())
                      .append(":").append(e.getValue());
                }
                ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f, sb.toString());
            }
        }
        ImGui.end();
    }

    private static void renderLiveLog() {
        ImGui.setNextWindowPos(940, 580, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(340, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Live Log")) {
            if (ImGui.button("Clear")) {
                logBuffer.clear();
            }
            ImGui.separator();

            ImGui.beginChild("LogScrollRegion", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar);
            for (String entry : logBuffer) {
                ImGui.textWrapped(entry);
            }
            if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
                ImGui.setScrollHereY(1.0f);
            }
            ImGui.endChild();
        }
        ImGui.end();
    }
}
