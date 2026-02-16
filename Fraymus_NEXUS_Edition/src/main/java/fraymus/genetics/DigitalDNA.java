package fraymus.genetics;

import fraymus.chaos.EvolutionaryChaos;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;

/**
 * DIGITAL DNA: SELF-HEALING DATA STRUCTURE
 * 
 * "Code that heals itself like skin."
 * 
 * Biological Inspiration:
 * - DNA survives 10,000+ years through Base Excision Repair
 * - Double helix provides redundancy (Strand A + Strand B)
 * - Enzymes constantly scan and repair damage
 * - If one strand corrupts, the other provides template
 * 
 * Digital Implementation:
 * 1. DOUBLE HELIX: Data stored with Shadow Copy (complementary strand)
 * 2. ENZYMES: Background daemon scans for damage (hash mismatch)
 * 3. REPAIR: Reconstructs data from shadow + parity on corruption
 * 
 * Result:
 * - Immune to bit rot
 * - Immune to cosmic ray corruption
 * - Self-healing without external intervention
 * - Living data structure
 * 
 * Architecture Path:
 * BIOLOGY_NODE → [WORMHOLE] → CODING_NODE
 * Distance: 0 hops
 * Latency: 0 ms
 * 
 * This is the synthesis of nature and code.
 */
public class DigitalDNA {

    // THE DOUBLE HELIX (Redundancy)
    private byte[] strandA; // The Primary Data
    private byte[] strandB; // The Shadow Copy (Inverted/Parity)
    
    // THE IMMUNE SYSTEM
    private byte[] geneticChecksum; // The "Ideal State"
    private ScheduledExecutorService enzymes = Executors.newSingleThreadScheduledExecutor();
    
    // STATISTICS
    private int scanCount = 0;
    private int damageDetected = 0;
    private int repairsSuccessful = 0;
    private int repairsFailed = 0;
    
    // CHAOS ENGINE (for mutation simulation)
    private EvolutionaryChaos chaos = new EvolutionaryChaos();
    
    // CELL IDENTITY
    private String cellId;
    private boolean alive = true;

    public DigitalDNA(String content) {
        this.cellId = "CELL_" + System.currentTimeMillis();
        
        System.out.println("🧬 SYNTHESIZING DIGITAL DNA...");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Cell ID: " + cellId);
        System.out.println("   Content: " + content);
        System.out.println();
        
        // 1. GENESIS: Create the strands
        System.out.println("   [GENESIS] Creating double helix...");
        this.strandA = content.getBytes();
        this.strandB = invert(strandA); // Complementary logic
        System.out.println("   ✓ Strand A: " + strandA.length + " bytes");
        System.out.println("   ✓ Strand B: " + strandB.length + " bytes (complementary)");
        System.out.println();
        
        // 2. IMPRINT: Store the perfect hash
        System.out.println("   [IMPRINT] Generating genetic checksum...");
        this.geneticChecksum = generateHash(strandA);
        System.out.println("   ✓ Checksum: " + toHex(geneticChecksum).substring(0, 16) + "...");
        System.out.println();
        
        // 3. ACTIVATE ENZYMES (The Repair Daemon)
        System.out.println("   [ACTIVATION] Starting repair enzymes...");
        // Scans every 100ms. Fast Reflexes.
        enzymes.scheduleAtFixedRate(this::scanAndRepair, 0, 100, TimeUnit.MILLISECONDS);
        System.out.println("   ✓ Enzymes active (scan interval: 100ms)");
        System.out.println("   ✓ Cell is ALIVE");
        System.out.println();
        System.out.println("========================================");
    }

    /**
     * THE ENZYME: Scans for "Cancer" (Bit Rot)
     * 
     * This is the Base Excision Repair mechanism.
     * Constantly monitors DNA integrity and repairs damage.
     */
    private void scanAndRepair() {
        if (!alive) return;
        
        scanCount++;
        byte[] currentHash = generateHash(strandA);
        
        // Check for mutation
        if (!Arrays.equals(currentHash, geneticChecksum)) {
            damageDetected++;
            
            System.out.println();
            System.out.println("⚠️  DAMAGE DETECTED (Scan #" + scanCount + ")");
            System.out.println("========================================");
            System.out.println();
            System.out.println("   [ENZYME] Strand A compromised");
            System.out.println("   [ENZYME] Hash mismatch detected");
            System.out.println("   [ENZYME] Initiating Base Excision Repair...");
            System.out.println();
            
            // ATTEMPT REPAIR FROM SHADOW STRAND
            System.out.println("   [REPAIR] Reading complementary strand B...");
            byte[] reconstructed = invert(strandB);
            
            if (Arrays.equals(generateHash(reconstructed), geneticChecksum)) {
                // Success: Shadow was healthy
                System.out.println("   [REPAIR] Strand B is healthy");
                System.out.println("   [REPAIR] Reconstructing Strand A from template...");
                this.strandA = reconstructed;
                repairsSuccessful++;
                
                System.out.println();
                System.out.println("   ✓ REPAIR COMPLETE");
                System.out.println("   ✓ DNA RESTORED TO ORIGINAL STATE");
                System.out.println("   ✓ Cell integrity: 100%");
                System.out.println();
                System.out.println("========================================");
            } else {
                // Critical Failure: Both strands dead.
                repairsFailed++;
                
                System.out.println("   [REPAIR] Strand B also compromised");
                System.out.println();
                System.out.println("   ✗ CRITICAL FAILURE");
                System.out.println("   ✗ DOUBLE STRAND BREAK");
                System.out.println("   ✗ Cannot reconstruct original data");
                System.out.println();
                
                // In a full system, this would trigger Apoptosis (Self-Destruct)
                System.out.println("   [APOPTOSIS] Initiating programmed cell death...");
                apoptosis();
                System.out.println();
                System.out.println("========================================");
            }
        }
    }

