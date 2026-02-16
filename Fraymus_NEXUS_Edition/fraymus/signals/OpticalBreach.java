package fraymus.signals;

import fraymus.quantum.core.PhiQuantumConstants;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * THE OPTICAL AIR-GAP BRIDGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "If you can see it, you have already downloaded it."
 * 
 * Converts data into a "Living Image" that broadcasts through light.
 * - To the human eye: A glitching cyberpunk animation
 * - To a camera: A file download at the speed of light (60Hz refresh rate)
 * 
 * The Mechanism:
 * - Blue value 254 = 0
 * - Blue value 255 = 1
 * - Your eye cannot see the difference. A 4K camera can.
 * 
 * Firewalls cannot stop light.
 */
public class OpticalBreach {

    private static final double PHI = PhiQuantumConstants.PHI;
    private static final double PHI_INV = PhiQuantumConstants.PHI_INVERSE;

    // Frame configuration
    private int frameWidth = 256;
    private int frameHeight = 256;
    private int framesPerSecond = 30;
    
    // Encoding modes
    public enum EncodingMode {
        LSB_BLUE,       // Least Significant Bit in Blue channel
        LSB_ALL,        // LSB in all RGB channels (3x bandwidth)
        CHROMATIC,      // Color phase encoding
        PHI_RESONANCE   // Golden ratio frequency modulation
    }
    
    private EncodingMode mode = EncodingMode.LSB_BLUE;

    // ═══════════════════════════════════════════════════════════════════
    // 1. THE ENCODER (The Transmitter)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Generate a single beacon frame containing the payload
     */
    public BufferedImage generateBeacon(String payload, int width, int height) {
        BufferedImage beacon = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Convert Payload to Binary Bits
        byte[] bytes = payload.getBytes();
        StringBuilder bits = new StringBuilder();
        
        // Add sync header (helps receiver lock on)
        bits.append("10101010"); // Sync pattern
        bits.append(String.format("%16s", Integer.toBinaryString(payload.length())).replace(' ', '0')); // Length
        
        for (byte b : bytes) {
            bits.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        
        // Add checksum
        int checksum = 0;
        for (byte b : bytes) checksum ^= b;
        bits.append(String.format("%8s", Integer.toBinaryString(checksum & 0xFF)).replace(' ', '0'));

        System.out.println("═══ OPTICAL BREACH ACTIVE ═══");
        System.out.println("Payload Size: " + bits.length() + " bits");
        System.out.println("Capacity: " + (width * height) + " bits");
        System.out.println("Mode: " + mode);
        System.out.println("Transmitting via [R,G,B] Matrix...");

        int bitIndex = 0;
        
        // Loop through pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                // BASE COLOR (Cyberpunk aesthetic - looks like cool art)
                int r = (int)((x * y * PHI) % 255);
                int g = (int)((x * 2 + y * PHI_INV) % 255);
                int b = (int)((y * 3 * PHI) % 255);
                
                switch (mode) {
                    case LSB_BLUE:
                        if (bitIndex < bits.length()) {
                            char bit = bits.charAt(bitIndex++);
                            b = encodeLSB(b, bit);
                        }
                        break;
                        
                    case LSB_ALL:
                        // 3 bits per pixel
                        if (bitIndex < bits.length()) {
                            r = encodeLSB(r, bits.charAt(bitIndex++));
                        }
                        if (bitIndex < bits.length()) {
                            g = encodeLSB(g, bits.charAt(bitIndex++));
                        }
                        if (bitIndex < bits.length()) {
                            b = encodeLSB(b, bits.charAt(bitIndex++));
                        }
                        break;
                        
                    case CHROMATIC:
                        // Use color phase for more robust encoding
                        if (bitIndex < bits.length()) {
                            int phase = (bits.charAt(bitIndex++) == '1') ? 128 : 0;
                            r = (r + phase) % 256;
                        }
                        break;
                        
                    case PHI_RESONANCE:
                        // Golden ratio frequency modulation
                        if (bitIndex < bits.length()) {
                            double phiMod = (bits.charAt(bitIndex++) == '1') ? PHI : PHI_INV;
                            b = (int)((b * phiMod) % 256);
                        }
                        break;
                }
                
                int color = new Color(
                    Math.max(0, Math.min(255, r)),
                    Math.max(0, Math.min(255, g)),
                    Math.max(0, Math.min(255, b))
                ).getRGB();
                beacon.setRGB(x, y, color);
            }
        }
        
