package fraymus.execution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ExecutionBridge - bare-metal code execution
 */
public class ExecutionBridgeTest {
    
    private final ExecutionBridge bridge = new ExecutionBridge();
    
    @Test
    public void testDetectCodeBlocks() {
        String text = "Here is some Java code:\n```java\npublic class Test {\n    public static void main(String[] args) {\n        System.out.println(\"Hello\");\n    }\n}\n```\nAnd some Python:\n```python\nprint(\"World\")\n```";
        
        var blocks = bridge.detectCodeBlocks(text);
        
        assertEquals(2, blocks.size());
        assertEquals("java", blocks.get(0).getLanguage());
        assertEquals("python", blocks.get(1).getLanguage());
        assertTrue(blocks.get(0).getCode().contains("public class Test"));
        assertTrue(blocks.get(1).getCode().contains("print(\"World\")"));
    }
    
    @Test
    public void testDetectCodeBlocksNoLanguage() {
        String text = "Code without language:\n```\necho hello\n```";
        
        var blocks = bridge.detectCodeBlocks(text);
        
        assertEquals(1, blocks.size());
        assertEquals("", blocks.get(0).getLanguage());
        assertTrue(blocks.get(0).getCode().contains("echo hello"));
    }
    
    @Test
    public void testDetectCodeBlocksEmpty() {
        String text = "No code blocks here";
        
        var blocks = bridge.detectCodeBlocks(text);
        
        assertTrue(blocks.isEmpty());
    }
    
    @Test
    public void testExecuteJavaSimple() {
        String code = "public class SimpleTest {\n" +
                      "    public static void main(String[] args) {\n" +
                      "        System.out.println(\"42\");\n" +
                      "    }\n" +
                      "}";

        ExecutionBridge.CodeBlock block = new ExecutionBridge.CodeBlock("java", code, 1, 5);
        ExecutionBridge.ExecutionResult result = bridge.executeCodeBlock(block);

        // If javac is not available, test passes gracefully
        if (!isJavacAvailable()) {
            assertFalse(result.isSuccess(), "Expected failure when javac unavailable");
            return;
        }

        assertTrue(result.isSuccess(), "Java execution should succeed: " + result.getStderr());
        assertTrue(result.getStdout().contains("42"), "Output should contain 42: " + result.getStdout());
        assertEquals(0, result.getExitCode());
    }
    
    @Test
    public void testExecuteJavaMath() {
        String code = "public class MathTest {\n" +
                      "    public static void main(String[] args) {\n" +
                      "        double c = 300;\n" +
                      "        double m0 = 14.5;\n" +
                      "        double v = 295;\n" +
                      "        double gamma = 1 / Math.sqrt(1 - (v * v) / (c * c));\n" +
                      "        double relativisticMass = m0 * gamma;\n" +
                      "        System.out.println(\"Relativistic Mass: \" + relativisticMass + \" kg\");\n" +
                      "    }\n" +
                      "}";

        ExecutionBridge.CodeBlock block = new ExecutionBridge.CodeBlock("java", code, 1, 9);
        ExecutionBridge.ExecutionResult result = bridge.executeCodeBlock(block);

        // If javac is not available, test passes gracefully
        if (!isJavacAvailable()) {
            assertFalse(result.isSuccess(), "Expected failure when javac unavailable");
            return;
        }

        assertTrue(result.isSuccess(), "Java execution should succeed: " + result.getStderr());
        assertTrue(result.getStdout().contains("79.75"), "Output should contain 79.75: " + result.getStdout());
    }
    
    @Test
    public void testExecutePythonSimple() {
        String code = "print(\"Hello from Python\")";
        
        ExecutionBridge.CodeBlock block = new ExecutionBridge.CodeBlock("python", code, 1, 1);
        ExecutionBridge.ExecutionResult result = bridge.executeCodeBlock(block);
        
        // Python might not be available on all systems
        // We'll just check that it doesn't crash
        assertNotNull(result);
    }
    
    @Test
    public void testExecuteShellEcho() {
        String command = "echo test";
        
        ExecutionBridge.ExecutionResult result = bridge.executeShell(command);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getStdout().toLowerCase().contains("test"));
    }
    
    @Test
    public void testProcessText() {
        String text = "Calculate:\n```java\npublic class Calc {\n    public static void main(String[] args) {\n        System.out.println(\"2+2=4\");\n    }\n}\n```";

        String processed = bridge.processText(text);

        // If javac is not available, test passes gracefully (code block remains unchanged)
        if (!isJavacAvailable()) {
            assertTrue(processed.contains("public class Calc"), "Code block should remain when javac unavailable");
            return;
        }

        // Should replace code block with execution result
        assertTrue(processed.contains("2+2=4"), "Output should contain 2+2=4: " + processed);
        assertFalse(processed.contains("public class Calc"), "Code block should be replaced");
    }
    
    @Test
    public void testProcessTextNoCode() {
        String text = "Just plain text with no code blocks";
        
        String processed = bridge.processText(text);
        
        assertEquals(text, processed);
    }
    
    @Test
    public void testExecutionResultSuccess() {
        ExecutionBridge.ExecutionResult result = 
            new ExecutionBridge.ExecutionResult("output", "", 0, 100);
        
        assertTrue(result.isSuccess());
        assertEquals("output", result.toString());
        assertEquals(0, result.getExitCode());
        assertEquals(100, result.getExecutionTimeMs());
    }
    
    @Test
    public void testExecutionResultFailure() {
        ExecutionBridge.ExecutionResult result = 
            new ExecutionBridge.ExecutionResult("", "error message", 1, 50);
        
        assertFalse(result.isSuccess());
        assertTrue(result.toString().contains("ERROR"));
        assertTrue(result.toString().contains("error message"));
        assertEquals(1, result.getExitCode());
    }
    
    /**
     * Helper method to check if javac is available in PATH
     */
    private boolean isJavacAvailable() {
        try {
            Process process = new ProcessBuilder("javac", "-version").start();
            process.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
