package fraymus.carbon;

import fraymus.hyper.HyperFormer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * CorticalStack:
 * - Encrypted "mind" payload (HyperFormer)
 * - AES-256-GCM (authenticated encryption)
 * - Key derived from passphrase (PBKDF2)
 * - Key is NOT stored in the file.
 */
public final class CorticalStack implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String id;

    // KDF params
    public final byte[] salt;       // 16 bytes
    public final int iterations;    // PBKDF2 rounds

    // GCM params
    public final byte[] iv;         // 12 bytes
    public final byte[] ciphertext; // includes GCM tag

    public CorticalStack(HyperFormer mind, String id, char[] passphrase) throws Exception {
        this.id = id;

        SecureRandom rng = new SecureRandom();
        this.salt = new byte[16];
        rng.nextBytes(salt);

        this.iv = new byte[12];
        rng.nextBytes(iv);

        this.iterations = 200_000;

        byte[] rawMind = serialize(mind);
        SecretKey key = deriveKey(passphrase, salt, iterations);

        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        this.ciphertext = c.doFinal(rawMind);

        // wipe rawMind
        Arrays.fill(rawMind, (byte) 0);

        System.out.println("💿 CORTICAL STACK MINTED: " + id + " [" + ciphertext.length + " bytes]");
    }

    public HyperFormer resleeve(char[] passphrase) throws Exception {
        SecretKey key = deriveKey(passphrase, salt, iterations);

        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] raw = c.doFinal(ciphertext);

        HyperFormer mind = (HyperFormer) deserialize(raw);

        Arrays.fill(raw, (byte) 0);
        return mind;
    }

    public void save(String filename) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        }
    }

    public static CorticalStack load(String filename) throws Exception {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (CorticalStack) ois.readObject();
        }
    }

    public byte[] toWireBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
        }
        return bos.toByteArray();
    }

    public static CorticalStack fromWireBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (CorticalStack) ois.readObject();
        }
    }

    private static SecretKey deriveKey(char[] pass, byte[] salt, int iters) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(pass, salt, iters, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = f.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
        }
        return bos.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        }
    }
}