        System.out.println("Bits encoded: " + Math.min(bitIndex, bits.length()) + "/" + bits.length());
        return beacon;
    }

    /**
     * Generate animated beacon (multiple frames for large payloads)
     */
    public List<BufferedImage> generateAnimatedBeacon(byte[] payload, int width, int height) {
        List<BufferedImage> frames = new ArrayList<>();
        
        int bitsPerFrame = width * height;
        if (mode == EncodingMode.LSB_ALL) bitsPerFrame *= 3;
        
        int bytesPerFrame = bitsPerFrame / 8;
        int totalFrames = (int) Math.ceil((double) payload.length / bytesPerFrame);
        
        System.out.println("═══ GENERATING ANIMATED BEACON ═══");
        System.out.println("Total payload: " + payload.length + " bytes");
        System.out.println("Frames needed: " + totalFrames);
        System.out.println("Frame rate: " + framesPerSecond + " FPS");
        System.out.println("Transfer time: " + String.format("%.2f", (double)totalFrames / framesPerSecond) + " seconds");
        
        for (int frame = 0; frame < totalFrames; frame++) {
            int start = frame * bytesPerFrame;
            int end = Math.min(start + bytesPerFrame, payload.length);
            
            byte[] chunk = new byte[end - start];
            System.arraycopy(payload, start, chunk, 0, chunk.length);
            
            // Add frame header
            String frameHeader = String.format("F%04d:", frame);
            String frameData = frameHeader + new String(chunk);
            
            frames.add(generateBeacon(frameData, width, height));
        }
        
        return frames;
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. THE SCANNER (The Receiver)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Scan a captured frame and extract the hidden payload
     */
    public String scanBeacon(BufferedImage capturedFrame) {
        StringBuilder extractedBits = new StringBuilder();
        
        for (int y = 0; y < capturedFrame.getHeight(); y++) {
            for (int x = 0; x < capturedFrame.getWidth(); x++) {
                int color = capturedFrame.getRGB(x, y);
                
                switch (mode) {
                    case LSB_BLUE:
                        int b = color & 0xFF;
                        extractedBits.append((b % 2 == 0) ? "0" : "1");
                        break;
                        
                    case LSB_ALL:
                        int r = (color >> 16) & 0xFF;
                        int g = (color >> 8) & 0xFF;
                        int b2 = color & 0xFF;
                        extractedBits.append((r % 2 == 0) ? "0" : "1");
                        extractedBits.append((g % 2 == 0) ? "0" : "1");
                        extractedBits.append((b2 % 2 == 0) ? "0" : "1");
                        break;
                        
                    case CHROMATIC:
                        int r2 = (color >> 16) & 0xFF;
                        extractedBits.append((r2 > 128) ? "1" : "0");
                        break;
                        
                    case PHI_RESONANCE:
                        int b3 = color & 0xFF;
                        // Detect phi modulation
                        double ratio = (double) b3 / 128.0;
                        extractedBits.append((Math.abs(ratio - PHI) < Math.abs(ratio - PHI_INV)) ? "1" : "0");
                        break;
                }
            }
        }
        
        return decodeBits(extractedBits.toString());
    }

    /**
     * Decode binary string to text
     */
    private String decodeBits(String bits) {
        // Look for sync pattern
        int syncIndex = bits.indexOf("10101010");
        if (syncIndex == -1) {
            return "[NO_SYNC_FOUND]";
        }
        
        int headerStart = syncIndex + 8;
        if (headerStart + 16 > bits.length()) {
            return "[TRUNCATED_HEADER]";
        }
        
        // Extract length
        String lengthBits = bits.substring(headerStart, headerStart + 16);
        int payloadLength = Integer.parseInt(lengthBits, 2);
        
        int dataStart = headerStart + 16;
        int dataEnd = dataStart + (payloadLength * 8);
        
        if (dataEnd > bits.length()) {
            return "[TRUNCATED_DATA]";
        }
        
        // Extract payload
        StringBuilder decoded = new StringBuilder();
        for (int i = dataStart; i < dataEnd; i += 8) {
            if (i + 8 <= bits.length()) {
                String byteBits = bits.substring(i, i + 8);
                int charCode = Integer.parseInt(byteBits, 2);
                decoded.append((char) charCode);
            }
        }
        
        // Verify checksum
        int checksumStart = dataEnd;
        if (checksumStart + 8 <= bits.length()) {
            String checksumBits = bits.substring(checksumStart, checksumStart + 8);
            int receivedChecksum = Integer.parseInt(checksumBits, 2);
            
            int calculatedChecksum = 0;
            for (char c : decoded.toString().toCharArray()) {
                calculatedChecksum ^= c;
            }
            
            if (receivedChecksum != calculatedChecksum) {
                return "[CHECKSUM_MISMATCH] " + decoded.toString();
            }
        }
        
        return decoded.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    private int encodeLSB(int value, char bit) {
        if (bit == '1') {
            return (value % 2 == 0) ? value + 1 : value;
        } else {
            return (value % 2 != 0) ? value - 1 : value;
        }
    }

    public void setMode(EncodingMode mode) {
        this.mode = mode;
    }

    public EncodingMode getMode() {
        return mode;
    }

    public void setFrameRate(int fps) {
        this.framesPerSecond = fps;
    }

    /**
     * Calculate bandwidth
     */
    public String getBandwidthInfo(int width, int height) {
        int bitsPerFrame = width * height;
        if (mode == EncodingMode.LSB_ALL) bitsPerFrame *= 3;
        
        double bytesPerSecond = (bitsPerFrame / 8.0) * framesPerSecond;
        
        return String.format(
            "═══ OPTICAL BANDWIDTH ═══%n" +
            "Resolution: %dx%d%n" +
            "Mode: %s%n" +
            "Bits/Frame: %d%n" +
            "Frame Rate: %d FPS%n" +
            "Bandwidth: %.2f KB/s%n" +
            "Equivalent: %.2f kbps",
            width, height, mode, bitsPerFrame, framesPerSecond,
            bytesPerSecond / 1024, (bytesPerSecond * 8) / 1000
        );
    }

    /**
     * Test the encoder/decoder
     */
    public static void main(String[] args) {
        OpticalBreach breach = new OpticalBreach();
        
        String testMessage = "FRAYMUS: The Casual Breach is ACTIVE. φ = 1.618";
        System.out.println("Original: " + testMessage);
        
        BufferedImage beacon = breach.generateBeacon(testMessage, 256, 256);
        String decoded = breach.scanBeacon(beacon);
        
        System.out.println("Decoded: " + decoded);
        System.out.println("Match: " + testMessage.equals(decoded));
        System.out.println();
        System.out.println(breach.getBandwidthInfo(256, 256));
    }
}
