package fraymus.knowledge;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * 📚 GENESIS DATABASE
 * The immutable knowledge core extracted from source texts.
 * Fraymus references this to simulate high-level reasoning.
 *
 * CORE 1: Assembly Language (Hardware Control)
 * CORE 2: Quantum Physics (Probabilistic Logic)
 * CORE 3: Calculus II (Optimization & Infinite Series)
 * CORE 4: String Theory (Hyper-Dimensional Topology)
 */
public class GenesisDatabase {

    // --- CORE 1: ASSEMBLY LANGUAGE (Hardware Control) ---
    // Source: Assembly Language for x86 Processors
    public static final Map<String, String> ASM_KNOWLEDGE = new HashMap<>();
    static {
        ASM_KNOWLEDGE.put("MOV", "Data Transfer: Dest = Source. Cycles: 1");
        ASM_KNOWLEDGE.put("XCHG", "Data Exchange: Temp=A; A=B; B=Temp. Cycles: 3");
        ASM_KNOWLEDGE.put("PUSH", "Stack Op: ESP-=4; [ESP]=Src");
        ASM_KNOWLEDGE.put("POP", "Stack Op: Dest=[ESP]; ESP+=4");
        ASM_KNOWLEDGE.put("LEA", "Address Calc: Load Effective Address (Pointer Arithmetic)");
        ASM_KNOWLEDGE.put("CMP", "Logic: Subtract Source from Dest, discard result, set FLAGS");
        ASM_KNOWLEDGE.put("JE", "Control Flow: Jump if Zero Flag (ZF) == 1");
        ASM_KNOWLEDGE.put("JNE", "Control Flow: Jump if Zero Flag (ZF) == 0");
        ASM_KNOWLEDGE.put("JMP", "Control Flow: Unconditional Jump to target address");
        ASM_KNOWLEDGE.put("CALL", "Control Flow: Push return address, jump to subroutine");
        ASM_KNOWLEDGE.put("RET", "Control Flow: Pop return address, jump back to caller");
        ASM_KNOWLEDGE.put("ADD", "Arithmetic: Dest = Dest + Source. Sets CF, OF, ZF, SF");
        ASM_KNOWLEDGE.put("SUB", "Arithmetic: Dest = Dest - Source. Sets CF, OF, ZF, SF");
        ASM_KNOWLEDGE.put("MUL", "Arithmetic: EDX:EAX = EAX * Source (unsigned)");
        ASM_KNOWLEDGE.put("DIV", "Arithmetic: EAX = EDX:EAX / Source, EDX = remainder (unsigned)");
        ASM_KNOWLEDGE.put("AND", "Bitwise: Dest = Dest & Source. Clears CF, OF");
        ASM_KNOWLEDGE.put("OR", "Bitwise: Dest = Dest | Source. Clears CF, OF");
        ASM_KNOWLEDGE.put("XOR", "Bitwise: Dest = Dest ^ Source. Clears CF, OF. XOR EAX,EAX = zero register");
        ASM_KNOWLEDGE.put("NOT", "Bitwise: Dest = ~Dest (One's complement). No flags affected");
        ASM_KNOWLEDGE.put("SHL", "Shift: Dest <<= count. Last bit shifted out goes to CF");
        ASM_KNOWLEDGE.put("SHR", "Shift: Dest >>= count (logical). Last bit shifted out goes to CF");
        ASM_KNOWLEDGE.put("INT 21h", "System Call: Interrupt Vector for DOS Services");
        ASM_KNOWLEDGE.put("INT 80h", "System Call: Linux Kernel Syscall Interface");
        ASM_KNOWLEDGE.put("SYSCALL", "System Call: 64-bit Linux/Windows fast system call");
        ASM_KNOWLEDGE.put("NOP", "No Operation: 1 byte, 1 cycle. Used for alignment and timing");
        ASM_KNOWLEDGE.put("HLT", "Halt: Stop CPU execution until next interrupt");
        ASM_KNOWLEDGE.put("REP MOVSB", "Block Transfer: Copy ECX bytes from ESI to EDI");
        ASM_KNOWLEDGE.put("LOOP", "Control Flow: Decrement ECX, jump if ECX != 0");
    }

