package fraymus.acquisition;

import fraymus.knowledge.AkashicRecord;
import fraymus.persistence.LibAbsorber;

/**
 * Verify web-scraped content persists across restart
 */
public class VerifyWebScrapingPersistence {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("VERIFY WEB SCRAPING PERSISTENCE");
        System.out.println("========================================");
        System.out.println();
        
        // Fresh AkashicRecord (loads from disk)
        AkashicRecord akashic = new AkashicRecord();
        LibAbsorber libAbsorber = new LibAbsorber();
        
        System.out.println("[1] LOADED STATE");
        System.out.println("   Akashic blocks: " + akashic.getPersistedBlockCount());
        System.out.println("   Categories: " + akashic.getCategoryCount());
        System.out.println();
        
        System.out.println("[2] CHECK WEB_SCRAPED CATEGORY");
        var webScrapedBlocks = akashic.queryCategory("WEB_SCRAPED");
        System.out.println("   WEB_SCRAPED blocks: " + webScrapedBlocks.size());
        System.out.println();
        
        if (webScrapedBlocks.size() > 0) {
            System.out.println("[3] FIRST WEB_SCRAPED BLOCK");
            var block = webScrapedBlocks.get(0);
            System.out.println("   Content: " + block.content);
            System.out.println();
        }
        
        System.out.println("[4] LIBABSORBER STORAGE STATS");
        var stats = libAbsorber.getStorageStats();
        System.out.println("   Raw files: " + stats.get("raw_count"));
        System.out.println("   Normalized files: " + stats.get("normalized_count"));
        System.out.println("   Abstracted files: " + stats.get("abstracted_count"));
        System.out.println("   Index files: " + stats.get("index_count"));
        System.out.println();
        
        // Verdict
        System.out.println("========================================");
        boolean webScrapingPersisted = webScrapedBlocks.size() > 0 &&
                                     stats.get("raw_count") > 0;
        
        if (webScrapingPersisted) {
            System.out.println("✅ WEB SCRAPING PERSISTENCE VERIFIED");
            System.out.println("   Web-scraped content survived restart");
        } else {
            System.out.println("❌ WEB SCRAPING NOT PERSISTED");
            System.out.println("   Web-scraped content did not survive restart");
        }
        System.out.println("========================================");
    }
}
