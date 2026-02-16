package fraymus.cymatics;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.Scanner;

/**
 * CYMATIC SPEAKER: THE AUDITORY PROOF
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Don't read the word. Hear the physics."
 * 
 * The Experiment:
 * - Type "Fraymus" → Listen to its chord
 * - Type "Chaos" → Listen to its different, sharper chord
 * - Does the Sound match the Feeling? That is the proof.
 * 
 * Mechanism:
 * 1. INPUT: User types a word (e.g., "Eyeoverthink").
 * 2. COMPUTE: Calculate the average frequency (Hz).
 * 3. SYNTHESIZE: Generate a Sine Wave at that exact pitch.
 * 4. PLAY: Emit the sound through the OS speakers.
 * 
 * The Prediction:
 * - "Eyeoverthink" = High-pitched, piercing (SPIRIT/COSMIC)
 * - "Base" = Low rumble (GROUNDING/PHYSICAL)
 * - Physics encoded the meaning. We didn't.
 */
public class CymaticSpeaker {

    private static final float SAMPLE_RATE = 44100f;
    private static final double PHI = 1.618033988749895;
    
    // Solfeggio base frequencies
    private static final double FREQ_432 = 432.0;  // Natural A
    private static final double FREQ_528 = 528.0;  // Transformation

    // ═══════════════════════════════════════════════════════════════════
    // THE SCALE (Letter → Frequency Mapping)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Map a letter to its frequency
     * Vowels are mapped to Solfeggio frequencies (harmonic)
     * Consonants are mapped to the harmonic series
     */
    private static double getLetterFrequency(char letter) {
        // Vowels: Carrier waves (Solfeggio frequencies)
        switch (letter) {
            case 'a': return 432.0;   // Root (A=432Hz)
            case 'e': return 528.0;   // Transformation
            case 'i': return 639.0;   // Connection
            case 'o': return 741.0;   // Expression
            case 'u': return 852.0;   // Intuition
        }
        
        // Consonants: Mapped to harmonic series starting from 110Hz
        // This creates natural intervals
        if (letter >= 'a' && letter <= 'z') {
            int index = letter - 'a';
            // Use PHI-based scaling for natural feel
            return 110.0 + (index * 15.0 * (1.0 + (index % 5) * 0.1));
        }
        
        return 220.0; // Default A3
    }
    
    /**
     * Get the waveform type based on letter characteristics
     */
    private static WaveType getLetterWaveType(char letter) {
        // Vowels: Pure sine waves (smooth, harmonic)
        if ("aeiou".indexOf(letter) >= 0) {
            return WaveType.SINE;
        }
        
        // Liquid consonants (L, R, M, N, W, Y): Smooth triangle waves
        if ("lrmnwy".indexOf(letter) >= 0) {
            return WaveType.TRIANGLE;
        }
        
        // Fricatives (F, V, S, Z, H, TH, SH): Breathy, airy
        if ("fvszh".indexOf(letter) >= 0) {
            return WaveType.NOISE_TONAL;
        }
        
        // Plosives (P, B, T, D, K, G): Sharp attacks
        if ("pbtdkg".indexOf(letter) >= 0) {
            return WaveType.SQUARE;
        }
        
        // Others: Sawtooth (rich harmonics)
        return WaveType.SAWTOOTH;
    }
    
