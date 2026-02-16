package fraynix.brain;

import fraynix.core.*;
import fraynix.observe.EventLogger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * HYPERTESSERACT: The 4D Brain - Policy Engine V1
 * 
 * Deterministic, explainable, swappable.
 * Not mysticism - a decision engine backed by spatial indexing.
 * 
 * Structure: [W][X][Y][Z] = 8×8×8×8 = 4,096 nodes
 */
public class HyperTesseract implements KernelService {

    private static final int DIM = 8;
    private final Node[][][][] nodes;
    private final BrainPolicy policy;
    private final Random rng;
    private final long seed;
    
    private final AtomicLong thoughtsProcessed = new AtomicLong();
    private final AtomicLong tickCount = new AtomicLong();
    private final ScheduledExecutorService ticker;
    
    private volatile boolean running = false;
    private long startTime;
    private EventLogger logger;

    public HyperTesseract() {
        this(System.currentTimeMillis());
    }

    public HyperTesseract(long seed) {
        this.seed = seed;
        this.rng = new Random(seed);
        this.nodes = new Node[DIM][DIM][DIM][DIM];
        this.policy = new BrainPolicy("HyperTesseract-V1");
        
        // Initialize nodes
        for (int w = 0; w < DIM; w++) {
            for (int x = 0; x < DIM; x++) {
                for (int y = 0; y < DIM; y++) {
                    for (int z = 0; z < DIM; z++) {
                        nodes[w][x][y][z] = new Node(w, x, y, z);
                    }
                }
            }
        }
        
        // Create connections (local connectivity)
        initializeConnections();
        
        this.ticker = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Brain-Ticker");
            t.setDaemon(true);
            return t;
        });
        
        System.out.println("🧠 HyperTesseract initialized (seed=" + seed + ", nodes=" + getTotalNodes() + ")");
    }

    private void initializeConnections() {
        // Connect each node to its neighbors (6-connectivity in 4D)
        int[][] offsets = {{1,0,0,0}, {-1,0,0,0}, {0,1,0,0}, {0,-1,0,0}, 
                          {0,0,1,0}, {0,0,-1,0}, {0,0,0,1}, {0,0,0,-1}};
        
        for (int w = 0; w < DIM; w++) {
            for (int x = 0; x < DIM; x++) {
                for (int y = 0; y < DIM; y++) {
                    for (int z = 0; z < DIM; z++) {
                        Node node = nodes[w][x][y][z];
                        for (int[] off : offsets) {
                            int nw = w + off[0], nx = x + off[1];
                            int ny = y + off[2], nz = z + off[3];
                            if (inBounds(nw, nx, ny, nz)) {
                                double weight = 0.1 + rng.nextDouble() * 0.2;
                                node.connect(nodes[nw][nx][ny][nz], weight);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean inBounds(int w, int x, int y, int z) {
        return w >= 0 && w < DIM && x >= 0 && x < DIM && 
               y >= 0 && y < DIM && z >= 0 && z < DIM;
    }

    public void setLogger(EventLogger logger) {
        this.logger = logger;
    }

    public void injectThought(String thought, double intensity) {
        // Hash thought to coordinates
        int hash = thought.hashCode();
        int w = Math.abs(hash) % DIM;
        int x = Math.abs(hash >> 8) % DIM;
        int y = Math.abs(hash >> 16) % DIM;
        int z = Math.abs(hash >> 24) % DIM;
        
        // Stimulate region
        stimulateRegion(w, x, y, z, intensity, 2);
        
        // Store in node memory
        nodes[w][x][y][z].remember("thought", thought);
        nodes[w][x][y][z].remember("timestamp", System.currentTimeMillis());
        
        thoughtsProcessed.incrementAndGet();
        
        if (logger != null) {
            logger.logEvent("thought_injected", Map.of(
                "thought", thought.length() > 50 ? thought.substring(0, 50) + "..." : thought,
                "coords", String.format("[%d,%d,%d,%d]", w, x, y, z),
                "intensity", intensity
            ));
        }
    }

    public void stimulateRegion(int cw, int cx, int cy, int cz, double intensity, int radius) {
        for (int w = Math.max(0, cw - radius); w <= Math.min(DIM - 1, cw + radius); w++) {
            for (int x = Math.max(0, cx - radius); x <= Math.min(DIM - 1, cx + radius); x++) {
                for (int y = Math.max(0, cy - radius); y <= Math.min(DIM - 1, cy + radius); y++) {
                    for (int z = Math.max(0, cz - radius); z <= Math.min(DIM - 1, cz + radius); z++) {
                        double dist = Math.sqrt(
                            Math.pow(w - cw, 2) + Math.pow(x - cx, 2) + 
                            Math.pow(y - cy, 2) + Math.pow(z - cz, 2)
                        );
                        if (dist <= radius) {
                            double falloff = 1.0 - (dist / (radius + 1));
                            nodes[w][x][y][z].stimulate(intensity * falloff);
                        }
                    }
                }
            }
        }
    }

    public void tick() {
        tickCount.incrementAndGet();
        
        // Update all nodes
        for (int w = 0; w < DIM; w++) {
            for (int x = 0; x < DIM; x++) {
                for (int y = 0; y < DIM; y++) {
                    for (int z = 0; z < DIM; z++) {
                        nodes[w][x][y][z].tick();
                    }
                }
            }
        }
    }

    public Node getNode(int w, int x, int y, int z) {
        if (!inBounds(w, x, y, z)) return null;
        return nodes[w][x][y][z];
    }

    public Policy.Decision<String> decide(Intent intent) {
        BrainState state = captureState();
        Policy.Decision<String> decision = policy.decide(state, intent);
        
        if (logger != null) {
            logger.logDecisionMade(
                intent.getId(),
                policy.getPolicyName(),
                decision.getBestChoice() != null ? decision.getBestChoice() : "NONE",
                decision.confidence(),
                decision.reason()
            );
        }
        
        return decision;
    }

    public BrainState captureState() {
        int activeCount = 0;
        double totalActivation = 0;
        List<BrainState.HotSpot> hotSpots = new ArrayList<>();
        
        for (int w = 0; w < DIM; w++) {
            for (int x = 0; x < DIM; x++) {
                for (int y = 0; y < DIM; y++) {
                    for (int z = 0; z < DIM; z++) {
                        Node node = nodes[w][x][y][z];
                        totalActivation += node.getActivation();
                        if (node.isActive()) {
                            activeCount++;
                            if (node.getActivation() > 0.8) {
                                Object thought = node.recall("thought");
                                hotSpots.add(new BrainState.HotSpot(
                                    new int[]{w, x, y, z},
                                    node.getActivation(),
                                    thought != null ? thought.toString() : null
                                ));
                            }
                        }
                    }
                }
            }
        }
        
        // Sort hotspots by activation
        hotSpots.sort((a, b) -> Double.compare(b.activation(), a.activation()));
        if (hotSpots.size() > 10) {
            hotSpots = hotSpots.subList(0, 10);
        }
        
        Runtime rt = Runtime.getRuntime();
        double memUsage = 1.0 - ((double) rt.freeMemory() / rt.maxMemory());
        
        return BrainState.builder()
            .activeNodeCount(activeCount)
            .averageActivation(totalActivation / getTotalNodes())
            .hotSpots(hotSpots)
            .thoughtsProcessed(thoughtsProcessed.get())
            .cpuUsage(0.0) // Would need OS-level access
            .memoryUsage(memUsage)
            .build();
    }

    public int getTotalNodes() {
        return DIM * DIM * DIM * DIM;
    }

    public long getSeed() {
        return seed;
    }

    public long getTickCount() {
        return tickCount.get();
    }

    public long getThoughtsProcessed() {
        return thoughtsProcessed.get();
    }

    // KernelService implementation
    @Override
    public String getName() { return "HyperTesseract"; }
    
    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        
        // Start tick loop (10 Hz)
        ticker.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
        
        System.out.println("🧠 HyperTesseract started (10 Hz tick)");
    }

    @Override
    public void stop() {
        running = false;
        ticker.shutdown();
        try {
            ticker.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("🧠 HyperTesseract stopped");
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    @Override
    public ServiceStatus getStatus() {
        return running ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
    }

    @Override
    public HealthReport getHealth() {
        long uptime = running ? System.currentTimeMillis() - startTime : 0;
        BrainState state = captureState();
        
        Map<String, Object> details = Map.of(
            "activeNodes", state.getActiveNodeCount(),
            "avgActivation", state.getAverageActivation(),
            "tickCount", tickCount.get(),
            "thoughtsProcessed", thoughtsProcessed.get()
        );
        
        if (!running) {
            return HealthReport.unhealthy(ServiceStatus.STOPPED, "Not running", 0);
        }
        
        return new HealthReport(ServiceStatus.RUNNING, true, "OK", uptime, details);
    }

    @Override
    public ServiceMetrics getMetrics() {
        return new ServiceMetrics(
            thoughtsProcessed.get(),
            thoughtsProcessed.get(),
            0,
            0,
            0,
            0.0,
            0,
            Map.of(
                "tickCount", tickCount.get(),
                "activeNodes", (long) captureState().getActiveNodeCount()
            )
        );
    }
}
