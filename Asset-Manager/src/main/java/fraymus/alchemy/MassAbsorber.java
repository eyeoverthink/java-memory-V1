package fraymus.alchemy;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * 🌪️ THE MASS ABSORBER - Gen 129
 * "Walks the earth, eating libraries."
 * 
 * Crawls directory trees, identifies source files in any language,
 * and feeds them to the Philosopher's Stone for transmutation.
 * 
 * Features:
 * - Parallel processing
 * - File filtering by extension
 * - Progress reporting
 * - Result aggregation
 */
public class MassAbsorber {

    private static final double PHI = 1.6180339887;
    
    private final PhilosophersStone stone;
    private final Set<String> targetExtensions;
    private final Set<String> excludePatterns;
    private final boolean parallel;
    private final int maxFiles;
    
    // Statistics
    private int filesProcessed = 0;
    private int filesSkipped = 0;
    private long startTime;
    private List<PhilosophersStone.TransmutationResult> results = new ArrayList<>();

    public MassAbsorber() {
        this(new PhilosophersStone());
    }
    
    public MassAbsorber(PhilosophersStone stone) {
        this.stone = stone;
        this.targetExtensions = new HashSet<>(Arrays.asList(
            ".py", ".go", ".cpp", ".c", ".rs", ".js", ".ts",
            ".rb", ".swift", ".kt", ".cs", ".php", ".lua"
        ));
        this.excludePatterns = new HashSet<>(Arrays.asList(
            "node_modules", "__pycache__", ".git", ".svn",
            "target", "build", "dist", "vendor", ".idea"
        ));
        this.parallel = false;
        this.maxFiles = Integer.MAX_VALUE;
    }
    
