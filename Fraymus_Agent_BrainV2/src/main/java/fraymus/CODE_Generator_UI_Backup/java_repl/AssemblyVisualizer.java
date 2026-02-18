/**
 * AssemblyVisualizer.java - Low-Level Command Deconstruction Window
 * 
 * Translates every REPL command into assembly-level operations.
 * Shows the low-level logic behind high-level commands.
 * Enables reverse engineering and deeper system understanding.
 * 
 * Assembly Operations:
 * - MOV (move data)
 * - PUSH (push to stack)
 * - POP (pop from stack)
 * - LOAD (load from memory)
 * - STORE (store to memory)
 * - ADD, SUB, MUL, DIV (arithmetic)
 * - CMP (compare)
 * - JMP, JE, JNE (jumps)
 * - CALL (function call)
 * - RET (return)
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package fraymus.CODE_Generator_UI_Backup.java_repl;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Visual window showing assembly-level deconstruction of commands.
 */
public class AssemblyVisualizer extends JFrame {
    
    private static final Color BG_COLOR = new Color(10, 10, 20);
    private static final Color TEXT_COLOR = new Color(0, 255, 100);
    private static final Color REGISTER_COLOR = new Color(255, 200, 0);
    private static final Color MEMORY_COLOR = new Color(100, 200, 255);
    private static final Color INSTRUCTION_COLOR = new Color(255, 100, 255);
    
    private JTextArea assemblyArea;
    private JPanel registersPanel;
    private JPanel stackPanel;
    private JTextArea analysisArea;
    private Map<String, Integer> registers;
    private java.util.List<String> stack;
    private int instructionPointer = 0;
    
    /**
     * Assembly instruction.
     */
    public static class Instruction {
        public final String opcode;
        public final String operand1;
        public final String operand2;
        public final String comment;
        
        public Instruction(String opcode, String operand1, String operand2, String comment) {
            this.opcode = opcode;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.comment = comment;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-8s", opcode));
            if (operand1 != null && !operand1.isEmpty()) {
                sb.append(String.format("%-8s", operand1));
            }
            if (operand2 != null && !operand2.isEmpty()) {
                sb.append(String.format("%-8s", operand2));
            }
            if (comment != null && !comment.isEmpty()) {
                sb.append("  ; ").append(comment);
            }
            return sb.toString();
        }
    }
    
    /**
     * Create the assembly visualizer window.
     */
    public AssemblyVisualizer() {
        setTitle("⚙️  Assembly Visualizer - Low-Level Command Deconstruction");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Retro terminal theme
        getContentPane().setBackground(BG_COLOR);
        
        // Initialize state
        registers = new LinkedHashMap<>();
        registers.put("AX", 0);  // Accumulator
        registers.put("BX", 0);  // Base
        registers.put("CX", 0);  // Counter
        registers.put("DX", 0);  // Data
        registers.put("SP", 0);  // Stack Pointer
        registers.put("BP", 0);  // Base Pointer
        registers.put("IP", 0);  // Instruction Pointer
        registers.put("FLAGS", 0);  // Flags register
        
        stack = new ArrayList<>();
        
        // Main layout
        setLayout(new BorderLayout(5, 5));
        
        // Top panel - Assembly code
        add(createAssemblyPanel(), BorderLayout.CENTER);
        
        // Right panel - Registers and stack
        add(createStatePanel(), BorderLayout.EAST);
        
        // Bottom panel - Analysis
        add(createAnalysisPanel(), BorderLayout.SOUTH);
        
        // Register with ActivityBus
        ActivityBus.getInstance().addListener(this::onActivity);
    }
    
    /**
     * Create assembly code panel.
     */
    private JPanel createAssemblyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("⚙️  ASSEMBLY CODE - Real-Time Deconstruction");
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(INSTRUCTION_COLOR);
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(title, BorderLayout.NORTH);
        
        assemblyArea = new JTextArea();
        assemblyArea.setEditable(false);
        assemblyArea.setBackground(BG_COLOR);
        assemblyArea.setForeground(TEXT_COLOR);
        assemblyArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        assemblyArea.setMargin(new Insets(10, 10, 10, 10));
        assemblyArea.setText("; Waiting for commands...\n; Each command will be deconstructed here\n");
        
