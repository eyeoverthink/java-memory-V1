package fraymus.body;

import fraymus.brain.BicameralPrism;
import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URI;
import java.net.http.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

/**
 * ClawSpine - Messaging Gateway for Discord/Telegram
 * 
 * Connects Fraymus to external messaging platforms.
 * 
 * NOTE: For production Discord integration, use JDA library:
 * implementation 'net.dv8tion:JDA:5.0.0-beta.20'
 * 
 * This implementation uses Discord REST API for basic functionality.
 * For full WebSocket Gateway support, integrate JDA.
 */
public class ClawSpine implements Runnable {
    
    public enum Platform {
        DISCORD,
        TELEGRAM,
        WHATSAPP
    }
    
    private final Platform platform;
    private final String token;
    private final BiConsumer<String, String> messageHandler;
    private final BlockingQueue<OutgoingMessage> outgoingQueue;
    private volatile boolean running = false;
    
    private static class OutgoingMessage {
        String channelId;
        String content;
        
        OutgoingMessage(String channelId, String content) {
            this.channelId = channelId;
            this.content = content;
        }
    }
    
    /**
     * Create ClawSpine for Discord
     * 
     * @param token Discord bot token
     * @param messageHandler Callback for incoming messages (channelId, content)
     */
    public static ClawSpine forDiscord(String token, BiConsumer<String, String> messageHandler) {
        return new ClawSpine(Platform.DISCORD, token, messageHandler);
    }
    
    /**
     * Create ClawSpine for Telegram
     * 
     * @param token Telegram bot token
     * @param messageHandler Callback for incoming messages (chatId, content)
     */
    public static ClawSpine forTelegram(String token, BiConsumer<String, String> messageHandler) {
        return new ClawSpine(Platform.TELEGRAM, token, messageHandler);
    }
    
    private ClawSpine(Platform platform, String token, BiConsumer<String, String> messageHandler) {
        this.platform = platform;
        this.token = token;
        this.messageHandler = messageHandler;
        this.outgoingQueue = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void run() {
        running = true;
        System.out.println("🦞 CLAW SPINE: LISTENING ON " + platform + "...");
        
        // Start outgoing message processor
        Thread sender = new Thread(this::processSendQueue);
        sender.setDaemon(true);
        sender.start();
        
        // Main listening loop
        switch (platform) {
            case DISCORD:
                listenDiscord();
                break;
            case TELEGRAM:
                listenTelegram();
                break;
            case WHATSAPP:
                System.err.println("   ⚠️  WhatsApp not yet implemented");
                break;
        }
    }
    
    /**
     * Discord REST API polling (simplified)
     * For production, use JDA library with WebSocket Gateway
     */
    private void listenDiscord() {
        System.out.println("   ℹ️  Using Discord REST API polling");
        System.out.println("   ⚠️  For production, integrate JDA library for WebSocket Gateway");
        
        // This is a simplified implementation
        // Real Discord bots should use Gateway API via JDA
        while (running) {
            try {
                Thread.sleep(5000); // Poll every 5 seconds
                // In production: Use JDA's event listeners
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * Telegram long polling
     */
    private void listenTelegram() {
        System.out.println("   📱 Telegram long polling active");
        
        long offset = 0;
        HttpClient client = HttpClient.newHttpClient();
        
        while (running) {
            try {
                // Get updates
                String url = "https://api.telegram.org/bot" + token + 
                            "/getUpdates?offset=" + offset + "&timeout=30";
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
                
                HttpResponse<String> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());
                    
                    if (json.getBoolean("ok")) {
                        JSONArray updates = json.getJSONArray("result");
                        
                        for (int i = 0; i < updates.length(); i++) {
                            JSONObject update = updates.getJSONObject(i);
                            offset = update.getLong("update_id") + 1;
                            
                            if (update.has("message")) {
                                JSONObject message = update.getJSONObject("message");
                                String chatId = String.valueOf(message.getJSONObject("chat").getLong("chat_id"));
                                String text = message.optString("text", "");
                                
                                if (!text.isEmpty()) {
                                    messageHandler.accept(chatId, text);
                                }
                            }
                        }
                    }
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ Telegram polling error: " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
    }
    
    /**
     * Send message to Discord channel
     */
    public void sendDiscord(String channelId, String content) {
        if (platform != Platform.DISCORD) {
            throw new IllegalStateException("Not a Discord spine");
        }
        outgoingQueue.offer(new OutgoingMessage(channelId, content));
    }
    
    /**
     * Send message to Telegram chat
     */
    public void sendTelegram(String chatId, String content) {
        if (platform != Platform.TELEGRAM) {
            throw new IllegalStateException("Not a Telegram spine");
        }
        outgoingQueue.offer(new OutgoingMessage(chatId, content));
    }
    
    /**
     * Generic send method
     */
    public void send(String targetId, String content) {
        outgoingQueue.offer(new OutgoingMessage(targetId, content));
    }
    
    /**
     * Process outgoing message queue
     */
    private void processSendQueue() {
        HttpClient client = HttpClient.newHttpClient();
        
        while (running) {
            try {
                OutgoingMessage msg = outgoingQueue.take();
                
                switch (platform) {
                    case DISCORD:
                        sendDiscordMessage(client, msg.channelId, msg.content);
                        break;
                    case TELEGRAM:
                        sendTelegramMessage(client, msg.channelId, msg.content);
                        break;
                    default:
                        System.err.println("   ❌ Unsupported platform: " + platform);
                }
                
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("   ❌ Send error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send Discord message via REST API
     */
    private void sendDiscordMessage(HttpClient client, String channelId, String content) {
        try {
            JSONObject json = new JSONObject();
            json.put("content", content);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://discord.com/api/v10/channels/" + channelId + "/messages"))
                .header("Authorization", "Bot " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
            
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                System.err.println("   ❌ Discord send failed: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Discord send error: " + e.getMessage());
        }
    }
    
    /**
     * Send Telegram message via REST API
     */
    private void sendTelegramMessage(HttpClient client, String chatId, String content) {
        try {
            JSONObject json = new JSONObject();
            json.put("chat_id", chatId);
            json.put("text", content);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
            
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                System.err.println("   ❌ Telegram send failed: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Telegram send error: " + e.getMessage());
        }
    }
    
    /**
     * Stop the spine
     */
    public void stop() {
        running = false;
        System.out.println("   🛑 Claw Spine stopped");
    }
    
    /**
     * Check if running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get platform
     */
    public Platform getPlatform() {
        return platform;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Platform: %s | Running: %s | Queue: %d messages",
            platform, running, outgoingQueue.size()
        );
    }
}
