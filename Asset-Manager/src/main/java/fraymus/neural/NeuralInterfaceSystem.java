package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;

/**
 * Neural Interface System
 * 
 * Bidirectional bio-digital transduction system for interfacing Fraynix with
 * biological neural platforms (organoids, brain-on-chip).
 * 
 * Phase 3.1: NeuralInterfaceSystem Implementation
 * 
 * Key capabilities:
 * - Spike-to-digital translation (neural spikes → digital events)
 * - Digital-to-stimulation translation (digital commands → neural stimulation)
 * - Field potential monitoring for state synchronization
 * - Real-time latency tracking for sub-1ms synchronization
 * - Closed-loop feedback integration
 * 
 * Architecture:
 * - Input Layer: Neuroplatform API → Spike Decoder → Cognitive Events
 * - Output Layer: Cognitive Events → Stimulation Encoder → Neuroplatform API
 * - Sync Layer: Field Potentials → State Synchronizer → Working Memory
 */
public class NeuralInterfaceSystem {
    
    private final NeuroplatformAPI neuroplatform;
    
    // Spike decoder
    private final SpikeDecoder spikeDecoder;
    
    // Stimulation encoder
    private final StimulationEncoder stimulationEncoder;
    
    // State synchronizer
    private final StateSynchronizer stateSynchronizer;
    
    // Event queues
    private final BlockingQueue<NeuralEvent> inputEventQueue;
    private final BlockingQueue<StimulationCommand> outputCommandQueue;
    
    // Latency tracking
    private final LatencyTracker latencyTracker;
    
    // Interface status
    private volatile boolean active = false;
    private volatile boolean isSynchronized = false;
    
    // Configuration
    private double targetLatency = 0.5; // Target 0.5ms latency
    private double synchronizationThreshold = 0.8; // Correlation threshold for sync
    
    public NeuralInterfaceSystem(NeuroplatformAPI neuroplatform) {
        this.neuroplatform = neuroplatform;
        this.spikeDecoder = new SpikeDecoder();
        this.stimulationEncoder = new StimulationEncoder();
        this.stateSynchronizer = new StateSynchronizer();
        this.inputEventQueue = new LinkedBlockingQueue<>(1000);
        this.outputCommandQueue = new LinkedBlockingQueue<>(1000);
        this.latencyTracker = new LatencyTracker();
    }
    
    /**
     * Initialize and activate the neural interface.
     */
    public boolean initialize() {
        if (!neuroplatform.isConnected()) {
            return false;
        }
        
        // Start input processing thread
        startInputProcessing();
        
        // Start output processing thread
        startOutputProcessing();
        
        // Start synchronization monitoring
        startSynchronizationMonitoring();
        
        active = true;
        return true;
    }
    
    /**
     * Deactivate the neural interface.
     */
    public void shutdown() {
        active = false;
        isSynchronized = false;
    }
    
    /**
     * Check if interface is active.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Check if isSynchronized with neural platform.
     */
    public boolean isSynchronized() {
        return isSynchronized;
    }
    
