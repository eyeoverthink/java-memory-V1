/**
 * Goroutine.java - Go Concurrency Compatibility
 * 
 * 🧬 THE GOROUTINE
 * 
 * Java implementation of Go's 'go' keyword using Virtual Threads (Java 21+).
 * 
 * Go Syntax:
 *   go myFunction()
 * 
 * Java Equivalent:
 *   Goroutine.go(() -> myFunction());
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 128 (Alchemy Engine)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package fraymus.golang;

import java.util.concurrent.*;

/**
 * Goroutine launcher using Virtual Threads.
 */
public class Goroutine {
    
    private static final ExecutorService executor = 
        Executors.newVirtualThreadPerTaskExecutor();
    
    /**
     * Launch goroutine (go func()).
     * 
     * Uses Java 21 Virtual Threads for lightweight concurrency.
     */
    public static Future<?> go(Runnable task) {
        return executor.submit(task);
    }
    
    /**
     * Launch goroutine with return value.
     */
    public static <T> Future<T> go(Callable<T> task) {
        return executor.submit(task);
    }
    
    /**
     * Wait for goroutine to complete.
     */
    public static <T> T await(Future<T> future) throws Exception {
        return future.get();
    }
    
    /**
     * Shutdown all goroutines (for cleanup).
     */
    public static void shutdown() {
        executor.shutdown();
    }
    
    /**
     * Force shutdown all goroutines.
     */
    public static void shutdownNow() {
        executor.shutdownNow();
    }
}
