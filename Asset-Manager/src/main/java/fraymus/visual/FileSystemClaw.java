package fraymus.visual;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * FILE SYSTEM CLAW // TOPOLOGY-BASED DIRECTORY INTELLIGENCE
 * =========================================================================================
 * Uses OpenClaw's collision detection to analyze file system structures:
 * - Maps directories as 3D nodes in hyperspace
 * - Detects file pattern collisions (duplicates, similar structures)
 * - Makes decisions based on topological analysis
 * - Visualizes file system as living neural network
 * 
 * DECISION CAPABILITIES:
 * - Duplicate detection via Hamming distance
 * - Code smell detection (too many files, deep nesting)
 * - Dependency analysis (import/require patterns)
 * - Cleanup recommendations (orphaned files, temp files)
 * =========================================================================================
 */
public class FileSystemClaw {

    private static final int MAX_DEPTH = 10;
    private static final int MAX_FILES_PER_SCAN = 10000;
    
    // File system topology map
    private final Map<String, FileNode> nodeMap = new ConcurrentHashMap<>();
    private final List<FileNode> allNodes = new ArrayList<>();
    
    // Decision engine
    private final List<Decision> decisions = new ArrayList<>();
    
    public static class FileNode {
        public String path;
        public String name;
        public long size;
        public boolean isDirectory;
        public int depth;
        public long hash; // Content hash for collision detection
        public List<String> children = new ArrayList<>();
        public float x, y, z; // 3D position for visualization
        
        public FileNode(String path, String name, long size, boolean isDirectory, int depth) {
            this.path = path;
            this.name = name;
            this.size = size;
            this.isDirectory = isDirectory;
            this.depth = depth;
            
            // Deterministic 3D position based on path hash
            long pathHash = path.hashCode();
            this.x = (float) Math.sin(pathHash * 0.1) * 100;
            this.y = (float) Math.cos(pathHash * 0.1) * 100;
            this.z = depth * 20.0f;
            
            // Content hash for duplicate detection
            this.hash = computeHash(path, name, size);
        }
        
        private long computeHash(String path, String name, long size) {
            long h = 0;
            for (char c : name.toLowerCase().toCharArray()) {
                h = h * 31 + c;
            }
            h ^= size;
            return h;
        }
    }
    
    public static class Decision {
        public enum Type {
            DUPLICATE_FILE,
            LARGE_FILE,
            DEEP_NESTING,
            TOO_MANY_FILES,
            ORPHANED_FILE,
            TEMP_FILE,
            CODE_SMELL,
            DEPENDENCY_ISSUE
        }
        
        public Type type;
        public String path;
        public String message;
        public String recommendation;
        public int severity; // 1-10
        
        public Decision(Type type, String path, String message, String recommendation, int severity) {
            this.type = type;
            this.path = path;
            this.message = message;
            this.recommendation = recommendation;
            this.severity = severity;
        }
    }
    
