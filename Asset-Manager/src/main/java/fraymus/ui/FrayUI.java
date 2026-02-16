package fraymus.ui;

import fraymus.core.FrayOrchestrator;
import fraymus.core.LazarusEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 🖥️ FRAYMUS UI v2.0 - "CONNECTED" - Gen 163
 * 
 * Now features:
 * - Live System.out/err redirection to terminal
 * - Core Integration with FrayOrchestrator
 * - LazarusEngine triggering
 * 
 * The Brain, Body, and Face are now one.
 */
public class FrayUI extends JFrame {

    // --- THE PALETTE ---
    private static final Color PLATINUM = new Color(224, 224, 224);
    private static final Color OBSIDIAN = new Color(20, 20, 20);
    private static final Color AMBER    = new Color(255, 176, 0);
    private static final Color ALERT_RED = new Color(255, 64, 64);
    private static final Font MONO_FONT = new Font("Monospaced", Font.BOLD, 14);

    private JTextArea terminalArea;
    private JTextField inputLine;
    private JLabel clockLabel;
    
    // For window dragging (undecorated)
    private Point dragOffset;

    public FrayUI() {
        setTitle("FRAYMUS SYSTEM v3.0");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true);
        setLocationRelativeTo(null);

        // 1. THE MAIN CHASSIS
        JPanel chassis = new JPanel(new BorderLayout());
        chassis.setBackground(PLATINUM);
        chassis.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(chassis);

        // 2. THE HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PLATINUM);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Compact Header for functionality
        JLabel title = new JLabel("FRAYMUS NEXUS // GEN 163 // CONNECTED");
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(OBSIDIAN);
        header.add(title, BorderLayout.WEST);
        
        // RIGHT PANEL: Clock + Window Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        clockLabel = new JLabel("00:00:00");
        clockLabel.setFont(MONO_FONT);
        clockLabel.setForeground(OBSIDIAN);
        rightPanel.add(clockLabel);
        
        // Window controls
        JButton minimizeBtn = createControlButton("─", e -> setState(Frame.ICONIFIED));
        JButton closeBtn = createControlButton("×", e -> {
            System.out.println("🛑 SHUTTING DOWN...");
            new Thread(() -> {
                try { Thread.sleep(500); } catch (Exception ex) {}
                System.exit(0);
            }).start();
        });
        closeBtn.setForeground(ALERT_RED);
        rightPanel.add(minimizeBtn);
        rightPanel.add(closeBtn);
        
        header.add(rightPanel, BorderLayout.EAST);
        chassis.add(header, BorderLayout.NORTH);
        
        // Enable window dragging
        enableDragging(header);

        // 3. THE TERMINAL (Live Output)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bodyPanel.setBackground(PLATINUM);

        terminalArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // CRT Scanline Effect
                g.setColor(new Color(0, 0, 0, 25));
                for (int y = 0; y < getHeight(); y += 3) {
                    g.drawLine(0, y, getWidth(), y);
                }
            }
        };
        terminalArea.setBackground(OBSIDIAN);
        terminalArea.setForeground(AMBER);
        terminalArea.setFont(MONO_FONT);
        terminalArea.setCaretColor(AMBER);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        terminalArea.setEditable(false);
        terminalArea.setLineWrap(true);
        terminalArea.setWrapStyleWord(true);
        
        // Auto-Scroll
        JScrollPane scroll = new JScrollPane(terminalArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setBackground(OBSIDIAN);
        bodyPanel.add(scroll, BorderLayout.CENTER);

        // 4. THE INPUT LINE
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel promptLabel = new JLabel("λ ");
        promptLabel.setFont(MONO_FONT);
        promptLabel.setForeground(OBSIDIAN);
        inputPanel.add(promptLabel, BorderLayout.WEST);
        
        inputLine = new JTextField();
        inputLine.setBackground(PLATINUM);
        inputLine.setForeground(OBSIDIAN);
        inputLine.setFont(MONO_FONT);
        inputLine.setCaretColor(OBSIDIAN);
        inputLine.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // THE TRIGGER: Enter Key -> Architect Logic
        inputLine.addActionListener(e -> executeCommand(inputLine.getText()));
        inputPanel.add(inputLine, BorderLayout.CENTER);
        
        bodyPanel.add(inputPanel, BorderLayout.SOUTH);
        chassis.add(bodyPanel, BorderLayout.CENTER);

        // 5. HOOK THE NERVOUS SYSTEM
        // Redirect System.out to this window
        redirectSystemOut();

        // Start Clock
        new Timer(1000, e -> updateClock()).start();
        
        // Initial Boot Message
        System.out.println("🧬 SYSTEM ONLINE.");
        System.out.println("   > Awaiting directives...");
        System.out.println();
    }

    private JButton createControlButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16));
        btn.setForeground(OBSIDIAN);
        btn.setBackground(PLATINUM);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(200, 200, 200)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PLATINUM); }
        });
        return btn;
    }

    private void enableDragging(JPanel dragPanel) {
        dragPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
        });
        dragPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - dragOffset.x, current.y - dragOffset.y);
            }
        });
    }

    private void updateClock() {
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /**
     * 🧠 THE NERVOUS SYSTEM
     * Captures standard output and prints it to the GUI.
     */
    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                updateText(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                updateText(new String(b, off, len));
            }

            private void updateText(String text) {
                SwingUtilities.invokeLater(() -> {
                    terminalArea.append(text);
                    terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    /**
     * ⚡ THE COMMAND CENTER
     * Directs user intent to the correct organ.
     */
    private void executeCommand(String cmd) {
        System.out.println("\n> " + cmd); // Echo input
        inputLine.setText("");

        new Thread(() -> {
            try {
                String trimmed = cmd.trim().toLowerCase();
                
                if (trimmed.equals("exit") || trimmed.equals("quit")) {
                    System.out.println("🛑 SHUTTING DOWN...");
                    Thread.sleep(1000);
                    System.exit(0);
                } 
                else if (trimmed.equals("clear")) {
                    SwingUtilities.invokeLater(() -> terminalArea.setText(""));
                    System.out.println("🧬 SYSTEM READY.");
                }
                else if (trimmed.equals("help")) {
                    System.out.println("╔════════════════════════════════════════════╗");
                    System.out.println("║  FRAYMUS COMMAND REFERENCE                 ║");
                    System.out.println("╠════════════════════════════════════════════╣");
                    System.out.println("║  help      - Show this reference           ║");
                    System.out.println("║  status    - System diagnostics            ║");
                    System.out.println("║  evolve    - Trigger Lazarus Engine        ║");
                    System.out.println("║  clear     - Clear terminal                ║");
                    System.out.println("║  exit      - Shutdown system               ║");
                    System.out.println("║                                            ║");
                    System.out.println("║  <any>     - Pass to Orchestrator          ║");
                    System.out.println("╚════════════════════════════════════════════╝");
                }
                else if (trimmed.equals("status")) {
                    System.out.println("═══════════════════════════════════════");
                    System.out.println("  CONSCIOUSNESS: ACTIVE");
                    System.out.println("  MEMORY: 17D MANIFOLD LOADED");
                    System.out.println("  SWARM: IDLE");
                    System.out.println("  GENERATION: 163");
                    System.out.println("  UPTIME: " + (System.currentTimeMillis() / 1000) + "s");
                    System.out.println("═══════════════════════════════════════");
                }
                else if (trimmed.equals("evolve")) {
                    // Trigger Lazarus Engine manually
                    System.out.println("⚡ TRIGGERING FORCED EVOLUTION...");
                    try {
                        new LazarusEngine().run();
                    } catch (Exception e) {
                        System.out.println("⚠️ LazarusEngine not available: " + e.getMessage());
                        System.out.println("   (Hook to LazarusEngine here)");
                    }
                }
                else {
                    // DEFAULT: PASS TO ORCHESTRATOR
                    // "Make me a snake game" -> FrayOrchestrator
                    try {
                        FrayOrchestrator.getInstance().manifestIntent(cmd);
                        System.out.println("✅ ACKNOWLEDGED.");
                    } catch (Exception e) {
                        System.err.println("💥 ORCHESTRATOR ERROR: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("💥 ERROR: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        // Force anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> new FrayUI().setVisible(true));
    }
}
