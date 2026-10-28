package fraymus.genomics;

import fraymus.FraynixBoot;
import fraymus.brain.unified.UnifiedBrainOrchestrator;
import fraymus.core.FraymusJSON;
import fraymus.knowledge.AkashicRecord;
import fraymus.neural.AeonApotheosis;
import fraymus.neural.AeonKronos;
import java.nio.file.*;
import java.util.*;

/** Run twice, in separate JVMs, from an isolated app fixture with the real Python worker. */
public final class GenomicsBrainIntegrationTest {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static void field(String name, Object value) throws Exception {
        var field = FraynixBoot.class.getDeclaredField(name);
        field.setAccessible(true); field.set(null, value);
    }
    @SuppressWarnings("unchecked") private static Map<String, Object> command(String arguments) throws Exception {
        var method = FraynixBoot.class.getDeclaredMethod("executeGenomicsCommand", String.class);
        method.setAccessible(true);
        var result = (CarbonGenomicsBridge.Result) method.invoke(null, arguments);
        require(result.success(), "Real command failed: " + result.output());
        return (Map<String, Object>) FraymusJSON.parseStrict(result.output());
    }
    @SuppressWarnings("unchecked") public static void main(String[] args) throws Exception {
        System.setProperty("fraynix.kronos.bootstrap.enabled", "false");
        System.setProperty("fraynix.genomics.bootstrapReferences", "false");
        var memory = new AkashicRecord();
        var kronos = AeonKronos.getInstance();
        var apotheosis = new AeonApotheosis();
        var orchestrator = new UnifiedBrainOrchestrator();
        orchestrator.wireHolographicMemory();
        orchestrator.wireLegacyAeonBrains(null, null, null, null, kronos, null, null, apotheosis, null, null, null);
        field("akashic", memory); field("kronos", kronos); field("unifiedOrchestrator", orchestrator);
        try {
            Path proof = Path.of("integration-proof.json");
            if (args.length > 0 && args[0].equals("restore")) {
                Map<String, Object> previous = DigitalGenomeArchive.readMap(proof);
                var output = command("profile"); // Wires and replays the real evidence ledger.
                Map<String, Object> profile = (Map<String, Object>) output.get("profile");
                require(previous.get("component_id").equals(profile.get("component_id")), "Component identity changed on restart");
                List<String> ids = (List<String>) profile.get("evidence_ids");
                require(ids.equals(previous.get("evidence_ids")), "Restart changed the evidence set");
                require(memory.queryCategory("GENOMICS_ANALYSIS").size() == ((Number) previous.get("analysis_blocks")).intValue(), "Akashic replay duplicated a block");
                require(kronos.getBindCount() == 0, "Restored Kronos bindings were recreated rather than reused");
                for (String id : ids) require(orchestrator.getHolographicMemory().retrieve("GENOMICS:" + id).retrievedValue.contains(id), "Holographic evidence was not rehydrated");
                require(memory.verifyIntegrity(), "Restored Akashic integrity failed");
                String manifest = (String) previous.get("concept_manifest");
                require(apotheosis.restoreTranscription(manifest), "Actual AEON restore rejected a valid capsule");
                require(apotheosis.transcribe("PAIN").equals(previous.get("restored_sequence")), "AEON re-seeded the label instead of preserving the changed vector");
                require(DigitalGenomeArchive.sha(Files.readAllBytes(Path.of(manifest))).equals(previous.get("concept_manifest_sha256")), "Historical concept capsule was overwritten");
                Files.writeString(Path.of("restart-proof.json"), FraymusJSON.stringify(Map.of("ok", true, "component_id", profile.get("component_id"), "evidence_count", ids.size(), "new_kronos_bindings", kronos.getBindCount(), "akashic_integrity", memory.verifyIntegrity(), "exact_concept_vector_restored", true)));
                System.out.println("PASS: fresh JVM restores stable identity, deduplicated Kronos/Akashic evidence, HDC facts and changed concept vector");
                return;
            }
            var unknown = command("identify blind-sample-A.fasta");
            require(FraymusJSON.stringify(unknown).contains("unknown_reference"), "Sequence was named without an absorbed reference");
            var absorbed = command("absorb data/genomics/references/U91966.1/reference.fasta --annotation data/genomics/references/U91966.1/reference.embl --source-url https://www.ebi.ac.uk/ena/browser/api/fasta/U91966.1?download=false");
            require(FraymusJSON.stringify(absorbed).contains("Arabidopsis thaliana"), "Primary reference facts missing");
            var identified = command("identify blind-sample-A.fasta");
            require(FraymusJSON.stringify(identified).contains("exact_reference_match"), "Blinded sequence was not matched");
            require(FraymusJSON.stringify(identified).contains("U91966.1"), "Matched accession missing");
            var analyzed = command("analyze blind-sample-A.fasta --compare-shuffled");
            require(FraymusJSON.stringify(analyzed).contains("exact_reference_match"), "Analysis lost the separate reference evidence");
            Map<String, Object> adaptation = (Map<String, Object>) analyzed.get("adaptation");
            Map<String, Object> destinations = (Map<String, Object>) adaptation.get("destinations");
            require(destinations.keySet().containsAll(List.of("holographic_memory", "kronos", "akashic")), "Existing memory brains were bypassed");
            var output = command("profile");
            Map<String, Object> profile = (Map<String, Object>) output.get("profile");
            List<String> ids = (List<String>) profile.get("evidence_ids");
            require(ids.size() == 4, "Unexpected evidence count: " + ids.size());
            for (String id : ids) require(orchestrator.getHolographicMemory().retrieve("GENOMICS:" + id).retrievedValue.contains(id), "Missing actual HDC evidence");
            require(FraymusJSON.stringify(output.get("live_registered_brains")).contains("Genomics"), "Specialist is not registered");
            Map<String, Object> genome = (Map<String, Object>) output.get("genome");
            command("genome verify \"" + genome.get("manifest") + "\"");
            String initial = apotheosis.transcribe("PAIN");
            Map<String, Object> initialGenome = apotheosis.getLastGenomeArchive();
            long[] vector = DigitalGenomeArchive.conceptVector(DigitalGenomeArchive.readPayload(Path.of((String) initialGenome.get("manifest"))));
            vector[0] ^= 1L;
            char changed = "ACGT".charAt((int) (vector[0] & 3));
            String sequence = changed + initial.substring(1);
            var changedGenome = DigitalGenomeArchive.archiveConcept(Path.of(".genomics/genomes"), "PAIN", vector, sequence);
            require(apotheosis.restoreTranscription((String) changedGenome.get("manifest")), "Actual AEON restore failed");
            require(apotheosis.transcribe("PAIN").equals(sequence), "AEON did not use the restored vector");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("component_id", profile.get("component_id")); result.put("evidence_ids", ids);
            result.put("analysis_blocks", memory.queryCategory("GENOMICS_ANALYSIS").size());
            result.put("concept_manifest", changedGenome.get("manifest")); result.put("restored_sequence", sequence);
            result.put("concept_manifest_sha256", DigitalGenomeArchive.sha(Files.readAllBytes(Path.of((String) changedGenome.get("manifest")))));
            result.put("reference_identification", identified); result.put("real_carbon_analysis", analyzed);
            Files.writeString(proof, FraymusJSON.stringify(result));
            require(memory.verifyIntegrity(), "Actual Akashic integrity failed");
            System.out.println("PASS: real Carbon worker, blind exact reference recognition, three existing memory destinations and exact AEON vector restore");
        } finally { kronos.shutdown(); apotheosis.shutdown(); memory.saveAll(); }
    }
}
