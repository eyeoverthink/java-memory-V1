package fraymus.knowledge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * THE AKASHIC RECORD
 * "The sum of all absorbed knowledge."
 */
public class AkashicRecord {
    
    // Map<Category, List<Data>>
    private Map<String, List<String>> knowledgeBase;

    public AkashicRecord() {
        this.knowledgeBase = new HashMap<>();
        System.out.println("   📚 AKASHIC RECORD INITIALIZED.");
    }

    public void addBlock(String category, String data) {
        knowledgeBase.computeIfAbsent(category, k -> new ArrayList<>()).add(data);
        // Only print significant updates to avoid spam
        if (!category.equals("FACT")) { 
            System.out.println("   [AKASHIC] >> Stored [" + category + "]: " + truncate(data, 50));
        }
    }
    
    public List<String> query(String keyword) {
        List<String> results = new ArrayList<>();
        knowledgeBase.forEach((cat, list) -> {
            for (String item : list) {
                if (item.toLowerCase().contains(keyword.toLowerCase())) {
                    results.add("[" + cat + "] " + item);
                }
            }
        });
        return results;
    }

    private String truncate(String s, int len) {
        return s.length() > len ? s.substring(0, len) + "..." : s;
    }
}