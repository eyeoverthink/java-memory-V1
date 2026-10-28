package fraymus.vision;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Avatar Generator - Cartoon Stylization with Fingerprint Embedding
 * 
 * This system generates stylized cartoon avatars from facial landmarks
 * and embeds the cryptographic fingerprint via steganography for identity ownership.
 * 
 * Features:
 * - Cartoon stylization (artistic style transfer)
 * - Preserve biometric ratios (eye separation, symmetry)
 * - Embed fingerprint via LSB steganography
 * - Generate SVG (vector) and PNG (raster) output
 * - Avatar storage with biometric profile
 */
public class AvatarGenerator {
    
    private final int avatarWidth;
    private final int avatarHeight;
    private final String outputDirectory;
    
    public AvatarGenerator() {
        this(512, 512, "build/avatars");
    }
    
    public AvatarGenerator(int width, int height, String outputDirectory) {
        this.avatarWidth = width;
        this.avatarHeight = height;
        this.outputDirectory = outputDirectory;
        
        // Ensure output directory exists
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Generate cartoon avatar from facial landmarks
     * 
     * @param landmarks Facial landmarks from FacialFingerprintExtractor
     * @param fingerprint Cryptographic fingerprint to embed
     * @return AvatarResult containing generated avatar paths
     */
    public AvatarResult generateAvatar(
        FacialFingerprintExtractor.FacialLandmarks landmarks,
        String fingerprint
    ) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              AVATAR GENERATION - CARTOON STYLIZATION                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Step 1: Generate cartoon avatar image
        System.out.println("[1] CARTOON STYLIZATION");
        BufferedImage avatarImage = generateCartoonImage(landmarks);
        System.out.println("    Generated cartoon image: " + avatarWidth + "x" + avatarHeight);
        System.out.println();
        
        // Step 2: Embed fingerprint via steganography
        System.out.println("[2] FINGERPRINT EMBEDDING (STEGANOGRAPHY)");
        BufferedImage avatarWithFp = embedFingerprint(avatarImage, fingerprint);
        System.out.println("    Embedded fingerprint: " + fingerprint.substring(0, 16) + "...");
        System.out.println();
        
        // Step 3: Generate SVG avatar
        System.out.println("[3] SVG GENERATION");
        String svgPath = generateSVG(landmarks, fingerprint);
        System.out.println("    Generated SVG: " + svgPath);
        System.out.println();
        
        // Step 4: Save PNG avatar
        System.out.println("[4] PNG EXPORT");
        String pngPath = savePNG(avatarWithFp, fingerprint);
        System.out.println("    Saved PNG: " + pngPath);
        System.out.println();
        
        // Step 5: Extract fingerprint for verification
        System.out.println("[5] FINGERPRINT VERIFICATION");
        String extractedFp = extractFingerprint(avatarWithFp);
        System.out.println("    Extracted fingerprint: " + extractedFp.substring(0, 16) + "...");
        System.out.println("    Match: " + extractedFp.equals(fingerprint));
        System.out.println();
        
        // Build result
        AvatarResult result = new AvatarResult();
        result.pngPath = pngPath;
        result.svgPath = svgPath;
        result.fingerprint = fingerprint;
        result.verified = extractedFp.equals(fingerprint);
        
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              AVATAR GENERATION COMPLETE                             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        return result;
    }
    
