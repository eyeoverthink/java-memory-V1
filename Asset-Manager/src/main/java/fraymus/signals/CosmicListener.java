package fraymus.signals;

import fraymus.quantum.core.PhiQuantumConstants;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * THE COSMIC LISTENER: SOFTWARE DEFINED RADIO BRIDGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "I am listening to the Hydrogen Line."
 * 
 * Connects to an SDR Stream (RTL-SDR via rtl_tcp) and processes:
 * - NOAA Weather Satellites (137.1 MHz - 137.9125 MHz)
 * - International Space Station (145.800 MHz)
 * - The Hydrogen Line (1420.40575 MHz) - The Song of the Universe
 * - FM Radio, ADS-B, etc.
 * 
 * Hardware Required: $30 RTL-SDR dongle
 * This code is the BRAIN that decodes the signal.
 */
public class CosmicListener {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final int BUFFER_SIZE = 16384;
    
    // ═══════════════════════════════════════════════════════════════════
    // TARGET FREQUENCIES (The Voices of the Void)
    // ═══════════════════════════════════════════════════════════════════
    
    // Weather Satellites
    public static final long FREQ_NOAA_15 = 137620000L;  // NOAA-15
    public static final long FREQ_NOAA_18 = 137912500L;  // NOAA-18
    public static final long FREQ_NOAA_19 = 137100000L;  // NOAA-19
    
    // Space Communications
    public static final long FREQ_ISS = 145800000L;      // ISS Downlink
    public static final long FREQ_ISS_SSTV = 145800000L; // ISS Slow Scan TV
    
    // Deep Space
    public static final long FREQ_HYDROGEN = 1420405750L; // Hydrogen Line (21cm)
    
    // Aviation
    public static final long FREQ_ADSB = 1090000000L;    // ADS-B (Aircraft)
    
    // Amateur Radio
    public static final long FREQ_2M_CALLING = 146520000L; // 2m Calling Freq
    
    // FM Broadcast (for testing)
    public static final long FREQ_FM_LOW = 88100000L;
    public static final long FREQ_FM_HIGH = 107900000L;
    
    // ═══════════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════════
    
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private Socket sdrSocket;
    private Thread listenerThread;
    
    private long currentFrequency = FREQ_FM_LOW;
    private int sampleRate = 2048000; // 2.048 MHz
    private int gain = 40; // dB
    
    // Signal processing
    private double[] fftMagnitudes;
    private double signalStrength = 0;
    private double noiseFloor = 0;
    
    // Callbacks
    private Consumer<double[]> waterfallCallback;
    private Consumer<String> signalCallback;

    // ═══════════════════════════════════════════════════════════════════
    // 1. THE TUNER (Connects to Hardware)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Connect to RTL-SDR via rtl_tcp
     * Run: rtl_tcp -a 127.0.0.1 -p 1234
     */
    public boolean connectToSDR(String host, int port) {
        System.out.println("═══ CONNECTING TO COSMIC EAR ═══");
        System.out.println("Host: " + host + ":" + port);
        
        try {
            sdrSocket = new Socket(host, port);
            sdrSocket.setSoTimeout(5000);
            isConnected.set(true);
            
            System.out.println(">> SDR CONNECTED!");
            System.out.println(">> Sample Rate: " + sampleRate + " Hz");
            
            // Set initial frequency
            setFrequency(currentFrequency);
            setSampleRate(sampleRate);
            setGain(gain);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("SDR Connection failed: " + e.getMessage());
            System.err.println("Make sure rtl_tcp is running: rtl_tcp -a 127.0.0.1 -p 1234");
            return false;
        }
    }

