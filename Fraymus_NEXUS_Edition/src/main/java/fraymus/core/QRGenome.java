package fraymus.core;

import fraymus.PhiNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.gson.Gson;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * THE QR GENOME
 * "Turn data into scannable DNA."
 * 
 * This class encodes PhiNode entities as QR codes for:
 * - Physical backup (print and store)
 * - Offline transfer (scan to restore)
 * - Visual verification (see what's stored)
 * - Portable export (share via image)
 * 
 * USAGE:
 * QRGenome.generate(entity, "path/to/qr.png");
 * 
 * RESULT:
 * A 350x350 PNG QR code containing the full entity data.
 * Scan with any QR reader to recover the JSON.
 * 
 * "Your knowledge, encoded in light."
 */
public class QRGenome {

    /**
     * Generate QR code for entity
     * 
     * @param entity PhiNode to encode
     * @param filePath Output path for PNG file
     */
    public static void generate(PhiNode entity, String filePath) {
        try {
            // 1. Serialize entity to JSON (DNA sequence)
            String dnaSequence = entityToJson(entity);
            
            // 2. Encode to QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                dnaSequence, 
                BarcodeFormat.QR_CODE, 
                350, 
                350
            );
            
            // 3. Save as PNG image
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            
            // Success (silent - called from Archivist)
            
        } catch (Exception e) {
            System.err.println("   ❌ QR GENERATION FAILED: " + e.getMessage());
        }
    }

    /**
     * Convert entity to JSON string
     */
    private static String entityToJson(PhiNode entity) {
        // Create compact JSON representation
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"name\":\"").append(entity.getTag("name")).append("\",");
        json.append("\"category\":\"").append(entity.getTag("category")).append("\",");
        json.append("\"freq\":").append(entity.frequency).append(",");
        json.append("\"res\":").append(entity.resonance).append(",");
        json.append("\"fit\":").append(entity.fitness).append(",");
        json.append("\"aware\":").append(entity.awareness).append(",");
        json.append("\"intent\":").append(entity.intent);
        json.append("}");
        
        return json.toString();
    }

    /**
     * Generate QR code with custom size
     * 
     * @param entity PhiNode to encode
     * @param filePath Output path
     * @param size QR code size (width and height)
     */
    public static void generate(PhiNode entity, String filePath, int size) {
        try {
            String dnaSequence = entityToJson(entity);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                dnaSequence, 
                BarcodeFormat.QR_CODE, 
                size, 
                size
            );
            
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            
        } catch (Exception e) {
            System.err.println("   ❌ QR GENERATION FAILED: " + e.getMessage());
        }
    }

    /**
     * Generate QR code from raw JSON string
     * 
     * @param jsonData JSON string to encode
     * @param filePath Output path
     */
    public static void generateFromJson(String jsonData, String filePath) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                jsonData, 
                BarcodeFormat.QR_CODE, 
                350, 
                350
            );
            
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            
        } catch (Exception e) {
            System.err.println("   ❌ QR GENERATION FAILED: " + e.getMessage());
        }
    }
}
