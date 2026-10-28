package brain_architecture.synapses;

import brain_architecture.brains.MemoryBrain;
import brain_architecture.communication.PhiResonancePattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * KnowledgeScraper - Coordinates knowledge ingestion from external sources.
 * 
 * Scrapes files, PDFs, markdown, code files, URLs, and GitHub repositories.
 * Extracts concepts from content and creates synaptic connections between them.
 * Integrates with MemoryBrain for persistent storage.
 */
public class KnowledgeScraper {
    
    private final MemoryBrain memoryBrain;
    private final HyperdimensionalMapper hdcMapper;
    private final HDCSimilarityEngine hdcEngine;
    private final ExecutorService executorService;
    
    // Parsers
    private final PDFParser pdfParser;
    private final MarkdownParser markdownParser;
    private final CodeParser codeParser;
    private final URLScraper urlScraper;
    private final GitHubScraper githubScraper;
    
    // Concept extraction
    private final ConceptExtractor conceptExtractor;
    
    // Synaptic connections
    private final SynapseManager synapseManager;
    
    public KnowledgeScraper(MemoryBrain memoryBrain, 
                           HyperdimensionalMapper hdcMapper,
                           HDCSimilarityEngine hdcEngine) {
        this.memoryBrain = memoryBrain;
        this.hdcMapper = hdcMapper;
        this.hdcEngine = hdcEngine;
        this.executorService = Executors.newFixedThreadPool(4);
        
        // Initialize parsers
        this.pdfParser = new PDFParser();
        this.markdownParser = new MarkdownParser();
        this.codeParser = new CodeParser();
        this.urlScraper = new URLScraper();
        this.githubScraper = new GitHubScraper();
        
        // Initialize concept extraction
        this.conceptExtractor = new ConceptExtractor(hdcMapper);
        
        // Initialize synapse manager
        this.synapseManager = new SynapseManager();
    }
    
