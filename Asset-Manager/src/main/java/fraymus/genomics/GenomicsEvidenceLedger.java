package fraymus.genomics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;

/** Durable source evidence, adaptation receipts, and the genomics component's software genome. */
public final class GenomicsEvidenceLedger {
    private final Path root;
    private final String componentId;

    public GenomicsEvidenceLedger(Path workspace) throws IOException {
        root = workspace.toAbsolutePath().normalize().resolve(".genomics");
        Path identity = root.resolve("component-identity.json");
        if (!Files.exists(identity)) {
            byte[] bytes = DigitalGenomeArchive.jsonBytes(Map.of("schema_version", 1,
                "component_id", UUID.randomUUID().toString(), "component", "FraynixGenomics"));
            Files.createDirectories(root);
            try { Files.write(identity, bytes, StandardOpenOption.CREATE_NEW); }
            catch (FileAlreadyExistsException ignored) { /* Another local runtime created the same identity. */ }
        }
        componentId = (String) DigitalGenomeArchive.readMap(identity).get("component_id");
    }

    public Map<String, Object> profile() throws IOException {
        Path latest = root.resolve("genomes/latest-system-profile.json");
        if (!Files.isRegularFile(latest)) return Map.of("ok", true, "component_id", componentId,
            "observations", 0, "state", "no successful absorption recorded");
        Map<String, Object> receipt = DigitalGenomeArchive.readMap(latest);
        return Map.of("ok", true, "genome", receipt,
            "profile", DigitalGenomeArchive.readPayload(Path.of((String) receipt.get("manifest"))),
            "absorbed_observations", observations());
    }

    public List<Map<String, Object>> observations() throws IOException {
        List<Map<String, Object>> observations = new ArrayList<>();
        for (Path path : events()) {
            Map<String, Object> event = readEvent(path);
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("evidence_id", path.getFileName().toString().replace(".json", ""));
            summary.put("arguments", event.get("arguments"));
            if (event.get("result") instanceof Map<?, ?> result) {
                if (result.containsKey("reference")) summary.put("reference_annotation", result.get("reference"));
                if (result.containsKey("knowledge_sha256")) summary.put("knowledge_sha256", result.get("knowledge_sha256"));
                if (result.containsKey("scores")) summary.put("measured_scores", result.get("scores"));
                Object rawRecords = result.get("records") instanceof List<?> ? result.get("records")
                    : event.get("report") instanceof Map<?, ?> report ? report.get("records") : null;
                if (rawRecords instanceof List<?> records) summary.put("reference_identities", records.stream()
                    .filter(r -> r instanceof Map<?, ?>).map(r -> ((Map<?, ?>) r).get("reference_identity"))
                    .filter(Objects::nonNull).toList());
            }
            observations.add(summary);
        }
        return observations;
    }

    private List<Path> events() throws IOException {
        Path directory = root.resolve("evidence/events");
        if (!Files.isDirectory(directory)) return List.of();
        try (var files = Files.list(directory)) {
            return files.filter(p -> p.toString().endsWith(".json")).sorted().toList();
        }
    }

    private Map<String, Object> readEvent(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        if (!path.getFileName().toString().equals(DigitalGenomeArchive.sha(bytes) + ".json"))
            throw new IOException("Genomics evidence checksum failure");
        Map<String, Object> event = DigitalGenomeArchive.readMap(path);
        if (event.get("artifacts") instanceof List<?> artifacts) for (Object raw : artifacts) {
            if (!(raw instanceof Map<?, ?> artifact)) throw new IOException("Invalid evidence artifact");
            Path blob = DigitalGenomeArchive.resolveStoredPath(Path.of((String) artifact.get("archive")));
            if (!DigitalGenomeArchive.sha(Files.readAllBytes(blob)).equals(artifact.get("sha256")))
                throw new IOException("Genomics evidence artifact checksum failure");
        }
        return event;
    }

    public int replay(BiFunction<String, Map<String, Object>, Map<String, Object>> sink) throws IOException {
        int count = 0;
        for (Path path : events()) {
            Map<String, Object> event = readEvent(path);
            sink.apply(path.getFileName().toString().replace(".json", ""), event);
            count++;
        }
        return count;
    }

