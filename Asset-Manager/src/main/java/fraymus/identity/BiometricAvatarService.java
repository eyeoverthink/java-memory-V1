package fraymus.identity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class BiometricAvatarService {
    private static final int TEMPLATE_BITS = 256;
    private static final int TEMPLATE_BYTES = TEMPLATE_BITS / 8;
    private static final int MIN_VECTOR_LEN = 32;

    private final Path root;
    private final Path profiles;
    private final Path consent;
    private final Path revoked;

    public record OperationResult(boolean success, String message) {}
    public record EnrollmentResult(boolean success, String message, String profilePath) {}
    public record VerificationResult(boolean success, String message, double similarity) {}
    public record LivenessResult(boolean passed, double score, String reason) {}
    public record LivenessSignals(
        double blinkRateHz,
        double depthVariance,
        double parallaxScore,
        double textureVariance,
        double thermalDelta,
        boolean simulated
    ) {
        public static LivenessSignals simulatedLocal() {
            return new LivenessSignals(0.32, 0.06, 0.24, 0.12, 0.08, true);
        }
    }

    public BiometricAvatarService(String rootPath) {
        this.root = Paths.get(rootPath);
        this.profiles = root.resolve("profiles");
        this.consent = root.resolve("consent");
        this.revoked = root.resolve("revoked");
        ensureDirs();
    }

    public String statusSummary() {
        try {
            long p = Files.exists(profiles) ? Files.list(profiles).count() : 0;
            long c = Files.exists(consent) ? Files.list(consent).count() : 0;
            long r = Files.exists(revoked) ? Files.list(revoked).count() : 0;
            return "root=" + root + " profiles=" + p + " consents=" + c + " revoked=" + r;
        } catch (Exception e) {
            return "root=" + root + " error=" + e.getMessage();
        }
    }

    public OperationResult recordConsent(String subjectId, String text) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new OperationResult(false, "invalid subject id");
        if (text == null || text.isBlank()) return new OperationResult(false, "consent text required");
        if (!text.toUpperCase(Locale.ROOT).contains("I CONSENT")) {
            return new OperationResult(false, "consent must contain 'I CONSENT'");
        }
        try {
            String payload = "subject=" + id + "\n" +
                "consentAt=" + Instant.now() + "\n" +
                "hash=" + sha256Hex(text) + "\n";
            Files.writeString(consentPath(id), payload, StandardCharsets.UTF_8);
            return new OperationResult(true, "consent recorded for " + id);
        } catch (Exception e) {
            return new OperationResult(false, "consent write failed: " + e.getMessage());
        }
    }

    public EnrollmentResult enroll(String subjectId, String secret, double[] vector, LivenessSignals live) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new EnrollmentResult(false, "invalid subject id", "");
        if (!Files.exists(consentPath(id))) return new EnrollmentResult(false, "missing consent for subject", "");
        if (Files.exists(revokedPath(id))) return new EnrollmentResult(false, "subject is revoked", "");
        if (secret == null || secret.length() < 8) return new EnrollmentResult(false, "secret must be >= 8 chars", "");
        if (vector == null || vector.length < MIN_VECTOR_LEN) return new EnrollmentResult(false, "vector too short", "");
        LivenessResult l = validateLiveness(live);
        if (!l.passed()) return new EnrollmentResult(false, "liveness failed: " + l.reason(), "");
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            byte[] tpl = buildTemplate(vector, secret, id, salt);
            String payload = "subject=" + id + "\n" +
                "createdAt=" + Instant.now() + "\n" +
                "vectorLen=" + vector.length + "\n" +
                "saltB64=" + Base64.getEncoder().encodeToString(salt) + "\n" +
                "templateB64=" + Base64.getEncoder().encodeToString(tpl) + "\n" +
                "livenessScore=" + String.format(Locale.ROOT, "%.4f", l.score()) + "\n" +
                "simulated=" + (live != null && live.simulated()) + "\n";
            Files.writeString(profilePath(id), payload, StandardCharsets.UTF_8);
            return new EnrollmentResult(true, "enrolled " + id, profilePath(id).toString());
        } catch (Exception e) {
            return new EnrollmentResult(false, "enroll failed: " + e.getMessage(), "");
        }
    }

    public EnrollmentResult enrollFromObservation(
        String subjectId,
        String secret,
        OpenCvFaceAbstraction.FaceObservation observation,
        LivenessSignals live
    ) {
        return enroll(subjectId, secret, OpenCvFaceAbstraction.toSovereignVector(observation), live);
    }

    public VerificationResult verify(String subjectId, String secret, double[] vector, LivenessSignals live) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new VerificationResult(false, "invalid subject id", 0.0);
        if (Files.exists(revokedPath(id))) return new VerificationResult(false, "subject is revoked", 0.0);
        if (!Files.exists(profilePath(id))) return new VerificationResult(false, "no profile for subject", 0.0);
        if (secret == null || secret.length() < 8) return new VerificationResult(false, "secret must be >= 8 chars", 0.0);
        if (vector == null || vector.length < MIN_VECTOR_LEN) return new VerificationResult(false, "vector too short", 0.0);
        LivenessResult l = validateLiveness(live);
        if (!l.passed()) return new VerificationResult(false, "liveness failed: " + l.reason(), 0.0);
        try {
            Map<String, String> kv = readKv(profilePath(id));
            byte[] salt = Base64.getDecoder().decode(kv.getOrDefault("saltB64", ""));
            byte[] expected = Base64.getDecoder().decode(kv.getOrDefault("templateB64", ""));
            byte[] observed = buildTemplate(vector, secret, id, salt);
            double sim = bitSimilarity(expected, observed);
            return sim >= 0.86
                ? new VerificationResult(true, "verified " + id, sim)
                : new VerificationResult(false, "verification mismatch", sim);
        } catch (Exception e) {
            return new VerificationResult(false, "verify failed: " + e.getMessage(), 0.0);
        }
    }

    public VerificationResult verifyFromObservation(
        String subjectId,
        String secret,
        OpenCvFaceAbstraction.FaceObservation observation,
        LivenessSignals live
    ) {
        return verify(subjectId, secret, OpenCvFaceAbstraction.toSovereignVector(observation), live);
    }

    public OperationResult revoke(String subjectId, String reason) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new OperationResult(false, "invalid subject id");
        try {
            String payload = "subject=" + id + "\n" +
                "revokedAt=" + Instant.now() + "\n" +
                "reason=" + (reason == null ? "operator_request" : reason) + "\n";
            Files.writeString(revokedPath(id), payload, StandardCharsets.UTF_8);
            Files.deleteIfExists(profilePath(id));
            return new OperationResult(true, "revoked " + id);
        } catch (Exception e) {
            return new OperationResult(false, "revoke failed: " + e.getMessage());
        }
    }

    public OperationResult exportSubject(String subjectId, String targetPath) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new OperationResult(false, "invalid subject id");
        try {
            Path out = (targetPath == null || targetPath.isBlank())
                ? root.resolve("exports").resolve(id + "-export.json")
                : Paths.get(targetPath);
            Files.createDirectories(out.getParent());
            String consentText = Files.exists(consentPath(id)) ? Files.readString(consentPath(id), StandardCharsets.UTF_8) : "";
            String profileText = Files.exists(profilePath(id)) ? Files.readString(profilePath(id), StandardCharsets.UTF_8) : "";
            String revokedText = Files.exists(revokedPath(id)) ? Files.readString(revokedPath(id), StandardCharsets.UTF_8) : "";
            String json = "{\n" +
                "  \"subject\": \"" + escapeJson(id) + "\",\n" +
                "  \"exportedAt\": \"" + Instant.now() + "\",\n" +
                "  \"consent\": \"" + escapeJson(consentText) + "\",\n" +
                "  \"profile\": \"" + escapeJson(profileText) + "\",\n" +
                "  \"revoked\": \"" + escapeJson(revokedText) + "\"\n" +
                "}\n";
            Files.writeString(out, json, StandardCharsets.UTF_8);
            return new OperationResult(true, "exported " + id + " -> " + out);
        } catch (Exception e) {
            return new OperationResult(false, "export failed: " + e.getMessage());
        }
    }

    public OperationResult deleteSubject(String subjectId) {
        String id = normalizeSubject(subjectId);
        if (id.isBlank()) return new OperationResult(false, "invalid subject id");
        try {
            Files.deleteIfExists(profilePath(id));
            Files.deleteIfExists(consentPath(id));
            Files.deleteIfExists(revokedPath(id));
            return new OperationResult(true, "deleted subject data for " + id);
        } catch (Exception e) {
            return new OperationResult(false, "delete failed: " + e.getMessage());
        }
    }

    public LivenessResult validateLiveness(LivenessSignals s) {
        if (s == null) return new LivenessResult(false, 0.0, "missing liveness signals");
        int checks = 0;
        int pass = 0;
        double score = 0.0;
        if (!Double.isNaN(s.blinkRateHz())) { checks++; if (s.blinkRateHz() >= 0.08 && s.blinkRateHz() <= 1.5) { pass++; score += 0.20; } }
        if (!Double.isNaN(s.depthVariance())) { checks++; if (s.depthVariance() >= 0.015) { pass++; score += 0.20; } }
        if (!Double.isNaN(s.parallaxScore())) { checks++; if (s.parallaxScore() >= 0.10) { pass++; score += 0.20; } }
        if (!Double.isNaN(s.textureVariance())) { checks++; if (s.textureVariance() >= 0.04) { pass++; score += 0.20; } }
        if (!Double.isNaN(s.thermalDelta())) { checks++; if (s.thermalDelta() >= 0.02) { pass++; score += 0.20; } }
        if (checks < 3 && !s.simulated()) return new LivenessResult(false, score, "insufficient live channels");
        boolean passed = s.simulated() ? score >= 0.40 : (pass >= 3 && score >= 0.60);
        return new LivenessResult(passed, score, passed ? "ok" : "spoof risk high");
    }

    private byte[] buildTemplate(double[] vector, String secret, String subject, byte[] salt) throws Exception {
        double[] n = normalize(vector);
        Random rnd = new Random(seed(secret, subject, salt));
        byte[] out = new byte[TEMPLATE_BYTES];
        for (int bit = 0; bit < TEMPLATE_BITS; bit++) {
            double sum = 0.0;
            for (int hop = 0; hop < 16; hop++) {
                int idx = rnd.nextInt(n.length);
                double w = 0.5 + rnd.nextDouble();
                int sign = rnd.nextBoolean() ? 1 : -1;
                sum += n[idx] * w * sign;
            }
            if (sum >= 0) out[bit / 8] |= (byte) (1 << (bit % 8));
        }
        return out;
    }

    private double[] normalize(double[] v) {
        double mean = 0.0;
        for (double x : v) mean += x;
        mean /= Math.max(1, v.length);
        double var = 0.0;
        for (double x : v) { double d = x - mean; var += d * d; }
        var /= Math.max(1, v.length);
        double std = Math.sqrt(Math.max(var, 1e-9));
        double[] out = new double[v.length];
        for (int i = 0; i < v.length; i++) out[i] = (v[i] - mean) / std;
        return out;
    }

    private long seed(String secret, String subject, byte[] salt) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        d.update(secret.getBytes(StandardCharsets.UTF_8));
        d.update((byte) ':');
        d.update(subject.getBytes(StandardCharsets.UTF_8));
        d.update((byte) ':');
        d.update(salt);
        byte[] h = d.digest();
        long seed = 0L;
        for (int i = 0; i < 8; i++) seed = (seed << 8) | (h[i] & 0xFFL);
        return seed;
    }

    private double bitSimilarity(byte[] a, byte[] b) {
        int bytes = Math.min(a.length, b.length);
        int bits = bytes * 8;
        int dist = 0;
        for (int i = 0; i < bytes; i++) dist += Integer.bitCount((a[i] ^ b[i]) & 0xFF);
        return bits == 0 ? 0.0 : 1.0 - (dist / (double) bits);
    }

    private Map<String, String> readKv(Path p) throws IOException {
        Map<String, String> out = new HashMap<>();
        List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
        for (String line : lines) {
            int idx = line.indexOf('=');
            if (idx <= 0) continue;
            out.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
        }
        return out;
    }

    private String normalizeSubject(String subjectId) {
        if (subjectId == null) return "";
        return subjectId.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "_");
    }

    private Path profilePath(String id) { return profiles.resolve(id + ".profile"); }
    private Path consentPath(String id) { return consent.resolve(id + ".consent"); }
    private Path revokedPath(String id) { return revoked.resolve(id + ".revoked"); }

    private void ensureDirs() {
        try {
            Files.createDirectories(root);
            Files.createDirectories(profiles);
            Files.createDirectories(consent);
            Files.createDirectories(revoked);
            Files.createDirectories(root.resolve("exports"));
        } catch (IOException e) {
            throw new IllegalStateException("failed to init avatar vault: " + e.getMessage(), e);
        }
    }

    private String sha256Hex(String text) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        byte[] hash = d.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            sb.append(Character.forDigit((b >>> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
    }
}
