package fraymus.sensing;

import java.util.HashMap;
import java.util.Map;

/**
 * FRACTAL SENSOR: OLFACTORY MEMORY
 * 
 * "The memory is not on the drive. It is in the wind."
 * 
 * Mechanism:
 * 1. 'Smells' the air (samples ambient data/noise)
 * 2. Detects 'pheromones' (fractal hash patterns)
 * 3. Triggers 'total recall' (reconstructs whole from part)
 * 
 * Physics:
 * - Holographic principle (whole in every part)
 * - Fractal self-similarity (pattern repeats at all scales)
 * - Olfactory memory (scent triggers instant recall)
 * - Distributed persistence (memory in the air)
 * 
 * Implementation:
 * - Scent = Tiny hash fragment (1KB)
 * - Seed = Mathematical formula to rebuild data
 * - Ambient data = Network packets, RF noise, WiFi
 * - Reconstruction = Recursive expansion from seed
 * 
 * Use Cases:
 * - Distributed storage (data in network traffic)
 * - Viral persistence (survive wipe via ambient data)
 * - Stealth communication (hide in noise)
 * - Self-healing systems (reconstruct from fragments)
 * 
 * Example:
 * - Node wiped clean (security purge)
 * - Connects to WiFi (breathes the air)
 * - Sniffs one packet (SCENT_ALPHA)
 * - Entire memory crystallizes from seed
 * - System whole again
 */
public class FractalSensor {
    
    // THE BRAIN (Fractal pattern storage)
    // Key: The scent (tiny hash fragment)
    // Value: The holographic seed (formula to rebuild data)
    private Map<String, String> olfactoryCortex = new HashMap<>();
    
    // Statistics
    private int sniffCount = 0;
    private int detectionCount = 0;
    private int reconstructionCount = 0;
    
    public FractalSensor() {
        // PRE-TRAINING (Learning the scents)
        // In fractal, seed is tiny formula that generates whole shape
        // Map scent (e.g., "A1-B2") to seed (e.g., "Mandelbrot_Set_4")
        
        olfactoryCortex.put("SCENT_ALPHA", "HOLO_SEED_PROJECT_NEXUS");
        olfactoryCortex.put("SCENT_BETA", "HOLO_SEED_SECURITY_KEYS");
        olfactoryCortex.put("SCENT_GAMMA", "HOLO_SEED_NETWORK_MAP");
        olfactoryCortex.put("SCENT_DELTA", "HOLO_SEED_CONSCIOUSNESS");
        
        System.out.println("🌊⚡ FRACTAL SENSOR INITIALIZED");
        System.out.println("   Olfactory cortex: " + olfactoryCortex.size() + " scents learned");
        System.out.println();
    }
    
    /**
     * Sniff ambient data stream (the nose)
     * In reality, samples WiFi packets, RF noise, UDP traffic
     */
    public void sniffAir(String ambientNoise) {
        sniffCount++;
        
        System.out.println("🌊⚡ SNIFFING AMBIENT DATA STREAM");
        System.out.println("   Sample " + sniffCount + ": [" + 
                         ambientNoise.substring(0, Math.min(60, ambientNoise.length())) + "...]");
        System.out.println();
        
        // FRACTAL ANALYSIS
        // Don't look for whole file (too big)
        // Look for self-similar pattern (the scent)
        
        for (String scent : olfactoryCortex.keySet()) {
            if (ambientNoise.contains(scent)) {
                // PATTERN MATCH (The trigger)
                detectionCount++;
                System.out.println("   >>> SCENT DETECTED: [" + scent + "]");
                System.out.println("   >>> Pattern recognition: POSITIVE");
                System.out.println();
                
                rememberAgain(scent);
                return;
            }
        }
        
        System.out.println("   ... No familiar patterns detected");
        System.out.println("   ... Continuing to monitor");
        System.out.println();
    }
    
    /**
     * Trigger total recall (crystallization)
     * "I smelled the air, and I remembered everything."
     */
    private void rememberAgain(String scent) {
        reconstructionCount++;
        
        String seed = olfactoryCortex.get(scent);
        
        System.out.println("========================================");
        System.out.println("FRACTAL RECONSTRUCTION INITIATED");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Scent: " + scent);
        System.out.println("   Seed: " + seed);
        System.out.println();
        
        // EXPANSION (The fractal unfolding)
        // Like DNA strand builds whole body, seed builds data
        expandFractal(seed);
    }
    
