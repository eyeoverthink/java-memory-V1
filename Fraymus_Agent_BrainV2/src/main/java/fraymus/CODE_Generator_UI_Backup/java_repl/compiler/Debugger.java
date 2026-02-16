/**
 * Debugger.java - Interactive Debugger with Breakpoints
 * 
 * Provides debugging capabilities:
 * - Breakpoints (line-based)
 * - Step execution
 * - Variable inspection
 * - Stack trace
 * - φ-harmonic execution tracking
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Interactive debugger for compiled code.
 */
public class Debugger {
    
    private static final double PHI = 1.618033988749895;
    
    private final Set<Integer> breakpoints;
    private final List<StackFrame> callStack;
    private final Map<Integer, Integer> executionCounts;
    private boolean stepping;
    private int currentLine;
    private boolean paused;
    
    /**
     * Stack frame for call stack tracking.
     */
    public static class StackFrame {
        private final String name;
        private final int line;
        private final Map<String, Object> locals;
        private final double phiWeight;
        
        public StackFrame(String name, int line) {
            this.name = name;
            this.line = line;
            this.locals = new HashMap<>();
            this.phiWeight = Math.pow(PHI, line % 7);
        }
        
        public String getName() { return name; }
        public int getLine() { return line; }
        public Map<String, Object> getLocals() { return new HashMap<>(locals); }
        public double getPhiWeight() { return phiWeight; }
        
        public void setLocal(String name, Object value) {
            locals.put(name, value);
        }
        
        @Override
        public String toString() {
            return String.format("%s (line %d, φ=%.4f)", name, line, phiWeight);
        }
    }
    
    public Debugger() {
        this.breakpoints = new HashSet<>();
        this.callStack = new ArrayList<>();
        this.executionCounts = new HashMap<>();
        this.stepping = false;
        this.currentLine = 0;
        this.paused = false;
    }
    
    /**
     * Add breakpoint at line.
     */
    public void addBreakpoint(int line) {
        breakpoints.add(line);
    }
    
    /**
     * Remove breakpoint at line.
     */
    public void removeBreakpoint(int line) {
        breakpoints.remove(line);
    }
    
    /**
     * Clear all breakpoints.
     */
    public void clearBreakpoints() {
        breakpoints.clear();
    }
    
    /**
     * Get all breakpoints.
     */
    public Set<Integer> getBreakpoints() {
        return new HashSet<>(breakpoints);
    }
    
    /**
     * Enable step mode.
     */
    public void enableStepping() {
        stepping = true;
    }
    
    /**
     * Disable step mode.
     */
    public void disableStepping() {
        stepping = false;
    }
    
    /**
     * Check if stepping is enabled.
     */
    public boolean isStepping() {
        return stepping;
    }
    
    /**
     * Continue execution.
     */
    public void continueExecution() {
        paused = false;
    }
    
    /**
     * Check if paused.
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Execute line (called by interpreter).
     */
    public void executeLine(int line) {
        currentLine = line;
        executionCounts.put(line, executionCounts.getOrDefault(line, 0) + 1);
        
        // Check breakpoint
        if (breakpoints.contains(line) || stepping) {
            paused = true;
        }
    }
    
    /**
     * Get current line.
     */
    public int getCurrentLine() {
        return currentLine;
    }
    
    /**
     * Push stack frame.
     */
    public void pushFrame(String name, int line) {
        callStack.add(new StackFrame(name, line));
    }
    
    /**
     * Pop stack frame.
     */
    public StackFrame popFrame() {
        if (!callStack.isEmpty()) {
            return callStack.remove(callStack.size() - 1);
        }
        return null;
    }
    
    /**
     * Get current frame.
     */
    public StackFrame getCurrentFrame() {
        if (callStack.isEmpty()) return null;
        return callStack.get(callStack.size() - 1);
    }
    
    /**
     * Get call stack.
     */
    public List<StackFrame> getCallStack() {
        return new ArrayList<>(callStack);
    }
    
    /**
     * Get execution count for line.
     */
    public int getExecutionCount(int line) {
        return executionCounts.getOrDefault(line, 0);
    }
    
    /**
     * Get all execution counts.
     */
    public Map<Integer, Integer> getExecutionCounts() {
        return new HashMap<>(executionCounts);
    }
    
    /**
     * Clear execution counts.
     */
    public void clearExecutionCounts() {
        executionCounts.clear();
    }
    
    /**
     * Reset debugger state.
     */
    public void reset() {
        breakpoints.clear();
        callStack.clear();
        executionCounts.clear();
        stepping = false;
        currentLine = 0;
        paused = false;
    }
    
    /**
     * Get debugger status.
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  DEBUGGER STATUS (φ-Harmonic Execution Tracking)           ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append(String.format("Current Line: %d\n", currentLine));
        sb.append(String.format("Paused: %s\n", paused ? "YES" : "NO"));
        sb.append(String.format("Stepping: %s\n\n", stepping ? "ENABLED" : "DISABLED"));
        
        sb.append(String.format("Breakpoints (%d):\n", breakpoints.size()));
        if (breakpoints.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            List<Integer> sorted = new ArrayList<>(breakpoints);
            Collections.sort(sorted);
            for (int line : sorted) {
                sb.append(String.format("  Line %d\n", line));
            }
        }
        sb.append("\n");
        
        sb.append(String.format("Call Stack (%d frames):\n", callStack.size()));
        if (callStack.isEmpty()) {
            sb.append("  (empty)\n");
        } else {
            for (int i = callStack.size() - 1; i >= 0; i--) {
                StackFrame frame = callStack.get(i);
                sb.append(String.format("  %d: %s\n", callStack.size() - i, frame));
            }
        }
        sb.append("\n");
        
        sb.append("Hot Lines (most executed):\n");
        executionCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .forEach(entry -> {
                sb.append(String.format("  Line %d: %d executions\n", entry.getKey(), entry.getValue()));
            });
        
        return sb.toString();
    }
    
    /**
     * Get stack trace.
     */
    public String getStackTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  STACK TRACE                                                ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        if (callStack.isEmpty()) {
            sb.append("(empty stack)\n");
        } else {
            for (int i = callStack.size() - 1; i >= 0; i--) {
                StackFrame frame = callStack.get(i);
                sb.append(String.format("  at %s (line %d)\n", frame.getName(), frame.getLine()));
                if (!frame.getLocals().isEmpty()) {
                    sb.append("    Locals:\n");
                    frame.getLocals().forEach((name, value) -> {
                        sb.append(String.format("      %s = %s\n", name, value));
                    });
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Inspect variable at current frame.
     */
    public String inspectVariable(String name, SymbolTable symbolTable) {
        SymbolTable.Symbol symbol = symbolTable.lookup(name);
        if (symbol == null) {
            return "Variable not found: " + name;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  VARIABLE: %-48s ║\n", name));
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("Type:      %s\n", symbol.getType()));
        sb.append(String.format("Value:     %s\n", symbol.getValue()));
        sb.append(String.format("Address:   0x%04X\n", symbol.getAddress()));
        sb.append(String.format("Scope:     %d\n", symbol.getScopeLevel()));
        sb.append(String.format("φ-Weight:  %.6f\n", symbol.getPhiWeight()));
        
        return sb.toString();
    }
}
