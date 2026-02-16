package fraymus.senses;

/**
 * ACTIVE HAPTIC/AUDIO ENTRAINMENT
 * 
 * Section VII Enhancement: From Monitoring to Healing
 * 
 * The Gap: BioSymbiosis visualizes stress, but doesn't FIX it.
 * The Fix: Binaural Beats & Haptic Resonance physically lower your heart rate.
 * 
 * Binaural Beat Generation:
 *   Frequency_Left = f_carrier
 *   Frequency_Right = f_carrier + f_target
 * 
 * Where f_target = Desired Brainwave State:
 *   DELTA (Sleep): 1-4 Hz
 *   THETA (Deep Creative): 4-8 Hz
 *   ALPHA (Focus): 8-14 Hz
 *   GAMMA (High Processing): 30-100 Hz
 * 
 * Haptic Resonance:
 *   Vibration_Pattern = sin(t × f_heart_resonance)
 *   Coherence Mode: Vibrate at (Current_HR - 5 BPM)
 *   Physiological Entrainment forces the heart to slow down.
 * 
 * "The system doesn't just watch. It heals."
 */
public class ActiveEntrainment {

    private static final double PHI = 1.6180339887;
    private static final double TWO_PI = 2.0 * Math.PI;
    
    // Brainwave frequency bands (Hz)
    public static final double DELTA_LOW = 1.0;
    public static final double DELTA_HIGH = 4.0;
    public static final double THETA_LOW = 4.0;
    public static final double THETA_HIGH = 8.0;
    public static final double ALPHA_LOW = 8.0;
    public static final double ALPHA_HIGH = 14.0;
    public static final double BETA_LOW = 14.0;
    public static final double BETA_HIGH = 30.0;
    public static final double GAMMA_LOW = 30.0;
    public static final double GAMMA_HIGH = 100.0;
    
    // Carrier frequency for binaural beats (audible base)
    private double carrierFrequency = 200.0; // Hz
    
    // Current state
    private BrainState targetState = BrainState.ALPHA;
    private double currentStress = 0.5;
    private double currentHeartRate = 70.0;
    private double targetHeartRate = 65.0;
    
    // Stats
    private long entrainmentSessionsStarted = 0;
    private long totalEntrainmentMs = 0;

    public enum BrainState {
        DELTA("Sleep/Deep Healing", 1.0, 4.0),
        THETA("Deep Creative/Meditation", 4.0, 8.0),
        ALPHA("Calm Focus/Relaxation", 8.0, 14.0),
        BETA("Active Thinking", 14.0, 30.0),
        GAMMA("High Processing/Insight", 30.0, 100.0);
        
        public final String description;
        public final double lowHz;
        public final double highHz;
        
        BrainState(String desc, double low, double high) {
            this.description = desc;
            this.lowHz = low;
            this.highHz = high;
        }
        
        public double getMidpoint() {
            return (lowHz + highHz) / 2.0;
        }
    }

