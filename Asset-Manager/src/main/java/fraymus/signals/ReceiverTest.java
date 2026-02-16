package fraymus.signals;

import java.lang.Math;
import java.util.Random;

/**
 * TEMPEST RECEIVER TEST
 * 
 * Isolated test environment for signal detection.
 * 
 * This app simulates signal detection via FFT.
 * Run this alongside TransmitterTest to test air-gap communication.
 * 
 * Usage:
 * 1. Compile: javac ReceiverTest.java
 * 2. Run: java test.tempest.ReceiverTest
 * 3. Run TransmitterTest in another terminal
 * 
 * Note: This is a simulation. Real detection requires:
 * - Microphone input
 * - SDR (Software Defined Radio)
 * - Antenna
 */
public class ReceiverTest {
    
    private static final double TWO_PI = 2.0 * Math.PI;
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ TEMPEST RECEIVER TEST");
        System.out.println("=".repeat(60));
        System.out.println();
        
        ReceiverTest receiver = new ReceiverTest();
        
        // Test 1: Detect 1 kHz signal
        System.out.println("TEST 1: Detecting 1 kHz Signal");
        receiver.detect(1000.0);
        
        System.out.println();
        
        // Test 2: Detect 10 kHz signal
        System.out.println("TEST 2: Detecting 10 kHz Signal");
        receiver.detect(10000.0);
        
        System.out.println();
        
        // Test 3: Detect 30 kHz signal
        System.out.println("TEST 3: Detecting 30 kHz Signal");
        receiver.detect(30000.0);
        
        System.out.println();
        
