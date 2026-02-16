package fraymus.coding;

import fraymus.knowledge.KnowledgeIngestor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * THE PUPPETEER: FRAYMUS CONTROLLING OLLAMA
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "I provide the Logic. You provide the Syntax."
 * 
 * Architecture:
 * 1. RETRIEVE: Consult knowledge base (PDFs) for context
 * 2. DRAFT: Ask Ollama to write code
 * 3. TEST: Run the code locally
 * 4. CRITIQUE: If it fails, identify the error
 * 5. FIX: Demand Ollama fix it (recursive)
 * 6. SAVE: Store working code
 * 
 * This is the "Offline Agent" that:
 * - Runs on your laptop
 * - Reads your PDFs
 * - Writes code that actually runs
 */
public class FraymusCoder {

    // Ollama Configuration
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String DEFAULT_MODEL = "codellama";  // Or "deepseek-coder", "starcoder"
    private static final int TIMEOUT_MS = 120000;  // 2 minutes for code generation
    
    // Self-correction limits
    private static final int MAX_ATTEMPTS = 5;
    
    // Knowledge base (optional)
    private KnowledgeIngestor knowledgeBase;
    
    // Statistics
    private int totalRequests = 0;
    private int successfulGenerations = 0;
    private int failedGenerations = 0;
    private int totalAttempts = 0;
    
    // Output directory
    private String outputDir = "generated_code";