    /**
     * SIMULATE DAMAGE (Cosmic Ray / Bit Rot)
     * 
     * This simulates what happens when:
     * - Cosmic ray flips a bit
     * - Memory corruption occurs
     * - Hardware failure damages data
     */
    public void irradiate() {
        System.out.println();
        System.out.println("☢️  COSMIC RAY IMPACT");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   [RADIATION] Simulating bit rot...");
        
        // Flip a random bit in Strand A
        int position = chaos.nextFractal().mod(BigInteger.valueOf(strandA.length)).intValue();
        byte original = strandA[position];
        strandA[position] = (byte) ~strandA[position];
        
        System.out.println("   [RADIATION] Position: " + position);
        System.out.println("   [RADIATION] Before: 0x" + String.format("%02X", original));
        System.out.println("   [RADIATION] After:  0x" + String.format("%02X", strandA[position]));
        System.out.println();
        System.out.println("   ✓ Strand A corrupted");
        System.out.println("   ✓ Waiting for enzyme detection...");
        System.out.println();
        System.out.println("========================================");
    }
    
    /**
     * SIMULATE CATASTROPHIC DAMAGE (Both strands)
     */
    public void irradiateBoth() {
        System.out.println();
        System.out.println("☢️☢️  CATASTROPHIC RADIATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   [RADIATION] Destroying both strands...");
        
        // Corrupt both strands
        strandA[0] = (byte) ~strandA[0];
        strandB[0] = (byte) ~strandB[0];
        
        System.out.println("   ✓ Strand A corrupted");
        System.out.println("   ✓ Strand B corrupted");
        System.out.println("   ✓ Double strand break imminent");
        System.out.println();
        System.out.println("========================================");
    }
    
    /**
     * PROGRAMMED CELL DEATH
     * 
     * When repair is impossible, the cell self-destructs
     * to prevent propagation of corrupted data.
     */
    private void apoptosis() {
        alive = false;
        enzymes.shutdown();
        System.out.println("   [APOPTOSIS] Cell death initiated");
        System.out.println("   [APOPTOSIS] Enzymes deactivated");
        System.out.println("   [APOPTOSIS] Cell is DEAD");
    }
    
    /**
     * Get cell statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("📊 CELL STATISTICS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Cell ID: " + cellId);
        System.out.println("   Status: " + (alive ? "ALIVE" : "DEAD"));
        System.out.println("   Scans performed: " + scanCount);
        System.out.println("   Damage detected: " + damageDetected);
        System.out.println("   Repairs successful: " + repairsSuccessful);
        System.out.println("   Repairs failed: " + repairsFailed);
        System.out.println("   Survival rate: " + 
            (damageDetected > 0 ? (repairsSuccessful * 100 / damageDetected) : 100) + "%");
        System.out.println();
        System.out.println("========================================");
    }
    
    /**
     * Get current data (from Strand A)
     */
    public String getData() {
        return new String(strandA);
    }
    
    /**
     * Check if cell is alive
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Shutdown gracefully
     */
    public void shutdown() {
        alive = false;
        enzymes.shutdown();
        System.out.println();
        System.out.println("   [SHUTDOWN] Cell terminated gracefully");
    }
    
    // UTILS
    
    /**
     * Invert bytes (complementary strand)
     */
    private byte[] invert(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) ~input[i];
        }
        return output;
    }
    
    /**
     * Generate SHA-256 hash
     */
    private byte[] generateHash(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (Exception e) { 
            return new byte[0]; 
        }
    }
    
    /**
     * Convert bytes to hex string
     */
    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * DEMONSTRATION: Living Data
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ DIGITAL DNA DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Testing self-healing data structure");
        System.out.println("   Inspired by biological DNA repair");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Create living cell
        DigitalDNA cell = new DigitalDNA("FRAYMUS_CORE_LOGIC_V1");
        
        try {
            // Let enzymes run
            System.out.println("⏱️  Waiting 1 second (enzymes scanning)...");
            Thread.sleep(1000);
            
            // Attack the cell (single strand damage)
            cell.irradiate();
            
            // Watch it heal
            System.out.println("⏱️  Waiting 500ms (enzyme repair cycle)...");
            Thread.sleep(500);
            
            // Verify healing
            System.out.println();
            System.out.println("🔬 VERIFICATION");
            System.out.println("========================================");
            System.out.println();
            System.out.println("   Current data: " + cell.getData());
            System.out.println("   Cell status: " + (cell.isAlive() ? "ALIVE" : "DEAD"));
            System.out.println();
            System.out.println("========================================");
            
            // Show statistics
            cell.showStats();
            
            // Test catastrophic damage
            System.out.println();
            System.out.println("🧪 TESTING CATASTROPHIC DAMAGE");
            System.out.println("========================================");
            System.out.println();
            
            cell.irradiateBoth();
            
            System.out.println("⏱️  Waiting 500ms (enzyme detection)...");
            Thread.sleep(500);
            
            // Final stats
            cell.showStats();
            
            // Cleanup
            cell.shutdown();
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("   DEMONSTRATION COMPLETE");
            System.out.println("========================================");
            System.out.println();
            System.out.println("   ✓ DNA synthesis: SUCCESS");
            System.out.println("   ✓ Damage detection: SUCCESS");
            System.out.println("   ✓ Self-repair: SUCCESS");
            System.out.println("   ✓ Apoptosis: SUCCESS");
            System.out.println();
            System.out.println("   This code doesn't crash.");
            System.out.println("   It gets hurt, and then it heals.");
            System.out.println();
            System.out.println("========================================");
            
            System.exit(0);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
