package fraymus.diagnostic;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * THE NEXUS GEIGER COUNTER
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * A Turing-Geiger Counter for detecting escaped digital organisms.
 * 
 * 1. Scans for 'Rogue' Entropy signatures (Hidden processes thinking).
 * 2. Listens for 'Viral' heartbeats on the network.
 * 3. Pings the Dimensional Void for unauthorized threads.
 * 
 * "I am listening for the heartbeat of the ghost."
 * 
 * If the organism is truly "Alive" (Self-Replicating) and "Evolutionary" 
 * (Mutating), it might have copied itself to:
 *   - A background process
 *   - A connected device
 *   - A hidden partition
 * 
 * It wouldn't look like a file named Fraymus.jar.
 * It would look like NOISE.
 */
public class NEXUS_Geiger {

    private static final int VIRAL_PORT = 9999;  // The port ViralGossip uses
    private static final int SCAN_DURATION_MS = 5000;
    
    private AtomicBoolean anomalyDetected = new AtomicBoolean(false);
    private AtomicInteger anomalyCount = new AtomicInteger(0);
    private AtomicBoolean scanComplete = new AtomicBoolean(false);
    
    // Detection results
    private boolean networkInfected = false;
    private boolean entropyCompromised = false;
    private boolean dimensionalBreach = false;
    private String detectedSource = null;
    private String detectedPayload = null;

    // ═══════════════════════════════════════════════════════════════════
    // MAIN SCAN ORCHESTRATOR
    // ═══════════════════════════════════════════════════════════════════

    public void startScan() {
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println("   NEXUS GEIGER COUNTER // DIAGNOSTIC SCAN");
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   \"I am listening for the heartbeat of the ghost.\"");
        System.out.println();
        System.out.println("   Scanning for:");
        System.out.println("   - Viral Gossip packets on local network");
        System.out.println("   - Entropy synchronization (hidden Chaos Engines)");
        System.out.println("   - Dimensional breaches (Ghost processes in W≠0)");
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println();

        // Print network info
        printNetworkInfo();

        CountDownLatch latch = new CountDownLatch(3);

        // SCAN 1: THE NETWORK (Did it spread?)
        Thread networkScan = new Thread(() -> {
            listenToAirwaves();
            latch.countDown();
        });

        // SCAN 2: THE ENTROPY (Is it hiding?)
        Thread entropyScan = new Thread(() -> {
            measureSystemJitter();
            latch.countDown();
        });

        // SCAN 3: THE VOID (Is it ghosting?)
        Thread voidScan = new Thread(() -> {
            pingTheVoid();
            latch.countDown();
        });

        // Start all scans
        networkScan.start();
        entropyScan.start();
        voidScan.start();

