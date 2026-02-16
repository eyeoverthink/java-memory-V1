package fraymus;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.time.Instant;
import java.util.*;

/**
 * MongoDB Persistence Layer
 * SAVE IMMEDIATELY → DESTROY MEMORY → PREVENT OVERFLOW
 */
public class MongoPersistence {
    
    private static final String DEFAULT_URI = "mongodb+srv://fraymus:fraymus123@aigenerator.12uhq.mongodb.net/?appName=aiGenerator";
    private static final String DB_NAME = "fraymus_brain";
    
    private MongoClient client;
    private MongoDatabase db;
    private boolean connected = false;
    
    // Collections
    private MongoCollection<Document> knowledgeCol;
    private MongoCollection<Document> genesisCol;
    private MongoCollection<Document> evolutionCol;
    private MongoCollection<Document> crashLogCol;
    
    private static MongoPersistence instance;
    
    public static MongoPersistence getInstance() {
        if (instance == null) {
            instance = new MongoPersistence();
        }
        return instance;
    }
    
    private MongoPersistence() {
        // Don't auto-connect - wait for explicit connect call
        connected = false;
    }
    
    /**
     * Connect to MongoDB using ServerApi (Atlas compatible)
     */
    public boolean connect(String uri) {
        try {
            if (client != null) {
                client.close();
            }
            
            // Use ServerApi for MongoDB Atlas compatibility
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .serverApi(serverApi)
                    .build();
            
            client = MongoClients.create(settings);
            db = client.getDatabase(DB_NAME);
            
            // Initialize collections
            knowledgeCol = db.getCollection("knowledge");
            genesisCol = db.getCollection("genesis_blocks");
            evolutionCol = db.getCollection("code_evolution");
            crashLogCol = db.getCollection("crash_log");
            
            // Test connection with ping
            db.runCommand(new Document("ping", 1));
            connected = true;
            System.out.println("[MongoPersistence] Connected to MongoDB Atlas!");
            return true;
            
        } catch (MongoException e) {
            System.err.println("[MongoPersistence] MongoDB error: " + e.getMessage());
            connected = false;
            return false;
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Failed to connect: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * SAVE KNOWLEDGE CHUNK IMMEDIATELY - then caller can destroy from memory
     */
    public void saveKnowledgeChunk(String content, double resonance, String source, 
                                    Map<String, String> meta) {
        if (!connected) return;
        
        try {
            Document doc = new Document()
                .append("content", content)
                .append("resonance", resonance)
                .append("source", source)
                .append("timestamp", Instant.now().toString())
                .append("meta", meta != null ? new Document(meta) : new Document());
            
            knowledgeCol.insertOne(doc);
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Save knowledge failed: " + e.getMessage());
        }
    }
    
    /**
     * BATCH SAVE - more efficient for scraping
     */
    public void saveKnowledgeBatch(List<Document> chunks) {
        if (!connected || chunks.isEmpty()) return;
        
        try {
            knowledgeCol.insertMany(chunks);
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Batch save failed: " + e.getMessage());
        }
    }
    
    /**
     * Save genesis block
     */
    public void saveGenesisBlock(int index, String eventType, String data, 
                                  String hash, String prevHash) {
        if (!connected) return;
        
        try {
            Document doc = new Document()
                .append("index", index)
                .append("eventType", eventType)
                .append("data", data)
                .append("hash", hash)
                .append("prevHash", prevHash)
                .append("timestamp", Instant.now().toString());
            
            // Upsert by index
            genesisCol.replaceOne(
                Filters.eq("index", index),
                doc,
                new com.mongodb.client.model.ReplaceOptions().upsert(true)
            );
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Save genesis block failed: " + e.getMessage());
        }
    }
    
    /**
     * Save code evolution event
     */
    public void saveEvolution(int generation, double phiIntegrity, String corticalRegion,
                               int patternsExtracted, String evolvedSource) {
        if (!connected) return;
        
        try {
            Document doc = new Document()
                .append("generation", generation)
                .append("phiIntegrity", phiIntegrity)
                .append("corticalRegion", corticalRegion)
                .append("patternsExtracted", patternsExtracted)
                .append("evolvedSource", evolvedSource.substring(0, Math.min(1000, evolvedSource.length())))
                .append("timestamp", Instant.now().toString());
            
            evolutionCol.insertOne(doc);
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Save evolution failed: " + e.getMessage());
        }
    }
    
    /**
     * Save memory record from InfiniteMemory
     */
    public void saveMemoryRecord(InfiniteMemory.MemoryRecord record) {
        if (!connected) return;
        
        try {
            Document doc = new Document()
                .append("id", record.id)
                .append("category", record.category)
                .append("content", record.content.substring(0, Math.min(5000, record.content.length())))
                .append("phiResonance", record.phiResonance)
                .append("entityName", record.entityName)
                .append("hash", record.hash)
                .append("timestamp", Instant.now().toString());
            
            if (record.metadata != null && !record.metadata.isEmpty()) {
                doc.append("metadata", new Document(record.metadata));
            }
            
            knowledgeCol.insertOne(doc);
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Save memory record failed: " + e.getMessage());
        }
    }
    
    /**
     * Save crash log
     */
    public void saveCrashLog(String source, String error, String stackTrace, long memoryMB) {
        if (!connected) return;
        
        try {
            Document doc = new Document()
                .append("source", source)
                .append("error", error)
                .append("stackTrace", stackTrace != null ? stackTrace.substring(0, Math.min(2000, stackTrace.length())) : "")
                .append("memoryMB", memoryMB)
                .append("timestamp", Instant.now().toString());
            
            crashLogCol.insertOne(doc);
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Save crash log failed: " + e.getMessage());
        }
    }
    
    /**
     * Query knowledge by topic
     */
    public List<String> queryKnowledge(String topic, int limit) {
        List<String> results = new ArrayList<>();
        if (!connected) return results;
        
        try {
            FindIterable<Document> docs = knowledgeCol.find(
                Filters.regex("content", topic, "i")
            ).limit(limit);
            
            for (Document doc : docs) {
                results.add(doc.getString("content"));
            }
        } catch (Exception e) {
            System.err.println("[MongoPersistence] Query failed: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get knowledge count
     */
    public long getKnowledgeCount() {
        if (!connected) return 0;
        try {
            return knowledgeCol.countDocuments();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get genesis block count
     */
    public long getGenesisBlockCount() {
        if (!connected) return 0;
        try {
            return genesisCol.countDocuments();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Close connection
     */
    public void close() {
        if (client != null) {
            client.close();
            connected = false;
        }
    }
    
    /**
     * Create Document helper for batch operations
     */
    public static Document createKnowledgeDoc(String content, double resonance, 
                                               String source, Map<String, String> meta) {
        return new Document()
            .append("content", content)
            .append("resonance", resonance)
            .append("source", source)
            .append("timestamp", Instant.now().toString())
            .append("meta", meta != null ? new Document(meta) : new Document());
    }
}
