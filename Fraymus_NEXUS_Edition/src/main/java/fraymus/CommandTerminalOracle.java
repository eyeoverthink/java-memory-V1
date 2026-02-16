package fraymus;

import fraymus.oracle.QuantumOracle;
import fraymus.oracle.Timeline;

/**
 * FRAYMUS QUANTUM ORACLE COMMAND HANDLERS
 * Multi-timeline reality simulation using learned knowledge
 */
public class CommandTerminalOracle {
    
    private static QuantumOracle oracle = null;
    private static Timeline currentReality = null;
    
    /**
     * Handle all oracle-related commands (CommandProcessor interface)
     */
    public static void handle(String command, String args) {
        String[] parts = args.trim().split("\\s+", 2);
        String subCommand = parts.length > 0 ? parts[0].toLowerCase() : "";
        
        switch (subCommand) {
            case "":
            case "help":
                showOracleHelp();
                break;
            case "init":
                initOracle();
                break;
            case "consult":
                consultOracle();
                break;
            case "status":
                showOracleStatus();
                break;
            case "run":
                int cycles = parts.length > 1 ? Integer.parseInt(parts[1]) : 5;
                runOracleCycles(cycles);
                break;
            case "reset":
            case "collapse":
                resetOracle();
                break;
            default:
                System.err.println("Unknown oracle command: " + subCommand);
                showOracleHelp();
        }
    }
    
    /**
     * Legacy method name for compatibility
     */
    public static void handleOracleCommand(String args) {
        handle("oracle", args);
    }
    
    private static void initOracle() {
        CommandTerminal.print("");
        CommandTerminal.print("🧬 INITIALIZING QUANTUM ORACLE...");
        CommandTerminal.print("");
        
        oracle = new QuantumOracle();
        currentReality = new Timeline(5.0, 50.0, 1);
        
        CommandTerminal.printSuccess("✓ Oracle initialized");
        CommandTerminal.print("  Initial Reality: " + currentReality);
        CommandTerminal.print("");
        CommandTerminal.print("  3 Parallel Timelines Ready:");
        CommandTerminal.print("    - ALPHA: x86 Assembly (Deterministic)");
        CommandTerminal.print("    - BETA: Quantum Mechanics (Stochastic)");
        CommandTerminal.print("    - GAMMA: String Theory + Calculus (Heuristic)");
    }
    
    private static void consultOracle() {
        if (oracle == null || currentReality == null) {
            CommandTerminal.printError("Oracle not initialized. Use 'oracle init' first.");
            return;
        }
        
        CommandTerminal.print("");
        CommandTerminal.print("👁️ CONSULTING QUANTUM ORACLE...");
        
        currentReality = oracle.consult(currentReality);
        
        CommandTerminal.print("");
        CommandTerminal.printSuccess(">> NEW REALITY STATE: " + currentReality);
        
        if (!currentReality.log.isEmpty()) {
            CommandTerminal.print("");
            CommandTerminal.print("Recent Operations:");
            int start = Math.max(0, currentReality.log.size() - 3);
            for (int i = start; i < currentReality.log.size(); i++) {
                CommandTerminal.print("  " + currentReality.log.get(i));
            }
        }
    }
    
    private static void showOracleStatus() {
        if (oracle == null || currentReality == null) {
            CommandTerminal.printWarning("Oracle not initialized");
            return;
        }
        
        CommandTerminal.print("");
        CommandTerminal.print("╔════════════════════════════════════════════════════════╗");
        CommandTerminal.print("║           QUANTUM ORACLE STATUS                        ║");
        CommandTerminal.print("╚════════════════════════════════════════════════════════╝");
        CommandTerminal.print("");
        CommandTerminal.print("Current Reality State:");
        CommandTerminal.print("  Coherence: " + String.format("%.2f", currentReality.coherence));
        CommandTerminal.print("  Entropy: " + String.format("%.2f", currentReality.entropy));
        CommandTerminal.print("  Complexity: " + currentReality.complexity);
        CommandTerminal.print("  Active Dimensions: " + currentReality.activeDimensions);
        CommandTerminal.print("");
        CommandTerminal.print("CPU Registers (x86):");
        currentReality.cpuRegisters.forEach((reg, val) -> 
            CommandTerminal.print("  " + reg + ": " + val)
        );
        CommandTerminal.print("");
        CommandTerminal.print("Qubit State (Quantum):");
        CommandTerminal.print("  |0⟩ amplitude: " + String.format("%.4f", currentReality.qubitState[0]));
        CommandTerminal.print("  |1⟩ amplitude: " + String.format("%.4f", currentReality.qubitState[1]));
        CommandTerminal.print("");
        CommandTerminal.print("Phi-Harmonic Resonance: " + String.format("%.2f", getResonance(currentReality)));
        CommandTerminal.print("");
        CommandTerminal.print("Operation Log (" + currentReality.log.size() + " entries):");
        int start = Math.max(0, currentReality.log.size() - 5);
        for (int i = start; i < currentReality.log.size(); i++) {
            CommandTerminal.print("  " + currentReality.log.get(i));
        }
    }
    
