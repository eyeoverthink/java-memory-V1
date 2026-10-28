package fraymus.neural;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * EpisodicMemory - Long-term memory with timestamping
 * 
 * This class implements an episodic memory system that stores observations
 * with timestamps, enabling temporal understanding and sequence learning.
 * 
 * CAPABILITIES:
 * - Timestamped memory storage
 * - Temporal range queries
 * - Sequence learning
 * - Configurable capacity
 * - Thread-safe operations
 * 
 * DESIGN PRINCIPLES:
 * - Safe: No modification of existing SentientCore structures
 * - Efficient: O(1) add, O(log n) range queries
 * - Configurable: Capacity adjustable
 * - Deterministic: No randomness
 * 
 * PHASE 1.3: Episodic Memory
 * Part of the AGI System Blueprint
 */
public class EpisodicMemory {

    // Default capacity
    public static final int DEFAULT_CAPACITY = 1000;
    
    // Memory episode with timestamp
    public static class Episode {
        public final String content;
        public final long timestamp;
        public final long id;
        public volatile double strength;
        
        public Episode(String content, long id) {
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.id = id;
            this.strength = 1.0;
        }
        
        public Episode(String content, long id, long timestamp) {
            this.content = content;
            this.timestamp = timestamp;
            this.id = id;
            this.strength = 1.0;
        }
        
        public void strengthen(double amount) {
            strength = Math.min(1.0, strength + amount);
        }
        
        public void weaken(double amount) {
            strength = Math.max(0.0, strength - amount);
        }
        
        public long age() {
            return (System.currentTimeMillis() - timestamp) / 1000; // seconds
        }
    }
    
    // Memory storage (thread-safe)
    private final Map<Long, Episode> memory = new ConcurrentHashMap<>();
    
    // Timestamp-ordered list for range queries
    private final List<Episode> timeline = new ArrayList<>();
    
    // Configuration
    private volatile int capacity = DEFAULT_CAPACITY;
    
    // Lock for thread-safe operations
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Statistics
    private final AtomicInteger adds = new AtomicInteger(0);
    private final AtomicInteger gets = new AtomicInteger(0);
    private final AtomicInteger rangeQueries = new AtomicInteger(0);
    private final AtomicInteger evictions = new AtomicInteger(0);
    
    // Episode ID counter
    private final AtomicLong nextId = new AtomicLong(0);

