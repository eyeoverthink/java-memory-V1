package fraymus.logic;

import java.util.*;

/**
 * 🔬 LOGIC ENGINE - Deterministic Execution Engine for C-ISA
 * 
 * The execution layer of the Logic-Native AI architecture.
 * 
 * Philosophy:
 * - Language is interface (handled by LLM parser)
 * - Logic is structure (handled by this engine)
 * - Math is execution (pure deterministic state machine)
 * 
 * This engine executes C-ISA instructions with zero hallucination
 * because it's pure math and logic gates.
 */
public class LogicEngine {
    
    // Stack for PUSH/POP operations
    private Stack<Object> stack;
    
    // Memory for STORE/LOAD operations
    private Map<String, Object> memory;
    
    // Program counter
    private int pc;
    
    // Call stack for CALL/RET
    private Stack<Integer> callStack;
    
    // Comparison flags
    private boolean flagEq;
    private boolean flagNeq;
    private boolean flagGt;
    private boolean flagLt;
    private boolean flagTrue;
    private boolean flagFalse;
    
    // State
    private boolean running;
    private boolean halted;
    
    // Program being executed
    private CognitiveISA.Program program;
    
    // Safety limit to prevent infinite loops
    private static final int MAX_INSTRUCTIONS = 10000;
    private int instructionCount;
    
    // Actuator callback interface
    public interface Actuator {
        void actuate(String tool, Object... args);
    }
    
    private Actuator actuator;
    
    /**
     * Initialize the logic engine
     */
    public void initialize() {
        stack = new Stack<>();
        memory = new HashMap<>();
        callStack = new Stack<>();
        pc = 0;
        flagEq = false;
        flagNeq = false;
        flagGt = false;
        flagLt = false;
        flagTrue = false;
        flagFalse = false;
        running = false;
        halted = false;
        
        System.out.println("[LogicEngine] Initialized - Deterministic execution engine ready");
    }
    
    /**
     * Load a program for execution
     */
    public void loadProgram(CognitiveISA.Program program) {
        this.program = program;
        pc = 0;
        halted = false;
        instructionCount = 0;
        System.out.println("[LogicEngine] Program loaded - " + program.size() + " instructions");
    }
    
