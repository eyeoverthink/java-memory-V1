package fraymus.neural;

import java.util.*;
import java.util.regex.*;

/**
 * PdfStructureParser - Parse PDF Structure
 * 
 * Extracts document structure from PDF:
 * - Sections and headings
 * - Paragraphs
 * - Entities (basic extraction from text)
 * - Citations (basic pattern matching)
 * 
 * Note: This is a simplified implementation that works with PDF text content.
 * Full PDF parsing requires Apache PDFBox or similar library.
 */
public class PdfStructureParser {
    
    public PdfStructureParser() {
        System.out.println("[PdfStructureParser] Initialized");
    }
    
    /**
     * Parse PDF structure from text content
     * 
     * @param pdfText Text extracted from PDF
     * @return DocumentSynapse with structure
     */
    public DocumentSynapse parsePdf(String pdfText) {
        DocumentSynapse synapse = new DocumentSynapse("pdf-" + UUID.randomUUID().toString().substring(0, 8), "PDF", "extracted");
        
        // Extract sections (based on numbered headings)
        List<Map<String, Object>> sections = extractSections(pdfText);
        for (Map<String, Object> section : sections) {
            synapse.addStructureElement("section_" + synapse.getStructure().size(), section);
        }
        
        // Extract paragraphs
        List<String> paragraphs = extractParagraphs(pdfText);
        synapse.addStructureElement("paragraph_count", paragraphs.size());
        
        // Extract entities (basic)
        List<Entity> entities = extractEntitiesFromText(pdfText);
        for (Entity entity : entities) {
            synapse.addEntity(entity);
        }
        
        // Extract citations (basic pattern)
        List<DocumentSynapse.Citation> citations = extractCitations(pdfText);
        for (DocumentSynapse.Citation citation : citations) {
            synapse.addCitation(citation);
        }
        
        return synapse;
    }
    
    /**
     * Extract sections based on numbered headings
     */
    private List<Map<String, Object>> extractSections(String text) {
        List<Map<String, Object>> sections = new ArrayList<>();
        
        // Pattern for numbered headings: 1. Title, 2. Subtitle, etc.
        Pattern pattern = Pattern.compile("(?m)^(\\d+\\.\\s+)(.+)$");
        Matcher matcher = pattern.matcher(text);
        
        int lastEnd = 0;
        while (matcher.find()) {
            Map<String, Object> section = new HashMap<>();
            int number = Integer.parseInt(matcher.group(1).replace(". ", ""));
            String title = matcher.group(2).trim();
            
            section.put("number", number);
            section.put("title", title);
            section.put("position", matcher.start());
            
            // Extract section content (from this heading to next heading or end)
            int start = matcher.end();
            int end = text.length();
            
            // Find next heading
            Matcher nextMatcher = pattern.matcher(text.substring(matcher.end()));
            if (nextMatcher.find()) {
                end = matcher.end() + nextMatcher.start();
            }
            
            String content = text.substring(start, end).trim();
            section.put("content", content);
            section.put("word_count", content.split("\\s+").length);
            
            sections.add(section);
        }
        
        return sections;
    }
    
    /**
     * Extract paragraphs
     */
    private List<String> extractParagraphs(String text) {
        List<String> paragraphs = new ArrayList<>();
        
        // Split by double newlines
        String[] parts = text.split("\n\n+");
        
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() > 20) { // Filter out very short lines
                paragraphs.add(trimmed);
            }
        }
        
        return paragraphs;
    }
    
    /**
     * Extract entities from text (basic capitalized word extraction)
     */
    private List<Entity> extractEntitiesFromText(String text) {
        List<Entity> entities = new ArrayList<>();
        
        // Extract capitalized words (2+ consecutive)
        Pattern pattern = Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)+)\\b");
        Matcher matcher = pattern.matcher(text);
        
        Set<String> seen = new HashSet<>();
        while (matcher.find()) {
            String name = matcher.group(1);
            if (!seen.contains(name)) {
                Entity entity = new Entity(name, "PERSON"); // Default to PERSON for now
                entities.add(entity);
                seen.add(name);
            }
        }
        
        return entities;
    }
    
    /**
     * Extract citations (basic pattern matching)
     */
    private List<DocumentSynapse.Citation> citations = new ArrayList<>();
    
    private List<DocumentSynapse.Citation> extractCitations(String text) {
        citations.clear();
        
        // Pattern for [1], [2], etc.
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(text);
        
        Set<String> seen = new HashSet<>();
        while (matcher.find()) {
            String ref = matcher.group(1);
            if (!seen.contains(ref)) {
                DocumentSynapse.Citation citation = new DocumentSynapse.Citation("ref-" + ref, "bibliographic", "");
                citations.add(citation);
                seen.add(ref);
            }
        }
        
        // Pattern for (Author, Year)
        Pattern authorYearPattern = Pattern.compile("\\(([A-Z][a-z]+,\\s+\\d{4})\\)");
        Matcher ayMatcher = authorYearPattern.matcher(text);
        
        // Also try pattern for Author (Year)
        Pattern authorYearPattern2 = Pattern.compile("([A-Z][a-z]+)\\s+\\(\\d{4}\\)");
        Matcher ayMatcher2 = authorYearPattern2.matcher(text);
        
        while (ayMatcher.find()) {
            String ref = ayMatcher.group(1);
            if (!seen.contains(ref)) {
                DocumentSynapse.Citation citation = new DocumentSynapse.Citation(ref, "in-text", "");
                citations.add(citation);
                seen.add(ref);
            }
        }
        
        while (ayMatcher2.find()) {
            String ref = ayMatcher2.group(1);
            if (!seen.contains(ref)) {
                DocumentSynapse.Citation citation = new DocumentSynapse.Citation(ref, "in-text", "");
                citations.add(citation);
                seen.add(ref);
            }
        }
        
        return citations;
    }
    
    /**
     * Get statistics
     */
    public String getStats(DocumentSynapse synapse) {
        return String.format(
            "[PdfStructureParser] Sections: %d, Paragraphs: %d, Entities: %d, Citations: %d",
            synapse.getStructure().size(),
            synapse.getStructure().containsKey("paragraph_count") ? (Integer) synapse.getStructure().get("paragraph_count") : 0,
            synapse.getEntities().size(),
            synapse.getCitations().size()
        );
    }
}
