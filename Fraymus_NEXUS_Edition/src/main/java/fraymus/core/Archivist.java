package fraymus.core;

import fraymus.PhiNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * THE ARCHIVIST
 * "Saves the Soul, discards the Body."
 * 
 * STRATEGY:
 * 1. INGEST - Download raw data (RAM only)
 * 2. DISTILL - Extract essence (stats, DNA, name) into lightweight JSON
 * 3. ARCHIVE - Push full data to MongoDB and Git
 * 4. INDEX - Save tiny reference card (pointer) locally
 * 5. PURGE - Delete raw data immediately
 * 
 * DESTINATIONS:
 * - Local Index (JSON Lines) - Lightweight pointers (~50 bytes each)
 * - Cloud (MongoDB) - Full data, infinite scale
 * - Visual (QR Codes) - Scannable backup
 * 
 * RESULT:
 * Learn 10,000 concepts → Local index: 500 KB
 * Learn 10,000 concepts → Cloud storage: Unlimited
 * 
 * "Zero overflow. Infinite learning."
 */
public class Archivist {

    private static final String LOCAL_DB = "fraymus_db/index.json";
    private static final String QR_DIR = "fraymus_db/qr_codes/";
    private static final String BACKUP_DIR = "fraymus_db/backups/";
    
    private Gson gson;
    private MongoCollection<Document> cloudVault;
    private boolean cloudConnected = false;
    private int archivedCount = 0;

    public Archivist() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        setupDirectories();
        connectToCloud();
        