    private MassAbsorber(Builder builder) {
        this.stone = builder.stone != null ? builder.stone : new PhilosophersStone();
        this.targetExtensions = builder.extensions;
        this.excludePatterns = builder.excludes;
        this.parallel = builder.parallel;
        this.maxFiles = builder.maxFiles;
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUILDER
    // ═══════════════════════════════════════════════════════════════════

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private PhilosophersStone stone;
        private Set<String> extensions = new HashSet<>(Arrays.asList(
            ".py", ".go", ".cpp", ".c", ".rs", ".js", ".ts"
        ));
        private Set<String> excludes = new HashSet<>(Arrays.asList(
            "node_modules", "__pycache__", ".git", "target", "build"
        ));
        private boolean parallel = false;
        private int maxFiles = Integer.MAX_VALUE;
        
        public Builder stone(PhilosophersStone s) { this.stone = s; return this; }
        public Builder extensions(String... exts) { 
            this.extensions = new HashSet<>(Arrays.asList(exts)); 
            return this; 
        }
        public Builder exclude(String... patterns) { 
            this.excludes = new HashSet<>(Arrays.asList(patterns)); 
            return this; 
        }
        public Builder parallel(boolean p) { this.parallel = p; return this; }
        public Builder maxFiles(int max) { this.maxFiles = max; return this; }
        
        public MassAbsorber build() { return new MassAbsorber(this); }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ABSORPTION API
    // ═══════════════════════════════════════════════════════════════════

    /**
     * ABSORB DIRECTORY
     * Recursively process all matching files in the directory.
     */
    public AbsorptionReport absorb(String directoryPath) {
        return absorb(Paths.get(directoryPath));
    }
    
    public AbsorptionReport absorb(Path directory) {
        startTime = System.currentTimeMillis();
        results.clear();
        filesProcessed = 0;
        filesSkipped = 0;
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌪️ MASS ABSORBER INITIATED                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("   Target: " + directory.toAbsolutePath());
        System.out.println("   Extensions: " + targetExtensions);
        System.out.println("   Parallel: " + parallel);
        System.out.println();
        
        try {
            // Collect files
            List<Path> filesToProcess = Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(this::shouldProcess)
                .limit(maxFiles)
                .collect(Collectors.toList());
            
            System.out.println("   Found " + filesToProcess.size() + " files to absorb.\n");
            
            if (parallel) {
                processParallel(filesToProcess);
            } else {
                processSequential(filesToProcess);
            }
            
        } catch (IOException e) {
            System.err.println("   ⚠️ Error walking directory: " + e.getMessage());
        }
        
        return generateReport();
    }
    
    /**
     * ABSORB SINGLE FILE
     */
    public PhilosophersStone.TransmutationResult absorbFile(Path file) {
        PhilosophersStone.TransmutationResult result = stone.assimilate(file.toFile());
        results.add(result);
        filesProcessed++;
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // PROCESSING
    // ═══════════════════════════════════════════════════════════════════

    private void processSequential(List<Path> files) {
        int total = files.size();
        int current = 0;
        
        for (Path file : files) {
            current++;
            System.out.println("━━━ [" + current + "/" + total + "] ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            PhilosophersStone.TransmutationResult result = stone.assimilate(file.toFile());
            results.add(result);
            filesProcessed++;
        }
    }
    
    private void processParallel(List<Path> files) {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<PhilosophersStone.TransmutationResult>> futures = new ArrayList<>();
        
        for (Path file : files) {
            futures.add(executor.submit(() -> stone.assimilate(file.toFile())));
        }
        
        int completed = 0;
        for (Future<PhilosophersStone.TransmutationResult> future : futures) {
            try {
                PhilosophersStone.TransmutationResult result = future.get();
                results.add(result);
                filesProcessed++;
                completed++;
                
                System.out.println("   Progress: " + completed + "/" + files.size() + 
                    " (" + (result.success ? "✓" : "✗") + " " + result.sourceFile + ")");
                
            } catch (Exception e) {
                System.err.println("   ⚠️ Error: " + e.getMessage());
            }
        }
        
        executor.shutdown();
    }
    
    private boolean shouldProcess(Path path) {
        String fileName = path.getFileName().toString();
        String pathStr = path.toString();
        
        // Check exclusions
        for (String exclude : excludePatterns) {
            if (pathStr.contains(File.separator + exclude + File.separator) ||
                pathStr.contains("/" + exclude + "/")) {
                filesSkipped++;
                return false;
            }
        }
        
        // Check extensions
        for (String ext : targetExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        
        filesSkipped++;
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // REPORTING
    // ═══════════════════════════════════════════════════════════════════

    private AbsorptionReport generateReport() {
        long elapsed = System.currentTimeMillis() - startTime;
        
        AbsorptionReport report = new AbsorptionReport();
        report.totalFiles = filesProcessed + filesSkipped;
        report.processedFiles = filesProcessed;
        report.skippedFiles = filesSkipped;
        report.elapsedMs = elapsed;
        report.results = new ArrayList<>(results);
        
        // Count successes/failures
        for (var r : results) {
            if (r.success) report.successCount++;
            else report.failCount++;
        }
        
        // Group by language
        report.byLanguage = results.stream()
            .collect(Collectors.groupingBy(
                r -> r.sourceLanguage,
                Collectors.counting()
            ));
        
        // Print summary
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌪️ ABSORPTION COMPLETE                                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Files processed: %-43d ║%n", report.processedFiles);
        System.out.printf("║  Files skipped:   %-43d ║%n", report.skippedFiles);
        System.out.printf("║  Successful:      %-43d ║%n", report.successCount);
        System.out.printf("║  Failed:          %-43d ║%n", report.failCount);
        System.out.printf("║  Time elapsed:    %-43s ║%n", formatDuration(elapsed));
        System.out.printf("║  φ-Efficiency:    %-43.6f ║%n", report.getEfficiency() * PHI);
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║  BY LANGUAGE:                                                 ║");
        for (var entry : report.byLanguage.entrySet()) {
            System.out.printf("║    %-12s: %-45d ║%n", entry.getKey(), entry.getValue());
        }
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        return report;
    }
    
    private String formatDuration(long ms) {
        if (ms < 1000) return ms + "ms";
        if (ms < 60000) return String.format("%.1fs", ms / 1000.0);
        return String.format("%dm %ds", ms / 60000, (ms % 60000) / 1000);
    }

    // ═══════════════════════════════════════════════════════════════════
    // REPORT CLASS
    // ═══════════════════════════════════════════════════════════════════

    public static class AbsorptionReport {
        public int totalFiles;
        public int processedFiles;
        public int skippedFiles;
        public int successCount;
        public int failCount;
        public long elapsedMs;
        public Map<String, Long> byLanguage = new HashMap<>();
        public List<PhilosophersStone.TransmutationResult> results = new ArrayList<>();
        
        public double getSuccessRate() {
            return processedFiles > 0 ? (double) successCount / processedFiles : 0;
        }
        
        public double getEfficiency() {
            return getSuccessRate();
        }
        
        public List<PhilosophersStone.TransmutationResult> getSuccessful() {
            return results.stream().filter(r -> r.success).collect(Collectors.toList());
        }
        
        public List<PhilosophersStone.TransmutationResult> getFailed() {
            return results.stream().filter(r -> !r.success).collect(Collectors.toList());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (CLI)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        String targetDir = args.length > 0 ? args[0] : "./alien_libs";
        
        MassAbsorber absorber = MassAbsorber.builder()
            .extensions(".py", ".go", ".cpp", ".rs", ".js")
            .exclude("node_modules", "__pycache__", ".git", "test", "tests")
            .parallel(false)
            .maxFiles(100)
            .build();
        
        absorber.absorb(targetDir);
    }
}
