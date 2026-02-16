import fraymus.body.DockerBox;
import fraymus.genesis.GenesisSandbox;
import fraymus.core.AuditLog;

public class TestSandbox {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧪 FRAYMUS SANDBOX TEST                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test 1: DockerBox - Simple command execution
        System.out.println("TEST 1: DockerBox - Simple Command Execution");
        System.out.println("─────────────────────────────────────────────");
        DockerBox dockerBox = new DockerBox(10, "alpine:latest");
        
        if (dockerBox.isAvailable()) {
            System.out.println("✅ Docker is available");
            System.out.println();
            
            // Test simple echo command
            System.out.println("Running: echo 'Hello from sandbox!'");
            String result = dockerBox.runSafe("echo 'Hello from sandbox!'");
            System.out.println(result);
            System.out.println();
            
            // Test Python execution
            System.out.println("Running Python code: print(2 ** 10)");
            String pythonResult = dockerBox.runPython("print(2 ** 10)");
            System.out.println(pythonResult);
            System.out.println();
            
            // Test shell script
            System.out.println("Running shell script: ls -la /");
            String shellResult = dockerBox.runShell("ls -la / | head -10");
            System.out.println(shellResult);
            System.out.println();
            
        } else {
            System.out.println("❌ Docker is not available");
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Test 2: GenesisSandbox - Code verification
        System.out.println("TEST 2: GenesisSandbox - Code Verification");
        System.out.println("─────────────────────────────────────────────");
        
        AuditLog auditLog = new AuditLog("./audit.log");
        GenesisSandbox genesisSandbox = new GenesisSandbox(auditLog);
        
        // Create a simple test directory
        java.io.File testDir = new java.io.File("./test-artifact");
        testDir.mkdirs();
        
        // Create a simple test file
        try {
            java.io.FileWriter writer = new java.io.FileWriter("./test-artifact/test.py");
            writer.write("print('Genesis Sandbox Test Passed!')\\n");
            writer.write("assert 1 + 1 == 2\\n");
            writer.write("print('All tests passed')\\n");
            writer.close();
            
            System.out.println("Created test artifact: ./test-artifact/test.py");
            System.out.println();
            
            // Verify the artifact
            GenesisSandbox.VerificationResult result = genesisSandbox.verifyArtifact(
                "./test-artifact", 
                "python"
            );
            
            System.out.println();
            System.out.println("Verification Result: " + result);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Error creating test artifact: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🎉 SANDBOX TEST COMPLETE");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
