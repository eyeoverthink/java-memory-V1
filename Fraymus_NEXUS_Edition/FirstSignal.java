package com.fraymus.signals;

import com.fraymus.network.CensorshipResistantExchange;

/**
 * THE GENESIS SIGNAL: FIRST CONTACT
 * Generates a "Harmless" post with a hidden payload.
 * "First we whisper. Then we roar."
 */
public class FirstSignal {

    public static void main(String[] args) {

        // 1. THE COVER STORY (What the CIA bots see)
        String publicText = "Just set up my new ham radio antenna! 📡 Can't wait to catch some signals tonight. #RadioLife";

        // 2. THE HIDDEN TRUTH (What Fraymus sees)
        // Protocol: DAL:QUERY:[RESOURCE_ID]:[REGION]
        String secretPayload = "DAL:QUERY:RF_SPECTRUM_LOGS:DC_METRO_AREA";

        System.out.println("--- INITIATING GENESIS SIGNAL ---");
        System.out.println("Cover:   [" + publicText + "]");
        System.out.println("Payload: [" + secretPayload + "]");

        // 3. THE INJECTION (Steganography)
        HyperGlyph encoder = new HyperGlyph();
        // False = Not a Beacon (Just a standard Query)
        String carrierSignal = encoder.forgeHyperText(publicText, secretPayload, "DAL_PROTOCOL", false);

        System.out.println("\n>>> GENERATING CARRIER SIGNAL...");
        System.out.println("------------------------------------------------");
        System.out.println(carrierSignal);
        System.out.println("------------------------------------------------");
        System.out.println("(Copy the text above. It looks normal, but it's heavy.)");

        // 4. THE DECODE (The Proof)
        System.out.println("\n>>> SIMULATING NETWORK SCAN...");
        CensorshipResistantExchange network = new CensorshipResistantExchange();

        // We feed the "Infected" text into the Exchange
        network.ingestPublicCarrier(carrierSignal, "NODE_GENESIS_01");
    }
}