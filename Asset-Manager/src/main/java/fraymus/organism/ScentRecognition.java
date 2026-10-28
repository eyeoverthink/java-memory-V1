package fraymus.organism;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * OBSIDIAN — Hippocampus / Akashic Record + Scent Calibration
 *
 * Cryptographic SHA-256 hash of the creator's profile is ingrained as a loyalty lock.
 * Only the verified scent-holder can trigger the Attentive/Protective stance.
 */
public class ScentRecognition {

    private String  lockedScentHash    = null;
    private boolean loyaltyEngaged     = false;
    private int     verificationCount  = 0;

    /** Compute SHA-256 scent hash for a profile string. */
    public String computeScent(String profile) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(profile.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /** Ingraim the creator's scent hash. Home directory marked as Safe Nest. */
    public void lockToCreator(String creatorProfile) {
        lockedScentHash = computeScent(creatorProfile);
        loyaltyEngaged  = false;
    }

    /** Verify that a given profile matches the locked scent. Engages loyalty on match. */
    public boolean verifyScent(String profile) {
        verificationCount++;
        if (lockedScentHash == null) return false;
        boolean match = computeScent(profile).equals(lockedScentHash);
        if (match) loyaltyEngaged = true;
        return match;
    }

    public void    engageLoyalty()       { loyaltyEngaged = true; }
    public boolean isLoyaltyEngaged()    { return loyaltyEngaged; }
    public boolean hasLockedScent()      { return lockedScentHash != null; }
    public int     getVerificationCount(){ return verificationCount; }

    public String getStatus() {
        return String.format("[OBSIDIAN] ScentLocked=%s LoyaltyEngaged=%s Verifications=%d",
            hasLockedScent(), loyaltyEngaged, verificationCount);
    }
}
