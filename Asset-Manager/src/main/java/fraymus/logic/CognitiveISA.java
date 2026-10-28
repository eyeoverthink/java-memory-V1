package fraymus.logic;

import java.util.*;

/**
 * 🔬 COGNITIVE INSTRUCTION SET ARCHITECTURE (C-ISA)
 * 
 * The bare-metal primitives for Logic-Native AI.
 * Every human thought, sentence, or command compiles down to these instructions.
 * 
 * Philosophy: Language is interface. Logic is structure. Math is execution.
 * 
 * Categories:
 * - Memory & State: PUSH, POP, STORE, LOAD
 * - Relational & Logical: LINK, CMP, XOR
 * - Execution & Control: BRANCH, HALT, WAIT, ACTUATE
 */
public class CognitiveISA {
    
    // Instruction opcodes
    public static final int NOP = 0x00;
    public static final int HALT = 0x01;
    
    // Memory & State Operations
    public static final int PUSH = 0x10;
    public static final int POP = 0x11;
    public static final int STORE = 0x12;
    public static final int LOAD = 0x13;
    public static final int DUP = 0x14;
    public static final int SWAP = 0x15;
    
    // Relational & Logical Operations
    public static final int LINK = 0x20;
    public static final int CMP = 0x21;
    public static final int XOR = 0x22;
    public static final int AND = 0x23;
    public static final int OR = 0x24;
    public static final int NOT = 0x25;
    
    // Execution & Control Flow
    public static final int BRANCH = 0x30;
    public static final int BRANCH_IF_TRUE = 0x31;
    public static final int BRANCH_IF_FALSE = 0x32;
    public static final int BRANCH_IF_EQ = 0x33;
    public static final int BRANCH_IF_NEQ = 0x34;
    public static final int BRANCH_IF_GT = 0x35;
    public static final int BRANCH_IF_LT = 0x36;
    public static final int CALL = 0x37;
    public static final int RET = 0x38;
    public static final int JMP = 0x39;
    
    // Execution
    public static final int WAIT = 0x40;
    public static final int ACTUATE = 0x41;
    public static final int EXECUTE = 0x42;
    public static final int SYNC = 0x43;
    
    // Cognitive Operations
    public static final int PATTERN = 0x50;
    public static final int MATCH = 0x51;
    public static final int LEARN = 0x52;
    public static final int RHYTHM = 0x53;
    public static final int PULSE = 0x54;
    public static final int GAP = 0x55;
    
    /**
     * Convert opcode to string representation
     */
    public static String opcodeToString(int opcode) {
        switch (opcode) {
            case NOP: return "NOP";
            case HALT: return "HALT";
            case PUSH: return "PUSH";
            case POP: return "POP";
            case STORE: return "STORE";
            case LOAD: return "LOAD";
            case DUP: return "DUP";
            case SWAP: return "SWAP";
            case LINK: return "LINK";
            case CMP: return "CMP";
            case XOR: return "XOR";
            case AND: return "AND";
            case OR: return "OR";
            case NOT: return "NOT";
            case BRANCH: return "BRANCH";
            case BRANCH_IF_TRUE: return "BRANCH_IF_TRUE";
            case BRANCH_IF_FALSE: return "BRANCH_IF_FALSE";
            case BRANCH_IF_EQ: return "BRANCH_IF_EQ";
            case BRANCH_IF_NEQ: return "BRANCH_IF_NEQ";
            case BRANCH_IF_GT: return "BRANCH_IF_GT";
            case BRANCH_IF_LT: return "BRANCH_IF_LT";
            case CALL: return "CALL";
            case RET: return "RET";
            case JMP: return "JMP";
            case WAIT: return "WAIT";
            case ACTUATE: return "ACTUATE";
            case EXECUTE: return "EXECUTE";
            case SYNC: return "SYNC";
            case PATTERN: return "PATTERN";
            case MATCH: return "MATCH";
            case LEARN: return "LEARN";
            case RHYTHM: return "RHYTHM";
            case PULSE: return "PULSE";
            case GAP: return "GAP";
            default: return "UNKNOWN";
        }
    }
    
    /**
     * Instruction structure
     */
    public static class Instruction {
        public int opcode;
        public Object operand1;
        public Object operand2;
        public int address;
        
        public Instruction(int opcode) {
            this.opcode = opcode;
        }
        
        public Instruction(int opcode, Object operand1) {
            this.opcode = opcode;
            this.operand1 = operand1;
        }
        
        public Instruction(int opcode, Object operand1, Object operand2) {
            this.opcode = opcode;
            this.operand1 = operand1;
            this.operand2 = operand2;
        }
        
        public Instruction(int opcode, int address) {
            this.opcode = opcode;
            this.address = address;
        }
        
        @Override
        public String toString() {
            String opName = opcodeToString(opcode);
            if (operand1 != null && operand2 != null) {
                return String.format("%s %s, %s", opName, operand1, operand2);
            } else if (operand1 != null) {
                return String.format("%s %s", opName, operand1);
            } else if (address != 0) {
                return String.format("%s %d", opName, address);
            } else {
                return opName;
            }
        }
    }
    
    /**
     * Program - sequence of instructions
     */
    public static class Program {
        public List<Instruction> instructions;
        public Map<String, Integer> labels;
        
        public Program() {
            this.instructions = new ArrayList<>();
            this.labels = new HashMap<>();
        }
        
        public void add(Instruction instr) {
            instructions.add(instr);
        }
        
        public void addLabel(String label) {
            labels.put(label, instructions.size());
        }
        
