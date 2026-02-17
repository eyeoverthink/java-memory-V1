package fraymus.nerve;

import fraymus.bio.HyperCortex;
import fraymus.core.FraymusCore;
import fraymus.core.FraymusNexus;
import fraymus.core.OmegaPoint;
import fraymus.core.UnifiedMind;
import fraymus.eternal.SoulCrystal;
import fraymus.hyper.HyperFormer;
import fraymus.kernel.FraymusKernel;
import fraymus.lowlevel.FraymusCPU;
import fraymus.net.PlanetaryNode;
import fraymus.omega.Chronos;
import fraymus.omega.RecursionEngine;
import fraymus.physics.HyperRigidBody;
import fraymus.quantum.security.SovereignIdentitySystem;
import fraymus.senses.AvatarCortex;
import fraymus.shell.IntentRegistry;
import fraymus.shell.SystemSkills;
import fraymus.symbolic.HoloGraph;
import fraymus.symbolic.ReasoningEngine;

import java.util.Arrays;
import java.util.List;

/**
 * PRIME DISPATCH: Routes commands from the dashboard WebSocket
 * to the actual FRAYMUS subsystems and returns results.
 *
 * This is the bridge between the HTML frontend and the Java organism.
 * Every command the user types in the dashboard terminal flows through here.
 */
public class PrimeDispatch {

    private final PrimeNerve nerve;
    private final PrimeTelemetry telemetry;

    // Subsystem references (wired during boot)
    private HyperFormer brain;
    private HyperCortex cortex;
    private FraymusKernel kernel;
    private OmegaPoint.TheShield shield;
    private FraymusCPU cpu;
    private HyperRigidBody physicsBody;
    private SovereignIdentitySystem sovereign;
    private HoloGraph holo;
    private ReasoningEngine reasoner;
    private Chronos chronos;
    private PlanetaryNode planetNode;
    private IntentRegistry intentReg;
    private UnifiedMind mind;
    private AvatarCortex avatar;
    private List<String> sessionLog;

    public PrimeDispatch(PrimeNerve nerve, PrimeTelemetry telemetry) {
        this.nerve = nerve;
        this.telemetry = telemetry;
    }

    // ── WIRING ───────────────────────────────────────────────────
    public void wireBrain(HyperFormer b) { this.brain = b; }
    public void wireCortex(HyperCortex c) { this.cortex = c; }
    public void wireKernel(FraymusKernel k) { this.kernel = k; }
    public void wireShield(OmegaPoint.TheShield s) { this.shield = s; }
    public void wireCPU(FraymusCPU c) { this.cpu = c; }
    public void wirePhysics(HyperRigidBody p) { this.physicsBody = p; }
    public void wireSovereign(SovereignIdentitySystem s) { this.sovereign = s; }
    public void wireHolo(HoloGraph h) { this.holo = h; }
    public void wireReasoner(ReasoningEngine r) { this.reasoner = r; }
    public void wireChronos(Chronos c) { this.chronos = c; }
    public void wirePlanetNode(PlanetaryNode p) { this.planetNode = p; }
    public void wireIntentReg(IntentRegistry i) { this.intentReg = i; }
    public void wireMind(UnifiedMind m) { this.mind = m; }
    public void wireAvatar(AvatarCortex a) { this.avatar = a; }
    public void wireSessionLog(List<String> log) { this.sessionLog = log; }

    /** Update brain reference (after chronos step or soul load) */
    public void updateBrain(HyperFormer b) { this.brain = b; }

