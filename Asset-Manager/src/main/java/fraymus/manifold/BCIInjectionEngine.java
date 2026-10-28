package fraymus.manifold;

/**
 * NEURAL-SILICON HYBRID / BCI OVERRIDE (Section XXVIII — Circulatronics)
 *
 * When a biological lobe outputs a faulty or low-confidence signal, the BCI
 * detects the electrical anomaly and injects a corrective, precisely timed
 * mathematical pulse using a Proportional-Derivative (PD) controller.
 *
 *   Injection(t) = Kp * (y_target − y(t)) + Kd * d/dt(y_target − y(t))
 *
 * The total system state is a fusion of wetware and software:
 *   State(t) = y(t) + Threshold( Injection(t) )
 *
 * Where Threshold(x) = clip(x, −maxInjection, +maxInjection) prevents
 * over-correction and signal saturation.
 *
 * This implements "Circulatronics": the brain-computer interface that
 * routes around failed or degraded biological lobes by substituting
 * precisely calibrated silicon correction pulses in real-time.
 *
 * Kp = proportional gain  (how strongly to correct steady-state error)
 * Kd = derivative gain    (how strongly to damp rate of change in error)
 */
public class BCIInjectionEngine {

    public static final double DEFAULT_KP             = 1.50;
    public static final double DEFAULT_KD             = 0.30;
    public static final double DEFAULT_MAX_INJECTION  = 0.80;
    public static final double DEFAULT_ANOMALY_THRESH = 0.60;

    private double kp;
    private double kd;
    private double maxInjection;
    private double anomalyThreshold;

    private double[] lastError;        // previous error — needed for derivative term
    private long     injectionCount;
    private long     anomalyCount;
    private double   totalCorrectionMag;
    private double   lastInjectionMag;

    public BCIInjectionEngine(double kp, double kd, double maxInjection) {
        this.kp              = kp;
        this.kd              = kd;
        this.maxInjection    = maxInjection;
        this.anomalyThreshold = DEFAULT_ANOMALY_THRESH;
        this.lastError       = null;
    }

    public BCIInjectionEngine() {
        this(DEFAULT_KP, DEFAULT_KD, DEFAULT_MAX_INJECTION);
    }

    // ─── ANOMALY DETECTION ───────────────────────────────────────────────────

    /**
     * Detect if a biological lobe output y(t) is anomalous relative to target.
     * An anomaly is declared when RMS(y_target − y(t)) > anomalyThreshold.
     */
    public boolean detectAnomaly(double[] biologicalOutput, double[] target) {
        int len = Math.min(biologicalOutput.length, target.length);
        double mse = 0;
        for (int i = 0; i < len; i++) {
            double e = target[i] - biologicalOutput[i];
            mse += e * e;
        }
        double rms = Math.sqrt(mse / len);
        if (rms > anomalyThreshold) { anomalyCount++; return true; }
        return false;
    }

    // ─── PD CONTROLLER ───────────────────────────────────────────────────────

    /**
     * Compute PD corrective injection pulse:
     *   Injection(t) = Kp*(y_target − y(t)) + Kd * d(y_target − y(t))/dt
     *
     * The derivative term estimates the rate of error change from the previous
     * call (discrete-time approximation: dError/dt ≈ Δerror / Δt, dt=1 step).
     *
     * @param biologicalOutput  y(t)  — raw lobe signal
     * @param target            y_target — desired output (survival/optimal)
     * @return raw injection vector (before threshold clamping)
     */
    public double[] computeInjection(double[] biologicalOutput, double[] target) {
        int len = Math.min(biologicalOutput.length, target.length);
        double[] error     = new double[len];
        double[] injection = new double[len];

        for (int i = 0; i < len; i++) error[i] = target[i] - biologicalOutput[i];

        double[] dError = new double[len];
        if (lastError != null && lastError.length == len) {
            for (int i = 0; i < len; i++) dError[i] = error[i] - lastError[i];
        }

        for (int i = 0; i < len; i++) {
            injection[i] = kp * error[i] + kd * dError[i];
        }

        lastError = error.clone();
        injectionCount++;
        return injection;
    }

