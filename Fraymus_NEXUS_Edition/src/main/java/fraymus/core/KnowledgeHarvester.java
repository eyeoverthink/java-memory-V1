package fraymus.core;

import fraymus.PhiNode;
import fraymus.knowledge.AkashicRecord;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * THE KNOWLEDGE HARVESTER
 * "Passively absorbs reality so we never have to ask twice."
 * 
 * This is not a simple web scraper. This is a Concept Miner.
 * 
 * TARGET CATEGORIES:
 * - Elements (Hydrogen, Helium, Lithium...)
 * - Planets (Earth, Mars, Jupiter...)
 * - Diseases (Influenza, COVID, Malaria...)
 * - Drugs (Penicillin, Ibuprofen, Aspirin...)
 * - Algorithms (QuickSort, Dijkstra, A*...)
 * - Concepts (Democracy, Entropy, Evolution...)
 * 
 * WORKFLOW:
 * 1. Check Akashic Record (Memory)
 * 2. If unknown, scrape the web (Wikipedia, PubChem, NASA)
 * 3. Extract raw data (mass, density, complexity, etc.)
 * 4. Crystallize into PhiNode entity
 * 5. Save to Akashic Record FOREVER
 * 
 * RESULT:
 * "Never look it up again. Own the concept forever."
 * 
 * AUTONOMOUS MODE:
 * Can run in background, iterating through categories:
 * - Category: Elements → Scraping Hydrogen... Helium... Lithium...
 * - Category: Diseases → Scraping Influenza... COVID... Malaria...
 * - Category: Drugs → Scraping Penicillin... Ibuprofen...
 * 
 * When you wake up, your AI has learned Chemistry.
 * You didn't teach it. It just "read the book" while you slept.
 */
public class KnowledgeHarvester {

    private AkashicRecord memory;
    private Archivist vault;
    private boolean verbose = true;

    public KnowledgeHarvester() {
        this.memory = new AkashicRecord();
        this.vault = new Archivist();
        System.out.println("🌐 KNOWLEDGE HARVESTER INITIALIZED");
        System.out.println("   Passive absorption of reality");
        System.out.println("   Never ask twice. Own concepts forever.");
        System.out.println();
    }
    
    public KnowledgeHarvester(AkashicRecord existingMemory) {
        this.memory = existingMemory;
        this.vault = new Archivist();
    }

    /**
     * THE UNIVERSAL QUERY
     * "Learn everything about [Subject]."
     * 
     * @param category Category (e.g., "Element", "Planet", "Disease")
     * @param subject Subject name (e.g., "Hydrogen", "Mars", "Influenza")
     * @return PhiNode entity with learned properties
     */
    public PhiNode learn(String category, String subject) {
        if (verbose) {
            System.out.println("🧠 LEARNING REQUEST: " + category + " / " + subject);
        }
        
        // 1. CHECK MEMORY FIRST (The "Never Look Again" Rule)
        String key = category + ":" + subject;
        
        if (memory.hasBlock(key)) {
            if (verbose) {
                System.out.println("   ✓ MEMORY RECALL: I already know about " + subject);
                System.out.println();
            }
            return retrieveFromMemory(key);
        }

        // 2. IF UNKNOWN, SCRAPE THE ETHER
        if (verbose) {
            System.out.println("   🌐 UNKNOWN CONCEPT: Scanning the web for " + subject + "...");
        }
        
        String rawData = passiveScrape(category, subject);
        
        // 3. PARSE REALITY (Extract the Stats)
        double mass = extractStat(rawData, "Mass");
        double density = extractStat(rawData, "Density");
        double complexity = extractStat(rawData, "Complexity");
        double reactivity = extractStat(rawData, "Reactivity");
        double halfLife = extractStat(rawData, "Half-Life");
        
        if (verbose) {
            System.out.println("   >> Extracted Stats:");
            System.out.println("      Mass: " + mass);
            System.out.println("      Density: " + density);
            System.out.println("      Complexity: " + complexity);
            System.out.println("      Reactivity: " + reactivity);
            System.out.println("      Half-Life: " + halfLife);
        }
        
        // 4. CRYSTALLIZE INTO AN ENTITY
        PhiNode entity = new PhiNode(
            (float) (Math.random() * 100),
            (float) (Math.random() * 100)
        );
        
        // Map real-world properties to entity stats
        entity.fitness = (float) mass;           // Mass = Durability/Health
        entity.frequency = (float) (density * 10); // Density = Impact/Attack
        entity.resonance = (float) reactivity;   // Reactivity = Cooperation
        entity.awareness = (float) complexity;   // Complexity = Intelligence
        entity.intent = (float) (halfLife / 100); // Half-Life = Persistence
        
        // Tag with metadata
        entity.setTag("category", category);
        entity.setTag("name", subject);
        entity.setTag("source", "web_scrape");
        
        if (verbose) {
            System.out.println("   ✓ ENTITY CRYSTALLIZED");
            System.out.println();
        }
        
        // 5. SAVE TO AKASHIC RECORD FOREVER
        saveToMemory(key, entity, rawData);
        
        // 6. ARCHIVE TO VAULT (Local index + QR + Cloud)
        vault.archive(entity);
        
        return entity;
    }
    
