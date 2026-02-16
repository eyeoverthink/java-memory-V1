/**
 * Channel.java - Go Channel Compatibility Layer
 * 
 * 🧬 THE CHANNEL
 * 
 * A Java implementation of Go's 'chan' keyword.
 * Allows transmuted Go code to run natively in Java.
 * 
 * Go Syntax:
 *   ch := make(chan int, 10)
 *   ch <- 42
 *   val := <-ch
 * 
 * Java Equivalent:
 *   Channel<Integer> ch = new Channel<>(10);
 *   ch.send(42);
 *   int val = ch.receive();
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
 * Go channel implementation using Java BlockingQueue.
 */
public class Channel<T> {
    
    private final BlockingQueue<T> queue;
    private volatile boolean closed = false;
    
    /**
     * Create unbuffered channel (blocking).
     */
    public Channel() {
        this.queue = new SynchronousQueue<>();
    }
    
    /**
     * Create buffered channel.
     */
    public Channel(int bufferSize) {
        this.queue = bufferSize > 0
            ? new LinkedBlockingQueue<>(bufferSize)
            : new SynchronousQueue<>();
    }
    
    /**
     * Send value to channel (ch <- val).
     * Blocks if channel is full.
     */
    public void send(T value) throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("Send on closed channel");
        }
        queue.put(value);
    }
    
    /**
     * Receive value from channel (val := <-ch).
     * Blocks if channel is empty.
     */
    public T receive() throws InterruptedException {
        T value = queue.take();
        if (value == null && closed) {
            return null; // Channel closed
        }
        return value;
    }
    
    /**
     * Try to send without blocking.
     */
    public boolean trySend(T value) {
        if (closed) return false;
        return queue.offer(value);
    }
    
    /**
     * Try to receive without blocking.
     */
    public T tryReceive() {
        return queue.poll();
    }
    
    /**
     * Close channel (close(ch)).
     */
    public void close() {
        closed = true;
    }
    
    /**
     * Check if channel is closed.
     */
    public boolean isClosed() {
        return closed;
    }
    
    /**
     * Get channel capacity.
     */
    public int capacity() {
        return queue.remainingCapacity() + queue.size();
    }
    
    /**
     * Get number of elements in channel.
     */
    public int size() {
        return queue.size();
    }
}
