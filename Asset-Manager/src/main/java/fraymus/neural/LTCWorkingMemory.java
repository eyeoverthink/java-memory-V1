package fraymus.neural;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LTC-Enhanced WorkingMemory - Fluid Memory Horizons via Liquid Time-Constant Dynamics
 * 
 * Replaces static decay with fluid memory horizons that adapt to input frequency.
 * 
 * Phase 1.3: Fluid Memory Horizon Verification
 * 
 * Key innovations:
 * - Input frequency tracking (Delta, Theta, Alpha, Beta, Gamma bands)
 * - Adaptive decay rate based on LTC time constants
 * - High-frequency input → short memory horizon (fast processing)
 * - Low-frequency input → long memory horizon (deep memory)
 * 
 * Biological inspiration:
 * - Gamma band (30-100 Hz): Rapid cognitive processing, short memory
 * - Delta band (0.5-4.0 Hz): Deep homeostatic states, long-term memory
 * 
 * Mathematical foundation:
 * - τ(t) = f(input_frequency, importance)
 * - decay_rate(t) = 1 / τ(t)
 * - Memory horizon = τ(t) * capacity
 */
public class LTCWorkingMemory {

    // Default capacity
    public static final int DEFAULT_CAPACITY = 100;
    
    // Base decay rate (will be adapted dynamically)
    public static final double BASE_DECAY_RATE = 0.05;
    
    // Frequency bands (Hz)
    public static final float DELTA_MIN = 0.5f;
    public static final float DELTA_MAX = 4.0f;
    public static final float THETA_MIN = 4.0f;
    public static final float THETA_MAX = 8.0f;
    public static final float ALPHA_MIN = 8.0f;
    public static final float ALPHA_MAX = 12.0f;
    public static final float BETA_MIN = 13.0f;
    public static final float BETA_MAX = 30.0f;
    public static final float GAMMA_MIN = 30.0f;
    public static final float GAMMA_MAX = 100.0f;
    
    // Memory item with importance and frequency tracking
    public static class MemoryItem {
        public final String content;
        public volatile double importance;
        public volatile long lastAccessed;
        public final long createdAt;
        public float inputFrequency;  // Frequency at time of storage
        public float timeConstant;    // Current time constant τ
        
        public MemoryItem(String content, double importance, float inputFrequency) {
            this.content = content;
            this.importance = importance;
            this.lastAccessed = System.currentTimeMillis();
            this.createdAt = System.currentTimeMillis();
            this.inputFrequency = inputFrequency;
            this.timeConstant = computeTimeConstant(inputFrequency);
        }
        
        public void access() {
            this.lastAccessed = System.currentTimeMillis();
        }
        
        /**
         * Compute time constant based on input frequency
         * High frequency → short τ (fast decay, short memory)
         * Low frequency → long τ (slow decay, long memory)
         */
        private float computeTimeConstant(float frequency) {
            // Normalize frequency to [0, 1] range
            float normalizedFreq = Math.min(1.0f, frequency / GAMMA_MAX);
            
            // Inverse mapping: high freq → short τ
            float tauMin = 0.1f;  // 100ms (fast)
            float tauMax = 10.0f; // 10s (slow)
            
            return tauMin + (1.0f - normalizedFreq) * (tauMax - tauMin);
        }
        
        /**
         * Adaptive decay based on time constant
         * decay = 1 - exp(-dt / τ)
         */
        public void adaptiveDecay(float dt) {
            float decay = 1.0f - (float)Math.exp(-dt / timeConstant);
            importance = importance * (1.0 - decay);
            if (importance < 0.001) importance = 0.001;
        }
        
        public void updateTimeConstant(float newFrequency) {
            this.inputFrequency = newFrequency;
            this.timeConstant = computeTimeConstant(newFrequency);
        }
        
        public double age() {
            return (System.currentTimeMillis() - lastAccessed) / 1000.0; // seconds
        }
        
        public double getMemoryHorizon() {
            return timeConstant; // τ represents the memory horizon
        }
    }
    
    // Memory storage (thread-safe)
    private final Map<String, MemoryItem> memory = new ConcurrentHashMap<>();
    
