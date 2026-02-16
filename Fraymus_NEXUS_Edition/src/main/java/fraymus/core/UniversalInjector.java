package fraymus.core;

import fraymus.PhiNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * THE UNIVERSAL INJECTOR
 * "Turns raw data into living entities."
 * 
 * This is the bridge between dead files and living warriors.
 * 
 * USAGE:
 * PhiNode cancer = UniversalInjector.inject(new File("cancer_cells.txt"));
 * PhiNode cure = UniversalInjector.inject(new File("crispr_sequence.txt"));
 * 
 * STAT CALCULATION:
 * - Entropy (Chaos) = Attack Power
 * - Complexity (Structure) = Intelligence/Defense
 * - Size (Mass) = Health Points
 * - Type (File Extension) = Entity Class
 * 
 * EXAMPLES:
 * - malware.exe → High entropy, low structure → High attack, low defense
 * - encryption.java → High structure, low entropy → High defense, low attack
 * - cancer.txt → Medium entropy, medium structure → Balanced stats
 * 
 * "Any data can become a warrior. Any problem can be simulated."
 */
public class UniversalInjector {

    /**
     * Inject any file into the arena as a living entity
     * 
     * @param targetFile The file to inject
     * @return PhiNode entity with stats based on file properties
     */
    public static PhiNode inject(File targetFile) {
        return inject(targetFile, null);
    }
    
    /**
     * Inject file with custom team assignment
     * 
     * @param targetFile The file to inject
     * @param team Team name (e.g., "RED_TEAM", "BLUE_TEAM")
     * @return PhiNode entity with stats and team tag
     */
    public static PhiNode inject(File targetFile, String team) {
        System.out.println("💉 INJECTING DATA: " + targetFile.getName());
        
        try {
            // 1. ABSORB THE DATA
            byte[] data = Files.readAllBytes(targetFile.toPath());
            String content = new String(data);
            
            // 2. GENERATE QUANTUM FINGERPRINT
            // This ensures every file has a unique "Soul"
            String fingerprint = generateFingerprint(content);
            
            // 3. CALCULATE STATS BASED ON DATA
            double complexity = calculateComplexity(content);
            double entropy = calculateEntropy(data);
            int size = data.length;
            
            System.out.println("   >> Complexity: " + String.format("%.2f", complexity));
            System.out.println("   >> Entropy: " + String.format("%.2f", entropy));
            System.out.println("   >> Size: " + size + " bytes");
            
            // 4. DETERMINE TYPE
            String name = targetFile.getName();
            String type = determineType(name);
            
            System.out.println("   >> Type: " + type);
            
            // 5. MANIFEST THE ENTITY
            PhiNode entity = new PhiNode(
                (float) (Math.random() * 100),
                (float) (Math.random() * 100)
            );
            
            // Set stats (normalized for game balance)
            entity.frequency = (float) (entropy * 50); // 0-400 Hz range
            entity.resonance = (float) complexity; // 0-1 range
            entity.fitness = (float) (size / 1000.0); // Scaled health
            entity.awareness = (float) complexity; // Intelligence
            entity.intent = (float) (entropy / 8.0); // Attack power (entropy max ~8)
            
            // Tag with metadata
            entity.setTag("name", name);
            entity.setTag("type", type);
            entity.setTag("fingerprint", fingerprint);
            if (team != null) {
                entity.setTag("team", team);
            }
            
            System.out.println("   ✅ ENTITY MANIFESTED: " + name);
            System.out.println("   📊 Frequency: " + String.format("%.2f", entity.frequency) + " Hz");
            System.out.println("   📊 Resonance: " + String.format("%.2f", entity.resonance));
            System.out.println("   📊 Fitness: " + String.format("%.2f", entity.fitness));
            System.out.println("   📊 Awareness: " + String.format("%.2f", entity.awareness));
            System.out.println("   📊 Intent: " + String.format("%.2f", entity.intent));
            System.out.println();
            
            return entity;
            
        } catch (IOException e) {
            System.err.println("   ❌ INJECTION FAILED: " + e.getMessage());
            
            // Return corrupted entity
            PhiNode glitch = new PhiNode(0, 0);
            glitch.setTag("name", "Glitch");
            glitch.setTag("type", "CORRUPTED");
            return glitch;
        }
    }
    
