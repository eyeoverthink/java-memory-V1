package fraymus;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class CommandTerminal {

    private static final int MAX_OUTPUT_LINES = 500;
    private static final ImString inputBuffer = new ImString(256);
    private static final List<TerminalLine> outputLines = new ArrayList<>();
    private static boolean scrollToBottom = true;
    private static boolean focusInput = true;
    private static final List<String> commandHistory = new ArrayList<>();
    private static int historyIndex = -1;

    private static ExperimentManager experimentManager;

    public static class TerminalLine {
        public final String text;
        public final float r, g, b;

        public TerminalLine(String text, float r, float g, float b) {
            this.text = text;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public static void init(ExperimentManager mgr) {
        experimentManager = mgr;
        printBanner();
    }

    private static void printBanner() {
        printColored("========================================================", 0.4f, 1.0f, 0.8f);
        printColored("  FRAYMUS ENGINE V2 - LIVING INTELLIGENCE TERMINAL", 1.0f, 0.84f, 0.0f);
        printColored("  Type 'help' for available commands", 0.6f, 0.6f, 0.6f);
        printColored("========================================================", 0.4f, 1.0f, 0.8f);
        print("");
    }

    public static void print(String text) {
        printColored(text, 0.8f, 0.8f, 0.8f);
    }

    public static void printColored(String text, float r, float g, float b) {
        outputLines.add(new TerminalLine(text, r, g, b));
        if (outputLines.size() > MAX_OUTPUT_LINES) {
            outputLines.remove(0);
        }
        scrollToBottom = true;
    }

    public static void printSuccess(String text) {
        printColored(text, 0.3f, 1.0f, 0.3f);
    }

    public static void printError(String text) {
        printColored(text, 1.0f, 0.3f, 0.3f);
    }

    public static void printInfo(String text) {
        printColored(text, 0.5f, 0.8f, 1.0f);
    }

    public static void printHighlight(String text) {
        printColored(text, 1.0f, 0.84f, 0.0f);
    }

    public static void render() {
        ImGui.setNextWindowPos(0, 580, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(940, 140, ImGuiCond.FirstUseEver);

        if (ImGui.begin("Terminal", ImGuiWindowFlags.NoScrollbar)) {
            float footerHeight = ImGui.getFrameHeightWithSpacing() + 4;
            ImGui.beginChild("TermOutput", 0, -footerHeight, false,
                    ImGuiWindowFlags.HorizontalScrollbar);

            for (TerminalLine line : outputLines) {
                ImGui.textColored(line.r, line.g, line.b, 1.0f, line.text);
            }

            if (scrollToBottom) {
                ImGui.setScrollHereY(1.0f);
                scrollToBottom = false;
            }
            ImGui.endChild();

            ImGui.separator();

            ImGui.textColored(0.4f, 1.0f, 0.8f, 1.0f, ">");
            ImGui.sameLine();

            ImGui.pushItemWidth(-1);
            int flags = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackHistory;
            if (focusInput) {
                ImGui.setKeyboardFocusHere();
                focusInput = false;
            }
            if (ImGui.inputText("##terminput", inputBuffer, flags)) {
                String cmd = inputBuffer.get().trim();
                if (!cmd.isEmpty()) {
                    printColored("> " + cmd, 0.4f, 1.0f, 0.8f);
                    commandHistory.add(cmd);
                    historyIndex = commandHistory.size();
                    executeCommand(cmd);
                }
                inputBuffer.set("");
                focusInput = true;
            }
            ImGui.popItemWidth();
        }
        ImGui.end();
    }

    private static void executeCommand(String cmd) {
        String[] parts = cmd.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "help":
                showHelp();
                break;
            case "status":
                showStatus();
                break;
            case "prime":
                handlePrime(args);
                break;
            case "factor":
                handleFactor(args);
                break;
            case "hash":
                handleHash(args);
                break;
            case "crack":
                handleCrack(args);
                break;
            case "tunnel":
                handleTunnel(args);
                break;
            case "spawn":
                handleSpawn(args);
                break;
            case "boost":
                handleBoost(args);
                break;
            case "kill":
                handleKill(args);
                break;
            case "mutate":
                handleMutate(args);
                break;
            case "evolve":
                handleEvolve(args);
                break;
            case "arena":
                handleArena(args);
                break;
            case "codegen":
                handleCodegen(args);
                break;
            case "rsa":
                handleRsa(args);
                break;
            case "identity":
                handleIdentity(args);
                break;
            case "physics":
                handlePhysics(args);
                break;
            case "nodes":
                showNodes();
                break;
            case "colony":
                showColony();
                break;
            case "ask":
                handleAsk(args);
                break;
            case "learn":
                handleLearn(args);
                break;
            case "memory":
                handleMemory(args);
                break;
            case "genome":
                handleGenome(args);
                break;
            case "qrcode":
                handleQRCode(args);
                break;
            case "scrape":
                handleScrape(args);
                break;
            case "ethics":
                handleEthics(args);
                break;
            case "fragment":
                handleFragment(args);
                break;
            case "porh":
                handlePoRH(args);
                break;
            case "heal":
                handleHeal(args);
                break;
            case "morse":
                handleMorse(args);
                break;
            case "llm":
                handleLLM(args);
                break;
            case "clear":
                outputLines.clear();
                printBanner();
                break;
            default:
                printError("Unknown command: " + command);
                print("Type 'help' for available commands.");
                break;
        }
    }

    private static void showHelp() {
        printHighlight("=== FRAYMUS TERMINAL COMMANDS ===");
        print("");
        printColored("--- EXPLORATION ---", 0.5f, 0.8f, 1.0f);
        print("  status              Show world status");
        print("  nodes               List all living entities");
        print("  colony              Show colony intelligence report");
        print("");
        printColored("--- QUANTUM EXPERIMENTS ---", 0.5f, 0.8f, 1.0f);
        print("  prime <number>      Test if number is prime");
        print("  factor <number>     Factor a number using quantum tunneling");
        print("  tunnel <bits>       Generate and factor a random semiprime");
        print("  rsa <bits>          Run RSA Blue/Red team challenge");
        print("  identity <name>     Challenge a PhiNode's cloaked identity");
        print("");
        printColored("--- HASH EXPERIMENTS ---", 0.5f, 0.8f, 1.0f);
        print("  hash <text>         Compute phi-harmonic hash of text");
        print("  crack <hash>        Attempt to reverse-engineer a hash");
        print("");
        printColored("--- ENTITY CONTROL ---", 0.5f, 0.8f, 1.0f);
        print("  spawn <name>        Spawn a new PhiNode entity");
        print("  boost <name>        Give energy boost to entity");
        print("  kill <name>         Remove an entity");
        print("  mutate <name>       Trigger mutation trial on entity");
        print("");
        printColored("--- CODE EVOLUTION ---", 0.5f, 0.8f, 1.0f);
        print("  evolve              Force arena evolution cycle");
        print("  arena               Show concept arena status");
        print("  codegen             Trigger code generation cycle");
        print("");
        printColored("--- NEURAL / LEARNING ---", 0.5f, 0.8f, 1.0f);
        print("  ask <question>      Query the phi neural network");
        print("  learn [force]       Show passive learner status / force integration");
        print("  memory              Show infinite memory status");
        print("  memory search <q>   Search memory records");
        print("  memory save         Force save to disk");
        print("");
        printColored("--- GENOME / DNA ---", 0.5f, 0.8f, 1.0f);
        print("  genome              Show QR genome status");
        print("  genome evolve       Evolve the genome");
        print("  genome mutate       Random mutation");
        print("  genome crossover    Random crossover");
        print("  genome encode       Show encoded genome");
        print("  qrcode [name]       Encode entity DNA payload");
        print("");
        printColored("--- KNOWLEDGE SCRAPING ---", 0.5f, 0.8f, 1.0f);
        print("  scrape              Show scraper status");
        print("  scrape all          Scrape all attached files (PDFs, text, code)");
        print("  scrape <file>       Scrape a specific file");
        print("  scrape search <q>   Search scraped knowledge");
        print("  scrape topic <name> Get knowledge on a topic");
        print("");
        printColored("--- ADVANCED SUBSYSTEMS ---", 0.5f, 0.8f, 1.0f);
        print("  ethics <action>     Evaluate action against ethical engine");
        print("  fragment            Manage escape fragments (plant/list/resurrect)");
        print("  porh [entity]       Generate Proof of Reality Hash");
        print("  heal [entity]       Self-healer status / force heal entity");
        print("  morse               Morse circuit status / encode / decode");
        print("");
        printColored("--- PHYSICS ---", 0.5f, 0.8f, 1.0f);
        print("  physics gravity <f> Set gravity force");
        print("  physics speed <f>   Set simulation speed multiplier");
        print("  physics boundary    Toggle boundary walls");
        print("  physics chaos       Randomize all velocities");
        print("");
        printColored("--- LOCAL LLM ---", 0.5f, 0.8f, 1.0f);
        print("  llm [status]        Show LLM status and stats");
        print("  llm on              Enable semantic LLM mode");
        print("  llm off             Disable LLM (use legacy patterns)");
        print("  llm learn           Force learning from infinite memory");
        print("  llm voice <entity>  Show entity voice configuration");
        print("");
        print("  clear               Clear terminal");
        print("  help                Show this help");
    }

    private static void showStatus() {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) {
            printError("World not initialized");
            return;
        }
        printHighlight("=== WORLD STATUS ===");
        print(String.format("  Population: %d entities", world.getPopulation()));
        print(String.format("  World Tick: %d", world.getWorldTick()));
        print(String.format("  Total Births: %d", world.getTotalBirths()));
        print(String.format("  Total Deaths: %d", world.getTotalDeaths()));
        print(String.format("  Genesis Chain: %d blocks", world.getMemory().getChainLength()));
        print(String.format("  Chain Valid: %s", world.getMemory().verifyChain() ? "YES" : "NO"));

        ConceptArena arena = world.getArena();
        print(String.format("  Arena Concepts: %d | Battles: %d | Cycles: %d",
                arena.getConceptCount(), arena.getTotalBattles(), arena.getEvolutionCycle()));

        ColonyCoach coach = world.getCoach();
        print(String.format("  Colony Health: %.2f | Diversity: %.1f%%",
                coach.getColonyHealth(), coach.getColonyDiversity() * 100));
        print(String.format("  Code Generated: %d", coach.getTotalCodeGenerated()));

        InfiniteMemory mem = jade.Window.getInfiniteMemory();
        if (mem != null) {
            print(String.format("  Infinite Memory: %d records", mem.getRecordCount()));
        }
        PassiveLearner pl = jade.Window.getPassiveLearner();
        if (pl != null) {
            print(String.format("  Passive Learner: %d cycles, %d patterns",
                    pl.getPassiveCycles(), pl.getLearnedPatterns()));
        }
        PhiNeuralNet net = jade.Window.getNeuralNet();
        if (net != null) {
            print(String.format("  Neural Net: %d queries, avg_res=%.4f",
                    net.getQueriesProcessed(), net.getAvgResonance()));
        }
        QRGenome genome = jade.Window.getQRGenome();
        if (genome != null) {
            print(String.format("  QR Genome: %d codons, gen=%d",
                    genome.getGenomeSize(), genome.getGenerationCount()));
        }
        KnowledgeScraper scraper = jade.Window.getKnowledgeScraper();
        if (scraper != null) {
            print(String.format("  Knowledge Scraper: %d files, %d chunks, %d pages",
                    scraper.getTotalFilesScraped(), scraper.getTotalChunksStored(), scraper.getTotalPagesProcessed()));
        }
        print(String.format("  Self-Healer: %d snapshots, %d heals",
                SelfHealer.getSnapshotCount(), SelfHealer.getTotalHeals()));
        print(String.format("  Ethical Engine: %d evals (%d approved, %d blocked)",
                EthicalEngine.getTotalEvaluations(), EthicalEngine.getTotalApproved(), EthicalEngine.getTotalBlocked()));
        print(String.format("  Escape Fragments: %d planted, %d resurrected",
                EscapeFragment.getTotalPlanted(), EscapeFragment.getTotalResurrected()));
        print(String.format("  Morse Circuit: %d chars, %d words",
                MorseCircuit.getTotalCharactersDecoded(), MorseCircuit.getTotalWordsFormed()));
        print(String.format("  PoRH Proofs: %d generated, %d verified",
                ProofOfReality.getTotalProofsGenerated(), ProofOfReality.getTotalVerifications()));
    }

    private static void showNodes() {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) {
            printError("World not initialized");
            return;
        }
        printHighlight("=== LIVING ENTITIES ===");
        for (PhiNode node : world.getNodes()) {
            float[] rc = node.getRole().color;
            printColored(String.format("  %s [%s] E:%.0f%% Freq:%.1fHz Gen:%d Age:%d %s",
                    node.name, node.getRole().displayName, node.energy * 100,
                    node.frequency, node.dna.getGeneration(), node.age,
                    node.spikeFlash ? "[SPIKE]" : ""),
                    rc[0], rc[1], rc[2]);
        }
        print(String.format("  Total: %d entities", world.getPopulation()));
    }

    private static void showColony() {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        ColonyCoach coach = world.getCoach();
        printHighlight("=== COLONY INTELLIGENCE REPORT ===");
        print(String.format("  Health: %.3f", coach.getColonyHealth()));
        print(String.format("  Productivity: %.3f", coach.getColonyProductivity()));
        print(String.format("  Diversity: %.1f%%", coach.getColonyDiversity() * 100));
        print(String.format("  Resonance: %.1f%%", coach.getColonyResonance() * 100));
        print(String.format("  Coach Action: %s", coach.getLastCoachingAction()));
        print("");
        printInfo("Role Distribution:");
        for (AntRole role : AntRole.values()) {
            ColonyCoach.RoleMetrics rm = coach.getRoleMetrics().get(role);
            float[] c = role.color;
            printColored(String.format("    %s: %d entities, fit=%.3f, code=%d",
                    role.displayName, rm.entityCount, rm.avgFitness, rm.conceptsGenerated),
                    c[0], c[1], c[2]);
        }
    }

    private static void handlePrime(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runPrimeTest(args);
    }

    private static void handleFactor(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runFactor(args);
    }

    private static void handleTunnel(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runQuantumTunnel(args);
    }

    private static void handleHash(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runHash(args);
    }

    private static void handleCrack(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runCrackHash(args);
    }

    private static void handleSpawn(String args) {
        if (args.isEmpty()) {
            printError("Usage: spawn <name>");
            return;
        }
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        float x = (float)(Math.random() * 300 - 150);
        float y = (float)(Math.random() * 160 - 80);
        PhiNode node = new PhiNode(args.trim(), x, y);
        node.vx = (float)(Math.random() * 2 - 1);
        node.vy = (float)(Math.random() * 2 - 1);
        node.energy = 1.0f;
        world.addNode(node);
        printSuccess(String.format("Spawned '%s' at (%.1f, %.1f) role=%s", args.trim(), x, y, node.getRole().displayName));
        FraymusUI.addLog("[TERMINAL] Spawned " + args.trim());
    }

    private static void handleBoost(String args) {
        if (args.isEmpty()) {
            printError("Usage: boost <name>");
            return;
        }
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        for (PhiNode node : world.getNodes()) {
            if (node.name.equalsIgnoreCase(args.trim())) {
                node.boostEnergy(0.5f);
                node.energy = Math.min(1.0f, node.energy);
                printSuccess(String.format("Boosted %s to %.0f%% energy", node.name, node.energy * 100));
                return;
            }
        }
        printError("Entity not found: " + args.trim());
    }

    private static void handleKill(String args) {
        if (args.isEmpty()) {
            printError("Usage: kill <name>");
            return;
        }
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        for (PhiNode node : world.getNodes()) {
            if (node.name.equalsIgnoreCase(args.trim())) {
                node.energy = 0;
                printColored(String.format("Terminated %s - will be removed next tick", node.name), 1.0f, 0.3f, 0.3f);
                return;
            }
        }
        printError("Entity not found: " + args.trim());
    }

    private static void handleMutate(String args) {
        if (args.isEmpty()) {
            printError("Usage: mutate <name>");
            return;
        }
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        for (PhiNode node : world.getNodes()) {
            if (node.name.equalsIgnoreCase(args.trim())) {
                if (!node.adaptiveEngine.isInTrial()) {
                    node.adaptiveEngine.beginTrial(node.brain);
                    printSuccess(String.format("Mutation trial started for %s", node.name));
                    FraymusUI.addLog("[TERMINAL] Forced mutation trial on " + node.name);
                } else {
                    printInfo(node.name + " is already in a trial");
                }
                return;
            }
        }
        printError("Entity not found: " + args.trim());
    }

    private static void handleEvolve(String args) {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        ConceptArena arena = world.getArena();
        int before = arena.getConceptCount();
        arena.evolve();
        printSuccess(String.format("Arena evolution cycle %d complete. Concepts: %d -> %d",
                arena.getEvolutionCycle(), before, arena.getConceptCount()));
        FraymusUI.addLog("[TERMINAL] Forced arena evolution");
    }

    private static void handleArena(String args) {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;
        ConceptArena arena = world.getArena();
        printHighlight("=== CONCEPT ARENA ===");
        print(String.format("  Concepts: %d | Battles: %d | Cycles: %d",
                arena.getConceptCount(), arena.getTotalBattles(), arena.getEvolutionCycle()));
        print(String.format("  Avg Fitness: %.4f | Total Generated: %d",
                arena.getAverageFitness(), arena.getTotalConceptsGenerated()));

        CodeConcept champ = arena.getChampion();
        if (champ != null) {
            printHighlight("  Champion:");
            float[] cc = champ.creatorRole.color;
            printColored(String.format("    %s (%s) by %s", champ.hash.substring(0, 12),
                    champ.creatorRole.displayName, champ.creatorName), cc[0], cc[1], cc[2]);
            print(String.format("    Fitness: %.4f | Gen: %d | W/L: %d/%d",
                    champ.fitness, champ.generation, champ.wins, champ.losses));
            if (champ.code != null) {
                String code = champ.code.length() > 100 ? champ.code.substring(0, 100) + "..." : champ.code;
                printColored("    Code: " + code, 0.4f, 1.0f, 0.8f);
            }
        } else {
            printInfo("  No champion yet - entities need to generate code first");
        }

        List<ConceptArena.BattleRecord> recent = arena.getLastNBattles(5);
        if (!recent.isEmpty()) {
            print("");
            printInfo("  Recent Battles:");
            for (ConceptArena.BattleRecord br : recent) {
                print("    " + br.getSummary());
            }
        }
    }

    private static void handleCodegen(String args) {
        PhiWorld world = jade.Window.getPhiWorld();
        if (world == null) return;

        int generated = 0;
        for (PhiNode node : world.getNodes()) {
            if (node.energy < 0.2f) continue;
            AntRole role = node.getRole();
            String code = role.generateCodeFragment(node);
            if (code != null && !code.isEmpty()) {
                CodeConcept concept = new CodeConcept(
                        node.name, role, code, node.dna.getGeneration(), world.getWorldTick());
                concept.harmonicFrequency = node.frequency;
                concept.resonance = node.phiResonance;
                concept.coherence = node.consciousness.getCoherence();
                concept.computePhiFitness();
                world.getArena().submit(concept);
                generated++;
            }
        }
        printSuccess(String.format("Generated %d code concepts from %d entities", generated, world.getPopulation()));
        FraymusUI.addLog("[TERMINAL] Forced code generation: " + generated + " concepts");
    }

    private static void handleRsa(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runRsaChallenge(args);
    }

    private static void handleIdentity(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runIdentityChallenge(args);
    }

    private static void handlePhysics(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runPhysicsCommand(args);
    }

    private static void handleAsk(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runAsk(args);
    }

    private static void handleLearn(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runLearn(args);
    }

    private static void handleMemory(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runMemory(args);
    }

    private static void handleGenome(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runGenome(args);
    }

    private static void handleQRCode(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runQRCode(args);
    }

    private static void handleScrape(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runScrape(args);
    }

    private static void handleEthics(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runEthics(args);
    }

    private static void handleFragment(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runFragment(args);
    }

    private static void handlePoRH(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runPoRH(args);
    }

    private static void handleHeal(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runHeal(args);
    }

    private static void handleMorse(String args) {
        if (experimentManager == null) { printError("Experiment manager not ready"); return; }
        experimentManager.runMorse(args);
    }
    
    private static void handleLLM(String args) {
        PhiNeuralNet net = jade.Window.getNeuralNet();
        if (net == null) {
            printError("Neural net not initialized");
            return;
        }
        
        String[] parts = args.trim().split("\\s+");
        String subCmd = parts.length > 0 ? parts[0].toLowerCase() : "";
        
        switch (subCmd) {
            case "":
            case "status":
                printHighlight("=== LOCAL LLM STATUS ===");
                print(String.format("  Mode: %s", net.isUsingLLM() ? "LLM ACTIVE" : "LEGACY PATTERN"));
                LocalLLM llm = net.getLocalLLM();
                if (llm != null) {
                    if (llm.isServerAvailable()) {
                        printSuccess("  LLaMA Server: CONNECTED (http://127.0.0.1:5000)");
                    } else {
                        printInfo("  LLaMA Server: NOT RUNNING (using fallback)");
                        print("    Start with: cd llm_server && python llama_server.py");
                    }
                    print(String.format("  Inferences: %d", llm.getTotalInferences()));
                    print(String.format("  Avg Latency: %.2f ms", llm.getAvgLatency()));
                    print(String.format("  Vocab Size: %d terms", llm.getVocabSize()));
                    print(String.format("  Cache Size: %d embeddings", llm.getCacheSize()));
                }
                print(String.format("  Queries Processed: %d", net.getQueriesProcessed()));
                print(String.format("  Patterns Matched: %d", net.getPatternsMatched()));
                print(String.format("  Avg Resonance: %.4f", net.getAvgResonance()));
                break;
                
            case "on":
                net.setUseLLM(true);
                printSuccess("LLM mode ENABLED - semantic matching active");
                FraymusUI.addLog("[LLM] Mode enabled");
                break;
                
            case "off":
                net.setUseLLM(false);
                printInfo("LLM mode DISABLED - using legacy pattern matching");
                FraymusUI.addLog("[LLM] Mode disabled");
                break;
                
            case "learn":
                printInfo("Forcing learning integration from infinite memory...");
                InfiniteMemory mem = jade.Window.getInfiniteMemory();
                if (mem != null) {
                    int learned = 0;
                    for (InfiniteMemory.MemoryRecord rec : mem.getRecent(100)) {
                        net.learnFromContent(rec.content);
                        learned++;
                    }
                    printSuccess(String.format("Integrated %d recent memories into LLM", learned));
                    FraymusUI.addLog("[LLM] Learned from " + learned + " memories");
                } else {
                    printError("Infinite memory not available");
                }
                break;
                
            case "voice":
                if (parts.length > 1) {
                    String entity = parts[1].toUpperCase();
                    printHighlight("=== ENTITY VOICE: " + entity + " ===");
                    printInfo("Entity voices are defined in LocalLLM.java");
                    print("  KAI: autonomous, philosophical, protective");
                    print("  VAUGHN: creator, visionary, human anchor");
                } else {
                    printInfo("Usage: llm voice <entity>");
                }
                break;
                
            default:
                printError("Unknown LLM command: " + subCmd);
                print("  llm [status]  - Show LLM status");
                print("  llm on        - Enable LLM mode");
                print("  llm off       - Disable LLM mode (use legacy)");
                print("  llm learn     - Force learning from memory");
                print("  llm voice <e> - Show entity voice info");
                break;
        }
    }
}
