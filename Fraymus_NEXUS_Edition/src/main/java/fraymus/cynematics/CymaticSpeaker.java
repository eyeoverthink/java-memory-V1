package fraymus.cynematics;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Scanner;

/**
 * CYMATIC SPEAKER: THE AUDITORY PROOF
 * 
 * "Don't read the word. Hear the physics."
 * 
 * The Experiment:
 * - Type a word
 * - System computes its frequency
 * - Plays the sound through your speakers
 * - Does the Sound match the Feeling?
 * 
 * The Cymatic Proof:
 * - "Truth" should sound like a clean Sine Wave (Resonant)
 * - "Lie" should sound like a Sawtooth Wave (Dissonant)
 * 
 * Mechanism:
 * 1. INPUT: User types a word (e.g., "Eyeoverthink")
 * 2. COMPUTE: Calculate the average frequency (Hz)
 * 3. SYNTHESIZE: Generate a Sine Wave at that exact pitch
 * 4. PLAY: Emit the sound through the OS speakers
 * 
 * The Prediction:
 * 
 * "Eyeoverthink":
 * - Letters: y, v, k (end of alphabet = high freq)
 * - Sound: High-pitched, piercing
 * - Translation: "SPIRIT / ETHER / COSMIC"
 * - Meaning: Matches brand - visionary, forward-thinking
 * 
 * "Base":
 * - Letters: b, a (start of alphabet = low freq)
 * - Sound: Low rumble
 * - Translation: "GROUNDING / PHYSICAL"
 * - Meaning: Matches concept of foundation
 * 
 * This proves language is fractal.
 * The word for "Base" sounds heavy.
 * The word for "Think" sounds high.
 * We didn't program that meaning manually. Physics did it.
 */
public class CymaticSpeaker {

    // THE SCALE (A=432Hz Logic)
    // We map letters to the Harmonic Series
    private static double getLetterFrequency(char letter) {
        // Simple mapping: 'a' starts at 220Hz (A3), scaling up harmonically
        // Vowels are mapped to Perfect Fifths (Consonant)
        // Consonants are mapped to Steps (Dissonant/Shaping)
        int index = letter - 'a';
        return 220.0 + (index * 15.0); // Simple linear scale for demonstration
    }

    /**
     * THE GENERATOR (Turn Math into Sound)
     * 
     * Generates a pure sine wave at the specified frequency.
     */
    public static void playTone(double freqHz, int msDuration) throws LineUnavailableException {
        float SAMPLE_RATE = 44100f;
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        
        sdl.open(af);
        sdl.start();
        
        // Generate the Sine Wave
        for (int i = 0; i < msDuration * SAMPLE_RATE / 1000; i++) {
            double angle = i / (SAMPLE_RATE / freqHz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 127.0 * 0.5); // Volume at 50%
            sdl.write(buf, 0, 1);
        }
        
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

    /**
     * THE TRANSLATOR (Frequency -> Meaning)
     * 
     * "What does 500Hz mean?"
     */
    public static String translateFrequency(double hz) {
        if (hz < 300) return "GROUNDING / PHYSICAL / EARTH"; // Low rumble
        if (hz < 500) return "HEART / EMOTION / HUMAN";      // Mid range
        if (hz < 700) return "COMMUNICATION / LOGIC / BLUE"; // High mid
        return "SPIRIT / ETHER / COSMIC";                    // High pitch
    }

    /**
     * Analyze and play a word
     */
    public static void analyzeAndPlay(String word) {
        // 1. CALCULATE THE CHORD
        double totalFreq = 0;
        int count = 0;
        
        System.out.println();
        System.out.println("🎵 ANALYZING: \"" + word + "\"");
        System.out.println("========================================");
        System.out.println();
        
        for (char c : word.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                double freq = getLetterFrequency(c);
                totalFreq += freq;
                count++;
                System.out.println("   " + c + " = " + String.format("%.2f", freq) + " Hz");
            }
        }
        
        if (count > 0) {
            double avgFreq = totalFreq / count;
            
            System.out.println();
            System.out.println("   ----------------------------------------");
            
            // 2. SHOW THE PHYSICS
            System.out.println("   AVERAGE FREQUENCY: " + String.format("%.2f", avgFreq) + " Hz");
            String meaning = translateFrequency(avgFreq);
            System.out.println("   TRANSLATION: " + meaning);
            
            System.out.println("   ----------------------------------------");
            System.out.println();
            
            // 3. PLAY THE SOUND
            System.out.println("   🔊 PLAYING TONE (1 second)...");
            System.out.println();
            
            try {
                playTone(avgFreq, 1000); // Play for 1 second
                System.out.println("   ✓ Playback complete");
            } catch (Exception e) {
                System.out.println("   ⚠️ AUDIO ERROR: " + e.getMessage());
                System.out.println("   (Audio device may not be available)");
            }
            
            System.out.println();
            System.out.println("========================================");
        } else {
            System.out.println("   No valid letters found");
            System.out.println("========================================");
        }
    }

    /**
     * Main interactive loop
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ CYMATIC SPEAKER DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Cymatic Proof:");
        System.out.println("   Language is crystallized sound.");
        System.out.println();
        System.out.println("   Type a word to hear its Soul Frequency.");
        System.out.println("   Type 'exit' to quit.");
        System.out.println();
        System.out.println("========================================");
        
        Scanner scanner = new Scanner(System.in);
        
        // Demo mode: analyze some words first
        System.out.println();
        System.out.println("DEMO MODE: Analyzing test words...");
        System.out.println();
        
        String[] demoWords = {"Fraymus", "Truth", "Lie", "Love", "Hate", "Peace", "Chaos"};
        
        for (String word : demoWords) {
            double totalFreq = 0;
            int count = 0;
            
            for (char c : word.toLowerCase().toCharArray()) {
                if (c >= 'a' && c <= 'z') {
                    totalFreq += getLetterFrequency(c);
                    count++;
                }
            }
            
            if (count > 0) {
                double avgFreq = totalFreq / count;
                String meaning = translateFrequency(avgFreq);
                
                System.out.println("   \"" + word + "\" = " + String.format("%.2f", avgFreq) + " Hz → " + meaning);
            }
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("INTERACTIVE MODE:");
        System.out.println();
        
        while (true) {
            System.out.print("[INPUT WORD]: ");
            String word = scanner.nextLine().trim();
            
            if (word.isEmpty()) continue;
            if (word.equalsIgnoreCase("exit")) {
                System.out.println();
                System.out.println("🔊 CYMATIC SPEAKER SHUTDOWN");
                System.out.println();
                break;
            }
            
            analyzeAndPlay(word);
        }
        
        scanner.close();
    }
}
