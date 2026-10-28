# Web & Document Cortex Design
## Specialized Neural Pathways for Web Content and Document Ingestion

---

## Executive Summary

Create a dedicated brain component (`WebCortex`) with specialized synapses for:
- URL/web content access
- PDF/document scraping
- Knowledge extraction from documents
- Cross-referencing web sources
- Citation tracking
- Fact verification

This addresses the current gap where web scraping works but isn't integrated with cognitive systems.

---

## Problem Statement

**Current State**:
- ✅ Web scraping works (OpenClaw)
- ✅ Document parsing works (Babel)
- ✅ Data persists (LibAbsorber)
- ❌ Data not used for reasoning
- ❌ No integration with cognitive systems
- ❌ Web/document knowledge isolated

**Gap**: The system can ingest web/document data but lacks dedicated neural pathways to process, reason about, and apply this knowledge.

---

## Solution: WebCortex Architecture

### Core Concept

A specialized brain lobe with synapse types designed specifically for:
1. **Web Content Synapses** - URL patterns, HTTP semantics, HTML structure
2. **Document Synapses** - PDF structure, document semantics, citation networks
3. **Knowledge Extraction Synapses** - Entity recognition, fact extraction, relationship mapping
4. **Cross-Reference Synapses** - Source linking, citation tracking, fact verification

### Integration Points

```
┌─────────────────────────────────────────────────────────────┐
│                    FRAYNIX BRAIN SYSTEM                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Prefrontal  │  │   Parietal   │  │  Hippocampus │      │
│  │   Cortex     │  │   Lobe       │  │   (Memory)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐    │
│  │              NEW: WebCortex                         │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │    │
│  │  │  Web    │ │ Document│ │Extract  │ │Cross-   │  │    │
│  │  │Synapses │ │Synapses │ │Synapses │ │Reference│  │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘  │    │
│  └──────────────────────────────────────────────────────┘    │
│           ↕                    ↕                    ↕        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  OpenClaw    │  │    Babel     │  │ LibAbsorber  │      │
│  │  (Acquire)   │  │ (Transmute)  │  │  (Persist)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Phase 1: Foundation (Week 1-2)

### 1.1 WebCortex Core Structure

**File**: `src/main/java/fraymus/neural/WebCortex.java`

```java
package fraymus.neural;

/**
 * WebCortex - Specialized brain lobe for web/document knowledge
 * 
 * Synapse Types:
 * - WebSynapse: URL patterns, HTTP semantics, HTML structure
 * - DocumentSynapse: PDF structure, document semantics
 * - ExtractionSynapse: Entity recognition, fact extraction
 * - CrossReferenceSynapse: Source linking, citation tracking
 */
public class WebCortex {
    
    // Synapse networks
    private Map<String, WebSynapse> webSynapses;
    private Map<String, DocumentSynapse> documentSynapses;
    private Map<String, ExtractionSynapse> extractionSynapses;
    private Map<String, CrossReferenceSynapse> crossReferenceSynapses;
    
    // Knowledge graph
    private WebKnowledgeGraph knowledgeGraph;
    
    // Integration
    private OpenClaw openClaw;
    private Babel babel;
    private LibAbsorber libAbsorber;
    private AkashicRecord akashic;
    private SentientCore sentientCore;
    
    public WebCortex() {
        initializeSynapses();
        initializeKnowledgeGraph();
    }
    