    // --- CORE 2: QUANTUM PHYSICS (Probabilistic Logic) ---
    // Source: The Physics of Quantum Information
    // Gates represented as 2x2 Unitary Matrices (flattened: [a,b,c,d] = [[a,b],[c,d]])
    public static final Map<String, double[]> QUANTUM_GATES = new HashMap<>();
    static {
        QUANTUM_GATES.put("Pauli-X", new double[]{0, 1, 1, 0});                          // Bit Flip (NOT)
        QUANTUM_GATES.put("Pauli-Y", new double[]{0, -1, 1, 0});                         // Bit+Phase Flip (imaginary components omitted)
        QUANTUM_GATES.put("Pauli-Z", new double[]{1, 0, 0, -1});                         // Phase Flip
        QUANTUM_GATES.put("Hadamard", new double[]{0.7071, 0.7071, 0.7071, -0.7071});    // Superposition Creator
        QUANTUM_GATES.put("Phase-S", new double[]{1, 0, 0, 0});                          // S Gate (pi/2 rotation, imaginary component omitted)
        QUANTUM_GATES.put("T-Gate", new double[]{1, 0, 0, 0.7071});                      // T Gate (pi/4 rotation, imaginary component omitted)
        QUANTUM_GATES.put("Identity", new double[]{1, 0, 0, 1});                         // No-op gate
    }

    public static final List<String> QUANTUM_PROTOCOLS = Arrays.asList(
        "BB84 Key Distribution: Encode bits in rectilinear/diagonal bases.",
        "EPR Paradox: Entangled pairs remain correlated over arbitrary distances.",
        "Quantum Teleportation: Destroys state at A to recreate it at B using Bell Measurement.",
        "Shor's Algorithm: Factors integers in polylogarithmic time (O((log N)^3)).",
        "Grover's Algorithm: Searches unsorted database in O(sqrt(N)) time.",
        "Quantum Error Correction: Shor code uses 9 physical qubits per logical qubit.",
        "No-Cloning Theorem: Arbitrary unknown quantum states cannot be copied.",
        "Bell Inequality: CHSH bound S <= 2 (classical), S <= 2*sqrt(2) (quantum).",
        "Decoherence: Environment interaction collapses superposition. T2 time defines coherence window.",
        "Quantum Zeno Effect: Frequent measurement prevents state evolution."
    );

    // --- CORE 3: CALCULUS II (Optimization & Infinite Series) ---
    // Source: Calculus 2
    public static final Map<String, String> MATH_CONCEPTS = new HashMap<>();
    static {
        MATH_CONCEPTS.put("Taylor Series", "f(x) = sum( f^(n)(a)/n! * (x-a)^n )");
        MATH_CONCEPTS.put("Maclaurin Series", "Taylor series centered at a=0: f(x) = sum( f^(n)(0)/n! * x^n )");
        MATH_CONCEPTS.put("Integration by Parts", "integral(u dv) = uv - integral(v du)");
        MATH_CONCEPTS.put("L'Hopital's Rule", "lim(f(x)/g(x)) = lim(f'(x)/g'(x)) if 0/0 or inf/inf");
        MATH_CONCEPTS.put("Ratio Test", "L = lim |a_(n+1)/a_n|; Converges if L < 1");
        MATH_CONCEPTS.put("Root Test", "L = lim |a_n|^(1/n); Converges if L < 1");
        MATH_CONCEPTS.put("Comparison Test", "If 0 <= a_n <= b_n and sum(b_n) converges, then sum(a_n) converges");
        MATH_CONCEPTS.put("Dot Product", "A . B = |A||B|cos(theta) (Projection)");
        MATH_CONCEPTS.put("Cross Product", "A x B = |A||B|sin(theta) n_hat (Perpendicular vector)");
        MATH_CONCEPTS.put("Euler's Formula", "e^(ix) = cos(x) + i*sin(x)");
        MATH_CONCEPTS.put("Fundamental Theorem", "integral_a^b f'(x)dx = f(b) - f(a)");
        MATH_CONCEPTS.put("Chain Rule", "d/dx[f(g(x))] = f'(g(x)) * g'(x)");
        MATH_CONCEPTS.put("Power Series", "sum(c_n * (x-a)^n) with radius of convergence R");
        MATH_CONCEPTS.put("Fourier Series", "f(x) = a0/2 + sum(an*cos(nx) + bn*sin(nx))");
        MATH_CONCEPTS.put("Gradient", "nabla f = (df/dx, df/dy, df/dz) points in direction of steepest ascent");
    }

