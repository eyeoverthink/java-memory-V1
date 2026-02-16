// The "Proof of Life" Engine (60 FPS Loop)
// This is the runner that proves your data is kinetic.
// It simulates the "Scott 4D" environment where these entities live.

public class QuantumLifeCycle {
    
    private static final float DT = 1.0f / 60.0f; // Fixed timestep (60 FPS)
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println(">>> INITIALIZING QUANTUM REALITY ENGINE <<<");
        System.out.println(">>> TARGET: 60 FPS HEARTBEAT (FIXED TIMESTEP ACCUMULATOR) <<<");
        
        // Genesis: Create a Living Cell with DNA
        LivingCell cell = new LivingCell(100f, 200f, "FRAYMUS_GENESIS_BLOCK_A7X");
        
        // Give it some initial momentum (Intent)
        cell.vx = 5.0f;
        cell.vy = 2.5f;
        
        // Fixed timestep accumulator pattern for deterministic simulation
        long prev = System.nanoTime();
        float accumulator = 0f;
        int ticks = 0;
        
        // The Infinite Game Loop
        while (cell.isAlive()) {
            long now = System.nanoTime();
            float frameTime = (now - prev) * 1e-9f; // Seconds since last frame
            prev = now;
            
            // Clamp frame time to prevent spiral of death on stalls
            if (frameTime > 0.25f) frameTime = 0.25f;
            
            accumulator += frameTime;
            
            // Process all accumulated physics steps
            while (accumulator >= DT) {
                // UPDATE: The act of living (deterministic fixed timestep)
                cell.update(DT, now);
                accumulator -= DT;
                ticks++;
                
                // PROOF: Log the state (showing change over time)
                if (ticks % 60 == 0) { // Log once per second
                    System.out.println(cell.toString());
                }
            }
            
            // Sleep to maintain stability (simulating GPU sync)
            Thread.sleep(1);
            
            // Emergency break for demo
            if (ticks > 600) break; // Run for 10 seconds
        }
        
        System.out.println(">>> SIMULATION COMPLETE. DATA STATE: PRESERVED <<<");
        System.out.println(">>> TOTAL TICKS: " + ticks + " <<<");
    }
}