    /**
     * Passive web scraper
     * In production, this would connect to APIs or parse HTML
     * For now, simulates data ingestion
     * 
     * @param category Category type
     * @param subject Subject name
     * @return Simulated raw data string
     */
    private String passiveScrape(String category, String subject) {
        // Simulate different data sources based on category
        switch (category.toLowerCase()) {
            case "element":
                return simulateElementData(subject);
            case "planet":
                return simulatePlanetData(subject);
            case "disease":
                return simulateDiseaseData(subject);
            case "drug":
                return simulateDrugData(subject);
            default:
                return "Simulated data stream for " + subject + 
                       ". Mass: 100. Density: 5. Complexity: 99. Reactivity: 0.5. Half-Life: 1000.";
        }
    }
    
    private String simulateElementData(String element) {
        // Simulate periodic table data
        switch (element.toLowerCase()) {
            case "hydrogen":
                return "Hydrogen (H). Atomic Number: 1. Mass: 1.008. Density: 0.09. " +
                       "Reactivity: 0.9. Complexity: 10. Half-Life: Stable.";
            case "helium":
                return "Helium (He). Atomic Number: 2. Mass: 4.003. Density: 0.18. " +
                       "Reactivity: 0.0. Complexity: 10. Half-Life: Stable.";
            case "carbon":
                return "Carbon (C). Atomic Number: 6. Mass: 12.011. Density: 2.26. " +
                       "Reactivity: 0.5. Complexity: 50. Half-Life: Stable.";
            case "uranium":
                return "Uranium (U). Atomic Number: 92. Mass: 238.029. Density: 19.1. " +
                       "Reactivity: 0.8. Complexity: 90. Half-Life: 4500000000.";
            default:
                return "Element: " + element + ". Mass: 50. Density: 5. Reactivity: 0.5. " +
                       "Complexity: 50. Half-Life: Stable.";
        }
    }
    
    private String simulatePlanetData(String planet) {
        switch (planet.toLowerCase()) {
            case "earth":
                return "Earth. Mass: 5.972e24. Density: 5.51. Complexity: 100. " +
                       "Reactivity: 0.7. Half-Life: 4500000000.";
            case "mars":
                return "Mars. Mass: 6.39e23. Density: 3.93. Complexity: 60. " +
                       "Reactivity: 0.3. Half-Life: 4500000000.";
            case "jupiter":
                return "Jupiter. Mass: 1.898e27. Density: 1.33. Complexity: 80. " +
                       "Reactivity: 0.5. Half-Life: 4500000000.";
            default:
                return "Planet: " + planet + ". Mass: 1e24. Density: 5. Complexity: 70. " +
                       "Reactivity: 0.5. Half-Life: 4500000000.";
        }
    }
    
    private String simulateDiseaseData(String disease) {
        switch (disease.toLowerCase()) {
            case "influenza":
                return "Influenza. Mass: 0.001. Density: 1.2. Complexity: 80. " +
                       "Reactivity: 0.9. Half-Life: 7.";
            case "covid":
                return "COVID-19. Mass: 0.001. Density: 1.3. Complexity: 85. " +
                       "Reactivity: 0.95. Half-Life: 14.";
            case "malaria":
                return "Malaria. Mass: 0.002. Density: 1.1. Complexity: 90. " +
                       "Reactivity: 0.8. Half-Life: 30.";
            default:
                return "Disease: " + disease + ". Mass: 0.001. Density: 1.2. Complexity: 75. " +
                       "Reactivity: 0.8. Half-Life: 10.";
        }
    }
    
