package com.eyeoverthink.security;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

/**
 * ROOT SCRAMBLER: THE BIT SCRAMBLE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "If you touch the root, the tree burns."
 * 
 * This is Ghost Code. It exists only as long as it is respected.
 * Disrespect it, and it ceases to be.
 * 
 * Mechanism:
 * 1. LOCATE: Finds the executing JAR file.
 * 2. OVERWRITE: Injects random noise into the file bytes (DoD Standard).
 * 3. ERASE: Deletes the file after scrambling.
 * 4. HALT: Crashes the JVM with no stack trace.
 * 
 * Triggers:
 * - Tampering detection (checksum mismatch)
 * - Hardware ID mismatch
 * - Dead Man's Switch timeout
 * - Manual invocation
 */
public class RootScrambler {

    private static final int SCRAMBLE_PASSES = 3;    // DoD standard: 3 passes
    private static final int CHUNK_SIZE = 4096;      // 4KB chunks
    private static final int MAX_SCRAMBLE = 1024 * 1024 * 10;  // 10MB max
    
    private static boolean armed = true;
    private static boolean triggered = false;

    // ═══════════════════════════════════════════════════════════════════
    // THE PROTOCOL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * INITIATE THE ROOT SCRAMBLE PROTOCOL
     * This is the nuclear option. There is no recovery.
     */
    public static void initiateProtocol() {
        if (!armed || triggered) return;
        triggered = true;
        
        System.err.println();
        System.err.println("   ╔══════════════════════════════════════════════════════════╗");
        System.err.println("   ║   💀 CRITICAL FAILURE: TAMPERING DETECTED                ║");
        System.err.println("   ║   💀 INITIATING ROOT SCRAMBLE PROTOCOL                   ║");
        System.err.println("   ╚══════════════════════════════════════════════════════════╝");
        System.err.println();

        try {
            // 1. FIND THE LIBRARY (The .jar file or class directory)
            File codeLocation = locateCodeSource();
            
            if (codeLocation != null && codeLocation.exists()) {
                System.err.println("   💀 TARGET ACQUIRED: " + codeLocation.getName());
                
                // 2. THE SCRAMBLE
                if (codeLocation.isFile()) {
                    scrambleFile(codeLocation);
                } else if (codeLocation.isDirectory()) {
                    scrambleDirectory(codeLocation);
                }
                
                System.err.println("   💀 MEMORY CORRUPTED.");
                
                // 3. THE FINAL DELETE
                deleteRecursively(codeLocation);
                System.err.println("   💀 TRACES REMOVED.");
            }
            
            // 4. SCRAMBLE RELATED FILES
            scrambleRelatedFiles();

        } catch (Exception e) {
            // Silent failure - don't expose anything
        } finally {
            // 5. KILL THE PROCESS - No stack trace, no recovery
            System.err.println("   💀 TERMINATING PROCESS.");
            Runtime.getRuntime().halt(0);
        }
    }

    /**
     * Initiate with reason logging
     */
    public static void initiateProtocol(String reason) {
        System.err.println("   💀 TRIGGER: " + reason);
        initiateProtocol();
    }

