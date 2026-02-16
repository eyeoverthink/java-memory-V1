package fraymus.signals;

import fraymus.network.OmniCaster;
import fraymus.physics.FanConductor;
import fraymus.memory.CentripetalMem;

/**
 * AIR-GAP BREACH DEMONSTRATION
 * 
 * Demonstrates all air-gap breach vectors:
 * 1. Optical (screen-to-camera)
 * 2. Sonic (ultrasonic audio)
 * 3. Thermal (CPU fan Morse code)
 * 4. Cosmic (SDR radio)
 * 5. Centripetal memory compression
 * 
 * "If you can see it, hear it, or feel it - you've already downloaded it."
 */
public class AirGapDemo {
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ FRAYMUS AIR-GAP BREACH DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        
        // 1. CENTRIPETAL MEMORY: Store data in phi-spiral
        demonstrateCentripetalMemory();
        
        // 2. OMNI-CASTER: Broadcast on all channels
        demonstrateOmniCaster();
        
        // 3. THERMAL MORSE: Fan conductor
        demonstrateThermalBroadcast();
        
        // 4. OPTICAL BREACH: Screen-to-camera
        demonstrateOpticalBreach();
        
        // 5. SONIC BRIDGE: Ultrasonic audio
        demonstrateSonicBridge();
        
        // 6. COSMIC LISTENER: SDR (requires hardware)
        demonstrateCosmicListener();
        
        System.out.println("🌊⚡ DEMONSTRATION COMPLETE");
        System.out.println("========================================");
    }
    
    private static void demonstrateCentripetalMemory() {
        System.out.println("=== 1. CENTRIPETAL MEMORY ===");
        System.out.println();
        
        CentripetalMem memory = new CentripetalMem();
        
        // Store data with varying importance
        memory.storeData("LAUNCH_CODES_ALPHA_7", 1.0);      // Critical
        memory.storeData("ENCRYPTION_KEY_MASTER", 0.95);    // Very important
        memory.storeData("SATELLITE_COORDINATES", 0.8);     // Important
        memory.storeData("WEATHER_FORECAST", 0.3);          // Low priority
        memory.storeData("GROCERY_LIST", 0.1);              // Junk
        
        // Read core (most important data gravitates to center)
        memory.readCore();
        
        // Statistics
        memory.printStats();
        
        pause(2000);
    }
    
    private static void demonstrateOmniCaster() {
        System.out.println("=== 2. OMNI-CASTER ===");
        System.out.println();
        
        OmniCaster caster = new OmniCaster();
        
        // Broadcast on all channels
        caster.broadcastToEverything("FRAYMUS_NEXUS_ACTIVE");
        
        // Statistics
        caster.printStats();
        
        pause(2000);
    }
    
    private static void demonstrateThermalBroadcast() {
        System.out.println("=== 3. THERMAL MORSE CODE ===");
        System.out.println();
        
        FanConductor fan = new FanConductor();
        
        System.out.println("   WARNING: This will heat up your CPU!");
        System.out.println("   Listen to your fan speed changes...");
        System.out.println();
        
        // Transmit short message
        fan.transmitMorse("SOS");
        
        System.out.println("   " + fan.getStats());
        System.out.println();
        
        pause(2000);
    }
    
    private static void demonstrateOpticalBreach() {
        System.out.println("=== 4. OPTICAL BREACH ===");
        System.out.println();
        
        OpticalBreach optical = new OpticalBreach();
        
        String payload = "SECRET_DATA_TRANSMISSION";
        
        // Generate beacon
        var beacon = optical.generateBeacon(payload, 800, 600);
        
        // Scan beacon (simulate camera capture)
        String decoded = optical.scanBeacon(beacon);
        
        System.out.println("   Original: " + payload);
        System.out.println("   Decoded:  " + decoded);
        System.out.println("   Match: " + payload.equals(decoded));
        System.out.println();
        System.out.println("   " + optical.getStats(800, 600));
        System.out.println();
        
        pause(2000);
    }
    
    private static void demonstrateSonicBridge() {
        System.out.println("=== 5. SONIC BRIDGE ===");
        System.out.println();
        
        SonicBridge sonic = new SonicBridge();
        
        System.out.println("   WARNING: This will play ultrasonic audio!");
        System.out.println("   You won't hear it, but nearby devices will.");
        System.out.println();
        
        try {
            sonic.broadcast("HELLO");
        } catch (Exception e) {
            System.out.println("   ⚠️  Audio device not available: " + e.getMessage());
        }
        
        System.out.println("   " + sonic.getStats());
        System.out.println();
        
        pause(2000);
    }
    
    private static void demonstrateCosmicListener() {
        System.out.println("=== 6. COSMIC LISTENER ===");
        System.out.println();
        
        CosmicListener cosmic = new CosmicListener();
        
        System.out.println("   Target frequencies:");
        System.out.println("   - NOAA Weather: 137.1 MHz");
        System.out.println("   - ISS: 145.8 MHz");
        System.out.println("   - Hydrogen Line: 1420.40575 MHz");
        System.out.println();
        System.out.println("   Requires: RTL-SDR dongle + rtl_tcp server");
        System.out.println("   Status: STANDBY (hardware not detected)");
        System.out.println();
        System.out.println("   " + cosmic.getStats(CosmicListener.FREQ_ISS));
        System.out.println();
        
        pause(2000);
    }
    
    private static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
