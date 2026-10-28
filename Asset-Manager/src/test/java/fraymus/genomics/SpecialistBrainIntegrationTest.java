package fraymus.genomics;

import fraymus.FraynixBoot;
import fraymus.core.FraymusJSON;
import fraymus.brain.unified.UnifiedBrainOrchestrator;
import fraymus.knowledge.AkashicRecord;
import fraymus.neural.AeonKronos;
import fraymus.worldmodel.WorldModel;
import fraymus.embodied.EmbodiedAI;
import java.nio.file.*;
import java.util.*;

/** Real local workers and shared-brain persistence. Run only in an isolated fixture, twice. */
public final class SpecialistBrainIntegrationTest {
    private static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    private static void field(String name, Object value) throws Exception {
        var field = FraynixBoot.class.getDeclaredField(name); field.setAccessible(true); field.set(null, value);
    }
    private static CarbonGenomicsBridge.Result raw(String arguments) throws Exception {
        var method = FraynixBoot.class.getDeclaredMethod("executeSpecialistCommand", String.class);
        method.setAccessible(true); return (CarbonGenomicsBridge.Result) method.invoke(null, arguments);
    }
    @SuppressWarnings("unchecked") private static Map<String, Object> call(String arguments) throws Exception {
        var result = raw(arguments); require(result.success(), result.output());
        return (Map<String, Object>) FraymusJSON.parseStrict(result.output());
    }
    private static String request(String name, Map<String, Object> value) throws Exception {
        Path path = Path.of(name + ".json"); Files.writeString(path, FraymusJSON.stringify(value)); return path.toString();
    }
    @SuppressWarnings("unchecked") public static void main(String[] arguments) throws Exception {
        System.setProperty("fraynix.kronos.bootstrap.enabled", "false");
        var memory = new AkashicRecord(); var kronos = AeonKronos.getInstance();
        var orchestrator = new UnifiedBrainOrchestrator(); orchestrator.wireHolographicMemory();
        var world = new WorldModel(4, new int[]{100, 50, 25, 10});
        var body = new EmbodiedAI(50, 20); body.initialize();
        orchestrator.wirePhase5Components(null, world, body, null, null, null);
        field("akashic", memory); field("kronos", kronos); field("unifiedOrchestrator", orchestrator);
        try {
            Path proofPath = Path.of("specialist-proof.json");
            call("list");
            if (arguments.length > 0 && arguments[0].equals("restore")) {
                var previous = DigitalGenomeArchive.readMap(proofPath);
                require(memory.queryCategory("SPECIALIST_EVIDENCE").size() == ((Number) previous.get("evidence_blocks")).intValue(), "Replay duplicated specialist facts");
                require(kronos.getBindCount() == 0, "Kronos specialist bindings duplicated");
                require(Files.isRegularFile(Path.of(".specialists/native-mind.json")), "Missing mind was not recovered from its actual DNA capsule");
                require(DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(".specialists/native-mind.json"))).equals(previous.get("mind_sha256")), "Recovered mind differs");
                var learned = call("brain " + request("reused-rule", Map.of("operation", "think", "goal", "100 - 37")));
                Map<String, Object> result = (Map<String, Object>) learned.get("result");
                require(((Number) result.get("got")).intValue() == 63, "Learned arithmetic rule was not reused");
                require(!FraymusJSON.stringify(result.get("steps")).contains("\"phase\":\"rewrite\""), "Restored mind rediscovered the same rule");
                require(memory.verifyIntegrity(), "Restored Akashic integrity failed");
                Files.writeString(Path.of("specialist-restart-proof.json"), FraymusJSON.stringify(Map.of("ok", true, "mind_restored_from_dna", true, "learned_subtraction_reused", true, "new_kronos_bindings_before_new_task", 0)));
                System.out.println("PASS: fresh JVM replays specialist evidence without duplicate bindings and restores/reuses the native learned mind");
                return;
            }
            var list = call("list");
            var modelIds=new HashSet<String>();
            for(Object item:(List<?>)list.get("models"))require(modelIds.add(String.valueOf(((Map<?,?>)item).get("id"))),"Duplicate specialist capability");
            require(modelIds.containsAll(Set.of("alphaprotein-novo","botanic1","needle3","jev","minimax-code","fraymus-enigma","odyssey3","fraymus-brain","fraymus-os","fraymus-scene")),"A required specialist capability was not registered");
            var needle = call("needle data/specialists/needle-status-request.json");
            Map<String, Object> needleResult = (Map<String, Object>) needle.get("result");
            require(FraymusJSON.stringify(needleResult.get("function_calls")).contains("system_status"), "Real Needle selected the wrong status tool");
            require(Boolean.FALSE.equals(needle.get("executed")), "Needle silently executed its proposal");
            Map<String, Object> actualStatus = orchestrator.getStatistics();
            var tool = FraynixBoot.class.getDeclaredMethod("executeToolCalls", String.class); tool.setAccessible(true);
            String toolResult = (String) tool.invoke(null, "[SPECIALIST: needle data/specialists/needle-status-request.json]");
            require(toolResult.contains("[SPECIALIST RESULT]") && toolResult.contains("system_status"), "Actual AI bracket tool bypassed the specialist dispatcher");
            var learned = call("brain " + request("learn-rule", Map.of("operation", "think", "goal", "14 - 9")));
            Map<String, Object> learnedResult = (Map<String, Object>) learned.get("result");
            require(((Number) learnedResult.get("got")).intValue() == 5 && FraymusJSON.stringify(learnedResult.get("steps")).contains("\"phase\":\"rewrite\""), "Actual compiler-map repair did not occur");
            String mindSha = DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(".specialists/native-mind.json")));
            String creationContext = fraymus.specialists.SpecialistModelService.contextFor("Needle3 and FrayVM integration");
            require(creationContext.contains("ACTUAL SAVED NATIVE MIND") && creationContext.contains("SUB")
                && creationContext.contains("ledgerWithOutliers"), "The creation context lost learned state or actual native method instructions");
            var absent = call("brain " + request("inherited-fact", Map.of("operation", "think", "goal", "recall constructor")));
            require("FAIL".equals(((Map<?, ?>) absent.get("result")).get("verdict")), "Inherited Object member became a stored fact");
            require(mindSha.equals(DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(".specialists/native-mind.json")))), "Rejected recall changed the mind");
            var vm = call("brain " + request("vm", Map.of("operation", "vm", "source", "PUSH_IMM 6\nPUSH_IMM 7\nMUL\nHALT")));
            require(Boolean.TRUE.equals(((Map<?, ?>) vm.get("result")).get("interpreter_wasm_agree")), "Native VM/WASM mismatch");
            var bytes = new byte[256]; for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) i;
            Files.write(Path.of("all-bytes.bin"), bytes);
            var encoded = call("enigma " + request("encode", Map.of("operation", "encode", "path", Path.of("all-bytes.bin").toAbsolutePath().toString(), "rotors", List.of("integration", "owner"), "png", true)));
            var decoded = call("enigma " + request("decode", Map.of("operation", "decode", "path", encoded.get("output"), "rotors", List.of("integration", "owner"))));
            require(Arrays.equals(bytes, Files.readAllBytes(Path.of((String) decoded.get("output")))), "Enigma/Plasmid full pipeline changed bytes");
            require(!raw("enigma " + request("wrong-rotors", Map.of("operation", "decode", "path", encoded.get("output"), "rotors", List.of("wrong")))).success(), "Wrong rotors were accepted");
            Map<String, Object> robot = new LinkedHashMap<>();
            robot.put("backend", "fraynix-native"); robot.put("frame", "test-fixture"); robot.put("timestamp_ms", 1000);
            robot.put("observation", Collections.nCopies(100, 0.1)); robot.put("units", Collections.nCopies(100, "normalized-feature"));
            robot.put("proprioception", Collections.nCopies(20, 0.0)); robot.put("body_channels", java.util.stream.IntStream.range(0, 50).boxed().toList());
            Map<String, Object> feedback = new LinkedHashMap<>();
            feedback.put("kind", "predicted"); feedback.put("frame", "test-fixture"); feedback.put("timestamp_ms", 1100);
            feedback.put("units", robot.get("units")); feedback.put("observation", Collections.nCopies(100, 0.12));
            feedback.put("proprioception", Collections.nCopies(20, 0.0)); feedback.put("reward", 0.75); robot.put("feedback", feedback);
            require(!raw("robot " + request("invalid-feedback", robot)).success(), "Predictions were accepted as observed feedback");
            require(world.getStatistics().totalPredictions == 0 && body.getStatistics().totalInteractions == 0, "Rejected feedback changed the wired brains");
            feedback.put("kind", "observed");
            var robotic = call("robot " + request("robot", robot));
            require(Boolean.TRUE.equals(robotic.get("feedback_applied")) && Boolean.FALSE.equals(robotic.get("executed")), "Robot protocol confused learning with actuation");
            require(body.getStatistics().totalInteractions == 1 && world.getStatistics().totalPredictions == 1, "Existing body/world instances were bypassed");
            for (var block : memory.queryCategory("SPECIALIST_EVIDENCE")) require(block.content.contains("evidence_id"), "Missing evidence identity");
            require(orchestrator.getHolographicMemory().getStoredKeys().stream().anyMatch(k -> k.startsWith("SPECIALIST:")), "No shared HDC specialist evidence");
            require(memory.verifyIntegrity(), "Akashic integrity failed");
            Files.writeString(proofPath, FraymusJSON.stringify(Map.of("ok", true, "needle", needle, "native_learning", learned,
                "native_vm", vm, "enigma", decoded, "robot", robotic, "actual_status_after_proposal", actualStatus,
                "evidence_blocks", memory.queryCategory("SPECIALIST_EVIDENCE").size(), "mind_sha256", mindSha)));
            Files.delete(Path.of(".specialists/native-mind.json")); // This isolated fixture deliberately tests recovery on the next JVM.
            System.out.println("PASS: real Needle, native rule repair, VM/WASM, strict Enigma/Plasmid, shared memory and existing world/body feedback");
        } finally { kronos.shutdown(); memory.saveAll(); }
    }
}
