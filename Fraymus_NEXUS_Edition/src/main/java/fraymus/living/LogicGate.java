package fraymus.living;

import java.util.Random;

/**
 * LOGIC GATE - Atomic Computing Unit
 * 
 * Single logic gate (AND/OR/XOR/NAND) with mutation capability.
 */
public class LogicGate {
    
    public static final int AND = 0;
    public static final int OR = 1;
    public static final int XOR = 2;
    public static final int NAND = 3;
    
    private int type;
    private int input1;
    private int input2;
    private boolean output;
    private Random random = new Random();
    
    public LogicGate(int type, int input1, int input2) {
        this.type = type;
        this.input1 = input1;
        this.input2 = input2;
    }
    
    /**
     * Random gate
     */
    public LogicGate() {
        this.type = random.nextInt(4);
        this.input1 = random.nextInt(8);
        this.input2 = random.nextInt(8);
    }
    
    /**
     * Compute gate output
     */
    public boolean compute(boolean[] inputs) {
        boolean a = inputs[input1];
        boolean b = inputs[input2];
        
        switch (type) {
            case AND:
                output = a && b;
                break;
            case OR:
                output = a || b;
                break;
            case XOR:
                output = a ^ b;
                break;
            case NAND:
                output = !(a && b);
                break;
        }
        
        return output;
    }
    
    /**
     * Mutate gate structure
     */
    public void mutate() {
        if (random.nextDouble() < 0.1) {
            type = random.nextInt(4);
        }
        if (random.nextDouble() < 0.1) {
            input1 = random.nextInt(8);
        }
        if (random.nextDouble() < 0.1) {
            input2 = random.nextInt(8);
        }
    }
    
    /**
     * Clone gate
     */
    public LogicGate clone() {
        return new LogicGate(type, input1, input2);
    }
    
    public String getTypeName() {
        switch (type) {
            case AND: return "AND";
            case OR: return "OR";
            case XOR: return "XOR";
            case NAND: return "NAND";
            default: return "UNKNOWN";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s(%d,%d)", getTypeName(), input1, input2);
    }
}
