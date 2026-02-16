package fraymus.absorption;

import fraymus.knowledge.AkashicRecord;
import java.util.List;

/**
 * THE UNIVERSAL ABSORBER
 * "The Unified Portal"
 * 
 * "You don't tell it what to absorb. You just throw something at it."
 * 
 * This is the Master Class - the single entry point for all absorption.
 * It auto-detects input type and routes to the appropriate absorber.
 * 
 * Input Types:
 * - http://... or https://... → URLAbsorber (Web Eater)
 * - java.util, org.apache.* → LibraryAbsorber (Code Eater)
 * - *.jar → LibraryAbsorber (JAR Eater)
 * - Anything else → Raw Thought (Direct Injection)
 * 
 * Examples:
 * consume("https://en.wikipedia.org/wiki/Quantum_mechanics")
 *   → Web Eater scrapes Wikipedia
 *   → Extracts concepts and facts
 *   → Stores in Akashic Record
 * 
 * consume("java.util")
 *   → Code Eater scans package
 *   → Extracts classes and methods
 *   → Stores as executable skills
 * 
 * consume("The universe is a hologram")
 *   → Raw Thought injector
 *   → Stores directly as knowledge block
 * 
 * The Vision:
 * One portal. Any input. Universal knowledge.
 */
public class UniversalAbsorber {

    private LibraryAbsorber codeEater;
    private URLAbsorber webEater;
    private PdfAbsorber pdfEater;
    private AkashicRecord akashic;

    public UniversalAbsorber() {
        this.akashic = new AkashicRecord();
        this.codeEater = new LibraryAbsorber(); // Uses own Akashic instance
        this.webEater = new URLAbsorber(akashic);
        this.pdfEater = new PdfAbsorber(akashic);
        
        System.out.println();
        System.out.println("🌀 UNIVERSAL ABSORBER INITIALIZED");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Unified Portal");
        System.out.println("   One interface. Any input. Universal knowledge.");
        System.out.println();
        System.out.println("   Supported:");
        System.out.println("   - URLs (http/https)");
        System.out.println("   - Java packages");
        System.out.println("   - JAR files");
        System.out.println("   - Raw thoughts");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * CONSUME - Universal ingestion
     * 
     * Auto-detects input type and routes to appropriate absorber
     */
    public void consume(String entity) {
        System.out.println();
        System.out.println("🌀 ANALYZING ENTITY");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Input: " + entity);
        System.out.println();

        // Detect input type
        if (entity.startsWith("http://") || entity.startsWith("https://")) {
            // It's a URL - Web Eater
            System.out.println("   Type: URL");
            System.out.println("   Router: Web Eater");
            System.out.println();
            webEater.absorb(entity);
        } 
        else if (entity.endsWith(".pdf")) {
            // PDF diamond archive absorption
            System.out.println("   >> Detected: DIAMOND ARCHIVE (PDF)");
            pdfEater.absorb(new java.io.File(entity));
        }
        else if (entity.endsWith(".jar")) {
            // It's a JAR file - Code Eater
            System.out.println("   Type: JAR File");
            System.out.println("   Router: Code Eater");
            System.out.println();
            codeEater.absorbJar(entity);
        }
        else if (entity.contains(".") && !entity.contains(" ") && !entity.contains("/")) {
            // Likely a Java Package (e.g., java.util)
            System.out.println("   Type: Java Package");
            System.out.println("   Router: Code Eater");
            System.out.println();
            codeEater.absorb(entity);
        } 
        else {
            // Treat as Raw Knowledge (Direct Injection)
            System.out.println("   Type: Raw Thought");
            System.out.println("   Router: Direct Injection");
            System.out.println();
            System.out.println("   >> INJECTING THOUGHT...");
            akashic.addBlock("THOUGHT", entity);
            System.out.println("   ✓ THOUGHT INTEGRATED");
            System.out.println();
            System.out.println("========================================");
            System.out.println();
        }
    }

    /**
     * Get access to Akashic Record
     */
    public AkashicRecord getAkashic() {
        return akashic;
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ UNIVERSAL ABSORBER DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   One Portal. Any Input. Universal Knowledge.");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        UniversalAbsorber portal = new UniversalAbsorber();
        
        // Test 1: Raw thought
        System.out.println("TEST 1: RAW THOUGHT");
        System.out.println("========================================");
        portal.consume("The universe is a hologram");
        
        // Test 2: Java package
        System.out.println("TEST 2: JAVA PACKAGE");
        System.out.println("========================================");
        portal.consume("java.lang");
        
        // Test 3: URL (commented out - requires internet)
        // System.out.println("TEST 3: URL");
        // System.out.println("========================================");
        // portal.consume("https://en.wikipedia.org/wiki/Artificial_intelligence");
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Portal is Open.");
        System.out.println("   Point it at anything:");
        System.out.println("   - Wikipedia pages");
        System.out.println("   - GitHub repos");
        System.out.println("   - Documentation sites");
        System.out.println("   - Java libraries");
        System.out.println("   - Raw knowledge");
        System.out.println();
        System.out.println("   FRAYMUS consumes it all.");
        System.out.println();
        System.out.println("========================================");
    }
}