    private static void runOracleCycles(int cycles) {
        if (oracle == null || currentReality == null) {
            CommandTerminal.printError("Oracle not initialized. Use 'oracle init' first.");
            return;
        }
        
        CommandTerminal.print("");
        CommandTerminal.print("🧬 RUNNING " + cycles + " ORACLE CYCLES...");
        CommandTerminal.print("");
        
        for (int i = 1; i <= cycles; i++) {
            CommandTerminal.print("--- CYCLE " + i + " ---");
            currentReality = oracle.consult(currentReality);
            CommandTerminal.printSuccess(">> NEW REALITY STATE: " + currentReality);
            CommandTerminal.print("");
        }
        
        CommandTerminal.print("╔════════════════════════════════════════════════════════╗");
        CommandTerminal.print("║           EVOLUTION COMPLETE                           ║");
        CommandTerminal.print("╚════════════════════════════════════════════════════════╝");
        CommandTerminal.print("");
        CommandTerminal.print("Final State:");
        CommandTerminal.print("  Coherence: " + String.format("%.2f", currentReality.coherence));
        CommandTerminal.print("  Entropy: " + String.format("%.2f", currentReality.entropy));
        CommandTerminal.print("  Complexity: " + currentReality.complexity);
        CommandTerminal.print("  Dimensions: " + currentReality.activeDimensions);
        CommandTerminal.print("  Resonance: " + String.format("%.2f", getResonance(currentReality)));
    }
    
    private static void resetOracle() {
        if (oracle != null) {
            oracle.shutdown();
        }
        oracle = null;
        currentReality = null;
        
        CommandTerminal.printSuccess("✓ Oracle reset");
    }
    
    private static double getResonance(Timeline t) {
        return (t.coherence * 1.618) + (t.entropy > 0 ? (100.0 / t.entropy) : 0);
    }
    
    private static void showOracleHelp() {
        CommandTerminal.print("");
        CommandTerminal.print("╔════════════════════════════════════════════════════════╗");
        CommandTerminal.print("║         QUANTUM ORACLE - MULTI-TIMELINE SIMULATOR      ║");
        CommandTerminal.print("╚════════════════════════════════════════════════════════╝");
        CommandTerminal.print("");
        CommandTerminal.print("The Oracle simulates 3 parallel timelines:");
        CommandTerminal.print("  - ALPHA: x86 Assembly operations (MOV, ADD, XOR, INC)");
        CommandTerminal.print("  - BETA: Quantum gates (Pauli-X, Hadamard, CNOT)");
        CommandTerminal.print("  - GAMMA: String Theory branes + Calculus optimization");
        CommandTerminal.print("");
        CommandTerminal.print("Each timeline evolves independently, then collapses to");
        CommandTerminal.print("the highest phi-harmonic resonance state (φ = 1.618).");
        CommandTerminal.print("");
        CommandTerminal.print("Commands:");
        CommandTerminal.print("  oracle init         Initialize the Oracle");
        CommandTerminal.print("  oracle consult      Run one consultation cycle");
        CommandTerminal.print("  oracle run <n>      Run N cycles (default: 5)");
        CommandTerminal.print("  oracle status       Show current reality state");
        CommandTerminal.print("  oracle reset        Reset the Oracle");
        CommandTerminal.print("");
        CommandTerminal.print("Resonance Formula:");
        CommandTerminal.print("  R = (Coherence × φ) + (100 / Entropy)");
        CommandTerminal.print("");
        CommandTerminal.print("Knowledge Domains:");
        CommandTerminal.print("  - x86 Assembly (learned from ASM_x86_CORE)");
        CommandTerminal.print("  - Quantum Information (learned from QUANTUM_INFO_CORE)");
        CommandTerminal.print("  - String Theory (learned from STRING_THEORY_CORE)");
        CommandTerminal.print("  - Calculus II (learned from CALCULUS_II_CORE)");
    }
}
