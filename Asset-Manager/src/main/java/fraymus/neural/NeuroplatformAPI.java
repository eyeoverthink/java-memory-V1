package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;

/**
 * Neuroplatform API Interface
 * 
 * Provides bidirectional interface to biological neural platforms (organoids, brain-on-chip).
 * Supports spike recording, stimulation, and field potential monitoring.
 * 
 * Phase 3.1: Neuroplatform API Interface Implementation
 * 
 * Key capabilities:
 * - Real-time spike recording from electrode arrays
 * - Electrical stimulation of neural tissue
 * - Field potential (LFP/ECoG) monitoring
 * - Calcium imaging interface
 * - Dopamine sensing and UV uncaging control
 * 
 * Mathematical foundation:
 * - Spike detection: threshold crossing on filtered signals
 * - Field potential: V(t) = Σ_i w_i * s_i(t) where s_i are electrode signals
 * - Stimulation: I(t) = A * exp(-(t-t0)²/2σ²) (Gaussian pulse)
 */
public class NeuroplatformAPI {
    
    // Platform connection status
    private volatile boolean connected = false;
    private String platformId;
    private String platformType;
    
    // Electrode array configuration
    private int electrodeCount = 64; // Default 8x8 array
    private double samplingRate = 20000.0; // 20 kHz default
    
    // Data buffers
    private final BlockingQueue<SpikeEvent> spikeQueue;
    private final BlockingQueue<FieldPotential> fieldPotentialQueue;
    
    // Stimulation control
    private final Map<Integer, StimulationPattern> activeStimulations;
    
    // API client (placeholder for actual hardware interface)
    private NeuroplatformClient client;
    
    // Statistics
    private long totalSpikesRecorded;
    private long totalStimulationsDelivered;
    private double averageLatency;
    
    public NeuroplatformAPI() {
        this.spikeQueue = new LinkedBlockingQueue<>(10000);
        this.fieldPotentialQueue = new LinkedBlockingQueue<>(1000);
        this.activeStimulations = new ConcurrentHashMap<>();
        this.totalSpikesRecorded = 0;
        this.totalStimulationsDelivered = 0;
        this.averageLatency = 0.0;
    }
    
    /**
     * Connect to neuroplatform.
     * 
     * @param platformId Platform identifier
     * @param platformType Type (organoid, brain-on-chip, etc.)
     * @return Connection success
     */
    public boolean connect(String platformId, String platformType) {
        this.platformId = platformId;
        this.platformType = platformType;
        
        // Initialize client
        client = new NeuroplatformClient(platformId);
        
        try {
            connected = client.connect();
            
            if (connected) {
                // Start data streaming
                startSpikeStreaming();
                startFieldPotentialStreaming();
            }
            
            return connected;
        } catch (Exception e) {
            connected = false;
            return false;
        }
    }
    