    // ─── THRESHOLD FUNCTION ──────────────────────────────────────────────────

    /**
     * Threshold(x) = clip(x, −maxInjection, +maxInjection)
     * Prevents biological over-correction and signal saturation.
     * Also updates internal injection magnitude tracking.
     */
    public double[] threshold(double[] injection) {
        double[] t   = new double[injection.length];
        double   mag = 0;
        for (int i = 0; i < injection.length; i++) {
            t[i] = Math.max(-maxInjection, Math.min(maxInjection, injection[i]));
            mag += t[i] * t[i];
        }
        lastInjectionMag    = Math.sqrt(mag / injection.length);
        totalCorrectionMag += lastInjectionMag;
        return t;
    }

    // ─── STATE FUSION ────────────────────────────────────────────────────────

    /**
     * State(t) = y(t) + Threshold( Injection(t) )
     *
     * Fuses the wetware biological output with the silicon corrective pulse.
     * The result is neither purely biological nor purely digital — it is the
     * neural-silicon hybrid state that characterises Circulatronics.
     *
     * @param biologicalOutput  y(t)
     * @param target            y_target
     * @return fused hybrid state
     */
    public double[] fuse(double[] biologicalOutput, double[] target) {
        double[] injection  = computeInjection(biologicalOutput, target);
        double[] tInjection = threshold(injection);
        int      len        = Math.min(biologicalOutput.length, tInjection.length);
        double[] fused      = biologicalOutput.clone();
        for (int i = 0; i < len; i++) fused[i] += tInjection[i];
        return fused;
    }

    /**
     * Smart fuse: inject only when anomaly is detected.
     * Returns biologicalOutput unchanged when the lobe signal is healthy.
     */
    public double[] smartFuse(double[] biologicalOutput, double[] target) {
        if (detectAnomaly(biologicalOutput, target)) return fuse(biologicalOutput, target);
        return biologicalOutput;
    }

    /**
     * Full lobe override: read lobe state from manifold, apply smart fusion,
     * write corrected signal back into the manifold.
     *
     * @param arm    the AnatomicalRoutingManifold providing the lobe state
     * @param lobe   the lobe to inspect and potentially correct
     * @param target desired lobe output vector
     * @return fused lobe vector (may be unchanged if no anomaly detected)
     */
    public double[] correctLobe(AnatomicalRoutingManifold arm,
                                AnatomicalRoutingManifold.Lobe lobe,
                                double[] target) {
        double[] lobeSignal = arm.getLobeState(lobe);
        double[] fused      = smartFuse(lobeSignal, target);
        arm.injectLobeSignal(lobe, fused);
        return fused;
    }

    // ─── CONFIGURATION ───────────────────────────────────────────────────────

    public void setKp(double kp)                   { this.kp = kp; }
    public void setKd(double kd)                   { this.kd = kd; }
    public void setMaxInjection(double max)        { this.maxInjection = max; }
    public void setAnomalyThreshold(double thresh) { this.anomalyThreshold = thresh; }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public double getKp()                { return kp; }
    public double getKd()                { return kd; }
    public double getMaxInjection()      { return maxInjection; }
    public double getAnomalyThreshold()  { return anomalyThreshold; }
    public double getLastInjectionMag()  { return lastInjectionMag; }
    public long   getInjectionCount()    { return injectionCount; }
    public long   getAnomalyCount()      { return anomalyCount; }
    public double getTotalCorrectionMag(){ return totalCorrectionMag; }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format(
            "BCIInjectionEngine | Kp=%.2f Kd=%.2f maxInj=%.2f anomThresh=%.2f | " +
            "injections=%d anomalies=%d totalCorr=%.4f lastMag=%.4f",
            kp, kd, maxInjection, anomalyThreshold,
            injectionCount, anomalyCount, totalCorrectionMag, lastInjectionMag);
    }
}
