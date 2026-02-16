package fraymus.core;

import fraymus.quantum.security.SovereignCrypto;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 🏰 FRAYMUS CORE
 * The Standard Library of the Sovereign.
 */
public class FraymusCore {

    public static final String VERSION = "SOVEREIGN-1.0";

    // IO ABSTRACTION
    public static String readFile(String path) {
        try { return Files.readString(Path.of(path)); } 
        catch (Exception e) { return null; }
    }

    public static boolean writeFile(String path, String content) {
        try { 
            Files.writeString(Path.of(path), content); 
            return true;
        } catch (Exception e) { return false; }
    }

    // NETWORK ABSTRACTION
    public static String netRequest(String url, Object payload) {
        String json = FraymusJSON.stringify(payload);
        return FraymusHTTP.post(url, json);
    }

    // IDENTITY
    public static void assertSovereignty() {
        System.out.println("⚡ FRAYMUS CORE ONLINE.");
        System.out.println("   [VERSION] " + VERSION);
        System.out.println("   [DEPS] 0 External Libraries");
        System.out.println("   [JSON] Internal Parser Active");
        System.out.println("   [NET]  Internal HTTP Active");
        System.out.println("   [CRYPTO] Sovereign Crypto Active (Pure BigInteger)");
    }

    // SOVEREIGN IDENTITY PROTOCOL
    public static void assertIdentity(String username, String password) {
        System.out.println("⚡ INITIALIZING SOVEREIGN IDENTITY PROTOCOL...");

        // 1. GENERATE IDENTITY (Blue Team)
        SovereignCrypto.KeyPair identity = new SovereignCrypto.KeyPair(username + password);
        System.out.println("   [ID] Public Lock: " + identity.publicKey);

        // 2. STRESS TEST (Red Team)
        // Fraymus attempts to hack itself to ensure the password is strong enough
        System.out.println("   [SECURITY] Red Team attacking lock...");
        long start = System.currentTimeMillis();
        BigInteger weakness = SovereignCrypto.breakLock(identity.publicKey);
        long end = System.currentTimeMillis();

        if (weakness != null && !weakness.equals(BigInteger.ONE)) {
            System.out.println("   ⚠️ WEAKNESS DETECTED: Lock factored in " + (end - start) + "ms.");
            System.out.println("      Factor found: " + weakness);
            System.out.println("      Password complexity insufficient for Sovereign Status.");
        } else {
            System.out.println("   ✅ LOCK SECURE. Red Team failed to factor in " + (end - start) + "ms.");
            System.out.println("      Identity confirmed as Sovereign.");
        }

        // 3. SIGN (Purple Team)
        String sig = SovereignCrypto.sign("GENESIS_HANDSHAKE", identity);
        System.out.println("   [SIGNATURE] " + sig.substring(0, 32) + "...");
    }
}