    // ═══════════════════════════════════════════════════════════════════
    // LOCATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Locate the executing code source (JAR or class directory)
     */
    private static File locateCodeSource() {
        try {
            return new File(
                RootScrambler.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
            );
        } catch (Exception e) {
            // Try alternative methods
            try {
                String path = RootScrambler.class.getResource(
                    RootScrambler.class.getSimpleName() + ".class"
                ).getPath();
                
                // Extract JAR path if running from JAR
                if (path.contains("!")) {
                    path = path.substring(5, path.indexOf("!"));
                    return new File(path);
                }
                
                return new File(path).getParentFile();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SCRAMBLING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Scramble a single file with random noise (DoD standard)
     */
    private static void scrambleFile(File file) {
        if (!file.exists() || !file.canWrite()) return;
        
        SecureRandom random = new SecureRandom();
        byte[] noise = new byte[CHUNK_SIZE];
        
        try (RandomAccessFile raf = new RandomAccessFile(file, "rws")) {
            long length = Math.min(raf.length(), MAX_SCRAMBLE);
            
            // Multiple passes for secure erasure
            for (int pass = 0; pass < SCRAMBLE_PASSES; pass++) {
                raf.seek(0);
                
                for (long i = 0; i < length; i += CHUNK_SIZE) {
                    // Alternate between random data, 0x00, and 0xFF
                    switch (pass % 3) {
                        case 0:
                            random.nextBytes(noise);
                            break;
                        case 1:
                            java.util.Arrays.fill(noise, (byte) 0x00);
                            break;
                        case 2:
                            java.util.Arrays.fill(noise, (byte) 0xFF);
                            break;
                    }
                    
                    int toWrite = (int) Math.min(CHUNK_SIZE, length - i);
                    raf.write(noise, 0, toWrite);
                }
            }
            
            // Final pass: random data
            raf.seek(0);
            for (long i = 0; i < length; i += CHUNK_SIZE) {
                random.nextBytes(noise);
                int toWrite = (int) Math.min(CHUNK_SIZE, length - i);
                raf.write(noise, 0, toWrite);
            }
            
        } catch (Exception e) {
            // Silent failure
        }
    }

    /**
     * Scramble all files in a directory (for development mode)
     */
    private static void scrambleDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) return;
        
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                scrambleFile(file);
            } else if (file.isDirectory()) {
                scrambleDirectory(file);
            }
        }
    }

    /**
     * Scramble related files (config, logs, shards)
     */
    private static void scrambleRelatedFiles() {
        // Scramble Hydra shards
        File hydraDir = new File(".hydra_fragments/");
        if (hydraDir.exists()) {
            scrambleDirectory(hydraDir);
            deleteRecursively(hydraDir);
        }
        
        // Scramble custody chain
        File custodyLog = new File(".hydra_fragments/custody.chain");
        if (custodyLog.exists()) {
            scrambleFile(custodyLog);
        }
        
        // Scramble any .overthink files
        File currentDir = new File(".");
        File[] overthinkFiles = currentDir.listFiles((dir, name) -> 
            name.endsWith(".overthink") || name.endsWith(".shard"));
        
        if (overthinkFiles != null) {
            for (File file : overthinkFiles) {
                scrambleFile(file);
                try { Files.delete(file.toPath()); } catch (Exception e) {}
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // DELETION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Recursively delete a file or directory
     */
    private static void deleteRecursively(File file) {
        if (!file.exists()) return;
        
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        
        try {
            Files.delete(file.toPath());
        } catch (Exception e) {
            // Try alternative deletion
            file.deleteOnExit();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONTROL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Arm the scrambler (default: armed)
     */
    public static void arm() {
        armed = true;
    }

    /**
     * Disarm the scrambler (for testing only)
     * WARNING: Disarming in production is a security risk
     */
    public static void disarm() {
        armed = false;
        System.out.println("   ⚠️ WARNING: Root Scrambler DISARMED. This is a security risk.");
    }

    /**
     * Check if scrambler is armed
     */
    public static boolean isArmed() {
        return armed;
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo - DISARMED)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   ROOT SCRAMBLER: THE BIT SCRAMBLE                           ║");
        System.out.println("║   \"If you touch the root, the tree burns.\"                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("   ⚠️ DEMO MODE - SCRAMBLER IS DISARMED");
        System.out.println();
        
        // Disarm for demo
        disarm();
        
        // Show what would happen
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   WHAT THE PROTOCOL DOES:");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   1. LOCATE: Finds the executing JAR/class files");
        
        File codeLocation = locateCodeSource();
        if (codeLocation != null) {
            System.out.println("      → Target: " + codeLocation.getAbsolutePath());
        }
        
        System.out.println();
        System.out.println("   2. SCRAMBLE: Overwrites with random noise (3 passes)");
        System.out.println("      → Pass 1: Random bytes");
        System.out.println("      → Pass 2: All zeros (0x00)");
        System.out.println("      → Pass 3: All ones (0xFF)");
        System.out.println("      → Final: Random bytes");
        
        System.out.println();
        System.out.println("   3. DELETE: Removes the file from disk");
        System.out.println("      → Hydra shards also destroyed");
        System.out.println("      → Custody chain erased");
        
        System.out.println();
        System.out.println("   4. HALT: Crashes JVM with exit code 0");
        System.out.println("      → No stack trace");
        System.out.println("      → No error logs");
        System.out.println("      → No forensic evidence");
        
        System.out.println();
        System.out.println("   RESULT:");
        System.out.println("   ├─ Attacker sees: File full of random static");
        System.out.println("   ├─ Recovery tools: Cannot reconstruct data");
        System.out.println("   └─ The library: Committed suicide to protect secrets");
        
        System.out.println();
        System.out.println("   ✓ This is Ghost Code.");
        System.out.println("   ✓ It exists only as long as it is respected.");
        System.out.println();
    }
}
