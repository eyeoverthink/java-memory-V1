package fraymus;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class KnowledgeScraper {

    private static final double PHI = PhiConstants.PHI;

    private final InfiniteMemory memory;
    private final PassiveLearner learner;
    private final PhiNeuralNet neuralNet;

    private final List<ScrapedDocument> scrapedDocs = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<String>> topicChunks = new ConcurrentHashMap<>();
    private final AtomicInteger totalChunksStored = new AtomicInteger(0);
    private final AtomicInteger totalFilesScraped = new AtomicInteger(0);
    private final AtomicInteger totalPagesProcessed = new AtomicInteger(0);
    private final AtomicBoolean scraping = new AtomicBoolean(false);
    private volatile String currentFile = "";
    private volatile double scrapeProgress = 0.0;
    private volatile String lastError = "";

    private static final int CHUNK_SIZE = 200;
    private static final int CHUNK_OVERLAP = 30;
    private static final int MAX_CHUNKS_PER_FILE = 500;  // Reduced to prevent overflow
    private static final long MAX_FILE_SIZE_MB = 10;     // Skip files larger than 10MB
    private static final int MAX_TEXT_LENGTH = 500000;   // Max chars to process per file

    private static final Map<String, String[]> TOPIC_KEYWORDS = new LinkedHashMap<>();

    static {
        TOPIC_KEYWORDS.put("physics", new String[]{"force", "velocity", "acceleration", "momentum", "energy",
                "gravity", "mass", "newton", "kinetic", "potential", "particle", "wave", "field",
                "electron", "photon", "magnetic", "electric", "thermodynamic", "entropy", "collision"});
        TOPIC_KEYWORDS.put("quantum", new String[]{"quantum", "superposition", "entanglement", "tunneling",
                "wavefunction", "planck", "heisenberg", "schrodinger", "hamiltonian", "eigenvalue",
                "boson", "fermion", "qubit", "decoherence", "feynman", "propagator", "dirac"});
        TOPIC_KEYWORDS.put("mathematics", new String[]{"integral", "derivative", "calculus", "theorem",
                "equation", "matrix", "vector", "polynomial", "convergence", "limit", "function",
                "differential", "series", "topology", "algebra", "geometry", "proof", "axiom"});
        TOPIC_KEYWORDS.put("programming", new String[]{"algorithm", "function", "variable", "class",
                "object", "loop", "array", "stack", "queue", "tree", "graph", "recursion",
                "compile", "runtime", "debug", "interface", "method", "constructor"});
        TOPIC_KEYWORDS.put("language", new String[]{"noun", "verb", "adjective", "adverb", "synonym",
                "antonym", "definition", "usage", "grammar", "syntax", "semantics", "etymology",
                "phrase", "clause", "sentence", "word", "dictionary", "thesaurus"});
        TOPIC_KEYWORDS.put("consciousness", new String[]{"awareness", "cognition", "perception",
                "intelligence", "neural", "brain", "mind", "thought", "sentience", "emergence",
                "self-reference", "metacognition", "qualia"});
        TOPIC_KEYWORDS.put("evolution", new String[]{"mutation", "selection", "fitness", "genome",
                "gene", "dna", "adaptation", "species", "population", "crossover", "heredity",
                "phenotype", "genotype", "natural selection"});
        TOPIC_KEYWORDS.put("cryptography", new String[]{"cipher", "encrypt", "decrypt", "hash",
                "key", "rsa", "prime", "factoring", "signature", "certificate", "protocol"});
    }

    public static class ScrapedDocument {
        public final String filename;
        public final String filetype;
        public final int pages;
        public final int chunks;
        public final long timestamp;
        public final List<String> detectedTopics;
        public final long fileSize;

        public ScrapedDocument(String filename, String filetype, int pages, int chunks,
                               List<String> detectedTopics, long fileSize) {
            this.filename = filename;
            this.filetype = filetype;
            this.pages = pages;
            this.chunks = chunks;
            this.timestamp = System.currentTimeMillis();
            this.detectedTopics = detectedTopics;
            this.fileSize = fileSize;
        }
    }

    public KnowledgeScraper(InfiniteMemory memory, PassiveLearner learner, PhiNeuralNet neuralNet) {
        this.memory = memory;
        this.learner = learner;
        this.neuralNet = neuralNet;
    }

    public void scrapeFile(String filepath) {
        if (scraping.get()) {
            CommandTerminal.printError("Already scraping - please wait");
            return;
        }

        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {
            path = Paths.get("attached_assets", filepath);
            if (!Files.exists(path)) {
                path = Paths.get("d:/Zip And Send/Java-Memory/Asset-Manager/attached_assets", filepath);
                if (!Files.exists(path)) {
                    CommandTerminal.printError("File not found: " + filepath);
                    return;
                }
            }
        }

        final Path finalPath = path;
        Thread scrapeThread = new Thread(() -> {
            scraping.set(true);
            try {
                processFile(finalPath);
            } catch (Exception e) {
                lastError = e.getMessage();
                CommandTerminal.printError("Scrape error: " + e.getMessage());
            } finally {
                scraping.set(false);
                currentFile = "";
                scrapeProgress = 1.0;
            }
        }, "KnowledgeScraper");
        scrapeThread.setDaemon(true);
        scrapeThread.start();
    }

    public void scrapeAll() {
        if (scraping.get()) {
            CommandTerminal.printError("Already scraping - please wait");
            return;
        }

        Thread scrapeThread = new Thread(() -> {
            scraping.set(true);
            try {
                Path assetsDir = Paths.get("attached_assets");
                if (!Files.exists(assetsDir)) {
                    // Try absolute path as fallback
                    assetsDir = Paths.get(System.getProperty("user.dir"), "attached_assets");
                    if (!Files.exists(assetsDir)) {
                        // Try one more fallback - project root
                        assetsDir = Paths.get("d:/Zip And Send/Java-Memory/Asset-Manager/attached_assets");
                        if (!Files.exists(assetsDir)) {
                            CommandTerminal.printError("No attached_assets directory found");
                            CommandTerminal.print("  Searched: ./attached_assets, " + System.getProperty("user.dir"));
                            return;
                        }
                    }
                }
                CommandTerminal.print("  Using: " + assetsDir.toAbsolutePath());

                List<Path> files;
                try (var stream = Files.list(assetsDir)) {
                    files = stream
                        .filter(p -> {
                            String name = p.getFileName().toString().toLowerCase();
                            return name.endsWith(".pdf") || name.endsWith(".txt") ||
                                    name.endsWith(".py") || name.endsWith(".java") ||
                                    name.endsWith(".md") || name.endsWith(".html") ||
                                    name.endsWith(".json") || name.endsWith(".csv");
                        })
                        .sorted()
                        .collect(Collectors.toList());
                }

                CommandTerminal.printHighlight(String.format("=== SCRAPING %d FILES ===", files.size()));

                for (int i = 0; i < files.size(); i++) {
                    scrapeProgress = (double) i / files.size();
                    
                    // Memory check every file
                    Runtime rt = Runtime.getRuntime();
                    long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
                    if (usedMB > 450) {
                        System.gc();
                        CommandTerminal.printColored("[GC triggered at " + usedMB + "MB]", 1.0f, 1.0f, 0.0f);
                        Thread.sleep(100); // Give GC time
                    }
                    
                    try {
                        processFile(files.get(i));
                    } catch (OutOfMemoryError oom) {
                        System.gc();
                        lastError = "OUT OF MEMORY";
                        CommandTerminal.printError("  OUT OF MEMORY on " + files.get(i).getFileName());
                        CrashLogger.log("scrape-oom", "OutOfMemory on " + files.get(i));
                        break; // Stop scraping
                    } catch (Exception e) {
                        String errMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                        lastError = errMsg;
                        CommandTerminal.printError("  Error on " + files.get(i).getFileName() + ": " + errMsg);
                        CrashLogger.log("scrape-file", e);
                    }
                }

                scrapeProgress = 1.0;
                CommandTerminal.printSuccess(String.format(
                        "=== SCRAPE COMPLETE: %d files, %d chunks, %d pages ===",
                        totalFilesScraped.get(), totalChunksStored.get(), totalPagesProcessed.get()));

                if (memory != null) memory.forceSave();

            } catch (OutOfMemoryError oom) {
                System.gc();
                lastError = "OUT OF MEMORY";
                CommandTerminal.printError("CRITICAL: Out of memory during scrape");
                CrashLogger.log("scrape-all-oom", "OutOfMemoryError");
            } catch (Throwable t) {
                lastError = t.getMessage();
                CommandTerminal.printError("Scrape all error: " + t.getMessage());
                t.printStackTrace();
                CrashLogger.log("scrape-all", t.getMessage() != null ? t.getMessage() : t.getClass().getName());
            } finally {
                scraping.set(false);
                currentFile = "";
            }
        }, "KnowledgeScraper-All");
        
        // Set uncaught exception handler
        scrapeThread.setUncaughtExceptionHandler((t, e) -> {
            System.err.println("[SCRAPE CRASH] " + e.getMessage());
            e.printStackTrace();
            CrashLogger.log("scrape-uncaught", e.getMessage() != null ? e.getMessage() : e.getClass().getName());
            scraping.set(false);
        });
        
        scrapeThread.setDaemon(true);
        scrapeThread.start();
    }

    private void processFile(Path path) throws Exception {
        String filename = path.getFileName().toString();
        currentFile = filename;
        long fileSize = Files.size(path);
        
        // Skip files that are too large
        if (fileSize > MAX_FILE_SIZE_MB * 1024 * 1024) {
            CommandTerminal.printColored("  Skipping " + filename + " (too large: " + (fileSize / 1024 / 1024) + "MB)", 1.0f, 0.5f, 0.0f);
            return;
        }
        
        // Check memory before processing
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        if (usedMB > 400) {
            System.gc(); // Force GC if memory is high
            CommandTerminal.printColored("  [Memory cleanup at " + usedMB + "MB]", 1.0f, 1.0f, 0.0f);
        }

        String nameLower = filename.toLowerCase();
        String text;
        int pageCount = 0;

        if (nameLower.endsWith(".pdf")) {
            CommandTerminal.printInfo("[SCRAPER] Extracting PDF: " + filename);
            PDDocument doc = null;
            try {
                doc = PDDocument.load(path.toFile());
                pageCount = doc.getNumberOfPages();
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                text = stripper.getText(doc);
                totalPagesProcessed.addAndGet(pageCount);
                CommandTerminal.printInfo(String.format("  Extracted %d pages, %d chars", pageCount, text.length()));
            } finally {
                if (doc != null) {
                    try { doc.close(); } catch (IOException ignored) {}
                }
            }
        } else {
            CommandTerminal.printInfo("[SCRAPER] Reading text file: " + filename);
            text = new String(Files.readAllBytes(path));
            pageCount = 1;
        }

        if (text == null || text.trim().isEmpty()) {
            CommandTerminal.printColored("  Skipping empty file: " + filename, 1.0f, 0.5f, 0.0f);
            return;
        }
        
        // Truncate if too long to prevent overflow
        if (text.length() > MAX_TEXT_LENGTH) {
            CommandTerminal.printColored("  Truncating " + filename + " from " + text.length() + " to " + MAX_TEXT_LENGTH + " chars", 1.0f, 1.0f, 0.0f);
            text = text.substring(0, MAX_TEXT_LENGTH);
        }

        text = cleanText(text);

        List<String> chunks = chunkText(text);
        if (chunks.size() > MAX_CHUNKS_PER_FILE) {
            chunks = chunks.subList(0, MAX_CHUNKS_PER_FILE);
        }

        List<String> detectedTopics = detectDocumentTopics(text);
        
        // SAVE ONLY TO MONGODB - skip local memory entirely to prevent overflow
        MongoPersistence mongo = MongoPersistence.getInstance();
        
        int storedCount = 0;
        int batchCount = 0;
        List<org.bson.Document> mongoBatch = new ArrayList<>(50);

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            if (chunk.trim().length() < 20) continue;

            double phiResonance = computeChunkResonance(chunk, i, chunks.size());

            // ONLY save to MongoDB - no local storage
            if (mongo.isConnected()) {
                Map<String, String> meta = new HashMap<>();
                meta.put("source", filename);
                meta.put("chunk_index", String.valueOf(i));
                meta.put("total_chunks", String.valueOf(chunks.size()));
                if (!detectedTopics.isEmpty()) {
                    meta.put("topics", String.join(",", detectedTopics));
                }
                mongoBatch.add(MongoPersistence.createKnowledgeDoc(chunk, phiResonance, filename, meta));
                batchCount++;
                
                // Batch save every 30 chunks and clear immediately
                if (batchCount >= 30) {
                    mongo.saveKnowledgeBatch(mongoBatch);
                    mongoBatch.clear();
                    batchCount = 0;
                }
            }
            
            // VERY light integration - only first 3 chunks, truncated heavily
            if (i < 3 && learner != null) {
                String topicTag = detectedTopics.isEmpty() ? "general" : detectedTopics.get(0);
                learner.integrateEvent(
                        "knowledge:" + topicTag,
                        chunk.substring(0, Math.min(100, chunk.length())),
                        phiResonance
                );
            }

            storedCount++;
            chunk = null; // Release reference immediately
        }
        
        // Save remaining batch
        if (!mongoBatch.isEmpty()) {
            mongo.saveKnowledgeBatch(mongoBatch);
            mongoBatch.clear();
        }
        
        // DESTROY all references immediately
        chunks.clear();
        chunks = null;
        text = null;

        totalChunksStored.addAndGet(storedCount);
        totalFilesScraped.incrementAndGet();

        // Create doc record before clearing topics
        List<String> topicsCopy = detectedTopics != null ? new ArrayList<>(detectedTopics) : new ArrayList<>();
        detectedTopics = null;
        
        ScrapedDocument doc = new ScrapedDocument(filename, nameLower.endsWith(".pdf") ? "PDF" : "TEXT",
                pageCount, storedCount, topicsCopy, fileSize);
        // Limit scraped docs list
        if (scrapedDocs.size() > 50) {
            scrapedDocs.remove(0);
        }
        scrapedDocs.add(doc);

        if (memory != null) {
            GenesisMemory genesis = jade.Window.getPhiWorld() != null ? jade.Window.getPhiWorld().getMemory() : null;
            if (genesis != null) {
                genesis.record("KNOWLEDGE_SCRAPED",
                        String.format("file=%s|pages=%d|chunks=%d|topics=%s",
                                filename, pageCount, storedCount,
                                String.join(",", detectedTopics)));
            }
        }

        // Force GC after each file
        System.gc();
        
        CommandTerminal.printSuccess(String.format("  Stored %d chunks → MongoDB from %s [%s]",
                storedCount, filename, String.join(", ", detectedTopics)));
    }

    private String cleanText(String raw) {
        raw = raw.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", " ");
        raw = raw.replaceAll("\\s{3,}", "\n\n");
        raw = raw.replaceAll("[ \\t]{2,}", " ");
        raw = raw.replaceAll("(?m)^\\s+$", "");
        return raw.trim();
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");

        for (int i = 0; i < words.length; i += CHUNK_SIZE - CHUNK_OVERLAP) {
            int end = Math.min(i + CHUNK_SIZE, words.length);
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < end; j++) {
                if (j > i) sb.append(" ");
                sb.append(words[j]);
            }
            String chunk = sb.toString().trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end >= words.length) break;
        }

        return chunks;
    }

    private List<String> detectDocumentTopics(String text) {
        String lower = text.toLowerCase();
        Map<String, Integer> topicScores = new LinkedHashMap<>();

        for (Map.Entry<String, String[]> entry : TOPIC_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                int idx = 0;
                while ((idx = lower.indexOf(keyword, idx)) != -1) {
                    score++;
                    idx += keyword.length();
                    if (score > 100) break;
                }
            }
            if (score >= 3) {
                topicScores.put(entry.getKey(), score);
            }
        }

        return topicScores.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double computeChunkResonance(String chunk, int index, int totalChunks) {
        double positionFactor = (double) index / Math.max(1, totalChunks);
        double phiPosition = (positionFactor * PHI) % 1.0;

        int charSum = 0;
        for (char c : chunk.toCharArray()) {
            charSum += c;
        }
        double charFactor = (charSum % 1000) / 1000.0;

        return 0.3 + 0.4 * phiPosition + 0.3 * charFactor;
    }

    public String queryKnowledge(String topic) {
        List<String> chunks = topicChunks.get(topic.toLowerCase());
        if (chunks == null || chunks.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : topicChunks.entrySet()) {
                if (entry.getKey().contains(topic.toLowerCase()) || topic.toLowerCase().contains(entry.getKey())) {
                    chunks = entry.getValue();
                    break;
                }
            }
        }
        if (chunks == null || chunks.isEmpty()) {
            return null;
        }

        int idx = (int) (Math.random() * chunks.size());
        String chunk = chunks.get(idx);
        if (chunk.length() > 300) {
            chunk = chunk.substring(0, 300) + "...";
        }
        return chunk;
    }

    public List<String> searchKnowledge(String query) {
        String lower = query.toLowerCase();
        List<String> results = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : topicChunks.entrySet()) {
            for (String chunk : entry.getValue()) {
                if (chunk.toLowerCase().contains(lower)) {
                    results.add("[" + entry.getKey() + "] " + (chunk.length() > 200 ? chunk.substring(0, 200) + "..." : chunk));
                    if (results.size() >= 10) return results;
                }
            }
        }

        return results;
    }

    public boolean isScraping() { return scraping.get(); }
    public String getCurrentFile() { return currentFile; }
    public double getScrapeProgress() { return scrapeProgress; }
    public int getTotalChunksStored() { return totalChunksStored.get(); }
    public int getTotalFilesScraped() { return totalFilesScraped.get(); }
    public int getTotalPagesProcessed() { return totalPagesProcessed.get(); }
    public String getLastError() { return lastError; }
    public List<ScrapedDocument> getScrapedDocs() { return Collections.unmodifiableList(scrapedDocs); }
    public Map<String, List<String>> getTopicChunks() { return Collections.unmodifiableMap(topicChunks); }

    public Map<String, Integer> getTopicCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : topicChunks.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }
        return counts;
    }
}