    // Wire to existing components
    public void wireOpenClaw(OpenClaw claw) { this.openClaw = claw; }
    public void wireBabel(Babel babel) { this.babel = babel; }
    public void wireLibAbsorber(LibAbsorber absorber) { this.libAbsorber = absorber; }
    public void wireAkashic(AkashicRecord akashic) { this.akashic = akashic; }
    public void wireSentient(SentientCore sentient) { this.sentientCore = sentient; }
}
```

### 1.2 Synapse Types

**WebSynapse** - URL/web content patterns:
```java
public class WebSynapse {
    private String urlPattern;
    private double relevanceScore;
    private List<String> extractedEntities;
    private Map<String, Object> semanticFeatures;
    private long lastAccessed;
    private int accessCount;
}
```

**DocumentSynapse** - Document structure:
```java
public class DocumentSynapse {
    private String documentId;
    private String documentType; // PDF, HTML, Markdown, etc.
    private Map<String, Object> structure; // sections, headings, etc.
    private List<Citation> citations;
    private Map<String, Entity> entities;
}
```

**ExtractionSynapse** - Knowledge extraction:
```java
public class ExtractionSynapse {
    private String sourceId;
    private List<Fact> extractedFacts;
    private List<Relationship> relationships;
    private double confidence;
}
```

**CrossReferenceSynapse** - Source linking:
```java
public class CrossReferenceSynapse {
    private String sourceId;
    private List<String> referencedSources;
    private List<String> referencingSources;
    private Map<String, Double> similarityScores;
}
```

### 1.3 WebKnowledgeGraph

```java
public class WebKnowledgeGraph {
    private Map<String, WebNode> nodes; // URLs, documents, entities
    private Map<String, WebEdge> edges; // citations, references, similarities
    
    public void addNode(WebNode node);
    public void addEdge(WebEdge edge);
    public List<WebNode> findSimilar(String query);
    public List<WebNode> findCitedBy(String sourceId);
    public List<WebNode> findCiting(String sourceId);
}
```

### 1.4 Integration with FraynixBoot

Add to FraynixBoot.java:
```java
// Phase 20.9: WebCortex - Web/Document Knowledge Processing
System.out.println("🌐 [20.9/24] IGNITING WEB CORTEX...");
webCortex = new WebCortex();
webCortex.wireOpenClaw(acquisitionLayer);
webCortex.wireBabel(translationLayer);
webCortex.wireLibAbsorber(persistenceLayer);
webCortex.wireAkashic(akashic);
webCortex.wireSentient(sentientCore);
System.out.println("   ✓ WebCortex online (specialized web/document synapses)");
```

---

## Phase 2: Web Content Processing (Week 3-4)

### 2.1 URL Pattern Learning

**Capability**: Learn URL patterns and predict content type

```java
public class WebSynapseLearner {
    
    public void learnFromUrl(String url, String contentType, String content) {
        WebSynapse synapse = new WebSynapse();
        synapse.urlPattern = extractPattern(url);
        synapse.semanticFeatures = extractFeatures(content);
        synapse.relevanceScore = calculateRelevance(content);
        
        webCortex.addWebSynapse(synapse);
    }
    
    public String predictContentType(String url) {
        // Use learned patterns to predict content type
    }
    
    public double calculateRelevance(String content) {
        // Calculate relevance based on entities, facts, etc.
    }
}
```

### 2.2 HTML Structure Recognition

**Capability**: Recognize HTML structure and extract semantic sections

```java
public class HtmlStructureAnalyzer {
    
    public Map<String, Object> analyzeStructure(String html) {
        Map<String, Object> structure = new HashMap<>();
        structure.put("headings", extractHeadings(html));
        structure.put("sections", extractSections(html));
        structure.put("links", extractLinks(html));
        structure.put("tables", extractTables(html));
        structure.put("lists", extractLists(html));
        return structure;
    }
}
```

### 2.3 Entity Recognition from Web

**Capability**: Extract entities (people, places, organizations, dates)

```java
public class WebEntityExtractor {
    
    public List<Entity> extractEntities(String content) {
        List<Entity> entities = new ArrayList<>();
        entities.addAll(extractPeople(content));
        entities.addAll(extractPlaces(content));
        entities.addAll(extractOrganizations(content));
        entities.addAll(extractDates(content));
        return entities;
    }
}
```

### 2.4 Fact Extraction

**Capability**: Extract factual statements from web content

```java
public class WebFactExtractor {
    
    public List<Fact> extractFacts(String content) {
        List<Fact> facts = new ArrayList<>();
        // Extract subject-predicate-object triples
        // Extract statistical data
        // Extract definitions
        // Extract procedures
        return facts;
    }
}
```

---

## Phase 3: Document Processing (Week 5-6)

### 3.1 PDF Structure Parsing

**Capability**: Parse PDF structure (sections, headings, paragraphs)

```java
public class PdfStructureParser {
    
