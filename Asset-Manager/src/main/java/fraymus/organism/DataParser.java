package fraymus.organism;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * JSOUP/TIKA Mandibles — Data Parser
 *
 * Strips raw HTML/XML, reads files, and extracts key concepts.
 * Pure Java — no external dependencies required.
 */
public class DataParser {

    private int  documentsParsed = 0;
    private long charsParsed     = 0L;

    /** Read and clean text from a file. */
    public String parseFile(Path file) throws IOException {
        documentsParsed++;
        String content = Files.readString(file);
        charsParsed += content.length();
        return stripHtml(content);
    }

    /** Strip HTML/XML tags and decode common entities. */
    public String stripHtml(String html) {
        return html.replaceAll("<[^>]+>", "")
                   .replaceAll("&[a-z]+;", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    /**
     * Extract key concepts: words longer than 4 chars, deduplicated, capped at 200.
     */
    public List<String> extractConcepts(String text) {
        List<String> concepts = new ArrayList<>();
        String[] tokens = text.split("[^A-Za-z]+");
        for (String token : tokens) {
            String t = token.toUpperCase().trim();
            if (t.length() > 4 && !concepts.contains(t)) {
                concepts.add(t);
                if (concepts.size() >= 200) break;
            }
        }
        return concepts;
    }

    public int  getDocumentsParsed() { return documentsParsed; }
    public long getCharsParsed()     { return charsParsed; }

    public String getStatus() {
        return String.format("[TIKA] DocumentsParsed=%d CharsParsed=%d",
            documentsParsed, charsParsed);
    }
}