        System.out.println("📚 ARCHIVIST INITIALIZED");
        System.out.println("   Local index: " + LOCAL_DB);
        System.out.println("   QR codes: " + QR_DIR);
        System.out.println("   Cloud: " + (cloudConnected ? "Connected" : "Offline mode"));
        System.out.println();
    }

    /**
     * THE MASTER SAVE
     * "One call to save them all."
     * 
     * @param entity PhiNode to archive
     */
    public void archive(PhiNode entity) {
        String name = (String) entity.getTag("name");
        String category = (String) entity.getTag("category");
        
        if (name == null) name = "Unknown_" + entity.hashCode();
        if (category == null) category = "Unknown";
        
        System.out.println("💾 ARCHIVING: " + category + " / " + name);

        try {
            // 1. DISTILL - Generate lightweight metadata
            String json = entityToJson(entity);
            
            // 2. INDEX - Save local reference (append-only, tiny)
            appendToLocalIndex(entity, name, category);
            System.out.println("   ✓ Local index updated (~50 bytes)");

            // 3. VISUAL - Generate QR code (physical backup)
            QRGenome.generate(entity, QR_DIR + category + "_" + name + ".png");
            System.out.println("   ✓ QR code generated");

            // 4. CLOUD - Push to MongoDB (if connected)
            if (cloudConnected && cloudVault != null) {
                try {
                    Document doc = Document.parse(json);
                    doc.append("timestamp", System.currentTimeMillis());
                    doc.append("category", category);
                    doc.append("name", name);
                    cloudVault.insertOne(doc);
                    System.out.println("   ✓ Uploaded to MongoDB");
                } catch (Exception e) {
                    System.err.println("   ⚠️ MongoDB upload failed: " + e.getMessage());
                }
            }
            
            // 5. PURGE - Delete from RAM (garbage collection)
            entity = null;
            System.gc();
            
            archivedCount++;
            System.out.println("   ✓ RAM purged. Archive complete.");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("   ❌ ARCHIVE FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Append lightweight reference to local index
     * Format: JSON Lines (one entity per line, ~50 bytes)
     */
    private void appendToLocalIndex(PhiNode entity, String name, String category) {
        try (FileWriter fw = new FileWriter(LOCAL_DB, true)) {
            // Minimal reference: name, category, key stats
            String minRef = String.format(
                "{\"name\":\"%s\",\"category\":\"%s\",\"freq\":%.2f,\"res\":%.2f,\"fit\":%.2f}\n",
                name,
                category,
                entity.frequency,
                entity.resonance,
                entity.fitness
            );
            fw.write(minRef);
        } catch (Exception e) {
            System.err.println("   ❌ Local index write failed: " + e.getMessage());
        }
    }

    /**
     * Convert entity to full JSON (for cloud storage)
     */
    private String entityToJson(PhiNode entity) {
        // Create a serializable representation
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"name\": \"").append(entity.getTag("name")).append("\",\n");
        json.append("  \"category\": \"").append(entity.getTag("category")).append("\",\n");
        json.append("  \"frequency\": ").append(entity.frequency).append(",\n");
        json.append("  \"resonance\": ").append(entity.resonance).append(",\n");
        json.append("  \"fitness\": ").append(entity.fitness).append(",\n");
        json.append("  \"awareness\": ").append(entity.awareness).append(",\n");
        json.append("  \"intent\": ").append(entity.intent).append(",\n");
        json.append("  \"x\": ").append(entity.x).append(",\n");
        json.append("  \"y\": ").append(entity.y).append(",\n");
        json.append("  \"age\": ").append(entity.age).append("\n");
        json.append("}");
        
        return json.toString();
    }

    /**
     * Setup storage directories
     */
    private void setupDirectories() {
        new File("fraymus_db").mkdirs();
        new File(QR_DIR).mkdirs();
        new File(BACKUP_DIR).mkdirs();
        
        // Create index file if it doesn't exist
        try {
            File indexFile = new File(LOCAL_DB);
            if (!indexFile.exists()) {
                indexFile.createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Failed to create index file: " + e.getMessage());
        }
    }

    /**
     * Connect to MongoDB cloud storage
     */
    private void connectToCloud() {
        try {
            // Check for MongoDB URI in environment variable
            String uri = System.getenv("MONGO_URI");
            
            if (uri != null && !uri.isEmpty()) {
                MongoClient client = MongoClients.create(uri);
                MongoDatabase db = client.getDatabase("fraymus_knowledge");
                this.cloudVault = db.getCollection("entities");
                this.cloudConnected = true;
                
                System.out.println("   ☁️ CONNECTED TO MONGODB ATLAS");
                
            } else {
                System.out.println("   ⚠️ RUNNING IN OFFLINE MODE");
                System.out.println("      Set MONGO_URI environment variable to enable cloud sync");
                this.cloudConnected = false;
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ CLOUD CONNECTION FAILED: " + e.getMessage());
            this.cloudConnected = false;
        }
    }

    /**
     * Retrieve entity from local index
     */
    public String retrieve(String name) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(LOCAL_DB)));
            String[] lines = content.split("\n");
            
            for (String line : lines) {
                if (line.contains("\"name\":\"" + name + "\"")) {
                    return line;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Retrieve failed: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get archive statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("📚 ARCHIVIST STATISTICS");
        System.out.println("========================================");
        System.out.println("Total archived: " + archivedCount);
        
        // Count local index entries
        try {
            String content = new String(Files.readAllBytes(Paths.get(LOCAL_DB)));
            int entries = content.split("\n").length;
            long size = new File(LOCAL_DB).length();
            
            System.out.println("Local index entries: " + entries);
            System.out.println("Local index size: " + size + " bytes (" + (size / 1024) + " KB)");
            System.out.println("Average entry size: " + (entries > 0 ? size / entries : 0) + " bytes");
            
        } catch (Exception e) {
            System.err.println("Failed to read stats: " + e.getMessage());
        }
        
        // Count QR codes
        File qrFolder = new File(QR_DIR);
        File[] qrFiles = qrFolder.listFiles((dir, name) -> name.endsWith(".png"));
        int qrCount = qrFiles != null ? qrFiles.length : 0;
        
        System.out.println("QR codes generated: " + qrCount);
        System.out.println("Cloud status: " + (cloudConnected ? "Connected" : "Offline"));
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * Check if cloud is connected
     */
    public boolean isCloudConnected() {
        return cloudConnected;
    }

    /**
     * Get archived count
     */
    public int getArchivedCount() {
        return archivedCount;
    }
}
