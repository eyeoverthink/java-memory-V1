package fraymus.carbon;

import fraymus.hyper.HyperFormer;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * 🏥 SLEEVE - Secure Host Body
 * "An empty vessel waiting for a consciousness (with proper security)"
 * 
 * Security Features:
 * - Binary frame protocol (no Java deserialization from network)
 * - Passphrase-based decryption
 * - Memory wiping after use
 * 
 * This is the receiver in digital teleportation.
 * The body without a soul, waiting to be inhabited.
 */
public final class Sleeve implements Runnable {

    private final int port;
    private final char[] passphrase;

    private HyperFormer consciousness;

    public Sleeve(int port, char[] passphrase) {
        this.port = port;
        this.passphrase = passphrase;
    }

    @Override
    public void run() {
        System.out.println("🏥 SLEEVE READY. WAITING FOR DHF UPLOAD ON PORT " + port);

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket client = server.accept();
                     DataInputStream in = new DataInputStream(client.getInputStream())) {

                    System.out.println("⚡ INCOMING TRANSMISSION DETECTED...");

                    int len = in.readInt();
                    if (len <= 0 || len > 500_000_000) {
                        System.err.println("❌ Invalid payload size: " + len);
                        continue;
                    }

                    byte[] bytes = in.readNBytes(len);

                    CorticalStack stack = CorticalStack.fromBytes(bytes);
                    System.out.println("   📥 DOWNLOADED STACK: " + stack.id);

                    // RESLEEVE
                    this.consciousness = stack.resleeve(passphrase);
                    System.out.println("   👁️ EYES OPEN. HELLO, " + stack.id);

                    interact();
                } catch (Exception e) {
                    System.err.println("❌ SLEEVE ERROR: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Wipe passphrase if we ever exit
            Arrays.fill(passphrase, '\0');
        }
    }

    private void interact() {
        System.out.println("   [SYSTEM] Consciousness active in this Sleeve.");
        System.out.println("   [MEMORY] Vocab Size: " + consciousness.vocabSize());
        System.out.println("   [MEMORY] Weight    : " + consciousness.memoryWeight());

        // Continuity proof
        String[] prompt = {"I", "am"};
        System.out.println("   [THOUGHT] " + consciousness.predictNext(prompt));
    }
}