    /**
     * Scrape a single file and extract knowledge.
     */
    public CompletableFuture<KnowledgeGraph> scrapeFile(Path filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String content = readFileContent(filePath);
                String extension = getFileExtension(filePath);
                
                String extractedContent;
                switch (extension.toLowerCase()) {
                    case "pdf":
                        extractedContent = pdfParser.parse(filePath);
                        break;
                    case "md":
                        extractedContent = markdownParser.parse(content);
                        break;
                    case "java":
                    case "py":
                    case "js":
                    case "ts":
                    case "cpp":
                    case "c":
                        extractedContent = codeParser.parse(content, extension);
                        break;
                    default:
                        extractedContent = content; // Plain text
                }
                
                List<Concept> concepts = conceptExtractor.extract(extractedContent);
                synapseManager.createSynapticConnections(concepts);
                
                KnowledgeGraph graph = new KnowledgeGraph(filePath.toString(), concepts);
                integrateWithMemoryBrain(graph);
                
                return graph;
            } catch (IOException e) {
                System.err.println("Error scraping file: " + filePath + " - " + e.getMessage());
                return new KnowledgeGraph(filePath.toString(), Collections.emptyList());
            }
        }, executorService);
    }
    
    /**
     * Scrape a directory recursively.
     */
    public CompletableFuture<KnowledgeGraph> scrapeDirectory(Path directoryPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Path> files = Files.walk(directoryPath)
                    .filter(Files::isRegularFile)
                    .toList();
                
                List<Concept> allConcepts = new ArrayList<>();
                List<String> sources = new ArrayList<>();
                
                for (Path file : files) {
                    try {
                        KnowledgeGraph fileGraph = scrapeFile(file).get();
                        allConcepts.addAll(fileGraph.getConcepts());
                        sources.add(file.toString());
                    } catch (Exception e) {
                        System.err.println("Error scraping file in directory: " + file + " - " + e.getMessage());
                    }
                }
                
                synapseManager.createSynapticConnections(allConcepts);
                
                KnowledgeGraph graph = new KnowledgeGraph(directoryPath.toString(), allConcepts);
                graph.setSources(sources);
                integrateWithMemoryBrain(graph);
                
                return graph;
            } catch (IOException e) {
                System.err.println("Error scraping directory: " + directoryPath + " - " + e.getMessage());
                return new KnowledgeGraph(directoryPath.toString(), Collections.emptyList());
            }
        }, executorService);
    }
    
    /**
     * Scrape a URL and extract knowledge.
     */
    public CompletableFuture<KnowledgeGraph> scrapeURL(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String content = urlScraper.scrape(url);
                List<Concept> concepts = conceptExtractor.extract(content);
                synapseManager.createSynapticConnections(concepts);
                
                KnowledgeGraph graph = new KnowledgeGraph(url, concepts);
                integrateWithMemoryBrain(graph);
                
                return graph;
            } catch (IOException e) {
                System.err.println("Error scraping URL: " + url + " - " + e.getMessage());
                return new KnowledgeGraph(url, Collections.emptyList());
            }
        }, executorService);
    }
    
    /**
     * Scrape a website recursively to a specified depth.
     */
    public CompletableFuture<KnowledgeGraph> scrapeWebsite(String baseUrl, int depth) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> visited = new HashSet<>();
            List<Concept> allConcepts = new ArrayList<>();
            
            scrapeWebsiteRecursive(baseUrl, depth, visited, allConcepts);
            
            synapseManager.createSynapticConnections(allConcepts);
            
            KnowledgeGraph graph = new KnowledgeGraph(baseUrl, allConcepts);
            integrateWithMemoryBrain(graph);
            
            return graph;
        }, executorService);
    }
    
    private void scrapeWebsiteRecursive(String url, int depth, Set<String> visited, List<Concept> allConcepts) {
        if (depth < 0 || visited.contains(url)) {
            return;
        }
        
        visited.add(url);
        
        try {
            String content = urlScraper.scrape(url);
            List<Concept> concepts = conceptExtractor.extract(content);
            allConcepts.addAll(concepts);
            
            List<String> links = urlScraper.extractLinks(content);
            for (String link : links) {
                if (!visited.contains(link)) {
                    scrapeWebsiteRecursive(link, depth - 1, visited, allConcepts);
                }
            }
        } catch (IOException e) {
            System.err.println("Error scraping website: " + url + " - " + e.getMessage());
        }
    }
    
    /**
     * Scrape a GitHub repository.
     */
    public CompletableFuture<KnowledgeGraph> scrapeGitHubRepository(String owner, String repo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GitHubRepository repoData = githubScraper.scrapeRepository(owner, repo);
                List<Concept> concepts = conceptExtractor.extract(repoData.getContent());
                synapseManager.createSynapticConnections(concepts);
                
                KnowledgeGraph graph = new KnowledgeGraph("github:" + owner + "/" + repo, concepts);
                graph.setMetadata("owner", owner);
                graph.setMetadata("repo", repo);
                integrateWithMemoryBrain(graph);
                
                return graph;
            } catch (IOException e) {
                System.err.println("Error scraping GitHub repo: " + owner + "/" + repo + " - " + e.getMessage());
                return new KnowledgeGraph("github:" + owner + "/" + repo, Collections.emptyList());
            }
        }, executorService);
    }
    
    /**
     * Scrape a GitHub gist.
     */
    public CompletableFuture<KnowledgeGraph> scrapeGitHubGist(String gistId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String content = githubScraper.scrapeGist(gistId);
                List<Concept> concepts = conceptExtractor.extract(content);
                synapseManager.createSynapticConnections(concepts);
                
                KnowledgeGraph graph = new KnowledgeGraph("gist:" + gistId, concepts);
                integrateWithMemoryBrain(graph);
                
                return graph;
            } catch (IOException e) {
                System.err.println("Error scraping GitHub gist: " + gistId + " - " + e.getMessage());
                return new KnowledgeGraph("gist:" + gistId, Collections.emptyList());
            }
        }, executorService);
    }
    
    /**
     * Integrate knowledge graph with MemoryBrain.
     */
    private void integrateWithMemoryBrain(KnowledgeGraph graph) {
        try {
            // Store each concept in MemoryBrain
            for (Concept concept : graph.getConcepts()) {
                String query = "Store concept: " + concept.getName();
                Map<String, Object> params = new HashMap<>();
                params.put("concept", concept.getName());
                params.put("definition", concept.getDefinition());
                params.put("vector", concept.getHdcVector());
                
                // Use MemoryBrain to persist
                memoryBrain.processQuery(query, params).get();
            }
            
            // Update PhiResonanceMatrix based on knowledge graph
            updatePhiResonanceMatrix(graph);
            
        } catch (Exception e) {
            System.err.println("Error integrating with MemoryBrain: " + e.getMessage());
        }
    }
    
    /**
     * Update PhiResonanceMatrix based on knowledge graph.
     */
    private void updatePhiResonanceMatrix(KnowledgeGraph graph) {
        // This would update the resonance patterns based on the new knowledge
        // For now, this is a placeholder
        System.out.println("Updated PhiResonanceMatrix with " + graph.getConcepts().size() + " concepts");
    }
    
    /**
     * Shutdown the executor service.
     */
    public void shutdown() {
        executorService.shutdown();
    }
    
    // Helper methods
    
    private String readFileContent(Path filePath) throws IOException {
        return Files.readString(filePath);
    }
    
    private String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }
}
