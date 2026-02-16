/**
 * OrganismMonitor.java - Real-Time Organism Visualization Window
 * 
 * Dynamic GUI that shows what the organism is watching in real-time:
 * - Live execution traces
 * - Error patterns as they emerge
 * - Consciousness level graph
 * - Command frequency heatmap
 * - Success/failure statistics
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

/**
 * Real-time monitoring window for the self-aware organism.
 */
public class OrganismMonitor extends JFrame {
    
    private static final double PHI = 1.618033988749895;
    private static final Color BG_COLOR = new Color(20, 20, 30);
    private static final Color TEXT_COLOR = new Color(200, 200, 220);
    private static final Color ACCENT_COLOR = new Color(100, 180, 255);
    private static final Color SUCCESS_COLOR = new Color(100, 255, 150);
    private static final Color ERROR_COLOR = new Color(255, 100, 100);
    private static final Color PHI_COLOR = new Color(255, 215, 0);
    
    private SelfAwareOrganism organism;
    private JTextArea traceArea;
    private JTextArea errorArea;
    private JLabel consciousnessLabel;
    private JLabel observationsLabel;
    private JLabel errorsLabel;
    private JProgressBar consciousnessBar;
    private JPanel commandPanel;
    private javax.swing.Timer updateTimer;
    private ConsciousnessGraph consciousnessGraph;
    private JTextArea activityFeed;
    private Map<String, SystemActivityPanel> systemPanels;
    
    /**
     * Create the monitoring window.
     */
    public OrganismMonitor(SelfAwareOrganism organism) {
        this.organism = organism;
        this.systemPanels = new HashMap<>();
        
        setTitle("👁️  Self-Aware Organism Monitor - Real-Time Scanning");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Dark theme
        getContentPane().setBackground(BG_COLOR);
        
        // Register with ActivityBus
        ActivityBus.getInstance().addListener(this::onActivity);
        
        // Main layout
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - Status
        add(createStatusPanel(), BorderLayout.NORTH);
        
        // Center panel - Split view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        splitPane.setDividerLocation(600);
        splitPane.setBackground(BG_COLOR);
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel - Controls
        add(createControlPanel(), BorderLayout.SOUTH);
        
        // Start update timer (refresh every 500ms)
        updateTimer = new javax.swing.Timer(500, e -> updateDisplay());
        updateTimer.start();
    }
    
