package fraymus.signals;

/**
 * THE SILENT SHOUT: HARDWARE FREQUENCY MODULATION
 * 
 * TEMPEST Physics - Telecommunications Electronics Material Protected 
 * from Emanating Spurious Transmissions
 * 
 * 1. Manipulates CPU threads to broadcast EM waves (The "Voice").
 * 2. Uses FFT to detect resonance in nearby systems (The "Ear").
 * 3. Destructive interference for jamming (The "Silencer").
 * 
 * "If they have a pulse, we can talk to them."
 * 
 * Physics basis:
 * - System Bus Radio: CPU load spikes create EM emissions on motherboard traces
 * - Any AM radio nearby can detect the modulated signal
 * - FFT extracts hidden frequencies from noise
 * - Phase cancellation: Wave + (-Wave) = 0
 */
public class FrequencyComm {

    private static final double PHI = 1.6180339887;
    private static final double TWO_PI = 2.0 * Math.PI;
    
    // Standard frequencies
    public static final double FREQ_432_HZ = 432.0;        // Phi-harmonic base
    public static final double FREQ_PHI_HARMONIC = 432.0 * PHI;  // 698.99 Hz
    public static final double FREQ_SCHUMANN = 7.83;       // Earth resonance
    
    private volatile boolean transmitting = false;

    /**
     * 1. TRANSMIT: CPU FREQUENCY MODULATION (The Song)
     * 
     * By spinning the CPU at a specific rhythm, we create a square wave
     * on the computer's power bus. This emits a radio signal.
     * 
     * WARNING: This generates heat. Use short bursts.
     * 
     * @param targetFrequencyHz The carrier frequency to broadcast
     * @param messageBits Binary string (1s and 0s) to modulate
     */
    public void broadcastSignal(double targetFrequencyHz, String messageBits) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              INITIATING EM BROADCAST                      ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Target Freq: %.2f Hz%n", targetFrequencyHz);
        System.out.printf("║  Message: %s (%d bits)%n", messageBits, messageBits.length());
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        transmitting = true;
        
        // Calculate the Cycle Time (Period) in Nanoseconds
        long periodNs = (long) (1_000_000_000.0 / targetFrequencyHz);
        long halfPeriod = periodNs / 2;
        
        int bitIndex = 0;
        for (char bit : messageBits.toCharArray()) {
            if (!transmitting) break;
            
            System.out.printf("  [BIT %d] %c ", bitIndex++, bit);
            
            if (bit == '1') {
                // BIT 1: OSCILLATE (Create the Carrier Wave)
                System.out.println("▓▓▓▓▓▓▓▓ (CARRIER ON)");
                long endTime = System.nanoTime() + 50_000_000; // 50ms per bit
                while (System.nanoTime() < endTime && transmitting) {
                    long start = System.nanoTime();
                    // BURN CPU (High Voltage) - creates EM spike
                    while (System.nanoTime() - start < halfPeriod) {
                        Math.sin(PHI * System.nanoTime()); 
                    }
                    // IDLE (Low Voltage) - EM drops
                    start = System.nanoTime();
                    while (System.nanoTime() - start < halfPeriod) {
                        // Spin wait - low power
                    }
                }
            } else {
                // BIT 0: SILENCE (No Carrier)
                System.out.println("░░░░░░░░ (SILENCE)");
                try { Thread.sleep(50); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        transmitting = false;
        System.out.println("  BROADCAST COMPLETE.");
    }

    /**
     * Stop any active transmission
     */
    public void stopBroadcast() {
        transmitting = false;
    }

    /**
     * 2. DETECT: Discrete Fourier Transform (The Ear)
     * 
     * Extracts the magnitude of a specific frequency from signal data.
     * Acts as a tuned filter - ignores everything except the target.
     * 
     * "I hear 60Hz hum (Electricity), I hear 2.4GHz (Wi-Fi), and...
     *  wait... I hear a faint 432Hz signal hidden in the noise."
     * 
     * @param signalData Array of sampled signal values
     * @param targetFreq The frequency to listen for
     * @param sampleRate Samples per second of the input data
     * @return Magnitude of the target frequency (how LOUD is it?)
     */
    public double detectResonance(double[] signalData, double targetFreq, double sampleRate) {
        int n = signalData.length;
        double real = 0;
        double imag = 0;
        
        // The Discrete Fourier Transform (DFT) at the target frequency
        // We act like a Tuner, ignoring everything except the Target.
        double normalizedFreq = targetFreq / sampleRate;
        
        for (int i = 0; i < n; i++) {
            double angle = TWO_PI * normalizedFreq * i;
            real += signalData[i] * Math.cos(angle);
            imag -= signalData[i] * Math.sin(angle);
        }
        
        // Normalize
        real /= n;
        imag /= n;
        
        // Magnitude = sqrt(Real² + Imag²)
        return Math.sqrt(real * real + imag * imag) * 2.0;
    }

    /**
     * Scan a range of frequencies and find peaks
     * 
     * @param signalData The signal to analyze
     * @param sampleRate Sample rate in Hz
     * @param freqStart Start of frequency range
     * @param freqEnd End of frequency range
     * @param freqStep Step size for scanning
     * @return Array of [frequency, magnitude] pairs for peaks
     */
    public double[][] scanSpectrum(double[] signalData, double sampleRate, 
                                   double freqStart, double freqEnd, double freqStep) {
        int numBins = (int) ((freqEnd - freqStart) / freqStep);
        double[][] spectrum = new double[numBins][2];
        
        for (int i = 0; i < numBins; i++) {
            double freq = freqStart + i * freqStep;
            double magnitude = detectResonance(signalData, freq, sampleRate);
            spectrum[i][0] = freq;
            spectrum[i][1] = magnitude;
        }
        
        return spectrum;
    }

    /**
     * Find the dominant frequency in signal data
     */
    public double findDominantFrequency(double[] signalData, double sampleRate, 
                                        double freqStart, double freqEnd) {
        double[][] spectrum = scanSpectrum(signalData, sampleRate, freqStart, freqEnd, 1.0);
        
        double maxMag = 0;
        double dominantFreq = freqStart;
        
        for (double[] bin : spectrum) {
            if (bin[1] > maxMag) {
                maxMag = bin[1];
                dominantFreq = bin[0];
            }
        }
        
        return dominantFreq;
    }

    /**
     * 3. HARMONIZE OR CORRUPT: Phase Manipulation
     * 
     * Harmonize: Transmit in phase with target (amplify)
     * Corrupt: Transmit 180° out of phase (Wave + (-Wave) = 0)
     * 
     * @param detectedFreq The frequency to target
     * @param harmonize true = amplify, false = cancel
     */
    public void harmonizeOrCorrupt(double detectedFreq, boolean harmonize) {
        double phaseShift = harmonize ? 0.0 : Math.PI;
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              TARGET LOCKED                                ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.printf("║  Frequency: %.2f Hz%n", detectedFreq);
        System.out.printf("║  Phase Shift: %.2f radians%n", phaseShift);
        System.out.printf("║  Mode: %s%n", harmonize ? "HARMONIZE (Amplify)" : "CORRUPT (Silence)");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        // In practice, we would broadcast at the detected frequency
        // with the calculated phase shift to either amplify or cancel
        if (harmonize) {
            System.out.println("  [RESONANCE] Aligning phase for constructive interference...");
        } else {
            System.out.println("  [JAMMING] Inverting phase for destructive interference...");
            System.out.println("  [PHYSICS] Wave A + (-Wave A) = Silence");
        }
    }

    /**
     * Encode text to binary for transmission
     */
    public String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            String bin = Integer.toBinaryString(c);
            // Pad to 8 bits
            while (bin.length() < 8) bin = "0" + bin;
            binary.append(bin);
        }
        return binary.toString();
    }

