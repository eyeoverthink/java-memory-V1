package fraymus.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.time.Instant;

/**
 * 📡 SIGNAL BUS - The Nervous System
 * "All communication flows through here"
 * 
 * Replaces scattered System.out.println calls with a unified
 * event routing system that can broadcast to:
 * - Console
 * - Web UI
 * - Log files
 * - Network mesh
 * 
 * This is the central nervous system of Fraymus.
 */
public class SignalBus {

    private static final List<Consumer<Signal>> listeners = new ArrayList<>();
    private static long messageCount = 0;

    /**
     * Subscribe to all signals
     */
    public static void subscribe(Consumer<Signal> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Emit a signal to all listeners
     */
    public static void emit(String source, String message) {
        emit(source, SignalType.INFO, message);
    }

    /**
     * Emit a typed signal
     */
    public static void emit(String source, SignalType type, String message) {
        Signal signal = new Signal(
            ++messageCount,
            source,
            type,
            message,
            Instant.now()
        );

        synchronized (listeners) {
            for (Consumer<Signal> listener : listeners) {
                try {
                    listener.accept(signal);
                } catch (Exception e) {
                    // Don't let listener failures break the bus
                    System.err.println("Listener error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Convenience methods
     */
    public static void info(String source, String message) {
        emit(source, SignalType.INFO, message);
    }

    public static void warn(String source, String message) {
        emit(source, SignalType.WARN, message);
    }

    public static void error(String source, String message) {
        emit(source, SignalType.ERROR, message);
    }

    public static void debug(String source, String message) {
        emit(source, SignalType.DEBUG, message);
    }

    /**
     * Get message count
     */
    public static long getMessageCount() {
        return messageCount;
    }

    /**
     * Signal types
     */
    public enum SignalType {
        INFO, WARN, ERROR, DEBUG, SYSTEM
    }

    /**
     * Signal record
     */
    public record Signal(
        long id,
        String source,
        SignalType type,
        String message,
        Instant timestamp
    ) {
        public String format() {
            return String.format("[%s] %s: %s", 
                source.toUpperCase(), 
                type, 
                message);
        }

        public String formatSimple() {
            return String.format("[%s] %s", source.toUpperCase(), message);
        }
    }
}
