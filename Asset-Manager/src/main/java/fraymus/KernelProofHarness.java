package fraymus;

import fraymus.contract.SystemStateContract;
import fraymus.core.FraymusCore;
import fraymus.neural.AeonAbsolute;
import fraymus.neural.AeonBabel;
import fraymus.os.FrayFS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Executes a safe kernel proof matrix and emits machine-checkable artifacts.
 */
final class KernelProofHarness {

    private static final DateTimeFormatter RUN_STAMP =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT).withZone(ZoneOffset.UTC);
    private static final String REPORT_DIRECTORY = ".northstar-kernel/generated/proofs";
    private static final String SUBSTRATE_DIRECTORY = ".northstar-kernel/generated/substrates";
    private static final List<String> REQUIRED_ENVELOPE_KEYS = List.of(
        "timestamp",
        "boot",
        "memory",
        "ollama",
        "cores",
        "subsystemHealth",
        "provenance"
    );

    private KernelProofHarness() {
        // Static utility.
    }

    static KernelProofReport run(KernelProofContext context) {
        Objects.requireNonNull(context, "context");
        Instant startedAt = Instant.now();
        String runId = RUN_STAMP.format(startedAt);

        List<KernelProofCheckResult> checks = new ArrayList<>();
        checks.add(runCheck("system_state_envelope", "System state envelope contract",
            () -> checkSystemStateEnvelope(context.systemStateSupplier().get())));
        checks.add(runCheck("absolute_swarm", "AEON Absolute swarm ignition",
            () -> checkAbsoluteSwarm(context.absolute())));
        checks.add(runCheck("absolute_recall", "AEON Absolute memory roundtrip",
            () -> checkAbsoluteRecall(context.absolute(), runId)));
        checks.add(runCheck("babel_transmute", "AEON Babel transmutation",
            () -> checkBabelTransmutation(context.babel())));
        checks.add(runCheck("frayfs_roundtrip", "FrayFS read/write roundtrip",
            () -> checkFrayFsRoundtrip(context.fs(), runId)));
        checks.add(runCheck("substrate_manifest", "Model substrate manifest",
            () -> checkSubstrateManifest(context.workspaceRoot())));
        checks.add(runCheck("runtime_inventory", "Runtime substrate inventory",
            () -> checkRuntimeInventory(context.workspaceRoot())));

        int passCount = 0;
        int failCount = 0;
        for (KernelProofCheckResult check : checks) {
            if ("PASS".equals(check.status())) {
                passCount++;
            } else {
                failCount++;
            }
        }

        String overallStatus = failCount == 0 ? "PASS" : "FAIL";
        Instant finishedAt = Instant.now();
        long durationMs = Math.max(0L, finishedAt.toEpochMilli() - startedAt.toEpochMilli());

        Path reportDir = context.workspaceRoot().resolve(REPORT_DIRECTORY).toAbsolutePath().normalize();
        Path jsonPath = reportDir.resolve("kernel-proof-" + runId + ".json");
        Path markdownPath = reportDir.resolve("kernel-proof-" + runId + ".md");
        Path latestJsonPath = reportDir.resolve("kernel-proof-latest.json");
        Path latestMarkdownPath = reportDir.resolve("kernel-proof-latest.md");

        Map<String, Object> artifacts = new LinkedHashMap<>();
        artifacts.put("reportDirectory", unixPath(reportDir));
        artifacts.put("jsonReportPath", unixPath(jsonPath));
        artifacts.put("markdownReportPath", unixPath(markdownPath));
        artifacts.put("latestJsonPath", unixPath(latestJsonPath));
        artifacts.put("latestMarkdownPath", unixPath(latestMarkdownPath));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("status", overallStatus);
        summary.put("passCount", passCount);
        summary.put("failCount", failCount);
        summary.put("durationMs", durationMs);
        summary.put("matrix", List.of(
            "SYSTEM_STATE envelope contract",
            "AEON Absolute ignition + memory roundtrip",
            "AEON Babel transmutation",
            "FrayFS read/write roundtrip",
            "Model + runtime substrate artifact checks"
        ));

        KernelProofReport report = new KernelProofReport(
            runId,
            startedAt.toString(),
            finishedAt.toString(),
            context.version(),
            overallStatus,
            passCount,
            failCount,
            durationMs,
            checks,
            artifacts,
            summary
        );

        writeReportFiles(reportDir, jsonPath, markdownPath, latestJsonPath, latestMarkdownPath, report);
        return report;
    }

    static ManifestSummary inspectModelManifest(Path manifestPath) {
        if (manifestPath == null || !Files.exists(manifestPath)) {
            return new ManifestSummary(false, 0, Map.of(), Map.of());
        }

        int modelCount = 0;
        Map<String, Integer> familyCounts = new LinkedHashMap<>();
        Map<String, Integer> provenanceCounts = new LinkedHashMap<>();

        try {
            List<String> lines = Files.readAllLines(manifestPath, StandardCharsets.UTF_8);
            boolean headerSkipped = false;
            for (String raw : lines) {
                String line = raw == null ? "" : raw.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] columns = line.split("\\t", -1);
                if (columns.length < 11) {
                    continue;
                }
                modelCount++;
                incrementCount(familyCounts, columns[1].isBlank() ? "unknown" : columns[1]);
                incrementCount(provenanceCounts, columns[3].isBlank() ? "unknown" : columns[3]);
            }
        } catch (Exception e) {
            return new ManifestSummary(false, 0, Map.of(), Map.of());
        }

        return new ManifestSummary(true, modelCount, familyCounts, provenanceCounts);
    }

    static RuntimeInventorySummary inspectRuntimeInventory(Path runtimeInventoryPath) {
        if (runtimeInventoryPath == null || !Files.exists(runtimeInventoryPath)) {
            return new RuntimeInventorySummary(false, 0, 0, Map.of());
        }

        int totalEntries = 0;
        int availableEntries = 0;
        Map<String, Integer> backendCounts = new LinkedHashMap<>();

        try {
            List<String> lines = Files.readAllLines(runtimeInventoryPath, StandardCharsets.UTF_8);
            for (String raw : lines) {
                String line = raw == null ? "" : raw.trim();
                if (!line.startsWith("- `")) {
                    continue;
                }
                totalEntries++;
                if (line.contains("available=`true`")) {
                    availableEntries++;
                }
                String backend = between(line, "backend=`", "`");
                if (!backend.isBlank()) {
                    incrementCount(backendCounts, backend);
                }
            }
        } catch (Exception e) {
            return new RuntimeInventorySummary(false, 0, 0, Map.of());
        }

        return new RuntimeInventorySummary(true, totalEntries, availableEntries, backendCounts);
    }

    private static KernelProofCheckResult runCheck(String id, String label, Supplier<CheckOutcome> check) {
        long start = System.nanoTime();
        try {
            CheckOutcome outcome = check.get();
            long durationMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);
            return new KernelProofCheckResult(
                id,
                label,
                outcome.pass() ? "PASS" : "FAIL",
                outcome.score(),
                outcome.detail(),
                durationMs,
                outcome.data()
            );
        } catch (Exception e) {
            long durationMs = Math.max(0L, (System.nanoTime() - start) / 1_000_000L);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("exception", e.getClass().getName());
            data.put("message", e.getMessage());
            return new KernelProofCheckResult(
                id,
                label,
                "FAIL",
                0.0,
                "Check failed with exception: " + e.getMessage(),
                durationMs,
                data
            );
        }
    }

    private static CheckOutcome checkSystemStateEnvelope(Map<String, Object> envelope) {
        if (envelope == null) {
            return CheckOutcome.fail("System state envelope is null.");
        }
        List<String> missing = new ArrayList<>();
        for (String key : REQUIRED_ENVELOPE_KEYS) {
            if (!envelope.containsKey(key)) {
                missing.add(key);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("requiredKeys", REQUIRED_ENVELOPE_KEYS);
        data.put("presentKeys", new ArrayList<>(envelope.keySet()));
        data.put("renderedEnvelope", SystemStateContract.renderEnvelope(envelope));
        if (!missing.isEmpty()) {
            data.put("missingKeys", missing);
            return CheckOutcome.fail("Missing envelope keys: " + String.join(", ", missing), data);
        }

        return CheckOutcome.pass("Envelope has all required contract keys.", data);
    }

    private static CheckOutcome checkAbsoluteSwarm(AeonAbsolute absolute) {
        if (absolute == null) {
            return CheckOutcome.fail("AEON Absolute is not initialized.");
        }

        int igniteResult = absolute.igniteSwarm();
        int activeCores = absolute.getActiveCores();
        boolean pass = absolute.isSwarmActive() && activeCores > 0;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("igniteResult", igniteResult);
        data.put("activeCores", activeCores);
        data.put("directive", absolute.getDirective());
        data.put("embeddedMode", absolute.isEmbeddedMode());
        data.put("swarmActive", absolute.isSwarmActive());

        if (!pass) {
            return CheckOutcome.fail("Absolute swarm did not become active.", data);
        }
        return CheckOutcome.pass("Absolute swarm active with " + activeCores + " core(s).", data);
    }

    private static CheckOutcome checkAbsoluteRecall(AeonAbsolute absolute, String runId) {
        if (absolute == null) {
            return CheckOutcome.fail("AEON Absolute is not initialized.");
        }

        String probe = "kernel-proof-" + runId;
        absolute.holoLearn(probe);
        double similarity = absolute.holoRecall(probe);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("probe", probe);
        data.put("similarity", similarity);
        data.put("threshold", 0.20);

        if (Double.isNaN(similarity) || similarity < 0.20) {
            return CheckOutcome.fail("Absolute recall similarity below threshold: " + similarity, data);
        }
        return CheckOutcome.pass("Absolute recall similarity " + similarity + " meets threshold.", data);
    }

    private static CheckOutcome checkBabelTransmutation(AeonBabel babel) {
        if (babel == null) {
            return CheckOutcome.fail("AEON Babel is not initialized.");
        }

        String code = babel.transmute("kernel proof matrix", "PYTHON");
        int lineCount = countNonBlankLines(code);
        boolean pass = code != null && !code.isBlank() && lineCount >= 8;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("lineCount", lineCount);
        data.put("targetLang", babel.getTargetLang());
        data.put("transmutationCount", babel.getTransmutationCount());
        data.put("pythonGenerations", babel.getPythonGenerations());

        if (!pass) {
            return CheckOutcome.fail("Babel did not return a substantial transmutation.", data);
        }
        return CheckOutcome.pass("Babel produced " + lineCount + " non-blank line(s).", data);
    }

    private static CheckOutcome checkFrayFsRoundtrip(FrayFS fs, String runId) {
        if (fs == null) {
            return CheckOutcome.fail("FrayFS is not initialized.");
        }
        String path = "proofs/kernel-proof-" + runId + ".txt";
        String payload = "kernel-proof:" + runId + ":" + Instant.now();
        fs.write(path, payload);
        String readback = fs.readString(path);
        boolean pass = payload.equals(readback);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("path", path);
        data.put("payloadLength", payload.length());
        data.put("readbackLength", readback == null ? 0 : readback.length());

        if (!pass) {
            return CheckOutcome.fail("FrayFS roundtrip mismatch.", data);
        }
        return CheckOutcome.pass("FrayFS roundtrip succeeded for " + path + ".", data);
    }

    private static CheckOutcome checkSubstrateManifest(Path workspaceRoot) {
        Path manifestPath = resolveSubstrateArtifact(workspaceRoot, "MODEL_SUBSTRATE_MANIFEST.tsv");
        ManifestSummary summary = inspectModelManifest(manifestPath);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("manifestPath", unixPath(manifestPath));
        data.put("exists", summary.exists());
        data.put("modelCount", summary.modelCount());
        data.put("familyCounts", summary.familyCounts());
        data.put("provenanceCounts", summary.provenanceCounts());

        if (!summary.exists()) {
            return CheckOutcome.fail("Model substrate manifest is missing.", data);
        }
        if (summary.modelCount() <= 0) {
            return CheckOutcome.fail("Model substrate manifest has no model rows.", data);
        }
        return CheckOutcome.pass("Model substrate manifest contains " + summary.modelCount() + " model row(s).", data);
    }

    private static CheckOutcome checkRuntimeInventory(Path workspaceRoot) {
        Path inventoryPath = resolveSubstrateArtifact(workspaceRoot, "LOCAL_MODEL_RUNTIME_INVENTORY.md");
        RuntimeInventorySummary summary = inspectRuntimeInventory(inventoryPath);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("inventoryPath", unixPath(inventoryPath));
        data.put("exists", summary.exists());
        data.put("totalEntries", summary.totalEntries());
        data.put("availableEntries", summary.availableEntries());
        data.put("backendCounts", summary.backendCounts());

        if (!summary.exists()) {
            return CheckOutcome.fail("Runtime inventory is missing.", data);
        }
        if (summary.totalEntries() <= 0) {
            return CheckOutcome.fail("Runtime inventory has no runtime entries.", data);
        }
        if (summary.availableEntries() <= 0) {
            return CheckOutcome.fail("Runtime inventory has no available runtime entries.", data);
        }
        return CheckOutcome.pass(
            "Runtime inventory lists " + summary.totalEntries() + " entries (" + summary.availableEntries() + " available).",
            data
        );
    }

    private static void writeReportFiles(
        Path reportDir,
        Path jsonPath,
        Path markdownPath,
        Path latestJsonPath,
        Path latestMarkdownPath,
        KernelProofReport report
    ) {
        try {
            Files.createDirectories(reportDir);
            Files.writeString(jsonPath, FraymusCore.toJSONPretty(report.toMap()), StandardCharsets.UTF_8);
            Files.writeString(markdownPath, report.toMarkdown(), StandardCharsets.UTF_8);
            Files.copy(jsonPath, latestJsonPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(markdownPath, latestMarkdownPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            // Keep command non-fatal; report object still returned with in-memory details.
            System.err.println("[PROVE] Could not write proof artifacts: " + ioe.getMessage());
        }
    }

    private static int countNonBlankLines(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        int count = 0;
        String[] lines = content.split("\\R");
        for (String line : lines) {
            if (!line.isBlank()) {
                count++;
            }
        }
        return count;
    }

    private static String between(String text, String left, String right) {
        int leftIndex = text.indexOf(left);
        if (leftIndex < 0) return "";
        int start = leftIndex + left.length();
        int rightIndex = text.indexOf(right, start);
        if (rightIndex < 0) return "";
        return text.substring(start, rightIndex).trim();
    }

    private static void incrementCount(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    private static Path resolveSubstrateArtifact(Path workspaceRoot, String fileName) {
        Path cursor = workspaceRoot == null ? null : workspaceRoot.toAbsolutePath().normalize();
        int guard = 0;
        while (cursor != null && guard < 8) {
            Path candidate = cursor.resolve(SUBSTRATE_DIRECTORY).resolve(fileName).toAbsolutePath().normalize();
            if (Files.exists(candidate)) {
                return candidate;
            }
            cursor = cursor.getParent();
            guard++;
        }
        return workspaceRoot.resolve(SUBSTRATE_DIRECTORY).resolve(fileName).toAbsolutePath().normalize();
    }

    private static String unixPath(Path path) {
        return path.toString().replace('\\', '/');
    }

    static final class KernelProofContext {
        private final Path workspaceRoot;
        private final Supplier<Map<String, Object>> systemStateSupplier;
        private final AeonAbsolute absolute;
        private final AeonBabel babel;
        private final FrayFS fs;
        private final String version;

        KernelProofContext(
            Path workspaceRoot,
            Supplier<Map<String, Object>> systemStateSupplier,
            AeonAbsolute absolute,
            AeonBabel babel,
            FrayFS fs,
            String version
        ) {
            this.workspaceRoot = workspaceRoot.toAbsolutePath().normalize();
            this.systemStateSupplier = systemStateSupplier;
            this.absolute = absolute;
            this.babel = babel;
            this.fs = fs;
            this.version = version == null ? "unknown" : version;
        }

        Path workspaceRoot() {
            return workspaceRoot;
        }

        Supplier<Map<String, Object>> systemStateSupplier() {
            return systemStateSupplier;
        }

        AeonAbsolute absolute() {
            return absolute;
        }

        AeonBabel babel() {
            return babel;
        }

        FrayFS fs() {
            return fs;
        }

        String version() {
            return version;
        }
    }

    static final class KernelProofReport {
        private final String runId;
        private final String startedAt;
        private final String finishedAt;
        private final String version;
        private final String status;
        private final int passCount;
        private final int failCount;
        private final long durationMs;
        private final List<KernelProofCheckResult> checks;
        private final Map<String, Object> artifacts;
        private final Map<String, Object> summary;

        KernelProofReport(
            String runId,
            String startedAt,
            String finishedAt,
            String version,
            String status,
            int passCount,
            int failCount,
            long durationMs,
            List<KernelProofCheckResult> checks,
            Map<String, Object> artifacts,
            Map<String, Object> summary
        ) {
            this.runId = runId;
            this.startedAt = startedAt;
            this.finishedAt = finishedAt;
            this.version = version;
            this.status = status;
            this.passCount = passCount;
            this.failCount = failCount;
            this.durationMs = durationMs;
            this.checks = List.copyOf(checks);
            this.artifacts = Map.copyOf(artifacts);
            this.summary = Map.copyOf(summary);
        }

        String runId() {
            return runId;
        }

        String status() {
            return status;
        }

        int passCount() {
            return passCount;
        }

        int failCount() {
            return failCount;
        }

        List<KernelProofCheckResult> checks() {
            return checks;
        }

        Map<String, Object> artifacts() {
            return artifacts;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("runId", runId);
            map.put("status", status);
            map.put("startedAt", startedAt);
            map.put("finishedAt", finishedAt);
            map.put("durationMs", durationMs);
            map.put("version", version);
            map.put("passCount", passCount);
            map.put("failCount", failCount);
            List<Map<String, Object>> checkMaps = new ArrayList<>();
            for (KernelProofCheckResult check : checks) {
                checkMaps.add(check.toMap());
            }
            map.put("checks", checkMaps);
            map.put("artifacts", artifacts);
            map.put("summary", summary);
            return map;
        }

        String toMarkdown() {
            StringBuilder sb = new StringBuilder();
            sb.append("# Kernel Proof Report\n\n");
            sb.append("- runId: ").append(runId).append('\n');
            sb.append("- status: ").append(status).append('\n');
            sb.append("- version: ").append(version).append('\n');
            sb.append("- startedAt: ").append(startedAt).append('\n');
            sb.append("- finishedAt: ").append(finishedAt).append('\n');
            sb.append("- durationMs: ").append(durationMs).append('\n');
            sb.append("- passCount: ").append(passCount).append('\n');
            sb.append("- failCount: ").append(failCount).append('\n');
            sb.append('\n');
            sb.append("## Check Matrix\n\n");
            sb.append("| Check | Status | Score | DurationMs | Detail |\n");
            sb.append("|---|---|---:|---:|---|\n");
            for (KernelProofCheckResult check : checks) {
                sb.append("| ").append(check.id())
                    .append(" | ").append(check.status())
                    .append(" | ").append(String.format(Locale.ROOT, "%.2f", check.score()))
                    .append(" | ").append(check.durationMs())
                    .append(" | ").append(check.detail().replace("|", "/"))
                    .append(" |\n");
            }
            sb.append('\n');
            sb.append("## Artifacts\n\n");
            for (Map.Entry<String, Object> entry : artifacts.entrySet()) {
                sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
            }
            return sb.toString();
        }

        String renderConsoleSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("[PROVE] Kernel proof ").append(status).append(" | runId=").append(runId)
                .append(" | pass=").append(passCount).append(" fail=").append(failCount)
                .append(" | durationMs=").append(durationMs).append('\n');
            for (KernelProofCheckResult check : checks) {
                sb.append("  - ").append(check.id()).append(": ").append(check.status())
                    .append(" (").append(check.durationMs()).append("ms)")
                    .append(" -> ").append(check.detail()).append('\n');
            }
            sb.append("  - json: ").append(artifacts.getOrDefault("jsonReportPath", "")).append('\n');
            sb.append("  - markdown: ").append(artifacts.getOrDefault("markdownReportPath", ""));
            return sb.toString();
        }
    }

    static final class KernelProofCheckResult {
        private final String id;
        private final String label;
        private final String status;
        private final double score;
        private final String detail;
        private final long durationMs;
        private final Map<String, Object> data;

        KernelProofCheckResult(
            String id,
            String label,
            String status,
            double score,
            String detail,
            long durationMs,
            Map<String, Object> data
        ) {
            this.id = id;
            this.label = label;
            this.status = status;
            this.score = score;
            this.detail = detail;
            this.durationMs = durationMs;
            this.data = data == null ? Map.of() : Map.copyOf(data);
        }

        String id() {
            return id;
        }

        String status() {
            return status;
        }

        double score() {
            return score;
        }

        String detail() {
            return detail;
        }

        long durationMs() {
            return durationMs;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", id);
            map.put("label", label);
            map.put("status", status);
            map.put("score", score);
            map.put("durationMs", durationMs);
            map.put("detail", detail);
            map.put("data", data);
            return map;
        }
    }

    static final class ManifestSummary {
        private final boolean exists;
        private final int modelCount;
        private final Map<String, Integer> familyCounts;
        private final Map<String, Integer> provenanceCounts;

        ManifestSummary(
            boolean exists,
            int modelCount,
            Map<String, Integer> familyCounts,
            Map<String, Integer> provenanceCounts
        ) {
            this.exists = exists;
            this.modelCount = modelCount;
            this.familyCounts = Map.copyOf(familyCounts);
            this.provenanceCounts = Map.copyOf(provenanceCounts);
        }

        boolean exists() {
            return exists;
        }

        int modelCount() {
            return modelCount;
        }

        Map<String, Integer> familyCounts() {
            return familyCounts;
        }

        Map<String, Integer> provenanceCounts() {
            return provenanceCounts;
        }
    }

    static final class RuntimeInventorySummary {
        private final boolean exists;
        private final int totalEntries;
        private final int availableEntries;
        private final Map<String, Integer> backendCounts;

        RuntimeInventorySummary(boolean exists, int totalEntries, int availableEntries, Map<String, Integer> backendCounts) {
            this.exists = exists;
            this.totalEntries = totalEntries;
            this.availableEntries = availableEntries;
            this.backendCounts = Map.copyOf(backendCounts);
        }

        boolean exists() {
            return exists;
        }

        int totalEntries() {
            return totalEntries;
        }

        int availableEntries() {
            return availableEntries;
        }

        Map<String, Integer> backendCounts() {
            return backendCounts;
        }
    }

    private static final class CheckOutcome {
        private final boolean pass;
        private final double score;
        private final String detail;
        private final Map<String, Object> data;

        private CheckOutcome(boolean pass, double score, String detail, Map<String, Object> data) {
            this.pass = pass;
            this.score = score;
            this.detail = detail;
            this.data = data == null ? Map.of() : data;
        }

        static CheckOutcome pass(String detail) {
            return new CheckOutcome(true, 1.0, detail, new LinkedHashMap<>());
        }

        static CheckOutcome pass(String detail, Map<String, Object> data) {
            return new CheckOutcome(true, 1.0, detail, data);
        }

        static CheckOutcome fail(String detail) {
            return new CheckOutcome(false, 0.0, detail, new LinkedHashMap<>());
        }

        static CheckOutcome fail(String detail, Map<String, Object> data) {
            return new CheckOutcome(false, 0.0, detail, data);
        }

        boolean pass() {
            return pass;
        }

        double score() {
            return score;
        }

        String detail() {
            return detail;
        }

        Map<String, Object> data() {
            return data;
        }
    }
}
