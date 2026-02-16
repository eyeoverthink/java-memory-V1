package fraymus.carbon;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Needlecast:
 * - sends stack as bytes (length-prefixed)
 * - no Java serialization gadget risk on the wire
 */
public final class Needlecast {

    public static void beam(CorticalStack stack, String targetIp, int port) {
        System.out.println("📡 NEEDLECAST -> " + targetIp + ":" + port);

        try (Socket socket = new Socket(targetIp, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            byte[] payload = stack.toWireBytes();
            out.writeInt(payload.length);
            out.write(payload);
            out.flush();

            System.out.println("✅ TRANSFER COMPLETE. MIND IS OFF-WORLD.");

        } catch (Exception e) {
            System.err.println("❌ NEEDLECAST FAILED: " + e.getMessage());
        }
    }
}