    // ── DISPATCH ─────────────────────────────────────────────────
    public void dispatch(String input) {
        if (input == null || input.trim().isEmpty()) return;
        input = input.trim();

        String[] tokens = input.split("\\s+");
        String cmd = tokens[0].toLowerCase();

        try {
            switch (cmd) {

                // ── HDC BRAIN ──
                case "learn" -> {
                    String[] words = Arrays.copyOfRange(tokens, 1, tokens.length);
                    if (words.length < 2) {
                        nerve.broadcast("LOG:Usage: learn <word1> <word2> ...");
                    } else {
                        brain.learnSentence(words);
                        for (String w : words) cortex.inject(w.toUpperCase());
                        nerve.broadcast("LOG:BRAIN: Absorbed. Vocab=" + brain.vocabSize() + " Assoc=" + brain.memorySize());
                        if (sessionLog != null) sessionLog.add("learn:" + String.join(" ", words));
                    }
                }

                case "predict" -> {
                    String[] ctx = Arrays.copyOfRange(tokens, 1, tokens.length);
                    String prediction = brain.predictNext(ctx);
                    nerve.broadcast("LOG:BRAIN >> " + prediction);
                    if (sessionLog != null) sessionLog.add("predict:" + prediction);
                }

                // ── BIOLOGICAL CORTEX ──
                case "inject" -> {
                    if (tokens.length < 2) {
                        nerve.broadcast("LOG:Usage: inject <concept>");
                    } else {
                        String concept = input.substring(7).trim().toUpperCase();
                        cortex.inject(concept);
                        brain.learn(concept.toLowerCase());
                        nerve.broadcast("LOG:CORTEX: Injected '" + concept + "'");
                    }
                }

                // ── OMEGA POINT (CRYPTO) ──
                case "encrypt" -> {
                    if (tokens.length < 2) {
                        nerve.broadcast("LOG:Usage: encrypt <text>");
                    } else {
                        String plaintext = input.substring(8);
                        String cipher = shield.encrypt(plaintext);
                        nerve.broadcast("LOG:SHIELD: " + cipher.substring(0, Math.min(40, cipher.length())) + "...");
                        if (sessionLog != null) sessionLog.add("enc:" + cipher.substring(0, Math.min(20, cipher.length())));
                    }
                }

                // ── PHI KERNEL ──
                case "kernel" -> {
                    int ticks = 10;
                    if (tokens.length > 1) {
                        try { ticks = Integer.parseInt(tokens[1]); } catch (NumberFormatException ignored) {}
                    }
                    nerve.broadcast("LOG:KERNEL: Running " + ticks + " ticks...");
                    for (int i = 0; i < ticks; i++) {
                        kernel.tick();
                    }
                    nerve.broadcast("LOG:KERNEL: Done. Processes=" + kernel.processCount() + " Ticks=" + kernel.getTickCount());
                }

                case "spawn" -> {
                    if (tokens.length < 2) {
                        nerve.broadcast("LOG:Usage: spawn <process_name>");
                    } else {
                        kernel.spawn(tokens[1]);
                        cortex.inject(tokens[1].toUpperCase());
                        nerve.broadcast("LOG:KERNEL: Spawned '" + tokens[1] + "'");
                    }
                }

                // ── PHYSICS ──
                case "physics" -> dispatchPhysics(tokens, input);

                // ── FVM ──
                case "fvm" -> dispatchFVM(tokens);

                // ── SOVEREIGN CRYPTO ──
                case "auth", "sovereign" -> {
                    if (tokens.length < 3) {
                        nerve.broadcast("LOG:Usage: auth <username> <password>");
                    } else {
                        String user = tokens[1];
                        String pass = input.substring(input.indexOf(tokens[2]));
                        nerve.broadcast("LOG:SOVEREIGN: Running Blue→Red→Purple loop...");
                        telemetry.setState("DEFENSE");
                        String result = sovereign.runSovereignLoop(user, pass);
                        nerve.broadcast("LOG:" + result.replace("\n", " | "));
                        telemetry.setState("NEUTRAL");
                        if (sessionLog != null) sessionLog.add("sovereign:" + user);
                    }
                }

                case "identity" -> {
                    nerve.broadcast("LOG:" + sovereign.getStatus());
                }

                // ── HOLO GRAPH ──
                case "holo" -> dispatchHolo(tokens, input);

                // ── CHRONOS ──
                case "chronos" -> dispatchChronos(tokens);

                // ── FRAY SHELL ──
                case "ff" -> dispatchShell(tokens, input);

                // ── PLANETARY NETWORK ──
                case "planet" -> dispatchPlanet(tokens, input);

                // ── SOUL CRYSTAL ──
                case "soul" -> dispatchSoul(tokens);

                // ── UNIFIED MIND ──
                case "mind" -> {
                    if (tokens.length < 2) {
                        nerve.broadcast("LOG:Usage: mind <query>");
                    } else {
                        String query = input.substring(5).trim();
                        nerve.broadcast("LOG:MIND: Engaging swarm...");
                        telemetry.setState("DREAMING");
                        String result = mind.processInput(query);
                        nerve.broadcast("LOG:MIND: " + result);
                        telemetry.setState("NEUTRAL");
                        if (sessionLog != null) sessionLog.add("mind:" + query);
                    }
                }

                // ── STATUS ──
                case "status" -> {
                    nerve.broadcast("LOG:=== FRAYMUS STATUS ===");
                    nerve.broadcast("LOG:BRAIN: Vocab=" + brain.vocabSize() + " Assoc=" + brain.memorySize());
                    nerve.broadcast("LOG:KERNEL: Procs=" + kernel.processCount() + " Ticks=" + kernel.getTickCount());
                    nerve.broadcast("LOG:PHYSICS: Speed=" + String.format("%.4f", physicsBody.getHyperSpeed()));
                    nerve.broadcast("LOG:FVM: ACC=" + cpu.getACC());
                    nerve.broadcast("LOG:SOVEREIGN: Evo=" + String.format("%.2f", sovereign.getEvolutionLevel()) + " Solved=" + sovereign.getEntitiesSolved());
                    nerve.broadcast("LOG:======================");
                }

                // ── HELP ──
                case "help" -> {
                    nerve.broadcast("LOG:── COMMANDS ──");
                    nerve.broadcast("LOG:learn <words>  | predict <words> | inject <concept>");
                    nerve.broadcast("LOG:encrypt <text> | kernel [n] | spawn <name>");
                    nerve.broadcast("LOG:physics hit/dump/export | fvm run/regs");
                    nerve.broadcast("LOG:auth <u> <p> | sovereign <u> <p> | identity");
                    nerve.broadcast("LOG:holo learn/ask/hop | chronos step/evolve");
                    nerve.broadcast("LOG:ff <cmd> | ff bind <skill> <phrase>");
                    nerve.broadcast("LOG:planet start/join/query/stop");
                    nerve.broadcast("LOG:soul save/load | mind <query> | status");
                }

                // ── BLUE/RED/PURPLE from dashboard ──
                default -> {
                    if (input.startsWith("BLUE_LOCK:")) {
                        nerve.broadcast("LOG:BLUE LOCK received from dashboard.");
                        telemetry.setState("DEFENSE");
                        Thread.sleep(500);
                        telemetry.setState("NEUTRAL");
                    } else if (input.startsWith("RED_BREACH:")) {
                        nerve.broadcast("LOG:RED BREACH received from dashboard.");
                        telemetry.setState("DEFENSE");
                        Thread.sleep(500);
                        telemetry.setState("NEUTRAL");
                    } else if (input.startsWith("PURPLE_VERIFIED:")) {
                        nerve.broadcast("LOG:PURPLE VERIFICATION received from dashboard.");
                        telemetry.setState("EVOLVING");
                        Thread.sleep(1000);
                        telemetry.setState("NEUTRAL");
                    } else {
                        nerve.broadcast("LOG:Unknown command: " + cmd + " (type 'help')");
                    }
                }
            }
        } catch (Exception e) {
            nerve.broadcast("LOG:ERROR: " + e.getMessage());
        }
    }

