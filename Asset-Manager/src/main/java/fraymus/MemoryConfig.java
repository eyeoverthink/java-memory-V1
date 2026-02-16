package fraymus;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Configuration for InfiniteMemory persistence backend
 * Supports: STREAMING_LOG (local), MONGODB (cloud), or HYBRID (both)
 */
public class MemoryConfig {

    public enum BackendType {
        STREAMING_LOG,  // Local append-only log (default)
        MONGODB,        // Cloud MongoDB Atlas
        HYBRID          // Both (log for speed, MongoDB for backup)
    }

    private static final Path CONFIG_FILE = Paths.get("data", "memory_config.properties");
    
    private BackendType backendType = BackendType.STREAMING_LOG;
    private String mongoConnectionString = "mongodb+srv://eyeoverthink:wolverine@aigenerator.12uhq.mongodb.net/?appName=aiGenerator";
    private String mongoDatabaseName = "fraymus_memory";
    private boolean enableQRBackup = false;

    public MemoryConfig() {
        loadConfig();
    }

    private void loadConfig() {
        if (!Files.exists(CONFIG_FILE)) {
            saveConfig(); // Create default config
            return;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
            props.load(in);
            
            String backend = props.getProperty("backend.type", "STREAMING_LOG");
            try {
                this.backendType = BackendType.valueOf(backend);
            } catch (IllegalArgumentException e) {
                this.backendType = BackendType.STREAMING_LOG;
            }
            
            this.mongoConnectionString = props.getProperty("mongodb.connection", "");
            this.mongoDatabaseName = props.getProperty("mongodb.database", "fraymus_memory");
            this.enableQRBackup = Boolean.parseBoolean(props.getProperty("qr.backup.enabled", "false"));
            
            System.out.println("[MemoryConfig] Loaded: backend=" + backendType);
        } catch (IOException e) {
            System.err.println("[MemoryConfig] Load failed: " + e.getMessage());
        }
    }

    public void saveConfig() {
        Properties props = new Properties();
        props.setProperty("backend.type", backendType.name());
        props.setProperty("mongodb.connection", mongoConnectionString);
        props.setProperty("mongodb.database", mongoDatabaseName);
        props.setProperty("qr.backup.enabled", String.valueOf(enableQRBackup));
        
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
                props.store(out, "Fraymus Memory Configuration");
            }
            System.out.println("[MemoryConfig] Saved: backend=" + backendType);
        } catch (IOException e) {
            System.err.println("[MemoryConfig] Save failed: " + e.getMessage());
        }
    }

    // Getters and setters
    public BackendType getBackendType() { return backendType; }
    public void setBackendType(BackendType type) { 
        this.backendType = type;
        saveConfig();
    }

    public String getMongoConnectionString() { return mongoConnectionString; }
    public void setMongoConnectionString(String connectionString) { 
        this.mongoConnectionString = connectionString;
        saveConfig();
    }

    public String getMongoDatabaseName() { return mongoDatabaseName; }
    public void setMongoDatabaseName(String dbName) { 
        this.mongoDatabaseName = dbName;
        saveConfig();
    }

    public boolean isQRBackupEnabled() { return enableQRBackup; }
    public void setQRBackupEnabled(boolean enabled) { 
        this.enableQRBackup = enabled;
        saveConfig();
    }

    public boolean isMongoConfigured() {
        return mongoConnectionString != null && !mongoConnectionString.isEmpty();
    }
}
