package fraymus.golang;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 🧬 THE CHANNEL - Gen 128
 * A Java implementation of Go's 'chan' keyword.
 * Allows transmuted Go code to feel at home.
 * 
 * Go:      ch := make(chan int, 10)
 * Java:    Channel<Integer> ch = new Channel<>(10);
 * 
 * Go:      ch <- 42
 * Java:    ch.send(42);
 * 
 * Go:      val := <-ch
 * Java:    int val = ch.receive();
 * 
 * Go:      select { case v := <-ch: ... }
 * Java:    ch.tryReceive(100, TimeUnit.MILLISECONDS);
 */
public class Channel<T> {
    
    private final LinkedBlockingQueue<T> queue;
    private volatile boolean closed = false;
    private final int capacity;

    /**
     * Create unbuffered channel (synchronous)
     */
    public Channel() {
        this(0);
    }
    
    /**
     * Create buffered channel
     * Go: make(chan T, bufferSize)
     */
    public Channel(int bufferSize) {
        this.capacity = bufferSize;
        this.queue = bufferSize > 0 
            ? new LinkedBlockingQueue<>(bufferSize) 
            : new LinkedBlockingQueue<>(1);  // Unbuffered = sync
    }

    /**
     * SEND - Blocking send
     * Go: ch <- val
     */
    public void send(T val) {
        if (closed) {
            throw new IllegalStateException("send on closed channel");
        }
        try {
            queue.put(val);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * TRY SEND - Non-blocking send
     * Returns true if successful
     */
    public boolean trySend(T val) {
        if (closed) return false;
        return queue.offer(val);
    }
    
    /**
     * SEND WITH TIMEOUT
     */
    public boolean send(T val, long timeout, TimeUnit unit) {
        if (closed) return false;
        try {
            return queue.offer(val, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * RECEIVE - Blocking receive
     * Go: val := <-ch
     */
    public T receive() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * TRY RECEIVE - Non-blocking receive
     * Returns null if no value available
     */
    public T tryReceive() {
        return queue.poll();
    }
    
    /**
     * RECEIVE WITH TIMEOUT
     * Go: select { case v := <-ch: ... case <-time.After(timeout): ... }
     */
    public T receive(long timeout, TimeUnit unit) {
        try {
            return queue.poll(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * CLOSE - Close the channel
     * Go: close(ch)
     */
    public void close() {
        this.closed = true;
    }
    
    /**
     * IS CLOSED
     */
    public boolean isClosed() {
        return closed;
    }
    
    /**
     * LENGTH - Number of items in buffer
     * Go: len(ch)
     */
    public int len() {
        return queue.size();
    }
    
    /**
     * CAPACITY - Buffer capacity
     * Go: cap(ch)
     */
    public int cap() {
        return capacity;
    }
    
    /**
     * IS EMPTY
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * RANGE - Iterate until channel is closed
     * Go: for v := range ch { ... }
     */
    public void range(java.util.function.Consumer<T> consumer) {
        Thread.startVirtualThread(() -> {
            while (!closed || !queue.isEmpty()) {
                T val = receive();
                if (val != null) {
                    consumer.accept(val);
                }
            }
        });
    }
}
