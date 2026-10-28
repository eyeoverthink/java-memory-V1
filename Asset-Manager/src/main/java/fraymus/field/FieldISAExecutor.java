package fraymus.field;

import java.util.HashMap;
import java.util.Map;

/**
 * Field-Based ISA Executor
 * 
 * Executes F-ISA instructions on field registers and control registers.
 * Implements the continuous field computation model.
 */
public class FieldISAExecutor {
    
    // Field registers (F0-F15): state s(t) ∈ [-1.0, +1.0]
    private final double[] fieldRegisters;
    
    // Control registers (C0-C7)
    private final double[] controlRegisters;
    
    // Status flags (S0-S3)
    private final boolean[] statusFlags;
    
    // Program counter
    private long programCounter;
    
    // Stack pointer
    private long stackPointer;
    
    // Threshold for collapse (C2)
    private static final double DEFAULT_THRESHOLD = 0.5;
    
    // Activation functions
    public enum ActivationFunction {
        SIGMOID,
        TANH,
        RELU,
        STEP
    }
    
    public FieldISAExecutor() {
        this.fieldRegisters = new double[16];
        this.controlRegisters = new double[8];
        this.statusFlags = new boolean[4];
        this.programCounter = 0;
        this.stackPointer = 0;
        
        // Initialize control registers
        controlRegisters[2] = DEFAULT_THRESHOLD; // Collapse threshold
        controlRegisters[3] = 0.5; // Superposition coefficient
        controlRegisters[4] = 0.99; // Decay rate
        controlRegisters[5] = 0.1; // Momentum
        controlRegisters[6] = 0.0; // Noise
        controlRegisters[7] = 0.0; // Mode (0 = continuous, 1 = discrete)
        
        // Initialize field registers to zero (balanced tension)
        for (int i = 0; i < 16; i++) {
            fieldRegisters[i] = 0.0;
        }
    }
    
    /**
     * Execute a single instruction
     */
    public void execute(Instruction instr) {
        switch (instr.getOpcode()) {
            // Load/Store
            case Instruction.FLDI:
                executeFLDI(instr);
                break;
            case Instruction.FLDC:
                executeFLDC(instr);
                break;
            
            // Arithmetic
            case Instruction.FADD:
                executeFADD(instr);
                break;
            case Instruction.FSUB:
                executeFSUB(instr);
                break;
            case Instruction.FMUL:
                executeFMUL(instr);
                break;
            case Instruction.FDIV:
                executeFDIV(instr);
                break;
            case Instruction.FPOW:
                executeFPOW(instr);
                break;
            
            // Ternary Logic
            case Instruction.FNOT:
                executeFNOT(instr);
                break;
            case Instruction.FAND:
                executeFAND(instr);
                break;
            case Instruction.FOR:
                executeFOR(instr);
                break;
            case Instruction.FNEG:
                executeFNEG(instr);
                break;
            case Instruction.FABS:
                executeFABS(instr);
                break;
            
            // Dynamics
            case Instruction.FDEC:
                executeFDEC(instr);
                break;
            case Instruction.FMOM:
                executeFMOM(instr);
                break;
            case Instruction.FNOI:
                executeFNOI(instr);
                break;
            
            // Superposition
            case Instruction.FSUP:
                executeFSUP(instr);
                break;
            case Instruction.FCOL:
                executeFCOL(instr);
                break;
            case Instruction.FEXP:
                executeFEXP(instr);
                break;
            
            // Neural Operations
            case Instruction.FSYN:
                executeFSYN(instr);
                break;
            case Instruction.FWEI:
                executeFWEI(instr);
                break;
            case Instruction.FACT:
                executeFACT(instr);
                break;
            
            // Control Flow
            case Instruction.JMP:
                executeJMP(instr);
                break;
            case Instruction.JCOL:
                executeJCOL(instr);
                break;
            case Instruction.JSUP:
                executeJSUP(instr);
                break;
            case Instruction.JOVF:
                executeJOVF(instr);
                break;
            case Instruction.RET:
                executeRET(instr);
                break;
            
            default:
                throw new IllegalArgumentException("Unknown opcode: " + instr.getOpcode());
        }
        
        updateStatusFlags();
    }
    
