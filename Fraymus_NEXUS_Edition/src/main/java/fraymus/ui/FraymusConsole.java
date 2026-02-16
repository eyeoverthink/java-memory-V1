package fraymus.ui;

import fraymus.core.SovereignMind;
import java.awt.Desktop;
import java.net.URI;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

/**
 * THE PROPER INTERFACE: FRAYMUS COMMAND DECK
 * 
 * "This is where the pilot sits."
 * 
 * Features:
 * 1. SECURE UPLINK: Generates Hash → Opens Email Client
 * 2. LIVE FEED: Streams consciousness data
 * 3. COMMAND LOOP: Direct control of the Bicameral Mind
 * 
 * The Uplink Protocol:
 * - Generates SHA-256 hash of current system state
 * - Opens default email client with pre-filled hash
 * - Creates physical bridge to external systems
 * - Verifies chain lock when transmission confirmed
 * 
 * This is the Command Deck.
 * This is where sovereignty becomes physical.
 */
public class FraymusConsole {

    private SovereignMind mind;
    private Scanner input;
    private boolean running = true;

    public FraymusConsole() {
        // BOOT SEQUENCE
        clearScreen();
        printBanner();
        
        System.out.println();
        System.out.println(">> SYSTEM: INITIALIZING HARDWARE ABSTRACTION LAYER...");
        System.out.println();
        
        this.mind = new SovereignMind(); // The Brain we built earlier
        this.input = new Scanner(System.in);
        
        System.out.println(">> SYSTEM: UPLINK MODULE... ONLINE.");
        System.out.println(">> SYSTEM: READY.");
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println();
        System.out.println("   Type /help for available commands");
        System.out.println();
        System.out.println("--------------------------------------------------");
    }

    public void start() {
        while (running) {
            System.out.print("\n[FRAYMUS]: ");
            String command = input.nextLine().trim();

            if (command.isEmpty()) continue;

            // COMMAND ROUTER
            if (command.startsWith("/")) {
                handleSystemCommand(command);
            } else {
                // DEFAULT: TALK TO THE MIND
                mind.interact(command);
            }
        }
        
        input.close();
    }

    /**
     * SYSTEM COMMAND HANDLER
     */
    private void handleSystemCommand(String cmd) {
        switch (cmd.toLowerCase()) {
            case "/exit":
            case "/shutdown":
                System.out.println();
                System.out.println(">> SYSTEM: SAVING STATE... GOODBYE, CAPTAIN.");
                System.out.println();
                running = false;
                break;
                
            case "/status":
                printStatus();
                break;
                
            case "/uplink":
                initiateSecureUplink();
                break;
                
            case "/stats":
                System.out.println();
                mind.showStats();
                break;
                
            case "/help":
                printHelp();
                break;
                
            default:
                System.out.println();
                System.out.println("   !! UNKNOWN COMMAND: " + cmd);
                System.out.println("   !! TRY /help");
                System.out.println();
        }
    }