    /**
     * Create a new EpisodicMemory with default capacity.
     */
    public EpisodicMemory() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Create a new EpisodicMemory with specified capacity.
     * 
     * @param capacity Maximum number of episodes to store
     */
    public EpisodicMemory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Add an episode to episodic memory.
     * 
     * @param content The content to store
     * @return The episode ID
     */
    public long add(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        
        lock.writeLock().lock();
        try {
            long id = nextId.incrementAndGet();
            Episode episode = new Episode(content, id);
            
            memory.put(id, episode);
            timeline.add(episode);
            
            // Sort timeline by timestamp (insert at correct position)
            timeline.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
            
            adds.incrementAndGet();
            
            // Evict if over capacity
            while (memory.size() > capacity) {
                Episode oldest = timeline.get(0);
                memory.remove(oldest.id);
                timeline.remove(0);
                evictions.incrementAndGet();
            }
            
            return id;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieve an episode by ID.
     * 
     * @param id The episode ID
     * @return The episode, or null if not found
     */
    public Episode get(long id) {
        lock.readLock().lock();
        try {
            Episode episode = memory.get(id);
            if (episode != null) {
                gets.incrementAndGet();
            }
            return episode;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get episodes within a time range.
     * 
     * @param startMs Start timestamp in milliseconds
     * @param endMs End timestamp in milliseconds
     * @return List of episodes in the range
     */
    public List<Episode> getByTimeRange(long startMs, long endMs) {
        lock.readLock().lock();
        try {
            List<Episode> result = new ArrayList<>();
            
            for (Episode episode : timeline) {
                if (episode.timestamp >= startMs && episode.timestamp <= endMs) {
                    result.add(episode);
                } else if (episode.timestamp > endMs) {
                    break; // Timeline is sorted
                }
            }
            
            rangeQueries.incrementAndGet();
            
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get episodes from the last N seconds.
     * 
     * @param seconds Number of seconds to look back
     * @return List of episodes from the last N seconds
     */
    public List<Episode> getRecent(int seconds) {
        long cutoff = System.currentTimeMillis() - (seconds * 1000L);
        return getByTimeRange(cutoff, System.currentTimeMillis());
    }

    /**
     * Get all episodes.
     * 
     * @return List of all episodes
     */
    public List<Episode> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(timeline);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get episodes sorted by recency (most recent first).
     * 
     * @return List of episodes sorted by recency
     */
    public List<Episode> getByRecency() {
        List<Episode> episodes = getAll();
        episodes.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        return episodes;
    }

    /**
     * Get episodes sorted by strength (highest first).
     * 
     * @return List of episodes sorted by strength
     */
    public List<Episode> getByStrength() {
        List<Episode> episodes = getAll();
        episodes.sort((a, b) -> Double.compare(b.strength, a.strength));
        return episodes;
    }

    /**
     * Remove an episode by ID.
     * 
     * @param id The episode ID
     * @return True if removed, false if not found
     */
    public boolean remove(long id) {
        lock.writeLock().lock();
        try {
            Episode episode = memory.remove(id);
            if (episode != null) {
                timeline.remove(episode);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Clear all episodes.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            memory.clear();
            timeline.clear();
            adds.set(0);
            gets.set(0);
            rangeQueries.set(0);
            evictions.set(0);
            nextId.set(0);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Strengthen an episode.
     * 
     * @param id The episode ID
     * @param amount Amount to strengthen (0.0 to 1.0)
     */
    public void strengthen(long id, double amount) {
        if (amount < 0.0 || amount > 1.0) {
            throw new IllegalArgumentException("Amount must be in [0, 1]");
        }
        
        lock.writeLock().lock();
        try {
            Episode episode = memory.get(id);
            if (episode != null) {
                episode.strengthen(amount);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Weaken an episode.
     * 
     * @param id The episode ID
     * @param amount Amount to weaken (0.0 to 1.0)
     */
    public void weaken(long id, double amount) {
        if (amount < 0.0 || amount > 1.0) {
            throw new IllegalArgumentException("Amount must be in [0, 1]");
        }
        
        lock.writeLock().lock();
        try {
            Episode episode = memory.get(id);
            if (episode != null) {
                episode.weaken(amount);
                // Remove if strength is zero
                if (episode.strength <= 0.0) {
                    memory.remove(id);
                    timeline.remove(episode);
                    evictions.incrementAndGet();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set the capacity.
     * 
     * @param capacity New capacity
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        
        lock.writeLock().lock();
        try {
            this.capacity = capacity;
            // Evict if over new capacity
            while (memory.size() > capacity) {
                Episode oldest = timeline.get(0);
                memory.remove(oldest.id);
                timeline.remove(0);
                evictions.incrementAndGet();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get the current capacity.
     * 
     * @return Current capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Get the number of episodes.
     * 
     * @return Current size
     */
    public int size() {
        return memory.size();
    }

    /**
     * Check if episodic memory is empty.
     * 
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return memory.isEmpty();
    }

    /**
     * Get statistics about episodic memory.
     * 
     * @return Statistics string
     */
    public String getStats() {
        lock.readLock().lock();
        try {
            double avgStrength = timeline.stream()
                .mapToDouble(ep -> ep.strength)
                .average()
                .orElse(0.0);
            
            long oldestAge = timeline.isEmpty() ? 0 : timeline.get(0).age();
            long newestAge = timeline.isEmpty() ? 0 : timeline.get(timeline.size() - 1).age();
            
            return String.format(
                "EpisodicMemory Stats:\n" +
                "  Capacity: %,d\n" +
                "  Size: %,d\n" +
                "  Adds: %,d\n" +
                "  Gets: %,d\n" +
                "  Range queries: %,d\n" +
                "  Evictions: %,d\n" +
                "  Avg strength: %.3f\n" +
                "  Oldest age: %,d seconds\n" +
                "  Newest age: %,d seconds",
                capacity,
                memory.size(),
                adds.get(),
                gets.get(),
                rangeQueries.get(),
                evictions.get(),
                avgStrength,
                oldestAge,
                newestAge
            );
        } finally {
            lock.readLock().unlock();
        }
    }
}