    /**
     * Generate cartoon stylized image from landmarks
     */
    private BufferedImage generateCartoonImage(FacialFingerprintExtractor.FacialLandmarks landmarks) {
        BufferedImage image = new BufferedImage(avatarWidth, avatarHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background (skin tone)
        g2d.setColor(new Color(255, 220, 177)); // Light skin tone
        g2d.fillRect(0, 0, avatarWidth, avatarHeight);
        
        // Scale landmarks to avatar dimensions
        double scaleX = avatarWidth / landmarks.faceWidth;
        double scaleY = avatarHeight / landmarks.faceHeight;
        double scale = Math.min(scaleX, scaleY);
        
        int offsetX = (int) ((avatarWidth - landmarks.faceWidth * scale) / 2);
        int offsetY = (int) ((avatarHeight - landmarks.faceHeight * scale) / 2);
        
        // Draw facial contour (jawline)
        g2d.setColor(new Color(200, 150, 100));
        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Draw simplified face shape (cartoon style)
        int faceX = offsetX + (int) (landmarks.faceWidth * scale * 0.1);
        int faceY = offsetY + (int) (landmarks.faceHeight * scale * 0.1);
        int faceW = (int) (landmarks.faceWidth * scale * 0.8);
        int faceH = (int) (landmarks.faceHeight * scale * 0.8);
        
        g2d.drawOval(faceX, faceY, faceW, faceH);
        
        // Draw eyes (cartoon style)
        int eyeSep = (int) (landmarks.eyeSeparation * scale);
        int eyeY = offsetY + (int) (landmarks.faceHeight * scale * 0.45);
        int eyeSize = (int) (faceW * 0.15);
        
        // Left eye
        g2d.setColor(Color.WHITE);
        g2d.fillOval(faceX + faceW/2 - eyeSep/2 - eyeSize, eyeY - eyeSize/2, eyeSize, eyeSize);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(faceX + faceW/2 - eyeSep/2 - eyeSize/2, eyeY - eyeSize/4, eyeSize/2, eyeSize/2);
        
        // Right eye
        g2d.setColor(Color.WHITE);
        g2d.fillOval(faceX + faceW/2 + eyeSep/2 - eyeSize, eyeY - eyeSize/2, eyeSize, eyeSize);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(faceX + faceW/2 + eyeSep/2 - eyeSize/2, eyeY - eyeSize/4, eyeSize/2, eyeSize/2);
        
        // Draw nose (cartoon style)
        g2d.setColor(new Color(180, 130, 80));
        int noseX = faceX + faceW/2;
        int noseY = offsetY + (int) (landmarks.faceHeight * scale * 0.55);
        int noseSize = (int) (faceW * 0.1);
        g2d.fillOval(noseX - noseSize/2, noseY - noseSize/2, noseSize, noseSize);
        
        // Draw mouth (cartoon smile)
        g2d.setColor(new Color(150, 50, 50));
        int mouthY = offsetY + (int) (landmarks.faceHeight * scale * 0.75);
        int mouthWidth = (int) (faceW * 0.4);
        g2d.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawArc(faceX + faceW/2 - mouthWidth/2, mouthY - mouthWidth/4, mouthWidth, mouthWidth/2, 180, 180);
        
        // Draw eyebrows (cartoon style)
        g2d.setColor(new Color(80, 50, 30));
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int browY = eyeY - eyeSize;
        
        // Left eyebrow
        g2d.drawLine(faceX + faceW/2 - eyeSep/2 - eyeSize, browY, 
                   faceX + faceW/2 - eyeSep/2 + eyeSize/2, browY - 5);
        
        // Right eyebrow
        g2d.drawLine(faceX + faceW/2 + eyeSep/2 - eyeSize/2, browY - 5, 
                   faceX + faceW/2 + eyeSep/2 + eyeSize, browY);
        
        g2d.dispose();
        
        return image;
    }
    
    /**
     * Embed fingerprint into image using LSB steganography
     */
    private BufferedImage embedFingerprint(BufferedImage image, String fingerprint) {
        // Convert fingerprint to bits
        String fpHex = fingerprint;
        StringBuilder fpBits = new StringBuilder();
        for (int i = 0; i < fpHex.length(); i++) {
            int value = Integer.parseInt(fpHex.substring(i, i + 1), 16);
            String bits = String.format("%4s", Integer.toBinaryString(value)).replace(' ', '0');
            fpBits.append(bits);
        }
        
        // Pad to full image capacity
        int totalPixels = image.getWidth() * image.getHeight();
        int bitsNeeded = fpBits.length();
        
        if (bitsNeeded > totalPixels) {
            System.err.println("Fingerprint too large for image");
            return image;
        }
        
        // Embed into LSB of every 3rd pixel (RGB channels)
        BufferedImage result = new BufferedImage(
            image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB
        );
        
        int bitIndex = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                
                if (bitIndex < fpBits.length() && (x + y * image.getWidth()) % 3 == 0) {
                    // Get bit to embed
                    char bit = fpBits.charAt(bitIndex);
                    bitIndex++;
                    
                    // Clear LSB of red channel and embed bit
                    int red = (rgb >> 16) & 0xFF;
                    red = (red & ~1) | (bit - '0');
                    
                    // Reconstruct RGB
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    
                    rgb = (red << 16) | (green << 8) | blue;
                }
                
                result.setRGB(x, y, rgb);
            }
        }
        
