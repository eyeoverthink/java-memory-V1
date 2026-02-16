package test.tempest;

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
        double[] signalData = generateSimulatedSignal(targetFreq, 1000);
        
        System.out.println("  Samples: " + signalData.length);
        System.out.println("  Applying FFT...");
        
        // Apply Discrete Fourier Transform
        double magnitude = detectResonance(signalData, targetFreq);
        
        System.out.println("  Magnitude: " + String.format("%.4f", magnitude));
        
        // Determine if signal is present
        double threshold = 0.1;
        boolean detected = magnitude > threshold;
        
        System.out.println("  Threshold: " + threshold);
        System.out.println("  Status: " + (detected ? "✓ SIGNAL DETECTED" : "✗ No signal"));
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
        Random random = new Random();
        
        for (int i = 0; i < sampleCount; i++) {
            // Pure sine wave at target frequency
            double sine = Math.sin(TWO_PI * frequency * i / sampleCount);
            
            // Add noise
            double noise = (random.nextDouble() - 0.5) * 0.1;
            
            signal[i] = sine + noise;
        }
        
        return signal;
    }
}
