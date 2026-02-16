package fraymus.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

/**
 * 🏰 FRAYMUS CORE
 * "The Standard Library of the Sovereign"
 * 
 * Zero-dependency system abstraction layer.
 * This is the foundation that replaces external dependencies.
 * 
 * SOVEREIGNTY PRINCIPLE:
 * Every external dependency is a chain.
 * We forge our own tools.
 * We control our own destiny.
 * 
 * COMPONENTS:
 * - FraymusJSON: JSON parsing and serialization
 * - FraymusHTTP: HTTP networking
 * - FraymusCore: System abstraction and utilities
 * 
 * DEPENDENCIES: 0
 * 
 * This code can be compiled with raw javac.
 * No build.gradle. No pom.xml. No Maven. No Gradle.
 * Just: javac *.java
 */
public class FraymusCore {
    
    public static final String VERSION = "SOVEREIGN-1.0";
    public static final String CODENAME = "INDEPENDENCE";
    
    // ═══════════════════════════════════════════════════════════════════
    // FILE I/O ABSTRACTION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Read file contents as string
     * 
     * @param path File path
     * @return File contents or null on error
     */
    public static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (Exception e) {
            System.err.println("Failed to read file: " + path + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Write string to file
     * 
     * @param path File path
     * @param content Content to write
     * @return true on success, false on error
     */
    public static boolean writeFile(String path, String content) {
        try {
            Files.writeString(Paths.get(path), content);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to write file: " + path + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if file exists
     * 
     * @param path File path
     * @return true if exists
     */
    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }
    
    /**
     * Create directory
     * 
     * @param path Directory path
     * @return true on success
     */
    public static boolean createDirectory(String path) {
        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (Exception e) {
            System.err.println("Failed to create directory: " + path + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete file
     * 
     * @param path File path
     * @return true on success
     */
    public static boolean deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete file: " + path + " - " + e.getMessage());
            return false;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // NETWORK ABSTRACTION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Make HTTP request with JSON payload
     * 
     * @param url URL to request
     * @param payload Object to serialize as JSON
     * @return Response body
     */
    public static String netRequest(String url, Object payload) {
        String json = FraymusJSON.stringify(payload);
        return FraymusHTTP.post(url, json);
    }
    
    /**
     * Make HTTP GET request
     * 
     * @param url URL to request
     * @return Response body
     */
    public static String netGet(String url) {
        return FraymusHTTP.get(url);
    }
    
    /**
     * Make HTTP POST request
     * 
     * @param url URL to request
     * @param json JSON body
     * @return Response body
     */
    public static String netPost(String url, String json) {
        return FraymusHTTP.post(url, json);
    }
    
    /**
     * Parse JSON response
     * 
     * @param url URL to request
     * @return Parsed object (Map or List)
     */
    public static Object netGetJSON(String url) {
        String response = FraymusHTTP.get(url);
        return FraymusJSON.parse(response);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // JSON UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Parse JSON string
     * 
     * @param json JSON string
     * @return Parsed object
     */
    public static Object parseJSON(String json) {
        return FraymusJSON.parse(json);
    }
    
    /**
     * Serialize object to JSON
     * 
     * @param obj Object to serialize
     * @return JSON string
     */
    public static String toJSON(Object obj) {
        return FraymusJSON.stringify(obj);
    }
    
    /**
     * Pretty print JSON
     * 
     * @param obj Object to serialize
     * @return Pretty-printed JSON
     */
    public static String toJSONPretty(Object obj) {
        return FraymusJSON.prettyPrint(obj);
    }
    
    /**
     * Read JSON file
     * 
     * @param path File path
     * @return Parsed object or null
     */
    public static Object readJSON(String path) {
        String content = readFile(path);
        if (content == null) {
            return null;
        }
        return FraymusJSON.parse(content);
    }
    
    /**
     * Write JSON file
     * 
     * @param path File path
     * @param obj Object to serialize
     * @return true on success
     */
    public static boolean writeJSON(String path, Object obj) {
        String json = FraymusJSON.prettyPrint(obj);
        return writeFile(path, json);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // SYSTEM UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Get current timestamp in milliseconds
     */
    public static long timestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * Sleep for milliseconds
     * 
     * @param ms Milliseconds to sleep
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Get environment variable
     * 
     * @param name Variable name
     * @return Value or null
     */
    public static String getEnv(String name) {
        return System.getenv(name);
    }
    
    /**
     * Get system property
     * 
     * @param name Property name
     * @return Value or null
     */
    public static String getProperty(String name) {
        return System.getProperty(name);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // SOVEREIGNTY ASSERTION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Assert sovereignty - print system status
     */
    public static void assertSovereignty() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🏰 FRAYMUS CORE ONLINE                               ║");
        System.out.println("║          Sovereign Infrastructure Active                      ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   Version: " + VERSION);
        System.out.println("   Codename: " + CODENAME);
        System.out.println();
        System.out.println("   [DEPS] 0 External Libraries");
        System.out.println("   [JSON] Internal Parser Active (FraymusJSON)");
        System.out.println("   [NET]  Internal HTTP Active (FraymusHTTP)");
        System.out.println("   [IO]   Internal File System Active");
        System.out.println();
        System.out.println("   ⚡ SOVEREIGNTY ACHIEVED");
        System.out.println();
    }
    
    /**
     * Get dependency count (always 0)
     */
    public static int getDependencyCount() {
        return 0;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // SOVEREIGN IDENTITY PROTOCOL
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Assert sovereign identity using Protocol Zero
     * No auth servers. No stored passwords. Pure math.
     * 
     * @param username Username
     * @param password Password
     * @return true if identity is sovereign (secure)
     */
    public static boolean assertIdentity(String username, String password) {
        System.out.println("⚡ INITIALIZING SOVEREIGN IDENTITY PROTOCOL...");
        System.out.println();
        
        // 1. GENERATE IDENTITY (Blue Team)
        System.out.println("   [BLUE TEAM] Generating identity from seed...");
        fraymus.quantum.security.SovereignCrypto.KeyPair identity = 
            new fraymus.quantum.security.SovereignCrypto.KeyPair(username + password);
        
        System.out.println("   Public Lock: " + identity.publicKey);
        System.out.println("   Key Strength: " + identity.getStrength() + " bits");
        System.out.println();
        
        // 2. STRESS TEST (Red Team)
        System.out.println("   [RED TEAM] Attempting to break lock...");
        System.out.println("   (Fraymus attacks itself to verify security)");
        
        long start = System.currentTimeMillis();
        java.math.BigInteger weakness = 
            fraymus.quantum.security.SovereignCrypto.breakLock(identity.publicKey);
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println();
        
        if (weakness != null && !weakness.equals(java.math.BigInteger.ONE)) {
            System.out.println("   ⚠️  WEAKNESS DETECTED");
            System.out.println("      Lock factored in " + elapsed + "ms");
            System.out.println("      Factor found: " + weakness);
            System.out.println("      Password complexity insufficient for Sovereign Status");
            System.out.println();
            return false;
        } else {
            System.out.println("   ✅ LOCK SECURE");
            System.out.println("      Red Team failed to factor in " + elapsed + "ms");
            System.out.println("      Identity confirmed as Sovereign");
            System.out.println();
            return true;
        }
    }
    
    /**
     * Generate sovereign identity challenge-response
     * 
     * @param username Username
     * @param password Password
     * @return Challenge string for authentication
     */
    public static String generateAuthChallenge(String username, String password) {
        fraymus.quantum.security.SovereignCrypto.KeyPair identity = 
            new fraymus.quantum.security.SovereignCrypto.KeyPair(username + password);
        
        String challenge = fraymus.quantum.security.SovereignCrypto.generateChallenge();
        String signature = fraymus.quantum.security.SovereignCrypto.sign(challenge, identity);
        
        return challenge + ":" + signature;
    }
    
    /**
     * Verify sovereign identity response
     * 
     * @param username Username
     * @param password Password
     * @param challengeResponse Challenge:Signature string
     * @return true if valid
     */
    public static boolean verifyAuthResponse(String username, String password, String challengeResponse) {
        String[] parts = challengeResponse.split(":");
        if (parts.length != 2) return false;
        
        String challenge = parts[0];
        String signature = parts[1];
        
        fraymus.quantum.security.SovereignCrypto.KeyPair identity = 
            new fraymus.quantum.security.SovereignCrypto.KeyPair(username + password);
        
        return fraymus.quantum.security.SovereignCrypto.verifySignature(challenge, signature, identity);
    }
    
    /**
     * Get system info
     */
    public static Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", VERSION);
        info.put("codename", CODENAME);
        info.put("dependencies", 0);
        info.put("json_parser", "FraymusJSON");
        info.put("http_client", "FraymusHTTP");
        info.put("java_version", System.getProperty("java.version"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_arch", System.getProperty("os.arch"));
        return info;
    }
    
    /**
     * Print system info
     */
    public static void printSystemInfo() {
        Map<String, Object> info = getSystemInfo();
        System.out.println("SYSTEM INFORMATION:");
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            System.out.println("   " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