    // Access order for LRU eviction
    private final LinkedHashMap<String, MemoryItem> accessOrder = 
        new LinkedHashMap<String, MemoryItem>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MemoryItem> eldest) {
                return size() > capacity;
            }
        };
    
    // Configuration
    private volatile int capacity = DEFAULT_CAPACITY;
    private volatile double baseDecayRate = BASE_DECAY_RATE;
    
    // Current input frequency estimate
    private volatile float currentInputFrequency = 10.0f; // Default to mid-range (Alpha)
    
    // Frequency smoothing factor
    private float frequencySmoothing = 0.1f;
    
    // Frequency band detection
    private String currentFrequencyBand = "ALPHA";
    
    // Lock for thread-safe operations
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Statistics
    private final AtomicInteger adds = new AtomicInteger(0);
    private final AtomicInteger gets = new AtomicInteger(0);
    private final AtomicInteger evictions = new AtomicInteger(0);
    private final AtomicInteger decays = new AtomicInteger(0);
    
    // Memory horizon tracking
    private float averageMemoryHorizon = 1.0f;
    private float minMemoryHorizon = Float.MAX_VALUE;
    private float maxMemoryHorizon = Float.MIN_VALUE;

    /**
     * Create a new LTCWorkingMemory with default capacity.
     */
    public LTCWorkingMemory() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Create a new LTCWorkingMemory with specified capacity.
     */
    public LTCWorkingMemory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Add an item to working memory with current input frequency.
     */
    public void add(String content, double importance) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (importance < 0.0 || importance > 1.0) {
            throw new IllegalArgumentException("Importance must be in [0, 1]");
        }
        
        lock.writeLock().lock();
        try {
            MemoryItem item = new MemoryItem(content, importance, currentInputFrequency);
            memory.put(content, item);
            accessOrder.put(content, item);
            adds.incrementAndGet();
            
            // Update memory horizon statistics
            updateMemoryHorizonStats();
            
            // Apply adaptive decay to all items
            applyAdaptiveDecay();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add an item with specific input frequency.
     */
    public void add(String content, double importance, float inputFrequency) {
        float prevFreq = currentInputFrequency;
        currentInputFrequency = inputFrequency;
        detectFrequencyBand();
        add(content, importance);
        currentInputFrequency = prevFreq;
    }

    /**
     * Add an item with default importance.
     */
    public void add(String content) {
        add(content, 0.5);
    }

    /**
     * Retrieve an item from working memory.
     */
    public MemoryItem get(String content) {
        if (content == null) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            MemoryItem item = memory.get(content);
            if (item != null) {
                item.access();
                gets.incrementAndGet();
            }
            return item;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Check if content is in working memory.
     */
    public boolean contains(String content) {
        return memory.containsKey(content);
    }

    /**
     * Get all items in working memory.
     */
    public List<MemoryItem> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(memory.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get items sorted by importance (highest first).
     */
    public List<MemoryItem> getByImportance() {
        List<MemoryItem> items = getAll();
        items.sort((a, b) -> Double.compare(b.importance, a.importance));
        return items;
    }

    /**
     * Get items sorted by memory horizon (longest first).
     */
    public List<MemoryItem> getByMemoryHorizon() {
        List<MemoryItem> items = getAll();
        items.sort((a, b) -> Float.compare(b.timeConstant, a.timeConstant));
        return items;
    }

    /**
     * Remove an item from working memory.
     */
    public boolean remove(String content) {
        if (content == null) {
            return false;
        }
        
        lock.writeLock().lock();
        try {
            MemoryItem removed = memory.remove(content);
            accessOrder.remove(content);
            if (removed != null) {
                updateMemoryHorizonStats();
            }
            return removed != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear all items from working memory.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            memory.clear();
            accessOrder.clear();
            adds.set(0);
            gets.set(0);
            evictions.set(0);
            decays.set(0);
            averageMemoryHorizon = 1.0f;
            minMemoryHorizon = Float.MAX_VALUE;
            maxMemoryHorizon = Float.MIN_VALUE;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Apply adaptive decay to all items in memory.
     * Each item decays based on its own time constant τ.
     */
    private void applyAdaptiveDecay() {
        lock.writeLock().lock();
        try {
            float dt = 0.05f; // Time step (50ms)
            List<String> toRemove = new ArrayList<>();
            
            for (Map.Entry<String, MemoryItem> entry : memory.entrySet()) {
                MemoryItem item = entry.getValue();
                
                // Update time constant based on current frequency
                item.updateTimeConstant(currentInputFrequency);
                
                // Apply adaptive decay
                item.adaptiveDecay(dt);
                decays.incrementAndGet();
                
                // Remove items with very low importance
                if (item.importance < 0.01) {
                    toRemove.add(entry.getKey());
                }
            }
            
            for (String key : toRemove) {
                memory.remove(key);
                accessOrder.remove(key);
                evictions.incrementAndGet();
            }
            
            updateMemoryHorizonStats();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Manually trigger adaptive decay on all items.
     */
    public void triggerDecay() {
        lock.writeLock().lock();
        try {
            applyAdaptiveDecay();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Update memory horizon statistics.
     */
    private void updateMemoryHorizonStats() {
        if (memory.isEmpty()) {
            averageMemoryHorizon = 1.0f;
            minMemoryHorizon = Float.MAX_VALUE;
            maxMemoryHorizon = Float.MIN_VALUE;
            return;
        }
        
        float sum = 0.0f;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        
        for (MemoryItem item : memory.values()) {
            float horizon = (float) item.getMemoryHorizon();
            sum += horizon;
            min = Math.min(min, horizon);
            max = Math.max(max, horizon);
        }
        
        averageMemoryHorizon = sum / memory.size();
        minMemoryHorizon = min;
        maxMemoryHorizon = max;
    }

    /**
     * Detect current frequency band.
     */
    private void detectFrequencyBand() {
        if (currentInputFrequency >= DELTA_MIN && currentInputFrequency < DELTA_MAX) {
            currentFrequencyBand = "DELTA";
        } else if (currentInputFrequency >= THETA_MIN && currentInputFrequency < THETA_MAX) {
            currentFrequencyBand = "THETA";
        } else if (currentInputFrequency >= ALPHA_MIN && currentInputFrequency < ALPHA_MAX) {
            currentFrequencyBand = "ALPHA";
        } else if (currentInputFrequency >= BETA_MIN && currentInputFrequency < BETA_MAX) {
            currentFrequencyBand = "BETA";
        } else if (currentInputFrequency >= GAMMA_MIN && currentInputFrequency <= GAMMA_MAX) {
            currentFrequencyBand = "GAMMA";
        } else {
            currentFrequencyBand = "UNKNOWN";
        }
    }

    /**
     * Update input frequency estimate.
     * Use this when you have external frequency information (e.g., from neural interface).
     */
    public void updateInputFrequency(float frequency) {
        lock.writeLock().lock();
        try {
            // Smooth frequency estimate
            currentInputFrequency = frequencySmoothing * frequency + 
                                  (1.0f - frequencySmoothing) * currentInputFrequency;
            detectFrequencyBand();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get current input frequency.
     */
    public float getCurrentInputFrequency() {
        return currentInputFrequency;
    }

    /**
     * Get current frequency band.
     */
    public String getCurrentFrequencyBand() {
        return currentFrequencyBand;
    }

    /**
     * Get average memory horizon (τ).
     */
    public float getAverageMemoryHorizon() {
        return averageMemoryHorizon;
    }

    /**
     * Get memory horizon range.
     */
    public float[] getMemoryHorizonRange() {
        return new float[] { minMemoryHorizon, maxMemoryHorizon };
    }

    /**
     * Set the capacity of working memory.
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        
        lock.writeLock().lock();
        try {
            this.capacity = capacity;
            while (memory.size() > capacity) {
                Map.Entry<String, MemoryItem> eldest = accessOrder.entrySet().iterator().next();
                memory.remove(eldest.getKey());
                accessOrder.remove(eldest.getKey());
                evictions.incrementAndGet();
            }
            updateMemoryHorizonStats();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get the current capacity.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Set the base decay rate (used as fallback).
     */
    public void setBaseDecayRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Decay rate must be in [0, 1]");
        }
        this.baseDecayRate = rate;
    }

    /**
     * Get the current base decay rate.
     */
    public double getBaseDecayRate() {
        return baseDecayRate;
    }

    /**
     * Get the number of items in working memory.
     */
    public int size() {
        return memory.size();
    }

    /**
     * Check if working memory is empty.
     */
    public boolean isEmpty() {
        return memory.isEmpty();
    }

    /**
     * Get statistics about working memory including memory horizon info.
     */
    public String getStats() {
        lock.readLock().lock();
        try {
            double avgImportance = memory.values().stream()
                .mapToDouble(item -> item.importance)
                .average()
                .orElse(0.0);
            
            return String.format(
                "LTCWorkingMemory Stats:\n" +
                "  Capacity: %,d\n" +
                "  Size: %,d\n" +
                "  Base decay rate: %.3f\n" +
                "  Current input frequency: %.2f Hz\n" +
                "  Current frequency band: %s\n" +
                "  Average memory horizon (τ): %.3f s\n" +
                "  Memory horizon range: [%.3f, %.3f] s\n" +
                "  Adds: %,d\n" +
                "  Gets: %,d\n" +
                "  Evictions: %,d\n" +
                "  Decays: %,d\n" +
                "  Avg importance: %.3f",
                capacity,
                memory.size(),
                baseDecayRate,
                currentInputFrequency,
                currentFrequencyBand,
                averageMemoryHorizon,
                minMemoryHorizon,
                maxMemoryHorizon,
                adds.get(),
                gets.get(),
                evictions.get(),
                decays.get(),
                avgImportance
            );
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Verify fluid memory horizons.
     * Returns true if memory horizons are adapting to input frequency.
     */
    public boolean verifyFluidMemoryHorizons() {
        lock.readLock().lock();
        try {
            if (memory.size() < 2) {
                return true; // Not enough data to verify
            }
            
            // Check if time constants vary based on frequency
            double[] doubleHorizons = memory.values().stream()
                .mapToDouble(item -> item.timeConstant)
                .toArray();
            float[] horizons = new float[doubleHorizons.length];
            for (int i = 0; i < doubleHorizons.length; i++) {
                horizons[i] = (float) doubleHorizons[i];
            }
            
            float variance = computeVariance(horizons);
            
            // Variance should be non-zero (adaptive) but not too high (stable)
            return variance > 0.01f && variance < 100.0f;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Compute variance of float array.
     */
    private float computeVariance(float[] values) {
        if (values.length == 0) return 0.0f;
        
        float mean = 0.0f;
        for (float v : values) mean += v;
        mean /= values.length;
        
        float variance = 0.0f;
        for (float v : values) variance += (v - mean) * (v - mean);
        variance /= values.length;
        
        return variance;
    }
}
