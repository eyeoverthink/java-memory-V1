package fraymus.neural;

import java.util.*;

/**
 * CitationNetworkExtractor - Build Citation Networks
 * 
 * Extracts and analyzes citation relationships:
 * - Which documents cite which
 * - Citation frequency
 * - Co-citation analysis
 * - Bibliographic coupling
 */
public class CitationNetworkExtractor {
    
    private final Map<String, Set<String>> citationGraph = new HashMap<>();
    private final Map<String, Integer> citationCount = new HashMap<>();
    private final Map<String, Set<String>> coCitation = new HashMap<>();
    
    public CitationNetworkExtractor() {
        System.out.println("[CitationNetworkExtractor] Initialized");
    }
    
    /**
     * Add document with its citations
     */
    public void addDocument(String docId, List<String> citedDocs) {
        citationGraph.put(docId, new HashSet<>(citedDocs));
        
        // Update citation counts
        for (String cited : citedDocs) {
            citationCount.put(cited, citationCount.getOrDefault(cited, 0) + 1);
        }
        
        // Update co-citation
        for (int i = 0; i < citedDocs.size(); i++) {
            for (int j = i + 1; j < citedDocs.size(); j++) {
                String docA = citedDocs.get(i);
                String docB = citedDocs.get(j);
                
                coCitation.computeIfAbsent(docA, k -> new HashSet<>()).add(docB);
                coCitation.computeIfAbsent(docB, k -> new HashSet<>()).add(docA);
            }
        }
    }
    
    /**
     * Get documents that cite a given document
     */
    public Set<String> getCitedBy(String docId) {
        Set<String> citedBy = new HashSet<>();
        
        for (Map.Entry<String, Set<String>> entry : citationGraph.entrySet()) {
            if (entry.getValue().contains(docId)) {
                citedBy.add(entry.getKey());
            }
        }
        
        return citedBy;
    }
    
    /**
     * Get documents cited by a given document
     */
    public Set<String> getCites(String docId) {
        return citationGraph.getOrDefault(docId, new HashSet<>());
    }
    
    /**
     * Get citation count for a document
     */
    public int getCitationCount(String docId) {
        return citationCount.getOrDefault(docId, 0);
    }
    
    /**
     * Get co-cited documents (documents cited together)
     */
    public Set<String> getCoCited(String docId) {
        return coCitation.getOrDefault(docId, new HashSet<>());
    }
    
    /**
     * Calculate bibliographic coupling (shared citations between two documents)
     */
    public int getBibliographicCoupling(String docA, String docB) {
        Set<String> citesA = getCites(docA);
        Set<String> citesB = getCites(docB);
        
        Set<String> intersection = new HashSet<>(citesA);
        intersection.retainAll(citesB);
        
        return intersection.size();
    }
    
    /**
     * Get most cited documents
     */
    public List<Map.Entry<String, Integer>> getMostCited(int limit) {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(citationCount.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        if (limit > 0 && sorted.size() > limit) {
            return sorted.subList(0, limit);
        }
        
        return sorted;
    }
    
    /**
     * Get network statistics
     */
    public String getStats() {
        int totalDocs = citationGraph.size();
        int totalCitations = citationCount.values().stream().mapToInt(Integer::intValue).sum();
        int avgCitations = totalDocs > 0 ? totalCitations / totalDocs : 0;
        
        return String.format(
            "[CitationNetworkExtractor] Documents: %d, Total citations: %d, Avg citations: %d",
            totalDocs, totalCitations, avgCitations
        );
    }
}
