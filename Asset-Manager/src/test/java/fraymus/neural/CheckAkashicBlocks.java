package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

/**
 * Check what's actually in AkashicRecord
 */
public class CheckAkashicBlocks {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("CHECK AKASHIC RECORD CONTENTS");
        System.out.println("========================================");
        System.out.println();
        
        AkashicRecord akashic = new AkashicRecord();
        
        System.out.println("[1] TOTAL BLOCKS");
        System.out.println("   Blocks: " + akashic.getPersistedBlockCount());
        System.out.println("   Categories: " + akashic.getCategoryCount());
        System.out.println();
        
        System.out.println("[2] LIBRARY_ABSORBED CATEGORY");
        var blocks = akashic.queryCategory("LIBRARY_ABSORBED");
        System.out.println("   Blocks in LIBRARY_ABSORBED: " + blocks.size());
        System.out.println();
        
        if (blocks.size() > 0) {
            System.out.println("[3] FIRST 5 LIBRARY_ABSORBED BLOCKS");
            int count = 0;
            for (var block : blocks) {
                if (count++ >= 5) break;
                System.out.println("   Block " + count + ": " + block.content.substring(0, Math.min(100, block.content.length())));
            }
            System.out.println();
        }
        
        System.out.println("[4] ALL CATEGORIES");
        akashic.printStats();
        System.out.println();
    }
}
