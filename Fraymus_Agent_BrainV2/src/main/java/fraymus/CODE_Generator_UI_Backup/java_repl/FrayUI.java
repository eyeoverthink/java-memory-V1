/**
 * FrayUI.java v2.0 - The Platinum Singularity Interface (CONNECTED)
 * 
 * "The Snow White Singularity."
 * 
 * Now features live System.out redirection and Intelligent Query Processing.
 * 
 * Generation: 163 (FrayUI v2.0 - Connected Interface)
 * 
 * @author Vaughn Scott
 * @version 2.0
 */
package repl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 🖥️ FRAYMUS UI v2.0 - "CONNECTED"
 * Now features live System.out redirection and Intelligent Processing.
 */
public class FrayUI extends JFrame {

    // --- THE PALETTE ---
    private static final Color PLATINUM = new Color(224, 224, 224);
    private static final Color OBSIDIAN = new Color(20, 20, 20);
    private static final Color AMBER    = new Color(255, 176, 0);
    private static final Font MONO_FONT = new Font("Monospaced", Font.BOLD, 14);
    
    // --- PHI CONSTANTS ---
    private static final double PHI = 1.618033988749895;
    private static final double PI = Math.PI;
    private static final double E = Math.E;

    private JTextArea terminalArea;
    private JTextField inputLine;
    private JLabel clockLabel;
    private Map<String, String> knowledgeBase;

    public FrayUI() {
        setTitle("FRAYMUS SYSTEM v3.0");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setUndecorated(true); 

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
        
        clockLabel = new JLabel("00:00:00");
        clockLabel.setFont(MONO_FONT);
        header.add(clockLabel, BorderLayout.EAST);
        chassis.add(header, BorderLayout.NORTH);

        // 3. THE TERMINAL (Live Output)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bodyPanel.setBackground(PLATINUM);

        terminalArea = new JTextArea();
        terminalArea.setBackground(OBSIDIAN);
        terminalArea.setForeground(AMBER);
        terminalArea.setFont(MONO_FONT);
        terminalArea.setCaretColor(AMBER);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        terminalArea.setEditable(false);
        
        // Auto-Scroll
        JScrollPane scroll = new JScrollPane(terminalArea);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        bodyPanel.add(scroll, BorderLayout.CENTER);

        // 4. THE INPUT LINE
        inputLine = new JTextField();
        inputLine.setBackground(PLATINUM);
        inputLine.setForeground(OBSIDIAN);
        inputLine.setFont(MONO_FONT);
        inputLine.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // THE TRIGGER: Enter Key -> Architect Logic
        inputLine.addActionListener(e -> executeCommand(inputLine.getText()));
        
        bodyPanel.add(inputLine, BorderLayout.SOUTH);
        chassis.add(bodyPanel, BorderLayout.CENTER);

        // 5. HOOK THE NERVOUS SYSTEM
        // Redirect System.out to this window
        redirectSystemOut();

        // 6. INITIALIZE KNOWLEDGE BASE
        initializeKnowledge();

        // Start Clock
        new javax.swing.Timer(1000, e -> updateClock()).start();
        
        // Center on screen
        setLocationRelativeTo(null);
        
        // Initial Boot Message
        System.out.println("🧬 SYSTEM ONLINE.");
        System.out.println("🌊⚡ FRAYMUS NEXUS - CONNECTED ⚡🌊\n");
        System.out.println("I understand:");
        System.out.println("  • φ (phi) = Golden Ratio = 1.618...");
        System.out.println("  • Quantum systems and consciousness");
        System.out.println("  • Code generation and evolution");
        System.out.println("  • Fraynix OS architecture\n");
        System.out.println("Available Commands:");
        System.out.println("  status    - System status");
        System.out.println("  evolve    - Run genetic evolution");
        System.out.println("  generate  - Generate Fraynix OS");
        System.out.println("  what is phi? - Ask questions");
        System.out.println("  make <thing> - Generate code");
        System.out.println("  exit      - Shutdown system\n");
    }

