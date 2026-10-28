package fraymus.genomics;

import java.util.List;

/** Standalone protocol/context checks; model weights are not loaded. Run from the app root. */
public final class CarbonGenomicsBridgeTest {
    public static void main(String[] args) {
        var parsed = CarbonGenomicsBridge.splitArguments(
            "analyze \"F:\\My sequences\\PAIN.fasta\" --compare-shuffled");
        if (!parsed.equals(List.of("analyze", "F:\\My sequences\\PAIN.fasta", "--compare-shuffled")))
            throw new AssertionError("Quoted Windows path was changed");
        if (CarbonGenomicsBridge.execute("analyze \"unclosed").success())
            throw new AssertionError("Unclosed command quote was accepted");
        for (String query : List.of("Analyze DNA", "Create a FASTA continuation", "Carbon-3B model",
                "genomic likelihood", "apotheosis transcribe PAIN")) {
            if (!CarbonGenomicsBridge.appliesTo(query)) throw new AssertionError("Specialist intent missed: " + query);
        }
        if (!CarbonGenomicsBridge.contextFor("calculate a mortgage").isEmpty())
            throw new AssertionError("Unrelated prompt received genomic instructions");
        String context = CarbonGenomicsBridge.contextFor("analyze DNA and create a new FASTA");
        if (!context.contains("genomics generate") || !context.contains("--compare-shuffled")
                || !context.contains("persist this knowledge in the active app")
                || !context.contains("CarbonGenomicsBridge.execute"))
            throw new AssertionError("Creation prompt is missing the actual tool/skill instructions");
        var status = CarbonGenomicsBridge.execute(List.of("status"));
        if (!status.success() || !status.output().contains("HuggingFaceBio/Carbon-3B"))
            throw new AssertionError("Java-to-Python status protocol failed: " + status.output());
        if (CarbonGenomicsBridge.execute(List.of("unknown-command")).success())
            throw new AssertionError("Worker failure was reported as success");
        System.out.println("CarbonGenomicsBridgeTest passed: quoted paths, intent, full creation context, real worker protocol and failure propagation");
    }
}
