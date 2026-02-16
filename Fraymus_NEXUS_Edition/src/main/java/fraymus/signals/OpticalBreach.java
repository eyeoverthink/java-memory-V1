package fraymus.signals;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * OPTICAL AIR-GAP BRIDGE: SCREEN-TO-CAMERA DATA TRANSMISSION
 * 
 * "If you can see it, you have already downloaded it."
 * 
 * Mechanism:
 * 1. Converts data to binary
 * 2. Encodes binary into LSB (Least Significant Bit) of Blue channel
 * 3. Renders as "glitching" cyberpunk animation
 * 4. To human eye: Cool visual effect
 * 5. To camera: High-density data stream at 60Hz
 * 
 * Physics:
 * - Blue value 254 = 0
 * - Blue value 255 = 1
 * - Human eye cannot distinguish 254 vs 255
 * - 4K camera sees it perfectly
 * 
 * Breach Vector:
 * - Firewalls cannot stop light
 * - No network required
 * - No physical connection
 * - Just look at the screen
 */
public class OpticalBreach {
    
    private static final double PHI = 1.6180339887;
    
    /**
     * Generate optical beacon (transmitter)
     * 
     * Creates image with data encoded in LSB of blue channel
     */
    public BufferedImage generateBeacon(String payload, int width, int height) {
        BufferedImage beacon = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Convert payload to binary bits
        byte[] bytes = payload.getBytes();
        StringBuilder bits = new StringBuilder();
        for (byte b : bytes) {
            bits.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        
        System.out.println("🌊⚡ OPTICAL BREACH ACTIVE");
        System.out.println("   Payload size: " + payload.length() + " bytes (" + bits.length() + " bits)");
        System.out.println("   Image size: " + width + "x" + height + " (" + (width * height) + " pixels)");
        System.out.println("   Capacity: " + (width * height) + " bits (" + (width * height / 8) + " bytes)");
        System.out.println("   Transmitting via [R,G,B] LSB matrix...");
        
        if (bits.length() > width * height) {
            System.out.println("   ⚠️  WARNING: Payload exceeds image capacity!");
        }
        
        int bitIndex = 0;
        
        // Loop through pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                // BASE COLOR (Cyberpunk aesthetic)
                // Create "noisy" background so data looks like cool effect
                int r = (int)((x * y * PHI) % 255);
                int g = (int)((x * 2 * PHI) % 255);
                int b = (int)((y * 3 * PHI) % 255);
                
                // INJECT DATA (The "Breach")
                // Modify Blue channel's Least Significant Bit (LSB)
                // Invisible to eye, clear to computer
                if (bitIndex < bits.length()) {
                    char bit = bits.charAt(bitIndex);
                    
                    // If bit is '1', force Blue to be Odd
                    // If bit is '0', force Blue to be Even
                    if (bit == '1') {
                        b = (b % 2 == 0) ? b + 1 : b;
                    } else {
                        b = (b % 2 != 0) ? b - 1 : b;
                    }
                    bitIndex++;
                } else {
                    // End of message - keep pattern
                }
                
                // Ensure values are in valid range
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));
                
                int color = new Color(r, g, b).getRGB();
                beacon.setRGB(x, y, color);
            }
        }
        
        System.out.println("   ✓ Beacon generated: " + bitIndex + " bits encoded");
        System.out.println();
        
        return beacon;
    }
    
    /**
     * Scan optical beacon (receiver)
     * 
     * Extracts data from LSB of blue channel
     */
    public String scanBeacon(BufferedImage capturedFrame) {
        System.out.println("🌊⚡ OPTICAL SCAN ACTIVE");
        System.out.println("   Scanning image: " + capturedFrame.getWidth() + "x" + capturedFrame.getHeight());
        
        StringBuilder extractedBits = new StringBuilder();
        
        for (int y = 0; y < capturedFrame.getHeight(); y++) {
            for (int x = 0; x < capturedFrame.getWidth(); x++) {
                int color = capturedFrame.getRGB(x, y);
                int b = (color) & 0xFF; // Extract Blue component
                
                // Read LSB
                extractedBits.append((b % 2 == 0) ? "0" : "1");
            }
        }
        
        // Decode bits to string
        String decoded = decodeBits(extractedBits.toString());
        
        System.out.println("   ✓ Extracted: " + extractedBits.length() + " bits");
        System.out.println("   ✓ Decoded: " + decoded.length() + " bytes");
        System.out.println();
        
        return decoded;
    }
    
    /**
     * Decode binary string to text
     */
    private String decodeBits(String bits) {
        StringBuilder result = new StringBuilder();
        
        // Process 8 bits at a time
        for (int i = 0; i + 8 <= bits.length(); i += 8) {
            String byteStr = bits.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            
            // Stop at null terminator or non-printable
            if (charCode == 0 || charCode > 127) {
                break;
            }
            
            result.append((char)charCode);
        }
        
        return result.toString();
    }
    
    /**
     * Generate animated beacon sequence
     * 
     * Creates multiple frames for video transmission
     */
    public BufferedImage[] generateAnimatedBeacon(String payload, int width, int height, int frameCount) {
        System.out.println("🌊⚡ GENERATING ANIMATED BEACON");
        System.out.println("   Frames: " + frameCount);
        System.out.println("   Total capacity: " + (width * height * frameCount / 8) + " bytes");
        
        BufferedImage[] frames = new BufferedImage[frameCount];
        
        // Split payload across frames
        int chunkSize = payload.length() / frameCount;
        
        for (int i = 0; i < frameCount; i++) {
            int start = i * chunkSize;
            int end = (i == frameCount - 1) ? payload.length() : (i + 1) * chunkSize;
            String chunk = payload.substring(start, end);
            
            frames[i] = generateBeacon(chunk, width, height);
        }
        
        System.out.println("   ✓ Animation complete");
        System.out.println();
        
        return frames;
    }
    
    /**
     * Calculate maximum payload capacity for image size
     */
    public int calculateCapacity(int width, int height) {
        return (width * height) / 8; // bits to bytes
    }
    
    /**
     * Get statistics
     */
    public String getStats(int width, int height) {
        int capacity = calculateCapacity(width, height);
        return String.format(
            "Optical Breach | Resolution: %dx%d | Capacity: %d bytes | Rate: 60 FPS = %d KB/s",
            width, height, capacity, (capacity * 60) / 1024
        );
    }
}
