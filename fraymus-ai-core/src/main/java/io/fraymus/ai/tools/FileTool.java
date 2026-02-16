package io.fraymus.ai.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FILE TOOL - Safe File Operations
 */
public class FileTool {

    public ToolResult listFiles(Map<String, Object> args) {
        try {
            String root = (String) args.get("path");
            int limit = args.containsKey("limit") ? (int) args.get("limit") : 50;

            Path p = Path.of(root);
            if (!Files.exists(p)) return new ToolResult("list_files", "Path not found: " + root);

            List<Path> files = Files.list(p).limit(limit).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            for (Path f : files) sb.append(f.toString()).append("\n");
            return new ToolResult("list_files", sb.toString());
        } catch (Exception e) {
            return new ToolResult("list_files", "[ERROR] " + e.getMessage());
        }
    }

    public ToolResult writeFile(Map<String, Object> args) {
        try {
            String rel = ((String) args.get("path")).replace("\\", "/");
            boolean overwrite = args.containsKey("overwrite") && (boolean) args.get("overwrite");
            String content = (String) args.get("content");

            if (!rel.startsWith("generated/")) {
                return new ToolResult("write_file", "DENIED: write_file only allowed under generated/");
            }

            Path p = Path.of(rel);
            Files.createDirectories(p.getParent());

            if (Files.exists(p) && !overwrite) {
                return new ToolResult("write_file", "DENIED: file exists (set overwrite=true) -> " + rel);
            }

            Files.writeString(p, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return new ToolResult("write_file", "WROTE: " + rel + " (" + content.length() + " chars)");
        } catch (Exception e) {
            return new ToolResult("write_file", "[ERROR] " + e.getMessage());
        }
    }
}