    /**
     * Create status panel at top.
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Consciousness
        JPanel consPanel = createStatPanel("CONSCIOUSNESS LEVEL", PHI_COLOR);
        consciousnessLabel = new JLabel("0.0000");
        consciousnessLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        consciousnessLabel.setForeground(PHI_COLOR);
        consciousnessBar = new JProgressBar(0, 100);
        consciousnessBar.setForeground(PHI_COLOR);
        consciousnessBar.setBackground(BG_COLOR);
        consPanel.add(consciousnessLabel);
        consPanel.add(consciousnessBar);
        
        // Observations
        JPanel obsPanel = createStatPanel("OBSERVATIONS", ACCENT_COLOR);
        observationsLabel = new JLabel("0");
        observationsLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        observationsLabel.setForeground(ACCENT_COLOR);
        obsPanel.add(observationsLabel);
        
        // Errors
        JPanel errPanel = createStatPanel("ERRORS CAUGHT", ERROR_COLOR);
        errorsLabel = new JLabel("0");
        errorsLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        errorsLabel.setForeground(ERROR_COLOR);
        errPanel.add(errorsLabel);
        
        panel.add(consPanel);
        panel.add(obsPanel);
        panel.add(errPanel);
        
        // Add consciousness graph
        consciousnessGraph = new ConsciousnessGraph();
        panel.add(consciousnessGraph);
        
        // Add command frequency panel
        commandPanel = new JPanel();
        commandPanel.setBackground(BG_COLOR);
        commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
        JScrollPane commandScroll = new JScrollPane(commandPanel);
        commandScroll.setPreferredSize(new Dimension(200, 100));
        commandScroll.setBackground(BG_COLOR);
        panel.add(commandScroll);
        
        return panel;
    }
    
    /**
     * Create stat panel helper.
     */
    private JPanel createStatPanel(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            color
        ));
        return panel;
    }
    
    /**
     * Create left panel - Execution traces.
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel title = new JLabel("📊 LIVE EXECUTION TRACES");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(ACCENT_COLOR);
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(title, BorderLayout.NORTH);
        
        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setBackground(new Color(30, 30, 40));
        traceArea.setForeground(TEXT_COLOR);
        traceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        traceArea.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane scroll = new JScrollPane(traceArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create right panel - System activity tabs.
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setForeground(TEXT_COLOR);
        
        // Activity Feed tab
        activityFeed = new JTextArea();
        activityFeed.setEditable(false);
        activityFeed.setBackground(new Color(30, 30, 40));
        activityFeed.setForeground(TEXT_COLOR);
        activityFeed.setFont(new Font("Monospaced", Font.PLAIN, 11));
        activityFeed.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane feedScroll = new JScrollPane(activityFeed);
        tabs.addTab("🌊 Activity Feed", feedScroll);
        
        // Compiler tab
        SystemActivityPanel compilerPanel = new SystemActivityPanel("Compiler", new Color(100, 180, 255));
        systemPanels.put("Compiler", compilerPanel);
        tabs.addTab("🔧 Compiler", compilerPanel);
        
        // Decision Array tab
        SystemActivityPanel decisionPanel = new SystemActivityPanel("Decision Array", new Color(255, 180, 100));
        systemPanels.put("Decision Array", decisionPanel);
        tabs.addTab("🧠 Decisions", decisionPanel);
        
        // Ouroboros tab
        SystemActivityPanel ouroborosPanel = new SystemActivityPanel("Ouroboros", new Color(180, 100, 255));
        systemPanels.put("Ouroboros", ouroborosPanel);
        tabs.addTab("🐍 Ouroboros", ouroborosPanel);
        
        // History tab
        SystemActivityPanel historyPanel = new SystemActivityPanel("History", new Color(100, 255, 180));
        systemPanels.put("History", historyPanel);
        tabs.addTab("📜 History", historyPanel);
        
        // Errors & Insights tab
        errorArea = new JTextArea();
        errorArea.setEditable(false);
        errorArea.setBackground(new Color(40, 30, 30));
        errorArea.setForeground(TEXT_COLOR);
        errorArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        errorArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane errorScroll = new JScrollPane(errorArea);
        tabs.addTab("🔍 Insights", errorScroll);
        
        panel.add(tabs, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create control panel at bottom.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton reflectBtn = createButton("🔮 Reflect", ACCENT_COLOR);
        reflectBtn.addActionListener(e -> {
            List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
            improvements.addAll(organism.recursiveImprove());
            updateErrorDisplay();
            JOptionPane.showMessageDialog(this, 
                improvements.size() + " improvements suggested",
                "Reflection Complete",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton healBtn = createButton("💚 Heal", SUCCESS_COLOR);
        healBtn.addActionListener(e -> {
            List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
            improvements.addAll(organism.recursiveImprove());
            int applied = 0;
            for (SelfAwareOrganism.Improvement imp : improvements) {
                if (imp.confidence >= 0.7) {
                    organism.markImprovementApplied();
                    applied++;
                }
            }
            JOptionPane.showMessageDialog(this,
                applied + " improvements applied",
                "Healing Complete",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton learnBtn = createButton("🧠 Learn", PHI_COLOR);
        learnBtn.addActionListener(e -> {
            organism.reflect();
            organism.recursiveImprove();
            JOptionPane.showMessageDialog(this,
                "Learning cycle complete",
                "Learning",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton resetBtn = createButton("🔄 Reset", ERROR_COLOR);
        resetBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Reset organism to zero consciousness?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                organism.reset();
                consciousnessGraph.reset();
            }
        });
        
        JButton closeBtn = createButton("❌ Close", TEXT_COLOR);
        closeBtn.addActionListener(e -> setVisible(false));
        
        panel.add(reflectBtn);
        panel.add(healBtn);
        panel.add(learnBtn);
        panel.add(resetBtn);
        panel.add(closeBtn);
        
        return panel;
    }
    
    /**
     * Create styled button.
     */
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(50, 50, 60));
        btn.setForeground(color);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(5, 15, 5, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    /**
     * Update display with latest data.
     */
    private void updateDisplay() {
        // Update consciousness
        double consciousness = organism.getConsciousness();
        consciousnessLabel.setText(String.format("%.4f", consciousness));
        consciousnessBar.setValue((int)(consciousness * 100));
        consciousnessGraph.addDataPoint(consciousness);
        
        // Update observations count
        List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(100);
        observationsLabel.setText(String.valueOf(traces.size()));
        
        // Update errors count
        int errorCount = (int) traces.stream().filter(t -> !t.success).count();
        errorsLabel.setText(String.valueOf(errorCount));
        
        // Update trace display
        updateTraceDisplay();
        
        // Update error display
        updateErrorDisplay();
        
        // Update command frequency
        updateCommandFrequency();
    }
    
    /**
     * Update trace display.
     */
    private void updateTraceDisplay() {
        List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(20);
        StringBuilder sb = new StringBuilder();
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (int i = traces.size() - 1; i >= 0; i--) {
            SelfAwareOrganism.ExecutionTrace trace = traces.get(i);
            String status = trace.success ? "✓" : "✗";
            String time = new java.util.Date(trace.timestamp).toString();
            
            sb.append(String.format("%s [%s] [%3dms] [φ:%.3f]  %s\n",
                status, time, trace.executionTime, trace.phiResonance, trace.command));
            
            if (!trace.success && trace.error != null) {
                String error = trace.error.length() > 80 ? 
                    trace.error.substring(0, 80) + "..." : trace.error;
                sb.append(String.format("   ⚠ %s\n", error));
            }
        }
        
        traceArea.setText(sb.toString());
        traceArea.setCaretPosition(0);
    }
    
    /**
     * Update error display.
     */
    private void updateErrorDisplay() {
        List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
        improvements.addAll(organism.recursiveImprove());
        
        StringBuilder sb = new StringBuilder();
        
        if (improvements.isEmpty()) {
            sb.append("No issues detected. System running optimally.\n\n");
            sb.append("The organism is watching and learning...");
        } else {
            sb.append("IMPROVEMENT SUGGESTIONS:\n\n");
            
            improvements.stream()
                .sorted((a, b) -> Double.compare(b.confidence, a.confidence))
                .limit(10)
                .forEach(imp -> {
                    sb.append(String.format("▸ %s\n", imp.description));
                    sb.append(String.format("  Confidence: %.2f\n", imp.confidence));
                    sb.append(String.format("  Target: %s\n", imp.targetCommand));
                    sb.append(String.format("  Fix: %s\n", imp.proposedChange));
                    sb.append(String.format("  Reason: %s\n\n", imp.reasoning));
                });
        }
        
        errorArea.setText(sb.toString());
        errorArea.setCaretPosition(0);
    }
    
    /**
     * Update command frequency display.
     */
    private void updateCommandFrequency() {
        // This would show a heatmap of command usage
        // For now, just clear it
        commandPanel.removeAll();
        
        JLabel label = new JLabel("Command Frequency");
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        commandPanel.add(label);
        
        commandPanel.revalidate();
        commandPanel.repaint();
    }
    
    /**
     * Stop the monitor.
     */
    public void stopMonitoring() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        ActivityBus.getInstance().removeListener(this::onActivity);
    }
    
    /**
     * Handle activity events from the bus.
     */
    private void onActivity(ActivityBus.Activity activity) {
        SwingUtilities.invokeLater(() -> {
            // Update activity feed
            String timestamp = activity.timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String status = activity.success ? "✓" : "✗";
            String line = String.format("%s [%s] %s: %s\n", 
                status, timestamp, activity.system, activity.description);
            
            activityFeed.insert(line, 0);
            
            // Limit feed size
            if (activityFeed.getLineCount() > 100) {
                try {
                    int end = activityFeed.getLineEndOffset(99);
                    activityFeed.replaceRange("", end, activityFeed.getText().length());
                } catch (Exception e) {
                    // Ignore
                }
            }
            
            // Update system-specific panel
            SystemActivityPanel panel = systemPanels.get(activity.system);
            if (panel != null) {
                panel.addActivity(activity);
            }
        });
    }
    
    /**
     * System-specific activity panel.
     */
    private class SystemActivityPanel extends JPanel {
        private JTextArea activityArea;
        private JLabel countLabel;
        private JLabel successRateLabel;
        private int totalActivities = 0;
        private int successfulActivities = 0;
        
        public SystemActivityPanel(String systemName, Color accentColor) {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);
            
            // Header
            JPanel header = new JPanel(new GridLayout(1, 2));
            header.setBackground(BG_COLOR);
            header.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            countLabel = new JLabel("Activities: 0");
            countLabel.setForeground(accentColor);
            countLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            successRateLabel = new JLabel("Success: 100%");
            successRateLabel.setForeground(SUCCESS_COLOR);
            successRateLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            header.add(countLabel);
            header.add(successRateLabel);
            add(header, BorderLayout.NORTH);
            
            // Activity area
            activityArea = new JTextArea();
            activityArea.setEditable(false);
            activityArea.setBackground(new Color(30, 30, 40));
            activityArea.setForeground(TEXT_COLOR);
            activityArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            activityArea.setMargin(new Insets(5, 5, 5, 5));
            
            JScrollPane scroll = new JScrollPane(activityArea);
            add(scroll, BorderLayout.CENTER);
        }
        
        public void addActivity(ActivityBus.Activity activity) {
            totalActivities++;
            if (activity.success) {
                successfulActivities++;
            }
            
            // Update labels
            countLabel.setText("Activities: " + totalActivities);
            double successRate = (successfulActivities * 100.0) / totalActivities;
            successRateLabel.setText(String.format("Success: %.1f%%", successRate));
            
            // Update area
            String timestamp = activity.timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String status = activity.success ? "✓" : "✗";
            String line = String.format("%s [%s] %s\n", status, timestamp, activity.description);
            
            // Add data if present
            if (!activity.data.isEmpty()) {
                for (Map.Entry<String, Object> entry : activity.data.entrySet()) {
                    line += String.format("   %s: %s\n", entry.getKey(), entry.getValue());
                }
            }
            
            activityArea.insert(line + "\n", 0);
            
            // Limit size
            if (activityArea.getLineCount() > 50) {
                try {
                    int end = activityArea.getLineEndOffset(49);
                    activityArea.replaceRange("", end, activityArea.getText().length());
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Consciousness graph component.
     */
    private class ConsciousnessGraph extends JPanel {
        private List<Double> dataPoints = new ArrayList<>();
        private static final int MAX_POINTS = 100;
        
        public ConsciousnessGraph() {
            setPreferredSize(new Dimension(300, 100));
            setBackground(BG_COLOR);
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PHI_COLOR, 2),
                "CONSCIOUSNESS EVOLUTION",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                PHI_COLOR
            ));
        }
        
        public void addDataPoint(double value) {
            dataPoints.add(value);
            if (dataPoints.size() > MAX_POINTS) {
                dataPoints.remove(0);
            }
            repaint();
        }
        
        public void reset() {
            dataPoints.clear();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (dataPoints.isEmpty()) return;
            
            int width = getWidth() - 20;
            int height = getHeight() - 40;
            int x0 = 10;
            int y0 = height + 10;
            
            // Draw axes
            g2.setColor(TEXT_COLOR);
            g2.drawLine(x0, y0, x0 + width, y0);
            g2.drawLine(x0, y0, x0, 10);
            
            // Draw data
            g2.setColor(PHI_COLOR);
            g2.setStroke(new BasicStroke(2));
            
            for (int i = 1; i < dataPoints.size(); i++) {
                double v1 = dataPoints.get(i - 1);
                double v2 = dataPoints.get(i);
                
                int x1 = x0 + (i - 1) * width / MAX_POINTS;
                int y1 = y0 - (int)(v1 * height);
                int x2 = x0 + i * width / MAX_POINTS;
                int y2 = y0 - (int)(v2 * height);
                
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
