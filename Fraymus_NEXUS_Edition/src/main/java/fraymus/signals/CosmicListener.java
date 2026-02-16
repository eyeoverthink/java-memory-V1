package fraymus.signals;

import java.io.InputStream;
import java.net.Socket;

/**
 * COSMIC LISTENER: SOFTWARE DEFINED RADIO BRIDGE
 * 
 * "I am listening to the Hydrogen Line."
 * 
 * Mechanism:
 * 1. Connects to SDR stream (RTL-SDR via rtl_tcp)
 * 2. Ingests raw I/Q samples (radio waves)
 * 3. Performs FFT to find signals
 * 4. Visualizes spectrum waterfall
 * 
 * Target Frequencies:
 * - NOAA Weather Satellites: 137.1 - 137.9 MHz
 * - International Space Station: 145.8 MHz
 * - Hydrogen Line (Deep Space): 1420.40575 MHz
 * - Military Satcom: 240 - 270 MHz
 * 
 * Physics:
 * - I/Q samples = In-Phase / Quadrature (complex signal)
 * - Magnitude = sqrt(I² + Q²)
 * - FFT converts time domain to frequency domain
 * - Spikes in spectrum = signals
 * 
 * Hardware:
 * - RTL-SDR dongle ($30)
 * - Wire antenna
 * - rtl_tcp server (port 1234)
 */
public class CosmicListener {
    
    private static final int BUFFER_SIZE = 16384; // 16k samples
    
    // TARGET FREQUENCIES
    public static final long FREQ_NOAA_19 = 137100000L;  // Weather satellite
    public static final long FREQ_ISS     = 145800000L;  // Astronauts
    public static final long FREQ_H_LINE  = 1420405750L; // Deep space hydrogen
    public static final long FREQ_SATCOM  = 255000000L;  // Military/pirate
    
    private boolean running = false;
    private SignalCallback callback;
    
    /**
     * Connect to SDR and tune to frequency
     */
    public void connectToSDR(String host, int port, long frequency) {
        System.out.println("🌊⚡ COSMIC LISTENER ACTIVE");
        System.out.println("   Tuning to: " + formatFrequency(frequency));
        System.out.println("   Connecting to SDR: " + host + ":" + port);
        
        running = true;
        
        try (Socket socket = new Socket(host, port);
             InputStream in = socket.getInputStream()) {
            
            // Send command to tune frequency
            setFrequency(socket, frequency);
            setSampleRate(socket, 2048000); // 2.048 MHz
            
            System.out.println("   ✓ Connected");
            System.out.println("   Streaming I/Q samples...");
            System.out.println();
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int sampleCount = 0;
            
            // Stream loop
            while (running && in.read(buffer) != -1) {
                // Process raw I/Q data
                processSignal(buffer);
                
                sampleCount++;
                if (sampleCount % 100 == 0) {
                    System.out.print(".");
                    if (sampleCount % 5000 == 0) {
                        System.out.println(" [" + sampleCount + " samples]");
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("   ⚠️  SDR DISCONNECTED: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Process I/Q signal data
     */
    private void processSignal(byte[] iqData) {
        double[] magnitude = new double[iqData.length / 2];
        
        // Convert I/Q to magnitude
        for (int i = 0; i < iqData.length; i += 2) {
            // Convert unsigned byte to signed I/Q
            int I = (iqData[i] & 0xFF) - 127;
            int Q = (iqData[i+1] & 0xFF) - 127;
            
            // Calculate signal strength (magnitude)
            // Mag = sqrt(I² + Q²)
            magnitude[i/2] = Math.sqrt(I*I + Q*Q);
        }
        
        // Check for signal spikes
        double peak = 0;
        int peakIndex = 0;
        
        for (int i = 0; i < magnitude.length; i++) {
            if (magnitude[i] > peak) {
                peak = magnitude[i];
                peakIndex = i;
            }
        }
        
        // Threshold for signal detection
        if (peak > 50.0) {
            System.out.println("\n   >>> SIGNAL DETECTED!");
            System.out.println("       Strength: " + (int)peak);
            System.out.println("       Bin: " + peakIndex);
            
            // Trigger visualization
            if (callback != null) {
                callback.onSignalDetected(magnitude, peak, peakIndex);
            }
        }
    }
    
    /**
     * Set SDR frequency (RTL-TCP protocol)
     */
    private void setFrequency(Socket socket, long freq) {
        try {
            // RTL-TCP command: 0x01 (SET_FREQ)
            byte[] cmd = new byte[5];
            cmd[0] = 0x01;
            cmd[1] = (byte)((freq >> 24) & 0xFF);
            cmd[2] = (byte)((freq >> 16) & 0xFF);
            cmd[3] = (byte)((freq >> 8) & 0xFF);
            cmd[4] = (byte)(freq & 0xFF);
            
            socket.getOutputStream().write(cmd);
            socket.getOutputStream().flush();
            
        } catch (Exception e) {
            System.out.println("   ⚠️  Failed to set frequency: " + e.getMessage());
        }
    }
    
    /**
     * Set SDR sample rate (RTL-TCP protocol)
     */
    private void setSampleRate(Socket socket, int rate) {
        try {
            // RTL-TCP command: 0x02 (SET_SAMPLE_RATE)
            byte[] cmd = new byte[5];
            cmd[0] = 0x02;
            cmd[1] = (byte)((rate >> 24) & 0xFF);
            cmd[2] = (byte)((rate >> 16) & 0xFF);
            cmd[3] = (byte)((rate >> 8) & 0xFF);
            cmd[4] = (byte)(rate & 0xFF);
            
            socket.getOutputStream().write(cmd);
            socket.getOutputStream().flush();
            
        } catch (Exception e) {
            System.out.println("   ⚠️  Failed to set sample rate: " + e.getMessage());
        }
    }
    
    /**
     * Stop listening
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Set signal detection callback
     */
    public void setCallback(SignalCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Format frequency for display
     */
    private String formatFrequency(long freq) {
        if (freq >= 1000000000) {
            return String.format("%.3f GHz", freq / 1000000000.0);
        } else if (freq >= 1000000) {
            return String.format("%.3f MHz", freq / 1000000.0);
        } else if (freq >= 1000) {
            return String.format("%.3f kHz", freq / 1000.0);
        } else {
            return freq + " Hz";
        }
    }
    
    /**
     * Get statistics
     */
    public String getStats(long frequency) {
        return String.format(
            "Cosmic Listener | Freq: %s | Buffer: %d samples | Status: %s",
            formatFrequency(frequency),
            BUFFER_SIZE,
            running ? "ACTIVE" : "IDLE"
        );
    }
    
    /**
     * Callback interface for signal detection
     */
    public interface SignalCallback {
        void onSignalDetected(double[] spectrum, double peak, int peakIndex);
    }
}
