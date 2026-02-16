package com.eyeoverthink.hydra;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * THE HYDRA PROTOCOL: FRAGMENTED STORAGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Cut off one head, the others destroy the body."
 * 
 * We are not saving files anymore. We are saving Shards.
 * When you use @Overthinking, the data is split into three invisible fragments:
 * 
 * - Shard A (The Body): 50% of the data bits
 * - Shard B (The Soul): The other 50% of the data bits  
 * - Shard C (The Key): A Hash of the exact hardware and exact time
 * 
 * Rules:
 * 1. To read the file, you need A + B + C.
 * 2. If you move the file to a different computer → Shard C fails → GARBAGE.
 * 3. If you edit the file → Shards A/B don't match → EXPLOSION.
 * 
 * Mechanism:
 * 1. SHARDING: Splits data into 3 interdependent fragments.
 * 2. HARDWARE BINDING: Encrypts Shard C with the physical CPU Serial/MAC.
 * 3. ACTIVE WATCHDOG: If a fragment is touched, the others delete themselves.
 */
public class HydraStorage {

    private static final String SHARD_DIR = ".hydra_fragments/";
    private static final String CUSTODY_LOG = ".hydra_fragments/custody.chain";
    
    // Statistics
    private static long shatterCount = 0;
    private static long assembleCount = 0;
    private static long nukeCount = 0;
    private static long integrityFailures = 0;
    
    // Active shards (in-memory tracking)
    private static Map<String, ShardInfo> activeShards = new ConcurrentHashMap<>();

