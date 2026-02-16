package fraymus;

import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MongoDB backend for InfiniteMemory
 * Cloud-based persistence with automatic scaling
 */
public class MongoMemoryBackend {

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> recordsCollection;
    private final ExecutorService parallelExecutor;
    private boolean connected = false;

    public MongoMemoryBackend(String connectionString, String dbName) {
        this.parallelExecutor = Executors.newFixedThreadPool(4);
        
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .build();

            this.mongoClient = MongoClients.create(settings);
            this.database = mongoClient.getDatabase(dbName);
            this.recordsCollection = database.getCollection("memory_records");
            
            // Test connection
            database.runCommand(new Document("ping", 1));
            this.connected = true;
            
            // Create indexes for fast queries
            createIndexes();
            
            System.out.println("[MongoMemory] Connected to MongoDB Atlas successfully");
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Connection failed: " + e.getMessage());
            throw new RuntimeException("MongoDB connection failed", e);
        }
    }

    private void createIndexes() {
        // Index on category for fast category queries
        recordsCollection.createIndex(new Document("category", 1));
        
        // Index on timestamp for recent queries
        recordsCollection.createIndex(new Document("timestamp", -1));
        
        // Index on phiResonance for range queries
        recordsCollection.createIndex(new Document("phiResonance", 1));
        
        // Text index for full-text search
        recordsCollection.createIndex(new Document("content", "text"));
        
        System.out.println("[MongoMemory] Indexes created");
    }

    public void storeRecord(InfiniteMemory.MemoryRecord record) {
        if (!connected) return;
        
        Document doc = new Document()
                .append("_id", record.id)
                .append("timestamp", record.timestamp)
                .append("category", record.category)
                .append("content", record.content)
                .append("phiResonance", record.phiResonance)
                .append("entityName", record.entityName)
                .append("hash", record.hash)
                .append("metadata", new Document(record.metadata));
        
        parallelExecutor.submit(() -> {
            try {
                recordsCollection.insertOne(doc);
            } catch (MongoException e) {
                System.err.println("[MongoMemory] Insert failed: " + e.getMessage());
            }
        });
    }

    public List<InfiniteMemory.MemoryRecord> getByCategory(String category) {
        if (!connected) return Collections.emptyList();
        
        List<InfiniteMemory.MemoryRecord> results = Collections.synchronizedList(new ArrayList<>());
        
        try {
            FindIterable<Document> docs = recordsCollection.find(new Document("category", category));
            for (Document doc : docs) {
                results.add(documentToRecord(doc));
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Query failed: " + e.getMessage());
        }
        
        return results;
    }

    public List<InfiniteMemory.MemoryRecord> search(String query) {
        if (!connected) return Collections.emptyList();
        
        List<InfiniteMemory.MemoryRecord> results = Collections.synchronizedList(new ArrayList<>());
        
        try {
            // Text search
            FindIterable<Document> docs = recordsCollection.find(
                new Document("$text", new Document("$search", query))
            );
            
            for (Document doc : docs) {
                results.add(documentToRecord(doc));
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Search failed: " + e.getMessage());
        }
        
        return results;
    }

    public List<InfiniteMemory.MemoryRecord> getByResonanceRange(double minRes, double maxRes) {
        if (!connected) return Collections.emptyList();
        
        List<InfiniteMemory.MemoryRecord> results = Collections.synchronizedList(new ArrayList<>());
        
        try {
            Document query = new Document("phiResonance", 
                new Document("$gte", minRes).append("$lte", maxRes));
            
            FindIterable<Document> docs = recordsCollection.find(query);
            for (Document doc : docs) {
                results.add(documentToRecord(doc));
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Range query failed: " + e.getMessage());
        }
        
        return results;
    }

    public List<InfiniteMemory.MemoryRecord> getRecent(int count) {
        if (!connected) return Collections.emptyList();
        
        List<InfiniteMemory.MemoryRecord> results = new ArrayList<>();
        
        try {
            FindIterable<Document> docs = recordsCollection.find()
                .sort(new Document("timestamp", -1))
                .limit(count);
            
            for (Document doc : docs) {
                results.add(documentToRecord(doc));
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Recent query failed: " + e.getMessage());
        }
        
        return results;
    }

    public long getRecordCount() {
        if (!connected) return 0;
        try {
            return recordsCollection.countDocuments();
        } catch (MongoException e) {
            return 0;
        }
    }

    public Map<String, Integer> getCategoryCounts() {
        if (!connected) return Collections.emptyMap();
        
        Map<String, Integer> counts = new LinkedHashMap<>();
        
        try {
            // Aggregation pipeline to count by category
            List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$category")
                    .append("count", new Document("$sum", 1)))
            );
            
            AggregateIterable<Document> results = recordsCollection.aggregate(pipeline);
            for (Document doc : results) {
                String category = doc.getString("_id");
                Integer count = doc.getInteger("count");
                counts.put(category, count);
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Aggregation failed: " + e.getMessage());
        }
        
        return counts;
    }

    public double getAverageResonance() {
        if (!connected) return 0;
        
        try {
            List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", null)
                    .append("avgResonance", new Document("$avg", "$phiResonance")))
            );
            
            AggregateIterable<Document> results = recordsCollection.aggregate(pipeline);
            Document result = results.first();
            if (result != null) {
                return result.getDouble("avgResonance");
            }
        } catch (MongoException e) {
            System.err.println("[MongoMemory] Avg query failed: " + e.getMessage());
        }
        
        return 0;
    }

    private InfiniteMemory.MemoryRecord documentToRecord(Document doc) {
        Map<String, String> metadata = new HashMap<>();
        Document metaDoc = doc.get("metadata", Document.class);
        if (metaDoc != null) {
            for (String key : metaDoc.keySet()) {
                metadata.put(key, metaDoc.getString(key));
            }
        }
        
        return new InfiniteMemory.MemoryRecord(
            doc.getString("_id"),
            doc.getLong("timestamp"),
            doc.getString("category"),
            doc.getString("content"),
            doc.getDouble("phiResonance"),
            doc.getString("entityName"),
            doc.getString("hash"),
            metadata
        );
    }

    public void shutdown() {
        parallelExecutor.shutdown();
        if (mongoClient != null) {
            mongoClient.close();
        }
        System.out.println("[MongoMemory] Disconnected from MongoDB");
    }

    public boolean isConnected() {
        return connected;
    }
}
