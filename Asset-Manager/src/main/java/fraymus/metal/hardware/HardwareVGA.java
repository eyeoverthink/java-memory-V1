package fraymus.metal.hardware;

import javax.swing.*;
import java.awt.*;

/**
 * FRAYMUS HARDWARE VGA CONTROLLER (CRT SIMULATOR)
 * "We do not print text. We manipulate video memory voltages."
 *
 * Memory Map: 0x8000 to 0x8FA0 (80 columns x 25 rows = 2000 chars * 2 bytes/char)
 * Each cell: [ASCII CODE byte] [COLOR ATTRIBUTE byte]
 *
 * Color Attributes:
 *   0x0A = Phosphor Green
 *   0x0C = Apoptosis Red
 *   0x0F = White
 *   0x09 = Cyan
 *   0x0E = Yellow
 *   0x0D = Magenta
 *   0x00 = Default Dim Green
 *
 * This runs as an independent hardware thread at 60Hz.
 * It does not know what Java is. It blindly reads RAM[0x8000]
 * and paints the voltage to the screen.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class HardwareVGA extends JPanel {

    private final byte[] physicalRAM;

    // Hardware Constants
    public static final int VRAM_START = 0x8000;
    public static final int COLS = 80;
    public static final int ROWS = 25;
    public static final int VRAM_SIZE = COLS * ROWS * 2; // 4000 bytes
    private static final int CELL_WIDTH = 12;
    private static final int CELL_HEIGHT = 20;

    public HardwareVGA(byte[] ramReference) {
        this.physicalRAM = ramReference;

        setPreferredSize(new Dimension(COLS * CELL_WIDTH, ROWS * CELL_HEIGHT));
        setBackground(new Color(5, 5, 5)); // CRT Black

        // The 60Hz Hardware Clock (Refresh Rate)
        Timer hardwareClock = new Timer(16, e -> repaint()); // ~60 FPS
        hardwareClock.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        // Anti-aliasing off for raw pixel look
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 16));

        // Scan the Video RAM buffer and blast pixels to the screen
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                // Calculate physical memory address for this screen coordinate
                int memoryAddress = VRAM_START + ((row * COLS) + col) * 2;

                // Byte 1: The ASCII Character
                char ascii = (char) (physicalRAM[memoryAddress] & 0xFF);

                // Byte 2: The Color Attribute
                byte colorAttr = physicalRAM[memoryAddress + 1];

                if (ascii != 0) { // If voltage is not 0
                    g2d.setColor(decodeColor(colorAttr));
                    g2d.drawString(String.valueOf(ascii), col * CELL_WIDTH, (row + 1) * CELL_HEIGHT - 4);
                }
            }
        }

        // Draw CRT scanline effects (phosphor glow)
        g2d.setColor(new Color(0, 255, 0, 10));
        for (int i = 0; i < getHeight(); i += 2) {
            g2d.drawLine(0, i, getWidth(), i);
        }
    }

    private Color decodeColor(byte attribute) {
        switch (attribute) {
            case 0x0A: return new Color(0, 255, 50);    // Fraymus Phosphor Green
            case 0x0C: return new Color(255, 50, 50);   // Apoptosis Red
            case 0x0F: return Color.WHITE;               // Bright White
            case 0x09: return new Color(100, 100, 255);  // Cyan/Blue
            case 0x0E: return new Color(255, 255, 50);   // Yellow
            case 0x0D: return new Color(255, 50, 255);   // Magenta
            case 0x07: return new Color(180, 180, 180);  // Light Gray
            case 0x08: return new Color(100, 100, 100);  // Dark Gray
            default:   return new Color(0, 200, 0);      // Default Dim Green
        }
    }

    // --- Utility: Write a string to VRAM at a given row/col with color ---
    public static void writeString(byte[] ram, int row, int col, String text, byte color) {
        for (int i = 0; i < text.length(); i++) {
            int addr = VRAM_START + ((row * COLS) + col + i) * 2;
            if (addr + 1 < ram.length) {
                ram[addr] = (byte) text.charAt(i);
                ram[addr + 1] = color;
            }
        }
    }

    // --- Utility: Clear the entire VRAM ---
    public static void clearScreen(byte[] ram) {
        for (int i = VRAM_START; i < VRAM_START + VRAM_SIZE; i++) {
            ram[i] = 0;
        }
    }

    /**
     * IGNITION SEQUENCE
     * Connects the Monitor to the RAM array and powers on the display.
     */
    public static JFrame powerOn(byte[] systemRAM) {
        JFrame crt = new JFrame("FRAYMUS OS - VGA TERMINAL [0x8000]");
        HardwareVGA vga = new HardwareVGA(systemRAM);
        crt.add(vga);
        crt.pack();
        crt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        crt.setLocationRelativeTo(null);
        crt.setResizable(false);
        crt.setVisible(true);

        // Write boot banner to VRAM
        writeString(systemRAM, 0, 0, "FRAYMUS OS v1.0 - VGA TERMINAL", (byte) 0x0A);
        writeString(systemRAM, 1, 0, "80x25 TEXT MODE | 60Hz REFRESH | MMIO @ 0x8000", (byte) 0x09);
        writeString(systemRAM, 2, 0, "================================================", (byte) 0x07);
        writeString(systemRAM, 3, 0, "POKE 8000 <ascii> | POKE 8001 <color>", (byte) 0x0E);
        writeString(systemRAM, 4, 0, "The CRT reads RAM. You write RAM. No print().", (byte) 0x07);

        return crt;
    }
}