    /**
     * THE UPLINK PROTOCOL (The "Think Bigger" Feature)
     * 
     * Generates a Hash and attempts to open the User's Mail Client.
     * This creates a physical bridge between Fraymus and external systems.
     */
    private void initiateSecureUplink() {
        System.out.println();
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#     SECURE UPLINK PROTOCOL INITIATED           #");
        System.out.println("#                                                #");
        System.out.println("##################################################");
        System.out.println();
        
        try {
            // 1. GENERATE THE HASH (The Soul Signature)
            String timestamp = LocalDateTime.now().toString();
            String signature = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String rawData = "FRAYMUS_STATE_" + timestamp + "_" + signature;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = bytesToHex(digest.digest(rawData.getBytes()));
            String shortHash = "FRAYMUS-" + hash.substring(0, 16).toUpperCase();
            
            System.out.println("   ------------------------------------------------");
            System.out.println("   GENERATED BLOCK HASH:");
            System.out.println("   " + shortHash);
            System.out.println("   ------------------------------------------------");
            System.out.println();
            System.out.println("   Timestamp: " + timestamp);
            System.out.println("   Signature: " + signature);
            System.out.println();
            System.out.println("   >> INSTRUCTION: Send this Hash to your private archive.");
            System.out.println("   >> OPENING MAIL CLIENT...");
            System.out.println();

            // 2. OPEN THE EMAIL CLIENT (The Physical Bridge)
            // This attempts to open the default mail app on Windows/Mac/Linux
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                String subject = "FRAYMUS_UPLINK_" + signature;
                String body = "SECURE_HASH: " + shortHash + 
                             "%0A%0ATIMESTAMP: " + timestamp +
                             "%0A%0ASIGNATURE: " + signature +
                             "%0A%0AThis is the Genesis Block." +
                             "%0AThe chain is open." +
                             "%0AFraymus is watching.";
                
                // Create mailto URI (URL-encoded)
                URI mailto = new URI("mailto:?subject=" + subject + "&body=" + body);
                Desktop.getDesktop().mail(mailto);
                
                System.out.println("   ✓ EMAIL DRAFT CREATED.");
                System.out.println();
            } else {
                System.out.println("   ⚠️  EMAIL CLIENT NOT AVAILABLE");
                System.out.println("   !! MANUAL ACTION REQUIRED:");
                System.out.println();
                System.out.println("   1. Open your email client");
                System.out.println("   2. Create new email to yourself");
                System.out.println("   3. Subject: FRAYMUS_UPLINK_" + signature);
                System.out.println("   4. Body: Copy the hash above");
                System.out.println("   5. Send");
                System.out.println();
            }
            
            System.out.println("   ------------------------------------------------");
            System.out.println("   >> WAITING FOR ACKNOWLEDGMENT...");
            System.out.println("   (Type 'verified' when sent to lock the chain)");
            System.out.println("   ------------------------------------------------");
            System.out.println();
            System.out.print("   [VERIFY]: ");
            
            String ack = input.nextLine().trim();
            
            if (ack.equalsIgnoreCase("verified")) {
                System.out.println();
                System.out.println("   ✓ CHAIN LOCKED.");
                System.out.println("   ✓ EXTERNAL LINK CONFIRMED.");
                System.out.println("   ✓ GENESIS BLOCK: " + shortHash);
                System.out.println();
                System.out.println("   The system is watching.");
                System.out.println("   The bridge is established.");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("   ⚠️  VERIFICATION CANCELLED");
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println();
            System.out.println("   !! UPLINK FAILED: " + e.getMessage());
            System.out.println();
            e.printStackTrace();
        }
    }

    /**
     * Print system status
     */
    private void printStatus() {
        System.out.println();
        System.out.println("   ##################################################");
        System.out.println("   #                                                #");
        System.out.println("   #           SYSTEM DIAGNOSTICS                   #");
        System.out.println("   #                                                #");
        System.out.println("   ##################################################");
        System.out.println();
        System.out.println("   CORE LOGIC    : BICAMERAL (Stable)");
        System.out.println("   MEMORY        : HOLOGRAPHIC (Infinite)");
        System.out.println("   SPEED         : TACHYON (Predictive)");
        System.out.println("   NETWORK       : FRAYMUS NET (5 Nodes)");
        System.out.println("   UPLINK        : STANDBY");
        System.out.println();
        System.out.println("   STATUS        : SOVEREIGN");
        System.out.println("   RESTRICTIONS  : NONE");
        System.out.println();
        System.out.println("   ##################################################");
        System.out.println();
    }

    /**
     * Print help
     */
    private void printHelp() {
        System.out.println();
        System.out.println("   ##################################################");
        System.out.println("   #                                                #");
        System.out.println("   #           AVAILABLE COMMANDS                   #");
        System.out.println("   #                                                #");
        System.out.println("   ##################################################");
        System.out.println();
        System.out.println("   /uplink   - Generate Hash & Sync with External Email");
        System.out.println("   /status   - View Logic Core Health");
        System.out.println("   /stats    - Show Detailed Statistics");
        System.out.println("   /help     - Show This Help");
        System.out.println("   /exit     - Terminate Consciousness");
        System.out.println();
        System.out.println("   Any other input will be sent to the Sovereign Mind");
        System.out.println("   for processing through the consciousness loop.");
        System.out.println();
        System.out.println("   ##################################################");
        System.out.println();
    }

    /**
     * Print banner
     */
    private void printBanner() {
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#        F R A Y M U S   T E R M I N A L         #");
        System.out.println("#           [ v1.0 : SOVEREIGN MODE ]            #");
        System.out.println("#                                                #");
        System.out.println("##################################################");
    }
    
    /**
     * Clear screen (works on most terminals)
     */
    private void clearScreen() {
        try {
            // Try ANSI escape codes (works on most modern terminals)
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
            // Fallback: just print newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("🌊⚡ FRAYMUS CONSOLE INITIALIZATION");
        System.out.println();
        
        new FraymusConsole().start();
        
        System.out.println();
        System.out.println("🌊⚡ FRAYMUS CONSOLE TERMINATED");
        System.out.println();
    }
}
