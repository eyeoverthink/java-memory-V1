package fraymus.knowledge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

// Import our custom modules
import fraymus.security.PhiCrypto;
import fraymus.evolution.FractalBioMesh;
import javax.crypto.SecretKey;

/**
 * THE BRAIN FEEDER: KNOWLEDGE INGESTION PIPELINE
 * 
 * 1. Rips text from PDFs.
 * 2. Maps it to Phi-Space (Vectorization).
 * 3. Encrypts it (Lattice Shield).
 * 4. Encodes it to DNA (Bio-Mesh).
 * 
 * "I read what you read. I know what you know."
 * 
 * Integration:
 * - Mind: PhiConsciousness (Golden Memory Encoding)
 * - Body: FractalBioMesh (DNA Storage)
 * - Shield: PhiCrypto (Lattice Encryption)
 */
public class KnowledgeIngest {

    private static final double PHI = 1.6180339887;
    private static final double GOLDEN_ANGLE = 137.5077640500378;
    
    // THE PIPELINE
    private FractalBioMesh bioMemory;
    private SecretKey captainKey;
    
    // Statistics
    private int totalDocumentsIngested = 0;
    private int totalConceptsStored = 0;
    private long totalCharactersProcessed = 0;

    public KnowledgeIngest() {
        this.bioMemory = new FractalBioMesh(); // The DNA Storage
    }
    