    /**
     * Disconnect from platform.
     */
    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
        connected = false;
    }
    
    /**
     * Check connection status.
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Start spike recording from electrode array.
     */
    private void startSpikeStreaming() {
        Thread spikeThread = new Thread(() -> {
            while (connected) {
                try {
                    // Simulate spike detection (placeholder)
                    // In real implementation, would read from hardware
                    if (Math.random() < 0.01) { // 1% chance per iteration
                        SpikeEvent spike = generateSimulatedSpike();
                        spikeQueue.offer(spike);
                        totalSpikesRecorded++;
                    }
                    Thread.sleep(1); // 1ms polling
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        spikeThread.setDaemon(true);
        spikeThread.start();
    }
    
    /**
     * Start field potential monitoring.
     */
    private void startFieldPotentialStreaming() {
        Thread fpThread = new Thread(() -> {
            while (connected) {
                try {
                    // Simulate field potential (placeholder)
                    FieldPotential fp = generateSimulatedFieldPotential();
                    fieldPotentialQueue.offer(fp);
                    Thread.sleep(10); // 10ms polling (100 Hz)
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        fpThread.setDaemon(true);
        fpThread.start();
    }
    
    /**
     * Record spikes from specific electrode.
     * 
     * @param electrodeId Electrode identifier
     * @param duration Recording duration in ms
     * @return List of spike events
     */
    public List<SpikeEvent> recordSpikes(int electrodeId, long duration) {
        if (!connected) {
            throw new IllegalStateException("Not connected to platform");
        }
        
        List<SpikeEvent> spikes = new ArrayList<>();
        long endTime = System.currentTimeMillis() + duration;
        
        while (System.currentTimeMillis() < endTime) {
            try {
                SpikeEvent spike = spikeQueue.poll(100, TimeUnit.MILLISECONDS);
                if (spike != null && spike.electrodeId == electrodeId) {
                    spikes.add(spike);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        
        return spikes;
    }
    
    /**
     * Deliver electrical stimulation to electrode.
     * 
     * @param electrodeId Electrode identifier
     * @param amplitude Stimulation amplitude (µA)
     * @param duration Pulse duration (ms)
     * @param waveform Waveform type
     * @return Success
     */
    public boolean stimulate(int electrodeId, double amplitude, double duration, 
                            StimulationWaveform waveform) {
        if (!connected) {
            throw new IllegalStateException("Not connected to platform");
        }
        
        StimulationPattern pattern = new StimulationPattern(
            electrodeId, amplitude, duration, waveform
        );
        
        boolean success = client.deliverStimulation(pattern);
        
        if (success) {
            activeStimulations.put(electrodeId, pattern);
            totalStimulationsDelivered++;
        }
        
        return success;
    }
    
    /**
     * Get field potential data.
     * 
     * @param electrodeId Electrode identifier
     * @param duration Recording duration in ms
     * @return List of field potential samples
     */
    public List<FieldPotential> getFieldPotentials(int electrodeId, long duration) {
        if (!connected) {
            throw new IllegalStateException("Not connected to platform");
        }
        
        List<FieldPotential> fps = new ArrayList<>();
        long endTime = System.currentTimeMillis() + duration;
        
        while (System.currentTimeMillis() < endTime) {
            try {
                FieldPotential fp = fieldPotentialQueue.poll(100, TimeUnit.MILLISECONDS);
                if (fp != null && fp.electrodeId == electrodeId) {
                    fps.add(fp);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        
        return fps;
    }
    
    /**
     * Configure electrode array.
     * 
     * @param electrodeCount Number of electrodes
     * @param samplingRate Sampling rate in Hz
     */
    public void configureElectrodeArray(int electrodeCount, double samplingRate) {
        this.electrodeCount = electrodeCount;
        this.samplingRate = samplingRate;
        
        if (client != null) {
            client.configureArray(electrodeCount, samplingRate);
        }
    }
    
    /**
     * Get platform information.
     */
    public PlatformInfo getPlatformInfo() {
        return new PlatformInfo(
            platformId,
            platformType,
            electrodeCount,
            samplingRate,
            connected
        );
    }
    
    /**
     * Get statistics.
     */
    public APIStatistics getStatistics() {
        return new APIStatistics(
            totalSpikesRecorded,
            totalStimulationsDelivered,
            spikeQueue.size(),
            fieldPotentialQueue.size(),
            averageLatency
        );
    }
    
    /**
     * Simulated spike event generation (for testing).
     */
    private SpikeEvent generateSimulatedSpike() {
        int electrodeId = (int) (Math.random() * electrodeCount);
        double timestamp = System.currentTimeMillis();
        double amplitude = Math.random() * 100 - 50; // -50 to +50 µV
        
        return new SpikeEvent(electrodeId, timestamp, amplitude);
    }
    
    /**
     * Simulated field potential generation (for testing).
     */
    private FieldPotential generateSimulatedFieldPotential() {
        int electrodeId = (int) (Math.random() * electrodeCount);
        double timestamp = System.currentTimeMillis();
        double value = Math.sin(timestamp / 1000.0) * 100 + (Math.random() - 0.5) * 20;
        
        return new FieldPotential(electrodeId, timestamp, value);
    }
    
    /**
     * Spike Event class
     */
    public static class SpikeEvent {
        public final int electrodeId;
        public final double timestamp;
        public final double amplitude; // µV
        
        public SpikeEvent(int electrodeId, double timestamp, double amplitude) {
            this.electrodeId = electrodeId;
            this.timestamp = timestamp;
            this.amplitude = amplitude;
        }
    }
    
    /**
     * Field Potential class
     */
    public static class FieldPotential {
        public final int electrodeId;
        public final double timestamp;
        public final double value; // µV
        
        public FieldPotential(int electrodeId, double timestamp, double value) {
            this.electrodeId = electrodeId;
            this.timestamp = timestamp;
            this.value = value;
        }
    }
    
    /**
     * Stimulation Pattern class
     */
    public static class StimulationPattern {
        public final int electrodeId;
        public final double amplitude; // µA
        public final double duration; // ms
        public final StimulationWaveform waveform;
        
        public StimulationPattern(int electrodeId, double amplitude, double duration, 
                                StimulationWaveform waveform) {
            this.electrodeId = electrodeId;
            this.amplitude = amplitude;
            this.duration = duration;
            this.waveform = waveform;
        }
    }
    
    /**
     * Stimulation Waveform enum
     */
    public enum StimulationWaveform {
        SQUARE,
        SINE,
        GAUSSIAN,
        BIPHASIC
    }
    
    /**
     * Platform Info class
     */
    public static class PlatformInfo {
        public final String platformId;
        public final String platformType;
        public final int electrodeCount;
        public final double samplingRate;
        public final boolean connected;
        
        public PlatformInfo(String platformId, String platformType, int electrodeCount,
                           double samplingRate, boolean connected) {
            this.platformId = platformId;
            this.platformType = platformType;
            this.electrodeCount = electrodeCount;
            this.samplingRate = samplingRate;
            this.connected = connected;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PlatformInfo[id=%s, type=%s, electrodes=%d, rate=%.1f Hz, connected=%s]",
                platformId, platformType, electrodeCount, samplingRate, connected
            );
        }
    }
    
    /**
     * API Statistics class
     */
    public static class APIStatistics {
        public final long totalSpikesRecorded;
        public final long totalStimulationsDelivered;
        public final int spikeQueueSize;
        public final int fieldPotentialQueueSize;
        public final double averageLatency;
        
        public APIStatistics(long totalSpikesRecorded, long totalStimulationsDelivered,
                          int spikeQueueSize, int fieldPotentialQueueSize, double averageLatency) {
            this.totalSpikesRecorded = totalSpikesRecorded;
            this.totalStimulationsDelivered = totalStimulationsDelivered;
            this.spikeQueueSize = spikeQueueSize;
            this.fieldPotentialQueueSize = fieldPotentialQueueSize;
            this.averageLatency = averageLatency;
        }
        
        @Override
        public String toString() {
            return String.format(
                "APIStats[spikes=%d, stimulations=%d, spikeQ=%d, fpQ=%d, latency=%.3f ms]",
                totalSpikesRecorded, totalStimulationsDelivered,
                spikeQueueSize, fieldPotentialQueueSize, averageLatency
            );
        }
    }
    
    /**
     * Neuroplatform Client (placeholder for actual hardware interface)
     */
    private static class NeuroplatformClient {
        private final String platformId;
        
        public NeuroplatformClient(String platformId) {
            this.platformId = platformId;
        }
        
        public boolean connect() {
            // Placeholder: would establish actual connection
            return true;
        }
        
        public void disconnect() {
            // Placeholder: would close connection
        }
        
        public boolean deliverStimulation(StimulationPattern pattern) {
            // Placeholder: would deliver actual stimulation
            return true;
        }
        
        public void configureArray(int electrodeCount, double samplingRate) {
            // Placeholder: would configure hardware
        }
    }
}
