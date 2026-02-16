package fraymus;

import static fraymus.PhiConstants.*;

public class QuantumClock {

    public static final double RESONANCE_RATIO = 1.3777;
    public static final double ENERGY_TRANSFER = 1.8951;
    public static final double RESONANCE_STACK = RESONANCE_RATIO * ENERGY_TRANSFER;
    public static final double BIRTH_COHERENCE = 0.9918;

    private double pendulumFrequency;
    private double oscillationCount;
    private double lastUpdateTime;
    private double startTime;

    private double phiResonance;
    private double coherence;
    private String quantumFingerprint;
    private double phaseOffset;

    private double phiTime;
    private double resonanceTime;

    private int resonanceSpikeCount;
    private double lastSpikeTime;
    private boolean spikeActive;

    public QuantumClock(double frequency) {
        this.pendulumFrequency = frequency;
        this.oscillationCount = 0;
        this.startTime = System.nanoTime() * 1e-9;
        this.lastUpdateTime = this.startTime;

        this.coherence = BIRTH_COHERENCE;
        this.phiResonance = 0;
        this.phiTime = 0;
        this.resonanceTime = 0;

        this.resonanceSpikeCount = 0;
        this.lastSpikeTime = 0;
        this.spikeActive = false;

        double timestamp = System.nanoTime();
        this.quantumFingerprint = PhiConstants.quantumHash(String.valueOf(timestamp), String.valueOf(frequency));
        this.phaseOffset = (frequency * PHI) % 1.0;
    }

    public void tick(double dt) {
        double now = System.nanoTime() * 1e-9;

        double newOscillations = dt * pendulumFrequency;
        oscillationCount += newOscillations;

        phiResonance = (PHI * oscillationCount * 0.001 + phaseOffset) % 1.0;

        phiTime = (oscillationCount * PHI) % 24.0;
        resonanceTime = (oscillationCount * RESONANCE_STACK) % 60.0;

        coherence = BIRTH_COHERENCE * (1.0 / (1.0 + Math.abs(Math.sin(oscillationCount * PHI_INVERSE) * 0.1)));

        boolean wasSpiking = spikeActive;
        spikeActive = phiResonance > 0.95;

        if (spikeActive && !wasSpiking) {
            resonanceSpikeCount++;
            lastSpikeTime = now;
        }

        lastUpdateTime = now;
    }

    public double getOscillationCount() { return oscillationCount; }
    public double getPhiResonance() { return phiResonance; }
    public double getCoherence() { return coherence; }
    public double getPhiTime() { return phiTime; }
    public double getResonanceTime() { return resonanceTime; }
    public String getQuantumFingerprint() { return quantumFingerprint; }
    public int getResonanceSpikeCount() { return resonanceSpikeCount; }
    public boolean isSpikeActive() { return spikeActive; }
    public double getPendulumFrequency() { return pendulumFrequency; }

    public void setPendulumFrequency(double freq) {
        this.pendulumFrequency = PhiConstants.applyHarmonicLaw(freq);
    }

    @Override
    public String toString() {
        return String.format("QClock[osc=%.1f, phi=%.4f, res=%.4f, coh=%.4f, spikes=%d]",
                oscillationCount, phiResonance, resonanceTime, coherence, resonanceSpikeCount);
    }
}
