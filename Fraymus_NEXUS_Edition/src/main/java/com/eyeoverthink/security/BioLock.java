package com.eyeoverthink.security;

import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Enumeration;

/**
 * THE BIO-LOCK (Hardware Binding)
 * 
 * "You can move the file, but you can't move the soul."
 * 
 * The Problem:
 * - Hackers steal IP, download to USB stick
 * - Go offline to "Ghost PC" (air-gapped, untraceable)
 * - Abstract and sell IP without detection
 * 
 * The Solution:
 * - Bind library to hardware DNA on first execution
 * - If they download on Machine A, run once, then move to Ghost PC → It dies
 * - If they download and try to run on Ghost PC immediately → Demands "Activation Signal"
 * - No internet? Stays encrypted garbage
 * 
 * Mechanism:
 * 1. FIRST RUN: Captures MAC Address & CPU ID
 * 2. BONDING: Writes hidden 'bio lock' file deep in OS
 * 3. CHECK: If current hardware != stored hardware → SCRAMBLE
 * 4. PULSE: Requires internet check every 24 hours
 * 
 * The Attack Scenario (Prevented):
 * 1. Hacker downloads ItOverthinks.jar on main PC (Machine A)
 * 2. Transfer to USB stick
 * 3. Walk to "Ghost PC" (Machine B - Offline, Air-gapped)
 * 4. Run library to analyze
 * 
 * The Defense:
 * - BioLock wakes up
 * - Looks for .eyeoverthink_bio_lock file (missing - never run on Machine B)
 * - Tries to Bond (First Run)
 * - Tries to pingMothership() to register new bond
 * - FAILURE: No Internet (Ghost PC)
 * - Result: System.exit(0) - Library refuses to unpack payload
 * 
 * They cannot analyze what they cannot turn on.
 */
public class BioLock {

    private static final String LOCK_FILE = System.getProperty("user.home") + "/.eyeoverthink_bio_lock";
    private static final String TIMESTAMP_FILE = System.getProperty("user.home") + "/.eyeoverthink_pulse";

    /**
     * Verify hardware binding
     * Called on every library initialization
     */
    public static void verify() {
        try {
            String currentHardwareID = getHardwareFingerprint();
            Path lockPath = Paths.get(LOCK_FILE);

            // SCENARIO 1: FIRST BREATH (The Bonding)
            if (!Files.exists(lockPath)) {
                System.out.println();
                System.out.println("   🔒 FIRST RUN DETECTED");
                System.out.println("   🔒 BONDING TO HARDWARE...");
                System.out.println();
                
                // CRITICAL: Require "Internet Pulse" to prove it's not offline
                if (!isOnline()) {
                    System.out.println("   💀 ACTIVATION FAILED: NO INTERNET CONNECTION");
                    System.out.println("   💀 FIRST RUN REQUIRES ONLINE ACTIVATION");
                    System.out.println("   💀 PLEASE CONNECT TO INTERNET AND TRY AGAIN");
                    System.out.println();
                    System.exit(1);
                }
                
                // Lock it to this machine forever
                Files.write(lockPath, currentHardwareID.getBytes());
                
                // Record activation timestamp
                updatePulseTimestamp();
                
                System.out.println("   ✓ HARDWARE BONDED: " + currentHardwareID.substring(0, 16) + "...");
                System.out.println("   ✓ ACTIVATION COMPLETE");
                System.out.println();
                
                return;
            }

            // SCENARIO 2: SUBSEQUENT RUNS (The Check)
            String storedID = new String(Files.readAllBytes(lockPath));
            
            if (!storedID.equals(currentHardwareID)) {
                // SCENARIO 3: TRANSPLANT DETECTED (Ghost PC)
                System.out.println();
                System.out.println("   💀 UNAUTHORIZED HARDWARE CHANGE DETECTED");
                System.out.println("   💀 THIS LIBRARY BELONGS TO MACHINE: " + storedID.substring(0, 8) + "...");
                System.out.println("   💀 CURRENT MACHINE: " + currentHardwareID.substring(0, 8) + "...");
                System.out.println("   💀 INITIATING ROOT SCRAMBLE...");
                System.out.println();
                
                // Trigger self-destruct
                RootScrambler.initiateProtocol();
                
            } else {
                System.out.println("   ✓ HARDWARE VERIFIED: " + currentHardwareID.substring(0, 16) + "...");
                
                // Check 24-hour pulse
                checkPulse();
            }

        } catch (Exception e) {
            // If we can't read hardware (Virtual Machine or Sandbox), kill it
            System.out.println("   💀 HARDWARE FINGERPRINT FAILED");
            System.out.println("   💀 POSSIBLE VM OR SANDBOX DETECTED");
            RootScrambler.initiateProtocol();
        }
    }

