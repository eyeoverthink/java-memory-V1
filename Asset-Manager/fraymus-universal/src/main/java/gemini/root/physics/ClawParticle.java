package gemini.root.physics;

import gemini.root.limbs.ClawConnector;

/**
 * CLAW PARTICLE: OpenClaw as a high-mass particle in the physics engine.
 * Tasks orbit toward it. On collision = EXECUTION.
 * 
 * This is the Singularity point where Gravity causes Code Execution.
 */
public class ClawParticle extends PhiSuit<String> {

    private final ClawConnector nerve;
    private volatile boolean isBusy = false;
    private int tasksExecuted = 0;

    public ClawParticle(double x, double y, double z) {
        super(x, y, z, "OPEN_CLAW_AGENT");
        this.nerve = new ClawConnector();
        this.amplitude = 100.0;  // Massive gravity well - pulls all tasks
        this.heat = 0.0;         // Cool execution state
        this.data = "EXECUTOR";
    }

    public ClawParticle(String agentUrl, double x, double y, double z) {
        super(x, y, z, "OPEN_CLAW_AGENT");
        this.nerve = new ClawConnector(agentUrl);
        this.amplitude = 100.0;
        this.heat = 0.0;
        this.data = "EXECUTOR";
    }

    /**
     * FUSION EVENT: When a Task particle collides with the Claw, EXECUTE IT.
     */
    @Override
    public void onCollision(PhiSuit<?> other) {
        // Only react to TASK particles when not busy
        if (other.label.startsWith("TASK_") && !isBusy && other.active) {
            System.out.println("⚡ KINETIC CAPTURE: Claw caught " + other.label);
            
            // Mark task as consumed
            other.active = false;
            
            // Claw becomes busy
            this.isBusy = true;
            this.heat = 100.0;  // Spike heat during work
            
            // Extract task intent
            String intent = other.data != null ? other.data.toString() : other.label;
            String context = String.format("Priority: %.0f | Heat: %.1f | Origin: (%.0f,%.0f,%.0f)", 
                other.amplitude, other.heat, other.x, other.y, other.z);
            
            // Execute in background thread (don't block physics loop)
            new Thread(() -> {
                try {
                    String result = nerve.dispatch(intent, context);
                    System.out.println("   > EXECUTION RESULT: " + result);
                    tasksExecuted++;
                } catch (Exception e) {
                    System.err.println("   > EXECUTION FAILED: " + e.getMessage());
                } finally {
                    this.isBusy = false;
                    this.heat = 10.0;  // Cool down
                }
            }, "ClawExecutor-" + tasksExecuted).start();
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    public int getTasksExecuted() {
        return tasksExecuted;
    }

    public boolean isClawOnline() {
        return nerve.ping();
    }

    @Override
    public String toString() {
        return String.format("[CLAW @ (%.0f,%.0f,%.0f) busy=%s tasks=%d heat=%.1f]",
            x, y, z, isBusy, tasksExecuted, heat);
    }
}