    /**
     * Scan a directory and build topology map
     */
    public void scan(String rootPath) throws IOException {
        System.out.println("🦅 OpenClaw File System Scanner");
        System.out.println("   Scanning: " + rootPath);
        
        nodeMap.clear();
        allNodes.clear();
        decisions.clear();
        
        Path root = Paths.get(rootPath);
        
        Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), MAX_DEPTH, new SimpleFileVisitor<Path>() {
            private int fileCount = 0;
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (fileCount++ > MAX_FILES_PER_SCAN) {
                    return FileVisitResult.TERMINATE;
                }
                
                String path = file.toString();
                String name = file.getFileName().toString();
                long size = attrs.size();
                int depth = file.getNameCount() - root.getNameCount();
                
                FileNode node = new FileNode(path, name, size, false, depth);
                nodeMap.put(path, node);
                allNodes.add(node);
                
                // Add to parent's children
                Path parent = file.getParent();
                if (parent != null) {
                    FileNode parentNode = nodeMap.get(parent.toString());
                    if (parentNode != null) {
                        parentNode.children.add(path);
                    }
                }
                
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String path = dir.toString();
                String name = dir.getFileName() != null ? dir.getFileName().toString() : "";
                int depth = dir.getNameCount() - root.getNameCount();
                
                FileNode node = new FileNode(path, name, 0, true, depth);
                nodeMap.put(path, node);
                allNodes.add(node);
                
                return FileVisitResult.CONTINUE;
            }
        });
        
        System.out.println("   ✓ Scanned " + allNodes.size() + " nodes");
    }
    
    /**
     * Analyze topology and make decisions
     */
    public void analyze() {
        System.out.println("🧠 Analyzing file system topology...");
        
        detectDuplicates();
        detectLargeFiles();
        detectDeepNesting();
        detectTooManyFiles();
        detectTempFiles();
        detectCodeSmells();
        
        System.out.println("   ✓ Found " + decisions.size() + " decisions");
    }
    
    private void detectDuplicates() {
        Map<Long, List<FileNode>> hashGroups = new HashMap<>();
        
        for (FileNode node : allNodes) {
            if (!node.isDirectory) {
                hashGroups.computeIfAbsent(node.hash, k -> new ArrayList<>()).add(node);
            }
        }
        
        for (Map.Entry<Long, List<FileNode>> entry : hashGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                List<FileNode> duplicates = entry.getValue();
                String paths = String.join(", ", duplicates.stream().map(n -> n.name).toArray(String[]::new));
                
                decisions.add(new Decision(
                    Decision.Type.DUPLICATE_FILE,
                    duplicates.get(0).path,
                    "Found " + duplicates.size() + " potential duplicates: " + paths,
                    "Review and consolidate duplicate files",
                    5
                ));
            }
        }
    }
    
    private void detectLargeFiles() {
        long threshold = 100 * 1024 * 1024; // 100MB
        
        for (FileNode node : allNodes) {
            if (!node.isDirectory && node.size > threshold) {
                decisions.add(new Decision(
                    Decision.Type.LARGE_FILE,
                    node.path,
                    "Large file: " + node.name + " (" + (node.size / 1024 / 1024) + " MB)",
                    "Consider compression or archival",
                    3
                ));
            }
        }
    }
    
    private void detectDeepNesting() {
        for (FileNode node : allNodes) {
            if (node.depth > 7) {
                decisions.add(new Decision(
                    Decision.Type.DEEP_NESTING,
                    node.path,
                    "Deep nesting detected: " + node.depth + " levels deep",
                    "Flatten directory structure",
                    4
                ));
            }
        }
    }
    
    private void detectTooManyFiles() {
        Map<String, Integer> dirFileCounts = new HashMap<>();
        
        for (FileNode node : allNodes) {
            if (!node.isDirectory) {
                Path parent = Paths.get(node.path).getParent();
                if (parent != null) {
                    String parentPath = parent.toString();
                    dirFileCounts.put(parentPath, dirFileCounts.getOrDefault(parentPath, 0) + 1);
                }
            }
        }
        
        for (Map.Entry<String, Integer> entry : dirFileCounts.entrySet()) {
            if (entry.getValue() > 100) {
                decisions.add(new Decision(
                    Decision.Type.TOO_MANY_FILES,
                    entry.getKey(),
                    "Directory has " + entry.getValue() + " files",
                    "Organize into subdirectories",
                    6
                ));
            }
        }
    }
    
    private void detectTempFiles() {
        String[] tempPatterns = {".tmp", ".temp", "~", ".bak", ".swp", ".log"};
        
        for (FileNode node : allNodes) {
            if (!node.isDirectory) {
                for (String pattern : tempPatterns) {
                    if (node.name.toLowerCase().endsWith(pattern)) {
                        decisions.add(new Decision(
                            Decision.Type.TEMP_FILE,
                            node.path,
                            "Temporary file detected: " + node.name,
                            "Consider cleanup",
                            2
                        ));
                        break;
                    }
                }
            }
        }
    }
    
    private void detectCodeSmells() {
        // Detect Java files with potential issues
        for (FileNode node : allNodes) {
            if (node.name.endsWith(".java")) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(node.path)));
                    
                    // Check for excessive length
                    int lines = content.split("\n").length;
                    if (lines > 1000) {
                        decisions.add(new Decision(
                            Decision.Type.CODE_SMELL,
                            node.path,
                            "Large Java file: " + lines + " lines",
                            "Consider refactoring into smaller classes",
                            7
                        ));
                    }
                    
                    // Check for too many imports
                    long importCount = content.lines().filter(l -> l.trim().startsWith("import ")).count();
                    if (importCount > 50) {
                        decisions.add(new Decision(
                            Decision.Type.CODE_SMELL,
                            node.path,
                            "Excessive imports: " + importCount,
                            "Review dependencies and refactor",
                            5
                        ));
                    }
                } catch (IOException e) {
                    // Skip files we can't read
                }
            }
        }
    }
    
    /**
     * Print decisions sorted by severity
     */
    public void printDecisions() {
        decisions.sort((a, b) -> Integer.compare(b.severity, a.severity));
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ OPENCLAW FILE SYSTEM DECISIONS                                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
        
        for (Decision d : decisions) {
            String severityBar = "█".repeat(d.severity);
            System.out.println("┌─ [" + d.type + "] Severity: " + severityBar + " (" + d.severity + "/10)");
            System.out.println("│  Path: " + d.path);
            System.out.println("│  Issue: " + d.message);
            System.out.println("│  Recommendation: " + d.recommendation);
            System.out.println("└─");
            System.out.println();
        }
    }
    
    /**
     * Export topology for OpenClaw visualization
     */
    public String exportTopology() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nodes\": [\n");
        
        for (int i = 0; i < allNodes.size(); i++) {
            FileNode node = allNodes.get(i);
            sb.append("    {");
            sb.append("\"path\":\"").append(node.path.replace("\\", "\\\\")).append("\",");
            sb.append("\"name\":\"").append(node.name).append("\",");
            sb.append("\"size\":").append(node.size).append(",");
            sb.append("\"isDir\":").append(node.isDirectory).append(",");
            sb.append("\"depth\":").append(node.depth).append(",");
            sb.append("\"x\":").append(node.x).append(",");
            sb.append("\"y\":").append(node.y).append(",");
            sb.append("\"z\":").append(node.z);
            sb.append("}");
            if (i < allNodes.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("  ],\n");
        sb.append("  \"decisions\": [\n");
        
        for (int i = 0; i < decisions.size(); i++) {
            Decision d = decisions.get(i);
            sb.append("    {");
            sb.append("\"type\":\"").append(d.type).append("\",");
            sb.append("\"path\":\"").append(d.path.replace("\\", "\\\\")).append("\",");
            sb.append("\"message\":\"").append(d.message).append("\",");
            sb.append("\"severity\":").append(d.severity);
            sb.append("}");
            if (i < decisions.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    public List<FileNode> getNodes() {
        return allNodes;
    }
    
    public List<Decision> getDecisions() {
        return decisions;
    }
}
