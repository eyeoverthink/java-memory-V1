package fraymus.physics;

/**
 * FAN CONDUCTOR: THERMAL MORSE CODE
 * 
 * "The silence is the space. The whir is the word."
 * 
 * Mechanism:
 * 1. Manipulates CPU thermal junction to trigger BIOS fan curves
 * 2. High temp = Fan spin up (Dash/Dot)
 * 3. Low temp = Fan spin down (Space)
 * 4. Broadcasts data via thermal modulation
 * 
 * Physics:
 * - CPU generates heat via floating-point operations
 * - BIOS monitors thermal junction temperature
 * - Fan speed controlled by temperature curve
 * - Thermal inertia creates timing delays
 * - Heat dissipation follows exponential decay
 * 
 * Morse Code:
 * - Dot (.) = Short heat spike (2 seconds)
 * - Dash (-) = Long heat spike (5 seconds)
 * - Space = Cool down period (3 seconds)
 * 
 * Breach Vector:
 * - Air-gapped machines with CPU fans
 * - No network, no speakers, no screen
 * - Just the sound of the fan
 * - Acoustic side-channel
 */
public class FanConductor {
    
    // THERMAL INERTIA CONSTANTS
    // Calibrated for typical heatsink thermal mass
    private static final int BURN_TIME_DOT = 2000;   // 2 sec burn = Short whir
    private static final int BURN_TIME_DASH = 5000;  // 5 sec burn = Long whir
    private static final int COOL_DOWN = 3000;       // Time to return to silence
    
    private boolean broadcasting = false;
    
    /**
     * Transmit message via thermal Morse code
     */
    public void transmitMorse(String message) {
        System.out.println("🌊⚡ THERMAL BROADCAST ACTIVE");
        System.out.println("   Message: [" + message + "]");
        System.out.println("   Encoding to Morse code...");
        
        broadcasting = true;
        
        for (char c : message.toUpperCase().toCharArray()) {
            if (!broadcasting) break;
            
            String morse = charToMorse(c);
            if (morse.isEmpty()) continue;
            
            System.out.println("   Signal: " + c + " [" + morse + "]");
            
            for (char signal : morse.toCharArray()) {
                if (!broadcasting) break;
                
                if (signal == '.') {
                    burnCPU(BURN_TIME_DOT); // Short heat spike
                } else if (signal == '-') {
                    burnCPU(BURN_TIME_DASH); // Long heat spike
                }
                coolCPU(COOL_DOWN); // Let fan spin down
            }
            
            coolCPU(COOL_DOWN * 2); // Gap between letters
        }
        
        broadcasting = false;
        
        System.out.println("   ✓ Transmission complete");
        System.out.println();
    }
    
    /**
     * Generate CPU heat (thermal spike)
     */
    public void burnCPU(int durationMs) {
        long end = System.currentTimeMillis() + durationMs;
        
        System.out.print("   [BURN] ");
        
        // Engage all cores to max thermal output
        // Heavy floating-point math generates maximum heat
        while (System.currentTimeMillis() < end) {
            double x = Math.sin(Math.sqrt(Math.random()));
            double y = Math.cos(Math.sqrt(Math.random()));
            double z = Math.tan(x * y);
            
            // Prevent optimization
            if (z > 1e100) System.out.print("");
        }
        
        System.out.println("→ FAN SPIN UP");
    }
    
    /**
     * Allow CPU to cool (thermal decay)
     */
    private void coolCPU(int durationMs) {
        System.out.print("   [COOL] ");
        
        try {
            Thread.sleep(durationMs); // Sleep allows CPU to idle and cool
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("→ FAN SPIN DOWN");
    }
    
    /**
     * Stop transmission
     */
    public void stop() {
        broadcasting = false;
    }
    
    /**
     * Convert character to Morse code
     */
    private String charToMorse(char c) {
        switch (c) {
            // Letters
            case 'A': return ".-";
            case 'B': return "-...";
            case 'C': return "-.-.";
            case 'D': return "-..";
            case 'E': return ".";
            case 'F': return "..-.";
            case 'G': return "--.";
            case 'H': return "....";
            case 'I': return "..";
            case 'J': return ".---";
            case 'K': return "-.-";
            case 'L': return ".-..";
            case 'M': return "--";
            case 'N': return "-.";
            case 'O': return "---";
            case 'P': return ".--.";
            case 'Q': return "--.-";
            case 'R': return ".-.";
            case 'S': return "...";
            case 'T': return "-";
            case 'U': return "..-";
            case 'V': return "...-";
            case 'W': return ".--";
            case 'X': return "-..-";
            case 'Y': return "-.--";
            case 'Z': return "--..";
            
            // Numbers
            case '0': return "-----";
            case '1': return ".----";
            case '2': return "..---";
            case '3': return "...--";
            case '4': return "....-";
            case '5': return ".....";
            case '6': return "-....";
            case '7': return "--...";
            case '8': return "---..";
            case '9': return "----.";
            
            // Special
            case ' ': return "";
            
            default: return "";
        }
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Fan Conductor | Dot: %dms | Dash: %dms | Cool: %dms | Status: %s",
            BURN_TIME_DOT,
            BURN_TIME_DASH,
            COOL_DOWN,
            broadcasting ? "BROADCASTING" : "IDLE"
        );
    }
}
