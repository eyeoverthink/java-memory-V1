package gemini.root;

/**
 * 🌌 GRAVITY ENGINE - Hebbian Physics
 * "The force that organizes chaos into constellations."
 */
public class GravityEngine implements Runnable {
    
    private static final double PHI = 1.6180339887;
    
    private volatile boolean running = false;
    private int tickRate = 100;
    private long tickCount = 0;
    private int gravitationalPulls = 0;
    private int fusionEvents = 0;
    
    private static GravityEngine instance;
    private Thread engineThread;
    
    private GravityEngine() {}
    
    public static GravityEngine getInstance() {
        if (instance == null) {
            instance = new GravityEngine();
        }
        return instance;
    }
    
    public void start() {
        if (running) return;
        running = true;
        engineThread = new Thread(this, "GravityEngine");
        engineThread.setDaemon(true);
        engineThread.start();
    }
    
    public void stop() {
        running = false;
        if (engineThread != null) {
            engineThread.interrupt();
        }
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                tickCount++;
                Thread.sleep(tickRate);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    public String getStatus() {
        return String.format(
            "🌌 GRAVITY ENGINE: %s | Ticks: %d | φ: %.6f",
            running ? "ONLINE" : "OFFLINE",
            tickCount,
            PHI
        );
    }
    
    public boolean isRunning() { return running; }
    public long getTickCount() { return tickCount; }
}
