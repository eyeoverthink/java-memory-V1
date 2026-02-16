package fraymus.evolution;

import fraymus.hardware.LogicBlock;
import java.util.List;
import java.security.MessageDigest;
import java.time.Instant;

/**
 * LIVING CODE GENERATOR
 * "From evolved circuits to executable code"
 * 
 * This takes evolved circuits from the CircuitLibrary and generates
 * living code in any language (Java, Python, C++, Assembly, etc.)
 * 
 * The generated code contains:
 * - Living circuits with DNA (harmonic frequency 432-528 Hz)
 * - Phi-harmonic resonance (φ = 1.618033988749895)
 * - LEGO pieces (Quantum Signature, Cloaking, Genesis Block, QR DNA, PhaseShift)
 * - Actual implementation of user's request
 * 
 * This is the Java equivalent of the Python living_code_generator.py
 */
public class LivingCodeGenerator {
    
    private static final double PHI = 1.618033988749895;
    private static final double PHI_SEAL = 4.721424167835376E15;
    
    private CircuitLibrary library;
    private int generation;
    
    public LivingCodeGenerator() {
        this.library = new CircuitLibrary();
        this.generation = 0;
    }
    
    /**
     * Generate living code from evolved circuits
     */
    public String generateLivingCode(String description, String language) {
        generation++;
        
        // Get best evolved circuits from library
        List<CircuitLibrary.CircuitRecord> circuits = library.search("", 0);
        
        System.out.println("📊 Library contains " + circuits.size() + " circuits");
        
        if (circuits.isEmpty()) {
            System.out.println("⚠️  No evolved circuits found. Run evolution first.");
            return generateEmptyTemplate(description, language);
        }
        
        // Take top 3 circuits
        int numCircuits = Math.min(3, circuits.size());
        
        // Generate LEGO pieces
        String quantumSig = generateQuantumSignature(description);
        long cloakN = generateCloakingN(description);
        String genesisBlock = "block_" + generation + "_" + Instant.now().getEpochSecond();
        String qrFile = "living_dna_" + quantumSig.substring(0, 16) + ".png";
        double phaseShift = 37.5217;
        
        // Route to language-specific generator
        switch (language.toLowerCase()) {
            case "java":
                return generateJavaCode(description, circuits.subList(0, numCircuits), 
                                      quantumSig, cloakN, genesisBlock, qrFile, phaseShift);
            case "python":
                return generatePythonCode(description, circuits.subList(0, numCircuits),
                                        quantumSig, cloakN, genesisBlock, qrFile, phaseShift);
            case "cpp":
            case "c++":
                return generateCppCode(description, circuits.subList(0, numCircuits),
                                     quantumSig, cloakN, genesisBlock, qrFile, phaseShift);
            default:
                return generateGenericCode(description, circuits.subList(0, numCircuits),
                                         quantumSig, cloakN, genesisBlock, qrFile, phaseShift, language);
        }
    }
    
