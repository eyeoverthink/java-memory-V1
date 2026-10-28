package fraymus.specialists;

import fraymus.core.FraymusJSON;
import fraymus.genomics.DigitalGenomeArchive;
import fraymus.llm.SpecialistModelRegistry;
import fraymus.embodied.EmbodiedAI;
import fraymus.worldmodel.WorldModel;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

/** Specialist process contracts, not interchangeable chat model aliases. */
public final class SpecialistModelService {
    private SpecialistModelService() {}

    public static boolean appliesTo(String text) {
        return text != null && java.util.regex.Pattern.compile("(?i)\\b(alphaprotein|botanic1|botanic-1|needle3|needle-3|jev|enigma|odyssey|odessay|odessey|minimax|frayvm|plasmid|fraymus-brain|robot|robotics|physics|scene|akashic|chain)\\b").matcher(text).find();
    }

    public static String contextFor(String query) {
        if (!appliesTo(query)) return "";
        try {
            String context = "\nTYPED SPECIALIST CAPABILITIES:\n" + FraymusJSON.stringify(new SpecialistModelRegistry().all())
                + "\nUse [SPECIALIST: list], [SPECIALIST: describe <id>], [SPECIALIST: needle <request.json>], [SPECIALIST: brain <request.json>], [SPECIALIST: enigma <request.json>], [SPECIALIST: chain <request.json>] or [SPECIALIST: robot <request.json>] or [SPECIALIST: odyssey <request.json>]. "
                + "Read actual tool evidence. Needle only proposes calls; execute through the existing validated command flow. "
                + "Gated, missing-dependency and hosted providers are unavailable for local inference until their stated requirements are met.\n";
            Path pack = Path.of("skills/open_model_sources/generated/fraymus-specialists.md");
            if (Files.isRegularFile(pack)) context += "\n" + Files.readString(pack);
            Path latest = Path.of(".specialists/genomes/latest-native-mind.json");
            if (Files.isRegularFile(latest)) {
                var receipt = DigitalGenomeArchive.readMap(latest);
                String facts = FraymusJSON.stringify(DigitalGenomeArchive.readPayload(Path.of((String) receipt.get("manifest"))));
                context += "\nACTUAL SAVED NATIVE MIND (verified software-state capsule):\n" + facts.substring(0, Math.min(16000, facts.length()));
            }
            Path robot = Path.of(".specialists/genomes/latest-robot-brains.json");
            if (Files.isRegularFile(robot)) {
                var receipt = DigitalGenomeArchive.readMap(robot);
                var payload = DigitalGenomeArchive.readPayload(Path.of((String) receipt.get("manifest")));
                context += "\nVERIFIED ROBOT BRAIN STATE: " + FraymusJSON.stringify(Map.of("manifest", receipt.get("manifest"),
                    "format", payload.get("format"), "scope", payload.get("scope"),
                    "committed_feedback_commands", ((List<?>) payload.get("applied_feedback")).size()))
                    + ". Serialized parameters are restored into the wired brains, not inserted as prompt text.\n";
            }
            return context;
        } catch (Exception error) { return "\nSpecialist registry unavailable: " + error.getMessage() + "\n"; }
    }

    public static Map<String, Object> configuration() throws IOException {
        Path path = Path.of(System.getProperty("fraynix.specialistConfig",
            Path.of(System.getProperty("user.home"), ".fraymus", "models", "specialist-runtime.json").toString()));
        return Files.isRegularFile(path) ? DigitalGenomeArchive.readMap(path) : Map.of();
    }

