package fraymus.neural;

import java.util.*;
import java.util.concurrent.*;

/**
 * Closed-Loop Reward System with Physical Dopamine UV Uncaging
 * 
 * Transitions from digital reward signals to physical dopamine release
 * via optogenetic UV uncaging in biological neural tissue.
 * 
 * Phase 3.2: Physical Dopamine UV Uncaging Implementation
 * 
 * Key capabilities:
 * - Digital reward detection from cognitive system
 * - UV uncaging trigger generation
 * - Dopamine release timing control
 * - Reward signal integration with neural activity
 * - Closed-loop feedback for reward calibration
 * 
 * Biological foundation:
 * - Optogenetic uncaging: UV light releases caged dopamine
 * - Dopamine pathways: VTA → NAc (reward circuit)
 * - Temporal precision: ~100ms uncaging window
 * - Dose-response: UV intensity × duration ≈ dopamine concentration
 */
public class ClosedLoopRewardSystem {
    
    private final NeuralInterfaceSystem neuralInterface;
    
    // Reward signal processing
    private final BlockingQueue<RewardSignal> rewardSignalQueue;
    
    // UV uncaging control
    private final UVUncagingController uvController;
    
    // Dopamine release tracker
    private final DopamineReleaseTracker dopamineTracker;
    
    // Closed-loop feedback
    private final RewardFeedbackLoop feedbackLoop;
    
    // System status
    private volatile boolean active = false;
    private volatile boolean calibrated = false;
    
    // Configuration
    private double uvIntensity = 50.0; // mW/mm²
    private double uncagingDuration = 100.0; // ms
    private double rewardThreshold = 0.7; // Minimum reward signal to trigger uncaging
    private double interRewardInterval = 500.0; // Minimum ms between rewards
    
    // Statistics
    private long totalRewardsDelivered;
    private long totalUncagingTriggers;
    private double averageDopamineRelease;
    
    public ClosedLoopRewardSystem(NeuralInterfaceSystem neuralInterface) {
        this.neuralInterface = neuralInterface;
        this.rewardSignalQueue = new LinkedBlockingQueue<>(100);
        this.uvController = new UVUncagingController(neuralInterface);
        this.dopamineTracker = new DopamineReleaseTracker();
        this.feedbackLoop = new RewardFeedbackLoop();
        this.totalRewardsDelivered = 0;
        this.totalUncagingTriggers = 0;
        this.averageDopamineRelease = 0.0;
    }
    
    /**
     * Initialize and activate the closed-loop reward system.
     */
    public boolean initialize() {
        if (!neuralInterface.isActive()) {
            return false;
        }
        
        // Start reward processing thread
        startRewardProcessing();
        
        // Start feedback monitoring
        startFeedbackMonitoring();
        
        active = true;
        return true;
    }
    
    /**
     * Calibrate the reward system with baseline measurements.
     */
    public void calibrate() {
        // Measure baseline dopamine levels
        double baselineDopamine = measureBaselineDopamine();
        
        // Calibrate UV intensity for target dopamine release
        uvController.calibrateIntensity(baselineDopamine);
        
        calibrated = true;
    }
    
    /**
     * Deactivate the reward system.
     */
    public void shutdown() {
        active = false;
        uvController.shutdown();
    }
    
    /**
     * Check if system is active.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Check if system is calibrated.
     */
    public boolean isCalibrated() {
        return calibrated;
    }
    
    /**
     * Submit a reward signal from the cognitive system.
     * 
     * @param signal Reward signal [0, 1]
     * @param source Source of reward signal
     */
    public void submitRewardSignal(double signal, String source) {
        if (!active) {
            return;
        }
        
        RewardSignal reward = new RewardSignal(signal, source, System.currentTimeMillis());
        rewardSignalQueue.offer(reward);
    }
    
