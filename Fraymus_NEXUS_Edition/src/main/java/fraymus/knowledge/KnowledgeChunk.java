package fraymus.knowledge;

import fraymus.hyper.HyperVector;

/**
 * Knowledge chunk with metadata
 * 
 * Represents a single piece of knowledge extracted from a document.
 * Contains the original text, its vector representation, and metadata.
 */
public class KnowledgeChunk {
    public String id;
    public String source;
    public String text;
    public HyperVector vector;
    public double similarity;
    
    public KnowledgeChunk(String id, String source, String text, HyperVector vector) {
        this.id = id;
        this.source = source;
        this.text = text;
        this.vector = vector;
        this.similarity = 0.0;
    }
    
    public String getContext() {
        return "Source: " + source + "\n\n" + text;
    }
}
