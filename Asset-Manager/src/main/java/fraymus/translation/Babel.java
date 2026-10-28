package fraymus.translation;

import fraymus.acquisition.OpenClaw;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Babel - Translation and Normalization Layer
 * 
 * Transmutes acquired content into normalized, structured formats.
 * Handles:
 * - Language translation (future: integration with translation APIs)
 * - Format normalization (HTML → text, markdown → plain text)
 * - Content extraction (remove boilerplate, extract main content)
 * - Encoding normalization (UTF-8 standardization)
 * - Structure parsing (JSON, XML, HTML parsing)
 * 
 * Pipeline position: Acquire (OpenClaw) → Transmute (Babel) → Absorb (Lib Absorber)
 */
public class Babel {
    
    private String defaultEncoding;
    private boolean stripHtml;
    private boolean extractMainContent;
    
    public Babel() {
        this.defaultEncoding = "UTF-8";
        this.stripHtml = true;
        this.extractMainContent = true;
    }
    
    /**
     * Transmutation result structure
     */
    public static class TransmutationResult {
        public String originalSource;
        public String originalType;
        public String normalizedContent;
        public String normalizedType;
        public String encoding;
        public Map<String, String> metadata;
        public List<String> extractedLinks;
        public List<String> extractedEntities;
        public String languageDetected;
        public int wordCount;
        public int charCount;
        public boolean success;
        public String error;
        
        public TransmutationResult() {
            this.metadata = new HashMap<>();
            this.extractedLinks = new ArrayList<>();
            this.extractedEntities = new ArrayList<>();
        }
    }
    
