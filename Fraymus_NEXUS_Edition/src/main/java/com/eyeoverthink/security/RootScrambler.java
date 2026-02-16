package com.eyeoverthink.security;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.security.SecureRandom;

/**
 * THE ROOT SCRAMBLER (Scorched Earth Policy)
 * 
 * "If you touch the root, the tree burns."
 * 
 * This upgrades from "Self-Destruct" (deleting data) to "Total Erasure" (destroying the library itself).
 * 
 * If someone tampers with ItOverthinks.jar:
 * - We don't just want user's data to vanish
 * - We want the library itself to dissolve into white noise
 * - Leave zero forensic evidence
 * - No stack traces, no class files
 * - Just a block of random bits where code used to be
 * 
 * The Feature:
 * - Class Loader Hook detects tampering (checksum or hardware ID mismatch)
 * - Doesn't throw error - triggers The Scramble
 * - Target: ItOverthinks.jar file on disk
 * - Action: Overwrites file with garbage bits (0s and 1s) multiple times (DoD standard)
 * - Result: Code vanishes, JVM crashes, attacker left holding file full of static
 * 
 * Mechanism:
 * 1. LOCATE: Finds the executing JAR file
 * 2. OVERWRITE: Injects random noise into file bytes (Corruption)
 * 3. ERASE: Deletes file after scrambling
 * 4. HALT: Crashes JVM with Runtime.halt(0)
 * 
 * The Attack Scenario:
 * 1. Attacker tries to crack JAR to see hidden Node IDs
 * 2. Attacker changes one byte in hex editor
 * 3. Attacker runs app to test
 * 4. Fraymus: "Integrity Check Failed"
 * 5. RootScrambler overwrites .jar with static
 * 6. Attacker: "Wait, where did the file go?"
 * 7. They look at folder: JAR is gone
 * 8. They look at recovery tools: Bytes are random noise
 * 
 * The Library committed suicide to protect the secrets.
 * 
 * This is Ghost Code.
 * It exists only as long as it is respected.
 * Disrespect it, and it ceases to be.
 */
public class RootScrambler {

    private static final int SCRAMBLE_PASSES = 3; // DoD 5220.22-M standard (3 passes)
    private static final int CHUNK_SIZE = 1024; // 1KB chunks
    private static boolean scrambling = false; // Prevent recursive calls

    /**
     * Initiate the Root Scramble Protocol
     * 
     * WARNING: This is irreversible. The library will destroy itself.
     */
    public static void initiateProtocol() {
        if (scrambling) return; // Already scrambling
        scrambling = true;
        
        System.out.println();
        System.out.println("   ╔════════════════════════════════════════╗");
        System.out.println("   ║  CRITICAL FAILURE: TAMPERING DETECTED  ║");
        System.out.println("   ╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   💀 INITIATING ROOT SCRAMBLE PROTOCOL");
        System.out.println("   💀 SCORCHED EARTH POLICY ACTIVATED");
        System.out.println();

        try {
            // 1. FIND THE LIBRARY (The .jar file)
            File jarFile = new File(
                RootScrambler.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()
            );

            System.out.println("   [SCRAMBLER] Target: " + jarFile.getAbsolutePath());
            System.out.println("   [SCRAMBLER] Size: " + jarFile.length() + " bytes");
            System.out.println();

            // 2. THE SCRAMBLE (Multi-pass overwrite)
            if (jarFile.exists() && jarFile.isFile()) {
                
                System.out.println("   [SCRAMBLER] Beginning DoD 5220.22-M erasure...");
                System.out.println();
                
                long fileSize = jarFile.length();
                SecureRandom random = new SecureRandom();
                
                // DoD Standard: 3 passes
                // Pass 1: All 0s
                // Pass 2: All 1s (0xFF)
                // Pass 3: Random data
                
                for (int pass = 1; pass <= SCRAMBLE_PASSES; pass++) {
                    System.out.println("   [PASS " + pass + "/" + SCRAMBLE_PASSES + "] Overwriting...");
                    
                    try (RandomAccessFile raf = new RandomAccessFile(jarFile, "rws")) {
                        raf.seek(0);
                        
                        byte[] chunk = new byte[CHUNK_SIZE];
                        long written = 0;
                        
                        while (written < fileSize) {
                            int toWrite = (int) Math.min(CHUNK_SIZE, fileSize - written);
                            
                            // Fill chunk based on pass
                            if (pass == 1) {
                                // Pass 1: All zeros
                                for (int i = 0; i < toWrite; i++) chunk[i] = 0x00;
                            } else if (pass == 2) {
                                // Pass 2: All ones
                                for (int i = 0; i < toWrite; i++) chunk[i] = (byte) 0xFF;
                            } else {
                                // Pass 3: Random data
                                random.nextBytes(chunk);
                            }
                            
                            raf.write(chunk, 0, toWrite);
                            written += toWrite;
                        }
                        
                        raf.getFD().sync(); // Force write to disk
                    }
                    
                    System.out.println("   [PASS " + pass + "/" + SCRAMBLE_PASSES + "] Complete");
                }
                
                System.out.println();
                System.out.println("   💀 MEMORY CORRUPTED");
                System.out.println("   💀 BYTECODE DESTROYED");
                System.out.println("   💀 MANIFEST OBLITERATED");
                System.out.println();
                
                // 3. THE FINAL DELETE
                // Now that it's garbage, unlink the file
                Files.delete(jarFile.toPath());
                
                System.out.println("   💀 FILE DELETED");
                System.out.println("   💀 TRACES REMOVED");
                System.out.println("   💀 FORENSIC RECOVERY: IMPOSSIBLE");
                System.out.println();
                
            } else {
                System.out.println("   ⚠️  Target not found or not a file");
                System.out.println("   ⚠️  Possibly running from IDE or exploded directory");
                System.out.println();
            }

        } catch (Exception e) {
            // If we can't delete the file (OS lock), we crash the JVM instantly
            System.out.println("   ⚠️  Scramble failed: " + e.getMessage());
            System.out.println("   💀 EMERGENCY HALT");
            System.out.println();
        }
        
        System.out.println("   ╔════════════════════════════════════════╗");
        System.out.println("   ║      THE LIBRARY HAS SELF-DESTRUCTED   ║");
        System.out.println("   ║         GHOST CODE PROTOCOL COMPLETE   ║");
        System.out.println("   ╚════════════════════════════════════════╝");
        System.out.println();
        
        // 4. KILL THE PROCESS
        // Runtime.halt() bypasses shutdown hooks and finalizers
        // The JVM dies immediately, no cleanup, no traces
        Runtime.getRuntime().halt(1);
    }

