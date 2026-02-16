package fraymus.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 🔧 SYSTEM SKILLS - The Toolbelt
 * "Raw OS capabilities exposed as lambdas"
 * 
 * This contains the actual implementations of system commands.
 * Each method is a Consumer<String> that can be registered in IntentRegistry.
 */
public class SystemSkills {

    /**
     * List files in current directory
     */
    public static void listFiles(String args) {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();
        if (files != null) {
            System.out.println();
            for (File f : files) {
                System.out.println(f.isDirectory() ? "📁 " + f.getName() : "📄 " + f.getName());
            }
        } else {
            System.out.println("❌ Could not list files");
        }
    }

    /**
     * Print working directory
     */
    public static void printWorkingDir(String args) {
        System.out.println("📍 " + System.getProperty("user.dir"));
    }

    /**
     * Echo text
     */
    public static void echo(String args) {
        System.out.println("🗣️ " + args);
    }
    
    /**
     * Read file contents
     */
    public static void cat(String args) {
        try {
            // Simple heuristic to find a filename in the args
            String[] parts = args.split(" ");
            String filename = parts[parts.length - 1]; 
            System.out.println();
            System.out.println(Files.readString(Path.of(filename)));
        } catch (Exception e) {
            System.out.println("❌ Could not read file: " + e.getMessage());
        }
    }

    /**
     * Show help
     */
    public static void help(String args) {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         FRAYSH HELP                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This is a hyper-dimensional shell. Type natural language:");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  'list files' or 'show me files' → lists directory");
        System.out.println("  'where am i' or 'current location' → prints working dir");
        System.out.println("  'read file.txt' or 'show file.txt' → displays file");
        System.out.println("  'say hello' or 'echo hello' → prints text");
        System.out.println();
        System.out.println("One-shot learning:");
        System.out.println("  bind ls <your phrase> → teaches new phrase for ls");
        System.out.println("  bind pwd <your phrase> → teaches new phrase for pwd");
        System.out.println();
        System.out.println("Type 'exit' to quit");
        System.out.println();
    }
}
