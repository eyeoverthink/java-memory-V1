package fraymus;

import java.nio.file.*;
import java.io.File;

public class SystemDiagnostics {
    
    public static void printWorkingDirectory() {
        String workingDir = System.getProperty("user.dir");
        CommandTerminal.printHighlight("=== SYSTEM DIAGNOSTICS ===");
        CommandTerminal.print("Working Directory: " + workingDir);
        
        // Check if attached_assets exists
        Path assetsPath = Paths.get("attached_assets");
        Path absoluteAssetsPath = assetsPath.toAbsolutePath();
        
        CommandTerminal.print("Looking for: " + absoluteAssetsPath);
        
        if (Files.exists(assetsPath)) {
            CommandTerminal.printSuccess("✓ attached_assets/ found!");
            try {
                long fileCount = Files.list(assetsPath).count();
                CommandTerminal.print("  Files: " + fileCount);
            } catch (Exception e) {
                CommandTerminal.printError("  Error counting files: " + e.getMessage());
            }
        } else {
            CommandTerminal.printError("✗ attached_assets/ NOT FOUND");
            CommandTerminal.print("  Expected at: " + absoluteAssetsPath);
            
            // Check parent directory
            Path parentAssets = Paths.get("..", "attached_assets");
            if (Files.exists(parentAssets)) {
                CommandTerminal.printWarning("  Found in parent directory: " + parentAssets.toAbsolutePath());
            }
            
            // Check common locations
            String[] checkPaths = {
                "D:\\Zip And Send\\Java-Memory\\Fraymus_Agent_BrainV2\\attached_assets",
                "./attached_assets",
                "../attached_assets"
            };
            
            CommandTerminal.print("  Checking common locations:");
            for (String checkPath : checkPaths) {
                Path p = Paths.get(checkPath);
                if (Files.exists(p)) {
                    CommandTerminal.printSuccess("    FOUND: " + p.toAbsolutePath());
                } else {
                    CommandTerminal.print("    Not found: " + checkPath);
                }
            }
        }
        
        // Check data directory
        Path dataPath = Paths.get("data");
        CommandTerminal.print("");
        CommandTerminal.print("Data Directory: " + dataPath.toAbsolutePath());
        if (Files.exists(dataPath)) {
            CommandTerminal.printSuccess("✓ data/ found");
        } else {
            CommandTerminal.printWarning("✗ data/ not found (will be created)");
        }
        
        // Check memory config
        Path configPath = Paths.get("data", "memory_config.properties");
        if (Files.exists(configPath)) {
            CommandTerminal.printSuccess("✓ Memory config exists");
        } else {
            CommandTerminal.printInfo("  Memory config will be created on first use");
        }
    }
    
    public static void printMemoryBackendStatus(MemoryConfig config) {
        CommandTerminal.printHighlight("=== MEMORY BACKEND STATUS ===");
        CommandTerminal.print("Backend Type: " + config.getBackendType());
        CommandTerminal.print("MongoDB Configured: " + (config.isMongoConfigured() ? "YES" : "NO"));
        CommandTerminal.print("QR Backup: " + (config.isQRBackupEnabled() ? "ENABLED" : "DISABLED"));
        
        if (config.isMongoConfigured()) {
            String connStr = config.getMongoConnectionString();
            // Mask password
            String masked = connStr.replaceAll(":[^@]+@", ":****@");
            CommandTerminal.print("MongoDB: " + masked);
            CommandTerminal.print("Database: " + config.getMongoDatabaseName());
        }
        
        CommandTerminal.print("");
        CommandTerminal.printInfo("To enable MongoDB:");
        CommandTerminal.print("  memory backend MONGODB");
        CommandTerminal.print("  memory backend HYBRID    (local + cloud)");
    }
}