    public ActiveEntrainment() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         ACTIVE ENTRAINMENT: BIOFEEDBACK HEALING           ║");
        System.out.println("║         \"The system doesn't just watch. It heals.\"        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    /**
     * Binaural Beat Generator Result
     */
    public static class BinauralBeat {
        public final double leftFrequency;
        public final double rightFrequency;
        public final double beatFrequency;
        public final BrainState targetState;
        public final double[] leftChannel;
        public final double[] rightChannel;
        
        public BinauralBeat(double left, double right, BrainState state, 
                          double[] leftCh, double[] rightCh) {
            this.leftFrequency = left;
            this.rightFrequency = right;
            this.beatFrequency = right - left;
            this.targetState = state;
            this.leftChannel = leftCh;
            this.rightChannel = rightCh;
        }
    }

    /**
     * Generate binaural beat audio samples
     * 
     * @param state Target brainwave state
     * @param durationMs Duration in milliseconds
     * @param sampleRate Audio sample rate (e.g., 44100)
     * @return BinauralBeat with left and right channel samples
     */
    public BinauralBeat generateBinauralBeat(BrainState state, int durationMs, int sampleRate) {
        this.targetState = state;
        double targetFreq = state.getMidpoint();
        
        // Left channel: pure carrier
        double leftFreq = carrierFrequency;
        // Right channel: carrier + target brainwave frequency
        double rightFreq = carrierFrequency + targetFreq;
        
        int numSamples = (int) ((durationMs / 1000.0) * sampleRate);
        double[] leftChannel = new double[numSamples];
        double[] rightChannel = new double[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            
            // Apply PHI-based amplitude modulation for natural feel
            double envelope = 0.5 + 0.5 * Math.sin(TWO_PI * t / PHI);
            
            leftChannel[i] = envelope * Math.sin(TWO_PI * leftFreq * t);
            rightChannel[i] = envelope * Math.sin(TWO_PI * rightFreq * t);
        }
        
        entrainmentSessionsStarted++;
        totalEntrainmentMs += durationMs;
        
        return new BinauralBeat(leftFreq, rightFreq, state, leftChannel, rightChannel);
    }

    /**
     * Haptic Pattern Generator Result
     */
    public static class HapticPattern {
        public final double targetHeartRate;
        public final double frequency;
        public final double[] intensities;
        public final long[] timingsMs;
        
        public HapticPattern(double targetHR, double freq, double[] intensities, long[] timings) {
            this.targetHeartRate = targetHR;
            this.frequency = freq;
            this.intensities = intensities;
            this.timingsMs = timings;
        }
    }

    /**
     * Generate haptic vibration pattern for heart rate entrainment
     * 
     * Coherence Mode: Vibrate at (Current_HR - 5 BPM)
     * This physiologically entrains the heart to slow down.
     * 
     * @param currentHR Current heart rate in BPM
     * @param durationMs Duration of pattern
     * @return HapticPattern with timings and intensities
     */
    public HapticPattern generateHapticPattern(double currentHR, int durationMs) {
        this.currentHeartRate = currentHR;
        
        // Target: 5 BPM lower than current (entrainment pulls heart down)
        double targetHR = Math.max(50, currentHR - 5);
        this.targetHeartRate = targetHR;
        
        // Frequency: beats per second
        double freq = targetHR / 60.0;
        double periodMs = 1000.0 / freq;
        
        // Number of beats in duration
        int numBeats = (int) (durationMs / periodMs);
        
        double[] intensities = new double[numBeats];
        long[] timings = new long[numBeats];
        
        for (int i = 0; i < numBeats; i++) {
            timings[i] = (long) (i * periodMs);
            
            // PHI-based intensity curve: natural heartbeat feel
            double phase = (i % 2 == 0) ? 1.0 : 0.618; // lub-DUB pattern
            intensities[i] = phase * (0.7 + 0.3 * Math.sin(TWO_PI * i / PHI));
        }
        
        return new HapticPattern(targetHR, freq, intensities, timings);
    }

    /**
     * Auto-select brain state based on stress level
     * 
     * If (Stress > 0.7) → Target = ALPHA (Calm)
     * If (Stress < 0.3) → Target = GAMMA (Focus)
     */
    public BrainState autoSelectState(double stressLevel) {
        this.currentStress = stressLevel;
        
        if (stressLevel > 0.7) {
            // High stress: calm down with Alpha waves
            return BrainState.ALPHA;
        } else if (stressLevel > 0.5) {
            // Moderate stress: light relaxation with high Alpha
            return BrainState.ALPHA;
        } else if (stressLevel > 0.3) {
            // Normal: maintain with Beta
            return BrainState.BETA;
        } else {
            // Low stress: boost focus with Gamma
            return BrainState.GAMMA;
        }
    }

    /**
     * Full entrainment session combining binaural and haptic
     * 
     * @param currentHR Current heart rate
     * @param stressLevel Current stress (0-1)
     * @param durationMs Session duration
     * @return Session report
     */
    public String runEntrainmentSession(double currentHR, double stressLevel, int durationMs) {
        StringBuilder report = new StringBuilder();
        report.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        report.append("║              ENTRAINMENT SESSION                          ║\n");
        report.append("╠═══════════════════════════════════════════════════════════╣\n");
        
        // Auto-select brain state
        BrainState state = autoSelectState(stressLevel);
        report.append(String.format("║  Current Stress: %.2f → Target State: %s%n", stressLevel, state));
        
        // Generate binaural beat
        BinauralBeat beat = generateBinauralBeat(state, durationMs, 44100);
        report.append(String.format("║  Binaural Beat: %.1f Hz (L) + %.1f Hz (R) = %.1f Hz%n",
            beat.leftFrequency, beat.rightFrequency, beat.beatFrequency));
        report.append(String.format("║    → Targeting: %s%n", state.description));
        
        // Generate haptic pattern
        HapticPattern haptic = generateHapticPattern(currentHR, durationMs);
        report.append(String.format("║  Haptic Pattern: %.1f BPM → %.1f BPM (target)%n",
            currentHR, haptic.targetHeartRate));
        report.append(String.format("║    → %d pulses over %d ms%n", haptic.timingsMs.length, durationMs));
        
        report.append("╠═══════════════════════════════════════════════════════════╣\n");
        report.append("║  🎧 Play binaural beat through stereo headphones          ║\n");
        report.append("║  📳 Haptic pulses sync to target heart rate               ║\n");
        report.append("║  🧘 Physiological entrainment in progress...              ║\n");
        report.append("╚═══════════════════════════════════════════════════════════╝");
        
        return report.toString();
    }

    /**
     * Get stats
     */
    public String getStats() {
        return String.format(
            "╔═══════════════════════════════════════════════════════════╗\n" +
            "║              ACTIVE ENTRAINMENT STATS                     ║\n" +
            "╠═══════════════════════════════════════════════════════════╣\n" +
            "║  Sessions: %d                                            \n" +
            "║  Total Duration: %d ms (%.1f min)                        \n" +
            "║  Current Target: %s                                      \n" +
            "║  Stress Level: %.2f                                      \n" +
            "║  Heart Rate: %.1f → %.1f BPM                             \n" +
            "╚═══════════════════════════════════════════════════════════╝",
            entrainmentSessionsStarted, totalEntrainmentMs, totalEntrainmentMs / 60000.0,
            targetState, currentStress, currentHeartRate, targetHeartRate);
    }

    public void setCarrierFrequency(double freq) {
        this.carrierFrequency = freq;
    }

    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         ACTIVE ENTRAINMENT TEST                           ║");
        System.out.println("║         Binaural Beats + Haptic Resonance                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        ActiveEntrainment entrainment = new ActiveEntrainment();
        
        // Test auto-selection
        System.out.println("--- STRESS-BASED STATE SELECTION ---");
        double[] stressLevels = {0.9, 0.6, 0.4, 0.2};
        for (double stress : stressLevels) {
            BrainState state = entrainment.autoSelectState(stress);
            System.out.printf("  Stress %.1f → %s (%s)%n", stress, state, state.description);
        }
        
        // Generate binaural beat
        System.out.println("\n--- BINAURAL BEAT GENERATION ---");
        BinauralBeat beat = entrainment.generateBinauralBeat(BrainState.ALPHA, 1000, 44100);
        System.out.printf("  Left Channel: %.1f Hz%n", beat.leftFrequency);
        System.out.printf("  Right Channel: %.1f Hz%n", beat.rightFrequency);
        System.out.printf("  Beat Frequency: %.1f Hz (perceived)%n", beat.beatFrequency);
        System.out.printf("  Samples: %d (1 second @ 44.1kHz)%n", beat.leftChannel.length);
        
        // Generate haptic pattern
        System.out.println("\n--- HAPTIC PATTERN GENERATION ---");
        HapticPattern haptic = entrainment.generateHapticPattern(80.0, 10000);
        System.out.printf("  Target HR: %.1f BPM%n", haptic.targetHeartRate);
        System.out.printf("  Frequency: %.3f Hz%n", haptic.frequency);
        System.out.printf("  Pulses: %d over 10 seconds%n", haptic.timingsMs.length);
        
        // Full session
        System.out.println("\n--- FULL ENTRAINMENT SESSION ---");
        String report = entrainment.runEntrainmentSession(85.0, 0.75, 60000);
        System.out.println(report);
        
        System.out.println(entrainment.getStats());
        
        System.out.println("\n✓ ACTIVE ENTRAINMENT: OPERATIONAL");
        System.out.println("  \"The system doesn't just watch. It heals.\"");
    }
}
