package fraymus.field;

/**
 * Field-Based Instruction Set Architecture (F-ISA) Instruction
 * 
 * Represents a single field-based instruction with opcode, registers, and immediate/operand.
 * Encoding: [Opcode: 8 bits][Rd: 4 bits][Rs: 4 bits][Imm/Addr: 48 bits]
 */
public class Instruction {
    
    // Opcode categories
    public static final int OPCODE_LOAD_STORE = 0x00;
    public static final int OPCODE_ARITHMETIC = 0x10;
    public static final int OPCODE_TERNARY_LOGIC = 0x20;
    public static final int OPCODE_DYNAMICS = 0x30;
    public static final int OPCODE_SUPERPOSITION = 0x40;
    public static final int OPCODE_NEURAL = 0x50;
    public static final int OPCODE_CONTROL = 0x60;
    public static final int OPCODE_MEMORY = 0x70;
    
    // Specific opcodes
    public static final int FLD = 0x00;   // Load field from memory
    public static final int FST = 0x01;   // Store field to memory
    public static final int FLDI = 0x02;  // Load immediate field value
    public static final int FLDC = 0x03;  // Load from control register
    
    public static final int FADD = 0x10;  // Field addition
    public static final int FSUB = 0x11;  // Field subtraction
    public static final int FMUL = 0x12;  // Field multiplication
    public static final int FDIV = 0x13;  // Field division
    public static final int FPOW = 0x14;  // Field exponentiation
    
    public static final int FNOT = 0x20;  // Ternary NOT
    public static final int FAND = 0x21;  // Ternary AND
    public static final int FOR = 0x22;   // Ternary OR
    public static final int FNEG = 0x23;  // Negation
    public static final int FABS = 0x24;  // Absolute value
    
    public static final int FDEC = 0x30;  // Field decay
    public static final int FMOM = 0x31;  // Field momentum
    public static final int FNOI = 0x32;  // Field noise
    public static final int FINT = 0x33;  // Integrate
    public static final int FDIF = 0x34;  // Differentiate
    
    public static final int FSUP = 0x40;  // Superposition
    public static final int FCOL = 0x41;  // Collapse
    public static final int FEXP = 0x42;  // Expand
    
    public static final int FSYN = 0x50;  // Synaptic integration
    public static final int FWEI = 0x51;  // Weight application
    public static final int FACT = 0x52;  // Activation function
    public static final int FTHD = 0x53;  // Threshold
    
    public static final int JMP = 0x60;   // Jump
    public static final int JCOL = 0x61;  // Jump if collapsed
    public static final int JSUP = 0x62;  // Jump if superposition
    public static final int JOVF = 0x63;  // Jump if overflow
    public static final int CALL = 0x64;  // Call subroutine
    public static final int RET = 0x65;   // Return
    
    public static final int FATT = 0x70;  // Load attractor
    public static final int FSTO = 0x71;  // Store attractor
    public static final int FCMP = 0x72;  // Compare fields
    
    private final int opcode;
    private final int rd;  // Destination register (0-15)
    private final int rs;  // Source register (0-15)
    private final long immediate;  // 48-bit immediate value or address
    
    public Instruction(int opcode, int rd, int rs, long immediate) {
        if (opcode < 0 || opcode > 0xFF) {
            throw new IllegalArgumentException("Opcode must be 8-bit (0-255)");
        }
        if (rd < 0 || rd > 15) {
            throw new IllegalArgumentException("Rd must be 4-bit (0-15)");
        }
        if (rs < 0 || rs > 15) {
            throw new IllegalArgumentException("Rs must be 4-bit (0-15)");
        }
        if (immediate < 0 || immediate > 0xFFFFFFFFFFFFL) {
            throw new IllegalArgumentException("Immediate must be 48-bit (0-2^48-1)");
        }
        
        this.opcode = opcode;
        this.rd = rd;
        this.rs = rs;
        this.immediate = immediate;
    }
    
    public Instruction(int opcode, int rd, int rs) {
        this(opcode, rd, rs, 0);
    }
    