    /**
     * Start processing neural spikes into digital events.
     */
    private void startInputProcessing() {
        Thread inputThread = new Thread(() -> {
            while (active) {
                try {
                    // Record spikes from all electrodes
                    List<NeuroplatformAPI.SpikeEvent> spikes = 
                        neuroplatform.recordSpikes(-1, 10); // All electrodes, 10ms window
                    
                    long startTime = System.nanoTime();
                    
                    // Decode spikes into neural events
                    for (NeuroplatformAPI.SpikeEvent spike : spikes) {
                        NeuralEvent event = spikeDecoder.decode(spike);
                        if (event != null) {
                            inputEventQueue.offer(event);
                        }
                    }
                    
                    long endTime = System.nanoTime();
                    latencyTracker.recordInputLatency((endTime - startTime) / 1_000_000.0);
                    
                    Thread.sleep(5); // 5ms polling
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }
    
    /**
     * Start processing digital commands into neural stimulation.
     */
    private void startOutputProcessing() {
        Thread outputThread = new Thread(() -> {
            while (active) {
                try {
                    StimulationCommand command = outputCommandQueue.poll(10, TimeUnit.MILLISECONDS);
                    
                    if (command != null) {
                        long startTime = System.nanoTime();
                        
                        // Encode command into stimulation pattern
                        NeuroplatformAPI.StimulationPattern pattern = 
                            stimulationEncoder.encode(command);
                        
                        // Deliver stimulation
                        boolean success = neuroplatform.stimulate(
                            pattern.electrodeId,
                            pattern.amplitude,
                            pattern.duration,
                            pattern.waveform
                        );
                        
                        long endTime = System.nanoTime();
                        latencyTracker.recordOutputLatency((endTime - startTime) / 1_000_000.0);
                        
                        if (!success) {
                            // Log stimulation failure
                            System.err.println("Stimulation failed for electrode " + 
                                pattern.electrodeId);
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        outputThread.setDaemon(true);
        outputThread.start();
    }
    
    /**
     * Start monitoring synchronization with field potentials.
     */
    private void startSynchronizationMonitoring() {
        Thread syncThread = new Thread(() -> {
            while (active) {
                try {
                    // Get field potentials
                    List<NeuroplatformAPI.FieldPotential> fps = 
                        neuroplatform.getFieldPotentials(-1, 100); // 100ms window
                    
                    // Check synchronization
                    double syncScore = stateSynchronizer.computeSynchronizationScore(fps);
                    
                    isSynchronized = syncScore >= synchronizationThreshold;
                    
                    // Update latency tracker with sync info
                    latencyTracker.recordSyncScore(syncScore);
                    
                    Thread.sleep(50); // 50ms monitoring
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        syncThread.setDaemon(true);
        syncThread.start();
    }
    
    /**
     * Get neural events from input queue.
     * 
     * @param timeout Timeout in milliseconds
     * @return Neural event or null if timeout
     */
    public NeuralEvent getNeuralEvent(long timeout) {
        try {
            return inputEventQueue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }
    
    /**
     * Send stimulation command.
     * 
     * @param command Stimulation command
     * @return Success
     */
    public boolean sendStimulation(StimulationCommand command) {
        return outputCommandQueue.offer(command);
    }
    
    /**
     * Get current latency statistics.
     */
    public LatencyStatistics getLatencyStatistics() {
        return latencyTracker.getStatistics();
    }
    
    /**
     * Set target latency.
     */
    public void setTargetLatency(double latency) {
        this.targetLatency = latency;
    }
    
    /**
     * Set synchronization threshold.
     */
    public void setSynchronizationThreshold(double threshold) {
        this.synchronizationThreshold = threshold;
    }
    
    /**
     * Get interface statistics.
     */
    public InterfaceStatistics getStatistics() {
        return new InterfaceStatistics(
            active,
            isSynchronized,
            inputEventQueue.size(),
            outputCommandQueue.size(),
            latencyTracker.getStatistics(),
            neuroplatform.getStatistics()
        );
    }
    
    /**
     * Neural Event class
     */
    public static class NeuralEvent {
        public final int electrodeId;
        public final double timestamp;
        public final EventType eventType;
        public final double intensity;
        public final Map<String, Object> metadata;
        
        public NeuralEvent(int electrodeId, double timestamp, EventType eventType, 
                          double intensity, Map<String, Object> metadata) {
            this.electrodeId = electrodeId;
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.intensity = intensity;
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }
    }
    
    /**
     * Event Type enum
     */
    public enum EventType {
        SPIKE_BURST,
        SINGLE_SPIKE,
        OSCILLATION,
        SILENCE,
        UNKNOWN
    }
    
    /**
     * Stimulation Command class
     */
    public static class StimulationCommand {
        public final int electrodeId;
        public final double amplitude;
        public final double duration;
        public final NeuroplatformAPI.StimulationWaveform waveform;
        public final Map<String, Object> parameters;
        
        public StimulationCommand(int electrodeId, double amplitude, double duration,
                                NeuroplatformAPI.StimulationWaveform waveform,
                                Map<String, Object> parameters) {
            this.electrodeId = electrodeId;
            this.amplitude = amplitude;
            this.duration = duration;
            this.waveform = waveform;
            this.parameters = parameters != null ? parameters : new HashMap<>();
        }
    }
    
    /**
     * Spike Decoder class
     */
    private static class SpikeDecoder {
        private final Map<Integer, Double> baselineActivity;
        
        public SpikeDecoder() {
            this.baselineActivity = new ConcurrentHashMap<>();
        }
        
        public NeuralEvent decode(NeuroplatformAPI.SpikeEvent spike) {
            // Determine event type based on spike characteristics
            EventType eventType = determineEventType(spike);
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("rawAmplitude", spike.amplitude);
            
            return new NeuralEvent(
                spike.electrodeId,
                spike.timestamp,
                eventType,
                Math.abs(spike.amplitude),
                metadata
            );
        }
        
        private EventType determineEventType(NeuroplatformAPI.SpikeEvent spike) {
            double amplitude = Math.abs(spike.amplitude);
            
            if (amplitude > 50.0) {
                return EventType.SPIKE_BURST;
            } else if (amplitude > 20.0) {
                return EventType.SINGLE_SPIKE;
            } else if (amplitude > 5.0) {
                return EventType.OSCILLATION;
            } else {
                return EventType.SILENCE;
            }
        }
    }
    
    /**
     * Stimulation Encoder class
     */
    private static class StimulationEncoder {
        public NeuroplatformAPI.StimulationPattern encode(StimulationCommand command) {
            return new NeuroplatformAPI.StimulationPattern(
                command.electrodeId,
                command.amplitude,
                command.duration,
                command.waveform
            );
        }
    }
    
    /**
     * State Synchronizer class
     */
    private static class StateSynchronizer {
        private final List<Double> fieldPotentialHistory;
        private static final int HISTORY_SIZE = 100;
        
        public StateSynchronizer() {
            this.fieldPotentialHistory = new ArrayList<>();
        }
        
        public double computeSynchronizationScore(List<NeuroplatformAPI.FieldPotential> fps) {
            if (fps.isEmpty()) {
                return 0.0;
            }
            
            // Add to history
            for (NeuroplatformAPI.FieldPotential fp : fps) {
                fieldPotentialHistory.add(fp.value);
                if (fieldPotentialHistory.size() > HISTORY_SIZE) {
                    fieldPotentialHistory.remove(0);
                }
            }
            
            // Compute synchronization score based on field potential coherence
            return computeCoherence();
        }
        
        private double computeCoherence() {
            if (fieldPotentialHistory.size() < 10) {
                return 0.5; // Default mid-range
            }
            
            // Compute variance as inverse measure of coherence
            double mean = fieldPotentialHistory.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
            
            double variance = fieldPotentialHistory.stream()
                .mapToDouble(d -> (d - mean) * (d - mean))
                .average()
                .orElse(1.0);
            
            // Normalize to [0, 1]
            double coherence = 1.0 / (1.0 + variance / 1000.0);
            
            return Math.max(0.0, Math.min(1.0, coherence));
        }
    }
    
    /**
     * Latency Tracker class
     */
    private static class LatencyTracker {
        private final List<Double> inputLatencies;
        private final List<Double> outputLatencies;
        private final List<Double> syncScores;
        private static final int MAX_HISTORY = 1000;
        
        public LatencyTracker() {
            this.inputLatencies = new ArrayList<>();
            this.outputLatencies = new ArrayList<>();
            this.syncScores = new ArrayList<>();
        }
        
        public void recordInputLatency(double latency) {
            inputLatencies.add(latency);
            if (inputLatencies.size() > MAX_HISTORY) {
                inputLatencies.remove(0);
            }
        }
        
        public void recordOutputLatency(double latency) {
            outputLatencies.add(latency);
            if (outputLatencies.size() > MAX_HISTORY) {
                outputLatencies.remove(0);
            }
        }
        
        public void recordSyncScore(double score) {
            syncScores.add(score);
            if (syncScores.size() > MAX_HISTORY) {
                syncScores.remove(0);
            }
        }
        
        public LatencyStatistics getStatistics() {
            double avgInput = inputLatencies.isEmpty() ? 0.0 :
                inputLatencies.stream().mapToDouble(d -> d).average().orElse(0.0);
            double avgOutput = outputLatencies.isEmpty() ? 0.0 :
                outputLatencies.stream().mapToDouble(d -> d).average().orElse(0.0);
            double avgSync = syncScores.isEmpty() ? 0.0 :
                syncScores.stream().mapToDouble(d -> d).average().orElse(0.0);
            
            double maxInput = inputLatencies.isEmpty() ? 0.0 :
                inputLatencies.stream().mapToDouble(d -> d).max().orElse(0.0);
            double maxOutput = outputLatencies.isEmpty() ? 0.0 :
                outputLatencies.stream().mapToDouble(d -> d).max().orElse(0.0);
            
            return new LatencyStatistics(
                avgInput, maxInput, inputLatencies.size(),
                avgOutput, maxOutput, outputLatencies.size(),
                avgSync, syncScores.size()
            );
        }
    }
    
    /**
     * Latency Statistics class
     */
    public static class LatencyStatistics {
        public final double averageInputLatency;
        public final double maxInputLatency;
        public final int inputSampleCount;
        public final double averageOutputLatency;
        public final double maxOutputLatency;
        public final int outputSampleCount;
        public final double averageSyncScore;
        public final int syncSampleCount;
        
        public LatencyStatistics(double avgInput, double maxInput, int inputCount,
                               double avgOutput, double maxOutput, int outputCount,
                               double avgSync, int syncCount) {
            this.averageInputLatency = avgInput;
            this.maxInputLatency = maxInput;
            this.inputSampleCount = inputCount;
            this.averageOutputLatency = avgOutput;
            this.maxOutputLatency = maxOutput;
            this.outputSampleCount = outputCount;
            this.averageSyncScore = avgSync;
            this.syncSampleCount = syncCount;
        }
        
        public boolean meetsSubMillisecondTarget() {
            return averageInputLatency < 1.0 && averageOutputLatency < 1.0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "LatencyStats[input: avg=%.3f ms, max=%.3f ms, n=%d; " +
                "output: avg=%.3f ms, max=%.3f ms, n=%d; sync=%.3f, n=%d]",
                averageInputLatency, maxInputLatency, inputSampleCount,
                averageOutputLatency, maxOutputLatency, outputSampleCount,
                averageSyncScore, syncSampleCount
            );
        }
    }
    
    /**
     * Interface Statistics class
     */
    public static class InterfaceStatistics {
        public final boolean active;
        public final boolean isSynchronized;
        public final int inputQueueSize;
        public final int outputQueueSize;
        public final LatencyStatistics latencyStats;
        public final NeuroplatformAPI.APIStatistics neuroplatformStats;
        
        public InterfaceStatistics(boolean active, boolean isSynchronized, int inputQueueSize,
                                 int outputQueueSize, LatencyStatistics latencyStats,
                                 NeuroplatformAPI.APIStatistics neuroplatformStats) {
            this.active = active;
            this.isSynchronized = isSynchronized;
            this.inputQueueSize = inputQueueSize;
            this.outputQueueSize = outputQueueSize;
            this.latencyStats = latencyStats;
            this.neuroplatformStats = neuroplatformStats;
        }
        
        @Override
        public String toString() {
            return String.format(
                "InterfaceStats[active=%s, synced=%s, inputQ=%d, outputQ=%d, %s, %s]",
                active, isSynchronized, inputQueueSize, outputQueueSize,
                latencyStats, neuroplatformStats
            );
        }
    }
}