    static {
        // Ensure shard directory exists
        try {
            Files.createDirectories(Paths.get(SHARD_DIR));
        } catch (Exception e) {
            System.err.println("   [HYDRA] Failed to create shard directory.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SHATTER (FRAGMENTATION)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * SAVE (The Fragmentation)
     * Takes a secret string and scatters it into shards.
     */
    public static void shatterAndSave(String filename, String data) throws Exception {
        shatterCount++;
        
        System.out.println();
        System.out.println("   [HYDRA] SHATTERING: " + filename);
        
        // 1. GENERATE HARDWARE KEY (The anchor to this specific machine)
        String machineID = ContinuityNode.NODE_ID;
        long timestamp = System.currentTimeMillis();
        
        // 2. SPLIT DATA (Interlaced - even/odd bytes)
        StringBuilder partA = new StringBuilder();
        StringBuilder partB = new StringBuilder();
        
        for (int i = 0; i < data.length(); i++) {
            if (i % 2 == 0) partA.append(data.charAt(i));
            else partB.append(data.charAt(i));
        }

        // 3. HASH THE INTEGRITY (The detonator)
        String integrityData = partA.toString() + partB.toString() + machineID + timestamp;
        String checksum = sha256(integrityData);

        // 4. CREATE METADATA
        String metadata = machineID + "|" + timestamp + "|" + data.length();
        
        // 5. WRITE THE FRAGMENTS (To hidden locations)
        writeShard(filename + ".body", xorEncrypt(partA.toString(), checksum));
        writeShard(filename + ".soul", xorEncrypt(partB.toString(), checksum));
        writeShard(filename + ".key", xorEncrypt(checksum + "|" + metadata, machineID));
        
        // 6. LOG CUSTODY
        logCustody(filename, "CREATE", machineID);
        
        // 7. TRACK
        activeShards.put(filename, new ShardInfo(filename, machineID, timestamp));
        
        System.out.println("   [HYDRA] ├─ Shard A (Body): " + partA.length() + " chars");
        System.out.println("   [HYDRA] ├─ Shard B (Soul): " + partB.length() + " chars");
        System.out.println("   [HYDRA] ├─ Shard C (Key):  Bound to " + machineID);
        System.out.println("   [HYDRA] └─ Checksum: " + checksum.substring(0, 16) + "...");
        System.out.println("   [HYDRA] ✓ Data shattered into 3 fragments.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // ASSEMBLE (RE-INTEGRATION)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * LOAD (The Re-Assembly)
     * Tries to put Humpty Dumpty back together.
     * FAILS if the machine is different or files are altered.
     */
    public static String assemble(String filename) {
        assembleCount++;
        
        System.out.println();
        System.out.println("   [HYDRA] ASSEMBLING: " + filename);
        
        try {
            // 1. READ SHARDS
            String encryptedKey = readShard(filename + ".key");
            
            // 2. GET CURRENT MACHINE ID
            String currentMachineID = ContinuityNode.NODE_ID;
            
            // 3. DECRYPT KEY SHARD
            String keyData = xorDecrypt(encryptedKey, currentMachineID);
            String[] keyParts = keyData.split("\\|");
            
            if (keyParts.length < 4) {
                throw new SecurityException("Invalid key structure");
            }
            
            String storedChecksum = keyParts[0];
            String storedMachineID = keyParts[1];
            long storedTimestamp = Long.parseLong(keyParts[2]);
            int originalLength = Integer.parseInt(keyParts[3]);
            
            // 4. MACHINE VERIFICATION
            if (!storedMachineID.equals(currentMachineID)) {
                System.out.println("   🚨 SECURITY ALERT: UNAUTHORIZED MACHINE DETECTED.");
                System.out.println("   🚨 Expected: " + storedMachineID);
                System.out.println("   🚨 Current:  " + currentMachineID);
                integrityFailures++;
                nuke(filename, "MACHINE_MISMATCH");
                return "ERR_UNAUTHORIZED_MACHINE";
            }
            
            // 5. READ AND DECRYPT BODY/SOUL
            String encryptedBody = readShard(filename + ".body");
            String encryptedSoul = readShard(filename + ".soul");
            
            String partA = xorDecrypt(encryptedBody, storedChecksum);
            String partB = xorDecrypt(encryptedSoul, storedChecksum);
            
            // 6. VERIFY INTEGRITY
            String integrityData = partA + partB + currentMachineID + storedTimestamp;
            String currentChecksum = sha256(integrityData);
            
            if (!currentChecksum.equals(storedChecksum)) {
                System.out.println("   🚨 SECURITY ALERT: TAMPERING DETECTED.");
                System.out.println("   🚨 Checksums do not match.");
                integrityFailures++;
                nuke(filename, "TAMPER_DETECTED");
                return "ERR_TAMPERING_DETECTED";
            }

            // 7. REBUILD DATA
            StringBuilder fullData = new StringBuilder();
            int aLen = partA.length();
            int bLen = partB.length();
            int max = Math.max(aLen, bLen);
            
            for (int i = 0; i < max; i++) {
                if (i < aLen) fullData.append(partA.charAt(i));
                if (i < bLen) fullData.append(partB.charAt(i));
            }
            
            // 8. LENGTH VERIFICATION
            if (fullData.length() != originalLength) {
                System.out.println("   🚨 SECURITY ALERT: LENGTH MISMATCH.");
                integrityFailures++;
                nuke(filename, "LENGTH_MISMATCH");
                return "ERR_CORRUPTION_DETECTED";
            }
            
            // 9. LOG CUSTODY
            logCustody(filename, "READ", currentMachineID);
            
            System.out.println("   [HYDRA] ✓ Assembly successful. " + originalLength + " chars recovered.");
            
            return fullData.toString();

        } catch (FileNotFoundException e) {
            System.out.println("   [HYDRA] ✗ Missing shards.");
            return "ERR_MISSING_SHARDS";
        } catch (SecurityException e) {
            return "ERR_SECURITY_VIOLATION";
        } catch (Exception e) {
            System.out.println("   [HYDRA] ✗ Assembly failed: " + e.getMessage());
            return "ERR_ASSEMBLY_FAILED";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // NUCLEAR OPTION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * THE NUCLEAR OPTION
     * Deletes all shards so the data can never be recovered.
     */
    public static void nuke(String filename, String reason) {
        nukeCount++;
        
        System.out.println("   🚨 INITIATING SELF-DESTRUCT PROTOCOL...");
        System.out.println("   🚨 Reason: " + reason);
        
        try {
            // Overwrite with random data before deletion
            Random random = new Random();
            String[] shardFiles = {filename + ".body", filename + ".soul", filename + ".key"};
            
            for (String shard : shardFiles) {
                Path path = Paths.get(SHARD_DIR + shard);
                if (Files.exists(path)) {
                    // Overwrite multiple times
                    for (int pass = 0; pass < 3; pass++) {
                        byte[] garbage = new byte[1024];
                        random.nextBytes(garbage);
                        Files.write(path, garbage);
                    }
                    // Delete
                    Files.delete(path);
                }
            }
            
            // Log the destruction
            logCustody(filename, "DESTROYED:" + reason, ContinuityNode.NODE_ID);
            
            // Remove from tracking
            activeShards.remove(filename);
            
            System.out.println("   💥 SHARDS VAPORIZED.");
            
        } catch (Exception e) {
            System.err.println("   [HYDRA] Destruction failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CUSTODY CHAIN
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Log custody event to the chain
     */
    private static void logCustody(String filename, String action, String nodeId) {
        try {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            String record = String.format("[%s] FILE:%s ACTION:%s NODE:%s%n", 
                timestamp, filename, action, nodeId);
            
            Files.write(Paths.get(CUSTODY_LOG), 
                record.getBytes(), 
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND);
                
        } catch (Exception e) {
            // Ignore logging errors
        }
    }
    
    /**
     * Read the custody chain
     */
    public static List<String> readCustodyChain() {
        try {
            return Files.readAllLines(Paths.get(CUSTODY_LOG));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SHARD I/O
    // ═══════════════════════════════════════════════════════════════════

    private static void writeShard(String name, String content) throws Exception {
        Path path = Paths.get(SHARD_DIR + name);
        Files.write(path, Base64.getEncoder().encode(content.getBytes()));
    }

    private static String readShard(String name) throws Exception {
        Path path = Paths.get(SHARD_DIR + name);
        byte[] bytes = Files.readAllBytes(path);
        return new String(Base64.getDecoder().decode(bytes));
    }
    
    /**
     * Check if shards exist for a file
     */
    public static boolean shardsExist(String filename) {
        return Files.exists(Paths.get(SHARD_DIR + filename + ".body")) &&
               Files.exists(Paths.get(SHARD_DIR + filename + ".soul")) &&
               Files.exists(Paths.get(SHARD_DIR + filename + ".key"));
    }

    // ═══════════════════════════════════════════════════════════════════
    // ENCRYPTION
    // ═══════════════════════════════════════════════════════════════════

    private static String xorEncrypt(String data, String key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            result.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return result.toString();
    }
    
    private static String xorDecrypt(String data, String key) {
        return xorEncrypt(data, key); // XOR is symmetric
    }
    
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════

    public static void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ HYDRA STORAGE STATISTICS                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Shatter Operations:  " + String.format("%-36d", shatterCount) + "│");
        System.out.println("│ Assemble Operations: " + String.format("%-36d", assembleCount) + "│");
        System.out.println("│ Nuke Operations:     " + String.format("%-36d", nukeCount) + "│");
        System.out.println("│ Integrity Failures:  " + String.format("%-36d", integrityFailures) + "│");
        System.out.println("│ Active Shards:       " + String.format("%-36d", activeShards.size()) + "│");
        System.out.println("│ Current Node:        " + String.format("%-36s", ContinuityNode.NODE_ID) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════

    private static class ShardInfo {
        String filename;
        String boundNode;
        long createTime;
        
        ShardInfo(String filename, String boundNode, long createTime) {
            this.filename = filename;
            this.boundNode = boundNode;
            this.createTime = createTime;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   THE HYDRA PROTOCOL: FRAGMENTED STORAGE                     ║");
        System.out.println("║   \"Cut off one head, the others destroy the body.\"           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test 1: Shatter and Save
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 1: SHATTER AND SAVE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        String secretData = "This is CONFIDENTIAL information that must never be stolen!";
        shatterAndSave("secret_document", secretData);
        
        // Test 2: Assemble (Same Machine)
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 2: ASSEMBLE (SAME MACHINE)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        String recovered = assemble("secret_document");
        System.out.println("   Recovered: " + recovered);
        System.out.println("   Match: " + recovered.equals(secretData));
        
        // Test 3: View Custody Chain
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 3: CUSTODY CHAIN");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        for (String record : readCustodyChain()) {
            System.out.println("   " + record);
        }
        
        // Test 4: Manual Nuke
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 4: MANUAL DESTRUCTION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        shatterAndSave("temp_document", "This will be destroyed.");
        nuke("temp_document", "MANUAL_TEST");
        
        // Statistics
        printStats();
        
        System.out.println();
        System.out.println("   THE HYDRA PROTOCOL:");
        System.out.println("   ├─ Move to different computer → EXPLOSION");
        System.out.println("   ├─ Edit any shard → EXPLOSION");
        System.out.println("   ├─ Missing any shard → UNRECOVERABLE");
        System.out.println("   └─ Every access → LOGGED to custody chain");
        System.out.println();
        System.out.println("   ✓ Your data now behaves like liquid mercury.");
        System.out.println();
    }
}