    private Map<String, Object> capture(Path path, String expectedHash) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        String key = DigitalGenomeArchive.sha(bytes);
        if (expectedHash != null && !key.equals(expectedHash)) throw new IOException("Source changed before absorption: " + path);
        Path archive = root.resolve("evidence/artifacts/" + key + ".blob");
        if (!Files.exists(archive)) DigitalGenomeArchive.atomicWrite(archive, bytes);
        if (!DigitalGenomeArchive.sha(Files.readAllBytes(archive)).equals(key)) throw new IOException("Artifact archive conflict");
        return Map.of("source", path.toAbsolutePath().normalize().toString(), "archive", archive.toString(), "sha256", key);
    }

    private void captureRecord(Object raw, List<Object> artifacts) throws IOException {
        if (raw instanceof Map<?, ?> record && record.get("path") instanceof String path
                && record.get("file_sha256") instanceof String hash) artifacts.add(capture(Path.of(path), hash));
    }

    public synchronized Map<String, Object> absorb(String arguments, Map<String, Object> result,
            List<Map<String, Object>> brains,
            BiFunction<String, Map<String, Object>, Map<String, Object>> sink) throws IOException {
        List<Object> artifacts = new ArrayList<>();
        Map<String, Object> report = Map.of();
        if (result.get("report") instanceof String filename) {
            Path path = Path.of(filename);
            artifacts.add(capture(path, null)); report = DigitalGenomeArchive.readMap(path);
            if (report.get("records") instanceof List<?> records) for (Object record : records) captureRecord(record, artifacts);
            captureRecord(report.get("primer"), artifacts);
            if (report.get("output") instanceof String output && report.get("output_sha256") instanceof String hash)
                artifacts.add(capture(Path.of(output), hash));
        }
        if (result.get("entry_path") instanceof String entry && result.get("entry_sha256") instanceof String hash)
            artifacts.add(capture(Path.of(entry), hash));
        if (result.get("reference") instanceof Map<?, ?> reference) {
            artifacts.add(capture(Path.of((String) reference.get("fasta_artifact")), (String) reference.get("fasta_sha256")));
            artifacts.add(capture(Path.of((String) reference.get("annotation_artifact")), (String) reference.get("annotation_sha256")));
        }
        for (String prefix : List.of("knowledge", "skill")) {
            if (result.get(prefix + "_source") instanceof String source && result.get(prefix + "_sha256") instanceof String hash)
                artifacts.add(capture(Path.of(source), hash));
        }
        if (report.get("engine") instanceof Map<?, ?> engine && engine.get("model_path") instanceof String model
                && engine.get("installation_manifest_sha256") instanceof String hash)
            artifacts.add(capture(Path.of(model, "installation.json"), hash));
        Map<String, Object> event = Map.of("schema_version", 1, "arguments", arguments,
            "result", result, "report", report, "artifacts", artifacts,
            "adaptation_mode", "validated reference facts, retrieval and tool knowledge; weights unchanged");
        byte[] bytes = DigitalGenomeArchive.jsonBytes(event);
        String id = DigitalGenomeArchive.sha(bytes);
        Path path = root.resolve("evidence/events/" + id + ".json");
        if (!Files.exists(path)) DigitalGenomeArchive.atomicWrite(path, bytes);
        readEvent(path);
        Map<String, Object> destinations = sink.apply(id, event);
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("schema_version", 1); profile.put("component_id", componentId);
        profile.put("component", "FraynixGenomics"); profile.put("software_identity", fraymus.core.FraymusIdentity.VERSION);
        profile.put("registered_brains", brains); profile.put("last_evidence_id", id);
        profile.put("evidence_ids", events().stream().map(p -> p.getFileName().toString().replace(".json", "")).toList());
        profile.put("last_adaptation_destinations", destinations);
        profile.put("weights_updated", false);
        profile.put("coverage", "genomics evidence, component identity and actual registered brain profile; model tensors and full application memory are not embedded");
        Map<String, Object> genome = DigitalGenomeArchive.save(root.resolve("genomes"), "system-profile", profile);
        Map<String, Object> receipt = Map.of("evidence_id", id, "event", path.toString(),
            "captured_artifacts", artifacts.size(), "destinations", destinations, "system_genome", genome, "weights_updated", false);
        DigitalGenomeArchive.atomicWrite(root.resolve("evidence/receipts/" + id + ".json"), DigitalGenomeArchive.jsonBytes(receipt));
        return receipt;
    }
}
