package fraymus.orchestration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Hydra Operator CLI
 *
 * Real operator surface for Hydra status/evolve/proof outside test harnesses.
 *
 * Commands:
 * - status
 * - evolve
 * - proof
 */
public final class HydraOperatorCli {

    private static final String DEFAULT_TARGET = "aubo_ledger";
    private static final String DEFAULT_METHOD = "mind_coalescence";
    private static final String DEFAULT_FEATURES = "wisdom_of_crowds";
    private static final String DEFAULT_CONSTRAINTS = "incremental_proof_updates";
    private static final String DEFAULT_OUTPUT = "distributed_execution_graph";
    private static final int DEFAULT_FIELD_DIM = 16384;

    private HydraOperatorCli() {
        // Utility class.
    }

    public static void main(String[] args) {
        try {
            if (args == null || args.length == 0 || isHelpCommand(args[0])) {
                printUsage();
                return;
            }

            String command = args[0].toLowerCase(Locale.ROOT);
            Map<String, String> flags = parseFlags(args, 1);
            String workspaceOverride = flag(flags, "workspace-root", null);
            if (workspaceOverride != null && !workspaceOverride.isBlank()) {
                Path override = Paths.get(workspaceOverride).toAbsolutePath().normalize();
                System.setProperty("user.dir", override.toString());
            }

            switch (command) {
                case "status":
                    runStatus();
                    return;
                case "evolve":
                    runEvolve(flags);
                    return;
                case "proof":
                    runProof(flags);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown hydra operator command: " + command);
            }
        } catch (Exception ex) {
            System.err.println("Hydra operator failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void runStatus() {
        try (HydraProtocol hydra = new HydraProtocol()) {
            System.out.println(hydra.renderCompactStatus());
        }
    }

    private static void runEvolve(Map<String, String> flags) {
        String target = safeToken(flag(flags, "target", DEFAULT_TARGET), DEFAULT_TARGET);
        String method = safeToken(flag(flags, "method", DEFAULT_METHOD), DEFAULT_METHOD);
        String features = safeToken(flag(flags, "features", DEFAULT_FEATURES), DEFAULT_FEATURES);
        if (isTruthy(flags.get("zk")) && !features.toLowerCase(Locale.ROOT).contains("dynamic_zk_snarks")) {
            features = features + ",dynamic_zk_snarks";
        }
        String constraints = safeToken(flag(flags, "constraints", DEFAULT_CONSTRAINTS), DEFAULT_CONSTRAINTS);
        String output = safeToken(flag(flags, "output", DEFAULT_OUTPUT), DEFAULT_OUTPUT);
        int fieldDim = parseIntFlag(flags, "field-dim", DEFAULT_FIELD_DIM);

        String command = "ai intent=transcend"
            + " target=" + target
            + " method=" + method
            + " features=" + features
            + " constraints=" + constraints
            + " output=" + output;

        CommandParser.ParsedCommand parsed = CommandParser.parse(command);
        if (parsed == null || !parsed.isValid()) {
            throw new IllegalStateException("Unable to parse generated Hydra evolve command");
        }

        try (HydraProtocol hydra = new HydraProtocol()) {
            System.out.println(hydra.execute(parsed, fieldDim));
        }
    }

    private static void runProof(Map<String, String> flags) throws IOException {
        Path workspaceRoot = resolveWorkspaceRoot(flags);
        Path latestProof = workspaceRoot
            .resolve("release-capsules")
            .resolve("offline-production")
            .resolve("reports")
            .resolve("hydra-proof-latest.txt")
            .normalize();

        if (!Files.exists(latestProof)) {
            throw new IllegalStateException("Hydra proof artifact missing: " + latestProof);
        }

        if (isTruthy(flags.get("path-only"))) {
            System.out.println(latestProof);
            return;
        }

        System.out.println("proof_path=" + latestProof);
        System.out.println();
        System.out.println(Files.readString(latestProof, StandardCharsets.UTF_8));
    }

    private static Path resolveWorkspaceRoot(Map<String, String> flags) {
        String override = flag(flags, "workspace-root", null);
        if (override != null && !override.isBlank()) {
            return Paths.get(override).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
    }

    private static int parseIntFlag(Map<String, String> flags, String key, int fallback) {
        String value = flag(flags, key, null);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static String safeToken(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim().replace(' ', '_');
    }

    private static String flag(Map<String, String> flags, String key, String fallback) {
        if (flags == null) {
            return fallback;
        }
        String value = flags.get(key.toLowerCase(Locale.ROOT));
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return !normalized.isEmpty()
            && !"0".equals(normalized)
            && !"false".equals(normalized)
            && !"no".equals(normalized);
    }

    private static Map<String, String> parseFlags(String[] args, int startIndex) {
        Map<String, String> result = new LinkedHashMap<>();
        if (args == null) {
            return result;
        }
        for (int i = Math.max(0, startIndex); i < args.length; i++) {
            String token = args[i];
            if (token == null || !token.startsWith("--")) {
                continue;
            }
            String key = token.substring(2).toLowerCase(Locale.ROOT);
            String value = "true";
            if ((i + 1) < args.length && args[i + 1] != null && !args[i + 1].startsWith("--")) {
                value = args[i + 1];
                i++;
            }
            result.put(key, value);
        }
        return result;
    }

    private static boolean isHelpCommand(String token) {
        if (token == null) {
            return false;
        }
        String t = token.trim().toLowerCase(Locale.ROOT);
        return "help".equals(t) || "-h".equals(t) || "--help".equals(t);
    }

    private static void printUsage() {
        System.out.println("HydraOperatorCli");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  status [--workspace-root <path>]");
        System.out.println("  evolve [--workspace-root <path>] [--target <value>] [--method <value>]");
        System.out.println("         [--features <csv>] [--constraints <csv>] [--output <value>]");
        System.out.println("         [--field-dim <int>] [--zk]");
        System.out.println("  proof  [--workspace-root <path>] [--path-only]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  status");
        System.out.println("  evolve --features wisdom_of_crowds,dynamic_zk_snarks --constraints incremental_proof_updates");
        System.out.println("  proof --path-only");
    }
}
