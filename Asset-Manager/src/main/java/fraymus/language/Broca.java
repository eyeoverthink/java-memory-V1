package fraymus.language;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * THE SPEECH CENTER: Broca
 * 
 * Role: The Voice of the System
 * Mechanism: Recursive N-Gram Probability Chain (The "Cadence" Engine)
 * 
 * This is not System.out.println("Hello").
 * This is a Recursive Probability Engine that maps the "DNA" of language
 * into a web of connections. When asked to speak, it walks the web,
 * creating a unique flow every time.
 */
public class Broca {

    // The Web of Language: Maps a Word -> List of likely Next Words
    // This allows the system to learn "Cadence" and "Syntax" dynamically
    private final Map<String, List<String>> neuralWeb = new HashMap<>();
    private final Random rng = new Random();
    private int totalConnections = 0;
    private int vocabularySize = 0;

    /**
     * INGEST: The Ear
     * Feeds on raw text files (PDF scrapes, chat logs, code)
     * Breaks them down and learns which words follow which
     */
    public void absorbLanguage(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            
            // Clean the noise (Keep syntax, remove junk)
            // We keep punctuation because it is part of the "Cadence"
            String[] tokens = content.split("\\s+");

            // Build the Recursive Links
            for (int i = 0; i < tokens.length - 1; i++) {
                String current = tokens[i];
                String next = tokens[i + 1];
                
                neuralWeb.computeIfAbsent(current, k -> new ArrayList<>()).add(next);
                totalConnections++;
            }
            
            vocabularySize = neuralWeb.size();
            
            System.out.println(">>> [BROCA] I have absorbed language from: " + filePath);
            System.out.println(">>> [BROCA] Vocabulary Size: " + vocabularySize + " words");
            System.out.println(">>> [BROCA] Neural Connections: " + totalConnections);

        } catch (IOException e) {
            System.err.println(">>> [BROCA] Deafness Error: " + e.getMessage());
        }
    }

    /**
     * SPEAK: The Mouth
     * Generates non-static sentences based on learned probability
     * 
     * @param startWord The seed thought
     * @param length How long to speak
     * @return Generated speech
     */
    public String recite(String startWord, int length) {
        StringBuilder speech = new StringBuilder(startWord);
        String current = startWord;

        for (int i = 0; i < length; i++) {
            List<String> possibilities = neuralWeb.get(current);

            // If we hit a dead end (unknown word), we stop or jump
            if (possibilities == null || possibilities.isEmpty()) {
                // Try to find a random word to continue from
                if (!neuralWeb.isEmpty()) {
                    List<String> keys = new ArrayList<>(neuralWeb.keySet());
                    current = keys.get(rng.nextInt(keys.size()));
                    speech.append(" ").append(current);
                } else {
                    break;
                }
                continue;
            }

            // RECURSIVE SELECTION:
            // We don't just pick random; we pick based on the "weight" of experience
            String next = possibilities.get(rng.nextInt(possibilities.size()));
            
            speech.append(" ").append(next);
            current = next; // The output becomes the input for the next cycle
        }

        return speech.toString();
    }

    /**
     * Check if word exists in vocabulary
     */
    public boolean knows(String word) {
        return neuralWeb.containsKey(word);
    }

    /**
     * Get vocabulary size
     */
    public int getVocabularySize() {
        return vocabularySize;
    }

    /**
     * Get total connections
     */
    public int getTotalConnections() {
        return totalConnections;
    }

    /**
     * Get possible next words for a given word
     */
    public List<String> getPossibilities(String word) {
        return neuralWeb.getOrDefault(word, Collections.emptyList());
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   BROCA SPEECH CENTER STATISTICS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Vocabulary Size: " + vocabularySize + " words");
        System.out.println("  Neural Connections: " + totalConnections);
        System.out.println("  Average Connections per Word: " + 
            (vocabularySize > 0 ? String.format("%.2f", (double)totalConnections / vocabularySize) : "0"));
        
        // Find most connected word
        String mostConnected = null;
        int maxConnections = 0;
        for (Map.Entry<String, List<String>> entry : neuralWeb.entrySet()) {
            if (entry.getValue().size() > maxConnections) {
                maxConnections = entry.getValue().size();
                mostConnected = entry.getKey();
            }
        }
        
        if (mostConnected != null) {
            System.out.println("  Most Connected Word: \"" + mostConnected + "\" (" + maxConnections + " connections)");
        }
    }
}