    public Instruction(int opcode, int rd, double immediateValue) {
        this(opcode, rd, 0, Double.doubleToLongBits(immediateValue) & 0xFFFFFFFFFFFFL);
    }
    
    public int getOpcode() {
        return opcode;
    }
    
    public int getRd() {
        return rd;
    }
    
    public int getRs() {
        return rs;
    }
    
    public long getImmediate() {
        return immediate;
    }
    
    public double getImmediateAsDouble() {
        return Double.longBitsToDouble(immediate);
    }
    
    /**
     * Encode instruction to 8-byte array
     * Format: [Opcode: 8][Rd: 4][Rs: 4][Imm: 48]
     */
    public byte[] encode() {
        byte[] encoded = new byte[8];
        
        // Byte 0: Opcode
        encoded[0] = (byte) opcode;
        
        // Byte 1: Rd (high nibble) + Rs (low nibble)
        encoded[1] = (byte) ((rd << 4) | rs);
        
        // Bytes 2-7: Immediate (48 bits, big-endian)
        for (int i = 0; i < 6; i++) {
            encoded[2 + i] = (byte) ((immediate >> (40 - i * 8)) & 0xFF);
        }
        
        return encoded;
    }
    
    /**
     * Decode instruction from 8-byte array
     */
    public static Instruction decode(byte[] encoded) {
        if (encoded.length != 8) {
            throw new IllegalArgumentException("Instruction must be 8 bytes");
        }
        
        int opcode = encoded[0] & 0xFF;
        int rd = (encoded[1] >> 4) & 0x0F;
        int rs = encoded[1] & 0x0F;
        
        long immediate = 0;
        for (int i = 0; i < 6; i++) {
            immediate = (immediate << 8) | (encoded[2 + i] & 0xFF);
        }
        
        return new Instruction(opcode, rd, rs, immediate);
    }
    
    public String getMnemonic() {
        switch (opcode) {
            case FLD: return "FLD";
            case FST: return "FST";
            case FLDI: return "FLDI";
            case FLDC: return "FLDC";
            case FADD: return "FADD";
            case FSUB: return "FSUB";
            case FMUL: return "FMUL";
            case FDIV: return "FDIV";
            case FPOW: return "FPOW";
            case FNOT: return "FNOT";
            case FAND: return "FAND";
            case FOR: return "FOR";
            case FNEG: return "FNEG";
            case FABS: return "FABS";
            case FDEC: return "FDEC";
            case FMOM: return "FMOM";
            case FNOI: return "FNOI";
            case FINT: return "FINT";
            case FDIF: return "FDIF";
            case FSUP: return "FSUP";
            case FCOL: return "FCOL";
            case FEXP: return "FEXP";
            case FSYN: return "FSYN";
            case FWEI: return "FWEI";
            case FACT: return "FACT";
            case FTHD: return "FTHD";
            case JMP: return "JMP";
            case JCOL: return "JCOL";
            case JSUP: return "JSUP";
            case JOVF: return "JOVF";
            case CALL: return "CALL";
            case RET: return "RET";
            case FATT: return "FATT";
            case FSTO: return "FSTO";
            case FCMP: return "FCMP";
            default: return "UNKNOWN";
        }
    }
    
    @Override
    public String toString() {
        String mnemonic = getMnemonic();
        
        if (opcode == RET || opcode == JMP) {
            return String.format("%s 0x%012X", mnemonic, immediate);
        }
        
        if (opcode == FLDI || opcode == FCOL || opcode == FDEC || opcode == FMOM) {
            return String.format("%s F%d, %.6f", mnemonic, rd, getImmediateAsDouble());
        }
        
        if (opcode == FLD || opcode == FST) {
            return String.format("%s F%d, [0x%012X]", mnemonic, rd, immediate);
        }
        
        if (rs == 0 && immediate == 0) {
            return String.format("%s F%d", mnemonic, rd);
        }
        
        return String.format("%s F%d, F%d", mnemonic, rd, rs);
    }
}
