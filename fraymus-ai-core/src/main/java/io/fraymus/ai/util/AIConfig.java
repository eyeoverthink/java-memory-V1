package io.fraymus.ai.util;

/**
 * AI Configuration
 */
public class AIConfig {
    public String chatModel = "llama3";
    public String embedModel = "embeddinggemma";
    public String vectorStorePath = "vault";
    public String memoryPath = "memory";
    public int ragTopK = 6;
    public int ragMaxChars = 8000;
    public int chunkSize = 1200;
    public int chunkOverlap = 200;
    public int maxSessionMessages = 20;
    public boolean verboseLogging = false;
}
