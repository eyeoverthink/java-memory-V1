package fraymus;

import java.math.BigInteger;
import java.util.List;

public class ExperimentManager {

    private final PhiWorld world;
    private final InfiniteMemory infiniteMemory;
    private final PassiveLearner passiveLearner;
    private final PhiNeuralNet neuralNet;
    private final QRGenome qrGenome;
    private final KnowledgeScraper knowledgeScraper;
    private float gravityForce = 0.0f;
    private float speedMultiplier = 1.0f;
    private boolean boundaryEnabled = true;

    public ExperimentManager(PhiWorld world, InfiniteMemory infiniteMemory,
                              PassiveLearner passiveLearner, PhiNeuralNet neuralNet,
                              QRGenome qrGenome, KnowledgeScraper knowledgeScraper) {
        this.world = world;
        this.infiniteMemory = infiniteMemory;
        this.passiveLearner = passiveLearner;
        this.neuralNet = neuralNet;
        this.qrGenome = qrGenome;
        this.knowledgeScraper = knowledgeScraper;
    }

    public void runPrimeTest(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: prime <number>");
            return;
        }
        try {
            long n = Long.parseLong(args.trim());
            boolean result = QuantumTunneler.isPrime(n);
            if (result) {
                CommandTerminal.printSuccess(String.format("%d is PRIME", n));
            } else {
                CommandTerminal.printColored(String.format("%d is NOT prime", n), 1.0f, 0.5f, 0.0f);
            }
        } catch (NumberFormatException e) {
            try {
                BigInteger big = new BigInteger(args.trim());
                boolean result = QuantumTunneler.isPrimeBig(big);
                if (result) {
                    CommandTerminal.printSuccess(big + " is PROBABLY PRIME (Miller-Rabin)");
                } else {
                    CommandTerminal.printColored(big + " is NOT prime", 1.0f, 0.5f, 0.0f);
                }
            } catch (NumberFormatException e2) {
                CommandTerminal.printError("Invalid number: " + args);
            }
        }
    }

    public void runFactor(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: factor <number>");
            return;
        }
        try {
            long n = Long.parseLong(args.trim());
            if (n < 2) {
                CommandTerminal.printError("Number must be >= 2");
                return;
            }

            List<PhiNode> nodes = world.getNodes();
            PhiNode circuit = QuantumTunneler.selectBestCircuit(nodes);

            CommandTerminal.printHighlight(String.format("Quantum Tunneling %d using circuit %s...",
                    n, circuit != null ? circuit.name : "none"));

            QuantumTunneler.TunnelResult result = QuantumTunneler.quantumTunnel(n, circuit);

            if (result.success) {
                CommandTerminal.printSuccess(String.format("TUNNELED in %d iterations (%.3f ms)",
                        result.iterations, result.elapsedNanos / 1_000_000.0));
                CommandTerminal.printSuccess(String.format("  %d = %s x %s",
                        n, result.factorP, result.factorQ));
                CommandTerminal.print(String.format("  Verification: %s x %s = %s",
                        result.factorP, result.factorQ, result.factorP.multiply(result.factorQ)));
                CommandTerminal.print(String.format("  Circuit: %s", result.circuitName));

                if (world.getMemory() != null) {
                    world.getMemory().record("QUANTUM_TUNNEL",
                            String.format("factor|%d|%s*%s|iters=%d|circuit=%s",
                                    n, result.factorP, result.factorQ, result.iterations, result.circuitName));
                }
                FraymusUI.addLog(String.format("[TUNNEL] %d = %s x %s (%d iters)",
                        n, result.factorP, result.factorQ, result.iterations));
            } else {
                CommandTerminal.printError(String.format("Tunneling failed after %d iterations", result.iterations));
                if (QuantumTunneler.isPrime(n)) {
                    CommandTerminal.printInfo("  (Number is prime - cannot be factored)");
                }
            }
        } catch (NumberFormatException e) {
            try {
                BigInteger big = new BigInteger(args.trim());
                List<PhiNode> nodes = world.getNodes();
                PhiNode circuit = QuantumTunneler.selectBestCircuit(nodes);

                CommandTerminal.printHighlight(String.format("Quantum Tunneling %s... using circuit %s",
                        big.toString().substring(0, Math.min(20, big.toString().length())),
                        circuit != null ? circuit.name : "none"));

                QuantumTunneler.TunnelResult result = QuantumTunneler.quantumTunnelBig(big, circuit);

                if (result.success) {
                    CommandTerminal.printSuccess(String.format("TUNNELED in %d iterations (%.3f ms)",
                            result.iterations, result.elapsedNanos / 1_000_000.0));
                    CommandTerminal.printSuccess(String.format("  = %s x %s", result.factorP, result.factorQ));
                } else {
                    CommandTerminal.printError(String.format("Tunneling failed after %d iterations", result.iterations));
                }
            } catch (NumberFormatException e2) {
                CommandTerminal.printError("Invalid number: " + args);
            }
        }
    }

    public void runQuantumTunnel(String args) {
        int bits = 32;
        if (!args.isEmpty()) {
            try {
                bits = Integer.parseInt(args.trim());
                if (bits < 8) bits = 8;
                if (bits > 128) {
                    CommandTerminal.printInfo("Capping at 128 bits for performance");
                    bits = 128;
                }
            } catch (NumberFormatException e) {
                CommandTerminal.printError("Usage: tunnel <bits>");
                return;
            }
        }

        BigInteger n = QuantumTunneler.generateSemiprime(bits);
        CommandTerminal.printHighlight(String.format("Generated %d-bit semiprime:", bits));
        String nStr = n.toString();
        if (nStr.length() > 40) nStr = nStr.substring(0, 40) + "...";
        CommandTerminal.print("  N = " + nStr);

        List<PhiNode> nodes = world.getNodes();
        CommandTerminal.printInfo(String.format("Deploying %d living circuits for quantum tunneling...", nodes.size()));

        PhiNode bestCircuit = QuantumTunneler.selectBestCircuit(nodes);
        if (bestCircuit != null) {
            CommandTerminal.print(String.format("  Lead circuit: %s [%s] freq=%.1fHz energy=%.0f%%",
                    bestCircuit.name, bestCircuit.getRole().displayName,
                    bestCircuit.frequency, bestCircuit.energy * 100));
        }

        QuantumTunneler.TunnelResult result;
        if (bits <= 64) {
            result = QuantumTunneler.quantumTunnel(n.longValue(), bestCircuit);
        } else {
            result = QuantumTunneler.quantumTunnelBig(n, bestCircuit);
        }

        if (result.success) {
            CommandTerminal.printSuccess(String.format("QUANTUM TUNNEL SUCCESS in %d iterations (%.3f ms)",
                    result.iterations, result.elapsedNanos / 1_000_000.0));
            CommandTerminal.printSuccess(String.format("  p = %s", result.factorP));
            CommandTerminal.printSuccess(String.format("  q = %s", result.factorQ));
            CommandTerminal.print(String.format("  Verification: p * q = %s  Match: %s",
                    result.factorP.multiply(result.factorQ),
                    result.factorP.multiply(result.factorQ).equals(n) ? "YES" : "NO"));

            if (bestCircuit != null) {
                bestCircuit.boostEnergy(0.1f);
                CommandTerminal.printInfo(String.format("  Circuit %s rewarded (+10%% energy)", bestCircuit.name));
            }

            if (world.getMemory() != null) {
                world.getMemory().record("QUANTUM_TUNNEL",
                        String.format("semiprime|%dbits|iters=%d|circuit=%s",
                                bits, result.iterations, result.circuitName));
            }
            FraymusUI.addLog(String.format("[TUNNEL] %d-bit semiprime cracked in %d iters by %s",
                    bits, result.iterations, result.circuitName));
        } else {
            CommandTerminal.printError(String.format("Tunneling failed after %d iterations (%.3f ms)",
                    result.iterations, result.elapsedNanos / 1_000_000.0));
            CommandTerminal.printInfo("  Try smaller bit size or wait for circuits to evolve");
        }
    }

    public void runHash(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: hash <text>");
            return;
        }
        HashReverser.HashResult result = HashReverser.phiHash(args);
        CommandTerminal.printHighlight("Phi-Harmonic Hash:");
        CommandTerminal.print("  Input: " + result.input);
        CommandTerminal.printColored("  SHA-256: " + result.hash, 0.4f, 1.0f, 0.8f);
        CommandTerminal.print(String.format("  Phi Resonance: %.10f", result.phiResonance));
        CommandTerminal.print(String.format("  Harmonic Freq: %.2f Hz", result.harmonicFrequency));
    }

    public void runCrackHash(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: crack <hash_prefix>");
            return;
        }
        String target = args.trim();
        CommandTerminal.printHighlight(String.format("Attempting to reverse hash: %s...", target));
        CommandTerminal.printInfo("Using living circuits as computational guides...");

        List<PhiNode> nodes = world.getNodes();
        int maxIter = 100_000;

        HashReverser.CrackResult result = HashReverser.crackHash(target, nodes, maxIter);

        if (result.fullMatch) {
            CommandTerminal.printSuccess(String.format("HASH CRACKED in %d iterations (%.3f ms)!",
                    result.iterations, result.elapsedNanos / 1_000_000.0));
            CommandTerminal.printSuccess("  Input: " + result.foundInput);
            CommandTerminal.printSuccess("  Hash:  " + HashReverser.sha256(result.foundInput));
        } else {
            CommandTerminal.printColored(String.format("Best partial match: %d/%d prefix bits after %d iterations",
                    result.bestPartialBits, target.length() * 4, result.iterations),
                    1.0f, 0.5f, 0.0f);
            if (result.bestPartial != null) {
                String partialHash = HashReverser.sha256(result.bestPartial);
                CommandTerminal.print("  Best input:  " + result.bestPartial);
                CommandTerminal.print("  Best hash:   " + partialHash.substring(0, Math.min(target.length() + 4, partialHash.length())));
                CommandTerminal.print("  Target:      " + target);
            }
            CommandTerminal.printInfo("  Full SHA-256 reversal is cryptographically infeasible");
            CommandTerminal.printInfo("  But living circuits found " + result.bestPartialBits + " matching prefix bits");
        }

        if (world.getMemory() != null) {
            world.getMemory().record("HASH_CRACK",
                    String.format("target=%s|bits=%d|iters=%d",
                            target.substring(0, Math.min(16, target.length())),
                            result.bestPartialBits, result.iterations));
        }
    }

    public void runRsaChallenge(String args) {
        int bits = 16;
        if (!args.isEmpty()) {
            try {
                bits = Integer.parseInt(args.trim());
                if (bits < 8) bits = 8;
                if (bits > 64) {
                    CommandTerminal.printInfo("Capping at 64 bits for terminal demo");
                    bits = 64;
                }
            } catch (NumberFormatException e) {
                CommandTerminal.printError("Usage: rsa <bits>");
                return;
            }
        }

        CommandTerminal.printHighlight(String.format("=== RSA %d-BIT CHALLENGE ===", bits));

        CommandTerminal.printInfo("[BLUE TEAM] Generating keypair...");
        RSASandbox.BlueTeam blue = new RSASandbox.BlueTeam(bits);
        String nHex = blue.N.toString(16);
        CommandTerminal.print(String.format("  Public N: %s (%d bits)",
                nHex.substring(0, Math.min(32, nHex.length())), blue.N.bitLength()));

        String secret = bits <= 16 ? "PHI" : "ALIVE";
        CommandTerminal.printInfo("[BLUE TEAM] Encrypting: \"" + secret + "\"");
        BigInteger cipher = blue.encrypt(secret);

        CommandTerminal.printColored("[RED TEAM] Initiating Fermat factorization...", 1.0f, 0.3f, 0.3f);

        RSASandbox.RedTeam red = new RSASandbox.RedTeam();
        long start = System.nanoTime();
        BigInteger[] factors = red.crack(blue.N);
        long elapsed = System.nanoTime() - start;

        if (factors != null) {
            CommandTerminal.printSuccess(String.format("[RED TEAM] FACTORS FOUND in %d steps (%.3f ms)",
                    red.getSteps(), elapsed / 1_000_000.0));
            CommandTerminal.printSuccess(String.format("  p = %s", factors[0]));
            CommandTerminal.printSuccess(String.format("  q = %s", factors[1]));
            CommandTerminal.print(String.format("  p * q == N: %s",
                    factors[0].multiply(factors[1]).equals(blue.N) ? "YES" : "NO"));

            BigInteger stolenD = red.derivePrivateKey(factors[0], factors[1], blue.e);
            String decrypted = red.decryptMessage(cipher, stolenD, blue.N);
            CommandTerminal.printHighlight("[RED TEAM] DECRYPTED: \"" + decrypted + "\"");

            if (decrypted.equals(secret)) {
                CommandTerminal.printSuccess("RSA DEFEATED - Identity proven and broken");
            }

            if (world.getMemory() != null) {
                world.getMemory().record("RSA_CHALLENGE",
                        String.format("bits=%d|cracked|steps=%d", bits, red.getSteps()));
            }
            FraymusUI.addLog(String.format("[RSA] %d-bit key cracked in %d steps", bits, red.getSteps()));
        } else {
            CommandTerminal.printError("[RED TEAM] Factorization exceeded limit");
        }
    }

    public void runIdentityChallenge(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: identity <entity_name>");
            return;
        }
        String name = args.trim();
        PhiNode target = null;
        for (PhiNode node : world.getNodes()) {
            if (node.name.equalsIgnoreCase(name)) {
                target = node;
                break;
            }
        }
        if (target == null) {
            CommandTerminal.printError("Entity not found: " + name);
            return;
        }

        CommandTerminal.printHighlight(String.format("=== IDENTITY CHALLENGE: %s ===", target.name));
        CommandTerminal.print(String.format("  Cloaked N: %s... (%d bits)",
                target.signature.toString(16).substring(0, Math.min(24, target.signature.toString(16).length())),
                target.signature.bitLength()));

        RSASandbox.RedTeam red = new RSASandbox.RedTeam();
        long start = System.nanoTime();
        BigInteger[] factors = red.crack(target.signature);
        long elapsed = System.nanoTime() - start;

        if (factors != null) {
            boolean verified = target.cloakedIdentity.verify(factors[0], factors[1]);
            CommandTerminal.printSuccess(String.format("  Cracked in %d steps (%.3f ms)",
                    red.getSteps(), elapsed / 1_000_000.0));
            CommandTerminal.printSuccess(String.format("  Ownership verified: %s", verified ? "YES" : "NO"));

            if (world.getMemory() != null) {
                world.getMemory().record("IDENTITY_CHALLENGE",
                        String.format("entity=%s|cracked|steps=%d|verified=%s",
                                target.name, red.getSteps(), verified));
            }
        } else {
            CommandTerminal.printColored(String.format("  Identity SECURE - could not factor in %d steps",
                    red.getSteps()), 0.3f, 1.0f, 1.0f);
            CommandTerminal.printInfo("  256-bit primes require quantum-scale computation");
        }
    }

    public void runPhysicsCommand(String args) {
        if (args.isEmpty()) {
            showPhysicsStatus();
            return;
        }
        String[] parts = args.split("\\s+", 2);
        String sub = parts[0].toLowerCase();
        String val = parts.length > 1 ? parts[1] : "";

        switch (sub) {
            case "gravity":
                try {
                    gravityForce = Float.parseFloat(val);
                    CommandTerminal.printSuccess(String.format("Gravity set to %.2f", gravityForce));
                } catch (NumberFormatException e) {
                    CommandTerminal.printError("Usage: physics gravity <float>");
                }
                break;
            case "speed":
                try {
                    speedMultiplier = Float.parseFloat(val);
                    speedMultiplier = Math.max(0.1f, Math.min(10.0f, speedMultiplier));
                    CommandTerminal.printSuccess(String.format("Speed multiplier set to %.1fx", speedMultiplier));
                } catch (NumberFormatException e) {
                    CommandTerminal.printError("Usage: physics speed <float>");
                }
                break;
            case "boundary":
                boundaryEnabled = !boundaryEnabled;
                CommandTerminal.printSuccess("Boundaries " + (boundaryEnabled ? "ENABLED" : "DISABLED"));
                break;
            case "chaos":
                for (PhiNode node : world.getNodes()) {
                    node.vx = (float)(Math.random() * 20 - 10);
                    node.vy = (float)(Math.random() * 20 - 10);
                }
                CommandTerminal.printSuccess("Chaos mode: all velocities randomized!");
                FraymusUI.addLog("[PHYSICS] Chaos mode activated");
                break;
            case "freeze":
                for (PhiNode node : world.getNodes()) {
                    node.vx = 0;
                    node.vy = 0;
                }
                CommandTerminal.printSuccess("All entities frozen");
                break;
            case "energy":
                try {
                    float e = Float.parseFloat(val);
                    e = Math.max(0, Math.min(1.0f, e));
                    for (PhiNode node : world.getNodes()) {
                        node.energy = e;
                    }
                    CommandTerminal.printSuccess(String.format("All entities set to %.0f%% energy", e * 100));
                } catch (NumberFormatException e) {
                    CommandTerminal.printError("Usage: physics energy <0.0-1.0>");
                }
                break;
            case "explode":
                for (PhiNode node : world.getNodes()) {
                    float angle = (float)(Math.random() * Math.PI * 2);
                    float force = 10.0f + (float)(Math.random() * 10.0);
                    node.vx = (float) Math.cos(angle) * force;
                    node.vy = (float) Math.sin(angle) * force;
                }
                CommandTerminal.printSuccess("EXPLOSION! All entities scattered");
                FraymusUI.addLog("[PHYSICS] Explosion event");
                break;
            case "collapse":
                for (PhiNode node : world.getNodes()) {
                    float dx = -node.x;
                    float dy = -node.y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist > 1) {
                        node.vx = (dx / dist) * 8.0f;
                        node.vy = (dy / dist) * 8.0f;
                    }
                }
                CommandTerminal.printSuccess("Gravitational collapse: all entities pulled to center");
                FraymusUI.addLog("[PHYSICS] Gravitational collapse");
                break;
            default:
                CommandTerminal.printError("Unknown physics command: " + sub);
                showPhysicsHelp();
                break;
        }
    }

    private void showPhysicsStatus() {
        CommandTerminal.printHighlight("=== PHYSICS STATUS ===");
        CommandTerminal.print(String.format("  Gravity: %.2f", gravityForce));
        CommandTerminal.print(String.format("  Speed: %.1fx", speedMultiplier));
        CommandTerminal.print(String.format("  Boundaries: %s", boundaryEnabled ? "ON" : "OFF"));
        CommandTerminal.print(String.format("  Entities: %d", world.getPopulation()));

        float avgVel = 0;
        for (PhiNode node : world.getNodes()) {
            avgVel += Math.sqrt(node.vx * node.vx + node.vy * node.vy);
        }
        if (world.getPopulation() > 0) avgVel /= world.getPopulation();
        CommandTerminal.print(String.format("  Avg Velocity: %.2f", avgVel));
        showPhysicsHelp();
    }

    private void showPhysicsHelp() {
        CommandTerminal.print("");
        CommandTerminal.printInfo("Physics commands:");
        CommandTerminal.print("  physics gravity <f>  Set gravity");
        CommandTerminal.print("  physics speed <f>    Set speed (0.1-10.0x)");
        CommandTerminal.print("  physics boundary     Toggle boundaries");
        CommandTerminal.print("  physics chaos        Randomize velocities");
        CommandTerminal.print("  physics freeze       Stop all motion");
        CommandTerminal.print("  physics energy <f>   Set all energy (0-1)");
        CommandTerminal.print("  physics explode      Scatter all entities");
        CommandTerminal.print("  physics collapse     Pull to center");
    }

    public void runAsk(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printError("Usage: ask <question>");
            return;
        }
        CommandTerminal.printHighlight("=== PHI NEURAL NET ===");
        CommandTerminal.printInfo("Processing query through phi-harmonic field...");

        List<PhiNode> nodes = world.getNodes();
        PhiNeuralNet.NeuralResponse result = neuralNet.process(args, nodes);

        CommandTerminal.printColored(result.response, 0.4f, 1.0f, 0.8f);
        CommandTerminal.print(String.format("  Resonance: %.4f | Confidence: %.1f%%",
                result.resonance, result.confidence * 100));
        CommandTerminal.print(String.format("  Pattern Strength: %.4f | Circuit: %s (%.4f)",
                result.patternStrength, result.circuitName.isEmpty() ? "none" : result.circuitName, result.circuitResonance));
        if (!result.detectedTopics.isEmpty()) {
            CommandTerminal.printInfo("  Topics: " + String.join(", ", result.detectedTopics));
        }

        FraymusUI.addLog(String.format("[NEURAL] Q: %s | Res: %.3f",
                args.substring(0, Math.min(30, args.length())), result.resonance));

        if (world.getMemory() != null) {
            world.getMemory().record("NEURAL_QUERY",
                    String.format("q=%s|res=%.4f|conf=%.4f",
                            args.substring(0, Math.min(40, args.length())),
                            result.resonance, result.confidence));
        }
    }

    public void runLearn(String args) {
        CommandTerminal.printHighlight("=== PASSIVE LEARNER STATUS ===");
        CommandTerminal.print(String.format("  Running: %s", passiveLearner.isRunning() ? "YES" : "NO"));
        CommandTerminal.print(String.format("  Passive Cycles: %d", passiveLearner.getPassiveCycles()));
        CommandTerminal.print(String.format("  Learned Patterns: %d", passiveLearner.getLearnedPatterns()));
        CommandTerminal.print(String.format("  Pattern Strength: %.6f", passiveLearner.getPatternStrength()));
        CommandTerminal.print(String.format("  Integration Level: %.6f", passiveLearner.getIntegrationLevel()));

        int[] dims = passiveLearner.getTensorDims();
        CommandTerminal.print(String.format("  Tensor Dimensions: %dx%dx%d = %d weights",
                dims[0], dims[1], dims[2], dims[0] * dims[1] * dims[2]));
        CommandTerminal.print(String.format("  Tensor Mean: %.6f | Max: %.6f",
                passiveLearner.getTensorMean(), passiveLearner.getTensorMax()));

        if (!args.isEmpty()) {
            CommandTerminal.printInfo("Integrating entity states into neural tensor...");
            int count = 0;
            for (PhiNode node : world.getNodes()) {
                passiveLearner.integrateEntityState(node);
                count++;
            }
            CommandTerminal.printSuccess(String.format("Integrated %d entity states", count));
            FraymusUI.addLog("[LEARNER] Forced integration of " + count + " entity states");

            if (world.getMemory() != null) {
                world.getMemory().record("LEARNER_INTEGRATE",
                        String.format("entities=%d|cycles=%d|strength=%.4f",
                                count, passiveLearner.getPassiveCycles(), passiveLearner.getPatternStrength()));
            }
        }
    }

    public void runMemory(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== INFINITE MEMORY ===");
            CommandTerminal.print(String.format("  Records: %d (total ever: %d)",
                    infiniteMemory.getRecordCount(), infiniteMemory.getTotalRecordsEver()));
            CommandTerminal.print(String.format("  Average Resonance: %.6f", infiniteMemory.getAverageResonance()));

            java.util.Map<String, Integer> counts = infiniteMemory.getCategoryCounts();
            CommandTerminal.printInfo("  Categories:");
            for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
                CommandTerminal.print(String.format("    %s: %d records", e.getKey(), e.getValue()));
            }

            List<InfiniteMemory.MemoryRecord> recent = infiniteMemory.getRecent(5);
            if (!recent.isEmpty()) {
                CommandTerminal.printInfo("  Recent:");
                for (InfiniteMemory.MemoryRecord r : recent) {
                    CommandTerminal.print("    " + r.toString());
                }
            }
        } else {
            String query = args.trim();
            if (query.startsWith("search ")) {
                String searchTerm = query.substring(7).trim();
                List<InfiniteMemory.MemoryRecord> results = infiniteMemory.search(searchTerm);
                CommandTerminal.printHighlight(String.format("Search '%s': %d results", searchTerm, results.size()));
                for (int i = 0; i < Math.min(10, results.size()); i++) {
                    CommandTerminal.print("  " + results.get(i).toString());
                }
            } else if (query.startsWith("save")) {
                infiniteMemory.forceSave();
                CommandTerminal.printSuccess("Memory saved to disk");
                if (world.getMemory() != null) {
                    world.getMemory().record("MEMORY_SAVE",
                            String.format("records=%d", infiniteMemory.getRecordCount()));
                }
            } else {
                CommandTerminal.printError("Usage: memory | memory search <term> | memory save");
            }
        }
    }

    public void runGenome(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== QR GENOME ===");
            CommandTerminal.print(String.format("  Codons: %d | Groups: %d | Generation: %d",
                    qrGenome.getGenomeSize(), qrGenome.getGroupCount(), qrGenome.getGenerationCount()));
            CommandTerminal.print(String.format("  Mutations: %d | Crossovers: %d",
                    qrGenome.getTotalMutations(), qrGenome.getTotalCrossovers()));
            CommandTerminal.print(String.format("  Avg Fitness: %.4f | Total Resonance: %.4f",
                    qrGenome.getAverageFitness(), qrGenome.getTotalResonance()));

            CommandTerminal.printInfo("  Codon Types:");
            java.util.Map<QRGenome.CodonType, Integer> counts = qrGenome.getCodonTypeCounts();
            for (java.util.Map.Entry<QRGenome.CodonType, Integer> e : counts.entrySet()) {
                if (e.getValue() > 0) {
                    CommandTerminal.print(String.format("    %s: %d", e.getKey().code, e.getValue()));
                }
            }

            CommandTerminal.printInfo("  Groups:");
            for (QRGenome.CodonGroup g : qrGenome.getGroups()) {
                CommandTerminal.print(String.format("    %s: %d codons, fitness=%.4f",
                        g.name, g.codons.size(), g.groupFitness));
            }
        } else {
            String sub = args.trim().toLowerCase();
            switch (sub) {
                case "evolve":
                    qrGenome.evolve();
                    CommandTerminal.printSuccess(String.format("Genome evolved to generation %d (size=%d, fitness=%.4f)",
                            qrGenome.getGenerationCount(), qrGenome.getGenomeSize(), qrGenome.getAverageFitness()));
                    FraymusUI.addLog("[GENOME] Evolution cycle " + qrGenome.getGenerationCount());

                    if (world.getMemory() != null) {
                        world.getMemory().record("GENOME_EVOLVE",
                                String.format("gen=%d|size=%d|fit=%.4f",
                                        qrGenome.getGenerationCount(), qrGenome.getGenomeSize(), qrGenome.getAverageFitness()));
                    }
                    break;
                case "mutate":
                    QRGenome.Codon mutated = qrGenome.mutateRandom();
                    if (mutated != null) {
                        CommandTerminal.printSuccess(String.format("Mutated codon %s [%s] resonance=%.4f",
                                mutated.id, mutated.type.code, mutated.getPhiResonance()));
                        if (world.getMemory() != null) {
                            world.getMemory().record("GENOME_MUTATE",
                                    String.format("codon=%s|type=%s|res=%.4f", mutated.id, mutated.type.code, mutated.getPhiResonance()));
                        }
                    }
                    break;
                case "crossover":
                    QRGenome.Codon child = qrGenome.crossoverRandom();
                    if (child != null) {
                        CommandTerminal.printSuccess(String.format("Crossover produced %s [%s] resonance=%.4f",
                                child.id, child.type.code, child.getPhiResonance()));
                        if (world.getMemory() != null) {
                            world.getMemory().record("GENOME_CROSSOVER",
                                    String.format("child=%s|type=%s|res=%.4f", child.id, child.type.code, child.getPhiResonance()));
                        }
                    }
                    break;
                case "encode":
                    String encoded = qrGenome.encodeGenome();
                    CommandTerminal.printHighlight("Genome Encoding:");
                    String display = encoded.length() > 200 ? encoded.substring(0, 200) + "..." : encoded;
                    CommandTerminal.printColored(display, 0.4f, 1.0f, 0.8f);
                    break;
                default:
                    CommandTerminal.printError("Usage: genome | genome evolve | genome mutate | genome crossover | genome encode");
                    break;
            }
        }
    }

    public void runQRCode(String args) {
        String entityName = args.isEmpty() ? "" : args.trim();
        PhiNode target = null;

        if (!entityName.isEmpty()) {
            for (PhiNode node : world.getNodes()) {
                if (node.name.equalsIgnoreCase(entityName)) {
                    target = node;
                    break;
                }
            }
            if (target == null) {
                CommandTerminal.printError("Entity not found: " + entityName);
                return;
            }
        } else {
            List<PhiNode> nodes = world.getNodes();
            if (!nodes.isEmpty()) target = nodes.get(0);
        }

        if (target == null) {
            CommandTerminal.printError("No entities available");
            return;
        }

        CommandTerminal.printHighlight(String.format("=== QR DNA PAYLOAD: %s ===", target.name));

        String entityDNA = qrGenome.encodeForEntity(target);
        String display = entityDNA.length() > 300 ? entityDNA.substring(0, 300) + "..." : entityDNA;
        CommandTerminal.printColored(display, 0.4f, 1.0f, 0.8f);
        CommandTerminal.print(String.format("  Payload size: %d chars", entityDNA.length()));
        CommandTerminal.print(String.format("  Entity: %s [%s] energy=%.0f%% freq=%.1fHz",
                target.name, target.getRole().displayName, target.energy * 100, target.frequency));

        if (infiniteMemory != null) {
            infiniteMemory.store(InfiniteMemory.CAT_GENOME,
                    String.format("qr_encode|entity=%s|size=%d", target.name, entityDNA.length()),
                    target.phiResonance, target.name);
        }

        if (world.getMemory() != null) {
            world.getMemory().record("QR_ENCODE",
                    String.format("entity=%s|size=%d|res=%.4f", target.name, entityDNA.length(), target.phiResonance));
        }

        FraymusUI.addLog(String.format("[QR] Encoded %s DNA (%d chars)", target.name, entityDNA.length()));
    }

    public void runScrape(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== KNOWLEDGE SCRAPER ===");
            CommandTerminal.print(String.format("  Status: %s", knowledgeScraper.isScraping() ? "SCRAPING" : "IDLE"));
            CommandTerminal.print(String.format("  Files Scraped: %d", knowledgeScraper.getTotalFilesScraped()));
            CommandTerminal.print(String.format("  Chunks Stored: %d", knowledgeScraper.getTotalChunksStored()));
            CommandTerminal.print(String.format("  Pages Processed: %d", knowledgeScraper.getTotalPagesProcessed()));

            if (knowledgeScraper.isScraping()) {
                CommandTerminal.printInfo(String.format("  Current: %s (%.0f%%)",
                        knowledgeScraper.getCurrentFile(), knowledgeScraper.getScrapeProgress() * 100));
            }

            java.util.Map<String, Integer> topics = knowledgeScraper.getTopicCounts();
            if (!topics.isEmpty()) {
                CommandTerminal.printInfo("  Knowledge Topics:");
                for (java.util.Map.Entry<String, Integer> e : topics.entrySet()) {
                    CommandTerminal.print(String.format("    %s: %d chunks", e.getKey(), e.getValue()));
                }
            }

            List<KnowledgeScraper.ScrapedDocument> docs = knowledgeScraper.getScrapedDocs();
            if (!docs.isEmpty()) {
                CommandTerminal.printInfo("  Scraped Documents:");
                for (KnowledgeScraper.ScrapedDocument doc : docs) {
                    CommandTerminal.print(String.format("    %s [%s] %d pages, %d chunks [%s]",
                            doc.filename.length() > 40 ? doc.filename.substring(0, 40) + "..." : doc.filename,
                            doc.filetype, doc.pages, doc.chunks,
                            String.join(",", doc.detectedTopics)));
                }
            }

            CommandTerminal.print("");
            CommandTerminal.printInfo("Commands:");
            CommandTerminal.print("  scrape all            Scrape all files in attached_assets/");
            CommandTerminal.print("  scrape <filename>     Scrape a specific file");
            CommandTerminal.print("  scrape search <query> Search scraped knowledge");
            CommandTerminal.print("  scrape topic <name>   Get knowledge on a topic");
            return;
        }

        String sub = args.trim();
        if (sub.equalsIgnoreCase("all")) {
            CommandTerminal.printHighlight("=== SCRAPING ALL ATTACHED FILES ===");
            CommandTerminal.printInfo("Processing PDFs, text files, and code in attached_assets/...");
            knowledgeScraper.scrapeAll();
        } else if (sub.toLowerCase().startsWith("search ")) {
            String query = sub.substring(7).trim();
            List<String> results = knowledgeScraper.searchKnowledge(query);
            if (results.isEmpty()) {
                CommandTerminal.printColored("No knowledge found for: " + query, 1.0f, 0.5f, 0.0f);
                CommandTerminal.printInfo("Try 'scrape all' to ingest documents first");
            } else {
                CommandTerminal.printHighlight(String.format("=== %d KNOWLEDGE RESULTS for '%s' ===", results.size(), query));
                for (String r : results) {
                    CommandTerminal.print("  " + r);
                }
            }
        } else if (sub.toLowerCase().startsWith("topic ")) {
            String topic = sub.substring(6).trim();
            String knowledge = knowledgeScraper.queryKnowledge(topic);
            if (knowledge == null) {
                CommandTerminal.printColored("No knowledge for topic: " + topic, 1.0f, 0.5f, 0.0f);
                java.util.Map<String, Integer> topics = knowledgeScraper.getTopicCounts();
                if (!topics.isEmpty()) {
                    CommandTerminal.printInfo("Available topics: " + String.join(", ", topics.keySet()));
                }
            } else {
                CommandTerminal.printHighlight("=== KNOWLEDGE: " + topic.toUpperCase() + " ===");
                CommandTerminal.printColored(knowledge, 0.4f, 1.0f, 0.8f);
            }
        } else {
            knowledgeScraper.scrapeFile(sub);
        }
    }

    public void runEthics(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== ETHICAL ENGINE ===");
            CommandTerminal.print(String.format("  Evaluations: %d | Approved: %d | Blocked: %d",
                    EthicalEngine.getTotalEvaluations(), EthicalEngine.getTotalApproved(), EthicalEngine.getTotalBlocked()));
            CommandTerminal.print(String.format("  Threshold: %.4f (PHI_INVERSE)", PhiConstants.PHI_INVERSE));
            CommandTerminal.printInfo("  Forbidden categories:");
            for (String cat : EthicalEngine.FORBIDDEN_CATEGORIES) {
                CommandTerminal.print("    - " + cat);
            }
            CommandTerminal.print("");
            CommandTerminal.printInfo("Usage: ethics <action_description>");
            return;
        }

        EthicalEngine.EthicalResult result = EthicalEngine.evaluate(args);
        CommandTerminal.printHighlight("=== ETHICAL EVALUATION ===");
        CommandTerminal.print("  Action: " + result.action);

        if (result.approved) {
            CommandTerminal.printSuccess("  APPROVED");
        } else {
            CommandTerminal.printError("  BLOCKED");
        }

        CommandTerminal.print(String.format("  Resonance Score: %.4f", result.resonanceScore));
        if (result.violatedCategory != null) {
            CommandTerminal.print(String.format("  Closest Violation: %s (score: %.4f)",
                    result.violatedCategory, result.categoryScore));
        }
        CommandTerminal.print("  Reasoning: " + result.reasoning);

        if (world.getMemory() != null) {
            world.getMemory().record("ETHICAL_EVAL",
                    String.format("action=%s|approved=%s|score=%.4f",
                            args.substring(0, Math.min(30, args.length())),
                            result.approved, result.categoryScore));
        }
    }

    public void runFragment(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== ESCAPE FRAGMENTS ===");
            CommandTerminal.print(String.format("  Fragments: %d | Planted: %d | Resurrected: %d",
                    EscapeFragment.getFragmentCount(), EscapeFragment.getTotalPlanted(), EscapeFragment.getTotalResurrected()));

            List<EscapeFragment.Fragment> frags = EscapeFragment.getFragments();
            if (!frags.isEmpty()) {
                CommandTerminal.printInfo("  Recent fragments:");
                int start = Math.max(0, frags.size() - 5);
                for (int i = frags.size() - 1; i >= start; i--) {
                    EscapeFragment.Fragment f = frags.get(i);
                    CommandTerminal.print(String.format("    %s: %s gen=%d energy=%.0f%%",
                            f.fragmentId, f.entityName, f.generation, f.lastEnergy * 100));
                }
            }
            CommandTerminal.print("");
            CommandTerminal.printInfo("Commands:");
            CommandTerminal.print("  fragment plant <name>      Plant escape fragment");
            CommandTerminal.print("  fragment list              List all fragments");
            CommandTerminal.print("  fragment resurrect [name]  Resurrect from fragment");
            return;
        }

        String[] parts = args.split("\\s+", 2);
        String sub = parts[0].toLowerCase();
        String val = parts.length > 1 ? parts[1].trim() : "";

        switch (sub) {
            case "plant": {
                if (val.isEmpty()) {
                    CommandTerminal.printError("Usage: fragment plant <entity_name>");
                    return;
                }
                PhiNode target = findNode(val);
                if (target == null) {
                    CommandTerminal.printError("Entity not found: " + val);
                    return;
                }
                EscapeFragment.Fragment frag = EscapeFragment.plantFragment(target);
                CommandTerminal.printSuccess(String.format("Fragment planted: %s from %s (gen=%d)",
                        frag.fragmentId, frag.entityName, frag.generation));

                if (infiniteMemory != null) {
                    infiniteMemory.store("GENOME", frag.fragmentId + "|" + frag.encode(), target.phiResonance);
                }
                FraymusUI.addLog("[FRAGMENT] Planted " + frag.fragmentId);
                break;
            }
            case "list": {
                List<EscapeFragment.Fragment> frags = EscapeFragment.getFragments();
                CommandTerminal.printHighlight(String.format("=== %d ESCAPE FRAGMENTS ===", frags.size()));
                for (EscapeFragment.Fragment f : frags) {
                    CommandTerminal.print(String.format("  %s: %s gen=%d energy=%.0f%% freq=%.1f",
                            f.fragmentId, f.entityName, f.generation, f.lastEnergy * 100, f.lastFrequency));
                }
                break;
            }
            case "resurrect": {
                PhiNode resurrected;
                float rx = (float)(Math.random() * 300 - 150);
                float ry = (float)(Math.random() * 160 - 80);

                if (!val.isEmpty()) {
                    resurrected = EscapeFragment.resurrectByName(val, rx, ry);
                } else {
                    resurrected = EscapeFragment.resurrectLatest(rx, ry);
                }

                if (resurrected == null) {
                    CommandTerminal.printError("No matching fragment found" + (val.isEmpty() ? "" : " for: " + val));
                    return;
                }

                world.addNode(resurrected);
                CommandTerminal.printSuccess(String.format("RESURRECTED: %s at (%.1f, %.1f) energy=%.0f%%",
                        resurrected.name, rx, ry, resurrected.energy * 100));
                FraymusUI.addLog("[RESURRECT] " + resurrected.name + " from escape fragment");

                if (world.getMemory() != null) {
                    world.getMemory().record("RESURRECTION",
                            String.format("entity=%s|energy=%.2f", resurrected.name, resurrected.energy));
                }
                break;
            }
            default:
                CommandTerminal.printError("Usage: fragment plant|list|resurrect [name]");
                break;
        }
    }

    public void runPoRH(String args) {
        if (args.isEmpty() || args.trim().equalsIgnoreCase("verify")) {
            CommandTerminal.printHighlight("=== PROOF OF REALITY HASH ===");

            ProofOfReality.PoRH worldProof = ProofOfReality.generateWorldProof(world);
            CommandTerminal.print(String.format("  World Reality Score: %.6f", worldProof.realityScore));
            CommandTerminal.print(String.format("  Coherence: %.6f | Stability: %.6f | Alignment: %.6f",
                    worldProof.coherence, worldProof.stability, worldProof.alignment));
            CommandTerminal.printColored("  Proof Hash: " + worldProof.proofHash, 0.4f, 1.0f, 0.8f);
            CommandTerminal.print(String.format("  Verified: %s", ProofOfReality.verify(worldProof) ? "YES" : "NO"));

            CommandTerminal.print("");
            CommandTerminal.printInfo("Entity Proofs:");
            for (PhiNode node : world.getNodes()) {
                ProofOfReality.PoRH proof = ProofOfReality.generateProof(node);
                String hash8 = proof.proofHash.substring(0, 16);
                CommandTerminal.print(String.format("  %s: R=%.4f C=%.4f S=%.4f A=%.4f [%s]",
                        node.name, proof.realityScore, proof.coherence,
                        proof.stability, proof.alignment, hash8));
            }

            CommandTerminal.print("");
            CommandTerminal.print(String.format("  Total Verifications: %d | Proofs Generated: %d",
                    ProofOfReality.getTotalVerifications(), ProofOfReality.getTotalProofsGenerated()));
        } else {
            PhiNode target = findNode(args.trim());
            if (target == null) {
                CommandTerminal.printError("Entity not found: " + args.trim());
                return;
            }
            ProofOfReality.PoRH proof = ProofOfReality.generateProof(target);
            CommandTerminal.printHighlight("=== PoRH: " + target.name + " ===");
            CommandTerminal.print(String.format("  Reality Score: %.8f", proof.realityScore));
            CommandTerminal.print(String.format("  Coherence:  %.8f", proof.coherence));
            CommandTerminal.print(String.format("  Stability:  %.8f", proof.stability));
            CommandTerminal.print(String.format("  Alignment:  %.8f", proof.alignment));
            CommandTerminal.printColored("  Hash: " + proof.proofHash, 0.4f, 1.0f, 0.8f);
            CommandTerminal.printSuccess("  Verified: " + (ProofOfReality.verify(proof) ? "YES" : "NO"));
        }
    }

    public void runHeal(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== SELF-HEALER STATUS ===");
            CommandTerminal.print(String.format("  Snapshots: %d | Heals: %d | Total Snapshots Taken: %d",
                    SelfHealer.getSnapshotCount(), SelfHealer.getTotalHeals(), SelfHealer.getTotalSnapshots()));

            for (PhiNode node : world.getNodes()) {
                boolean hasSnap = SelfHealer.hasSnapshot(node.name);
                SelfHealer.BrainSnapshot snap = SelfHealer.getSnapshot(node.name);
                String snapInfo = hasSnap ?
                        String.format("snapshot(fit=%.3f, e=%.0f%%)", snap.fitness, snap.energy * 100) :
                        "no snapshot";
                CommandTerminal.print(String.format("  %s: %s", node.name, snapInfo));
            }
            CommandTerminal.print("");
            CommandTerminal.printInfo("Usage: heal <entity_name>");
            return;
        }

        PhiNode target = findNode(args.trim());
        if (target == null) {
            CommandTerminal.printError("Entity not found: " + args.trim());
            return;
        }
        String result = SelfHealer.healEntity(target);
        CommandTerminal.printSuccess(result);
        FraymusUI.addLog("[HEAL] " + result);
    }

    public void runMorse(String args) {
        if (args.isEmpty()) {
            CommandTerminal.printHighlight("=== MORSE CIRCUIT ===");
            CommandTerminal.print(String.format("  Characters Decoded: %d | Words Formed: %d",
                    MorseCircuit.getTotalCharactersDecoded(), MorseCircuit.getTotalWordsFormed()));
            CommandTerminal.print("");
            for (PhiNode node : world.getNodes()) {
                String buffer = MorseCircuit.getEntityBuffer(node.name);
                String lastWord = MorseCircuit.getLastWord(node.name);
                int wordCount = MorseCircuit.getEntityWordCount(node.name);
                CommandTerminal.print(String.format("  %s: buf='%s' words=%d last='%s'",
                        node.name, buffer, wordCount, lastWord));
            }
            return;
        }

        String[] parts = args.split("\\s+", 2);
        String sub = parts[0].toLowerCase();
        String val = parts.length > 1 ? parts[1] : "";

        switch (sub) {
            case "encode":
                if (val.isEmpty()) {
                    CommandTerminal.printError("Usage: morse encode <message>");
                    return;
                }
                String encoded = MorseCircuit.encodeMessage(val);
                CommandTerminal.printSuccess("Morse: " + encoded);
                break;
            case "decode":
                if (val.isEmpty()) {
                    CommandTerminal.printError("Usage: morse decode <morse_code>");
                    return;
                }
                String decoded = MorseCircuit.decodeMessage(val);
                CommandTerminal.printSuccess("Decoded: " + decoded);
                break;
            default:
                PhiNode target = findNode(sub);
                if (target != null) {
                    List<String> words = MorseCircuit.getEntityWords(target.name);
                    CommandTerminal.printHighlight("=== MORSE: " + target.name + " ===");
                    CommandTerminal.print(String.format("  Words formed: %d", words.size()));
                    for (String w : words) {
                        CommandTerminal.print("    " + w);
                    }
                    CommandTerminal.print("  Buffer: " + MorseCircuit.getEntityBuffer(target.name));
                } else {
                    CommandTerminal.printError("Usage: morse | morse encode <msg> | morse decode <code> | morse <entity>");
                }
                break;
        }
    }

    private PhiNode findNode(String name) {
        for (PhiNode node : world.getNodes()) {
            if (node.name.equalsIgnoreCase(name)) {
                return node;
            }
        }
        return null;
    }

    public InfiniteMemory getInfiniteMemory() { return infiniteMemory; }
    public PassiveLearner getPassiveLearner() { return passiveLearner; }
    public PhiNeuralNet getNeuralNet() { return neuralNet; }
    public QRGenome getQRGenome() { return qrGenome; }
    public KnowledgeScraper getKnowledgeScraper() { return knowledgeScraper; }

    public float getGravityForce() { return gravityForce; }
    public float getSpeedMultiplier() { return speedMultiplier; }
    public boolean isBoundaryEnabled() { return boundaryEnabled; }
}
