package fraymus.brain.unified;

import fraymus.core.FraymusJSON;
import fraymus.genomics.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import java.util.function.*;

/** Typed genomic specialist that shares verified observations with the existing memory brains. */
public final class GenomicsBrainAdapter extends AbstractBrainAdapter {
    public static final String NAME = "Genomics";
    private final Supplier<List<Map<String, Object>>> brainProfiles;
    private final BiFunction<String, Map<String, Object>, Map<String, Object>> sink;
    private GenomicsEvidenceLedger ledger;
    private String restoreError = "";

    public GenomicsBrainAdapter(Supplier<List<Map<String, Object>>> brainProfiles,
            BiFunction<String, Map<String, Object>, Map<String, Object>> sink) {
        super(NAME, "genomic_sequence_analysis", List.of(BrainCapability.DATA));
        this.brainProfiles = brainProfiles; this.sink = sink;
    }

    @Override protected void onWire() {
        try {
            ledger = new GenomicsEvidenceLedger(Path.of(System.getProperty("user.dir")));
            ledger.replay(sink);
        } catch (Exception error) { restoreError = error.getMessage(); updateHealth(0.0); }
    }

    public static boolean accepts(BrainQuery query) {
        if (query.getContext() != null && query.getContext().get("genomics.arguments") instanceof String) return true;
        return query.getQuery() != null && query.getQuery().matches("(?s)^(genomics|carbon)(\\s.*)?$");
    }

    public synchronized void bootstrapLocalReferences() {
        if (!Boolean.parseBoolean(System.getProperty("fraynix.genomics.bootstrapReferences", "true"))) return;
        Path workspace = Path.of(System.getProperty("user.dir"));
        Path directory = workspace.resolve("data/genomics/references");
        if (!Files.isDirectory(directory)) return;
        try (var folders = Files.list(directory)) {
            for (Path folder : folders.filter(Files::isDirectory).sorted().toList()) {
                Path fasta = folder.resolve("reference.fasta"), annotation = folder.resolve("reference.embl");
                Path provenance = folder.resolve("download-provenance.json");
                if (!Files.isRegularFile(fasta) || !Files.isRegularFile(annotation) || !Files.isRegularFile(provenance)) continue;
                Object parsed = FraymusJSON.parseStrict(Files.readString(provenance));
                if (!(parsed instanceof List<?> sources) || sources.isEmpty() || !(sources.get(0) instanceof Map<?, ?> source)) continue;
                String url = String.valueOf(source.get("source_url"));
                // Only the explicitly curated, locally downloaded package is bootstrapped. No network fetch occurs here.
                String key = DigitalGenomeArchive.sha((DigitalGenomeArchive.sha(Files.readAllBytes(fasta))
                    + DigitalGenomeArchive.sha(Files.readAllBytes(annotation)) + url).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                Path marker = workspace.resolve(".genomics/curated/" + key + ".json");
                if (Files.isRegularFile(marker)) {
                    Map<String, Object> prior = DigitalGenomeArchive.readMap(marker);
                    if (prior.get("evidence_id") instanceof String id
                            && Files.isRegularFile(workspace.resolve(".genomics/evidence/events/" + id + ".json"))) continue;
                }
                String arguments = "absorb \"" + fasta + "\" --annotation \"" + annotation + "\" --source-url \"" + url + "\"";
                BrainResponse response = process(new BrainQuery("genomics " + arguments, List.of(BrainCapability.DATA),
                    Map.of("genomics.arguments", arguments), 1.0));
                if (!response.isSuccess()) throw new IllegalStateException(response.getResponse());
                Object output = FraymusJSON.parseStrict(response.getResponse());
                if (output instanceof Map<?, ?> result && result.get("adaptation") instanceof Map<?, ?> adaptation)
                    DigitalGenomeArchive.atomicWrite(marker, DigitalGenomeArchive.jsonBytes(Map.of("evidence_id", adaptation.get("evidence_id"))));
            }
        } catch (Exception error) { restoreError = error.getMessage(); updateHealth(0.0); }
    }

    @Override @SuppressWarnings("unchecked") public synchronized BrainResponse process(BrainQuery query) {
        long started = System.currentTimeMillis();
        if (!wired || ledger == null || !restoreError.isEmpty()) return BrainResponse.error(NAME, "Genomics restore unavailable: " + restoreError);
        try {
            Object supplied = query.getContext() == null ? null : query.getContext().get("genomics.arguments");
            String arguments = supplied instanceof String value ? value
                : query.getQuery().replaceFirst("^(genomics|carbon)\\s*", "");
            String operation = arguments.isBlank() ? "status" : arguments.trim().split("\\s+", 2)[0];
            Map<String, Object> output;
            if (operation.equals("profile")) {
                output = new LinkedHashMap<>(ledger.profile());
                output.put("live_registered_brains", brainProfiles.get());
            }
            else {
                var result = CarbonGenomicsBridge.execute(arguments);
                if (!result.success()) { recordFailure(); return BrainResponse.error(NAME, result.output()); }
                output = new LinkedHashMap<>((Map<String, Object>) FraymusJSON.parseStrict(result.output()));
                if (!operation.equals("status") && !operation.equals("genome"))
                    output.put("adaptation", ledger.absorb(arguments, new LinkedHashMap<>(output), brainProfiles.get(), sink));
            }
            long elapsed = System.currentTimeMillis() - started;
            recordSuccess(elapsed, 1.0, 0.0);
            return BrainResponse.success(NAME, FraymusJSON.stringify(output), 1.0, 0.0,
                Map.of("queryRoute", "GENOMICS", "selectedBrain", NAME,
                       "confidenceScope", "validated process/evidence handling; not species probability"), elapsed);
        } catch (Exception error) { recordFailure(); return BrainResponse.error(NAME, "Genomics evidence handling failed: " + error.getMessage()); }
    }
}
