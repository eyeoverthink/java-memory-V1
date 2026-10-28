package fraymus.neural;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * AudioEngine - Audio processing for AGI system
 * 
 * Phase 5: Embodiment & Multi-Modal
 * 
 * This class provides:
 * - Audio input processing
 * - Feature extraction
 * - Speech recognition (placeholder)
 * - Sound classification (placeholder)
 * 
 * RULE SC-1: Never modify existing data structures without safety checks
 * RULE SC-2: Maintains backward compatibility - optional enhancement
 */
public class AudioEngine {
    
    private final SentientCore core;
    private final HDCSimilarityEngine hdcEngine;
    private final Map<String, AudioFeature> features;
    
    // Statistics
    private int audioSegmentsProcessed = 0;
    private int featuresExtracted = 0;
    private long totalProcessingTimeMs = 0;

    private static final long SPEECHBRAIN_TIMEOUT_SECONDS = 120;
    private static final String LEGACY_SPEECHBRAIN_SCRIPT =
        "F:/Full-AI-System/Java-Memory/Asset-Manager/speechbrain_interface.py";
    
    public AudioEngine(SentientCore core, HDCSimilarityEngine hdcEngine) {
        if (core == null) {
            throw new IllegalArgumentException("SentientCore cannot be null");
        }
        this.core = core;
        this.hdcEngine = hdcEngine; // Optional
        this.features = new HashMap<>();
    }
    
    /**
     * Process audio segment (placeholder - in production would use actual audio processing)
     * 
     * @param audioId Identifier for the audio
     * @param audioData Audio data (in production, would be actual audio bytes)
     * @return Processing result
     */
    public AudioResult processAudio(String audioId, byte[] audioData) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (audioId == null || audioData == null) {
                return new AudioResult(false, "Invalid input", 0, 0);
            }
            
            // Process audio (placeholder)
            // In production, this would:
            // 1. Load the audio
            // 2. Extract features using MFCC or similar
            // 3. Recognize speech
            // 4. Classify sounds
            
            audioSegmentsProcessed++;
            
            // Extract features (simplified)
            int featureCount = extractFeatures(audioId, audioData);
            
            // Log to core
            core.learn("audio input: audio " + audioId + " with " + featureCount + " features");
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs += elapsed;
            