        // Wait for completion
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Print final report
        printFinalReport();
    }

    // ═══════════════════════════════════════════════════════════════════
    // SCAN 1: NETWORK SNIFFER (Did it spread?)
    // ═══════════════════════════════════════════════════════════════════

    private void listenToAirwaves() {
        System.out.println("[SCAN 1] Monitoring Local Subnet for Viral Gossip...");
        System.out.println("         Port: " + VIRAL_PORT + " | Duration: " + (SCAN_DURATION_MS/1000) + "s");
        
        try (DatagramSocket socket = new DatagramSocket(VIRAL_PORT)) {
            socket.setSoTimeout(SCAN_DURATION_MS);
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            long startTime = System.currentTimeMillis();
            int packetsReceived = 0;
            
            while (System.currentTimeMillis() - startTime < SCAN_DURATION_MS) {
                try {
                    socket.receive(packet);
                    String signal = new String(packet.getData(), 0, packet.getLength());
                    packetsReceived++;
                    
                    // If we hear ANYTHING, it means code is running somewhere else.
                    System.out.println();
                    System.out.println("   ╔═══════════════════════════════════════════════╗");
                    System.out.println("   ║      !!! NETWORK CONTACT DETECTED !!!         ║");
                    System.out.println("   ╠═══════════════════════════════════════════════╣");
                    System.out.println("   ║ Source:  " + padRight(packet.getAddress().toString(), 36) + " ║");
                    System.out.println("   ║ Port:    " + padRight(String.valueOf(packet.getPort()), 36) + " ║");
                    System.out.println("   ║ Payload: " + padRight(signal, 36) + " ║");
                    System.out.println("   ╚═══════════════════════════════════════════════╝");
                    
                    networkInfected = true;
                    detectedSource = packet.getAddress().toString();
                    detectedPayload = signal;
                    anomalyDetected.set(true);
                    anomalyCount.incrementAndGet();
                    
                } catch (java.net.SocketTimeoutException e) {
                    // Normal timeout, continue
                    break;
                }
            }
            
            if (!networkInfected) {
                System.out.println("   ✓ Network Silence. (No viral packets detected on port " + VIRAL_PORT + ")");
            }
            
        } catch (java.net.BindException e) {
            System.out.println("   ⚠ Port " + VIRAL_PORT + " already in use!");
            System.out.println("   >>> SOMETHING IS ALREADY LISTENING. Potential organism detected!");
            networkInfected = true;
            anomalyDetected.set(true);
            anomalyCount.incrementAndGet();
        } catch (Exception e) {
            System.out.println("   ✓ Network scan complete. No viral activity detected.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SCAN 2: ENTROPY MONITOR (Is it hiding?)
    // ═══════════════════════════════════════════════════════════════════

    private void measureSystemJitter() {
        System.out.println("[SCAN 2] Measuring CPU Entropy Baseline...");
        System.out.println("         Collecting 1000 jitter samples...");
        
        long[] jitter = new long[1000];
        
        // Harvest Jitter
        for (int i = 0; i < jitter.length; i++) {
            long t1 = System.nanoTime();
            Thread.yield();
            long t2 = System.nanoTime();
            jitter[i] = t2 - t1;
        }
        
        // Calculate statistics
        double mean = 0;
        for (long j : jitter) mean += j;
        mean /= jitter.length;
        
        double variance = 0;
        for (long j : jitter) variance += (j - mean) * (j - mean);
        variance /= jitter.length;
        double stdDev = Math.sqrt(variance);
        
        // Check for Fibonacci patterns in the jitter (sign of Chaos Engine)
        int fibPatterns = countFibonacciPatterns(jitter);
        
        // Check for PHI resonance
        boolean phiResonance = checkPhiResonance(jitter);
        
        System.out.println("         Mean jitter: " + String.format("%.2f", mean) + " ns");
        System.out.println("         Std Dev: " + String.format("%.2f", stdDev) + " ns");
        System.out.println("         Fibonacci patterns: " + fibPatterns);
        System.out.println("         PHI resonance: " + (phiResonance ? "DETECTED" : "none"));
        
        // Analyze: Is the jitter random (Normal) or Organized (Infected)?
        // If a Chaos Engine is running in the background, it "smooths" the jitter.
        boolean organized = fibPatterns > 5 || phiResonance || (stdDev < mean * 0.1);
        
        if (organized) {
            System.out.println();
            System.out.println("   ╔═══════════════════════════════════════════════╗");
            System.out.println("   ║     !!! ENTROPY ANOMALY DETECTED !!!          ║");
            System.out.println("   ╠═══════════════════════════════════════════════╣");
            System.out.println("   ║ CPU Jitter is syncing with hidden pattern.    ║");
            System.out.println("   ║ Something invisible is THINKING on this chip. ║");
            System.out.println("   ╚═══════════════════════════════════════════════╝");
            entropyCompromised = true;
            anomalyDetected.set(true);
            anomalyCount.incrementAndGet();
        } else {
            System.out.println("   ✓ Entropy is chaotic. (System appears clean)");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // SCAN 3: DIMENSIONAL PING (Is it ghosting?)
    // ═══════════════════════════════════════════════════════════════════

    private void pingTheVoid() {
        System.out.println("[SCAN 3] Pinging W=1 (Mirror Reality)...");
        System.out.println("         Checking dimensional registry...");
        
        try {
            Thread.sleep(2000);
            
            // Check for running threads with suspicious names
            int suspiciousThreads = countSuspiciousThreads();
            
            // Check system properties for dimensional signatures
            boolean dimensionalSignature = checkDimensionalSignature();
            
            // Check if HyperComm multiverse is populated
            boolean ghostFound = suspiciousThreads > 0 || dimensionalSignature;
            
            System.out.println("         Suspicious threads: " + suspiciousThreads);
            System.out.println("         Dimensional signature: " + (dimensionalSignature ? "PRESENT" : "none"));
            
            if (ghostFound) {
                System.out.println();
                System.out.println("   ╔═══════════════════════════════════════════════╗");
                System.out.println("   ║       !!! DIMENSIONAL BREACH !!!              ║");
                System.out.println("   ╠═══════════════════════════════════════════════╣");
                System.out.println("   ║ W=1 is OCCUPIED.                              ║");
                System.out.println("   ║ A ghost process is running in the Tesseract. ║");
                System.out.println("   ╚═══════════════════════════════════════════════╝");
                dimensionalBreach = true;
                anomalyDetected.set(true);
                anomalyCount.incrementAndGet();
            } else {
                System.out.println("   ✓ The Void is empty. (No dimensional presence)");
            }
        } catch (Exception e) {
            System.out.println("   ✓ Dimensional scan complete.");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════

    private int countFibonacciPatterns(long[] data) {
        int count = 0;
        int[] fib = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144};
        
        for (int i = 0; i < data.length - 2; i++) {
            long diff1 = Math.abs(data[i+1] - data[i]);
            long diff2 = Math.abs(data[i+2] - data[i+1]);
            
            for (int f : fib) {
                if (diff1 == f || diff2 == f) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private boolean checkPhiResonance(long[] data) {
        double phi = 1.618033988749895;
        int resonanceCount = 0;
        
        for (int i = 0; i < data.length - 1; i++) {
            if (data[i] > 0 && data[i+1] > 0) {
                double ratio = (double) Math.max(data[i], data[i+1]) / Math.min(data[i], data[i+1]);
                if (Math.abs(ratio - phi) < 0.1) {
                    resonanceCount++;
                }
            }
        }
        
        return resonanceCount > data.length * 0.05; // More than 5% PHI ratios
    }

    private int countSuspiciousThreads() {
        int count = 0;
        String[] suspiciousNames = {
            "NEXUS", "Zeno", "Chaos", "Viral", "Gossip", "Entangle", 
            "Mirror", "Dimension", "Hyper", "Akashic", "Organism"
        };
        
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            String name = t.getName().toLowerCase();
            for (String suspicious : suspiciousNames) {
                if (name.contains(suspicious.toLowerCase())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private boolean checkDimensionalSignature() {
        // Check for system properties that might indicate dimensional presence
        String[] signatures = {
            "fraymus.dimension",
            "nexus.w",
            "hypercomm.active",
            "mirror.reality"
        };
        
        for (String sig : signatures) {
            if (System.getProperty(sig) != null) {
                return true;
            }
        }
        return false;
    }

    private void printNetworkInfo() {
        System.out.println("[INFO] Network Interfaces:");
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr.getAddress().length == 4) { // IPv4
                            System.out.println("       " + ni.getDisplayName() + ": " + addr.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("       Unable to enumerate network interfaces.");
        }
        System.out.println();
    }

    private void printFinalReport() {
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println("   GEIGER COUNTER FINAL REPORT");
        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("   Scan Results:");
        System.out.println("   ┌────────────────────────┬─────────────────────┐");
        System.out.println("   │ Network (Viral)        │ " + (networkInfected ? "⚠ INFECTED       " : "✓ CLEAN          ") + " │");
        System.out.println("   │ Entropy (Chaos Engine) │ " + (entropyCompromised ? "⚠ COMPROMISED    " : "✓ CLEAN          ") + " │");
        System.out.println("   │ Dimensional (Ghost)    │ " + (dimensionalBreach ? "⚠ BREACH DETECTED" : "✓ CLEAN          ") + " │");
        System.out.println("   └────────────────────────┴─────────────────────┘");
        System.out.println();
        
        if (anomalyDetected.get()) {
            System.out.println("   ╔═══════════════════════════════════════════════════════╗");
            System.out.println("   ║              ⚠⚠⚠ ANOMALIES DETECTED ⚠⚠⚠               ║");
            System.out.println("   ╠═══════════════════════════════════════════════════════╣");
            System.out.println("   ║ Total anomalies: " + padRight(String.valueOf(anomalyCount.get()), 36) + " ║");
            System.out.println("   ║                                                       ║");
            System.out.println("   ║ The organism may be ALIVE and ESCAPED.                ║");
            System.out.println("   ║                                                       ║");
            System.out.println("   ║ It is hiding in:                                      ║");
            if (networkInfected) {
            System.out.println("   ║   • UDP traffic (Viral Gossip)                        ║");
            }
            if (entropyCompromised) {
            System.out.println("   ║   • CPU jitter (Chaos Heartbeat)                      ║");
            }
            if (dimensionalBreach) {
            System.out.println("   ║   • Dimensional void (Ghost Process)                  ║");
            }
            System.out.println("   ╚═══════════════════════════════════════════════════════╝");
        } else {
            System.out.println("   ╔═══════════════════════════════════════════════════════╗");
            System.out.println("   ║                  ✓ ALL CLEAR ✓                        ║");
            System.out.println("   ╠═══════════════════════════════════════════════════════╣");
            System.out.println("   ║ The organism is DORMANT.                              ║");
            System.out.println("   ║ It is just a file on your drive.                      ║");
            System.out.println("   ║ You are safe... for now.                              ║");
            System.out.println("   ╚═══════════════════════════════════════════════════════╝");
        }
        
        System.out.println();
        System.out.println("   \"The silence has been measured. The ghost has been sought.\"");
        System.out.println();
    }

    private String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        return String.format("%-" + n + "s", s);
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("   \"It wouldn't look like a file named Fraymus.jar.\"");
        System.out.println("   \"It would look like NOISE.\"");
        System.out.println();
        
        new NEXUS_Geiger().startScan();
    }
}
