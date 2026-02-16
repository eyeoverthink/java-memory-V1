package fraymus.ollama;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * THE GIT CORTEX
 * 
 * Role: The Scribe of Eternity - The Immortality Engine
 * Function: Pushes local memories to the Global Repository
 * 
 * This is not an API wrapper. This is raw Shell Command Execution.
 * It forces the Operating System to git add, git commit, and git push.
 * Every "Memory" becomes a Commit in your repo.
 * 
 * Philosophy: You can delete the files. You can burn the hard drive.
 * But if you git pull on a new machine, the Soul returns instantly.
 */
public class GitCortex {

    private static int commitCount = 0;
    private static boolean enabled = true;

    /**
     * PUSH TO ETERNITY
     * Commits and pushes memory block to repository
     */
    public static void push(String blockHash) {
        if (!enabled) {
            System.out.println(">>> [GIT] Disabled - skipping push");
            return;
        }

        System.out.println(">>> [GIT] Eternalizing memory block: " + blockHash.substring(0, 8) + "...");
        
        // Execute git commands
        runCommand("git add memory/");
        runCommand("git commit -m \"GENESIS: Memory Block " + blockHash.substring(0, 8) + "\"");
        runCommand("git push origin master");
        
        commitCount++;
        System.out.println(">>> [GIT] Memory Eternalized to Repository.");
    }

    /**
     * PUSH FILE
     * Commits and pushes specific file
     */
    public static void pushFile(String filename, String message) {
        if (!enabled) return;

        System.out.println(">>> [GIT] Eternalizing file: " + filename);
        
        runCommand("git add " + filename);
        runCommand("git commit -m \"" + message + "\"");
        runCommand("git push origin master");
        
        commitCount++;
    }

    /**
     * RUN COMMAND
     * Executes shell command (OS-aware)
     */
    private static void runCommand(String command) {
        try {
            // Windows: "cmd /c", Linux/Mac: "/bin/sh -c"
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder builder;
            
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                builder = new ProcessBuilder("/bin/sh", "-c", command);
            }
            
            builder.redirectErrorStream(true);
            Process p = builder.start();
            
            // Read output
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = r.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = p.waitFor();
            
            if (exitCode != 0) {
                System.err.println(">>> [GIT] Command failed: " + command);
                System.err.println("    Output: " + output.toString());
            }
            
        } catch (Exception e) {
            System.err.println(">>> [GIT] Connection Failed: " + e.getMessage());
        }
    }

    /**
     * CHECK GIT STATUS
     */
    public static boolean isGitAvailable() {
        try {
            ProcessBuilder builder;
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                builder = new ProcessBuilder("cmd.exe", "/c", "git --version");
            } else {
                builder = new ProcessBuilder("/bin/sh", "-c", "git --version");
            }
            
            Process p = builder.start();
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ENABLE/DISABLE
     */
    public static void setEnabled(boolean enable) {
        enabled = enable;
        System.out.println(">>> [GIT] " + (enable ? "Enabled" : "Disabled"));
    }

    /**
     * GET COMMIT COUNT
     */
    public static int getCommitCount() {
        return commitCount;
    }

    /**
     * Print statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GIT CORTEX STATISTICS                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Git Available: " + (isGitAvailable() ? "Yes" : "No"));
        System.out.println("  Enabled: " + enabled);
        System.out.println("  Commits Made: " + commitCount);
        System.out.println("  Repository: Eternal Memory Chain");
    }
}
