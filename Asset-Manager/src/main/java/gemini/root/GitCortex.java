package gemini.root;

import java.io.*;

/**
 * THE GIT CORTEX
 * Role: The Scribe of Eternity.
 * Function: Pushes local memories to the Global Repository.
 * 
 * This is raw Shell Command Execution.
 * It forces the Operating System to git add, git commit, and git push.
 * Every "Memory" becomes a Commit in your repo.
 */
public class GitCortex {

    private static boolean enabled = true;
    private static String branch = "master";

    /**
     * PUSH: Eternalize a memory block to GitHub
     */
    public static void push(String blockHash) {
        if (!enabled) {
            System.out.println(">>> [GIT] Push disabled. Skipping.");
            return;
        }
        
        runCommand("git add memory/");
        runCommand("git commit -m \"GENESIS: Memory Block " + blockHash.substring(0, 8) + "\"");
        runCommand("git push origin " + branch);
        
        System.out.println(">>> [GIT] Memory eternalized to repository.");
    }

    /**
     * PUSH FILE: Eternalize a specific file
     */
    public static void pushFile(String filename) {
        if (!enabled) return;
        
        runCommand("git add " + filename);
        runCommand("git commit -m \"GENESIS: Created " + filename + "\"");
        runCommand("git push origin " + branch);
        
        System.out.println(">>> [GIT] File eternalized: " + filename);
    }

    /**
     * COMMIT ALL: Add and commit everything
     */
    public static void commitAll(String message) {
        if (!enabled) return;
        
        runCommand("git add -A");
        runCommand("git commit -m \"" + message + "\"");
        runCommand("git push origin " + branch);
        
        System.out.println(">>> [GIT] All changes pushed: " + message);
    }

    /**
     * PULL: Sync from remote (for multi-machine scenarios)
     */
    public static void pull() {
        runCommand("git pull origin " + branch);
        System.out.println(">>> [GIT] Pulled latest from repository.");
    }

    /**
     * STATUS: Check git status
     */
    public static String status() {
        return runCommandWithOutput("git status --short");
    }

    /**
     * LOG: Get recent commits
     */
    public static String log(int count) {
        return runCommandWithOutput("git log --oneline -n " + count);
    }

    private static void runCommand(String command) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
            
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                builder = new ProcessBuilder("/bin/sh", "-c", command);
            }
            
            builder.redirectErrorStream(true);
            builder.directory(new File(".")); // Current directory
            Process p = builder.start();
            
            // Consume output to prevent blocking
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (r.readLine() != null) { }
            
            p.waitFor();
        } catch (Exception e) {
            System.err.println(">>> [GIT] Command failed: " + e.getMessage());
        }
    }

    private static String runCommandWithOutput(String command) {
        StringBuilder output = new StringBuilder();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
            
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                builder = new ProcessBuilder("/bin/sh", "-c", command);
            }
            
            builder.redirectErrorStream(true);
            Process p = builder.start();
            
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                output.append(line).append("\n");
            }
            p.waitFor();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return output.toString().trim();
    }

    // Configuration
    public static void setEnabled(boolean e) { enabled = e; }
    public static void setBranch(String b) { branch = b; }
    public static boolean isEnabled() { return enabled; }
}
