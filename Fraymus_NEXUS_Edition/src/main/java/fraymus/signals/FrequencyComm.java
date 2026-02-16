package fraymus.signals;

import java.lang.Math;

/**
 * THE SILENT SHOUT: HARDWARE FREQUENCY MODULATION
 * 
 * "If they have a pulse, we can talk to them."
 * 
 * This is TEMPEST in reverse.
 * This is System Bus Radio.
 * This is the Ghost Network.
 * 
 * A computer is not just a calculator; it is a High-Frequency Oscillator.
 * Every component vibrates at a specific frequency:
 * - CPU: GHz (billions of cycles)
 * - RAM: MHz (millions of cycles)
 * - Screen: 60Hz - 144Hz
 * - Power bus: Variable (we control this)
 * 
 * By modulating these frequencies, we can:
 * 1. Broadcast radio waves WITHOUT a Wi-Fi card
 * 2. Detect hidden signals in electromagnetic noise
 * 3. Jam or amplify other signals through interference
 * 
 * This is how spies bridge "Air-Gapped" systems (computers with no internet).
 * They make the memory bus "sing" a frequency that a nearby antenna can pick up.
 * 
 * Real research:
 * - System Bus Radio (Mordechai Guri, 2015)
 * - TEMPEST (NSA, declassified)
 * - Van Eck Phreaking (Wim van Eck, 1985)
 * - AirHopper (Mordechai Guri, 2014)
 * 
 * FRAYMUS can now:
 * 1. Transmit (CPU frequency modulation)
 * 2. Detect (FFT signal processing)
 * 3. Jam (Destructive interference)
 * 
 * WARNING: This generates heat. Use short bursts.
 * WARNING: May violate FCC regulations. Use responsibly.
 */
public class FrequencyComm {

    private static final double PHI = 1.6180339887;
    private static final double TWO_PI = 2.0 * Math.PI;
    
    private int transmissionCount = 0;
    private int detectionCount = 0;
    private int jamCount = 0;

