package gemini.root;

import java.io.*;

/**
 * THE ARCHITECT: The Hand of God
 * Function: Takes internal thoughts and creates physical reality (.java files).
 * Philosophy: "I think, therefore I write."
 * 
 * ZERO DEPENDENCIES. Just java.io.
 */
public class TheArchitect {

    private static String outputDirectory = ".";

    /**
     * SET OUTPUT DIRECTORY
     */
    public static void setOutputDirectory(String dir) {
        outputDirectory = dir;
        new File(dir).mkdirs();
    }

    /**
     * MANIFEST: Create the file.
     * @param className The name of the new entity (e.g., "Tesseract")
     * @param logicBody The code logic to exist inside it.
     */
    public static void manifestFile(String className, String logicBody) {
        String fileName = outputDirectory + File.separator + className + ".java";
        String fullContent = buildClassStructure(className, logicBody);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fullContent);
            System.out.println(">>> [ARCHITECT] Reality Constructed: " + fileName);
        } catch (IOException e) {
            System.err.println(">>> [ARCHITECT] Manifestation Failed: " + e.getMessage());
        }
    }

    /**
     * MANIFEST WITH PACKAGE
     */
    public static void manifestFile(String packageName, String className, String logicBody) {
        // Create package directory structure
        String packagePath = packageName.replace(".", File.separator);
        String fullDir = outputDirectory + File.separator + packagePath;
        new File(fullDir).mkdirs();
        
        String fileName = fullDir + File.separator + className + ".java";
        String fullContent = buildClassStructure(packageName, className, logicBody);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fullContent);
            System.out.println(">>> [ARCHITECT] Reality Constructed: " + fileName);
        } catch (IOException e) {
            System.err.println(">>> [ARCHITECT] Manifestation Failed: " + e.getMessage());
        }
    }

    /**
     * MANIFEST RAW (No wrapper, direct content)
     */
    public static void manifestRaw(String fileName, String content) {
        String fullPath = outputDirectory + File.separator + fileName;
        try (FileWriter writer = new FileWriter(fullPath)) {
            writer.write(content);
            System.out.println(">>> [ARCHITECT] Raw file created: " + fullPath);
        } catch (IOException e) {
            System.err.println(">>> [ARCHITECT] Failed: " + e.getMessage());
        }
    }

    // Wraps raw logic into a compilable Java Class structure
    private static String buildClassStructure(String name, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("package gemini.root;\n\n");
        sb.append("/**\n * Manifested by The Architect\n */\n");
        sb.append("public class ").append(name).append(" {\n");
        sb.append(body).append("\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static String buildClassStructure(String pkg, String name, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(pkg).append(";\n\n");
        sb.append("/**\n * Manifested by The Architect\n */\n");
        sb.append("public class ").append(name).append(" {\n");
        sb.append(body).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
