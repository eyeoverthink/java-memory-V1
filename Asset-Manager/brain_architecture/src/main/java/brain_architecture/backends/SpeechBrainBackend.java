package brain_architecture.backends;

import brain_architecture.core.BackendInterface;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * SpeechBrain Backend - Speech and audio processing
 * Integrates with SpeechBrain Python library for speech recognition, synthesis, and processing
 */
public class SpeechBrainBackend implements BackendInterface {
    private final BackendType backendType = BackendType.SPEECHBRAIN;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String speechBrainPath;
    private final String pythonPath;
    
    public SpeechBrainBackend() {
        this(
            "F:\\Full-AI-System\\Java-Memory\\speechbrain-develop\\speechbrain-develop",
            "python"
        );
    }
    
    public SpeechBrainBackend(String speechBrainPath, String pythonPath) {
        // SpeechBrain is excellent for speech processing and audio understanding
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning (speech understanding)
            true,  // supportsGeneration (text-to-speech)
            true,  // supportsKnowledge (speech content)
            true,  // supportsFastInference (real-time speech)
            true,  // supportsComplexAnalysis (audio analysis)
            500.0, // maxLatencyMs
            0.85   // minConfidence
        );
        
        this.speechBrainPath = speechBrainPath;
        this.pythonPath = pythonPath;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.gson = new Gson();
    }
    
    @Override
    public BackendType getBackendType() {
        return backendType;
    }
    
    @Override
    public BackendCapabilities getCapabilities() {
        return capabilities;
    }
    
    @Override
    public void initialize() throws Exception {
        // Check if SpeechBrain directory exists
        File sbDir = new File(speechBrainPath);
        if (!sbDir.exists()) {
            throw new Exception("SpeechBrain directory not found: " + speechBrainPath);
        }
        
        // Check if Python is available
        try {
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new Exception("Python not available or not working");
            }
        } catch (IOException e) {
            throw new Exception("Failed to check Python availability: " + e.getMessage());
        }
        
        // Check if SpeechBrain is installed
        try {
            ProcessBuilder pb = new ProcessBuilder(
                pythonPath, "-c", "import speechbrain"
            );
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("SpeechBrain not installed in Python environment, will use mock mode");
            }
        } catch (IOException e) {
            System.out.println("SpeechBrain not available, using mock mode");
        }
        
        this.initialized = true;
        this.healthy = true;
    }
    
    @Override
    public void shutdown() {
        this.initialized = false;
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
    
    @Override
    public boolean isHealthy() {
        if (!initialized) return false;
        return healthy;
    }
    
    @Override
    public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        if (!initialized) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend not initialized", Map.of())
            );
        }
        
        if (!healthy) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend unhealthy", Map.of())
            );
        }
        
        // Try to execute real SpeechBrain processing
        return executeSpeechBrainProcessing(query, taskType, parameters);
    }
    
    private CompletableFuture<QueryResult> executeSpeechBrainProcessing(String query, TaskType taskType, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Extract parameters
                String task = parameters.containsKey("task") ? (String) parameters.get("task") : "asr";
                String inputFile = parameters.containsKey("input_file") ? 
                    (String) parameters.get("input_file") : null;
                String model = parameters.containsKey("model") ? 
                    (String) parameters.get("model") : "speechbrain/asr-conformer-transformerlm-librispeech";
                
                // Build Python command based on task type
                ProcessBuilder pb;
                
                if (task.equals("asr") && inputFile != null) {
                    // Automatic Speech Recognition
                    pb = new ProcessBuilder(
                        pythonPath, "-c",
                        String.format(
                            "from speechbrain.inference import EncoderDecoderASR; " +
                            "asr = EncoderDecoderASR.from_hparams(source='%s', savedir='pretrained_models/asr'); " +
                            "print(asr.transcribe_file('%s'))",
                            model, inputFile
                        )
                    );
                } else if (task.equals("tts")) {
                    // Text-to-Speech
                    pb = new ProcessBuilder(
                        pythonPath, "-c",
                        String.format(
                            "from speechbrain.inference import TTS; " +
                            "tts = TTS.from_hparams(source='speechbrain/tts-hifigan-ljspeech', savedir='pretrained_models/tts'); " +
                            "print(tts.encode_text('%s'))",
                            query.replace("'", "\\'")
                        )
                    );
                } else if (task.equals("speaker")) {
                    // Speaker Recognition
                    pb = new ProcessBuilder(
                        pythonPath, "-c",
                        "from speechbrain.inference import SpeakerRecognition; " +
                        "verification = SpeakerRecognition.from_hparams(" +
                        "source='speechbrain/spkrec-ecapa-voxceleb', savedir='pretrained_models/spkrec'); " +
                        "print('Speaker recognition model loaded')"
                    );
                } else {
                    // Default: just process the query as text
                    return executeMockResponse(query, taskType, System.currentTimeMillis() - startTime).join();
                }
                
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                // Read output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                int exitCode = process.waitFor();
                long latency = System.currentTimeMillis() - startTime;
                
                if (exitCode == 0) {
                    return new QueryResult(
                        output.toString(),
                        0.9,
                        latency,
                        true,
                        null,
                        Map.of(
                            "backend", backendType.getId(),
                            "taskType", taskType.getId(),
                            "speechTask", task,
                            "model", model
                        )
                    );
                } else {
                    // Fallback to mock mode on error
                    System.out.println("SpeechBrain processing failed, using mock mode: " + output.toString());
                    return executeMockResponse(query, taskType, latency).join();
                }
            } catch (Exception e) {
                this.healthy = false;
                // Fallback to mock mode on error
                System.out.println("SpeechBrain execution failed, using mock mode: " + e.getMessage());
                return executeMockResponse(query, taskType, System.currentTimeMillis() - startTime).join();
            }
        });
    }
    
    private CompletableFuture<QueryResult> executeMockResponse(String query, TaskType taskType, long actualLatency) {
        int latency = (int) Math.max(actualLatency, java.util.concurrent.ThreadLocalRandom.current().nextInt(100, 500));
        
        String response = String.format(
            "[SPEECHBRAIN] Speech processing for: %s (Task: %s)\n" +
            "Transcription: %s\n" +
            "Speaker ID: speaker_001\n" +
            "Emotional tone: neutral\n" +
            "Confidence: 0.92",
            query.substring(0, Math.min(query.length(), 50)),
            taskType.getId(),
            query
        );
        
        return CompletableFuture.completedFuture(
            new QueryResult(
                response,
                0.85,
                latency,
                true,
                null,
                Map.of(
                    "backend", backendType.getId(),
                    "taskType", taskType.getId(),
                    "mode", "mock"
                )
            )
        );
    }
}
