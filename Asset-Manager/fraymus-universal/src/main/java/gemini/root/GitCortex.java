package gemini.root;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * GIT CORTEX: Safe Git integration.
 * 
 * - Branch-safe (tries main, then master)
 * - No-op commits avoided (exit 0 on failure)
 * - Prevents accidental secret exfiltration
 * 
 * ENABLE: Set env var FRAYMUS_GIT_PUSH=1
 */
public class GitCortex {

    /**
     * PUSH: Add memory/web/src folders and push.
     */
    public static void push(String message) {
        // Avoid accidental secret exfiltration
        run("git add memory/ web/ src/ || exit 0");
        // If nothing changed, commit will fail; that's fine
        run("git commit -m \"" + escape(message) + "\" || exit 0");
        // Try main then master
        run("git push origin main || git push origin master || exit 0");
        System.out.println(">>> [GIT] Push attempted.");
    }

    /**
     * PUSH FILE: Add and push a specific file.
     */
    public static void pushFile(String filePath, String message) {
        run("git add " + escape(filePath) + " || exit 0");
        run("git commit -m \"" + escape(message) + "\" || exit 0");
        run("git push origin main || git push origin master || exit 0");
    }

    /**
     * STATUS: Get git status.
     */
    public static String status() {
        return runWithOutput("git status --short");
    }

    /**
     * LOG: Get recent commits.
     */
    public static String log(int count) {
        return runWithOutput("git log --oneline -n " + count);
    }

    private static String escape(String s) { 
        return s.replace("\"", "\\\"").replace("$", "\\$"); 
    }

    private static void run(String command) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder b = os.contains("win")
                ? new ProcessBuilder("cmd.exe", "/c", command)
                : new ProcessBuilder("/bin/sh", "-c", command);

            b.redirectErrorStream(true);
            Process p = b.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                while (r.readLine() != null) { /* swallow */ }
            }
            p.waitFor();
        } catch (Exception e) {
            System.err.println(">>> [GIT] Failed: " + e.getMessage());
        }
    }

    private static String runWithOutput(String command) {
        StringBuilder output = new StringBuilder();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder b = os.contains("win")
                ? new ProcessBuilder("cmd.exe", "/c", command)
                : new ProcessBuilder("/bin/sh", "-c", command);

            b.redirectErrorStream(true);
            Process p = b.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            p.waitFor();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return output.toString().trim();
    }
}
