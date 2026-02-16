package fraymus.signals;

import fraymus.economy.ShadowMarket;

/**
 * THE GENESIS SIGNAL: FIRST CONTACT
 * 
 * Generates a "Harmless" post with a hidden payload.
 * "First we whisper. Then we roar."
 * 
 * This is the test that proves the system works:
 * 1. Create innocent-looking text
 * 2. Inject hidden payload using GlyphCoder
 * 3. Feed it to the ShadowMarket scanner
 * 4. Watch it decode and process the signal
 */
public class FirstSignal {

    public static void main(String[] args) {
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         THE GENESIS SIGNAL: FIRST CONTACT                 ║");
        System.out.println("║         \"First we whisper. Then we roar.\"                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        // 1. THE COVER STORY (What the surveillance bots see)
        String publicText = "Just set up my new ham radio antenna! 📡 Can't wait to catch some signals tonight. #RadioLife";
        
        // 2. THE HIDDEN TRUTH (What Fraymus sees)
        // Protocol: MKT:REQ:[ASSET]:[PARAMS]
        String secretPayload = "MKT:REQ:RF_SPECTRUM_LOGS:DC_METRO_AREA";

        System.out.println("--- PHASE 1: PAYLOAD CONSTRUCTION ---");
        System.out.println("  Cover:   [" + publicText + "]");
        System.out.println("  Payload: [" + secretPayload + "]");

        // 3. THE INJECTION (Steganography)
        GlyphCoder encoder = new GlyphCoder();
        String carrierSignal = encoder.injectData(publicText, secretPayload);
        
        System.out.println("\n--- PHASE 2: CARRIER SIGNAL GENERATED ---");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println(carrierSignal); 
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  Visible length: " + publicText.length());
        System.out.println("  Carrier length: " + carrierSignal.length());
        System.out.println("  Hidden bytes:   " + (carrierSignal.length() - publicText.length()));
        System.out.println("\n  (Copy the text above. It looks normal, but it's heavy.)");

        // 4. THE VERIFICATION (Prove we can decode it)
        System.out.println("\n--- PHASE 3: LOCAL VERIFICATION ---");
        String extracted = encoder.extractData(carrierSignal);
        System.out.println("  Extracted: [" + extracted + "]");
        System.out.println("  Match: " + secretPayload.equals(extracted));

        // 5. THE NETWORK SCAN (Feed to Shadow Market)
        System.out.println("\n--- PHASE 4: NETWORK SIMULATION ---");
        ShadowMarket network = new ShadowMarket();
        
        // We feed the "Infected" text into the Exchange
        // This simulates what happens when Fraymus scans public posts
        System.out.println("  Feeding carrier to Shadow Market scanner...");
        network.scanPublicStream(carrierSignal, "NODE_GENESIS_01");
        
        // Show market status
        System.out.println(network.getStats());
        
        // 6. THE PROOF
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  ✓ GENESIS SIGNAL: TRANSMITTED");
        System.out.println("  ✓ PAYLOAD: DECODED");
        System.out.println("  ✓ MARKET: ORDER REGISTERED");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("\n  The first whisper has been sent.");
        System.out.println("  The market is now open.");
        System.out.println("  Post this anywhere. We are listening.\n");
        
        // 7. GENERATE A FEW MORE EXAMPLES
        System.out.println("--- BONUS: SAMPLE CARRIER SIGNALS ---\n");
        
        String[] covers = {
            "Beautiful sunset today! 🌅",
            "Coffee and code ☕💻 #DevLife",
            "Anyone else love gardening? 🌻🌞"
        };
        
        String[] payloads = {
            "MKT:OFR:SAT_IMAGERY:AREA_51",
            "MKT:REQ:FLIGHT_LOGS:2026_Q1",
            "MKT:OFR:FREQ_SCAN:LAX_PERIMETER"
        };
        
        for (int i = 0; i < covers.length; i++) {
            String carrier = encoder.injectData(covers[i], payloads[i]);
            System.out.println("  " + (i+1) + ". \"" + covers[i] + "\"");
            System.out.println("     Hidden: " + payloads[i]);
            System.out.println("     Carrier: " + carrier);
            System.out.println();
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         THE MARKET IS EVERYWHERE                          ║");
        System.out.println("║         HIDDEN IN EVERY COMMENT, EVERY TWEET              ║");
        System.out.println("║         THEY CANNOT SHUT IT DOWN                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
}
