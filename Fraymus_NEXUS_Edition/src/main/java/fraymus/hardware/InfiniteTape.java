package fraymus.hardware;

import java.util.HashMap;
import java.util.Map;

/**
 * THE INFINITE TAPE
 * "A memory system with no boundaries."
 * 
 * This is the Turing Machine's infinite tape implemented in software.
 * Unlike standard RAM (fixed array), this grows dynamically as you explore it.
 * 
 * STANDARD RAM:
 * int[] memory = new int[256]; // Fixed, limited
 * 
 * INFINITE TAPE:
 * Map<Long, Integer> cells; // Unlimited, sparse
 * 
 * You can write to address 0 or address 10,000,000,000.
 * Only stores what you write (sparse storage).
 * 
 * "The universe expands as you explore it."
 */
public class InfiniteTape {

    // Map<Address, Value> → Allows writing to any address
    // Boolean-based for circuit evolution (true/false, 1/0)
    private Map<Long, Boolean> cells = new HashMap<>();
    private long headPosition = 0;
    private long minAddress = 0;
    private long maxAddress = 0;

    public InfiniteTape() {
        System.out.println("📼 INFINITE TAPE INITIALIZED");
        System.out.println("   Memory: Unlimited");
        System.out.println("   Storage: Sparse (only stores written cells)");
        System.out.println();
    }

    /**
     * Write boolean value to address
     * 
     * @param address Memory address (can be any long value)
     * @param value Boolean value to store
     */
    public void write(long address, boolean value) {
        cells.put(address, value);
        
        // Track bounds
        if (cells.size() == 1) {
            minAddress = address;
            maxAddress = address;
        } else {
            if (address < minAddress) minAddress = address;
            if (address > maxAddress) maxAddress = address;
        }
        
        // Visualize expansion
        if (cells.size() % 1000 == 0) {
            System.out.println("   🧠 MEMORY EXPANDED: " + cells.size() + " active cells");
            System.out.println("      Range: [" + minAddress + " → " + maxAddress + "]");
        }
    }

    /**
     * Read boolean value from address
     * Returns false if cell has never been written (the void)
     * 
     * @param address Memory address
     * @return Boolean value at address, or false if unwritten
     */
    public boolean read(long address) {
        return cells.getOrDefault(address, false);
    }
    
    /**
     * Move tape head (Turing machine operation)
     * 
     * @param offset Offset to move (positive = right, negative = left)
     */
    public void moveHead(long offset) {
        headPosition += offset;
    }
    
    /**
     * Get current head position
     */
    public long getHead() {
        return headPosition;
    }
    
    /**
     * Set head position
     */
    public void setHead(long position) {
        headPosition = position;
    }
    
    /**
     * Read boolean value at current head position
     */
    public boolean readAtHead() {
        return read(headPosition);
    }
    
    /**
     * Write boolean value at current head position
     */
    public void writeAtHead(boolean value) {
        write(headPosition, value);
    }
    
    /**
     * Get number of active cells
     */
    public int getActiveCells() {
        return cells.size();
    }
    
    /**
     * Get memory span (max - min address)
     */
    public long getSpan() {
        if (cells.isEmpty()) return 0;
        return maxAddress - minAddress;
    }
    
    /**
     * Clear all memory
     */
    public void clear() {
        cells.clear();
        headPosition = 0;
        minAddress = 0;
        maxAddress = 0;
        System.out.println("   🧹 TAPE CLEARED");
    }
    
    /**
     * Show statistics
     */
    public void showStats() {
        System.out.println("📼 INFINITE TAPE STATISTICS");
        System.out.println("   Active cells: " + cells.size());
        System.out.println("   Head position: " + headPosition);
        if (!cells.isEmpty()) {
            System.out.println("   Address range: [" + minAddress + " → " + maxAddress + "]");
            System.out.println("   Span: " + getSpan() + " addresses");
        }
        System.out.println();
    }
}
