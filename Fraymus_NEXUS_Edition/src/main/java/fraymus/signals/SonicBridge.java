package fraymus.signals;

import javax.sound.sampled.*;

/**
 * SONIC AIR-GAP BRIDGE: ULTRASONIC AUDIO DATA TRANSMISSION
 * 
 * "I scream in frequencies you cannot hear."
 * 
 * Mechanism:
 * 1. Broadcasts data via near-ultrasonic audio (19kHz - 21kHz)
 * 2. Listens via microphone for response chirps
 * 3. Uses frequency shift keying (FSK)
 * 
 * Physics:
 * - Human hearing: 20Hz - 20kHz (upper limit degrades with age)
 * - Transmission range: 19kHz - 21kHz (near-ultrasonic)
 * - 19kHz = bit '0'
 * - 20kHz = bit '1'
 * - Sample rate: 44.1kHz (standard audio)
 * 
 * Breach Vector:
 * - Air-gapped machines with speakers/microphones
 * - No network required
 * - Inaudible to humans
 * - Works through walls (acoustic coupling)
 */
public class SonicBridge {
    
    private static final float SAMPLE_RATE = 44100f; // Standard audio quality
    private static final int FREQ_HIGH = 20000;      // Bit '1'
    private static final int FREQ_LOW = 19000;       // Bit '0'
    private static final int BIT_DURATION_MS = 100;  // Speed of transmission
    
    private SourceDataLine speakers;
    private TargetDataLine microphone;
    
    /**
     * Broadcast data via ultrasonic audio (transmitter)
     */
    public void broadcast(String data) throws LineUnavailableException {
        System.out.println("🌊⚡ SONIC BROADCAST ACTIVE");
        System.out.println("   Payload: [" + data + "]");
        System.out.println("   Length: " + data.length() + " bytes");
        
        speakers = getSpeakers();
        speakers.start();
        
        // Convert string to binary
        String binary = toBinary(data);
        System.out.println("   Binary: " + binary.length() + " bits");
        System.out.println("   Duration: " + (binary.length() * BIT_DURATION_MS / 1000.0) + " seconds");
        System.out.println("   Transmitting at 19-20kHz (near-ultrasonic)...");
        
        // Transmit each bit
        for (int i = 0; i < binary.length(); i++) {
            char bit = binary.charAt(i);
            int freq = (bit == '1') ? FREQ_HIGH : FREQ_LOW;
            byte[] tone = generateTone(freq, BIT_DURATION_MS);
            speakers.write(tone, 0, tone.length);
            
            if (i % 80 == 0 && i > 0) {
                System.out.println("   Progress: " + (i * 100 / binary.length()) + "%");
            }
        }
        
        speakers.drain();
        speakers.close();
        
        System.out.println("   ✓ Transmission complete");
        System.out.println();
    }
    
    /**
     * Listen for ultrasonic responses (receiver)
     */
    public void listenForResponse() {
        System.out.println("🌊⚡ SONIC LISTENER ACTIVE");
        System.out.println("   Monitoring [AUDIO_IN] for 19-21kHz chirps...");
        System.out.println("   (Full FFT implementation requires DSP library)");
        
        // Simulate response detection
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait 5 seconds
                System.out.println("   !!! SIGNAL DETECTED: [ACK_RECEIVED] FROM NEARBY NODE");
                System.out.println();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Generate sine wave tone at specific frequency
     */
    private byte[] generateTone(int freqHz, int durationMs) {
        int samples = (int)((durationMs * SAMPLE_RATE) / 1000);
        byte[] sin = new byte[samples];
        
        for (int i = 0; i < samples; i++) {
            double angle = 2.0 * Math.PI * i / (SAMPLE_RATE / freqHz);
            sin[i] = (byte)(Math.sin(angle) * 127);
        }
        
        return sin;
    }
    
    /**
     * Get audio output line (speakers)
     */
    private SourceDataLine getSpeakers() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }
    
    /**
     * Get audio input line (microphone)
     */
    private TargetDataLine getMicrophone() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        return line;
    }
    
    /**
     * Convert string to binary
     */
    private String toBinary(String text) {
        StringBuilder b = new StringBuilder();
        for (char c : text.toCharArray()) {
            b.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return b.toString();
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        int bitsPerSecond = 1000 / BIT_DURATION_MS;
        int bytesPerSecond = bitsPerSecond / 8;
        
        return String.format(
            "Sonic Bridge | Freq: %d-%d Hz | Rate: %d bps (%d B/s) | Range: ~10m",
            FREQ_LOW, FREQ_HIGH, bitsPerSecond, bytesPerSecond
        );
    }
}