    // ── SUB-DISPATCHERS ──────────────────────────────────────────

    private void dispatchPhysics(String[] tokens, String input) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: physics hit/dump/export");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "hit" -> {
                if (tokens.length < 3) {
                    nerve.broadcast("LOG:Usage: physics hit <concept>");
                } else {
                    String concept = input.substring(input.indexOf(tokens[2]));
                    physicsBody.applyDataForce(concept);
                    nerve.broadcast("LOG:PHYSICS: IMPACT. Speed=" + String.format("%.4f", physicsBody.getHyperSpeed())
                            + " Deform=" + String.format("%.4f", physicsBody.geometry.getDeformation()));
                    if (sessionLog != null) sessionLog.add("physics:hit:" + concept);
                }
            }
            case "dump" -> {
                nerve.broadcast("LOG:" + physicsBody.getStatus());
            }
            case "export" -> {
                nerve.broadcast("LOG:" + physicsBody.geometry.exportObj().substring(0, Math.min(200, physicsBody.geometry.exportObj().length())) + "...");
            }
            default -> nerve.broadcast("LOG:Unknown physics command: " + tokens[1]);
        }
    }

    private void dispatchFVM(String[] tokens) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: fvm run/regs");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "run" -> {
                byte[] program = cpu.transmutate("Add 10 and 20");
                cpu.flash(program);
                cpu.cycle();
                nerve.broadcast("LOG:FVM: " + cpu.dumpRegisters());
                if (sessionLog != null) sessionLog.add("fvm:run:ACC=" + cpu.getACC());
            }
            case "regs" -> nerve.broadcast("LOG:FVM: " + cpu.dumpRegisters());
            default -> nerve.broadcast("LOG:Unknown FVM command: " + tokens[1]);
        }
    }

    private void dispatchHolo(String[] tokens, String input) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: holo learn/ask/hop");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "learn" -> {
                if (tokens.length < 5) {
                    nerve.broadcast("LOG:Usage: holo learn <subj> <rel> <obj>");
                } else {
                    holo.learn(tokens[2], tokens[3], tokens[4]);
                    nerve.broadcast("LOG:HOLO: Learned " + tokens[2] + " " + tokens[3] + " " + tokens[4]);
                    if (sessionLog != null) sessionLog.add("holo:learn:" + tokens[2] + ":" + tokens[3] + ":" + tokens[4]);
                }
            }
            case "ask" -> {
                if (tokens.length < 4) {
                    nerve.broadcast("LOG:Usage: holo ask <subj> <rel>");
                } else {
                    String answer = holo.ask(tokens[2], tokens[3]);
                    nerve.broadcast("LOG:HOLO ANSWER: " + answer);
                    telemetry.setHoloResult(answer);
                    if (sessionLog != null) sessionLog.add("holo:ask:" + tokens[2] + ":" + tokens[3] + "=" + answer);
                }
            }
            case "hop" -> {
                if (tokens.length < 5) {
                    nerve.broadcast("LOG:Usage: holo hop <start> <rel1> <rel2> ...");
                } else {
                    String[] rels = Arrays.copyOfRange(tokens, 3, tokens.length);
                    String result = reasoner.multiHop(tokens[2], rels);
                    nerve.broadcast("LOG:HOLO HOP: " + result);
                    telemetry.setHoloResult(result);
                    if (sessionLog != null) sessionLog.add("holo:hop:" + tokens[2] + "=" + result);
                }
            }
            default -> nerve.broadcast("LOG:Unknown holo command: " + tokens[1]);
        }
    }

    private void dispatchChronos(String[] tokens) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: chronos step/evolve");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "step" -> {
                nerve.broadcast("LOG:CHRONOS: Splitting timeline...");
                telemetry.setState("EVOLVING");
                HyperFormer evolved = chronos.step();
                if (evolved != null) {
                    brain = evolved;
                    nerve.broadcast("LOG:CHRONOS: Brain evolved. Timeline collapsed.");
                    telemetry.setChronoGen(telemetry.chronoGen + 1);
                }
                telemetry.setState("NEUTRAL");
                if (sessionLog != null) sessionLog.add("chronos:step");
            }
            case "evolve" -> {
                int gens = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 3;
                nerve.broadcast("LOG:CHRONOS: Recursion engine " + gens + " generations...");
                RecursionEngine re = new RecursionEngine(gens);
                Thread evoThread = new Thread(re::ignite, "RecursionEngine");
                evoThread.setDaemon(true);
                evoThread.start();
                if (sessionLog != null) sessionLog.add("chronos:evolve:" + gens);
            }
            default -> nerve.broadcast("LOG:Unknown chronos command: " + tokens[1]);
        }
    }

    private void dispatchShell(String[] tokens, String input) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: ff <command> | ff bind <skill> <phrase>");
            return;
        }
        if (tokens[1].equalsIgnoreCase("bind") && tokens.length >= 4) {
            String skill = tokens[2];
            String phrase = input.substring(input.indexOf(tokens[3]));
            String[] phraseWords = phrase.split("\\s+");
            var newVec = brain.embed(phraseWords[0]).copy();
            for (int i = 1; i < phraseWords.length; i++) {
                newVec.bundle(brain.embed(phraseWords[i]));
            }
            switch (skill) {
                case "ls" -> intentReg.register("learned_ls_" + phrase.hashCode(), newVec, SystemSkills::listFiles);
                case "pwd" -> intentReg.register("learned_pwd_" + phrase.hashCode(), newVec, SystemSkills::printWorkingDir);
                case "cat" -> intentReg.register("learned_cat_" + phrase.hashCode(), newVec, SystemSkills::cat);
                case "echo" -> intentReg.register("learned_echo_" + phrase.hashCode(), newVec, SystemSkills::echo);
                default -> nerve.broadcast("LOG:Unknown skill: " + skill);
            }
            nerve.broadcast("LOG:SHELL: Learned '" + phrase + "' → " + skill);
            if (sessionLog != null) sessionLog.add("ff:bind:" + skill + ":" + phrase);
        } else {
            String ffCmd = input.substring(3).trim();
            String[] words = ffCmd.split("\\s+");
            var thought = brain.embed(words[0]).copy();
            for (int i = 1; i < words.length; i++) {
                thought.bundle(brain.embed(words[i]));
            }
            var action = intentReg.resolve(thought, 0.51);
            if (action != null) {
                action.accept(ffCmd);
                nerve.broadcast("LOG:SHELL: Executed intent for '" + ffCmd + "'");
            } else {
                nerve.broadcast("LOG:SHELL: Intent not recognized. Try: ff bind <ls|pwd|cat|echo> <phrase>");
            }
            if (sessionLog != null) sessionLog.add("ff:" + ffCmd);
        }
    }

    private void dispatchPlanet(String[] tokens, String input) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: planet start/join/query/stop");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "start" -> {
                nerve.broadcast("LOG:PLANET: Starting node...");
                telemetry.setNetStatus("LIVE");
                if (sessionLog != null) sessionLog.add("planet:start");
            }
            case "join" -> {
                if (tokens.length < 3) {
                    nerve.broadcast("LOG:Usage: planet join <ip:port>");
                } else {
                    String[] hp = tokens[2].split(":");
                    String host = hp[0];
                    int port = hp.length > 1 ? Integer.parseInt(hp[1]) : 7777;
                    var peer = planetNode.handshake(host, port);
                    if (peer != null) {
                        nerve.broadcast("LOG:PLANET: Connected to " + peer.id);
                        telemetry.setNetPeers(telemetry.netPeers + 1);
                    } else {
                        nerve.broadcast("LOG:PLANET: Handshake failed.");
                    }
                    if (sessionLog != null) sessionLog.add("planet:join:" + tokens[2]);
                }
            }
            case "query" -> {
                if (tokens.length < 3) {
                    nerve.broadcast("LOG:Usage: planet query <concept>");
                } else {
                    String concept = input.substring(input.indexOf(tokens[2]));
                    var resp = planetNode.queryNetwork(concept);
                    nerve.broadcast("LOG:PLANET: " + resp);
                    if (sessionLog != null) sessionLog.add("planet:query:" + concept);
                }
            }
            case "stop" -> {
                planetNode.stop();
                nerve.broadcast("LOG:PLANET: Node stopped.");
                telemetry.setNetStatus("OFFLINE");
                telemetry.setNetPeers(0);
            }
            default -> nerve.broadcast("LOG:Unknown planet command: " + tokens[1]);
        }
    }

    private void dispatchSoul(String[] tokens) {
        if (tokens.length < 2) {
            nerve.broadcast("LOG:Usage: soul save/load");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "save", "preserve" -> {
                SoulCrystal.preserve(brain);
                nerve.broadcast("LOG:SOUL: Brain preserved to SoulCrystal.");
                telemetry.setSoulState("PRESERVED");
                if (sessionLog != null) sessionLog.add("soul:save");
            }
            case "load", "resurrect" -> {
                brain = SoulCrystal.resurrect();
                nerve.broadcast("LOG:SOUL: Brain resurrected. Vocab=" + brain.vocabSize());
                telemetry.setSoulState("RESURRECTED");
                if (sessionLog != null) sessionLog.add("soul:load");
            }
            default -> nerve.broadcast("LOG:Unknown soul command: " + tokens[1]);
        }
    }
}