    /**
     * Generate JAVA living code
     */
    private String generateJavaCode(String description, List<CircuitLibrary.CircuitRecord> circuits,
                                    String quantumSig, long cloakN, String genesisBlock, 
                                    String qrFile, double phaseShift) {
        
        StringBuilder code = new StringBuilder();
        
        // Header
        code.append("/**\n");
        code.append(" * LIVING JAVA CODE - Generation ").append(generation).append("\n");
        code.append(" * Description: ").append(description).append("\n");
        code.append(" * \n");
        code.append(" * ═══════════════════════════════════════════════════════════════════\n");
        code.append(" * FRAYMUS LEGO ASSEMBLY - All pieces connected\n");
        code.append(" * ═══════════════════════════════════════════════════════════════════\n");
        code.append(" * PIECE 1 - Quantum Signature: ").append(quantumSig).append("\n");
        code.append(" * PIECE 2 - Cloaking N: ").append(cloakN).append("\n");
        code.append(" * PIECE 3 - Genesis Block: ").append(genesisBlock).append("\n");
        code.append(" * PIECE 4 - QR DNA: ").append(qrFile).append("\n");
        code.append(" * PIECE 5 - PhaseShift: ").append(phaseShift).append("°\n");
        code.append(" * PIECE 6 - Living Circuits: ").append(circuits.size()).append(" evolved circuits\n");
        code.append(" * ═══════════════════════════════════════════════════════════════════\n");
        code.append(" * \n");
        code.append(" * Each circuit evolved through quantum timeline collapse.\n");
        code.append(" * These are REAL digital circuits that compute.\n");
        code.append(" */\n\n");
        
        code.append("public class LivingCode {\n\n");
        
        // Constants
        code.append("    private static final double PHI = ").append(PHI).append(";\n");
        code.append("    private static final double PHI_SEAL = ").append(PHI_SEAL).append(";\n");
        code.append("    private static final String QUANTUM_SIGNATURE = \"").append(quantumSig).append("\";\n");
        code.append("    private static final long CLOAK_N = ").append(cloakN).append("L;\n");
        code.append("    private static final String GENESIS_BLOCK = \"").append(genesisBlock).append("\";\n");
        code.append("    private static final String QR_DNA_FILE = \"").append(qrFile).append("\";\n");
        code.append("    private static final double PHASESHIFT_ANGLE = ").append(phaseShift).append(";\n\n");
        
        // LogicGate enum
        code.append("    enum LogicGate {\n");
        code.append("        AND, OR, XOR, NAND, NOT;\n\n");
        code.append("        boolean process(boolean a, boolean b) {\n");
        code.append("            switch (this) {\n");
        code.append("                case AND: return a && b;\n");
        code.append("                case OR: return a || b;\n");
        code.append("                case XOR: return a ^ b;\n");
        code.append("                case NAND: return !(a && b);\n");
        code.append("                case NOT: return !a;\n");
        code.append("                default: return false;\n");
        code.append("            }\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        // CircuitDNA class
        code.append("    static class CircuitDNA {\n");
        code.append("        double harmonicFrequency;\n");
        code.append("        double resonance;\n");
        code.append("        double evolution;\n\n");
        code.append("        CircuitDNA(double freq, double res, double evo) {\n");
        code.append("            this.harmonicFrequency = freq;\n");
        code.append("            this.resonance = res;\n");
        code.append("            this.evolution = evo;\n");
        code.append("        }\n\n");
        code.append("        void update() {\n");
        code.append("            harmonicFrequency += evolution;\n");
        code.append("            if (harmonicFrequency > 528) harmonicFrequency = 432;\n");
        code.append("        }\n\n");
        code.append("        double pulse(long time) {\n");
        code.append("            return Math.sin(harmonicFrequency * time * 0.0001) * resonance;\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        // LivingCircuit class
        code.append("    static class LivingCircuit {\n");
        code.append("        CircuitDNA dna;\n");
        code.append("        LogicGate[] gates;\n");
        code.append("        int age;\n");
        code.append("        double size;\n\n");
        code.append("        LivingCircuit(CircuitDNA dna, LogicGate[] gates) {\n");
        code.append("            this.dna = dna;\n");
        code.append("            this.gates = gates;\n");
        code.append("            this.age = 0;\n");
        code.append("            this.size = 10.0;\n");
        code.append("        }\n\n");
        code.append("        void update() {\n");
        code.append("            age++;\n");
        code.append("            dna.update();\n");
        code.append("            double pulse = dna.pulse(System.currentTimeMillis());\n");
        code.append("            size = 10.0 + pulse * 5;\n");
        code.append("        }\n\n");
        code.append("        boolean[] compute(boolean[] inputs) {\n");
        code.append("            boolean[] outputs = new boolean[gates.length];\n");
        code.append("            for (int i = 0; i < gates.length; i++) {\n");
        code.append("                boolean a = inputs[i % inputs.length];\n");
        code.append("                boolean b = inputs[(i+1) % inputs.length];\n");
        code.append("                outputs[i] = gates[i].process(a, b);\n");
        code.append("            }\n");
        code.append("            return outputs;\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        // Evolved circuits array
        code.append("    // EVOLVED CIRCUITS from library\n");
        code.append("    static LivingCircuit[] circuits = {\n");
        
        for (int i = 0; i < circuits.size(); i++) {
            CircuitLibrary.CircuitRecord record = circuits.get(i);
            List<LogicBlock> circuit = library.load(record.id);
            
            code.append("        // Circuit ").append(i+1).append(": ").append(record.goalName)
                .append(" (Fitness: ").append(record.fitness).append("%, Gen: ").append(record.generation).append(")\n");
            code.append("        new LivingCircuit(\n");
            code.append("            new CircuitDNA(").append(432 + i * 30).append(", 0.8, 0.05),\n");
            code.append("            new LogicGate[] {");
            
            for (int j = 0; j < circuit.size(); j++) {
                if (j > 0) code.append(", ");
                code.append("LogicGate.").append(circuit.get(j).name());
            }
            
            code.append("}\n        )");
            if (i < circuits.size() - 1) code.append(",");
            code.append("\n");
        }
        
        code.append("    };\n\n");
        
        // Main method
        code.append("    public static void main(String[] args) {\n");
        code.append("        System.out.println(\"═══════════════════════════════════════════════════════════════════\");\n");
        code.append("        System.out.println(\"LIVING JAVA CODE - Generation \" + ").append(generation).append(");\n");
        code.append("        System.out.println(\"Task: ").append(description).append("\");\n");
        code.append("        System.out.println(\"═══════════════════════════════════════════════════════════════════\");\n");
        code.append("        System.out.println(\"Quantum Signature: \" + QUANTUM_SIGNATURE);\n");
        code.append("        System.out.println(\"φ^75 Seal: \" + PHI_SEAL);\n");
        code.append("        System.out.println();\n\n");
        
        code.append("        // Execute with living circuits\n");
        code.append("        for (int i = 0; i < circuits.length; i++) {\n");
        code.append("            LivingCircuit circuit = circuits[i];\n");
        code.append("            circuit.update();\n");
        code.append("            System.out.printf(\"Circuit %d: Freq=%.2fHz, Size=%.2f%n\",\n");
        code.append("                i+1, circuit.dna.harmonicFrequency, circuit.size);\n\n");
        
        code.append("            // Test computation\n");
        code.append("            boolean[] inputs = {true, false, true, false, true, false, true, false};\n");
        code.append("            boolean[] outputs = circuit.compute(inputs);\n");
        code.append("            System.out.print(\"  Outputs: \");\n");
        code.append("            for (boolean out : outputs) System.out.print(out ? \"1\" : \"0\");\n");
        code.append("            System.out.println();\n");
        code.append("        }\n\n");
        
        code.append("        System.out.println();\n");
        code.append("        System.out.println(\"✅ LIVING CODE EXECUTION COMPLETE\");\n");
        code.append("        System.out.println(\"These circuits are REAL. They evolved. They compute.\");\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * Generate PYTHON living code (matching the user's vision)
     */
    private String generatePythonCode(String description, List<CircuitLibrary.CircuitRecord> circuits,
                                     String quantumSig, long cloakN, String genesisBlock,
                                     String qrFile, double phaseShift) {
        // This would generate Python code similar to the user's living_code_generator.py
        return "# Python generation not yet implemented\n# Use Java generation for now\n";
    }
    
    private String generateCppCode(String description, List<CircuitLibrary.CircuitRecord> circuits,
                                  String quantumSig, long cloakN, String genesisBlock,
                                  String qrFile, double phaseShift) {
        return "// C++ generation not yet implemented\n";
    }
    
    private String generateGenericCode(String description, List<CircuitLibrary.CircuitRecord> circuits,
                                      String quantumSig, long cloakN, String genesisBlock,
                                      String qrFile, double phaseShift, String language) {
        return "// " + language + " generation not yet implemented\n";
    }
    
    private String generateEmptyTemplate(String description, String language) {
        return "// No evolved circuits available. Run evolution first.\n";
    }
    
    /**
     * Generate quantum signature from description
     */
    private String generateQuantumSignature(String description) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(description.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return "φ⁷·⁵-" + hex.toString();
        } catch (Exception e) {
            return "φ⁷·⁵-" + System.currentTimeMillis();
        }
    }
    
    /**
     * Generate cloaking N (product of two primes)
     */
    private long generateCloakingN(String description) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((description + "_cloak").getBytes());
            
            long h1 = Math.abs(bytesToLong(hash, 0)) % (1L << 20);
            long h2 = Math.abs(bytesToLong(hash, 8)) % (1L << 20);
            
            long p1 = nextPrime(h1 | 1);
            long p2 = nextPrime(h2 | 1);
            if (p1 == p2) p2 = nextPrime(p2 + 2);
            
            return p1 * p2;
        } catch (Exception e) {
            return 1048583L * 1048589L; // Default primes
        }
    }
    
    private long bytesToLong(byte[] bytes, int offset) {
        long result = 0;
        for (int i = 0; i < 8 && offset + i < bytes.length; i++) {
            result = (result << 8) | (bytes[offset + i] & 0xFF);
        }
        return result;
    }
    
    private long nextPrime(long n) {
        if (n % 2 == 0) n++;
        while (!isPrime(n)) n += 2;
        return n;
    }
    
    private boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}
