package fraymus.body;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * DockerBox - Sandboxed Command Execution
 * 
 * Runs potentially dangerous commands inside Docker containers
 * to prevent damage to the host system.
 * 
 * Requires Docker to be installed and running.
 */
public class DockerBox {
    
    private final int timeoutSeconds;
    private final String defaultImage;
    private boolean dockerAvailable = false;
    
    public DockerBox() {
        this(5, "alpine:latest");
    }
    
    public DockerBox(int timeoutSeconds, String defaultImage) {
        this.timeoutSeconds = timeoutSeconds;
        this.defaultImage = defaultImage;
        checkDockerAvailability();
    }
    
    /**
     * Check if Docker is available on the system
     */
    private void checkDockerAvailability() {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "--version");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            boolean finished = p.waitFor(2, TimeUnit.SECONDS);
            if (finished && p.exitValue() == 0) {
                dockerAvailable = true;
                
                // Read version
                String version = new String(p.getInputStream().readAllBytes()).trim();
                System.out.println("   🐳 DOCKER DETECTED: " + version);
            } else {
                System.err.println("   ⚠️  DOCKER NOT AVAILABLE");
            }
            
        } catch (Exception e) {
            System.err.println("   ⚠️  DOCKER CHECK FAILED: " + e.getMessage());
            dockerAvailable = false;
        }
    }
    
    /**
     * Check if Docker is available
     */
    public boolean isAvailable() {
        return dockerAvailable;
    }
    
    /**
     * Run command safely in default Alpine container
     */
    public String runSafe(String command) {
        return runSafe(command, defaultImage);
    }
    
    /**
     * Run command safely in specified container image
     */
    public String runSafe(String command, String image) {
        if (!dockerAvailable) {
            return "❌ DOCKER NOT AVAILABLE\nInstall Docker to use sandboxed execution.";
        }
        
        try {
            System.out.println("   🐳 DOCKER BOX: Executing safely in " + image + "...");
            System.out.println("   📝 Command: " + command);
            
            // Build Docker command
            List<String> dockerCmd = new ArrayList<>();
            dockerCmd.add("docker");
            dockerCmd.add("run");
            dockerCmd.add("--rm");                    // Remove container after execution
            dockerCmd.add("--network=none");          // No network access
            dockerCmd.add("--memory=256m");           // Memory limit
            dockerCmd.add("--cpus=0.5");              // CPU limit
            dockerCmd.add("--read-only");             // Read-only filesystem
            dockerCmd.add("--tmpfs=/tmp:rw,size=10m"); // Small writable tmp
            dockerCmd.add(image);
            dockerCmd.add("/bin/sh");
            dockerCmd.add("-c");
            dockerCmd.add(command);
            
            ProcessBuilder pb = new ProcessBuilder(dockerCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            // Capture output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // Wait with timeout
            boolean finished = p.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                p.destroy();
                p.waitFor(1, TimeUnit.SECONDS);
                if (p.isAlive()) {
                    p.destroyForcibly();
                }
                return "❌ TIMEOUT (" + timeoutSeconds + "s)\n" +
                       "Container killed by sandbox.\n\n" +
                       "Partial output:\n" + output.toString();
            }
            
            int exitCode = p.exitValue();
            String result = "📦 SANDBOX OUTPUT (exit: " + exitCode + "):\n\n" + output.toString();
            
            if (exitCode != 0) {
                result = "⚠️  " + result;
            } else {
                result = "✅ " + result;
            }
            
            return result;
            
        } catch (Exception e) {
            return "❌ SANDBOX ERROR: " + e.getMessage() + "\n" +
                   "Stack trace: " + getStackTrace(e);
        }
    }
    
    /**
     * Run Python code safely
     */
    public String runPython(String code) {
        return runSafe("python3 -c '" + escapeSingleQuotes(code) + "'", "python:3.11-alpine");
    }
    
    /**
     * Run Node.js code safely
     */
    public String runNode(String code) {
        return runSafe("node -e '" + escapeSingleQuotes(code) + "'", "node:alpine");
    }
    
    /**
     * Run shell script safely
     */
    public String runShell(String script) {
        return runSafe(script, "alpine:latest");
    }
    
    /**
     * Test if a specific Docker image is available
     */
    public boolean hasImage(String image) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "images", "-q", image);
            Process p = pb.start();
            
            String output = new String(p.getInputStream().readAllBytes()).trim();
            p.waitFor(2, TimeUnit.SECONDS);
            
            return !output.isEmpty();
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Pull a Docker image if not available
     */
    public boolean pullImage(String image) {
        if (!dockerAvailable) return false;
        
        try {
            System.out.println("   📥 Pulling Docker image: " + image);
            
            ProcessBuilder pb = new ProcessBuilder("docker", "pull", image);
            pb.redirectErrorStream(true);
            pb.inheritIO(); // Show progress
            
            Process p = pb.start();
            boolean finished = p.waitFor(60, TimeUnit.SECONDS);
            
            return finished && p.exitValue() == 0;
            
        } catch (Exception e) {
            System.err.println("   ❌ Image pull failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get sandbox statistics
     */
    public String getStats() {
        if (!dockerAvailable) {
            return "Docker: NOT AVAILABLE";
        }
        
        return String.format(
            "Docker: AVAILABLE | Default Image: %s | Timeout: %ds",
            defaultImage, timeoutSeconds
        );
    }
    
    /**
     * Escape single quotes for shell commands
     */
    private String escapeSingleQuotes(String str) {
        return str.replace("'", "'\\''");
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
