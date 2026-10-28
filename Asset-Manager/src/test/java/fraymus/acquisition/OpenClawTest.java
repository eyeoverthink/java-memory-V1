package fraymus.acquisition;

/**
 * OpenClaw Test - Full Capability Acquisition Layer
 * Demonstrates unrestricted access to web, file system, and command execution
 */
public class OpenClawTest {
    
    public static void main(String[] args) {
        System.out.println("=== OpenClaw Full Capability Test ===\n");
        
        // Test 1: Web acquisition
        testWebAcquisition();
        
        // Test 2: File acquisition
        testFileAcquisition();
        
        // Test 3: Directory acquisition
        testDirectoryAcquisition();
        
        // Test 4: Command execution
        testCommandExecution();
        
        // Test 5: Multiple sources
        testMultipleSources();
        
        // Test 6: File search
        testFileSearch();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testWebAcquisition() {
        System.out.println("Test 1: Web Acquisition");
        
        OpenClaw claw = new OpenClaw();
        
        // Test with a simple accessible URL (example.com)
        String url = "https://example.com";
        OpenClaw.AcquisitionResult result = claw.acquireWeb(url);
        
        System.out.println("URL: " + url);
        System.out.println("Success: " + result.success);
        System.out.println("Content Type: " + result.contentType);
        System.out.println("Size: " + result.size + " bytes");
        if (result.error != null) {
            System.out.println("Error: " + result.error);
        }
        if (result.content != null && result.content.length() < 500) {
            System.out.println("Content preview: " + result.content.substring(0, Math.min(200, result.content.length())));
        }
        System.out.println();
    }
    
    private static void testFileAcquisition() {
        System.out.println("Test 2: File Acquisition");
        
        OpenClaw claw = new OpenClaw();
        
        // Test with current directory (this test file)
        String filePath = "src/test/java/fraymus/acquisition/OpenClawTest.java";
        OpenClaw.AcquisitionResult result = claw.acquireFile(filePath);
        
        System.out.println("File: " + filePath);
        System.out.println("Success: " + result.success);
        System.out.println("Content Type: " + result.contentType);
        System.out.println("Size: " + result.size + " bytes");
        if (result.success && result.content != null) {
            System.out.println("Lines: " + result.content.split("\n").length);
        }
        if (result.error != null) {
            System.out.println("Error: " + result.error);
        }
        System.out.println();
    }
    
    private static void testDirectoryAcquisition() {
        System.out.println("Test 3: Directory Acquisition");
        
        OpenClaw claw = new OpenClaw();
        
        // Test with current directory
        String dirPath = ".";
        OpenClaw.AcquisitionResult result = claw.acquireDirectory(dirPath, false);
        
        System.out.println("Directory: " + dirPath);
        System.out.println("Success: " + result.success);
        if (result.success) {
            System.out.println("Content preview (first 500 chars):");
            System.out.println(result.content.substring(0, Math.min(500, result.content.length())));
        }
        if (result.error != null) {
            System.out.println("Error: " + result.error);
        }
        System.out.println();
    }
    
    private static void testCommandExecution() {
        System.out.println("Test 4: Command Execution");
        
        OpenClaw claw = new OpenClaw();
        
        // Test with simple command (list directory)
        String command = "cmd /c dir"; // Windows
        // String command = "ls"; // Unix/Linux/Mac
        
        OpenClaw.AcquisitionResult result = claw.executeCommand(command);
        
        System.out.println("Command: " + command);
        System.out.println("Success: " + result.success);
        System.out.println("Exit Code: " + result.metadata.get("exit_code"));
        System.out.println("Size: " + result.size + " bytes");
        if (result.success && result.content != null) {
            System.out.println("Output preview (first 500 chars):");
            System.out.println(result.content.substring(0, Math.min(500, result.content.length())));
        }
        if (result.error != null) {
            System.out.println("Error: " + result.error);
        }
        System.out.println();
    }
    
    private static void testMultipleSources() {
        System.out.println("Test 5: Multiple Sources");
        
        OpenClaw claw = new OpenClaw();
        
        java.util.List<String> sources = new java.util.ArrayList<>();
        sources.add("src/test/java/fraymus/acquisition/OpenClawTest.java");
        sources.add("src/main/java/fraymus/acquisition/OpenClaw.java");
        
        java.util.List<OpenClaw.AcquisitionResult> results = claw.acquireMultiple(sources);
        
        System.out.println("Sources: " + sources.size());
        int successCount = 0;
        for (OpenClaw.AcquisitionResult result : results) {
            if (result.success) successCount++;
            System.out.println("  " + result.source + ": " + (result.success ? "SUCCESS" : "FAILED"));
        }
        System.out.println("Total: " + successCount + "/" + sources.size() + " successful");
        System.out.println();
    }
    
    private static void testFileSearch() {
        System.out.println("Test 6: File Search");
        
        OpenClaw claw = new OpenClaw();
        
        // Search for Java files in current directory
        String directory = ".";
        String pattern = ".java";
        
        java.util.List<String> matches = claw.searchFiles(directory, pattern, false);
        
        System.out.println("Directory: " + directory);
        System.out.println("Pattern: " + pattern);
        System.out.println("Matches: " + matches.size());
        if (matches.size() > 0) {
            System.out.println("First 5 matches:");
            for (int i = 0; i < Math.min(5, matches.size()); i++) {
                System.out.println("  " + matches.get(i));
            }
        }
        System.out.println();
    }
}
