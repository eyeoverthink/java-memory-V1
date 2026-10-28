package fraymus.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AssemblyVisualizer.java - The Sovereign JVM Bypass
 *
 * This is not a simulation. This is a living Virtual Machine.
 * It compiles REPL commands into raw bytes, flashes them to a 64KB RAM array,
 * and steps through the opcodes, performing real binary arithmetic on the registers.
 *
 * phi^75 Validation Seal: 4721424167835376.00
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class AssemblyVisualizer extends JFrame {

    private static final Color BG_COLOR = new Color(5, 5, 10);
    private static final Color TEXT_COLOR = new Color(0, 255, 100);
    private static final Color REGISTER_COLOR = new Color(255, 200, 0);
    private static final Color MEMORY_COLOR = new Color(100, 200, 255);
    private static final Color INSTRUCTION_COLOR = new Color(255, 100, 255);

    private JTextArea assemblyArea;
    private JPanel registersPanel;
    private JPanel stackPanel;
    private JTextArea analysisArea;
    private JTextField commandField;

    // =========================================================
    // THE RAW HARDWARE (NO CHEATS)
    // =========================================================
    private final byte[] RAM = new byte[65536]; // 64KB True Memory Space
    private final Map<String, Integer> registers = new LinkedHashMap<>();

    // Core Opcodes (Fraymus ISA)
    private static final byte OP_NOP      = 0x00;
    private static final byte OP_MOV_EAX  = 0x01;
    private static final byte OP_ADD_EAX  = 0x02;
    private static final byte OP_XOR_EAX  = 0x03;
    private static final byte OP_PUSH_EAX = 0x04;
    private static final byte OP_POP_EBX  = 0x05;
    private static final byte OP_SYS_CALL = 0x06;
    private static final byte OP_SUB_EAX  = 0x07;
    private static final byte OP_CMP_EAX  = 0x08;
    private static final byte OP_MOV_EBX  = 0x09;
    private static final byte OP_MUL_EAX  = 0x0A;
    private static final byte OP_HALT     = (byte) 0xFF;

    private Timer executionTimer;
    private boolean isExecuting = false;
    private long totalInstructions = 0;

    public AssemblyVisualizer() {
        setTitle("Fraymus FVM - Live Memory & Opcode Execution");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        // Initialize True 32-bit Registers
        registers.put("EAX", 0);        // Accumulator
        registers.put("EBX", 0);        // Base
        registers.put("ECX", 0);        // Counter
        registers.put("ESP", 0xFFFF);   // Stack Pointer starts at end of RAM
        registers.put("EBP", 0xFFFF);   // Base Pointer
        registers.put("EIP", 0x0200);   // Instruction Pointer starts at 0x0200
        registers.put("ZF", 0);         // Zero Flag

        setLayout(new BorderLayout(5, 5));
        add(createAssemblyPanel(), BorderLayout.CENTER);
        add(createStatePanel(), BorderLayout.EAST);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createAssemblyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("  RAW MEMORY EXECUTION (EIP STREAM)");
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(INSTRUCTION_COLOR);
        panel.add(title, BorderLayout.NORTH);

        assemblyArea = new JTextArea();
        assemblyArea.setEditable(false);
        assemblyArea.setBackground(BG_COLOR);
        assemblyArea.setForeground(TEXT_COLOR);
        assemblyArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        assemblyArea.setText("; FRAYNIX MONOLITH VISUALIZER v1.0\n");
        assemblyArea.append("; 64K RAM | 7 Registers | Fraymus ISA\n");
        assemblyArea.append("; Machine ready. Awaiting entropy...\n");
        assemblyArea.append("; Type a command below and press Enter.\n");
        assemblyArea.append("; Commands: calc <n>, phi, xor <n>, fib, asm <hex bytes>\n\n");

        JScrollPane scroll = new JScrollPane(assemblyArea);
        scroll.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(BG_COLOR);
        panel.setPreferredSize(new Dimension(280, 0));

        // Registers
        JPanel regPanel = new JPanel(new BorderLayout());
        regPanel.setBackground(BG_COLOR);
        regPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(REGISTER_COLOR, 1), "REGISTERS",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Monospaced", Font.BOLD, 12), REGISTER_COLOR));

        registersPanel = new JPanel();
        registersPanel.setLayout(new BoxLayout(registersPanel, BoxLayout.Y_AXIS));
        registersPanel.setBackground(BG_COLOR);
        regPanel.add(new JScrollPane(registersPanel), BorderLayout.CENTER);

        // Stack (Reads directly from RAM)
        JPanel stkPanel = new JPanel(new BorderLayout());
        stkPanel.setBackground(BG_COLOR);
        stkPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MEMORY_COLOR, 1), "PHYSICAL STACK (RAM)",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Monospaced", Font.BOLD, 12), MEMORY_COLOR));

        stackPanel = new JPanel();
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
        stackPanel.setBackground(BG_COLOR);
        stkPanel.add(new JScrollPane(stackPanel), BorderLayout.CENTER);

        panel.add(regPanel);
        panel.add(stkPanel);

        updateDisplays();
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBackground(BG_COLOR);
        bottom.setPreferredSize(new Dimension(0, 200));

        // Analysis area
        analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setBackground(new Color(15, 15, 20));
        analysisArea.setForeground(TEXT_COLOR);
        analysisArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        analysisArea.setText("MACHINE CODE ANALYSIS:\nAwaiting execution...\n");

        JScrollPane analysisScroll = new JScrollPane(analysisArea);
        analysisScroll.setPreferredSize(new Dimension(0, 130));
        analysisScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(INSTRUCTION_COLOR, 1), "ANALYSIS",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Monospaced", Font.BOLD, 12), INSTRUCTION_COLOR));
        bottom.add(analysisScroll, BorderLayout.CENTER);

        // Command input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(BG_COLOR);
        inputPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel prompt = new JLabel("FRAYNIX_SYS> ");
        prompt.setFont(new Font("Courier New", Font.BOLD, 14));
        prompt.setForeground(TEXT_COLOR);
        inputPanel.add(prompt, BorderLayout.WEST);

        commandField = new JTextField();
        commandField.setBackground(new Color(15, 15, 20));
        commandField.setForeground(TEXT_COLOR);
        commandField.setCaretColor(TEXT_COLOR);
        commandField.setFont(new Font("Courier New", Font.PLAIN, 14));
        commandField.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 1));
        commandField.addActionListener(e -> {
            String cmd = commandField.getText().trim();
            if (!cmd.isEmpty() && !isExecuting) {
                commandField.setText("");
                initiateHardwareExecution(cmd);
            }
        });
        inputPanel.add(commandField, BorderLayout.CENTER);

        bottom.add(inputPanel, BorderLayout.SOUTH);
        return bottom;
    }

    /**
     * Translates a high-level command into RAW BYTES and flashes them to RAM.
     */
    public void initiateHardwareExecution(String command) {
        isExecuting = true;
        assemblyArea.append("\n; ========== INGESTING: " + command.toUpperCase() + " ==========\n");

        // Reset EIP to program start
        registers.put("EIP", 0x0200);
        int ptr = 0x0200;

        String lower = command.toLowerCase().trim();

        // --- THE JIT COMPILER ---
        if (lower.startsWith("calc ")) {
            byte val = (byte) Integer.parseInt(command.split("\\s+")[1]);
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = val;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = val; // Double it
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;

        } else if (lower.equals("phi")) {
            // phi approximation: (16 + 18) XOR 0xFF = 0xDD = 221
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = (byte) 16;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = (byte) 18;
            RAM[ptr++] = OP_XOR_EAX;  RAM[ptr++] = (byte) 0xFF;
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;

        } else if (lower.startsWith("xor ")) {
            byte val = (byte) Integer.parseInt(command.split("\\s+")[1]);
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = val;
            RAM[ptr++] = OP_XOR_EAX;  RAM[ptr++] = (byte) 0xFF;
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;

        } else if (lower.equals("fib")) {
            // Fibonacci: 0, 1, 1, 2, 3, 5, 8 via register ops
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = 0;      // EAX = 0
            RAM[ptr++] = OP_PUSH_EAX;                        // Push 0
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = 1;      // EAX = 1
            RAM[ptr++] = OP_PUSH_EAX;                        // Push 1
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = 0;      // EAX = 1+0 = 1
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = 1;      // EAX = 1+1 = 2
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = 1;      // EAX = 2+1 = 3
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = 2;      // EAX = 3+2 = 5
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_ADD_EAX;  RAM[ptr++] = 3;      // EAX = 5+3 = 8
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;

        } else if (lower.startsWith("asm ")) {
            // Raw hex bytes: "asm 01 0A 02 05 06 FF"
            String[] hexBytes = command.substring(4).trim().split("\\s+");
            for (String hb : hexBytes) {
                RAM[ptr++] = (byte) Integer.parseInt(hb, 16);
            }

        } else if (lower.startsWith("mul ")) {
            // mul <a> <b>
            String[] parts = command.split("\\s+");
            byte a = (byte) Integer.parseInt(parts[1]);
            byte b = (byte) Integer.parseInt(parts[2]);
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = a;
            RAM[ptr++] = OP_MOV_EBX;  RAM[ptr++] = b;
            RAM[ptr++] = OP_MUL_EAX;  RAM[ptr++] = b;
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;

        } else {
            // Generic Command: Push length to stack, sys call
            RAM[ptr++] = OP_MOV_EAX;  RAM[ptr++] = (byte) command.length();
            RAM[ptr++] = OP_PUSH_EAX;
            RAM[ptr++] = OP_SYS_CALL;
            RAM[ptr++] = OP_HALT;
        }

        // Hex dump the compiled region
        int codeLen = ptr - 0x0200;
        StringBuilder hexLine = new StringBuilder("; Compiled " + codeLen + " bytes: ");
        for (int i = 0x0200; i < ptr; i++) {
            hexLine.append(String.format("%02X ", RAM[i] & 0xFF));
        }
        assemblyArea.append(hexLine + "\n");

        startCPUClock();
    }

    /**
     * The actual Virtual Machine execution loop.
     * Steps through RAM[EIP] visually every 350ms.
     */
    private void startCPUClock() {
        if (executionTimer != null && executionTimer.isRunning()) return;

        analysisArea.setText("CPU IGNITED. Fetching from 0x0200...\n");

        executionTimer = new Timer(350, e -> {
            int eip = registers.get("EIP");
            if (eip < 0 || eip >= RAM.length) {
                executionTimer.stop();
                isExecuting = false;
                return;
            }

            byte opcode = RAM[eip];
            String mnemonic = "";
            boolean halt = false;

            // DECODE AND EXECUTE
            switch (opcode) {
                case OP_NOP:
                    mnemonic = "NOP";
                    break;
                case OP_MOV_EAX:
                    byte v1 = RAM[++eip];
                    registers.put("EAX", (int) v1 & 0xFF);
                    mnemonic = String.format("MOV EAX, 0x%02X", v1 & 0xFF);
                    break;
                case OP_ADD_EAX:
                    byte v2 = RAM[++eip];
                    registers.put("EAX", fraymus.metal.silicon.HardwareALU.executeAdd(registers.get("EAX"), v2 & 0xFF));
                    mnemonic = String.format("ADD EAX, 0x%02X  -> EAX=%d [ALU]", v2 & 0xFF, registers.get("EAX"));
                    break;
                case OP_SUB_EAX:
                    byte v7 = RAM[++eip];
                    registers.put("EAX", fraymus.metal.silicon.HardwareALU.executeSub(registers.get("EAX"), v7 & 0xFF));
                    mnemonic = String.format("SUB EAX, 0x%02X  -> EAX=%d [ALU]", v7 & 0xFF, registers.get("EAX"));
                    break;
                case OP_XOR_EAX:
                    byte v3 = RAM[++eip];
                    registers.put("EAX", registers.get("EAX") ^ (v3 & 0xFF));
                    mnemonic = String.format("XOR EAX, 0x%02X  -> EAX=0x%02X", v3 & 0xFF, registers.get("EAX") & 0xFF);
                    break;
                case OP_MUL_EAX:
                    byte v8 = RAM[++eip];
                    registers.put("EAX", fraymus.metal.silicon.HardwareALU.executeMultiply(registers.get("EAX"), v8 & 0xFF));
                    mnemonic = String.format("MUL EAX, 0x%02X  -> EAX=%d [ALU]", v8 & 0xFF, registers.get("EAX"));
                    break;
                case OP_CMP_EAX:
                    byte v9 = RAM[++eip];
                    int cmpResult = fraymus.metal.silicon.HardwareALU.executeCompare(registers.get("EAX"), v9 & 0xFF);
                    registers.put("ZF", cmpResult == 0 ? 1 : 0);
                    mnemonic = String.format("CMP EAX, 0x%02X  -> ZF=%d [ALU]", v9 & 0xFF, registers.get("ZF"));
                    break;
                case OP_MOV_EBX:
                    byte v10 = RAM[++eip];
                    registers.put("EBX", (int) v10 & 0xFF);
                    mnemonic = String.format("MOV EBX, 0x%02X", v10 & 0xFF);
                    break;
                case OP_PUSH_EAX:
                    int esp = registers.get("ESP");
                    RAM[esp] = (byte) (registers.get("EAX") & 0xFF);
                    registers.put("ESP", esp - 1);
                    mnemonic = String.format("PUSH EAX (0x%02X -> [0x%04X])", registers.get("EAX") & 0xFF, esp);
                    break;
                case OP_POP_EBX:
                    int esp2 = registers.get("ESP") + 1;
                    registers.put("EBX", (int) RAM[esp2] & 0xFF);
                    registers.put("ESP", esp2);
                    mnemonic = String.format("POP EBX (0x%02X <- [0x%04X])", registers.get("EBX"), esp2);
                    break;
                case OP_SYS_CALL:
                    mnemonic = String.format("INT 0x80 (SYS_CALL) EAX=%d", registers.get("EAX"));
                    break;
                case OP_HALT:
                    mnemonic = "HLT (END OF EXECUTION)";
                    halt = true;
                    break;
                default:
                    mnemonic = String.format("??? ILLEGAL OPCODE 0x%02X", opcode & 0xFF);
                    halt = true;
            }

            int oldEip = registers.get("EIP");
            registers.put("EIP", eip + 1);
            totalInstructions++;

            // Log to UI
            assemblyArea.append(String.format("0x%04X:  %-35s ; Opcode: 0x%02X\n", oldEip, mnemonic, opcode & 0xFF));
            assemblyArea.setCaretPosition(assemblyArea.getDocument().getLength());
            updateDisplays();

            if (halt) {
                executionTimer.stop();
                isExecuting = false;
                int stackDepth = 0xFFFF - registers.get("ESP");
                analysisArea.setText(String.format(
                    "EXECUTION HALTED.\n" +
                    "Final EAX: %d (0x%04X)\n" +
                    "Final EBX: %d (0x%04X)\n" +
                    "Stack Depth: %d bytes used\n" +
                    "Total Instructions: %d\n" +
                    "Zero Flag: %d\n" +
                    "Ready for next command.",
                    registers.get("EAX"), registers.get("EAX"),
                    registers.get("EBX"), registers.get("EBX"),
                    stackDepth, totalInstructions,
                    registers.get("ZF")));
            }
        });

        executionTimer.start();
    }

    private void updateDisplays() {
        // Render Registers
        registersPanel.removeAll();
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            JLabel label = new JLabel(String.format(" %-4s : 0x%08X (%d)", entry.getKey(), entry.getValue(), entry.getValue()));
            label.setFont(new Font("Courier New", Font.BOLD, 13));
            label.setForeground(REGISTER_COLOR);
            registersPanel.add(label);
        }
        registersPanel.revalidate();
        registersPanel.repaint();

        // Render True RAM Stack
        stackPanel.removeAll();
        int currentEsp = registers.get("ESP");
        int shown = 0;
        for (int addr = 0xFFFF; addr > currentEsp && shown < 32; addr--, shown++) {
            JLabel label = new JLabel(String.format(" [0x%04X] : 0x%02X (%d)", addr, RAM[addr] & 0xFF, RAM[addr] & 0xFF));
            label.setFont(new Font("Courier New", Font.PLAIN, 13));
            label.setForeground(MEMORY_COLOR);
            stackPanel.add(label);
        }

        if (currentEsp == 0xFFFF) {
            JLabel empty = new JLabel(" (Stack Empty)");
            empty.setFont(new Font("Courier New", Font.ITALIC, 13));
            empty.setForeground(Color.GRAY);
            stackPanel.add(empty);
        }

        stackPanel.revalidate();
        stackPanel.repaint();
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            AssemblyVisualizer viz = new AssemblyVisualizer();
            viz.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AssemblyVisualizer viz = new AssemblyVisualizer();
            viz.setVisible(true);

            // Demo: auto-execute "calc 5" after 1 second
            new Timer(1000, e -> {
                viz.initiateHardwareExecution("calc 5");
                ((Timer) e.getSource()).stop();
            }).start();
        });
    }
}