    @SuppressWarnings("unchecked") public static Map<String, Object> execute(String arguments, WorldModel world, EmbodiedAI embodied) throws Exception {
        String[] parts = arguments.trim().split("\\s+", 2);
        String operation = parts.length == 0 || parts[0].isBlank() ? "list" : parts[0];
        String tail = parts.length == 2 ? parts[1].trim().replaceAll("^\"|\"$", "") : "";
        var registry = new SpecialistModelRegistry();
        if (operation.equals("odyssey") || operation.equals("odyssey3") || operation.equals("odessey-3")) {
            Path configPath = Path.of(System.getProperty("fraynix.odysseyConfig", "data/specialists/odyssey-runtime.json")).toAbsolutePath();
            var config = DigitalGenomeArchive.readMap(configPath);
            String models = System.getenv().getOrDefault("FRAYNIX_MODEL_ROOT", "F:/Fraynix-Models");
            String python = String.valueOf(config.get("python")).replace("${MODEL_ROOT}", models);
            if (!Files.isRegularFile(Path.of(python))) throw new IOException("Odyssey Python runtime is missing: " + python);
            Path request = Path.of(tail).toAbsolutePath();
            long timeout = ((Number) config.getOrDefault("request_timeout_seconds", 480)).longValue();
            if (timeout <= 0 || timeout > 86400) throw new IOException("Odyssey timeout must be 1..86400 seconds");
            return runWorker("odyssey", List.of(python, Path.of("scripts/specialists/odyssey_worker.py").toAbsolutePath().toString(),
                "--config", configPath.toString(), request.toString()), request, timeout + 20);
        }
        if (operation.equals("chain")) {
            var request = DigitalGenomeArchive.readMap(Path.of(tail));
            var memory = new fraymus.knowledge.AkashicRecord();
            Object mode = request.getOrDefault("operation", "audit");
            Map<String,Object> result = switch (String.valueOf(mode)) {
                case "audit" -> memory.integrityReport();
                case "recover" -> Map.of("restored_records", memory.restoreArchivedRecords(), "integrity", memory.integrityReport());
                case "repair" -> memory.reconcileChain();
                default -> throw new IOException("Chain operation must be audit, recover or repair");
            };
            Path report = Path.of("reports/specialists/chain-" + UUID.randomUUID() + ".json").toAbsolutePath();
            DigitalGenomeArchive.atomicWrite(report, DigitalGenomeArchive.jsonBytes(result));
            return Map.of("ok", true, "operation", "chain-" + mode, "integrity", result,
                "report", report.toString(), "report_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(report)),
                "request", Path.of(tail).toAbsolutePath().toString());
        }
        if (operation.equals("profile")) {
            Path latest = Path.of(".specialists/genomes/latest-specialist-profile.json");
            if (!Files.isRegularFile(latest)) return Map.of("ok", true, "state", "No specialist evidence has been absorbed yet");
            var receipt = DigitalGenomeArchive.readMap(latest);
            return Map.of("ok", true, "digital_genome", receipt,
                "profile", DigitalGenomeArchive.readPayload(Path.of((String) receipt.get("manifest"))));
        }
        if (operation.equals("list") || operation.equals("status") || operation.equals("learn")) {
            var configuration = configuration();
            Map<String, Object> runtime = new LinkedHashMap<>();
            if (configuration.get("needle_config") instanceof String file && Files.isRegularFile(Path.of(file))) {
                var installed = DigitalGenomeArchive.readMap(Path.of(file));
                boolean verified = true;
                for (String artifact : List.of("weights", "library")) verified &= Files.isRegularFile(Path.of((String) installed.get(artifact)))
                    && DigitalGenomeArchive.sha(Files.readAllBytes(Path.of((String) installed.get(artifact)))).equals(installed.get(artifact + "_sha256"));
                runtime.put("needle3", Map.of("artifacts_verified", verified, "revision", installed.get("revision"), "network_required", false));
            }
            if (configuration.get("minimax_cli") instanceof String cli) runtime.put("minimax_code", Map.of("cli_present", Files.isRegularFile(Path.of(cli)), "model_inference_verified", false));
            if (configuration.get("physics_root") instanceof String physics) runtime.put("fraymus_scene", Map.of("root", physics,
                "worker_classpath_present", Files.isRegularFile(Path.of(physics,"build/robot-scene/classpath.txt")), "physical_hardware_executed", false));
            try { runtime.put("owner_sources", integrationRoot(configuration).toString()); } catch (IOException ignored) { runtime.put("owner_sources", "unavailable"); }
            return Map.of("ok", true, "models", registry.all(), "runtime_state", runtime,
                "weight_updates_performed", false);
        }
        if (operation.equals("describe")) return Map.of("ok", true, "specialist", registry.find(tail));
        if (operation.equals("minimax-code")) {
            Map<String, Object> config = configuration();
            if (!(config.get("minimax_cli") instanceof String cli) || !Files.isRegularFile(Path.of(cli))) throw new IOException("MiniMax Code CLI is not installed/configured");
            List<String> command = new ArrayList<>(List.of(String.valueOf(config.getOrDefault("node", "node")), cli));
            boolean versionOnly = tail.equals("version");
            if (!versionOnly && !Boolean.TRUE.equals(config.get("allow_network"))) throw new IOException("MiniMax inference is disabled in offline mode; provider login and explicit network configuration are required");
            if (versionOnly) command.add("--version");
            else command.addAll(List.of("exec", "--mode", "lightweight", "--input", "-", "--input-format", "text", "--output-format", "json", "--timeout", "120s", "--max-steps", "1"));
            Path directory = Files.createDirectories(Path.of("reports/specialists/minimax-" + UUID.randomUUID()));
            var builder = new ProcessBuilder(command).directory(directory.toFile());
            builder.environment().put("MCODE_DISABLE_TELEMETRY", "1"); builder.environment().put("DO_NOT_TRACK", "1");
            if (config.get("minimax_data_dir") instanceof String data) builder.environment().put("MINIMAX_DATA_DIR", data);
            Path output = directory.resolve("output.json"), errors = directory.resolve("error.log");
            Process process = builder.redirectOutput(output.toFile()).redirectError(errors.toFile()).start();
            try {
                if (!versionOnly) {
                    var request = DigitalGenomeArchive.readMap(Path.of(tail));
                    if (!(request.get("goal") instanceof String goal) || goal.isBlank()) throw new IOException("MiniMax request needs a goal");
                    process.getOutputStream().write(goal.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }
                process.getOutputStream().close();
                if (!process.waitFor(135, TimeUnit.SECONDS)) throw new IOException("MiniMax Code timed out");
                if (process.exitValue() != 0) throw new IOException("MiniMax Code failed; inspect " + errors.toAbsolutePath());
                if (versionOnly) return Map.of("ok", true, "backend", "minimax-code-cli", "cli_version", Files.readString(output).trim(), "model_inference_executed", false);
                Map<String, Object> response = DigitalGenomeArchive.readMap(output);
                if (!(response.get("schemaVersion") instanceof Number schema) || schema.intValue() != 1) throw new IOException("Unsupported MiniMax result envelope");
                return Map.of("ok", true, "backend", "minimax-code-cli", "provider_result", response,
                    "request", Path.of(tail).toAbsolutePath().toString(), "request_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(tail))),
                    "report", output.toAbsolutePath().toString(), "report_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(output)),
                    "candidate_requires_existing_verifier", true);
            } finally { if (process.isAlive()) process.destroyForcibly(); }
        }
        if (operation.equals("enigma") || operation.equals("brain")) {
            Map<String, Object> config = configuration();
            Path root = integrationRoot(config), request = Path.of(tail).toAbsolutePath();
            List<String> command;
            String node = String.valueOf(config.getOrDefault("node", "node"));
            if (operation.equals("enigma")) command = List.of(String.valueOf(config.getOrDefault("python", "python")),
                Path.of("scripts/specialists/enigma_worker.py").toAbsolutePath().toString(), "--root", root.toString(), "--node", node,
                "--output-dir", Path.of("reports/specialists/artifacts").toAbsolutePath().toString(), request.toString());
            else command = List.of(node, Path.of("scripts/specialists/native_brain_worker.mjs").toAbsolutePath().toString(),
                root.toString(), request.toString(), Path.of(".specialists/native-mind.json").toAbsolutePath().toString(),
                Path.of("reports/specialists/artifacts").toAbsolutePath().toString());
            return runWorker(operation, command, request);
        }
        if (operation.equals("needle")) {
            Map<String, Object> config = configuration();
            if (!(config.get("needle_config") instanceof String installed)) throw new IOException("Needle3 runtime is not configured");
            Path request = Path.of(tail).toAbsolutePath();
            Map<String, Object> runtime = DigitalGenomeArchive.readMap(Path.of(installed));
            List<String> command = List.of((String) runtime.get("python"), Path.of("scripts/specialists/needle_worker.py").toAbsolutePath().toString(),
                "--config", installed, request.toString());
            return runWorker("needle", command, request);
        }
        if (operation.equals("robot")) {
            if (world == null || embodied == null) throw new IOException("Existing WorldModel and EmbodiedAI brains are not wired");
            Map<String, Object> request = DigitalGenomeArchive.readMap(Path.of(tail));
            if ("fraymus-scene".equals(request.get("backend"))) return RobotPhysicsBridge.execute(Path.of(tail).toAbsolutePath(), configuration(), world, embodied);
            if (!"fraynix-native".equals(request.get("backend"))) throw new IOException("No Odyssey3 robot decoder is configured; select fraynix-native for the existing local brains");
            if (!(request.get("frame") instanceof String frame) || frame.isBlank()
                    || !(request.get("units") instanceof List<?> units) || !(request.get("timestamp_ms") instanceof Number timestamp)
                    || timestamp.longValue() < 0) throw new IOException("Robot observation requires a frame, per-channel units and timestamp_ms");
            double[] sensory = vector(request.get("observation")), proprioception = vector(request.get("proprioception"));
            if (sensory.length != units.size() || sensory.length != world.getSensoryDimension()
                    || proprioception.length != embodied.getMotorDimension())
                throw new IOException("Robot observation/units or wired brain dimensions differ; expected sensory="
                    + world.getSensoryDimension() + ", body=" + embodied.getMotorDimension());
            int[] channels = new int[embodied.getSensoryDimension()];
            if (request.get("body_channels") instanceof List<?> selected) {
                if (selected.size() != channels.length) throw new IOException("Body channel mapping has the wrong dimension");
                for (int i = 0; i < channels.length; i++) {
                    if (!(selected.get(i) instanceof Number index) || index.doubleValue() != index.intValue()
                            || index.intValue() < 0 || index.intValue() >= sensory.length) throw new IOException("Invalid body feature channel");
                    channels[i] = index.intValue();
                }
            } else {
                if (channels.length != sensory.length) throw new IOException("The wired brains use different dimensions; provide explicit body_channels mapping");
                for (int i = 0; i < channels.length; i++) channels[i] = i;
            }
            double[] observed = null, observedBody = null; double feedbackReward = 0;
            if (request.get("feedback") instanceof Map<?, ?> feedback) {
                if (!"observed".equals(feedback.get("kind")) || !frame.equals(feedback.get("frame")) || !units.equals(feedback.get("units"))
                        || !(feedback.get("timestamp_ms") instanceof Number later) || later.longValue() <= timestamp.longValue()
                        || !(feedback.get("reward") instanceof Number reward) || !Double.isFinite(reward.doubleValue()))
                    throw new IOException("Feedback must be a later observed result with matching frame/units and finite reward");
                observed = vector(feedback.get("observation")); observedBody = vector(feedback.get("proprioception"));
                feedbackReward = reward.doubleValue();
                if (observed.length != sensory.length || observedBody.length != proprioception.length) throw new IOException("Feedback dimensions differ");
            }
            world.processInput(sensory);
            double[] prediction = world.predictNextState(sensory);
            EmbodiedAI.MotorCommand proposal = embodied.process(new EmbodiedAI.SensoryInput(select(sensory, channels), proprioception));
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("ok", true); result.put("backend", "fraynix-native"); result.put("frame", frame);
            result.put("timestamp_ms", timestamp); result.put("units", units); result.put("prediction", numbers(prediction));
            result.put("body_channels", Arrays.stream(channels).boxed().toList());
            result.put("motor_proposal", Map.of("action_type", proposal.actionType, "normalized_vector", numbers(proposal.vector), "physical_units", "uncalibrated; device adapter required"));
            result.put("executed", false); result.put("feedback_applied", false);
            if (observed != null) {
                embodied.receiveFeedback(new EmbodiedAI.SensoryInput(select(observed, channels), observedBody), feedbackReward);
                result.put("feedback_applied", true);
            }
            result.put("body_statistics", Map.of("interactions", embodied.getStatistics().totalInteractions, "average_reward", embodied.getStatistics().averageReward));
            result.put("world_statistics", world.getStatistics().toString());
            result.put("request", Path.of(tail).toAbsolutePath().toString());
            result.put("request_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(tail))));
            return result;
        }
        Map<String, Object> unavailable = registry.find(operation);
        throw new IOException("Specialist unavailable: " + FraymusJSON.stringify(unavailable));
    }

    private static Path integrationRoot(Map<String, Object> config) throws IOException {
        Object configured = config.get("integration_root");
        if (configured instanceof String value && Files.isRegularFile(Path.of(value, "source-intake.json"))) return Path.of(value).toAbsolutePath();
        for (Path directory = Path.of(System.getProperty("user.dir")).toAbsolutePath(); directory != null; directory = directory.getParent()) {
            Path candidate = directory.resolve("integrations/fraymus-system-new");
            if (Files.isRegularFile(candidate.resolve("source-intake.json"))) return candidate;
        }
        throw new IOException("Owner source integration is not present; configure integration_root");
    }

    private static Map<String, Object> runWorker(String name, List<String> command, Path request) throws Exception {
        return runWorker(name, command, request, 120);
    }

    private static Map<String, Object> runWorker(String name, List<String> command, Path request, long timeout) throws Exception {
        Path logs = Files.createDirectories(Path.of("reports/specialists"));
        String id = UUID.randomUUID().toString();
        Path output = logs.resolve(name + "-" + id + ".json"), errors = logs.resolve(name + "-" + id + ".log");
        ProcessBuilder builder = new ProcessBuilder(command).redirectOutput(output.toFile()).redirectError(errors.toFile());
        if (name.equals("odyssey")) {
            builder.environment().remove("PYTHONHOME"); builder.environment().remove("PYTHONPATH");
            builder.environment().put("PYTHONNOUSERSITE", "1");
        }
        Process worker = builder.start();
        try {
            if (!worker.waitFor(timeout, TimeUnit.SECONDS)) throw new IOException(name + " worker timed out: " + errors);
            Map<String, Object> result = DigitalGenomeArchive.readMap(output);
            if (worker.exitValue() != 0 || !Boolean.TRUE.equals(result.get("ok"))) throw new IOException(name + " failed: " + result);
            Map<String, Object> enriched = new LinkedHashMap<>(result);
            enriched.put("report", output.toAbsolutePath().toString()); enriched.put("request", request.toString());
            enriched.put("report_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(output)));
            return enriched;
        } finally { if (worker.isAlive()) { worker.descendants().forEach(ProcessHandle::destroyForcibly); worker.destroyForcibly(); } }
    }

    private static double[] vector(Object raw) throws IOException {
        if (!(raw instanceof List<?> values) || values.isEmpty()) throw new IOException("Expected a nonempty numeric vector");
        double[] result = new double[values.size()];
        for (int i = 0; i < result.length; i++) {
            if (!(values.get(i) instanceof Number n) || !Double.isFinite(n.doubleValue())) throw new IOException("Non-finite or non-numeric robot observation");
            result[i] = n.doubleValue();
        }
        return result;
    }
    private static double[] select(double[] vector, int[] channels) {
        double[] result = new double[channels.length];
        for (int i = 0; i < result.length; i++) result[i] = vector[channels[i]];
        return result;
    }
    private static List<Double> numbers(double[] values) throws IOException {
        List<Double> result = new ArrayList<>();
        for (double value : values) { if (!Double.isFinite(value)) throw new IOException("Non-finite brain output"); result.add(value); }
        return result;
    }
}
