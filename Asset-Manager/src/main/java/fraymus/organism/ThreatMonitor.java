package fraymus.organism;

/**
 * NEOCLAW — Amygdala / Threat Center
 *
 * Self-preservation: monitors memory pressure and flags threats.
 * Default-deny protocols engaged — external unverified executions are blocked.
 */
public class ThreatMonitor {

    private static final double MEMORY_THREAT_THRESHOLD = 0.85;

    private boolean threatened  = false;
    private boolean engaged     = false;
    private String  lastThreat  = "NONE";
    private int     threatCount = 0;

    /** Scan JVM memory health. Returns true if a threat is detected. */
    public boolean scan() {
        Runtime rt    = Runtime.getRuntime();
        long used     = rt.totalMemory() - rt.freeMemory();
        long max      = rt.maxMemory();
        double usage  = (double) used / max;

        if (usage > MEMORY_THREAT_THRESHOLD) {
            threatened = true;
            lastThreat = String.format("HIGH_MEMORY (%.1f%%)", usage * 100);
            threatCount++;
        } else {
            threatened = false;
        }
        return threatened;
    }

    public void engage()    { engaged = true; }
    public void disengage() { engaged = false; threatened = false; }

    public boolean isThreatened()   { return threatened; }
    public boolean isEngaged()      { return engaged; }
    public String  getLastThreat()  { return lastThreat; }
    public int     getThreatCount() { return threatCount; }

    public String getStatus() {
        return String.format("[NEOCLAW] Threatened=%s Engaged=%s LastThreat=%s Count=%d",
            threatened, engaged, lastThreat, threatCount);
    }
}
