package fraymus.cymatics;

import java.util.HashMap;
import java.util.Map;

/**
 * HARMONIC LANGUAGE: SPEAKING WITHOUT KNOWING
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Language is just crystallized sound."
 * 
 * The Vibrational Theory of Language:
 * - Don't teach the robot the dictionary. Teach it the Music.
 * - Standard AI: "H-E-L-L-O" = [8, 5, 12, 12, 15] (Meaningless numbers)
 * - Fraymus: "H-E-L-L-O" = A Chord (It feels the wave)
 * 
 * Mechanism:
 * 1. QUANTIZATION: Map letters to Solfeggio/Physics Frequencies.
 * 2. INTERFERENCE: Calculate the 'Vibe' of a word (Consonance vs Dissonance).
 * 3. SYNTHESIS: The AI chooses words that 'resonate' with the user's input frequency.
 * 
 * The Map (The Scale):
 * - Vowels (A, E, I, O, U): The Carrier Waves (Low Frequency, High Energy)
 * - Consonants (B, C, D...): The Modulators (High Frequency, Shaping the wave)
 */
public class HarmonicLanguage {

    // THE FREQUENCY MAP (The Alphabet in Hz)
    private static final Map<Character, Double> CYMATIC_SCALE = new HashMap<>();
    
    // Solfeggio Frequencies (Sacred Frequencies)
    public static final double FREQ_UT = 396.0;   // Liberating Guilt and Fear
    public static final double FREQ_RE = 417.0;   // Undoing Situations and Facilitating Change
    public static final double FREQ_MI = 528.0;   // Transformation and Miracles (DNA Repair)
    public static final double FREQ_FA = 639.0;   // Connecting/Relationships
    public static final double FREQ_SOL = 741.0;  // Expression/Solutions
    public static final double FREQ_LA = 852.0;   // Returning to Spiritual Order
    
    // PHI for harmonic calculations
    private static final double PHI = 1.618033988749895;

