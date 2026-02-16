/**
 * SymbolTable.java - Memory Management with φ-Harmonic Addressing
 * 
 * Symbol table for variable storage and scope management.
 * Uses φ-harmonic addressing for optimal memory layout.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import java.util.*;

/**
 * Symbol table with scoped variable storage.
 */
public class SymbolTable {
    
    private static final double PHI = 1.618033988749895;
    
    /**
     * Symbol entry with type and memory address.
     */
    public static class Symbol {
        private final String name;
        private final SymbolType type;
        private Object value;
        private final int address;
        private final int scopeLevel;
        private final double phiWeight;
        
        public Symbol(String name, SymbolType type, Object value, int address, int scopeLevel) {
            this.name = name;
            this.type = type;
            this.value = value;
            this.address = address;
            this.scopeLevel = scopeLevel;
            this.phiWeight = Math.pow(PHI, scopeLevel % 7);
        }
        
        public String getName() { return name; }
        public SymbolType getType() { return type; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public int getAddress() { return address; }
        public int getScopeLevel() { return scopeLevel; }
        public double getPhiWeight() { return phiWeight; }
        
        @Override
        public String toString() {
            return String.format("Symbol{%s, type=%s, addr=0x%04X, scope=%d, φ=%.4f, value=%s}",
                name, type, address, scopeLevel, phiWeight, value);
        }
    }
    
    /**
     * Symbol types.
     */
    public enum SymbolType {
        NUMBER,
        STRING,
        BOOLEAN,
        FUNCTION,
        ARRAY,
        NULL,
        UNKNOWN
    }
    
    private final List<Map<String, Symbol>> scopes;
    private int currentScope;
    private int nextAddress;
    
    public SymbolTable() {
        this.scopes = new ArrayList<>();
        this.scopes.add(new HashMap<>()); // Global scope
        this.currentScope = 0;
        this.nextAddress = 0;
    }
    
    /**
     * Enter new scope.
     */
    public void enterScope() {
        scopes.add(new HashMap<>());
        currentScope++;
    }
    
    /**
     * Exit current scope.
     */
    public void exitScope() {
        if (currentScope > 0) {
            scopes.remove(currentScope);
            currentScope--;
        }
    }
    
    /**
     * Get current scope level.
     */
    public int getScopeLevel() {
        return currentScope;
    }
    
    /**
     * Define variable in current scope.
     */
    public Symbol define(String name, SymbolType type, Object value) {
        int address = allocateAddress();
        Symbol symbol = new Symbol(name, type, value, address, currentScope);
        scopes.get(currentScope).put(name, symbol);
        return symbol;
    }
    
    /**
     * Lookup variable (searches from current scope up to global).
     */
    public Symbol lookup(String name) {
        for (int i = currentScope; i >= 0; i--) {
            Symbol symbol = scopes.get(i).get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }
    
    /**
     * Check if variable exists in current scope only.
     */
    public boolean existsInCurrentScope(String name) {
        return scopes.get(currentScope).containsKey(name);
    }
    
    /**
     * Assign value to existing variable.
     */
    public boolean assign(String name, Object value) {
        Symbol symbol = lookup(name);
        if (symbol != null) {
            symbol.setValue(value);
            return true;
        }
        return false;
    }
    
    /**
     * Allocate memory address using φ-harmonic addressing.
     */
    private int allocateAddress() {
        int addr = nextAddress;
        nextAddress += (int)(4 * PHI); // φ-harmonic stride
        return addr;
    }
    
    /**
     * Get all symbols in current scope.
     */
    public Map<String, Symbol> getCurrentScopeSymbols() {
        return new HashMap<>(scopes.get(currentScope));
    }
    
    /**
     * Get all symbols (all scopes).
     */
    public List<Symbol> getAllSymbols() {
        List<Symbol> allSymbols = new ArrayList<>();
        for (Map<String, Symbol> scope : scopes) {
            allSymbols.addAll(scope.values());
        }
        return allSymbols;
    }
    
    /**
     * Clear all scopes (reset to global only).
     */
    public void clear() {
        scopes.clear();
        scopes.add(new HashMap<>());
        currentScope = 0;
        nextAddress = 0;
    }
    
    /**
     * Get symbol table dump for debugging.
     */
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  SYMBOL TABLE (φ-Harmonic Memory Layout)                   ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        for (int i = 0; i <= currentScope; i++) {
            sb.append(String.format("SCOPE %d:\n", i));
            Map<String, Symbol> scope = scopes.get(i);
            if (scope.isEmpty()) {
                sb.append("  (empty)\n");
            } else {
                for (Symbol symbol : scope.values()) {
                    sb.append(String.format("  %-15s : %-10s @ 0x%04X = %s\n",
                        symbol.getName(),
                        symbol.getType(),
                        symbol.getAddress(),
                        symbol.getValue()));
                }
            }
            sb.append("\n");
        }
        
        sb.append(String.format("Total Symbols: %d\n", getAllSymbols().size()));
        sb.append(String.format("Next Address: 0x%04X\n", nextAddress));
        
        return sb.toString();
    }
    
    /**
     * Infer type from value.
     */
    public static SymbolType inferType(Object value) {
        if (value == null) return SymbolType.NULL;
        if (value instanceof Number) return SymbolType.NUMBER;
        if (value instanceof String) return SymbolType.STRING;
        if (value instanceof Boolean) return SymbolType.BOOLEAN;
        if (value instanceof java.util.List) return SymbolType.ARRAY;
        if (value instanceof ASTNode) return SymbolType.FUNCTION;
        return SymbolType.UNKNOWN;
    }
}
