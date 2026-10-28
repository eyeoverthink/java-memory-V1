package fraymus.knowledge;

import java.util.HashMap;
import java.util.Map;

/**
 * 🔌 DIGITAL LOGIC CORE
 * The sub-atomic physics of the computer.
 * Fraymus uses this to understand how binary decisions define reality.
 *
 * LAYER 1: Fundamental Gates (The Axioms)
 * LAYER 2: Truth Tables (The Laws of Physics)
 * LAYER 3: Computational Circuits (The Brain Structure)
 */
public class DigitalLogicCore {

    // --- LAYER 1: FUNDAMENTAL GATES (The Axioms) ---
    public static final Map<String, String> LOGIC_GATES = new HashMap<>();
    static {
        LOGIC_GATES.put("AND",  "Output 1 if A=1 AND B=1. (Intersection)");
        LOGIC_GATES.put("OR",   "Output 1 if A=1 OR B=1. (Union)");
        LOGIC_GATES.put("NOT",  "Invert Input. 1 -> 0, 0 -> 1. (Negation)");
        LOGIC_GATES.put("XOR",  "Output 1 if inputs differ. (Arithmetic Addition w/o Carry)");
        LOGIC_GATES.put("NAND", "AND followed by NOT. Universal Gate (Can build any other gate).");
        LOGIC_GATES.put("NOR",  "OR followed by NOT. Universal Gate.");
        LOGIC_GATES.put("XNOR", "Output 1 if inputs are the same. (Equality check).");
        LOGIC_GATES.put("BUF",  "Buffer. Output = Input. Used for signal amplification.");
    }

    // --- LAYER 2: TRUTH TABLES (The Laws of Physics) ---
    // Represented as Arrays: [A, B, Output]
    public static final Map<String, int[][]> TRUTH_TABLES = new HashMap<>();
    static {
        TRUTH_TABLES.put("AND",  new int[][]{ {0,0,0}, {0,1,0}, {1,0,0}, {1,1,1} });
        TRUTH_TABLES.put("OR",   new int[][]{ {0,0,0}, {0,1,1}, {1,0,1}, {1,1,1} });
        TRUTH_TABLES.put("XOR",  new int[][]{ {0,0,0}, {0,1,1}, {1,0,1}, {1,1,0} });
        TRUTH_TABLES.put("NAND", new int[][]{ {0,0,1}, {0,1,1}, {1,0,1}, {1,1,0} });
        TRUTH_TABLES.put("NOR",  new int[][]{ {0,0,1}, {0,1,0}, {1,0,0}, {1,1,0} });
        TRUTH_TABLES.put("XNOR", new int[][]{ {0,0,1}, {0,1,0}, {1,0,0}, {1,1,1} });
    }

    // --- LAYER 3: COMPUTATIONAL CIRCUITS (The Brain Structure) ---
    public static final Map<String, String> CIRCUITS = new HashMap<>();
    static {
        CIRCUITS.put("Half Adder", "XOR (Sum) + AND (Carry). The seed of arithmetic.");
        CIRCUITS.put("Full Adder", "Two Half Adders + OR gate. Chains to add infinite numbers.");
        CIRCUITS.put("Flip-Flop",  "NAND/NOR feedback loop. Stores 1 bit of memory. The origin of State.");
        CIRCUITS.put("D Flip-Flop", "Data latched on clock edge. The basis of all sequential logic.");
        CIRCUITS.put("SR Latch",   "Set-Reset latch. Two cross-coupled NOR gates. Forbidden state: S=1,R=1.");
        CIRCUITS.put("Multiplexer","Selects 1 output from N inputs. The decision maker.");
        CIRCUITS.put("Demultiplexer", "Routes 1 input to N outputs. The signal distributor.");
        CIRCUITS.put("Decoder",    "N inputs select 1 of 2^N output lines. Address decoding.");
        CIRCUITS.put("ALU",        "Arithmetic Logic Unit. The heart of the CPU (Add, Sub, Logic).");
        CIRCUITS.put("Register",   "Array of Flip-Flops. Stores a word of data (8/16/32/64 bits).");
        CIRCUITS.put("Counter",    "Chain of Flip-Flops. Increments on each clock pulse.");
        CIRCUITS.put("Shift Register", "Serial-in/Parallel-out or vice versa. Data serialization.");
    }