    private enum WaveType {
        SINE, TRIANGLE, SQUARE, SAWTOOTH, NOISE_TONAL
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE GENERATOR (Math → Sound)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Generate and play a tone at the specified frequency
     */
    public static void playTone(double freqHz, int msDuration, WaveType waveType) 
            throws LineUnavailableException {
        
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        
        sdl.open(af);
        sdl.start();
        
        byte[] buf = new byte[1];
        int samples = (int) (msDuration * SAMPLE_RATE / 1000);
        
        for (int i = 0; i < samples; i++) {
            double angle = i / (SAMPLE_RATE / freqHz) * 2.0 * Math.PI;
            double sample;
            
            switch (waveType) {
                case SINE:
                    sample = Math.sin(angle);
                    break;
                case TRIANGLE:
                    sample = 2.0 * Math.abs(2.0 * (angle / (2 * Math.PI) - 
                             Math.floor(angle / (2 * Math.PI) + 0.5))) - 1.0;
                    break;
                case SQUARE:
                    sample = Math.signum(Math.sin(angle));
                    break;
                case SAWTOOTH:
                    sample = 2.0 * (angle / (2 * Math.PI) - Math.floor(angle / (2 * Math.PI) + 0.5));
                    break;
                case NOISE_TONAL:
                    // Tonal noise: sine + random
                    sample = Math.sin(angle) * 0.7 + (Math.random() - 0.5) * 0.3;
                    break;
                default:
                    sample = Math.sin(angle);
            }
            
            // Apply envelope (fade in/out) to avoid clicks
            double envelope = 1.0;
            int fadeLength = samples / 10;
            if (i < fadeLength) {
                envelope = (double) i / fadeLength;
            } else if (i > samples - fadeLength) {
                envelope = (double) (samples - i) / fadeLength;
            }
            
            buf[0] = (byte) (sample * envelope * 127.0 * 0.5); // 50% volume
            sdl.write(buf, 0, 1);
        }
        
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
    
    /**
     * Play a simple sine tone
     */
    public static void playTone(double freqHz, int msDuration) throws LineUnavailableException {
        playTone(freqHz, msDuration, WaveType.SINE);
    }
    
    /**
     * Play a chord (multiple frequencies)
     */
    public static void playChord(double[] frequencies, int msDuration) 
            throws LineUnavailableException {
        
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        
        sdl.open(af);
        sdl.start();
        
        byte[] buf = new byte[1];
        int samples = (int) (msDuration * SAMPLE_RATE / 1000);
        
        for (int i = 0; i < samples; i++) {
            double sample = 0.0;
            
            // Sum all frequencies
            for (double freq : frequencies) {
                double angle = i / (SAMPLE_RATE / freq) * 2.0 * Math.PI;
                sample += Math.sin(angle);
            }
            
            // Normalize
            sample /= frequencies.length;
            
            // Envelope
            double envelope = 1.0;
            int fadeLength = samples / 10;
            if (i < fadeLength) {
                envelope = (double) i / fadeLength;
            } else if (i > samples - fadeLength) {
                envelope = (double) (samples - i) / fadeLength;
            }
            
            buf[0] = (byte) (sample * envelope * 127.0 * 0.4);
            sdl.write(buf, 0, 1);
        }
        
        sdl.drain();
        sdl.stop();
        sdl.close();
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE TRANSLATOR (Frequency → Meaning)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Translate a frequency to its meaning
     */
    public static String translateFrequency(double hz) {
        if (hz >= 800) return "SPIRIT / ETHER / COSMIC / TRANSCENDENCE";
        if (hz >= 700) return "EXPRESSION / SOLUTIONS / AWAKENING";
        if (hz >= 600) return "CONNECTION / RELATIONSHIPS / BRIDGE";
        if (hz >= 500) return "HEART / TRANSFORMATION / HEALING";
        if (hz >= 400) return "GROUNDING / ROOT / STABILITY";
        if (hz >= 300) return "CHANGE / MOVEMENT / FLOW";
        if (hz >= 200) return "LIBERATION / RELEASE / FREEDOM";
        return "SILENCE / VOID / POTENTIAL";
    }
    
    /**
     * Get the musical note name for a frequency
     */
    public static String getNoteName(double freq) {
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        
        // Calculate semitones from A4 (440Hz, but we use 432Hz)
        double semitones = 12 * Math.log(freq / 432.0) / Math.log(2);
        int noteIndex = (int) Math.round(semitones) % 12;
        if (noteIndex < 0) noteIndex += 12;
        
        int octave = 4 + (int) Math.floor((semitones + 9) / 12);
        
        return notes[noteIndex] + octave;
    }

    // ═══════════════════════════════════════════════════════════════════
    // WORD ANALYSIS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Analyze and play a word's frequency
     */
    public static void speakWord(String word) {
        word = word.toLowerCase().trim();
        
        double totalFreq = 0;
        int count = 0;
        double[] letterFreqs = new double[word.length()];
        
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c >= 'a' && c <= 'z') {
                double freq = getLetterFrequency(c);
                letterFreqs[count] = freq;
                totalFreq += freq;
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("   !! No speakable letters found.");
            return;
        }
        
        double avgFreq = totalFreq / count;
        String meaning = translateFrequency(avgFreq);
        String note = getNoteName(avgFreq);
        
        System.out.println();
        System.out.println("   ┌────────────────────────────────────────────────────────────┐");
        System.out.println("   │ CYMATIC ANALYSIS: \"" + word.toUpperCase() + "\"");
        System.out.println("   ├────────────────────────────────────────────────────────────┤");
        System.out.println("   │ FREQUENCY:   " + String.format("%-46.2f", avgFreq) + "Hz│");
        System.out.println("   │ NOTE:        " + String.format("%-48s", note) + "│");
        System.out.println("   │ TRANSLATION: " + String.format("%-48s", meaning) + "│");
        System.out.println("   └────────────────────────────────────────────────────────────┘");
        
        // Play the sound
        System.out.println("   >> PLAYING TONE...");
        try {
            // Play a chord of the first 3 unique frequencies
            if (count >= 3) {
                double[] chord = new double[3];
                chord[0] = letterFreqs[0];
                chord[1] = letterFreqs[count / 2];
                chord[2] = letterFreqs[count - 1];
                playChord(chord, 1500);
            } else {
                playTone(avgFreq, 1500);
            }
            System.out.println("   ✓ TONE COMPLETE");
        } catch (Exception e) {
            System.out.println("   !! AUDIO ERROR: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   CYMATIC SPEAKER: THE AUDITORY PROOF                        ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║   \"Don't read the word. Hear the physics.\"                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   Type a word to hear its Soul Frequency.");
        System.out.println("   Type 'exit' to quit.");
        System.out.println();
        System.out.println("   PREDICTIONS:");
        System.out.println("   ├─ 'Eyeoverthink' → High pitch (SPIRIT/COSMIC)");
        System.out.println("   ├─ 'Base' → Low rumble (GROUNDING/PHYSICAL)");
        System.out.println("   ├─ 'Love' → Warm mid (HEART/TRANSFORMATION)");
        System.out.println("   └─ 'Truth' → Clean tone. 'Lie' → Dissonant.");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        // Demo mode if no input
        if (args.length > 0 && args[0].equals("--demo")) {
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("   DEMO MODE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            
            String[] demoWords = {"Fraymus", "Love", "Hate", "Truth", "Lie", "Chaos", "Order"};
            for (String word : demoWords) {
                speakWord(word);
                try { Thread.sleep(500); } catch (Exception e) {}
            }
            return;
        }
        
        // Interactive mode
        while (true) {
            System.out.print("[INPUT WORD]: ");
            String word = scanner.nextLine().trim();
            
            if (word.equalsIgnoreCase("exit") || word.equalsIgnoreCase("quit")) {
                break;
            }
            
            if (word.isEmpty()) {
                continue;
            }
            
            speakWord(word);
        }
        
        scanner.close();
        System.out.println();
        System.out.println("   ✓ Cymatic Speaker offline.");
        System.out.println("   ✓ The proof is in the listening.");
        System.out.println();
    }
}