    /**
     * Transmute OpenClaw acquisition result
     */
    public TransmutationResult transmute(OpenClaw.AcquisitionResult acquisition) {
        TransmutationResult result = new TransmutationResult();
        result.originalSource = acquisition.source;
        result.originalType = acquisition.type;
        result.success = acquisition.success;
        result.error = acquisition.error;
        
        if (!acquisition.success) {
            return result;
        }
        
        try {
            // Determine content type and transmute accordingly
            String contentType = acquisition.contentType != null ? acquisition.contentType.toLowerCase() : "text/plain";
            
            if (contentType.contains("html")) {
                result = transmuteHtml(acquisition.content);
            } else if (contentType.contains("json")) {
                result = transmuteJson(acquisition.content);
            } else if (contentType.contains("xml")) {
                result = transmuteXml(acquisition.content);
            } else if (contentType.contains("markdown") || contentType.contains("md")) {
                result = transmuteMarkdown(acquisition.content);
            } else {
                result = transmutePlainText(acquisition.content);
            }
            
            result.originalSource = acquisition.source;
            result.originalType = acquisition.type;
            result.encoding = defaultEncoding;
            result.metadata.putAll(acquisition.metadata);
            
            // Extract links if present
            if (acquisition.content != null) {
                result.extractedLinks = extractLinks(acquisition.content);
            }
            
            // Calculate statistics
            result.charCount = result.normalizedContent != null ? result.normalizedContent.length() : 0;
            result.wordCount = result.normalizedContent != null ? result.normalizedContent.split("\\s+").length : 0;
            
            // Detect language (simple heuristic)
            result.languageDetected = detectLanguage(result.normalizedContent);
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Transmute HTML to plain text
     */
    private TransmutationResult transmuteHtml(String html) {
        TransmutationResult result = new TransmutationResult();
        result.normalizedType = "text/plain";
        
        try {
            String text = html;
            
            // Remove HTML tags
            if (stripHtml) {
                text = text.replaceAll("<[^>]+>", " ");
                // Remove multiple spaces
                text = text.replaceAll("\\s+", " ").trim();
            }
            
            // Remove script and style content
            text = text.replaceAll("(?s)<script[^>]*>.*?</script>", "");
            text = text.replaceAll("(?s)<style[^>]*>.*?</style>", "");
            
            // Decode HTML entities (basic)
            text = text.replaceAll("&nbsp;", " ");
            text = text.replaceAll("&lt;", "<");
            text = text.replaceAll("&gt;", ">");
            text = text.replaceAll("&amp;", "&");
            text = text.replaceAll("&quot;", "\"");
            text = text.replaceAll("&apos;", "'");
            
            // Extract main content (heuristic: keep text between <body> or longest text block)
            if (extractMainContent) {
                text = extractMainContentFromHtml(html, text);
            }
            
            result.normalizedContent = text;
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Extract main content from HTML
     */
    private String extractMainContentFromHtml(String html, String plainText) {
        // Simple heuristic: keep the longest text block
        String[] blocks = plainText.split("\\n\\n+");
        String longestBlock = "";
        int maxLength = 0;
        
        for (String block : blocks) {
            if (block.length() > maxLength && block.trim().length() > 50) {
                maxLength = block.length();
                longestBlock = block;
            }
        }
        
        return longestBlock.isEmpty() ? plainText : longestBlock;
    }
    
    /**
     * Transmute JSON to structured format
     */
    private TransmutationResult transmuteJson(String json) {
        TransmutationResult result = new TransmutationResult();
        result.normalizedType = "application/json";
        
        try {
            // Validate and pretty-print JSON
            if (json != null && !json.trim().isEmpty()) {
                // Basic JSON validation (check for braces)
                if (json.trim().startsWith("{") || json.trim().startsWith("[")) {
                    result.normalizedContent = json;
                    result.metadata.put("json_valid", "true");
                } else {
                    result.normalizedContent = json;
                    result.metadata.put("json_valid", "false");
                }
            } else {
                result.normalizedContent = "";
            }
            
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Transmute XML to structured format
     */
    private TransmutationResult transmuteXml(String xml) {
        TransmutationResult result = new TransmutationResult();
        result.normalizedType = "application/xml";
        
        try {
            // Basic XML validation and normalization
            if (xml != null && !xml.trim().isEmpty()) {
                if (xml.trim().startsWith("<")) {
                    result.normalizedContent = xml;
                    result.metadata.put("xml_valid", "true");
                } else {
                    result.normalizedContent = xml;
                    result.metadata.put("xml_valid", "false");
                }
            } else {
                result.normalizedContent = "";
            }
            
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Transmute Markdown to plain text
     */
    private TransmutationResult transmuteMarkdown(String markdown) {
        TransmutationResult result = new TransmutationResult();
        result.normalizedType = "text/plain";
        
        try {
            String text = markdown;
            
            // Remove markdown syntax
            text = text.replaceAll("#{1,6}\\s+", ""); // Headers
            text = text.replaceAll("\\*\\*([^*]+)\\*\\*", "$1"); // Bold
            text = text.replaceAll("\\*([^*]+)\\*", "$1"); // Italic
            text = text.replaceAll("`([^`]+)`", "$1"); // Code
            text = text.replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1"); // Links
            text = text.replaceAll("!\\[[^\\]]+\\]\\([^\\)]+\\)", "[IMAGE]"); // Images
            text = text.replaceAll("^\\s*[-*+]\\s+", ""); // Lists
            text = text.replaceAll("^\\s*\\d+\\.\\s+", ""); // Numbered lists
            
            // Remove code blocks
            text = text.replaceAll("```[\\s\\S]*?```", "[CODE BLOCK]");
            
            result.normalizedContent = text;
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Transmute plain text (normalize whitespace, encoding)
     */
    private TransmutationResult transmutePlainText(String text) {
        TransmutationResult result = new TransmutationResult();
        result.normalizedType = "text/plain";
        
        try {
            if (text != null) {
                // Normalize whitespace
                String normalized = text.replaceAll("\\s+", " ").trim();
                // Normalize line endings
                normalized = normalized.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
                
                result.normalizedContent = normalized;
            } else {
                result.normalizedContent = "";
            }
            
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Extract links from content
     */
    private List<String> extractLinks(String content) {
        List<String> links = new ArrayList<>();
        
        if (content == null) {
            return links;
        }
        
        // Extract HTTP/HTTPS URLs
        Pattern urlPattern = Pattern.compile("https?://[^\\s<>\"']+");
        Matcher matcher = urlPattern.matcher(content);
        
        while (matcher.find()) {
            String url = matcher.group();
            if (!links.contains(url)) {
                links.add(url);
            }
        }
        
        return links;
    }
    
    /**
     * Detect language (simple heuristic based on common words)
     */
    private String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }
        
        String lowerText = text.toLowerCase();
        
        // Simple language detection based on common words
        if (lowerText.contains("the ") && lowerText.contains("and ") && lowerText.contains("is ")) {
            return "english";
        } else if (lowerText.contains("el ") && lowerText.contains("la ") && lowerText.contains("de ")) {
            return "spanish";
        } else if (lowerText.contains("le ") && lowerText.contains("la ") && lowerText.contains("et ")) {
            return "french";
        } else if (lowerText.contains("der ") && lowerText.contains("die ") && lowerText.contains("und ")) {
            return "german";
        }
        
        return "unknown";
    }
    
    /**
     * Transmute multiple acquisition results
     */
    public List<TransmutationResult> transmuteMultiple(List<OpenClaw.AcquisitionResult> acquisitions) {
        List<TransmutationResult> results = new ArrayList<>();
        
        for (OpenClaw.AcquisitionResult acquisition : acquisitions) {
            results.add(transmute(acquisition));
        }
        
        return results;
    }
    
    /**
     * Set default encoding
     */
    public void setDefaultEncoding(String encoding) {
        this.defaultEncoding = encoding;
    }
    
    /**
     * Enable/disable HTML stripping
     */
    public void setStripHtml(boolean strip) {
        this.stripHtml = strip;
    }
    
    /**
     * Enable/disable main content extraction
     */
    public void setExtractMainContent(boolean extract) {
        this.extractMainContent = extract;
    }
}