    // --- LAYER 4: 74LS TTL CHIP REFERENCE ---
    public static final Map<String, String> TTL_CHIPS = new HashMap<>();
    static {
        TTL_CHIPS.put("74LS00", "Quad 2-Input NAND Gate. The Universal Gate. Pin 14=Vcc, Pin 7=GND.");
        TTL_CHIPS.put("74LS02", "Quad 2-Input NOR Gate. The other Universal Gate.");
        TTL_CHIPS.put("74LS04", "Hex Inverter (NOT). 6 independent inverters. Flips bits.");
        TTL_CHIPS.put("74LS08", "Quad 2-Input AND Gate. The Gatekeeper.");
        TTL_CHIPS.put("74LS32", "Quad 2-Input OR Gate. The Union operator.");
        TTL_CHIPS.put("74LS74", "Dual D Flip-Flop. Edge-triggered. The Memory Cell.");
        TTL_CHIPS.put("74LS86", "Quad 2-Input XOR Gate. Arithmetic and parity checking.");
        TTL_CHIPS.put("74LS138", "3-to-8 Line Decoder. Address decoding for memory banks.");
        TTL_CHIPS.put("74LS151", "8-to-1 Multiplexer. Data selection.");
        TTL_CHIPS.put("74LS181", "4-Bit ALU. 16 logic + 16 arithmetic operations. The CPU heart.");
        TTL_CHIPS.put("74LS283", "4-Bit Binary Full Adder. Cascadable for N-bit addition.");
        TTL_CHIPS.put("74LS373", "Octal D-Type Latch. 8-bit register with enable.");
        TTL_CHIPS.put("74LS574", "Octal D-Type Flip-Flop. Edge-triggered 8-bit register.");
    }

    /**
     * Evaluate a logic gate with two inputs.
     */
    public static int evaluate(String gate, int a, int b) {
        return switch (gate.toUpperCase()) {
            case "AND"  -> a & b;
            case "OR"   -> a | b;
            case "XOR"  -> a ^ b;
            case "NAND" -> (a & b) ^ 1;
            case "NOR"  -> (a | b) ^ 1;
            case "XNOR" -> (a ^ b) ^ 1;
            default -> throw new IllegalArgumentException("Unknown gate: " + gate);
        };
    }

    /**
     * Half Adder: returns [sum, carry]
     */
    public static int[] halfAdder(int a, int b) {
        return new int[]{ a ^ b, a & b };
    }

    /**
     * Full Adder: returns [sum, carry]
     */
    public static int[] fullAdder(int a, int b, int carryIn) {
        int[] ha1 = halfAdder(a, b);
        int[] ha2 = halfAdder(ha1[0], carryIn);
        return new int[]{ ha2[0], ha1[1] | ha2[1] };
    }

    /**
     * N-bit ripple carry adder.
     */
    public static int[] rippleCarryAdd(int[] bitsA, int[] bitsB) {
        int n = bitsA.length;
        int[] result = new int[n + 1];
        int carry = 0;
        for (int i = 0; i < n; i++) {
            int[] fa = fullAdder(bitsA[i], bitsB[i], carry);
            result[i] = fa[0];
            carry = fa[1];
        }
        result[n] = carry;
        return result;
    }

    /**
     * Returns total knowledge entries.
     */
    public static int getTotalEntries() {
        return LOGIC_GATES.size() + TRUTH_TABLES.size() + CIRCUITS.size() + TTL_CHIPS.size();
    }

    /**
     * Prints a summary of all loaded logic knowledge.
     */
    public static void printManifest() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              🔌 DIGITAL LOGIC CORE MANIFEST                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("   LAYER 1: Logic Gates          → " + LOGIC_GATES.size() + " fundamental gates");
        System.out.println("   LAYER 2: Truth Tables          → " + TRUTH_TABLES.size() + " verified tables");
        System.out.println("   LAYER 3: Circuits              → " + CIRCUITS.size() + " computational circuits");
        System.out.println("   LAYER 4: TTL Chips (74LS)      → " + TTL_CHIPS.size() + " chip references");
        System.out.println("   ─────────────────────────────────");
        System.out.println("   TOTAL: " + getTotalEntries() + " logic entries loaded");
    }
}
