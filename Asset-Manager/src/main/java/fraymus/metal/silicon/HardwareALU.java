package fraymus.metal.silicon;

/**
 * FRAYMUS HARDWARE ALU (74LS181 / 74LS283 Simulation)
 * "The final cheat is removed. We do not use math. We use gates."
 *
 * BANNED OPERATORS: +, -, *, /
 * ALLOWED OPERATORS: ^ (XOR), & (AND), | (OR), ~ (NOT), << >> (SHIFTS)
 *
 * This class cascades virtual transistors to perform arithmetic.
 * Every mathematical operation is decomposed into pure logic gate operations,
 * exactly as physical silicon implements them.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class HardwareALU {

    // --- LOGIC GATE PRIMITIVES (The 74LS Series) ---

    // 74LS86: Quad 2-Input Exclusive-OR (XOR) Gate
    private static int gateXOR(int a, int b) { return a ^ b; }

    // 74LS08: Quad 2-Input AND Gate
    private static int gateAND(int a, int b) { return a & b; }

    // 74LS32: Quad 2-Input OR Gate
    private static int gateOR(int a, int b) { return a | b; }

    // 74LS04: Hex Inverter (NOT) Gate
    private static int gateNOT(int a) { return ~a; }

    /**
     * THE FULL ADDER (Cascaded 32-bit)
     * Simulates the propagation delay of electrons moving through logic gates.
     * Computes Addition without EVER using the '+' symbol.
     */
    public static int executeAdd(int a, int b) {
        int sum, carry;

        // The electrons propagate until the carry buffer is empty
        do {
            // 1. Calculate the raw sum ignoring carries (XOR Gate)
            sum = gateXOR(a, b);

            // 2. Calculate where the carries occur (AND Gate), then shift them left
            carry = gateAND(a, b) << 1;

            // 3. Feed the results back into the inputs for the next logic tier
            a = sum;
            b = carry;

        } while (b != 0); // Loop terminates when physical propagation finishes

        return a;
    }

    /**
     * THE SUBTRACTOR
     * Computers cannot subtract. They can only add negative numbers.
     * We use the 74LS04 Inverter to create the 2's Complement, then Add.
     */
    public static int executeSub(int a, int b) {
        // 2's Complement: Invert the bits (~B) and Add 1
        int invertedB = gateNOT(b);

        // Execute (A) + (-B) using gate-level addition
        int twosComplementB = executeAdd(invertedB, 1);

        return executeAdd(a, twosComplementB);
    }

    /**
     * THE MULTIPLIER (Shift-and-Add)
     * Simulates hardware multiplication circuits.
     * Uses only shifts and gate-level addition.
     */
    public static int executeMultiply(int a, int b) {
        int result = 0;
        boolean negative = false;

        // Handle negative multipliers
        if (b < 0) {
            b = executeSub(0, b);
            negative = !negative;
        }
        if (a < 0) {
            a = executeSub(0, a);
            negative = !negative;
        }

        while (b != 0) {
            // If the lowest bit of B is 1, add A to the result
            if ((b & 1) != 0) {
                result = executeAdd(result, a);
            }
            // Shift A left (multiply by 2)
            a <<= 1;
            // Shift B right (divide by 2)
            b >>>= 1;
        }

        return negative ? executeSub(0, result) : result;
    }

    /**
     * THE DIVIDER (Shift-and-Subtract)
     * Long division implemented with gate-level subtraction.
     */
    public static int executeDivide(int a, int b) {
        if (b == 0) return 0; // Division by zero protection

        boolean negative = false;
        if (a < 0) { a = executeSub(0, a); negative = !negative; }
        if (b < 0) { b = executeSub(0, b); negative = !negative; }

        int quotient = 0;
        int remainder = 0;

        for (int i = 31; i >= 0; i = executeSub(i, 1)) {
            remainder <<= 1;
            remainder = gateOR(remainder, (a >>> i) & 1);

            if (remainder >= b) {
                remainder = executeSub(remainder, b);
                quotient = gateOR(quotient, 1 << i);
            }

            if (i == 0) break; // Prevent underflow on the loop counter
        }

        return negative ? executeSub(0, quotient) : quotient;
    }

    /**
     * COMPARISON (Gate-level)
     * Returns: 0 if equal, negative if a < b, positive if a > b
     */
    public static int executeCompare(int a, int b) {
        return executeSub(a, b);
    }

    // --- DIAGNOSTIC TEST ---
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  74LS181 HARDWARE ALU INITIALIZED");
        System.out.println("  BANNED: + - * /   ALLOWED: ^ & | ~ << >>");
        System.out.println("==================================================");

        int val1 = 150;
        int val2 = 42;

        System.out.println("\nTesting Gate-Level Addition (150 + 42):");
        int sum = executeAdd(val1, val2);
        System.out.println("  -> Result: " + sum + (sum == 192 ? " [VALID]" : " [FRACTURE]"));

        System.out.println("Testing Gate-Level Subtraction (150 - 42):");
        int diff = executeSub(val1, val2);
        System.out.println("  -> Result: " + diff + (diff == 108 ? " [VALID]" : " [FRACTURE]"));

        System.out.println("Testing Gate-Level Multiplication (150 * 42):");
        int prod = executeMultiply(val1, val2);
        System.out.println("  -> Result: " + prod + (prod == 6300 ? " [VALID]" : " [FRACTURE]"));

        System.out.println("Testing Gate-Level Division (150 / 42):");
        int quot = executeDivide(val1, val2);
        System.out.println("  -> Result: " + quot + (quot == 3 ? " [VALID]" : " [FRACTURE]"));

        System.out.println("Testing Gate-Level Compare (150 vs 42):");
        int cmp = executeCompare(val1, val2);
        System.out.println("  -> Result: " + cmp + (cmp > 0 ? " [A > B]" : cmp < 0 ? " [A < B]" : " [A == B]"));
    }
}
