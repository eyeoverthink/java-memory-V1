package fraymus;

import java.nio.file.*;

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
            
            // Check common locations
            String[] checkPaths = {
                "d:/Zip And Send/Java-Memory/Asset-Manager/attached_assets",
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
            CommandTerminal.printColored("✗ data/ not found (will be created)", 1.0f, 1.0f, 0.0f);
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
