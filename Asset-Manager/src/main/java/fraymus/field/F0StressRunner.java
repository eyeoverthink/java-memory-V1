package fraymus.field;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Executes the F-0 adversarial stress lane.
 *
 * Prefers the Rust runtime when Cargo is available, but falls back to the
 * in-process Java field engine so the structured AI probe still exercises real
 * dynamics in shells that do not have Rust tooling installed.
 */
public final class F0StressRunner {
    private static final int DEFAULT_DIMENSION = 1024;
    private static final float DEFAULT_DT = 0.001f;
    private static final float DEFAULT_DAMPING = 0.5f;
    private static final float DEFAULT_COLLAPSE_THRESHOLD = 0.5f;
    private static final float FISHER_EPSILON = 0.01f;
    private static final int FISHER_SAMPLES = 32;

    public StressResult run(Path workspaceRoot) {
        Path engineDir = workspaceRoot == null ? null : workspaceRoot.resolve("f0_engine");
        boolean cargoAvailable = isCommandAvailable("cargo");

        if (cargoAvailable && engineDir != null && Files.exists(engineDir.resolve("Cargo.toml"))) {
            StressResult cargoResult = tryCargo(engineDir);
            if (cargoResult != null) {
                return cargoResult;
            }
        }

        return runJavaFallback(cargoAvailable, engineDir);
    }