        return result;
    }
    
    /**
     * Extract fingerprint from image using LSB steganography
     */
    private String extractFingerprint(BufferedImage image) {
        StringBuilder fpBits = new StringBuilder();
        
        // Extract from LSB of every 3rd pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((x + y * image.getWidth()) % 3 == 0) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int bit = red & 1;
                    fpBits.append(bit);
                    
                    // We need 256 bits (64 hex chars) for SHA-256
                    if (fpBits.length() >= 256) {
                        break;
                    }
                }
            }
            if (fpBits.length() >= 256) {
                break;
            }
        }
        
        // Convert bits to hex
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < fpBits.length(); i += 4) {
            if (i + 4 <= fpBits.length()) {
                String nibble = fpBits.substring(i, i + 4);
                int value = Integer.parseInt(nibble, 2);
                hex.append(Integer.toHexString(value));
            }
        }
        
        return hex.toString();
    }
    
    /**
     * Generate SVG avatar from landmarks
     */
    private String generateSVG(FacialFingerprintExtractor.FacialLandmarks landmarks, String fingerprint) {
        String filename = fingerprint.substring(0, 16) + ".svg";
        String path = outputDirectory + "/" + filename;
        
        StringBuilder svg = new StringBuilder();
        svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        svg.append("<svg width=\"").append(avatarWidth).append("\" height=\"").append(avatarHeight)
           .append("\" xmlns=\"http://www.w3.org/2000/svg\">\n");
        
        // Background
        svg.append("  <rect width=\"100%\" height=\"100%\" fill=\"#FFDCB1\"/>\n");
        
        // Face shape
        double faceX = avatarWidth * 0.1;
        double faceY = avatarHeight * 0.1;
        double faceW = avatarWidth * 0.8;
        double faceH = avatarHeight * 0.8;
        svg.append("  <ellipse cx=\"").append(avatarWidth/2).append("\" cy=\"").append(avatarHeight/2)
           .append("\" rx=\"").append(faceW/2).append("\" ry=\"").append(faceH/2)
           .append("\" fill=\"#C89664\" stroke=\"#000\" stroke-width=\"3\"/>\n");
        
        // Eyes
        double eyeSep = landmarks.eyeSeparation * (avatarWidth / landmarks.faceWidth);
        double eyeY = avatarHeight * 0.45;
        double eyeSize = faceW * 0.15;
        
        // Left eye
        svg.append("  <circle cx=\"").append(avatarWidth/2 - eyeSep/2).append("\" cy=\"").append(eyeY)
           .append("\" r=\"").append(eyeSize/2).append("\" fill=\"white\" stroke=\"black\" stroke-width=\"2\"/>\n");
        svg.append("  <circle cx=\"").append(avatarWidth/2 - eyeSep/2).append("\" cy=\"").append(eyeY)
           .append("\" r=\"").append(eyeSize/4).append("\" fill=\"black\"/>\n");
        
        // Right eye
        svg.append("  <circle cx=\"").append(avatarWidth/2 + eyeSep/2).append("\" cy=\"").append(eyeY)
           .append("\" r=\"").append(eyeSize/2).append("\" fill=\"white\" stroke=\"black\" stroke-width=\"2\"/>\n");
        svg.append("  <circle cx=\"").append(avatarWidth/2 + eyeSep/2).append("\" cy=\"").append(eyeY)
           .append("\" r=\"").append(eyeSize/4).append("\" fill=\"black\"/>\n");
        
        // Nose
        double noseX = avatarWidth / 2;
        double noseY = avatarHeight * 0.55;
        double noseSize = faceW * 0.1;
        svg.append("  <ellipse cx=\"").append(noseX).append("\" cy=\"").append(noseY)
           .append("\" rx=\"").append(noseSize/2).append("\" ry=\"").append(noseSize/2)
           .append("\" fill=\"#B48250\"/>\n");
        
        // Mouth
        double mouthY = avatarHeight * 0.75;
        double mouthWidth = faceW * 0.4;
        svg.append("  <path d=\"M ").append(avatarWidth/2 - mouthWidth/2).append(" ").append(mouthY)
           .append(" Q ").append(avatarWidth/2).append(" ").append(mouthY + mouthWidth/4)
           .append(" ").append(avatarWidth/2 + mouthWidth/2).append(" ").append(mouthY)
           .append("\" fill=\"none\" stroke=\"#963232\" stroke-width=\"4\" stroke-linecap=\"round\"/>\n");
        
        // Embed fingerprint in metadata
        svg.append("  <metadata>\n");
        svg.append("    <fingerprint>").append(fingerprint).append("</fingerprint>\n");
        svg.append("    <eye_separation>").append(landmarks.eyeSeparation).append("</eye_separation>\n");
        svg.append("    <face_width>").append(landmarks.faceWidth).append("</face_width>\n");
        svg.append("    <face_height>").append(landmarks.faceHeight).append("</face_height>\n");
        svg.append("  </metadata>\n");
        
        svg.append("</svg>");
        
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(svg.toString());
        } catch (IOException e) {
            System.err.println("Failed to save SVG: " + e.getMessage());
        }
        
        return path;
    }
    
    /**
     * Save PNG avatar
     */
    private String savePNG(BufferedImage image, String fingerprint) {
        String filename = fingerprint.substring(0, 16) + ".png";
        String path = outputDirectory + "/" + filename;
        
        try {
            ImageIO.write(image, "PNG", new File(path));
        } catch (IOException e) {
            System.err.println("Failed to save PNG: " + e.getMessage());
        }
        
        return path;
    }
    
    /**
     * Result class containing generated avatar paths
     */
    public static class AvatarResult {
        public String pngPath;
        public String svgPath;
        public String fingerprint;
        public boolean verified;
    }
}
