package fraymus.translation;

import fraymus.acquisition.OpenClaw;

/**
 * Babel Test - Translation and Normalization Layer
 * Demonstrates transmutation of acquired content into normalized formats
 */
public class BabelTest {
    
    public static void main(String[] args) {
        System.out.println("=== Babel Translation Layer Test ===\n");
        
        // Test 1: HTML transmutation
        testHtmlTransmutation();
        
        // Test 2: Markdown transmutation
        testMarkdownTransmutation();
        
        // Test 3: Plain text transmutation
        testPlainTextTransmutation();
        
        // Test 4: JSON transmutation
        testJsonTransmutation();
        
        // Test 5: Pipeline integration (OpenClaw → Babel)
        testPipelineIntegration();
        
        System.out.println("\n=== All Tests Complete ===");
    }
    
    private static void testHtmlTransmutation() {
        System.out.println("Test 1: HTML Transmutation");
        
        Babel babel = new Babel();
        
        String html = "<html><head><title>Test</title></head>" +
                      "<body><h1>Hello World</h1><p>This is a <strong>test</strong> paragraph.</p>" +
                      "<script>alert('ignore me');</script></body></html>";
        
        OpenClaw.AcquisitionResult acquisition = new OpenClaw.AcquisitionResult();
        acquisition.source = "test.html";
        acquisition.type = "web";
        acquisition.contentType = "text/html";
        acquisition.content = html;
        acquisition.success = true;
        
        Babel.TransmutationResult result = babel.transmute(acquisition);
        
        System.out.println("Original type: " + result.originalType);
        System.out.println("Normalized type: " + result.normalizedType);
        System.out.println("Success: " + result.success);
        System.out.println("Word count: " + result.wordCount);
        System.out.println("Language detected: " + result.languageDetected);
        System.out.println("Normalized content (first 200 chars):");
        System.out.println("  " + result.normalizedContent.substring(0, Math.min(200, result.normalizedContent.length())));
        System.out.println();
    }
    
    private static void testMarkdownTransmutation() {
        System.out.println("Test 2: Markdown Transmutation");
        
        Babel babel = new Babel();
        
        String markdown = "# Header\n\nThis is **bold** and *italic* text.\n\n" +
                         "## Subheader\n\n- Item 1\n- Item 2\n\n" +
                         "```code block```";
        
        OpenClaw.AcquisitionResult acquisition = new OpenClaw.AcquisitionResult();
        acquisition.source = "test.md";
        acquisition.type = "file";
        acquisition.contentType = "text/markdown";
        acquisition.content = markdown;
        acquisition.success = true;
        
        Babel.TransmutationResult result = babel.transmute(acquisition);
        
        System.out.println("Original type: " + result.originalType);
        System.out.println("Normalized type: " + result.normalizedType);
        System.out.println("Success: " + result.success);
        System.out.println("Word count: " + result.wordCount);
        System.out.println("Normalized content:");
        System.out.println("  " + result.normalizedContent);
        System.out.println();
    }
    
    private static void testPlainTextTransmutation() {
        System.out.println("Test 3: Plain Text Transmutation");
        
        Babel babel = new Babel();
        
        String text = "This  is   a    test\n\nWith  irregular\nwhitespace  ";
        
        OpenClaw.AcquisitionResult acquisition = new OpenClaw.AcquisitionResult();
        acquisition.source = "test.txt";
        acquisition.type = "file";
        acquisition.contentType = "text/plain";
        acquisition.content = text;
        acquisition.success = true;
        
        Babel.TransmutationResult result = babel.transmute(acquisition);
        
        System.out.println("Original type: " + result.originalType);
        System.out.println("Normalized type: " + result.normalizedType);
        System.out.println("Success: " + result.success);
        System.out.println("Original: '" + text + "'");
        System.out.println("Normalized: '" + result.normalizedContent + "'");
        System.out.println();
    }
    
    private static void testJsonTransmutation() {
        System.out.println("Test 4: JSON Transmutation");
        
        Babel babel = new Babel();
        
        String json = "{\"name\": \"test\", \"value\": 123, \"nested\": {\"key\": \"value\"}}";
        
        OpenClaw.AcquisitionResult acquisition = new OpenClaw.AcquisitionResult();
        acquisition.source = "test.json";
        acquisition.type = "file";
        acquisition.contentType = "application/json";
        acquisition.content = json;
        acquisition.success = true;
        
        Babel.TransmutationResult result = babel.transmute(acquisition);
        
        System.out.println("Original type: " + result.originalType);
        System.out.println("Normalized type: " + result.normalizedType);
        System.out.println("Success: " + result.success);
        System.out.println("JSON valid: " + result.metadata.get("json_valid"));
        System.out.println("Normalized content:");
        System.out.println("  " + result.normalizedContent);
        System.out.println();
    }
    
    private static void testPipelineIntegration() {
        System.out.println("Test 5: Pipeline Integration (OpenClaw → Babel)");
        
        OpenClaw claw = new OpenClaw();
        Babel babel = new Babel();
        
        // Acquire from web
        System.out.println("Step 1: Acquire from web...");
        OpenClaw.AcquisitionResult acquisition = claw.acquireWeb("https://example.com");
        
        System.out.println("  Acquisition success: " + acquisition.success);
        System.out.println("  Content type: " + acquisition.contentType);
        System.out.println("  Size: " + acquisition.size + " bytes");
        
        if (acquisition.success) {
            // Transmute with Babel
            System.out.println("\nStep 2: Transmute with Babel...");
            Babel.TransmutationResult result = babel.transmute(acquisition);
            
            System.out.println("  Transmutation success: " + result.success);
            System.out.println("  Normalized type: " + result.normalizedType);
            System.out.println("  Word count: " + result.wordCount);
            System.out.println("  Language detected: " + result.languageDetected);
            System.out.println("  Links extracted: " + result.extractedLinks.size());
            
            if (result.extractedLinks.size() > 0) {
                System.out.println("  First link: " + result.extractedLinks.get(0));
            }
            
            System.out.println("\nNormalized content (first 300 chars):");
            System.out.println("  " + result.normalizedContent.substring(0, Math.min(300, result.normalizedContent.length())));
        }
        
        System.out.println();
    }
}
