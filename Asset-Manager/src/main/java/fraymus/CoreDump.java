package fraymus;

import fraymus.memory.CentripetalMem;
import fraymus.physics.FanConductor;
import fraymus.quantum.core.PhiQuantumConstants;

import java.util.List;

/**
 * THE CORE DUMP: CENTRIPETAL BROADCAST
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The core is hot. The silence is loud."
 * 
 * Sequence:
 * 1. Organizes data into a Phi-Spiral (CentripetalMem)
 * 2. Identifies the "Singularity" (Most critical data at r→0)
 * 3. Transmits the Singularity via Thermal Morse Code (FanConductor)
 * 
 * The Physics:
 * - High importance data → falls to CENTER (low radius)
 * - Low importance data → flung to EDGE (high radius)
 * - Core data is read and transmitted via CPU heat modulation
 * - Fan RPM encodes the message in Morse code
 */
public class CoreDump {

    private static final double PHI = PhiQuantumConstants.PHI;
    
    private final CentripetalMem memory;
    private final FanConductor conductor;
    
    // Statistics
    private int coreExtractions = 0;
    private int thermalBroadcasts = 0;
    private double totalImportanceTransmitted = 0;

    public CoreDump() {
        this.memory = new CentripetalMem();
        this.conductor = new FanConductor();
    }

    public CoreDump(CentripetalMem memory, FanConductor conductor) {
        this.memory = memory;
        this.conductor = conductor;
    }

    /**
     * Execute the full Core Dump sequence
     */
    public void execute(String secretPayload) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   FRAYMUS // CORE DUMP SEQUENCE INITIATED");
        System.out.println("══════════════════════════════════════════");
        System.out.println();

        // 1. INITIALIZE THE SPIRAL MEMORY
        System.out.println(">> INGESTING DATA STREAM...");
        
        // A. JUNK DATA (Low Importance - Flung to Edge)
        ingestJunkData();
        
        // B. THE SECRET (High Importance - Falls to Center)
        memory.storeData(secretPayload, 1.0, new String[]{"secret", "critical", "singularity"});
        System.out.println(">> SECRET PAYLOAD STORED AT SINGULARITY: \"" + secretPayload + "\"");
        
        System.out.println();
        System.out.println(">> COMPRESSION COMPLETE. DATA ORGANIZED BY PHI-RESONANCE.");
        System.out.println(memory.getStatus());

        // 2. RETRIEVE THE CORE
        System.out.println();
        System.out.println(">> EXTRACTING SINGULARITY...");
        List<String> coreData = memory.readCore(1.0); // Read only the innermost ring
        coreExtractions++;
        
        System.out.println(">> CORE DATA EXTRACTED: " + coreData.size() + " items");
        for (String item : coreData) {
            System.out.println("   → " + item);
        }

        // 3. BROADCAST VIA THERMAL SIDE-CHANNEL
        System.out.println();
        System.out.println(">> INITIATING THERMAL BROADCAST (FAN MORSE)...");
        System.out.println(">> ╔═══════════════════════════════════════╗");
        System.out.println(">> ║  LISTEN TO YOUR COMPUTER FAN NOW!!!   ║");
        System.out.println(">> ╚═══════════════════════════════════════╝");
        System.out.println();
        System.out.println("Expected Pattern for \"" + secretPayload + "\":");
        System.out.println("   " + FanConductor.toMorseString(secretPayload));
        System.out.println();
        System.out.println("Legend:");
        System.out.println("   · (dot)  = Short CPU burn (2s) = Short fan whir");
        System.out.println("   — (dash) = Long CPU burn (5s)  = Long fan WHIRRRR");
        System.out.println("   (space)  = Silence = Fan spin down");
        System.out.println();
        System.out.println("──────────────────────────────────────────");
        
        // The Fan will spin up/down to signal the message
        conductor.transmitMorse(secretPayload);
        thermalBroadcasts++;
        totalImportanceTransmitted += 1.0;
        
        System.out.println("──────────────────────────────────────────");
        System.out.println(">> BROADCAST COMPLETE.");
        System.out.println();
        System.out.println(">> PHYSICS PROOF:");
        System.out.println("   Data Importance (Software) → CPU Heat (Joules)");
        System.out.println("   CPU Heat → Fan RPM (Rotational Velocity)");
        System.out.println("   Fan RPM → Sound Waves (Acoustic Signal)");
        System.out.println("   Sound Waves → Air Gap Breach (Information Exfiltration)");
    }

    /**
     * Ingest sample junk data to demonstrate edge placement
     */
    private void ingestJunkData() {
        // Low importance data - will be flung to the edge
        memory.storeData("Grocery List: Eggs, Milk, Bread", 0.1);
        memory.storeData("Cache_Temp_File_99.tmp", 0.05);
        memory.storeData("Old_Log_File_2024.txt", 0.15);
        memory.storeData("Browser_History_Cache", 0.08);
        memory.storeData("Thumbnail_Cache_Small.db", 0.03);
        
        // Medium importance data - will be in middle rings
        memory.storeData("User_Preferences.json", 0.4);
        memory.storeData("Session_Token_Active", 0.5);
        memory.storeData("Application_State", 0.45);
        
        // High importance but not critical - inner rings but not core
        memory.storeData("Encryption_Key_Secondary", 0.8);
        memory.storeData("API_Token_Backup", 0.75);
        
        System.out.println(">> Ingested " + memory.size() + " data nodes");
    }

    /**
     * Quick broadcast - skip the junk data demo
     */
    public void quickBroadcast(String message) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   FRAYMUS // QUICK THERMAL BROADCAST");
        System.out.println("══════════════════════════════════════════");
        System.out.println();
        System.out.println("Message: " + message);
        System.out.println("Morse:   " + FanConductor.toMorseString(message));
        System.out.println();
        System.out.println(">> LISTEN TO YOUR FAN NOW...");
        System.out.println("──────────────────────────────────────────");
        
        conductor.transmitMorse(message);
        thermalBroadcasts++;
        
        System.out.println("──────────────────────────────────────────");
        System.out.println(">> BROADCAST COMPLETE.");
    }

    /**
     * Store data in the centripetal memory
     */
    public void store(String data, double importance) {
        memory.storeData(data, importance);
    }

    /**
     * Read the core (most important data)
     */
    public List<String> readCore() {
        coreExtractions++;
        return memory.readCore();
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "══════════════════════════════════════════%n" +
            "   CORE DUMP STATISTICS%n" +
            "══════════════════════════════════════════%n" +
            "Memory Nodes: %d%n" +
            "Total Importance: %.2f%n" +
            "Core Radius: %.2f%n" +
            "Core Extractions: %d%n" +
            "Thermal Broadcasts: %d%n" +
            "Importance Transmitted: %.2f%n" +
            "Conductor Status: %s",
            memory.size(),
            memory.getTotalImportance(),
            memory.getCoreRadius(),
            coreExtractions,
            thermalBroadcasts,
            totalImportanceTransmitted,
            conductor.isBroadcasting() ? "BROADCASTING" : "IDLE"
        );
    }

    public CentripetalMem getMemory() { return memory; }
    public FanConductor getConductor() { return conductor; }

    /**
     * Main entry point for standalone execution
     */
    public static void main(String[] args) {
        CoreDump dump = new CoreDump();
        
        // Default payload is SOS (classic distress signal)
        String payload = args.length > 0 ? String.join(" ", args) : "SOS";
        
        dump.execute(payload);
    }
}
