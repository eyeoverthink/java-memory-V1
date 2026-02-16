package test.tempest;

import java.lang.Math;

/**
 * TEMPEST TRANSMITTER TEST
 * 
 * Isolated test environment for System Bus Radio transmission.
 * 
 * This app generates EM radiation via CPU oscillation.
 * Run this alongside ReceiverTest to test air-gap communication.
 * 
 * Usage:
 * 1. Compile: javac TransmitterTest.java
 * 2. Run: java test.tempest.TransmitterTest
 * 3. Monitor with AM radio or ReceiverTest app
 * 
 * WARNING: Generates CPU heat. Use short bursts.
 */
public class TransmitterTest {
    
    private static final double PHI = 1.6180339887;
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ TEMPEST TRANSMITTER TEST");
        System.out.println("=".repeat(60));
        System.out.println();
        
        TransmitterTest transmitter = new TransmitterTest();
        
        // Test 1: Low frequency (1 kHz)
        System.out.println("TEST 1: Low Frequency Transmission");
        transmitter.transmit(1000.0, "10101010");
        
        System.out.println();
        
        // Test 2: Mid frequency (10 kHz)
        System.out.println("TEST 2: Mid Frequency Transmission");
        transmitter.transmit(10000.0, "11001100");
        
        System.out.println();
        
        // Test 3: High frequency (30 kHz)
        System.out.println("TEST 3: High Frequency Transmission");
        transmitter.transmit(30000.0, "11110000");
        
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("TRANSMISSION COMPLETE");
        System.out.println();
        System.out.println("Monitor with:");
        System.out.println("  - AM radio tuned to test frequency");
        System.out.println("  - ReceiverTest app");
        System.out.println("  - Spectrum analyzer");
    }
    
    /**
     * Transmit binary message at specified frequency
     * 
     * @param frequencyHz Target frequency (Hz)
     * @param message Binary message (e.g., "10110101")
     */
    public void transmit(double frequencyHz, String message) {
        System.out.println("  Frequency: " + frequencyHz + " Hz");
        System.out.println("  Message: " + message);
        System.out.println("  Bit count: " + message.length());
        System.out.println();
        
        // Calculate period
        long periodNs = (long) (1_000_000_000.0 / frequencyHz);
        long halfPeriod = periodNs / 2;
        
        System.out.println("  Period: " + periodNs + " ns");
        System.out.println("  Half period: " + halfPeriod + " ns");
        System.out.println();
        
        System.out.print("  Transmitting: ");
        
        for (int i = 0; i < message.length(); i++) {
            char bit = message.charAt(i);
            
            if (bit == '1') {
                // Bit 1: Oscillate (carrier ON)
                oscillate(halfPeriod, 100_000_000); // 100ms per bit
                System.out.print("█");
            } else {
                // Bit 0: Silence (carrier OFF)
                silence(100); // 100ms per bit
                System.out.print("░");
            }
        }
        
        System.out.println(" DONE");
    }
    
    /**
     * Oscillate CPU at specified frequency
     * 
     * @param halfPeriodNs Half period in nanoseconds
     * @param durationNs Duration to oscillate
     */
    private void oscillate(long halfPeriodNs, long durationNs) {
        long endTime = System.nanoTime() + durationNs;
        
        while (System.nanoTime() < endTime) {
            // HIGH phase (CPU burn)
            long start = System.nanoTime();
            while (System.nanoTime() - start < halfPeriodNs) {
                // Burn CPU cycles - creates high voltage on power bus
                Math.sin(PHI);
                Math.cos(PHI);
                Math.sqrt(PHI);
                Math.pow(PHI, 2);
            }
            
            // LOW phase (idle)
            start = System.nanoTime();
            while (System.nanoTime() - start < halfPeriodNs) {
                // Idle - creates low voltage on power bus
                // This creates the square wave pattern
            }
        }
    }
    
    /**
     * Silence (no carrier)
     * 
     * @param durationMs Duration in milliseconds
     */
    private void silence(long durationMs) {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
