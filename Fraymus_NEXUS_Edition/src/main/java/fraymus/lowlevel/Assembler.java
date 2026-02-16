package fraymus.lowlevel;

import java.util.ArrayList;
import java.util.List;

/**
 * THE ASSEMBLER
 * "Translates Human Intent into Machine Bytes."
 * 
 * This is the compiler for the Fraymus Virtual Machine.
 * It takes human-readable assembly language and converts it to bytecode.
 * 
 * SYNTAX:
 * SET R0 50    → MOV R0, 50
 * ADD R0       → ADD R0 to ACC
 * SUB R1       → SUB R1 from ACC
 * STORE 100    → Store ACC to RAM[100]
 * LOAD 100     → Load RAM[100] to ACC
 * PRINT        → Print ACC
 * END          → Halt
 * 
 * EXAMPLE:
 * "SET 0 50 SET 1 20 ADD 0 ADD 1 PRINT END"
 * 
 * Compiles to:
 * [0x10, 0x00, 0x32, 0x10, 0x01, 0x14, 0x20, 0x00, 0x20, 0x01, 0xFF, 0x00]
 * 
 * "Human intent becomes machine reality."
 */
public class Assembler {

    /**
     * Compile human-readable assembly to bytecode
     * 
     * @param sourceCode Assembly language string
     * @return Bytecode array ready for FVM execution
     */
    public static int[] compile(String sourceCode) {
        System.out.println("🔨 ASSEMBLER: Compiling source code");
        System.out.println("   Source: \"" + sourceCode + "\"");
        System.out.println();
        
        List<Integer> bytecode = new ArrayList<>();
        String[] tokens = sourceCode.trim().split("\\s+");
        
        int i = 0;
        while (i < tokens.length) {
            String token = tokens[i];
            
            try {
                switch (token.toUpperCase()) {
                    case "SET": // SET R0 10 → MOV R0, 10
                        bytecode.add(FraymusCPU.OP_MOV);
                        bytecode.add(Integer.parseInt(tokens[++i])); // Register index
                        bytecode.add(Integer.parseInt(tokens[++i])); // Value
                        break;
                    
                    case "MOV": // MOV R0 10 (alias for SET)
                        bytecode.add(FraymusCPU.OP_MOV);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "ADD": // ADD R0
                        bytecode.add(FraymusCPU.OP_ADD);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "SUB": // SUB R0
                        bytecode.add(FraymusCPU.OP_SUB);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "MUL": // MUL R0
                        bytecode.add(FraymusCPU.OP_MUL);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "DIV": // DIV R0
                        bytecode.add(FraymusCPU.OP_DIV);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "STORE": // STORE 100
                    case "STR":
                        bytecode.add(FraymusCPU.OP_STR);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "LOAD": // LOAD 100
                    case "LDR":
                        bytecode.add(FraymusCPU.OP_LDR);
                        bytecode.add(Integer.parseInt(tokens[++i]));
                        break;
                    
                    case "PRINT": // PRINT
                    case "PRT":
                        bytecode.add(FraymusCPU.OP_PRT);
                        break;
                    
                    case "END": // END
                    case "HALT":
                    case "HLT":
                        bytecode.add(FraymusCPU.OP_HLT);
                        break;
                    
                    default:
                        System.err.println("   ⚠️  Unknown instruction: " + token);
                }
                
            } catch (NumberFormatException e) {
                System.err.println("   ⚠️  Invalid number format: " + tokens[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("   ⚠️  Missing argument for: " + token);
            }
            
            i++;
        }
        
        // Convert List to int array
        int[] result = bytecode.stream().mapToInt(Integer::intValue).toArray();
        
        System.out.println("   Compiled " + tokens.length + " tokens → " + result.length + " bytes");
        System.out.print("   Bytecode: ");
        for (int b : result) {
            System.out.print(String.format("0x%02X ", b));
        }
        System.out.println();
        System.out.println();
        
        return result;
    }
    
    /**
     * Compile from multiple lines
     * 
     * @param lines Array of assembly lines
     * @return Bytecode array
     */
    public static int[] compileMultiline(String[] lines) {
        StringBuilder combined = new StringBuilder();
        for (String line : lines) {
            // Remove comments (anything after ;)
            int commentIndex = line.indexOf(';');
            if (commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }
            combined.append(line.trim()).append(" ");
        }
        return compile(combined.toString());
    }
    
    /**
     * Disassemble bytecode back to human-readable form
     * 
     * @param bytecode Bytecode array
     * @return Human-readable assembly
     */
    public static String disassemble(int[] bytecode) {
        StringBuilder asm = new StringBuilder();
        
        int i = 0;
        while (i < bytecode.length) {
            int opcode = bytecode[i++];
            
            switch (opcode) {
                case FraymusCPU.OP_MOV:
                    if (i + 1 < bytecode.length) {
                        asm.append("SET R").append(bytecode[i++])
                           .append(" ").append(bytecode[i++]).append("\n");
                    }
                    break;
                
                case FraymusCPU.OP_ADD:
                    if (i < bytecode.length) {
                        asm.append("ADD R").append(bytecode[i++]).append("\n");
                    }
                    break;
                
                case FraymusCPU.OP_SUB:
                    if (i < bytecode.length) {
                        asm.append("SUB R").append(bytecode[i++]).append("\n");
                    }
                    break;
                
                case FraymusCPU.OP_STR:
                    if (i < bytecode.length) {
                        asm.append("STORE ").append(bytecode[i++]).append("\n");
                    }
                    break;
                
                case FraymusCPU.OP_LDR:
                    if (i < bytecode.length) {
                        asm.append("LOAD ").append(bytecode[i++]).append("\n");
                    }
                    break;
                
                case FraymusCPU.OP_PRT:
                    asm.append("PRINT\n");
                    break;
                
                case FraymusCPU.OP_HLT:
                    asm.append("END\n");
                    break;
                
                default:
                    asm.append("; Unknown opcode: 0x")
                       .append(String.format("%02X", opcode)).append("\n");
            }
        }
        
        return asm.toString();
    }
}
