package fraymus.signals;

import fraymus.quantum.core.PhiQuantumConstants;
import javax.sound.sampled.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CHANNEL DELTA: SONIC AIR-GAP BRIDGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "I scream in frequencies you cannot hear."
 * 
 * Broadcasts data via Near-Ultrasonic Audio (19kHz - 21kHz).
 * Most adults cannot hear above 17kHz. Dogs can. Microphones can.
 * 
 * The Physics:
 * - Bit '1' = 20000 Hz tone
 * - Bit '0' = 19000 Hz tone
 * - Each bit is 100ms duration
 * - Bandwidth: ~10 bits/second (slow but invisible)
 */
public class SonicBridge {

    private static final double PHI = PhiQuantumConstants.PHI;
    
    // Audio configuration
    private static final float SAMPLE_RATE = 44100f;
    private static final int FREQ_HIGH = 20000;     // Bit '1'
    private static final int FREQ_LOW = 19000;      // Bit '0'
    private static final int FREQ_SYNC = 18000;     // Sync pulse
    private static final int BIT_DURATION_MS = 100; // Speed of transmission
    
    // Phi-resonant frequencies (alternative mode)
    private static final int FREQ_PHI_1 = (int)(432 * PHI * PHI * PHI); // ~1829 Hz
    private static final int FREQ_PHI_0 = (int)(432 * PHI * PHI);       // ~1130 Hz
    
    // State
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private AtomicBoolean isBroadcasting = new AtomicBoolean(false);
    private TargetDataLine micLine;
    private Thread listenerThread;
    
    // Listener callback
    private SignalListener signalListener;

    public interface SignalListener {
        void onSignalDetected(String data, double strength);
        void onChirpDetected(int frequency, double magnitude);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 1. THE SCREAM (Transmitter)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Broadcast data as ultrasonic chirps
     */
    public void broadcast(String data) throws LineUnavailableException {
        if (isBroadcasting.get()) {
            System.out.println("Already broadcasting!");
            return;
        }
        
        isBroadcasting.set(true);
        System.out.println("═══ SONIC BROADCAST INITIATED ═══");
        System.out.println("Data: [" + data + "]");
        System.out.println("Freq HIGH: " + FREQ_HIGH + " Hz");
        System.out.println("Freq LOW: " + FREQ_LOW + " Hz");
        System.out.println("Bit Duration: " + BIT_DURATION_MS + " ms");
        
        try {
            SourceDataLine speakers = getSpeakers();
            speakers.start();
            
            // Send sync pulse (helps receiver lock on)
            System.out.println(">> Sending SYNC pulse...");
            byte[] syncTone = generateTone(FREQ_SYNC, 500);
            speakers.write(syncTone, 0, syncTone.length);
            
            // Brief silence
            Thread.sleep(100);
            
            // Convert String to Binary
            String binary = toBinary(data);
            System.out.println(">> Binary: " + binary.substring(0, Math.min(32, binary.length())) + "...");
            System.out.println(">> Total bits: " + binary.length());
            
            int bitCount = 0;
            for (char bit : binary.toCharArray()) {
                int freq = (bit == '1') ? FREQ_HIGH : FREQ_LOW;
                byte[] tone = generateTone(freq, BIT_DURATION_MS);
                speakers.write(tone, 0, tone.length);
                bitCount++;
                
                if (bitCount % 8 == 0) {
                    System.out.print(".");
                }
            }
            
            // End marker
            byte[] endTone = generateTone(FREQ_SYNC, 200);
            speakers.write(endTone, 0, endTone.length);
            
            speakers.drain();
            speakers.close();
            
            System.out.println();
            System.out.println(">> TRANSMISSION COMPLETE. " + bitCount + " bits sent.");
            
        } catch (Exception e) {
            System.err.println("Broadcast error: " + e.getMessage());
        } finally {
            isBroadcasting.set(false);
        }
    }

