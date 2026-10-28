package fraymus.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

/**
 * FRAYNIX VIRTUAL MACHINE MONITOR (VMM)
 * "The window to the new universe."
 *
 * 1. Desktop GUI interface (Escaping the command line).
 * 2. Hexadecimal DNA Visualizer (To read raw machine code).
 * 3. Hardware Emulator Launcher (QEMU).
 *
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class FrayVMM extends JFrame {

    private JTextArea hexDisplay;
    private JLabel statusLabel;
    private File currentImage;

    private static final Color BG_COLOR = new Color(5, 5, 8);
    private static final Color CYAN = new Color(0, 243, 255);
    private static final Color GOLD = new Color(255, 176, 0);

    public FrayVMM() {
        setTitle("FRAYNIX OS // VIRTUAL MACHINE MONITOR");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- TOP PANEL (Controls) ---
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnLoad = createCyberButton("1. IMPORT OS IMAGE (.img / .iso / .bin)");
        JButton btnBoot = createCyberButton("2. BOOT FRAYNIX (Requires QEMU)");
        btnBoot.setForeground(GOLD);

        topPanel.add(btnLoad);
        topPanel.add(btnBoot);

        // --- CENTER PANEL (Hex Visualizer) ---
        hexDisplay = new JTextArea();
        hexDisplay.setBackground(new Color(10, 10, 15));
        hexDisplay.setForeground(CYAN);
        hexDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hexDisplay.setEditable(false);
        hexDisplay.setCaretColor(CYAN);
        hexDisplay.setText("\n\n\n\n\n                             AWAITING BOOT IMAGE...");

        JScrollPane scrollPane = new JScrollPane(hexDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(CYAN, 1));
        scrollPane.getViewport().setBackground(new Color(10, 10, 15));

        // --- BOTTOM PANEL (Status) ---
        statusLabel = new JLabel("STATUS: OFFLINE");
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(BG_COLOR);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // --- EVENT LISTENERS ---
        btnLoad.addActionListener(e -> loadFile());
        btnBoot.addActionListener(e -> bootSystem());
    }

    private JButton createCyberButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(20, 20, 30));
        btn.setForeground(CYAN);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(CYAN, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "OS Images (*.img, *.iso, *.bin)", "img", "iso", "bin"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentImage = fileChooser.getSelectedFile();
            statusLabel.setText("STATUS: IMAGE LOADED [" + currentImage.getName() +
                "] (" + currentImage.length() + " bytes)");
            displayHex(currentImage);
        }
    }

    private void displayHex(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            StringBuilder hex = new StringBuilder();
            StringBuilder ascii = new StringBuilder();

            hex.append("PHYSICAL MEMORY DUMP (MASTER BOOT RECORD):\n");
            hex.append("FILE: ").append(file.getName()).append(" (").append(bytes.length).append(" bytes)\n");
            hex.append("---------------------------------------------------------------------------\n");
            hex.append("OFFSET   00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F  | ASCII\n");
            hex.append("---------------------------------------------------------------------------\n");

            // Read up to the first 512 bytes (Standard boot sector size)
            for (int i = 0; i < Math.min(bytes.length, 512); i++) {
                if (i % 16 == 0) {
                    if (i > 0) hex.append(" | ").append(ascii).append("\n");
                    hex.append(String.format("%04X     ", i));
                    ascii.setLength(0);
                }

                // Insert extra space at byte 8 for readability
                if (i % 16 == 8) hex.append(" ");

                hex.append(String.format("%02X ", bytes[i] & 0xFF));

                char c = (char) (bytes[i] & 0xFF);
                if (c >= 32 && c <= 126) ascii.append(c);
                else ascii.append('.');
            }

            // Pad the last line if needed
            int remaining = bytes.length % 16;
            if (remaining > 0) {
                for (int i = remaining; i < 16; i++) {
                    hex.append("   ");
                    if (i == 8) hex.append(" ");
                }
            }
            hex.append(" | ").append(ascii).append("\n");

            hex.append("---------------------------------------------------------------------------\n");
            hex.append("TOTAL: ").append(Math.min(bytes.length, 512)).append(" / ").append(bytes.length).append(" bytes displayed\n");

            // Boot signature check
            if (bytes.length >= 512) {
                int sig = ((bytes[511] & 0xFF) << 8) | (bytes[510] & 0xFF);
                if (sig == 0xAA55) {
                    hex.append("\n>>> BOOT SIGNATURE DETECTED: 0x55AA (VALID MBR)\n");
                    hex.append(">>> THIS IS A BOOTABLE OPERATING SYSTEM IMAGE.\n");
                } else {
                    hex.append(String.format("\n>>> BOOT SIGNATURE: 0x%04X (non-standard)\n", sig));
                }
            }

            hexDisplay.setText(hex.toString());
            hexDisplay.setCaretPosition(0);

        } catch (IOException ex) {
            hexDisplay.setText("Error reading file: " + ex.getMessage());
        }
    }

    private void bootSystem() {
        if (currentImage == null) {
            JOptionPane.showMessageDialog(this,
                "Import an OS image first.", "No Image", JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("STATUS: IGNITING VIRTUAL MACHINE...");

        try {
            String[] command = {
                "qemu-system-x86_64",
                "-drive", "format=raw,file=" + currentImage.getAbsolutePath()
            };
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
            statusLabel.setText("STATUS: FRAYNIX IS RUNNING ON BARE METAL.");

        } catch (IOException ex) {
            statusLabel.setText("STATUS: EMULATOR NOT FOUND.");
            hexDisplay.setText(hexDisplay.getText() +
                "\n\n>>> COULD NOT FIND QEMU EMULATOR." +
                "\n>>> Install QEMU (qemu.org), add it to your PATH, and click Boot again." +
                "\n>>> Alternatively, use Rufus to burn this .img to a USB and boot a physical machine.");
        }
    }

    /**
     * Load and display a file directly (for programmatic access from FraynixBoot).
     */
    public void loadImageDirect(File imageFile) {
        currentImage = imageFile;
        statusLabel.setText("STATUS: IMAGE LOADED [" + imageFile.getName() +
            "] (" + imageFile.length() + " bytes)");
        displayHex(imageFile);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { /* fallback */ }

        SwingUtilities.invokeLater(() -> new FrayVMM().setVisible(true));
    }
}
