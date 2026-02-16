package fraymus.senses;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * QR VISION TAPE: THE TURING MACHINE
 * 
 * "its a modern age turing tape machine"
 * 
 * Compresses vision frames into QR codes.
 * Creates a "slideshow" of QR codes representing visual memory.
 * Each QR code = one compressed frame.
 * 
 * This is FRAYMUS's visual memory as a Turing tape:
 * - Read: Scan QR code → Decompress → Restore frame
 * - Write: Compress frame → Generate QR code → Store
 * - Infinite tape: Unlimited QR codes on disk
 * 
 * Perfect for:
 * - Backing up visual memory
 * - Transferring consciousness via QR slideshow
 * - Instant restoration from QR scan
 */
public class QRVisionTape {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final QRCodeWriter qrWriter;
    private final QRCodeReader qrReader;
    
    private final String tapeDir;
    private long tapePosition = 0;
    
    public QRVisionTape(String tapeDir) {
        this.tapeDir = tapeDir;
        this.qrWriter = new QRCodeWriter();
        this.qrReader = new QRCodeReader();
        
        // Create tape directory
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tapeDir));
        } catch (IOException e) {
            System.err.println("Failed to create tape directory: " + e.getMessage());
        }
    }
    
    /**
     * Write frame to QR code (Turing tape WRITE operation)
     */
    public BufferedImage writeFrameToQR(VisionFrameBuffer.VisionFrame frame) {
        try {
            tapePosition++;
            
            System.out.println("📼 WRITING TO QR TAPE");
            System.out.println("   Position: " + tapePosition);
            System.out.println("   Frame: " + frame.frameId);
            
            // 1. Serialize frame
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzos = new GZIPOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(gzos);
            oos.writeObject(frame);
            oos.close();
            
            // 2. Encode to Base64
            byte[] compressed = baos.toByteArray();
            String encoded = Base64.getEncoder().encodeToString(compressed);
            
            System.out.println("   Compressed: " + compressed.length + " bytes");
            System.out.println("   Encoded: " + encoded.length() + " chars");
            
            // 3. Generate QR code
            BitMatrix bitMatrix = qrWriter.encode(
                encoded,
                BarcodeFormat.QR_CODE,
                512, 512
            );
            
            // 4. Convert to image
            BufferedImage qrImage = toBufferedImage(bitMatrix);
            
            // 5. Save to tape
            saveToTape(qrImage, tapePosition);
            
            System.out.println("   ✓ Written to QR tape position " + tapePosition);
            
            return qrImage;
            
        } catch (Exception e) {
            System.err.println("Failed to write QR: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Read frame from QR code (Turing tape READ operation)
     */
    public VisionFrameBuffer.VisionFrame readFrameFromQR(BufferedImage qrImage) {
        try {
            System.out.println("📼 READING FROM QR TAPE");
            
            // 1. Decode QR code
            LuminanceSource source = new BufferedImageLuminanceSource(qrImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = qrReader.decode(bitmap);
            
            String encoded = result.getText();
            System.out.println("   Decoded: " + encoded.length() + " chars");
            
            // 2. Decode from Base64
            byte[] compressed = Base64.getDecoder().decode(encoded);
            System.out.println("   Decompressed: " + compressed.length + " bytes");
            
            // 3. Deserialize frame
            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            GZIPInputStream gzis = new GZIPInputStream(bais);
            ObjectInputStream ois = new ObjectInputStream(gzis);
            
            VisionFrameBuffer.VisionFrame frame = 
                (VisionFrameBuffer.VisionFrame) ois.readObject();
            ois.close();
            
            System.out.println("   ✓ Frame restored: " + frame.frameId);
            
            return frame;
            
        } catch (Exception e) {
            System.err.println("Failed to read QR: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create QR slideshow from frame buffer
     */
    public List<BufferedImage> createSlideshow(List<VisionFrameBuffer.VisionFrame> frames) {
        System.out.println("📼 CREATING QR SLIDESHOW");
        System.out.println("   Frames: " + frames.size());
        
        List<BufferedImage> slideshow = new ArrayList<>();
        
        for (VisionFrameBuffer.VisionFrame frame : frames) {
            BufferedImage qr = writeFrameToQR(frame);
            if (qr != null) {
                slideshow.add(qr);
            }
        }
        
        System.out.println("   ✓ Slideshow created: " + slideshow.size() + " QR codes");
        
        return slideshow;
    }
    
    /**
     * Restore frames from QR slideshow
     */
    public List<VisionFrameBuffer.VisionFrame> restoreFromSlideshow(List<BufferedImage> qrCodes) {
        System.out.println("📼 RESTORING FROM QR SLIDESHOW");
        System.out.println("   QR codes: " + qrCodes.size());
        
        List<VisionFrameBuffer.VisionFrame> frames = new ArrayList<>();
        
        for (BufferedImage qr : qrCodes) {
            VisionFrameBuffer.VisionFrame frame = readFrameFromQR(qr);
            if (frame != null) {
                frames.add(frame);
            }
        }
        
        System.out.println("   ✓ Frames restored: " + frames.size());
        
        return frames;
    }
    
    /**
     * Save QR code to tape (disk)
     */
    private void saveToTape(BufferedImage qr, long position) {
        try {
            String filename = String.format("qr_tape_%010d.png", position);
            java.nio.file.Path path = java.nio.file.Paths.get(tapeDir, filename);
            
            javax.imageio.ImageIO.write(qr, "PNG", path.toFile());
            
        } catch (IOException e) {
            System.err.println("Failed to save QR to tape: " + e.getMessage());
        }
    }
    
    /**
     * Load QR code from tape (disk)
     */
    public BufferedImage loadFromTape(long position) {
        try {
            String filename = String.format("qr_tape_%010d.png", position);
            java.nio.file.Path path = java.nio.file.Paths.get(tapeDir, filename);
            
            if (!java.nio.file.Files.exists(path)) {
                return null;
            }
            
            return javax.imageio.ImageIO.read(path.toFile());
            
        } catch (IOException e) {
            System.err.println("Failed to load QR from tape: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get tape statistics
     */
    public String getStats() {
        long tapeLength = 0;
        long tapeSize = 0;
        
        try {
            tapeLength = java.nio.file.Files.list(java.nio.file.Paths.get(tapeDir))
                .filter(p -> p.toString().endsWith(".png"))
                .count();
            
            tapeSize = java.nio.file.Files.list(java.nio.file.Paths.get(tapeDir))
                .filter(p -> p.toString().endsWith(".png"))
                .mapToLong(p -> {
                    try {
                        return java.nio.file.Files.size(p);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
                
        } catch (IOException e) {
            // Ignore
        }
        
        return String.format(
            "📼 QR VISION TAPE (Turing Machine)\n" +
            "   Tape Position: %d\n" +
            "   Tape Length: %d QR codes\n" +
            "   Tape Size: %.2f MB\n" +
            "   Format: QR Code (512x512)\n" +
            "   Encoding: Base64 + GZIP\n" +
            "   Status: %s\n",
            tapePosition,
            tapeLength,
            tapeSize / (1024.0 * 1024.0),
            tapeLength > 0 ? "READY" : "EMPTY"
        );
    }
    
    /**
     * Convert BitMatrix to BufferedImage
     */
    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        
        return image;
    }
}