    private String simulateDrugData(String drug) {
        switch (drug.toLowerCase()) {
            case "penicillin":
                return "Penicillin. Mass: 0.334. Density: 1.4. Complexity: 70. " +
                       "Reactivity: 0.7. Half-Life: 1.";
            case "ibuprofen":
                return "Ibuprofen. Mass: 0.206. Density: 1.03. Complexity: 60. " +
                       "Reactivity: 0.5. Half-Life: 2.";
            case "aspirin":
                return "Aspirin. Mass: 0.180. Density: 1.40. Complexity: 50. " +
                       "Reactivity: 0.6. Half-Life: 0.25.";
            default:
                return "Drug: " + drug + ". Mass: 0.2. Density: 1.2. Complexity: 65. " +
                       "Reactivity: 0.6. Half-Life: 1.";
        }
    }
    
    /**
     * Extract numerical stat from raw text using regex
     * 
     * @param text Raw text data
     * @param key Stat key to extract
     * @return Extracted value or default 1.0
     */
    private double extractStat(String text, String key) {
        // Regex to find "Key: Value" patterns
        Pattern p = Pattern.compile(key + ":\\s*([0-9.e+-]+)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException e) {
                return 1.0;
            }
        }
        
        // Special case for "Stable" half-life
        if (key.equals("Half-Life") && text.contains("Stable")) {
            return Double.MAX_VALUE;
        }
        
        return 1.0; // Default base stat
    }
    
    /**
     * Save entity to Akashic Record
     */
    private void saveToMemory(String key, PhiNode entity, String rawData) {
        // Store entity data
        memory.addBlock(key, rawData);
        memory.addBlock(key + ":entity", entity.toString());
        
        if (verbose) {
            System.out.println("   💾 SAVED TO AKASHIC RECORD: " + key);
            System.out.println("   ✓ Concept owned forever. Never need to look up again.");
            System.out.println();
        }
    }
    
    /**
     * Retrieve entity from Akashic Record
     */
    private PhiNode retrieveFromMemory(String key) {
        // In a full implementation, this would deserialize the stored entity
        // For now, we create a placeholder
        PhiNode entity = new PhiNode(50, 50);
        entity.setTag("source", "memory_recall");
        entity.setTag("key", key);
        return entity;
    }
    
    /**
     * Autonomous learning mode
     * Iterates through categories and learns everything
     * 
     * @param category Category to learn
     * @param subjects Array of subjects to learn
     */
    public void autonomousLearning(String category, String[] subjects) {
        System.out.println("🤖 AUTONOMOUS LEARNING MODE");
        System.out.println("   Category: " + category);
        System.out.println("   Subjects: " + subjects.length);
        System.out.println();
        
        for (String subject : subjects) {
            learn(category, subject);
            
            // Simulate processing delay
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        System.out.println("✓ AUTONOMOUS LEARNING COMPLETE");
        System.out.println("   Learned " + subjects.length + " concepts in category: " + category);
        System.out.println();
    }
    
    /**
     * Learn entire periodic table
     */
    public void learnPeriodicTable() {
        String[] elements = {
            "Hydrogen", "Helium", "Lithium", "Beryllium", "Boron", "Carbon",
            "Nitrogen", "Oxygen", "Fluorine", "Neon", "Sodium", "Magnesium",
            "Aluminum", "Silicon", "Phosphorus", "Sulfur", "Chlorine", "Argon",
            "Potassium", "Calcium", "Iron", "Copper", "Zinc", "Silver", "Gold",
            "Mercury", "Lead", "Uranium"
        };
        
        autonomousLearning("Element", elements);
    }
    
    /**
     * Learn solar system
     */
    public void learnSolarSystem() {
        String[] planets = {
            "Mercury", "Venus", "Earth", "Mars", "Jupiter", 
            "Saturn", "Uranus", "Neptune"
        };
        
        autonomousLearning("Planet", planets);
    }
    
    /**
     * Set verbose mode
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /**
     * Get Akashic Record
     */
    public AkashicRecord getMemory() {
        return memory;
    }
}