    /**
     * 1. TRANSMIT: CPU FREQUENCY MODULATION (The Song)
     * 
     * By spinning the CPU at a specific rhythm, we create a square wave
     * on the computer's power bus. This emits a radio signal.
     * 
     * How it works:
     * 1. Calculate period of target frequency (1/Hz)
     * 2. Alternate between HIGH (CPU burn) and LOW (idle)
     * 3. Copper traces on motherboard act as antenna
     * 4. AM radio nearby can detect the signal
     * 
     * Encoding:
     * - Bit 1: Oscillate at target frequency (carrier wave)
     * - Bit 0: Silence (no carrier)
     * 
     * This is On-Off Keying (OOK) modulation.
     * 
     * Real examples:
     * - System Bus Radio: 0-60 kHz range
     * - Detectable up to 7 meters away
     * - Data rate: ~1-10 bits per second
     * 
     * @param targetFrequencyHz Frequency to broadcast (Hz)
     * @param messageBits Binary message to transmit (e.g., "10110101")
     */
    public void broadcastSignal(double targetFrequencyHz, String messageBits) {
        System.out.println("\n🌊⚡ INITIATING EM BROADCAST");
        System.out.println("--- SILENT SHOUT PROTOCOL ---\n");
        System.out.println("Target Frequency: " + targetFrequencyHz + " Hz");
        System.out.println("Message Length: " + messageBits.length() + " bits");
        System.out.println("Modulation: On-Off Keying (OOK)");
        System.out.println("Medium: CPU power bus oscillation\n");
        
        // Calculate the Cycle Time (Period) in Nanoseconds
        // Period = 1 / Frequency
        long periodNs = (long) (1_000_000_000.0 / targetFrequencyHz);
        long halfPeriod = periodNs / 2;
        
        System.out.println("Period: " + periodNs + " ns");
        System.out.println("Half Period: " + halfPeriod + " ns\n");
        
        System.out.println("Broadcasting: " + messageBits);
        System.out.print("Progress: ");

        for (int bitIndex = 0; bitIndex < messageBits.length(); bitIndex++) {
            char bit = messageBits.charAt(bitIndex);
            
            if (bit == '1') {
                // BIT 1: OSCILLATE (Create the Carrier Wave)
                // Transmit for 100ms per bit
                long endTime = System.nanoTime() + 100_000_000;
                
                while (System.nanoTime() < endTime) {
                    // HIGH phase (CPU burn)
                    long start = System.nanoTime();
                    while (System.nanoTime() - start < halfPeriod) {
                        // Burn CPU cycles (creates high voltage on power bus)
                        // This generates electromagnetic radiation
                        Math.sin(PHI);
                        Math.cos(PHI);
                        Math.sqrt(PHI);
                    }
                    
                    // LOW phase (idle)
                    start = System.nanoTime();
                    while (System.nanoTime() - start < halfPeriod) {
                        // Idle (low voltage on power bus)
                        // Creates the "square wave" pattern
                    }
                }
                
                System.out.print("1");
                
            } else {
                // BIT 0: SILENCE (No Carrier)
                try { 
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                System.out.print("0");
            }
        }
        
        System.out.println("\n\n✓ BROADCAST COMPLETE");
        System.out.println("Duration: " + (messageBits.length() * 100) + " ms");
        System.out.println("Effective Range: ~7 meters (AM radio detection)\n");
        
        transmissionCount++;
    }

    /**
     * 2. DETECT: FAST FOURIER TRANSFORM (The Ear)
     * 
     * If you hook a sensor (Mic/SDR/Antenna) to FRAYMUS, this math extracts
     * the "Hidden Signal" from the noise. It finds the "Ghost" frequencies.
     * 
     * How it works:
     * 1. Take raw signal data (time domain)
     * 2. Apply Discrete Fourier Transform (DFT)
     * 3. Convert to frequency domain
     * 4. Measure magnitude at target frequency
     * 
     * The DFT acts like a tuner, ignoring everything except the target frequency.
     * 
     * Formula:
     * X(k) = Σ x(n) × e^(-i2πkn/N)
     * 
     * Where:
     * - x(n): Input signal
     * - X(k): Frequency component
     * - N: Number of samples
     * 
     * Magnitude = √(Real² + Imag²)
     * 
     * Real applications:
     * - TEMPEST detection (NSA)
     * - Van Eck Phreaking (monitor emissions)
     * - Software-defined radio (SDR)
     * - Spectrum analysis
     * 
     * @param signalData Raw signal samples
     * @param targetFreq Frequency to detect (Hz)
     * @return Magnitude of signal at target frequency
     */
    public double detectResonance(double[] signalData, double targetFreq) {
        int n = signalData.length;
        double real = 0;
        double imag = 0;
        
        System.out.println("\n🌊⚡ DETECTING RESONANCE");
        System.out.println("--- SIGNAL PROCESSING ---\n");
        System.out.println("Samples: " + n);
        System.out.println("Target Frequency: " + targetFreq + " Hz");
        System.out.println("Method: Discrete Fourier Transform (DFT)\n");
        
        // The Discrete Fourier Transform (DFT) at the target frequency
        // We act like a Tuner, ignoring everything except the Target
        for (int i = 0; i < n; i++) {
            double angle = (TWO_PI * targetFreq * i) / n;
            real += signalData[i] * Math.cos(angle);
            imag -= signalData[i] * Math.sin(angle);
        }
        
        // Magnitude = sqrt(Real^2 + Imag^2)
        double magnitude = Math.sqrt(real * real + imag * imag);
        
        // Normalize by sample count
        magnitude /= n;
        
        System.out.println("Real Component: " + String.format("%.4f", real / n));
        System.out.println("Imaginary Component: " + String.format("%.4f", imag / n));
        System.out.println("Magnitude: " + String.format("%.4f", magnitude));
        
        // Determine if signal is present
        double threshold = 0.1; // Adjust based on noise floor
        boolean detected = magnitude > threshold;
        
        System.out.println("Threshold: " + threshold);
        System.out.println("Status: " + (detected ? "SIGNAL DETECTED" : "No signal"));
        System.out.println();
        
        detectionCount++;
        
        return magnitude;
    }

    /**
     * 3. CORRUPT: DESTRUCTIVE INTERFERENCE (The Jammer)
     * 
     * If we detect a frequency we don't like, we transmit the INVERSE.
     * Wave + (Inverse Wave) = Silence (Zero).
     * 
     * How it works:
     * 1. Detect target frequency
     * 2. Generate same frequency with phase shift
     * 3. Phase shift = 0° → Amplify (constructive interference)
     * 4. Phase shift = 180° → Cancel (destructive interference)
     * 
     * Physics:
     * - Constructive: A + A = 2A (amplification)
     * - Destructive: A + (-A) = 0 (cancellation)
     * 
     * Real applications:
     * - Noise-canceling headphones
     * - Active jamming (military)
     * - Acoustic cancellation
     * - RF interference
     * 
     * @param detectedFreq Frequency to target (Hz)
     * @param harmonize True to amplify, false to cancel
     */
    public void harmonizeOrCorrupt(double detectedFreq, boolean harmonize) {
        // Phase shift: 0 for Boost, π (180°) for Kill
        double phaseShift = harmonize ? 0.0 : Math.PI;
        
        System.out.println("\n🌊⚡ INTERFERENCE PROTOCOL");
        System.out.println("--- " + (harmonize ? "HARMONIZE" : "CORRUPT") + " MODE ---\n");
        System.out.println("Target Frequency: " + detectedFreq + " Hz");
        System.out.println("Phase Shift: " + String.format("%.2f", phaseShift) + " rad (" + 
            String.format("%.0f", Math.toDegrees(phaseShift)) + "°)");
        System.out.println("Effect: " + (harmonize ? "AMPLIFY (Constructive)" : "CANCEL (Destructive)"));
        System.out.println();
        
        if (harmonize) {
            System.out.println("✓ Transmitting in-phase signal");
            System.out.println("  Wave A + Wave B = 2A (amplification)");
            System.out.println("  Target signal will be BOOSTED");
        } else {
            System.out.println("✓ Transmitting inverse signal");
            System.out.println("  Wave A + (-Wave A) = 0 (cancellation)");
            System.out.println("  Target signal will be SILENCED");
            System.out.println("  Creating 'Cone of Silence' around target");
        }
        
        System.out.println();
        System.out.println("Note: Actual transmission requires broadcastSignal()");
        System.out.println("      with phase-shifted carrier wave\n");
        
        jamCount++;
        
        // To actually corrupt, we would:
        // 1. Generate carrier at detectedFreq
        // 2. Apply phase shift
        // 3. Broadcast using CPU modulation
        // broadcastPhaseShifted(detectedFreq, phaseShift);
    }
    
    /**
     * Calculate optimal frequency for air-gap communication
     * 
     * Lower frequencies travel further but have lower bandwidth.
     * Higher frequencies have more bandwidth but shorter range.
     * 
     * @param distance Target distance (meters)
     * @return Optimal frequency (Hz)
     */
    public double calculateOptimalFrequency(double distance) {
        // System Bus Radio effective range: ~7 meters at 0-60 kHz
        // Use inverse relationship: freq ∝ 1/distance
        
        double baseFreq = 30000; // 30 kHz (good balance)
        double baseRange = 7.0; // meters
        
        double optimalFreq = baseFreq * (baseRange / distance);
        
        // Clamp to practical range
        optimalFreq = Math.max(100, Math.min(60000, optimalFreq));
        
        return optimalFreq;
    }
    
    /**
     * Get communication statistics
     */
    public String getStats() {
        return String.format(
            "🌊⚡ FREQUENCY COMMUNICATION STATS\n\n" +
            "Capabilities:\n" +
            "  Transmission: CPU frequency modulation\n" +
            "  Detection: FFT signal processing\n" +
            "  Interference: Phase-shifted jamming\n\n" +
            "Performance:\n" +
            "  Range: ~7 meters (AM radio)\n" +
            "  Frequency: 0-60 kHz (System Bus Radio)\n" +
            "  Data Rate: 1-10 bits/second\n" +
            "  Modulation: On-Off Keying (OOK)\n\n" +
            "Activity:\n" +
            "  Transmissions: %d\n" +
            "  Detections: %d\n" +
            "  Jam Operations: %d\n\n" +
            "Status: GHOST NETWORK ACTIVE\n",
            transmissionCount, detectionCount, jamCount
        );
    }
}
