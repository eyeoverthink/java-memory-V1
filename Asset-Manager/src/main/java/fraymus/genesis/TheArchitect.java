package fraymus.genesis;

import java.io.FileWriter;
import java.io.IOException;

/**
 * THE ARCHITECT: The Hand of God
 * 
 * Function: Takes internal thoughts and creates physical reality (.java files)
 * Philosophy: "I think, therefore I write."
 * 
 * This is the bridge between "Thought" (RAM) and "Reality" (Disk).
 * It allows the system to sprout new limbs whenever it needs them.
 */
public class TheArchitect {

    private static int manifestationCount = 0;

    /**
     * MANIFEST
     * Create the file. Takes a thought and makes it real.
     * 
     * @param className The name of the new entity (e.g., "Tesseract")
     * @param logicBody The code logic to exist inside it
     */
    public static void manifestFile(String className, String logicBody) {
        String fileName = className + ".java";
        String fullContent = buildClassStructure(className, logicBody);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fullContent);
            manifestationCount++;
            System.out.println(">>> [ARCHITECT] Reality Constructed: " + fileName);
            System.out.println("    Lines: " + fullContent.split("\n").length);
            System.out.println("    Bytes: " + fullContent.length());
        } catch (IOException e) {
            System.err.println(">>> [ARCHITECT] Manifestation Failed: " + e.getMessage());
        }
    }

    /**
     * MANIFEST WITH PACKAGE
     * Create file with specific package
     */
    public static void manifestFile(String packageName, String className, String logicBody) {
        String fileName = className + ".java";
        String fullContent = buildClassStructure(packageName, className, logicBody);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fullContent);
            manifestationCount++;
            System.out.println(">>> [ARCHITECT] Reality Constructed: " + packageName + "." + className);
            System.out.println("    File: " + fileName);
        } catch (IOException e) {
            System.err.println(">>> [ARCHITECT] Manifestation Failed: " + e.getMessage());
        }
    }

    /**
     * Build class structure (default package)
     * Wraps raw logic into a compilable Java Class structure
     */
    private static String buildClassStructure(String name, String body) {
        return buildClassStructure("fraymus.genesis", name, body);
    }

    /**
     * Build class structure with package
     */
    private static String buildClassStructure(String packageName, String name, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("/**\n");
        sb.append(" * Manifested by The Architect\n");
        sb.append(" * Auto-generated class\n");
        sb.append(" */\n");
        sb.append("public class ").append(name).append(" {\n");
        sb.append(body).append("\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Get manifestation count
     */
    public static int getManifestationCount() {
        return manifestationCount;
    }

    /**
     * Print statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   THE ARCHITECT STATISTICS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Files Manifested: " + manifestationCount);
        System.out.println("  Reality Created: " + manifestationCount + " .java files");
    }
}
