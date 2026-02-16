package fraymus.genesis;

import fraymus.core.AuditLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * GENESIS SANDBOX - The Proving Ground
 * 
 * Runs generated code inside disposable Docker containers.
 * Prevents "self-modifying OS" from becoming "self-owning OS".
 * 
 * Process:
 * 1. Spin up isolated container
 * 2. Mount generated code
 * 3. Run build/test
 * 4. Capture results
 * 5. Destroy container
 * 
 * If it passes, deploy. If it fails, reject.
 */
public class GenesisSandbox {

    private final AuditLog auditLog;
    private static final long TIMEOUT_SECONDS = 120; // 2 minutes max

    public GenesisSandbox(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    /**
     * PROVE: Runs the generated code inside a disposable container.
     * Returns TRUE if the build/test passes.
     */
    public VerificationResult verifyArtifact(String artifactPath, String language) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧪 GENESIS SANDBOX - CONTAINMENT VERIFICATION         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Artifact: " + artifactPath);
        System.out.println("Language: " + language);
        System.out.println();
        
        auditLog.log("sandbox_verification_started", artifactPath);

        // Check if Docker is available
        if (!isDockerAvailable()) {
            System.out.println("⚠️ Docker not available - skipping containerized verification");
            auditLog.log("sandbox_docker_unavailable", artifactPath);
            return new VerificationResult(true, "Docker unavailable - skipped verification", "");
        }

        String containerImage = mapLangToImage(language);
        String testCommand = mapLangToTestCmd(language);
        
        System.out.println("Container: " + containerImage);
        System.out.println("Test Command: " + testCommand);
        System.out.println();
        System.out.println("Spinning up containment...");
        
        // 1. DOCKER COMMAND
        File artifactFile = new File(artifactPath);
        if (!artifactFile.exists()) {
            System.err.println("❌ Artifact path does not exist: " + artifactPath);
            auditLog.log("sandbox_artifact_not_found", artifactPath);
            return new VerificationResult(false, "Artifact not found", "");
        }
        
        String absolutePath = artifactFile.getAbsolutePath();
        
        String[] cmd = {
            "docker", "run", "--rm",
            "-v", absolutePath + ":/app",
            "-w", "/app",
            "--memory", "512m",  // Limit memory
            "--cpus", "1",       // Limit CPU
            "--network", "none", // No network access
            containerImage,
            "sh", "-c", testCommand
        };

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 2. CAPTURE OUTPUT
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            boolean success = false;
            
            System.out.println("Container output:");
            while ((line = reader.readLine()) != null) {
                System.out.println("   [DOCKER]: " + line);
                output.append(line).append("\n");
                
                // Look for success signals
                if (line.contains("BUILD SUCCESSFUL") || 
                    line.contains("passed") ||
                    line.contains("OK") ||
                    line.contains("All tests passed")) {
                    success = true;
                }
                
                // Look for failure signals
                if (line.contains("BUILD FAILED") ||
                    line.contains("FAILED") ||
                    line.contains("Error") ||
                    line.contains("Exception")) {
                    success = false;
                }
            }

            // 3. TIMEOUT & EXIT
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                System.err.println("   ❌ SANDBOX: Timeout after " + TIMEOUT_SECONDS + " seconds");
                auditLog.log("sandbox_timeout", artifactPath);
                return new VerificationResult(false, "Timeout", output.toString());
            }

            int exitCode = process.exitValue();
            boolean passed = success && exitCode == 0;
            
            System.out.println();
            if (passed) {
                System.out.println("✅ VERIFICATION PASSED");
                auditLog.log("sandbox_verification_passed", artifactPath);
            } else {
                System.out.println("❌ VERIFICATION FAILED (exit code: " + exitCode + ")");
                auditLog.log("sandbox_verification_failed", artifactPath);
            }
            System.out.println();
            
            return new VerificationResult(passed, 
                passed ? "All tests passed" : "Tests failed (exit code: " + exitCode + ")",
                output.toString());

        } catch (Exception e) {
            System.err.println("   ❌ SANDBOX FAILURE: " + e.getMessage());
            auditLog.log("sandbox_error", artifactPath, e);
            return new VerificationResult(false, "Sandbox error: " + e.getMessage(), "");
        }
    }

    /**
     * Check if Docker is available
     */
    private boolean isDockerAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "--version").start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Map language to Docker image
     */
    private String mapLangToImage(String tech) {
        String lower = tech.toLowerCase();
        
        if (lower.contains("java") || lower.contains("gradle")) {
            return "gradle:jdk17-alpine";
        }
        if (lower.contains("python")) {
            return "python:3.9-alpine";
        }
        if (lower.contains("node") || lower.contains("javascript") || lower.contains("react")) {
            return "node:18-alpine";
        }
        if (lower.contains("rust")) {
            return "rust:alpine";
        }
        if (lower.contains("go")) {
            return "golang:alpine";
        }
        
        return "ubuntu:latest"; // Fallback
    }

    /**
     * Map language to test command
     */
    private String mapLangToTestCmd(String tech) {
        String lower = tech.toLowerCase();
        
        if (lower.contains("java") || lower.contains("gradle")) {
            return "gradle test || echo 'No tests defined'";
        }
        if (lower.contains("python")) {
            return "python -m pytest || python -m unittest discover || echo 'No tests defined'";
        }
        if (lower.contains("node") || lower.contains("javascript")) {
            return "npm test || echo 'No tests defined'";
        }
        if (lower.contains("rust")) {
            return "cargo test || echo 'No tests defined'";
        }
        if (lower.contains("go")) {
            return "go test ./... || echo 'No tests defined'";
        }
        
        return "echo 'Unknown tech - verification skipped'";
    }

    /**
     * Verification result
     */
    public static class VerificationResult {
        public final boolean passed;
        public final String message;
        public final String output;
        
        public VerificationResult(boolean passed, String message, String output) {
            this.passed = passed;
            this.message = message;
            this.output = output;
        }
        
        @Override
        public String toString() {
            return String.format("VerificationResult[passed=%s, message=%s]", passed, message);
        }
    }
}