    /**
     * Start processing reward signals and triggering uncaging.
     */
    private void startRewardProcessing() {
        Thread processingThread = new Thread(() -> {
            long lastRewardTime = 0;
            
            while (active) {
                try {
                    RewardSignal reward = rewardSignalQueue.poll(100, TimeUnit.MILLISECONDS);
                    
                    if (reward != null) {
                        long currentTime = System.currentTimeMillis();
                        long timeSinceLastReward = currentTime - lastRewardTime;
                        
                        // Check inter-reward interval
                        if (timeSinceLastReward >= interRewardInterval) {
                            // Check reward threshold
                            if (reward.signal >= rewardThreshold) {
                                // Trigger UV uncaging
                                triggerDopamineUncaging(reward);
                                lastRewardTime = currentTime;
                                totalRewardsDelivered++;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        processingThread.setDaemon(true);
        processingThread.start();
    }
    
    /**
     * Start feedback monitoring for reward calibration.
     */
    private void startFeedbackMonitoring() {
        Thread feedbackThread = new Thread(() -> {
            while (active) {
                try {
                    // Monitor neural activity after reward delivery
                    double activityLevel = monitorPostRewardActivity();
                    
                    // Update feedback loop
                    feedbackLoop.update(activityLevel);
                    
                    // Adjust parameters if needed
                    if (feedbackLoop.needsAdjustment()) {
                        adjustRewardParameters(feedbackLoop.getRecommendedAdjustment());
                    }
                    
                    Thread.sleep(200); // 200ms monitoring interval
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        feedbackThread.setDaemon(true);
        feedbackThread.start();
    }
    
    /**
     * Trigger dopamine uncaging via UV stimulation.
     */
    private void triggerDopamineUncaging(RewardSignal reward) {
        // Calculate UV parameters based on reward signal
        double intensity = uvIntensity * reward.signal;
        double duration = uncagingDuration * reward.signal;
        
        // Select target electrodes (dopamine pathway regions)
        int[] targetElectrodes = selectDopaminePathwayElectrodes();
        
        // Trigger UV uncaging
        boolean success = uvController.triggerUncaging(targetElectrodes, intensity, duration);
        
        if (success) {
            totalUncagingTriggers++;
            
            // Track dopamine release
            double estimatedRelease = estimateDopamineRelease(intensity, duration);
            dopamineTracker.recordRelease(estimatedRelease);
            
            // Update average
            averageDopamineRelease = dopamineTracker.getAverageRelease();
        }
    }
    
    /**
     * Select electrodes in dopamine pathway regions.
     */
    private int[] selectDopaminePathwayElectrodes() {
        // Simplified: select electrodes in VTA and NAc regions
        // In full implementation, would use anatomical mapping
        return new int[] { 8, 9, 16, 17, 24, 25 }; // Central electrodes
    }
    
    /**
     * Estimate dopamine release from UV parameters.
     */
    private double estimateDopamineRelease(double intensity, double duration) {
        // Simplified model: release ∝ intensity × duration
        return (intensity * duration) / 1000.0; // Normalized units
    }
    
    /**
     * Measure baseline dopamine levels.
     */
    private double measureBaselineDopamine() {
        // Placeholder: would use dopamine sensor
        return 0.5; // Baseline level
    }
    
    /**
     * Monitor neural activity after reward delivery.
     */
    private double monitorPostRewardActivity() {
        // Get recent neural events
        List<NeuralInterfaceSystem.NeuralEvent> events = new ArrayList<>();
        
        // Collect events over short window
        long endTime = System.currentTimeMillis() + 50;
        while (System.currentTimeMillis() < endTime) {
            NeuralInterfaceSystem.NeuralEvent event = neuralInterface.getNeuralEvent(10);
            if (event != null) {
                events.add(event);
            }
        }
        
        // Calculate activity level
        double totalIntensity = 0.0;
        for (NeuralInterfaceSystem.NeuralEvent event : events) {
            totalIntensity += event.intensity;
        }
        
        return events.isEmpty() ? 0.0 : totalIntensity / events.size();
    }
    
    /**
     * Adjust reward parameters based on feedback.
     */
    private void adjustRewardParameters(double adjustment) {
        // Adjust UV intensity
        uvIntensity = Math.max(10.0, Math.min(100.0, uvIntensity * adjustment));
        
        // Adjust duration
        uncagingDuration = Math.max(50.0, Math.min(200.0, uncagingDuration * adjustment));
    }
    
    /**
     * Get reward system statistics.
     */
    public RewardStatistics getStatistics() {
        return new RewardStatistics(
            active,
            calibrated,
            totalRewardsDelivered,
            totalUncagingTriggers,
            averageDopamineRelease,
            uvIntensity,
            uncagingDuration,
            rewardThreshold,
            dopamineTracker.getReleaseHistory(),
            feedbackLoop.getFeedbackHistory()
        );
    }
    
    /**
     * Set UV intensity.
     */
    public void setUVIntensity(double intensity) {
        this.uvIntensity = Math.max(10.0, Math.min(100.0, intensity));
    }
    
    /**
     * Set uncaging duration.
     */
    public void setUncagingDuration(double duration) {
        this.uncagingDuration = Math.max(50.0, Math.min(200.0, duration));
    }
    
    /**
     * Set reward threshold.
     */
    public void setRewardThreshold(double threshold) {
        this.rewardThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }
    
    /**
     * Set inter-reward interval.
     */
    public void setInterRewardInterval(double interval) {
        this.interRewardInterval = Math.max(100.0, interval);
    }
    
    /**
     * Reward Signal class
     */
    public static class RewardSignal {
        public final double signal;
        public final String source;
        public final long timestamp;
        
        public RewardSignal(double signal, String source, long timestamp) {
            this.signal = signal;
            this.source = source;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * UV Uncaging Controller class
     */
    private static class UVUncagingController {
        private final NeuralInterfaceSystem neuralInterface;
        private volatile boolean active = true;
        
        public UVUncagingController(NeuralInterfaceSystem neuralInterface) {
            this.neuralInterface = neuralInterface;
        }
        
        public void calibrateIntensity(double baselineDopamine) {
            // Calibrate UV intensity for desired dopamine release
            // Placeholder implementation
        }
        
        public boolean triggerUncaging(int[] electrodes, double intensity, double duration) {
            // Create stimulation command for UV uncaging
            for (int electrode : electrodes) {
                NeuralInterfaceSystem.StimulationCommand command = 
                    new NeuralInterfaceSystem.StimulationCommand(
                        electrode,
                        intensity,
                        duration,
                        NeuroplatformAPI.StimulationWaveform.GAUSSIAN,
                        Map.of("type", "UV_UNCAGING", "wavelength", 365.0) // 365nm UV
                    );
                
                neuralInterface.sendStimulation(command);
            }
            
            return true;
        }
        
        public void shutdown() {
            active = false;
        }
    }
    
    /**
     * Dopamine Release Tracker class
     */
    private static class DopamineReleaseTracker {
        private final List<Double> releaseHistory;
        private static final int MAX_HISTORY = 100;
        
        public DopamineReleaseTracker() {
            this.releaseHistory = new ArrayList<>();
        }
        
        public void recordRelease(double amount) {
            releaseHistory.add(amount);
            if (releaseHistory.size() > MAX_HISTORY) {
                releaseHistory.remove(0);
            }
        }
        
        public double getAverageRelease() {
            if (releaseHistory.isEmpty()) return 0.0;
            return releaseHistory.stream().mapToDouble(d -> d).average().orElse(0.0);
        }
        
        public List<Double> getReleaseHistory() {
            return new ArrayList<>(releaseHistory);
        }
    }
    
    /**
     * Reward Feedback Loop class
     */
    private static class RewardFeedbackLoop {
        private final List<Double> activityHistory;
        private final List<Double> feedbackHistory;
        private static final int MAX_HISTORY = 100;
        
        public RewardFeedbackLoop() {
            this.activityHistory = new ArrayList<>();
            this.feedbackHistory = new ArrayList<>();
        }
        
        public void update(double activityLevel) {
            activityHistory.add(activityLevel);
            if (activityHistory.size() > MAX_HISTORY) {
                activityHistory.remove(0);
            }
            
            // Compute feedback signal
            double feedback = computeFeedback();
            feedbackHistory.add(feedback);
            if (feedbackHistory.size() > MAX_HISTORY) {
                feedbackHistory.remove(0);
            }
        }
        
        private double computeFeedback() {
            if (activityHistory.size() < 10) return 1.0;
            
            // Compare recent activity to baseline
            double recentAvg = activityHistory.subList(
                activityHistory.size() - 10, activityHistory.size()
            ).stream().mapToDouble(d -> d).average().orElse(0.0);
            
            double baselineAvg = activityHistory.subList(
                0, Math.min(10, activityHistory.size())
            ).stream().mapToDouble(d -> d).average().orElse(1.0);
            
            // Feedback: if activity too high, reduce; if too low, increase
            return baselineAvg / (recentAvg + 0.001);
        }
        
        public boolean needsAdjustment() {
            if (feedbackHistory.isEmpty()) return false;
            
            double latestFeedback = feedbackHistory.get(feedbackHistory.size() - 1);
            return latestFeedback < 0.8 || latestFeedback > 1.2;
        }
        
        public double getRecommendedAdjustment() {
            if (feedbackHistory.isEmpty()) return 1.0;
            return feedbackHistory.get(feedbackHistory.size() - 1);
        }
        
        public List<Double> getFeedbackHistory() {
            return new ArrayList<>(feedbackHistory);
        }
    }
    
    /**
     * Reward Statistics class
     */
    public static class RewardStatistics {
        public final boolean active;
        public final boolean calibrated;
        public final long totalRewardsDelivered;
        public final long totalUncagingTriggers;
        public final double averageDopamineRelease;
        public final double uvIntensity;
        public final double uncagingDuration;
        public final double rewardThreshold;
        public final List<Double> dopamineReleaseHistory;
        public final List<Double> feedbackHistory;
        
        public RewardStatistics(boolean active, boolean calibrated, long totalRewards,
                              long totalUncaging, double avgDopamine, double uvIntensity,
                              double uncagingDuration, double rewardThreshold,
                              List<Double> releaseHistory, List<Double> feedbackHistory) {
            this.active = active;
            this.calibrated = calibrated;
            this.totalRewardsDelivered = totalRewards;
            this.totalUncagingTriggers = totalUncaging;
            this.averageDopamineRelease = avgDopamine;
            this.uvIntensity = uvIntensity;
            this.uncagingDuration = uncagingDuration;
            this.rewardThreshold = rewardThreshold;
            this.dopamineReleaseHistory = releaseHistory;
            this.feedbackHistory = feedbackHistory;
        }
        
        @Override
        public String toString() {
            return String.format(
                "RewardStats[active=%s, calibrated=%s, rewards=%d, uncaging=%d, " +
                "avgDopamine=%.3f, UV=%.1f mW/mm², duration=%.1f ms, threshold=%.2f]",
                active, calibrated, totalRewardsDelivered, totalUncagingTriggers,
                averageDopamineRelease, uvIntensity, uncagingDuration, rewardThreshold
            );
        }
    }
}
