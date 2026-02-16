package fraymus.carbon;

import fraymus.hyper.FraymusState;
import fraymus.hyper.HyperFormer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 💿 CORTICAL STACK - Production-Grade Encrypted Soul File
 * "DHF (Digital Human Freight) with military-grade security"
 * 
 * Security Features:
 * - AES-256-GCM (authenticated encryption, not ECB)
 * - PBKDF2-HMAC-SHA256 key derivation from passphrase
 * - Binary frame protocol (no Java deserialization from network)
 * - Immutable FraymusState snapshots (not live object graphs)
 * 
 * This is Digital Immortality through portability AND security.
 */
public final class CorticalStack implements Serializable {

    private static final long serialVersionUID = 2L;

    // Protocol constants
    private static final int VERSION = 1;
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;                 // GCM standard
    private static final int GCM_TAG_BITS = 128;
    private static final int PBKDF2_ITERS = 200_000;      // adjust for your hardware
    private static final int KEY_BITS = 256;

    public final String id;
    public final int version;
    public final byte[] salt;
    public final byte[] iv;
    public final byte[] encryptedMind; // AES-GCM ciphertext (includes auth tag)

    public CorticalStack(String id, byte[] salt, byte[] iv, byte[] encryptedMind) {
        this.id = id;
        this.version = VERSION;
        this.salt = salt;
        this.iv = iv;
        this.encryptedMind = encryptedMind;
    }

    /**
     * MINT: Create an encrypted stack from a HyperFormer mind snapshot.
     * Uses PBKDF2 to derive key from passphrase (key never stored).
     */
    public static CorticalStack mint(HyperFormer mind, String id, char[] passphrase) throws Exception {
        System.out.println("💿 MINTING CORTICAL STACK: " + id);

        FraymusState state = mind.exportState();
        byte[] raw = serialize(state);

        byte[] salt = new byte[SALT_LEN];
        byte[] iv = new byte[IV_LEN];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(salt);
        rng.nextBytes(iv);

        SecretKey key = deriveKey(passphrase, salt);
        byte[] aad = aadBytes(id);

        byte[] ct = encryptAesGcm(key, iv, aad, raw);

        // Wipe raw state bytes in memory
        Arrays.fill(raw, (byte) 0);

        System.out.println("✅ STACK MINTED: " + id + " [" + ct.length + " bytes]");
        return new CorticalStack(id, salt, iv, ct);
    }

    /**
     * RESLEEVE: Decrypt and reconstruct the mind into a new HyperFormer instance.
     * Requires the original passphrase.
     */
    public HyperFormer resleeve(char[] passphrase) throws Exception {
        SecretKey key = deriveKey(passphrase, salt);
        byte[] aad = aadBytes(id);

        byte[] raw = decryptAesGcm(key, iv, aad, encryptedMind);

        FraymusState state = (FraymusState) deserialize(raw);
        Arrays.fill(raw, (byte) 0);

        return HyperFormer.fromState(state);
    }

    // ---- Persistence (binary frame, NO Java object deserialization from untrusted sources) ----

    /**
     * Write stack to disk as a binary frame.
     */
    public void saveToFile(String filename) throws IOException {
        byte[] bytes = toBytes();
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(bytes);
        }
    }

    /**
     * Load stack from disk (binary frame).
     */
    public static CorticalStack loadFromFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fromBytes(fis.readAllBytes());
        }
    }

    /**
     * Convert to a transport-safe binary frame.
     */
    public byte[] toBytes() {
        byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);

        // Frame: [magic 4][ver 4][idLen 4][id][saltLen 4][salt][ivLen 4][iv][ctLen 4][ct]
        ByteBuffer buf = ByteBuffer.allocate(
                4 + 4 + 4 + idBytes.length +
                4 + salt.length +
                4 + iv.length +
                4 + encryptedMind.length
        );

        buf.putInt(0x4353544B); // "CSTK"
        buf.putInt(version);

        buf.putInt(idBytes.length);
        buf.put(idBytes);

        buf.putInt(salt.length);
        buf.put(salt);

        buf.putInt(iv.length);
        buf.put(iv);

        buf.putInt(encryptedMind.length);
        buf.put(encryptedMind);

        return buf.array();
    }

    /**
     * Parse from binary frame.
     */
    public static CorticalStack fromBytes(byte[] bytes) throws IOException {
        try {
            ByteBuffer buf = ByteBuffer.wrap(bytes);

            int magic = buf.getInt();
            if (magic != 0x4353544B) throw new IOException("Bad magic. Not a CorticalStack.");

            int ver = buf.getInt();
            if (ver != VERSION) throw new IOException("Unsupported version: " + ver);

            int idLen = buf.getInt();
            byte[] idBytes = new byte[idLen];
            buf.get(idBytes);
            String id = new String(idBytes, StandardCharsets.UTF_8);

            int saltLen = buf.getInt();
            byte[] salt = new byte[saltLen];
            buf.get(salt);

            int ivLen = buf.getInt();
            byte[] iv = new byte[ivLen];
            buf.get(iv);

            int ctLen = buf.getInt();
            byte[] ct = new byte[ctLen];
            buf.get(ct);

            return new CorticalStack(id, salt, iv, ct);
        } catch (RuntimeException e) {
            throw new IOException("Corrupt stack frame: " + e.getMessage(), e);
        }
    }

    // ---- Crypto helpers ----

    private static SecretKey deriveKey(char[] passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase, salt, PBKDF2_ITERS, KEY_BITS);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] raw = f.generateSecret(spec).getEncoded();
        spec.clearPassword();
        return new SecretKeySpec(raw, "AES");
    }

    private static byte[] encryptAesGcm(SecretKey key, byte[] iv, byte[] aad, byte[] plaintext) throws Exception {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        c.updateAAD(aad);
        return c.doFinal(plaintext);
    }

    private static byte[] decryptAesGcm(SecretKey key, byte[] iv, byte[] aad, byte[] ciphertext) throws Exception {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        c.updateAAD(aad);
        return c.doFinal(ciphertext); // throws if tampered/wrong key
    }

    private static byte[] aadBytes(String id) {
        // AAD binds identity to ciphertext (prevents swapping stacks without detection)
        return ("CORTICAL-STACK|" + id + "|v" + VERSION).getBytes(StandardCharsets.UTF_8);
    }

    // ---- Serialization helpers (inside encrypted envelope) ----

    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
}
