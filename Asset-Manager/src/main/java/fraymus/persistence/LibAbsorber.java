package fraymus.persistence;

import fraymus.translation.Babel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Lib Absorber - Knowledge Persistence Layer
 * 
 * Absorbs transmuted content and persists it across three levels:
 * 1. Raw: Original acquisition (unedited)
 * 2. Normalized: Transmuted content (processed)
 * 3. Abstracted: Summarized and indexed (knowledge graph ready)
 * 
 * Pipeline position: Acquire (OpenClaw) → Transmute (Babel) → Absorb (Lib Absorber) → Index → Reason → Act → Reflect
 */
public class LibAbsorber {
    
    private String storageRoot;
    private boolean enableRawStorage;
    private boolean enableNormalizedStorage;
    private boolean enableAbstractedStorage;
    
    // Storage paths
    private String rawPath;
    private String normalizedPath;
    private String abstractedPath;
    private String indexPath;
    
    public LibAbsorber() {
        this.storageRoot = ".akashic"; // Default storage root
        this.enableRawStorage = true;
        this.enableNormalizedStorage = true;
        this.enableAbstractedStorage = true;
        
        initializePaths();
    }
    
    public LibAbsorber(String storageRoot) {
        this.storageRoot = storageRoot;
        this.enableRawStorage = true;
        this.enableNormalizedStorage = true;
        this.enableAbstractedStorage = true;
        
        initializePaths();
    }
    
    /**
     * Initialize storage directory structure
     */
    private void initializePaths() {
        Path root = Paths.get(storageRoot);
        
        try {
            // Create root directory if needed
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            
            // Create subdirectories
            this.rawPath = Paths.get(storageRoot, "raw").toString();
            this.normalizedPath = Paths.get(storageRoot, "normalized").toString();
            this.abstractedPath = Paths.get(storageRoot, "abstracted").toString();
            this.indexPath = Paths.get(storageRoot, "index").toString();
            
            Files.createDirectories(Paths.get(rawPath));
            Files.createDirectories(Paths.get(normalizedPath));
            Files.createDirectories(Paths.get(abstractedPath));
            Files.createDirectories(Paths.get(indexPath));
            
        } catch (IOException e) {
            System.err.println("Failed to initialize storage paths: " + e.getMessage());
        }
    }
    
    /**
     * Absorption result structure
     */
    public static class AbsorptionResult {
        public String id;
        public String source;
        public String timestamp;
        public Map<String, String> storagePaths;
        public Map<String, String> metadata;
        public String rawPath;
        public String normalizedPath;
        public String abstractedPath;
        public String indexPath;
        public boolean success;
        public String error;
        public int rawSize;
        public int normalizedSize;
        public int abstractedSize;
        
        public AbsorptionResult() {
            this.id = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.storagePaths = new HashMap<>();
            this.metadata = new HashMap<>();
        }
    }
    
    /**
     * Absorb transmuted content
     */
    public AbsorptionResult absorb(Babel.TransmutationResult transmutation) {
        AbsorptionResult result = new AbsorptionResult();
        result.source = transmutation.originalSource;
        
        try {
            // Store raw content (if enabled)
            if (enableRawStorage) {
                String rawFile = storeContent(
                    rawPath, 
                    result.id + "_raw.txt", 
                    transmutation.originalSource + "\n\n" + 
                    "Type: " + transmutation.originalType + "\n" +
                    "Timestamp: " + result.timestamp
                );
                result.rawPath = rawFile;
                result.rawSize = new File(rawFile).length() > 0 ? (int)new File(rawFile).length() : 0;
                result.storagePaths.put("raw", rawFile);
            }
            
            // Store normalized content (if enabled)
            if (enableNormalizedStorage && transmutation.normalizedContent != null) {
                String normalizedFile = storeContent(
                    normalizedPath, 
                    result.id + "_normalized.txt", 
                    transmutation.normalizedContent
                );
                result.normalizedPath = normalizedFile;
                result.normalizedSize = new File(normalizedFile).length() > 0 ? (int)new File(normalizedFile).length() : 0;
                result.storagePaths.put("normalized", normalizedFile);
            }
            
            // Store abstracted content (if enabled)
            if (enableAbstractedStorage && transmutation.normalizedContent != null) {
                String abstracted = generateAbstraction(transmutation);
                String abstractedFile = storeContent(
                    abstractedPath, 
                    result.id + "_abstracted.txt", 
                    abstracted
                );
                result.abstractedPath = abstractedFile;
                result.abstractedSize = new File(abstractedFile).length() > 0 ? (int)new File(abstractedFile).length() : 0;
                result.storagePaths.put("abstracted", abstractedFile);
            }
            
            // Create index entry
            String indexFile = createIndexEntry(result, transmutation);
            result.indexPath = indexFile;
            result.storagePaths.put("index", indexFile);
            
            // Add metadata
            result.metadata.put("word_count", String.valueOf(transmutation.wordCount));
            result.metadata.put("char_count", String.valueOf(transmutation.charCount));
            result.metadata.put("language", transmutation.languageDetected);
            result.metadata.put("type", transmutation.normalizedType);
            result.metadata.put("links_count", String.valueOf(transmutation.extractedLinks.size()));
            
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Generate abstraction (summary) from transmuted content
     */
    private String generateAbstraction(Babel.TransmutationResult transmutation) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== ABSTRACTION ===\n");
        sb.append("Source: ").append(transmutation.originalSource).append("\n");
        sb.append("Type: ").append(transmutation.normalizedType).append("\n");
        sb.append("Language: ").append(transmutation.languageDetected).append("\n");
        sb.append("Word Count: ").append(transmutation.wordCount).append("\n");
        sb.append("Char Count: ").append(transmutation.charCount).append("\n");
        sb.append("Links: ").append(transmutation.extractedLinks.size()).append("\n");
        
        if (transmutation.extractedLinks.size() > 0) {
            sb.append("\nLinks:\n");
            for (String link : transmutation.extractedLinks) {
                sb.append("  - ").append(link).append("\n");
            }
        }
        
        sb.append("\n=== SUMMARY ===\n");
        
        // Simple summary: first 500 characters
        String content = transmutation.normalizedContent != null ? transmutation.normalizedContent : "";
        int summaryLength = Math.min(500, content.length());
        if (summaryLength > 0) {
            sb.append(content.substring(0, summaryLength));
            if (content.length() > 500) {
                sb.append("...");
            }
        }
        
        sb.append("\n");
        
        return sb.toString();
    }
    
    /**
     * Create index entry
     */
    private String createIndexEntry(AbsorptionResult result, Babel.TransmutationResult transmutation) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("ID: ").append(result.id).append("\n");
        sb.append("Source: ").append(result.source).append("\n");
        sb.append("Timestamp: ").append(result.timestamp).append("\n");
        sb.append("Type: ").append(transmutation.normalizedType).append("\n");
        sb.append("Language: ").append(transmutation.languageDetected).append("\n");
        sb.append("Word Count: ").append(transmutation.wordCount).append("\n");
        sb.append("Char Count: ").append(transmutation.charCount).append("\n");
        sb.append("Links: ").append(transmutation.extractedLinks.size()).append("\n");
        
        if (transmutation.extractedLinks.size() > 0) {
            sb.append("Link List: ").append(transmutation.extractedLinks).append("\n");
        }
        
        sb.append("Raw Path: ").append(result.rawPath != null ? result.rawPath : "N/A").append("\n");
        sb.append("Normalized Path: ").append(result.normalizedPath != null ? result.normalizedPath : "N/A").append("\n");
        sb.append("Abstracted Path: ").append(result.abstractedPath != null ? result.abstractedPath : "N/A").append("\n");
        
        String indexContent = sb.toString();
        
        // Store index entry
        return storeContent(indexPath, result.id + "_index.txt", indexContent);
    }
    
