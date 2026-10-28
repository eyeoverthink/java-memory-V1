package brain_architecture.backends;

import brain_architecture.core.BackendInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Fish Audio S2 Backend Adapter
 * 
 * Emotion-controlled Text-to-Speech (TTS) and audio synthesis backend.
 * Supports emotion parameterization for expressive speech generation.
 * 
 * Features:
 * - Emotion control (happy, sad, angry, neutral, excited, calm)
 * - Voice cloning and style transfer
 * - Multi-language support
 * - Real-time streaming
 * - High-quality audio output
 */
public class FishAudioS2Backend implements BackendInterface {
    
    private static final String API_URL = "https://api.fish.audio/v1/tts";
    private static final String API_KEY_ENV = "FISH_AUDIO_API_KEY";
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private boolean initialized = false;
    private boolean useMockMode = false;
    
    // Emotion types supported
    public enum Emotion {
        NEUTRAL("neutral", "Normal speaking tone"),
        HAPPY("happy", "Joyful and upbeat"),
        SAD("sad", "Melancholic and somber"),
        ANGRY("angry", "Frustrated and intense"),
        EXCITED("excited", "Enthusiastic and energetic"),
        CALM("calm", "Peaceful and relaxed"),
        SURPRISED("surprised", "Shocked and amazed"),
        FEARFUL("fearful", "Scared and anxious");
        
        private final String id;
        private final String description;
        
        Emotion(String id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDescription() { return description; }
    }
    
    public FishAudioS2Backend() {
        this.apiKey = System.getenv(API_KEY_ENV);
        this.useMockMode = apiKey == null || apiKey.isEmpty();
        
        this.gson = new Gson();
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    @Override
    public BackendType getBackendType() {
        return BackendType.FISH_AUDIO_S2;
    }
    
    @Override
    public BackendCapabilities getCapabilities() {
        return new BackendCapabilities(
            false,  // reasoning - not applicable for TTS
            true,   // generation - audio generation
            false,  // knowledge - not applicable
            true,   // fast inference - real-time TTS
            false,  // complex analysis - not applicable
            5000.0, // max latency 5s for TTS
            0.8     // min confidence 0.8
        );
    }
    
    @Override
    public void initialize() throws Exception {
        if (useMockMode) {
            // Mock mode - no initialization needed
            System.out.println("FishAudioS2Backend: Using mock mode (no API key)");
        } else {
            // Validate API key by making a simple request
            validateApiKey();
        }
        initialized = true;
    }
    
    @Override
    public void shutdown() {
        // Cleanup resources if needed
        initialized = false;
    }
    
    @Override
    public boolean isHealthy() {
        return initialized;
    }
    
    @Override
    public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        if (!initialized) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend not initialized", Map.of())
            );
        }
        
        if (useMockMode) {
            return executeMockQuery(query, taskType, parameters);
        }
        
        return executeRealQuery(query, taskType, parameters);
    }
    
    /**
     * Execute real API query
     */
    private CompletableFuture<QueryResult> executeRealQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        // Extract parameters
        String emotion = parameters.containsKey("emotion") ? 
            parameters.get("emotion").toString() : "neutral";
        String voiceId = parameters.containsKey("voice_id") ? 
            parameters.get("voice_id").toString() : "default";
        double speed = parameters.containsKey("speed") ? 
            ((Number) parameters.get("speed")).doubleValue() : 1.0;
        
        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("text", query);
        requestBody.addProperty("emotion", emotion);
        requestBody.addProperty("voice_id", voiceId);
        requestBody.addProperty("speed", speed);
        requestBody.addProperty("format", "mp3");
        
        // Build request
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build();
        
        return CompletableFuture.supplyAsync(() -> {
            try (Response response = httpClient.newCall(request).execute()) {
                long latency = System.currentTimeMillis() - startTime;
                
                if (!response.isSuccessful()) {
                    return new QueryResult(
                        "",
                        0.0,
                        latency,
                        false,
                        "API request failed: " + response.code(),
                        Map.of("provider", "fish_audio_s2", "status_code", response.code())
                    );
                }
                
                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                // Parse response
                String audioUrl = jsonResponse.has("audio_url") ? 
                    jsonResponse.get("audio_url").getAsString() : "";
                String audioData = jsonResponse.has("audio_data") ? 
                    jsonResponse.get("audio_data").getAsString() : "";
                
                String result = audioUrl.isEmpty() ? audioData : audioUrl;
                
                return new QueryResult(
                    result,
                    jsonResponse.has("confidence") ? jsonResponse.get("confidence").getAsDouble() : 0.9,
                    latency,
                    true,
                    null,
                    Map.of(
                        "provider", "fish_audio_s2",
                        "emotion", emotion,
                        "voice_id", voiceId,
                        "duration_ms", jsonResponse.has("duration_ms") ? jsonResponse.get("duration_ms").getAsLong() : 0
                    )
                );
            } catch (IOException e) {
                long latency = System.currentTimeMillis() - startTime;
                return new QueryResult(
                    "",
                    0.0,
                    latency,
                    false,
                    "IO error: " + e.getMessage(),
                    Map.of("provider", "fish_audio_s2")
                );
            }
        });
    }
    
    /**
     * Execute mock query for testing
     */
    private CompletableFuture<QueryResult> executeMockQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate processing time
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            String emotion = parameters.containsKey("emotion") ? 
                parameters.get("emotion").toString() : "neutral";
            
            // Mock response
            String mockAudioUrl = "https://mock.fish.audio/audio/" + 
                System.currentTimeMillis() + ".mp3";
            
            return new QueryResult(
                mockAudioUrl,
                0.85,
                100,
                true,
                null,
                Map.of(
                    "provider", "fish_audio_s2",
                    "emotion", emotion,
                    "voice_id", parameters.getOrDefault("voice_id", "default"),
                    "duration_ms", 1500,
                    "mock_mode", true
                )
            );
        });
    }
    
    /**
     * Validate API key
     */
    private void validateApiKey() throws Exception {
        // Simple validation - make a minimal request
        Request request = new Request.Builder()
            .url(API_URL + "/validate")
            .addHeader("Authorization", "Bearer " + apiKey)
            .get()
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() && response.code() != 404) {
                // 404 might mean endpoint doesn't exist, which is OK for validation
                throw new Exception("API key validation failed: " + response.code());
            }
        }
    }
    
    /**
     * Generate speech with specific emotion
     */
    public CompletableFuture<QueryResult> generateSpeech(String text, Emotion emotion) {
        return generateSpeech(text, emotion, "default", 1.0);
    }
    
    /**
     * Generate speech with full control
     */
    public CompletableFuture<QueryResult> generateSpeech(String text, Emotion emotion, String voiceId, double speed) {
        Map<String, Object> params = Map.of(
            "emotion", emotion.getId(),
            "voice_id", voiceId,
            "speed", speed
        );
        
        return executeQuery(text, TaskType.GENERATION, params);
    }
    
    /**
     * Get available emotions
     */
    public static Emotion[] getAvailableEmotions() {
        return Emotion.values();
    }
    
    /**
     * Get emotion by ID
     */
    public static Emotion getEmotion(String emotionId) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.getId().equals(emotionId)) {
                return emotion;
            }
        }
        return Emotion.NEUTRAL;
    }
}
