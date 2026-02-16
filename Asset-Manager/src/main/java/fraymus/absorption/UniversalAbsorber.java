package fraymus.absorption;

import fraymus.knowledge.AkashicRecord;

/**
 * UNIVERSAL ABSORBER: THE UNIFIED PORTAL
 * 
 * "Throw anything at me. I'll figure out what it is."
 * 
 * Input: java.util       -> Triggers LibraryAbsorber
 * Input: http://...      -> Triggers URLAbsorber  
 * Input: C:/MyCode/...   -> Triggers FileAbsorber (Future)
 * Input: Raw Text        -> Direct Akashic Injection
 */
public class UniversalAbsorber {

    private LibraryAbsorber libEater;
    private URLAbsorber webEater;
    private FileAbsorber fileEater;
    private AkashicRecord akashic;

    public UniversalAbsorber() {
        this.akashic = new AkashicRecord();
        this.libEater = new LibraryAbsorber(akashic);
        this.webEater = new URLAbsorber(akashic);
        this.fileEater = new FileAbsorber(akashic);
        
        System.out.println("🌀 UNIVERSAL PORTAL OPEN.");
    }

    public UniversalAbsorber(AkashicRecord sharedAkashic) {
        this.akashic = sharedAkashic;
        this.libEater = new LibraryAbsorber(akashic);
        this.webEater = new URLAbsorber(akashic);
        this.fileEater = new FileAbsorber(akashic);
        
        System.out.println("🌀 UNIVERSAL PORTAL OPEN (SHARED MEMORY).");
    }

    public void consume(String entity) {
        System.out.println("\n>> ANALYZING ENTITY: [" + entity + "]");

        if (entity.startsWith("http://") || entity.startsWith("https://")) {
            // It's a URL
            System.out.println("   >> TYPE: WEB PAGE");
            webEater.absorb(entity);
        } 
        else if (entity.contains(".") && !entity.contains(" ") && !entity.contains("/") && !entity.contains("\\")) {
            // Likely a Java Package (e.g., java.util)
            System.out.println("   >> TYPE: JAVA PACKAGE");
            libEater.absorb(entity);
        }
        else if (entity.contains("/") || entity.contains("\\")) {
            // File path
            System.out.println("   >> TYPE: FILE PATH");
            fileEater.absorb(entity);
        }
        else {
            // Treat as Raw Knowledge (Direct Injection)
            System.out.println("   >> TYPE: RAW THOUGHT");
            akashic.addBlock("THOUGHT", entity);
            System.out.println("   ✓ THOUGHT INTEGRATED.");
        }
    }

    public AkashicRecord getAkashic() {
        return akashic;
    }
    
    /**
     * OUROBOROS PROTOCOL: Ingest Self
     * The system feeds on its own source code to enable recursive evolution.
     * This is the first step toward self-optimization.
     */
    public void ingestSelf() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   🐍 OUROBOROS PROTOCOL INITIATED                     ║");
        System.out.println("║   \"The serpent consumes its own tail...\"              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        // Ingest core system files
        String[] coreFiles = {
            "./src/main/java/fraymus/core/LogicCircuit.java",
            "./src/main/java/fraymus/core/LazarusEngine.java",
            "./src/main/java/fraymus/core/AkashicRecord.java",
            "./src/main/java/fraymus/absorption/UniversalAbsorber.java",
            "./src/main/java/fraymus/UniversalSync.java"
        };
        
        int successCount = 0;
        for (String filePath : coreFiles) {
            try {
                fileEater.absorb(filePath);
                successCount++;
            } catch (Exception e) {
                System.err.println("   ⚠️  Failed to ingest: " + filePath);
            }
        }
        
        System.out.println("\n   ✓ SELF-INGESTION COMPLETE");
        System.out.println("   Files Absorbed: " + successCount + "/" + coreFiles.length);
        System.out.println("   The system now contains its own DNA.");
    }
    
    /**
     * Ingest specific source file path
     */
    public void ingestPath(String filePath) {
        fileEater.absorb(filePath);
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║          UNIVERSAL ABSORBER - THE MOUTH               ║");
        System.out.println("║      \"Throw anything at me. I'll eat it.\"             ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();

        UniversalAbsorber mouth = new UniversalAbsorber();

        // Demo: Absorb a Wikipedia page
        mouth.consume("https://en.wikipedia.org/wiki/Quantum_mechanics");

        // Demo: Absorb a Java package
        mouth.consume("java.util.List");

        // Demo: Raw thought injection
        mouth.consume("The universe is a hologram projected from the boundary");

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("AKASHIC RECORD STATUS:");
        mouth.getAkashic().printStats();
    }
}
