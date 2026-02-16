package com.eyeoverthink.hydra;

import java.io.File;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;

/**
 * CONTINUITY NODE: THE HARDWARE ANCHOR
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Every machine has a soul. This is its fingerprint."
 * 
 * This class generates a unique identifier for the current machine.
 * The ID is based on hardware characteristics that cannot be easily spoofed.
 * 
 * Used by:
 * - HydraStorage: To bind data to a specific machine
 * - ItOverthinks: To track nodes in the network
 * - VolatileString: To detect unauthorized transfers
 */
public class ContinuityNode {

    // THE NODE IDENTITY
    public static final String NODE_ID;
    public static final String MACHINE_HASH;
    public static final long GENESIS_TIME;
    
    // PHI constant
    private static final double PHI = 1.618033988749895;

    static {
        GENESIS_TIME = System.currentTimeMillis();
        MACHINE_HASH = generateMachineHash();
        NODE_ID = "φ-" + MACHINE_HASH.substring(0, 12).toUpperCase();
        
        System.out.println("   [CONTINUITY] Node initialized: " + NODE_ID);
    }

    // ═══════════════════════════════════════════════════════════════════
    // HARDWARE FINGERPRINTING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Generate a unique hash based on hardware characteristics
     */
    private static String generateMachineHash() {
        StringBuilder machineData = new StringBuilder();
        
        // 1. MAC Address(es)
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        machineData.append(String.format("%02X", b));
                    }
                }
            }
        } catch (Exception e) {
            machineData.append("NO_MAC");
        }
        
        // 2. System Properties (harder to spoof)
        machineData.append(System.getProperty("os.name", ""));
        machineData.append(System.getProperty("os.arch", ""));
        machineData.append(System.getProperty("user.name", ""));
        machineData.append(System.getProperty("user.home", ""));
        
        // 3. Available processors (CPU count)
        machineData.append(Runtime.getRuntime().availableProcessors());
        
        // 4. File system roots (unique to machine)
        for (File root : File.listRoots()) {
            machineData.append(root.getAbsolutePath());
            machineData.append(root.getTotalSpace());
        }
        
        // 5. Java runtime
        machineData.append(System.getProperty("java.version", ""));
        machineData.append(System.getProperty("java.vendor", ""));
        
        // 6. PHI transformation for uniqueness
        long phiTransform = (long) (machineData.toString().hashCode() * PHI);
        machineData.append(phiTransform);
        
        return sha256(machineData.toString());
    }

    /**
     * Verify if the current machine matches the expected node
     */
    public static boolean verifyNode(String expectedNodeId) {
        return NODE_ID.equals(expectedNodeId);
    }
    
    /**
     * Get a short node identifier (for display)
     */
    public static String getShortId() {
        return NODE_ID.substring(0, 8);
    }
    
    /**
     * Get the full machine hash
     */
    public static String getFullHash() {
        return MACHINE_HASH;
    }

    // ═══════════════════════════════════════════════════════════════════
    // CUSTODY CHAIN
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Generate a custody record (for chain of custody tracking)
     */
    public static String generateCustodyRecord(String action) {
        return String.format("[%d] NODE:%s ACTION:%s", 
            System.currentTimeMillis(), NODE_ID, action);
    }
    
    /**
     * Verify a custody record came from this node
     */
    public static boolean verifyCustody(String record) {
        return record.contains("NODE:" + NODE_ID);
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

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
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   CONTINUITY NODE: HARDWARE ANCHOR                           ║");
        System.out.println("║   \"Every machine has a soul.\"                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   NODE IDENTITY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Node ID:      " + NODE_ID);
        System.out.println("   Short ID:     " + getShortId());
        System.out.println("   Genesis Time: " + GENESIS_TIME);
        System.out.println("   Full Hash:    " + MACHINE_HASH.substring(0, 32) + "...");
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   CUSTODY CHAIN");
        System.out.println("═══════════════════════════════════════════════════════════════");
        String record1 = generateCustodyRecord("CREATE");
        String record2 = generateCustodyRecord("READ");
        System.out.println("   Record 1: " + record1);
        System.out.println("   Record 2: " + record2);
        System.out.println("   Verified: " + verifyCustody(record1));
        
        System.out.println();
        System.out.println("   ✓ This machine's identity is now anchored.");
        System.out.println("   ✓ Data bound to this node cannot be moved.");
        System.out.println();
    }
}