        JScrollPane scroll = new JScrollPane(assemblyArea);
        scroll.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create state panel (registers and stack).
     */
    private JPanel createStatePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 5, 10, 10));
        panel.setPreferredSize(new Dimension(250, 0));
        
        // Registers
        JPanel regPanel = new JPanel(new BorderLayout());
        regPanel.setBackground(BG_COLOR);
        regPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(REGISTER_COLOR, 2),
            "REGISTERS",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 12),
            REGISTER_COLOR
        ));
        
        registersPanel = new JPanel();
        registersPanel.setLayout(new BoxLayout(registersPanel, BoxLayout.Y_AXIS));
        registersPanel.setBackground(BG_COLOR);
        updateRegistersDisplay();
        
        JScrollPane regScroll = new JScrollPane(registersPanel);
        regScroll.setBackground(BG_COLOR);
        regPanel.add(regScroll, BorderLayout.CENTER);
        
        // Stack
        JPanel stackPanelContainer = new JPanel(new BorderLayout());
        stackPanelContainer.setBackground(BG_COLOR);
        stackPanelContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(MEMORY_COLOR, 2),
            "STACK",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 12),
            MEMORY_COLOR
        ));
        
        stackPanel = new JPanel();
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setBackground(BG_COLOR);
        updateStackDisplay();
        
        JScrollPane stackScroll = new JScrollPane(stackPanel);
        stackScroll.setBackground(BG_COLOR);
        stackPanelContainer.add(stackScroll, BorderLayout.CENTER);
        
        panel.add(regPanel);
        panel.add(stackPanelContainer);
        
        return panel;
    }
    
    /**
     * Create analysis panel.
     */
    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(5, 10, 10, 10));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JLabel title = new JLabel("🔍 REVERSE ENGINEERING ANALYSIS");
        title.setFont(new Font("Monospaced", Font.BOLD, 12));
        title.setForeground(INSTRUCTION_COLOR);
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(title, BorderLayout.NORTH);
        
        analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setBackground(new Color(20, 20, 30));
        analysisArea.setForeground(TEXT_COLOR);
        analysisArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        analysisArea.setMargin(new Insets(5, 5, 5, 5));
        analysisArea.setText("Analysis will appear here...");
        
        JScrollPane scroll = new JScrollPane(analysisArea);
        scroll.setBorder(BorderFactory.createLineBorder(INSTRUCTION_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Handle activity from bus.
     */
    private void onActivity(ActivityBus.Activity activity) {
        if (activity.type == ActivityBus.ActivityType.COMMAND_EXECUTE) {
            String command = (String) activity.data.get("command");
            if (command != null) {
                SwingUtilities.invokeLater(() -> deconstructCommand(command));
            }
        }
    }
    
    /**
     * Deconstruct command into assembly.
     */
    private void deconstructCommand(String command) {
        List<Instruction> instructions = translateToAssembly(command);
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n; ========== COMMAND: %s ==========\n", command));
        sb.append(String.format("; IP: 0x%04X\n", instructionPointer));
        sb.append("\n");
        
        for (Instruction inst : instructions) {
            sb.append(String.format("0x%04X:  %s\n", instructionPointer++, inst.toString()));
            executeInstruction(inst);
        }
        
        sb.append("\n");
        assemblyArea.insert(sb.toString(), 0);
        
        // Limit size
        if (assemblyArea.getLineCount() > 200) {
            try {
                int end = assemblyArea.getLineEndOffset(199);
                assemblyArea.replaceRange("", end, assemblyArea.getText().length());
            } catch (Exception e) {
                // Ignore
            }
        }
        
        // Update displays
        updateRegistersDisplay();
        updateStackDisplay();
        updateAnalysis(command, instructions);
    }
    
    /**
     * Translate command to assembly instructions.
     */
    private List<Instruction> translateToAssembly(String command) {
        List<Instruction> instructions = new ArrayList<>();
        String[] parts = command.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();
        
        // Parse command type
        if (cmd.startsWith(":")) {
            // REPL command
            instructions.add(new Instruction("PUSH", "BP", "", "Save base pointer"));
            instructions.add(new Instruction("MOV", "BP", "SP", "Set up stack frame"));
            instructions.add(new Instruction("LOAD", "AX", "[CMD]", "Load command ID"));
            instructions.add(new Instruction("CMP", "AX", cmd, "Compare command"));
            instructions.add(new Instruction("JE", "EXEC_" + cmd.substring(1).toUpperCase(), "", "Jump if equal"));
            instructions.add(new Instruction("CALL", "REGISTRY", "", "Call command registry"));
            instructions.add(new Instruction("MOV", "SP", "BP", "Restore stack"));
            instructions.add(new Instruction("POP", "BP", "", "Restore base pointer"));
            instructions.add(new Instruction("RET", "", "", "Return"));
        } else if (cmd.equals("phi") || cmd.equals("fib") || cmd.equals("calc")) {
            // Math command
            instructions.add(new Instruction("PUSH", "AX", "", "Save accumulator"));
            instructions.add(new Instruction("LOAD", "BX", "[ARGS]", "Load arguments"));
            instructions.add(new Instruction("CALL", "MATH_" + cmd.toUpperCase(), "", "Call math function"));
            instructions.add(new Instruction("MOV", "AX", "RESULT", "Move result to AX"));
            instructions.add(new Instruction("CALL", "PRINT", "", "Print result"));
            instructions.add(new Instruction("POP", "AX", "", "Restore accumulator"));
            instructions.add(new Instruction("RET", "", "", "Return"));
        } else if (cmd.equals("watch") || cmd.equals("ouroboros")) {
            // System command
            instructions.add(new Instruction("LOAD", "CX", "[STATE]", "Load system state"));
            instructions.add(new Instruction("TEST", "CX", "ACTIVE", "Test if active"));
            instructions.add(new Instruction("JNE", "TOGGLE", "", "Jump if not equal"));
            instructions.add(new Instruction("MOV", "CX", "1", "Set active"));
            instructions.add(new Instruction("STORE", "[STATE]", "CX", "Save state"));
            instructions.add(new Instruction("RET", "", "", "Return"));
        } else {
            // Generic command
            instructions.add(new Instruction("PUSH", "BP", "", "Save base pointer"));
            instructions.add(new Instruction("LOAD", "AX", "[CMD]", "Load command"));
            instructions.add(new Instruction("PUSH", "AX", "", "Push to stack"));
            instructions.add(new Instruction("CALL", "EXECUTE", "", "Execute command"));
            instructions.add(new Instruction("ADD", "SP", "4", "Clean stack"));
            instructions.add(new Instruction("POP", "BP", "", "Restore BP"));
            instructions.add(new Instruction("RET", "", "", "Return"));
        }
        
        return instructions;
    }
    
    /**
     * Execute instruction (simulate).
     */
    private void executeInstruction(Instruction inst) {
        switch (inst.opcode.toUpperCase()) {
            case "PUSH":
                stack.add(0, inst.operand1);
                registers.put("SP", registers.get("SP") + 1);
                break;
            case "POP":
                if (!stack.isEmpty()) {
                    stack.remove(0);
                    registers.put("SP", registers.get("SP") - 1);
                }
                break;
            case "MOV":
                // Simulate register move
                if (registers.containsKey(inst.operand1)) {
                    registers.put(inst.operand1, registers.getOrDefault(inst.operand2, 0));
                }
                break;
            case "ADD":
                if (inst.operand1.equals("SP")) {
                    try {
                        int val = Integer.parseInt(inst.operand2);
                        registers.put("SP", registers.get("SP") + val);
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                break;
            case "CALL":
                stack.add(0, "RET_ADDR");
                registers.put("SP", registers.get("SP") + 1);
                break;
            case "RET":
                if (!stack.isEmpty() && stack.get(0).equals("RET_ADDR")) {
                    stack.remove(0);
                    registers.put("SP", registers.get("SP") - 1);
                }
                break;
        }
        
        registers.put("IP", instructionPointer);
    }
    
    /**
     * Update registers display.
     */
    private void updateRegistersDisplay() {
        registersPanel.removeAll();
        
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            JLabel label = new JLabel(String.format("%s: 0x%04X (%d)", 
                entry.getKey(), entry.getValue(), entry.getValue()));
            label.setFont(new Font("Courier New", Font.PLAIN, 12));
            label.setForeground(REGISTER_COLOR);
            registersPanel.add(label);
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
            label.setFont(new Font("Courier New", Font.ITALIC, 12));
            label.setForeground(MEMORY_COLOR);
            stackPanel.add(label);
        } else {
            for (int i = 0; i < Math.min(stack.size(), 10); i++) {
                JLabel label = new JLabel(String.format("[SP+%d]: %s", i, stack.get(i)));
                label.setFont(new Font("Courier New", Font.PLAIN, 12));
                label.setForeground(MEMORY_COLOR);
                stackPanel.add(label);
            }
        }
        
        stackPanel.revalidate();
        stackPanel.repaint();
    }
    
    /**
     * Update analysis.
     */
    private void updateAnalysis(String command, List<Instruction> instructions) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Command: %s\n", command));
        sb.append(String.format("Instructions: %d\n", instructions.size()));
        sb.append(String.format("Stack depth: %d\n", stack.size()));
        sb.append(String.format("Complexity: O(%d)\n", instructions.size()));
        sb.append("\nPattern Analysis:\n");
        
        // Analyze patterns
        long pushCount = instructions.stream().filter(i -> i.opcode.equals("PUSH")).count();
        long popCount = instructions.stream().filter(i -> i.opcode.equals("POP")).count();
        long callCount = instructions.stream().filter(i -> i.opcode.equals("CALL")).count();
        
        sb.append(String.format("- Stack operations: %d PUSH, %d POP\n", pushCount, popCount));
        sb.append(String.format("- Function calls: %d\n", callCount));
        
        if (pushCount != popCount) {
            sb.append("⚠ WARNING: Unbalanced stack operations!\n");
        }
        
        sb.append("\nOptimization Potential:\n");
        if (instructions.size() > 10) {
            sb.append("- High instruction count - consider optimization\n");
        }
        if (callCount > 3) {
            sb.append("- Multiple calls - consider inlining\n");
        }
        
        analysisArea.setText(sb.toString());
    }
    
    /**
     * Stop the visualizer.
     */
    public void stopVisualizer() {
        ActivityBus.getInstance().removeListener(this::onActivity);
    }
}