    private StressResult tryCargo(Path engineDir) {
        Process process = null;
        try {
            process = new ProcessBuilder("cargo", "run", "--quiet", "--", "stress")
                .directory(engineDir.toFile())
                .redirectErrorStream(true)
                .start();
            boolean finished = process.waitFor(90, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return null;
            }

            String output = readAll(process.getInputStream());
            if (process.exitValue() != 0) {
                return null;
            }

            JSONObject json = new JSONObject(output.trim());
            return StressResult.fromCargo(json, output);
        } catch (Exception e) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private StressResult runJavaFallback(boolean cargoAvailable, Path engineDir) {
        FieldEngine engine = new FieldEngine(DEFAULT_DIMENSION, DEFAULT_DT, DEFAULT_DAMPING, DEFAULT_COLLAPSE_THRESHOLD);
        float[] attractor = createAttractor(engine.getDimension());

        engine.excite(attractor, 0.35f);
        for (int i = 0; i < 25; i++) {
            engine.attractorPull(0.08f, attractor);
            engine.integrate();
            if (i % 5 == 0) {
                engine.homeostasis(0.35f, 0.04f);
            }
        }

        float[] baseState = engine.getState();
        double baseCoherence = engine.computeCoherence(0.5f);
        double baseEnergy = engine.computeAttractorEnergy(attractor);

        float[] adversarial = baseState.clone();
        for (int i = 0; i < Math.min(64, adversarial.length); i++) {
            adversarial[i] = clamp(adversarial[i] + ((i % 2 == 0) ? 0.35f : -0.35f));
        }
        engine.setState(adversarial);
        for (int i = 0; i < 30; i++) {
            engine.integrate();
            engine.homeostasis(0.35f, 0.05f);
        }

        float[] stressedState = engine.getState();
        double stressedCoherence = engine.computeCoherence(0.5f);
        double stressedEnergy = engine.computeAttractorEnergy(attractor);
        double fisherTrace = fisherTrace(baseState, attractor, FISHER_EPSILON, FISHER_SAMPLES);
        double maxResponse = maxAbsDelta(baseState, stressedState);
        double meanActivity = meanAbs(stressedState);
        double stabilityScore = 1.0 / (1.0 + Math.max(0.0, stressedCoherence - baseCoherence));
        String verdict = stressedCoherence < (baseCoherence * 4.0) && stressedEnergy < (baseEnergy * 6.0)
            ? "PASS_EXECUTABLE_F0_STRESS_FALLBACK"
            : "FAIL_F0_COHERENCE_COLLAPSE";

        return new StressResult(
            verdict,
            cargoAvailable,
            false,
            true,
            "java-fallback",
            engineDir == null ? "(none)" : engineDir.toAbsolutePath().normalize().toString(),
            engine.getDimension(),
            DEFAULT_DAMPING,
            DEFAULT_DT,
            baseCoherence,
            stressedCoherence,
            stressedCoherence - baseCoherence,
            baseEnergy,
            stressedEnergy,
            stressedEnergy - baseEnergy,
            fisherTrace,
            maxResponse,
            meanActivity,
            stabilityScore,
            ""
        );
    }

    private float[] createAttractor(int dimension) {
        float[] attractor = new float[dimension];
        float[] seed = new float[] {0.8f, 0.9f, -0.5f, -0.8f, 0.2f, 0.1f, 0.9f, 1.0f};
        System.arraycopy(seed, 0, attractor, 0, seed.length);
        return attractor;
    }

    private double fisherTrace(float[] baseState, float[] attractor, float epsilon, int samples) {
        int limit = Math.min(samples, Math.min(baseState.length, attractor.length));
        double trace = 0.0;
        for (int i = 0; i < limit; i++) {
            float[] plus = baseState.clone();
            float[] minus = baseState.clone();
            plus[i] = clamp(plus[i] + epsilon);
            minus[i] = clamp(minus[i] - epsilon);
            double energyPlus = energy(plus, attractor);
            double energyMinus = energy(minus, attractor);
            double derivative = (energyPlus - energyMinus) / (2.0 * epsilon);
            trace += derivative * derivative;
        }
        return trace;
    }

    private double energy(float[] state, float[] attractor) {
        double energy = 0.0;
        for (int i = 0; i < Math.min(state.length, attractor.length); i++) {
            double diff = state[i] - attractor[i];
            energy += diff * diff;
        }
        return energy;
    }

    private double maxAbsDelta(float[] left, float[] right) {
        double max = 0.0;
        for (int i = 0; i < Math.min(left.length, right.length); i++) {
            max = Math.max(max, Math.abs(right[i] - left[i]));
        }
        return max;
    }

    private double meanAbs(float[] values) {
        double sum = 0.0;
        for (float value : values) {
            sum += Math.abs(value);
        }
        return values.length == 0 ? 0.0 : sum / values.length;
    }

    private float clamp(float value) {
        return Math.max(-1.0f, Math.min(1.0f, value));
    }

    private boolean isCommandAvailable(String command) {
        String pathValue = System.getenv("PATH");
        if (pathValue == null || pathValue.isBlank()) {
            return false;
        }
        for (String entry : pathValue.split(java.io.File.pathSeparator)) {
            Path directory = Path.of(entry);
            Path candidate = directory.resolve(command + (isWindows() ? ".exe" : ""));
            if (Files.isRegularFile(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private String readAll(InputStream inputStream) throws IOException {
        try (InputStream stream = inputStream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = stream.read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
            return output.toString(StandardCharsets.UTF_8);
        }
    }

    public static final class StressResult {
        private final String verdict;
        private final boolean cargoAvailable;
        private final boolean cargoExecuted;
        private final boolean javaFallback;
        private final String executionMode;
        private final String enginePath;
        private final int substrateSize;
        private final double damping;
        private final double dt;
        private final double baseCoherence;
        private final double stressedCoherence;
        private final double coherenceDelta;
        private final double baseAttractorEnergy;
        private final double stressedAttractorEnergy;
        private final double energyDelta;
        private final double fisherTrace;
        private final double maxResponse;
        private final double meanActivity;
        private final double stabilityScore;
        private final String rawOutput;

        public StressResult(
            String verdict,
            boolean cargoAvailable,
            boolean cargoExecuted,
            boolean javaFallback,
            String executionMode,
            String enginePath,
            int substrateSize,
            double damping,
            double dt,
            double baseCoherence,
            double stressedCoherence,
            double coherenceDelta,
            double baseAttractorEnergy,
            double stressedAttractorEnergy,
            double energyDelta,
            double fisherTrace,
            double maxResponse,
            double meanActivity,
            double stabilityScore,
            String rawOutput
        ) {
            this.verdict = verdict;
            this.cargoAvailable = cargoAvailable;
            this.cargoExecuted = cargoExecuted;
            this.javaFallback = javaFallback;
            this.executionMode = executionMode;
            this.enginePath = enginePath;
            this.substrateSize = substrateSize;
            this.damping = damping;
            this.dt = dt;
            this.baseCoherence = baseCoherence;
            this.stressedCoherence = stressedCoherence;
            this.coherenceDelta = coherenceDelta;
            this.baseAttractorEnergy = baseAttractorEnergy;
            this.stressedAttractorEnergy = stressedAttractorEnergy;
            this.energyDelta = energyDelta;
            this.fisherTrace = fisherTrace;
            this.maxResponse = maxResponse;
            this.meanActivity = meanActivity;
            this.stabilityScore = stabilityScore;
            this.rawOutput = rawOutput;
        }

        public static StressResult fromCargo(JSONObject json, String rawOutput) {
            return new StressResult(
                json.optString("verdict", "PASS_EXECUTABLE_F0_STRESS_LANE"),
                true,
                true,
                false,
                json.optString("executionMode", "cargo"),
                json.optString("enginePath", ""),
                json.optInt("substrateSize", DEFAULT_DIMENSION),
                json.optDouble("damping", DEFAULT_DAMPING),
                json.optDouble("dt", DEFAULT_DT),
                json.optDouble("baseCoherence", 0.0),
                json.optDouble("stressedCoherence", 0.0),
                json.optDouble("coherenceDelta", 0.0),
                json.optDouble("baseAttractorEnergy", 0.0),
                json.optDouble("stressedAttractorEnergy", 0.0),
                json.optDouble("energyDelta", 0.0),
                json.optDouble("fisherTrace", 0.0),
                json.optDouble("maxResponse", 0.0),
                json.optDouble("meanActivity", 0.0),
                json.optDouble("stabilityScore", 0.0),
                rawOutput
            );
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("verdict", verdict);
            json.put("cargoAvailable", cargoAvailable);
            json.put("cargoExecuted", cargoExecuted);
            json.put("javaFallback", javaFallback);
            json.put("executionMode", executionMode);
            json.put("enginePath", enginePath);
            json.put("substrateSize", substrateSize);
            json.put("damping", damping);
            json.put("dt", dt);
            json.put("baseCoherence", baseCoherence);
            json.put("stressedCoherence", stressedCoherence);
            json.put("coherenceDelta", coherenceDelta);
            json.put("baseAttractorEnergy", baseAttractorEnergy);
            json.put("stressedAttractorEnergy", stressedAttractorEnergy);
            json.put("energyDelta", energyDelta);
            json.put("fisherTrace", fisherTrace);
            json.put("maxResponse", maxResponse);
            json.put("meanActivity", meanActivity);
            json.put("stabilityScore", stabilityScore);
            json.put("rawOutput", rawOutput);
            return json;
        }

        public String verdict() {
            return verdict;
        }

        public boolean cargoAvailable() {
            return cargoAvailable;
        }

        public boolean cargoExecuted() {
            return cargoExecuted;
        }

        public boolean javaFallback() {
            return javaFallback;
        }

        public String executionMode() {
            return executionMode;
        }

        public String enginePath() {
            return enginePath;
        }

        public int substrateSize() {
            return substrateSize;
        }

        public double damping() {
            return damping;
        }

        public double dt() {
            return dt;
        }

        public double baseCoherence() {
            return baseCoherence;
        }

        public double stressedCoherence() {
            return stressedCoherence;
        }

        public double coherenceDelta() {
            return coherenceDelta;
        }

        public double baseAttractorEnergy() {
            return baseAttractorEnergy;
        }

        public double stressedAttractorEnergy() {
            return stressedAttractorEnergy;
        }

        public double energyDelta() {
            return energyDelta;
        }

        public double fisherTrace() {
            return fisherTrace;
        }

        public double maxResponse() {
            return maxResponse;
        }

        public double meanActivity() {
            return meanActivity;
        }

        public double stabilityScore() {
            return stabilityScore;
        }

        public String rawOutput() {
            return rawOutput;
        }
    }
}
