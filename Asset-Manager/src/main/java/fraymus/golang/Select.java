package fraymus.golang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 🧬 SELECT - Gen 128
 * Java implementation of Go's 'select' statement.
 * 
 * Go:
 *   select {
 *   case msg := <-ch1:
 *       handle(msg)
 *   case ch2 <- val:
 *       // sent
 *   case <-time.After(1 * time.Second):
 *       // timeout
 *   default:
 *       // non-blocking
 *   }
 * 
 * Java:
 *   new Select()
 *       .onReceive(ch1, msg -> handle(msg))
 *       .onSend(ch2, val, () -> {})
 *       .timeout(1000, () -> {})
 *       .defaultCase(() -> {})
 *       .run();
 */
public class Select {
    
    private final List<Case<?>> cases = new ArrayList<>();
    private Runnable defaultCase;
    private long timeoutMs = -1;
    private Runnable timeoutAction;

    /**
     * ON RECEIVE - case v := <-ch:
     */
    public <T> Select onReceive(Channel<T> ch, Consumer<T> action) {
        cases.add(new ReceiveCase<>(ch, action));
        return this;
    }
    
    /**
     * ON SEND - case ch <- v:
     */
    public <T> Select onSend(Channel<T> ch, T value, Runnable action) {
        cases.add(new SendCase<>(ch, value, action));
        return this;
    }
    
    /**
     * DEFAULT - default:
     */
    public Select defaultCase(Runnable action) {
        this.defaultCase = action;
        return this;
    }
    
    /**
     * TIMEOUT - case <-time.After(d):
     */
    public Select timeout(long ms, Runnable action) {
        this.timeoutMs = ms;
        this.timeoutAction = action;
        return this;
    }
    
    /**
     * RUN - Execute the select
     * Returns true if a case was handled, false if default was used
     */
    public boolean run() {
        // Check for immediate availability (non-blocking check)
        for (Case<?> c : cases) {
            if (c.isReady()) {
                c.execute();
                return true;
            }
        }
        
        // If default exists and no case ready, use default
        if (defaultCase != null) {
            defaultCase.run();
            return false;
        }
        
        // Blocking wait with optional timeout
        long deadline = timeoutMs > 0 ? System.currentTimeMillis() + timeoutMs : Long.MAX_VALUE;
        
        while (System.currentTimeMillis() < deadline) {
            for (Case<?> c : cases) {
                if (c.isReady()) {
                    c.execute();
                    return true;
                }
            }
            
            // Small sleep to prevent busy-waiting
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        // Timeout reached
        if (timeoutAction != null) {
            timeoutAction.run();
        }
        return false;
    }
    
    /**
     * RUN FOREVER - Keep selecting in a loop
     * Go: for { select { ... } }
     */
    public void loop() {
        while (!Thread.currentThread().isInterrupted()) {
            run();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // CASE CLASSES
    // ═══════════════════════════════════════════════════════════════════
    
    private interface Case<T> {
        boolean isReady();
        void execute();
    }
    
    private static class ReceiveCase<T> implements Case<T> {
        private final Channel<T> channel;
        private final Consumer<T> action;
        
        ReceiveCase(Channel<T> channel, Consumer<T> action) {
            this.channel = channel;
            this.action = action;
        }
        
        @Override
        public boolean isReady() {
            return !channel.isEmpty();
        }
        
        @Override
        public void execute() {
            T val = channel.tryReceive();
            if (val != null) {
                action.accept(val);
            }
        }
    }
    
    private static class SendCase<T> implements Case<T> {
        private final Channel<T> channel;
        private final T value;
        private final Runnable action;
        
        SendCase(Channel<T> channel, T value, Runnable action) {
            this.channel = channel;
            this.value = value;
            this.action = action;
        }
        
        @Override
        public boolean isReady() {
            return channel.len() < channel.cap();
        }
        
        @Override
        public void execute() {
            if (channel.trySend(value)) {
                action.run();
            }
        }
    }
}