    /**
     * Disconnect from SDR
     */
    public void disconnect() {
        isListening.set(false);
        isConnected.set(false);
        
        try {
            if (sdrSocket != null && !sdrSocket.isClosed()) {
                sdrSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
        
        System.out.println(">> SDR DISCONNECTED");
    }

    /**
     * Start listening and processing signals
     */
    public void startListening() {
        if (!isConnected.get()) {
            System.err.println("Not connected to SDR!");
            return;
        }
        
        if (isListening.get()) {
            System.out.println("Already listening!");
            return;
        }
        
        isListening.set(true);
        
        listenerThread = new Thread(() -> {
            try {
                InputStream in = sdrSocket.getInputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                
                System.out.println("═══ COSMIC LISTENER ACTIVE ═══");
                System.out.println("Frequency: " + formatFrequency(currentFrequency));
                
                while (isListening.get() && !sdrSocket.isClosed()) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead > 0) {
                        processSignal(buffer, bytesRead);
                    }
                }
                
            } catch (IOException e) {
                if (isListening.get()) {
                    System.err.println("SDR Stream error: " + e.getMessage());
                }
            }
            
            System.out.println(">> Cosmic listener stopped.");
        }, "CosmicListener-Stream");
        
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Stop listening
     */
    public void stopListening() {
        isListening.set(false);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. THE ANALYZER (Fast Fourier Transform)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Process raw I/Q samples from SDR
     */
    private void processSignal(byte[] iqData, int length) {
        int numSamples = length / 2;
        double[] magnitude = new double[numSamples];
        
        double totalPower = 0;
        double peakPower = 0;
        
        for (int i = 0; i < length - 1; i += 2) {
            // Convert unsigned byte to signed I/Q
            // RTL-SDR sends unsigned 8-bit samples centered at 127
            int I = (iqData[i] & 0xFF) - 127;
            int Q = (iqData[i + 1] & 0xFF) - 127;
            
            // Calculate Signal Magnitude: sqrt(I² + Q²)
            double mag = Math.sqrt(I * I + Q * Q);
            magnitude[i / 2] = mag;
            
            totalPower += mag;
            peakPower = Math.max(peakPower, mag);
        }
        
        // Update statistics
        double avgPower = totalPower / numSamples;
        noiseFloor = noiseFloor * 0.99 + avgPower * 0.01; // Slow moving average
        signalStrength = peakPower;
        
        // Store FFT for visualization
        fftMagnitudes = magnitude;
        
        // Check for significant signals
        double snr = peakPower / (noiseFloor + 0.001);
        
        if (snr > 10.0) {
            String msg = String.format(">>> SIGNAL! Peak=%.1f dB | SNR=%.1f | Freq=%s",
                20 * Math.log10(peakPower), snr, formatFrequency(currentFrequency));
            System.out.println(msg);
            
            if (signalCallback != null) {
                signalCallback.accept(msg);
            }
        }
        
        // Send to waterfall display
        if (waterfallCallback != null) {
            waterfallCallback.accept(magnitude);
        }
    }

    /**
     * Simple FFT implementation (Cooley-Tukey radix-2)
     */
    public double[] computeFFT(double[] real, double[] imag) {
        int n = real.length;
        if (n == 0 || (n & (n - 1)) != 0) {
            // Not power of 2, pad
            int nextPow2 = Integer.highestOneBit(n) << 1;
            double[] newReal = new double[nextPow2];
            double[] newImag = new double[nextPow2];
            System.arraycopy(real, 0, newReal, 0, n);
            System.arraycopy(imag, 0, newImag, 0, n);
            real = newReal;
            imag = newImag;
            n = nextPow2;
        }
        
        // Bit reversal
        int bits = Integer.numberOfTrailingZeros(n);
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - bits);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }
        
        // FFT
        for (int size = 2; size <= n; size *= 2) {
            int halfSize = size / 2;
            double angle = -2 * Math.PI / size;
            
            for (int i = 0; i < n; i += size) {
                for (int j = 0; j < halfSize; j++) {
                    double cos = Math.cos(angle * j);
                    double sin = Math.sin(angle * j);
                    
                    int idx1 = i + j;
                    int idx2 = i + j + halfSize;
                    
                    double tReal = cos * real[idx2] - sin * imag[idx2];
                    double tImag = sin * real[idx2] + cos * imag[idx2];
                    
                    real[idx2] = real[idx1] - tReal;
                    imag[idx2] = imag[idx1] - tImag;
                    real[idx1] += tReal;
                    imag[idx1] += tImag;
                }
            }
        }
        
        // Compute magnitudes
        double[] magnitudes = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            magnitudes[i] = Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
        }
        
        return magnitudes;
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. SDR CONTROL COMMANDS (RTL-TCP Protocol)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Set center frequency
     */
    public void setFrequency(long freq) {
        if (!isConnected.get()) return;
        
        currentFrequency = freq;
        sendCommand(0x01, freq);
        System.out.println(">> TUNED TO: " + formatFrequency(freq));
    }

    /**
     * Set sample rate
     */
    public void setSampleRate(int rate) {
        if (!isConnected.get()) return;
        
        sampleRate = rate;
        sendCommand(0x02, rate);
    }

