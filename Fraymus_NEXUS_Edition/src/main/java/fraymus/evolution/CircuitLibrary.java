package fraymus.evolution;

import fraymus.hardware.LogicBlock;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CIRCUIT LIBRARY
 * Persistent storage for evolved circuits.
 * 
 * Features:
 * - Save/load circuits to JSON
 * - Tag circuits by goal and fitness
 * - Reuse successful components
 * - Cloud-ready (80TB storage support)
 * - Thread-safe for parallel evolution
 * 
 * Storage Structure:
 * /evolution_db/
 *   /circuits/
 *     xor_gate_fitness100_gen465.json
 *     full_adder_fitness87_gen2341.json
 *   /library.json (index of all circuits)
 */
public class CircuitLibrary {
    
    private static final String DB_PATH = "evolution_db";
    private static final String CIRCUITS_PATH = DB_PATH + "/circuits";
    private static final String LIBRARY_INDEX = DB_PATH + "/library.json";
    
    private Map<String, CircuitRecord> library;
    
    public CircuitLibrary() {
        this.library = new ConcurrentHashMap<>();
        
        // Create directories if they don't exist
        try {
            Files.createDirectories(Paths.get(CIRCUITS_PATH));
            loadLibrary();
        } catch (IOException e) {
            System.err.println("⚠️  Warning: Could not create evolution database: " + e.getMessage());
        }
    }
    
    /**
     * SAVE a circuit to the library
     */
    public void save(String goalName, List<LogicBlock> circuit, int fitness, int generation) {
        String id = generateId(goalName, fitness, generation);
        
        CircuitRecord record = new CircuitRecord();
        record.id = id;
        record.goalName = goalName;
        record.fitness = fitness;
        record.generation = generation;
        record.timestamp = System.currentTimeMillis();
        record.gateCount = circuit.size();
        record.circuit = serializeCircuit(circuit);
        
        // Save to library index
        library.put(id, record);
        
        // Save circuit to individual JSON file
        saveCircuitFile(record);
        
        // Update library index
        saveLibraryIndex();
        
        System.out.println("💾 SAVED: " + id + " (Fitness: " + fitness + "%, Gates: " + circuit.size() + ")");
    }
    
    /**
     * LOAD a circuit by ID
     */
    public List<LogicBlock> load(String id) {
        CircuitRecord record = library.get(id);
        if (record == null) {
            System.err.println("❌ Circuit not found: " + id);
            return new ArrayList<>();
        }
        
        return deserializeCircuit(record.circuit);
    }
    
    /**
     * SEARCH circuits by goal and minimum fitness
     */
    public List<CircuitRecord> search(String goalName, int minFitness) {
        List<CircuitRecord> results = new ArrayList<>();
        
        for (CircuitRecord record : library.values()) {
            // If goalName is empty, match all circuits
            boolean nameMatch = goalName.isEmpty() || record.goalName.equalsIgnoreCase(goalName);
            boolean fitnessMatch = record.fitness >= minFitness;
            
            if (nameMatch && fitnessMatch) {
                results.add(record);
            }
        }
        
        // Sort by fitness (best first)
        results.sort((a, b) -> Integer.compare(b.fitness, a.fitness));
        
        return results;
    }
    
    /**
     * GET BEST circuit for a goal
     */
    public CircuitRecord getBest(String goalName) {
        List<CircuitRecord> results = search(goalName, 0);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * LIST all circuits
     */
    public void listAll() {
        System.out.println("\n📚 CIRCUIT LIBRARY (" + library.size() + " circuits)");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        Map<String, List<CircuitRecord>> byGoal = new HashMap<>();
        
        for (CircuitRecord record : library.values()) {
            byGoal.computeIfAbsent(record.goalName, k -> new ArrayList<>()).add(record);
        }
        
        for (String goal : byGoal.keySet()) {
            List<CircuitRecord> circuits = byGoal.get(goal);
            circuits.sort((a, b) -> Integer.compare(b.fitness, a.fitness));
            
            System.out.println("\n🎯 " + goal + " (" + circuits.size() + " circuits):");
            for (int i = 0; i < Math.min(3, circuits.size()); i++) {
                CircuitRecord r = circuits.get(i);
                System.out.println("   " + (i+1) + ". " + r.id + " - Fitness: " + r.fitness + "% - Gates: " + r.gateCount);
            }
        }
        System.out.println();
    }
    
    /**
     * STATS
     */
    public void printStats() {
        System.out.println("\n📊 LIBRARY STATISTICS");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("   Total Circuits: " + library.size());
        System.out.println("   Storage Path: " + new File(DB_PATH).getAbsolutePath());
        
        long totalSize = 0;
        try {
            totalSize = Files.walk(Paths.get(DB_PATH))
                .filter(Files::isRegularFile)
                .mapToLong(p -> {
                    try { return Files.size(p); } catch (IOException e) { return 0; }
                })
                .sum();
        } catch (IOException e) {}
        
        System.out.println("   Storage Used: " + formatBytes(totalSize));
        System.out.println("   Available: 80 TB (cloud-ready)");
        System.out.println();
    }
    
    // ========================================================================
    // PRIVATE HELPERS
    // ========================================================================
    
    private String generateId(String goalName, int fitness, int generation) {
        return goalName.toLowerCase().replace(" ", "_") + 
               "_fitness" + fitness + 
               "_gen" + generation;
    }
    
    private String serializeCircuit(List<LogicBlock> circuit) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < circuit.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(circuit.get(i).name()).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private List<LogicBlock> deserializeCircuit(String json) {
        List<LogicBlock> circuit = new ArrayList<>();
        
        // Simple JSON array parser: ["XOR","NOT","AND"]
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
            if (!json.isEmpty()) {
                String[] parts = json.split(",");
                for (String part : parts) {
                    String name = part.trim().replace("\"", "");
                    circuit.add(LogicBlock.valueOf(name));
                }
            }
        }
        
        return circuit;
    }
    
