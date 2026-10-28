package fraymus.neural;

import java.util.*;
import java.util.regex.*;

/**
 * HtmlStructureAnalyzer - Parse HTML Structure
 * 
 * Extracts semantic structure from HTML:
 * - Headings (h1-h6 hierarchy)
 * - Sections (based on headings)
 * - Links (href, text)
 * - Tables (rows, columns)
 * - Lists (ordered, unordered)
 */
public class HtmlStructureAnalyzer {
    
    public HtmlStructureAnalyzer() {
        System.out.println("[HtmlStructureAnalyzer] Initialized");
    }
    
    /**
     * Analyze HTML structure
     */
    public Map<String, Object> analyzeStructure(String html) {
        Map<String, Object> structure = new HashMap<>();
        
        structure.put("headings", extractHeadings(html));
        structure.put("sections", extractSections(html));
        structure.put("links", extractLinks(html));
        structure.put("tables", extractTables(html));
        structure.put("lists", extractLists(html));
        structure.put("paragraphs", extractParagraphs(html));
        
        return structure;
    }
    
    /**
     * Extract headings with hierarchy
     */
    private List<Map<String, Object>> extractHeadings(String html) {
        List<Map<String, Object>> headings = new ArrayList<>();
        
        // Pattern for h1-h6 tags
        Pattern pattern = Pattern.compile("<h([1-6])[^>]*>(.*?)</h\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        
        while (matcher.find()) {
            Map<String, Object> heading = new HashMap<>();
            int level = Integer.parseInt(matcher.group(1));
            String text = matcher.group(2).replaceAll("<[^>]+>", "").trim(); // Remove nested tags
            
            heading.put("level", level);
            heading.put("text", text);
            heading.put("position", matcher.start());
            
            headings.add(heading);
        }
        
        return headings;
    }
    
    /**
     * Extract sections based on headings
     */
    private List<Map<String, Object>> extractSections(String html) {
        List<Map<String, Object>> sections = new ArrayList<>();
        
        List<Map<String, Object>> headings = extractHeadings(html);
        
        for (int i = 0; i < headings.size(); i++) {
            Map<String, Object> heading = headings.get(i);
            Map<String, Object> section = new HashMap<>();
            
            int start = (Integer) heading.get("position");
            int end = (i < headings.size() - 1) ? 
                (Integer) headings.get(i + 1).get("position") : html.length();
            
            String sectionContent = html.substring(start, end);
            
            section.put("heading", heading.get("text"));
            section.put("level", heading.get("level"));
            section.put("content", sectionContent);
            section.put("word_count", sectionContent.split("\\s+").length);
            
            sections.add(section);
        }
        
        return sections;
    }
    
    /**
     * Extract links (href and text)
     */
    private List<Map<String, Object>> extractLinks(String html) {
        List<Map<String, Object>> links = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("<a[^>]+href=[\"']([^\"']+)[\"'][^>]*>(.*?)</a>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        
        while (matcher.find()) {
            Map<String, Object> link = new HashMap<>();
            String href = matcher.group(1);
            String text = matcher.group(2).replaceAll("<[^>]+>", "").trim();
            
            link.put("href", href);
            link.put("text", text);
            link.put("is_external", isExternalLink(href));
            
            links.add(link);
        }
        
        return links;
    }
    
    /**
     * Extract tables
     */
    private List<Map<String, Object>> extractTables(String html) {
        List<Map<String, Object>> tables = new ArrayList<>();
        
        Pattern tablePattern = Pattern.compile("<table[^>]*>(.*?)</table>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher tableMatcher = tablePattern.matcher(html);
        
        while (tableMatcher.find()) {
            Map<String, Object> table = new HashMap<>();
            String tableContent = tableMatcher.group(1);
            
            // Count rows
            Pattern rowPattern = Pattern.compile("<tr[^>]*>", Pattern.CASE_INSENSITIVE);
            Matcher rowMatcher = rowPattern.matcher(tableContent);
            int rowCount = 0;
            while (rowMatcher.find()) rowCount++;
            
            // Count cells
            Pattern cellPattern = Pattern.compile("<t[dh][^>]*>", Pattern.CASE_INSENSITIVE);
            Matcher cellMatcher = cellPattern.matcher(tableContent);
            int cellCount = 0;
            while (cellMatcher.find()) cellCount++;
            
            table.put("row_count", rowCount);
            table.put("cell_count", cellCount);
            table.put("content", tableContent);
            
            tables.add(table);
        }
        
        return tables;
    }
    
    /**
     * Extract lists (ordered and unordered)
     */
    private List<Map<String, Object>> extractLists(String html) {
        List<Map<String, Object>> lists = new ArrayList<>();
        
        // Unordered lists
        Pattern ulPattern = Pattern.compile("<ul[^>]*>(.*?)</ul>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher ulMatcher = ulPattern.matcher(html);
        
        while (ulMatcher.find()) {
            Map<String, Object> list = new HashMap<>();
            String listContent = ulMatcher.group(1);
            
            Pattern liPattern = Pattern.compile("<li[^>]*>(.*?)</li>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher liMatcher = liPattern.matcher(listContent);
            
            List<String> items = new ArrayList<>();
            while (liMatcher.find()) {
                String item = liMatcher.group(1).replaceAll("<[^>]+>", "").trim();
                items.add(item);
            }
            
            list.put("type", "unordered");
            list.put("item_count", items.size());
            list.put("items", items);
            
            lists.add(list);
        }
        
        // Ordered lists
        Pattern olPattern = Pattern.compile("<ol[^>]*>(.*?)</ol>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher olMatcher = olPattern.matcher(html);
        
        while (olMatcher.find()) {
            Map<String, Object> list = new HashMap<>();
            String listContent = olMatcher.group(1);
            
            Pattern liPattern = Pattern.compile("<li[^>]*>(.*?)</li>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher liMatcher = liPattern.matcher(listContent);
            
            List<String> items = new ArrayList<>();
            while (liMatcher.find()) {
                String item = liMatcher.group(1).replaceAll("<[^>]+>", "").trim();
                items.add(item);
            }
            
            list.put("type", "ordered");
            list.put("item_count", items.size());
            list.put("items", items);
            
            lists.add(list);
        }
        
        return lists;
    }
    
    /**
     * Extract paragraphs
     */
    private List<String> extractParagraphs(String html) {
        List<String> paragraphs = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        
        while (matcher.find()) {
            String text = matcher.group(1).replaceAll("<[^>]+>", "").trim();
            if (!text.isEmpty()) {
                paragraphs.add(text);
            }
        }
        
        return paragraphs;
    }
    
    /**
     * Check if link is external
     */
    private boolean isExternalLink(String href) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return true;
        }
        return false;
    }
    
    /**
     * Get structure statistics
     */
    public String getStructureStats(Map<String, Object> structure) {
        List<Map<String, Object>> headings = mapList(structure.get("headings"));
        List<Map<String, Object>> links = mapList(structure.get("links"));
        List<Map<String, Object>> tables = mapList(structure.get("tables"));
        List<Map<String, Object>> lists = mapList(structure.get("lists"));
        List<String> paragraphs = stringList(structure.get("paragraphs"));

        return String.format(
            "[HtmlStructureAnalyzer] Headings: %d, Links: %d, Tables: %d, Lists: %d, Paragraphs: %d",
            headings.size(), links.size(), tables.size(), lists.size(), paragraphs.size()
        );
    }

    private static List<Map<String, Object>> mapList(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                Map<String, Object> casted = new HashMap<>();
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    casted.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                out.add(casted);
            }
        }
        return out;
    }

    private static List<String> stringList(Object value) {
        if (!(value instanceof List<?> list)) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (Object item : list) {
            if (item != null) {
                out.add(String.valueOf(item));
            }
        }
        return out;
    }
}