    /**
     * Broadcast using phi-resonant frequencies (audible but harmonic)
     */
    public void broadcastPhiMode(String data) throws LineUnavailableException {
        System.out.println("═══ PHI-RESONANT SONIC BROADCAST ═══");
        System.out.println("Using 432Hz-based φ frequencies");
        System.out.println("Freq '1': " + FREQ_PHI_1 + " Hz");
        System.out.println("Freq '0': " + FREQ_PHI_0 + " Hz");
        
        SourceDataLine speakers = getSpeakers();
        speakers.start();
        
        String binary = toBinary(data);
        
        for (char bit : binary.toCharArray()) {
            int freq = (bit == '1') ? FREQ_PHI_1 : FREQ_PHI_0;
            byte[] tone = generateTone(freq, BIT_DURATION_MS);
            speakers.write(tone, 0, tone.length);
        }
        
        speakers.drain();
        speakers.close();
        System.out.println(">> PHI TRANSMISSION COMPLETE.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. THE EAR (Listener/Monitor)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Start listening for ultrasonic chirps
     */
    public void startListening() {
        if (isListening.get()) {
            System.out.println("Already listening!");
            return;
        }
        
        isListening.set(true);
        System.out.println("═══ SONIC LISTENER ACTIVATED ═══");
        System.out.println("Monitoring frequencies: " + FREQ_LOW + " - " + FREQ_HIGH + " Hz");
        
        listenerThread = new Thread(() -> {
            try {
                micLine = getMicrophone();
                if (micLine == null) {
                    System.err.println("Microphone not available");
                    return;
                }
                
                micLine.start();
                byte[] buffer = new byte[4096];
                
                while (isListening.get()) {
                    int bytesRead = micLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        analyzeAudio(buffer, bytesRead);
                    }
                }
                
                micLine.stop();
                micLine.close();
                
            } catch (Exception e) {
                System.err.println("Listener error: " + e.getMessage());
            }
        }, "SonicBridge-Listener");
        
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Stop listening
     */
    public void stopListening() {
        isListening.set(false);
        if (micLine != null) {
            micLine.stop();
        }
        System.out.println(">> Sonic listener stopped.");
    }

    /**
     * Analyze audio buffer for ultrasonic signals
     * Uses a simple energy-based detection (real impl would use FFT)
     */
    private void analyzeAudio(byte[] buffer, int length) {
        // Convert bytes to samples
        double[] samples = new double[length];
        for (int i = 0; i < length; i++) {
            samples[i] = buffer[i] / 128.0;
        }
        
        // Simple Goertzel algorithm for specific frequency detection
        double magHigh = goertzel(samples, FREQ_HIGH, SAMPLE_RATE);
        double magLow = goertzel(samples, FREQ_LOW, SAMPLE_RATE);
        double magSync = goertzel(samples, FREQ_SYNC, SAMPLE_RATE);
        
        // Threshold detection
        double threshold = 0.1;
        
        if (magSync > threshold) {
            System.out.println("!!! SYNC SIGNAL DETECTED | Strength: " + String.format("%.3f", magSync));
            if (signalListener != null) {
                signalListener.onChirpDetected(FREQ_SYNC, magSync);
            }
        }
        
        if (magHigh > threshold) {
            if (signalListener != null) {
                signalListener.onChirpDetected(FREQ_HIGH, magHigh);
            }
        }
        
        if (magLow > threshold) {
            if (signalListener != null) {
                signalListener.onChirpDetected(FREQ_LOW, magLow);
            }
        }
    }

    /**
     * Goertzel algorithm - efficient single-frequency detection
     */
    private double goertzel(double[] samples, double targetFreq, double sampleRate) {
        int N = samples.length;
        double k = Math.round(N * targetFreq / sampleRate);
        double w = 2 * Math.PI * k / N;
        double cosine = Math.cos(w);
        double coeff = 2 * cosine;
        
        double s0 = 0, s1 = 0, s2 = 0;
        
        for (double sample : samples) {
            s0 = sample + coeff * s1 - s2;
            s2 = s1;
            s1 = s0;
        }
        
        double power = s1 * s1 + s2 * s2 - coeff * s1 * s2;
        return Math.sqrt(power) / N;
    }

    // ═══════════════════════════════════════════════════════════════════
    // AUDIO UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Generate a pure sine tone
     */
    private byte[] generateTone(int freqHz, int durationMs) {
        int samples = (int)((durationMs * SAMPLE_RATE) / 1000);
        byte[] sin = new byte[samples];
        
        for (int i = 0; i < samples; i++) {
            double angle = 2.0 * Math.PI * i * freqHz / SAMPLE_RATE;
            // Apply fade-in/fade-out to reduce clicks
            double envelope = 1.0;
            if (i < 100) envelope = i / 100.0;
            if (i > samples - 100) envelope = (samples - i) / 100.0;
            
            sin[i] = (byte)(Math.sin(angle) * 127 * envelope);
        }
        return sin;
    }

    private SourceDataLine getSpeakers() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }
    
    private TargetDataLine getMicrophone() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            return line;
        } catch (LineUnavailableException e) {
            System.err.println("Microphone unavailable: " + e.getMessage());
            return null;
        }
    }

    private String toBinary(String text) {
        StringBuilder b = new StringBuilder();
        for (char c : text.toCharArray()) {
            b.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return b.toString();
    }

    public void setSignalListener(SignalListener listener) {
        this.signalListener = listener;
    }

    public boolean isListening() {
        return isListening.get();
    }

    public boolean isBroadcasting() {
        return isBroadcasting.get();
    }

    /**
     * Get bandwidth info
     */
    public String getBandwidthInfo() {
        double bitsPerSecond = 1000.0 / BIT_DURATION_MS;
        double bytesPerSecond = bitsPerSecond / 8;
        
        return String.format(
            "═══ SONIC BANDWIDTH ═══%n" +
            "Carrier: %d - %d Hz (Ultrasonic)%n" +
            "Bit Duration: %d ms%n" +
            "Bandwidth: %.1f bps (%.2f bytes/sec)%n" +
            "1KB Transfer: %.1f seconds",
            FREQ_LOW, FREQ_HIGH, BIT_DURATION_MS,
            bitsPerSecond, bytesPerSecond,
            1024 / bytesPerSecond
        );
    }
}