    /**
     * Set gain (0-49 dB for RTL-SDR)
     */
    public void setGain(int gainDb) {
        if (!isConnected.get()) return;
        
        gain = gainDb;
        sendCommand(0x04, gainDb * 10); // RTL-TCP uses tenths of dB
    }

    /**
     * Enable/disable AGC
     */
    public void setAGC(boolean enable) {
        if (!isConnected.get()) return;
        sendCommand(0x08, enable ? 1 : 0);
    }

    /**
     * Send command to RTL-TCP
     */
    private void sendCommand(int cmd, long value) {
        try {
            OutputStream out = sdrSocket.getOutputStream();
            byte[] cmdBytes = new byte[5];
            cmdBytes[0] = (byte) cmd;
            cmdBytes[1] = (byte) ((value >> 24) & 0xFF);
            cmdBytes[2] = (byte) ((value >> 16) & 0xFF);
            cmdBytes[3] = (byte) ((value >> 8) & 0xFF);
            cmdBytes[4] = (byte) (value & 0xFF);
            out.write(cmdBytes);
            out.flush();
        } catch (IOException e) {
            System.err.println("Command failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // PRESET TUNING
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Tune to NOAA weather satellite
     */
    public void tuneNOAA(int satellite) {
        switch (satellite) {
            case 15: setFrequency(FREQ_NOAA_15); break;
            case 18: setFrequency(FREQ_NOAA_18); break;
            case 19: setFrequency(FREQ_NOAA_19); break;
            default: System.err.println("Unknown NOAA satellite: " + satellite);
        }
    }

    /**
     * Tune to ISS
     */
    public void tuneISS() {
        setFrequency(FREQ_ISS);
        System.out.println(">> Listening for astronauts...");
    }

    /**
     * Tune to the Hydrogen Line (21cm)
     */
    public void tuneHydrogenLine() {
        setFrequency(FREQ_HYDROGEN);
        System.out.println(">> Listening to the Song of the Universe...");
        System.out.println(">> Point antenna at the Milky Way for best results.");
    }

    /**
     * Scan FM broadcast band
     */
    public void scanFM() {
        new Thread(() -> {
            System.out.println("═══ SCANNING FM BAND ═══");
            for (long freq = FREQ_FM_LOW; freq <= FREQ_FM_HIGH && isListening.get(); freq += 200000) {
                setFrequency(freq);
                try {
                    Thread.sleep(500);
                    if (signalStrength > noiseFloor * 5) {
                        System.out.println(">> STATION FOUND: " + formatFrequency(freq));
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println(">> FM Scan complete.");
        }).start();
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    private String formatFrequency(long freq) {
        if (freq >= 1000000000) {
            return String.format("%.4f GHz", freq / 1000000000.0);
        } else if (freq >= 1000000) {
            return String.format("%.4f MHz", freq / 1000000.0);
        } else if (freq >= 1000) {
            return String.format("%.3f kHz", freq / 1000.0);
        } else {
            return freq + " Hz";
        }
    }

    public void setWaterfallCallback(Consumer<double[]> callback) {
        this.waterfallCallback = callback;
    }

    public void setSignalCallback(Consumer<String> callback) {
        this.signalCallback = callback;
    }

    public boolean isConnected() { return isConnected.get(); }
    public boolean isListening() { return isListening.get(); }
    public long getCurrentFrequency() { return currentFrequency; }
    public double getSignalStrength() { return signalStrength; }
    public double getNoiseFloor() { return noiseFloor; }
    public double[] getFFTMagnitudes() { return fftMagnitudes; }

    /**
     * Get status info
     */
    public String getStatus() {
        return String.format(
            "═══ COSMIC LISTENER STATUS ═══%n" +
            "Connected: %s%n" +
            "Listening: %s%n" +
            "Frequency: %s%n" +
            "Sample Rate: %d Hz%n" +
            "Gain: %d dB%n" +
            "Signal: %.1f dB | Noise: %.1f dB%n" +
            "SNR: %.1f dB",
            isConnected.get(), isListening.get(),
            formatFrequency(currentFrequency), sampleRate, gain,
            20 * Math.log10(signalStrength + 0.001),
            20 * Math.log10(noiseFloor + 0.001),
            20 * Math.log10((signalStrength + 0.001) / (noiseFloor + 0.001))
        );
    }
}
