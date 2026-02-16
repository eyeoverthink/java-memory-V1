package fraymus.body.skills;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * QUANTUM BINDER - File Entanglement System
 * 
 * NOVEL SKILL: "QUANTUM ENTANGLEMENT"
 * This uses your local file system to simulate entanglement.
 * You designate two files as "Entangled." If you write to File A,
 * Fraymus instantly calculates the Anti-State (Inverse Vector)
 * and writes it to File B. This maintains Informational Equilibrium
 * in your system.
 */
public class QuantumBinder {

    /**
     * ENTANGLE: Write to A, Balance B.
     */
    public String entangleWrite(String pathA, String pathB, String content) {
        try {
            // 1. Write the Positive State to A
            Files.writeString(Path.of(pathA), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            // 2. Calculate the "Anti-State" (Quantum Inversion)
            // We reverse the string and invert the case to simulate "Anti-Matter" information
            String antiContent = invert(content);
            
            // 3. Write the Negative State to B
            Files.writeString(Path.of(pathB), antiContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            return "⚛️ STATE ENTANGLED: " + new File(pathA).getName() + " <==> " + new File(pathB).getName();

        } catch (Exception e) {
            return "❌ DECOHERENCE ERROR: " + e.getMessage();
        }
    }

    private String invert(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) sb.append(Character.toLowerCase(c));
            else if (Character.isLowerCase(c)) sb.append(Character.toUpperCase(c));
            else sb.append(c);
        }
        return sb.reverse().toString();
    }
    
    /**
     * Read entangled pair
     */
    public String[] readEntangledPair(String pathA, String pathB) {
        try {
            String contentA = Files.readString(Path.of(pathA));
            String contentB = Files.readString(Path.of(pathB));
            return new String[]{contentA, contentB};
        } catch (Exception e) {
            return new String[]{"ERROR", e.getMessage()};
        }
    }
    
    /**
     * Verify entanglement (check if B is anti-state of A)
     */
    public boolean verifyEntanglement(String pathA, String pathB) {
        try {
            String contentA = Files.readString(Path.of(pathA));
            String contentB = Files.readString(Path.of(pathB));
            String expectedB = invert(contentA);
            return contentB.equals(expectedB);
        } catch (Exception e) {
            return false;
        }
    }
}