    public DocumentSynapse parsePdf(byte[] pdfBytes) {
        DocumentSynapse synapse = new DocumentSynapse();
        synapse.documentType = "PDF";
        synapse.structure = extractStructure(pdfBytes);
        synapse.entities = extractEntities(pdfBytes);
        synapse.citations = extractCitations(pdfBytes);
        return synapse;
    }
}
```

### 3.2 Citation Network Extraction

**Capability**: Extract and track citations between documents

```java
public class CitationNetworkExtractor {
    
    public List<Citation> extractCitations(String document) {
        List<Citation> citations = new ArrayList<>();
        // Extract bibliographic references
        // Extract in-text citations
        // Extract footnotes
        return citations;
    }
    
    public void buildCitationGraph(List<Citation> citations) {
        // Build citation network graph
    }
}
```

### 3.3 Document Similarity

**Capability**: Calculate similarity between documents

```java
public class DocumentSimilarityCalculator {
    
    public double calculateSimilarity(String doc1, String doc2) {
        // Use semantic similarity
        // Use citation overlap
        // Use entity overlap
    }
}
```

---

## Phase 4: Knowledge Integration (Week 7-8)

### 4.1 Knowledge Graph Construction

**Capability**: Build knowledge graph from web/document data

```java
public class WebKnowledgeGraphBuilder {
    
    public void buildFromScrapedData(List<WebContent> scrapedData) {
        for (WebContent content : scrapedData) {
            WebNode node = createNode(content);
            webCortex.getKnowledgeGraph().addNode(node);
            
            // Extract relationships
            List<Relationship> relationships = extractRelationships(content);
            for (Relationship rel : relationships) {
                WebEdge edge = createEdge(rel);
                webCortex.getKnowledgeGraph().addEdge(edge);
            }
        }
    }
}
```

### 4.2 Cross-Reference Tracking

**Capability**: Track cross-references between sources

```java
public class CrossReferenceTracker {
    
    public void trackReference(String sourceId, String referencedId) {
        CrossReferenceSynapse synapse = new CrossReferenceSynapse();
        synapse.sourceId = sourceId;
        synapse.referencedSources.add(referencedId);
        
        webCortex.addCrossReferenceSynapse(synapse);
    }
    
    public List<String> findCitedBy(String sourceId) {
        // Find all sources that cite this source
    }
}
```

### 4.3 Fact Verification

**Capability**: Verify facts against multiple sources

```java
public class FactVerifier {
    
    public VerificationResult verifyFact(Fact fact) {
        List<WebNode> sources = webCortex.getKnowledgeGraph().findSourcesForFact(fact);
        
        int supportingSources = 0;
        int contradictingSources = 0;
        
        for (WebNode source : sources) {
            if (source.supportsFact(fact)) {
                supportingSources++;
            } else {
                contradictingSources++;
            }
        }
        
        return new VerificationResult(fact, supportingSources, contradictingSources);
    }
}
```

---

## Phase 5: Cognitive Integration (Week 9-10)

### 5.1 Integration with SentientCore

**Capability**: Feed web/document knowledge into SentientCore

```java
public class WebCortexSentientBridge {
    
    public void feedToSentientCore(WebContent content) {
        // Extract causal relationships
        List<CausalEdge> causalEdges = extractCausalEdges(content);
        for (CausalEdge edge : causalEdges) {
            sentientCore.getCausalGraph().addEdge(edge);
        }
        
        // Extract concept lattice attributes
        List<LatticeAttribute> attributes = extractLatticeAttributes(content);
        for (LatticeAttribute attr : attributes) {
            sentientCore.getConceptLattice().addAttribute(attr);
        }
        
        // Update statistics
        sentientCore.getStats().observe("web_content_ingested", 1.0);
        sentientCore.getStats().observe("entities_extracted", content.entities.size());
    }
}
```

### 5.2 Integration with UnifiedBrainOrchestrator

**Capability**: Use web/document knowledge in reasoning

```java
public class WebCortexReasoningBridge {
    
