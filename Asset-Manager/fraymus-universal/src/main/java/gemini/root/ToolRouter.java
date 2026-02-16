package gemini.root;

import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TOOL ROUTER: Real commands behind the prompt.
 * 
 * This is where the system becomes more than a chatbot:
 * deterministic math, safe file writes, indexing, memory search.
 */
public class ToolRouter {

    public static class ToolResult {
        public final String tool;
        public final String output;
        public ToolResult(String tool, String output) {
            this.tool = tool;
            this.output = output;
        }
    }

    // Path security: allowlisted roots for file ops
    private static final Set<String> ALLOWED_ROOTS = Set.of(
        "docs", "vault_sources", "generated", "vault", "memory", "."
    );

    private final VectorVault vault;
    private final Transmudder transmudder;
    private final OllamaSpine brain;

    public ToolRouter(VectorVault vault, Transmudder transmudder, OllamaSpine brain) {
        this.vault = vault;
        this.transmudder = transmudder;
        this.brain = brain;
    }

    private final DNACloaking dna = new DNACloaking();

    /**
     * Normalize path and check against allowlist.
     * Returns null if path is not allowed.
     */
    private Path validatePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) return null;
        
        // Normalize and resolve
        Path p = Path.of(rawPath.replace("\\", "/")).normalize();
        String pStr = p.toString().replace("\\", "/");
        
        // Block parent traversal
        if (pStr.contains("..")) return null;
        
        // Check if starts with allowed root
        for (String root : ALLOWED_ROOTS) {
            if (pStr.equals(root) || pStr.startsWith(root + "/") || pStr.startsWith("./" + root)) {
                return p;
            }
        }
        
        // Allow absolute paths only if they resolve inside CWD
        try {
            Path cwd = Path.of(".").toAbsolutePath().normalize();
            Path abs = p.toAbsolutePath().normalize();
            if (abs.startsWith(cwd)) return p;
        } catch (Exception ignored) {}
        
        return null;
    }

    public ToolResult run(String tool, JsonObject args) {
        try {
            switch (tool) {
                case "calc":
                    return new ToolResult(tool, SafeMath.evalToString(args.get("expression").getAsString()));
                case "phi_calc":
                    return new ToolResult(tool, phiCalc(args));
                case "dna_hash":
                    return new ToolResult(tool, dnaHash(args));
                case "list_files":
                    return new ToolResult(tool, listFiles(args));
                case "write_file":
                    return new ToolResult(tool, writeFile(args));
                case "index_path":
                    return new ToolResult(tool, indexPath(args));
                case "memory_search":
                    return new ToolResult(tool, memorySearch(args));
                case "none":
                default:
                    return new ToolResult("none", "");
            }
        } catch (Exception e) {
            return new ToolResult(tool, "[TOOL_ERROR] " + e.getMessage());
        }
    }

    private String phiCalc(JsonObject args) {
        String op = args.has("op") ? args.get("op").getAsString() : "scale";
        double val = args.has("value") ? args.get("value").getAsDouble() : 1.0;

        return switch (op) {
            case "scale" -> "φ×" + val + " = " + PhiMath.phiScale(val);
            case "inverse" -> "φ⁻¹×" + val + " = " + PhiMath.phiInverse(val);
            case "fib" -> "fib(" + (int) val + ") = " + java.util.Arrays.toString(PhiMath.fibonacci((int) val));
            case "temp" -> "φ-temperature = " + PhiMath.phiTemperature();
            default -> "φ = " + PhiMath.PHI;
        };
    }

    private String dnaHash(JsonObject args) {
        String text = args.has("text") ? args.get("text").getAsString() : "";
        if (text.isEmpty()) return "Need 'text' argument";

        String phiHash = dna.generatePhiHash(text);
        Object[] comp = dna.compress(text);
        double phiRatio = (Double) comp[1];

        return "DNA: " + phiHash + " | φ-ratio: " + String.format("%.4f", phiRatio);
    }

    private String listFiles(JsonObject args) throws Exception {
        String root = args.get("path").getAsString();
        int limit = args.has("limit") ? args.get("limit").getAsInt() : 50;

        Path p = validatePath(root);
        if (p == null) return "DENIED: path not in allowlist: " + root;
        if (!Files.exists(p)) return "Path not found: " + root;

        List<Path> files = Files.list(p).limit(limit).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (Path f : files) sb.append(f.toString()).append("\n");
        return sb.toString();
    }

    private String writeFile(JsonObject args) throws Exception {
        String rel = args.get("path").getAsString().replace("\\", "/");
        boolean overwrite = args.has("overwrite") && args.get("overwrite").getAsBoolean();
        String content = args.get("content").getAsString();

        // Safety: only allow generated/
        if (!rel.startsWith("generated/")) {
            return "DENIED: write_file only allowed under generated/";
        }

        Path p = Path.of(rel);
        Files.createDirectories(p.getParent());

        if (Files.exists(p) && !overwrite) {
            return "DENIED: file exists (set overwrite=true) -> " + rel;
        }

        Files.writeString(p, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return "WROTE: " + rel + " (" + content.length() + " chars)";
    }

    private String indexPath(JsonObject args) throws Exception {
        String root = args.get("path").getAsString();
        int chunkSize = args.has("chunkSize") ? args.get("chunkSize").getAsInt() : 1200;
        int overlap = args.has("overlap") ? args.get("overlap").getAsInt() : 200;

        Path validated = validatePath(root);
        if (validated == null) return "DENIED: path not in allowlist: " + root;

        Set<String> exts = new HashSet<>(List.of("txt", "md", "java", "js", "html", "css", "json", "xml", "pdf"));

        List<Path> files = transmudder.walkIndexableFiles(validated.toString(), exts);
        int added = 0;

        for (Path f : files) {
            String raw = transmudder.readFileToText(f.toString());
            String clean = transmudder.cleanse(raw);
            if (clean.isBlank()) continue;

            List<String> chunks = transmudder.chunk(clean, chunkSize, overlap);
            List<float[]> vecs = brain.embedBatch(chunks);
            if (vecs.size() != chunks.size()) continue;

            vault.addAndPersist(f.toString(), chunks, vecs);
            added += chunks.size();
        }

        return "INDEXED: " + files.size() + " files, " + added + " chunks. Vault size=" + vault.size();
    }

    private String memorySearch(JsonObject args) {
        String q = args.get("query").getAsString().toLowerCase();
        int limit = args.has("limit") ? args.get("limit").getAsInt() : 10;

        List<GenesisBlock> chain = Hippocampus.chain;
        StringBuilder sb = new StringBuilder();
        int hits = 0;

        for (int i = chain.size() - 1; i >= 0 && hits < limit; i--) {
            GenesisBlock b = chain.get(i);
            if (b.data != null && b.data.toLowerCase().contains(q)) {
                sb.append("[").append(b.type).append("] ")
                  .append(new Date(b.timestamp)).append(" :: ")
                  .append(trim(b.data, 240)).append("\n");
                hits++;
            }
        }
        if (hits == 0) return "No memory hits for: " + q;
        return sb.toString();
    }

    private String trim(String s, int n) {
        if (s == null) return "";
        return s.length() <= n ? s : s.substring(0, n) + "...";
    }
}
