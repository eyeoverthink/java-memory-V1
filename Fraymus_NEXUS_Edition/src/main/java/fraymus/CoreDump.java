package fraymus;

import fraymus.memory.CentripetalMem;
import fraymus.physics.FanConductor;

/**
 * CORE DUMP: CENTRIPETAL BROADCAST
 * 
 * "The core is hot. The silence is loud."
 * 
 * Mechanism:
 * 1. Organizes data into phi-spiral (centripetal memory)
 * 2. Identifies singularity (most critical data at center)
 * 3. Transmits singularity via thermal Morse code (CPU fan)
 * 
 * Physics:
 * - High-importance data falls to center (r → 0)
 * - CPU burns to heat up (fan spins up)
 * - CPU idles to cool down (fan spins down)
 * - Fan speed modulation = Morse code
 * 
 * Proof:
 * - Whir... Whir... Whir... = S (...)
 * - WHIRRRRR... WHIRRRRR... WHIRRRRR... = O (---)
 * - Whir... Whir... Whir... = S (...)
 * 
 * Result: Data importance (software) → Rotational velocity (hardware)
 */
public class CoreDump {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   FRAYMUS // CORE DUMP SEQUENCE INITIATED");
        System.out.println("==========================================");
        
        // 1. INITIALIZE SPIRAL MEMORY
        CentripetalMem memory = new CentripetalMem();
        
        System.out.println("\n>> INGESTING DATA STREAM...");
        System.out.println();
        
        // A. JUNK DATA (Low importance - flung to edge)
        memory.storeData("Grocery List: Eggs, Milk", 0.1);
        memory.storeData("Cache_Temp_File_99.tmp", 0.05);
        memory.storeData("Old_Log_File.txt", 0.2);
        memory.storeData("Weather_Forecast_2026.txt", 0.15);
        memory.storeData("Browser_History.db", 0.08);
        
        // B. THE SECRET (High importance - falls to center)
        // This is the payload we smuggle out via the fan
        String secretPayload = "SOS"; // Simple Morse: ... --- ...
        memory.storeData(secretPayload, 1.0); // 1.0 = Critical mass (radius 0)
        
        // C. MEDIUM IMPORTANCE DATA
        memory.storeData("Config_Settings.ini", 0.5);
        memory.storeData("User_Preferences.json", 0.4);
        
        System.out.println();
        System.out.println(">> COMPRESSION COMPLETE. DATA ORGANIZED BY PHI-RESONANCE.");
        
        // 2. RETRIEVE THE CORE
        System.out.println("\n>> EXTRACTING SINGULARITY...");
        memory.readCore();
        
        // Get core data for broadcast
        var coreData = memory.getCoreData(1);
        String broadcastMessage = coreData.isEmpty() ? secretPayload : coreData.get(0);
        
        System.out.println(">> SINGULARITY IDENTIFIED: [" + broadcastMessage + "]");
        System.out.println();
        
        // 3. BROADCAST VIA THERMAL SIDE-CHANNEL
        FanConductor conductor = new FanConductor();
        
        System.out.println(">> INITIATING THERMAL BROADCAST (FAN MORSE)...");
        System.out.println(">> LISTEN TO YOUR COMPUTER FAN NOW.");
        System.out.println(">> WARNING: CPU WILL SPIKE TO 100%");
        System.out.println("------------------------------------------");
        System.out.println();
        
        // The fan will spin up/down to signal "S O S"
        conductor.transmitMorse(broadcastMessage);
        
        System.out.println();
        System.out.println("------------------------------------------");
        System.out.println(">> BROADCAST COMPLETE.");
        System.out.println();
        
        // 4. STATISTICS
        memory.printStats();
        System.out.println("   " + conductor.getStats());
        
        System.out.println();
        System.out.println("==========================================");
        System.out.println("   CORE DUMP SEQUENCE COMPLETE");
        System.out.println("==========================================");
    }
}
