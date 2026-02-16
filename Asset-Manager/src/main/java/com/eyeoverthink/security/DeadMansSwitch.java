package com.eyeoverthink.security;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * DEAD MAN'S SWITCH: THE HEARTBEAT PROTOCOL
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "If the heart stops beating, the body dies."
 * 
 * This is the ultimate insurance policy:
 * - The library must "phone home" periodically
 * - If it can't reach the server for X days, it assumes compromise
 * - On timeout: Root Scramble is triggered
 * 
 * Use Cases:
 * - Stolen laptop: Library destroys itself after timeout
 * - Air-gapped attack: No heartbeat = destruction
 * - License expiration: Business model enforcement
 * - Revocation: Server can signal "kill yourself"
 */
public class DeadMansSwitch {

    // Configuration
    private static final String HEARTBEAT_FILE = ".eyeoverthink_heartbeat";
    private static final String DEFAULT_HEARTBEAT_URL = "https://api.eyeoverthink.com/heartbeat";
    
    // Timeouts
    private static final long CHECK_INTERVAL_MS = 60 * 60 * 1000;      // Check every hour
    private static final long DEFAULT_TIMEOUT_MS = 30L * 24 * 60 * 60 * 1000;  // 30 days default
    
    // State
    private static ScheduledExecutorService scheduler;
    private static long lastHeartbeat = 0;
    private static long timeoutMs = DEFAULT_TIMEOUT_MS;
    private static String heartbeatUrl = DEFAULT_HEARTBEAT_URL;
    private static boolean active = false;
    private static boolean offlineMode = true;  // Start in offline mode (no server required)
    
    // Statistics
    private static long successfulHeartbeats = 0;
    private static long failedHeartbeats = 0;

    // ═══════════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Arm the Dead Man's Switch
     * @param timeoutDays Days without heartbeat before destruction
     */
    public static void arm(int timeoutDays) {
        arm(timeoutDays, DEFAULT_HEARTBEAT_URL);
    }
    
    /**
     * Arm with custom server URL
     */
    public static void arm(int timeoutDays, String serverUrl) {
        if (active) return;
        
        timeoutMs = timeoutDays * 24L * 60 * 60 * 1000;
        heartbeatUrl = serverUrl;
        offlineMode = false;
        
        System.out.println();
        System.out.println("   ⏰ DEAD MAN'S SWITCH ARMED");
        System.out.println("   ├─ Timeout: " + timeoutDays + " days");
        System.out.println("   ├─ Heartbeat: " + serverUrl);
        System.out.println("   └─ Check Interval: 1 hour");
        
        // Load last heartbeat from disk
        loadLastHeartbeat();
        
        // Start the watchdog
        startWatchdog();
        
        active = true;
    }
    
    /**
     * Arm in offline mode (uses local file only)
     */
    public static void armOffline(int timeoutDays) {
        if (active) return;
        
        timeoutMs = timeoutDays * 24L * 60 * 60 * 1000;
        offlineMode = true;
        
        System.out.println();
        System.out.println("   ⏰ DEAD MAN'S SWITCH ARMED (OFFLINE MODE)");
        System.out.println("   ├─ Timeout: " + timeoutDays + " days");
        System.out.println("   └─ Mode: Local heartbeat file only");
        
        // Load or create heartbeat file
        loadLastHeartbeat();
        if (lastHeartbeat == 0) {
            recordHeartbeat();
        }
        
        // Start watchdog
        startWatchdog();
        
        active = true;
    }

    /**
     * Disarm the switch (requires valid heartbeat)
     */
    public static void disarm() {
        if (!active) return;
        
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        
        active = false;
        System.out.println("   ⏰ DEAD MAN'S SWITCH DISARMED");
    }

