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

public final class OpenCvFaceAbstraction {
    private static final int DEFAULT_CAPTURE_FRAMES = 48;
    private static final int DEFAULT_CAPTURE_TIMEOUT_SECONDS = 8;
    private static final long PROCESS_TIMEOUT_GRACE_SECONDS = 2;
    private static final Path CAPTURE_SCRIPT = Paths.get("scripts", "avatar", "opencv_capture.py");

    private OpenCvFaceAbstraction() {}

    public record FaceObservation(
        double interocularDistance,
        double symmetryScore,
        double[] grayscaleHistogram,
        double[] depthProfile,
        double[] thermalProfile,
        double[] landmarkVector
    ) {}

    public record CaptureResult(
        boolean success,
        String message,
        String backend,
        FaceObservation observation,
        BiometricAvatarService.LivenessSignals livenessSignals
    ) {}

    public static double[] toSovereignVector(FaceObservation obs) {
        if (obs == null) return new double[0];
        List<Double> flat = new ArrayList<>();
        flat.add(obs.interocularDistance());
        flat.add(obs.symmetryScore());
        append(flat, obs.grayscaleHistogram());
        append(flat, obs.depthProfile());
        append(flat, obs.thermalProfile());
        append(flat, obs.landmarkVector());
        double[] out = new double[flat.size()];
        for (int i = 0; i < flat.size(); i++) out[i] = flat.get(i);
        return out;
    }

    public static CaptureResult captureFromCamera(int cameraIndex, boolean opencvAllowed) {
        return captureFromCamera(cameraIndex, DEFAULT_CAPTURE_FRAMES, opencvAllowed);
    }

    public static CaptureResult captureFromCamera(int cameraIndex, int frameCount, boolean opencvAllowed) {
        if (!opencvAllowed) {
            return new CaptureResult(
                false,
                "OpenCV capture blocked: permission not granted",
                "none",
                null,
                null
            );
        }

        if (cameraIndex < 0) {
            return new CaptureResult(false, "Invalid camera index", "none", null, null);
        }

        if (frameCount < 16) {
            frameCount = 16;
        }

        if (!Files.exists(CAPTURE_SCRIPT)) {
            return new CaptureResult(
                false,
                "Capture script not found: " + CAPTURE_SCRIPT,
                "none",
                null,
                null
            );
        }

        ScriptRunResult run = runCaptureScript(cameraIndex, frameCount, DEFAULT_CAPTURE_TIMEOUT_SECONDS);
        if (!run.success()) {
            return new CaptureResult(false, run.message(), run.backend(), null, null);
        }

        return parseCaptureJson(run.output(), run.backend());
    }

    private static void append(List<Double> out, double[] src) {
        if (src == null) return;
        for (double v : src) out.add(v);
    }

    private static CaptureResult parseCaptureJson(String rawOutput, String backend) {
        try {
            Object parsed = FraymusJSON.parseStrict(rawOutput);
            if (!(parsed instanceof Map<?, ?> root)) {
                throw new IllegalArgumentException("Capture response must be a JSON object");
            }
            boolean ok = getBoolean(root, "ok", false);
            String message = getString(root, "message", ok ? "capture ok" : "capture failed");

            if (!ok) {
                return new CaptureResult(false, message, backend, null, null);
            }

            FaceObservation observation = new FaceObservation(
                getDouble(root, "interocular_distance", 0.0),
                getDouble(root, "symmetry_score", 0.0),
                getDoubleArray(root, "grayscale_histogram"),
                getDoubleArray(root, "depth_profile"),
                getDoubleArray(root, "thermal_profile"),
                getDoubleArray(root, "landmark_vector")
            );

            BiometricAvatarService.LivenessSignals liveness = new BiometricAvatarService.LivenessSignals(
                getDouble(root, "blink_rate_hz", 0.0),
                getDouble(root, "depth_variance", 0.0),
                getDouble(root, "parallax_score", 0.0),
                getDouble(root, "texture_variance", 0.0),
                getDouble(root, "thermal_delta", 0.0),
                getBoolean(root, "simulated", false)
            );

            return new CaptureResult(true, message, backend, observation, liveness);
        } catch (Exception e) {
            return new CaptureResult(false, "Capture parse failed: " + e.getMessage(), backend, null, null);
        }
    }

    private static ScriptRunResult runCaptureScript(int cameraIndex, int frameCount, int timeoutSeconds) {
        List<List<String>> pythonCandidates = List.of(
            List.of("python"),
            List.of("py", "-3")
        );

        String argsCamera = Integer.toString(cameraIndex);
        String argsFrames = Integer.toString(frameCount);
        String argsTimeout = Integer.toString(Math.max(2, timeoutSeconds));
        String scriptPath = CAPTURE_SCRIPT.toString();
        String backendHint = preferredBackendHint();

        List<String> errors = new ArrayList<>();

        for (List<String> candidate : pythonCandidates) {
            List<String> cmd = new ArrayList<>(candidate);
            cmd.add(scriptPath);
            cmd.add("--camera");
            cmd.add(argsCamera);
            cmd.add("--frames");
            cmd.add(argsFrames);
            cmd.add("--timeout-sec");
            cmd.add(argsTimeout);
            cmd.add("--backend");
            cmd.add(backendHint);

            try {
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                long waitSeconds = Math.max(3, timeoutSeconds) + PROCESS_TIMEOUT_GRACE_SECONDS;
                boolean finished = p.waitFor(waitSeconds, TimeUnit.SECONDS);
                if (!finished) {
                    p.destroyForcibly();
                    String message = String.join(" ", candidate)
                        + ": timed out after " + waitSeconds + "s";
                    errors.add(message);
                    return new ScriptRunResult(false, "", String.join(" ", candidate), message);
                }

                String output = readAll(p.getInputStream()).trim();
                if (looksLikeJsonCapturePayload(output)) {
                    return new ScriptRunResult(true, output, String.join(" ", candidate), "json");
                }
                if (p.exitValue() == 0) {
                    return new ScriptRunResult(true, output, String.join(" ", candidate), "ok");
                }

                errors.add(String.join(" ", candidate) + ": exit " + p.exitValue() + " output=" + output);
            } catch (IOException ioe) {
                errors.add(String.join(" ", candidate) + ": " + ioe.getMessage());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return new ScriptRunResult(false, "", String.join(" ", candidate), "interrupted");
            }
        }

        return new ScriptRunResult(false, "", "none", String.join(" | ", errors));
    }

    private static boolean looksLikeJsonCapturePayload(String output) {
        if (output == null) return false;
        String trimmed = output.trim();
        return trimmed.startsWith("{")
            && trimmed.endsWith("}")
            && trimmed.contains("\"ok\"");
    }

    private static String preferredBackendHint() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return "dshow";
        }
        return "any";
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

    private static double asDouble(Object value, double fallback) {
        try {
            double number = value instanceof Number n ? n.doubleValue()
                : value instanceof String text ? Double.parseDouble(text) : fallback;
            return Double.isFinite(number) ? number : fallback;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double getDouble(Map<?, ?> obj, String key, double fallback) {
        return asDouble(obj.get(key), fallback);
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

    private static double[] getDoubleArray(Map<?, ?> obj, String key) {
        if (!(obj.get(key) instanceof List<?> arr)) return new double[0];
        double[] out = new double[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            out[i] = asDouble(arr.get(i), 0.0);
        }
        return out;
    }

    private record ScriptRunResult(boolean success, String output, String backend, String message) {}
}