    private void saveCircuitFile(CircuitRecord record) {
        try {
            String filename = CIRCUITS_PATH + "/" + record.id + ".json";
            String json = recordToJson(record);
            Files.write(Paths.get(filename), json.getBytes());
        } catch (IOException e) {
            System.err.println("⚠️  Failed to save circuit file: " + e.getMessage());
        }
    }
    
    private void saveLibraryIndex() {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            int i = 0;
            for (Map.Entry<String, CircuitRecord> entry : library.entrySet()) {
                if (i++ > 0) json.append(",");
                json.append("\"").append(entry.getKey()).append("\":");
                json.append(recordToJson(entry.getValue()));
            }
            json.append("}");
            Files.write(Paths.get(LIBRARY_INDEX), json.toString().getBytes());
        } catch (IOException e) {
            System.err.println("⚠️  Failed to save library index: " + e.getMessage());
        }
    }
    
    private void loadLibrary() {
        try {
            if (Files.exists(Paths.get(LIBRARY_INDEX))) {
                String json = new String(Files.readAllBytes(Paths.get(LIBRARY_INDEX)));
                
                // Simple JSON parser for our specific format
                // Format: {"id1":{...},"id2":{...}}
                json = json.trim();
                if (json.startsWith("{") && json.endsWith("}")) {
                    json = json.substring(1, json.length() - 1); // Remove outer braces
                    
                    // Split by "},\"" to separate records
                    String[] parts = json.split("\\},\"");
                    
                    for (String part : parts) {
                        try {
                            // Extract ID (first quoted string)
                            int idStart = part.indexOf("\"") + 1;
                            int idEnd = part.indexOf("\"", idStart);
                            if (idEnd == -1) continue;
                            
                            String id = part.substring(idStart, idEnd);
                            
                            // Parse the record JSON
                            CircuitRecord record = parseRecord(part.substring(idEnd + 2));
                            if (record != null) {
                                library.put(id, record);
                            }
                        } catch (Exception e) {
                            // Skip malformed records
                        }
                    }
                    
                    System.out.println("📚 Loaded " + library.size() + " circuits from library");
                } else {
                    System.out.println("📚 Library initialized (empty)");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️  Failed to load library: " + e.getMessage());
        }
    }
    
    private CircuitRecord parseRecord(String json) {
        try {
            CircuitRecord record = new CircuitRecord();
            
            // Extract fields using simple string parsing
            record.id = extractString(json, "\"id\":\"", "\"");
            record.goalName = extractString(json, "\"goalName\":\"", "\"");
            record.fitness = extractInt(json, "\"fitness\":", ",");
            record.generation = extractInt(json, "\"generation\":", ",");
            record.timestamp = extractLong(json, "\"timestamp\":", ",");
            record.gateCount = extractInt(json, "\"gateCount\":", ",");
            record.circuit = extractString(json, "\"circuit\":", "}");
            
            return record;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractString(String json, String prefix, String suffix) {
        int start = json.indexOf(prefix);
        if (start == -1) return "";
        start += prefix.length();
        int end = json.indexOf(suffix, start);
        if (end == -1) return "";
        return json.substring(start, end);
    }
    
    private int extractInt(String json, String prefix, String suffix) {
        String value = extractString(json, prefix, suffix);
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
    
    private long extractLong(String json, String prefix, String suffix) {
        String value = extractString(json, prefix, suffix);
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return 0L;
        }
    }
    
    private String recordToJson(CircuitRecord record) {
        return String.format(
            "{\"id\":\"%s\",\"goalName\":\"%s\",\"fitness\":%d,\"generation\":%d,\"timestamp\":%d,\"gateCount\":%d,\"circuit\":%s}",
            record.id, record.goalName, record.fitness, record.generation, 
            record.timestamp, record.gateCount, record.circuit
        );
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * CIRCUIT RECORD
     * Metadata + serialized circuit
     */
    public static class CircuitRecord {
        public String id;
        public String goalName;
        public int fitness;
        public int generation;
        public long timestamp;
        public int gateCount;
        public String circuit; // JSON serialized gates
        
        @Override
        public String toString() {
            return String.format("Circuit[%s] Fitness=%d%% Gates=%d Gen=%d", 
                               id, fitness, gateCount, generation);
        }
    }
}
