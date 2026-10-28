package fraymus.identity;

import fraymus.core.FraymusJSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DeepfakeScanService {
    private static final Path SCRIPT = Paths.get("scripts", "avatar", "deepfake_scan.py");
    private static final int DEFAULT_TIMEOUT_SECONDS = 12;
    private static final long PROCESS_TIMEOUT_GRACE_SECONDS = 3;

    private DeepfakeScanService() {}

    public record ScannedImage(
        String source,
        String decision,
        double risk,
        double confidence,
        int faces,
        double depthConsistency,
        double reflectionConsistency,
        double noiseNaturalness,
        double boundaryConsistency,
        double symmetryScore,
        double qhrcPrimaryAlignment,
        double qhrcSecondaryAlignment,
        double qhrcDualCoherence,
        double qhrcRatioAlignment,
        String provenanceStatus,
        double provenanceConfidence,
        String provenanceSignals,
        String notes
    ) {}

    public record ScanResult(
        boolean success,
        String message,
        String sourceType,
        String source,
        String overallDecision,
        double overallRisk,
        double confidence,
        List<ScannedImage> images
    ) {}

    public static ScanResult scanFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return new ScanResult(false, "file path required", "file", "", "inconclusive", 0.5, 0.0, List.of());
        }
        if (!Files.exists(Paths.get(filePath))) {
            return new ScanResult(false, "file not found: " + filePath, "file", filePath, "inconclusive", 0.5, 0.0, List.of());
        }

        ScriptRun run = runScript(List.of("--file", filePath));
        if (!run.success()) {
            return new ScanResult(false, run.message(), "file", filePath, "inconclusive", 0.5, 0.0, List.of());
        }
        return parseJson(run.output(), "file", filePath);
    }

    public static ScanResult scanUrl(String url, int maxImages) {
        if (url == null || url.isBlank()) {
            return new ScanResult(false, "url required", "url", "", "inconclusive", 0.5, 0.0, List.of());
        }
        int limit = Math.max(1, maxImages);
        ScriptRun run = runScript(List.of("--url", url, "--max-images", Integer.toString(limit)));
        if (!run.success()) {
            return new ScanResult(false, run.message(), "url", url, "inconclusive", 0.5, 0.0, List.of());
        }
        return parseJson(run.output(), "url", url);
    }

    private static ScanResult parseJson(String raw, String fallbackType, String fallbackSource) {
        try {
            Object parsed = FraymusJSON.parseStrict(raw);
            if (!(parsed instanceof Map<?, ?> root)) {
                throw new IllegalArgumentException("Scan response must be a JSON object");
            }
            boolean ok = getBoolean(root, "ok", false);
            String message = getString(root, "message", ok ? "scan complete" : "scan failed");
            String sourceType = getString(root, "source_type", fallbackType);
            String source = getString(root, "source", fallbackSource);
            String overallDecision = getString(root, "overall_decision", "inconclusive");
            double overallRisk = getDouble(root, "overall_risk", 0.5);
            double confidence = getDouble(root, "confidence", 0.0);
            List<ScannedImage> images = parseImages(root.get("images"));

            return new ScanResult(
                ok,
                message,
                sourceType,
                source,
                overallDecision,
                overallRisk,
                confidence,
                images
            );
        } catch (Exception e) {
            return new ScanResult(
                false,
                "scan parse failed: " + e.getMessage(),
                fallbackType,
                fallbackSource,
                "inconclusive",
                0.5,
                0.0,
                List.of()
            );
        }
    }

    private static List<ScannedImage> parseImages(Object value) {
        List<ScannedImage> out = new ArrayList<>();
        if (value == null) return out;
        if (!(value instanceof List<?> arr)) {
            throw new IllegalArgumentException("Scan images must be an array");
        }
        for (Object item : arr) {
            if (!(item instanceof Map<?, ?> o)) continue;
            out.add(new ScannedImage(
                getString(o, "source", ""),
                getString(o, "decision", "inconclusive"),
                getDouble(o, "risk", 0.5),
                getDouble(o, "confidence", 0.0),
                (int) getDouble(o, "faces", 0.0),
                getDouble(o, "depth_consistency", 0.0),
                getDouble(o, "reflection_consistency", 0.0),
                getDouble(o, "noise_naturalness", 0.0),
                getDouble(o, "boundary_consistency", 0.0),
                getDouble(o, "symmetry_score", 0.0),
                getDouble(o, "qhrc_primary_alignment", 0.0),
                getDouble(o, "qhrc_secondary_alignment", 0.0),
                getDouble(o, "qhrc_dual_coherence", 0.0),
                getDouble(o, "qhrc_ratio_alignment", 0.0),
                getString(o, "provenance_status", "not_checked"),
                getDouble(o, "provenance_confidence", 0.0),
                joinNotes(o.get("provenance_signals")),
                joinNotes(o.get("notes"))
            ));
        }
        return out;
    }

    private static String joinNotes(Object value) {
        if (value == null) return "";
        if (!(value instanceof List<?> notes)) {
            throw new IllegalArgumentException("Scan notes must be an array");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notes.size(); i++) {
            if (i > 0) sb.append("; ");
            Object note = notes.get(i);
            sb.append(note instanceof String || note instanceof Number || note instanceof Boolean
                ? note.toString() : "note");
        }
        return sb.toString();
    }

    private static ScriptRun runScript(List<String> args) {
        if (!Files.exists(SCRIPT)) {
            return new ScriptRun(false, "", "script missing: " + SCRIPT);
        }

        List<List<String>> pythonCandidates = List.of(
            List.of("python"),
            List.of("py", "-3")
        );

        List<String> errors = new ArrayList<>();
        for (List<String> candidate : pythonCandidates) {
            List<String> cmd = new ArrayList<>(candidate);
            cmd.add(SCRIPT.toString());
            cmd.addAll(args);

            try {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                long waitSeconds = DEFAULT_TIMEOUT_SECONDS + PROCESS_TIMEOUT_GRACE_SECONDS;
                boolean finished = p.waitFor(waitSeconds, TimeUnit.SECONDS);
                if (!finished) {
                    p.destroyForcibly();
                    return new ScriptRun(false, "", String.join(" ", candidate) + ": timed out after " + waitSeconds + "s");
                }

                String output = readAll(p.getInputStream()).trim();
                if (looksLikeJson(output)) {
                    return new ScriptRun(true, output, "json");
                }
                if (p.exitValue() == 0) {
                    return new ScriptRun(true, output, "ok");
                }
                errors.add(String.join(" ", candidate) + ": exit " + p.exitValue() + " output=" + output);
            } catch (IOException ioe) {
                errors.add(String.join(" ", candidate) + ": " + ioe.getMessage());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return new ScriptRun(false, "", "interrupted");
            }
        }
        return new ScriptRun(false, "", String.join(" | ", errors));
    }

    private static boolean looksLikeJson(String output) {
        if (output == null) return false;
        String t = output.trim();
        return t.startsWith("{") && t.endsWith("}") && t.contains("\"ok\"");
    }

    private static String readAll(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!sb.isEmpty()) sb.append('\n');
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static double getDouble(Map<?, ?> obj, String key, double fallback) {
        Object value = obj.get(key);
        try {
            double number = value instanceof Number n ? n.doubleValue()
                : value instanceof String text ? Double.parseDouble(text) : fallback;
            return Double.isFinite(number) ? number : fallback;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static boolean getBoolean(Map<?, ?> obj, String key, boolean fallback) {
        Object value = obj.get(key);
        if (value instanceof Boolean b) return b;
        return value instanceof String text ? Boolean.parseBoolean(text) : fallback;
    }

    private static String getString(Map<?, ?> obj, String key, String fallback) {
        Object value = obj.get(key);
        return value instanceof String || value instanceof Number || value instanceof Boolean
            ? value.toString() : fallback;
    }

    private record ScriptRun(boolean success, String output, String message) {}
}
