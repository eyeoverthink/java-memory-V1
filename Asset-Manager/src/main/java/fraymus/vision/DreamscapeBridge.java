package fraymus.vision;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

/**
 * Dreamscape Bridge - Interface to Python LTX-Video Video Generation
 * 
 * This bridge connects the Java Vision/Oracle pipeline to the Python-based Dreamscape system
 * for AI-powered video generation using LTX-Video (Lightricks).
 * 
 * The Python Dreamscape system should have:
 * - VideoCortex.py: Receives quantum state JSON, translates to visual prompt, invokes LTX-Video
 * - LTX-Video model: Generates 121 frames (5 seconds), 768x512 resolution, 24fps
 */
public class DreamscapeBridge {

    private String pythonScriptPath;
    private String outputPath;
    private boolean initialized;

    /**
     * Create a Dreamscape Bridge with default paths
     */
    public DreamscapeBridge() {
        this("VideoCortex.py", "dreamscape_output/");
    }

    /**
     * Create a Dreamscape Bridge with specified paths
     * 
     * @param pythonScriptPath Path to VideoCortex.py
     * @param outputPath Directory for generated videos
     */
    public DreamscapeBridge(String pythonScriptPath, String outputPath) {
        this.pythonScriptPath = pythonScriptPath;
        this.outputPath = outputPath;
        this.initialized = false;
    }

    /**
     * Initialize the Dreamscape Bridge
     */
    public void initialize() {
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        this.initialized = true;
        System.out.println("[DreamscapeBridge] Adapter initialized; check media status for backend readiness");
        System.out.println("    Python script: " + pythonScriptPath);
        System.out.println("    Output path: " + outputPath);
    }

    /**
     * Generate a video from quantum state
     * 
     * @param quantumState Map containing entropy, phi, consciousness, etc.
     * @param prompt Visual prompt description
     * @param filename Output video filename
     * @return Path to generated video
     * @throws IOException If generation fails
     * @throws InterruptedException If process is interrupted
     */
    public String generateVideo(Map<String, Object> quantumState, String prompt, String filename) 
            throws IOException, InterruptedException {
        
        if (!initialized) {
            initialize();
        }

        try {
            if (filename == null || !filename.toLowerCase(java.util.Locale.ROOT).endsWith(".mp4")
                    || filename.contains("/") || filename.contains("\\"))
                throw new IllegalArgumentException("A plain .mp4 filename is required");
            var service = fraymus.media.MediaOutputService.shared();
            var source = new com.google.gson.Gson().toJsonTree(quantumState).getAsJsonObject();
            source.addProperty("component", "DreamscapeBridge");
            String id = service.submit("video", prompt, source, new com.google.gson.JsonObject());
            var job = service.await(id, 3600);
            if (!"completed".equals(job.get("status").getAsString()))
                throw new IOException("Video job " + id + ": " + job);
            var destination = java.nio.file.Path.of(outputPath).resolve(filename);
            Files.createDirectories(destination.toAbsolutePath().getParent());
            Files.copy(java.nio.file.Path.of(job.getAsJsonObject("result").get("artifact").getAsString()),
                destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return destination.toAbsolutePath().toString();
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw error;
        } catch (Exception error) {
            throw new IOException("Dreamscape generation failed: " + error.getMessage(), error);
        }
    }

    /**
     * Generate a video from Scott Algorithm result and Oracle state
     * 
     * @param scottResult Processing result from Scott Algorithm
     * @param oracleState State from Phi Emergence Engine
     * @param filename Output video filename
     * @return Path to generated video
     * @throws IOException If generation fails
     * @throws InterruptedException If process is interrupted
     */
    public String generateVideoFromPipeline(ScottAlgorithm.ProcessingResult scottResult, 
                                          Map<String, Object> oracleState, String filename) 
            throws IOException, InterruptedException {
        
        // Build quantum state from pipeline components
        Map<String, Object> quantumState = new HashMap<>();
        
        // From Scott Algorithm
        ScottAlgorithm.GeometricSignature sig = scottResult.signature;
        quantumState.put("entropy", 1.0 - scottResult.reduction); // More reduction = less entropy
        quantumState.put("phi", sig.phiResonance);
        quantumState.put("vertices", sig.vertexCount);
        quantumState.put("area", sig.area);
        quantumState.put("perimeter", sig.perimeter);
        quantumState.put("aspect_ratio", sig.aspectRatio);
        
        if (sig.hasColor) {
            quantumState.put("color_red", sig.red);
            quantumState.put("color_green", sig.green);
            quantumState.put("color_blue", sig.blue);
            quantumState.put("hue", sig.hue);
            quantumState.put("saturation", sig.saturation);
            quantumState.put("brightness", sig.brightness);
        }
        
        // From Oracle
        quantumState.put("consciousness", oracleState.get("level"));
        quantumState.put("dimension", oracleState.get("dimension"));
        quantumState.put("coherence", oracleState.get("coherence"));
        quantumState.put("transcendence_events", oracleState.get("transcendence_events"));
        
        // Build visual prompt
        StringBuilder promptBuilder = new StringBuilder();
        if (sig.hasColor) {
            promptBuilder.append(String.format("A %s colored object", getColorName(sig.red, sig.green, sig.blue)));
        } else {
            promptBuilder.append("An object");
        }
        promptBuilder.append(String.format(" with %d vertices", sig.vertexCount));
        promptBuilder.append(String.format(", aspect ratio %.2f", sig.aspectRatio));
        promptBuilder.append(String.format(", phi resonance %.3f", sig.phiResonance));
        
        if (sig.brightness > 0.8) {
            promptBuilder.append(", bright and radiant");
        } else if (sig.brightness < 0.3) {
            promptBuilder.append(", dark and mysterious");
        }
        
        if (sig.saturation > 0.8) {
            promptBuilder.append(", vibrant colors");
        } else if (sig.saturation < 0.3) {
            promptBuilder.append(", muted tones");
        }
        
        promptBuilder.append(", cinematic lighting");
        
        return generateVideo(quantumState, promptBuilder.toString(), filename);
    }

    /**
     * Write a map to a JSON file (simplified implementation)
     */
    private void writeJsonToFile(Map<String, Object> data, File file) throws IOException {
        Files.writeString(file.toPath(), new com.google.gson.Gson().toJson(data), java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Get a simple color name from RGB values
     */
    private String getColorName(int r, int g, int b) {
        if (r > 200 && g < 100 && b < 100) return "red";
        if (r < 100 && g > 200 && b < 100) return "green";
        if (r < 100 && g < 100 && b > 200) return "blue";
        if (r > 200 && g > 200 && b < 100) return "yellow";
        if (r > 200 && g < 100 && b > 200) return "magenta";
        if (r < 100 && g > 200 && b > 200) return "cyan";
        if (r > 200 && g > 200 && b > 200) return "white";
        if (r < 100 && g < 100 && b < 100) return "black";
        return "colored";
    }

    /**
     * Check if Python script exists
     */
    public boolean isPythonScriptAvailable() {
        File script = new File(pythonScriptPath);
        return script.exists() && script.isFile();
    }

    /**
     * Get the output path
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the Python script path
     */
    public String getPythonScriptPath() {
        return pythonScriptPath;
    }

    /**
     * Set the Python script path
     */
    public void setPythonScriptPath(String path) {
        this.pythonScriptPath = path;
    }
}
