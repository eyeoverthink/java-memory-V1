package gemini.root.physics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * HIVE GRAVITY ENGINE: The physics simulation for the Fraynix-Claw system.
 * Particles attract each other via gravity. Collisions trigger fusion events.
 * 
 * When TASK particles gain enough velocity toward the CLAW particle,
 * they collide and the task executes automatically.
 * 
 * This is TELEPATHY: Gravity causes Code Execution.
 */
public class HiveGravityEngine implements Runnable {

    private static final double PHI = 1.618033988749895;
    private static final double G = 0.5;  // Gravitational constant
    private static final double COLLISION_RADIUS = 5.0;  // Fusion distance
    
    private final List<PhiSuit<?>> particles = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;
    private long tickCount = 0;
    private int tickDelayMs = 50;

    public HiveGravityEngine() {
    }

    public HiveGravityEngine(int tickDelayMs) {
        this.tickDelayMs = tickDelayMs;
    }

    /**
     * Register a particle in the universe
     */
    public void register(PhiSuit<?> particle) {
        particles.add(particle);
        System.out.println(">>> REGISTERED: " + particle);
    }

    /**
     * Inject a task into the universe
     */
    public PhiSuit<String> injectTask(String intent, double priority, double x, double y, double z) {
        PhiSuit<String> task = new PhiSuit<>(intent, x, y, z);
        task.label = "TASK_" + System.currentTimeMillis();
        task.amplitude = priority;  // Higher priority = more mass = faster attraction
        task.heat = priority * 0.5;  // Initial energy
        register(task);
        return task;
    }

    /**
     * Single physics tick
     */
    public void tick() {
        tickCount++;
        
        // Apply gravity between all particles
        for (PhiSuit<?> p1 : particles) {
            if (!p1.active) continue;
            
            for (PhiSuit<?> p2 : particles) {
                if (p1 == p2 || !p2.active) continue;
                
                // Lighter particles attracted to heavier ones
                if (p1.amplitude < p2.amplitude) {
                    p1.attractTo(p2, G);
                }
                
                // Check for collision
                if (p1.isColliding(p2, COLLISION_RADIUS)) {
                    // Heavier particle absorbs the collision event
                    if (p2.amplitude > p1.amplitude) {
                        p2.onCollision(p1);
                    } else {
                        p1.onCollision(p2);
                    }
                }
            }
        }
        
        // Update all particle positions
        for (PhiSuit<?> p : particles) {
            if (p.active) {
                p.tick();
            }
        }
        
        // Clean up dead particles periodically
        if (tickCount % 100 == 0) {
            particles.removeIf(p -> !p.active);
        }
    }

    /**
     * Run the physics loop
     */
    @Override
    public void run() {
        running = true;
        System.out.println(">>> HIVE GRAVITY ENGINE: ONLINE");
        
        while (running) {
            tick();
            
            // Debug output every 20 ticks
            if (tickCount % 20 == 0) {
                System.out.println("[TICK " + tickCount + "] Particles: " + particles.size());
                for (PhiSuit<?> p : particles) {
                    if (p.active) System.out.println("   " + p);
                }
            }
            
            try {
                Thread.sleep(tickDelayMs);
            } catch (InterruptedException e) {
                running = false;
            }
        }
        
        System.out.println(">>> HIVE GRAVITY ENGINE: OFFLINE");
    }

    /**
     * Start the engine in a background thread
     */
    public Thread start() {
        Thread t = new Thread(this, "HiveGravityEngine");
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Stop the engine
     */
    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public long getTickCount() {
        return tickCount;
    }

    public int getParticleCount() {
        return particles.size();
    }

    public List<PhiSuit<?>> getParticles() {
        return particles;
    }
}