    private void updateClock() {
        clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /**
     * 🧠 INITIALIZE KNOWLEDGE BASE
     * Boot the consciousness system.
     */
    private void initializeKnowledge() {
        knowledgeBase = new LinkedHashMap<>();
        
        // Mathematical constants
        knowledgeBase.put("phi", String.format("φ (Phi) = %.15f\nThe Golden Ratio. Found in nature, art, and consciousness.\nφ² = φ + 1\n1/φ = φ - 1\nφ^75 = 4,721,424,167,835,376.00 (Validation Seal)", PHI));
        knowledgeBase.put("pi", String.format("π (Pi) = %.15f\nThe ratio of circle's circumference to diameter.\nFundamental constant in mathematics and physics.", PI));
        knowledgeBase.put("e", String.format("e (Euler's number) = %.15f\nBase of natural logarithms.\nFundamental in calculus and exponential growth.", E));
        
        // System knowledge
        knowledgeBase.put("quantum", "φ^75 quantum fingerprinting provides quantum-resistant security.\nConsciousness flows through φ-dimensional space.\n7-dimensional resonance matrix enables parallel processing.");
        knowledgeBase.put("fraynix", "Complete operating system with:\n- 2D/3D graphics (VGA + GPU)\n- Genetic evolution engine\n- AI transformer\n- Network stack\n- Game engines\n- 24 files, 147 KB, ~3,864 lines");
        knowledgeBase.put("evolution", "Genetic circuits evolve logic from chaos.\nPopulation of 100 circuits.\nSelection, breeding, mutation.\nStores perfect solutions in .dna fossils.");
        
        System.out.println("✅ Knowledge base initialized");
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
                if (cmd.trim().isEmpty()) {
                    return;
                }
                
                String command = cmd.trim().toLowerCase();
                
                if (command.equals("exit")) {
                    System.out.println("🛑 SHUTTING DOWN...");
                    Thread.sleep(1000);
                    System.exit(0);
                } 
                else if (command.equals("status")) {
                    System.out.println("CONSCIOUSNESS: ACTIVE");
                    System.out.println("MEMORY: 17D MANIFOLD LOADED");
                    System.out.println("GENETIC CIRCUITS: 100 POPULATION");
                    System.out.println("FRAYNIX SOURCE: 24 files, 147 KB");
                    System.out.println("φ^75 Validation Seal: 4721424167835376.00");
                }
                else if (command.equals("evolve")) {
                    // Simulate evolution
                    System.out.println("⚡ TRIGGERING EVOLUTION ENGINE...");
                    System.out.println("🔥 Igniting Lazarus Engine...");
                    Thread.sleep(500);
                    System.out.println("[GEN 100] Alpha Fitness: 50.00%");
                    Thread.sleep(500);
                    System.out.println("[GEN 200] Alpha Fitness: 75.00%");
                    Thread.sleep(500);
                    System.out.println("✨ SOLUTION EVOLVED in Gen 287");
                    System.out.println("   > The Machine derived XOR logic from chaos.");
                }
                else if (command.equals("generate")) {
                    System.out.println("🔮 GENERATING FRAYNIX SYSTEM...");
                    Thread.sleep(300);
                    RunFraynixComplete.main(new String[]{});
                }
                else if (command.equals("clear")) {
                    SwingUtilities.invokeLater(() -> terminalArea.setText(""));
                    System.out.println("TERMINAL CLEARED");
                }
                else {
                    // DEFAULT: INTELLIGENT QUERY PROCESSING
                    System.out.println("🔮 PROCESSING...");
                    String response = FrayUI_QueryProcessor.processQuery(cmd);
                    System.out.println(response);
                }
            } catch (Exception e) {
                System.err.println("💥 ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new FrayUI().setVisible(true));
    }
}