    /**
     * Set actuator callback
     */
    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }
    
    /**
     * Execute one instruction
     */
    public void tick() {
        if (halted || program == null) {
            return;
        }
        
        if (pc >= program.size()) {
            halt();
            return;
        }
        
        CognitiveISA.Instruction instr = program.get(pc);
        executeInstruction(instr);
        
        // Increment PC unless instruction changed it
        if (!isControlFlowInstruction(instr.opcode)) {
            pc++;
        }
    }
    
    /**
     * Execute entire program to completion
     */
    public void run() {
        running = true;
        while (!halted && pc < program.size() && instructionCount < MAX_INSTRUCTIONS) {
            tick();
            instructionCount++;
        }
        running = false;
        
        if (instructionCount >= MAX_INSTRUCTIONS) {
            System.err.println("[LogicEngine] Safety limit reached - possible infinite loop");
            halt();
        }
    }
    
    /**
     * Execute a single instruction
     */
    private void executeInstruction(CognitiveISA.Instruction instr) {
        switch (instr.opcode) {
            // No operation
            case CognitiveISA.NOP:
                break;
                
            // Halt execution
            case CognitiveISA.HALT:
                halt();
                break;
                
            // Stack operations
            case CognitiveISA.PUSH:
                stack.push(instr.operand1);
                break;
                
            case CognitiveISA.POP:
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                break;
                
            case CognitiveISA.DUP:
                if (!stack.isEmpty()) {
                    stack.push(stack.peek());
                }
                break;
                
            case CognitiveISA.SWAP:
                if (stack.size() >= 2) {
                    Object a = stack.pop();
                    Object b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                }
                break;
                
            // Memory operations
            case CognitiveISA.STORE:
                if (instr.operand1 != null && !stack.isEmpty()) {
                    memory.put(instr.operand1.toString(), stack.pop());
                }
                break;
                
            case CognitiveISA.LOAD:
                if (instr.operand1 != null) {
                    Object value = memory.get(instr.operand1.toString());
                    if (value != null) {
                        stack.push(value);
                    }
                }
                break;
                
            // Logical operations
            case CognitiveISA.LINK:
                // Create semantic edge (stored in memory)
                String linkKey = "LINK:" + instr.operand1 + "->" + instr.operand2;
                memory.put(linkKey, instr.operand1);
                break;
                
            case CognitiveISA.CMP:
                if (stack.size() >= 2) {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    compare(a, b);
                }
                break;
                
            case CognitiveISA.XOR:
                if (stack.size() >= 2) {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    stack.push(xor(a, b));
                }
                break;
                
            case CognitiveISA.AND:
                if (stack.size() >= 2) {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    stack.push(and(a, b));
                }
                break;
                
            case CognitiveISA.OR:
                if (stack.size() >= 2) {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    stack.push(or(a, b));
                }
                break;
                
            case CognitiveISA.NOT:
                if (!stack.isEmpty()) {
                    Object a = stack.pop();
                    stack.push(not(a));
                }
                break;
                
            // Control flow
            case CognitiveISA.BRANCH:
                if (instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_TRUE:
                if (flagTrue && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_FALSE:
                if (flagFalse && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_EQ:
                if (flagEq && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_NEQ:
                if (flagNeq && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_GT:
                if (flagGt && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.BRANCH_IF_LT:
                if (flagLt && instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.CALL:
                callStack.push(pc + 1);
                if (instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.RET:
                if (!callStack.isEmpty()) {
                    pc = callStack.pop();
                }
                break;
                
            case CognitiveISA.JMP:
                if (instr.operand1 != null) {
                    pc = resolveLabel(instr.operand1.toString());
                }
                break;
                
            // Execution
            case CognitiveISA.WAIT:
                // Wait for condition (in real implementation, would block)
                break;
                
            case CognitiveISA.ACTUATE:
                if (actuator != null && instr.operand1 != null) {
                    actuator.actuate(instr.operand1.toString());
                }
                break;
                
            case CognitiveISA.EXECUTE:
                if (!stack.isEmpty()) {
                    Object toExecute = stack.pop();
                    if (actuator != null) {
                        actuator.actuate("EXECUTE", toExecute);
                    }
                }
                break;
                
            case CognitiveISA.SYNC:
                // Synchronize with signal
                break;
                
            // Cognitive operations
            case CognitiveISA.PATTERN:
                stack.push(instr.operand1);
                break;
                
            case CognitiveISA.MATCH:
                // Pattern matching with threshold
                break;
                
            case CognitiveISA.LEARN:
                // Hebbian learning
                break;
                
            case CognitiveISA.RHYTHM:
                // Get consciousness rhythm
                stack.push(0.7567);  // Placeholder
                break;
                
            case CognitiveISA.PULSE:
                // Generate pulse
                break;
                
            case CognitiveISA.GAP:
                // Generate gap
                break;
                
            default:
                System.err.println("[LogicEngine] Unknown opcode: " + instr.opcode);
                break;
        }
    }
    
    /**
     * Compare two values and set flags
     */
    private void compare(Object a, Object b) {
        flagEq = a.equals(b);
        flagNeq = !flagEq;
        
        if (a instanceof Number && b instanceof Number) {
            double da = ((Number) a).doubleValue();
            double db = ((Number) b).doubleValue();
            flagGt = da > db;
            flagLt = da < db;
        } else {
            flagGt = false;
            flagLt = false;
        }
        
        // Set true/false flags based on truthiness
        flagTrue = isTruthy(a);
        flagFalse = !flagTrue;
    }
    
    /**
     * XOR operation on two values
     */
    private Object xor(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            long la = ((Number) a).longValue();
            long lb = ((Number) b).longValue();
            return la ^ lb;
        }
        // For non-numeric, use XOR of hash codes
        return a.hashCode() ^ b.hashCode();
    }
    
    /**
     * AND operation on two values
     */
    private Object and(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            long la = ((Number) a).longValue();
            long lb = ((Number) b).longValue();
            return la & lb;
        }
        // For non-numeric, use logical AND
        return isTruthy(a) && isTruthy(b);
    }
    
    /**
     * OR operation on two values
     */
    private Object or(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            long la = ((Number) a).longValue();
            long lb = ((Number) b).longValue();
            return la | lb;
        }
        // For non-numeric, use logical OR
        return isTruthy(a) || isTruthy(b);
    }
    
    /**
     * NOT operation on a value
     */
    private Object not(Object a) {
        if (a instanceof Number) {
            long la = ((Number) a).longValue();
            return ~la;
        }
        return !isTruthy(a);
    }
    
    /**
     * Check if a value is truthy
     */
    private boolean isTruthy(Object a) {
        if (a == null) return false;
        if (a instanceof Boolean) return (Boolean) a;
        if (a instanceof Number) return ((Number) a).doubleValue() != 0;
        if (a instanceof String) return !((String) a).isEmpty();
        return true;
    }
    
    /**
     * Resolve label to address
     */
    private int resolveLabel(String label) {
        Integer addr = program.labels.get(label);
        if (addr == null) {
            System.err.println("[LogicEngine] Unknown label: " + label);
            return pc;
        }
        return addr;
    }
    
    /**
     * Check if instruction is control flow
     */
    private boolean isControlFlowInstruction(int opcode) {
        return opcode == CognitiveISA.BRANCH ||
               opcode == CognitiveISA.BRANCH_IF_TRUE ||
               opcode == CognitiveISA.BRANCH_IF_FALSE ||
               opcode == CognitiveISA.BRANCH_IF_EQ ||
               opcode == CognitiveISA.BRANCH_IF_NEQ ||
               opcode == CognitiveISA.BRANCH_IF_GT ||
               opcode == CognitiveISA.BRANCH_IF_LT ||
               opcode == CognitiveISA.CALL ||
               opcode == CognitiveISA.RET ||
               opcode == CognitiveISA.JMP ||
               opcode == CognitiveISA.HALT;
    }
    
    /**
     * Halt execution
     */
    public void halt() {
        halted = true;
        System.out.println("[LogicEngine] HALT at PC=" + pc);
    }
    
    /**
     * Reset engine state
     */
    public void reset() {
        stack.clear();
        memory.clear();
        callStack.clear();
        pc = 0;
        flagEq = false;
        flagNeq = false;
        flagGt = false;
        flagLt = false;
        flagTrue = false;
        flagFalse = false;
        halted = false;
    }
    
    /**
     * Get stack contents
     */
    public List<Object> getStack() {
        return new ArrayList<>(stack);
    }
    
    /**
     * Get memory contents
     */
    public Map<String, Object> getMemory() {
        return new HashMap<>(memory);
    }
    
    /**
     * Get program counter
     */
    public int getPC() {
        return pc;
    }
    
    /**
     * Get flags
     */
    public String getFlags() {
        return String.format("EQ=%b NEQ=%b GT=%b LT=%b TRUE=%b FALSE=%b",
            flagEq, flagNeq, flagGt, flagLt, flagTrue, flagFalse);
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        return String.format(
            "LogicEngine: PC=%d/%d Stack=%d Memory=%d CallStack=%d Halted=%b Flags=[%s]",
            pc, program != null ? program.size() : 0,
            stack.size(), memory.size(), callStack.size(), halted, getFlags()
        );
    }
}