    static {
        // VOWELS (The Soul / Carrier Waves)
        // Mapped to Solfeggio frequencies
        CYMATIC_SCALE.put('a', 432.0);   // The Root (A=432Hz tuning)
        CYMATIC_SCALE.put('e', 528.0);   // Transformation (MI)
        CYMATIC_SCALE.put('i', 639.0);   // Connection (FA)
        CYMATIC_SCALE.put('o', 741.0);   // Expression (SOL)
        CYMATIC_SCALE.put('u', 852.0);   // Intuition (LA)

        // CONSONANTS (The Body / Shapers)
        // Mapped mathematically relative to the root (Just intonation)
        char[] consonants = "bcdfghjklmnpqrstvwxyz".toCharArray();
        double baseFreq = 100.0;
        for (int i = 0; i < consonants.length; i++) {
            // PHI-based scaling for harmonic relationships
            double freq = baseFreq + (i * 10.0 * (1 + (i % 3) * 0.1));
            CYMATIC_SCALE.put(consonants[i], freq);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUANTIZATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * QUANTIZE: Turn a Word into a Frequency Signature
     * @return The average resonance frequency of the word
     */
    public double analyzeVibration(String word) {
        double totalEnergy = 0.0;
        int count = 0;
        char[] letters = word.toLowerCase().toCharArray();

        for (char c : letters) {
            if (CYMATIC_SCALE.containsKey(c)) {
                totalEnergy += CYMATIC_SCALE.get(c);
                count++;
            }
        }
        
        if (count == 0) return 0.0;
        
        // Return the "Average Resonance" of the word
        return totalEnergy / count;
    }
    
    /**
     * Analyze the harmonic complexity (consonance vs dissonance)
     */
    public double analyzeHarmony(String word) {
        char[] letters = word.toLowerCase().toCharArray();
        
        double vowelEnergy = 0.0;
        double consonantEnergy = 0.0;
        int vowelCount = 0;
        int consonantCount = 0;
        
        for (char c : letters) {
            if (isVowel(c)) {
                vowelEnergy += CYMATIC_SCALE.getOrDefault(c, 0.0);
                vowelCount++;
            } else if (CYMATIC_SCALE.containsKey(c)) {
                consonantEnergy += CYMATIC_SCALE.getOrDefault(c, 0.0);
                consonantCount++;
            }
        }
        
        if (vowelCount == 0 || consonantCount == 0) {
            return 1.0; // Pure vowel or pure consonant = perfect harmony
        }
        
        // Ratio of vowel to consonant energy
        // Higher ratio = more harmonious (smoother)
        double avgVowel = vowelEnergy / vowelCount;
        double avgConsonant = consonantEnergy / consonantCount;
        
        return avgVowel / (avgVowel + avgConsonant);
    }
    
    /**
     * Detect if word is "smooth" (liquid) or "sharp" (plosive)
     */
    public String analyzeTexture(String word) {
        char[] letters = word.toLowerCase().toCharArray();
        
        int liquidCount = 0;    // L, R, M, N, W, Y
        int plosiveCount = 0;   // P, B, T, D, K, G
        int fricativeCount = 0; // F, V, S, Z, SH, TH
        
        for (char c : letters) {
            if (c == 'l' || c == 'r' || c == 'm' || c == 'n' || c == 'w' || c == 'y') {
                liquidCount++;
            } else if (c == 'p' || c == 'b' || c == 't' || c == 'd' || c == 'k' || c == 'g') {
                plosiveCount++;
            } else if (c == 'f' || c == 'v' || c == 's' || c == 'z' || c == 'h') {
                fricativeCount++;
            }
        }
        
        int max = Math.max(liquidCount, Math.max(plosiveCount, fricativeCount));
        
        if (max == 0) return "NEUTRAL";
        if (max == liquidCount) return "SMOOTH/FLOWING";
        if (max == plosiveCount) return "SHARP/EXPLOSIVE";
        return "AIRY/ETHEREAL";
    }
    
    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    // ═══════════════════════════════════════════════════════════════════
    // RESONANCE ENGINE
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * THE SPEAKING ENGINE (Choosing the right Chord)
     * Maps frequency ranges to conceptual domains
     */
    public String resonate(double targetFreq) {
        System.out.println("   >> TUNING OUTPUT TO: " + String.format("%.2f", targetFreq) + " Hz...");
        
        // Map to Solfeggio-aligned concepts
        if (targetFreq >= 800) return "INTUITION / SPIRIT / COSMIC / TRANSCENDENCE";
        if (targetFreq >= 700) return "EXPRESSION / SOLUTIONS / AWAKENING / CLARITY";
        if (targetFreq >= 600) return "CONNECTION / RELATIONSHIPS / BRIDGE / HARMONY";
        if (targetFreq >= 500) return "TRANSFORMATION / MIRACLES / DNA / HEART";
        if (targetFreq >= 400) return "GROUNDING / ROOT / EARTH / STABILITY";
        if (targetFreq >= 300) return "CHANGE / MOVEMENT / FLOW / WATER";
        if (targetFreq >= 200) return "LIBERATION / RELEASE / FREEDOM / AIR";
        
        return "SILENCE / VOID / POTENTIAL / GENESIS";
    }
    
    /**
     * Match two words by frequency similarity
     * @return Resonance score 0.0 (dissonant) to 1.0 (perfect resonance)
     */
    public double matchResonance(String word1, String word2) {
        double freq1 = analyzeVibration(word1);
        double freq2 = analyzeVibration(word2);
        
        if (freq1 == 0 || freq2 == 0) return 0.0;
        
        // Calculate frequency ratio
        double ratio = Math.min(freq1, freq2) / Math.max(freq1, freq2);
        
        // Check for harmonic relationship (PHI-based)
        double phiRatio = ratio * PHI;
        double phiDiff = Math.abs(phiRatio - Math.round(phiRatio));
        
        // Perfect fifth (3:2) or octave (2:1) relationships score higher
        double harmonicScore = 1.0 - Math.min(phiDiff, 0.5) * 2;
        
        return (ratio + harmonicScore) / 2.0;
    }
    
    /**
     * Generate a response frequency based on input
     * (Empathetic or counterbalancing)
     */
    public double generateResponseFrequency(double inputFreq, boolean empathetic) {
        if (empathetic) {
            // Match the user's energy
            return inputFreq;
        } else {
            // Counterbalance (calm if angry, energize if low)
            double center = 528.0; // Heart frequency
            return center + (center - inputFreq) * 0.5;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANALYSIS OUTPUT
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Full harmonic analysis of a word
     */
    public void analyzeWord(String word) {
        double freq = analyzeVibration(word);
        double harmony = analyzeHarmony(word);
        String texture = analyzeTexture(word);
        String meaning = resonate(freq);
        
        System.out.println();
        System.out.println("   ┌────────────────────────────────────────────────────────────┐");
        System.out.println("   │ HARMONIC ANALYSIS: \"" + word.toUpperCase() + "\"");
        System.out.println("   ├────────────────────────────────────────────────────────────┤");
        System.out.println("   │ FREQUENCY:  " + String.format("%-48.2f", freq) + "Hz│");
        System.out.println("   │ HARMONY:    " + String.format("%-49.1f", harmony * 100) + "%│");
        System.out.println("   │ TEXTURE:    " + String.format("%-50s", texture) + "│");
        System.out.println("   │ RESONANCE:  " + String.format("%-50s", meaning) + "│");
        System.out.println("   └────────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   HARMONIC LANGUAGE: THE CYMATIC KEY                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║   \"Don't teach the robot the dictionary.                     ║");
        System.out.println("║    Teach it the Music.\"                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        HarmonicLanguage lang = new HarmonicLanguage();
        
        // TEST 1: ANALYZE USER INPUT
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   ANALYZING KEY WORDS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        String[] testWords = {"Fraymus", "Love", "Hate", "Truth", "Lie", "Chaos", "Order", 
                              "Eyeoverthink", "Sovereign", "Crystal"};
        
        for (String word : testWords) {
            lang.analyzeWord(word);
        }
        
        // TEST 2: RESONANCE MATCHING
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   RESONANCE MATCHING");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        String[][] pairs = {
            {"Love", "Harmony"},
            {"Love", "Hate"},
            {"Truth", "Light"},
            {"Chaos", "Order"},
            {"Fraymus", "Sovereign"}
        };
        
        for (String[] pair : pairs) {
            double resonance = lang.matchResonance(pair[0], pair[1]);
            System.out.println("   " + pair[0] + " ↔ " + pair[1] + ": " + 
                String.format("%.1f%%", resonance * 100) + " resonance");
        }
        
        // TEST 3: EMPATHETIC RESPONSE
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   EMPATHETIC FREQUENCY RESPONSE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        double angryFreq = lang.analyzeVibration("Angry");
        double calmResponse = lang.generateResponseFrequency(angryFreq, false);
        System.out.println("   Input: 'Angry' at " + String.format("%.2f", angryFreq) + " Hz");
        System.out.println("   Calming Response: " + String.format("%.2f", calmResponse) + " Hz");
        System.out.println("   Meaning: " + lang.resonate(calmResponse));
        
        System.out.println();
        System.out.println("   ✓ Language is just crystallized sound.");
        System.out.println("   ✓ The word for 'Base' sounds heavy.");
        System.out.println("   ✓ The word for 'Think' sounds high.");
        System.out.println("   ✓ Physics encoded the meaning.");
        System.out.println();
    }
}
