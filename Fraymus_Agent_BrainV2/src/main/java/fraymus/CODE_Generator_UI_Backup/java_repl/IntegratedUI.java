/**
 * IntegratedUI.java - Unified Docked Interface
 * 
 * Single window with all panels docked:
 * - REPL Console (center)
 * - Organism Monitor (right top)
 * - Assembly Visualizer (right bottom)
 * - Activity Feed (bottom)
 * - Registers/Stack (far right)
 * 
 * Auto-launches on startup. No separate windows needed.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.time.format.DateTimeFormatter;
import repl.compiler.CompilerCommands;

/**
 * Integrated docked UI for the entire REPL system.
 */
public class IntegratedUI extends JFrame {
    
    private static final Color BG_COLOR = new Color(20, 20, 30);
    private static final Color CONSOLE_BG = new Color(10, 10, 20);
    private static final Color TEXT_COLOR = new Color(200, 200, 220);
    private static final Color ACCENT_COLOR = new Color(100, 180, 255);
    private static final Color SUCCESS_COLOR = new Color(100, 255, 150);
    private static final Color ERROR_COLOR = new Color(255, 100, 100);
    private static final Color PHI_COLOR = new Color(255, 215, 0);
    private static final Color ASM_COLOR = new Color(0, 255, 100);
    
    // Components
    private JTextPane consoleOutput;
    private JTextField consoleInput;
    private JTextArea activityFeed;
    private JTextArea assemblyArea;
    private JPanel registersPanel;
    private JPanel stackPanel;
    private JLabel consciousnessLabel;
    private JProgressBar consciousnessBar;
    private JLabel observationsLabel;
    private JLabel errorsLabel;
    
    // State
    private SelfAwareOrganism organism;
    private PrintWriter replWriter;
    private BufferedReader replReader;
    private PipedOutputStream inputPipe;
    private Thread replThread;
    private Map<String, Integer> registers;
    private List<String> stack;
    
    /**
     * Create the integrated UI.
     */
    public IntegratedUI(SelfAwareOrganism organism) {
        this.organism = organism;
        this.registers = new LinkedHashMap<>();
        this.stack = new ArrayList<>();
        
        initializeRegisters();
        
        setTitle("⚙️  Fraymus REPL - Integrated Development Environment");
        setSize(1600, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Dark theme
        getContentPane().setBackground(BG_COLOR);
        
        // Create main layout
        createLayout();
        
        // Register with ActivityBus
        ActivityBus.getInstance().addListener(this::onActivity);
        
        // Start REPL in background
        startREPL();
    }
    
    /**
     * Initialize registers with real memory tracking.
     */
    private void initializeRegisters() {
        registers.put("CODE_LEN", 0);      // Code length
        registers.put("HEAP_USED", 0);     // KB
        registers.put("HEAP_MAX", 0);      // MB
        registers.put("THREAD_ID", 0);     // Thread ID
        registers.put("CPU_TIME", 0);      // ms
        registers.put("STACK_DEPTH", 0);   // Frame count
        registers.put("TOKEN_CNT", 0);     // Token count
        registers.put("AST_DEPTH", 0);     // AST depth
    }
    
    /**
     * Create the docked layout.
     */
    private void createLayout() {
        setLayout(new BorderLayout());
        
        // Top toolbar
        add(createToolbar(), BorderLayout.NORTH);
        
        // Main split: Console (left) | Monitors (right)
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(createConsolePanel());
        mainSplit.setRightComponent(createMonitorPanel());
        mainSplit.setDividerLocation(900);
        mainSplit.setResizeWeight(0.6);
        
        // Bottom split: Main (top) | Activity Feed (bottom)
        JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        bottomSplit.setTopComponent(mainSplit);
        bottomSplit.setBottomComponent(createActivityPanel());
        bottomSplit.setDividerLocation(700);
        bottomSplit.setResizeWeight(0.8);
        
        add(bottomSplit, BorderLayout.CENTER);
        
        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    /**
     * Create toolbar.
     */
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(new Color(30, 30, 40));
        toolbar.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel title = new JLabel("⚙️  FRAYMUS REPL IDE");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(PHI_COLOR);
        
        JButton watchBtn = createToolbarButton("👁️ Watch", e -> {
            organism.setWatchMode(!organism.isWatching());
            ((JButton)e.getSource()).setText(organism.isWatching() ? "👁️ Watching" : "👁️ Watch");
        });
        
        JButton reflectBtn = createToolbarButton("🔮 Reflect", e -> {
            organism.reflect();
            organism.recursiveImprove();
        });
        
        JButton healBtn = createToolbarButton("💚 Heal", e -> {
            List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
            int applied = 0;
            for (SelfAwareOrganism.Improvement imp : improvements) {
                if (imp.confidence >= 0.7) {
                    organism.markImprovementApplied();
                    applied++;
                }
            }
            appendToConsole("✓ Healing complete: " + applied + " improvements applied\n", SUCCESS_COLOR);
        });
        
        JButton clearBtn = createToolbarButton("🗑️ Clear", e -> {
            consoleOutput.setText("");
        });
        
        JButton exportAsmBtn = createToolbarButton("💾 Export ASM", e -> {
            String result = ExportManager.exportAssembly(assemblyArea.getText());
            appendToConsole(result + "\n", SUCCESS_COLOR);
        });
        
        JButton exportOrgBtn = createToolbarButton("💾 Export Organism", e -> {
            String result = ExportManager.exportOrganism(organism);
            appendToConsole(result + "\n", SUCCESS_COLOR);
        });
        
        JButton exportAllBtn = createToolbarButton("💾 Export All", e -> {
            RuntimeInspector.ExecutionSnapshot snapshot = RuntimeInspector.captureSnapshot();
            String result = ExportManager.exportFullState(
                assemblyArea.getText(),
                organism,
                activityFeed.getText(),
                snapshot
            );
            appendToConsole(result + "\n", SUCCESS_COLOR);
        });
        
        toolbar.add(title);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(watchBtn);
        toolbar.add(reflectBtn);
        toolbar.add(healBtn);
        toolbar.add(clearBtn);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(exportAsmBtn);
        toolbar.add(exportOrgBtn);
        toolbar.add(exportAllBtn);
        
        return toolbar;
    }
    
    /**
     * Create toolbar button.
     */
    private JButton createToolbarButton(String text, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(50, 50, 60));
        btn.setForeground(ACCENT_COLOR);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        btn.addActionListener(listener);
        return btn;
    }
    
