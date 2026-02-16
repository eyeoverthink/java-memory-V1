package fraymus.physics;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * THE FAN CONDUCTOR: THERMAL MORSE CODE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The silence is the space. The whir is the word."
 * 
 * Manipulates CPU temperature to trigger BIOS fan curves.
 * - High Temp = Fan Spin Up (Dash/Dot)
 * - Low Temp = Fan Spin Down (Space)
 * 
 * The Physics:
 * - Heavy floating point math generates maximum heat
 * - Sleep allows CPU to idle and cool
 * - BIOS fan curves respond to temperature changes
 * - Fan speed modulation becomes audible Morse code
 * 
 * Even airgapped systems have fans. If it has a fan, it can speak.
 */
public class FanConductor {

    private static final double PHI = PhiQuantumConstants.PHI;

    // Thermal inertia constants (calibrate to your heatsink)
    private int burnTimeDot = 2000;    // 2 sec burn = Short Whir (.)
    private int burnTimeDash = 5000;   // 5 sec burn = Long Whir (-)
    private int coolDownTime = 3000;   // Time to return to silence
    private int letterGap = 4000;      // Gap between letters
    private int wordGap = 7000;        // Gap between words
    
    // State
    private AtomicBoolean isBroadcasting = new AtomicBoolean(false);
    private AtomicBoolean shouldStop = new AtomicBoolean(false);
    private Thread broadcastThread;
    
    // CPU burn intensity (number of threads)
    private int burnThreads = Runtime.getRuntime().availableProcessors();
    
    // Morse code lookup
    private static final Map<Character, String> MORSE_CODE = new HashMap<>();
    