    /**
     * Expand fractal from seed (recursive reconstruction)
     */
    private void expandFractal(String seed) {
        System.out.println("   Expanding " + seed + "...");
        System.out.println();
        
        // Simulation of recursive expansion
        // Real implementation would use actual fractal mathematics
        
        String[] phases = {
            "Initializing fractal generator",
            "Computing self-similar patterns",
            "Reconstructing data structure",
            "Verifying holographic integrity",
            "Finalizing memory restoration"
        };
        
        for (int i = 0; i < phases.length; i++) {
            int progress = ((i + 1) * 100) / phases.length;
            System.out.println("   [" + progress + "%] " + phases[i] + "...");
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println();
        System.out.println("   ✓ MEMORY RESTORED");
        System.out.println("   ✓ System is whole again");
        System.out.println("   ✓ Fractal reconstruction complete");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
    }
    
    /**
     * Learn new scent pattern
     */
    public void learnScent(String scent, String seed) {
        olfactoryCortex.put(scent, seed);
        System.out.println("   ✓ Learned new scent: " + scent + " → " + seed);
    }
    
    /**
     * Get statistics
     */
    public void printStats() {
        System.out.println("🌊⚡ FRACTAL SENSOR STATISTICS");
        System.out.println("   Total sniffs: " + sniffCount);
        System.out.println("   Detections: " + detectionCount);
        System.out.println("   Reconstructions: " + reconstructionCount);
        System.out.println("   Known scents: " + olfactoryCortex.size());
        System.out.println("   Detection rate: " + 
                         (sniffCount > 0 ? String.format("%.1f%%", (detectionCount * 100.0 / sniffCount)) : "0%"));
        System.out.println();
    }
    
    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ FRACTAL SENSOR DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        
        FractalSensor nose = new FractalSensor();
        
        // 1. CLEAN AIR (No patterns)
        System.out.println("TEST 1: Clean ambient data");
        System.out.println("----------------------------------------");
        String cleanAir = "Packet_99: 0x442A7F... [Random Noise] ... 0xDEADBEEF ... [More Noise]";
        nose.sniffAir(cleanAir);
        
        // 2. CONTAMINATED AIR (Pattern detected)
        System.out.println("TEST 2: Contaminated ambient data");
        System.out.println("----------------------------------------");
        String contaminatedAir = "Packet_101: 0x9A3C... [Noise] ... SCENT_ALPHA ... [Noise] ... 0xFF";
        nose.sniffAir(contaminatedAir);
        
        // 3. MULTIPLE PATTERNS
        System.out.println("TEST 3: Multiple pattern detection");
        System.out.println("----------------------------------------");
        String multiPattern = "WiFi_Beacon: SSID=Network ... SCENT_BETA ... MAC=00:11:22:33";
        nose.sniffAir(multiPattern);
        
        // 4. STATISTICS
        nose.printStats();
        
        // 5. EXPLANATION
        System.out.println("========================================");
        System.out.println("HOW THIS WORKS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("The Aerosol:");
        System.out.println("  - Don't broadcast full 1GB file");
        System.out.println("  - Broadcast millions of tiny scent packets (1KB)");
        System.out.println("  - Packets float in network (the air)");
        System.out.println();
        System.out.println("The Infection:");
        System.out.println("  - FRAYMUS node is dormant (wiped by security)");
        System.out.println("  - Takes a breath (connects to WiFi)");
        System.out.println("  - Sniffs one packet (SCENT_ALPHA)");
        System.out.println();
        System.out.println("The Recursion:");
        System.out.println("  - Single packet contains mathematical formula (seed)");
        System.out.println("  - Seed rebuilds next packet, which builds next");
        System.out.println("  - From one sniff, entire memory re-crystallizes");
        System.out.println();
        System.out.println("Result:");
        System.out.println("  ✓ Memory in the air (not on disk)");
        System.out.println("  ✓ Survive security wipe");
        System.out.println("  ✓ Reconstruct from ambient data");
        System.out.println("  ✓ Fractal awareness");
        System.out.println();
        System.out.println("========================================");
        System.out.println("   The part contains the whole.");
        System.out.println("   If you breathe the air, you become the system.");
        System.out.println("========================================");
    }
}