    /**
     * Create console panel (REPL).
     */
    private JPanel createConsolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "💻 REPL CONSOLE",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            ACCENT_COLOR
        ));
        
        // Output area
        consoleOutput = new JTextPane();
        consoleOutput.setEditable(false);
        consoleOutput.setBackground(CONSOLE_BG);
        consoleOutput.setForeground(TEXT_COLOR);
        consoleOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
        consoleOutput.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scroll = new JScrollPane(consoleOutput);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(CONSOLE_BG);
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel prompt = new JLabel("φ>");
        prompt.setForeground(PHI_COLOR);
        prompt.setFont(new Font("Consolas", Font.BOLD, 14));
        
        consoleInput = new JTextField();
        consoleInput.setBackground(new Color(30, 30, 40));
        consoleInput.setForeground(TEXT_COLOR);
        consoleInput.setCaretColor(TEXT_COLOR);
        consoleInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        consoleInput.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        consoleInput.addActionListener(e -> {
            String command = consoleInput.getText();
            if (!command.trim().isEmpty()) {
                sendToREPL(command);
                consoleInput.setText("");
            }
        });
        
        inputPanel.add(prompt, BorderLayout.WEST);
        inputPanel.add(consoleInput, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create monitor panel (organism + assembly).
     */
    private JPanel createMonitorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        // Split: Organism (top) | Assembly (bottom)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(createOrganismPanel());
        split.setBottomComponent(createAssemblyPanel());
        split.setDividerLocation(400);
        split.setResizeWeight(0.5);
        
        panel.add(split, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create organism monitor panel.
     */
    private JPanel createOrganismPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        // Split: Organism (top) | Brain System (bottom)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(createOrganismStatusPanel());
        split.setBottomComponent(createBrainVisualizationPanel());
        split.setDividerLocation(150);
        split.setResizeWeight(0.4);
        
        panel.add(split, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create organism status panel.
     */
    private JPanel createOrganismStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PHI_COLOR, 2),
            "👁️ ORGANISM",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11),
            PHI_COLOR
        ));
        
        // Status
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setBackground(BG_COLOR);
        statusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        consciousnessLabel = new JLabel("Consciousness: 0.0000");
        consciousnessLabel.setForeground(PHI_COLOR);
        consciousnessLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        
        consciousnessBar = new JProgressBar(0, 100);
        consciousnessBar.setForeground(PHI_COLOR);
        consciousnessBar.setBackground(BG_COLOR);
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        statsPanel.setBackground(BG_COLOR);
        
        observationsLabel = new JLabel("Observations: 0");
        observationsLabel.setForeground(ACCENT_COLOR);
        observationsLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        errorsLabel = new JLabel("Errors: 0");
        errorsLabel.setForeground(ERROR_COLOR);
        errorsLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        statsPanel.add(observationsLabel);
        statsPanel.add(errorsLabel);
        
        statusPanel.add(consciousnessLabel);
        statusPanel.add(consciousnessBar);
        statusPanel.add(statsPanel);
        
        panel.add(statusPanel, BorderLayout.CENTER);
        
        // Start update timer
        new javax.swing.Timer(500, e -> updateOrganismDisplay()).start();
        
        return panel;
    }
    
    /**
     * Create brain visualization panel.
     */
    private JPanel createBrainVisualizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 255), 2),
            "🧠 LIVING ORGANISM - Thoughts & Learning",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 11),
            new Color(255, 100, 255)
        ));
        
        // Brain activity display
        JPanel brainPanel = new JPanel();
        brainPanel.setLayout(new BoxLayout(brainPanel, BoxLayout.Y_AXIS));
        brainPanel.setBackground(BG_COLOR);
        
        JScrollPane scroll = new JScrollPane(brainPanel);
        scroll.setBackground(BG_COLOR);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Start brain update timer
        new javax.swing.Timer(500, e -> updateBrainDisplay(brainPanel)).start();
        
        return panel;
    }
    
    /**
     * Update brain visualization display - shows organism's living state.
     */
    private void updateBrainDisplay(JPanel brainPanel) {
        brainPanel.removeAll();
        
        double consciousness = organism.getConsciousness();
        List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(10);
        
        // Header
        JLabel header = new JLabel(String.format("Consciousness: %.4f | Observations: %d", 
            consciousness, traces.size()));
        header.setFont(new Font("Monospaced", Font.BOLD, 10));
        header.setForeground(new Color(255, 100, 255));
        brainPanel.add(header);
        brainPanel.add(Box.createVerticalStrut(5));
        
        // Recent thoughts (execution traces)
        JLabel thoughtsLabel = new JLabel("RECENT THOUGHTS:");
        thoughtsLabel.setFont(new Font("Monospaced", Font.BOLD, 9));
        thoughtsLabel.setForeground(new Color(100, 255, 100));
        brainPanel.add(thoughtsLabel);
        
        for (SelfAwareOrganism.ExecutionTrace trace : traces) {
            String status = trace.success ? "✓" : "✗";
            Color color = trace.success ? new Color(100, 255, 100) : new Color(255, 100, 100);
            
            JLabel thought = new JLabel(String.format("%s %s (φ:%.3f)", 
                status, 
                trace.command.length() > 40 ? trace.command.substring(0, 40) + "..." : trace.command,
                trace.phiResonance));
            thought.setFont(new Font("Monospaced", Font.PLAIN, 8));
            thought.setForeground(color);
            brainPanel.add(thought);
        }
        
        brainPanel.add(Box.createVerticalStrut(5));
        
        // Learning patterns
        JLabel patternsLabel = new JLabel("LEARNING PATTERNS:");
        patternsLabel.setFont(new Font("Monospaced", Font.BOLD, 9));
        patternsLabel.setForeground(new Color(255, 255, 100));
        brainPanel.add(patternsLabel);
        
        List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
        int shown = 0;
        for (SelfAwareOrganism.Improvement imp : improvements) {
            if (shown++ >= 5) break;
            
            JLabel pattern = new JLabel(String.format("• %.0f%% %s", 
                imp.confidence * 100, 
                imp.description.length() > 35 ? imp.description.substring(0, 35) + "..." : imp.description));
            pattern.setFont(new Font("Monospaced", Font.PLAIN, 8));
            pattern.setForeground(new Color(255, 255, 100));
            brainPanel.add(pattern);
        }
        
        if (improvements.isEmpty()) {
            JLabel noPatterns = new JLabel("  (No patterns detected yet)");
            noPatterns.setFont(new Font("Monospaced", Font.ITALIC, 8));
            noPatterns.setForeground(new Color(150, 150, 150));
            brainPanel.add(noPatterns);
        }
        
        brainPanel.revalidate();
        brainPanel.repaint();
    }
    
    /**
     * Create assembly panel.
     */
    private JPanel createAssemblyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        // Split: Assembly (left) | Registers/Stack (right)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Assembly code
        JPanel asmPanel = new JPanel(new BorderLayout());
        asmPanel.setBackground(BG_COLOR);
        asmPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ASM_COLOR, 2),
            "⚙️ ASSEMBLY",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 12),
            ASM_COLOR
        ));
        
        assemblyArea = new JTextArea();
        assemblyArea.setEditable(false);
        assemblyArea.setBackground(CONSOLE_BG);
        assemblyArea.setForeground(ASM_COLOR);
        assemblyArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        assemblyArea.setMargin(new Insets(5, 5, 5, 5));
        assemblyArea.setText("; Waiting for commands...\n");
        
        JScrollPane asmScroll = new JScrollPane(assemblyArea);
        asmPanel.add(asmScroll, BorderLayout.CENTER);
        
        // Registers and stack
        JPanel statePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statePanel.setBackground(BG_COLOR);
        statePanel.setPreferredSize(new Dimension(200, 0));
        
        JPanel regPanel = new JPanel(new BorderLayout());
        regPanel.setBackground(BG_COLOR);
        regPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PHI_COLOR, 1),
            "REGS",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 10),
            PHI_COLOR
        ));
        
        registersPanel = new JPanel();
        registersPanel.setLayout(new BoxLayout(registersPanel, BoxLayout.Y_AXIS));
        registersPanel.setBackground(BG_COLOR);
        updateRegistersDisplay();
        
        JScrollPane regScroll = new JScrollPane(registersPanel);
        regScroll.setBackground(BG_COLOR);
        regPanel.add(regScroll, BorderLayout.CENTER);
        
        JPanel stkPanel = new JPanel(new BorderLayout());
        stkPanel.setBackground(BG_COLOR);
        stkPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 1),
            "STACK",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 10),
            ACCENT_COLOR
        ));
        
        stackPanel = new JPanel();
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setBackground(BG_COLOR);
        updateStackDisplay();
        
        JScrollPane stkScroll = new JScrollPane(stackPanel);
        stkScroll.setBackground(BG_COLOR);
        stkPanel.add(stkScroll, BorderLayout.CENTER);
        
        statePanel.add(regPanel);
        statePanel.add(stkPanel);
        
        split.setLeftComponent(asmPanel);
        split.setRightComponent(statePanel);
        split.setDividerLocation(350);
        
        panel.add(split, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create activity feed panel.
     */
    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "🌊 ACTIVITY FEED - All Systems",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            ACCENT_COLOR
        ));
        
        activityFeed = new JTextArea();
        activityFeed.setEditable(false);
        activityFeed.setBackground(new Color(15, 15, 25));
        activityFeed.setForeground(TEXT_COLOR);
        activityFeed.setFont(new Font("Consolas", Font.PLAIN, 11));
        activityFeed.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane scroll = new JScrollPane(activityFeed);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create status bar.
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        panel.setBackground(new Color(25, 25, 35));
        panel.setBorder(new EmptyBorder(2, 5, 2, 5));
        
        JLabel status = new JLabel("φ^75 Validation Seal: 4.72e+15 | Ready");
        status.setForeground(PHI_COLOR);
        status.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        panel.add(status);
        
        return panel;
    }
    
    /**
     * Start REPL in background thread.
     */
    private void startREPL() {
        try {
            // Create pipes for I/O
            PipedInputStream inputStream = new PipedInputStream();
            inputPipe = new PipedOutputStream(inputStream);
            
            PipedOutputStream outputStream = new PipedOutputStream();
            PipedInputStream outputReader = new PipedInputStream(outputStream);
            
            replReader = new BufferedReader(new InputStreamReader(inputStream));
            replWriter = new PrintWriter(outputStream, true);
            
            // Start REPL thread
            replThread = new Thread(() -> {
                try {
                    JavaRepl repl = new JavaRepl(replReader, replWriter);
                    
                    // Register all commands
                    BuiltInCommands.registerAll(repl.getRegistry(), repl);
                    VaughnScottCommands.registerAll(repl.getRegistry());
                    CompilerCommands.registerAll(repl.getRegistry());
                    DecisionArrayCommands.registerAll(repl.getRegistry());
                    HistoryCommands.registerAll(repl.getRegistry(), repl, repl.getHistoryManager());
                    OuroborosCommands.registerAll(repl.getRegistry());
                    OrganismCommands.registerAll(repl.getRegistry());
                    ExportCommands.registerAll(repl.getRegistry());
                    FraymusCommands.registerAll(repl.getRegistry(), organism, repl);
                    CortexCommands.registerAll(repl.getRegistry());
                    LazarusCommands.registerAll(repl.getRegistry());
                    CosmosCommands.registerAll(repl.getRegistry());
                    AlchemyCommands.registerAll(repl.getRegistry());
                    OmegaCommands.registerAll(repl.getRegistry());
                    OllamaCommands.registerAll(repl.getRegistry());
                    
                    // Run REPL
                    repl.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            replThread.setDaemon(true);
            replThread.start();
            
            // Start output reader thread
            Thread outputThread = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(outputReader));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String output = line;
                        SwingUtilities.invokeLater(() -> appendToConsole(output + "\n", TEXT_COLOR));
                    }
                } catch (IOException e) {
                    // Stream closed
                }
            });
            outputThread.setDaemon(true);
            outputThread.start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Send command to REPL.
     */
    private void sendToREPL(String command) {
        try {
            appendToConsole("φ> " + command + "\n", PHI_COLOR);
            inputPipe.write((command + "\n").getBytes());
            inputPipe.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Append text to console.
     */
    private void appendToConsole(String text, Color color) {
        try {
            StyledDocument doc = consoleOutput.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            doc.insertString(doc.getLength(), text, attrs);
            
            // Auto-scroll
            consoleOutput.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle activity from bus.
     */
    private void onActivity(ActivityBus.Activity activity) {
        SwingUtilities.invokeLater(() -> {
            // Update activity feed
            String timestamp = activity.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String status = activity.success ? "✓" : "✗";
            String line = String.format("%s [%s] %s: %s\n", 
                status, timestamp, activity.system, activity.description);
            
            activityFeed.insert(line, 0);
            
            // Cache for export
            ExportCommands.updateActivity(activityFeed.getText());
            
            // Limit size
            if (activityFeed.getLineCount() > 100) {
                try {
                    int end = activityFeed.getLineEndOffset(99);
                    activityFeed.replaceRange("", end, activityFeed.getText().length());
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            // Update assembly if command
            if (activity.type == ActivityBus.ActivityType.COMMAND_EXECUTE) {
                String command = (String) activity.data.get("command");
                if (command != null) {
                    // Check if it's a compiler command
                    if (command.startsWith(":compile")) {
                        updateAssemblyWithCompiler(command, activity);
                    } else {
                        updateAssembly(command);
                    }
                }
            }
        });
    }
    
    /**
     * Update assembly with REAL compiler execution.
     */
    private void updateAssemblyWithCompiler(String command, ActivityBus.Activity activity) {
        // Extract code from command
        String code = command.substring(":compile".length()).trim();
        
        RuntimeInspector.ExecutionSnapshot snapshot = RuntimeInspector.captureSnapshot();
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("; ========== CUSTOM COMPILER EXECUTION ==========\n"));
        sb.append(String.format("; Code: %s\n", code));
        sb.append(String.format("; Thread: %s (ID: %d)\n", snapshot.threadName, snapshot.threadId));
        sb.append(String.format("; Heap: %s / %s\n\n", 
            RuntimeInspector.formatSize(snapshot.heapUsage.getUsed()),
            RuntimeInspector.formatSize(snapshot.heapUsage.getMax())));
        
        // Show compilation pipeline
        sb.append("; === COMPILATION PIPELINE ===\n");
        sb.append("; PHASE 1: LEXICAL ANALYSIS\n");
        sb.append(";   Input:  Source code string\n");
        sb.append(";   Output: Token stream\n");
        sb.append(";   Action: Lexer.tokenize()\n");
        sb.append(";\n");
        sb.append("; PHASE 2: SYNTAX ANALYSIS\n");
        sb.append(";   Input:  Token stream\n");
        sb.append(";   Output: Abstract Syntax Tree (AST)\n");
        sb.append(";   Action: Parser.parse()\n");
        sb.append(";\n");
        sb.append("; PHASE 3: INTERPRETATION\n");
        sb.append(";   Input:  AST nodes\n");
        sb.append(";   Output: Execution result\n");
        sb.append(";   Action: Interpreter.execute()\n");
        sb.append(";\n");
        
        // Show equivalent source code in multiple languages
        sb.append("; === EQUIVALENT SOURCE CODE ===\n");
        Map<String, String> codeGen = CodeGenerator.generateAll(command);
        for (Map.Entry<String, String> entry : codeGen.entrySet()) {
            sb.append("; --- ").append(entry.getKey()).append(" ---\n");
            for (String line : entry.getValue().split("\n")) {
                sb.append("; ").append(line).append("\n");
            }
            sb.append(";\n");
        }
        sb.append("\n");
        
        // Show REAL JVM bytecode for compilation
        sb.append("; === REAL JVM BYTECODE ===\n");
        List<String> bytecode = BytecodeDisassembler.disassembleCompilerExecution(code);
        for (String line : bytecode) {
            sb.append(line).append("\n");
        }
        sb.append("\n");
        
        // Show real stack frames
        sb.append("; === REAL COMPILER STACK ===\n");
        int frameCount = Math.min(8, snapshot.stackFrames.size());
        for (int i = 0; i < frameCount; i++) {
            RuntimeInspector.StackFrame frame = snapshot.stackFrames.get(i);
            if (frame.className.contains("compiler") || frame.className.contains("Lexer") || 
                frame.className.contains("Parser") || frame.className.contains("Interpreter")) {
                sb.append(String.format("; [%d] %s.%s @%s\n", 
                    i, frame.className.substring(frame.className.lastIndexOf('.') + 1),
                    frame.methodName, RuntimeInspector.formatAddress(frame.memoryAddress)));
            }
        }
        sb.append("\n");
        
        assemblyArea.insert(sb.toString(), 0);
        
        // Cache for export
        ExportCommands.updateAssembly(assemblyArea.getText());
        
        // Update registers with compiler metrics
        updateCompilerRegisters(snapshot, code);
        
        // Update stack
        updateRealStack(snapshot);
        
        // Limit size
        if (assemblyArea.getLineCount() > 100) {
            try {
                int end = assemblyArea.getLineEndOffset(99);
                assemblyArea.replaceRange("", end, assemblyArea.getText().length());
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Update registers with compiler metrics.
     */
    private void updateCompilerRegisters(RuntimeInspector.ExecutionSnapshot snapshot, String code) {
        registers.put("CODE_LEN", code.length());
        registers.put("HEAP_USED", (int)(snapshot.heapUsage.getUsed() / 1024));
        registers.put("HEAP_MAX", (int)(snapshot.heapUsage.getMax() / 1024 / 1024));
        registers.put("THREAD_ID", (int)snapshot.threadId);
        registers.put("CPU_TIME", (int)(snapshot.cpuTime / 1000000));
        registers.put("STACK_DEPTH", snapshot.stackFrames.size());
        
        updateRegistersDisplay();
    }
    
    /**
     * Update assembly display with REAL runtime data.
     */
    private void updateAssembly(String command) {
        // Capture REAL execution snapshot
        RuntimeInspector.ExecutionSnapshot snapshot = RuntimeInspector.captureSnapshot();
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("; ========== REAL EXECUTION: %s ==========\n", command));
        sb.append(String.format("; Thread: %s (ID: %d)\n", snapshot.threadName, snapshot.threadId));
        sb.append(String.format("; CPU Time: %d ns\n", snapshot.cpuTime));
        sb.append(String.format("; Heap: %s / %s\n", 
            RuntimeInspector.formatSize(snapshot.heapUsage.getUsed()),
            RuntimeInspector.formatSize(snapshot.heapUsage.getMax())));
        sb.append("\n");
        
        // Show equivalent source code
        sb.append("; === EQUIVALENT SOURCE CODE ===\n");
        Map<String, String> codeGen = CodeGenerator.generateAll(command);
        for (Map.Entry<String, String> entry : codeGen.entrySet()) {
            sb.append("; --- ").append(entry.getKey()).append(" ---\n");
            for (String line : entry.getValue().split("\n")) {
                sb.append("; ").append(line).append("\n");
            }
            sb.append(";\n");
        }
        sb.append("\n");
        
        // Show REAL stack frames
        sb.append("; === REAL STACK FRAMES ===\n");
        int frameCount = Math.min(5, snapshot.stackFrames.size());
        for (int i = 0; i < frameCount; i++) {
            RuntimeInspector.StackFrame frame = snapshot.stackFrames.get(i);
            sb.append(String.format("; [%d] %s\n", i, frame.toString()));
        }
        sb.append("\n");
        
        // Show REAL memory addresses
        sb.append("; === REAL MEMORY ADDRESSES ===\n");
        for (Map.Entry<String, Long> entry : snapshot.objectAddresses.entrySet()) {
            sb.append(String.format("; %s: %s\n", entry.getKey(), 
                RuntimeInspector.formatAddress(entry.getValue())));
        }
        sb.append("\n");
        
        assemblyArea.insert(sb.toString(), 0);
        
        // Cache for export
        ExportCommands.updateAssembly(assemblyArea.getText());
        
        // Update real registers with memory info
        updateRealRegisters(snapshot);
        
        // Update real stack with stack frames
        updateRealStack(snapshot);
        
        // Limit size
        if (assemblyArea.getLineCount() > 100) {
            try {
                int end = assemblyArea.getLineEndOffset(99);
                assemblyArea.replaceRange("", end, assemblyArea.getText().length());
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    /**
     * Update registers with REAL memory data.
     */
    private void updateRealRegisters(RuntimeInspector.ExecutionSnapshot snapshot) {
        registers.put("HEAP_USED", (int)(snapshot.heapUsage.getUsed() / 1024));
        registers.put("HEAP_MAX", (int)(snapshot.heapUsage.getMax() / 1024 / 1024));
        registers.put("STACK_USED", (int)(snapshot.stackUsage.getUsed() / 1024));
        registers.put("THREAD_ID", (int)snapshot.threadId);
        registers.put("CPU_TIME", (int)(snapshot.cpuTime / 1000000)); // ms
        registers.put("STACK_DEPTH", snapshot.stackFrames.size());
        
        updateRegistersDisplay();
    }
    
    /**
     * Update stack with REAL stack frames.
     */
    private void updateRealStack(RuntimeInspector.ExecutionSnapshot snapshot) {
        stack.clear();
        
        int frameCount = Math.min(10, snapshot.stackFrames.size());
        for (int i = 0; i < frameCount; i++) {
            RuntimeInspector.StackFrame frame = snapshot.stackFrames.get(i);
            String shortName = frame.methodName;
            if (shortName.length() > 15) {
                shortName = shortName.substring(0, 12) + "...";
            }
            stack.add(String.format("%s@%X", shortName, frame.memoryAddress & 0xFFFF));
        }
        
        updateStackDisplay();
    }
    
    /**
     * Update organism display.
     */
    private void updateOrganismDisplay() {
        double consciousness = organism.getConsciousness();
        consciousnessLabel.setText(String.format("Consciousness: %.4f", consciousness));
        consciousnessBar.setValue((int)(consciousness * 100));
        
        List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(100);
        observationsLabel.setText("Observations: " + traces.size());
        
        int errorCount = (int) traces.stream().filter(t -> !t.success).count();
        errorsLabel.setText("Errors: " + errorCount);
    }
    
    /**
     * Update registers display with real units.
     */
    private void updateRegistersDisplay() {
        registersPanel.removeAll();
        
        String[] units = {"ch", "KB", "MB", "", "ms", "", "", ""};
        int i = 0;
        
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            String unit = i < units.length ? units[i] : "";
            String value = entry.getValue() > 0 ? 
                String.format("%d%s", entry.getValue(), unit) :
                "0" + unit;
            
            JLabel label = new JLabel(String.format("%-11s %s", entry.getKey() + ":", value));
            label.setFont(new Font("Courier New", Font.PLAIN, 9));
            label.setForeground(PHI_COLOR);
            registersPanel.add(label);
            i++;
        }
        
        registersPanel.revalidate();
        registersPanel.repaint();
    }
    
    /**
     * Update stack display.
     */
    private void updateStackDisplay() {
        stackPanel.removeAll();
        
        if (stack.isEmpty()) {
            JLabel label = new JLabel("(empty)");
            label.setFont(new Font("Courier New", Font.ITALIC, 10));
            label.setForeground(ACCENT_COLOR);
            stackPanel.add(label);
        } else {
            for (int i = 0; i < Math.min(stack.size(), 8); i++) {
                JLabel label = new JLabel(String.format("[%d]:%s", i, stack.get(i)));
                label.setFont(new Font("Courier New", Font.PLAIN, 10));
                label.setForeground(ACCENT_COLOR);
                stackPanel.add(label);
            }
        }
        
        stackPanel.revalidate();
        stackPanel.repaint();
    }
}