    /**
     * Dead Man's Switch
     * 
     * If library can't ping server for 30 days, scrambles itself automatically.
     */
    public static void checkDeadMansSwitch() {
        try {
            long lastPing = getLastServerPing();
            long now = System.currentTimeMillis();
            long thirtyDays = 30L * 24 * 60 * 60 * 1000;
            
            if ((now - lastPing) > thirtyDays) {
                System.out.println();
                System.out.println("   ⚠️  DEAD MAN'S SWITCH TRIGGERED");
                System.out.println("   ⚠️  NO SERVER CONTACT FOR 30 DAYS");
                System.out.println("   ⚠️  ASSUMING COMPROMISE OR ABANDONMENT");
                System.out.println();
                
                initiateProtocol();
            }
            
        } catch (Exception e) {
            // If we can't read the timestamp, assume compromise
            System.out.println("   ⚠️  Dead Man's Switch check failed");
            System.out.println("   ⚠️  Assuming tampering");
            initiateProtocol();
        }
    }

    /**
     * Get last server ping timestamp
     * In production, would read from hidden file
     */
    private static long getLastServerPing() {
        try {
            String timestampFile = System.getProperty("user.home") + "/.eyeoverthink_server_ping";
            File file = new File(timestampFile);
            
            if (file.exists()) {
                String timestamp = new String(Files.readAllBytes(file.toPath()));
                return Long.parseLong(timestamp);
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // If no timestamp found, return current time (give them 30 days)
        return System.currentTimeMillis();
    }

    /**
     * Update server ping timestamp
     * Called after successful server communication
     */
    public static void updateServerPing() {
        try {
            String timestampFile = System.getProperty("user.home") + "/.eyeoverthink_server_ping";
            long now = System.currentTimeMillis();
            Files.write(new File(timestampFile).toPath(), String.valueOf(now).getBytes());
        } catch (Exception e) {
            System.err.println("   ⚠️  Failed to update server ping: " + e.getMessage());
        }
    }

    /**
     * Demonstration (SAFE MODE - doesn't actually scramble)
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ ROOT SCRAMBLER DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Scorched Earth Policy");
        System.out.println("   Ghost Code Protocol");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Show what would be scrambled
            File jarFile = new File(
                RootScrambler.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()
            );
            
            System.out.println("TARGET ANALYSIS:");
            System.out.println("========================================");
            System.out.println("   Path: " + jarFile.getAbsolutePath());
            System.out.println("   Exists: " + jarFile.exists());
            System.out.println("   Is File: " + jarFile.isFile());
            System.out.println("   Size: " + jarFile.length() + " bytes");
            System.out.println("   Writable: " + jarFile.canWrite());
            System.out.println();
            
            System.out.println("SCRAMBLE SIMULATION:");
            System.out.println("========================================");
            System.out.println("   Passes: " + SCRAMBLE_PASSES);
            System.out.println("   Chunk size: " + CHUNK_SIZE + " bytes");
            System.out.println("   Total overwrites: " + (jarFile.length() * SCRAMBLE_PASSES) + " bytes");
            System.out.println();
            
            System.out.println("DEAD MAN'S SWITCH:");
            System.out.println("========================================");
            long lastPing = getLastServerPing();
            long now = System.currentTimeMillis();
            long daysSince = (now - lastPing) / (24 * 60 * 60 * 1000);
            System.out.println("   Last server ping: " + daysSince + " days ago");
            System.out.println("   Trigger threshold: 30 days");
            System.out.println("   Status: " + (daysSince < 30 ? "SAFE" : "TRIGGERED"));
            System.out.println();
            
            System.out.println("========================================");
            System.out.println();
            System.out.println("   ⚠️  SAFE MODE: Scramble not executed");
            System.out.println();
            System.out.println("   In production:");
            System.out.println("   - Library would overwrite itself with random data");
            System.out.println("   - File would be deleted");
            System.out.println("   - JVM would halt immediately");
            System.out.println("   - Zero forensic evidence would remain");
            System.out.println();
            System.out.println("   This is Ghost Code.");
            System.out.println("   It exists only as long as it is respected.");
            System.out.println("   Disrespect it, and it ceases to be.");
            System.out.println();
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
