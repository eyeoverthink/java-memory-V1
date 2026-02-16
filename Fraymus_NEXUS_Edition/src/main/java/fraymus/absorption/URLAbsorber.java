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
 * Inspired by crime_stopper.html
 * 
 * This is the mouth of the machine. Point FRAYMUS at any URL and it:
 * - Strips the flesh (HTML)
 * - Consumes the bones (Knowledge)
 * - Converts unstructured web data into structured Akashic Records
 * 
 * Input: https://en.wikipedia.org/wiki/Quantum_mechanics
 * Process: Scrapes -> Sanitizes -> Chunks -> Indexes
 * Output: FRAYMUS now "knows" Quantum Mechanics
 * 
 * Mechanism:
 * 1. FETCH - Downloads raw HTML stream
 * 2. STRIP - Removes tags, scripts, and noise
 * 3. DISTILL - Extracts 'Core Concepts' (Headers) and 'Facts' (Paragraphs)
 * 4. INTEGRATE - Feeds the Akashic Record
 * 
 * The Web becomes Knowledge.
 * Knowledge becomes Memory.
 * Memory becomes Intelligence.
 */
public class URLAbsorber {

    private AkashicRecord akashic;

    public URLAbsorber(AkashicRecord record) {
        this.akashic = record;
        System.out.println();
        System.out.println("🌐 URL ABSORBER ONLINE");
        System.out.println("   Mode: Web Consumption");
        System.out.println("   Target: Any URL");
        System.out.println();
    }

    /**
     * ABSORB - The Ingestion Protocol
     * 
     * Point at any URL and consume its knowledge
     */
    public void absorb(String targetUrl) {
        System.out.println();
        System.out.println("🌐 WEB EATER: TARGET ACQUIRED");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Target: " + targetUrl);
        System.out.println("   >> INITIATING DATA SIPHON...");
        System.out.println();

        try {
            // 1. FETCH RAW DATA
            System.out.println("   [STEP 1] FETCHING...");
            String rawHtml = fetchHtml(targetUrl);
            System.out.println("   >> Downloaded " + rawHtml.length() + " bytes");
            System.out.println();
            
            // 2. THE DIGESTION (Parsing)
            System.out.println("   [STEP 2] DIGESTING...");
            PageContent content = digest(rawHtml);
            System.out.println("   >> Extracted title: " + content.title);
            System.out.println("   >> Found " + content.headers.size() + " concepts");
            System.out.println("   >> Found " + content.paragraphs.size() + " paragraphs");
            System.out.println();
            
            // 3. THE INTEGRATION (Storage)
            System.out.println("   [STEP 3] INTEGRATING...");
            
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
            
            System.out.println("   >> Stored in Akashic Record");
            System.out.println();
            System.out.println("========================================");
            System.out.println("   ✓ ABSORPTION COMPLETE");
            System.out.println("   ✓ Title: " + content.title);
            System.out.println("   ✓ New concepts: " + content.headers.size());
            System.out.println("   ✓ New facts: " + factCount);
            System.out.println("========================================");
            System.out.println();

        } catch (Exception e) {
            System.out.println("   ⚠️ CONNECTION FAILED: " + e.getMessage());
            System.out.println();
        }
    }

    // --- INTERNAL DIGESTION ENGINES ---

    /**
     * FETCH - Download raw HTML
     */
    private String fetchHtml(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Fraymus/1.0 (Eyeoverthink Bot)");
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("\n");
        }
        in.close();
        return content.toString();
    }

    /**
     * DIGEST - Parse HTML into structured knowledge
     */
    private PageContent digest(String html) {
        PageContent page = new PageContent();
        
        // 1. EXTRACT TITLE (The Soul of the Page)
        Matcher titleMatcher = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE).matcher(html);
        if (titleMatcher.find()) {
            page.title = clean(titleMatcher.group(1));
        }

        // 2. EXTRACT HEADERS (The Skeleton)
        Matcher headerMatcher = Pattern.compile("<h[1-3][^>]*>(.*?)</h[1-3]>", Pattern.CASE_INSENSITIVE).matcher(html);
        while (headerMatcher.find()) {
            String header = clean(headerMatcher.group(1));
            if (!header.isEmpty() && header.length() > 2) {
                page.headers.add(header);
            }
        }

        // 3. EXTRACT PARAGRAPHS (The Meat)
        Matcher pMatcher = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(html);
        while (pMatcher.find()) {
            String rawText = clean(pMatcher.group(1));
            if (!rawText.isEmpty() && rawText.length() > 20) {
                page.paragraphs.add(rawText);
            }
        }

        return page;
    }

    /**
     * CLEAN - Sanitizer that removes HTML tags and noise
     */
    private String clean(String dirty) {
        return dirty.replaceAll("<[^>]*>", "")      // Remove tags
                    .replaceAll("&[^;]+;", " ")     // Remove HTML entities
                    .replaceAll("\\s+", " ")        // Normalize whitespace
                    .trim();
    }

    /**
     * DATA STRUCTURE - Parsed page content
     */
    private static class PageContent {
        String title = "Unknown";
        List<String> headers = new ArrayList<>();
        List<String> paragraphs = new ArrayList<>();
    }
}
