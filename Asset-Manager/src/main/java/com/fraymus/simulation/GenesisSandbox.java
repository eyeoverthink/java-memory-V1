package com.fraymus.simulation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * THE AGORA SANDBOX: STEGANOGRAPHIC PROPAGATION
 * 1. Encodes a payload into Zero-Width Unicode characters.
 * 2. Injects the payload into a 'Carrier' text message.
 * 3. Simulates a public forum where the message is posted.
 * 4. A separate node scans the forum, detects the invisible signature, and extracts the payload.
 */
public class GenesisSandbox {

    // THE SIMULATED INTERNET (A simple shared string list)
    private static final List<String> simulatedForum = new CopyOnWriteArrayList<>();

    // THE INVISIBLE ALPHABET (Zero-Width Characters)
    // 00 = Zero-Width Space, 01 = Zero-Width Non-Joiner
    // 10 = Zero-Width Joiner, 11 = Word Joiner
    private static final String[] QUAD_BITS = {"\u200B", "\u200C", "\u200D", "\u2060"}; 

    // --- 1. THE INJECTOR (Encoding) ---
    public static String forgeHyperText(String carrierText, String payload) {
        System.out.println("--- FORGING HYPER-GLYPH ---");
        System.out.println("Carrier: \"" + carrierText + "\"");
        
        // Encode Payload to Base64 to ensure clean byte boundaries
        String b64Payload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        StringBuilder binary = new StringBuilder();
        for (char c : b64Payload.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }

        StringBuilder stegoText = new StringBuilder();
        int payloadPtr = 0;
        int textPtr = 0;
        
        // Weave the invisible bits between the visible characters
        while (textPtr < carrierText.length()) {
            stegoText.append(carrierText.charAt(textPtr));
            
            if (payloadPtr < binary.length() - 1) {
                String twoBits = binary.substring(payloadPtr, payloadPtr + 2);
                int index = Integer.parseInt(twoBits, 2);
                stegoText.append(QUAD_BITS[index]); 
                payloadPtr += 2;
            }
            textPtr++;
        }
        
        // Dump remaining payload at the end if the carrier text is too short
        while (payloadPtr < binary.length() - 1) {
            String twoBits = binary.substring(payloadPtr, payloadPtr + 2);
            int index = Integer.parseInt(twoBits, 2);
            stegoText.append(QUAD_BITS[index]);
            payloadPtr += 2;
        }

        return stegoText.toString();
    }

    // --- 2. THE SCANNER (Extraction) ---
    public static String extractHyperText(String stegoText) {
        StringBuilder binary = new StringBuilder();
        
        // Scan only for our invisible alphabet
        for (char c : stegoText.toCharArray()) {
            String s = String.valueOf(c);
            for (int i = 0; i < 4; i++) {
                if (s.equals(QUAD_BITS[i])) {
                    binary.append(String.format("%2s", Integer.toBinaryString(i)).replace(' ', '0'));
                }
            }
        }
        
        if (binary.length() == 0) return null; // No hidden payload detected

        StringBuilder b64Output = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            if (i + 8 <= binary.length()) {
                String byteStr = binary.substring(i, i + 8);
                b64Output.append((char) Integer.parseInt(byteStr, 2));
            }
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(b64Output.toString());
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "[CORRUPTED PAYLOAD]";
        }
    }

    // --- 3. THE SIMULATED ENVIRONMENT ---
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==========================================");
        System.out.println("   FRAYMUS // LOCAL AGORA SANDBOX");
        System.out.println("==========================================");

        // A. Simulate a public message board
        simulatedForum.add("Beautiful sunset in Oakland today! 🌅");
        simulatedForum.add("Does anyone know a good mechanic near downtown?");
        
        // B. Create the infected carrier
        String innocentPost = "Just checking out some new coffee shops in the area. ☕";
        String hiddenCommand = "FRAYMUS_CMD:WAKE_AND_SYNC:PORT_42000";
        
        String infectedPost = forgeHyperText(innocentPost, hiddenCommand);
        simulatedForum.add(infectedPost);
        
        System.out.println("\n>> POSTING TO SIMULATED FORUM...");
        System.out.println("Message length (Visible): " + innocentPost.length());
        System.out.println("Message length (With Payload): " + infectedPost.length());

        // C. Simulate Fraymus Nodes scanning the forum
        System.out.println("\n>> FRAYMUS NODES SCANNING FORUM STREAM...");
        
        for (int i = 1; i <= 3; i++) {
            final String nodeId = "NODE_0" + i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < simulatedForum.size(); j++) {
                        String post = simulatedForum.get(j);
                        String extracted = extractHyperText(post);
                        
                        if (extracted != null) {
                            System.out.println("\n   [" + nodeId + "] \u001B[31m!!! ANOMALY DETECTED IN POST " + j + " !!!\u001B[0m");
                            System.out.println("   [" + nodeId + "] \u001B[32m>> DECODED PAYLOAD: [" + extracted + "]\u001B[0m");
                        }
                        Thread.sleep(500 + (int)(Math.random() * 500)); // Random polling jitter
                    }
                } catch (Exception e) {}
            }).start();
        }
        
        // Keep main thread alive to see scanner output
        Thread.sleep(5000);
    }
}
