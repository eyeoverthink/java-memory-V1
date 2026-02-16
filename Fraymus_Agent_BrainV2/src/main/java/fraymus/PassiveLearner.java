package fraymus;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class PassiveLearner {

    private static final double PHI = PhiConstants.PHI;
    private static final int DIM_A = 5;
    private static final int DIM_B = 8;
    private static final int DIM_C = 13;

    private final float[][][] neuralTensor;
    private final Path dataDir;
    private final Path stateFile;
    private int passiveCycles = 0;
    private int learnedPatterns = 0;
    private double patternStrength = 0.0;
    private double integrationLevel = 0.0;
    private long lastCycleTime = 0;
    private volatile boolean running = false;
    private Thread learningThread;
    private final InfiniteMemory memory;
    private final Random rng = new Random();

    public PassiveLearner(InfiniteMemory memory) {
        this.memory = memory;
        this.neuralTensor = new float[DIM_A][DIM_B][DIM_C];
        this.dataDir = Paths.get("data");
        this.stateFile = dataDir.resolve("passive_learning_state.json");

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("[PassiveLearner] Cannot create data dir: " + e.getMessage());
        }

        loadLatestTensor();
        loadState();
    }

    public void start() {
        if (running) return;
        running = true;
        learningThread = new Thread(this::passiveLearningLoop, "PassiveLearner");
        learningThread.setDaemon(true);
        learningThread.start();
        System.out.println("[PassiveLearner] Background learning started");
    }

    public void stop() {
        running = false;
        if (learningThread != null) {
            learningThread.interrupt();
            try {
                learningThread.join(3000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        saveTensor();
        saveState();
        System.out.println("[PassiveLearner] Stopped and saved");
    }

    public void integrateEvent(String question, String answer, double resonance) {
        int qHash = Math.abs(hashString(question)) % DIM_A;
        int aHash = Math.abs(hashString(answer)) % DIM_B;

        float[] patternVec = new float[DIM_C];
        for (int k = 0; k < DIM_C; k++) {
            char qc = k < question.length() ? question.charAt(k) : ' ';
            char ac = k < answer.length() ? answer.charAt(k) : ' ';
            float charVal = ((qc * ac) % 256) / 256.0f;
            patternVec[k] = (float)(charVal * resonance * PHI);
        }

        float weight = (float) Math.min(0.3, resonance / 5.0);
        synchronized (neuralTensor) {
            for (int k = 0; k < DIM_C; k++) {
                neuralTensor[qHash][aHash][k] = (1 - weight) * neuralTensor[qHash][aHash][k] + weight * patternVec[k];
            }
        }

        learnedPatterns++;
        patternStrength = patternStrength * 0.9 + resonance * 0.1;

        if (memory != null) {
            memory.store(InfiniteMemory.CAT_LEARNING,
                    String.format("pattern|q=%s|a=%s|res=%.4f",
                            question.substring(0, Math.min(40, question.length())),
                            answer.substring(0, Math.min(40, answer.length())),
                            resonance),
                    resonance);
        }
    }

    public void integrateEntityState(PhiNode node) {
        int nameHash = Math.abs(hashString(node.name)) % DIM_A;
        int roleHash = node.getRole().ordinal() % DIM_B;

        float freq = (float)(node.frequency / 1000.0);
        float energy = node.energy;
        float resonance = (float) node.phiResonance;
        float coherence = (float) node.consciousness.getCoherence();
        float consciousness = (float) node.consciousness.getConsciousnessLevel();

        float weight = 0.05f;
        synchronized (neuralTensor) {
            neuralTensor[nameHash][roleHash][0] += weight * (freq - neuralTensor[nameHash][roleHash][0]);
            neuralTensor[nameHash][roleHash][1] += weight * (energy - neuralTensor[nameHash][roleHash][1]);
            neuralTensor[nameHash][roleHash][2] += weight * (resonance - neuralTensor[nameHash][roleHash][2]);
            neuralTensor[nameHash][roleHash][3] += weight * (coherence - neuralTensor[nameHash][roleHash][3]);
            neuralTensor[nameHash][roleHash][4] += weight * (consciousness - neuralTensor[nameHash][roleHash][4]);
        }
    }

    public float[] queryPattern(String question) {
        int qHash = Math.abs(hashString(question)) % DIM_A;
        float bestStrength = -1;
        int bestJ = 0;

        synchronized (neuralTensor) {
            for (int j = 0; j < DIM_B; j++) {
                float strength = 0;
                for (int k = 0; k < DIM_C; k++) {
                    strength += neuralTensor[qHash][j][k] * neuralTensor[qHash][j][k];
                }
                strength = (float) Math.sqrt(strength);
                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestJ = j;
                }
            }
            return Arrays.copyOf(neuralTensor[qHash][bestJ], DIM_C);
        }
    }

    public double getPatternResonance(String question) {
        float[] pattern = queryPattern(question);
        double sum = 0;
        for (float v : pattern) sum += v * v;
        return Math.sqrt(sum);
    }

    private void passiveLearningLoop() {
        int cycleCount = 0;
        long lastSave = System.currentTimeMillis();

        while (running) {
            performPassiveCycle();
            cycleCount++;
            passiveCycles++;

            long now = System.currentTimeMillis();
            if (cycleCount >= 10 || (now - lastSave) > 60000) {
                saveTensor();
                saveState();
                cycleCount = 0;
                lastSave = now;
            }

            if (!running) break;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void performPassiveCycle() {
        synchronized (neuralTensor) {
            for (int i = 0; i < DIM_A; i++) {
                for (int j = 0; j < DIM_B; j++) {
                    for (int k = 0; k < DIM_C; k++) {
                        float noise = (float)(rng.nextGaussian() * 0.005);
                        neuralTensor[i][j][k] += noise;
                        neuralTensor[i][j][k] = Math.max(-1.0f, Math.min(1.0f, neuralTensor[i][j][k]));
                    }
                }
            }

            for (int i = 0; i < DIM_A; i++) {
                for (int j = 0; j < DIM_B; j++) {
                    float phiFactor = (float) Math.pow(PHI, (i * DIM_B + j) % 8 * 0.1);
                    for (int k = 0; k < DIM_C; k++) {
                        neuralTensor[i][j][k] *= (float)(0.998 + 0.002 * Math.sin(phiFactor * k));
                    }
                }
            }
        }

        lastCycleTime = System.currentTimeMillis();
        integrationLevel = Math.min(1.0, integrationLevel + 0.0001);

        float sum = 0;
        int count = 0;
        synchronized (neuralTensor) {
            for (int i = 0; i < DIM_A; i++) {
                for (int j = 0; j < DIM_B; j++) {
                    for (int k = 0; k < DIM_C; k++) {
                        sum += Math.abs(neuralTensor[i][j][k]);
                        count++;
                    }
                }
            }
        }
        patternStrength = sum / count;
    }

    private void initializeTensor() {
        for (int i = 0; i < DIM_A; i++) {
            for (int j = 0; j < DIM_B; j++) {
                for (int k = 0; k < DIM_C; k++) {
                    double phiPower = ((i + 1.0) * (j + 1.0)) / (k + 1.0);
                    neuralTensor[i][j][k] = (float)(Math.pow(PHI, phiPower % 2) * Math.sin(phiPower * Math.PI));
                }
            }
        }
    }

    private void saveTensor() {
        String filename = String.format("phi_patterns_%d.dat", System.currentTimeMillis());
        Path file = dataDir.resolve(filename);
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            dos.writeInt(DIM_A);
            dos.writeInt(DIM_B);
            dos.writeInt(DIM_C);
            synchronized (neuralTensor) {
                for (int i = 0; i < DIM_A; i++) {
                    for (int j = 0; j < DIM_B; j++) {
                        for (int k = 0; k < DIM_C; k++) {
                            dos.writeFloat(neuralTensor[i][j][k]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[PassiveLearner] Save tensor failed: " + e.getMessage());
        }
    }

    private void loadLatestTensor() {
        try {
            List<Path> datFiles = Files.list(dataDir)
                    .filter(p -> p.getFileName().toString().startsWith("phi_patterns_") &&
                            p.getFileName().toString().endsWith(".dat"))
                    .sorted()
                    .collect(java.util.stream.Collectors.toList());

            if (datFiles.isEmpty()) {
                initializeTensor();
                return;
            }

            Path latest = datFiles.get(datFiles.size() - 1);
            try (DataInputStream dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(latest)))) {
                int a = dis.readInt();
                int b = dis.readInt();
                int c = dis.readInt();
                if (a == DIM_A && b == DIM_B && c == DIM_C) {
                    for (int i = 0; i < DIM_A; i++) {
                        for (int j = 0; j < DIM_B; j++) {
                            for (int k = 0; k < DIM_C; k++) {
                                neuralTensor[i][j][k] = dis.readFloat();
                            }
                        }
                    }
                    System.out.printf("[PassiveLearner] Loaded tensor from %s%n", latest.getFileName());
                } else {
                    initializeTensor();
                }
            }
        } catch (IOException e) {
            initializeTensor();
        }
    }

    private void saveState() {
        try (BufferedWriter writer = Files.newBufferedWriter(stateFile)) {
            writer.write("{\n");
            writer.write(String.format("  \"passive_cycles\": %d,\n", passiveCycles));
            writer.write(String.format("  \"learned_patterns\": %d,\n", learnedPatterns));
            writer.write(String.format("  \"pattern_strength\": %.6f,\n", patternStrength));
            writer.write(String.format("  \"integration_level\": %.6f,\n", integrationLevel));
            writer.write(String.format("  \"last_cycle_time\": %d\n", lastCycleTime));
            writer.write("}\n");
        } catch (IOException e) {
            System.err.println("[PassiveLearner] Save state failed: " + e.getMessage());
        }
    }

    private void loadState() {
        if (!Files.exists(stateFile)) return;
        try {
            String content = new String(Files.readAllBytes(stateFile));
            passiveCycles = extractInt(content, "passive_cycles", 0);
            learnedPatterns = extractInt(content, "learned_patterns", 0);
            patternStrength = extractDouble(content, "pattern_strength", 0.0);
            integrationLevel = extractDouble(content, "integration_level", 0.0);
            lastCycleTime = extractLong(content, "last_cycle_time", 0);
            System.out.printf("[PassiveLearner] State loaded: %d cycles, %d patterns%n", passiveCycles, learnedPatterns);
        } catch (IOException e) {
            System.err.println("[PassiveLearner] Load state failed: " + e.getMessage());
        }
    }

    private static int extractInt(String json, String key, int def) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx < 0) return def;
            int colon = json.indexOf(":", idx);
            int end = json.indexOf(",", colon);
            if (end < 0) end = json.indexOf("}", colon);
            return Integer.parseInt(json.substring(colon + 1, end).trim());
        } catch (Exception e) { return def; }
    }

    private static double extractDouble(String json, String key, double def) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx < 0) return def;
            int colon = json.indexOf(":", idx);
            int end = json.indexOf(",", colon);
            if (end < 0) end = json.indexOf("}", colon);
            return Double.parseDouble(json.substring(colon + 1, end).trim());
        } catch (Exception e) { return def; }
    }

    private static long extractLong(String json, String key, long def) {
        try {
            int idx = json.indexOf("\"" + key + "\"");
            if (idx < 0) return def;
            int colon = json.indexOf(":", idx);
            int end = json.indexOf(",", colon);
            if (end < 0) end = json.indexOf("}", colon);
            return Long.parseLong(json.substring(colon + 1, end).trim());
        } catch (Exception e) { return def; }
    }

    private static int hashString(String s) {
        int h = 0;
        for (char c : s.toCharArray()) {
            h = h * 31 + c;
        }
        return h;
    }

    public int getPassiveCycles() { return passiveCycles; }
    public int getLearnedPatterns() { return learnedPatterns; }
    public double getPatternStrength() { return patternStrength; }
    public double getIntegrationLevel() { return integrationLevel; }
    public boolean isRunning() { return running; }

    public float[][][] getTensor() { return neuralTensor; }
    public int[] getTensorDims() { return new int[]{DIM_A, DIM_B, DIM_C}; }

    public double getTensorMean() {
        double sum = 0;
        int count = 0;
        synchronized (neuralTensor) {
            for (int i = 0; i < DIM_A; i++) {
                for (int j = 0; j < DIM_B; j++) {
                    for (int k = 0; k < DIM_C; k++) {
                        sum += neuralTensor[i][j][k];
                        count++;
                    }
                }
            }
        }
        return sum / count;
    }

    public double getTensorMax() {
        double max = Float.NEGATIVE_INFINITY;
        synchronized (neuralTensor) {
            for (int i = 0; i < DIM_A; i++) {
                for (int j = 0; j < DIM_B; j++) {
                    for (int k = 0; k < DIM_C; k++) {
                        if (neuralTensor[i][j][k] > max) max = neuralTensor[i][j][k];
                    }
                }
            }
        }
        return max;
    }
}