    public List<WebSource> findRelevantSources(String query) {
        // Use knowledge graph to find relevant sources
        return webCortex.getKnowledgeGraph().findSimilar(query);
    }
    
    public String augmentReasoning(String originalReasoning, String query) {
        List<WebSource> sources = findRelevantSources(query);
        
        StringBuilder augmented = new StringBuilder(originalReasoning);
        augmented.append("\n\n[SOURCES]\n");
        for (WebSource source : sources) {
            augmented.append("- ").append(source.url).append("\n");
        }
        
        return augmented.toString();
    }
}
```

### 5.3 Query Interface

**Capability**: Query web/document knowledge

```java
public class WebCortexQuery {
    
    public String query(String queryString) {
        // Search knowledge graph
        // Return relevant sources, facts, entities
    }
    
    public List<Fact> queryFacts(String entity) {
        // Find all facts about an entity
    }
    
    public List<WebSource> querySources(String topic) {
        // Find all sources about a topic
    }
}
```

---

## Phase 6: Shell Integration (Week 11)

### 6.1 Web Scraping Commands

Add to FraynixBoot shell:

```
web scrape <url>              - Scrape web content
web query <query>              - Query web knowledge
web facts <entity>             - Query facts about entity
web sources <topic>            - Query sources about topic
web verify <fact>              - Verify fact against sources
web citations <source>         - Show citations for source
web similar <url>              - Find similar web content
```

### 6.2 Document Processing Commands

```
doc parse <file>               - Parse document (PDF, etc.)
doc extract <file>             - Extract entities/facts
doc citations <file>           - Extract citations
doc similar <file1> <file2>    - Calculate similarity
doc graph <topic>              - Show document graph for topic
```

---

## Phase 7: Testing & Validation (Week 12)

### 7.1 Unit Tests

- WebSynapse learning
- DocumentSynapse parsing
- ExtractionSynapse accuracy
- CrossReferenceSynapse tracking
- Knowledge graph operations

### 7.2 Integration Tests

- Full pipeline: Scrape → Extract → Store → Retrieve → Query
- SentientCore integration
- UnifiedBrainOrchestrator integration
- Cross-reference accuracy

### 7.3 Performance Tests

- Large-scale scraping (1000+ URLs)
- Large document processing (100+ PDFs)
- Knowledge graph query performance
- Memory usage

---

## Success Metrics

### Quantitative

- **Web Content Ingestion**: 1000+ URLs processed
- **Document Processing**: 100+ PDFs parsed
- **Entity Extraction**: 10,000+ entities extracted
- **Fact Extraction**: 50,000+ facts extracted
- **Citation Tracking**: 5,000+ citations tracked
- **Query Accuracy**: >80% relevant results
- **Fact Verification**: >70% accuracy

### Qualitative

- Web/document knowledge integrated with cognitive systems
- SentientCore learns from web content
- UnifiedBrainOrchestrator uses web sources in reasoning
- Cross-reference tracking enables fact verification
- Query interface provides relevant web/document knowledge

---

## Risks & Mitigations

### Risk 1: Information Overload
**Mitigation**: Relevance scoring, deduplication, summarization

### Risk 2: False Information
**Mitigation**: Source credibility scoring, fact verification, cross-reference checking

### Risk 3: Performance Issues
**Mitigation**: Async processing, caching, incremental loading

### Risk 4: Memory Issues
**Mitigation**: Disk-based knowledge graph, pruning old data, compression

---

## Future Enhancements

- Real-time web monitoring
- Automatic fact checking
- Knowledge graph visualization
- Collaborative filtering (learn from user feedback)
- Multi-language support
- Image/video content analysis
- API integration (Wikipedia, arXiv, etc.)

---

## Conclusion

The WebCortex with specialized synapses will transform the system from "can ingest web data" to "can reason with web knowledge". This creates dedicated neural pathways for web/document intelligence, enabling the system to:

1. Learn from web content continuously
2. Build knowledge graphs from scraped data
3. Verify facts against multiple sources
4. Use web/document knowledge in reasoning
5. Track citations and cross-references

This is a foundational capability for AGI - the ability to learn from the web and apply that knowledge intelligently.
