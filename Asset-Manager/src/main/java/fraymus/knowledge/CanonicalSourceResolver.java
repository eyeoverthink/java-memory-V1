package fraymus.knowledge;

import java.nio.file.*;
import java.util.*;

/**
 * Canonical Source Resolver
 * 
 * Implements duplicate suppression rules for AkashicRecord absorption.
 * Prevents learning from stale/duplicate documentation sources.
 * 
 * Based on FRAYNIX_UNIVERSAL_LIBRARY/CANONICAL_DOC_GRAPH.md
 */
public class CanonicalSourceResolver {
    
    // Source priority levels
    private static final int PRIORITY_ROOT = 4;           // Maximum priority
    private static final int PRIORITY_NORTHSTAR = 3;      // High priority
    private static final int PRIORITY_ASSET_MANAGER = 3; // High priority
    private static final int PRIORITY_REPORTS = 2;       // Medium priority
    private static final int PRIORITY_BLOCKED = 0;        // Blocked
    
    // Blocked paths
    private static final Set<String> BLOCKED_PATHS = new HashSet<>(Arrays.asList(
        "reports/md-scan-2026/full-abstraction",
        "reports/md-scan-2026/full",
        "full-abstraction"
    ));
    
    // Canonical sources for key topics
    private static final Map<String, String> CANONICAL_SOURCES = new HashMap<>();
    
    static {
        // System architecture
        CANONICAL_SOURCES.put("FRAYNIX_TRUE_AGI_ROADMAP.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("FRAYNIX_SYSTEM_ATLAS.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("FRAYNIX_SYSTEM_CAPABILITIES.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("BRAIN_SYSTEMS_REFERENCE.md", "F:\\Full-AI-System\\");
        
        // Mathematical foundations
        CANONICAL_SOURCES.put("FRAYMUS_MATHEMATICAL_FOUNDATIONS.md", 
            "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\docs\\");
        CANONICAL_SOURCES.put("AI_EVOLUTION_MATH.md", 
            "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\docs\\");
        
        // Phase 5
        CANONICAL_SOURCES.put("FRAYNIX_PHASE5_IMPLEMENTATION_GUIDE.md", 
            "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\docs\\");
        
        // Phase completion
        CANONICAL_SOURCES.put("WEEK1_NERVOUS_SYSTEM_COMPLETE.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("WEEK2_SENSORY_EXPANSION_COMPLETE.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("WEEK3_MEMORY_SUBSTRATE_COMPLETE.md", "F:\\Full-AI-System\\");
        CANONICAL_SOURCES.put("WEEK4_INTEGRATION_OPTIMIZATION_COMPLETE.md", "F:\\Full-AI-System\\");
    }
    
    /**
     * Determine if a file should be absorbed by AkashicRecord
     */
    public static boolean shouldAbsorb(String sourcePath, String filename) {
        // Normalize path
        String normalizedPath = sourcePath.replace("/", "\\").toLowerCase();
        
        // Check for blocked paths
        for (String blocked : BLOCKED_PATHS) {
            if (normalizedPath.contains(blocked.toLowerCase())) {
                return false;
            }
        }
        
        // Check source priority
        int priority = getSourcePriority(sourcePath);
        if (priority == PRIORITY_BLOCKED) {
            return false;
        }
        
        // Check for canonical authority
        String canonicalSource = CANONICAL_SOURCES.get(filename);
        if (canonicalSource != null) {
            if (!sourcePath.toLowerCase().startsWith(canonicalSource.toLowerCase())) {
                return false; // Block duplicate from non-canonical source
            }
        }
        
        // Check filename collision
        if (filename.equalsIgnoreCase("README.md")) {
            // Only allow ROOT README
            if (!sourcePath.equalsIgnoreCase("F:\\Full-AI-System\\")) {
                return false;
            }
        }
        
        // Check for temporary/historical files
        if (filename.contains("~") || filename.contains(".bak") || 
            filename.contains(".tmp") || filename.contains(".old")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get source priority level
     */
    private static int getSourcePriority(String sourcePath) {
        String normalizedPath = sourcePath.toLowerCase();
        
        // ROOT - Maximum priority
        if (normalizedPath.startsWith("f:\\full-ai-system\\") && 
            !normalizedPath.contains("reports") &&
            !normalizedPath.contains("java-memory") &&
            !normalizedPath.contains(".northstar")) {
            return PRIORITY_ROOT;
        }
        
        // NORTHSTAR - High priority
        if (normalizedPath.contains(".northstar-kernel\\distilled")) {
            return PRIORITY_NORTHSTAR;
        }
        
        // ASSET-MANAGER - High priority
        if (normalizedPath.contains("java-memory\\asset-manager")) {
            return PRIORITY_ASSET_MANAGER;
        }
        
        // REPORTS - Medium priority
        if (normalizedPath.contains("reports")) {
            return PRIORITY_REPORTS;
        }
        
        // External - Low priority
        if (normalizedPath.contains("external")) {
            return PRIORITY_REPORTS - 1;
        }
        
        // Default: allow but low priority
        return 1;
    }
    
    /**
     * Get canonical source for a given filename
     */
    public static String getCanonicalSource(String filename) {
        return CANONICAL_SOURCES.get(filename);
    }
    
    /**
     * Check if a source is canonical for a given filename
     */
    public static boolean isCanonicalSource(String sourcePath, String filename) {
        String canonicalSource = CANONICAL_SOURCES.get(filename);
        if (canonicalSource == null) {
            return true; // No canonical defined, allow
        }
        
        return sourcePath.toLowerCase().startsWith(canonicalSource.toLowerCase());
    }
    
    /**
     * Get absorption reason (for logging)
     */
    public static String getAbsorptionReason(String sourcePath, String filename) {
        if (!shouldAbsorb(sourcePath, filename)) {
            // Check why blocked
            for (String blocked : BLOCKED_PATHS) {
                if (sourcePath.toLowerCase().contains(blocked.toLowerCase())) {
                    return "BLOCKED: Path contains blocked segment: " + blocked;
                }
            }
            
            String canonicalSource = CANONICAL_SOURCES.get(filename);
            if (canonicalSource != null && 
                !sourcePath.toLowerCase().startsWith(canonicalSource.toLowerCase())) {
                return "BLOCKED: Non-canonical source (canonical: " + canonicalSource + ")";
            }
            
            if (filename.equalsIgnoreCase("README.md") && 
                !sourcePath.equalsIgnoreCase("F:\\Full-AI-System\\")) {
                return "BLOCKED: Non-root README.md";
            }
            
            if (filename.contains("~") || filename.contains(".bak")) {
                return "BLOCKED: Temporary file";
            }
            
            return "BLOCKED: Low priority source";
        }
        
        return "ALLOWED: " + getSourcePriorityName(getSourcePriority(sourcePath));
    }
    
    private static String getSourcePriorityName(int priority) {
        switch (priority) {
            case PRIORITY_ROOT: return "ROOT (Maximum)";
            case PRIORITY_NORTHSTAR: return "HIGH (Northstar / Asset-Manager)";
            case PRIORITY_REPORTS: return "REPORTS (Medium)";
            case PRIORITY_BLOCKED: return "BLOCKED";
            default: return "UNKNOWN";
        }
    }
}