    /**
     * Calculate entropy (randomness) of data
     * Higher entropy = Higher chaos = Higher attack power
     * 
     * @param data Raw bytes
     * @return Entropy value (0.0 to 8.0)
     */
    private static double calculateEntropy(byte[] data) {
        if (data.length == 0) return 0.0;
        
        // Count frequency of each byte value
        int[] frequency = new int[256];
        for (byte b : data) {
            frequency[b & 0xFF]++;
        }
        
        // Calculate Shannon entropy
        double entropy = 0.0;
        for (int count : frequency) {
            if (count == 0) continue;
            double p = (double) count / data.length;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        
        return entropy; // Usually 0.0 to 8.0
    }
    
    /**
     * Calculate complexity (structure) of content
     * Higher complexity = More structure = Higher intelligence/defense
     * 
     * @param content Text content
     * @return Complexity value (0.0 to 1.0)
     */
    private static double calculateComplexity(String content) {
        if (content.isEmpty()) return 0.0;
        
        // Split into tokens (words/symbols)
        String[] tokens = content.split("\\s+");
        if (tokens.length == 0) return 0.0;
        
        // Count unique tokens
        long uniqueTokens = Arrays.stream(tokens).distinct().count();
        
        // Complexity = ratio of unique to total
        // High ratio = high structure/vocabulary
        double complexity = (double) uniqueTokens / tokens.length;
        
        return Math.min(1.0, complexity);
    }
    
    /**
     * Determine entity type based on file extension
     * 
     * @param filename File name
     * @return Entity type classification
     */
    private static String determineType(String filename) {
        String lower = filename.toLowerCase();
        
        // Malware/Executable
        if (lower.endsWith(".exe") || lower.endsWith(".sh") || 
            lower.endsWith(".bat") || lower.endsWith(".bin")) {
            return "MALWARE_CLASS";
        }
        
        // Code/Logic
        if (lower.endsWith(".java") || lower.endsWith(".py") || 
            lower.endsWith(".js") || lower.endsWith(".c") || 
            lower.endsWith(".cpp") || lower.endsWith(".rs")) {
            return "LOGIC_CLASS";
        }
        
        // Knowledge/Text
        if (lower.endsWith(".txt") || lower.endsWith(".pdf") || 
            lower.endsWith(".md") || lower.endsWith(".doc")) {
            return "KNOWLEDGE_CLASS";
        }
        
        // Data/Config
        if (lower.endsWith(".json") || lower.endsWith(".xml") || 
            lower.endsWith(".yaml") || lower.endsWith(".csv")) {
            return "DATA_CLASS";
        }
        
        // Web
        if (lower.endsWith(".html") || lower.endsWith(".css")) {
            return "WEB_CLASS";
        }
        
        // Archive
        if (lower.endsWith(".jar") || lower.endsWith(".zip") || 
            lower.endsWith(".tar") || lower.endsWith(".gz")) {
            return "ARCHIVE_CLASS";
        }
        
        return "UNKNOWN_ENTITY";
    }
    
    /**
     * Generate unique fingerprint for content
     * Simple hash-based approach
     * 
     * @param content Content to fingerprint
     * @return Hex fingerprint string
     */
    private static String generateFingerprint(String content) {
        int hash = content.hashCode();
        return Integer.toHexString(hash);
    }
    
    /**
     * Inject multiple files as a team
     * 
     * @param files Array of files
     * @param teamName Team name
     * @return Array of PhiNode entities
     */
    public static PhiNode[] injectTeam(File[] files, String teamName) {
        System.out.println("💉 INJECTING TEAM: " + teamName);
        System.out.println("   Files: " + files.length);
        System.out.println();
        
        PhiNode[] team = new PhiNode[files.length];
        
        for (int i = 0; i < files.length; i++) {
            team[i] = inject(files[i], teamName);
        }
        
        System.out.println("   ✅ TEAM MANIFESTED: " + teamName + " (" + files.length + " members)");
        System.out.println();
        
        return team;
    }
    
    /**
     * Show statistics about injected entity
     * 
     * @param entity PhiNode to analyze
     */
    public static void showStats(PhiNode entity) {
        System.out.println("📊 ENTITY STATS");
        System.out.println("   Name: " + entity.getTag("name"));
        System.out.println("   Type: " + entity.getTag("type"));
        System.out.println("   Fingerprint: " + entity.getTag("fingerprint"));
        System.out.println("   Team: " + entity.getTag("team"));
        System.out.println();
        System.out.println("   Frequency (Attack): " + String.format("%.2f", entity.frequency));
        System.out.println("   Resonance (Defense): " + String.format("%.2f", entity.resonance));
        System.out.println("   Fitness (Health): " + String.format("%.2f", entity.fitness));
        System.out.println("   Awareness (Intelligence): " + String.format("%.2f", entity.awareness));
        System.out.println("   Intent (Aggression): " + String.format("%.2f", entity.intent));
        System.out.println();
    }
}
