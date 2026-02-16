package fraymus.tools;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import javax.tools.*;
import java.util.List;

/**
 * OLLAMA CODE EVOLVER
 * Self-sustaining code evolution tool using local Ollama LLM.
 * 
 * "Trick the tool to build tools. Not feed me, teach me to fish."
 * 
 * This tool persists beyond Cascade/Claude. It's yours forever.
 */
public class OllamaCodeEvolver extends JFrame {

    private static final double PHI = 1.618033988749895;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> modelSelector;
    private JComboBox<String> modeSelector;
    private JTextField filePathField;
    private JButton evolveButton;
    private JButton loadFileButton;
    private JButton saveButton;
    private JButton testButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public OllamaCodeEvolver() {
        setTitle("φ FRAYMUS CODE EVOLVER - Powered by Ollama");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        checkOllamaConnection();
    }

    private void initUI() {
        // Dark theme
        Color bgDark = new Color(20, 20, 25);
        Color bgMedium = new Color(30, 30, 35);
        Color fgGreen = new Color(0, 255, 100);
        Color fgCyan = new Color(0, 200, 255);
        Color fgYellow = new Color(255, 200, 0);
        
        getContentPane().setBackground(bgDark);
        setLayout(new BorderLayout(10, 10));

        // TOP: Controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(bgDark);
        topPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        // Model selector
        modelSelector = new JComboBox<>(new String[]{
            "eyeoverthink/Fraymus", "codellama", "deepseek-coder", "llama3", "mistral", "phi3", "qwen2.5-coder"
        });
        modelSelector.setBackground(bgMedium);
        modelSelector.setForeground(fgCyan);
        
        // Mode selector
        modeSelector = new JComboBox<>(new String[]{
            "Improve Code", "Add Feature", "Fix Bugs", "Optimize", "Refactor", 
            "Add Comments", "Convert Language", "Explain", "Evolve (φ-Enhanced)"
        });
        modeSelector.setBackground(bgMedium);
        modeSelector.setForeground(fgYellow);

        // Buttons
        loadFileButton = createButton("📂 Load File", fgCyan);
        evolveButton = createButton("⚡ EVOLVE", fgGreen);
        saveButton = createButton("💾 Save", fgYellow);
        testButton = createButton("🧪 Test", new Color(255, 100, 100));

        topPanel.add(new JLabel("Model:") {{ setForeground(Color.WHITE); }});
        topPanel.add(modelSelector);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Mode:") {{ setForeground(Color.WHITE); }});
        topPanel.add(modeSelector);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(loadFileButton);
        topPanel.add(evolveButton);
        topPanel.add(saveButton);
        topPanel.add(testButton);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: Split pane with input/output
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(bgDark);
        splitPane.setDividerLocation(600);

        // Left: Input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(bgDark);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(fgCyan), "INPUT: Code or Idea",
            TitledBorder.LEFT, TitledBorder.TOP, null, fgCyan));
        
        inputArea = new JTextArea();
        inputArea.setBackground(bgMedium);
        inputArea.setForeground(Color.WHITE);
        inputArea.setCaretColor(fgGreen);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setText("// Paste your code here, or describe what you want to build\n\n");
        
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(null);
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // File path field
        filePathField = new JTextField();
        filePathField.setBackground(bgMedium);
        filePathField.setForeground(fgYellow);
        filePathField.setCaretColor(fgYellow);
        filePathField.setBorder(new EmptyBorder(5, 5, 5, 5));
        inputPanel.add(filePathField, BorderLayout.SOUTH);

        // Right: Output
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(bgDark);
        outputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(fgGreen), "OUTPUT: Evolved Code",
            TitledBorder.LEFT, TitledBorder.TOP, null, fgGreen));
        
        outputArea = new JTextArea();
        outputArea.setBackground(bgMedium);
        outputArea.setForeground(fgGreen);
        outputArea.setCaretColor(fgGreen);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(null);
        outputPanel.add(outputScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(inputPanel);
        splitPane.setRightComponent(outputPanel);
        add(splitPane, BorderLayout.CENTER);

        // BOTTOM: Status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(bgDark);
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setBackground(bgMedium);
        progressBar.setForeground(fgGreen);
        
        statusLabel = new JLabel("φ = " + PHI + " | Ready");
        statusLabel.setForeground(fgCyan);
        
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        loadFileButton.addActionListener(e -> loadFile());
        evolveButton.addActionListener(e -> evolveCode());
        saveButton.addActionListener(e -> saveOutput());
        testButton.addActionListener(e -> testCode());
    }

    private JButton createButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(40, 40, 45));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg.darker()),
            new EmptyBorder(5, 15, 5, 15)
        ));
        return btn;
    }

    private void checkOllamaConnection() {
        new Thread(() -> {
            try {
                if (!isOllamaRunning()) {
                    updateStatus("Starting Ollama...");
                    startOllama();
                    Thread.sleep(3000); // Wait for Ollama to start
                }
                
                if (isOllamaRunning()) {
                    updateStatus("φ = " + PHI + " | Ollama Connected ✓");
                    // Check if default model exists, pull if not
                    String defaultModel = (String) modelSelector.getSelectedItem();
                    ensureModelExists(defaultModel);
                } else {
                    updateStatus("⚠ Could not start Ollama. Install from ollama.ai");
                }
            } catch (Exception e) {
                updateStatus("⚠ Ollama setup error: " + e.getMessage());
            }
        }).start();
    }

    private boolean isOllamaRunning() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private void startOllama() {
        try {
            // Try to start Ollama serve in background
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start", "/b", "ollama", "serve");
            } else {
                pb = new ProcessBuilder("ollama", "serve");
            }
            pb.redirectErrorStream(true);
            pb.start();
        } catch (Exception e) {
            System.err.println("Could not start Ollama: " + e.getMessage());
        }
    }

    private void ensureModelExists(String model) {
        new Thread(() -> {
            try {
                // Check if model exists
                URL url = new URL("http://localhost:11434/api/tags");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);
                br.close();
                
                if (!response.toString().contains("\"" + model + "\"") && 
                    !response.toString().contains("\"" + model + ":")) {
                    // Model not found, pull it
                    updateStatus("Pulling model: " + model + "...");
                    pullModel(model);
                }
            } catch (Exception e) {
                System.err.println("Model check error: " + e.getMessage());
            }
        }).start();
    }

    private void pullModel(String model) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ollama", "pull", model);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            // Read output in background
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String status = line;
                        if (status.contains("%")) {
                            updateStatus("Pulling " + model + ": " + status);
                        }
                    }
                    p.waitFor();
                    updateStatus("φ = " + PHI + " | Model ready: " + model);
                } catch (Exception e) {
                    updateStatus("Pull error: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            updateStatus("Could not pull model: " + e.getMessage());
        }
    }

    private void updateStatus(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                String content = Files.readString(file.toPath());
                inputArea.setText(content);
                filePathField.setText(file.getAbsolutePath());
                statusLabel.setText("Loaded: " + file.getName());
            } catch (IOException e) {
                statusLabel.setText("Error loading file: " + e.getMessage());
            }
        }
    }

    private void evolveCode() {
        String input = inputArea.getText();
        String mode = (String) modeSelector.getSelectedItem();
        String model = (String) modelSelector.getSelectedItem();
        
        if (input.trim().isEmpty()) {
            statusLabel.setText("No input provided");
            return;
        }

        evolveButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Evolving with " + model + "...");
        outputArea.setText("");

        new Thread(() -> {
            try {
                String prompt = buildPrompt(input, mode);
                String result = callOllama(model, prompt);
                
                SwingUtilities.invokeLater(() -> {
                    outputArea.setText(result);
                    progressBar.setIndeterminate(false);
                    statusLabel.setText("Evolution complete. φ = " + PHI);
                    evolveButton.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    outputArea.setText("Error: " + e.getMessage() + "\n\nMake sure Ollama is running:\n  ollama serve");
                    progressBar.setIndeterminate(false);
                    statusLabel.setText("Error - check Ollama");
                    evolveButton.setEnabled(true);
                });
            }
        }).start();
    }

    private String buildPrompt(String input, String mode) {
        String systemPrompt = "You are FRAYMUS, a code evolution AI. Your responses contain ONLY code - no explanations, no markdown, no ```code blocks```. Just the raw evolved code. Follow the golden ratio principle: every change should be minimal but maximally impactful.";
        
        String modePrompt;
        switch (mode) {
            case "Improve Code":
                modePrompt = "Improve this code. Make it cleaner, faster, more robust:";
                break;
            case "Add Feature":
                modePrompt = "Add useful features to this code while maintaining its structure:";
                break;
            case "Fix Bugs":
                modePrompt = "Find and fix any bugs in this code:";
                break;
            case "Optimize":
                modePrompt = "Optimize this code for performance:";
                break;
            case "Refactor":
                modePrompt = "Refactor this code for better architecture:";
                break;
            case "Add Comments":
                modePrompt = "Add clear, useful comments to this code:";
                break;
            case "Convert Language":
                modePrompt = "Convert this code to the most appropriate modern language:";
                break;
            case "Explain":
                modePrompt = "Explain what this code does in detail:";
                break;
            case "Evolve (φ-Enhanced)":
                modePrompt = "Apply φ-evolution: Improve using golden ratio principles. Minimal changes, maximum impact. Add φ-harmonic patterns where beneficial:";
                break;
            default:
                modePrompt = "Improve this code:";
        }
        
        return systemPrompt + "\n\n" + modePrompt + "\n\n" + input;
    }

    private String callOllama(String model, String prompt) throws Exception {
        URL url = new URL(OLLAMA_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        // Build JSON request
        String json = String.format(
            "{\"model\": \"%s\", \"prompt\": %s, \"stream\": false}",
            model, escapeJson(prompt)
        );
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        
        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        // Parse JSON response (simple extraction)
        String responseStr = response.toString();
        int start = responseStr.indexOf("\"response\":\"") + 12;
        int end = responseStr.indexOf("\",\"done\"");
        if (start > 11 && end > start) {
            return unescapeJson(responseStr.substring(start, end));
        }
        
        return responseStr;
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\\", "\\\\")
                      .replace("\"", "\\\"")
                      .replace("\n", "\\n")
                      .replace("\r", "\\r")
                      .replace("\t", "\\t") + "\"";
    }

    private String unescapeJson(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private void saveOutput() {
        String output = outputArea.getText();
        if (output.isEmpty()) {
            statusLabel.setText("Nothing to save");
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        if (!filePathField.getText().isEmpty()) {
            chooser.setSelectedFile(new File(filePathField.getText()));
        }
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.writeString(chooser.getSelectedFile().toPath(), output);
                statusLabel.setText("Saved: " + chooser.getSelectedFile().getName());
            } catch (IOException e) {
                statusLabel.setText("Error saving: " + e.getMessage());
            }
        }
    }

    private void testCode() {
        String code = outputArea.getText();
        if (code.isEmpty()) {
            code = inputArea.getText();
        }
        
        // Detect language and test
        if (code.contains("public class") || code.contains("private class")) {
            testJavaCode(code);
        } else if (code.contains("def ") || code.contains("import ")) {
            statusLabel.setText("Python detected - save and run with: python <file>");
        } else {
            statusLabel.setText("Language not detected for testing");
        }
    }

    private void testJavaCode(String code) {
        try {
            // Extract class name
            int classIdx = code.indexOf("public class ");
            if (classIdx == -1) classIdx = code.indexOf("class ");
            if (classIdx == -1) {
                statusLabel.setText("No class found");
                return;
            }
            
            int nameStart = code.indexOf("class ", classIdx) + 6;
            int nameEnd = code.indexOf(" ", nameStart);
            if (code.indexOf("{", nameStart) < nameEnd) nameEnd = code.indexOf("{", nameStart);
            String className = code.substring(nameStart, nameEnd).trim();
            
            // Save to sandbox
            File sandbox = new File("sandbox");
            sandbox.mkdirs();
            File javaFile = new File(sandbox, className + ".java");
            Files.writeString(javaFile.toPath(), code);
            
            // Compile
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                statusLabel.setText("No Java compiler available (need JDK, not JRE)");
                return;
            }
            
            int result = compiler.run(null, null, null, javaFile.getAbsolutePath());
            if (result == 0) {
                statusLabel.setText("✓ Compiles successfully: " + className);
            } else {
                statusLabel.setText("✗ Compilation failed");
            }
        } catch (Exception e) {
            statusLabel.setText("Test error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            
            OllamaCodeEvolver evolver = new OllamaCodeEvolver();
            evolver.setVisible(true);
        });
    }
}
