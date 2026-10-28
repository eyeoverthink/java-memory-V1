package fraymus.genomics;

import fraymus.core.FraymusJSON;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Local process boundary for Carbon; no Python or model is loaded at Java boot. */
public final class CarbonGenomicsBridge {
    public record Result(boolean success, String output) {}

    private CarbonGenomicsBridge() {}

    public static boolean appliesTo(String query) {
        return query != null && java.util.regex.Pattern.compile(
            "\\b(dna|fasta|genomics|genomic|genome|nucleotide|plasmid|genotype|carbon-3b|apotheosis)\\b")
            .matcher(query.toLowerCase(Locale.ROOT)).find();
    }

    /** Supplies the actual local creation instructions without loading model weights. */
    public static String contextFor(String query) {
        if (!appliesTo(query)) return "";
        StringBuilder context = new StringBuilder("\nLOCAL GENOMICS KNOWLEDGE AND TOOLS:\n");
        context.append("Installed shell commands: genomics status; genomics inventory; ")
            .append("genomics analyze <FASTA path> --compare-shuffled; ")
            .append("genomics generate <primer FASTA path> --bases <count>; genomics learn; ")
            .append("genomics identify <FASTA path>; genomics profile. Quote paths with spaces.\n")
            .append("Java code can call fraymus.genomics.CarbonGenomicsBridge.execute(List<String> arguments).\n")
            .append("Inside the running app use the genomics shell or GENOMICS tool route so shared-brain absorption and provenance are retained; the direct bridge is the low-level worker boundary.\n")
            .append("Actual process results are required before claiming a file was created or a score was measured.\n")
            .append("Concept-vector encodings and model predictions do not establish biological function.\n");
        for (String file : List.of("skills/open_model_sources/generated/carbon-3b.md",
                "skills/genomics_carbon/SKILL.md")) {
            try {
                Path path = Path.of(file);
                if (Files.isRegularFile(path)) {
                    String content = Files.readString(path, StandardCharsets.UTF_8);
                    context.append("\nSource: ").append(file).append('\n')
                        .append(content, 0, Math.min(content.length(), 12000)).append('\n');
                }
            } catch (java.io.IOException error) {
                context.append("Knowledge source unavailable: ").append(file).append('\n');
            }
        }
        try {
            Path state = Path.of(".genomics/evidence/events");
            if (Files.isDirectory(state)) {
                String facts = FraymusJSON.stringify(new GenomicsEvidenceLedger(Path.of(".")).observations());
                context.append("\nABSORBED GENOMICS OBSERVATIONS (reference annotations and measured results):\n")
                    .append(facts, 0, Math.min(facts.length(), 16000)).append('\n');
            }
        } catch (java.io.IOException error) { context.append("Absorbed observation integrity unavailable: ").append(error.getMessage()).append('\n'); }
        return context.toString();
    }

    public static Result execute(String arguments) {
        try { return execute(splitArguments(arguments)); }
        catch (IllegalArgumentException error) { return failure(error.getMessage()); }
    }

    public static Result execute(List<String> arguments) {
        try {
            Path root = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
            if (!arguments.isEmpty() && arguments.get(0).equals("profile"))
                return new Result(true, FraymusJSON.stringify(new GenomicsEvidenceLedger(root).profile()));
            if (arguments.size() == 3 && arguments.get(0).equals("genome") && arguments.get(1).equals("verify"))
                return new Result(true, FraymusJSON.stringify(Map.of("ok", true,
                    "payload", DigitalGenomeArchive.readPayload(Path.of(arguments.get(2))), "round_trip_verified", true)));
            Path script = root.resolve("scripts/genomics/carbon_worker.py");
            if (!Files.isRegularFile(script)) return failure("Carbon worker not found: " + script);
            String configured = System.getProperty("fraynix.carbonConfig",
                System.getenv().getOrDefault("FRAYNIX_CARBON_CONFIG", ""));
            Path config = configured.isBlank()
                ? Path.of(System.getProperty("user.home"), ".fraymus", "models", "carbon-runtime.json")
                : Path.of(configured);
            String python = "python";
            long timeout = 3600;
            if (Files.isRegularFile(config)) {
                Object parsed = FraymusJSON.parseStrict(Files.readString(config, StandardCharsets.UTF_8));
                if (!(parsed instanceof Map<?, ?> values)) return failure("Carbon runtime config must be an object");
                if (values.get("python") instanceof String value && !value.isBlank()) python = value;
                if (values.get("timeout_seconds") instanceof Number value) timeout = Math.max(1, value.longValue());
            }
            List<String> command = new ArrayList<>(List.of(python, script.toString(), "--config", config.toString()));
            command.addAll(arguments.isEmpty() ? List.of("status") : arguments);
            Path logs = Files.createDirectories(root.resolve("reports/genomics/worker-logs"));
            String id = UUID.randomUUID().toString();
            Path output = logs.resolve(id + ".json"), errors = logs.resolve(id + ".log");
            Process process = new ProcessBuilder(command).directory(root.toFile())
                .redirectOutput(output.toFile()).redirectError(errors.toFile()).start();
            Thread cleanup = new Thread(() -> process.destroy(), "Carbon-Worker-Cleanup");
            Runtime.getRuntime().addShutdownHook(cleanup);
            try {
                if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    return failure("Carbon worker timed out; log: " + errors);
                }
                String response = Files.readString(output, StandardCharsets.UTF_8).trim();
                Object parsed = FraymusJSON.parseStrict(response);
                boolean success = process.exitValue() == 0 && parsed instanceof Map<?, ?> object
                    && Boolean.TRUE.equals(object.get("ok"));
                return new Result(success, response);
            } finally {
                if (process.isAlive()) process.destroyForcibly();
                Runtime.getRuntime().removeShutdownHook(cleanup);
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return failure("Carbon worker interrupted");
        } catch (Exception error) {
            return failure(error.getMessage());
        }
    }

    /** Quotes group paths; backslashes remain literal Windows path characters. */
    static List<String> splitArguments(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        char quote = 0;
        for (char c : input.trim().toCharArray()) {
            if (quote != 0) {
                if (c == quote) quote = 0;
                else word.append(c);
            } else if (c == '"' || c == '\'') quote = c;
            else if (Character.isWhitespace(c)) {
                if (!word.isEmpty()) { result.add(word.toString()); word.setLength(0); }
            } else word.append(c);
        }
        if (quote != 0) throw new IllegalArgumentException("Unclosed quote in genomics command");
        if (!word.isEmpty()) result.add(word.toString());
        return List.copyOf(result);
    }

    private static Result failure(String message) {
        return new Result(false, FraymusJSON.stringify(Map.of("ok", false, "error", String.valueOf(message))));
    }

    public static void main(String[] arguments) {
        Result result = execute(List.of(arguments));
        System.out.println(result.output());
        if (!result.success()) System.exit(1);
    }
}