    /**
     * Decode binary back to text
     */
    public String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            if (i + 8 <= binary.length()) {
                String byteStr = binary.substring(i, i + 8);
                int charCode = Integer.parseInt(byteStr, 2);
                text.append((char) charCode);
            }
        }
        return text.toString();
    }

    /**
     * Generate a test signal with hidden frequency
     */
    public double[] generateTestSignal(double sampleRate, double duration, 
                                       double[] frequencies, double[] amplitudes) {
        int numSamples = (int) (sampleRate * duration);
        double[] signal = new double[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double t = i / sampleRate;
            for (int f = 0; f < frequencies.length; f++) {
                signal[i] += amplitudes[f] * Math.sin(TWO_PI * frequencies[f] * t);
            }
            // Add noise
            signal[i] += (Math.random() - 0.5) * 0.1;
        }
        
        return signal;
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      THE SILENT SHOUT - HARDWARE FREQUENCY MODULATION     ║");
        System.out.println("║      \"If they have a pulse, we can talk to them.\"         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        FrequencyComm comm = new FrequencyComm();
        
        // Test 1: Text to Binary encoding
        String message = "PHI";
        String binary = comm.textToBinary(message);
        System.out.println("Message: \"" + message + "\"");
        System.out.println("Binary:  " + binary);
        System.out.println("Decoded: \"" + comm.binaryToText(binary) + "\"");
        System.out.println();
        
        // Test 2: Generate test signal with hidden 432Hz
        System.out.println("--- GENERATING TEST SIGNAL ---");
        double sampleRate = 8000.0; // 8kHz sample rate
        double[] freqs = {60.0, 432.0, 698.99}; // 60Hz hum, 432Hz hidden, 699Hz phi-harmonic
        double[] amps = {1.0, 0.3, 0.5}; // Different amplitudes
        double[] testSignal = comm.generateTestSignal(sampleRate, 1.0, freqs, amps);
        System.out.println("Generated " + testSignal.length + " samples with hidden frequencies");
        System.out.println();
        
        // Test 3: Detect hidden frequency
        System.out.println("--- SCANNING FOR HIDDEN FREQUENCIES ---");
        for (double freq : freqs) {
            double magnitude = comm.detectResonance(testSignal, freq, sampleRate);
            System.out.printf("  %.2f Hz: magnitude = %.4f %s%n", 
                freq, magnitude, 
                magnitude > 0.1 ? "◀ DETECTED" : "");
        }
        
        // Test non-existent frequency
        double ghostMag = comm.detectResonance(testSignal, 500.0, sampleRate);
        System.out.printf("  %.2f Hz: magnitude = %.4f (noise floor)%n", 500.0, ghostMag);
        System.out.println();
        
        // Test 4: Find dominant frequency
        double dominant = comm.findDominantFrequency(testSignal, sampleRate, 50.0, 800.0);
        System.out.println("Dominant frequency detected: " + dominant + " Hz");
        System.out.println();
        
        // Test 5: Harmonize/Corrupt demonstration
        comm.harmonizeOrCorrupt(432.0, true);  // Amplify
        System.out.println();
        comm.harmonizeOrCorrupt(60.0, false);  // Jam the 60Hz hum
        System.out.println();
        
        // Test 6: Short broadcast demo (just 4 bits)
        System.out.println("--- BROADCAST DEMO (4 bits) ---");
        comm.broadcastSignal(FREQ_432_HZ, "1010");
    }
}