    // --- CORE 4: STRING THEORY (Hyper-Dimensional Topology) ---
    // Source: String Theory and M-Theory
    public static final Map<String, String> STRING_TOPOLOGY = new HashMap<>();
    static {
        STRING_TOPOLOGY.put("T-Duality", "Equivalence of physics on Radius R and 1/R (Large/Small are same).");
        STRING_TOPOLOGY.put("S-Duality", "Equivalence of Strong Coupling (g) and Weak Coupling (1/g).");
        STRING_TOPOLOGY.put("Calabi-Yau", "6D Compactified Manifold required for Supersymmetry.");
        STRING_TOPOLOGY.put("D-Branes", "Boundary conditions for open strings; can trap gauge fields.");
        STRING_TOPOLOGY.put("M-Theory", "11-Dimensional unification of the 5 Superstring theories.");
        STRING_TOPOLOGY.put("AdS/CFT", "Anti-de Sitter space / Conformal Field Theory correspondence. Holographic principle.");
        STRING_TOPOLOGY.put("Compactification", "Extra dimensions curled into sub-Planck-scale manifolds.");
        STRING_TOPOLOGY.put("Supersymmetry", "Every boson has a fermionic partner and vice versa. Doubles the particle spectrum.");
        STRING_TOPOLOGY.put("Brane Worlds", "Our 4D universe may be a 3-brane embedded in higher-dimensional bulk.");
        STRING_TOPOLOGY.put("Landscape", "10^500 possible vacuum states. The string theory landscape problem.");
    }

    /**
     * Returns all knowledge domains as a unified map for indexing.
     */
    public static Map<String, Map<String, String>> getAllDomains() {
        Map<String, Map<String, String>> domains = new HashMap<>();
        domains.put("ASM", ASM_KNOWLEDGE);
        domains.put("MATH", MATH_CONCEPTS);
        domains.put("STRING_THEORY", STRING_TOPOLOGY);
        // Quantum gates are double[] so we convert descriptions
        Map<String, String> quantumDescriptions = new HashMap<>();
        for (Map.Entry<String, double[]> e : QUANTUM_GATES.entrySet()) {
            double[] m = e.getValue();
            quantumDescriptions.put(e.getKey(), String.format("Gate Matrix: [[%.4f,%.4f],[%.4f,%.4f]]", m[0], m[1], m[2], m[3]));
        }
        domains.put("QUANTUM_GATES", quantumDescriptions);
        return domains;
    }

    /**
     * Returns total knowledge entries across all domains.
     */
    public static int getTotalEntries() {
        return ASM_KNOWLEDGE.size() + QUANTUM_GATES.size() + QUANTUM_PROTOCOLS.size() +
               MATH_CONCEPTS.size() + STRING_TOPOLOGY.size();
    }

    /**
     * Prints a summary of all loaded knowledge.
     */
    public static void printManifest() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              📚 GENESIS DATABASE MANIFEST                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("   CORE 1: Assembly Language    → " + ASM_KNOWLEDGE.size() + " instructions");
        System.out.println("   CORE 2: Quantum Physics      → " + QUANTUM_GATES.size() + " gates, " + QUANTUM_PROTOCOLS.size() + " protocols");
        System.out.println("   CORE 3: Calculus II          → " + MATH_CONCEPTS.size() + " concepts");
        System.out.println("   CORE 4: String Theory        → " + STRING_TOPOLOGY.size() + " topologies");
        System.out.println("   ─────────────────────────────────");
        System.out.println("   TOTAL: " + getTotalEntries() + " knowledge entries loaded");
    }
}
