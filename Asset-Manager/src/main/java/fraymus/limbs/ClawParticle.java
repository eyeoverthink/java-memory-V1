package fraymus.limbs;

import fraymus.core.PhiSuit;

/**
 * CLAW PARTICLE - The Physical Representation
 * 
 * In the GravityEngine, OpenClaw isn't software - it's a High-Mass Particle.
 * It has immense gravity, pulling "Task" particles toward it.
 * When they collide, the task executes.
 */
public class ClawParticle extends PhiSuit<String> {

    private final ClawConnector nerve;
    private boolean isBusy = false;

    public ClawParticle(int x, int y, int z) {
        super("OPEN_CLAW_AGENT", x, y, z);
        this.nerve = new ClawConnector();
        this.a = 100; // Massive gravity well (amplitude)
    }

    /**
     * The Fusion Logic: When a Task hits the Claw, EXECUTE IT.
     * Called by GravityEngine when particles are close enough.
     */
    public void executeTask(PhiSuit<?> other) {
        // Only react to High-Energy Tasks
        if (other.getLabel().startsWith("TASK_") && !isBusy) {
            System.out.println("⚡ KINETIC CAPTURE: Claw caught " + other.getLabel());
            this.isBusy = true;
            this.a = 100; // Spike amplitude during work
            
            // Execute in background (don't block physics loop)
            new Thread(() -> {
                String result = nerve.dispatch(other.get().toString(), "Priority: Critical");
                System.out.println("   > RESULT: " + result);
                this.isBusy = false;
                this.a = 10; // Cool down
            }).start();
        }
    }
}
