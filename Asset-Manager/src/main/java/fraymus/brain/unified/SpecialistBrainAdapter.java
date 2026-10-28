package fraymus.brain.unified;

import fraymus.core.FraymusJSON;
import fraymus.genomics.DigitalGenomeArchive;
import fraymus.specialists.SpecialistModelService;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

/** Routes explicit specialist contracts and persists evidence through the existing shared brains. */
public final class SpecialistBrainAdapter extends AbstractBrainAdapter {
    public static final String NAME = "ModelSpecialists";
    private final Function<String, Map<String, Object>> execute;
    private final Supplier<List<Map<String, Object>>> profiles;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> sink;
    private String restoreError = "";

    public SpecialistBrainAdapter(Function<String, Map<String, Object>> execute, Supplier<List<Map<String, Object>>> profiles,
            BiFunction<String, Map<String, Object>, Map<String, Object>> sink) {
        super(NAME, "typed_model_specialists", List.of());
        this.execute = execute; this.profiles = profiles; this.sink = sink;
    }
    private Path root() { return Path.of(System.getProperty("user.dir"), ".specialists"); }
    public static boolean accepts(BrainQuery query) {
        return query.getContext() != null && query.getContext().get("specialist.arguments") instanceof String
            || query.getQuery() != null && query.getQuery().matches("(?s)^specialist(\\s.*)?$");
    }
    @Override protected void onWire() {
        Path events = root().resolve("events");
        try {
            Path latest = root().resolve("genomes/latest-native-mind.json");
            if (!Files.exists(root().resolve("native-mind.json")) && Files.isRegularFile(latest)) {
                var receipt = DigitalGenomeArchive.readMap(latest);
                var payload = DigitalGenomeArchive.readPayload(Path.of((String) receipt.get("manifest")));
                byte[] bytes = ((String) payload.get("state_json")).getBytes(java.nio.charset.StandardCharsets.UTF_8);
                if (!DigitalGenomeArchive.sha(bytes).equals(payload.get("state_sha256"))) throw new java.io.IOException("Native mind capsule hash mismatch");
                DigitalGenomeArchive.atomicWrite(root().resolve("native-mind.json"), bytes);
            }
        } catch (Exception error) { restoreError = error.getMessage(); updateHealth(0); return; }
        if (!Files.isDirectory(events)) return;
        try (var files = Files.list(events)) {
            for (Path path : files.filter(p -> p.toString().endsWith(".json")).sorted().toList()) {
                if (!path.getFileName().toString().equals(DigitalGenomeArchive.sha(Files.readAllBytes(path)) + ".json"))
                    throw new java.io.IOException("Specialist evidence checksum mismatch");
                Map<String, Object> event = DigitalGenomeArchive.readMap(path);
                validateArtifacts(event);
                sink.apply(path.getFileName().toString().replace(".json", ""), event);
            }
        } catch (Exception error) { restoreError = error.getMessage(); updateHealth(0); }
    }

    public synchronized void bootstrapCapabilities() {
        try {
            Path catalog = Path.of(System.getProperty("user.dir"), "data/specialists/catalog.json");
            if (!Files.isRegularFile(catalog) || !restoreError.isEmpty()) return;
            String sha = DigitalGenomeArchive.sha(Files.readAllBytes(catalog));
            Path marker = root().resolve("curated/" + sha + ".json");
            if (Files.isRegularFile(marker)) return;
            BrainResponse result = process(new BrainQuery("specialist learn", List.of(), Map.of("specialist.arguments", "learn"), 1));
            if (!result.isSuccess()) throw new java.io.IOException(result.getResponse());
            DigitalGenomeArchive.atomicWrite(marker, DigitalGenomeArchive.jsonBytes(Map.of("catalog_sha256", sha)));
        } catch (Exception error) { restoreError = error.getMessage(); updateHealth(0); }
    }

    private Map<String, Object> capture(String path, Object expected) throws java.io.IOException {
        byte[] bytes = Files.readAllBytes(DigitalGenomeArchive.resolveStoredPath(Path.of(path)));
        String sha = DigitalGenomeArchive.sha(bytes);
        if (expected instanceof String digest && !sha.equals(digest)) throw new java.io.IOException("Specialist source changed before absorption");
        Path archive = root().resolve("artifacts/" + sha + ".blob");
        if (!Files.exists(archive)) DigitalGenomeArchive.atomicWrite(archive, bytes);
        if (!DigitalGenomeArchive.sha(Files.readAllBytes(archive)).equals(sha)) throw new java.io.IOException("Specialist artifact archive mismatch");
        return Map.of("source", path, "archive", archive.toString(), "sha256", sha);
    }

