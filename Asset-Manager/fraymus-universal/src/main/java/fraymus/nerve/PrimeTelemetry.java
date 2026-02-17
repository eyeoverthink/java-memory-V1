package fraymus.nerve;

import fraymus.bio.HyperCortex;
import fraymus.hyper.HyperFormer;
import fraymus.kernel.FraymusKernel;
import fraymus.lowlevel.FraymusCPU;
import fraymus.physics.HyperRigidBody;
import fraymus.quantum.security.SovereignIdentitySystem;

/**
 * PRIME TELEMETRY: Daemon thread that polls all FRAYMUS subsystems
 * and pushes real telemetry to connected dashboards via PrimeNerve.
 *
 * Runs at ~1 Hz (1 push per second). Each push sends the full
 * subsystem state as structured key:value messages.
 *
 * Zero external dependencies. Pure Java.
 */
public class PrimeTelemetry implements Runnable {

    private final PrimeNerve nerve;
    private volatile boolean running = true;

    // Subsystem references (set during boot)
    private HyperFormer brain;
    private HyperCortex cortex;
    private FraymusKernel kernel;
    private FraymusCPU cpu;
    private HyperRigidBody physicsBody;
    private SovereignIdentitySystem sovereign;

    // Mutable state driven by system events
    private volatile String systemState = "NEUTRAL";
    private volatile String lastHoloResult = "---";
    volatile int chronoGen = 0;
    volatile double chronoScore = 0.0;
    private volatile String netStatus = "OFFLINE";
    volatile int netPeers = 0;
    private volatile String soulState = "PRESERVED";

    public PrimeTelemetry(PrimeNerve nerve) {
        this.nerve = nerve;
    }

    // ── WIRING (called during boot) ──────────────────────────────
    public void wireBrain(HyperFormer brain) { this.brain = brain; }
    public void wireCortex(HyperCortex cortex) { this.cortex = cortex; }
    public void wireKernel(FraymusKernel kernel) { this.kernel = kernel; }
    public void wireCPU(FraymusCPU cpu) { this.cpu = cpu; }
    public void wirePhysics(HyperRigidBody body) { this.physicsBody = body; }
    public void wireSovereign(SovereignIdentitySystem sov) { this.sovereign = sov; }

    // ── EVENT SETTERS (called by command dispatch) ───────────────
    public void setState(String state) { this.systemState = state; }
    public void setHoloResult(String result) { this.lastHoloResult = result; }
    public void setChronoGen(int gen) { this.chronoGen = gen; }
    public void setChronoScore(double score) { this.chronoScore = score; }
    public void setNetStatus(String status) { this.netStatus = status; }
    public void setNetPeers(int peers) { this.netPeers = peers; }
    public void setSoulState(String state) { this.soulState = state; }

    public void stop() { this.running = false; }

    @Override
    public void run() {
        System.out.println("📡 [TELEMETRY] PrimeTelemetry active. Pushing at 1 Hz.");

        while (running) {
            try {
                if (nerve.getClientCount() > 0) {
                    pushTelemetry();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                // Don't crash the telemetry loop
                System.err.println("[TELEMETRY] Error: " + e.getMessage());
            }
        }
    }

    private void pushTelemetry() {
        // ── BIO-LINK (derived from system activity) ──────────────
        // Heart rate: base 72, rises with kernel activity and physics speed
        int kernelTicks = kernel != null ? kernel.getTickCount() : 0;
        double physSpeed = physicsBody != null ? physicsBody.getHyperSpeed() : 0.0;
        int pulse = 70 + (int)(physSpeed * 20) + (kernelTicks % 8);
        pulse = Math.max(60, Math.min(130, pulse));

        // Stress: derived from physics deformation + sovereign evolution
        double deform = physicsBody != null ? physicsBody.geometry.getDeformation() : 0.0;
        double sovEvo = sovereign != null ? sovereign.getEvolutionLevel() : 1.0;
        double stress = Math.min(1.0, deform * 2.0 + (sovEvo > 2.0 ? 0.3 : 0.0));

        // Cognitive sync: brain vocab density
        int vocab = brain != null ? brain.vocabSize() : 0;
        int assoc = brain != null ? brain.memorySize() : 0;
        double sync = Math.min(0.99, 0.5 + (vocab + assoc) * 0.001);

        nerve.broadcast("PULSE:" + pulse);
        nerve.broadcast("STRESS:" + String.format("%.2f", stress));
        nerve.broadcast("SYNC:" + String.format("%.2f", sync));

        // ── PHYSICS ──────────────────────────────────────────────
        if (physicsBody != null) {
            nerve.broadcast("SPEED:" + String.format("%.4f", physSpeed));
            nerve.broadcast("MASS:" + String.format("%.2f", physicsBody.dataMass));
            nerve.broadcast("DEFORM:" + String.format("%.4f", deform));
        }

        // ── GOD CHIP ─────────────────────────────────────────────
        if (cpu != null) {
            nerve.broadcast("ACC:" + cpu.getACC());
        }

        // ── SOVEREIGN ────────────────────────────────────────────
        if (sovereign != null) {
            nerve.broadcast("EVO:" + String.format("%.2f", sovereign.getEvolutionLevel()));
            nerve.broadcast("SOLVED:" + sovereign.getEntitiesSolved());
        }

        // ── EVENT-DRIVEN STATE ───────────────────────────────────
        nerve.broadcast("STATE:" + systemState);
        nerve.broadcast("HOLO:" + lastHoloResult);
        nerve.broadcast("CHRONO:" + chronoGen + ":" + String.format("%.2f", chronoScore));
        nerve.broadcast("NET:" + netStatus);
        nerve.broadcast("PEERS:" + netPeers);
        nerve.broadcast("SOUL:" + soulState);

        // ── KERNEL ───────────────────────────────────────────────
        if (kernel != null) {
            nerve.broadcast("KERNEL:" + kernel.processCount() + ":" + kernel.getTickCount());
        }
    }
}