    /**
     * Store content to file
     */
    private String storeContent(String directory, String filename, String content) {
        Path filePath = Paths.get(directory, filename);
        
        try {
            FileWriter writer = new FileWriter(filePath.toFile());
            writer.write(content);
            writer.close();
            
            return filePath.toString();
            
        } catch (IOException e) {
            System.err.println("Failed to store content: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Retrieve content by ID
     */
    public String retrieve(String id, String level) {
        String directory;
        
        switch (level.toLowerCase()) {
            case "raw":
                directory = rawPath;
                break;
            case "normalized":
                directory = normalizedPath;
                break;
            case "abstracted":
                directory = abstractedPath;
                break;
            case "index":
                directory = indexPath;
                break;
            default:
                return null;
        }
        
        Path filePath = Paths.get(directory, id + "_" + level + ".txt");
        
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * List all stored IDs
     */
    public List<String> listStoredIds() {
        List<String> ids = new ArrayList<>();
        
        try {
            File indexDir = new File(indexPath);
            File[] files = indexDir.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith("_index.txt")) {
                        String id = file.getName().replace("_index.txt", "");
                        ids.add(id);
                    }
                }
            }
        } catch (Exception e) {
            // Error listing files
        }
        
        return ids;
    }
    
    /**
     * Absorb multiple transmutations
     */
    public List<AbsorptionResult> absorbMultiple(List<Babel.TransmutationResult> transmutations) {
        List<AbsorptionResult> results = new ArrayList<>();
        
        for (Babel.TransmutationResult transmutation : transmutations) {
            results.add(absorb(transmutation));
        }
        
        return results;
    }
    
    /**
     * Set storage root
     */
    public void setStorageRoot(String root) {
        this.storageRoot = root;
        initializePaths();
    }
    
    /**
     * Enable/disable raw storage
     */
    public void setEnableRawStorage(boolean enable) {
        this.enableRawStorage = enable;
    }
    
    /**
     * Enable/disable normalized storage
     */
    public void setEnableNormalizedStorage(boolean enable) {
        this.enableNormalizedStorage = enable;
    }
    
    /**
     * Enable/disable abstracted storage
     */
    public void setEnableAbstractedStorage(boolean enable) {
        this.enableAbstractedStorage = enable;
    }
    
    /**
     * Get storage statistics
     */
    public Map<String, Integer> getStorageStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("raw_count", new File(rawPath).listFiles() != null ? new File(rawPath).listFiles().length : 0);
        stats.put("normalized_count", new File(normalizedPath).listFiles() != null ? new File(normalizedPath).listFiles().length : 0);
        stats.put("abstracted_count", new File(abstractedPath).listFiles() != null ? new File(abstractedPath).listFiles().length : 0);
        stats.put("index_count", new File(indexPath).listFiles() != null ? new File(indexPath).listFiles().length : 0);
        
        return stats;
    }
}
