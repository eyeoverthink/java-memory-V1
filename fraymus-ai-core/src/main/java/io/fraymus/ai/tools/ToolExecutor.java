package io.fraymus.ai.tools;

import io.fraymus.ai.core.*;
import io.fraymus.ai.util.*;
import java.util.*;

/**
 * TOOL EXECUTOR - Function Calling
 * 
 * Executes tools: calc, list_files, write_file, etc.
 */
public class ToolExecutor {

    private final VectorStore vectorStore;
    private final DocumentProcessor documentProcessor;
    private final OllamaClient ollama;
    private final MathTool mathTool;
    private final FileTool fileTool;

    public ToolExecutor(VectorStore vectorStore, DocumentProcessor documentProcessor, OllamaClient ollama) {
        this.vectorStore = vectorStore;
        this.documentProcessor = documentProcessor;
        this.ollama = ollama;
        this.mathTool = new MathTool();
        this.fileTool = new FileTool();
    }

    /**
     * EXECUTE TOOL
     */
    public ToolResult execute(String toolName, Map<String, Object> args) {
        try {
            return switch (toolName) {
                case "calc" -> mathTool.execute(args);
                case "list_files" -> fileTool.listFiles(args);
                case "write_file" -> fileTool.writeFile(args);
                case "index_path" -> indexPath(args);
                default -> new ToolResult(toolName, "[UNKNOWN TOOL]");
            };
        } catch (Exception e) {
            return new ToolResult(toolName, "[ERROR] " + e.getMessage());
        }
    }

    private ToolResult indexPath(Map<String, Object> args) throws Exception {
        String root = (String) args.get("path");
        int chunkSize = args.containsKey("chunkSize") ? (int) args.get("chunkSize") : 1200;
        int overlap = args.containsKey("overlap") ? (int) args.get("overlap") : 200;

        Set<String> exts = new HashSet<>(List.of("txt", "md", "java", "js", "html", "css", "json", "xml", "pdf"));
        var files = documentProcessor.walkIndexableFiles(root, exts);
        int added = 0;

        for (var f : files) {
            String raw = documentProcessor.readFileToText(f.toString());
            String clean = documentProcessor.cleanse(raw);
            if (clean.isBlank()) continue;

            List<String> chunks = documentProcessor.chunk(clean, chunkSize, overlap);
            List<double[]> vecs = ollama.embedBatch(chunks);
            if (vecs.size() != chunks.size()) continue;

            vectorStore.addAndPersist(f.toString(), chunks, vecs);
            added += chunks.size();
        }

        return new ToolResult("index_path", 
            "INDEXED: " + files.size() + " files, " + added + " chunks. Vault size=" + vectorStore.size());
    }
}
