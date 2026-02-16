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
