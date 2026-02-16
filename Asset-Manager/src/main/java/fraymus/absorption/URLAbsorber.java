package fraymus.absorption;

import fraymus.knowledge.AkashicRecord;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL ABSORBER: THE WEB EATER
 * 
 * "The Internet is just a database waiting to be ingested."
 * 
 * Mechanism:
 * 1. FETCH: Downloads raw HTML stream.
 * 2. STRIP: Removes tags, scripts, and noise.
 * 3. DISTILL: Extracts 'Core Concepts' (Headers) and 'Facts' (Paragraphs).
 * 4. INTEGRATE: Feeds the Akashic Record.
 */
public class URLAbsorber {

    private AkashicRecord akashic;

    public URLAbsorber(AkashicRecord record) {
        this.akashic = record;
        System.out.println("🌐 URL ABSORBER ONLINE. WAITING FOR TARGETS.");
    }

    /**
     * ABSORB: The Ingestion Protocol
     */
    public void absorb(String targetUrl) {
        System.out.println("   >> TARGET LOCKED: " + targetUrl);
        System.out.println("   >> INITIATING DATA SIPHON...");

        try {
            // 1. FETCH RAW DATA
            String rawHtml = fetchHtml(targetUrl);
            
            // 2. THE DIGESTION (Parsing)
            PageContent content = digest(rawHtml);
            
            // 3. THE INTEGRATION (Storage)
            System.out.println("   >> ABSORBING KNOWLEDGE...");
            
            // Store the Title as a High-Level Concept
            akashic.addBlock("CONCEPT", content.title);
            
            // Store Headers as Sub-Concepts
            for (String header : content.headers) {
                akashic.addBlock("SUB_CONCEPT", header);
            }
            
            // Store Paragraphs as Raw Facts
            int factCount = 0;
            for (String paragraph : content.paragraphs) {
                // Only store substantial facts
                if (paragraph.length() > 50) {
                    akashic.addBlock("FACT", paragraph);
                    factCount++;
                }
            }
            
            System.out.println("   ✓ ABSORPTION COMPLETE.");
            System.out.println("   ✓ NEW CONCEPTS: " + content.headers.size());
            System.out.println("   ✓ NEW FACTS: " + factCount);

        } catch (Exception e) {
            System.out.println("   !! CONNECTION FAILED: " + e.getMessage());
        }
    }

    // --- INTERNAL DIGESTION ENGINES ---

    private String fetchHtml(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Fraymus/1.0 (Eyeoverthink Bot)");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    private PageContent digest(String html) {
        PageContent page = new PageContent();
        
        // 1. EXTRACT TITLE (The Soul of the Page)
        Matcher titleMatcher = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE).matcher(html);
        if (titleMatcher.find()) page.title = clean(titleMatcher.group(1));

        // 2. EXTRACT HEADERS (The Skeleton)
        Matcher headerMatcher = Pattern.compile("<h[1-3][^>]*>(.*?)</h[1-3]>", Pattern.CASE_INSENSITIVE).matcher(html);
        while (headerMatcher.find()) {
            page.headers.add(clean(headerMatcher.group(1)));
        }

        // 3. EXTRACT PARAGRAPHS (The Meat)
        Matcher pMatcher = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.CASE_INSENSITIVE).matcher(html);
        while (pMatcher.find()) {
            String rawText = clean(pMatcher.group(1));
            if (!rawText.isEmpty()) page.paragraphs.add(rawText);
        }

        return page;
    }

    // SANITIZER: Removes HTML tags and noise
    private String clean(String dirty) {
        return dirty.replaceAll("<[^>]*>", "") // Remove tags
                    .replaceAll("&[^;]+;", " ") // Remove HTML entities
                    .trim();
    }

    // DATA STRUCTURE
    private static class PageContent {
        String title = "Unknown";
        List<String> headers = new ArrayList<>();
        List<String> paragraphs = new ArrayList<>();
    }
}