    // ========== Load/Store ==========
    
    private void executeFLDI(Instruction instr) {
        int rd = instr.getRd();
        double value = instr.getImmediateAsDouble();
        fieldRegisters[rd] = clamp(value);
    }
    
    private void executeFLDC(Instruction instr) {
        int rd = instr.getRd();
        int cn = (int) instr.getImmediate(); // Control register number
        if (cn >= 0 && cn < 8) {
            fieldRegisters[rd] = clamp(controlRegisters[cn]);
        }
    }
    
    // ========== Arithmetic ==========
    
    private void executeFADD(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] += fieldRegisters[rs];
        clamp(rd);
    }
    
    private void executeFSUB(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] -= fieldRegisters[rs];
        clamp(rd);
    }
    
    private void executeFMUL(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] *= fieldRegisters[rs];
        clamp(rd);
    }
    
    private void executeFDIV(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        if (fieldRegisters[rs] != 0) {
            fieldRegisters[rd] /= fieldRegisters[rs];
        }
        clamp(rd);
    }
    
    private void executeFPOW(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] = Math.pow(fieldRegisters[rd], fieldRegisters[rs]);
        clamp(rd);
    }
    
    // ========== Ternary Logic ==========
    
    private void executeFNOT(Instruction instr) {
        int rd = instr.getRd();
        fieldRegisters[rd] = -fieldRegisters[rd];
    }
    
    private void executeFAND(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] = ternaryAnd(fieldRegisters[rd], fieldRegisters[rs]);
    }
    
    private void executeFOR(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        fieldRegisters[rd] = ternaryOr(fieldRegisters[rd], fieldRegisters[rs]);
    }
    
    private void executeFNEG(Instruction instr) {
        int rd = instr.getRd();
        fieldRegisters[rd] = -fieldRegisters[rd];
    }
    
    private void executeFABS(Instruction instr) {
        int rd = instr.getRd();
        fieldRegisters[rd] = Math.abs(fieldRegisters[rd]);
    }
    
    // ========== Dynamics ==========
    
    private void executeFDEC(Instruction instr) {
        int rd = instr.getRd();
        double decay = instr.getImmediateAsDouble();
        fieldRegisters[rd] *= decay;
    }
    
    private void executeFMOM(Instruction instr) {
        int rd = instr.getRd();
        double momentum = instr.getImmediateAsDouble();
        // Simple momentum: blend current with previous (simplified)
        fieldRegisters[rd] = fieldRegisters[rd] * (1 - momentum) + fieldRegisters[rd] * momentum;
    }
    
    private void executeFNOI(Instruction instr) {
        int rd = instr.getRd();
        double noise = instr.getImmediateAsDouble();
        fieldRegisters[rd] += (Math.random() - 0.5) * noise;
        clamp(rd);
    }
    
    // ========== Superposition ==========
    
    private void executeFSUP(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        double alpha = controlRegisters[3]; // Superposition coefficient
        fieldRegisters[rd] = alpha * fieldRegisters[rd] + (1 - alpha) * fieldRegisters[rs];
        clamp(rd);
    }
    
    private void executeFCOL(Instruction instr) {
        int rd = instr.getRd();
        double threshold = instr.getImmediateAsDouble();
        fieldRegisters[rd] = collapse(fieldRegisters[rd], threshold);
    }
    
    private void executeFEXP(Instruction instr) {
        int rd = instr.getRd();
        double epsilon = 0.01;
        fieldRegisters[rd] = fieldRegisters[rd] * (1 + epsilon);
        clamp(rd);
    }
    
    // ========== Neural Operations ==========
    
    private void executeFSYN(Instruction instr) {
        int rd = instr.getRd();
        int rs = instr.getRs();
        double weight = instr.getImmediateAsDouble();
        fieldRegisters[rd] += weight * fieldRegisters[rs];
        clamp(rd);
    }
    
    private void executeFWEI(Instruction instr) {
        int rd = instr.getRd();
        double weight = instr.getImmediateAsDouble();
        fieldRegisters[rd] *= weight;
        clamp(rd);
    }
    
    private void executeFACT(Instruction instr) {
        int rd = instr.getRd();
        // Default to sigmoid activation
        fieldRegisters[rd] = sigmoid(fieldRegisters[rd]);
    }
    
    // ========== Control Flow ==========
    
    private void executeJMP(Instruction instr) {
        programCounter = instr.getImmediate();
    }
    
    private void executeJCOL(Instruction instr) {
        if (statusFlags[0] || statusFlags[1]) { // Collapsed to +1 or -1
            programCounter = instr.getImmediate();
        }
    }
    
    private void executeJSUP(Instruction instr) {
        if (statusFlags[2]) { // In superposition
            programCounter = instr.getImmediate();
        }
    }
    
    private void executeJOVF(Instruction instr) {
        if (statusFlags[3]) { // Overflow
            programCounter = instr.getImmediate();
        }
    }
    
    private void executeRET(Instruction instr) {
        // Simplified: just set return value
        // In full implementation, would pop from stack
    }
    
    // ========== Helper Methods ==========
    
    private void clamp(int reg) {
        fieldRegisters[reg] = clamp(fieldRegisters[reg]);
    }
    
    private double clamp(double value) {
        return Math.max(-1.0, Math.min(1.0, value));
    }
    
    private void updateStatusFlags() {
        // Check all field registers for status
        boolean hasCollapsedPlus = false;
        boolean hasCollapsedMinus = false;
        boolean hasSuperposition = false;
        boolean hasOverflow = false;
        
        double threshold = controlRegisters[2];
        
        for (int i = 0; i < 16; i++) {
            double value = fieldRegisters[i];
            if (value > threshold) {
                hasCollapsedPlus = true;
            } else if (value < -threshold) {
                hasCollapsedMinus = true;
            } else {
                hasSuperposition = true;
            }
            
            if (Math.abs(value) > 1.0) {
                hasOverflow = true;
            }
        }
        
        statusFlags[0] = hasCollapsedPlus;
        statusFlags[1] = hasCollapsedMinus;
        statusFlags[2] = hasSuperposition;
        statusFlags[3] = hasOverflow;
    }
    
    // Ternary logic gates
    private double ternaryAnd(double a, double b) {
        if (a > 0.5 && b > 0.5) return 1.0;
        if (a < -0.5 || b < -0.5) return -1.0;
        return 0.0;
    }
    
    private double ternaryOr(double a, double b) {
        if (a > 0.5 || b > 0.5) return 1.0;
        if (a < -0.5 && b < -0.5) return -1.0;
        return 0.0;
    }
    
    // Collapse function
    private int collapse(double state, double threshold) {
        if (state > threshold) return 1;
        if (state < -threshold) return -1;
        return 0;
    }
    
    // Activation functions
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    private double tanh(double x) {
        return Math.tanh(x);
    }
    
    private double relu(double x) {
        return Math.max(0, x);
    }
    
    private double step(double x) {
        return x >= 0 ? 1.0 : 0.0;
    }
    
    // ========== Getters ==========
    
    public double getFieldRegister(int reg) {
        if (reg < 0 || reg > 15) {
            throw new IllegalArgumentException("Register must be 0-15");
        }
        return fieldRegisters[reg];
    }
    
    public void setFieldRegister(int reg, double value) {
        if (reg < 0 || reg > 15) {
            throw new IllegalArgumentException("Register must be 0-15");
        }
        fieldRegisters[reg] = clamp(value);
    }
    
    public double[] getAllFieldRegisters() {
        return fieldRegisters.clone();
    }
    
    public boolean[] getStatusFlags() {
        return statusFlags.clone();
    }
    
    public long getProgramCounter() {
        return programCounter;
    }
    
    public void setProgramCounter(long pc) {
        this.programCounter = pc;
    }
    
    public void reset() {
        for (int i = 0; i < 16; i++) {
            fieldRegisters[i] = 0.0;
        }
        programCounter = 0;
        stackPointer = 0;
        for (int i = 0; i < 4; i++) {
            statusFlags[i] = false;
        }
    }
}