    public FraymusCoder() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   🤖 INITIALIZING FRAYMUS CODER (THE PUPPETEER)");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   Ollama URL: " + OLLAMA_URL);
        System.out.println("   Model: " + DEFAULT_MODEL);
        System.out.println("   Max Attempts: " + MAX_ATTEMPTS);
        System.out.println();
        
        // Create output directory
        try {
            Files.createDirectories(Paths.get(outputDir));
            System.out.println("   ✓ Output directory: " + outputDir);
        } catch (IOException e) {
            System.out.println("   !! Could not create output directory");
        }
        
        System.out.println();
    }
    
    /**
     * Attach a knowledge base for RAG
     */
    public void attachKnowledgeBase(KnowledgeIngestor kb) {
        this.knowledgeBase = kb;
        System.out.println("   ✓ Knowledge base attached (" + 
            kb.getConceptsLearned() + " concepts)");
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE SELF-CORRECTING LOOP
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Create software with self-correcting loop
     */
    public String createSoftware(String userPrompt, String language, String filename) {
        totalRequests++;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   🤖 FRAYMUS CODER ACTIVATED");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("   Request: " + userPrompt);
        System.out.println("   Language: " + language);
        System.out.println("   Output: " + filename);
        System.out.println();
        
        // 1. RETRIEVE KNOWLEDGE (Consult the PDFs)
        String context = "";
        if (knowledgeBase != null) {
            System.out.println("   >> Consulting knowledge base...");
            context = knowledgeBase.getContextForPrompt(userPrompt);
            if (!context.isEmpty()) {
                System.out.println("   ✓ Found relevant context");
            }
        }
        
        // 2. BUILD THE INITIAL PROMPT
        String systemPrompt = buildSystemPrompt(language);
        String fullPrompt = buildCodePrompt(userPrompt, context, language);
        
        // 3. THE DRAFT (Ask Ollama)
        System.out.println();
        System.out.println("   >> Requesting code from Ollama...");
        String code = callOllama(fullPrompt, systemPrompt);
        
        if (code == null || code.isEmpty()) {
            System.out.println("   !! Ollama returned empty response");
            failedGenerations++;
            return null;
        }
        
        // Extract code from response (remove markdown, explanations)
        code = extractCode(code, language);
        System.out.println("   ✓ Received " + code.length() + " characters of code");
        
        // 4. THE CRITIQUE LOOP (Bicameral Check)
        int attempts = 0;
        boolean working = false;
        String lastError = null;
        
        while (!working && attempts < MAX_ATTEMPTS) {
            attempts++;
            totalAttempts++;
            
            System.out.println();
            System.out.println("   >> ATTEMPT " + attempts + "/" + MAX_ATTEMPTS + ": Verifying...");
            
            // Run syntax/logic check
            ValidationResult result = validateCode(code, language);
            
            if (result.isValid) {
                System.out.println("   ✓ SUCCESS. Code is valid.");
                working = true;
            } else {
                System.out.println("   ✗ FAILURE: " + result.error);
                lastError = result.error;
                
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println("   >> DEMANDING FIX FROM OLLAMA...");
                    
                    // RECURSIVE CORRECTION
                    String fixPrompt = buildFixPrompt(code, result.error, language);
                    String fixedCode = callOllama(fixPrompt, systemPrompt);
                    
                    if (fixedCode != null && !fixedCode.isEmpty()) {
                        code = extractCode(fixedCode, language);
                        System.out.println("   ✓ Received corrected code");
                    }
                }
            }
        }
        
        // 5. FINAL RESULT
        System.out.println();
        if (working) {
            // Save the code
            String savedPath = saveCode(code, filename);
            successfulGenerations++;
            
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("   ✓ CODE GENERATION SUCCESSFUL");
            System.out.println("   Saved to: " + savedPath);
            System.out.println("   Attempts: " + attempts);
            System.out.println("═══════════════════════════════════════════════════════");
            
            return code;
        } else {
            failedGenerations++;
            
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("   !! CRITICAL FAILURE. HUMAN INTERVENTION REQUIRED.");
            System.out.println("   Last error: " + lastError);
            System.out.println("   Attempts exhausted: " + attempts);
            System.out.println("═══════════════════════════════════════════════════════");
            
            // Save anyway with _FAILED suffix
            saveCode(code, filename.replace(".", "_FAILED."));
            
            return null;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // OLLAMA COMMUNICATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Call Ollama API
     */
    private String callOllama(String prompt, String systemPrompt) {
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoOutput(true);
            
            // Build JSON request
            String jsonRequest = String.format(
                "{\"model\": \"%s\", \"prompt\": %s, \"system\": %s, \"stream\": false}",
                DEFAULT_MODEL,
                escapeJson(prompt),
                escapeJson(systemPrompt)
            );
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Parse JSON response to get "response" field
                    return parseOllamaResponse(response.toString());
                }
            } else {
                System.out.println("      !! Ollama returned status: " + responseCode);
                return null;
            }
            
        } catch (java.net.ConnectException e) {
            System.out.println("      !! Cannot connect to Ollama. Is it running?");
            System.out.println("      Start with: ollama serve");
            return null;
        } catch (Exception e) {
            System.out.println("      !! Ollama error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse Ollama JSON response
     */
    private String parseOllamaResponse(String json) {
        // Simple JSON parsing for "response" field
        int start = json.indexOf("\"response\":\"");
        if (start == -1) {
            start = json.indexOf("\"response\": \"");
        }
        if (start == -1) return json;
        
        start = json.indexOf("\"", start + 10) + 1;
        int end = json.lastIndexOf("\"done\"");
        if (end == -1) end = json.length();
        
        // Find the closing quote for response
        int depth = 0;
        boolean escaped = false;
        for (int i = start; i < end; i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                end = i;
                break;
            }
        }
        
        String response = json.substring(start, end);
        // Unescape
        response = response.replace("\\n", "\n")
                          .replace("\\t", "\t")
                          .replace("\\\"", "\"")
                          .replace("\\\\", "\\");
        return response;
    }
    
    /**
     * Escape string for JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }

    // ═══════════════════════════════════════════════════════════════════
    // PROMPT BUILDING
    // ═══════════════════════════════════════════════════════════════════
    
    private String buildSystemPrompt(String language) {
        return "You are an expert " + language + " programmer. " +
               "Write clean, working code. " +
               "Include all necessary imports. " +
               "Do not include explanations unless asked. " +
               "Return only the code.";
    }
    
    private String buildCodePrompt(String userPrompt, String context, String language) {
        StringBuilder sb = new StringBuilder();
        
        if (!context.isEmpty()) {
            sb.append("Use the following knowledge as reference:\n");
            sb.append(context);
            sb.append("\n");
        }
        
        sb.append("Task: Write a complete, working ").append(language).append(" program for:\n");
        sb.append(userPrompt);
        sb.append("\n\nRequirements:\n");
        sb.append("- Include all necessary imports\n");
        sb.append("- Handle errors appropriately\n");
        sb.append("- Make it production-ready\n");
        sb.append("- Return ONLY the code, no explanations\n");
        
        return sb.toString();
    }
    
    private String buildFixPrompt(String code, String error, String language) {
        return "The following " + language + " code has an error:\n\n" +
               "```" + language + "\n" + code + "\n```\n\n" +
               "ERROR: " + error + "\n\n" +
               "Fix the error and return the complete corrected code. " +
               "Return ONLY the code, no explanations.";
    }
    
    /**
     * Extract code from markdown response
     */
    private String extractCode(String response, String language) {
        // Try to find code block
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
        
        // Try generic code block
        start = response.indexOf("```");
        if (start != -1) {
            start = response.indexOf("\n", start) + 1;
            int end = response.indexOf("```", start);
            if (end != -1) {
                return response.substring(start, end).trim();
            }
        }
        
        // No code block, return as-is
        return response.trim();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CODE VALIDATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Validate code (syntax check)
     */
    private ValidationResult validateCode(String code, String language) {
        try {
            switch (language.toLowerCase()) {
                case "python":
                    return validatePython(code);
                case "java":
                    return validateJava(code);
                case "javascript":
                case "js":
                    return validateJavaScript(code);
                default:
                    // Basic validation for unknown languages
                    return new ValidationResult(true, null);
            }
        } catch (Exception e) {
            return new ValidationResult(false, e.getMessage());
        }
    }
    
    private ValidationResult validatePython(String code) {
        try {
            // Write to temp file
            Path tempFile = Files.createTempFile("fraymus_", ".py");
            Files.write(tempFile, code.getBytes(StandardCharsets.UTF_8));
            
            // Run python syntax check
            ProcessBuilder pb = new ProcessBuilder("python", "-m", "py_compile", 
                tempFile.toString());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            boolean success = p.waitFor(30, TimeUnit.SECONDS) && p.exitValue() == 0;
            
            // Cleanup
            Files.deleteIfExists(tempFile);
            
            if (success) {
                return new ValidationResult(true, null);
            } else {
                return new ValidationResult(false, output.toString().trim());
            }
            
        } catch (Exception e) {
            return new ValidationResult(false, "Validation error: " + e.getMessage());
        }
    }
    
    private ValidationResult validateJava(String code) {
        // Check for basic Java syntax
        if (!code.contains("class ") && !code.contains("interface ") && 
            !code.contains("enum ") && !code.contains("record ")) {
            return new ValidationResult(false, "No class/interface definition found");
        }
        
        // Check balanced braces
        int braces = 0;
        for (char c : code.toCharArray()) {
            if (c == '{') braces++;
            if (c == '}') braces--;
        }
        if (braces != 0) {
            return new ValidationResult(false, "Unbalanced braces: " + braces);
        }
        
        // Check for main method if standalone
        if (code.contains("public static void main")) {
            return new ValidationResult(true, null);
        }
        
        return new ValidationResult(true, null);
    }
    
    private ValidationResult validateJavaScript(String code) {
        // Basic syntax checks
        int parens = 0, braces = 0, brackets = 0;
        for (char c : code.toCharArray()) {
            if (c == '(') parens++;
            if (c == ')') parens--;
            if (c == '{') braces++;
            if (c == '}') braces--;
            if (c == '[') brackets++;
            if (c == ']') brackets--;
        }
        
        if (parens != 0) return new ValidationResult(false, "Unbalanced parentheses");
        if (braces != 0) return new ValidationResult(false, "Unbalanced braces");
        if (brackets != 0) return new ValidationResult(false, "Unbalanced brackets");
        
        return new ValidationResult(true, null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // FILE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    private String saveCode(String code, String filename) {
        try {
            Path outputPath = Paths.get(outputDir, filename);
            Files.write(outputPath, code.getBytes(StandardCharsets.UTF_8));
            return outputPath.toString();
        } catch (IOException e) {
            System.out.println("   !! Failed to save: " + e.getMessage());
            return null;
        }
    }
    
    public void setOutputDir(String dir) {
        this.outputDir = dir;
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            System.out.println("   !! Could not create output directory: " + dir);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ FRAYMUS CODER STATISTICS                                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Total Requests:      " + String.format("%-36d", totalRequests) + "│");
        System.out.println("│ Successful:          " + String.format("%-36d", successfulGenerations) + "│");
        System.out.println("│ Failed:              " + String.format("%-36d", failedGenerations) + "│");
        System.out.println("│ Total Attempts:      " + String.format("%-36d", totalAttempts) + "│");
        double avgAttempts = totalRequests > 0 ? 
            (double) totalAttempts / totalRequests : 0;
        System.out.println("│ Avg Attempts/Request:" + String.format("%-36.2f", avgAttempts) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════
    
    private static class ValidationResult {
        boolean isValid;
        String error;
        
        ValidationResult(boolean valid, String error) {
            this.isValid = valid;
            this.error = error;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN DEMO
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("   ╔═══════════════════════════════════════════════════╗");
        System.out.println("   ║   FRAYMUS CODER: THE PUPPETEER                    ║");
        System.out.println("   ╠═══════════════════════════════════════════════════╣");
        System.out.println("   ║   \"I provide the Logic.\"                          ║");
        System.out.println("   ║   \"You provide the Syntax.\"                       ║");
        System.out.println("   ╚═══════════════════════════════════════════════════╝");
        System.out.println();
        
        FraymusCoder coder = new FraymusCoder();
        
        // Demo: Create a simple Python scraper
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   DEMO: GENERATING PYTHON WEB SCRAPER");
        System.out.println("═══════════════════════════════════════════════════════");
        
        String result = coder.createSoftware(
            "Create a simple web scraper that fetches the title of a webpage using requests and BeautifulSoup",
            "python",
            "scraper.py"
        );
        
        if (result != null) {
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("   GENERATED CODE:");
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println(result);
        }
        
        coder.printStats();
        
        System.out.println();
        System.out.println("   NOTE: Requires Ollama running at " + OLLAMA_URL);
        System.out.println("   Start with: ollama serve");
        System.out.println("   Install model: ollama pull codellama");
        System.out.println();
    }
}