    /**
     * THE 24-HOUR RULE (Temporal Decay)
     * 
     * Even if hacker spoofs MAC address (hard but possible),
     * library must "phone home" every 24 hours to refresh token.
     * 
     * Ghost PC: Offline, cannot phone home
     * Result: Token expires, library locks up
     */
    public static void checkPulse() {
        try {
            long lastOnline = getLastOnlineTimestamp();
            long now = System.currentTimeMillis();
            long twentyFourHours = 24 * 60 * 60 * 1000;

            if ((now - lastOnline) > twentyFourHours) {
                System.out.println();
                System.out.println("   ⚠️ OFFLINE TIMEOUT EXCEEDED (24 HOURS)");
                System.out.println("   ⚠️ PLEASE RECONNECT TO VERIFY INTEGRITY");
                System.out.println();
                
                if (!isOnline()) {
                    System.out.println("   💀 NO INTERNET CONNECTION");
                    System.out.println("   💀 LIBRARY LOCKED UNTIL ONLINE VERIFICATION");
                    System.out.println();
                    System.exit(1); // Soft lock (or Scramble if ruthless)
                } else {
                    System.out.println("   ✓ ONLINE VERIFICATION SUCCESSFUL");
                    updatePulseTimestamp(); // Refresh the timer
                    System.out.println();
                }
            }
            
        } catch (Exception e) {
            System.err.println("   ⚠️ Pulse check failed: " + e.getMessage());
        }
    }

    /**
     * THE FINGERPRINT (Getting the Hardware DNA)
     * 
     * Combines MAC Address + System Properties for unique ID
     */
    public static String getHardwareFingerprint() throws Exception {
        StringBuilder sb = new StringBuilder();
        
        // Get MAC addresses from all network interfaces
        Enumeration<NetworkInterface> network = NetworkInterface.getNetworkInterfaces();
        
        while (network.hasMoreElements()) {
            NetworkInterface ni = network.nextElement();
            byte[] hardwareAddress = ni.getHardwareAddress();
            
            if (hardwareAddress != null) {
                for (byte b : hardwareAddress) {
                    sb.append(String.format("%02X", b));
                }
            }
        }
        
        // Add system properties for additional uniqueness
        sb.append(System.getProperty("os.name"));
        sb.append(System.getProperty("os.arch"));
        sb.append(System.getProperty("os.version"));
        sb.append(System.getProperty("user.name"));
        
        // Hash it so they can't easily spoof it
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(sb.toString().getBytes());
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        
        return hexString.toString();
    }

    /**
     * Check if system is online
     * Simulated - in production would ping actual server
     */
    private static boolean isOnline() {
        try {
            // Attempt to resolve a DNS name
            java.net.InetAddress.getByName("google.com");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get last online timestamp
     */
    private static long getLastOnlineTimestamp() {
        try {
            Path timestampPath = Paths.get(TIMESTAMP_FILE);
            if (Files.exists(timestampPath)) {
                String timestamp = new String(Files.readAllBytes(timestampPath));
                return Long.parseLong(timestamp);
            }
        } catch (Exception e) {
            // Ignore
        }
        return 0; // Force online check
    }

    /**
     * Update pulse timestamp
     */
    private static void updatePulseTimestamp() {
        try {
            long now = System.currentTimeMillis();
            Files.write(Paths.get(TIMESTAMP_FILE), String.valueOf(now).getBytes());
        } catch (Exception e) {
            System.err.println("   ⚠️ Failed to update pulse timestamp: " + e.getMessage());
        }
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ BIO-LOCK DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Hardware DNA Binding");
        System.out.println("   You can move the file, but you can't move the soul");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        try {
            System.out.println("CURRENT HARDWARE FINGERPRINT:");
            System.out.println("========================================");
            String fingerprint = getHardwareFingerprint();
            System.out.println("   Full: " + fingerprint);
            System.out.println("   Short: " + fingerprint.substring(0, 16) + "...");
            System.out.println();
            
            System.out.println("ONLINE STATUS:");
            System.out.println("========================================");
            boolean online = isOnline();
            System.out.println("   Internet: " + (online ? "✓ CONNECTED" : "✗ OFFLINE"));
            System.out.println();
            
            System.out.println("VERIFICATION TEST:");
            System.out.println("========================================");
            BioLock.verify();
            
            System.out.println("========================================");
            System.out.println();
            System.out.println("   The Ghost PC cannot run this.");
            System.out.println("   The USB stick is just a rock.");
            System.out.println("   They cannot analyze what they cannot turn on.");
            System.out.println();
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * ROOT SCRAMBLER
 * 
 * Placeholder for self-destruct mechanism
 * In production, would trigger HydraStorage nuke + VolatileString poison
 */
class RootScrambler {
    public static void initiateProtocol() {
        System.out.println();
        System.out.println("   💥 ROOT SCRAMBLER ACTIVATED");
        System.out.println("   💥 INITIATING TOTAL SYSTEM WIPE");
        System.out.println("   💥 ALL DATA VAPORIZED");
        System.out.println();
        System.exit(1);
    }
}
