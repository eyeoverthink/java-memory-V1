package fraymus.golang;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 🧬 GOROUTINE - Gen 128
 * Java implementation of Go's 'go' keyword using Virtual Threads.
 * 
 * Go:      go myFunc()
 * Java:    Goroutine.go(() -> myFunc());
 * 
 * Go:      go func() { ... }()
 * Java:    Goroutine.go(() -> { ... });
 * 
 * Requires Java 21+ for Virtual Threads.
 */
public class Goroutine {
    
    private static final ExecutorService virtualExecutor = 
        Executors.newVirtualThreadPerTaskExecutor();

    /**
     * GO - Fire and forget
     * Go: go func() { ... }()
     */
    public static void go(Runnable task) {
        Thread.startVirtualThread(task);
    }
    
    /**
     * GO with name (for debugging)
     */
    public static void go(String name, Runnable task) {
        Thread.ofVirtual()
            .name(name)
            .start(task);
    }
    
    /**
     * GO with result - Returns a Future
     * Useful for async/await patterns
     */
    public static <T> Future<T> goAsync(Supplier<T> task) {
        return virtualExecutor.submit(task::get);
    }
    
    /**
     * GO with result - Callable version
     */
    public static <T> Future<T> goAsync(Callable<T> task) {
        return virtualExecutor.submit(task);
    }
    
    /**
     * DEFER - Execute at end of scope
     * Go: defer cleanup()
     * 
     * Usage:
     *   try (var d = Goroutine.defer(() -> cleanup())) {
     *       // ... code ...
     *   }
     */
    public static Deferred defer(Runnable action) {
        return new Deferred(action);
    }
    
    /**
     * PANIC - Equivalent to Go's panic()
     */
    public static void panic(String message) {
        throw new GoPanic(message);
    }
    
    /**
     * RECOVER - Catch panics
     * Returns the panic message or null
     */
    public static String recover(Runnable block) {
        try {
            block.run();
            return null;
        } catch (GoPanic p) {
            return p.getMessage();
        }
    }
    
    /**
     * SLEEP - Like time.Sleep()
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * WAIT GROUP - Synchronize multiple goroutines
     * Go: var wg sync.WaitGroup
     */
    public static WaitGroup newWaitGroup() {
        return new WaitGroup();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // WAIT GROUP (sync.WaitGroup)
    // ═══════════════════════════════════════════════════════════════════
    
    public static class WaitGroup {
        private final Phaser phaser = new Phaser(1);
        
        /**
         * ADD - Increment counter
         * Go: wg.Add(1)
         */
        public void add(int delta) {
            for (int i = 0; i < delta; i++) {
                phaser.register();
            }
        }
        
        /**
         * DONE - Decrement counter
         * Go: wg.Done()
         */
        public void done() {
            phaser.arriveAndDeregister();
        }
        
        /**
         * WAIT - Block until counter is zero
         * Go: wg.Wait()
         */
        public void await() {
            phaser.arriveAndAwaitAdvance();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // DEFERRED (for try-with-resources defer pattern)
    // ═══════════════════════════════════════════════════════════════════
    
    public static class Deferred implements AutoCloseable {
        private final Runnable action;
        
        Deferred(Runnable action) {
            this.action = action;
        }
        
        @Override
        public void close() {
            action.run();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PANIC EXCEPTION
    // ═══════════════════════════════════════════════════════════════════
    
    public static class GoPanic extends RuntimeException {
        public GoPanic(String message) {
            super(message);
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // MUTEX (sync.Mutex)
    // ═══════════════════════════════════════════════════════════════════
    
    public static class Mutex {
        private final java.util.concurrent.locks.ReentrantLock lock = 
            new java.util.concurrent.locks.ReentrantLock();
        
        public void lock() {
            lock.lock();
        }
        
        public void unlock() {
            lock.unlock();
        }
        
        public boolean tryLock() {
            return lock.tryLock();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // RWMUTEX (sync.RWMutex)
    // ═══════════════════════════════════════════════════════════════════
    
    public static class RWMutex {
        private final java.util.concurrent.locks.ReentrantReadWriteLock lock = 
            new java.util.concurrent.locks.ReentrantReadWriteLock();
        
        public void rLock() {
            lock.readLock().lock();
        }
        
        public void rUnlock() {
            lock.readLock().unlock();
        }
        
        public void lock() {
            lock.writeLock().lock();
        }
        
        public void unlock() {
            lock.writeLock().unlock();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ONCE (sync.Once)
    // ═══════════════════════════════════════════════════════════════════
    
    public static class Once {
        private volatile boolean done = false;
        private final Object lock = new Object();
        
        public void doOnce(Runnable action) {
            if (!done) {
                synchronized (lock) {
                    if (!done) {
                        action.run();
                        done = true;
                    }
                }
            }
        }
    }
}