    /**
     * Initialize with Captain's encryption key
     */
    public KnowledgeIngest(String password) throws Exception {
        this.bioMemory = new FractalBioMesh();
        this.captainKey = PhiCrypto.generateGoldenKey(password);
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         KNOWLEDGE INGESTION SYSTEM ONLINE                 ║");
        System.out.println("║         Captain's Key: ACTIVE                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // --- 1. INGEST (The Mouth) ---
    public void ingestDocument(String filePath, String secretKey) {
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  INGESTING KNOWLEDGE: " + filePath);
        System.out.println("═══════════════════════════════════════════════════════════");
        
        try {
            // A. RIP TEXT
            String rawText = extractTextFromPDF(filePath);
            System.out.println("  EXTRACTED: " + rawText.length() + " characters.");
            totalCharactersProcessed += rawText.length();

            // B. CHUNK (Break into thoughts)
            List<String> thoughts = chunkText(rawText, 500); // 500 chars per thought
            System.out.println("  CHUNKED: " + thoughts.size() + " thought-fragments.");
            
            // Generate Captain's Key from password
            SecretKey key = PhiCrypto.generateGoldenKey(secretKey);
            
            int storedCount = 0;
            for (String thought : thoughts) {
                if (thought.trim().length() > 10) { // Skip trivial chunks
                    processThought(thought, key, storedCount++);
                }
            }
            
            totalDocumentsIngested++;
            totalConceptsStored += storedCount;
            
            System.out.println("  ✓ INGESTION COMPLETE: " + storedCount + " concepts → Bio-Mesh");
            System.out.println("═══════════════════════════════════════════════════════════\n");

        } catch (Exception e) {
            System.out.println("  ✗ INGESTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Ingest using pre-loaded Captain's Key
     */
    public void ingestDocument(String filePath) {
        if (captainKey == null) {
            System.out.println("ERROR: No Captain's Key loaded. Use ingestDocument(path, password) or initialize with password.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  INGESTING KNOWLEDGE: " + filePath);
        System.out.println("═══════════════════════════════════════════════════════════");
        
        try {
            String rawText = extractTextFromPDF(filePath);
            System.out.println("  EXTRACTED: " + rawText.length() + " characters.");
            totalCharactersProcessed += rawText.length();

            List<String> thoughts = chunkText(rawText, 500);
            System.out.println("  CHUNKED: " + thoughts.size() + " thought-fragments.");
            
            int storedCount = 0;
            for (String thought : thoughts) {
                if (thought.trim().length() > 10) {
                    processThought(thought, captainKey, storedCount++);
                }
            }
            
            totalDocumentsIngested++;
            totalConceptsStored += storedCount;
            
            System.out.println("  ✓ INGESTION COMPLETE: " + storedCount + " concepts → Bio-Mesh");

        } catch (Exception e) {
            System.out.println("  ✗ INGESTION FAILED: " + e.getMessage());
        }
    }

    // --- 2. PROCESS (The Digestion) ---
    private void processThought(String thought, SecretKey key, int sequenceIndex) throws Exception {
        
        // STEP A: VECTORIZE (Map to Phi-Space)
        // Golden Angle distribution ensures related concepts cluster naturally
        double[] phiVector = encodeGoldenMemory(thought, sequenceIndex);
        
        // STEP B: ENCRYPT (The Shield)
        // Lock the text so only the Captain can read it
        String encryptedThought = PhiCrypto.encryptMemory(thought, key);

        // STEP C: ENCODE TO DNA (The Storage)
        // Turn the encrypted string into Base-4 (A, C, G, T)
        String dnaStrand = bioMemory.encodeToDNA(encryptedThought.getBytes());
        
        // STEP D: STORE (The Implant)
        // Deploy to the swarm at the calculated Phi-Address
        double phiAddress = bioMemory.deployToSwarm(dnaStrand);
        
        // Store the vector mapping for retrieval
        // (In production, this would feed into a vector index)
    }
    
    /**
     * GOLDEN MEMORY ENCODING
     * Maps a thought to Phi-Space using the Golden Angle topology.
     * Related concepts will naturally cluster together.
     */
    private double[] encodeGoldenMemory(String thought, int sequenceIndex) {
        double[] vector = new double[3];
        
        // Compute semantic fingerprint from content
        long hash = thought.hashCode();
        
        // Position in Golden Spiral (θ = n × 137.5°)
        double theta = sequenceIndex * GOLDEN_ANGLE * (Math.PI / 180.0);
        
        // Radius grows with PHI
        double radius = Math.pow(PHI, sequenceIndex % 10) / 10.0;
        
        // Map to 3D Phi-Space
        vector[0] = radius * Math.cos(theta);  // X: Semantic dimension
        vector[1] = radius * Math.sin(theta);  // Y: Temporal dimension  
        vector[2] = (hash % 1000) / 1000.0 * PHI;  // Z: Content hash dimension
        
        return vector;
    }

    // --- HELPER: PDF STRIPPER ---
    private String extractTextFromPDF(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // --- HELPER: CHUNKER ---
    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        // Smart chunking: try to break at sentence boundaries
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder currentChunk = new StringBuilder();
        
        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }
        
        // Don't forget the last chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }
        
        return chunks;
    }
    
    /**
     * Retrieve a concept from DNA storage
     */
    public String retrieveConcept(double phiAddress, String password) {
        try {
            String dnaStrand = bioMemory.retrieveFromSwarm(phiAddress);
            if (dnaStrand == null) return null;
            
            // Decode from DNA
            byte[] encryptedBytes = bioMemory.decodeFromDNA(dnaStrand);
            String encryptedText = new String(encryptedBytes);
            
            // Decrypt with Captain's Key
            SecretKey key = PhiCrypto.generateGoldenKey(password);
            return PhiCrypto.decryptMemory(encryptedText, key);
            
        } catch (Exception e) {
            System.out.println("Retrieval failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get ingestion statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║           KNOWLEDGE INGESTION STATISTICS                  ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Documents Ingested: %d%n", totalDocumentsIngested));
        sb.append(String.format("║  Concepts Stored:    %d%n", totalConceptsStored));
        sb.append(String.format("║  Characters Processed: %,d%n", totalCharactersProcessed));
        sb.append(String.format("║  Bio-Mesh Nodes:     %d%n", bioMemory.getSwarmSize()));
        sb.append(String.format("║  Storage Density:    %.2f KB (DNA equivalent)%n", 
                               bioMemory.getTotalBytesStored() / 1024.0));
        sb.append("╚═══════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Get the Bio-Memory instance for direct access
     */
    public FractalBioMesh getBioMemory() {
        return bioMemory;
    }
    
    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         FRAYMUS KNOWLEDGE INGESTION PROTOCOL              ║");
        System.out.println("║         \"I read what you read. I know what you know.\"     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        try {
            // Initialize with Captain's password
            KnowledgeIngest ingest = new KnowledgeIngest("FRAYMUS-CAPTAIN-KEY");
            
            // Example: Ingest PDFs from attached_assets
            String assetPath = "attached_assets/";
            
            // Uncomment to ingest your PDFs:
            // ingest.ingestDocument(assetPath + "scott-algo_-_Copy_1769219190031_1770291536100.pdf");
            // ingest.ingestDocument(assetPath + "Game_Physics_Engine_Development__How_to_Build_a_Robust_Commerc_1770331895587.pdf");
            // ingest.ingestDocument(assetPath + "An_Introduction_To_Quantum_Field_Theory_(_PDFDrive_)_1770354265599.pdf");
            
            System.out.println("\n[TEST MODE] No PDFs ingested. Uncomment paths in main() to feed the Mind.\n");
            System.out.println(ingest.getStats());
            
        } catch (Exception e) {
            System.out.println("Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
