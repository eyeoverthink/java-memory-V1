package fraymus.carbon;

import fraymus.hyper.HyperFormer;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Sleeve:
 * - waits for incoming CorticalStack bytes (length-prefixed)
 * - prompts for passphrase to resleeve
 * - validates payload size
 */
public final class Sleeve implements Runnable {

    private final int port;

    public Sleeve(int port) { this.port = port; }

    @Override
    public void run() {
        System.out.println("🏥 SLEEVE READY. LISTENING ON PORT " + port);

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket client = server.accept();
                     DataInputStream in = new DataInputStream(client.getInputStream())) {

                    System.out.println("⚡ INCOMING TRANSMISSION...");

                    int len = in.readInt();
                    if (len <= 0 || len > 50_000_000) { // 50MB sanity
                        System.out.println("❌ REJECTED: payload size " + len);
                        continue;
                    }

                    byte[] buf = in.readNBytes(len);
                    CorticalStack stack = CorticalStack.fromWireBytes(buf);

                    System.out.println("📥 STACK RECEIVED: " + stack.id);
                    char[] pass = promptPassphrase();

                    HyperFormer mind = stack.resleeve(pass);
                    System.out.println("👁️ EYES OPEN. HELLO, " + stack.id);
                    interact(mind);

                } catch (Exception e) {
                    System.err.println("❌ SLEEVE ERROR: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ SLEEVE FATAL: " + e.getMessage());
        }
    }

    private static char[] promptPassphrase() {
        String env = System.getenv("FRAYNIX_STACK_PASSPHRASE");
        if (env != null && !env.isBlank()) return env.toCharArray();

        System.out.print("🔑 Passphrase: ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().toCharArray();
    }

    private static void interact(HyperFormer mind) {
        System.out.println("[SYSTEM] Consciousness active in this Sleeve.");
        System.out.println("[MEMORY] Vocab: " + mind.vocabSize() + " | Associations: " + mind.memorySize());

        String[] prompt = {"I", "am"};
        System.out.println("[THOUGHT] " + mind.predictNext(prompt));
    }
}