    static {
        // Letters
        MORSE_CODE.put('A', ".-");
        MORSE_CODE.put('B', "-...");
        MORSE_CODE.put('C', "-.-.");
        MORSE_CODE.put('D', "-..");
        MORSE_CODE.put('E', ".");
        MORSE_CODE.put('F', "..-.");
        MORSE_CODE.put('G', "--.");
        MORSE_CODE.put('H', "....");
        MORSE_CODE.put('I', "..");
        MORSE_CODE.put('J', ".---");
        MORSE_CODE.put('K', "-.-");
        MORSE_CODE.put('L', ".-..");
        MORSE_CODE.put('M', "--");
        MORSE_CODE.put('N', "-.");
        MORSE_CODE.put('O', "---");
        MORSE_CODE.put('P', ".--.");
        MORSE_CODE.put('Q', "--.-");
        MORSE_CODE.put('R', ".-.");
        MORSE_CODE.put('S', "...");
        MORSE_CODE.put('T', "-");
        MORSE_CODE.put('U', "..-");
        MORSE_CODE.put('V', "...-");
        MORSE_CODE.put('W', ".--");
        MORSE_CODE.put('X', "-..-");
        MORSE_CODE.put('Y', "-.--");
        MORSE_CODE.put('Z', "--..");
        
        // Numbers
        MORSE_CODE.put('0', "-----");
        MORSE_CODE.put('1', ".----");
        MORSE_CODE.put('2', "..---");
        MORSE_CODE.put('3', "...--");
        MORSE_CODE.put('4', "....-");
        MORSE_CODE.put('5', ".....");
        MORSE_CODE.put('6', "-....");
        MORSE_CODE.put('7', "--...");
        MORSE_CODE.put('8', "---..");
        MORSE_CODE.put('9', "----.");
        
        // Special characters
        MORSE_CODE.put('.', ".-.-.-");
        MORSE_CODE.put(',', "--..--");
        MORSE_CODE.put('?', "..--..");
        MORSE_CODE.put('!', "-.-.--");
        MORSE_CODE.put(' ', " ");  // Word separator
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE BROADCAST
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Transmit message as thermal Morse code
     */
    public void transmitMorse(String message) {
        if (isBroadcasting.get()) {
            System.out.println("Already broadcasting!");
            return;
        }
        
        isBroadcasting.set(true);
        shouldStop.set(false);
        
        broadcastThread = new Thread(() -> {
            System.out.println("═══ INITIATING THERMAL BROADCAST ═══");
            System.out.println("Message: " + message);
            System.out.println("Using " + burnThreads + " CPU threads for thermal load");
            System.out.println("Dot: " + burnTimeDot + "ms | Dash: " + burnTimeDash + "ms");
            System.out.println();
            
            String upperMessage = message.toUpperCase();
            
            for (int i = 0; i < upperMessage.length() && !shouldStop.get(); i++) {
                char c = upperMessage.charAt(i);
                String morse = MORSE_CODE.getOrDefault(c, "");
                
                if (morse.isEmpty()) {
                    System.out.println("Skipping unknown char: " + c);
                    continue;
                }
                
                System.out.print("Signal: " + c + " [" + morse + "] ");
                
                if (c == ' ') {
                    // Word gap
                    System.out.println("(word gap)");
                    coolCPU(wordGap);
                } else {
                    // Transmit each dot/dash
                    for (char signal : morse.toCharArray()) {
                        if (shouldStop.get()) break;
                        
                        if (signal == '.') {
                            System.out.print("·");
                            burnCPU(burnTimeDot);
                        } else if (signal == '-') {
                            System.out.print("—");
                            burnCPU(burnTimeDash);
                        }
                        coolCPU(coolDownTime);
                    }
                    System.out.println();
                    
                    // Letter gap
                    coolCPU(letterGap);
                }
            }
            
            System.out.println();
            System.out.println(">> THERMAL BROADCAST COMPLETE.");
            isBroadcasting.set(false);
            
        }, "FanConductor-Broadcast");
        
        broadcastThread.start();
    }

    /**
     * Transmit raw binary data
     */
    public void transmitBinary(byte[] data) {
        if (isBroadcasting.get()) {
            System.out.println("Already broadcasting!");
            return;
        }
        
        isBroadcasting.set(true);
        shouldStop.set(false);
        
        broadcastThread = new Thread(() -> {
            System.out.println("═══ THERMAL BINARY BROADCAST ═══");
            System.out.println("Data size: " + data.length + " bytes");
            
            // Sync pulse (long burn to signal start)
            System.out.println(">> Sending sync pulse...");
            burnCPU(burnTimeDash * 2);
            coolCPU(coolDownTime);
            
            int bitCount = 0;
            for (byte b : data) {
                if (shouldStop.get()) break;
                
                for (int bit = 7; bit >= 0 && !shouldStop.get(); bit--) {
                    boolean isOne = ((b >> bit) & 1) == 1;
                    
                    if (isOne) {
                        burnCPU(burnTimeDash);
                    } else {
                        burnCPU(burnTimeDot);
                    }
                    coolCPU(coolDownTime);
                    bitCount++;
                    
                    if (bitCount % 8 == 0) {
                        System.out.print(".");
                    }
                }
            }
            
            System.out.println();
            System.out.println(">> Transmitted " + bitCount + " bits.");
            isBroadcasting.set(false);
            
        }, "FanConductor-Binary");
        
        broadcastThread.start();
    }

    /**
     * Stop current broadcast
     */
    public void stopBroadcast() {
        shouldStop.set(true);
        System.out.println(">> Stopping thermal broadcast...");
    }

    // ═══════════════════════════════════════════════════════════════════
    // PHYSICS: THE BURN (Generate Heat)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Burn CPU to generate heat
     */
    private void burnCPU(int durationMs) {
        long endTime = System.currentTimeMillis() + durationMs;
        
        // Spawn multiple threads to maximize thermal output
        Thread[] burners = new Thread[burnThreads];
        AtomicBoolean stopBurn = new AtomicBoolean(false);
        
        for (int t = 0; t < burnThreads; t++) {
            final int threadId = t;
            burners[t] = new Thread(() -> {
                double accumulator = threadId * PHI;
                
                while (System.currentTimeMillis() < endTime && !stopBurn.get() && !shouldStop.get()) {
                    // Heavy floating point math generates maximum heat
                    for (int i = 0; i < 10000; i++) {
                        accumulator += Math.sin(Math.sqrt(Math.random() * PHI));
                        accumulator *= Math.cos(accumulator * PHI);
                        accumulator = Math.sqrt(Math.abs(accumulator) + 1);
                    }
                }
                
                // Prevent compiler optimization
                if (accumulator == Double.NaN) {
                    System.out.println("NaN");
                }
            }, "Burner-" + t);
            burners[t].start();
        }
        
        // Wait for all burners
        for (Thread burner : burners) {
            try {
                burner.join();
            } catch (InterruptedException e) {
                stopBurn.set(true);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // PHYSICS: THE COOL (Dissipate Heat)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Let CPU cool down (idle)
     */
    private void coolCPU(int durationMs) {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            // Interrupted
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    
    public void setBurnTimeDot(int ms) { this.burnTimeDot = ms; }
    public void setBurnTimeDash(int ms) { this.burnTimeDash = ms; }
    public void setCoolDownTime(int ms) { this.coolDownTime = ms; }
    public void setLetterGap(int ms) { this.letterGap = ms; }
    public void setWordGap(int ms) { this.wordGap = ms; }
    public void setBurnThreads(int threads) { this.burnThreads = threads; }
    
    public boolean isBroadcasting() { return isBroadcasting.get(); }

    /**
     * Calculate transmission time for a message
     */
    public int estimateTransmissionTime(String message) {
        int totalMs = 0;
        String upper = message.toUpperCase();
        
        for (char c : upper.toCharArray()) {
            String morse = MORSE_CODE.getOrDefault(c, "");
            
            if (c == ' ') {
                totalMs += wordGap;
            } else {
                for (char signal : morse.toCharArray()) {
                    totalMs += (signal == '.') ? burnTimeDot : burnTimeDash;
                    totalMs += coolDownTime;
                }
                totalMs += letterGap;
            }
        }
        
        return totalMs;
    }

    /**
     * Get status info
     */
    public String getStatus() {
        return String.format(
            "═══ FAN CONDUCTOR STATUS ═══%n" +
            "Broadcasting: %s%n" +
            "Burn Threads: %d%n" +
            "Dot Duration: %d ms%n" +
            "Dash Duration: %d ms%n" +
            "Cool Down: %d ms%n" +
            "Letter Gap: %d ms%n" +
            "Word Gap: %d ms",
            isBroadcasting.get(), burnThreads,
            burnTimeDot, burnTimeDash, coolDownTime, letterGap, wordGap
        );
    }

    /**
     * Convert message to Morse code string (for display)
     */
    public static String toMorseString(String message) {
        StringBuilder morse = new StringBuilder();
        
        for (char c : message.toUpperCase().toCharArray()) {
            String code = MORSE_CODE.getOrDefault(c, "?");
            morse.append(code).append(" ");
        }
        
        return morse.toString().trim();
    }
}
