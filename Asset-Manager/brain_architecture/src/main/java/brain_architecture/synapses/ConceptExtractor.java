package brain_architecture.synapses;

import brain_architecture.synapses.HyperdimensionalMapper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ConceptExtractor - Extracts concepts from content and maps them to HDC vectors.
 * 
 * Uses NLP techniques (placeholder) to identify concepts.
 * Maps concepts to 16,384-D binary vectors using HyperdimensionalMapper.
 */
public class ConceptExtractor {
    
    private final HyperdimensionalMapper hdcMapper;
    
    // Patterns for concept extraction
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-zA-Z]{3,}\\b");
    private static final Pattern DEFINITION_PATTERN = Pattern.compile(
        "([A-Z][a-z]+)\\s+(?:is|are|means|refers to|defined as)\\s+([^.]+)\\."
    );
    
    public ConceptExtractor(HyperdimensionalMapper hdcMapper) {
        this.hdcMapper = hdcMapper;
    }
    
    /**
     * Extract concepts from content.
     */
    public List<Concept> extract(String content) {
        List<Concept> concepts = new ArrayList<>();
        
        // Extract definitions
        concepts.addAll(extractDefinitions(content));
        
        // Extract words as concepts (simple heuristic)
        concepts.addAll(extractWords(content));
        
        return concepts;
    }
    
    /**
     * Extract definitions from content.
     */
    private List<Concept> extractDefinitions(String content) {
        List<Concept> concepts = new ArrayList<>();
        Matcher matcher = DEFINITION_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String name = matcher.group(1);
            String definition = matcher.group(2);
            String category = "definition";
            
            // Map to HDC vector
            BitSet hdcVector = mapToHDC(name);
            
            Concept concept = new Concept(name, definition, category, hdcVector);
            concepts.add(concept);
        }
        
        return concepts;
    }
    
    /**
     * Extract words as concepts.
     */
    private List<Concept> extractWords(String content) {
        List<Concept> concepts = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String word = matcher.group();
            
            // Skip common words (stop words)
            if (isStopWord(word)) {
                continue;
            }
            
            String definition = "Extracted from content";
            String category = "word";
            
            // Map to HDC vector
            BitSet hdcVector = mapToHDC(word);
            
            Concept concept = new Concept(word, definition, category, hdcVector);
            concepts.add(concept);
        }
        
        return concepts;
    }
    
    /**
     * Map a concept to an HDC vector.
     */
    private BitSet mapToHDC(String concept) {
        if (hdcMapper != null) {
            return hdcMapper.map(concept);
        }
        
        // Fallback: simple hash-based mapping
        return hashToBitSet(concept, 16384);
    }
    
    /**
     * Simple hash-based HDC mapping as fallback.
     */
    private BitSet hashToBitSet(String input, int dimension) {
        BitSet vector = new BitSet(dimension);
        int hash = input.hashCode();
        
        // Use hash to set bits
        for (int i = 0; i < dimension; i++) {
            if ((hash & (1 << (i % 32))) != 0) {
                vector.set(i);
            }
        }
        
        return vector;
    }
    
    /**
     * Check if a word is a stop word.
     */
    private boolean isStopWord(String word) {
        String lower = word.toLowerCase();
        return lower.equals("the") || lower.equals("and") || lower.equals("or") ||
               lower.equals("but") || lower.equals("if") || lower.equals("then") ||
               lower.equals("else") || lower.equals("when") || lower.equals("where") ||
               lower.equals("while") || lower.equals("for") || lower.equals("with") ||
               lower.equals("from") || lower.equals("to") || lower.equals("of") ||
               lower.equals("in") || lower.equals("on") || lower.equals("at") ||
               lower.equals("by") || lower.equals("as") || lower.equals("is") ||
               lower.equals("are") || lower.equals("was") || lower.equals("were") ||
               lower.equals("be") || lower.equals("been") || lower.equals("being") ||
               lower.equals("have") || lower.equals("has") || lower.equals("had") ||
               lower.equals("do") || lower.equals("does") || lower.equals("did") ||
               lower.equals("will") || lower.equals("would") || lower.equals("could") ||
               lower.equals("should") || lower.equals("may") || lower.equals("might") ||
               lower.equals("must") || lower.equals("can") || lower.equals("this") ||
               lower.equals("that") || lower.equals("these") || lower.equals("those") ||
               lower.equals("a") || lower.equals("an") || lower.equals("it") ||
               lower.equals("its") || lower.equals("they") || lower.equals("them") ||
               lower.equals("their") || lower.equals("what") || lower.equals("which") ||
               lower.equals("who") || lower.equals("whom") || lower.equals("whose") ||
               lower.equals("how") || lower.equals("why") || lower.equals("when") ||
               lower.equals("where") || lower.equals("there") || lower.equals("here") ||
               lower.equals("all") || lower.equals("any") || lower.equals("some") ||
               lower.equals("no") || lower.equals("not") || lower.equals("only") ||
               lower.equals("own") || lower.equals("same") || lower.equals("so") ||
               lower.equals("than") || lower.equals("too") || lower.equals("very") ||
               lower.equals("just") || lower.equals("also") || lower.equals("now");
    }
}
