package fraymus.coding;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * ISOLATED TEST: FRAYMUS CODER SELF-CORRECTION LOOP
 * 
 * This test verifies the logic WITHOUT requiring Ollama to be running.
 * It uses a MockOllama that simulates:
 * 1. First attempt: Returns code with intentional error
 * 2. Second attempt: Returns fixed code
 * 
 * This proves the self-correcting loop works.
 */
public class FraymusCoderTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   🧪 FRAYMUS CODER ISOLATED TEST");
        System.out.println("   Testing self-correcting code generation logic");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Run tests
        testCodeExtraction();
        testPythonValidation();
        testJavaValidation();
        testJavaScriptValidation();
        testSelfCorrectingLoop();
        testKnowledgeIntegration();
        
        // Summary
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Passed: " + testsPassed);
        System.out.println("   Failed: " + testsFailed);
        System.out.println("   Total:  " + (testsPassed + testsFailed));
        System.out.println();
        
        if (testsFailed == 0) {
            System.out.println("   ✅ ALL TESTS PASSED - Logic verified!");
        } else {
            System.out.println("   ❌ SOME TESTS FAILED - Review output above");
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 1: Code Extraction from Markdown
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testCodeExtraction() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 1: Code Extraction from Markdown                       │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        MockCoder coder = new MockCoder();
        
        // Test 1a: Extract from Python code block
        String markdown1 = "Here's the code:\n```python\nprint('hello')\n```\nThat's it.";
        String extracted1 = coder.testExtractCode(markdown1, "python");
        assert_equals("print('hello')", extracted1, "Python code block extraction");
        
        // Test 1b: Extract from generic code block
        String markdown2 = "```\nfunction test() {}\n```";
        String extracted2 = coder.testExtractCode(markdown2, "javascript");
        assert_equals("function test() {}", extracted2, "Generic code block extraction");
        
        // Test 1c: No code block - return as-is
        String noBlock = "def hello(): pass";
        String extracted3 = coder.testExtractCode(noBlock, "python");
        assert_equals("def hello(): pass", extracted3, "No code block - return trimmed");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 2: Python Validation
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testPythonValidation() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 2: Python Syntax Validation                            │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        MockCoder coder = new MockCoder();
        
        // Valid Python
        String validPython = "def hello():\n    print('hello')\n\nhello()";
        boolean isValid1 = coder.testValidatePython(validPython);
        assert_true(isValid1, "Valid Python passes validation");
        
        // Invalid Python (syntax error)
        String invalidPython = "def hello(\n    print('hello')";
        boolean isValid2 = coder.testValidatePython(invalidPython);
        assert_false(isValid2, "Invalid Python fails validation");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 3: Java Validation
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testJavaValidation() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 3: Java Syntax Validation                              │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        MockCoder coder = new MockCoder();
        
        // Valid Java
        String validJava = "public class Test {\n    public static void main(String[] args) {\n        System.out.println(\"Hello\");\n    }\n}";
        boolean isValid1 = coder.testValidateJava(validJava);
        assert_true(isValid1, "Valid Java passes validation");
        
        // Invalid Java (unbalanced braces)
        String invalidJava = "public class Test {\n    public void hello() {\n}";
        boolean isValid2 = coder.testValidateJava(invalidJava);
        assert_false(isValid2, "Unbalanced braces fails validation");
        
        // No class definition
        String noClass = "System.out.println(\"Hello\");";
        boolean isValid3 = coder.testValidateJava(noClass);
        assert_false(isValid3, "No class definition fails validation");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 4: JavaScript Validation
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testJavaScriptValidation() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 4: JavaScript Syntax Validation                        │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        MockCoder coder = new MockCoder();
        
        // Valid JS
        String validJS = "function test() {\n    console.log('hello');\n}\ntest();";
        boolean isValid1 = coder.testValidateJS(validJS);
        assert_true(isValid1, "Valid JS passes validation");
        
        // Unbalanced parentheses
        String invalidJS1 = "function test( {\n    console.log('hello');\n}";
        boolean isValid2 = coder.testValidateJS(invalidJS1);
        assert_false(isValid2, "Unbalanced parentheses fails");
        
        // Unbalanced brackets
        String invalidJS2 = "const arr = [1, 2, 3;";
        boolean isValid3 = coder.testValidateJS(invalidJS2);
        assert_false(isValid3, "Unbalanced brackets fails");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 5: Self-Correcting Loop (THE MAIN TEST)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testSelfCorrectingLoop() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 5: SELF-CORRECTING LOOP (Main Logic)                   │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        // This test simulates:
        // 1. First call to "Ollama" returns buggy code
        // 2. Validation fails
        // 3. Second call to "Ollama" returns fixed code
        // 4. Validation passes
        // 5. Code is saved
        
        MockCoder coder = new MockCoder();
        
        // Set up mock responses
        coder.setMockResponses(new String[] {
            // First response: buggy code (unbalanced braces)
            "public class Scraper {\n    public void run() {\n        System.out.println(\"Scraping...\");\n}",
            // Second response: fixed code
            "public class Scraper {\n    public void run() {\n        System.out.println(\"Scraping...\");\n    }\n}"
        });
        
        System.out.println("   Simulating self-correcting loop:");
        System.out.println("   - Mock response 1: Buggy code (unbalanced braces)");
        System.out.println("   - Mock response 2: Fixed code");
        System.out.println();
        
        String result = coder.createSoftwareMock("Create a web scraper", "java", "Scraper.java");
        
        assert_not_null(result, "Self-correcting loop produces result");
        assert_equals(2, coder.getAttemptCount(), "Took exactly 2 attempts");
        assert_true(result.contains("public class Scraper"), "Result contains class definition");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 6: Knowledge Integration
    // ═══════════════════════════════════════════════════════════════════
    
    private static void testKnowledgeIntegration() {
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ TEST 6: Knowledge Base Integration                          │");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        MockCoder coder = new MockCoder();
        
        // Test context building
        String context = coder.testBuildPrompt("Create a scraper", "Use requests library", "python");
        
        assert_true(context.contains("requests library"), "Context is included in prompt");
        assert_true(context.contains("Create a scraper"), "User request is included");
        assert_true(context.contains("python"), "Language is mentioned");
        
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // ASSERTION HELPERS
    // ═══════════════════════════════════════════════════════════════════
    
    private static void assert_equals(Object expected, Object actual, String testName) {
        if (expected.equals(actual)) {
            System.out.println("   ✅ " + testName);
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName);
            System.out.println("      Expected: " + expected);
            System.out.println("      Actual:   " + actual);
            testsFailed++;
        }
    }
    
    private static void assert_equals(int expected, int actual, String testName) {
        if (expected == actual) {
            System.out.println("   ✅ " + testName);
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName);
            System.out.println("      Expected: " + expected);
            System.out.println("      Actual:   " + actual);
            testsFailed++;
        }
    }
    
    private static void assert_true(boolean condition, String testName) {
        if (condition) {
            System.out.println("   ✅ " + testName);
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName);
            testsFailed++;
        }
    }
    
    private static void assert_false(boolean condition, String testName) {
        if (!condition) {
            System.out.println("   ✅ " + testName);
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName);
            testsFailed++;
        }
    }
    
    private static void assert_not_null(Object obj, String testName) {
        if (obj != null) {
            System.out.println("   ✅ " + testName);
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName + " (was null)");
            testsFailed++;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MOCK CODER (Isolated from real Ollama)
    // ═══════════════════════════════════════════════════════════════════
    
    static class MockCoder {
        private String[] mockResponses;
        private int responseIndex = 0;
        private int attemptCount = 0;
        
        void setMockResponses(String[] responses) {
            this.mockResponses = responses;
            this.responseIndex = 0;
            this.attemptCount = 0;
        }
        
        int getAttemptCount() {
            return attemptCount;
        }
        
        // Mock Ollama call - returns preset responses
        String mockCallOllama(String prompt) {
            if (mockResponses == null || responseIndex >= mockResponses.length) {
                return null;
            }
            return mockResponses[responseIndex++];
        }
        
        // Test code extraction
        String testExtractCode(String response, String language) {
            String codeBlockStart = "```" + language.toLowerCase();
            String codeBlockEnd = "```";
            
            int start = response.indexOf(codeBlockStart);
            if (start != -1) {
                start += codeBlockStart.length();
                int end = response.indexOf(codeBlockEnd, start);
                if (end != -1) {
                    return response.substring(start, end).trim();
                }
            }
            
            start = response.indexOf("```");
            if (start != -1) {
                start = response.indexOf("\n", start) + 1;
                int end = response.indexOf("```", start);
                if (end != -1) {
                    return response.substring(start, end).trim();
                }
            }
            
            return response.trim();
        }
        
        // Test Python validation
        boolean testValidatePython(String code) {
            try {
                Path tempFile = Files.createTempFile("test_", ".py");
                Files.write(tempFile, code.getBytes(StandardCharsets.UTF_8));
                
                ProcessBuilder pb = new ProcessBuilder("python", "-m", "py_compile", 
                    tempFile.toString());
                pb.redirectErrorStream(true);
                Process p = pb.start();
                
                boolean success = p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0;
                
                Files.deleteIfExists(tempFile);
                return success;
                
            } catch (Exception e) {
                return false;
            }
        }
        
        // Test Java validation
        boolean testValidateJava(String code) {
            if (!code.contains("class ") && !code.contains("interface ") && 
                !code.contains("enum ") && !code.contains("record ")) {
                return false;
            }
            
            int braces = 0;
            for (char c : code.toCharArray()) {
                if (c == '{') braces++;
                if (c == '}') braces--;
            }
            return braces == 0;
        }
        
        // Test JS validation
        boolean testValidateJS(String code) {
            int parens = 0, braces = 0, brackets = 0;
            for (char c : code.toCharArray()) {
                if (c == '(') parens++;
                if (c == ')') parens--;
                if (c == '{') braces++;
                if (c == '}') braces--;
                if (c == '[') brackets++;
                if (c == ']') brackets--;
            }
            return parens == 0 && braces == 0 && brackets == 0;
        }
        
        // Test prompt building
        String testBuildPrompt(String userPrompt, String context, String language) {
            StringBuilder sb = new StringBuilder();
            
            if (!context.isEmpty()) {
                sb.append("Use the following knowledge as reference:\n");
                sb.append(context);
                sb.append("\n");
            }
            
            sb.append("Task: Write a complete, working ").append(language).append(" program for:\n");
            sb.append(userPrompt);
            
            return sb.toString();
        }
        
        // Main self-correcting loop (mocked)
        String createSoftwareMock(String userPrompt, String language, String filename) {
            System.out.println("      >> Requesting code from Mock Ollama...");
            String code = mockCallOllama(userPrompt);
            
            if (code == null) return null;
            
            boolean working = false;
            int maxAttempts = 5;
            
            while (!working && attemptCount < maxAttempts) {
                attemptCount++;
                System.out.println("      >> ATTEMPT " + attemptCount + ": Validating...");
                
                boolean isValid = false;
                switch (language.toLowerCase()) {
                    case "java":
                        isValid = testValidateJava(code);
                        break;
                    case "python":
                        isValid = testValidatePython(code);
                        break;
                    case "javascript":
                        isValid = testValidateJS(code);
                        break;
                    default:
                        isValid = true;
                }
                
                if (isValid) {
                    System.out.println("      ✓ Validation passed!");
                    working = true;
                } else {
                    System.out.println("      ✗ Validation failed - requesting fix...");
                    code = mockCallOllama("Fix the error");
                    if (code == null) break;
                }
            }
            
            return working ? code : null;
        }
    }
}