    private void validateArtifacts(Map<String, Object> event) throws java.io.IOException {
        if (event.get("artifacts") instanceof List<?> artifacts) for (Object raw : artifacts) {
            if (!(raw instanceof Map<?, ?> artifact) || !DigitalGenomeArchive.sha(Files.readAllBytes(DigitalGenomeArchive.resolveStoredPath(Path.of((String) artifact.get("archive"))))).equals(artifact.get("sha256")))
                throw new java.io.IOException("Specialist evidence artifact checksum mismatch");
        }
    }
    @Override public synchronized BrainResponse process(BrainQuery query) {
        long started = System.currentTimeMillis();
        if (!restoreError.isEmpty()) return BrainResponse.error(NAME, restoreError);
        try {
            String arguments = query.getContext() != null && query.getContext().get("specialist.arguments") instanceof String value ? value
                : query.getQuery().replaceFirst("^specialist\\s*", "");
            Map<String, Object> result = execute.apply(arguments);
            if (arguments.startsWith("needle ") || arguments.startsWith("robot ") || arguments.startsWith("chain ") || arguments.startsWith("enigma ") || arguments.startsWith("brain ") || arguments.startsWith("minimax-code ") || arguments.matches("^(odyssey|odyssey3|odessey-3)\\s.*") || arguments.equals("learn")) {
                List<Object> artifacts = new ArrayList<>();
                for (String key : List.of("report", "request", "output", "brain_state_manifest", "input_snapshot")) if (result.get(key) instanceof String path)
                    artifacts.add(capture(path, result.get(key + "_sha256")));
                if (result.get("provider_result") instanceof Map<?, ?> provider && provider.get("artifact") instanceof String path)
                    artifacts.add(capture(path, provider.get("sha256")));
                if (result.get("result") instanceof Map<?, ?> nested) {
                    if (nested.get("output") instanceof String path) artifacts.add(capture(path, nested.get("output_sha256")));
                    if (nested.get("state_path") instanceof String path && Files.isRegularFile(Path.of(path))) {
                        artifacts.add(capture(path, nested.get("state_sha256")));
                        DigitalGenomeArchive.save(root().resolve("genomes"), "native-mind", Map.of("state_json", Files.readString(Path.of(path)),
                            "state_sha256", nested.get("state_sha256"), "format", "fraymus-owner-brain-json"));
                    }
                }
                if (arguments.equals("learn")) artifacts.add(capture(Path.of(System.getProperty("user.dir"), "data/specialists/catalog.json").toString(), null));
                Map<String, Object> event = Map.of("arguments", arguments, "result", result, "artifacts", artifacts,
                    "weight_updates_performed", Boolean.TRUE.equals(result.get("feedback_applied")),
                    "foundation_model_weight_updates_performed", false, "registered_brains", profiles.get());
                byte[] bytes = DigitalGenomeArchive.jsonBytes(event);
                String id = DigitalGenomeArchive.sha(bytes);
                Path path = root().resolve("events/" + id + ".json");
                if (!Files.exists(path)) DigitalGenomeArchive.atomicWrite(path, bytes);
                validateArtifacts(event);
                Map<String, Object> destinations = sink.apply(id, event);
                Map<String, Object> genome = DigitalGenomeArchive.save(root().resolve("genomes"), "specialist-profile",
                    Map.of("registered_brains", profiles.get(), "last_evidence_id", id, "last_result", result,
                        "coverage", "specialist tool evidence and brain profile; model weights are external"));
                result = new LinkedHashMap<>(result);
                result.put("adaptation", Map.of("evidence_id", id, "event", path.toString(), "destinations", destinations, "digital_genome", genome));
            }
            recordSuccess(System.currentTimeMillis() - started, 1.0, 0.0);
            return BrainResponse.success(NAME, FraymusJSON.stringify(result), 1, 0,
                Map.of("route", "SPECIALIST", "confidenceScope", "validated protocol only"), System.currentTimeMillis() - started);
        } catch (Exception error) { recordFailure(); return BrainResponse.error(NAME, "Specialist failed: " + error.getMessage()); }
    }
}