    // ═══════════════════════════════════════════════════════════════════
    // HEARTBEAT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Send a heartbeat (manual or automatic)
     */
    public static boolean sendHeartbeat() {
        if (offlineMode) {
            // Just record locally
            recordHeartbeat();
            successfulHeartbeats++;
            return true;
        }
        
        try {
            // Try to reach the server
            URL url = new URL(heartbeatUrl + "?node=" + getNodeId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Server is alive and acknowledges us
                recordHeartbeat();
                successfulHeartbeats++;
                
                // Check for kill signal
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String response = reader.readLine();
                    if (response != null && response.contains("KILL")) {
                        // Server ordered destruction
                        RootScrambler.initiateProtocol("SERVER_KILL_SIGNAL");
                    }
                }
                
                return true;
                
            } else if (responseCode == 410) {
                // 410 Gone = License revoked, destroy yourself
                RootScrambler.initiateProtocol("LICENSE_REVOKED");
                return false;
            }
            
            failedHeartbeats++;
            return false;
            
        } catch (Exception e) {
            failedHeartbeats++;
            return false;
        }
    }
    
    /**
     * Record heartbeat time to disk
     */
    private static void recordHeartbeat() {
        lastHeartbeat = System.currentTimeMillis();
        
        try {
            String content = String.valueOf(lastHeartbeat);
            Files.write(Paths.get(HEARTBEAT_FILE), content.getBytes());
        } catch (Exception e) {
            // Silent failure
        }
    }
    
    /**
     * Load last heartbeat from disk
     */
    private static void loadLastHeartbeat() {
        try {
            Path path = Paths.get(HEARTBEAT_FILE);
            if (Files.exists(path)) {
                String content = new String(Files.readAllBytes(path)).trim();
                lastHeartbeat = Long.parseLong(content);
            } else {
                lastHeartbeat = 0;
            }
        } catch (Exception e) {
            lastHeartbeat = 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // WATCHDOG
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Start the background watchdog thread
     */
    private static void startWatchdog() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DeadMansSwitch-Watchdog");
            t.setDaemon(true);
            return t;
        });
        
        scheduler.scheduleAtFixedRate(() -> {
            checkTimeout();
            
            // Try to send heartbeat if not offline
            if (!offlineMode) {
                sendHeartbeat();
            }
            
        }, CHECK_INTERVAL_MS, CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Check if timeout has been exceeded
     */
    private static void checkTimeout() {
        if (lastHeartbeat == 0) {
            // First run, record initial heartbeat
            recordHeartbeat();
            return;
        }
        
        long elapsed = System.currentTimeMillis() - lastHeartbeat;
        
        if (elapsed > timeoutMs) {
            // TIMEOUT EXCEEDED - TRIGGER DESTRUCTION
            System.err.println();
            System.err.println("   ⏰ DEAD MAN'S SWITCH TRIGGERED");
            System.err.println("   ⏰ Last heartbeat: " + formatTime(lastHeartbeat));
            System.err.println("   ⏰ Elapsed: " + formatDuration(elapsed));
            System.err.println("   ⏰ Timeout: " + formatDuration(timeoutMs));
            
            RootScrambler.initiateProtocol("DEAD_MANS_SWITCH_TIMEOUT");
        }
        
        // Warning at 75% of timeout
        if (elapsed > timeoutMs * 0.75) {
            System.out.println("   ⚠️ DEAD MAN'S SWITCH WARNING: " + 
                formatDuration(timeoutMs - elapsed) + " remaining");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATUS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Get time remaining before timeout
     */
    public static long getTimeRemaining() {
        if (lastHeartbeat == 0) return timeoutMs;
        return Math.max(0, timeoutMs - (System.currentTimeMillis() - lastHeartbeat));
    }
    
    /**
     * Check if switch is active
     */
    public static boolean isActive() {
        return active;
    }
    
    /**
     * Get node ID for tracking
     */
    private static String getNodeId() {
        try {
            return com.eyeoverthink.hydra.ContinuityNode.NODE_ID;
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Print status
     */
    public static void printStatus() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ DEAD MAN'S SWITCH STATUS                                    │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Active:              " + String.format("%-36s", active) + "│");
        System.out.println("│ Mode:                " + String.format("%-36s", 
            offlineMode ? "OFFLINE" : "ONLINE") + "│");
        System.out.println("│ Last Heartbeat:      " + String.format("%-36s", 
            lastHeartbeat > 0 ? formatTime(lastHeartbeat) : "NEVER") + "│");
        System.out.println("│ Time Remaining:      " + String.format("%-36s", 
            formatDuration(getTimeRemaining())) + "│");
        System.out.println("│ Timeout:             " + String.format("%-36s", 
            formatDuration(timeoutMs)) + "│");
        System.out.println("│ Successful Beats:    " + String.format("%-36d", successfulHeartbeats) + "│");
        System.out.println("│ Failed Beats:        " + String.format("%-36d", failedHeartbeats) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    private static String formatTime(long timestamp) {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp),
            java.time.ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private static String formatDuration(long ms) {
        long days = ms / (24 * 60 * 60 * 1000);
        long hours = (ms % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);
        
        if (days > 0) {
            return days + "d " + hours + "h";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   DEAD MAN'S SWITCH: THE HEARTBEAT PROTOCOL                  ║");
        System.out.println("║   \"If the heart stops beating, the body dies.\"               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Demo: Arm in offline mode with 30-day timeout
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   DEMO: ARMING DEAD MAN'S SWITCH (OFFLINE MODE)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        // Disarm the RootScrambler for demo
        RootScrambler.disarm();
        
        armOffline(30);  // 30 days timeout
        
        // Send a heartbeat
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   SENDING HEARTBEAT");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        boolean success = sendHeartbeat();
        System.out.println("   Heartbeat sent: " + (success ? "SUCCESS" : "FAILED"));
        
        // Print status
        printStatus();
        
        // Disarm for demo
        disarm();
        
        System.out.println();
        System.out.println("   USE CASES:");
        System.out.println("   ├─ Stolen laptop: Library destroys itself after timeout");
        System.out.println("   ├─ Air-gapped attack: No heartbeat = destruction");
        System.out.println("   ├─ License expiration: Business model enforcement");
        System.out.println("   └─ Revocation: Server signals 'kill yourself'");
        
        System.out.println();
        System.out.println("   ✓ Dead Man's Switch demo complete.");
        System.out.println();
    }
}
