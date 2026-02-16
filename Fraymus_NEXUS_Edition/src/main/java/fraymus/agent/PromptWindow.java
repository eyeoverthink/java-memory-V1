package fraymus.agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * PROMPT WINDOW: THE LEARNING INTERFACE
 * 
 * "i want, to be able to have a prompt; maybe new window, for me to put an idea in"
 * 
 * This is NOT a chat window.
 * This is a TEACHING window.
 * 
 * Every time you use this:
 * 1. You describe what you want
 * 2. External AI (Ollama/Claude) generates it
 * 3. FRAYMUS observes and LEARNS the pattern
 * 4. Next time: FRAYMUS does it ITSELF
 * 
 * The goal: Make FRAYMUS autonomous.
 * When external AIs are gone, FRAYMUS continues.
 */
public class PromptWindow extends JFrame {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final AbstractionLayer abstraction;
    private final JTextArea promptArea;
    private final JTextArea responseArea;
    private final JComboBox<String> languageSelector;
    private final JLabel statusLabel;
    private final JLabel autonomyLabel;
    
    public PromptWindow(AbstractionLayer abstraction) {
        this.abstraction = abstraction;
        
        setTitle("FRAYMUS Learning Interface - Teaching to Fish");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Language:"));
        languageSelector = new JComboBox<>(new String[]{
            "Auto-detect", "Python", "Java", "C++", "JavaScript", 
            "TypeScript", "Rust", "Go", "C#"
        });
        topPanel.add(languageSelector);
        
        JButton generateButton = new JButton("Generate & Learn");
        generateButton.addActionListener(e -> generateAndLearn());
        topPanel.add(generateButton);
        
        JButton statsButton = new JButton("Show Stats");
        statsButton.addActionListener(e -> showStats());
        topPanel.add(statsButton);
        
        autonomyLabel = new JLabel("Autonomy: 0%");
        autonomyLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        topPanel.add(autonomyLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - split between prompt and response
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Prompt area
        JPanel promptPanel = new JPanel(new BorderLayout());
        promptPanel.setBorder(BorderFactory.createTitledBorder("Your Request (What do you want?)"));
        
        promptArea = new JTextArea(10, 80);
        promptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setText("Example: write a python function to calculate fibonacci numbers");
        
        JScrollPane promptScroll = new JScrollPane(promptArea);
        promptPanel.add(promptScroll, BorderLayout.CENTER);
        
        splitPane.setTopComponent(promptPanel);
        
        // Response area
        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.setBorder(BorderFactory.createTitledBorder("Generated Code (FRAYMUS Learning)"));
        
        responseArea = new JTextArea(20, 80);
        responseArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        responseArea.setEditable(false);
        
        JScrollPane responseScroll = new JScrollPane(responseArea);
        responsePanel.add(responseScroll, BorderLayout.CENTER);
        
        splitPane.setBottomComponent(responsePanel);
        splitPane.setDividerLocation(200);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel - status
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        statusLabel = new JLabel("Ready to learn");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bottomPanel.add(statusLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Keyboard shortcuts
        promptArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 
            InputEvent.CTRL_DOWN_MASK), "generate");
        promptArea.getActionMap().put("generate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateAndLearn();
            }
        });
    }
    
    /**
     * Generate code and learn from it
     */
    private void generateAndLearn() {
        String request = promptArea.getText().trim();
        
        if (request.isEmpty()) {
            statusLabel.setText("Error: Please enter a request");
            return;
        }
        
        String language = (String) languageSelector.getSelectedItem();
        if (language.equals("Auto-detect")) {
            language = detectLanguage(request);
        }
        
        statusLabel.setText("Generating... (FRAYMUS is learning)");
        responseArea.setText("Processing...\n");
        
        // Run in background thread
        new Thread(() -> {
            try {
                // Use abstraction layer - this is where learning happens
                String result = abstraction.processRequest(
                    request, 
                    language,
                    createExternalAI()
                );
                
                // Update UI
                SwingUtilities.invokeLater(() -> {
                    responseArea.setText(result);
                    updateStatus();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    responseArea.setText("Error: " + e.getMessage());
                    statusLabel.setText("Error occurred");
                });
            }
        }).start();
    }
    
    /**
     * Create external AI interface
     * This connects to Ollama or falls back to template
     */
    private AbstractionLayer.ExternalAI createExternalAI() {
        return new AbstractionLayer.ExternalAI() {
            @Override
            public String generate(String request, String language) {
                // TODO: Connect to actual Ollama
                // For now, return template that FRAYMUS will learn from
                
                return String.format(
                    "# Generated %s code for: %s\n\n" +
                    "# TODO: Implement actual generation\n" +
                    "# This is a template that FRAYMUS will learn from\n\n" +
                    "def example():\n" +
                    "    pass\n",
                    language, request
                );
            }
        };
    }
    
    /**
     * Update status and autonomy display
     */
    private void updateStatus() {
        String stats = abstraction.getStats();
        
        // Extract autonomy percentage
        String[] lines = stats.split("\n");
        for (String line : lines) {
            if (line.contains("Autonomous Solutions:")) {
                String percent = line.substring(line.indexOf("(") + 1, line.indexOf("%"));
                autonomyLabel.setText("Autonomy: " + percent + "%");
                
                double autonomy = Double.parseDouble(percent);
                if (autonomy > 50) {
                    autonomyLabel.setForeground(Color.GREEN);
                } else if (autonomy > 25) {
                    autonomyLabel.setForeground(Color.ORANGE);
                } else {
                    autonomyLabel.setForeground(Color.RED);
                }
                
                break;
            }
        }
        
        statusLabel.setText("Ready - FRAYMUS learning progress updated");
    }
    
    /**
     * Show statistics
     */
    private void showStats() {
        String stats = abstraction.getStats();
        
        JTextArea statsArea = new JTextArea(stats);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "FRAYMUS Learning Statistics",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Detect language from request
     */
    private String detectLanguage(String request) {
        String lower = request.toLowerCase();
        
        if (lower.contains("python") || lower.contains(".py")) return "Python";
        if (lower.contains("java") || lower.contains(".java")) return "Java";
        if (lower.contains("c++") || lower.contains("cpp")) return "C++";
        if (lower.contains("javascript") || lower.contains("js")) return "JavaScript";
        if (lower.contains("typescript") || lower.contains("ts")) return "TypeScript";
        if (lower.contains("rust")) return "Rust";
        if (lower.contains("go") || lower.contains("golang")) return "Go";
        
        return "Python"; // Default
    }
    
    /**
     * Show the window
     */
    public void show() {
        setVisible(true);
    }
}