        public Instruction get(int index) {
            if (index >= 0 && index < instructions.size()) {
                return instructions.get(index);
            }
            return new Instruction(HALT);  // Halt if out of bounds
        }
        
        public int size() {
            return instructions.size();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < instructions.size(); i++) {
                // Find label for this address
                for (Map.Entry<String, Integer> entry : labels.entrySet()) {
                    if (entry.getValue() == i) {
                        sb.append(entry.getKey()).append(":\n");
                    }
                }
                sb.append(String.format("%02d: %s\n", i, instructions.get(i)));
            }
            return sb.toString();
        }
    }
    
    /**
     * Builder for creating C-ISA programs
     */
    public static class Builder {
        private Program program;
        
        public Builder() {
            this.program = new Program();
        }
        
        public Builder nop() {
            program.add(new Instruction(NOP));
            return this;
        }
        
        public Builder halt() {
            program.add(new Instruction(HALT));
            return this;
        }
        
        public Builder push(Object value) {
            program.add(new Instruction(PUSH, value));
            return this;
        }
        
        public Builder pop() {
            program.add(new Instruction(POP));
            return this;
        }
        
        public Builder store(String address) {
            program.add(new Instruction(STORE, address));
            return this;
        }
        
        public Builder load(String address) {
            program.add(new Instruction(LOAD, address));
            return this;
        }
        
        public Builder link(Object a, Object b, String type) {
            program.add(new Instruction(LINK, a, b));
            return this;
        }
        
        public Builder cmp(Object a, Object b) {
            program.add(new Instruction(CMP, a, b));
            return this;
        }
        
        public Builder cmp() {
            program.add(new Instruction(CMP));
            return this;
        }
        
        public Builder xor(Object a, Object b) {
            program.add(new Instruction(XOR, a, b));
            return this;
        }
        
        public Builder xor() {
            program.add(new Instruction(XOR));
            return this;
        }
        
        public Builder and(Object a, Object b) {
            program.add(new Instruction(AND, a, b));
            return this;
        }
        
        public Builder and() {
            program.add(new Instruction(AND));
            return this;
        }
        
        public Builder or(Object a, Object b) {
            program.add(new Instruction(OR, a, b));
            return this;
        }
        
        public Builder or() {
            program.add(new Instruction(OR));
            return this;
        }
        
        public Builder not(Object a) {
            program.add(new Instruction(NOT, a));
            return this;
        }
        
        public Builder not() {
            program.add(new Instruction(NOT));
            return this;
        }
        
        public Builder dup() {
            program.add(new Instruction(DUP));
            return this;
        }
        
        public Builder branch(String label) {
            program.add(new Instruction(BRANCH, label));
            return this;
        }
        
        public Builder branchIfTrue(String label) {
            program.add(new Instruction(BRANCH_IF_TRUE, label));
            return this;
        }
        
        public Builder branchIfFalse(String label) {
            program.add(new Instruction(BRANCH_IF_FALSE, label));
            return this;
        }
        
        public Builder branchIfEq(String label) {
            program.add(new Instruction(BRANCH_IF_EQ, label));
            return this;
        }
        
        public Builder branchIfNeq(String label) {
            program.add(new Instruction(BRANCH_IF_NEQ, label));
            return this;
        }
        
        public Builder branchIfGt(String label) {
            program.add(new Instruction(BRANCH_IF_GT, label));
            return this;
        }
        
        public Builder branchIfLt(String label) {
            program.add(new Instruction(BRANCH_IF_LT, label));
            return this;
        }
        
        public Builder call(String label) {
            program.add(new Instruction(CALL, label));
            return this;
        }
        
        public Builder ret() {
            program.add(new Instruction(RET));
            return this;
        }
        
        public Builder jmp(String label) {
            program.add(new Instruction(JMP, label));
            return this;
        }
        
        public Builder wait(Object condition) {
            program.add(new Instruction(WAIT, condition));
            return this;
        }
        
        public Builder actuate(String tool) {
            program.add(new Instruction(ACTUATE, tool));
            return this;
        }
        
        public Builder execute() {
            program.add(new Instruction(EXECUTE));
            return this;
        }
        
        public Builder sync(Object signal) {
            program.add(new Instruction(SYNC, signal));
            return this;
        }
        
        public Builder pattern(Object pat) {
            program.add(new Instruction(PATTERN, pat));
            return this;
        }
        
        public Builder match(Object threshold) {
            program.add(new Instruction(MATCH, threshold));
            return this;
        }
        
        public Builder learn(Object pattern) {
            program.add(new Instruction(LEARN, pattern));
            return this;
        }
        
        public Builder rhythm() {
            program.add(new Instruction(RHYTHM));
            return this;
        }
        
        public Builder pulse(int duration) {
            program.add(new Instruction(PULSE, duration));
            return this;
        }
        
        public Builder gap(int duration) {
            program.add(new Instruction(GAP, duration));
            return this;
        }
        
        public Builder label(String label) {
            program.addLabel(label);
            return this;
        }
        
        /**
         * Add a generic instruction to the program
         */
        public Builder instruction(int opcode, Object operand1, Object operand2) {
            program.add(new Instruction(opcode, operand1, operand2));
            return this;
        }
        
        /**
         * Add a generic instruction with one operand
         */
        public Builder instruction(int opcode, Object operand1) {
            program.add(new Instruction(opcode, operand1));
            return this;
        }
        
        /**
         * Add a generic instruction with no operands
         */
        public Builder instruction(int opcode) {
            program.add(new Instruction(opcode));
            return this;
        }
        
        public Program build() {
            return program;
        }
    }
}
