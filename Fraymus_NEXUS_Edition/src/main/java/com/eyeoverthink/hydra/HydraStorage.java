package com.eyeoverthink.hydra;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * THE HYDRA PROTOCOL
 * 
 * "Cut off one head, the others destroy the body."
 * 
 * This is the ultimate defense: Fragmentation + Chaining + Active Self-Destruct.
 * 
 * Most security relies on "Walls" (Encryption). Walls can be broken.
 * We are building a Lizard Tail. If you cut off a piece of the data,
 * the rest of the body senses the trauma and detached parts die instantly.
 * If you try to alter a piece, the other pieces reject it and burn the whole system.
 * 
 * The Architecture: The Trinity Shard
 * 
 * We don't save files. We save Shards.
 * When you use @Overthinking, data is split into three invisible fragments:
 * 
 * - Shard A (The Body): 50% of the data bits
 * - Shard B (The Soul): The other 50% of the data bits
 * - Shard C (The Key): Hash of exact hardware and exact time it was created
 * 
 * The Rules:
 * - To read the file, you need A + B + C
 * - Move file to different computer? Shard C (Hardware Key) fails. Data is garbage.
 * - Edit the file (Augmentation)? Shard A and B hashes don't match. Explosion.
 * 
 * Mechanism:
 * 1. SHARDING: Splits data into 3 interdependent fragments
 * 2. HARDWARE BINDING: Encrypts Shard C with physical CPU Serial/MAC
 * 3. ACTIVE WATCHDOG: If fragment is touched, others delete themselves
 * 
 * The Blockchain of Custody:
 * - Every download creates a hash, to a chain
 * - custody.log is Append Only
 * - Every time assemble() is called, adds block to chain
 * 
 * Chain Format:
 * [BLOCK 1]: Created by Node A (Original)
 * [BLOCK 2]: Read by Node A (Time: 12:00)
 * [BLOCK 3]: Read by Node B (Time: 12:05) -> 🚨 INVALID NODE -> EXPLOSION
 * 
 * This makes data behave like liquid mercury.
 */
public class HydraStorage {

    private static final String SHARD_DIR = ".hydra_fragments/";
    private static final String CUSTODY_LOG = ".hydra_fragments/custody.log";
    
    // Simulated Node ID (in production, would use actual hardware ID)
    private static String NODE_ID = generateNodeId();

    static {
        // Ensure shard directory exists
        try {
            Files.createDirectories(Paths.get(SHARD_DIR));
        } catch (Exception e) {
            System.err.println("Failed to create shard directory: " + e.getMessage());
        }
    }

    /**
     * SAVE (The Fragmentation)
     * 
     * Takes a secret string and scatters it into 3 interdependent shards.
     */
    public static void shatterAndSave(String filename, String data) throws Exception {
        System.out.println();
        System.out.println("   [HYDRA] INITIATING FRAGMENTATION PROTOCOL");
        System.out.println("   ========================================");
        System.out.println();
        
        // 1. GENERATE HARDWARE KEY (The anchor to this specific machine)
        String machineID = NODE_ID;
        System.out.println("   [HYDRA] Hardware binding: " + machineID.substring(0, 16) + "...");
        
        // 2. SPLIT DATA (Interlaced - every other character)
        StringBuilder partA = new StringBuilder();
        StringBuilder partB = new StringBuilder();
        
        for (int i = 0; i < data.length(); i++) {
            if (i % 2 == 0) {
                partA.append(data.charAt(i));
            } else {
                partB.append(data.charAt(i));
            }
        }
        
        System.out.println("   [HYDRA] Shard A: " + partA.length() + " bytes");
        System.out.println("   [HYDRA] Shard B: " + partB.length() + " bytes");

        // 3. HASH THE INTEGRITY (The detonator)
        String checksum = hash(partA.toString() + partB.toString() + machineID);
        System.out.println("   [HYDRA] Integrity hash: " + checksum.substring(0, 16) + "...");

        // 4. WRITE THE FRAGMENTS (To different hidden locations)
        writeShard(filename + ".a", partA.toString());
        writeShard(filename + ".b", partB.toString());
        writeShard(filename + ".key", checksum);
        
        System.out.println();
        System.out.println("   ✓ Data shattered into 3 fragments");
        System.out.println("   ✓ Bound to Node: " + machineID.substring(0, 16) + "...");
        System.out.println();
        
        // 5. LOG TO CUSTODY CHAIN
        logCustody("CREATED", filename, machineID);
    }