        // Test 4: Full spectrum scan
        System.out.println("TEST 4: Full Spectrum Scan (Finding Hidden Ghosts)");
        receiver.scanSpectrum(0.0, 15000.0, 100.0);
        
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("DETECTION COMPLETE");
        System.out.println();
        System.out.println("Note: This is a simulation.");
        System.out.println("Real detection requires audio input or SDR.");
    }
    
    /**
     * Detect signal at specified frequency
     * 
     * @param targetFreq Target frequency (Hz)
     */
    public void detect(double targetFreq) {
        System.out.println("  Target Frequency: " + targetFreq + " Hz");
        System.out.println("  Generating simulated signal...");
        
        // Generate simulated signal data
        // In real implementation, this would come from microphone/SDR
        double sampleRate = 44100.0;
        double[] signalData = generateSimulatedSignal(targetFreq, 4410); // 0.1 seconds
        
        System.out.println("  Samples: " + signalData.length);
        System.out.println("  Sample Rate: " + sampleRate + " Hz");
        System.out.println("  Applying FFT...");
        
        // Apply Discrete Fourier Transform with proper sample rate
        double magnitude = detectResonanceWithRate(signalData, targetFreq, sampleRate);
        
        System.out.println("  Magnitude: " + String.format("%.4f", magnitude));
        
        // Determine if signal is present
        double threshold = 0.1;
        boolean detected = magnitude > threshold;
        
        System.out.println("  Threshold: " + threshold);
        System.out.println("  Status: " + (detected ? "✓ SIGNAL DETECTED" : "✗ No signal"));
        
        // Visual spectrum
        printSpectrum(magnitude);
    }
    
    /**
     * Scan a range of frequencies for hidden signals
     */
    public void scanSpectrum(double freqStart, double freqEnd, double step) {
        System.out.println("  Scanning " + freqStart + " Hz to " + freqEnd + " Hz");
        System.out.println();
        
        // Generate signal with multiple hidden frequencies
        double[] hiddenFreqs = {1000.0, 5000.0, 10000.0};
        double[] signalData = generateMultiFreqSignal(hiddenFreqs, 8000);
        
        double maxMag = 0;
        double peakFreq = freqStart;
        
        for (double freq = freqStart; freq <= freqEnd; freq += step) {
            double mag = detectResonance(signalData, freq);
            
            if (mag > maxMag) {
                maxMag = mag;
                peakFreq = freq;
            }
            
            // Print spectrum bar every 1000 Hz
            if (freq % 1000 == 0) {
                String bar = generateBar(mag, 0.3);
                System.out.printf("  %6.0f Hz: %s %.3f%n", freq, bar, mag);
            }
        }
        
        System.out.println();
        System.out.println("  Peak frequency: " + peakFreq + " Hz");
        System.out.println("  Peak magnitude: " + String.format("%.4f", maxMag));
    }
    
    /**
     * Generate signal with multiple frequencies
     */
    private double[] generateMultiFreqSignal(double[] frequencies, int sampleCount) {
        double[] signal = new double[sampleCount];
        Random random = new Random(42); // Fixed seed for reproducibility
        
        for (int i = 0; i < sampleCount; i++) {
            for (double freq : frequencies) {
                signal[i] += Math.sin(TWO_PI * freq * i / sampleCount);
            }
            signal[i] += (random.nextDouble() - 0.5) * 0.2; // noise
        }
        return signal;
    }
    
    /**
     * Print visual spectrum bar
     */
    private void printSpectrum(double magnitude) {
        String bar = generateBar(magnitude, 0.5);
        System.out.println("  Spectrum: " + bar);
    }
    
    /**
     * Generate visual bar
     */
    private String generateBar(double magnitude, double scale) {
        int len = (int) (magnitude / scale * 20);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(len, 40); i++) sb.append("▓");
        return sb.toString();
    }
    
    /**
     * Detect resonance at target frequency using DFT
     * 
     * @param signalData Raw signal samples
     * @param targetFreq Target frequency (Hz)
     * @return Magnitude at target frequency
     */
    private double detectResonance(double[] signalData, double targetFreq) {
        int n = signalData.length;
        double real = 0;
        double imag = 0;
        
        // Discrete Fourier Transform at target frequency
        for (int i = 0; i < n; i++) {
            double angle = (TWO_PI * targetFreq * i) / n;
            real += signalData[i] * Math.cos(angle);
            imag -= signalData[i] * Math.sin(angle);
        }
        
        // Magnitude = sqrt(Real² + Imag²)
        double magnitude = Math.sqrt(real * real + imag * imag);
        
        // Normalize by sample count
        magnitude /= n;
        
        return magnitude;
    }
    
    /**
     * Generate simulated signal data
     * 
     * In real implementation, this would come from:
     * - Microphone input (javax.sound.sampled)
     * - SDR device
     * - Antenna + ADC
     * 
     * @param frequency Signal frequency (Hz)
     * @param sampleCount Number of samples
     * @return Simulated signal data
     */
    private double[] generateSimulatedSignal(double frequency, int sampleCount) {
        double[] signal = new double[sampleCount];
        Random random = new Random(42); // Fixed seed for reproducibility
        double sampleRate = 44100.0; // Standard audio sample rate
        
        for (int i = 0; i < sampleCount; i++) {
            // Pure sine wave at target frequency (properly normalized)
            double t = i / sampleRate;
            double sine = Math.sin(TWO_PI * frequency * t);
            
            // Add noise
            double noise = (random.nextDouble() - 0.5) * 0.1;
            
            signal[i] = sine + noise;
        }
        
        return signal;
    }
    
    /**
     * Detect resonance with proper sample rate
     */
    private double detectResonanceWithRate(double[] signalData, double targetFreq, double sampleRate) {
        int n = signalData.length;
        double real = 0;
        double imag = 0;
        double normalizedFreq = targetFreq / sampleRate;
        
        for (int i = 0; i < n; i++) {
            double angle = TWO_PI * normalizedFreq * i;
            real += signalData[i] * Math.cos(angle);
            imag -= signalData[i] * Math.sin(angle);
        }
        
        real /= n;
        imag /= n;
        return Math.sqrt(real * real + imag * imag) * 2.0;
    }
}