            return new AudioResult(true, "Audio processed", featureCount, elapsed);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new AudioResult(false, "ERROR: " + e.getMessage(), 0, elapsed);
        }
    }
    
    /**
     * Extract features from audio (placeholder implementation)
     */
    private int extractFeatures(String audioId, byte[] audioData) {
        // In production, this would use actual feature extraction (MFCC, etc.)
        // For now, simulate with simple hash-based features
        int featureCount = Math.min(50, audioData.length / 200);
        
        for (int i = 0; i < featureCount; i++) {
            String featureId = audioId + "_feature_" + i;
            double featureValue = (audioData[i % audioData.length] & 0xFF) / 255.0;
            features.put(featureId, new AudioFeature(featureId, featureValue));
            featuresExtracted++;
        }
        
        return featureCount;
    }
    
    /**
     * Recognize speech using SpeechBrain
     * 
     * @param audioFile Path to audio file
     * @return Recognized text, or error message if SpeechBrain not available
     */
    public String recognizeSpeech(String audioFile) {
        try {
            Path resolvedAudio = resolveAudioPath(audioFile);
            if (resolvedAudio == null || !Files.exists(resolvedAudio) || Files.isDirectory(resolvedAudio)) {
                return "Speech recognition failed: audio file not found: " + audioFile;
            }

            String scriptPath = resolveSpeechBrainScript();
            if (scriptPath == null) {
                return "Speech recognition failed: speechbrain_interface.py not found";
            }

            // Call Python script for speech recognition
            ProcessBuilder pb = new ProcessBuilder(
                resolvePythonCommand(),
                scriptPath,
                "recognize",
                resolvedAudio.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            boolean completed = process.waitFor(SPEECHBRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                return "Speech recognition failed: timed out after " + SPEECHBRAIN_TIMEOUT_SECONDS + "s";
            }

            // Parse JSON result using simple string parsing (avoiding dependency on JSON library)
            String result = output.toString().trim();
            if (result.isEmpty()) {
                return "Speech recognition failed: no output from speechbrain interface";
            }

            if (result.contains("\"success\":") && result.contains("true")) {
                String text = extractJsonStringField(result, "text");
                if (text != null && !text.isBlank()) {
                    return text;
                }
            }

            String error = extractJsonStringField(result, "error");
            if (error != null && !error.isBlank()) {
                return "Speech recognition failed: " + humanizeSpeechBrainError(error);
            }
            return "Speech recognition failed: " + result;

        } catch (Exception e) {
            return "Speech recognition error: " + e.getMessage();
        }
    }
    
    /**
     * Recognize speech from audio ID (placeholder - needs file mapping)
     */
    public String recognizeSpeechById(String audioId) {
        // In production, this would map audioId to actual file path
        // For now, return placeholder
        return "Speech recognition from ID not yet implemented (needs file mapping)";
    }
    
    /**
     * Classify sound using SpeechBrain
     * 
     * @param audioFile Path to audio file
     * @return Classification result, or error message if SpeechBrain not available
     */
    public String classifySound(String audioFile) {
        try {
            Path resolvedAudio = resolveAudioPath(audioFile);
            if (resolvedAudio == null || !Files.exists(resolvedAudio) || Files.isDirectory(resolvedAudio)) {
                return "Sound classification failed: audio file not found: " + audioFile;
            }

            String scriptPath = resolveSpeechBrainScript();
            if (scriptPath == null) {
                return "Sound classification failed: speechbrain_interface.py not found";
            }

            // Call Python script for audio classification
            ProcessBuilder pb = new ProcessBuilder(
                resolvePythonCommand(),
                scriptPath,
                "classify",
                resolvedAudio.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            
            boolean completed = process.waitFor(SPEECHBRAIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                return "Sound classification failed: timed out after " + SPEECHBRAIN_TIMEOUT_SECONDS + "s";
            }
            
            // Parse JSON result using simple string parsing
            String result = output.toString().trim();
            if (result.isEmpty()) {
                return "Sound classification failed: no output from speechbrain interface";
            }

            if (result.contains("\"success\":") && result.contains("true")) {
                String className = extractJsonStringField(result, "class");
                String confidence = extractJsonNumberField(result, "confidence");
                if (className != null && !className.isBlank()) {
                    if (confidence != null && !confidence.isBlank()) {
                        return className + " (confidence: " + confidence + ")";
                    }
                    return className;
                }
            }

            String error = extractJsonStringField(result, "error");
            if (error != null && !error.isBlank()) {
                return "Sound classification failed: " + humanizeSpeechBrainError(error);
            }
            return "Sound classification failed: " + result;
            
        } catch (Exception e) {
            return "Sound classification error: " + e.getMessage();
        }
    }
    
    /**
     * Classify sound from audio ID (placeholder - needs file mapping)
     */
    public String classifySoundById(String audioId) {
        // In production, this would map audioId to actual file path
        // For now, return placeholder
        return "Sound classification from ID not yet implemented (needs file mapping)";
    }

    private Path resolveAudioPath(String audioFile) {
        if (audioFile == null || audioFile.isBlank()) {
            return null;
        }
        return Paths.get(audioFile).toAbsolutePath().normalize();
    }

    private String resolveSpeechBrainScript() {
        List<Path> candidates = List.of(
            Paths.get("speechbrain_interface.py"),
            Paths.get("Asset-Manager", "speechbrain_interface.py"),
            Paths.get("Java-Memory", "Asset-Manager", "speechbrain_interface.py"),
            Paths.get(LEGACY_SPEECHBRAIN_SCRIPT)
        );
        for (Path candidate : candidates) {
            Path normalized = candidate.toAbsolutePath().normalize();
            if (Files.exists(normalized) && Files.isRegularFile(normalized)) {
                return normalized.toString();
            }
        }
        return null;
    }

    private String resolvePythonCommand() {
        String configured = System.getenv("PYTHON_BIN");
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }
        return "python";
    }

    private String extractJsonStringField(String json, String field) {
        if (json == null || field == null) {
            return null;
        }
        String marker = "\"" + field + "\"";
        int fieldStart = json.indexOf(marker);
        if (fieldStart < 0) {
            return null;
        }
        int colon = json.indexOf(":", fieldStart + marker.length());
        if (colon < 0) {
            return null;
        }
        int cursor = colon + 1;
        while (cursor < json.length() && Character.isWhitespace(json.charAt(cursor))) {
            cursor++;
        }
        if (cursor >= json.length() || json.startsWith("null", cursor)) {
            return null;
        }
        if (json.charAt(cursor) != '"') {
            return null;
        }
        cursor++;
        int end = cursor;
        boolean escaped = false;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            }
            end++;
        }
        if (end <= cursor || end >= json.length()) {
            return null;
        }
        String raw = json.substring(cursor, end);
        return raw
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\t", "\t");
    }

    private String extractJsonNumberField(String json, String field) {
        if (json == null || field == null) {
            return null;
        }
        String marker = "\"" + field + "\"";
        int fieldStart = json.indexOf(marker);
        if (fieldStart < 0) {
            return null;
        }
        int colon = json.indexOf(":", fieldStart + marker.length());
        if (colon < 0) {
            return null;
        }
        int cursor = colon + 1;
        while (cursor < json.length() && Character.isWhitespace(json.charAt(cursor))) {
            cursor++;
        }
        int end = cursor;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (Character.isDigit(c) || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E') {
                end++;
            } else {
                break;
            }
        }
        if (end <= cursor) {
            return null;
        }
        return json.substring(cursor, end);
    }

    private String humanizeSpeechBrainError(String error) {
        if (error == null || error.isBlank()) {
            return "unknown speechbrain error";
        }
        if (error.contains("WinError 1314")) {
            return error + " (Windows model-cache permission issue; run once elevated or change cache path).";
        }
        if (error.contains("Error opening")) {
            return error + " (check audio path and supported format).";
        }
        return error;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        long avgTime = audioSegmentsProcessed > 0 ? totalProcessingTimeMs / audioSegmentsProcessed : 0;
        
        return String.format(
            "Audio Engine Statistics:\n" +
            "  Audio segments processed: %d\n" +
            "  Features extracted: %d\n" +
            "  Average processing time: %dms\n",
            audioSegmentsProcessed,
            featuresExtracted,
            avgTime
        );
    }
    
    // =========================================================================================
    // INNER CLASSES
    // =========================================================================================
    
    public static class AudioFeature {
        public final String id;
        public final double value;
        
        public AudioFeature(String id, double value) {
            this.id = id;
            this.value = value;
        }
    }
    
    public static class AudioResult {
        public final boolean success;
        public final String message;
        public final int featureCount;
        public final long elapsedTimeMs;
        
        public AudioResult(boolean success, String message, int featureCount, long elapsedTimeMs) {
            this.success = success;
            this.message = message;
            this.featureCount = featureCount;
            this.elapsedTimeMs = elapsedTimeMs;
        }
    }
}