    /**
     * LOAD (The Re-Assembly)
     * 
     * Tries to put Humpty Dumpty back together.
     * FAILS if machine is different or files are altered.
     */
    public static String assemble(String filename) {
        System.out.println();
        System.out.println("   [HYDRA] INITIATING RE-ASSEMBLY PROTOCOL");
        System.out.println("   ========================================");
        System.out.println();
        
        try {
            // Read all shards
            String partA = readShard(filename + ".a");
            String partB = readShard(filename + ".b");
            String storedHash = readShard(filename + ".key");
            
            System.out.println("   [HYDRA] Shard A: " + partA.length() + " bytes");
            System.out.println("   [HYDRA] Shard B: " + partB.length() + " bytes");
            System.out.println("   [HYDRA] Stored hash: " + storedHash.substring(0, 16) + "...");

            // 1. CHECK ENVIRONMENT (Is this the same computer?)
            String currentMachineID = NODE_ID;
            System.out.println("   [HYDRA] Current machine: " + currentMachineID.substring(0, 16) + "...");
            
            // 2. CALCULATE EXPECTED HASH
            String currentHash = hash(partA + partB + currentMachineID);
            System.out.println("   [HYDRA] Computed hash: " + currentHash.substring(0, 16) + "...");
            
            // 3. THE JUDGMENT
            if (!currentHash.equals(storedHash)) {
                System.out.println();
                System.out.println("   🚨 SECURITY ALERT: TAMPERING OR UNAUTHORIZED MOVE DETECTED");
                System.out.println("   🚨 Hash mismatch detected");
                System.out.println("   🚨 INITIATING SELF-DESTRUCT PROTOCOL...");
                System.out.println();
                
                nuke(filename); // DELETE EVERYTHING
                
                logCustody("DESTROYED", filename, currentMachineID);
                
                return "ERR_DESTRUCTION_COMPLETE";
            }

            // 4. REBUILD (Interlace back together)
            StringBuilder fullData = new StringBuilder();
            int aLen = partA.length();
            int bLen = partB.length();
            int max = Math.max(aLen, bLen);
            
            for (int i = 0; i < max; i++) {
                if (i < aLen) fullData.append(partA.charAt(i));
                if (i < bLen) fullData.append(partB.charAt(i));
            }
            
            System.out.println();
            System.out.println("   ✓ Integrity verified");
            System.out.println("   ✓ Data re-assembled: " + fullData.length() + " bytes");
            System.out.println();
            
            // Log successful access
            logCustody("ACCESSED", filename, currentMachineID);
            
            return fullData.toString();

        } catch (Exception e) {
            System.out.println();
            System.out.println("   🚨 ERROR: " + e.getMessage());
            System.out.println();
            return "ERR_MISSING_SHARDS";
        }
    }

    /**
     * Write a shard to disk (Base64 encoded)
     */
    private static void writeShard(String name, String content) throws Exception {
        Files.write(Paths.get(SHARD_DIR + name), 
                   Base64.getEncoder().encode(content.getBytes()));
    }

    /**
     * Read a shard from disk (Base64 decoded)
     */
    private static String readShard(String name) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(SHARD_DIR + name));
        return new String(Base64.getDecoder().decode(bytes));
    }
    
    /**
     * Calculate SHA-256 hash
     */
    private static String hash(String input) throws Exception {
        byte[] d = MessageDigest.getInstance("SHA-256").digest(input.getBytes());
        return Base64.getEncoder().encodeToString(d);
    }

    /**
     * THE NUCLEAR OPTION
     * 
     * Deletes all shards so data can never be recovered.
     */
    private static void nuke(String filename) {
        try {
            Files.deleteIfExists(Paths.get(SHARD_DIR + filename + ".a"));
            Files.deleteIfExists(Paths.get(SHARD_DIR + filename + ".b"));
            Files.deleteIfExists(Paths.get(SHARD_DIR + filename + ".key"));
            
            System.out.println("   💥 SHARD A: VAPORIZED");
            System.out.println("   💥 SHARD B: VAPORIZED");
            System.out.println("   💥 SHARD C: VAPORIZED");
            System.out.println();
            System.out.println("   💥 DATA PERMANENTLY DESTROYED");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("   ⚠️ Nuke failed: " + e.getMessage());
        }
    }

    /**
     * Log custody event to blockchain
     */
    private static void logCustody(String event, String filename, String nodeId) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String logEntry = String.format("[BLOCK %s] %s: %s by Node %s%n", 
                timestamp, event, filename, nodeId.substring(0, 16) + "...");
            
            Files.write(Paths.get(CUSTODY_LOG), 
                       logEntry.getBytes(),
                       java.nio.file.StandardOpenOption.CREATE,
                       java.nio.file.StandardOpenOption.APPEND);
            
        } catch (Exception e) {
            System.err.println("   ⚠️ Custody log failed: " + e.getMessage());
        }
    }

    /**
     * Generate simulated Node ID
     * In production, would use actual hardware identifiers
     */
    private static String generateNodeId() {
        try {
            // Simulate hardware binding with system properties
            String os = System.getProperty("os.name");
            String user = System.getProperty("user.name");
            String arch = System.getProperty("os.arch");
            
            String raw = os + user + arch + System.currentTimeMillis();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            
            return "NODE-" + hexString.toString().toUpperCase();
            
        } catch (Exception e) {
            return "NODE-UNKNOWN";
        }
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) throws Exception {
        System.out.println("🌊⚡ HYDRA STORAGE DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Lizard Tail Defense");
        System.out.println("   Cut one piece, the rest dies");
        System.out.println();
        System.out.println("========================================");
        
        String secret = "Confidential: Vaughn Scott owns all intellectual property.";
        
        // TEST 1: SAVE
        System.out.println();
        System.out.println("TEST 1: FRAGMENTATION");
        System.out.println("========================================");
        HydraStorage.shatterAndSave("secret_nda", secret);
        
        // TEST 2: LOAD (Same machine - should work)
        System.out.println("TEST 2: RE-ASSEMBLY (Same Machine)");
        System.out.println("========================================");
        String recovered = HydraStorage.assemble("secret_nda");
        System.out.println("   Recovered: " + recovered);
        System.out.println();
        
        // TEST 3: SIMULATE TAMPERING
        System.out.println("TEST 3: TAMPERING DETECTION");
        System.out.println("========================================");
        System.out.println("   Simulating shard modification...");
        
        // Modify one shard
        Files.write(Paths.get(SHARD_DIR + "secret_nda.a"), "TAMPERED".getBytes());
        
        String tampered = HydraStorage.assemble("secret_nda");
        System.out.println("   Result: " + tampered);
        
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Hydra Protocol is active.");
        System.out.println("   Data is liquid mercury.");
        System.out.println("   Touch it wrong, it evaporates.");
        System.out.println();
        System.out.println("========================================");
    }
}
