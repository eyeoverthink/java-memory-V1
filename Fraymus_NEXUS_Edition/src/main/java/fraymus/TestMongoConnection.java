package fraymus;

import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;

/**
 * Test MongoDB Atlas connection
 */
public class TestMongoConnection {
    
    public static void main(String[] args) {
        System.out.println("=== Testing MongoDB Connection ===");
        
        MemoryConfig config = new MemoryConfig();
        String connectionString = config.getMongoConnectionString();
        String dbName = config.getMongoDatabaseName();
        
        System.out.println("Connection String: " + maskPassword(connectionString));
        System.out.println("Database: " + dbName);
        System.out.println();
        
        try {
            System.out.println("Attempting connection...");
            MongoMemoryBackend backend = new MongoMemoryBackend(connectionString, dbName);
            
            if (backend.isConnected()) {
                System.out.println("✓ Connection successful!");
                System.out.println();
                
                // Test basic operations
                System.out.println("Testing basic operations:");
                
                // Store a test record
                InfiniteMemory.MemoryRecord testRecord = new InfiniteMemory.MemoryRecord(
                    "TEST", 
                    "MongoDB connection test at " + System.currentTimeMillis(),
                    1.618,
                    "TestMongoConnection"
                );
                
                System.out.println("  Storing test record...");
                backend.storeRecord(testRecord);
                Thread.sleep(1000); // Wait for async write
                
                // Get record count
                long count = backend.getRecordCount();
                System.out.println("  ✓ Total records in database: " + count);
                
                // Test search
                System.out.println("  Testing search...");
                var results = backend.search("test");
                System.out.println("  ✓ Search returned " + results.size() + " results");
                
                // Test category query
                System.out.println("  Testing category query...");
                var testRecords = backend.getByCategory("TEST");
                System.out.println("  ✓ Found " + testRecords.size() + " TEST records");
                
                // Get category counts
                var categoryCounts = backend.getCategoryCounts();
                System.out.println("  ✓ Categories in database:");
                for (var entry : categoryCounts.entrySet()) {
                    System.out.println("    - " + entry.getKey() + ": " + entry.getValue());
                }
                
                System.out.println();
                System.out.println("=== All Tests Passed ===");
                
                backend.shutdown();
            } else {
                System.out.println("✗ Connection failed - not connected");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String maskPassword(String connectionString) {
        // Mask password in connection string for display
        return connectionString.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }
}
