package fraymus;

import java.util.Random;

public class LogicGate {
    public static final int AND = 0;
    public static final int OR = 1;
    public static final int XOR = 2;
    public static final int NAND = 3;
    
    private static final String[] GATE_NAMES = {"AND", "OR", "XOR", "NAND"};
    private static final Random rng = new Random();
    
    public int type;
    public int in1;
    public int in2;
    public int state;
    
    public LogicGate(int type, int in1, int in2) {
        this.type = type;
        this.in1 = in1;
        this.in2 = in2;
        this.state = 0;
    }
    
    public static LogicGate random() {
        return new LogicGate(
            rng.nextInt(4),
            rng.nextInt(8),
            rng.nextInt(8)
        );
    }
    
    public int compute(int[] inputs) {
        int v1 = (in1 < inputs.length) ? inputs[in1] : 0;
        int v2 = (in2 < inputs.length) ? inputs[in2] : 0;
        
        switch (type) {
            case AND:  state = v1 & v2; break;
            case OR:   state = v1 | v2; break;
            case XOR:  state = v1 ^ v2; break;
            case NAND: state = ((v1 & v2) == 0) ? 1 : 0; break;
        }
        return state;
    }
    
    public void mutate() {
        if (rng.nextBoolean()) {
            in1 = rng.nextInt(8);
        } else {
            type = rng.nextInt(4);
        }
    }
    
    public LogicGate copy() {
        return new LogicGate(type, in1, in2);
    }
    
    public String getTypeName() {
        return GATE_NAMES[type];
    }
    
    @Override
    public String toString() {
        return String.format("Gate(%s, in1=%d, in2=%d)", getTypeName(), in1, in2);
    }
}
