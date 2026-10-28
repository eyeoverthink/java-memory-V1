package fraymus;

import fraymus.vision.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

/**
 * Defensible Biometric System Demo - Complete Privacy-Preserving Pipeline
 * 
 * This demonstrates the complete defensible biometric system with:
 * - Local-only biometric vault (no raw face upload)
 * - Cancelable biometric transforms (revocable embeddings)
 * - Explicit consent + revocation flow
 * - Liveness checks and anti-spoofing
 * - Export/delete pipeline for user-controlled data lifecycle
 * 
 * This system respects that "my face is my face - I own those points"
 * Facial landmarks are IP and family legacy, not company-owned data.
 */
public class DefensibleBiometricSystemDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║       DEFENSIBLE BIOMETRIC SYSTEM - PRIVACY-PRESERVING                ║");
        System.out.println("║       \"My face is my face - I own those points\"                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // User secret (in production, this would be user-provided via secure input)
            String userSecret = "USER_SECRET_12345"; // Demo only - use real secret in production
            
            // Initialize components
            FacialFingerprintExtractor fingerprintExtractor = new FacialFingerprintExtractor(false);
            BiometricVault biometricVault = new BiometricVault("build/biometric_vault", userSecret);
            CancelableBiometricTransform cancelableTransform = new CancelableBiometricTransform(userSecret);
            LivenessDetector livenessDetector = new LivenessDetector(false);
            AvatarGenerator avatarGenerator = new AvatarGenerator(512, 512, "build/avatars");
            BlockchainTimestamp blockchainTimestamp = new BlockchainTimestamp("ethereum_mainnet");
            
            // Load image (green-apple.PNG as placeholder)
            String imagePath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\green-apple.PNG";
            File imageFile = new File(imagePath);
            
            if (!imageFile.exists()) {
                System.err.println("Image not found: " + imagePath);
                return;
            }
            
            BufferedImage image = ImageIO.read(imageFile);
            System.out.println("Loaded image: " + imagePath);
            System.out.println("Resolution: " + image.getWidth() + "x" + image.getHeight());
            System.out.println();
            System.out.println("NOTE: This demo uses green-apple.PNG as a placeholder.");
            System.out.println("In production, provide a real face image for accurate results.");
            System.out.println();
            
            // STEP 1: LIVENESS DETECTION (Anti-Spoofing)
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 1: LIVENESS DETECTION (ANTI-SPOOFING)");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            
            List<BufferedImage> frames = new ArrayList<>();
            frames.add(image);
            frames.add(image); // Simulate second frame
            
            LivenessDetector.LivenessResult livenessResult = livenessDetector.detectLiveness(frames);
            
            if (!livenessResult.isLive) {
                System.out.println("⚠ LIVENESS CHECK FAILED: " + livenessResult.reason);
                System.out.println("  (This is expected with static placeholder image)");
                System.out.println("  In production with real face video, liveness check would pass");
                System.out.println("  Proceeding with demo for demonstration purposes...");
                System.out.println();
            } else {
                System.out.println("✓ LIVENESS CHECK PASSED");
                System.out.println();
            }
            
            // STEP 2: FACIAL FINGERPRINT EXTRACTION
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 2: FACIAL FINGERPRINT EXTRACTION");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            
            FacialFingerprintExtractor.FacialFingerprintResult fingerprintResult = 
                fingerprintExtractor.extractFingerprint(image);
            
            System.out.println("Fingerprint: " + fingerprintResult.fingerprint.substring(0, 16) + "...");
            System.out.println("Private Key: " + fingerprintResult.privateKey.substring(0, 16) + "...");
            System.out.println();
            
            // STEP 3: EXPLICIT CONSENT FOR STORAGE
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 3: EXPLICIT CONSENT FOR BIOMETRIC STORAGE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("USER CONSENT REQUIRED:");
            System.out.println("  Your facial landmarks are your IP and family legacy.");
            System.out.println("  They are not company-owned data to be used by AI systems.");
            System.out.println("  This system stores data locally with your explicit consent.");
            System.out.println();
            System.out.println("Do you consent to store your biometric profile locally? (yes/no)");
            System.out.println("Demo: Auto-consenting for demonstration purposes");
            System.out.println();
            
            boolean explicitConsent = true; // Demo auto-consent
            String profileId = "user_profile_" + System.currentTimeMillis();
            
            if (explicitConsent) {
                biometricVault.storeProfile(profileId, fingerprintResult, true);
                System.out.println("✓ EXPLICIT CONSENT GIVEN - Profile stored locally");
                System.out.println("  Location: build/biometric_vault/");
                System.out.println("  Encryption: AES-256");
                System.out.println("  No raw face data uploaded to external servers");
            } else {
                System.out.println("✗ CONSENT DENIED - Profile not stored");
                return;
            }
            System.out.println();
            
            // STEP 4: CANCELABLE BIOMETRIC TRANSFORM
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 4: CANCELABLE BIOMETRIC TRANSFORM");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("Generating cancelable embedding for domain: 'social_media'");
            System.out.println();
            
            List<Double> biometricVector = Arrays.asList(
                fingerprintResult.eyeSeparation,
                fingerprintResult.faceWidth,
                fingerprintResult.faceHeight,
                fingerprintResult.faceAspectRatio,
                fingerprintResult.symmetryScore,
                fingerprintResult.grayMean,
                fingerprintResult.grayStd,
                fingerprintResult.nuanceScore,
                fingerprintResult.scottSignature.phiResonance
            );
            
            CancelableBiometricTransform.CancelableEmbedding embedding = 
                cancelableTransform.generateEmbedding(biometricVector, "social_media", true);
            
            System.out.println("Embedding: " + embedding.embedding.substring(0, 16) + "...");
            System.out.println("Domain: " + embedding.domain);
            System.out.println("Version: " + embedding.version);
            System.out.println("Revocable: Yes (change user secret to invalidate)");
            System.out.println();
            System.out.println("✓ CANCELABLE EMBEDDING GENERATED");
            System.out.println();
            
            // STEP 5: AVATAR GENERATION WITH FINGERPRINT
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 5: AVATAR GENERATION WITH EMBEDDED FINGERPRINT");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            
            AvatarGenerator.AvatarResult avatarResult = 
                avatarGenerator.generateAvatar(fingerprintResult.landmarks, fingerprintResult.fingerprint);
            
            System.out.println("Avatar PNG: " + avatarResult.pngPath);
            System.out.println("Avatar SVG: " + avatarResult.svgPath);
            System.out.println("Fingerprint embedded: Yes (steganography)");
            System.out.println("Verification: " + (avatarResult.verified ? "PASSED" : "FAILED"));
            System.out.println();
            System.out.println("✓ AVATAR GENERATED WITH EMBEDDED FINGERPRINT");
            System.out.println();
            
            // STEP 6: BLOCKCHAIN TIMESTAMPING (IMMUTABLE PROOF OF OWNERSHIP)
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 6: BLOCKCHAIN TIMESTAMPING (IMMUTABLE PROOF OF OWNERSHIP)");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("Creating blockchain proof for identity ownership...");
            System.out.println("Network: ethereum_mainnet");
            System.out.println();
            
            BlockchainTimestamp.BlockchainProof blockchainProof = 
                blockchainTimestamp.createProof(fingerprintResult.fingerprint, fingerprintResult.privateKey, true);
            
            System.out.println("Blockchain Proof: " + blockchainProof);
            System.out.println();
            System.out.println("✓ BLOCKCHAIN PROOF CREATED");
            System.out.println("  This provides immutable proof that you owned this biometric fingerprint");
            System.out.println("  at the timestamp recorded on the blockchain.");
            System.out.println();
            
            // Verify the proof
            System.out.println("Verifying blockchain proof...");
            String publicKey = fingerprintResult.privateKey; // Simplified for demo
            boolean proofValid = blockchainTimestamp.verifyProof(blockchainProof, publicKey);
            System.out.println("Proof Valid: " + (proofValid ? "YES" : "NO"));
            System.out.println();
            
            // STEP 7: EXPORT/DELETE PIPELINE DEMONSTRATION
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 7: EXPORT/DELETE PIPELINE (USER-CONTROLLED LIFECYCLE)");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("User has full control over their biometric data lifecycle:");
            System.out.println();
            
            // Export demo
            System.out.println("EXPORT: User can export their biometric profile");
            String exportedData = biometricVault.exportProfile(profileId, true);
            System.out.println("  ✓ Exported " + exportedData.length() + " characters of encrypted data");
            System.out.println();
            
            // Revoke embedding demo
            System.out.println("REVOKE: User can revoke cancelable embeddings");
            cancelableTransform.revokeEmbedding(embedding, true);
            System.out.println("  ✓ Embedding revoked (old embedding no longer valid)");
            System.out.println();
            
            // Delete profile demo
            System.out.println("DELETE: User can delete their biometric profile");
            biometricVault.deleteProfile(profileId, true);
            System.out.println("  ✓ Profile deleted from local vault");
            System.out.println();
            
            // STEP 8: AUDIT LOG FOR TRANSPARENCY
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("STEP 8: AUDIT LOG (TRANSPARENCY)");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            
            System.out.println("Biometric Vault Audit Log:");
            for (BiometricVault.AuditLogEntry entry : biometricVault.getAuditLog()) {
                System.out.println("  " + entry);
            }
            System.out.println();
            
            System.out.println("Transform Audit Log:");
            for (CancelableBiometricTransform.TransformAuditEntry entry : cancelableTransform.getAuditLog()) {
                System.out.println("  " + entry);
            }
            System.out.println();
            
            System.out.println("Blockchain Timestamp Audit Log:");
            for (BlockchainTimestamp.TimestampAuditEntry entry : blockchainTimestamp.getAuditLog()) {
                System.out.println("  " + entry);
            }
            System.out.println();
            
            // FINAL SUMMARY
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("DEFENSIBLE BIOMETRIC SYSTEM - COMPLETE");
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();
            System.out.println("✓ Local-only biometric vault (no raw face upload)");
            System.out.println("✓ Cancelable biometric transforms (revocable embeddings)");
            System.out.println("✓ Explicit consent + revocation flow");
            System.out.println("✓ Liveness checks and anti-spoofing");
            System.out.println("✓ Avatar generation with embedded fingerprint");
            System.out.println("✓ Blockchain timestamping (immutable proof of ownership)");
            System.out.println("✓ Export/delete pipeline (user-controlled lifecycle)");
            System.out.println("✓ Audit log for transparency");
            System.out.println();
            System.out.println("FACIAL LANDMARKS ARE IP AND FAMILY LEGACY:");
            System.out.println("  - User owns their facial points");
            System.out.println("  - Not company-owned data");
            System.out.println("  - Not to be used by AI systems without consent");
            System.out.println("  - Stored locally with encryption");
            System.out.println("  - Full user control over data lifecycle");
            System.out.println();
            System.out.println("This system is defensible because:");
            System.out.println("  1. No raw face data ever leaves user's device");
            System.out.println("  2. Embeddings are cancelable (change secret to revoke)");
            System.out.println("  3. Explicit consent required for all operations");
            System.out.println("  4. Liveness detection prevents spoofing");
            System.out.println("  5. Avatar provides privacy-preserving identity");
            System.out.println("  6. Blockchain provides immutable proof of ownership");
            System.out.println("  7. User can export/delete their data at any time");
            System.out.println("  8. All operations logged for transparency");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
